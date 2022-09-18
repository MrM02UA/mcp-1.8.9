package net.minecraft.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;

public class RenderFireball extends Render<EntityFireball>
{
    private float scale;

    public RenderFireball(RenderManager renderManagerIn, float scaleIn)
    {
        super(renderManagerIn);
        this.scale = scaleIn;
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityFireball entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        GlStateManager.pushMatrix();
        this.bindEntityTexture(entity);
        GlStateManager.translate((float)x, (float)y, (float)z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(this.scale, this.scale, this.scale);
        TextureAtlasSprite lvt_10_1_ = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(Items.fire_charge);
        Tessellator lvt_11_1_ = Tessellator.getInstance();
        WorldRenderer lvt_12_1_ = lvt_11_1_.getWorldRenderer();
        float lvt_13_1_ = lvt_10_1_.getMinU();
        float lvt_14_1_ = lvt_10_1_.getMaxU();
        float lvt_15_1_ = lvt_10_1_.getMinV();
        float lvt_16_1_ = lvt_10_1_.getMaxV();
        float lvt_17_1_ = 1.0F;
        float lvt_18_1_ = 0.5F;
        float lvt_19_1_ = 0.25F;
        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        lvt_12_1_.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        lvt_12_1_.pos(-0.5D, -0.25D, 0.0D).tex((double)lvt_13_1_, (double)lvt_16_1_).normal(0.0F, 1.0F, 0.0F).endVertex();
        lvt_12_1_.pos(0.5D, -0.25D, 0.0D).tex((double)lvt_14_1_, (double)lvt_16_1_).normal(0.0F, 1.0F, 0.0F).endVertex();
        lvt_12_1_.pos(0.5D, 0.75D, 0.0D).tex((double)lvt_14_1_, (double)lvt_15_1_).normal(0.0F, 1.0F, 0.0F).endVertex();
        lvt_12_1_.pos(-0.5D, 0.75D, 0.0D).tex((double)lvt_13_1_, (double)lvt_15_1_).normal(0.0F, 1.0F, 0.0F).endVertex();
        lvt_11_1_.draw();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityFireball entity)
    {
        return TextureMap.locationBlocksTexture;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return this.getEntityTexture((EntityFireball)entity);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.doRender((EntityFireball)entity, x, y, z, entityYaw, partialTicks);
    }
}
