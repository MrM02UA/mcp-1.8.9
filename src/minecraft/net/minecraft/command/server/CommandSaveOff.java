package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class CommandSaveOff extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "save-off";
    }

    /**
     * Gets the usage string for the command.
     */
    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.save-off.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        MinecraftServer lvt_3_1_ = MinecraftServer.getServer();
        boolean lvt_4_1_ = false;

        for (int lvt_5_1_ = 0; lvt_5_1_ < lvt_3_1_.worldServers.length; ++lvt_5_1_)
        {
            if (lvt_3_1_.worldServers[lvt_5_1_] != null)
            {
                WorldServer lvt_6_1_ = lvt_3_1_.worldServers[lvt_5_1_];

                if (!lvt_6_1_.disableLevelSaving)
                {
                    lvt_6_1_.disableLevelSaving = true;
                    lvt_4_1_ = true;
                }
            }
        }

        if (lvt_4_1_)
        {
            notifyOperators(sender, this, "commands.save.disabled", new Object[0]);
        }
        else
        {
            throw new CommandException("commands.save-off.alreadyOff", new Object[0]);
        }
    }
}
