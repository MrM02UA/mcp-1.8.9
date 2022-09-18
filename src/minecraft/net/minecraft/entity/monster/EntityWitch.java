package net.minecraft.entity.monster;

import java.util.List;
import java.util.UUID;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityWitch extends EntityMob implements IRangedAttackMob
{
    private static final UUID MODIFIER_UUID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
    private static final AttributeModifier MODIFIER = (new AttributeModifier(MODIFIER_UUID, "Drinking speed penalty", -0.25D, 0)).setSaved(false);

    /** List of items a witch should drop on death. */
    private static final Item[] witchDrops = new Item[] {Items.glowstone_dust, Items.sugar, Items.redstone, Items.spider_eye, Items.glass_bottle, Items.gunpowder, Items.stick, Items.stick};

    /**
     * Timer used as interval for a witch's attack, decremented every tick if aggressive and when reaches zero the witch
     * will throw a potion at the target entity.
     */
    private int witchAttackTimer;

    public EntityWitch(World worldIn)
    {
        super(worldIn);
        this.setSize(0.6F, 1.95F);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIArrowAttack(this, 1.0D, 60, 10.0F));
        this.tasks.addTask(2, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(3, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
    }

    protected void entityInit()
    {
        super.entityInit();
        this.getDataWatcher().addObject(21, Byte.valueOf((byte)0));
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return null;
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return null;
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return null;
    }

    /**
     * Set whether this witch is aggressive at an entity.
     */
    public void setAggressive(boolean aggressive)
    {
        this.getDataWatcher().updateObject(21, Byte.valueOf((byte)(aggressive ? 1 : 0)));
    }

    /**
     * Return whether this witch is aggressive at an entity.
     */
    public boolean getAggressive()
    {
        return this.getDataWatcher().getWatchableObjectByte(21) == 1;
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(26.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        if (!this.worldObj.isRemote)
        {
            if (this.getAggressive())
            {
                if (this.witchAttackTimer-- <= 0)
                {
                    this.setAggressive(false);
                    ItemStack lvt_1_1_ = this.getHeldItem();
                    this.setCurrentItemOrArmor(0, (ItemStack)null);

                    if (lvt_1_1_ != null && lvt_1_1_.getItem() == Items.potionitem)
                    {
                        List<PotionEffect> lvt_2_1_ = Items.potionitem.getEffects(lvt_1_1_);

                        if (lvt_2_1_ != null)
                        {
                            for (PotionEffect lvt_4_1_ : lvt_2_1_)
                            {
                                this.addPotionEffect(new PotionEffect(lvt_4_1_));
                            }
                        }
                    }

                    this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).removeModifier(MODIFIER);
                }
            }
            else
            {
                int lvt_1_2_ = -1;

                if (this.rand.nextFloat() < 0.15F && this.isInsideOfMaterial(Material.water) && !this.isPotionActive(Potion.waterBreathing))
                {
                    lvt_1_2_ = 8237;
                }
                else if (this.rand.nextFloat() < 0.15F && this.isBurning() && !this.isPotionActive(Potion.fireResistance))
                {
                    lvt_1_2_ = 16307;
                }
                else if (this.rand.nextFloat() < 0.05F && this.getHealth() < this.getMaxHealth())
                {
                    lvt_1_2_ = 16341;
                }
                else if (this.rand.nextFloat() < 0.25F && this.getAttackTarget() != null && !this.isPotionActive(Potion.moveSpeed) && this.getAttackTarget().getDistanceSqToEntity(this) > 121.0D)
                {
                    lvt_1_2_ = 16274;
                }
                else if (this.rand.nextFloat() < 0.25F && this.getAttackTarget() != null && !this.isPotionActive(Potion.moveSpeed) && this.getAttackTarget().getDistanceSqToEntity(this) > 121.0D)
                {
                    lvt_1_2_ = 16274;
                }

                if (lvt_1_2_ > -1)
                {
                    this.setCurrentItemOrArmor(0, new ItemStack(Items.potionitem, 1, lvt_1_2_));
                    this.witchAttackTimer = this.getHeldItem().getMaxItemUseDuration();
                    this.setAggressive(true);
                    IAttributeInstance lvt_2_2_ = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
                    lvt_2_2_.removeModifier(MODIFIER);
                    lvt_2_2_.applyModifier(MODIFIER);
                }
            }

            if (this.rand.nextFloat() < 7.5E-4F)
            {
                this.worldObj.setEntityState(this, (byte)15);
            }
        }

        super.onLivingUpdate();
    }

    public void handleStatusUpdate(byte id)
    {
        if (id == 15)
        {
            for (int lvt_2_1_ = 0; lvt_2_1_ < this.rand.nextInt(35) + 10; ++lvt_2_1_)
            {
                this.worldObj.spawnParticle(EnumParticleTypes.SPELL_WITCH, this.posX + this.rand.nextGaussian() * 0.12999999523162842D, this.getEntityBoundingBox().maxY + 0.5D + this.rand.nextGaussian() * 0.12999999523162842D, this.posZ + this.rand.nextGaussian() * 0.12999999523162842D, 0.0D, 0.0D, 0.0D, new int[0]);
            }
        }
        else
        {
            super.handleStatusUpdate(id);
        }
    }

    /**
     * Reduces damage, depending on potions
     */
    protected float applyPotionDamageCalculations(DamageSource source, float damage)
    {
        damage = super.applyPotionDamageCalculations(source, damage);

        if (source.getEntity() == this)
        {
            damage = 0.0F;
        }

        if (source.isMagicDamage())
        {
            damage = (float)((double)damage * 0.15D);
        }

        return damage;
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
        int lvt_3_1_ = this.rand.nextInt(3) + 1;

        for (int lvt_4_1_ = 0; lvt_4_1_ < lvt_3_1_; ++lvt_4_1_)
        {
            int lvt_5_1_ = this.rand.nextInt(3);
            Item lvt_6_1_ = witchDrops[this.rand.nextInt(witchDrops.length)];

            if (lootingModifier > 0)
            {
                lvt_5_1_ += this.rand.nextInt(lootingModifier + 1);
            }

            for (int lvt_7_1_ = 0; lvt_7_1_ < lvt_5_1_; ++lvt_7_1_)
            {
                this.dropItem(lvt_6_1_, 1);
            }
        }
    }

    /**
     * Attack the specified entity using a ranged attack.
     */
    public void attackEntityWithRangedAttack(EntityLivingBase target, float p_82196_2_)
    {
        if (!this.getAggressive())
        {
            EntityPotion lvt_3_1_ = new EntityPotion(this.worldObj, this, 32732);
            double lvt_4_1_ = target.posY + (double)target.getEyeHeight() - 1.100000023841858D;
            lvt_3_1_.rotationPitch -= -20.0F;
            double lvt_6_1_ = target.posX + target.motionX - this.posX;
            double lvt_8_1_ = lvt_4_1_ - this.posY;
            double lvt_10_1_ = target.posZ + target.motionZ - this.posZ;
            float lvt_12_1_ = MathHelper.sqrt_double(lvt_6_1_ * lvt_6_1_ + lvt_10_1_ * lvt_10_1_);

            if (lvt_12_1_ >= 8.0F && !target.isPotionActive(Potion.moveSlowdown))
            {
                lvt_3_1_.setPotionDamage(32698);
            }
            else if (target.getHealth() >= 8.0F && !target.isPotionActive(Potion.poison))
            {
                lvt_3_1_.setPotionDamage(32660);
            }
            else if (lvt_12_1_ <= 3.0F && !target.isPotionActive(Potion.weakness) && this.rand.nextFloat() < 0.25F)
            {
                lvt_3_1_.setPotionDamage(32696);
            }

            lvt_3_1_.setThrowableHeading(lvt_6_1_, lvt_8_1_ + (double)(lvt_12_1_ * 0.2F), lvt_10_1_, 0.75F, 8.0F);
            this.worldObj.spawnEntityInWorld(lvt_3_1_);
        }
    }

    public float getEyeHeight()
    {
        return 1.62F;
    }
}
