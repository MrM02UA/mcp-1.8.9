package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockStateHelper;
import net.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.StatCollector;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSkull extends BlockContainer
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyBool NODROP = PropertyBool.create("nodrop");
    private static final Predicate<BlockWorldState> IS_WITHER_SKELETON = new Predicate<BlockWorldState>()
    {
        public boolean apply(BlockWorldState p_apply_1_)
        {
            return p_apply_1_.getBlockState() != null && p_apply_1_.getBlockState().getBlock() == Blocks.skull && p_apply_1_.getTileEntity() instanceof TileEntitySkull && ((TileEntitySkull)p_apply_1_.getTileEntity()).getSkullType() == 1;
        }
        public boolean apply(Object p_apply_1_)
        {
            return this.apply((BlockWorldState)p_apply_1_);
        }
    };
    private BlockPattern witherBasePattern;
    private BlockPattern witherPattern;

    protected BlockSkull()
    {
        super(Material.circuits);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(NODROP, Boolean.valueOf(false)));
        this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
    }

    /**
     * Gets the localized name of this block. Used for the statistics page.
     */
    public String getLocalizedName()
    {
        return StatCollector.translateToLocal("tile.skull.skeleton.name");
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

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
        switch ((EnumFacing)worldIn.getBlockState(pos).getValue(FACING))
        {
            case UP:
            default:
                this.setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
                break;

            case NORTH:
                this.setBlockBounds(0.25F, 0.25F, 0.5F, 0.75F, 0.75F, 1.0F);
                break;

            case SOUTH:
                this.setBlockBounds(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.5F);
                break;

            case WEST:
                this.setBlockBounds(0.5F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
                break;

            case EAST:
                this.setBlockBounds(0.0F, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F);
        }
    }

    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
    {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        return super.getCollisionBoundingBox(worldIn, pos, state);
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(NODROP, Boolean.valueOf(false));
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntitySkull();
    }

    public Item getItem(World worldIn, BlockPos pos)
    {
        return Items.skull;
    }

    /**
     * Gets the meta to use for the Pick Block ItemStack result
     */
    public int getDamageValue(World worldIn, BlockPos pos)
    {
        TileEntity lvt_3_1_ = worldIn.getTileEntity(pos);
        return lvt_3_1_ instanceof TileEntitySkull ? ((TileEntitySkull)lvt_3_1_).getSkullType() : super.getDamageValue(worldIn, pos);
    }

    /**
     * Spawns this Block's drops into the World as EntityItems.
     */
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
    }

    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        if (player.capabilities.isCreativeMode)
        {
            state = state.withProperty(NODROP, Boolean.valueOf(true));
            worldIn.setBlockState(pos, state, 4);
        }

        super.onBlockHarvested(worldIn, pos, state, player);
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote)
        {
            if (!((Boolean)state.getValue(NODROP)).booleanValue())
            {
                TileEntity lvt_4_1_ = worldIn.getTileEntity(pos);

                if (lvt_4_1_ instanceof TileEntitySkull)
                {
                    TileEntitySkull lvt_5_1_ = (TileEntitySkull)lvt_4_1_;
                    ItemStack lvt_6_1_ = new ItemStack(Items.skull, 1, this.getDamageValue(worldIn, pos));

                    if (lvt_5_1_.getSkullType() == 3 && lvt_5_1_.getPlayerProfile() != null)
                    {
                        lvt_6_1_.setTagCompound(new NBTTagCompound());
                        NBTTagCompound lvt_7_1_ = new NBTTagCompound();
                        NBTUtil.writeGameProfile(lvt_7_1_, lvt_5_1_.getPlayerProfile());
                        lvt_6_1_.getTagCompound().setTag("SkullOwner", lvt_7_1_);
                    }

                    spawnAsEntity(worldIn, pos, lvt_6_1_);
                }
            }

            super.breakBlock(worldIn, pos, state);
        }
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.skull;
    }

    public boolean canDispenserPlace(World worldIn, BlockPos pos, ItemStack stack)
    {
        return stack.getMetadata() == 1 && pos.getY() >= 2 && worldIn.getDifficulty() != EnumDifficulty.PEACEFUL && !worldIn.isRemote ? this.getWitherBasePattern().match(worldIn, pos) != null : false;
    }

    public void checkWitherSpawn(World worldIn, BlockPos pos, TileEntitySkull te)
    {
        if (te.getSkullType() == 1 && pos.getY() >= 2 && worldIn.getDifficulty() != EnumDifficulty.PEACEFUL && !worldIn.isRemote)
        {
            BlockPattern lvt_4_1_ = this.getWitherPattern();
            BlockPattern.PatternHelper lvt_5_1_ = lvt_4_1_.match(worldIn, pos);

            if (lvt_5_1_ != null)
            {
                for (int lvt_6_1_ = 0; lvt_6_1_ < 3; ++lvt_6_1_)
                {
                    BlockWorldState lvt_7_1_ = lvt_5_1_.translateOffset(lvt_6_1_, 0, 0);
                    worldIn.setBlockState(lvt_7_1_.getPos(), lvt_7_1_.getBlockState().withProperty(NODROP, Boolean.valueOf(true)), 2);
                }

                for (int lvt_6_2_ = 0; lvt_6_2_ < lvt_4_1_.getPalmLength(); ++lvt_6_2_)
                {
                    for (int lvt_7_2_ = 0; lvt_7_2_ < lvt_4_1_.getThumbLength(); ++lvt_7_2_)
                    {
                        BlockWorldState lvt_8_1_ = lvt_5_1_.translateOffset(lvt_6_2_, lvt_7_2_, 0);
                        worldIn.setBlockState(lvt_8_1_.getPos(), Blocks.air.getDefaultState(), 2);
                    }
                }

                BlockPos lvt_6_3_ = lvt_5_1_.translateOffset(1, 0, 0).getPos();
                EntityWither lvt_7_3_ = new EntityWither(worldIn);
                BlockPos lvt_8_2_ = lvt_5_1_.translateOffset(1, 2, 0).getPos();
                lvt_7_3_.setLocationAndAngles((double)lvt_8_2_.getX() + 0.5D, (double)lvt_8_2_.getY() + 0.55D, (double)lvt_8_2_.getZ() + 0.5D, lvt_5_1_.getFinger().getAxis() == EnumFacing.Axis.X ? 0.0F : 90.0F, 0.0F);
                lvt_7_3_.renderYawOffset = lvt_5_1_.getFinger().getAxis() == EnumFacing.Axis.X ? 0.0F : 90.0F;
                lvt_7_3_.func_82206_m();

                for (EntityPlayer lvt_10_1_ : worldIn.getEntitiesWithinAABB(EntityPlayer.class, lvt_7_3_.getEntityBoundingBox().expand(50.0D, 50.0D, 50.0D)))
                {
                    lvt_10_1_.triggerAchievement(AchievementList.spawnWither);
                }

                worldIn.spawnEntityInWorld(lvt_7_3_);

                for (int lvt_9_2_ = 0; lvt_9_2_ < 120; ++lvt_9_2_)
                {
                    worldIn.spawnParticle(EnumParticleTypes.SNOWBALL, (double)lvt_6_3_.getX() + worldIn.rand.nextDouble(), (double)(lvt_6_3_.getY() - 2) + worldIn.rand.nextDouble() * 3.9D, (double)lvt_6_3_.getZ() + worldIn.rand.nextDouble(), 0.0D, 0.0D, 0.0D, new int[0]);
                }

                for (int lvt_9_3_ = 0; lvt_9_3_ < lvt_4_1_.getPalmLength(); ++lvt_9_3_)
                {
                    for (int lvt_10_2_ = 0; lvt_10_2_ < lvt_4_1_.getThumbLength(); ++lvt_10_2_)
                    {
                        BlockWorldState lvt_11_1_ = lvt_5_1_.translateOffset(lvt_9_3_, lvt_10_2_, 0);
                        worldIn.notifyNeighborsRespectDebug(lvt_11_1_.getPos(), Blocks.air);
                    }
                }
            }
        }
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta & 7)).withProperty(NODROP, Boolean.valueOf((meta & 8) > 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int lvt_2_1_ = 0;
        lvt_2_1_ = lvt_2_1_ | ((EnumFacing)state.getValue(FACING)).getIndex();

        if (((Boolean)state.getValue(NODROP)).booleanValue())
        {
            lvt_2_1_ |= 8;
        }

        return lvt_2_1_;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {FACING, NODROP});
    }

    protected BlockPattern getWitherBasePattern()
    {
        if (this.witherBasePattern == null)
        {
            this.witherBasePattern = FactoryBlockPattern.start().aisle(new String[] {"   ", "###", "~#~"}).where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.soul_sand))).where('~', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.air))).build();
        }

        return this.witherBasePattern;
    }

    protected BlockPattern getWitherPattern()
    {
        if (this.witherPattern == null)
        {
            this.witherPattern = FactoryBlockPattern.start().aisle(new String[] {"^^^", "###", "~#~"}).where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.soul_sand))).where('^', IS_WITHER_SKELETON).where('~', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.air))).build();
        }

        return this.witherPattern;
    }
}
