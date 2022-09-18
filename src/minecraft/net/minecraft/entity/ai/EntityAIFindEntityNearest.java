package net.minecraft.entity.ai;

import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAIFindEntityNearest extends EntityAIBase
{
    private static final Logger LOGGER = LogManager.getLogger();
    private EntityLiving mob;
    private final Predicate<EntityLivingBase> field_179443_c;
    private final EntityAINearestAttackableTarget.Sorter field_179440_d;
    private EntityLivingBase target;
    private Class <? extends EntityLivingBase > field_179439_f;

    public EntityAIFindEntityNearest(EntityLiving mobIn, Class <? extends EntityLivingBase > p_i45884_2_)
    {
        this.mob = mobIn;
        this.field_179439_f = p_i45884_2_;

        if (mobIn instanceof EntityCreature)
        {
            LOGGER.warn("Use NearestAttackableTargetGoal.class for PathfinerMob mobs!");
        }

        this.field_179443_c = new Predicate<EntityLivingBase>()
        {
            public boolean apply(EntityLivingBase p_apply_1_)
            {
                double lvt_2_1_ = EntityAIFindEntityNearest.this.getFollowRange();

                if (p_apply_1_.isSneaking())
                {
                    lvt_2_1_ *= 0.800000011920929D;
                }

                return p_apply_1_.isInvisible() ? false : ((double)p_apply_1_.getDistanceToEntity(EntityAIFindEntityNearest.this.mob) > lvt_2_1_ ? false : EntityAITarget.isSuitableTarget(EntityAIFindEntityNearest.this.mob, p_apply_1_, false, true));
            }
            public boolean apply(Object p_apply_1_)
            {
                return this.apply((EntityLivingBase)p_apply_1_);
            }
        };
        this.field_179440_d = new EntityAINearestAttackableTarget.Sorter(mobIn);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        double lvt_1_1_ = this.getFollowRange();
        List<EntityLivingBase> lvt_3_1_ = this.mob.worldObj.<EntityLivingBase>getEntitiesWithinAABB(this.field_179439_f, this.mob.getEntityBoundingBox().expand(lvt_1_1_, 4.0D, lvt_1_1_), this.field_179443_c);
        Collections.sort(lvt_3_1_, this.field_179440_d);

        if (lvt_3_1_.isEmpty())
        {
            return false;
        }
        else
        {
            this.target = (EntityLivingBase)lvt_3_1_.get(0);
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        EntityLivingBase lvt_1_1_ = this.mob.getAttackTarget();

        if (lvt_1_1_ == null)
        {
            return false;
        }
        else if (!lvt_1_1_.isEntityAlive())
        {
            return false;
        }
        else
        {
            double lvt_2_1_ = this.getFollowRange();
            return this.mob.getDistanceSqToEntity(lvt_1_1_) > lvt_2_1_ * lvt_2_1_ ? false : !(lvt_1_1_ instanceof EntityPlayerMP) || !((EntityPlayerMP)lvt_1_1_).theItemInWorldManager.isCreative();
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.mob.setAttackTarget(this.target);
        super.startExecuting();
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.mob.setAttackTarget((EntityLivingBase)null);
        super.startExecuting();
    }

    protected double getFollowRange()
    {
        IAttributeInstance lvt_1_1_ = this.mob.getEntityAttribute(SharedMonsterAttributes.followRange);
        return lvt_1_1_ == null ? 16.0D : lvt_1_1_.getAttributeValue();
    }
}
