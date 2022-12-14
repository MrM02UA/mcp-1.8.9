package net.minecraft.entity.projectile;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public abstract class EntityFireball extends Entity
{
    private int xTile = -1;
    private int yTile = -1;
    private int zTile = -1;
    private Block inTile;
    private boolean inGround;
    public EntityLivingBase shootingEntity;
    private int ticksAlive;
    private int ticksInAir;
    public double accelerationX;
    public double accelerationY;
    public double accelerationZ;

    public EntityFireball(World worldIn)
    {
        super(worldIn);
        this.setSize(1.0F, 1.0F);
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

    public EntityFireball(World worldIn, double x, double y, double z, double accelX, double accelY, double accelZ)
    {
        super(worldIn);
        this.setSize(1.0F, 1.0F);
        this.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
        this.setPosition(x, y, z);
        double lvt_14_1_ = (double)MathHelper.sqrt_double(accelX * accelX + accelY * accelY + accelZ * accelZ);
        this.accelerationX = accelX / lvt_14_1_ * 0.1D;
        this.accelerationY = accelY / lvt_14_1_ * 0.1D;
        this.accelerationZ = accelZ / lvt_14_1_ * 0.1D;
    }

    public EntityFireball(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ)
    {
        super(worldIn);
        this.shootingEntity = shooter;
        this.setSize(1.0F, 1.0F);
        this.setLocationAndAngles(shooter.posX, shooter.posY, shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.motionX = this.motionY = this.motionZ = 0.0D;
        accelX = accelX + this.rand.nextGaussian() * 0.4D;
        accelY = accelY + this.rand.nextGaussian() * 0.4D;
        accelZ = accelZ + this.rand.nextGaussian() * 0.4D;
        double lvt_9_1_ = (double)MathHelper.sqrt_double(accelX * accelX + accelY * accelY + accelZ * accelZ);
        this.accelerationX = accelX / lvt_9_1_ * 0.1D;
        this.accelerationY = accelY / lvt_9_1_ * 0.1D;
        this.accelerationZ = accelZ / lvt_9_1_ * 0.1D;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        if (this.worldObj.isRemote || (this.shootingEntity == null || !this.shootingEntity.isDead) && this.worldObj.isBlockLoaded(new BlockPos(this)))
        {
            super.onUpdate();
            this.setFire(1);

            if (this.inGround)
            {
                if (this.worldObj.getBlockState(new BlockPos(this.xTile, this.yTile, this.zTile)).getBlock() == this.inTile)
                {
                    ++this.ticksAlive;

                    if (this.ticksAlive == 600)
                    {
                        this.setDead();
                    }

                    return;
                }

                this.inGround = false;
                this.motionX *= (double)(this.rand.nextFloat() * 0.2F);
                this.motionY *= (double)(this.rand.nextFloat() * 0.2F);
                this.motionZ *= (double)(this.rand.nextFloat() * 0.2F);
                this.ticksAlive = 0;
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

            Entity lvt_4_1_ = null;
            List<Entity> lvt_5_1_ = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
            double lvt_6_1_ = 0.0D;

            for (int lvt_8_1_ = 0; lvt_8_1_ < lvt_5_1_.size(); ++lvt_8_1_)
            {
                Entity lvt_9_1_ = (Entity)lvt_5_1_.get(lvt_8_1_);

                if (lvt_9_1_.canBeCollidedWith() && (!lvt_9_1_.isEntityEqual(this.shootingEntity) || this.ticksInAir >= 25))
                {
                    float lvt_10_1_ = 0.3F;
                    AxisAlignedBB lvt_11_1_ = lvt_9_1_.getEntityBoundingBox().expand((double)lvt_10_1_, (double)lvt_10_1_, (double)lvt_10_1_);
                    MovingObjectPosition lvt_12_1_ = lvt_11_1_.calculateIntercept(lvt_1_1_, lvt_2_1_);

                    if (lvt_12_1_ != null)
                    {
                        double lvt_13_1_ = lvt_1_1_.squareDistanceTo(lvt_12_1_.hitVec);

                        if (lvt_13_1_ < lvt_6_1_ || lvt_6_1_ == 0.0D)
                        {
                            lvt_4_1_ = lvt_9_1_;
                            lvt_6_1_ = lvt_13_1_;
                        }
                    }
                }
            }

            if (lvt_4_1_ != null)
            {
                lvt_3_1_ = new MovingObjectPosition(lvt_4_1_);
            }

            if (lvt_3_1_ != null)
            {
                this.onImpact(lvt_3_1_);
            }

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            float lvt_8_2_ = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float)(MathHelper.atan2(this.motionZ, this.motionX) * 180.0D / Math.PI) + 90.0F;

            for (this.rotationPitch = (float)(MathHelper.atan2((double)lvt_8_2_, this.motionY) * 180.0D / Math.PI) - 90.0F; this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
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
            float lvt_9_2_ = this.getMotionFactor();

            if (this.isInWater())
            {
                for (int lvt_10_2_ = 0; lvt_10_2_ < 4; ++lvt_10_2_)
                {
                    float lvt_11_2_ = 0.25F;
                    this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * (double)lvt_11_2_, this.posY - this.motionY * (double)lvt_11_2_, this.posZ - this.motionZ * (double)lvt_11_2_, this.motionX, this.motionY, this.motionZ, new int[0]);
                }

                lvt_9_2_ = 0.8F;
            }

            this.motionX += this.accelerationX;
            this.motionY += this.accelerationY;
            this.motionZ += this.accelerationZ;
            this.motionX *= (double)lvt_9_2_;
            this.motionY *= (double)lvt_9_2_;
            this.motionZ *= (double)lvt_9_2_;
            this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D, new int[0]);
            this.setPosition(this.posX, this.posY, this.posZ);
        }
        else
        {
            this.setDead();
        }
    }

    /**
     * Return the motion factor for this projectile. The factor is multiplied by the original motion.
     */
    protected float getMotionFactor()
    {
        return 0.95F;
    }

    /**
     * Called when this EntityFireball hits a block or entity.
     */
    protected abstract void onImpact(MovingObjectPosition movingObject);

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
        tagCompound.setByte("inGround", (byte)(this.inGround ? 1 : 0));
        tagCompound.setTag("direction", this.newDoubleNBTList(new double[] {this.motionX, this.motionY, this.motionZ}));
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

        this.inGround = tagCompund.getByte("inGround") == 1;

        if (tagCompund.hasKey("direction", 9))
        {
            NBTTagList lvt_2_1_ = tagCompund.getTagList("direction", 6);
            this.motionX = lvt_2_1_.getDoubleAt(0);
            this.motionY = lvt_2_1_.getDoubleAt(1);
            this.motionZ = lvt_2_1_.getDoubleAt(2);
        }
        else
        {
            this.setDead();
        }
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return true;
    }

    public float getCollisionBorderSize()
    {
        return 1.0F;
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
        else
        {
            this.setBeenAttacked();

            if (source.getEntity() != null)
            {
                Vec3 lvt_3_1_ = source.getEntity().getLookVec();

                if (lvt_3_1_ != null)
                {
                    this.motionX = lvt_3_1_.xCoord;
                    this.motionY = lvt_3_1_.yCoord;
                    this.motionZ = lvt_3_1_.zCoord;
                    this.accelerationX = this.motionX * 0.1D;
                    this.accelerationY = this.motionY * 0.1D;
                    this.accelerationZ = this.motionZ * 0.1D;
                }

                if (source.getEntity() instanceof EntityLivingBase)
                {
                    this.shootingEntity = (EntityLivingBase)source.getEntity();
                }

                return true;
            }
            else
            {
                return false;
            }
        }
    }

    /**
     * Gets how bright this entity is.
     */
    public float getBrightness(float partialTicks)
    {
        return 1.0F;
    }

    public int getBrightnessForRender(float partialTicks)
    {
        return 15728880;
    }
}
