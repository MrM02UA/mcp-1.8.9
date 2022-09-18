package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockVine extends Block
{
    public static final PropertyBool UP = PropertyBool.create("up");
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    public static final PropertyBool[] ALL_FACES = new PropertyBool[] {UP, NORTH, SOUTH, WEST, EAST};

    public BlockVine()
    {
        super(Material.vine);
        this.setDefaultState(this.blockState.getBaseState().withProperty(UP, Boolean.valueOf(false)).withProperty(NORTH, Boolean.valueOf(false)).withProperty(EAST, Boolean.valueOf(false)).withProperty(SOUTH, Boolean.valueOf(false)).withProperty(WEST, Boolean.valueOf(false)));
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return state.withProperty(UP, Boolean.valueOf(worldIn.getBlockState(pos.up()).getBlock().isBlockNormalCube()));
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
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
     * Whether this Block can be replaced directly by other blocks (true for e.g. tall grass)
     */
    public boolean isReplaceable(World worldIn, BlockPos pos)
    {
        return true;
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
        float lvt_3_1_ = 0.0625F;
        float lvt_4_1_ = 1.0F;
        float lvt_5_1_ = 1.0F;
        float lvt_6_1_ = 1.0F;
        float lvt_7_1_ = 0.0F;
        float lvt_8_1_ = 0.0F;
        float lvt_9_1_ = 0.0F;
        boolean lvt_10_1_ = false;

        if (((Boolean)worldIn.getBlockState(pos).getValue(WEST)).booleanValue())
        {
            lvt_7_1_ = Math.max(lvt_7_1_, 0.0625F);
            lvt_4_1_ = 0.0F;
            lvt_5_1_ = 0.0F;
            lvt_8_1_ = 1.0F;
            lvt_6_1_ = 0.0F;
            lvt_9_1_ = 1.0F;
            lvt_10_1_ = true;
        }

        if (((Boolean)worldIn.getBlockState(pos).getValue(EAST)).booleanValue())
        {
            lvt_4_1_ = Math.min(lvt_4_1_, 0.9375F);
            lvt_7_1_ = 1.0F;
            lvt_5_1_ = 0.0F;
            lvt_8_1_ = 1.0F;
            lvt_6_1_ = 0.0F;
            lvt_9_1_ = 1.0F;
            lvt_10_1_ = true;
        }

        if (((Boolean)worldIn.getBlockState(pos).getValue(NORTH)).booleanValue())
        {
            lvt_9_1_ = Math.max(lvt_9_1_, 0.0625F);
            lvt_6_1_ = 0.0F;
            lvt_4_1_ = 0.0F;
            lvt_7_1_ = 1.0F;
            lvt_5_1_ = 0.0F;
            lvt_8_1_ = 1.0F;
            lvt_10_1_ = true;
        }

        if (((Boolean)worldIn.getBlockState(pos).getValue(SOUTH)).booleanValue())
        {
            lvt_6_1_ = Math.min(lvt_6_1_, 0.9375F);
            lvt_9_1_ = 1.0F;
            lvt_4_1_ = 0.0F;
            lvt_7_1_ = 1.0F;
            lvt_5_1_ = 0.0F;
            lvt_8_1_ = 1.0F;
            lvt_10_1_ = true;
        }

        if (!lvt_10_1_ && this.canPlaceOn(worldIn.getBlockState(pos.up()).getBlock()))
        {
            lvt_5_1_ = Math.min(lvt_5_1_, 0.9375F);
            lvt_8_1_ = 1.0F;
            lvt_4_1_ = 0.0F;
            lvt_7_1_ = 1.0F;
            lvt_6_1_ = 0.0F;
            lvt_9_1_ = 1.0F;
        }

        this.setBlockBounds(lvt_4_1_, lvt_5_1_, lvt_6_1_, lvt_7_1_, lvt_8_1_, lvt_9_1_);
    }

    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
    {
        return null;
    }

    /**
     * Check whether this Block can be placed on the given side
     */
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side)
    {
        switch (side)
        {
            case UP:
                return this.canPlaceOn(worldIn.getBlockState(pos.up()).getBlock());

            case NORTH:
            case SOUTH:
            case EAST:
            case WEST:
                return this.canPlaceOn(worldIn.getBlockState(pos.offset(side.getOpposite())).getBlock());

            default:
                return false;
        }
    }

    private boolean canPlaceOn(Block blockIn)
    {
        return blockIn.isFullCube() && blockIn.blockMaterial.blocksMovement();
    }

    private boolean recheckGrownSides(World worldIn, BlockPos pos, IBlockState state)
    {
        IBlockState lvt_4_1_ = state;

        for (EnumFacing lvt_6_1_ : EnumFacing.Plane.HORIZONTAL)
        {
            PropertyBool lvt_7_1_ = getPropertyFor(lvt_6_1_);

            if (((Boolean)state.getValue(lvt_7_1_)).booleanValue() && !this.canPlaceOn(worldIn.getBlockState(pos.offset(lvt_6_1_)).getBlock()))
            {
                IBlockState lvt_8_1_ = worldIn.getBlockState(pos.up());

                if (lvt_8_1_.getBlock() != this || !((Boolean)lvt_8_1_.getValue(lvt_7_1_)).booleanValue())
                {
                    state = state.withProperty(lvt_7_1_, Boolean.valueOf(false));
                }
            }
        }

        if (getNumGrownFaces(state) == 0)
        {
            return false;
        }
        else
        {
            if (lvt_4_1_ != state)
            {
                worldIn.setBlockState(pos, state, 2);
            }

            return true;
        }
    }

    public int getBlockColor()
    {
        return ColorizerFoliage.getFoliageColorBasic();
    }

    public int getRenderColor(IBlockState state)
    {
        return ColorizerFoliage.getFoliageColorBasic();
    }

    public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass)
    {
        return worldIn.getBiomeGenForCoords(pos).getFoliageColorAtPos(pos);
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        if (!worldIn.isRemote && !this.recheckGrownSides(worldIn, pos, state))
        {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isRemote)
        {
            if (worldIn.rand.nextInt(4) == 0)
            {
                int lvt_5_1_ = 4;
                int lvt_6_1_ = 5;
                boolean lvt_7_1_ = false;
                label62:

                for (int lvt_8_1_ = -lvt_5_1_; lvt_8_1_ <= lvt_5_1_; ++lvt_8_1_)
                {
                    for (int lvt_9_1_ = -lvt_5_1_; lvt_9_1_ <= lvt_5_1_; ++lvt_9_1_)
                    {
                        for (int lvt_10_1_ = -1; lvt_10_1_ <= 1; ++lvt_10_1_)
                        {
                            if (worldIn.getBlockState(pos.add(lvt_8_1_, lvt_10_1_, lvt_9_1_)).getBlock() == this)
                            {
                                --lvt_6_1_;

                                if (lvt_6_1_ <= 0)
                                {
                                    lvt_7_1_ = true;
                                    break label62;
                                }
                            }
                        }
                    }
                }

                EnumFacing lvt_8_2_ = EnumFacing.random(rand);
                BlockPos lvt_9_2_ = pos.up();

                if (lvt_8_2_ == EnumFacing.UP && pos.getY() < 255 && worldIn.isAirBlock(lvt_9_2_))
                {
                    if (!lvt_7_1_)
                    {
                        IBlockState lvt_10_2_ = state;

                        for (EnumFacing lvt_12_1_ : EnumFacing.Plane.HORIZONTAL)
                        {
                            if (rand.nextBoolean() || !this.canPlaceOn(worldIn.getBlockState(lvt_9_2_.offset(lvt_12_1_)).getBlock()))
                            {
                                lvt_10_2_ = lvt_10_2_.withProperty(getPropertyFor(lvt_12_1_), Boolean.valueOf(false));
                            }
                        }

                        if (((Boolean)lvt_10_2_.getValue(NORTH)).booleanValue() || ((Boolean)lvt_10_2_.getValue(EAST)).booleanValue() || ((Boolean)lvt_10_2_.getValue(SOUTH)).booleanValue() || ((Boolean)lvt_10_2_.getValue(WEST)).booleanValue())
                        {
                            worldIn.setBlockState(lvt_9_2_, lvt_10_2_, 2);
                        }
                    }
                }
                else if (lvt_8_2_.getAxis().isHorizontal() && !((Boolean)state.getValue(getPropertyFor(lvt_8_2_))).booleanValue())
                {
                    if (!lvt_7_1_)
                    {
                        BlockPos lvt_10_3_ = pos.offset(lvt_8_2_);
                        Block lvt_11_2_ = worldIn.getBlockState(lvt_10_3_).getBlock();

                        if (lvt_11_2_.blockMaterial == Material.air)
                        {
                            EnumFacing lvt_12_2_ = lvt_8_2_.rotateY();
                            EnumFacing lvt_13_1_ = lvt_8_2_.rotateYCCW();
                            boolean lvt_14_1_ = ((Boolean)state.getValue(getPropertyFor(lvt_12_2_))).booleanValue();
                            boolean lvt_15_1_ = ((Boolean)state.getValue(getPropertyFor(lvt_13_1_))).booleanValue();
                            BlockPos lvt_16_1_ = lvt_10_3_.offset(lvt_12_2_);
                            BlockPos lvt_17_1_ = lvt_10_3_.offset(lvt_13_1_);

                            if (lvt_14_1_ && this.canPlaceOn(worldIn.getBlockState(lvt_16_1_).getBlock()))
                            {
                                worldIn.setBlockState(lvt_10_3_, this.getDefaultState().withProperty(getPropertyFor(lvt_12_2_), Boolean.valueOf(true)), 2);
                            }
                            else if (lvt_15_1_ && this.canPlaceOn(worldIn.getBlockState(lvt_17_1_).getBlock()))
                            {
                                worldIn.setBlockState(lvt_10_3_, this.getDefaultState().withProperty(getPropertyFor(lvt_13_1_), Boolean.valueOf(true)), 2);
                            }
                            else if (lvt_14_1_ && worldIn.isAirBlock(lvt_16_1_) && this.canPlaceOn(worldIn.getBlockState(pos.offset(lvt_12_2_)).getBlock()))
                            {
                                worldIn.setBlockState(lvt_16_1_, this.getDefaultState().withProperty(getPropertyFor(lvt_8_2_.getOpposite()), Boolean.valueOf(true)), 2);
                            }
                            else if (lvt_15_1_ && worldIn.isAirBlock(lvt_17_1_) && this.canPlaceOn(worldIn.getBlockState(pos.offset(lvt_13_1_)).getBlock()))
                            {
                                worldIn.setBlockState(lvt_17_1_, this.getDefaultState().withProperty(getPropertyFor(lvt_8_2_.getOpposite()), Boolean.valueOf(true)), 2);
                            }
                            else if (this.canPlaceOn(worldIn.getBlockState(lvt_10_3_.up()).getBlock()))
                            {
                                worldIn.setBlockState(lvt_10_3_, this.getDefaultState(), 2);
                            }
                        }
                        else if (lvt_11_2_.blockMaterial.isOpaque() && lvt_11_2_.isFullCube())
                        {
                            worldIn.setBlockState(pos, state.withProperty(getPropertyFor(lvt_8_2_), Boolean.valueOf(true)), 2);
                        }
                    }
                }
                else
                {
                    if (pos.getY() > 1)
                    {
                        BlockPos lvt_10_4_ = pos.down();
                        IBlockState lvt_11_3_ = worldIn.getBlockState(lvt_10_4_);
                        Block lvt_12_3_ = lvt_11_3_.getBlock();

                        if (lvt_12_3_.blockMaterial == Material.air)
                        {
                            IBlockState lvt_13_2_ = state;

                            for (EnumFacing lvt_15_2_ : EnumFacing.Plane.HORIZONTAL)
                            {
                                if (rand.nextBoolean())
                                {
                                    lvt_13_2_ = lvt_13_2_.withProperty(getPropertyFor(lvt_15_2_), Boolean.valueOf(false));
                                }
                            }

                            if (((Boolean)lvt_13_2_.getValue(NORTH)).booleanValue() || ((Boolean)lvt_13_2_.getValue(EAST)).booleanValue() || ((Boolean)lvt_13_2_.getValue(SOUTH)).booleanValue() || ((Boolean)lvt_13_2_.getValue(WEST)).booleanValue())
                            {
                                worldIn.setBlockState(lvt_10_4_, lvt_13_2_, 2);
                            }
                        }
                        else if (lvt_12_3_ == this)
                        {
                            IBlockState lvt_13_3_ = lvt_11_3_;

                            for (EnumFacing lvt_15_3_ : EnumFacing.Plane.HORIZONTAL)
                            {
                                PropertyBool lvt_16_2_ = getPropertyFor(lvt_15_3_);

                                if (rand.nextBoolean() && ((Boolean)state.getValue(lvt_16_2_)).booleanValue())
                                {
                                    lvt_13_3_ = lvt_13_3_.withProperty(lvt_16_2_, Boolean.valueOf(true));
                                }
                            }

                            if (((Boolean)lvt_13_3_.getValue(NORTH)).booleanValue() || ((Boolean)lvt_13_3_.getValue(EAST)).booleanValue() || ((Boolean)lvt_13_3_.getValue(SOUTH)).booleanValue() || ((Boolean)lvt_13_3_.getValue(WEST)).booleanValue())
                            {
                                worldIn.setBlockState(lvt_10_4_, lvt_13_3_, 2);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        IBlockState lvt_9_1_ = this.getDefaultState().withProperty(UP, Boolean.valueOf(false)).withProperty(NORTH, Boolean.valueOf(false)).withProperty(EAST, Boolean.valueOf(false)).withProperty(SOUTH, Boolean.valueOf(false)).withProperty(WEST, Boolean.valueOf(false));
        return facing.getAxis().isHorizontal() ? lvt_9_1_.withProperty(getPropertyFor(facing.getOpposite()), Boolean.valueOf(true)) : lvt_9_1_;
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return null;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 0;
    }

    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te)
    {
        if (!worldIn.isRemote && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.shears)
        {
            player.triggerAchievement(StatList.mineBlockStatArray[Block.getIdFromBlock(this)]);
            spawnAsEntity(worldIn, pos, new ItemStack(Blocks.vine, 1, 0));
        }
        else
        {
            super.harvestBlock(worldIn, player, pos, state, te);
        }
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
        return this.getDefaultState().withProperty(SOUTH, Boolean.valueOf((meta & 1) > 0)).withProperty(WEST, Boolean.valueOf((meta & 2) > 0)).withProperty(NORTH, Boolean.valueOf((meta & 4) > 0)).withProperty(EAST, Boolean.valueOf((meta & 8) > 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int lvt_2_1_ = 0;

        if (((Boolean)state.getValue(SOUTH)).booleanValue())
        {
            lvt_2_1_ |= 1;
        }

        if (((Boolean)state.getValue(WEST)).booleanValue())
        {
            lvt_2_1_ |= 2;
        }

        if (((Boolean)state.getValue(NORTH)).booleanValue())
        {
            lvt_2_1_ |= 4;
        }

        if (((Boolean)state.getValue(EAST)).booleanValue())
        {
            lvt_2_1_ |= 8;
        }

        return lvt_2_1_;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {UP, NORTH, EAST, SOUTH, WEST});
    }

    public static PropertyBool getPropertyFor(EnumFacing side)
    {
        switch (side)
        {
            case UP:
                return UP;

            case NORTH:
                return NORTH;

            case SOUTH:
                return SOUTH;

            case EAST:
                return EAST;

            case WEST:
                return WEST;

            default:
                throw new IllegalArgumentException(side + " is an invalid choice");
        }
    }

    public static int getNumGrownFaces(IBlockState state)
    {
        int lvt_1_1_ = 0;

        for (PropertyBool lvt_5_1_ : ALL_FACES)
        {
            if (((Boolean)state.getValue(lvt_5_1_)).booleanValue())
            {
                ++lvt_1_1_;
            }
        }

        return lvt_1_1_;
    }
}
