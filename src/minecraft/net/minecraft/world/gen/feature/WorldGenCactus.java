package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenCactus extends WorldGenerator
{
    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        for (int lvt_4_1_ = 0; lvt_4_1_ < 10; ++lvt_4_1_)
        {
            BlockPos lvt_5_1_ = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

            if (worldIn.isAirBlock(lvt_5_1_))
            {
                int lvt_6_1_ = 1 + rand.nextInt(rand.nextInt(3) + 1);

                for (int lvt_7_1_ = 0; lvt_7_1_ < lvt_6_1_; ++lvt_7_1_)
                {
                    if (Blocks.cactus.canBlockStay(worldIn, lvt_5_1_))
                    {
                        worldIn.setBlockState(lvt_5_1_.up(lvt_7_1_), Blocks.cactus.getDefaultState(), 2);
                    }
                }
            }
        }

        return true;
    }
}
