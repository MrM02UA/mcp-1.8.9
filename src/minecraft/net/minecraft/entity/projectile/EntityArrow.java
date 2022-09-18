package net.minecraft.entity.projectile;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityArrow extends Entity implements IProjectile
{
    private int xTile = -1;
    private int yTile = -1;
    private int zTile = -1;
    private Block inTile;
    private int inData;
    private boolean inGround;

    /** 1 if the player can pick up the arrow */
    public int canBePickedUp;

    /** Seems to be some sort of timer for animating an arrow. */
    public int arrowShake;

    /** The owner of this arrow. */
    public Entity shootingEntity;
    private int ticksInGround;
    private int ticksInAir;
    private double damage = 2.0D;

    /** The amount of knockback an arrow applies when it hits a mob. */
    private int knockbackStrength;

    public EntityArrow(World worldIn)
    {
        super(worldIn);
        this.renderDistanceWeight = 10.0D;
        this.setSize(0.5F, 0.5F);
    }

    public EntityArrow(World worldIn, double x, double y, double z)
    {
        super(worldIn);
        this.renderDistanceWeight = 10.0D;
        this.setSize(0.5F, 0.5F);
        this.setPosition(x, y, z);
    }

    public EntityArrow(World worldIn, EntityLivingBase shooter, EntityLivingBase target, float velocity, float innacuracy)
    {
        super(worldIn);
        this.renderDistanceWeight = 10.0D;
        this.shootingEntity = shooter;

        if (shooter instanceof EntityPlayer)
        {
            this.canBePickedUp = 1;
        }

        this.posY = shooter.posY + (double)shooter.getEyeHeight() - 0.10000000149011612D;
        double lvt_6_1_ = target.posX - shooter.posX;
        double lvt_8_1_ = target.getEntityBoundingBox().minY + (double)(target.height / 3.0F) - this.posY;
        double lvt_10_1_ = target.posZ - shooter.posZ;
        double lvt_12_1_ = (double)MathHelper.sqrt_double(lvt_6_1_ * lvt_6_1_ + lvt_10_1_ * lvt_10_1_);

        if (lvt_12_1_ >= 1.0E-7D)
        {
            float lvt_14_1_ = (float)(MathHelper.atan2(lvt_10_1_, lvt_6_1_) * 180.0D / Math.PI) - 90.0F;
            float lvt_15_1_ = (float)(-(MathHelper.atan2(lvt_8_1_, lvt_12_1_) * 180.0D / Math.PI));
            double lvt_16_1_ = lvt_6_1_ / lvt_12_1_;
            double lvt_18_1_ = lvt_10_1_ / lvt_12_1_;
            this.setLocationAndAngles(shooter.posX + lvt_16_1_, this.posY, shooter.posZ + lvt_18_1_, lvt_14_1_, lvt_15_1_);
            float lvt_20_1_ = (float)(lvt_12_1_ * 0.20000000298023224D);
            this.setThrowableHeading(lvt_6_1_, lvt_8_1_ + (double)lvt_20_1_, lvt_10_1_, velocity, innacuracy);
        }
    }

    public EntityArrow(World worldIn, EntityLivingBase shooter, float velocity)
    {
        super(worldIn);
        this.renderDistanceWeight = 10.0D;
        this.shootingEntity = shooter;

        if (shooter instanceof EntityPlayer)
        {
            this.canBePickedUp = 1;
        }

        this.setSize(0.5F, 0.5F);
        this.setLocationAndAngles(shooter.posX, shooter.posY + (double)shooter.getEyeHeight(), shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
        this.posX -= (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        this.posY -= 0.10000000149011612D;
        this.posZ -= (double)(MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.motionX = (double)(-MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
        this.motionZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
        this.motionY = (double)(-MathHelper.sin(this.rotationPitch / 180.0F * (float)Math.PI));
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, velocity * 1.5F, 1.0F);
    }

    protected void entityInit()
    {
        this.dataWatcher.addObject(16, Byte.valueOf((byte)0));
    }

    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
     */
    public void setThrowableHeading(double x, double y, double z, float velocity, float inaccuracy)
    {
        float lvt_9_1_ = MathHelper.sqrt_double(x * x + y * y + z * z);
        x = x / (double)lvt_9_1_;
        y = y / (double)lvt_9_1_;
        z = z / (double)lvt_9_1_;
        x = x + this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)inaccuracy;
        y = y + this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)inaccuracy;
        z = z + this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)inaccuracy;
        x = x * (double)velocity;
        y = y * (double)velocity;
        z = z * (double)velocity;
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
        float lvt_10_1_ = MathHelper.sqrt_double(x * x + z * z);
        this.prevRotationYaw = this.rotationYaw = (float)(MathHelper.atan2(x, z) * 180.0D / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float)(MathHelper.atan2(y, (double)lvt_10_1_) * 180.0D / Math.PI);
        this.ticksInGround = 0;
    }

    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean p_180426_10_)
    {
        this.setPosition(x, y, z);
        this.setRotation(yaw, pitch);
    }

    /**
     * Sets the velocity to the args. Args: x, y, z
     */
    public void setVelocity(double x, double y, double z)
    {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float lvt_7_1_ = MathHelper.sqrt_double(x * x + z * z);
            this.prevRotationYaw = this.rotationYaw = (float)(MathHelper.atan2(x, z) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(MathHelper.atan2(y, (double)lvt_7_1_) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationYaw = this.rotationYaw;
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            this.ticksInGround = 0;
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float lvt_1_1_ = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.prevRotationYaw = this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)lvt_1_1_) * 180.0D / Math.PI);
        }

        BlockPos lvt_1_2_ = new BlockPos(this.xTile, this.yTile, this.zTile);
        IBlockState lvt_2_1_ = this.worldObj.getBlockState(lvt_1_2_);
        Block lvt_3_1_ = lvt_2_1_.getBlock();

        if (lvt_3_1_.getMaterial() != Material.air)
        {
            lvt_3_1_.setBlockBoundsBasedOnState(this.worldObj, lvt_1_2_);
            AxisAlignedBB lvt_4_1_ = lvt_3_1_.getCollisionBoundingBox(this.worldObj, lvt_1_2_, lvt_2_1_);

            if (lvt_4_1_ != null && lvt_4_1_.isVecInside(new Vec3(this.posX, this.posY, this.posZ)))
            {
                this.inGround = true;
            }
        }

        if (this.arrowShake > 0)
        {
            --this.arrowShake;
        }

        if (this.inGround)
        {
            int lvt_4_2_ = lvt_3_1_.getMetaFromState(lvt_2_1_);

            if (lvt_3_1_ == this.inTile && lvt_4_2_ == this.inData)
            {
                ++this.ticksInGround;

                if (this.ticksInGround >= 1200)
                {
                    this.setDead();
                }
            }
            else
            {
                this.inGround = false;
                this.motionX *= (double)(this.rand.nextFloat() * 0.2F);
                this.motionY *= (double)(this.rand.nextFloat() * 0.2F);
                this.motionZ *= (double)(this.rand.nextFloat() * 0.2F);
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            }
        }
        else
        {
            ++this.ticksInAir;
            Vec3 lvt_4_3_ = new Vec3(this.posX, this.posY, this.posZ);
            Vec3 lvt_5_1_ = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            MovingObjectPosition lvt_6_1_ = this.worldObj.rayTraceBlocks(lvt_4_3_, lvt_5_1_, false, true, false);
            lvt_4_3_ = new Vec3(this.posX, this.posY, this.posZ);
            lvt_5_1_ = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

            if (lvt_6_1_ != null)
            {
                lvt_5_1_ = new Vec3(lvt_6_1_.hitVec.xCoord, lvt_6_1_.hitVec.yCoord, lvt_6_1_.hitVec.zCoord);
            }

            Entity lvt_7_1_ = null;
            List<Entity> lvt_8_1_ = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
            double lvt_9_1_ = 0.0D;

            for (int lvt_11_1_ = 0; lvt_11_1_ < lvt_8_1_.size(); ++lvt_11_1_)
            {
                Entity lvt_12_1_ = (Entity)lvt_8_1_.get(lvt_11_1_);

                if (lvt_12_1_.canBeCollidedWith() && (lvt_12_1_ != this.shootingEntity || this.ticksInAir >= 5))
                {
                    float lvt_13_1_ = 0.3F;
                    AxisAlignedBB lvt_14_1_ = lvt_12_1_.getEntityBoundingBox().expand((double)lvt_13_1_, (double)lvt_13_1_, (double)lvt_13_1_);
                    MovingObjectPosition lvt_15_1_ = lvt_14_1_.calculateIntercept(lvt_4_3_, lvt_5_1_);

                    if (lvt_15_1_ != null)
                    {
                        double lvt_16_1_ = lvt_4_3_.squareDistanceTo(lvt_15_1_.hitVec);

                        if (lvt_16_1_ < lvt_9_1_ || lvt_9_1_ == 0.0D)
                        {
                            lvt_7_1_ = lvt_12_1_;
                            lvt_9_1_ = lvt_16_1_;
                        }
                    }
                }
            }

            if (lvt_7_1_ != null)
            {
                lvt_6_1_ = new MovingObjectPosition(lvt_7_1_);
            }

            if (lvt_6_1_ != null && lvt_6_1_.entityHit != null && lvt_6_1_.entityHit instanceof EntityPlayer)
            {
                EntityPlayer lvt_11_2_ = (EntityPlayer)lvt_6_1_.entityHit;

                if (lvt_11_2_.capabilities.disableDamage || this.shootingEntity instanceof EntityPlayer && !((EntityPlayer)this.shootingEntity).canAttackPlayer(lvt_11_2_))
                {
                    lvt_6_1_ = null;
                }
            }

            if (lvt_6_1_ != null)
            {
                if (lvt_6_1_.entityHit != null)
                {
                    float lvt_11_3_ = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                    int lvt_12_2_ = MathHelper.ceiling_double_int((double)lvt_11_3_ * this.damage);

                    if (this.getIsCritical())
                    {
                        lvt_12_2_ += this.rand.nextInt(lvt_12_2_ / 2 + 2);
                    }

                    DamageSource lvt_13_2_;

                    if (this.shootingEntity == null)
                    {
                        lvt_13_2_ = DamageSource.causeArrowDamage(this, this);
                    }
                    else
                    {
                        lvt_13_2_ = DamageSource.causeArrowDamage(this, this.shootingEntity);
                    }

                    if (this.isBurning() && !(lvt_6_1_.entityHit instanceof EntityEnderman))
                    {
                        lvt_6_1_.entityHit.setFire(5);
                    }

                    if (lvt_6_1_.entityHit.attackEntityFrom(lvt_13_2_, (float)lvt_12_2_))
                    {
                        if (lvt_6_1_.entityHit instanceof EntityLivingBase)
                        {
                            EntityLivingBase lvt_14_2_ = (EntityLivingBase)lvt_6_1_.entityHit;

                            if (!this.worldObj.isRemote)
                            {
                                lvt_14_2_.setArrowCountInEntity(lvt_14_2_.getArrowCountInEntity() + 1);
                            }

                            if (this.knockbackStrength > 0)
                            {
                                float lvt_15_2_ = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);

                                if (lvt_15_2_ > 0.0F)
                                {
                                    lvt_6_1_.entityHit.addVelocity(this.motionX * (double)this.knockbackStrength * 0.6000000238418579D / (double)lvt_15_2_, 0.1D, this.motionZ * (double)this.knockbackStrength * 0.6000000238418579D / (double)lvt_15_2_);
                                }
                            }

                            if (this.shootingEntity instanceof EntityLivingBase)
                            {
                                EnchantmentHelper.applyThornEnchantments(lvt_14_2_, this.shootingEntity);
                                EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase)this.shootingEntity, lvt_14_2_);
                            }

                            if (this.shootingEntity != null && lvt_6_1_.entityHit != this.shootingEntity && lvt_6_1_.entityHit instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP)
                            {
                                ((EntityPlayerMP)this.shootingEntity).playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(6, 0.0F));
                            }
                        }

                        this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));

                        if (!(lvt_6_1_.entityHit instanceof EntityEnderman))
                        {
                            this.setDead();
                        }
                    }
                    else
                    {
                        this.motionX *= -0.10000000149011612D;
                        this.motionY *= -0.10000000149011612D;
                        this.motionZ *= -0.10000000149011612D;
                        this.rotationYaw += 180.0F;
                        this.prevRotationYaw += 180.0F;
                        this.ticksInAir = 0;
                    }
                }
                else
                {
                    BlockPos lvt_11_4_ = lvt_6_1_.getBlockPos();
                    this.xTile = lvt_11_4_.getX();
                    this.yTile = lvt_11_4_.getY();
                    this.zTile = lvt_11_4_.getZ();
                    IBlockState lvt_12_3_ = this.worldObj.getBlockState(lvt_11_4_);
                    this.inTile = lvt_12_3_.getBlock();
                    this.inData = this.inTile.getMetaFromState(lvt_12_3_);
                    this.motionX = (double)((float)(lvt_6_1_.hitVec.xCoord - this.posX));
                    this.motionY = (double)((float)(lvt_6_1_.hitVec.yCoord - this.posY));
                    this.motionZ = (double)((float)(lvt_6_1_.hitVec.zCoord - this.posZ));
                    float lvt_13_4_ = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                    this.posX -= this.motionX / (double)lvt_13_4_ * 0.05000000074505806D;
                    this.posY -= this.motionY / (double)lvt_13_4_ * 0.05000000074505806D;
                    this.posZ -= this.motionZ / (double)lvt_13_4_ * 0.05000000074505806D;
                    this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                    this.inGround = true;
                    this.arrowShake = 7;
                    this.setIsCritical(false);

                    if (this.inTile.getMaterial() != Material.air)
                    {
                        this.inTile.onEntityCollidedWithBlock(this.worldObj, lvt_11_4_, lvt_12_3_, this);
                    }
                }
            }

            if (this.getIsCritical())
            {
                for (int lvt_11_5_ = 0; lvt_11_5_ < 4; ++lvt_11_5_)
                {
                    this.worldObj.spawnParticle(EnumParticleTypes.CRIT, this.posX + this.motionX * (double)lvt_11_5_ / 4.0D, this.posY + this.motionY * (double)lvt_11_5_ / 4.0D, this.posZ + this.motionZ * (double)lvt_11_5_ / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ, new int[0]);
                }
            }

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            float lvt_11_6_ = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

            for (this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)lvt_11_6_) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
            {
                ;
            }

            while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
            {
                this.prevRotationPitch += 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw < -180.0F)
            {
                this.prevRotationYaw -= 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
            {
                this.prevRotationYaw += 360.0F;
            }

            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
            float lvt_12_4_ = 0.99F;
            float lvt_13_5_ = 0.05F;

            if (this.isInWater())
            {
                for (int lvt_14_3_ = 0; lvt_14_3_ < 4; ++lvt_14_3_)
                {
                    float lvt_15_3_ = 0.25F;
                    this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * (double)lvt_15_3_, this.posY - this.motionY * (double)lvt_15_3_, this.posZ - this.motionZ * (double)lvt_15_3_, this.motionX, this.motionY, this.motionZ, new int[0]);
                }

                lvt_12_4_ = 0.6F;
            }

            if (this.isWet())
            {
                this.extinguish();
            }

            this.motionX *= (double)lvt_12_4_;
            this.motionY *= (double)lvt_12_4_;
            this.motionZ *= (double)lvt_12_4_;
            this.motionY -= (double)lvt_13_5_;
            this.setPosition(this.posX, this.posY, this.posZ);
            this.doBlockCollisions();
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        tagCompound.setShort("xTile", (short)this.xTile);
        tagCompound.setShort("yTile", (short)this.yTile);
        tagCompound.setShort("zTile", (short)this.zTile);
        tagCompound.setShort("life", (short)this.ticksInGround);
        ResourceLocation lvt_2_1_ = (ResourceLocation)Block.blockRegistry.getNameForObject(this.inTile);
        tagCompound.setString("inTile", lvt_2_1_ == null ? "" : lvt_2_1_.toString());
        tagCompound.setByte("inData", (byte)this.inData);
        tagCompound.setByte("shake", (byte)this.arrowShake);
        tagCompound.setByte("inGround", (byte)(this.inGround ? 1 : 0));
        tagCompound.setByte("pickup", (byte)this.canBePickedUp);
        tagCompound.setDouble("damage", this.damage);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        this.xTile = tagCompund.getShort("xTile");
        this.yTile = tagCompund.getShort("yTile");
        this.zTile = tagCompund.getShort("zTile");
        this.ticksInGround = tagCompund.getShort("life");

        if (tagCompund.hasKey("inTile", 8))
        {
            this.inTile = Block.getBlockFromName(tagCompund.getString("inTile"));
        }
        else
        {
            this.inTile = Block.getBlockById(tagCompund.getByte("inTile") & 255);
        }

        this.inData = tagCompund.getByte("inData") & 255;
        this.arrowShake = tagCompund.getByte("shake") & 255;
        this.inGround = tagCompund.getByte("inGround") == 1;

        if (tagCompund.hasKey("damage", 99))
        {
            this.damage = tagCompund.getDouble("damage");
        }

        if (tagCompund.hasKey("pickup", 99))
        {
            this.canBePickedUp = tagCompund.getByte("pickup");
        }
        else if (tagCompund.hasKey("player", 99))
        {
            this.canBePickedUp = tagCompund.getBoolean("player") ? 1 : 0;
        }
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    public void onCollideWithPlayer(EntityPlayer entityIn)
    {
        if (!this.worldObj.isRemote && this.inGround && this.arrowShake <= 0)
        {
            boolean lvt_2_1_ = this.canBePickedUp == 1 || this.canBePickedUp == 2 && entityIn.capabilities.isCreativeMode;

            if (this.canBePickedUp == 1 && !entityIn.inventory.addItemStackToInventory(new ItemStack(Items.arrow, 1)))
            {
                lvt_2_1_ = false;
            }

            if (lvt_2_1_)
            {
                this.playSound("random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                entityIn.onItemPickup(this, 1);
                this.setDead();
            }
        }
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    public void setDamage(double damageIn)
    {
        this.damage = damageIn;
    }

    public double getDamage()
    {
        return this.damage;
    }

    /**
     * Sets the amount of knockback the arrow applies when it hits a mob.
     */
    public void setKnockbackStrength(int knockbackStrengthIn)
    {
        this.knockbackStrength = knockbackStrengthIn;
    }

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    public boolean canAttackWithItem()
    {
        return false;
    }

    public float getEyeHeight()
    {
        return 0.0F;
    }

    /**
     * Whether the arrow has a stream of critical hit particles flying behind it.
     */
    public void setIsCritical(boolean critical)
    {
        byte lvt_2_1_ = this.dataWatcher.getWatchableObjectByte(16);

        if (critical)
        {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte)(lvt_2_1_ | 1)));
        }
        else
        {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte)(lvt_2_1_ & -2)));
        }
    }

    /**
     * Whether the arrow has a stream of critical hit particles flying behind it.
     */
    public boolean getIsCritical()
    {
        byte lvt_1_1_ = this.dataWatcher.getWatchableObjectByte(16);
        return (lvt_1_1_ & 1) != 0;
    }
}
