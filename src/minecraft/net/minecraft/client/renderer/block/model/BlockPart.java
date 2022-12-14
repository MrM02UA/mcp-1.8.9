package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.MathHelper;
import org.lwjgl.util.vector.Vector3f;

public class BlockPart
{
    public final Vector3f positionFrom;
    public final Vector3f positionTo;
    public final Map<EnumFacing, BlockPartFace> mapFaces;
    public final BlockPartRotation partRotation;
    public final boolean shade;

    public BlockPart(Vector3f positionFromIn, Vector3f positionToIn, Map<EnumFacing, BlockPartFace> mapFacesIn, BlockPartRotation partRotationIn, boolean shadeIn)
    {
        this.positionFrom = positionFromIn;
        this.positionTo = positionToIn;
        this.mapFaces = mapFacesIn;
        this.partRotation = partRotationIn;
        this.shade = shadeIn;
        this.setDefaultUvs();
    }

    private void setDefaultUvs()
    {
        for (Entry<EnumFacing, BlockPartFace> lvt_2_1_ : this.mapFaces.entrySet())
        {
            float[] lvt_3_1_ = this.getFaceUvs((EnumFacing)lvt_2_1_.getKey());
            ((BlockPartFace)lvt_2_1_.getValue()).blockFaceUV.setUvs(lvt_3_1_);
        }
    }

    private float[] getFaceUvs(EnumFacing p_178236_1_)
    {
        float[] lvt_2_1_;

        switch (p_178236_1_)
        {
            case DOWN:
            case UP:
                lvt_2_1_ = new float[] {this.positionFrom.x, this.positionFrom.z, this.positionTo.x, this.positionTo.z};
                break;
            case NORTH:
            case SOUTH:
                lvt_2_1_ = new float[] {this.positionFrom.x, 16.0F - this.positionTo.y, this.positionTo.x, 16.0F - this.positionFrom.y};
                break;
            case WEST:
            case EAST:
                lvt_2_1_ = new float[] {this.positionFrom.z, 16.0F - this.positionTo.y, this.positionTo.z, 16.0F - this.positionFrom.y};
                break;
            default:
                throw new NullPointerException();
        }

        return lvt_2_1_;
    }

    static class Deserializer implements JsonDeserializer<BlockPart>
    {
        public BlockPart deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            JsonObject lvt_4_1_ = p_deserialize_1_.getAsJsonObject();
            Vector3f lvt_5_1_ = this.parsePositionFrom(lvt_4_1_);
            Vector3f lvt_6_1_ = this.parsePositionTo(lvt_4_1_);
            BlockPartRotation lvt_7_1_ = this.parseRotation(lvt_4_1_);
            Map<EnumFacing, BlockPartFace> lvt_8_1_ = this.parseFacesCheck(p_deserialize_3_, lvt_4_1_);

            if (lvt_4_1_.has("shade") && !JsonUtils.isBoolean(lvt_4_1_, "shade"))
            {
                throw new JsonParseException("Expected shade to be a Boolean");
            }
            else
            {
                boolean lvt_9_1_ = JsonUtils.getBoolean(lvt_4_1_, "shade", true);
                return new BlockPart(lvt_5_1_, lvt_6_1_, lvt_8_1_, lvt_7_1_, lvt_9_1_);
            }
        }

        private BlockPartRotation parseRotation(JsonObject p_178256_1_)
        {
            BlockPartRotation lvt_2_1_ = null;

            if (p_178256_1_.has("rotation"))
            {
                JsonObject lvt_3_1_ = JsonUtils.getJsonObject(p_178256_1_, "rotation");
                Vector3f lvt_4_1_ = this.parsePosition(lvt_3_1_, "origin");
                lvt_4_1_.scale(0.0625F);
                EnumFacing.Axis lvt_5_1_ = this.parseAxis(lvt_3_1_);
                float lvt_6_1_ = this.parseAngle(lvt_3_1_);
                boolean lvt_7_1_ = JsonUtils.getBoolean(lvt_3_1_, "rescale", false);
                lvt_2_1_ = new BlockPartRotation(lvt_4_1_, lvt_5_1_, lvt_6_1_, lvt_7_1_);
            }

            return lvt_2_1_;
        }

        private float parseAngle(JsonObject p_178255_1_)
        {
            float lvt_2_1_ = JsonUtils.getFloat(p_178255_1_, "angle");

            if (lvt_2_1_ != 0.0F && MathHelper.abs(lvt_2_1_) != 22.5F && MathHelper.abs(lvt_2_1_) != 45.0F)
            {
                throw new JsonParseException("Invalid rotation " + lvt_2_1_ + " found, only -45/-22.5/0/22.5/45 allowed");
            }
            else
            {
                return lvt_2_1_;
            }
        }

        private EnumFacing.Axis parseAxis(JsonObject p_178252_1_)
        {
            String lvt_2_1_ = JsonUtils.getString(p_178252_1_, "axis");
            EnumFacing.Axis lvt_3_1_ = EnumFacing.Axis.byName(lvt_2_1_.toLowerCase());

            if (lvt_3_1_ == null)
            {
                throw new JsonParseException("Invalid rotation axis: " + lvt_2_1_);
            }
            else
            {
                return lvt_3_1_;
            }
        }

        private Map<EnumFacing, BlockPartFace> parseFacesCheck(JsonDeserializationContext p_178250_1_, JsonObject p_178250_2_)
        {
            Map<EnumFacing, BlockPartFace> lvt_3_1_ = this.parseFaces(p_178250_1_, p_178250_2_);

            if (lvt_3_1_.isEmpty())
            {
                throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
            }
            else
            {
                return lvt_3_1_;
            }
        }

        private Map<EnumFacing, BlockPartFace> parseFaces(JsonDeserializationContext p_178253_1_, JsonObject p_178253_2_)
        {
            Map<EnumFacing, BlockPartFace> lvt_3_1_ = Maps.newEnumMap(EnumFacing.class);
            JsonObject lvt_4_1_ = JsonUtils.getJsonObject(p_178253_2_, "faces");

            for (Entry<String, JsonElement> lvt_6_1_ : lvt_4_1_.entrySet())
            {
                EnumFacing lvt_7_1_ = this.parseEnumFacing((String)lvt_6_1_.getKey());
                lvt_3_1_.put(lvt_7_1_, (BlockPartFace)p_178253_1_.deserialize((JsonElement)lvt_6_1_.getValue(), BlockPartFace.class));
            }

            return lvt_3_1_;
        }

        private EnumFacing parseEnumFacing(String name)
        {
            EnumFacing lvt_2_1_ = EnumFacing.byName(name);

            if (lvt_2_1_ == null)
            {
                throw new JsonParseException("Unknown facing: " + name);
            }
            else
            {
                return lvt_2_1_;
            }
        }

        private Vector3f parsePositionTo(JsonObject p_178247_1_)
        {
            Vector3f lvt_2_1_ = this.parsePosition(p_178247_1_, "to");

            if (lvt_2_1_.x >= -16.0F && lvt_2_1_.y >= -16.0F && lvt_2_1_.z >= -16.0F && lvt_2_1_.x <= 32.0F && lvt_2_1_.y <= 32.0F && lvt_2_1_.z <= 32.0F)
            {
                return lvt_2_1_;
            }
            else
            {
                throw new JsonParseException("\'to\' specifier exceeds the allowed boundaries: " + lvt_2_1_);
            }
        }

        private Vector3f parsePositionFrom(JsonObject p_178249_1_)
        {
            Vector3f lvt_2_1_ = this.parsePosition(p_178249_1_, "from");

            if (lvt_2_1_.x >= -16.0F && lvt_2_1_.y >= -16.0F && lvt_2_1_.z >= -16.0F && lvt_2_1_.x <= 32.0F && lvt_2_1_.y <= 32.0F && lvt_2_1_.z <= 32.0F)
            {
                return lvt_2_1_;
            }
            else
            {
                throw new JsonParseException("\'from\' specifier exceeds the allowed boundaries: " + lvt_2_1_);
            }
        }

        private Vector3f parsePosition(JsonObject p_178251_1_, String p_178251_2_)
        {
            JsonArray lvt_3_1_ = JsonUtils.getJsonArray(p_178251_1_, p_178251_2_);

            if (lvt_3_1_.size() != 3)
            {
                throw new JsonParseException("Expected 3 " + p_178251_2_ + " values, found: " + lvt_3_1_.size());
            }
            else
            {
                float[] lvt_4_1_ = new float[3];

                for (int lvt_5_1_ = 0; lvt_5_1_ < lvt_4_1_.length; ++lvt_5_1_)
                {
                    lvt_4_1_[lvt_5_1_] = JsonUtils.getFloat(lvt_3_1_.get(lvt_5_1_), p_178251_2_ + "[" + lvt_5_1_ + "]");
                }

                return new Vector3f(lvt_4_1_[0], lvt_4_1_[1], lvt_4_1_[2]);
            }
        }

        public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
        }
    }
}
