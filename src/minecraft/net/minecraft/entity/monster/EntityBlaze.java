package net.minecraft.entity.monster;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityBlaze extends EntityMob
{
    /** Random offset used in floating behaviour */
    private float heightOffset = 0.5F;

    /** ticks until heightOffset is randomized */
    private int heightOffsetUpdateTime;

    public EntityBlaze(World worldIn)
    {
        super(worldIn);
        this.isImmuneToFire = true;
        this.experienceValue = 10;
        this.tasks.addTask(4, new EntityBlaze.AIFireballAttack(this));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(6.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.23000000417232513D);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(48.0D);
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(16, new Byte((byte)0));
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.blaze.breathe";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.blaze.hit";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.blaze.death";
    }

    public int getBrightnessForRender(float partialTicks)
    {
        return 15728880;
    }

    /**
     * Gets how bright this entity is.
     */
    public float getBrightness(float partialTicks)
    {
        return 1.0F;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        if (!this.onGround && this.motionY < 0.0D)
        {
            this.motionY *= 0.6D;
        }

        if (this.worldObj.isRemote)
        {
            if (this.rand.nextInt(24) == 0 && !this.isSilent())
            {
                this.worldObj.playSound(this.posX + 0.5D, this.posY + 0.5D, this.posZ + 0.5D, "fire.fire", 1.0F + this.rand.nextFloat(), this.rand.nextFloat() * 0.7F + 0.3F, false);
            }

            for (int lvt_1_1_ = 0; lvt_1_1_ < 2; ++lvt_1_1_)
            {
                this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
            }
        }

        super.onLivingUpdate();
    }

    protected void updateAITasks()
    {
        if (this.isWet())
        {
            this.attackEntityFrom(DamageSource.drown, 1.0F);
        }

        --this.heightOffsetUpdateTime;

        if (this.heightOffsetUpdateTime <= 0)
        {
            this.heightOffsetUpdateTime = 100;
            this.heightOffset = 0.5F + (float)this.rand.nextGaussian() * 3.0F;
        }

        EntityLivingBase lvt_1_1_ = this.getAttackTarget();

        if (lvt_1_1_ != null && lvt_1_1_.posY + (double)lvt_1_1_.getEyeHeight() > this.posY + (double)this.getEyeHeight() + (double)this.heightOffset)
        {
            this.motionY += (0.30000001192092896D - this.motionY) * 0.30000001192092896D;
            this.isAirBorne = true;
        }

        super.updateAITasks();
    }

    public void fall(float distance, float damageMultiplier)
    {
    }

    protected Item getDropItem()
    {
        return Items.blaze_rod;
    }

    /**
     * Returns true if the entity is on fire. Used by render to add the fire effect on rendering.
     */
    public boolean isBurning()
    {
        return this.func_70845_n();
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
        if (wasRecentlyHit)
        {
            int lvt_3_1_ = this.rand.nextInt(2 + lootingModifier);

            for (int lvt_4_1_ = 0; lvt_4_1_ < lvt_3_1_; ++lvt_4_1_)
            {
                this.dropItem(Items.blaze_rod, 1);
            }
        }
    }

    public boolean func_70845_n()
    {
        return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
    }

    public void setOnFire(boolean onFire)
    {
        byte lvt_2_1_ = this.dataWatcher.getWatchableObjectByte(16);

        if (onFire)
        {
            lvt_2_1_ = (byte)(lvt_2_1_ | 1);
        }
        else
        {
            lvt_2_1_ = (byte)(lvt_2_1_ & -2);
        }

        this.dataWatcher.updateObject(16, Byte.valueOf(lvt_2_1_));
    }

    /**
     * Checks to make sure the light is not too bright where the mob is spawning
     */
    protected boolean isValidLightLevel()
    {
        return true;
    }

    static class AIFireballAttack extends EntityAIBase
    {
        private EntityBlaze blaze;
        private int field_179467_b;
        private int field_179468_c;

        public AIFireballAttack(EntityBlaze p_i45846_1_)
        {
            this.blaze = p_i45846_1_;
            this.setMutexBits(3);
        }

        public boolean shouldExecute()
        {
            EntityLivingBase lvt_1_1_ = this.blaze.getAttackTarget();
            return lvt_1_1_ != null && lvt_1_1_.isEntityAlive();
        }

        public void startExecuting()
        {
            this.field_179467_b = 0;
        }

        public void resetTask()
        {
            this.blaze.setOnFire(false);
        }

        public void updateTask()
        {
            --this.field_179468_c;
            EntityLivingBase lvt_1_1_ = this.blaze.getAttackTarget();
            double lvt_2_1_ = this.blaze.getDistanceSqToEntity(lvt_1_1_);

            if (lvt_2_1_ < 4.0D)
            {
                if (this.field_179468_c <= 0)
                {
                    this.field_179468_c = 20;
                    this.blaze.attackEntityAsMob(lvt_1_1_);
                }

                this.blaze.getMoveHelper().setMoveTo(lvt_1_1_.posX, lvt_1_1_.posY, lvt_1_1_.posZ, 1.0D);
            }
            else if (lvt_2_1_ < 256.0D)
            {
                double lvt_4_1_ = lvt_1_1_.posX - this.blaze.posX;
                double lvt_6_1_ = lvt_1_1_.getEntityBoundingBox().minY + (double)(lvt_1_1_.height / 2.0F) - (this.blaze.posY + (double)(this.blaze.height / 2.0F));
                double lvt_8_1_ = lvt_1_1_.posZ - this.blaze.posZ;

                if (this.field_179468_c <= 0)
                {
                    ++this.field_179467_b;

                    if (this.field_179467_b == 1)
                    {
                        this.field_179468_c = 60;
                        this.blaze.setOnFire(true);
                    }
                    else if (this.field_179467_b <= 4)
                    {
                        this.field_179468_c = 6;
                    }
                    else
                    {
                        this.field_179468_c = 100;
                        this.field_179467_b = 0;
                        this.blaze.setOnFire(false);
                    }

                    if (this.field_179467_b > 1)
                    {
                        float lvt_10_1_ = MathHelper.sqrt_float(MathHelper.sqrt_double(lvt_2_1_)) * 0.5F;
                        this.blaze.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1009, new BlockPos((int)this.blaze.posX, (int)this.blaze.posY, (int)this.blaze.posZ), 0);

                        for (int lvt_11_1_ = 0; lvt_11_1_ < 1; ++lvt_11_1_)
                        {
                            EntitySmallFireball lvt_12_1_ = new EntitySmallFireball(this.blaze.worldObj, this.blaze, lvt_4_1_ + this.blaze.getRNG().nextGaussian() * (double)lvt_10_1_, lvt_6_1_, lvt_8_1_ + this.blaze.getRNG().nextGaussian() * (double)lvt_10_1_);
                            lvt_12_1_.posY = this.blaze.posY + (double)(this.blaze.height / 2.0F) + 0.5D;
                            this.blaze.worldObj.spawnEntityInWorld(lvt_12_1_);
                        }
                    }
                }

                this.blaze.getLookHelper().setLookPositionWithEntity(lvt_1_1_, 10.0F, 10.0F);
            }
            else
            {
                this.blaze.getNavigator().clearPathEntity();
                this.blaze.getMoveHelper().setMoveTo(lvt_1_1_.posX, lvt_1_1_.posY, lvt_1_1_.posZ, 1.0D);
            }

            super.updateTask();
        }
    }
}
