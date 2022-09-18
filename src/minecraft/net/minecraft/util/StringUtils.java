package net.minecraft.util;

import java.util.regex.Pattern;

public class StringUtils
{
    private static final Pattern patternControlCode = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

    /**
     * Returns the time elapsed for the given number of ticks, in "mm:ss" format.
     */
    public static String ticksToElapsedTime(int ticks)
    {
        int lvt_1_1_ = ticks / 20;
        int lvt_2_1_ = lvt_1_1_ / 60;
        lvt_1_1_ = lvt_1_1_ % 60;
        return lvt_1_1_ < 10 ? lvt_2_1_ + ":0" + lvt_1_1_ : lvt_2_1_ + ":" + lvt_1_1_;
    }

    public static String stripControlCodes(String text)
    {
        return patternControlCode.matcher(text).replaceAll("");
    }

    /**
     * Returns a value indicating whether the given string is null or empty.
     */
    public static boolean isNullOrEmpty(String string)
    {
        return org.apache.commons.lang3.StringUtils.isEmpty(string);
    }
}
