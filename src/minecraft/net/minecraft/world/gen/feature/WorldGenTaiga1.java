package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenTaiga1 extends WorldGenAbstractTree
{
    private static final IBlockState field_181636_a = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE);
    private static final IBlockState field_181637_b = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.SPRUCE).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));

    public WorldGenTaiga1()
    {
        super(false);
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        int lvt_4_1_ = rand.nextInt(5) + 7;
        int lvt_5_1_ = lvt_4_1_ - rand.nextInt(2) - 3;
        int lvt_6_1_ = lvt_4_1_ - lvt_5_1_;
        int lvt_7_1_ = 1 + rand.nextInt(lvt_6_1_ + 1);
        boolean lvt_8_1_ = true;

        if (position.getY() >= 1 && position.getY() + lvt_4_1_ + 1 <= 256)
        {
            for (int lvt_9_1_ = position.getY(); lvt_9_1_ <= position.getY() + 1 + lvt_4_1_ && lvt_8_1_; ++lvt_9_1_)
            {
                int lvt_10_1_ = 1;

                if (lvt_9_1_ - position.getY() < lvt_5_1_)
                {
                    lvt_10_1_ = 0;
                }
                else
                {
                    lvt_10_1_ = lvt_7_1_;
                }

                BlockPos.MutableBlockPos lvt_11_1_ = new BlockPos.MutableBlockPos();

                for (int lvt_12_1_ = position.getX() - lvt_10_1_; lvt_12_1_ <= position.getX() + lvt_10_1_ && lvt_8_1_; ++lvt_12_1_)
                {
                    for (int lvt_13_1_ = position.getZ() - lvt_10_1_; lvt_13_1_ <= position.getZ() + lvt_10_1_ && lvt_8_1_; ++lvt_13_1_)
                    {
                        if (lvt_9_1_ >= 0 && lvt_9_1_ < 256)
                        {
                            if (!this.func_150523_a(worldIn.getBlockState(lvt_11_1_.set(lvt_12_1_, lvt_9_1_, lvt_13_1_)).getBlock()))
                            {
                                lvt_8_1_ = false;
                            }
                        }
                        else
                        {
                            lvt_8_1_ = false;
                        }
                    }
                }
            }

            if (!lvt_8_1_)
            {
                return false;
            }
            else
            {
                Block lvt_9_2_ = worldIn.getBlockState(position.down()).getBlock();

                if ((lvt_9_2_ == Blocks.grass || lvt_9_2_ == Blocks.dirt) && position.getY() < 256 - lvt_4_1_ - 1)
                {
                    this.func_175921_a(worldIn, position.down());
                    int lvt_10_2_ = 0;

                    for (int lvt_11_2_ = position.getY() + lvt_4_1_; lvt_11_2_ >= position.getY() + lvt_5_1_; --lvt_11_2_)
                    {
                        for (int lvt_12_2_ = position.getX() - lvt_10_2_; lvt_12_2_ <= position.getX() + lvt_10_2_; ++lvt_12_2_)
                        {
                            int lvt_13_2_ = lvt_12_2_ - position.getX();

                            for (int lvt_14_1_ = position.getZ() - lvt_10_2_; lvt_14_1_ <= position.getZ() + lvt_10_2_; ++lvt_14_1_)
                            {
                                int lvt_15_1_ = lvt_14_1_ - position.getZ();

                                if (Math.abs(lvt_13_2_) != lvt_10_2_ || Math.abs(lvt_15_1_) != lvt_10_2_ || lvt_10_2_ <= 0)
                                {
                                    BlockPos lvt_16_1_ = new BlockPos(lvt_12_2_, lvt_11_2_, lvt_14_1_);

                                    if (!worldIn.getBlockState(lvt_16_1_).getBlock().isFullBlock())
                                    {
                                        this.setBlockAndNotifyAdequately(worldIn, lvt_16_1_, field_181637_b);
                                    }
                                }
                            }
                        }

                        if (lvt_10_2_ >= 1 && lvt_11_2_ == position.getY() + lvt_5_1_ + 1)
                        {
                            --lvt_10_2_;
                        }
                        else if (lvt_10_2_ < lvt_7_1_)
                        {
                            ++lvt_10_2_;
                        }
                    }

                    for (int lvt_11_3_ = 0; lvt_11_3_ < lvt_4_1_ - 1; ++lvt_11_3_)
                    {
                        Block lvt_12_3_ = worldIn.getBlockState(position.up(lvt_11_3_)).getBlock();

                        if (lvt_12_3_.getMaterial() == Material.air || lvt_12_3_.getMaterial() == Material.leaves)
                        {
                            this.setBlockAndNotifyAdequately(worldIn, position.up(lvt_11_3_), field_181636_a);
                        }
                    }

                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        else
        {
            return false;
        }
    }
}
