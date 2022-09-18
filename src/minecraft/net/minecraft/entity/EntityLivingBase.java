package net.minecraft.entity;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityLivingBase extends Entity
{
    private static final UUID sprintingSpeedBoostModifierUUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    private static final AttributeModifier sprintingSpeedBoostModifier = (new AttributeModifier(sprintingSpeedBoostModifierUUID, "Sprinting speed boost", 0.30000001192092896D, 2)).setSaved(false);
    private BaseAttributeMap attributeMap;
    private final CombatTracker _combatTracker = new CombatTracker(this);
    private final Map<Integer, PotionEffect> activePotionsMap = Maps.newHashMap();

    /** The equipment this mob was previously wearing, used for syncing. */
    private final ItemStack[] previousEquipment = new ItemStack[5];

    /** Whether an arm swing is currently in progress. */
    public boolean isSwingInProgress;
    public int swingProgressInt;
    public int arrowHitTimer;

    /**
     * The amount of time remaining this entity should act 'hurt'. (Visual appearance of red tint)
     */
    public int hurtTime;

    /** What the hurt time was max set to last. */
    public int maxHurtTime;

    /** The yaw at which this entity was last attacked from. */
    public float attackedAtYaw;

    /**
     * The amount of time remaining this entity should act 'dead', i.e. have a corpse in the world.
     */
    public int deathTime;
    public float prevSwingProgress;
    public float swingProgress;
    public float prevLimbSwingAmount;
    public float limbSwingAmount;

    /**
     * Only relevant when limbYaw is not 0(the entity is moving). Influences where in its swing legs and arms currently
     * are.
     */
    public float limbSwing;
    public int maxHurtResistantTime = 20;
    public float prevCameraPitch;
    public float cameraPitch;
    public float randomUnused2;
    public float randomUnused1;
    public float renderYawOffset;
    public float prevRenderYawOffset;

    /** Entity head rotation yaw */
    public float rotationYawHead;

    /** Entity head rotation yaw at previous tick */
    public float prevRotationYawHead;

    /**
     * A factor used to determine how far this entity will move each tick if it is jumping or falling.
     */
    public float jumpMovementFactor = 0.02F;

    /** The most recent player that has attacked this entity */
    protected EntityPlayer attackingPlayer;

    /**
     * Set to 60 when hit by the player or the player's wolf, then decrements. Used to determine whether the entity
     * should drop items on death.
     */
    protected int recentlyHit;

    /**
     * This gets set on entity death, but never used. Looks like a duplicate of isDead
     */
    protected boolean dead;

    /** The age of this EntityLiving (used to determine when it dies) */
    protected int entityAge;
    protected float prevOnGroundSpeedFactor;
    protected float onGroundSpeedFactor;
    protected float movedDistance;
    protected float prevMovedDistance;
    protected float unused180;

    /** The score value of the Mob, the amount of points the mob is worth. */
    protected int scoreValue;

    /**
     * Damage taken in the last hit. Mobs are resistant to damage less than this for a short time after taking damage.
     */
    protected float lastDamage;

    /** used to check whether entity is jumping. */
    protected boolean isJumping;
    public float moveStrafing;
    public float moveForward;
    protected float randomYawVelocity;

    /**
     * The number of updates over which the new position and rotation are to be applied to the entity.
     */
    protected int newPosRotationIncrements;

    /** The new X position to be applied to the entity. */
    protected double newPosX;

    /** The new Y position to be applied to the entity. */
    protected double newPosY;
    protected double newPosZ;

    /** The new yaw rotation to be applied to the entity. */
    protected double newRotationYaw;

    /** The new yaw rotation to be applied to the entity. */
    protected double newRotationPitch;

    /** Whether the DataWatcher needs to be updated with the active potions */
    private boolean potionsNeedUpdate = true;

    /** is only being set, has no uses as of MC 1.1 */
    private EntityLivingBase entityLivingToAttack;
    private int revengeTimer;
    private EntityLivingBase lastAttacker;

    /** Holds the value of ticksExisted when setLastAttacker was last called. */
    private int lastAttackerTime;

    /**
     * A factor used to determine how far this entity will move each tick if it is walking on land. Adjusted by speed,
     * and slipperiness of the current block.
     */
    private float landMovementFactor;

    /** Number of ticks since last jump */
    private int jumpTicks;
    private float absorptionAmount;

    /**
     * Called by the /kill command.
     */
    public void onKillCommand()
    {
        this.attackEntityFrom(DamageSource.outOfWorld, Float.MAX_VALUE);
    }

    public EntityLivingBase(World worldIn)
    {
        super(worldIn);
        this.applyEntityAttributes();
        this.setHealth(this.getMaxHealth());
        this.preventEntitySpawning = true;
        this.randomUnused1 = (float)((Math.random() + 1.0D) * 0.009999999776482582D);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.randomUnused2 = (float)Math.random() * 12398.0F;
        this.rotationYaw = (float)(Math.random() * Math.PI * 2.0D);
        this.rotationYawHead = this.rotationYaw;
        this.stepHeight = 0.6F;
    }

    protected void entityInit()
    {
        this.dataWatcher.addObject(7, Integer.valueOf(0));
        this.dataWatcher.addObject(8, Byte.valueOf((byte)0));
        this.dataWatcher.addObject(9, Byte.valueOf((byte)0));
        this.dataWatcher.addObject(6, Float.valueOf(1.0F));
    }

    protected void applyEntityAttributes()
    {
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.maxHealth);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.knockbackResistance);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.movementSpeed);
    }

    protected void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos)
    {
        if (!this.isInWater())
        {
            this.handleWaterMovement();
        }

        if (!this.worldObj.isRemote && this.fallDistance > 3.0F && onGroundIn)
        {
            IBlockState lvt_6_1_ = this.worldObj.getBlockState(pos);
            Block lvt_7_1_ = lvt_6_1_.getBlock();
            float lvt_8_1_ = (float)MathHelper.ceiling_float_int(this.fallDistance - 3.0F);

            if (lvt_7_1_.getMaterial() != Material.air)
            {
                double lvt_9_1_ = (double)Math.min(0.2F + lvt_8_1_ / 15.0F, 10.0F);

                if (lvt_9_1_ > 2.5D)
                {
                    lvt_9_1_ = 2.5D;
                }

                int lvt_11_1_ = (int)(150.0D * lvt_9_1_);
                ((WorldServer)this.worldObj).spawnParticle(EnumParticleTypes.BLOCK_DUST, this.posX, this.posY, this.posZ, lvt_11_1_, 0.0D, 0.0D, 0.0D, 0.15000000596046448D, new int[] {Block.getStateId(lvt_6_1_)});
            }
        }

        super.updateFallState(y, onGroundIn, blockIn, pos);
    }

    public boolean canBreatheUnderwater()
    {
        return false;
    }

    /**
     * Gets called every tick from main Entity class
     */
    public void onEntityUpdate()
    {
        this.prevSwingProgress = this.swingProgress;
        super.onEntityUpdate();
        this.worldObj.theProfiler.startSection("livingEntityBaseTick");
        boolean lvt_1_1_ = this instanceof EntityPlayer;

        if (this.isEntityAlive())
        {
            if (this.isEntityInsideOpaqueBlock())
            {
                this.attackEntityFrom(DamageSource.inWall, 1.0F);
            }
            else if (lvt_1_1_ && !this.worldObj.getWorldBorder().contains(this.getEntityBoundingBox()))
            {
                double lvt_2_1_ = this.worldObj.getWorldBorder().getClosestDistance(this) + this.worldObj.getWorldBorder().getDamageBuffer();

                if (lvt_2_1_ < 0.0D)
                {
                    this.attackEntityFrom(DamageSource.inWall, (float)Math.max(1, MathHelper.floor_double(-lvt_2_1_ * this.worldObj.getWorldBorder().getDamageAmount())));
                }
            }
        }

        if (this.isImmuneToFire() || this.worldObj.isRemote)
        {
            this.extinguish();
        }

        boolean lvt_2_2_ = lvt_1_1_ && ((EntityPlayer)this).capabilities.disableDamage;

        if (this.isEntityAlive())
        {
            if (this.isInsideOfMaterial(Material.water))
            {
                if (!this.canBreatheUnderwater() && !this.isPotionActive(Potion.waterBreathing.id) && !lvt_2_2_)
                {
                    this.setAir(this.decreaseAirSupply(this.getAir()));

                    if (this.getAir() == -20)
                    {
                        this.setAir(0);

                        for (int lvt_3_1_ = 0; lvt_3_1_ < 8; ++lvt_3_1_)
                        {
                            float lvt_4_1_ = this.rand.nextFloat() - this.rand.nextFloat();
                            float lvt_5_1_ = this.rand.nextFloat() - this.rand.nextFloat();
                            float lvt_6_1_ = this.rand.nextFloat() - this.rand.nextFloat();
                            this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX + (double)lvt_4_1_, this.posY + (double)lvt_5_1_, this.posZ + (double)lvt_6_1_, this.motionX, this.motionY, this.motionZ, new int[0]);
                        }

                        this.attackEntityFrom(DamageSource.drown, 2.0F);
                    }
                }

                if (!this.worldObj.isRemote && this.isRiding() && this.ridingEntity instanceof EntityLivingBase)
                {
                    this.mountEntity((Entity)null);
                }
            }
            else
            {
                this.setAir(300);
            }
        }

        if (this.isEntityAlive() && this.isWet())
        {
            this.extinguish();
        }

        this.prevCameraPitch = this.cameraPitch;

        if (this.hurtTime > 0)
        {
            --this.hurtTime;
        }

        if (this.hurtResistantTime > 0 && !(this instanceof EntityPlayerMP))
        {
            --this.hurtResistantTime;
        }

        if (this.getHealth() <= 0.0F)
        {
            this.onDeathUpdate();
        }

        if (this.recentlyHit > 0)
        {
            --this.recentlyHit;
        }
        else
        {
            this.attackingPlayer = null;
        }

        if (this.lastAttacker != null && !this.lastAttacker.isEntityAlive())
        {
            this.lastAttacker = null;
        }

        if (this.entityLivingToAttack != null)
        {
            if (!this.entityLivingToAttack.isEntityAlive())
            {
                this.setRevengeTarget((EntityLivingBase)null);
            }
            else if (this.ticksExisted - this.revengeTimer > 100)
            {
                this.setRevengeTarget((EntityLivingBase)null);
            }
        }

        this.updatePotionEffects();
        this.prevMovedDistance = this.movedDistance;
        this.prevRenderYawOffset = this.renderYawOffset;
        this.prevRotationYawHead = this.rotationYawHead;
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
        this.worldObj.theProfiler.endSection();
    }

    /**
     * If Animal, checks if the age timer is negative
     */
    public boolean isChild()
    {
        return false;
    }

    /**
     * handles entity death timer, experience orb and particle creation
     */
    protected void onDeathUpdate()
    {
        ++this.deathTime;

        if (this.deathTime == 20)
        {
            if (!this.worldObj.isRemote && (this.recentlyHit > 0 || this.isPlayer()) && this.canDropLoot() && this.worldObj.getGameRules().getBoolean("doMobLoot"))
            {
                int lvt_1_1_ = this.getExperiencePoints(this.attackingPlayer);

                while (lvt_1_1_ > 0)
                {
                    int lvt_2_1_ = EntityXPOrb.getXPSplit(lvt_1_1_);
                    lvt_1_1_ -= lvt_2_1_;
                    this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, lvt_2_1_));
                }
            }

            this.setDead();

            for (int lvt_1_2_ = 0; lvt_1_2_ < 20; ++lvt_1_2_)
            {
                double lvt_2_2_ = this.rand.nextGaussian() * 0.02D;
                double lvt_4_1_ = this.rand.nextGaussian() * 0.02D;
                double lvt_6_1_ = this.rand.nextGaussian() * 0.02D;
                this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, lvt_2_2_, lvt_4_1_, lvt_6_1_, new int[0]);
            }
        }
    }

    /**
     * Entity won't drop items or experience points if this returns false
     */
    protected boolean canDropLoot()
    {
        return !this.isChild();
    }

    /**
     * Decrements the entity's air supply when underwater
     */
    protected int decreaseAirSupply(int p_70682_1_)
    {
        int lvt_2_1_ = EnchantmentHelper.getRespiration(this);
        return lvt_2_1_ > 0 && this.rand.nextInt(lvt_2_1_ + 1) > 0 ? p_70682_1_ : p_70682_1_ - 1;
    }

    /**
     * Get the experience points the entity currently has.
     */
    protected int getExperiencePoints(EntityPlayer player)
    {
        return 0;
    }

    /**
     * Only use is to identify if class is an instance of player for experience dropping
     */
    protected boolean isPlayer()
    {
        return false;
    }

    public Random getRNG()
    {
        return this.rand;
    }

    public EntityLivingBase getAITarget()
    {
        return this.entityLivingToAttack;
    }

    public int getRevengeTimer()
    {
        return this.revengeTimer;
    }

    public void setRevengeTarget(EntityLivingBase livingBase)
    {
        this.entityLivingToAttack = livingBase;
        this.revengeTimer = this.ticksExisted;
    }

    public EntityLivingBase getLastAttacker()
    {
        return this.lastAttacker;
    }

    public int getLastAttackerTime()
    {
        return this.lastAttackerTime;
    }

    public void setLastAttacker(Entity entityIn)
    {
        if (entityIn instanceof EntityLivingBase)
        {
            this.lastAttacker = (EntityLivingBase)entityIn;
        }
        else
        {
            this.lastAttacker = null;
        }

        this.lastAttackerTime = this.ticksExisted;
    }

    public int getAge()
    {
        return this.entityAge;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        tagCompound.setFloat("HealF", this.getHealth());
        tagCompound.setShort("Health", (short)((int)Math.ceil((double)this.getHealth())));
        tagCompound.setShort("HurtTime", (short)this.hurtTime);
        tagCompound.setInteger("HurtByTimestamp", this.revengeTimer);
        tagCompound.setShort("DeathTime", (short)this.deathTime);
        tagCompound.setFloat("AbsorptionAmount", this.getAbsorptionAmount());

        for (ItemStack lvt_5_1_ : this.getInventory())
        {
            if (lvt_5_1_ != null)
            {
                this.attributeMap.removeAttributeModifiers(lvt_5_1_.getAttributeModifiers());
            }
        }

        tagCompound.setTag("Attributes", SharedMonsterAttributes.writeBaseAttributeMapToNBT(this.getAttributeMap()));

        for (ItemStack lvt_5_2_ : this.getInventory())
        {
            if (lvt_5_2_ != null)
            {
                this.attributeMap.applyAttributeModifiers(lvt_5_2_.getAttributeModifiers());
            }
        }

        if (!this.activePotionsMap.isEmpty())
        {
            NBTTagList lvt_2_3_ = new NBTTagList();

            for (PotionEffect lvt_4_3_ : this.activePotionsMap.values())
            {
                lvt_2_3_.appendTag(lvt_4_3_.writeCustomPotionEffectToNBT(new NBTTagCompound()));
            }

            tagCompound.setTag("ActiveEffects", lvt_2_3_);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        this.setAbsorptionAmount(tagCompund.getFloat("AbsorptionAmount"));

        if (tagCompund.hasKey("Attributes", 9) && this.worldObj != null && !this.worldObj.isRemote)
        {
            SharedMonsterAttributes.setAttributeModifiers(this.getAttributeMap(), tagCompund.getTagList("Attributes", 10));
        }

        if (tagCompund.hasKey("ActiveEffects", 9))
        {
            NBTTagList lvt_2_1_ = tagCompund.getTagList("ActiveEffects", 10);

            for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.tagCount(); ++lvt_3_1_)
            {
                NBTTagCompound lvt_4_1_ = lvt_2_1_.getCompoundTagAt(lvt_3_1_);
                PotionEffect lvt_5_1_ = PotionEffect.readCustomPotionEffectFromNBT(lvt_4_1_);

                if (lvt_5_1_ != null)
                {
                    this.activePotionsMap.put(Integer.valueOf(lvt_5_1_.getPotionID()), lvt_5_1_);
                }
            }
        }

        if (tagCompund.hasKey("HealF", 99))
        {
            this.setHealth(tagCompund.getFloat("HealF"));
        }
        else
        {
            NBTBase lvt_2_2_ = tagCompund.getTag("Health");

            if (lvt_2_2_ == null)
            {
                this.setHealth(this.getMaxHealth());
            }
            else if (lvt_2_2_.getId() == 5)
            {
                this.setHealth(((NBTTagFloat)lvt_2_2_).getFloat());
            }
            else if (lvt_2_2_.getId() == 2)
            {
                this.setHealth((float)((NBTTagShort)lvt_2_2_).getShort());
            }
        }

        this.hurtTime = tagCompund.getShort("HurtTime");
        this.deathTime = tagCompund.getShort("DeathTime");
        this.revengeTimer = tagCompund.getInteger("HurtByTimestamp");
    }

    protected void updatePotionEffects()
    {
        Iterator<Integer> lvt_1_1_ = this.activePotionsMap.keySet().iterator();

        while (lvt_1_1_.hasNext())
        {
            Integer lvt_2_1_ = (Integer)lvt_1_1_.next();
            PotionEffect lvt_3_1_ = (PotionEffect)this.activePotionsMap.get(lvt_2_1_);

            if (!lvt_3_1_.onUpdate(this))
            {
                if (!this.worldObj.isRemote)
                {
                    lvt_1_1_.remove();
                    this.onFinishedPotionEffect(lvt_3_1_);
                }
            }
            else if (lvt_3_1_.getDuration() % 600 == 0)
            {
                this.onChangedPotionEffect(lvt_3_1_, false);
            }
        }

        if (this.potionsNeedUpdate)
        {
            if (!this.worldObj.isRemote)
            {
                this.updatePotionMetadata();
            }

            this.potionsNeedUpdate = false;
        }

        int lvt_2_2_ = this.dataWatcher.getWatchableObjectInt(7);
        boolean lvt_3_2_ = this.dataWatcher.getWatchableObjectByte(8) > 0;

        if (lvt_2_2_ > 0)
        {
            boolean lvt_4_1_ = false;

            if (!this.isInvisible())
            {
                lvt_4_1_ = this.rand.nextBoolean();
            }
            else
            {
                lvt_4_1_ = this.rand.nextInt(15) == 0;
            }

            if (lvt_3_2_)
            {
                lvt_4_1_ &= this.rand.nextInt(5) == 0;
            }

            if (lvt_4_1_ && lvt_2_2_ > 0)
            {
                double lvt_5_1_ = (double)(lvt_2_2_ >> 16 & 255) / 255.0D;
                double lvt_7_1_ = (double)(lvt_2_2_ >> 8 & 255) / 255.0D;
                double lvt_9_1_ = (double)(lvt_2_2_ >> 0 & 255) / 255.0D;
                this.worldObj.spawnParticle(lvt_3_2_ ? EnumParticleTypes.SPELL_MOB_AMBIENT : EnumParticleTypes.SPELL_MOB, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, lvt_5_1_, lvt_7_1_, lvt_9_1_, new int[0]);
            }
        }
    }

    /**
     * Clears potion metadata values if the entity has no potion effects. Otherwise, updates potion effect color,
     * ambience, and invisibility metadata values
     */
    protected void updatePotionMetadata()
    {
        if (this.activePotionsMap.isEmpty())
        {
            this.resetPotionEffectMetadata();
            this.setInvisible(false);
        }
        else
        {
            int lvt_1_1_ = PotionHelper.calcPotionLiquidColor(this.activePotionsMap.values());
            this.dataWatcher.updateObject(8, Byte.valueOf((byte)(PotionHelper.getAreAmbient(this.activePotionsMap.values()) ? 1 : 0)));
            this.dataWatcher.updateObject(7, Integer.valueOf(lvt_1_1_));
            this.setInvisible(this.isPotionActive(Potion.invisibility.id));
        }
    }

    /**
     * Resets the potion effect color and ambience metadata values
     */
    protected void resetPotionEffectMetadata()
    {
        this.dataWatcher.updateObject(8, Byte.valueOf((byte)0));
        this.dataWatcher.updateObject(7, Integer.valueOf(0));
    }

    public void clearActivePotions()
    {
        Iterator<Integer> lvt_1_1_ = this.activePotionsMap.keySet().iterator();

        while (lvt_1_1_.hasNext())
        {
            Integer lvt_2_1_ = (Integer)lvt_1_1_.next();
            PotionEffect lvt_3_1_ = (PotionEffect)this.activePotionsMap.get(lvt_2_1_);

            if (!this.worldObj.isRemote)
            {
                lvt_1_1_.remove();
                this.onFinishedPotionEffect(lvt_3_1_);
            }
        }
    }

    public Collection<PotionEffect> getActivePotionEffects()
    {
        return this.activePotionsMap.values();
    }

    public boolean isPotionActive(int potionId)
    {
        return this.activePotionsMap.containsKey(Integer.valueOf(potionId));
    }

    public boolean isPotionActive(Potion potionIn)
    {
        return this.activePotionsMap.containsKey(Integer.valueOf(potionIn.id));
    }

    /**
     * returns the PotionEffect for the supplied Potion if it is active, null otherwise.
     */
    public PotionEffect getActivePotionEffect(Potion potionIn)
    {
        return (PotionEffect)this.activePotionsMap.get(Integer.valueOf(potionIn.id));
    }

    /**
     * adds a PotionEffect to the entity
     */
    public void addPotionEffect(PotionEffect potioneffectIn)
    {
        if (this.isPotionApplicable(potioneffectIn))
        {
            if (this.activePotionsMap.containsKey(Integer.valueOf(potioneffectIn.getPotionID())))
            {
                ((PotionEffect)this.activePotionsMap.get(Integer.valueOf(potioneffectIn.getPotionID()))).combine(potioneffectIn);
                this.onChangedPotionEffect((PotionEffect)this.activePotionsMap.get(Integer.valueOf(potioneffectIn.getPotionID())), true);
            }
            else
            {
                this.activePotionsMap.put(Integer.valueOf(potioneffectIn.getPotionID()), potioneffectIn);
                this.onNewPotionEffect(potioneffectIn);
            }
        }
    }

    public boolean isPotionApplicable(PotionEffect potioneffectIn)
    {
        if (this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD)
        {
            int lvt_2_1_ = potioneffectIn.getPotionID();

            if (lvt_2_1_ == Potion.regeneration.id || lvt_2_1_ == Potion.poison.id)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns true if this entity is undead.
     */
    public boolean isEntityUndead()
    {
        return this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
    }

    /**
     * Remove the speified potion effect from this entity.
     */
    public void removePotionEffectClient(int potionId)
    {
        this.activePotionsMap.remove(Integer.valueOf(potionId));
    }

    /**
     * Remove the specified potion effect from this entity.
     */
    public void removePotionEffect(int potionId)
    {
        PotionEffect lvt_2_1_ = (PotionEffect)this.activePotionsMap.remove(Integer.valueOf(potionId));

        if (lvt_2_1_ != null)
        {
            this.onFinishedPotionEffect(lvt_2_1_);
        }
    }

    protected void onNewPotionEffect(PotionEffect id)
    {
        this.potionsNeedUpdate = true;

        if (!this.worldObj.isRemote)
        {
            Potion.potionTypes[id.getPotionID()].applyAttributesModifiersToEntity(this, this.getAttributeMap(), id.getAmplifier());
        }
    }

    protected void onChangedPotionEffect(PotionEffect id, boolean p_70695_2_)
    {
        this.potionsNeedUpdate = true;

        if (p_70695_2_ && !this.worldObj.isRemote)
        {
            Potion.potionTypes[id.getPotionID()].removeAttributesModifiersFromEntity(this, this.getAttributeMap(), id.getAmplifier());
            Potion.potionTypes[id.getPotionID()].applyAttributesModifiersToEntity(this, this.getAttributeMap(), id.getAmplifier());
        }
    }

    protected void onFinishedPotionEffect(PotionEffect effect)
    {
        this.potionsNeedUpdate = true;

        if (!this.worldObj.isRemote)
        {
            Potion.potionTypes[effect.getPotionID()].removeAttributesModifiersFromEntity(this, this.getAttributeMap(), effect.getAmplifier());
        }
    }

    /**
     * Heal living entity (param: amount of half-hearts)
     */
    public void heal(float healAmount)
    {
        float lvt_2_1_ = this.getHealth();

        if (lvt_2_1_ > 0.0F)
        {
            this.setHealth(lvt_2_1_ + healAmount);
        }
    }

    public final float getHealth()
    {
        return this.dataWatcher.getWatchableObjectFloat(6);
    }

    public void setHealth(float health)
    {
        this.dataWatcher.updateObject(6, Float.valueOf(MathHelper.clamp_float(health, 0.0F, this.getMaxHealth())));
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
        else if (this.worldObj.isRemote)
        {
            return false;
        }
        else
        {
            this.entityAge = 0;

            if (this.getHealth() <= 0.0F)
            {
                return false;
            }
            else if (source.isFireDamage() && this.isPotionActive(Potion.fireResistance))
            {
                return false;
            }
            else
            {
                if ((source == DamageSource.anvil || source == DamageSource.fallingBlock) && this.getEquipmentInSlot(4) != null)
                {
                    this.getEquipmentInSlot(4).damageItem((int)(amount * 4.0F + this.rand.nextFloat() * amount * 2.0F), this);
                    amount *= 0.75F;
                }

                this.limbSwingAmount = 1.5F;
                boolean lvt_3_1_ = true;

                if ((float)this.hurtResistantTime > (float)this.maxHurtResistantTime / 2.0F)
                {
                    if (amount <= this.lastDamage)
                    {
                        return false;
                    }

                    this.damageEntity(source, amount - this.lastDamage);
                    this.lastDamage = amount;
                    lvt_3_1_ = false;
                }
                else
                {
                    this.lastDamage = amount;
                    this.hurtResistantTime = this.maxHurtResistantTime;
                    this.damageEntity(source, amount);
                    this.hurtTime = this.maxHurtTime = 10;
                }

                this.attackedAtYaw = 0.0F;
                Entity lvt_4_1_ = source.getEntity();

                if (lvt_4_1_ != null)
                {
                    if (lvt_4_1_ instanceof EntityLivingBase)
                    {
                        this.setRevengeTarget((EntityLivingBase)lvt_4_1_);
                    }

                    if (lvt_4_1_ instanceof EntityPlayer)
                    {
                        this.recentlyHit = 100;
                        this.attackingPlayer = (EntityPlayer)lvt_4_1_;
                    }
                    else if (lvt_4_1_ instanceof EntityWolf)
                    {
                        EntityWolf lvt_5_1_ = (EntityWolf)lvt_4_1_;

                        if (lvt_5_1_.isTamed())
                        {
                            this.recentlyHit = 100;
                            this.attackingPlayer = null;
                        }
                    }
                }

                if (lvt_3_1_)
                {
                    this.worldObj.setEntityState(this, (byte)2);

                    if (source != DamageSource.drown)
                    {
                        this.setBeenAttacked();
                    }

                    if (lvt_4_1_ != null)
                    {
                        double lvt_5_2_ = lvt_4_1_.posX - this.posX;
                        double lvt_7_1_;

                        for (lvt_7_1_ = lvt_4_1_.posZ - this.posZ; lvt_5_2_ * lvt_5_2_ + lvt_7_1_ * lvt_7_1_ < 1.0E-4D; lvt_7_1_ = (Math.random() - Math.random()) * 0.01D)
                        {
                            lvt_5_2_ = (Math.random() - Math.random()) * 0.01D;
                        }

                        this.attackedAtYaw = (float)(MathHelper.atan2(lvt_7_1_, lvt_5_2_) * 180.0D / Math.PI - (double)this.rotationYaw);
                        this.knockBack(lvt_4_1_, amount, lvt_5_2_, lvt_7_1_);
                    }
                    else
                    {
                        this.attackedAtYaw = (float)((int)(Math.random() * 2.0D) * 180);
                    }
                }

                if (this.getHealth() <= 0.0F)
                {
                    String lvt_5_3_ = this.getDeathSound();

                    if (lvt_3_1_ && lvt_5_3_ != null)
                    {
                        this.playSound(lvt_5_3_, this.getSoundVolume(), this.getSoundPitch());
                    }

                    this.onDeath(source);
                }
                else
                {
                    String lvt_5_4_ = this.getHurtSound();

                    if (lvt_3_1_ && lvt_5_4_ != null)
                    {
                        this.playSound(lvt_5_4_, this.getSoundVolume(), this.getSoundPitch());
                    }
                }

                return true;
            }
        }
    }

    /**
     * Renders broken item particles using the given ItemStack
     */
    public void renderBrokenItemStack(ItemStack stack)
    {
        this.playSound("random.break", 0.8F, 0.8F + this.worldObj.rand.nextFloat() * 0.4F);

        for (int lvt_2_1_ = 0; lvt_2_1_ < 5; ++lvt_2_1_)
        {
            Vec3 lvt_3_1_ = new Vec3(((double)this.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
            lvt_3_1_ = lvt_3_1_.rotatePitch(-this.rotationPitch * (float)Math.PI / 180.0F);
            lvt_3_1_ = lvt_3_1_.rotateYaw(-this.rotationYaw * (float)Math.PI / 180.0F);
            double lvt_4_1_ = (double)(-this.rand.nextFloat()) * 0.6D - 0.3D;
            Vec3 lvt_6_1_ = new Vec3(((double)this.rand.nextFloat() - 0.5D) * 0.3D, lvt_4_1_, 0.6D);
            lvt_6_1_ = lvt_6_1_.rotatePitch(-this.rotationPitch * (float)Math.PI / 180.0F);
            lvt_6_1_ = lvt_6_1_.rotateYaw(-this.rotationYaw * (float)Math.PI / 180.0F);
            lvt_6_1_ = lvt_6_1_.addVector(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
            this.worldObj.spawnParticle(EnumParticleTypes.ITEM_CRACK, lvt_6_1_.xCoord, lvt_6_1_.yCoord, lvt_6_1_.zCoord, lvt_3_1_.xCoord, lvt_3_1_.yCoord + 0.05D, lvt_3_1_.zCoord, new int[] {Item.getIdFromItem(stack.getItem())});
        }
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void onDeath(DamageSource cause)
    {
        Entity lvt_2_1_ = cause.getEntity();
        EntityLivingBase lvt_3_1_ = this.getAttackingEntity();

        if (this.scoreValue >= 0 && lvt_3_1_ != null)
        {
            lvt_3_1_.addToPlayerScore(this, this.scoreValue);
        }

        if (lvt_2_1_ != null)
        {
            lvt_2_1_.onKillEntity(this);
        }

        this.dead = true;
        this.getCombatTracker().reset();

        if (!this.worldObj.isRemote)
        {
            int lvt_4_1_ = 0;

            if (lvt_2_1_ instanceof EntityPlayer)
            {
                lvt_4_1_ = EnchantmentHelper.getLootingModifier((EntityLivingBase)lvt_2_1_);
            }

            if (this.canDropLoot() && this.worldObj.getGameRules().getBoolean("doMobLoot"))
            {
                this.dropFewItems(this.recentlyHit > 0, lvt_4_1_);
                this.dropEquipment(this.recentlyHit > 0, lvt_4_1_);

                if (this.recentlyHit > 0 && this.rand.nextFloat() < 0.025F + (float)lvt_4_1_ * 0.01F)
                {
                    this.addRandomDrop();
                }
            }
        }

        this.worldObj.setEntityState(this, (byte)3);
    }

    /**
     * Drop the equipment for this entity.
     *  
     * @param wasRecentlyHit true if this this entity was recently hit by appropriate entity (generally only if player
     * or tameable)
     * @param lootingModifier level of enchanment to be applied to this drop
     */
    protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier)
    {
    }

    /**
     * knocks back this entity
     */
    public void knockBack(Entity entityIn, float p_70653_2_, double p_70653_3_, double p_70653_5_)
    {
        if (this.rand.nextDouble() >= this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue())
        {
            this.isAirBorne = true;
            float lvt_7_1_ = MathHelper.sqrt_double(p_70653_3_ * p_70653_3_ + p_70653_5_ * p_70653_5_);
            float lvt_8_1_ = 0.4F;
            this.motionX /= 2.0D;
            this.motionY /= 2.0D;
            this.motionZ /= 2.0D;
            this.motionX -= p_70653_3_ / (double)lvt_7_1_ * (double)lvt_8_1_;
            this.motionY += (double)lvt_8_1_;
            this.motionZ -= p_70653_5_ / (double)lvt_7_1_ * (double)lvt_8_1_;

            if (this.motionY > 0.4000000059604645D)
            {
                this.motionY = 0.4000000059604645D;
            }
        }
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "game.neutral.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "game.neutral.die";
    }

    /**
     * Causes this Entity to drop a random item.
     */
    protected void addRandomDrop()
    {
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
    }

    /**
     * returns true if this entity is by a ladder, false otherwise
     */
    public boolean isOnLadder()
    {
        int lvt_1_1_ = MathHelper.floor_double(this.posX);
        int lvt_2_1_ = MathHelper.floor_double(this.getEntityBoundingBox().minY);
        int lvt_3_1_ = MathHelper.floor_double(this.posZ);
        Block lvt_4_1_ = this.worldObj.getBlockState(new BlockPos(lvt_1_1_, lvt_2_1_, lvt_3_1_)).getBlock();
        return (lvt_4_1_ == Blocks.ladder || lvt_4_1_ == Blocks.vine) && (!(this instanceof EntityPlayer) || !((EntityPlayer)this).isSpectator());
    }

    /**
     * Checks whether target entity is alive.
     */
    public boolean isEntityAlive()
    {
        return !this.isDead && this.getHealth() > 0.0F;
    }

    public void fall(float distance, float damageMultiplier)
    {
        super.fall(distance, damageMultiplier);
        PotionEffect lvt_3_1_ = this.getActivePotionEffect(Potion.jump);
        float lvt_4_1_ = lvt_3_1_ != null ? (float)(lvt_3_1_.getAmplifier() + 1) : 0.0F;
        int lvt_5_1_ = MathHelper.ceiling_float_int((distance - 3.0F - lvt_4_1_) * damageMultiplier);

        if (lvt_5_1_ > 0)
        {
            this.playSound(this.getFallSoundString(lvt_5_1_), 1.0F, 1.0F);
            this.attackEntityFrom(DamageSource.fall, (float)lvt_5_1_);
            int lvt_6_1_ = MathHelper.floor_double(this.posX);
            int lvt_7_1_ = MathHelper.floor_double(this.posY - 0.20000000298023224D);
            int lvt_8_1_ = MathHelper.floor_double(this.posZ);
            Block lvt_9_1_ = this.worldObj.getBlockState(new BlockPos(lvt_6_1_, lvt_7_1_, lvt_8_1_)).getBlock();

            if (lvt_9_1_.getMaterial() != Material.air)
            {
                Block.SoundType lvt_10_1_ = lvt_9_1_.stepSound;
                this.playSound(lvt_10_1_.getStepSound(), lvt_10_1_.getVolume() * 0.5F, lvt_10_1_.getFrequency() * 0.75F);
            }
        }
    }

    protected String getFallSoundString(int damageValue)
    {
        return damageValue > 4 ? "game.neutral.hurt.fall.big" : "game.neutral.hurt.fall.small";
    }

    /**
     * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
     */
    public void performHurtAnimation()
    {
        this.hurtTime = this.maxHurtTime = 10;
        this.attackedAtYaw = 0.0F;
    }

    /**
     * Returns the current armor value as determined by a call to InventoryPlayer.getTotalArmorValue
     */
    public int getTotalArmorValue()
    {
        int lvt_1_1_ = 0;

        for (ItemStack lvt_5_1_ : this.getInventory())
        {
            if (lvt_5_1_ != null && lvt_5_1_.getItem() instanceof ItemArmor)
            {
                int lvt_6_1_ = ((ItemArmor)lvt_5_1_.getItem()).damageReduceAmount;
                lvt_1_1_ += lvt_6_1_;
            }
        }

        return lvt_1_1_;
    }

    protected void damageArmor(float p_70675_1_)
    {
    }

    /**
     * Reduces damage, depending on armor
     */
    protected float applyArmorCalculations(DamageSource source, float damage)
    {
        if (!source.isUnblockable())
        {
            int lvt_3_1_ = 25 - this.getTotalArmorValue();
            float lvt_4_1_ = damage * (float)lvt_3_1_;
            this.damageArmor(damage);
            damage = lvt_4_1_ / 25.0F;
        }

        return damage;
    }

    /**
     * Reduces damage, depending on potions
     */
    protected float applyPotionDamageCalculations(DamageSource source, float damage)
    {
        if (source.isDamageAbsolute())
        {
            return damage;
        }
        else
        {
            if (this.isPotionActive(Potion.resistance) && source != DamageSource.outOfWorld)
            {
                int lvt_3_1_ = (this.getActivePotionEffect(Potion.resistance).getAmplifier() + 1) * 5;
                int lvt_4_1_ = 25 - lvt_3_1_;
                float lvt_5_1_ = damage * (float)lvt_4_1_;
                damage = lvt_5_1_ / 25.0F;
            }

            if (damage <= 0.0F)
            {
                return 0.0F;
            }
            else
            {
                int lvt_3_2_ = EnchantmentHelper.getEnchantmentModifierDamage(this.getInventory(), source);

                if (lvt_3_2_ > 20)
                {
                    lvt_3_2_ = 20;
                }

                if (lvt_3_2_ > 0 && lvt_3_2_ <= 20)
                {
                    int lvt_4_2_ = 25 - lvt_3_2_;
                    float lvt_5_2_ = damage * (float)lvt_4_2_;
                    damage = lvt_5_2_ / 25.0F;
                }

                return damage;
            }
        }
    }

    /**
     * Deals damage to the entity. If its a EntityPlayer then will take damage from the armor first and then health
     * second with the reduced value. Args: damageAmount
     */
    protected void damageEntity(DamageSource damageSrc, float damageAmount)
    {
        if (!this.isEntityInvulnerable(damageSrc))
        {
            damageAmount = this.applyArmorCalculations(damageSrc, damageAmount);
            damageAmount = this.applyPotionDamageCalculations(damageSrc, damageAmount);
            float lvt_3_1_ = damageAmount;
            damageAmount = Math.max(damageAmount - this.getAbsorptionAmount(), 0.0F);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - (lvt_3_1_ - damageAmount));

            if (damageAmount != 0.0F)
            {
                float lvt_4_1_ = this.getHealth();
                this.setHealth(lvt_4_1_ - damageAmount);
                this.getCombatTracker().trackDamage(damageSrc, lvt_4_1_, damageAmount);
                this.setAbsorptionAmount(this.getAbsorptionAmount() - damageAmount);
            }
        }
    }

    /**
     * 1.8.9
     */
    public CombatTracker getCombatTracker()
    {
        return this._combatTracker;
    }

    public EntityLivingBase getAttackingEntity()
    {
        return (EntityLivingBase)(this._combatTracker.func_94550_c() != null ? this._combatTracker.func_94550_c() : (this.attackingPlayer != null ? this.attackingPlayer : (this.entityLivingToAttack != null ? this.entityLivingToAttack : null)));
    }

    public final float getMaxHealth()
    {
        return (float)this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue();
    }

    /**
     * counts the amount of arrows stuck in the entity. getting hit by arrows increases this, used in rendering
     */
    public final int getArrowCountInEntity()
    {
        return this.dataWatcher.getWatchableObjectByte(9);
    }

    /**
     * sets the amount of arrows stuck in the entity. used for rendering those
     */
    public final void setArrowCountInEntity(int count)
    {
        this.dataWatcher.updateObject(9, Byte.valueOf((byte)count));
    }

    /**
     * Returns an integer indicating the end point of the swing animation, used by {@link #swingProgress} to provide a
     * progress indicator. Takes dig speed enchantments into account.
     */
    private int getArmSwingAnimationEnd()
    {
        return this.isPotionActive(Potion.digSpeed) ? 6 - (1 + this.getActivePotionEffect(Potion.digSpeed).getAmplifier()) * 1 : (this.isPotionActive(Potion.digSlowdown) ? 6 + (1 + this.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2 : 6);
    }

    /**
     * Swings the item the player is holding.
     */
    public void swingItem()
    {
        if (!this.isSwingInProgress || this.swingProgressInt >= this.getArmSwingAnimationEnd() / 2 || this.swingProgressInt < 0)
        {
            this.swingProgressInt = -1;
            this.isSwingInProgress = true;

            if (this.worldObj instanceof WorldServer)
            {
                ((WorldServer)this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S0BPacketAnimation(this, 0));
            }
        }
    }

    public void handleStatusUpdate(byte id)
    {
        if (id == 2)
        {
            this.limbSwingAmount = 1.5F;
            this.hurtResistantTime = this.maxHurtResistantTime;
            this.hurtTime = this.maxHurtTime = 10;
            this.attackedAtYaw = 0.0F;
            String lvt_2_1_ = this.getHurtSound();

            if (lvt_2_1_ != null)
            {
                this.playSound(this.getHurtSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            }

            this.attackEntityFrom(DamageSource.generic, 0.0F);
        }
        else if (id == 3)
        {
            String lvt_2_2_ = this.getDeathSound();

            if (lvt_2_2_ != null)
            {
                this.playSound(this.getDeathSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            }

            this.setHealth(0.0F);
            this.onDeath(DamageSource.generic);
        }
        else
        {
            super.handleStatusUpdate(id);
        }
    }

    /**
     * sets the dead flag. Used when you fall off the bottom of the world.
     */
    protected void kill()
    {
        this.attackEntityFrom(DamageSource.outOfWorld, 4.0F);
    }

    /**
     * Updates the arm swing progress counters and animation progress
     */
    protected void updateArmSwingProgress()
    {
        int lvt_1_1_ = this.getArmSwingAnimationEnd();

        if (this.isSwingInProgress)
        {
            ++this.swingProgressInt;

            if (this.swingProgressInt >= lvt_1_1_)
            {
                this.swingProgressInt = 0;
                this.isSwingInProgress = false;
            }
        }
        else
        {
            this.swingProgressInt = 0;
        }

        this.swingProgress = (float)this.swingProgressInt / (float)lvt_1_1_;
    }

    public IAttributeInstance getEntityAttribute(IAttribute attribute)
    {
        return this.getAttributeMap().getAttributeInstance(attribute);
    }

    public BaseAttributeMap getAttributeMap()
    {
        if (this.attributeMap == null)
        {
            this.attributeMap = new ServersideAttributeMap();
        }

        return this.attributeMap;
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.UNDEFINED;
    }

    /**
     * Returns the item that this EntityLiving is holding, if any.
     */
    public abstract ItemStack getHeldItem();

    /**
     * 0: Tool in Hand; 1-4: Armor
     */
    public abstract ItemStack getEquipmentInSlot(int slotIn);

    public abstract ItemStack getCurrentArmor(int slotIn);

    /**
     * Sets the held item, or an armor slot. Slot 0 is held item. Slot 1-4 is armor. Params: Item, slot
     */
    public abstract void setCurrentItemOrArmor(int slotIn, ItemStack stack);

    /**
     * Set sprinting switch for Entity.
     */
    public void setSprinting(boolean sprinting)
    {
        super.setSprinting(sprinting);
        IAttributeInstance lvt_2_1_ = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);

        if (lvt_2_1_.getModifier(sprintingSpeedBoostModifierUUID) != null)
        {
            lvt_2_1_.removeModifier(sprintingSpeedBoostModifier);
        }

        if (sprinting)
        {
            lvt_2_1_.applyModifier(sprintingSpeedBoostModifier);
        }
    }

    /**
     * returns the inventory of this entity (only used in EntityPlayerMP it seems)
     */
    public abstract ItemStack[] getInventory();

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume()
    {
        return 1.0F;
    }

    /**
     * Gets the pitch of living sounds in living entities.
     */
    protected float getSoundPitch()
    {
        return this.isChild() ? (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.5F : (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F;
    }

    /**
     * Dead and sleeping entities cannot move
     */
    protected boolean isMovementBlocked()
    {
        return this.getHealth() <= 0.0F;
    }

    /**
     * Moves the entity to a position out of the way of its mount.
     */
    public void dismountEntity(Entity entityIn)
    {
        double lvt_3_1_ = entityIn.posX;
        double lvt_5_1_ = entityIn.getEntityBoundingBox().minY + (double)entityIn.height;
        double lvt_7_1_ = entityIn.posZ;
        int lvt_9_1_ = 1;

        for (int lvt_10_1_ = -lvt_9_1_; lvt_10_1_ <= lvt_9_1_; ++lvt_10_1_)
        {
            for (int lvt_11_1_ = -lvt_9_1_; lvt_11_1_ < lvt_9_1_; ++lvt_11_1_)
            {
                if (lvt_10_1_ != 0 || lvt_11_1_ != 0)
                {
                    int lvt_12_1_ = (int)(this.posX + (double)lvt_10_1_);
                    int lvt_13_1_ = (int)(this.posZ + (double)lvt_11_1_);
                    AxisAlignedBB lvt_2_1_ = this.getEntityBoundingBox().offset((double)lvt_10_1_, 1.0D, (double)lvt_11_1_);

                    if (this.worldObj.getCollisionBoxes(lvt_2_1_).isEmpty())
                    {
                        if (World.doesBlockHaveSolidTopSurface(this.worldObj, new BlockPos(lvt_12_1_, (int)this.posY, lvt_13_1_)))
                        {
                            this.setPositionAndUpdate(this.posX + (double)lvt_10_1_, this.posY + 1.0D, this.posZ + (double)lvt_11_1_);
                            return;
                        }

                        if (World.doesBlockHaveSolidTopSurface(this.worldObj, new BlockPos(lvt_12_1_, (int)this.posY - 1, lvt_13_1_)) || this.worldObj.getBlockState(new BlockPos(lvt_12_1_, (int)this.posY - 1, lvt_13_1_)).getBlock().getMaterial() == Material.water)
                        {
                            lvt_3_1_ = this.posX + (double)lvt_10_1_;
                            lvt_5_1_ = this.posY + 1.0D;
                            lvt_7_1_ = this.posZ + (double)lvt_11_1_;
                        }
                    }
                }
            }
        }

        this.setPositionAndUpdate(lvt_3_1_, lvt_5_1_, lvt_7_1_);
    }

    public boolean getAlwaysRenderNameTagForRender()
    {
        return false;
    }

    protected float getJumpUpwardsMotion()
    {
        return 0.42F;
    }

    /**
     * Causes this entity to do an upwards motion (jumping).
     */
    protected void jump()
    {
        this.motionY = (double)this.getJumpUpwardsMotion();

        if (this.isPotionActive(Potion.jump))
        {
            this.motionY += (double)((float)(this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
        }

        if (this.isSprinting())
        {
            float lvt_1_1_ = this.rotationYaw * 0.017453292F;
            this.motionX -= (double)(MathHelper.sin(lvt_1_1_) * 0.2F);
            this.motionZ += (double)(MathHelper.cos(lvt_1_1_) * 0.2F);
        }

        this.isAirBorne = true;
    }

    /**
     * main AI tick function, replaces updateEntityActionState
     */
    protected void updateAITick()
    {
        this.motionY += 0.03999999910593033D;
    }

    protected void handleJumpLava()
    {
        this.motionY += 0.03999999910593033D;
    }

    /**
     * Moves the entity based on the specified heading.  Args: strafe, forward
     */
    public void moveEntityWithHeading(float strafe, float forward)
    {
        if (this.isServerWorld())
        {
            if (!this.isInWater() || this instanceof EntityPlayer && ((EntityPlayer)this).capabilities.isFlying)
            {
                if (!this.isInLava() || this instanceof EntityPlayer && ((EntityPlayer)this).capabilities.isFlying)
                {
                    float lvt_3_3_ = 0.91F;

                    if (this.onGround)
                    {
                        lvt_3_3_ = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ))).getBlock().slipperiness * 0.91F;
                    }

                    float lvt_4_1_ = 0.16277136F / (lvt_3_3_ * lvt_3_3_ * lvt_3_3_);
                    float lvt_5_2_;

                    if (this.onGround)
                    {
                        lvt_5_2_ = this.getAIMoveSpeed() * lvt_4_1_;
                    }
                    else
                    {
                        lvt_5_2_ = this.jumpMovementFactor;
                    }

                    this.moveFlying(strafe, forward, lvt_5_2_);
                    lvt_3_3_ = 0.91F;

                    if (this.onGround)
                    {
                        lvt_3_3_ = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ))).getBlock().slipperiness * 0.91F;
                    }

                    if (this.isOnLadder())
                    {
                        float lvt_6_2_ = 0.15F;
                        this.motionX = MathHelper.clamp_double(this.motionX, (double)(-lvt_6_2_), (double)lvt_6_2_);
                        this.motionZ = MathHelper.clamp_double(this.motionZ, (double)(-lvt_6_2_), (double)lvt_6_2_);
                        this.fallDistance = 0.0F;

                        if (this.motionY < -0.15D)
                        {
                            this.motionY = -0.15D;
                        }

                        boolean lvt_7_2_ = this.isSneaking() && this instanceof EntityPlayer;

                        if (lvt_7_2_ && this.motionY < 0.0D)
                        {
                            this.motionY = 0.0D;
                        }
                    }

                    this.moveEntity(this.motionX, this.motionY, this.motionZ);

                    if (this.isCollidedHorizontally && this.isOnLadder())
                    {
                        this.motionY = 0.2D;
                    }

                    if (this.worldObj.isRemote && (!this.worldObj.isBlockLoaded(new BlockPos((int)this.posX, 0, (int)this.posZ)) || !this.worldObj.getChunkFromBlockCoords(new BlockPos((int)this.posX, 0, (int)this.posZ)).isLoaded()))
                    {
                        if (this.posY > 0.0D)
                        {
                            this.motionY = -0.1D;
                        }
                        else
                        {
                            this.motionY = 0.0D;
                        }
                    }
                    else
                    {
                        this.motionY -= 0.08D;
                    }

                    this.motionY *= 0.9800000190734863D;
                    this.motionX *= (double)lvt_3_3_;
                    this.motionZ *= (double)lvt_3_3_;
                }
                else
                {
                    double lvt_3_2_ = this.posY;
                    this.moveFlying(strafe, forward, 0.02F);
                    this.moveEntity(this.motionX, this.motionY, this.motionZ);
                    this.motionX *= 0.5D;
                    this.motionY *= 0.5D;
                    this.motionZ *= 0.5D;
                    this.motionY -= 0.02D;

                    if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6000000238418579D - this.posY + lvt_3_2_, this.motionZ))
                    {
                        this.motionY = 0.30000001192092896D;
                    }
                }
            }
            else
            {
                double lvt_3_1_ = this.posY;
                float lvt_5_1_ = 0.8F;
                float lvt_6_1_ = 0.02F;
                float lvt_7_1_ = (float)EnchantmentHelper.getDepthStriderModifier(this);

                if (lvt_7_1_ > 3.0F)
                {
                    lvt_7_1_ = 3.0F;
                }

                if (!this.onGround)
                {
                    lvt_7_1_ *= 0.5F;
                }

                if (lvt_7_1_ > 0.0F)
                {
                    lvt_5_1_ += (0.54600006F - lvt_5_1_) * lvt_7_1_ / 3.0F;
                    lvt_6_1_ += (this.getAIMoveSpeed() * 1.0F - lvt_6_1_) * lvt_7_1_ / 3.0F;
                }

                this.moveFlying(strafe, forward, lvt_6_1_);
                this.moveEntity(this.motionX, this.motionY, this.motionZ);
                this.motionX *= (double)lvt_5_1_;
                this.motionY *= 0.800000011920929D;
                this.motionZ *= (double)lvt_5_1_;
                this.motionY -= 0.02D;

                if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6000000238418579D - this.posY + lvt_3_1_, this.motionZ))
                {
                    this.motionY = 0.30000001192092896D;
                }
            }
        }

        this.prevLimbSwingAmount = this.limbSwingAmount;
        double lvt_3_4_ = this.posX - this.prevPosX;
        double lvt_5_4_ = this.posZ - this.prevPosZ;
        float lvt_7_3_ = MathHelper.sqrt_double(lvt_3_4_ * lvt_3_4_ + lvt_5_4_ * lvt_5_4_) * 4.0F;

        if (lvt_7_3_ > 1.0F)
        {
            lvt_7_3_ = 1.0F;
        }

        this.limbSwingAmount += (lvt_7_3_ - this.limbSwingAmount) * 0.4F;
        this.limbSwing += this.limbSwingAmount;
    }

    /**
     * the movespeed used for the new AI system
     */
    public float getAIMoveSpeed()
    {
        return this.landMovementFactor;
    }

    /**
     * set the movespeed used for the new AI system
     */
    public void setAIMoveSpeed(float speedIn)
    {
        this.landMovementFactor = speedIn;
    }

    public boolean attackEntityAsMob(Entity entityIn)
    {
        this.setLastAttacker(entityIn);
        return false;
    }

    /**
     * Returns whether player is sleeping or not
     */
    public boolean isPlayerSleeping()
    {
        return false;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (!this.worldObj.isRemote)
        {
            int lvt_1_1_ = this.getArrowCountInEntity();

            if (lvt_1_1_ > 0)
            {
                if (this.arrowHitTimer <= 0)
                {
                    this.arrowHitTimer = 20 * (30 - lvt_1_1_);
                }

                --this.arrowHitTimer;

                if (this.arrowHitTimer <= 0)
                {
                    this.setArrowCountInEntity(lvt_1_1_ - 1);
                }
            }

            for (int lvt_2_1_ = 0; lvt_2_1_ < 5; ++lvt_2_1_)
            {
                ItemStack lvt_3_1_ = this.previousEquipment[lvt_2_1_];
                ItemStack lvt_4_1_ = this.getEquipmentInSlot(lvt_2_1_);

                if (!ItemStack.areItemStacksEqual(lvt_4_1_, lvt_3_1_))
                {
                    ((WorldServer)this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S04PacketEntityEquipment(this.getEntityId(), lvt_2_1_, lvt_4_1_));

                    if (lvt_3_1_ != null)
                    {
                        this.attributeMap.removeAttributeModifiers(lvt_3_1_.getAttributeModifiers());
                    }

                    if (lvt_4_1_ != null)
                    {
                        this.attributeMap.applyAttributeModifiers(lvt_4_1_.getAttributeModifiers());
                    }

                    this.previousEquipment[lvt_2_1_] = lvt_4_1_ == null ? null : lvt_4_1_.copy();
                }
            }

            if (this.ticksExisted % 20 == 0)
            {
                this.getCombatTracker().reset();
            }
        }

        this.onLivingUpdate();
        double lvt_1_2_ = this.posX - this.prevPosX;
        double lvt_3_2_ = this.posZ - this.prevPosZ;
        float lvt_5_1_ = (float)(lvt_1_2_ * lvt_1_2_ + lvt_3_2_ * lvt_3_2_);
        float lvt_6_1_ = this.renderYawOffset;
        float lvt_7_1_ = 0.0F;
        this.prevOnGroundSpeedFactor = this.onGroundSpeedFactor;
        float lvt_8_1_ = 0.0F;

        if (lvt_5_1_ > 0.0025000002F)
        {
            lvt_8_1_ = 1.0F;
            lvt_7_1_ = (float)Math.sqrt((double)lvt_5_1_) * 3.0F;
            lvt_6_1_ = (float)MathHelper.atan2(lvt_3_2_, lvt_1_2_) * 180.0F / (float)Math.PI - 90.0F;
        }

        if (this.swingProgress > 0.0F)
        {
            lvt_6_1_ = this.rotationYaw;
        }

        if (!this.onGround)
        {
            lvt_8_1_ = 0.0F;
        }

        this.onGroundSpeedFactor += (lvt_8_1_ - this.onGroundSpeedFactor) * 0.3F;
        this.worldObj.theProfiler.startSection("headTurn");
        lvt_7_1_ = this.updateDistance(lvt_6_1_, lvt_7_1_);
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("rangeChecks");

        while (this.rotationYaw - this.prevRotationYaw < -180.0F)
        {
            this.prevRotationYaw -= 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
        {
            this.prevRotationYaw += 360.0F;
        }

        while (this.renderYawOffset - this.prevRenderYawOffset < -180.0F)
        {
            this.prevRenderYawOffset -= 360.0F;
        }

        while (this.renderYawOffset - this.prevRenderYawOffset >= 180.0F)
        {
            this.prevRenderYawOffset += 360.0F;
        }

        while (this.rotationPitch - this.prevRotationPitch < -180.0F)
        {
            this.prevRotationPitch -= 360.0F;
        }

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
        {
            this.prevRotationPitch += 360.0F;
        }

        while (this.rotationYawHead - this.prevRotationYawHead < -180.0F)
        {
            this.prevRotationYawHead -= 360.0F;
        }

        while (this.rotationYawHead - this.prevRotationYawHead >= 180.0F)
        {
            this.prevRotationYawHead += 360.0F;
        }

        this.worldObj.theProfiler.endSection();
        this.movedDistance += lvt_7_1_;
    }

    protected float updateDistance(float p_110146_1_, float p_110146_2_)
    {
        float lvt_3_1_ = MathHelper.wrapAngleTo180_float(p_110146_1_ - this.renderYawOffset);
        this.renderYawOffset += lvt_3_1_ * 0.3F;
        float lvt_4_1_ = MathHelper.wrapAngleTo180_float(this.rotationYaw - this.renderYawOffset);
        boolean lvt_5_1_ = lvt_4_1_ < -90.0F || lvt_4_1_ >= 90.0F;

        if (lvt_4_1_ < -75.0F)
        {
            lvt_4_1_ = -75.0F;
        }

        if (lvt_4_1_ >= 75.0F)
        {
            lvt_4_1_ = 75.0F;
        }

        this.renderYawOffset = this.rotationYaw - lvt_4_1_;

        if (lvt_4_1_ * lvt_4_1_ > 2500.0F)
        {
            this.renderYawOffset += lvt_4_1_ * 0.2F;
        }

        if (lvt_5_1_)
        {
            p_110146_2_ *= -1.0F;
        }

        return p_110146_2_;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        if (this.jumpTicks > 0)
        {
            --this.jumpTicks;
        }

        if (this.newPosRotationIncrements > 0)
        {
            double lvt_1_1_ = this.posX + (this.newPosX - this.posX) / (double)this.newPosRotationIncrements;
            double lvt_3_1_ = this.posY + (this.newPosY - this.posY) / (double)this.newPosRotationIncrements;
            double lvt_5_1_ = this.posZ + (this.newPosZ - this.posZ) / (double)this.newPosRotationIncrements;
            double lvt_7_1_ = MathHelper.wrapAngleTo180_double(this.newRotationYaw - (double)this.rotationYaw);
            this.rotationYaw = (float)((double)this.rotationYaw + lvt_7_1_ / (double)this.newPosRotationIncrements);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.newRotationPitch - (double)this.rotationPitch) / (double)this.newPosRotationIncrements);
            --this.newPosRotationIncrements;
            this.setPosition(lvt_1_1_, lvt_3_1_, lvt_5_1_);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
        else if (!this.isServerWorld())
        {
            this.motionX *= 0.98D;
            this.motionY *= 0.98D;
            this.motionZ *= 0.98D;
        }

        if (Math.abs(this.motionX) < 0.005D)
        {
            this.motionX = 0.0D;
        }

        if (Math.abs(this.motionY) < 0.005D)
        {
            this.motionY = 0.0D;
        }

        if (Math.abs(this.motionZ) < 0.005D)
        {
            this.motionZ = 0.0D;
        }

        this.worldObj.theProfiler.startSection("ai");

        if (this.isMovementBlocked())
        {
            this.isJumping = false;
            this.moveStrafing = 0.0F;
            this.moveForward = 0.0F;
            this.randomYawVelocity = 0.0F;
        }
        else if (this.isServerWorld())
        {
            this.worldObj.theProfiler.startSection("newAi");
            this.updateEntityActionState();
            this.worldObj.theProfiler.endSection();
        }

        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("jump");

        if (this.isJumping)
        {
            if (this.isInWater())
            {
                this.updateAITick();
            }
            else if (this.isInLava())
            {
                this.handleJumpLava();
            }
            else if (this.onGround && this.jumpTicks == 0)
            {
                this.jump();
                this.jumpTicks = 10;
            }
        }
        else
        {
            this.jumpTicks = 0;
        }

        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("travel");
        this.moveStrafing *= 0.98F;
        this.moveForward *= 0.98F;
        this.randomYawVelocity *= 0.9F;
        this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("push");

        if (!this.worldObj.isRemote)
        {
            this.collideWithNearbyEntities();
        }

        this.worldObj.theProfiler.endSection();
    }

    protected void updateEntityActionState()
    {
    }

    protected void collideWithNearbyEntities()
    {
        List<Entity> lvt_1_1_ = this.worldObj.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().expand(0.20000000298023224D, 0.0D, 0.20000000298023224D), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>()
        {
            public boolean apply(Entity p_apply_1_)
            {
                return p_apply_1_.canBePushed();
            }
            public boolean apply(Object p_apply_1_)
            {
                return this.apply((Entity)p_apply_1_);
            }
        }));

        if (!lvt_1_1_.isEmpty())
        {
            for (int lvt_2_1_ = 0; lvt_2_1_ < lvt_1_1_.size(); ++lvt_2_1_)
            {
                Entity lvt_3_1_ = (Entity)lvt_1_1_.get(lvt_2_1_);
                this.collideWithEntity(lvt_3_1_);
            }
        }
    }

    protected void collideWithEntity(Entity entityIn)
    {
        entityIn.applyEntityCollision(this);
    }

    /**
     * Called when a player mounts an entity. e.g. mounts a pig, mounts a boat.
     */
    public void mountEntity(Entity entityIn)
    {
        if (this.ridingEntity != null && entityIn == null)
        {
            if (!this.worldObj.isRemote)
            {
                this.dismountEntity(this.ridingEntity);
            }

            if (this.ridingEntity != null)
            {
                this.ridingEntity.riddenByEntity = null;
            }

            this.ridingEntity = null;
        }
        else
        {
            super.mountEntity(entityIn);
        }
    }

    /**
     * Handles updating while being ridden by an entity
     */
    public void updateRidden()
    {
        super.updateRidden();
        this.prevOnGroundSpeedFactor = this.onGroundSpeedFactor;
        this.onGroundSpeedFactor = 0.0F;
        this.fallDistance = 0.0F;
    }

    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean p_180426_10_)
    {
        this.newPosX = x;
        this.newPosY = y;
        this.newPosZ = z;
        this.newRotationYaw = (double)yaw;
        this.newRotationPitch = (double)pitch;
        this.newPosRotationIncrements = posRotationIncrements;
    }

    public void setJumping(boolean jumping)
    {
        this.isJumping = jumping;
    }

    /**
     * Called whenever an item is picked up from walking over it. Args: pickedUpEntity, stackSize
     */
    public void onItemPickup(Entity p_71001_1_, int p_71001_2_)
    {
        if (!p_71001_1_.isDead && !this.worldObj.isRemote)
        {
            EntityTracker lvt_3_1_ = ((WorldServer)this.worldObj).getEntityTracker();

            if (p_71001_1_ instanceof EntityItem)
            {
                lvt_3_1_.sendToAllTrackingEntity(p_71001_1_, new S0DPacketCollectItem(p_71001_1_.getEntityId(), this.getEntityId()));
            }

            if (p_71001_1_ instanceof EntityArrow)
            {
                lvt_3_1_.sendToAllTrackingEntity(p_71001_1_, new S0DPacketCollectItem(p_71001_1_.getEntityId(), this.getEntityId()));
            }

            if (p_71001_1_ instanceof EntityXPOrb)
            {
                lvt_3_1_.sendToAllTrackingEntity(p_71001_1_, new S0DPacketCollectItem(p_71001_1_.getEntityId(), this.getEntityId()));
            }
        }
    }

    /**
     * returns true if the entity provided in the argument can be seen. (Raytrace)
     */
    public boolean canEntityBeSeen(Entity entityIn)
    {
        return this.worldObj.rayTraceBlocks(new Vec3(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ), new Vec3(entityIn.posX, entityIn.posY + (double)entityIn.getEyeHeight(), entityIn.posZ)) == null;
    }

    /**
     * returns a (normalized) vector of where this entity is looking
     */
    public Vec3 getLookVec()
    {
        return this.getLook(1.0F);
    }

    /**
     * interpolated look vector
     */
    public Vec3 getLook(float partialTicks)
    {
        if (partialTicks == 1.0F)
        {
            return this.getVectorForRotation(this.rotationPitch, this.rotationYawHead);
        }
        else
        {
            float lvt_2_1_ = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * partialTicks;
            float lvt_3_1_ = this.prevRotationYawHead + (this.rotationYawHead - this.prevRotationYawHead) * partialTicks;
            return this.getVectorForRotation(lvt_2_1_, lvt_3_1_);
        }
    }

    /**
     * Returns where in the swing animation the living entity is (from 0 to 1).  Args: partialTickTime
     */
    public float getSwingProgress(float partialTickTime)
    {
        float lvt_2_1_ = this.swingProgress - this.prevSwingProgress;

        if (lvt_2_1_ < 0.0F)
        {
            ++lvt_2_1_;
        }

        return this.prevSwingProgress + lvt_2_1_ * partialTickTime;
    }

    /**
     * Returns whether the entity is in a server world
     */
    public boolean isServerWorld()
    {
        return !this.worldObj.isRemote;
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    public boolean canBePushed()
    {
        return !this.isDead;
    }

    /**
     * Sets that this entity has been attacked.
     */
    protected void setBeenAttacked()
    {
        this.velocityChanged = this.rand.nextDouble() >= this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue();
    }

    public float getRotationYawHead()
    {
        return this.rotationYawHead;
    }

    /**
     * Sets the head's yaw rotation of the entity.
     */
    public void setRotationYawHead(float rotation)
    {
        this.rotationYawHead = rotation;
    }

    /**
     * Set the render yaw offset
     *  
     * @param offset The render yaw offset
     */
    public void setRenderYawOffset(float offset)
    {
        this.renderYawOffset = offset;
    }

    public float getAbsorptionAmount()
    {
        return this.absorptionAmount;
    }

    public void setAbsorptionAmount(float amount)
    {
        if (amount < 0.0F)
        {
            amount = 0.0F;
        }

        this.absorptionAmount = amount;
    }

    public Team getTeam()
    {
        return this.worldObj.getScoreboard().getPlayersTeam(this.getUniqueID().toString());
    }

    public boolean isOnSameTeam(EntityLivingBase otherEntity)
    {
        return this.isOnTeam(otherEntity.getTeam());
    }

    /**
     * Returns true if the entity is on a specific team.
     */
    public boolean isOnTeam(Team teamIn)
    {
        return this.getTeam() != null ? this.getTeam().isSameTeam(teamIn) : false;
    }

    /**
     * Sends an ENTER_COMBAT packet to the client
     */
    public void sendEnterCombat()
    {
    }

    /**
     * Sends an END_COMBAT packet to the client
     */
    public void sendEndCombat()
    {
    }

    protected void markPotionsDirty()
    {
        this.potionsNeedUpdate = true;
    }
}
