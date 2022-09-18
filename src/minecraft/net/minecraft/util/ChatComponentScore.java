package net.minecraft.util;

import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;

public class ChatComponentScore extends ChatComponentStyle
{
    private final String name;
    private final String objective;

    /** The value displayed instead of the real score (may be null) */
    private String value = "";

    public ChatComponentScore(String nameIn, String objectiveIn)
    {
        this.name = nameIn;
        this.objective = objectiveIn;
    }

    public String getName()
    {
        return this.name;
    }

    public String getObjective()
    {
        return this.objective;
    }

    /**
     * Sets the value displayed instead of the real score.
     */
    public void setValue(String valueIn)
    {
        this.value = valueIn;
    }

    /**
     * Gets the text of this component, without any special formatting codes added, for chat.  TODO: why is this two
     * different methods?
     */
    public String getUnformattedTextForChat()
    {
        MinecraftServer lvt_1_1_ = MinecraftServer.getServer();

        if (lvt_1_1_ != null && lvt_1_1_.isAnvilFileSet() && StringUtils.isNullOrEmpty(this.value))
        {
            Scoreboard lvt_2_1_ = lvt_1_1_.worldServerForDimension(0).getScoreboard();
            ScoreObjective lvt_3_1_ = lvt_2_1_.getObjective(this.objective);

            if (lvt_2_1_.entityHasObjective(this.name, lvt_3_1_))
            {
                Score lvt_4_1_ = lvt_2_1_.getValueFromObjective(this.name, lvt_3_1_);
                this.setValue(String.format("%d", new Object[] {Integer.valueOf(lvt_4_1_.getScorePoints())}));
            }
            else
            {
                this.value = "";
            }
        }

        return this.value;
    }

    /**
     * Creates a copy of this component.  Almost a deep copy, except the style is shallow-copied.
     */
    public ChatComponentScore createCopy()
    {
        ChatComponentScore lvt_1_1_ = new ChatComponentScore(this.name, this.objective);
        lvt_1_1_.setValue(this.value);
        lvt_1_1_.setChatStyle(this.getChatStyle().createShallowCopy());

        for (IChatComponent lvt_3_1_ : this.getSiblings())
        {
            lvt_1_1_.appendSibling(lvt_3_1_.createCopy());
        }

        return lvt_1_1_;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof ChatComponentScore))
        {
            return false;
        }
        else
        {
            ChatComponentScore lvt_2_1_ = (ChatComponentScore)p_equals_1_;
            return this.name.equals(lvt_2_1_.name) && this.objective.equals(lvt_2_1_.objective) && super.equals(p_equals_1_);
        }
    }

    public String toString()
    {
        return "ScoreComponent{name=\'" + this.name + '\'' + "objective=\'" + this.objective + '\'' + ", siblings=" + this.siblings + ", style=" + this.getChatStyle() + '}';
    }

    /**
     * Creates a copy of this component.  Almost a deep copy, except the style is shallow-copied.
     */
    public IChatComponent createCopy()
    {
        return this.createCopy();
    }
}
