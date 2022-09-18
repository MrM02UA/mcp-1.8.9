package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModelBlock
{
    private static final Logger LOGGER = LogManager.getLogger();
    static final Gson SERIALIZER = (new GsonBuilder()).registerTypeAdapter(ModelBlock.class, new ModelBlock.Deserializer()).registerTypeAdapter(BlockPart.class, new BlockPart.Deserializer()).registerTypeAdapter(BlockPartFace.class, new BlockPartFace.Deserializer()).registerTypeAdapter(BlockFaceUV.class, new BlockFaceUV.Deserializer()).registerTypeAdapter(ItemTransformVec3f.class, new ItemTransformVec3f.Deserializer()).registerTypeAdapter(ItemCameraTransforms.class, new ItemCameraTransforms.Deserializer()).create();
    private final List<BlockPart> elements;
    private final boolean gui3d;
    private final boolean ambientOcclusion;
    private ItemCameraTransforms cameraTransforms;
    public String name;
    protected final Map<String, String> textures;
    protected ModelBlock parent;
    protected ResourceLocation parentLocation;

    public static ModelBlock deserialize(Reader readerIn)
    {
        return (ModelBlock)SERIALIZER.fromJson(readerIn, ModelBlock.class);
    }

    public static ModelBlock deserialize(String jsonString)
    {
        return deserialize(new StringReader(jsonString));
    }

    protected ModelBlock(List<BlockPart> elementsIn, Map<String, String> texturesIn, boolean ambientOcclusionIn, boolean gui3dIn, ItemCameraTransforms cameraTransformsIn)
    {
        this((ResourceLocation)null, elementsIn, texturesIn, ambientOcclusionIn, gui3dIn, cameraTransformsIn);
    }

    protected ModelBlock(ResourceLocation parentLocationIn, Map<String, String> texturesIn, boolean ambientOcclusionIn, boolean gui3dIn, ItemCameraTransforms cameraTransformsIn)
    {
        this(parentLocationIn, Collections.emptyList(), texturesIn, ambientOcclusionIn, gui3dIn, cameraTransformsIn);
    }

    private ModelBlock(ResourceLocation parentLocationIn, List<BlockPart> elementsIn, Map<String, String> texturesIn, boolean ambientOcclusionIn, boolean gui3dIn, ItemCameraTransforms cameraTransformsIn)
    {
        this.name = "";
        this.elements = elementsIn;
        this.ambientOcclusion = ambientOcclusionIn;
        this.gui3d = gui3dIn;
        this.textures = texturesIn;
        this.parentLocation = parentLocationIn;
        this.cameraTransforms = cameraTransformsIn;
    }

    public List<BlockPart> getElements()
    {
        return this.hasParent() ? this.parent.getElements() : this.elements;
    }

    private boolean hasParent()
    {
        return this.parent != null;
    }

    public boolean isAmbientOcclusion()
    {
        return this.hasParent() ? this.parent.isAmbientOcclusion() : this.ambientOcclusion;
    }

    public boolean isGui3d()
    {
        return this.gui3d;
    }

    public boolean isResolved()
    {
        return this.parentLocation == null || this.parent != null && this.parent.isResolved();
    }

    public void getParentFromMap(Map<ResourceLocation, ModelBlock> p_178299_1_)
    {
        if (this.parentLocation != null)
        {
            this.parent = (ModelBlock)p_178299_1_.get(this.parentLocation);
        }
    }

    public boolean isTexturePresent(String textureName)
    {
        return !"missingno".equals(this.resolveTextureName(textureName));
    }

    public String resolveTextureName(String textureName)
    {
        if (!this.startsWithHash(textureName))
        {
            textureName = '#' + textureName;
        }

        return this.resolveTextureName(textureName, new ModelBlock.Bookkeep(this));
    }

    private String resolveTextureName(String textureName, ModelBlock.Bookkeep p_178302_2_)
    {
        if (this.startsWithHash(textureName))
        {
            if (this == p_178302_2_.modelExt)
            {
                LOGGER.warn("Unable to resolve texture due to upward reference: " + textureName + " in " + this.name);
                return "missingno";
            }
            else
            {
                String lvt_3_1_ = (String)this.textures.get(textureName.substring(1));

                if (lvt_3_1_ == null && this.hasParent())
                {
                    lvt_3_1_ = this.parent.resolveTextureName(textureName, p_178302_2_);
                }

                p_178302_2_.modelExt = this;

                if (lvt_3_1_ != null && this.startsWithHash(lvt_3_1_))
                {
                    lvt_3_1_ = p_178302_2_.model.resolveTextureName(lvt_3_1_, p_178302_2_);
                }

                return lvt_3_1_ != null && !this.startsWithHash(lvt_3_1_) ? lvt_3_1_ : "missingno";
            }
        }
        else
        {
            return textureName;
        }
    }

    private boolean startsWithHash(String hash)
    {
        return hash.charAt(0) == 35;
    }

    public ResourceLocation getParentLocation()
    {
        return this.parentLocation;
    }

    public ModelBlock getRootModel()
    {
        return this.hasParent() ? this.parent.getRootModel() : this;
    }

    public ItemCameraTransforms getAllTransforms()
    {
        ItemTransformVec3f lvt_1_1_ = this.getTransform(ItemCameraTransforms.TransformType.THIRD_PERSON);
        ItemTransformVec3f lvt_2_1_ = this.getTransform(ItemCameraTransforms.TransformType.FIRST_PERSON);
        ItemTransformVec3f lvt_3_1_ = this.getTransform(ItemCameraTransforms.TransformType.HEAD);
        ItemTransformVec3f lvt_4_1_ = this.getTransform(ItemCameraTransforms.TransformType.GUI);
        ItemTransformVec3f lvt_5_1_ = this.getTransform(ItemCameraTransforms.TransformType.GROUND);
        ItemTransformVec3f lvt_6_1_ = this.getTransform(ItemCameraTransforms.TransformType.FIXED);
        return new ItemCameraTransforms(lvt_1_1_, lvt_2_1_, lvt_3_1_, lvt_4_1_, lvt_5_1_, lvt_6_1_);
    }

    private ItemTransformVec3f getTransform(ItemCameraTransforms.TransformType type)
    {
        return this.parent != null && !this.cameraTransforms.func_181687_c(type) ? this.parent.getTransform(type) : this.cameraTransforms.getTransform(type);
    }

    public static void checkModelHierarchy(Map<ResourceLocation, ModelBlock> p_178312_0_)
    {
        for (ModelBlock lvt_2_1_ : p_178312_0_.values())
        {
            try
            {
                ModelBlock lvt_3_1_ = lvt_2_1_.parent;

                for (ModelBlock lvt_4_1_ = lvt_3_1_.parent; lvt_3_1_ != lvt_4_1_; lvt_4_1_ = lvt_4_1_.parent.parent)
                {
                    lvt_3_1_ = lvt_3_1_.parent;
                }

                throw new ModelBlock.LoopException();
            }
            catch (NullPointerException var5)
            {
                ;
            }
        }
    }

    static final class Bookkeep
    {
        public final ModelBlock model;
        public ModelBlock modelExt;

        private Bookkeep(ModelBlock p_i46223_1_)
        {
            this.model = p_i46223_1_;
        }
    }

    public static class Deserializer implements JsonDeserializer<ModelBlock>
    {
        public ModelBlock deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            JsonObject lvt_4_1_ = p_deserialize_1_.getAsJsonObject();
            List<BlockPart> lvt_5_1_ = this.getModelElements(p_deserialize_3_, lvt_4_1_);
            String lvt_6_1_ = this.getParent(lvt_4_1_);
            boolean lvt_7_1_ = StringUtils.isEmpty(lvt_6_1_);
            boolean lvt_8_1_ = lvt_5_1_.isEmpty();

            if (lvt_8_1_ && lvt_7_1_)
            {
                throw new JsonParseException("BlockModel requires either elements or parent, found neither");
            }
            else if (!lvt_7_1_ && !lvt_8_1_)
            {
                throw new JsonParseException("BlockModel requires either elements or parent, found both");
            }
            else
            {
                Map<String, String> lvt_9_1_ = this.getTextures(lvt_4_1_);
                boolean lvt_10_1_ = this.getAmbientOcclusionEnabled(lvt_4_1_);
                ItemCameraTransforms lvt_11_1_ = ItemCameraTransforms.DEFAULT;

                if (lvt_4_1_.has("display"))
                {
                    JsonObject lvt_12_1_ = JsonUtils.getJsonObject(lvt_4_1_, "display");
                    lvt_11_1_ = (ItemCameraTransforms)p_deserialize_3_.deserialize(lvt_12_1_, ItemCameraTransforms.class);
                }

                return lvt_8_1_ ? new ModelBlock(new ResourceLocation(lvt_6_1_), lvt_9_1_, lvt_10_1_, true, lvt_11_1_) : new ModelBlock(lvt_5_1_, lvt_9_1_, lvt_10_1_, true, lvt_11_1_);
            }
        }

        private Map<String, String> getTextures(JsonObject p_178329_1_)
        {
            Map<String, String> lvt_2_1_ = Maps.newHashMap();

            if (p_178329_1_.has("textures"))
            {
                JsonObject lvt_3_1_ = p_178329_1_.getAsJsonObject("textures");

                for (Entry<String, JsonElement> lvt_5_1_ : lvt_3_1_.entrySet())
                {
                    lvt_2_1_.put(lvt_5_1_.getKey(), ((JsonElement)lvt_5_1_.getValue()).getAsString());
                }
            }

            return lvt_2_1_;
        }

        private String getParent(JsonObject p_178326_1_)
        {
            return JsonUtils.getString(p_178326_1_, "parent", "");
        }

        protected boolean getAmbientOcclusionEnabled(JsonObject p_178328_1_)
        {
            return JsonUtils.getBoolean(p_178328_1_, "ambientocclusion", true);
        }

        protected List<BlockPart> getModelElements(JsonDeserializationContext p_178325_1_, JsonObject p_178325_2_)
        {
            List<BlockPart> lvt_3_1_ = Lists.newArrayList();

            if (p_178325_2_.has("elements"))
            {
                for (JsonElement lvt_5_1_ : JsonUtils.getJsonArray(p_178325_2_, "elements"))
                {
                    lvt_3_1_.add((BlockPart)p_178325_1_.deserialize(lvt_5_1_, BlockPart.class));
                }
            }

            return lvt_3_1_;
        }

        public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
        }
    }

    public static class LoopException extends RuntimeException
    {
    }
}
