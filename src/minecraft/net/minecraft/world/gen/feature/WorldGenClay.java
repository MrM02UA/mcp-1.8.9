package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenClay extends WorldGenerator
{
    private Block field_150546_a = Blocks.clay;

    /** The number of blocks to generate. */
    private int numberOfBlocks;

    public WorldGenClay(int p_i2011_1_)
    {
        this.numberOfBlocks = p_i2011_1_;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        if (worldIn.getBlockState(position).getBlock().getMaterial() != Material.water)
        {
            return false;
        }
        else
        {
            int lvt_4_1_ = rand.nextInt(this.numberOfBlocks - 2) + 2;
            int lvt_5_1_ = 1;

            for (int lvt_6_1_ = position.getX() - lvt_4_1_; lvt_6_1_ <= position.getX() + lvt_4_1_; ++lvt_6_1_)
            {
                for (int lvt_7_1_ = position.getZ() - lvt_4_1_; lvt_7_1_ <= position.getZ() + lvt_4_1_; ++lvt_7_1_)
                {
                    int lvt_8_1_ = lvt_6_1_ - position.getX();
                    int lvt_9_1_ = lvt_7_1_ - position.getZ();

                    if (lvt_8_1_ * lvt_8_1_ + lvt_9_1_ * lvt_9_1_ <= lvt_4_1_ * lvt_4_1_)
                    {
                        for (int lvt_10_1_ = position.getY() - lvt_5_1_; lvt_10_1_ <= position.getY() + lvt_5_1_; ++lvt_10_1_)
                        {
                            BlockPos lvt_11_1_ = new BlockPos(lvt_6_1_, lvt_10_1_, lvt_7_1_);
                            Block lvt_12_1_ = worldIn.getBlockState(lvt_11_1_).getBlock();

                            if (lvt_12_1_ == Blocks.dirt || lvt_12_1_ == Blocks.clay)
                            {
                                worldIn.setBlockState(lvt_11_1_, this.field_150546_a.getDefaultState(), 2);
                            }
                        }
                    }
                }
            }

            return true;
        }
    }
}
