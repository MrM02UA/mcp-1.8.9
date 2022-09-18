package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMagmaCube;

public class ModelMagmaCube extends ModelBase
{
    ModelRenderer[] segments = new ModelRenderer[8];
    ModelRenderer core;

    public ModelMagmaCube()
    {
        for (int lvt_1_1_ = 0; lvt_1_1_ < this.segments.length; ++lvt_1_1_)
        {
            int lvt_2_1_ = 0;
            int lvt_3_1_ = lvt_1_1_;

            if (lvt_1_1_ == 2)
            {
                lvt_2_1_ = 24;
                lvt_3_1_ = 10;
            }
            else if (lvt_1_1_ == 3)
            {
                lvt_2_1_ = 24;
                lvt_3_1_ = 19;
            }

            this.segments[lvt_1_1_] = new ModelRenderer(this, lvt_2_1_, lvt_3_1_);
            this.segments[lvt_1_1_].addBox(-4.0F, (float)(16 + lvt_1_1_), -4.0F, 8, 1, 8);
        }

        this.core = new ModelRenderer(this, 0, 16);
        this.core.addBox(-2.0F, 18.0F, -2.0F, 4, 4, 4);
    }

    /**
     * Used for easily adding entity-dependent animations. The second and third float params here are the same second
     * and third as in the setRotationAngles method.
     */
    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float p_78086_2_, float p_78086_3_, float partialTickTime)
    {
        EntityMagmaCube lvt_5_1_ = (EntityMagmaCube)entitylivingbaseIn;
        float lvt_6_1_ = lvt_5_1_.prevSquishFactor + (lvt_5_1_.squishFactor - lvt_5_1_.prevSquishFactor) * partialTickTime;

        if (lvt_6_1_ < 0.0F)
        {
            lvt_6_1_ = 0.0F;
        }

        for (int lvt_7_1_ = 0; lvt_7_1_ < this.segments.length; ++lvt_7_1_)
        {
            this.segments[lvt_7_1_].rotationPointY = (float)(-(4 - lvt_7_1_)) * lvt_6_1_ * 1.7F;
        }
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale)
    {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entityIn);
        this.core.render(scale);

        for (int lvt_8_1_ = 0; lvt_8_1_ < this.segments.length; ++lvt_8_1_)
        {
            this.segments[lvt_8_1_].render(scale);
        }
    }
}
