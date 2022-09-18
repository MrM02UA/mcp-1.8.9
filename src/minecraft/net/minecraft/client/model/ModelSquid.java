package net.minecraft.client.model;

import net.minecraft.entity.Entity;

public class ModelSquid extends ModelBase
{
    /** The squid's body */
    ModelRenderer squidBody;

    /** The squid's tentacles */
    ModelRenderer[] squidTentacles = new ModelRenderer[8];

    public ModelSquid()
    {
        int lvt_1_1_ = -16;
        this.squidBody = new ModelRenderer(this, 0, 0);
        this.squidBody.addBox(-6.0F, -8.0F, -6.0F, 12, 16, 12);
        this.squidBody.rotationPointY += (float)(24 + lvt_1_1_);

        for (int lvt_2_1_ = 0; lvt_2_1_ < this.squidTentacles.length; ++lvt_2_1_)
        {
            this.squidTentacles[lvt_2_1_] = new ModelRenderer(this, 48, 0);
            double lvt_3_1_ = (double)lvt_2_1_ * Math.PI * 2.0D / (double)this.squidTentacles.length;
            float lvt_5_1_ = (float)Math.cos(lvt_3_1_) * 5.0F;
            float lvt_6_1_ = (float)Math.sin(lvt_3_1_) * 5.0F;
            this.squidTentacles[lvt_2_1_].addBox(-1.0F, 0.0F, -1.0F, 2, 18, 2);
            this.squidTentacles[lvt_2_1_].rotationPointX = lvt_5_1_;
            this.squidTentacles[lvt_2_1_].rotationPointZ = lvt_6_1_;
            this.squidTentacles[lvt_2_1_].rotationPointY = (float)(31 + lvt_1_1_);
            lvt_3_1_ = (double)lvt_2_1_ * Math.PI * -2.0D / (double)this.squidTentacles.length + (Math.PI / 2D);
            this.squidTentacles[lvt_2_1_].rotateAngleY = (float)lvt_3_1_;
        }
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        for (ModelRenderer lvt_11_1_ : this.squidTentacles)
        {
            lvt_11_1_.rotateAngleX = ageInTicks;
        }
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale)
    {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entityIn);
        this.squidBody.render(scale);

        for (int lvt_8_1_ = 0; lvt_8_1_ < this.squidTentacles.length; ++lvt_8_1_)
        {
            this.squidTentacles[lvt_8_1_].render(scale);
        }
    }
}
