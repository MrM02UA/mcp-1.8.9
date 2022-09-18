package net.minecraft.command.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

public class CommandScoreboard extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "scoreboard";
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
        return "commands.scoreboard.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (!this.func_175780_b(sender, args))
        {
            if (args.length < 1)
            {
                throw new WrongUsageException("commands.scoreboard.usage", new Object[0]);
            }
            else
            {
                if (args[0].equalsIgnoreCase("objectives"))
                {
                    if (args.length == 1)
                    {
                        throw new WrongUsageException("commands.scoreboard.objectives.usage", new Object[0]);
                    }

                    if (args[1].equalsIgnoreCase("list"))
                    {
                        this.listObjectives(sender);
                    }
                    else if (args[1].equalsIgnoreCase("add"))
                    {
                        if (args.length < 4)
                        {
                            throw new WrongUsageException("commands.scoreboard.objectives.add.usage", new Object[0]);
                        }

                        this.addObjective(sender, args, 2);
                    }
                    else if (args[1].equalsIgnoreCase("remove"))
                    {
                        if (args.length != 3)
                        {
                            throw new WrongUsageException("commands.scoreboard.objectives.remove.usage", new Object[0]);
                        }

                        this.removeObjective(sender, args[2]);
                    }
                    else
                    {
                        if (!args[1].equalsIgnoreCase("setdisplay"))
                        {
                            throw new WrongUsageException("commands.scoreboard.objectives.usage", new Object[0]);
                        }

                        if (args.length != 3 && args.length != 4)
                        {
                            throw new WrongUsageException("commands.scoreboard.objectives.setdisplay.usage", new Object[0]);
                        }

                        this.setObjectiveDisplay(sender, args, 2);
                    }
                }
                else if (args[0].equalsIgnoreCase("players"))
                {
                    if (args.length == 1)
                    {
                        throw new WrongUsageException("commands.scoreboard.players.usage", new Object[0]);
                    }

                    if (args[1].equalsIgnoreCase("list"))
                    {
                        if (args.length > 3)
                        {
                            throw new WrongUsageException("commands.scoreboard.players.list.usage", new Object[0]);
                        }

                        this.listPlayers(sender, args, 2);
                    }
                    else if (args[1].equalsIgnoreCase("add"))
                    {
                        if (args.length < 5)
                        {
                            throw new WrongUsageException("commands.scoreboard.players.add.usage", new Object[0]);
                        }

                        this.setPlayer(sender, args, 2);
                    }
                    else if (args[1].equalsIgnoreCase("remove"))
                    {
                        if (args.length < 5)
                        {
                            throw new WrongUsageException("commands.scoreboard.players.remove.usage", new Object[0]);
                        }

                        this.setPlayer(sender, args, 2);
                    }
                    else if (args[1].equalsIgnoreCase("set"))
                    {
                        if (args.length < 5)
                        {
                            throw new WrongUsageException("commands.scoreboard.players.set.usage", new Object[0]);
                        }

                        this.setPlayer(sender, args, 2);
                    }
                    else if (args[1].equalsIgnoreCase("reset"))
                    {
                        if (args.length != 3 && args.length != 4)
                        {
                            throw new WrongUsageException("commands.scoreboard.players.reset.usage", new Object[0]);
                        }

                        this.resetPlayers(sender, args, 2);
                    }
                    else if (args[1].equalsIgnoreCase("enable"))
                    {
                        if (args.length != 4)
                        {
                            throw new WrongUsageException("commands.scoreboard.players.enable.usage", new Object[0]);
                        }

                        this.func_175779_n(sender, args, 2);
                    }
                    else if (args[1].equalsIgnoreCase("test"))
                    {
                        if (args.length != 5 && args.length != 6)
                        {
                            throw new WrongUsageException("commands.scoreboard.players.test.usage", new Object[0]);
                        }

                        this.func_175781_o(sender, args, 2);
                    }
                    else
                    {
                        if (!args[1].equalsIgnoreCase("operation"))
                        {
                            throw new WrongUsageException("commands.scoreboard.players.usage", new Object[0]);
                        }

                        if (args.length != 7)
                        {
                            throw new WrongUsageException("commands.scoreboard.players.operation.usage", new Object[0]);
                        }

                        this.func_175778_p(sender, args, 2);
                    }
                }
                else
                {
                    if (!args[0].equalsIgnoreCase("teams"))
                    {
                        throw new WrongUsageException("commands.scoreboard.usage", new Object[0]);
                    }

                    if (args.length == 1)
                    {
                        throw new WrongUsageException("commands.scoreboard.teams.usage", new Object[0]);
                    }

                    if (args[1].equalsIgnoreCase("list"))
                    {
                        if (args.length > 3)
                        {
                            throw new WrongUsageException("commands.scoreboard.teams.list.usage", new Object[0]);
                        }

                        this.listTeams(sender, args, 2);
                    }
                    else if (args[1].equalsIgnoreCase("add"))
                    {
                        if (args.length < 3)
                        {
                            throw new WrongUsageException("commands.scoreboard.teams.add.usage", new Object[0]);
                        }

                        this.addTeam(sender, args, 2);
                    }
                    else if (args[1].equalsIgnoreCase("remove"))
                    {
                        if (args.length != 3)
                        {
                            throw new WrongUsageException("commands.scoreboard.teams.remove.usage", new Object[0]);
                        }

                        this.removeTeam(sender, args, 2);
                    }
                    else if (args[1].equalsIgnoreCase("empty"))
                    {
                        if (args.length != 3)
                        {
                            throw new WrongUsageException("commands.scoreboard.teams.empty.usage", new Object[0]);
                        }

                        this.emptyTeam(sender, args, 2);
                    }
                    else if (args[1].equalsIgnoreCase("join"))
                    {
                        if (args.length < 4 && (args.length != 3 || !(sender instanceof EntityPlayer)))
                        {
                            throw new WrongUsageException("commands.scoreboard.teams.join.usage", new Object[0]);
                        }

                        this.joinTeam(sender, args, 2);
                    }
                    else if (args[1].equalsIgnoreCase("leave"))
                    {
                        if (args.length < 3 && !(sender instanceof EntityPlayer))
                        {
                            throw new WrongUsageException("commands.scoreboard.teams.leave.usage", new Object[0]);
                        }

                        this.leaveTeam(sender, args, 2);
                    }
                    else
                    {
                        if (!args[1].equalsIgnoreCase("option"))
                        {
                            throw new WrongUsageException("commands.scoreboard.teams.usage", new Object[0]);
                        }

                        if (args.length != 4 && args.length != 5)
                        {
                            throw new WrongUsageException("commands.scoreboard.teams.option.usage", new Object[0]);
                        }

                        this.setTeamOption(sender, args, 2);
                    }
                }
            }
        }
    }

    private boolean func_175780_b(ICommandSender p_175780_1_, String[] p_175780_2_) throws CommandException
    {
        int lvt_3_1_ = -1;

        for (int lvt_4_1_ = 0; lvt_4_1_ < p_175780_2_.length; ++lvt_4_1_)
        {
            if (this.isUsernameIndex(p_175780_2_, lvt_4_1_) && "*".equals(p_175780_2_[lvt_4_1_]))
            {
                if (lvt_3_1_ >= 0)
                {
                    throw new CommandException("commands.scoreboard.noMultiWildcard", new Object[0]);
                }

                lvt_3_1_ = lvt_4_1_;
            }
        }

        if (lvt_3_1_ < 0)
        {
            return false;
        }
        else
        {
            List<String> lvt_4_2_ = Lists.newArrayList(this.getScoreboard().getObjectiveNames());
            String lvt_5_1_ = p_175780_2_[lvt_3_1_];
            List<String> lvt_6_1_ = Lists.newArrayList();

            for (String lvt_8_1_ : lvt_4_2_)
            {
                p_175780_2_[lvt_3_1_] = lvt_8_1_;

                try
                {
                    this.processCommand(p_175780_1_, p_175780_2_);
                    lvt_6_1_.add(lvt_8_1_);
                }
                catch (CommandException var11)
                {
                    ChatComponentTranslation lvt_10_1_ = new ChatComponentTranslation(var11.getMessage(), var11.getErrorObjects());
                    lvt_10_1_.getChatStyle().setColor(EnumChatFormatting.RED);
                    p_175780_1_.addChatMessage(lvt_10_1_);
                }
            }

            p_175780_2_[lvt_3_1_] = lvt_5_1_;
            p_175780_1_.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, lvt_6_1_.size());

            if (lvt_6_1_.size() == 0)
            {
                throw new WrongUsageException("commands.scoreboard.allMatchesFailed", new Object[0]);
            }
            else
            {
                return true;
            }
        }
    }

    protected Scoreboard getScoreboard()
    {
        return MinecraftServer.getServer().worldServerForDimension(0).getScoreboard();
    }

    protected ScoreObjective getObjective(String name, boolean edit) throws CommandException
    {
        Scoreboard lvt_3_1_ = this.getScoreboard();
        ScoreObjective lvt_4_1_ = lvt_3_1_.getObjective(name);

        if (lvt_4_1_ == null)
        {
            throw new CommandException("commands.scoreboard.objectiveNotFound", new Object[] {name});
        }
        else if (edit && lvt_4_1_.getCriteria().isReadOnly())
        {
            throw new CommandException("commands.scoreboard.objectiveReadOnly", new Object[] {name});
        }
        else
        {
            return lvt_4_1_;
        }
    }

    protected ScorePlayerTeam getTeam(String name) throws CommandException
    {
        Scoreboard lvt_2_1_ = this.getScoreboard();
        ScorePlayerTeam lvt_3_1_ = lvt_2_1_.getTeam(name);

        if (lvt_3_1_ == null)
        {
            throw new CommandException("commands.scoreboard.teamNotFound", new Object[] {name});
        }
        else
        {
            return lvt_3_1_;
        }
    }

    protected void addObjective(ICommandSender sender, String[] args, int index) throws CommandException
    {
        String lvt_4_1_ = args[index++];
        String lvt_5_1_ = args[index++];
        Scoreboard lvt_6_1_ = this.getScoreboard();
        IScoreObjectiveCriteria lvt_7_1_ = (IScoreObjectiveCriteria)IScoreObjectiveCriteria.INSTANCES.get(lvt_5_1_);

        if (lvt_7_1_ == null)
        {
            throw new WrongUsageException("commands.scoreboard.objectives.add.wrongType", new Object[] {lvt_5_1_});
        }
        else if (lvt_6_1_.getObjective(lvt_4_1_) != null)
        {
            throw new CommandException("commands.scoreboard.objectives.add.alreadyExists", new Object[] {lvt_4_1_});
        }
        else if (lvt_4_1_.length() > 16)
        {
            throw new SyntaxErrorException("commands.scoreboard.objectives.add.tooLong", new Object[] {lvt_4_1_, Integer.valueOf(16)});
        }
        else if (lvt_4_1_.length() == 0)
        {
            throw new WrongUsageException("commands.scoreboard.objectives.add.usage", new Object[0]);
        }
        else
        {
            if (args.length > index)
            {
                String lvt_8_1_ = getChatComponentFromNthArg(sender, args, index).getUnformattedText();

                if (lvt_8_1_.length() > 32)
                {
                    throw new SyntaxErrorException("commands.scoreboard.objectives.add.displayTooLong", new Object[] {lvt_8_1_, Integer.valueOf(32)});
                }

                if (lvt_8_1_.length() > 0)
                {
                    lvt_6_1_.addScoreObjective(lvt_4_1_, lvt_7_1_).setDisplayName(lvt_8_1_);
                }
                else
                {
                    lvt_6_1_.addScoreObjective(lvt_4_1_, lvt_7_1_);
                }
            }
            else
            {
                lvt_6_1_.addScoreObjective(lvt_4_1_, lvt_7_1_);
            }

            notifyOperators(sender, this, "commands.scoreboard.objectives.add.success", new Object[] {lvt_4_1_});
        }
    }

    protected void addTeam(ICommandSender sender, String[] args, int index) throws CommandException
    {
        String lvt_4_1_ = args[index++];
        Scoreboard lvt_5_1_ = this.getScoreboard();

        if (lvt_5_1_.getTeam(lvt_4_1_) != null)
        {
            throw new CommandException("commands.scoreboard.teams.add.alreadyExists", new Object[] {lvt_4_1_});
        }
        else if (lvt_4_1_.length() > 16)
        {
            throw new SyntaxErrorException("commands.scoreboard.teams.add.tooLong", new Object[] {lvt_4_1_, Integer.valueOf(16)});
        }
        else if (lvt_4_1_.length() == 0)
        {
            throw new WrongUsageException("commands.scoreboard.teams.add.usage", new Object[0]);
        }
        else
        {
            if (args.length > index)
            {
                String lvt_6_1_ = getChatComponentFromNthArg(sender, args, index).getUnformattedText();

                if (lvt_6_1_.length() > 32)
                {
                    throw new SyntaxErrorException("commands.scoreboard.teams.add.displayTooLong", new Object[] {lvt_6_1_, Integer.valueOf(32)});
                }

                if (lvt_6_1_.length() > 0)
                {
                    lvt_5_1_.createTeam(lvt_4_1_).setTeamName(lvt_6_1_);
                }
                else
                {
                    lvt_5_1_.createTeam(lvt_4_1_);
                }
            }
            else
            {
                lvt_5_1_.createTeam(lvt_4_1_);
            }

            notifyOperators(sender, this, "commands.scoreboard.teams.add.success", new Object[] {lvt_4_1_});
        }
    }

    protected void setTeamOption(ICommandSender sender, String[] args, int index) throws CommandException
    {
        ScorePlayerTeam lvt_4_1_ = this.getTeam(args[index++]);

        if (lvt_4_1_ != null)
        {
            String lvt_5_1_ = args[index++].toLowerCase();

            if (!lvt_5_1_.equalsIgnoreCase("color") && !lvt_5_1_.equalsIgnoreCase("friendlyfire") && !lvt_5_1_.equalsIgnoreCase("seeFriendlyInvisibles") && !lvt_5_1_.equalsIgnoreCase("nametagVisibility") && !lvt_5_1_.equalsIgnoreCase("deathMessageVisibility"))
            {
                throw new WrongUsageException("commands.scoreboard.teams.option.usage", new Object[0]);
            }
            else if (args.length == 4)
            {
                if (lvt_5_1_.equalsIgnoreCase("color"))
                {
                    throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[] {lvt_5_1_, joinNiceStringFromCollection(EnumChatFormatting.getValidValues(true, false))});
                }
                else if (!lvt_5_1_.equalsIgnoreCase("friendlyfire") && !lvt_5_1_.equalsIgnoreCase("seeFriendlyInvisibles"))
                {
                    if (!lvt_5_1_.equalsIgnoreCase("nametagVisibility") && !lvt_5_1_.equalsIgnoreCase("deathMessageVisibility"))
                    {
                        throw new WrongUsageException("commands.scoreboard.teams.option.usage", new Object[0]);
                    }
                    else
                    {
                        throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[] {lvt_5_1_, joinNiceString(Team.EnumVisible.func_178825_a())});
                    }
                }
                else
                {
                    throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[] {lvt_5_1_, joinNiceStringFromCollection(Arrays.asList(new String[]{"true", "false"}))});
                }
            }
            else
            {
                String lvt_6_1_ = args[index];

                if (lvt_5_1_.equalsIgnoreCase("color"))
                {
                    EnumChatFormatting lvt_7_1_ = EnumChatFormatting.getValueByName(lvt_6_1_);

                    if (lvt_7_1_ == null || lvt_7_1_.isFancyStyling())
                    {
                        throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[] {lvt_5_1_, joinNiceStringFromCollection(EnumChatFormatting.getValidValues(true, false))});
                    }

                    lvt_4_1_.setChatFormat(lvt_7_1_);
                    lvt_4_1_.setNamePrefix(lvt_7_1_.toString());
                    lvt_4_1_.setNameSuffix(EnumChatFormatting.RESET.toString());
                }
                else if (lvt_5_1_.equalsIgnoreCase("friendlyfire"))
                {
                    if (!lvt_6_1_.equalsIgnoreCase("true") && !lvt_6_1_.equalsIgnoreCase("false"))
                    {
                        throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[] {lvt_5_1_, joinNiceStringFromCollection(Arrays.asList(new String[]{"true", "false"}))});
                    }

                    lvt_4_1_.setAllowFriendlyFire(lvt_6_1_.equalsIgnoreCase("true"));
                }
                else if (lvt_5_1_.equalsIgnoreCase("seeFriendlyInvisibles"))
                {
                    if (!lvt_6_1_.equalsIgnoreCase("true") && !lvt_6_1_.equalsIgnoreCase("false"))
                    {
                        throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[] {lvt_5_1_, joinNiceStringFromCollection(Arrays.asList(new String[]{"true", "false"}))});
                    }

                    lvt_4_1_.setSeeFriendlyInvisiblesEnabled(lvt_6_1_.equalsIgnoreCase("true"));
                }
                else if (lvt_5_1_.equalsIgnoreCase("nametagVisibility"))
                {
                    Team.EnumVisible lvt_7_2_ = Team.EnumVisible.func_178824_a(lvt_6_1_);

                    if (lvt_7_2_ == null)
                    {
                        throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[] {lvt_5_1_, joinNiceString(Team.EnumVisible.func_178825_a())});
                    }

                    lvt_4_1_.setNameTagVisibility(lvt_7_2_);
                }
                else if (lvt_5_1_.equalsIgnoreCase("deathMessageVisibility"))
                {
                    Team.EnumVisible lvt_7_3_ = Team.EnumVisible.func_178824_a(lvt_6_1_);

                    if (lvt_7_3_ == null)
                    {
                        throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[] {lvt_5_1_, joinNiceString(Team.EnumVisible.func_178825_a())});
                    }

                    lvt_4_1_.setDeathMessageVisibility(lvt_7_3_);
                }

                notifyOperators(sender, this, "commands.scoreboard.teams.option.success", new Object[] {lvt_5_1_, lvt_4_1_.getRegisteredName(), lvt_6_1_});
            }
        }
    }

    protected void removeTeam(ICommandSender p_147194_1_, String[] p_147194_2_, int p_147194_3_) throws CommandException
    {
        Scoreboard lvt_4_1_ = this.getScoreboard();
        ScorePlayerTeam lvt_5_1_ = this.getTeam(p_147194_2_[p_147194_3_]);

        if (lvt_5_1_ != null)
        {
            lvt_4_1_.removeTeam(lvt_5_1_);
            notifyOperators(p_147194_1_, this, "commands.scoreboard.teams.remove.success", new Object[] {lvt_5_1_.getRegisteredName()});
        }
    }

    protected void listTeams(ICommandSender p_147186_1_, String[] p_147186_2_, int p_147186_3_) throws CommandException
    {
        Scoreboard lvt_4_1_ = this.getScoreboard();

        if (p_147186_2_.length > p_147186_3_)
        {
            ScorePlayerTeam lvt_5_1_ = this.getTeam(p_147186_2_[p_147186_3_]);

            if (lvt_5_1_ == null)
            {
                return;
            }

            Collection<String> lvt_6_1_ = lvt_5_1_.getMembershipCollection();
            p_147186_1_.setCommandStat(CommandResultStats.Type.QUERY_RESULT, lvt_6_1_.size());

            if (lvt_6_1_.size() <= 0)
            {
                throw new CommandException("commands.scoreboard.teams.list.player.empty", new Object[] {lvt_5_1_.getRegisteredName()});
            }

            ChatComponentTranslation lvt_7_1_ = new ChatComponentTranslation("commands.scoreboard.teams.list.player.count", new Object[] {Integer.valueOf(lvt_6_1_.size()), lvt_5_1_.getRegisteredName()});
            lvt_7_1_.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
            p_147186_1_.addChatMessage(lvt_7_1_);
            p_147186_1_.addChatMessage(new ChatComponentText(joinNiceString(lvt_6_1_.toArray())));
        }
        else
        {
            Collection<ScorePlayerTeam> lvt_5_2_ = lvt_4_1_.getTeams();
            p_147186_1_.setCommandStat(CommandResultStats.Type.QUERY_RESULT, lvt_5_2_.size());

            if (lvt_5_2_.size() <= 0)
            {
                throw new CommandException("commands.scoreboard.teams.list.empty", new Object[0]);
            }

            ChatComponentTranslation lvt_6_2_ = new ChatComponentTranslation("commands.scoreboard.teams.list.count", new Object[] {Integer.valueOf(lvt_5_2_.size())});
            lvt_6_2_.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
            p_147186_1_.addChatMessage(lvt_6_2_);

            for (ScorePlayerTeam lvt_8_1_ : lvt_5_2_)
            {
                p_147186_1_.addChatMessage(new ChatComponentTranslation("commands.scoreboard.teams.list.entry", new Object[] {lvt_8_1_.getRegisteredName(), lvt_8_1_.getTeamName(), Integer.valueOf(lvt_8_1_.getMembershipCollection().size())}));
            }
        }
    }

    protected void joinTeam(ICommandSender p_147190_1_, String[] p_147190_2_, int p_147190_3_) throws CommandException
    {
        Scoreboard lvt_4_1_ = this.getScoreboard();
        String lvt_5_1_ = p_147190_2_[p_147190_3_++];
        Set<String> lvt_6_1_ = Sets.newHashSet();
        Set<String> lvt_7_1_ = Sets.newHashSet();

        if (p_147190_1_ instanceof EntityPlayer && p_147190_3_ == p_147190_2_.length)
        {
            String lvt_8_1_ = getCommandSenderAsPlayer(p_147190_1_).getName();

            if (lvt_4_1_.addPlayerToTeam(lvt_8_1_, lvt_5_1_))
            {
                lvt_6_1_.add(lvt_8_1_);
            }
            else
            {
                lvt_7_1_.add(lvt_8_1_);
            }
        }
        else
        {
            while (p_147190_3_ < p_147190_2_.length)
            {
                String lvt_8_2_ = p_147190_2_[p_147190_3_++];

                if (lvt_8_2_.startsWith("@"))
                {
                    for (Entity lvt_11_1_ : func_175763_c(p_147190_1_, lvt_8_2_))
                    {
                        String lvt_12_1_ = getEntityName(p_147190_1_, lvt_11_1_.getUniqueID().toString());

                        if (lvt_4_1_.addPlayerToTeam(lvt_12_1_, lvt_5_1_))
                        {
                            lvt_6_1_.add(lvt_12_1_);
                        }
                        else
                        {
                            lvt_7_1_.add(lvt_12_1_);
                        }
                    }
                }
                else
                {
                    String lvt_9_2_ = getEntityName(p_147190_1_, lvt_8_2_);

                    if (lvt_4_1_.addPlayerToTeam(lvt_9_2_, lvt_5_1_))
                    {
                        lvt_6_1_.add(lvt_9_2_);
                    }
                    else
                    {
                        lvt_7_1_.add(lvt_9_2_);
                    }
                }
            }
        }

        if (!lvt_6_1_.isEmpty())
        {
            p_147190_1_.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, lvt_6_1_.size());
            notifyOperators(p_147190_1_, this, "commands.scoreboard.teams.join.success", new Object[] {Integer.valueOf(lvt_6_1_.size()), lvt_5_1_, joinNiceString(lvt_6_1_.toArray(new String[lvt_6_1_.size()]))});
        }

        if (!lvt_7_1_.isEmpty())
        {
            throw new CommandException("commands.scoreboard.teams.join.failure", new Object[] {Integer.valueOf(lvt_7_1_.size()), lvt_5_1_, joinNiceString(lvt_7_1_.toArray(new String[lvt_7_1_.size()]))});
        }
    }

    protected void leaveTeam(ICommandSender p_147199_1_, String[] p_147199_2_, int p_147199_3_) throws CommandException
    {
        Scoreboard lvt_4_1_ = this.getScoreboard();
        Set<String> lvt_5_1_ = Sets.newHashSet();
        Set<String> lvt_6_1_ = Sets.newHashSet();

        if (p_147199_1_ instanceof EntityPlayer && p_147199_3_ == p_147199_2_.length)
        {
            String lvt_7_1_ = getCommandSenderAsPlayer(p_147199_1_).getName();

            if (lvt_4_1_.removePlayerFromTeams(lvt_7_1_))
            {
                lvt_5_1_.add(lvt_7_1_);
            }
            else
            {
                lvt_6_1_.add(lvt_7_1_);
            }
        }
        else
        {
            while (p_147199_3_ < p_147199_2_.length)
            {
                String lvt_7_2_ = p_147199_2_[p_147199_3_++];

                if (lvt_7_2_.startsWith("@"))
                {
                    for (Entity lvt_10_1_ : func_175763_c(p_147199_1_, lvt_7_2_))
                    {
                        String lvt_11_1_ = getEntityName(p_147199_1_, lvt_10_1_.getUniqueID().toString());

                        if (lvt_4_1_.removePlayerFromTeams(lvt_11_1_))
                        {
                            lvt_5_1_.add(lvt_11_1_);
                        }
                        else
                        {
                            lvt_6_1_.add(lvt_11_1_);
                        }
                    }
                }
                else
                {
                    String lvt_8_2_ = getEntityName(p_147199_1_, lvt_7_2_);

                    if (lvt_4_1_.removePlayerFromTeams(lvt_8_2_))
                    {
                        lvt_5_1_.add(lvt_8_2_);
                    }
                    else
                    {
                        lvt_6_1_.add(lvt_8_2_);
                    }
                }
            }
        }

        if (!lvt_5_1_.isEmpty())
        {
            p_147199_1_.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, lvt_5_1_.size());
            notifyOperators(p_147199_1_, this, "commands.scoreboard.teams.leave.success", new Object[] {Integer.valueOf(lvt_5_1_.size()), joinNiceString(lvt_5_1_.toArray(new String[lvt_5_1_.size()]))});
        }

        if (!lvt_6_1_.isEmpty())
        {
            throw new CommandException("commands.scoreboard.teams.leave.failure", new Object[] {Integer.valueOf(lvt_6_1_.size()), joinNiceString(lvt_6_1_.toArray(new String[lvt_6_1_.size()]))});
        }
    }

    protected void emptyTeam(ICommandSender p_147188_1_, String[] p_147188_2_, int p_147188_3_) throws CommandException
    {
        Scoreboard lvt_4_1_ = this.getScoreboard();
        ScorePlayerTeam lvt_5_1_ = this.getTeam(p_147188_2_[p_147188_3_]);

        if (lvt_5_1_ != null)
        {
            Collection<String> lvt_6_1_ = Lists.newArrayList(lvt_5_1_.getMembershipCollection());
            p_147188_1_.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, lvt_6_1_.size());

            if (lvt_6_1_.isEmpty())
            {
                throw new CommandException("commands.scoreboard.teams.empty.alreadyEmpty", new Object[] {lvt_5_1_.getRegisteredName()});
            }
            else
            {
                for (String lvt_8_1_ : lvt_6_1_)
                {
                    lvt_4_1_.removePlayerFromTeam(lvt_8_1_, lvt_5_1_);
                }

                notifyOperators(p_147188_1_, this, "commands.scoreboard.teams.empty.success", new Object[] {Integer.valueOf(lvt_6_1_.size()), lvt_5_1_.getRegisteredName()});
            }
        }
    }

    protected void removeObjective(ICommandSender p_147191_1_, String p_147191_2_) throws CommandException
    {
        Scoreboard lvt_3_1_ = this.getScoreboard();
        ScoreObjective lvt_4_1_ = this.getObjective(p_147191_2_, false);
        lvt_3_1_.removeObjective(lvt_4_1_);
        notifyOperators(p_147191_1_, this, "commands.scoreboard.objectives.remove.success", new Object[] {p_147191_2_});
    }

    protected void listObjectives(ICommandSender p_147196_1_) throws CommandException
    {
        Scoreboard lvt_2_1_ = this.getScoreboard();
        Collection<ScoreObjective> lvt_3_1_ = lvt_2_1_.getScoreObjectives();

        if (lvt_3_1_.size() <= 0)
        {
            throw new CommandException("commands.scoreboard.objectives.list.empty", new Object[0]);
        }
        else
        {
            ChatComponentTranslation lvt_4_1_ = new ChatComponentTranslation("commands.scoreboard.objectives.list.count", new Object[] {Integer.valueOf(lvt_3_1_.size())});
            lvt_4_1_.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
            p_147196_1_.addChatMessage(lvt_4_1_);

            for (ScoreObjective lvt_6_1_ : lvt_3_1_)
            {
                p_147196_1_.addChatMessage(new ChatComponentTranslation("commands.scoreboard.objectives.list.entry", new Object[] {lvt_6_1_.getName(), lvt_6_1_.getDisplayName(), lvt_6_1_.getCriteria().getName()}));
            }
        }
    }

    protected void setObjectiveDisplay(ICommandSender p_147198_1_, String[] p_147198_2_, int p_147198_3_) throws CommandException
    {
        Scoreboard lvt_4_1_ = this.getScoreboard();
        String lvt_5_1_ = p_147198_2_[p_147198_3_++];
        int lvt_6_1_ = Scoreboard.getObjectiveDisplaySlotNumber(lvt_5_1_);
        ScoreObjective lvt_7_1_ = null;

        if (p_147198_2_.length == 4)
        {
            lvt_7_1_ = this.getObjective(p_147198_2_[p_147198_3_], false);
        }

        if (lvt_6_1_ < 0)
        {
            throw new CommandException("commands.scoreboard.objectives.setdisplay.invalidSlot", new Object[] {lvt_5_1_});
        }
        else
        {
            lvt_4_1_.setObjectiveInDisplaySlot(lvt_6_1_, lvt_7_1_);

            if (lvt_7_1_ != null)
            {
                notifyOperators(p_147198_1_, this, "commands.scoreboard.objectives.setdisplay.successSet", new Object[] {Scoreboard.getObjectiveDisplaySlot(lvt_6_1_), lvt_7_1_.getName()});
            }
            else
            {
                notifyOperators(p_147198_1_, this, "commands.scoreboard.objectives.setdisplay.successCleared", new Object[] {Scoreboard.getObjectiveDisplaySlot(lvt_6_1_)});
            }
        }
    }

    protected void listPlayers(ICommandSender p_147195_1_, String[] p_147195_2_, int p_147195_3_) throws CommandException
    {
        Scoreboard lvt_4_1_ = this.getScoreboard();

        if (p_147195_2_.length > p_147195_3_)
        {
            String lvt_5_1_ = getEntityName(p_147195_1_, p_147195_2_[p_147195_3_]);
            Map<ScoreObjective, Score> lvt_6_1_ = lvt_4_1_.getObjectivesForEntity(lvt_5_1_);
            p_147195_1_.setCommandStat(CommandResultStats.Type.QUERY_RESULT, lvt_6_1_.size());

            if (lvt_6_1_.size() <= 0)
            {
                throw new CommandException("commands.scoreboard.players.list.player.empty", new Object[] {lvt_5_1_});
            }

            ChatComponentTranslation lvt_7_1_ = new ChatComponentTranslation("commands.scoreboard.players.list.player.count", new Object[] {Integer.valueOf(lvt_6_1_.size()), lvt_5_1_});
            lvt_7_1_.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
            p_147195_1_.addChatMessage(lvt_7_1_);

            for (Score lvt_9_1_ : lvt_6_1_.values())
            {
                p_147195_1_.addChatMessage(new ChatComponentTranslation("commands.scoreboard.players.list.player.entry", new Object[] {Integer.valueOf(lvt_9_1_.getScorePoints()), lvt_9_1_.getObjective().getDisplayName(), lvt_9_1_.getObjective().getName()}));
            }
        }
        else
        {
            Collection<String> lvt_5_2_ = lvt_4_1_.getObjectiveNames();
            p_147195_1_.setCommandStat(CommandResultStats.Type.QUERY_RESULT, lvt_5_2_.size());

            if (lvt_5_2_.size() <= 0)
            {
                throw new CommandException("commands.scoreboard.players.list.empty", new Object[0]);
            }

            ChatComponentTranslation lvt_6_2_ = new ChatComponentTranslation("commands.scoreboard.players.list.count", new Object[] {Integer.valueOf(lvt_5_2_.size())});
            lvt_6_2_.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
            p_147195_1_.addChatMessage(lvt_6_2_);
            p_147195_1_.addChatMessage(new ChatComponentText(joinNiceString(lvt_5_2_.toArray())));
        }
    }

    protected void setPlayer(ICommandSender p_147197_1_, String[] p_147197_2_, int p_147197_3_) throws CommandException
    {
        String lvt_4_1_ = p_147197_2_[p_147197_3_ - 1];
        int lvt_5_1_ = p_147197_3_;
        String lvt_6_1_ = getEntityName(p_147197_1_, p_147197_2_[p_147197_3_++]);

        if (lvt_6_1_.length() > 40)
        {
            throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", new Object[] {lvt_6_1_, Integer.valueOf(40)});
        }
        else
        {
            ScoreObjective lvt_7_1_ = this.getObjective(p_147197_2_[p_147197_3_++], true);
            int lvt_8_1_ = lvt_4_1_.equalsIgnoreCase("set") ? parseInt(p_147197_2_[p_147197_3_++]) : parseInt(p_147197_2_[p_147197_3_++], 0);

            if (p_147197_2_.length > p_147197_3_)
            {
                Entity lvt_9_1_ = getEntity(p_147197_1_, p_147197_2_[lvt_5_1_]);

                try
                {
                    NBTTagCompound lvt_10_1_ = JsonToNBT.getTagFromJson(buildString(p_147197_2_, p_147197_3_));
                    NBTTagCompound lvt_11_1_ = new NBTTagCompound();
                    lvt_9_1_.writeToNBT(lvt_11_1_);

                    if (!NBTUtil.func_181123_a(lvt_10_1_, lvt_11_1_, true))
                    {
                        throw new CommandException("commands.scoreboard.players.set.tagMismatch", new Object[] {lvt_6_1_});
                    }
                }
                catch (NBTException var12)
                {
                    throw new CommandException("commands.scoreboard.players.set.tagError", new Object[] {var12.getMessage()});
                }
            }

            Scoreboard lvt_9_2_ = this.getScoreboard();
            Score lvt_10_3_ = lvt_9_2_.getValueFromObjective(lvt_6_1_, lvt_7_1_);

            if (lvt_4_1_.equalsIgnoreCase("set"))
            {
                lvt_10_3_.setScorePoints(lvt_8_1_);
            }
            else if (lvt_4_1_.equalsIgnoreCase("add"))
            {
                lvt_10_3_.increseScore(lvt_8_1_);
            }
            else
            {
                lvt_10_3_.decreaseScore(lvt_8_1_);
            }

            notifyOperators(p_147197_1_, this, "commands.scoreboard.players.set.success", new Object[] {lvt_7_1_.getName(), lvt_6_1_, Integer.valueOf(lvt_10_3_.getScorePoints())});
        }
    }

    protected void resetPlayers(ICommandSender p_147187_1_, String[] p_147187_2_, int p_147187_3_) throws CommandException
    {
        Scoreboard lvt_4_1_ = this.getScoreboard();
        String lvt_5_1_ = getEntityName(p_147187_1_, p_147187_2_[p_147187_3_++]);

        if (p_147187_2_.length > p_147187_3_)
        {
            ScoreObjective lvt_6_1_ = this.getObjective(p_147187_2_[p_147187_3_++], false);
            lvt_4_1_.removeObjectiveFromEntity(lvt_5_1_, lvt_6_1_);
            notifyOperators(p_147187_1_, this, "commands.scoreboard.players.resetscore.success", new Object[] {lvt_6_1_.getName(), lvt_5_1_});
        }
        else
        {
            lvt_4_1_.removeObjectiveFromEntity(lvt_5_1_, (ScoreObjective)null);
            notifyOperators(p_147187_1_, this, "commands.scoreboard.players.reset.success", new Object[] {lvt_5_1_});
        }
    }

    protected void func_175779_n(ICommandSender p_175779_1_, String[] p_175779_2_, int p_175779_3_) throws CommandException
    {
        Scoreboard lvt_4_1_ = this.getScoreboard();
        String lvt_5_1_ = getPlayerName(p_175779_1_, p_175779_2_[p_175779_3_++]);

        if (lvt_5_1_.length() > 40)
        {
            throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", new Object[] {lvt_5_1_, Integer.valueOf(40)});
        }
        else
        {
            ScoreObjective lvt_6_1_ = this.getObjective(p_175779_2_[p_175779_3_], false);

            if (lvt_6_1_.getCriteria() != IScoreObjectiveCriteria.TRIGGER)
            {
                throw new CommandException("commands.scoreboard.players.enable.noTrigger", new Object[] {lvt_6_1_.getName()});
            }
            else
            {
                Score lvt_7_1_ = lvt_4_1_.getValueFromObjective(lvt_5_1_, lvt_6_1_);
                lvt_7_1_.setLocked(false);
                notifyOperators(p_175779_1_, this, "commands.scoreboard.players.enable.success", new Object[] {lvt_6_1_.getName(), lvt_5_1_});
            }
        }
    }

    protected void func_175781_o(ICommandSender p_175781_1_, String[] p_175781_2_, int p_175781_3_) throws CommandException
    {
        Scoreboard lvt_4_1_ = this.getScoreboard();
        String lvt_5_1_ = getEntityName(p_175781_1_, p_175781_2_[p_175781_3_++]);

        if (lvt_5_1_.length() > 40)
        {
            throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", new Object[] {lvt_5_1_, Integer.valueOf(40)});
        }
        else
        {
            ScoreObjective lvt_6_1_ = this.getObjective(p_175781_2_[p_175781_3_++], false);

            if (!lvt_4_1_.entityHasObjective(lvt_5_1_, lvt_6_1_))
            {
                throw new CommandException("commands.scoreboard.players.test.notFound", new Object[] {lvt_6_1_.getName(), lvt_5_1_});
            }
            else
            {
                int lvt_7_1_ = p_175781_2_[p_175781_3_].equals("*") ? Integer.MIN_VALUE : parseInt(p_175781_2_[p_175781_3_]);
                ++p_175781_3_;
                int lvt_8_1_ = p_175781_3_ < p_175781_2_.length && !p_175781_2_[p_175781_3_].equals("*") ? parseInt(p_175781_2_[p_175781_3_], lvt_7_1_) : Integer.MAX_VALUE;
                Score lvt_9_1_ = lvt_4_1_.getValueFromObjective(lvt_5_1_, lvt_6_1_);

                if (lvt_9_1_.getScorePoints() >= lvt_7_1_ && lvt_9_1_.getScorePoints() <= lvt_8_1_)
                {
                    notifyOperators(p_175781_1_, this, "commands.scoreboard.players.test.success", new Object[] {Integer.valueOf(lvt_9_1_.getScorePoints()), Integer.valueOf(lvt_7_1_), Integer.valueOf(lvt_8_1_)});
                }
                else
                {
                    throw new CommandException("commands.scoreboard.players.test.failed", new Object[] {Integer.valueOf(lvt_9_1_.getScorePoints()), Integer.valueOf(lvt_7_1_), Integer.valueOf(lvt_8_1_)});
                }
            }
        }
    }

    protected void func_175778_p(ICommandSender p_175778_1_, String[] p_175778_2_, int p_175778_3_) throws CommandException
    {
        Scoreboard lvt_4_1_ = this.getScoreboard();
        String lvt_5_1_ = getEntityName(p_175778_1_, p_175778_2_[p_175778_3_++]);
        ScoreObjective lvt_6_1_ = this.getObjective(p_175778_2_[p_175778_3_++], true);
        String lvt_7_1_ = p_175778_2_[p_175778_3_++];
        String lvt_8_1_ = getEntityName(p_175778_1_, p_175778_2_[p_175778_3_++]);
        ScoreObjective lvt_9_1_ = this.getObjective(p_175778_2_[p_175778_3_], false);

        if (lvt_5_1_.length() > 40)
        {
            throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", new Object[] {lvt_5_1_, Integer.valueOf(40)});
        }
        else if (lvt_8_1_.length() > 40)
        {
            throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", new Object[] {lvt_8_1_, Integer.valueOf(40)});
        }
        else
        {
            Score lvt_10_1_ = lvt_4_1_.getValueFromObjective(lvt_5_1_, lvt_6_1_);

            if (!lvt_4_1_.entityHasObjective(lvt_8_1_, lvt_9_1_))
            {
                throw new CommandException("commands.scoreboard.players.operation.notFound", new Object[] {lvt_9_1_.getName(), lvt_8_1_});
            }
            else
            {
                Score lvt_11_1_ = lvt_4_1_.getValueFromObjective(lvt_8_1_, lvt_9_1_);

                if (lvt_7_1_.equals("+="))
                {
                    lvt_10_1_.setScorePoints(lvt_10_1_.getScorePoints() + lvt_11_1_.getScorePoints());
                }
                else if (lvt_7_1_.equals("-="))
                {
                    lvt_10_1_.setScorePoints(lvt_10_1_.getScorePoints() - lvt_11_1_.getScorePoints());
                }
                else if (lvt_7_1_.equals("*="))
                {
                    lvt_10_1_.setScorePoints(lvt_10_1_.getScorePoints() * lvt_11_1_.getScorePoints());
                }
                else if (lvt_7_1_.equals("/="))
                {
                    if (lvt_11_1_.getScorePoints() != 0)
                    {
                        lvt_10_1_.setScorePoints(lvt_10_1_.getScorePoints() / lvt_11_1_.getScorePoints());
                    }
                }
                else if (lvt_7_1_.equals("%="))
                {
                    if (lvt_11_1_.getScorePoints() != 0)
                    {
                        lvt_10_1_.setScorePoints(lvt_10_1_.getScorePoints() % lvt_11_1_.getScorePoints());
                    }
                }
                else if (lvt_7_1_.equals("="))
                {
                    lvt_10_1_.setScorePoints(lvt_11_1_.getScorePoints());
                }
                else if (lvt_7_1_.equals("<"))
                {
                    lvt_10_1_.setScorePoints(Math.min(lvt_10_1_.getScorePoints(), lvt_11_1_.getScorePoints()));
                }
                else if (lvt_7_1_.equals(">"))
                {
                    lvt_10_1_.setScorePoints(Math.max(lvt_10_1_.getScorePoints(), lvt_11_1_.getScorePoints()));
                }
                else
                {
                    if (!lvt_7_1_.equals("><"))
                    {
                        throw new CommandException("commands.scoreboard.players.operation.invalidOperation", new Object[] {lvt_7_1_});
                    }

                    int lvt_12_1_ = lvt_10_1_.getScorePoints();
                    lvt_10_1_.setScorePoints(lvt_11_1_.getScorePoints());
                    lvt_11_1_.setScorePoints(lvt_12_1_);
                }

                notifyOperators(p_175778_1_, this, "commands.scoreboard.players.operation.success", new Object[0]);
            }
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, new String[] {"objectives", "players", "teams"});
        }
        else
        {
            if (args[0].equalsIgnoreCase("objectives"))
            {
                if (args.length == 2)
                {
                    return getListOfStringsMatchingLastWord(args, new String[] {"list", "add", "remove", "setdisplay"});
                }

                if (args[1].equalsIgnoreCase("add"))
                {
                    if (args.length == 4)
                    {
                        Set<String> lvt_4_1_ = IScoreObjectiveCriteria.INSTANCES.keySet();
                        return getListOfStringsMatchingLastWord(args, lvt_4_1_);
                    }
                }
                else if (args[1].equalsIgnoreCase("remove"))
                {
                    if (args.length == 3)
                    {
                        return getListOfStringsMatchingLastWord(args, this.func_147184_a(false));
                    }
                }
                else if (args[1].equalsIgnoreCase("setdisplay"))
                {
                    if (args.length == 3)
                    {
                        return getListOfStringsMatchingLastWord(args, Scoreboard.getDisplaySlotStrings());
                    }

                    if (args.length == 4)
                    {
                        return getListOfStringsMatchingLastWord(args, this.func_147184_a(false));
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("players"))
            {
                if (args.length == 2)
                {
                    return getListOfStringsMatchingLastWord(args, new String[] {"set", "add", "remove", "reset", "list", "enable", "test", "operation"});
                }

                if (!args[1].equalsIgnoreCase("set") && !args[1].equalsIgnoreCase("add") && !args[1].equalsIgnoreCase("remove") && !args[1].equalsIgnoreCase("reset"))
                {
                    if (args[1].equalsIgnoreCase("enable"))
                    {
                        if (args.length == 3)
                        {
                            return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
                        }

                        if (args.length == 4)
                        {
                            return getListOfStringsMatchingLastWord(args, this.func_175782_e());
                        }
                    }
                    else if (!args[1].equalsIgnoreCase("list") && !args[1].equalsIgnoreCase("test"))
                    {
                        if (args[1].equalsIgnoreCase("operation"))
                        {
                            if (args.length == 3)
                            {
                                return getListOfStringsMatchingLastWord(args, this.getScoreboard().getObjectiveNames());
                            }

                            if (args.length == 4)
                            {
                                return getListOfStringsMatchingLastWord(args, this.func_147184_a(true));
                            }

                            if (args.length == 5)
                            {
                                return getListOfStringsMatchingLastWord(args, new String[] {"+=", "-=", "*=", "/=", "%=", "=", "<", ">", "><"});
                            }

                            if (args.length == 6)
                            {
                                return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
                            }

                            if (args.length == 7)
                            {
                                return getListOfStringsMatchingLastWord(args, this.func_147184_a(false));
                            }
                        }
                    }
                    else
                    {
                        if (args.length == 3)
                        {
                            return getListOfStringsMatchingLastWord(args, this.getScoreboard().getObjectiveNames());
                        }

                        if (args.length == 4 && args[1].equalsIgnoreCase("test"))
                        {
                            return getListOfStringsMatchingLastWord(args, this.func_147184_a(false));
                        }
                    }
                }
                else
                {
                    if (args.length == 3)
                    {
                        return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
                    }

                    if (args.length == 4)
                    {
                        return getListOfStringsMatchingLastWord(args, this.func_147184_a(true));
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("teams"))
            {
                if (args.length == 2)
                {
                    return getListOfStringsMatchingLastWord(args, new String[] {"add", "remove", "join", "leave", "empty", "list", "option"});
                }

                if (args[1].equalsIgnoreCase("join"))
                {
                    if (args.length == 3)
                    {
                        return getListOfStringsMatchingLastWord(args, this.getScoreboard().getTeamNames());
                    }

                    if (args.length >= 4)
                    {
                        return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
                    }
                }
                else
                {
                    if (args[1].equalsIgnoreCase("leave"))
                    {
                        return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
                    }

                    if (!args[1].equalsIgnoreCase("empty") && !args[1].equalsIgnoreCase("list") && !args[1].equalsIgnoreCase("remove"))
                    {
                        if (args[1].equalsIgnoreCase("option"))
                        {
                            if (args.length == 3)
                            {
                                return getListOfStringsMatchingLastWord(args, this.getScoreboard().getTeamNames());
                            }

                            if (args.length == 4)
                            {
                                return getListOfStringsMatchingLastWord(args, new String[] {"color", "friendlyfire", "seeFriendlyInvisibles", "nametagVisibility", "deathMessageVisibility"});
                            }

                            if (args.length == 5)
                            {
                                if (args[3].equalsIgnoreCase("color"))
                                {
                                    return getListOfStringsMatchingLastWord(args, EnumChatFormatting.getValidValues(true, false));
                                }

                                if (args[3].equalsIgnoreCase("nametagVisibility") || args[3].equalsIgnoreCase("deathMessageVisibility"))
                                {
                                    return getListOfStringsMatchingLastWord(args, Team.EnumVisible.func_178825_a());
                                }

                                if (args[3].equalsIgnoreCase("friendlyfire") || args[3].equalsIgnoreCase("seeFriendlyInvisibles"))
                                {
                                    return getListOfStringsMatchingLastWord(args, new String[] {"true", "false"});
                                }
                            }
                        }
                    }
                    else if (args.length == 3)
                    {
                        return getListOfStringsMatchingLastWord(args, this.getScoreboard().getTeamNames());
                    }
                }
            }

            return null;
        }
    }

    protected List<String> func_147184_a(boolean p_147184_1_)
    {
        Collection<ScoreObjective> lvt_2_1_ = this.getScoreboard().getScoreObjectives();
        List<String> lvt_3_1_ = Lists.newArrayList();

        for (ScoreObjective lvt_5_1_ : lvt_2_1_)
        {
            if (!p_147184_1_ || !lvt_5_1_.getCriteria().isReadOnly())
            {
                lvt_3_1_.add(lvt_5_1_.getName());
            }
        }

        return lvt_3_1_;
    }

    protected List<String> func_175782_e()
    {
        Collection<ScoreObjective> lvt_1_1_ = this.getScoreboard().getScoreObjectives();
        List<String> lvt_2_1_ = Lists.newArrayList();

        for (ScoreObjective lvt_4_1_ : lvt_1_1_)
        {
            if (lvt_4_1_.getCriteria() == IScoreObjectiveCriteria.TRIGGER)
            {
                lvt_2_1_.add(lvt_4_1_.getName());
            }
        }

        return lvt_2_1_;
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] args, int index)
    {
        return !args[0].equalsIgnoreCase("players") ? (args[0].equalsIgnoreCase("teams") ? index == 2 : false) : (args.length > 1 && args[1].equalsIgnoreCase("operation") ? index == 2 || index == 5 : index == 2);
    }
}
