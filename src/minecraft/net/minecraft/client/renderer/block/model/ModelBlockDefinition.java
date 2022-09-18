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
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class ModelBlockDefinition
{
    static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(ModelBlockDefinition.class, new ModelBlockDefinition.Deserializer()).registerTypeAdapter(ModelBlockDefinition.Variant.class, new ModelBlockDefinition.Variant.Deserializer()).create();
    private final Map<String, ModelBlockDefinition.Variants> mapVariants = Maps.newHashMap();

    public static ModelBlockDefinition parseFromReader(Reader p_178331_0_)
    {
        return (ModelBlockDefinition)GSON.fromJson(p_178331_0_, ModelBlockDefinition.class);
    }

    public ModelBlockDefinition(Collection<ModelBlockDefinition.Variants> p_i46221_1_)
    {
        for (ModelBlockDefinition.Variants lvt_3_1_ : p_i46221_1_)
        {
            this.mapVariants.put(lvt_3_1_.name, lvt_3_1_);
        }
    }

    public ModelBlockDefinition(List<ModelBlockDefinition> p_i46222_1_)
    {
        for (ModelBlockDefinition lvt_3_1_ : p_i46222_1_)
        {
            this.mapVariants.putAll(lvt_3_1_.mapVariants);
        }
    }

    public ModelBlockDefinition.Variants getVariants(String p_178330_1_)
    {
        ModelBlockDefinition.Variants lvt_2_1_ = (ModelBlockDefinition.Variants)this.mapVariants.get(p_178330_1_);

        if (lvt_2_1_ == null)
        {
            throw new ModelBlockDefinition.MissingVariantException();
        }
        else
        {
            return lvt_2_1_;
        }
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (p_equals_1_ instanceof ModelBlockDefinition)
        {
            ModelBlockDefinition lvt_2_1_ = (ModelBlockDefinition)p_equals_1_;
            return this.mapVariants.equals(lvt_2_1_.mapVariants);
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return this.mapVariants.hashCode();
    }

    public static class Deserializer implements JsonDeserializer<ModelBlockDefinition>
    {
        public ModelBlockDefinition deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            JsonObject lvt_4_1_ = p_deserialize_1_.getAsJsonObject();
            List<ModelBlockDefinition.Variants> lvt_5_1_ = this.parseVariantsList(p_deserialize_3_, lvt_4_1_);
            return new ModelBlockDefinition(lvt_5_1_);
        }

        protected List<ModelBlockDefinition.Variants> parseVariantsList(JsonDeserializationContext p_178334_1_, JsonObject p_178334_2_)
        {
            JsonObject lvt_3_1_ = JsonUtils.getJsonObject(p_178334_2_, "variants");
            List<ModelBlockDefinition.Variants> lvt_4_1_ = Lists.newArrayList();

            for (Entry<String, JsonElement> lvt_6_1_ : lvt_3_1_.entrySet())
            {
                lvt_4_1_.add(this.parseVariants(p_178334_1_, lvt_6_1_));
            }

            return lvt_4_1_;
        }

        protected ModelBlockDefinition.Variants parseVariants(JsonDeserializationContext p_178335_1_, Entry<String, JsonElement> p_178335_2_)
        {
            String lvt_3_1_ = (String)p_178335_2_.getKey();
            List<ModelBlockDefinition.Variant> lvt_4_1_ = Lists.newArrayList();
            JsonElement lvt_5_1_ = (JsonElement)p_178335_2_.getValue();

            if (lvt_5_1_.isJsonArray())
            {
                for (JsonElement lvt_7_1_ : lvt_5_1_.getAsJsonArray())
                {
                    lvt_4_1_.add((ModelBlockDefinition.Variant)p_178335_1_.deserialize(lvt_7_1_, ModelBlockDefinition.Variant.class));
                }
            }
            else
            {
                lvt_4_1_.add((ModelBlockDefinition.Variant)p_178335_1_.deserialize(lvt_5_1_, ModelBlockDefinition.Variant.class));
            }

            return new ModelBlockDefinition.Variants(lvt_3_1_, lvt_4_1_);
        }

        public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
        }
    }

    public class MissingVariantException extends RuntimeException
    {
    }

    public static class Variant
    {
        private final ResourceLocation modelLocation;
        private final ModelRotation modelRotation;
        private final boolean uvLock;
        private final int weight;

        public Variant(ResourceLocation modelLocationIn, ModelRotation modelRotationIn, boolean uvLockIn, int weightIn)
        {
            this.modelLocation = modelLocationIn;
            this.modelRotation = modelRotationIn;
            this.uvLock = uvLockIn;
            this.weight = weightIn;
        }

        public ResourceLocation getModelLocation()
        {
            return this.modelLocation;
        }

        public ModelRotation getRotation()
        {
            return this.modelRotation;
        }

        public boolean isUvLocked()
        {
            return this.uvLock;
        }

        public int getWeight()
        {
            return this.weight;
        }

        public boolean equals(Object p_equals_1_)
        {
            if (this == p_equals_1_)
            {
                return true;
            }
            else if (!(p_equals_1_ instanceof ModelBlockDefinition.Variant))
            {
                return false;
            }
            else
            {
                ModelBlockDefinition.Variant lvt_2_1_ = (ModelBlockDefinition.Variant)p_equals_1_;
                return this.modelLocation.equals(lvt_2_1_.modelLocation) && this.modelRotation == lvt_2_1_.modelRotation && this.uvLock == lvt_2_1_.uvLock;
            }
        }

        public int hashCode()
        {
            int lvt_1_1_ = this.modelLocation.hashCode();
            lvt_1_1_ = 31 * lvt_1_1_ + (this.modelRotation != null ? this.modelRotation.hashCode() : 0);
            lvt_1_1_ = 31 * lvt_1_1_ + (this.uvLock ? 1 : 0);
            return lvt_1_1_;
        }

        public static class Deserializer implements JsonDeserializer<ModelBlockDefinition.Variant>
        {
            public ModelBlockDefinition.Variant deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
            {
                JsonObject lvt_4_1_ = p_deserialize_1_.getAsJsonObject();
                String lvt_5_1_ = this.parseModel(lvt_4_1_);
                ModelRotation lvt_6_1_ = this.parseRotation(lvt_4_1_);
                boolean lvt_7_1_ = this.parseUvLock(lvt_4_1_);
                int lvt_8_1_ = this.parseWeight(lvt_4_1_);
                return new ModelBlockDefinition.Variant(this.makeModelLocation(lvt_5_1_), lvt_6_1_, lvt_7_1_, lvt_8_1_);
            }

            private ResourceLocation makeModelLocation(String p_178426_1_)
            {
                ResourceLocation lvt_2_1_ = new ResourceLocation(p_178426_1_);
                lvt_2_1_ = new ResourceLocation(lvt_2_1_.getResourceDomain(), "block/" + lvt_2_1_.getResourcePath());
                return lvt_2_1_;
            }

            private boolean parseUvLock(JsonObject p_178429_1_)
            {
                return JsonUtils.getBoolean(p_178429_1_, "uvlock", false);
            }

            protected ModelRotation parseRotation(JsonObject p_178428_1_)
            {
                int lvt_2_1_ = JsonUtils.getInt(p_178428_1_, "x", 0);
                int lvt_3_1_ = JsonUtils.getInt(p_178428_1_, "y", 0);
                ModelRotation lvt_4_1_ = ModelRotation.getModelRotation(lvt_2_1_, lvt_3_1_);

                if (lvt_4_1_ == null)
                {
                    throw new JsonParseException("Invalid BlockModelRotation x: " + lvt_2_1_ + ", y: " + lvt_3_1_);
                }
                else
                {
                    return lvt_4_1_;
                }
            }

            protected String parseModel(JsonObject p_178424_1_)
            {
                return JsonUtils.getString(p_178424_1_, "model");
            }

            protected int parseWeight(JsonObject p_178427_1_)
            {
                return JsonUtils.getInt(p_178427_1_, "weight", 1);
            }

            public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
            {
                return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
            }
        }
    }

    public static class Variants
    {
        private final String name;
        private final List<ModelBlockDefinition.Variant> listVariants;

        public Variants(String nameIn, List<ModelBlockDefinition.Variant> listVariantsIn)
        {
            this.name = nameIn;
            this.listVariants = listVariantsIn;
        }

        public List<ModelBlockDefinition.Variant> getVariants()
        {
            return this.listVariants;
        }

        public boolean equals(Object p_equals_1_)
        {
            if (this == p_equals_1_)
            {
                return true;
            }
            else if (!(p_equals_1_ instanceof ModelBlockDefinition.Variants))
            {
                return false;
            }
            else
            {
                ModelBlockDefinition.Variants lvt_2_1_ = (ModelBlockDefinition.Variants)p_equals_1_;
                return !this.name.equals(lvt_2_1_.name) ? false : this.listVariants.equals(lvt_2_1_.listVariants);
            }
        }

        public int hashCode()
        {
            int lvt_1_1_ = this.name.hashCode();
            lvt_1_1_ = 31 * lvt_1_1_ + this.listVariants.hashCode();
            return lvt_1_1_;
        }
    }
}
