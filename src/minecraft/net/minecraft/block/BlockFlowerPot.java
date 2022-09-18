package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFlowerPot extends BlockContainer
{
    public static final PropertyInteger LEGACY_DATA = PropertyInteger.create("legacy_data", 0, 15);
    public static final PropertyEnum<BlockFlowerPot.EnumFlowerType> CONTENTS = PropertyEnum.<BlockFlowerPot.EnumFlowerType>create("contents", BlockFlowerPot.EnumFlowerType.class);

    public BlockFlowerPot()
    {
        super(Material.circuits);
        this.setDefaultState(this.blockState.getBaseState().withProperty(CONTENTS, BlockFlowerPot.EnumFlowerType.EMPTY).withProperty(LEGACY_DATA, Integer.valueOf(0)));
        this.setBlockBoundsForItemRender();
    }

    /**
     * Gets the localized name of this block. Used for the statistics page.
     */
    public String getLocalizedName()
    {
        return StatCollector.translateToLocal("item.flowerPot.name");
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        float lvt_1_1_ = 0.375F;
        float lvt_2_1_ = lvt_1_1_ / 2.0F;
        this.setBlockBounds(0.5F - lvt_2_1_, 0.0F, 0.5F - lvt_2_1_, 0.5F + lvt_2_1_, lvt_1_1_, 0.5F + lvt_2_1_);
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
    public int getRenderType()
    {
        return 3;
    }

    public boolean isFullCube()
    {
        return false;
    }

    public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass)
    {
        TileEntity lvt_4_1_ = worldIn.getTileEntity(pos);

        if (lvt_4_1_ instanceof TileEntityFlowerPot)
        {
            Item lvt_5_1_ = ((TileEntityFlowerPot)lvt_4_1_).getFlowerPotItem();

            if (lvt_5_1_ instanceof ItemBlock)
            {
                return Block.getBlockFromItem(lvt_5_1_).colorMultiplier(worldIn, pos, renderPass);
            }
        }

        return 16777215;
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        ItemStack lvt_9_1_ = playerIn.inventory.getCurrentItem();

        if (lvt_9_1_ != null && lvt_9_1_.getItem() instanceof ItemBlock)
        {
            TileEntityFlowerPot lvt_10_1_ = this.getTileEntity(worldIn, pos);

            if (lvt_10_1_ == null)
            {
                return false;
            }
            else if (lvt_10_1_.getFlowerPotItem() != null)
            {
                return false;
            }
            else
            {
                Block lvt_11_1_ = Block.getBlockFromItem(lvt_9_1_.getItem());

                if (!this.canNotContain(lvt_11_1_, lvt_9_1_.getMetadata()))
                {
                    return false;
                }
                else
                {
                    lvt_10_1_.setFlowerPotData(lvt_9_1_.getItem(), lvt_9_1_.getMetadata());
                    lvt_10_1_.markDirty();
                    worldIn.markBlockForUpdate(pos);
                    playerIn.triggerAchievement(StatList.field_181736_T);

                    if (!playerIn.capabilities.isCreativeMode && --lvt_9_1_.stackSize <= 0)
                    {
                        playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, (ItemStack)null);
                    }

                    return true;
                }
            }
        }
        else
        {
            return false;
        }
    }

    private boolean canNotContain(Block blockIn, int meta)
    {
        return blockIn != Blocks.yellow_flower && blockIn != Blocks.red_flower && blockIn != Blocks.cactus && blockIn != Blocks.brown_mushroom && blockIn != Blocks.red_mushroom && blockIn != Blocks.sapling && blockIn != Blocks.deadbush ? blockIn == Blocks.tallgrass && meta == BlockTallGrass.EnumType.FERN.getMeta() : true;
    }

    public Item getItem(World worldIn, BlockPos pos)
    {
        TileEntityFlowerPot lvt_3_1_ = this.getTileEntity(worldIn, pos);
        return lvt_3_1_ != null && lvt_3_1_.getFlowerPotItem() != null ? lvt_3_1_.getFlowerPotItem() : Items.flower_pot;
    }

    /**
     * Gets the meta to use for the Pick Block ItemStack result
     */
    public int getDamageValue(World worldIn, BlockPos pos)
    {
        TileEntityFlowerPot lvt_3_1_ = this.getTileEntity(worldIn, pos);
        return lvt_3_1_ != null && lvt_3_1_.getFlowerPotItem() != null ? lvt_3_1_.getFlowerPotData() : 0;
    }

    /**
     * Returns true only if block is flowerPot
     */
    public boolean isFlowerPot()
    {
        return true;
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return super.canPlaceBlockAt(worldIn, pos) && World.doesBlockHaveSolidTopSurface(worldIn, pos.down());
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down()))
        {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntityFlowerPot lvt_4_1_ = this.getTileEntity(worldIn, pos);

        if (lvt_4_1_ != null && lvt_4_1_.getFlowerPotItem() != null)
        {
            spawnAsEntity(worldIn, pos, new ItemStack(lvt_4_1_.getFlowerPotItem(), 1, lvt_4_1_.getFlowerPotData()));
        }

        super.breakBlock(worldIn, pos, state);
    }

    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        super.onBlockHarvested(worldIn, pos, state, player);

        if (player.capabilities.isCreativeMode)
        {
            TileEntityFlowerPot lvt_5_1_ = this.getTileEntity(worldIn, pos);

            if (lvt_5_1_ != null)
            {
                lvt_5_1_.setFlowerPotData((Item)null, 0);
            }
        }
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.flower_pot;
    }

    private TileEntityFlowerPot getTileEntity(World worldIn, BlockPos pos)
    {
        TileEntity lvt_3_1_ = worldIn.getTileEntity(pos);
        return lvt_3_1_ instanceof TileEntityFlowerPot ? (TileEntityFlowerPot)lvt_3_1_ : null;
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        Block lvt_3_1_ = null;
        int lvt_4_1_ = 0;

        switch (meta)
        {
            case 1:
                lvt_3_1_ = Blocks.red_flower;
                lvt_4_1_ = BlockFlower.EnumFlowerType.POPPY.getMeta();
                break;

            case 2:
                lvt_3_1_ = Blocks.yellow_flower;
                break;

            case 3:
                lvt_3_1_ = Blocks.sapling;
                lvt_4_1_ = BlockPlanks.EnumType.OAK.getMetadata();
                break;

            case 4:
                lvt_3_1_ = Blocks.sapling;
                lvt_4_1_ = BlockPlanks.EnumType.SPRUCE.getMetadata();
                break;

            case 5:
                lvt_3_1_ = Blocks.sapling;
                lvt_4_1_ = BlockPlanks.EnumType.BIRCH.getMetadata();
                break;

            case 6:
                lvt_3_1_ = Blocks.sapling;
                lvt_4_1_ = BlockPlanks.EnumType.JUNGLE.getMetadata();
                break;

            case 7:
                lvt_3_1_ = Blocks.red_mushroom;
                break;

            case 8:
                lvt_3_1_ = Blocks.brown_mushroom;
                break;

            case 9:
                lvt_3_1_ = Blocks.cactus;
                break;

            case 10:
                lvt_3_1_ = Blocks.deadbush;
                break;

            case 11:
                lvt_3_1_ = Blocks.tallgrass;
                lvt_4_1_ = BlockTallGrass.EnumType.FERN.getMeta();
                break;

            case 12:
                lvt_3_1_ = Blocks.sapling;
                lvt_4_1_ = BlockPlanks.EnumType.ACACIA.getMetadata();
                break;

            case 13:
                lvt_3_1_ = Blocks.sapling;
                lvt_4_1_ = BlockPlanks.EnumType.DARK_OAK.getMetadata();
        }

        return new TileEntityFlowerPot(Item.getItemFromBlock(lvt_3_1_), lvt_4_1_);
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {CONTENTS, LEGACY_DATA});
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((Integer)state.getValue(LEGACY_DATA)).intValue();
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        BlockFlowerPot.EnumFlowerType lvt_4_1_ = BlockFlowerPot.EnumFlowerType.EMPTY;
        TileEntity lvt_5_1_ = worldIn.getTileEntity(pos);

        if (lvt_5_1_ instanceof TileEntityFlowerPot)
        {
            TileEntityFlowerPot lvt_6_1_ = (TileEntityFlowerPot)lvt_5_1_;
            Item lvt_7_1_ = lvt_6_1_.getFlowerPotItem();

            if (lvt_7_1_ instanceof ItemBlock)
            {
                int lvt_8_1_ = lvt_6_1_.getFlowerPotData();
                Block lvt_9_1_ = Block.getBlockFromItem(lvt_7_1_);

                if (lvt_9_1_ == Blocks.sapling)
                {
                    switch (BlockPlanks.EnumType.byMetadata(lvt_8_1_))
                    {
                        case OAK:
                            lvt_4_1_ = BlockFlowerPot.EnumFlowerType.OAK_SAPLING;
                            break;

                        case SPRUCE:
                            lvt_4_1_ = BlockFlowerPot.EnumFlowerType.SPRUCE_SAPLING;
                            break;

                        case BIRCH:
                            lvt_4_1_ = BlockFlowerPot.EnumFlowerType.BIRCH_SAPLING;
                            break;

                        case JUNGLE:
                            lvt_4_1_ = BlockFlowerPot.EnumFlowerType.JUNGLE_SAPLING;
                            break;

                        case ACACIA:
                            lvt_4_1_ = BlockFlowerPot.EnumFlowerType.ACACIA_SAPLING;
                            break;

                        case DARK_OAK:
                            lvt_4_1_ = BlockFlowerPot.EnumFlowerType.DARK_OAK_SAPLING;
                            break;

                        default:
                            lvt_4_1_ = BlockFlowerPot.EnumFlowerType.EMPTY;
                    }
                }
                else if (lvt_9_1_ == Blocks.tallgrass)
                {
                    switch (lvt_8_1_)
                    {
                        case 0:
                            lvt_4_1_ = BlockFlowerPot.EnumFlowerType.DEAD_BUSH;
                            break;

                        case 2:
                            lvt_4_1_ = BlockFlowerPot.EnumFlowerType.FERN;
                            break;

                        default:
                            lvt_4_1_ = BlockFlowerPot.EnumFlowerType.EMPTY;
                    }
                }
                else if (lvt_9_1_ == Blocks.yellow_flower)
                {
                    lvt_4_1_ = BlockFlowerPot.EnumFlowerType.DANDELION;
                }
                else if (lvt_9_1_ == Blocks.red_flower)
                {
                    switch (BlockFlower.EnumFlowerType.getType(BlockFlower.EnumFlowerColor.RED, lvt_8_1_))
                    {
                        case POPPY:
                            lvt_4_1_ = BlockFlowerPot.EnumFlowerType.POPPY;
                            break;

                        case BLUE_ORCHID:
                            lvt_4_1_ = BlockFlowerPot.EnumFlowerType.BLUE_ORCHID;
                            break;

                        case ALLIUM:
                            lvt_4_1_ = BlockFlowerPot.EnumFlowerType.ALLIUM;
                            break;

                        case HOUSTONIA:
                            lvt_4_1_ = BlockFlowerPot.EnumFlowerType.HOUSTONIA;
                            break;

                        case RED_TULIP:
                            lvt_4_1_ = BlockFlowerPot.EnumFlowerType.RED_TULIP;
                            break;

                        case ORANGE_TULIP:
                            lvt_4_1_ = BlockFlowerPot.EnumFlowerType.ORANGE_TULIP;
                            break;

                        case WHITE_TULIP:
                            lvt_4_1_ = BlockFlowerPot.EnumFlowerType.WHITE_TULIP;
                            break;

                        case PINK_TULIP:
                            lvt_4_1_ = BlockFlowerPot.EnumFlowerType.PINK_TULIP;
                            break;

                        case OXEYE_DAISY:
                            lvt_4_1_ = BlockFlowerPot.EnumFlowerType.OXEYE_DAISY;
                            break;

                        default:
                            lvt_4_1_ = BlockFlowerPot.EnumFlowerType.EMPTY;
                    }
                }
                else if (lvt_9_1_ == Blocks.red_mushroom)
                {
                    lvt_4_1_ = BlockFlowerPot.EnumFlowerType.MUSHROOM_RED;
                }
                else if (lvt_9_1_ == Blocks.brown_mushroom)
                {
                    lvt_4_1_ = BlockFlowerPot.EnumFlowerType.MUSHROOM_BROWN;
                }
                else if (lvt_9_1_ == Blocks.deadbush)
                {
                    lvt_4_1_ = BlockFlowerPot.EnumFlowerType.DEAD_BUSH;
                }
                else if (lvt_9_1_ == Blocks.cactus)
                {
                    lvt_4_1_ = BlockFlowerPot.EnumFlowerType.CACTUS;
                }
            }
        }

        return state.withProperty(CONTENTS, lvt_4_1_);
    }

    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.CUTOUT;
    }

    public static enum EnumFlowerType implements IStringSerializable
    {
        EMPTY("empty"),
        POPPY("rose"),
        BLUE_ORCHID("blue_orchid"),
        ALLIUM("allium"),
        HOUSTONIA("houstonia"),
        RED_TULIP("red_tulip"),
        ORANGE_TULIP("orange_tulip"),
        WHITE_TULIP("white_tulip"),
        PINK_TULIP("pink_tulip"),
        OXEYE_DAISY("oxeye_daisy"),
        DANDELION("dandelion"),
        OAK_SAPLING("oak_sapling"),
        SPRUCE_SAPLING("spruce_sapling"),
        BIRCH_SAPLING("birch_sapling"),
        JUNGLE_SAPLING("jungle_sapling"),
        ACACIA_SAPLING("acacia_sapling"),
        DARK_OAK_SAPLING("dark_oak_sapling"),
        MUSHROOM_RED("mushroom_red"),
        MUSHROOM_BROWN("mushroom_brown"),
        DEAD_BUSH("dead_bush"),
        FERN("fern"),
        CACTUS("cactus");

        private final String name;

        private EnumFlowerType(String name)
        {
            this.name = name;
        }

        public String toString()
        {
            return this.name;
        }

        public String getName()
        {
            return this.name;
        }
    }
}
