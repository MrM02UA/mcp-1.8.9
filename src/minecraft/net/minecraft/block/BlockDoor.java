package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDoor extends Block
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool OPEN = PropertyBool.create("open");
    public static final PropertyEnum<BlockDoor.EnumHingePosition> HINGE = PropertyEnum.<BlockDoor.EnumHingePosition>create("hinge", BlockDoor.EnumHingePosition.class);
    public static final PropertyBool POWERED = PropertyBool.create("powered");
    public static final PropertyEnum<BlockDoor.EnumDoorHalf> HALF = PropertyEnum.<BlockDoor.EnumDoorHalf>create("half", BlockDoor.EnumDoorHalf.class);

    protected BlockDoor(Material materialIn)
    {
        super(materialIn);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(OPEN, Boolean.valueOf(false)).withProperty(HINGE, BlockDoor.EnumHingePosition.LEFT).withProperty(POWERED, Boolean.valueOf(false)).withProperty(HALF, BlockDoor.EnumDoorHalf.LOWER));
    }

    /**
     * Gets the localized name of this block. Used for the statistics page.
     */
    public String getLocalizedName()
    {
        return StatCollector.translateToLocal((this.getUnlocalizedName() + ".name").replaceAll("tile", "item"));
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
    {
        return isOpen(combineMetadata(worldIn, pos));
    }

    public boolean isFullCube()
    {
        return false;
    }

    public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos)
    {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        return super.getSelectedBoundingBox(worldIn, pos);
    }

    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
    {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        return super.getCollisionBoundingBox(worldIn, pos, state);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
        this.setBoundBasedOnMeta(combineMetadata(worldIn, pos));
    }

    private void setBoundBasedOnMeta(int combinedMeta)
    {
        float lvt_2_1_ = 0.1875F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
        EnumFacing lvt_3_1_ = getFacing(combinedMeta);
        boolean lvt_4_1_ = isOpen(combinedMeta);
        boolean lvt_5_1_ = isHingeLeft(combinedMeta);

        if (lvt_4_1_)
        {
            if (lvt_3_1_ == EnumFacing.EAST)
            {
                if (!lvt_5_1_)
                {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, lvt_2_1_);
                }
                else
                {
                    this.setBlockBounds(0.0F, 0.0F, 1.0F - lvt_2_1_, 1.0F, 1.0F, 1.0F);
                }
            }
            else if (lvt_3_1_ == EnumFacing.SOUTH)
            {
                if (!lvt_5_1_)
                {
                    this.setBlockBounds(1.0F - lvt_2_1_, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                }
                else
                {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, lvt_2_1_, 1.0F, 1.0F);
                }
            }
            else if (lvt_3_1_ == EnumFacing.WEST)
            {
                if (!lvt_5_1_)
                {
                    this.setBlockBounds(0.0F, 0.0F, 1.0F - lvt_2_1_, 1.0F, 1.0F, 1.0F);
                }
                else
                {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, lvt_2_1_);
                }
            }
            else if (lvt_3_1_ == EnumFacing.NORTH)
            {
                if (!lvt_5_1_)
                {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, lvt_2_1_, 1.0F, 1.0F);
                }
                else
                {
                    this.setBlockBounds(1.0F - lvt_2_1_, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                }
            }
        }
        else if (lvt_3_1_ == EnumFacing.EAST)
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, lvt_2_1_, 1.0F, 1.0F);
        }
        else if (lvt_3_1_ == EnumFacing.SOUTH)
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, lvt_2_1_);
        }
        else if (lvt_3_1_ == EnumFacing.WEST)
        {
            this.setBlockBounds(1.0F - lvt_2_1_, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
        else if (lvt_3_1_ == EnumFacing.NORTH)
        {
            this.setBlockBounds(0.0F, 0.0F, 1.0F - lvt_2_1_, 1.0F, 1.0F, 1.0F);
        }
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (this.blockMaterial == Material.iron)
        {
            return true;
        }
        else
        {
            BlockPos lvt_9_1_ = state.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER ? pos : pos.down();
            IBlockState lvt_10_1_ = pos.equals(lvt_9_1_) ? state : worldIn.getBlockState(lvt_9_1_);

            if (lvt_10_1_.getBlock() != this)
            {
                return false;
            }
            else
            {
                state = lvt_10_1_.cycleProperty(OPEN);
                worldIn.setBlockState(lvt_9_1_, state, 2);
                worldIn.markBlockRangeForRenderUpdate(lvt_9_1_, pos);
                worldIn.playAuxSFXAtEntity(playerIn, ((Boolean)state.getValue(OPEN)).booleanValue() ? 1003 : 1006, pos, 0);
                return true;
            }
        }
    }

    public void toggleDoor(World worldIn, BlockPos pos, boolean open)
    {
        IBlockState lvt_4_1_ = worldIn.getBlockState(pos);

        if (lvt_4_1_.getBlock() == this)
        {
            BlockPos lvt_5_1_ = lvt_4_1_.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER ? pos : pos.down();
            IBlockState lvt_6_1_ = pos == lvt_5_1_ ? lvt_4_1_ : worldIn.getBlockState(lvt_5_1_);

            if (lvt_6_1_.getBlock() == this && ((Boolean)lvt_6_1_.getValue(OPEN)).booleanValue() != open)
            {
                worldIn.setBlockState(lvt_5_1_, lvt_6_1_.withProperty(OPEN, Boolean.valueOf(open)), 2);
                worldIn.markBlockRangeForRenderUpdate(lvt_5_1_, pos);
                worldIn.playAuxSFXAtEntity((EntityPlayer)null, open ? 1003 : 1006, pos, 0);
            }
        }
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER)
        {
            BlockPos lvt_5_1_ = pos.down();
            IBlockState lvt_6_1_ = worldIn.getBlockState(lvt_5_1_);

            if (lvt_6_1_.getBlock() != this)
            {
                worldIn.setBlockToAir(pos);
            }
            else if (neighborBlock != this)
            {
                this.onNeighborBlockChange(worldIn, lvt_5_1_, lvt_6_1_, neighborBlock);
            }
        }
        else
        {
            boolean lvt_5_2_ = false;
            BlockPos lvt_6_2_ = pos.up();
            IBlockState lvt_7_1_ = worldIn.getBlockState(lvt_6_2_);

            if (lvt_7_1_.getBlock() != this)
            {
                worldIn.setBlockToAir(pos);
                lvt_5_2_ = true;
            }

            if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down()))
            {
                worldIn.setBlockToAir(pos);
                lvt_5_2_ = true;

                if (lvt_7_1_.getBlock() == this)
                {
                    worldIn.setBlockToAir(lvt_6_2_);
                }
            }

            if (lvt_5_2_)
            {
                if (!worldIn.isRemote)
                {
                    this.dropBlockAsItem(worldIn, pos, state, 0);
                }
            }
            else
            {
                boolean lvt_8_1_ = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(lvt_6_2_);

                if ((lvt_8_1_ || neighborBlock.canProvidePower()) && neighborBlock != this && lvt_8_1_ != ((Boolean)lvt_7_1_.getValue(POWERED)).booleanValue())
                {
                    worldIn.setBlockState(lvt_6_2_, lvt_7_1_.withProperty(POWERED, Boolean.valueOf(lvt_8_1_)), 2);

                    if (lvt_8_1_ != ((Boolean)state.getValue(OPEN)).booleanValue())
                    {
                        worldIn.setBlockState(pos, state.withProperty(OPEN, Boolean.valueOf(lvt_8_1_)), 2);
                        worldIn.markBlockRangeForRenderUpdate(pos, pos);
                        worldIn.playAuxSFXAtEntity((EntityPlayer)null, lvt_8_1_ ? 1003 : 1006, pos, 0);
                    }
                }
            }
        }
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? null : this.getItem();
    }

    /**
     * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit.
     */
    public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end)
    {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        return super.collisionRayTrace(worldIn, pos, start, end);
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return pos.getY() >= 255 ? false : World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && super.canPlaceBlockAt(worldIn, pos) && super.canPlaceBlockAt(worldIn, pos.up());
    }

    public int getMobilityFlag()
    {
        return 1;
    }

    public static int combineMetadata(IBlockAccess worldIn, BlockPos pos)
    {
        IBlockState lvt_2_1_ = worldIn.getBlockState(pos);
        int lvt_3_1_ = lvt_2_1_.getBlock().getMetaFromState(lvt_2_1_);
        boolean lvt_4_1_ = isTop(lvt_3_1_);
        IBlockState lvt_5_1_ = worldIn.getBlockState(pos.down());
        int lvt_6_1_ = lvt_5_1_.getBlock().getMetaFromState(lvt_5_1_);
        int lvt_7_1_ = lvt_4_1_ ? lvt_6_1_ : lvt_3_1_;
        IBlockState lvt_8_1_ = worldIn.getBlockState(pos.up());
        int lvt_9_1_ = lvt_8_1_.getBlock().getMetaFromState(lvt_8_1_);
        int lvt_10_1_ = lvt_4_1_ ? lvt_3_1_ : lvt_9_1_;
        boolean lvt_11_1_ = (lvt_10_1_ & 1) != 0;
        boolean lvt_12_1_ = (lvt_10_1_ & 2) != 0;
        return removeHalfBit(lvt_7_1_) | (lvt_4_1_ ? 8 : 0) | (lvt_11_1_ ? 16 : 0) | (lvt_12_1_ ? 32 : 0);
    }

    public Item getItem(World worldIn, BlockPos pos)
    {
        return this.getItem();
    }

    private Item getItem()
    {
        return this == Blocks.iron_door ? Items.iron_door : (this == Blocks.spruce_door ? Items.spruce_door : (this == Blocks.birch_door ? Items.birch_door : (this == Blocks.jungle_door ? Items.jungle_door : (this == Blocks.acacia_door ? Items.acacia_door : (this == Blocks.dark_oak_door ? Items.dark_oak_door : Items.oak_door)))));
    }

    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        BlockPos lvt_5_1_ = pos.down();

        if (player.capabilities.isCreativeMode && state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER && worldIn.getBlockState(lvt_5_1_).getBlock() == this)
        {
            worldIn.setBlockToAir(lvt_5_1_);
        }
    }

    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.CUTOUT;
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER)
        {
            IBlockState lvt_4_1_ = worldIn.getBlockState(pos.up());

            if (lvt_4_1_.getBlock() == this)
            {
                state = state.withProperty(HINGE, lvt_4_1_.getValue(HINGE)).withProperty(POWERED, lvt_4_1_.getValue(POWERED));
            }
        }
        else
        {
            IBlockState lvt_4_2_ = worldIn.getBlockState(pos.down());

            if (lvt_4_2_.getBlock() == this)
            {
                state = state.withProperty(FACING, lvt_4_2_.getValue(FACING)).withProperty(OPEN, lvt_4_2_.getValue(OPEN));
            }
        }

        return state;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return (meta & 8) > 0 ? this.getDefaultState().withProperty(HALF, BlockDoor.EnumDoorHalf.UPPER).withProperty(HINGE, (meta & 1) > 0 ? BlockDoor.EnumHingePosition.RIGHT : BlockDoor.EnumHingePosition.LEFT).withProperty(POWERED, Boolean.valueOf((meta & 2) > 0)) : this.getDefaultState().withProperty(HALF, BlockDoor.EnumDoorHalf.LOWER).withProperty(FACING, EnumFacing.getHorizontal(meta & 3).rotateYCCW()).withProperty(OPEN, Boolean.valueOf((meta & 4) > 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int lvt_2_1_ = 0;

        if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER)
        {
            lvt_2_1_ = lvt_2_1_ | 8;

            if (state.getValue(HINGE) == BlockDoor.EnumHingePosition.RIGHT)
            {
                lvt_2_1_ |= 1;
            }

            if (((Boolean)state.getValue(POWERED)).booleanValue())
            {
                lvt_2_1_ |= 2;
            }
        }
        else
        {
            lvt_2_1_ = lvt_2_1_ | ((EnumFacing)state.getValue(FACING)).rotateY().getHorizontalIndex();

            if (((Boolean)state.getValue(OPEN)).booleanValue())
            {
                lvt_2_1_ |= 4;
            }
        }

        return lvt_2_1_;
    }

    protected static int removeHalfBit(int meta)
    {
        return meta & 7;
    }

    public static boolean isOpen(IBlockAccess worldIn, BlockPos pos)
    {
        return isOpen(combineMetadata(worldIn, pos));
    }

    public static EnumFacing getFacing(IBlockAccess worldIn, BlockPos pos)
    {
        return getFacing(combineMetadata(worldIn, pos));
    }

    public static EnumFacing getFacing(int combinedMeta)
    {
        return EnumFacing.getHorizontal(combinedMeta & 3).rotateYCCW();
    }

    protected static boolean isOpen(int combinedMeta)
    {
        return (combinedMeta & 4) != 0;
    }

    protected static boolean isTop(int meta)
    {
        return (meta & 8) != 0;
    }

    protected static boolean isHingeLeft(int combinedMeta)
    {
        return (combinedMeta & 16) != 0;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {HALF, FACING, OPEN, HINGE, POWERED});
    }

    public static enum EnumDoorHalf implements IStringSerializable
    {
        UPPER,
        LOWER;

        public String toString()
        {
            return this.getName();
        }

        public String getName()
        {
            return this == UPPER ? "upper" : "lower";
        }
    }

    public static enum EnumHingePosition implements IStringSerializable
    {
        LEFT,
        RIGHT;

        public String toString()
        {
            return this.getName();
        }

        public String getName()
        {
            return this == LEFT ? "left" : "right";
        }
    }
}
