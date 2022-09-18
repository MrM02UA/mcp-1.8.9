package net.minecraft.client.model;

import net.minecraft.entity.Entity;

public class ModelMinecart extends ModelBase
{
    public ModelRenderer[] sideModels = new ModelRenderer[7];

    public ModelMinecart()
    {
        this.sideModels[0] = new ModelRenderer(this, 0, 10);
        this.sideModels[1] = new ModelRenderer(this, 0, 0);
        this.sideModels[2] = new ModelRenderer(this, 0, 0);
        this.sideModels[3] = new ModelRenderer(this, 0, 0);
        this.sideModels[4] = new ModelRenderer(this, 0, 0);
        this.sideModels[5] = new ModelRenderer(this, 44, 10);
        int lvt_1_1_ = 20;
        int lvt_2_1_ = 8;
        int lvt_3_1_ = 16;
        int lvt_4_1_ = 4;
        this.sideModels[0].addBox((float)(-lvt_1_1_ / 2), (float)(-lvt_3_1_ / 2), -1.0F, lvt_1_1_, lvt_3_1_, 2, 0.0F);
        this.sideModels[0].setRotationPoint(0.0F, (float)lvt_4_1_, 0.0F);
        this.sideModels[5].addBox((float)(-lvt_1_1_ / 2 + 1), (float)(-lvt_3_1_ / 2 + 1), -1.0F, lvt_1_1_ - 2, lvt_3_1_ - 2, 1, 0.0F);
        this.sideModels[5].setRotationPoint(0.0F, (float)lvt_4_1_, 0.0F);
        this.sideModels[1].addBox((float)(-lvt_1_1_ / 2 + 2), (float)(-lvt_2_1_ - 1), -1.0F, lvt_1_1_ - 4, lvt_2_1_, 2, 0.0F);
        this.sideModels[1].setRotationPoint((float)(-lvt_1_1_ / 2 + 1), (float)lvt_4_1_, 0.0F);
        this.sideModels[2].addBox((float)(-lvt_1_1_ / 2 + 2), (float)(-lvt_2_1_ - 1), -1.0F, lvt_1_1_ - 4, lvt_2_1_, 2, 0.0F);
        this.sideModels[2].setRotationPoint((float)(lvt_1_1_ / 2 - 1), (float)lvt_4_1_, 0.0F);
        this.sideModels[3].addBox((float)(-lvt_1_1_ / 2 + 2), (float)(-lvt_2_1_ - 1), -1.0F, lvt_1_1_ - 4, lvt_2_1_, 2, 0.0F);
        this.sideModels[3].setRotationPoint(0.0F, (float)lvt_4_1_, (float)(-lvt_3_1_ / 2 + 1));
        this.sideModels[4].addBox((float)(-lvt_1_1_ / 2 + 2), (float)(-lvt_2_1_ - 1), -1.0F, lvt_1_1_ - 4, lvt_2_1_, 2, 0.0F);
        this.sideModels[4].setRotationPoint(0.0F, (float)lvt_4_1_, (float)(lvt_3_1_ / 2 - 1));
        this.sideModels[0].rotateAngleX = ((float)Math.PI / 2F);
        this.sideModels[1].rotateAngleY = ((float)Math.PI * 3F / 2F);
        this.sideModels[2].rotateAngleY = ((float)Math.PI / 2F);
        this.sideModels[3].rotateAngleY = (float)Math.PI;
        this.sideModels[5].rotateAngleX = -((float)Math.PI / 2F);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale)
    {
        this.sideModels[5].rotationPointY = 4.0F - p_78088_4_;

        for (int lvt_8_1_ = 0; lvt_8_1_ < 6; ++lvt_8_1_)
        {
            this.sideModels[lvt_8_1_].render(scale);
        }
    }
}
