package net.minecraft.client.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;

public class ModelDragon extends ModelBase
{
    /** The head Model renderer of the dragon */
    private ModelRenderer head;

    /** The spine Model renderer of the dragon */
    private ModelRenderer spine;

    /** The jaw Model renderer of the dragon */
    private ModelRenderer jaw;

    /** The body Model renderer of the dragon */
    private ModelRenderer body;

    /** The rear leg Model renderer of the dragon */
    private ModelRenderer rearLeg;

    /** The front leg Model renderer of the dragon */
    private ModelRenderer frontLeg;

    /** The rear leg tip Model renderer of the dragon */
    private ModelRenderer rearLegTip;

    /** The front leg tip Model renderer of the dragon */
    private ModelRenderer frontLegTip;

    /** The rear foot Model renderer of the dragon */
    private ModelRenderer rearFoot;

    /** The front foot Model renderer of the dragon */
    private ModelRenderer frontFoot;

    /** The wing Model renderer of the dragon */
    private ModelRenderer wing;

    /** The wing tip Model renderer of the dragon */
    private ModelRenderer wingTip;
    private float partialTicks;

    public ModelDragon(float p_i46360_1_)
    {
        this.textureWidth = 256;
        this.textureHeight = 256;
        this.setTextureOffset("body.body", 0, 0);
        this.setTextureOffset("wing.skin", -56, 88);
        this.setTextureOffset("wingtip.skin", -56, 144);
        this.setTextureOffset("rearleg.main", 0, 0);
        this.setTextureOffset("rearfoot.main", 112, 0);
        this.setTextureOffset("rearlegtip.main", 196, 0);
        this.setTextureOffset("head.upperhead", 112, 30);
        this.setTextureOffset("wing.bone", 112, 88);
        this.setTextureOffset("head.upperlip", 176, 44);
        this.setTextureOffset("jaw.jaw", 176, 65);
        this.setTextureOffset("frontleg.main", 112, 104);
        this.setTextureOffset("wingtip.bone", 112, 136);
        this.setTextureOffset("frontfoot.main", 144, 104);
        this.setTextureOffset("neck.box", 192, 104);
        this.setTextureOffset("frontlegtip.main", 226, 138);
        this.setTextureOffset("body.scale", 220, 53);
        this.setTextureOffset("head.scale", 0, 0);
        this.setTextureOffset("neck.scale", 48, 0);
        this.setTextureOffset("head.nostril", 112, 0);
        float lvt_2_1_ = -16.0F;
        this.head = new ModelRenderer(this, "head");
        this.head.addBox("upperlip", -6.0F, -1.0F, -8.0F + lvt_2_1_, 12, 5, 16);
        this.head.addBox("upperhead", -8.0F, -8.0F, 6.0F + lvt_2_1_, 16, 16, 16);
        this.head.mirror = true;
        this.head.addBox("scale", -5.0F, -12.0F, 12.0F + lvt_2_1_, 2, 4, 6);
        this.head.addBox("nostril", -5.0F, -3.0F, -6.0F + lvt_2_1_, 2, 2, 4);
        this.head.mirror = false;
        this.head.addBox("scale", 3.0F, -12.0F, 12.0F + lvt_2_1_, 2, 4, 6);
        this.head.addBox("nostril", 3.0F, -3.0F, -6.0F + lvt_2_1_, 2, 2, 4);
        this.jaw = new ModelRenderer(this, "jaw");
        this.jaw.setRotationPoint(0.0F, 4.0F, 8.0F + lvt_2_1_);
        this.jaw.addBox("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16);
        this.head.addChild(this.jaw);
        this.spine = new ModelRenderer(this, "neck");
        this.spine.addBox("box", -5.0F, -5.0F, -5.0F, 10, 10, 10);
        this.spine.addBox("scale", -1.0F, -9.0F, -3.0F, 2, 4, 6);
        this.body = new ModelRenderer(this, "body");
        this.body.setRotationPoint(0.0F, 4.0F, 8.0F);
        this.body.addBox("body", -12.0F, 0.0F, -16.0F, 24, 24, 64);
        this.body.addBox("scale", -1.0F, -6.0F, -10.0F, 2, 6, 12);
        this.body.addBox("scale", -1.0F, -6.0F, 10.0F, 2, 6, 12);
        this.body.addBox("scale", -1.0F, -6.0F, 30.0F, 2, 6, 12);
        this.wing = new ModelRenderer(this, "wing");
        this.wing.setRotationPoint(-12.0F, 5.0F, 2.0F);
        this.wing.addBox("bone", -56.0F, -4.0F, -4.0F, 56, 8, 8);
        this.wing.addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56);
        this.wingTip = new ModelRenderer(this, "wingtip");
        this.wingTip.setRotationPoint(-56.0F, 0.0F, 0.0F);
        this.wingTip.addBox("bone", -56.0F, -2.0F, -2.0F, 56, 4, 4);
        this.wingTip.addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56);
        this.wing.addChild(this.wingTip);
        this.frontLeg = new ModelRenderer(this, "frontleg");
        this.frontLeg.setRotationPoint(-12.0F, 20.0F, 2.0F);
        this.frontLeg.addBox("main", -4.0F, -4.0F, -4.0F, 8, 24, 8);
        this.frontLegTip = new ModelRenderer(this, "frontlegtip");
        this.frontLegTip.setRotationPoint(0.0F, 20.0F, -1.0F);
        this.frontLegTip.addBox("main", -3.0F, -1.0F, -3.0F, 6, 24, 6);
        this.frontLeg.addChild(this.frontLegTip);
        this.frontFoot = new ModelRenderer(this, "frontfoot");
        this.frontFoot.setRotationPoint(0.0F, 23.0F, 0.0F);
        this.frontFoot.addBox("main", -4.0F, 0.0F, -12.0F, 8, 4, 16);
        this.frontLegTip.addChild(this.frontFoot);
        this.rearLeg = new ModelRenderer(this, "rearleg");
        this.rearLeg.setRotationPoint(-16.0F, 16.0F, 42.0F);
        this.rearLeg.addBox("main", -8.0F, -4.0F, -8.0F, 16, 32, 16);
        this.rearLegTip = new ModelRenderer(this, "rearlegtip");
        this.rearLegTip.setRotationPoint(0.0F, 32.0F, -4.0F);
        this.rearLegTip.addBox("main", -6.0F, -2.0F, 0.0F, 12, 32, 12);
        this.rearLeg.addChild(this.rearLegTip);
        this.rearFoot = new ModelRenderer(this, "rearfoot");
        this.rearFoot.setRotationPoint(0.0F, 31.0F, 4.0F);
        this.rearFoot.addBox("main", -9.0F, 0.0F, -20.0F, 18, 6, 24);
        this.rearLegTip.addChild(this.rearFoot);
    }

    /**
     * Used for easily adding entity-dependent animations. The second and third float params here are the same second
     * and third as in the setRotationAngles method.
     */
    public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float p_78086_2_, float p_78086_3_, float partialTickTime)
    {
        this.partialTicks = partialTickTime;
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale)
    {
        GlStateManager.pushMatrix();
        EntityDragon lvt_8_1_ = (EntityDragon)entityIn;
        float lvt_9_1_ = lvt_8_1_.prevAnimTime + (lvt_8_1_.animTime - lvt_8_1_.prevAnimTime) * this.partialTicks;
        this.jaw.rotateAngleX = (float)(Math.sin((double)(lvt_9_1_ * (float)Math.PI * 2.0F)) + 1.0D) * 0.2F;
        float lvt_10_1_ = (float)(Math.sin((double)(lvt_9_1_ * (float)Math.PI * 2.0F - 1.0F)) + 1.0D);
        lvt_10_1_ = (lvt_10_1_ * lvt_10_1_ * 1.0F + lvt_10_1_ * 2.0F) * 0.05F;
        GlStateManager.translate(0.0F, lvt_10_1_ - 2.0F, -3.0F);
        GlStateManager.rotate(lvt_10_1_ * 2.0F, 1.0F, 0.0F, 0.0F);
        float lvt_11_1_ = -30.0F;
        float lvt_13_1_ = 0.0F;
        float lvt_14_1_ = 1.5F;
        double[] lvt_15_1_ = lvt_8_1_.getMovementOffsets(6, this.partialTicks);
        float lvt_16_1_ = this.updateRotations(lvt_8_1_.getMovementOffsets(5, this.partialTicks)[0] - lvt_8_1_.getMovementOffsets(10, this.partialTicks)[0]);
        float lvt_17_1_ = this.updateRotations(lvt_8_1_.getMovementOffsets(5, this.partialTicks)[0] + (double)(lvt_16_1_ / 2.0F));
        lvt_11_1_ = lvt_11_1_ + 2.0F;
        float lvt_18_1_ = lvt_9_1_ * (float)Math.PI * 2.0F;
        lvt_11_1_ = 20.0F;
        float lvt_12_1_ = -12.0F;

        for (int lvt_19_1_ = 0; lvt_19_1_ < 5; ++lvt_19_1_)
        {
            double[] lvt_20_1_ = lvt_8_1_.getMovementOffsets(5 - lvt_19_1_, this.partialTicks);
            float lvt_21_1_ = (float)Math.cos((double)((float)lvt_19_1_ * 0.45F + lvt_18_1_)) * 0.15F;
            this.spine.rotateAngleY = this.updateRotations(lvt_20_1_[0] - lvt_15_1_[0]) * (float)Math.PI / 180.0F * lvt_14_1_;
            this.spine.rotateAngleX = lvt_21_1_ + (float)(lvt_20_1_[1] - lvt_15_1_[1]) * (float)Math.PI / 180.0F * lvt_14_1_ * 5.0F;
            this.spine.rotateAngleZ = -this.updateRotations(lvt_20_1_[0] - (double)lvt_17_1_) * (float)Math.PI / 180.0F * lvt_14_1_;
            this.spine.rotationPointY = lvt_11_1_;
            this.spine.rotationPointZ = lvt_12_1_;
            this.spine.rotationPointX = lvt_13_1_;
            lvt_11_1_ = (float)((double)lvt_11_1_ + Math.sin((double)this.spine.rotateAngleX) * 10.0D);
            lvt_12_1_ = (float)((double)lvt_12_1_ - Math.cos((double)this.spine.rotateAngleY) * Math.cos((double)this.spine.rotateAngleX) * 10.0D);
            lvt_13_1_ = (float)((double)lvt_13_1_ - Math.sin((double)this.spine.rotateAngleY) * Math.cos((double)this.spine.rotateAngleX) * 10.0D);
            this.spine.render(scale);
        }

        this.head.rotationPointY = lvt_11_1_;
        this.head.rotationPointZ = lvt_12_1_;
        this.head.rotationPointX = lvt_13_1_;
        double[] lvt_19_2_ = lvt_8_1_.getMovementOffsets(0, this.partialTicks);
        this.head.rotateAngleY = this.updateRotations(lvt_19_2_[0] - lvt_15_1_[0]) * (float)Math.PI / 180.0F * 1.0F;
        this.head.rotateAngleZ = -this.updateRotations(lvt_19_2_[0] - (double)lvt_17_1_) * (float)Math.PI / 180.0F * 1.0F;
        this.head.render(scale);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-lvt_16_1_ * lvt_14_1_ * 1.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(0.0F, -1.0F, 0.0F);
        this.body.rotateAngleZ = 0.0F;
        this.body.render(scale);

        for (int lvt_20_2_ = 0; lvt_20_2_ < 2; ++lvt_20_2_)
        {
            GlStateManager.enableCull();
            float lvt_21_2_ = lvt_9_1_ * (float)Math.PI * 2.0F;
            this.wing.rotateAngleX = 0.125F - (float)Math.cos((double)lvt_21_2_) * 0.2F;
            this.wing.rotateAngleY = 0.25F;
            this.wing.rotateAngleZ = (float)(Math.sin((double)lvt_21_2_) + 0.125D) * 0.8F;
            this.wingTip.rotateAngleZ = -((float)(Math.sin((double)(lvt_21_2_ + 2.0F)) + 0.5D)) * 0.75F;
            this.rearLeg.rotateAngleX = 1.0F + lvt_10_1_ * 0.1F;
            this.rearLegTip.rotateAngleX = 0.5F + lvt_10_1_ * 0.1F;
            this.rearFoot.rotateAngleX = 0.75F + lvt_10_1_ * 0.1F;
            this.frontLeg.rotateAngleX = 1.3F + lvt_10_1_ * 0.1F;
            this.frontLegTip.rotateAngleX = -0.5F - lvt_10_1_ * 0.1F;
            this.frontFoot.rotateAngleX = 0.75F + lvt_10_1_ * 0.1F;
            this.wing.render(scale);
            this.frontLeg.render(scale);
            this.rearLeg.render(scale);
            GlStateManager.scale(-1.0F, 1.0F, 1.0F);

            if (lvt_20_2_ == 0)
            {
                GlStateManager.cullFace(1028);
            }
        }

        GlStateManager.popMatrix();
        GlStateManager.cullFace(1029);
        GlStateManager.disableCull();
        float lvt_20_3_ = -((float)Math.sin((double)(lvt_9_1_ * (float)Math.PI * 2.0F))) * 0.0F;
        lvt_18_1_ = lvt_9_1_ * (float)Math.PI * 2.0F;
        lvt_11_1_ = 10.0F;
        lvt_12_1_ = 60.0F;
        lvt_13_1_ = 0.0F;
        lvt_15_1_ = lvt_8_1_.getMovementOffsets(11, this.partialTicks);

        for (int lvt_21_3_ = 0; lvt_21_3_ < 12; ++lvt_21_3_)
        {
            lvt_19_2_ = lvt_8_1_.getMovementOffsets(12 + lvt_21_3_, this.partialTicks);
            lvt_20_3_ = (float)((double)lvt_20_3_ + Math.sin((double)((float)lvt_21_3_ * 0.45F + lvt_18_1_)) * 0.05000000074505806D);
            this.spine.rotateAngleY = (this.updateRotations(lvt_19_2_[0] - lvt_15_1_[0]) * lvt_14_1_ + 180.0F) * (float)Math.PI / 180.0F;
            this.spine.rotateAngleX = lvt_20_3_ + (float)(lvt_19_2_[1] - lvt_15_1_[1]) * (float)Math.PI / 180.0F * lvt_14_1_ * 5.0F;
            this.spine.rotateAngleZ = this.updateRotations(lvt_19_2_[0] - (double)lvt_17_1_) * (float)Math.PI / 180.0F * lvt_14_1_;
            this.spine.rotationPointY = lvt_11_1_;
            this.spine.rotationPointZ = lvt_12_1_;
            this.spine.rotationPointX = lvt_13_1_;
            lvt_11_1_ = (float)((double)lvt_11_1_ + Math.sin((double)this.spine.rotateAngleX) * 10.0D);
            lvt_12_1_ = (float)((double)lvt_12_1_ - Math.cos((double)this.spine.rotateAngleY) * Math.cos((double)this.spine.rotateAngleX) * 10.0D);
            lvt_13_1_ = (float)((double)lvt_13_1_ - Math.sin((double)this.spine.rotateAngleY) * Math.cos((double)this.spine.rotateAngleX) * 10.0D);
            this.spine.render(scale);
        }

        GlStateManager.popMatrix();
    }

    /**
     * Updates the rotations in the parameters for rotations greater than 180 degrees or less than -180 degrees. It adds
     * or subtracts 360 degrees, so that the appearance is the same, although the numbers are then simplified to range
     * -180 to 180
     */
    private float updateRotations(double p_78214_1_)
    {
        while (p_78214_1_ >= 180.0D)
        {
            p_78214_1_ -= 360.0D;
        }

        while (p_78214_1_ < -180.0D)
        {
            p_78214_1_ += 360.0D;
        }

        return (float)p_78214_1_;
    }
}
