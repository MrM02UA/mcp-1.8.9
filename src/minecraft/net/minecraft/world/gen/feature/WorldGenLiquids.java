package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenLiquids extends WorldGenerator
{
    private Block block;

    public WorldGenLiquids(Block p_i45465_1_)
    {
        this.block = p_i45465_1_;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        if (worldIn.getBlockState(position.up()).getBlock() != Blocks.stone)
        {
            return false;
        }
        else if (worldIn.getBlockState(position.down()).getBlock() != Blocks.stone)
        {
            return false;
        }
        else if (worldIn.getBlockState(position).getBlock().getMaterial() != Material.air && worldIn.getBlockState(position).getBlock() != Blocks.stone)
        {
            return false;
        }
        else
        {
            int lvt_4_1_ = 0;

            if (worldIn.getBlockState(position.west()).getBlock() == Blocks.stone)
            {
                ++lvt_4_1_;
            }

            if (worldIn.getBlockState(position.east()).getBlock() == Blocks.stone)
            {
                ++lvt_4_1_;
            }

            if (worldIn.getBlockState(position.north()).getBlock() == Blocks.stone)
            {
                ++lvt_4_1_;
            }

            if (worldIn.getBlockState(position.south()).getBlock() == Blocks.stone)
            {
                ++lvt_4_1_;
            }

            int lvt_5_1_ = 0;

            if (worldIn.isAirBlock(position.west()))
            {
                ++lvt_5_1_;
            }

            if (worldIn.isAirBlock(position.east()))
            {
                ++lvt_5_1_;
            }

            if (worldIn.isAirBlock(position.north()))
            {
                ++lvt_5_1_;
            }

            if (worldIn.isAirBlock(position.south()))
            {
                ++lvt_5_1_;
            }

            if (lvt_4_1_ == 3 && lvt_5_1_ == 1)
            {
                worldIn.setBlockState(position, this.block.getDefaultState(), 2);
                worldIn.forceBlockUpdateTick(this.block, position, rand);
            }

            return true;
        }
    }
}
