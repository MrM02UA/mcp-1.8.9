package net.minecraft.world.gen.feature;

import com.google.common.base.Predicates;
import java.util.Random;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockStateHelper;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class WorldGenDesertWells extends WorldGenerator
{
    private static final BlockStateHelper field_175913_a = BlockStateHelper.forBlock(Blocks.sand).where(BlockSand.VARIANT, Predicates.equalTo(BlockSand.EnumType.SAND));
    private final IBlockState field_175911_b = Blocks.stone_slab.getDefaultState().withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.SAND).withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.BOTTOM);
    private final IBlockState field_175912_c = Blocks.sandstone.getDefaultState();
    private final IBlockState field_175910_d = Blocks.flowing_water.getDefaultState();

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        while (worldIn.isAirBlock(position) && position.getY() > 2)
        {
            position = position.down();
        }

        if (!field_175913_a.apply(worldIn.getBlockState(position)))
        {
            return false;
        }
        else
        {
            for (int lvt_4_1_ = -2; lvt_4_1_ <= 2; ++lvt_4_1_)
            {
                for (int lvt_5_1_ = -2; lvt_5_1_ <= 2; ++lvt_5_1_)
                {
                    if (worldIn.isAirBlock(position.add(lvt_4_1_, -1, lvt_5_1_)) && worldIn.isAirBlock(position.add(lvt_4_1_, -2, lvt_5_1_)))
                    {
                        return false;
                    }
                }
            }

            for (int lvt_4_2_ = -1; lvt_4_2_ <= 0; ++lvt_4_2_)
            {
                for (int lvt_5_2_ = -2; lvt_5_2_ <= 2; ++lvt_5_2_)
                {
                    for (int lvt_6_1_ = -2; lvt_6_1_ <= 2; ++lvt_6_1_)
                    {
                        worldIn.setBlockState(position.add(lvt_5_2_, lvt_4_2_, lvt_6_1_), this.field_175912_c, 2);
                    }
                }
            }

            worldIn.setBlockState(position, this.field_175910_d, 2);

            for (EnumFacing lvt_5_3_ : EnumFacing.Plane.HORIZONTAL)
            {
                worldIn.setBlockState(position.offset(lvt_5_3_), this.field_175910_d, 2);
            }

            for (int lvt_4_4_ = -2; lvt_4_4_ <= 2; ++lvt_4_4_)
            {
                for (int lvt_5_4_ = -2; lvt_5_4_ <= 2; ++lvt_5_4_)
                {
                    if (lvt_4_4_ == -2 || lvt_4_4_ == 2 || lvt_5_4_ == -2 || lvt_5_4_ == 2)
                    {
                        worldIn.setBlockState(position.add(lvt_4_4_, 1, lvt_5_4_), this.field_175912_c, 2);
                    }
                }
            }

            worldIn.setBlockState(position.add(2, 1, 0), this.field_175911_b, 2);
            worldIn.setBlockState(position.add(-2, 1, 0), this.field_175911_b, 2);
            worldIn.setBlockState(position.add(0, 1, 2), this.field_175911_b, 2);
            worldIn.setBlockState(position.add(0, 1, -2), this.field_175911_b, 2);

            for (int lvt_4_5_ = -1; lvt_4_5_ <= 1; ++lvt_4_5_)
            {
                for (int lvt_5_5_ = -1; lvt_5_5_ <= 1; ++lvt_5_5_)
                {
                    if (lvt_4_5_ == 0 && lvt_5_5_ == 0)
                    {
                        worldIn.setBlockState(position.add(lvt_4_5_, 4, lvt_5_5_), this.field_175912_c, 2);
                    }
                    else
                    {
                        worldIn.setBlockState(position.add(lvt_4_5_, 4, lvt_5_5_), this.field_175911_b, 2);
                    }
                }
            }

            for (int lvt_4_6_ = 1; lvt_4_6_ <= 3; ++lvt_4_6_)
            {
                worldIn.setBlockState(position.add(-1, lvt_4_6_, -1), this.field_175912_c, 2);
                worldIn.setBlockState(position.add(-1, lvt_4_6_, 1), this.field_175912_c, 2);
                worldIn.setBlockState(position.add(1, lvt_4_6_, -1), this.field_175912_c, 2);
                worldIn.setBlockState(position.add(1, lvt_4_6_, 1), this.field_175912_c, 2);
            }

            return true;
        }
    }
}
