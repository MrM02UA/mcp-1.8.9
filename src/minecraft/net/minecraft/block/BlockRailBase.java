package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockRailBase extends Block
{
    protected final boolean isPowered;

    public static boolean isRailBlock(World worldIn, BlockPos pos)
    {
        return isRailBlock(worldIn.getBlockState(pos));
    }

    public static boolean isRailBlock(IBlockState state)
    {
        Block lvt_1_1_ = state.getBlock();
        return lvt_1_1_ == Blocks.rail || lvt_1_1_ == Blocks.golden_rail || lvt_1_1_ == Blocks.detector_rail || lvt_1_1_ == Blocks.activator_rail;
    }

    protected BlockRailBase(boolean isPowered)
    {
        super(Material.circuits);
        this.isPowered = isPowered;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
        this.setCreativeTab(CreativeTabs.tabTransport);
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

    /**
     * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit.
     */
    public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end)
    {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        return super.collisionRayTrace(worldIn, pos, start, end);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
        IBlockState lvt_3_1_ = worldIn.getBlockState(pos);
        BlockRailBase.EnumRailDirection lvt_4_1_ = lvt_3_1_.getBlock() == this ? (BlockRailBase.EnumRailDirection)lvt_3_1_.getValue(this.getShapeProperty()) : null;

        if (lvt_4_1_ != null && lvt_4_1_.isAscending())
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
        }
        else
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
        }
    }

    public boolean isFullCube()
    {
        return false;
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return World.doesBlockHaveSolidTopSurface(worldIn, pos.down());
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote)
        {
            state = this.func_176564_a(worldIn, pos, state, true);

            if (this.isPowered)
            {
                this.onNeighborBlockChange(worldIn, pos, state, this);
            }
        }
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        if (!worldIn.isRemote)
        {
            BlockRailBase.EnumRailDirection lvt_5_1_ = (BlockRailBase.EnumRailDirection)state.getValue(this.getShapeProperty());
            boolean lvt_6_1_ = false;

            if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down()))
            {
                lvt_6_1_ = true;
            }

            if (lvt_5_1_ == BlockRailBase.EnumRailDirection.ASCENDING_EAST && !World.doesBlockHaveSolidTopSurface(worldIn, pos.east()))
            {
                lvt_6_1_ = true;
            }
            else if (lvt_5_1_ == BlockRailBase.EnumRailDirection.ASCENDING_WEST && !World.doesBlockHaveSolidTopSurface(worldIn, pos.west()))
            {
                lvt_6_1_ = true;
            }
            else if (lvt_5_1_ == BlockRailBase.EnumRailDirection.ASCENDING_NORTH && !World.doesBlockHaveSolidTopSurface(worldIn, pos.north()))
            {
                lvt_6_1_ = true;
            }
            else if (lvt_5_1_ == BlockRailBase.EnumRailDirection.ASCENDING_SOUTH && !World.doesBlockHaveSolidTopSurface(worldIn, pos.south()))
            {
                lvt_6_1_ = true;
            }

            if (lvt_6_1_)
            {
                this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
            }
            else
            {
                this.onNeighborChangedInternal(worldIn, pos, state, neighborBlock);
            }
        }
    }

    protected void onNeighborChangedInternal(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
    }

    protected IBlockState func_176564_a(World worldIn, BlockPos p_176564_2_, IBlockState p_176564_3_, boolean p_176564_4_)
    {
        return worldIn.isRemote ? p_176564_3_ : (new BlockRailBase.Rail(worldIn, p_176564_2_, p_176564_3_)).func_180364_a(worldIn.isBlockPowered(p_176564_2_), p_176564_4_).getBlockState();
    }

    public int getMobilityFlag()
    {
        return 0;
    }

    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.CUTOUT;
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        super.breakBlock(worldIn, pos, state);

        if (((BlockRailBase.EnumRailDirection)state.getValue(this.getShapeProperty())).isAscending())
        {
            worldIn.notifyNeighborsOfStateChange(pos.up(), this);
        }

        if (this.isPowered)
        {
            worldIn.notifyNeighborsOfStateChange(pos, this);
            worldIn.notifyNeighborsOfStateChange(pos.down(), this);
        }
    }

    public abstract IProperty<BlockRailBase.EnumRailDirection> getShapeProperty();

    public static enum EnumRailDirection implements IStringSerializable
    {
        NORTH_SOUTH(0, "north_south"),
        EAST_WEST(1, "east_west"),
        ASCENDING_EAST(2, "ascending_east"),
        ASCENDING_WEST(3, "ascending_west"),
        ASCENDING_NORTH(4, "ascending_north"),
        ASCENDING_SOUTH(5, "ascending_south"),
        SOUTH_EAST(6, "south_east"),
        SOUTH_WEST(7, "south_west"),
        NORTH_WEST(8, "north_west"),
        NORTH_EAST(9, "north_east");

        private static final BlockRailBase.EnumRailDirection[] META_LOOKUP = new BlockRailBase.EnumRailDirection[values().length];
        private final int meta;
        private final String name;

        private EnumRailDirection(int meta, String name)
        {
            this.meta = meta;
            this.name = name;
        }

        public int getMetadata()
        {
            return this.meta;
        }

        public String toString()
        {
            return this.name;
        }

        public boolean isAscending()
        {
            return this == ASCENDING_NORTH || this == ASCENDING_EAST || this == ASCENDING_SOUTH || this == ASCENDING_WEST;
        }

        public static BlockRailBase.EnumRailDirection byMetadata(int meta)
        {
            if (meta < 0 || meta >= META_LOOKUP.length)
            {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public String getName()
        {
            return this.name;
        }

        static {
            for (BlockRailBase.EnumRailDirection lvt_3_1_ : values())
            {
                META_LOOKUP[lvt_3_1_.getMetadata()] = lvt_3_1_;
            }
        }
    }

    public class Rail
    {
        private final World world;
        private final BlockPos pos;
        private final BlockRailBase block;
        private IBlockState state;
        private final boolean isPowered;
        private final List<BlockPos> field_150657_g = Lists.newArrayList();

        public Rail(World worldIn, BlockPos pos, IBlockState state)
        {
            this.world = worldIn;
            this.pos = pos;
            this.state = state;
            this.block = (BlockRailBase)state.getBlock();
            BlockRailBase.EnumRailDirection lvt_5_1_ = (BlockRailBase.EnumRailDirection)state.getValue(BlockRailBase.this.getShapeProperty());
            this.isPowered = this.block.isPowered;
            this.func_180360_a(lvt_5_1_);
        }

        private void func_180360_a(BlockRailBase.EnumRailDirection p_180360_1_)
        {
            this.field_150657_g.clear();

            switch (p_180360_1_)
            {
                case NORTH_SOUTH:
                    this.field_150657_g.add(this.pos.north());
                    this.field_150657_g.add(this.pos.south());
                    break;

                case EAST_WEST:
                    this.field_150657_g.add(this.pos.west());
                    this.field_150657_g.add(this.pos.east());
                    break;

                case ASCENDING_EAST:
                    this.field_150657_g.add(this.pos.west());
                    this.field_150657_g.add(this.pos.east().up());
                    break;

                case ASCENDING_WEST:
                    this.field_150657_g.add(this.pos.west().up());
                    this.field_150657_g.add(this.pos.east());
                    break;

                case ASCENDING_NORTH:
                    this.field_150657_g.add(this.pos.north().up());
                    this.field_150657_g.add(this.pos.south());
                    break;

                case ASCENDING_SOUTH:
                    this.field_150657_g.add(this.pos.north());
                    this.field_150657_g.add(this.pos.south().up());
                    break;

                case SOUTH_EAST:
                    this.field_150657_g.add(this.pos.east());
                    this.field_150657_g.add(this.pos.south());
                    break;

                case SOUTH_WEST:
                    this.field_150657_g.add(this.pos.west());
                    this.field_150657_g.add(this.pos.south());
                    break;

                case NORTH_WEST:
                    this.field_150657_g.add(this.pos.west());
                    this.field_150657_g.add(this.pos.north());
                    break;

                case NORTH_EAST:
                    this.field_150657_g.add(this.pos.east());
                    this.field_150657_g.add(this.pos.north());
            }
        }

        private void func_150651_b()
        {
            for (int lvt_1_1_ = 0; lvt_1_1_ < this.field_150657_g.size(); ++lvt_1_1_)
            {
                BlockRailBase.Rail lvt_2_1_ = this.findRailAt((BlockPos)this.field_150657_g.get(lvt_1_1_));

                if (lvt_2_1_ != null && lvt_2_1_.func_150653_a(this))
                {
                    this.field_150657_g.set(lvt_1_1_, lvt_2_1_.pos);
                }
                else
                {
                    this.field_150657_g.remove(lvt_1_1_--);
                }
            }
        }

        private boolean hasRailAt(BlockPos pos)
        {
            return BlockRailBase.isRailBlock(this.world, pos) || BlockRailBase.isRailBlock(this.world, pos.up()) || BlockRailBase.isRailBlock(this.world, pos.down());
        }

        private BlockRailBase.Rail findRailAt(BlockPos pos)
        {
            IBlockState lvt_3_1_ = this.world.getBlockState(pos);

            if (BlockRailBase.isRailBlock(lvt_3_1_))
            {
                return BlockRailBase.this.new Rail(this.world, pos, lvt_3_1_);
            }
            else
            {
                BlockPos lvt_2_1_ = pos.up();
                lvt_3_1_ = this.world.getBlockState(lvt_2_1_);

                if (BlockRailBase.isRailBlock(lvt_3_1_))
                {
                    return BlockRailBase.this.new Rail(this.world, lvt_2_1_, lvt_3_1_);
                }
                else
                {
                    lvt_2_1_ = pos.down();
                    lvt_3_1_ = this.world.getBlockState(lvt_2_1_);
                    return BlockRailBase.isRailBlock(lvt_3_1_) ? BlockRailBase.this.new Rail(this.world, lvt_2_1_, lvt_3_1_) : null;
                }
            }
        }

        private boolean func_150653_a(BlockRailBase.Rail p_150653_1_)
        {
            return this.func_180363_c(p_150653_1_.pos);
        }

        private boolean func_180363_c(BlockPos p_180363_1_)
        {
            for (int lvt_2_1_ = 0; lvt_2_1_ < this.field_150657_g.size(); ++lvt_2_1_)
            {
                BlockPos lvt_3_1_ = (BlockPos)this.field_150657_g.get(lvt_2_1_);

                if (lvt_3_1_.getX() == p_180363_1_.getX() && lvt_3_1_.getZ() == p_180363_1_.getZ())
                {
                    return true;
                }
            }

            return false;
        }

        protected int countAdjacentRails()
        {
            int lvt_1_1_ = 0;

            for (EnumFacing lvt_3_1_ : EnumFacing.Plane.HORIZONTAL)
            {
                if (this.hasRailAt(this.pos.offset(lvt_3_1_)))
                {
                    ++lvt_1_1_;
                }
            }

            return lvt_1_1_;
        }

        private boolean func_150649_b(BlockRailBase.Rail rail)
        {
            return this.func_150653_a(rail) || this.field_150657_g.size() != 2;
        }

        private void func_150645_c(BlockRailBase.Rail p_150645_1_)
        {
            this.field_150657_g.add(p_150645_1_.pos);
            BlockPos lvt_2_1_ = this.pos.north();
            BlockPos lvt_3_1_ = this.pos.south();
            BlockPos lvt_4_1_ = this.pos.west();
            BlockPos lvt_5_1_ = this.pos.east();
            boolean lvt_6_1_ = this.func_180363_c(lvt_2_1_);
            boolean lvt_7_1_ = this.func_180363_c(lvt_3_1_);
            boolean lvt_8_1_ = this.func_180363_c(lvt_4_1_);
            boolean lvt_9_1_ = this.func_180363_c(lvt_5_1_);
            BlockRailBase.EnumRailDirection lvt_10_1_ = null;

            if (lvt_6_1_ || lvt_7_1_)
            {
                lvt_10_1_ = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            }

            if (lvt_8_1_ || lvt_9_1_)
            {
                lvt_10_1_ = BlockRailBase.EnumRailDirection.EAST_WEST;
            }

            if (!this.isPowered)
            {
                if (lvt_7_1_ && lvt_9_1_ && !lvt_6_1_ && !lvt_8_1_)
                {
                    lvt_10_1_ = BlockRailBase.EnumRailDirection.SOUTH_EAST;
                }

                if (lvt_7_1_ && lvt_8_1_ && !lvt_6_1_ && !lvt_9_1_)
                {
                    lvt_10_1_ = BlockRailBase.EnumRailDirection.SOUTH_WEST;
                }

                if (lvt_6_1_ && lvt_8_1_ && !lvt_7_1_ && !lvt_9_1_)
                {
                    lvt_10_1_ = BlockRailBase.EnumRailDirection.NORTH_WEST;
                }

                if (lvt_6_1_ && lvt_9_1_ && !lvt_7_1_ && !lvt_8_1_)
                {
                    lvt_10_1_ = BlockRailBase.EnumRailDirection.NORTH_EAST;
                }
            }

            if (lvt_10_1_ == BlockRailBase.EnumRailDirection.NORTH_SOUTH)
            {
                if (BlockRailBase.isRailBlock(this.world, lvt_2_1_.up()))
                {
                    lvt_10_1_ = BlockRailBase.EnumRailDirection.ASCENDING_NORTH;
                }

                if (BlockRailBase.isRailBlock(this.world, lvt_3_1_.up()))
                {
                    lvt_10_1_ = BlockRailBase.EnumRailDirection.ASCENDING_SOUTH;
                }
            }

            if (lvt_10_1_ == BlockRailBase.EnumRailDirection.EAST_WEST)
            {
                if (BlockRailBase.isRailBlock(this.world, lvt_5_1_.up()))
                {
                    lvt_10_1_ = BlockRailBase.EnumRailDirection.ASCENDING_EAST;
                }

                if (BlockRailBase.isRailBlock(this.world, lvt_4_1_.up()))
                {
                    lvt_10_1_ = BlockRailBase.EnumRailDirection.ASCENDING_WEST;
                }
            }

            if (lvt_10_1_ == null)
            {
                lvt_10_1_ = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            }

            this.state = this.state.withProperty(this.block.getShapeProperty(), lvt_10_1_);
            this.world.setBlockState(this.pos, this.state, 3);
        }

        private boolean func_180361_d(BlockPos p_180361_1_)
        {
            BlockRailBase.Rail lvt_2_1_ = this.findRailAt(p_180361_1_);

            if (lvt_2_1_ == null)
            {
                return false;
            }
            else
            {
                lvt_2_1_.func_150651_b();
                return lvt_2_1_.func_150649_b(this);
            }
        }

        public BlockRailBase.Rail func_180364_a(boolean p_180364_1_, boolean p_180364_2_)
        {
            BlockPos lvt_3_1_ = this.pos.north();
            BlockPos lvt_4_1_ = this.pos.south();
            BlockPos lvt_5_1_ = this.pos.west();
            BlockPos lvt_6_1_ = this.pos.east();
            boolean lvt_7_1_ = this.func_180361_d(lvt_3_1_);
            boolean lvt_8_1_ = this.func_180361_d(lvt_4_1_);
            boolean lvt_9_1_ = this.func_180361_d(lvt_5_1_);
            boolean lvt_10_1_ = this.func_180361_d(lvt_6_1_);
            BlockRailBase.EnumRailDirection lvt_11_1_ = null;

            if ((lvt_7_1_ || lvt_8_1_) && !lvt_9_1_ && !lvt_10_1_)
            {
                lvt_11_1_ = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            }

            if ((lvt_9_1_ || lvt_10_1_) && !lvt_7_1_ && !lvt_8_1_)
            {
                lvt_11_1_ = BlockRailBase.EnumRailDirection.EAST_WEST;
            }

            if (!this.isPowered)
            {
                if (lvt_8_1_ && lvt_10_1_ && !lvt_7_1_ && !lvt_9_1_)
                {
                    lvt_11_1_ = BlockRailBase.EnumRailDirection.SOUTH_EAST;
                }

                if (lvt_8_1_ && lvt_9_1_ && !lvt_7_1_ && !lvt_10_1_)
                {
                    lvt_11_1_ = BlockRailBase.EnumRailDirection.SOUTH_WEST;
                }

                if (lvt_7_1_ && lvt_9_1_ && !lvt_8_1_ && !lvt_10_1_)
                {
                    lvt_11_1_ = BlockRailBase.EnumRailDirection.NORTH_WEST;
                }

                if (lvt_7_1_ && lvt_10_1_ && !lvt_8_1_ && !lvt_9_1_)
                {
                    lvt_11_1_ = BlockRailBase.EnumRailDirection.NORTH_EAST;
                }
            }

            if (lvt_11_1_ == null)
            {
                if (lvt_7_1_ || lvt_8_1_)
                {
                    lvt_11_1_ = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
                }

                if (lvt_9_1_ || lvt_10_1_)
                {
                    lvt_11_1_ = BlockRailBase.EnumRailDirection.EAST_WEST;
                }

                if (!this.isPowered)
                {
                    if (p_180364_1_)
                    {
                        if (lvt_8_1_ && lvt_10_1_)
                        {
                            lvt_11_1_ = BlockRailBase.EnumRailDirection.SOUTH_EAST;
                        }

                        if (lvt_9_1_ && lvt_8_1_)
                        {
                            lvt_11_1_ = BlockRailBase.EnumRailDirection.SOUTH_WEST;
                        }

                        if (lvt_10_1_ && lvt_7_1_)
                        {
                            lvt_11_1_ = BlockRailBase.EnumRailDirection.NORTH_EAST;
                        }

                        if (lvt_7_1_ && lvt_9_1_)
                        {
                            lvt_11_1_ = BlockRailBase.EnumRailDirection.NORTH_WEST;
                        }
                    }
                    else
                    {
                        if (lvt_7_1_ && lvt_9_1_)
                        {
                            lvt_11_1_ = BlockRailBase.EnumRailDirection.NORTH_WEST;
                        }

                        if (lvt_10_1_ && lvt_7_1_)
                        {
                            lvt_11_1_ = BlockRailBase.EnumRailDirection.NORTH_EAST;
                        }

                        if (lvt_9_1_ && lvt_8_1_)
                        {
                            lvt_11_1_ = BlockRailBase.EnumRailDirection.SOUTH_WEST;
                        }

                        if (lvt_8_1_ && lvt_10_1_)
                        {
                            lvt_11_1_ = BlockRailBase.EnumRailDirection.SOUTH_EAST;
                        }
                    }
                }
            }

            if (lvt_11_1_ == BlockRailBase.EnumRailDirection.NORTH_SOUTH)
            {
                if (BlockRailBase.isRailBlock(this.world, lvt_3_1_.up()))
                {
                    lvt_11_1_ = BlockRailBase.EnumRailDirection.ASCENDING_NORTH;
                }

                if (BlockRailBase.isRailBlock(this.world, lvt_4_1_.up()))
                {
                    lvt_11_1_ = BlockRailBase.EnumRailDirection.ASCENDING_SOUTH;
                }
            }

            if (lvt_11_1_ == BlockRailBase.EnumRailDirection.EAST_WEST)
            {
                if (BlockRailBase.isRailBlock(this.world, lvt_6_1_.up()))
                {
                    lvt_11_1_ = BlockRailBase.EnumRailDirection.ASCENDING_EAST;
                }

                if (BlockRailBase.isRailBlock(this.world, lvt_5_1_.up()))
                {
                    lvt_11_1_ = BlockRailBase.EnumRailDirection.ASCENDING_WEST;
                }
            }

            if (lvt_11_1_ == null)
            {
                lvt_11_1_ = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            }

            this.func_180360_a(lvt_11_1_);
            this.state = this.state.withProperty(this.block.getShapeProperty(), lvt_11_1_);

            if (p_180364_2_ || this.world.getBlockState(this.pos) != this.state)
            {
                this.world.setBlockState(this.pos, this.state, 3);

                for (int lvt_12_1_ = 0; lvt_12_1_ < this.field_150657_g.size(); ++lvt_12_1_)
                {
                    BlockRailBase.Rail lvt_13_1_ = this.findRailAt((BlockPos)this.field_150657_g.get(lvt_12_1_));

                    if (lvt_13_1_ != null)
                    {
                        lvt_13_1_.func_150651_b();

                        if (lvt_13_1_.func_150649_b(this))
                        {
                            lvt_13_1_.func_150645_c(this);
                        }
                    }
                }
            }

            return this;
        }

        public IBlockState getBlockState()
        {
            return this.state;
        }
    }
}
