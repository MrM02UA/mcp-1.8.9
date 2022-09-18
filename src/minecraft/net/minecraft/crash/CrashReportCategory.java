package net.minecraft.crash;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

public class CrashReportCategory
{
    private final CrashReport crashReport;
    private final String name;
    private final List<CrashReportCategory.Entry> children = Lists.newArrayList();
    private StackTraceElement[] stackTrace = new StackTraceElement[0];

    public CrashReportCategory(CrashReport report, String name)
    {
        this.crashReport = report;
        this.name = name;
    }

    public static String getCoordinateInfo(double x, double y, double z)
    {
        return String.format("%.2f,%.2f,%.2f - %s", new Object[] {Double.valueOf(x), Double.valueOf(y), Double.valueOf(z), getCoordinateInfo(new BlockPos(x, y, z))});
    }

    public static String getCoordinateInfo(BlockPos pos)
    {
        int lvt_1_1_ = pos.getX();
        int lvt_2_1_ = pos.getY();
        int lvt_3_1_ = pos.getZ();
        StringBuilder lvt_4_1_ = new StringBuilder();

        try
        {
            lvt_4_1_.append(String.format("World: (%d,%d,%d)", new Object[] {Integer.valueOf(lvt_1_1_), Integer.valueOf(lvt_2_1_), Integer.valueOf(lvt_3_1_)}));
        }
        catch (Throwable var17)
        {
            lvt_4_1_.append("(Error finding world loc)");
        }

        lvt_4_1_.append(", ");

        try
        {
            int lvt_5_2_ = lvt_1_1_ >> 4;
            int lvt_6_1_ = lvt_3_1_ >> 4;
            int lvt_7_1_ = lvt_1_1_ & 15;
            int lvt_8_1_ = lvt_2_1_ >> 4;
            int lvt_9_1_ = lvt_3_1_ & 15;
            int lvt_10_1_ = lvt_5_2_ << 4;
            int lvt_11_1_ = lvt_6_1_ << 4;
            int lvt_12_1_ = (lvt_5_2_ + 1 << 4) - 1;
            int lvt_13_1_ = (lvt_6_1_ + 1 << 4) - 1;
            lvt_4_1_.append(String.format("Chunk: (at %d,%d,%d in %d,%d; contains blocks %d,0,%d to %d,255,%d)", new Object[] {Integer.valueOf(lvt_7_1_), Integer.valueOf(lvt_8_1_), Integer.valueOf(lvt_9_1_), Integer.valueOf(lvt_5_2_), Integer.valueOf(lvt_6_1_), Integer.valueOf(lvt_10_1_), Integer.valueOf(lvt_11_1_), Integer.valueOf(lvt_12_1_), Integer.valueOf(lvt_13_1_)}));
        }
        catch (Throwable var16)
        {
            lvt_4_1_.append("(Error finding chunk loc)");
        }

        lvt_4_1_.append(", ");

        try
        {
            int lvt_5_4_ = lvt_1_1_ >> 9;
            int lvt_6_2_ = lvt_3_1_ >> 9;
            int lvt_7_2_ = lvt_5_4_ << 5;
            int lvt_8_2_ = lvt_6_2_ << 5;
            int lvt_9_2_ = (lvt_5_4_ + 1 << 5) - 1;
            int lvt_10_2_ = (lvt_6_2_ + 1 << 5) - 1;
            int lvt_11_2_ = lvt_5_4_ << 9;
            int lvt_12_2_ = lvt_6_2_ << 9;
            int lvt_13_2_ = (lvt_5_4_ + 1 << 9) - 1;
            int lvt_14_1_ = (lvt_6_2_ + 1 << 9) - 1;
            lvt_4_1_.append(String.format("Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,0,%d to %d,255,%d)", new Object[] {Integer.valueOf(lvt_5_4_), Integer.valueOf(lvt_6_2_), Integer.valueOf(lvt_7_2_), Integer.valueOf(lvt_8_2_), Integer.valueOf(lvt_9_2_), Integer.valueOf(lvt_10_2_), Integer.valueOf(lvt_11_2_), Integer.valueOf(lvt_12_2_), Integer.valueOf(lvt_13_2_), Integer.valueOf(lvt_14_1_)}));
        }
        catch (Throwable var15)
        {
            lvt_4_1_.append("(Error finding world loc)");
        }

        return lvt_4_1_.toString();
    }

    /**
     * Adds a Crashreport section with the given name with the value set to the result of the given Callable;
     */
    public void addCrashSectionCallable(String sectionName, Callable<String> callable)
    {
        try
        {
            this.addCrashSection(sectionName, callable.call());
        }
        catch (Throwable var4)
        {
            this.addCrashSectionThrowable(sectionName, var4);
        }
    }

    /**
     * Adds a Crashreport section with the given name with the given value (convered .toString())
     */
    public void addCrashSection(String sectionName, Object value)
    {
        this.children.add(new CrashReportCategory.Entry(sectionName, value));
    }

    /**
     * Adds a Crashreport section with the given name with the given Throwable
     */
    public void addCrashSectionThrowable(String sectionName, Throwable throwable)
    {
        this.addCrashSection(sectionName, throwable);
    }

    /**
     * Resets our stack trace according to the current trace, pruning the deepest 3 entries.  The parameter indicates
     * how many additional deepest entries to prune.  Returns the number of entries in the resulting pruned stack trace.
     */
    public int getPrunedStackTrace(int size)
    {
        StackTraceElement[] lvt_2_1_ = Thread.currentThread().getStackTrace();

        if (lvt_2_1_.length <= 0)
        {
            return 0;
        }
        else
        {
            this.stackTrace = new StackTraceElement[lvt_2_1_.length - 3 - size];
            System.arraycopy(lvt_2_1_, 3 + size, this.stackTrace, 0, this.stackTrace.length);
            return this.stackTrace.length;
        }
    }

    /**
     * Do the deepest two elements of our saved stack trace match the given elements, in order from the deepest?
     */
    public boolean firstTwoElementsOfStackTraceMatch(StackTraceElement s1, StackTraceElement s2)
    {
        if (this.stackTrace.length != 0 && s1 != null)
        {
            StackTraceElement lvt_3_1_ = this.stackTrace[0];

            if (lvt_3_1_.isNativeMethod() == s1.isNativeMethod() && lvt_3_1_.getClassName().equals(s1.getClassName()) && lvt_3_1_.getFileName().equals(s1.getFileName()) && lvt_3_1_.getMethodName().equals(s1.getMethodName()))
            {
                if (s2 != null != this.stackTrace.length > 1)
                {
                    return false;
                }
                else if (s2 != null && !this.stackTrace[1].equals(s2))
                {
                    return false;
                }
                else
                {
                    this.stackTrace[0] = s1;
                    return true;
                }
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Removes the given number entries from the bottom of the stack trace.
     */
    public void trimStackTraceEntriesFromBottom(int amount)
    {
        StackTraceElement[] lvt_2_1_ = new StackTraceElement[this.stackTrace.length - amount];
        System.arraycopy(this.stackTrace, 0, lvt_2_1_, 0, lvt_2_1_.length);
        this.stackTrace = lvt_2_1_;
    }

    public void appendToStringBuilder(StringBuilder builder)
    {
        builder.append("-- ").append(this.name).append(" --\n");
        builder.append("Details:");

        for (CrashReportCategory.Entry lvt_3_1_ : this.children)
        {
            builder.append("\n\t");
            builder.append(lvt_3_1_.getKey());
            builder.append(": ");
            builder.append(lvt_3_1_.getValue());
        }

        if (this.stackTrace != null && this.stackTrace.length > 0)
        {
            builder.append("\nStacktrace:");

            for (StackTraceElement lvt_5_1_ : this.stackTrace)
            {
                builder.append("\n\tat ");
                builder.append(lvt_5_1_.toString());
            }
        }
    }

    public StackTraceElement[] getStackTrace()
    {
        return this.stackTrace;
    }

    public static void addBlockInfo(CrashReportCategory category, final BlockPos pos, final Block blockIn, final int blockData)
    {
        final int lvt_4_1_ = Block.getIdFromBlock(blockIn);
        category.addCrashSectionCallable("Block type", new Callable<String>()
        {
            public String call() throws Exception
            {
                try
                {
                    return String.format("ID #%d (%s // %s)", new Object[] {Integer.valueOf(lvt_4_1_), blockIn.getUnlocalizedName(), blockIn.getClass().getCanonicalName()});
                }
                catch (Throwable var2)
                {
                    return "ID #" + lvt_4_1_;
                }
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        category.addCrashSectionCallable("Block data value", new Callable<String>()
        {
            public String call() throws Exception
            {
                if (blockData < 0)
                {
                    return "Unknown? (Got " + blockData + ")";
                }
                else
                {
                    String lvt_1_1_ = String.format("%4s", new Object[] {Integer.toBinaryString(blockData)}).replace(" ", "0");
                    return String.format("%1$d / 0x%1$X / 0b%2$s", new Object[] {Integer.valueOf(blockData), lvt_1_1_});
                }
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        category.addCrashSectionCallable("Block location", new Callable<String>()
        {
            public String call() throws Exception
            {
                return CrashReportCategory.getCoordinateInfo(pos);
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
    }

    public static void addBlockInfo(CrashReportCategory category, final BlockPos pos, final IBlockState state)
    {
        category.addCrashSectionCallable("Block", new Callable<String>()
        {
            public String call() throws Exception
            {
                return state.toString();
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        category.addCrashSectionCallable("Block location", new Callable<String>()
        {
            public String call() throws Exception
            {
                return CrashReportCategory.getCoordinateInfo(pos);
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
    }

    static class Entry
    {
        private final String key;
        private final String value;

        public Entry(String key, Object value)
        {
            this.key = key;

            if (value == null)
            {
                this.value = "~~NULL~~";
            }
            else if (value instanceof Throwable)
            {
                Throwable lvt_3_1_ = (Throwable)value;
                this.value = "~~ERROR~~ " + lvt_3_1_.getClass().getSimpleName() + ": " + lvt_3_1_.getMessage();
            }
            else
            {
                this.value = value.toString();
            }
        }

        public String getKey()
        {
            return this.key;
        }

        public String getValue()
        {
            return this.value;
        }
    }
}
