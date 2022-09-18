package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityAIOcelotSit extends EntityAIMoveToBlock
{
    private final EntityOcelot ocelot;

    public EntityAIOcelotSit(EntityOcelot ocelotIn, double p_i45315_2_)
    {
        super(ocelotIn, p_i45315_2_, 8);
        this.ocelot = ocelotIn;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        return this.ocelot.isTamed() && !this.ocelot.isSitting() && super.shouldExecute();
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return super.continueExecuting();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        super.startExecuting();
        this.ocelot.getAISit().setSitting(false);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        super.resetTask();
        this.ocelot.setSitting(false);
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        super.updateTask();
        this.ocelot.getAISit().setSitting(false);

        if (!this.getIsAboveDestination())
        {
            this.ocelot.setSitting(false);
        }
        else if (!this.ocelot.isSitting())
        {
            this.ocelot.setSitting(true);
        }
    }

    /**
     * Return true to set given position as destination
     */
    protected boolean shouldMoveTo(World worldIn, BlockPos pos)
    {
        if (!worldIn.isAirBlock(pos.up()))
        {
            return false;
        }
        else
        {
            IBlockState lvt_3_1_ = worldIn.getBlockState(pos);
            Block lvt_4_1_ = lvt_3_1_.getBlock();

            if (lvt_4_1_ == Blocks.chest)
            {
                TileEntity lvt_5_1_ = worldIn.getTileEntity(pos);

                if (lvt_5_1_ instanceof TileEntityChest && ((TileEntityChest)lvt_5_1_).numPlayersUsing < 1)
                {
                    return true;
                }
            }
            else
            {
                if (lvt_4_1_ == Blocks.lit_furnace)
                {
                    return true;
                }

                if (lvt_4_1_ == Blocks.bed && lvt_3_1_.getValue(BlockBed.PART) != BlockBed.EnumPartType.HEAD)
                {
                    return true;
                }
            }

            return false;
        }
    }
}
