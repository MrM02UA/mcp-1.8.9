package net.minecraft.entity;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class EntityFlying extends EntityLiving
{
    public EntityFlying(World worldIn)
    {
        super(worldIn);
    }

    public void fall(float distance, float damageMultiplier)
    {
    }

    protected void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos)
    {
    }

    /**
     * Moves the entity based on the specified heading.  Args: strafe, forward
     */
    public void moveEntityWithHeading(float strafe, float forward)
    {
        if (this.isInWater())
        {
            this.moveFlying(strafe, forward, 0.02F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.800000011920929D;
            this.motionY *= 0.800000011920929D;
            this.motionZ *= 0.800000011920929D;
        }
        else if (this.isInLava())
        {
            this.moveFlying(strafe, forward, 0.02F);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.5D;
            this.motionY *= 0.5D;
            this.motionZ *= 0.5D;
        }
        else
        {
            float lvt_3_1_ = 0.91F;

            if (this.onGround)
            {
                lvt_3_1_ = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ))).getBlock().slipperiness * 0.91F;
            }

            float lvt_4_1_ = 0.16277136F / (lvt_3_1_ * lvt_3_1_ * lvt_3_1_);
            this.moveFlying(strafe, forward, this.onGround ? 0.1F * lvt_4_1_ : 0.02F);
            lvt_3_1_ = 0.91F;

            if (this.onGround)
            {
                lvt_3_1_ = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ))).getBlock().slipperiness * 0.91F;
            }

            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double)lvt_3_1_;
            this.motionY *= (double)lvt_3_1_;
            this.motionZ *= (double)lvt_3_1_;
        }

        this.prevLimbSwingAmount = this.limbSwingAmount;
        double lvt_3_2_ = this.posX - this.prevPosX;
        double lvt_5_1_ = this.posZ - this.prevPosZ;
        float lvt_7_1_ = MathHelper.sqrt_double(lvt_3_2_ * lvt_3_2_ + lvt_5_1_ * lvt_5_1_) * 4.0F;

        if (lvt_7_1_ > 1.0F)
        {
            lvt_7_1_ = 1.0F;
        }

        this.limbSwingAmount += (lvt_7_1_ - this.limbSwingAmount) * 0.4F;
        this.limbSwing += this.limbSwingAmount;
    }

    /**
     * returns true if this entity is by a ladder, false otherwise
     */
    public boolean isOnLadder()
    {
        return false;
    }
}
