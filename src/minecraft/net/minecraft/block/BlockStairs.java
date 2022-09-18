package net.minecraft.block;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStairs extends Block
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyEnum<BlockStairs.EnumHalf> HALF = PropertyEnum.<BlockStairs.EnumHalf>create("half", BlockStairs.EnumHalf.class);
    public static final PropertyEnum<BlockStairs.EnumShape> SHAPE = PropertyEnum.<BlockStairs.EnumShape>create("shape", BlockStairs.EnumShape.class);
    private static final int[][] field_150150_a = new int[][] {{4, 5}, {5, 7}, {6, 7}, {4, 6}, {0, 1}, {1, 3}, {2, 3}, {0, 2}};
    private final Block modelBlock;
    private final IBlockState modelState;
    private boolean hasRaytraced;
    private int rayTracePass;

    protected BlockStairs(IBlockState modelState)
    {
        super(modelState.getBlock().blockMaterial);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(HALF, BlockStairs.EnumHalf.BOTTOM).withProperty(SHAPE, BlockStairs.EnumShape.STRAIGHT));
        this.modelBlock = modelState.getBlock();
        this.modelState = modelState;
        this.setHardness(this.modelBlock.blockHardness);
        this.setResistance(this.modelBlock.blockResistance / 3.0F);
        this.setStepSound(this.modelBlock.stepSound);
        this.setLightOpacity(255);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
        if (this.hasRaytraced)
        {
            this.setBlockBounds(0.5F * (float)(this.rayTracePass % 2), 0.5F * (float)(this.rayTracePass / 4 % 2), 0.5F * (float)(this.rayTracePass / 2 % 2), 0.5F + 0.5F * (float)(this.rayTracePass % 2), 0.5F + 0.5F * (float)(this.rayTracePass / 4 % 2), 0.5F + 0.5F * (float)(this.rayTracePass / 2 % 2));
        }
        else
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
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
     * Set the block bounds as the collision bounds for the stairs at the given position
     */
    public void setBaseCollisionBounds(IBlockAccess worldIn, BlockPos pos)
    {
        if (worldIn.getBlockState(pos).getValue(HALF) == BlockStairs.EnumHalf.TOP)
        {
            this.setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
        else
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }
    }

    /**
     * Checks if a block is stairs
     */
    public static boolean isBlockStairs(Block blockIn)
    {
        return blockIn instanceof BlockStairs;
    }

    /**
     * Check whether there is a stair block at the given position and it has the same properties as the given BlockState
     */
    public static boolean isSameStair(IBlockAccess worldIn, BlockPos pos, IBlockState state)
    {
        IBlockState lvt_3_1_ = worldIn.getBlockState(pos);
        Block lvt_4_1_ = lvt_3_1_.getBlock();
        return isBlockStairs(lvt_4_1_) && lvt_3_1_.getValue(HALF) == state.getValue(HALF) && lvt_3_1_.getValue(FACING) == state.getValue(FACING);
    }

    public int func_176307_f(IBlockAccess blockAccess, BlockPos pos)
    {
        IBlockState lvt_3_1_ = blockAccess.getBlockState(pos);
        EnumFacing lvt_4_1_ = (EnumFacing)lvt_3_1_.getValue(FACING);
        BlockStairs.EnumHalf lvt_5_1_ = (BlockStairs.EnumHalf)lvt_3_1_.getValue(HALF);
        boolean lvt_6_1_ = lvt_5_1_ == BlockStairs.EnumHalf.TOP;

        if (lvt_4_1_ == EnumFacing.EAST)
        {
            IBlockState lvt_7_1_ = blockAccess.getBlockState(pos.east());
            Block lvt_8_1_ = lvt_7_1_.getBlock();

            if (isBlockStairs(lvt_8_1_) && lvt_5_1_ == lvt_7_1_.getValue(HALF))
            {
                EnumFacing lvt_9_1_ = (EnumFacing)lvt_7_1_.getValue(FACING);

                if (lvt_9_1_ == EnumFacing.NORTH && !isSameStair(blockAccess, pos.south(), lvt_3_1_))
                {
                    return lvt_6_1_ ? 1 : 2;
                }

                if (lvt_9_1_ == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.north(), lvt_3_1_))
                {
                    return lvt_6_1_ ? 2 : 1;
                }
            }
        }
        else if (lvt_4_1_ == EnumFacing.WEST)
        {
            IBlockState lvt_7_2_ = blockAccess.getBlockState(pos.west());
            Block lvt_8_2_ = lvt_7_2_.getBlock();

            if (isBlockStairs(lvt_8_2_) && lvt_5_1_ == lvt_7_2_.getValue(HALF))
            {
                EnumFacing lvt_9_2_ = (EnumFacing)lvt_7_2_.getValue(FACING);

                if (lvt_9_2_ == EnumFacing.NORTH && !isSameStair(blockAccess, pos.south(), lvt_3_1_))
                {
                    return lvt_6_1_ ? 2 : 1;
                }

                if (lvt_9_2_ == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.north(), lvt_3_1_))
                {
                    return lvt_6_1_ ? 1 : 2;
                }
            }
        }
        else if (lvt_4_1_ == EnumFacing.SOUTH)
        {
            IBlockState lvt_7_3_ = blockAccess.getBlockState(pos.south());
            Block lvt_8_3_ = lvt_7_3_.getBlock();

            if (isBlockStairs(lvt_8_3_) && lvt_5_1_ == lvt_7_3_.getValue(HALF))
            {
                EnumFacing lvt_9_3_ = (EnumFacing)lvt_7_3_.getValue(FACING);

                if (lvt_9_3_ == EnumFacing.WEST && !isSameStair(blockAccess, pos.east(), lvt_3_1_))
                {
                    return lvt_6_1_ ? 2 : 1;
                }

                if (lvt_9_3_ == EnumFacing.EAST && !isSameStair(blockAccess, pos.west(), lvt_3_1_))
                {
                    return lvt_6_1_ ? 1 : 2;
                }
            }
        }
        else if (lvt_4_1_ == EnumFacing.NORTH)
        {
            IBlockState lvt_7_4_ = blockAccess.getBlockState(pos.north());
            Block lvt_8_4_ = lvt_7_4_.getBlock();

            if (isBlockStairs(lvt_8_4_) && lvt_5_1_ == lvt_7_4_.getValue(HALF))
            {
                EnumFacing lvt_9_4_ = (EnumFacing)lvt_7_4_.getValue(FACING);

                if (lvt_9_4_ == EnumFacing.WEST && !isSameStair(blockAccess, pos.east(), lvt_3_1_))
                {
                    return lvt_6_1_ ? 1 : 2;
                }

                if (lvt_9_4_ == EnumFacing.EAST && !isSameStair(blockAccess, pos.west(), lvt_3_1_))
                {
                    return lvt_6_1_ ? 2 : 1;
                }
            }
        }

        return 0;
    }

    public int func_176305_g(IBlockAccess blockAccess, BlockPos pos)
    {
        IBlockState lvt_3_1_ = blockAccess.getBlockState(pos);
        EnumFacing lvt_4_1_ = (EnumFacing)lvt_3_1_.getValue(FACING);
        BlockStairs.EnumHalf lvt_5_1_ = (BlockStairs.EnumHalf)lvt_3_1_.getValue(HALF);
        boolean lvt_6_1_ = lvt_5_1_ == BlockStairs.EnumHalf.TOP;

        if (lvt_4_1_ == EnumFacing.EAST)
        {
            IBlockState lvt_7_1_ = blockAccess.getBlockState(pos.west());
            Block lvt_8_1_ = lvt_7_1_.getBlock();

            if (isBlockStairs(lvt_8_1_) && lvt_5_1_ == lvt_7_1_.getValue(HALF))
            {
                EnumFacing lvt_9_1_ = (EnumFacing)lvt_7_1_.getValue(FACING);

                if (lvt_9_1_ == EnumFacing.NORTH && !isSameStair(blockAccess, pos.north(), lvt_3_1_))
                {
                    return lvt_6_1_ ? 1 : 2;
                }

                if (lvt_9_1_ == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.south(), lvt_3_1_))
                {
                    return lvt_6_1_ ? 2 : 1;
                }
            }
        }
        else if (lvt_4_1_ == EnumFacing.WEST)
        {
            IBlockState lvt_7_2_ = blockAccess.getBlockState(pos.east());
            Block lvt_8_2_ = lvt_7_2_.getBlock();

            if (isBlockStairs(lvt_8_2_) && lvt_5_1_ == lvt_7_2_.getValue(HALF))
            {
                EnumFacing lvt_9_2_ = (EnumFacing)lvt_7_2_.getValue(FACING);

                if (lvt_9_2_ == EnumFacing.NORTH && !isSameStair(blockAccess, pos.north(), lvt_3_1_))
                {
                    return lvt_6_1_ ? 2 : 1;
                }

                if (lvt_9_2_ == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.south(), lvt_3_1_))
                {
                    return lvt_6_1_ ? 1 : 2;
                }
            }
        }
        else if (lvt_4_1_ == EnumFacing.SOUTH)
        {
            IBlockState lvt_7_3_ = blockAccess.getBlockState(pos.north());
            Block lvt_8_3_ = lvt_7_3_.getBlock();

            if (isBlockStairs(lvt_8_3_) && lvt_5_1_ == lvt_7_3_.getValue(HALF))
            {
                EnumFacing lvt_9_3_ = (EnumFacing)lvt_7_3_.getValue(FACING);

                if (lvt_9_3_ == EnumFacing.WEST && !isSameStair(blockAccess, pos.west(), lvt_3_1_))
                {
                    return lvt_6_1_ ? 2 : 1;
                }

                if (lvt_9_3_ == EnumFacing.EAST && !isSameStair(blockAccess, pos.east(), lvt_3_1_))
                {
                    return lvt_6_1_ ? 1 : 2;
                }
            }
        }
        else if (lvt_4_1_ == EnumFacing.NORTH)
        {
            IBlockState lvt_7_4_ = blockAccess.getBlockState(pos.south());
            Block lvt_8_4_ = lvt_7_4_.getBlock();

            if (isBlockStairs(lvt_8_4_) && lvt_5_1_ == lvt_7_4_.getValue(HALF))
            {
                EnumFacing lvt_9_4_ = (EnumFacing)lvt_7_4_.getValue(FACING);

                if (lvt_9_4_ == EnumFacing.WEST && !isSameStair(blockAccess, pos.west(), lvt_3_1_))
                {
                    return lvt_6_1_ ? 1 : 2;
                }

                if (lvt_9_4_ == EnumFacing.EAST && !isSameStair(blockAccess, pos.east(), lvt_3_1_))
                {
                    return lvt_6_1_ ? 2 : 1;
                }
            }
        }

        return 0;
    }

    public boolean func_176306_h(IBlockAccess blockAccess, BlockPos pos)
    {
        IBlockState lvt_3_1_ = blockAccess.getBlockState(pos);
        EnumFacing lvt_4_1_ = (EnumFacing)lvt_3_1_.getValue(FACING);
        BlockStairs.EnumHalf lvt_5_1_ = (BlockStairs.EnumHalf)lvt_3_1_.getValue(HALF);
        boolean lvt_6_1_ = lvt_5_1_ == BlockStairs.EnumHalf.TOP;
        float lvt_7_1_ = 0.5F;
        float lvt_8_1_ = 1.0F;

        if (lvt_6_1_)
        {
            lvt_7_1_ = 0.0F;
            lvt_8_1_ = 0.5F;
        }

        float lvt_9_1_ = 0.0F;
        float lvt_10_1_ = 1.0F;
        float lvt_11_1_ = 0.0F;
        float lvt_12_1_ = 0.5F;
        boolean lvt_13_1_ = true;

        if (lvt_4_1_ == EnumFacing.EAST)
        {
            lvt_9_1_ = 0.5F;
            lvt_12_1_ = 1.0F;
            IBlockState lvt_14_1_ = blockAccess.getBlockState(pos.east());
            Block lvt_15_1_ = lvt_14_1_.getBlock();

            if (isBlockStairs(lvt_15_1_) && lvt_5_1_ == lvt_14_1_.getValue(HALF))
            {
                EnumFacing lvt_16_1_ = (EnumFacing)lvt_14_1_.getValue(FACING);

                if (lvt_16_1_ == EnumFacing.NORTH && !isSameStair(blockAccess, pos.south(), lvt_3_1_))
                {
                    lvt_12_1_ = 0.5F;
                    lvt_13_1_ = false;
                }
                else if (lvt_16_1_ == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.north(), lvt_3_1_))
                {
                    lvt_11_1_ = 0.5F;
                    lvt_13_1_ = false;
                }
            }
        }
        else if (lvt_4_1_ == EnumFacing.WEST)
        {
            lvt_10_1_ = 0.5F;
            lvt_12_1_ = 1.0F;
            IBlockState lvt_14_2_ = blockAccess.getBlockState(pos.west());
            Block lvt_15_2_ = lvt_14_2_.getBlock();

            if (isBlockStairs(lvt_15_2_) && lvt_5_1_ == lvt_14_2_.getValue(HALF))
            {
                EnumFacing lvt_16_2_ = (EnumFacing)lvt_14_2_.getValue(FACING);

                if (lvt_16_2_ == EnumFacing.NORTH && !isSameStair(blockAccess, pos.south(), lvt_3_1_))
                {
                    lvt_12_1_ = 0.5F;
                    lvt_13_1_ = false;
                }
                else if (lvt_16_2_ == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.north(), lvt_3_1_))
                {
                    lvt_11_1_ = 0.5F;
                    lvt_13_1_ = false;
                }
            }
        }
        else if (lvt_4_1_ == EnumFacing.SOUTH)
        {
            lvt_11_1_ = 0.5F;
            lvt_12_1_ = 1.0F;
            IBlockState lvt_14_3_ = blockAccess.getBlockState(pos.south());
            Block lvt_15_3_ = lvt_14_3_.getBlock();

            if (isBlockStairs(lvt_15_3_) && lvt_5_1_ == lvt_14_3_.getValue(HALF))
            {
                EnumFacing lvt_16_3_ = (EnumFacing)lvt_14_3_.getValue(FACING);

                if (lvt_16_3_ == EnumFacing.WEST && !isSameStair(blockAccess, pos.east(), lvt_3_1_))
                {
                    lvt_10_1_ = 0.5F;
                    lvt_13_1_ = false;
                }
                else if (lvt_16_3_ == EnumFacing.EAST && !isSameStair(blockAccess, pos.west(), lvt_3_1_))
                {
                    lvt_9_1_ = 0.5F;
                    lvt_13_1_ = false;
                }
            }
        }
        else if (lvt_4_1_ == EnumFacing.NORTH)
        {
            IBlockState lvt_14_4_ = blockAccess.getBlockState(pos.north());
            Block lvt_15_4_ = lvt_14_4_.getBlock();

            if (isBlockStairs(lvt_15_4_) && lvt_5_1_ == lvt_14_4_.getValue(HALF))
            {
                EnumFacing lvt_16_4_ = (EnumFacing)lvt_14_4_.getValue(FACING);

                if (lvt_16_4_ == EnumFacing.WEST && !isSameStair(blockAccess, pos.east(), lvt_3_1_))
                {
                    lvt_10_1_ = 0.5F;
                    lvt_13_1_ = false;
                }
                else if (lvt_16_4_ == EnumFacing.EAST && !isSameStair(blockAccess, pos.west(), lvt_3_1_))
                {
                    lvt_9_1_ = 0.5F;
                    lvt_13_1_ = false;
                }
            }
        }

        this.setBlockBounds(lvt_9_1_, lvt_7_1_, lvt_11_1_, lvt_10_1_, lvt_8_1_, lvt_12_1_);
        return lvt_13_1_;
    }

    public boolean func_176304_i(IBlockAccess blockAccess, BlockPos pos)
    {
        IBlockState lvt_3_1_ = blockAccess.getBlockState(pos);
        EnumFacing lvt_4_1_ = (EnumFacing)lvt_3_1_.getValue(FACING);
        BlockStairs.EnumHalf lvt_5_1_ = (BlockStairs.EnumHalf)lvt_3_1_.getValue(HALF);
        boolean lvt_6_1_ = lvt_5_1_ == BlockStairs.EnumHalf.TOP;
        float lvt_7_1_ = 0.5F;
        float lvt_8_1_ = 1.0F;

        if (lvt_6_1_)
        {
            lvt_7_1_ = 0.0F;
            lvt_8_1_ = 0.5F;
        }

        float lvt_9_1_ = 0.0F;
        float lvt_10_1_ = 0.5F;
        float lvt_11_1_ = 0.5F;
        float lvt_12_1_ = 1.0F;
        boolean lvt_13_1_ = false;

        if (lvt_4_1_ == EnumFacing.EAST)
        {
            IBlockState lvt_14_1_ = blockAccess.getBlockState(pos.west());
            Block lvt_15_1_ = lvt_14_1_.getBlock();

            if (isBlockStairs(lvt_15_1_) && lvt_5_1_ == lvt_14_1_.getValue(HALF))
            {
                EnumFacing lvt_16_1_ = (EnumFacing)lvt_14_1_.getValue(FACING);

                if (lvt_16_1_ == EnumFacing.NORTH && !isSameStair(blockAccess, pos.north(), lvt_3_1_))
                {
                    lvt_11_1_ = 0.0F;
                    lvt_12_1_ = 0.5F;
                    lvt_13_1_ = true;
                }
                else if (lvt_16_1_ == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.south(), lvt_3_1_))
                {
                    lvt_11_1_ = 0.5F;
                    lvt_12_1_ = 1.0F;
                    lvt_13_1_ = true;
                }
            }
        }
        else if (lvt_4_1_ == EnumFacing.WEST)
        {
            IBlockState lvt_14_2_ = blockAccess.getBlockState(pos.east());
            Block lvt_15_2_ = lvt_14_2_.getBlock();

            if (isBlockStairs(lvt_15_2_) && lvt_5_1_ == lvt_14_2_.getValue(HALF))
            {
                lvt_9_1_ = 0.5F;
                lvt_10_1_ = 1.0F;
                EnumFacing lvt_16_2_ = (EnumFacing)lvt_14_2_.getValue(FACING);

                if (lvt_16_2_ == EnumFacing.NORTH && !isSameStair(blockAccess, pos.north(), lvt_3_1_))
                {
                    lvt_11_1_ = 0.0F;
                    lvt_12_1_ = 0.5F;
                    lvt_13_1_ = true;
                }
                else if (lvt_16_2_ == EnumFacing.SOUTH && !isSameStair(blockAccess, pos.south(), lvt_3_1_))
                {
                    lvt_11_1_ = 0.5F;
                    lvt_12_1_ = 1.0F;
                    lvt_13_1_ = true;
                }
            }
        }
        else if (lvt_4_1_ == EnumFacing.SOUTH)
        {
            IBlockState lvt_14_3_ = blockAccess.getBlockState(pos.north());
            Block lvt_15_3_ = lvt_14_3_.getBlock();

            if (isBlockStairs(lvt_15_3_) && lvt_5_1_ == lvt_14_3_.getValue(HALF))
            {
                lvt_11_1_ = 0.0F;
                lvt_12_1_ = 0.5F;
                EnumFacing lvt_16_3_ = (EnumFacing)lvt_14_3_.getValue(FACING);

                if (lvt_16_3_ == EnumFacing.WEST && !isSameStair(blockAccess, pos.west(), lvt_3_1_))
                {
                    lvt_13_1_ = true;
                }
                else if (lvt_16_3_ == EnumFacing.EAST && !isSameStair(blockAccess, pos.east(), lvt_3_1_))
                {
                    lvt_9_1_ = 0.5F;
                    lvt_10_1_ = 1.0F;
                    lvt_13_1_ = true;
                }
            }
        }
        else if (lvt_4_1_ == EnumFacing.NORTH)
        {
            IBlockState lvt_14_4_ = blockAccess.getBlockState(pos.south());
            Block lvt_15_4_ = lvt_14_4_.getBlock();

            if (isBlockStairs(lvt_15_4_) && lvt_5_1_ == lvt_14_4_.getValue(HALF))
            {
                EnumFacing lvt_16_4_ = (EnumFacing)lvt_14_4_.getValue(FACING);

                if (lvt_16_4_ == EnumFacing.WEST && !isSameStair(blockAccess, pos.west(), lvt_3_1_))
                {
                    lvt_13_1_ = true;
                }
                else if (lvt_16_4_ == EnumFacing.EAST && !isSameStair(blockAccess, pos.east(), lvt_3_1_))
                {
                    lvt_9_1_ = 0.5F;
                    lvt_10_1_ = 1.0F;
                    lvt_13_1_ = true;
                }
            }
        }

        if (lvt_13_1_)
        {
            this.setBlockBounds(lvt_9_1_, lvt_7_1_, lvt_11_1_, lvt_10_1_, lvt_8_1_, lvt_12_1_);
        }

        return lvt_13_1_;
    }

    /**
     * Add all collision boxes of this Block to the list that intersect with the given mask.
     */
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity)
    {
        this.setBaseCollisionBounds(worldIn, pos);
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        boolean lvt_7_1_ = this.func_176306_h(worldIn, pos);
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);

        if (lvt_7_1_ && this.func_176304_i(worldIn, pos))
        {
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        }

        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        this.modelBlock.randomDisplayTick(worldIn, pos, state, rand);
    }

    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn)
    {
        this.modelBlock.onBlockClicked(worldIn, pos, playerIn);
    }

    /**
     * Called when a player destroys this Block
     */
    public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state)
    {
        this.modelBlock.onBlockDestroyedByPlayer(worldIn, pos, state);
    }

    public int getMixedBrightnessForBlock(IBlockAccess worldIn, BlockPos pos)
    {
        return this.modelBlock.getMixedBrightnessForBlock(worldIn, pos);
    }

    /**
     * Returns how much this block can resist explosions from the passed in entity.
     */
    public float getExplosionResistance(Entity exploder)
    {
        return this.modelBlock.getExplosionResistance(exploder);
    }

    public EnumWorldBlockLayer getBlockLayer()
    {
        return this.modelBlock.getBlockLayer();
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World worldIn)
    {
        return this.modelBlock.tickRate(worldIn);
    }

    public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos)
    {
        return this.modelBlock.getSelectedBoundingBox(worldIn, pos);
    }

    public Vec3 modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3 motion)
    {
        return this.modelBlock.modifyAcceleration(worldIn, pos, entityIn, motion);
    }

    /**
     * Returns if this block is collidable (only used by Fire). Args: x, y, z
     */
    public boolean isCollidable()
    {
        return this.modelBlock.isCollidable();
    }

    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid)
    {
        return this.modelBlock.canCollideCheck(state, hitIfLiquid);
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return this.modelBlock.canPlaceBlockAt(worldIn, pos);
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        this.onNeighborBlockChange(worldIn, pos, this.modelState, Blocks.air);
        this.modelBlock.onBlockAdded(worldIn, pos, this.modelState);
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        this.modelBlock.breakBlock(worldIn, pos, this.modelState);
    }

    /**
     * Triggered whenever an entity collides with this block (enters into the block)
     */
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, Entity entityIn)
    {
        this.modelBlock.onEntityCollidedWithBlock(worldIn, pos, entityIn);
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        this.modelBlock.updateTick(worldIn, pos, state, rand);
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        return this.modelBlock.onBlockActivated(worldIn, pos, this.modelState, playerIn, EnumFacing.DOWN, 0.0F, 0.0F, 0.0F);
    }

    /**
     * Called when this Block is destroyed by an Explosion
     */
    public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn)
    {
        this.modelBlock.onBlockDestroyedByExplosion(worldIn, pos, explosionIn);
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    public MapColor getMapColor(IBlockState state)
    {
        return this.modelBlock.getMapColor(this.modelState);
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        IBlockState lvt_9_1_ = super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
        lvt_9_1_ = lvt_9_1_.withProperty(FACING, placer.getHorizontalFacing()).withProperty(SHAPE, BlockStairs.EnumShape.STRAIGHT);
        return facing != EnumFacing.DOWN && (facing == EnumFacing.UP || (double)hitY <= 0.5D) ? lvt_9_1_.withProperty(HALF, BlockStairs.EnumHalf.BOTTOM) : lvt_9_1_.withProperty(HALF, BlockStairs.EnumHalf.TOP);
    }

    /**
     * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit.
     */
    public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end)
    {
        MovingObjectPosition[] lvt_5_1_ = new MovingObjectPosition[8];
        IBlockState lvt_6_1_ = worldIn.getBlockState(pos);
        int lvt_7_1_ = ((EnumFacing)lvt_6_1_.getValue(FACING)).getHorizontalIndex();
        boolean lvt_8_1_ = lvt_6_1_.getValue(HALF) == BlockStairs.EnumHalf.TOP;
        int[] lvt_9_1_ = field_150150_a[lvt_7_1_ + (lvt_8_1_ ? 4 : 0)];
        this.hasRaytraced = true;

        for (int lvt_10_1_ = 0; lvt_10_1_ < 8; ++lvt_10_1_)
        {
            this.rayTracePass = lvt_10_1_;

            if (Arrays.binarySearch(lvt_9_1_, lvt_10_1_) < 0)
            {
                lvt_5_1_[lvt_10_1_] = super.collisionRayTrace(worldIn, pos, start, end);
            }
        }

        for (int lvt_13_1_ : lvt_9_1_)
        {
            lvt_5_1_[lvt_13_1_] = null;
        }

        MovingObjectPosition lvt_10_3_ = null;
        double lvt_11_2_ = 0.0D;

        for (MovingObjectPosition lvt_16_1_ : lvt_5_1_)
        {
            if (lvt_16_1_ != null)
            {
                double lvt_17_1_ = lvt_16_1_.hitVec.squareDistanceTo(end);

                if (lvt_17_1_ > lvt_11_2_)
                {
                    lvt_10_3_ = lvt_16_1_;
                    lvt_11_2_ = lvt_17_1_;
                }
            }
        }

        return lvt_10_3_;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        IBlockState lvt_2_1_ = this.getDefaultState().withProperty(HALF, (meta & 4) > 0 ? BlockStairs.EnumHalf.TOP : BlockStairs.EnumHalf.BOTTOM);
        lvt_2_1_ = lvt_2_1_.withProperty(FACING, EnumFacing.getFront(5 - (meta & 3)));
        return lvt_2_1_;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int lvt_2_1_ = 0;

        if (state.getValue(HALF) == BlockStairs.EnumHalf.TOP)
        {
            lvt_2_1_ |= 4;
        }

        lvt_2_1_ = lvt_2_1_ | 5 - ((EnumFacing)state.getValue(FACING)).getIndex();
        return lvt_2_1_;
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        if (this.func_176306_h(worldIn, pos))
        {
            switch (this.func_176305_g(worldIn, pos))
            {
                case 0:
                    state = state.withProperty(SHAPE, BlockStairs.EnumShape.STRAIGHT);
                    break;

                case 1:
                    state = state.withProperty(SHAPE, BlockStairs.EnumShape.INNER_RIGHT);
                    break;

                case 2:
                    state = state.withProperty(SHAPE, BlockStairs.EnumShape.INNER_LEFT);
            }
        }
        else
        {
            switch (this.func_176307_f(worldIn, pos))
            {
                case 0:
                    state = state.withProperty(SHAPE, BlockStairs.EnumShape.STRAIGHT);
                    break;

                case 1:
                    state = state.withProperty(SHAPE, BlockStairs.EnumShape.OUTER_RIGHT);
                    break;

                case 2:
                    state = state.withProperty(SHAPE, BlockStairs.EnumShape.OUTER_LEFT);
            }
        }

        return state;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {FACING, HALF, SHAPE});
    }

    public static enum EnumHalf implements IStringSerializable
    {
        TOP("top"),
        BOTTOM("bottom");

        private final String name;

        private EnumHalf(String name)
        {
            this.name = name;
        }

        public String toString()
        {
            return this.name;
        }

        public String getName()
        {
            return this.name;
        }
    }

    public static enum EnumShape implements IStringSerializable
    {
        STRAIGHT("straight"),
        INNER_LEFT("inner_left"),
        INNER_RIGHT("inner_right"),
        OUTER_LEFT("outer_left"),
        OUTER_RIGHT("outer_right");

        private final String name;

        private EnumShape(String name)
        {
            this.name = name;
        }

        public String toString()
        {
            return this.name;
        }

        public String getName()
        {
            return this.name;
        }
    }
}
