package net.minecraft.client.network;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OldServerPinger
{
    private static final Splitter PING_RESPONSE_SPLITTER = Splitter.on('\u0000').limit(6);
    private static final Logger logger = LogManager.getLogger();
    private final List<NetworkManager> pingDestinations = Collections.synchronizedList(Lists.newArrayList());

    public void ping(final ServerData server) throws UnknownHostException
    {
        ServerAddress lvt_2_1_ = ServerAddress.fromString(server.serverIP);
        final NetworkManager lvt_3_1_ = NetworkManager.createNetworkManagerAndConnect(InetAddress.getByName(lvt_2_1_.getIP()), lvt_2_1_.getPort(), false);
        this.pingDestinations.add(lvt_3_1_);
        server.serverMOTD = "Pinging...";
        server.pingToServer = -1L;
        server.playerList = null;
        lvt_3_1_.setNetHandler(new INetHandlerStatusClient()
        {
            private boolean field_147403_d = false;
            private boolean field_183009_e = false;
            private long field_175092_e = 0L;
            public void handleServerInfo(S00PacketServerInfo packetIn)
            {
                if (this.field_183009_e)
                {
                    lvt_3_1_.closeChannel(new ChatComponentText("Received unrequested status"));
                }
                else
                {
                    this.field_183009_e = true;
                    ServerStatusResponse lvt_2_1_ = packetIn.getResponse();

                    if (lvt_2_1_.getServerDescription() != null)
                    {
                        server.serverMOTD = lvt_2_1_.getServerDescription().getFormattedText();
                    }
                    else
                    {
                        server.serverMOTD = "";
                    }

                    if (lvt_2_1_.getProtocolVersionInfo() != null)
                    {
                        server.gameVersion = lvt_2_1_.getProtocolVersionInfo().getName();
                        server.version = lvt_2_1_.getProtocolVersionInfo().getProtocol();
                    }
                    else
                    {
                        server.gameVersion = "Old";
                        server.version = 0;
                    }

                    if (lvt_2_1_.getPlayerCountData() != null)
                    {
                        server.populationInfo = EnumChatFormatting.GRAY + "" + lvt_2_1_.getPlayerCountData().getOnlinePlayerCount() + "" + EnumChatFormatting.DARK_GRAY + "/" + EnumChatFormatting.GRAY + lvt_2_1_.getPlayerCountData().getMaxPlayers();

                        if (ArrayUtils.isNotEmpty(lvt_2_1_.getPlayerCountData().getPlayers()))
                        {
                            StringBuilder lvt_3_1_ = new StringBuilder();

                            for (GameProfile lvt_7_1_ : lvt_2_1_.getPlayerCountData().getPlayers())
                            {
                                if (lvt_3_1_.length() > 0)
                                {
                                    lvt_3_1_.append("\n");
                                }

                                lvt_3_1_.append(lvt_7_1_.getName());
                            }

                            if (lvt_2_1_.getPlayerCountData().getPlayers().length < lvt_2_1_.getPlayerCountData().getOnlinePlayerCount())
                            {
                                if (lvt_3_1_.length() > 0)
                                {
                                    lvt_3_1_.append("\n");
                                }

                                lvt_3_1_.append("... and ").append(lvt_2_1_.getPlayerCountData().getOnlinePlayerCount() - lvt_2_1_.getPlayerCountData().getPlayers().length).append(" more ...");
                            }

                            server.playerList = lvt_3_1_.toString();
                        }
                    }
                    else
                    {
                        server.populationInfo = EnumChatFormatting.DARK_GRAY + "???";
                    }

                    if (lvt_2_1_.getFavicon() != null)
                    {
                        String lvt_3_2_ = lvt_2_1_.getFavicon();

                        if (lvt_3_2_.startsWith("data:image/png;base64,"))
                        {
                            server.setBase64EncodedIconData(lvt_3_2_.substring("data:image/png;base64,".length()));
                        }
                        else
                        {
                            OldServerPinger.logger.error("Invalid server icon (unknown format)");
                        }
                    }
                    else
                    {
                        server.setBase64EncodedIconData((String)null);
                    }

                    this.field_175092_e = Minecraft.getSystemTime();
                    lvt_3_1_.sendPacket(new C01PacketPing(this.field_175092_e));
                    this.field_147403_d = true;
                }
            }
            public void handlePong(S01PacketPong packetIn)
            {
                long lvt_2_1_ = this.field_175092_e;
                long lvt_4_1_ = Minecraft.getSystemTime();
                server.pingToServer = lvt_4_1_ - lvt_2_1_;
                lvt_3_1_.closeChannel(new ChatComponentText("Finished"));
            }
            public void onDisconnect(IChatComponent reason)
            {
                if (!this.field_147403_d)
                {
                    OldServerPinger.logger.error("Can\'t ping " + server.serverIP + ": " + reason.getUnformattedText());
                    server.serverMOTD = EnumChatFormatting.DARK_RED + "Can\'t connect to server.";
                    server.populationInfo = "";
                    OldServerPinger.this.tryCompatibilityPing(server);
                }
            }
        });

        try
        {
            lvt_3_1_.sendPacket(new C00Handshake(47, lvt_2_1_.getIP(), lvt_2_1_.getPort(), EnumConnectionState.STATUS));
            lvt_3_1_.sendPacket(new C00PacketServerQuery());
        }
        catch (Throwable var5)
        {
            logger.error(var5);
        }
    }

    private void tryCompatibilityPing(final ServerData server)
    {
        final ServerAddress lvt_2_1_ = ServerAddress.fromString(server.serverIP);
        ((Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).group((EventLoopGroup)NetworkManager.CLIENT_NIO_EVENTLOOP.getValue())).handler(new ChannelInitializer<Channel>()
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

                p_initChannel_1_.pipeline().addLast(new ChannelHandler[] {new SimpleChannelInboundHandler<ByteBuf>()
                    {
                        public void channelActive(ChannelHandlerContext p_channelActive_1_) throws Exception
                        {
                            super.channelActive(p_channelActive_1_);
                            ByteBuf lvt_2_1_ = Unpooled.buffer();

                            try
                            {
                                lvt_2_1_.writeByte(254);
                                lvt_2_1_.writeByte(1);
                                lvt_2_1_.writeByte(250);
                                char[] lvt_3_1_ = "MC|PingHost".toCharArray();
                                lvt_2_1_.writeShort(lvt_3_1_.length);

                                for (char lvt_7_1_ : lvt_3_1_)
                                {
                                    lvt_2_1_.writeChar(lvt_7_1_);
                                }

                                lvt_2_1_.writeShort(7 + 2 * lvt_2_1_.getIP().length());
                                lvt_2_1_.writeByte(127);
                                lvt_3_1_ = lvt_2_1_.getIP().toCharArray();
                                lvt_2_1_.writeShort(lvt_3_1_.length);

                                for (char lvt_7_2_ : lvt_3_1_)
                                {
                                    lvt_2_1_.writeChar(lvt_7_2_);
                                }

                                lvt_2_1_.writeInt(lvt_2_1_.getPort());
                                p_channelActive_1_.channel().writeAndFlush(lvt_2_1_).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                            }
                            finally
                            {
                                lvt_2_1_.release();
                            }
                        }
                        protected void channelRead0(ChannelHandlerContext p_channelRead0_1_, ByteBuf p_channelRead0_2_) throws Exception
                        {
                            short lvt_3_1_ = p_channelRead0_2_.readUnsignedByte();

                            if (lvt_3_1_ == 255)
                            {
                                String lvt_4_1_ = new String(p_channelRead0_2_.readBytes(p_channelRead0_2_.readShort() * 2).array(), Charsets.UTF_16BE);
                                String[] lvt_5_1_ = (String[])Iterables.toArray(OldServerPinger.PING_RESPONSE_SPLITTER.split(lvt_4_1_), String.class);

                                if ("\u00a71".equals(lvt_5_1_[0]))
                                {
                                    int lvt_6_1_ = MathHelper.parseIntWithDefault(lvt_5_1_[1], 0);
                                    String lvt_7_1_ = lvt_5_1_[2];
                                    String lvt_8_1_ = lvt_5_1_[3];
                                    int lvt_9_1_ = MathHelper.parseIntWithDefault(lvt_5_1_[4], -1);
                                    int lvt_10_1_ = MathHelper.parseIntWithDefault(lvt_5_1_[5], -1);
                                    server.version = -1;
                                    server.gameVersion = lvt_7_1_;
                                    server.serverMOTD = lvt_8_1_;
                                    server.populationInfo = EnumChatFormatting.GRAY + "" + lvt_9_1_ + "" + EnumChatFormatting.DARK_GRAY + "/" + EnumChatFormatting.GRAY + lvt_10_1_;
                                }
                            }

                            p_channelRead0_1_.close();
                        }
                        public void exceptionCaught(ChannelHandlerContext p_exceptionCaught_1_, Throwable p_exceptionCaught_2_) throws Exception
                        {
                            p_exceptionCaught_1_.close();
                        }
                        protected void channelRead0(ChannelHandlerContext p_channelRead0_1_, Object p_channelRead0_2_) throws Exception
                        {
                            this.channelRead0(p_channelRead0_1_, (ByteBuf)p_channelRead0_2_);
                        }
                    }
                });
            }
        })).channel(NioSocketChannel.class)).connect(lvt_2_1_.getIP(), lvt_2_1_.getPort());
    }

    public void pingPendingNetworks()
    {
        synchronized (this.pingDestinations)
        {
            Iterator<NetworkManager> lvt_2_1_ = this.pingDestinations.iterator();

            while (lvt_2_1_.hasNext())
            {
                NetworkManager lvt_3_1_ = (NetworkManager)lvt_2_1_.next();

                if (lvt_3_1_.isChannelOpen())
                {
                    lvt_3_1_.processReceivedPackets();
                }
                else
                {
                    lvt_2_1_.remove();
                    lvt_3_1_.checkDisconnected();
                }
            }
        }
    }

    public void clearPendingNetworks()
    {
        synchronized (this.pingDestinations)
        {
            Iterator<NetworkManager> lvt_2_1_ = this.pingDestinations.iterator();

            while (lvt_2_1_.hasNext())
            {
                NetworkManager lvt_3_1_ = (NetworkManager)lvt_2_1_.next();

                if (lvt_3_1_.isChannelOpen())
                {
                    lvt_2_1_.remove();
                    lvt_3_1_.closeChannel(new ChatComponentText("Cancelled"));
                }
            }
        }
    }
}
