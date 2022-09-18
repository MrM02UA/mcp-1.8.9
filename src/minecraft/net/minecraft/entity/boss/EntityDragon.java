package net.minecraft.entity.boss;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class EntityDragon extends EntityLiving implements IBossDisplayData, IEntityMultiPart, IMob
{
    public double targetX;
    public double targetY;
    public double targetZ;

    /**
     * Ring buffer array for the last 64 Y-positions and yaw rotations. Used to calculate offsets for the animations.
     */
    public double[][] ringBuffer = new double[64][3];

    /**
     * Index into the ring buffer. Incremented once per tick and restarts at 0 once it reaches the end of the buffer.
     */
    public int ringBufferIndex = -1;

    /** An array containing all body parts of this dragon */
    public EntityDragonPart[] dragonPartArray;

    /** The head bounding box of a dragon */
    public EntityDragonPart dragonPartHead;

    /** The body bounding box of a dragon */
    public EntityDragonPart dragonPartBody;
    public EntityDragonPart dragonPartTail1;
    public EntityDragonPart dragonPartTail2;
    public EntityDragonPart dragonPartTail3;
    public EntityDragonPart dragonPartWing1;
    public EntityDragonPart dragonPartWing2;

    /** Animation time at previous tick. */
    public float prevAnimTime;

    /**
     * Animation time, used to control the speed of the animation cycles (wings flapping, jaw opening, etc.)
     */
    public float animTime;

    /** Force selecting a new flight target at next tick if set to true. */
    public boolean forceNewTarget;

    /**
     * Activated if the dragon is flying though obsidian, white stone or bedrock. Slows movement and animation speed.
     */
    public boolean slowed;
    private Entity target;
    public int deathTicks;

    /** The current endercrystal that is healing this dragon */
    public EntityEnderCrystal healingEnderCrystal;

    public EntityDragon(World worldIn)
    {
        super(worldIn);
        this.dragonPartArray = new EntityDragonPart[] {this.dragonPartHead = new EntityDragonPart(this, "head", 6.0F, 6.0F), this.dragonPartBody = new EntityDragonPart(this, "body", 8.0F, 8.0F), this.dragonPartTail1 = new EntityDragonPart(this, "tail", 4.0F, 4.0F), this.dragonPartTail2 = new EntityDragonPart(this, "tail", 4.0F, 4.0F), this.dragonPartTail3 = new EntityDragonPart(this, "tail", 4.0F, 4.0F), this.dragonPartWing1 = new EntityDragonPart(this, "wing", 4.0F, 4.0F), this.dragonPartWing2 = new EntityDragonPart(this, "wing", 4.0F, 4.0F)};
        this.setHealth(this.getMaxHealth());
        this.setSize(16.0F, 8.0F);
        this.noClip = true;
        this.isImmuneToFire = true;
        this.targetY = 100.0D;
        this.ignoreFrustumCheck = true;
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(200.0D);
    }

    protected void entityInit()
    {
        super.entityInit();
    }

    /**
     * Returns a double[3] array with movement offsets, used to calculate trailing tail/neck positions. [0] = yaw
     * offset, [1] = y offset, [2] = unused, always 0. Parameters: buffer index offset, partial ticks.
     */
    public double[] getMovementOffsets(int p_70974_1_, float p_70974_2_)
    {
        if (this.getHealth() <= 0.0F)
        {
            p_70974_2_ = 0.0F;
        }

        p_70974_2_ = 1.0F - p_70974_2_;
        int lvt_3_1_ = this.ringBufferIndex - p_70974_1_ * 1 & 63;
        int lvt_4_1_ = this.ringBufferIndex - p_70974_1_ * 1 - 1 & 63;
        double[] lvt_5_1_ = new double[3];
        double lvt_6_1_ = this.ringBuffer[lvt_3_1_][0];
        double lvt_8_1_ = MathHelper.wrapAngleTo180_double(this.ringBuffer[lvt_4_1_][0] - lvt_6_1_);
        lvt_5_1_[0] = lvt_6_1_ + lvt_8_1_ * (double)p_70974_2_;
        lvt_6_1_ = this.ringBuffer[lvt_3_1_][1];
        lvt_8_1_ = this.ringBuffer[lvt_4_1_][1] - lvt_6_1_;
        lvt_5_1_[1] = lvt_6_1_ + lvt_8_1_ * (double)p_70974_2_;
        lvt_5_1_[2] = this.ringBuffer[lvt_3_1_][2] + (this.ringBuffer[lvt_4_1_][2] - this.ringBuffer[lvt_3_1_][2]) * (double)p_70974_2_;
        return lvt_5_1_;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        if (this.worldObj.isRemote)
        {
            float lvt_1_1_ = MathHelper.cos(this.animTime * (float)Math.PI * 2.0F);
            float lvt_2_1_ = MathHelper.cos(this.prevAnimTime * (float)Math.PI * 2.0F);

            if (lvt_2_1_ <= -0.3F && lvt_1_1_ >= -0.3F && !this.isSilent())
            {
                this.worldObj.playSound(this.posX, this.posY, this.posZ, "mob.enderdragon.wings", 5.0F, 0.8F + this.rand.nextFloat() * 0.3F, false);
            }
        }

        this.prevAnimTime = this.animTime;

        if (this.getHealth() <= 0.0F)
        {
            float lvt_1_2_ = (this.rand.nextFloat() - 0.5F) * 8.0F;
            float lvt_2_2_ = (this.rand.nextFloat() - 0.5F) * 4.0F;
            float lvt_3_1_ = (this.rand.nextFloat() - 0.5F) * 8.0F;
            this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.posX + (double)lvt_1_2_, this.posY + 2.0D + (double)lvt_2_2_, this.posZ + (double)lvt_3_1_, 0.0D, 0.0D, 0.0D, new int[0]);
        }
        else
        {
            this.updateDragonEnderCrystal();
            float lvt_1_3_ = 0.2F / (MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 10.0F + 1.0F);
            lvt_1_3_ = lvt_1_3_ * (float)Math.pow(2.0D, this.motionY);

            if (this.slowed)
            {
                this.animTime += lvt_1_3_ * 0.5F;
            }
            else
            {
                this.animTime += lvt_1_3_;
            }

            this.rotationYaw = MathHelper.wrapAngleTo180_float(this.rotationYaw);

            if (this.isAIDisabled())
            {
                this.animTime = 0.5F;
            }
            else
            {
                if (this.ringBufferIndex < 0)
                {
                    for (int lvt_2_3_ = 0; lvt_2_3_ < this.ringBuffer.length; ++lvt_2_3_)
                    {
                        this.ringBuffer[lvt_2_3_][0] = (double)this.rotationYaw;
                        this.ringBuffer[lvt_2_3_][1] = this.posY;
                    }
                }

                if (++this.ringBufferIndex == this.ringBuffer.length)
                {
                    this.ringBufferIndex = 0;
                }

                this.ringBuffer[this.ringBufferIndex][0] = (double)this.rotationYaw;
                this.ringBuffer[this.ringBufferIndex][1] = this.posY;

                if (this.worldObj.isRemote)
                {
                    if (this.newPosRotationIncrements > 0)
                    {
                        double lvt_2_4_ = this.posX + (this.newPosX - this.posX) / (double)this.newPosRotationIncrements;
                        double lvt_4_1_ = this.posY + (this.newPosY - this.posY) / (double)this.newPosRotationIncrements;
                        double lvt_6_1_ = this.posZ + (this.newPosZ - this.posZ) / (double)this.newPosRotationIncrements;
                        double lvt_8_1_ = MathHelper.wrapAngleTo180_double(this.newRotationYaw - (double)this.rotationYaw);
                        this.rotationYaw = (float)((double)this.rotationYaw + lvt_8_1_ / (double)this.newPosRotationIncrements);
                        this.rotationPitch = (float)((double)this.rotationPitch + (this.newRotationPitch - (double)this.rotationPitch) / (double)this.newPosRotationIncrements);
                        --this.newPosRotationIncrements;
                        this.setPosition(lvt_2_4_, lvt_4_1_, lvt_6_1_);
                        this.setRotation(this.rotationYaw, this.rotationPitch);
                    }
                }
                else
                {
                    double lvt_2_5_ = this.targetX - this.posX;
                    double lvt_4_2_ = this.targetY - this.posY;
                    double lvt_6_2_ = this.targetZ - this.posZ;
                    double lvt_8_2_ = lvt_2_5_ * lvt_2_5_ + lvt_4_2_ * lvt_4_2_ + lvt_6_2_ * lvt_6_2_;

                    if (this.target != null)
                    {
                        this.targetX = this.target.posX;
                        this.targetZ = this.target.posZ;
                        double lvt_10_1_ = this.targetX - this.posX;
                        double lvt_12_1_ = this.targetZ - this.posZ;
                        double lvt_14_1_ = Math.sqrt(lvt_10_1_ * lvt_10_1_ + lvt_12_1_ * lvt_12_1_);
                        double lvt_16_1_ = 0.4000000059604645D + lvt_14_1_ / 80.0D - 1.0D;

                        if (lvt_16_1_ > 10.0D)
                        {
                            lvt_16_1_ = 10.0D;
                        }

                        this.targetY = this.target.getEntityBoundingBox().minY + lvt_16_1_;
                    }
                    else
                    {
                        this.targetX += this.rand.nextGaussian() * 2.0D;
                        this.targetZ += this.rand.nextGaussian() * 2.0D;
                    }

                    if (this.forceNewTarget || lvt_8_2_ < 100.0D || lvt_8_2_ > 22500.0D || this.isCollidedHorizontally || this.isCollidedVertically)
                    {
                        this.setNewTarget();
                    }

                    lvt_4_2_ = lvt_4_2_ / (double)MathHelper.sqrt_double(lvt_2_5_ * lvt_2_5_ + lvt_6_2_ * lvt_6_2_);
                    float lvt_10_2_ = 0.6F;
                    lvt_4_2_ = MathHelper.clamp_double(lvt_4_2_, (double)(-lvt_10_2_), (double)lvt_10_2_);
                    this.motionY += lvt_4_2_ * 0.10000000149011612D;
                    this.rotationYaw = MathHelper.wrapAngleTo180_float(this.rotationYaw);
                    double lvt_11_1_ = 180.0D - MathHelper.atan2(lvt_2_5_, lvt_6_2_) * 180.0D / Math.PI;
                    double lvt_13_1_ = MathHelper.wrapAngleTo180_double(lvt_11_1_ - (double)this.rotationYaw);

                    if (lvt_13_1_ > 50.0D)
                    {
                        lvt_13_1_ = 50.0D;
                    }

                    if (lvt_13_1_ < -50.0D)
                    {
                        lvt_13_1_ = -50.0D;
                    }

                    Vec3 lvt_15_1_ = (new Vec3(this.targetX - this.posX, this.targetY - this.posY, this.targetZ - this.posZ)).normalize();
                    double lvt_16_2_ = (double)(-MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F));
                    Vec3 lvt_18_1_ = (new Vec3((double)MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F), this.motionY, lvt_16_2_)).normalize();
                    float lvt_19_1_ = ((float)lvt_18_1_.dotProduct(lvt_15_1_) + 0.5F) / 1.5F;

                    if (lvt_19_1_ < 0.0F)
                    {
                        lvt_19_1_ = 0.0F;
                    }

                    this.randomYawVelocity *= 0.8F;
                    float lvt_20_1_ = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ) * 1.0F + 1.0F;
                    double lvt_21_1_ = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ) * 1.0D + 1.0D;

                    if (lvt_21_1_ > 40.0D)
                    {
                        lvt_21_1_ = 40.0D;
                    }

                    this.randomYawVelocity = (float)((double)this.randomYawVelocity + lvt_13_1_ * (0.699999988079071D / lvt_21_1_ / (double)lvt_20_1_));
                    this.rotationYaw += this.randomYawVelocity * 0.1F;
                    float lvt_23_1_ = (float)(2.0D / (lvt_21_1_ + 1.0D));
                    float lvt_24_1_ = 0.06F;
                    this.moveFlying(0.0F, -1.0F, lvt_24_1_ * (lvt_19_1_ * lvt_23_1_ + (1.0F - lvt_23_1_)));

                    if (this.slowed)
                    {
                        this.moveEntity(this.motionX * 0.800000011920929D, this.motionY * 0.800000011920929D, this.motionZ * 0.800000011920929D);
                    }
                    else
                    {
                        this.moveEntity(this.motionX, this.motionY, this.motionZ);
                    }

                    Vec3 lvt_25_1_ = (new Vec3(this.motionX, this.motionY, this.motionZ)).normalize();
                    float lvt_26_1_ = ((float)lvt_25_1_.dotProduct(lvt_18_1_) + 1.0F) / 2.0F;
                    lvt_26_1_ = 0.8F + 0.15F * lvt_26_1_;
                    this.motionX *= (double)lvt_26_1_;
                    this.motionZ *= (double)lvt_26_1_;
                    this.motionY *= 0.9100000262260437D;
                }

                this.renderYawOffset = this.rotationYaw;
                this.dragonPartHead.width = this.dragonPartHead.height = 3.0F;
                this.dragonPartTail1.width = this.dragonPartTail1.height = 2.0F;
                this.dragonPartTail2.width = this.dragonPartTail2.height = 2.0F;
                this.dragonPartTail3.width = this.dragonPartTail3.height = 2.0F;
                this.dragonPartBody.height = 3.0F;
                this.dragonPartBody.width = 5.0F;
                this.dragonPartWing1.height = 2.0F;
                this.dragonPartWing1.width = 4.0F;
                this.dragonPartWing2.height = 3.0F;
                this.dragonPartWing2.width = 4.0F;
                float lvt_2_6_ = (float)(this.getMovementOffsets(5, 1.0F)[1] - this.getMovementOffsets(10, 1.0F)[1]) * 10.0F / 180.0F * (float)Math.PI;
                float lvt_3_2_ = MathHelper.cos(lvt_2_6_);
                float lvt_4_3_ = -MathHelper.sin(lvt_2_6_);
                float lvt_5_1_ = this.rotationYaw * (float)Math.PI / 180.0F;
                float lvt_6_3_ = MathHelper.sin(lvt_5_1_);
                float lvt_7_1_ = MathHelper.cos(lvt_5_1_);
                this.dragonPartBody.onUpdate();
                this.dragonPartBody.setLocationAndAngles(this.posX + (double)(lvt_6_3_ * 0.5F), this.posY, this.posZ - (double)(lvt_7_1_ * 0.5F), 0.0F, 0.0F);
                this.dragonPartWing1.onUpdate();
                this.dragonPartWing1.setLocationAndAngles(this.posX + (double)(lvt_7_1_ * 4.5F), this.posY + 2.0D, this.posZ + (double)(lvt_6_3_ * 4.5F), 0.0F, 0.0F);
                this.dragonPartWing2.onUpdate();
                this.dragonPartWing2.setLocationAndAngles(this.posX - (double)(lvt_7_1_ * 4.5F), this.posY + 2.0D, this.posZ - (double)(lvt_6_3_ * 4.5F), 0.0F, 0.0F);

                if (!this.worldObj.isRemote && this.hurtTime == 0)
                {
                    this.collideWithEntities(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartWing1.getEntityBoundingBox().expand(4.0D, 2.0D, 4.0D).offset(0.0D, -2.0D, 0.0D)));
                    this.collideWithEntities(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartWing2.getEntityBoundingBox().expand(4.0D, 2.0D, 4.0D).offset(0.0D, -2.0D, 0.0D)));
                    this.attackEntitiesInList(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartHead.getEntityBoundingBox().expand(1.0D, 1.0D, 1.0D)));
                }

                double[] lvt_8_3_ = this.getMovementOffsets(5, 1.0F);
                double[] lvt_9_1_ = this.getMovementOffsets(0, 1.0F);
                float lvt_10_3_ = MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F - this.randomYawVelocity * 0.01F);
                float lvt_11_2_ = MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F - this.randomYawVelocity * 0.01F);
                this.dragonPartHead.onUpdate();
                this.dragonPartHead.setLocationAndAngles(this.posX + (double)(lvt_10_3_ * 5.5F * lvt_3_2_), this.posY + (lvt_9_1_[1] - lvt_8_3_[1]) * 1.0D + (double)(lvt_4_3_ * 5.5F), this.posZ - (double)(lvt_11_2_ * 5.5F * lvt_3_2_), 0.0F, 0.0F);

                for (int lvt_9_2_ = 0; lvt_9_2_ < 3; ++lvt_9_2_)
                {
                    EntityDragonPart lvt_10_4_ = null;

                    if (lvt_9_2_ == 0)
                    {
                        lvt_10_4_ = this.dragonPartTail1;
                    }

                    if (lvt_9_2_ == 1)
                    {
                        lvt_10_4_ = this.dragonPartTail2;
                    }

                    if (lvt_9_2_ == 2)
                    {
                        lvt_10_4_ = this.dragonPartTail3;
                    }

                    double[] lvt_11_3_ = this.getMovementOffsets(12 + lvt_9_2_ * 2, 1.0F);
                    float lvt_12_2_ = this.rotationYaw * (float)Math.PI / 180.0F + this.simplifyAngle(lvt_11_3_[0] - lvt_8_3_[0]) * (float)Math.PI / 180.0F * 1.0F;
                    float lvt_13_2_ = MathHelper.sin(lvt_12_2_);
                    float lvt_14_2_ = MathHelper.cos(lvt_12_2_);
                    float lvt_15_2_ = 1.5F;
                    float lvt_16_3_ = (float)(lvt_9_2_ + 1) * 2.0F;
                    lvt_10_4_.onUpdate();
                    lvt_10_4_.setLocationAndAngles(this.posX - (double)((lvt_6_3_ * lvt_15_2_ + lvt_13_2_ * lvt_16_3_) * lvt_3_2_), this.posY + (lvt_11_3_[1] - lvt_8_3_[1]) * 1.0D - (double)((lvt_16_3_ + lvt_15_2_) * lvt_4_3_) + 1.5D, this.posZ + (double)((lvt_7_1_ * lvt_15_2_ + lvt_14_2_ * lvt_16_3_) * lvt_3_2_), 0.0F, 0.0F);
                }

                if (!this.worldObj.isRemote)
                {
                    this.slowed = this.destroyBlocksInAABB(this.dragonPartHead.getEntityBoundingBox()) | this.destroyBlocksInAABB(this.dragonPartBody.getEntityBoundingBox());
                }
            }
        }
    }

    /**
     * Updates the state of the enderdragon's current endercrystal.
     */
    private void updateDragonEnderCrystal()
    {
        if (this.healingEnderCrystal != null)
        {
            if (this.healingEnderCrystal.isDead)
            {
                if (!this.worldObj.isRemote)
                {
                    this.attackEntityFromPart(this.dragonPartHead, DamageSource.setExplosionSource((Explosion)null), 10.0F);
                }

                this.healingEnderCrystal = null;
            }
            else if (this.ticksExisted % 10 == 0 && this.getHealth() < this.getMaxHealth())
            {
                this.setHealth(this.getHealth() + 1.0F);
            }
        }

        if (this.rand.nextInt(10) == 0)
        {
            float lvt_1_1_ = 32.0F;
            List<EntityEnderCrystal> lvt_2_1_ = this.worldObj.<EntityEnderCrystal>getEntitiesWithinAABB(EntityEnderCrystal.class, this.getEntityBoundingBox().expand((double)lvt_1_1_, (double)lvt_1_1_, (double)lvt_1_1_));
            EntityEnderCrystal lvt_3_1_ = null;
            double lvt_4_1_ = Double.MAX_VALUE;

            for (EntityEnderCrystal lvt_7_1_ : lvt_2_1_)
            {
                double lvt_8_1_ = lvt_7_1_.getDistanceSqToEntity(this);

                if (lvt_8_1_ < lvt_4_1_)
                {
                    lvt_4_1_ = lvt_8_1_;
                    lvt_3_1_ = lvt_7_1_;
                }
            }

            this.healingEnderCrystal = lvt_3_1_;
        }
    }

    /**
     * Pushes all entities inside the list away from the enderdragon.
     */
    private void collideWithEntities(List<Entity> p_70970_1_)
    {
        double lvt_2_1_ = (this.dragonPartBody.getEntityBoundingBox().minX + this.dragonPartBody.getEntityBoundingBox().maxX) / 2.0D;
        double lvt_4_1_ = (this.dragonPartBody.getEntityBoundingBox().minZ + this.dragonPartBody.getEntityBoundingBox().maxZ) / 2.0D;

        for (Entity lvt_7_1_ : p_70970_1_)
        {
            if (lvt_7_1_ instanceof EntityLivingBase)
            {
                double lvt_8_1_ = lvt_7_1_.posX - lvt_2_1_;
                double lvt_10_1_ = lvt_7_1_.posZ - lvt_4_1_;
                double lvt_12_1_ = lvt_8_1_ * lvt_8_1_ + lvt_10_1_ * lvt_10_1_;
                lvt_7_1_.addVelocity(lvt_8_1_ / lvt_12_1_ * 4.0D, 0.20000000298023224D, lvt_10_1_ / lvt_12_1_ * 4.0D);
            }
        }
    }

    /**
     * Attacks all entities inside this list, dealing 5 hearts of damage.
     */
    private void attackEntitiesInList(List<Entity> p_70971_1_)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < p_70971_1_.size(); ++lvt_2_1_)
        {
            Entity lvt_3_1_ = (Entity)p_70971_1_.get(lvt_2_1_);

            if (lvt_3_1_ instanceof EntityLivingBase)
            {
                lvt_3_1_.attackEntityFrom(DamageSource.causeMobDamage(this), 10.0F);
                this.applyEnchantments(this, lvt_3_1_);
            }
        }
    }

    /**
     * Sets a new target for the flight AI. It can be a random coordinate or a nearby player.
     */
    private void setNewTarget()
    {
        this.forceNewTarget = false;
        List<EntityPlayer> lvt_1_1_ = Lists.newArrayList(this.worldObj.playerEntities);
        Iterator<EntityPlayer> lvt_2_1_ = lvt_1_1_.iterator();

        while (lvt_2_1_.hasNext())
        {
            if (((EntityPlayer)lvt_2_1_.next()).isSpectator())
            {
                lvt_2_1_.remove();
            }
        }

        if (this.rand.nextInt(2) == 0 && !lvt_1_1_.isEmpty())
        {
            this.target = (Entity)lvt_1_1_.get(this.rand.nextInt(lvt_1_1_.size()));
        }
        else
        {
            while (true)
            {
                this.targetX = 0.0D;
                this.targetY = (double)(70.0F + this.rand.nextFloat() * 50.0F);
                this.targetZ = 0.0D;
                this.targetX += (double)(this.rand.nextFloat() * 120.0F - 60.0F);
                this.targetZ += (double)(this.rand.nextFloat() * 120.0F - 60.0F);
                double lvt_4_1_ = this.posX - this.targetX;
                double lvt_6_1_ = this.posY - this.targetY;
                double lvt_8_1_ = this.posZ - this.targetZ;
                boolean lvt_3_1_ = lvt_4_1_ * lvt_4_1_ + lvt_6_1_ * lvt_6_1_ + lvt_8_1_ * lvt_8_1_ > 100.0D;

                if (lvt_3_1_)
                {
                    break;
                }
            }

            this.target = null;
        }
    }

    /**
     * Simplifies the value of a number by adding/subtracting 180 to the point that the number is between -180 and 180.
     */
    private float simplifyAngle(double p_70973_1_)
    {
        return (float)MathHelper.wrapAngleTo180_double(p_70973_1_);
    }

    /**
     * Destroys all blocks that aren't associated with 'The End' inside the given bounding box.
     */
    private boolean destroyBlocksInAABB(AxisAlignedBB p_70972_1_)
    {
        int lvt_2_1_ = MathHelper.floor_double(p_70972_1_.minX);
        int lvt_3_1_ = MathHelper.floor_double(p_70972_1_.minY);
        int lvt_4_1_ = MathHelper.floor_double(p_70972_1_.minZ);
        int lvt_5_1_ = MathHelper.floor_double(p_70972_1_.maxX);
        int lvt_6_1_ = MathHelper.floor_double(p_70972_1_.maxY);
        int lvt_7_1_ = MathHelper.floor_double(p_70972_1_.maxZ);
        boolean lvt_8_1_ = false;
        boolean lvt_9_1_ = false;

        for (int lvt_10_1_ = lvt_2_1_; lvt_10_1_ <= lvt_5_1_; ++lvt_10_1_)
        {
            for (int lvt_11_1_ = lvt_3_1_; lvt_11_1_ <= lvt_6_1_; ++lvt_11_1_)
            {
                for (int lvt_12_1_ = lvt_4_1_; lvt_12_1_ <= lvt_7_1_; ++lvt_12_1_)
                {
                    BlockPos lvt_13_1_ = new BlockPos(lvt_10_1_, lvt_11_1_, lvt_12_1_);
                    Block lvt_14_1_ = this.worldObj.getBlockState(lvt_13_1_).getBlock();

                    if (lvt_14_1_.getMaterial() != Material.air)
                    {
                        if (lvt_14_1_ != Blocks.barrier && lvt_14_1_ != Blocks.obsidian && lvt_14_1_ != Blocks.end_stone && lvt_14_1_ != Blocks.bedrock && lvt_14_1_ != Blocks.command_block && this.worldObj.getGameRules().getBoolean("mobGriefing"))
                        {
                            lvt_9_1_ = this.worldObj.setBlockToAir(lvt_13_1_) || lvt_9_1_;
                        }
                        else
                        {
                            lvt_8_1_ = true;
                        }
                    }
                }
            }
        }

        if (lvt_9_1_)
        {
            double lvt_10_2_ = p_70972_1_.minX + (p_70972_1_.maxX - p_70972_1_.minX) * (double)this.rand.nextFloat();
            double lvt_12_2_ = p_70972_1_.minY + (p_70972_1_.maxY - p_70972_1_.minY) * (double)this.rand.nextFloat();
            double lvt_14_2_ = p_70972_1_.minZ + (p_70972_1_.maxZ - p_70972_1_.minZ) * (double)this.rand.nextFloat();
            this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, lvt_10_2_, lvt_12_2_, lvt_14_2_, 0.0D, 0.0D, 0.0D, new int[0]);
        }

        return lvt_8_1_;
    }

    public boolean attackEntityFromPart(EntityDragonPart dragonPart, DamageSource source, float p_70965_3_)
    {
        if (dragonPart != this.dragonPartHead)
        {
            p_70965_3_ = p_70965_3_ / 4.0F + 1.0F;
        }

        float lvt_4_1_ = this.rotationYaw * (float)Math.PI / 180.0F;
        float lvt_5_1_ = MathHelper.sin(lvt_4_1_);
        float lvt_6_1_ = MathHelper.cos(lvt_4_1_);
        this.targetX = this.posX + (double)(lvt_5_1_ * 5.0F) + (double)((this.rand.nextFloat() - 0.5F) * 2.0F);
        this.targetY = this.posY + (double)(this.rand.nextFloat() * 3.0F) + 1.0D;
        this.targetZ = this.posZ - (double)(lvt_6_1_ * 5.0F) + (double)((this.rand.nextFloat() - 0.5F) * 2.0F);
        this.target = null;

        if (source.getEntity() instanceof EntityPlayer || source.isExplosion())
        {
            this.attackDragonFrom(source, p_70965_3_);
        }

        return true;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (source instanceof EntityDamageSource && ((EntityDamageSource)source).getIsThornsDamage())
        {
            this.attackDragonFrom(source, amount);
        }

        return false;
    }

    /**
     * Provides a way to cause damage to an ender dragon.
     */
    protected boolean attackDragonFrom(DamageSource source, float amount)
    {
        return super.attackEntityFrom(source, amount);
    }

    /**
     * Called by the /kill command.
     */
    public void onKillCommand()
    {
        this.setDead();
    }

    /**
     * handles entity death timer, experience orb and particle creation
     */
    protected void onDeathUpdate()
    {
        ++this.deathTicks;

        if (this.deathTicks >= 180 && this.deathTicks <= 200)
        {
            float lvt_1_1_ = (this.rand.nextFloat() - 0.5F) * 8.0F;
            float lvt_2_1_ = (this.rand.nextFloat() - 0.5F) * 4.0F;
            float lvt_3_1_ = (this.rand.nextFloat() - 0.5F) * 8.0F;
            this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.posX + (double)lvt_1_1_, this.posY + 2.0D + (double)lvt_2_1_, this.posZ + (double)lvt_3_1_, 0.0D, 0.0D, 0.0D, new int[0]);
        }

        boolean lvt_1_2_ = this.worldObj.getGameRules().getBoolean("doMobLoot");

        if (!this.worldObj.isRemote)
        {
            if (this.deathTicks > 150 && this.deathTicks % 5 == 0 && lvt_1_2_)
            {
                int lvt_2_2_ = 1000;

                while (lvt_2_2_ > 0)
                {
                    int lvt_3_2_ = EntityXPOrb.getXPSplit(lvt_2_2_);
                    lvt_2_2_ -= lvt_3_2_;
                    this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, lvt_3_2_));
                }
            }

            if (this.deathTicks == 1)
            {
                this.worldObj.playBroadcastSound(1018, new BlockPos(this), 0);
            }
        }

        this.moveEntity(0.0D, 0.10000000149011612D, 0.0D);
        this.renderYawOffset = this.rotationYaw += 20.0F;

        if (this.deathTicks == 200 && !this.worldObj.isRemote)
        {
            if (lvt_1_2_)
            {
                int lvt_2_3_ = 2000;

                while (lvt_2_3_ > 0)
                {
                    int lvt_3_3_ = EntityXPOrb.getXPSplit(lvt_2_3_);
                    lvt_2_3_ -= lvt_3_3_;
                    this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, lvt_3_3_));
                }
            }

            this.generatePortal(new BlockPos(this.posX, 64.0D, this.posZ));
            this.setDead();
        }
    }

    /**
     * Generate the portal when the dragon dies
     */
    private void generatePortal(BlockPos pos)
    {
        int lvt_2_1_ = 4;
        double lvt_3_1_ = 12.25D;
        double lvt_5_1_ = 6.25D;

        for (int lvt_7_1_ = -1; lvt_7_1_ <= 32; ++lvt_7_1_)
        {
            for (int lvt_8_1_ = -4; lvt_8_1_ <= 4; ++lvt_8_1_)
            {
                for (int lvt_9_1_ = -4; lvt_9_1_ <= 4; ++lvt_9_1_)
                {
                    double lvt_10_1_ = (double)(lvt_8_1_ * lvt_8_1_ + lvt_9_1_ * lvt_9_1_);

                    if (lvt_10_1_ <= 12.25D)
                    {
                        BlockPos lvt_12_1_ = pos.add(lvt_8_1_, lvt_7_1_, lvt_9_1_);

                        if (lvt_7_1_ < 0)
                        {
                            if (lvt_10_1_ <= 6.25D)
                            {
                                this.worldObj.setBlockState(lvt_12_1_, Blocks.bedrock.getDefaultState());
                            }
                        }
                        else if (lvt_7_1_ > 0)
                        {
                            this.worldObj.setBlockState(lvt_12_1_, Blocks.air.getDefaultState());
                        }
                        else if (lvt_10_1_ > 6.25D)
                        {
                            this.worldObj.setBlockState(lvt_12_1_, Blocks.bedrock.getDefaultState());
                        }
                        else
                        {
                            this.worldObj.setBlockState(lvt_12_1_, Blocks.end_portal.getDefaultState());
                        }
                    }
                }
            }
        }

        this.worldObj.setBlockState(pos, Blocks.bedrock.getDefaultState());
        this.worldObj.setBlockState(pos.up(), Blocks.bedrock.getDefaultState());
        BlockPos lvt_7_2_ = pos.up(2);
        this.worldObj.setBlockState(lvt_7_2_, Blocks.bedrock.getDefaultState());
        this.worldObj.setBlockState(lvt_7_2_.west(), Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.EAST));
        this.worldObj.setBlockState(lvt_7_2_.east(), Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.WEST));
        this.worldObj.setBlockState(lvt_7_2_.north(), Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.SOUTH));
        this.worldObj.setBlockState(lvt_7_2_.south(), Blocks.torch.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.NORTH));
        this.worldObj.setBlockState(pos.up(3), Blocks.bedrock.getDefaultState());
        this.worldObj.setBlockState(pos.up(4), Blocks.dragon_egg.getDefaultState());
    }

    /**
     * Makes the entity despawn if requirements are reached
     */
    protected void despawnEntity()
    {
    }

    /**
     * Return the Entity parts making up this Entity (currently only for dragons)
     */
    public Entity[] getParts()
    {
        return this.dragonPartArray;
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return false;
    }

    public World getWorld()
    {
        return this.worldObj;
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.enderdragon.growl";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.enderdragon.hit";
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume()
    {
        return 5.0F;
    }
}
