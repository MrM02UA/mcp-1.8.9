package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.util.StringTranslate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LanguageManager implements IResourceManagerReloadListener
{
    private static final Logger logger = LogManager.getLogger();
    private final IMetadataSerializer theMetadataSerializer;
    private String currentLanguage;
    protected static final Locale currentLocale = new Locale();
    private Map<String, Language> languageMap = Maps.newHashMap();

    public LanguageManager(IMetadataSerializer theMetadataSerializerIn, String currentLanguageIn)
    {
        this.theMetadataSerializer = theMetadataSerializerIn;
        this.currentLanguage = currentLanguageIn;
        I18n.setLocale(currentLocale);
    }

    public void parseLanguageMetadata(List<IResourcePack> resourcesPacks)
    {
        this.languageMap.clear();

        for (IResourcePack lvt_3_1_ : resourcesPacks)
        {
            try
            {
                LanguageMetadataSection lvt_4_1_ = (LanguageMetadataSection)lvt_3_1_.getPackMetadata(this.theMetadataSerializer, "language");

                if (lvt_4_1_ != null)
                {
                    for (Language lvt_6_1_ : lvt_4_1_.getLanguages())
                    {
                        if (!this.languageMap.containsKey(lvt_6_1_.getLanguageCode()))
                        {
                            this.languageMap.put(lvt_6_1_.getLanguageCode(), lvt_6_1_);
                        }
                    }
                }
            }
            catch (RuntimeException var7)
            {
                logger.warn("Unable to parse metadata section of resourcepack: " + lvt_3_1_.getPackName(), var7);
            }
            catch (IOException var8)
            {
                logger.warn("Unable to parse metadata section of resourcepack: " + lvt_3_1_.getPackName(), var8);
            }
        }
    }

    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        List<String> lvt_2_1_ = Lists.newArrayList(new String[] {"en_US"});

        if (!"en_US".equals(this.currentLanguage))
        {
            lvt_2_1_.add(this.currentLanguage);
        }

        currentLocale.loadLocaleDataFiles(resourceManager, lvt_2_1_);
        StringTranslate.replaceWith(currentLocale.properties);
    }

    public boolean isCurrentLocaleUnicode()
    {
        return currentLocale.isUnicode();
    }

    public boolean isCurrentLanguageBidirectional()
    {
        return this.getCurrentLanguage() != null && this.getCurrentLanguage().isBidirectional();
    }

    public void setCurrentLanguage(Language currentLanguageIn)
    {
        this.currentLanguage = currentLanguageIn.getLanguageCode();
    }

    public Language getCurrentLanguage()
    {
        return this.languageMap.containsKey(this.currentLanguage) ? (Language)this.languageMap.get(this.currentLanguage) : (Language)this.languageMap.get("en_US");
    }

    public SortedSet<Language> getLanguages()
    {
        return Sets.newTreeSet(this.languageMap.values());
    }
}
