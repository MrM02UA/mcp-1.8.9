package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenShrub extends WorldGenTrees
{
    private final IBlockState leavesMetadata;
    private final IBlockState woodMetadata;

    public WorldGenShrub(IBlockState p_i46450_1_, IBlockState p_i46450_2_)
    {
        super(false);
        this.woodMetadata = p_i46450_1_;
        this.leavesMetadata = p_i46450_2_;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        Block lvt_4_1_;

        while (((lvt_4_1_ = worldIn.getBlockState(position).getBlock()).getMaterial() == Material.air || lvt_4_1_.getMaterial() == Material.leaves) && position.getY() > 0)
        {
            position = position.down();
        }

        Block lvt_5_1_ = worldIn.getBlockState(position).getBlock();

        if (lvt_5_1_ == Blocks.dirt || lvt_5_1_ == Blocks.grass)
        {
            position = position.up();
            this.setBlockAndNotifyAdequately(worldIn, position, this.woodMetadata);

            for (int lvt_6_1_ = position.getY(); lvt_6_1_ <= position.getY() + 2; ++lvt_6_1_)
            {
                int lvt_7_1_ = lvt_6_1_ - position.getY();
                int lvt_8_1_ = 2 - lvt_7_1_;

                for (int lvt_9_1_ = position.getX() - lvt_8_1_; lvt_9_1_ <= position.getX() + lvt_8_1_; ++lvt_9_1_)
                {
                    int lvt_10_1_ = lvt_9_1_ - position.getX();

                    for (int lvt_11_1_ = position.getZ() - lvt_8_1_; lvt_11_1_ <= position.getZ() + lvt_8_1_; ++lvt_11_1_)
                    {
                        int lvt_12_1_ = lvt_11_1_ - position.getZ();

                        if (Math.abs(lvt_10_1_) != lvt_8_1_ || Math.abs(lvt_12_1_) != lvt_8_1_ || rand.nextInt(2) != 0)
                        {
                            BlockPos lvt_13_1_ = new BlockPos(lvt_9_1_, lvt_6_1_, lvt_11_1_);

                            if (!worldIn.getBlockState(lvt_13_1_).getBlock().isFullBlock())
                            {
                                this.setBlockAndNotifyAdequately(worldIn, lvt_13_1_, this.leavesMetadata);
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
