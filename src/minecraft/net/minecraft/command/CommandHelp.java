package net.minecraft.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

public class CommandHelp extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "help";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    /**
     * Gets the usage string for the command.
     */
    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.help.usage";
    }

    public List<String> getCommandAliases()
    {
        return Arrays.asList(new String[] {"?"});
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        List<ICommand> lvt_3_1_ = this.getSortedPossibleCommands(sender);
        int lvt_4_1_ = 7;
        int lvt_5_1_ = (lvt_3_1_.size() - 1) / 7;
        int lvt_6_1_ = 0;

        try
        {
            lvt_6_1_ = args.length == 0 ? 0 : parseInt(args[0], 1, lvt_5_1_ + 1) - 1;
        }
        catch (NumberInvalidException var12)
        {
            Map<String, ICommand> lvt_8_1_ = this.getCommands();
            ICommand lvt_9_1_ = (ICommand)lvt_8_1_.get(args[0]);

            if (lvt_9_1_ != null)
            {
                throw new WrongUsageException(lvt_9_1_.getCommandUsage(sender), new Object[0]);
            }

            if (MathHelper.parseIntWithDefault(args[0], -1) != -1)
            {
                throw var12;
            }

            throw new CommandNotFoundException();
        }

        int lvt_7_2_ = Math.min((lvt_6_1_ + 1) * 7, lvt_3_1_.size());
        ChatComponentTranslation lvt_8_2_ = new ChatComponentTranslation("commands.help.header", new Object[] {Integer.valueOf(lvt_6_1_ + 1), Integer.valueOf(lvt_5_1_ + 1)});
        lvt_8_2_.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
        sender.addChatMessage(lvt_8_2_);

        for (int lvt_9_2_ = lvt_6_1_ * 7; lvt_9_2_ < lvt_7_2_; ++lvt_9_2_)
        {
            ICommand lvt_10_1_ = (ICommand)lvt_3_1_.get(lvt_9_2_);
            ChatComponentTranslation lvt_11_1_ = new ChatComponentTranslation(lvt_10_1_.getCommandUsage(sender), new Object[0]);
            lvt_11_1_.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + lvt_10_1_.getCommandName() + " "));
            sender.addChatMessage(lvt_11_1_);
        }

        if (lvt_6_1_ == 0 && sender instanceof EntityPlayer)
        {
            ChatComponentTranslation lvt_9_3_ = new ChatComponentTranslation("commands.help.footer", new Object[0]);
            lvt_9_3_.getChatStyle().setColor(EnumChatFormatting.GREEN);
            sender.addChatMessage(lvt_9_3_);
        }
    }

    protected List<ICommand> getSortedPossibleCommands(ICommandSender p_71534_1_)
    {
        List<ICommand> lvt_2_1_ = MinecraftServer.getServer().getCommandManager().getPossibleCommands(p_71534_1_);
        Collections.sort(lvt_2_1_);
        return lvt_2_1_;
    }

    protected Map<String, ICommand> getCommands()
    {
        return MinecraftServer.getServer().getCommandManager().getCommands();
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            Set<String> lvt_4_1_ = this.getCommands().keySet();
            return getListOfStringsMatchingLastWord(args, (String[])lvt_4_1_.toArray(new String[lvt_4_1_.size()]));
        }
        else
        {
            return null;
        }
    }
}
