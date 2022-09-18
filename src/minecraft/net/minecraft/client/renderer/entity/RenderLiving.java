package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

public abstract class RenderLiving<T extends EntityLiving> extends RendererLivingEntity<T>
{
    public RenderLiving(RenderManager rendermanagerIn, ModelBase modelbaseIn, float shadowsizeIn)
    {
        super(rendermanagerIn, modelbaseIn, shadowsizeIn);
    }

    protected boolean canRenderName(T entity)
    {
        return super.canRenderName(entity) && (entity.getAlwaysRenderNameTagForRender() || entity.hasCustomName() && entity == this.renderManager.pointedEntity);
    }

    public boolean shouldRender(T livingEntity, ICamera camera, double camX, double camY, double camZ)
    {
        if (super.shouldRender(livingEntity, camera, camX, camY, camZ))
        {
            return true;
        }
        else if (livingEntity.getLeashed() && livingEntity.getLeashedToEntity() != null)
        {
            Entity lvt_9_1_ = livingEntity.getLeashedToEntity();
            return camera.isBoundingBoxInFrustum(lvt_9_1_.getEntityBoundingBox());
        }
        else
        {
            return false;
        }
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        this.renderLeash(entity, x, y, z, entityYaw, partialTicks);
    }

    public void setLightmap(T entityLivingIn, float partialTicks)
    {
        int lvt_3_1_ = entityLivingIn.getBrightnessForRender(partialTicks);
        int lvt_4_1_ = lvt_3_1_ % 65536;
        int lvt_5_1_ = lvt_3_1_ / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)lvt_4_1_ / 1.0F, (float)lvt_5_1_ / 1.0F);
    }

    /**
     * Gets the value between start and end according to pct
     */
    private double interpolateValue(double start, double end, double pct)
    {
        return start + (end - start) * pct;
    }

    protected void renderLeash(T entityLivingIn, double x, double y, double z, float entityYaw, float partialTicks)
    {
        Entity lvt_10_1_ = entityLivingIn.getLeashedToEntity();

        if (lvt_10_1_ != null)
        {
            y = y - (1.6D - (double)entityLivingIn.height) * 0.5D;
            Tessellator lvt_11_1_ = Tessellator.getInstance();
            WorldRenderer lvt_12_1_ = lvt_11_1_.getWorldRenderer();
            double lvt_13_1_ = this.interpolateValue((double)lvt_10_1_.prevRotationYaw, (double)lvt_10_1_.rotationYaw, (double)(partialTicks * 0.5F)) * 0.01745329238474369D;
            double lvt_15_1_ = this.interpolateValue((double)lvt_10_1_.prevRotationPitch, (double)lvt_10_1_.rotationPitch, (double)(partialTicks * 0.5F)) * 0.01745329238474369D;
            double lvt_17_1_ = Math.cos(lvt_13_1_);
            double lvt_19_1_ = Math.sin(lvt_13_1_);
            double lvt_21_1_ = Math.sin(lvt_15_1_);

            if (lvt_10_1_ instanceof EntityHanging)
            {
                lvt_17_1_ = 0.0D;
                lvt_19_1_ = 0.0D;
                lvt_21_1_ = -1.0D;
            }

            double lvt_23_1_ = Math.cos(lvt_15_1_);
            double lvt_25_1_ = this.interpolateValue(lvt_10_1_.prevPosX, lvt_10_1_.posX, (double)partialTicks) - lvt_17_1_ * 0.7D - lvt_19_1_ * 0.5D * lvt_23_1_;
            double lvt_27_1_ = this.interpolateValue(lvt_10_1_.prevPosY + (double)lvt_10_1_.getEyeHeight() * 0.7D, lvt_10_1_.posY + (double)lvt_10_1_.getEyeHeight() * 0.7D, (double)partialTicks) - lvt_21_1_ * 0.5D - 0.25D;
            double lvt_29_1_ = this.interpolateValue(lvt_10_1_.prevPosZ, lvt_10_1_.posZ, (double)partialTicks) - lvt_19_1_ * 0.7D + lvt_17_1_ * 0.5D * lvt_23_1_;
            double lvt_31_1_ = this.interpolateValue((double)entityLivingIn.prevRenderYawOffset, (double)entityLivingIn.renderYawOffset, (double)partialTicks) * 0.01745329238474369D + (Math.PI / 2D);
            lvt_17_1_ = Math.cos(lvt_31_1_) * (double)entityLivingIn.width * 0.4D;
            lvt_19_1_ = Math.sin(lvt_31_1_) * (double)entityLivingIn.width * 0.4D;
            double lvt_33_1_ = this.interpolateValue(entityLivingIn.prevPosX, entityLivingIn.posX, (double)partialTicks) + lvt_17_1_;
            double lvt_35_1_ = this.interpolateValue(entityLivingIn.prevPosY, entityLivingIn.posY, (double)partialTicks);
            double lvt_37_1_ = this.interpolateValue(entityLivingIn.prevPosZ, entityLivingIn.posZ, (double)partialTicks) + lvt_19_1_;
            x = x + lvt_17_1_;
            z = z + lvt_19_1_;
            double lvt_39_1_ = (double)((float)(lvt_25_1_ - lvt_33_1_));
            double lvt_41_1_ = (double)((float)(lvt_27_1_ - lvt_35_1_));
            double lvt_43_1_ = (double)((float)(lvt_29_1_ - lvt_37_1_));
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            int lvt_45_1_ = 24;
            double lvt_46_1_ = 0.025D;
            lvt_12_1_.begin(5, DefaultVertexFormats.POSITION_COLOR);

            for (int lvt_48_1_ = 0; lvt_48_1_ <= 24; ++lvt_48_1_)
            {
                float lvt_49_1_ = 0.5F;
                float lvt_50_1_ = 0.4F;
                float lvt_51_1_ = 0.3F;

                if (lvt_48_1_ % 2 == 0)
                {
                    lvt_49_1_ *= 0.7F;
                    lvt_50_1_ *= 0.7F;
                    lvt_51_1_ *= 0.7F;
                }

                float lvt_52_1_ = (float)lvt_48_1_ / 24.0F;
                lvt_12_1_.pos(x + lvt_39_1_ * (double)lvt_52_1_ + 0.0D, y + lvt_41_1_ * (double)(lvt_52_1_ * lvt_52_1_ + lvt_52_1_) * 0.5D + (double)((24.0F - (float)lvt_48_1_) / 18.0F + 0.125F), z + lvt_43_1_ * (double)lvt_52_1_).color(lvt_49_1_, lvt_50_1_, lvt_51_1_, 1.0F).endVertex();
                lvt_12_1_.pos(x + lvt_39_1_ * (double)lvt_52_1_ + 0.025D, y + lvt_41_1_ * (double)(lvt_52_1_ * lvt_52_1_ + lvt_52_1_) * 0.5D + (double)((24.0F - (float)lvt_48_1_) / 18.0F + 0.125F) + 0.025D, z + lvt_43_1_ * (double)lvt_52_1_).color(lvt_49_1_, lvt_50_1_, lvt_51_1_, 1.0F).endVertex();
            }

            lvt_11_1_.draw();
            lvt_12_1_.begin(5, DefaultVertexFormats.POSITION_COLOR);

            for (int lvt_48_2_ = 0; lvt_48_2_ <= 24; ++lvt_48_2_)
            {
                float lvt_49_2_ = 0.5F;
                float lvt_50_2_ = 0.4F;
                float lvt_51_2_ = 0.3F;

                if (lvt_48_2_ % 2 == 0)
                {
                    lvt_49_2_ *= 0.7F;
                    lvt_50_2_ *= 0.7F;
                    lvt_51_2_ *= 0.7F;
                }

                float lvt_52_2_ = (float)lvt_48_2_ / 24.0F;
                lvt_12_1_.pos(x + lvt_39_1_ * (double)lvt_52_2_ + 0.0D, y + lvt_41_1_ * (double)(lvt_52_2_ * lvt_52_2_ + lvt_52_2_) * 0.5D + (double)((24.0F - (float)lvt_48_2_) / 18.0F + 0.125F) + 0.025D, z + lvt_43_1_ * (double)lvt_52_2_).color(lvt_49_2_, lvt_50_2_, lvt_51_2_, 1.0F).endVertex();
                lvt_12_1_.pos(x + lvt_39_1_ * (double)lvt_52_2_ + 0.025D, y + lvt_41_1_ * (double)(lvt_52_2_ * lvt_52_2_ + lvt_52_2_) * 0.5D + (double)((24.0F - (float)lvt_48_2_) / 18.0F + 0.125F), z + lvt_43_1_ * (double)lvt_52_2_ + 0.025D).color(lvt_49_2_, lvt_50_2_, lvt_51_2_, 1.0F).endVertex();
            }

            lvt_11_1_.draw();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.enableCull();
        }
    }

    protected boolean canRenderName(EntityLivingBase entity)
    {
        return this.canRenderName((EntityLiving)entity);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.doRender((EntityLiving)entity, x, y, z, entityYaw, partialTicks);
    }

    protected boolean canRenderName(Entity entity)
    {
        return this.canRenderName((EntityLiving)entity);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.doRender((EntityLiving)entity, x, y, z, entityYaw, partialTicks);
    }

    public boolean shouldRender(Entity livingEntity, ICamera camera, double camX, double camY, double camZ)
    {
        return this.shouldRender((EntityLiving)livingEntity, camera, camX, camY, camZ);
    }
}
