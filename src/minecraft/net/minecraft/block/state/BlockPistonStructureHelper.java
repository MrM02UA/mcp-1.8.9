package net.minecraft.block.state;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockPistonStructureHelper
{
    private final World world;
    private final BlockPos pistonPos;
    private final BlockPos blockToMove;
    private final EnumFacing moveDirection;
    private final List<BlockPos> toMove = Lists.newArrayList();
    private final List<BlockPos> toDestroy = Lists.newArrayList();

    public BlockPistonStructureHelper(World worldIn, BlockPos posIn, EnumFacing pistonFacing, boolean extending)
    {
        this.world = worldIn;
        this.pistonPos = posIn;

        if (extending)
        {
            this.moveDirection = pistonFacing;
            this.blockToMove = posIn.offset(pistonFacing);
        }
        else
        {
            this.moveDirection = pistonFacing.getOpposite();
            this.blockToMove = posIn.offset(pistonFacing, 2);
        }
    }

    public boolean canMove()
    {
        this.toMove.clear();
        this.toDestroy.clear();
        Block lvt_1_1_ = this.world.getBlockState(this.blockToMove).getBlock();

        if (!BlockPistonBase.canPush(lvt_1_1_, this.world, this.blockToMove, this.moveDirection, false))
        {
            if (lvt_1_1_.getMobilityFlag() != 1)
            {
                return false;
            }
            else
            {
                this.toDestroy.add(this.blockToMove);
                return true;
            }
        }
        else if (!this.func_177251_a(this.blockToMove))
        {
            return false;
        }
        else
        {
            for (int lvt_2_1_ = 0; lvt_2_1_ < this.toMove.size(); ++lvt_2_1_)
            {
                BlockPos lvt_3_1_ = (BlockPos)this.toMove.get(lvt_2_1_);

                if (this.world.getBlockState(lvt_3_1_).getBlock() == Blocks.slime_block && !this.func_177250_b(lvt_3_1_))
                {
                    return false;
                }
            }

            return true;
        }
    }

    private boolean func_177251_a(BlockPos origin)
    {
        Block lvt_2_1_ = this.world.getBlockState(origin).getBlock();

        if (lvt_2_1_.getMaterial() == Material.air)
        {
            return true;
        }
        else if (!BlockPistonBase.canPush(lvt_2_1_, this.world, origin, this.moveDirection, false))
        {
            return true;
        }
        else if (origin.equals(this.pistonPos))
        {
            return true;
        }
        else if (this.toMove.contains(origin))
        {
            return true;
        }
        else
        {
            int lvt_3_1_ = 1;

            if (lvt_3_1_ + this.toMove.size() > 12)
            {
                return false;
            }
            else
            {
                while (lvt_2_1_ == Blocks.slime_block)
                {
                    BlockPos lvt_4_1_ = origin.offset(this.moveDirection.getOpposite(), lvt_3_1_);
                    lvt_2_1_ = this.world.getBlockState(lvt_4_1_).getBlock();

                    if (lvt_2_1_.getMaterial() == Material.air || !BlockPistonBase.canPush(lvt_2_1_, this.world, lvt_4_1_, this.moveDirection, false) || lvt_4_1_.equals(this.pistonPos))
                    {
                        break;
                    }

                    ++lvt_3_1_;

                    if (lvt_3_1_ + this.toMove.size() > 12)
                    {
                        return false;
                    }
                }

                int lvt_4_2_ = 0;

                for (int lvt_5_1_ = lvt_3_1_ - 1; lvt_5_1_ >= 0; --lvt_5_1_)
                {
                    this.toMove.add(origin.offset(this.moveDirection.getOpposite(), lvt_5_1_));
                    ++lvt_4_2_;
                }

                int lvt_5_2_ = 1;

                while (true)
                {
                    BlockPos lvt_6_1_ = origin.offset(this.moveDirection, lvt_5_2_);
                    int lvt_7_1_ = this.toMove.indexOf(lvt_6_1_);

                    if (lvt_7_1_ > -1)
                    {
                        this.func_177255_a(lvt_4_2_, lvt_7_1_);

                        for (int lvt_8_1_ = 0; lvt_8_1_ <= lvt_7_1_ + lvt_4_2_; ++lvt_8_1_)
                        {
                            BlockPos lvt_9_1_ = (BlockPos)this.toMove.get(lvt_8_1_);

                            if (this.world.getBlockState(lvt_9_1_).getBlock() == Blocks.slime_block && !this.func_177250_b(lvt_9_1_))
                            {
                                return false;
                            }
                        }

                        return true;
                    }

                    lvt_2_1_ = this.world.getBlockState(lvt_6_1_).getBlock();

                    if (lvt_2_1_.getMaterial() == Material.air)
                    {
                        return true;
                    }

                    if (!BlockPistonBase.canPush(lvt_2_1_, this.world, lvt_6_1_, this.moveDirection, true) || lvt_6_1_.equals(this.pistonPos))
                    {
                        return false;
                    }

                    if (lvt_2_1_.getMobilityFlag() == 1)
                    {
                        this.toDestroy.add(lvt_6_1_);
                        return true;
                    }

                    if (this.toMove.size() >= 12)
                    {
                        return false;
                    }

                    this.toMove.add(lvt_6_1_);
                    ++lvt_4_2_;
                    ++lvt_5_2_;
                }
            }
        }
    }

    private void func_177255_a(int p_177255_1_, int p_177255_2_)
    {
        List<BlockPos> lvt_3_1_ = Lists.newArrayList();
        List<BlockPos> lvt_4_1_ = Lists.newArrayList();
        List<BlockPos> lvt_5_1_ = Lists.newArrayList();
        lvt_3_1_.addAll(this.toMove.subList(0, p_177255_2_));
        lvt_4_1_.addAll(this.toMove.subList(this.toMove.size() - p_177255_1_, this.toMove.size()));
        lvt_5_1_.addAll(this.toMove.subList(p_177255_2_, this.toMove.size() - p_177255_1_));
        this.toMove.clear();
        this.toMove.addAll(lvt_3_1_);
        this.toMove.addAll(lvt_4_1_);
        this.toMove.addAll(lvt_5_1_);
    }

    private boolean func_177250_b(BlockPos p_177250_1_)
    {
        for (EnumFacing lvt_5_1_ : EnumFacing.values())
        {
            if (lvt_5_1_.getAxis() != this.moveDirection.getAxis() && !this.func_177251_a(p_177250_1_.offset(lvt_5_1_)))
            {
                return false;
            }
        }

        return true;
    }

    public List<BlockPos> getBlocksToMove()
    {
        return this.toMove;
    }

    public List<BlockPos> getBlocksToDestroy()
    {
        return this.toDestroy;
    }
}
