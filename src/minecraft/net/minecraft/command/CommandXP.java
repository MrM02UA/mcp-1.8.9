package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandXP extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "xp";
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
        return "commands.xp.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length <= 0)
        {
            throw new WrongUsageException("commands.xp.usage", new Object[0]);
        }
        else
        {
            String lvt_3_1_ = args[0];
            boolean lvt_4_1_ = lvt_3_1_.endsWith("l") || lvt_3_1_.endsWith("L");

            if (lvt_4_1_ && lvt_3_1_.length() > 1)
            {
                lvt_3_1_ = lvt_3_1_.substring(0, lvt_3_1_.length() - 1);
            }

            int lvt_5_1_ = parseInt(lvt_3_1_);
            boolean lvt_6_1_ = lvt_5_1_ < 0;

            if (lvt_6_1_)
            {
                lvt_5_1_ *= -1;
            }

            EntityPlayer lvt_7_1_ = args.length > 1 ? getPlayer(sender, args[1]) : getCommandSenderAsPlayer(sender);

            if (lvt_4_1_)
            {
                sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, lvt_7_1_.experienceLevel);

                if (lvt_6_1_)
                {
                    lvt_7_1_.addExperienceLevel(-lvt_5_1_);
                    notifyOperators(sender, this, "commands.xp.success.negative.levels", new Object[] {Integer.valueOf(lvt_5_1_), lvt_7_1_.getName()});
                }
                else
                {
                    lvt_7_1_.addExperienceLevel(lvt_5_1_);
                    notifyOperators(sender, this, "commands.xp.success.levels", new Object[] {Integer.valueOf(lvt_5_1_), lvt_7_1_.getName()});
                }
            }
            else
            {
                sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, lvt_7_1_.experienceTotal);

                if (lvt_6_1_)
                {
                    throw new CommandException("commands.xp.failure.widthdrawXp", new Object[0]);
                }

                lvt_7_1_.addExperience(lvt_5_1_);
                notifyOperators(sender, this, "commands.xp.success", new Object[] {Integer.valueOf(lvt_5_1_), lvt_7_1_.getName()});
            }
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length == 2 ? getListOfStringsMatchingLastWord(args, this.getAllUsernames()) : null;
    }

    protected String[] getAllUsernames()
    {
        return MinecraftServer.getServer().getAllUsernames();
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 1;
    }
}
