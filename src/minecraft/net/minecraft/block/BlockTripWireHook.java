package net.minecraft.block;

import com.google.common.base.Objects;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTripWireHook extends Block
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool POWERED = PropertyBool.create("powered");
    public static final PropertyBool ATTACHED = PropertyBool.create("attached");
    public static final PropertyBool SUSPENDED = PropertyBool.create("suspended");

    public BlockTripWireHook()
    {
        super(Material.circuits);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(POWERED, Boolean.valueOf(false)).withProperty(ATTACHED, Boolean.valueOf(false)).withProperty(SUSPENDED, Boolean.valueOf(false)));
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.setTickRandomly(true);
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return state.withProperty(SUSPENDED, Boolean.valueOf(!World.doesBlockHaveSolidTopSurface(worldIn, pos.down())));
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

    /**
     * Check whether this Block can be placed on the given side
     */
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side)
    {
        return side.getAxis().isHorizontal() && worldIn.getBlockState(pos.offset(side.getOpposite())).getBlock().isNormalCube();
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        for (EnumFacing lvt_4_1_ : EnumFacing.Plane.HORIZONTAL)
        {
            if (worldIn.getBlockState(pos.offset(lvt_4_1_)).getBlock().isNormalCube())
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        IBlockState lvt_9_1_ = this.getDefaultState().withProperty(POWERED, Boolean.valueOf(false)).withProperty(ATTACHED, Boolean.valueOf(false)).withProperty(SUSPENDED, Boolean.valueOf(false));

        if (facing.getAxis().isHorizontal())
        {
            lvt_9_1_ = lvt_9_1_.withProperty(FACING, facing);
        }

        return lvt_9_1_;
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        this.func_176260_a(worldIn, pos, state, false, false, -1, (IBlockState)null);
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        if (neighborBlock != this)
        {
            if (this.checkForDrop(worldIn, pos, state))
            {
                EnumFacing lvt_5_1_ = (EnumFacing)state.getValue(FACING);

                if (!worldIn.getBlockState(pos.offset(lvt_5_1_.getOpposite())).getBlock().isNormalCube())
                {
                    this.dropBlockAsItem(worldIn, pos, state, 0);
                    worldIn.setBlockToAir(pos);
                }
            }
        }
    }

    public void func_176260_a(World worldIn, BlockPos pos, IBlockState hookState, boolean p_176260_4_, boolean p_176260_5_, int p_176260_6_, IBlockState p_176260_7_)
    {
        EnumFacing lvt_8_1_ = (EnumFacing)hookState.getValue(FACING);
        boolean lvt_9_1_ = ((Boolean)hookState.getValue(ATTACHED)).booleanValue();
        boolean lvt_10_1_ = ((Boolean)hookState.getValue(POWERED)).booleanValue();
        boolean lvt_11_1_ = !World.doesBlockHaveSolidTopSurface(worldIn, pos.down());
        boolean lvt_12_1_ = !p_176260_4_;
        boolean lvt_13_1_ = false;
        int lvt_14_1_ = 0;
        IBlockState[] lvt_15_1_ = new IBlockState[42];

        for (int lvt_16_1_ = 1; lvt_16_1_ < 42; ++lvt_16_1_)
        {
            BlockPos lvt_17_1_ = pos.offset(lvt_8_1_, lvt_16_1_);
            IBlockState lvt_18_1_ = worldIn.getBlockState(lvt_17_1_);

            if (lvt_18_1_.getBlock() == Blocks.tripwire_hook)
            {
                if (lvt_18_1_.getValue(FACING) == lvt_8_1_.getOpposite())
                {
                    lvt_14_1_ = lvt_16_1_;
                }

                break;
            }

            if (lvt_18_1_.getBlock() != Blocks.tripwire && lvt_16_1_ != p_176260_6_)
            {
                lvt_15_1_[lvt_16_1_] = null;
                lvt_12_1_ = false;
            }
            else
            {
                if (lvt_16_1_ == p_176260_6_)
                {
                    lvt_18_1_ = (IBlockState)Objects.firstNonNull(p_176260_7_, lvt_18_1_);
                }

                boolean lvt_19_1_ = !((Boolean)lvt_18_1_.getValue(BlockTripWire.DISARMED)).booleanValue();
                boolean lvt_20_1_ = ((Boolean)lvt_18_1_.getValue(BlockTripWire.POWERED)).booleanValue();
                boolean lvt_21_1_ = ((Boolean)lvt_18_1_.getValue(BlockTripWire.SUSPENDED)).booleanValue();
                lvt_12_1_ &= lvt_21_1_ == lvt_11_1_;
                lvt_13_1_ |= lvt_19_1_ && lvt_20_1_;
                lvt_15_1_[lvt_16_1_] = lvt_18_1_;

                if (lvt_16_1_ == p_176260_6_)
                {
                    worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
                    lvt_12_1_ &= lvt_19_1_;
                }
            }
        }

        lvt_12_1_ = lvt_12_1_ & lvt_14_1_ > 1;
        lvt_13_1_ = lvt_13_1_ & lvt_12_1_;
        IBlockState lvt_16_2_ = this.getDefaultState().withProperty(ATTACHED, Boolean.valueOf(lvt_12_1_)).withProperty(POWERED, Boolean.valueOf(lvt_13_1_));

        if (lvt_14_1_ > 0)
        {
            BlockPos lvt_17_2_ = pos.offset(lvt_8_1_, lvt_14_1_);
            EnumFacing lvt_18_2_ = lvt_8_1_.getOpposite();
            worldIn.setBlockState(lvt_17_2_, lvt_16_2_.withProperty(FACING, lvt_18_2_), 3);
            this.func_176262_b(worldIn, lvt_17_2_, lvt_18_2_);
            this.func_180694_a(worldIn, lvt_17_2_, lvt_12_1_, lvt_13_1_, lvt_9_1_, lvt_10_1_);
        }

        this.func_180694_a(worldIn, pos, lvt_12_1_, lvt_13_1_, lvt_9_1_, lvt_10_1_);

        if (!p_176260_4_)
        {
            worldIn.setBlockState(pos, lvt_16_2_.withProperty(FACING, lvt_8_1_), 3);

            if (p_176260_5_)
            {
                this.func_176262_b(worldIn, pos, lvt_8_1_);
            }
        }

        if (lvt_9_1_ != lvt_12_1_)
        {
            for (int lvt_17_3_ = 1; lvt_17_3_ < lvt_14_1_; ++lvt_17_3_)
            {
                BlockPos lvt_18_3_ = pos.offset(lvt_8_1_, lvt_17_3_);
                IBlockState lvt_19_2_ = lvt_15_1_[lvt_17_3_];

                if (lvt_19_2_ != null && worldIn.getBlockState(lvt_18_3_).getBlock() != Blocks.air)
                {
                    worldIn.setBlockState(lvt_18_3_, lvt_19_2_.withProperty(ATTACHED, Boolean.valueOf(lvt_12_1_)), 3);
                }
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
        this.func_176260_a(worldIn, pos, state, false, true, -1, (IBlockState)null);
    }

    private void func_180694_a(World worldIn, BlockPos pos, boolean p_180694_3_, boolean p_180694_4_, boolean p_180694_5_, boolean p_180694_6_)
    {
        if (p_180694_4_ && !p_180694_6_)
        {
            worldIn.playSoundEffect((double)pos.getX() + 0.5D, (double)pos.getY() + 0.1D, (double)pos.getZ() + 0.5D, "random.click", 0.4F, 0.6F);
        }
        else if (!p_180694_4_ && p_180694_6_)
        {
            worldIn.playSoundEffect((double)pos.getX() + 0.5D, (double)pos.getY() + 0.1D, (double)pos.getZ() + 0.5D, "random.click", 0.4F, 0.5F);
        }
        else if (p_180694_3_ && !p_180694_5_)
        {
            worldIn.playSoundEffect((double)pos.getX() + 0.5D, (double)pos.getY() + 0.1D, (double)pos.getZ() + 0.5D, "random.click", 0.4F, 0.7F);
        }
        else if (!p_180694_3_ && p_180694_5_)
        {
            worldIn.playSoundEffect((double)pos.getX() + 0.5D, (double)pos.getY() + 0.1D, (double)pos.getZ() + 0.5D, "random.bowhit", 0.4F, 1.2F / (worldIn.rand.nextFloat() * 0.2F + 0.9F));
        }
    }

    private void func_176262_b(World worldIn, BlockPos p_176262_2_, EnumFacing p_176262_3_)
    {
        worldIn.notifyNeighborsOfStateChange(p_176262_2_, this);
        worldIn.notifyNeighborsOfStateChange(p_176262_2_.offset(p_176262_3_.getOpposite()), this);
    }

    private boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!this.canPlaceBlockAt(worldIn, pos))
        {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
            return false;
        }
        else
        {
            return true;
        }
    }

    @SuppressWarnings("incomplete-switch")
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
        float lvt_3_1_ = 0.1875F;

        switch ((EnumFacing)worldIn.getBlockState(pos).getValue(FACING))
        {
            case EAST:
                this.setBlockBounds(0.0F, 0.2F, 0.5F - lvt_3_1_, lvt_3_1_ * 2.0F, 0.8F, 0.5F + lvt_3_1_);
                break;

            case WEST:
                this.setBlockBounds(1.0F - lvt_3_1_ * 2.0F, 0.2F, 0.5F - lvt_3_1_, 1.0F, 0.8F, 0.5F + lvt_3_1_);
                break;

            case SOUTH:
                this.setBlockBounds(0.5F - lvt_3_1_, 0.2F, 0.0F, 0.5F + lvt_3_1_, 0.8F, lvt_3_1_ * 2.0F);
                break;

            case NORTH:
                this.setBlockBounds(0.5F - lvt_3_1_, 0.2F, 1.0F - lvt_3_1_ * 2.0F, 0.5F + lvt_3_1_, 0.8F, 1.0F);
        }
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        boolean lvt_4_1_ = ((Boolean)state.getValue(ATTACHED)).booleanValue();
        boolean lvt_5_1_ = ((Boolean)state.getValue(POWERED)).booleanValue();

        if (lvt_4_1_ || lvt_5_1_)
        {
            this.func_176260_a(worldIn, pos, state, true, false, -1, (IBlockState)null);
        }

        if (lvt_5_1_)
        {
            worldIn.notifyNeighborsOfStateChange(pos, this);
            worldIn.notifyNeighborsOfStateChange(pos.offset(((EnumFacing)state.getValue(FACING)).getOpposite()), this);
        }

        super.breakBlock(worldIn, pos, state);
    }

    public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side)
    {
        return ((Boolean)state.getValue(POWERED)).booleanValue() ? 15 : 0;
    }

    public int getStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side)
    {
        return !((Boolean)state.getValue(POWERED)).booleanValue() ? 0 : (state.getValue(FACING) == side ? 15 : 0);
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return true;
    }

    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.CUTOUT_MIPPED;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta & 3)).withProperty(POWERED, Boolean.valueOf((meta & 8) > 0)).withProperty(ATTACHED, Boolean.valueOf((meta & 4) > 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int lvt_2_1_ = 0;
        lvt_2_1_ = lvt_2_1_ | ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();

        if (((Boolean)state.getValue(POWERED)).booleanValue())
        {
            lvt_2_1_ |= 8;
        }

        if (((Boolean)state.getValue(ATTACHED)).booleanValue())
        {
            lvt_2_1_ |= 4;
        }

        return lvt_2_1_;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {FACING, POWERED, ATTACHED, SUSPENDED});
    }
}
