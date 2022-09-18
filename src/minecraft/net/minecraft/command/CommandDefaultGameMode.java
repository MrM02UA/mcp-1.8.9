package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.WorldSettings;

public class CommandDefaultGameMode extends CommandGameMode
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "defaultgamemode";
    }

    /**
     * Gets the usage string for the command.
     */
    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.defaultgamemode.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length <= 0)
        {
            throw new WrongUsageException("commands.defaultgamemode.usage", new Object[0]);
        }
        else
        {
            WorldSettings.GameType lvt_3_1_ = this.getGameModeFromCommand(sender, args[0]);
            this.setGameType(lvt_3_1_);
            notifyOperators(sender, this, "commands.defaultgamemode.success", new Object[] {new ChatComponentTranslation("gameMode." + lvt_3_1_.getName(), new Object[0])});
        }
    }

    protected void setGameType(WorldSettings.GameType gameMode)
    {
        MinecraftServer lvt_2_1_ = MinecraftServer.getServer();
        lvt_2_1_.setGameType(gameMode);

        if (lvt_2_1_.getForceGamemode())
        {
            for (EntityPlayerMP lvt_4_1_ : MinecraftServer.getServer().getConfigurationManager().getPlayerList())
            {
                lvt_4_1_.setGameType(gameMode);
                lvt_4_1_.fallDistance = 0.0F;
            }
        }
    }
}
