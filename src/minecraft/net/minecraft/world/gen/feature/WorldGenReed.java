package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenReed extends WorldGenerator
{
    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        for (int lvt_4_1_ = 0; lvt_4_1_ < 20; ++lvt_4_1_)
        {
            BlockPos lvt_5_1_ = position.add(rand.nextInt(4) - rand.nextInt(4), 0, rand.nextInt(4) - rand.nextInt(4));

            if (worldIn.isAirBlock(lvt_5_1_))
            {
                BlockPos lvt_6_1_ = lvt_5_1_.down();

                if (worldIn.getBlockState(lvt_6_1_.west()).getBlock().getMaterial() == Material.water || worldIn.getBlockState(lvt_6_1_.east()).getBlock().getMaterial() == Material.water || worldIn.getBlockState(lvt_6_1_.north()).getBlock().getMaterial() == Material.water || worldIn.getBlockState(lvt_6_1_.south()).getBlock().getMaterial() == Material.water)
                {
                    int lvt_7_1_ = 2 + rand.nextInt(rand.nextInt(3) + 1);

                    for (int lvt_8_1_ = 0; lvt_8_1_ < lvt_7_1_; ++lvt_8_1_)
                    {
                        if (Blocks.reeds.canBlockStay(worldIn, lvt_5_1_))
                        {
                            worldIn.setBlockState(lvt_5_1_.up(lvt_8_1_), Blocks.reeds.getDefaultState(), 2);
                        }
                    }
                }
            }
        }

        return true;
    }
}
