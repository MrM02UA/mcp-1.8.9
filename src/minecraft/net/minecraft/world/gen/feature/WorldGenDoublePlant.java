package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenDoublePlant extends WorldGenerator
{
    private BlockDoublePlant.EnumPlantType field_150549_a;

    public void setPlantType(BlockDoublePlant.EnumPlantType p_180710_1_)
    {
        this.field_150549_a = p_180710_1_;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        boolean lvt_4_1_ = false;

        for (int lvt_5_1_ = 0; lvt_5_1_ < 64; ++lvt_5_1_)
        {
            BlockPos lvt_6_1_ = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

            if (worldIn.isAirBlock(lvt_6_1_) && (!worldIn.provider.getHasNoSky() || lvt_6_1_.getY() < 254) && Blocks.double_plant.canPlaceBlockAt(worldIn, lvt_6_1_))
            {
                Blocks.double_plant.placeAt(worldIn, lvt_6_1_, this.field_150549_a, 2);
                lvt_4_1_ = true;
            }
        }

        return lvt_4_1_;
    }
}
