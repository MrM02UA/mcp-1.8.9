package net.minecraft.client.renderer.entity.layers;

import java.util.Random;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.MathHelper;

public class LayerArrow implements LayerRenderer<EntityLivingBase>
{
    private final RendererLivingEntity field_177168_a;

    public LayerArrow(RendererLivingEntity p_i46124_1_)
    {
        this.field_177168_a = p_i46124_1_;
    }

    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
    {
        int lvt_9_1_ = entitylivingbaseIn.getArrowCountInEntity();

        if (lvt_9_1_ > 0)
        {
            Entity lvt_10_1_ = new EntityArrow(entitylivingbaseIn.worldObj, entitylivingbaseIn.posX, entitylivingbaseIn.posY, entitylivingbaseIn.posZ);
            Random lvt_11_1_ = new Random((long)entitylivingbaseIn.getEntityId());
            RenderHelper.disableStandardItemLighting();

            for (int lvt_12_1_ = 0; lvt_12_1_ < lvt_9_1_; ++lvt_12_1_)
            {
                GlStateManager.pushMatrix();
                ModelRenderer lvt_13_1_ = this.field_177168_a.getMainModel().getRandomModelBox(lvt_11_1_);
                ModelBox lvt_14_1_ = (ModelBox)lvt_13_1_.cubeList.get(lvt_11_1_.nextInt(lvt_13_1_.cubeList.size()));
                lvt_13_1_.postRender(0.0625F);
                float lvt_15_1_ = lvt_11_1_.nextFloat();
                float lvt_16_1_ = lvt_11_1_.nextFloat();
                float lvt_17_1_ = lvt_11_1_.nextFloat();
                float lvt_18_1_ = (lvt_14_1_.posX1 + (lvt_14_1_.posX2 - lvt_14_1_.posX1) * lvt_15_1_) / 16.0F;
                float lvt_19_1_ = (lvt_14_1_.posY1 + (lvt_14_1_.posY2 - lvt_14_1_.posY1) * lvt_16_1_) / 16.0F;
                float lvt_20_1_ = (lvt_14_1_.posZ1 + (lvt_14_1_.posZ2 - lvt_14_1_.posZ1) * lvt_17_1_) / 16.0F;
                GlStateManager.translate(lvt_18_1_, lvt_19_1_, lvt_20_1_);
                lvt_15_1_ = lvt_15_1_ * 2.0F - 1.0F;
                lvt_16_1_ = lvt_16_1_ * 2.0F - 1.0F;
                lvt_17_1_ = lvt_17_1_ * 2.0F - 1.0F;
                lvt_15_1_ = lvt_15_1_ * -1.0F;
                lvt_16_1_ = lvt_16_1_ * -1.0F;
                lvt_17_1_ = lvt_17_1_ * -1.0F;
                float lvt_21_1_ = MathHelper.sqrt_float(lvt_15_1_ * lvt_15_1_ + lvt_17_1_ * lvt_17_1_);
                lvt_10_1_.prevRotationYaw = lvt_10_1_.rotationYaw = (float)(Math.atan2((double)lvt_15_1_, (double)lvt_17_1_) * 180.0D / Math.PI);
                lvt_10_1_.prevRotationPitch = lvt_10_1_.rotationPitch = (float)(Math.atan2((double)lvt_16_1_, (double)lvt_21_1_) * 180.0D / Math.PI);
                double lvt_22_1_ = 0.0D;
                double lvt_24_1_ = 0.0D;
                double lvt_26_1_ = 0.0D;
                this.field_177168_a.getRenderManager().renderEntityWithPosYaw(lvt_10_1_, lvt_22_1_, lvt_24_1_, lvt_26_1_, 0.0F, partialTicks);
                GlStateManager.popMatrix();
            }

            RenderHelper.enableStandardItemLighting();
        }
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }
}
