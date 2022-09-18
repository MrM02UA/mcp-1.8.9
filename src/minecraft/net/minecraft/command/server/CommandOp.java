package net.minecraft.command.server;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandOp extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "op";
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
        return "commands.op.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length == 1 && args[0].length() > 0)
        {
            MinecraftServer lvt_3_1_ = MinecraftServer.getServer();
            GameProfile lvt_4_1_ = lvt_3_1_.getPlayerProfileCache().getGameProfileForUsername(args[0]);

            if (lvt_4_1_ == null)
            {
                throw new CommandException("commands.op.failed", new Object[] {args[0]});
            }
            else
            {
                lvt_3_1_.getConfigurationManager().addOp(lvt_4_1_);
                notifyOperators(sender, this, "commands.op.success", new Object[] {args[0]});
            }
        }
        else
        {
            throw new WrongUsageException("commands.op.usage", new Object[0]);
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            String lvt_4_1_ = args[args.length - 1];
            List<String> lvt_5_1_ = Lists.newArrayList();

            for (GameProfile lvt_9_1_ : MinecraftServer.getServer().getGameProfiles())
            {
                if (!MinecraftServer.getServer().getConfigurationManager().canSendCommands(lvt_9_1_) && doesStringStartWith(lvt_4_1_, lvt_9_1_.getName()))
                {
                    lvt_5_1_.add(lvt_9_1_.getName());
                }
            }

            return lvt_5_1_;
        }
        else
        {
            return null;
        }
    }
}
