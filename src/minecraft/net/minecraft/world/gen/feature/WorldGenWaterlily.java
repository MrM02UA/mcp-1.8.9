package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenWaterlily extends WorldGenerator
{
    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        for (int lvt_4_1_ = 0; lvt_4_1_ < 10; ++lvt_4_1_)
        {
            int lvt_5_1_ = position.getX() + rand.nextInt(8) - rand.nextInt(8);
            int lvt_6_1_ = position.getY() + rand.nextInt(4) - rand.nextInt(4);
            int lvt_7_1_ = position.getZ() + rand.nextInt(8) - rand.nextInt(8);

            if (worldIn.isAirBlock(new BlockPos(lvt_5_1_, lvt_6_1_, lvt_7_1_)) && Blocks.waterlily.canPlaceBlockAt(worldIn, new BlockPos(lvt_5_1_, lvt_6_1_, lvt_7_1_)))
            {
                worldIn.setBlockState(new BlockPos(lvt_5_1_, lvt_6_1_, lvt_7_1_), Blocks.waterlily.getDefaultState(), 2);
            }
        }

        return true;
    }
}
