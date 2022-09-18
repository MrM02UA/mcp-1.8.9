package net.minecraft.profiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Profiler
{
    private static final Logger logger = LogManager.getLogger();
    private final List<String> sectionList = Lists.newArrayList();
    private final List<Long> timestampList = Lists.newArrayList();

    /** Flag profiling enabled */
    public boolean profilingEnabled;

    /** Current profiling section */
    private String profilingSection = "";
    private final Map<String, Long> profilingMap = Maps.newHashMap();

    /**
     * Clear profiling.
     */
    public void clearProfiling()
    {
        this.profilingMap.clear();
        this.profilingSection = "";
        this.sectionList.clear();
    }

    /**
     * Start section
     */
    public void startSection(String name)
    {
        if (this.profilingEnabled)
        {
            if (this.profilingSection.length() > 0)
            {
                this.profilingSection = this.profilingSection + ".";
            }

            this.profilingSection = this.profilingSection + name;
            this.sectionList.add(this.profilingSection);
            this.timestampList.add(Long.valueOf(System.nanoTime()));
        }
    }

    /**
     * End section
     */
    public void endSection()
    {
        if (this.profilingEnabled)
        {
            long lvt_1_1_ = System.nanoTime();
            long lvt_3_1_ = ((Long)this.timestampList.remove(this.timestampList.size() - 1)).longValue();
            this.sectionList.remove(this.sectionList.size() - 1);
            long lvt_5_1_ = lvt_1_1_ - lvt_3_1_;

            if (this.profilingMap.containsKey(this.profilingSection))
            {
                this.profilingMap.put(this.profilingSection, Long.valueOf(((Long)this.profilingMap.get(this.profilingSection)).longValue() + lvt_5_1_));
            }
            else
            {
                this.profilingMap.put(this.profilingSection, Long.valueOf(lvt_5_1_));
            }

            if (lvt_5_1_ > 100000000L)
            {
                logger.warn("Something\'s taking too long! \'" + this.profilingSection + "\' took aprox " + (double)lvt_5_1_ / 1000000.0D + " ms");
            }

            this.profilingSection = !this.sectionList.isEmpty() ? (String)this.sectionList.get(this.sectionList.size() - 1) : "";
        }
    }

    public List<Profiler.Result> getProfilingData(String profilerName)
    {
        if (!this.profilingEnabled)
        {
            return null;
        }
        else
        {
            long lvt_3_1_ = this.profilingMap.containsKey("root") ? ((Long)this.profilingMap.get("root")).longValue() : 0L;
            long lvt_5_1_ = this.profilingMap.containsKey(profilerName) ? ((Long)this.profilingMap.get(profilerName)).longValue() : -1L;
            List<Profiler.Result> lvt_7_1_ = Lists.newArrayList();

            if (profilerName.length() > 0)
            {
                profilerName = profilerName + ".";
            }

            long lvt_8_1_ = 0L;

            for (String lvt_11_1_ : this.profilingMap.keySet())
            {
                if (lvt_11_1_.length() > profilerName.length() && lvt_11_1_.startsWith(profilerName) && lvt_11_1_.indexOf(".", profilerName.length() + 1) < 0)
                {
                    lvt_8_1_ += ((Long)this.profilingMap.get(lvt_11_1_)).longValue();
                }
            }

            float lvt_10_2_ = (float)lvt_8_1_;

            if (lvt_8_1_ < lvt_5_1_)
            {
                lvt_8_1_ = lvt_5_1_;
            }

            if (lvt_3_1_ < lvt_8_1_)
            {
                lvt_3_1_ = lvt_8_1_;
            }

            for (String lvt_12_1_ : this.profilingMap.keySet())
            {
                if (lvt_12_1_.length() > profilerName.length() && lvt_12_1_.startsWith(profilerName) && lvt_12_1_.indexOf(".", profilerName.length() + 1) < 0)
                {
                    long lvt_13_1_ = ((Long)this.profilingMap.get(lvt_12_1_)).longValue();
                    double lvt_15_1_ = (double)lvt_13_1_ * 100.0D / (double)lvt_8_1_;
                    double lvt_17_1_ = (double)lvt_13_1_ * 100.0D / (double)lvt_3_1_;
                    String lvt_19_1_ = lvt_12_1_.substring(profilerName.length());
                    lvt_7_1_.add(new Profiler.Result(lvt_19_1_, lvt_15_1_, lvt_17_1_));
                }
            }

            for (String lvt_12_2_ : this.profilingMap.keySet())
            {
                this.profilingMap.put(lvt_12_2_, Long.valueOf(((Long)this.profilingMap.get(lvt_12_2_)).longValue() * 999L / 1000L));
            }

            if ((float)lvt_8_1_ > lvt_10_2_)
            {
                lvt_7_1_.add(new Profiler.Result("unspecified", (double)((float)lvt_8_1_ - lvt_10_2_) * 100.0D / (double)lvt_8_1_, (double)((float)lvt_8_1_ - lvt_10_2_) * 100.0D / (double)lvt_3_1_));
            }

            Collections.sort(lvt_7_1_);
            lvt_7_1_.add(0, new Profiler.Result(profilerName, 100.0D, (double)lvt_8_1_ * 100.0D / (double)lvt_3_1_));
            return lvt_7_1_;
        }
    }

    /**
     * End current section and start a new section
     */
    public void endStartSection(String name)
    {
        this.endSection();
        this.startSection(name);
    }

    public String getNameOfLastSection()
    {
        return this.sectionList.size() == 0 ? "[UNKNOWN]" : (String)this.sectionList.get(this.sectionList.size() - 1);
    }

    public static final class Result implements Comparable<Profiler.Result>
    {
        public double field_76332_a;
        public double field_76330_b;
        public String field_76331_c;

        public Result(String profilerName, double usePercentage, double totalUsePercentage)
        {
            this.field_76331_c = profilerName;
            this.field_76332_a = usePercentage;
            this.field_76330_b = totalUsePercentage;
        }

        public int compareTo(Profiler.Result p_compareTo_1_)
        {
            return p_compareTo_1_.field_76332_a < this.field_76332_a ? -1 : (p_compareTo_1_.field_76332_a > this.field_76332_a ? 1 : p_compareTo_1_.field_76331_c.compareTo(this.field_76331_c));
        }

        public int getColor()
        {
            return (this.field_76331_c.hashCode() & 11184810) + 4473924;
        }

        public int compareTo(Object p_compareTo_1_)
        {
            return this.compareTo((Profiler.Result)p_compareTo_1_);
        }
    }
}
