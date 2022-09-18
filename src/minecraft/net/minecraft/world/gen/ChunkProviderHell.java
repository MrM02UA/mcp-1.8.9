package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockHelper;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenFire;
import net.minecraft.world.gen.feature.WorldGenGlowStone1;
import net.minecraft.world.gen.feature.WorldGenGlowStone2;
import net.minecraft.world.gen.feature.WorldGenHellLava;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.MapGenNetherBridge;

public class ChunkProviderHell implements IChunkProvider
{
    /** Is the world that the nether is getting generated. */
    private final World worldObj;
    private final boolean field_177466_i;
    private final Random hellRNG;

    /**
     * Holds the noise used to determine whether slowsand can be generated at a location
     */
    private double[] slowsandNoise = new double[256];
    private double[] gravelNoise = new double[256];

    /**
     * Holds the noise used to determine whether something other than netherrack can be generated at a location
     */
    private double[] netherrackExclusivityNoise = new double[256];
    private double[] noiseField;

    /** A NoiseGeneratorOctaves used in generating nether terrain */
    private final NoiseGeneratorOctaves netherNoiseGen1;
    private final NoiseGeneratorOctaves netherNoiseGen2;
    private final NoiseGeneratorOctaves netherNoiseGen3;

    /** Determines whether slowsand or gravel can be generated at a location */
    private final NoiseGeneratorOctaves slowsandGravelNoiseGen;

    /**
     * Determines whether something other than nettherack can be generated at a location
     */
    private final NoiseGeneratorOctaves netherrackExculsivityNoiseGen;
    public final NoiseGeneratorOctaves netherNoiseGen6;
    public final NoiseGeneratorOctaves netherNoiseGen7;
    private final WorldGenFire field_177470_t = new WorldGenFire();
    private final WorldGenGlowStone1 field_177469_u = new WorldGenGlowStone1();
    private final WorldGenGlowStone2 field_177468_v = new WorldGenGlowStone2();
    private final WorldGenerator field_177467_w = new WorldGenMinable(Blocks.quartz_ore.getDefaultState(), 14, BlockHelper.forBlock(Blocks.netherrack));
    private final WorldGenHellLava field_177473_x = new WorldGenHellLava(Blocks.flowing_lava, true);
    private final WorldGenHellLava field_177472_y = new WorldGenHellLava(Blocks.flowing_lava, false);
    private final GeneratorBushFeature field_177471_z = new GeneratorBushFeature(Blocks.brown_mushroom);
    private final GeneratorBushFeature field_177465_A = new GeneratorBushFeature(Blocks.red_mushroom);
    private final MapGenNetherBridge genNetherBridge = new MapGenNetherBridge();
    private final MapGenBase netherCaveGenerator = new MapGenCavesHell();
    double[] noiseData1;
    double[] noiseData2;
    double[] noiseData3;
    double[] noiseData4;
    double[] noiseData5;

    public ChunkProviderHell(World worldIn, boolean p_i45637_2_, long seed)
    {
        this.worldObj = worldIn;
        this.field_177466_i = p_i45637_2_;
        this.hellRNG = new Random(seed);
        this.netherNoiseGen1 = new NoiseGeneratorOctaves(this.hellRNG, 16);
        this.netherNoiseGen2 = new NoiseGeneratorOctaves(this.hellRNG, 16);
        this.netherNoiseGen3 = new NoiseGeneratorOctaves(this.hellRNG, 8);
        this.slowsandGravelNoiseGen = new NoiseGeneratorOctaves(this.hellRNG, 4);
        this.netherrackExculsivityNoiseGen = new NoiseGeneratorOctaves(this.hellRNG, 4);
        this.netherNoiseGen6 = new NoiseGeneratorOctaves(this.hellRNG, 10);
        this.netherNoiseGen7 = new NoiseGeneratorOctaves(this.hellRNG, 16);
        worldIn.setSeaLevel(63);
    }

    public void func_180515_a(int p_180515_1_, int p_180515_2_, ChunkPrimer p_180515_3_)
    {
        int lvt_4_1_ = 4;
        int lvt_5_1_ = this.worldObj.getSeaLevel() / 2 + 1;
        int lvt_6_1_ = lvt_4_1_ + 1;
        int lvt_7_1_ = 17;
        int lvt_8_1_ = lvt_4_1_ + 1;
        this.noiseField = this.initializeNoiseField(this.noiseField, p_180515_1_ * lvt_4_1_, 0, p_180515_2_ * lvt_4_1_, lvt_6_1_, lvt_7_1_, lvt_8_1_);

        for (int lvt_9_1_ = 0; lvt_9_1_ < lvt_4_1_; ++lvt_9_1_)
        {
            for (int lvt_10_1_ = 0; lvt_10_1_ < lvt_4_1_; ++lvt_10_1_)
            {
                for (int lvt_11_1_ = 0; lvt_11_1_ < 16; ++lvt_11_1_)
                {
                    double lvt_12_1_ = 0.125D;
                    double lvt_14_1_ = this.noiseField[((lvt_9_1_ + 0) * lvt_8_1_ + lvt_10_1_ + 0) * lvt_7_1_ + lvt_11_1_ + 0];
                    double lvt_16_1_ = this.noiseField[((lvt_9_1_ + 0) * lvt_8_1_ + lvt_10_1_ + 1) * lvt_7_1_ + lvt_11_1_ + 0];
                    double lvt_18_1_ = this.noiseField[((lvt_9_1_ + 1) * lvt_8_1_ + lvt_10_1_ + 0) * lvt_7_1_ + lvt_11_1_ + 0];
                    double lvt_20_1_ = this.noiseField[((lvt_9_1_ + 1) * lvt_8_1_ + lvt_10_1_ + 1) * lvt_7_1_ + lvt_11_1_ + 0];
                    double lvt_22_1_ = (this.noiseField[((lvt_9_1_ + 0) * lvt_8_1_ + lvt_10_1_ + 0) * lvt_7_1_ + lvt_11_1_ + 1] - lvt_14_1_) * lvt_12_1_;
                    double lvt_24_1_ = (this.noiseField[((lvt_9_1_ + 0) * lvt_8_1_ + lvt_10_1_ + 1) * lvt_7_1_ + lvt_11_1_ + 1] - lvt_16_1_) * lvt_12_1_;
                    double lvt_26_1_ = (this.noiseField[((lvt_9_1_ + 1) * lvt_8_1_ + lvt_10_1_ + 0) * lvt_7_1_ + lvt_11_1_ + 1] - lvt_18_1_) * lvt_12_1_;
                    double lvt_28_1_ = (this.noiseField[((lvt_9_1_ + 1) * lvt_8_1_ + lvt_10_1_ + 1) * lvt_7_1_ + lvt_11_1_ + 1] - lvt_20_1_) * lvt_12_1_;

                    for (int lvt_30_1_ = 0; lvt_30_1_ < 8; ++lvt_30_1_)
                    {
                        double lvt_31_1_ = 0.25D;
                        double lvt_33_1_ = lvt_14_1_;
                        double lvt_35_1_ = lvt_16_1_;
                        double lvt_37_1_ = (lvt_18_1_ - lvt_14_1_) * lvt_31_1_;
                        double lvt_39_1_ = (lvt_20_1_ - lvt_16_1_) * lvt_31_1_;

                        for (int lvt_41_1_ = 0; lvt_41_1_ < 4; ++lvt_41_1_)
                        {
                            double lvt_42_1_ = 0.25D;
                            double lvt_44_1_ = lvt_33_1_;
                            double lvt_46_1_ = (lvt_35_1_ - lvt_33_1_) * lvt_42_1_;

                            for (int lvt_48_1_ = 0; lvt_48_1_ < 4; ++lvt_48_1_)
                            {
                                IBlockState lvt_49_1_ = null;

                                if (lvt_11_1_ * 8 + lvt_30_1_ < lvt_5_1_)
                                {
                                    lvt_49_1_ = Blocks.lava.getDefaultState();
                                }

                                if (lvt_44_1_ > 0.0D)
                                {
                                    lvt_49_1_ = Blocks.netherrack.getDefaultState();
                                }

                                int lvt_50_1_ = lvt_41_1_ + lvt_9_1_ * 4;
                                int lvt_51_1_ = lvt_30_1_ + lvt_11_1_ * 8;
                                int lvt_52_1_ = lvt_48_1_ + lvt_10_1_ * 4;
                                p_180515_3_.setBlockState(lvt_50_1_, lvt_51_1_, lvt_52_1_, lvt_49_1_);
                                lvt_44_1_ += lvt_46_1_;
                            }

                            lvt_33_1_ += lvt_37_1_;
                            lvt_35_1_ += lvt_39_1_;
                        }

                        lvt_14_1_ += lvt_22_1_;
                        lvt_16_1_ += lvt_24_1_;
                        lvt_18_1_ += lvt_26_1_;
                        lvt_20_1_ += lvt_28_1_;
                    }
                }
            }
        }
    }

    public void func_180516_b(int p_180516_1_, int p_180516_2_, ChunkPrimer p_180516_3_)
    {
        int lvt_4_1_ = this.worldObj.getSeaLevel() + 1;
        double lvt_5_1_ = 0.03125D;
        this.slowsandNoise = this.slowsandGravelNoiseGen.generateNoiseOctaves(this.slowsandNoise, p_180516_1_ * 16, p_180516_2_ * 16, 0, 16, 16, 1, lvt_5_1_, lvt_5_1_, 1.0D);
        this.gravelNoise = this.slowsandGravelNoiseGen.generateNoiseOctaves(this.gravelNoise, p_180516_1_ * 16, 109, p_180516_2_ * 16, 16, 1, 16, lvt_5_1_, 1.0D, lvt_5_1_);
        this.netherrackExclusivityNoise = this.netherrackExculsivityNoiseGen.generateNoiseOctaves(this.netherrackExclusivityNoise, p_180516_1_ * 16, p_180516_2_ * 16, 0, 16, 16, 1, lvt_5_1_ * 2.0D, lvt_5_1_ * 2.0D, lvt_5_1_ * 2.0D);

        for (int lvt_7_1_ = 0; lvt_7_1_ < 16; ++lvt_7_1_)
        {
            for (int lvt_8_1_ = 0; lvt_8_1_ < 16; ++lvt_8_1_)
            {
                boolean lvt_9_1_ = this.slowsandNoise[lvt_7_1_ + lvt_8_1_ * 16] + this.hellRNG.nextDouble() * 0.2D > 0.0D;
                boolean lvt_10_1_ = this.gravelNoise[lvt_7_1_ + lvt_8_1_ * 16] + this.hellRNG.nextDouble() * 0.2D > 0.0D;
                int lvt_11_1_ = (int)(this.netherrackExclusivityNoise[lvt_7_1_ + lvt_8_1_ * 16] / 3.0D + 3.0D + this.hellRNG.nextDouble() * 0.25D);
                int lvt_12_1_ = -1;
                IBlockState lvt_13_1_ = Blocks.netherrack.getDefaultState();
                IBlockState lvt_14_1_ = Blocks.netherrack.getDefaultState();

                for (int lvt_15_1_ = 127; lvt_15_1_ >= 0; --lvt_15_1_)
                {
                    if (lvt_15_1_ < 127 - this.hellRNG.nextInt(5) && lvt_15_1_ > this.hellRNG.nextInt(5))
                    {
                        IBlockState lvt_16_1_ = p_180516_3_.getBlockState(lvt_8_1_, lvt_15_1_, lvt_7_1_);

                        if (lvt_16_1_.getBlock() != null && lvt_16_1_.getBlock().getMaterial() != Material.air)
                        {
                            if (lvt_16_1_.getBlock() == Blocks.netherrack)
                            {
                                if (lvt_12_1_ == -1)
                                {
                                    if (lvt_11_1_ <= 0)
                                    {
                                        lvt_13_1_ = null;
                                        lvt_14_1_ = Blocks.netherrack.getDefaultState();
                                    }
                                    else if (lvt_15_1_ >= lvt_4_1_ - 4 && lvt_15_1_ <= lvt_4_1_ + 1)
                                    {
                                        lvt_13_1_ = Blocks.netherrack.getDefaultState();
                                        lvt_14_1_ = Blocks.netherrack.getDefaultState();

                                        if (lvt_10_1_)
                                        {
                                            lvt_13_1_ = Blocks.gravel.getDefaultState();
                                            lvt_14_1_ = Blocks.netherrack.getDefaultState();
                                        }

                                        if (lvt_9_1_)
                                        {
                                            lvt_13_1_ = Blocks.soul_sand.getDefaultState();
                                            lvt_14_1_ = Blocks.soul_sand.getDefaultState();
                                        }
                                    }

                                    if (lvt_15_1_ < lvt_4_1_ && (lvt_13_1_ == null || lvt_13_1_.getBlock().getMaterial() == Material.air))
                                    {
                                        lvt_13_1_ = Blocks.lava.getDefaultState();
                                    }

                                    lvt_12_1_ = lvt_11_1_;

                                    if (lvt_15_1_ >= lvt_4_1_ - 1)
                                    {
                                        p_180516_3_.setBlockState(lvt_8_1_, lvt_15_1_, lvt_7_1_, lvt_13_1_);
                                    }
                                    else
                                    {
                                        p_180516_3_.setBlockState(lvt_8_1_, lvt_15_1_, lvt_7_1_, lvt_14_1_);
                                    }
                                }
                                else if (lvt_12_1_ > 0)
                                {
                                    --lvt_12_1_;
                                    p_180516_3_.setBlockState(lvt_8_1_, lvt_15_1_, lvt_7_1_, lvt_14_1_);
                                }
                            }
                        }
                        else
                        {
                            lvt_12_1_ = -1;
                        }
                    }
                    else
                    {
                        p_180516_3_.setBlockState(lvt_8_1_, lvt_15_1_, lvt_7_1_, Blocks.bedrock.getDefaultState());
                    }
                }
            }
        }
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    public Chunk provideChunk(int x, int z)
    {
        this.hellRNG.setSeed((long)x * 341873128712L + (long)z * 132897987541L);
        ChunkPrimer lvt_3_1_ = new ChunkPrimer();
        this.func_180515_a(x, z, lvt_3_1_);
        this.func_180516_b(x, z, lvt_3_1_);
        this.netherCaveGenerator.generate(this, this.worldObj, x, z, lvt_3_1_);

        if (this.field_177466_i)
        {
            this.genNetherBridge.generate(this, this.worldObj, x, z, lvt_3_1_);
        }

        Chunk lvt_4_1_ = new Chunk(this.worldObj, lvt_3_1_, x, z);
        BiomeGenBase[] lvt_5_1_ = this.worldObj.getWorldChunkManager().loadBlockGeneratorData((BiomeGenBase[])null, x * 16, z * 16, 16, 16);
        byte[] lvt_6_1_ = lvt_4_1_.getBiomeArray();

        for (int lvt_7_1_ = 0; lvt_7_1_ < lvt_6_1_.length; ++lvt_7_1_)
        {
            lvt_6_1_[lvt_7_1_] = (byte)lvt_5_1_[lvt_7_1_].biomeID;
        }

        lvt_4_1_.resetRelightChecks();
        return lvt_4_1_;
    }

    /**
     * generates a subset of the level's terrain data. Takes 7 arguments: the [empty] noise array, the position, and the
     * size.
     */
    private double[] initializeNoiseField(double[] p_73164_1_, int p_73164_2_, int p_73164_3_, int p_73164_4_, int p_73164_5_, int p_73164_6_, int p_73164_7_)
    {
        if (p_73164_1_ == null)
        {
            p_73164_1_ = new double[p_73164_5_ * p_73164_6_ * p_73164_7_];
        }

        double lvt_8_1_ = 684.412D;
        double lvt_10_1_ = 2053.236D;
        this.noiseData4 = this.netherNoiseGen6.generateNoiseOctaves(this.noiseData4, p_73164_2_, p_73164_3_, p_73164_4_, p_73164_5_, 1, p_73164_7_, 1.0D, 0.0D, 1.0D);
        this.noiseData5 = this.netherNoiseGen7.generateNoiseOctaves(this.noiseData5, p_73164_2_, p_73164_3_, p_73164_4_, p_73164_5_, 1, p_73164_7_, 100.0D, 0.0D, 100.0D);
        this.noiseData1 = this.netherNoiseGen3.generateNoiseOctaves(this.noiseData1, p_73164_2_, p_73164_3_, p_73164_4_, p_73164_5_, p_73164_6_, p_73164_7_, lvt_8_1_ / 80.0D, lvt_10_1_ / 60.0D, lvt_8_1_ / 80.0D);
        this.noiseData2 = this.netherNoiseGen1.generateNoiseOctaves(this.noiseData2, p_73164_2_, p_73164_3_, p_73164_4_, p_73164_5_, p_73164_6_, p_73164_7_, lvt_8_1_, lvt_10_1_, lvt_8_1_);
        this.noiseData3 = this.netherNoiseGen2.generateNoiseOctaves(this.noiseData3, p_73164_2_, p_73164_3_, p_73164_4_, p_73164_5_, p_73164_6_, p_73164_7_, lvt_8_1_, lvt_10_1_, lvt_8_1_);
        int lvt_12_1_ = 0;
        double[] lvt_13_1_ = new double[p_73164_6_];

        for (int lvt_14_1_ = 0; lvt_14_1_ < p_73164_6_; ++lvt_14_1_)
        {
            lvt_13_1_[lvt_14_1_] = Math.cos((double)lvt_14_1_ * Math.PI * 6.0D / (double)p_73164_6_) * 2.0D;
            double lvt_15_1_ = (double)lvt_14_1_;

            if (lvt_14_1_ > p_73164_6_ / 2)
            {
                lvt_15_1_ = (double)(p_73164_6_ - 1 - lvt_14_1_);
            }

            if (lvt_15_1_ < 4.0D)
            {
                lvt_15_1_ = 4.0D - lvt_15_1_;
                lvt_13_1_[lvt_14_1_] -= lvt_15_1_ * lvt_15_1_ * lvt_15_1_ * 10.0D;
            }
        }

        for (int lvt_14_2_ = 0; lvt_14_2_ < p_73164_5_; ++lvt_14_2_)
        {
            for (int lvt_15_2_ = 0; lvt_15_2_ < p_73164_7_; ++lvt_15_2_)
            {
                double lvt_16_1_ = 0.0D;

                for (int lvt_18_1_ = 0; lvt_18_1_ < p_73164_6_; ++lvt_18_1_)
                {
                    double lvt_19_1_ = 0.0D;
                    double lvt_21_1_ = lvt_13_1_[lvt_18_1_];
                    double lvt_23_1_ = this.noiseData2[lvt_12_1_] / 512.0D;
                    double lvt_25_1_ = this.noiseData3[lvt_12_1_] / 512.0D;
                    double lvt_27_1_ = (this.noiseData1[lvt_12_1_] / 10.0D + 1.0D) / 2.0D;

                    if (lvt_27_1_ < 0.0D)
                    {
                        lvt_19_1_ = lvt_23_1_;
                    }
                    else if (lvt_27_1_ > 1.0D)
                    {
                        lvt_19_1_ = lvt_25_1_;
                    }
                    else
                    {
                        lvt_19_1_ = lvt_23_1_ + (lvt_25_1_ - lvt_23_1_) * lvt_27_1_;
                    }

                    lvt_19_1_ = lvt_19_1_ - lvt_21_1_;

                    if (lvt_18_1_ > p_73164_6_ - 4)
                    {
                        double lvt_29_1_ = (double)((float)(lvt_18_1_ - (p_73164_6_ - 4)) / 3.0F);
                        lvt_19_1_ = lvt_19_1_ * (1.0D - lvt_29_1_) + -10.0D * lvt_29_1_;
                    }

                    if ((double)lvt_18_1_ < lvt_16_1_)
                    {
                        double lvt_29_2_ = (lvt_16_1_ - (double)lvt_18_1_) / 4.0D;
                        lvt_29_2_ = MathHelper.clamp_double(lvt_29_2_, 0.0D, 1.0D);
                        lvt_19_1_ = lvt_19_1_ * (1.0D - lvt_29_2_) + -10.0D * lvt_29_2_;
                    }

                    p_73164_1_[lvt_12_1_] = lvt_19_1_;
                    ++lvt_12_1_;
                }
            }
        }

        return p_73164_1_;
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
        BlockPos lvt_4_1_ = new BlockPos(x * 16, 0, z * 16);
        ChunkCoordIntPair lvt_5_1_ = new ChunkCoordIntPair(x, z);
        this.genNetherBridge.generateStructure(this.worldObj, this.hellRNG, lvt_5_1_);

        for (int lvt_6_1_ = 0; lvt_6_1_ < 8; ++lvt_6_1_)
        {
            this.field_177472_y.generate(this.worldObj, this.hellRNG, lvt_4_1_.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(120) + 4, this.hellRNG.nextInt(16) + 8));
        }

        for (int lvt_6_2_ = 0; lvt_6_2_ < this.hellRNG.nextInt(this.hellRNG.nextInt(10) + 1) + 1; ++lvt_6_2_)
        {
            this.field_177470_t.generate(this.worldObj, this.hellRNG, lvt_4_1_.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(120) + 4, this.hellRNG.nextInt(16) + 8));
        }

        for (int lvt_6_3_ = 0; lvt_6_3_ < this.hellRNG.nextInt(this.hellRNG.nextInt(10) + 1); ++lvt_6_3_)
        {
            this.field_177469_u.generate(this.worldObj, this.hellRNG, lvt_4_1_.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(120) + 4, this.hellRNG.nextInt(16) + 8));
        }

        for (int lvt_6_4_ = 0; lvt_6_4_ < 10; ++lvt_6_4_)
        {
            this.field_177468_v.generate(this.worldObj, this.hellRNG, lvt_4_1_.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(128), this.hellRNG.nextInt(16) + 8));
        }

        if (this.hellRNG.nextBoolean())
        {
            this.field_177471_z.generate(this.worldObj, this.hellRNG, lvt_4_1_.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(128), this.hellRNG.nextInt(16) + 8));
        }

        if (this.hellRNG.nextBoolean())
        {
            this.field_177465_A.generate(this.worldObj, this.hellRNG, lvt_4_1_.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(128), this.hellRNG.nextInt(16) + 8));
        }

        for (int lvt_6_5_ = 0; lvt_6_5_ < 16; ++lvt_6_5_)
        {
            this.field_177467_w.generate(this.worldObj, this.hellRNG, lvt_4_1_.add(this.hellRNG.nextInt(16), this.hellRNG.nextInt(108) + 10, this.hellRNG.nextInt(16)));
        }

        for (int lvt_6_6_ = 0; lvt_6_6_ < 16; ++lvt_6_6_)
        {
            this.field_177473_x.generate(this.worldObj, this.hellRNG, lvt_4_1_.add(this.hellRNG.nextInt(16), this.hellRNG.nextInt(108) + 10, this.hellRNG.nextInt(16)));
        }

        BlockFalling.fallInstantly = false;
    }

    public boolean populateChunk(IChunkProvider chunkProvider, Chunk chunkIn, int x, int z)
    {
        return false;
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
        return "HellRandomLevelSource";
    }

    public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
    {
        if (creatureType == EnumCreatureType.MONSTER)
        {
            if (this.genNetherBridge.func_175795_b(pos))
            {
                return this.genNetherBridge.getSpawnList();
            }

            if (this.genNetherBridge.isPositionInStructure(this.worldObj, pos) && this.worldObj.getBlockState(pos.down()).getBlock() == Blocks.nether_brick)
            {
                return this.genNetherBridge.getSpawnList();
            }
        }

        BiomeGenBase lvt_3_1_ = this.worldObj.getBiomeGenForCoords(pos);
        return lvt_3_1_.getSpawnableList(creatureType);
    }

    public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position)
    {
        return null;
    }

    public int getLoadedChunkCount()
    {
        return 0;
    }

    public void recreateStructures(Chunk chunkIn, int x, int z)
    {
        this.genNetherBridge.generate(this, this.worldObj, x, z, (ChunkPrimer)null);
    }

    public Chunk provideChunk(BlockPos blockPosIn)
    {
        return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
    }
}
