package net.minecraft.client.network;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ThreadLanServerPing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LanServerDetector
{
    private static final AtomicInteger field_148551_a = new AtomicInteger(0);
    private static final Logger logger = LogManager.getLogger();

    public static class LanServer
    {
        private String lanServerMotd;
        private String lanServerIpPort;
        private long timeLastSeen;

        public LanServer(String motd, String address)
        {
            this.lanServerMotd = motd;
            this.lanServerIpPort = address;
            this.timeLastSeen = Minecraft.getSystemTime();
        }

        public String getServerMotd()
        {
            return this.lanServerMotd;
        }

        public String getServerIpPort()
        {
            return this.lanServerIpPort;
        }

        public void updateLastSeen()
        {
            this.timeLastSeen = Minecraft.getSystemTime();
        }
    }

    public static class LanServerList
    {
        private List<LanServerDetector.LanServer> listOfLanServers = Lists.newArrayList();
        boolean wasUpdated;

        public synchronized boolean getWasUpdated()
        {
            return this.wasUpdated;
        }

        public synchronized void setWasNotUpdated()
        {
            this.wasUpdated = false;
        }

        public synchronized List<LanServerDetector.LanServer> getLanServers()
        {
            return Collections.unmodifiableList(this.listOfLanServers);
        }

        public synchronized void func_77551_a(String p_77551_1_, InetAddress p_77551_2_)
        {
            String lvt_3_1_ = ThreadLanServerPing.getMotdFromPingResponse(p_77551_1_);
            String lvt_4_1_ = ThreadLanServerPing.getAdFromPingResponse(p_77551_1_);

            if (lvt_4_1_ != null)
            {
                lvt_4_1_ = p_77551_2_.getHostAddress() + ":" + lvt_4_1_;
                boolean lvt_5_1_ = false;

                for (LanServerDetector.LanServer lvt_7_1_ : this.listOfLanServers)
                {
                    if (lvt_7_1_.getServerIpPort().equals(lvt_4_1_))
                    {
                        lvt_7_1_.updateLastSeen();
                        lvt_5_1_ = true;
                        break;
                    }
                }

                if (!lvt_5_1_)
                {
                    this.listOfLanServers.add(new LanServerDetector.LanServer(lvt_3_1_, lvt_4_1_));
                    this.wasUpdated = true;
                }
            }
        }
    }

    public static class ThreadLanServerFind extends Thread
    {
        private final LanServerDetector.LanServerList localServerList;
        private final InetAddress broadcastAddress;
        private final MulticastSocket socket;

        public ThreadLanServerFind(LanServerDetector.LanServerList p_i1320_1_) throws IOException
        {
            super("LanServerDetector #" + LanServerDetector.field_148551_a.incrementAndGet());
            this.localServerList = p_i1320_1_;
            this.setDaemon(true);
            this.socket = new MulticastSocket(4445);
            this.broadcastAddress = InetAddress.getByName("224.0.2.60");
            this.socket.setSoTimeout(5000);
            this.socket.joinGroup(this.broadcastAddress);
        }

        public void run()
        {
            byte[] lvt_2_1_ = new byte[1024];

            while (!this.isInterrupted())
            {
                DatagramPacket lvt_1_1_ = new DatagramPacket(lvt_2_1_, lvt_2_1_.length);

                try
                {
                    this.socket.receive(lvt_1_1_);
                }
                catch (SocketTimeoutException var5)
                {
                    continue;
                }
                catch (IOException var6)
                {
                    LanServerDetector.logger.error("Couldn\'t ping server", var6);
                    break;
                }

                String lvt_3_3_ = new String(lvt_1_1_.getData(), lvt_1_1_.getOffset(), lvt_1_1_.getLength());
                LanServerDetector.logger.debug(lvt_1_1_.getAddress() + ": " + lvt_3_3_);
                this.localServerList.func_77551_a(lvt_3_3_, lvt_1_1_.getAddress());
            }

            try
            {
                this.socket.leaveGroup(this.broadcastAddress);
            }
            catch (IOException var4)
            {
                ;
            }

            this.socket.close();
        }
    }
}
