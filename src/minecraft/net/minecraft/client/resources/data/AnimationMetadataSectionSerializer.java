package net.minecraft.client.resources.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.List;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.Validate;

public class AnimationMetadataSectionSerializer extends BaseMetadataSectionSerializer<AnimationMetadataSection> implements JsonSerializer<AnimationMetadataSection>
{
    public AnimationMetadataSection deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
    {
        List<AnimationFrame> lvt_4_1_ = Lists.newArrayList();
        JsonObject lvt_5_1_ = JsonUtils.getJsonObject(p_deserialize_1_, "metadata section");
        int lvt_6_1_ = JsonUtils.getInt(lvt_5_1_, "frametime", 1);

        if (lvt_6_1_ != 1)
        {
            Validate.inclusiveBetween(1L, 2147483647L, (long)lvt_6_1_, "Invalid default frame time");
        }

        if (lvt_5_1_.has("frames"))
        {
            try
            {
                JsonArray lvt_7_1_ = JsonUtils.getJsonArray(lvt_5_1_, "frames");

                for (int lvt_8_1_ = 0; lvt_8_1_ < lvt_7_1_.size(); ++lvt_8_1_)
                {
                    JsonElement lvt_9_1_ = lvt_7_1_.get(lvt_8_1_);
                    AnimationFrame lvt_10_1_ = this.parseAnimationFrame(lvt_8_1_, lvt_9_1_);

                    if (lvt_10_1_ != null)
                    {
                        lvt_4_1_.add(lvt_10_1_);
                    }
                }
            }
            catch (ClassCastException var11)
            {
                throw new JsonParseException("Invalid animation->frames: expected array, was " + lvt_5_1_.get("frames"), var11);
            }
        }

        int lvt_7_3_ = JsonUtils.getInt(lvt_5_1_, "width", -1);
        int lvt_8_2_ = JsonUtils.getInt(lvt_5_1_, "height", -1);

        if (lvt_7_3_ != -1)
        {
            Validate.inclusiveBetween(1L, 2147483647L, (long)lvt_7_3_, "Invalid width");
        }

        if (lvt_8_2_ != -1)
        {
            Validate.inclusiveBetween(1L, 2147483647L, (long)lvt_8_2_, "Invalid height");
        }

        boolean lvt_9_2_ = JsonUtils.getBoolean(lvt_5_1_, "interpolate", false);
        return new AnimationMetadataSection(lvt_4_1_, lvt_7_3_, lvt_8_2_, lvt_6_1_, lvt_9_2_);
    }

    private AnimationFrame parseAnimationFrame(int p_110492_1_, JsonElement p_110492_2_)
    {
        if (p_110492_2_.isJsonPrimitive())
        {
            return new AnimationFrame(JsonUtils.getInt(p_110492_2_, "frames[" + p_110492_1_ + "]"));
        }
        else if (p_110492_2_.isJsonObject())
        {
            JsonObject lvt_3_1_ = JsonUtils.getJsonObject(p_110492_2_, "frames[" + p_110492_1_ + "]");
            int lvt_4_1_ = JsonUtils.getInt(lvt_3_1_, "time", -1);

            if (lvt_3_1_.has("time"))
            {
                Validate.inclusiveBetween(1L, 2147483647L, (long)lvt_4_1_, "Invalid frame time");
            }

            int lvt_5_1_ = JsonUtils.getInt(lvt_3_1_, "index");
            Validate.inclusiveBetween(0L, 2147483647L, (long)lvt_5_1_, "Invalid frame index");
            return new AnimationFrame(lvt_5_1_, lvt_4_1_);
        }
        else
        {
            return null;
        }
    }

    public JsonElement serialize(AnimationMetadataSection p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_)
    {
        JsonObject lvt_4_1_ = new JsonObject();
        lvt_4_1_.addProperty("frametime", Integer.valueOf(p_serialize_1_.getFrameTime()));

        if (p_serialize_1_.getFrameWidth() != -1)
        {
            lvt_4_1_.addProperty("width", Integer.valueOf(p_serialize_1_.getFrameWidth()));
        }

        if (p_serialize_1_.getFrameHeight() != -1)
        {
            lvt_4_1_.addProperty("height", Integer.valueOf(p_serialize_1_.getFrameHeight()));
        }

        if (p_serialize_1_.getFrameCount() > 0)
        {
            JsonArray lvt_5_1_ = new JsonArray();

            for (int lvt_6_1_ = 0; lvt_6_1_ < p_serialize_1_.getFrameCount(); ++lvt_6_1_)
            {
                if (p_serialize_1_.frameHasTime(lvt_6_1_))
                {
                    JsonObject lvt_7_1_ = new JsonObject();
                    lvt_7_1_.addProperty("index", Integer.valueOf(p_serialize_1_.getFrameIndex(lvt_6_1_)));
                    lvt_7_1_.addProperty("time", Integer.valueOf(p_serialize_1_.getFrameTimeSingle(lvt_6_1_)));
                    lvt_5_1_.add(lvt_7_1_);
                }
                else
                {
                    lvt_5_1_.add(new JsonPrimitive(Integer.valueOf(p_serialize_1_.getFrameIndex(lvt_6_1_))));
                }
            }

            lvt_4_1_.add("frames", lvt_5_1_);
        }

        return lvt_4_1_;
    }

    /**
     * The name of this section type as it appears in JSON.
     */
    public String getSectionName()
    {
        return "animation";
    }

    public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
    {
        return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
    }

    public JsonElement serialize(Object p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_)
    {
        return this.serialize((AnimationMetadataSection)p_serialize_1_, p_serialize_2_, p_serialize_3_);
    }
}
