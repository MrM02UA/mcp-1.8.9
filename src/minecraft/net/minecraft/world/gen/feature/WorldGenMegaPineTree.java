package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenMegaPineTree extends WorldGenHugeTrees
{
    private static final IBlockState field_181633_e = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE);
    private static final IBlockState field_181634_f = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.SPRUCE).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
    private static final IBlockState field_181635_g = Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.PODZOL);
    private boolean useBaseHeight;

    public WorldGenMegaPineTree(boolean p_i45457_1_, boolean p_i45457_2_)
    {
        super(p_i45457_1_, 13, 15, field_181633_e, field_181634_f);
        this.useBaseHeight = p_i45457_2_;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        int lvt_4_1_ = this.func_150533_a(rand);

        if (!this.func_175929_a(worldIn, rand, position, lvt_4_1_))
        {
            return false;
        }
        else
        {
            this.func_150541_c(worldIn, position.getX(), position.getZ(), position.getY() + lvt_4_1_, 0, rand);

            for (int lvt_5_1_ = 0; lvt_5_1_ < lvt_4_1_; ++lvt_5_1_)
            {
                Block lvt_6_1_ = worldIn.getBlockState(position.up(lvt_5_1_)).getBlock();

                if (lvt_6_1_.getMaterial() == Material.air || lvt_6_1_.getMaterial() == Material.leaves)
                {
                    this.setBlockAndNotifyAdequately(worldIn, position.up(lvt_5_1_), this.woodMetadata);
                }

                if (lvt_5_1_ < lvt_4_1_ - 1)
                {
                    lvt_6_1_ = worldIn.getBlockState(position.add(1, lvt_5_1_, 0)).getBlock();

                    if (lvt_6_1_.getMaterial() == Material.air || lvt_6_1_.getMaterial() == Material.leaves)
                    {
                        this.setBlockAndNotifyAdequately(worldIn, position.add(1, lvt_5_1_, 0), this.woodMetadata);
                    }

                    lvt_6_1_ = worldIn.getBlockState(position.add(1, lvt_5_1_, 1)).getBlock();

                    if (lvt_6_1_.getMaterial() == Material.air || lvt_6_1_.getMaterial() == Material.leaves)
                    {
                        this.setBlockAndNotifyAdequately(worldIn, position.add(1, lvt_5_1_, 1), this.woodMetadata);
                    }

                    lvt_6_1_ = worldIn.getBlockState(position.add(0, lvt_5_1_, 1)).getBlock();

                    if (lvt_6_1_.getMaterial() == Material.air || lvt_6_1_.getMaterial() == Material.leaves)
                    {
                        this.setBlockAndNotifyAdequately(worldIn, position.add(0, lvt_5_1_, 1), this.woodMetadata);
                    }
                }
            }

            return true;
        }
    }

    private void func_150541_c(World worldIn, int p_150541_2_, int p_150541_3_, int p_150541_4_, int p_150541_5_, Random p_150541_6_)
    {
        int lvt_7_1_ = p_150541_6_.nextInt(5) + (this.useBaseHeight ? this.baseHeight : 3);
        int lvt_8_1_ = 0;

        for (int lvt_9_1_ = p_150541_4_ - lvt_7_1_; lvt_9_1_ <= p_150541_4_; ++lvt_9_1_)
        {
            int lvt_10_1_ = p_150541_4_ - lvt_9_1_;
            int lvt_11_1_ = p_150541_5_ + MathHelper.floor_float((float)lvt_10_1_ / (float)lvt_7_1_ * 3.5F);
            this.func_175925_a(worldIn, new BlockPos(p_150541_2_, lvt_9_1_, p_150541_3_), lvt_11_1_ + (lvt_10_1_ > 0 && lvt_11_1_ == lvt_8_1_ && (lvt_9_1_ & 1) == 0 ? 1 : 0));
            lvt_8_1_ = lvt_11_1_;
        }
    }

    public void func_180711_a(World worldIn, Random p_180711_2_, BlockPos p_180711_3_)
    {
        this.func_175933_b(worldIn, p_180711_3_.west().north());
        this.func_175933_b(worldIn, p_180711_3_.east(2).north());
        this.func_175933_b(worldIn, p_180711_3_.west().south(2));
        this.func_175933_b(worldIn, p_180711_3_.east(2).south(2));

        for (int lvt_4_1_ = 0; lvt_4_1_ < 5; ++lvt_4_1_)
        {
            int lvt_5_1_ = p_180711_2_.nextInt(64);
            int lvt_6_1_ = lvt_5_1_ % 8;
            int lvt_7_1_ = lvt_5_1_ / 8;

            if (lvt_6_1_ == 0 || lvt_6_1_ == 7 || lvt_7_1_ == 0 || lvt_7_1_ == 7)
            {
                this.func_175933_b(worldIn, p_180711_3_.add(-3 + lvt_6_1_, 0, -3 + lvt_7_1_));
            }
        }
    }

    private void func_175933_b(World worldIn, BlockPos p_175933_2_)
    {
        for (int lvt_3_1_ = -2; lvt_3_1_ <= 2; ++lvt_3_1_)
        {
            for (int lvt_4_1_ = -2; lvt_4_1_ <= 2; ++lvt_4_1_)
            {
                if (Math.abs(lvt_3_1_) != 2 || Math.abs(lvt_4_1_) != 2)
                {
                    this.func_175934_c(worldIn, p_175933_2_.add(lvt_3_1_, 0, lvt_4_1_));
                }
            }
        }
    }

    private void func_175934_c(World worldIn, BlockPos p_175934_2_)
    {
        for (int lvt_3_1_ = 2; lvt_3_1_ >= -3; --lvt_3_1_)
        {
            BlockPos lvt_4_1_ = p_175934_2_.up(lvt_3_1_);
            Block lvt_5_1_ = worldIn.getBlockState(lvt_4_1_).getBlock();

            if (lvt_5_1_ == Blocks.grass || lvt_5_1_ == Blocks.dirt)
            {
                this.setBlockAndNotifyAdequately(worldIn, lvt_4_1_, field_181635_g);
                break;
            }

            if (lvt_5_1_.getMaterial() != Material.air && lvt_3_1_ < 0)
            {
                break;
            }
        }
    }
}
