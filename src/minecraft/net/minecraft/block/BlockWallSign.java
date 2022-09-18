package net.minecraft.block;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockWallSign extends BlockSign
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    public BlockWallSign()
    {
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @SuppressWarnings("incomplete-switch")
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
        EnumFacing lvt_3_1_ = (EnumFacing)worldIn.getBlockState(pos).getValue(FACING);
        float lvt_4_1_ = 0.28125F;
        float lvt_5_1_ = 0.78125F;
        float lvt_6_1_ = 0.0F;
        float lvt_7_1_ = 1.0F;
        float lvt_8_1_ = 0.125F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

        switch (lvt_3_1_)
        {
            case NORTH:
                this.setBlockBounds(lvt_6_1_, lvt_4_1_, 1.0F - lvt_8_1_, lvt_7_1_, lvt_5_1_, 1.0F);
                break;

            case SOUTH:
                this.setBlockBounds(lvt_6_1_, lvt_4_1_, 0.0F, lvt_7_1_, lvt_5_1_, lvt_8_1_);
                break;

            case WEST:
                this.setBlockBounds(1.0F - lvt_8_1_, lvt_4_1_, lvt_6_1_, 1.0F, lvt_5_1_, lvt_7_1_);
                break;

            case EAST:
                this.setBlockBounds(0.0F, lvt_4_1_, lvt_6_1_, lvt_8_1_, lvt_5_1_, lvt_7_1_);
        }
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        EnumFacing lvt_5_1_ = (EnumFacing)state.getValue(FACING);

        if (!worldIn.getBlockState(pos.offset(lvt_5_1_.getOpposite())).getBlock().getMaterial().isSolid())
        {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }

        super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing lvt_2_1_ = EnumFacing.getFront(meta);

        if (lvt_2_1_.getAxis() == EnumFacing.Axis.Y)
        {
            lvt_2_1_ = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, lvt_2_1_);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((EnumFacing)state.getValue(FACING)).getIndex();
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {FACING});
    }
}
