package net.minecraft.entity.monster;

import java.util.Calendar;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderHell;

public class EntitySkeleton extends EntityMob implements IRangedAttackMob
{
    private EntityAIArrowAttack aiArrowAttack = new EntityAIArrowAttack(this, 1.0D, 20, 60, 15.0F);
    private EntityAIAttackOnCollide aiAttackOnCollide = new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.2D, false);

    public EntitySkeleton(World worldIn)
    {
        super(worldIn);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIRestrictSun(this));
        this.tasks.addTask(3, new EntityAIFleeSun(this, 1.0D));
        this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityWolf.class, 6.0F, 1.0D, 1.2D));
        this.tasks.addTask(4, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(6, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityIronGolem.class, true));

        if (worldIn != null && !worldIn.isRemote)
        {
            this.setCombatTask();
        }
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(13, new Byte((byte)0));
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return "mob.skeleton.say";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "mob.skeleton.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "mob.skeleton.death";
    }

    protected void playStepSound(BlockPos pos, Block blockIn)
    {
        this.playSound("mob.skeleton.step", 0.15F, 1.0F);
    }

    public boolean attackEntityAsMob(Entity entityIn)
    {
        if (super.attackEntityAsMob(entityIn))
        {
            if (this.getSkeletonType() == 1 && entityIn instanceof EntityLivingBase)
            {
                ((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(Potion.wither.id, 200));
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.UNDEAD;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        if (this.worldObj.isDaytime() && !this.worldObj.isRemote)
        {
            float lvt_1_1_ = this.getBrightness(1.0F);
            BlockPos lvt_2_1_ = new BlockPos(this.posX, (double)Math.round(this.posY), this.posZ);

            if (lvt_1_1_ > 0.5F && this.rand.nextFloat() * 30.0F < (lvt_1_1_ - 0.4F) * 2.0F && this.worldObj.canSeeSky(lvt_2_1_))
            {
                boolean lvt_3_1_ = true;
                ItemStack lvt_4_1_ = this.getEquipmentInSlot(4);

                if (lvt_4_1_ != null)
                {
                    if (lvt_4_1_.isItemStackDamageable())
                    {
                        lvt_4_1_.setItemDamage(lvt_4_1_.getItemDamage() + this.rand.nextInt(2));

                        if (lvt_4_1_.getItemDamage() >= lvt_4_1_.getMaxDamage())
                        {
                            this.renderBrokenItemStack(lvt_4_1_);
                            this.setCurrentItemOrArmor(4, (ItemStack)null);
                        }
                    }

                    lvt_3_1_ = false;
                }

                if (lvt_3_1_)
                {
                    this.setFire(8);
                }
            }
        }

        if (this.worldObj.isRemote && this.getSkeletonType() == 1)
        {
            this.setSize(0.72F, 2.535F);
        }

        super.onLivingUpdate();
    }

    /**
     * Handles updating while being ridden by an entity
     */
    public void updateRidden()
    {
        super.updateRidden();

        if (this.ridingEntity instanceof EntityCreature)
        {
            EntityCreature lvt_1_1_ = (EntityCreature)this.ridingEntity;
            this.renderYawOffset = lvt_1_1_.renderYawOffset;
        }
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void onDeath(DamageSource cause)
    {
        super.onDeath(cause);

        if (cause.getSourceOfDamage() instanceof EntityArrow && cause.getEntity() instanceof EntityPlayer)
        {
            EntityPlayer lvt_2_1_ = (EntityPlayer)cause.getEntity();
            double lvt_3_1_ = lvt_2_1_.posX - this.posX;
            double lvt_5_1_ = lvt_2_1_.posZ - this.posZ;

            if (lvt_3_1_ * lvt_3_1_ + lvt_5_1_ * lvt_5_1_ >= 2500.0D)
            {
                lvt_2_1_.triggerAchievement(AchievementList.snipeSkeleton);
            }
        }
        else if (cause.getEntity() instanceof EntityCreeper && ((EntityCreeper)cause.getEntity()).getPowered() && ((EntityCreeper)cause.getEntity()).isAIEnabled())
        {
            ((EntityCreeper)cause.getEntity()).func_175493_co();
            this.entityDropItem(new ItemStack(Items.skull, 1, this.getSkeletonType() == 1 ? 1 : 0), 0.0F);
        }
    }

    protected Item getDropItem()
    {
        return Items.arrow;
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
        if (this.getSkeletonType() == 1)
        {
            int lvt_3_1_ = this.rand.nextInt(3 + lootingModifier) - 1;

            for (int lvt_4_1_ = 0; lvt_4_1_ < lvt_3_1_; ++lvt_4_1_)
            {
                this.dropItem(Items.coal, 1);
            }
        }
        else
        {
            int lvt_3_2_ = this.rand.nextInt(3 + lootingModifier);

            for (int lvt_4_2_ = 0; lvt_4_2_ < lvt_3_2_; ++lvt_4_2_)
            {
                this.dropItem(Items.arrow, 1);
            }
        }

        int lvt_3_3_ = this.rand.nextInt(3 + lootingModifier);

        for (int lvt_4_3_ = 0; lvt_4_3_ < lvt_3_3_; ++lvt_4_3_)
        {
            this.dropItem(Items.bone, 1);
        }
    }

    /**
     * Causes this Entity to drop a random item.
     */
    protected void addRandomDrop()
    {
        if (this.getSkeletonType() == 1)
        {
            this.entityDropItem(new ItemStack(Items.skull, 1, 1), 0.0F);
        }
    }

    /**
     * Gives armor or weapon for entity based on given DifficultyInstance
     */
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty)
    {
        super.setEquipmentBasedOnDifficulty(difficulty);
        this.setCurrentItemOrArmor(0, new ItemStack(Items.bow));
    }

    /**
     * Called only once on an entity when first time spawned, via egg, mob spawner, natural spawning etc, but not called
     * when entity is reloaded from nbt. Mainly used for initializing attributes and inventory
     */
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
    {
        livingdata = super.onInitialSpawn(difficulty, livingdata);

        if (this.worldObj.provider instanceof WorldProviderHell && this.getRNG().nextInt(5) > 0)
        {
            this.tasks.addTask(4, this.aiAttackOnCollide);
            this.setSkeletonType(1);
            this.setCurrentItemOrArmor(0, new ItemStack(Items.stone_sword));
            this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0D);
        }
        else
        {
            this.tasks.addTask(4, this.aiArrowAttack);
            this.setEquipmentBasedOnDifficulty(difficulty);
            this.setEnchantmentBasedOnDifficulty(difficulty);
        }

        this.setCanPickUpLoot(this.rand.nextFloat() < 0.55F * difficulty.getClampedAdditionalDifficulty());

        if (this.getEquipmentInSlot(4) == null)
        {
            Calendar lvt_3_1_ = this.worldObj.getCurrentDate();

            if (lvt_3_1_.get(2) + 1 == 10 && lvt_3_1_.get(5) == 31 && this.rand.nextFloat() < 0.25F)
            {
                this.setCurrentItemOrArmor(4, new ItemStack(this.rand.nextFloat() < 0.1F ? Blocks.lit_pumpkin : Blocks.pumpkin));
                this.equipmentDropChances[4] = 0.0F;
            }
        }

        return livingdata;
    }

    /**
     * sets this entity's combat AI.
     */
    public void setCombatTask()
    {
        this.tasks.removeTask(this.aiAttackOnCollide);
        this.tasks.removeTask(this.aiArrowAttack);
        ItemStack lvt_1_1_ = this.getHeldItem();

        if (lvt_1_1_ != null && lvt_1_1_.getItem() == Items.bow)
        {
            this.tasks.addTask(4, this.aiArrowAttack);
        }
        else
        {
            this.tasks.addTask(4, this.aiAttackOnCollide);
        }
    }

    /**
     * Attack the specified entity using a ranged attack.
     */
    public void attackEntityWithRangedAttack(EntityLivingBase target, float p_82196_2_)
    {
        EntityArrow lvt_3_1_ = new EntityArrow(this.worldObj, this, target, 1.6F, (float)(14 - this.worldObj.getDifficulty().getDifficultyId() * 4));
        int lvt_4_1_ = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, this.getHeldItem());
        int lvt_5_1_ = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, this.getHeldItem());
        lvt_3_1_.setDamage((double)(p_82196_2_ * 2.0F) + this.rand.nextGaussian() * 0.25D + (double)((float)this.worldObj.getDifficulty().getDifficultyId() * 0.11F));

        if (lvt_4_1_ > 0)
        {
            lvt_3_1_.setDamage(lvt_3_1_.getDamage() + (double)lvt_4_1_ * 0.5D + 0.5D);
        }

        if (lvt_5_1_ > 0)
        {
            lvt_3_1_.setKnockbackStrength(lvt_5_1_);
        }

        if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, this.getHeldItem()) > 0 || this.getSkeletonType() == 1)
        {
            lvt_3_1_.setFire(100);
        }

        this.playSound("random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.worldObj.spawnEntityInWorld(lvt_3_1_);
    }

    /**
     * Return this skeleton's type.
     */
    public int getSkeletonType()
    {
        return this.dataWatcher.getWatchableObjectByte(13);
    }

    /**
     * Set this skeleton's type.
     */
    public void setSkeletonType(int p_82201_1_)
    {
        this.dataWatcher.updateObject(13, Byte.valueOf((byte)p_82201_1_));
        this.isImmuneToFire = p_82201_1_ == 1;

        if (p_82201_1_ == 1)
        {
            this.setSize(0.72F, 2.535F);
        }
        else
        {
            this.setSize(0.6F, 1.95F);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        super.readEntityFromNBT(tagCompund);

        if (tagCompund.hasKey("SkeletonType", 99))
        {
            int lvt_2_1_ = tagCompund.getByte("SkeletonType");
            this.setSkeletonType(lvt_2_1_);
        }

        this.setCombatTask();
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setByte("SkeletonType", (byte)this.getSkeletonType());
    }

    /**
     * Sets the held item, or an armor slot. Slot 0 is held item. Slot 1-4 is armor. Params: Item, slot
     */
    public void setCurrentItemOrArmor(int slotIn, ItemStack stack)
    {
        super.setCurrentItemOrArmor(slotIn, stack);

        if (!this.worldObj.isRemote && slotIn == 0)
        {
            this.setCombatTask();
        }
    }

    public float getEyeHeight()
    {
        return this.getSkeletonType() == 1 ? super.getEyeHeight() : 1.74F;
    }

    /**
     * Returns the Y Offset of this entity.
     */
    public double getYOffset()
    {
        return this.isChild() ? 0.0D : -0.35D;
    }
}
