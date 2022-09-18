package net.minecraft.entity.ai;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityAIMate extends EntityAIBase
{
    private EntityAnimal theAnimal;
    World theWorld;
    private EntityAnimal targetMate;

    /**
     * Delay preventing a baby from spawning immediately when two mate-able animals find each other.
     */
    int spawnBabyDelay;

    /** The speed the creature moves at during mating behavior. */
    double moveSpeed;

    public EntityAIMate(EntityAnimal animal, double speedIn)
    {
        this.theAnimal = animal;
        this.theWorld = animal.worldObj;
        this.moveSpeed = speedIn;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!this.theAnimal.isInLove())
        {
            return false;
        }
        else
        {
            this.targetMate = this.getNearbyMate();
            return this.targetMate != null;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.targetMate.isEntityAlive() && this.targetMate.isInLove() && this.spawnBabyDelay < 60;
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.targetMate = null;
        this.spawnBabyDelay = 0;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        this.theAnimal.getLookHelper().setLookPositionWithEntity(this.targetMate, 10.0F, (float)this.theAnimal.getVerticalFaceSpeed());
        this.theAnimal.getNavigator().tryMoveToEntityLiving(this.targetMate, this.moveSpeed);
        ++this.spawnBabyDelay;

        if (this.spawnBabyDelay >= 60 && this.theAnimal.getDistanceSqToEntity(this.targetMate) < 9.0D)
        {
            this.spawnBaby();
        }
    }

    /**
     * Loops through nearby animals and finds another animal of the same type that can be mated with. Returns the first
     * valid mate found.
     */
    private EntityAnimal getNearbyMate()
    {
        float lvt_1_1_ = 8.0F;
        List<EntityAnimal> lvt_2_1_ = this.theWorld.<EntityAnimal>getEntitiesWithinAABB(this.theAnimal.getClass(), this.theAnimal.getEntityBoundingBox().expand((double)lvt_1_1_, (double)lvt_1_1_, (double)lvt_1_1_));
        double lvt_3_1_ = Double.MAX_VALUE;
        EntityAnimal lvt_5_1_ = null;

        for (EntityAnimal lvt_7_1_ : lvt_2_1_)
        {
            if (this.theAnimal.canMateWith(lvt_7_1_) && this.theAnimal.getDistanceSqToEntity(lvt_7_1_) < lvt_3_1_)
            {
                lvt_5_1_ = lvt_7_1_;
                lvt_3_1_ = this.theAnimal.getDistanceSqToEntity(lvt_7_1_);
            }
        }

        return lvt_5_1_;
    }

    /**
     * Spawns a baby animal of the same type.
     */
    private void spawnBaby()
    {
        EntityAgeable lvt_1_1_ = this.theAnimal.createChild(this.targetMate);

        if (lvt_1_1_ != null)
        {
            EntityPlayer lvt_2_1_ = this.theAnimal.getPlayerInLove();

            if (lvt_2_1_ == null && this.targetMate.getPlayerInLove() != null)
            {
                lvt_2_1_ = this.targetMate.getPlayerInLove();
            }

            if (lvt_2_1_ != null)
            {
                lvt_2_1_.triggerAchievement(StatList.animalsBredStat);

                if (this.theAnimal instanceof EntityCow)
                {
                    lvt_2_1_.triggerAchievement(AchievementList.breedCow);
                }
            }

            this.theAnimal.setGrowingAge(6000);
            this.targetMate.setGrowingAge(6000);
            this.theAnimal.resetInLove();
            this.targetMate.resetInLove();
            lvt_1_1_.setGrowingAge(-24000);
            lvt_1_1_.setLocationAndAngles(this.theAnimal.posX, this.theAnimal.posY, this.theAnimal.posZ, 0.0F, 0.0F);
            this.theWorld.spawnEntityInWorld(lvt_1_1_);
            Random lvt_3_1_ = this.theAnimal.getRNG();

            for (int lvt_4_1_ = 0; lvt_4_1_ < 7; ++lvt_4_1_)
            {
                double lvt_5_1_ = lvt_3_1_.nextGaussian() * 0.02D;
                double lvt_7_1_ = lvt_3_1_.nextGaussian() * 0.02D;
                double lvt_9_1_ = lvt_3_1_.nextGaussian() * 0.02D;
                double lvt_11_1_ = lvt_3_1_.nextDouble() * (double)this.theAnimal.width * 2.0D - (double)this.theAnimal.width;
                double lvt_13_1_ = 0.5D + lvt_3_1_.nextDouble() * (double)this.theAnimal.height;
                double lvt_15_1_ = lvt_3_1_.nextDouble() * (double)this.theAnimal.width * 2.0D - (double)this.theAnimal.width;
                this.theWorld.spawnParticle(EnumParticleTypes.HEART, this.theAnimal.posX + lvt_11_1_, this.theAnimal.posY + lvt_13_1_, this.theAnimal.posZ + lvt_15_1_, lvt_5_1_, lvt_7_1_, lvt_9_1_, new int[0]);
            }

            if (this.theWorld.getGameRules().getBoolean("doMobLoot"))
            {
                this.theWorld.spawnEntityInWorld(new EntityXPOrb(this.theWorld, this.theAnimal.posX, this.theAnimal.posY, this.theAnimal.posZ, lvt_3_1_.nextInt(7) + 1));
            }
        }
    }
}
