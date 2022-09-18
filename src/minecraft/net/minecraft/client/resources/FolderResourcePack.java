package net.minecraft.client.resources;

import com.google.common.collect.Sets;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import org.apache.commons.io.filefilter.DirectoryFileFilter;

public class FolderResourcePack extends AbstractResourcePack
{
    public FolderResourcePack(File resourcePackFileIn)
    {
        super(resourcePackFileIn);
    }

    protected InputStream getInputStreamByName(String name) throws IOException
    {
        return new BufferedInputStream(new FileInputStream(new File(this.resourcePackFile, name)));
    }

    protected boolean hasResourceName(String name)
    {
        return (new File(this.resourcePackFile, name)).isFile();
    }

    public Set<String> getResourceDomains()
    {
        Set<String> lvt_1_1_ = Sets.newHashSet();
        File lvt_2_1_ = new File(this.resourcePackFile, "assets/");

        if (lvt_2_1_.isDirectory())
        {
            for (File lvt_6_1_ : lvt_2_1_.listFiles(DirectoryFileFilter.DIRECTORY))
            {
                String lvt_7_1_ = getRelativeName(lvt_2_1_, lvt_6_1_);

                if (!lvt_7_1_.equals(lvt_7_1_.toLowerCase()))
                {
                    this.logNameNotLowercase(lvt_7_1_);
                }
                else
                {
                    lvt_1_1_.add(lvt_7_1_.substring(0, lvt_7_1_.length() - 1));
                }
            }
        }

        return lvt_1_1_;
    }
}
