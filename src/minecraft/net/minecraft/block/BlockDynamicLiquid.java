package net.minecraft.block;

import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockDynamicLiquid extends BlockLiquid
{
    int adjacentSourceBlocks;

    protected BlockDynamicLiquid(Material materialIn)
    {
        super(materialIn);
    }

    private void placeStaticBlock(World worldIn, BlockPos pos, IBlockState currentState)
    {
        worldIn.setBlockState(pos, getStaticBlock(this.blockMaterial).getDefaultState().withProperty(LEVEL, currentState.getValue(LEVEL)), 2);
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        int lvt_5_1_ = ((Integer)state.getValue(LEVEL)).intValue();
        int lvt_6_1_ = 1;

        if (this.blockMaterial == Material.lava && !worldIn.provider.doesWaterVaporize())
        {
            lvt_6_1_ = 2;
        }

        int lvt_7_1_ = this.tickRate(worldIn);

        if (lvt_5_1_ > 0)
        {
            int lvt_8_1_ = -100;
            this.adjacentSourceBlocks = 0;

            for (EnumFacing lvt_10_1_ : EnumFacing.Plane.HORIZONTAL)
            {
                lvt_8_1_ = this.checkAdjacentBlock(worldIn, pos.offset(lvt_10_1_), lvt_8_1_);
            }

            int lvt_9_2_ = lvt_8_1_ + lvt_6_1_;

            if (lvt_9_2_ >= 8 || lvt_8_1_ < 0)
            {
                lvt_9_2_ = -1;
            }

            if (this.getLevel(worldIn, pos.up()) >= 0)
            {
                int lvt_10_2_ = this.getLevel(worldIn, pos.up());

                if (lvt_10_2_ >= 8)
                {
                    lvt_9_2_ = lvt_10_2_;
                }
                else
                {
                    lvt_9_2_ = lvt_10_2_ + 8;
                }
            }

            if (this.adjacentSourceBlocks >= 2 && this.blockMaterial == Material.water)
            {
                IBlockState lvt_10_3_ = worldIn.getBlockState(pos.down());

                if (lvt_10_3_.getBlock().getMaterial().isSolid())
                {
                    lvt_9_2_ = 0;
                }
                else if (lvt_10_3_.getBlock().getMaterial() == this.blockMaterial && ((Integer)lvt_10_3_.getValue(LEVEL)).intValue() == 0)
                {
                    lvt_9_2_ = 0;
                }
            }

            if (this.blockMaterial == Material.lava && lvt_5_1_ < 8 && lvt_9_2_ < 8 && lvt_9_2_ > lvt_5_1_ && rand.nextInt(4) != 0)
            {
                lvt_7_1_ *= 4;
            }

            if (lvt_9_2_ == lvt_5_1_)
            {
                this.placeStaticBlock(worldIn, pos, state);
            }
            else
            {
                lvt_5_1_ = lvt_9_2_;

                if (lvt_9_2_ < 0)
                {
                    worldIn.setBlockToAir(pos);
                }
                else
                {
                    state = state.withProperty(LEVEL, Integer.valueOf(lvt_9_2_));
                    worldIn.setBlockState(pos, state, 2);
                    worldIn.scheduleUpdate(pos, this, lvt_7_1_);
                    worldIn.notifyNeighborsOfStateChange(pos, this);
                }
            }
        }
        else
        {
            this.placeStaticBlock(worldIn, pos, state);
        }

        IBlockState lvt_8_2_ = worldIn.getBlockState(pos.down());

        if (this.canFlowInto(worldIn, pos.down(), lvt_8_2_))
        {
            if (this.blockMaterial == Material.lava && worldIn.getBlockState(pos.down()).getBlock().getMaterial() == Material.water)
            {
                worldIn.setBlockState(pos.down(), Blocks.stone.getDefaultState());
                this.triggerMixEffects(worldIn, pos.down());
                return;
            }

            if (lvt_5_1_ >= 8)
            {
                this.tryFlowInto(worldIn, pos.down(), lvt_8_2_, lvt_5_1_);
            }
            else
            {
                this.tryFlowInto(worldIn, pos.down(), lvt_8_2_, lvt_5_1_ + 8);
            }
        }
        else if (lvt_5_1_ >= 0 && (lvt_5_1_ == 0 || this.isBlocked(worldIn, pos.down(), lvt_8_2_)))
        {
            Set<EnumFacing> lvt_9_3_ = this.getPossibleFlowDirections(worldIn, pos);
            int lvt_10_4_ = lvt_5_1_ + lvt_6_1_;

            if (lvt_5_1_ >= 8)
            {
                lvt_10_4_ = 1;
            }

            if (lvt_10_4_ >= 8)
            {
                return;
            }

            for (EnumFacing lvt_12_1_ : lvt_9_3_)
            {
                this.tryFlowInto(worldIn, pos.offset(lvt_12_1_), worldIn.getBlockState(pos.offset(lvt_12_1_)), lvt_10_4_);
            }
        }
    }

    private void tryFlowInto(World worldIn, BlockPos pos, IBlockState state, int level)
    {
        if (this.canFlowInto(worldIn, pos, state))
        {
            if (state.getBlock() != Blocks.air)
            {
                if (this.blockMaterial == Material.lava)
                {
                    this.triggerMixEffects(worldIn, pos);
                }
                else
                {
                    state.getBlock().dropBlockAsItem(worldIn, pos, state, 0);
                }
            }

            worldIn.setBlockState(pos, this.getDefaultState().withProperty(LEVEL, Integer.valueOf(level)), 3);
        }
    }

    private int func_176374_a(World worldIn, BlockPos pos, int distance, EnumFacing calculateFlowCost)
    {
        int lvt_5_1_ = 1000;

        for (EnumFacing lvt_7_1_ : EnumFacing.Plane.HORIZONTAL)
        {
            if (lvt_7_1_ != calculateFlowCost)
            {
                BlockPos lvt_8_1_ = pos.offset(lvt_7_1_);
                IBlockState lvt_9_1_ = worldIn.getBlockState(lvt_8_1_);

                if (!this.isBlocked(worldIn, lvt_8_1_, lvt_9_1_) && (lvt_9_1_.getBlock().getMaterial() != this.blockMaterial || ((Integer)lvt_9_1_.getValue(LEVEL)).intValue() > 0))
                {
                    if (!this.isBlocked(worldIn, lvt_8_1_.down(), lvt_9_1_))
                    {
                        return distance;
                    }

                    if (distance < 4)
                    {
                        int lvt_10_1_ = this.func_176374_a(worldIn, lvt_8_1_, distance + 1, lvt_7_1_.getOpposite());

                        if (lvt_10_1_ < lvt_5_1_)
                        {
                            lvt_5_1_ = lvt_10_1_;
                        }
                    }
                }
            }
        }

        return lvt_5_1_;
    }

    private Set<EnumFacing> getPossibleFlowDirections(World worldIn, BlockPos pos)
    {
        int lvt_3_1_ = 1000;
        Set<EnumFacing> lvt_4_1_ = EnumSet.noneOf(EnumFacing.class);

        for (EnumFacing lvt_6_1_ : EnumFacing.Plane.HORIZONTAL)
        {
            BlockPos lvt_7_1_ = pos.offset(lvt_6_1_);
            IBlockState lvt_8_1_ = worldIn.getBlockState(lvt_7_1_);

            if (!this.isBlocked(worldIn, lvt_7_1_, lvt_8_1_) && (lvt_8_1_.getBlock().getMaterial() != this.blockMaterial || ((Integer)lvt_8_1_.getValue(LEVEL)).intValue() > 0))
            {
                int lvt_9_1_;

                if (this.isBlocked(worldIn, lvt_7_1_.down(), worldIn.getBlockState(lvt_7_1_.down())))
                {
                    lvt_9_1_ = this.func_176374_a(worldIn, lvt_7_1_, 1, lvt_6_1_.getOpposite());
                }
                else
                {
                    lvt_9_1_ = 0;
                }

                if (lvt_9_1_ < lvt_3_1_)
                {
                    lvt_4_1_.clear();
                }

                if (lvt_9_1_ <= lvt_3_1_)
                {
                    lvt_4_1_.add(lvt_6_1_);
                    lvt_3_1_ = lvt_9_1_;
                }
            }
        }

        return lvt_4_1_;
    }

    private boolean isBlocked(World worldIn, BlockPos pos, IBlockState state)
    {
        Block lvt_4_1_ = worldIn.getBlockState(pos).getBlock();
        return !(lvt_4_1_ instanceof BlockDoor) && lvt_4_1_ != Blocks.standing_sign && lvt_4_1_ != Blocks.ladder && lvt_4_1_ != Blocks.reeds ? (lvt_4_1_.blockMaterial == Material.portal ? true : lvt_4_1_.blockMaterial.blocksMovement()) : true;
    }

    protected int checkAdjacentBlock(World worldIn, BlockPos pos, int currentMinLevel)
    {
        int lvt_4_1_ = this.getLevel(worldIn, pos);

        if (lvt_4_1_ < 0)
        {
            return currentMinLevel;
        }
        else
        {
            if (lvt_4_1_ == 0)
            {
                ++this.adjacentSourceBlocks;
            }

            if (lvt_4_1_ >= 8)
            {
                lvt_4_1_ = 0;
            }

            return currentMinLevel >= 0 && lvt_4_1_ >= currentMinLevel ? currentMinLevel : lvt_4_1_;
        }
    }

    private boolean canFlowInto(World worldIn, BlockPos pos, IBlockState state)
    {
        Material lvt_4_1_ = state.getBlock().getMaterial();
        return lvt_4_1_ != this.blockMaterial && lvt_4_1_ != Material.lava && !this.isBlocked(worldIn, pos, state);
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!this.checkForMixing(worldIn, pos, state))
        {
            worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
        }
    }
}
