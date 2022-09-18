package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelCreeper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerCreeperCharge;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderCreeper extends RenderLiving<EntityCreeper>
{
    private static final ResourceLocation creeperTextures = new ResourceLocation("textures/entity/creeper/creeper.png");

    public RenderCreeper(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelCreeper(), 0.5F);
        this.addLayer(new LayerCreeperCharge(this));
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(EntityCreeper entitylivingbaseIn, float partialTickTime)
    {
        float lvt_3_1_ = entitylivingbaseIn.getCreeperFlashIntensity(partialTickTime);
        float lvt_4_1_ = 1.0F + MathHelper.sin(lvt_3_1_ * 100.0F) * lvt_3_1_ * 0.01F;
        lvt_3_1_ = MathHelper.clamp_float(lvt_3_1_, 0.0F, 1.0F);
        lvt_3_1_ = lvt_3_1_ * lvt_3_1_;
        lvt_3_1_ = lvt_3_1_ * lvt_3_1_;
        float lvt_5_1_ = (1.0F + lvt_3_1_ * 0.4F) * lvt_4_1_;
        float lvt_6_1_ = (1.0F + lvt_3_1_ * 0.1F) / lvt_4_1_;
        GlStateManager.scale(lvt_5_1_, lvt_6_1_, lvt_5_1_);
    }

    /**
     * Returns an ARGB int color back. Args: entityLiving, lightBrightness, partialTickTime
     */
    protected int getColorMultiplier(EntityCreeper entitylivingbaseIn, float lightBrightness, float partialTickTime)
    {
        float lvt_4_1_ = entitylivingbaseIn.getCreeperFlashIntensity(partialTickTime);

        if ((int)(lvt_4_1_ * 10.0F) % 2 == 0)
        {
            return 0;
        }
        else
        {
            int lvt_5_1_ = (int)(lvt_4_1_ * 0.2F * 255.0F);
            lvt_5_1_ = MathHelper.clamp_int(lvt_5_1_, 0, 255);
            return lvt_5_1_ << 24 | 16777215;
        }
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityCreeper entity)
    {
        return creeperTextures;
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(EntityLivingBase entitylivingbaseIn, float partialTickTime)
    {
        this.preRenderCallback((EntityCreeper)entitylivingbaseIn, partialTickTime);
    }

    /**
     * Returns an ARGB int color back. Args: entityLiving, lightBrightness, partialTickTime
     */
    protected int getColorMultiplier(EntityLivingBase entitylivingbaseIn, float lightBrightness, float partialTickTime)
    {
        return this.getColorMultiplier((EntityCreeper)entitylivingbaseIn, lightBrightness, partialTickTime);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return this.getEntityTexture((EntityCreeper)entity);
    }
}
