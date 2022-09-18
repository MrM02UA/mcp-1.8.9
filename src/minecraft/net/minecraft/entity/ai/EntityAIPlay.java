package net.minecraft.entity.ai;

import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.Vec3;

public class EntityAIPlay extends EntityAIBase
{
    private EntityVillager villagerObj;
    private EntityLivingBase targetVillager;
    private double speed;
    private int playTime;

    public EntityAIPlay(EntityVillager villagerObjIn, double speedIn)
    {
        this.villagerObj = villagerObjIn;
        this.speed = speedIn;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.villagerObj.getGrowingAge() >= 0)
        {
            return false;
        }
        else if (this.villagerObj.getRNG().nextInt(400) != 0)
        {
            return false;
        }
        else
        {
            List<EntityVillager> lvt_1_1_ = this.villagerObj.worldObj.<EntityVillager>getEntitiesWithinAABB(EntityVillager.class, this.villagerObj.getEntityBoundingBox().expand(6.0D, 3.0D, 6.0D));
            double lvt_2_1_ = Double.MAX_VALUE;

            for (EntityVillager lvt_5_1_ : lvt_1_1_)
            {
                if (lvt_5_1_ != this.villagerObj && !lvt_5_1_.isPlaying() && lvt_5_1_.getGrowingAge() < 0)
                {
                    double lvt_6_1_ = lvt_5_1_.getDistanceSqToEntity(this.villagerObj);

                    if (lvt_6_1_ <= lvt_2_1_)
                    {
                        lvt_2_1_ = lvt_6_1_;
                        this.targetVillager = lvt_5_1_;
                    }
                }
            }

            if (this.targetVillager == null)
            {
                Vec3 lvt_4_2_ = RandomPositionGenerator.findRandomTarget(this.villagerObj, 16, 3);

                if (lvt_4_2_ == null)
                {
                    return false;
                }
            }

            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.playTime > 0;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        if (this.targetVillager != null)
        {
            this.villagerObj.setPlaying(true);
        }

        this.playTime = 1000;
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.villagerObj.setPlaying(false);
        this.targetVillager = null;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        --this.playTime;

        if (this.targetVillager != null)
        {
            if (this.villagerObj.getDistanceSqToEntity(this.targetVillager) > 4.0D)
            {
                this.villagerObj.getNavigator().tryMoveToEntityLiving(this.targetVillager, this.speed);
            }
        }
        else if (this.villagerObj.getNavigator().noPath())
        {
            Vec3 lvt_1_1_ = RandomPositionGenerator.findRandomTarget(this.villagerObj, 16, 3);

            if (lvt_1_1_ == null)
            {
                return;
            }

            this.villagerObj.getNavigator().tryMoveToXYZ(lvt_1_1_.xCoord, lvt_1_1_.yCoord, lvt_1_1_.zCoord, this.speed);
        }
    }
}
