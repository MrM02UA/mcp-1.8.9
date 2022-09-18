package net.minecraft.world.gen.feature;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockHelper;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenMinable extends WorldGenerator
{
    private final IBlockState oreBlock;

    /** The number of blocks to generate. */
    private final int numberOfBlocks;
    private final Predicate<IBlockState> predicate;

    public WorldGenMinable(IBlockState state, int blockCount)
    {
        this(state, blockCount, BlockHelper.forBlock(Blocks.stone));
    }

    public WorldGenMinable(IBlockState state, int blockCount, Predicate<IBlockState> p_i45631_3_)
    {
        this.oreBlock = state;
        this.numberOfBlocks = blockCount;
        this.predicate = p_i45631_3_;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        float lvt_4_1_ = rand.nextFloat() * (float)Math.PI;
        double lvt_5_1_ = (double)((float)(position.getX() + 8) + MathHelper.sin(lvt_4_1_) * (float)this.numberOfBlocks / 8.0F);
        double lvt_7_1_ = (double)((float)(position.getX() + 8) - MathHelper.sin(lvt_4_1_) * (float)this.numberOfBlocks / 8.0F);
        double lvt_9_1_ = (double)((float)(position.getZ() + 8) + MathHelper.cos(lvt_4_1_) * (float)this.numberOfBlocks / 8.0F);
        double lvt_11_1_ = (double)((float)(position.getZ() + 8) - MathHelper.cos(lvt_4_1_) * (float)this.numberOfBlocks / 8.0F);
        double lvt_13_1_ = (double)(position.getY() + rand.nextInt(3) - 2);
        double lvt_15_1_ = (double)(position.getY() + rand.nextInt(3) - 2);

        for (int lvt_17_1_ = 0; lvt_17_1_ < this.numberOfBlocks; ++lvt_17_1_)
        {
            float lvt_18_1_ = (float)lvt_17_1_ / (float)this.numberOfBlocks;
            double lvt_19_1_ = lvt_5_1_ + (lvt_7_1_ - lvt_5_1_) * (double)lvt_18_1_;
            double lvt_21_1_ = lvt_13_1_ + (lvt_15_1_ - lvt_13_1_) * (double)lvt_18_1_;
            double lvt_23_1_ = lvt_9_1_ + (lvt_11_1_ - lvt_9_1_) * (double)lvt_18_1_;
            double lvt_25_1_ = rand.nextDouble() * (double)this.numberOfBlocks / 16.0D;
            double lvt_27_1_ = (double)(MathHelper.sin((float)Math.PI * lvt_18_1_) + 1.0F) * lvt_25_1_ + 1.0D;
            double lvt_29_1_ = (double)(MathHelper.sin((float)Math.PI * lvt_18_1_) + 1.0F) * lvt_25_1_ + 1.0D;
            int lvt_31_1_ = MathHelper.floor_double(lvt_19_1_ - lvt_27_1_ / 2.0D);
            int lvt_32_1_ = MathHelper.floor_double(lvt_21_1_ - lvt_29_1_ / 2.0D);
            int lvt_33_1_ = MathHelper.floor_double(lvt_23_1_ - lvt_27_1_ / 2.0D);
            int lvt_34_1_ = MathHelper.floor_double(lvt_19_1_ + lvt_27_1_ / 2.0D);
            int lvt_35_1_ = MathHelper.floor_double(lvt_21_1_ + lvt_29_1_ / 2.0D);
            int lvt_36_1_ = MathHelper.floor_double(lvt_23_1_ + lvt_27_1_ / 2.0D);

            for (int lvt_37_1_ = lvt_31_1_; lvt_37_1_ <= lvt_34_1_; ++lvt_37_1_)
            {
                double lvt_38_1_ = ((double)lvt_37_1_ + 0.5D - lvt_19_1_) / (lvt_27_1_ / 2.0D);

                if (lvt_38_1_ * lvt_38_1_ < 1.0D)
                {
                    for (int lvt_40_1_ = lvt_32_1_; lvt_40_1_ <= lvt_35_1_; ++lvt_40_1_)
                    {
                        double lvt_41_1_ = ((double)lvt_40_1_ + 0.5D - lvt_21_1_) / (lvt_29_1_ / 2.0D);

                        if (lvt_38_1_ * lvt_38_1_ + lvt_41_1_ * lvt_41_1_ < 1.0D)
                        {
                            for (int lvt_43_1_ = lvt_33_1_; lvt_43_1_ <= lvt_36_1_; ++lvt_43_1_)
                            {
                                double lvt_44_1_ = ((double)lvt_43_1_ + 0.5D - lvt_23_1_) / (lvt_27_1_ / 2.0D);

                                if (lvt_38_1_ * lvt_38_1_ + lvt_41_1_ * lvt_41_1_ + lvt_44_1_ * lvt_44_1_ < 1.0D)
                                {
                                    BlockPos lvt_46_1_ = new BlockPos(lvt_37_1_, lvt_40_1_, lvt_43_1_);

                                    if (this.predicate.apply(worldIn.getBlockState(lvt_46_1_)))
                                    {
                                        worldIn.setBlockState(lvt_46_1_, this.oreBlock, 2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
