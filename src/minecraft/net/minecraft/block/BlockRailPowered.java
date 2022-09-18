package net.minecraft.block;

import com.google.common.base.Predicate;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockRailPowered extends BlockRailBase
{
    public static final PropertyEnum<BlockRailBase.EnumRailDirection> SHAPE = PropertyEnum.<BlockRailBase.EnumRailDirection>create("shape", BlockRailBase.EnumRailDirection.class, new Predicate<BlockRailBase.EnumRailDirection>()
    {
        public boolean apply(BlockRailBase.EnumRailDirection p_apply_1_)
        {
            return p_apply_1_ != BlockRailBase.EnumRailDirection.NORTH_EAST && p_apply_1_ != BlockRailBase.EnumRailDirection.NORTH_WEST && p_apply_1_ != BlockRailBase.EnumRailDirection.SOUTH_EAST && p_apply_1_ != BlockRailBase.EnumRailDirection.SOUTH_WEST;
        }
        public boolean apply(Object p_apply_1_)
        {
            return this.apply((BlockRailBase.EnumRailDirection)p_apply_1_);
        }
    });
    public static final PropertyBool POWERED = PropertyBool.create("powered");

    protected BlockRailPowered()
    {
        super(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_SOUTH).withProperty(POWERED, Boolean.valueOf(false)));
    }

    @SuppressWarnings("incomplete-switch")
    protected boolean func_176566_a(World worldIn, BlockPos pos, IBlockState state, boolean p_176566_4_, int p_176566_5_)
    {
        if (p_176566_5_ >= 8)
        {
            return false;
        }
        else
        {
            int lvt_6_1_ = pos.getX();
            int lvt_7_1_ = pos.getY();
            int lvt_8_1_ = pos.getZ();
            boolean lvt_9_1_ = true;
            BlockRailBase.EnumRailDirection lvt_10_1_ = (BlockRailBase.EnumRailDirection)state.getValue(SHAPE);

            switch (lvt_10_1_)
            {
                case NORTH_SOUTH:
                    if (p_176566_4_)
                    {
                        ++lvt_8_1_;
                    }
                    else
                    {
                        --lvt_8_1_;
                    }

                    break;

                case EAST_WEST:
                    if (p_176566_4_)
                    {
                        --lvt_6_1_;
                    }
                    else
                    {
                        ++lvt_6_1_;
                    }

                    break;

                case ASCENDING_EAST:
                    if (p_176566_4_)
                    {
                        --lvt_6_1_;
                    }
                    else
                    {
                        ++lvt_6_1_;
                        ++lvt_7_1_;
                        lvt_9_1_ = false;
                    }

                    lvt_10_1_ = BlockRailBase.EnumRailDirection.EAST_WEST;
                    break;

                case ASCENDING_WEST:
                    if (p_176566_4_)
                    {
                        --lvt_6_1_;
                        ++lvt_7_1_;
                        lvt_9_1_ = false;
                    }
                    else
                    {
                        ++lvt_6_1_;
                    }

                    lvt_10_1_ = BlockRailBase.EnumRailDirection.EAST_WEST;
                    break;

                case ASCENDING_NORTH:
                    if (p_176566_4_)
                    {
                        ++lvt_8_1_;
                    }
                    else
                    {
                        --lvt_8_1_;
                        ++lvt_7_1_;
                        lvt_9_1_ = false;
                    }

                    lvt_10_1_ = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
                    break;

                case ASCENDING_SOUTH:
                    if (p_176566_4_)
                    {
                        ++lvt_8_1_;
                        ++lvt_7_1_;
                        lvt_9_1_ = false;
                    }
                    else
                    {
                        --lvt_8_1_;
                    }

                    lvt_10_1_ = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            }

            return this.func_176567_a(worldIn, new BlockPos(lvt_6_1_, lvt_7_1_, lvt_8_1_), p_176566_4_, p_176566_5_, lvt_10_1_) ? true : lvt_9_1_ && this.func_176567_a(worldIn, new BlockPos(lvt_6_1_, lvt_7_1_ - 1, lvt_8_1_), p_176566_4_, p_176566_5_, lvt_10_1_);
        }
    }

    protected boolean func_176567_a(World worldIn, BlockPos p_176567_2_, boolean p_176567_3_, int distance, BlockRailBase.EnumRailDirection p_176567_5_)
    {
        IBlockState lvt_6_1_ = worldIn.getBlockState(p_176567_2_);

        if (lvt_6_1_.getBlock() != this)
        {
            return false;
        }
        else
        {
            BlockRailBase.EnumRailDirection lvt_7_1_ = (BlockRailBase.EnumRailDirection)lvt_6_1_.getValue(SHAPE);
            return p_176567_5_ != BlockRailBase.EnumRailDirection.EAST_WEST || lvt_7_1_ != BlockRailBase.EnumRailDirection.NORTH_SOUTH && lvt_7_1_ != BlockRailBase.EnumRailDirection.ASCENDING_NORTH && lvt_7_1_ != BlockRailBase.EnumRailDirection.ASCENDING_SOUTH ? (p_176567_5_ != BlockRailBase.EnumRailDirection.NORTH_SOUTH || lvt_7_1_ != BlockRailBase.EnumRailDirection.EAST_WEST && lvt_7_1_ != BlockRailBase.EnumRailDirection.ASCENDING_EAST && lvt_7_1_ != BlockRailBase.EnumRailDirection.ASCENDING_WEST ? (((Boolean)lvt_6_1_.getValue(POWERED)).booleanValue() ? (worldIn.isBlockPowered(p_176567_2_) ? true : this.func_176566_a(worldIn, p_176567_2_, lvt_6_1_, p_176567_3_, distance + 1)) : false) : false) : false;
        }
    }

    protected void onNeighborChangedInternal(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        boolean lvt_5_1_ = ((Boolean)state.getValue(POWERED)).booleanValue();
        boolean lvt_6_1_ = worldIn.isBlockPowered(pos) || this.func_176566_a(worldIn, pos, state, true, 0) || this.func_176566_a(worldIn, pos, state, false, 0);

        if (lvt_6_1_ != lvt_5_1_)
        {
            worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(lvt_6_1_)), 3);
            worldIn.notifyNeighborsOfStateChange(pos.down(), this);

            if (((BlockRailBase.EnumRailDirection)state.getValue(SHAPE)).isAscending())
            {
                worldIn.notifyNeighborsOfStateChange(pos.up(), this);
            }
        }
    }

    public IProperty<BlockRailBase.EnumRailDirection> getShapeProperty()
    {
        return SHAPE;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.byMetadata(meta & 7)).withProperty(POWERED, Boolean.valueOf((meta & 8) > 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int lvt_2_1_ = 0;
        lvt_2_1_ = lvt_2_1_ | ((BlockRailBase.EnumRailDirection)state.getValue(SHAPE)).getMetadata();

        if (((Boolean)state.getValue(POWERED)).booleanValue())
        {
            lvt_2_1_ |= 8;
        }

        return lvt_2_1_;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {SHAPE, POWERED});
    }
}
