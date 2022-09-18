package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTripWire extends Block
{
    public static final PropertyBool POWERED = PropertyBool.create("powered");
    public static final PropertyBool SUSPENDED = PropertyBool.create("suspended");
    public static final PropertyBool ATTACHED = PropertyBool.create("attached");
    public static final PropertyBool DISARMED = PropertyBool.create("disarmed");
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");

    public BlockTripWire()
    {
        super(Material.circuits);
        this.setDefaultState(this.blockState.getBaseState().withProperty(POWERED, Boolean.valueOf(false)).withProperty(SUSPENDED, Boolean.valueOf(false)).withProperty(ATTACHED, Boolean.valueOf(false)).withProperty(DISARMED, Boolean.valueOf(false)).withProperty(NORTH, Boolean.valueOf(false)).withProperty(EAST, Boolean.valueOf(false)).withProperty(SOUTH, Boolean.valueOf(false)).withProperty(WEST, Boolean.valueOf(false)));
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.15625F, 1.0F);
        this.setTickRandomly(true);
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return state.withProperty(NORTH, Boolean.valueOf(isConnectedTo(worldIn, pos, state, EnumFacing.NORTH))).withProperty(EAST, Boolean.valueOf(isConnectedTo(worldIn, pos, state, EnumFacing.EAST))).withProperty(SOUTH, Boolean.valueOf(isConnectedTo(worldIn, pos, state, EnumFacing.SOUTH))).withProperty(WEST, Boolean.valueOf(isConnectedTo(worldIn, pos, state, EnumFacing.WEST)));
    }

    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
    {
        return null;
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

    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.TRANSLUCENT;
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.string;
    }

    public Item getItem(World worldIn, BlockPos pos)
    {
        return Items.string;
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        boolean lvt_5_1_ = ((Boolean)state.getValue(SUSPENDED)).booleanValue();
        boolean lvt_6_1_ = !World.doesBlockHaveSolidTopSurface(worldIn, pos.down());

        if (lvt_5_1_ != lvt_6_1_)
        {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
        IBlockState lvt_3_1_ = worldIn.getBlockState(pos);
        boolean lvt_4_1_ = ((Boolean)lvt_3_1_.getValue(ATTACHED)).booleanValue();
        boolean lvt_5_1_ = ((Boolean)lvt_3_1_.getValue(SUSPENDED)).booleanValue();

        if (!lvt_5_1_)
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.09375F, 1.0F);
        }
        else if (!lvt_4_1_)
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }
        else
        {
            this.setBlockBounds(0.0F, 0.0625F, 0.0F, 1.0F, 0.15625F, 1.0F);
        }
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        state = state.withProperty(SUSPENDED, Boolean.valueOf(!World.doesBlockHaveSolidTopSurface(worldIn, pos.down())));
        worldIn.setBlockState(pos, state, 3);
        this.notifyHook(worldIn, pos, state);
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        this.notifyHook(worldIn, pos, state.withProperty(POWERED, Boolean.valueOf(true)));
    }

    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        if (!worldIn.isRemote)
        {
            if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.shears)
            {
                worldIn.setBlockState(pos, state.withProperty(DISARMED, Boolean.valueOf(true)), 4);
            }
        }
    }

    private void notifyHook(World worldIn, BlockPos pos, IBlockState state)
    {
        for (EnumFacing lvt_7_1_ : new EnumFacing[] {EnumFacing.SOUTH, EnumFacing.WEST})
        {
            for (int lvt_8_1_ = 1; lvt_8_1_ < 42; ++lvt_8_1_)
            {
                BlockPos lvt_9_1_ = pos.offset(lvt_7_1_, lvt_8_1_);
                IBlockState lvt_10_1_ = worldIn.getBlockState(lvt_9_1_);

                if (lvt_10_1_.getBlock() == Blocks.tripwire_hook)
                {
                    if (lvt_10_1_.getValue(BlockTripWireHook.FACING) == lvt_7_1_.getOpposite())
                    {
                        Blocks.tripwire_hook.func_176260_a(worldIn, lvt_9_1_, lvt_10_1_, false, true, lvt_8_1_, state);
                    }

                    break;
                }

                if (lvt_10_1_.getBlock() != Blocks.tripwire)
                {
                    break;
                }
            }
        }
    }

    /**
     * Called When an Entity Collided with the Block
     */
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        if (!worldIn.isRemote)
        {
            if (!((Boolean)state.getValue(POWERED)).booleanValue())
            {
                this.updateState(worldIn, pos);
            }
        }
    }

    /**
     * Called randomly when setTickRandomly is set to true (used by e.g. crops to grow, etc.)
     */
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random)
    {
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isRemote)
        {
            if (((Boolean)worldIn.getBlockState(pos).getValue(POWERED)).booleanValue())
            {
                this.updateState(worldIn, pos);
            }
        }
    }

    private void updateState(World worldIn, BlockPos pos)
    {
        IBlockState lvt_3_1_ = worldIn.getBlockState(pos);
        boolean lvt_4_1_ = ((Boolean)lvt_3_1_.getValue(POWERED)).booleanValue();
        boolean lvt_5_1_ = false;
        List <? extends Entity > lvt_6_1_ = worldIn.getEntitiesWithinAABBExcludingEntity((Entity)null, new AxisAlignedBB((double)pos.getX() + this.minX, (double)pos.getY() + this.minY, (double)pos.getZ() + this.minZ, (double)pos.getX() + this.maxX, (double)pos.getY() + this.maxY, (double)pos.getZ() + this.maxZ));

        if (!lvt_6_1_.isEmpty())
        {
            for (Entity lvt_8_1_ : lvt_6_1_)
            {
                if (!lvt_8_1_.doesEntityNotTriggerPressurePlate())
                {
                    lvt_5_1_ = true;
                    break;
                }
            }
        }

        if (lvt_5_1_ != lvt_4_1_)
        {
            lvt_3_1_ = lvt_3_1_.withProperty(POWERED, Boolean.valueOf(lvt_5_1_));
            worldIn.setBlockState(pos, lvt_3_1_, 3);
            this.notifyHook(worldIn, pos, lvt_3_1_);
        }

        if (lvt_5_1_)
        {
            worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
        }
    }

    public static boolean isConnectedTo(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing direction)
    {
        BlockPos lvt_4_1_ = pos.offset(direction);
        IBlockState lvt_5_1_ = worldIn.getBlockState(lvt_4_1_);
        Block lvt_6_1_ = lvt_5_1_.getBlock();

        if (lvt_6_1_ == Blocks.tripwire_hook)
        {
            EnumFacing lvt_7_1_ = direction.getOpposite();
            return lvt_5_1_.getValue(BlockTripWireHook.FACING) == lvt_7_1_;
        }
        else if (lvt_6_1_ == Blocks.tripwire)
        {
            boolean lvt_7_2_ = ((Boolean)state.getValue(SUSPENDED)).booleanValue();
            boolean lvt_8_1_ = ((Boolean)lvt_5_1_.getValue(SUSPENDED)).booleanValue();
            return lvt_7_2_ == lvt_8_1_;
        }
        else
        {
            return false;
        }
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(POWERED, Boolean.valueOf((meta & 1) > 0)).withProperty(SUSPENDED, Boolean.valueOf((meta & 2) > 0)).withProperty(ATTACHED, Boolean.valueOf((meta & 4) > 0)).withProperty(DISARMED, Boolean.valueOf((meta & 8) > 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int lvt_2_1_ = 0;

        if (((Boolean)state.getValue(POWERED)).booleanValue())
        {
            lvt_2_1_ |= 1;
        }

        if (((Boolean)state.getValue(SUSPENDED)).booleanValue())
        {
            lvt_2_1_ |= 2;
        }

        if (((Boolean)state.getValue(ATTACHED)).booleanValue())
        {
            lvt_2_1_ |= 4;
        }

        if (((Boolean)state.getValue(DISARMED)).booleanValue())
        {
            lvt_2_1_ |= 8;
        }

        return lvt_2_1_;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {POWERED, SUSPENDED, ATTACHED, DISARMED, NORTH, EAST, WEST, SOUTH});
    }
}
