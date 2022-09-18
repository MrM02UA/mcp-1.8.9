package net.minecraft.server.management;

import com.google.common.base.Charsets;
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserList<K, V extends UserListEntry<K>>
{
    protected static final Logger logger = LogManager.getLogger();
    protected final Gson gson;
    private final File saveFile;
    private final Map<String, V> values = Maps.newHashMap();
    private boolean lanServer = true;
    private static final ParameterizedType saveFileFormat = new ParameterizedType()
    {
        public Type[] getActualTypeArguments()
        {
            return new Type[] {UserListEntry.class};
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

    public UserList(File saveFile)
    {
        this.saveFile = saveFile;
        GsonBuilder lvt_2_1_ = (new GsonBuilder()).setPrettyPrinting();
        lvt_2_1_.registerTypeHierarchyAdapter(UserListEntry.class, new UserList.Serializer());
        this.gson = lvt_2_1_.create();
    }

    public boolean isLanServer()
    {
        return this.lanServer;
    }

    public void setLanServer(boolean state)
    {
        this.lanServer = state;
    }

    /**
     * Adds an entry to the list
     */
    public void addEntry(V entry)
    {
        this.values.put(this.getObjectKey(entry.getValue()), entry);

        try
        {
            this.writeChanges();
        }
        catch (IOException var3)
        {
            logger.warn("Could not save the list after adding a user.", var3);
        }
    }

    public V getEntry(K obj)
    {
        this.removeExpired();
        return (V)((UserListEntry)this.values.get(this.getObjectKey(obj)));
    }

    public void removeEntry(K entry)
    {
        this.values.remove(this.getObjectKey(entry));

        try
        {
            this.writeChanges();
        }
        catch (IOException var3)
        {
            logger.warn("Could not save the list after removing a user.", var3);
        }
    }

    public String[] getKeys()
    {
        return (String[])this.values.keySet().toArray(new String[this.values.size()]);
    }

    /**
     * Gets the key value for the given object
     */
    protected String getObjectKey(K obj)
    {
        return obj.toString();
    }

    protected boolean hasEntry(K entry)
    {
        return this.values.containsKey(this.getObjectKey(entry));
    }

    /**
     * Removes expired bans from the list. See {@link BanEntry#hasBanExpired}
     */
    private void removeExpired()
    {
        List<K> lvt_1_1_ = Lists.newArrayList();

        for (V lvt_3_1_ : this.values.values())
        {
            if (lvt_3_1_.hasBanExpired())
            {
                lvt_1_1_.add(lvt_3_1_.getValue());
            }
        }

        for (K lvt_3_2_ : lvt_1_1_)
        {
            this.values.remove(lvt_3_2_);
        }
    }

    protected UserListEntry<K> createEntry(JsonObject entryData)
    {
        return new UserListEntry((Object)null, entryData);
    }

    protected Map<String, V> getValues()
    {
        return this.values;
    }

    public void writeChanges() throws IOException
    {
        Collection<V> lvt_1_1_ = this.values.values();
        String lvt_2_1_ = this.gson.toJson(lvt_1_1_);
        BufferedWriter lvt_3_1_ = null;

        try
        {
            lvt_3_1_ = Files.newWriter(this.saveFile, Charsets.UTF_8);
            lvt_3_1_.write(lvt_2_1_);
        }
        finally
        {
            IOUtils.closeQuietly(lvt_3_1_);
        }
    }

    class Serializer implements JsonDeserializer<UserListEntry<K>>, JsonSerializer<UserListEntry<K>>
    {
        private Serializer()
        {
        }

        public JsonElement serialize(UserListEntry<K> p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_)
        {
            JsonObject lvt_4_1_ = new JsonObject();
            p_serialize_1_.onSerialization(lvt_4_1_);
            return lvt_4_1_;
        }

        public UserListEntry<K> deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            if (p_deserialize_1_.isJsonObject())
            {
                JsonObject lvt_4_1_ = p_deserialize_1_.getAsJsonObject();
                UserListEntry<K> lvt_5_1_ = UserList.this.createEntry(lvt_4_1_);
                return lvt_5_1_;
            }
            else
            {
                return null;
            }
        }

        public JsonElement serialize(Object p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_)
        {
            return this.serialize((UserListEntry)p_serialize_1_, p_serialize_2_, p_serialize_3_);
        }

        public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
        }
    }
}
