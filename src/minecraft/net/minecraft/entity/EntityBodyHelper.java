package net.minecraft.entity;

import net.minecraft.util.MathHelper;

public class EntityBodyHelper
{
    /** Instance of EntityLiving. */
    private EntityLivingBase theLiving;

    /**
     * Used to progressively ajust the rotation of the body to the rotation of the head
     */
    private int rotationTickCounter;
    private float prevRenderYawHead;

    public EntityBodyHelper(EntityLivingBase p_i1611_1_)
    {
        this.theLiving = p_i1611_1_;
    }

    /**
     * Update the Head and Body rendenring angles
     */
    public void updateRenderAngles()
    {
        double lvt_1_1_ = this.theLiving.posX - this.theLiving.prevPosX;
        double lvt_3_1_ = this.theLiving.posZ - this.theLiving.prevPosZ;

        if (lvt_1_1_ * lvt_1_1_ + lvt_3_1_ * lvt_3_1_ > 2.500000277905201E-7D)
        {
            this.theLiving.renderYawOffset = this.theLiving.rotationYaw;
            this.theLiving.rotationYawHead = this.computeAngleWithBound(this.theLiving.renderYawOffset, this.theLiving.rotationYawHead, 75.0F);
            this.prevRenderYawHead = this.theLiving.rotationYawHead;
            this.rotationTickCounter = 0;
        }
        else
        {
            float lvt_5_1_ = 75.0F;

            if (Math.abs(this.theLiving.rotationYawHead - this.prevRenderYawHead) > 15.0F)
            {
                this.rotationTickCounter = 0;
                this.prevRenderYawHead = this.theLiving.rotationYawHead;
            }
            else
            {
                ++this.rotationTickCounter;
                int lvt_6_1_ = 10;

                if (this.rotationTickCounter > 10)
                {
                    lvt_5_1_ = Math.max(1.0F - (float)(this.rotationTickCounter - 10) / 10.0F, 0.0F) * 75.0F;
                }
            }

            this.theLiving.renderYawOffset = this.computeAngleWithBound(this.theLiving.rotationYawHead, this.theLiving.renderYawOffset, lvt_5_1_);
        }
    }

    /**
     * Return the new angle2 such that the difference between angle1 and angle2 is lower than angleMax. Args : angle1,
     * angle2, angleMax
     */
    private float computeAngleWithBound(float p_75665_1_, float p_75665_2_, float p_75665_3_)
    {
        float lvt_4_1_ = MathHelper.wrapAngleTo180_float(p_75665_1_ - p_75665_2_);

        if (lvt_4_1_ < -p_75665_3_)
        {
            lvt_4_1_ = -p_75665_3_;
        }

        if (lvt_4_1_ >= p_75665_3_)
        {
            lvt_4_1_ = p_75665_3_;
        }

        return p_75665_1_ - lvt_4_1_;
    }
}
