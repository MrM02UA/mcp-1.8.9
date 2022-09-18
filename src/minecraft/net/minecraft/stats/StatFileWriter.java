package net.minecraft.stats;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.TupleIntJsonSerializable;

public class StatFileWriter
{
    protected final Map<StatBase, TupleIntJsonSerializable> statsData = Maps.newConcurrentMap();

    /**
     * Returns true if the achievement has been unlocked.
     */
    public boolean hasAchievementUnlocked(Achievement achievementIn)
    {
        return this.readStat(achievementIn) > 0;
    }

    /**
     * Returns true if the parent has been unlocked, or there is no parent
     */
    public boolean canUnlockAchievement(Achievement achievementIn)
    {
        return achievementIn.parentAchievement == null || this.hasAchievementUnlocked(achievementIn.parentAchievement);
    }

    public int func_150874_c(Achievement p_150874_1_)
    {
        if (this.hasAchievementUnlocked(p_150874_1_))
        {
            return 0;
        }
        else
        {
            int lvt_2_1_ = 0;

            for (Achievement lvt_3_1_ = p_150874_1_.parentAchievement; lvt_3_1_ != null && !this.hasAchievementUnlocked(lvt_3_1_); ++lvt_2_1_)
            {
                lvt_3_1_ = lvt_3_1_.parentAchievement;
            }

            return lvt_2_1_;
        }
    }

    public void increaseStat(EntityPlayer player, StatBase stat, int amount)
    {
        if (!stat.isAchievement() || this.canUnlockAchievement((Achievement)stat))
        {
            this.unlockAchievement(player, stat, this.readStat(stat) + amount);
        }
    }

    /**
     * Triggers the logging of an achievement and attempts to announce to server
     */
    public void unlockAchievement(EntityPlayer playerIn, StatBase statIn, int p_150873_3_)
    {
        TupleIntJsonSerializable lvt_4_1_ = (TupleIntJsonSerializable)this.statsData.get(statIn);

        if (lvt_4_1_ == null)
        {
            lvt_4_1_ = new TupleIntJsonSerializable();
            this.statsData.put(statIn, lvt_4_1_);
        }

        lvt_4_1_.setIntegerValue(p_150873_3_);
    }

    /**
     * Reads the given stat and returns its value as an int.
     */
    public int readStat(StatBase stat)
    {
        TupleIntJsonSerializable lvt_2_1_ = (TupleIntJsonSerializable)this.statsData.get(stat);
        return lvt_2_1_ == null ? 0 : lvt_2_1_.getIntegerValue();
    }

    public <T extends IJsonSerializable> T func_150870_b(StatBase p_150870_1_)
    {
        TupleIntJsonSerializable lvt_2_1_ = (TupleIntJsonSerializable)this.statsData.get(p_150870_1_);
        return (T)(lvt_2_1_ != null ? lvt_2_1_.getJsonSerializableValue() : null);
    }

    public <T extends IJsonSerializable> T func_150872_a(StatBase p_150872_1_, T p_150872_2_)
    {
        TupleIntJsonSerializable lvt_3_1_ = (TupleIntJsonSerializable)this.statsData.get(p_150872_1_);

        if (lvt_3_1_ == null)
        {
            lvt_3_1_ = new TupleIntJsonSerializable();
            this.statsData.put(p_150872_1_, lvt_3_1_);
        }

        lvt_3_1_.setJsonSerializableValue(p_150872_2_);
        return (T)p_150872_2_;
    }
}
