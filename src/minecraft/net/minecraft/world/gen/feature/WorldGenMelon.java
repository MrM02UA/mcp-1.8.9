package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenMelon extends WorldGenerator
{
    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        for (int lvt_4_1_ = 0; lvt_4_1_ < 64; ++lvt_4_1_)
        {
            BlockPos lvt_5_1_ = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

            if (Blocks.melon_block.canPlaceBlockAt(worldIn, lvt_5_1_) && worldIn.getBlockState(lvt_5_1_.down()).getBlock() == Blocks.grass)
            {
                worldIn.setBlockState(lvt_5_1_, Blocks.melon_block.getDefaultState(), 2);
            }
        }

        return true;
    }
}
