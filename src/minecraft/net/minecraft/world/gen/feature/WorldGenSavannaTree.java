package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class WorldGenSavannaTree extends WorldGenAbstractTree
{
    private static final IBlockState field_181643_a = Blocks.log2.getDefaultState().withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.ACACIA);
    private static final IBlockState field_181644_b = Blocks.leaves2.getDefaultState().withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.ACACIA).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));

    public WorldGenSavannaTree(boolean p_i45463_1_)
    {
        super(p_i45463_1_);
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        int lvt_4_1_ = rand.nextInt(3) + rand.nextInt(3) + 5;
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

                if ((lvt_6_2_ == Blocks.grass || lvt_6_2_ == Blocks.dirt) && position.getY() < 256 - lvt_4_1_ - 1)
                {
                    this.func_175921_a(worldIn, position.down());
                    EnumFacing lvt_7_2_ = EnumFacing.Plane.HORIZONTAL.random(rand);
                    int lvt_8_2_ = lvt_4_1_ - rand.nextInt(4) - 1;
                    int lvt_9_2_ = 3 - rand.nextInt(3);
                    int lvt_10_2_ = position.getX();
                    int lvt_11_1_ = position.getZ();
                    int lvt_12_1_ = 0;

                    for (int lvt_13_1_ = 0; lvt_13_1_ < lvt_4_1_; ++lvt_13_1_)
                    {
                        int lvt_14_1_ = position.getY() + lvt_13_1_;

                        if (lvt_13_1_ >= lvt_8_2_ && lvt_9_2_ > 0)
                        {
                            lvt_10_2_ += lvt_7_2_.getFrontOffsetX();
                            lvt_11_1_ += lvt_7_2_.getFrontOffsetZ();
                            --lvt_9_2_;
                        }

                        BlockPos lvt_15_1_ = new BlockPos(lvt_10_2_, lvt_14_1_, lvt_11_1_);
                        Material lvt_16_1_ = worldIn.getBlockState(lvt_15_1_).getBlock().getMaterial();

                        if (lvt_16_1_ == Material.air || lvt_16_1_ == Material.leaves)
                        {
                            this.func_181642_b(worldIn, lvt_15_1_);
                            lvt_12_1_ = lvt_14_1_;
                        }
                    }

                    BlockPos lvt_13_2_ = new BlockPos(lvt_10_2_, lvt_12_1_, lvt_11_1_);

                    for (int lvt_14_2_ = -3; lvt_14_2_ <= 3; ++lvt_14_2_)
                    {
                        for (int lvt_15_2_ = -3; lvt_15_2_ <= 3; ++lvt_15_2_)
                        {
                            if (Math.abs(lvt_14_2_) != 3 || Math.abs(lvt_15_2_) != 3)
                            {
                                this.func_175924_b(worldIn, lvt_13_2_.add(lvt_14_2_, 0, lvt_15_2_));
                            }
                        }
                    }

                    lvt_13_2_ = lvt_13_2_.up();

                    for (int lvt_14_3_ = -1; lvt_14_3_ <= 1; ++lvt_14_3_)
                    {
                        for (int lvt_15_3_ = -1; lvt_15_3_ <= 1; ++lvt_15_3_)
                        {
                            this.func_175924_b(worldIn, lvt_13_2_.add(lvt_14_3_, 0, lvt_15_3_));
                        }
                    }

                    this.func_175924_b(worldIn, lvt_13_2_.east(2));
                    this.func_175924_b(worldIn, lvt_13_2_.west(2));
                    this.func_175924_b(worldIn, lvt_13_2_.south(2));
                    this.func_175924_b(worldIn, lvt_13_2_.north(2));
                    lvt_10_2_ = position.getX();
                    lvt_11_1_ = position.getZ();
                    EnumFacing lvt_13_3_ = EnumFacing.Plane.HORIZONTAL.random(rand);

                    if (lvt_13_3_ != lvt_7_2_)
                    {
                        int lvt_14_4_ = lvt_8_2_ - rand.nextInt(2) - 1;
                        int lvt_15_4_ = 1 + rand.nextInt(3);
                        lvt_12_1_ = 0;

                        for (int lvt_16_2_ = lvt_14_4_; lvt_16_2_ < lvt_4_1_ && lvt_15_4_ > 0; --lvt_15_4_)
                        {
                            if (lvt_16_2_ >= 1)
                            {
                                int lvt_17_1_ = position.getY() + lvt_16_2_;
                                lvt_10_2_ += lvt_13_3_.getFrontOffsetX();
                                lvt_11_1_ += lvt_13_3_.getFrontOffsetZ();
                                BlockPos lvt_18_1_ = new BlockPos(lvt_10_2_, lvt_17_1_, lvt_11_1_);
                                Material lvt_19_1_ = worldIn.getBlockState(lvt_18_1_).getBlock().getMaterial();

                                if (lvt_19_1_ == Material.air || lvt_19_1_ == Material.leaves)
                                {
                                    this.func_181642_b(worldIn, lvt_18_1_);
                                    lvt_12_1_ = lvt_17_1_;
                                }
                            }

                            ++lvt_16_2_;
                        }

                        if (lvt_12_1_ > 0)
                        {
                            BlockPos lvt_16_3_ = new BlockPos(lvt_10_2_, lvt_12_1_, lvt_11_1_);

                            for (int lvt_17_2_ = -2; lvt_17_2_ <= 2; ++lvt_17_2_)
                            {
                                for (int lvt_18_2_ = -2; lvt_18_2_ <= 2; ++lvt_18_2_)
                                {
                                    if (Math.abs(lvt_17_2_) != 2 || Math.abs(lvt_18_2_) != 2)
                                    {
                                        this.func_175924_b(worldIn, lvt_16_3_.add(lvt_17_2_, 0, lvt_18_2_));
                                    }
                                }
                            }

                            lvt_16_3_ = lvt_16_3_.up();

                            for (int lvt_17_3_ = -1; lvt_17_3_ <= 1; ++lvt_17_3_)
                            {
                                for (int lvt_18_3_ = -1; lvt_18_3_ <= 1; ++lvt_18_3_)
                                {
                                    this.func_175924_b(worldIn, lvt_16_3_.add(lvt_17_3_, 0, lvt_18_3_));
                                }
                            }
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

    private void func_181642_b(World p_181642_1_, BlockPos p_181642_2_)
    {
        this.setBlockAndNotifyAdequately(p_181642_1_, p_181642_2_, field_181643_a);
    }

    private void func_175924_b(World worldIn, BlockPos p_175924_2_)
    {
        Material lvt_3_1_ = worldIn.getBlockState(p_175924_2_).getBlock().getMaterial();

        if (lvt_3_1_ == Material.air || lvt_3_1_ == Material.leaves)
        {
            this.setBlockAndNotifyAdequately(worldIn, p_175924_2_, field_181644_b);
        }
    }
}
