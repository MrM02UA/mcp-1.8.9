package net.minecraft.realms;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsServerStatusPinger
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<NetworkManager> connections = Collections.synchronizedList(Lists.newArrayList());

    public void pingServer(final String p_pingServer_1_, final RealmsServerPing p_pingServer_2_) throws UnknownHostException
    {
        if (p_pingServer_1_ != null && !p_pingServer_1_.startsWith("0.0.0.0") && !p_pingServer_1_.isEmpty())
        {
            RealmsServerAddress lvt_3_1_ = RealmsServerAddress.parseString(p_pingServer_1_);
            final NetworkManager lvt_4_1_ = NetworkManager.createNetworkManagerAndConnect(InetAddress.getByName(lvt_3_1_.getHost()), lvt_3_1_.getPort(), false);
            this.connections.add(lvt_4_1_);
            lvt_4_1_.setNetHandler(new INetHandlerStatusClient()
            {
                private boolean field_154345_e = false;
                public void handleServerInfo(S00PacketServerInfo packetIn)
                {
                    ServerStatusResponse lvt_2_1_ = packetIn.getResponse();

                    if (lvt_2_1_.getPlayerCountData() != null)
                    {
                        p_pingServer_2_.nrOfPlayers = String.valueOf(lvt_2_1_.getPlayerCountData().getOnlinePlayerCount());

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

                            p_pingServer_2_.playerList = lvt_3_1_.toString();
                        }
                    }
                    else
                    {
                        p_pingServer_2_.playerList = "";
                    }

                    lvt_4_1_.sendPacket(new C01PacketPing(Realms.currentTimeMillis()));
                    this.field_154345_e = true;
                }
                public void handlePong(S01PacketPong packetIn)
                {
                    lvt_4_1_.closeChannel(new ChatComponentText("Finished"));
                }
                public void onDisconnect(IChatComponent reason)
                {
                    if (!this.field_154345_e)
                    {
                        RealmsServerStatusPinger.LOGGER.error("Can\'t ping " + p_pingServer_1_ + ": " + reason.getUnformattedText());
                    }
                }
            });

            try
            {
                lvt_4_1_.sendPacket(new C00Handshake(RealmsSharedConstants.NETWORK_PROTOCOL_VERSION, lvt_3_1_.getHost(), lvt_3_1_.getPort(), EnumConnectionState.STATUS));
                lvt_4_1_.sendPacket(new C00PacketServerQuery());
            }
            catch (Throwable var6)
            {
                LOGGER.error(var6);
            }
        }
    }

    public void tick()
    {
        synchronized (this.connections)
        {
            Iterator<NetworkManager> lvt_2_1_ = this.connections.iterator();

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

    public void removeAll()
    {
        synchronized (this.connections)
        {
            Iterator<NetworkManager> lvt_2_1_ = this.connections.iterator();

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
