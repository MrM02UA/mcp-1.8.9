package net.minecraft.client.resources;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleReloadableResourceManager implements IReloadableResourceManager
{
    private static final Logger logger = LogManager.getLogger();
    private static final Joiner joinerResourcePacks = Joiner.on(", ");
    private final Map<String, FallbackResourceManager> domainResourceManagers = Maps.newHashMap();
    private final List<IResourceManagerReloadListener> reloadListeners = Lists.newArrayList();
    private final Set<String> setResourceDomains = Sets.newLinkedHashSet();
    private final IMetadataSerializer rmMetadataSerializer;

    public SimpleReloadableResourceManager(IMetadataSerializer rmMetadataSerializerIn)
    {
        this.rmMetadataSerializer = rmMetadataSerializerIn;
    }

    public void reloadResourcePack(IResourcePack resourcePack)
    {
        for (String lvt_3_1_ : resourcePack.getResourceDomains())
        {
            this.setResourceDomains.add(lvt_3_1_);
            FallbackResourceManager lvt_4_1_ = (FallbackResourceManager)this.domainResourceManagers.get(lvt_3_1_);

            if (lvt_4_1_ == null)
            {
                lvt_4_1_ = new FallbackResourceManager(this.rmMetadataSerializer);
                this.domainResourceManagers.put(lvt_3_1_, lvt_4_1_);
            }

            lvt_4_1_.addResourcePack(resourcePack);
        }
    }

    public Set<String> getResourceDomains()
    {
        return this.setResourceDomains;
    }

    public IResource getResource(ResourceLocation location) throws IOException
    {
        IResourceManager lvt_2_1_ = (IResourceManager)this.domainResourceManagers.get(location.getResourceDomain());

        if (lvt_2_1_ != null)
        {
            return lvt_2_1_.getResource(location);
        }
        else
        {
            throw new FileNotFoundException(location.toString());
        }
    }

    public List<IResource> getAllResources(ResourceLocation location) throws IOException
    {
        IResourceManager lvt_2_1_ = (IResourceManager)this.domainResourceManagers.get(location.getResourceDomain());

        if (lvt_2_1_ != null)
        {
            return lvt_2_1_.getAllResources(location);
        }
        else
        {
            throw new FileNotFoundException(location.toString());
        }
    }

    private void clearResources()
    {
        this.domainResourceManagers.clear();
        this.setResourceDomains.clear();
    }

    public void reloadResources(List<IResourcePack> resourcesPacksList)
    {
        this.clearResources();
        logger.info("Reloading ResourceManager: " + joinerResourcePacks.join(Iterables.transform(resourcesPacksList, new Function<IResourcePack, String>()
        {
            public String apply(IResourcePack p_apply_1_)
            {
                return p_apply_1_.getPackName();
            }
            public Object apply(Object p_apply_1_)
            {
                return this.apply((IResourcePack)p_apply_1_);
            }
        })));

        for (IResourcePack lvt_3_1_ : resourcesPacksList)
        {
            this.reloadResourcePack(lvt_3_1_);
        }

        this.notifyReloadListeners();
    }

    public void registerReloadListener(IResourceManagerReloadListener reloadListener)
    {
        this.reloadListeners.add(reloadListener);
        reloadListener.onResourceManagerReload(this);
    }

    private void notifyReloadListeners()
    {
        for (IResourceManagerReloadListener lvt_2_1_ : this.reloadListeners)
        {
            lvt_2_1_.onResourceManagerReload(this);
        }
    }
}
