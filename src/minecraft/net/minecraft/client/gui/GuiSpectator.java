package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuRecipient;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.categories.SpectatorDetails;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class GuiSpectator extends Gui implements ISpectatorMenuRecipient
{
    private static final ResourceLocation field_175267_f = new ResourceLocation("textures/gui/widgets.png");
    public static final ResourceLocation field_175269_a = new ResourceLocation("textures/gui/spectator_widgets.png");
    private final Minecraft field_175268_g;
    private long field_175270_h;
    private SpectatorMenu field_175271_i;

    public GuiSpectator(Minecraft mcIn)
    {
        this.field_175268_g = mcIn;
    }

    public void func_175260_a(int p_175260_1_)
    {
        this.field_175270_h = Minecraft.getSystemTime();

        if (this.field_175271_i != null)
        {
            this.field_175271_i.func_178644_b(p_175260_1_);
        }
        else
        {
            this.field_175271_i = new SpectatorMenu(this);
        }
    }

    private float func_175265_c()
    {
        long lvt_1_1_ = this.field_175270_h - Minecraft.getSystemTime() + 5000L;
        return MathHelper.clamp_float((float)lvt_1_1_ / 2000.0F, 0.0F, 1.0F);
    }

    public void renderTooltip(ScaledResolution p_175264_1_, float p_175264_2_)
    {
        if (this.field_175271_i != null)
        {
            float lvt_3_1_ = this.func_175265_c();

            if (lvt_3_1_ <= 0.0F)
            {
                this.field_175271_i.func_178641_d();
            }
            else
            {
                int lvt_4_1_ = p_175264_1_.getScaledWidth() / 2;
                float lvt_5_1_ = this.zLevel;
                this.zLevel = -90.0F;
                float lvt_6_1_ = (float)p_175264_1_.getScaledHeight() - 22.0F * lvt_3_1_;
                SpectatorDetails lvt_7_1_ = this.field_175271_i.func_178646_f();
                this.func_175258_a(p_175264_1_, lvt_3_1_, lvt_4_1_, lvt_6_1_, lvt_7_1_);
                this.zLevel = lvt_5_1_;
            }
        }
    }

    protected void func_175258_a(ScaledResolution p_175258_1_, float p_175258_2_, int p_175258_3_, float p_175258_4_, SpectatorDetails p_175258_5_)
    {
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(1.0F, 1.0F, 1.0F, p_175258_2_);
        this.field_175268_g.getTextureManager().bindTexture(field_175267_f);
        this.drawTexturedModalRect((float)(p_175258_3_ - 91), p_175258_4_, 0, 0, 182, 22);

        if (p_175258_5_.func_178681_b() >= 0)
        {
            this.drawTexturedModalRect((float)(p_175258_3_ - 91 - 1 + p_175258_5_.func_178681_b() * 20), p_175258_4_ - 1.0F, 0, 22, 24, 22);
        }

        RenderHelper.enableGUIStandardItemLighting();

        for (int lvt_6_1_ = 0; lvt_6_1_ < 9; ++lvt_6_1_)
        {
            this.func_175266_a(lvt_6_1_, p_175258_1_.getScaledWidth() / 2 - 90 + lvt_6_1_ * 20 + 2, p_175258_4_ + 3.0F, p_175258_2_, p_175258_5_.func_178680_a(lvt_6_1_));
        }

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
    }

    private void func_175266_a(int p_175266_1_, int p_175266_2_, float p_175266_3_, float p_175266_4_, ISpectatorMenuObject p_175266_5_)
    {
        this.field_175268_g.getTextureManager().bindTexture(field_175269_a);

        if (p_175266_5_ != SpectatorMenu.field_178657_a)
        {
            int lvt_6_1_ = (int)(p_175266_4_ * 255.0F);
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)p_175266_2_, p_175266_3_, 0.0F);
            float lvt_7_1_ = p_175266_5_.func_178662_A_() ? 1.0F : 0.25F;
            GlStateManager.color(lvt_7_1_, lvt_7_1_, lvt_7_1_, p_175266_4_);
            p_175266_5_.func_178663_a(lvt_7_1_, lvt_6_1_);
            GlStateManager.popMatrix();
            String lvt_8_1_ = String.valueOf(GameSettings.getKeyDisplayString(this.field_175268_g.gameSettings.keyBindsHotbar[p_175266_1_].getKeyCode()));

            if (lvt_6_1_ > 3 && p_175266_5_.func_178662_A_())
            {
                this.field_175268_g.fontRendererObj.drawStringWithShadow(lvt_8_1_, (float)(p_175266_2_ + 19 - 2 - this.field_175268_g.fontRendererObj.getStringWidth(lvt_8_1_)), p_175266_3_ + 6.0F + 3.0F, 16777215 + (lvt_6_1_ << 24));
            }
        }
    }

    public void renderSelectedItem(ScaledResolution p_175263_1_)
    {
        int lvt_2_1_ = (int)(this.func_175265_c() * 255.0F);

        if (lvt_2_1_ > 3 && this.field_175271_i != null)
        {
            ISpectatorMenuObject lvt_3_1_ = this.field_175271_i.func_178645_b();
            String lvt_4_1_ = lvt_3_1_ != SpectatorMenu.field_178657_a ? lvt_3_1_.getSpectatorName().getFormattedText() : this.field_175271_i.func_178650_c().func_178670_b().getFormattedText();

            if (lvt_4_1_ != null)
            {
                int lvt_5_1_ = (p_175263_1_.getScaledWidth() - this.field_175268_g.fontRendererObj.getStringWidth(lvt_4_1_)) / 2;
                int lvt_6_1_ = p_175263_1_.getScaledHeight() - 35;
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                this.field_175268_g.fontRendererObj.drawStringWithShadow(lvt_4_1_, (float)lvt_5_1_, (float)lvt_6_1_, 16777215 + (lvt_2_1_ << 24));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }
    }

    public void func_175257_a(SpectatorMenu p_175257_1_)
    {
        this.field_175271_i = null;
        this.field_175270_h = 0L;
    }

    public boolean func_175262_a()
    {
        return this.field_175271_i != null;
    }

    public void func_175259_b(int p_175259_1_)
    {
        int lvt_2_1_;

        for (lvt_2_1_ = this.field_175271_i.func_178648_e() + p_175259_1_; lvt_2_1_ >= 0 && lvt_2_1_ <= 8 && (this.field_175271_i.func_178643_a(lvt_2_1_) == SpectatorMenu.field_178657_a || !this.field_175271_i.func_178643_a(lvt_2_1_).func_178662_A_()); lvt_2_1_ += p_175259_1_)
        {
            ;
        }

        if (lvt_2_1_ >= 0 && lvt_2_1_ <= 8)
        {
            this.field_175271_i.func_178644_b(lvt_2_1_);
            this.field_175270_h = Minecraft.getSystemTime();
        }
    }

    public void func_175261_b()
    {
        this.field_175270_h = Minecraft.getSystemTime();

        if (this.func_175262_a())
        {
            int lvt_1_1_ = this.field_175271_i.func_178648_e();

            if (lvt_1_1_ != -1)
            {
                this.field_175271_i.func_178644_b(lvt_1_1_);
            }
        }
        else
        {
            this.field_175271_i = new SpectatorMenu(this);
        }
    }
}
