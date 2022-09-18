package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import net.minecraft.world.gen.feature.WorldGenForest;
import net.minecraft.world.gen.feature.WorldGenMegaJungle;
import net.minecraft.world.gen.feature.WorldGenMegaPineTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BlockSapling extends BlockBush implements IGrowable
{
    public static final PropertyEnum<BlockPlanks.EnumType> TYPE = PropertyEnum.<BlockPlanks.EnumType>create("type", BlockPlanks.EnumType.class);
    public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 1);

    protected BlockSapling()
    {
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, BlockPlanks.EnumType.OAK).withProperty(STAGE, Integer.valueOf(0)));
        float lvt_1_1_ = 0.4F;
        this.setBlockBounds(0.5F - lvt_1_1_, 0.0F, 0.5F - lvt_1_1_, 0.5F + lvt_1_1_, lvt_1_1_ * 2.0F, 0.5F + lvt_1_1_);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    /**
     * Gets the localized name of this block. Used for the statistics page.
     */
    public String getLocalizedName()
    {
        return StatCollector.translateToLocal(this.getUnlocalizedName() + "." + BlockPlanks.EnumType.OAK.getUnlocalizedName() + ".name");
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isRemote)
        {
            super.updateTick(worldIn, pos, state, rand);

            if (worldIn.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(7) == 0)
            {
                this.grow(worldIn, pos, state, rand);
            }
        }
    }

    public void grow(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (((Integer)state.getValue(STAGE)).intValue() == 0)
        {
            worldIn.setBlockState(pos, state.cycleProperty(STAGE), 4);
        }
        else
        {
            this.generateTree(worldIn, pos, state, rand);
        }
    }

    public void generateTree(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        WorldGenerator lvt_5_1_ = (WorldGenerator)(rand.nextInt(10) == 0 ? new WorldGenBigTree(true) : new WorldGenTrees(true));
        int lvt_6_1_ = 0;
        int lvt_7_1_ = 0;
        boolean lvt_8_1_ = false;

        switch ((BlockPlanks.EnumType)state.getValue(TYPE))
        {
            case SPRUCE:
                label114:
                for (lvt_6_1_ = 0; lvt_6_1_ >= -1; --lvt_6_1_)
                {
                    for (lvt_7_1_ = 0; lvt_7_1_ >= -1; --lvt_7_1_)
                    {
                        if (this.func_181624_a(worldIn, pos, lvt_6_1_, lvt_7_1_, BlockPlanks.EnumType.SPRUCE))
                        {
                            lvt_5_1_ = new WorldGenMegaPineTree(false, rand.nextBoolean());
                            lvt_8_1_ = true;
                            break label114;
                        }
                    }
                }

                if (!lvt_8_1_)
                {
                    lvt_7_1_ = 0;
                    lvt_6_1_ = 0;
                    lvt_5_1_ = new WorldGenTaiga2(true);
                }

                break;

            case BIRCH:
                lvt_5_1_ = new WorldGenForest(true, false);
                break;

            case JUNGLE:
                IBlockState lvt_9_1_ = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE);
                IBlockState lvt_10_1_ = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
                label269:

                for (lvt_6_1_ = 0; lvt_6_1_ >= -1; --lvt_6_1_)
                {
                    for (lvt_7_1_ = 0; lvt_7_1_ >= -1; --lvt_7_1_)
                    {
                        if (this.func_181624_a(worldIn, pos, lvt_6_1_, lvt_7_1_, BlockPlanks.EnumType.JUNGLE))
                        {
                            lvt_5_1_ = new WorldGenMegaJungle(true, 10, 20, lvt_9_1_, lvt_10_1_);
                            lvt_8_1_ = true;
                            break label269;
                        }
                    }
                }

                if (!lvt_8_1_)
                {
                    lvt_7_1_ = 0;
                    lvt_6_1_ = 0;
                    lvt_5_1_ = new WorldGenTrees(true, 4 + rand.nextInt(7), lvt_9_1_, lvt_10_1_, false);
                }

                break;

            case ACACIA:
                lvt_5_1_ = new WorldGenSavannaTree(true);
                break;

            case DARK_OAK:
                label390:
                for (lvt_6_1_ = 0; lvt_6_1_ >= -1; --lvt_6_1_)
                {
                    for (lvt_7_1_ = 0; lvt_7_1_ >= -1; --lvt_7_1_)
                    {
                        if (this.func_181624_a(worldIn, pos, lvt_6_1_, lvt_7_1_, BlockPlanks.EnumType.DARK_OAK))
                        {
                            lvt_5_1_ = new WorldGenCanopyTree(true);
                            lvt_8_1_ = true;
                            break label390;
                        }
                    }
                }

                if (!lvt_8_1_)
                {
                    return;
                }

            case OAK:
        }

        IBlockState lvt_9_2_ = Blocks.air.getDefaultState();

        if (lvt_8_1_)
        {
            worldIn.setBlockState(pos.add(lvt_6_1_, 0, lvt_7_1_), lvt_9_2_, 4);
            worldIn.setBlockState(pos.add(lvt_6_1_ + 1, 0, lvt_7_1_), lvt_9_2_, 4);
            worldIn.setBlockState(pos.add(lvt_6_1_, 0, lvt_7_1_ + 1), lvt_9_2_, 4);
            worldIn.setBlockState(pos.add(lvt_6_1_ + 1, 0, lvt_7_1_ + 1), lvt_9_2_, 4);
        }
        else
        {
            worldIn.setBlockState(pos, lvt_9_2_, 4);
        }

        if (!lvt_5_1_.generate(worldIn, rand, pos.add(lvt_6_1_, 0, lvt_7_1_)))
        {
            if (lvt_8_1_)
            {
                worldIn.setBlockState(pos.add(lvt_6_1_, 0, lvt_7_1_), state, 4);
                worldIn.setBlockState(pos.add(lvt_6_1_ + 1, 0, lvt_7_1_), state, 4);
                worldIn.setBlockState(pos.add(lvt_6_1_, 0, lvt_7_1_ + 1), state, 4);
                worldIn.setBlockState(pos.add(lvt_6_1_ + 1, 0, lvt_7_1_ + 1), state, 4);
            }
            else
            {
                worldIn.setBlockState(pos, state, 4);
            }
        }
    }

    private boolean func_181624_a(World p_181624_1_, BlockPos p_181624_2_, int p_181624_3_, int p_181624_4_, BlockPlanks.EnumType p_181624_5_)
    {
        return this.isTypeAt(p_181624_1_, p_181624_2_.add(p_181624_3_, 0, p_181624_4_), p_181624_5_) && this.isTypeAt(p_181624_1_, p_181624_2_.add(p_181624_3_ + 1, 0, p_181624_4_), p_181624_5_) && this.isTypeAt(p_181624_1_, p_181624_2_.add(p_181624_3_, 0, p_181624_4_ + 1), p_181624_5_) && this.isTypeAt(p_181624_1_, p_181624_2_.add(p_181624_3_ + 1, 0, p_181624_4_ + 1), p_181624_5_);
    }

    /**
     * Check whether the given BlockPos has a Sapling of the given type
     */
    public boolean isTypeAt(World worldIn, BlockPos pos, BlockPlanks.EnumType type)
    {
        IBlockState lvt_4_1_ = worldIn.getBlockState(pos);
        return lvt_4_1_.getBlock() == this && lvt_4_1_.getValue(TYPE) == type;
    }

    /**
     * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
     * returns the metadata of the dropped item based on the old metadata of the block.
     */
    public int damageDropped(IBlockState state)
    {
        return ((BlockPlanks.EnumType)state.getValue(TYPE)).getMetadata();
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
    {
        for (BlockPlanks.EnumType lvt_7_1_ : BlockPlanks.EnumType.values())
        {
            list.add(new ItemStack(itemIn, 1, lvt_7_1_.getMetadata()));
        }
    }

    /**
     * Whether this IGrowable can grow
     */
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        return true;
    }

    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        return (double)worldIn.rand.nextFloat() < 0.45D;
    }

    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        this.grow(worldIn, pos, state, rand);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(TYPE, BlockPlanks.EnumType.byMetadata(meta & 7)).withProperty(STAGE, Integer.valueOf((meta & 8) >> 3));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int lvt_2_1_ = 0;
        lvt_2_1_ = lvt_2_1_ | ((BlockPlanks.EnumType)state.getValue(TYPE)).getMetadata();
        lvt_2_1_ = lvt_2_1_ | ((Integer)state.getValue(STAGE)).intValue() << 3;
        return lvt_2_1_;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {TYPE, STAGE});
    }
}
