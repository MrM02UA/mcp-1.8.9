package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class WorldGenVines extends WorldGenerator
{
    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        for (; position.getY() < 128; position = position.up())
        {
            if (worldIn.isAirBlock(position))
            {
                for (EnumFacing lvt_7_1_ : EnumFacing.Plane.HORIZONTAL.facings())
                {
                    if (Blocks.vine.canPlaceBlockOnSide(worldIn, position, lvt_7_1_))
                    {
                        IBlockState lvt_8_1_ = Blocks.vine.getDefaultState().withProperty(BlockVine.NORTH, Boolean.valueOf(lvt_7_1_ == EnumFacing.NORTH)).withProperty(BlockVine.EAST, Boolean.valueOf(lvt_7_1_ == EnumFacing.EAST)).withProperty(BlockVine.SOUTH, Boolean.valueOf(lvt_7_1_ == EnumFacing.SOUTH)).withProperty(BlockVine.WEST, Boolean.valueOf(lvt_7_1_ == EnumFacing.WEST));
                        worldIn.setBlockState(position, lvt_8_1_, 2);
                        break;
                    }
                }
            }
            else
            {
                position = position.add(rand.nextInt(4) - rand.nextInt(4), 0, rand.nextInt(4) - rand.nextInt(4));
            }
        }

        return true;
    }
}
