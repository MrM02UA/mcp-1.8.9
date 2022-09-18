package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.MathHelper;
import org.lwjgl.util.vector.Vector3f;

public class ItemTransformVec3f
{
    public static final ItemTransformVec3f DEFAULT = new ItemTransformVec3f(new Vector3f(), new Vector3f(), new Vector3f(1.0F, 1.0F, 1.0F));
    public final Vector3f rotation;
    public final Vector3f translation;
    public final Vector3f scale;

    public ItemTransformVec3f(Vector3f rotation, Vector3f translation, Vector3f scale)
    {
        this.rotation = new Vector3f(rotation);
        this.translation = new Vector3f(translation);
        this.scale = new Vector3f(scale);
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (this.getClass() != p_equals_1_.getClass())
        {
            return false;
        }
        else
        {
            ItemTransformVec3f lvt_2_1_ = (ItemTransformVec3f)p_equals_1_;
            return !this.rotation.equals(lvt_2_1_.rotation) ? false : (!this.scale.equals(lvt_2_1_.scale) ? false : this.translation.equals(lvt_2_1_.translation));
        }
    }

    public int hashCode()
    {
        int lvt_1_1_ = this.rotation.hashCode();
        lvt_1_1_ = 31 * lvt_1_1_ + this.translation.hashCode();
        lvt_1_1_ = 31 * lvt_1_1_ + this.scale.hashCode();
        return lvt_1_1_;
    }

    static class Deserializer implements JsonDeserializer<ItemTransformVec3f>
    {
        private static final Vector3f ROTATION_DEFAULT = new Vector3f(0.0F, 0.0F, 0.0F);
        private static final Vector3f TRANSLATION_DEFAULT = new Vector3f(0.0F, 0.0F, 0.0F);
        private static final Vector3f SCALE_DEFAULT = new Vector3f(1.0F, 1.0F, 1.0F);

        public ItemTransformVec3f deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            JsonObject lvt_4_1_ = p_deserialize_1_.getAsJsonObject();
            Vector3f lvt_5_1_ = this.parseVector3f(lvt_4_1_, "rotation", ROTATION_DEFAULT);
            Vector3f lvt_6_1_ = this.parseVector3f(lvt_4_1_, "translation", TRANSLATION_DEFAULT);
            lvt_6_1_.scale(0.0625F);
            lvt_6_1_.x = MathHelper.clamp_float(lvt_6_1_.x, -1.5F, 1.5F);
            lvt_6_1_.y = MathHelper.clamp_float(lvt_6_1_.y, -1.5F, 1.5F);
            lvt_6_1_.z = MathHelper.clamp_float(lvt_6_1_.z, -1.5F, 1.5F);
            Vector3f lvt_7_1_ = this.parseVector3f(lvt_4_1_, "scale", SCALE_DEFAULT);
            lvt_7_1_.x = MathHelper.clamp_float(lvt_7_1_.x, -4.0F, 4.0F);
            lvt_7_1_.y = MathHelper.clamp_float(lvt_7_1_.y, -4.0F, 4.0F);
            lvt_7_1_.z = MathHelper.clamp_float(lvt_7_1_.z, -4.0F, 4.0F);
            return new ItemTransformVec3f(lvt_5_1_, lvt_6_1_, lvt_7_1_);
        }

        private Vector3f parseVector3f(JsonObject jsonObject, String key, Vector3f defaultValue)
        {
            if (!jsonObject.has(key))
            {
                return defaultValue;
            }
            else
            {
                JsonArray lvt_4_1_ = JsonUtils.getJsonArray(jsonObject, key);

                if (lvt_4_1_.size() != 3)
                {
                    throw new JsonParseException("Expected 3 " + key + " values, found: " + lvt_4_1_.size());
                }
                else
                {
                    float[] lvt_5_1_ = new float[3];

                    for (int lvt_6_1_ = 0; lvt_6_1_ < lvt_5_1_.length; ++lvt_6_1_)
                    {
                        lvt_5_1_[lvt_6_1_] = JsonUtils.getFloat(lvt_4_1_.get(lvt_6_1_), key + "[" + lvt_6_1_ + "]");
                    }

                    return new Vector3f(lvt_5_1_[0], lvt_5_1_[1], lvt_5_1_[2]);
                }
            }
        }

        public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
        }
    }
}
