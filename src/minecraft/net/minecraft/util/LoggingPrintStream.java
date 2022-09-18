package net.minecraft.util;

import java.io.OutputStream;
import java.io.PrintStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggingPrintStream extends PrintStream
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final String domain;

    public LoggingPrintStream(String domainIn, OutputStream outStream)
    {
        super(outStream);
        this.domain = domainIn;
    }

    public void println(String p_println_1_)
    {
        this.logString(p_println_1_);
    }

    public void println(Object p_println_1_)
    {
        this.logString(String.valueOf(p_println_1_));
    }

    private void logString(String string)
    {
        StackTraceElement[] lvt_2_1_ = Thread.currentThread().getStackTrace();
        StackTraceElement lvt_3_1_ = lvt_2_1_[Math.min(3, lvt_2_1_.length)];
        LOGGER.info("[{}]@.({}:{}): {}", new Object[] {this.domain, lvt_3_1_.getFileName(), Integer.valueOf(lvt_3_1_.getLineNumber()), string});
    }
}
