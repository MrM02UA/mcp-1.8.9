package net.minecraft.client.resources.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.JsonUtils;

public class PackMetadataSectionSerializer extends BaseMetadataSectionSerializer<PackMetadataSection> implements JsonSerializer<PackMetadataSection>
{
    public PackMetadataSection deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
    {
        JsonObject lvt_4_1_ = p_deserialize_1_.getAsJsonObject();
        IChatComponent lvt_5_1_ = (IChatComponent)p_deserialize_3_.deserialize(lvt_4_1_.get("description"), IChatComponent.class);

        if (lvt_5_1_ == null)
        {
            throw new JsonParseException("Invalid/missing description!");
        }
        else
        {
            int lvt_6_1_ = JsonUtils.getInt(lvt_4_1_, "pack_format");
            return new PackMetadataSection(lvt_5_1_, lvt_6_1_);
        }
    }

    public JsonElement serialize(PackMetadataSection p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_)
    {
        JsonObject lvt_4_1_ = new JsonObject();
        lvt_4_1_.addProperty("pack_format", Integer.valueOf(p_serialize_1_.getPackFormat()));
        lvt_4_1_.add("description", p_serialize_3_.serialize(p_serialize_1_.getPackDescription()));
        return lvt_4_1_;
    }

    /**
     * The name of this section type as it appears in JSON.
     */
    public String getSectionName()
    {
        return "pack";
    }

    public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
    {
        return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
    }

    public JsonElement serialize(Object p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_)
    {
        return this.serialize((PackMetadataSection)p_serialize_1_, p_serialize_2_, p_serialize_3_);
    }
}
