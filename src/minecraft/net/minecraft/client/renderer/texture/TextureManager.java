package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureManager implements ITickable, IResourceManagerReloadListener
{
    private static final Logger logger = LogManager.getLogger();
    private final Map<ResourceLocation, ITextureObject> mapTextureObjects = Maps.newHashMap();
    private final List<ITickable> listTickables = Lists.newArrayList();
    private final Map<String, Integer> mapTextureCounters = Maps.newHashMap();
    private IResourceManager theResourceManager;

    public TextureManager(IResourceManager resourceManager)
    {
        this.theResourceManager = resourceManager;
    }

    public void bindTexture(ResourceLocation resource)
    {
        ITextureObject lvt_2_1_ = (ITextureObject)this.mapTextureObjects.get(resource);

        if (lvt_2_1_ == null)
        {
            lvt_2_1_ = new SimpleTexture(resource);
            this.loadTexture(resource, lvt_2_1_);
        }

        TextureUtil.bindTexture(lvt_2_1_.getGlTextureId());
    }

    public boolean loadTickableTexture(ResourceLocation textureLocation, ITickableTextureObject textureObj)
    {
        if (this.loadTexture(textureLocation, textureObj))
        {
            this.listTickables.add(textureObj);
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean loadTexture(ResourceLocation textureLocation, final ITextureObject textureObj)
    {
        boolean lvt_3_1_ = true;

        try
        {
            ((ITextureObject)textureObj).loadTexture(this.theResourceManager);
        }
        catch (IOException var8)
        {
            logger.warn("Failed to load texture: " + textureLocation, var8);
            textureObj = TextureUtil.missingTexture;
            this.mapTextureObjects.put(textureLocation, textureObj);
            lvt_3_1_ = false;
        }
        catch (Throwable var9)
        {
            CrashReport lvt_5_1_ = CrashReport.makeCrashReport(var9, "Registering texture");
            CrashReportCategory lvt_6_1_ = lvt_5_1_.makeCategory("Resource location being registered");
            lvt_6_1_.addCrashSection("Resource location", textureLocation);
            lvt_6_1_.addCrashSectionCallable("Texture object class", new Callable<String>()
            {
                public String call() throws Exception
                {
                    return textureObj.getClass().getName();
                }
                public Object call() throws Exception
                {
                    return this.call();
                }
            });
            throw new ReportedException(lvt_5_1_);
        }

        this.mapTextureObjects.put(textureLocation, textureObj);
        return lvt_3_1_;
    }

    public ITextureObject getTexture(ResourceLocation textureLocation)
    {
        return (ITextureObject)this.mapTextureObjects.get(textureLocation);
    }

    public ResourceLocation getDynamicTextureLocation(String name, DynamicTexture texture)
    {
        Integer lvt_3_1_ = (Integer)this.mapTextureCounters.get(name);

        if (lvt_3_1_ == null)
        {
            lvt_3_1_ = Integer.valueOf(1);
        }
        else
        {
            lvt_3_1_ = Integer.valueOf(lvt_3_1_.intValue() + 1);
        }

        this.mapTextureCounters.put(name, lvt_3_1_);
        ResourceLocation lvt_4_1_ = new ResourceLocation(String.format("dynamic/%s_%d", new Object[] {name, lvt_3_1_}));
        this.loadTexture(lvt_4_1_, texture);
        return lvt_4_1_;
    }

    public void tick()
    {
        for (ITickable lvt_2_1_ : this.listTickables)
        {
            lvt_2_1_.tick();
        }
    }

    public void deleteTexture(ResourceLocation textureLocation)
    {
        ITextureObject lvt_2_1_ = this.getTexture(textureLocation);

        if (lvt_2_1_ != null)
        {
            TextureUtil.deleteTexture(lvt_2_1_.getGlTextureId());
        }
    }

    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        for (Entry<ResourceLocation, ITextureObject> lvt_3_1_ : this.mapTextureObjects.entrySet())
        {
            this.loadTexture((ResourceLocation)lvt_3_1_.getKey(), (ITextureObject)lvt_3_1_.getValue());
        }
    }
}
