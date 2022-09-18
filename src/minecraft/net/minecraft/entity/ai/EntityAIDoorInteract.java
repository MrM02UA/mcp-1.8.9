package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.BlockPos;

public abstract class EntityAIDoorInteract extends EntityAIBase
{
    protected EntityLiving theEntity;
    protected BlockPos doorPosition = BlockPos.ORIGIN;

    /** The wooden door block */
    protected BlockDoor doorBlock;

    /**
     * If is true then the Entity has stopped Door Interaction and compoleted the task.
     */
    boolean hasStoppedDoorInteraction;
    float entityPositionX;
    float entityPositionZ;

    public EntityAIDoorInteract(EntityLiving entityIn)
    {
        this.theEntity = entityIn;

        if (!(entityIn.getNavigator() instanceof PathNavigateGround))
        {
            throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
        }
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!this.theEntity.isCollidedHorizontally)
        {
            return false;
        }
        else
        {
            PathNavigateGround lvt_1_1_ = (PathNavigateGround)this.theEntity.getNavigator();
            PathEntity lvt_2_1_ = lvt_1_1_.getPath();

            if (lvt_2_1_ != null && !lvt_2_1_.isFinished() && lvt_1_1_.getEnterDoors())
            {
                for (int lvt_3_1_ = 0; lvt_3_1_ < Math.min(lvt_2_1_.getCurrentPathIndex() + 2, lvt_2_1_.getCurrentPathLength()); ++lvt_3_1_)
                {
                    PathPoint lvt_4_1_ = lvt_2_1_.getPathPointFromIndex(lvt_3_1_);
                    this.doorPosition = new BlockPos(lvt_4_1_.xCoord, lvt_4_1_.yCoord + 1, lvt_4_1_.zCoord);

                    if (this.theEntity.getDistanceSq((double)this.doorPosition.getX(), this.theEntity.posY, (double)this.doorPosition.getZ()) <= 2.25D)
                    {
                        this.doorBlock = this.getBlockDoor(this.doorPosition);

                        if (this.doorBlock != null)
                        {
                            return true;
                        }
                    }
                }

                this.doorPosition = (new BlockPos(this.theEntity)).up();
                this.doorBlock = this.getBlockDoor(this.doorPosition);
                return this.doorBlock != null;
            }
            else
            {
                return false;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.hasStoppedDoorInteraction;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.hasStoppedDoorInteraction = false;
        this.entityPositionX = (float)((double)((float)this.doorPosition.getX() + 0.5F) - this.theEntity.posX);
        this.entityPositionZ = (float)((double)((float)this.doorPosition.getZ() + 0.5F) - this.theEntity.posZ);
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        float lvt_1_1_ = (float)((double)((float)this.doorPosition.getX() + 0.5F) - this.theEntity.posX);
        float lvt_2_1_ = (float)((double)((float)this.doorPosition.getZ() + 0.5F) - this.theEntity.posZ);
        float lvt_3_1_ = this.entityPositionX * lvt_1_1_ + this.entityPositionZ * lvt_2_1_;

        if (lvt_3_1_ < 0.0F)
        {
            this.hasStoppedDoorInteraction = true;
        }
    }

    private BlockDoor getBlockDoor(BlockPos pos)
    {
        Block lvt_2_1_ = this.theEntity.worldObj.getBlockState(pos).getBlock();
        return lvt_2_1_ instanceof BlockDoor && lvt_2_1_.getMaterial() == Material.wood ? (BlockDoor)lvt_2_1_ : null;
    }
}
