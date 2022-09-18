package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelWitch extends ModelVillager
{
    public boolean field_82900_g;
    private ModelRenderer field_82901_h = (new ModelRenderer(this)).setTextureSize(64, 128);
    private ModelRenderer witchHat;

    public ModelWitch(float p_i46361_1_)
    {
        super(p_i46361_1_, 0.0F, 64, 128);
        this.field_82901_h.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.field_82901_h.setTextureOffset(0, 0).addBox(0.0F, 3.0F, -6.75F, 1, 1, 1, -0.25F);
        this.villagerNose.addChild(this.field_82901_h);
        this.witchHat = (new ModelRenderer(this)).setTextureSize(64, 128);
        this.witchHat.setRotationPoint(-5.0F, -10.03125F, -5.0F);
        this.witchHat.setTextureOffset(0, 64).addBox(0.0F, 0.0F, 0.0F, 10, 2, 10);
        this.villagerHead.addChild(this.witchHat);
        ModelRenderer lvt_2_1_ = (new ModelRenderer(this)).setTextureSize(64, 128);
        lvt_2_1_.setRotationPoint(1.75F, -4.0F, 2.0F);
        lvt_2_1_.setTextureOffset(0, 76).addBox(0.0F, 0.0F, 0.0F, 7, 4, 7);
        lvt_2_1_.rotateAngleX = -0.05235988F;
        lvt_2_1_.rotateAngleZ = 0.02617994F;
        this.witchHat.addChild(lvt_2_1_);
        ModelRenderer lvt_3_1_ = (new ModelRenderer(this)).setTextureSize(64, 128);
        lvt_3_1_.setRotationPoint(1.75F, -4.0F, 2.0F);
        lvt_3_1_.setTextureOffset(0, 87).addBox(0.0F, 0.0F, 0.0F, 4, 4, 4);
        lvt_3_1_.rotateAngleX = -0.10471976F;
        lvt_3_1_.rotateAngleZ = 0.05235988F;
        lvt_2_1_.addChild(lvt_3_1_);
        ModelRenderer lvt_4_1_ = (new ModelRenderer(this)).setTextureSize(64, 128);
        lvt_4_1_.setRotationPoint(1.75F, -2.0F, 2.0F);
        lvt_4_1_.setTextureOffset(0, 95).addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.25F);
        lvt_4_1_.rotateAngleX = -0.20943952F;
        lvt_4_1_.rotateAngleZ = 0.10471976F;
        lvt_3_1_.addChild(lvt_4_1_);
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        this.villagerNose.offsetX = this.villagerNose.offsetY = this.villagerNose.offsetZ = 0.0F;
        float lvt_8_1_ = 0.01F * (float)(entityIn.getEntityId() % 10);
        this.villagerNose.rotateAngleX = MathHelper.sin((float)entityIn.ticksExisted * lvt_8_1_) * 4.5F * (float)Math.PI / 180.0F;
        this.villagerNose.rotateAngleY = 0.0F;
        this.villagerNose.rotateAngleZ = MathHelper.cos((float)entityIn.ticksExisted * lvt_8_1_) * 2.5F * (float)Math.PI / 180.0F;

        if (this.field_82900_g)
        {
            this.villagerNose.rotateAngleX = -0.9F;
            this.villagerNose.offsetZ = -0.09375F;
            this.villagerNose.offsetY = 0.1875F;
        }
    }
}
