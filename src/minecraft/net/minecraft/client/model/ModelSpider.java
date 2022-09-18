package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelSpider extends ModelBase
{
    /** The spider's head box */
    public ModelRenderer spiderHead;

    /** The spider's neck box */
    public ModelRenderer spiderNeck;

    /** The spider's body box */
    public ModelRenderer spiderBody;

    /** Spider's first leg */
    public ModelRenderer spiderLeg1;

    /** Spider's second leg */
    public ModelRenderer spiderLeg2;

    /** Spider's third leg */
    public ModelRenderer spiderLeg3;

    /** Spider's fourth leg */
    public ModelRenderer spiderLeg4;

    /** Spider's fifth leg */
    public ModelRenderer spiderLeg5;

    /** Spider's sixth leg */
    public ModelRenderer spiderLeg6;

    /** Spider's seventh leg */
    public ModelRenderer spiderLeg7;

    /** Spider's eight leg */
    public ModelRenderer spiderLeg8;

    public ModelSpider()
    {
        float lvt_1_1_ = 0.0F;
        int lvt_2_1_ = 15;
        this.spiderHead = new ModelRenderer(this, 32, 4);
        this.spiderHead.addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8, lvt_1_1_);
        this.spiderHead.setRotationPoint(0.0F, (float)lvt_2_1_, -3.0F);
        this.spiderNeck = new ModelRenderer(this, 0, 0);
        this.spiderNeck.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6, lvt_1_1_);
        this.spiderNeck.setRotationPoint(0.0F, (float)lvt_2_1_, 0.0F);
        this.spiderBody = new ModelRenderer(this, 0, 12);
        this.spiderBody.addBox(-5.0F, -4.0F, -6.0F, 10, 8, 12, lvt_1_1_);
        this.spiderBody.setRotationPoint(0.0F, (float)lvt_2_1_, 9.0F);
        this.spiderLeg1 = new ModelRenderer(this, 18, 0);
        this.spiderLeg1.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, lvt_1_1_);
        this.spiderLeg1.setRotationPoint(-4.0F, (float)lvt_2_1_, 2.0F);
        this.spiderLeg2 = new ModelRenderer(this, 18, 0);
        this.spiderLeg2.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, lvt_1_1_);
        this.spiderLeg2.setRotationPoint(4.0F, (float)lvt_2_1_, 2.0F);
        this.spiderLeg3 = new ModelRenderer(this, 18, 0);
        this.spiderLeg3.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, lvt_1_1_);
        this.spiderLeg3.setRotationPoint(-4.0F, (float)lvt_2_1_, 1.0F);
        this.spiderLeg4 = new ModelRenderer(this, 18, 0);
        this.spiderLeg4.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, lvt_1_1_);
        this.spiderLeg4.setRotationPoint(4.0F, (float)lvt_2_1_, 1.0F);
        this.spiderLeg5 = new ModelRenderer(this, 18, 0);
        this.spiderLeg5.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, lvt_1_1_);
        this.spiderLeg5.setRotationPoint(-4.0F, (float)lvt_2_1_, 0.0F);
        this.spiderLeg6 = new ModelRenderer(this, 18, 0);
        this.spiderLeg6.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, lvt_1_1_);
        this.spiderLeg6.setRotationPoint(4.0F, (float)lvt_2_1_, 0.0F);
        this.spiderLeg7 = new ModelRenderer(this, 18, 0);
        this.spiderLeg7.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, lvt_1_1_);
        this.spiderLeg7.setRotationPoint(-4.0F, (float)lvt_2_1_, -1.0F);
        this.spiderLeg8 = new ModelRenderer(this, 18, 0);
        this.spiderLeg8.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, lvt_1_1_);
        this.spiderLeg8.setRotationPoint(4.0F, (float)lvt_2_1_, -1.0F);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale)
    {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entityIn);
        this.spiderHead.render(scale);
        this.spiderNeck.render(scale);
        this.spiderBody.render(scale);
        this.spiderLeg1.render(scale);
        this.spiderLeg2.render(scale);
        this.spiderLeg3.render(scale);
        this.spiderLeg4.render(scale);
        this.spiderLeg5.render(scale);
        this.spiderLeg6.render(scale);
        this.spiderLeg7.render(scale);
        this.spiderLeg8.render(scale);
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        this.spiderHead.rotateAngleY = netHeadYaw / (180F / (float)Math.PI);
        this.spiderHead.rotateAngleX = headPitch / (180F / (float)Math.PI);
        float lvt_8_1_ = ((float)Math.PI / 4F);
        this.spiderLeg1.rotateAngleZ = -lvt_8_1_;
        this.spiderLeg2.rotateAngleZ = lvt_8_1_;
        this.spiderLeg3.rotateAngleZ = -lvt_8_1_ * 0.74F;
        this.spiderLeg4.rotateAngleZ = lvt_8_1_ * 0.74F;
        this.spiderLeg5.rotateAngleZ = -lvt_8_1_ * 0.74F;
        this.spiderLeg6.rotateAngleZ = lvt_8_1_ * 0.74F;
        this.spiderLeg7.rotateAngleZ = -lvt_8_1_;
        this.spiderLeg8.rotateAngleZ = lvt_8_1_;
        float lvt_9_1_ = -0.0F;
        float lvt_10_1_ = 0.3926991F;
        this.spiderLeg1.rotateAngleY = lvt_10_1_ * 2.0F + lvt_9_1_;
        this.spiderLeg2.rotateAngleY = -lvt_10_1_ * 2.0F - lvt_9_1_;
        this.spiderLeg3.rotateAngleY = lvt_10_1_ * 1.0F + lvt_9_1_;
        this.spiderLeg4.rotateAngleY = -lvt_10_1_ * 1.0F - lvt_9_1_;
        this.spiderLeg5.rotateAngleY = -lvt_10_1_ * 1.0F + lvt_9_1_;
        this.spiderLeg6.rotateAngleY = lvt_10_1_ * 1.0F - lvt_9_1_;
        this.spiderLeg7.rotateAngleY = -lvt_10_1_ * 2.0F + lvt_9_1_;
        this.spiderLeg8.rotateAngleY = lvt_10_1_ * 2.0F - lvt_9_1_;
        float lvt_11_1_ = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + 0.0F) * 0.4F) * limbSwingAmount;
        float lvt_12_1_ = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + (float)Math.PI) * 0.4F) * limbSwingAmount;
        float lvt_13_1_ = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + ((float)Math.PI / 2F)) * 0.4F) * limbSwingAmount;
        float lvt_14_1_ = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + ((float)Math.PI * 3F / 2F)) * 0.4F) * limbSwingAmount;
        float lvt_15_1_ = Math.abs(MathHelper.sin(limbSwing * 0.6662F + 0.0F) * 0.4F) * limbSwingAmount;
        float lvt_16_1_ = Math.abs(MathHelper.sin(limbSwing * 0.6662F + (float)Math.PI) * 0.4F) * limbSwingAmount;
        float lvt_17_1_ = Math.abs(MathHelper.sin(limbSwing * 0.6662F + ((float)Math.PI / 2F)) * 0.4F) * limbSwingAmount;
        float lvt_18_1_ = Math.abs(MathHelper.sin(limbSwing * 0.6662F + ((float)Math.PI * 3F / 2F)) * 0.4F) * limbSwingAmount;
        this.spiderLeg1.rotateAngleY += lvt_11_1_;
        this.spiderLeg2.rotateAngleY += -lvt_11_1_;
        this.spiderLeg3.rotateAngleY += lvt_12_1_;
        this.spiderLeg4.rotateAngleY += -lvt_12_1_;
        this.spiderLeg5.rotateAngleY += lvt_13_1_;
        this.spiderLeg6.rotateAngleY += -lvt_13_1_;
        this.spiderLeg7.rotateAngleY += lvt_14_1_;
        this.spiderLeg8.rotateAngleY += -lvt_14_1_;
        this.spiderLeg1.rotateAngleZ += lvt_15_1_;
        this.spiderLeg2.rotateAngleZ += -lvt_15_1_;
        this.spiderLeg3.rotateAngleZ += lvt_16_1_;
        this.spiderLeg4.rotateAngleZ += -lvt_16_1_;
        this.spiderLeg5.rotateAngleZ += lvt_17_1_;
        this.spiderLeg6.rotateAngleZ += -lvt_17_1_;
        this.spiderLeg7.rotateAngleZ += lvt_18_1_;
        this.spiderLeg8.rotateAngleZ += -lvt_18_1_;
    }
}
