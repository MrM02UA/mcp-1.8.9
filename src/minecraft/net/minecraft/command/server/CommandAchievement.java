package net.minecraft.command.server;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;

public class CommandAchievement extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "achievement";
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
        return "commands.achievement.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException("commands.achievement.usage", new Object[0]);
        }
        else
        {
            final StatBase lvt_3_1_ = StatList.getOneShotStat(args[1]);

            if (lvt_3_1_ == null && !args[1].equals("*"))
            {
                throw new CommandException("commands.achievement.unknownAchievement", new Object[] {args[1]});
            }
            else
            {
                final EntityPlayerMP lvt_4_1_ = args.length >= 3 ? getPlayer(sender, args[2]) : getCommandSenderAsPlayer(sender);
                boolean lvt_5_1_ = args[0].equalsIgnoreCase("give");
                boolean lvt_6_1_ = args[0].equalsIgnoreCase("take");

                if (lvt_5_1_ || lvt_6_1_)
                {
                    if (lvt_3_1_ == null)
                    {
                        if (lvt_5_1_)
                        {
                            for (Achievement lvt_8_1_ : AchievementList.achievementList)
                            {
                                lvt_4_1_.triggerAchievement(lvt_8_1_);
                            }

                            notifyOperators(sender, this, "commands.achievement.give.success.all", new Object[] {lvt_4_1_.getName()});
                        }
                        else if (lvt_6_1_)
                        {
                            for (Achievement lvt_8_2_ : Lists.reverse(AchievementList.achievementList))
                            {
                                lvt_4_1_.func_175145_a(lvt_8_2_);
                            }

                            notifyOperators(sender, this, "commands.achievement.take.success.all", new Object[] {lvt_4_1_.getName()});
                        }
                    }
                    else
                    {
                        if (lvt_3_1_ instanceof Achievement)
                        {
                            Achievement lvt_7_3_ = (Achievement)lvt_3_1_;

                            if (lvt_5_1_)
                            {
                                if (lvt_4_1_.getStatFile().hasAchievementUnlocked(lvt_7_3_))
                                {
                                    throw new CommandException("commands.achievement.alreadyHave", new Object[] {lvt_4_1_.getName(), lvt_3_1_.createChatComponent()});
                                }

                                List<Achievement> lvt_8_3_;

                                for (lvt_8_3_ = Lists.newArrayList(); lvt_7_3_.parentAchievement != null && !lvt_4_1_.getStatFile().hasAchievementUnlocked(lvt_7_3_.parentAchievement); lvt_7_3_ = lvt_7_3_.parentAchievement)
                                {
                                    lvt_8_3_.add(lvt_7_3_.parentAchievement);
                                }

                                for (Achievement lvt_10_1_ : Lists.reverse(lvt_8_3_))
                                {
                                    lvt_4_1_.triggerAchievement(lvt_10_1_);
                                }
                            }
                            else if (lvt_6_1_)
                            {
                                if (!lvt_4_1_.getStatFile().hasAchievementUnlocked(lvt_7_3_))
                                {
                                    throw new CommandException("commands.achievement.dontHave", new Object[] {lvt_4_1_.getName(), lvt_3_1_.createChatComponent()});
                                }

                                List<Achievement> lvt_8_4_ = Lists.newArrayList(Iterators.filter(AchievementList.achievementList.iterator(), new Predicate<Achievement>()
                                {
                                    public boolean apply(Achievement p_apply_1_)
                                    {
                                        return lvt_4_1_.getStatFile().hasAchievementUnlocked(p_apply_1_) && p_apply_1_ != lvt_3_1_;
                                    }
                                    public boolean apply(Object p_apply_1_)
                                    {
                                        return this.apply((Achievement)p_apply_1_);
                                    }
                                }));
                                List<Achievement> lvt_9_2_ = Lists.newArrayList(lvt_8_4_);

                                for (Achievement lvt_11_1_ : lvt_8_4_)
                                {
                                    Achievement lvt_12_1_ = lvt_11_1_;
                                    boolean lvt_13_1_;

                                    for (lvt_13_1_ = false; lvt_12_1_ != null; lvt_12_1_ = lvt_12_1_.parentAchievement)
                                    {
                                        if (lvt_12_1_ == lvt_3_1_)
                                        {
                                            lvt_13_1_ = true;
                                        }
                                    }

                                    if (!lvt_13_1_)
                                    {
                                        for (lvt_12_1_ = lvt_11_1_; lvt_12_1_ != null; lvt_12_1_ = lvt_12_1_.parentAchievement)
                                        {
                                            lvt_9_2_.remove(lvt_11_1_);
                                        }
                                    }
                                }

                                for (Achievement lvt_11_2_ : lvt_9_2_)
                                {
                                    lvt_4_1_.func_175145_a(lvt_11_2_);
                                }
                            }
                        }

                        if (lvt_5_1_)
                        {
                            lvt_4_1_.triggerAchievement(lvt_3_1_);
                            notifyOperators(sender, this, "commands.achievement.give.success.one", new Object[] {lvt_4_1_.getName(), lvt_3_1_.createChatComponent()});
                        }
                        else if (lvt_6_1_)
                        {
                            lvt_4_1_.func_175145_a(lvt_3_1_);
                            notifyOperators(sender, this, "commands.achievement.take.success.one", new Object[] {lvt_3_1_.createChatComponent(), lvt_4_1_.getName()});
                        }
                    }
                }
            }
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, new String[] {"give", "take"});
        }
        else if (args.length != 2)
        {
            return args.length == 3 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
        }
        else
        {
            List<String> lvt_4_1_ = Lists.newArrayList();

            for (StatBase lvt_6_1_ : StatList.allStats)
            {
                lvt_4_1_.add(lvt_6_1_.statId);
            }

            return getListOfStringsMatchingLastWord(args, lvt_4_1_);
        }
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 2;
    }
}
