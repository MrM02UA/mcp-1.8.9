package net.minecraft.client.renderer.entity.layers;

import java.util.Random;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;

public class LayerEnderDragonDeath implements LayerRenderer<EntityDragon>
{
    public void doRenderLayer(EntityDragon entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
    {
        if (entitylivingbaseIn.deathTicks > 0)
        {
            Tessellator lvt_9_1_ = Tessellator.getInstance();
            WorldRenderer lvt_10_1_ = lvt_9_1_.getWorldRenderer();
            RenderHelper.disableStandardItemLighting();
            float lvt_11_1_ = ((float)entitylivingbaseIn.deathTicks + partialTicks) / 200.0F;
            float lvt_12_1_ = 0.0F;

            if (lvt_11_1_ > 0.8F)
            {
                lvt_12_1_ = (lvt_11_1_ - 0.8F) / 0.2F;
            }

            Random lvt_13_1_ = new Random(432L);
            GlStateManager.disableTexture2D();
            GlStateManager.shadeModel(7425);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 1);
            GlStateManager.disableAlpha();
            GlStateManager.enableCull();
            GlStateManager.depthMask(false);
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, -1.0F, -2.0F);

            for (int lvt_14_1_ = 0; (float)lvt_14_1_ < (lvt_11_1_ + lvt_11_1_ * lvt_11_1_) / 2.0F * 60.0F; ++lvt_14_1_)
            {
                GlStateManager.rotate(lvt_13_1_.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(lvt_13_1_.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(lvt_13_1_.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(lvt_13_1_.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(lvt_13_1_.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(lvt_13_1_.nextFloat() * 360.0F + lvt_11_1_ * 90.0F, 0.0F, 0.0F, 1.0F);
                float lvt_15_1_ = lvt_13_1_.nextFloat() * 20.0F + 5.0F + lvt_12_1_ * 10.0F;
                float lvt_16_1_ = lvt_13_1_.nextFloat() * 2.0F + 1.0F + lvt_12_1_ * 2.0F;
                lvt_10_1_.begin(6, DefaultVertexFormats.POSITION_COLOR);
                lvt_10_1_.pos(0.0D, 0.0D, 0.0D).color(255, 255, 255, (int)(255.0F * (1.0F - lvt_12_1_))).endVertex();
                lvt_10_1_.pos(-0.866D * (double)lvt_16_1_, (double)lvt_15_1_, (double)(-0.5F * lvt_16_1_)).color(255, 0, 255, 0).endVertex();
                lvt_10_1_.pos(0.866D * (double)lvt_16_1_, (double)lvt_15_1_, (double)(-0.5F * lvt_16_1_)).color(255, 0, 255, 0).endVertex();
                lvt_10_1_.pos(0.0D, (double)lvt_15_1_, (double)(1.0F * lvt_16_1_)).color(255, 0, 255, 0).endVertex();
                lvt_10_1_.pos(-0.866D * (double)lvt_16_1_, (double)lvt_15_1_, (double)(-0.5F * lvt_16_1_)).color(255, 0, 255, 0).endVertex();
                lvt_9_1_.draw();
            }

            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
            GlStateManager.disableCull();
            GlStateManager.disableBlend();
            GlStateManager.shadeModel(7424);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableTexture2D();
            GlStateManager.enableAlpha();
            RenderHelper.enableStandardItemLighting();
        }
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }

    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
    {
        this.doRenderLayer((EntityDragon)entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale);
    }
}
