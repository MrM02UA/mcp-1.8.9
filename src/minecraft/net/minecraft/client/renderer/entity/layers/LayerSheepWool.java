package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.ModelSheep1;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderSheep;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;

public class LayerSheepWool implements LayerRenderer<EntitySheep>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/sheep/sheep_fur.png");
    private final RenderSheep sheepRenderer;
    private final ModelSheep1 sheepModel = new ModelSheep1();

    public LayerSheepWool(RenderSheep sheepRendererIn)
    {
        this.sheepRenderer = sheepRendererIn;
    }

    public void doRenderLayer(EntitySheep entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
    {
        if (!entitylivingbaseIn.getSheared() && !entitylivingbaseIn.isInvisible())
        {
            this.sheepRenderer.bindTexture(TEXTURE);

            if (entitylivingbaseIn.hasCustomName() && "jeb_".equals(entitylivingbaseIn.getCustomNameTag()))
            {
                int lvt_9_1_ = 25;
                int lvt_10_1_ = entitylivingbaseIn.ticksExisted / 25 + entitylivingbaseIn.getEntityId();
                int lvt_11_1_ = EnumDyeColor.values().length;
                int lvt_12_1_ = lvt_10_1_ % lvt_11_1_;
                int lvt_13_1_ = (lvt_10_1_ + 1) % lvt_11_1_;
                float lvt_14_1_ = ((float)(entitylivingbaseIn.ticksExisted % 25) + partialTicks) / 25.0F;
                float[] lvt_15_1_ = EntitySheep.getDyeRgb(EnumDyeColor.byMetadata(lvt_12_1_));
                float[] lvt_16_1_ = EntitySheep.getDyeRgb(EnumDyeColor.byMetadata(lvt_13_1_));
                GlStateManager.color(lvt_15_1_[0] * (1.0F - lvt_14_1_) + lvt_16_1_[0] * lvt_14_1_, lvt_15_1_[1] * (1.0F - lvt_14_1_) + lvt_16_1_[1] * lvt_14_1_, lvt_15_1_[2] * (1.0F - lvt_14_1_) + lvt_16_1_[2] * lvt_14_1_);
            }
            else
            {
                float[] lvt_9_2_ = EntitySheep.getDyeRgb(entitylivingbaseIn.getFleeceColor());
                GlStateManager.color(lvt_9_2_[0], lvt_9_2_[1], lvt_9_2_[2]);
            }

            this.sheepModel.setModelAttributes(this.sheepRenderer.getMainModel());
            this.sheepModel.setLivingAnimations(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks);
            this.sheepModel.render(entitylivingbaseIn, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, scale);
        }
    }

    public boolean shouldCombineTextures()
    {
        return true;
    }

    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
    {
        this.doRenderLayer((EntitySheep)entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale);
    }
}
