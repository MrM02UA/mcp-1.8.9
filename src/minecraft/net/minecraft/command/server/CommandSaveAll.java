package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;

public class CommandSaveAll extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "save-all";
    }

    /**
     * Gets the usage string for the command.
     */
    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.save.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        MinecraftServer lvt_3_1_ = MinecraftServer.getServer();
        sender.addChatMessage(new ChatComponentTranslation("commands.save.start", new Object[0]));

        if (lvt_3_1_.getConfigurationManager() != null)
        {
            lvt_3_1_.getConfigurationManager().saveAllPlayerData();
        }

        try
        {
            for (int lvt_4_1_ = 0; lvt_4_1_ < lvt_3_1_.worldServers.length; ++lvt_4_1_)
            {
                if (lvt_3_1_.worldServers[lvt_4_1_] != null)
                {
                    WorldServer lvt_5_1_ = lvt_3_1_.worldServers[lvt_4_1_];
                    boolean lvt_6_1_ = lvt_5_1_.disableLevelSaving;
                    lvt_5_1_.disableLevelSaving = false;
                    lvt_5_1_.saveAllChunks(true, (IProgressUpdate)null);
                    lvt_5_1_.disableLevelSaving = lvt_6_1_;
                }
            }

            if (args.length > 0 && "flush".equals(args[0]))
            {
                sender.addChatMessage(new ChatComponentTranslation("commands.save.flushStart", new Object[0]));

                for (int lvt_4_2_ = 0; lvt_4_2_ < lvt_3_1_.worldServers.length; ++lvt_4_2_)
                {
                    if (lvt_3_1_.worldServers[lvt_4_2_] != null)
                    {
                        WorldServer lvt_5_2_ = lvt_3_1_.worldServers[lvt_4_2_];
                        boolean lvt_6_2_ = lvt_5_2_.disableLevelSaving;
                        lvt_5_2_.disableLevelSaving = false;
                        lvt_5_2_.saveChunkData();
                        lvt_5_2_.disableLevelSaving = lvt_6_2_;
                    }
                }

                sender.addChatMessage(new ChatComponentTranslation("commands.save.flushEnd", new Object[0]));
            }
        }
        catch (MinecraftException var7)
        {
            notifyOperators(sender, this, "commands.save.failed", new Object[] {var7.getMessage()});
            return;
        }

        notifyOperators(sender, this, "commands.save.success", new Object[0]);
    }
}
