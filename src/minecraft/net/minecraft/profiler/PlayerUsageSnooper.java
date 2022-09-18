package net.minecraft.profiler;

import com.google.common.collect.Maps;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.util.HttpUtil;

public class PlayerUsageSnooper
{
    private final Map<String, Object> snooperStats = Maps.newHashMap();
    private final Map<String, Object> clientStats = Maps.newHashMap();
    private final String uniqueID = UUID.randomUUID().toString();

    /** URL of the server to send the report to */
    private final URL serverUrl;
    private final IPlayerUsage playerStatsCollector;

    /** set to fire the snooperThread every 15 mins */
    private final Timer threadTrigger = new Timer("Snooper Timer", true);
    private final Object syncLock = new Object();
    private final long minecraftStartTimeMilis;
    private boolean isRunning;

    /** incremented on every getSelfCounterFor */
    private int selfCounter;

    public PlayerUsageSnooper(String side, IPlayerUsage playerStatCollector, long startTime)
    {
        try
        {
            this.serverUrl = new URL("http://snoop.minecraft.net/" + side + "?version=" + 2);
        }
        catch (MalformedURLException var6)
        {
            throw new IllegalArgumentException();
        }

        this.playerStatsCollector = playerStatCollector;
        this.minecraftStartTimeMilis = startTime;
    }

    /**
     * Note issuing start multiple times is not an error.
     */
    public void startSnooper()
    {
        if (!this.isRunning)
        {
            this.isRunning = true;
            this.addOSData();
            this.threadTrigger.schedule(new TimerTask()
            {
                public void run()
                {
                    if (PlayerUsageSnooper.this.playerStatsCollector.isSnooperEnabled())
                    {
                        Map<String, Object> lvt_1_1_;

                        synchronized (PlayerUsageSnooper.this.syncLock)
                        {
                            lvt_1_1_ = Maps.newHashMap(PlayerUsageSnooper.this.clientStats);

                            if (PlayerUsageSnooper.this.selfCounter == 0)
                            {
                                lvt_1_1_.putAll(PlayerUsageSnooper.this.snooperStats);
                            }

                            lvt_1_1_.put("snooper_count", Integer.valueOf(PlayerUsageSnooper.this.selfCounter++));
                            lvt_1_1_.put("snooper_token", PlayerUsageSnooper.this.uniqueID);
                        }

                        HttpUtil.postMap(PlayerUsageSnooper.this.serverUrl, lvt_1_1_, true);
                    }
                }
            }, 0L, 900000L);
        }
    }

    /**
     * Add OS data into the snooper
     */
    private void addOSData()
    {
        this.addJvmArgsToSnooper();
        this.addClientStat("snooper_token", this.uniqueID);
        this.addStatToSnooper("snooper_token", this.uniqueID);
        this.addStatToSnooper("os_name", System.getProperty("os.name"));
        this.addStatToSnooper("os_version", System.getProperty("os.version"));
        this.addStatToSnooper("os_architecture", System.getProperty("os.arch"));
        this.addStatToSnooper("java_version", System.getProperty("java.version"));
        this.addClientStat("version", "1.8.9");
        this.playerStatsCollector.addServerTypeToSnooper(this);
    }

    private void addJvmArgsToSnooper()
    {
        RuntimeMXBean lvt_1_1_ = ManagementFactory.getRuntimeMXBean();
        List<String> lvt_2_1_ = lvt_1_1_.getInputArguments();
        int lvt_3_1_ = 0;

        for (String lvt_5_1_ : lvt_2_1_)
        {
            if (lvt_5_1_.startsWith("-X"))
            {
                this.addClientStat("jvm_arg[" + lvt_3_1_++ + "]", lvt_5_1_);
            }
        }

        this.addClientStat("jvm_args", Integer.valueOf(lvt_3_1_));
    }

    public void addMemoryStatsToSnooper()
    {
        this.addStatToSnooper("memory_total", Long.valueOf(Runtime.getRuntime().totalMemory()));
        this.addStatToSnooper("memory_max", Long.valueOf(Runtime.getRuntime().maxMemory()));
        this.addStatToSnooper("memory_free", Long.valueOf(Runtime.getRuntime().freeMemory()));
        this.addStatToSnooper("cpu_cores", Integer.valueOf(Runtime.getRuntime().availableProcessors()));
        this.playerStatsCollector.addServerStatsToSnooper(this);
    }

    public void addClientStat(String statName, Object statValue)
    {
        synchronized (this.syncLock)
        {
            this.clientStats.put(statName, statValue);
        }
    }

    public void addStatToSnooper(String statName, Object statValue)
    {
        synchronized (this.syncLock)
        {
            this.snooperStats.put(statName, statValue);
        }
    }

    public Map<String, String> getCurrentStats()
    {
        Map<String, String> lvt_1_1_ = Maps.newLinkedHashMap();

        synchronized (this.syncLock)
        {
            this.addMemoryStatsToSnooper();

            for (Entry<String, Object> lvt_4_1_ : this.snooperStats.entrySet())
            {
                lvt_1_1_.put(lvt_4_1_.getKey(), lvt_4_1_.getValue().toString());
            }

            for (Entry<String, Object> lvt_4_2_ : this.clientStats.entrySet())
            {
                lvt_1_1_.put(lvt_4_2_.getKey(), lvt_4_2_.getValue().toString());
            }

            return lvt_1_1_;
        }
    }

    public boolean isSnooperRunning()
    {
        return this.isRunning;
    }

    public void stopSnooper()
    {
        this.threadTrigger.cancel();
    }

    public String getUniqueID()
    {
        return this.uniqueID;
    }

    /**
     * Returns the saved value of System#currentTimeMillis when the game started
     */
    public long getMinecraftStartTimeMillis()
    {
        return this.minecraftStartTimeMilis;
    }
}
