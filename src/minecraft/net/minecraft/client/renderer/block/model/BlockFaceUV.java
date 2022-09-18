package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;

public class BlockFaceUV
{
    public float[] uvs;
    public final int rotation;

    public BlockFaceUV(float[] uvsIn, int rotationIn)
    {
        this.uvs = uvsIn;
        this.rotation = rotationIn;
    }

    public float func_178348_a(int p_178348_1_)
    {
        if (this.uvs == null)
        {
            throw new NullPointerException("uvs");
        }
        else
        {
            int lvt_2_1_ = this.func_178347_d(p_178348_1_);
            return lvt_2_1_ != 0 && lvt_2_1_ != 1 ? this.uvs[2] : this.uvs[0];
        }
    }

    public float func_178346_b(int p_178346_1_)
    {
        if (this.uvs == null)
        {
            throw new NullPointerException("uvs");
        }
        else
        {
            int lvt_2_1_ = this.func_178347_d(p_178346_1_);
            return lvt_2_1_ != 0 && lvt_2_1_ != 3 ? this.uvs[3] : this.uvs[1];
        }
    }

    private int func_178347_d(int p_178347_1_)
    {
        return (p_178347_1_ + this.rotation / 90) % 4;
    }

    public int func_178345_c(int p_178345_1_)
    {
        return (p_178345_1_ + (4 - this.rotation / 90)) % 4;
    }

    public void setUvs(float[] uvsIn)
    {
        if (this.uvs == null)
        {
            this.uvs = uvsIn;
        }
    }

    static class Deserializer implements JsonDeserializer<BlockFaceUV>
    {
        public BlockFaceUV deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            JsonObject lvt_4_1_ = p_deserialize_1_.getAsJsonObject();
            float[] lvt_5_1_ = this.parseUV(lvt_4_1_);
            int lvt_6_1_ = this.parseRotation(lvt_4_1_);
            return new BlockFaceUV(lvt_5_1_, lvt_6_1_);
        }

        protected int parseRotation(JsonObject p_178291_1_)
        {
            int lvt_2_1_ = JsonUtils.getInt(p_178291_1_, "rotation", 0);

            if (lvt_2_1_ >= 0 && lvt_2_1_ % 90 == 0 && lvt_2_1_ / 90 <= 3)
            {
                return lvt_2_1_;
            }
            else
            {
                throw new JsonParseException("Invalid rotation " + lvt_2_1_ + " found, only 0/90/180/270 allowed");
            }
        }

        private float[] parseUV(JsonObject p_178292_1_)
        {
            if (!p_178292_1_.has("uv"))
            {
                return null;
            }
            else
            {
                JsonArray lvt_2_1_ = JsonUtils.getJsonArray(p_178292_1_, "uv");

                if (lvt_2_1_.size() != 4)
                {
                    throw new JsonParseException("Expected 4 uv values, found: " + lvt_2_1_.size());
                }
                else
                {
                    float[] lvt_3_1_ = new float[4];

                    for (int lvt_4_1_ = 0; lvt_4_1_ < lvt_3_1_.length; ++lvt_4_1_)
                    {
                        lvt_3_1_[lvt_4_1_] = JsonUtils.getFloat(lvt_2_1_.get(lvt_4_1_), "uv[" + lvt_4_1_ + "]");
                    }

                    return lvt_3_1_;
                }
            }
        }

        public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
        }
    }
}
