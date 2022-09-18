package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneWire extends Block
{
    public static final PropertyEnum<BlockRedstoneWire.EnumAttachPosition> NORTH = PropertyEnum.<BlockRedstoneWire.EnumAttachPosition>create("north", BlockRedstoneWire.EnumAttachPosition.class);
    public static final PropertyEnum<BlockRedstoneWire.EnumAttachPosition> EAST = PropertyEnum.<BlockRedstoneWire.EnumAttachPosition>create("east", BlockRedstoneWire.EnumAttachPosition.class);
    public static final PropertyEnum<BlockRedstoneWire.EnumAttachPosition> SOUTH = PropertyEnum.<BlockRedstoneWire.EnumAttachPosition>create("south", BlockRedstoneWire.EnumAttachPosition.class);
    public static final PropertyEnum<BlockRedstoneWire.EnumAttachPosition> WEST = PropertyEnum.<BlockRedstoneWire.EnumAttachPosition>create("west", BlockRedstoneWire.EnumAttachPosition.class);
    public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 15);
    private boolean canProvidePower = true;
    private final Set<BlockPos> blocksNeedingUpdate = Sets.newHashSet();

    public BlockRedstoneWire()
    {
        super(Material.circuits);
        this.setDefaultState(this.blockState.getBaseState().withProperty(NORTH, BlockRedstoneWire.EnumAttachPosition.NONE).withProperty(EAST, BlockRedstoneWire.EnumAttachPosition.NONE).withProperty(SOUTH, BlockRedstoneWire.EnumAttachPosition.NONE).withProperty(WEST, BlockRedstoneWire.EnumAttachPosition.NONE).withProperty(POWER, Integer.valueOf(0)));
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        state = state.withProperty(WEST, this.getAttachPosition(worldIn, pos, EnumFacing.WEST));
        state = state.withProperty(EAST, this.getAttachPosition(worldIn, pos, EnumFacing.EAST));
        state = state.withProperty(NORTH, this.getAttachPosition(worldIn, pos, EnumFacing.NORTH));
        state = state.withProperty(SOUTH, this.getAttachPosition(worldIn, pos, EnumFacing.SOUTH));
        return state;
    }

    private BlockRedstoneWire.EnumAttachPosition getAttachPosition(IBlockAccess worldIn, BlockPos pos, EnumFacing direction)
    {
        BlockPos lvt_4_1_ = pos.offset(direction);
        Block lvt_5_1_ = worldIn.getBlockState(pos.offset(direction)).getBlock();

        if (!canConnectTo(worldIn.getBlockState(lvt_4_1_), direction) && (lvt_5_1_.isBlockNormalCube() || !canConnectUpwardsTo(worldIn.getBlockState(lvt_4_1_.down()))))
        {
            Block lvt_6_1_ = worldIn.getBlockState(pos.up()).getBlock();
            return !lvt_6_1_.isBlockNormalCube() && lvt_5_1_.isBlockNormalCube() && canConnectUpwardsTo(worldIn.getBlockState(lvt_4_1_.up())) ? BlockRedstoneWire.EnumAttachPosition.UP : BlockRedstoneWire.EnumAttachPosition.NONE;
        }
        else
        {
            return BlockRedstoneWire.EnumAttachPosition.SIDE;
        }
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

    public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass)
    {
        IBlockState lvt_4_1_ = worldIn.getBlockState(pos);
        return lvt_4_1_.getBlock() != this ? super.colorMultiplier(worldIn, pos, renderPass) : this.colorMultiplier(((Integer)lvt_4_1_.getValue(POWER)).intValue());
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) || worldIn.getBlockState(pos.down()).getBlock() == Blocks.glowstone;
    }

    private IBlockState updateSurroundingRedstone(World worldIn, BlockPos pos, IBlockState state)
    {
        state = this.calculateCurrentChanges(worldIn, pos, pos, state);
        List<BlockPos> lvt_4_1_ = Lists.newArrayList(this.blocksNeedingUpdate);
        this.blocksNeedingUpdate.clear();

        for (BlockPos lvt_6_1_ : lvt_4_1_)
        {
            worldIn.notifyNeighborsOfStateChange(lvt_6_1_, this);
        }

        return state;
    }

    private IBlockState calculateCurrentChanges(World worldIn, BlockPos pos1, BlockPos pos2, IBlockState state)
    {
        IBlockState lvt_5_1_ = state;
        int lvt_6_1_ = ((Integer)state.getValue(POWER)).intValue();
        int lvt_7_1_ = 0;
        lvt_7_1_ = this.getMaxCurrentStrength(worldIn, pos2, lvt_7_1_);
        this.canProvidePower = false;
        int lvt_8_1_ = worldIn.isBlockIndirectlyGettingPowered(pos1);
        this.canProvidePower = true;

        if (lvt_8_1_ > 0 && lvt_8_1_ > lvt_7_1_ - 1)
        {
            lvt_7_1_ = lvt_8_1_;
        }

        int lvt_9_1_ = 0;

        for (EnumFacing lvt_11_1_ : EnumFacing.Plane.HORIZONTAL)
        {
            BlockPos lvt_12_1_ = pos1.offset(lvt_11_1_);
            boolean lvt_13_1_ = lvt_12_1_.getX() != pos2.getX() || lvt_12_1_.getZ() != pos2.getZ();

            if (lvt_13_1_)
            {
                lvt_9_1_ = this.getMaxCurrentStrength(worldIn, lvt_12_1_, lvt_9_1_);
            }

            if (worldIn.getBlockState(lvt_12_1_).getBlock().isNormalCube() && !worldIn.getBlockState(pos1.up()).getBlock().isNormalCube())
            {
                if (lvt_13_1_ && pos1.getY() >= pos2.getY())
                {
                    lvt_9_1_ = this.getMaxCurrentStrength(worldIn, lvt_12_1_.up(), lvt_9_1_);
                }
            }
            else if (!worldIn.getBlockState(lvt_12_1_).getBlock().isNormalCube() && lvt_13_1_ && pos1.getY() <= pos2.getY())
            {
                lvt_9_1_ = this.getMaxCurrentStrength(worldIn, lvt_12_1_.down(), lvt_9_1_);
            }
        }

        if (lvt_9_1_ > lvt_7_1_)
        {
            lvt_7_1_ = lvt_9_1_ - 1;
        }
        else if (lvt_7_1_ > 0)
        {
            --lvt_7_1_;
        }
        else
        {
            lvt_7_1_ = 0;
        }

        if (lvt_8_1_ > lvt_7_1_ - 1)
        {
            lvt_7_1_ = lvt_8_1_;
        }

        if (lvt_6_1_ != lvt_7_1_)
        {
            state = state.withProperty(POWER, Integer.valueOf(lvt_7_1_));

            if (worldIn.getBlockState(pos1) == lvt_5_1_)
            {
                worldIn.setBlockState(pos1, state, 2);
            }

            this.blocksNeedingUpdate.add(pos1);

            for (EnumFacing lvt_13_2_ : EnumFacing.values())
            {
                this.blocksNeedingUpdate.add(pos1.offset(lvt_13_2_));
            }
        }

        return state;
    }

    /**
     * Calls World.notifyNeighborsOfStateChange() for all neighboring blocks, but only if the given block is a redstone
     * wire.
     */
    private void notifyWireNeighborsOfStateChange(World worldIn, BlockPos pos)
    {
        if (worldIn.getBlockState(pos).getBlock() == this)
        {
            worldIn.notifyNeighborsOfStateChange(pos, this);

            for (EnumFacing lvt_6_1_ : EnumFacing.values())
            {
                worldIn.notifyNeighborsOfStateChange(pos.offset(lvt_6_1_), this);
            }
        }
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote)
        {
            this.updateSurroundingRedstone(worldIn, pos, state);

            for (EnumFacing lvt_5_1_ : EnumFacing.Plane.VERTICAL)
            {
                worldIn.notifyNeighborsOfStateChange(pos.offset(lvt_5_1_), this);
            }

            for (EnumFacing lvt_5_2_ : EnumFacing.Plane.HORIZONTAL)
            {
                this.notifyWireNeighborsOfStateChange(worldIn, pos.offset(lvt_5_2_));
            }

            for (EnumFacing lvt_5_3_ : EnumFacing.Plane.HORIZONTAL)
            {
                BlockPos lvt_6_1_ = pos.offset(lvt_5_3_);

                if (worldIn.getBlockState(lvt_6_1_).getBlock().isNormalCube())
                {
                    this.notifyWireNeighborsOfStateChange(worldIn, lvt_6_1_.up());
                }
                else
                {
                    this.notifyWireNeighborsOfStateChange(worldIn, lvt_6_1_.down());
                }
            }
        }
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        super.breakBlock(worldIn, pos, state);

        if (!worldIn.isRemote)
        {
            for (EnumFacing lvt_7_1_ : EnumFacing.values())
            {
                worldIn.notifyNeighborsOfStateChange(pos.offset(lvt_7_1_), this);
            }

            this.updateSurroundingRedstone(worldIn, pos, state);

            for (EnumFacing lvt_5_2_ : EnumFacing.Plane.HORIZONTAL)
            {
                this.notifyWireNeighborsOfStateChange(worldIn, pos.offset(lvt_5_2_));
            }

            for (EnumFacing lvt_5_3_ : EnumFacing.Plane.HORIZONTAL)
            {
                BlockPos lvt_6_2_ = pos.offset(lvt_5_3_);

                if (worldIn.getBlockState(lvt_6_2_).getBlock().isNormalCube())
                {
                    this.notifyWireNeighborsOfStateChange(worldIn, lvt_6_2_.up());
                }
                else
                {
                    this.notifyWireNeighborsOfStateChange(worldIn, lvt_6_2_.down());
                }
            }
        }
    }

    private int getMaxCurrentStrength(World worldIn, BlockPos pos, int strength)
    {
        if (worldIn.getBlockState(pos).getBlock() != this)
        {
            return strength;
        }
        else
        {
            int lvt_4_1_ = ((Integer)worldIn.getBlockState(pos).getValue(POWER)).intValue();
            return lvt_4_1_ > strength ? lvt_4_1_ : strength;
        }
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        if (!worldIn.isRemote)
        {
            if (this.canPlaceBlockAt(worldIn, pos))
            {
                this.updateSurroundingRedstone(worldIn, pos, state);
            }
            else
            {
                this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
            }
        }
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.redstone;
    }

    public int getStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side)
    {
        return !this.canProvidePower ? 0 : this.getWeakPower(worldIn, pos, state, side);
    }

    public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side)
    {
        if (!this.canProvidePower)
        {
            return 0;
        }
        else
        {
            int lvt_5_1_ = ((Integer)state.getValue(POWER)).intValue();

            if (lvt_5_1_ == 0)
            {
                return 0;
            }
            else if (side == EnumFacing.UP)
            {
                return lvt_5_1_;
            }
            else
            {
                EnumSet<EnumFacing> lvt_6_1_ = EnumSet.noneOf(EnumFacing.class);

                for (EnumFacing lvt_8_1_ : EnumFacing.Plane.HORIZONTAL)
                {
                    if (this.func_176339_d(worldIn, pos, lvt_8_1_))
                    {
                        lvt_6_1_.add(lvt_8_1_);
                    }
                }

                if (side.getAxis().isHorizontal() && lvt_6_1_.isEmpty())
                {
                    return lvt_5_1_;
                }
                else if (lvt_6_1_.contains(side) && !lvt_6_1_.contains(side.rotateYCCW()) && !lvt_6_1_.contains(side.rotateY()))
                {
                    return lvt_5_1_;
                }
                else
                {
                    return 0;
                }
            }
        }
    }

    private boolean func_176339_d(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        BlockPos lvt_4_1_ = pos.offset(side);
        IBlockState lvt_5_1_ = worldIn.getBlockState(lvt_4_1_);
        Block lvt_6_1_ = lvt_5_1_.getBlock();
        boolean lvt_7_1_ = lvt_6_1_.isNormalCube();
        boolean lvt_8_1_ = worldIn.getBlockState(pos.up()).getBlock().isNormalCube();
        return !lvt_8_1_ && lvt_7_1_ && canConnectUpwardsTo(worldIn, lvt_4_1_.up()) ? true : (canConnectTo(lvt_5_1_, side) ? true : (lvt_6_1_ == Blocks.powered_repeater && lvt_5_1_.getValue(BlockRedstoneDiode.FACING) == side ? true : !lvt_7_1_ && canConnectUpwardsTo(worldIn, lvt_4_1_.down())));
    }

    protected static boolean canConnectUpwardsTo(IBlockAccess worldIn, BlockPos pos)
    {
        return canConnectUpwardsTo(worldIn.getBlockState(pos));
    }

    protected static boolean canConnectUpwardsTo(IBlockState state)
    {
        return canConnectTo(state, (EnumFacing)null);
    }

    protected static boolean canConnectTo(IBlockState blockState, EnumFacing side)
    {
        Block lvt_2_1_ = blockState.getBlock();

        if (lvt_2_1_ == Blocks.redstone_wire)
        {
            return true;
        }
        else if (Blocks.unpowered_repeater.isAssociated(lvt_2_1_))
        {
            EnumFacing lvt_3_1_ = (EnumFacing)blockState.getValue(BlockRedstoneRepeater.FACING);
            return lvt_3_1_ == side || lvt_3_1_.getOpposite() == side;
        }
        else
        {
            return lvt_2_1_.canProvidePower() && side != null;
        }
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return this.canProvidePower;
    }

    private int colorMultiplier(int powerLevel)
    {
        float lvt_2_1_ = (float)powerLevel / 15.0F;
        float lvt_3_1_ = lvt_2_1_ * 0.6F + 0.4F;

        if (powerLevel == 0)
        {
            lvt_3_1_ = 0.3F;
        }

        float lvt_4_1_ = lvt_2_1_ * lvt_2_1_ * 0.7F - 0.5F;
        float lvt_5_1_ = lvt_2_1_ * lvt_2_1_ * 0.6F - 0.7F;

        if (lvt_4_1_ < 0.0F)
        {
            lvt_4_1_ = 0.0F;
        }

        if (lvt_5_1_ < 0.0F)
        {
            lvt_5_1_ = 0.0F;
        }

        int lvt_6_1_ = MathHelper.clamp_int((int)(lvt_3_1_ * 255.0F), 0, 255);
        int lvt_7_1_ = MathHelper.clamp_int((int)(lvt_4_1_ * 255.0F), 0, 255);
        int lvt_8_1_ = MathHelper.clamp_int((int)(lvt_5_1_ * 255.0F), 0, 255);
        return -16777216 | lvt_6_1_ << 16 | lvt_7_1_ << 8 | lvt_8_1_;
    }

    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        int lvt_5_1_ = ((Integer)state.getValue(POWER)).intValue();

        if (lvt_5_1_ != 0)
        {
            double lvt_6_1_ = (double)pos.getX() + 0.5D + ((double)rand.nextFloat() - 0.5D) * 0.2D;
            double lvt_8_1_ = (double)((float)pos.getY() + 0.0625F);
            double lvt_10_1_ = (double)pos.getZ() + 0.5D + ((double)rand.nextFloat() - 0.5D) * 0.2D;
            float lvt_12_1_ = (float)lvt_5_1_ / 15.0F;
            float lvt_13_1_ = lvt_12_1_ * 0.6F + 0.4F;
            float lvt_14_1_ = Math.max(0.0F, lvt_12_1_ * lvt_12_1_ * 0.7F - 0.5F);
            float lvt_15_1_ = Math.max(0.0F, lvt_12_1_ * lvt_12_1_ * 0.6F - 0.7F);
            worldIn.spawnParticle(EnumParticleTypes.REDSTONE, lvt_6_1_, lvt_8_1_, lvt_10_1_, (double)lvt_13_1_, (double)lvt_14_1_, (double)lvt_15_1_, new int[0]);
        }
    }

    public Item getItem(World worldIn, BlockPos pos)
    {
        return Items.redstone;
    }

    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.CUTOUT;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(POWER, Integer.valueOf(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((Integer)state.getValue(POWER)).intValue();
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {NORTH, EAST, SOUTH, WEST, POWER});
    }

    static enum EnumAttachPosition implements IStringSerializable
    {
        UP("up"),
        SIDE("side"),
        NONE("none");

        private final String name;

        private EnumAttachPosition(String name)
        {
            this.name = name;
        }

        public String toString()
        {
            return this.getName();
        }

        public String getName()
        {
            return this.name;
        }
    }
}
