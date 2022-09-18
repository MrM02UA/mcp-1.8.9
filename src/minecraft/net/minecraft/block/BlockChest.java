package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;

public class BlockChest extends BlockContainer
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    /** 0 : Normal chest, 1 : Trapped chest */
    public final int chestType;

    protected BlockChest(int type)
    {
        super(Material.wood);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        this.chestType = type;
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean isFullCube()
    {
        return false;
    }

    /**
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
    public int getRenderType()
    {
        return 2;
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
        if (worldIn.getBlockState(pos.north()).getBlock() == this)
        {
            this.setBlockBounds(0.0625F, 0.0F, 0.0F, 0.9375F, 0.875F, 0.9375F);
        }
        else if (worldIn.getBlockState(pos.south()).getBlock() == this)
        {
            this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 1.0F);
        }
        else if (worldIn.getBlockState(pos.west()).getBlock() == this)
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
        }
        else if (worldIn.getBlockState(pos.east()).getBlock() == this)
        {
            this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 1.0F, 0.875F, 0.9375F);
        }
        else
        {
            this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
        }
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        this.checkForSurroundingChests(worldIn, pos, state);

        for (EnumFacing lvt_5_1_ : EnumFacing.Plane.HORIZONTAL)
        {
            BlockPos lvt_6_1_ = pos.offset(lvt_5_1_);
            IBlockState lvt_7_1_ = worldIn.getBlockState(lvt_6_1_);

            if (lvt_7_1_.getBlock() == this)
            {
                this.checkForSurroundingChests(worldIn, lvt_6_1_, lvt_7_1_);
            }
        }
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        EnumFacing lvt_6_1_ = EnumFacing.getHorizontal(MathHelper.floor_double((double)(placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3).getOpposite();
        state = state.withProperty(FACING, lvt_6_1_);
        BlockPos lvt_7_1_ = pos.north();
        BlockPos lvt_8_1_ = pos.south();
        BlockPos lvt_9_1_ = pos.west();
        BlockPos lvt_10_1_ = pos.east();
        boolean lvt_11_1_ = this == worldIn.getBlockState(lvt_7_1_).getBlock();
        boolean lvt_12_1_ = this == worldIn.getBlockState(lvt_8_1_).getBlock();
        boolean lvt_13_1_ = this == worldIn.getBlockState(lvt_9_1_).getBlock();
        boolean lvt_14_1_ = this == worldIn.getBlockState(lvt_10_1_).getBlock();

        if (!lvt_11_1_ && !lvt_12_1_ && !lvt_13_1_ && !lvt_14_1_)
        {
            worldIn.setBlockState(pos, state, 3);
        }
        else if (lvt_6_1_.getAxis() != EnumFacing.Axis.X || !lvt_11_1_ && !lvt_12_1_)
        {
            if (lvt_6_1_.getAxis() == EnumFacing.Axis.Z && (lvt_13_1_ || lvt_14_1_))
            {
                if (lvt_13_1_)
                {
                    worldIn.setBlockState(lvt_9_1_, state, 3);
                }
                else
                {
                    worldIn.setBlockState(lvt_10_1_, state, 3);
                }

                worldIn.setBlockState(pos, state, 3);
            }
        }
        else
        {
            if (lvt_11_1_)
            {
                worldIn.setBlockState(lvt_7_1_, state, 3);
            }
            else
            {
                worldIn.setBlockState(lvt_8_1_, state, 3);
            }

            worldIn.setBlockState(pos, state, 3);
        }

        if (stack.hasDisplayName())
        {
            TileEntity lvt_15_1_ = worldIn.getTileEntity(pos);

            if (lvt_15_1_ instanceof TileEntityChest)
            {
                ((TileEntityChest)lvt_15_1_).setCustomName(stack.getDisplayName());
            }
        }
    }

    public IBlockState checkForSurroundingChests(World worldIn, BlockPos pos, IBlockState state)
    {
        if (worldIn.isRemote)
        {
            return state;
        }
        else
        {
            IBlockState lvt_4_1_ = worldIn.getBlockState(pos.north());
            IBlockState lvt_5_1_ = worldIn.getBlockState(pos.south());
            IBlockState lvt_6_1_ = worldIn.getBlockState(pos.west());
            IBlockState lvt_7_1_ = worldIn.getBlockState(pos.east());
            EnumFacing lvt_8_1_ = (EnumFacing)state.getValue(FACING);
            Block lvt_9_1_ = lvt_4_1_.getBlock();
            Block lvt_10_1_ = lvt_5_1_.getBlock();
            Block lvt_11_1_ = lvt_6_1_.getBlock();
            Block lvt_12_1_ = lvt_7_1_.getBlock();

            if (lvt_9_1_ != this && lvt_10_1_ != this)
            {
                boolean lvt_13_2_ = lvt_9_1_.isFullBlock();
                boolean lvt_14_2_ = lvt_10_1_.isFullBlock();

                if (lvt_11_1_ == this || lvt_12_1_ == this)
                {
                    BlockPos lvt_15_2_ = lvt_11_1_ == this ? pos.west() : pos.east();
                    IBlockState lvt_16_3_ = worldIn.getBlockState(lvt_15_2_.north());
                    IBlockState lvt_17_2_ = worldIn.getBlockState(lvt_15_2_.south());
                    lvt_8_1_ = EnumFacing.SOUTH;
                    EnumFacing lvt_18_2_;

                    if (lvt_11_1_ == this)
                    {
                        lvt_18_2_ = (EnumFacing)lvt_6_1_.getValue(FACING);
                    }
                    else
                    {
                        lvt_18_2_ = (EnumFacing)lvt_7_1_.getValue(FACING);
                    }

                    if (lvt_18_2_ == EnumFacing.NORTH)
                    {
                        lvt_8_1_ = EnumFacing.NORTH;
                    }

                    Block lvt_19_1_ = lvt_16_3_.getBlock();
                    Block lvt_20_1_ = lvt_17_2_.getBlock();

                    if ((lvt_13_2_ || lvt_19_1_.isFullBlock()) && !lvt_14_2_ && !lvt_20_1_.isFullBlock())
                    {
                        lvt_8_1_ = EnumFacing.SOUTH;
                    }

                    if ((lvt_14_2_ || lvt_20_1_.isFullBlock()) && !lvt_13_2_ && !lvt_19_1_.isFullBlock())
                    {
                        lvt_8_1_ = EnumFacing.NORTH;
                    }
                }
            }
            else
            {
                BlockPos lvt_13_1_ = lvt_9_1_ == this ? pos.north() : pos.south();
                IBlockState lvt_14_1_ = worldIn.getBlockState(lvt_13_1_.west());
                IBlockState lvt_15_1_ = worldIn.getBlockState(lvt_13_1_.east());
                lvt_8_1_ = EnumFacing.EAST;
                EnumFacing lvt_16_1_;

                if (lvt_9_1_ == this)
                {
                    lvt_16_1_ = (EnumFacing)lvt_4_1_.getValue(FACING);
                }
                else
                {
                    lvt_16_1_ = (EnumFacing)lvt_5_1_.getValue(FACING);
                }

                if (lvt_16_1_ == EnumFacing.WEST)
                {
                    lvt_8_1_ = EnumFacing.WEST;
                }

                Block lvt_17_1_ = lvt_14_1_.getBlock();
                Block lvt_18_1_ = lvt_15_1_.getBlock();

                if ((lvt_11_1_.isFullBlock() || lvt_17_1_.isFullBlock()) && !lvt_12_1_.isFullBlock() && !lvt_18_1_.isFullBlock())
                {
                    lvt_8_1_ = EnumFacing.EAST;
                }

                if ((lvt_12_1_.isFullBlock() || lvt_18_1_.isFullBlock()) && !lvt_11_1_.isFullBlock() && !lvt_17_1_.isFullBlock())
                {
                    lvt_8_1_ = EnumFacing.WEST;
                }
            }

            state = state.withProperty(FACING, lvt_8_1_);
            worldIn.setBlockState(pos, state, 3);
            return state;
        }
    }

    public IBlockState correctFacing(World worldIn, BlockPos pos, IBlockState state)
    {
        EnumFacing lvt_4_1_ = null;

        for (EnumFacing lvt_6_1_ : EnumFacing.Plane.HORIZONTAL)
        {
            IBlockState lvt_7_1_ = worldIn.getBlockState(pos.offset(lvt_6_1_));

            if (lvt_7_1_.getBlock() == this)
            {
                return state;
            }

            if (lvt_7_1_.getBlock().isFullBlock())
            {
                if (lvt_4_1_ != null)
                {
                    lvt_4_1_ = null;
                    break;
                }

                lvt_4_1_ = lvt_6_1_;
            }
        }

        if (lvt_4_1_ != null)
        {
            return state.withProperty(FACING, lvt_4_1_.getOpposite());
        }
        else
        {
            EnumFacing lvt_5_2_ = (EnumFacing)state.getValue(FACING);

            if (worldIn.getBlockState(pos.offset(lvt_5_2_)).getBlock().isFullBlock())
            {
                lvt_5_2_ = lvt_5_2_.getOpposite();
            }

            if (worldIn.getBlockState(pos.offset(lvt_5_2_)).getBlock().isFullBlock())
            {
                lvt_5_2_ = lvt_5_2_.rotateY();
            }

            if (worldIn.getBlockState(pos.offset(lvt_5_2_)).getBlock().isFullBlock())
            {
                lvt_5_2_ = lvt_5_2_.getOpposite();
            }

            return state.withProperty(FACING, lvt_5_2_);
        }
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        int lvt_3_1_ = 0;
        BlockPos lvt_4_1_ = pos.west();
        BlockPos lvt_5_1_ = pos.east();
        BlockPos lvt_6_1_ = pos.north();
        BlockPos lvt_7_1_ = pos.south();

        if (worldIn.getBlockState(lvt_4_1_).getBlock() == this)
        {
            if (this.isDoubleChest(worldIn, lvt_4_1_))
            {
                return false;
            }

            ++lvt_3_1_;
        }

        if (worldIn.getBlockState(lvt_5_1_).getBlock() == this)
        {
            if (this.isDoubleChest(worldIn, lvt_5_1_))
            {
                return false;
            }

            ++lvt_3_1_;
        }

        if (worldIn.getBlockState(lvt_6_1_).getBlock() == this)
        {
            if (this.isDoubleChest(worldIn, lvt_6_1_))
            {
                return false;
            }

            ++lvt_3_1_;
        }

        if (worldIn.getBlockState(lvt_7_1_).getBlock() == this)
        {
            if (this.isDoubleChest(worldIn, lvt_7_1_))
            {
                return false;
            }

            ++lvt_3_1_;
        }

        return lvt_3_1_ <= 1;
    }

    private boolean isDoubleChest(World worldIn, BlockPos pos)
    {
        if (worldIn.getBlockState(pos).getBlock() != this)
        {
            return false;
        }
        else
        {
            for (EnumFacing lvt_4_1_ : EnumFacing.Plane.HORIZONTAL)
            {
                if (worldIn.getBlockState(pos.offset(lvt_4_1_)).getBlock() == this)
                {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
        TileEntity lvt_5_1_ = worldIn.getTileEntity(pos);

        if (lvt_5_1_ instanceof TileEntityChest)
        {
            lvt_5_1_.updateContainingBlockInfo();
        }
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity lvt_4_1_ = worldIn.getTileEntity(pos);

        if (lvt_4_1_ instanceof IInventory)
        {
            InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)lvt_4_1_);
            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        else
        {
            ILockableContainer lvt_9_1_ = this.getLockableContainer(worldIn, pos);

            if (lvt_9_1_ != null)
            {
                playerIn.displayGUIChest(lvt_9_1_);

                if (this.chestType == 0)
                {
                    playerIn.triggerAchievement(StatList.field_181723_aa);
                }
                else if (this.chestType == 1)
                {
                    playerIn.triggerAchievement(StatList.field_181737_U);
                }
            }

            return true;
        }
    }

    public ILockableContainer getLockableContainer(World worldIn, BlockPos pos)
    {
        TileEntity lvt_3_1_ = worldIn.getTileEntity(pos);

        if (!(lvt_3_1_ instanceof TileEntityChest))
        {
            return null;
        }
        else
        {
            ILockableContainer lvt_4_1_ = (TileEntityChest)lvt_3_1_;

            if (this.isBlocked(worldIn, pos))
            {
                return null;
            }
            else
            {
                for (EnumFacing lvt_6_1_ : EnumFacing.Plane.HORIZONTAL)
                {
                    BlockPos lvt_7_1_ = pos.offset(lvt_6_1_);
                    Block lvt_8_1_ = worldIn.getBlockState(lvt_7_1_).getBlock();

                    if (lvt_8_1_ == this)
                    {
                        if (this.isBlocked(worldIn, lvt_7_1_))
                        {
                            return null;
                        }

                        TileEntity lvt_9_1_ = worldIn.getTileEntity(lvt_7_1_);

                        if (lvt_9_1_ instanceof TileEntityChest)
                        {
                            if (lvt_6_1_ != EnumFacing.WEST && lvt_6_1_ != EnumFacing.NORTH)
                            {
                                lvt_4_1_ = new InventoryLargeChest("container.chestDouble", lvt_4_1_, (TileEntityChest)lvt_9_1_);
                            }
                            else
                            {
                                lvt_4_1_ = new InventoryLargeChest("container.chestDouble", (TileEntityChest)lvt_9_1_, lvt_4_1_);
                            }
                        }
                    }
                }

                return lvt_4_1_;
            }
        }
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityChest();
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return this.chestType == 1;
    }

    public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side)
    {
        if (!this.canProvidePower())
        {
            return 0;
        }
        else
        {
            int lvt_5_1_ = 0;
            TileEntity lvt_6_1_ = worldIn.getTileEntity(pos);

            if (lvt_6_1_ instanceof TileEntityChest)
            {
                lvt_5_1_ = ((TileEntityChest)lvt_6_1_).numPlayersUsing;
            }

            return MathHelper.clamp_int(lvt_5_1_, 0, 15);
        }
    }

    public int getStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side)
    {
        return side == EnumFacing.UP ? this.getWeakPower(worldIn, pos, state, side) : 0;
    }

    private boolean isBlocked(World worldIn, BlockPos pos)
    {
        return this.isBelowSolidBlock(worldIn, pos) || this.isOcelotSittingOnChest(worldIn, pos);
    }

    private boolean isBelowSolidBlock(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.up()).getBlock().isNormalCube();
    }

    private boolean isOcelotSittingOnChest(World worldIn, BlockPos pos)
    {
        for (Entity lvt_4_1_ : worldIn.getEntitiesWithinAABB(EntityOcelot.class, new AxisAlignedBB((double)pos.getX(), (double)(pos.getY() + 1), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 2), (double)(pos.getZ() + 1))))
        {
            EntityOcelot lvt_5_1_ = (EntityOcelot)lvt_4_1_;

            if (lvt_5_1_.isSitting())
            {
                return true;
            }
        }

        return false;
    }

    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    public int getComparatorInputOverride(World worldIn, BlockPos pos)
    {
        return Container.calcRedstoneFromInventory(this.getLockableContainer(worldIn, pos));
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
