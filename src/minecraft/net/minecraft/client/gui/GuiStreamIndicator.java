package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class GuiStreamIndicator
{
    private static final ResourceLocation locationStreamIndicator = new ResourceLocation("textures/gui/stream_indicator.png");
    private final Minecraft mc;
    private float streamAlpha = 1.0F;
    private int streamAlphaDelta = 1;

    public GuiStreamIndicator(Minecraft mcIn)
    {
        this.mc = mcIn;
    }

    public void render(int p_152437_1_, int p_152437_2_)
    {
        if (this.mc.getTwitchStream().isBroadcasting())
        {
            GlStateManager.enableBlend();
            int lvt_3_1_ = this.mc.getTwitchStream().func_152920_A();

            if (lvt_3_1_ > 0)
            {
                String lvt_4_1_ = "" + lvt_3_1_;
                int lvt_5_1_ = this.mc.fontRendererObj.getStringWidth(lvt_4_1_);
                int lvt_6_1_ = 20;
                int lvt_7_1_ = p_152437_1_ - lvt_5_1_ - 1;
                int lvt_8_1_ = p_152437_2_ + 20 - 1;
                int lvt_10_1_ = p_152437_2_ + 20 + this.mc.fontRendererObj.FONT_HEIGHT - 1;
                GlStateManager.disableTexture2D();
                Tessellator lvt_11_1_ = Tessellator.getInstance();
                WorldRenderer lvt_12_1_ = lvt_11_1_.getWorldRenderer();
                GlStateManager.color(0.0F, 0.0F, 0.0F, (0.65F + 0.35000002F * this.streamAlpha) / 2.0F);
                lvt_12_1_.begin(7, DefaultVertexFormats.POSITION);
                lvt_12_1_.pos((double)lvt_7_1_, (double)lvt_10_1_, 0.0D).endVertex();
                lvt_12_1_.pos((double)p_152437_1_, (double)lvt_10_1_, 0.0D).endVertex();
                lvt_12_1_.pos((double)p_152437_1_, (double)lvt_8_1_, 0.0D).endVertex();
                lvt_12_1_.pos((double)lvt_7_1_, (double)lvt_8_1_, 0.0D).endVertex();
                lvt_11_1_.draw();
                GlStateManager.enableTexture2D();
                this.mc.fontRendererObj.drawString(lvt_4_1_, p_152437_1_ - lvt_5_1_, p_152437_2_ + 20, 16777215);
            }

            this.render(p_152437_1_, p_152437_2_, this.func_152440_b(), 0);
            this.render(p_152437_1_, p_152437_2_, this.func_152438_c(), 17);
        }
    }

    private void render(int p_152436_1_, int p_152436_2_, int p_152436_3_, int p_152436_4_)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.65F + 0.35000002F * this.streamAlpha);
        this.mc.getTextureManager().bindTexture(locationStreamIndicator);
        float lvt_5_1_ = 150.0F;
        float lvt_6_1_ = 0.0F;
        float lvt_7_1_ = (float)p_152436_3_ * 0.015625F;
        float lvt_8_1_ = 1.0F;
        float lvt_9_1_ = (float)(p_152436_3_ + 16) * 0.015625F;
        Tessellator lvt_10_1_ = Tessellator.getInstance();
        WorldRenderer lvt_11_1_ = lvt_10_1_.getWorldRenderer();
        lvt_11_1_.begin(7, DefaultVertexFormats.POSITION_TEX);
        lvt_11_1_.pos((double)(p_152436_1_ - 16 - p_152436_4_), (double)(p_152436_2_ + 16), (double)lvt_5_1_).tex((double)lvt_6_1_, (double)lvt_9_1_).endVertex();
        lvt_11_1_.pos((double)(p_152436_1_ - p_152436_4_), (double)(p_152436_2_ + 16), (double)lvt_5_1_).tex((double)lvt_8_1_, (double)lvt_9_1_).endVertex();
        lvt_11_1_.pos((double)(p_152436_1_ - p_152436_4_), (double)(p_152436_2_ + 0), (double)lvt_5_1_).tex((double)lvt_8_1_, (double)lvt_7_1_).endVertex();
        lvt_11_1_.pos((double)(p_152436_1_ - 16 - p_152436_4_), (double)(p_152436_2_ + 0), (double)lvt_5_1_).tex((double)lvt_6_1_, (double)lvt_7_1_).endVertex();
        lvt_10_1_.draw();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private int func_152440_b()
    {
        return this.mc.getTwitchStream().isPaused() ? 16 : 0;
    }

    private int func_152438_c()
    {
        return this.mc.getTwitchStream().func_152929_G() ? 48 : 32;
    }

    public void updateStreamAlpha()
    {
        if (this.mc.getTwitchStream().isBroadcasting())
        {
            this.streamAlpha += 0.025F * (float)this.streamAlphaDelta;

            if (this.streamAlpha < 0.0F)
            {
                this.streamAlphaDelta *= -1;
                this.streamAlpha = 0.0F;
            }
            else if (this.streamAlpha > 1.0F)
            {
                this.streamAlphaDelta *= -1;
                this.streamAlpha = 1.0F;
            }
        }
        else
        {
            this.streamAlpha = 1.0F;
            this.streamAlphaDelta = 1;
        }
    }
}
