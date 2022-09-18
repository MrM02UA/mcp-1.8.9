package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderArrow extends Render<EntityArrow>
{
    private static final ResourceLocation arrowTextures = new ResourceLocation("textures/entity/arrow.png");

    public RenderArrow(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityArrow entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.bindEntityTexture(entity);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y, (float)z);
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
        Tessellator lvt_10_1_ = Tessellator.getInstance();
        WorldRenderer lvt_11_1_ = lvt_10_1_.getWorldRenderer();
        int lvt_12_1_ = 0;
        float lvt_13_1_ = 0.0F;
        float lvt_14_1_ = 0.5F;
        float lvt_15_1_ = (float)(0 + lvt_12_1_ * 10) / 32.0F;
        float lvt_16_1_ = (float)(5 + lvt_12_1_ * 10) / 32.0F;
        float lvt_17_1_ = 0.0F;
        float lvt_18_1_ = 0.15625F;
        float lvt_19_1_ = (float)(5 + lvt_12_1_ * 10) / 32.0F;
        float lvt_20_1_ = (float)(10 + lvt_12_1_ * 10) / 32.0F;
        float lvt_21_1_ = 0.05625F;
        GlStateManager.enableRescaleNormal();
        float lvt_22_1_ = (float)entity.arrowShake - partialTicks;

        if (lvt_22_1_ > 0.0F)
        {
            float lvt_23_1_ = -MathHelper.sin(lvt_22_1_ * 3.0F) * lvt_22_1_;
            GlStateManager.rotate(lvt_23_1_, 0.0F, 0.0F, 1.0F);
        }

        GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(lvt_21_1_, lvt_21_1_, lvt_21_1_);
        GlStateManager.translate(-4.0F, 0.0F, 0.0F);
        GL11.glNormal3f(lvt_21_1_, 0.0F, 0.0F);
        lvt_11_1_.begin(7, DefaultVertexFormats.POSITION_TEX);
        lvt_11_1_.pos(-7.0D, -2.0D, -2.0D).tex((double)lvt_17_1_, (double)lvt_19_1_).endVertex();
        lvt_11_1_.pos(-7.0D, -2.0D, 2.0D).tex((double)lvt_18_1_, (double)lvt_19_1_).endVertex();
        lvt_11_1_.pos(-7.0D, 2.0D, 2.0D).tex((double)lvt_18_1_, (double)lvt_20_1_).endVertex();
        lvt_11_1_.pos(-7.0D, 2.0D, -2.0D).tex((double)lvt_17_1_, (double)lvt_20_1_).endVertex();
        lvt_10_1_.draw();
        GL11.glNormal3f(-lvt_21_1_, 0.0F, 0.0F);
        lvt_11_1_.begin(7, DefaultVertexFormats.POSITION_TEX);
        lvt_11_1_.pos(-7.0D, 2.0D, -2.0D).tex((double)lvt_17_1_, (double)lvt_19_1_).endVertex();
        lvt_11_1_.pos(-7.0D, 2.0D, 2.0D).tex((double)lvt_18_1_, (double)lvt_19_1_).endVertex();
        lvt_11_1_.pos(-7.0D, -2.0D, 2.0D).tex((double)lvt_18_1_, (double)lvt_20_1_).endVertex();
        lvt_11_1_.pos(-7.0D, -2.0D, -2.0D).tex((double)lvt_17_1_, (double)lvt_20_1_).endVertex();
        lvt_10_1_.draw();

        for (int lvt_23_2_ = 0; lvt_23_2_ < 4; ++lvt_23_2_)
        {
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glNormal3f(0.0F, 0.0F, lvt_21_1_);
            lvt_11_1_.begin(7, DefaultVertexFormats.POSITION_TEX);
            lvt_11_1_.pos(-8.0D, -2.0D, 0.0D).tex((double)lvt_13_1_, (double)lvt_15_1_).endVertex();
            lvt_11_1_.pos(8.0D, -2.0D, 0.0D).tex((double)lvt_14_1_, (double)lvt_15_1_).endVertex();
            lvt_11_1_.pos(8.0D, 2.0D, 0.0D).tex((double)lvt_14_1_, (double)lvt_16_1_).endVertex();
            lvt_11_1_.pos(-8.0D, 2.0D, 0.0D).tex((double)lvt_13_1_, (double)lvt_16_1_).endVertex();
            lvt_10_1_.draw();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityArrow entity)
    {
        return arrowTextures;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return this.getEntityTexture((EntityArrow)entity);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.doRender((EntityArrow)entity, x, y, z, entityYaw, partialTicks);
    }
}
