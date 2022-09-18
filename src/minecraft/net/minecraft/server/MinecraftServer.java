package net.minecraft.server;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import javax.imageio.ImageIO;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkSystem;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.profiler.IPlayerUsage;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ITickable;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Util;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldManager;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MinecraftServer implements Runnable, ICommandSender, IThreadListener, IPlayerUsage
{
    private static final Logger logger = LogManager.getLogger();
    public static final File USER_CACHE_FILE = new File("usercache.json");

    /** Instance of Minecraft Server. */
    private static MinecraftServer mcServer;
    private final ISaveFormat anvilConverterForAnvilFile;

    /** The PlayerUsageSnooper instance. */
    private final PlayerUsageSnooper usageSnooper = new PlayerUsageSnooper("server", this, getCurrentTimeMillis());
    private final File anvilFile;
    private final List<ITickable> playersOnline = Lists.newArrayList();
    protected final ICommandManager commandManager;
    public final Profiler theProfiler = new Profiler();
    private final NetworkSystem networkSystem;
    private final ServerStatusResponse statusResponse = new ServerStatusResponse();
    private final Random random = new Random();

    /** The server's port. */
    private int serverPort = -1;

    /** The server world instances. */
    public WorldServer[] worldServers;

    /** The ServerConfigurationManager instance. */
    private ServerConfigurationManager serverConfigManager;

    /**
     * Indicates whether the server is running or not. Set to false to initiate a shutdown.
     */
    private boolean serverRunning = true;

    /** Indicates to other classes that the server is safely stopped. */
    private boolean serverStopped;

    /** Incremented every tick. */
    private int tickCounter;
    protected final Proxy serverProxy;

    /**
     * The task the server is currently working on(and will output on outputPercentRemaining).
     */
    public String currentTask;

    /** The percentage of the current task finished so far. */
    public int percentDone;

    /** True if the server is in online mode. */
    private boolean onlineMode;

    /** True if the server has animals turned on. */
    private boolean canSpawnAnimals;
    private boolean canSpawnNPCs;

    /** Indicates whether PvP is active on the server or not. */
    private boolean pvpEnabled;

    /** Determines if flight is allowed or not. */
    private boolean allowFlight;

    /** The server MOTD string. */
    private String motd;

    /** Maximum build height. */
    private int buildLimit;
    private int maxPlayerIdleMinutes = 0;
    public final long[] tickTimeArray = new long[100];

    /** Stats are [dimension][tick%100] system.nanoTime is stored. */
    public long[][] timeOfLastDimensionTick;
    private KeyPair serverKeyPair;

    /** Username of the server owner (for integrated servers) */
    private String serverOwner;
    private String folderName;
    private String worldName;
    private boolean isDemo;
    private boolean enableBonusChest;

    /**
     * If true, there is no need to save chunks or stop the server, because that is already being done.
     */
    private boolean worldIsBeingDeleted;

    /** The texture pack for the server */
    private String resourcePackUrl = "";
    private String resourcePackHash = "";
    private boolean serverIsRunning;

    /**
     * Set when warned for "Can't keep up", which triggers again after 15 seconds.
     */
    private long timeOfLastWarning;
    private String userMessage;
    private boolean startProfiling;
    private boolean isGamemodeForced;
    private final YggdrasilAuthenticationService authService;
    private final MinecraftSessionService sessionService;
    private long nanoTimeSinceStatusRefresh = 0L;
    private final GameProfileRepository profileRepo;
    private final PlayerProfileCache profileCache;
    protected final Queue < FutureTask<? >> futureTaskQueue = Queues.newArrayDeque();
    private Thread serverThread;
    private long currentTime = getCurrentTimeMillis();

    public MinecraftServer(Proxy proxy, File workDir)
    {
        this.serverProxy = proxy;
        mcServer = this;
        this.anvilFile = null;
        this.networkSystem = null;
        this.profileCache = new PlayerProfileCache(this, workDir);
        this.commandManager = null;
        this.anvilConverterForAnvilFile = null;
        this.authService = new YggdrasilAuthenticationService(proxy, UUID.randomUUID().toString());
        this.sessionService = this.authService.createMinecraftSessionService();
        this.profileRepo = this.authService.createProfileRepository();
    }

    public MinecraftServer(File workDir, Proxy proxy, File profileCacheDir)
    {
        this.serverProxy = proxy;
        mcServer = this;
        this.anvilFile = workDir;
        this.networkSystem = new NetworkSystem(this);
        this.profileCache = new PlayerProfileCache(this, profileCacheDir);
        this.commandManager = this.createNewCommandManager();
        this.anvilConverterForAnvilFile = new AnvilSaveConverter(workDir);
        this.authService = new YggdrasilAuthenticationService(proxy, UUID.randomUUID().toString());
        this.sessionService = this.authService.createMinecraftSessionService();
        this.profileRepo = this.authService.createProfileRepository();
    }

    protected ServerCommandManager createNewCommandManager()
    {
        return new ServerCommandManager();
    }

    /**
     * Initialises the server and starts it.
     */
    protected abstract boolean startServer() throws IOException;

    protected void convertMapIfNeeded(String worldNameIn)
    {
        if (this.getActiveAnvilConverter().isOldMapFormat(worldNameIn))
        {
            logger.info("Converting map!");
            this.setUserMessage("menu.convertingLevel");
            this.getActiveAnvilConverter().convertMapFormat(worldNameIn, new IProgressUpdate()
            {
                private long startTime = System.currentTimeMillis();
                public void displaySavingString(String message)
                {
                }
                public void resetProgressAndMessage(String message)
                {
                }
                public void setLoadingProgress(int progress)
                {
                    if (System.currentTimeMillis() - this.startTime >= 1000L)
                    {
                        this.startTime = System.currentTimeMillis();
                        MinecraftServer.logger.info("Converting... " + progress + "%");
                    }
                }
                public void setDoneWorking()
                {
                }
                public void displayLoadingString(String message)
                {
                }
            });
        }
    }

    /**
     * Typically "menu.convertingLevel", "menu.loadingLevel" or others.
     */
    protected synchronized void setUserMessage(String message)
    {
        this.userMessage = message;
    }

    public synchronized String getUserMessage()
    {
        return this.userMessage;
    }

    protected void loadAllWorlds(String saveName, String worldNameIn, long seed, WorldType type, String worldNameIn2)
    {
        this.convertMapIfNeeded(saveName);
        this.setUserMessage("menu.loadingLevel");
        this.worldServers = new WorldServer[3];
        this.timeOfLastDimensionTick = new long[this.worldServers.length][100];
        ISaveHandler lvt_7_1_ = this.anvilConverterForAnvilFile.getSaveLoader(saveName, true);
        this.setResourcePackFromWorld(this.getFolderName(), lvt_7_1_);
        WorldInfo lvt_9_1_ = lvt_7_1_.loadWorldInfo();
        WorldSettings lvt_8_1_;

        if (lvt_9_1_ == null)
        {
            if (this.isDemo())
            {
                lvt_8_1_ = DemoWorldServer.demoWorldSettings;
            }
            else
            {
                lvt_8_1_ = new WorldSettings(seed, this.getGameType(), this.canStructuresSpawn(), this.isHardcore(), type);
                lvt_8_1_.setWorldName(worldNameIn2);

                if (this.enableBonusChest)
                {
                    lvt_8_1_.enableBonusChest();
                }
            }

            lvt_9_1_ = new WorldInfo(lvt_8_1_, worldNameIn);
        }
        else
        {
            lvt_9_1_.setWorldName(worldNameIn);
            lvt_8_1_ = new WorldSettings(lvt_9_1_);
        }

        for (int lvt_10_1_ = 0; lvt_10_1_ < this.worldServers.length; ++lvt_10_1_)
        {
            int lvt_11_1_ = 0;

            if (lvt_10_1_ == 1)
            {
                lvt_11_1_ = -1;
            }

            if (lvt_10_1_ == 2)
            {
                lvt_11_1_ = 1;
            }

            if (lvt_10_1_ == 0)
            {
                if (this.isDemo())
                {
                    this.worldServers[lvt_10_1_] = (WorldServer)(new DemoWorldServer(this, lvt_7_1_, lvt_9_1_, lvt_11_1_, this.theProfiler)).init();
                }
                else
                {
                    this.worldServers[lvt_10_1_] = (WorldServer)(new WorldServer(this, lvt_7_1_, lvt_9_1_, lvt_11_1_, this.theProfiler)).init();
                }

                this.worldServers[lvt_10_1_].initialize(lvt_8_1_);
            }
            else
            {
                this.worldServers[lvt_10_1_] = (WorldServer)(new WorldServerMulti(this, lvt_7_1_, lvt_11_1_, this.worldServers[0], this.theProfiler)).init();
            }

            this.worldServers[lvt_10_1_].addWorldAccess(new WorldManager(this, this.worldServers[lvt_10_1_]));

            if (!this.isSinglePlayer())
            {
                this.worldServers[lvt_10_1_].getWorldInfo().setGameType(this.getGameType());
            }
        }

        this.serverConfigManager.setPlayerManager(this.worldServers);
        this.setDifficultyForAllWorlds(this.getDifficulty());
        this.initialWorldChunkLoad();
    }

    protected void initialWorldChunkLoad()
    {
        int lvt_1_1_ = 16;
        int lvt_2_1_ = 4;
        int lvt_3_1_ = 192;
        int lvt_4_1_ = 625;
        int lvt_5_1_ = 0;
        this.setUserMessage("menu.generatingTerrain");
        int lvt_6_1_ = 0;
        logger.info("Preparing start region for level " + lvt_6_1_);
        WorldServer lvt_7_1_ = this.worldServers[lvt_6_1_];
        BlockPos lvt_8_1_ = lvt_7_1_.getSpawnPoint();
        long lvt_9_1_ = getCurrentTimeMillis();

        for (int lvt_11_1_ = -192; lvt_11_1_ <= 192 && this.isServerRunning(); lvt_11_1_ += 16)
        {
            for (int lvt_12_1_ = -192; lvt_12_1_ <= 192 && this.isServerRunning(); lvt_12_1_ += 16)
            {
                long lvt_13_1_ = getCurrentTimeMillis();

                if (lvt_13_1_ - lvt_9_1_ > 1000L)
                {
                    this.outputPercentRemaining("Preparing spawn area", lvt_5_1_ * 100 / 625);
                    lvt_9_1_ = lvt_13_1_;
                }

                ++lvt_5_1_;
                lvt_7_1_.theChunkProviderServer.loadChunk(lvt_8_1_.getX() + lvt_11_1_ >> 4, lvt_8_1_.getZ() + lvt_12_1_ >> 4);
            }
        }

        this.clearCurrentTask();
    }

    protected void setResourcePackFromWorld(String worldNameIn, ISaveHandler saveHandlerIn)
    {
        File lvt_3_1_ = new File(saveHandlerIn.getWorldDirectory(), "resources.zip");

        if (lvt_3_1_.isFile())
        {
            this.setResourcePack("level://" + worldNameIn + "/" + lvt_3_1_.getName(), "");
        }
    }

    public abstract boolean canStructuresSpawn();

    public abstract WorldSettings.GameType getGameType();

    /**
     * Get the server's difficulty
     */
    public abstract EnumDifficulty getDifficulty();

    /**
     * Defaults to false.
     */
    public abstract boolean isHardcore();

    public abstract int getOpPermissionLevel();

    /**
     * Get if RCON command events should be broadcast to ops
     */
    public abstract boolean shouldBroadcastRconToOps();

    /**
     * Get if console command events should be broadcast to ops
     */
    public abstract boolean shouldBroadcastConsoleToOps();

    /**
     * Used to display a percent remaining given text and the percentage.
     */
    protected void outputPercentRemaining(String message, int percent)
    {
        this.currentTask = message;
        this.percentDone = percent;
        logger.info(message + ": " + percent + "%");
    }

    /**
     * Set current task to null and set its percentage to 0.
     */
    protected void clearCurrentTask()
    {
        this.currentTask = null;
        this.percentDone = 0;
    }

    /**
     * par1 indicates if a log message should be output.
     */
    protected void saveAllWorlds(boolean dontLog)
    {
        if (!this.worldIsBeingDeleted)
        {
            for (WorldServer lvt_5_1_ : this.worldServers)
            {
                if (lvt_5_1_ != null)
                {
                    if (!dontLog)
                    {
                        logger.info("Saving chunks for level \'" + lvt_5_1_.getWorldInfo().getWorldName() + "\'/" + lvt_5_1_.provider.getDimensionName());
                    }

                    try
                    {
                        lvt_5_1_.saveAllChunks(true, (IProgressUpdate)null);
                    }
                    catch (MinecraftException var7)
                    {
                        logger.warn(var7.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Saves all necessary data as preparation for stopping the server.
     */
    public void stopServer()
    {
        if (!this.worldIsBeingDeleted)
        {
            logger.info("Stopping server");

            if (this.getNetworkSystem() != null)
            {
                this.getNetworkSystem().terminateEndpoints();
            }

            if (this.serverConfigManager != null)
            {
                logger.info("Saving players");
                this.serverConfigManager.saveAllPlayerData();
                this.serverConfigManager.removeAllPlayers();
            }

            if (this.worldServers != null)
            {
                logger.info("Saving worlds");
                this.saveAllWorlds(false);

                for (int lvt_1_1_ = 0; lvt_1_1_ < this.worldServers.length; ++lvt_1_1_)
                {
                    WorldServer lvt_2_1_ = this.worldServers[lvt_1_1_];
                    lvt_2_1_.flush();
                }
            }

            if (this.usageSnooper.isSnooperRunning())
            {
                this.usageSnooper.stopSnooper();
            }
        }
    }

    public boolean isServerRunning()
    {
        return this.serverRunning;
    }

    /**
     * Sets the serverRunning variable to false, in order to get the server to shut down.
     */
    public void initiateShutdown()
    {
        this.serverRunning = false;
    }

    protected void setInstance()
    {
        mcServer = this;
    }

    public void run()
    {
        try
        {
            if (this.startServer())
            {
                this.currentTime = getCurrentTimeMillis();
                long lvt_1_1_ = 0L;
                this.statusResponse.setServerDescription(new ChatComponentText(this.motd));
                this.statusResponse.setProtocolVersionInfo(new ServerStatusResponse.MinecraftProtocolVersionIdentifier("1.8.9", 47));
                this.addFaviconToStatusResponse(this.statusResponse);

                while (this.serverRunning)
                {
                    long lvt_3_1_ = getCurrentTimeMillis();
                    long lvt_5_1_ = lvt_3_1_ - this.currentTime;

                    if (lvt_5_1_ > 2000L && this.currentTime - this.timeOfLastWarning >= 15000L)
                    {
                        logger.warn("Can\'t keep up! Did the system time change, or is the server overloaded? Running {}ms behind, skipping {} tick(s)", new Object[] {Long.valueOf(lvt_5_1_), Long.valueOf(lvt_5_1_ / 50L)});
                        lvt_5_1_ = 2000L;
                        this.timeOfLastWarning = this.currentTime;
                    }

                    if (lvt_5_1_ < 0L)
                    {
                        logger.warn("Time ran backwards! Did the system time change?");
                        lvt_5_1_ = 0L;
                    }

                    lvt_1_1_ += lvt_5_1_;
                    this.currentTime = lvt_3_1_;

                    if (this.worldServers[0].areAllPlayersAsleep())
                    {
                        this.tick();
                        lvt_1_1_ = 0L;
                    }
                    else
                    {
                        while (lvt_1_1_ > 50L)
                        {
                            lvt_1_1_ -= 50L;
                            this.tick();
                        }
                    }

                    Thread.sleep(Math.max(1L, 50L - lvt_1_1_));
                    this.serverIsRunning = true;
                }
            }
            else
            {
                this.finalTick((CrashReport)null);
            }
        }
        catch (Throwable var46)
        {
            logger.error("Encountered an unexpected exception", var46);
            CrashReport lvt_2_1_ = null;

            if (var46 instanceof ReportedException)
            {
                lvt_2_1_ = this.addServerInfoToCrashReport(((ReportedException)var46).getCrashReport());
            }
            else
            {
                lvt_2_1_ = this.addServerInfoToCrashReport(new CrashReport("Exception in server tick loop", var46));
            }

            File lvt_3_2_ = new File(new File(this.getDataDirectory(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");

            if (lvt_2_1_.saveToFile(lvt_3_2_))
            {
                logger.error("This crash report has been saved to: " + lvt_3_2_.getAbsolutePath());
            }
            else
            {
                logger.error("We were unable to save this crash report to disk.");
            }

            this.finalTick(lvt_2_1_);
        }
        finally
        {
            try
            {
                this.serverStopped = true;
                this.stopServer();
            }
            catch (Throwable var44)
            {
                logger.error("Exception stopping the server", var44);
            }
            finally
            {
                this.systemExitNow();
            }
        }
    }

    private void addFaviconToStatusResponse(ServerStatusResponse response)
    {
        File lvt_2_1_ = this.getFile("server-icon.png");

        if (lvt_2_1_.isFile())
        {
            ByteBuf lvt_3_1_ = Unpooled.buffer();

            try
            {
                BufferedImage lvt_4_1_ = ImageIO.read(lvt_2_1_);
                Validate.validState(lvt_4_1_.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                Validate.validState(lvt_4_1_.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
                ImageIO.write(lvt_4_1_, "PNG", new ByteBufOutputStream(lvt_3_1_));
                ByteBuf lvt_5_1_ = Base64.encode(lvt_3_1_);
                response.setFavicon("data:image/png;base64," + lvt_5_1_.toString(Charsets.UTF_8));
            }
            catch (Exception var9)
            {
                logger.error("Couldn\'t load server icon", var9);
            }
            finally
            {
                lvt_3_1_.release();
            }
        }
    }

    public File getDataDirectory()
    {
        return new File(".");
    }

    /**
     * Called on exit from the main run() loop.
     */
    protected void finalTick(CrashReport report)
    {
    }

    /**
     * Directly calls System.exit(0), instantly killing the program.
     */
    protected void systemExitNow()
    {
    }

    /**
     * Main function called by run() every loop.
     */
    public void tick()
    {
        long lvt_1_1_ = System.nanoTime();
        ++this.tickCounter;

        if (this.startProfiling)
        {
            this.startProfiling = false;
            this.theProfiler.profilingEnabled = true;
            this.theProfiler.clearProfiling();
        }

        this.theProfiler.startSection("root");
        this.updateTimeLightAndEntities();

        if (lvt_1_1_ - this.nanoTimeSinceStatusRefresh >= 5000000000L)
        {
            this.nanoTimeSinceStatusRefresh = lvt_1_1_;
            this.statusResponse.setPlayerCountData(new ServerStatusResponse.PlayerCountData(this.getMaxPlayers(), this.getCurrentPlayerCount()));
            GameProfile[] lvt_3_1_ = new GameProfile[Math.min(this.getCurrentPlayerCount(), 12)];
            int lvt_4_1_ = MathHelper.getRandomIntegerInRange(this.random, 0, this.getCurrentPlayerCount() - lvt_3_1_.length);

            for (int lvt_5_1_ = 0; lvt_5_1_ < lvt_3_1_.length; ++lvt_5_1_)
            {
                lvt_3_1_[lvt_5_1_] = ((EntityPlayerMP)this.serverConfigManager.getPlayerList().get(lvt_4_1_ + lvt_5_1_)).getGameProfile();
            }

            Collections.shuffle(Arrays.asList(lvt_3_1_));
            this.statusResponse.getPlayerCountData().setPlayers(lvt_3_1_);
        }

        if (this.tickCounter % 900 == 0)
        {
            this.theProfiler.startSection("save");
            this.serverConfigManager.saveAllPlayerData();
            this.saveAllWorlds(true);
            this.theProfiler.endSection();
        }

        this.theProfiler.startSection("tallying");
        this.tickTimeArray[this.tickCounter % 100] = System.nanoTime() - lvt_1_1_;
        this.theProfiler.endSection();
        this.theProfiler.startSection("snooper");

        if (!this.usageSnooper.isSnooperRunning() && this.tickCounter > 100)
        {
            this.usageSnooper.startSnooper();
        }

        if (this.tickCounter % 6000 == 0)
        {
            this.usageSnooper.addMemoryStatsToSnooper();
        }

        this.theProfiler.endSection();
        this.theProfiler.endSection();
    }

    public void updateTimeLightAndEntities()
    {
        this.theProfiler.startSection("jobs");

        synchronized (this.futureTaskQueue)
        {
            while (!this.futureTaskQueue.isEmpty())
            {
                Util.runTask((FutureTask)this.futureTaskQueue.poll(), logger);
            }
        }

        this.theProfiler.endStartSection("levels");

        for (int lvt_1_1_ = 0; lvt_1_1_ < this.worldServers.length; ++lvt_1_1_)
        {
            long lvt_2_1_ = System.nanoTime();

            if (lvt_1_1_ == 0 || this.getAllowNether())
            {
                WorldServer lvt_4_1_ = this.worldServers[lvt_1_1_];
                this.theProfiler.startSection(lvt_4_1_.getWorldInfo().getWorldName());

                if (this.tickCounter % 20 == 0)
                {
                    this.theProfiler.startSection("timeSync");
                    this.serverConfigManager.sendPacketToAllPlayersInDimension(new S03PacketTimeUpdate(lvt_4_1_.getTotalWorldTime(), lvt_4_1_.getWorldTime(), lvt_4_1_.getGameRules().getBoolean("doDaylightCycle")), lvt_4_1_.provider.getDimensionId());
                    this.theProfiler.endSection();
                }

                this.theProfiler.startSection("tick");

                try
                {
                    lvt_4_1_.tick();
                }
                catch (Throwable var8)
                {
                    CrashReport lvt_6_1_ = CrashReport.makeCrashReport(var8, "Exception ticking world");
                    lvt_4_1_.addWorldInfoToCrashReport(lvt_6_1_);
                    throw new ReportedException(lvt_6_1_);
                }

                try
                {
                    lvt_4_1_.updateEntities();
                }
                catch (Throwable var7)
                {
                    CrashReport lvt_6_2_ = CrashReport.makeCrashReport(var7, "Exception ticking world entities");
                    lvt_4_1_.addWorldInfoToCrashReport(lvt_6_2_);
                    throw new ReportedException(lvt_6_2_);
                }

                this.theProfiler.endSection();
                this.theProfiler.startSection("tracker");
                lvt_4_1_.getEntityTracker().updateTrackedEntities();
                this.theProfiler.endSection();
                this.theProfiler.endSection();
            }

            this.timeOfLastDimensionTick[lvt_1_1_][this.tickCounter % 100] = System.nanoTime() - lvt_2_1_;
        }

        this.theProfiler.endStartSection("connection");
        this.getNetworkSystem().networkTick();
        this.theProfiler.endStartSection("players");
        this.serverConfigManager.onTick();
        this.theProfiler.endStartSection("tickables");

        for (int lvt_1_2_ = 0; lvt_1_2_ < this.playersOnline.size(); ++lvt_1_2_)
        {
            ((ITickable)this.playersOnline.get(lvt_1_2_)).update();
        }

        this.theProfiler.endSection();
    }

    public boolean getAllowNether()
    {
        return true;
    }

    public void startServerThread()
    {
        this.serverThread = new Thread(this, "Server thread");
        this.serverThread.start();
    }

    /**
     * Returns a File object from the specified string.
     */
    public File getFile(String fileName)
    {
        return new File(this.getDataDirectory(), fileName);
    }

    /**
     * Logs the message with a level of WARN.
     */
    public void logWarning(String msg)
    {
        logger.warn(msg);
    }

    /**
     * Gets the worldServer by the given dimension.
     */
    public WorldServer worldServerForDimension(int dimension)
    {
        return dimension == -1 ? this.worldServers[1] : (dimension == 1 ? this.worldServers[2] : this.worldServers[0]);
    }

    /**
     * Returns the server's Minecraft version as string.
     */
    public String getMinecraftVersion()
    {
        return "1.8.9";
    }

    /**
     * Returns the number of players currently on the server.
     */
    public int getCurrentPlayerCount()
    {
        return this.serverConfigManager.getCurrentPlayerCount();
    }

    /**
     * Returns the maximum number of players allowed on the server.
     */
    public int getMaxPlayers()
    {
        return this.serverConfigManager.getMaxPlayers();
    }

    /**
     * Returns an array of the usernames of all the connected players.
     */
    public String[] getAllUsernames()
    {
        return this.serverConfigManager.getAllUsernames();
    }

    /**
     * Returns an array of the GameProfiles of all the connected players
     */
    public GameProfile[] getGameProfiles()
    {
        return this.serverConfigManager.getAllProfiles();
    }

    public String getServerModName()
    {
        return "vanilla";
    }

    /**
     * Adds the server info, including from theWorldServer, to the crash report.
     */
    public CrashReport addServerInfoToCrashReport(CrashReport report)
    {
        report.getCategory().addCrashSectionCallable("Profiler Position", new Callable<String>()
        {
            public String call() throws Exception
            {
                return MinecraftServer.this.theProfiler.profilingEnabled ? MinecraftServer.this.theProfiler.getNameOfLastSection() : "N/A (disabled)";
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });

        if (this.serverConfigManager != null)
        {
            report.getCategory().addCrashSectionCallable("Player Count", new Callable<String>()
            {
                public String call()
                {
                    return MinecraftServer.this.serverConfigManager.getCurrentPlayerCount() + " / " + MinecraftServer.this.serverConfigManager.getMaxPlayers() + "; " + MinecraftServer.this.serverConfigManager.getPlayerList();
                }
                public Object call() throws Exception
                {
                    return this.call();
                }
            });
        }

        return report;
    }

    public List<String> getTabCompletions(ICommandSender sender, String input, BlockPos pos)
    {
        List<String> lvt_4_1_ = Lists.newArrayList();

        if (input.startsWith("/"))
        {
            input = input.substring(1);
            boolean lvt_5_1_ = !input.contains(" ");
            List<String> lvt_6_1_ = this.commandManager.getTabCompletionOptions(sender, input, pos);

            if (lvt_6_1_ != null)
            {
                for (String lvt_8_1_ : lvt_6_1_)
                {
                    if (lvt_5_1_)
                    {
                        lvt_4_1_.add("/" + lvt_8_1_);
                    }
                    else
                    {
                        lvt_4_1_.add(lvt_8_1_);
                    }
                }
            }

            return lvt_4_1_;
        }
        else
        {
            String[] lvt_5_2_ = input.split(" ", -1);
            String lvt_6_2_ = lvt_5_2_[lvt_5_2_.length - 1];

            for (String lvt_10_1_ : this.serverConfigManager.getAllUsernames())
            {
                if (CommandBase.doesStringStartWith(lvt_6_2_, lvt_10_1_))
                {
                    lvt_4_1_.add(lvt_10_1_);
                }
            }

            return lvt_4_1_;
        }
    }

    /**
     * Gets mcServer.
     */
    public static MinecraftServer getServer()
    {
        return mcServer;
    }

    public boolean isAnvilFileSet()
    {
        return this.anvilFile != null;
    }

    /**
     * Get the name of this object. For players this returns their username
     */
    public String getName()
    {
        return "Server";
    }

    /**
     * Send a chat message to the CommandSender
     */
    public void addChatMessage(IChatComponent component)
    {
        logger.info(component.getUnformattedText());
    }

    /**
     * Returns {@code true} if the CommandSender is allowed to execute the command, {@code false} if not
     */
    public boolean canCommandSenderUseCommand(int permLevel, String commandName)
    {
        return true;
    }

    public ICommandManager getCommandManager()
    {
        return this.commandManager;
    }

    /**
     * Gets KeyPair instanced in MinecraftServer.
     */
    public KeyPair getKeyPair()
    {
        return this.serverKeyPair;
    }

    /**
     * Returns the username of the server owner (for integrated servers)
     */
    public String getServerOwner()
    {
        return this.serverOwner;
    }

    /**
     * Sets the username of the owner of this server (in the case of an integrated server)
     */
    public void setServerOwner(String owner)
    {
        this.serverOwner = owner;
    }

    public boolean isSinglePlayer()
    {
        return this.serverOwner != null;
    }

    public String getFolderName()
    {
        return this.folderName;
    }

    public void setFolderName(String name)
    {
        this.folderName = name;
    }

    public void setWorldName(String p_71246_1_)
    {
        this.worldName = p_71246_1_;
    }

    public String getWorldName()
    {
        return this.worldName;
    }

    public void setKeyPair(KeyPair keyPair)
    {
        this.serverKeyPair = keyPair;
    }

    public void setDifficultyForAllWorlds(EnumDifficulty difficulty)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < this.worldServers.length; ++lvt_2_1_)
        {
            World lvt_3_1_ = this.worldServers[lvt_2_1_];

            if (lvt_3_1_ != null)
            {
                if (lvt_3_1_.getWorldInfo().isHardcoreModeEnabled())
                {
                    lvt_3_1_.getWorldInfo().setDifficulty(EnumDifficulty.HARD);
                    lvt_3_1_.setAllowedSpawnTypes(true, true);
                }
                else if (this.isSinglePlayer())
                {
                    lvt_3_1_.getWorldInfo().setDifficulty(difficulty);
                    lvt_3_1_.setAllowedSpawnTypes(lvt_3_1_.getDifficulty() != EnumDifficulty.PEACEFUL, true);
                }
                else
                {
                    lvt_3_1_.getWorldInfo().setDifficulty(difficulty);
                    lvt_3_1_.setAllowedSpawnTypes(this.allowSpawnMonsters(), this.canSpawnAnimals);
                }
            }
        }
    }

    protected boolean allowSpawnMonsters()
    {
        return true;
    }

    /**
     * Gets whether this is a demo or not.
     */
    public boolean isDemo()
    {
        return this.isDemo;
    }

    /**
     * Sets whether this is a demo or not.
     */
    public void setDemo(boolean demo)
    {
        this.isDemo = demo;
    }

    public void canCreateBonusChest(boolean enable)
    {
        this.enableBonusChest = enable;
    }

    public ISaveFormat getActiveAnvilConverter()
    {
        return this.anvilConverterForAnvilFile;
    }

    /**
     * WARNING : directly calls
     * getActiveAnvilConverter().deleteWorldDirectory(theWorldServer[0].getSaveHandler().getWorldDirectoryName());
     */
    public void deleteWorldAndStopServer()
    {
        this.worldIsBeingDeleted = true;
        this.getActiveAnvilConverter().flushCache();

        for (int lvt_1_1_ = 0; lvt_1_1_ < this.worldServers.length; ++lvt_1_1_)
        {
            WorldServer lvt_2_1_ = this.worldServers[lvt_1_1_];

            if (lvt_2_1_ != null)
            {
                lvt_2_1_.flush();
            }
        }

        this.getActiveAnvilConverter().deleteWorldDirectory(this.worldServers[0].getSaveHandler().getWorldDirectoryName());
        this.initiateShutdown();
    }

    public String getResourcePackUrl()
    {
        return this.resourcePackUrl;
    }

    public String getResourcePackHash()
    {
        return this.resourcePackHash;
    }

    public void setResourcePack(String url, String hash)
    {
        this.resourcePackUrl = url;
        this.resourcePackHash = hash;
    }

    public void addServerStatsToSnooper(PlayerUsageSnooper playerSnooper)
    {
        playerSnooper.addClientStat("whitelist_enabled", Boolean.valueOf(false));
        playerSnooper.addClientStat("whitelist_count", Integer.valueOf(0));

        if (this.serverConfigManager != null)
        {
            playerSnooper.addClientStat("players_current", Integer.valueOf(this.getCurrentPlayerCount()));
            playerSnooper.addClientStat("players_max", Integer.valueOf(this.getMaxPlayers()));
            playerSnooper.addClientStat("players_seen", Integer.valueOf(this.serverConfigManager.getAvailablePlayerDat().length));
        }

        playerSnooper.addClientStat("uses_auth", Boolean.valueOf(this.onlineMode));
        playerSnooper.addClientStat("gui_state", this.getGuiEnabled() ? "enabled" : "disabled");
        playerSnooper.addClientStat("run_time", Long.valueOf((getCurrentTimeMillis() - playerSnooper.getMinecraftStartTimeMillis()) / 60L * 1000L));
        playerSnooper.addClientStat("avg_tick_ms", Integer.valueOf((int)(MathHelper.average(this.tickTimeArray) * 1.0E-6D)));
        int lvt_2_1_ = 0;

        if (this.worldServers != null)
        {
            for (int lvt_3_1_ = 0; lvt_3_1_ < this.worldServers.length; ++lvt_3_1_)
            {
                if (this.worldServers[lvt_3_1_] != null)
                {
                    WorldServer lvt_4_1_ = this.worldServers[lvt_3_1_];
                    WorldInfo lvt_5_1_ = lvt_4_1_.getWorldInfo();
                    playerSnooper.addClientStat("world[" + lvt_2_1_ + "][dimension]", Integer.valueOf(lvt_4_1_.provider.getDimensionId()));
                    playerSnooper.addClientStat("world[" + lvt_2_1_ + "][mode]", lvt_5_1_.getGameType());
                    playerSnooper.addClientStat("world[" + lvt_2_1_ + "][difficulty]", lvt_4_1_.getDifficulty());
                    playerSnooper.addClientStat("world[" + lvt_2_1_ + "][hardcore]", Boolean.valueOf(lvt_5_1_.isHardcoreModeEnabled()));
                    playerSnooper.addClientStat("world[" + lvt_2_1_ + "][generator_name]", lvt_5_1_.getTerrainType().getWorldTypeName());
                    playerSnooper.addClientStat("world[" + lvt_2_1_ + "][generator_version]", Integer.valueOf(lvt_5_1_.getTerrainType().getGeneratorVersion()));
                    playerSnooper.addClientStat("world[" + lvt_2_1_ + "][height]", Integer.valueOf(this.buildLimit));
                    playerSnooper.addClientStat("world[" + lvt_2_1_ + "][chunks_loaded]", Integer.valueOf(lvt_4_1_.getChunkProvider().getLoadedChunkCount()));
                    ++lvt_2_1_;
                }
            }
        }

        playerSnooper.addClientStat("worlds", Integer.valueOf(lvt_2_1_));
    }

    public void addServerTypeToSnooper(PlayerUsageSnooper playerSnooper)
    {
        playerSnooper.addStatToSnooper("singleplayer", Boolean.valueOf(this.isSinglePlayer()));
        playerSnooper.addStatToSnooper("server_brand", this.getServerModName());
        playerSnooper.addStatToSnooper("gui_supported", GraphicsEnvironment.isHeadless() ? "headless" : "supported");
        playerSnooper.addStatToSnooper("dedicated", Boolean.valueOf(this.isDedicatedServer()));
    }

    /**
     * Returns whether snooping is enabled or not.
     */
    public boolean isSnooperEnabled()
    {
        return true;
    }

    public abstract boolean isDedicatedServer();

    public boolean isServerInOnlineMode()
    {
        return this.onlineMode;
    }

    public void setOnlineMode(boolean online)
    {
        this.onlineMode = online;
    }

    public boolean getCanSpawnAnimals()
    {
        return this.canSpawnAnimals;
    }

    public void setCanSpawnAnimals(boolean spawnAnimals)
    {
        this.canSpawnAnimals = spawnAnimals;
    }

    public boolean getCanSpawnNPCs()
    {
        return this.canSpawnNPCs;
    }

    /**
     * Get if native transport should be used. Native transport means linux server performance improvements and
     * optimized packet sending/receiving on linux
     */
    public abstract boolean shouldUseNativeTransport();

    public void setCanSpawnNPCs(boolean spawnNpcs)
    {
        this.canSpawnNPCs = spawnNpcs;
    }

    public boolean isPVPEnabled()
    {
        return this.pvpEnabled;
    }

    public void setAllowPvp(boolean allowPvp)
    {
        this.pvpEnabled = allowPvp;
    }

    public boolean isFlightAllowed()
    {
        return this.allowFlight;
    }

    public void setAllowFlight(boolean allow)
    {
        this.allowFlight = allow;
    }

    /**
     * Return whether command blocks are enabled.
     */
    public abstract boolean isCommandBlockEnabled();

    public String getMOTD()
    {
        return this.motd;
    }

    public void setMOTD(String motdIn)
    {
        this.motd = motdIn;
    }

    public int getBuildLimit()
    {
        return this.buildLimit;
    }

    public void setBuildLimit(int maxBuildHeight)
    {
        this.buildLimit = maxBuildHeight;
    }

    public boolean isServerStopped()
    {
        return this.serverStopped;
    }

    public ServerConfigurationManager getConfigurationManager()
    {
        return this.serverConfigManager;
    }

    public void setConfigManager(ServerConfigurationManager configManager)
    {
        this.serverConfigManager = configManager;
    }

    /**
     * Sets the game type for all worlds.
     */
    public void setGameType(WorldSettings.GameType gameMode)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < this.worldServers.length; ++lvt_2_1_)
        {
            getServer().worldServers[lvt_2_1_].getWorldInfo().setGameType(gameMode);
        }
    }

    public NetworkSystem getNetworkSystem()
    {
        return this.networkSystem;
    }

    public boolean serverIsInRunLoop()
    {
        return this.serverIsRunning;
    }

    public boolean getGuiEnabled()
    {
        return false;
    }

    /**
     * On dedicated does nothing. On integrated, sets commandsAllowedForAll, gameType and allows external connections.
     */
    public abstract String shareToLAN(WorldSettings.GameType type, boolean allowCheats);

    public int getTickCounter()
    {
        return this.tickCounter;
    }

    public void enableProfiling()
    {
        this.startProfiling = true;
    }

    public PlayerUsageSnooper getPlayerUsageSnooper()
    {
        return this.usageSnooper;
    }

    /**
     * Get the position in the world. <b>{@code null} is not allowed!</b> If you are not an entity in the world, return
     * the coordinates 0, 0, 0
     */
    public BlockPos getPosition()
    {
        return BlockPos.ORIGIN;
    }

    /**
     * Get the position vector. <b>{@code null} is not allowed!</b> If you are not an entity in the world, return 0.0D,
     * 0.0D, 0.0D
     */
    public Vec3 getPositionVector()
    {
        return new Vec3(0.0D, 0.0D, 0.0D);
    }

    /**
     * Get the world, if available. <b>{@code null} is not allowed!</b> If you are not an entity in the world, return
     * the overworld
     */
    public World getEntityWorld()
    {
        return this.worldServers[0];
    }

    /**
     * Returns the entity associated with the command sender. MAY BE NULL!
     */
    public Entity getCommandSenderEntity()
    {
        return null;
    }

    /**
     * Return the spawn protection area's size.
     */
    public int getSpawnProtectionSize()
    {
        return 16;
    }

    public boolean isBlockProtected(World worldIn, BlockPos pos, EntityPlayer playerIn)
    {
        return false;
    }

    public boolean getForceGamemode()
    {
        return this.isGamemodeForced;
    }

    public Proxy getServerProxy()
    {
        return this.serverProxy;
    }

    public static long getCurrentTimeMillis()
    {
        return System.currentTimeMillis();
    }

    public int getMaxPlayerIdleMinutes()
    {
        return this.maxPlayerIdleMinutes;
    }

    public void setPlayerIdleTimeout(int idleTimeout)
    {
        this.maxPlayerIdleMinutes = idleTimeout;
    }

    /**
     * Get the formatted ChatComponent that will be used for the sender's username in chat
     */
    public IChatComponent getDisplayName()
    {
        return new ChatComponentText(this.getName());
    }

    public boolean isAnnouncingPlayerAchievements()
    {
        return true;
    }

    public MinecraftSessionService getMinecraftSessionService()
    {
        return this.sessionService;
    }

    public GameProfileRepository getGameProfileRepository()
    {
        return this.profileRepo;
    }

    public PlayerProfileCache getPlayerProfileCache()
    {
        return this.profileCache;
    }

    public ServerStatusResponse getServerStatusResponse()
    {
        return this.statusResponse;
    }

    public void refreshStatusNextTick()
    {
        this.nanoTimeSinceStatusRefresh = 0L;
    }

    public Entity getEntityFromUuid(UUID uuid)
    {
        for (WorldServer lvt_5_1_ : this.worldServers)
        {
            if (lvt_5_1_ != null)
            {
                Entity lvt_6_1_ = lvt_5_1_.getEntityFromUuid(uuid);

                if (lvt_6_1_ != null)
                {
                    return lvt_6_1_;
                }
            }
        }

        return null;
    }

    /**
     * Returns true if the command sender should be sent feedback about executed commands
     */
    public boolean sendCommandFeedback()
    {
        return getServer().worldServers[0].getGameRules().getBoolean("sendCommandFeedback");
    }

    public void setCommandStat(CommandResultStats.Type type, int amount)
    {
    }

    public int getMaxWorldSize()
    {
        return 29999984;
    }

    public <V> ListenableFuture<V> callFromMainThread(Callable<V> callable)
    {
        Validate.notNull(callable);

        if (!this.isCallingFromMinecraftThread() && !this.isServerStopped())
        {
            ListenableFutureTask<V> lvt_2_1_ = ListenableFutureTask.create(callable);

            synchronized (this.futureTaskQueue)
            {
                this.futureTaskQueue.add(lvt_2_1_);
                return lvt_2_1_;
            }
        }
        else
        {
            try
            {
                return Futures.immediateFuture(callable.call());
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
        return this.<Object>callFromMainThread(Executors.callable(runnableToSchedule));
    }

    public boolean isCallingFromMinecraftThread()
    {
        return Thread.currentThread() == this.serverThread;
    }

    /**
     * The compression treshold. If the packet is larger than the specified amount of bytes, it will be compressed
     */
    public int getNetworkCompressionTreshold()
    {
        return 256;
    }
}
