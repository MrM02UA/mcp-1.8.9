package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;

public class EntityAIOwnerHurtTarget extends EntityAITarget
{
    EntityTameable theEntityTameable;
    EntityLivingBase theTarget;
    private int field_142050_e;

    public EntityAIOwnerHurtTarget(EntityTameable theEntityTameableIn)
    {
        super(theEntityTameableIn, false);
        this.theEntityTameable = theEntityTameableIn;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!this.theEntityTameable.isTamed())
        {
            return false;
        }
        else
        {
            EntityLivingBase lvt_1_1_ = this.theEntityTameable.getOwner();

            if (lvt_1_1_ == null)
            {
                return false;
            }
            else
            {
                this.theTarget = lvt_1_1_.getLastAttacker();
                int lvt_2_1_ = lvt_1_1_.getLastAttackerTime();
                return lvt_2_1_ != this.field_142050_e && this.isSuitableTarget(this.theTarget, false) && this.theEntityTameable.shouldAttackEntity(this.theTarget, lvt_1_1_);
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.theTarget);
        EntityLivingBase lvt_1_1_ = this.theEntityTameable.getOwner();

        if (lvt_1_1_ != null)
        {
            this.field_142050_e = lvt_1_1_.getLastAttackerTime();
        }

        super.startExecuting();
    }
}
