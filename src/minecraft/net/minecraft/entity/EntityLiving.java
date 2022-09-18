package net.minecraft.entity;

import java.util.UUID;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityJumpHelper;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntitySenses;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityLiving extends EntityLivingBase
{
    /** Number of ticks since this EntityLiving last produced its sound */
    public int livingSoundTime;

    /** The experience points the Entity gives. */
    protected int experienceValue;
    private EntityLookHelper lookHelper;
    protected EntityMoveHelper moveHelper;

    /** Entity jumping helper */
    protected EntityJumpHelper jumpHelper;
    private EntityBodyHelper bodyHelper;
    protected PathNavigate navigator;

    /** Passive tasks (wandering, look, idle, ...) */
    protected final EntityAITasks tasks;

    /** Fighting tasks (used by monsters, wolves, ocelots) */
    protected final EntityAITasks targetTasks;

    /** The active target the Task system uses for tracking */
    private EntityLivingBase attackTarget;
    private EntitySenses senses;

    /** Equipment (armor and held item) for this entity. */
    private ItemStack[] equipment = new ItemStack[5];

    /** Chances for each equipment piece from dropping when this entity dies. */
    protected float[] equipmentDropChances = new float[5];

    /** Whether this entity can pick up items from the ground. */
    private boolean canPickUpLoot;

    /** Whether this entity should NOT despawn. */
    private boolean persistenceRequired;
    private boolean isLeashed;
    private Entity leashedToEntity;
    private NBTTagCompound leashNBTTag;

    public EntityLiving(World worldIn)
    {
        super(worldIn);
        this.tasks = new EntityAITasks(worldIn != null && worldIn.theProfiler != null ? worldIn.theProfiler : null);
        this.targetTasks = new EntityAITasks(worldIn != null && worldIn.theProfiler != null ? worldIn.theProfiler : null);
        this.lookHelper = new EntityLookHelper(this);
        this.moveHelper = new EntityMoveHelper(this);
        this.jumpHelper = new EntityJumpHelper(this);
        this.bodyHelper = new EntityBodyHelper(this);
        this.navigator = this.getNewNavigator(worldIn);
        this.senses = new EntitySenses(this);

        for (int lvt_2_1_ = 0; lvt_2_1_ < this.equipmentDropChances.length; ++lvt_2_1_)
        {
            this.equipmentDropChances[lvt_2_1_] = 0.085F;
        }
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);
    }

    /**
     * Returns new PathNavigateGround instance
     */
    protected PathNavigate getNewNavigator(World worldIn)
    {
        return new PathNavigateGround(this, worldIn);
    }

    public EntityLookHelper getLookHelper()
    {
        return this.lookHelper;
    }

    public EntityMoveHelper getMoveHelper()
    {
        return this.moveHelper;
    }

    public EntityJumpHelper getJumpHelper()
    {
        return this.jumpHelper;
    }

    public PathNavigate getNavigator()
    {
        return this.navigator;
    }

    /**
     * returns the EntitySenses Object for the EntityLiving
     */
    public EntitySenses getEntitySenses()
    {
        return this.senses;
    }

    /**
     * Gets the active target the Task system uses for tracking
     */
    public EntityLivingBase getAttackTarget()
    {
        return this.attackTarget;
    }

    /**
     * Sets the active target the Task system uses for tracking
     */
    public void setAttackTarget(EntityLivingBase entitylivingbaseIn)
    {
        this.attackTarget = entitylivingbaseIn;
    }

    /**
     * Returns true if this entity can attack entities of the specified class.
     */
    public boolean canAttackClass(Class <? extends EntityLivingBase > cls)
    {
        return cls != EntityGhast.class;
    }

    /**
     * This function applies the benefits of growing back wool and faster growing up to the acting entity. (This
     * function is used in the AIEatGrass)
     */
    public void eatGrassBonus()
    {
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(15, Byte.valueOf((byte)0));
    }

    /**
     * Get number of ticks, at least during which the living entity will be silent.
     */
    public int getTalkInterval()
    {
        return 80;
    }

    /**
     * Plays living's sound at its position
     */
    public void playLivingSound()
    {
        String lvt_1_1_ = this.getLivingSound();

        if (lvt_1_1_ != null)
        {
            this.playSound(lvt_1_1_, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    /**
     * Gets called every tick from main Entity class
     */
    public void onEntityUpdate()
    {
        super.onEntityUpdate();
        this.worldObj.theProfiler.startSection("mobBaseTick");

        if (this.isEntityAlive() && this.rand.nextInt(1000) < this.livingSoundTime++)
        {
            this.livingSoundTime = -this.getTalkInterval();
            this.playLivingSound();
        }

        this.worldObj.theProfiler.endSection();
    }

    /**
     * Get the experience points the entity currently has.
     */
    protected int getExperiencePoints(EntityPlayer player)
    {
        if (this.experienceValue > 0)
        {
            int lvt_2_1_ = this.experienceValue;
            ItemStack[] lvt_3_1_ = this.getInventory();

            for (int lvt_4_1_ = 0; lvt_4_1_ < lvt_3_1_.length; ++lvt_4_1_)
            {
                if (lvt_3_1_[lvt_4_1_] != null && this.equipmentDropChances[lvt_4_1_] <= 1.0F)
                {
                    lvt_2_1_ += 1 + this.rand.nextInt(3);
                }
            }

            return lvt_2_1_;
        }
        else
        {
            return this.experienceValue;
        }
    }

    /**
     * Spawns an explosion particle around the Entity's location
     */
    public void spawnExplosionParticle()
    {
        if (this.worldObj.isRemote)
        {
            for (int lvt_1_1_ = 0; lvt_1_1_ < 20; ++lvt_1_1_)
            {
                double lvt_2_1_ = this.rand.nextGaussian() * 0.02D;
                double lvt_4_1_ = this.rand.nextGaussian() * 0.02D;
                double lvt_6_1_ = this.rand.nextGaussian() * 0.02D;
                double lvt_8_1_ = 10.0D;
                this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width - lvt_2_1_ * lvt_8_1_, this.posY + (double)(this.rand.nextFloat() * this.height) - lvt_4_1_ * lvt_8_1_, this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width - lvt_6_1_ * lvt_8_1_, lvt_2_1_, lvt_4_1_, lvt_6_1_, new int[0]);
            }
        }
        else
        {
            this.worldObj.setEntityState(this, (byte)20);
        }
    }

    public void handleStatusUpdate(byte id)
    {
        if (id == 20)
        {
            this.spawnExplosionParticle();
        }
        else
        {
            super.handleStatusUpdate(id);
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (!this.worldObj.isRemote)
        {
            this.updateLeashedState();
        }
    }

    protected float updateDistance(float p_110146_1_, float p_110146_2_)
    {
        this.bodyHelper.updateRenderAngles();
        return p_110146_2_;
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    protected String getLivingSound()
    {
        return null;
    }

    protected Item getDropItem()
    {
        return null;
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
        Item lvt_3_1_ = this.getDropItem();

        if (lvt_3_1_ != null)
        {
            int lvt_4_1_ = this.rand.nextInt(3);

            if (lootingModifier > 0)
            {
                lvt_4_1_ += this.rand.nextInt(lootingModifier + 1);
            }

            for (int lvt_5_1_ = 0; lvt_5_1_ < lvt_4_1_; ++lvt_5_1_)
            {
                this.dropItem(lvt_3_1_, 1);
            }
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setBoolean("CanPickUpLoot", this.canPickUpLoot());
        tagCompound.setBoolean("PersistenceRequired", this.persistenceRequired);
        NBTTagList lvt_2_1_ = new NBTTagList();

        for (int lvt_3_1_ = 0; lvt_3_1_ < this.equipment.length; ++lvt_3_1_)
        {
            NBTTagCompound lvt_4_1_ = new NBTTagCompound();

            if (this.equipment[lvt_3_1_] != null)
            {
                this.equipment[lvt_3_1_].writeToNBT(lvt_4_1_);
            }

            lvt_2_1_.appendTag(lvt_4_1_);
        }

        tagCompound.setTag("Equipment", lvt_2_1_);
        NBTTagList lvt_3_2_ = new NBTTagList();

        for (int lvt_4_2_ = 0; lvt_4_2_ < this.equipmentDropChances.length; ++lvt_4_2_)
        {
            lvt_3_2_.appendTag(new NBTTagFloat(this.equipmentDropChances[lvt_4_2_]));
        }

        tagCompound.setTag("DropChances", lvt_3_2_);
        tagCompound.setBoolean("Leashed", this.isLeashed);

        if (this.leashedToEntity != null)
        {
            NBTTagCompound lvt_4_3_ = new NBTTagCompound();

            if (this.leashedToEntity instanceof EntityLivingBase)
            {
                lvt_4_3_.setLong("UUIDMost", this.leashedToEntity.getUniqueID().getMostSignificantBits());
                lvt_4_3_.setLong("UUIDLeast", this.leashedToEntity.getUniqueID().getLeastSignificantBits());
            }
            else if (this.leashedToEntity instanceof EntityHanging)
            {
                BlockPos lvt_5_1_ = ((EntityHanging)this.leashedToEntity).getHangingPosition();
                lvt_4_3_.setInteger("X", lvt_5_1_.getX());
                lvt_4_3_.setInteger("Y", lvt_5_1_.getY());
                lvt_4_3_.setInteger("Z", lvt_5_1_.getZ());
            }

            tagCompound.setTag("Leash", lvt_4_3_);
        }

        if (this.isAIDisabled())
        {
            tagCompound.setBoolean("NoAI", this.isAIDisabled());
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        super.readEntityFromNBT(tagCompund);

        if (tagCompund.hasKey("CanPickUpLoot", 1))
        {
            this.setCanPickUpLoot(tagCompund.getBoolean("CanPickUpLoot"));
        }

        this.persistenceRequired = tagCompund.getBoolean("PersistenceRequired");

        if (tagCompund.hasKey("Equipment", 9))
        {
            NBTTagList lvt_2_1_ = tagCompund.getTagList("Equipment", 10);

            for (int lvt_3_1_ = 0; lvt_3_1_ < this.equipment.length; ++lvt_3_1_)
            {
                this.equipment[lvt_3_1_] = ItemStack.loadItemStackFromNBT(lvt_2_1_.getCompoundTagAt(lvt_3_1_));
            }
        }

        if (tagCompund.hasKey("DropChances", 9))
        {
            NBTTagList lvt_2_2_ = tagCompund.getTagList("DropChances", 5);

            for (int lvt_3_2_ = 0; lvt_3_2_ < lvt_2_2_.tagCount(); ++lvt_3_2_)
            {
                this.equipmentDropChances[lvt_3_2_] = lvt_2_2_.getFloatAt(lvt_3_2_);
            }
        }

        this.isLeashed = tagCompund.getBoolean("Leashed");

        if (this.isLeashed && tagCompund.hasKey("Leash", 10))
        {
            this.leashNBTTag = tagCompund.getCompoundTag("Leash");
        }

        this.setNoAI(tagCompund.getBoolean("NoAI"));
    }

    public void setMoveForward(float p_70657_1_)
    {
        this.moveForward = p_70657_1_;
    }

    /**
     * set the movespeed used for the new AI system
     */
    public void setAIMoveSpeed(float speedIn)
    {
        super.setAIMoveSpeed(speedIn);
        this.setMoveForward(speedIn);
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        this.worldObj.theProfiler.startSection("looting");

        if (!this.worldObj.isRemote && this.canPickUpLoot() && !this.dead && this.worldObj.getGameRules().getBoolean("mobGriefing"))
        {
            for (EntityItem lvt_3_1_ : this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().expand(1.0D, 0.0D, 1.0D)))
            {
                if (!lvt_3_1_.isDead && lvt_3_1_.getEntityItem() != null && !lvt_3_1_.cannotPickup())
                {
                    this.updateEquipmentIfNeeded(lvt_3_1_);
                }
            }
        }

        this.worldObj.theProfiler.endSection();
    }

    /**
     * Tests if this entity should pickup a weapon or an armor. Entity drops current weapon or armor if the new one is
     * better.
     */
    protected void updateEquipmentIfNeeded(EntityItem itemEntity)
    {
        ItemStack lvt_2_1_ = itemEntity.getEntityItem();
        int lvt_3_1_ = getArmorPosition(lvt_2_1_);

        if (lvt_3_1_ > -1)
        {
            boolean lvt_4_1_ = true;
            ItemStack lvt_5_1_ = this.getEquipmentInSlot(lvt_3_1_);

            if (lvt_5_1_ != null)
            {
                if (lvt_3_1_ == 0)
                {
                    if (lvt_2_1_.getItem() instanceof ItemSword && !(lvt_5_1_.getItem() instanceof ItemSword))
                    {
                        lvt_4_1_ = true;
                    }
                    else if (lvt_2_1_.getItem() instanceof ItemSword && lvt_5_1_.getItem() instanceof ItemSword)
                    {
                        ItemSword lvt_6_1_ = (ItemSword)lvt_2_1_.getItem();
                        ItemSword lvt_7_1_ = (ItemSword)lvt_5_1_.getItem();

                        if (lvt_6_1_.getDamageVsEntity() != lvt_7_1_.getDamageVsEntity())
                        {
                            lvt_4_1_ = lvt_6_1_.getDamageVsEntity() > lvt_7_1_.getDamageVsEntity();
                        }
                        else
                        {
                            lvt_4_1_ = lvt_2_1_.getMetadata() > lvt_5_1_.getMetadata() || lvt_2_1_.hasTagCompound() && !lvt_5_1_.hasTagCompound();
                        }
                    }
                    else if (lvt_2_1_.getItem() instanceof ItemBow && lvt_5_1_.getItem() instanceof ItemBow)
                    {
                        lvt_4_1_ = lvt_2_1_.hasTagCompound() && !lvt_5_1_.hasTagCompound();
                    }
                    else
                    {
                        lvt_4_1_ = false;
                    }
                }
                else if (lvt_2_1_.getItem() instanceof ItemArmor && !(lvt_5_1_.getItem() instanceof ItemArmor))
                {
                    lvt_4_1_ = true;
                }
                else if (lvt_2_1_.getItem() instanceof ItemArmor && lvt_5_1_.getItem() instanceof ItemArmor)
                {
                    ItemArmor lvt_6_2_ = (ItemArmor)lvt_2_1_.getItem();
                    ItemArmor lvt_7_2_ = (ItemArmor)lvt_5_1_.getItem();

                    if (lvt_6_2_.damageReduceAmount != lvt_7_2_.damageReduceAmount)
                    {
                        lvt_4_1_ = lvt_6_2_.damageReduceAmount > lvt_7_2_.damageReduceAmount;
                    }
                    else
                    {
                        lvt_4_1_ = lvt_2_1_.getMetadata() > lvt_5_1_.getMetadata() || lvt_2_1_.hasTagCompound() && !lvt_5_1_.hasTagCompound();
                    }
                }
                else
                {
                    lvt_4_1_ = false;
                }
            }

            if (lvt_4_1_ && this.func_175448_a(lvt_2_1_))
            {
                if (lvt_5_1_ != null && this.rand.nextFloat() - 0.1F < this.equipmentDropChances[lvt_3_1_])
                {
                    this.entityDropItem(lvt_5_1_, 0.0F);
                }

                if (lvt_2_1_.getItem() == Items.diamond && itemEntity.getThrower() != null)
                {
                    EntityPlayer lvt_6_3_ = this.worldObj.getPlayerEntityByName(itemEntity.getThrower());

                    if (lvt_6_3_ != null)
                    {
                        lvt_6_3_.triggerAchievement(AchievementList.diamondsToYou);
                    }
                }

                this.setCurrentItemOrArmor(lvt_3_1_, lvt_2_1_);
                this.equipmentDropChances[lvt_3_1_] = 2.0F;
                this.persistenceRequired = true;
                this.onItemPickup(itemEntity, 1);
                itemEntity.setDead();
            }
        }
    }

    protected boolean func_175448_a(ItemStack stack)
    {
        return true;
    }

    /**
     * Determines if an entity can be despawned, used on idle far away entities
     */
    protected boolean canDespawn()
    {
        return true;
    }

    /**
     * Makes the entity despawn if requirements are reached
     */
    protected void despawnEntity()
    {
        if (this.persistenceRequired)
        {
            this.entityAge = 0;
        }
        else
        {
            Entity lvt_1_1_ = this.worldObj.getClosestPlayerToEntity(this, -1.0D);

            if (lvt_1_1_ != null)
            {
                double lvt_2_1_ = lvt_1_1_.posX - this.posX;
                double lvt_4_1_ = lvt_1_1_.posY - this.posY;
                double lvt_6_1_ = lvt_1_1_.posZ - this.posZ;
                double lvt_8_1_ = lvt_2_1_ * lvt_2_1_ + lvt_4_1_ * lvt_4_1_ + lvt_6_1_ * lvt_6_1_;

                if (this.canDespawn() && lvt_8_1_ > 16384.0D)
                {
                    this.setDead();
                }

                if (this.entityAge > 600 && this.rand.nextInt(800) == 0 && lvt_8_1_ > 1024.0D && this.canDespawn())
                {
                    this.setDead();
                }
                else if (lvt_8_1_ < 1024.0D)
                {
                    this.entityAge = 0;
                }
            }
        }
    }

    protected final void updateEntityActionState()
    {
        ++this.entityAge;
        this.worldObj.theProfiler.startSection("checkDespawn");
        this.despawnEntity();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("sensing");
        this.senses.clearSensingCache();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("targetSelector");
        this.targetTasks.onUpdateTasks();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("goalSelector");
        this.tasks.onUpdateTasks();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("navigation");
        this.navigator.onUpdateNavigation();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("mob tick");
        this.updateAITasks();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("controls");
        this.worldObj.theProfiler.startSection("move");
        this.moveHelper.onUpdateMoveHelper();
        this.worldObj.theProfiler.endStartSection("look");
        this.lookHelper.onUpdateLook();
        this.worldObj.theProfiler.endStartSection("jump");
        this.jumpHelper.doJump();
        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.endSection();
    }

    protected void updateAITasks()
    {
    }

    /**
     * The speed it takes to move the entityliving's rotationPitch through the faceEntity method. This is only currently
     * use in wolves.
     */
    public int getVerticalFaceSpeed()
    {
        return 40;
    }

    /**
     * Changes pitch and yaw so that the entity calling the function is facing the entity provided as an argument.
     */
    public void faceEntity(Entity entityIn, float p_70625_2_, float p_70625_3_)
    {
        double lvt_4_1_ = entityIn.posX - this.posX;
        double lvt_8_1_ = entityIn.posZ - this.posZ;
        double lvt_6_1_;

        if (entityIn instanceof EntityLivingBase)
        {
            EntityLivingBase lvt_10_1_ = (EntityLivingBase)entityIn;
            lvt_6_1_ = lvt_10_1_.posY + (double)lvt_10_1_.getEyeHeight() - (this.posY + (double)this.getEyeHeight());
        }
        else
        {
            lvt_6_1_ = (entityIn.getEntityBoundingBox().minY + entityIn.getEntityBoundingBox().maxY) / 2.0D - (this.posY + (double)this.getEyeHeight());
        }

        double lvt_10_2_ = (double)MathHelper.sqrt_double(lvt_4_1_ * lvt_4_1_ + lvt_8_1_ * lvt_8_1_);
        float lvt_12_1_ = (float)(MathHelper.atan2(lvt_8_1_, lvt_4_1_) * 180.0D / Math.PI) - 90.0F;
        float lvt_13_1_ = (float)(-(MathHelper.atan2(lvt_6_1_, lvt_10_2_) * 180.0D / Math.PI));
        this.rotationPitch = this.updateRotation(this.rotationPitch, lvt_13_1_, p_70625_3_);
        this.rotationYaw = this.updateRotation(this.rotationYaw, lvt_12_1_, p_70625_2_);
    }

    /**
     * Arguments: current rotation, intended rotation, max increment.
     */
    private float updateRotation(float p_70663_1_, float p_70663_2_, float p_70663_3_)
    {
        float lvt_4_1_ = MathHelper.wrapAngleTo180_float(p_70663_2_ - p_70663_1_);

        if (lvt_4_1_ > p_70663_3_)
        {
            lvt_4_1_ = p_70663_3_;
        }

        if (lvt_4_1_ < -p_70663_3_)
        {
            lvt_4_1_ = -p_70663_3_;
        }

        return p_70663_1_ + lvt_4_1_;
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
        return true;
    }

    /**
     * Checks that the entity is not colliding with any blocks / liquids
     */
    public boolean isNotColliding()
    {
        return this.worldObj.checkNoEntityCollision(this.getEntityBoundingBox(), this) && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty() && !this.worldObj.isAnyLiquid(this.getEntityBoundingBox());
    }

    /**
     * Returns render size modifier
     */
    public float getRenderSizeModifier()
    {
        return 1.0F;
    }

    /**
     * Will return how many at most can spawn in a chunk at once.
     */
    public int getMaxSpawnedInChunk()
    {
        return 4;
    }

    /**
     * The maximum height from where the entity is alowed to jump (used in pathfinder)
     */
    public int getMaxFallHeight()
    {
        if (this.getAttackTarget() == null)
        {
            return 3;
        }
        else
        {
            int lvt_1_1_ = (int)(this.getHealth() - this.getMaxHealth() * 0.33F);
            lvt_1_1_ = lvt_1_1_ - (3 - this.worldObj.getDifficulty().getDifficultyId()) * 4;

            if (lvt_1_1_ < 0)
            {
                lvt_1_1_ = 0;
            }

            return lvt_1_1_ + 3;
        }
    }

    /**
     * Returns the item that this EntityLiving is holding, if any.
     */
    public ItemStack getHeldItem()
    {
        return this.equipment[0];
    }

    /**
     * 0: Tool in Hand; 1-4: Armor
     */
    public ItemStack getEquipmentInSlot(int slotIn)
    {
        return this.equipment[slotIn];
    }

    public ItemStack getCurrentArmor(int slotIn)
    {
        return this.equipment[slotIn + 1];
    }

    /**
     * Sets the held item, or an armor slot. Slot 0 is held item. Slot 1-4 is armor. Params: Item, slot
     */
    public void setCurrentItemOrArmor(int slotIn, ItemStack stack)
    {
        this.equipment[slotIn] = stack;
    }

    /**
     * returns the inventory of this entity (only used in EntityPlayerMP it seems)
     */
    public ItemStack[] getInventory()
    {
        return this.equipment;
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
        for (int lvt_3_1_ = 0; lvt_3_1_ < this.getInventory().length; ++lvt_3_1_)
        {
            ItemStack lvt_4_1_ = this.getEquipmentInSlot(lvt_3_1_);
            boolean lvt_5_1_ = this.equipmentDropChances[lvt_3_1_] > 1.0F;

            if (lvt_4_1_ != null && (wasRecentlyHit || lvt_5_1_) && this.rand.nextFloat() - (float)lootingModifier * 0.01F < this.equipmentDropChances[lvt_3_1_])
            {
                if (!lvt_5_1_ && lvt_4_1_.isItemStackDamageable())
                {
                    int lvt_6_1_ = Math.max(lvt_4_1_.getMaxDamage() - 25, 1);
                    int lvt_7_1_ = lvt_4_1_.getMaxDamage() - this.rand.nextInt(this.rand.nextInt(lvt_6_1_) + 1);

                    if (lvt_7_1_ > lvt_6_1_)
                    {
                        lvt_7_1_ = lvt_6_1_;
                    }

                    if (lvt_7_1_ < 1)
                    {
                        lvt_7_1_ = 1;
                    }

                    lvt_4_1_.setItemDamage(lvt_7_1_);
                }

                this.entityDropItem(lvt_4_1_, 0.0F);
            }
        }
    }

    /**
     * Gives armor or weapon for entity based on given DifficultyInstance
     */
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty)
    {
        if (this.rand.nextFloat() < 0.15F * difficulty.getClampedAdditionalDifficulty())
        {
            int lvt_2_1_ = this.rand.nextInt(2);
            float lvt_3_1_ = this.worldObj.getDifficulty() == EnumDifficulty.HARD ? 0.1F : 0.25F;

            if (this.rand.nextFloat() < 0.095F)
            {
                ++lvt_2_1_;
            }

            if (this.rand.nextFloat() < 0.095F)
            {
                ++lvt_2_1_;
            }

            if (this.rand.nextFloat() < 0.095F)
            {
                ++lvt_2_1_;
            }

            for (int lvt_4_1_ = 3; lvt_4_1_ >= 0; --lvt_4_1_)
            {
                ItemStack lvt_5_1_ = this.getCurrentArmor(lvt_4_1_);

                if (lvt_4_1_ < 3 && this.rand.nextFloat() < lvt_3_1_)
                {
                    break;
                }

                if (lvt_5_1_ == null)
                {
                    Item lvt_6_1_ = getArmorItemForSlot(lvt_4_1_ + 1, lvt_2_1_);

                    if (lvt_6_1_ != null)
                    {
                        this.setCurrentItemOrArmor(lvt_4_1_ + 1, new ItemStack(lvt_6_1_));
                    }
                }
            }
        }
    }

    public static int getArmorPosition(ItemStack stack)
    {
        if (stack.getItem() != Item.getItemFromBlock(Blocks.pumpkin) && stack.getItem() != Items.skull)
        {
            if (stack.getItem() instanceof ItemArmor)
            {
                switch (((ItemArmor)stack.getItem()).armorType)
                {
                    case 0:
                        return 4;

                    case 1:
                        return 3;

                    case 2:
                        return 2;

                    case 3:
                        return 1;
                }
            }

            return 0;
        }
        else
        {
            return 4;
        }
    }

    /**
     * Gets the vanilla armor Item that can go in the slot specified for the given tier.
     */
    public static Item getArmorItemForSlot(int armorSlot, int itemTier)
    {
        switch (armorSlot)
        {
            case 4:
                if (itemTier == 0)
                {
                    return Items.leather_helmet;
                }
                else if (itemTier == 1)
                {
                    return Items.golden_helmet;
                }
                else if (itemTier == 2)
                {
                    return Items.chainmail_helmet;
                }
                else if (itemTier == 3)
                {
                    return Items.iron_helmet;
                }
                else if (itemTier == 4)
                {
                    return Items.diamond_helmet;
                }

            case 3:
                if (itemTier == 0)
                {
                    return Items.leather_chestplate;
                }
                else if (itemTier == 1)
                {
                    return Items.golden_chestplate;
                }
                else if (itemTier == 2)
                {
                    return Items.chainmail_chestplate;
                }
                else if (itemTier == 3)
                {
                    return Items.iron_chestplate;
                }
                else if (itemTier == 4)
                {
                    return Items.diamond_chestplate;
                }

            case 2:
                if (itemTier == 0)
                {
                    return Items.leather_leggings;
                }
                else if (itemTier == 1)
                {
                    return Items.golden_leggings;
                }
                else if (itemTier == 2)
                {
                    return Items.chainmail_leggings;
                }
                else if (itemTier == 3)
                {
                    return Items.iron_leggings;
                }
                else if (itemTier == 4)
                {
                    return Items.diamond_leggings;
                }

            case 1:
                if (itemTier == 0)
                {
                    return Items.leather_boots;
                }
                else if (itemTier == 1)
                {
                    return Items.golden_boots;
                }
                else if (itemTier == 2)
                {
                    return Items.chainmail_boots;
                }
                else if (itemTier == 3)
                {
                    return Items.iron_boots;
                }
                else if (itemTier == 4)
                {
                    return Items.diamond_boots;
                }

            default:
                return null;
        }
    }

    /**
     * Enchants Entity's current equipments based on given DifficultyInstance
     */
    protected void setEnchantmentBasedOnDifficulty(DifficultyInstance difficulty)
    {
        float lvt_2_1_ = difficulty.getClampedAdditionalDifficulty();

        if (this.getHeldItem() != null && this.rand.nextFloat() < 0.25F * lvt_2_1_)
        {
            EnchantmentHelper.addRandomEnchantment(this.rand, this.getHeldItem(), (int)(5.0F + lvt_2_1_ * (float)this.rand.nextInt(18)));
        }

        for (int lvt_3_1_ = 0; lvt_3_1_ < 4; ++lvt_3_1_)
        {
            ItemStack lvt_4_1_ = this.getCurrentArmor(lvt_3_1_);

            if (lvt_4_1_ != null && this.rand.nextFloat() < 0.5F * lvt_2_1_)
            {
                EnchantmentHelper.addRandomEnchantment(this.rand, lvt_4_1_, (int)(5.0F + lvt_2_1_ * (float)this.rand.nextInt(18)));
            }
        }
    }

    /**
     * Called only once on an entity when first time spawned, via egg, mob spawner, natural spawning etc, but not called
     * when entity is reloaded from nbt. Mainly used for initializing attributes and inventory
     */
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
    {
        this.getEntityAttribute(SharedMonsterAttributes.followRange).applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextGaussian() * 0.05D, 1));
        return livingdata;
    }

    /**
     * returns true if all the conditions for steering the entity are met. For pigs, this is true if it is being ridden
     * by a player and the player is holding a carrot-on-a-stick
     */
    public boolean canBeSteered()
    {
        return false;
    }

    /**
     * Enable the Entity persistence
     */
    public void enablePersistence()
    {
        this.persistenceRequired = true;
    }

    public void setEquipmentDropChance(int slotIn, float chance)
    {
        this.equipmentDropChances[slotIn] = chance;
    }

    public boolean canPickUpLoot()
    {
        return this.canPickUpLoot;
    }

    public void setCanPickUpLoot(boolean canPickup)
    {
        this.canPickUpLoot = canPickup;
    }

    public boolean isNoDespawnRequired()
    {
        return this.persistenceRequired;
    }

    /**
     * First layer of player interaction
     */
    public final boolean interactFirst(EntityPlayer playerIn)
    {
        if (this.getLeashed() && this.getLeashedToEntity() == playerIn)
        {
            this.clearLeashed(true, !playerIn.capabilities.isCreativeMode);
            return true;
        }
        else
        {
            ItemStack lvt_2_1_ = playerIn.inventory.getCurrentItem();

            if (lvt_2_1_ != null && lvt_2_1_.getItem() == Items.lead && this.allowLeashing())
            {
                if (!(this instanceof EntityTameable) || !((EntityTameable)this).isTamed())
                {
                    this.setLeashedToEntity(playerIn, true);
                    --lvt_2_1_.stackSize;
                    return true;
                }

                if (((EntityTameable)this).isOwner(playerIn))
                {
                    this.setLeashedToEntity(playerIn, true);
                    --lvt_2_1_.stackSize;
                    return true;
                }
            }

            if (this.interact(playerIn))
            {
                return true;
            }
            else
            {
                return super.interactFirst(playerIn);
            }
        }
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    protected boolean interact(EntityPlayer player)
    {
        return false;
    }

    /**
     * Applies logic related to leashes, for example dragging the entity or breaking the leash.
     */
    protected void updateLeashedState()
    {
        if (this.leashNBTTag != null)
        {
            this.recreateLeash();
        }

        if (this.isLeashed)
        {
            if (!this.isEntityAlive())
            {
                this.clearLeashed(true, true);
            }

            if (this.leashedToEntity == null || this.leashedToEntity.isDead)
            {
                this.clearLeashed(true, true);
            }
        }
    }

    /**
     * Removes the leash from this entity
     */
    public void clearLeashed(boolean sendPacket, boolean dropLead)
    {
        if (this.isLeashed)
        {
            this.isLeashed = false;
            this.leashedToEntity = null;

            if (!this.worldObj.isRemote && dropLead)
            {
                this.dropItem(Items.lead, 1);
            }

            if (!this.worldObj.isRemote && sendPacket && this.worldObj instanceof WorldServer)
            {
                ((WorldServer)this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S1BPacketEntityAttach(1, this, (Entity)null));
            }
        }
    }

    public boolean allowLeashing()
    {
        return !this.getLeashed() && !(this instanceof IMob);
    }

    public boolean getLeashed()
    {
        return this.isLeashed;
    }

    public Entity getLeashedToEntity()
    {
        return this.leashedToEntity;
    }

    /**
     * Sets the entity to be leashed to.
     */
    public void setLeashedToEntity(Entity entityIn, boolean sendAttachNotification)
    {
        this.isLeashed = true;
        this.leashedToEntity = entityIn;

        if (!this.worldObj.isRemote && sendAttachNotification && this.worldObj instanceof WorldServer)
        {
            ((WorldServer)this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S1BPacketEntityAttach(1, this, this.leashedToEntity));
        }
    }

    private void recreateLeash()
    {
        if (this.isLeashed && this.leashNBTTag != null)
        {
            if (this.leashNBTTag.hasKey("UUIDMost", 4) && this.leashNBTTag.hasKey("UUIDLeast", 4))
            {
                UUID lvt_1_1_ = new UUID(this.leashNBTTag.getLong("UUIDMost"), this.leashNBTTag.getLong("UUIDLeast"));

                for (EntityLivingBase lvt_4_1_ : this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().expand(10.0D, 10.0D, 10.0D)))
                {
                    if (lvt_4_1_.getUniqueID().equals(lvt_1_1_))
                    {
                        this.leashedToEntity = lvt_4_1_;
                        break;
                    }
                }
            }
            else if (this.leashNBTTag.hasKey("X", 99) && this.leashNBTTag.hasKey("Y", 99) && this.leashNBTTag.hasKey("Z", 99))
            {
                BlockPos lvt_1_2_ = new BlockPos(this.leashNBTTag.getInteger("X"), this.leashNBTTag.getInteger("Y"), this.leashNBTTag.getInteger("Z"));
                EntityLeashKnot lvt_2_2_ = EntityLeashKnot.getKnotForPosition(this.worldObj, lvt_1_2_);

                if (lvt_2_2_ == null)
                {
                    lvt_2_2_ = EntityLeashKnot.createKnot(this.worldObj, lvt_1_2_);
                }

                this.leashedToEntity = lvt_2_2_;
            }
            else
            {
                this.clearLeashed(false, true);
            }
        }

        this.leashNBTTag = null;
    }

    public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn)
    {
        int lvt_3_1_;

        if (inventorySlot == 99)
        {
            lvt_3_1_ = 0;
        }
        else
        {
            lvt_3_1_ = inventorySlot - 100 + 1;

            if (lvt_3_1_ < 0 || lvt_3_1_ >= this.equipment.length)
            {
                return false;
            }
        }

        if (itemStackIn != null && getArmorPosition(itemStackIn) != lvt_3_1_ && (lvt_3_1_ != 4 || !(itemStackIn.getItem() instanceof ItemBlock)))
        {
            return false;
        }
        else
        {
            this.setCurrentItemOrArmor(lvt_3_1_, itemStackIn);
            return true;
        }
    }

    /**
     * Returns whether the entity is in a server world
     */
    public boolean isServerWorld()
    {
        return super.isServerWorld() && !this.isAIDisabled();
    }

    /**
     * Set whether this Entity's AI is disabled
     */
    public void setNoAI(boolean disable)
    {
        this.dataWatcher.updateObject(15, Byte.valueOf((byte)(disable ? 1 : 0)));
    }

    /**
     * Get whether this Entity's AI is disabled
     */
    public boolean isAIDisabled()
    {
        return this.dataWatcher.getWatchableObjectByte(15) != 0;
    }

    public static enum SpawnPlacementType
    {
        ON_GROUND,
        IN_AIR,
        IN_WATER;
    }
}
