package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;

public class EntityAIOwnerHurtByTarget extends EntityAITarget
{
    EntityTameable theDefendingTameable;
    EntityLivingBase theOwnerAttacker;
    private int field_142051_e;

    public EntityAIOwnerHurtByTarget(EntityTameable theDefendingTameableIn)
    {
        super(theDefendingTameableIn, false);
        this.theDefendingTameable = theDefendingTameableIn;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!this.theDefendingTameable.isTamed())
        {
            return false;
        }
        else
        {
            EntityLivingBase lvt_1_1_ = this.theDefendingTameable.getOwner();

            if (lvt_1_1_ == null)
            {
                return false;
            }
            else
            {
                this.theOwnerAttacker = lvt_1_1_.getAITarget();
                int lvt_2_1_ = lvt_1_1_.getRevengeTimer();
                return lvt_2_1_ != this.field_142051_e && this.isSuitableTarget(this.theOwnerAttacker, false) && this.theDefendingTameable.shouldAttackEntity(this.theOwnerAttacker, lvt_1_1_);
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.theOwnerAttacker);
        EntityLivingBase lvt_1_1_ = this.theDefendingTameable.getOwner();

        if (lvt_1_1_ != null)
        {
            this.field_142051_e = lvt_1_1_.getRevengeTimer();
        }

        super.startExecuting();
    }
}
