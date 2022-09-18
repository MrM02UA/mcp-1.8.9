package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;

public abstract class BlockLeaves extends BlockLeavesBase
{
    public static final PropertyBool DECAYABLE = PropertyBool.create("decayable");
    public static final PropertyBool CHECK_DECAY = PropertyBool.create("check_decay");
    int[] surroundings;
    protected int iconIndex;
    protected boolean isTransparent;

    public BlockLeaves()
    {
        super(Material.leaves, false);
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setHardness(0.2F);
        this.setLightOpacity(1);
        this.setStepSound(soundTypeGrass);
    }

    public int getBlockColor()
    {
        return ColorizerFoliage.getFoliageColor(0.5D, 1.0D);
    }

    public int getRenderColor(IBlockState state)
    {
        return ColorizerFoliage.getFoliageColorBasic();
    }

    public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass)
    {
        return BiomeColorHelper.getFoliageColorAtPos(worldIn, pos);
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        int lvt_4_1_ = 1;
        int lvt_5_1_ = lvt_4_1_ + 1;
        int lvt_6_1_ = pos.getX();
        int lvt_7_1_ = pos.getY();
        int lvt_8_1_ = pos.getZ();

        if (worldIn.isAreaLoaded(new BlockPos(lvt_6_1_ - lvt_5_1_, lvt_7_1_ - lvt_5_1_, lvt_8_1_ - lvt_5_1_), new BlockPos(lvt_6_1_ + lvt_5_1_, lvt_7_1_ + lvt_5_1_, lvt_8_1_ + lvt_5_1_)))
        {
            for (int lvt_9_1_ = -lvt_4_1_; lvt_9_1_ <= lvt_4_1_; ++lvt_9_1_)
            {
                for (int lvt_10_1_ = -lvt_4_1_; lvt_10_1_ <= lvt_4_1_; ++lvt_10_1_)
                {
                    for (int lvt_11_1_ = -lvt_4_1_; lvt_11_1_ <= lvt_4_1_; ++lvt_11_1_)
                    {
                        BlockPos lvt_12_1_ = pos.add(lvt_9_1_, lvt_10_1_, lvt_11_1_);
                        IBlockState lvt_13_1_ = worldIn.getBlockState(lvt_12_1_);

                        if (lvt_13_1_.getBlock().getMaterial() == Material.leaves && !((Boolean)lvt_13_1_.getValue(CHECK_DECAY)).booleanValue())
                        {
                            worldIn.setBlockState(lvt_12_1_, lvt_13_1_.withProperty(CHECK_DECAY, Boolean.valueOf(true)), 4);
                        }
                    }
                }
            }
        }
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isRemote)
        {
            if (((Boolean)state.getValue(CHECK_DECAY)).booleanValue() && ((Boolean)state.getValue(DECAYABLE)).booleanValue())
            {
                int lvt_5_1_ = 4;
                int lvt_6_1_ = lvt_5_1_ + 1;
                int lvt_7_1_ = pos.getX();
                int lvt_8_1_ = pos.getY();
                int lvt_9_1_ = pos.getZ();
                int lvt_10_1_ = 32;
                int lvt_11_1_ = lvt_10_1_ * lvt_10_1_;
                int lvt_12_1_ = lvt_10_1_ / 2;

                if (this.surroundings == null)
                {
                    this.surroundings = new int[lvt_10_1_ * lvt_10_1_ * lvt_10_1_];
                }

                if (worldIn.isAreaLoaded(new BlockPos(lvt_7_1_ - lvt_6_1_, lvt_8_1_ - lvt_6_1_, lvt_9_1_ - lvt_6_1_), new BlockPos(lvt_7_1_ + lvt_6_1_, lvt_8_1_ + lvt_6_1_, lvt_9_1_ + lvt_6_1_)))
                {
                    BlockPos.MutableBlockPos lvt_13_1_ = new BlockPos.MutableBlockPos();

                    for (int lvt_14_1_ = -lvt_5_1_; lvt_14_1_ <= lvt_5_1_; ++lvt_14_1_)
                    {
                        for (int lvt_15_1_ = -lvt_5_1_; lvt_15_1_ <= lvt_5_1_; ++lvt_15_1_)
                        {
                            for (int lvt_16_1_ = -lvt_5_1_; lvt_16_1_ <= lvt_5_1_; ++lvt_16_1_)
                            {
                                Block lvt_17_1_ = worldIn.getBlockState(lvt_13_1_.set(lvt_7_1_ + lvt_14_1_, lvt_8_1_ + lvt_15_1_, lvt_9_1_ + lvt_16_1_)).getBlock();

                                if (lvt_17_1_ != Blocks.log && lvt_17_1_ != Blocks.log2)
                                {
                                    if (lvt_17_1_.getMaterial() == Material.leaves)
                                    {
                                        this.surroundings[(lvt_14_1_ + lvt_12_1_) * lvt_11_1_ + (lvt_15_1_ + lvt_12_1_) * lvt_10_1_ + lvt_16_1_ + lvt_12_1_] = -2;
                                    }
                                    else
                                    {
                                        this.surroundings[(lvt_14_1_ + lvt_12_1_) * lvt_11_1_ + (lvt_15_1_ + lvt_12_1_) * lvt_10_1_ + lvt_16_1_ + lvt_12_1_] = -1;
                                    }
                                }
                                else
                                {
                                    this.surroundings[(lvt_14_1_ + lvt_12_1_) * lvt_11_1_ + (lvt_15_1_ + lvt_12_1_) * lvt_10_1_ + lvt_16_1_ + lvt_12_1_] = 0;
                                }
                            }
                        }
                    }

                    for (int lvt_14_2_ = 1; lvt_14_2_ <= 4; ++lvt_14_2_)
                    {
                        for (int lvt_15_2_ = -lvt_5_1_; lvt_15_2_ <= lvt_5_1_; ++lvt_15_2_)
                        {
                            for (int lvt_16_2_ = -lvt_5_1_; lvt_16_2_ <= lvt_5_1_; ++lvt_16_2_)
                            {
                                for (int lvt_17_2_ = -lvt_5_1_; lvt_17_2_ <= lvt_5_1_; ++lvt_17_2_)
                                {
                                    if (this.surroundings[(lvt_15_2_ + lvt_12_1_) * lvt_11_1_ + (lvt_16_2_ + lvt_12_1_) * lvt_10_1_ + lvt_17_2_ + lvt_12_1_] == lvt_14_2_ - 1)
                                    {
                                        if (this.surroundings[(lvt_15_2_ + lvt_12_1_ - 1) * lvt_11_1_ + (lvt_16_2_ + lvt_12_1_) * lvt_10_1_ + lvt_17_2_ + lvt_12_1_] == -2)
                                        {
                                            this.surroundings[(lvt_15_2_ + lvt_12_1_ - 1) * lvt_11_1_ + (lvt_16_2_ + lvt_12_1_) * lvt_10_1_ + lvt_17_2_ + lvt_12_1_] = lvt_14_2_;
                                        }

                                        if (this.surroundings[(lvt_15_2_ + lvt_12_1_ + 1) * lvt_11_1_ + (lvt_16_2_ + lvt_12_1_) * lvt_10_1_ + lvt_17_2_ + lvt_12_1_] == -2)
                                        {
                                            this.surroundings[(lvt_15_2_ + lvt_12_1_ + 1) * lvt_11_1_ + (lvt_16_2_ + lvt_12_1_) * lvt_10_1_ + lvt_17_2_ + lvt_12_1_] = lvt_14_2_;
                                        }

                                        if (this.surroundings[(lvt_15_2_ + lvt_12_1_) * lvt_11_1_ + (lvt_16_2_ + lvt_12_1_ - 1) * lvt_10_1_ + lvt_17_2_ + lvt_12_1_] == -2)
                                        {
                                            this.surroundings[(lvt_15_2_ + lvt_12_1_) * lvt_11_1_ + (lvt_16_2_ + lvt_12_1_ - 1) * lvt_10_1_ + lvt_17_2_ + lvt_12_1_] = lvt_14_2_;
                                        }

                                        if (this.surroundings[(lvt_15_2_ + lvt_12_1_) * lvt_11_1_ + (lvt_16_2_ + lvt_12_1_ + 1) * lvt_10_1_ + lvt_17_2_ + lvt_12_1_] == -2)
                                        {
                                            this.surroundings[(lvt_15_2_ + lvt_12_1_) * lvt_11_1_ + (lvt_16_2_ + lvt_12_1_ + 1) * lvt_10_1_ + lvt_17_2_ + lvt_12_1_] = lvt_14_2_;
                                        }

                                        if (this.surroundings[(lvt_15_2_ + lvt_12_1_) * lvt_11_1_ + (lvt_16_2_ + lvt_12_1_) * lvt_10_1_ + (lvt_17_2_ + lvt_12_1_ - 1)] == -2)
                                        {
                                            this.surroundings[(lvt_15_2_ + lvt_12_1_) * lvt_11_1_ + (lvt_16_2_ + lvt_12_1_) * lvt_10_1_ + (lvt_17_2_ + lvt_12_1_ - 1)] = lvt_14_2_;
                                        }

                                        if (this.surroundings[(lvt_15_2_ + lvt_12_1_) * lvt_11_1_ + (lvt_16_2_ + lvt_12_1_) * lvt_10_1_ + lvt_17_2_ + lvt_12_1_ + 1] == -2)
                                        {
                                            this.surroundings[(lvt_15_2_ + lvt_12_1_) * lvt_11_1_ + (lvt_16_2_ + lvt_12_1_) * lvt_10_1_ + lvt_17_2_ + lvt_12_1_ + 1] = lvt_14_2_;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                int lvt_13_2_ = this.surroundings[lvt_12_1_ * lvt_11_1_ + lvt_12_1_ * lvt_10_1_ + lvt_12_1_];

                if (lvt_13_2_ >= 0)
                {
                    worldIn.setBlockState(pos, state.withProperty(CHECK_DECAY, Boolean.valueOf(false)), 4);
                }
                else
                {
                    this.destroy(worldIn, pos);
                }
            }
        }
    }

    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (worldIn.isRainingAt(pos.up()) && !World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && rand.nextInt(15) == 1)
        {
            double lvt_5_1_ = (double)((float)pos.getX() + rand.nextFloat());
            double lvt_7_1_ = (double)pos.getY() - 0.05D;
            double lvt_9_1_ = (double)((float)pos.getZ() + rand.nextFloat());
            worldIn.spawnParticle(EnumParticleTypes.DRIP_WATER, lvt_5_1_, lvt_7_1_, lvt_9_1_, 0.0D, 0.0D, 0.0D, new int[0]);
        }
    }

    private void destroy(World worldIn, BlockPos pos)
    {
        this.dropBlockAsItem(worldIn, pos, worldIn.getBlockState(pos), 0);
        worldIn.setBlockToAir(pos);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return random.nextInt(20) == 0 ? 1 : 0;
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(Blocks.sapling);
    }

    /**
     * Spawns this Block's drops into the World as EntityItems.
     */
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        if (!worldIn.isRemote)
        {
            int lvt_6_1_ = this.getSaplingDropChance(state);

            if (fortune > 0)
            {
                lvt_6_1_ -= 2 << fortune;

                if (lvt_6_1_ < 10)
                {
                    lvt_6_1_ = 10;
                }
            }

            if (worldIn.rand.nextInt(lvt_6_1_) == 0)
            {
                Item lvt_7_1_ = this.getItemDropped(state, worldIn.rand, fortune);
                spawnAsEntity(worldIn, pos, new ItemStack(lvt_7_1_, 1, this.damageDropped(state)));
            }

            lvt_6_1_ = 200;

            if (fortune > 0)
            {
                lvt_6_1_ -= 10 << fortune;

                if (lvt_6_1_ < 40)
                {
                    lvt_6_1_ = 40;
                }
            }

            this.dropApple(worldIn, pos, state, lvt_6_1_);
        }
    }

    protected void dropApple(World worldIn, BlockPos pos, IBlockState state, int chance)
    {
    }

    protected int getSaplingDropChance(IBlockState state)
    {
        return 20;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube()
    {
        return !this.fancyGraphics;
    }

    /**
     * Pass true to draw this block using fancy graphics, or false for fast graphics.
     */
    public void setGraphicsLevel(boolean fancy)
    {
        this.isTransparent = fancy;
        this.fancyGraphics = fancy;
        this.iconIndex = fancy ? 0 : 1;
    }

    public EnumWorldBlockLayer getBlockLayer()
    {
        return this.isTransparent ? EnumWorldBlockLayer.CUTOUT_MIPPED : EnumWorldBlockLayer.SOLID;
    }

    public boolean isVisuallyOpaque()
    {
        return false;
    }

    public abstract BlockPlanks.EnumType getWoodType(int meta);
}
