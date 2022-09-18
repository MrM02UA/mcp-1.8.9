package net.minecraft.world.gen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;
import net.minecraft.world.biome.BiomeGenBase;

public class ChunkProviderSettings
{
    public final float coordinateScale;
    public final float heightScale;
    public final float upperLimitScale;
    public final float lowerLimitScale;
    public final float depthNoiseScaleX;
    public final float depthNoiseScaleZ;
    public final float depthNoiseScaleExponent;
    public final float mainNoiseScaleX;
    public final float mainNoiseScaleY;
    public final float mainNoiseScaleZ;
    public final float baseSize;
    public final float stretchY;
    public final float biomeDepthWeight;
    public final float biomeDepthOffSet;
    public final float biomeScaleWeight;
    public final float biomeScaleOffset;
    public final int seaLevel;
    public final boolean useCaves;
    public final boolean useDungeons;
    public final int dungeonChance;
    public final boolean useStrongholds;
    public final boolean useVillages;
    public final boolean useMineShafts;
    public final boolean useTemples;
    public final boolean useMonuments;
    public final boolean useRavines;
    public final boolean useWaterLakes;
    public final int waterLakeChance;
    public final boolean useLavaLakes;
    public final int lavaLakeChance;
    public final boolean useLavaOceans;
    public final int fixedBiome;
    public final int biomeSize;
    public final int riverSize;
    public final int dirtSize;
    public final int dirtCount;
    public final int dirtMinHeight;
    public final int dirtMaxHeight;
    public final int gravelSize;
    public final int gravelCount;
    public final int gravelMinHeight;
    public final int gravelMaxHeight;
    public final int graniteSize;
    public final int graniteCount;
    public final int graniteMinHeight;
    public final int graniteMaxHeight;
    public final int dioriteSize;
    public final int dioriteCount;
    public final int dioriteMinHeight;
    public final int dioriteMaxHeight;
    public final int andesiteSize;
    public final int andesiteCount;
    public final int andesiteMinHeight;
    public final int andesiteMaxHeight;
    public final int coalSize;
    public final int coalCount;
    public final int coalMinHeight;
    public final int coalMaxHeight;
    public final int ironSize;
    public final int ironCount;
    public final int ironMinHeight;
    public final int ironMaxHeight;
    public final int goldSize;
    public final int goldCount;
    public final int goldMinHeight;
    public final int goldMaxHeight;
    public final int redstoneSize;
    public final int redstoneCount;
    public final int redstoneMinHeight;
    public final int redstoneMaxHeight;
    public final int diamondSize;
    public final int diamondCount;
    public final int diamondMinHeight;
    public final int diamondMaxHeight;
    public final int lapisSize;
    public final int lapisCount;
    public final int lapisCenterHeight;
    public final int lapisSpread;

    private ChunkProviderSettings(ChunkProviderSettings.Factory settingsFactory)
    {
        this.coordinateScale = settingsFactory.coordinateScale;
        this.heightScale = settingsFactory.heightScale;
        this.upperLimitScale = settingsFactory.upperLimitScale;
        this.lowerLimitScale = settingsFactory.lowerLimitScale;
        this.depthNoiseScaleX = settingsFactory.depthNoiseScaleX;
        this.depthNoiseScaleZ = settingsFactory.depthNoiseScaleZ;
        this.depthNoiseScaleExponent = settingsFactory.depthNoiseScaleExponent;
        this.mainNoiseScaleX = settingsFactory.mainNoiseScaleX;
        this.mainNoiseScaleY = settingsFactory.mainNoiseScaleY;
        this.mainNoiseScaleZ = settingsFactory.mainNoiseScaleZ;
        this.baseSize = settingsFactory.baseSize;
        this.stretchY = settingsFactory.stretchY;
        this.biomeDepthWeight = settingsFactory.biomeDepthWeight;
        this.biomeDepthOffSet = settingsFactory.biomeDepthOffset;
        this.biomeScaleWeight = settingsFactory.biomeScaleWeight;
        this.biomeScaleOffset = settingsFactory.biomeScaleOffset;
        this.seaLevel = settingsFactory.seaLevel;
        this.useCaves = settingsFactory.useCaves;
        this.useDungeons = settingsFactory.useDungeons;
        this.dungeonChance = settingsFactory.dungeonChance;
        this.useStrongholds = settingsFactory.useStrongholds;
        this.useVillages = settingsFactory.useVillages;
        this.useMineShafts = settingsFactory.useMineShafts;
        this.useTemples = settingsFactory.useTemples;
        this.useMonuments = settingsFactory.useMonuments;
        this.useRavines = settingsFactory.useRavines;
        this.useWaterLakes = settingsFactory.useWaterLakes;
        this.waterLakeChance = settingsFactory.waterLakeChance;
        this.useLavaLakes = settingsFactory.useLavaLakes;
        this.lavaLakeChance = settingsFactory.lavaLakeChance;
        this.useLavaOceans = settingsFactory.useLavaOceans;
        this.fixedBiome = settingsFactory.fixedBiome;
        this.biomeSize = settingsFactory.biomeSize;
        this.riverSize = settingsFactory.riverSize;
        this.dirtSize = settingsFactory.dirtSize;
        this.dirtCount = settingsFactory.dirtCount;
        this.dirtMinHeight = settingsFactory.dirtMinHeight;
        this.dirtMaxHeight = settingsFactory.dirtMaxHeight;
        this.gravelSize = settingsFactory.gravelSize;
        this.gravelCount = settingsFactory.gravelCount;
        this.gravelMinHeight = settingsFactory.gravelMinHeight;
        this.gravelMaxHeight = settingsFactory.gravelMaxHeight;
        this.graniteSize = settingsFactory.graniteSize;
        this.graniteCount = settingsFactory.graniteCount;
        this.graniteMinHeight = settingsFactory.graniteMinHeight;
        this.graniteMaxHeight = settingsFactory.graniteMaxHeight;
        this.dioriteSize = settingsFactory.dioriteSize;
        this.dioriteCount = settingsFactory.dioriteCount;
        this.dioriteMinHeight = settingsFactory.dioriteMinHeight;
        this.dioriteMaxHeight = settingsFactory.dioriteMaxHeight;
        this.andesiteSize = settingsFactory.andesiteSize;
        this.andesiteCount = settingsFactory.andesiteCount;
        this.andesiteMinHeight = settingsFactory.andesiteMinHeight;
        this.andesiteMaxHeight = settingsFactory.andesiteMaxHeight;
        this.coalSize = settingsFactory.coalSize;
        this.coalCount = settingsFactory.coalCount;
        this.coalMinHeight = settingsFactory.coalMinHeight;
        this.coalMaxHeight = settingsFactory.coalMaxHeight;
        this.ironSize = settingsFactory.ironSize;
        this.ironCount = settingsFactory.ironCount;
        this.ironMinHeight = settingsFactory.ironMinHeight;
        this.ironMaxHeight = settingsFactory.ironMaxHeight;
        this.goldSize = settingsFactory.goldSize;
        this.goldCount = settingsFactory.goldCount;
        this.goldMinHeight = settingsFactory.goldMinHeight;
        this.goldMaxHeight = settingsFactory.goldMaxHeight;
        this.redstoneSize = settingsFactory.redstoneSize;
        this.redstoneCount = settingsFactory.redstoneCount;
        this.redstoneMinHeight = settingsFactory.redstoneMinHeight;
        this.redstoneMaxHeight = settingsFactory.redstoneMaxHeight;
        this.diamondSize = settingsFactory.diamondSize;
        this.diamondCount = settingsFactory.diamondCount;
        this.diamondMinHeight = settingsFactory.diamondMinHeight;
        this.diamondMaxHeight = settingsFactory.diamondMaxHeight;
        this.lapisSize = settingsFactory.lapisSize;
        this.lapisCount = settingsFactory.lapisCount;
        this.lapisCenterHeight = settingsFactory.lapisCenterHeight;
        this.lapisSpread = settingsFactory.lapisSpread;
    }

    public static class Factory
    {
        static final Gson JSON_ADAPTER = (new GsonBuilder()).registerTypeAdapter(ChunkProviderSettings.Factory.class, new ChunkProviderSettings.Serializer()).create();
        public float coordinateScale = 684.412F;
        public float heightScale = 684.412F;
        public float upperLimitScale = 512.0F;
        public float lowerLimitScale = 512.0F;
        public float depthNoiseScaleX = 200.0F;
        public float depthNoiseScaleZ = 200.0F;
        public float depthNoiseScaleExponent = 0.5F;
        public float mainNoiseScaleX = 80.0F;
        public float mainNoiseScaleY = 160.0F;
        public float mainNoiseScaleZ = 80.0F;
        public float baseSize = 8.5F;
        public float stretchY = 12.0F;
        public float biomeDepthWeight = 1.0F;
        public float biomeDepthOffset = 0.0F;
        public float biomeScaleWeight = 1.0F;
        public float biomeScaleOffset = 0.0F;
        public int seaLevel = 63;
        public boolean useCaves = true;
        public boolean useDungeons = true;
        public int dungeonChance = 8;
        public boolean useStrongholds = true;
        public boolean useVillages = true;
        public boolean useMineShafts = true;
        public boolean useTemples = true;
        public boolean useMonuments = true;
        public boolean useRavines = true;
        public boolean useWaterLakes = true;
        public int waterLakeChance = 4;
        public boolean useLavaLakes = true;
        public int lavaLakeChance = 80;
        public boolean useLavaOceans = false;
        public int fixedBiome = -1;
        public int biomeSize = 4;
        public int riverSize = 4;
        public int dirtSize = 33;
        public int dirtCount = 10;
        public int dirtMinHeight = 0;
        public int dirtMaxHeight = 256;
        public int gravelSize = 33;
        public int gravelCount = 8;
        public int gravelMinHeight = 0;
        public int gravelMaxHeight = 256;
        public int graniteSize = 33;
        public int graniteCount = 10;
        public int graniteMinHeight = 0;
        public int graniteMaxHeight = 80;
        public int dioriteSize = 33;
        public int dioriteCount = 10;
        public int dioriteMinHeight = 0;
        public int dioriteMaxHeight = 80;
        public int andesiteSize = 33;
        public int andesiteCount = 10;
        public int andesiteMinHeight = 0;
        public int andesiteMaxHeight = 80;
        public int coalSize = 17;
        public int coalCount = 20;
        public int coalMinHeight = 0;
        public int coalMaxHeight = 128;
        public int ironSize = 9;
        public int ironCount = 20;
        public int ironMinHeight = 0;
        public int ironMaxHeight = 64;
        public int goldSize = 9;
        public int goldCount = 2;
        public int goldMinHeight = 0;
        public int goldMaxHeight = 32;
        public int redstoneSize = 8;
        public int redstoneCount = 8;
        public int redstoneMinHeight = 0;
        public int redstoneMaxHeight = 16;
        public int diamondSize = 8;
        public int diamondCount = 1;
        public int diamondMinHeight = 0;
        public int diamondMaxHeight = 16;
        public int lapisSize = 7;
        public int lapisCount = 1;
        public int lapisCenterHeight = 16;
        public int lapisSpread = 16;

        public static ChunkProviderSettings.Factory jsonToFactory(String p_177865_0_)
        {
            if (p_177865_0_.length() == 0)
            {
                return new ChunkProviderSettings.Factory();
            }
            else
            {
                try
                {
                    return (ChunkProviderSettings.Factory)JSON_ADAPTER.fromJson(p_177865_0_, ChunkProviderSettings.Factory.class);
                }
                catch (Exception var2)
                {
                    return new ChunkProviderSettings.Factory();
                }
            }
        }

        public String toString()
        {
            return JSON_ADAPTER.toJson(this);
        }

        public Factory()
        {
            this.func_177863_a();
        }

        public void func_177863_a()
        {
            this.coordinateScale = 684.412F;
            this.heightScale = 684.412F;
            this.upperLimitScale = 512.0F;
            this.lowerLimitScale = 512.0F;
            this.depthNoiseScaleX = 200.0F;
            this.depthNoiseScaleZ = 200.0F;
            this.depthNoiseScaleExponent = 0.5F;
            this.mainNoiseScaleX = 80.0F;
            this.mainNoiseScaleY = 160.0F;
            this.mainNoiseScaleZ = 80.0F;
            this.baseSize = 8.5F;
            this.stretchY = 12.0F;
            this.biomeDepthWeight = 1.0F;
            this.biomeDepthOffset = 0.0F;
            this.biomeScaleWeight = 1.0F;
            this.biomeScaleOffset = 0.0F;
            this.seaLevel = 63;
            this.useCaves = true;
            this.useDungeons = true;
            this.dungeonChance = 8;
            this.useStrongholds = true;
            this.useVillages = true;
            this.useMineShafts = true;
            this.useTemples = true;
            this.useMonuments = true;
            this.useRavines = true;
            this.useWaterLakes = true;
            this.waterLakeChance = 4;
            this.useLavaLakes = true;
            this.lavaLakeChance = 80;
            this.useLavaOceans = false;
            this.fixedBiome = -1;
            this.biomeSize = 4;
            this.riverSize = 4;
            this.dirtSize = 33;
            this.dirtCount = 10;
            this.dirtMinHeight = 0;
            this.dirtMaxHeight = 256;
            this.gravelSize = 33;
            this.gravelCount = 8;
            this.gravelMinHeight = 0;
            this.gravelMaxHeight = 256;
            this.graniteSize = 33;
            this.graniteCount = 10;
            this.graniteMinHeight = 0;
            this.graniteMaxHeight = 80;
            this.dioriteSize = 33;
            this.dioriteCount = 10;
            this.dioriteMinHeight = 0;
            this.dioriteMaxHeight = 80;
            this.andesiteSize = 33;
            this.andesiteCount = 10;
            this.andesiteMinHeight = 0;
            this.andesiteMaxHeight = 80;
            this.coalSize = 17;
            this.coalCount = 20;
            this.coalMinHeight = 0;
            this.coalMaxHeight = 128;
            this.ironSize = 9;
            this.ironCount = 20;
            this.ironMinHeight = 0;
            this.ironMaxHeight = 64;
            this.goldSize = 9;
            this.goldCount = 2;
            this.goldMinHeight = 0;
            this.goldMaxHeight = 32;
            this.redstoneSize = 8;
            this.redstoneCount = 8;
            this.redstoneMinHeight = 0;
            this.redstoneMaxHeight = 16;
            this.diamondSize = 8;
            this.diamondCount = 1;
            this.diamondMinHeight = 0;
            this.diamondMaxHeight = 16;
            this.lapisSize = 7;
            this.lapisCount = 1;
            this.lapisCenterHeight = 16;
            this.lapisSpread = 16;
        }

        public boolean equals(Object p_equals_1_)
        {
            if (this == p_equals_1_)
            {
                return true;
            }
            else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass())
            {
                ChunkProviderSettings.Factory lvt_2_1_ = (ChunkProviderSettings.Factory)p_equals_1_;
                return this.andesiteCount != lvt_2_1_.andesiteCount ? false : (this.andesiteMaxHeight != lvt_2_1_.andesiteMaxHeight ? false : (this.andesiteMinHeight != lvt_2_1_.andesiteMinHeight ? false : (this.andesiteSize != lvt_2_1_.andesiteSize ? false : (Float.compare(lvt_2_1_.baseSize, this.baseSize) != 0 ? false : (Float.compare(lvt_2_1_.biomeDepthOffset, this.biomeDepthOffset) != 0 ? false : (Float.compare(lvt_2_1_.biomeDepthWeight, this.biomeDepthWeight) != 0 ? false : (Float.compare(lvt_2_1_.biomeScaleOffset, this.biomeScaleOffset) != 0 ? false : (Float.compare(lvt_2_1_.biomeScaleWeight, this.biomeScaleWeight) != 0 ? false : (this.biomeSize != lvt_2_1_.biomeSize ? false : (this.coalCount != lvt_2_1_.coalCount ? false : (this.coalMaxHeight != lvt_2_1_.coalMaxHeight ? false : (this.coalMinHeight != lvt_2_1_.coalMinHeight ? false : (this.coalSize != lvt_2_1_.coalSize ? false : (Float.compare(lvt_2_1_.coordinateScale, this.coordinateScale) != 0 ? false : (Float.compare(lvt_2_1_.depthNoiseScaleExponent, this.depthNoiseScaleExponent) != 0 ? false : (Float.compare(lvt_2_1_.depthNoiseScaleX, this.depthNoiseScaleX) != 0 ? false : (Float.compare(lvt_2_1_.depthNoiseScaleZ, this.depthNoiseScaleZ) != 0 ? false : (this.diamondCount != lvt_2_1_.diamondCount ? false : (this.diamondMaxHeight != lvt_2_1_.diamondMaxHeight ? false : (this.diamondMinHeight != lvt_2_1_.diamondMinHeight ? false : (this.diamondSize != lvt_2_1_.diamondSize ? false : (this.dioriteCount != lvt_2_1_.dioriteCount ? false : (this.dioriteMaxHeight != lvt_2_1_.dioriteMaxHeight ? false : (this.dioriteMinHeight != lvt_2_1_.dioriteMinHeight ? false : (this.dioriteSize != lvt_2_1_.dioriteSize ? false : (this.dirtCount != lvt_2_1_.dirtCount ? false : (this.dirtMaxHeight != lvt_2_1_.dirtMaxHeight ? false : (this.dirtMinHeight != lvt_2_1_.dirtMinHeight ? false : (this.dirtSize != lvt_2_1_.dirtSize ? false : (this.dungeonChance != lvt_2_1_.dungeonChance ? false : (this.fixedBiome != lvt_2_1_.fixedBiome ? false : (this.goldCount != lvt_2_1_.goldCount ? false : (this.goldMaxHeight != lvt_2_1_.goldMaxHeight ? false : (this.goldMinHeight != lvt_2_1_.goldMinHeight ? false : (this.goldSize != lvt_2_1_.goldSize ? false : (this.graniteCount != lvt_2_1_.graniteCount ? false : (this.graniteMaxHeight != lvt_2_1_.graniteMaxHeight ? false : (this.graniteMinHeight != lvt_2_1_.graniteMinHeight ? false : (this.graniteSize != lvt_2_1_.graniteSize ? false : (this.gravelCount != lvt_2_1_.gravelCount ? false : (this.gravelMaxHeight != lvt_2_1_.gravelMaxHeight ? false : (this.gravelMinHeight != lvt_2_1_.gravelMinHeight ? false : (this.gravelSize != lvt_2_1_.gravelSize ? false : (Float.compare(lvt_2_1_.heightScale, this.heightScale) != 0 ? false : (this.ironCount != lvt_2_1_.ironCount ? false : (this.ironMaxHeight != lvt_2_1_.ironMaxHeight ? false : (this.ironMinHeight != lvt_2_1_.ironMinHeight ? false : (this.ironSize != lvt_2_1_.ironSize ? false : (this.lapisCenterHeight != lvt_2_1_.lapisCenterHeight ? false : (this.lapisCount != lvt_2_1_.lapisCount ? false : (this.lapisSize != lvt_2_1_.lapisSize ? false : (this.lapisSpread != lvt_2_1_.lapisSpread ? false : (this.lavaLakeChance != lvt_2_1_.lavaLakeChance ? false : (Float.compare(lvt_2_1_.lowerLimitScale, this.lowerLimitScale) != 0 ? false : (Float.compare(lvt_2_1_.mainNoiseScaleX, this.mainNoiseScaleX) != 0 ? false : (Float.compare(lvt_2_1_.mainNoiseScaleY, this.mainNoiseScaleY) != 0 ? false : (Float.compare(lvt_2_1_.mainNoiseScaleZ, this.mainNoiseScaleZ) != 0 ? false : (this.redstoneCount != lvt_2_1_.redstoneCount ? false : (this.redstoneMaxHeight != lvt_2_1_.redstoneMaxHeight ? false : (this.redstoneMinHeight != lvt_2_1_.redstoneMinHeight ? false : (this.redstoneSize != lvt_2_1_.redstoneSize ? false : (this.riverSize != lvt_2_1_.riverSize ? false : (this.seaLevel != lvt_2_1_.seaLevel ? false : (Float.compare(lvt_2_1_.stretchY, this.stretchY) != 0 ? false : (Float.compare(lvt_2_1_.upperLimitScale, this.upperLimitScale) != 0 ? false : (this.useCaves != lvt_2_1_.useCaves ? false : (this.useDungeons != lvt_2_1_.useDungeons ? false : (this.useLavaLakes != lvt_2_1_.useLavaLakes ? false : (this.useLavaOceans != lvt_2_1_.useLavaOceans ? false : (this.useMineShafts != lvt_2_1_.useMineShafts ? false : (this.useRavines != lvt_2_1_.useRavines ? false : (this.useStrongholds != lvt_2_1_.useStrongholds ? false : (this.useTemples != lvt_2_1_.useTemples ? false : (this.useMonuments != lvt_2_1_.useMonuments ? false : (this.useVillages != lvt_2_1_.useVillages ? false : (this.useWaterLakes != lvt_2_1_.useWaterLakes ? false : this.waterLakeChance == lvt_2_1_.waterLakeChance))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))));
            }
            else
            {
                return false;
            }
        }

        public int hashCode()
        {
            int lvt_1_1_ = this.coordinateScale != 0.0F ? Float.floatToIntBits(this.coordinateScale) : 0;
            lvt_1_1_ = 31 * lvt_1_1_ + (this.heightScale != 0.0F ? Float.floatToIntBits(this.heightScale) : 0);
            lvt_1_1_ = 31 * lvt_1_1_ + (this.upperLimitScale != 0.0F ? Float.floatToIntBits(this.upperLimitScale) : 0);
            lvt_1_1_ = 31 * lvt_1_1_ + (this.lowerLimitScale != 0.0F ? Float.floatToIntBits(this.lowerLimitScale) : 0);
            lvt_1_1_ = 31 * lvt_1_1_ + (this.depthNoiseScaleX != 0.0F ? Float.floatToIntBits(this.depthNoiseScaleX) : 0);
            lvt_1_1_ = 31 * lvt_1_1_ + (this.depthNoiseScaleZ != 0.0F ? Float.floatToIntBits(this.depthNoiseScaleZ) : 0);
            lvt_1_1_ = 31 * lvt_1_1_ + (this.depthNoiseScaleExponent != 0.0F ? Float.floatToIntBits(this.depthNoiseScaleExponent) : 0);
            lvt_1_1_ = 31 * lvt_1_1_ + (this.mainNoiseScaleX != 0.0F ? Float.floatToIntBits(this.mainNoiseScaleX) : 0);
            lvt_1_1_ = 31 * lvt_1_1_ + (this.mainNoiseScaleY != 0.0F ? Float.floatToIntBits(this.mainNoiseScaleY) : 0);
            lvt_1_1_ = 31 * lvt_1_1_ + (this.mainNoiseScaleZ != 0.0F ? Float.floatToIntBits(this.mainNoiseScaleZ) : 0);
            lvt_1_1_ = 31 * lvt_1_1_ + (this.baseSize != 0.0F ? Float.floatToIntBits(this.baseSize) : 0);
            lvt_1_1_ = 31 * lvt_1_1_ + (this.stretchY != 0.0F ? Float.floatToIntBits(this.stretchY) : 0);
            lvt_1_1_ = 31 * lvt_1_1_ + (this.biomeDepthWeight != 0.0F ? Float.floatToIntBits(this.biomeDepthWeight) : 0);
            lvt_1_1_ = 31 * lvt_1_1_ + (this.biomeDepthOffset != 0.0F ? Float.floatToIntBits(this.biomeDepthOffset) : 0);
            lvt_1_1_ = 31 * lvt_1_1_ + (this.biomeScaleWeight != 0.0F ? Float.floatToIntBits(this.biomeScaleWeight) : 0);
            lvt_1_1_ = 31 * lvt_1_1_ + (this.biomeScaleOffset != 0.0F ? Float.floatToIntBits(this.biomeScaleOffset) : 0);
            lvt_1_1_ = 31 * lvt_1_1_ + this.seaLevel;
            lvt_1_1_ = 31 * lvt_1_1_ + (this.useCaves ? 1 : 0);
            lvt_1_1_ = 31 * lvt_1_1_ + (this.useDungeons ? 1 : 0);
            lvt_1_1_ = 31 * lvt_1_1_ + this.dungeonChance;
            lvt_1_1_ = 31 * lvt_1_1_ + (this.useStrongholds ? 1 : 0);
            lvt_1_1_ = 31 * lvt_1_1_ + (this.useVillages ? 1 : 0);
            lvt_1_1_ = 31 * lvt_1_1_ + (this.useMineShafts ? 1 : 0);
            lvt_1_1_ = 31 * lvt_1_1_ + (this.useTemples ? 1 : 0);
            lvt_1_1_ = 31 * lvt_1_1_ + (this.useMonuments ? 1 : 0);
            lvt_1_1_ = 31 * lvt_1_1_ + (this.useRavines ? 1 : 0);
            lvt_1_1_ = 31 * lvt_1_1_ + (this.useWaterLakes ? 1 : 0);
            lvt_1_1_ = 31 * lvt_1_1_ + this.waterLakeChance;
            lvt_1_1_ = 31 * lvt_1_1_ + (this.useLavaLakes ? 1 : 0);
            lvt_1_1_ = 31 * lvt_1_1_ + this.lavaLakeChance;
            lvt_1_1_ = 31 * lvt_1_1_ + (this.useLavaOceans ? 1 : 0);
            lvt_1_1_ = 31 * lvt_1_1_ + this.fixedBiome;
            lvt_1_1_ = 31 * lvt_1_1_ + this.biomeSize;
            lvt_1_1_ = 31 * lvt_1_1_ + this.riverSize;
            lvt_1_1_ = 31 * lvt_1_1_ + this.dirtSize;
            lvt_1_1_ = 31 * lvt_1_1_ + this.dirtCount;
            lvt_1_1_ = 31 * lvt_1_1_ + this.dirtMinHeight;
            lvt_1_1_ = 31 * lvt_1_1_ + this.dirtMaxHeight;
            lvt_1_1_ = 31 * lvt_1_1_ + this.gravelSize;
            lvt_1_1_ = 31 * lvt_1_1_ + this.gravelCount;
            lvt_1_1_ = 31 * lvt_1_1_ + this.gravelMinHeight;
            lvt_1_1_ = 31 * lvt_1_1_ + this.gravelMaxHeight;
            lvt_1_1_ = 31 * lvt_1_1_ + this.graniteSize;
            lvt_1_1_ = 31 * lvt_1_1_ + this.graniteCount;
            lvt_1_1_ = 31 * lvt_1_1_ + this.graniteMinHeight;
            lvt_1_1_ = 31 * lvt_1_1_ + this.graniteMaxHeight;
            lvt_1_1_ = 31 * lvt_1_1_ + this.dioriteSize;
            lvt_1_1_ = 31 * lvt_1_1_ + this.dioriteCount;
            lvt_1_1_ = 31 * lvt_1_1_ + this.dioriteMinHeight;
            lvt_1_1_ = 31 * lvt_1_1_ + this.dioriteMaxHeight;
            lvt_1_1_ = 31 * lvt_1_1_ + this.andesiteSize;
            lvt_1_1_ = 31 * lvt_1_1_ + this.andesiteCount;
            lvt_1_1_ = 31 * lvt_1_1_ + this.andesiteMinHeight;
            lvt_1_1_ = 31 * lvt_1_1_ + this.andesiteMaxHeight;
            lvt_1_1_ = 31 * lvt_1_1_ + this.coalSize;
            lvt_1_1_ = 31 * lvt_1_1_ + this.coalCount;
            lvt_1_1_ = 31 * lvt_1_1_ + this.coalMinHeight;
            lvt_1_1_ = 31 * lvt_1_1_ + this.coalMaxHeight;
            lvt_1_1_ = 31 * lvt_1_1_ + this.ironSize;
            lvt_1_1_ = 31 * lvt_1_1_ + this.ironCount;
            lvt_1_1_ = 31 * lvt_1_1_ + this.ironMinHeight;
            lvt_1_1_ = 31 * lvt_1_1_ + this.ironMaxHeight;
            lvt_1_1_ = 31 * lvt_1_1_ + this.goldSize;
            lvt_1_1_ = 31 * lvt_1_1_ + this.goldCount;
            lvt_1_1_ = 31 * lvt_1_1_ + this.goldMinHeight;
            lvt_1_1_ = 31 * lvt_1_1_ + this.goldMaxHeight;
            lvt_1_1_ = 31 * lvt_1_1_ + this.redstoneSize;
            lvt_1_1_ = 31 * lvt_1_1_ + this.redstoneCount;
            lvt_1_1_ = 31 * lvt_1_1_ + this.redstoneMinHeight;
            lvt_1_1_ = 31 * lvt_1_1_ + this.redstoneMaxHeight;
            lvt_1_1_ = 31 * lvt_1_1_ + this.diamondSize;
            lvt_1_1_ = 31 * lvt_1_1_ + this.diamondCount;
            lvt_1_1_ = 31 * lvt_1_1_ + this.diamondMinHeight;
            lvt_1_1_ = 31 * lvt_1_1_ + this.diamondMaxHeight;
            lvt_1_1_ = 31 * lvt_1_1_ + this.lapisSize;
            lvt_1_1_ = 31 * lvt_1_1_ + this.lapisCount;
            lvt_1_1_ = 31 * lvt_1_1_ + this.lapisCenterHeight;
            lvt_1_1_ = 31 * lvt_1_1_ + this.lapisSpread;
            return lvt_1_1_;
        }

        public ChunkProviderSettings func_177864_b()
        {
            return new ChunkProviderSettings(this);
        }
    }

    public static class Serializer implements JsonDeserializer<ChunkProviderSettings.Factory>, JsonSerializer<ChunkProviderSettings.Factory>
    {
        public ChunkProviderSettings.Factory deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            JsonObject lvt_4_1_ = p_deserialize_1_.getAsJsonObject();
            ChunkProviderSettings.Factory lvt_5_1_ = new ChunkProviderSettings.Factory();

            try
            {
                lvt_5_1_.coordinateScale = JsonUtils.getFloat(lvt_4_1_, "coordinateScale", lvt_5_1_.coordinateScale);
                lvt_5_1_.heightScale = JsonUtils.getFloat(lvt_4_1_, "heightScale", lvt_5_1_.heightScale);
                lvt_5_1_.lowerLimitScale = JsonUtils.getFloat(lvt_4_1_, "lowerLimitScale", lvt_5_1_.lowerLimitScale);
                lvt_5_1_.upperLimitScale = JsonUtils.getFloat(lvt_4_1_, "upperLimitScale", lvt_5_1_.upperLimitScale);
                lvt_5_1_.depthNoiseScaleX = JsonUtils.getFloat(lvt_4_1_, "depthNoiseScaleX", lvt_5_1_.depthNoiseScaleX);
                lvt_5_1_.depthNoiseScaleZ = JsonUtils.getFloat(lvt_4_1_, "depthNoiseScaleZ", lvt_5_1_.depthNoiseScaleZ);
                lvt_5_1_.depthNoiseScaleExponent = JsonUtils.getFloat(lvt_4_1_, "depthNoiseScaleExponent", lvt_5_1_.depthNoiseScaleExponent);
                lvt_5_1_.mainNoiseScaleX = JsonUtils.getFloat(lvt_4_1_, "mainNoiseScaleX", lvt_5_1_.mainNoiseScaleX);
                lvt_5_1_.mainNoiseScaleY = JsonUtils.getFloat(lvt_4_1_, "mainNoiseScaleY", lvt_5_1_.mainNoiseScaleY);
                lvt_5_1_.mainNoiseScaleZ = JsonUtils.getFloat(lvt_4_1_, "mainNoiseScaleZ", lvt_5_1_.mainNoiseScaleZ);
                lvt_5_1_.baseSize = JsonUtils.getFloat(lvt_4_1_, "baseSize", lvt_5_1_.baseSize);
                lvt_5_1_.stretchY = JsonUtils.getFloat(lvt_4_1_, "stretchY", lvt_5_1_.stretchY);
                lvt_5_1_.biomeDepthWeight = JsonUtils.getFloat(lvt_4_1_, "biomeDepthWeight", lvt_5_1_.biomeDepthWeight);
                lvt_5_1_.biomeDepthOffset = JsonUtils.getFloat(lvt_4_1_, "biomeDepthOffset", lvt_5_1_.biomeDepthOffset);
                lvt_5_1_.biomeScaleWeight = JsonUtils.getFloat(lvt_4_1_, "biomeScaleWeight", lvt_5_1_.biomeScaleWeight);
                lvt_5_1_.biomeScaleOffset = JsonUtils.getFloat(lvt_4_1_, "biomeScaleOffset", lvt_5_1_.biomeScaleOffset);
                lvt_5_1_.seaLevel = JsonUtils.getInt(lvt_4_1_, "seaLevel", lvt_5_1_.seaLevel);
                lvt_5_1_.useCaves = JsonUtils.getBoolean(lvt_4_1_, "useCaves", lvt_5_1_.useCaves);
                lvt_5_1_.useDungeons = JsonUtils.getBoolean(lvt_4_1_, "useDungeons", lvt_5_1_.useDungeons);
                lvt_5_1_.dungeonChance = JsonUtils.getInt(lvt_4_1_, "dungeonChance", lvt_5_1_.dungeonChance);
                lvt_5_1_.useStrongholds = JsonUtils.getBoolean(lvt_4_1_, "useStrongholds", lvt_5_1_.useStrongholds);
                lvt_5_1_.useVillages = JsonUtils.getBoolean(lvt_4_1_, "useVillages", lvt_5_1_.useVillages);
                lvt_5_1_.useMineShafts = JsonUtils.getBoolean(lvt_4_1_, "useMineShafts", lvt_5_1_.useMineShafts);
                lvt_5_1_.useTemples = JsonUtils.getBoolean(lvt_4_1_, "useTemples", lvt_5_1_.useTemples);
                lvt_5_1_.useMonuments = JsonUtils.getBoolean(lvt_4_1_, "useMonuments", lvt_5_1_.useMonuments);
                lvt_5_1_.useRavines = JsonUtils.getBoolean(lvt_4_1_, "useRavines", lvt_5_1_.useRavines);
                lvt_5_1_.useWaterLakes = JsonUtils.getBoolean(lvt_4_1_, "useWaterLakes", lvt_5_1_.useWaterLakes);
                lvt_5_1_.waterLakeChance = JsonUtils.getInt(lvt_4_1_, "waterLakeChance", lvt_5_1_.waterLakeChance);
                lvt_5_1_.useLavaLakes = JsonUtils.getBoolean(lvt_4_1_, "useLavaLakes", lvt_5_1_.useLavaLakes);
                lvt_5_1_.lavaLakeChance = JsonUtils.getInt(lvt_4_1_, "lavaLakeChance", lvt_5_1_.lavaLakeChance);
                lvt_5_1_.useLavaOceans = JsonUtils.getBoolean(lvt_4_1_, "useLavaOceans", lvt_5_1_.useLavaOceans);
                lvt_5_1_.fixedBiome = JsonUtils.getInt(lvt_4_1_, "fixedBiome", lvt_5_1_.fixedBiome);

                if (lvt_5_1_.fixedBiome < 38 && lvt_5_1_.fixedBiome >= -1)
                {
                    if (lvt_5_1_.fixedBiome >= BiomeGenBase.hell.biomeID)
                    {
                        lvt_5_1_.fixedBiome += 2;
                    }
                }
                else
                {
                    lvt_5_1_.fixedBiome = -1;
                }

                lvt_5_1_.biomeSize = JsonUtils.getInt(lvt_4_1_, "biomeSize", lvt_5_1_.biomeSize);
                lvt_5_1_.riverSize = JsonUtils.getInt(lvt_4_1_, "riverSize", lvt_5_1_.riverSize);
                lvt_5_1_.dirtSize = JsonUtils.getInt(lvt_4_1_, "dirtSize", lvt_5_1_.dirtSize);
                lvt_5_1_.dirtCount = JsonUtils.getInt(lvt_4_1_, "dirtCount", lvt_5_1_.dirtCount);
                lvt_5_1_.dirtMinHeight = JsonUtils.getInt(lvt_4_1_, "dirtMinHeight", lvt_5_1_.dirtMinHeight);
                lvt_5_1_.dirtMaxHeight = JsonUtils.getInt(lvt_4_1_, "dirtMaxHeight", lvt_5_1_.dirtMaxHeight);
                lvt_5_1_.gravelSize = JsonUtils.getInt(lvt_4_1_, "gravelSize", lvt_5_1_.gravelSize);
                lvt_5_1_.gravelCount = JsonUtils.getInt(lvt_4_1_, "gravelCount", lvt_5_1_.gravelCount);
                lvt_5_1_.gravelMinHeight = JsonUtils.getInt(lvt_4_1_, "gravelMinHeight", lvt_5_1_.gravelMinHeight);
                lvt_5_1_.gravelMaxHeight = JsonUtils.getInt(lvt_4_1_, "gravelMaxHeight", lvt_5_1_.gravelMaxHeight);
                lvt_5_1_.graniteSize = JsonUtils.getInt(lvt_4_1_, "graniteSize", lvt_5_1_.graniteSize);
                lvt_5_1_.graniteCount = JsonUtils.getInt(lvt_4_1_, "graniteCount", lvt_5_1_.graniteCount);
                lvt_5_1_.graniteMinHeight = JsonUtils.getInt(lvt_4_1_, "graniteMinHeight", lvt_5_1_.graniteMinHeight);
                lvt_5_1_.graniteMaxHeight = JsonUtils.getInt(lvt_4_1_, "graniteMaxHeight", lvt_5_1_.graniteMaxHeight);
                lvt_5_1_.dioriteSize = JsonUtils.getInt(lvt_4_1_, "dioriteSize", lvt_5_1_.dioriteSize);
                lvt_5_1_.dioriteCount = JsonUtils.getInt(lvt_4_1_, "dioriteCount", lvt_5_1_.dioriteCount);
                lvt_5_1_.dioriteMinHeight = JsonUtils.getInt(lvt_4_1_, "dioriteMinHeight", lvt_5_1_.dioriteMinHeight);
                lvt_5_1_.dioriteMaxHeight = JsonUtils.getInt(lvt_4_1_, "dioriteMaxHeight", lvt_5_1_.dioriteMaxHeight);
                lvt_5_1_.andesiteSize = JsonUtils.getInt(lvt_4_1_, "andesiteSize", lvt_5_1_.andesiteSize);
                lvt_5_1_.andesiteCount = JsonUtils.getInt(lvt_4_1_, "andesiteCount", lvt_5_1_.andesiteCount);
                lvt_5_1_.andesiteMinHeight = JsonUtils.getInt(lvt_4_1_, "andesiteMinHeight", lvt_5_1_.andesiteMinHeight);
                lvt_5_1_.andesiteMaxHeight = JsonUtils.getInt(lvt_4_1_, "andesiteMaxHeight", lvt_5_1_.andesiteMaxHeight);
                lvt_5_1_.coalSize = JsonUtils.getInt(lvt_4_1_, "coalSize", lvt_5_1_.coalSize);
                lvt_5_1_.coalCount = JsonUtils.getInt(lvt_4_1_, "coalCount", lvt_5_1_.coalCount);
                lvt_5_1_.coalMinHeight = JsonUtils.getInt(lvt_4_1_, "coalMinHeight", lvt_5_1_.coalMinHeight);
                lvt_5_1_.coalMaxHeight = JsonUtils.getInt(lvt_4_1_, "coalMaxHeight", lvt_5_1_.coalMaxHeight);
                lvt_5_1_.ironSize = JsonUtils.getInt(lvt_4_1_, "ironSize", lvt_5_1_.ironSize);
                lvt_5_1_.ironCount = JsonUtils.getInt(lvt_4_1_, "ironCount", lvt_5_1_.ironCount);
                lvt_5_1_.ironMinHeight = JsonUtils.getInt(lvt_4_1_, "ironMinHeight", lvt_5_1_.ironMinHeight);
                lvt_5_1_.ironMaxHeight = JsonUtils.getInt(lvt_4_1_, "ironMaxHeight", lvt_5_1_.ironMaxHeight);
                lvt_5_1_.goldSize = JsonUtils.getInt(lvt_4_1_, "goldSize", lvt_5_1_.goldSize);
                lvt_5_1_.goldCount = JsonUtils.getInt(lvt_4_1_, "goldCount", lvt_5_1_.goldCount);
                lvt_5_1_.goldMinHeight = JsonUtils.getInt(lvt_4_1_, "goldMinHeight", lvt_5_1_.goldMinHeight);
                lvt_5_1_.goldMaxHeight = JsonUtils.getInt(lvt_4_1_, "goldMaxHeight", lvt_5_1_.goldMaxHeight);
                lvt_5_1_.redstoneSize = JsonUtils.getInt(lvt_4_1_, "redstoneSize", lvt_5_1_.redstoneSize);
                lvt_5_1_.redstoneCount = JsonUtils.getInt(lvt_4_1_, "redstoneCount", lvt_5_1_.redstoneCount);
                lvt_5_1_.redstoneMinHeight = JsonUtils.getInt(lvt_4_1_, "redstoneMinHeight", lvt_5_1_.redstoneMinHeight);
                lvt_5_1_.redstoneMaxHeight = JsonUtils.getInt(lvt_4_1_, "redstoneMaxHeight", lvt_5_1_.redstoneMaxHeight);
                lvt_5_1_.diamondSize = JsonUtils.getInt(lvt_4_1_, "diamondSize", lvt_5_1_.diamondSize);
                lvt_5_1_.diamondCount = JsonUtils.getInt(lvt_4_1_, "diamondCount", lvt_5_1_.diamondCount);
                lvt_5_1_.diamondMinHeight = JsonUtils.getInt(lvt_4_1_, "diamondMinHeight", lvt_5_1_.diamondMinHeight);
                lvt_5_1_.diamondMaxHeight = JsonUtils.getInt(lvt_4_1_, "diamondMaxHeight", lvt_5_1_.diamondMaxHeight);
                lvt_5_1_.lapisSize = JsonUtils.getInt(lvt_4_1_, "lapisSize", lvt_5_1_.lapisSize);
                lvt_5_1_.lapisCount = JsonUtils.getInt(lvt_4_1_, "lapisCount", lvt_5_1_.lapisCount);
                lvt_5_1_.lapisCenterHeight = JsonUtils.getInt(lvt_4_1_, "lapisCenterHeight", lvt_5_1_.lapisCenterHeight);
                lvt_5_1_.lapisSpread = JsonUtils.getInt(lvt_4_1_, "lapisSpread", lvt_5_1_.lapisSpread);
            }
            catch (Exception var7)
            {
                ;
            }

            return lvt_5_1_;
        }

        public JsonElement serialize(ChunkProviderSettings.Factory p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_)
        {
            JsonObject lvt_4_1_ = new JsonObject();
            lvt_4_1_.addProperty("coordinateScale", Float.valueOf(p_serialize_1_.coordinateScale));
            lvt_4_1_.addProperty("heightScale", Float.valueOf(p_serialize_1_.heightScale));
            lvt_4_1_.addProperty("lowerLimitScale", Float.valueOf(p_serialize_1_.lowerLimitScale));
            lvt_4_1_.addProperty("upperLimitScale", Float.valueOf(p_serialize_1_.upperLimitScale));
            lvt_4_1_.addProperty("depthNoiseScaleX", Float.valueOf(p_serialize_1_.depthNoiseScaleX));
            lvt_4_1_.addProperty("depthNoiseScaleZ", Float.valueOf(p_serialize_1_.depthNoiseScaleZ));
            lvt_4_1_.addProperty("depthNoiseScaleExponent", Float.valueOf(p_serialize_1_.depthNoiseScaleExponent));
            lvt_4_1_.addProperty("mainNoiseScaleX", Float.valueOf(p_serialize_1_.mainNoiseScaleX));
            lvt_4_1_.addProperty("mainNoiseScaleY", Float.valueOf(p_serialize_1_.mainNoiseScaleY));
            lvt_4_1_.addProperty("mainNoiseScaleZ", Float.valueOf(p_serialize_1_.mainNoiseScaleZ));
            lvt_4_1_.addProperty("baseSize", Float.valueOf(p_serialize_1_.baseSize));
            lvt_4_1_.addProperty("stretchY", Float.valueOf(p_serialize_1_.stretchY));
            lvt_4_1_.addProperty("biomeDepthWeight", Float.valueOf(p_serialize_1_.biomeDepthWeight));
            lvt_4_1_.addProperty("biomeDepthOffset", Float.valueOf(p_serialize_1_.biomeDepthOffset));
            lvt_4_1_.addProperty("biomeScaleWeight", Float.valueOf(p_serialize_1_.biomeScaleWeight));
            lvt_4_1_.addProperty("biomeScaleOffset", Float.valueOf(p_serialize_1_.biomeScaleOffset));
            lvt_4_1_.addProperty("seaLevel", Integer.valueOf(p_serialize_1_.seaLevel));
            lvt_4_1_.addProperty("useCaves", Boolean.valueOf(p_serialize_1_.useCaves));
            lvt_4_1_.addProperty("useDungeons", Boolean.valueOf(p_serialize_1_.useDungeons));
            lvt_4_1_.addProperty("dungeonChance", Integer.valueOf(p_serialize_1_.dungeonChance));
            lvt_4_1_.addProperty("useStrongholds", Boolean.valueOf(p_serialize_1_.useStrongholds));
            lvt_4_1_.addProperty("useVillages", Boolean.valueOf(p_serialize_1_.useVillages));
            lvt_4_1_.addProperty("useMineShafts", Boolean.valueOf(p_serialize_1_.useMineShafts));
            lvt_4_1_.addProperty("useTemples", Boolean.valueOf(p_serialize_1_.useTemples));
            lvt_4_1_.addProperty("useMonuments", Boolean.valueOf(p_serialize_1_.useMonuments));
            lvt_4_1_.addProperty("useRavines", Boolean.valueOf(p_serialize_1_.useRavines));
            lvt_4_1_.addProperty("useWaterLakes", Boolean.valueOf(p_serialize_1_.useWaterLakes));
            lvt_4_1_.addProperty("waterLakeChance", Integer.valueOf(p_serialize_1_.waterLakeChance));
            lvt_4_1_.addProperty("useLavaLakes", Boolean.valueOf(p_serialize_1_.useLavaLakes));
            lvt_4_1_.addProperty("lavaLakeChance", Integer.valueOf(p_serialize_1_.lavaLakeChance));
            lvt_4_1_.addProperty("useLavaOceans", Boolean.valueOf(p_serialize_1_.useLavaOceans));
            lvt_4_1_.addProperty("fixedBiome", Integer.valueOf(p_serialize_1_.fixedBiome));
            lvt_4_1_.addProperty("biomeSize", Integer.valueOf(p_serialize_1_.biomeSize));
            lvt_4_1_.addProperty("riverSize", Integer.valueOf(p_serialize_1_.riverSize));
            lvt_4_1_.addProperty("dirtSize", Integer.valueOf(p_serialize_1_.dirtSize));
            lvt_4_1_.addProperty("dirtCount", Integer.valueOf(p_serialize_1_.dirtCount));
            lvt_4_1_.addProperty("dirtMinHeight", Integer.valueOf(p_serialize_1_.dirtMinHeight));
            lvt_4_1_.addProperty("dirtMaxHeight", Integer.valueOf(p_serialize_1_.dirtMaxHeight));
            lvt_4_1_.addProperty("gravelSize", Integer.valueOf(p_serialize_1_.gravelSize));
            lvt_4_1_.addProperty("gravelCount", Integer.valueOf(p_serialize_1_.gravelCount));
            lvt_4_1_.addProperty("gravelMinHeight", Integer.valueOf(p_serialize_1_.gravelMinHeight));
            lvt_4_1_.addProperty("gravelMaxHeight", Integer.valueOf(p_serialize_1_.gravelMaxHeight));
            lvt_4_1_.addProperty("graniteSize", Integer.valueOf(p_serialize_1_.graniteSize));
            lvt_4_1_.addProperty("graniteCount", Integer.valueOf(p_serialize_1_.graniteCount));
            lvt_4_1_.addProperty("graniteMinHeight", Integer.valueOf(p_serialize_1_.graniteMinHeight));
            lvt_4_1_.addProperty("graniteMaxHeight", Integer.valueOf(p_serialize_1_.graniteMaxHeight));
            lvt_4_1_.addProperty("dioriteSize", Integer.valueOf(p_serialize_1_.dioriteSize));
            lvt_4_1_.addProperty("dioriteCount", Integer.valueOf(p_serialize_1_.dioriteCount));
            lvt_4_1_.addProperty("dioriteMinHeight", Integer.valueOf(p_serialize_1_.dioriteMinHeight));
            lvt_4_1_.addProperty("dioriteMaxHeight", Integer.valueOf(p_serialize_1_.dioriteMaxHeight));
            lvt_4_1_.addProperty("andesiteSize", Integer.valueOf(p_serialize_1_.andesiteSize));
            lvt_4_1_.addProperty("andesiteCount", Integer.valueOf(p_serialize_1_.andesiteCount));
            lvt_4_1_.addProperty("andesiteMinHeight", Integer.valueOf(p_serialize_1_.andesiteMinHeight));
            lvt_4_1_.addProperty("andesiteMaxHeight", Integer.valueOf(p_serialize_1_.andesiteMaxHeight));
            lvt_4_1_.addProperty("coalSize", Integer.valueOf(p_serialize_1_.coalSize));
            lvt_4_1_.addProperty("coalCount", Integer.valueOf(p_serialize_1_.coalCount));
            lvt_4_1_.addProperty("coalMinHeight", Integer.valueOf(p_serialize_1_.coalMinHeight));
            lvt_4_1_.addProperty("coalMaxHeight", Integer.valueOf(p_serialize_1_.coalMaxHeight));
            lvt_4_1_.addProperty("ironSize", Integer.valueOf(p_serialize_1_.ironSize));
            lvt_4_1_.addProperty("ironCount", Integer.valueOf(p_serialize_1_.ironCount));
            lvt_4_1_.addProperty("ironMinHeight", Integer.valueOf(p_serialize_1_.ironMinHeight));
            lvt_4_1_.addProperty("ironMaxHeight", Integer.valueOf(p_serialize_1_.ironMaxHeight));
            lvt_4_1_.addProperty("goldSize", Integer.valueOf(p_serialize_1_.goldSize));
            lvt_4_1_.addProperty("goldCount", Integer.valueOf(p_serialize_1_.goldCount));
            lvt_4_1_.addProperty("goldMinHeight", Integer.valueOf(p_serialize_1_.goldMinHeight));
            lvt_4_1_.addProperty("goldMaxHeight", Integer.valueOf(p_serialize_1_.goldMaxHeight));
            lvt_4_1_.addProperty("redstoneSize", Integer.valueOf(p_serialize_1_.redstoneSize));
            lvt_4_1_.addProperty("redstoneCount", Integer.valueOf(p_serialize_1_.redstoneCount));
            lvt_4_1_.addProperty("redstoneMinHeight", Integer.valueOf(p_serialize_1_.redstoneMinHeight));
            lvt_4_1_.addProperty("redstoneMaxHeight", Integer.valueOf(p_serialize_1_.redstoneMaxHeight));
            lvt_4_1_.addProperty("diamondSize", Integer.valueOf(p_serialize_1_.diamondSize));
            lvt_4_1_.addProperty("diamondCount", Integer.valueOf(p_serialize_1_.diamondCount));
            lvt_4_1_.addProperty("diamondMinHeight", Integer.valueOf(p_serialize_1_.diamondMinHeight));
            lvt_4_1_.addProperty("diamondMaxHeight", Integer.valueOf(p_serialize_1_.diamondMaxHeight));
            lvt_4_1_.addProperty("lapisSize", Integer.valueOf(p_serialize_1_.lapisSize));
            lvt_4_1_.addProperty("lapisCount", Integer.valueOf(p_serialize_1_.lapisCount));
            lvt_4_1_.addProperty("lapisCenterHeight", Integer.valueOf(p_serialize_1_.lapisCenterHeight));
            lvt_4_1_.addProperty("lapisSpread", Integer.valueOf(p_serialize_1_.lapisSpread));
            return lvt_4_1_;
        }

        public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
        }

        public JsonElement serialize(Object p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_)
        {
            return this.serialize((ChunkProviderSettings.Factory)p_serialize_1_, p_serialize_2_, p_serialize_3_);
        }
    }
}
