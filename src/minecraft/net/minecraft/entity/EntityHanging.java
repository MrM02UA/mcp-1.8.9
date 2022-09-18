package net.minecraft.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;

public abstract class EntityHanging extends Entity
{
    private int tickCounter1;
    protected BlockPos hangingPosition;

    /** The direction the entity is facing */
    public EnumFacing facingDirection;

    public EntityHanging(World worldIn)
    {
        super(worldIn);
        this.setSize(0.5F, 0.5F);
    }

    public EntityHanging(World worldIn, BlockPos hangingPositionIn)
    {
        this(worldIn);
        this.hangingPosition = hangingPositionIn;
    }

    protected void entityInit()
    {
    }

    /**
     * Updates facing and bounding box based on it
     */
    protected void updateFacingWithBoundingBox(EnumFacing facingDirectionIn)
    {
        Validate.notNull(facingDirectionIn);
        Validate.isTrue(facingDirectionIn.getAxis().isHorizontal());
        this.facingDirection = facingDirectionIn;
        this.prevRotationYaw = this.rotationYaw = (float)(this.facingDirection.getHorizontalIndex() * 90);
        this.updateBoundingBox();
    }

    /**
     * Updates the entity bounding box based on current facing
     */
    private void updateBoundingBox()
    {
        if (this.facingDirection != null)
        {
            double lvt_1_1_ = (double)this.hangingPosition.getX() + 0.5D;
            double lvt_3_1_ = (double)this.hangingPosition.getY() + 0.5D;
            double lvt_5_1_ = (double)this.hangingPosition.getZ() + 0.5D;
            double lvt_7_1_ = 0.46875D;
            double lvt_9_1_ = this.func_174858_a(this.getWidthPixels());
            double lvt_11_1_ = this.func_174858_a(this.getHeightPixels());
            lvt_1_1_ = lvt_1_1_ - (double)this.facingDirection.getFrontOffsetX() * 0.46875D;
            lvt_5_1_ = lvt_5_1_ - (double)this.facingDirection.getFrontOffsetZ() * 0.46875D;
            lvt_3_1_ = lvt_3_1_ + lvt_11_1_;
            EnumFacing lvt_13_1_ = this.facingDirection.rotateYCCW();
            lvt_1_1_ = lvt_1_1_ + lvt_9_1_ * (double)lvt_13_1_.getFrontOffsetX();
            lvt_5_1_ = lvt_5_1_ + lvt_9_1_ * (double)lvt_13_1_.getFrontOffsetZ();
            this.posX = lvt_1_1_;
            this.posY = lvt_3_1_;
            this.posZ = lvt_5_1_;
            double lvt_14_1_ = (double)this.getWidthPixels();
            double lvt_16_1_ = (double)this.getHeightPixels();
            double lvt_18_1_ = (double)this.getWidthPixels();

            if (this.facingDirection.getAxis() == EnumFacing.Axis.Z)
            {
                lvt_18_1_ = 1.0D;
            }
            else
            {
                lvt_14_1_ = 1.0D;
            }

            lvt_14_1_ = lvt_14_1_ / 32.0D;
            lvt_16_1_ = lvt_16_1_ / 32.0D;
            lvt_18_1_ = lvt_18_1_ / 32.0D;
            this.setEntityBoundingBox(new AxisAlignedBB(lvt_1_1_ - lvt_14_1_, lvt_3_1_ - lvt_16_1_, lvt_5_1_ - lvt_18_1_, lvt_1_1_ + lvt_14_1_, lvt_3_1_ + lvt_16_1_, lvt_5_1_ + lvt_18_1_));
        }
    }

    private double func_174858_a(int p_174858_1_)
    {
        return p_174858_1_ % 32 == 0 ? 0.5D : 0.0D;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.tickCounter1++ == 100 && !this.worldObj.isRemote)
        {
            this.tickCounter1 = 0;

            if (!this.isDead && !this.onValidSurface())
            {
                this.setDead();
                this.onBroken((Entity)null);
            }
        }
    }

    /**
     * checks to make sure painting can be placed there
     */
    public boolean onValidSurface()
    {
        if (!this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty())
        {
            return false;
        }
        else
        {
            int lvt_1_1_ = Math.max(1, this.getWidthPixels() / 16);
            int lvt_2_1_ = Math.max(1, this.getHeightPixels() / 16);
            BlockPos lvt_3_1_ = this.hangingPosition.offset(this.facingDirection.getOpposite());
            EnumFacing lvt_4_1_ = this.facingDirection.rotateYCCW();

            for (int lvt_5_1_ = 0; lvt_5_1_ < lvt_1_1_; ++lvt_5_1_)
            {
                for (int lvt_6_1_ = 0; lvt_6_1_ < lvt_2_1_; ++lvt_6_1_)
                {
                    BlockPos lvt_7_1_ = lvt_3_1_.offset(lvt_4_1_, lvt_5_1_).up(lvt_6_1_);
                    Block lvt_8_1_ = this.worldObj.getBlockState(lvt_7_1_).getBlock();

                    if (!lvt_8_1_.getMaterial().isSolid() && !BlockRedstoneDiode.isRedstoneRepeaterBlockID(lvt_8_1_))
                    {
                        return false;
                    }
                }
            }

            for (Entity lvt_7_2_ : this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox()))
            {
                if (lvt_7_2_ instanceof EntityHanging)
                {
                    return false;
                }
            }

            return true;
        }
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return true;
    }

    /**
     * Called when a player attacks an entity. If this returns true the attack will not happen.
     */
    public boolean hitByEntity(Entity entityIn)
    {
        return entityIn instanceof EntityPlayer ? this.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)entityIn), 0.0F) : false;
    }

    public EnumFacing getHorizontalFacing()
    {
        return this.facingDirection;
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
            if (!this.isDead && !this.worldObj.isRemote)
            {
                this.setDead();
                this.setBeenAttacked();
                this.onBroken(source.getEntity());
            }

            return true;
        }
    }

    /**
     * Tries to moves the entity by the passed in displacement. Args: x, y, z
     */
    public void moveEntity(double x, double y, double z)
    {
        if (!this.worldObj.isRemote && !this.isDead && x * x + y * y + z * z > 0.0D)
        {
            this.setDead();
            this.onBroken((Entity)null);
        }
    }

    /**
     * Adds to the current velocity of the entity. Args: x, y, z
     */
    public void addVelocity(double x, double y, double z)
    {
        if (!this.worldObj.isRemote && !this.isDead && x * x + y * y + z * z > 0.0D)
        {
            this.setDead();
            this.onBroken((Entity)null);
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        tagCompound.setByte("Facing", (byte)this.facingDirection.getHorizontalIndex());
        tagCompound.setInteger("TileX", this.getHangingPosition().getX());
        tagCompound.setInteger("TileY", this.getHangingPosition().getY());
        tagCompound.setInteger("TileZ", this.getHangingPosition().getZ());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        this.hangingPosition = new BlockPos(tagCompund.getInteger("TileX"), tagCompund.getInteger("TileY"), tagCompund.getInteger("TileZ"));
        EnumFacing lvt_2_1_;

        if (tagCompund.hasKey("Direction", 99))
        {
            lvt_2_1_ = EnumFacing.getHorizontal(tagCompund.getByte("Direction"));
            this.hangingPosition = this.hangingPosition.offset(lvt_2_1_);
        }
        else if (tagCompund.hasKey("Facing", 99))
        {
            lvt_2_1_ = EnumFacing.getHorizontal(tagCompund.getByte("Facing"));
        }
        else
        {
            lvt_2_1_ = EnumFacing.getHorizontal(tagCompund.getByte("Dir"));
        }

        this.updateFacingWithBoundingBox(lvt_2_1_);
    }

    public abstract int getWidthPixels();

    public abstract int getHeightPixels();

    /**
     * Called when this entity is broken. Entity parameter may be null.
     */
    public abstract void onBroken(Entity brokenEntity);

    protected boolean shouldSetPosAfterLoading()
    {
        return false;
    }

    /**
     * Sets the x,y,z of the entity from the given parameters. Also seems to set up a bounding box.
     */
    public void setPosition(double x, double y, double z)
    {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        BlockPos lvt_7_1_ = this.hangingPosition;
        this.hangingPosition = new BlockPos(x, y, z);

        if (!this.hangingPosition.equals(lvt_7_1_))
        {
            this.updateBoundingBox();
            this.isAirBorne = true;
        }
    }

    public BlockPos getHangingPosition()
    {
        return this.hangingPosition;
    }
}
