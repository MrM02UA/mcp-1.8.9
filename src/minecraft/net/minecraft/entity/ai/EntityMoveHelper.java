package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.MathHelper;

public class EntityMoveHelper
{
    /** The EntityLiving that is being moved */
    protected EntityLiving entity;
    protected double posX;
    protected double posY;
    protected double posZ;

    /** The speed at which the entity should move */
    protected double speed;
    protected boolean update;

    public EntityMoveHelper(EntityLiving entitylivingIn)
    {
        this.entity = entitylivingIn;
        this.posX = entitylivingIn.posX;
        this.posY = entitylivingIn.posY;
        this.posZ = entitylivingIn.posZ;
    }

    public boolean isUpdating()
    {
        return this.update;
    }

    public double getSpeed()
    {
        return this.speed;
    }

    /**
     * Sets the speed and location to move to
     */
    public void setMoveTo(double x, double y, double z, double speedIn)
    {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.speed = speedIn;
        this.update = true;
    }

    public void onUpdateMoveHelper()
    {
        this.entity.setMoveForward(0.0F);

        if (this.update)
        {
            this.update = false;
            int lvt_1_1_ = MathHelper.floor_double(this.entity.getEntityBoundingBox().minY + 0.5D);
            double lvt_2_1_ = this.posX - this.entity.posX;
            double lvt_4_1_ = this.posZ - this.entity.posZ;
            double lvt_6_1_ = this.posY - (double)lvt_1_1_;
            double lvt_8_1_ = lvt_2_1_ * lvt_2_1_ + lvt_6_1_ * lvt_6_1_ + lvt_4_1_ * lvt_4_1_;

            if (lvt_8_1_ >= 2.500000277905201E-7D)
            {
                float lvt_10_1_ = (float)(MathHelper.atan2(lvt_4_1_, lvt_2_1_) * 180.0D / Math.PI) - 90.0F;
                this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, lvt_10_1_, 30.0F);
                this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue()));

                if (lvt_6_1_ > 0.0D && lvt_2_1_ * lvt_2_1_ + lvt_4_1_ * lvt_4_1_ < 1.0D)
                {
                    this.entity.getJumpHelper().setJumping();
                }
            }
        }
    }

    /**
     * Limits the given angle to a upper and lower limit.
     */
    protected float limitAngle(float p_75639_1_, float p_75639_2_, float p_75639_3_)
    {
        float lvt_4_1_ = MathHelper.wrapAngleTo180_float(p_75639_2_ - p_75639_1_);

        if (lvt_4_1_ > p_75639_3_)
        {
            lvt_4_1_ = p_75639_3_;
        }

        if (lvt_4_1_ < -p_75639_3_)
        {
            lvt_4_1_ = -p_75639_3_;
        }

        float lvt_5_1_ = p_75639_1_ + lvt_4_1_;

        if (lvt_5_1_ < 0.0F)
        {
            lvt_5_1_ += 360.0F;
        }
        else if (lvt_5_1_ > 360.0F)
        {
            lvt_5_1_ -= 360.0F;
        }

        return lvt_5_1_;
    }

    public double getX()
    {
        return this.posX;
    }

    public double getY()
    {
        return this.posY;
    }

    public double getZ()
    {
        return this.posZ;
    }
}
