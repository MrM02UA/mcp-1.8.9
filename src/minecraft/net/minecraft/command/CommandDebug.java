package net.minecraft.command;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandDebug extends CommandBase
{
    private static final Logger logger = LogManager.getLogger();

    /** The time (in milliseconds) that profiling was started */
    private long profileStartTime;

    /** The tick number that profiling was started on */
    private int profileStartTick;

    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "debug";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 3;
    }

    /**
     * Gets the usage string for the command.
     */
    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.debug.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.debug.usage", new Object[0]);
        }
        else
        {
            if (args[0].equals("start"))
            {
                if (args.length != 1)
                {
                    throw new WrongUsageException("commands.debug.usage", new Object[0]);
                }

                notifyOperators(sender, this, "commands.debug.start", new Object[0]);
                MinecraftServer.getServer().enableProfiling();
                this.profileStartTime = MinecraftServer.getCurrentTimeMillis();
                this.profileStartTick = MinecraftServer.getServer().getTickCounter();
            }
            else
            {
                if (!args[0].equals("stop"))
                {
                    throw new WrongUsageException("commands.debug.usage", new Object[0]);
                }

                if (args.length != 1)
                {
                    throw new WrongUsageException("commands.debug.usage", new Object[0]);
                }

                if (!MinecraftServer.getServer().theProfiler.profilingEnabled)
                {
                    throw new CommandException("commands.debug.notStarted", new Object[0]);
                }

                long lvt_3_1_ = MinecraftServer.getCurrentTimeMillis();
                int lvt_5_1_ = MinecraftServer.getServer().getTickCounter();
                long lvt_6_1_ = lvt_3_1_ - this.profileStartTime;
                int lvt_8_1_ = lvt_5_1_ - this.profileStartTick;
                this.saveProfileResults(lvt_6_1_, lvt_8_1_);
                MinecraftServer.getServer().theProfiler.profilingEnabled = false;
                notifyOperators(sender, this, "commands.debug.stop", new Object[] {Float.valueOf((float)lvt_6_1_ / 1000.0F), Integer.valueOf(lvt_8_1_)});
            }
        }
    }

    /**
     * Save the profiling results from the last profile
     */
    private void saveProfileResults(long timeSpan, int tickSpan)
    {
        File lvt_4_1_ = new File(MinecraftServer.getServer().getFile("debug"), "profile-results-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");
        lvt_4_1_.getParentFile().mkdirs();

        try
        {
            FileWriter lvt_5_1_ = new FileWriter(lvt_4_1_);
            lvt_5_1_.write(this.getProfileResults(timeSpan, tickSpan));
            lvt_5_1_.close();
        }
        catch (Throwable var6)
        {
            logger.error("Could not save profiler results to " + lvt_4_1_, var6);
        }
    }

    /**
     * Get the profiling results from the last profile
     */
    private String getProfileResults(long timeSpan, int tickSpan)
    {
        StringBuilder lvt_4_1_ = new StringBuilder();
        lvt_4_1_.append("---- Minecraft Profiler Results ----\n");
        lvt_4_1_.append("// ");
        lvt_4_1_.append(getWittyComment());
        lvt_4_1_.append("\n\n");
        lvt_4_1_.append("Time span: ").append(timeSpan).append(" ms\n");
        lvt_4_1_.append("Tick span: ").append(tickSpan).append(" ticks\n");
        lvt_4_1_.append("// This is approximately ").append(String.format("%.2f", new Object[] {Float.valueOf((float)tickSpan / ((float)timeSpan / 1000.0F))})).append(" ticks per second. It should be ").append(20).append(" ticks per second\n\n");
        lvt_4_1_.append("--- BEGIN PROFILE DUMP ---\n\n");
        this.func_147202_a(0, "root", lvt_4_1_);
        lvt_4_1_.append("--- END PROFILE DUMP ---\n\n");
        return lvt_4_1_.toString();
    }

    private void func_147202_a(int p_147202_1_, String p_147202_2_, StringBuilder stringBuilder)
    {
        List<Profiler.Result> lvt_4_1_ = MinecraftServer.getServer().theProfiler.getProfilingData(p_147202_2_);

        if (lvt_4_1_ != null && lvt_4_1_.size() >= 3)
        {
            for (int lvt_5_1_ = 1; lvt_5_1_ < lvt_4_1_.size(); ++lvt_5_1_)
            {
                Profiler.Result lvt_6_1_ = (Profiler.Result)lvt_4_1_.get(lvt_5_1_);
                stringBuilder.append(String.format("[%02d] ", new Object[] {Integer.valueOf(p_147202_1_)}));

                for (int lvt_7_1_ = 0; lvt_7_1_ < p_147202_1_; ++lvt_7_1_)
                {
                    stringBuilder.append(" ");
                }

                stringBuilder.append(lvt_6_1_.field_76331_c).append(" - ").append(String.format("%.2f", new Object[] {Double.valueOf(lvt_6_1_.field_76332_a)})).append("%/").append(String.format("%.2f", new Object[] {Double.valueOf(lvt_6_1_.field_76330_b)})).append("%\n");

                if (!lvt_6_1_.field_76331_c.equals("unspecified"))
                {
                    try
                    {
                        this.func_147202_a(p_147202_1_ + 1, p_147202_2_ + "." + lvt_6_1_.field_76331_c, stringBuilder);
                    }
                    catch (Exception var8)
                    {
                        stringBuilder.append("[[ EXCEPTION ").append(var8).append(" ]]");
                    }
                }
            }
        }
    }

    /**
     * Get a random witty comment
     */
    private static String getWittyComment()
    {
        String[] lvt_0_1_ = new String[] {"Shiny numbers!", "Am I not running fast enough? :(", "I\'m working as hard as I can!", "Will I ever be good enough for you? :(", "Speedy. Zoooooom!", "Hello world", "40% better than a crash report.", "Now with extra numbers", "Now with less numbers", "Now with the same numbers", "You should add flames to things, it makes them go faster!", "Do you feel the need for... optimization?", "*cracks redstone whip*", "Maybe if you treated it better then it\'ll have more motivation to work faster! Poor server."};

        try
        {
            return lvt_0_1_[(int)(System.nanoTime() % (long)lvt_0_1_.length)];
        }
        catch (Throwable var2)
        {
            return "Witty comment unavailable :(";
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[] {"start", "stop"}): null;
    }
}
