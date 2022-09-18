package net.minecraft.client.audio;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.Validate;

public class SoundListSerializer implements JsonDeserializer<SoundList>
{
    public SoundList deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
    {
        JsonObject lvt_4_1_ = JsonUtils.getJsonObject(p_deserialize_1_, "entry");
        SoundList lvt_5_1_ = new SoundList();
        lvt_5_1_.setReplaceExisting(JsonUtils.getBoolean(lvt_4_1_, "replace", false));
        SoundCategory lvt_6_1_ = SoundCategory.getCategory(JsonUtils.getString(lvt_4_1_, "category", SoundCategory.MASTER.getCategoryName()));
        lvt_5_1_.setSoundCategory(lvt_6_1_);
        Validate.notNull(lvt_6_1_, "Invalid category", new Object[0]);

        if (lvt_4_1_.has("sounds"))
        {
            JsonArray lvt_7_1_ = JsonUtils.getJsonArray(lvt_4_1_, "sounds");

            for (int lvt_8_1_ = 0; lvt_8_1_ < lvt_7_1_.size(); ++lvt_8_1_)
            {
                JsonElement lvt_9_1_ = lvt_7_1_.get(lvt_8_1_);
                SoundList.SoundEntry lvt_10_1_ = new SoundList.SoundEntry();

                if (JsonUtils.isString(lvt_9_1_))
                {
                    lvt_10_1_.setSoundEntryName(JsonUtils.getString(lvt_9_1_, "sound"));
                }
                else
                {
                    JsonObject lvt_11_1_ = JsonUtils.getJsonObject(lvt_9_1_, "sound");
                    lvt_10_1_.setSoundEntryName(JsonUtils.getString(lvt_11_1_, "name"));

                    if (lvt_11_1_.has("type"))
                    {
                        SoundList.SoundEntry.Type lvt_12_1_ = SoundList.SoundEntry.Type.getType(JsonUtils.getString(lvt_11_1_, "type"));
                        Validate.notNull(lvt_12_1_, "Invalid type", new Object[0]);
                        lvt_10_1_.setSoundEntryType(lvt_12_1_);
                    }

                    if (lvt_11_1_.has("volume"))
                    {
                        float lvt_12_2_ = JsonUtils.getFloat(lvt_11_1_, "volume");
                        Validate.isTrue(lvt_12_2_ > 0.0F, "Invalid volume", new Object[0]);
                        lvt_10_1_.setSoundEntryVolume(lvt_12_2_);
                    }

                    if (lvt_11_1_.has("pitch"))
                    {
                        float lvt_12_3_ = JsonUtils.getFloat(lvt_11_1_, "pitch");
                        Validate.isTrue(lvt_12_3_ > 0.0F, "Invalid pitch", new Object[0]);
                        lvt_10_1_.setSoundEntryPitch(lvt_12_3_);
                    }

                    if (lvt_11_1_.has("weight"))
                    {
                        int lvt_12_4_ = JsonUtils.getInt(lvt_11_1_, "weight");
                        Validate.isTrue(lvt_12_4_ > 0, "Invalid weight", new Object[0]);
                        lvt_10_1_.setSoundEntryWeight(lvt_12_4_);
                    }

                    if (lvt_11_1_.has("stream"))
                    {
                        lvt_10_1_.setStreaming(JsonUtils.getBoolean(lvt_11_1_, "stream"));
                    }
                }

                lvt_5_1_.getSoundList().add(lvt_10_1_);
            }
        }

        return lvt_5_1_;
    }

    public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
    {
        return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
    }
}
