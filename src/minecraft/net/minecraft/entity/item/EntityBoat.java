package net.minecraft.entity.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityBoat extends Entity
{
    /** true if no player in boat */
    private boolean isBoatEmpty;
    private double speedMultiplier;
    private int boatPosRotationIncrements;
    private double boatX;
    private double boatY;
    private double boatZ;
    private double boatYaw;
    private double boatPitch;
    private double velocityX;
    private double velocityY;
    private double velocityZ;

    public EntityBoat(World worldIn)
    {
        super(worldIn);
        this.isBoatEmpty = true;
        this.speedMultiplier = 0.07D;
        this.preventEntitySpawning = true;
        this.setSize(1.5F, 0.6F);
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    protected void entityInit()
    {
        this.dataWatcher.addObject(17, new Integer(0));
        this.dataWatcher.addObject(18, new Integer(1));
        this.dataWatcher.addObject(19, new Float(0.0F));
    }

    /**
     * Returns a boundingBox used to collide the entity with other entities and blocks. This enables the entity to be
     * pushable on contact, like boats or minecarts.
     */
    public AxisAlignedBB getCollisionBox(Entity entityIn)
    {
        return entityIn.getEntityBoundingBox();
    }

    /**
     * Returns the collision bounding box for this entity
     */
    public AxisAlignedBB getCollisionBoundingBox()
    {
        return this.getEntityBoundingBox();
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    public boolean canBePushed()
    {
        return true;
    }

    public EntityBoat(World worldIn, double p_i1705_2_, double p_i1705_4_, double p_i1705_6_)
    {
        this(worldIn);
        this.setPosition(p_i1705_2_, p_i1705_4_, p_i1705_6_);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = p_i1705_2_;
        this.prevPosY = p_i1705_4_;
        this.prevPosZ = p_i1705_6_;
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    public double getMountedYOffset()
    {
        return -0.3D;
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
        else if (!this.worldObj.isRemote && !this.isDead)
        {
            if (this.riddenByEntity != null && this.riddenByEntity == source.getEntity() && source instanceof EntityDamageSourceIndirect)
            {
                return false;
            }
            else
            {
                this.setForwardDirection(-this.getForwardDirection());
                this.setTimeSinceHit(10);
                this.setDamageTaken(this.getDamageTaken() + amount * 10.0F);
                this.setBeenAttacked();
                boolean lvt_3_1_ = source.getEntity() instanceof EntityPlayer && ((EntityPlayer)source.getEntity()).capabilities.isCreativeMode;

                if (lvt_3_1_ || this.getDamageTaken() > 40.0F)
                {
                    if (this.riddenByEntity != null)
                    {
                        this.riddenByEntity.mountEntity(this);
                    }

                    if (!lvt_3_1_ && this.worldObj.getGameRules().getBoolean("doEntityDrops"))
                    {
                        this.dropItemWithOffset(Items.boat, 1, 0.0F);
                    }

                    this.setDead();
                }

                return true;
            }
        }
        else
        {
            return true;
        }
    }

    /**
     * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
     */
    public void performHurtAnimation()
    {
        this.setForwardDirection(-this.getForwardDirection());
        this.setTimeSinceHit(10);
        this.setDamageTaken(this.getDamageTaken() * 11.0F);
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }

    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean p_180426_10_)
    {
        if (p_180426_10_ && this.riddenByEntity != null)
        {
            this.prevPosX = this.posX = x;
            this.prevPosY = this.posY = y;
            this.prevPosZ = this.posZ = z;
            this.rotationYaw = yaw;
            this.rotationPitch = pitch;
            this.boatPosRotationIncrements = 0;
            this.setPosition(x, y, z);
            this.motionX = this.velocityX = 0.0D;
            this.motionY = this.velocityY = 0.0D;
            this.motionZ = this.velocityZ = 0.0D;
        }
        else
        {
            if (this.isBoatEmpty)
            {
                this.boatPosRotationIncrements = posRotationIncrements + 5;
            }
            else
            {
                double lvt_11_1_ = x - this.posX;
                double lvt_13_1_ = y - this.posY;
                double lvt_15_1_ = z - this.posZ;
                double lvt_17_1_ = lvt_11_1_ * lvt_11_1_ + lvt_13_1_ * lvt_13_1_ + lvt_15_1_ * lvt_15_1_;

                if (lvt_17_1_ <= 1.0D)
                {
                    return;
                }

                this.boatPosRotationIncrements = 3;
            }

            this.boatX = x;
            this.boatY = y;
            this.boatZ = z;
            this.boatYaw = (double)yaw;
            this.boatPitch = (double)pitch;
            this.motionX = this.velocityX;
            this.motionY = this.velocityY;
            this.motionZ = this.velocityZ;
        }
    }

    /**
     * Sets the velocity to the args. Args: x, y, z
     */
    public void setVelocity(double x, double y, double z)
    {
        this.velocityX = this.motionX = x;
        this.velocityY = this.motionY = y;
        this.velocityZ = this.motionZ = z;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (this.getTimeSinceHit() > 0)
        {
            this.setTimeSinceHit(this.getTimeSinceHit() - 1);
        }

        if (this.getDamageTaken() > 0.0F)
        {
            this.setDamageTaken(this.getDamageTaken() - 1.0F);
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        int lvt_1_1_ = 5;
        double lvt_2_1_ = 0.0D;

        for (int lvt_4_1_ = 0; lvt_4_1_ < lvt_1_1_; ++lvt_4_1_)
        {
            double lvt_5_1_ = this.getEntityBoundingBox().minY + (this.getEntityBoundingBox().maxY - this.getEntityBoundingBox().minY) * (double)(lvt_4_1_ + 0) / (double)lvt_1_1_ - 0.125D;
            double lvt_7_1_ = this.getEntityBoundingBox().minY + (this.getEntityBoundingBox().maxY - this.getEntityBoundingBox().minY) * (double)(lvt_4_1_ + 1) / (double)lvt_1_1_ - 0.125D;
            AxisAlignedBB lvt_9_1_ = new AxisAlignedBB(this.getEntityBoundingBox().minX, lvt_5_1_, this.getEntityBoundingBox().minZ, this.getEntityBoundingBox().maxX, lvt_7_1_, this.getEntityBoundingBox().maxZ);

            if (this.worldObj.isAABBInMaterial(lvt_9_1_, Material.water))
            {
                lvt_2_1_ += 1.0D / (double)lvt_1_1_;
            }
        }

        double lvt_4_2_ = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

        if (lvt_4_2_ > 0.2975D)
        {
            double lvt_6_1_ = Math.cos((double)this.rotationYaw * Math.PI / 180.0D);
            double lvt_8_1_ = Math.sin((double)this.rotationYaw * Math.PI / 180.0D);

            for (int lvt_10_1_ = 0; (double)lvt_10_1_ < 1.0D + lvt_4_2_ * 60.0D; ++lvt_10_1_)
            {
                double lvt_11_1_ = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
                double lvt_13_1_ = (double)(this.rand.nextInt(2) * 2 - 1) * 0.7D;

                if (this.rand.nextBoolean())
                {
                    double lvt_15_1_ = this.posX - lvt_6_1_ * lvt_11_1_ * 0.8D + lvt_8_1_ * lvt_13_1_;
                    double lvt_17_1_ = this.posZ - lvt_8_1_ * lvt_11_1_ * 0.8D - lvt_6_1_ * lvt_13_1_;
                    this.worldObj.spawnParticle(EnumParticleTypes.WATER_SPLASH, lvt_15_1_, this.posY - 0.125D, lvt_17_1_, this.motionX, this.motionY, this.motionZ, new int[0]);
                }
                else
                {
                    double lvt_15_2_ = this.posX + lvt_6_1_ + lvt_8_1_ * lvt_11_1_ * 0.7D;
                    double lvt_17_2_ = this.posZ + lvt_8_1_ - lvt_6_1_ * lvt_11_1_ * 0.7D;
                    this.worldObj.spawnParticle(EnumParticleTypes.WATER_SPLASH, lvt_15_2_, this.posY - 0.125D, lvt_17_2_, this.motionX, this.motionY, this.motionZ, new int[0]);
                }
            }
        }

        if (this.worldObj.isRemote && this.isBoatEmpty)
        {
            if (this.boatPosRotationIncrements > 0)
            {
                double lvt_6_2_ = this.posX + (this.boatX - this.posX) / (double)this.boatPosRotationIncrements;
                double lvt_8_2_ = this.posY + (this.boatY - this.posY) / (double)this.boatPosRotationIncrements;
                double lvt_10_2_ = this.posZ + (this.boatZ - this.posZ) / (double)this.boatPosRotationIncrements;
                double lvt_12_1_ = MathHelper.wrapAngleTo180_double(this.boatYaw - (double)this.rotationYaw);
                this.rotationYaw = (float)((double)this.rotationYaw + lvt_12_1_ / (double)this.boatPosRotationIncrements);
                this.rotationPitch = (float)((double)this.rotationPitch + (this.boatPitch - (double)this.rotationPitch) / (double)this.boatPosRotationIncrements);
                --this.boatPosRotationIncrements;
                this.setPosition(lvt_6_2_, lvt_8_2_, lvt_10_2_);
                this.setRotation(this.rotationYaw, this.rotationPitch);
            }
            else
            {
                double lvt_6_3_ = this.posX + this.motionX;
                double lvt_8_3_ = this.posY + this.motionY;
                double lvt_10_3_ = this.posZ + this.motionZ;
                this.setPosition(lvt_6_3_, lvt_8_3_, lvt_10_3_);

                if (this.onGround)
                {
                    this.motionX *= 0.5D;
                    this.motionY *= 0.5D;
                    this.motionZ *= 0.5D;
                }

                this.motionX *= 0.9900000095367432D;
                this.motionY *= 0.949999988079071D;
                this.motionZ *= 0.9900000095367432D;
            }
        }
        else
        {
            if (lvt_2_1_ < 1.0D)
            {
                double lvt_6_4_ = lvt_2_1_ * 2.0D - 1.0D;
                this.motionY += 0.03999999910593033D * lvt_6_4_;
            }
            else
            {
                if (this.motionY < 0.0D)
                {
                    this.motionY /= 2.0D;
                }

                this.motionY += 0.007000000216066837D;
            }

            if (this.riddenByEntity instanceof EntityLivingBase)
            {
                EntityLivingBase lvt_6_5_ = (EntityLivingBase)this.riddenByEntity;
                float lvt_7_2_ = this.riddenByEntity.rotationYaw + -lvt_6_5_.moveStrafing * 90.0F;
                this.motionX += -Math.sin((double)(lvt_7_2_ * (float)Math.PI / 180.0F)) * this.speedMultiplier * (double)lvt_6_5_.moveForward * 0.05000000074505806D;
                this.motionZ += Math.cos((double)(lvt_7_2_ * (float)Math.PI / 180.0F)) * this.speedMultiplier * (double)lvt_6_5_.moveForward * 0.05000000074505806D;
            }

            double lvt_6_6_ = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

            if (lvt_6_6_ > 0.35D)
            {
                double lvt_8_4_ = 0.35D / lvt_6_6_;
                this.motionX *= lvt_8_4_;
                this.motionZ *= lvt_8_4_;
                lvt_6_6_ = 0.35D;
            }

            if (lvt_6_6_ > lvt_4_2_ && this.speedMultiplier < 0.35D)
            {
                this.speedMultiplier += (0.35D - this.speedMultiplier) / 35.0D;

                if (this.speedMultiplier > 0.35D)
                {
                    this.speedMultiplier = 0.35D;
                }
            }
            else
            {
                this.speedMultiplier -= (this.speedMultiplier - 0.07D) / 35.0D;

                if (this.speedMultiplier < 0.07D)
                {
                    this.speedMultiplier = 0.07D;
                }
            }

            for (int lvt_8_5_ = 0; lvt_8_5_ < 4; ++lvt_8_5_)
            {
                int lvt_9_2_ = MathHelper.floor_double(this.posX + ((double)(lvt_8_5_ % 2) - 0.5D) * 0.8D);
                int lvt_10_4_ = MathHelper.floor_double(this.posZ + ((double)(lvt_8_5_ / 2) - 0.5D) * 0.8D);

                for (int lvt_11_2_ = 0; lvt_11_2_ < 2; ++lvt_11_2_)
                {
                    int lvt_12_2_ = MathHelper.floor_double(this.posY) + lvt_11_2_;
                    BlockPos lvt_13_2_ = new BlockPos(lvt_9_2_, lvt_12_2_, lvt_10_4_);
                    Block lvt_14_1_ = this.worldObj.getBlockState(lvt_13_2_).getBlock();

                    if (lvt_14_1_ == Blocks.snow_layer)
                    {
                        this.worldObj.setBlockToAir(lvt_13_2_);
                        this.isCollidedHorizontally = false;
                    }
                    else if (lvt_14_1_ == Blocks.waterlily)
                    {
                        this.worldObj.destroyBlock(lvt_13_2_, true);
                        this.isCollidedHorizontally = false;
                    }
                }
            }

            if (this.onGround)
            {
                this.motionX *= 0.5D;
                this.motionY *= 0.5D;
                this.motionZ *= 0.5D;
            }

            this.moveEntity(this.motionX, this.motionY, this.motionZ);

            if (this.isCollidedHorizontally && lvt_4_2_ > 0.2975D)
            {
                if (!this.worldObj.isRemote && !this.isDead)
                {
                    this.setDead();

                    if (this.worldObj.getGameRules().getBoolean("doEntityDrops"))
                    {
                        for (int lvt_8_6_ = 0; lvt_8_6_ < 3; ++lvt_8_6_)
                        {
                            this.dropItemWithOffset(Item.getItemFromBlock(Blocks.planks), 1, 0.0F);
                        }

                        for (int lvt_8_7_ = 0; lvt_8_7_ < 2; ++lvt_8_7_)
                        {
                            this.dropItemWithOffset(Items.stick, 1, 0.0F);
                        }
                    }
                }
            }
            else
            {
                this.motionX *= 0.9900000095367432D;
                this.motionY *= 0.949999988079071D;
                this.motionZ *= 0.9900000095367432D;
            }

            this.rotationPitch = 0.0F;
            double lvt_8_8_ = (double)this.rotationYaw;
            double lvt_10_5_ = this.prevPosX - this.posX;
            double lvt_12_3_ = this.prevPosZ - this.posZ;

            if (lvt_10_5_ * lvt_10_5_ + lvt_12_3_ * lvt_12_3_ > 0.001D)
            {
                lvt_8_8_ = (double)((float)(MathHelper.atan2(lvt_12_3_, lvt_10_5_) * 180.0D / Math.PI));
            }

            double lvt_14_2_ = MathHelper.wrapAngleTo180_double(lvt_8_8_ - (double)this.rotationYaw);

            if (lvt_14_2_ > 20.0D)
            {
                lvt_14_2_ = 20.0D;
            }

            if (lvt_14_2_ < -20.0D)
            {
                lvt_14_2_ = -20.0D;
            }

            this.rotationYaw = (float)((double)this.rotationYaw + lvt_14_2_);
            this.setRotation(this.rotationYaw, this.rotationPitch);

            if (!this.worldObj.isRemote)
            {
                List<Entity> lvt_16_1_ = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));

                if (lvt_16_1_ != null && !lvt_16_1_.isEmpty())
                {
                    for (int lvt_17_3_ = 0; lvt_17_3_ < lvt_16_1_.size(); ++lvt_17_3_)
                    {
                        Entity lvt_18_1_ = (Entity)lvt_16_1_.get(lvt_17_3_);

                        if (lvt_18_1_ != this.riddenByEntity && lvt_18_1_.canBePushed() && lvt_18_1_ instanceof EntityBoat)
                        {
                            lvt_18_1_.applyEntityCollision(this);
                        }
                    }
                }

                if (this.riddenByEntity != null && this.riddenByEntity.isDead)
                {
                    this.riddenByEntity = null;
                }
            }
        }
    }

    public void updateRiderPosition()
    {
        if (this.riddenByEntity != null)
        {
            double lvt_1_1_ = Math.cos((double)this.rotationYaw * Math.PI / 180.0D) * 0.4D;
            double lvt_3_1_ = Math.sin((double)this.rotationYaw * Math.PI / 180.0D) * 0.4D;
            this.riddenByEntity.setPosition(this.posX + lvt_1_1_, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ + lvt_3_1_);
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound tagCompound)
    {
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound tagCompund)
    {
    }

    /**
     * First layer of player interaction
     */
    public boolean interactFirst(EntityPlayer playerIn)
    {
        if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != playerIn)
        {
            return true;
        }
        else
        {
            if (!this.worldObj.isRemote)
            {
                playerIn.mountEntity(this);
            }

            return true;
        }
    }

    protected void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos)
    {
        if (onGroundIn)
        {
            if (this.fallDistance > 3.0F)
            {
                this.fall(this.fallDistance, 1.0F);

                if (!this.worldObj.isRemote && !this.isDead)
                {
                    this.setDead();

                    if (this.worldObj.getGameRules().getBoolean("doEntityDrops"))
                    {
                        for (int lvt_6_1_ = 0; lvt_6_1_ < 3; ++lvt_6_1_)
                        {
                            this.dropItemWithOffset(Item.getItemFromBlock(Blocks.planks), 1, 0.0F);
                        }

                        for (int lvt_6_2_ = 0; lvt_6_2_ < 2; ++lvt_6_2_)
                        {
                            this.dropItemWithOffset(Items.stick, 1, 0.0F);
                        }
                    }
                }

                this.fallDistance = 0.0F;
            }
        }
        else if (this.worldObj.getBlockState((new BlockPos(this)).down()).getBlock().getMaterial() != Material.water && y < 0.0D)
        {
            this.fallDistance = (float)((double)this.fallDistance - y);
        }
    }

    /**
     * Sets the damage taken from the last hit.
     */
    public void setDamageTaken(float p_70266_1_)
    {
        this.dataWatcher.updateObject(19, Float.valueOf(p_70266_1_));
    }

    /**
     * Gets the damage taken from the last hit.
     */
    public float getDamageTaken()
    {
        return this.dataWatcher.getWatchableObjectFloat(19);
    }

    /**
     * Sets the time to count down from since the last time entity was hit.
     */
    public void setTimeSinceHit(int p_70265_1_)
    {
        this.dataWatcher.updateObject(17, Integer.valueOf(p_70265_1_));
    }

    /**
     * Gets the time since the last hit.
     */
    public int getTimeSinceHit()
    {
        return this.dataWatcher.getWatchableObjectInt(17);
    }

    /**
     * Sets the forward direction of the entity.
     */
    public void setForwardDirection(int p_70269_1_)
    {
        this.dataWatcher.updateObject(18, Integer.valueOf(p_70269_1_));
    }

    /**
     * Gets the forward direction of the entity.
     */
    public int getForwardDirection()
    {
        return this.dataWatcher.getWatchableObjectInt(18);
    }

    /**
     * true if no player in boat
     */
    public void setIsBoatEmpty(boolean p_70270_1_)
    {
        this.isBoatEmpty = p_70270_1_;
    }
}
