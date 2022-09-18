package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.WorldServer;

public class CommandTime extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "time";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    /**
     * Gets the usage string for the command.
     */
    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.time.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length > 1)
        {
            if (args[0].equals("set"))
            {
                int lvt_3_1_;

                if (args[1].equals("day"))
                {
                    lvt_3_1_ = 1000;
                }
                else if (args[1].equals("night"))
                {
                    lvt_3_1_ = 13000;
                }
                else
                {
                    lvt_3_1_ = parseInt(args[1], 0);
                }

                this.setTime(sender, lvt_3_1_);
                notifyOperators(sender, this, "commands.time.set", new Object[] {Integer.valueOf(lvt_3_1_)});
                return;
            }

            if (args[0].equals("add"))
            {
                int lvt_3_4_ = parseInt(args[1], 0);
                this.addTime(sender, lvt_3_4_);
                notifyOperators(sender, this, "commands.time.added", new Object[] {Integer.valueOf(lvt_3_4_)});
                return;
            }

            if (args[0].equals("query"))
            {
                if (args[1].equals("daytime"))
                {
                    int lvt_3_5_ = (int)(sender.getEntityWorld().getWorldTime() % 2147483647L);
                    sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, lvt_3_5_);
                    notifyOperators(sender, this, "commands.time.query", new Object[] {Integer.valueOf(lvt_3_5_)});
                    return;
                }

                if (args[1].equals("gametime"))
                {
                    int lvt_3_6_ = (int)(sender.getEntityWorld().getTotalWorldTime() % 2147483647L);
                    sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, lvt_3_6_);
                    notifyOperators(sender, this, "commands.time.query", new Object[] {Integer.valueOf(lvt_3_6_)});
                    return;
                }
            }
        }

        throw new WrongUsageException("commands.time.usage", new Object[0]);
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[] {"set", "add", "query"}): (args.length == 2 && args[0].equals("set") ? getListOfStringsMatchingLastWord(args, new String[] {"day", "night"}): (args.length == 2 && args[0].equals("query") ? getListOfStringsMatchingLastWord(args, new String[] {"daytime", "gametime"}): null));
    }

    /**
     * Set the time in the server object.
     */
    protected void setTime(ICommandSender sender, int time)
    {
        for (int lvt_3_1_ = 0; lvt_3_1_ < MinecraftServer.getServer().worldServers.length; ++lvt_3_1_)
        {
            MinecraftServer.getServer().worldServers[lvt_3_1_].setWorldTime((long)time);
        }
    }

    /**
     * Adds (or removes) time in the server object.
     */
    protected void addTime(ICommandSender sender, int time)
    {
        for (int lvt_3_1_ = 0; lvt_3_1_ < MinecraftServer.getServer().worldServers.length; ++lvt_3_1_)
        {
            WorldServer lvt_4_1_ = MinecraftServer.getServer().worldServers[lvt_3_1_];
            lvt_4_1_.setWorldTime(lvt_4_1_.getWorldTime() + (long)time);
        }
    }
}
