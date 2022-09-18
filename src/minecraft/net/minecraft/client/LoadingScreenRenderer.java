package net.minecraft.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MinecraftError;

public class LoadingScreenRenderer implements IProgressUpdate
{
    private String message = "";

    /** A reference to the Minecraft object. */
    private Minecraft mc;

    /**
     * The text currently displayed (i.e. the argument to the last call to printText or displayString)
     */
    private String currentlyDisplayedText = "";

    /** The system's time represented in milliseconds. */
    private long systemTime = Minecraft.getSystemTime();

    /** True if the loading ended with a success */
    private boolean loadingSuccess;
    private ScaledResolution scaledResolution;
    private Framebuffer framebuffer;

    public LoadingScreenRenderer(Minecraft mcIn)
    {
        this.mc = mcIn;
        this.scaledResolution = new ScaledResolution(mcIn);
        this.framebuffer = new Framebuffer(mcIn.displayWidth, mcIn.displayHeight, false);
        this.framebuffer.setFramebufferFilter(9728);
    }

    /**
     * this string, followed by "working..." and then the "% complete" are the 3 lines shown. This resets progress to 0,
     * and the WorkingString to "working...".
     */
    public void resetProgressAndMessage(String message)
    {
        this.loadingSuccess = false;
        this.displayString(message);
    }

    /**
     * Shows the 'Saving level' string.
     */
    public void displaySavingString(String message)
    {
        this.loadingSuccess = true;
        this.displayString(message);
    }

    private void displayString(String message)
    {
        this.currentlyDisplayedText = message;

        if (!this.mc.running)
        {
            if (!this.loadingSuccess)
            {
                throw new MinecraftError();
            }
        }
        else
        {
            GlStateManager.clear(256);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();

            if (OpenGlHelper.isFramebufferEnabled())
            {
                int lvt_2_1_ = this.scaledResolution.getScaleFactor();
                GlStateManager.ortho(0.0D, (double)(this.scaledResolution.getScaledWidth() * lvt_2_1_), (double)(this.scaledResolution.getScaledHeight() * lvt_2_1_), 0.0D, 100.0D, 300.0D);
            }
            else
            {
                ScaledResolution lvt_2_2_ = new ScaledResolution(this.mc);
                GlStateManager.ortho(0.0D, lvt_2_2_.getScaledWidth_double(), lvt_2_2_.getScaledHeight_double(), 0.0D, 100.0D, 300.0D);
            }

            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, 0.0F, -200.0F);
        }
    }

    /**
     * Displays a string on the loading screen supposed to indicate what is being done currently.
     */
    public void displayLoadingString(String message)
    {
        if (!this.mc.running)
        {
            if (!this.loadingSuccess)
            {
                throw new MinecraftError();
            }
        }
        else
        {
            this.systemTime = 0L;
            this.message = message;
            this.setLoadingProgress(-1);
            this.systemTime = 0L;
        }
    }

    /**
     * Updates the progress bar on the loading screen to the specified amount. Args: loadProgress
     */
    public void setLoadingProgress(int progress)
    {
        if (!this.mc.running)
        {
            if (!this.loadingSuccess)
            {
                throw new MinecraftError();
            }
        }
        else
        {
            long lvt_2_1_ = Minecraft.getSystemTime();

            if (lvt_2_1_ - this.systemTime >= 100L)
            {
                this.systemTime = lvt_2_1_;
                ScaledResolution lvt_4_1_ = new ScaledResolution(this.mc);
                int lvt_5_1_ = lvt_4_1_.getScaleFactor();
                int lvt_6_1_ = lvt_4_1_.getScaledWidth();
                int lvt_7_1_ = lvt_4_1_.getScaledHeight();

                if (OpenGlHelper.isFramebufferEnabled())
                {
                    this.framebuffer.framebufferClear();
                }
                else
                {
                    GlStateManager.clear(256);
                }

                this.framebuffer.bindFramebuffer(false);
                GlStateManager.matrixMode(5889);
                GlStateManager.loadIdentity();
                GlStateManager.ortho(0.0D, lvt_4_1_.getScaledWidth_double(), lvt_4_1_.getScaledHeight_double(), 0.0D, 100.0D, 300.0D);
                GlStateManager.matrixMode(5888);
                GlStateManager.loadIdentity();
                GlStateManager.translate(0.0F, 0.0F, -200.0F);

                if (!OpenGlHelper.isFramebufferEnabled())
                {
                    GlStateManager.clear(16640);
                }

                Tessellator lvt_8_1_ = Tessellator.getInstance();
                WorldRenderer lvt_9_1_ = lvt_8_1_.getWorldRenderer();
                this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
                float lvt_10_1_ = 32.0F;
                lvt_9_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                lvt_9_1_.pos(0.0D, (double)lvt_7_1_, 0.0D).tex(0.0D, (double)((float)lvt_7_1_ / lvt_10_1_)).color(64, 64, 64, 255).endVertex();
                lvt_9_1_.pos((double)lvt_6_1_, (double)lvt_7_1_, 0.0D).tex((double)((float)lvt_6_1_ / lvt_10_1_), (double)((float)lvt_7_1_ / lvt_10_1_)).color(64, 64, 64, 255).endVertex();
                lvt_9_1_.pos((double)lvt_6_1_, 0.0D, 0.0D).tex((double)((float)lvt_6_1_ / lvt_10_1_), 0.0D).color(64, 64, 64, 255).endVertex();
                lvt_9_1_.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0.0D).color(64, 64, 64, 255).endVertex();
                lvt_8_1_.draw();

                if (progress >= 0)
                {
                    int lvt_11_1_ = 100;
                    int lvt_12_1_ = 2;
                    int lvt_13_1_ = lvt_6_1_ / 2 - lvt_11_1_ / 2;
                    int lvt_14_1_ = lvt_7_1_ / 2 + 16;
                    GlStateManager.disableTexture2D();
                    lvt_9_1_.begin(7, DefaultVertexFormats.POSITION_COLOR);
                    lvt_9_1_.pos((double)lvt_13_1_, (double)lvt_14_1_, 0.0D).color(128, 128, 128, 255).endVertex();
                    lvt_9_1_.pos((double)lvt_13_1_, (double)(lvt_14_1_ + lvt_12_1_), 0.0D).color(128, 128, 128, 255).endVertex();
                    lvt_9_1_.pos((double)(lvt_13_1_ + lvt_11_1_), (double)(lvt_14_1_ + lvt_12_1_), 0.0D).color(128, 128, 128, 255).endVertex();
                    lvt_9_1_.pos((double)(lvt_13_1_ + lvt_11_1_), (double)lvt_14_1_, 0.0D).color(128, 128, 128, 255).endVertex();
                    lvt_9_1_.pos((double)lvt_13_1_, (double)lvt_14_1_, 0.0D).color(128, 255, 128, 255).endVertex();
                    lvt_9_1_.pos((double)lvt_13_1_, (double)(lvt_14_1_ + lvt_12_1_), 0.0D).color(128, 255, 128, 255).endVertex();
                    lvt_9_1_.pos((double)(lvt_13_1_ + progress), (double)(lvt_14_1_ + lvt_12_1_), 0.0D).color(128, 255, 128, 255).endVertex();
                    lvt_9_1_.pos((double)(lvt_13_1_ + progress), (double)lvt_14_1_, 0.0D).color(128, 255, 128, 255).endVertex();
                    lvt_8_1_.draw();
                    GlStateManager.enableTexture2D();
                }

                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                this.mc.fontRendererObj.drawStringWithShadow(this.currentlyDisplayedText, (float)((lvt_6_1_ - this.mc.fontRendererObj.getStringWidth(this.currentlyDisplayedText)) / 2), (float)(lvt_7_1_ / 2 - 4 - 16), 16777215);
                this.mc.fontRendererObj.drawStringWithShadow(this.message, (float)((lvt_6_1_ - this.mc.fontRendererObj.getStringWidth(this.message)) / 2), (float)(lvt_7_1_ / 2 - 4 + 8), 16777215);
                this.framebuffer.unbindFramebuffer();

                if (OpenGlHelper.isFramebufferEnabled())
                {
                    this.framebuffer.framebufferRender(lvt_6_1_ * lvt_5_1_, lvt_7_1_ * lvt_5_1_);
                }

                this.mc.updateDisplay();

                try
                {
                    Thread.yield();
                }
                catch (Exception var15)
                {
                    ;
                }
            }
        }
    }

    public void setDoneWorking()
    {
    }
}
