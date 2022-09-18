package net.minecraft.world.chunk.storage;

import com.google.common.collect.Lists;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveFormatComparator;
import net.minecraft.world.storage.SaveFormatOld;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilSaveConverter extends SaveFormatOld
{
    private static final Logger logger = LogManager.getLogger();

    public AnvilSaveConverter(File savesDirectoryIn)
    {
        super(savesDirectoryIn);
    }

    /**
     * Returns the name of the save format.
     */
    public String getName()
    {
        return "Anvil";
    }

    public List<SaveFormatComparator> getSaveList() throws AnvilConverterException
    {
        if (this.savesDirectory != null && this.savesDirectory.exists() && this.savesDirectory.isDirectory())
        {
            List<SaveFormatComparator> lvt_1_1_ = Lists.newArrayList();
            File[] lvt_2_1_ = this.savesDirectory.listFiles();

            for (File lvt_6_1_ : lvt_2_1_)
            {
                if (lvt_6_1_.isDirectory())
                {
                    String lvt_7_1_ = lvt_6_1_.getName();
                    WorldInfo lvt_8_1_ = this.getWorldInfo(lvt_7_1_);

                    if (lvt_8_1_ != null && (lvt_8_1_.getSaveVersion() == 19132 || lvt_8_1_.getSaveVersion() == 19133))
                    {
                        boolean lvt_9_1_ = lvt_8_1_.getSaveVersion() != this.getSaveVersion();
                        String lvt_10_1_ = lvt_8_1_.getWorldName();

                        if (StringUtils.isEmpty(lvt_10_1_))
                        {
                            lvt_10_1_ = lvt_7_1_;
                        }

                        long lvt_11_1_ = 0L;
                        lvt_1_1_.add(new SaveFormatComparator(lvt_7_1_, lvt_10_1_, lvt_8_1_.getLastTimePlayed(), lvt_11_1_, lvt_8_1_.getGameType(), lvt_9_1_, lvt_8_1_.isHardcoreModeEnabled(), lvt_8_1_.areCommandsAllowed()));
                    }
                }
            }

            return lvt_1_1_;
        }
        else
        {
            throw new AnvilConverterException("Unable to read or access folder where game worlds are saved!");
        }
    }

    protected int getSaveVersion()
    {
        return 19133;
    }

    public void flushCache()
    {
        RegionFileCache.clearRegionFileReferences();
    }

    /**
     * Returns back a loader for the specified save directory
     */
    public ISaveHandler getSaveLoader(String saveName, boolean storePlayerdata)
    {
        return new AnvilSaveHandler(this.savesDirectory, saveName, storePlayerdata);
    }

    public boolean isConvertible(String saveName)
    {
        WorldInfo lvt_2_1_ = this.getWorldInfo(saveName);
        return lvt_2_1_ != null && lvt_2_1_.getSaveVersion() == 19132;
    }

    /**
     * gets if the map is old chunk saving (true) or McRegion (false)
     */
    public boolean isOldMapFormat(String saveName)
    {
        WorldInfo lvt_2_1_ = this.getWorldInfo(saveName);
        return lvt_2_1_ != null && lvt_2_1_.getSaveVersion() != this.getSaveVersion();
    }

    /**
     * converts the map to mcRegion
     */
    public boolean convertMapFormat(String filename, IProgressUpdate progressCallback)
    {
        progressCallback.setLoadingProgress(0);
        List<File> lvt_3_1_ = Lists.newArrayList();
        List<File> lvt_4_1_ = Lists.newArrayList();
        List<File> lvt_5_1_ = Lists.newArrayList();
        File lvt_6_1_ = new File(this.savesDirectory, filename);
        File lvt_7_1_ = new File(lvt_6_1_, "DIM-1");
        File lvt_8_1_ = new File(lvt_6_1_, "DIM1");
        logger.info("Scanning folders...");
        this.addRegionFilesToCollection(lvt_6_1_, lvt_3_1_);

        if (lvt_7_1_.exists())
        {
            this.addRegionFilesToCollection(lvt_7_1_, lvt_4_1_);
        }

        if (lvt_8_1_.exists())
        {
            this.addRegionFilesToCollection(lvt_8_1_, lvt_5_1_);
        }

        int lvt_9_1_ = lvt_3_1_.size() + lvt_4_1_.size() + lvt_5_1_.size();
        logger.info("Total conversion count is " + lvt_9_1_);
        WorldInfo lvt_10_1_ = this.getWorldInfo(filename);
        WorldChunkManager lvt_11_1_ = null;

        if (lvt_10_1_.getTerrainType() == WorldType.FLAT)
        {
            lvt_11_1_ = new WorldChunkManagerHell(BiomeGenBase.plains, 0.5F);
        }
        else
        {
            lvt_11_1_ = new WorldChunkManager(lvt_10_1_.getSeed(), lvt_10_1_.getTerrainType(), lvt_10_1_.getGeneratorOptions());
        }

        this.convertFile(new File(lvt_6_1_, "region"), lvt_3_1_, lvt_11_1_, 0, lvt_9_1_, progressCallback);
        this.convertFile(new File(lvt_7_1_, "region"), lvt_4_1_, new WorldChunkManagerHell(BiomeGenBase.hell, 0.0F), lvt_3_1_.size(), lvt_9_1_, progressCallback);
        this.convertFile(new File(lvt_8_1_, "region"), lvt_5_1_, new WorldChunkManagerHell(BiomeGenBase.sky, 0.0F), lvt_3_1_.size() + lvt_4_1_.size(), lvt_9_1_, progressCallback);
        lvt_10_1_.setSaveVersion(19133);

        if (lvt_10_1_.getTerrainType() == WorldType.DEFAULT_1_1)
        {
            lvt_10_1_.setTerrainType(WorldType.DEFAULT);
        }

        this.createFile(filename);
        ISaveHandler lvt_12_1_ = this.getSaveLoader(filename, false);
        lvt_12_1_.saveWorldInfo(lvt_10_1_);
        return true;
    }

    /**
     * par: filename for the level.dat_mcr backup
     */
    private void createFile(String filename)
    {
        File lvt_2_1_ = new File(this.savesDirectory, filename);

        if (!lvt_2_1_.exists())
        {
            logger.warn("Unable to create level.dat_mcr backup");
        }
        else
        {
            File lvt_3_1_ = new File(lvt_2_1_, "level.dat");

            if (!lvt_3_1_.exists())
            {
                logger.warn("Unable to create level.dat_mcr backup");
            }
            else
            {
                File lvt_4_1_ = new File(lvt_2_1_, "level.dat_mcr");

                if (!lvt_3_1_.renameTo(lvt_4_1_))
                {
                    logger.warn("Unable to create level.dat_mcr backup");
                }
            }
        }
    }

    private void convertFile(File p_75813_1_, Iterable<File> p_75813_2_, WorldChunkManager p_75813_3_, int p_75813_4_, int p_75813_5_, IProgressUpdate p_75813_6_)
    {
        for (File lvt_8_1_ : p_75813_2_)
        {
            this.convertChunks(p_75813_1_, lvt_8_1_, p_75813_3_, p_75813_4_, p_75813_5_, p_75813_6_);
            ++p_75813_4_;
            int lvt_9_1_ = (int)Math.round(100.0D * (double)p_75813_4_ / (double)p_75813_5_);
            p_75813_6_.setLoadingProgress(lvt_9_1_);
        }
    }

    /**
     * copies a 32x32 chunk set from par2File to par1File, via AnvilConverterData
     */
    private void convertChunks(File p_75811_1_, File p_75811_2_, WorldChunkManager p_75811_3_, int p_75811_4_, int p_75811_5_, IProgressUpdate progressCallback)
    {
        try
        {
            String lvt_7_1_ = p_75811_2_.getName();
            RegionFile lvt_8_1_ = new RegionFile(p_75811_2_);
            RegionFile lvt_9_1_ = new RegionFile(new File(p_75811_1_, lvt_7_1_.substring(0, lvt_7_1_.length() - ".mcr".length()) + ".mca"));

            for (int lvt_10_1_ = 0; lvt_10_1_ < 32; ++lvt_10_1_)
            {
                for (int lvt_11_1_ = 0; lvt_11_1_ < 32; ++lvt_11_1_)
                {
                    if (lvt_8_1_.isChunkSaved(lvt_10_1_, lvt_11_1_) && !lvt_9_1_.isChunkSaved(lvt_10_1_, lvt_11_1_))
                    {
                        DataInputStream lvt_12_1_ = lvt_8_1_.getChunkDataInputStream(lvt_10_1_, lvt_11_1_);

                        if (lvt_12_1_ == null)
                        {
                            logger.warn("Failed to fetch input stream");
                        }
                        else
                        {
                            NBTTagCompound lvt_13_1_ = CompressedStreamTools.read(lvt_12_1_);
                            lvt_12_1_.close();
                            NBTTagCompound lvt_14_1_ = lvt_13_1_.getCompoundTag("Level");
                            ChunkLoader.AnvilConverterData lvt_15_1_ = ChunkLoader.load(lvt_14_1_);
                            NBTTagCompound lvt_16_1_ = new NBTTagCompound();
                            NBTTagCompound lvt_17_1_ = new NBTTagCompound();
                            lvt_16_1_.setTag("Level", lvt_17_1_);
                            ChunkLoader.convertToAnvilFormat(lvt_15_1_, lvt_17_1_, p_75811_3_);
                            DataOutputStream lvt_18_1_ = lvt_9_1_.getChunkDataOutputStream(lvt_10_1_, lvt_11_1_);
                            CompressedStreamTools.write(lvt_16_1_, lvt_18_1_);
                            lvt_18_1_.close();
                        }
                    }
                }

                int lvt_11_2_ = (int)Math.round(100.0D * (double)(p_75811_4_ * 1024) / (double)(p_75811_5_ * 1024));
                int lvt_12_2_ = (int)Math.round(100.0D * (double)((lvt_10_1_ + 1) * 32 + p_75811_4_ * 1024) / (double)(p_75811_5_ * 1024));

                if (lvt_12_2_ > lvt_11_2_)
                {
                    progressCallback.setLoadingProgress(lvt_12_2_);
                }
            }

            lvt_8_1_.close();
            lvt_9_1_.close();
        }
        catch (IOException var19)
        {
            var19.printStackTrace();
        }
    }

    /**
     * filters the files in the par1 directory, and adds them to the par2 collections
     */
    private void addRegionFilesToCollection(File worldDir, Collection<File> collection)
    {
        File lvt_3_1_ = new File(worldDir, "region");
        File[] lvt_4_1_ = lvt_3_1_.listFiles(new FilenameFilter()
        {
            public boolean accept(File p_accept_1_, String p_accept_2_)
            {
                return p_accept_2_.endsWith(".mcr");
            }
        });

        if (lvt_4_1_ != null)
        {
            Collections.addAll(collection, lvt_4_1_);
        }
    }
}
