package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenForest extends WorldGenAbstractTree
{
    private static final IBlockState field_181629_a = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.BIRCH);
    private static final IBlockState field_181630_b = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.BIRCH).withProperty(BlockOldLeaf.CHECK_DECAY, Boolean.valueOf(false));
    private boolean useExtraRandomHeight;

    public WorldGenForest(boolean p_i45449_1_, boolean p_i45449_2_)
    {
        super(p_i45449_1_);
        this.useExtraRandomHeight = p_i45449_2_;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        int lvt_4_1_ = rand.nextInt(3) + 5;

        if (this.useExtraRandomHeight)
        {
            lvt_4_1_ += rand.nextInt(7);
        }

        boolean lvt_5_1_ = true;

        if (position.getY() >= 1 && position.getY() + lvt_4_1_ + 1 <= 256)
        {
            for (int lvt_6_1_ = position.getY(); lvt_6_1_ <= position.getY() + 1 + lvt_4_1_; ++lvt_6_1_)
            {
                int lvt_7_1_ = 1;

                if (lvt_6_1_ == position.getY())
                {
                    lvt_7_1_ = 0;
                }

                if (lvt_6_1_ >= position.getY() + 1 + lvt_4_1_ - 2)
                {
                    lvt_7_1_ = 2;
                }

                BlockPos.MutableBlockPos lvt_8_1_ = new BlockPos.MutableBlockPos();

                for (int lvt_9_1_ = position.getX() - lvt_7_1_; lvt_9_1_ <= position.getX() + lvt_7_1_ && lvt_5_1_; ++lvt_9_1_)
                {
                    for (int lvt_10_1_ = position.getZ() - lvt_7_1_; lvt_10_1_ <= position.getZ() + lvt_7_1_ && lvt_5_1_; ++lvt_10_1_)
                    {
                        if (lvt_6_1_ >= 0 && lvt_6_1_ < 256)
                        {
                            if (!this.func_150523_a(worldIn.getBlockState(lvt_8_1_.set(lvt_9_1_, lvt_6_1_, lvt_10_1_)).getBlock()))
                            {
                                lvt_5_1_ = false;
                            }
                        }
                        else
                        {
                            lvt_5_1_ = false;
                        }
                    }
                }
            }

            if (!lvt_5_1_)
            {
                return false;
            }
            else
            {
                Block lvt_6_2_ = worldIn.getBlockState(position.down()).getBlock();

                if ((lvt_6_2_ == Blocks.grass || lvt_6_2_ == Blocks.dirt || lvt_6_2_ == Blocks.farmland) && position.getY() < 256 - lvt_4_1_ - 1)
                {
                    this.func_175921_a(worldIn, position.down());

                    for (int lvt_7_2_ = position.getY() - 3 + lvt_4_1_; lvt_7_2_ <= position.getY() + lvt_4_1_; ++lvt_7_2_)
                    {
                        int lvt_8_2_ = lvt_7_2_ - (position.getY() + lvt_4_1_);
                        int lvt_9_2_ = 1 - lvt_8_2_ / 2;

                        for (int lvt_10_2_ = position.getX() - lvt_9_2_; lvt_10_2_ <= position.getX() + lvt_9_2_; ++lvt_10_2_)
                        {
                            int lvt_11_1_ = lvt_10_2_ - position.getX();

                            for (int lvt_12_1_ = position.getZ() - lvt_9_2_; lvt_12_1_ <= position.getZ() + lvt_9_2_; ++lvt_12_1_)
                            {
                                int lvt_13_1_ = lvt_12_1_ - position.getZ();

                                if (Math.abs(lvt_11_1_) != lvt_9_2_ || Math.abs(lvt_13_1_) != lvt_9_2_ || rand.nextInt(2) != 0 && lvt_8_2_ != 0)
                                {
                                    BlockPos lvt_14_1_ = new BlockPos(lvt_10_2_, lvt_7_2_, lvt_12_1_);
                                    Block lvt_15_1_ = worldIn.getBlockState(lvt_14_1_).getBlock();

                                    if (lvt_15_1_.getMaterial() == Material.air || lvt_15_1_.getMaterial() == Material.leaves)
                                    {
                                        this.setBlockAndNotifyAdequately(worldIn, lvt_14_1_, field_181630_b);
                                    }
                                }
                            }
                        }
                    }

                    for (int lvt_7_3_ = 0; lvt_7_3_ < lvt_4_1_; ++lvt_7_3_)
                    {
                        Block lvt_8_3_ = worldIn.getBlockState(position.up(lvt_7_3_)).getBlock();

                        if (lvt_8_3_.getMaterial() == Material.air || lvt_8_3_.getMaterial() == Material.leaves)
                        {
                            this.setBlockAndNotifyAdequately(worldIn, position.up(lvt_7_3_), field_181629_a);
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
