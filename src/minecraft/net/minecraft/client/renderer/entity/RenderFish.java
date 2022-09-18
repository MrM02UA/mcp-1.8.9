package net.minecraft.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class RenderFish extends Render<EntityFishHook>
{
    private static final ResourceLocation FISH_PARTICLES = new ResourceLocation("textures/particle/particles.png");

    public RenderFish(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityFishHook entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y, (float)z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        this.bindEntityTexture(entity);
        Tessellator lvt_10_1_ = Tessellator.getInstance();
        WorldRenderer lvt_11_1_ = lvt_10_1_.getWorldRenderer();
        int lvt_12_1_ = 1;
        int lvt_13_1_ = 2;
        float lvt_14_1_ = 0.0625F;
        float lvt_15_1_ = 0.125F;
        float lvt_16_1_ = 0.125F;
        float lvt_17_1_ = 0.1875F;
        float lvt_18_1_ = 1.0F;
        float lvt_19_1_ = 0.5F;
        float lvt_20_1_ = 0.5F;
        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        lvt_11_1_.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        lvt_11_1_.pos(-0.5D, -0.5D, 0.0D).tex(0.0625D, 0.1875D).normal(0.0F, 1.0F, 0.0F).endVertex();
        lvt_11_1_.pos(0.5D, -0.5D, 0.0D).tex(0.125D, 0.1875D).normal(0.0F, 1.0F, 0.0F).endVertex();
        lvt_11_1_.pos(0.5D, 0.5D, 0.0D).tex(0.125D, 0.125D).normal(0.0F, 1.0F, 0.0F).endVertex();
        lvt_11_1_.pos(-0.5D, 0.5D, 0.0D).tex(0.0625D, 0.125D).normal(0.0F, 1.0F, 0.0F).endVertex();
        lvt_10_1_.draw();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();

        if (entity.angler != null)
        {
            float lvt_21_1_ = entity.angler.getSwingProgress(partialTicks);
            float lvt_22_1_ = MathHelper.sin(MathHelper.sqrt_float(lvt_21_1_) * (float)Math.PI);
            Vec3 lvt_23_1_ = new Vec3(-0.36D, 0.03D, 0.35D);
            lvt_23_1_ = lvt_23_1_.rotatePitch(-(entity.angler.prevRotationPitch + (entity.angler.rotationPitch - entity.angler.prevRotationPitch) * partialTicks) * (float)Math.PI / 180.0F);
            lvt_23_1_ = lvt_23_1_.rotateYaw(-(entity.angler.prevRotationYaw + (entity.angler.rotationYaw - entity.angler.prevRotationYaw) * partialTicks) * (float)Math.PI / 180.0F);
            lvt_23_1_ = lvt_23_1_.rotateYaw(lvt_22_1_ * 0.5F);
            lvt_23_1_ = lvt_23_1_.rotatePitch(-lvt_22_1_ * 0.7F);
            double lvt_24_1_ = entity.angler.prevPosX + (entity.angler.posX - entity.angler.prevPosX) * (double)partialTicks + lvt_23_1_.xCoord;
            double lvt_26_1_ = entity.angler.prevPosY + (entity.angler.posY - entity.angler.prevPosY) * (double)partialTicks + lvt_23_1_.yCoord;
            double lvt_28_1_ = entity.angler.prevPosZ + (entity.angler.posZ - entity.angler.prevPosZ) * (double)partialTicks + lvt_23_1_.zCoord;
            double lvt_30_1_ = (double)entity.angler.getEyeHeight();

            if (this.renderManager.options != null && this.renderManager.options.thirdPersonView > 0 || entity.angler != Minecraft.getMinecraft().thePlayer)
            {
                float lvt_32_1_ = (entity.angler.prevRenderYawOffset + (entity.angler.renderYawOffset - entity.angler.prevRenderYawOffset) * partialTicks) * (float)Math.PI / 180.0F;
                double lvt_33_1_ = (double)MathHelper.sin(lvt_32_1_);
                double lvt_35_1_ = (double)MathHelper.cos(lvt_32_1_);
                double lvt_37_1_ = 0.35D;
                double lvt_39_1_ = 0.8D;
                lvt_24_1_ = entity.angler.prevPosX + (entity.angler.posX - entity.angler.prevPosX) * (double)partialTicks - lvt_35_1_ * 0.35D - lvt_33_1_ * 0.8D;
                lvt_26_1_ = entity.angler.prevPosY + lvt_30_1_ + (entity.angler.posY - entity.angler.prevPosY) * (double)partialTicks - 0.45D;
                lvt_28_1_ = entity.angler.prevPosZ + (entity.angler.posZ - entity.angler.prevPosZ) * (double)partialTicks - lvt_33_1_ * 0.35D + lvt_35_1_ * 0.8D;
                lvt_30_1_ = entity.angler.isSneaking() ? -0.1875D : 0.0D;
            }

            double lvt_32_2_ = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)partialTicks;
            double lvt_34_1_ = entity.prevPosY + (entity.posY - entity.prevPosY) * (double)partialTicks + 0.25D;
            double lvt_36_1_ = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)partialTicks;
            double lvt_38_1_ = (double)((float)(lvt_24_1_ - lvt_32_2_));
            double lvt_40_1_ = (double)((float)(lvt_26_1_ - lvt_34_1_)) + lvt_30_1_;
            double lvt_42_1_ = (double)((float)(lvt_28_1_ - lvt_36_1_));
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            lvt_11_1_.begin(3, DefaultVertexFormats.POSITION_COLOR);
            int lvt_44_1_ = 16;

            for (int lvt_45_1_ = 0; lvt_45_1_ <= 16; ++lvt_45_1_)
            {
                float lvt_46_1_ = (float)lvt_45_1_ / 16.0F;
                lvt_11_1_.pos(x + lvt_38_1_ * (double)lvt_46_1_, y + lvt_40_1_ * (double)(lvt_46_1_ * lvt_46_1_ + lvt_46_1_) * 0.5D + 0.25D, z + lvt_42_1_ * (double)lvt_46_1_).color(0, 0, 0, 255).endVertex();
            }

            lvt_10_1_.draw();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityFishHook entity)
    {
        return FISH_PARTICLES;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return this.getEntityTexture((EntityFishHook)entity);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.doRender((EntityFishHook)entity, x, y, z, entityYaw, partialTicks);
    }
}
