package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenIceSpike extends WorldGenerator
{
    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        while (worldIn.isAirBlock(position) && position.getY() > 2)
        {
            position = position.down();
        }

        if (worldIn.getBlockState(position).getBlock() != Blocks.snow)
        {
            return false;
        }
        else
        {
            position = position.up(rand.nextInt(4));
            int lvt_4_1_ = rand.nextInt(4) + 7;
            int lvt_5_1_ = lvt_4_1_ / 4 + rand.nextInt(2);

            if (lvt_5_1_ > 1 && rand.nextInt(60) == 0)
            {
                position = position.up(10 + rand.nextInt(30));
            }

            for (int lvt_6_1_ = 0; lvt_6_1_ < lvt_4_1_; ++lvt_6_1_)
            {
                float lvt_7_1_ = (1.0F - (float)lvt_6_1_ / (float)lvt_4_1_) * (float)lvt_5_1_;
                int lvt_8_1_ = MathHelper.ceiling_float_int(lvt_7_1_);

                for (int lvt_9_1_ = -lvt_8_1_; lvt_9_1_ <= lvt_8_1_; ++lvt_9_1_)
                {
                    float lvt_10_1_ = (float)MathHelper.abs_int(lvt_9_1_) - 0.25F;

                    for (int lvt_11_1_ = -lvt_8_1_; lvt_11_1_ <= lvt_8_1_; ++lvt_11_1_)
                    {
                        float lvt_12_1_ = (float)MathHelper.abs_int(lvt_11_1_) - 0.25F;

                        if ((lvt_9_1_ == 0 && lvt_11_1_ == 0 || lvt_10_1_ * lvt_10_1_ + lvt_12_1_ * lvt_12_1_ <= lvt_7_1_ * lvt_7_1_) && (lvt_9_1_ != -lvt_8_1_ && lvt_9_1_ != lvt_8_1_ && lvt_11_1_ != -lvt_8_1_ && lvt_11_1_ != lvt_8_1_ || rand.nextFloat() <= 0.75F))
                        {
                            Block lvt_13_1_ = worldIn.getBlockState(position.add(lvt_9_1_, lvt_6_1_, lvt_11_1_)).getBlock();

                            if (lvt_13_1_.getMaterial() == Material.air || lvt_13_1_ == Blocks.dirt || lvt_13_1_ == Blocks.snow || lvt_13_1_ == Blocks.ice)
                            {
                                this.setBlockAndNotifyAdequately(worldIn, position.add(lvt_9_1_, lvt_6_1_, lvt_11_1_), Blocks.packed_ice.getDefaultState());
                            }

                            if (lvt_6_1_ != 0 && lvt_8_1_ > 1)
                            {
                                lvt_13_1_ = worldIn.getBlockState(position.add(lvt_9_1_, -lvt_6_1_, lvt_11_1_)).getBlock();

                                if (lvt_13_1_.getMaterial() == Material.air || lvt_13_1_ == Blocks.dirt || lvt_13_1_ == Blocks.snow || lvt_13_1_ == Blocks.ice)
                                {
                                    this.setBlockAndNotifyAdequately(worldIn, position.add(lvt_9_1_, -lvt_6_1_, lvt_11_1_), Blocks.packed_ice.getDefaultState());
                                }
                            }
                        }
                    }
                }
            }

            int lvt_6_2_ = lvt_5_1_ - 1;

            if (lvt_6_2_ < 0)
            {
                lvt_6_2_ = 0;
            }
            else if (lvt_6_2_ > 1)
            {
                lvt_6_2_ = 1;
            }

            for (int lvt_7_2_ = -lvt_6_2_; lvt_7_2_ <= lvt_6_2_; ++lvt_7_2_)
            {
                for (int lvt_8_2_ = -lvt_6_2_; lvt_8_2_ <= lvt_6_2_; ++lvt_8_2_)
                {
                    BlockPos lvt_9_2_ = position.add(lvt_7_2_, -1, lvt_8_2_);
                    int lvt_10_2_ = 50;

                    if (Math.abs(lvt_7_2_) == 1 && Math.abs(lvt_8_2_) == 1)
                    {
                        lvt_10_2_ = rand.nextInt(5);
                    }

                    while (lvt_9_2_.getY() > 50)
                    {
                        Block lvt_11_2_ = worldIn.getBlockState(lvt_9_2_).getBlock();

                        if (lvt_11_2_.getMaterial() != Material.air && lvt_11_2_ != Blocks.dirt && lvt_11_2_ != Blocks.snow && lvt_11_2_ != Blocks.ice && lvt_11_2_ != Blocks.packed_ice)
                        {
                            break;
                        }

                        this.setBlockAndNotifyAdequately(worldIn, lvt_9_2_, Blocks.packed_ice.getDefaultState());
                        lvt_9_2_ = lvt_9_2_.down();
                        --lvt_10_2_;

                        if (lvt_10_2_ <= 0)
                        {
                            lvt_9_2_ = lvt_9_2_.down(rand.nextInt(5) + 1);
                            lvt_10_2_ = rand.nextInt(5);
                        }
                    }
                }
            }

            return true;
        }
    }
}
