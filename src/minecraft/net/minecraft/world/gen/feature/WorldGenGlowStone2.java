package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class WorldGenGlowStone2 extends WorldGenerator
{
    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        if (!worldIn.isAirBlock(position))
        {
            return false;
        }
        else if (worldIn.getBlockState(position.up()).getBlock() != Blocks.netherrack)
        {
            return false;
        }
        else
        {
            worldIn.setBlockState(position, Blocks.glowstone.getDefaultState(), 2);

            for (int lvt_4_1_ = 0; lvt_4_1_ < 1500; ++lvt_4_1_)
            {
                BlockPos lvt_5_1_ = position.add(rand.nextInt(8) - rand.nextInt(8), -rand.nextInt(12), rand.nextInt(8) - rand.nextInt(8));

                if (worldIn.getBlockState(lvt_5_1_).getBlock().getMaterial() == Material.air)
                {
                    int lvt_6_1_ = 0;

                    for (EnumFacing lvt_10_1_ : EnumFacing.values())
                    {
                        if (worldIn.getBlockState(lvt_5_1_.offset(lvt_10_1_)).getBlock() == Blocks.glowstone)
                        {
                            ++lvt_6_1_;
                        }

                        if (lvt_6_1_ > 1)
                        {
                            break;
                        }
                    }

                    if (lvt_6_1_ == 1)
                    {
                        worldIn.setBlockState(lvt_5_1_, Blocks.glowstone.getDefaultState(), 2);
                    }
                }
            }

            return true;
        }
    }
}
