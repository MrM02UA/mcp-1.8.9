package net.minecraft.entity.boss;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityWither extends EntityMob implements IBossDisplayData, IRangedAttackMob
{
    private float[] field_82220_d = new float[2];
    private float[] field_82221_e = new float[2];
    private float[] field_82217_f = new float[2];
    private float[] field_82218_g = new float[2];
    private int[] field_82223_h = new int[2];
    private int[] field_82224_i = new int[2];

    /** Time before the Wither tries to break blocks */
    private int blockBreakCounter;
    private static final Predicate<Entity> attackEntitySelector = new Predicate<Entity>()
    {
        public boolean apply(Entity p_apply_1_)
        {
            return p_apply_1_ instanceof EntityLivingBase && ((EntityLivingBase)p_apply_1_).getCreatureAttribute() != EnumCreatureAttribute.UNDEAD;
        }
        public boolean apply(Object p_apply_1_)
        {
            return this.apply((Entity)p_apply_1_);
        }
    };

    public EntityWither(World worldIn)
    {
        super(worldIn);
        this.setHealth(this.getMaxHealth());
        this.setSize(0.9F, 3.5F);
        this.isImmuneToFire = true;
        ((PathNavigateGround)this.getNavigator()).setCanSwim(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIArrowAttack(this, 1.0D, 40, 20.0F));
        this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityLiving.class, 0, false, false, attackEntitySelector));
        this.experienceValue = 50;
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(17, new Integer(0));
        this.dataWatcher.addObject(18, new Integer(0));
        this.dataWatcher.addObject(19, new Integer(0));
        this.dataWatcher.addObject(20, new Integer(0));
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("Invul", this.getInvulTime());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        super.readEntityFromNBT(tagCompund);
        this.setInvulTime(tagCompund.getInteger("Invul"));
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.wither.idle";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.wither.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.wither.death";
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        this.motionY *= 0.6000000238418579D;

        if (!this.worldObj.isRemote && this.getWatchedTargetId(0) > 0)
        {
            Entity lvt_1_1_ = this.worldObj.getEntityByID(this.getWatchedTargetId(0));

            if (lvt_1_1_ != null)
            {
                if (this.posY < lvt_1_1_.posY || !this.isArmored() && this.posY < lvt_1_1_.posY + 5.0D)
                {
                    if (this.motionY < 0.0D)
                    {
                        this.motionY = 0.0D;
                    }

                    this.motionY += (0.5D - this.motionY) * 0.6000000238418579D;
                }

                double lvt_2_1_ = lvt_1_1_.posX - this.posX;
                double lvt_4_1_ = lvt_1_1_.posZ - this.posZ;
                double lvt_6_1_ = lvt_2_1_ * lvt_2_1_ + lvt_4_1_ * lvt_4_1_;

                if (lvt_6_1_ > 9.0D)
                {
                    double lvt_8_1_ = (double)MathHelper.sqrt_double(lvt_6_1_);
                    this.motionX += (lvt_2_1_ / lvt_8_1_ * 0.5D - this.motionX) * 0.6000000238418579D;
                    this.motionZ += (lvt_4_1_ / lvt_8_1_ * 0.5D - this.motionZ) * 0.6000000238418579D;
                }
            }
        }

        if (this.motionX * this.motionX + this.motionZ * this.motionZ > 0.05000000074505806D)
        {
            this.rotationYaw = (float)MathHelper.atan2(this.motionZ, this.motionX) * (180F / (float)Math.PI) - 90.0F;
        }

        super.onLivingUpdate();

        for (int lvt_1_2_ = 0; lvt_1_2_ < 2; ++lvt_1_2_)
        {
            this.field_82218_g[lvt_1_2_] = this.field_82221_e[lvt_1_2_];
            this.field_82217_f[lvt_1_2_] = this.field_82220_d[lvt_1_2_];
        }

        for (int lvt_1_3_ = 0; lvt_1_3_ < 2; ++lvt_1_3_)
        {
            int lvt_2_2_ = this.getWatchedTargetId(lvt_1_3_ + 1);
            Entity lvt_3_1_ = null;

            if (lvt_2_2_ > 0)
            {
                lvt_3_1_ = this.worldObj.getEntityByID(lvt_2_2_);
            }

            if (lvt_3_1_ != null)
            {
                double lvt_4_2_ = this.func_82214_u(lvt_1_3_ + 1);
                double lvt_6_2_ = this.func_82208_v(lvt_1_3_ + 1);
                double lvt_8_2_ = this.func_82213_w(lvt_1_3_ + 1);
                double lvt_10_1_ = lvt_3_1_.posX - lvt_4_2_;
                double lvt_12_1_ = lvt_3_1_.posY + (double)lvt_3_1_.getEyeHeight() - lvt_6_2_;
                double lvt_14_1_ = lvt_3_1_.posZ - lvt_8_2_;
                double lvt_16_1_ = (double)MathHelper.sqrt_double(lvt_10_1_ * lvt_10_1_ + lvt_14_1_ * lvt_14_1_);
                float lvt_18_1_ = (float)(MathHelper.atan2(lvt_14_1_, lvt_10_1_) * 180.0D / Math.PI) - 90.0F;
                float lvt_19_1_ = (float)(-(MathHelper.atan2(lvt_12_1_, lvt_16_1_) * 180.0D / Math.PI));
                this.field_82220_d[lvt_1_3_] = this.func_82204_b(this.field_82220_d[lvt_1_3_], lvt_19_1_, 40.0F);
                this.field_82221_e[lvt_1_3_] = this.func_82204_b(this.field_82221_e[lvt_1_3_], lvt_18_1_, 10.0F);
            }
            else
            {
                this.field_82221_e[lvt_1_3_] = this.func_82204_b(this.field_82221_e[lvt_1_3_], this.renderYawOffset, 10.0F);
            }
        }

        boolean lvt_1_4_ = this.isArmored();

        for (int lvt_2_3_ = 0; lvt_2_3_ < 3; ++lvt_2_3_)
        {
            double lvt_3_2_ = this.func_82214_u(lvt_2_3_);
            double lvt_5_1_ = this.func_82208_v(lvt_2_3_);
            double lvt_7_1_ = this.func_82213_w(lvt_2_3_);
            this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, lvt_3_2_ + this.rand.nextGaussian() * 0.30000001192092896D, lvt_5_1_ + this.rand.nextGaussian() * 0.30000001192092896D, lvt_7_1_ + this.rand.nextGaussian() * 0.30000001192092896D, 0.0D, 0.0D, 0.0D, new int[0]);

            if (lvt_1_4_ && this.worldObj.rand.nextInt(4) == 0)
            {
                this.worldObj.spawnParticle(EnumParticleTypes.SPELL_MOB, lvt_3_2_ + this.rand.nextGaussian() * 0.30000001192092896D, lvt_5_1_ + this.rand.nextGaussian() * 0.30000001192092896D, lvt_7_1_ + this.rand.nextGaussian() * 0.30000001192092896D, 0.699999988079071D, 0.699999988079071D, 0.5D, new int[0]);
            }
        }

        if (this.getInvulTime() > 0)
        {
            for (int lvt_2_4_ = 0; lvt_2_4_ < 3; ++lvt_2_4_)
            {
                this.worldObj.spawnParticle(EnumParticleTypes.SPELL_MOB, this.posX + this.rand.nextGaussian() * 1.0D, this.posY + (double)(this.rand.nextFloat() * 3.3F), this.posZ + this.rand.nextGaussian() * 1.0D, 0.699999988079071D, 0.699999988079071D, 0.8999999761581421D, new int[0]);
            }
        }
    }

    protected void updateAITasks()
    {
        if (this.getInvulTime() > 0)
        {
            int lvt_1_1_ = this.getInvulTime() - 1;

            if (lvt_1_1_ <= 0)
            {
                this.worldObj.newExplosion(this, this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, 7.0F, false, this.worldObj.getGameRules().getBoolean("mobGriefing"));
                this.worldObj.playBroadcastSound(1013, new BlockPos(this), 0);
            }

            this.setInvulTime(lvt_1_1_);

            if (this.ticksExisted % 10 == 0)
            {
                this.heal(10.0F);
            }
        }
        else
        {
            super.updateAITasks();

            for (int lvt_1_2_ = 1; lvt_1_2_ < 3; ++lvt_1_2_)
            {
                if (this.ticksExisted >= this.field_82223_h[lvt_1_2_ - 1])
                {
                    this.field_82223_h[lvt_1_2_ - 1] = this.ticksExisted + 10 + this.rand.nextInt(10);

                    if (this.worldObj.getDifficulty() == EnumDifficulty.NORMAL || this.worldObj.getDifficulty() == EnumDifficulty.HARD)
                    {
                        int var10001 = lvt_1_2_ - 1;
                        int var10003 = this.field_82224_i[lvt_1_2_ - 1];
                        this.field_82224_i[var10001] = this.field_82224_i[lvt_1_2_ - 1] + 1;

                        if (var10003 > 15)
                        {
                            float lvt_2_1_ = 10.0F;
                            float lvt_3_1_ = 5.0F;
                            double lvt_4_1_ = MathHelper.getRandomDoubleInRange(this.rand, this.posX - (double)lvt_2_1_, this.posX + (double)lvt_2_1_);
                            double lvt_6_1_ = MathHelper.getRandomDoubleInRange(this.rand, this.posY - (double)lvt_3_1_, this.posY + (double)lvt_3_1_);
                            double lvt_8_1_ = MathHelper.getRandomDoubleInRange(this.rand, this.posZ - (double)lvt_2_1_, this.posZ + (double)lvt_2_1_);
                            this.launchWitherSkullToCoords(lvt_1_2_ + 1, lvt_4_1_, lvt_6_1_, lvt_8_1_, true);
                            this.field_82224_i[lvt_1_2_ - 1] = 0;
                        }
                    }

                    int lvt_2_2_ = this.getWatchedTargetId(lvt_1_2_);

                    if (lvt_2_2_ > 0)
                    {
                        Entity lvt_3_2_ = this.worldObj.getEntityByID(lvt_2_2_);

                        if (lvt_3_2_ != null && lvt_3_2_.isEntityAlive() && this.getDistanceSqToEntity(lvt_3_2_) <= 900.0D && this.canEntityBeSeen(lvt_3_2_))
                        {
                            if (lvt_3_2_ instanceof EntityPlayer && ((EntityPlayer)lvt_3_2_).capabilities.disableDamage)
                            {
                                this.updateWatchedTargetId(lvt_1_2_, 0);
                            }
                            else
                            {
                                this.launchWitherSkullToEntity(lvt_1_2_ + 1, (EntityLivingBase)lvt_3_2_);
                                this.field_82223_h[lvt_1_2_ - 1] = this.ticksExisted + 40 + this.rand.nextInt(20);
                                this.field_82224_i[lvt_1_2_ - 1] = 0;
                            }
                        }
                        else
                        {
                            this.updateWatchedTargetId(lvt_1_2_, 0);
                        }
                    }
                    else
                    {
                        List<EntityLivingBase> lvt_3_3_ = this.worldObj.<EntityLivingBase>getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().expand(20.0D, 8.0D, 20.0D), Predicates.and(attackEntitySelector, EntitySelectors.NOT_SPECTATING));

                        for (int lvt_4_2_ = 0; lvt_4_2_ < 10 && !lvt_3_3_.isEmpty(); ++lvt_4_2_)
                        {
                            EntityLivingBase lvt_5_1_ = (EntityLivingBase)lvt_3_3_.get(this.rand.nextInt(lvt_3_3_.size()));

                            if (lvt_5_1_ != this && lvt_5_1_.isEntityAlive() && this.canEntityBeSeen(lvt_5_1_))
                            {
                                if (lvt_5_1_ instanceof EntityPlayer)
                                {
                                    if (!((EntityPlayer)lvt_5_1_).capabilities.disableDamage)
                                    {
                                        this.updateWatchedTargetId(lvt_1_2_, lvt_5_1_.getEntityId());
                                    }
                                }
                                else
                                {
                                    this.updateWatchedTargetId(lvt_1_2_, lvt_5_1_.getEntityId());
                                }

                                break;
                            }

                            lvt_3_3_.remove(lvt_5_1_);
                        }
                    }
                }
            }

            if (this.getAttackTarget() != null)
            {
                this.updateWatchedTargetId(0, this.getAttackTarget().getEntityId());
            }
            else
            {
                this.updateWatchedTargetId(0, 0);
            }

            if (this.blockBreakCounter > 0)
            {
                --this.blockBreakCounter;

                if (this.blockBreakCounter == 0 && this.worldObj.getGameRules().getBoolean("mobGriefing"))
                {
                    int lvt_1_3_ = MathHelper.floor_double(this.posY);
                    int lvt_2_3_ = MathHelper.floor_double(this.posX);
                    int lvt_3_4_ = MathHelper.floor_double(this.posZ);
                    boolean lvt_4_3_ = false;

                    for (int lvt_5_2_ = -1; lvt_5_2_ <= 1; ++lvt_5_2_)
                    {
                        for (int lvt_6_2_ = -1; lvt_6_2_ <= 1; ++lvt_6_2_)
                        {
                            for (int lvt_7_1_ = 0; lvt_7_1_ <= 3; ++lvt_7_1_)
                            {
                                int lvt_8_2_ = lvt_2_3_ + lvt_5_2_;
                                int lvt_9_1_ = lvt_1_3_ + lvt_7_1_;
                                int lvt_10_1_ = lvt_3_4_ + lvt_6_2_;
                                BlockPos lvt_11_1_ = new BlockPos(lvt_8_2_, lvt_9_1_, lvt_10_1_);
                                Block lvt_12_1_ = this.worldObj.getBlockState(lvt_11_1_).getBlock();

                                if (lvt_12_1_.getMaterial() != Material.air && canDestroyBlock(lvt_12_1_))
                                {
                                    lvt_4_3_ = this.worldObj.destroyBlock(lvt_11_1_, true) || lvt_4_3_;
                                }
                            }
                        }
                    }

                    if (lvt_4_3_)
                    {
                        this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1012, new BlockPos(this), 0);
                    }
                }
            }

            if (this.ticksExisted % 20 == 0)
            {
                this.heal(1.0F);
            }
        }
    }

    public static boolean canDestroyBlock(Block p_181033_0_)
    {
        return p_181033_0_ != Blocks.bedrock && p_181033_0_ != Blocks.end_portal && p_181033_0_ != Blocks.end_portal_frame && p_181033_0_ != Blocks.command_block && p_181033_0_ != Blocks.barrier;
    }

    public void func_82206_m()
    {
        this.setInvulTime(220);
        this.setHealth(this.getMaxHealth() / 3.0F);
    }

    /**
     * Sets the Entity inside a web block.
     */
    public void setInWeb()
    {
    }

    /**
     * Returns the current armor value as determined by a call to InventoryPlayer.getTotalArmorValue
     */
    public int getTotalArmorValue()
    {
        return 4;
    }

    private double func_82214_u(int p_82214_1_)
    {
        if (p_82214_1_ <= 0)
        {
            return this.posX;
        }
        else
        {
            float lvt_2_1_ = (this.renderYawOffset + (float)(180 * (p_82214_1_ - 1))) / 180.0F * (float)Math.PI;
            float lvt_3_1_ = MathHelper.cos(lvt_2_1_);
            return this.posX + (double)lvt_3_1_ * 1.3D;
        }
    }

    private double func_82208_v(int p_82208_1_)
    {
        return p_82208_1_ <= 0 ? this.posY + 3.0D : this.posY + 2.2D;
    }

    private double func_82213_w(int p_82213_1_)
    {
        if (p_82213_1_ <= 0)
        {
            return this.posZ;
        }
        else
        {
            float lvt_2_1_ = (this.renderYawOffset + (float)(180 * (p_82213_1_ - 1))) / 180.0F * (float)Math.PI;
            float lvt_3_1_ = MathHelper.sin(lvt_2_1_);
            return this.posZ + (double)lvt_3_1_ * 1.3D;
        }
    }

    private float func_82204_b(float p_82204_1_, float p_82204_2_, float p_82204_3_)
    {
        float lvt_4_1_ = MathHelper.wrapAngleTo180_float(p_82204_2_ - p_82204_1_);

        if (lvt_4_1_ > p_82204_3_)
        {
            lvt_4_1_ = p_82204_3_;
        }

        if (lvt_4_1_ < -p_82204_3_)
        {
            lvt_4_1_ = -p_82204_3_;
        }

        return p_82204_1_ + lvt_4_1_;
    }

    private void launchWitherSkullToEntity(int p_82216_1_, EntityLivingBase p_82216_2_)
    {
        this.launchWitherSkullToCoords(p_82216_1_, p_82216_2_.posX, p_82216_2_.posY + (double)p_82216_2_.getEyeHeight() * 0.5D, p_82216_2_.posZ, p_82216_1_ == 0 && this.rand.nextFloat() < 0.001F);
    }

    /**
     * Launches a Wither skull toward (par2, par4, par6)
     */
    private void launchWitherSkullToCoords(int p_82209_1_, double x, double y, double z, boolean invulnerable)
    {
        this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1014, new BlockPos(this), 0);
        double lvt_9_1_ = this.func_82214_u(p_82209_1_);
        double lvt_11_1_ = this.func_82208_v(p_82209_1_);
        double lvt_13_1_ = this.func_82213_w(p_82209_1_);
        double lvt_15_1_ = x - lvt_9_1_;
        double lvt_17_1_ = y - lvt_11_1_;
        double lvt_19_1_ = z - lvt_13_1_;
        EntityWitherSkull lvt_21_1_ = new EntityWitherSkull(this.worldObj, this, lvt_15_1_, lvt_17_1_, lvt_19_1_);

        if (invulnerable)
        {
            lvt_21_1_.setInvulnerable(true);
        }

        lvt_21_1_.posY = lvt_11_1_;
        lvt_21_1_.posX = lvt_9_1_;
        lvt_21_1_.posZ = lvt_13_1_;
        this.worldObj.spawnEntityInWorld(lvt_21_1_);
    }

    /**
     * Attack the specified entity using a ranged attack.
     */
    public void attackEntityWithRangedAttack(EntityLivingBase target, float p_82196_2_)
    {
        this.launchWitherSkullToEntity(0, target);
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
        else if (source != DamageSource.drown && !(source.getEntity() instanceof EntityWither))
        {
            if (this.getInvulTime() > 0 && source != DamageSource.outOfWorld)
            {
                return false;
            }
            else
            {
                if (this.isArmored())
                {
                    Entity lvt_3_1_ = source.getSourceOfDamage();

                    if (lvt_3_1_ instanceof EntityArrow)
                    {
                        return false;
                    }
                }

                Entity lvt_3_2_ = source.getEntity();

                if (lvt_3_2_ != null && !(lvt_3_2_ instanceof EntityPlayer) && lvt_3_2_ instanceof EntityLivingBase && ((EntityLivingBase)lvt_3_2_).getCreatureAttribute() == this.getCreatureAttribute())
                {
                    return false;
                }
                else
                {
                    if (this.blockBreakCounter <= 0)
                    {
                        this.blockBreakCounter = 20;
                    }

                    for (int lvt_4_1_ = 0; lvt_4_1_ < this.field_82224_i.length; ++lvt_4_1_)
                    {
                        this.field_82224_i[lvt_4_1_] += 3;
                    }

                    return super.attackEntityFrom(source, amount);
                }
            }
        }
        else
        {
            return false;
        }
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
        EntityItem lvt_3_1_ = this.dropItem(Items.nether_star, 1);

        if (lvt_3_1_ != null)
        {
            lvt_3_1_.setNoDespawn();
        }

        if (!this.worldObj.isRemote)
        {
            for (EntityPlayer lvt_5_1_ : this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, this.getEntityBoundingBox().expand(50.0D, 100.0D, 50.0D)))
            {
                lvt_5_1_.triggerAchievement(AchievementList.killWither);
            }
        }
    }

    /**
     * Makes the entity despawn if requirements are reached
     */
    protected void despawnEntity()
    {
        this.entityAge = 0;
    }

    public int getBrightnessForRender(float partialTicks)
    {
        return 15728880;
    }

    public void fall(float distance, float damageMultiplier)
    {
    }

    /**
     * adds a PotionEffect to the entity
     */
    public void addPotionEffect(PotionEffect potioneffectIn)
    {
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(300.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.6000000238418579D);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0D);
    }

    public float func_82207_a(int p_82207_1_)
    {
        return this.field_82221_e[p_82207_1_];
    }

    public float func_82210_r(int p_82210_1_)
    {
        return this.field_82220_d[p_82210_1_];
    }

    public int getInvulTime()
    {
        return this.dataWatcher.getWatchableObjectInt(20);
    }

    public void setInvulTime(int p_82215_1_)
    {
        this.dataWatcher.updateObject(20, Integer.valueOf(p_82215_1_));
    }

    /**
     * Returns the target entity ID if present, or -1 if not @param par1 The target offset, should be from 0-2
     */
    public int getWatchedTargetId(int p_82203_1_)
    {
        return this.dataWatcher.getWatchableObjectInt(17 + p_82203_1_);
    }

    /**
     * Updates the target entity ID
     */
    public void updateWatchedTargetId(int targetOffset, int newId)
    {
        this.dataWatcher.updateObject(17 + targetOffset, Integer.valueOf(newId));
    }

    /**
     * Returns whether the wither is armored with its boss armor or not by checking whether its health is below half of
     * its maximum.
     */
    public boolean isArmored()
    {
        return this.getHealth() <= this.getMaxHealth() / 2.0F;
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.UNDEAD;
    }

    /**
     * Called when a player mounts an entity. e.g. mounts a pig, mounts a boat.
     */
    public void mountEntity(Entity entityIn)
    {
        this.ridingEntity = null;
    }
}
