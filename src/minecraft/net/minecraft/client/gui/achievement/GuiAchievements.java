package net.minecraft.client.gui.achievement;

import java.io.IOException;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

public class GuiAchievements extends GuiScreen implements IProgressMeter
{
    private static final int field_146572_y = AchievementList.minDisplayColumn * 24 - 112;
    private static final int field_146571_z = AchievementList.minDisplayRow * 24 - 112;
    private static final int field_146559_A = AchievementList.maxDisplayColumn * 24 - 77;
    private static final int field_146560_B = AchievementList.maxDisplayRow * 24 - 77;
    private static final ResourceLocation ACHIEVEMENT_BACKGROUND = new ResourceLocation("textures/gui/achievement/achievement_background.png");
    protected GuiScreen parentScreen;
    protected int field_146555_f = 256;
    protected int field_146557_g = 202;
    protected int field_146563_h;
    protected int field_146564_i;
    protected float field_146570_r = 1.0F;
    protected double field_146569_s;
    protected double field_146568_t;
    protected double field_146567_u;
    protected double field_146566_v;
    protected double field_146565_w;
    protected double field_146573_x;
    private int field_146554_D;
    private StatFileWriter statFileWriter;
    private boolean loadingAchievements = true;

    public GuiAchievements(GuiScreen parentScreenIn, StatFileWriter statFileWriterIn)
    {
        this.parentScreen = parentScreenIn;
        this.statFileWriter = statFileWriterIn;
        int lvt_3_1_ = 141;
        int lvt_4_1_ = 141;
        this.field_146569_s = this.field_146567_u = this.field_146565_w = (double)(AchievementList.openInventory.displayColumn * 24 - lvt_3_1_ / 2 - 12);
        this.field_146568_t = this.field_146566_v = this.field_146573_x = (double)(AchievementList.openInventory.displayRow * 24 - lvt_4_1_ / 2);
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.mc.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.REQUEST_STATS));
        this.buttonList.clear();
        this.buttonList.add(new GuiOptionButton(1, this.width / 2 + 24, this.height / 2 + 74, 80, 20, I18n.format("gui.done", new Object[0])));
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (!this.loadingAchievements)
        {
            if (button.id == 1)
            {
                this.mc.displayGuiScreen(this.parentScreen);
            }
        }
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode())
        {
            this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
        }
        else
        {
            super.keyTyped(typedChar, keyCode);
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (this.loadingAchievements)
        {
            this.drawDefaultBackground();
            this.drawCenteredString(this.fontRendererObj, I18n.format("multiplayer.downloadingStats", new Object[0]), this.width / 2, this.height / 2, 16777215);
            this.drawCenteredString(this.fontRendererObj, lanSearchStates[(int)(Minecraft.getSystemTime() / 150L % (long)lanSearchStates.length)], this.width / 2, this.height / 2 + this.fontRendererObj.FONT_HEIGHT * 2, 16777215);
        }
        else
        {
            if (Mouse.isButtonDown(0))
            {
                int lvt_4_1_ = (this.width - this.field_146555_f) / 2;
                int lvt_5_1_ = (this.height - this.field_146557_g) / 2;
                int lvt_6_1_ = lvt_4_1_ + 8;
                int lvt_7_1_ = lvt_5_1_ + 17;

                if ((this.field_146554_D == 0 || this.field_146554_D == 1) && mouseX >= lvt_6_1_ && mouseX < lvt_6_1_ + 224 && mouseY >= lvt_7_1_ && mouseY < lvt_7_1_ + 155)
                {
                    if (this.field_146554_D == 0)
                    {
                        this.field_146554_D = 1;
                    }
                    else
                    {
                        this.field_146567_u -= (double)((float)(mouseX - this.field_146563_h) * this.field_146570_r);
                        this.field_146566_v -= (double)((float)(mouseY - this.field_146564_i) * this.field_146570_r);
                        this.field_146565_w = this.field_146569_s = this.field_146567_u;
                        this.field_146573_x = this.field_146568_t = this.field_146566_v;
                    }

                    this.field_146563_h = mouseX;
                    this.field_146564_i = mouseY;
                }
            }
            else
            {
                this.field_146554_D = 0;
            }

            int lvt_4_2_ = Mouse.getDWheel();
            float lvt_5_2_ = this.field_146570_r;

            if (lvt_4_2_ < 0)
            {
                this.field_146570_r += 0.25F;
            }
            else if (lvt_4_2_ > 0)
            {
                this.field_146570_r -= 0.25F;
            }

            this.field_146570_r = MathHelper.clamp_float(this.field_146570_r, 1.0F, 2.0F);

            if (this.field_146570_r != lvt_5_2_)
            {
                float var10000 = lvt_5_2_ - this.field_146570_r;
                float lvt_7_2_ = lvt_5_2_ * (float)this.field_146555_f;
                float lvt_8_1_ = lvt_5_2_ * (float)this.field_146557_g;
                float lvt_9_1_ = this.field_146570_r * (float)this.field_146555_f;
                float lvt_10_1_ = this.field_146570_r * (float)this.field_146557_g;
                this.field_146567_u -= (double)((lvt_9_1_ - lvt_7_2_) * 0.5F);
                this.field_146566_v -= (double)((lvt_10_1_ - lvt_8_1_) * 0.5F);
                this.field_146565_w = this.field_146569_s = this.field_146567_u;
                this.field_146573_x = this.field_146568_t = this.field_146566_v;
            }

            if (this.field_146565_w < (double)field_146572_y)
            {
                this.field_146565_w = (double)field_146572_y;
            }

            if (this.field_146573_x < (double)field_146571_z)
            {
                this.field_146573_x = (double)field_146571_z;
            }

            if (this.field_146565_w >= (double)field_146559_A)
            {
                this.field_146565_w = (double)(field_146559_A - 1);
            }

            if (this.field_146573_x >= (double)field_146560_B)
            {
                this.field_146573_x = (double)(field_146560_B - 1);
            }

            this.drawDefaultBackground();
            this.drawAchievementScreen(mouseX, mouseY, partialTicks);
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            this.drawTitle();
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
        }
    }

    public void doneLoading()
    {
        if (this.loadingAchievements)
        {
            this.loadingAchievements = false;
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        if (!this.loadingAchievements)
        {
            this.field_146569_s = this.field_146567_u;
            this.field_146568_t = this.field_146566_v;
            double lvt_1_1_ = this.field_146565_w - this.field_146567_u;
            double lvt_3_1_ = this.field_146573_x - this.field_146566_v;

            if (lvt_1_1_ * lvt_1_1_ + lvt_3_1_ * lvt_3_1_ < 4.0D)
            {
                this.field_146567_u += lvt_1_1_;
                this.field_146566_v += lvt_3_1_;
            }
            else
            {
                this.field_146567_u += lvt_1_1_ * 0.85D;
                this.field_146566_v += lvt_3_1_ * 0.85D;
            }
        }
    }

    protected void drawTitle()
    {
        int lvt_1_1_ = (this.width - this.field_146555_f) / 2;
        int lvt_2_1_ = (this.height - this.field_146557_g) / 2;
        this.fontRendererObj.drawString(I18n.format("gui.achievements", new Object[0]), lvt_1_1_ + 15, lvt_2_1_ + 5, 4210752);
    }

    protected void drawAchievementScreen(int p_146552_1_, int p_146552_2_, float p_146552_3_)
    {
        int lvt_4_1_ = MathHelper.floor_double(this.field_146569_s + (this.field_146567_u - this.field_146569_s) * (double)p_146552_3_);
        int lvt_5_1_ = MathHelper.floor_double(this.field_146568_t + (this.field_146566_v - this.field_146568_t) * (double)p_146552_3_);

        if (lvt_4_1_ < field_146572_y)
        {
            lvt_4_1_ = field_146572_y;
        }

        if (lvt_5_1_ < field_146571_z)
        {
            lvt_5_1_ = field_146571_z;
        }

        if (lvt_4_1_ >= field_146559_A)
        {
            lvt_4_1_ = field_146559_A - 1;
        }

        if (lvt_5_1_ >= field_146560_B)
        {
            lvt_5_1_ = field_146560_B - 1;
        }

        int lvt_6_1_ = (this.width - this.field_146555_f) / 2;
        int lvt_7_1_ = (this.height - this.field_146557_g) / 2;
        int lvt_8_1_ = lvt_6_1_ + 16;
        int lvt_9_1_ = lvt_7_1_ + 17;
        this.zLevel = 0.0F;
        GlStateManager.depthFunc(518);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)lvt_8_1_, (float)lvt_9_1_, -200.0F);
        GlStateManager.scale(1.0F / this.field_146570_r, 1.0F / this.field_146570_r, 0.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        int lvt_10_1_ = lvt_4_1_ + 288 >> 4;
        int lvt_11_1_ = lvt_5_1_ + 288 >> 4;
        int lvt_12_1_ = (lvt_4_1_ + 288) % 16;
        int lvt_13_1_ = (lvt_5_1_ + 288) % 16;
        int lvt_14_1_ = 4;
        int lvt_15_1_ = 8;
        int lvt_16_1_ = 10;
        int lvt_17_1_ = 22;
        int lvt_18_1_ = 37;
        Random lvt_19_1_ = new Random();
        float lvt_20_1_ = 16.0F / this.field_146570_r;
        float lvt_21_1_ = 16.0F / this.field_146570_r;

        for (int lvt_22_1_ = 0; (float)lvt_22_1_ * lvt_20_1_ - (float)lvt_13_1_ < 155.0F; ++lvt_22_1_)
        {
            float lvt_23_1_ = 0.6F - (float)(lvt_11_1_ + lvt_22_1_) / 25.0F * 0.3F;
            GlStateManager.color(lvt_23_1_, lvt_23_1_, lvt_23_1_, 1.0F);

            for (int lvt_24_1_ = 0; (float)lvt_24_1_ * lvt_21_1_ - (float)lvt_12_1_ < 224.0F; ++lvt_24_1_)
            {
                lvt_19_1_.setSeed((long)(this.mc.getSession().getPlayerID().hashCode() + lvt_10_1_ + lvt_24_1_ + (lvt_11_1_ + lvt_22_1_) * 16));
                int lvt_25_1_ = lvt_19_1_.nextInt(1 + lvt_11_1_ + lvt_22_1_) + (lvt_11_1_ + lvt_22_1_) / 2;
                TextureAtlasSprite lvt_26_1_ = this.func_175371_a(Blocks.sand);

                if (lvt_25_1_ <= 37 && lvt_11_1_ + lvt_22_1_ != 35)
                {
                    if (lvt_25_1_ == 22)
                    {
                        if (lvt_19_1_.nextInt(2) == 0)
                        {
                            lvt_26_1_ = this.func_175371_a(Blocks.diamond_ore);
                        }
                        else
                        {
                            lvt_26_1_ = this.func_175371_a(Blocks.redstone_ore);
                        }
                    }
                    else if (lvt_25_1_ == 10)
                    {
                        lvt_26_1_ = this.func_175371_a(Blocks.iron_ore);
                    }
                    else if (lvt_25_1_ == 8)
                    {
                        lvt_26_1_ = this.func_175371_a(Blocks.coal_ore);
                    }
                    else if (lvt_25_1_ > 4)
                    {
                        lvt_26_1_ = this.func_175371_a(Blocks.stone);
                    }
                    else if (lvt_25_1_ > 0)
                    {
                        lvt_26_1_ = this.func_175371_a(Blocks.dirt);
                    }
                }
                else
                {
                    Block lvt_27_1_ = Blocks.bedrock;
                    lvt_26_1_ = this.func_175371_a(lvt_27_1_);
                }

                this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                this.drawTexturedModalRect(lvt_24_1_ * 16 - lvt_12_1_, lvt_22_1_ * 16 - lvt_13_1_, lvt_26_1_, 16, 16);
            }
        }

        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515);
        this.mc.getTextureManager().bindTexture(ACHIEVEMENT_BACKGROUND);

        for (int lvt_22_2_ = 0; lvt_22_2_ < AchievementList.achievementList.size(); ++lvt_22_2_)
        {
            Achievement lvt_23_2_ = (Achievement)AchievementList.achievementList.get(lvt_22_2_);

            if (lvt_23_2_.parentAchievement != null)
            {
                int lvt_24_2_ = lvt_23_2_.displayColumn * 24 - lvt_4_1_ + 11;
                int lvt_25_2_ = lvt_23_2_.displayRow * 24 - lvt_5_1_ + 11;
                int lvt_26_2_ = lvt_23_2_.parentAchievement.displayColumn * 24 - lvt_4_1_ + 11;
                int lvt_27_2_ = lvt_23_2_.parentAchievement.displayRow * 24 - lvt_5_1_ + 11;
                boolean lvt_28_1_ = this.statFileWriter.hasAchievementUnlocked(lvt_23_2_);
                boolean lvt_29_1_ = this.statFileWriter.canUnlockAchievement(lvt_23_2_);
                int lvt_30_1_ = this.statFileWriter.func_150874_c(lvt_23_2_);

                if (lvt_30_1_ <= 4)
                {
                    int lvt_31_1_ = -16777216;

                    if (lvt_28_1_)
                    {
                        lvt_31_1_ = -6250336;
                    }
                    else if (lvt_29_1_)
                    {
                        lvt_31_1_ = -16711936;
                    }

                    this.drawHorizontalLine(lvt_24_2_, lvt_26_2_, lvt_25_2_, lvt_31_1_);
                    this.drawVerticalLine(lvt_26_2_, lvt_25_2_, lvt_27_2_, lvt_31_1_);

                    if (lvt_24_2_ > lvt_26_2_)
                    {
                        this.drawTexturedModalRect(lvt_24_2_ - 11 - 7, lvt_25_2_ - 5, 114, 234, 7, 11);
                    }
                    else if (lvt_24_2_ < lvt_26_2_)
                    {
                        this.drawTexturedModalRect(lvt_24_2_ + 11, lvt_25_2_ - 5, 107, 234, 7, 11);
                    }
                    else if (lvt_25_2_ > lvt_27_2_)
                    {
                        this.drawTexturedModalRect(lvt_24_2_ - 5, lvt_25_2_ - 11 - 7, 96, 234, 11, 7);
                    }
                    else if (lvt_25_2_ < lvt_27_2_)
                    {
                        this.drawTexturedModalRect(lvt_24_2_ - 5, lvt_25_2_ + 11, 96, 241, 11, 7);
                    }
                }
            }
        }

        Achievement lvt_22_3_ = null;
        float lvt_23_3_ = (float)(p_146552_1_ - lvt_8_1_) * this.field_146570_r;
        float lvt_24_3_ = (float)(p_146552_2_ - lvt_9_1_) * this.field_146570_r;
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();

        for (int lvt_25_3_ = 0; lvt_25_3_ < AchievementList.achievementList.size(); ++lvt_25_3_)
        {
            Achievement lvt_26_3_ = (Achievement)AchievementList.achievementList.get(lvt_25_3_);
            int lvt_27_3_ = lvt_26_3_.displayColumn * 24 - lvt_4_1_;
            int lvt_28_2_ = lvt_26_3_.displayRow * 24 - lvt_5_1_;

            if (lvt_27_3_ >= -24 && lvt_28_2_ >= -24 && (float)lvt_27_3_ <= 224.0F * this.field_146570_r && (float)lvt_28_2_ <= 155.0F * this.field_146570_r)
            {
                int lvt_29_2_ = this.statFileWriter.func_150874_c(lvt_26_3_);

                if (this.statFileWriter.hasAchievementUnlocked(lvt_26_3_))
                {
                    float lvt_30_2_ = 0.75F;
                    GlStateManager.color(lvt_30_2_, lvt_30_2_, lvt_30_2_, 1.0F);
                }
                else if (this.statFileWriter.canUnlockAchievement(lvt_26_3_))
                {
                    float lvt_30_3_ = 1.0F;
                    GlStateManager.color(lvt_30_3_, lvt_30_3_, lvt_30_3_, 1.0F);
                }
                else if (lvt_29_2_ < 3)
                {
                    float lvt_30_4_ = 0.3F;
                    GlStateManager.color(lvt_30_4_, lvt_30_4_, lvt_30_4_, 1.0F);
                }
                else if (lvt_29_2_ == 3)
                {
                    float lvt_30_5_ = 0.2F;
                    GlStateManager.color(lvt_30_5_, lvt_30_5_, lvt_30_5_, 1.0F);
                }
                else
                {
                    if (lvt_29_2_ != 4)
                    {
                        continue;
                    }

                    float lvt_30_6_ = 0.1F;
                    GlStateManager.color(lvt_30_6_, lvt_30_6_, lvt_30_6_, 1.0F);
                }

                this.mc.getTextureManager().bindTexture(ACHIEVEMENT_BACKGROUND);

                if (lvt_26_3_.getSpecial())
                {
                    this.drawTexturedModalRect(lvt_27_3_ - 2, lvt_28_2_ - 2, 26, 202, 26, 26);
                }
                else
                {
                    this.drawTexturedModalRect(lvt_27_3_ - 2, lvt_28_2_ - 2, 0, 202, 26, 26);
                }

                if (!this.statFileWriter.canUnlockAchievement(lvt_26_3_))
                {
                    float lvt_30_7_ = 0.1F;
                    GlStateManager.color(lvt_30_7_, lvt_30_7_, lvt_30_7_, 1.0F);
                    this.itemRender.isNotRenderingEffectsInGUI(false);
                }

                GlStateManager.enableLighting();
                GlStateManager.enableCull();
                this.itemRender.renderItemAndEffectIntoGUI(lvt_26_3_.theItemStack, lvt_27_3_ + 3, lvt_28_2_ + 3);
                GlStateManager.blendFunc(770, 771);
                GlStateManager.disableLighting();

                if (!this.statFileWriter.canUnlockAchievement(lvt_26_3_))
                {
                    this.itemRender.isNotRenderingEffectsInGUI(true);
                }

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                if (lvt_23_3_ >= (float)lvt_27_3_ && lvt_23_3_ <= (float)(lvt_27_3_ + 22) && lvt_24_3_ >= (float)lvt_28_2_ && lvt_24_3_ <= (float)(lvt_28_2_ + 22))
                {
                    lvt_22_3_ = lvt_26_3_;
                }
            }
        }

        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(ACHIEVEMENT_BACKGROUND);
        this.drawTexturedModalRect(lvt_6_1_, lvt_7_1_, 0, 0, this.field_146555_f, this.field_146557_g);
        this.zLevel = 0.0F;
        GlStateManager.depthFunc(515);
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();
        super.drawScreen(p_146552_1_, p_146552_2_, p_146552_3_);

        if (lvt_22_3_ != null)
        {
            String lvt_25_4_ = lvt_22_3_.getStatName().getUnformattedText();
            String lvt_26_4_ = lvt_22_3_.getDescription();
            int lvt_27_4_ = p_146552_1_ + 12;
            int lvt_28_3_ = p_146552_2_ - 4;
            int lvt_29_3_ = this.statFileWriter.func_150874_c(lvt_22_3_);

            if (this.statFileWriter.canUnlockAchievement(lvt_22_3_))
            {
                int lvt_30_8_ = Math.max(this.fontRendererObj.getStringWidth(lvt_25_4_), 120);
                int lvt_31_2_ = this.fontRendererObj.splitStringWidth(lvt_26_4_, lvt_30_8_);

                if (this.statFileWriter.hasAchievementUnlocked(lvt_22_3_))
                {
                    lvt_31_2_ += 12;
                }

                this.drawGradientRect(lvt_27_4_ - 3, lvt_28_3_ - 3, lvt_27_4_ + lvt_30_8_ + 3, lvt_28_3_ + lvt_31_2_ + 3 + 12, -1073741824, -1073741824);
                this.fontRendererObj.drawSplitString(lvt_26_4_, lvt_27_4_, lvt_28_3_ + 12, lvt_30_8_, -6250336);

                if (this.statFileWriter.hasAchievementUnlocked(lvt_22_3_))
                {
                    this.fontRendererObj.drawStringWithShadow(I18n.format("achievement.taken", new Object[0]), (float)lvt_27_4_, (float)(lvt_28_3_ + lvt_31_2_ + 4), -7302913);
                }
            }
            else if (lvt_29_3_ == 3)
            {
                lvt_25_4_ = I18n.format("achievement.unknown", new Object[0]);
                int lvt_30_9_ = Math.max(this.fontRendererObj.getStringWidth(lvt_25_4_), 120);
                String lvt_31_3_ = (new ChatComponentTranslation("achievement.requires", new Object[] {lvt_22_3_.parentAchievement.getStatName()})).getUnformattedText();
                int lvt_32_1_ = this.fontRendererObj.splitStringWidth(lvt_31_3_, lvt_30_9_);
                this.drawGradientRect(lvt_27_4_ - 3, lvt_28_3_ - 3, lvt_27_4_ + lvt_30_9_ + 3, lvt_28_3_ + lvt_32_1_ + 12 + 3, -1073741824, -1073741824);
                this.fontRendererObj.drawSplitString(lvt_31_3_, lvt_27_4_, lvt_28_3_ + 12, lvt_30_9_, -9416624);
            }
            else if (lvt_29_3_ < 3)
            {
                int lvt_30_10_ = Math.max(this.fontRendererObj.getStringWidth(lvt_25_4_), 120);
                String lvt_31_4_ = (new ChatComponentTranslation("achievement.requires", new Object[] {lvt_22_3_.parentAchievement.getStatName()})).getUnformattedText();
                int lvt_32_2_ = this.fontRendererObj.splitStringWidth(lvt_31_4_, lvt_30_10_);
                this.drawGradientRect(lvt_27_4_ - 3, lvt_28_3_ - 3, lvt_27_4_ + lvt_30_10_ + 3, lvt_28_3_ + lvt_32_2_ + 12 + 3, -1073741824, -1073741824);
                this.fontRendererObj.drawSplitString(lvt_31_4_, lvt_27_4_, lvt_28_3_ + 12, lvt_30_10_, -9416624);
            }
            else
            {
                lvt_25_4_ = null;
            }

            if (lvt_25_4_ != null)
            {
                this.fontRendererObj.drawStringWithShadow(lvt_25_4_, (float)lvt_27_4_, (float)lvt_28_3_, this.statFileWriter.canUnlockAchievement(lvt_22_3_) ? (lvt_22_3_.getSpecial() ? -128 : -1) : (lvt_22_3_.getSpecial() ? -8355776 : -8355712));
            }
        }

        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        RenderHelper.disableStandardItemLighting();
    }

    private TextureAtlasSprite func_175371_a(Block p_175371_1_)
    {
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(p_175371_1_.getDefaultState());
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return !this.loadingAchievements;
    }
}
