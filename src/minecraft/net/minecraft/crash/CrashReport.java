package net.minecraft.crash;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.util.ReportedException;
import net.minecraft.world.gen.layer.IntCache;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrashReport
{
    private static final Logger logger = LogManager.getLogger();

    /** Description of the crash report. */
    private final String description;

    /** The Throwable that is the "cause" for this crash and Crash Report. */
    private final Throwable cause;

    /** Category of crash */
    private final CrashReportCategory theReportCategory = new CrashReportCategory(this, "System Details");
    private final List<CrashReportCategory> crashReportSections = Lists.newArrayList();

    /** File of crash report. */
    private File crashReportFile;

    /** Is true when the current category is the first in the crash report */
    private boolean firstCategoryInCrashReport = true;
    private StackTraceElement[] stacktrace = new StackTraceElement[0];

    public CrashReport(String descriptionIn, Throwable causeThrowable)
    {
        this.description = descriptionIn;
        this.cause = causeThrowable;
        this.populateEnvironment();
    }

    /**
     * Populates this crash report with initial information about the running server and operating system / java
     * environment
     */
    private void populateEnvironment()
    {
        this.theReportCategory.addCrashSectionCallable("Minecraft Version", new Callable<String>()
        {
            public String call()
            {
                return "1.8.9";
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        this.theReportCategory.addCrashSectionCallable("Operating System", new Callable<String>()
        {
            public String call()
            {
                return System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version");
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        this.theReportCategory.addCrashSectionCallable("Java Version", new Callable<String>()
        {
            public String call()
            {
                return System.getProperty("java.version") + ", " + System.getProperty("java.vendor");
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        this.theReportCategory.addCrashSectionCallable("Java VM Version", new Callable<String>()
        {
            public String call()
            {
                return System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        this.theReportCategory.addCrashSectionCallable("Memory", new Callable<String>()
        {
            public String call()
            {
                Runtime lvt_1_1_ = Runtime.getRuntime();
                long lvt_2_1_ = lvt_1_1_.maxMemory();
                long lvt_4_1_ = lvt_1_1_.totalMemory();
                long lvt_6_1_ = lvt_1_1_.freeMemory();
                long lvt_8_1_ = lvt_2_1_ / 1024L / 1024L;
                long lvt_10_1_ = lvt_4_1_ / 1024L / 1024L;
                long lvt_12_1_ = lvt_6_1_ / 1024L / 1024L;
                return lvt_6_1_ + " bytes (" + lvt_12_1_ + " MB) / " + lvt_4_1_ + " bytes (" + lvt_10_1_ + " MB) up to " + lvt_2_1_ + " bytes (" + lvt_8_1_ + " MB)";
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        this.theReportCategory.addCrashSectionCallable("JVM Flags", new Callable<String>()
        {
            public String call()
            {
                RuntimeMXBean lvt_1_1_ = ManagementFactory.getRuntimeMXBean();
                List<String> lvt_2_1_ = lvt_1_1_.getInputArguments();
                int lvt_3_1_ = 0;
                StringBuilder lvt_4_1_ = new StringBuilder();

                for (String lvt_6_1_ : lvt_2_1_)
                {
                    if (lvt_6_1_.startsWith("-X"))
                    {
                        if (lvt_3_1_++ > 0)
                        {
                            lvt_4_1_.append(" ");
                        }

                        lvt_4_1_.append(lvt_6_1_);
                    }
                }

                return String.format("%d total; %s", new Object[] {Integer.valueOf(lvt_3_1_), lvt_4_1_.toString()});
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        this.theReportCategory.addCrashSectionCallable("IntCache", new Callable<String>()
        {
            public String call() throws Exception
            {
                return IntCache.getCacheSizes();
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
    }

    /**
     * Returns the description of the Crash Report.
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * Returns the Throwable object that is the cause for the crash and Crash Report.
     */
    public Throwable getCrashCause()
    {
        return this.cause;
    }

    /**
     * Gets the various sections of the crash report into the given StringBuilder
     */
    public void getSectionsInStringBuilder(StringBuilder builder)
    {
        if ((this.stacktrace == null || this.stacktrace.length <= 0) && this.crashReportSections.size() > 0)
        {
            this.stacktrace = (StackTraceElement[])ArrayUtils.subarray(((CrashReportCategory)this.crashReportSections.get(0)).getStackTrace(), 0, 1);
        }

        if (this.stacktrace != null && this.stacktrace.length > 0)
        {
            builder.append("-- Head --\n");
            builder.append("Stacktrace:\n");

            for (StackTraceElement lvt_5_1_ : this.stacktrace)
            {
                builder.append("\t").append("at ").append(lvt_5_1_.toString());
                builder.append("\n");
            }

            builder.append("\n");
        }

        for (CrashReportCategory lvt_3_2_ : this.crashReportSections)
        {
            lvt_3_2_.appendToStringBuilder(builder);
            builder.append("\n\n");
        }

        this.theReportCategory.appendToStringBuilder(builder);
    }

    /**
     * Gets the stack trace of the Throwable that caused this crash report, or if that fails, the cause .toString().
     */
    public String getCauseStackTraceOrString()
    {
        StringWriter lvt_1_1_ = null;
        PrintWriter lvt_2_1_ = null;
        Throwable lvt_3_1_ = this.cause;

        if (lvt_3_1_.getMessage() == null)
        {
            if (lvt_3_1_ instanceof NullPointerException)
            {
                lvt_3_1_ = new NullPointerException(this.description);
            }
            else if (lvt_3_1_ instanceof StackOverflowError)
            {
                lvt_3_1_ = new StackOverflowError(this.description);
            }
            else if (lvt_3_1_ instanceof OutOfMemoryError)
            {
                lvt_3_1_ = new OutOfMemoryError(this.description);
            }

            lvt_3_1_.setStackTrace(this.cause.getStackTrace());
        }

        String lvt_4_1_ = lvt_3_1_.toString();

        try
        {
            lvt_1_1_ = new StringWriter();
            lvt_2_1_ = new PrintWriter(lvt_1_1_);
            lvt_3_1_.printStackTrace(lvt_2_1_);
            lvt_4_1_ = lvt_1_1_.toString();
        }
        finally
        {
            IOUtils.closeQuietly(lvt_1_1_);
            IOUtils.closeQuietly(lvt_2_1_);
        }

        return lvt_4_1_;
    }

    /**
     * Gets the complete report with headers, stack trace, and different sections as a string.
     */
    public String getCompleteReport()
    {
        StringBuilder lvt_1_1_ = new StringBuilder();
        lvt_1_1_.append("---- Minecraft Crash Report ----\n");
        lvt_1_1_.append("// ");
        lvt_1_1_.append(getWittyComment());
        lvt_1_1_.append("\n\n");
        lvt_1_1_.append("Time: ");
        lvt_1_1_.append((new SimpleDateFormat()).format(new Date()));
        lvt_1_1_.append("\n");
        lvt_1_1_.append("Description: ");
        lvt_1_1_.append(this.description);
        lvt_1_1_.append("\n\n");
        lvt_1_1_.append(this.getCauseStackTraceOrString());
        lvt_1_1_.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");

        for (int lvt_2_1_ = 0; lvt_2_1_ < 87; ++lvt_2_1_)
        {
            lvt_1_1_.append("-");
        }

        lvt_1_1_.append("\n\n");
        this.getSectionsInStringBuilder(lvt_1_1_);
        return lvt_1_1_.toString();
    }

    /**
     * Gets the file this crash report is saved into.
     */
    public File getFile()
    {
        return this.crashReportFile;
    }

    /**
     * Saves this CrashReport to the given file and returns a value indicating whether we were successful at doing so.
     */
    public boolean saveToFile(File toFile)
    {
        if (this.crashReportFile != null)
        {
            return false;
        }
        else
        {
            if (toFile.getParentFile() != null)
            {
                toFile.getParentFile().mkdirs();
            }

            try
            {
                FileWriter lvt_2_1_ = new FileWriter(toFile);
                lvt_2_1_.write(this.getCompleteReport());
                lvt_2_1_.close();
                this.crashReportFile = toFile;
                return true;
            }
            catch (Throwable var3)
            {
                logger.error("Could not save crash report to " + toFile, var3);
                return false;
            }
        }
    }

    public CrashReportCategory getCategory()
    {
        return this.theReportCategory;
    }

    /**
     * Creates a CrashReportCategory
     */
    public CrashReportCategory makeCategory(String name)
    {
        return this.makeCategoryDepth(name, 1);
    }

    /**
     * Creates a CrashReportCategory for the given stack trace depth
     */
    public CrashReportCategory makeCategoryDepth(String categoryName, int stacktraceLength)
    {
        CrashReportCategory lvt_3_1_ = new CrashReportCategory(this, categoryName);

        if (this.firstCategoryInCrashReport)
        {
            int lvt_4_1_ = lvt_3_1_.getPrunedStackTrace(stacktraceLength);
            StackTraceElement[] lvt_5_1_ = this.cause.getStackTrace();
            StackTraceElement lvt_6_1_ = null;
            StackTraceElement lvt_7_1_ = null;
            int lvt_8_1_ = lvt_5_1_.length - lvt_4_1_;

            if (lvt_8_1_ < 0)
            {
                System.out.println("Negative index in crash report handler (" + lvt_5_1_.length + "/" + lvt_4_1_ + ")");
            }

            if (lvt_5_1_ != null && 0 <= lvt_8_1_ && lvt_8_1_ < lvt_5_1_.length)
            {
                lvt_6_1_ = lvt_5_1_[lvt_8_1_];

                if (lvt_5_1_.length + 1 - lvt_4_1_ < lvt_5_1_.length)
                {
                    lvt_7_1_ = lvt_5_1_[lvt_5_1_.length + 1 - lvt_4_1_];
                }
            }

            this.firstCategoryInCrashReport = lvt_3_1_.firstTwoElementsOfStackTraceMatch(lvt_6_1_, lvt_7_1_);

            if (lvt_4_1_ > 0 && !this.crashReportSections.isEmpty())
            {
                CrashReportCategory lvt_9_1_ = (CrashReportCategory)this.crashReportSections.get(this.crashReportSections.size() - 1);
                lvt_9_1_.trimStackTraceEntriesFromBottom(lvt_4_1_);
            }
            else if (lvt_5_1_ != null && lvt_5_1_.length >= lvt_4_1_ && 0 <= lvt_8_1_ && lvt_8_1_ < lvt_5_1_.length)
            {
                this.stacktrace = new StackTraceElement[lvt_8_1_];
                System.arraycopy(lvt_5_1_, 0, this.stacktrace, 0, this.stacktrace.length);
            }
            else
            {
                this.firstCategoryInCrashReport = false;
            }
        }

        this.crashReportSections.add(lvt_3_1_);
        return lvt_3_1_;
    }

    /**
     * Gets a random witty comment for inclusion in this CrashReport
     */
    private static String getWittyComment()
    {
        String[] lvt_0_1_ = new String[] {"Who set us up the TNT?", "Everything\'s going to plan. No, really, that was supposed to happen.", "Uh... Did I do that?", "Oops.", "Why did you do that?", "I feel sad now :(", "My bad.", "I\'m sorry, Dave.", "I let you down. Sorry :(", "On the bright side, I bought you a teddy bear!", "Daisy, daisy...", "Oh - I know what I did wrong!", "Hey, that tickles! Hehehe!", "I blame Dinnerbone.", "You should try our sister game, Minceraft!", "Don\'t be sad. I\'ll do better next time, I promise!", "Don\'t be sad, have a hug! <3", "I just don\'t know what went wrong :(", "Shall we play a game?", "Quite honestly, I wouldn\'t worry myself about that.", "I bet Cylons wouldn\'t have this problem.", "Sorry :(", "Surprise! Haha. Well, this is awkward.", "Would you like a cupcake?", "Hi. I\'m Minecraft, and I\'m a crashaholic.", "Ooh. Shiny.", "This doesn\'t make any sense!", "Why is it breaking :(", "Don\'t do that.", "Ouch. That hurt :(", "You\'re mean.", "This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]", "There are four lights!", "But it works on my machine."};

        try
        {
            return lvt_0_1_[(int)(System.nanoTime() % (long)lvt_0_1_.length)];
        }
        catch (Throwable var2)
        {
            return "Witty comment unavailable :(";
        }
    }

    /**
     * Creates a crash report for the exception
     */
    public static CrashReport makeCrashReport(Throwable causeIn, String descriptionIn)
    {
        CrashReport lvt_2_1_;

        if (causeIn instanceof ReportedException)
        {
            lvt_2_1_ = ((ReportedException)causeIn).getCrashReport();
        }
        else
        {
            lvt_2_1_ = new CrashReport(descriptionIn, causeIn);
        }

        return lvt_2_1_;
    }
}
