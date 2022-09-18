package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderXPOrb extends Render<EntityXPOrb>
{
    private static final ResourceLocation experienceOrbTextures = new ResourceLocation("textures/entity/experience_orb.png");

    public RenderXPOrb(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
        this.shadowSize = 0.15F;
        this.shadowOpaque = 0.75F;
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityXPOrb entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y, (float)z);
        this.bindEntityTexture(entity);
        int lvt_10_1_ = entity.getTextureByXP();
        float lvt_11_1_ = (float)(lvt_10_1_ % 4 * 16 + 0) / 64.0F;
        float lvt_12_1_ = (float)(lvt_10_1_ % 4 * 16 + 16) / 64.0F;
        float lvt_13_1_ = (float)(lvt_10_1_ / 4 * 16 + 0) / 64.0F;
        float lvt_14_1_ = (float)(lvt_10_1_ / 4 * 16 + 16) / 64.0F;
        float lvt_15_1_ = 1.0F;
        float lvt_16_1_ = 0.5F;
        float lvt_17_1_ = 0.25F;
        int lvt_18_1_ = entity.getBrightnessForRender(partialTicks);
        int lvt_19_1_ = lvt_18_1_ % 65536;
        int lvt_20_1_ = lvt_18_1_ / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)lvt_19_1_ / 1.0F, (float)lvt_20_1_ / 1.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        float lvt_18_2_ = 255.0F;
        float lvt_19_2_ = ((float)entity.xpColor + partialTicks) / 2.0F;
        lvt_20_1_ = (int)((MathHelper.sin(lvt_19_2_ + 0.0F) + 1.0F) * 0.5F * 255.0F);
        int lvt_21_1_ = 255;
        int lvt_22_1_ = (int)((MathHelper.sin(lvt_19_2_ + 4.1887903F) + 1.0F) * 0.1F * 255.0F);
        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        float lvt_23_1_ = 0.3F;
        GlStateManager.scale(0.3F, 0.3F, 0.3F);
        Tessellator lvt_24_1_ = Tessellator.getInstance();
        WorldRenderer lvt_25_1_ = lvt_24_1_.getWorldRenderer();
        lvt_25_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        lvt_25_1_.pos((double)(0.0F - lvt_16_1_), (double)(0.0F - lvt_17_1_), 0.0D).tex((double)lvt_11_1_, (double)lvt_14_1_).color(lvt_20_1_, 255, lvt_22_1_, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
        lvt_25_1_.pos((double)(lvt_15_1_ - lvt_16_1_), (double)(0.0F - lvt_17_1_), 0.0D).tex((double)lvt_12_1_, (double)lvt_14_1_).color(lvt_20_1_, 255, lvt_22_1_, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
        lvt_25_1_.pos((double)(lvt_15_1_ - lvt_16_1_), (double)(1.0F - lvt_17_1_), 0.0D).tex((double)lvt_12_1_, (double)lvt_13_1_).color(lvt_20_1_, 255, lvt_22_1_, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
        lvt_25_1_.pos((double)(0.0F - lvt_16_1_), (double)(1.0F - lvt_17_1_), 0.0D).tex((double)lvt_11_1_, (double)lvt_13_1_).color(lvt_20_1_, 255, lvt_22_1_, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
        lvt_24_1_.draw();
        GlStateManager.disableBlend();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityXPOrb entity)
    {
        return experienceOrbTextures;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return this.getEntityTexture((EntityXPOrb)entity);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.doRender((EntityXPOrb)entity, x, y, z, entityYaw, partialTicks);
    }
}
