package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

public class Scoreboard
{
    private final Map<String, ScoreObjective> scoreObjectives = Maps.newHashMap();
    private final Map<IScoreObjectiveCriteria, List<ScoreObjective>> scoreObjectiveCriterias = Maps.newHashMap();
    private final Map<String, Map<ScoreObjective, Score>> entitiesScoreObjectives = Maps.newHashMap();

    /** Index 0 is tab menu, 1 is sidebar, and 2 is below name */
    private final ScoreObjective[] objectiveDisplaySlots = new ScoreObjective[19];
    private final Map<String, ScorePlayerTeam> teams = Maps.newHashMap();
    private final Map<String, ScorePlayerTeam> teamMemberships = Maps.newHashMap();
    private static String[] field_178823_g = null;

    /**
     * Returns a ScoreObjective for the objective name
     */
    public ScoreObjective getObjective(String name)
    {
        return (ScoreObjective)this.scoreObjectives.get(name);
    }

    /**
     * Create and returns the score objective for the given name and ScoreCriteria
     */
    public ScoreObjective addScoreObjective(String name, IScoreObjectiveCriteria criteria)
    {
        if (name.length() > 16)
        {
            throw new IllegalArgumentException("The objective name \'" + name + "\' is too long!");
        }
        else
        {
            ScoreObjective lvt_3_1_ = this.getObjective(name);

            if (lvt_3_1_ != null)
            {
                throw new IllegalArgumentException("An objective with the name \'" + name + "\' already exists!");
            }
            else
            {
                lvt_3_1_ = new ScoreObjective(this, name, criteria);
                List<ScoreObjective> lvt_4_1_ = (List)this.scoreObjectiveCriterias.get(criteria);

                if (lvt_4_1_ == null)
                {
                    lvt_4_1_ = Lists.newArrayList();
                    this.scoreObjectiveCriterias.put(criteria, lvt_4_1_);
                }

                lvt_4_1_.add(lvt_3_1_);
                this.scoreObjectives.put(name, lvt_3_1_);
                this.onScoreObjectiveAdded(lvt_3_1_);
                return lvt_3_1_;
            }
        }
    }

    public Collection<ScoreObjective> getObjectivesFromCriteria(IScoreObjectiveCriteria criteria)
    {
        Collection<ScoreObjective> lvt_2_1_ = (Collection)this.scoreObjectiveCriterias.get(criteria);
        return lvt_2_1_ == null ? Lists.newArrayList() : Lists.newArrayList(lvt_2_1_);
    }

    /**
     * Returns if the entity has the given ScoreObjective
     */
    public boolean entityHasObjective(String name, ScoreObjective p_178819_2_)
    {
        Map<ScoreObjective, Score> lvt_3_1_ = (Map)this.entitiesScoreObjectives.get(name);

        if (lvt_3_1_ == null)
        {
            return false;
        }
        else
        {
            Score lvt_4_1_ = (Score)lvt_3_1_.get(p_178819_2_);
            return lvt_4_1_ != null;
        }
    }

    /**
     * Returns the value of the given objective for the given entity name
     */
    public Score getValueFromObjective(String name, ScoreObjective objective)
    {
        if (name.length() > 40)
        {
            throw new IllegalArgumentException("The player name \'" + name + "\' is too long!");
        }
        else
        {
            Map<ScoreObjective, Score> lvt_3_1_ = (Map)this.entitiesScoreObjectives.get(name);

            if (lvt_3_1_ == null)
            {
                lvt_3_1_ = Maps.newHashMap();
                this.entitiesScoreObjectives.put(name, lvt_3_1_);
            }

            Score lvt_4_1_ = (Score)lvt_3_1_.get(objective);

            if (lvt_4_1_ == null)
            {
                lvt_4_1_ = new Score(this, objective, name);
                lvt_3_1_.put(objective, lvt_4_1_);
            }

            return lvt_4_1_;
        }
    }

    public Collection<Score> getSortedScores(ScoreObjective objective)
    {
        List<Score> lvt_2_1_ = Lists.newArrayList();

        for (Map<ScoreObjective, Score> lvt_4_1_ : this.entitiesScoreObjectives.values())
        {
            Score lvt_5_1_ = (Score)lvt_4_1_.get(objective);

            if (lvt_5_1_ != null)
            {
                lvt_2_1_.add(lvt_5_1_);
            }
        }

        Collections.sort(lvt_2_1_, Score.scoreComparator);
        return lvt_2_1_;
    }

    public Collection<ScoreObjective> getScoreObjectives()
    {
        return this.scoreObjectives.values();
    }

    public Collection<String> getObjectiveNames()
    {
        return this.entitiesScoreObjectives.keySet();
    }

    /**
     * Remove the given ScoreObjective for the given Entity name.
     */
    public void removeObjectiveFromEntity(String name, ScoreObjective objective)
    {
        if (objective == null)
        {
            Map<ScoreObjective, Score> lvt_3_1_ = (Map)this.entitiesScoreObjectives.remove(name);

            if (lvt_3_1_ != null)
            {
                this.func_96516_a(name);
            }
        }
        else
        {
            Map<ScoreObjective, Score> lvt_3_2_ = (Map)this.entitiesScoreObjectives.get(name);

            if (lvt_3_2_ != null)
            {
                Score lvt_4_1_ = (Score)lvt_3_2_.remove(objective);

                if (lvt_3_2_.size() < 1)
                {
                    Map<ScoreObjective, Score> lvt_5_1_ = (Map)this.entitiesScoreObjectives.remove(name);

                    if (lvt_5_1_ != null)
                    {
                        this.func_96516_a(name);
                    }
                }
                else if (lvt_4_1_ != null)
                {
                    this.func_178820_a(name, objective);
                }
            }
        }
    }

    public Collection<Score> getScores()
    {
        Collection<Map<ScoreObjective, Score>> lvt_1_1_ = this.entitiesScoreObjectives.values();
        List<Score> lvt_2_1_ = Lists.newArrayList();

        for (Map<ScoreObjective, Score> lvt_4_1_ : lvt_1_1_)
        {
            lvt_2_1_.addAll(lvt_4_1_.values());
        }

        return lvt_2_1_;
    }

    public Map<ScoreObjective, Score> getObjectivesForEntity(String name)
    {
        Map<ScoreObjective, Score> lvt_2_1_ = (Map)this.entitiesScoreObjectives.get(name);

        if (lvt_2_1_ == null)
        {
            lvt_2_1_ = Maps.newHashMap();
        }

        return lvt_2_1_;
    }

    public void removeObjective(ScoreObjective p_96519_1_)
    {
        this.scoreObjectives.remove(p_96519_1_.getName());

        for (int lvt_2_1_ = 0; lvt_2_1_ < 19; ++lvt_2_1_)
        {
            if (this.getObjectiveInDisplaySlot(lvt_2_1_) == p_96519_1_)
            {
                this.setObjectiveInDisplaySlot(lvt_2_1_, (ScoreObjective)null);
            }
        }

        List<ScoreObjective> lvt_2_2_ = (List)this.scoreObjectiveCriterias.get(p_96519_1_.getCriteria());

        if (lvt_2_2_ != null)
        {
            lvt_2_2_.remove(p_96519_1_);
        }

        for (Map<ScoreObjective, Score> lvt_4_1_ : this.entitiesScoreObjectives.values())
        {
            lvt_4_1_.remove(p_96519_1_);
        }

        this.onScoreObjectiveRemoved(p_96519_1_);
    }

    /**
     * 0 is tab menu, 1 is sidebar, 2 is below name
     */
    public void setObjectiveInDisplaySlot(int p_96530_1_, ScoreObjective p_96530_2_)
    {
        this.objectiveDisplaySlots[p_96530_1_] = p_96530_2_;
    }

    /**
     * 0 is tab menu, 1 is sidebar, 2 is below name
     */
    public ScoreObjective getObjectiveInDisplaySlot(int p_96539_1_)
    {
        return this.objectiveDisplaySlots[p_96539_1_];
    }

    /**
     * Retrieve the ScorePlayerTeam instance identified by the passed team name
     */
    public ScorePlayerTeam getTeam(String p_96508_1_)
    {
        return (ScorePlayerTeam)this.teams.get(p_96508_1_);
    }

    public ScorePlayerTeam createTeam(String name)
    {
        if (name.length() > 16)
        {
            throw new IllegalArgumentException("The team name \'" + name + "\' is too long!");
        }
        else
        {
            ScorePlayerTeam lvt_2_1_ = this.getTeam(name);

            if (lvt_2_1_ != null)
            {
                throw new IllegalArgumentException("A team with the name \'" + name + "\' already exists!");
            }
            else
            {
                lvt_2_1_ = new ScorePlayerTeam(this, name);
                this.teams.put(name, lvt_2_1_);
                this.broadcastTeamCreated(lvt_2_1_);
                return lvt_2_1_;
            }
        }
    }

    /**
     * Removes the team from the scoreboard, updates all player memberships and broadcasts the deletion to all players
     */
    public void removeTeam(ScorePlayerTeam p_96511_1_)
    {
        this.teams.remove(p_96511_1_.getRegisteredName());

        for (String lvt_3_1_ : p_96511_1_.getMembershipCollection())
        {
            this.teamMemberships.remove(lvt_3_1_);
        }

        this.func_96513_c(p_96511_1_);
    }

    /**
     * Adds a player to the given team
     */
    public boolean addPlayerToTeam(String player, String newTeam)
    {
        if (player.length() > 40)
        {
            throw new IllegalArgumentException("The player name \'" + player + "\' is too long!");
        }
        else if (!this.teams.containsKey(newTeam))
        {
            return false;
        }
        else
        {
            ScorePlayerTeam lvt_3_1_ = this.getTeam(newTeam);

            if (this.getPlayersTeam(player) != null)
            {
                this.removePlayerFromTeams(player);
            }

            this.teamMemberships.put(player, lvt_3_1_);
            lvt_3_1_.getMembershipCollection().add(player);
            return true;
        }
    }

    public boolean removePlayerFromTeams(String p_96524_1_)
    {
        ScorePlayerTeam lvt_2_1_ = this.getPlayersTeam(p_96524_1_);

        if (lvt_2_1_ != null)
        {
            this.removePlayerFromTeam(p_96524_1_, lvt_2_1_);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Removes the given username from the given ScorePlayerTeam. If the player is not on the team then an
     * IllegalStateException is thrown.
     */
    public void removePlayerFromTeam(String p_96512_1_, ScorePlayerTeam p_96512_2_)
    {
        if (this.getPlayersTeam(p_96512_1_) != p_96512_2_)
        {
            throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team \'" + p_96512_2_.getRegisteredName() + "\'.");
        }
        else
        {
            this.teamMemberships.remove(p_96512_1_);
            p_96512_2_.getMembershipCollection().remove(p_96512_1_);
        }
    }

    public Collection<String> getTeamNames()
    {
        return this.teams.keySet();
    }

    public Collection<ScorePlayerTeam> getTeams()
    {
        return this.teams.values();
    }

    /**
     * Gets the ScorePlayerTeam object for the given username.
     */
    public ScorePlayerTeam getPlayersTeam(String p_96509_1_)
    {
        return (ScorePlayerTeam)this.teamMemberships.get(p_96509_1_);
    }

    /**
     * Called when a score objective is added
     */
    public void onScoreObjectiveAdded(ScoreObjective scoreObjectiveIn)
    {
    }

    public void onObjectiveDisplayNameChanged(ScoreObjective p_96532_1_)
    {
    }

    public void onScoreObjectiveRemoved(ScoreObjective p_96533_1_)
    {
    }

    public void func_96536_a(Score p_96536_1_)
    {
    }

    public void func_96516_a(String p_96516_1_)
    {
    }

    public void func_178820_a(String p_178820_1_, ScoreObjective p_178820_2_)
    {
    }

    /**
     * This packet will notify the players that this team is created, and that will register it on the client
     */
    public void broadcastTeamCreated(ScorePlayerTeam playerTeam)
    {
    }

    /**
     * This packet will notify the players that this team is updated
     */
    public void sendTeamUpdate(ScorePlayerTeam playerTeam)
    {
    }

    public void func_96513_c(ScorePlayerTeam playerTeam)
    {
    }

    /**
     * Returns 'list' for 0, 'sidebar' for 1, 'belowName for 2, otherwise null.
     */
    public static String getObjectiveDisplaySlot(int p_96517_0_)
    {
        switch (p_96517_0_)
        {
            case 0:
                return "list";

            case 1:
                return "sidebar";

            case 2:
                return "belowName";

            default:
                if (p_96517_0_ >= 3 && p_96517_0_ <= 18)
                {
                    EnumChatFormatting lvt_1_1_ = EnumChatFormatting.func_175744_a(p_96517_0_ - 3);

                    if (lvt_1_1_ != null && lvt_1_1_ != EnumChatFormatting.RESET)
                    {
                        return "sidebar.team." + lvt_1_1_.getFriendlyName();
                    }
                }

                return null;
        }
    }

    /**
     * Returns 0 for (case-insensitive) 'list', 1 for 'sidebar', 2 for 'belowName', otherwise -1.
     */
    public static int getObjectiveDisplaySlotNumber(String p_96537_0_)
    {
        if (p_96537_0_.equalsIgnoreCase("list"))
        {
            return 0;
        }
        else if (p_96537_0_.equalsIgnoreCase("sidebar"))
        {
            return 1;
        }
        else if (p_96537_0_.equalsIgnoreCase("belowName"))
        {
            return 2;
        }
        else
        {
            if (p_96537_0_.startsWith("sidebar.team."))
            {
                String lvt_1_1_ = p_96537_0_.substring("sidebar.team.".length());
                EnumChatFormatting lvt_2_1_ = EnumChatFormatting.getValueByName(lvt_1_1_);

                if (lvt_2_1_ != null && lvt_2_1_.getColorIndex() >= 0)
                {
                    return lvt_2_1_.getColorIndex() + 3;
                }
            }

            return -1;
        }
    }

    public static String[] getDisplaySlotStrings()
    {
        if (field_178823_g == null)
        {
            field_178823_g = new String[19];

            for (int lvt_0_1_ = 0; lvt_0_1_ < 19; ++lvt_0_1_)
            {
                field_178823_g[lvt_0_1_] = getObjectiveDisplaySlot(lvt_0_1_);
            }
        }

        return field_178823_g;
    }

    public void func_181140_a(Entity p_181140_1_)
    {
        if (p_181140_1_ != null && !(p_181140_1_ instanceof EntityPlayer) && !p_181140_1_.isEntityAlive())
        {
            String lvt_2_1_ = p_181140_1_.getUniqueID().toString();
            this.removeObjectiveFromEntity(lvt_2_1_, (ScoreObjective)null);
            this.removePlayerFromTeams(lvt_2_1_);
        }
    }
}
