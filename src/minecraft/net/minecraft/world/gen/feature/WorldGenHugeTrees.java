package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public abstract class WorldGenHugeTrees extends WorldGenAbstractTree
{
    /** The base height of the tree */
    protected final int baseHeight;

    /** Sets the metadata for the wood blocks used */
    protected final IBlockState woodMetadata;

    /** Sets the metadata for the leaves used in huge trees */
    protected final IBlockState leavesMetadata;
    protected int extraRandomHeight;

    public WorldGenHugeTrees(boolean p_i46447_1_, int p_i46447_2_, int p_i46447_3_, IBlockState p_i46447_4_, IBlockState p_i46447_5_)
    {
        super(p_i46447_1_);
        this.baseHeight = p_i46447_2_;
        this.extraRandomHeight = p_i46447_3_;
        this.woodMetadata = p_i46447_4_;
        this.leavesMetadata = p_i46447_5_;
    }

    protected int func_150533_a(Random p_150533_1_)
    {
        int lvt_2_1_ = p_150533_1_.nextInt(3) + this.baseHeight;

        if (this.extraRandomHeight > 1)
        {
            lvt_2_1_ += p_150533_1_.nextInt(this.extraRandomHeight);
        }

        return lvt_2_1_;
    }

    private boolean func_175926_c(World worldIn, BlockPos p_175926_2_, int p_175926_3_)
    {
        boolean lvt_4_1_ = true;

        if (p_175926_2_.getY() >= 1 && p_175926_2_.getY() + p_175926_3_ + 1 <= 256)
        {
            for (int lvt_5_1_ = 0; lvt_5_1_ <= 1 + p_175926_3_; ++lvt_5_1_)
            {
                int lvt_6_1_ = 2;

                if (lvt_5_1_ == 0)
                {
                    lvt_6_1_ = 1;
                }
                else if (lvt_5_1_ >= 1 + p_175926_3_ - 2)
                {
                    lvt_6_1_ = 2;
                }

                for (int lvt_7_1_ = -lvt_6_1_; lvt_7_1_ <= lvt_6_1_ && lvt_4_1_; ++lvt_7_1_)
                {
                    for (int lvt_8_1_ = -lvt_6_1_; lvt_8_1_ <= lvt_6_1_ && lvt_4_1_; ++lvt_8_1_)
                    {
                        if (p_175926_2_.getY() + lvt_5_1_ < 0 || p_175926_2_.getY() + lvt_5_1_ >= 256 || !this.func_150523_a(worldIn.getBlockState(p_175926_2_.add(lvt_7_1_, lvt_5_1_, lvt_8_1_)).getBlock()))
                        {
                            lvt_4_1_ = false;
                        }
                    }
                }
            }

            return lvt_4_1_;
        }
        else
        {
            return false;
        }
    }

    private boolean func_175927_a(BlockPos p_175927_1_, World worldIn)
    {
        BlockPos lvt_3_1_ = p_175927_1_.down();
        Block lvt_4_1_ = worldIn.getBlockState(lvt_3_1_).getBlock();

        if ((lvt_4_1_ == Blocks.grass || lvt_4_1_ == Blocks.dirt) && p_175927_1_.getY() >= 2)
        {
            this.func_175921_a(worldIn, lvt_3_1_);
            this.func_175921_a(worldIn, lvt_3_1_.east());
            this.func_175921_a(worldIn, lvt_3_1_.south());
            this.func_175921_a(worldIn, lvt_3_1_.south().east());
            return true;
        }
        else
        {
            return false;
        }
    }

    protected boolean func_175929_a(World worldIn, Random p_175929_2_, BlockPos p_175929_3_, int p_175929_4_)
    {
        return this.func_175926_c(worldIn, p_175929_3_, p_175929_4_) && this.func_175927_a(p_175929_3_, worldIn);
    }

    protected void func_175925_a(World worldIn, BlockPos p_175925_2_, int p_175925_3_)
    {
        int lvt_4_1_ = p_175925_3_ * p_175925_3_;

        for (int lvt_5_1_ = -p_175925_3_; lvt_5_1_ <= p_175925_3_ + 1; ++lvt_5_1_)
        {
            for (int lvt_6_1_ = -p_175925_3_; lvt_6_1_ <= p_175925_3_ + 1; ++lvt_6_1_)
            {
                int lvt_7_1_ = lvt_5_1_ - 1;
                int lvt_8_1_ = lvt_6_1_ - 1;

                if (lvt_5_1_ * lvt_5_1_ + lvt_6_1_ * lvt_6_1_ <= lvt_4_1_ || lvt_7_1_ * lvt_7_1_ + lvt_8_1_ * lvt_8_1_ <= lvt_4_1_ || lvt_5_1_ * lvt_5_1_ + lvt_8_1_ * lvt_8_1_ <= lvt_4_1_ || lvt_7_1_ * lvt_7_1_ + lvt_6_1_ * lvt_6_1_ <= lvt_4_1_)
                {
                    BlockPos lvt_9_1_ = p_175925_2_.add(lvt_5_1_, 0, lvt_6_1_);
                    Material lvt_10_1_ = worldIn.getBlockState(lvt_9_1_).getBlock().getMaterial();

                    if (lvt_10_1_ == Material.air || lvt_10_1_ == Material.leaves)
                    {
                        this.setBlockAndNotifyAdequately(worldIn, lvt_9_1_, this.leavesMetadata);
                    }
                }
            }
        }
    }

    protected void func_175928_b(World worldIn, BlockPos p_175928_2_, int p_175928_3_)
    {
        int lvt_4_1_ = p_175928_3_ * p_175928_3_;

        for (int lvt_5_1_ = -p_175928_3_; lvt_5_1_ <= p_175928_3_; ++lvt_5_1_)
        {
            for (int lvt_6_1_ = -p_175928_3_; lvt_6_1_ <= p_175928_3_; ++lvt_6_1_)
            {
                if (lvt_5_1_ * lvt_5_1_ + lvt_6_1_ * lvt_6_1_ <= lvt_4_1_)
                {
                    BlockPos lvt_7_1_ = p_175928_2_.add(lvt_5_1_, 0, lvt_6_1_);
                    Material lvt_8_1_ = worldIn.getBlockState(lvt_7_1_).getBlock().getMaterial();

                    if (lvt_8_1_ == Material.air || lvt_8_1_ == Material.leaves)
                    {
                        this.setBlockAndNotifyAdequately(worldIn, lvt_7_1_, this.leavesMetadata);
                    }
                }
            }
        }
    }
}
