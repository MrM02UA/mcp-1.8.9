package net.minecraft.entity.projectile;

import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityThrowable extends Entity implements IProjectile
{
    private int xTile = -1;
    private int yTile = -1;
    private int zTile = -1;
    private Block inTile;
    protected boolean inGround;
    public int throwableShake;

    /** The entity that threw this throwable item. */
    private EntityLivingBase thrower;
    private String throwerName;
    private int ticksInGround;
    private int ticksInAir;

    public EntityThrowable(World worldIn)
    {
        super(worldIn);
        this.setSize(0.25F, 0.25F);
    }

    protected void entityInit()
    {
    }

    /**
     * Checks if the entity is in range to render by using the past in distance and comparing it to its average edge
     * length * 64 * renderDistanceWeight Args: distance
     */
    public boolean isInRangeToRenderDist(double distance)
    {
        double lvt_3_1_ = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0D;

        if (Double.isNaN(lvt_3_1_))
        {
            lvt_3_1_ = 4.0D;
        }

        lvt_3_1_ = lvt_3_1_ * 64.0D;
        return distance < lvt_3_1_ * lvt_3_1_;
    }

    public EntityThrowable(World worldIn, EntityLivingBase throwerIn)
    {
        super(worldIn);
        this.thrower = throwerIn;
        this.setSize(0.25F, 0.25F);
        this.setLocationAndAngles(throwerIn.posX, throwerIn.posY + (double)throwerIn.getEyeHeight(), throwerIn.posZ, throwerIn.rotationYaw, throwerIn.rotationPitch);
        this.posX -= (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        this.posY -= 0.10000000149011612D;
        this.posZ -= (double)(MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        this.setPosition(this.posX, this.posY, this.posZ);
        float lvt_3_1_ = 0.4F;
        this.motionX = (double)(-MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * lvt_3_1_);
        this.motionZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * lvt_3_1_);
        this.motionY = (double)(-MathHelper.sin((this.rotationPitch + this.getInaccuracy()) / 180.0F * (float)Math.PI) * lvt_3_1_);
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, this.getVelocity(), 1.0F);
    }

    public EntityThrowable(World worldIn, double x, double y, double z)
    {
        super(worldIn);
        this.ticksInGround = 0;
        this.setSize(0.25F, 0.25F);
        this.setPosition(x, y, z);
    }

    protected float getVelocity()
    {
        return 1.5F;
    }

    protected float getInaccuracy()
    {
        return 0.0F;
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
        x = x + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        y = y + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        z = z + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
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
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;
        super.onUpdate();

        if (this.throwableShake > 0)
        {
            --this.throwableShake;
        }

        if (this.inGround)
        {
            if (this.worldObj.getBlockState(new BlockPos(this.xTile, this.yTile, this.zTile)).getBlock() == this.inTile)
            {
                ++this.ticksInGround;

                if (this.ticksInGround == 1200)
                {
                    this.setDead();
                }

                return;
            }

            this.inGround = false;
            this.motionX *= (double)(this.rand.nextFloat() * 0.2F);
            this.motionY *= (double)(this.rand.nextFloat() * 0.2F);
            this.motionZ *= (double)(this.rand.nextFloat() * 0.2F);
            this.ticksInGround = 0;
            this.ticksInAir = 0;
        }
        else
        {
            ++this.ticksInAir;
        }

        Vec3 lvt_1_1_ = new Vec3(this.posX, this.posY, this.posZ);
        Vec3 lvt_2_1_ = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        MovingObjectPosition lvt_3_1_ = this.worldObj.rayTraceBlocks(lvt_1_1_, lvt_2_1_);
        lvt_1_1_ = new Vec3(this.posX, this.posY, this.posZ);
        lvt_2_1_ = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

        if (lvt_3_1_ != null)
        {
            lvt_2_1_ = new Vec3(lvt_3_1_.hitVec.xCoord, lvt_3_1_.hitVec.yCoord, lvt_3_1_.hitVec.zCoord);
        }

        if (!this.worldObj.isRemote)
        {
            Entity lvt_4_1_ = null;
            List<Entity> lvt_5_1_ = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
            double lvt_6_1_ = 0.0D;
            EntityLivingBase lvt_8_1_ = this.getThrower();

            for (int lvt_9_1_ = 0; lvt_9_1_ < lvt_5_1_.size(); ++lvt_9_1_)
            {
                Entity lvt_10_1_ = (Entity)lvt_5_1_.get(lvt_9_1_);

                if (lvt_10_1_.canBeCollidedWith() && (lvt_10_1_ != lvt_8_1_ || this.ticksInAir >= 5))
                {
                    float lvt_11_1_ = 0.3F;
                    AxisAlignedBB lvt_12_1_ = lvt_10_1_.getEntityBoundingBox().expand((double)lvt_11_1_, (double)lvt_11_1_, (double)lvt_11_1_);
                    MovingObjectPosition lvt_13_1_ = lvt_12_1_.calculateIntercept(lvt_1_1_, lvt_2_1_);

                    if (lvt_13_1_ != null)
                    {
                        double lvt_14_1_ = lvt_1_1_.squareDistanceTo(lvt_13_1_.hitVec);

                        if (lvt_14_1_ < lvt_6_1_ || lvt_6_1_ == 0.0D)
                        {
                            lvt_4_1_ = lvt_10_1_;
                            lvt_6_1_ = lvt_14_1_;
                        }
                    }
                }
            }

            if (lvt_4_1_ != null)
            {
                lvt_3_1_ = new MovingObjectPosition(lvt_4_1_);
            }
        }

        if (lvt_3_1_ != null)
        {
            if (lvt_3_1_.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.worldObj.getBlockState(lvt_3_1_.getBlockPos()).getBlock() == Blocks.portal)
            {
                this.setPortal(lvt_3_1_.getBlockPos());
            }
            else
            {
                this.onImpact(lvt_3_1_);
            }
        }

        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        float lvt_4_2_ = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

        for (this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)lvt_4_2_) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
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
        float lvt_5_2_ = 0.99F;
        float lvt_6_2_ = this.getGravityVelocity();

        if (this.isInWater())
        {
            for (int lvt_7_1_ = 0; lvt_7_1_ < 4; ++lvt_7_1_)
            {
                float lvt_8_2_ = 0.25F;
                this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * (double)lvt_8_2_, this.posY - this.motionY * (double)lvt_8_2_, this.posZ - this.motionZ * (double)lvt_8_2_, this.motionX, this.motionY, this.motionZ, new int[0]);
            }

            lvt_5_2_ = 0.8F;
        }

        this.motionX *= (double)lvt_5_2_;
        this.motionY *= (double)lvt_5_2_;
        this.motionZ *= (double)lvt_5_2_;
        this.motionY -= (double)lvt_6_2_;
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    /**
     * Gets the amount of gravity to apply to the thrown entity with each tick.
     */
    protected float getGravityVelocity()
    {
        return 0.03F;
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    protected abstract void onImpact(MovingObjectPosition p_70184_1_);

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        tagCompound.setShort("xTile", (short)this.xTile);
        tagCompound.setShort("yTile", (short)this.yTile);
        tagCompound.setShort("zTile", (short)this.zTile);
        ResourceLocation lvt_2_1_ = (ResourceLocation)Block.blockRegistry.getNameForObject(this.inTile);
        tagCompound.setString("inTile", lvt_2_1_ == null ? "" : lvt_2_1_.toString());
        tagCompound.setByte("shake", (byte)this.throwableShake);
        tagCompound.setByte("inGround", (byte)(this.inGround ? 1 : 0));

        if ((this.throwerName == null || this.throwerName.length() == 0) && this.thrower instanceof EntityPlayer)
        {
            this.throwerName = this.thrower.getName();
        }

        tagCompound.setString("ownerName", this.throwerName == null ? "" : this.throwerName);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        this.xTile = tagCompund.getShort("xTile");
        this.yTile = tagCompund.getShort("yTile");
        this.zTile = tagCompund.getShort("zTile");

        if (tagCompund.hasKey("inTile", 8))
        {
            this.inTile = Block.getBlockFromName(tagCompund.getString("inTile"));
        }
        else
        {
            this.inTile = Block.getBlockById(tagCompund.getByte("inTile") & 255);
        }

        this.throwableShake = tagCompund.getByte("shake") & 255;
        this.inGround = tagCompund.getByte("inGround") == 1;
        this.thrower = null;
        this.throwerName = tagCompund.getString("ownerName");

        if (this.throwerName != null && this.throwerName.length() == 0)
        {
            this.throwerName = null;
        }

        this.thrower = this.getThrower();
    }

    public EntityLivingBase getThrower()
    {
        if (this.thrower == null && this.throwerName != null && this.throwerName.length() > 0)
        {
            this.thrower = this.worldObj.getPlayerEntityByName(this.throwerName);

            if (this.thrower == null && this.worldObj instanceof WorldServer)
            {
                try
                {
                    Entity lvt_1_1_ = ((WorldServer)this.worldObj).getEntityFromUuid(UUID.fromString(this.throwerName));

                    if (lvt_1_1_ instanceof EntityLivingBase)
                    {
                        this.thrower = (EntityLivingBase)lvt_1_1_;
                    }
                }
                catch (Throwable var2)
                {
                    this.thrower = null;
                }
            }
        }

        return this.thrower;
    }
}
