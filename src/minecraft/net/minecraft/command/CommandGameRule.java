package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.GameRules;

public class CommandGameRule extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "gamerule";
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
        return "commands.gamerule.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        GameRules lvt_3_1_ = this.getGameRules();
        String lvt_4_1_ = args.length > 0 ? args[0] : "";
        String lvt_5_1_ = args.length > 1 ? buildString(args, 1) : "";

        switch (args.length)
        {
            case 0:
                sender.addChatMessage(new ChatComponentText(joinNiceString(lvt_3_1_.getRules())));
                break;

            case 1:
                if (!lvt_3_1_.hasRule(lvt_4_1_))
                {
                    throw new CommandException("commands.gamerule.norule", new Object[] {lvt_4_1_});
                }

                String lvt_6_1_ = lvt_3_1_.getString(lvt_4_1_);
                sender.addChatMessage((new ChatComponentText(lvt_4_1_)).appendText(" = ").appendText(lvt_6_1_));
                sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, lvt_3_1_.getInt(lvt_4_1_));
                break;

            default:
                if (lvt_3_1_.areSameType(lvt_4_1_, GameRules.ValueType.BOOLEAN_VALUE) && !"true".equals(lvt_5_1_) && !"false".equals(lvt_5_1_))
                {
                    throw new CommandException("commands.generic.boolean.invalid", new Object[] {lvt_5_1_});
                }

                lvt_3_1_.setOrCreateGameRule(lvt_4_1_, lvt_5_1_);
                func_175773_a(lvt_3_1_, lvt_4_1_);
                notifyOperators(sender, this, "commands.gamerule.success", new Object[0]);
        }
    }

    public static void func_175773_a(GameRules rules, String p_175773_1_)
    {
        if ("reducedDebugInfo".equals(p_175773_1_))
        {
            byte lvt_2_1_ = (byte)(rules.getBoolean(p_175773_1_) ? 22 : 23);

            for (EntityPlayerMP lvt_4_1_ : MinecraftServer.getServer().getConfigurationManager().getPlayerList())
            {
                lvt_4_1_.playerNetServerHandler.sendPacket(new S19PacketEntityStatus(lvt_4_1_, lvt_2_1_));
            }
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, this.getGameRules().getRules());
        }
        else
        {
            if (args.length == 2)
            {
                GameRules lvt_4_1_ = this.getGameRules();

                if (lvt_4_1_.areSameType(args[0], GameRules.ValueType.BOOLEAN_VALUE))
                {
                    return getListOfStringsMatchingLastWord(args, new String[] {"true", "false"});
                }
            }

            return null;
        }
    }

    /**
     * Return the game rule set this command should be able to manipulate.
     */
    private GameRules getGameRules()
    {
        return MinecraftServer.getServer().worldServerForDimension(0).getGameRules();
    }
}
