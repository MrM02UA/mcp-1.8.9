package net.minecraft.command;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class CommandResultStats
{
    /** The number of result command result types that are possible. */
    private static final int NUM_RESULT_TYPES = CommandResultStats.Type.values().length;
    private static final String[] STRING_RESULT_TYPES = new String[NUM_RESULT_TYPES];

    /**
     * List of entityID who set a stat, username for a player, UUID for all entities
     */
    private String[] entitiesID;

    /** List of all the Objectives names */
    private String[] objectives;

    public CommandResultStats()
    {
        this.entitiesID = STRING_RESULT_TYPES;
        this.objectives = STRING_RESULT_TYPES;
    }

    /**
     * Set the score on the ScoreBoard
     *  
     * @param scorePoint The score to set to the score board
     */
    public void setCommandStatScore(final ICommandSender sender, CommandResultStats.Type resultTypeIn, int scorePoint)
    {
        String lvt_4_1_ = this.entitiesID[resultTypeIn.getTypeID()];

        if (lvt_4_1_ != null)
        {
            ICommandSender lvt_5_1_ = new ICommandSender()
            {
                public String getName()
                {
                    return sender.getName();
                }
                public IChatComponent getDisplayName()
                {
                    return sender.getDisplayName();
                }
                public void addChatMessage(IChatComponent component)
                {
                    sender.addChatMessage(component);
                }
                public boolean canCommandSenderUseCommand(int permLevel, String commandName)
                {
                    return true;
                }
                public BlockPos getPosition()
                {
                    return sender.getPosition();
                }
                public Vec3 getPositionVector()
                {
                    return sender.getPositionVector();
                }
                public World getEntityWorld()
                {
                    return sender.getEntityWorld();
                }
                public Entity getCommandSenderEntity()
                {
                    return sender.getCommandSenderEntity();
                }
                public boolean sendCommandFeedback()
                {
                    return sender.sendCommandFeedback();
                }
                public void setCommandStat(CommandResultStats.Type type, int amount)
                {
                    sender.setCommandStat(type, amount);
                }
            };
            String lvt_6_1_;

            try
            {
                lvt_6_1_ = CommandBase.getEntityName(lvt_5_1_, lvt_4_1_);
            }
            catch (EntityNotFoundException var11)
            {
                return;
            }

            String lvt_7_2_ = this.objectives[resultTypeIn.getTypeID()];

            if (lvt_7_2_ != null)
            {
                Scoreboard lvt_8_1_ = sender.getEntityWorld().getScoreboard();
                ScoreObjective lvt_9_1_ = lvt_8_1_.getObjective(lvt_7_2_);

                if (lvt_9_1_ != null)
                {
                    if (lvt_8_1_.entityHasObjective(lvt_6_1_, lvt_9_1_))
                    {
                        Score lvt_10_1_ = lvt_8_1_.getValueFromObjective(lvt_6_1_, lvt_9_1_);
                        lvt_10_1_.setScorePoints(scorePoint);
                    }
                }
            }
        }
    }

    public void readStatsFromNBT(NBTTagCompound tagcompound)
    {
        if (tagcompound.hasKey("CommandStats", 10))
        {
            NBTTagCompound lvt_2_1_ = tagcompound.getCompoundTag("CommandStats");

            for (CommandResultStats.Type lvt_6_1_ : CommandResultStats.Type.values())
            {
                String lvt_7_1_ = lvt_6_1_.getTypeName() + "Name";
                String lvt_8_1_ = lvt_6_1_.getTypeName() + "Objective";

                if (lvt_2_1_.hasKey(lvt_7_1_, 8) && lvt_2_1_.hasKey(lvt_8_1_, 8))
                {
                    String lvt_9_1_ = lvt_2_1_.getString(lvt_7_1_);
                    String lvt_10_1_ = lvt_2_1_.getString(lvt_8_1_);
                    setScoreBoardStat(this, lvt_6_1_, lvt_9_1_, lvt_10_1_);
                }
            }
        }
    }

    public void writeStatsToNBT(NBTTagCompound tagcompound)
    {
        NBTTagCompound lvt_2_1_ = new NBTTagCompound();

        for (CommandResultStats.Type lvt_6_1_ : CommandResultStats.Type.values())
        {
            String lvt_7_1_ = this.entitiesID[lvt_6_1_.getTypeID()];
            String lvt_8_1_ = this.objectives[lvt_6_1_.getTypeID()];

            if (lvt_7_1_ != null && lvt_8_1_ != null)
            {
                lvt_2_1_.setString(lvt_6_1_.getTypeName() + "Name", lvt_7_1_);
                lvt_2_1_.setString(lvt_6_1_.getTypeName() + "Objective", lvt_8_1_);
            }
        }

        if (!lvt_2_1_.hasNoTags())
        {
            tagcompound.setTag("CommandStats", lvt_2_1_);
        }
    }

    /**
     * Set a stat in the scoreboard
     *  
     * @param entityID The username of the player or the UUID of an Entity
     * @param objectiveName The name of the Objective
     */
    public static void setScoreBoardStat(CommandResultStats stats, CommandResultStats.Type resultType, String entityID, String objectiveName)
    {
        if (entityID != null && entityID.length() != 0 && objectiveName != null && objectiveName.length() != 0)
        {
            if (stats.entitiesID == STRING_RESULT_TYPES || stats.objectives == STRING_RESULT_TYPES)
            {
                stats.entitiesID = new String[NUM_RESULT_TYPES];
                stats.objectives = new String[NUM_RESULT_TYPES];
            }

            stats.entitiesID[resultType.getTypeID()] = entityID;
            stats.objectives[resultType.getTypeID()] = objectiveName;
        }
        else
        {
            removeScoreBoardStat(stats, resultType);
        }
    }

    /**
     * Remove a stat from the scoreboard
     */
    private static void removeScoreBoardStat(CommandResultStats resultStatsIn, CommandResultStats.Type resultTypeIn)
    {
        if (resultStatsIn.entitiesID != STRING_RESULT_TYPES && resultStatsIn.objectives != STRING_RESULT_TYPES)
        {
            resultStatsIn.entitiesID[resultTypeIn.getTypeID()] = null;
            resultStatsIn.objectives[resultTypeIn.getTypeID()] = null;
            boolean lvt_2_1_ = true;

            for (CommandResultStats.Type lvt_6_1_ : CommandResultStats.Type.values())
            {
                if (resultStatsIn.entitiesID[lvt_6_1_.getTypeID()] != null && resultStatsIn.objectives[lvt_6_1_.getTypeID()] != null)
                {
                    lvt_2_1_ = false;
                    break;
                }
            }

            if (lvt_2_1_)
            {
                resultStatsIn.entitiesID = STRING_RESULT_TYPES;
                resultStatsIn.objectives = STRING_RESULT_TYPES;
            }
        }
    }

    /**
     * Add all stats in the CommandResultStats
     */
    public void addAllStats(CommandResultStats resultStatsIn)
    {
        for (CommandResultStats.Type lvt_5_1_ : CommandResultStats.Type.values())
        {
            setScoreBoardStat(this, lvt_5_1_, resultStatsIn.entitiesID[lvt_5_1_.getTypeID()], resultStatsIn.objectives[lvt_5_1_.getTypeID()]);
        }
    }

    public static enum Type
    {
        SUCCESS_COUNT(0, "SuccessCount"),
        AFFECTED_BLOCKS(1, "AffectedBlocks"),
        AFFECTED_ENTITIES(2, "AffectedEntities"),
        AFFECTED_ITEMS(3, "AffectedItems"),
        QUERY_RESULT(4, "QueryResult");

        final int typeID;
        final String typeName;

        private Type(int id, String name)
        {
            this.typeID = id;
            this.typeName = name;
        }

        public int getTypeID()
        {
            return this.typeID;
        }

        public String getTypeName()
        {
            return this.typeName;
        }

        public static String[] getTypeNames()
        {
            String[] lvt_0_1_ = new String[values().length];
            int lvt_1_1_ = 0;

            for (CommandResultStats.Type lvt_5_1_ : values())
            {
                lvt_0_1_[lvt_1_1_++] = lvt_5_1_.getTypeName();
            }

            return lvt_0_1_;
        }

        public static CommandResultStats.Type getTypeByName(String name)
        {
            for (CommandResultStats.Type lvt_4_1_ : values())
            {
                if (lvt_4_1_.getTypeName().equals(name))
                {
                    return lvt_4_1_;
                }
            }

            return null;
        }
    }
}
