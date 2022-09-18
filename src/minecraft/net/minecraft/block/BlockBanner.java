package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBanner extends BlockContainer
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyInteger ROTATION = PropertyInteger.create("rotation", 0, 15);

    protected BlockBanner()
    {
        super(Material.wood);
        float lvt_1_1_ = 0.25F;
        float lvt_2_1_ = 1.0F;
        this.setBlockBounds(0.5F - lvt_1_1_, 0.0F, 0.5F - lvt_1_1_, 0.5F + lvt_1_1_, lvt_2_1_, 0.5F + lvt_1_1_);
    }

    /**
     * Gets the localized name of this block. Used for the statistics page.
     */
    public String getLocalizedName()
    {
        return StatCollector.translateToLocal("item.banner.white.name");
    }

    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
    {
        return null;
    }

    public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos)
    {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        return super.getSelectedBoundingBox(worldIn, pos);
    }

    public boolean isFullCube()
    {
        return false;
    }

    public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
    {
        return true;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * Return true if an entity can be spawned inside the block (used to get the player's bed spawn location)
     */
    public boolean canSpawnInBlock()
    {
        return true;
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityBanner();
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.banner;
    }

    public Item getItem(World worldIn, BlockPos pos)
    {
        return Items.banner;
    }

    /**
     * Spawns this Block's drops into the World as EntityItems.
     */
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        TileEntity lvt_6_1_ = worldIn.getTileEntity(pos);

        if (lvt_6_1_ instanceof TileEntityBanner)
        {
            ItemStack lvt_7_1_ = new ItemStack(Items.banner, 1, ((TileEntityBanner)lvt_6_1_).getBaseColor());
            NBTTagCompound lvt_8_1_ = new NBTTagCompound();
            lvt_6_1_.writeToNBT(lvt_8_1_);
            lvt_8_1_.removeTag("x");
            lvt_8_1_.removeTag("y");
            lvt_8_1_.removeTag("z");
            lvt_8_1_.removeTag("id");
            lvt_7_1_.setTagInfo("BlockEntityTag", lvt_8_1_);
            spawnAsEntity(worldIn, pos, lvt_7_1_);
        }
        else
        {
            super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
        }
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return !this.hasInvalidNeighbor(worldIn, pos) && super.canPlaceBlockAt(worldIn, pos);
    }

    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te)
    {
        if (te instanceof TileEntityBanner)
        {
            TileEntityBanner lvt_6_1_ = (TileEntityBanner)te;
            ItemStack lvt_7_1_ = new ItemStack(Items.banner, 1, ((TileEntityBanner)te).getBaseColor());
            NBTTagCompound lvt_8_1_ = new NBTTagCompound();
            TileEntityBanner.setBaseColorAndPatterns(lvt_8_1_, lvt_6_1_.getBaseColor(), lvt_6_1_.getPatterns());
            lvt_7_1_.setTagInfo("BlockEntityTag", lvt_8_1_);
            spawnAsEntity(worldIn, pos, lvt_7_1_);
        }
        else
        {
            super.harvestBlock(worldIn, player, pos, state, (TileEntity)null);
        }
    }

    public static class BlockBannerHanging extends BlockBanner
    {
        public BlockBannerHanging()
        {
            this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        }

        public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
        {
            EnumFacing lvt_3_1_ = (EnumFacing)worldIn.getBlockState(pos).getValue(FACING);
            float lvt_4_1_ = 0.0F;
            float lvt_5_1_ = 0.78125F;
            float lvt_6_1_ = 0.0F;
            float lvt_7_1_ = 1.0F;
            float lvt_8_1_ = 0.125F;
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

            switch (lvt_3_1_)
            {
                case NORTH:
                default:
                    this.setBlockBounds(lvt_6_1_, lvt_4_1_, 1.0F - lvt_8_1_, lvt_7_1_, lvt_5_1_, 1.0F);
                    break;

                case SOUTH:
                    this.setBlockBounds(lvt_6_1_, lvt_4_1_, 0.0F, lvt_7_1_, lvt_5_1_, lvt_8_1_);
                    break;

                case WEST:
                    this.setBlockBounds(1.0F - lvt_8_1_, lvt_4_1_, lvt_6_1_, 1.0F, lvt_5_1_, lvt_7_1_);
                    break;

                case EAST:
                    this.setBlockBounds(0.0F, lvt_4_1_, lvt_6_1_, lvt_8_1_, lvt_5_1_, lvt_7_1_);
            }
        }

        public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
        {
            EnumFacing lvt_5_1_ = (EnumFacing)state.getValue(FACING);

            if (!worldIn.getBlockState(pos.offset(lvt_5_1_.getOpposite())).getBlock().getMaterial().isSolid())
            {
                this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
            }

            super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
        }

        public IBlockState getStateFromMeta(int meta)
        {
            EnumFacing lvt_2_1_ = EnumFacing.getFront(meta);

            if (lvt_2_1_.getAxis() == EnumFacing.Axis.Y)
            {
                lvt_2_1_ = EnumFacing.NORTH;
            }

            return this.getDefaultState().withProperty(FACING, lvt_2_1_);
        }

        public int getMetaFromState(IBlockState state)
        {
            return ((EnumFacing)state.getValue(FACING)).getIndex();
        }

        protected BlockState createBlockState()
        {
            return new BlockState(this, new IProperty[] {FACING});
        }
    }

    public static class BlockBannerStanding extends BlockBanner
    {
        public BlockBannerStanding()
        {
            this.setDefaultState(this.blockState.getBaseState().withProperty(ROTATION, Integer.valueOf(0)));
        }

        public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
        {
            if (!worldIn.getBlockState(pos.down()).getBlock().getMaterial().isSolid())
            {
                this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
            }

            super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
        }

        public IBlockState getStateFromMeta(int meta)
        {
            return this.getDefaultState().withProperty(ROTATION, Integer.valueOf(meta));
        }

        public int getMetaFromState(IBlockState state)
        {
            return ((Integer)state.getValue(ROTATION)).intValue();
        }

        protected BlockState createBlockState()
        {
            return new BlockState(this, new IProperty[] {ROTATION});
        }
    }
}
