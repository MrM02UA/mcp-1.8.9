package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;

public class BlockPartFace
{
    public static final EnumFacing FACING_DEFAULT = null;
    public final EnumFacing cullFace;
    public final int tintIndex;
    public final String texture;
    public final BlockFaceUV blockFaceUV;

    public BlockPartFace(EnumFacing cullFaceIn, int tintIndexIn, String textureIn, BlockFaceUV blockFaceUVIn)
    {
        this.cullFace = cullFaceIn;
        this.tintIndex = tintIndexIn;
        this.texture = textureIn;
        this.blockFaceUV = blockFaceUVIn;
    }

    static class Deserializer implements JsonDeserializer<BlockPartFace>
    {
        public BlockPartFace deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            JsonObject lvt_4_1_ = p_deserialize_1_.getAsJsonObject();
            EnumFacing lvt_5_1_ = this.parseCullFace(lvt_4_1_);
            int lvt_6_1_ = this.parseTintIndex(lvt_4_1_);
            String lvt_7_1_ = this.parseTexture(lvt_4_1_);
            BlockFaceUV lvt_8_1_ = (BlockFaceUV)p_deserialize_3_.deserialize(lvt_4_1_, BlockFaceUV.class);
            return new BlockPartFace(lvt_5_1_, lvt_6_1_, lvt_7_1_, lvt_8_1_);
        }

        protected int parseTintIndex(JsonObject p_178337_1_)
        {
            return JsonUtils.getInt(p_178337_1_, "tintindex", -1);
        }

        private String parseTexture(JsonObject p_178340_1_)
        {
            return JsonUtils.getString(p_178340_1_, "texture");
        }

        private EnumFacing parseCullFace(JsonObject p_178339_1_)
        {
            String lvt_2_1_ = JsonUtils.getString(p_178339_1_, "cullface", "");
            return EnumFacing.byName(lvt_2_1_);
        }

        public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
        }
    }
}
