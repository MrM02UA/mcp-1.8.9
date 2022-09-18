package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenBlockBlob extends WorldGenerator
{
    private final Block field_150545_a;
    private final int field_150544_b;

    public WorldGenBlockBlob(Block p_i45450_1_, int p_i45450_2_)
    {
        super(false);
        this.field_150545_a = p_i45450_1_;
        this.field_150544_b = p_i45450_2_;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        while (true)
        {
            label0:
            {
                if (position.getY() > 3)
                {
                    if (worldIn.isAirBlock(position.down()))
                    {
                        break label0;
                    }

                    Block lvt_4_1_ = worldIn.getBlockState(position.down()).getBlock();

                    if (lvt_4_1_ != Blocks.grass && lvt_4_1_ != Blocks.dirt && lvt_4_1_ != Blocks.stone)
                    {
                        break label0;
                    }
                }

                if (position.getY() <= 3)
                {
                    return false;
                }

                int lvt_4_2_ = this.field_150544_b;

                for (int lvt_5_1_ = 0; lvt_4_2_ >= 0 && lvt_5_1_ < 3; ++lvt_5_1_)
                {
                    int lvt_6_1_ = lvt_4_2_ + rand.nextInt(2);
                    int lvt_7_1_ = lvt_4_2_ + rand.nextInt(2);
                    int lvt_8_1_ = lvt_4_2_ + rand.nextInt(2);
                    float lvt_9_1_ = (float)(lvt_6_1_ + lvt_7_1_ + lvt_8_1_) * 0.333F + 0.5F;

                    for (BlockPos lvt_11_1_ : BlockPos.getAllInBox(position.add(-lvt_6_1_, -lvt_7_1_, -lvt_8_1_), position.add(lvt_6_1_, lvt_7_1_, lvt_8_1_)))
                    {
                        if (lvt_11_1_.distanceSq(position) <= (double)(lvt_9_1_ * lvt_9_1_))
                        {
                            worldIn.setBlockState(lvt_11_1_, this.field_150545_a.getDefaultState(), 4);
                        }
                    }

                    position = position.add(-(lvt_4_2_ + 1) + rand.nextInt(2 + lvt_4_2_ * 2), 0 - rand.nextInt(2), -(lvt_4_2_ + 1) + rand.nextInt(2 + lvt_4_2_ * 2));
                }

                return true;
            }
            position = position.down();
        }
    }
}
