package net.minecraft.block;

import com.google.common.base.Predicate;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockStateHelper;
import net.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class BlockPumpkin extends BlockDirectional
{
    private BlockPattern snowmanBasePattern;
    private BlockPattern snowmanPattern;
    private BlockPattern golemBasePattern;
    private BlockPattern golemPattern;
    private static final Predicate<IBlockState> field_181085_Q = new Predicate<IBlockState>()
    {
        public boolean apply(IBlockState p_apply_1_)
        {
            return p_apply_1_ != null && (p_apply_1_.getBlock() == Blocks.pumpkin || p_apply_1_.getBlock() == Blocks.lit_pumpkin);
        }
        public boolean apply(Object p_apply_1_)
        {
            return this.apply((IBlockState)p_apply_1_);
        }
    };

    protected BlockPumpkin()
    {
        super(Material.gourd, MapColor.adobeColor);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        super.onBlockAdded(worldIn, pos, state);
        this.trySpawnGolem(worldIn, pos);
    }

    public boolean canDispenserPlace(World worldIn, BlockPos pos)
    {
        return this.getSnowmanBasePattern().match(worldIn, pos) != null || this.getGolemBasePattern().match(worldIn, pos) != null;
    }

    private void trySpawnGolem(World worldIn, BlockPos pos)
    {
        BlockPattern.PatternHelper lvt_3_1_;

        if ((lvt_3_1_ = this.getSnowmanPattern().match(worldIn, pos)) != null)
        {
            for (int lvt_4_1_ = 0; lvt_4_1_ < this.getSnowmanPattern().getThumbLength(); ++lvt_4_1_)
            {
                BlockWorldState lvt_5_1_ = lvt_3_1_.translateOffset(0, lvt_4_1_, 0);
                worldIn.setBlockState(lvt_5_1_.getPos(), Blocks.air.getDefaultState(), 2);
            }

            EntitySnowman lvt_4_2_ = new EntitySnowman(worldIn);
            BlockPos lvt_5_2_ = lvt_3_1_.translateOffset(0, 2, 0).getPos();
            lvt_4_2_.setLocationAndAngles((double)lvt_5_2_.getX() + 0.5D, (double)lvt_5_2_.getY() + 0.05D, (double)lvt_5_2_.getZ() + 0.5D, 0.0F, 0.0F);
            worldIn.spawnEntityInWorld(lvt_4_2_);

            for (int lvt_6_1_ = 0; lvt_6_1_ < 120; ++lvt_6_1_)
            {
                worldIn.spawnParticle(EnumParticleTypes.SNOW_SHOVEL, (double)lvt_5_2_.getX() + worldIn.rand.nextDouble(), (double)lvt_5_2_.getY() + worldIn.rand.nextDouble() * 2.5D, (double)lvt_5_2_.getZ() + worldIn.rand.nextDouble(), 0.0D, 0.0D, 0.0D, new int[0]);
            }

            for (int lvt_6_2_ = 0; lvt_6_2_ < this.getSnowmanPattern().getThumbLength(); ++lvt_6_2_)
            {
                BlockWorldState lvt_7_1_ = lvt_3_1_.translateOffset(0, lvt_6_2_, 0);
                worldIn.notifyNeighborsRespectDebug(lvt_7_1_.getPos(), Blocks.air);
            }
        }
        else if ((lvt_3_1_ = this.getGolemPattern().match(worldIn, pos)) != null)
        {
            for (int lvt_4_3_ = 0; lvt_4_3_ < this.getGolemPattern().getPalmLength(); ++lvt_4_3_)
            {
                for (int lvt_5_3_ = 0; lvt_5_3_ < this.getGolemPattern().getThumbLength(); ++lvt_5_3_)
                {
                    worldIn.setBlockState(lvt_3_1_.translateOffset(lvt_4_3_, lvt_5_3_, 0).getPos(), Blocks.air.getDefaultState(), 2);
                }
            }

            BlockPos lvt_4_4_ = lvt_3_1_.translateOffset(1, 2, 0).getPos();
            EntityIronGolem lvt_5_4_ = new EntityIronGolem(worldIn);
            lvt_5_4_.setPlayerCreated(true);
            lvt_5_4_.setLocationAndAngles((double)lvt_4_4_.getX() + 0.5D, (double)lvt_4_4_.getY() + 0.05D, (double)lvt_4_4_.getZ() + 0.5D, 0.0F, 0.0F);
            worldIn.spawnEntityInWorld(lvt_5_4_);

            for (int lvt_6_3_ = 0; lvt_6_3_ < 120; ++lvt_6_3_)
            {
                worldIn.spawnParticle(EnumParticleTypes.SNOWBALL, (double)lvt_4_4_.getX() + worldIn.rand.nextDouble(), (double)lvt_4_4_.getY() + worldIn.rand.nextDouble() * 3.9D, (double)lvt_4_4_.getZ() + worldIn.rand.nextDouble(), 0.0D, 0.0D, 0.0D, new int[0]);
            }

            for (int lvt_6_4_ = 0; lvt_6_4_ < this.getGolemPattern().getPalmLength(); ++lvt_6_4_)
            {
                for (int lvt_7_2_ = 0; lvt_7_2_ < this.getGolemPattern().getThumbLength(); ++lvt_7_2_)
                {
                    BlockWorldState lvt_8_1_ = lvt_3_1_.translateOffset(lvt_6_4_, lvt_7_2_, 0);
                    worldIn.notifyNeighborsRespectDebug(lvt_8_1_.getPos(), Blocks.air);
                }
            }
        }
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos).getBlock().blockMaterial.isReplaceable() && World.doesBlockHaveSolidTopSurface(worldIn, pos.down());
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
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {FACING});
    }

    protected BlockPattern getSnowmanBasePattern()
    {
        if (this.snowmanBasePattern == null)
        {
            this.snowmanBasePattern = FactoryBlockPattern.start().aisle(new String[] {" ", "#", "#"}).where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.snow))).build();
        }

        return this.snowmanBasePattern;
    }

    protected BlockPattern getSnowmanPattern()
    {
        if (this.snowmanPattern == null)
        {
            this.snowmanPattern = FactoryBlockPattern.start().aisle(new String[] {"^", "#", "#"}).where('^', BlockWorldState.hasState(field_181085_Q)).where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.snow))).build();
        }

        return this.snowmanPattern;
    }

    protected BlockPattern getGolemBasePattern()
    {
        if (this.golemBasePattern == null)
        {
            this.golemBasePattern = FactoryBlockPattern.start().aisle(new String[] {"~ ~", "###", "~#~"}).where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.iron_block))).where('~', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.air))).build();
        }

        return this.golemBasePattern;
    }

    protected BlockPattern getGolemPattern()
    {
        if (this.golemPattern == null)
        {
            this.golemPattern = FactoryBlockPattern.start().aisle(new String[] {"~^~", "###", "~#~"}).where('^', BlockWorldState.hasState(field_181085_Q)).where('#', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.iron_block))).where('~', BlockWorldState.hasState(BlockStateHelper.forBlock(Blocks.air))).build();
        }

        return this.golemPattern;
    }
}
