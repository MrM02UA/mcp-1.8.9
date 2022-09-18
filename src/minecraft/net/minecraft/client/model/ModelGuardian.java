package net.minecraft.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class ModelGuardian extends ModelBase
{
    private ModelRenderer guardianBody;
    private ModelRenderer guardianEye;
    private ModelRenderer[] guardianSpines;
    private ModelRenderer[] guardianTail;

    public ModelGuardian()
    {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.guardianSpines = new ModelRenderer[12];
        this.guardianBody = new ModelRenderer(this);
        this.guardianBody.setTextureOffset(0, 0).addBox(-6.0F, 10.0F, -8.0F, 12, 12, 16);
        this.guardianBody.setTextureOffset(0, 28).addBox(-8.0F, 10.0F, -6.0F, 2, 12, 12);
        this.guardianBody.setTextureOffset(0, 28).addBox(6.0F, 10.0F, -6.0F, 2, 12, 12, true);
        this.guardianBody.setTextureOffset(16, 40).addBox(-6.0F, 8.0F, -6.0F, 12, 2, 12);
        this.guardianBody.setTextureOffset(16, 40).addBox(-6.0F, 22.0F, -6.0F, 12, 2, 12);

        for (int lvt_1_1_ = 0; lvt_1_1_ < this.guardianSpines.length; ++lvt_1_1_)
        {
            this.guardianSpines[lvt_1_1_] = new ModelRenderer(this, 0, 0);
            this.guardianSpines[lvt_1_1_].addBox(-1.0F, -4.5F, -1.0F, 2, 9, 2);
            this.guardianBody.addChild(this.guardianSpines[lvt_1_1_]);
        }

        this.guardianEye = new ModelRenderer(this, 8, 0);
        this.guardianEye.addBox(-1.0F, 15.0F, 0.0F, 2, 2, 1);
        this.guardianBody.addChild(this.guardianEye);
        this.guardianTail = new ModelRenderer[3];
        this.guardianTail[0] = new ModelRenderer(this, 40, 0);
        this.guardianTail[0].addBox(-2.0F, 14.0F, 7.0F, 4, 4, 8);
        this.guardianTail[1] = new ModelRenderer(this, 0, 54);
        this.guardianTail[1].addBox(0.0F, 14.0F, 0.0F, 3, 3, 7);
        this.guardianTail[2] = new ModelRenderer(this);
        this.guardianTail[2].setTextureOffset(41, 32).addBox(0.0F, 14.0F, 0.0F, 2, 2, 6);
        this.guardianTail[2].setTextureOffset(25, 19).addBox(1.0F, 10.5F, 3.0F, 1, 9, 9);
        this.guardianBody.addChild(this.guardianTail[0]);
        this.guardianTail[0].addChild(this.guardianTail[1]);
        this.guardianTail[1].addChild(this.guardianTail[2]);
    }

    public int func_178706_a()
    {
        return 54;
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale)
    {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entityIn);
        this.guardianBody.render(scale);
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        EntityGuardian lvt_8_1_ = (EntityGuardian)entityIn;
        float lvt_9_1_ = ageInTicks - (float)lvt_8_1_.ticksExisted;
        this.guardianBody.rotateAngleY = netHeadYaw / (180F / (float)Math.PI);
        this.guardianBody.rotateAngleX = headPitch / (180F / (float)Math.PI);
        float[] lvt_10_1_ = new float[] {1.75F, 0.25F, 0.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 1.25F, 0.75F, 0.0F, 0.0F};
        float[] lvt_11_1_ = new float[] {0.0F, 0.0F, 0.0F, 0.0F, 0.25F, 1.75F, 1.25F, 0.75F, 0.0F, 0.0F, 0.0F, 0.0F};
        float[] lvt_12_1_ = new float[] {0.0F, 0.0F, 0.25F, 1.75F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.75F, 1.25F};
        float[] lvt_13_1_ = new float[] {0.0F, 0.0F, 8.0F, -8.0F, -8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F, 8.0F, -8.0F};
        float[] lvt_14_1_ = new float[] { -8.0F, -8.0F, -8.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 8.0F, 8.0F, 8.0F};
        float[] lvt_15_1_ = new float[] {8.0F, -8.0F, 0.0F, 0.0F, -8.0F, -8.0F, 8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F};
        float lvt_16_1_ = (1.0F - lvt_8_1_.func_175469_o(lvt_9_1_)) * 0.55F;

        for (int lvt_17_1_ = 0; lvt_17_1_ < 12; ++lvt_17_1_)
        {
            this.guardianSpines[lvt_17_1_].rotateAngleX = (float)Math.PI * lvt_10_1_[lvt_17_1_];
            this.guardianSpines[lvt_17_1_].rotateAngleY = (float)Math.PI * lvt_11_1_[lvt_17_1_];
            this.guardianSpines[lvt_17_1_].rotateAngleZ = (float)Math.PI * lvt_12_1_[lvt_17_1_];
            this.guardianSpines[lvt_17_1_].rotationPointX = lvt_13_1_[lvt_17_1_] * (1.0F + MathHelper.cos(ageInTicks * 1.5F + (float)lvt_17_1_) * 0.01F - lvt_16_1_);
            this.guardianSpines[lvt_17_1_].rotationPointY = 16.0F + lvt_14_1_[lvt_17_1_] * (1.0F + MathHelper.cos(ageInTicks * 1.5F + (float)lvt_17_1_) * 0.01F - lvt_16_1_);
            this.guardianSpines[lvt_17_1_].rotationPointZ = lvt_15_1_[lvt_17_1_] * (1.0F + MathHelper.cos(ageInTicks * 1.5F + (float)lvt_17_1_) * 0.01F - lvt_16_1_);
        }

        this.guardianEye.rotationPointZ = -8.25F;
        Entity lvt_17_2_ = Minecraft.getMinecraft().getRenderViewEntity();

        if (lvt_8_1_.hasTargetedEntity())
        {
            lvt_17_2_ = lvt_8_1_.getTargetedEntity();
        }

        if (lvt_17_2_ != null)
        {
            Vec3 lvt_18_1_ = lvt_17_2_.getPositionEyes(0.0F);
            Vec3 lvt_19_1_ = entityIn.getPositionEyes(0.0F);
            double lvt_20_1_ = lvt_18_1_.yCoord - lvt_19_1_.yCoord;

            if (lvt_20_1_ > 0.0D)
            {
                this.guardianEye.rotationPointY = 0.0F;
            }
            else
            {
                this.guardianEye.rotationPointY = 1.0F;
            }

            Vec3 lvt_22_1_ = entityIn.getLook(0.0F);
            lvt_22_1_ = new Vec3(lvt_22_1_.xCoord, 0.0D, lvt_22_1_.zCoord);
            Vec3 lvt_23_1_ = (new Vec3(lvt_19_1_.xCoord - lvt_18_1_.xCoord, 0.0D, lvt_19_1_.zCoord - lvt_18_1_.zCoord)).normalize().rotateYaw(((float)Math.PI / 2F));
            double lvt_24_1_ = lvt_22_1_.dotProduct(lvt_23_1_);
            this.guardianEye.rotationPointX = MathHelper.sqrt_float((float)Math.abs(lvt_24_1_)) * 2.0F * (float)Math.signum(lvt_24_1_);
        }

        this.guardianEye.showModel = true;
        float lvt_18_2_ = lvt_8_1_.func_175471_a(lvt_9_1_);
        this.guardianTail[0].rotateAngleY = MathHelper.sin(lvt_18_2_) * (float)Math.PI * 0.05F;
        this.guardianTail[1].rotateAngleY = MathHelper.sin(lvt_18_2_) * (float)Math.PI * 0.1F;
        this.guardianTail[1].rotationPointX = -1.5F;
        this.guardianTail[1].rotationPointY = 0.5F;
        this.guardianTail[1].rotationPointZ = 14.0F;
        this.guardianTail[2].rotateAngleY = MathHelper.sin(lvt_18_2_) * (float)Math.PI * 0.15F;
        this.guardianTail[2].rotationPointX = 0.5F;
        this.guardianTail[2].rotationPointY = 0.5F;
        this.guardianTail[2].rotationPointZ = 6.0F;
    }
}
