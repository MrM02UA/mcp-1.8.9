package net.minecraft.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandHandler implements ICommandManager
{
    private static final Logger logger = LogManager.getLogger();
    private final Map<String, ICommand> commandMap = Maps.newHashMap();
    private final Set<ICommand> commandSet = Sets.newHashSet();

    /**
     * Attempt to execute a command. This method should return the number of times that the command was executed. If the
     * command does not exist or if the player does not have permission, 0 will be returned. A number greater than 1 can
     * be returned if a player selector is used.
     *  
     * @param sender The person who executed the command. This could be an EntityPlayer, RCon Source, Command Block,
     * etc.
     * @param rawCommand The raw arguments that were passed. This includes the command name.
     */
    public int executeCommand(ICommandSender sender, String rawCommand)
    {
        rawCommand = rawCommand.trim();

        if (rawCommand.startsWith("/"))
        {
            rawCommand = rawCommand.substring(1);
        }

        String[] lvt_3_1_ = rawCommand.split(" ");
        String lvt_4_1_ = lvt_3_1_[0];
        lvt_3_1_ = dropFirstString(lvt_3_1_);
        ICommand lvt_5_1_ = (ICommand)this.commandMap.get(lvt_4_1_);
        int lvt_6_1_ = this.getUsernameIndex(lvt_5_1_, lvt_3_1_);
        int lvt_7_1_ = 0;

        if (lvt_5_1_ == null)
        {
            ChatComponentTranslation lvt_8_1_ = new ChatComponentTranslation("commands.generic.notFound", new Object[0]);
            lvt_8_1_.getChatStyle().setColor(EnumChatFormatting.RED);
            sender.addChatMessage(lvt_8_1_);
        }
        else if (lvt_5_1_.canCommandSenderUseCommand(sender))
        {
            if (lvt_6_1_ > -1)
            {
                List<Entity> lvt_8_2_ = PlayerSelector.<Entity>matchEntities(sender, lvt_3_1_[lvt_6_1_], Entity.class);
                String lvt_9_1_ = lvt_3_1_[lvt_6_1_];
                sender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, lvt_8_2_.size());

                for (Entity lvt_11_1_ : lvt_8_2_)
                {
                    lvt_3_1_[lvt_6_1_] = lvt_11_1_.getUniqueID().toString();

                    if (this.tryExecute(sender, lvt_3_1_, lvt_5_1_, rawCommand))
                    {
                        ++lvt_7_1_;
                    }
                }

                lvt_3_1_[lvt_6_1_] = lvt_9_1_;
            }
            else
            {
                sender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, 1);

                if (this.tryExecute(sender, lvt_3_1_, lvt_5_1_, rawCommand))
                {
                    ++lvt_7_1_;
                }
            }
        }
        else
        {
            ChatComponentTranslation lvt_8_3_ = new ChatComponentTranslation("commands.generic.permission", new Object[0]);
            lvt_8_3_.getChatStyle().setColor(EnumChatFormatting.RED);
            sender.addChatMessage(lvt_8_3_);
        }

        sender.setCommandStat(CommandResultStats.Type.SUCCESS_COUNT, lvt_7_1_);
        return lvt_7_1_;
    }

    protected boolean tryExecute(ICommandSender sender, String[] args, ICommand command, String input)
    {
        try
        {
            command.processCommand(sender, args);
            return true;
        }
        catch (WrongUsageException var7)
        {
            ChatComponentTranslation lvt_6_1_ = new ChatComponentTranslation("commands.generic.usage", new Object[] {new ChatComponentTranslation(var7.getMessage(), var7.getErrorObjects())});
            lvt_6_1_.getChatStyle().setColor(EnumChatFormatting.RED);
            sender.addChatMessage(lvt_6_1_);
        }
        catch (CommandException var8)
        {
            ChatComponentTranslation lvt_6_2_ = new ChatComponentTranslation(var8.getMessage(), var8.getErrorObjects());
            lvt_6_2_.getChatStyle().setColor(EnumChatFormatting.RED);
            sender.addChatMessage(lvt_6_2_);
        }
        catch (Throwable var9)
        {
            ChatComponentTranslation lvt_6_3_ = new ChatComponentTranslation("commands.generic.exception", new Object[0]);
            lvt_6_3_.getChatStyle().setColor(EnumChatFormatting.RED);
            sender.addChatMessage(lvt_6_3_);
            logger.warn("Couldn\'t process command: \'" + input + "\'");
        }

        return false;
    }

    /**
     * adds the command and any aliases it has to the internal map of available commands
     */
    public ICommand registerCommand(ICommand command)
    {
        this.commandMap.put(command.getCommandName(), command);
        this.commandSet.add(command);

        for (String lvt_3_1_ : command.getCommandAliases())
        {
            ICommand lvt_4_1_ = (ICommand)this.commandMap.get(lvt_3_1_);

            if (lvt_4_1_ == null || !lvt_4_1_.getCommandName().equals(lvt_3_1_))
            {
                this.commandMap.put(lvt_3_1_, command);
            }
        }

        return command;
    }

    /**
     * creates a new array and sets elements 0..n-2 to be 0..n-1 of the input (n elements)
     */
    private static String[] dropFirstString(String[] input)
    {
        String[] lvt_1_1_ = new String[input.length - 1];
        System.arraycopy(input, 1, lvt_1_1_, 0, input.length - 1);
        return lvt_1_1_;
    }

    public List<String> getTabCompletionOptions(ICommandSender sender, String input, BlockPos pos)
    {
        String[] lvt_4_1_ = input.split(" ", -1);
        String lvt_5_1_ = lvt_4_1_[0];

        if (lvt_4_1_.length == 1)
        {
            List<String> lvt_6_1_ = Lists.newArrayList();

            for (Entry<String, ICommand> lvt_8_1_ : this.commandMap.entrySet())
            {
                if (CommandBase.doesStringStartWith(lvt_5_1_, (String)lvt_8_1_.getKey()) && ((ICommand)lvt_8_1_.getValue()).canCommandSenderUseCommand(sender))
                {
                    lvt_6_1_.add(lvt_8_1_.getKey());
                }
            }

            return lvt_6_1_;
        }
        else
        {
            if (lvt_4_1_.length > 1)
            {
                ICommand lvt_6_2_ = (ICommand)this.commandMap.get(lvt_5_1_);

                if (lvt_6_2_ != null && lvt_6_2_.canCommandSenderUseCommand(sender))
                {
                    return lvt_6_2_.addTabCompletionOptions(sender, dropFirstString(lvt_4_1_), pos);
                }
            }

            return null;
        }
    }

    public List<ICommand> getPossibleCommands(ICommandSender sender)
    {
        List<ICommand> lvt_2_1_ = Lists.newArrayList();

        for (ICommand lvt_4_1_ : this.commandSet)
        {
            if (lvt_4_1_.canCommandSenderUseCommand(sender))
            {
                lvt_2_1_.add(lvt_4_1_);
            }
        }

        return lvt_2_1_;
    }

    public Map<String, ICommand> getCommands()
    {
        return this.commandMap;
    }

    /**
     * Return a command's first parameter index containing a valid username.
     */
    private int getUsernameIndex(ICommand command, String[] args)
    {
        if (command == null)
        {
            return -1;
        }
        else
        {
            for (int lvt_3_1_ = 0; lvt_3_1_ < args.length; ++lvt_3_1_)
            {
                if (command.isUsernameIndex(args, lvt_3_1_) && PlayerSelector.matchesMultiplePlayers(args[lvt_3_1_]))
                {
                    return lvt_3_1_;
                }
            }

            return -1;
        }
    }
}
