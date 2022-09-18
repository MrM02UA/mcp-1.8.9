package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStem extends BlockBush implements IGrowable
{
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 7);
    public static final PropertyDirection FACING = PropertyDirection.create("facing", new Predicate<EnumFacing>()
    {
        public boolean apply(EnumFacing p_apply_1_)
        {
            return p_apply_1_ != EnumFacing.DOWN;
        }
        public boolean apply(Object p_apply_1_)
        {
            return this.apply((EnumFacing)p_apply_1_);
        }
    });
    private final Block crop;

    protected BlockStem(Block crop)
    {
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)).withProperty(FACING, EnumFacing.UP));
        this.crop = crop;
        this.setTickRandomly(true);
        float lvt_2_1_ = 0.125F;
        this.setBlockBounds(0.5F - lvt_2_1_, 0.0F, 0.5F - lvt_2_1_, 0.5F + lvt_2_1_, 0.25F, 0.5F + lvt_2_1_);
        this.setCreativeTab((CreativeTabs)null);
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        state = state.withProperty(FACING, EnumFacing.UP);

        for (EnumFacing lvt_5_1_ : EnumFacing.Plane.HORIZONTAL)
        {
            if (worldIn.getBlockState(pos.offset(lvt_5_1_)).getBlock() == this.crop)
            {
                state = state.withProperty(FACING, lvt_5_1_);
                break;
            }
        }

        return state;
    }

    /**
     * is the block grass, dirt or farmland
     */
    protected boolean canPlaceBlockOn(Block ground)
    {
        return ground == Blocks.farmland;
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        super.updateTick(worldIn, pos, state, rand);

        if (worldIn.getLightFromNeighbors(pos.up()) >= 9)
        {
            float lvt_5_1_ = BlockCrops.getGrowthChance(this, worldIn, pos);

            if (rand.nextInt((int)(25.0F / lvt_5_1_) + 1) == 0)
            {
                int lvt_6_1_ = ((Integer)state.getValue(AGE)).intValue();

                if (lvt_6_1_ < 7)
                {
                    state = state.withProperty(AGE, Integer.valueOf(lvt_6_1_ + 1));
                    worldIn.setBlockState(pos, state, 2);
                }
                else
                {
                    for (EnumFacing lvt_8_1_ : EnumFacing.Plane.HORIZONTAL)
                    {
                        if (worldIn.getBlockState(pos.offset(lvt_8_1_)).getBlock() == this.crop)
                        {
                            return;
                        }
                    }

                    pos = pos.offset(EnumFacing.Plane.HORIZONTAL.random(rand));
                    Block lvt_7_2_ = worldIn.getBlockState(pos.down()).getBlock();

                    if (worldIn.getBlockState(pos).getBlock().blockMaterial == Material.air && (lvt_7_2_ == Blocks.farmland || lvt_7_2_ == Blocks.dirt || lvt_7_2_ == Blocks.grass))
                    {
                        worldIn.setBlockState(pos, this.crop.getDefaultState());
                    }
                }
            }
        }
    }

    public void growStem(World worldIn, BlockPos pos, IBlockState state)
    {
        int lvt_4_1_ = ((Integer)state.getValue(AGE)).intValue() + MathHelper.getRandomIntegerInRange(worldIn.rand, 2, 5);
        worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(Math.min(7, lvt_4_1_))), 2);
    }

    public int getRenderColor(IBlockState state)
    {
        if (state.getBlock() != this)
        {
            return super.getRenderColor(state);
        }
        else
        {
            int lvt_2_1_ = ((Integer)state.getValue(AGE)).intValue();
            int lvt_3_1_ = lvt_2_1_ * 32;
            int lvt_4_1_ = 255 - lvt_2_1_ * 8;
            int lvt_5_1_ = lvt_2_1_ * 4;
            return lvt_3_1_ << 16 | lvt_4_1_ << 8 | lvt_5_1_;
        }
    }

    public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass)
    {
        return this.getRenderColor(worldIn.getBlockState(pos));
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        float lvt_1_1_ = 0.125F;
        this.setBlockBounds(0.5F - lvt_1_1_, 0.0F, 0.5F - lvt_1_1_, 0.5F + lvt_1_1_, 0.25F, 0.5F + lvt_1_1_);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
        this.maxY = (double)((float)(((Integer)worldIn.getBlockState(pos).getValue(AGE)).intValue() * 2 + 2) / 16.0F);
        float lvt_3_1_ = 0.125F;
        this.setBlockBounds(0.5F - lvt_3_1_, 0.0F, 0.5F - lvt_3_1_, 0.5F + lvt_3_1_, (float)this.maxY, 0.5F + lvt_3_1_);
    }

    /**
     * Spawns this Block's drops into the World as EntityItems.
     */
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);

        if (!worldIn.isRemote)
        {
            Item lvt_6_1_ = this.getSeedItem();

            if (lvt_6_1_ != null)
            {
                int lvt_7_1_ = ((Integer)state.getValue(AGE)).intValue();

                for (int lvt_8_1_ = 0; lvt_8_1_ < 3; ++lvt_8_1_)
                {
                    if (worldIn.rand.nextInt(15) <= lvt_7_1_)
                    {
                        spawnAsEntity(worldIn, pos, new ItemStack(lvt_6_1_));
                    }
                }
            }
        }
    }

    protected Item getSeedItem()
    {
        return this.crop == Blocks.pumpkin ? Items.pumpkin_seeds : (this.crop == Blocks.melon_block ? Items.melon_seeds : null);
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return null;
    }

    public Item getItem(World worldIn, BlockPos pos)
    {
        Item lvt_3_1_ = this.getSeedItem();
        return lvt_3_1_ != null ? lvt_3_1_ : null;
    }

    /**
     * Whether this IGrowable can grow
     */
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        return ((Integer)state.getValue(AGE)).intValue() != 7;
    }

    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        return true;
    }

    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        this.growStem(worldIn, pos, state);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(AGE, Integer.valueOf(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((Integer)state.getValue(AGE)).intValue();
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {AGE, FACING});
    }
}
