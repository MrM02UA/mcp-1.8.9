package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

public class EntityAIRestrictOpenDoor extends EntityAIBase
{
    private EntityCreature entityObj;
    private VillageDoorInfo frontDoor;

    public EntityAIRestrictOpenDoor(EntityCreature creatureIn)
    {
        this.entityObj = creatureIn;

        if (!(creatureIn.getNavigator() instanceof PathNavigateGround))
        {
            throw new IllegalArgumentException("Unsupported mob type for RestrictOpenDoorGoal");
        }
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.entityObj.worldObj.isDaytime())
        {
            return false;
        }
        else
        {
            BlockPos lvt_1_1_ = new BlockPos(this.entityObj);
            Village lvt_2_1_ = this.entityObj.worldObj.getVillageCollection().getNearestVillage(lvt_1_1_, 16);

            if (lvt_2_1_ == null)
            {
                return false;
            }
            else
            {
                this.frontDoor = lvt_2_1_.getNearestDoor(lvt_1_1_);
                return this.frontDoor == null ? false : (double)this.frontDoor.getDistanceToInsideBlockSq(lvt_1_1_) < 2.25D;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.entityObj.worldObj.isDaytime() ? false : !this.frontDoor.getIsDetachedFromVillageFlag() && this.frontDoor.func_179850_c(new BlockPos(this.entityObj));
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        ((PathNavigateGround)this.entityObj.getNavigator()).setBreakDoors(false);
        ((PathNavigateGround)this.entityObj.getNavigator()).setEnterDoors(false);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        ((PathNavigateGround)this.entityObj.getNavigator()).setBreakDoors(true);
        ((PathNavigateGround)this.entityObj.getNavigator()).setEnterDoors(true);
        this.frontDoor = null;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        this.frontDoor.incrementDoorOpeningRestrictionCounter();
    }
}
