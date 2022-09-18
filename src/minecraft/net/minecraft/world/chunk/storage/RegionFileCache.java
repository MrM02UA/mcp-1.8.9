package net.minecraft.world.chunk.storage;

import com.google.common.collect.Maps;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class RegionFileCache
{
    private static final Map<File, RegionFile> regionsByFilename = Maps.newHashMap();

    public static synchronized RegionFile createOrLoadRegionFile(File worldDir, int chunkX, int chunkZ)
    {
        File lvt_3_1_ = new File(worldDir, "region");
        File lvt_4_1_ = new File(lvt_3_1_, "r." + (chunkX >> 5) + "." + (chunkZ >> 5) + ".mca");
        RegionFile lvt_5_1_ = (RegionFile)regionsByFilename.get(lvt_4_1_);

        if (lvt_5_1_ != null)
        {
            return lvt_5_1_;
        }
        else
        {
            if (!lvt_3_1_.exists())
            {
                lvt_3_1_.mkdirs();
            }

            if (regionsByFilename.size() >= 256)
            {
                clearRegionFileReferences();
            }

            RegionFile lvt_6_1_ = new RegionFile(lvt_4_1_);
            regionsByFilename.put(lvt_4_1_, lvt_6_1_);
            return lvt_6_1_;
        }
    }

    /**
     * clears region file references
     */
    public static synchronized void clearRegionFileReferences()
    {
        for (RegionFile lvt_1_1_ : regionsByFilename.values())
        {
            try
            {
                if (lvt_1_1_ != null)
                {
                    lvt_1_1_.close();
                }
            }
            catch (IOException var3)
            {
                var3.printStackTrace();
            }
        }

        regionsByFilename.clear();
    }

    /**
     * Returns an input stream for the specified chunk. Args: worldDir, chunkX, chunkZ
     */
    public static DataInputStream getChunkInputStream(File worldDir, int chunkX, int chunkZ)
    {
        RegionFile lvt_3_1_ = createOrLoadRegionFile(worldDir, chunkX, chunkZ);
        return lvt_3_1_.getChunkDataInputStream(chunkX & 31, chunkZ & 31);
    }

    /**
     * Returns an output stream for the specified chunk. Args: worldDir, chunkX, chunkZ
     */
    public static DataOutputStream getChunkOutputStream(File worldDir, int chunkX, int chunkZ)
    {
        RegionFile lvt_3_1_ = createOrLoadRegionFile(worldDir, chunkX, chunkZ);
        return lvt_3_1_.getChunkDataOutputStream(chunkX & 31, chunkZ & 31);
    }
}
