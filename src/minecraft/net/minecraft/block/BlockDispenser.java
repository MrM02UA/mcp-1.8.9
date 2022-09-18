package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.PositionImpl;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.RegistryDefaulted;
import net.minecraft.world.World;

public class BlockDispenser extends BlockContainer
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyBool TRIGGERED = PropertyBool.create("triggered");
    public static final RegistryDefaulted<Item, IBehaviorDispenseItem> dispenseBehaviorRegistry = new RegistryDefaulted(new BehaviorDefaultDispenseItem());
    protected Random rand = new Random();

    protected BlockDispenser()
    {
        super(Material.rock);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(TRIGGERED, Boolean.valueOf(false)));
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World worldIn)
    {
        return 4;
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        super.onBlockAdded(worldIn, pos, state);
        this.setDefaultDirection(worldIn, pos, state);
    }

    private void setDefaultDirection(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote)
        {
            EnumFacing lvt_4_1_ = (EnumFacing)state.getValue(FACING);
            boolean lvt_5_1_ = worldIn.getBlockState(pos.north()).getBlock().isFullBlock();
            boolean lvt_6_1_ = worldIn.getBlockState(pos.south()).getBlock().isFullBlock();

            if (lvt_4_1_ == EnumFacing.NORTH && lvt_5_1_ && !lvt_6_1_)
            {
                lvt_4_1_ = EnumFacing.SOUTH;
            }
            else if (lvt_4_1_ == EnumFacing.SOUTH && lvt_6_1_ && !lvt_5_1_)
            {
                lvt_4_1_ = EnumFacing.NORTH;
            }
            else
            {
                boolean lvt_7_1_ = worldIn.getBlockState(pos.west()).getBlock().isFullBlock();
                boolean lvt_8_1_ = worldIn.getBlockState(pos.east()).getBlock().isFullBlock();

                if (lvt_4_1_ == EnumFacing.WEST && lvt_7_1_ && !lvt_8_1_)
                {
                    lvt_4_1_ = EnumFacing.EAST;
                }
                else if (lvt_4_1_ == EnumFacing.EAST && lvt_8_1_ && !lvt_7_1_)
                {
                    lvt_4_1_ = EnumFacing.WEST;
                }
            }

            worldIn.setBlockState(pos, state.withProperty(FACING, lvt_4_1_).withProperty(TRIGGERED, Boolean.valueOf(false)), 2);
        }
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        else
        {
            TileEntity lvt_9_1_ = worldIn.getTileEntity(pos);

            if (lvt_9_1_ instanceof TileEntityDispenser)
            {
                playerIn.displayGUIChest((TileEntityDispenser)lvt_9_1_);

                if (lvt_9_1_ instanceof TileEntityDropper)
                {
                    playerIn.triggerAchievement(StatList.field_181731_O);
                }
                else
                {
                    playerIn.triggerAchievement(StatList.field_181733_Q);
                }
            }

            return true;
        }
    }

    protected void dispense(World worldIn, BlockPos pos)
    {
        BlockSourceImpl lvt_3_1_ = new BlockSourceImpl(worldIn, pos);
        TileEntityDispenser lvt_4_1_ = (TileEntityDispenser)lvt_3_1_.getBlockTileEntity();

        if (lvt_4_1_ != null)
        {
            int lvt_5_1_ = lvt_4_1_.getDispenseSlot();

            if (lvt_5_1_ < 0)
            {
                worldIn.playAuxSFX(1001, pos, 0);
            }
            else
            {
                ItemStack lvt_6_1_ = lvt_4_1_.getStackInSlot(lvt_5_1_);
                IBehaviorDispenseItem lvt_7_1_ = this.getBehavior(lvt_6_1_);

                if (lvt_7_1_ != IBehaviorDispenseItem.itemDispenseBehaviorProvider)
                {
                    ItemStack lvt_8_1_ = lvt_7_1_.dispense(lvt_3_1_, lvt_6_1_);
                    lvt_4_1_.setInventorySlotContents(lvt_5_1_, lvt_8_1_.stackSize <= 0 ? null : lvt_8_1_);
                }
            }
        }
    }

    protected IBehaviorDispenseItem getBehavior(ItemStack stack)
    {
        return (IBehaviorDispenseItem)dispenseBehaviorRegistry.getObject(stack == null ? null : stack.getItem());
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        boolean lvt_5_1_ = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(pos.up());
        boolean lvt_6_1_ = ((Boolean)state.getValue(TRIGGERED)).booleanValue();

        if (lvt_5_1_ && !lvt_6_1_)
        {
            worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
            worldIn.setBlockState(pos, state.withProperty(TRIGGERED, Boolean.valueOf(true)), 4);
        }
        else if (!lvt_5_1_ && lvt_6_1_)
        {
            worldIn.setBlockState(pos, state.withProperty(TRIGGERED, Boolean.valueOf(false)), 4);
        }
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isRemote)
        {
            this.dispense(worldIn, pos);
        }
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityDispenser();
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, BlockPistonBase.getFacingFromEntity(worldIn, pos, placer)).withProperty(TRIGGERED, Boolean.valueOf(false));
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        worldIn.setBlockState(pos, state.withProperty(FACING, BlockPistonBase.getFacingFromEntity(worldIn, pos, placer)), 2);

        if (stack.hasDisplayName())
        {
            TileEntity lvt_6_1_ = worldIn.getTileEntity(pos);

            if (lvt_6_1_ instanceof TileEntityDispenser)
            {
                ((TileEntityDispenser)lvt_6_1_).setCustomName(stack.getDisplayName());
            }
        }
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity lvt_4_1_ = worldIn.getTileEntity(pos);

        if (lvt_4_1_ instanceof TileEntityDispenser)
        {
            InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityDispenser)lvt_4_1_);
            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
    }

    /**
     * Get the position where the dispenser at the given Coordinates should dispense to.
     */
    public static IPosition getDispensePosition(IBlockSource coords)
    {
        EnumFacing lvt_1_1_ = getFacing(coords.getBlockMetadata());
        double lvt_2_1_ = coords.getX() + 0.7D * (double)lvt_1_1_.getFrontOffsetX();
        double lvt_4_1_ = coords.getY() + 0.7D * (double)lvt_1_1_.getFrontOffsetY();
        double lvt_6_1_ = coords.getZ() + 0.7D * (double)lvt_1_1_.getFrontOffsetZ();
        return new PositionImpl(lvt_2_1_, lvt_4_1_, lvt_6_1_);
    }

    /**
     * Get the facing of a dispenser with the given metadata
     */
    public static EnumFacing getFacing(int meta)
    {
        return EnumFacing.getFront(meta & 7);
    }

    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    public int getComparatorInputOverride(World worldIn, BlockPos pos)
    {
        return Container.calcRedstone(worldIn.getTileEntity(pos));
    }

    /**
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
    public int getRenderType()
    {
        return 3;
    }

    /**
     * Possibly modify the given BlockState before rendering it on an Entity (Minecarts, Endermen, ...)
     */
    public IBlockState getStateForEntityRender(IBlockState state)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, getFacing(meta)).withProperty(TRIGGERED, Boolean.valueOf((meta & 8) > 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int lvt_2_1_ = 0;
        lvt_2_1_ = lvt_2_1_ | ((EnumFacing)state.getValue(FACING)).getIndex();

        if (((Boolean)state.getValue(TRIGGERED)).booleanValue())
        {
            lvt_2_1_ |= 8;
        }

        return lvt_2_1_;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {FACING, TRIGGERED});
    }
}
