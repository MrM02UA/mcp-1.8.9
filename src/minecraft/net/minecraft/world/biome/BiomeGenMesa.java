package net.minecraft.world.biome;

import java.util.Arrays;
import java.util.Random;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class BiomeGenMesa extends BiomeGenBase
{
    private IBlockState[] field_150621_aC;
    private long field_150622_aD;
    private NoiseGeneratorPerlin field_150623_aE;
    private NoiseGeneratorPerlin field_150624_aF;
    private NoiseGeneratorPerlin field_150625_aG;
    private boolean field_150626_aH;
    private boolean field_150620_aI;

    public BiomeGenMesa(int id, boolean p_i45380_2_, boolean p_i45380_3_)
    {
        super(id);
        this.field_150626_aH = p_i45380_2_;
        this.field_150620_aI = p_i45380_3_;
        this.setDisableRain();
        this.setTemperatureRainfall(2.0F, 0.0F);
        this.spawnableCreatureList.clear();
        this.topBlock = Blocks.sand.getDefaultState().withProperty(BlockSand.VARIANT, BlockSand.EnumType.RED_SAND);
        this.fillerBlock = Blocks.stained_hardened_clay.getDefaultState();
        this.theBiomeDecorator.treesPerChunk = -999;
        this.theBiomeDecorator.deadBushPerChunk = 20;
        this.theBiomeDecorator.reedsPerChunk = 3;
        this.theBiomeDecorator.cactiPerChunk = 5;
        this.theBiomeDecorator.flowersPerChunk = 0;
        this.spawnableCreatureList.clear();

        if (p_i45380_3_)
        {
            this.theBiomeDecorator.treesPerChunk = 5;
        }
    }

    public WorldGenAbstractTree genBigTreeChance(Random rand)
    {
        return this.worldGeneratorTrees;
    }

    public int getFoliageColorAtPos(BlockPos pos)
    {
        return 10387789;
    }

    public int getGrassColorAtPos(BlockPos pos)
    {
        return 9470285;
    }

    public void decorate(World worldIn, Random rand, BlockPos pos)
    {
        super.decorate(worldIn, rand, pos);
    }

    public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal)
    {
        if (this.field_150621_aC == null || this.field_150622_aD != worldIn.getSeed())
        {
            this.func_150619_a(worldIn.getSeed());
        }

        if (this.field_150623_aE == null || this.field_150624_aF == null || this.field_150622_aD != worldIn.getSeed())
        {
            Random lvt_8_1_ = new Random(this.field_150622_aD);
            this.field_150623_aE = new NoiseGeneratorPerlin(lvt_8_1_, 4);
            this.field_150624_aF = new NoiseGeneratorPerlin(lvt_8_1_, 1);
        }

        this.field_150622_aD = worldIn.getSeed();
        double lvt_8_2_ = 0.0D;

        if (this.field_150626_aH)
        {
            int lvt_10_1_ = (x & -16) + (z & 15);
            int lvt_11_1_ = (z & -16) + (x & 15);
            double lvt_12_1_ = Math.min(Math.abs(noiseVal), this.field_150623_aE.func_151601_a((double)lvt_10_1_ * 0.25D, (double)lvt_11_1_ * 0.25D));

            if (lvt_12_1_ > 0.0D)
            {
                double lvt_14_1_ = 0.001953125D;
                double lvt_16_1_ = Math.abs(this.field_150624_aF.func_151601_a((double)lvt_10_1_ * lvt_14_1_, (double)lvt_11_1_ * lvt_14_1_));
                lvt_8_2_ = lvt_12_1_ * lvt_12_1_ * 2.5D;
                double lvt_18_1_ = Math.ceil(lvt_16_1_ * 50.0D) + 14.0D;

                if (lvt_8_2_ > lvt_18_1_)
                {
                    lvt_8_2_ = lvt_18_1_;
                }

                lvt_8_2_ = lvt_8_2_ + 64.0D;
            }
        }

        int lvt_10_2_ = x & 15;
        int lvt_11_2_ = z & 15;
        int lvt_12_2_ = worldIn.getSeaLevel();
        IBlockState lvt_13_1_ = Blocks.stained_hardened_clay.getDefaultState();
        IBlockState lvt_14_2_ = this.fillerBlock;
        int lvt_15_1_ = (int)(noiseVal / 3.0D + 3.0D + rand.nextDouble() * 0.25D);
        boolean lvt_16_2_ = Math.cos(noiseVal / 3.0D * Math.PI) > 0.0D;
        int lvt_17_1_ = -1;
        boolean lvt_18_2_ = false;

        for (int lvt_19_1_ = 255; lvt_19_1_ >= 0; --lvt_19_1_)
        {
            if (chunkPrimerIn.getBlockState(lvt_11_2_, lvt_19_1_, lvt_10_2_).getBlock().getMaterial() == Material.air && lvt_19_1_ < (int)lvt_8_2_)
            {
                chunkPrimerIn.setBlockState(lvt_11_2_, lvt_19_1_, lvt_10_2_, Blocks.stone.getDefaultState());
            }

            if (lvt_19_1_ <= rand.nextInt(5))
            {
                chunkPrimerIn.setBlockState(lvt_11_2_, lvt_19_1_, lvt_10_2_, Blocks.bedrock.getDefaultState());
            }
            else
            {
                IBlockState lvt_20_1_ = chunkPrimerIn.getBlockState(lvt_11_2_, lvt_19_1_, lvt_10_2_);

                if (lvt_20_1_.getBlock().getMaterial() == Material.air)
                {
                    lvt_17_1_ = -1;
                }
                else if (lvt_20_1_.getBlock() == Blocks.stone)
                {
                    if (lvt_17_1_ == -1)
                    {
                        lvt_18_2_ = false;

                        if (lvt_15_1_ <= 0)
                        {
                            lvt_13_1_ = null;
                            lvt_14_2_ = Blocks.stone.getDefaultState();
                        }
                        else if (lvt_19_1_ >= lvt_12_2_ - 4 && lvt_19_1_ <= lvt_12_2_ + 1)
                        {
                            lvt_13_1_ = Blocks.stained_hardened_clay.getDefaultState();
                            lvt_14_2_ = this.fillerBlock;
                        }

                        if (lvt_19_1_ < lvt_12_2_ && (lvt_13_1_ == null || lvt_13_1_.getBlock().getMaterial() == Material.air))
                        {
                            lvt_13_1_ = Blocks.water.getDefaultState();
                        }

                        lvt_17_1_ = lvt_15_1_ + Math.max(0, lvt_19_1_ - lvt_12_2_);

                        if (lvt_19_1_ < lvt_12_2_ - 1)
                        {
                            chunkPrimerIn.setBlockState(lvt_11_2_, lvt_19_1_, lvt_10_2_, lvt_14_2_);

                            if (lvt_14_2_.getBlock() == Blocks.stained_hardened_clay)
                            {
                                chunkPrimerIn.setBlockState(lvt_11_2_, lvt_19_1_, lvt_10_2_, lvt_14_2_.getBlock().getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE));
                            }
                        }
                        else if (this.field_150620_aI && lvt_19_1_ > 86 + lvt_15_1_ * 2)
                        {
                            if (lvt_16_2_)
                            {
                                chunkPrimerIn.setBlockState(lvt_11_2_, lvt_19_1_, lvt_10_2_, Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT));
                            }
                            else
                            {
                                chunkPrimerIn.setBlockState(lvt_11_2_, lvt_19_1_, lvt_10_2_, Blocks.grass.getDefaultState());
                            }
                        }
                        else if (lvt_19_1_ <= lvt_12_2_ + 3 + lvt_15_1_)
                        {
                            chunkPrimerIn.setBlockState(lvt_11_2_, lvt_19_1_, lvt_10_2_, this.topBlock);
                            lvt_18_2_ = true;
                        }
                        else
                        {
                            IBlockState lvt_21_2_;

                            if (lvt_19_1_ >= 64 && lvt_19_1_ <= 127)
                            {
                                if (lvt_16_2_)
                                {
                                    lvt_21_2_ = Blocks.hardened_clay.getDefaultState();
                                }
                                else
                                {
                                    lvt_21_2_ = this.func_180629_a(x, lvt_19_1_, z);
                                }
                            }
                            else
                            {
                                lvt_21_2_ = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE);
                            }

                            chunkPrimerIn.setBlockState(lvt_11_2_, lvt_19_1_, lvt_10_2_, lvt_21_2_);
                        }
                    }
                    else if (lvt_17_1_ > 0)
                    {
                        --lvt_17_1_;

                        if (lvt_18_2_)
                        {
                            chunkPrimerIn.setBlockState(lvt_11_2_, lvt_19_1_, lvt_10_2_, Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE));
                        }
                        else
                        {
                            IBlockState lvt_21_4_ = this.func_180629_a(x, lvt_19_1_, z);
                            chunkPrimerIn.setBlockState(lvt_11_2_, lvt_19_1_, lvt_10_2_, lvt_21_4_);
                        }
                    }
                }
            }
        }
    }

    private void func_150619_a(long p_150619_1_)
    {
        this.field_150621_aC = new IBlockState[64];
        Arrays.fill(this.field_150621_aC, Blocks.hardened_clay.getDefaultState());
        Random lvt_3_1_ = new Random(p_150619_1_);
        this.field_150625_aG = new NoiseGeneratorPerlin(lvt_3_1_, 1);

        for (int lvt_4_1_ = 0; lvt_4_1_ < 64; ++lvt_4_1_)
        {
            lvt_4_1_ += lvt_3_1_.nextInt(5) + 1;

            if (lvt_4_1_ < 64)
            {
                this.field_150621_aC[lvt_4_1_] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE);
            }
        }

        int lvt_4_2_ = lvt_3_1_.nextInt(4) + 2;

        for (int lvt_5_1_ = 0; lvt_5_1_ < lvt_4_2_; ++lvt_5_1_)
        {
            int lvt_6_1_ = lvt_3_1_.nextInt(3) + 1;
            int lvt_7_1_ = lvt_3_1_.nextInt(64);

            for (int lvt_8_1_ = 0; lvt_7_1_ + lvt_8_1_ < 64 && lvt_8_1_ < lvt_6_1_; ++lvt_8_1_)
            {
                this.field_150621_aC[lvt_7_1_ + lvt_8_1_] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.YELLOW);
            }
        }

        int lvt_5_2_ = lvt_3_1_.nextInt(4) + 2;

        for (int lvt_6_2_ = 0; lvt_6_2_ < lvt_5_2_; ++lvt_6_2_)
        {
            int lvt_7_2_ = lvt_3_1_.nextInt(3) + 2;
            int lvt_8_2_ = lvt_3_1_.nextInt(64);

            for (int lvt_9_1_ = 0; lvt_8_2_ + lvt_9_1_ < 64 && lvt_9_1_ < lvt_7_2_; ++lvt_9_1_)
            {
                this.field_150621_aC[lvt_8_2_ + lvt_9_1_] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BROWN);
            }
        }

        int lvt_6_3_ = lvt_3_1_.nextInt(4) + 2;

        for (int lvt_7_3_ = 0; lvt_7_3_ < lvt_6_3_; ++lvt_7_3_)
        {
            int lvt_8_3_ = lvt_3_1_.nextInt(3) + 1;
            int lvt_9_2_ = lvt_3_1_.nextInt(64);

            for (int lvt_10_1_ = 0; lvt_9_2_ + lvt_10_1_ < 64 && lvt_10_1_ < lvt_8_3_; ++lvt_10_1_)
            {
                this.field_150621_aC[lvt_9_2_ + lvt_10_1_] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.RED);
            }
        }

        int lvt_7_4_ = lvt_3_1_.nextInt(3) + 3;
        int lvt_8_4_ = 0;

        for (int lvt_9_3_ = 0; lvt_9_3_ < lvt_7_4_; ++lvt_9_3_)
        {
            int lvt_10_2_ = 1;
            lvt_8_4_ += lvt_3_1_.nextInt(16) + 4;

            for (int lvt_11_1_ = 0; lvt_8_4_ + lvt_11_1_ < 64 && lvt_11_1_ < lvt_10_2_; ++lvt_11_1_)
            {
                this.field_150621_aC[lvt_8_4_ + lvt_11_1_] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.WHITE);

                if (lvt_8_4_ + lvt_11_1_ > 1 && lvt_3_1_.nextBoolean())
                {
                    this.field_150621_aC[lvt_8_4_ + lvt_11_1_ - 1] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.SILVER);
                }

                if (lvt_8_4_ + lvt_11_1_ < 63 && lvt_3_1_.nextBoolean())
                {
                    this.field_150621_aC[lvt_8_4_ + lvt_11_1_ + 1] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.SILVER);
                }
            }
        }
    }

    private IBlockState func_180629_a(int p_180629_1_, int p_180629_2_, int p_180629_3_)
    {
        int lvt_4_1_ = (int)Math.round(this.field_150625_aG.func_151601_a((double)p_180629_1_ * 1.0D / 512.0D, (double)p_180629_1_ * 1.0D / 512.0D) * 2.0D);
        return this.field_150621_aC[(p_180629_2_ + lvt_4_1_ + 64) % 64];
    }

    protected BiomeGenBase createMutatedBiome(int p_180277_1_)
    {
        boolean lvt_2_1_ = this.biomeID == BiomeGenBase.mesa.biomeID;
        BiomeGenMesa lvt_3_1_ = new BiomeGenMesa(p_180277_1_, lvt_2_1_, this.field_150620_aI);

        if (!lvt_2_1_)
        {
            lvt_3_1_.setHeight(height_LowHills);
            lvt_3_1_.setBiomeName(this.biomeName + " M");
        }
        else
        {
            lvt_3_1_.setBiomeName(this.biomeName + " (Bryce)");
        }

        lvt_3_1_.func_150557_a(this.color, true);
        return lvt_3_1_;
    }
}
