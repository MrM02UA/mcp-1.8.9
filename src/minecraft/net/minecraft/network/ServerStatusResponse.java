package net.minecraft.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Type;
import java.util.UUID;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.JsonUtils;

public class ServerStatusResponse
{
    private IChatComponent serverMotd;
    private ServerStatusResponse.PlayerCountData playerCount;
    private ServerStatusResponse.MinecraftProtocolVersionIdentifier protocolVersion;
    private String favicon;

    public IChatComponent getServerDescription()
    {
        return this.serverMotd;
    }

    public void setServerDescription(IChatComponent motd)
    {
        this.serverMotd = motd;
    }

    public ServerStatusResponse.PlayerCountData getPlayerCountData()
    {
        return this.playerCount;
    }

    public void setPlayerCountData(ServerStatusResponse.PlayerCountData countData)
    {
        this.playerCount = countData;
    }

    public ServerStatusResponse.MinecraftProtocolVersionIdentifier getProtocolVersionInfo()
    {
        return this.protocolVersion;
    }

    public void setProtocolVersionInfo(ServerStatusResponse.MinecraftProtocolVersionIdentifier protocolVersionData)
    {
        this.protocolVersion = protocolVersionData;
    }

    public void setFavicon(String faviconBlob)
    {
        this.favicon = faviconBlob;
    }

    public String getFavicon()
    {
        return this.favicon;
    }

    public static class MinecraftProtocolVersionIdentifier
    {
        private final String name;
        private final int protocol;

        public MinecraftProtocolVersionIdentifier(String nameIn, int protocolIn)
        {
            this.name = nameIn;
            this.protocol = protocolIn;
        }

        public String getName()
        {
            return this.name;
        }

        public int getProtocol()
        {
            return this.protocol;
        }

        public static class Serializer implements JsonDeserializer<ServerStatusResponse.MinecraftProtocolVersionIdentifier>, JsonSerializer<ServerStatusResponse.MinecraftProtocolVersionIdentifier>
        {
            public ServerStatusResponse.MinecraftProtocolVersionIdentifier deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
            {
                JsonObject lvt_4_1_ = JsonUtils.getJsonObject(p_deserialize_1_, "version");
                return new ServerStatusResponse.MinecraftProtocolVersionIdentifier(JsonUtils.getString(lvt_4_1_, "name"), JsonUtils.getInt(lvt_4_1_, "protocol"));
            }

            public JsonElement serialize(ServerStatusResponse.MinecraftProtocolVersionIdentifier p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_)
            {
                JsonObject lvt_4_1_ = new JsonObject();
                lvt_4_1_.addProperty("name", p_serialize_1_.getName());
                lvt_4_1_.addProperty("protocol", Integer.valueOf(p_serialize_1_.getProtocol()));
                return lvt_4_1_;
            }

            public JsonElement serialize(Object p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_)
            {
                return this.serialize((ServerStatusResponse.MinecraftProtocolVersionIdentifier)p_serialize_1_, p_serialize_2_, p_serialize_3_);
            }

            public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
            {
                return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
            }
        }
    }

    public static class PlayerCountData
    {
        private final int maxPlayers;
        private final int onlinePlayerCount;
        private GameProfile[] players;

        public PlayerCountData(int maxOnlinePlayers, int onlinePlayers)
        {
            this.maxPlayers = maxOnlinePlayers;
            this.onlinePlayerCount = onlinePlayers;
        }

        public int getMaxPlayers()
        {
            return this.maxPlayers;
        }

        public int getOnlinePlayerCount()
        {
            return this.onlinePlayerCount;
        }

        public GameProfile[] getPlayers()
        {
            return this.players;
        }

        public void setPlayers(GameProfile[] playersIn)
        {
            this.players = playersIn;
        }

        public static class Serializer implements JsonDeserializer<ServerStatusResponse.PlayerCountData>, JsonSerializer<ServerStatusResponse.PlayerCountData>
        {
            public ServerStatusResponse.PlayerCountData deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
            {
                JsonObject lvt_4_1_ = JsonUtils.getJsonObject(p_deserialize_1_, "players");
                ServerStatusResponse.PlayerCountData lvt_5_1_ = new ServerStatusResponse.PlayerCountData(JsonUtils.getInt(lvt_4_1_, "max"), JsonUtils.getInt(lvt_4_1_, "online"));

                if (JsonUtils.isJsonArray(lvt_4_1_, "sample"))
                {
                    JsonArray lvt_6_1_ = JsonUtils.getJsonArray(lvt_4_1_, "sample");

                    if (lvt_6_1_.size() > 0)
                    {
                        GameProfile[] lvt_7_1_ = new GameProfile[lvt_6_1_.size()];

                        for (int lvt_8_1_ = 0; lvt_8_1_ < lvt_7_1_.length; ++lvt_8_1_)
                        {
                            JsonObject lvt_9_1_ = JsonUtils.getJsonObject(lvt_6_1_.get(lvt_8_1_), "player[" + lvt_8_1_ + "]");
                            String lvt_10_1_ = JsonUtils.getString(lvt_9_1_, "id");
                            lvt_7_1_[lvt_8_1_] = new GameProfile(UUID.fromString(lvt_10_1_), JsonUtils.getString(lvt_9_1_, "name"));
                        }

                        lvt_5_1_.setPlayers(lvt_7_1_);
                    }
                }

                return lvt_5_1_;
            }

            public JsonElement serialize(ServerStatusResponse.PlayerCountData p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_)
            {
                JsonObject lvt_4_1_ = new JsonObject();
                lvt_4_1_.addProperty("max", Integer.valueOf(p_serialize_1_.getMaxPlayers()));
                lvt_4_1_.addProperty("online", Integer.valueOf(p_serialize_1_.getOnlinePlayerCount()));

                if (p_serialize_1_.getPlayers() != null && p_serialize_1_.getPlayers().length > 0)
                {
                    JsonArray lvt_5_1_ = new JsonArray();

                    for (int lvt_6_1_ = 0; lvt_6_1_ < p_serialize_1_.getPlayers().length; ++lvt_6_1_)
                    {
                        JsonObject lvt_7_1_ = new JsonObject();
                        UUID lvt_8_1_ = p_serialize_1_.getPlayers()[lvt_6_1_].getId();
                        lvt_7_1_.addProperty("id", lvt_8_1_ == null ? "" : lvt_8_1_.toString());
                        lvt_7_1_.addProperty("name", p_serialize_1_.getPlayers()[lvt_6_1_].getName());
                        lvt_5_1_.add(lvt_7_1_);
                    }

                    lvt_4_1_.add("sample", lvt_5_1_);
                }

                return lvt_4_1_;
            }

            public JsonElement serialize(Object p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_)
            {
                return this.serialize((ServerStatusResponse.PlayerCountData)p_serialize_1_, p_serialize_2_, p_serialize_3_);
            }

            public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
            {
                return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
            }
        }
    }

    public static class Serializer implements JsonDeserializer<ServerStatusResponse>, JsonSerializer<ServerStatusResponse>
    {
        public ServerStatusResponse deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            JsonObject lvt_4_1_ = JsonUtils.getJsonObject(p_deserialize_1_, "status");
            ServerStatusResponse lvt_5_1_ = new ServerStatusResponse();

            if (lvt_4_1_.has("description"))
            {
                lvt_5_1_.setServerDescription((IChatComponent)p_deserialize_3_.deserialize(lvt_4_1_.get("description"), IChatComponent.class));
            }

            if (lvt_4_1_.has("players"))
            {
                lvt_5_1_.setPlayerCountData((ServerStatusResponse.PlayerCountData)p_deserialize_3_.deserialize(lvt_4_1_.get("players"), ServerStatusResponse.PlayerCountData.class));
            }

            if (lvt_4_1_.has("version"))
            {
                lvt_5_1_.setProtocolVersionInfo((ServerStatusResponse.MinecraftProtocolVersionIdentifier)p_deserialize_3_.deserialize(lvt_4_1_.get("version"), ServerStatusResponse.MinecraftProtocolVersionIdentifier.class));
            }

            if (lvt_4_1_.has("favicon"))
            {
                lvt_5_1_.setFavicon(JsonUtils.getString(lvt_4_1_, "favicon"));
            }

            return lvt_5_1_;
        }

        public JsonElement serialize(ServerStatusResponse p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_)
        {
            JsonObject lvt_4_1_ = new JsonObject();

            if (p_serialize_1_.getServerDescription() != null)
            {
                lvt_4_1_.add("description", p_serialize_3_.serialize(p_serialize_1_.getServerDescription()));
            }

            if (p_serialize_1_.getPlayerCountData() != null)
            {
                lvt_4_1_.add("players", p_serialize_3_.serialize(p_serialize_1_.getPlayerCountData()));
            }

            if (p_serialize_1_.getProtocolVersionInfo() != null)
            {
                lvt_4_1_.add("version", p_serialize_3_.serialize(p_serialize_1_.getProtocolVersionInfo()));
            }

            if (p_serialize_1_.getFavicon() != null)
            {
                lvt_4_1_.addProperty("favicon", p_serialize_1_.getFavicon());
            }

            return lvt_4_1_;
        }

        public JsonElement serialize(Object p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_)
        {
            return this.serialize((ServerStatusResponse)p_serialize_1_, p_serialize_2_, p_serialize_3_);
        }

        public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
        }
    }
}
