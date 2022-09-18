package net.minecraft.client.gui;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.border.WorldBorder;

public class GuiIngame extends Gui
{
    private static final ResourceLocation vignetteTexPath = new ResourceLocation("textures/misc/vignette.png");
    private static final ResourceLocation widgetsTexPath = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation pumpkinBlurTexPath = new ResourceLocation("textures/misc/pumpkinblur.png");
    private final Random rand = new Random();
    private final Minecraft mc;
    private final RenderItem itemRenderer;

    /** ChatGUI instance that retains all previous chat data */
    private final GuiNewChat persistantChatGUI;
    private final GuiStreamIndicator streamIndicator;
    private int updateCounter;

    /** The string specifying which record music is playing */
    private String recordPlaying = "";

    /** How many ticks the record playing message will be displayed */
    private int recordPlayingUpFor;
    private boolean recordIsPlaying;

    /** Previous frame vignette brightness (slowly changes by 1% each frame) */
    public float prevVignetteBrightness = 1.0F;

    /** Remaining ticks the item highlight should be visible */
    private int remainingHighlightTicks;

    /** The ItemStack that is currently being highlighted */
    private ItemStack highlightingItemStack;
    private final GuiOverlayDebug overlayDebug;

    /** The spectator GUI for this in-game GUI instance */
    private final GuiSpectator spectatorGui;
    private final GuiPlayerTabOverlay overlayPlayerList;

    /** A timer for the current title and subtitle displayed */
    private int titlesTimer;

    /** The current title displayed */
    private String displayedTitle = "";

    /** The current sub-title displayed */
    private String displayedSubTitle = "";

    /** The time that the title take to fade in */
    private int titleFadeIn;

    /** The time that the title is display */
    private int titleDisplayTime;

    /** The time that the title take to fade out */
    private int titleFadeOut;
    private int playerHealth = 0;
    private int lastPlayerHealth = 0;

    /** The last recorded system time */
    private long lastSystemTime = 0L;

    /** Used with updateCounter to make the heart bar flash */
    private long healthUpdateCounter = 0L;

    public GuiIngame(Minecraft mcIn)
    {
        this.mc = mcIn;
        this.itemRenderer = mcIn.getRenderItem();
        this.overlayDebug = new GuiOverlayDebug(mcIn);
        this.spectatorGui = new GuiSpectator(mcIn);
        this.persistantChatGUI = new GuiNewChat(mcIn);
        this.streamIndicator = new GuiStreamIndicator(mcIn);
        this.overlayPlayerList = new GuiPlayerTabOverlay(mcIn, this);
        this.setDefaultTitlesTimes();
    }

    /**
     * Set the differents times for the titles to their default values
     */
    public void setDefaultTitlesTimes()
    {
        this.titleFadeIn = 10;
        this.titleDisplayTime = 70;
        this.titleFadeOut = 20;
    }

    public void renderGameOverlay(float partialTicks)
    {
        ScaledResolution lvt_2_1_ = new ScaledResolution(this.mc);
        int lvt_3_1_ = lvt_2_1_.getScaledWidth();
        int lvt_4_1_ = lvt_2_1_.getScaledHeight();
        this.mc.entityRenderer.setupOverlayRendering();
        GlStateManager.enableBlend();

        if (Minecraft.isFancyGraphicsEnabled())
        {
            this.renderVignette(this.mc.thePlayer.getBrightness(partialTicks), lvt_2_1_);
        }
        else
        {
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        }

        ItemStack lvt_5_1_ = this.mc.thePlayer.inventory.armorItemInSlot(3);

        if (this.mc.gameSettings.thirdPersonView == 0 && lvt_5_1_ != null && lvt_5_1_.getItem() == Item.getItemFromBlock(Blocks.pumpkin))
        {
            this.renderPumpkinOverlay(lvt_2_1_);
        }

        if (!this.mc.thePlayer.isPotionActive(Potion.confusion))
        {
            float lvt_6_1_ = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * partialTicks;

            if (lvt_6_1_ > 0.0F)
            {
                this.renderPortal(lvt_6_1_, lvt_2_1_);
            }
        }

        if (this.mc.playerController.isSpectator())
        {
            this.spectatorGui.renderTooltip(lvt_2_1_, partialTicks);
        }
        else
        {
            this.renderTooltip(lvt_2_1_, partialTicks);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(icons);
        GlStateManager.enableBlend();

        if (this.showCrosshair())
        {
            GlStateManager.tryBlendFuncSeparate(775, 769, 1, 0);
            GlStateManager.enableAlpha();
            this.drawTexturedModalRect(lvt_3_1_ / 2 - 7, lvt_4_1_ / 2 - 7, 0, 0, 16, 16);
        }

        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        this.mc.mcProfiler.startSection("bossHealth");
        this.renderBossHealth();
        this.mc.mcProfiler.endSection();

        if (this.mc.playerController.shouldDrawHUD())
        {
            this.renderPlayerStats(lvt_2_1_);
        }

        GlStateManager.disableBlend();

        if (this.mc.thePlayer.getSleepTimer() > 0)
        {
            this.mc.mcProfiler.startSection("sleep");
            GlStateManager.disableDepth();
            GlStateManager.disableAlpha();
            int lvt_6_2_ = this.mc.thePlayer.getSleepTimer();
            float lvt_7_1_ = (float)lvt_6_2_ / 100.0F;

            if (lvt_7_1_ > 1.0F)
            {
                lvt_7_1_ = 1.0F - (float)(lvt_6_2_ - 100) / 10.0F;
            }

            int lvt_8_1_ = (int)(220.0F * lvt_7_1_) << 24 | 1052704;
            drawRect(0, 0, lvt_3_1_, lvt_4_1_, lvt_8_1_);
            GlStateManager.enableAlpha();
            GlStateManager.enableDepth();
            this.mc.mcProfiler.endSection();
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        int lvt_6_3_ = lvt_3_1_ / 2 - 91;

        if (this.mc.thePlayer.isRidingHorse())
        {
            this.renderHorseJumpBar(lvt_2_1_, lvt_6_3_);
        }
        else if (this.mc.playerController.gameIsSurvivalOrAdventure())
        {
            this.renderExpBar(lvt_2_1_, lvt_6_3_);
        }

        if (this.mc.gameSettings.heldItemTooltips && !this.mc.playerController.isSpectator())
        {
            this.renderSelectedItem(lvt_2_1_);
        }
        else if (this.mc.thePlayer.isSpectator())
        {
            this.spectatorGui.renderSelectedItem(lvt_2_1_);
        }

        if (this.mc.isDemo())
        {
            this.renderDemo(lvt_2_1_);
        }

        if (this.mc.gameSettings.showDebugInfo)
        {
            this.overlayDebug.renderDebugInfo(lvt_2_1_);
        }

        if (this.recordPlayingUpFor > 0)
        {
            this.mc.mcProfiler.startSection("overlayMessage");
            float lvt_7_2_ = (float)this.recordPlayingUpFor - partialTicks;
            int lvt_8_2_ = (int)(lvt_7_2_ * 255.0F / 20.0F);

            if (lvt_8_2_ > 255)
            {
                lvt_8_2_ = 255;
            }

            if (lvt_8_2_ > 8)
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)(lvt_3_1_ / 2), (float)(lvt_4_1_ - 68), 0.0F);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                int lvt_9_1_ = 16777215;

                if (this.recordIsPlaying)
                {
                    lvt_9_1_ = MathHelper.hsvToRGB(lvt_7_2_ / 50.0F, 0.7F, 0.6F) & 16777215;
                }

                this.getFontRenderer().drawString(this.recordPlaying, -this.getFontRenderer().getStringWidth(this.recordPlaying) / 2, -4, lvt_9_1_ + (lvt_8_2_ << 24 & -16777216));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }

            this.mc.mcProfiler.endSection();
        }

        if (this.titlesTimer > 0)
        {
            this.mc.mcProfiler.startSection("titleAndSubtitle");
            float lvt_7_3_ = (float)this.titlesTimer - partialTicks;
            int lvt_8_3_ = 255;

            if (this.titlesTimer > this.titleFadeOut + this.titleDisplayTime)
            {
                float lvt_9_2_ = (float)(this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut) - lvt_7_3_;
                lvt_8_3_ = (int)(lvt_9_2_ * 255.0F / (float)this.titleFadeIn);
            }

            if (this.titlesTimer <= this.titleFadeOut)
            {
                lvt_8_3_ = (int)(lvt_7_3_ * 255.0F / (float)this.titleFadeOut);
            }

            lvt_8_3_ = MathHelper.clamp_int(lvt_8_3_, 0, 255);

            if (lvt_8_3_ > 8)
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)(lvt_3_1_ / 2), (float)(lvt_4_1_ / 2), 0.0F);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.pushMatrix();
                GlStateManager.scale(4.0F, 4.0F, 4.0F);
                int lvt_9_4_ = lvt_8_3_ << 24 & -16777216;
                this.getFontRenderer().drawString(this.displayedTitle, (float)(-this.getFontRenderer().getStringWidth(this.displayedTitle) / 2), -10.0F, 16777215 | lvt_9_4_, true);
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.scale(2.0F, 2.0F, 2.0F);
                this.getFontRenderer().drawString(this.displayedSubTitle, (float)(-this.getFontRenderer().getStringWidth(this.displayedSubTitle) / 2), 5.0F, 16777215 | lvt_9_4_, true);
                GlStateManager.popMatrix();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }

            this.mc.mcProfiler.endSection();
        }

        Scoreboard lvt_7_4_ = this.mc.theWorld.getScoreboard();
        ScoreObjective lvt_8_4_ = null;
        ScorePlayerTeam lvt_9_5_ = lvt_7_4_.getPlayersTeam(this.mc.thePlayer.getName());

        if (lvt_9_5_ != null)
        {
            int lvt_10_1_ = lvt_9_5_.getChatFormat().getColorIndex();

            if (lvt_10_1_ >= 0)
            {
                lvt_8_4_ = lvt_7_4_.getObjectiveInDisplaySlot(3 + lvt_10_1_);
            }
        }

        ScoreObjective lvt_10_2_ = lvt_8_4_ != null ? lvt_8_4_ : lvt_7_4_.getObjectiveInDisplaySlot(1);

        if (lvt_10_2_ != null)
        {
            this.renderScoreboard(lvt_10_2_, lvt_2_1_);
        }

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, (float)(lvt_4_1_ - 48), 0.0F);
        this.mc.mcProfiler.startSection("chat");
        this.persistantChatGUI.drawChat(this.updateCounter);
        this.mc.mcProfiler.endSection();
        GlStateManager.popMatrix();
        lvt_10_2_ = lvt_7_4_.getObjectiveInDisplaySlot(0);

        if (!this.mc.gameSettings.keyBindPlayerList.isKeyDown() || this.mc.isIntegratedServerRunning() && this.mc.thePlayer.sendQueue.getPlayerInfoMap().size() <= 1 && lvt_10_2_ == null)
        {
            this.overlayPlayerList.updatePlayerList(false);
        }
        else
        {
            this.overlayPlayerList.updatePlayerList(true);
            this.overlayPlayerList.renderPlayerlist(lvt_3_1_, lvt_7_4_, lvt_10_2_);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
    }

    protected void renderTooltip(ScaledResolution sr, float partialTicks)
    {
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer)
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(widgetsTexPath);
            EntityPlayer lvt_3_1_ = (EntityPlayer)this.mc.getRenderViewEntity();
            int lvt_4_1_ = sr.getScaledWidth() / 2;
            float lvt_5_1_ = this.zLevel;
            this.zLevel = -90.0F;
            this.drawTexturedModalRect(lvt_4_1_ - 91, sr.getScaledHeight() - 22, 0, 0, 182, 22);
            this.drawTexturedModalRect(lvt_4_1_ - 91 - 1 + lvt_3_1_.inventory.currentItem * 20, sr.getScaledHeight() - 22 - 1, 0, 22, 24, 22);
            this.zLevel = lvt_5_1_;
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            RenderHelper.enableGUIStandardItemLighting();

            for (int lvt_6_1_ = 0; lvt_6_1_ < 9; ++lvt_6_1_)
            {
                int lvt_7_1_ = sr.getScaledWidth() / 2 - 90 + lvt_6_1_ * 20 + 2;
                int lvt_8_1_ = sr.getScaledHeight() - 16 - 3;
                this.renderHotbarItem(lvt_6_1_, lvt_7_1_, lvt_8_1_, partialTicks, lvt_3_1_);
            }

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
        }
    }

    public void renderHorseJumpBar(ScaledResolution scaledRes, int x)
    {
        this.mc.mcProfiler.startSection("jumpBar");
        this.mc.getTextureManager().bindTexture(Gui.icons);
        float lvt_3_1_ = this.mc.thePlayer.getHorseJumpPower();
        int lvt_4_1_ = 182;
        int lvt_5_1_ = (int)(lvt_3_1_ * (float)(lvt_4_1_ + 1));
        int lvt_6_1_ = scaledRes.getScaledHeight() - 32 + 3;
        this.drawTexturedModalRect(x, lvt_6_1_, 0, 84, lvt_4_1_, 5);

        if (lvt_5_1_ > 0)
        {
            this.drawTexturedModalRect(x, lvt_6_1_, 0, 89, lvt_5_1_, 5);
        }

        this.mc.mcProfiler.endSection();
    }

    public void renderExpBar(ScaledResolution scaledRes, int x)
    {
        this.mc.mcProfiler.startSection("expBar");
        this.mc.getTextureManager().bindTexture(Gui.icons);
        int lvt_3_1_ = this.mc.thePlayer.xpBarCap();

        if (lvt_3_1_ > 0)
        {
            int lvt_4_1_ = 182;
            int lvt_5_1_ = (int)(this.mc.thePlayer.experience * (float)(lvt_4_1_ + 1));
            int lvt_6_1_ = scaledRes.getScaledHeight() - 32 + 3;
            this.drawTexturedModalRect(x, lvt_6_1_, 0, 64, lvt_4_1_, 5);

            if (lvt_5_1_ > 0)
            {
                this.drawTexturedModalRect(x, lvt_6_1_, 0, 69, lvt_5_1_, 5);
            }
        }

        this.mc.mcProfiler.endSection();

        if (this.mc.thePlayer.experienceLevel > 0)
        {
            this.mc.mcProfiler.startSection("expLevel");
            int lvt_4_2_ = 8453920;
            String lvt_5_2_ = "" + this.mc.thePlayer.experienceLevel;
            int lvt_6_2_ = (scaledRes.getScaledWidth() - this.getFontRenderer().getStringWidth(lvt_5_2_)) / 2;
            int lvt_7_1_ = scaledRes.getScaledHeight() - 31 - 4;
            int lvt_8_1_ = 0;
            this.getFontRenderer().drawString(lvt_5_2_, lvt_6_2_ + 1, lvt_7_1_, 0);
            this.getFontRenderer().drawString(lvt_5_2_, lvt_6_2_ - 1, lvt_7_1_, 0);
            this.getFontRenderer().drawString(lvt_5_2_, lvt_6_2_, lvt_7_1_ + 1, 0);
            this.getFontRenderer().drawString(lvt_5_2_, lvt_6_2_, lvt_7_1_ - 1, 0);
            this.getFontRenderer().drawString(lvt_5_2_, lvt_6_2_, lvt_7_1_, lvt_4_2_);
            this.mc.mcProfiler.endSection();
        }
    }

    public void renderSelectedItem(ScaledResolution scaledRes)
    {
        this.mc.mcProfiler.startSection("selectedItemName");

        if (this.remainingHighlightTicks > 0 && this.highlightingItemStack != null)
        {
            String lvt_2_1_ = this.highlightingItemStack.getDisplayName();

            if (this.highlightingItemStack.hasDisplayName())
            {
                lvt_2_1_ = EnumChatFormatting.ITALIC + lvt_2_1_;
            }

            int lvt_3_1_ = (scaledRes.getScaledWidth() - this.getFontRenderer().getStringWidth(lvt_2_1_)) / 2;
            int lvt_4_1_ = scaledRes.getScaledHeight() - 59;

            if (!this.mc.playerController.shouldDrawHUD())
            {
                lvt_4_1_ += 14;
            }

            int lvt_5_1_ = (int)((float)this.remainingHighlightTicks * 256.0F / 10.0F);

            if (lvt_5_1_ > 255)
            {
                lvt_5_1_ = 255;
            }

            if (lvt_5_1_ > 0)
            {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                this.getFontRenderer().drawStringWithShadow(lvt_2_1_, (float)lvt_3_1_, (float)lvt_4_1_, 16777215 + (lvt_5_1_ << 24));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }

        this.mc.mcProfiler.endSection();
    }

    public void renderDemo(ScaledResolution scaledRes)
    {
        this.mc.mcProfiler.startSection("demo");
        String lvt_2_1_ = "";

        if (this.mc.theWorld.getTotalWorldTime() >= 120500L)
        {
            lvt_2_1_ = I18n.format("demo.demoExpired", new Object[0]);
        }
        else
        {
            lvt_2_1_ = I18n.format("demo.remainingTime", new Object[] {StringUtils.ticksToElapsedTime((int)(120500L - this.mc.theWorld.getTotalWorldTime()))});
        }

        int lvt_3_1_ = this.getFontRenderer().getStringWidth(lvt_2_1_);
        this.getFontRenderer().drawStringWithShadow(lvt_2_1_, (float)(scaledRes.getScaledWidth() - lvt_3_1_ - 10), 5.0F, 16777215);
        this.mc.mcProfiler.endSection();
    }

    protected boolean showCrosshair()
    {
        if (this.mc.gameSettings.showDebugInfo && !this.mc.thePlayer.hasReducedDebug() && !this.mc.gameSettings.reducedDebugInfo)
        {
            return false;
        }
        else if (this.mc.playerController.isSpectator())
        {
            if (this.mc.pointedEntity != null)
            {
                return true;
            }
            else
            {
                if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
                {
                    BlockPos lvt_1_1_ = this.mc.objectMouseOver.getBlockPos();

                    if (this.mc.theWorld.getTileEntity(lvt_1_1_) instanceof IInventory)
                    {
                        return true;
                    }
                }

                return false;
            }
        }
        else
        {
            return true;
        }
    }

    public void renderStreamIndicator(ScaledResolution scaledRes)
    {
        this.streamIndicator.render(scaledRes.getScaledWidth() - 10, 10);
    }

    private void renderScoreboard(ScoreObjective objective, ScaledResolution scaledRes)
    {
        Scoreboard lvt_3_1_ = objective.getScoreboard();
        Collection<Score> lvt_4_1_ = lvt_3_1_.getSortedScores(objective);
        List<Score> lvt_5_1_ = Lists.newArrayList(Iterables.filter(lvt_4_1_, new Predicate<Score>()
        {
            public boolean apply(Score p_apply_1_)
            {
                return p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#");
            }
            public boolean apply(Object p_apply_1_)
            {
                return this.apply((Score)p_apply_1_);
            }
        }));

        if (lvt_5_1_.size() > 15)
        {
            lvt_4_1_ = Lists.newArrayList(Iterables.skip(lvt_5_1_, lvt_4_1_.size() - 15));
        }
        else
        {
            lvt_4_1_ = lvt_5_1_;
        }

        int lvt_6_1_ = this.getFontRenderer().getStringWidth(objective.getDisplayName());

        for (Score lvt_8_1_ : lvt_4_1_)
        {
            ScorePlayerTeam lvt_9_1_ = lvt_3_1_.getPlayersTeam(lvt_8_1_.getPlayerName());
            String lvt_10_1_ = ScorePlayerTeam.formatPlayerName(lvt_9_1_, lvt_8_1_.getPlayerName()) + ": " + EnumChatFormatting.RED + lvt_8_1_.getScorePoints();
            lvt_6_1_ = Math.max(lvt_6_1_, this.getFontRenderer().getStringWidth(lvt_10_1_));
        }

        int lvt_7_2_ = lvt_4_1_.size() * this.getFontRenderer().FONT_HEIGHT;
        int lvt_8_2_ = scaledRes.getScaledHeight() / 2 + lvt_7_2_ / 3;
        int lvt_9_2_ = 3;
        int lvt_10_2_ = scaledRes.getScaledWidth() - lvt_6_1_ - lvt_9_2_;
        int lvt_11_1_ = 0;

        for (Score lvt_13_1_ : lvt_4_1_)
        {
            ++lvt_11_1_;
            ScorePlayerTeam lvt_14_1_ = lvt_3_1_.getPlayersTeam(lvt_13_1_.getPlayerName());
            String lvt_15_1_ = ScorePlayerTeam.formatPlayerName(lvt_14_1_, lvt_13_1_.getPlayerName());
            String lvt_16_1_ = EnumChatFormatting.RED + "" + lvt_13_1_.getScorePoints();
            int lvt_18_1_ = lvt_8_2_ - lvt_11_1_ * this.getFontRenderer().FONT_HEIGHT;
            int lvt_19_1_ = scaledRes.getScaledWidth() - lvt_9_2_ + 2;
            drawRect(lvt_10_2_ - 2, lvt_18_1_, lvt_19_1_, lvt_18_1_ + this.getFontRenderer().FONT_HEIGHT, 1342177280);
            this.getFontRenderer().drawString(lvt_15_1_, lvt_10_2_, lvt_18_1_, 553648127);
            this.getFontRenderer().drawString(lvt_16_1_, lvt_19_1_ - this.getFontRenderer().getStringWidth(lvt_16_1_), lvt_18_1_, 553648127);

            if (lvt_11_1_ == lvt_4_1_.size())
            {
                String lvt_20_1_ = objective.getDisplayName();
                drawRect(lvt_10_2_ - 2, lvt_18_1_ - this.getFontRenderer().FONT_HEIGHT - 1, lvt_19_1_, lvt_18_1_ - 1, 1610612736);
                drawRect(lvt_10_2_ - 2, lvt_18_1_ - 1, lvt_19_1_, lvt_18_1_, 1342177280);
                this.getFontRenderer().drawString(lvt_20_1_, lvt_10_2_ + lvt_6_1_ / 2 - this.getFontRenderer().getStringWidth(lvt_20_1_) / 2, lvt_18_1_ - this.getFontRenderer().FONT_HEIGHT, 553648127);
            }
        }
    }

    private void renderPlayerStats(ScaledResolution scaledRes)
    {
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer)
        {
            EntityPlayer lvt_2_1_ = (EntityPlayer)this.mc.getRenderViewEntity();
            int lvt_3_1_ = MathHelper.ceiling_float_int(lvt_2_1_.getHealth());
            boolean lvt_4_1_ = this.healthUpdateCounter > (long)this.updateCounter && (this.healthUpdateCounter - (long)this.updateCounter) / 3L % 2L == 1L;

            if (lvt_3_1_ < this.playerHealth && lvt_2_1_.hurtResistantTime > 0)
            {
                this.lastSystemTime = Minecraft.getSystemTime();
                this.healthUpdateCounter = (long)(this.updateCounter + 20);
            }
            else if (lvt_3_1_ > this.playerHealth && lvt_2_1_.hurtResistantTime > 0)
            {
                this.lastSystemTime = Minecraft.getSystemTime();
                this.healthUpdateCounter = (long)(this.updateCounter + 10);
            }

            if (Minecraft.getSystemTime() - this.lastSystemTime > 1000L)
            {
                this.playerHealth = lvt_3_1_;
                this.lastPlayerHealth = lvt_3_1_;
                this.lastSystemTime = Minecraft.getSystemTime();
            }

            this.playerHealth = lvt_3_1_;
            int lvt_5_1_ = this.lastPlayerHealth;
            this.rand.setSeed((long)(this.updateCounter * 312871));
            boolean lvt_6_1_ = false;
            FoodStats lvt_7_1_ = lvt_2_1_.getFoodStats();
            int lvt_8_1_ = lvt_7_1_.getFoodLevel();
            int lvt_9_1_ = lvt_7_1_.getPrevFoodLevel();
            IAttributeInstance lvt_10_1_ = lvt_2_1_.getEntityAttribute(SharedMonsterAttributes.maxHealth);
            int lvt_11_1_ = scaledRes.getScaledWidth() / 2 - 91;
            int lvt_12_1_ = scaledRes.getScaledWidth() / 2 + 91;
            int lvt_13_1_ = scaledRes.getScaledHeight() - 39;
            float lvt_14_1_ = (float)lvt_10_1_.getAttributeValue();
            float lvt_15_1_ = lvt_2_1_.getAbsorptionAmount();
            int lvt_16_1_ = MathHelper.ceiling_float_int((lvt_14_1_ + lvt_15_1_) / 2.0F / 10.0F);
            int lvt_17_1_ = Math.max(10 - (lvt_16_1_ - 2), 3);
            int lvt_18_1_ = lvt_13_1_ - (lvt_16_1_ - 1) * lvt_17_1_ - 10;
            float lvt_19_1_ = lvt_15_1_;
            int lvt_20_1_ = lvt_2_1_.getTotalArmorValue();
            int lvt_21_1_ = -1;

            if (lvt_2_1_.isPotionActive(Potion.regeneration))
            {
                lvt_21_1_ = this.updateCounter % MathHelper.ceiling_float_int(lvt_14_1_ + 5.0F);
            }

            this.mc.mcProfiler.startSection("armor");

            for (int lvt_22_1_ = 0; lvt_22_1_ < 10; ++lvt_22_1_)
            {
                if (lvt_20_1_ > 0)
                {
                    int lvt_23_1_ = lvt_11_1_ + lvt_22_1_ * 8;

                    if (lvt_22_1_ * 2 + 1 < lvt_20_1_)
                    {
                        this.drawTexturedModalRect(lvt_23_1_, lvt_18_1_, 34, 9, 9, 9);
                    }

                    if (lvt_22_1_ * 2 + 1 == lvt_20_1_)
                    {
                        this.drawTexturedModalRect(lvt_23_1_, lvt_18_1_, 25, 9, 9, 9);
                    }

                    if (lvt_22_1_ * 2 + 1 > lvt_20_1_)
                    {
                        this.drawTexturedModalRect(lvt_23_1_, lvt_18_1_, 16, 9, 9, 9);
                    }
                }
            }

            this.mc.mcProfiler.endStartSection("health");

            for (int lvt_22_2_ = MathHelper.ceiling_float_int((lvt_14_1_ + lvt_15_1_) / 2.0F) - 1; lvt_22_2_ >= 0; --lvt_22_2_)
            {
                int lvt_23_2_ = 16;

                if (lvt_2_1_.isPotionActive(Potion.poison))
                {
                    lvt_23_2_ += 36;
                }
                else if (lvt_2_1_.isPotionActive(Potion.wither))
                {
                    lvt_23_2_ += 72;
                }

                int lvt_24_1_ = 0;

                if (lvt_4_1_)
                {
                    lvt_24_1_ = 1;
                }

                int lvt_25_1_ = MathHelper.ceiling_float_int((float)(lvt_22_2_ + 1) / 10.0F) - 1;
                int lvt_26_1_ = lvt_11_1_ + lvt_22_2_ % 10 * 8;
                int lvt_27_1_ = lvt_13_1_ - lvt_25_1_ * lvt_17_1_;

                if (lvt_3_1_ <= 4)
                {
                    lvt_27_1_ += this.rand.nextInt(2);
                }

                if (lvt_22_2_ == lvt_21_1_)
                {
                    lvt_27_1_ -= 2;
                }

                int lvt_28_1_ = 0;

                if (lvt_2_1_.worldObj.getWorldInfo().isHardcoreModeEnabled())
                {
                    lvt_28_1_ = 5;
                }

                this.drawTexturedModalRect(lvt_26_1_, lvt_27_1_, 16 + lvt_24_1_ * 9, 9 * lvt_28_1_, 9, 9);

                if (lvt_4_1_)
                {
                    if (lvt_22_2_ * 2 + 1 < lvt_5_1_)
                    {
                        this.drawTexturedModalRect(lvt_26_1_, lvt_27_1_, lvt_23_2_ + 54, 9 * lvt_28_1_, 9, 9);
                    }

                    if (lvt_22_2_ * 2 + 1 == lvt_5_1_)
                    {
                        this.drawTexturedModalRect(lvt_26_1_, lvt_27_1_, lvt_23_2_ + 63, 9 * lvt_28_1_, 9, 9);
                    }
                }

                if (lvt_19_1_ > 0.0F)
                {
                    if (lvt_19_1_ == lvt_15_1_ && lvt_15_1_ % 2.0F == 1.0F)
                    {
                        this.drawTexturedModalRect(lvt_26_1_, lvt_27_1_, lvt_23_2_ + 153, 9 * lvt_28_1_, 9, 9);
                    }
                    else
                    {
                        this.drawTexturedModalRect(lvt_26_1_, lvt_27_1_, lvt_23_2_ + 144, 9 * lvt_28_1_, 9, 9);
                    }

                    lvt_19_1_ -= 2.0F;
                }
                else
                {
                    if (lvt_22_2_ * 2 + 1 < lvt_3_1_)
                    {
                        this.drawTexturedModalRect(lvt_26_1_, lvt_27_1_, lvt_23_2_ + 36, 9 * lvt_28_1_, 9, 9);
                    }

                    if (lvt_22_2_ * 2 + 1 == lvt_3_1_)
                    {
                        this.drawTexturedModalRect(lvt_26_1_, lvt_27_1_, lvt_23_2_ + 45, 9 * lvt_28_1_, 9, 9);
                    }
                }
            }

            Entity lvt_22_3_ = lvt_2_1_.ridingEntity;

            if (lvt_22_3_ == null)
            {
                this.mc.mcProfiler.endStartSection("food");

                for (int lvt_23_3_ = 0; lvt_23_3_ < 10; ++lvt_23_3_)
                {
                    int lvt_24_2_ = lvt_13_1_;
                    int lvt_25_2_ = 16;
                    int lvt_26_2_ = 0;

                    if (lvt_2_1_.isPotionActive(Potion.hunger))
                    {
                        lvt_25_2_ += 36;
                        lvt_26_2_ = 13;
                    }

                    if (lvt_2_1_.getFoodStats().getSaturationLevel() <= 0.0F && this.updateCounter % (lvt_8_1_ * 3 + 1) == 0)
                    {
                        lvt_24_2_ = lvt_13_1_ + (this.rand.nextInt(3) - 1);
                    }

                    if (lvt_6_1_)
                    {
                        lvt_26_2_ = 1;
                    }

                    int lvt_27_2_ = lvt_12_1_ - lvt_23_3_ * 8 - 9;
                    this.drawTexturedModalRect(lvt_27_2_, lvt_24_2_, 16 + lvt_26_2_ * 9, 27, 9, 9);

                    if (lvt_6_1_)
                    {
                        if (lvt_23_3_ * 2 + 1 < lvt_9_1_)
                        {
                            this.drawTexturedModalRect(lvt_27_2_, lvt_24_2_, lvt_25_2_ + 54, 27, 9, 9);
                        }

                        if (lvt_23_3_ * 2 + 1 == lvt_9_1_)
                        {
                            this.drawTexturedModalRect(lvt_27_2_, lvt_24_2_, lvt_25_2_ + 63, 27, 9, 9);
                        }
                    }

                    if (lvt_23_3_ * 2 + 1 < lvt_8_1_)
                    {
                        this.drawTexturedModalRect(lvt_27_2_, lvt_24_2_, lvt_25_2_ + 36, 27, 9, 9);
                    }

                    if (lvt_23_3_ * 2 + 1 == lvt_8_1_)
                    {
                        this.drawTexturedModalRect(lvt_27_2_, lvt_24_2_, lvt_25_2_ + 45, 27, 9, 9);
                    }
                }
            }
            else if (lvt_22_3_ instanceof EntityLivingBase)
            {
                this.mc.mcProfiler.endStartSection("mountHealth");
                EntityLivingBase lvt_23_4_ = (EntityLivingBase)lvt_22_3_;
                int lvt_24_3_ = (int)Math.ceil((double)lvt_23_4_.getHealth());
                float lvt_25_3_ = lvt_23_4_.getMaxHealth();
                int lvt_26_3_ = (int)(lvt_25_3_ + 0.5F) / 2;

                if (lvt_26_3_ > 30)
                {
                    lvt_26_3_ = 30;
                }

                int lvt_27_3_ = lvt_13_1_;

                for (int lvt_28_2_ = 0; lvt_26_3_ > 0; lvt_28_2_ += 20)
                {
                    int lvt_29_1_ = Math.min(lvt_26_3_, 10);
                    lvt_26_3_ -= lvt_29_1_;

                    for (int lvt_30_1_ = 0; lvt_30_1_ < lvt_29_1_; ++lvt_30_1_)
                    {
                        int lvt_31_1_ = 52;
                        int lvt_32_1_ = 0;

                        if (lvt_6_1_)
                        {
                            lvt_32_1_ = 1;
                        }

                        int lvt_33_1_ = lvt_12_1_ - lvt_30_1_ * 8 - 9;
                        this.drawTexturedModalRect(lvt_33_1_, lvt_27_3_, lvt_31_1_ + lvt_32_1_ * 9, 9, 9, 9);

                        if (lvt_30_1_ * 2 + 1 + lvt_28_2_ < lvt_24_3_)
                        {
                            this.drawTexturedModalRect(lvt_33_1_, lvt_27_3_, lvt_31_1_ + 36, 9, 9, 9);
                        }

                        if (lvt_30_1_ * 2 + 1 + lvt_28_2_ == lvt_24_3_)
                        {
                            this.drawTexturedModalRect(lvt_33_1_, lvt_27_3_, lvt_31_1_ + 45, 9, 9, 9);
                        }
                    }

                    lvt_27_3_ -= 10;
                }
            }

            this.mc.mcProfiler.endStartSection("air");

            if (lvt_2_1_.isInsideOfMaterial(Material.water))
            {
                int lvt_23_5_ = this.mc.thePlayer.getAir();
                int lvt_24_4_ = MathHelper.ceiling_double_int((double)(lvt_23_5_ - 2) * 10.0D / 300.0D);
                int lvt_25_4_ = MathHelper.ceiling_double_int((double)lvt_23_5_ * 10.0D / 300.0D) - lvt_24_4_;

                for (int lvt_26_4_ = 0; lvt_26_4_ < lvt_24_4_ + lvt_25_4_; ++lvt_26_4_)
                {
                    if (lvt_26_4_ < lvt_24_4_)
                    {
                        this.drawTexturedModalRect(lvt_12_1_ - lvt_26_4_ * 8 - 9, lvt_18_1_, 16, 18, 9, 9);
                    }
                    else
                    {
                        this.drawTexturedModalRect(lvt_12_1_ - lvt_26_4_ * 8 - 9, lvt_18_1_, 25, 18, 9, 9);
                    }
                }
            }

            this.mc.mcProfiler.endSection();
        }
    }

    /**
     * Renders dragon's (boss) health on the HUD
     */
    private void renderBossHealth()
    {
        if (BossStatus.bossName != null && BossStatus.statusBarTime > 0)
        {
            --BossStatus.statusBarTime;
            FontRenderer lvt_1_1_ = this.mc.fontRendererObj;
            ScaledResolution lvt_2_1_ = new ScaledResolution(this.mc);
            int lvt_3_1_ = lvt_2_1_.getScaledWidth();
            int lvt_4_1_ = 182;
            int lvt_5_1_ = lvt_3_1_ / 2 - lvt_4_1_ / 2;
            int lvt_6_1_ = (int)(BossStatus.healthScale * (float)(lvt_4_1_ + 1));
            int lvt_7_1_ = 12;
            this.drawTexturedModalRect(lvt_5_1_, lvt_7_1_, 0, 74, lvt_4_1_, 5);
            this.drawTexturedModalRect(lvt_5_1_, lvt_7_1_, 0, 74, lvt_4_1_, 5);

            if (lvt_6_1_ > 0)
            {
                this.drawTexturedModalRect(lvt_5_1_, lvt_7_1_, 0, 79, lvt_6_1_, 5);
            }

            String lvt_8_1_ = BossStatus.bossName;
            this.getFontRenderer().drawStringWithShadow(lvt_8_1_, (float)(lvt_3_1_ / 2 - this.getFontRenderer().getStringWidth(lvt_8_1_) / 2), (float)(lvt_7_1_ - 10), 16777215);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(icons);
        }
    }

    private void renderPumpkinOverlay(ScaledResolution scaledRes)
    {
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableAlpha();
        this.mc.getTextureManager().bindTexture(pumpkinBlurTexPath);
        Tessellator lvt_2_1_ = Tessellator.getInstance();
        WorldRenderer lvt_3_1_ = lvt_2_1_.getWorldRenderer();
        lvt_3_1_.begin(7, DefaultVertexFormats.POSITION_TEX);
        lvt_3_1_.pos(0.0D, (double)scaledRes.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
        lvt_3_1_.pos((double)scaledRes.getScaledWidth(), (double)scaledRes.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
        lvt_3_1_.pos((double)scaledRes.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
        lvt_3_1_.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
        lvt_2_1_.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Renders a Vignette arount the entire screen that changes with light level.
     *  
     * @param lightLevel The current brightness
     * @param scaledRes The current resolution of the game
     */
    private void renderVignette(float lightLevel, ScaledResolution scaledRes)
    {
        lightLevel = 1.0F - lightLevel;
        lightLevel = MathHelper.clamp_float(lightLevel, 0.0F, 1.0F);
        WorldBorder lvt_3_1_ = this.mc.theWorld.getWorldBorder();
        float lvt_4_1_ = (float)lvt_3_1_.getClosestDistance(this.mc.thePlayer);
        double lvt_5_1_ = Math.min(lvt_3_1_.getResizeSpeed() * (double)lvt_3_1_.getWarningTime() * 1000.0D, Math.abs(lvt_3_1_.getTargetSize() - lvt_3_1_.getDiameter()));
        double lvt_7_1_ = Math.max((double)lvt_3_1_.getWarningDistance(), lvt_5_1_);

        if ((double)lvt_4_1_ < lvt_7_1_)
        {
            lvt_4_1_ = 1.0F - (float)((double)lvt_4_1_ / lvt_7_1_);
        }
        else
        {
            lvt_4_1_ = 0.0F;
        }

        this.prevVignetteBrightness = (float)((double)this.prevVignetteBrightness + (double)(lightLevel - this.prevVignetteBrightness) * 0.01D);
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(0, 769, 1, 0);

        if (lvt_4_1_ > 0.0F)
        {
            GlStateManager.color(0.0F, lvt_4_1_, lvt_4_1_, 1.0F);
        }
        else
        {
            GlStateManager.color(this.prevVignetteBrightness, this.prevVignetteBrightness, this.prevVignetteBrightness, 1.0F);
        }

        this.mc.getTextureManager().bindTexture(vignetteTexPath);
        Tessellator lvt_9_1_ = Tessellator.getInstance();
        WorldRenderer lvt_10_1_ = lvt_9_1_.getWorldRenderer();
        lvt_10_1_.begin(7, DefaultVertexFormats.POSITION_TEX);
        lvt_10_1_.pos(0.0D, (double)scaledRes.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
        lvt_10_1_.pos((double)scaledRes.getScaledWidth(), (double)scaledRes.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
        lvt_10_1_.pos((double)scaledRes.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
        lvt_10_1_.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
        lvt_9_1_.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    }

    private void renderPortal(float timeInPortal, ScaledResolution scaledRes)
    {
        if (timeInPortal < 1.0F)
        {
            timeInPortal = timeInPortal * timeInPortal;
            timeInPortal = timeInPortal * timeInPortal;
            timeInPortal = timeInPortal * 0.8F + 0.2F;
        }

        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(1.0F, 1.0F, 1.0F, timeInPortal);
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        TextureAtlasSprite lvt_3_1_ = this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.portal.getDefaultState());
        float lvt_4_1_ = lvt_3_1_.getMinU();
        float lvt_5_1_ = lvt_3_1_.getMinV();
        float lvt_6_1_ = lvt_3_1_.getMaxU();
        float lvt_7_1_ = lvt_3_1_.getMaxV();
        Tessellator lvt_8_1_ = Tessellator.getInstance();
        WorldRenderer lvt_9_1_ = lvt_8_1_.getWorldRenderer();
        lvt_9_1_.begin(7, DefaultVertexFormats.POSITION_TEX);
        lvt_9_1_.pos(0.0D, (double)scaledRes.getScaledHeight(), -90.0D).tex((double)lvt_4_1_, (double)lvt_7_1_).endVertex();
        lvt_9_1_.pos((double)scaledRes.getScaledWidth(), (double)scaledRes.getScaledHeight(), -90.0D).tex((double)lvt_6_1_, (double)lvt_7_1_).endVertex();
        lvt_9_1_.pos((double)scaledRes.getScaledWidth(), 0.0D, -90.0D).tex((double)lvt_6_1_, (double)lvt_5_1_).endVertex();
        lvt_9_1_.pos(0.0D, 0.0D, -90.0D).tex((double)lvt_4_1_, (double)lvt_5_1_).endVertex();
        lvt_8_1_.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderHotbarItem(int index, int xPos, int yPos, float partialTicks, EntityPlayer player)
    {
        ItemStack lvt_6_1_ = player.inventory.mainInventory[index];

        if (lvt_6_1_ != null)
        {
            float lvt_7_1_ = (float)lvt_6_1_.animationsToGo - partialTicks;

            if (lvt_7_1_ > 0.0F)
            {
                GlStateManager.pushMatrix();
                float lvt_8_1_ = 1.0F + lvt_7_1_ / 5.0F;
                GlStateManager.translate((float)(xPos + 8), (float)(yPos + 12), 0.0F);
                GlStateManager.scale(1.0F / lvt_8_1_, (lvt_8_1_ + 1.0F) / 2.0F, 1.0F);
                GlStateManager.translate((float)(-(xPos + 8)), (float)(-(yPos + 12)), 0.0F);
            }

            this.itemRenderer.renderItemAndEffectIntoGUI(lvt_6_1_, xPos, yPos);

            if (lvt_7_1_ > 0.0F)
            {
                GlStateManager.popMatrix();
            }

            this.itemRenderer.renderItemOverlays(this.mc.fontRendererObj, lvt_6_1_, xPos, yPos);
        }
    }

    /**
     * The update tick for the ingame UI
     */
    public void updateTick()
    {
        if (this.recordPlayingUpFor > 0)
        {
            --this.recordPlayingUpFor;
        }

        if (this.titlesTimer > 0)
        {
            --this.titlesTimer;

            if (this.titlesTimer <= 0)
            {
                this.displayedTitle = "";
                this.displayedSubTitle = "";
            }
        }

        ++this.updateCounter;
        this.streamIndicator.updateStreamAlpha();

        if (this.mc.thePlayer != null)
        {
            ItemStack lvt_1_1_ = this.mc.thePlayer.inventory.getCurrentItem();

            if (lvt_1_1_ == null)
            {
                this.remainingHighlightTicks = 0;
            }
            else if (this.highlightingItemStack != null && lvt_1_1_.getItem() == this.highlightingItemStack.getItem() && ItemStack.areItemStackTagsEqual(lvt_1_1_, this.highlightingItemStack) && (lvt_1_1_.isItemStackDamageable() || lvt_1_1_.getMetadata() == this.highlightingItemStack.getMetadata()))
            {
                if (this.remainingHighlightTicks > 0)
                {
                    --this.remainingHighlightTicks;
                }
            }
            else
            {
                this.remainingHighlightTicks = 40;
            }

            this.highlightingItemStack = lvt_1_1_;
        }
    }

    public void setRecordPlayingMessage(String recordName)
    {
        this.setRecordPlaying(I18n.format("record.nowPlaying", new Object[] {recordName}), true);
    }

    public void setRecordPlaying(String message, boolean isPlaying)
    {
        this.recordPlaying = message;
        this.recordPlayingUpFor = 60;
        this.recordIsPlaying = isPlaying;
    }

    public void displayTitle(String title, String subTitle, int timeFadeIn, int displayTime, int timeFadeOut)
    {
        if (title == null && subTitle == null && timeFadeIn < 0 && displayTime < 0 && timeFadeOut < 0)
        {
            this.displayedTitle = "";
            this.displayedSubTitle = "";
            this.titlesTimer = 0;
        }
        else if (title != null)
        {
            this.displayedTitle = title;
            this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
        }
        else if (subTitle != null)
        {
            this.displayedSubTitle = subTitle;
        }
        else
        {
            if (timeFadeIn >= 0)
            {
                this.titleFadeIn = timeFadeIn;
            }

            if (displayTime >= 0)
            {
                this.titleDisplayTime = displayTime;
            }

            if (timeFadeOut >= 0)
            {
                this.titleFadeOut = timeFadeOut;
            }

            if (this.titlesTimer > 0)
            {
                this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
            }
        }
    }

    public void setRecordPlaying(IChatComponent component, boolean isPlaying)
    {
        this.setRecordPlaying(component.getUnformattedText(), isPlaying);
    }

    /**
     * returns a pointer to the persistant Chat GUI, containing all previous chat messages and such
     */
    public GuiNewChat getChatGUI()
    {
        return this.persistantChatGUI;
    }

    public int getUpdateCounter()
    {
        return this.updateCounter;
    }

    public FontRenderer getFontRenderer()
    {
        return this.mc.fontRendererObj;
    }

    public GuiSpectator getSpectatorGui()
    {
        return this.spectatorGui;
    }

    public GuiPlayerTabOverlay getTabList()
    {
        return this.overlayPlayerList;
    }

    /**
     * Reset the GuiPlayerTabOverlay's message header and footer
     */
    public void resetPlayersOverlayFooterHeader()
    {
        this.overlayPlayerList.resetFooterHeader();
    }
}
