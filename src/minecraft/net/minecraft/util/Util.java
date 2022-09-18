package net.minecraft.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import org.apache.logging.log4j.Logger;

public class Util
{
    public static Util.EnumOS getOSType()
    {
        String lvt_0_1_ = System.getProperty("os.name").toLowerCase();
        return lvt_0_1_.contains("win") ? Util.EnumOS.WINDOWS : (lvt_0_1_.contains("mac") ? Util.EnumOS.OSX : (lvt_0_1_.contains("solaris") ? Util.EnumOS.SOLARIS : (lvt_0_1_.contains("sunos") ? Util.EnumOS.SOLARIS : (lvt_0_1_.contains("linux") ? Util.EnumOS.LINUX : (lvt_0_1_.contains("unix") ? Util.EnumOS.LINUX : Util.EnumOS.UNKNOWN)))));
    }

    public static <V> V runTask(FutureTask<V> task, Logger logger)
    {
        try
        {
            task.run();
            return (V)task.get();
        }
        catch (ExecutionException var3)
        {
            logger.fatal("Error executing task", var3);
        }
        catch (InterruptedException var4)
        {
            logger.fatal("Error executing task", var4);
        }

        return (V)null;
    }

    public static enum EnumOS
    {
        LINUX,
        SOLARIS,
        WINDOWS,
        OSX,
        UNKNOWN;
    }
}
