package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;

public class ChunkProviderEnd implements IChunkProvider
{
    private Random endRNG;
    private NoiseGeneratorOctaves noiseGen1;
    private NoiseGeneratorOctaves noiseGen2;
    private NoiseGeneratorOctaves noiseGen3;
    public NoiseGeneratorOctaves noiseGen4;
    public NoiseGeneratorOctaves noiseGen5;
    private World endWorld;
    private double[] densities;

    /** The biomes that are used to generate the chunk */
    private BiomeGenBase[] biomesForGeneration;
    double[] noiseData1;
    double[] noiseData2;
    double[] noiseData3;
    double[] noiseData4;
    double[] noiseData5;

    public ChunkProviderEnd(World worldIn, long seed)
    {
        this.endWorld = worldIn;
        this.endRNG = new Random(seed);
        this.noiseGen1 = new NoiseGeneratorOctaves(this.endRNG, 16);
        this.noiseGen2 = new NoiseGeneratorOctaves(this.endRNG, 16);
        this.noiseGen3 = new NoiseGeneratorOctaves(this.endRNG, 8);
        this.noiseGen4 = new NoiseGeneratorOctaves(this.endRNG, 10);
        this.noiseGen5 = new NoiseGeneratorOctaves(this.endRNG, 16);
    }

    public void func_180520_a(int p_180520_1_, int p_180520_2_, ChunkPrimer p_180520_3_)
    {
        int lvt_4_1_ = 2;
        int lvt_5_1_ = lvt_4_1_ + 1;
        int lvt_6_1_ = 33;
        int lvt_7_1_ = lvt_4_1_ + 1;
        this.densities = this.initializeNoiseField(this.densities, p_180520_1_ * lvt_4_1_, 0, p_180520_2_ * lvt_4_1_, lvt_5_1_, lvt_6_1_, lvt_7_1_);

        for (int lvt_8_1_ = 0; lvt_8_1_ < lvt_4_1_; ++lvt_8_1_)
        {
            for (int lvt_9_1_ = 0; lvt_9_1_ < lvt_4_1_; ++lvt_9_1_)
            {
                for (int lvt_10_1_ = 0; lvt_10_1_ < 32; ++lvt_10_1_)
                {
                    double lvt_11_1_ = 0.25D;
                    double lvt_13_1_ = this.densities[((lvt_8_1_ + 0) * lvt_7_1_ + lvt_9_1_ + 0) * lvt_6_1_ + lvt_10_1_ + 0];
                    double lvt_15_1_ = this.densities[((lvt_8_1_ + 0) * lvt_7_1_ + lvt_9_1_ + 1) * lvt_6_1_ + lvt_10_1_ + 0];
                    double lvt_17_1_ = this.densities[((lvt_8_1_ + 1) * lvt_7_1_ + lvt_9_1_ + 0) * lvt_6_1_ + lvt_10_1_ + 0];
                    double lvt_19_1_ = this.densities[((lvt_8_1_ + 1) * lvt_7_1_ + lvt_9_1_ + 1) * lvt_6_1_ + lvt_10_1_ + 0];
                    double lvt_21_1_ = (this.densities[((lvt_8_1_ + 0) * lvt_7_1_ + lvt_9_1_ + 0) * lvt_6_1_ + lvt_10_1_ + 1] - lvt_13_1_) * lvt_11_1_;
                    double lvt_23_1_ = (this.densities[((lvt_8_1_ + 0) * lvt_7_1_ + lvt_9_1_ + 1) * lvt_6_1_ + lvt_10_1_ + 1] - lvt_15_1_) * lvt_11_1_;
                    double lvt_25_1_ = (this.densities[((lvt_8_1_ + 1) * lvt_7_1_ + lvt_9_1_ + 0) * lvt_6_1_ + lvt_10_1_ + 1] - lvt_17_1_) * lvt_11_1_;
                    double lvt_27_1_ = (this.densities[((lvt_8_1_ + 1) * lvt_7_1_ + lvt_9_1_ + 1) * lvt_6_1_ + lvt_10_1_ + 1] - lvt_19_1_) * lvt_11_1_;

                    for (int lvt_29_1_ = 0; lvt_29_1_ < 4; ++lvt_29_1_)
                    {
                        double lvt_30_1_ = 0.125D;
                        double lvt_32_1_ = lvt_13_1_;
                        double lvt_34_1_ = lvt_15_1_;
                        double lvt_36_1_ = (lvt_17_1_ - lvt_13_1_) * lvt_30_1_;
                        double lvt_38_1_ = (lvt_19_1_ - lvt_15_1_) * lvt_30_1_;

                        for (int lvt_40_1_ = 0; lvt_40_1_ < 8; ++lvt_40_1_)
                        {
                            double lvt_41_1_ = 0.125D;
                            double lvt_43_1_ = lvt_32_1_;
                            double lvt_45_1_ = (lvt_34_1_ - lvt_32_1_) * lvt_41_1_;

                            for (int lvt_47_1_ = 0; lvt_47_1_ < 8; ++lvt_47_1_)
                            {
                                IBlockState lvt_48_1_ = null;

                                if (lvt_43_1_ > 0.0D)
                                {
                                    lvt_48_1_ = Blocks.end_stone.getDefaultState();
                                }

                                int lvt_49_1_ = lvt_40_1_ + lvt_8_1_ * 8;
                                int lvt_50_1_ = lvt_29_1_ + lvt_10_1_ * 4;
                                int lvt_51_1_ = lvt_47_1_ + lvt_9_1_ * 8;
                                p_180520_3_.setBlockState(lvt_49_1_, lvt_50_1_, lvt_51_1_, lvt_48_1_);
                                lvt_43_1_ += lvt_45_1_;
                            }

                            lvt_32_1_ += lvt_36_1_;
                            lvt_34_1_ += lvt_38_1_;
                        }

                        lvt_13_1_ += lvt_21_1_;
                        lvt_15_1_ += lvt_23_1_;
                        lvt_17_1_ += lvt_25_1_;
                        lvt_19_1_ += lvt_27_1_;
                    }
                }
            }
        }
    }

    public void func_180519_a(ChunkPrimer p_180519_1_)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < 16; ++lvt_2_1_)
        {
            for (int lvt_3_1_ = 0; lvt_3_1_ < 16; ++lvt_3_1_)
            {
                int lvt_4_1_ = 1;
                int lvt_5_1_ = -1;
                IBlockState lvt_6_1_ = Blocks.end_stone.getDefaultState();
                IBlockState lvt_7_1_ = Blocks.end_stone.getDefaultState();

                for (int lvt_8_1_ = 127; lvt_8_1_ >= 0; --lvt_8_1_)
                {
                    IBlockState lvt_9_1_ = p_180519_1_.getBlockState(lvt_2_1_, lvt_8_1_, lvt_3_1_);

                    if (lvt_9_1_.getBlock().getMaterial() == Material.air)
                    {
                        lvt_5_1_ = -1;
                    }
                    else if (lvt_9_1_.getBlock() == Blocks.stone)
                    {
                        if (lvt_5_1_ == -1)
                        {
                            if (lvt_4_1_ <= 0)
                            {
                                lvt_6_1_ = Blocks.air.getDefaultState();
                                lvt_7_1_ = Blocks.end_stone.getDefaultState();
                            }

                            lvt_5_1_ = lvt_4_1_;

                            if (lvt_8_1_ >= 0)
                            {
                                p_180519_1_.setBlockState(lvt_2_1_, lvt_8_1_, lvt_3_1_, lvt_6_1_);
                            }
                            else
                            {
                                p_180519_1_.setBlockState(lvt_2_1_, lvt_8_1_, lvt_3_1_, lvt_7_1_);
                            }
                        }
                        else if (lvt_5_1_ > 0)
                        {
                            --lvt_5_1_;
                            p_180519_1_.setBlockState(lvt_2_1_, lvt_8_1_, lvt_3_1_, lvt_7_1_);
                        }
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
        this.endRNG.setSeed((long)x * 341873128712L + (long)z * 132897987541L);
        ChunkPrimer lvt_3_1_ = new ChunkPrimer();
        this.biomesForGeneration = this.endWorld.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, x * 16, z * 16, 16, 16);
        this.func_180520_a(x, z, lvt_3_1_);
        this.func_180519_a(lvt_3_1_);
        Chunk lvt_4_1_ = new Chunk(this.endWorld, lvt_3_1_, x, z);
        byte[] lvt_5_1_ = lvt_4_1_.getBiomeArray();

        for (int lvt_6_1_ = 0; lvt_6_1_ < lvt_5_1_.length; ++lvt_6_1_)
        {
            lvt_5_1_[lvt_6_1_] = (byte)this.biomesForGeneration[lvt_6_1_].biomeID;
        }

        lvt_4_1_.generateSkylightMap();
        return lvt_4_1_;
    }

    /**
     * generates a subset of the level's terrain data. Takes 7 arguments: the [empty] noise array, the position, and the
     * size.
     */
    private double[] initializeNoiseField(double[] p_73187_1_, int p_73187_2_, int p_73187_3_, int p_73187_4_, int p_73187_5_, int p_73187_6_, int p_73187_7_)
    {
        if (p_73187_1_ == null)
        {
            p_73187_1_ = new double[p_73187_5_ * p_73187_6_ * p_73187_7_];
        }

        double lvt_8_1_ = 684.412D;
        double lvt_10_1_ = 684.412D;
        this.noiseData4 = this.noiseGen4.generateNoiseOctaves(this.noiseData4, p_73187_2_, p_73187_4_, p_73187_5_, p_73187_7_, 1.121D, 1.121D, 0.5D);
        this.noiseData5 = this.noiseGen5.generateNoiseOctaves(this.noiseData5, p_73187_2_, p_73187_4_, p_73187_5_, p_73187_7_, 200.0D, 200.0D, 0.5D);
        lvt_8_1_ = lvt_8_1_ * 2.0D;
        this.noiseData1 = this.noiseGen3.generateNoiseOctaves(this.noiseData1, p_73187_2_, p_73187_3_, p_73187_4_, p_73187_5_, p_73187_6_, p_73187_7_, lvt_8_1_ / 80.0D, lvt_10_1_ / 160.0D, lvt_8_1_ / 80.0D);
        this.noiseData2 = this.noiseGen1.generateNoiseOctaves(this.noiseData2, p_73187_2_, p_73187_3_, p_73187_4_, p_73187_5_, p_73187_6_, p_73187_7_, lvt_8_1_, lvt_10_1_, lvt_8_1_);
        this.noiseData3 = this.noiseGen2.generateNoiseOctaves(this.noiseData3, p_73187_2_, p_73187_3_, p_73187_4_, p_73187_5_, p_73187_6_, p_73187_7_, lvt_8_1_, lvt_10_1_, lvt_8_1_);
        int lvt_12_1_ = 0;

        for (int lvt_13_1_ = 0; lvt_13_1_ < p_73187_5_; ++lvt_13_1_)
        {
            for (int lvt_14_1_ = 0; lvt_14_1_ < p_73187_7_; ++lvt_14_1_)
            {
                float lvt_15_1_ = (float)(lvt_13_1_ + p_73187_2_) / 1.0F;
                float lvt_16_1_ = (float)(lvt_14_1_ + p_73187_4_) / 1.0F;
                float lvt_17_1_ = 100.0F - MathHelper.sqrt_float(lvt_15_1_ * lvt_15_1_ + lvt_16_1_ * lvt_16_1_) * 8.0F;

                if (lvt_17_1_ > 80.0F)
                {
                    lvt_17_1_ = 80.0F;
                }

                if (lvt_17_1_ < -100.0F)
                {
                    lvt_17_1_ = -100.0F;
                }

                for (int lvt_18_1_ = 0; lvt_18_1_ < p_73187_6_; ++lvt_18_1_)
                {
                    double lvt_19_1_ = 0.0D;
                    double lvt_21_1_ = this.noiseData2[lvt_12_1_] / 512.0D;
                    double lvt_23_1_ = this.noiseData3[lvt_12_1_] / 512.0D;
                    double lvt_25_1_ = (this.noiseData1[lvt_12_1_] / 10.0D + 1.0D) / 2.0D;

                    if (lvt_25_1_ < 0.0D)
                    {
                        lvt_19_1_ = lvt_21_1_;
                    }
                    else if (lvt_25_1_ > 1.0D)
                    {
                        lvt_19_1_ = lvt_23_1_;
                    }
                    else
                    {
                        lvt_19_1_ = lvt_21_1_ + (lvt_23_1_ - lvt_21_1_) * lvt_25_1_;
                    }

                    lvt_19_1_ = lvt_19_1_ - 8.0D;
                    lvt_19_1_ = lvt_19_1_ + (double)lvt_17_1_;
                    int lvt_27_1_ = 2;

                    if (lvt_18_1_ > p_73187_6_ / 2 - lvt_27_1_)
                    {
                        double lvt_28_1_ = (double)((float)(lvt_18_1_ - (p_73187_6_ / 2 - lvt_27_1_)) / 64.0F);
                        lvt_28_1_ = MathHelper.clamp_double(lvt_28_1_, 0.0D, 1.0D);
                        lvt_19_1_ = lvt_19_1_ * (1.0D - lvt_28_1_) + -3000.0D * lvt_28_1_;
                    }

                    lvt_27_1_ = 8;

                    if (lvt_18_1_ < lvt_27_1_)
                    {
                        double lvt_28_2_ = (double)((float)(lvt_27_1_ - lvt_18_1_) / ((float)lvt_27_1_ - 1.0F));
                        lvt_19_1_ = lvt_19_1_ * (1.0D - lvt_28_2_) + -30.0D * lvt_28_2_;
                    }

                    p_73187_1_[lvt_12_1_] = lvt_19_1_;
                    ++lvt_12_1_;
                }
            }
        }

        return p_73187_1_;
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
        this.endWorld.getBiomeGenForCoords(lvt_4_1_.add(16, 0, 16)).decorate(this.endWorld, this.endWorld.rand, lvt_4_1_);
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
        return "RandomLevelSource";
    }

    public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
    {
        return this.endWorld.getBiomeGenForCoords(pos).getSpawnableList(creatureType);
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
    }

    public Chunk provideChunk(BlockPos blockPosIn)
    {
        return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
    }
}
