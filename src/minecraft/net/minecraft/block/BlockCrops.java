package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockCrops extends BlockBush implements IGrowable
{
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 7);

    protected BlockCrops()
    {
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)));
        this.setTickRandomly(true);
        float lvt_1_1_ = 0.5F;
        this.setBlockBounds(0.5F - lvt_1_1_, 0.0F, 0.5F - lvt_1_1_, 0.5F + lvt_1_1_, 0.25F, 0.5F + lvt_1_1_);
        this.setCreativeTab((CreativeTabs)null);
        this.setHardness(0.0F);
        this.setStepSound(soundTypeGrass);
        this.disableStats();
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
            int lvt_5_1_ = ((Integer)state.getValue(AGE)).intValue();

            if (lvt_5_1_ < 7)
            {
                float lvt_6_1_ = getGrowthChance(this, worldIn, pos);

                if (rand.nextInt((int)(25.0F / lvt_6_1_) + 1) == 0)
                {
                    worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(lvt_5_1_ + 1)), 2);
                }
            }
        }
    }

    public void grow(World worldIn, BlockPos pos, IBlockState state)
    {
        int lvt_4_1_ = ((Integer)state.getValue(AGE)).intValue() + MathHelper.getRandomIntegerInRange(worldIn.rand, 2, 5);

        if (lvt_4_1_ > 7)
        {
            lvt_4_1_ = 7;
        }

        worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(lvt_4_1_)), 2);
    }

    protected static float getGrowthChance(Block blockIn, World worldIn, BlockPos pos)
    {
        float lvt_3_1_ = 1.0F;
        BlockPos lvt_4_1_ = pos.down();

        for (int lvt_5_1_ = -1; lvt_5_1_ <= 1; ++lvt_5_1_)
        {
            for (int lvt_6_1_ = -1; lvt_6_1_ <= 1; ++lvt_6_1_)
            {
                float lvt_7_1_ = 0.0F;
                IBlockState lvt_8_1_ = worldIn.getBlockState(lvt_4_1_.add(lvt_5_1_, 0, lvt_6_1_));

                if (lvt_8_1_.getBlock() == Blocks.farmland)
                {
                    lvt_7_1_ = 1.0F;

                    if (((Integer)lvt_8_1_.getValue(BlockFarmland.MOISTURE)).intValue() > 0)
                    {
                        lvt_7_1_ = 3.0F;
                    }
                }

                if (lvt_5_1_ != 0 || lvt_6_1_ != 0)
                {
                    lvt_7_1_ /= 4.0F;
                }

                lvt_3_1_ += lvt_7_1_;
            }
        }

        BlockPos lvt_5_2_ = pos.north();
        BlockPos lvt_6_2_ = pos.south();
        BlockPos lvt_7_2_ = pos.west();
        BlockPos lvt_8_2_ = pos.east();
        boolean lvt_9_1_ = blockIn == worldIn.getBlockState(lvt_7_2_).getBlock() || blockIn == worldIn.getBlockState(lvt_8_2_).getBlock();
        boolean lvt_10_1_ = blockIn == worldIn.getBlockState(lvt_5_2_).getBlock() || blockIn == worldIn.getBlockState(lvt_6_2_).getBlock();

        if (lvt_9_1_ && lvt_10_1_)
        {
            lvt_3_1_ /= 2.0F;
        }
        else
        {
            boolean lvt_11_1_ = blockIn == worldIn.getBlockState(lvt_7_2_.north()).getBlock() || blockIn == worldIn.getBlockState(lvt_8_2_.north()).getBlock() || blockIn == worldIn.getBlockState(lvt_8_2_.south()).getBlock() || blockIn == worldIn.getBlockState(lvt_7_2_.south()).getBlock();

            if (lvt_11_1_)
            {
                lvt_3_1_ /= 2.0F;
            }
        }

        return lvt_3_1_;
    }

    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        return (worldIn.getLight(pos) >= 8 || worldIn.canSeeSky(pos)) && this.canPlaceBlockOn(worldIn.getBlockState(pos.down()).getBlock());
    }

    protected Item getSeed()
    {
        return Items.wheat_seeds;
    }

    protected Item getCrop()
    {
        return Items.wheat;
    }

    /**
     * Spawns this Block's drops into the World as EntityItems.
     */
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        super.dropBlockAsItemWithChance(worldIn, pos, state, chance, 0);

        if (!worldIn.isRemote)
        {
            int lvt_6_1_ = ((Integer)state.getValue(AGE)).intValue();

            if (lvt_6_1_ >= 7)
            {
                int lvt_7_1_ = 3 + fortune;

                for (int lvt_8_1_ = 0; lvt_8_1_ < lvt_7_1_; ++lvt_8_1_)
                {
                    if (worldIn.rand.nextInt(15) <= lvt_6_1_)
                    {
                        spawnAsEntity(worldIn, pos, new ItemStack(this.getSeed(), 1, 0));
                    }
                }
            }
        }
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return ((Integer)state.getValue(AGE)).intValue() == 7 ? this.getCrop() : this.getSeed();
    }

    public Item getItem(World worldIn, BlockPos pos)
    {
        return this.getSeed();
    }

    /**
     * Whether this IGrowable can grow
     */
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        return ((Integer)state.getValue(AGE)).intValue() < 7;
    }

    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        return true;
    }

    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        this.grow(worldIn, pos, state);
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
        return new BlockState(this, new IProperty[] {AGE});
    }
}
