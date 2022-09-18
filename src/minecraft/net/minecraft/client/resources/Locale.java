package net.minecraft.client.resources;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class Locale
{
    /** Splits on "=" */
    private static final Splitter splitter = Splitter.on('=').limit(2);
    private static final Pattern pattern = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    Map<String, String> properties = Maps.newHashMap();
    private boolean unicode;

    /**
     * For each domain $D and language $L, attempts to load the resource $D:lang/$L.lang
     */
    public synchronized void loadLocaleDataFiles(IResourceManager resourceManager, List<String> languageList)
    {
        this.properties.clear();

        for (String lvt_4_1_ : languageList)
        {
            String lvt_5_1_ = String.format("lang/%s.lang", new Object[] {lvt_4_1_});

            for (String lvt_7_1_ : resourceManager.getResourceDomains())
            {
                try
                {
                    this.loadLocaleData(resourceManager.getAllResources(new ResourceLocation(lvt_7_1_, lvt_5_1_)));
                }
                catch (IOException var9)
                {
                    ;
                }
            }
        }

        this.checkUnicode();
    }

    public boolean isUnicode()
    {
        return this.unicode;
    }

    private void checkUnicode()
    {
        this.unicode = false;
        int lvt_1_1_ = 0;
        int lvt_2_1_ = 0;

        for (String lvt_4_1_ : this.properties.values())
        {
            int lvt_5_1_ = lvt_4_1_.length();
            lvt_2_1_ += lvt_5_1_;

            for (int lvt_6_1_ = 0; lvt_6_1_ < lvt_5_1_; ++lvt_6_1_)
            {
                if (lvt_4_1_.charAt(lvt_6_1_) >= 256)
                {
                    ++lvt_1_1_;
                }
            }
        }

        float lvt_3_2_ = (float)lvt_1_1_ / (float)lvt_2_1_;
        this.unicode = (double)lvt_3_2_ > 0.1D;
    }

    /**
     * Loads the locale data for the list of resources.
     */
    private void loadLocaleData(List<IResource> resourcesList) throws IOException
    {
        for (IResource lvt_3_1_ : resourcesList)
        {
            InputStream lvt_4_1_ = lvt_3_1_.getInputStream();

            try
            {
                this.loadLocaleData(lvt_4_1_);
            }
            finally
            {
                IOUtils.closeQuietly(lvt_4_1_);
            }
        }
    }

    private void loadLocaleData(InputStream inputStreamIn) throws IOException
    {
        for (String lvt_3_1_ : IOUtils.readLines(inputStreamIn, Charsets.UTF_8))
        {
            if (!lvt_3_1_.isEmpty() && lvt_3_1_.charAt(0) != 35)
            {
                String[] lvt_4_1_ = (String[])Iterables.toArray(splitter.split(lvt_3_1_), String.class);

                if (lvt_4_1_ != null && lvt_4_1_.length == 2)
                {
                    String lvt_5_1_ = lvt_4_1_[0];
                    String lvt_6_1_ = pattern.matcher(lvt_4_1_[1]).replaceAll("%$1s");
                    this.properties.put(lvt_5_1_, lvt_6_1_);
                }
            }
        }
    }

    /**
     * Returns the translation, or the key itself if the key could not be translated.
     */
    private String translateKeyPrivate(String translateKey)
    {
        String lvt_2_1_ = (String)this.properties.get(translateKey);
        return lvt_2_1_ == null ? translateKey : lvt_2_1_;
    }

    /**
     * Calls String.format(translateKey(key), params)
     */
    public String formatMessage(String translateKey, Object[] parameters)
    {
        String lvt_3_1_ = this.translateKeyPrivate(translateKey);

        try
        {
            return String.format(lvt_3_1_, parameters);
        }
        catch (IllegalFormatException var5)
        {
            return "Format error: " + lvt_3_1_;
        }
    }
}
