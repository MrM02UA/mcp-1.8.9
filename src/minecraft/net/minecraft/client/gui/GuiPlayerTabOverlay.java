package net.minecraft.client.gui;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldSettings;

public class GuiPlayerTabOverlay extends Gui
{
    private static final Ordering<NetworkPlayerInfo> field_175252_a = Ordering.from(new GuiPlayerTabOverlay.PlayerComparator());
    private final Minecraft mc;
    private final GuiIngame guiIngame;
    private IChatComponent footer;
    private IChatComponent header;

    /**
     * The last time the playerlist was opened (went from not being renderd, to being rendered)
     */
    private long lastTimeOpened;

    /** Weither or not the playerlist is currently being rendered */
    private boolean isBeingRendered;

    public GuiPlayerTabOverlay(Minecraft mcIn, GuiIngame guiIngameIn)
    {
        this.mc = mcIn;
        this.guiIngame = guiIngameIn;
    }

    /**
     * Returns the name that should be renderd for the player supplied
     */
    public String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn)
    {
        return networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
    }

    /**
     * Called by GuiIngame to update the information stored in the playerlist, does not actually render the list,
     * however.
     */
    public void updatePlayerList(boolean willBeRendered)
    {
        if (willBeRendered && !this.isBeingRendered)
        {
            this.lastTimeOpened = Minecraft.getSystemTime();
        }

        this.isBeingRendered = willBeRendered;
    }

    /**
     * Renders the playerlist, its background, headers and footers.
     */
    public void renderPlayerlist(int width, Scoreboard scoreboardIn, ScoreObjective scoreObjectiveIn)
    {
        NetHandlerPlayClient lvt_4_1_ = this.mc.thePlayer.sendQueue;
        List<NetworkPlayerInfo> lvt_5_1_ = field_175252_a.sortedCopy(lvt_4_1_.getPlayerInfoMap());
        int lvt_6_1_ = 0;
        int lvt_7_1_ = 0;

        for (NetworkPlayerInfo lvt_9_1_ : lvt_5_1_)
        {
            int lvt_10_1_ = this.mc.fontRendererObj.getStringWidth(this.getPlayerName(lvt_9_1_));
            lvt_6_1_ = Math.max(lvt_6_1_, lvt_10_1_);

            if (scoreObjectiveIn != null && scoreObjectiveIn.getRenderType() != IScoreObjectiveCriteria.EnumRenderType.HEARTS)
            {
                lvt_10_1_ = this.mc.fontRendererObj.getStringWidth(" " + scoreboardIn.getValueFromObjective(lvt_9_1_.getGameProfile().getName(), scoreObjectiveIn).getScorePoints());
                lvt_7_1_ = Math.max(lvt_7_1_, lvt_10_1_);
            }
        }

        lvt_5_1_ = lvt_5_1_.subList(0, Math.min(lvt_5_1_.size(), 80));
        int lvt_8_2_ = lvt_5_1_.size();
        int lvt_9_2_ = lvt_8_2_;
        int lvt_10_2_;

        for (lvt_10_2_ = 1; lvt_9_2_ > 20; lvt_9_2_ = (lvt_8_2_ + lvt_10_2_ - 1) / lvt_10_2_)
        {
            ++lvt_10_2_;
        }

        boolean lvt_11_1_ = this.mc.isIntegratedServerRunning() || this.mc.getNetHandler().getNetworkManager().getIsencrypted();
        int lvt_12_1_;

        if (scoreObjectiveIn != null)
        {
            if (scoreObjectiveIn.getRenderType() == IScoreObjectiveCriteria.EnumRenderType.HEARTS)
            {
                lvt_12_1_ = 90;
            }
            else
            {
                lvt_12_1_ = lvt_7_1_;
            }
        }
        else
        {
            lvt_12_1_ = 0;
        }

        int lvt_13_1_ = Math.min(lvt_10_2_ * ((lvt_11_1_ ? 9 : 0) + lvt_6_1_ + lvt_12_1_ + 13), width - 50) / lvt_10_2_;
        int lvt_14_1_ = width / 2 - (lvt_13_1_ * lvt_10_2_ + (lvt_10_2_ - 1) * 5) / 2;
        int lvt_15_1_ = 10;
        int lvt_16_1_ = lvt_13_1_ * lvt_10_2_ + (lvt_10_2_ - 1) * 5;
        List<String> lvt_17_1_ = null;
        List<String> lvt_18_1_ = null;

        if (this.header != null)
        {
            lvt_17_1_ = this.mc.fontRendererObj.listFormattedStringToWidth(this.header.getFormattedText(), width - 50);

            for (String lvt_20_1_ : lvt_17_1_)
            {
                lvt_16_1_ = Math.max(lvt_16_1_, this.mc.fontRendererObj.getStringWidth(lvt_20_1_));
            }
        }

        if (this.footer != null)
        {
            lvt_18_1_ = this.mc.fontRendererObj.listFormattedStringToWidth(this.footer.getFormattedText(), width - 50);

            for (String lvt_20_2_ : lvt_18_1_)
            {
                lvt_16_1_ = Math.max(lvt_16_1_, this.mc.fontRendererObj.getStringWidth(lvt_20_2_));
            }
        }

        if (lvt_17_1_ != null)
        {
            drawRect(width / 2 - lvt_16_1_ / 2 - 1, lvt_15_1_ - 1, width / 2 + lvt_16_1_ / 2 + 1, lvt_15_1_ + lvt_17_1_.size() * this.mc.fontRendererObj.FONT_HEIGHT, Integer.MIN_VALUE);

            for (String lvt_20_3_ : lvt_17_1_)
            {
                int lvt_21_1_ = this.mc.fontRendererObj.getStringWidth(lvt_20_3_);
                this.mc.fontRendererObj.drawStringWithShadow(lvt_20_3_, (float)(width / 2 - lvt_21_1_ / 2), (float)lvt_15_1_, -1);
                lvt_15_1_ += this.mc.fontRendererObj.FONT_HEIGHT;
            }

            ++lvt_15_1_;
        }

        drawRect(width / 2 - lvt_16_1_ / 2 - 1, lvt_15_1_ - 1, width / 2 + lvt_16_1_ / 2 + 1, lvt_15_1_ + lvt_9_2_ * 9, Integer.MIN_VALUE);

        for (int lvt_19_4_ = 0; lvt_19_4_ < lvt_8_2_; ++lvt_19_4_)
        {
            int lvt_20_4_ = lvt_19_4_ / lvt_9_2_;
            int lvt_21_2_ = lvt_19_4_ % lvt_9_2_;
            int lvt_22_1_ = lvt_14_1_ + lvt_20_4_ * lvt_13_1_ + lvt_20_4_ * 5;
            int lvt_23_1_ = lvt_15_1_ + lvt_21_2_ * 9;
            drawRect(lvt_22_1_, lvt_23_1_, lvt_22_1_ + lvt_13_1_, lvt_23_1_ + 8, 553648127);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

            if (lvt_19_4_ < lvt_5_1_.size())
            {
                NetworkPlayerInfo lvt_24_1_ = (NetworkPlayerInfo)lvt_5_1_.get(lvt_19_4_);
                String lvt_25_1_ = this.getPlayerName(lvt_24_1_);
                GameProfile lvt_26_1_ = lvt_24_1_.getGameProfile();

                if (lvt_11_1_)
                {
                    EntityPlayer lvt_27_1_ = this.mc.theWorld.getPlayerEntityByUUID(lvt_26_1_.getId());
                    boolean lvt_28_1_ = lvt_27_1_ != null && lvt_27_1_.isWearing(EnumPlayerModelParts.CAPE) && (lvt_26_1_.getName().equals("Dinnerbone") || lvt_26_1_.getName().equals("Grumm"));
                    this.mc.getTextureManager().bindTexture(lvt_24_1_.getLocationSkin());
                    int lvt_29_1_ = 8 + (lvt_28_1_ ? 8 : 0);
                    int lvt_30_1_ = 8 * (lvt_28_1_ ? -1 : 1);
                    Gui.drawScaledCustomSizeModalRect(lvt_22_1_, lvt_23_1_, 8.0F, (float)lvt_29_1_, 8, lvt_30_1_, 8, 8, 64.0F, 64.0F);

                    if (lvt_27_1_ != null && lvt_27_1_.isWearing(EnumPlayerModelParts.HAT))
                    {
                        int lvt_31_1_ = 8 + (lvt_28_1_ ? 8 : 0);
                        int lvt_32_1_ = 8 * (lvt_28_1_ ? -1 : 1);
                        Gui.drawScaledCustomSizeModalRect(lvt_22_1_, lvt_23_1_, 40.0F, (float)lvt_31_1_, 8, lvt_32_1_, 8, 8, 64.0F, 64.0F);
                    }

                    lvt_22_1_ += 9;
                }

                if (lvt_24_1_.getGameType() == WorldSettings.GameType.SPECTATOR)
                {
                    lvt_25_1_ = EnumChatFormatting.ITALIC + lvt_25_1_;
                    this.mc.fontRendererObj.drawStringWithShadow(lvt_25_1_, (float)lvt_22_1_, (float)lvt_23_1_, -1862270977);
                }
                else
                {
                    this.mc.fontRendererObj.drawStringWithShadow(lvt_25_1_, (float)lvt_22_1_, (float)lvt_23_1_, -1);
                }

                if (scoreObjectiveIn != null && lvt_24_1_.getGameType() != WorldSettings.GameType.SPECTATOR)
                {
                    int lvt_27_2_ = lvt_22_1_ + lvt_6_1_ + 1;
                    int lvt_28_2_ = lvt_27_2_ + lvt_12_1_;

                    if (lvt_28_2_ - lvt_27_2_ > 5)
                    {
                        this.drawScoreboardValues(scoreObjectiveIn, lvt_23_1_, lvt_26_1_.getName(), lvt_27_2_, lvt_28_2_, lvt_24_1_);
                    }
                }

                this.drawPing(lvt_13_1_, lvt_22_1_ - (lvt_11_1_ ? 9 : 0), lvt_23_1_, lvt_24_1_);
            }
        }

        if (lvt_18_1_ != null)
        {
            lvt_15_1_ = lvt_15_1_ + lvt_9_2_ * 9 + 1;
            drawRect(width / 2 - lvt_16_1_ / 2 - 1, lvt_15_1_ - 1, width / 2 + lvt_16_1_ / 2 + 1, lvt_15_1_ + lvt_18_1_.size() * this.mc.fontRendererObj.FONT_HEIGHT, Integer.MIN_VALUE);

            for (String lvt_20_5_ : lvt_18_1_)
            {
                int lvt_21_3_ = this.mc.fontRendererObj.getStringWidth(lvt_20_5_);
                this.mc.fontRendererObj.drawStringWithShadow(lvt_20_5_, (float)(width / 2 - lvt_21_3_ / 2), (float)lvt_15_1_, -1);
                lvt_15_1_ += this.mc.fontRendererObj.FONT_HEIGHT;
            }
        }
    }

    protected void drawPing(int p_175245_1_, int p_175245_2_, int p_175245_3_, NetworkPlayerInfo networkPlayerInfoIn)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(icons);
        int lvt_5_1_ = 0;
        int lvt_6_1_ = 0;

        if (networkPlayerInfoIn.getResponseTime() < 0)
        {
            lvt_6_1_ = 5;
        }
        else if (networkPlayerInfoIn.getResponseTime() < 150)
        {
            lvt_6_1_ = 0;
        }
        else if (networkPlayerInfoIn.getResponseTime() < 300)
        {
            lvt_6_1_ = 1;
        }
        else if (networkPlayerInfoIn.getResponseTime() < 600)
        {
            lvt_6_1_ = 2;
        }
        else if (networkPlayerInfoIn.getResponseTime() < 1000)
        {
            lvt_6_1_ = 3;
        }
        else
        {
            lvt_6_1_ = 4;
        }

        this.zLevel += 100.0F;
        this.drawTexturedModalRect(p_175245_2_ + p_175245_1_ - 11, p_175245_3_, 0 + lvt_5_1_ * 10, 176 + lvt_6_1_ * 8, 10, 8);
        this.zLevel -= 100.0F;
    }

    private void drawScoreboardValues(ScoreObjective p_175247_1_, int p_175247_2_, String p_175247_3_, int p_175247_4_, int p_175247_5_, NetworkPlayerInfo p_175247_6_)
    {
        int lvt_7_1_ = p_175247_1_.getScoreboard().getValueFromObjective(p_175247_3_, p_175247_1_).getScorePoints();

        if (p_175247_1_.getRenderType() == IScoreObjectiveCriteria.EnumRenderType.HEARTS)
        {
            this.mc.getTextureManager().bindTexture(icons);

            if (this.lastTimeOpened == p_175247_6_.func_178855_p())
            {
                if (lvt_7_1_ < p_175247_6_.func_178835_l())
                {
                    p_175247_6_.func_178846_a(Minecraft.getSystemTime());
                    p_175247_6_.func_178844_b((long)(this.guiIngame.getUpdateCounter() + 20));
                }
                else if (lvt_7_1_ > p_175247_6_.func_178835_l())
                {
                    p_175247_6_.func_178846_a(Minecraft.getSystemTime());
                    p_175247_6_.func_178844_b((long)(this.guiIngame.getUpdateCounter() + 10));
                }
            }

            if (Minecraft.getSystemTime() - p_175247_6_.func_178847_n() > 1000L || this.lastTimeOpened != p_175247_6_.func_178855_p())
            {
                p_175247_6_.func_178836_b(lvt_7_1_);
                p_175247_6_.func_178857_c(lvt_7_1_);
                p_175247_6_.func_178846_a(Minecraft.getSystemTime());
            }

            p_175247_6_.func_178843_c(this.lastTimeOpened);
            p_175247_6_.func_178836_b(lvt_7_1_);
            int lvt_8_1_ = MathHelper.ceiling_float_int((float)Math.max(lvt_7_1_, p_175247_6_.func_178860_m()) / 2.0F);
            int lvt_9_1_ = Math.max(MathHelper.ceiling_float_int((float)(lvt_7_1_ / 2)), Math.max(MathHelper.ceiling_float_int((float)(p_175247_6_.func_178860_m() / 2)), 10));
            boolean lvt_10_1_ = p_175247_6_.func_178858_o() > (long)this.guiIngame.getUpdateCounter() && (p_175247_6_.func_178858_o() - (long)this.guiIngame.getUpdateCounter()) / 3L % 2L == 1L;

            if (lvt_8_1_ > 0)
            {
                float lvt_11_1_ = Math.min((float)(p_175247_5_ - p_175247_4_ - 4) / (float)lvt_9_1_, 9.0F);

                if (lvt_11_1_ > 3.0F)
                {
                    for (int lvt_12_1_ = lvt_8_1_; lvt_12_1_ < lvt_9_1_; ++lvt_12_1_)
                    {
                        this.drawTexturedModalRect((float)p_175247_4_ + (float)lvt_12_1_ * lvt_11_1_, (float)p_175247_2_, lvt_10_1_ ? 25 : 16, 0, 9, 9);
                    }

                    for (int lvt_12_2_ = 0; lvt_12_2_ < lvt_8_1_; ++lvt_12_2_)
                    {
                        this.drawTexturedModalRect((float)p_175247_4_ + (float)lvt_12_2_ * lvt_11_1_, (float)p_175247_2_, lvt_10_1_ ? 25 : 16, 0, 9, 9);

                        if (lvt_10_1_)
                        {
                            if (lvt_12_2_ * 2 + 1 < p_175247_6_.func_178860_m())
                            {
                                this.drawTexturedModalRect((float)p_175247_4_ + (float)lvt_12_2_ * lvt_11_1_, (float)p_175247_2_, 70, 0, 9, 9);
                            }

                            if (lvt_12_2_ * 2 + 1 == p_175247_6_.func_178860_m())
                            {
                                this.drawTexturedModalRect((float)p_175247_4_ + (float)lvt_12_2_ * lvt_11_1_, (float)p_175247_2_, 79, 0, 9, 9);
                            }
                        }

                        if (lvt_12_2_ * 2 + 1 < lvt_7_1_)
                        {
                            this.drawTexturedModalRect((float)p_175247_4_ + (float)lvt_12_2_ * lvt_11_1_, (float)p_175247_2_, lvt_12_2_ >= 10 ? 160 : 52, 0, 9, 9);
                        }

                        if (lvt_12_2_ * 2 + 1 == lvt_7_1_)
                        {
                            this.drawTexturedModalRect((float)p_175247_4_ + (float)lvt_12_2_ * lvt_11_1_, (float)p_175247_2_, lvt_12_2_ >= 10 ? 169 : 61, 0, 9, 9);
                        }
                    }
                }
                else
                {
                    float lvt_12_3_ = MathHelper.clamp_float((float)lvt_7_1_ / 20.0F, 0.0F, 1.0F);
                    int lvt_13_1_ = (int)((1.0F - lvt_12_3_) * 255.0F) << 16 | (int)(lvt_12_3_ * 255.0F) << 8;
                    String lvt_14_1_ = "" + (float)lvt_7_1_ / 2.0F;

                    if (p_175247_5_ - this.mc.fontRendererObj.getStringWidth(lvt_14_1_ + "hp") >= p_175247_4_)
                    {
                        lvt_14_1_ = lvt_14_1_ + "hp";
                    }

                    this.mc.fontRendererObj.drawStringWithShadow(lvt_14_1_, (float)((p_175247_5_ + p_175247_4_) / 2 - this.mc.fontRendererObj.getStringWidth(lvt_14_1_) / 2), (float)p_175247_2_, lvt_13_1_);
                }
            }
        }
        else
        {
            String lvt_8_2_ = EnumChatFormatting.YELLOW + "" + lvt_7_1_;
            this.mc.fontRendererObj.drawStringWithShadow(lvt_8_2_, (float)(p_175247_5_ - this.mc.fontRendererObj.getStringWidth(lvt_8_2_)), (float)p_175247_2_, 16777215);
        }
    }

    public void setFooter(IChatComponent footerIn)
    {
        this.footer = footerIn;
    }

    public void setHeader(IChatComponent headerIn)
    {
        this.header = headerIn;
    }

    public void resetFooterHeader()
    {
        this.header = null;
        this.footer = null;
    }

    static class PlayerComparator implements Comparator<NetworkPlayerInfo>
    {
        private PlayerComparator()
        {
        }

        public int compare(NetworkPlayerInfo p_compare_1_, NetworkPlayerInfo p_compare_2_)
        {
            ScorePlayerTeam lvt_3_1_ = p_compare_1_.getPlayerTeam();
            ScorePlayerTeam lvt_4_1_ = p_compare_2_.getPlayerTeam();
            return ComparisonChain.start().compareTrueFirst(p_compare_1_.getGameType() != WorldSettings.GameType.SPECTATOR, p_compare_2_.getGameType() != WorldSettings.GameType.SPECTATOR).compare(lvt_3_1_ != null ? lvt_3_1_.getRegisteredName() : "", lvt_4_1_ != null ? lvt_4_1_.getRegisteredName() : "").compare(p_compare_1_.getGameProfile().getName(), p_compare_2_.getGameProfile().getName()).result();
        }

        public int compare(Object p_compare_1_, Object p_compare_2_)
        {
            return this.compare((NetworkPlayerInfo)p_compare_1_, (NetworkPlayerInfo)p_compare_2_);
        }
    }
}
