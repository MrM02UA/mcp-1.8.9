package net.minecraft.stats;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import net.minecraft.event.HoverEvent;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IJsonSerializable;

public class StatBase
{
    /** The Stat ID */
    public final String statId;

    /** The Stat name */
    private final IChatComponent statName;
    public boolean isIndependent;
    private final IStatType type;
    private final IScoreObjectiveCriteria objectiveCriteria;
    private Class <? extends IJsonSerializable > field_150956_d;
    private static NumberFormat numberFormat = NumberFormat.getIntegerInstance(Locale.US);
    public static IStatType simpleStatType = new IStatType()
    {
        public String format(int number)
        {
            return StatBase.numberFormat.format((long)number);
        }
    };
    private static DecimalFormat decimalFormat = new DecimalFormat("########0.00");
    public static IStatType timeStatType = new IStatType()
    {
        public String format(int number)
        {
            double lvt_2_1_ = (double)number / 20.0D;
            double lvt_4_1_ = lvt_2_1_ / 60.0D;
            double lvt_6_1_ = lvt_4_1_ / 60.0D;
            double lvt_8_1_ = lvt_6_1_ / 24.0D;
            double lvt_10_1_ = lvt_8_1_ / 365.0D;
            return lvt_10_1_ > 0.5D ? StatBase.decimalFormat.format(lvt_10_1_) + " y" : (lvt_8_1_ > 0.5D ? StatBase.decimalFormat.format(lvt_8_1_) + " d" : (lvt_6_1_ > 0.5D ? StatBase.decimalFormat.format(lvt_6_1_) + " h" : (lvt_4_1_ > 0.5D ? StatBase.decimalFormat.format(lvt_4_1_) + " m" : lvt_2_1_ + " s")));
        }
    };
    public static IStatType distanceStatType = new IStatType()
    {
        public String format(int number)
        {
            double lvt_2_1_ = (double)number / 100.0D;
            double lvt_4_1_ = lvt_2_1_ / 1000.0D;
            return lvt_4_1_ > 0.5D ? StatBase.decimalFormat.format(lvt_4_1_) + " km" : (lvt_2_1_ > 0.5D ? StatBase.decimalFormat.format(lvt_2_1_) + " m" : number + " cm");
        }
    };
    public static IStatType field_111202_k = new IStatType()
    {
        public String format(int number)
        {
            return StatBase.decimalFormat.format((double)number * 0.1D);
        }
    };

    public StatBase(String statIdIn, IChatComponent statNameIn, IStatType typeIn)
    {
        this.statId = statIdIn;
        this.statName = statNameIn;
        this.type = typeIn;
        this.objectiveCriteria = new ObjectiveStat(this);
        IScoreObjectiveCriteria.INSTANCES.put(this.objectiveCriteria.getName(), this.objectiveCriteria);
    }

    public StatBase(String statIdIn, IChatComponent statNameIn)
    {
        this(statIdIn, statNameIn, simpleStatType);
    }

    /**
     * Initializes the current stat as independent (i.e., lacking prerequisites for being updated) and returns the
     * current instance.
     */
    public StatBase initIndependentStat()
    {
        this.isIndependent = true;
        return this;
    }

    /**
     * Register the stat into StatList.
     */
    public StatBase registerStat()
    {
        if (StatList.oneShotStats.containsKey(this.statId))
        {
            throw new RuntimeException("Duplicate stat id: \"" + ((StatBase)StatList.oneShotStats.get(this.statId)).statName + "\" and \"" + this.statName + "\" at id " + this.statId);
        }
        else
        {
            StatList.allStats.add(this);
            StatList.oneShotStats.put(this.statId, this);
            return this;
        }
    }

    /**
     * Returns whether or not the StatBase-derived class is a statistic (running counter) or an achievement (one-shot).
     */
    public boolean isAchievement()
    {
        return false;
    }

    public String format(int p_75968_1_)
    {
        return this.type.format(p_75968_1_);
    }

    public IChatComponent getStatName()
    {
        IChatComponent lvt_1_1_ = this.statName.createCopy();
        lvt_1_1_.getChatStyle().setColor(EnumChatFormatting.GRAY);
        lvt_1_1_.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ACHIEVEMENT, new ChatComponentText(this.statId)));
        return lvt_1_1_;
    }

    /**
     * 1.8.9
     */
    public IChatComponent createChatComponent()
    {
        IChatComponent lvt_1_1_ = this.getStatName();
        IChatComponent lvt_2_1_ = (new ChatComponentText("[")).appendSibling(lvt_1_1_).appendText("]");
        lvt_2_1_.setChatStyle(lvt_1_1_.getChatStyle());
        return lvt_2_1_;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass())
        {
            StatBase lvt_2_1_ = (StatBase)p_equals_1_;
            return this.statId.equals(lvt_2_1_.statId);
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return this.statId.hashCode();
    }

    public String toString()
    {
        return "Stat{id=" + this.statId + ", nameId=" + this.statName + ", awardLocallyOnly=" + this.isIndependent + ", formatter=" + this.type + ", objectiveCriteria=" + this.objectiveCriteria + '}';
    }

    /**
     * 1.8.9
     */
    public IScoreObjectiveCriteria getCriteria()
    {
        return this.objectiveCriteria;
    }

    public Class <? extends IJsonSerializable > func_150954_l()
    {
        return this.field_150956_d;
    }

    public StatBase func_150953_b(Class <? extends IJsonSerializable > p_150953_1_)
    {
        this.field_150956_d = p_150953_1_;
        return this;
    }
}
