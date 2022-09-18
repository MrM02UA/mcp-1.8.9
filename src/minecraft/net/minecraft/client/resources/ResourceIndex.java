package net.minecraft.client.resources;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.JsonUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourceIndex
{
    private static final Logger logger = LogManager.getLogger();
    private final Map<String, File> resourceMap = Maps.newHashMap();

    public ResourceIndex(File p_i1047_1_, String p_i1047_2_)
    {
        if (p_i1047_2_ != null)
        {
            File lvt_3_1_ = new File(p_i1047_1_, "objects");
            File lvt_4_1_ = new File(p_i1047_1_, "indexes/" + p_i1047_2_ + ".json");
            BufferedReader lvt_5_1_ = null;

            try
            {
                lvt_5_1_ = Files.newReader(lvt_4_1_, Charsets.UTF_8);
                JsonObject lvt_6_1_ = (new JsonParser()).parse(lvt_5_1_).getAsJsonObject();
                JsonObject lvt_7_1_ = JsonUtils.getJsonObject(lvt_6_1_, "objects", (JsonObject)null);

                if (lvt_7_1_ != null)
                {
                    for (Entry<String, JsonElement> lvt_9_1_ : lvt_7_1_.entrySet())
                    {
                        JsonObject lvt_10_1_ = (JsonObject)lvt_9_1_.getValue();
                        String lvt_11_1_ = (String)lvt_9_1_.getKey();
                        String[] lvt_12_1_ = lvt_11_1_.split("/", 2);
                        String lvt_13_1_ = lvt_12_1_.length == 1 ? lvt_12_1_[0] : lvt_12_1_[0] + ":" + lvt_12_1_[1];
                        String lvt_14_1_ = JsonUtils.getString(lvt_10_1_, "hash");
                        File lvt_15_1_ = new File(lvt_3_1_, lvt_14_1_.substring(0, 2) + "/" + lvt_14_1_);
                        this.resourceMap.put(lvt_13_1_, lvt_15_1_);
                    }
                }
            }
            catch (JsonParseException var20)
            {
                logger.error("Unable to parse resource index file: " + lvt_4_1_);
            }
            catch (FileNotFoundException var21)
            {
                logger.error("Can\'t find the resource index file: " + lvt_4_1_);
            }
            finally
            {
                IOUtils.closeQuietly(lvt_5_1_);
            }
        }
    }

    public Map<String, File> getResourceMap()
    {
        return this.resourceMap;
    }
}
