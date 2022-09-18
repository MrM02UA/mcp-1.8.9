package net.minecraft.network;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.client.network.NetHandlerHandshakeMemory;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.NetHandlerHandshakeTCP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.LazyLoadBase;
import net.minecraft.util.MessageDeserializer;
import net.minecraft.util.MessageDeserializer2;
import net.minecraft.util.MessageSerializer;
import net.minecraft.util.MessageSerializer2;
import net.minecraft.util.ReportedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkSystem
{
    private static final Logger logger = LogManager.getLogger();
    public static final LazyLoadBase<NioEventLoopGroup> eventLoops = new LazyLoadBase<NioEventLoopGroup>()
    {
        protected NioEventLoopGroup load()
        {
            return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Server IO #%d").setDaemon(true).build());
        }
        protected Object load()
        {
            return this.load();
        }
    };
    public static final LazyLoadBase<EpollEventLoopGroup> SERVER_EPOLL_EVENTLOOP = new LazyLoadBase<EpollEventLoopGroup>()
    {
        protected EpollEventLoopGroup load()
        {
            return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build());
        }
        protected Object load()
        {
            return this.load();
        }
    };
    public static final LazyLoadBase<LocalEventLoopGroup> SERVER_LOCAL_EVENTLOOP = new LazyLoadBase<LocalEventLoopGroup>()
    {
        protected LocalEventLoopGroup load()
        {
            return new LocalEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Local Server IO #%d").setDaemon(true).build());
        }
        protected Object load()
        {
            return this.load();
        }
    };

    /** Reference to the MinecraftServer object. */
    private final MinecraftServer mcServer;

    /** True if this NetworkSystem has never had his endpoints terminated */
    public volatile boolean isAlive;
    private final List<ChannelFuture> endpoints = Collections.synchronizedList(Lists.newArrayList());
    private final List<NetworkManager> networkManagers = Collections.synchronizedList(Lists.newArrayList());

    public NetworkSystem(MinecraftServer server)
    {
        this.mcServer = server;
        this.isAlive = true;
    }

    /**
     * Adds a channel that listens on publicly accessible network ports
     */
    public void addLanEndpoint(InetAddress address, int port) throws IOException
    {
        synchronized (this.endpoints)
        {
            Class <? extends ServerSocketChannel > lvt_4_1_;
            LazyLoadBase <? extends EventLoopGroup > lvt_5_1_;

            if (Epoll.isAvailable() && this.mcServer.shouldUseNativeTransport())
            {
                lvt_4_1_ = EpollServerSocketChannel.class;
                lvt_5_1_ = SERVER_EPOLL_EVENTLOOP;
                logger.info("Using epoll channel type");
            }
            else
            {
                lvt_4_1_ = NioServerSocketChannel.class;
                lvt_5_1_ = eventLoops;
                logger.info("Using default channel type");
            }

            this.endpoints.add(((ServerBootstrap)((ServerBootstrap)(new ServerBootstrap()).channel(lvt_4_1_)).childHandler(new ChannelInitializer<Channel>()
            {
                protected void initChannel(Channel p_initChannel_1_) throws Exception
                {
                    try
                    {
                        p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, Boolean.valueOf(true));
                    }
                    catch (ChannelException var3)
                    {
                        ;
                    }

                    p_initChannel_1_.pipeline().addLast("timeout", new ReadTimeoutHandler(30)).addLast("legacy_query", new PingResponseHandler(NetworkSystem.this)).addLast("splitter", new MessageDeserializer2()).addLast("decoder", new MessageDeserializer(EnumPacketDirection.SERVERBOUND)).addLast("prepender", new MessageSerializer2()).addLast("encoder", new MessageSerializer(EnumPacketDirection.CLIENTBOUND));
                    NetworkManager lvt_2_1_ = new NetworkManager(EnumPacketDirection.SERVERBOUND);
                    NetworkSystem.this.networkManagers.add(lvt_2_1_);
                    p_initChannel_1_.pipeline().addLast("packet_handler", lvt_2_1_);
                    lvt_2_1_.setNetHandler(new NetHandlerHandshakeTCP(NetworkSystem.this.mcServer, lvt_2_1_));
                }
            }).group((EventLoopGroup)lvt_5_1_.getValue()).localAddress(address, port)).bind().syncUninterruptibly());
        }
    }

    /**
     * Adds a channel that listens locally
     */
    public SocketAddress addLocalEndpoint()
    {
        ChannelFuture lvt_1_1_;

        synchronized (this.endpoints)
        {
            lvt_1_1_ = ((ServerBootstrap)((ServerBootstrap)(new ServerBootstrap()).channel(LocalServerChannel.class)).childHandler(new ChannelInitializer<Channel>()
            {
                protected void initChannel(Channel p_initChannel_1_) throws Exception
                {
                    NetworkManager lvt_2_1_ = new NetworkManager(EnumPacketDirection.SERVERBOUND);
                    lvt_2_1_.setNetHandler(new NetHandlerHandshakeMemory(NetworkSystem.this.mcServer, lvt_2_1_));
                    NetworkSystem.this.networkManagers.add(lvt_2_1_);
                    p_initChannel_1_.pipeline().addLast("packet_handler", lvt_2_1_);
                }
            }).group((EventLoopGroup)eventLoops.getValue()).localAddress(LocalAddress.ANY)).bind().syncUninterruptibly();
            this.endpoints.add(lvt_1_1_);
        }

        return lvt_1_1_.channel().localAddress();
    }

    /**
     * Shuts down all open endpoints (with immediate effect?)
     */
    public void terminateEndpoints()
    {
        this.isAlive = false;

        for (ChannelFuture lvt_2_1_ : this.endpoints)
        {
            try
            {
                lvt_2_1_.channel().close().sync();
            }
            catch (InterruptedException var4)
            {
                logger.error("Interrupted whilst closing channel");
            }
        }
    }

    /**
     * Will try to process the packets received by each NetworkManager, gracefully manage processing failures and cleans
     * up dead connections
     */
    public void networkTick()
    {
        synchronized (this.networkManagers)
        {
            Iterator<NetworkManager> lvt_2_1_ = this.networkManagers.iterator();

            while (lvt_2_1_.hasNext())
            {
                final NetworkManager lvt_3_1_ = (NetworkManager)lvt_2_1_.next();

                if (!lvt_3_1_.hasNoChannel())
                {
                    if (!lvt_3_1_.isChannelOpen())
                    {
                        lvt_2_1_.remove();
                        lvt_3_1_.checkDisconnected();
                    }
                    else
                    {
                        try
                        {
                            lvt_3_1_.processReceivedPackets();
                        }
                        catch (Exception var8)
                        {
                            if (lvt_3_1_.isLocalChannel())
                            {
                                CrashReport lvt_5_1_ = CrashReport.makeCrashReport(var8, "Ticking memory connection");
                                CrashReportCategory lvt_6_1_ = lvt_5_1_.makeCategory("Ticking connection");
                                lvt_6_1_.addCrashSectionCallable("Connection", new Callable<String>()
                                {
                                    public String call() throws Exception
                                    {
                                        return lvt_3_1_.toString();
                                    }
                                    public Object call() throws Exception
                                    {
                                        return this.call();
                                    }
                                });
                                throw new ReportedException(lvt_5_1_);
                            }

                            logger.warn("Failed to handle packet for " + lvt_3_1_.getRemoteAddress(), var8);
                            final ChatComponentText lvt_5_2_ = new ChatComponentText("Internal server error");
                            lvt_3_1_.sendPacket(new S40PacketDisconnect(lvt_5_2_), new GenericFutureListener < Future <? super Void >> ()
                            {
                                public void operationComplete(Future <? super Void > p_operationComplete_1_) throws Exception
                                {
                                    lvt_3_1_.closeChannel(lvt_5_2_);
                                }
                            }, new GenericFutureListener[0]);
                            lvt_3_1_.disableAutoRead();
                        }
                    }
                }
            }
        }
    }

    public MinecraftServer getServer()
    {
        return this.mcServer;
    }
}
