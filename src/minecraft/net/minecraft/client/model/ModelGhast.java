package net.minecraft.client.model;

import java.util.Random;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelGhast extends ModelBase
{
    ModelRenderer body;
    ModelRenderer[] tentacles = new ModelRenderer[9];

    public ModelGhast()
    {
        int lvt_1_1_ = -16;
        this.body = new ModelRenderer(this, 0, 0);
        this.body.addBox(-8.0F, -8.0F, -8.0F, 16, 16, 16);
        this.body.rotationPointY += (float)(24 + lvt_1_1_);
        Random lvt_2_1_ = new Random(1660L);

        for (int lvt_3_1_ = 0; lvt_3_1_ < this.tentacles.length; ++lvt_3_1_)
        {
            this.tentacles[lvt_3_1_] = new ModelRenderer(this, 0, 0);
            float lvt_4_1_ = (((float)(lvt_3_1_ % 3) - (float)(lvt_3_1_ / 3 % 2) * 0.5F + 0.25F) / 2.0F * 2.0F - 1.0F) * 5.0F;
            float lvt_5_1_ = ((float)(lvt_3_1_ / 3) / 2.0F * 2.0F - 1.0F) * 5.0F;
            int lvt_6_1_ = lvt_2_1_.nextInt(7) + 8;
            this.tentacles[lvt_3_1_].addBox(-1.0F, 0.0F, -1.0F, 2, lvt_6_1_, 2);
            this.tentacles[lvt_3_1_].rotationPointX = lvt_4_1_;
            this.tentacles[lvt_3_1_].rotationPointZ = lvt_5_1_;
            this.tentacles[lvt_3_1_].rotationPointY = (float)(31 + lvt_1_1_);
        }
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        for (int lvt_8_1_ = 0; lvt_8_1_ < this.tentacles.length; ++lvt_8_1_)
        {
            this.tentacles[lvt_8_1_].rotateAngleX = 0.2F * MathHelper.sin(ageInTicks * 0.3F + (float)lvt_8_1_) + 0.4F;
        }
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale)
    {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entityIn);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.6F, 0.0F);
        this.body.render(scale);

        for (ModelRenderer lvt_11_1_ : this.tentacles)
        {
            lvt_11_1_.render(scale);
        }

        GlStateManager.popMatrix();
    }
}
