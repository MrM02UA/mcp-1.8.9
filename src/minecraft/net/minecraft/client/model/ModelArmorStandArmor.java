package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;

public class ModelArmorStandArmor extends ModelBiped
{
    public ModelArmorStandArmor()
    {
        this(0.0F);
    }

    public ModelArmorStandArmor(float modelSize)
    {
        this(modelSize, 64, 32);
    }

    protected ModelArmorStandArmor(float modelSize, int textureWidthIn, int textureHeightIn)
    {
        super(modelSize, 0.0F, textureWidthIn, textureHeightIn);
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        if (entityIn instanceof EntityArmorStand)
        {
            EntityArmorStand lvt_8_1_ = (EntityArmorStand)entityIn;
            this.bipedHead.rotateAngleX = 0.017453292F * lvt_8_1_.getHeadRotation().getX();
            this.bipedHead.rotateAngleY = 0.017453292F * lvt_8_1_.getHeadRotation().getY();
            this.bipedHead.rotateAngleZ = 0.017453292F * lvt_8_1_.getHeadRotation().getZ();
            this.bipedHead.setRotationPoint(0.0F, 1.0F, 0.0F);
            this.bipedBody.rotateAngleX = 0.017453292F * lvt_8_1_.getBodyRotation().getX();
            this.bipedBody.rotateAngleY = 0.017453292F * lvt_8_1_.getBodyRotation().getY();
            this.bipedBody.rotateAngleZ = 0.017453292F * lvt_8_1_.getBodyRotation().getZ();
            this.bipedLeftArm.rotateAngleX = 0.017453292F * lvt_8_1_.getLeftArmRotation().getX();
            this.bipedLeftArm.rotateAngleY = 0.017453292F * lvt_8_1_.getLeftArmRotation().getY();
            this.bipedLeftArm.rotateAngleZ = 0.017453292F * lvt_8_1_.getLeftArmRotation().getZ();
            this.bipedRightArm.rotateAngleX = 0.017453292F * lvt_8_1_.getRightArmRotation().getX();
            this.bipedRightArm.rotateAngleY = 0.017453292F * lvt_8_1_.getRightArmRotation().getY();
            this.bipedRightArm.rotateAngleZ = 0.017453292F * lvt_8_1_.getRightArmRotation().getZ();
            this.bipedLeftLeg.rotateAngleX = 0.017453292F * lvt_8_1_.getLeftLegRotation().getX();
            this.bipedLeftLeg.rotateAngleY = 0.017453292F * lvt_8_1_.getLeftLegRotation().getY();
            this.bipedLeftLeg.rotateAngleZ = 0.017453292F * lvt_8_1_.getLeftLegRotation().getZ();
            this.bipedLeftLeg.setRotationPoint(1.9F, 11.0F, 0.0F);
            this.bipedRightLeg.rotateAngleX = 0.017453292F * lvt_8_1_.getRightLegRotation().getX();
            this.bipedRightLeg.rotateAngleY = 0.017453292F * lvt_8_1_.getRightLegRotation().getY();
            this.bipedRightLeg.rotateAngleZ = 0.017453292F * lvt_8_1_.getRightLegRotation().getZ();
            this.bipedRightLeg.setRotationPoint(-1.9F, 11.0F, 0.0F);
            copyModelAngles(this.bipedHead, this.bipedHeadwear);
        }
    }
}
