package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelIronGolem;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerIronGolemFlower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.util.ResourceLocation;

public class RenderIronGolem extends RenderLiving<EntityIronGolem>
{
    private static final ResourceLocation ironGolemTextures = new ResourceLocation("textures/entity/iron_golem.png");

    public RenderIronGolem(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelIronGolem(), 0.5F);
        this.addLayer(new LayerIronGolemFlower(this));
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityIronGolem entity)
    {
        return ironGolemTextures;
    }

    protected void rotateCorpse(EntityIronGolem bat, float p_77043_2_, float p_77043_3_, float partialTicks)
    {
        super.rotateCorpse(bat, p_77043_2_, p_77043_3_, partialTicks);

        if ((double)bat.limbSwingAmount >= 0.01D)
        {
            float lvt_5_1_ = 13.0F;
            float lvt_6_1_ = bat.limbSwing - bat.limbSwingAmount * (1.0F - partialTicks) + 6.0F;
            float lvt_7_1_ = (Math.abs(lvt_6_1_ % lvt_5_1_ - lvt_5_1_ * 0.5F) - lvt_5_1_ * 0.25F) / (lvt_5_1_ * 0.25F);
            GlStateManager.rotate(6.5F * lvt_7_1_, 0.0F, 0.0F, 1.0F);
        }
    }

    protected void rotateCorpse(EntityLivingBase bat, float p_77043_2_, float p_77043_3_, float partialTicks)
    {
        this.rotateCorpse((EntityIronGolem)bat, p_77043_2_, p_77043_3_, partialTicks);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return this.getEntityTexture((EntityIronGolem)entity);
    }
}
