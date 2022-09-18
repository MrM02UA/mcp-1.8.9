package net.minecraft.command.server;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class CommandWhitelist extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "whitelist";
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
        return "commands.whitelist.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.whitelist.usage", new Object[0]);
        }
        else
        {
            MinecraftServer lvt_3_1_ = MinecraftServer.getServer();

            if (args[0].equals("on"))
            {
                lvt_3_1_.getConfigurationManager().setWhiteListEnabled(true);
                notifyOperators(sender, this, "commands.whitelist.enabled", new Object[0]);
            }
            else if (args[0].equals("off"))
            {
                lvt_3_1_.getConfigurationManager().setWhiteListEnabled(false);
                notifyOperators(sender, this, "commands.whitelist.disabled", new Object[0]);
            }
            else if (args[0].equals("list"))
            {
                sender.addChatMessage(new ChatComponentTranslation("commands.whitelist.list", new Object[] {Integer.valueOf(lvt_3_1_.getConfigurationManager().getWhitelistedPlayerNames().length), Integer.valueOf(lvt_3_1_.getConfigurationManager().getAvailablePlayerDat().length)}));
                String[] lvt_4_1_ = lvt_3_1_.getConfigurationManager().getWhitelistedPlayerNames();
                sender.addChatMessage(new ChatComponentText(joinNiceString(lvt_4_1_)));
            }
            else if (args[0].equals("add"))
            {
                if (args.length < 2)
                {
                    throw new WrongUsageException("commands.whitelist.add.usage", new Object[0]);
                }

                GameProfile lvt_4_2_ = lvt_3_1_.getPlayerProfileCache().getGameProfileForUsername(args[1]);

                if (lvt_4_2_ == null)
                {
                    throw new CommandException("commands.whitelist.add.failed", new Object[] {args[1]});
                }

                lvt_3_1_.getConfigurationManager().addWhitelistedPlayer(lvt_4_2_);
                notifyOperators(sender, this, "commands.whitelist.add.success", new Object[] {args[1]});
            }
            else if (args[0].equals("remove"))
            {
                if (args.length < 2)
                {
                    throw new WrongUsageException("commands.whitelist.remove.usage", new Object[0]);
                }

                GameProfile lvt_4_3_ = lvt_3_1_.getConfigurationManager().getWhitelistedPlayers().getBannedProfile(args[1]);

                if (lvt_4_3_ == null)
                {
                    throw new CommandException("commands.whitelist.remove.failed", new Object[] {args[1]});
                }

                lvt_3_1_.getConfigurationManager().removePlayerFromWhitelist(lvt_4_3_);
                notifyOperators(sender, this, "commands.whitelist.remove.success", new Object[] {args[1]});
            }
            else if (args[0].equals("reload"))
            {
                lvt_3_1_.getConfigurationManager().loadWhiteList();
                notifyOperators(sender, this, "commands.whitelist.reloaded", new Object[0]);
            }
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, new String[] {"on", "off", "list", "add", "remove", "reload"});
        }
        else
        {
            if (args.length == 2)
            {
                if (args[0].equals("remove"))
                {
                    return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getConfigurationManager().getWhitelistedPlayerNames());
                }

                if (args[0].equals("add"))
                {
                    return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getPlayerProfileCache().getUsernames());
                }
            }

            return null;
        }
    }
}
