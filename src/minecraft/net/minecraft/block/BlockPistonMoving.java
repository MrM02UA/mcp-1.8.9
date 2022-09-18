package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonMoving extends BlockContainer
{
    public static final PropertyDirection FACING = BlockPistonExtension.FACING;
    public static final PropertyEnum<BlockPistonExtension.EnumPistonType> TYPE = BlockPistonExtension.TYPE;

    public BlockPistonMoving()
    {
        super(Material.piston);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(TYPE, BlockPistonExtension.EnumPistonType.DEFAULT));
        this.setHardness(-1.0F);
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return null;
    }

    public static TileEntity newTileEntity(IBlockState state, EnumFacing facing, boolean extending, boolean renderHead)
    {
        return new TileEntityPiston(state, facing, extending, renderHead);
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity lvt_4_1_ = worldIn.getTileEntity(pos);

        if (lvt_4_1_ instanceof TileEntityPiston)
        {
            ((TileEntityPiston)lvt_4_1_).clearPistonTileEntity();
        }
        else
        {
            super.breakBlock(worldIn, pos, state);
        }
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return false;
    }

    /**
     * Check whether this Block can be placed on the given side
     */
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side)
    {
        return false;
    }

    /**
     * Called when a player destroys this Block
     */
    public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state)
    {
        BlockPos lvt_4_1_ = pos.offset(((EnumFacing)state.getValue(FACING)).getOpposite());
        IBlockState lvt_5_1_ = worldIn.getBlockState(lvt_4_1_);

        if (lvt_5_1_.getBlock() instanceof BlockPistonBase && ((Boolean)lvt_5_1_.getValue(BlockPistonBase.EXTENDED)).booleanValue())
        {
            worldIn.setBlockToAir(lvt_4_1_);
        }
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

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote && worldIn.getTileEntity(pos) == null)
        {
            worldIn.setBlockToAir(pos);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return null;
    }

    /**
     * Spawns this Block's drops into the World as EntityItems.
     */
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        if (!worldIn.isRemote)
        {
            TileEntityPiston lvt_6_1_ = this.getTileEntity(worldIn, pos);

            if (lvt_6_1_ != null)
            {
                IBlockState lvt_7_1_ = lvt_6_1_.getPistonState();
                lvt_7_1_.getBlock().dropBlockAsItem(worldIn, pos, lvt_7_1_, 0);
            }
        }
    }

    /**
     * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit.
     */
    public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end)
    {
        return null;
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        if (!worldIn.isRemote)
        {
            worldIn.getTileEntity(pos);
        }
    }

    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntityPiston lvt_4_1_ = this.getTileEntity(worldIn, pos);

        if (lvt_4_1_ == null)
        {
            return null;
        }
        else
        {
            float lvt_5_1_ = lvt_4_1_.getProgress(0.0F);

            if (lvt_4_1_.isExtending())
            {
                lvt_5_1_ = 1.0F - lvt_5_1_;
            }

            return this.getBoundingBox(worldIn, pos, lvt_4_1_.getPistonState(), lvt_5_1_, lvt_4_1_.getFacing());
        }
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
        TileEntityPiston lvt_3_1_ = this.getTileEntity(worldIn, pos);

        if (lvt_3_1_ != null)
        {
            IBlockState lvt_4_1_ = lvt_3_1_.getPistonState();
            Block lvt_5_1_ = lvt_4_1_.getBlock();

            if (lvt_5_1_ == this || lvt_5_1_.getMaterial() == Material.air)
            {
                return;
            }

            float lvt_6_1_ = lvt_3_1_.getProgress(0.0F);

            if (lvt_3_1_.isExtending())
            {
                lvt_6_1_ = 1.0F - lvt_6_1_;
            }

            lvt_5_1_.setBlockBoundsBasedOnState(worldIn, pos);

            if (lvt_5_1_ == Blocks.piston || lvt_5_1_ == Blocks.sticky_piston)
            {
                lvt_6_1_ = 0.0F;
            }

            EnumFacing lvt_7_1_ = lvt_3_1_.getFacing();
            this.minX = lvt_5_1_.getBlockBoundsMinX() - (double)((float)lvt_7_1_.getFrontOffsetX() * lvt_6_1_);
            this.minY = lvt_5_1_.getBlockBoundsMinY() - (double)((float)lvt_7_1_.getFrontOffsetY() * lvt_6_1_);
            this.minZ = lvt_5_1_.getBlockBoundsMinZ() - (double)((float)lvt_7_1_.getFrontOffsetZ() * lvt_6_1_);
            this.maxX = lvt_5_1_.getBlockBoundsMaxX() - (double)((float)lvt_7_1_.getFrontOffsetX() * lvt_6_1_);
            this.maxY = lvt_5_1_.getBlockBoundsMaxY() - (double)((float)lvt_7_1_.getFrontOffsetY() * lvt_6_1_);
            this.maxZ = lvt_5_1_.getBlockBoundsMaxZ() - (double)((float)lvt_7_1_.getFrontOffsetZ() * lvt_6_1_);
        }
    }

    public AxisAlignedBB getBoundingBox(World worldIn, BlockPos pos, IBlockState extendingBlock, float progress, EnumFacing direction)
    {
        if (extendingBlock.getBlock() != this && extendingBlock.getBlock().getMaterial() != Material.air)
        {
            AxisAlignedBB lvt_6_1_ = extendingBlock.getBlock().getCollisionBoundingBox(worldIn, pos, extendingBlock);

            if (lvt_6_1_ == null)
            {
                return null;
            }
            else
            {
                double lvt_7_1_ = lvt_6_1_.minX;
                double lvt_9_1_ = lvt_6_1_.minY;
                double lvt_11_1_ = lvt_6_1_.minZ;
                double lvt_13_1_ = lvt_6_1_.maxX;
                double lvt_15_1_ = lvt_6_1_.maxY;
                double lvt_17_1_ = lvt_6_1_.maxZ;

                if (direction.getFrontOffsetX() < 0)
                {
                    lvt_7_1_ -= (double)((float)direction.getFrontOffsetX() * progress);
                }
                else
                {
                    lvt_13_1_ -= (double)((float)direction.getFrontOffsetX() * progress);
                }

                if (direction.getFrontOffsetY() < 0)
                {
                    lvt_9_1_ -= (double)((float)direction.getFrontOffsetY() * progress);
                }
                else
                {
                    lvt_15_1_ -= (double)((float)direction.getFrontOffsetY() * progress);
                }

                if (direction.getFrontOffsetZ() < 0)
                {
                    lvt_11_1_ -= (double)((float)direction.getFrontOffsetZ() * progress);
                }
                else
                {
                    lvt_17_1_ -= (double)((float)direction.getFrontOffsetZ() * progress);
                }

                return new AxisAlignedBB(lvt_7_1_, lvt_9_1_, lvt_11_1_, lvt_13_1_, lvt_15_1_, lvt_17_1_);
            }
        }
        else
        {
            return null;
        }
    }

    private TileEntityPiston getTileEntity(IBlockAccess worldIn, BlockPos pos)
    {
        TileEntity lvt_3_1_ = worldIn.getTileEntity(pos);
        return lvt_3_1_ instanceof TileEntityPiston ? (TileEntityPiston)lvt_3_1_ : null;
    }

    public Item getItem(World worldIn, BlockPos pos)
    {
        return null;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, BlockPistonExtension.getFacing(meta)).withProperty(TYPE, (meta & 8) > 0 ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int lvt_2_1_ = 0;
        lvt_2_1_ = lvt_2_1_ | ((EnumFacing)state.getValue(FACING)).getIndex();

        if (state.getValue(TYPE) == BlockPistonExtension.EnumPistonType.STICKY)
        {
            lvt_2_1_ |= 8;
        }

        return lvt_2_1_;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {FACING, TYPE});
    }
}
