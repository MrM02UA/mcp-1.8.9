package net.minecraft.command.server;

import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListBansEntry;
import net.minecraft.util.BlockPos;

public class CommandBanPlayer extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "ban";
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
        return "commands.ban.usage";
    }

    /**
     * Returns true if the given command sender is allowed to use this command.
     */
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        return MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().isLanServer() && super.canCommandSenderUseCommand(sender);
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length >= 1 && args[0].length() > 0)
        {
            MinecraftServer lvt_3_1_ = MinecraftServer.getServer();
            GameProfile lvt_4_1_ = lvt_3_1_.getPlayerProfileCache().getGameProfileForUsername(args[0]);

            if (lvt_4_1_ == null)
            {
                throw new CommandException("commands.ban.failed", new Object[] {args[0]});
            }
            else
            {
                String lvt_5_1_ = null;

                if (args.length >= 2)
                {
                    lvt_5_1_ = getChatComponentFromNthArg(sender, args, 1).getUnformattedText();
                }

                UserListBansEntry lvt_6_1_ = new UserListBansEntry(lvt_4_1_, (Date)null, sender.getName(), (Date)null, lvt_5_1_);
                lvt_3_1_.getConfigurationManager().getBannedPlayers().addEntry(lvt_6_1_);
                EntityPlayerMP lvt_7_1_ = lvt_3_1_.getConfigurationManager().getPlayerByUsername(args[0]);

                if (lvt_7_1_ != null)
                {
                    lvt_7_1_.playerNetServerHandler.kickPlayerFromServer("You are banned from this server.");
                }

                notifyOperators(sender, this, "commands.ban.success", new Object[] {args[0]});
            }
        }
        else
        {
            throw new WrongUsageException("commands.ban.usage", new Object[0]);
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length >= 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
    }
}
