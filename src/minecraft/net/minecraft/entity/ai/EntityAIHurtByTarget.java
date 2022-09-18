package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;

public class EntityAIHurtByTarget extends EntityAITarget
{
    private boolean entityCallsForHelp;

    /** Store the previous revengeTimer value */
    private int revengeTimerOld;
    private final Class[] targetClasses;

    public EntityAIHurtByTarget(EntityCreature creatureIn, boolean entityCallsForHelpIn, Class... targetClassesIn)
    {
        super(creatureIn, false);
        this.entityCallsForHelp = entityCallsForHelpIn;
        this.targetClasses = targetClassesIn;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        int lvt_1_1_ = this.taskOwner.getRevengeTimer();
        return lvt_1_1_ != this.revengeTimerOld && this.isSuitableTarget(this.taskOwner.getAITarget(), false);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.taskOwner.getAITarget());
        this.revengeTimerOld = this.taskOwner.getRevengeTimer();

        if (this.entityCallsForHelp)
        {
            double lvt_1_1_ = this.getTargetDistance();

            for (EntityCreature lvt_5_1_ : this.taskOwner.worldObj.getEntitiesWithinAABB(this.taskOwner.getClass(), (new AxisAlignedBB(this.taskOwner.posX, this.taskOwner.posY, this.taskOwner.posZ, this.taskOwner.posX + 1.0D, this.taskOwner.posY + 1.0D, this.taskOwner.posZ + 1.0D)).expand(lvt_1_1_, 10.0D, lvt_1_1_)))
            {
                if (this.taskOwner != lvt_5_1_ && lvt_5_1_.getAttackTarget() == null && !lvt_5_1_.isOnSameTeam(this.taskOwner.getAITarget()))
                {
                    boolean lvt_6_1_ = false;

                    for (Class lvt_10_1_ : this.targetClasses)
                    {
                        if (lvt_5_1_.getClass() == lvt_10_1_)
                        {
                            lvt_6_1_ = true;
                            break;
                        }
                    }

                    if (!lvt_6_1_)
                    {
                        this.setEntityAttackTarget(lvt_5_1_, this.taskOwner.getAITarget());
                    }
                }
            }
        }

        super.startExecuting();
    }

    protected void setEntityAttackTarget(EntityCreature creatureIn, EntityLivingBase entityLivingBaseIn)
    {
        creatureIn.setAttackTarget(entityLivingBaseIn);
    }
}
