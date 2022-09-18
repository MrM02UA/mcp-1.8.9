package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class BlockFurnace extends BlockContainer
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    private final boolean isBurning;
    private static boolean keepInventory;

    protected BlockFurnace(boolean isBurning)
    {
        super(Material.rock);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        this.isBurning = isBurning;
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(Blocks.furnace);
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        this.setDefaultFacing(worldIn, pos, state);
    }

    private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote)
        {
            Block lvt_4_1_ = worldIn.getBlockState(pos.north()).getBlock();
            Block lvt_5_1_ = worldIn.getBlockState(pos.south()).getBlock();
            Block lvt_6_1_ = worldIn.getBlockState(pos.west()).getBlock();
            Block lvt_7_1_ = worldIn.getBlockState(pos.east()).getBlock();
            EnumFacing lvt_8_1_ = (EnumFacing)state.getValue(FACING);

            if (lvt_8_1_ == EnumFacing.NORTH && lvt_4_1_.isFullBlock() && !lvt_5_1_.isFullBlock())
            {
                lvt_8_1_ = EnumFacing.SOUTH;
            }
            else if (lvt_8_1_ == EnumFacing.SOUTH && lvt_5_1_.isFullBlock() && !lvt_4_1_.isFullBlock())
            {
                lvt_8_1_ = EnumFacing.NORTH;
            }
            else if (lvt_8_1_ == EnumFacing.WEST && lvt_6_1_.isFullBlock() && !lvt_7_1_.isFullBlock())
            {
                lvt_8_1_ = EnumFacing.EAST;
            }
            else if (lvt_8_1_ == EnumFacing.EAST && lvt_7_1_.isFullBlock() && !lvt_6_1_.isFullBlock())
            {
                lvt_8_1_ = EnumFacing.WEST;
            }

            worldIn.setBlockState(pos, state.withProperty(FACING, lvt_8_1_), 2);
        }
    }

    @SuppressWarnings("incomplete-switch")
    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (this.isBurning)
        {
            EnumFacing lvt_5_1_ = (EnumFacing)state.getValue(FACING);
            double lvt_6_1_ = (double)pos.getX() + 0.5D;
            double lvt_8_1_ = (double)pos.getY() + rand.nextDouble() * 6.0D / 16.0D;
            double lvt_10_1_ = (double)pos.getZ() + 0.5D;
            double lvt_12_1_ = 0.52D;
            double lvt_14_1_ = rand.nextDouble() * 0.6D - 0.3D;

            switch (lvt_5_1_)
            {
                case WEST:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, lvt_6_1_ - lvt_12_1_, lvt_8_1_, lvt_10_1_ + lvt_14_1_, 0.0D, 0.0D, 0.0D, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, lvt_6_1_ - lvt_12_1_, lvt_8_1_, lvt_10_1_ + lvt_14_1_, 0.0D, 0.0D, 0.0D, new int[0]);
                    break;

                case EAST:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, lvt_6_1_ + lvt_12_1_, lvt_8_1_, lvt_10_1_ + lvt_14_1_, 0.0D, 0.0D, 0.0D, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, lvt_6_1_ + lvt_12_1_, lvt_8_1_, lvt_10_1_ + lvt_14_1_, 0.0D, 0.0D, 0.0D, new int[0]);
                    break;

                case NORTH:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, lvt_6_1_ + lvt_14_1_, lvt_8_1_, lvt_10_1_ - lvt_12_1_, 0.0D, 0.0D, 0.0D, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, lvt_6_1_ + lvt_14_1_, lvt_8_1_, lvt_10_1_ - lvt_12_1_, 0.0D, 0.0D, 0.0D, new int[0]);
                    break;

                case SOUTH:
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, lvt_6_1_ + lvt_14_1_, lvt_8_1_, lvt_10_1_ + lvt_12_1_, 0.0D, 0.0D, 0.0D, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, lvt_6_1_ + lvt_14_1_, lvt_8_1_, lvt_10_1_ + lvt_12_1_, 0.0D, 0.0D, 0.0D, new int[0]);
            }
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

            if (lvt_9_1_ instanceof TileEntityFurnace)
            {
                playerIn.displayGUIChest((TileEntityFurnace)lvt_9_1_);
                playerIn.triggerAchievement(StatList.field_181741_Y);
            }

            return true;
        }
    }

    public static void setState(boolean active, World worldIn, BlockPos pos)
    {
        IBlockState lvt_3_1_ = worldIn.getBlockState(pos);
        TileEntity lvt_4_1_ = worldIn.getTileEntity(pos);
        keepInventory = true;

        if (active)
        {
            worldIn.setBlockState(pos, Blocks.lit_furnace.getDefaultState().withProperty(FACING, lvt_3_1_.getValue(FACING)), 3);
            worldIn.setBlockState(pos, Blocks.lit_furnace.getDefaultState().withProperty(FACING, lvt_3_1_.getValue(FACING)), 3);
        }
        else
        {
            worldIn.setBlockState(pos, Blocks.furnace.getDefaultState().withProperty(FACING, lvt_3_1_.getValue(FACING)), 3);
            worldIn.setBlockState(pos, Blocks.furnace.getDefaultState().withProperty(FACING, lvt_3_1_.getValue(FACING)), 3);
        }

        keepInventory = false;

        if (lvt_4_1_ != null)
        {
            lvt_4_1_.validate();
            worldIn.setTileEntity(pos, lvt_4_1_);
        }
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityFurnace();
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);

        if (stack.hasDisplayName())
        {
            TileEntity lvt_6_1_ = worldIn.getTileEntity(pos);

            if (lvt_6_1_ instanceof TileEntityFurnace)
            {
                ((TileEntityFurnace)lvt_6_1_).setCustomInventoryName(stack.getDisplayName());
            }
        }
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!keepInventory)
        {
            TileEntity lvt_4_1_ = worldIn.getTileEntity(pos);

            if (lvt_4_1_ instanceof TileEntityFurnace)
            {
                InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityFurnace)lvt_4_1_);
                worldIn.updateComparatorOutputLevel(pos, this);
            }
        }

        super.breakBlock(worldIn, pos, state);
    }

    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    public int getComparatorInputOverride(World worldIn, BlockPos pos)
    {
        return Container.calcRedstone(worldIn.getTileEntity(pos));
    }

    public Item getItem(World worldIn, BlockPos pos)
    {
        return Item.getItemFromBlock(Blocks.furnace);
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
