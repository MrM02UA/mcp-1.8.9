package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Set;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FallbackResourceManager implements IResourceManager
{
    private static final Logger logger = LogManager.getLogger();
    protected final List<IResourcePack> resourcePacks = Lists.newArrayList();
    private final IMetadataSerializer frmMetadataSerializer;

    public FallbackResourceManager(IMetadataSerializer frmMetadataSerializerIn)
    {
        this.frmMetadataSerializer = frmMetadataSerializerIn;
    }

    public void addResourcePack(IResourcePack resourcePack)
    {
        this.resourcePacks.add(resourcePack);
    }

    public Set<String> getResourceDomains()
    {
        return null;
    }

    public IResource getResource(ResourceLocation location) throws IOException
    {
        IResourcePack lvt_2_1_ = null;
        ResourceLocation lvt_3_1_ = getLocationMcmeta(location);

        for (int lvt_4_1_ = this.resourcePacks.size() - 1; lvt_4_1_ >= 0; --lvt_4_1_)
        {
            IResourcePack lvt_5_1_ = (IResourcePack)this.resourcePacks.get(lvt_4_1_);

            if (lvt_2_1_ == null && lvt_5_1_.resourceExists(lvt_3_1_))
            {
                lvt_2_1_ = lvt_5_1_;
            }

            if (lvt_5_1_.resourceExists(location))
            {
                InputStream lvt_6_1_ = null;

                if (lvt_2_1_ != null)
                {
                    lvt_6_1_ = this.getInputStream(lvt_3_1_, lvt_2_1_);
                }

                return new SimpleResource(lvt_5_1_.getPackName(), location, this.getInputStream(location, lvt_5_1_), lvt_6_1_, this.frmMetadataSerializer);
            }
        }

        throw new FileNotFoundException(location.toString());
    }

    protected InputStream getInputStream(ResourceLocation location, IResourcePack resourcePack) throws IOException
    {
        InputStream lvt_3_1_ = resourcePack.getInputStream(location);
        return (InputStream)(logger.isDebugEnabled() ? new FallbackResourceManager.InputStreamLeakedResourceLogger(lvt_3_1_, location, resourcePack.getPackName()) : lvt_3_1_);
    }

    public List<IResource> getAllResources(ResourceLocation location) throws IOException
    {
        List<IResource> lvt_2_1_ = Lists.newArrayList();
        ResourceLocation lvt_3_1_ = getLocationMcmeta(location);

        for (IResourcePack lvt_5_1_ : this.resourcePacks)
        {
            if (lvt_5_1_.resourceExists(location))
            {
                InputStream lvt_6_1_ = lvt_5_1_.resourceExists(lvt_3_1_) ? this.getInputStream(lvt_3_1_, lvt_5_1_) : null;
                lvt_2_1_.add(new SimpleResource(lvt_5_1_.getPackName(), location, this.getInputStream(location, lvt_5_1_), lvt_6_1_, this.frmMetadataSerializer));
            }
        }

        if (lvt_2_1_.isEmpty())
        {
            throw new FileNotFoundException(location.toString());
        }
        else
        {
            return lvt_2_1_;
        }
    }

    static ResourceLocation getLocationMcmeta(ResourceLocation location)
    {
        return new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + ".mcmeta");
    }

    static class InputStreamLeakedResourceLogger extends InputStream
    {
        private final InputStream inputStream;
        private final String message;
        private boolean isClosed = false;

        public InputStreamLeakedResourceLogger(InputStream p_i46093_1_, ResourceLocation location, String resourcePack)
        {
            this.inputStream = p_i46093_1_;
            ByteArrayOutputStream lvt_4_1_ = new ByteArrayOutputStream();
            (new Exception()).printStackTrace(new PrintStream(lvt_4_1_));
            this.message = "Leaked resource: \'" + location + "\' loaded from pack: \'" + resourcePack + "\'\n" + lvt_4_1_.toString();
        }

        public void close() throws IOException
        {
            this.inputStream.close();
            this.isClosed = true;
        }

        protected void finalize() throws Throwable
        {
            if (!this.isClosed)
            {
                FallbackResourceManager.logger.warn(this.message);
            }

            super.finalize();
        }

        public int read() throws IOException
        {
            return this.inputStream.read();
        }
    }
}
