package net.minecraft.entity.ai;

import java.util.List;
import net.minecraft.entity.passive.EntityAnimal;

public class EntityAIFollowParent extends EntityAIBase
{
    /** The child that is following its parent. */
    EntityAnimal childAnimal;
    EntityAnimal parentAnimal;
    double moveSpeed;
    private int delayCounter;

    public EntityAIFollowParent(EntityAnimal animal, double speed)
    {
        this.childAnimal = animal;
        this.moveSpeed = speed;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.childAnimal.getGrowingAge() >= 0)
        {
            return false;
        }
        else
        {
            List<EntityAnimal> lvt_1_1_ = this.childAnimal.worldObj.<EntityAnimal>getEntitiesWithinAABB(this.childAnimal.getClass(), this.childAnimal.getEntityBoundingBox().expand(8.0D, 4.0D, 8.0D));
            EntityAnimal lvt_2_1_ = null;
            double lvt_3_1_ = Double.MAX_VALUE;

            for (EntityAnimal lvt_6_1_ : lvt_1_1_)
            {
                if (lvt_6_1_.getGrowingAge() >= 0)
                {
                    double lvt_7_1_ = this.childAnimal.getDistanceSqToEntity(lvt_6_1_);

                    if (lvt_7_1_ <= lvt_3_1_)
                    {
                        lvt_3_1_ = lvt_7_1_;
                        lvt_2_1_ = lvt_6_1_;
                    }
                }
            }

            if (lvt_2_1_ == null)
            {
                return false;
            }
            else if (lvt_3_1_ < 9.0D)
            {
                return false;
            }
            else
            {
                this.parentAnimal = lvt_2_1_;
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        if (this.childAnimal.getGrowingAge() >= 0)
        {
            return false;
        }
        else if (!this.parentAnimal.isEntityAlive())
        {
            return false;
        }
        else
        {
            double lvt_1_1_ = this.childAnimal.getDistanceSqToEntity(this.parentAnimal);
            return lvt_1_1_ >= 9.0D && lvt_1_1_ <= 256.0D;
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.delayCounter = 0;
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.parentAnimal = null;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        if (--this.delayCounter <= 0)
        {
            this.delayCounter = 10;
            this.childAnimal.getNavigator().tryMoveToEntityLiving(this.parentAnimal, this.moveSpeed);
        }
    }
}
