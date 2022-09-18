package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureOceanMonument;

public class ChunkProviderGenerate implements IChunkProvider
{
    /** RNG. */
    private Random rand;
    private NoiseGeneratorOctaves field_147431_j;
    private NoiseGeneratorOctaves field_147432_k;
    private NoiseGeneratorOctaves field_147429_l;
    private NoiseGeneratorPerlin field_147430_m;

    /** A NoiseGeneratorOctaves used in generating terrain */
    public NoiseGeneratorOctaves noiseGen5;

    /** A NoiseGeneratorOctaves used in generating terrain */
    public NoiseGeneratorOctaves noiseGen6;
    public NoiseGeneratorOctaves mobSpawnerNoise;

    /** Reference to the World object. */
    private World worldObj;

    /** are map structures going to be generated (e.g. strongholds) */
    private final boolean mapFeaturesEnabled;
    private WorldType field_177475_o;
    private final double[] field_147434_q;
    private final float[] parabolicField;
    private ChunkProviderSettings settings;
    private Block oceanBlockTmpl = Blocks.water;
    private double[] stoneNoise = new double[256];
    private MapGenBase caveGenerator = new MapGenCaves();

    /** Holds Stronghold Generator */
    private MapGenStronghold strongholdGenerator = new MapGenStronghold();

    /** Holds Village Generator */
    private MapGenVillage villageGenerator = new MapGenVillage();

    /** Holds Mineshaft Generator */
    private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();
    private MapGenScatteredFeature scatteredFeatureGenerator = new MapGenScatteredFeature();

    /** Holds ravine generator */
    private MapGenBase ravineGenerator = new MapGenRavine();
    private StructureOceanMonument oceanMonumentGenerator = new StructureOceanMonument();

    /** The biomes that are used to generate the chunk */
    private BiomeGenBase[] biomesForGeneration;
    double[] mainNoiseArray;
    double[] lowerLimitNoiseArray;
    double[] upperLimitNoiseArray;
    double[] depthNoiseArray;

    public ChunkProviderGenerate(World worldIn, long seed, boolean generateStructures, String structuresJson)
    {
        this.worldObj = worldIn;
        this.mapFeaturesEnabled = generateStructures;
        this.field_177475_o = worldIn.getWorldInfo().getTerrainType();
        this.rand = new Random(seed);
        this.field_147431_j = new NoiseGeneratorOctaves(this.rand, 16);
        this.field_147432_k = new NoiseGeneratorOctaves(this.rand, 16);
        this.field_147429_l = new NoiseGeneratorOctaves(this.rand, 8);
        this.field_147430_m = new NoiseGeneratorPerlin(this.rand, 4);
        this.noiseGen5 = new NoiseGeneratorOctaves(this.rand, 10);
        this.noiseGen6 = new NoiseGeneratorOctaves(this.rand, 16);
        this.mobSpawnerNoise = new NoiseGeneratorOctaves(this.rand, 8);
        this.field_147434_q = new double[825];
        this.parabolicField = new float[25];

        for (int lvt_6_1_ = -2; lvt_6_1_ <= 2; ++lvt_6_1_)
        {
            for (int lvt_7_1_ = -2; lvt_7_1_ <= 2; ++lvt_7_1_)
            {
                float lvt_8_1_ = 10.0F / MathHelper.sqrt_float((float)(lvt_6_1_ * lvt_6_1_ + lvt_7_1_ * lvt_7_1_) + 0.2F);
                this.parabolicField[lvt_6_1_ + 2 + (lvt_7_1_ + 2) * 5] = lvt_8_1_;
            }
        }

        if (structuresJson != null)
        {
            this.settings = ChunkProviderSettings.Factory.jsonToFactory(structuresJson).func_177864_b();
            this.oceanBlockTmpl = this.settings.useLavaOceans ? Blocks.lava : Blocks.water;
            worldIn.setSeaLevel(this.settings.seaLevel);
        }
    }

    /**
     * Generates a bare-bones chunk of nothing but stone or ocean blocks, formed, but featureless.
     */
    public void setBlocksInChunk(int x, int z, ChunkPrimer primer)
    {
        this.biomesForGeneration = this.worldObj.getWorldChunkManager().getBiomesForGeneration(this.biomesForGeneration, x * 4 - 2, z * 4 - 2, 10, 10);
        this.func_147423_a(x * 4, 0, z * 4);

        for (int lvt_4_1_ = 0; lvt_4_1_ < 4; ++lvt_4_1_)
        {
            int lvt_5_1_ = lvt_4_1_ * 5;
            int lvt_6_1_ = (lvt_4_1_ + 1) * 5;

            for (int lvt_7_1_ = 0; lvt_7_1_ < 4; ++lvt_7_1_)
            {
                int lvt_8_1_ = (lvt_5_1_ + lvt_7_1_) * 33;
                int lvt_9_1_ = (lvt_5_1_ + lvt_7_1_ + 1) * 33;
                int lvt_10_1_ = (lvt_6_1_ + lvt_7_1_) * 33;
                int lvt_11_1_ = (lvt_6_1_ + lvt_7_1_ + 1) * 33;

                for (int lvt_12_1_ = 0; lvt_12_1_ < 32; ++lvt_12_1_)
                {
                    double lvt_13_1_ = 0.125D;
                    double lvt_15_1_ = this.field_147434_q[lvt_8_1_ + lvt_12_1_];
                    double lvt_17_1_ = this.field_147434_q[lvt_9_1_ + lvt_12_1_];
                    double lvt_19_1_ = this.field_147434_q[lvt_10_1_ + lvt_12_1_];
                    double lvt_21_1_ = this.field_147434_q[lvt_11_1_ + lvt_12_1_];
                    double lvt_23_1_ = (this.field_147434_q[lvt_8_1_ + lvt_12_1_ + 1] - lvt_15_1_) * lvt_13_1_;
                    double lvt_25_1_ = (this.field_147434_q[lvt_9_1_ + lvt_12_1_ + 1] - lvt_17_1_) * lvt_13_1_;
                    double lvt_27_1_ = (this.field_147434_q[lvt_10_1_ + lvt_12_1_ + 1] - lvt_19_1_) * lvt_13_1_;
                    double lvt_29_1_ = (this.field_147434_q[lvt_11_1_ + lvt_12_1_ + 1] - lvt_21_1_) * lvt_13_1_;

                    for (int lvt_31_1_ = 0; lvt_31_1_ < 8; ++lvt_31_1_)
                    {
                        double lvt_32_1_ = 0.25D;
                        double lvt_34_1_ = lvt_15_1_;
                        double lvt_36_1_ = lvt_17_1_;
                        double lvt_38_1_ = (lvt_19_1_ - lvt_15_1_) * lvt_32_1_;
                        double lvt_40_1_ = (lvt_21_1_ - lvt_17_1_) * lvt_32_1_;

                        for (int lvt_42_1_ = 0; lvt_42_1_ < 4; ++lvt_42_1_)
                        {
                            double lvt_43_1_ = 0.25D;
                            double lvt_47_1_ = (lvt_36_1_ - lvt_34_1_) * lvt_43_1_;
                            double lvt_45_1_ = lvt_34_1_ - lvt_47_1_;

                            for (int lvt_49_1_ = 0; lvt_49_1_ < 4; ++lvt_49_1_)
                            {
                                if ((lvt_45_1_ += lvt_47_1_) > 0.0D)
                                {
                                    primer.setBlockState(lvt_4_1_ * 4 + lvt_42_1_, lvt_12_1_ * 8 + lvt_31_1_, lvt_7_1_ * 4 + lvt_49_1_, Blocks.stone.getDefaultState());
                                }
                                else if (lvt_12_1_ * 8 + lvt_31_1_ < this.settings.seaLevel)
                                {
                                    primer.setBlockState(lvt_4_1_ * 4 + lvt_42_1_, lvt_12_1_ * 8 + lvt_31_1_, lvt_7_1_ * 4 + lvt_49_1_, this.oceanBlockTmpl.getDefaultState());
                                }
                            }

                            lvt_34_1_ += lvt_38_1_;
                            lvt_36_1_ += lvt_40_1_;
                        }

                        lvt_15_1_ += lvt_23_1_;
                        lvt_17_1_ += lvt_25_1_;
                        lvt_19_1_ += lvt_27_1_;
                        lvt_21_1_ += lvt_29_1_;
                    }
                }
            }
        }
    }

    /**
     * Possibly reshapes the biome if appropriate for the biome type, and replaces some stone with things like dirt,
     * grass, gravel, ice
     */
    public void replaceBlocksForBiome(int x, int z, ChunkPrimer primer, BiomeGenBase[] biomeGens)
    {
        double lvt_5_1_ = 0.03125D;
        this.stoneNoise = this.field_147430_m.func_151599_a(this.stoneNoise, (double)(x * 16), (double)(z * 16), 16, 16, lvt_5_1_ * 2.0D, lvt_5_1_ * 2.0D, 1.0D);

        for (int lvt_7_1_ = 0; lvt_7_1_ < 16; ++lvt_7_1_)
        {
            for (int lvt_8_1_ = 0; lvt_8_1_ < 16; ++lvt_8_1_)
            {
                BiomeGenBase lvt_9_1_ = biomeGens[lvt_8_1_ + lvt_7_1_ * 16];
                lvt_9_1_.genTerrainBlocks(this.worldObj, this.rand, primer, x * 16 + lvt_7_1_, z * 16 + lvt_8_1_, this.stoneNoise[lvt_8_1_ + lvt_7_1_ * 16]);
            }
        }
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    public Chunk provideChunk(int x, int z)
    {
        this.rand.setSeed((long)x * 341873128712L + (long)z * 132897987541L);
        ChunkPrimer lvt_3_1_ = new ChunkPrimer();
        this.setBlocksInChunk(x, z, lvt_3_1_);
        this.biomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, x * 16, z * 16, 16, 16);
        this.replaceBlocksForBiome(x, z, lvt_3_1_, this.biomesForGeneration);

        if (this.settings.useCaves)
        {
            this.caveGenerator.generate(this, this.worldObj, x, z, lvt_3_1_);
        }

        if (this.settings.useRavines)
        {
            this.ravineGenerator.generate(this, this.worldObj, x, z, lvt_3_1_);
        }

        if (this.settings.useMineShafts && this.mapFeaturesEnabled)
        {
            this.mineshaftGenerator.generate(this, this.worldObj, x, z, lvt_3_1_);
        }

        if (this.settings.useVillages && this.mapFeaturesEnabled)
        {
            this.villageGenerator.generate(this, this.worldObj, x, z, lvt_3_1_);
        }

        if (this.settings.useStrongholds && this.mapFeaturesEnabled)
        {
            this.strongholdGenerator.generate(this, this.worldObj, x, z, lvt_3_1_);
        }

        if (this.settings.useTemples && this.mapFeaturesEnabled)
        {
            this.scatteredFeatureGenerator.generate(this, this.worldObj, x, z, lvt_3_1_);
        }

        if (this.settings.useMonuments && this.mapFeaturesEnabled)
        {
            this.oceanMonumentGenerator.generate(this, this.worldObj, x, z, lvt_3_1_);
        }

        Chunk lvt_4_1_ = new Chunk(this.worldObj, lvt_3_1_, x, z);
        byte[] lvt_5_1_ = lvt_4_1_.getBiomeArray();

        for (int lvt_6_1_ = 0; lvt_6_1_ < lvt_5_1_.length; ++lvt_6_1_)
        {
            lvt_5_1_[lvt_6_1_] = (byte)this.biomesForGeneration[lvt_6_1_].biomeID;
        }

        lvt_4_1_.generateSkylightMap();
        return lvt_4_1_;
    }

    private void func_147423_a(int x, int y, int z)
    {
        this.depthNoiseArray = this.noiseGen6.generateNoiseOctaves(this.depthNoiseArray, x, z, 5, 5, (double)this.settings.depthNoiseScaleX, (double)this.settings.depthNoiseScaleZ, (double)this.settings.depthNoiseScaleExponent);
        float lvt_4_1_ = this.settings.coordinateScale;
        float lvt_5_1_ = this.settings.heightScale;
        this.mainNoiseArray = this.field_147429_l.generateNoiseOctaves(this.mainNoiseArray, x, y, z, 5, 33, 5, (double)(lvt_4_1_ / this.settings.mainNoiseScaleX), (double)(lvt_5_1_ / this.settings.mainNoiseScaleY), (double)(lvt_4_1_ / this.settings.mainNoiseScaleZ));
        this.lowerLimitNoiseArray = this.field_147431_j.generateNoiseOctaves(this.lowerLimitNoiseArray, x, y, z, 5, 33, 5, (double)lvt_4_1_, (double)lvt_5_1_, (double)lvt_4_1_);
        this.upperLimitNoiseArray = this.field_147432_k.generateNoiseOctaves(this.upperLimitNoiseArray, x, y, z, 5, 33, 5, (double)lvt_4_1_, (double)lvt_5_1_, (double)lvt_4_1_);
        z = 0;
        x = 0;
        int lvt_6_1_ = 0;
        int lvt_7_1_ = 0;

        for (int lvt_8_1_ = 0; lvt_8_1_ < 5; ++lvt_8_1_)
        {
            for (int lvt_9_1_ = 0; lvt_9_1_ < 5; ++lvt_9_1_)
            {
                float lvt_10_1_ = 0.0F;
                float lvt_11_1_ = 0.0F;
                float lvt_12_1_ = 0.0F;
                int lvt_13_1_ = 2;
                BiomeGenBase lvt_14_1_ = this.biomesForGeneration[lvt_8_1_ + 2 + (lvt_9_1_ + 2) * 10];

                for (int lvt_15_1_ = -lvt_13_1_; lvt_15_1_ <= lvt_13_1_; ++lvt_15_1_)
                {
                    for (int lvt_16_1_ = -lvt_13_1_; lvt_16_1_ <= lvt_13_1_; ++lvt_16_1_)
                    {
                        BiomeGenBase lvt_17_1_ = this.biomesForGeneration[lvt_8_1_ + lvt_15_1_ + 2 + (lvt_9_1_ + lvt_16_1_ + 2) * 10];
                        float lvt_18_1_ = this.settings.biomeDepthOffSet + lvt_17_1_.minHeight * this.settings.biomeDepthWeight;
                        float lvt_19_1_ = this.settings.biomeScaleOffset + lvt_17_1_.maxHeight * this.settings.biomeScaleWeight;

                        if (this.field_177475_o == WorldType.AMPLIFIED && lvt_18_1_ > 0.0F)
                        {
                            lvt_18_1_ = 1.0F + lvt_18_1_ * 2.0F;
                            lvt_19_1_ = 1.0F + lvt_19_1_ * 4.0F;
                        }

                        float lvt_20_1_ = this.parabolicField[lvt_15_1_ + 2 + (lvt_16_1_ + 2) * 5] / (lvt_18_1_ + 2.0F);

                        if (lvt_17_1_.minHeight > lvt_14_1_.minHeight)
                        {
                            lvt_20_1_ /= 2.0F;
                        }

                        lvt_10_1_ += lvt_19_1_ * lvt_20_1_;
                        lvt_11_1_ += lvt_18_1_ * lvt_20_1_;
                        lvt_12_1_ += lvt_20_1_;
                    }
                }

                lvt_10_1_ = lvt_10_1_ / lvt_12_1_;
                lvt_11_1_ = lvt_11_1_ / lvt_12_1_;
                lvt_10_1_ = lvt_10_1_ * 0.9F + 0.1F;
                lvt_11_1_ = (lvt_11_1_ * 4.0F - 1.0F) / 8.0F;
                double lvt_15_2_ = this.depthNoiseArray[lvt_7_1_] / 8000.0D;

                if (lvt_15_2_ < 0.0D)
                {
                    lvt_15_2_ = -lvt_15_2_ * 0.3D;
                }

                lvt_15_2_ = lvt_15_2_ * 3.0D - 2.0D;

                if (lvt_15_2_ < 0.0D)
                {
                    lvt_15_2_ = lvt_15_2_ / 2.0D;

                    if (lvt_15_2_ < -1.0D)
                    {
                        lvt_15_2_ = -1.0D;
                    }

                    lvt_15_2_ = lvt_15_2_ / 1.4D;
                    lvt_15_2_ = lvt_15_2_ / 2.0D;
                }
                else
                {
                    if (lvt_15_2_ > 1.0D)
                    {
                        lvt_15_2_ = 1.0D;
                    }

                    lvt_15_2_ = lvt_15_2_ / 8.0D;
                }

                ++lvt_7_1_;
                double lvt_17_2_ = (double)lvt_11_1_;
                double lvt_19_2_ = (double)lvt_10_1_;
                lvt_17_2_ = lvt_17_2_ + lvt_15_2_ * 0.2D;
                lvt_17_2_ = lvt_17_2_ * (double)this.settings.baseSize / 8.0D;
                double lvt_21_1_ = (double)this.settings.baseSize + lvt_17_2_ * 4.0D;

                for (int lvt_23_1_ = 0; lvt_23_1_ < 33; ++lvt_23_1_)
                {
                    double lvt_24_1_ = ((double)lvt_23_1_ - lvt_21_1_) * (double)this.settings.stretchY * 128.0D / 256.0D / lvt_19_2_;

                    if (lvt_24_1_ < 0.0D)
                    {
                        lvt_24_1_ *= 4.0D;
                    }

                    double lvt_26_1_ = this.lowerLimitNoiseArray[lvt_6_1_] / (double)this.settings.lowerLimitScale;
                    double lvt_28_1_ = this.upperLimitNoiseArray[lvt_6_1_] / (double)this.settings.upperLimitScale;
                    double lvt_30_1_ = (this.mainNoiseArray[lvt_6_1_] / 10.0D + 1.0D) / 2.0D;
                    double lvt_32_1_ = MathHelper.denormalizeClamp(lvt_26_1_, lvt_28_1_, lvt_30_1_) - lvt_24_1_;

                    if (lvt_23_1_ > 29)
                    {
                        double lvt_34_1_ = (double)((float)(lvt_23_1_ - 29) / 3.0F);
                        lvt_32_1_ = lvt_32_1_ * (1.0D - lvt_34_1_) + -10.0D * lvt_34_1_;
                    }

                    this.field_147434_q[lvt_6_1_] = lvt_32_1_;
                    ++lvt_6_1_;
                }
            }
        }
    }

    /**
     * Checks to see if a chunk exists at x, z
     */
    public boolean chunkExists(int x, int z)
    {
        return true;
    }

    /**
     * Populates chunk with ores etc etc
     */
    public void populate(IChunkProvider chunkProvider, int x, int z)
    {
        BlockFalling.fallInstantly = true;
        int lvt_4_1_ = x * 16;
        int lvt_5_1_ = z * 16;
        BlockPos lvt_6_1_ = new BlockPos(lvt_4_1_, 0, lvt_5_1_);
        BiomeGenBase lvt_7_1_ = this.worldObj.getBiomeGenForCoords(lvt_6_1_.add(16, 0, 16));
        this.rand.setSeed(this.worldObj.getSeed());
        long lvt_8_1_ = this.rand.nextLong() / 2L * 2L + 1L;
        long lvt_10_1_ = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed((long)x * lvt_8_1_ + (long)z * lvt_10_1_ ^ this.worldObj.getSeed());
        boolean lvt_12_1_ = false;
        ChunkCoordIntPair lvt_13_1_ = new ChunkCoordIntPair(x, z);

        if (this.settings.useMineShafts && this.mapFeaturesEnabled)
        {
            this.mineshaftGenerator.generateStructure(this.worldObj, this.rand, lvt_13_1_);
        }

        if (this.settings.useVillages && this.mapFeaturesEnabled)
        {
            lvt_12_1_ = this.villageGenerator.generateStructure(this.worldObj, this.rand, lvt_13_1_);
        }

        if (this.settings.useStrongholds && this.mapFeaturesEnabled)
        {
            this.strongholdGenerator.generateStructure(this.worldObj, this.rand, lvt_13_1_);
        }

        if (this.settings.useTemples && this.mapFeaturesEnabled)
        {
            this.scatteredFeatureGenerator.generateStructure(this.worldObj, this.rand, lvt_13_1_);
        }

        if (this.settings.useMonuments && this.mapFeaturesEnabled)
        {
            this.oceanMonumentGenerator.generateStructure(this.worldObj, this.rand, lvt_13_1_);
        }

        if (lvt_7_1_ != BiomeGenBase.desert && lvt_7_1_ != BiomeGenBase.desertHills && this.settings.useWaterLakes && !lvt_12_1_ && this.rand.nextInt(this.settings.waterLakeChance) == 0)
        {
            int lvt_14_1_ = this.rand.nextInt(16) + 8;
            int lvt_15_1_ = this.rand.nextInt(256);
            int lvt_16_1_ = this.rand.nextInt(16) + 8;
            (new WorldGenLakes(Blocks.water)).generate(this.worldObj, this.rand, lvt_6_1_.add(lvt_14_1_, lvt_15_1_, lvt_16_1_));
        }

        if (!lvt_12_1_ && this.rand.nextInt(this.settings.lavaLakeChance / 10) == 0 && this.settings.useLavaLakes)
        {
            int lvt_14_2_ = this.rand.nextInt(16) + 8;
            int lvt_15_2_ = this.rand.nextInt(this.rand.nextInt(248) + 8);
            int lvt_16_2_ = this.rand.nextInt(16) + 8;

            if (lvt_15_2_ < this.worldObj.getSeaLevel() || this.rand.nextInt(this.settings.lavaLakeChance / 8) == 0)
            {
                (new WorldGenLakes(Blocks.lava)).generate(this.worldObj, this.rand, lvt_6_1_.add(lvt_14_2_, lvt_15_2_, lvt_16_2_));
            }
        }

        if (this.settings.useDungeons)
        {
            for (int lvt_14_3_ = 0; lvt_14_3_ < this.settings.dungeonChance; ++lvt_14_3_)
            {
                int lvt_15_3_ = this.rand.nextInt(16) + 8;
                int lvt_16_3_ = this.rand.nextInt(256);
                int lvt_17_1_ = this.rand.nextInt(16) + 8;
                (new WorldGenDungeons()).generate(this.worldObj, this.rand, lvt_6_1_.add(lvt_15_3_, lvt_16_3_, lvt_17_1_));
            }
        }

        lvt_7_1_.decorate(this.worldObj, this.rand, new BlockPos(lvt_4_1_, 0, lvt_5_1_));
        SpawnerAnimals.performWorldGenSpawning(this.worldObj, lvt_7_1_, lvt_4_1_ + 8, lvt_5_1_ + 8, 16, 16, this.rand);
        lvt_6_1_ = lvt_6_1_.add(8, 0, 8);

        for (int lvt_14_4_ = 0; lvt_14_4_ < 16; ++lvt_14_4_)
        {
            for (int lvt_15_4_ = 0; lvt_15_4_ < 16; ++lvt_15_4_)
            {
                BlockPos lvt_16_4_ = this.worldObj.getPrecipitationHeight(lvt_6_1_.add(lvt_14_4_, 0, lvt_15_4_));
                BlockPos lvt_17_2_ = lvt_16_4_.down();

                if (this.worldObj.canBlockFreezeWater(lvt_17_2_))
                {
                    this.worldObj.setBlockState(lvt_17_2_, Blocks.ice.getDefaultState(), 2);
                }

                if (this.worldObj.canSnowAt(lvt_16_4_, true))
                {
                    this.worldObj.setBlockState(lvt_16_4_, Blocks.snow_layer.getDefaultState(), 2);
                }
            }
        }

        BlockFalling.fallInstantly = false;
    }

    public boolean populateChunk(IChunkProvider chunkProvider, Chunk chunkIn, int x, int z)
    {
        boolean lvt_5_1_ = false;

        if (this.settings.useMonuments && this.mapFeaturesEnabled && chunkIn.getInhabitedTime() < 3600L)
        {
            lvt_5_1_ |= this.oceanMonumentGenerator.generateStructure(this.worldObj, this.rand, new ChunkCoordIntPair(x, z));
        }

        return lvt_5_1_;
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
     * Return true if all chunks have been saved.
     */
    public boolean saveChunks(boolean saveAllChunks, IProgressUpdate progressCallback)
    {
        return true;
    }

    /**
     * Save extra data not associated with any Chunk.  Not saved during autosave, only during world unload.  Currently
     * unimplemented.
     */
    public void saveExtraData()
    {
    }

    /**
     * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
     */
    public boolean unloadQueuedChunks()
    {
        return false;
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    public boolean canSave()
    {
        return true;
    }

    /**
     * Converts the instance data to a readable string.
     */
    public String makeString()
    {
        return "RandomLevelSource";
    }

    public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
    {
        BiomeGenBase lvt_3_1_ = this.worldObj.getBiomeGenForCoords(pos);

        if (this.mapFeaturesEnabled)
        {
            if (creatureType == EnumCreatureType.MONSTER && this.scatteredFeatureGenerator.func_175798_a(pos))
            {
                return this.scatteredFeatureGenerator.getScatteredFeatureSpawnList();
            }

            if (creatureType == EnumCreatureType.MONSTER && this.settings.useMonuments && this.oceanMonumentGenerator.isPositionInStructure(this.worldObj, pos))
            {
                return this.oceanMonumentGenerator.getScatteredFeatureSpawnList();
            }
        }

        return lvt_3_1_.getSpawnableList(creatureType);
    }

    public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position)
    {
        return "Stronghold".equals(structureName) && this.strongholdGenerator != null ? this.strongholdGenerator.getClosestStrongholdPos(worldIn, position) : null;
    }

    public int getLoadedChunkCount()
    {
        return 0;
    }

    public void recreateStructures(Chunk chunkIn, int x, int z)
    {
        if (this.settings.useMineShafts && this.mapFeaturesEnabled)
        {
            this.mineshaftGenerator.generate(this, this.worldObj, x, z, (ChunkPrimer)null);
        }

        if (this.settings.useVillages && this.mapFeaturesEnabled)
        {
            this.villageGenerator.generate(this, this.worldObj, x, z, (ChunkPrimer)null);
        }

        if (this.settings.useStrongholds && this.mapFeaturesEnabled)
        {
            this.strongholdGenerator.generate(this, this.worldObj, x, z, (ChunkPrimer)null);
        }

        if (this.settings.useTemples && this.mapFeaturesEnabled)
        {
            this.scatteredFeatureGenerator.generate(this, this.worldObj, x, z, (ChunkPrimer)null);
        }

        if (this.settings.useMonuments && this.mapFeaturesEnabled)
        {
            this.oceanMonumentGenerator.generate(this, this.worldObj, x, z, (ChunkPrimer)null);
        }
    }

    public Chunk provideChunk(BlockPos blockPosIn)
    {
        return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
    }
}
