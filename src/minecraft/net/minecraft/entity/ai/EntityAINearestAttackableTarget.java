package net.minecraft.entity.ai;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;

public class EntityAINearestAttackableTarget<T extends EntityLivingBase> extends EntityAITarget
{
    protected final Class<T> targetClass;
    private final int targetChance;

    /** Instance of EntityAINearestAttackableTargetSorter. */
    protected final EntityAINearestAttackableTarget.Sorter theNearestAttackableTargetSorter;
    protected Predicate <? super T > targetEntitySelector;
    protected EntityLivingBase targetEntity;

    public EntityAINearestAttackableTarget(EntityCreature creature, Class<T> classTarget, boolean checkSight)
    {
        this(creature, classTarget, checkSight, false);
    }

    public EntityAINearestAttackableTarget(EntityCreature creature, Class<T> classTarget, boolean checkSight, boolean onlyNearby)
    {
        this(creature, classTarget, 10, checkSight, onlyNearby, (Predicate <? super T >)null);
    }

    public EntityAINearestAttackableTarget(EntityCreature creature, Class<T> classTarget, int chance, boolean checkSight, boolean onlyNearby, final Predicate <? super T > targetSelector)
    {
        super(creature, checkSight, onlyNearby);
        this.targetClass = classTarget;
        this.targetChance = chance;
        this.theNearestAttackableTargetSorter = new EntityAINearestAttackableTarget.Sorter(creature);
        this.setMutexBits(1);
        this.targetEntitySelector = new Predicate<T>()
        {
            public boolean apply(T p_apply_1_)
            {
                if (targetSelector != null && !targetSelector.apply(p_apply_1_))
                {
                    return false;
                }
                else
                {
                    if (p_apply_1_ instanceof EntityPlayer)
                    {
                        double lvt_2_1_ = EntityAINearestAttackableTarget.this.getTargetDistance();

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

                        if ((double)p_apply_1_.getDistanceToEntity(EntityAINearestAttackableTarget.this.taskOwner) > lvt_2_1_)
                        {
                            return false;
                        }
                    }

                    return EntityAINearestAttackableTarget.this.isSuitableTarget(p_apply_1_, false);
                }
            }
            public boolean apply(Object p_apply_1_)
            {
                return this.apply((EntityLivingBase)p_apply_1_);
            }
        };
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0)
        {
            return false;
        }
        else
        {
            double lvt_1_1_ = this.getTargetDistance();
            List<T> lvt_3_1_ = this.taskOwner.worldObj.<T>getEntitiesWithinAABB(this.targetClass, this.taskOwner.getEntityBoundingBox().expand(lvt_1_1_, 4.0D, lvt_1_1_), Predicates.and(this.targetEntitySelector, EntitySelectors.NOT_SPECTATING));
            Collections.sort(lvt_3_1_, this.theNearestAttackableTargetSorter);

            if (lvt_3_1_.isEmpty())
            {
                return false;
            }
            else
            {
                this.targetEntity = (EntityLivingBase)lvt_3_1_.get(0);
                return true;
            }
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.targetEntity);
        super.startExecuting();
    }

    public static class Sorter implements Comparator<Entity>
    {
        private final Entity theEntity;

        public Sorter(Entity theEntityIn)
        {
            this.theEntity = theEntityIn;
        }

        public int compare(Entity p_compare_1_, Entity p_compare_2_)
        {
            double lvt_3_1_ = this.theEntity.getDistanceSqToEntity(p_compare_1_);
            double lvt_5_1_ = this.theEntity.getDistanceSqToEntity(p_compare_2_);
            return lvt_3_1_ < lvt_5_1_ ? -1 : (lvt_3_1_ > lvt_5_1_ ? 1 : 0);
        }

        public int compare(Object p_compare_1_, Object p_compare_2_)
        {
            return this.compare((Entity)p_compare_1_, (Entity)p_compare_2_);
        }
    }
}
