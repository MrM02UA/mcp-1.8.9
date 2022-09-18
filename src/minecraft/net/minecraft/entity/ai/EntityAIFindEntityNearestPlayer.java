package net.minecraft.entity.ai;

import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Team;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAIFindEntityNearestPlayer extends EntityAIBase
{
    private static final Logger LOGGER = LogManager.getLogger();

    /** The entity that use this AI */
    private EntityLiving entityLiving;
    private final Predicate<Entity> predicate;

    /** Used to compare two entities */
    private final EntityAINearestAttackableTarget.Sorter sorter;

    /** The current target */
    private EntityLivingBase entityTarget;

    public EntityAIFindEntityNearestPlayer(EntityLiving entityLivingIn)
    {
        this.entityLiving = entityLivingIn;

        if (entityLivingIn instanceof EntityCreature)
        {
            LOGGER.warn("Use NearestAttackableTargetGoal.class for PathfinerMob mobs!");
        }

        this.predicate = new Predicate<Entity>()
        {
            public boolean apply(Entity p_apply_1_)
            {
                if (!(p_apply_1_ instanceof EntityPlayer))
                {
                    return false;
                }
                else if (((EntityPlayer)p_apply_1_).capabilities.disableDamage)
                {
                    return false;
                }
                else
                {
                    double lvt_2_1_ = EntityAIFindEntityNearestPlayer.this.maxTargetRange();

                    if (p_apply_1_.isSneaking())
                    {
                        lvt_2_1_ *= 0.800000011920929D;
                    }

                    if (p_apply_1_.isInvisible())
                    {
                        float lvt_4_1_ = ((EntityPlayer)p_apply_1_).getArmorVisibility();

                        if (lvt_4_1_ < 0.1F)
                        {
                            lvt_4_1_ = 0.1F;
                        }

                        lvt_2_1_ *= (double)(0.7F * lvt_4_1_);
                    }

                    return (double)p_apply_1_.getDistanceToEntity(EntityAIFindEntityNearestPlayer.this.entityLiving) > lvt_2_1_ ? false : EntityAITarget.isSuitableTarget(EntityAIFindEntityNearestPlayer.this.entityLiving, (EntityLivingBase)p_apply_1_, false, true);
                }
            }
            public boolean apply(Object p_apply_1_)
            {
                return this.apply((Entity)p_apply_1_);
            }
        };
        this.sorter = new EntityAINearestAttackableTarget.Sorter(entityLivingIn);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        double lvt_1_1_ = this.maxTargetRange();
        List<EntityPlayer> lvt_3_1_ = this.entityLiving.worldObj.<EntityPlayer>getEntitiesWithinAABB(EntityPlayer.class, this.entityLiving.getEntityBoundingBox().expand(lvt_1_1_, 4.0D, lvt_1_1_), this.predicate);
        Collections.sort(lvt_3_1_, this.sorter);

        if (lvt_3_1_.isEmpty())
        {
            return false;
        }
        else
        {
            this.entityTarget = (EntityLivingBase)lvt_3_1_.get(0);
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        EntityLivingBase lvt_1_1_ = this.entityLiving.getAttackTarget();

        if (lvt_1_1_ == null)
        {
            return false;
        }
        else if (!lvt_1_1_.isEntityAlive())
        {
            return false;
        }
        else if (lvt_1_1_ instanceof EntityPlayer && ((EntityPlayer)lvt_1_1_).capabilities.disableDamage)
        {
            return false;
        }
        else
        {
            Team lvt_2_1_ = this.entityLiving.getTeam();
            Team lvt_3_1_ = lvt_1_1_.getTeam();

            if (lvt_2_1_ != null && lvt_3_1_ == lvt_2_1_)
            {
                return false;
            }
            else
            {
                double lvt_4_1_ = this.maxTargetRange();
                return this.entityLiving.getDistanceSqToEntity(lvt_1_1_) > lvt_4_1_ * lvt_4_1_ ? false : !(lvt_1_1_ instanceof EntityPlayerMP) || !((EntityPlayerMP)lvt_1_1_).theItemInWorldManager.isCreative();
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.entityLiving.setAttackTarget(this.entityTarget);
        super.startExecuting();
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.entityLiving.setAttackTarget((EntityLivingBase)null);
        super.startExecuting();
    }

    /**
     * Return the max target range of the entiity (16 by default)
     */
    protected double maxTargetRange()
    {
        IAttributeInstance lvt_1_1_ = this.entityLiving.getEntityAttribute(SharedMonsterAttributes.followRange);
        return lvt_1_1_ == null ? 16.0D : lvt_1_1_.getAttributeValue();
    }
}
