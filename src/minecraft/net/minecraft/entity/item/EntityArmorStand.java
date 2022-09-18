package net.minecraft.entity.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Rotations;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityArmorStand extends EntityLivingBase
{
    private static final Rotations DEFAULT_HEAD_ROTATION = new Rotations(0.0F, 0.0F, 0.0F);
    private static final Rotations DEFAULT_BODY_ROTATION = new Rotations(0.0F, 0.0F, 0.0F);
    private static final Rotations DEFAULT_LEFTARM_ROTATION = new Rotations(-10.0F, 0.0F, -10.0F);
    private static final Rotations DEFAULT_RIGHTARM_ROTATION = new Rotations(-15.0F, 0.0F, 10.0F);
    private static final Rotations DEFAULT_LEFTLEG_ROTATION = new Rotations(-1.0F, 0.0F, -1.0F);
    private static final Rotations DEFAULT_RIGHTLEG_ROTATION = new Rotations(1.0F, 0.0F, 1.0F);
    private final ItemStack[] contents;
    private boolean canInteract;

    /**
     * After punching the stand, the cooldown before you can punch it again without breaking it.
     */
    private long punchCooldown;
    private int disabledSlots;
    private boolean field_181028_bj;
    private Rotations headRotation;
    private Rotations bodyRotation;
    private Rotations leftArmRotation;
    private Rotations rightArmRotation;
    private Rotations leftLegRotation;
    private Rotations rightLegRotation;

    public EntityArmorStand(World worldIn)
    {
        super(worldIn);
        this.contents = new ItemStack[5];
        this.headRotation = DEFAULT_HEAD_ROTATION;
        this.bodyRotation = DEFAULT_BODY_ROTATION;
        this.leftArmRotation = DEFAULT_LEFTARM_ROTATION;
        this.rightArmRotation = DEFAULT_RIGHTARM_ROTATION;
        this.leftLegRotation = DEFAULT_LEFTLEG_ROTATION;
        this.rightLegRotation = DEFAULT_RIGHTLEG_ROTATION;
        this.setSilent(true);
        this.noClip = this.hasNoGravity();
        this.setSize(0.5F, 1.975F);
    }

    public EntityArmorStand(World worldIn, double posX, double posY, double posZ)
    {
        this(worldIn);
        this.setPosition(posX, posY, posZ);
    }

    /**
     * Returns whether the entity is in a server world
     */
    public boolean isServerWorld()
    {
        return super.isServerWorld() && !this.hasNoGravity();
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.addObject(10, Byte.valueOf((byte)0));
        this.dataWatcher.addObject(11, DEFAULT_HEAD_ROTATION);
        this.dataWatcher.addObject(12, DEFAULT_BODY_ROTATION);
        this.dataWatcher.addObject(13, DEFAULT_LEFTARM_ROTATION);
        this.dataWatcher.addObject(14, DEFAULT_RIGHTARM_ROTATION);
        this.dataWatcher.addObject(15, DEFAULT_LEFTLEG_ROTATION);
        this.dataWatcher.addObject(16, DEFAULT_RIGHTLEG_ROTATION);
    }

    /**
     * Returns the item that this EntityLiving is holding, if any.
     */
    public ItemStack getHeldItem()
    {
        return this.contents[0];
    }

    /**
     * 0: Tool in Hand; 1-4: Armor
     */
    public ItemStack getEquipmentInSlot(int slotIn)
    {
        return this.contents[slotIn];
    }

    public ItemStack getCurrentArmor(int slotIn)
    {
        return this.contents[slotIn + 1];
    }

    /**
     * Sets the held item, or an armor slot. Slot 0 is held item. Slot 1-4 is armor. Params: Item, slot
     */
    public void setCurrentItemOrArmor(int slotIn, ItemStack stack)
    {
        this.contents[slotIn] = stack;
    }

    /**
     * returns the inventory of this entity (only used in EntityPlayerMP it seems)
     */
    public ItemStack[] getInventory()
    {
        return this.contents;
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

            if (lvt_3_1_ < 0 || lvt_3_1_ >= this.contents.length)
            {
                return false;
            }
        }

        if (itemStackIn != null && EntityLiving.getArmorPosition(itemStackIn) != lvt_3_1_ && (lvt_3_1_ != 4 || !(itemStackIn.getItem() instanceof ItemBlock)))
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
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        super.writeEntityToNBT(tagCompound);
        NBTTagList lvt_2_1_ = new NBTTagList();

        for (int lvt_3_1_ = 0; lvt_3_1_ < this.contents.length; ++lvt_3_1_)
        {
            NBTTagCompound lvt_4_1_ = new NBTTagCompound();

            if (this.contents[lvt_3_1_] != null)
            {
                this.contents[lvt_3_1_].writeToNBT(lvt_4_1_);
            }

            lvt_2_1_.appendTag(lvt_4_1_);
        }

        tagCompound.setTag("Equipment", lvt_2_1_);

        if (this.getAlwaysRenderNameTag() && (this.getCustomNameTag() == null || this.getCustomNameTag().length() == 0))
        {
            tagCompound.setBoolean("CustomNameVisible", this.getAlwaysRenderNameTag());
        }

        tagCompound.setBoolean("Invisible", this.isInvisible());
        tagCompound.setBoolean("Small", this.isSmall());
        tagCompound.setBoolean("ShowArms", this.getShowArms());
        tagCompound.setInteger("DisabledSlots", this.disabledSlots);
        tagCompound.setBoolean("NoGravity", this.hasNoGravity());
        tagCompound.setBoolean("NoBasePlate", this.hasNoBasePlate());

        if (this.hasMarker())
        {
            tagCompound.setBoolean("Marker", this.hasMarker());
        }

        tagCompound.setTag("Pose", this.readPoseFromNBT());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        super.readEntityFromNBT(tagCompund);

        if (tagCompund.hasKey("Equipment", 9))
        {
            NBTTagList lvt_2_1_ = tagCompund.getTagList("Equipment", 10);

            for (int lvt_3_1_ = 0; lvt_3_1_ < this.contents.length; ++lvt_3_1_)
            {
                this.contents[lvt_3_1_] = ItemStack.loadItemStackFromNBT(lvt_2_1_.getCompoundTagAt(lvt_3_1_));
            }
        }

        this.setInvisible(tagCompund.getBoolean("Invisible"));
        this.setSmall(tagCompund.getBoolean("Small"));
        this.setShowArms(tagCompund.getBoolean("ShowArms"));
        this.disabledSlots = tagCompund.getInteger("DisabledSlots");
        this.setNoGravity(tagCompund.getBoolean("NoGravity"));
        this.setNoBasePlate(tagCompund.getBoolean("NoBasePlate"));
        this.setMarker(tagCompund.getBoolean("Marker"));
        this.field_181028_bj = !this.hasMarker();
        this.noClip = this.hasNoGravity();
        NBTTagCompound lvt_2_2_ = tagCompund.getCompoundTag("Pose");
        this.writePoseToNBT(lvt_2_2_);
    }

    /**
     * Saves the pose to an NBTTagCompound.
     */
    private void writePoseToNBT(NBTTagCompound tagCompound)
    {
        NBTTagList lvt_2_1_ = tagCompound.getTagList("Head", 5);

        if (lvt_2_1_.tagCount() > 0)
        {
            this.setHeadRotation(new Rotations(lvt_2_1_));
        }
        else
        {
            this.setHeadRotation(DEFAULT_HEAD_ROTATION);
        }

        NBTTagList lvt_3_1_ = tagCompound.getTagList("Body", 5);

        if (lvt_3_1_.tagCount() > 0)
        {
            this.setBodyRotation(new Rotations(lvt_3_1_));
        }
        else
        {
            this.setBodyRotation(DEFAULT_BODY_ROTATION);
        }

        NBTTagList lvt_4_1_ = tagCompound.getTagList("LeftArm", 5);

        if (lvt_4_1_.tagCount() > 0)
        {
            this.setLeftArmRotation(new Rotations(lvt_4_1_));
        }
        else
        {
            this.setLeftArmRotation(DEFAULT_LEFTARM_ROTATION);
        }

        NBTTagList lvt_5_1_ = tagCompound.getTagList("RightArm", 5);

        if (lvt_5_1_.tagCount() > 0)
        {
            this.setRightArmRotation(new Rotations(lvt_5_1_));
        }
        else
        {
            this.setRightArmRotation(DEFAULT_RIGHTARM_ROTATION);
        }

        NBTTagList lvt_6_1_ = tagCompound.getTagList("LeftLeg", 5);

        if (lvt_6_1_.tagCount() > 0)
        {
            this.setLeftLegRotation(new Rotations(lvt_6_1_));
        }
        else
        {
            this.setLeftLegRotation(DEFAULT_LEFTLEG_ROTATION);
        }

        NBTTagList lvt_7_1_ = tagCompound.getTagList("RightLeg", 5);

        if (lvt_7_1_.tagCount() > 0)
        {
            this.setRightLegRotation(new Rotations(lvt_7_1_));
        }
        else
        {
            this.setRightLegRotation(DEFAULT_RIGHTLEG_ROTATION);
        }
    }

    private NBTTagCompound readPoseFromNBT()
    {
        NBTTagCompound lvt_1_1_ = new NBTTagCompound();

        if (!DEFAULT_HEAD_ROTATION.equals(this.headRotation))
        {
            lvt_1_1_.setTag("Head", this.headRotation.writeToNBT());
        }

        if (!DEFAULT_BODY_ROTATION.equals(this.bodyRotation))
        {
            lvt_1_1_.setTag("Body", this.bodyRotation.writeToNBT());
        }

        if (!DEFAULT_LEFTARM_ROTATION.equals(this.leftArmRotation))
        {
            lvt_1_1_.setTag("LeftArm", this.leftArmRotation.writeToNBT());
        }

        if (!DEFAULT_RIGHTARM_ROTATION.equals(this.rightArmRotation))
        {
            lvt_1_1_.setTag("RightArm", this.rightArmRotation.writeToNBT());
        }

        if (!DEFAULT_LEFTLEG_ROTATION.equals(this.leftLegRotation))
        {
            lvt_1_1_.setTag("LeftLeg", this.leftLegRotation.writeToNBT());
        }

        if (!DEFAULT_RIGHTLEG_ROTATION.equals(this.rightLegRotation))
        {
            lvt_1_1_.setTag("RightLeg", this.rightLegRotation.writeToNBT());
        }

        return lvt_1_1_;
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    public boolean canBePushed()
    {
        return false;
    }

    protected void collideWithEntity(Entity entityIn)
    {
    }

    protected void collideWithNearbyEntities()
    {
        List<Entity> lvt_1_1_ = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox());

        if (lvt_1_1_ != null && !lvt_1_1_.isEmpty())
        {
            for (int lvt_2_1_ = 0; lvt_2_1_ < lvt_1_1_.size(); ++lvt_2_1_)
            {
                Entity lvt_3_1_ = (Entity)lvt_1_1_.get(lvt_2_1_);

                if (lvt_3_1_ instanceof EntityMinecart && ((EntityMinecart)lvt_3_1_).getMinecartType() == EntityMinecart.EnumMinecartType.RIDEABLE && this.getDistanceSqToEntity(lvt_3_1_) <= 0.2D)
                {
                    lvt_3_1_.applyEntityCollision(this);
                }
            }
        }
    }

    /**
     * New version of interactWith that includes vector information on where precisely the player targeted.
     */
    public boolean interactAt(EntityPlayer player, Vec3 targetVec3)
    {
        if (this.hasMarker())
        {
            return false;
        }
        else if (!this.worldObj.isRemote && !player.isSpectator())
        {
            int lvt_3_1_ = 0;
            ItemStack lvt_4_1_ = player.getCurrentEquippedItem();
            boolean lvt_5_1_ = lvt_4_1_ != null;

            if (lvt_5_1_ && lvt_4_1_.getItem() instanceof ItemArmor)
            {
                ItemArmor lvt_6_1_ = (ItemArmor)lvt_4_1_.getItem();

                if (lvt_6_1_.armorType == 3)
                {
                    lvt_3_1_ = 1;
                }
                else if (lvt_6_1_.armorType == 2)
                {
                    lvt_3_1_ = 2;
                }
                else if (lvt_6_1_.armorType == 1)
                {
                    lvt_3_1_ = 3;
                }
                else if (lvt_6_1_.armorType == 0)
                {
                    lvt_3_1_ = 4;
                }
            }

            if (lvt_5_1_ && (lvt_4_1_.getItem() == Items.skull || lvt_4_1_.getItem() == Item.getItemFromBlock(Blocks.pumpkin)))
            {
                lvt_3_1_ = 4;
            }

            double lvt_6_2_ = 0.1D;
            double lvt_8_1_ = 0.9D;
            double lvt_10_1_ = 0.4D;
            double lvt_12_1_ = 1.6D;
            int lvt_14_1_ = 0;
            boolean lvt_15_1_ = this.isSmall();
            double lvt_16_1_ = lvt_15_1_ ? targetVec3.yCoord * 2.0D : targetVec3.yCoord;

            if (lvt_16_1_ >= 0.1D && lvt_16_1_ < 0.1D + (lvt_15_1_ ? 0.8D : 0.45D) && this.contents[1] != null)
            {
                lvt_14_1_ = 1;
            }
            else if (lvt_16_1_ >= 0.9D + (lvt_15_1_ ? 0.3D : 0.0D) && lvt_16_1_ < 0.9D + (lvt_15_1_ ? 1.0D : 0.7D) && this.contents[3] != null)
            {
                lvt_14_1_ = 3;
            }
            else if (lvt_16_1_ >= 0.4D && lvt_16_1_ < 0.4D + (lvt_15_1_ ? 1.0D : 0.8D) && this.contents[2] != null)
            {
                lvt_14_1_ = 2;
            }
            else if (lvt_16_1_ >= 1.6D && this.contents[4] != null)
            {
                lvt_14_1_ = 4;
            }

            boolean lvt_18_1_ = this.contents[lvt_14_1_] != null;

            if ((this.disabledSlots & 1 << lvt_14_1_) != 0 || (this.disabledSlots & 1 << lvt_3_1_) != 0)
            {
                lvt_14_1_ = lvt_3_1_;

                if ((this.disabledSlots & 1 << lvt_3_1_) != 0)
                {
                    if ((this.disabledSlots & 1) != 0)
                    {
                        return true;
                    }

                    lvt_14_1_ = 0;
                }
            }

            if (lvt_5_1_ && lvt_3_1_ == 0 && !this.getShowArms())
            {
                return true;
            }
            else
            {
                if (lvt_5_1_)
                {
                    this.func_175422_a(player, lvt_3_1_);
                }
                else if (lvt_18_1_)
                {
                    this.func_175422_a(player, lvt_14_1_);
                }

                return true;
            }
        }
        else
        {
            return true;
        }
    }

    private void func_175422_a(EntityPlayer p_175422_1_, int p_175422_2_)
    {
        ItemStack lvt_3_1_ = this.contents[p_175422_2_];

        if (lvt_3_1_ == null || (this.disabledSlots & 1 << p_175422_2_ + 8) == 0)
        {
            if (lvt_3_1_ != null || (this.disabledSlots & 1 << p_175422_2_ + 16) == 0)
            {
                int lvt_4_1_ = p_175422_1_.inventory.currentItem;
                ItemStack lvt_5_1_ = p_175422_1_.inventory.getStackInSlot(lvt_4_1_);

                if (p_175422_1_.capabilities.isCreativeMode && (lvt_3_1_ == null || lvt_3_1_.getItem() == Item.getItemFromBlock(Blocks.air)) && lvt_5_1_ != null)
                {
                    ItemStack lvt_6_1_ = lvt_5_1_.copy();
                    lvt_6_1_.stackSize = 1;
                    this.setCurrentItemOrArmor(p_175422_2_, lvt_6_1_);
                }
                else if (lvt_5_1_ != null && lvt_5_1_.stackSize > 1)
                {
                    if (lvt_3_1_ == null)
                    {
                        ItemStack lvt_6_2_ = lvt_5_1_.copy();
                        lvt_6_2_.stackSize = 1;
                        this.setCurrentItemOrArmor(p_175422_2_, lvt_6_2_);
                        --lvt_5_1_.stackSize;
                    }
                }
                else
                {
                    this.setCurrentItemOrArmor(p_175422_2_, lvt_5_1_);
                    p_175422_1_.inventory.setInventorySlotContents(lvt_4_1_, lvt_3_1_);
                }
            }
        }
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.worldObj.isRemote)
        {
            return false;
        }
        else if (DamageSource.outOfWorld.equals(source))
        {
            this.setDead();
            return false;
        }
        else if (!this.isEntityInvulnerable(source) && !this.canInteract && !this.hasMarker())
        {
            if (source.isExplosion())
            {
                this.dropContents();
                this.setDead();
                return false;
            }
            else if (DamageSource.inFire.equals(source))
            {
                if (!this.isBurning())
                {
                    this.setFire(5);
                }
                else
                {
                    this.damageArmorStand(0.15F);
                }

                return false;
            }
            else if (DamageSource.onFire.equals(source) && this.getHealth() > 0.5F)
            {
                this.damageArmorStand(4.0F);
                return false;
            }
            else
            {
                boolean lvt_3_1_ = "arrow".equals(source.getDamageType());
                boolean lvt_4_1_ = "player".equals(source.getDamageType());

                if (!lvt_4_1_ && !lvt_3_1_)
                {
                    return false;
                }
                else
                {
                    if (source.getSourceOfDamage() instanceof EntityArrow)
                    {
                        source.getSourceOfDamage().setDead();
                    }

                    if (source.getEntity() instanceof EntityPlayer && !((EntityPlayer)source.getEntity()).capabilities.allowEdit)
                    {
                        return false;
                    }
                    else if (source.isCreativePlayer())
                    {
                        this.playParticles();
                        this.setDead();
                        return false;
                    }
                    else
                    {
                        long lvt_5_1_ = this.worldObj.getTotalWorldTime();

                        if (lvt_5_1_ - this.punchCooldown > 5L && !lvt_3_1_)
                        {
                            this.punchCooldown = lvt_5_1_;
                        }
                        else
                        {
                            this.dropBlock();
                            this.playParticles();
                            this.setDead();
                        }

                        return false;
                    }
                }
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Checks if the entity is in range to render by using the past in distance and comparing it to its average edge
     * length * 64 * renderDistanceWeight Args: distance
     */
    public boolean isInRangeToRenderDist(double distance)
    {
        double lvt_3_1_ = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0D;

        if (Double.isNaN(lvt_3_1_) || lvt_3_1_ == 0.0D)
        {
            lvt_3_1_ = 4.0D;
        }

        lvt_3_1_ = lvt_3_1_ * 64.0D;
        return distance < lvt_3_1_ * lvt_3_1_;
    }

    private void playParticles()
    {
        if (this.worldObj instanceof WorldServer)
        {
            ((WorldServer)this.worldObj).spawnParticle(EnumParticleTypes.BLOCK_DUST, this.posX, this.posY + (double)this.height / 1.5D, this.posZ, 10, (double)(this.width / 4.0F), (double)(this.height / 4.0F), (double)(this.width / 4.0F), 0.05D, new int[] {Block.getStateId(Blocks.planks.getDefaultState())});
        }
    }

    private void damageArmorStand(float p_175406_1_)
    {
        float lvt_2_1_ = this.getHealth();
        lvt_2_1_ = lvt_2_1_ - p_175406_1_;

        if (lvt_2_1_ <= 0.5F)
        {
            this.dropContents();
            this.setDead();
        }
        else
        {
            this.setHealth(lvt_2_1_);
        }
    }

    private void dropBlock()
    {
        Block.spawnAsEntity(this.worldObj, new BlockPos(this), new ItemStack(Items.armor_stand));
        this.dropContents();
    }

    private void dropContents()
    {
        for (int lvt_1_1_ = 0; lvt_1_1_ < this.contents.length; ++lvt_1_1_)
        {
            if (this.contents[lvt_1_1_] != null && this.contents[lvt_1_1_].stackSize > 0)
            {
                if (this.contents[lvt_1_1_] != null)
                {
                    Block.spawnAsEntity(this.worldObj, (new BlockPos(this)).up(), this.contents[lvt_1_1_]);
                }

                this.contents[lvt_1_1_] = null;
            }
        }
    }

    protected float updateDistance(float p_110146_1_, float p_110146_2_)
    {
        this.prevRenderYawOffset = this.prevRotationYaw;
        this.renderYawOffset = this.rotationYaw;
        return 0.0F;
    }

    public float getEyeHeight()
    {
        return this.isChild() ? this.height * 0.5F : this.height * 0.9F;
    }

    /**
     * Moves the entity based on the specified heading.  Args: strafe, forward
     */
    public void moveEntityWithHeading(float strafe, float forward)
    {
        if (!this.hasNoGravity())
        {
            super.moveEntityWithHeading(strafe, forward);
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();
        Rotations lvt_1_1_ = this.dataWatcher.getWatchableObjectRotations(11);

        if (!this.headRotation.equals(lvt_1_1_))
        {
            this.setHeadRotation(lvt_1_1_);
        }

        Rotations lvt_2_1_ = this.dataWatcher.getWatchableObjectRotations(12);

        if (!this.bodyRotation.equals(lvt_2_1_))
        {
            this.setBodyRotation(lvt_2_1_);
        }

        Rotations lvt_3_1_ = this.dataWatcher.getWatchableObjectRotations(13);

        if (!this.leftArmRotation.equals(lvt_3_1_))
        {
            this.setLeftArmRotation(lvt_3_1_);
        }

        Rotations lvt_4_1_ = this.dataWatcher.getWatchableObjectRotations(14);

        if (!this.rightArmRotation.equals(lvt_4_1_))
        {
            this.setRightArmRotation(lvt_4_1_);
        }

        Rotations lvt_5_1_ = this.dataWatcher.getWatchableObjectRotations(15);

        if (!this.leftLegRotation.equals(lvt_5_1_))
        {
            this.setLeftLegRotation(lvt_5_1_);
        }

        Rotations lvt_6_1_ = this.dataWatcher.getWatchableObjectRotations(16);

        if (!this.rightLegRotation.equals(lvt_6_1_))
        {
            this.setRightLegRotation(lvt_6_1_);
        }

        boolean lvt_7_1_ = this.hasMarker();

        if (!this.field_181028_bj && lvt_7_1_)
        {
            this.func_181550_a(false);
        }
        else
        {
            if (!this.field_181028_bj || lvt_7_1_)
            {
                return;
            }

            this.func_181550_a(true);
        }

        this.field_181028_bj = lvt_7_1_;
    }

    private void func_181550_a(boolean p_181550_1_)
    {
        double lvt_2_1_ = this.posX;
        double lvt_4_1_ = this.posY;
        double lvt_6_1_ = this.posZ;

        if (p_181550_1_)
        {
            this.setSize(0.5F, 1.975F);
        }
        else
        {
            this.setSize(0.0F, 0.0F);
        }

        this.setPosition(lvt_2_1_, lvt_4_1_, lvt_6_1_);
    }

    /**
     * Clears potion metadata values if the entity has no potion effects. Otherwise, updates potion effect color,
     * ambience, and invisibility metadata values
     */
    protected void updatePotionMetadata()
    {
        this.setInvisible(this.canInteract);
    }

    public void setInvisible(boolean invisible)
    {
        this.canInteract = invisible;
        super.setInvisible(invisible);
    }

    /**
     * If Animal, checks if the age timer is negative
     */
    public boolean isChild()
    {
        return this.isSmall();
    }

    /**
     * Called by the /kill command.
     */
    public void onKillCommand()
    {
        this.setDead();
    }

    public boolean isImmuneToExplosions()
    {
        return this.isInvisible();
    }

    private void setSmall(boolean p_175420_1_)
    {
        byte lvt_2_1_ = this.dataWatcher.getWatchableObjectByte(10);

        if (p_175420_1_)
        {
            lvt_2_1_ = (byte)(lvt_2_1_ | 1);
        }
        else
        {
            lvt_2_1_ = (byte)(lvt_2_1_ & -2);
        }

        this.dataWatcher.updateObject(10, Byte.valueOf(lvt_2_1_));
    }

    public boolean isSmall()
    {
        return (this.dataWatcher.getWatchableObjectByte(10) & 1) != 0;
    }

    private void setNoGravity(boolean p_175425_1_)
    {
        byte lvt_2_1_ = this.dataWatcher.getWatchableObjectByte(10);

        if (p_175425_1_)
        {
            lvt_2_1_ = (byte)(lvt_2_1_ | 2);
        }
        else
        {
            lvt_2_1_ = (byte)(lvt_2_1_ & -3);
        }

        this.dataWatcher.updateObject(10, Byte.valueOf(lvt_2_1_));
    }

    public boolean hasNoGravity()
    {
        return (this.dataWatcher.getWatchableObjectByte(10) & 2) != 0;
    }

    private void setShowArms(boolean p_175413_1_)
    {
        byte lvt_2_1_ = this.dataWatcher.getWatchableObjectByte(10);

        if (p_175413_1_)
        {
            lvt_2_1_ = (byte)(lvt_2_1_ | 4);
        }
        else
        {
            lvt_2_1_ = (byte)(lvt_2_1_ & -5);
        }

        this.dataWatcher.updateObject(10, Byte.valueOf(lvt_2_1_));
    }

    public boolean getShowArms()
    {
        return (this.dataWatcher.getWatchableObjectByte(10) & 4) != 0;
    }

    private void setNoBasePlate(boolean p_175426_1_)
    {
        byte lvt_2_1_ = this.dataWatcher.getWatchableObjectByte(10);

        if (p_175426_1_)
        {
            lvt_2_1_ = (byte)(lvt_2_1_ | 8);
        }
        else
        {
            lvt_2_1_ = (byte)(lvt_2_1_ & -9);
        }

        this.dataWatcher.updateObject(10, Byte.valueOf(lvt_2_1_));
    }

    public boolean hasNoBasePlate()
    {
        return (this.dataWatcher.getWatchableObjectByte(10) & 8) != 0;
    }

    /**
     * Marker defines where if true, the size is 0 and will not be rendered or intractable.
     */
    private void setMarker(boolean p_181027_1_)
    {
        byte lvt_2_1_ = this.dataWatcher.getWatchableObjectByte(10);

        if (p_181027_1_)
        {
            lvt_2_1_ = (byte)(lvt_2_1_ | 16);
        }
        else
        {
            lvt_2_1_ = (byte)(lvt_2_1_ & -17);
        }

        this.dataWatcher.updateObject(10, Byte.valueOf(lvt_2_1_));
    }

    /**
     * Gets whether the armor stand has marker enabled. If true, the armor stand's bounding box is set to zero and
     * cannot be interacted with.
     */
    public boolean hasMarker()
    {
        return (this.dataWatcher.getWatchableObjectByte(10) & 16) != 0;
    }

    public void setHeadRotation(Rotations p_175415_1_)
    {
        this.headRotation = p_175415_1_;
        this.dataWatcher.updateObject(11, p_175415_1_);
    }

    public void setBodyRotation(Rotations p_175424_1_)
    {
        this.bodyRotation = p_175424_1_;
        this.dataWatcher.updateObject(12, p_175424_1_);
    }

    public void setLeftArmRotation(Rotations p_175405_1_)
    {
        this.leftArmRotation = p_175405_1_;
        this.dataWatcher.updateObject(13, p_175405_1_);
    }

    public void setRightArmRotation(Rotations p_175428_1_)
    {
        this.rightArmRotation = p_175428_1_;
        this.dataWatcher.updateObject(14, p_175428_1_);
    }

    public void setLeftLegRotation(Rotations p_175417_1_)
    {
        this.leftLegRotation = p_175417_1_;
        this.dataWatcher.updateObject(15, p_175417_1_);
    }

    public void setRightLegRotation(Rotations p_175427_1_)
    {
        this.rightLegRotation = p_175427_1_;
        this.dataWatcher.updateObject(16, p_175427_1_);
    }

    public Rotations getHeadRotation()
    {
        return this.headRotation;
    }

    public Rotations getBodyRotation()
    {
        return this.bodyRotation;
    }

    public Rotations getLeftArmRotation()
    {
        return this.leftArmRotation;
    }

    public Rotations getRightArmRotation()
    {
        return this.rightArmRotation;
    }

    public Rotations getLeftLegRotation()
    {
        return this.leftLegRotation;
    }

    public Rotations getRightLegRotation()
    {
        return this.rightLegRotation;
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return super.canBeCollidedWith() && !this.hasMarker();
    }
}
