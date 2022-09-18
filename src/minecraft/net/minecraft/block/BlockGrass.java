package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;

public class BlockGrass extends Block implements IGrowable
{
    public static final PropertyBool SNOWY = PropertyBool.create("snowy");

    protected BlockGrass()
    {
        super(Material.grass);
        this.setDefaultState(this.blockState.getBaseState().withProperty(SNOWY, Boolean.valueOf(false)));
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        Block lvt_4_1_ = worldIn.getBlockState(pos.up()).getBlock();
        return state.withProperty(SNOWY, Boolean.valueOf(lvt_4_1_ == Blocks.snow || lvt_4_1_ == Blocks.snow_layer));
    }

    public int getBlockColor()
    {
        return ColorizerGrass.getGrassColor(0.5D, 1.0D);
    }

    public int getRenderColor(IBlockState state)
    {
        return this.getBlockColor();
    }

    public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass)
    {
        return BiomeColorHelper.getGrassColorAtPos(worldIn, pos);
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isRemote)
        {
            if (worldIn.getLightFromNeighbors(pos.up()) < 4 && worldIn.getBlockState(pos.up()).getBlock().getLightOpacity() > 2)
            {
                worldIn.setBlockState(pos, Blocks.dirt.getDefaultState());
            }
            else
            {
                if (worldIn.getLightFromNeighbors(pos.up()) >= 9)
                {
                    for (int lvt_5_1_ = 0; lvt_5_1_ < 4; ++lvt_5_1_)
                    {
                        BlockPos lvt_6_1_ = pos.add(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);
                        Block lvt_7_1_ = worldIn.getBlockState(lvt_6_1_.up()).getBlock();
                        IBlockState lvt_8_1_ = worldIn.getBlockState(lvt_6_1_);

                        if (lvt_8_1_.getBlock() == Blocks.dirt && lvt_8_1_.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.DIRT && worldIn.getLightFromNeighbors(lvt_6_1_.up()) >= 4 && lvt_7_1_.getLightOpacity() <= 2)
                        {
                            worldIn.setBlockState(lvt_6_1_, Blocks.grass.getDefaultState());
                        }
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
        return Blocks.dirt.getItemDropped(Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT), rand, fortune);
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
        return true;
    }

    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        BlockPos lvt_5_1_ = pos.up();

        for (int lvt_6_1_ = 0; lvt_6_1_ < 128; ++lvt_6_1_)
        {
            BlockPos lvt_7_1_ = lvt_5_1_;
            int lvt_8_1_ = 0;

            while (true)
            {
                if (lvt_8_1_ >= lvt_6_1_ / 16)
                {
                    if (worldIn.getBlockState(lvt_7_1_).getBlock().blockMaterial == Material.air)
                    {
                        if (rand.nextInt(8) == 0)
                        {
                            BlockFlower.EnumFlowerType lvt_8_2_ = worldIn.getBiomeGenForCoords(lvt_7_1_).pickRandomFlower(rand, lvt_7_1_);
                            BlockFlower lvt_9_1_ = lvt_8_2_.getBlockType().getBlock();
                            IBlockState lvt_10_1_ = lvt_9_1_.getDefaultState().withProperty(lvt_9_1_.getTypeProperty(), lvt_8_2_);

                            if (lvt_9_1_.canBlockStay(worldIn, lvt_7_1_, lvt_10_1_))
                            {
                                worldIn.setBlockState(lvt_7_1_, lvt_10_1_, 3);
                            }
                        }
                        else
                        {
                            IBlockState lvt_8_3_ = Blocks.tallgrass.getDefaultState().withProperty(BlockTallGrass.TYPE, BlockTallGrass.EnumType.GRASS);

                            if (Blocks.tallgrass.canBlockStay(worldIn, lvt_7_1_, lvt_8_3_))
                            {
                                worldIn.setBlockState(lvt_7_1_, lvt_8_3_, 3);
                            }
                        }
                    }

                    break;
                }

                lvt_7_1_ = lvt_7_1_.add(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);

                if (worldIn.getBlockState(lvt_7_1_.down()).getBlock() != Blocks.grass || worldIn.getBlockState(lvt_7_1_).getBlock().isNormalCube())
                {
                    break;
                }

                ++lvt_8_1_;
            }
        }
    }

    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.CUTOUT_MIPPED;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {SNOWY});
    }
}
