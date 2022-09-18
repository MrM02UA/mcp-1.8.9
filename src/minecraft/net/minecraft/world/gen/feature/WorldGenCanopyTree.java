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

public class WorldGenCanopyTree extends WorldGenAbstractTree
{
    private static final IBlockState field_181640_a = Blocks.log2.getDefaultState().withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.DARK_OAK);
    private static final IBlockState field_181641_b = Blocks.leaves2.getDefaultState().withProperty(BlockNewLeaf.VARIANT, BlockPlanks.EnumType.DARK_OAK).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));

    public WorldGenCanopyTree(boolean p_i45461_1_)
    {
        super(p_i45461_1_);
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        int lvt_4_1_ = rand.nextInt(3) + rand.nextInt(2) + 6;
        int lvt_5_1_ = position.getX();
        int lvt_6_1_ = position.getY();
        int lvt_7_1_ = position.getZ();

        if (lvt_6_1_ >= 1 && lvt_6_1_ + lvt_4_1_ + 1 < 256)
        {
            BlockPos lvt_8_1_ = position.down();
            Block lvt_9_1_ = worldIn.getBlockState(lvt_8_1_).getBlock();

            if (lvt_9_1_ != Blocks.grass && lvt_9_1_ != Blocks.dirt)
            {
                return false;
            }
            else if (!this.func_181638_a(worldIn, position, lvt_4_1_))
            {
                return false;
            }
            else
            {
                this.func_175921_a(worldIn, lvt_8_1_);
                this.func_175921_a(worldIn, lvt_8_1_.east());
                this.func_175921_a(worldIn, lvt_8_1_.south());
                this.func_175921_a(worldIn, lvt_8_1_.south().east());
                EnumFacing lvt_10_1_ = EnumFacing.Plane.HORIZONTAL.random(rand);
                int lvt_11_1_ = lvt_4_1_ - rand.nextInt(4);
                int lvt_12_1_ = 2 - rand.nextInt(3);
                int lvt_13_1_ = lvt_5_1_;
                int lvt_14_1_ = lvt_7_1_;
                int lvt_15_1_ = lvt_6_1_ + lvt_4_1_ - 1;

                for (int lvt_16_1_ = 0; lvt_16_1_ < lvt_4_1_; ++lvt_16_1_)
                {
                    if (lvt_16_1_ >= lvt_11_1_ && lvt_12_1_ > 0)
                    {
                        lvt_13_1_ += lvt_10_1_.getFrontOffsetX();
                        lvt_14_1_ += lvt_10_1_.getFrontOffsetZ();
                        --lvt_12_1_;
                    }

                    int lvt_17_1_ = lvt_6_1_ + lvt_16_1_;
                    BlockPos lvt_18_1_ = new BlockPos(lvt_13_1_, lvt_17_1_, lvt_14_1_);
                    Material lvt_19_1_ = worldIn.getBlockState(lvt_18_1_).getBlock().getMaterial();

                    if (lvt_19_1_ == Material.air || lvt_19_1_ == Material.leaves)
                    {
                        this.func_181639_b(worldIn, lvt_18_1_);
                        this.func_181639_b(worldIn, lvt_18_1_.east());
                        this.func_181639_b(worldIn, lvt_18_1_.south());
                        this.func_181639_b(worldIn, lvt_18_1_.east().south());
                    }
                }

                for (int lvt_16_2_ = -2; lvt_16_2_ <= 0; ++lvt_16_2_)
                {
                    for (int lvt_17_2_ = -2; lvt_17_2_ <= 0; ++lvt_17_2_)
                    {
                        int lvt_18_2_ = -1;
                        this.func_150526_a(worldIn, lvt_13_1_ + lvt_16_2_, lvt_15_1_ + lvt_18_2_, lvt_14_1_ + lvt_17_2_);
                        this.func_150526_a(worldIn, 1 + lvt_13_1_ - lvt_16_2_, lvt_15_1_ + lvt_18_2_, lvt_14_1_ + lvt_17_2_);
                        this.func_150526_a(worldIn, lvt_13_1_ + lvt_16_2_, lvt_15_1_ + lvt_18_2_, 1 + lvt_14_1_ - lvt_17_2_);
                        this.func_150526_a(worldIn, 1 + lvt_13_1_ - lvt_16_2_, lvt_15_1_ + lvt_18_2_, 1 + lvt_14_1_ - lvt_17_2_);

                        if ((lvt_16_2_ > -2 || lvt_17_2_ > -1) && (lvt_16_2_ != -1 || lvt_17_2_ != -2))
                        {
                            lvt_18_2_ = 1;
                            this.func_150526_a(worldIn, lvt_13_1_ + lvt_16_2_, lvt_15_1_ + lvt_18_2_, lvt_14_1_ + lvt_17_2_);
                            this.func_150526_a(worldIn, 1 + lvt_13_1_ - lvt_16_2_, lvt_15_1_ + lvt_18_2_, lvt_14_1_ + lvt_17_2_);
                            this.func_150526_a(worldIn, lvt_13_1_ + lvt_16_2_, lvt_15_1_ + lvt_18_2_, 1 + lvt_14_1_ - lvt_17_2_);
                            this.func_150526_a(worldIn, 1 + lvt_13_1_ - lvt_16_2_, lvt_15_1_ + lvt_18_2_, 1 + lvt_14_1_ - lvt_17_2_);
                        }
                    }
                }

                if (rand.nextBoolean())
                {
                    this.func_150526_a(worldIn, lvt_13_1_, lvt_15_1_ + 2, lvt_14_1_);
                    this.func_150526_a(worldIn, lvt_13_1_ + 1, lvt_15_1_ + 2, lvt_14_1_);
                    this.func_150526_a(worldIn, lvt_13_1_ + 1, lvt_15_1_ + 2, lvt_14_1_ + 1);
                    this.func_150526_a(worldIn, lvt_13_1_, lvt_15_1_ + 2, lvt_14_1_ + 1);
                }

                for (int lvt_16_3_ = -3; lvt_16_3_ <= 4; ++lvt_16_3_)
                {
                    for (int lvt_17_3_ = -3; lvt_17_3_ <= 4; ++lvt_17_3_)
                    {
                        if ((lvt_16_3_ != -3 || lvt_17_3_ != -3) && (lvt_16_3_ != -3 || lvt_17_3_ != 4) && (lvt_16_3_ != 4 || lvt_17_3_ != -3) && (lvt_16_3_ != 4 || lvt_17_3_ != 4) && (Math.abs(lvt_16_3_) < 3 || Math.abs(lvt_17_3_) < 3))
                        {
                            this.func_150526_a(worldIn, lvt_13_1_ + lvt_16_3_, lvt_15_1_, lvt_14_1_ + lvt_17_3_);
                        }
                    }
                }

                for (int lvt_16_4_ = -1; lvt_16_4_ <= 2; ++lvt_16_4_)
                {
                    for (int lvt_17_4_ = -1; lvt_17_4_ <= 2; ++lvt_17_4_)
                    {
                        if ((lvt_16_4_ < 0 || lvt_16_4_ > 1 || lvt_17_4_ < 0 || lvt_17_4_ > 1) && rand.nextInt(3) <= 0)
                        {
                            int lvt_18_3_ = rand.nextInt(3) + 2;

                            for (int lvt_19_2_ = 0; lvt_19_2_ < lvt_18_3_; ++lvt_19_2_)
                            {
                                this.func_181639_b(worldIn, new BlockPos(lvt_5_1_ + lvt_16_4_, lvt_15_1_ - lvt_19_2_ - 1, lvt_7_1_ + lvt_17_4_));
                            }

                            for (int lvt_19_3_ = -1; lvt_19_3_ <= 1; ++lvt_19_3_)
                            {
                                for (int lvt_20_1_ = -1; lvt_20_1_ <= 1; ++lvt_20_1_)
                                {
                                    this.func_150526_a(worldIn, lvt_13_1_ + lvt_16_4_ + lvt_19_3_, lvt_15_1_, lvt_14_1_ + lvt_17_4_ + lvt_20_1_);
                                }
                            }

                            for (int lvt_19_4_ = -2; lvt_19_4_ <= 2; ++lvt_19_4_)
                            {
                                for (int lvt_20_2_ = -2; lvt_20_2_ <= 2; ++lvt_20_2_)
                                {
                                    if (Math.abs(lvt_19_4_) != 2 || Math.abs(lvt_20_2_) != 2)
                                    {
                                        this.func_150526_a(worldIn, lvt_13_1_ + lvt_16_4_ + lvt_19_4_, lvt_15_1_ - 1, lvt_14_1_ + lvt_17_4_ + lvt_20_2_);
                                    }
                                }
                            }
                        }
                    }
                }

                return true;
            }
        }
        else
        {
            return false;
        }
    }

    private boolean func_181638_a(World p_181638_1_, BlockPos p_181638_2_, int p_181638_3_)
    {
        int lvt_4_1_ = p_181638_2_.getX();
        int lvt_5_1_ = p_181638_2_.getY();
        int lvt_6_1_ = p_181638_2_.getZ();
        BlockPos.MutableBlockPos lvt_7_1_ = new BlockPos.MutableBlockPos();

        for (int lvt_8_1_ = 0; lvt_8_1_ <= p_181638_3_ + 1; ++lvt_8_1_)
        {
            int lvt_9_1_ = 1;

            if (lvt_8_1_ == 0)
            {
                lvt_9_1_ = 0;
            }

            if (lvt_8_1_ >= p_181638_3_ - 1)
            {
                lvt_9_1_ = 2;
            }

            for (int lvt_10_1_ = -lvt_9_1_; lvt_10_1_ <= lvt_9_1_; ++lvt_10_1_)
            {
                for (int lvt_11_1_ = -lvt_9_1_; lvt_11_1_ <= lvt_9_1_; ++lvt_11_1_)
                {
                    if (!this.func_150523_a(p_181638_1_.getBlockState(lvt_7_1_.set(lvt_4_1_ + lvt_10_1_, lvt_5_1_ + lvt_8_1_, lvt_6_1_ + lvt_11_1_)).getBlock()))
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private void func_181639_b(World p_181639_1_, BlockPos p_181639_2_)
    {
        if (this.func_150523_a(p_181639_1_.getBlockState(p_181639_2_).getBlock()))
        {
            this.setBlockAndNotifyAdequately(p_181639_1_, p_181639_2_, field_181640_a);
        }
    }

    private void func_150526_a(World worldIn, int p_150526_2_, int p_150526_3_, int p_150526_4_)
    {
        BlockPos lvt_5_1_ = new BlockPos(p_150526_2_, p_150526_3_, p_150526_4_);
        Block lvt_6_1_ = worldIn.getBlockState(lvt_5_1_).getBlock();

        if (lvt_6_1_.getMaterial() == Material.air)
        {
            this.setBlockAndNotifyAdequately(worldIn, lvt_5_1_, field_181641_b);
        }
    }
}
