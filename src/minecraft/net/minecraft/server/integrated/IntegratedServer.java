package net.minecraft.server.integrated;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ThreadLanServerPing;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.CryptManager;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.Util;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldManager;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IntegratedServer extends MinecraftServer
{
    private static final Logger logger = LogManager.getLogger();

    /** The Minecraft instance. */
    private final Minecraft mc;
    private final WorldSettings theWorldSettings;
    private boolean isGamePaused;
    private boolean isPublic;
    private ThreadLanServerPing lanServerPing;

    public IntegratedServer(Minecraft mcIn)
    {
        super(mcIn.getProxy(), new File(mcIn.mcDataDir, USER_CACHE_FILE.getName()));
        this.mc = mcIn;
        this.theWorldSettings = null;
    }

    public IntegratedServer(Minecraft mcIn, String folderName, String worldName, WorldSettings settings)
    {
        super(new File(mcIn.mcDataDir, "saves"), mcIn.getProxy(), new File(mcIn.mcDataDir, USER_CACHE_FILE.getName()));
        this.setServerOwner(mcIn.getSession().getUsername());
        this.setFolderName(folderName);
        this.setWorldName(worldName);
        this.setDemo(mcIn.isDemo());
        this.canCreateBonusChest(settings.isBonusChestEnabled());
        this.setBuildLimit(256);
        this.setConfigManager(new IntegratedPlayerList(this));
        this.mc = mcIn;
        this.theWorldSettings = this.isDemo() ? DemoWorldServer.demoWorldSettings : settings;
    }

    protected ServerCommandManager createNewCommandManager()
    {
        return new IntegratedServerCommandManager();
    }

    protected void loadAllWorlds(String saveName, String worldNameIn, long seed, WorldType type, String worldNameIn2)
    {
        this.convertMapIfNeeded(saveName);
        this.worldServers = new WorldServer[3];
        this.timeOfLastDimensionTick = new long[this.worldServers.length][100];
        ISaveHandler lvt_7_1_ = this.getActiveAnvilConverter().getSaveLoader(saveName, true);
        this.setResourcePackFromWorld(this.getFolderName(), lvt_7_1_);
        WorldInfo lvt_8_1_ = lvt_7_1_.loadWorldInfo();

        if (lvt_8_1_ == null)
        {
            lvt_8_1_ = new WorldInfo(this.theWorldSettings, worldNameIn);
        }
        else
        {
            lvt_8_1_.setWorldName(worldNameIn);
        }

        for (int lvt_9_1_ = 0; lvt_9_1_ < this.worldServers.length; ++lvt_9_1_)
        {
            int lvt_10_1_ = 0;

            if (lvt_9_1_ == 1)
            {
                lvt_10_1_ = -1;
            }

            if (lvt_9_1_ == 2)
            {
                lvt_10_1_ = 1;
            }

            if (lvt_9_1_ == 0)
            {
                if (this.isDemo())
                {
                    this.worldServers[lvt_9_1_] = (WorldServer)(new DemoWorldServer(this, lvt_7_1_, lvt_8_1_, lvt_10_1_, this.theProfiler)).init();
                }
                else
                {
                    this.worldServers[lvt_9_1_] = (WorldServer)(new WorldServer(this, lvt_7_1_, lvt_8_1_, lvt_10_1_, this.theProfiler)).init();
                }

                this.worldServers[lvt_9_1_].initialize(this.theWorldSettings);
            }
            else
            {
                this.worldServers[lvt_9_1_] = (WorldServer)(new WorldServerMulti(this, lvt_7_1_, lvt_10_1_, this.worldServers[0], this.theProfiler)).init();
            }

            this.worldServers[lvt_9_1_].addWorldAccess(new WorldManager(this, this.worldServers[lvt_9_1_]));
        }

        this.getConfigurationManager().setPlayerManager(this.worldServers);

        if (this.worldServers[0].getWorldInfo().getDifficulty() == null)
        {
            this.setDifficultyForAllWorlds(this.mc.gameSettings.difficulty);
        }

        this.initialWorldChunkLoad();
    }

    /**
     * Initialises the server and starts it.
     */
    protected boolean startServer() throws IOException
    {
        logger.info("Starting integrated minecraft server version 1.8.9");
        this.setOnlineMode(true);
        this.setCanSpawnAnimals(true);
        this.setCanSpawnNPCs(true);
        this.setAllowPvp(true);
        this.setAllowFlight(true);
        logger.info("Generating keypair");
        this.setKeyPair(CryptManager.generateKeyPair());
        this.loadAllWorlds(this.getFolderName(), this.getWorldName(), this.theWorldSettings.getSeed(), this.theWorldSettings.getTerrainType(), this.theWorldSettings.getWorldName());
        this.setMOTD(this.getServerOwner() + " - " + this.worldServers[0].getWorldInfo().getWorldName());
        return true;
    }

    /**
     * Main function called by run() every loop.
     */
    public void tick()
    {
        boolean lvt_1_1_ = this.isGamePaused;
        this.isGamePaused = Minecraft.getMinecraft().getNetHandler() != null && Minecraft.getMinecraft().isGamePaused();

        if (!lvt_1_1_ && this.isGamePaused)
        {
            logger.info("Saving and pausing game...");
            this.getConfigurationManager().saveAllPlayerData();
            this.saveAllWorlds(false);
        }

        if (this.isGamePaused)
        {
            synchronized (this.futureTaskQueue)
            {
                while (!this.futureTaskQueue.isEmpty())
                {
                    Util.runTask((FutureTask)this.futureTaskQueue.poll(), logger);
                }
            }
        }
        else
        {
            super.tick();

            if (this.mc.gameSettings.renderDistanceChunks != this.getConfigurationManager().getViewDistance())
            {
                logger.info("Changing view distance to {}, from {}", new Object[] {Integer.valueOf(this.mc.gameSettings.renderDistanceChunks), Integer.valueOf(this.getConfigurationManager().getViewDistance())});
                this.getConfigurationManager().setViewDistance(this.mc.gameSettings.renderDistanceChunks);
            }

            if (this.mc.theWorld != null)
            {
                WorldInfo lvt_2_1_ = this.worldServers[0].getWorldInfo();
                WorldInfo lvt_3_1_ = this.mc.theWorld.getWorldInfo();

                if (!lvt_2_1_.isDifficultyLocked() && lvt_3_1_.getDifficulty() != lvt_2_1_.getDifficulty())
                {
                    logger.info("Changing difficulty to {}, from {}", new Object[] {lvt_3_1_.getDifficulty(), lvt_2_1_.getDifficulty()});
                    this.setDifficultyForAllWorlds(lvt_3_1_.getDifficulty());
                }
                else if (lvt_3_1_.isDifficultyLocked() && !lvt_2_1_.isDifficultyLocked())
                {
                    logger.info("Locking difficulty to {}", new Object[] {lvt_3_1_.getDifficulty()});

                    for (WorldServer lvt_7_1_ : this.worldServers)
                    {
                        if (lvt_7_1_ != null)
                        {
                            lvt_7_1_.getWorldInfo().setDifficultyLocked(true);
                        }
                    }
                }
            }
        }
    }

    public boolean canStructuresSpawn()
    {
        return false;
    }

    public WorldSettings.GameType getGameType()
    {
        return this.theWorldSettings.getGameType();
    }

    /**
     * Get the server's difficulty
     */
    public EnumDifficulty getDifficulty()
    {
        return this.mc.theWorld.getWorldInfo().getDifficulty();
    }

    /**
     * Defaults to false.
     */
    public boolean isHardcore()
    {
        return this.theWorldSettings.getHardcoreEnabled();
    }

    /**
     * Get if RCON command events should be broadcast to ops
     */
    public boolean shouldBroadcastRconToOps()
    {
        return true;
    }

    /**
     * Get if console command events should be broadcast to ops
     */
    public boolean shouldBroadcastConsoleToOps()
    {
        return true;
    }

    public File getDataDirectory()
    {
        return this.mc.mcDataDir;
    }

    public boolean isDedicatedServer()
    {
        return false;
    }

    /**
     * Get if native transport should be used. Native transport means linux server performance improvements and
     * optimized packet sending/receiving on linux
     */
    public boolean shouldUseNativeTransport()
    {
        return false;
    }

    /**
     * Called on exit from the main run() loop.
     */
    protected void finalTick(CrashReport report)
    {
        this.mc.crashed(report);
    }

    /**
     * Adds the server info, including from theWorldServer, to the crash report.
     */
    public CrashReport addServerInfoToCrashReport(CrashReport report)
    {
        report = super.addServerInfoToCrashReport(report);
        report.getCategory().addCrashSectionCallable("Type", new Callable<String>()
        {
            public String call() throws Exception
            {
                return "Integrated Server (map_client.txt)";
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        report.getCategory().addCrashSectionCallable("Is Modded", new Callable<String>()
        {
            public String call() throws Exception
            {
                String lvt_1_1_ = ClientBrandRetriever.getClientModName();

                if (!lvt_1_1_.equals("vanilla"))
                {
                    return "Definitely; Client brand changed to \'" + lvt_1_1_ + "\'";
                }
                else
                {
                    lvt_1_1_ = IntegratedServer.this.getServerModName();
                    return !lvt_1_1_.equals("vanilla") ? "Definitely; Server brand changed to \'" + lvt_1_1_ + "\'" : (Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and both client + server brands are untouched.");
                }
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        return report;
    }

    public void setDifficultyForAllWorlds(EnumDifficulty difficulty)
    {
        super.setDifficultyForAllWorlds(difficulty);

        if (this.mc.theWorld != null)
        {
            this.mc.theWorld.getWorldInfo().setDifficulty(difficulty);
        }
    }

    public void addServerStatsToSnooper(PlayerUsageSnooper playerSnooper)
    {
        super.addServerStatsToSnooper(playerSnooper);
        playerSnooper.addClientStat("snooper_partner", this.mc.getPlayerUsageSnooper().getUniqueID());
    }

    /**
     * Returns whether snooping is enabled or not.
     */
    public boolean isSnooperEnabled()
    {
        return Minecraft.getMinecraft().isSnooperEnabled();
    }

    /**
     * On dedicated does nothing. On integrated, sets commandsAllowedForAll, gameType and allows external connections.
     */
    public String shareToLAN(WorldSettings.GameType type, boolean allowCheats)
    {
        try
        {
            int lvt_3_1_ = -1;

            try
            {
                lvt_3_1_ = HttpUtil.getSuitableLanPort();
            }
            catch (IOException var5)
            {
                ;
            }

            if (lvt_3_1_ <= 0)
            {
                lvt_3_1_ = 25564;
            }

            this.getNetworkSystem().addLanEndpoint((InetAddress)null, lvt_3_1_);
            logger.info("Started on " + lvt_3_1_);
            this.isPublic = true;
            this.lanServerPing = new ThreadLanServerPing(this.getMOTD(), lvt_3_1_ + "");
            this.lanServerPing.start();
            this.getConfigurationManager().setGameType(type);
            this.getConfigurationManager().setCommandsAllowedForAll(allowCheats);
            return lvt_3_1_ + "";
        }
        catch (IOException var6)
        {
            return null;
        }
    }

    /**
     * Saves all necessary data as preparation for stopping the server.
     */
    public void stopServer()
    {
        super.stopServer();

        if (this.lanServerPing != null)
        {
            this.lanServerPing.interrupt();
            this.lanServerPing = null;
        }
    }

    /**
     * Sets the serverRunning variable to false, in order to get the server to shut down.
     */
    public void initiateShutdown()
    {
        Futures.getUnchecked(this.addScheduledTask(new Runnable()
        {
            public void run()
            {
                for (EntityPlayerMP lvt_3_1_ : Lists.newArrayList(IntegratedServer.this.getConfigurationManager().getPlayerList()))
                {
                    IntegratedServer.this.getConfigurationManager().playerLoggedOut(lvt_3_1_);
                }
            }
        }));
        super.initiateShutdown();

        if (this.lanServerPing != null)
        {
            this.lanServerPing.interrupt();
            this.lanServerPing = null;
        }
    }

    public void setStaticInstance()
    {
        this.setInstance();
    }

    /**
     * Returns true if this integrated server is open to LAN
     */
    public boolean getPublic()
    {
        return this.isPublic;
    }

    /**
     * Sets the game type for all worlds.
     */
    public void setGameType(WorldSettings.GameType gameMode)
    {
        this.getConfigurationManager().setGameType(gameMode);
    }

    /**
     * Return whether command blocks are enabled.
     */
    public boolean isCommandBlockEnabled()
    {
        return true;
    }

    public int getOpPermissionLevel()
    {
        return 4;
    }
}
