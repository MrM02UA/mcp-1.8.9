package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemLead;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFence extends Block
{
    /** Whether this fence connects in the northern direction */
    public static final PropertyBool NORTH = PropertyBool.create("north");

    /** Whether this fence connects in the eastern direction */
    public static final PropertyBool EAST = PropertyBool.create("east");

    /** Whether this fence connects in the southern direction */
    public static final PropertyBool SOUTH = PropertyBool.create("south");

    /** Whether this fence connects in the western direction */
    public static final PropertyBool WEST = PropertyBool.create("west");

    public BlockFence(Material materialIn)
    {
        this(materialIn, materialIn.getMaterialMapColor());
    }

    public BlockFence(Material p_i46395_1_, MapColor p_i46395_2_)
    {
        super(p_i46395_1_, p_i46395_2_);
        this.setDefaultState(this.blockState.getBaseState().withProperty(NORTH, Boolean.valueOf(false)).withProperty(EAST, Boolean.valueOf(false)).withProperty(SOUTH, Boolean.valueOf(false)).withProperty(WEST, Boolean.valueOf(false)));
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    /**
     * Add all collision boxes of this Block to the list that intersect with the given mask.
     */
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity)
    {
        boolean lvt_7_1_ = this.canConnectTo(worldIn, pos.north());
        boolean lvt_8_1_ = this.canConnectTo(worldIn, pos.south());
        boolean lvt_9_1_ = this.canConnectTo(worldIn, pos.west());
        boolean lvt_10_1_ = this.canConnectTo(worldIn, pos.east());
        float lvt_11_1_ = 0.375F;
        float lvt_12_1_ = 0.625F;
        float lvt_13_1_ = 0.375F;
        float lvt_14_1_ = 0.625F;

        if (lvt_7_1_)
        {
            lvt_13_1_ = 0.0F;
        }

        if (lvt_8_1_)
        {
            lvt_14_1_ = 1.0F;
        }

        if (lvt_7_1_ || lvt_8_1_)
        {
            this.setBlockBounds(lvt_11_1_, 0.0F, lvt_13_1_, lvt_12_1_, 1.5F, lvt_14_1_);
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        }

        lvt_13_1_ = 0.375F;
        lvt_14_1_ = 0.625F;

        if (lvt_9_1_)
        {
            lvt_11_1_ = 0.0F;
        }

        if (lvt_10_1_)
        {
            lvt_12_1_ = 1.0F;
        }

        if (lvt_9_1_ || lvt_10_1_ || !lvt_7_1_ && !lvt_8_1_)
        {
            this.setBlockBounds(lvt_11_1_, 0.0F, lvt_13_1_, lvt_12_1_, 1.5F, lvt_14_1_);
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        }

        if (lvt_7_1_)
        {
            lvt_13_1_ = 0.0F;
        }

        if (lvt_8_1_)
        {
            lvt_14_1_ = 1.0F;
        }

        this.setBlockBounds(lvt_11_1_, 0.0F, lvt_13_1_, lvt_12_1_, 1.0F, lvt_14_1_);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
        boolean lvt_3_1_ = this.canConnectTo(worldIn, pos.north());
        boolean lvt_4_1_ = this.canConnectTo(worldIn, pos.south());
        boolean lvt_5_1_ = this.canConnectTo(worldIn, pos.west());
        boolean lvt_6_1_ = this.canConnectTo(worldIn, pos.east());
        float lvt_7_1_ = 0.375F;
        float lvt_8_1_ = 0.625F;
        float lvt_9_1_ = 0.375F;
        float lvt_10_1_ = 0.625F;

        if (lvt_3_1_)
        {
            lvt_9_1_ = 0.0F;
        }

        if (lvt_4_1_)
        {
            lvt_10_1_ = 1.0F;
        }

        if (lvt_5_1_)
        {
            lvt_7_1_ = 0.0F;
        }

        if (lvt_6_1_)
        {
            lvt_8_1_ = 1.0F;
        }

        this.setBlockBounds(lvt_7_1_, 0.0F, lvt_9_1_, lvt_8_1_, 1.0F, lvt_10_1_);
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

    public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
    {
        return false;
    }

    public boolean canConnectTo(IBlockAccess worldIn, BlockPos pos)
    {
        Block lvt_3_1_ = worldIn.getBlockState(pos).getBlock();
        return lvt_3_1_ == Blocks.barrier ? false : ((!(lvt_3_1_ instanceof BlockFence) || lvt_3_1_.blockMaterial != this.blockMaterial) && !(lvt_3_1_ instanceof BlockFenceGate) ? (lvt_3_1_.blockMaterial.isOpaque() && lvt_3_1_.isFullCube() ? lvt_3_1_.blockMaterial != Material.gourd : false) : true);
    }

    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        return true;
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        return worldIn.isRemote ? true : ItemLead.attachToFence(playerIn, worldIn, pos);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return state.withProperty(NORTH, Boolean.valueOf(this.canConnectTo(worldIn, pos.north()))).withProperty(EAST, Boolean.valueOf(this.canConnectTo(worldIn, pos.east()))).withProperty(SOUTH, Boolean.valueOf(this.canConnectTo(worldIn, pos.south()))).withProperty(WEST, Boolean.valueOf(this.canConnectTo(worldIn, pos.west())));
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {NORTH, EAST, WEST, SOUTH});
    }
}
