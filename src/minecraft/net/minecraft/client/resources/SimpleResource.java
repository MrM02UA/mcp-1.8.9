package net.minecraft.client.resources;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

public class SimpleResource implements IResource
{
    private final Map<String, IMetadataSection> mapMetadataSections = Maps.newHashMap();
    private final String resourcePackName;
    private final ResourceLocation srResourceLocation;
    private final InputStream resourceInputStream;
    private final InputStream mcmetaInputStream;
    private final IMetadataSerializer srMetadataSerializer;
    private boolean mcmetaJsonChecked;
    private JsonObject mcmetaJson;

    public SimpleResource(String resourcePackNameIn, ResourceLocation srResourceLocationIn, InputStream resourceInputStreamIn, InputStream mcmetaInputStreamIn, IMetadataSerializer srMetadataSerializerIn)
    {
        this.resourcePackName = resourcePackNameIn;
        this.srResourceLocation = srResourceLocationIn;
        this.resourceInputStream = resourceInputStreamIn;
        this.mcmetaInputStream = mcmetaInputStreamIn;
        this.srMetadataSerializer = srMetadataSerializerIn;
    }

    public ResourceLocation getResourceLocation()
    {
        return this.srResourceLocation;
    }

    public InputStream getInputStream()
    {
        return this.resourceInputStream;
    }

    public boolean hasMetadata()
    {
        return this.mcmetaInputStream != null;
    }

    public <T extends IMetadataSection> T getMetadata(String p_110526_1_)
    {
        if (!this.hasMetadata())
        {
            return (T)null;
        }
        else
        {
            if (this.mcmetaJson == null && !this.mcmetaJsonChecked)
            {
                this.mcmetaJsonChecked = true;
                BufferedReader lvt_2_1_ = null;

                try
                {
                    lvt_2_1_ = new BufferedReader(new InputStreamReader(this.mcmetaInputStream));
                    this.mcmetaJson = (new JsonParser()).parse(lvt_2_1_).getAsJsonObject();
                }
                finally
                {
                    IOUtils.closeQuietly(lvt_2_1_);
                }
            }

            T lvt_2_2_ = (IMetadataSection)this.mapMetadataSections.get(p_110526_1_);

            if (lvt_2_2_ == null)
            {
                lvt_2_2_ = this.srMetadataSerializer.parseMetadataSection(p_110526_1_, this.mcmetaJson);
            }

            return lvt_2_2_;
        }
    }

    public String getResourcePackName()
    {
        return this.resourcePackName;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof SimpleResource))
        {
            return false;
        }
        else
        {
            SimpleResource lvt_2_1_ = (SimpleResource)p_equals_1_;

            if (this.srResourceLocation != null)
            {
                if (!this.srResourceLocation.equals(lvt_2_1_.srResourceLocation))
                {
                    return false;
                }
            }
            else if (lvt_2_1_.srResourceLocation != null)
            {
                return false;
            }

            if (this.resourcePackName != null)
            {
                if (!this.resourcePackName.equals(lvt_2_1_.resourcePackName))
                {
                    return false;
                }
            }
            else if (lvt_2_1_.resourcePackName != null)
            {
                return false;
            }

            return true;
        }
    }

    public int hashCode()
    {
        int lvt_1_1_ = this.resourcePackName != null ? this.resourcePackName.hashCode() : 0;
        lvt_1_1_ = 31 * lvt_1_1_ + (this.srResourceLocation != null ? this.srResourceLocation.hashCode() : 0);
        return lvt_1_1_;
    }
}
