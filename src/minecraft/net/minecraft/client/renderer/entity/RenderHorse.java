package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.ResourceLocation;

public class RenderHorse extends RenderLiving<EntityHorse>
{
    private static final Map<String, ResourceLocation> field_110852_a = Maps.newHashMap();
    private static final ResourceLocation whiteHorseTextures = new ResourceLocation("textures/entity/horse/horse_white.png");
    private static final ResourceLocation muleTextures = new ResourceLocation("textures/entity/horse/mule.png");
    private static final ResourceLocation donkeyTextures = new ResourceLocation("textures/entity/horse/donkey.png");
    private static final ResourceLocation zombieHorseTextures = new ResourceLocation("textures/entity/horse/horse_zombie.png");
    private static final ResourceLocation skeletonHorseTextures = new ResourceLocation("textures/entity/horse/horse_skeleton.png");

    public RenderHorse(RenderManager rendermanagerIn, ModelHorse model, float shadowSizeIn)
    {
        super(rendermanagerIn, model, shadowSizeIn);
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(EntityHorse entitylivingbaseIn, float partialTickTime)
    {
        float lvt_3_1_ = 1.0F;
        int lvt_4_1_ = entitylivingbaseIn.getHorseType();

        if (lvt_4_1_ == 1)
        {
            lvt_3_1_ *= 0.87F;
        }
        else if (lvt_4_1_ == 2)
        {
            lvt_3_1_ *= 0.92F;
        }

        GlStateManager.scale(lvt_3_1_, lvt_3_1_, lvt_3_1_);
        super.preRenderCallback(entitylivingbaseIn, partialTickTime);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityHorse entity)
    {
        if (!entity.func_110239_cn())
        {
            switch (entity.getHorseType())
            {
                case 0:
                default:
                    return whiteHorseTextures;

                case 1:
                    return donkeyTextures;

                case 2:
                    return muleTextures;

                case 3:
                    return zombieHorseTextures;

                case 4:
                    return skeletonHorseTextures;
            }
        }
        else
        {
            return this.func_110848_b(entity);
        }
    }

    private ResourceLocation func_110848_b(EntityHorse horse)
    {
        String lvt_2_1_ = horse.getHorseTexture();

        if (!horse.func_175507_cI())
        {
            return null;
        }
        else
        {
            ResourceLocation lvt_3_1_ = (ResourceLocation)field_110852_a.get(lvt_2_1_);

            if (lvt_3_1_ == null)
            {
                lvt_3_1_ = new ResourceLocation(lvt_2_1_);
                Minecraft.getMinecraft().getTextureManager().loadTexture(lvt_3_1_, new LayeredTexture(horse.getVariantTexturePaths()));
                field_110852_a.put(lvt_2_1_, lvt_3_1_);
            }

            return lvt_3_1_;
        }
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(EntityLivingBase entitylivingbaseIn, float partialTickTime)
    {
        this.preRenderCallback((EntityHorse)entitylivingbaseIn, partialTickTime);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return this.getEntityTexture((EntityHorse)entity);
    }
}
