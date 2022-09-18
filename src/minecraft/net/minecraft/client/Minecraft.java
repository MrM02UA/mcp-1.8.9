package net.minecraft.client;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMemoryErrorScreen;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.stream.GuiStreamUnavailable;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.ResourceIndex;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.AnimationMetadataSectionSerializer;
import net.minecraft.client.resources.data.FontMetadataSection;
import net.minecraft.client.resources.data.FontMetadataSectionSerializer;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.client.resources.data.LanguageMetadataSectionSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.resources.data.PackMetadataSectionSerializer;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSectionSerializer;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.stream.IStream;
import net.minecraft.client.stream.NullStream;
import net.minecraft.client.stream.TwitchStream;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.profiler.IPlayerUsage;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.IStatStringFormat;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MinecraftError;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import net.minecraft.util.Util;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

public class Minecraft implements IThreadListener, IPlayerUsage
{
    private static final Logger logger = LogManager.getLogger();
    private static final ResourceLocation locationMojangPng = new ResourceLocation("textures/gui/title/mojang.png");
    public static final boolean isRunningOnMac = Util.getOSType() == Util.EnumOS.OSX;

    /** A 10MiB preallocation to ensure the heap is reasonably sized. */
    public static byte[] memoryReserve = new byte[10485760];
    private static final List<DisplayMode> macDisplayModes = Lists.newArrayList(new DisplayMode[] {new DisplayMode(2560, 1600), new DisplayMode(2880, 1800)});
    private final File fileResourcepacks;
    private final PropertyMap twitchDetails;

    /** The player's GameProfile properties */
    private final PropertyMap profileProperties;
    private ServerData currentServerData;

    /** The RenderEngine instance used by Minecraft */
    private TextureManager renderEngine;

    /**
     * Set to 'this' in Minecraft constructor; used by some settings get methods
     */
    private static Minecraft theMinecraft;
    public PlayerControllerMP playerController;
    private boolean fullscreen;
    private boolean enableGLErrorChecking = true;
    private boolean hasCrashed;

    /** Instance of CrashReport. */
    private CrashReport crashReporter;
    public int displayWidth;
    public int displayHeight;

    /** True if the player is connected to a realms server */
    private boolean connectedToRealms = false;
    private Timer timer = new Timer(20.0F);

    /** Instance of PlayerUsageSnooper. */
    private PlayerUsageSnooper usageSnooper = new PlayerUsageSnooper("client", this, MinecraftServer.getCurrentTimeMillis());
    public WorldClient theWorld;
    public RenderGlobal renderGlobal;
    private RenderManager renderManager;
    private RenderItem renderItem;
    private ItemRenderer itemRenderer;
    public EntityPlayerSP thePlayer;
    private Entity renderViewEntity;
    public Entity pointedEntity;
    public EffectRenderer effectRenderer;
    private final Session session;
    private boolean isGamePaused;

    /** The font renderer used for displaying and measuring text */
    public FontRenderer fontRendererObj;
    public FontRenderer standardGalacticFontRenderer;

    /** The GuiScreen that's being displayed at the moment. */
    public GuiScreen currentScreen;
    public LoadingScreenRenderer loadingScreen;
    public EntityRenderer entityRenderer;

    /** Mouse left click counter */
    private int leftClickCounter;

    /** Display width */
    private int tempDisplayWidth;

    /** Display height */
    private int tempDisplayHeight;

    /** Instance of IntegratedServer. */
    private IntegratedServer theIntegratedServer;

    /** Gui achievement */
    public GuiAchievement guiAchievement;
    public GuiIngame ingameGUI;

    /** Skip render world */
    public boolean skipRenderWorld;

    /** The ray trace hit that the mouse is over. */
    public MovingObjectPosition objectMouseOver;

    /** The game settings that currently hold effect. */
    public GameSettings gameSettings;

    /** Mouse helper instance. */
    public MouseHelper mouseHelper;
    public final File mcDataDir;
    private final File fileAssets;
    private final String launchedVersion;
    private final Proxy proxy;
    private ISaveFormat saveLoader;

    /**
     * This is set to fpsCounter every debug screen update, and is shown on the debug screen. It's also sent as part of
     * the usage snooping.
     */
    private static int debugFPS;

    /**
     * When you place a block, it's set to 6, decremented once per tick, when it's 0, you can place another block.
     */
    private int rightClickDelayTimer;
    private String serverName;
    private int serverPort;

    /**
     * Does the actual gameplay have focus. If so then mouse and keys will effect the player instead of menus.
     */
    public boolean inGameHasFocus;
    long systemTime = getSystemTime();

    /** Join player counter */
    private int joinPlayerCounter;

    /** The FrameTimer's instance */
    public final FrameTimer frameTimer = new FrameTimer();

    /** Time in nanoseconds of when the class is loaded */
    long startNanoTime = System.nanoTime();
    private final boolean jvm64bit;
    private final boolean isDemo;
    private NetworkManager myNetworkManager;
    private boolean integratedServerIsRunning;

    /** The profiler instance */
    public final Profiler mcProfiler = new Profiler();

    /**
     * Keeps track of how long the debug crash keycombo (F3+C) has been pressed for, in order to crash after 10 seconds.
     */
    private long debugCrashKeyPressTime = -1L;
    private IReloadableResourceManager mcResourceManager;
    private final IMetadataSerializer metadataSerializer_ = new IMetadataSerializer();
    private final List<IResourcePack> defaultResourcePacks = Lists.newArrayList();
    private final DefaultResourcePack mcDefaultResourcePack;
    private ResourcePackRepository mcResourcePackRepository;
    private LanguageManager mcLanguageManager;
    private IStream stream;
    private Framebuffer framebufferMc;
    private TextureMap textureMapBlocks;
    private SoundHandler mcSoundHandler;
    private MusicTicker mcMusicTicker;
    private ResourceLocation mojangLogo;
    private final MinecraftSessionService sessionService;
    private SkinManager skinManager;
    private final Queue < FutureTask<? >> scheduledTasks = Queues.newArrayDeque();
    private long field_175615_aJ = 0L;
    private final Thread mcThread = Thread.currentThread();
    private ModelManager modelManager;

    /**
     * The BlockRenderDispatcher instance that will be used based off gamesettings
     */
    private BlockRendererDispatcher blockRenderDispatcher;

    /**
     * Set to true to keep the game loop running. Set to false by shutdown() to allow the game loop to exit cleanly.
     */
    volatile boolean running = true;

    /** String that shows the debug information */
    public String debug = "";
    public boolean field_175613_B = false;
    public boolean field_175614_C = false;
    public boolean field_175611_D = false;
    public boolean renderChunksMany = true;

    /** Approximate time (in ms) of last update to debug string */
    long debugUpdateTime = getSystemTime();

    /** holds the current fps */
    int fpsCounter;
    long prevFrameTime = -1L;

    /** Profiler currently displayed in the debug screen pie chart */
    private String debugProfilerName = "root";

    public Minecraft(GameConfiguration gameConfig)
    {
        theMinecraft = this;
        this.mcDataDir = gameConfig.folderInfo.mcDataDir;
        this.fileAssets = gameConfig.folderInfo.assetsDir;
        this.fileResourcepacks = gameConfig.folderInfo.resourcePacksDir;
        this.launchedVersion = gameConfig.gameInfo.version;
        this.twitchDetails = gameConfig.userInfo.userProperties;
        this.profileProperties = gameConfig.userInfo.profileProperties;
        this.mcDefaultResourcePack = new DefaultResourcePack((new ResourceIndex(gameConfig.folderInfo.assetsDir, gameConfig.folderInfo.assetIndex)).getResourceMap());
        this.proxy = gameConfig.userInfo.proxy == null ? Proxy.NO_PROXY : gameConfig.userInfo.proxy;
        this.sessionService = (new YggdrasilAuthenticationService(gameConfig.userInfo.proxy, UUID.randomUUID().toString())).createMinecraftSessionService();
        this.session = gameConfig.userInfo.session;
        logger.info("Setting user: " + this.session.getUsername());
        logger.info("(Session ID is " + this.session.getSessionID() + ")");
        this.isDemo = gameConfig.gameInfo.isDemo;
        this.displayWidth = gameConfig.displayInfo.width > 0 ? gameConfig.displayInfo.width : 1;
        this.displayHeight = gameConfig.displayInfo.height > 0 ? gameConfig.displayInfo.height : 1;
        this.tempDisplayWidth = gameConfig.displayInfo.width;
        this.tempDisplayHeight = gameConfig.displayInfo.height;
        this.fullscreen = gameConfig.displayInfo.fullscreen;
        this.jvm64bit = isJvm64bit();
        this.theIntegratedServer = new IntegratedServer(this);

        if (gameConfig.serverInfo.serverName != null)
        {
            this.serverName = gameConfig.serverInfo.serverName;
            this.serverPort = gameConfig.serverInfo.serverPort;
        }

        ImageIO.setUseCache(false);
        Bootstrap.register();
    }

    public void run()
    {
        this.running = true;

        try
        {
            this.startGame();
        }
        catch (Throwable var11)
        {
            CrashReport lvt_2_1_ = CrashReport.makeCrashReport(var11, "Initializing game");
            lvt_2_1_.makeCategory("Initialization");
            this.displayCrashReport(this.addGraphicsAndWorldToCrashReport(lvt_2_1_));
            return;
        }

        while (true)
        {
            try
            {
                while (this.running)
                {
                    if (!this.hasCrashed || this.crashReporter == null)
                    {
                        try
                        {
                            this.runGameLoop();
                        }
                        catch (OutOfMemoryError var10)
                        {
                            this.freeMemory();
                            this.displayGuiScreen(new GuiMemoryErrorScreen());
                            System.gc();
                        }
                    }
                    else
                    {
                        this.displayCrashReport(this.crashReporter);
                    }
                }
            }
            catch (MinecraftError var12)
            {
                break;
            }
            catch (ReportedException var13)
            {
                this.addGraphicsAndWorldToCrashReport(var13.getCrashReport());
                this.freeMemory();
                logger.fatal("Reported exception thrown!", var13);
                this.displayCrashReport(var13.getCrashReport());
                break;
            }
            catch (Throwable var14)
            {
                CrashReport lvt_2_2_ = this.addGraphicsAndWorldToCrashReport(new CrashReport("Unexpected error", var14));
                this.freeMemory();
                logger.fatal("Unreported exception thrown!", var14);
                this.displayCrashReport(lvt_2_2_);
                break;
            }
            finally
            {
                this.shutdownMinecraftApplet();
            }

            return;
        }
    }

    /**
     * Starts the game: initializes the canvas, the title, the settings, etcetera.
     */
    private void startGame() throws LWJGLException, IOException
    {
        this.gameSettings = new GameSettings(this, this.mcDataDir);
        this.defaultResourcePacks.add(this.mcDefaultResourcePack);
        this.startTimerHackThread();

        if (this.gameSettings.overrideHeight > 0 && this.gameSettings.overrideWidth > 0)
        {
            this.displayWidth = this.gameSettings.overrideWidth;
            this.displayHeight = this.gameSettings.overrideHeight;
        }

        logger.info("LWJGL Version: " + Sys.getVersion());
        this.setWindowIcon();
        this.setInitialDisplayMode();
        this.createDisplay();
        OpenGlHelper.initializeTextures();
        this.framebufferMc = new Framebuffer(this.displayWidth, this.displayHeight, true);
        this.framebufferMc.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
        this.registerMetadataSerializers();
        this.mcResourcePackRepository = new ResourcePackRepository(this.fileResourcepacks, new File(this.mcDataDir, "server-resource-packs"), this.mcDefaultResourcePack, this.metadataSerializer_, this.gameSettings);
        this.mcResourceManager = new SimpleReloadableResourceManager(this.metadataSerializer_);
        this.mcLanguageManager = new LanguageManager(this.metadataSerializer_, this.gameSettings.language);
        this.mcResourceManager.registerReloadListener(this.mcLanguageManager);
        this.refreshResources();
        this.renderEngine = new TextureManager(this.mcResourceManager);
        this.mcResourceManager.registerReloadListener(this.renderEngine);
        this.drawSplashScreen(this.renderEngine);
        this.initStream();
        this.skinManager = new SkinManager(this.renderEngine, new File(this.fileAssets, "skins"), this.sessionService);
        this.saveLoader = new AnvilSaveConverter(new File(this.mcDataDir, "saves"));
        this.mcSoundHandler = new SoundHandler(this.mcResourceManager, this.gameSettings);
        this.mcResourceManager.registerReloadListener(this.mcSoundHandler);
        this.mcMusicTicker = new MusicTicker(this);
        this.fontRendererObj = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.renderEngine, false);

        if (this.gameSettings.language != null)
        {
            this.fontRendererObj.setUnicodeFlag(this.isUnicode());
            this.fontRendererObj.setBidiFlag(this.mcLanguageManager.isCurrentLanguageBidirectional());
        }

        this.standardGalacticFontRenderer = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii_sga.png"), this.renderEngine, false);
        this.mcResourceManager.registerReloadListener(this.fontRendererObj);
        this.mcResourceManager.registerReloadListener(this.standardGalacticFontRenderer);
        this.mcResourceManager.registerReloadListener(new GrassColorReloadListener());
        this.mcResourceManager.registerReloadListener(new FoliageColorReloadListener());
        AchievementList.openInventory.setStatStringFormatter(new IStatStringFormat()
        {
            public String formatString(String str)
            {
                try
                {
                    return String.format(str, new Object[] {GameSettings.getKeyDisplayString(Minecraft.this.gameSettings.keyBindInventory.getKeyCode())});
                }
                catch (Exception var3)
                {
                    return "Error: " + var3.getLocalizedMessage();
                }
            }
        });
        this.mouseHelper = new MouseHelper();
        this.checkGLError("Pre startup");
        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(7425);
        GlStateManager.clearDepth(1.0D);
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.cullFace(1029);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        this.checkGLError("Startup");
        this.textureMapBlocks = new TextureMap("textures");
        this.textureMapBlocks.setMipmapLevels(this.gameSettings.mipmapLevels);
        this.renderEngine.loadTickableTexture(TextureMap.locationBlocksTexture, this.textureMapBlocks);
        this.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        this.textureMapBlocks.setBlurMipmapDirect(false, this.gameSettings.mipmapLevels > 0);
        this.modelManager = new ModelManager(this.textureMapBlocks);
        this.mcResourceManager.registerReloadListener(this.modelManager);
        this.renderItem = new RenderItem(this.renderEngine, this.modelManager);
        this.renderManager = new RenderManager(this.renderEngine, this.renderItem);
        this.itemRenderer = new ItemRenderer(this);
        this.mcResourceManager.registerReloadListener(this.renderItem);
        this.entityRenderer = new EntityRenderer(this, this.mcResourceManager);
        this.mcResourceManager.registerReloadListener(this.entityRenderer);
        this.blockRenderDispatcher = new BlockRendererDispatcher(this.modelManager.getBlockModelShapes(), this.gameSettings);
        this.mcResourceManager.registerReloadListener(this.blockRenderDispatcher);
        this.renderGlobal = new RenderGlobal(this);
        this.mcResourceManager.registerReloadListener(this.renderGlobal);
        this.guiAchievement = new GuiAchievement(this);
        GlStateManager.viewport(0, 0, this.displayWidth, this.displayHeight);
        this.effectRenderer = new EffectRenderer(this.theWorld, this.renderEngine);
        this.checkGLError("Post startup");
        this.ingameGUI = new GuiIngame(this);

        if (this.serverName != null)
        {
            this.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), this, this.serverName, this.serverPort));
        }
        else
        {
            this.displayGuiScreen(new GuiMainMenu());
        }

        this.renderEngine.deleteTexture(this.mojangLogo);
        this.mojangLogo = null;
        this.loadingScreen = new LoadingScreenRenderer(this);

        if (this.gameSettings.fullScreen && !this.fullscreen)
        {
            this.toggleFullscreen();
        }

        try
        {
            Display.setVSyncEnabled(this.gameSettings.enableVsync);
        }
        catch (OpenGLException var2)
        {
            this.gameSettings.enableVsync = false;
            this.gameSettings.saveOptions();
        }

        this.renderGlobal.makeEntityOutlineShader();
    }

    private void registerMetadataSerializers()
    {
        this.metadataSerializer_.registerMetadataSectionType(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new FontMetadataSectionSerializer(), FontMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new PackMetadataSectionSerializer(), PackMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new LanguageMetadataSectionSerializer(), LanguageMetadataSection.class);
    }

    private void initStream()
    {
        try
        {
            this.stream = new TwitchStream(this, (Property)Iterables.getFirst(this.twitchDetails.get("twitch_access_token"), (Object)null));
        }
        catch (Throwable var2)
        {
            this.stream = new NullStream(var2);
            logger.error("Couldn\'t initialize twitch stream");
        }
    }

    private void createDisplay() throws LWJGLException
    {
        Display.setResizable(true);
        Display.setTitle("Minecraft 1.8.9");

        try
        {
            Display.create((new PixelFormat()).withDepthBits(24));
        }
        catch (LWJGLException var4)
        {
            logger.error("Couldn\'t set pixel format", var4);

            try
            {
                Thread.sleep(1000L);
            }
            catch (InterruptedException var3)
            {
                ;
            }

            if (this.fullscreen)
            {
                this.updateDisplayMode();
            }

            Display.create();
        }
    }

    private void setInitialDisplayMode() throws LWJGLException
    {
        if (this.fullscreen)
        {
            Display.setFullscreen(true);
            DisplayMode lvt_1_1_ = Display.getDisplayMode();
            this.displayWidth = Math.max(1, lvt_1_1_.getWidth());
            this.displayHeight = Math.max(1, lvt_1_1_.getHeight());
        }
        else
        {
            Display.setDisplayMode(new DisplayMode(this.displayWidth, this.displayHeight));
        }
    }

    private void setWindowIcon()
    {
        Util.EnumOS lvt_1_1_ = Util.getOSType();

        if (lvt_1_1_ != Util.EnumOS.OSX)
        {
            InputStream lvt_2_1_ = null;
            InputStream lvt_3_1_ = null;

            try
            {
                lvt_2_1_ = this.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("icons/icon_16x16.png"));
                lvt_3_1_ = this.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("icons/icon_32x32.png"));

                if (lvt_2_1_ != null && lvt_3_1_ != null)
                {
                    Display.setIcon(new ByteBuffer[] {this.readImageToBuffer(lvt_2_1_), this.readImageToBuffer(lvt_3_1_)});
                }
            }
            catch (IOException var8)
            {
                logger.error("Couldn\'t set icon", var8);
            }
            finally
            {
                IOUtils.closeQuietly(lvt_2_1_);
                IOUtils.closeQuietly(lvt_3_1_);
            }
        }
    }

    private static boolean isJvm64bit()
    {
        String[] lvt_0_1_ = new String[] {"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

        for (String lvt_4_1_ : lvt_0_1_)
        {
            String lvt_5_1_ = System.getProperty(lvt_4_1_);

            if (lvt_5_1_ != null && lvt_5_1_.contains("64"))
            {
                return true;
            }
        }

        return false;
    }

    public Framebuffer getFramebuffer()
    {
        return this.framebufferMc;
    }

    public String getVersion()
    {
        return this.launchedVersion;
    }

    private void startTimerHackThread()
    {
        Thread lvt_1_1_ = new Thread("Timer hack thread")
        {
            public void run()
            {
                while (Minecraft.this.running)
                {
                    try
                    {
                        Thread.sleep(2147483647L);
                    }
                    catch (InterruptedException var2)
                    {
                        ;
                    }
                }
            }
        };
        lvt_1_1_.setDaemon(true);
        lvt_1_1_.start();
    }

    public void crashed(CrashReport crash)
    {
        this.hasCrashed = true;
        this.crashReporter = crash;
    }

    /**
     * Wrapper around displayCrashReportInternal
     */
    public void displayCrashReport(CrashReport crashReportIn)
    {
        File lvt_2_1_ = new File(getMinecraft().mcDataDir, "crash-reports");
        File lvt_3_1_ = new File(lvt_2_1_, "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-client.txt");
        Bootstrap.printToSYSOUT(crashReportIn.getCompleteReport());

        if (crashReportIn.getFile() != null)
        {
            Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + crashReportIn.getFile());
            System.exit(-1);
        }
        else if (crashReportIn.saveToFile(lvt_3_1_))
        {
            Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + lvt_3_1_.getAbsolutePath());
            System.exit(-1);
        }
        else
        {
            Bootstrap.printToSYSOUT("#@?@# Game crashed! Crash report could not be saved. #@?@#");
            System.exit(-2);
        }
    }

    public boolean isUnicode()
    {
        return this.mcLanguageManager.isCurrentLocaleUnicode() || this.gameSettings.forceUnicodeFont;
    }

    public void refreshResources()
    {
        List<IResourcePack> lvt_1_1_ = Lists.newArrayList(this.defaultResourcePacks);

        for (ResourcePackRepository.Entry lvt_3_1_ : this.mcResourcePackRepository.getRepositoryEntries())
        {
            lvt_1_1_.add(lvt_3_1_.getResourcePack());
        }

        if (this.mcResourcePackRepository.getResourcePackInstance() != null)
        {
            lvt_1_1_.add(this.mcResourcePackRepository.getResourcePackInstance());
        }

        try
        {
            this.mcResourceManager.reloadResources(lvt_1_1_);
        }
        catch (RuntimeException var4)
        {
            logger.info("Caught error stitching, removing all assigned resourcepacks", var4);
            lvt_1_1_.clear();
            lvt_1_1_.addAll(this.defaultResourcePacks);
            this.mcResourcePackRepository.setRepositories(Collections.emptyList());
            this.mcResourceManager.reloadResources(lvt_1_1_);
            this.gameSettings.resourcePacks.clear();
            this.gameSettings.incompatibleResourcePacks.clear();
            this.gameSettings.saveOptions();
        }

        this.mcLanguageManager.parseLanguageMetadata(lvt_1_1_);

        if (this.renderGlobal != null)
        {
            this.renderGlobal.loadRenderers();
        }
    }

    private ByteBuffer readImageToBuffer(InputStream imageStream) throws IOException
    {
        BufferedImage lvt_2_1_ = ImageIO.read(imageStream);
        int[] lvt_3_1_ = lvt_2_1_.getRGB(0, 0, lvt_2_1_.getWidth(), lvt_2_1_.getHeight(), (int[])null, 0, lvt_2_1_.getWidth());
        ByteBuffer lvt_4_1_ = ByteBuffer.allocate(4 * lvt_3_1_.length);

        for (int lvt_8_1_ : lvt_3_1_)
        {
            lvt_4_1_.putInt(lvt_8_1_ << 8 | lvt_8_1_ >> 24 & 255);
        }

        lvt_4_1_.flip();
        return lvt_4_1_;
    }

    private void updateDisplayMode() throws LWJGLException
    {
        Set<DisplayMode> lvt_1_1_ = Sets.newHashSet();
        Collections.addAll(lvt_1_1_, Display.getAvailableDisplayModes());
        DisplayMode lvt_2_1_ = Display.getDesktopDisplayMode();

        if (!lvt_1_1_.contains(lvt_2_1_) && Util.getOSType() == Util.EnumOS.OSX)
        {
            label53:

            for (DisplayMode lvt_4_1_ : macDisplayModes)
            {
                boolean lvt_5_1_ = true;

                for (DisplayMode lvt_7_1_ : lvt_1_1_)
                {
                    if (lvt_7_1_.getBitsPerPixel() == 32 && lvt_7_1_.getWidth() == lvt_4_1_.getWidth() && lvt_7_1_.getHeight() == lvt_4_1_.getHeight())
                    {
                        lvt_5_1_ = false;
                        break;
                    }
                }

                if (!lvt_5_1_)
                {
                    Iterator lvt_6_2_ = lvt_1_1_.iterator();
                    DisplayMode lvt_7_2_;

                    while (true)
                    {
                        if (!lvt_6_2_.hasNext())
                        {
                            continue label53;
                        }

                        lvt_7_2_ = (DisplayMode)lvt_6_2_.next();

                        if (lvt_7_2_.getBitsPerPixel() == 32 && lvt_7_2_.getWidth() == lvt_4_1_.getWidth() / 2 && lvt_7_2_.getHeight() == lvt_4_1_.getHeight() / 2)
                        {
                            break;
                        }
                    }

                    lvt_2_1_ = lvt_7_2_;
                }
            }
        }

        Display.setDisplayMode(lvt_2_1_);
        this.displayWidth = lvt_2_1_.getWidth();
        this.displayHeight = lvt_2_1_.getHeight();
    }

    private void drawSplashScreen(TextureManager textureManagerInstance) throws LWJGLException
    {
        ScaledResolution lvt_2_1_ = new ScaledResolution(this);
        int lvt_3_1_ = lvt_2_1_.getScaleFactor();
        Framebuffer lvt_4_1_ = new Framebuffer(lvt_2_1_.getScaledWidth() * lvt_3_1_, lvt_2_1_.getScaledHeight() * lvt_3_1_, true);
        lvt_4_1_.bindFramebuffer(false);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, (double)lvt_2_1_.getScaledWidth(), (double)lvt_2_1_.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();
        InputStream lvt_5_1_ = null;

        try
        {
            lvt_5_1_ = this.mcDefaultResourcePack.getInputStream(locationMojangPng);
            this.mojangLogo = textureManagerInstance.getDynamicTextureLocation("logo", new DynamicTexture(ImageIO.read(lvt_5_1_)));
            textureManagerInstance.bindTexture(this.mojangLogo);
        }
        catch (IOException var12)
        {
            logger.error("Unable to load logo: " + locationMojangPng, var12);
        }
        finally
        {
            IOUtils.closeQuietly(lvt_5_1_);
        }

        Tessellator lvt_6_2_ = Tessellator.getInstance();
        WorldRenderer lvt_7_1_ = lvt_6_2_.getWorldRenderer();
        lvt_7_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        lvt_7_1_.pos(0.0D, (double)this.displayHeight, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
        lvt_7_1_.pos((double)this.displayWidth, (double)this.displayHeight, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
        lvt_7_1_.pos((double)this.displayWidth, 0.0D, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
        lvt_7_1_.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
        lvt_6_2_.draw();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        int lvt_8_1_ = 256;
        int lvt_9_1_ = 256;
        this.draw((lvt_2_1_.getScaledWidth() - lvt_8_1_) / 2, (lvt_2_1_.getScaledHeight() - lvt_9_1_) / 2, 0, 0, lvt_8_1_, lvt_9_1_, 255, 255, 255, 255);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        lvt_4_1_.unbindFramebuffer();
        lvt_4_1_.framebufferRender(lvt_2_1_.getScaledWidth() * lvt_3_1_, lvt_2_1_.getScaledHeight() * lvt_3_1_);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        this.updateDisplay();
    }

    /**
     * Draw with the WorldRenderer
     *  
     * @param posX X position for the render
     * @param posY Y position for the render
     * @param texU X position for the texture
     * @param texV Y position for the texture
     * @param width Width of the render
     * @param height Height of the render
     * @param red The red component of the render's color
     * @param green The green component of the render's color
     * @param blue The blue component of the render's color
     * @param alpha The alpha component of the render's color
     */
    public void draw(int posX, int posY, int texU, int texV, int width, int height, int red, int green, int blue, int alpha)
    {
        float lvt_11_1_ = 0.00390625F;
        float lvt_12_1_ = 0.00390625F;
        WorldRenderer lvt_13_1_ = Tessellator.getInstance().getWorldRenderer();
        lvt_13_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        lvt_13_1_.pos((double)posX, (double)(posY + height), 0.0D).tex((double)((float)texU * lvt_11_1_), (double)((float)(texV + height) * lvt_12_1_)).color(red, green, blue, alpha).endVertex();
        lvt_13_1_.pos((double)(posX + width), (double)(posY + height), 0.0D).tex((double)((float)(texU + width) * lvt_11_1_), (double)((float)(texV + height) * lvt_12_1_)).color(red, green, blue, alpha).endVertex();
        lvt_13_1_.pos((double)(posX + width), (double)posY, 0.0D).tex((double)((float)(texU + width) * lvt_11_1_), (double)((float)texV * lvt_12_1_)).color(red, green, blue, alpha).endVertex();
        lvt_13_1_.pos((double)posX, (double)posY, 0.0D).tex((double)((float)texU * lvt_11_1_), (double)((float)texV * lvt_12_1_)).color(red, green, blue, alpha).endVertex();
        Tessellator.getInstance().draw();
    }

    /**
     * Returns the save loader that is currently being used
     */
    public ISaveFormat getSaveLoader()
    {
        return this.saveLoader;
    }

    /**
     * Sets the argument GuiScreen as the main (topmost visible) screen.
     */
    public void displayGuiScreen(GuiScreen guiScreenIn)
    {
        if (this.currentScreen != null)
        {
            this.currentScreen.onGuiClosed();
        }

        if (guiScreenIn == null && this.theWorld == null)
        {
            guiScreenIn = new GuiMainMenu();
        }
        else if (guiScreenIn == null && this.thePlayer.getHealth() <= 0.0F)
        {
            guiScreenIn = new GuiGameOver();
        }

        if (guiScreenIn instanceof GuiMainMenu)
        {
            this.gameSettings.showDebugInfo = false;
            this.ingameGUI.getChatGUI().clearChatMessages();
        }

        this.currentScreen = (GuiScreen)guiScreenIn;

        if (guiScreenIn != null)
        {
            this.setIngameNotInFocus();
            ScaledResolution lvt_2_1_ = new ScaledResolution(this);
            int lvt_3_1_ = lvt_2_1_.getScaledWidth();
            int lvt_4_1_ = lvt_2_1_.getScaledHeight();
            ((GuiScreen)guiScreenIn).setWorldAndResolution(this, lvt_3_1_, lvt_4_1_);
            this.skipRenderWorld = false;
        }
        else
        {
            this.mcSoundHandler.resumeSounds();
            this.setIngameFocus();
        }
    }

    /**
     * Checks for an OpenGL error. If there is one, prints the error ID and error string.
     */
    private void checkGLError(String message)
    {
        if (this.enableGLErrorChecking)
        {
            int lvt_2_1_ = GL11.glGetError();

            if (lvt_2_1_ != 0)
            {
                String lvt_3_1_ = GLU.gluErrorString(lvt_2_1_);
                logger.error("########## GL ERROR ##########");
                logger.error("@ " + message);
                logger.error(lvt_2_1_ + ": " + lvt_3_1_);
            }
        }
    }

    /**
     * Shuts down the minecraft applet by stopping the resource downloads, and clearing up GL stuff; called when the
     * application (or web page) is exited.
     */
    public void shutdownMinecraftApplet()
    {
        try
        {
            this.stream.shutdownStream();
            logger.info("Stopping!");

            try
            {
                this.loadWorld((WorldClient)null);
            }
            catch (Throwable var5)
            {
                ;
            }

            this.mcSoundHandler.unloadSounds();
        }
        finally
        {
            Display.destroy();

            if (!this.hasCrashed)
            {
                System.exit(0);
            }
        }

        System.gc();
    }

    /**
     * Called repeatedly from run()
     */
    private void runGameLoop() throws IOException
    {
        long lvt_1_1_ = System.nanoTime();
        this.mcProfiler.startSection("root");

        if (Display.isCreated() && Display.isCloseRequested())
        {
            this.shutdown();
        }

        if (this.isGamePaused && this.theWorld != null)
        {
            float lvt_3_1_ = this.timer.renderPartialTicks;
            this.timer.updateTimer();
            this.timer.renderPartialTicks = lvt_3_1_;
        }
        else
        {
            this.timer.updateTimer();
        }

        this.mcProfiler.startSection("scheduledExecutables");

        synchronized (this.scheduledTasks)
        {
            while (!this.scheduledTasks.isEmpty())
            {
                Util.runTask((FutureTask)this.scheduledTasks.poll(), logger);
            }
        }

        this.mcProfiler.endSection();
        long lvt_3_2_ = System.nanoTime();
        this.mcProfiler.startSection("tick");

        for (int lvt_5_1_ = 0; lvt_5_1_ < this.timer.elapsedTicks; ++lvt_5_1_)
        {
            this.runTick();
        }

        this.mcProfiler.endStartSection("preRenderErrors");
        long lvt_5_2_ = System.nanoTime() - lvt_3_2_;
        this.checkGLError("Pre render");
        this.mcProfiler.endStartSection("sound");
        this.mcSoundHandler.setListener(this.thePlayer, this.timer.renderPartialTicks);
        this.mcProfiler.endSection();
        this.mcProfiler.startSection("render");
        GlStateManager.pushMatrix();
        GlStateManager.clear(16640);
        this.framebufferMc.bindFramebuffer(true);
        this.mcProfiler.startSection("display");
        GlStateManager.enableTexture2D();

        if (this.thePlayer != null && this.thePlayer.isEntityInsideOpaqueBlock())
        {
            this.gameSettings.thirdPersonView = 0;
        }

        this.mcProfiler.endSection();

        if (!this.skipRenderWorld)
        {
            this.mcProfiler.endStartSection("gameRenderer");
            this.entityRenderer.updateCameraAndRender(this.timer.renderPartialTicks, lvt_1_1_);
            this.mcProfiler.endSection();
        }

        this.mcProfiler.endSection();

        if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart && !this.gameSettings.hideGUI)
        {
            if (!this.mcProfiler.profilingEnabled)
            {
                this.mcProfiler.clearProfiling();
            }

            this.mcProfiler.profilingEnabled = true;
            this.displayDebugInfo(lvt_5_2_);
        }
        else
        {
            this.mcProfiler.profilingEnabled = false;
            this.prevFrameTime = System.nanoTime();
        }

        this.guiAchievement.updateAchievementWindow();
        this.framebufferMc.unbindFramebuffer();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        this.framebufferMc.framebufferRender(this.displayWidth, this.displayHeight);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        this.entityRenderer.renderStreamIndicator(this.timer.renderPartialTicks);
        GlStateManager.popMatrix();
        this.mcProfiler.startSection("root");
        this.updateDisplay();
        Thread.yield();
        this.mcProfiler.startSection("stream");
        this.mcProfiler.startSection("update");
        this.stream.func_152935_j();
        this.mcProfiler.endStartSection("submit");
        this.stream.func_152922_k();
        this.mcProfiler.endSection();
        this.mcProfiler.endSection();
        this.checkGLError("Post render");
        ++this.fpsCounter;
        this.isGamePaused = this.isSingleplayer() && this.currentScreen != null && this.currentScreen.doesGuiPauseGame() && !this.theIntegratedServer.getPublic();
        long lvt_7_1_ = System.nanoTime();
        this.frameTimer.addFrame(lvt_7_1_ - this.startNanoTime);
        this.startNanoTime = lvt_7_1_;

        while (getSystemTime() >= this.debugUpdateTime + 1000L)
        {
            debugFPS = this.fpsCounter;
            this.debug = String.format("%d fps (%d chunk update%s) T: %s%s%s%s%s", new Object[] {Integer.valueOf(debugFPS), Integer.valueOf(RenderChunk.renderChunksUpdated), RenderChunk.renderChunksUpdated != 1 ? "s" : "", (float)this.gameSettings.limitFramerate == GameSettings.Options.FRAMERATE_LIMIT.getValueMax() ? "inf" : Integer.valueOf(this.gameSettings.limitFramerate), this.gameSettings.enableVsync ? " vsync" : "", this.gameSettings.fancyGraphics ? "" : " fast", this.gameSettings.clouds == 0 ? "" : (this.gameSettings.clouds == 1 ? " fast-clouds" : " fancy-clouds"), OpenGlHelper.useVbo() ? " vbo" : ""});
            RenderChunk.renderChunksUpdated = 0;
            this.debugUpdateTime += 1000L;
            this.fpsCounter = 0;
            this.usageSnooper.addMemoryStatsToSnooper();

            if (!this.usageSnooper.isSnooperRunning())
            {
                this.usageSnooper.startSnooper();
            }
        }

        if (this.isFramerateLimitBelowMax())
        {
            this.mcProfiler.startSection("fpslimit_wait");
            Display.sync(this.getLimitFramerate());
            this.mcProfiler.endSection();
        }

        this.mcProfiler.endSection();
    }

    public void updateDisplay()
    {
        this.mcProfiler.startSection("display_update");
        Display.update();
        this.mcProfiler.endSection();
        this.checkWindowResize();
    }

    protected void checkWindowResize()
    {
        if (!this.fullscreen && Display.wasResized())
        {
            int lvt_1_1_ = this.displayWidth;
            int lvt_2_1_ = this.displayHeight;
            this.displayWidth = Display.getWidth();
            this.displayHeight = Display.getHeight();

            if (this.displayWidth != lvt_1_1_ || this.displayHeight != lvt_2_1_)
            {
                if (this.displayWidth <= 0)
                {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0)
                {
                    this.displayHeight = 1;
                }

                this.resize(this.displayWidth, this.displayHeight);
            }
        }
    }

    public int getLimitFramerate()
    {
        return this.theWorld == null && this.currentScreen != null ? 30 : this.gameSettings.limitFramerate;
    }

    public boolean isFramerateLimitBelowMax()
    {
        return (float)this.getLimitFramerate() < GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
    }

    public void freeMemory()
    {
        try
        {
            memoryReserve = new byte[0];
            this.renderGlobal.deleteAllDisplayLists();
        }
        catch (Throwable var3)
        {
            ;
        }

        try
        {
            System.gc();
            this.loadWorld((WorldClient)null);
        }
        catch (Throwable var2)
        {
            ;
        }

        System.gc();
    }

    /**
     * Update debugProfilerName in response to number keys in debug screen
     */
    private void updateDebugProfilerName(int keyCount)
    {
        List<Profiler.Result> lvt_2_1_ = this.mcProfiler.getProfilingData(this.debugProfilerName);

        if (lvt_2_1_ != null && !lvt_2_1_.isEmpty())
        {
            Profiler.Result lvt_3_1_ = (Profiler.Result)lvt_2_1_.remove(0);

            if (keyCount == 0)
            {
                if (lvt_3_1_.field_76331_c.length() > 0)
                {
                    int lvt_4_1_ = this.debugProfilerName.lastIndexOf(".");

                    if (lvt_4_1_ >= 0)
                    {
                        this.debugProfilerName = this.debugProfilerName.substring(0, lvt_4_1_);
                    }
                }
            }
            else
            {
                --keyCount;

                if (keyCount < lvt_2_1_.size() && !((Profiler.Result)lvt_2_1_.get(keyCount)).field_76331_c.equals("unspecified"))
                {
                    if (this.debugProfilerName.length() > 0)
                    {
                        this.debugProfilerName = this.debugProfilerName + ".";
                    }

                    this.debugProfilerName = this.debugProfilerName + ((Profiler.Result)lvt_2_1_.get(keyCount)).field_76331_c;
                }
            }
        }
    }

    /**
     * Parameter appears to be unused
     */
    private void displayDebugInfo(long elapsedTicksTime)
    {
        if (this.mcProfiler.profilingEnabled)
        {
            List<Profiler.Result> lvt_3_1_ = this.mcProfiler.getProfilingData(this.debugProfilerName);
            Profiler.Result lvt_4_1_ = (Profiler.Result)lvt_3_1_.remove(0);
            GlStateManager.clear(256);
            GlStateManager.matrixMode(5889);
            GlStateManager.enableColorMaterial();
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0D, (double)this.displayWidth, (double)this.displayHeight, 0.0D, 1000.0D, 3000.0D);
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, 0.0F, -2000.0F);
            GL11.glLineWidth(1.0F);
            GlStateManager.disableTexture2D();
            Tessellator lvt_5_1_ = Tessellator.getInstance();
            WorldRenderer lvt_6_1_ = lvt_5_1_.getWorldRenderer();
            int lvt_7_1_ = 160;
            int lvt_8_1_ = this.displayWidth - lvt_7_1_ - 10;
            int lvt_9_1_ = this.displayHeight - lvt_7_1_ * 2;
            GlStateManager.enableBlend();
            lvt_6_1_.begin(7, DefaultVertexFormats.POSITION_COLOR);
            lvt_6_1_.pos((double)((float)lvt_8_1_ - (float)lvt_7_1_ * 1.1F), (double)((float)lvt_9_1_ - (float)lvt_7_1_ * 0.6F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
            lvt_6_1_.pos((double)((float)lvt_8_1_ - (float)lvt_7_1_ * 1.1F), (double)(lvt_9_1_ + lvt_7_1_ * 2), 0.0D).color(200, 0, 0, 0).endVertex();
            lvt_6_1_.pos((double)((float)lvt_8_1_ + (float)lvt_7_1_ * 1.1F), (double)(lvt_9_1_ + lvt_7_1_ * 2), 0.0D).color(200, 0, 0, 0).endVertex();
            lvt_6_1_.pos((double)((float)lvt_8_1_ + (float)lvt_7_1_ * 1.1F), (double)((float)lvt_9_1_ - (float)lvt_7_1_ * 0.6F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
            lvt_5_1_.draw();
            GlStateManager.disableBlend();
            double lvt_10_1_ = 0.0D;

            for (int lvt_12_1_ = 0; lvt_12_1_ < lvt_3_1_.size(); ++lvt_12_1_)
            {
                Profiler.Result lvt_13_1_ = (Profiler.Result)lvt_3_1_.get(lvt_12_1_);
                int lvt_14_1_ = MathHelper.floor_double(lvt_13_1_.field_76332_a / 4.0D) + 1;
                lvt_6_1_.begin(6, DefaultVertexFormats.POSITION_COLOR);
                int lvt_15_1_ = lvt_13_1_.getColor();
                int lvt_16_1_ = lvt_15_1_ >> 16 & 255;
                int lvt_17_1_ = lvt_15_1_ >> 8 & 255;
                int lvt_18_1_ = lvt_15_1_ & 255;
                lvt_6_1_.pos((double)lvt_8_1_, (double)lvt_9_1_, 0.0D).color(lvt_16_1_, lvt_17_1_, lvt_18_1_, 255).endVertex();

                for (int lvt_19_1_ = lvt_14_1_; lvt_19_1_ >= 0; --lvt_19_1_)
                {
                    float lvt_20_1_ = (float)((lvt_10_1_ + lvt_13_1_.field_76332_a * (double)lvt_19_1_ / (double)lvt_14_1_) * Math.PI * 2.0D / 100.0D);
                    float lvt_21_1_ = MathHelper.sin(lvt_20_1_) * (float)lvt_7_1_;
                    float lvt_22_1_ = MathHelper.cos(lvt_20_1_) * (float)lvt_7_1_ * 0.5F;
                    lvt_6_1_.pos((double)((float)lvt_8_1_ + lvt_21_1_), (double)((float)lvt_9_1_ - lvt_22_1_), 0.0D).color(lvt_16_1_, lvt_17_1_, lvt_18_1_, 255).endVertex();
                }

                lvt_5_1_.draw();
                lvt_6_1_.begin(5, DefaultVertexFormats.POSITION_COLOR);

                for (int lvt_19_2_ = lvt_14_1_; lvt_19_2_ >= 0; --lvt_19_2_)
                {
                    float lvt_20_2_ = (float)((lvt_10_1_ + lvt_13_1_.field_76332_a * (double)lvt_19_2_ / (double)lvt_14_1_) * Math.PI * 2.0D / 100.0D);
                    float lvt_21_2_ = MathHelper.sin(lvt_20_2_) * (float)lvt_7_1_;
                    float lvt_22_2_ = MathHelper.cos(lvt_20_2_) * (float)lvt_7_1_ * 0.5F;
                    lvt_6_1_.pos((double)((float)lvt_8_1_ + lvt_21_2_), (double)((float)lvt_9_1_ - lvt_22_2_), 0.0D).color(lvt_16_1_ >> 1, lvt_17_1_ >> 1, lvt_18_1_ >> 1, 255).endVertex();
                    lvt_6_1_.pos((double)((float)lvt_8_1_ + lvt_21_2_), (double)((float)lvt_9_1_ - lvt_22_2_ + 10.0F), 0.0D).color(lvt_16_1_ >> 1, lvt_17_1_ >> 1, lvt_18_1_ >> 1, 255).endVertex();
                }

                lvt_5_1_.draw();
                lvt_10_1_ += lvt_13_1_.field_76332_a;
            }

            DecimalFormat lvt_12_2_ = new DecimalFormat("##0.00");
            GlStateManager.enableTexture2D();
            String lvt_13_2_ = "";

            if (!lvt_4_1_.field_76331_c.equals("unspecified"))
            {
                lvt_13_2_ = lvt_13_2_ + "[0] ";
            }

            if (lvt_4_1_.field_76331_c.length() == 0)
            {
                lvt_13_2_ = lvt_13_2_ + "ROOT ";
            }
            else
            {
                lvt_13_2_ = lvt_13_2_ + lvt_4_1_.field_76331_c + " ";
            }

            int lvt_14_2_ = 16777215;
            this.fontRendererObj.drawStringWithShadow(lvt_13_2_, (float)(lvt_8_1_ - lvt_7_1_), (float)(lvt_9_1_ - lvt_7_1_ / 2 - 16), lvt_14_2_);
            this.fontRendererObj.drawStringWithShadow(lvt_13_2_ = lvt_12_2_.format(lvt_4_1_.field_76330_b) + "%", (float)(lvt_8_1_ + lvt_7_1_ - this.fontRendererObj.getStringWidth(lvt_13_2_)), (float)(lvt_9_1_ - lvt_7_1_ / 2 - 16), lvt_14_2_);

            for (int lvt_13_3_ = 0; lvt_13_3_ < lvt_3_1_.size(); ++lvt_13_3_)
            {
                Profiler.Result lvt_14_3_ = (Profiler.Result)lvt_3_1_.get(lvt_13_3_);
                String lvt_15_2_ = "";

                if (lvt_14_3_.field_76331_c.equals("unspecified"))
                {
                    lvt_15_2_ = lvt_15_2_ + "[?] ";
                }
                else
                {
                    lvt_15_2_ = lvt_15_2_ + "[" + (lvt_13_3_ + 1) + "] ";
                }

                lvt_15_2_ = lvt_15_2_ + lvt_14_3_.field_76331_c;
                this.fontRendererObj.drawStringWithShadow(lvt_15_2_, (float)(lvt_8_1_ - lvt_7_1_), (float)(lvt_9_1_ + lvt_7_1_ / 2 + lvt_13_3_ * 8 + 20), lvt_14_3_.getColor());
                this.fontRendererObj.drawStringWithShadow(lvt_15_2_ = lvt_12_2_.format(lvt_14_3_.field_76332_a) + "%", (float)(lvt_8_1_ + lvt_7_1_ - 50 - this.fontRendererObj.getStringWidth(lvt_15_2_)), (float)(lvt_9_1_ + lvt_7_1_ / 2 + lvt_13_3_ * 8 + 20), lvt_14_3_.getColor());
                this.fontRendererObj.drawStringWithShadow(lvt_15_2_ = lvt_12_2_.format(lvt_14_3_.field_76330_b) + "%", (float)(lvt_8_1_ + lvt_7_1_ - this.fontRendererObj.getStringWidth(lvt_15_2_)), (float)(lvt_9_1_ + lvt_7_1_ / 2 + lvt_13_3_ * 8 + 20), lvt_14_3_.getColor());
            }
        }
    }

    /**
     * Called when the window is closing. Sets 'running' to false which allows the game loop to exit cleanly.
     */
    public void shutdown()
    {
        this.running = false;
    }

    /**
     * Will set the focus to ingame if the Minecraft window is the active with focus. Also clears any GUI screen
     * currently displayed
     */
    public void setIngameFocus()
    {
        if (Display.isActive())
        {
            if (!this.inGameHasFocus)
            {
                this.inGameHasFocus = true;
                this.mouseHelper.grabMouseCursor();
                this.displayGuiScreen((GuiScreen)null);
                this.leftClickCounter = 10000;
            }
        }
    }

    /**
     * Resets the player keystate, disables the ingame focus, and ungrabs the mouse cursor.
     */
    public void setIngameNotInFocus()
    {
        if (this.inGameHasFocus)
        {
            KeyBinding.unPressAllKeys();
            this.inGameHasFocus = false;
            this.mouseHelper.ungrabMouseCursor();
        }
    }

    /**
     * Displays the ingame menu
     */
    public void displayInGameMenu()
    {
        if (this.currentScreen == null)
        {
            this.displayGuiScreen(new GuiIngameMenu());

            if (this.isSingleplayer() && !this.theIntegratedServer.getPublic())
            {
                this.mcSoundHandler.pauseSounds();
            }
        }
    }

    private void sendClickBlockToController(boolean leftClick)
    {
        if (!leftClick)
        {
            this.leftClickCounter = 0;
        }

        if (this.leftClickCounter <= 0 && !this.thePlayer.isUsingItem())
        {
            if (leftClick && this.objectMouseOver != null && this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            {
                BlockPos lvt_2_1_ = this.objectMouseOver.getBlockPos();

                if (this.theWorld.getBlockState(lvt_2_1_).getBlock().getMaterial() != Material.air && this.playerController.onPlayerDamageBlock(lvt_2_1_, this.objectMouseOver.sideHit))
                {
                    this.effectRenderer.addBlockHitEffects(lvt_2_1_, this.objectMouseOver.sideHit);
                    this.thePlayer.swingItem();
                }
            }
            else
            {
                this.playerController.resetBlockRemoving();
            }
        }
    }

    private void clickMouse()
    {
        if (this.leftClickCounter <= 0)
        {
            this.thePlayer.swingItem();

            if (this.objectMouseOver == null)
            {
                logger.error("Null returned as \'hitResult\', this shouldn\'t happen!");

                if (this.playerController.isNotCreative())
                {
                    this.leftClickCounter = 10;
                }
            }
            else
            {
                switch (this.objectMouseOver.typeOfHit)
                {
                    case ENTITY:
                        this.playerController.attackEntity(this.thePlayer, this.objectMouseOver.entityHit);
                        break;

                    case BLOCK:
                        BlockPos lvt_1_1_ = this.objectMouseOver.getBlockPos();

                        if (this.theWorld.getBlockState(lvt_1_1_).getBlock().getMaterial() != Material.air)
                        {
                            this.playerController.clickBlock(lvt_1_1_, this.objectMouseOver.sideHit);
                            break;
                        }

                    case MISS:
                    default:
                        if (this.playerController.isNotCreative())
                        {
                            this.leftClickCounter = 10;
                        }
                }
            }
        }
    }

    @SuppressWarnings("incomplete-switch")

    /**
     * Called when user clicked he's mouse right button (place)
     */
    private void rightClickMouse()
    {
        if (!this.playerController.getIsHittingBlock())
        {
            this.rightClickDelayTimer = 4;
            boolean lvt_1_1_ = true;
            ItemStack lvt_2_1_ = this.thePlayer.inventory.getCurrentItem();

            if (this.objectMouseOver == null)
            {
                logger.warn("Null returned as \'hitResult\', this shouldn\'t happen!");
            }
            else
            {
                switch (this.objectMouseOver.typeOfHit)
                {
                    case ENTITY:
                        if (this.playerController.isPlayerRightClickingOnEntity(this.thePlayer, this.objectMouseOver.entityHit, this.objectMouseOver))
                        {
                            lvt_1_1_ = false;
                        }
                        else if (this.playerController.interactWithEntitySendPacket(this.thePlayer, this.objectMouseOver.entityHit))
                        {
                            lvt_1_1_ = false;
                        }

                        break;

                    case BLOCK:
                        BlockPos lvt_3_1_ = this.objectMouseOver.getBlockPos();

                        if (this.theWorld.getBlockState(lvt_3_1_).getBlock().getMaterial() != Material.air)
                        {
                            int lvt_4_1_ = lvt_2_1_ != null ? lvt_2_1_.stackSize : 0;

                            if (this.playerController.onPlayerRightClick(this.thePlayer, this.theWorld, lvt_2_1_, lvt_3_1_, this.objectMouseOver.sideHit, this.objectMouseOver.hitVec))
                            {
                                lvt_1_1_ = false;
                                this.thePlayer.swingItem();
                            }

                            if (lvt_2_1_ == null)
                            {
                                return;
                            }

                            if (lvt_2_1_.stackSize == 0)
                            {
                                this.thePlayer.inventory.mainInventory[this.thePlayer.inventory.currentItem] = null;
                            }
                            else if (lvt_2_1_.stackSize != lvt_4_1_ || this.playerController.isInCreativeMode())
                            {
                                this.entityRenderer.itemRenderer.resetEquippedProgress();
                            }
                        }
                }
            }

            if (lvt_1_1_)
            {
                ItemStack lvt_3_2_ = this.thePlayer.inventory.getCurrentItem();

                if (lvt_3_2_ != null && this.playerController.sendUseItem(this.thePlayer, this.theWorld, lvt_3_2_))
                {
                    this.entityRenderer.itemRenderer.resetEquippedProgress2();
                }
            }
        }
    }

    /**
     * Toggles fullscreen mode.
     */
    public void toggleFullscreen()
    {
        try
        {
            this.fullscreen = !this.fullscreen;
            this.gameSettings.fullScreen = this.fullscreen;

            if (this.fullscreen)
            {
                this.updateDisplayMode();
                this.displayWidth = Display.getDisplayMode().getWidth();
                this.displayHeight = Display.getDisplayMode().getHeight();

                if (this.displayWidth <= 0)
                {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0)
                {
                    this.displayHeight = 1;
                }
            }
            else
            {
                Display.setDisplayMode(new DisplayMode(this.tempDisplayWidth, this.tempDisplayHeight));
                this.displayWidth = this.tempDisplayWidth;
                this.displayHeight = this.tempDisplayHeight;

                if (this.displayWidth <= 0)
                {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0)
                {
                    this.displayHeight = 1;
                }
            }

            if (this.currentScreen != null)
            {
                this.resize(this.displayWidth, this.displayHeight);
            }
            else
            {
                this.updateFramebufferSize();
            }

            Display.setFullscreen(this.fullscreen);
            Display.setVSyncEnabled(this.gameSettings.enableVsync);
            this.updateDisplay();
        }
        catch (Exception var2)
        {
            logger.error("Couldn\'t toggle fullscreen", var2);
        }
    }

    /**
     * Called to resize the current screen.
     */
    private void resize(int width, int height)
    {
        this.displayWidth = Math.max(1, width);
        this.displayHeight = Math.max(1, height);

        if (this.currentScreen != null)
        {
            ScaledResolution lvt_3_1_ = new ScaledResolution(this);
            this.currentScreen.onResize(this, lvt_3_1_.getScaledWidth(), lvt_3_1_.getScaledHeight());
        }

        this.loadingScreen = new LoadingScreenRenderer(this);
        this.updateFramebufferSize();
    }

    private void updateFramebufferSize()
    {
        this.framebufferMc.createBindFramebuffer(this.displayWidth, this.displayHeight);

        if (this.entityRenderer != null)
        {
            this.entityRenderer.updateShaderGroupSize(this.displayWidth, this.displayHeight);
        }
    }

    /**
     * Return the musicTicker's instance
     */
    public MusicTicker getMusicTicker()
    {
        return this.mcMusicTicker;
    }

    /**
     * Runs the current tick.
     */
    public void runTick() throws IOException
    {
        if (this.rightClickDelayTimer > 0)
        {
            --this.rightClickDelayTimer;
        }

        this.mcProfiler.startSection("gui");

        if (!this.isGamePaused)
        {
            this.ingameGUI.updateTick();
        }

        this.mcProfiler.endSection();
        this.entityRenderer.getMouseOver(1.0F);
        this.mcProfiler.startSection("gameMode");

        if (!this.isGamePaused && this.theWorld != null)
        {
            this.playerController.updateController();
        }

        this.mcProfiler.endStartSection("textures");

        if (!this.isGamePaused)
        {
            this.renderEngine.tick();
        }

        if (this.currentScreen == null && this.thePlayer != null)
        {
            if (this.thePlayer.getHealth() <= 0.0F)
            {
                this.displayGuiScreen((GuiScreen)null);
            }
            else if (this.thePlayer.isPlayerSleeping() && this.theWorld != null)
            {
                this.displayGuiScreen(new GuiSleepMP());
            }
        }
        else if (this.currentScreen != null && this.currentScreen instanceof GuiSleepMP && !this.thePlayer.isPlayerSleeping())
        {
            this.displayGuiScreen((GuiScreen)null);
        }

        if (this.currentScreen != null)
        {
            this.leftClickCounter = 10000;
        }

        if (this.currentScreen != null)
        {
            try
            {
                this.currentScreen.handleInput();
            }
            catch (Throwable var7)
            {
                CrashReport lvt_2_1_ = CrashReport.makeCrashReport(var7, "Updating screen events");
                CrashReportCategory lvt_3_1_ = lvt_2_1_.makeCategory("Affected screen");
                lvt_3_1_.addCrashSectionCallable("Screen name", new Callable<String>()
                {
                    public String call() throws Exception
                    {
                        return Minecraft.this.currentScreen.getClass().getCanonicalName();
                    }
                    public Object call() throws Exception
                    {
                        return this.call();
                    }
                });
                throw new ReportedException(lvt_2_1_);
            }

            if (this.currentScreen != null)
            {
                try
                {
                    this.currentScreen.updateScreen();
                }
                catch (Throwable var6)
                {
                    CrashReport lvt_2_2_ = CrashReport.makeCrashReport(var6, "Ticking screen");
                    CrashReportCategory lvt_3_2_ = lvt_2_2_.makeCategory("Affected screen");
                    lvt_3_2_.addCrashSectionCallable("Screen name", new Callable<String>()
                    {
                        public String call() throws Exception
                        {
                            return Minecraft.this.currentScreen.getClass().getCanonicalName();
                        }
                        public Object call() throws Exception
                        {
                            return this.call();
                        }
                    });
                    throw new ReportedException(lvt_2_2_);
                }
            }
        }

        if (this.currentScreen == null || this.currentScreen.allowUserInput)
        {
            this.mcProfiler.endStartSection("mouse");

            while (Mouse.next())
            {
                int lvt_1_3_ = Mouse.getEventButton();
                KeyBinding.setKeyBindState(lvt_1_3_ - 100, Mouse.getEventButtonState());

                if (Mouse.getEventButtonState())
                {
                    if (this.thePlayer.isSpectator() && lvt_1_3_ == 2)
                    {
                        this.ingameGUI.getSpectatorGui().func_175261_b();
                    }
                    else
                    {
                        KeyBinding.onTick(lvt_1_3_ - 100);
                    }
                }

                long lvt_2_3_ = getSystemTime() - this.systemTime;

                if (lvt_2_3_ <= 200L)
                {
                    int lvt_4_1_ = Mouse.getEventDWheel();

                    if (lvt_4_1_ != 0)
                    {
                        if (this.thePlayer.isSpectator())
                        {
                            lvt_4_1_ = lvt_4_1_ < 0 ? -1 : 1;

                            if (this.ingameGUI.getSpectatorGui().func_175262_a())
                            {
                                this.ingameGUI.getSpectatorGui().func_175259_b(-lvt_4_1_);
                            }
                            else
                            {
                                float lvt_5_1_ = MathHelper.clamp_float(this.thePlayer.capabilities.getFlySpeed() + (float)lvt_4_1_ * 0.005F, 0.0F, 0.2F);
                                this.thePlayer.capabilities.setFlySpeed(lvt_5_1_);
                            }
                        }
                        else
                        {
                            this.thePlayer.inventory.changeCurrentItem(lvt_4_1_);
                        }
                    }

                    if (this.currentScreen == null)
                    {
                        if (!this.inGameHasFocus && Mouse.getEventButtonState())
                        {
                            this.setIngameFocus();
                        }
                    }
                    else if (this.currentScreen != null)
                    {
                        this.currentScreen.handleMouseInput();
                    }
                }
            }

            if (this.leftClickCounter > 0)
            {
                --this.leftClickCounter;
            }

            this.mcProfiler.endStartSection("keyboard");

            while (Keyboard.next())
            {
                int lvt_1_4_ = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
                KeyBinding.setKeyBindState(lvt_1_4_, Keyboard.getEventKeyState());

                if (Keyboard.getEventKeyState())
                {
                    KeyBinding.onTick(lvt_1_4_);
                }

                if (this.debugCrashKeyPressTime > 0L)
                {
                    if (getSystemTime() - this.debugCrashKeyPressTime >= 6000L)
                    {
                        throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
                    }

                    if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61))
                    {
                        this.debugCrashKeyPressTime = -1L;
                    }
                }
                else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61))
                {
                    this.debugCrashKeyPressTime = getSystemTime();
                }

                this.dispatchKeypresses();

                if (Keyboard.getEventKeyState())
                {
                    if (lvt_1_4_ == 62 && this.entityRenderer != null)
                    {
                        this.entityRenderer.switchUseShader();
                    }

                    if (this.currentScreen != null)
                    {
                        this.currentScreen.handleKeyboardInput();
                    }
                    else
                    {
                        if (lvt_1_4_ == 1)
                        {
                            this.displayInGameMenu();
                        }

                        if (lvt_1_4_ == 32 && Keyboard.isKeyDown(61) && this.ingameGUI != null)
                        {
                            this.ingameGUI.getChatGUI().clearChatMessages();
                        }

                        if (lvt_1_4_ == 31 && Keyboard.isKeyDown(61))
                        {
                            this.refreshResources();
                        }

                        if (lvt_1_4_ == 17 && Keyboard.isKeyDown(61))
                        {
                            ;
                        }

                        if (lvt_1_4_ == 18 && Keyboard.isKeyDown(61))
                        {
                            ;
                        }

                        if (lvt_1_4_ == 47 && Keyboard.isKeyDown(61))
                        {
                            ;
                        }

                        if (lvt_1_4_ == 38 && Keyboard.isKeyDown(61))
                        {
                            ;
                        }

                        if (lvt_1_4_ == 22 && Keyboard.isKeyDown(61))
                        {
                            ;
                        }

                        if (lvt_1_4_ == 20 && Keyboard.isKeyDown(61))
                        {
                            this.refreshResources();
                        }

                        if (lvt_1_4_ == 33 && Keyboard.isKeyDown(61))
                        {
                            this.gameSettings.setOptionValue(GameSettings.Options.RENDER_DISTANCE, GuiScreen.isShiftKeyDown() ? -1 : 1);
                        }

                        if (lvt_1_4_ == 30 && Keyboard.isKeyDown(61))
                        {
                            this.renderGlobal.loadRenderers();
                        }

                        if (lvt_1_4_ == 35 && Keyboard.isKeyDown(61))
                        {
                            this.gameSettings.advancedItemTooltips = !this.gameSettings.advancedItemTooltips;
                            this.gameSettings.saveOptions();
                        }

                        if (lvt_1_4_ == 48 && Keyboard.isKeyDown(61))
                        {
                            this.renderManager.setDebugBoundingBox(!this.renderManager.isDebugBoundingBox());
                        }

                        if (lvt_1_4_ == 25 && Keyboard.isKeyDown(61))
                        {
                            this.gameSettings.pauseOnLostFocus = !this.gameSettings.pauseOnLostFocus;
                            this.gameSettings.saveOptions();
                        }

                        if (lvt_1_4_ == 59)
                        {
                            this.gameSettings.hideGUI = !this.gameSettings.hideGUI;
                        }

                        if (lvt_1_4_ == 61)
                        {
                            this.gameSettings.showDebugInfo = !this.gameSettings.showDebugInfo;
                            this.gameSettings.showDebugProfilerChart = GuiScreen.isShiftKeyDown();
                            this.gameSettings.showLagometer = GuiScreen.isAltKeyDown();
                        }

                        if (this.gameSettings.keyBindTogglePerspective.isPressed())
                        {
                            ++this.gameSettings.thirdPersonView;

                            if (this.gameSettings.thirdPersonView > 2)
                            {
                                this.gameSettings.thirdPersonView = 0;
                            }

                            if (this.gameSettings.thirdPersonView == 0)
                            {
                                this.entityRenderer.loadEntityShader(this.getRenderViewEntity());
                            }
                            else if (this.gameSettings.thirdPersonView == 1)
                            {
                                this.entityRenderer.loadEntityShader((Entity)null);
                            }

                            this.renderGlobal.setDisplayListEntitiesDirty();
                        }

                        if (this.gameSettings.keyBindSmoothCamera.isPressed())
                        {
                            this.gameSettings.smoothCamera = !this.gameSettings.smoothCamera;
                        }
                    }

                    if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart)
                    {
                        if (lvt_1_4_ == 11)
                        {
                            this.updateDebugProfilerName(0);
                        }

                        for (int lvt_2_4_ = 0; lvt_2_4_ < 9; ++lvt_2_4_)
                        {
                            if (lvt_1_4_ == 2 + lvt_2_4_)
                            {
                                this.updateDebugProfilerName(lvt_2_4_ + 1);
                            }
                        }
                    }
                }
            }

            for (int lvt_1_5_ = 0; lvt_1_5_ < 9; ++lvt_1_5_)
            {
                if (this.gameSettings.keyBindsHotbar[lvt_1_5_].isPressed())
                {
                    if (this.thePlayer.isSpectator())
                    {
                        this.ingameGUI.getSpectatorGui().func_175260_a(lvt_1_5_);
                    }
                    else
                    {
                        this.thePlayer.inventory.currentItem = lvt_1_5_;
                    }
                }
            }

            boolean lvt_1_6_ = this.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN;

            while (this.gameSettings.keyBindInventory.isPressed())
            {
                if (this.playerController.isRidingHorse())
                {
                    this.thePlayer.sendHorseInventory();
                }
                else
                {
                    this.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                    this.displayGuiScreen(new GuiInventory(this.thePlayer));
                }
            }

            while (this.gameSettings.keyBindDrop.isPressed())
            {
                if (!this.thePlayer.isSpectator())
                {
                    this.thePlayer.dropOneItem(GuiScreen.isCtrlKeyDown());
                }
            }

            while (this.gameSettings.keyBindChat.isPressed() && lvt_1_6_)
            {
                this.displayGuiScreen(new GuiChat());
            }

            if (this.currentScreen == null && this.gameSettings.keyBindCommand.isPressed() && lvt_1_6_)
            {
                this.displayGuiScreen(new GuiChat("/"));
            }

            if (this.thePlayer.isUsingItem())
            {
                if (!this.gameSettings.keyBindUseItem.isKeyDown())
                {
                    this.playerController.onStoppedUsingItem(this.thePlayer);
                }

                while (this.gameSettings.keyBindAttack.isPressed())
                {
                    ;
                }

                while (this.gameSettings.keyBindUseItem.isPressed())
                {
                    ;
                }

                while (this.gameSettings.keyBindPickBlock.isPressed())
                {
                    ;
                }
            }
            else
            {
                while (this.gameSettings.keyBindAttack.isPressed())
                {
                    this.clickMouse();
                }

                while (this.gameSettings.keyBindUseItem.isPressed())
                {
                    this.rightClickMouse();
                }

                while (this.gameSettings.keyBindPickBlock.isPressed())
                {
                    this.middleClickMouse();
                }
            }

            if (this.gameSettings.keyBindUseItem.isKeyDown() && this.rightClickDelayTimer == 0 && !this.thePlayer.isUsingItem())
            {
                this.rightClickMouse();
            }

            this.sendClickBlockToController(this.currentScreen == null && this.gameSettings.keyBindAttack.isKeyDown() && this.inGameHasFocus);
        }

        if (this.theWorld != null)
        {
            if (this.thePlayer != null)
            {
                ++this.joinPlayerCounter;

                if (this.joinPlayerCounter == 30)
                {
                    this.joinPlayerCounter = 0;
                    this.theWorld.joinEntityInSurroundings(this.thePlayer);
                }
            }

            this.mcProfiler.endStartSection("gameRenderer");

            if (!this.isGamePaused)
            {
                this.entityRenderer.updateRenderer();
            }

            this.mcProfiler.endStartSection("levelRenderer");

            if (!this.isGamePaused)
            {
                this.renderGlobal.updateClouds();
            }

            this.mcProfiler.endStartSection("level");

            if (!this.isGamePaused)
            {
                if (this.theWorld.getLastLightningBolt() > 0)
                {
                    this.theWorld.setLastLightningBolt(this.theWorld.getLastLightningBolt() - 1);
                }

                this.theWorld.updateEntities();
            }
        }
        else if (this.entityRenderer.isShaderActive())
        {
            this.entityRenderer.stopUseShader();
        }

        if (!this.isGamePaused)
        {
            this.mcMusicTicker.update();
            this.mcSoundHandler.update();
        }

        if (this.theWorld != null)
        {
            if (!this.isGamePaused)
            {
                this.theWorld.setAllowedSpawnTypes(this.theWorld.getDifficulty() != EnumDifficulty.PEACEFUL, true);

                try
                {
                    this.theWorld.tick();
                }
                catch (Throwable var8)
                {
                    CrashReport lvt_2_5_ = CrashReport.makeCrashReport(var8, "Exception in world tick");

                    if (this.theWorld == null)
                    {
                        CrashReportCategory lvt_3_3_ = lvt_2_5_.makeCategory("Affected level");
                        lvt_3_3_.addCrashSection("Problem", "Level is null!");
                    }
                    else
                    {
                        this.theWorld.addWorldInfoToCrashReport(lvt_2_5_);
                    }

                    throw new ReportedException(lvt_2_5_);
                }
            }

            this.mcProfiler.endStartSection("animateTick");

            if (!this.isGamePaused && this.theWorld != null)
            {
                this.theWorld.doVoidFogParticles(MathHelper.floor_double(this.thePlayer.posX), MathHelper.floor_double(this.thePlayer.posY), MathHelper.floor_double(this.thePlayer.posZ));
            }

            this.mcProfiler.endStartSection("particles");

            if (!this.isGamePaused)
            {
                this.effectRenderer.updateEffects();
            }
        }
        else if (this.myNetworkManager != null)
        {
            this.mcProfiler.endStartSection("pendingConnection");
            this.myNetworkManager.processReceivedPackets();
        }

        this.mcProfiler.endSection();
        this.systemTime = getSystemTime();
    }

    /**
     * Arguments: World foldername,  World ingame name, WorldSettings
     */
    public void launchIntegratedServer(String folderName, String worldName, WorldSettings worldSettingsIn)
    {
        this.loadWorld((WorldClient)null);
        System.gc();
        ISaveHandler lvt_4_1_ = this.saveLoader.getSaveLoader(folderName, false);
        WorldInfo lvt_5_1_ = lvt_4_1_.loadWorldInfo();

        if (lvt_5_1_ == null && worldSettingsIn != null)
        {
            lvt_5_1_ = new WorldInfo(worldSettingsIn, folderName);
            lvt_4_1_.saveWorldInfo(lvt_5_1_);
        }

        if (worldSettingsIn == null)
        {
            worldSettingsIn = new WorldSettings(lvt_5_1_);
        }

        try
        {
            this.theIntegratedServer = new IntegratedServer(this, folderName, worldName, worldSettingsIn);
            this.theIntegratedServer.startServerThread();
            this.integratedServerIsRunning = true;
        }
        catch (Throwable var10)
        {
            CrashReport lvt_7_1_ = CrashReport.makeCrashReport(var10, "Starting integrated server");
            CrashReportCategory lvt_8_1_ = lvt_7_1_.makeCategory("Starting integrated server");
            lvt_8_1_.addCrashSection("Level ID", folderName);
            lvt_8_1_.addCrashSection("Level Name", worldName);
            throw new ReportedException(lvt_7_1_);
        }

        this.loadingScreen.displaySavingString(I18n.format("menu.loadingLevel", new Object[0]));

        while (!this.theIntegratedServer.serverIsInRunLoop())
        {
            String lvt_6_2_ = this.theIntegratedServer.getUserMessage();

            if (lvt_6_2_ != null)
            {
                this.loadingScreen.displayLoadingString(I18n.format(lvt_6_2_, new Object[0]));
            }
            else
            {
                this.loadingScreen.displayLoadingString("");
            }

            try
            {
                Thread.sleep(200L);
            }
            catch (InterruptedException var9)
            {
                ;
            }
        }

        this.displayGuiScreen((GuiScreen)null);
        SocketAddress lvt_6_3_ = this.theIntegratedServer.getNetworkSystem().addLocalEndpoint();
        NetworkManager lvt_7_2_ = NetworkManager.provideLocalClient(lvt_6_3_);
        lvt_7_2_.setNetHandler(new NetHandlerLoginClient(lvt_7_2_, this, (GuiScreen)null));
        lvt_7_2_.sendPacket(new C00Handshake(47, lvt_6_3_.toString(), 0, EnumConnectionState.LOGIN));
        lvt_7_2_.sendPacket(new C00PacketLoginStart(this.getSession().getProfile()));
        this.myNetworkManager = lvt_7_2_;
    }

    /**
     * unloads the current world first
     */
    public void loadWorld(WorldClient worldClientIn)
    {
        this.loadWorld(worldClientIn, "");
    }

    /**
     * par2Str is displayed on the loading screen to the user unloads the current world first
     */
    public void loadWorld(WorldClient worldClientIn, String loadingMessage)
    {
        if (worldClientIn == null)
        {
            NetHandlerPlayClient lvt_3_1_ = this.getNetHandler();

            if (lvt_3_1_ != null)
            {
                lvt_3_1_.cleanup();
            }

            if (this.theIntegratedServer != null && this.theIntegratedServer.isAnvilFileSet())
            {
                this.theIntegratedServer.initiateShutdown();
                this.theIntegratedServer.setStaticInstance();
            }

            this.theIntegratedServer = null;
            this.guiAchievement.clearAchievements();
            this.entityRenderer.getMapItemRenderer().clearLoadedMaps();
        }

        this.renderViewEntity = null;
        this.myNetworkManager = null;

        if (this.loadingScreen != null)
        {
            this.loadingScreen.resetProgressAndMessage(loadingMessage);
            this.loadingScreen.displayLoadingString("");
        }

        if (worldClientIn == null && this.theWorld != null)
        {
            this.mcResourcePackRepository.clearResourcePack();
            this.ingameGUI.resetPlayersOverlayFooterHeader();
            this.setServerData((ServerData)null);
            this.integratedServerIsRunning = false;
        }

        this.mcSoundHandler.stopSounds();
        this.theWorld = worldClientIn;

        if (worldClientIn != null)
        {
            if (this.renderGlobal != null)
            {
                this.renderGlobal.setWorldAndLoadRenderers(worldClientIn);
            }

            if (this.effectRenderer != null)
            {
                this.effectRenderer.clearEffects(worldClientIn);
            }

            if (this.thePlayer == null)
            {
                this.thePlayer = this.playerController.func_178892_a(worldClientIn, new StatFileWriter());
                this.playerController.flipPlayer(this.thePlayer);
            }

            this.thePlayer.preparePlayerToSpawn();
            worldClientIn.spawnEntityInWorld(this.thePlayer);
            this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
            this.playerController.setPlayerCapabilities(this.thePlayer);
            this.renderViewEntity = this.thePlayer;
        }
        else
        {
            this.saveLoader.flushCache();
            this.thePlayer = null;
        }

        System.gc();
        this.systemTime = 0L;
    }

    public void setDimensionAndSpawnPlayer(int dimension)
    {
        this.theWorld.setInitialSpawnLocation();
        this.theWorld.removeAllEntities();
        int lvt_2_1_ = 0;
        String lvt_3_1_ = null;

        if (this.thePlayer != null)
        {
            lvt_2_1_ = this.thePlayer.getEntityId();
            this.theWorld.removeEntity(this.thePlayer);
            lvt_3_1_ = this.thePlayer.getClientBrand();
        }

        this.renderViewEntity = null;
        EntityPlayerSP lvt_4_1_ = this.thePlayer;
        this.thePlayer = this.playerController.func_178892_a(this.theWorld, this.thePlayer == null ? new StatFileWriter() : this.thePlayer.getStatFileWriter());
        this.thePlayer.getDataWatcher().updateWatchedObjectsFromList(lvt_4_1_.getDataWatcher().getAllWatched());
        this.thePlayer.dimension = dimension;
        this.renderViewEntity = this.thePlayer;
        this.thePlayer.preparePlayerToSpawn();
        this.thePlayer.setClientBrand(lvt_3_1_);
        this.theWorld.spawnEntityInWorld(this.thePlayer);
        this.playerController.flipPlayer(this.thePlayer);
        this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
        this.thePlayer.setEntityId(lvt_2_1_);
        this.playerController.setPlayerCapabilities(this.thePlayer);
        this.thePlayer.setReducedDebug(lvt_4_1_.hasReducedDebug());

        if (this.currentScreen instanceof GuiGameOver)
        {
            this.displayGuiScreen((GuiScreen)null);
        }
    }

    /**
     * Gets whether this is a demo or not.
     */
    public final boolean isDemo()
    {
        return this.isDemo;
    }

    public NetHandlerPlayClient getNetHandler()
    {
        return this.thePlayer != null ? this.thePlayer.sendQueue : null;
    }

    public static boolean isGuiEnabled()
    {
        return theMinecraft == null || !theMinecraft.gameSettings.hideGUI;
    }

    public static boolean isFancyGraphicsEnabled()
    {
        return theMinecraft != null && theMinecraft.gameSettings.fancyGraphics;
    }

    /**
     * Returns if ambient occlusion is enabled
     */
    public static boolean isAmbientOcclusionEnabled()
    {
        return theMinecraft != null && theMinecraft.gameSettings.ambientOcclusion != 0;
    }

    /**
     * Called when user clicked he's mouse middle button (pick block)
     */
    private void middleClickMouse()
    {
        if (this.objectMouseOver != null)
        {
            boolean lvt_1_1_ = this.thePlayer.capabilities.isCreativeMode;
            int lvt_3_1_ = 0;
            boolean lvt_4_1_ = false;
            TileEntity lvt_5_1_ = null;
            Item lvt_2_1_;

            if (this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            {
                BlockPos lvt_6_1_ = this.objectMouseOver.getBlockPos();
                Block lvt_7_1_ = this.theWorld.getBlockState(lvt_6_1_).getBlock();

                if (lvt_7_1_.getMaterial() == Material.air)
                {
                    return;
                }

                lvt_2_1_ = lvt_7_1_.getItem(this.theWorld, lvt_6_1_);

                if (lvt_2_1_ == null)
                {
                    return;
                }

                if (lvt_1_1_ && GuiScreen.isCtrlKeyDown())
                {
                    lvt_5_1_ = this.theWorld.getTileEntity(lvt_6_1_);
                }

                Block lvt_8_1_ = lvt_2_1_ instanceof ItemBlock && !lvt_7_1_.isFlowerPot() ? Block.getBlockFromItem(lvt_2_1_) : lvt_7_1_;
                lvt_3_1_ = lvt_8_1_.getDamageValue(this.theWorld, lvt_6_1_);
                lvt_4_1_ = lvt_2_1_.getHasSubtypes();
            }
            else
            {
                if (this.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY || this.objectMouseOver.entityHit == null || !lvt_1_1_)
                {
                    return;
                }

                if (this.objectMouseOver.entityHit instanceof EntityPainting)
                {
                    lvt_2_1_ = Items.painting;
                }
                else if (this.objectMouseOver.entityHit instanceof EntityLeashKnot)
                {
                    lvt_2_1_ = Items.lead;
                }
                else if (this.objectMouseOver.entityHit instanceof EntityItemFrame)
                {
                    EntityItemFrame lvt_6_2_ = (EntityItemFrame)this.objectMouseOver.entityHit;
                    ItemStack lvt_7_2_ = lvt_6_2_.getDisplayedItem();

                    if (lvt_7_2_ == null)
                    {
                        lvt_2_1_ = Items.item_frame;
                    }
                    else
                    {
                        lvt_2_1_ = lvt_7_2_.getItem();
                        lvt_3_1_ = lvt_7_2_.getMetadata();
                        lvt_4_1_ = true;
                    }
                }
                else if (this.objectMouseOver.entityHit instanceof EntityMinecart)
                {
                    EntityMinecart lvt_6_3_ = (EntityMinecart)this.objectMouseOver.entityHit;

                    switch (lvt_6_3_.getMinecartType())
                    {
                        case FURNACE:
                            lvt_2_1_ = Items.furnace_minecart;
                            break;

                        case CHEST:
                            lvt_2_1_ = Items.chest_minecart;
                            break;

                        case TNT:
                            lvt_2_1_ = Items.tnt_minecart;
                            break;

                        case HOPPER:
                            lvt_2_1_ = Items.hopper_minecart;
                            break;

                        case COMMAND_BLOCK:
                            lvt_2_1_ = Items.command_block_minecart;
                            break;

                        default:
                            lvt_2_1_ = Items.minecart;
                    }
                }
                else if (this.objectMouseOver.entityHit instanceof EntityBoat)
                {
                    lvt_2_1_ = Items.boat;
                }
                else if (this.objectMouseOver.entityHit instanceof EntityArmorStand)
                {
                    lvt_2_1_ = Items.armor_stand;
                }
                else
                {
                    lvt_2_1_ = Items.spawn_egg;
                    lvt_3_1_ = EntityList.getEntityID(this.objectMouseOver.entityHit);
                    lvt_4_1_ = true;

                    if (!EntityList.entityEggs.containsKey(Integer.valueOf(lvt_3_1_)))
                    {
                        return;
                    }
                }
            }

            InventoryPlayer lvt_6_4_ = this.thePlayer.inventory;

            if (lvt_5_1_ == null)
            {
                lvt_6_4_.setCurrentItem(lvt_2_1_, lvt_3_1_, lvt_4_1_, lvt_1_1_);
            }
            else
            {
                ItemStack lvt_7_3_ = this.pickBlockWithNBT(lvt_2_1_, lvt_3_1_, lvt_5_1_);
                lvt_6_4_.setInventorySlotContents(lvt_6_4_.currentItem, lvt_7_3_);
            }

            if (lvt_1_1_)
            {
                int lvt_7_4_ = this.thePlayer.inventoryContainer.inventorySlots.size() - 9 + lvt_6_4_.currentItem;
                this.playerController.sendSlotPacket(lvt_6_4_.getStackInSlot(lvt_6_4_.currentItem), lvt_7_4_);
            }
        }
    }

    /**
     * Return an ItemStack with the NBTTag of the TileEntity ("Owner" if the block is a skull)
     *  
     * @param itemIn The item from the block picked
     * @param meta Metadata of the item
     * @param tileEntityIn TileEntity of the block picked
     */
    private ItemStack pickBlockWithNBT(Item itemIn, int meta, TileEntity tileEntityIn)
    {
        ItemStack lvt_4_1_ = new ItemStack(itemIn, 1, meta);
        NBTTagCompound lvt_5_1_ = new NBTTagCompound();
        tileEntityIn.writeToNBT(lvt_5_1_);

        if (itemIn == Items.skull && lvt_5_1_.hasKey("Owner"))
        {
            NBTTagCompound lvt_6_1_ = lvt_5_1_.getCompoundTag("Owner");
            NBTTagCompound lvt_7_1_ = new NBTTagCompound();
            lvt_7_1_.setTag("SkullOwner", lvt_6_1_);
            lvt_4_1_.setTagCompound(lvt_7_1_);
            return lvt_4_1_;
        }
        else
        {
            lvt_4_1_.setTagInfo("BlockEntityTag", lvt_5_1_);
            NBTTagCompound lvt_6_2_ = new NBTTagCompound();
            NBTTagList lvt_7_2_ = new NBTTagList();
            lvt_7_2_.appendTag(new NBTTagString("(+NBT)"));
            lvt_6_2_.setTag("Lore", lvt_7_2_);
            lvt_4_1_.setTagInfo("display", lvt_6_2_);
            return lvt_4_1_;
        }
    }

    /**
     * adds core server Info (GL version , Texture pack, isModded, type), and the worldInfo to the crash report
     */
    public CrashReport addGraphicsAndWorldToCrashReport(CrashReport theCrash)
    {
        theCrash.getCategory().addCrashSectionCallable("Launched Version", new Callable<String>()
        {
            public String call() throws Exception
            {
                return Minecraft.this.launchedVersion;
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        theCrash.getCategory().addCrashSectionCallable("LWJGL", new Callable<String>()
        {
            public String call()
            {
                return Sys.getVersion();
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        theCrash.getCategory().addCrashSectionCallable("OpenGL", new Callable<String>()
        {
            public String call()
            {
                return GL11.glGetString(GL11.GL_RENDERER) + " GL version " + GL11.glGetString(GL11.GL_VERSION) + ", " + GL11.glGetString(GL11.GL_VENDOR);
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        theCrash.getCategory().addCrashSectionCallable("GL Caps", new Callable<String>()
        {
            public String call()
            {
                return OpenGlHelper.getLogText();
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Using VBOs", new Callable<String>()
        {
            public String call()
            {
                return Minecraft.this.gameSettings.useVbo ? "Yes" : "No";
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Is Modded", new Callable<String>()
        {
            public String call() throws Exception
            {
                String lvt_1_1_ = ClientBrandRetriever.getClientModName();
                return !lvt_1_1_.equals("vanilla") ? "Definitely; Client brand changed to \'" + lvt_1_1_ + "\'" : (Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and client brand is untouched.");
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Type", new Callable<String>()
        {
            public String call() throws Exception
            {
                return "Client (map_client.txt)";
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Resource Packs", new Callable<String>()
        {
            public String call() throws Exception
            {
                StringBuilder lvt_1_1_ = new StringBuilder();

                for (String lvt_3_1_ : Minecraft.this.gameSettings.resourcePacks)
                {
                    if (lvt_1_1_.length() > 0)
                    {
                        lvt_1_1_.append(", ");
                    }

                    lvt_1_1_.append(lvt_3_1_);

                    if (Minecraft.this.gameSettings.incompatibleResourcePacks.contains(lvt_3_1_))
                    {
                        lvt_1_1_.append(" (incompatible)");
                    }
                }

                return lvt_1_1_.toString();
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Current Language", new Callable<String>()
        {
            public String call() throws Exception
            {
                return Minecraft.this.mcLanguageManager.getCurrentLanguage().toString();
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Profiler Position", new Callable<String>()
        {
            public String call() throws Exception
            {
                return Minecraft.this.mcProfiler.profilingEnabled ? Minecraft.this.mcProfiler.getNameOfLastSection() : "N/A (disabled)";
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        theCrash.getCategory().addCrashSectionCallable("CPU", new Callable<String>()
        {
            public String call()
            {
                return OpenGlHelper.getCpu();
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });

        if (this.theWorld != null)
        {
            this.theWorld.addWorldInfoToCrashReport(theCrash);
        }

        return theCrash;
    }

    /**
     * Return the singleton Minecraft instance for the game
     */
    public static Minecraft getMinecraft()
    {
        return theMinecraft;
    }

    public ListenableFuture<Object> scheduleResourcesRefresh()
    {
        return this.addScheduledTask(new Runnable()
        {
            public void run()
            {
                Minecraft.this.refreshResources();
            }
        });
    }

    public void addServerStatsToSnooper(PlayerUsageSnooper playerSnooper)
    {
        playerSnooper.addClientStat("fps", Integer.valueOf(debugFPS));
        playerSnooper.addClientStat("vsync_enabled", Boolean.valueOf(this.gameSettings.enableVsync));
        playerSnooper.addClientStat("display_frequency", Integer.valueOf(Display.getDisplayMode().getFrequency()));
        playerSnooper.addClientStat("display_type", this.fullscreen ? "fullscreen" : "windowed");
        playerSnooper.addClientStat("run_time", Long.valueOf((MinecraftServer.getCurrentTimeMillis() - playerSnooper.getMinecraftStartTimeMillis()) / 60L * 1000L));
        playerSnooper.addClientStat("current_action", this.getCurrentAction());
        String lvt_2_1_ = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
        playerSnooper.addClientStat("endianness", lvt_2_1_);
        playerSnooper.addClientStat("resource_packs", Integer.valueOf(this.mcResourcePackRepository.getRepositoryEntries().size()));
        int lvt_3_1_ = 0;

        for (ResourcePackRepository.Entry lvt_5_1_ : this.mcResourcePackRepository.getRepositoryEntries())
        {
            playerSnooper.addClientStat("resource_pack[" + lvt_3_1_++ + "]", lvt_5_1_.getResourcePackName());
        }

        if (this.theIntegratedServer != null && this.theIntegratedServer.getPlayerUsageSnooper() != null)
        {
            playerSnooper.addClientStat("snooper_partner", this.theIntegratedServer.getPlayerUsageSnooper().getUniqueID());
        }
    }

    /**
     * Return the current action's name
     */
    private String getCurrentAction()
    {
        return this.theIntegratedServer != null ? (this.theIntegratedServer.getPublic() ? "hosting_lan" : "singleplayer") : (this.currentServerData != null ? (this.currentServerData.isOnLAN() ? "playing_lan" : "multiplayer") : "out_of_game");
    }

    public void addServerTypeToSnooper(PlayerUsageSnooper playerSnooper)
    {
        playerSnooper.addStatToSnooper("opengl_version", GL11.glGetString(GL11.GL_VERSION));
        playerSnooper.addStatToSnooper("opengl_vendor", GL11.glGetString(GL11.GL_VENDOR));
        playerSnooper.addStatToSnooper("client_brand", ClientBrandRetriever.getClientModName());
        playerSnooper.addStatToSnooper("launched_version", this.launchedVersion);
        ContextCapabilities lvt_2_1_ = GLContext.getCapabilities();
        playerSnooper.addStatToSnooper("gl_caps[ARB_arrays_of_arrays]", Boolean.valueOf(lvt_2_1_.GL_ARB_arrays_of_arrays));
        playerSnooper.addStatToSnooper("gl_caps[ARB_base_instance]", Boolean.valueOf(lvt_2_1_.GL_ARB_base_instance));
        playerSnooper.addStatToSnooper("gl_caps[ARB_blend_func_extended]", Boolean.valueOf(lvt_2_1_.GL_ARB_blend_func_extended));
        playerSnooper.addStatToSnooper("gl_caps[ARB_clear_buffer_object]", Boolean.valueOf(lvt_2_1_.GL_ARB_clear_buffer_object));
        playerSnooper.addStatToSnooper("gl_caps[ARB_color_buffer_float]", Boolean.valueOf(lvt_2_1_.GL_ARB_color_buffer_float));
        playerSnooper.addStatToSnooper("gl_caps[ARB_compatibility]", Boolean.valueOf(lvt_2_1_.GL_ARB_compatibility));
        playerSnooper.addStatToSnooper("gl_caps[ARB_compressed_texture_pixel_storage]", Boolean.valueOf(lvt_2_1_.GL_ARB_compressed_texture_pixel_storage));
        playerSnooper.addStatToSnooper("gl_caps[ARB_compute_shader]", Boolean.valueOf(lvt_2_1_.GL_ARB_compute_shader));
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_buffer]", Boolean.valueOf(lvt_2_1_.GL_ARB_copy_buffer));
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_image]", Boolean.valueOf(lvt_2_1_.GL_ARB_copy_image));
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_buffer_float]", Boolean.valueOf(lvt_2_1_.GL_ARB_depth_buffer_float));
        playerSnooper.addStatToSnooper("gl_caps[ARB_compute_shader]", Boolean.valueOf(lvt_2_1_.GL_ARB_compute_shader));
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_buffer]", Boolean.valueOf(lvt_2_1_.GL_ARB_copy_buffer));
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_image]", Boolean.valueOf(lvt_2_1_.GL_ARB_copy_image));
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_buffer_float]", Boolean.valueOf(lvt_2_1_.GL_ARB_depth_buffer_float));
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_clamp]", Boolean.valueOf(lvt_2_1_.GL_ARB_depth_clamp));
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_texture]", Boolean.valueOf(lvt_2_1_.GL_ARB_depth_texture));
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_buffers]", Boolean.valueOf(lvt_2_1_.GL_ARB_draw_buffers));
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_buffers_blend]", Boolean.valueOf(lvt_2_1_.GL_ARB_draw_buffers_blend));
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_elements_base_vertex]", Boolean.valueOf(lvt_2_1_.GL_ARB_draw_elements_base_vertex));
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_indirect]", Boolean.valueOf(lvt_2_1_.GL_ARB_draw_indirect));
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_instanced]", Boolean.valueOf(lvt_2_1_.GL_ARB_draw_instanced));
        playerSnooper.addStatToSnooper("gl_caps[ARB_explicit_attrib_location]", Boolean.valueOf(lvt_2_1_.GL_ARB_explicit_attrib_location));
        playerSnooper.addStatToSnooper("gl_caps[ARB_explicit_uniform_location]", Boolean.valueOf(lvt_2_1_.GL_ARB_explicit_uniform_location));
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_layer_viewport]", Boolean.valueOf(lvt_2_1_.GL_ARB_fragment_layer_viewport));
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_program]", Boolean.valueOf(lvt_2_1_.GL_ARB_fragment_program));
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_shader]", Boolean.valueOf(lvt_2_1_.GL_ARB_fragment_shader));
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_program_shadow]", Boolean.valueOf(lvt_2_1_.GL_ARB_fragment_program_shadow));
        playerSnooper.addStatToSnooper("gl_caps[ARB_framebuffer_object]", Boolean.valueOf(lvt_2_1_.GL_ARB_framebuffer_object));
        playerSnooper.addStatToSnooper("gl_caps[ARB_framebuffer_sRGB]", Boolean.valueOf(lvt_2_1_.GL_ARB_framebuffer_sRGB));
        playerSnooper.addStatToSnooper("gl_caps[ARB_geometry_shader4]", Boolean.valueOf(lvt_2_1_.GL_ARB_geometry_shader4));
        playerSnooper.addStatToSnooper("gl_caps[ARB_gpu_shader5]", Boolean.valueOf(lvt_2_1_.GL_ARB_gpu_shader5));
        playerSnooper.addStatToSnooper("gl_caps[ARB_half_float_pixel]", Boolean.valueOf(lvt_2_1_.GL_ARB_half_float_pixel));
        playerSnooper.addStatToSnooper("gl_caps[ARB_half_float_vertex]", Boolean.valueOf(lvt_2_1_.GL_ARB_half_float_vertex));
        playerSnooper.addStatToSnooper("gl_caps[ARB_instanced_arrays]", Boolean.valueOf(lvt_2_1_.GL_ARB_instanced_arrays));
        playerSnooper.addStatToSnooper("gl_caps[ARB_map_buffer_alignment]", Boolean.valueOf(lvt_2_1_.GL_ARB_map_buffer_alignment));
        playerSnooper.addStatToSnooper("gl_caps[ARB_map_buffer_range]", Boolean.valueOf(lvt_2_1_.GL_ARB_map_buffer_range));
        playerSnooper.addStatToSnooper("gl_caps[ARB_multisample]", Boolean.valueOf(lvt_2_1_.GL_ARB_multisample));
        playerSnooper.addStatToSnooper("gl_caps[ARB_multitexture]", Boolean.valueOf(lvt_2_1_.GL_ARB_multitexture));
        playerSnooper.addStatToSnooper("gl_caps[ARB_occlusion_query2]", Boolean.valueOf(lvt_2_1_.GL_ARB_occlusion_query2));
        playerSnooper.addStatToSnooper("gl_caps[ARB_pixel_buffer_object]", Boolean.valueOf(lvt_2_1_.GL_ARB_pixel_buffer_object));
        playerSnooper.addStatToSnooper("gl_caps[ARB_seamless_cube_map]", Boolean.valueOf(lvt_2_1_.GL_ARB_seamless_cube_map));
        playerSnooper.addStatToSnooper("gl_caps[ARB_shader_objects]", Boolean.valueOf(lvt_2_1_.GL_ARB_shader_objects));
        playerSnooper.addStatToSnooper("gl_caps[ARB_shader_stencil_export]", Boolean.valueOf(lvt_2_1_.GL_ARB_shader_stencil_export));
        playerSnooper.addStatToSnooper("gl_caps[ARB_shader_texture_lod]", Boolean.valueOf(lvt_2_1_.GL_ARB_shader_texture_lod));
        playerSnooper.addStatToSnooper("gl_caps[ARB_shadow]", Boolean.valueOf(lvt_2_1_.GL_ARB_shadow));
        playerSnooper.addStatToSnooper("gl_caps[ARB_shadow_ambient]", Boolean.valueOf(lvt_2_1_.GL_ARB_shadow_ambient));
        playerSnooper.addStatToSnooper("gl_caps[ARB_stencil_texturing]", Boolean.valueOf(lvt_2_1_.GL_ARB_stencil_texturing));
        playerSnooper.addStatToSnooper("gl_caps[ARB_sync]", Boolean.valueOf(lvt_2_1_.GL_ARB_sync));
        playerSnooper.addStatToSnooper("gl_caps[ARB_tessellation_shader]", Boolean.valueOf(lvt_2_1_.GL_ARB_tessellation_shader));
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_border_clamp]", Boolean.valueOf(lvt_2_1_.GL_ARB_texture_border_clamp));
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_buffer_object]", Boolean.valueOf(lvt_2_1_.GL_ARB_texture_buffer_object));
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_cube_map]", Boolean.valueOf(lvt_2_1_.GL_ARB_texture_cube_map));
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_cube_map_array]", Boolean.valueOf(lvt_2_1_.GL_ARB_texture_cube_map_array));
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_non_power_of_two]", Boolean.valueOf(lvt_2_1_.GL_ARB_texture_non_power_of_two));
        playerSnooper.addStatToSnooper("gl_caps[ARB_uniform_buffer_object]", Boolean.valueOf(lvt_2_1_.GL_ARB_uniform_buffer_object));
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_blend]", Boolean.valueOf(lvt_2_1_.GL_ARB_vertex_blend));
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_buffer_object]", Boolean.valueOf(lvt_2_1_.GL_ARB_vertex_buffer_object));
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_program]", Boolean.valueOf(lvt_2_1_.GL_ARB_vertex_program));
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_shader]", Boolean.valueOf(lvt_2_1_.GL_ARB_vertex_shader));
        playerSnooper.addStatToSnooper("gl_caps[EXT_bindable_uniform]", Boolean.valueOf(lvt_2_1_.GL_EXT_bindable_uniform));
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_equation_separate]", Boolean.valueOf(lvt_2_1_.GL_EXT_blend_equation_separate));
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_func_separate]", Boolean.valueOf(lvt_2_1_.GL_EXT_blend_func_separate));
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_minmax]", Boolean.valueOf(lvt_2_1_.GL_EXT_blend_minmax));
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_subtract]", Boolean.valueOf(lvt_2_1_.GL_EXT_blend_subtract));
        playerSnooper.addStatToSnooper("gl_caps[EXT_draw_instanced]", Boolean.valueOf(lvt_2_1_.GL_EXT_draw_instanced));
        playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_multisample]", Boolean.valueOf(lvt_2_1_.GL_EXT_framebuffer_multisample));
        playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_object]", Boolean.valueOf(lvt_2_1_.GL_EXT_framebuffer_object));
        playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_sRGB]", Boolean.valueOf(lvt_2_1_.GL_EXT_framebuffer_sRGB));
        playerSnooper.addStatToSnooper("gl_caps[EXT_geometry_shader4]", Boolean.valueOf(lvt_2_1_.GL_EXT_geometry_shader4));
        playerSnooper.addStatToSnooper("gl_caps[EXT_gpu_program_parameters]", Boolean.valueOf(lvt_2_1_.GL_EXT_gpu_program_parameters));
        playerSnooper.addStatToSnooper("gl_caps[EXT_gpu_shader4]", Boolean.valueOf(lvt_2_1_.GL_EXT_gpu_shader4));
        playerSnooper.addStatToSnooper("gl_caps[EXT_multi_draw_arrays]", Boolean.valueOf(lvt_2_1_.GL_EXT_multi_draw_arrays));
        playerSnooper.addStatToSnooper("gl_caps[EXT_packed_depth_stencil]", Boolean.valueOf(lvt_2_1_.GL_EXT_packed_depth_stencil));
        playerSnooper.addStatToSnooper("gl_caps[EXT_paletted_texture]", Boolean.valueOf(lvt_2_1_.GL_EXT_paletted_texture));
        playerSnooper.addStatToSnooper("gl_caps[EXT_rescale_normal]", Boolean.valueOf(lvt_2_1_.GL_EXT_rescale_normal));
        playerSnooper.addStatToSnooper("gl_caps[EXT_separate_shader_objects]", Boolean.valueOf(lvt_2_1_.GL_EXT_separate_shader_objects));
        playerSnooper.addStatToSnooper("gl_caps[EXT_shader_image_load_store]", Boolean.valueOf(lvt_2_1_.GL_EXT_shader_image_load_store));
        playerSnooper.addStatToSnooper("gl_caps[EXT_shadow_funcs]", Boolean.valueOf(lvt_2_1_.GL_EXT_shadow_funcs));
        playerSnooper.addStatToSnooper("gl_caps[EXT_shared_texture_palette]", Boolean.valueOf(lvt_2_1_.GL_EXT_shared_texture_palette));
        playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_clear_tag]", Boolean.valueOf(lvt_2_1_.GL_EXT_stencil_clear_tag));
        playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_two_side]", Boolean.valueOf(lvt_2_1_.GL_EXT_stencil_two_side));
        playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_wrap]", Boolean.valueOf(lvt_2_1_.GL_EXT_stencil_wrap));
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_3d]", Boolean.valueOf(lvt_2_1_.GL_EXT_texture_3d));
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_array]", Boolean.valueOf(lvt_2_1_.GL_EXT_texture_array));
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_buffer_object]", Boolean.valueOf(lvt_2_1_.GL_EXT_texture_buffer_object));
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_integer]", Boolean.valueOf(lvt_2_1_.GL_EXT_texture_integer));
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_lod_bias]", Boolean.valueOf(lvt_2_1_.GL_EXT_texture_lod_bias));
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_sRGB]", Boolean.valueOf(lvt_2_1_.GL_EXT_texture_sRGB));
        playerSnooper.addStatToSnooper("gl_caps[EXT_vertex_shader]", Boolean.valueOf(lvt_2_1_.GL_EXT_vertex_shader));
        playerSnooper.addStatToSnooper("gl_caps[EXT_vertex_weighting]", Boolean.valueOf(lvt_2_1_.GL_EXT_vertex_weighting));
        playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_uniforms]", Integer.valueOf(GL11.glGetInteger(GL20.GL_MAX_VERTEX_UNIFORM_COMPONENTS)));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_fragment_uniforms]", Integer.valueOf(GL11.glGetInteger(GL20.GL_MAX_FRAGMENT_UNIFORM_COMPONENTS)));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_attribs]", Integer.valueOf(GL11.glGetInteger(GL20.GL_MAX_VERTEX_ATTRIBS)));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_texture_image_units]", Integer.valueOf(GL11.glGetInteger(GL20.GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS)));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_texture_image_units]", Integer.valueOf(GL11.glGetInteger(GL20.GL_MAX_TEXTURE_IMAGE_UNITS)));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_texture_image_units]", Integer.valueOf(GL11.glGetInteger(35071)));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_max_texture_size", Integer.valueOf(getGLMaximumTextureSize()));
    }

    /**
     * Used in the usage snooper.
     */
    public static int getGLMaximumTextureSize()
    {
        for (int lvt_0_1_ = 16384; lvt_0_1_ > 0; lvt_0_1_ >>= 1)
        {
            GL11.glTexImage2D(GL11.GL_PROXY_TEXTURE_2D, 0, GL11.GL_RGBA, lvt_0_1_, lvt_0_1_, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
            int lvt_1_1_ = GL11.glGetTexLevelParameteri(GL11.GL_PROXY_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);

            if (lvt_1_1_ != 0)
            {
                return lvt_0_1_;
            }
        }

        return -1;
    }

    /**
     * Returns whether snooping is enabled or not.
     */
    public boolean isSnooperEnabled()
    {
        return this.gameSettings.snooperEnabled;
    }

    /**
     * Set the current ServerData instance.
     */
    public void setServerData(ServerData serverDataIn)
    {
        this.currentServerData = serverDataIn;
    }

    public ServerData getCurrentServerData()
    {
        return this.currentServerData;
    }

    public boolean isIntegratedServerRunning()
    {
        return this.integratedServerIsRunning;
    }

    /**
     * Returns true if there is only one player playing, and the current server is the integrated one.
     */
    public boolean isSingleplayer()
    {
        return this.integratedServerIsRunning && this.theIntegratedServer != null;
    }

    /**
     * Returns the currently running integrated server
     */
    public IntegratedServer getIntegratedServer()
    {
        return this.theIntegratedServer;
    }

    public static void stopIntegratedServer()
    {
        if (theMinecraft != null)
        {
            IntegratedServer lvt_0_1_ = theMinecraft.getIntegratedServer();

            if (lvt_0_1_ != null)
            {
                lvt_0_1_.stopServer();
            }
        }
    }

    /**
     * Returns the PlayerUsageSnooper instance.
     */
    public PlayerUsageSnooper getPlayerUsageSnooper()
    {
        return this.usageSnooper;
    }

    /**
     * Gets the system time in milliseconds.
     */
    public static long getSystemTime()
    {
        return Sys.getTime() * 1000L / Sys.getTimerResolution();
    }

    /**
     * Returns whether we're in full screen or not.
     */
    public boolean isFullScreen()
    {
        return this.fullscreen;
    }

    public Session getSession()
    {
        return this.session;
    }

    public PropertyMap getTwitchDetails()
    {
        return this.twitchDetails;
    }

    /**
     * Return the player's GameProfile properties
     */
    public PropertyMap getProfileProperties()
    {
        if (this.profileProperties.isEmpty())
        {
            GameProfile lvt_1_1_ = this.getSessionService().fillProfileProperties(this.session.getProfile(), false);
            this.profileProperties.putAll(lvt_1_1_.getProperties());
        }

        return this.profileProperties;
    }

    public Proxy getProxy()
    {
        return this.proxy;
    }

    public TextureManager getTextureManager()
    {
        return this.renderEngine;
    }

    public IResourceManager getResourceManager()
    {
        return this.mcResourceManager;
    }

    public ResourcePackRepository getResourcePackRepository()
    {
        return this.mcResourcePackRepository;
    }

    public LanguageManager getLanguageManager()
    {
        return this.mcLanguageManager;
    }

    public TextureMap getTextureMapBlocks()
    {
        return this.textureMapBlocks;
    }

    public boolean isJava64bit()
    {
        return this.jvm64bit;
    }

    public boolean isGamePaused()
    {
        return this.isGamePaused;
    }

    public SoundHandler getSoundHandler()
    {
        return this.mcSoundHandler;
    }

    public MusicTicker.MusicType getAmbientMusicType()
    {
        return this.thePlayer != null ? (this.thePlayer.worldObj.provider instanceof WorldProviderHell ? MusicTicker.MusicType.NETHER : (this.thePlayer.worldObj.provider instanceof WorldProviderEnd ? (BossStatus.bossName != null && BossStatus.statusBarTime > 0 ? MusicTicker.MusicType.END_BOSS : MusicTicker.MusicType.END) : (this.thePlayer.capabilities.isCreativeMode && this.thePlayer.capabilities.allowFlying ? MusicTicker.MusicType.CREATIVE : MusicTicker.MusicType.GAME))) : MusicTicker.MusicType.MENU;
    }

    public IStream getTwitchStream()
    {
        return this.stream;
    }

    public void dispatchKeypresses()
    {
        int lvt_1_1_ = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() : Keyboard.getEventKey();

        if (lvt_1_1_ != 0 && !Keyboard.isRepeatEvent())
        {
            if (!(this.currentScreen instanceof GuiControls) || ((GuiControls)this.currentScreen).time <= getSystemTime() - 20L)
            {
                if (Keyboard.getEventKeyState())
                {
                    if (lvt_1_1_ == this.gameSettings.keyBindStreamStartStop.getKeyCode())
                    {
                        if (this.getTwitchStream().isBroadcasting())
                        {
                            this.getTwitchStream().stopBroadcasting();
                        }
                        else if (this.getTwitchStream().isReadyToBroadcast())
                        {
                            this.displayGuiScreen(new GuiYesNo(new GuiYesNoCallback()
                            {
                                public void confirmClicked(boolean result, int id)
                                {
                                    if (result)
                                    {
                                        Minecraft.this.getTwitchStream().func_152930_t();
                                    }

                                    Minecraft.this.displayGuiScreen((GuiScreen)null);
                                }
                            }, I18n.format("stream.confirm_start", new Object[0]), "", 0));
                        }
                        else if (this.getTwitchStream().func_152928_D() && this.getTwitchStream().func_152936_l())
                        {
                            if (this.theWorld != null)
                            {
                                this.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("Not ready to start streaming yet!"));
                            }
                        }
                        else
                        {
                            GuiStreamUnavailable.func_152321_a(this.currentScreen);
                        }
                    }
                    else if (lvt_1_1_ == this.gameSettings.keyBindStreamPauseUnpause.getKeyCode())
                    {
                        if (this.getTwitchStream().isBroadcasting())
                        {
                            if (this.getTwitchStream().isPaused())
                            {
                                this.getTwitchStream().unpause();
                            }
                            else
                            {
                                this.getTwitchStream().pause();
                            }
                        }
                    }
                    else if (lvt_1_1_ == this.gameSettings.keyBindStreamCommercials.getKeyCode())
                    {
                        if (this.getTwitchStream().isBroadcasting())
                        {
                            this.getTwitchStream().requestCommercial();
                        }
                    }
                    else if (lvt_1_1_ == this.gameSettings.keyBindStreamToggleMic.getKeyCode())
                    {
                        this.stream.muteMicrophone(true);
                    }
                    else if (lvt_1_1_ == this.gameSettings.keyBindFullscreen.getKeyCode())
                    {
                        this.toggleFullscreen();
                    }
                    else if (lvt_1_1_ == this.gameSettings.keyBindScreenshot.getKeyCode())
                    {
                        this.ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot(this.mcDataDir, this.displayWidth, this.displayHeight, this.framebufferMc));
                    }
                }
                else if (lvt_1_1_ == this.gameSettings.keyBindStreamToggleMic.getKeyCode())
                {
                    this.stream.muteMicrophone(false);
                }
            }
        }
    }

    public MinecraftSessionService getSessionService()
    {
        return this.sessionService;
    }

    public SkinManager getSkinManager()
    {
        return this.skinManager;
    }

    public Entity getRenderViewEntity()
    {
        return this.renderViewEntity;
    }

    public void setRenderViewEntity(Entity viewingEntity)
    {
        this.renderViewEntity = viewingEntity;
        this.entityRenderer.loadEntityShader(viewingEntity);
    }

    public <V> ListenableFuture<V> addScheduledTask(Callable<V> callableToSchedule)
    {
        Validate.notNull(callableToSchedule);

        if (!this.isCallingFromMinecraftThread())
        {
            ListenableFutureTask<V> lvt_2_1_ = ListenableFutureTask.create(callableToSchedule);

            synchronized (this.scheduledTasks)
            {
                this.scheduledTasks.add(lvt_2_1_);
                return lvt_2_1_;
            }
        }
        else
        {
            try
            {
                return Futures.immediateFuture(callableToSchedule.call());
            }
            catch (Exception var6)
            {
                return Futures.immediateFailedCheckedFuture(var6);
            }
        }
    }

    public ListenableFuture<Object> addScheduledTask(Runnable runnableToSchedule)
    {
        Validate.notNull(runnableToSchedule);
        return this.<Object>addScheduledTask(Executors.callable(runnableToSchedule));
    }

    public boolean isCallingFromMinecraftThread()
    {
        return Thread.currentThread() == this.mcThread;
    }

    public BlockRendererDispatcher getBlockRendererDispatcher()
    {
        return this.blockRenderDispatcher;
    }

    public RenderManager getRenderManager()
    {
        return this.renderManager;
    }

    public RenderItem getRenderItem()
    {
        return this.renderItem;
    }

    public ItemRenderer getItemRenderer()
    {
        return this.itemRenderer;
    }

    public static int getDebugFPS()
    {
        return debugFPS;
    }

    /**
     * Return the FrameTimer's instance
     */
    public FrameTimer getFrameTimer()
    {
        return this.frameTimer;
    }

    public static Map<String, String> getSessionInfo()
    {
        Map<String, String> lvt_0_1_ = Maps.newHashMap();
        lvt_0_1_.put("X-Minecraft-Username", getMinecraft().getSession().getUsername());
        lvt_0_1_.put("X-Minecraft-UUID", getMinecraft().getSession().getPlayerID());
        lvt_0_1_.put("X-Minecraft-Version", "1.8.9");
        return lvt_0_1_;
    }

    /**
     * Return true if the player is connected to a realms server
     */
    public boolean isConnectedToRealms()
    {
        return this.connectedToRealms;
    }

    /**
     * Set if the player is connected to a realms server
     *  
     * @param isConnected The value that set if the player is connected to a realms server or not
     */
    public void setConnectedToRealms(boolean isConnected)
    {
        this.connectedToRealms = isConnected;
    }
}
