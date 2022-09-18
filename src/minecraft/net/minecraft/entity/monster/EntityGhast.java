package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityGhast extends EntityFlying implements IMob
{
    /** The explosion radius of spawned fireballs. */
    private int explosionStrength = 1;

    public EntityGhast(World worldIn)
    {
        super(worldIn);
        this.setSize(4.0F, 4.0F);
        this.isImmuneToFire = true;
        this.experienceValue = 5;
        this.moveHelper = new EntityGhast.GhastMoveHelper(this);
        this.tasks.addTask(5, new EntityGhast.AIRandomFly(this));
        this.tasks.addTask(7, new EntityGhast.AILookAround(this));
        this.tasks.addTask(7, new EntityGhast.AIFireballAttack(this));
        this.targetTasks.addTask(1, new EntityAIFindEntityNearestPlayer(this));
    }

    public boolean isAttacking()
    {
        return this.dataWatcher.getWatchableObjectByte(16) != 0;
    }

    public void setAttacking(boolean attacking)
    {
        this.dataWatcher.updateObject(16, Byte.valueOf((byte)(attacking ? 1 : 0)));
    }

    public int getFireballStrength()
    {
        return this.explosionStrength;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (!this.worldObj.isRemote && this.worldObj.getDifficulty() == EnumDifficulty.PEACEFUL)
        {
            this.setDead();
        }
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isEntityInvulnerable(source))
        {
            return false;
        }
        else if ("fireball".equals(source.getDamageType()) && source.getEntity() instanceof EntityPlayer)
        {
            super.attackEntityFrom(source, 1000.0F);
            ((EntityPlayer)source.getEntity()).triggerAchievement(AchievementList.ghast);
            return true;
        }
        else
        {
            return super.attackEntityFrom(source, amount);
        }
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(16, Byte.valueOf((byte)0));
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(100.0D);
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.ghast.moan";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.ghast.scream";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.ghast.death";
    }

    protected Item getDropItem()
    {
        return Items.gunpowder;
    }

    /**
     * Drop 0-2 items of this living's type
     *  
     * @param wasRecentlyHit true if this this entity was recently hit by appropriate entity (generally only if player
     * or tameable)
     * @param lootingModifier level of enchanment to be applied to this drop
     */
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier)
    {
        int lvt_3_1_ = this.rand.nextInt(2) + this.rand.nextInt(1 + lootingModifier);

        for (int lvt_4_1_ = 0; lvt_4_1_ < lvt_3_1_; ++lvt_4_1_)
        {
            this.dropItem(Items.ghast_tear, 1);
        }

        lvt_3_1_ = this.rand.nextInt(3) + this.rand.nextInt(1 + lootingModifier);

        for (int lvt_4_2_ = 0; lvt_4_2_ < lvt_3_1_; ++lvt_4_2_)
        {
            this.dropItem(Items.gunpowder, 1);
        }
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume()
    {
        return 10.0F;
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
        return this.rand.nextInt(20) == 0 && super.getCanSpawnHere() && this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL;
    }

    /**
     * Will return how many at most can spawn in a chunk at once.
     */
    public int getMaxSpawnedInChunk()
    {
        return 1;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("ExplosionPower", this.explosionStrength);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        super.readEntityFromNBT(tagCompund);

        if (tagCompund.hasKey("ExplosionPower", 99))
        {
            this.explosionStrength = tagCompund.getInteger("ExplosionPower");
        }
    }

    public float getEyeHeight()
    {
        return 2.6F;
    }

    static class AIFireballAttack extends EntityAIBase
    {
        private EntityGhast parentEntity;
        public int attackTimer;

        public AIFireballAttack(EntityGhast ghast)
        {
            this.parentEntity = ghast;
        }

        public boolean shouldExecute()
        {
            return this.parentEntity.getAttackTarget() != null;
        }

        public void startExecuting()
        {
            this.attackTimer = 0;
        }

        public void resetTask()
        {
            this.parentEntity.setAttacking(false);
        }

        public void updateTask()
        {
            EntityLivingBase lvt_1_1_ = this.parentEntity.getAttackTarget();
            double lvt_2_1_ = 64.0D;

            if (lvt_1_1_.getDistanceSqToEntity(this.parentEntity) < lvt_2_1_ * lvt_2_1_ && this.parentEntity.canEntityBeSeen(lvt_1_1_))
            {
                World lvt_4_1_ = this.parentEntity.worldObj;
                ++this.attackTimer;

                if (this.attackTimer == 10)
                {
                    lvt_4_1_.playAuxSFXAtEntity((EntityPlayer)null, 1007, new BlockPos(this.parentEntity), 0);
                }

                if (this.attackTimer == 20)
                {
                    double lvt_5_1_ = 4.0D;
                    Vec3 lvt_7_1_ = this.parentEntity.getLook(1.0F);
                    double lvt_8_1_ = lvt_1_1_.posX - (this.parentEntity.posX + lvt_7_1_.xCoord * lvt_5_1_);
                    double lvt_10_1_ = lvt_1_1_.getEntityBoundingBox().minY + (double)(lvt_1_1_.height / 2.0F) - (0.5D + this.parentEntity.posY + (double)(this.parentEntity.height / 2.0F));
                    double lvt_12_1_ = lvt_1_1_.posZ - (this.parentEntity.posZ + lvt_7_1_.zCoord * lvt_5_1_);
                    lvt_4_1_.playAuxSFXAtEntity((EntityPlayer)null, 1008, new BlockPos(this.parentEntity), 0);
                    EntityLargeFireball lvt_14_1_ = new EntityLargeFireball(lvt_4_1_, this.parentEntity, lvt_8_1_, lvt_10_1_, lvt_12_1_);
                    lvt_14_1_.explosionPower = this.parentEntity.getFireballStrength();
                    lvt_14_1_.posX = this.parentEntity.posX + lvt_7_1_.xCoord * lvt_5_1_;
                    lvt_14_1_.posY = this.parentEntity.posY + (double)(this.parentEntity.height / 2.0F) + 0.5D;
                    lvt_14_1_.posZ = this.parentEntity.posZ + lvt_7_1_.zCoord * lvt_5_1_;
                    lvt_4_1_.spawnEntityInWorld(lvt_14_1_);
                    this.attackTimer = -40;
                }
            }
            else if (this.attackTimer > 0)
            {
                --this.attackTimer;
            }

            this.parentEntity.setAttacking(this.attackTimer > 10);
        }
    }

    static class AILookAround extends EntityAIBase
    {
        private EntityGhast parentEntity;

        public AILookAround(EntityGhast ghast)
        {
            this.parentEntity = ghast;
            this.setMutexBits(2);
        }

        public boolean shouldExecute()
        {
            return true;
        }

        public void updateTask()
        {
            if (this.parentEntity.getAttackTarget() == null)
            {
                this.parentEntity.renderYawOffset = this.parentEntity.rotationYaw = -((float)MathHelper.atan2(this.parentEntity.motionX, this.parentEntity.motionZ)) * 180.0F / (float)Math.PI;
            }
            else
            {
                EntityLivingBase lvt_1_1_ = this.parentEntity.getAttackTarget();
                double lvt_2_1_ = 64.0D;

                if (lvt_1_1_.getDistanceSqToEntity(this.parentEntity) < lvt_2_1_ * lvt_2_1_)
                {
                    double lvt_4_1_ = lvt_1_1_.posX - this.parentEntity.posX;
                    double lvt_6_1_ = lvt_1_1_.posZ - this.parentEntity.posZ;
                    this.parentEntity.renderYawOffset = this.parentEntity.rotationYaw = -((float)MathHelper.atan2(lvt_4_1_, lvt_6_1_)) * 180.0F / (float)Math.PI;
                }
            }
        }
    }

    static class AIRandomFly extends EntityAIBase
    {
        private EntityGhast parentEntity;

        public AIRandomFly(EntityGhast ghast)
        {
            this.parentEntity = ghast;
            this.setMutexBits(1);
        }

        public boolean shouldExecute()
        {
            EntityMoveHelper lvt_1_1_ = this.parentEntity.getMoveHelper();

            if (!lvt_1_1_.isUpdating())
            {
                return true;
            }
            else
            {
                double lvt_2_1_ = lvt_1_1_.getX() - this.parentEntity.posX;
                double lvt_4_1_ = lvt_1_1_.getY() - this.parentEntity.posY;
                double lvt_6_1_ = lvt_1_1_.getZ() - this.parentEntity.posZ;
                double lvt_8_1_ = lvt_2_1_ * lvt_2_1_ + lvt_4_1_ * lvt_4_1_ + lvt_6_1_ * lvt_6_1_;
                return lvt_8_1_ < 1.0D || lvt_8_1_ > 3600.0D;
            }
        }

        public boolean continueExecuting()
        {
            return false;
        }

        public void startExecuting()
        {
            Random lvt_1_1_ = this.parentEntity.getRNG();
            double lvt_2_1_ = this.parentEntity.posX + (double)((lvt_1_1_.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double lvt_4_1_ = this.parentEntity.posY + (double)((lvt_1_1_.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double lvt_6_1_ = this.parentEntity.posZ + (double)((lvt_1_1_.nextFloat() * 2.0F - 1.0F) * 16.0F);
            this.parentEntity.getMoveHelper().setMoveTo(lvt_2_1_, lvt_4_1_, lvt_6_1_, 1.0D);
        }
    }

    static class GhastMoveHelper extends EntityMoveHelper
    {
        private EntityGhast parentEntity;
        private int courseChangeCooldown;

        public GhastMoveHelper(EntityGhast ghast)
        {
            super(ghast);
            this.parentEntity = ghast;
        }

        public void onUpdateMoveHelper()
        {
            if (this.update)
            {
                double lvt_1_1_ = this.posX - this.parentEntity.posX;
                double lvt_3_1_ = this.posY - this.parentEntity.posY;
                double lvt_5_1_ = this.posZ - this.parentEntity.posZ;
                double lvt_7_1_ = lvt_1_1_ * lvt_1_1_ + lvt_3_1_ * lvt_3_1_ + lvt_5_1_ * lvt_5_1_;

                if (this.courseChangeCooldown-- <= 0)
                {
                    this.courseChangeCooldown += this.parentEntity.getRNG().nextInt(5) + 2;
                    lvt_7_1_ = (double)MathHelper.sqrt_double(lvt_7_1_);

                    if (this.isNotColliding(this.posX, this.posY, this.posZ, lvt_7_1_))
                    {
                        this.parentEntity.motionX += lvt_1_1_ / lvt_7_1_ * 0.1D;
                        this.parentEntity.motionY += lvt_3_1_ / lvt_7_1_ * 0.1D;
                        this.parentEntity.motionZ += lvt_5_1_ / lvt_7_1_ * 0.1D;
                    }
                    else
                    {
                        this.update = false;
                    }
                }
            }
        }

        private boolean isNotColliding(double x, double y, double z, double p_179926_7_)
        {
            double lvt_9_1_ = (x - this.parentEntity.posX) / p_179926_7_;
            double lvt_11_1_ = (y - this.parentEntity.posY) / p_179926_7_;
            double lvt_13_1_ = (z - this.parentEntity.posZ) / p_179926_7_;
            AxisAlignedBB lvt_15_1_ = this.parentEntity.getEntityBoundingBox();

            for (int lvt_16_1_ = 1; (double)lvt_16_1_ < p_179926_7_; ++lvt_16_1_)
            {
                lvt_15_1_ = lvt_15_1_.offset(lvt_9_1_, lvt_11_1_, lvt_13_1_);

                if (!this.parentEntity.worldObj.getCollidingBoundingBoxes(this.parentEntity, lvt_15_1_).isEmpty())
                {
                    return false;
                }
            }

            return true;
        }
    }
}
