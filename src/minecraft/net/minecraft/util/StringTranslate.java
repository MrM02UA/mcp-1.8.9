package net.minecraft.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.util.IllegalFormatException;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class StringTranslate
{
    /**
     * Pattern that matches numeric variable placeholders in a resource string, such as "%d", "%3$d", "%.2f"
     */
    private static final Pattern numericVariablePattern = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");

    /**
     * A Splitter that splits a string on the first "=".  For example, "a=b=c" would split into ["a", "b=c"].
     */
    private static final Splitter equalSignSplitter = Splitter.on('=').limit(2);

    /** Is the private singleton instance of StringTranslate. */
    private static StringTranslate instance = new StringTranslate();
    private final Map<String, String> languageList = Maps.newHashMap();

    /**
     * The time, in milliseconds since epoch, that this instance was last updated
     */
    private long lastUpdateTimeInMilliseconds;

    public StringTranslate()
    {
        try
        {
            InputStream lvt_1_1_ = StringTranslate.class.getResourceAsStream("/assets/minecraft/lang/en_US.lang");

            for (String lvt_3_1_ : IOUtils.readLines(lvt_1_1_, Charsets.UTF_8))
            {
                if (!lvt_3_1_.isEmpty() && lvt_3_1_.charAt(0) != 35)
                {
                    String[] lvt_4_1_ = (String[])Iterables.toArray(equalSignSplitter.split(lvt_3_1_), String.class);

                    if (lvt_4_1_ != null && lvt_4_1_.length == 2)
                    {
                        String lvt_5_1_ = lvt_4_1_[0];
                        String lvt_6_1_ = numericVariablePattern.matcher(lvt_4_1_[1]).replaceAll("%$1s");
                        this.languageList.put(lvt_5_1_, lvt_6_1_);
                    }
                }
            }

            this.lastUpdateTimeInMilliseconds = System.currentTimeMillis();
        }
        catch (IOException var7)
        {
            ;
        }
    }

    /**
     * Return the StringTranslate singleton instance
     */
    static StringTranslate getInstance()
    {
        return instance;
    }

    /**
     * Replaces all the current instance's translations with the ones that are passed in.
     */
    public static synchronized void replaceWith(Map<String, String> p_135063_0_)
    {
        instance.languageList.clear();
        instance.languageList.putAll(p_135063_0_);
        instance.lastUpdateTimeInMilliseconds = System.currentTimeMillis();
    }

    /**
     * Translate a key to current language.
     */
    public synchronized String translateKey(String key)
    {
        return this.tryTranslateKey(key);
    }

    /**
     * Translate a key to current language applying String.format()
     */
    public synchronized String translateKeyFormat(String key, Object... format)
    {
        String lvt_3_1_ = this.tryTranslateKey(key);

        try
        {
            return String.format(lvt_3_1_, format);
        }
        catch (IllegalFormatException var5)
        {
            return "Format error: " + lvt_3_1_;
        }
    }

    /**
     * Tries to look up a translation for the given key; spits back the key if no result was found.
     */
    private String tryTranslateKey(String key)
    {
        String lvt_2_1_ = (String)this.languageList.get(key);
        return lvt_2_1_ == null ? key : lvt_2_1_;
    }

    /**
     * Returns true if the passed key is in the translation table.
     */
    public synchronized boolean isKeyTranslated(String key)
    {
        return this.languageList.containsKey(key);
    }

    /**
     * Gets the time, in milliseconds since epoch, that this instance was last updated
     */
    public long getLastUpdateTimeInMilliseconds()
    {
        return this.lastUpdateTimeInMilliseconds;
    }
}
