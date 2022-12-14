package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

public abstract class BlockLog extends BlockRotatedPillar
{
    public static final PropertyEnum<BlockLog.EnumAxis> LOG_AXIS = PropertyEnum.<BlockLog.EnumAxis>create("axis", BlockLog.EnumAxis.class);

    public BlockLog()
    {
        super(Material.wood);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setHardness(2.0F);
        this.setStepSound(soundTypeWood);
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        int lvt_4_1_ = 4;
        int lvt_5_1_ = lvt_4_1_ + 1;

        if (worldIn.isAreaLoaded(pos.add(-lvt_5_1_, -lvt_5_1_, -lvt_5_1_), pos.add(lvt_5_1_, lvt_5_1_, lvt_5_1_)))
        {
            for (BlockPos lvt_7_1_ : BlockPos.getAllInBox(pos.add(-lvt_4_1_, -lvt_4_1_, -lvt_4_1_), pos.add(lvt_4_1_, lvt_4_1_, lvt_4_1_)))
            {
                IBlockState lvt_8_1_ = worldIn.getBlockState(lvt_7_1_);

                if (lvt_8_1_.getBlock().getMaterial() == Material.leaves && !((Boolean)lvt_8_1_.getValue(BlockLeaves.CHECK_DECAY)).booleanValue())
                {
                    worldIn.setBlockState(lvt_7_1_, lvt_8_1_.withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(true)), 4);
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
        return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(LOG_AXIS, BlockLog.EnumAxis.fromFacingAxis(facing.getAxis()));
    }

    public static enum EnumAxis implements IStringSerializable
    {
        X("x"),
        Y("y"),
        Z("z"),
        NONE("none");

        private final String name;

        private EnumAxis(String name)
        {
            this.name = name;
        }

        public String toString()
        {
            return this.name;
        }

        public static BlockLog.EnumAxis fromFacingAxis(EnumFacing.Axis axis)
        {
            switch (axis)
            {
                case X:
                    return X;

                case Y:
                    return Y;

                case Z:
                    return Z;

                default:
                    return NONE;
            }
        }

        public String getName()
        {
            return this.name;
        }
    }
}
