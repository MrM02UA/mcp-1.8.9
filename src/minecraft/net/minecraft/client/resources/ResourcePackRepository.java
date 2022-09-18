package net.minecraft.client.resources;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackRepository
{
    private static final Logger logger = LogManager.getLogger();
    private static final FileFilter resourcePackFilter = new FileFilter()
    {
        public boolean accept(File p_accept_1_)
        {
            boolean lvt_2_1_ = p_accept_1_.isFile() && p_accept_1_.getName().endsWith(".zip");
            boolean lvt_3_1_ = p_accept_1_.isDirectory() && (new File(p_accept_1_, "pack.mcmeta")).isFile();
            return lvt_2_1_ || lvt_3_1_;
        }
    };
    private final File dirResourcepacks;
    public final IResourcePack rprDefaultResourcePack;
    private final File dirServerResourcepacks;
    public final IMetadataSerializer rprMetadataSerializer;
    private IResourcePack resourcePackInstance;
    private final ReentrantLock lock = new ReentrantLock();
    private ListenableFuture<Object> downloadingPacks;
    private List<ResourcePackRepository.Entry> repositoryEntriesAll = Lists.newArrayList();
    private List<ResourcePackRepository.Entry> repositoryEntries = Lists.newArrayList();

    public ResourcePackRepository(File dirResourcepacksIn, File dirServerResourcepacksIn, IResourcePack rprDefaultResourcePackIn, IMetadataSerializer rprMetadataSerializerIn, GameSettings settings)
    {
        this.dirResourcepacks = dirResourcepacksIn;
        this.dirServerResourcepacks = dirServerResourcepacksIn;
        this.rprDefaultResourcePack = rprDefaultResourcePackIn;
        this.rprMetadataSerializer = rprMetadataSerializerIn;
        this.fixDirResourcepacks();
        this.updateRepositoryEntriesAll();
        Iterator<String> lvt_6_1_ = settings.resourcePacks.iterator();

        while (lvt_6_1_.hasNext())
        {
            String lvt_7_1_ = (String)lvt_6_1_.next();

            for (ResourcePackRepository.Entry lvt_9_1_ : this.repositoryEntriesAll)
            {
                if (lvt_9_1_.getResourcePackName().equals(lvt_7_1_))
                {
                    if (lvt_9_1_.func_183027_f() == 1 || settings.incompatibleResourcePacks.contains(lvt_9_1_.getResourcePackName()))
                    {
                        this.repositoryEntries.add(lvt_9_1_);
                        break;
                    }

                    lvt_6_1_.remove();
                    logger.warn("Removed selected resource pack {} because it\'s no longer compatible", new Object[] {lvt_9_1_.getResourcePackName()});
                }
            }
        }
    }

    private void fixDirResourcepacks()
    {
        if (this.dirResourcepacks.exists())
        {
            if (!this.dirResourcepacks.isDirectory() && (!this.dirResourcepacks.delete() || !this.dirResourcepacks.mkdirs()))
            {
                logger.warn("Unable to recreate resourcepack folder, it exists but is not a directory: " + this.dirResourcepacks);
            }
        }
        else if (!this.dirResourcepacks.mkdirs())
        {
            logger.warn("Unable to create resourcepack folder: " + this.dirResourcepacks);
        }
    }

    private List<File> getResourcePackFiles()
    {
        return this.dirResourcepacks.isDirectory() ? Arrays.asList(this.dirResourcepacks.listFiles(resourcePackFilter)) : Collections.<File>emptyList();
    }

    public void updateRepositoryEntriesAll()
    {
        List<ResourcePackRepository.Entry> lvt_1_1_ = Lists.newArrayList();

        for (File lvt_3_1_ : this.getResourcePackFiles())
        {
            ResourcePackRepository.Entry lvt_4_1_ = new ResourcePackRepository.Entry(lvt_3_1_);

            if (!this.repositoryEntriesAll.contains(lvt_4_1_))
            {
                try
                {
                    lvt_4_1_.updateResourcePack();
                    lvt_1_1_.add(lvt_4_1_);
                }
                catch (Exception var6)
                {
                    lvt_1_1_.remove(lvt_4_1_);
                }
            }
            else
            {
                int lvt_5_2_ = this.repositoryEntriesAll.indexOf(lvt_4_1_);

                if (lvt_5_2_ > -1 && lvt_5_2_ < this.repositoryEntriesAll.size())
                {
                    lvt_1_1_.add(this.repositoryEntriesAll.get(lvt_5_2_));
                }
            }
        }

        this.repositoryEntriesAll.removeAll(lvt_1_1_);

        for (ResourcePackRepository.Entry lvt_3_2_ : this.repositoryEntriesAll)
        {
            lvt_3_2_.closeResourcePack();
        }

        this.repositoryEntriesAll = lvt_1_1_;
    }

    public List<ResourcePackRepository.Entry> getRepositoryEntriesAll()
    {
        return ImmutableList.copyOf(this.repositoryEntriesAll);
    }

    public List<ResourcePackRepository.Entry> getRepositoryEntries()
    {
        return ImmutableList.copyOf(this.repositoryEntries);
    }

    public void setRepositories(List<ResourcePackRepository.Entry> repositories)
    {
        this.repositoryEntries.clear();
        this.repositoryEntries.addAll(repositories);
    }

    public File getDirResourcepacks()
    {
        return this.dirResourcepacks;
    }

    public ListenableFuture<Object> downloadResourcePack(String url, String hash)
    {
        String lvt_3_1_;

        if (hash.matches("^[a-f0-9]{40}$"))
        {
            lvt_3_1_ = hash;
        }
        else
        {
            lvt_3_1_ = "legacy";
        }

        final File lvt_4_1_ = new File(this.dirServerResourcepacks, lvt_3_1_);
        this.lock.lock();

        try
        {
            this.clearResourcePack();

            if (lvt_4_1_.exists() && hash.length() == 40)
            {
                try
                {
                    String lvt_5_1_ = Hashing.sha1().hashBytes(Files.toByteArray(lvt_4_1_)).toString();

                    if (lvt_5_1_.equals(hash))
                    {
                        ListenableFuture var16 = this.setResourcePackInstance(lvt_4_1_);
                        return var16;
                    }

                    logger.warn("File " + lvt_4_1_ + " had wrong hash (expected " + hash + ", found " + lvt_5_1_ + "). Deleting it.");
                    FileUtils.deleteQuietly(lvt_4_1_);
                }
                catch (IOException var13)
                {
                    logger.warn("File " + lvt_4_1_ + " couldn\'t be hashed. Deleting it.", var13);
                    FileUtils.deleteQuietly(lvt_4_1_);
                }
            }

            this.deleteOldServerResourcesPacks();
            final GuiScreenWorking lvt_5_3_ = new GuiScreenWorking();
            Map<String, String> lvt_6_1_ = Minecraft.getSessionInfo();
            final Minecraft lvt_7_1_ = Minecraft.getMinecraft();
            Futures.getUnchecked(lvt_7_1_.addScheduledTask(new Runnable()
            {
                public void run()
                {
                    lvt_7_1_.displayGuiScreen(lvt_5_3_);
                }
            }));
            final SettableFuture<Object> lvt_8_1_ = SettableFuture.create();
            this.downloadingPacks = HttpUtil.downloadResourcePack(lvt_4_1_, url, lvt_6_1_, 52428800, lvt_5_3_, lvt_7_1_.getProxy());
            Futures.addCallback(this.downloadingPacks, new FutureCallback<Object>()
            {
                public void onSuccess(Object p_onSuccess_1_)
                {
                    ResourcePackRepository.this.setResourcePackInstance(lvt_4_1_);
                    lvt_8_1_.set((Object)null);
                }
                public void onFailure(Throwable p_onFailure_1_)
                {
                    lvt_8_1_.setException(p_onFailure_1_);
                }
            });
            ListenableFuture var9 = this.downloadingPacks;
            return var9;
        }
        finally
        {
            this.lock.unlock();
        }
    }

    /**
     * Keep only the 10 most recent resources packs, delete the others
     */
    private void deleteOldServerResourcesPacks()
    {
        List<File> lvt_1_1_ = Lists.newArrayList(FileUtils.listFiles(this.dirServerResourcepacks, TrueFileFilter.TRUE, (IOFileFilter)null));
        Collections.sort(lvt_1_1_, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
        int lvt_2_1_ = 0;

        for (File lvt_4_1_ : lvt_1_1_)
        {
            if (lvt_2_1_++ >= 10)
            {
                logger.info("Deleting old server resource pack " + lvt_4_1_.getName());
                FileUtils.deleteQuietly(lvt_4_1_);
            }
        }
    }

    public ListenableFuture<Object> setResourcePackInstance(File resourceFile)
    {
        this.resourcePackInstance = new FileResourcePack(resourceFile);
        return Minecraft.getMinecraft().scheduleResourcesRefresh();
    }

    /**
     * Getter for the IResourcePack instance associated with this ResourcePackRepository
     */
    public IResourcePack getResourcePackInstance()
    {
        return this.resourcePackInstance;
    }

    public void clearResourcePack()
    {
        this.lock.lock();

        try
        {
            if (this.downloadingPacks != null)
            {
                this.downloadingPacks.cancel(true);
            }

            this.downloadingPacks = null;

            if (this.resourcePackInstance != null)
            {
                this.resourcePackInstance = null;
                Minecraft.getMinecraft().scheduleResourcesRefresh();
            }
        }
        finally
        {
            this.lock.unlock();
        }
    }

    public class Entry
    {
        private final File resourcePackFile;
        private IResourcePack reResourcePack;
        private PackMetadataSection rePackMetadataSection;
        private BufferedImage texturePackIcon;
        private ResourceLocation locationTexturePackIcon;

        private Entry(File resourcePackFileIn)
        {
            this.resourcePackFile = resourcePackFileIn;
        }

        public void updateResourcePack() throws IOException
        {
            this.reResourcePack = (IResourcePack)(this.resourcePackFile.isDirectory() ? new FolderResourcePack(this.resourcePackFile) : new FileResourcePack(this.resourcePackFile));
            this.rePackMetadataSection = (PackMetadataSection)this.reResourcePack.getPackMetadata(ResourcePackRepository.this.rprMetadataSerializer, "pack");

            try
            {
                this.texturePackIcon = this.reResourcePack.getPackImage();
            }
            catch (IOException var2)
            {
                ;
            }

            if (this.texturePackIcon == null)
            {
                this.texturePackIcon = ResourcePackRepository.this.rprDefaultResourcePack.getPackImage();
            }

            this.closeResourcePack();
        }

        public void bindTexturePackIcon(TextureManager textureManagerIn)
        {
            if (this.locationTexturePackIcon == null)
            {
                this.locationTexturePackIcon = textureManagerIn.getDynamicTextureLocation("texturepackicon", new DynamicTexture(this.texturePackIcon));
            }

            textureManagerIn.bindTexture(this.locationTexturePackIcon);
        }

        public void closeResourcePack()
        {
            if (this.reResourcePack instanceof Closeable)
            {
                IOUtils.closeQuietly((Closeable)this.reResourcePack);
            }
        }

        public IResourcePack getResourcePack()
        {
            return this.reResourcePack;
        }

        public String getResourcePackName()
        {
            return this.reResourcePack.getPackName();
        }

        public String getTexturePackDescription()
        {
            return this.rePackMetadataSection == null ? EnumChatFormatting.RED + "Invalid pack.mcmeta (or missing \'pack\' section)" : this.rePackMetadataSection.getPackDescription().getFormattedText();
        }

        public int func_183027_f()
        {
            return this.rePackMetadataSection.getPackFormat();
        }

        public boolean equals(Object p_equals_1_)
        {
            return this == p_equals_1_ ? true : (p_equals_1_ instanceof ResourcePackRepository.Entry ? this.toString().equals(p_equals_1_.toString()) : false);
        }

        public int hashCode()
        {
            return this.toString().hashCode();
        }

        public String toString()
        {
            return String.format("%s:%s:%d", new Object[] {this.resourcePackFile.getName(), this.resourcePackFile.isDirectory() ? "folder" : "zip", Long.valueOf(this.resourcePackFile.lastModified())});
        }
    }
}
