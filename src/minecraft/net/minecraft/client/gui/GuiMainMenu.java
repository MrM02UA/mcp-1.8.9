package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback
{
    private static final AtomicInteger field_175373_f = new AtomicInteger(0);
    private static final Logger logger = LogManager.getLogger();
    private static final Random RANDOM = new Random();

    /** Counts the number of screen updates. */
    private float updateCounter;

    /** The splash message. */
    private String splashText;
    private GuiButton buttonResetDemo;

    /** Timer used to rotate the panorama, increases every tick. */
    private int panoramaTimer;

    /**
     * Texture allocated for the current viewport of the main menu's panorama background.
     */
    private DynamicTexture viewportTexture;
    private boolean field_175375_v = true;

    /**
     * The Object object utilized as a thread lock when performing non thread-safe operations
     */
    private final Object threadLock = new Object();

    /** OpenGL graphics card warning. */
    private String openGLWarning1;

    /** OpenGL graphics card warning. */
    private String openGLWarning2;

    /** Link to the Mojang Support about minimum requirements */
    private String openGLWarningLink;
    private static final ResourceLocation splashTexts = new ResourceLocation("texts/splashes.txt");
    private static final ResourceLocation minecraftTitleTextures = new ResourceLocation("textures/gui/title/minecraft.png");

    /** An array of all the paths to the panorama pictures. */
    private static final ResourceLocation[] titlePanoramaPaths = new ResourceLocation[] {new ResourceLocation("textures/gui/title/background/panorama_0.png"), new ResourceLocation("textures/gui/title/background/panorama_1.png"), new ResourceLocation("textures/gui/title/background/panorama_2.png"), new ResourceLocation("textures/gui/title/background/panorama_3.png"), new ResourceLocation("textures/gui/title/background/panorama_4.png"), new ResourceLocation("textures/gui/title/background/panorama_5.png")};
    public static final String field_96138_a = "Please click " + EnumChatFormatting.UNDERLINE + "here" + EnumChatFormatting.RESET + " for more information.";
    private int field_92024_r;
    private int field_92023_s;
    private int field_92022_t;
    private int field_92021_u;
    private int field_92020_v;
    private int field_92019_w;
    private ResourceLocation backgroundTexture;

    /** Minecraft Realms button. */
    private GuiButton realmsButton;
    private boolean field_183502_L;
    private GuiScreen field_183503_M;

    public GuiMainMenu()
    {
        this.openGLWarning2 = field_96138_a;
        this.field_183502_L = false;
        this.splashText = "missingno";
        BufferedReader lvt_1_1_ = null;

        try
        {
            List<String> lvt_2_1_ = Lists.newArrayList();
            lvt_1_1_ = new BufferedReader(new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(splashTexts).getInputStream(), Charsets.UTF_8));
            String lvt_3_1_;

            while ((lvt_3_1_ = lvt_1_1_.readLine()) != null)
            {
                lvt_3_1_ = lvt_3_1_.trim();

                if (!lvt_3_1_.isEmpty())
                {
                    lvt_2_1_.add(lvt_3_1_);
                }
            }

            if (!lvt_2_1_.isEmpty())
            {
                while (true)
                {
                    this.splashText = (String)lvt_2_1_.get(RANDOM.nextInt(lvt_2_1_.size()));

                    if (this.splashText.hashCode() != 125780783)
                    {
                        break;
                    }
                }
            }
        }
        catch (IOException var12)
        {
            ;
        }
        finally
        {
            if (lvt_1_1_ != null)
            {
                try
                {
                    lvt_1_1_.close();
                }
                catch (IOException var11)
                {
                    ;
                }
            }
        }

        this.updateCounter = RANDOM.nextFloat();
        this.openGLWarning1 = "";

        if (!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported())
        {
            this.openGLWarning1 = I18n.format("title.oldgl1", new Object[0]);
            this.openGLWarning2 = I18n.format("title.oldgl2", new Object[0]);
            this.openGLWarningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
        }
    }

    private boolean func_183501_a()
    {
        return Minecraft.getMinecraft().gameSettings.getOptionOrdinalValue(GameSettings.Options.REALMS_NOTIFICATIONS) && this.field_183503_M != null;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        ++this.panoramaTimer;

        if (this.func_183501_a())
        {
            this.field_183503_M.updateScreen();
        }
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.viewportTexture = new DynamicTexture(256, 256);
        this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture);
        Calendar lvt_1_1_ = Calendar.getInstance();
        lvt_1_1_.setTime(new Date());

        if (lvt_1_1_.get(2) + 1 == 12 && lvt_1_1_.get(5) == 24)
        {
            this.splashText = "Merry X-mas!";
        }
        else if (lvt_1_1_.get(2) + 1 == 1 && lvt_1_1_.get(5) == 1)
        {
            this.splashText = "Happy new year!";
        }
        else if (lvt_1_1_.get(2) + 1 == 10 && lvt_1_1_.get(5) == 31)
        {
            this.splashText = "OOoooOOOoooo! Spooky!";
        }

        int lvt_2_1_ = 24;
        int lvt_3_1_ = this.height / 4 + 48;

        if (this.mc.isDemo())
        {
            this.addDemoButtons(lvt_3_1_, 24);
        }
        else
        {
            this.addSingleplayerMultiplayerButtons(lvt_3_1_, 24);
        }

        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, lvt_3_1_ + 72 + 12, 98, 20, I18n.format("menu.options", new Object[0])));
        this.buttonList.add(new GuiButton(4, this.width / 2 + 2, lvt_3_1_ + 72 + 12, 98, 20, I18n.format("menu.quit", new Object[0])));
        this.buttonList.add(new GuiButtonLanguage(5, this.width / 2 - 124, lvt_3_1_ + 72 + 12));

        synchronized (this.threadLock)
        {
            this.field_92023_s = this.fontRendererObj.getStringWidth(this.openGLWarning1);
            this.field_92024_r = this.fontRendererObj.getStringWidth(this.openGLWarning2);
            int lvt_5_1_ = Math.max(this.field_92023_s, this.field_92024_r);
            this.field_92022_t = (this.width - lvt_5_1_) / 2;
            this.field_92021_u = ((GuiButton)this.buttonList.get(0)).yPosition - 24;
            this.field_92020_v = this.field_92022_t + lvt_5_1_;
            this.field_92019_w = this.field_92021_u + 24;
        }

        this.mc.setConnectedToRealms(false);

        if (Minecraft.getMinecraft().gameSettings.getOptionOrdinalValue(GameSettings.Options.REALMS_NOTIFICATIONS) && !this.field_183502_L)
        {
            RealmsBridge lvt_4_1_ = new RealmsBridge();
            this.field_183503_M = lvt_4_1_.getNotificationScreen(this);
            this.field_183502_L = true;
        }

        if (this.func_183501_a())
        {
            this.field_183503_M.setGuiSize(this.width, this.height);
            this.field_183503_M.initGui();
        }
    }

    /**
     * Adds Singleplayer and Multiplayer buttons on Main Menu for players who have bought the game.
     */
    private void addSingleplayerMultiplayerButtons(int p_73969_1_, int p_73969_2_)
    {
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, p_73969_1_, I18n.format("menu.singleplayer", new Object[0])));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 1, I18n.format("menu.multiplayer", new Object[0])));
        this.buttonList.add(this.realmsButton = new GuiButton(14, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 2, I18n.format("menu.online", new Object[0])));
    }

    /**
     * Adds Demo buttons on Main Menu for players who are playing Demo.
     */
    private void addDemoButtons(int p_73972_1_, int p_73972_2_)
    {
        this.buttonList.add(new GuiButton(11, this.width / 2 - 100, p_73972_1_, I18n.format("menu.playdemo", new Object[0])));
        this.buttonList.add(this.buttonResetDemo = new GuiButton(12, this.width / 2 - 100, p_73972_1_ + p_73972_2_ * 1, I18n.format("menu.resetdemo", new Object[0])));
        ISaveFormat lvt_3_1_ = this.mc.getSaveLoader();
        WorldInfo lvt_4_1_ = lvt_3_1_.getWorldInfo("Demo_World");

        if (lvt_4_1_ == null)
        {
            this.buttonResetDemo.enabled = false;
        }
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        }

        if (button.id == 5)
        {
            this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
        }

        if (button.id == 1)
        {
            this.mc.displayGuiScreen(new GuiSelectWorld(this));
        }

        if (button.id == 2)
        {
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        }

        if (button.id == 14 && this.realmsButton.visible)
        {
            this.switchToRealms();
        }

        if (button.id == 4)
        {
            this.mc.shutdown();
        }

        if (button.id == 11)
        {
            this.mc.launchIntegratedServer("Demo_World", "Demo_World", DemoWorldServer.demoWorldSettings);
        }

        if (button.id == 12)
        {
            ISaveFormat lvt_2_1_ = this.mc.getSaveLoader();
            WorldInfo lvt_3_1_ = lvt_2_1_.getWorldInfo("Demo_World");

            if (lvt_3_1_ != null)
            {
                GuiYesNo lvt_4_1_ = GuiSelectWorld.makeDeleteWorldYesNo(this, lvt_3_1_.getWorldName(), 12);
                this.mc.displayGuiScreen(lvt_4_1_);
            }
        }
    }

    private void switchToRealms()
    {
        RealmsBridge lvt_1_1_ = new RealmsBridge();
        lvt_1_1_.switchToRealms(this);
    }

    public void confirmClicked(boolean result, int id)
    {
        if (result && id == 12)
        {
            ISaveFormat lvt_3_1_ = this.mc.getSaveLoader();
            lvt_3_1_.flushCache();
            lvt_3_1_.deleteWorldDirectory("Demo_World");
            this.mc.displayGuiScreen(this);
        }
        else if (id == 13)
        {
            if (result)
            {
                try
                {
                    Class<?> lvt_3_2_ = Class.forName("java.awt.Desktop");
                    Object lvt_4_1_ = lvt_3_2_.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
                    lvt_3_2_.getMethod("browse", new Class[] {URI.class}).invoke(lvt_4_1_, new Object[] {new URI(this.openGLWarningLink)});
                }
                catch (Throwable var5)
                {
                    logger.error("Couldn\'t open link", var5);
                }
            }

            this.mc.displayGuiScreen(this);
        }
    }

    /**
     * Draws the main menu panorama
     */
    private void drawPanorama(int p_73970_1_, int p_73970_2_, float p_73970_3_)
    {
        Tessellator lvt_4_1_ = Tessellator.getInstance();
        WorldRenderer lvt_5_1_ = lvt_4_1_.getWorldRenderer();
        GlStateManager.matrixMode(5889);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        int lvt_6_1_ = 8;

        for (int lvt_7_1_ = 0; lvt_7_1_ < lvt_6_1_ * lvt_6_1_; ++lvt_7_1_)
        {
            GlStateManager.pushMatrix();
            float lvt_8_1_ = ((float)(lvt_7_1_ % lvt_6_1_) / (float)lvt_6_1_ - 0.5F) / 64.0F;
            float lvt_9_1_ = ((float)(lvt_7_1_ / lvt_6_1_) / (float)lvt_6_1_ - 0.5F) / 64.0F;
            float lvt_10_1_ = 0.0F;
            GlStateManager.translate(lvt_8_1_, lvt_9_1_, lvt_10_1_);
            GlStateManager.rotate(MathHelper.sin(((float)this.panoramaTimer + p_73970_3_) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-((float)this.panoramaTimer + p_73970_3_) * 0.1F, 0.0F, 1.0F, 0.0F);

            for (int lvt_11_1_ = 0; lvt_11_1_ < 6; ++lvt_11_1_)
            {
                GlStateManager.pushMatrix();

                if (lvt_11_1_ == 1)
                {
                    GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (lvt_11_1_ == 2)
                {
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                }

                if (lvt_11_1_ == 3)
                {
                    GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (lvt_11_1_ == 4)
                {
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (lvt_11_1_ == 5)
                {
                    GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                }

                this.mc.getTextureManager().bindTexture(titlePanoramaPaths[lvt_11_1_]);
                lvt_5_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                int lvt_12_1_ = 255 / (lvt_7_1_ + 1);
                float lvt_13_1_ = 0.0F;
                lvt_5_1_.pos(-1.0D, -1.0D, 1.0D).tex(0.0D, 0.0D).color(255, 255, 255, lvt_12_1_).endVertex();
                lvt_5_1_.pos(1.0D, -1.0D, 1.0D).tex(1.0D, 0.0D).color(255, 255, 255, lvt_12_1_).endVertex();
                lvt_5_1_.pos(1.0D, 1.0D, 1.0D).tex(1.0D, 1.0D).color(255, 255, 255, lvt_12_1_).endVertex();
                lvt_5_1_.pos(-1.0D, 1.0D, 1.0D).tex(0.0D, 1.0D).color(255, 255, 255, lvt_12_1_).endVertex();
                lvt_4_1_.draw();
                GlStateManager.popMatrix();
            }

            GlStateManager.popMatrix();
            GlStateManager.colorMask(true, true, true, false);
        }

        lvt_5_1_.setTranslation(0.0D, 0.0D, 0.0D);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.matrixMode(5889);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
    }

    /**
     * Rotate and blurs the skybox view in the main menu
     */
    private void rotateAndBlurSkybox(float p_73968_1_)
    {
        this.mc.getTextureManager().bindTexture(this.backgroundTexture);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.colorMask(true, true, true, false);
        Tessellator lvt_2_1_ = Tessellator.getInstance();
        WorldRenderer lvt_3_1_ = lvt_2_1_.getWorldRenderer();
        lvt_3_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        GlStateManager.disableAlpha();
        int lvt_4_1_ = 3;

        for (int lvt_5_1_ = 0; lvt_5_1_ < lvt_4_1_; ++lvt_5_1_)
        {
            float lvt_6_1_ = 1.0F / (float)(lvt_5_1_ + 1);
            int lvt_7_1_ = this.width;
            int lvt_8_1_ = this.height;
            float lvt_9_1_ = (float)(lvt_5_1_ - lvt_4_1_ / 2) / 256.0F;
            lvt_3_1_.pos((double)lvt_7_1_, (double)lvt_8_1_, (double)this.zLevel).tex((double)(0.0F + lvt_9_1_), 1.0D).color(1.0F, 1.0F, 1.0F, lvt_6_1_).endVertex();
            lvt_3_1_.pos((double)lvt_7_1_, 0.0D, (double)this.zLevel).tex((double)(1.0F + lvt_9_1_), 1.0D).color(1.0F, 1.0F, 1.0F, lvt_6_1_).endVertex();
            lvt_3_1_.pos(0.0D, 0.0D, (double)this.zLevel).tex((double)(1.0F + lvt_9_1_), 0.0D).color(1.0F, 1.0F, 1.0F, lvt_6_1_).endVertex();
            lvt_3_1_.pos(0.0D, (double)lvt_8_1_, (double)this.zLevel).tex((double)(0.0F + lvt_9_1_), 0.0D).color(1.0F, 1.0F, 1.0F, lvt_6_1_).endVertex();
        }

        lvt_2_1_.draw();
        GlStateManager.enableAlpha();
        GlStateManager.colorMask(true, true, true, true);
    }

    /**
     * Renders the skybox in the main menu
     */
    private void renderSkybox(int p_73971_1_, int p_73971_2_, float p_73971_3_)
    {
        this.mc.getFramebuffer().unbindFramebuffer();
        GlStateManager.viewport(0, 0, 256, 256);
        this.drawPanorama(p_73971_1_, p_73971_2_, p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        float lvt_4_1_ = this.width > this.height ? 120.0F / (float)this.width : 120.0F / (float)this.height;
        float lvt_5_1_ = (float)this.height * lvt_4_1_ / 256.0F;
        float lvt_6_1_ = (float)this.width * lvt_4_1_ / 256.0F;
        int lvt_7_1_ = this.width;
        int lvt_8_1_ = this.height;
        Tessellator lvt_9_1_ = Tessellator.getInstance();
        WorldRenderer lvt_10_1_ = lvt_9_1_.getWorldRenderer();
        lvt_10_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        lvt_10_1_.pos(0.0D, (double)lvt_8_1_, (double)this.zLevel).tex((double)(0.5F - lvt_5_1_), (double)(0.5F + lvt_6_1_)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        lvt_10_1_.pos((double)lvt_7_1_, (double)lvt_8_1_, (double)this.zLevel).tex((double)(0.5F - lvt_5_1_), (double)(0.5F - lvt_6_1_)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        lvt_10_1_.pos((double)lvt_7_1_, 0.0D, (double)this.zLevel).tex((double)(0.5F + lvt_5_1_), (double)(0.5F - lvt_6_1_)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        lvt_10_1_.pos(0.0D, 0.0D, (double)this.zLevel).tex((double)(0.5F + lvt_5_1_), (double)(0.5F + lvt_6_1_)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        lvt_9_1_.draw();
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        GlStateManager.disableAlpha();
        this.renderSkybox(mouseX, mouseY, partialTicks);
        GlStateManager.enableAlpha();
        Tessellator lvt_4_1_ = Tessellator.getInstance();
        WorldRenderer lvt_5_1_ = lvt_4_1_.getWorldRenderer();
        int lvt_6_1_ = 274;
        int lvt_7_1_ = this.width / 2 - lvt_6_1_ / 2;
        int lvt_8_1_ = 30;
        this.drawGradientRect(0, 0, this.width, this.height, -2130706433, 16777215);
        this.drawGradientRect(0, 0, this.width, this.height, 0, Integer.MIN_VALUE);
        this.mc.getTextureManager().bindTexture(minecraftTitleTextures);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if ((double)this.updateCounter < 1.0E-4D)
        {
            this.drawTexturedModalRect(lvt_7_1_ + 0, lvt_8_1_ + 0, 0, 0, 99, 44);
            this.drawTexturedModalRect(lvt_7_1_ + 99, lvt_8_1_ + 0, 129, 0, 27, 44);
            this.drawTexturedModalRect(lvt_7_1_ + 99 + 26, lvt_8_1_ + 0, 126, 0, 3, 44);
            this.drawTexturedModalRect(lvt_7_1_ + 99 + 26 + 3, lvt_8_1_ + 0, 99, 0, 26, 44);
            this.drawTexturedModalRect(lvt_7_1_ + 155, lvt_8_1_ + 0, 0, 45, 155, 44);
        }
        else
        {
            this.drawTexturedModalRect(lvt_7_1_ + 0, lvt_8_1_ + 0, 0, 0, 155, 44);
            this.drawTexturedModalRect(lvt_7_1_ + 155, lvt_8_1_ + 0, 0, 45, 155, 44);
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate((float)(this.width / 2 + 90), 70.0F, 0.0F);
        GlStateManager.rotate(-20.0F, 0.0F, 0.0F, 1.0F);
        float lvt_9_1_ = 1.8F - MathHelper.abs(MathHelper.sin((float)(Minecraft.getSystemTime() % 1000L) / 1000.0F * (float)Math.PI * 2.0F) * 0.1F);
        lvt_9_1_ = lvt_9_1_ * 100.0F / (float)(this.fontRendererObj.getStringWidth(this.splashText) + 32);
        GlStateManager.scale(lvt_9_1_, lvt_9_1_, lvt_9_1_);
        this.drawCenteredString(this.fontRendererObj, this.splashText, 0, -8, -256);
        GlStateManager.popMatrix();
        String lvt_10_1_ = "Minecraft 1.8.9";

        if (this.mc.isDemo())
        {
            lvt_10_1_ = lvt_10_1_ + " Demo";
        }

        this.drawString(this.fontRendererObj, lvt_10_1_, 2, this.height - 10, -1);
        String lvt_11_1_ = "Copyright Mojang AB. Do not distribute!";
        this.drawString(this.fontRendererObj, lvt_11_1_, this.width - this.fontRendererObj.getStringWidth(lvt_11_1_) - 2, this.height - 10, -1);

        if (this.openGLWarning1 != null && this.openGLWarning1.length() > 0)
        {
            drawRect(this.field_92022_t - 2, this.field_92021_u - 2, this.field_92020_v + 2, this.field_92019_w - 1, 1428160512);
            this.drawString(this.fontRendererObj, this.openGLWarning1, this.field_92022_t, this.field_92021_u, -1);
            this.drawString(this.fontRendererObj, this.openGLWarning2, (this.width - this.field_92024_r) / 2, ((GuiButton)this.buttonList.get(0)).yPosition - 12, -1);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        if (this.func_183501_a())
        {
            this.field_183503_M.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        synchronized (this.threadLock)
        {
            if (this.openGLWarning1.length() > 0 && mouseX >= this.field_92022_t && mouseX <= this.field_92020_v && mouseY >= this.field_92021_u && mouseY <= this.field_92019_w)
            {
                GuiConfirmOpenLink lvt_5_1_ = new GuiConfirmOpenLink(this, this.openGLWarningLink, 13, true);
                lvt_5_1_.disableSecurityWarning();
                this.mc.displayGuiScreen(lvt_5_1_);
            }
        }

        if (this.func_183501_a())
        {
            this.field_183503_M.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        if (this.field_183503_M != null)
        {
            this.field_183503_M.onGuiClosed();
        }
    }
}
