package net.minecraft.server.management;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.IOUtils;

public class PlayerProfileCache
{
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    private final Map<String, PlayerProfileCache.ProfileEntry> usernameToProfileEntryMap = Maps.newHashMap();
    private final Map<UUID, PlayerProfileCache.ProfileEntry> uuidToProfileEntryMap = Maps.newHashMap();
    private final LinkedList<GameProfile> gameProfiles = Lists.newLinkedList();
    private final MinecraftServer mcServer;
    protected final Gson gson;
    private final File usercacheFile;
    private static final ParameterizedType TYPE = new ParameterizedType()
    {
        public Type[] getActualTypeArguments()
        {
            return new Type[] {PlayerProfileCache.ProfileEntry.class};
        }
        public Type getRawType()
        {
            return List.class;
        }
        public Type getOwnerType()
        {
            return null;
        }
    };

    public PlayerProfileCache(MinecraftServer server, File cacheFile)
    {
        this.mcServer = server;
        this.usercacheFile = cacheFile;
        GsonBuilder lvt_3_1_ = new GsonBuilder();
        lvt_3_1_.registerTypeHierarchyAdapter(PlayerProfileCache.ProfileEntry.class, new PlayerProfileCache.Serializer());
        this.gson = lvt_3_1_.create();
        this.load();
    }

    /**
     * Get a GameProfile given the MinecraftServer and the player's username.

     *  The UUID of the GameProfile will <b>not</b> be null. If the server is offline, a UUID based on the hash of the
     * username will be used.
     */
    private static GameProfile getGameProfile(MinecraftServer server, String username)
    {
        final GameProfile[] lvt_2_1_ = new GameProfile[1];
        ProfileLookupCallback lvt_3_1_ = new ProfileLookupCallback()
        {
            public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_)
            {
                lvt_2_1_[0] = p_onProfileLookupSucceeded_1_;
            }
            public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_)
            {
                lvt_2_1_[0] = null;
            }
        };
        server.getGameProfileRepository().findProfilesByNames(new String[] {username}, Agent.MINECRAFT, lvt_3_1_);

        if (!server.isServerInOnlineMode() && lvt_2_1_[0] == null)
        {
            UUID lvt_4_1_ = EntityPlayer.getUUID(new GameProfile((UUID)null, username));
            GameProfile lvt_5_1_ = new GameProfile(lvt_4_1_, username);
            lvt_3_1_.onProfileLookupSucceeded(lvt_5_1_);
        }

        return lvt_2_1_[0];
    }

    /**
     * Add an entry to this cache
     */
    public void addEntry(GameProfile gameProfile)
    {
        this.addEntry(gameProfile, (Date)null);
    }

    /**
     * Add an entry to this cache
     */
    private void addEntry(GameProfile gameProfile, Date expirationDate)
    {
        UUID lvt_3_1_ = gameProfile.getId();

        if (expirationDate == null)
        {
            Calendar lvt_4_1_ = Calendar.getInstance();
            lvt_4_1_.setTime(new Date());
            lvt_4_1_.add(2, 1);
            expirationDate = lvt_4_1_.getTime();
        }

        String lvt_4_2_ = gameProfile.getName().toLowerCase(Locale.ROOT);
        PlayerProfileCache.ProfileEntry lvt_5_1_ = new PlayerProfileCache.ProfileEntry(gameProfile, expirationDate);

        if (this.uuidToProfileEntryMap.containsKey(lvt_3_1_))
        {
            PlayerProfileCache.ProfileEntry lvt_6_1_ = (PlayerProfileCache.ProfileEntry)this.uuidToProfileEntryMap.get(lvt_3_1_);
            this.usernameToProfileEntryMap.remove(lvt_6_1_.getGameProfile().getName().toLowerCase(Locale.ROOT));
            this.gameProfiles.remove(gameProfile);
        }

        this.usernameToProfileEntryMap.put(gameProfile.getName().toLowerCase(Locale.ROOT), lvt_5_1_);
        this.uuidToProfileEntryMap.put(lvt_3_1_, lvt_5_1_);
        this.gameProfiles.addFirst(gameProfile);
        this.save();
    }

    /**
     * Get a player's GameProfile given their username. Mojang's server's will be contacted if the entry is not cached
     * locally.
     */
    public GameProfile getGameProfileForUsername(String username)
    {
        String lvt_2_1_ = username.toLowerCase(Locale.ROOT);
        PlayerProfileCache.ProfileEntry lvt_3_1_ = (PlayerProfileCache.ProfileEntry)this.usernameToProfileEntryMap.get(lvt_2_1_);

        if (lvt_3_1_ != null && (new Date()).getTime() >= lvt_3_1_.expirationDate.getTime())
        {
            this.uuidToProfileEntryMap.remove(lvt_3_1_.getGameProfile().getId());
            this.usernameToProfileEntryMap.remove(lvt_3_1_.getGameProfile().getName().toLowerCase(Locale.ROOT));
            this.gameProfiles.remove(lvt_3_1_.getGameProfile());
            lvt_3_1_ = null;
        }

        if (lvt_3_1_ != null)
        {
            GameProfile lvt_4_1_ = lvt_3_1_.getGameProfile();
            this.gameProfiles.remove(lvt_4_1_);
            this.gameProfiles.addFirst(lvt_4_1_);
        }
        else
        {
            GameProfile lvt_4_2_ = getGameProfile(this.mcServer, lvt_2_1_);

            if (lvt_4_2_ != null)
            {
                this.addEntry(lvt_4_2_);
                lvt_3_1_ = (PlayerProfileCache.ProfileEntry)this.usernameToProfileEntryMap.get(lvt_2_1_);
            }
        }

        this.save();
        return lvt_3_1_ == null ? null : lvt_3_1_.getGameProfile();
    }

    /**
     * Get an array of the usernames that are cached in this cache
     */
    public String[] getUsernames()
    {
        List<String> lvt_1_1_ = Lists.newArrayList(this.usernameToProfileEntryMap.keySet());
        return (String[])lvt_1_1_.toArray(new String[lvt_1_1_.size()]);
    }

    /**
     * Get a player's {@link GameProfile} given their UUID
     */
    public GameProfile getProfileByUUID(UUID uuid)
    {
        PlayerProfileCache.ProfileEntry lvt_2_1_ = (PlayerProfileCache.ProfileEntry)this.uuidToProfileEntryMap.get(uuid);
        return lvt_2_1_ == null ? null : lvt_2_1_.getGameProfile();
    }

    /**
     * Get a {@link ProfileEntry} by UUID
     */
    private PlayerProfileCache.ProfileEntry getByUUID(UUID uuid)
    {
        PlayerProfileCache.ProfileEntry lvt_2_1_ = (PlayerProfileCache.ProfileEntry)this.uuidToProfileEntryMap.get(uuid);

        if (lvt_2_1_ != null)
        {
            GameProfile lvt_3_1_ = lvt_2_1_.getGameProfile();
            this.gameProfiles.remove(lvt_3_1_);
            this.gameProfiles.addFirst(lvt_3_1_);
        }

        return lvt_2_1_;
    }

    /**
     * Load the cached profiles from disk
     */
    public void load()
    {
        BufferedReader lvt_1_1_ = null;

        try
        {
            lvt_1_1_ = Files.newReader(this.usercacheFile, Charsets.UTF_8);
            List<PlayerProfileCache.ProfileEntry> lvt_2_1_ = (List)this.gson.fromJson(lvt_1_1_, TYPE);
            this.usernameToProfileEntryMap.clear();
            this.uuidToProfileEntryMap.clear();
            this.gameProfiles.clear();

            for (PlayerProfileCache.ProfileEntry lvt_4_1_ : Lists.reverse(lvt_2_1_))
            {
                if (lvt_4_1_ != null)
                {
                    this.addEntry(lvt_4_1_.getGameProfile(), lvt_4_1_.getExpirationDate());
                }
            }
        }
        catch (FileNotFoundException var9)
        {
            ;
        }
        catch (JsonParseException var10)
        {
            ;
        }
        finally
        {
            IOUtils.closeQuietly(lvt_1_1_);
        }
    }

    /**
     * Save the cached profiles to disk
     */
    public void save()
    {
        String lvt_1_1_ = this.gson.toJson(this.getEntriesWithLimit(1000));
        BufferedWriter lvt_2_1_ = null;

        try
        {
            lvt_2_1_ = Files.newWriter(this.usercacheFile, Charsets.UTF_8);
            lvt_2_1_.write(lvt_1_1_);
            return;
        }
        catch (FileNotFoundException var8)
        {
            ;
        }
        catch (IOException var9)
        {
            return;
        }
        finally
        {
            IOUtils.closeQuietly(lvt_2_1_);
        }
    }

    private List<PlayerProfileCache.ProfileEntry> getEntriesWithLimit(int limitSize)
    {
        ArrayList<PlayerProfileCache.ProfileEntry> lvt_2_1_ = Lists.newArrayList();

        for (GameProfile lvt_5_1_ : Lists.newArrayList(Iterators.limit(this.gameProfiles.iterator(), limitSize)))
        {
            PlayerProfileCache.ProfileEntry lvt_6_1_ = this.getByUUID(lvt_5_1_.getId());

            if (lvt_6_1_ != null)
            {
                lvt_2_1_.add(lvt_6_1_);
            }
        }

        return lvt_2_1_;
    }

    class ProfileEntry
    {
        private final GameProfile gameProfile;
        private final Date expirationDate;

        private ProfileEntry(GameProfile gameProfileIn, Date expirationDateIn)
        {
            this.gameProfile = gameProfileIn;
            this.expirationDate = expirationDateIn;
        }

        public GameProfile getGameProfile()
        {
            return this.gameProfile;
        }

        public Date getExpirationDate()
        {
            return this.expirationDate;
        }
    }

    class Serializer implements JsonDeserializer<PlayerProfileCache.ProfileEntry>, JsonSerializer<PlayerProfileCache.ProfileEntry>
    {
        private Serializer()
        {
        }

        public JsonElement serialize(PlayerProfileCache.ProfileEntry p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_)
        {
            JsonObject lvt_4_1_ = new JsonObject();
            lvt_4_1_.addProperty("name", p_serialize_1_.getGameProfile().getName());
            UUID lvt_5_1_ = p_serialize_1_.getGameProfile().getId();
            lvt_4_1_.addProperty("uuid", lvt_5_1_ == null ? "" : lvt_5_1_.toString());
            lvt_4_1_.addProperty("expiresOn", PlayerProfileCache.dateFormat.format(p_serialize_1_.getExpirationDate()));
            return lvt_4_1_;
        }

        public PlayerProfileCache.ProfileEntry deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            if (p_deserialize_1_.isJsonObject())
            {
                JsonObject lvt_4_1_ = p_deserialize_1_.getAsJsonObject();
                JsonElement lvt_5_1_ = lvt_4_1_.get("name");
                JsonElement lvt_6_1_ = lvt_4_1_.get("uuid");
                JsonElement lvt_7_1_ = lvt_4_1_.get("expiresOn");

                if (lvt_5_1_ != null && lvt_6_1_ != null)
                {
                    String lvt_8_1_ = lvt_6_1_.getAsString();
                    String lvt_9_1_ = lvt_5_1_.getAsString();
                    Date lvt_10_1_ = null;

                    if (lvt_7_1_ != null)
                    {
                        try
                        {
                            lvt_10_1_ = PlayerProfileCache.dateFormat.parse(lvt_7_1_.getAsString());
                        }
                        catch (ParseException var14)
                        {
                            lvt_10_1_ = null;
                        }
                    }

                    if (lvt_9_1_ != null && lvt_8_1_ != null)
                    {
                        UUID lvt_11_2_;

                        try
                        {
                            lvt_11_2_ = UUID.fromString(lvt_8_1_);
                        }
                        catch (Throwable var13)
                        {
                            return null;
                        }

                        PlayerProfileCache.ProfileEntry lvt_12_2_ = PlayerProfileCache.this.new ProfileEntry(new GameProfile(lvt_11_2_, lvt_9_1_), lvt_10_1_);
                        return lvt_12_2_;
                    }
                    else
                    {
                        return null;
                    }
                }
                else
                {
                    return null;
                }
            }
            else
            {
                return null;
            }
        }

        public JsonElement serialize(Object p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_)
        {
            return this.serialize((PlayerProfileCache.ProfileEntry)p_serialize_1_, p_serialize_2_, p_serialize_3_);
        }

        public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
        }
    }
}
