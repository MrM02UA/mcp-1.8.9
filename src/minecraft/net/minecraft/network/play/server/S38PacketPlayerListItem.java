package net.minecraft.network.play.server;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.IOException;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldSettings;

public class S38PacketPlayerListItem implements Packet<INetHandlerPlayClient>
{
    private S38PacketPlayerListItem.Action action;
    private final List<S38PacketPlayerListItem.AddPlayerData> players = Lists.newArrayList();

    public S38PacketPlayerListItem()
    {
    }

    public S38PacketPlayerListItem(S38PacketPlayerListItem.Action actionIn, EntityPlayerMP... players)
    {
        this.action = actionIn;

        for (EntityPlayerMP lvt_6_1_ : players)
        {
            this.players.add(new S38PacketPlayerListItem.AddPlayerData(lvt_6_1_.getGameProfile(), lvt_6_1_.ping, lvt_6_1_.theItemInWorldManager.getGameType(), lvt_6_1_.getTabListDisplayName()));
        }
    }

    public S38PacketPlayerListItem(S38PacketPlayerListItem.Action actionIn, Iterable<EntityPlayerMP> players)
    {
        this.action = actionIn;

        for (EntityPlayerMP lvt_4_1_ : players)
        {
            this.players.add(new S38PacketPlayerListItem.AddPlayerData(lvt_4_1_.getGameProfile(), lvt_4_1_.ping, lvt_4_1_.theItemInWorldManager.getGameType(), lvt_4_1_.getTabListDisplayName()));
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.action = (S38PacketPlayerListItem.Action)buf.readEnumValue(S38PacketPlayerListItem.Action.class);
        int lvt_2_1_ = buf.readVarIntFromBuffer();

        for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_; ++lvt_3_1_)
        {
            GameProfile lvt_4_1_ = null;
            int lvt_5_1_ = 0;
            WorldSettings.GameType lvt_6_1_ = null;
            IChatComponent lvt_7_1_ = null;

            switch (this.action)
            {
                case ADD_PLAYER:
                    lvt_4_1_ = new GameProfile(buf.readUuid(), buf.readStringFromBuffer(16));
                    int lvt_8_1_ = buf.readVarIntFromBuffer();
                    int lvt_9_1_ = 0;

                    for (; lvt_9_1_ < lvt_8_1_; ++lvt_9_1_)
                    {
                        String lvt_10_1_ = buf.readStringFromBuffer(32767);
                        String lvt_11_1_ = buf.readStringFromBuffer(32767);

                        if (buf.readBoolean())
                        {
                            lvt_4_1_.getProperties().put(lvt_10_1_, new Property(lvt_10_1_, lvt_11_1_, buf.readStringFromBuffer(32767)));
                        }
                        else
                        {
                            lvt_4_1_.getProperties().put(lvt_10_1_, new Property(lvt_10_1_, lvt_11_1_));
                        }
                    }

                    lvt_6_1_ = WorldSettings.GameType.getByID(buf.readVarIntFromBuffer());
                    lvt_5_1_ = buf.readVarIntFromBuffer();

                    if (buf.readBoolean())
                    {
                        lvt_7_1_ = buf.readChatComponent();
                    }

                    break;

                case UPDATE_GAME_MODE:
                    lvt_4_1_ = new GameProfile(buf.readUuid(), (String)null);
                    lvt_6_1_ = WorldSettings.GameType.getByID(buf.readVarIntFromBuffer());
                    break;

                case UPDATE_LATENCY:
                    lvt_4_1_ = new GameProfile(buf.readUuid(), (String)null);
                    lvt_5_1_ = buf.readVarIntFromBuffer();
                    break;

                case UPDATE_DISPLAY_NAME:
                    lvt_4_1_ = new GameProfile(buf.readUuid(), (String)null);

                    if (buf.readBoolean())
                    {
                        lvt_7_1_ = buf.readChatComponent();
                    }

                    break;

                case REMOVE_PLAYER:
                    lvt_4_1_ = new GameProfile(buf.readUuid(), (String)null);
            }

            this.players.add(new S38PacketPlayerListItem.AddPlayerData(lvt_4_1_, lvt_5_1_, lvt_6_1_, lvt_7_1_));
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeEnumValue(this.action);
        buf.writeVarIntToBuffer(this.players.size());

        for (S38PacketPlayerListItem.AddPlayerData lvt_3_1_ : this.players)
        {
            switch (this.action)
            {
                case ADD_PLAYER:
                    buf.writeUuid(lvt_3_1_.getProfile().getId());
                    buf.writeString(lvt_3_1_.getProfile().getName());
                    buf.writeVarIntToBuffer(lvt_3_1_.getProfile().getProperties().size());

                    for (Property lvt_5_1_ : lvt_3_1_.getProfile().getProperties().values())
                    {
                        buf.writeString(lvt_5_1_.getName());
                        buf.writeString(lvt_5_1_.getValue());

                        if (lvt_5_1_.hasSignature())
                        {
                            buf.writeBoolean(true);
                            buf.writeString(lvt_5_1_.getSignature());
                        }
                        else
                        {
                            buf.writeBoolean(false);
                        }
                    }

                    buf.writeVarIntToBuffer(lvt_3_1_.getGameMode().getID());
                    buf.writeVarIntToBuffer(lvt_3_1_.getPing());

                    if (lvt_3_1_.getDisplayName() == null)
                    {
                        buf.writeBoolean(false);
                    }
                    else
                    {
                        buf.writeBoolean(true);
                        buf.writeChatComponent(lvt_3_1_.getDisplayName());
                    }

                    break;

                case UPDATE_GAME_MODE:
                    buf.writeUuid(lvt_3_1_.getProfile().getId());
                    buf.writeVarIntToBuffer(lvt_3_1_.getGameMode().getID());
                    break;

                case UPDATE_LATENCY:
                    buf.writeUuid(lvt_3_1_.getProfile().getId());
                    buf.writeVarIntToBuffer(lvt_3_1_.getPing());
                    break;

                case UPDATE_DISPLAY_NAME:
                    buf.writeUuid(lvt_3_1_.getProfile().getId());

                    if (lvt_3_1_.getDisplayName() == null)
                    {
                        buf.writeBoolean(false);
                    }
                    else
                    {
                        buf.writeBoolean(true);
                        buf.writeChatComponent(lvt_3_1_.getDisplayName());
                    }

                    break;

                case REMOVE_PLAYER:
                    buf.writeUuid(lvt_3_1_.getProfile().getId());
            }
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handlePlayerListItem(this);
    }

    public List<S38PacketPlayerListItem.AddPlayerData> getEntries()
    {
        return this.players;
    }

    public S38PacketPlayerListItem.Action getAction()
    {
        return this.action;
    }

    public String toString()
    {
        return Objects.toStringHelper(this).add("action", this.action).add("entries", this.players).toString();
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }

    public static enum Action
    {
        ADD_PLAYER,
        UPDATE_GAME_MODE,
        UPDATE_LATENCY,
        UPDATE_DISPLAY_NAME,
        REMOVE_PLAYER;
    }

    public class AddPlayerData
    {
        private final int ping;
        private final WorldSettings.GameType gamemode;
        private final GameProfile profile;
        private final IChatComponent displayName;

        public AddPlayerData(GameProfile profile, int pingIn, WorldSettings.GameType gamemodeIn, IChatComponent displayNameIn)
        {
            this.profile = profile;
            this.ping = pingIn;
            this.gamemode = gamemodeIn;
            this.displayName = displayNameIn;
        }

        public GameProfile getProfile()
        {
            return this.profile;
        }

        public int getPing()
        {
            return this.ping;
        }

        public WorldSettings.GameType getGameMode()
        {
            return this.gamemode;
        }

        public IChatComponent getDisplayName()
        {
            return this.displayName;
        }

        public String toString()
        {
            return Objects.toStringHelper(this).add("latency", this.ping).add("gameMode", this.gamemode).add("profile", this.profile).add("displayName", this.displayName == null ? null : IChatComponent.Serializer.componentToJson(this.displayName)).toString();
        }
    }
}
