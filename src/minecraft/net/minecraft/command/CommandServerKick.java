package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandServerKick extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "kick";
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
        return "commands.kick.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length > 0 && args[0].length() > 1)
        {
            EntityPlayerMP lvt_3_1_ = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(args[0]);
            String lvt_4_1_ = "Kicked by an operator.";
            boolean lvt_5_1_ = false;

            if (lvt_3_1_ == null)
            {
                throw new PlayerNotFoundException();
            }
            else
            {
                if (args.length >= 2)
                {
                    lvt_4_1_ = getChatComponentFromNthArg(sender, args, 1).getUnformattedText();
                    lvt_5_1_ = true;
                }

                lvt_3_1_.playerNetServerHandler.kickPlayerFromServer(lvt_4_1_);

                if (lvt_5_1_)
                {
                    notifyOperators(sender, this, "commands.kick.success.reason", new Object[] {lvt_3_1_.getName(), lvt_4_1_});
                }
                else
                {
                    notifyOperators(sender, this, "commands.kick.success", new Object[] {lvt_3_1_.getName()});
                }
            }
        }
        else
        {
            throw new WrongUsageException("commands.kick.usage", new Object[0]);
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length >= 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
    }
}
