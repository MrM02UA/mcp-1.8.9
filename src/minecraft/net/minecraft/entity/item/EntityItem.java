package net.minecraft.entity.item;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityItem extends Entity
{
    private static final Logger logger = LogManager.getLogger();

    /**
     * The age of this EntityItem (used to animate it up and down as well as expire it)
     */
    private int age;
    private int delayBeforeCanPickup;

    /** The health of this EntityItem. (For example, damage for tools) */
    private int health;
    private String thrower;
    private String owner;

    /** The EntityItem's random initial float height. */
    public float hoverStart;

    public EntityItem(World worldIn, double x, double y, double z)
    {
        super(worldIn);
        this.health = 5;
        this.hoverStart = (float)(Math.random() * Math.PI * 2.0D);
        this.setSize(0.25F, 0.25F);
        this.setPosition(x, y, z);
        this.rotationYaw = (float)(Math.random() * 360.0D);
        this.motionX = (double)((float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D));
        this.motionY = 0.20000000298023224D;
        this.motionZ = (double)((float)(Math.random() * 0.20000000298023224D - 0.10000000149011612D));
    }

    public EntityItem(World worldIn, double x, double y, double z, ItemStack stack)
    {
        this(worldIn, x, y, z);
        this.setEntityItemStack(stack);
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    public EntityItem(World worldIn)
    {
        super(worldIn);
        this.health = 5;
        this.hoverStart = (float)(Math.random() * Math.PI * 2.0D);
        this.setSize(0.25F, 0.25F);
        this.setEntityItemStack(new ItemStack(Blocks.air, 0));
    }

    protected void entityInit()
    {
        this.getDataWatcher().addObjectByDataType(10, 5);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        if (this.getEntityItem() == null)
        {
            this.setDead();
        }
        else
        {
            super.onUpdate();

            if (this.delayBeforeCanPickup > 0 && this.delayBeforeCanPickup != 32767)
            {
                --this.delayBeforeCanPickup;
            }

            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            this.motionY -= 0.03999999910593033D;
            this.noClip = this.pushOutOfBlocks(this.posX, (this.getEntityBoundingBox().minY + this.getEntityBoundingBox().maxY) / 2.0D, this.posZ);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            boolean lvt_1_1_ = (int)this.prevPosX != (int)this.posX || (int)this.prevPosY != (int)this.posY || (int)this.prevPosZ != (int)this.posZ;

            if (lvt_1_1_ || this.ticksExisted % 25 == 0)
            {
                if (this.worldObj.getBlockState(new BlockPos(this)).getBlock().getMaterial() == Material.lava)
                {
                    this.motionY = 0.20000000298023224D;
                    this.motionX = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
                    this.motionZ = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
                    this.playSound("random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
                }

                if (!this.worldObj.isRemote)
                {
                    this.searchForOtherItemsNearby();
                }
            }

            float lvt_2_1_ = 0.98F;

            if (this.onGround)
            {
                lvt_2_1_ = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ))).getBlock().slipperiness * 0.98F;
            }

            this.motionX *= (double)lvt_2_1_;
            this.motionY *= 0.9800000190734863D;
            this.motionZ *= (double)lvt_2_1_;

            if (this.onGround)
            {
                this.motionY *= -0.5D;
            }

            if (this.age != -32768)
            {
                ++this.age;
            }

            this.handleWaterMovement();

            if (!this.worldObj.isRemote && this.age >= 6000)
            {
                this.setDead();
            }
        }
    }

    /**
     * Looks for other itemstacks nearby and tries to stack them together
     */
    private void searchForOtherItemsNearby()
    {
        for (EntityItem lvt_2_1_ : this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().expand(0.5D, 0.0D, 0.5D)))
        {
            this.combineItems(lvt_2_1_);
        }
    }

    /**
     * Tries to merge this item with the item passed as the parameter. Returns true if successful. Either this item or
     * the other item will  be removed from the world.
     */
    private boolean combineItems(EntityItem other)
    {
        if (other == this)
        {
            return false;
        }
        else if (other.isEntityAlive() && this.isEntityAlive())
        {
            ItemStack lvt_2_1_ = this.getEntityItem();
            ItemStack lvt_3_1_ = other.getEntityItem();

            if (this.delayBeforeCanPickup != 32767 && other.delayBeforeCanPickup != 32767)
            {
                if (this.age != -32768 && other.age != -32768)
                {
                    if (lvt_3_1_.getItem() != lvt_2_1_.getItem())
                    {
                        return false;
                    }
                    else if (lvt_3_1_.hasTagCompound() ^ lvt_2_1_.hasTagCompound())
                    {
                        return false;
                    }
                    else if (lvt_3_1_.hasTagCompound() && !lvt_3_1_.getTagCompound().equals(lvt_2_1_.getTagCompound()))
                    {
                        return false;
                    }
                    else if (lvt_3_1_.getItem() == null)
                    {
                        return false;
                    }
                    else if (lvt_3_1_.getItem().getHasSubtypes() && lvt_3_1_.getMetadata() != lvt_2_1_.getMetadata())
                    {
                        return false;
                    }
                    else if (lvt_3_1_.stackSize < lvt_2_1_.stackSize)
                    {
                        return other.combineItems(this);
                    }
                    else if (lvt_3_1_.stackSize + lvt_2_1_.stackSize > lvt_3_1_.getMaxStackSize())
                    {
                        return false;
                    }
                    else
                    {
                        lvt_3_1_.stackSize += lvt_2_1_.stackSize;
                        other.delayBeforeCanPickup = Math.max(other.delayBeforeCanPickup, this.delayBeforeCanPickup);
                        other.age = Math.min(other.age, this.age);
                        other.setEntityItemStack(lvt_3_1_);
                        this.setDead();
                        return true;
                    }
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * sets the age of the item so that it'll despawn one minute after it has been dropped (instead of five). Used when
     * items are dropped from players in creative mode
     */
    public void setAgeToCreativeDespawnTime()
    {
        this.age = 4800;
    }

    /**
     * Returns if this entity is in water and will end up adding the waters velocity to the entity
     */
    public boolean handleWaterMovement()
    {
        if (this.worldObj.handleMaterialAcceleration(this.getEntityBoundingBox(), Material.water, this))
        {
            if (!this.inWater && !this.firstUpdate)
            {
                this.resetHeight();
            }

            this.inWater = true;
        }
        else
        {
            this.inWater = false;
        }

        return this.inWater;
    }

    /**
     * Will deal the specified amount of damage to the entity if the entity isn't immune to fire damage. Args:
     * amountDamage
     */
    protected void dealFireDamage(int amount)
    {
        this.attackEntityFrom(DamageSource.inFire, (float)amount);
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
        else if (this.getEntityItem() != null && this.getEntityItem().getItem() == Items.nether_star && source.isExplosion())
        {
            return false;
        }
        else
        {
            this.setBeenAttacked();
            this.health = (int)((float)this.health - amount);

            if (this.health <= 0)
            {
                this.setDead();
            }

            return false;
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        tagCompound.setShort("Health", (short)((byte)this.health));
        tagCompound.setShort("Age", (short)this.age);
        tagCompound.setShort("PickupDelay", (short)this.delayBeforeCanPickup);

        if (this.getThrower() != null)
        {
            tagCompound.setString("Thrower", this.thrower);
        }

        if (this.getOwner() != null)
        {
            tagCompound.setString("Owner", this.owner);
        }

        if (this.getEntityItem() != null)
        {
            tagCompound.setTag("Item", this.getEntityItem().writeToNBT(new NBTTagCompound()));
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        this.health = tagCompund.getShort("Health") & 255;
        this.age = tagCompund.getShort("Age");

        if (tagCompund.hasKey("PickupDelay"))
        {
            this.delayBeforeCanPickup = tagCompund.getShort("PickupDelay");
        }

        if (tagCompund.hasKey("Owner"))
        {
            this.owner = tagCompund.getString("Owner");
        }

        if (tagCompund.hasKey("Thrower"))
        {
            this.thrower = tagCompund.getString("Thrower");
        }

        NBTTagCompound lvt_2_1_ = tagCompund.getCompoundTag("Item");
        this.setEntityItemStack(ItemStack.loadItemStackFromNBT(lvt_2_1_));

        if (this.getEntityItem() == null)
        {
            this.setDead();
        }
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    public void onCollideWithPlayer(EntityPlayer entityIn)
    {
        if (!this.worldObj.isRemote)
        {
            ItemStack lvt_2_1_ = this.getEntityItem();
            int lvt_3_1_ = lvt_2_1_.stackSize;

            if (this.delayBeforeCanPickup == 0 && (this.owner == null || 6000 - this.age <= 200 || this.owner.equals(entityIn.getName())) && entityIn.inventory.addItemStackToInventory(lvt_2_1_))
            {
                if (lvt_2_1_.getItem() == Item.getItemFromBlock(Blocks.log))
                {
                    entityIn.triggerAchievement(AchievementList.mineWood);
                }

                if (lvt_2_1_.getItem() == Item.getItemFromBlock(Blocks.log2))
                {
                    entityIn.triggerAchievement(AchievementList.mineWood);
                }

                if (lvt_2_1_.getItem() == Items.leather)
                {
                    entityIn.triggerAchievement(AchievementList.killCow);
                }

                if (lvt_2_1_.getItem() == Items.diamond)
                {
                    entityIn.triggerAchievement(AchievementList.diamonds);
                }

                if (lvt_2_1_.getItem() == Items.blaze_rod)
                {
                    entityIn.triggerAchievement(AchievementList.blazeRod);
                }

                if (lvt_2_1_.getItem() == Items.diamond && this.getThrower() != null)
                {
                    EntityPlayer lvt_4_1_ = this.worldObj.getPlayerEntityByName(this.getThrower());

                    if (lvt_4_1_ != null && lvt_4_1_ != entityIn)
                    {
                        lvt_4_1_.triggerAchievement(AchievementList.diamondsToYou);
                    }
                }

                if (!this.isSilent())
                {
                    this.worldObj.playSoundAtEntity(entityIn, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                }

                entityIn.onItemPickup(this, lvt_3_1_);

                if (lvt_2_1_.stackSize <= 0)
                {
                    this.setDead();
                }
            }
        }
    }

    /**
     * Get the name of this object. For players this returns their username
     */
    public String getName()
    {
        return this.hasCustomName() ? this.getCustomNameTag() : StatCollector.translateToLocal("item." + this.getEntityItem().getUnlocalizedName());
    }

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    public boolean canAttackWithItem()
    {
        return false;
    }

    /**
     * Teleports the entity to another dimension. Params: Dimension number to teleport to
     */
    public void travelToDimension(int dimensionId)
    {
        super.travelToDimension(dimensionId);

        if (!this.worldObj.isRemote)
        {
            this.searchForOtherItemsNearby();
        }
    }

    /**
     * Returns the ItemStack corresponding to the Entity (Note: if no item exists, will log an error but still return an
     * ItemStack containing Block.stone)
     */
    public ItemStack getEntityItem()
    {
        ItemStack lvt_1_1_ = this.getDataWatcher().getWatchableObjectItemStack(10);

        if (lvt_1_1_ == null)
        {
            if (this.worldObj != null)
            {
                logger.error("Item entity " + this.getEntityId() + " has no item?!");
            }

            return new ItemStack(Blocks.stone);
        }
        else
        {
            return lvt_1_1_;
        }
    }

    /**
     * Sets the ItemStack for this entity
     */
    public void setEntityItemStack(ItemStack stack)
    {
        this.getDataWatcher().updateObject(10, stack);
        this.getDataWatcher().setObjectWatched(10);
    }

    public String getOwner()
    {
        return this.owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    public String getThrower()
    {
        return this.thrower;
    }

    public void setThrower(String thrower)
    {
        this.thrower = thrower;
    }

    public int getAge()
    {
        return this.age;
    }

    public void setDefaultPickupDelay()
    {
        this.delayBeforeCanPickup = 10;
    }

    public void setNoPickupDelay()
    {
        this.delayBeforeCanPickup = 0;
    }

    public void setInfinitePickupDelay()
    {
        this.delayBeforeCanPickup = 32767;
    }

    public void setPickupDelay(int ticks)
    {
        this.delayBeforeCanPickup = ticks;
    }

    public boolean cannotPickup()
    {
        return this.delayBeforeCanPickup > 0;
    }

    public void setNoDespawn()
    {
        this.age = -6000;
    }

    public void func_174870_v()
    {
        this.setInfinitePickupDelay();
        this.age = 5999;
    }
}
