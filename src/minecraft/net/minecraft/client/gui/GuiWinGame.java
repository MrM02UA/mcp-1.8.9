package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiWinGame extends GuiScreen
{
    private static final Logger logger = LogManager.getLogger();
    private static final ResourceLocation MINECRAFT_LOGO = new ResourceLocation("textures/gui/title/minecraft.png");
    private static final ResourceLocation VIGNETTE_TEXTURE = new ResourceLocation("textures/misc/vignette.png");
    private int field_146581_h;
    private List<String> field_146582_i;
    private int field_146579_r;
    private float field_146578_s = 0.5F;

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        MusicTicker lvt_1_1_ = this.mc.getMusicTicker();
        SoundHandler lvt_2_1_ = this.mc.getSoundHandler();

        if (this.field_146581_h == 0)
        {
            lvt_1_1_.func_181557_a();
            lvt_1_1_.func_181558_a(MusicTicker.MusicType.CREDITS);
            lvt_2_1_.resumeSounds();
        }

        lvt_2_1_.update();
        ++this.field_146581_h;
        float lvt_3_1_ = (float)(this.field_146579_r + this.height + this.height + 24) / this.field_146578_s;

        if ((float)this.field_146581_h > lvt_3_1_)
        {
            this.sendRespawnPacket();
        }
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == 1)
        {
            this.sendRespawnPacket();
        }
    }

    private void sendRespawnPacket()
    {
        this.mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
        this.mc.displayGuiScreen((GuiScreen)null);
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return true;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        if (this.field_146582_i == null)
        {
            this.field_146582_i = Lists.newArrayList();

            try
            {
                String lvt_1_1_ = "";
                String lvt_2_1_ = "" + EnumChatFormatting.WHITE + EnumChatFormatting.OBFUSCATED + EnumChatFormatting.GREEN + EnumChatFormatting.AQUA;
                int lvt_3_1_ = 274;
                InputStream lvt_4_1_ = this.mc.getResourceManager().getResource(new ResourceLocation("texts/end.txt")).getInputStream();
                BufferedReader lvt_5_1_ = new BufferedReader(new InputStreamReader(lvt_4_1_, Charsets.UTF_8));
                Random lvt_6_1_ = new Random(8124371L);

                while ((lvt_1_1_ = lvt_5_1_.readLine()) != null)
                {
                    String lvt_8_1_;
                    String lvt_9_1_;

                    for (lvt_1_1_ = lvt_1_1_.replaceAll("PLAYERNAME", this.mc.getSession().getUsername()); lvt_1_1_.contains(lvt_2_1_); lvt_1_1_ = lvt_8_1_ + EnumChatFormatting.WHITE + EnumChatFormatting.OBFUSCATED + "XXXXXXXX".substring(0, lvt_6_1_.nextInt(4) + 3) + lvt_9_1_)
                    {
                        int lvt_7_1_ = lvt_1_1_.indexOf(lvt_2_1_);
                        lvt_8_1_ = lvt_1_1_.substring(0, lvt_7_1_);
                        lvt_9_1_ = lvt_1_1_.substring(lvt_7_1_ + lvt_2_1_.length());
                    }

                    this.field_146582_i.addAll(this.mc.fontRendererObj.listFormattedStringToWidth(lvt_1_1_, lvt_3_1_));
                    this.field_146582_i.add("");
                }

                lvt_4_1_.close();

                for (int lvt_7_2_ = 0; lvt_7_2_ < 8; ++lvt_7_2_)
                {
                    this.field_146582_i.add("");
                }

                lvt_4_1_ = this.mc.getResourceManager().getResource(new ResourceLocation("texts/credits.txt")).getInputStream();
                lvt_5_1_ = new BufferedReader(new InputStreamReader(lvt_4_1_, Charsets.UTF_8));

                while ((lvt_1_1_ = lvt_5_1_.readLine()) != null)
                {
                    lvt_1_1_ = lvt_1_1_.replaceAll("PLAYERNAME", this.mc.getSession().getUsername());
                    lvt_1_1_ = lvt_1_1_.replaceAll("\t", "    ");
                    this.field_146582_i.addAll(this.mc.fontRendererObj.listFormattedStringToWidth(lvt_1_1_, lvt_3_1_));
                    this.field_146582_i.add("");
                }

                lvt_4_1_.close();
                this.field_146579_r = this.field_146582_i.size() * 12;
            }
            catch (Exception var10)
            {
                logger.error("Couldn\'t load credits", var10);
            }
        }
    }

    private void drawWinGameScreen(int p_146575_1_, int p_146575_2_, float p_146575_3_)
    {
        Tessellator lvt_4_1_ = Tessellator.getInstance();
        WorldRenderer lvt_5_1_ = lvt_4_1_.getWorldRenderer();
        this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
        lvt_5_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        int lvt_6_1_ = this.width;
        float lvt_7_1_ = 0.0F - ((float)this.field_146581_h + p_146575_3_) * 0.5F * this.field_146578_s;
        float lvt_8_1_ = (float)this.height - ((float)this.field_146581_h + p_146575_3_) * 0.5F * this.field_146578_s;
        float lvt_9_1_ = 0.015625F;
        float lvt_10_1_ = ((float)this.field_146581_h + p_146575_3_ - 0.0F) * 0.02F;
        float lvt_11_1_ = (float)(this.field_146579_r + this.height + this.height + 24) / this.field_146578_s;
        float lvt_12_1_ = (lvt_11_1_ - 20.0F - ((float)this.field_146581_h + p_146575_3_)) * 0.005F;

        if (lvt_12_1_ < lvt_10_1_)
        {
            lvt_10_1_ = lvt_12_1_;
        }

        if (lvt_10_1_ > 1.0F)
        {
            lvt_10_1_ = 1.0F;
        }

        lvt_10_1_ = lvt_10_1_ * lvt_10_1_;
        lvt_10_1_ = lvt_10_1_ * 96.0F / 255.0F;
        lvt_5_1_.pos(0.0D, (double)this.height, (double)this.zLevel).tex(0.0D, (double)(lvt_7_1_ * lvt_9_1_)).color(lvt_10_1_, lvt_10_1_, lvt_10_1_, 1.0F).endVertex();
        lvt_5_1_.pos((double)lvt_6_1_, (double)this.height, (double)this.zLevel).tex((double)((float)lvt_6_1_ * lvt_9_1_), (double)(lvt_7_1_ * lvt_9_1_)).color(lvt_10_1_, lvt_10_1_, lvt_10_1_, 1.0F).endVertex();
        lvt_5_1_.pos((double)lvt_6_1_, 0.0D, (double)this.zLevel).tex((double)((float)lvt_6_1_ * lvt_9_1_), (double)(lvt_8_1_ * lvt_9_1_)).color(lvt_10_1_, lvt_10_1_, lvt_10_1_, 1.0F).endVertex();
        lvt_5_1_.pos(0.0D, 0.0D, (double)this.zLevel).tex(0.0D, (double)(lvt_8_1_ * lvt_9_1_)).color(lvt_10_1_, lvt_10_1_, lvt_10_1_, 1.0F).endVertex();
        lvt_4_1_.draw();
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawWinGameScreen(mouseX, mouseY, partialTicks);
        Tessellator lvt_4_1_ = Tessellator.getInstance();
        WorldRenderer lvt_5_1_ = lvt_4_1_.getWorldRenderer();
        int lvt_6_1_ = 274;
        int lvt_7_1_ = this.width / 2 - lvt_6_1_ / 2;
        int lvt_8_1_ = this.height + 50;
        float lvt_9_1_ = -((float)this.field_146581_h + partialTicks) * this.field_146578_s;
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, lvt_9_1_, 0.0F);
        this.mc.getTextureManager().bindTexture(MINECRAFT_LOGO);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(lvt_7_1_, lvt_8_1_, 0, 0, 155, 44);
        this.drawTexturedModalRect(lvt_7_1_ + 155, lvt_8_1_, 0, 45, 155, 44);
        int lvt_10_1_ = lvt_8_1_ + 200;

        for (int lvt_11_1_ = 0; lvt_11_1_ < this.field_146582_i.size(); ++lvt_11_1_)
        {
            if (lvt_11_1_ == this.field_146582_i.size() - 1)
            {
                float lvt_12_1_ = (float)lvt_10_1_ + lvt_9_1_ - (float)(this.height / 2 - 6);

                if (lvt_12_1_ < 0.0F)
                {
                    GlStateManager.translate(0.0F, -lvt_12_1_, 0.0F);
                }
            }

            if ((float)lvt_10_1_ + lvt_9_1_ + 12.0F + 8.0F > 0.0F && (float)lvt_10_1_ + lvt_9_1_ < (float)this.height)
            {
                String lvt_12_2_ = (String)this.field_146582_i.get(lvt_11_1_);

                if (lvt_12_2_.startsWith("[C]"))
                {
                    this.fontRendererObj.drawStringWithShadow(lvt_12_2_.substring(3), (float)(lvt_7_1_ + (lvt_6_1_ - this.fontRendererObj.getStringWidth(lvt_12_2_.substring(3))) / 2), (float)lvt_10_1_, 16777215);
                }
                else
                {
                    this.fontRendererObj.fontRandom.setSeed((long)lvt_11_1_ * 4238972211L + (long)(this.field_146581_h / 4));
                    this.fontRendererObj.drawStringWithShadow(lvt_12_2_, (float)lvt_7_1_, (float)lvt_10_1_, 16777215);
                }
            }

            lvt_10_1_ += 12;
        }

        GlStateManager.popMatrix();
        this.mc.getTextureManager().bindTexture(VIGNETTE_TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(0, 769);
        int lvt_11_2_ = this.width;
        int lvt_12_3_ = this.height;
        lvt_5_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        lvt_5_1_.pos(0.0D, (double)lvt_12_3_, (double)this.zLevel).tex(0.0D, 1.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        lvt_5_1_.pos((double)lvt_11_2_, (double)lvt_12_3_, (double)this.zLevel).tex(1.0D, 1.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        lvt_5_1_.pos((double)lvt_11_2_, 0.0D, (double)this.zLevel).tex(1.0D, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        lvt_5_1_.pos(0.0D, 0.0D, (double)this.zLevel).tex(0.0D, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        lvt_4_1_.draw();
        GlStateManager.disableBlend();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
