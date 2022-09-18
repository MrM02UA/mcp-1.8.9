package net.minecraft.command;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandTrigger extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "trigger";
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
        return "commands.trigger.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 3)
        {
            throw new WrongUsageException("commands.trigger.usage", new Object[0]);
        }
        else
        {
            EntityPlayerMP lvt_3_1_;

            if (sender instanceof EntityPlayerMP)
            {
                lvt_3_1_ = (EntityPlayerMP)sender;
            }
            else
            {
                Entity lvt_4_1_ = sender.getCommandSenderEntity();

                if (!(lvt_4_1_ instanceof EntityPlayerMP))
                {
                    throw new CommandException("commands.trigger.invalidPlayer", new Object[0]);
                }

                lvt_3_1_ = (EntityPlayerMP)lvt_4_1_;
            }

            Scoreboard lvt_4_2_ = MinecraftServer.getServer().worldServerForDimension(0).getScoreboard();
            ScoreObjective lvt_5_1_ = lvt_4_2_.getObjective(args[0]);

            if (lvt_5_1_ != null && lvt_5_1_.getCriteria() == IScoreObjectiveCriteria.TRIGGER)
            {
                int lvt_6_1_ = parseInt(args[2]);

                if (!lvt_4_2_.entityHasObjective(lvt_3_1_.getName(), lvt_5_1_))
                {
                    throw new CommandException("commands.trigger.invalidObjective", new Object[] {args[0]});
                }
                else
                {
                    Score lvt_7_1_ = lvt_4_2_.getValueFromObjective(lvt_3_1_.getName(), lvt_5_1_);

                    if (lvt_7_1_.isLocked())
                    {
                        throw new CommandException("commands.trigger.disabled", new Object[] {args[0]});
                    }
                    else
                    {
                        if ("set".equals(args[1]))
                        {
                            lvt_7_1_.setScorePoints(lvt_6_1_);
                        }
                        else
                        {
                            if (!"add".equals(args[1]))
                            {
                                throw new CommandException("commands.trigger.invalidMode", new Object[] {args[1]});
                            }

                            lvt_7_1_.increseScore(lvt_6_1_);
                        }

                        lvt_7_1_.setLocked(true);

                        if (lvt_3_1_.theItemInWorldManager.isCreative())
                        {
                            notifyOperators(sender, this, "commands.trigger.success", new Object[] {args[0], args[1], args[2]});
                        }
                    }
                }
            }
            else
            {
                throw new CommandException("commands.trigger.invalidObjective", new Object[] {args[0]});
            }
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            Scoreboard lvt_4_1_ = MinecraftServer.getServer().worldServerForDimension(0).getScoreboard();
            List<String> lvt_5_1_ = Lists.newArrayList();

            for (ScoreObjective lvt_7_1_ : lvt_4_1_.getScoreObjectives())
            {
                if (lvt_7_1_.getCriteria() == IScoreObjectiveCriteria.TRIGGER)
                {
                    lvt_5_1_.add(lvt_7_1_.getName());
                }
            }

            return getListOfStringsMatchingLastWord(args, (String[])lvt_5_1_.toArray(new String[lvt_5_1_.size()]));
        }
        else
        {
            return args.length == 2 ? getListOfStringsMatchingLastWord(args, new String[] {"add", "set"}): null;
        }
    }
}
