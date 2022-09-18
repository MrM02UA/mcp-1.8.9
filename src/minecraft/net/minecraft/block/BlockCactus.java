package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;

public class BlockCactus extends Block
{
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);

    protected BlockCactus()
    {
        super(Material.cactus);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)));
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        BlockPos lvt_5_1_ = pos.up();

        if (worldIn.isAirBlock(lvt_5_1_))
        {
            int lvt_6_1_;

            for (lvt_6_1_ = 1; worldIn.getBlockState(pos.down(lvt_6_1_)).getBlock() == this; ++lvt_6_1_)
            {
                ;
            }

            if (lvt_6_1_ < 3)
            {
                int lvt_7_1_ = ((Integer)state.getValue(AGE)).intValue();

                if (lvt_7_1_ == 15)
                {
                    worldIn.setBlockState(lvt_5_1_, this.getDefaultState());
                    IBlockState lvt_8_1_ = state.withProperty(AGE, Integer.valueOf(0));
                    worldIn.setBlockState(pos, lvt_8_1_, 4);
                    this.onNeighborBlockChange(worldIn, lvt_5_1_, lvt_8_1_, this);
                }
                else
                {
                    worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(lvt_7_1_ + 1)), 4);
                }
            }
        }
    }

    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
    {
        float lvt_4_1_ = 0.0625F;
        return new AxisAlignedBB((double)((float)pos.getX() + lvt_4_1_), (double)pos.getY(), (double)((float)pos.getZ() + lvt_4_1_), (double)((float)(pos.getX() + 1) - lvt_4_1_), (double)((float)(pos.getY() + 1) - lvt_4_1_), (double)((float)(pos.getZ() + 1) - lvt_4_1_));
    }

    public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos)
    {
        float lvt_3_1_ = 0.0625F;
        return new AxisAlignedBB((double)((float)pos.getX() + lvt_3_1_), (double)pos.getY(), (double)((float)pos.getZ() + lvt_3_1_), (double)((float)(pos.getX() + 1) - lvt_3_1_), (double)(pos.getY() + 1), (double)((float)(pos.getZ() + 1) - lvt_3_1_));
    }

    public boolean isFullCube()
    {
        return false;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return super.canPlaceBlockAt(worldIn, pos) ? this.canBlockStay(worldIn, pos) : false;
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        if (!this.canBlockStay(worldIn, pos))
        {
            worldIn.destroyBlock(pos, true);
        }
    }

    public boolean canBlockStay(World worldIn, BlockPos pos)
    {
        for (EnumFacing lvt_4_1_ : EnumFacing.Plane.HORIZONTAL)
        {
            if (worldIn.getBlockState(pos.offset(lvt_4_1_)).getBlock().getMaterial().isSolid())
            {
                return false;
            }
        }

        Block lvt_3_2_ = worldIn.getBlockState(pos.down()).getBlock();
        return lvt_3_2_ == Blocks.cactus || lvt_3_2_ == Blocks.sand;
    }

    /**
     * Called When an Entity Collided with the Block
     */
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        entityIn.attackEntityFrom(DamageSource.cactus, 1.0F);
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
