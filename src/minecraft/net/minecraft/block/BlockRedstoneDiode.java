package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockRedstoneDiode extends BlockDirectional
{
    /** Tells whether the repeater is powered or not */
    protected final boolean isRepeaterPowered;

    protected BlockRedstoneDiode(boolean powered)
    {
        super(Material.circuits);
        this.isRepeaterPowered = powered;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
    }

    public boolean isFullCube()
    {
        return false;
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) ? super.canPlaceBlockAt(worldIn, pos) : false;
    }

    public boolean canBlockStay(World worldIn, BlockPos pos)
    {
        return World.doesBlockHaveSolidTopSurface(worldIn, pos.down());
    }

    /**
     * Called randomly when setTickRandomly is set to true (used by e.g. crops to grow, etc.)
     */
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random)
    {
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!this.isLocked(worldIn, pos, state))
        {
            boolean lvt_5_1_ = this.shouldBePowered(worldIn, pos, state);

            if (this.isRepeaterPowered && !lvt_5_1_)
            {
                worldIn.setBlockState(pos, this.getUnpoweredState(state), 2);
            }
            else if (!this.isRepeaterPowered)
            {
                worldIn.setBlockState(pos, this.getPoweredState(state), 2);

                if (!lvt_5_1_)
                {
                    worldIn.updateBlockTick(pos, this.getPoweredState(state).getBlock(), this.getTickDelay(state), -1);
                }
            }
        }
    }

    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        return side.getAxis() != EnumFacing.Axis.Y;
    }

    protected boolean isPowered(IBlockState state)
    {
        return this.isRepeaterPowered;
    }

    public int getStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side)
    {
        return this.getWeakPower(worldIn, pos, state, side);
    }

    public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side)
    {
        return !this.isPowered(state) ? 0 : (state.getValue(FACING) == side ? this.getActiveSignal(worldIn, pos, state) : 0);
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        if (this.canBlockStay(worldIn, pos))
        {
            this.updateState(worldIn, pos, state);
        }
        else
        {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);

            for (EnumFacing lvt_8_1_ : EnumFacing.values())
            {
                worldIn.notifyNeighborsOfStateChange(pos.offset(lvt_8_1_), this);
            }
        }
    }

    protected void updateState(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!this.isLocked(worldIn, pos, state))
        {
            boolean lvt_4_1_ = this.shouldBePowered(worldIn, pos, state);

            if ((this.isRepeaterPowered && !lvt_4_1_ || !this.isRepeaterPowered && lvt_4_1_) && !worldIn.isBlockTickPending(pos, this))
            {
                int lvt_5_1_ = -1;

                if (this.isFacingTowardsRepeater(worldIn, pos, state))
                {
                    lvt_5_1_ = -3;
                }
                else if (this.isRepeaterPowered)
                {
                    lvt_5_1_ = -2;
                }

                worldIn.updateBlockTick(pos, this, this.getDelay(state), lvt_5_1_);
            }
        }
    }

    public boolean isLocked(IBlockAccess worldIn, BlockPos pos, IBlockState state)
    {
        return false;
    }

    protected boolean shouldBePowered(World worldIn, BlockPos pos, IBlockState state)
    {
        return this.calculateInputStrength(worldIn, pos, state) > 0;
    }

    protected int calculateInputStrength(World worldIn, BlockPos pos, IBlockState state)
    {
        EnumFacing lvt_4_1_ = (EnumFacing)state.getValue(FACING);
        BlockPos lvt_5_1_ = pos.offset(lvt_4_1_);
        int lvt_6_1_ = worldIn.getRedstonePower(lvt_5_1_, lvt_4_1_);

        if (lvt_6_1_ >= 15)
        {
            return lvt_6_1_;
        }
        else
        {
            IBlockState lvt_7_1_ = worldIn.getBlockState(lvt_5_1_);
            return Math.max(lvt_6_1_, lvt_7_1_.getBlock() == Blocks.redstone_wire ? ((Integer)lvt_7_1_.getValue(BlockRedstoneWire.POWER)).intValue() : 0);
        }
    }

    protected int getPowerOnSides(IBlockAccess worldIn, BlockPos pos, IBlockState state)
    {
        EnumFacing lvt_4_1_ = (EnumFacing)state.getValue(FACING);
        EnumFacing lvt_5_1_ = lvt_4_1_.rotateY();
        EnumFacing lvt_6_1_ = lvt_4_1_.rotateYCCW();
        return Math.max(this.getPowerOnSide(worldIn, pos.offset(lvt_5_1_), lvt_5_1_), this.getPowerOnSide(worldIn, pos.offset(lvt_6_1_), lvt_6_1_));
    }

    protected int getPowerOnSide(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        IBlockState lvt_4_1_ = worldIn.getBlockState(pos);
        Block lvt_5_1_ = lvt_4_1_.getBlock();
        return this.canPowerSide(lvt_5_1_) ? (lvt_5_1_ == Blocks.redstone_wire ? ((Integer)lvt_4_1_.getValue(BlockRedstoneWire.POWER)).intValue() : worldIn.getStrongPower(pos, side)) : 0;
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return true;
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        if (this.shouldBePowered(worldIn, pos, state))
        {
            worldIn.scheduleUpdate(pos, this, 1);
        }
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        this.notifyNeighbors(worldIn, pos, state);
    }

    protected void notifyNeighbors(World worldIn, BlockPos pos, IBlockState state)
    {
        EnumFacing lvt_4_1_ = (EnumFacing)state.getValue(FACING);
        BlockPos lvt_5_1_ = pos.offset(lvt_4_1_.getOpposite());
        worldIn.notifyBlockOfStateChange(lvt_5_1_, this);
        worldIn.notifyNeighborsOfStateExcept(lvt_5_1_, this, lvt_4_1_);
    }

    /**
     * Called when a player destroys this Block
     */
    public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state)
    {
        if (this.isRepeaterPowered)
        {
            for (EnumFacing lvt_7_1_ : EnumFacing.values())
            {
                worldIn.notifyNeighborsOfStateChange(pos.offset(lvt_7_1_), this);
            }
        }

        super.onBlockDestroyedByPlayer(worldIn, pos, state);
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    protected boolean canPowerSide(Block blockIn)
    {
        return blockIn.canProvidePower();
    }

    protected int getActiveSignal(IBlockAccess worldIn, BlockPos pos, IBlockState state)
    {
        return 15;
    }

    public static boolean isRedstoneRepeaterBlockID(Block blockIn)
    {
        return Blocks.unpowered_repeater.isAssociated(blockIn) || Blocks.unpowered_comparator.isAssociated(blockIn);
    }

    public boolean isAssociated(Block other)
    {
        return other == this.getPoweredState(this.getDefaultState()).getBlock() || other == this.getUnpoweredState(this.getDefaultState()).getBlock();
    }

    public boolean isFacingTowardsRepeater(World worldIn, BlockPos pos, IBlockState state)
    {
        EnumFacing lvt_4_1_ = ((EnumFacing)state.getValue(FACING)).getOpposite();
        BlockPos lvt_5_1_ = pos.offset(lvt_4_1_);
        return isRedstoneRepeaterBlockID(worldIn.getBlockState(lvt_5_1_).getBlock()) ? worldIn.getBlockState(lvt_5_1_).getValue(FACING) != lvt_4_1_ : false;
    }

    protected int getTickDelay(IBlockState state)
    {
        return this.getDelay(state);
    }

    protected abstract int getDelay(IBlockState state);

    protected abstract IBlockState getPoweredState(IBlockState unpoweredState);

    protected abstract IBlockState getUnpoweredState(IBlockState poweredState);

    public boolean isAssociatedBlock(Block other)
    {
        return this.isAssociated(other);
    }

    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.CUTOUT;
    }
}
