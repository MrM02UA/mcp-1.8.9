package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.block.BlockBush;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class GeneratorBushFeature extends WorldGenerator
{
    private BlockBush field_175908_a;

    public GeneratorBushFeature(BlockBush p_i45633_1_)
    {
        this.field_175908_a = p_i45633_1_;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        for (int lvt_4_1_ = 0; lvt_4_1_ < 64; ++lvt_4_1_)
        {
            BlockPos lvt_5_1_ = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

            if (worldIn.isAirBlock(lvt_5_1_) && (!worldIn.provider.getHasNoSky() || lvt_5_1_.getY() < 255) && this.field_175908_a.canBlockStay(worldIn, lvt_5_1_, this.field_175908_a.getDefaultState()))
            {
                worldIn.setBlockState(lvt_5_1_, this.field_175908_a.getDefaultState(), 2);
            }
        }

        return true;
    }
}
