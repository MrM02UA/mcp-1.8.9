package net.minecraft.client.resources;

import com.google.common.collect.ImmutableSet;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

public class DefaultResourcePack implements IResourcePack
{
    public static final Set<String> defaultResourceDomains = ImmutableSet.of("minecraft", "realms");
    private final Map<String, File> mapAssets;

    public DefaultResourcePack(Map<String, File> mapAssetsIn)
    {
        this.mapAssets = mapAssetsIn;
    }

    public InputStream getInputStream(ResourceLocation location) throws IOException
    {
        InputStream lvt_2_1_ = this.getResourceStream(location);

        if (lvt_2_1_ != null)
        {
            return lvt_2_1_;
        }
        else
        {
            InputStream lvt_3_1_ = this.getInputStreamAssets(location);

            if (lvt_3_1_ != null)
            {
                return lvt_3_1_;
            }
            else
            {
                throw new FileNotFoundException(location.getResourcePath());
            }
        }
    }

    public InputStream getInputStreamAssets(ResourceLocation location) throws IOException, FileNotFoundException
    {
        File lvt_2_1_ = (File)this.mapAssets.get(location.toString());
        return lvt_2_1_ != null && lvt_2_1_.isFile() ? new FileInputStream(lvt_2_1_) : null;
    }

    private InputStream getResourceStream(ResourceLocation location)
    {
        return DefaultResourcePack.class.getResourceAsStream("/assets/" + location.getResourceDomain() + "/" + location.getResourcePath());
    }

    public boolean resourceExists(ResourceLocation location)
    {
        return this.getResourceStream(location) != null || this.mapAssets.containsKey(location.toString());
    }

    public Set<String> getResourceDomains()
    {
        return defaultResourceDomains;
    }

    public <T extends IMetadataSection> T getPackMetadata(IMetadataSerializer metadataSerializer, String metadataSectionName) throws IOException
    {
        try
        {
            InputStream lvt_3_1_ = new FileInputStream((File)this.mapAssets.get("pack.mcmeta"));
            return AbstractResourcePack.readMetadata(metadataSerializer, lvt_3_1_, metadataSectionName);
        }
        catch (RuntimeException var4)
        {
            return (T)null;
        }
        catch (FileNotFoundException var5)
        {
            return (T)null;
        }
    }

    public BufferedImage getPackImage() throws IOException
    {
        return TextureUtil.readBufferedImage(DefaultResourcePack.class.getResourceAsStream("/" + (new ResourceLocation("pack.png")).getResourcePath()));
    }

    public String getPackName()
    {
        return "Default";
    }
}
