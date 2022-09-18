package net.minecraft.client.resources;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileResourcePack extends AbstractResourcePack implements Closeable
{
    public static final Splitter entryNameSplitter = Splitter.on('/').omitEmptyStrings().limit(3);
    private ZipFile resourcePackZipFile;

    public FileResourcePack(File resourcePackFileIn)
    {
        super(resourcePackFileIn);
    }

    private ZipFile getResourcePackZipFile() throws IOException
    {
        if (this.resourcePackZipFile == null)
        {
            this.resourcePackZipFile = new ZipFile(this.resourcePackFile);
        }

        return this.resourcePackZipFile;
    }

    protected InputStream getInputStreamByName(String name) throws IOException
    {
        ZipFile lvt_2_1_ = this.getResourcePackZipFile();
        ZipEntry lvt_3_1_ = lvt_2_1_.getEntry(name);

        if (lvt_3_1_ == null)
        {
            throw new ResourcePackFileNotFoundException(this.resourcePackFile, name);
        }
        else
        {
            return lvt_2_1_.getInputStream(lvt_3_1_);
        }
    }

    public boolean hasResourceName(String name)
    {
        try
        {
            return this.getResourcePackZipFile().getEntry(name) != null;
        }
        catch (IOException var3)
        {
            return false;
        }
    }

    public Set<String> getResourceDomains()
    {
        ZipFile lvt_1_1_;

        try
        {
            lvt_1_1_ = this.getResourcePackZipFile();
        }
        catch (IOException var8)
        {
            return Collections.emptySet();
        }

        Enumeration <? extends ZipEntry > lvt_2_2_ = lvt_1_1_.entries();
        Set<String> lvt_3_1_ = Sets.newHashSet();

        while (lvt_2_2_.hasMoreElements())
        {
            ZipEntry lvt_4_1_ = (ZipEntry)lvt_2_2_.nextElement();
            String lvt_5_1_ = lvt_4_1_.getName();

            if (lvt_5_1_.startsWith("assets/"))
            {
                List<String> lvt_6_1_ = Lists.newArrayList(entryNameSplitter.split(lvt_5_1_));

                if (lvt_6_1_.size() > 1)
                {
                    String lvt_7_1_ = (String)lvt_6_1_.get(1);

                    if (!lvt_7_1_.equals(lvt_7_1_.toLowerCase()))
                    {
                        this.logNameNotLowercase(lvt_7_1_);
                    }
                    else
                    {
                        lvt_3_1_.add(lvt_7_1_);
                    }
                }
            }
        }

        return lvt_3_1_;
    }

    protected void finalize() throws Throwable
    {
        this.close();
        super.finalize();
    }

    public void close() throws IOException
    {
        if (this.resourcePackZipFile != null)
        {
            this.resourcePackZipFile.close();
            this.resourcePackZipFile = null;
        }
    }
}
