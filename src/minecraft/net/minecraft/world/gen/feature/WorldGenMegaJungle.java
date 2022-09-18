package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockVine;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenMegaJungle extends WorldGenHugeTrees
{
    public WorldGenMegaJungle(boolean p_i46448_1_, int p_i46448_2_, int p_i46448_3_, IBlockState p_i46448_4_, IBlockState p_i46448_5_)
    {
        super(p_i46448_1_, p_i46448_2_, p_i46448_3_, p_i46448_4_, p_i46448_5_);
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
            this.func_175930_c(worldIn, position.up(lvt_4_1_), 2);

            for (int lvt_5_1_ = position.getY() + lvt_4_1_ - 2 - rand.nextInt(4); lvt_5_1_ > position.getY() + lvt_4_1_ / 2; lvt_5_1_ -= 2 + rand.nextInt(4))
            {
                float lvt_6_1_ = rand.nextFloat() * (float)Math.PI * 2.0F;
                int lvt_7_1_ = position.getX() + (int)(0.5F + MathHelper.cos(lvt_6_1_) * 4.0F);
                int lvt_8_1_ = position.getZ() + (int)(0.5F + MathHelper.sin(lvt_6_1_) * 4.0F);

                for (int lvt_9_1_ = 0; lvt_9_1_ < 5; ++lvt_9_1_)
                {
                    lvt_7_1_ = position.getX() + (int)(1.5F + MathHelper.cos(lvt_6_1_) * (float)lvt_9_1_);
                    lvt_8_1_ = position.getZ() + (int)(1.5F + MathHelper.sin(lvt_6_1_) * (float)lvt_9_1_);
                    this.setBlockAndNotifyAdequately(worldIn, new BlockPos(lvt_7_1_, lvt_5_1_ - 3 + lvt_9_1_ / 2, lvt_8_1_), this.woodMetadata);
                }

                int lvt_9_2_ = 1 + rand.nextInt(2);
                int lvt_10_1_ = lvt_5_1_;

                for (int lvt_11_1_ = lvt_5_1_ - lvt_9_2_; lvt_11_1_ <= lvt_10_1_; ++lvt_11_1_)
                {
                    int lvt_12_1_ = lvt_11_1_ - lvt_10_1_;
                    this.func_175928_b(worldIn, new BlockPos(lvt_7_1_, lvt_11_1_, lvt_8_1_), 1 - lvt_12_1_);
                }
            }

            for (int lvt_6_2_ = 0; lvt_6_2_ < lvt_4_1_; ++lvt_6_2_)
            {
                BlockPos lvt_7_2_ = position.up(lvt_6_2_);

                if (this.func_150523_a(worldIn.getBlockState(lvt_7_2_).getBlock()))
                {
                    this.setBlockAndNotifyAdequately(worldIn, lvt_7_2_, this.woodMetadata);

                    if (lvt_6_2_ > 0)
                    {
                        this.func_181632_a(worldIn, rand, lvt_7_2_.west(), BlockVine.EAST);
                        this.func_181632_a(worldIn, rand, lvt_7_2_.north(), BlockVine.SOUTH);
                    }
                }

                if (lvt_6_2_ < lvt_4_1_ - 1)
                {
                    BlockPos lvt_8_2_ = lvt_7_2_.east();

                    if (this.func_150523_a(worldIn.getBlockState(lvt_8_2_).getBlock()))
                    {
                        this.setBlockAndNotifyAdequately(worldIn, lvt_8_2_, this.woodMetadata);

                        if (lvt_6_2_ > 0)
                        {
                            this.func_181632_a(worldIn, rand, lvt_8_2_.east(), BlockVine.WEST);
                            this.func_181632_a(worldIn, rand, lvt_8_2_.north(), BlockVine.SOUTH);
                        }
                    }

                    BlockPos lvt_9_3_ = lvt_7_2_.south().east();

                    if (this.func_150523_a(worldIn.getBlockState(lvt_9_3_).getBlock()))
                    {
                        this.setBlockAndNotifyAdequately(worldIn, lvt_9_3_, this.woodMetadata);

                        if (lvt_6_2_ > 0)
                        {
                            this.func_181632_a(worldIn, rand, lvt_9_3_.east(), BlockVine.WEST);
                            this.func_181632_a(worldIn, rand, lvt_9_3_.south(), BlockVine.NORTH);
                        }
                    }

                    BlockPos lvt_10_2_ = lvt_7_2_.south();

                    if (this.func_150523_a(worldIn.getBlockState(lvt_10_2_).getBlock()))
                    {
                        this.setBlockAndNotifyAdequately(worldIn, lvt_10_2_, this.woodMetadata);

                        if (lvt_6_2_ > 0)
                        {
                            this.func_181632_a(worldIn, rand, lvt_10_2_.west(), BlockVine.EAST);
                            this.func_181632_a(worldIn, rand, lvt_10_2_.south(), BlockVine.NORTH);
                        }
                    }
                }
            }

            return true;
        }
    }

    private void func_181632_a(World p_181632_1_, Random p_181632_2_, BlockPos p_181632_3_, PropertyBool p_181632_4_)
    {
        if (p_181632_2_.nextInt(3) > 0 && p_181632_1_.isAirBlock(p_181632_3_))
        {
            this.setBlockAndNotifyAdequately(p_181632_1_, p_181632_3_, Blocks.vine.getDefaultState().withProperty(p_181632_4_, Boolean.valueOf(true)));
        }
    }

    private void func_175930_c(World worldIn, BlockPos p_175930_2_, int p_175930_3_)
    {
        int lvt_4_1_ = 2;

        for (int lvt_5_1_ = -lvt_4_1_; lvt_5_1_ <= 0; ++lvt_5_1_)
        {
            this.func_175925_a(worldIn, p_175930_2_.up(lvt_5_1_), p_175930_3_ + 1 - lvt_5_1_);
        }
    }
}
