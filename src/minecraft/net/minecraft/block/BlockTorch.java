package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class BlockTorch extends Block
{
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

    protected BlockTorch()
    {
        super(Material.circuits);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
    {
        return null;
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

    private boolean canPlaceOn(World worldIn, BlockPos pos)
    {
        if (World.doesBlockHaveSolidTopSurface(worldIn, pos))
        {
            return true;
        }
        else
        {
            Block lvt_3_1_ = worldIn.getBlockState(pos).getBlock();
            return lvt_3_1_ instanceof BlockFence || lvt_3_1_ == Blocks.glass || lvt_3_1_ == Blocks.cobblestone_wall || lvt_3_1_ == Blocks.stained_glass;
        }
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        for (EnumFacing lvt_4_1_ : FACING.getAllowedValues())
        {
            if (this.canPlaceAt(worldIn, pos, lvt_4_1_))
            {
                return true;
            }
        }

        return false;
    }

    private boolean canPlaceAt(World worldIn, BlockPos pos, EnumFacing facing)
    {
        BlockPos lvt_4_1_ = pos.offset(facing.getOpposite());
        boolean lvt_5_1_ = facing.getAxis().isHorizontal();
        return lvt_5_1_ && worldIn.isBlockNormalCube(lvt_4_1_, true) || facing.equals(EnumFacing.UP) && this.canPlaceOn(worldIn, lvt_4_1_);
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        if (this.canPlaceAt(worldIn, pos, facing))
        {
            return this.getDefaultState().withProperty(FACING, facing);
        }
        else
        {
            for (EnumFacing lvt_10_1_ : EnumFacing.Plane.HORIZONTAL)
            {
                if (worldIn.isBlockNormalCube(pos.offset(lvt_10_1_.getOpposite()), true))
                {
                    return this.getDefaultState().withProperty(FACING, lvt_10_1_);
                }
            }

            return this.getDefaultState();
        }
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        this.checkForDrop(worldIn, pos, state);
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        this.onNeighborChangeInternal(worldIn, pos, state);
    }

    protected boolean onNeighborChangeInternal(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!this.checkForDrop(worldIn, pos, state))
        {
            return true;
        }
        else
        {
            EnumFacing lvt_4_1_ = (EnumFacing)state.getValue(FACING);
            EnumFacing.Axis lvt_5_1_ = lvt_4_1_.getAxis();
            EnumFacing lvt_6_1_ = lvt_4_1_.getOpposite();
            boolean lvt_7_1_ = false;

            if (lvt_5_1_.isHorizontal() && !worldIn.isBlockNormalCube(pos.offset(lvt_6_1_), true))
            {
                lvt_7_1_ = true;
            }
            else if (lvt_5_1_.isVertical() && !this.canPlaceOn(worldIn, pos.offset(lvt_6_1_)))
            {
                lvt_7_1_ = true;
            }

            if (lvt_7_1_)
            {
                this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    protected boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state)
    {
        if (state.getBlock() == this && this.canPlaceAt(worldIn, pos, (EnumFacing)state.getValue(FACING)))
        {
            return true;
        }
        else
        {
            if (worldIn.getBlockState(pos).getBlock() == this)
            {
                this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
            }

            return false;
        }
    }

    /**
     * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit.
     */
    public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end)
    {
        EnumFacing lvt_5_1_ = (EnumFacing)worldIn.getBlockState(pos).getValue(FACING);
        float lvt_6_1_ = 0.15F;

        if (lvt_5_1_ == EnumFacing.EAST)
        {
            this.setBlockBounds(0.0F, 0.2F, 0.5F - lvt_6_1_, lvt_6_1_ * 2.0F, 0.8F, 0.5F + lvt_6_1_);
        }
        else if (lvt_5_1_ == EnumFacing.WEST)
        {
            this.setBlockBounds(1.0F - lvt_6_1_ * 2.0F, 0.2F, 0.5F - lvt_6_1_, 1.0F, 0.8F, 0.5F + lvt_6_1_);
        }
        else if (lvt_5_1_ == EnumFacing.SOUTH)
        {
            this.setBlockBounds(0.5F - lvt_6_1_, 0.2F, 0.0F, 0.5F + lvt_6_1_, 0.8F, lvt_6_1_ * 2.0F);
        }
        else if (lvt_5_1_ == EnumFacing.NORTH)
        {
            this.setBlockBounds(0.5F - lvt_6_1_, 0.2F, 1.0F - lvt_6_1_ * 2.0F, 0.5F + lvt_6_1_, 0.8F, 1.0F);
        }
        else
        {
            lvt_6_1_ = 0.1F;
            this.setBlockBounds(0.5F - lvt_6_1_, 0.0F, 0.5F - lvt_6_1_, 0.5F + lvt_6_1_, 0.6F, 0.5F + lvt_6_1_);
        }

        return super.collisionRayTrace(worldIn, pos, start, end);
    }

    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        EnumFacing lvt_5_1_ = (EnumFacing)state.getValue(FACING);
        double lvt_6_1_ = (double)pos.getX() + 0.5D;
        double lvt_8_1_ = (double)pos.getY() + 0.7D;
        double lvt_10_1_ = (double)pos.getZ() + 0.5D;
        double lvt_12_1_ = 0.22D;
        double lvt_14_1_ = 0.27D;

        if (lvt_5_1_.getAxis().isHorizontal())
        {
            EnumFacing lvt_16_1_ = lvt_5_1_.getOpposite();
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, lvt_6_1_ + lvt_14_1_ * (double)lvt_16_1_.getFrontOffsetX(), lvt_8_1_ + lvt_12_1_, lvt_10_1_ + lvt_14_1_ * (double)lvt_16_1_.getFrontOffsetZ(), 0.0D, 0.0D, 0.0D, new int[0]);
            worldIn.spawnParticle(EnumParticleTypes.FLAME, lvt_6_1_ + lvt_14_1_ * (double)lvt_16_1_.getFrontOffsetX(), lvt_8_1_ + lvt_12_1_, lvt_10_1_ + lvt_14_1_ * (double)lvt_16_1_.getFrontOffsetZ(), 0.0D, 0.0D, 0.0D, new int[0]);
        }
        else
        {
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, lvt_6_1_, lvt_8_1_, lvt_10_1_, 0.0D, 0.0D, 0.0D, new int[0]);
            worldIn.spawnParticle(EnumParticleTypes.FLAME, lvt_6_1_, lvt_8_1_, lvt_10_1_, 0.0D, 0.0D, 0.0D, new int[0]);
        }
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
        IBlockState lvt_2_1_ = this.getDefaultState();

        switch (meta)
        {
            case 1:
                lvt_2_1_ = lvt_2_1_.withProperty(FACING, EnumFacing.EAST);
                break;

            case 2:
                lvt_2_1_ = lvt_2_1_.withProperty(FACING, EnumFacing.WEST);
                break;

            case 3:
                lvt_2_1_ = lvt_2_1_.withProperty(FACING, EnumFacing.SOUTH);
                break;

            case 4:
                lvt_2_1_ = lvt_2_1_.withProperty(FACING, EnumFacing.NORTH);
                break;

            case 5:
            default:
                lvt_2_1_ = lvt_2_1_.withProperty(FACING, EnumFacing.UP);
        }

        return lvt_2_1_;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int lvt_2_1_ = 0;

        switch ((EnumFacing)state.getValue(FACING))
        {
            case EAST:
                lvt_2_1_ = lvt_2_1_ | 1;
                break;

            case WEST:
                lvt_2_1_ = lvt_2_1_ | 2;
                break;

            case SOUTH:
                lvt_2_1_ = lvt_2_1_ | 3;
                break;

            case NORTH:
                lvt_2_1_ = lvt_2_1_ | 4;
                break;

            case DOWN:
            case UP:
            default:
                lvt_2_1_ = lvt_2_1_ | 5;
        }

        return lvt_2_1_;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {FACING});
    }
}
