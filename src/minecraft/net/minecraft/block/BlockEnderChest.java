package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class BlockEnderChest extends BlockContainer
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    protected BlockEnderChest()
    {
        super(Material.rock);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
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
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
    public int getRenderType()
    {
        return 2;
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(Blocks.obsidian);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 8;
    }

    protected boolean canSilkHarvest()
    {
        return true;
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
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        InventoryEnderChest lvt_9_1_ = playerIn.getInventoryEnderChest();
        TileEntity lvt_10_1_ = worldIn.getTileEntity(pos);

        if (lvt_9_1_ != null && lvt_10_1_ instanceof TileEntityEnderChest)
        {
            if (worldIn.getBlockState(pos.up()).getBlock().isNormalCube())
            {
                return true;
            }
            else if (worldIn.isRemote)
            {
                return true;
            }
            else
            {
                lvt_9_1_.setChestTileEntity((TileEntityEnderChest)lvt_10_1_);
                playerIn.displayGUIChest(lvt_9_1_);
                playerIn.triggerAchievement(StatList.field_181738_V);
                return true;
            }
        }
        else
        {
            return true;
        }
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityEnderChest();
    }

    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        for (int lvt_5_1_ = 0; lvt_5_1_ < 3; ++lvt_5_1_)
        {
            int lvt_6_1_ = rand.nextInt(2) * 2 - 1;
            int lvt_7_1_ = rand.nextInt(2) * 2 - 1;
            double lvt_8_1_ = (double)pos.getX() + 0.5D + 0.25D * (double)lvt_6_1_;
            double lvt_10_1_ = (double)((float)pos.getY() + rand.nextFloat());
            double lvt_12_1_ = (double)pos.getZ() + 0.5D + 0.25D * (double)lvt_7_1_;
            double lvt_14_1_ = (double)(rand.nextFloat() * (float)lvt_6_1_);
            double lvt_16_1_ = ((double)rand.nextFloat() - 0.5D) * 0.125D;
            double lvt_18_1_ = (double)(rand.nextFloat() * (float)lvt_7_1_);
            worldIn.spawnParticle(EnumParticleTypes.PORTAL, lvt_8_1_, lvt_10_1_, lvt_12_1_, lvt_14_1_, lvt_16_1_, lvt_18_1_, new int[0]);
        }
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
