package net.minecraft.client.model;

import net.minecraft.entity.Entity;

public class ModelBoat extends ModelBase
{
    public ModelRenderer[] boatSides = new ModelRenderer[5];

    public ModelBoat()
    {
        this.boatSides[0] = new ModelRenderer(this, 0, 8);
        this.boatSides[1] = new ModelRenderer(this, 0, 0);
        this.boatSides[2] = new ModelRenderer(this, 0, 0);
        this.boatSides[3] = new ModelRenderer(this, 0, 0);
        this.boatSides[4] = new ModelRenderer(this, 0, 0);
        int lvt_1_1_ = 24;
        int lvt_2_1_ = 6;
        int lvt_3_1_ = 20;
        int lvt_4_1_ = 4;
        this.boatSides[0].addBox((float)(-lvt_1_1_ / 2), (float)(-lvt_3_1_ / 2 + 2), -3.0F, lvt_1_1_, lvt_3_1_ - 4, 4, 0.0F);
        this.boatSides[0].setRotationPoint(0.0F, (float)lvt_4_1_, 0.0F);
        this.boatSides[1].addBox((float)(-lvt_1_1_ / 2 + 2), (float)(-lvt_2_1_ - 1), -1.0F, lvt_1_1_ - 4, lvt_2_1_, 2, 0.0F);
        this.boatSides[1].setRotationPoint((float)(-lvt_1_1_ / 2 + 1), (float)lvt_4_1_, 0.0F);
        this.boatSides[2].addBox((float)(-lvt_1_1_ / 2 + 2), (float)(-lvt_2_1_ - 1), -1.0F, lvt_1_1_ - 4, lvt_2_1_, 2, 0.0F);
        this.boatSides[2].setRotationPoint((float)(lvt_1_1_ / 2 - 1), (float)lvt_4_1_, 0.0F);
        this.boatSides[3].addBox((float)(-lvt_1_1_ / 2 + 2), (float)(-lvt_2_1_ - 1), -1.0F, lvt_1_1_ - 4, lvt_2_1_, 2, 0.0F);
        this.boatSides[3].setRotationPoint(0.0F, (float)lvt_4_1_, (float)(-lvt_3_1_ / 2 + 1));
        this.boatSides[4].addBox((float)(-lvt_1_1_ / 2 + 2), (float)(-lvt_2_1_ - 1), -1.0F, lvt_1_1_ - 4, lvt_2_1_, 2, 0.0F);
        this.boatSides[4].setRotationPoint(0.0F, (float)lvt_4_1_, (float)(lvt_3_1_ / 2 - 1));
        this.boatSides[0].rotateAngleX = ((float)Math.PI / 2F);
        this.boatSides[1].rotateAngleY = ((float)Math.PI * 3F / 2F);
        this.boatSides[2].rotateAngleY = ((float)Math.PI / 2F);
        this.boatSides[3].rotateAngleY = (float)Math.PI;
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale)
    {
        for (int lvt_8_1_ = 0; lvt_8_1_ < 5; ++lvt_8_1_)
        {
            this.boatSides[lvt_8_1_].render(scale);
        }
    }
}
