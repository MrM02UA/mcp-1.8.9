package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonBase extends Block
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyBool EXTENDED = PropertyBool.create("extended");

    /** This piston is the sticky one? */
    private final boolean isSticky;

    public BlockPistonBase(boolean isSticky)
    {
        super(Material.piston);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(EXTENDED, Boolean.valueOf(false)));
        this.isSticky = isSticky;
        this.setStepSound(soundTypePiston);
        this.setHardness(0.5F);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        worldIn.setBlockState(pos, state.withProperty(FACING, getFacingFromEntity(worldIn, pos, placer)), 2);

        if (!worldIn.isRemote)
        {
            this.checkForMove(worldIn, pos, state);
        }
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        if (!worldIn.isRemote)
        {
            this.checkForMove(worldIn, pos, state);
        }
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote && worldIn.getTileEntity(pos) == null)
        {
            this.checkForMove(worldIn, pos, state);
        }
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, getFacingFromEntity(worldIn, pos, placer)).withProperty(EXTENDED, Boolean.valueOf(false));
    }

    private void checkForMove(World worldIn, BlockPos pos, IBlockState state)
    {
        EnumFacing lvt_4_1_ = (EnumFacing)state.getValue(FACING);
        boolean lvt_5_1_ = this.shouldBeExtended(worldIn, pos, lvt_4_1_);

        if (lvt_5_1_ && !((Boolean)state.getValue(EXTENDED)).booleanValue())
        {
            if ((new BlockPistonStructureHelper(worldIn, pos, lvt_4_1_, true)).canMove())
            {
                worldIn.addBlockEvent(pos, this, 0, lvt_4_1_.getIndex());
            }
        }
        else if (!lvt_5_1_ && ((Boolean)state.getValue(EXTENDED)).booleanValue())
        {
            worldIn.setBlockState(pos, state.withProperty(EXTENDED, Boolean.valueOf(false)), 2);
            worldIn.addBlockEvent(pos, this, 1, lvt_4_1_.getIndex());
        }
    }

    private boolean shouldBeExtended(World worldIn, BlockPos pos, EnumFacing facing)
    {
        for (EnumFacing lvt_7_1_ : EnumFacing.values())
        {
            if (lvt_7_1_ != facing && worldIn.isSidePowered(pos.offset(lvt_7_1_), lvt_7_1_))
            {
                return true;
            }
        }

        if (worldIn.isSidePowered(pos, EnumFacing.DOWN))
        {
            return true;
        }
        else
        {
            BlockPos lvt_4_2_ = pos.up();

            for (EnumFacing lvt_8_1_ : EnumFacing.values())
            {
                if (lvt_8_1_ != EnumFacing.DOWN && worldIn.isSidePowered(lvt_4_2_.offset(lvt_8_1_), lvt_8_1_))
                {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * Called on both Client and Server when World#addBlockEvent is called
     */
    public boolean onBlockEventReceived(World worldIn, BlockPos pos, IBlockState state, int eventID, int eventParam)
    {
        EnumFacing lvt_6_1_ = (EnumFacing)state.getValue(FACING);

        if (!worldIn.isRemote)
        {
            boolean lvt_7_1_ = this.shouldBeExtended(worldIn, pos, lvt_6_1_);

            if (lvt_7_1_ && eventID == 1)
            {
                worldIn.setBlockState(pos, state.withProperty(EXTENDED, Boolean.valueOf(true)), 2);
                return false;
            }

            if (!lvt_7_1_ && eventID == 0)
            {
                return false;
            }
        }

        if (eventID == 0)
        {
            if (!this.doMove(worldIn, pos, lvt_6_1_, true))
            {
                return false;
            }

            worldIn.setBlockState(pos, state.withProperty(EXTENDED, Boolean.valueOf(true)), 2);
            worldIn.playSoundEffect((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, "tile.piston.out", 0.5F, worldIn.rand.nextFloat() * 0.25F + 0.6F);
        }
        else if (eventID == 1)
        {
            TileEntity lvt_7_2_ = worldIn.getTileEntity(pos.offset(lvt_6_1_));

            if (lvt_7_2_ instanceof TileEntityPiston)
            {
                ((TileEntityPiston)lvt_7_2_).clearPistonTileEntity();
            }

            worldIn.setBlockState(pos, Blocks.piston_extension.getDefaultState().withProperty(BlockPistonMoving.FACING, lvt_6_1_).withProperty(BlockPistonMoving.TYPE, this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT), 3);
            worldIn.setTileEntity(pos, BlockPistonMoving.newTileEntity(this.getStateFromMeta(eventParam), lvt_6_1_, false, true));

            if (this.isSticky)
            {
                BlockPos lvt_8_1_ = pos.add(lvt_6_1_.getFrontOffsetX() * 2, lvt_6_1_.getFrontOffsetY() * 2, lvt_6_1_.getFrontOffsetZ() * 2);
                Block lvt_9_1_ = worldIn.getBlockState(lvt_8_1_).getBlock();
                boolean lvt_10_1_ = false;

                if (lvt_9_1_ == Blocks.piston_extension)
                {
                    TileEntity lvt_11_1_ = worldIn.getTileEntity(lvt_8_1_);

                    if (lvt_11_1_ instanceof TileEntityPiston)
                    {
                        TileEntityPiston lvt_12_1_ = (TileEntityPiston)lvt_11_1_;

                        if (lvt_12_1_.getFacing() == lvt_6_1_ && lvt_12_1_.isExtending())
                        {
                            lvt_12_1_.clearPistonTileEntity();
                            lvt_10_1_ = true;
                        }
                    }
                }

                if (!lvt_10_1_ && lvt_9_1_.getMaterial() != Material.air && canPush(lvt_9_1_, worldIn, lvt_8_1_, lvt_6_1_.getOpposite(), false) && (lvt_9_1_.getMobilityFlag() == 0 || lvt_9_1_ == Blocks.piston || lvt_9_1_ == Blocks.sticky_piston))
                {
                    this.doMove(worldIn, pos, lvt_6_1_, false);
                }
            }
            else
            {
                worldIn.setBlockToAir(pos.offset(lvt_6_1_));
            }

            worldIn.playSoundEffect((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, "tile.piston.in", 0.5F, worldIn.rand.nextFloat() * 0.15F + 0.6F);
        }

        return true;
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
        IBlockState lvt_3_1_ = worldIn.getBlockState(pos);

        if (lvt_3_1_.getBlock() == this && ((Boolean)lvt_3_1_.getValue(EXTENDED)).booleanValue())
        {
            float lvt_4_1_ = 0.25F;
            EnumFacing lvt_5_1_ = (EnumFacing)lvt_3_1_.getValue(FACING);

            if (lvt_5_1_ != null)
            {
                switch (lvt_5_1_)
                {
                    case DOWN:
                        this.setBlockBounds(0.0F, 0.25F, 0.0F, 1.0F, 1.0F, 1.0F);
                        break;

                    case UP:
                        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
                        break;

                    case NORTH:
                        this.setBlockBounds(0.0F, 0.0F, 0.25F, 1.0F, 1.0F, 1.0F);
                        break;

                    case SOUTH:
                        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.75F);
                        break;

                    case WEST:
                        this.setBlockBounds(0.25F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                        break;

                    case EAST:
                        this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.75F, 1.0F, 1.0F);
                }
            }
        }
        else
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Add all collision boxes of this Block to the list that intersect with the given mask.
     */
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity)
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    }

    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
    {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        return super.getCollisionBoundingBox(worldIn, pos, state);
    }

    public boolean isFullCube()
    {
        return false;
    }

    public static EnumFacing getFacing(int meta)
    {
        int lvt_1_1_ = meta & 7;
        return lvt_1_1_ > 5 ? null : EnumFacing.getFront(lvt_1_1_);
    }

    public static EnumFacing getFacingFromEntity(World worldIn, BlockPos clickedBlock, EntityLivingBase entityIn)
    {
        if (MathHelper.abs((float)entityIn.posX - (float)clickedBlock.getX()) < 2.0F && MathHelper.abs((float)entityIn.posZ - (float)clickedBlock.getZ()) < 2.0F)
        {
            double lvt_3_1_ = entityIn.posY + (double)entityIn.getEyeHeight();

            if (lvt_3_1_ - (double)clickedBlock.getY() > 2.0D)
            {
                return EnumFacing.UP;
            }

            if ((double)clickedBlock.getY() - lvt_3_1_ > 0.0D)
            {
                return EnumFacing.DOWN;
            }
        }

        return entityIn.getHorizontalFacing().getOpposite();
    }

    public static boolean canPush(Block blockIn, World worldIn, BlockPos pos, EnumFacing direction, boolean allowDestroy)
    {
        if (blockIn == Blocks.obsidian)
        {
            return false;
        }
        else if (!worldIn.getWorldBorder().contains(pos))
        {
            return false;
        }
        else if (pos.getY() >= 0 && (direction != EnumFacing.DOWN || pos.getY() != 0))
        {
            if (pos.getY() <= worldIn.getHeight() - 1 && (direction != EnumFacing.UP || pos.getY() != worldIn.getHeight() - 1))
            {
                if (blockIn != Blocks.piston && blockIn != Blocks.sticky_piston)
                {
                    if (blockIn.getBlockHardness(worldIn, pos) == -1.0F)
                    {
                        return false;
                    }

                    if (blockIn.getMobilityFlag() == 2)
                    {
                        return false;
                    }

                    if (blockIn.getMobilityFlag() == 1)
                    {
                        if (!allowDestroy)
                        {
                            return false;
                        }

                        return true;
                    }
                }
                else if (((Boolean)worldIn.getBlockState(pos).getValue(EXTENDED)).booleanValue())
                {
                    return false;
                }

                return !(blockIn instanceof ITileEntityProvider);
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    private boolean doMove(World worldIn, BlockPos pos, EnumFacing direction, boolean extending)
    {
        if (!extending)
        {
            worldIn.setBlockToAir(pos.offset(direction));
        }

        BlockPistonStructureHelper lvt_5_1_ = new BlockPistonStructureHelper(worldIn, pos, direction, extending);
        List<BlockPos> lvt_6_1_ = lvt_5_1_.getBlocksToMove();
        List<BlockPos> lvt_7_1_ = lvt_5_1_.getBlocksToDestroy();

        if (!lvt_5_1_.canMove())
        {
            return false;
        }
        else
        {
            int lvt_8_1_ = lvt_6_1_.size() + lvt_7_1_.size();
            Block[] lvt_9_1_ = new Block[lvt_8_1_];
            EnumFacing lvt_10_1_ = extending ? direction : direction.getOpposite();

            for (int lvt_11_1_ = lvt_7_1_.size() - 1; lvt_11_1_ >= 0; --lvt_11_1_)
            {
                BlockPos lvt_12_1_ = (BlockPos)lvt_7_1_.get(lvt_11_1_);
                Block lvt_13_1_ = worldIn.getBlockState(lvt_12_1_).getBlock();
                lvt_13_1_.dropBlockAsItem(worldIn, lvt_12_1_, worldIn.getBlockState(lvt_12_1_), 0);
                worldIn.setBlockToAir(lvt_12_1_);
                --lvt_8_1_;
                lvt_9_1_[lvt_8_1_] = lvt_13_1_;
            }

            for (int lvt_11_2_ = lvt_6_1_.size() - 1; lvt_11_2_ >= 0; --lvt_11_2_)
            {
                BlockPos lvt_12_2_ = (BlockPos)lvt_6_1_.get(lvt_11_2_);
                IBlockState lvt_13_2_ = worldIn.getBlockState(lvt_12_2_);
                Block lvt_14_1_ = lvt_13_2_.getBlock();
                lvt_14_1_.getMetaFromState(lvt_13_2_);
                worldIn.setBlockToAir(lvt_12_2_);
                lvt_12_2_ = lvt_12_2_.offset(lvt_10_1_);
                worldIn.setBlockState(lvt_12_2_, Blocks.piston_extension.getDefaultState().withProperty(FACING, direction), 4);
                worldIn.setTileEntity(lvt_12_2_, BlockPistonMoving.newTileEntity(lvt_13_2_, direction, extending, false));
                --lvt_8_1_;
                lvt_9_1_[lvt_8_1_] = lvt_14_1_;
            }

            BlockPos lvt_11_3_ = pos.offset(direction);

            if (extending)
            {
                BlockPistonExtension.EnumPistonType lvt_12_3_ = this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT;
                IBlockState lvt_13_3_ = Blocks.piston_head.getDefaultState().withProperty(BlockPistonExtension.FACING, direction).withProperty(BlockPistonExtension.TYPE, lvt_12_3_);
                IBlockState lvt_14_2_ = Blocks.piston_extension.getDefaultState().withProperty(BlockPistonMoving.FACING, direction).withProperty(BlockPistonMoving.TYPE, this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);
                worldIn.setBlockState(lvt_11_3_, lvt_14_2_, 4);
                worldIn.setTileEntity(lvt_11_3_, BlockPistonMoving.newTileEntity(lvt_13_3_, direction, true, false));
            }

            for (int lvt_12_4_ = lvt_7_1_.size() - 1; lvt_12_4_ >= 0; --lvt_12_4_)
            {
                worldIn.notifyNeighborsOfStateChange((BlockPos)lvt_7_1_.get(lvt_12_4_), lvt_9_1_[lvt_8_1_++]);
            }

            for (int lvt_12_5_ = lvt_6_1_.size() - 1; lvt_12_5_ >= 0; --lvt_12_5_)
            {
                worldIn.notifyNeighborsOfStateChange((BlockPos)lvt_6_1_.get(lvt_12_5_), lvt_9_1_[lvt_8_1_++]);
            }

            if (extending)
            {
                worldIn.notifyNeighborsOfStateChange(lvt_11_3_, Blocks.piston_head);
                worldIn.notifyNeighborsOfStateChange(pos, this);
            }

            return true;
        }
    }

    /**
     * Possibly modify the given BlockState before rendering it on an Entity (Minecarts, Endermen, ...)
     */
    public IBlockState getStateForEntityRender(IBlockState state)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.UP);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, getFacing(meta)).withProperty(EXTENDED, Boolean.valueOf((meta & 8) > 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int lvt_2_1_ = 0;
        lvt_2_1_ = lvt_2_1_ | ((EnumFacing)state.getValue(FACING)).getIndex();

        if (((Boolean)state.getValue(EXTENDED)).booleanValue())
        {
            lvt_2_1_ |= 8;
        }

        return lvt_2_1_;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {FACING, EXTENDED});
    }
}
