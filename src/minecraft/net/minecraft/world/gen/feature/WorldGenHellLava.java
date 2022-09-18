package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenHellLava extends WorldGenerator
{
    private final Block field_150553_a;
    private final boolean field_94524_b;

    public WorldGenHellLava(Block p_i45453_1_, boolean p_i45453_2_)
    {
        this.field_150553_a = p_i45453_1_;
        this.field_94524_b = p_i45453_2_;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        if (worldIn.getBlockState(position.up()).getBlock() != Blocks.netherrack)
        {
            return false;
        }
        else if (worldIn.getBlockState(position).getBlock().getMaterial() != Material.air && worldIn.getBlockState(position).getBlock() != Blocks.netherrack)
        {
            return false;
        }
        else
        {
            int lvt_4_1_ = 0;

            if (worldIn.getBlockState(position.west()).getBlock() == Blocks.netherrack)
            {
                ++lvt_4_1_;
            }

            if (worldIn.getBlockState(position.east()).getBlock() == Blocks.netherrack)
            {
                ++lvt_4_1_;
            }

            if (worldIn.getBlockState(position.north()).getBlock() == Blocks.netherrack)
            {
                ++lvt_4_1_;
            }

            if (worldIn.getBlockState(position.south()).getBlock() == Blocks.netherrack)
            {
                ++lvt_4_1_;
            }

            if (worldIn.getBlockState(position.down()).getBlock() == Blocks.netherrack)
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

            if (worldIn.isAirBlock(position.down()))
            {
                ++lvt_5_1_;
            }

            if (!this.field_94524_b && lvt_4_1_ == 4 && lvt_5_1_ == 1 || lvt_4_1_ == 5)
            {
                worldIn.setBlockState(position, this.field_150553_a.getDefaultState(), 2);
                worldIn.forceBlockUpdateTick(this.field_150553_a, position, rand);
            }

            return true;
        }
    }
}
