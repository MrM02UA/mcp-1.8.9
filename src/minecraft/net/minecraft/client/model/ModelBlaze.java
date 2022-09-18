package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelBlaze extends ModelBase
{
    /** The sticks that fly around the Blaze. */
    private ModelRenderer[] blazeSticks = new ModelRenderer[12];
    private ModelRenderer blazeHead;

    public ModelBlaze()
    {
        for (int lvt_1_1_ = 0; lvt_1_1_ < this.blazeSticks.length; ++lvt_1_1_)
        {
            this.blazeSticks[lvt_1_1_] = new ModelRenderer(this, 0, 16);
            this.blazeSticks[lvt_1_1_].addBox(0.0F, 0.0F, 0.0F, 2, 8, 2);
        }

        this.blazeHead = new ModelRenderer(this, 0, 0);
        this.blazeHead.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale)
    {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entityIn);
        this.blazeHead.render(scale);

        for (int lvt_8_1_ = 0; lvt_8_1_ < this.blazeSticks.length; ++lvt_8_1_)
        {
            this.blazeSticks[lvt_8_1_].render(scale);
        }
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        float lvt_8_1_ = ageInTicks * (float)Math.PI * -0.1F;

        for (int lvt_9_1_ = 0; lvt_9_1_ < 4; ++lvt_9_1_)
        {
            this.blazeSticks[lvt_9_1_].rotationPointY = -2.0F + MathHelper.cos(((float)(lvt_9_1_ * 2) + ageInTicks) * 0.25F);
            this.blazeSticks[lvt_9_1_].rotationPointX = MathHelper.cos(lvt_8_1_) * 9.0F;
            this.blazeSticks[lvt_9_1_].rotationPointZ = MathHelper.sin(lvt_8_1_) * 9.0F;
            ++lvt_8_1_;
        }

        lvt_8_1_ = ((float)Math.PI / 4F) + ageInTicks * (float)Math.PI * 0.03F;

        for (int lvt_9_2_ = 4; lvt_9_2_ < 8; ++lvt_9_2_)
        {
            this.blazeSticks[lvt_9_2_].rotationPointY = 2.0F + MathHelper.cos(((float)(lvt_9_2_ * 2) + ageInTicks) * 0.25F);
            this.blazeSticks[lvt_9_2_].rotationPointX = MathHelper.cos(lvt_8_1_) * 7.0F;
            this.blazeSticks[lvt_9_2_].rotationPointZ = MathHelper.sin(lvt_8_1_) * 7.0F;
            ++lvt_8_1_;
        }

        lvt_8_1_ = 0.47123894F + ageInTicks * (float)Math.PI * -0.05F;

        for (int lvt_9_3_ = 8; lvt_9_3_ < 12; ++lvt_9_3_)
        {
            this.blazeSticks[lvt_9_3_].rotationPointY = 11.0F + MathHelper.cos(((float)lvt_9_3_ * 1.5F + ageInTicks) * 0.5F);
            this.blazeSticks[lvt_9_3_].rotationPointX = MathHelper.cos(lvt_8_1_) * 5.0F;
            this.blazeSticks[lvt_9_3_].rotationPointZ = MathHelper.sin(lvt_8_1_) * 5.0F;
            ++lvt_8_1_;
        }

        this.blazeHead.rotateAngleY = netHeadYaw / (180F / (float)Math.PI);
        this.blazeHead.rotateAngleX = headPitch / (180F / (float)Math.PI);
    }
}
