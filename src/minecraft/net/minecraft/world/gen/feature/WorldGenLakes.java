package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class WorldGenLakes extends WorldGenerator
{
    private Block block;

    public WorldGenLakes(Block blockIn)
    {
        this.block = blockIn;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        for (position = position.add(-8, 0, -8); position.getY() > 5 && worldIn.isAirBlock(position); position = position.down())
        {
            ;
        }

        if (position.getY() <= 4)
        {
            return false;
        }
        else
        {
            position = position.down(4);
            boolean[] lvt_4_1_ = new boolean[2048];
            int lvt_5_1_ = rand.nextInt(4) + 4;

            for (int lvt_6_1_ = 0; lvt_6_1_ < lvt_5_1_; ++lvt_6_1_)
            {
                double lvt_7_1_ = rand.nextDouble() * 6.0D + 3.0D;
                double lvt_9_1_ = rand.nextDouble() * 4.0D + 2.0D;
                double lvt_11_1_ = rand.nextDouble() * 6.0D + 3.0D;
                double lvt_13_1_ = rand.nextDouble() * (16.0D - lvt_7_1_ - 2.0D) + 1.0D + lvt_7_1_ / 2.0D;
                double lvt_15_1_ = rand.nextDouble() * (8.0D - lvt_9_1_ - 4.0D) + 2.0D + lvt_9_1_ / 2.0D;
                double lvt_17_1_ = rand.nextDouble() * (16.0D - lvt_11_1_ - 2.0D) + 1.0D + lvt_11_1_ / 2.0D;

                for (int lvt_19_1_ = 1; lvt_19_1_ < 15; ++lvt_19_1_)
                {
                    for (int lvt_20_1_ = 1; lvt_20_1_ < 15; ++lvt_20_1_)
                    {
                        for (int lvt_21_1_ = 1; lvt_21_1_ < 7; ++lvt_21_1_)
                        {
                            double lvt_22_1_ = ((double)lvt_19_1_ - lvt_13_1_) / (lvt_7_1_ / 2.0D);
                            double lvt_24_1_ = ((double)lvt_21_1_ - lvt_15_1_) / (lvt_9_1_ / 2.0D);
                            double lvt_26_1_ = ((double)lvt_20_1_ - lvt_17_1_) / (lvt_11_1_ / 2.0D);
                            double lvt_28_1_ = lvt_22_1_ * lvt_22_1_ + lvt_24_1_ * lvt_24_1_ + lvt_26_1_ * lvt_26_1_;

                            if (lvt_28_1_ < 1.0D)
                            {
                                lvt_4_1_[(lvt_19_1_ * 16 + lvt_20_1_) * 8 + lvt_21_1_] = true;
                            }
                        }
                    }
                }
            }

            for (int lvt_6_2_ = 0; lvt_6_2_ < 16; ++lvt_6_2_)
            {
                for (int lvt_7_2_ = 0; lvt_7_2_ < 16; ++lvt_7_2_)
                {
                    for (int lvt_8_1_ = 0; lvt_8_1_ < 8; ++lvt_8_1_)
                    {
                        boolean lvt_9_2_ = !lvt_4_1_[(lvt_6_2_ * 16 + lvt_7_2_) * 8 + lvt_8_1_] && (lvt_6_2_ < 15 && lvt_4_1_[((lvt_6_2_ + 1) * 16 + lvt_7_2_) * 8 + lvt_8_1_] || lvt_6_2_ > 0 && lvt_4_1_[((lvt_6_2_ - 1) * 16 + lvt_7_2_) * 8 + lvt_8_1_] || lvt_7_2_ < 15 && lvt_4_1_[(lvt_6_2_ * 16 + lvt_7_2_ + 1) * 8 + lvt_8_1_] || lvt_7_2_ > 0 && lvt_4_1_[(lvt_6_2_ * 16 + (lvt_7_2_ - 1)) * 8 + lvt_8_1_] || lvt_8_1_ < 7 && lvt_4_1_[(lvt_6_2_ * 16 + lvt_7_2_) * 8 + lvt_8_1_ + 1] || lvt_8_1_ > 0 && lvt_4_1_[(lvt_6_2_ * 16 + lvt_7_2_) * 8 + (lvt_8_1_ - 1)]);

                        if (lvt_9_2_)
                        {
                            Material lvt_10_1_ = worldIn.getBlockState(position.add(lvt_6_2_, lvt_8_1_, lvt_7_2_)).getBlock().getMaterial();

                            if (lvt_8_1_ >= 4 && lvt_10_1_.isLiquid())
                            {
                                return false;
                            }

                            if (lvt_8_1_ < 4 && !lvt_10_1_.isSolid() && worldIn.getBlockState(position.add(lvt_6_2_, lvt_8_1_, lvt_7_2_)).getBlock() != this.block)
                            {
                                return false;
                            }
                        }
                    }
                }
            }

            for (int lvt_6_3_ = 0; lvt_6_3_ < 16; ++lvt_6_3_)
            {
                for (int lvt_7_3_ = 0; lvt_7_3_ < 16; ++lvt_7_3_)
                {
                    for (int lvt_8_2_ = 0; lvt_8_2_ < 8; ++lvt_8_2_)
                    {
                        if (lvt_4_1_[(lvt_6_3_ * 16 + lvt_7_3_) * 8 + lvt_8_2_])
                        {
                            worldIn.setBlockState(position.add(lvt_6_3_, lvt_8_2_, lvt_7_3_), lvt_8_2_ >= 4 ? Blocks.air.getDefaultState() : this.block.getDefaultState(), 2);
                        }
                    }
                }
            }

            for (int lvt_6_4_ = 0; lvt_6_4_ < 16; ++lvt_6_4_)
            {
                for (int lvt_7_4_ = 0; lvt_7_4_ < 16; ++lvt_7_4_)
                {
                    for (int lvt_8_3_ = 4; lvt_8_3_ < 8; ++lvt_8_3_)
                    {
                        if (lvt_4_1_[(lvt_6_4_ * 16 + lvt_7_4_) * 8 + lvt_8_3_])
                        {
                            BlockPos lvt_9_3_ = position.add(lvt_6_4_, lvt_8_3_ - 1, lvt_7_4_);

                            if (worldIn.getBlockState(lvt_9_3_).getBlock() == Blocks.dirt && worldIn.getLightFor(EnumSkyBlock.SKY, position.add(lvt_6_4_, lvt_8_3_, lvt_7_4_)) > 0)
                            {
                                BiomeGenBase lvt_10_2_ = worldIn.getBiomeGenForCoords(lvt_9_3_);

                                if (lvt_10_2_.topBlock.getBlock() == Blocks.mycelium)
                                {
                                    worldIn.setBlockState(lvt_9_3_, Blocks.mycelium.getDefaultState(), 2);
                                }
                                else
                                {
                                    worldIn.setBlockState(lvt_9_3_, Blocks.grass.getDefaultState(), 2);
                                }
                            }
                        }
                    }
                }
            }

            if (this.block.getMaterial() == Material.lava)
            {
                for (int lvt_6_5_ = 0; lvt_6_5_ < 16; ++lvt_6_5_)
                {
                    for (int lvt_7_5_ = 0; lvt_7_5_ < 16; ++lvt_7_5_)
                    {
                        for (int lvt_8_4_ = 0; lvt_8_4_ < 8; ++lvt_8_4_)
                        {
                            boolean lvt_9_4_ = !lvt_4_1_[(lvt_6_5_ * 16 + lvt_7_5_) * 8 + lvt_8_4_] && (lvt_6_5_ < 15 && lvt_4_1_[((lvt_6_5_ + 1) * 16 + lvt_7_5_) * 8 + lvt_8_4_] || lvt_6_5_ > 0 && lvt_4_1_[((lvt_6_5_ - 1) * 16 + lvt_7_5_) * 8 + lvt_8_4_] || lvt_7_5_ < 15 && lvt_4_1_[(lvt_6_5_ * 16 + lvt_7_5_ + 1) * 8 + lvt_8_4_] || lvt_7_5_ > 0 && lvt_4_1_[(lvt_6_5_ * 16 + (lvt_7_5_ - 1)) * 8 + lvt_8_4_] || lvt_8_4_ < 7 && lvt_4_1_[(lvt_6_5_ * 16 + lvt_7_5_) * 8 + lvt_8_4_ + 1] || lvt_8_4_ > 0 && lvt_4_1_[(lvt_6_5_ * 16 + lvt_7_5_) * 8 + (lvt_8_4_ - 1)]);

                            if (lvt_9_4_ && (lvt_8_4_ < 4 || rand.nextInt(2) != 0) && worldIn.getBlockState(position.add(lvt_6_5_, lvt_8_4_, lvt_7_5_)).getBlock().getMaterial().isSolid())
                            {
                                worldIn.setBlockState(position.add(lvt_6_5_, lvt_8_4_, lvt_7_5_), Blocks.stone.getDefaultState(), 2);
                            }
                        }
                    }
                }
            }

            if (this.block.getMaterial() == Material.water)
            {
                for (int lvt_6_6_ = 0; lvt_6_6_ < 16; ++lvt_6_6_)
                {
                    for (int lvt_7_6_ = 0; lvt_7_6_ < 16; ++lvt_7_6_)
                    {
                        int lvt_8_5_ = 4;

                        if (worldIn.canBlockFreezeWater(position.add(lvt_6_6_, lvt_8_5_, lvt_7_6_)))
                        {
                            worldIn.setBlockState(position.add(lvt_6_6_, lvt_8_5_, lvt_7_6_), Blocks.ice.getDefaultState(), 2);
                        }
                    }
                }
            }

            return true;
        }
    }
}
