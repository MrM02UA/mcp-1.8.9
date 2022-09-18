package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IProgressUpdate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveFormatOld implements ISaveFormat
{
    private static final Logger logger = LogManager.getLogger();

    /**
     * Reference to the File object representing the directory for the world saves
     */
    protected final File savesDirectory;

    public SaveFormatOld(File savesDirectoryIn)
    {
        if (!savesDirectoryIn.exists())
        {
            savesDirectoryIn.mkdirs();
        }

        this.savesDirectory = savesDirectoryIn;
    }

    /**
     * Returns the name of the save format.
     */
    public String getName()
    {
        return "Old Format";
    }

    public List<SaveFormatComparator> getSaveList() throws AnvilConverterException
    {
        List<SaveFormatComparator> lvt_1_1_ = Lists.newArrayList();

        for (int lvt_2_1_ = 0; lvt_2_1_ < 5; ++lvt_2_1_)
        {
            String lvt_3_1_ = "World" + (lvt_2_1_ + 1);
            WorldInfo lvt_4_1_ = this.getWorldInfo(lvt_3_1_);

            if (lvt_4_1_ != null)
            {
                lvt_1_1_.add(new SaveFormatComparator(lvt_3_1_, "", lvt_4_1_.getLastTimePlayed(), lvt_4_1_.getSizeOnDisk(), lvt_4_1_.getGameType(), false, lvt_4_1_.isHardcoreModeEnabled(), lvt_4_1_.areCommandsAllowed()));
            }
        }

        return lvt_1_1_;
    }

    public void flushCache()
    {
    }

    /**
     * Returns the world's WorldInfo object
     */
    public WorldInfo getWorldInfo(String saveName)
    {
        File lvt_2_1_ = new File(this.savesDirectory, saveName);

        if (!lvt_2_1_.exists())
        {
            return null;
        }
        else
        {
            File lvt_3_1_ = new File(lvt_2_1_, "level.dat");

            if (lvt_3_1_.exists())
            {
                try
                {
                    NBTTagCompound lvt_4_1_ = CompressedStreamTools.readCompressed(new FileInputStream(lvt_3_1_));
                    NBTTagCompound lvt_5_1_ = lvt_4_1_.getCompoundTag("Data");
                    return new WorldInfo(lvt_5_1_);
                }
                catch (Exception var7)
                {
                    logger.error("Exception reading " + lvt_3_1_, var7);
                }
            }

            lvt_3_1_ = new File(lvt_2_1_, "level.dat_old");

            if (lvt_3_1_.exists())
            {
                try
                {
                    NBTTagCompound lvt_4_3_ = CompressedStreamTools.readCompressed(new FileInputStream(lvt_3_1_));
                    NBTTagCompound lvt_5_2_ = lvt_4_3_.getCompoundTag("Data");
                    return new WorldInfo(lvt_5_2_);
                }
                catch (Exception var6)
                {
                    logger.error("Exception reading " + lvt_3_1_, var6);
                }
            }

            return null;
        }
    }

    /**
     * Renames the world by storing the new name in level.dat. It does *not* rename the directory containing the world
     * data.
     */
    public void renameWorld(String dirName, String newName)
    {
        File lvt_3_1_ = new File(this.savesDirectory, dirName);

        if (lvt_3_1_.exists())
        {
            File lvt_4_1_ = new File(lvt_3_1_, "level.dat");

            if (lvt_4_1_.exists())
            {
                try
                {
                    NBTTagCompound lvt_5_1_ = CompressedStreamTools.readCompressed(new FileInputStream(lvt_4_1_));
                    NBTTagCompound lvt_6_1_ = lvt_5_1_.getCompoundTag("Data");
                    lvt_6_1_.setString("LevelName", newName);
                    CompressedStreamTools.writeCompressed(lvt_5_1_, new FileOutputStream(lvt_4_1_));
                }
                catch (Exception var7)
                {
                    var7.printStackTrace();
                }
            }
        }
    }

    public boolean isNewLevelIdAcceptable(String saveName)
    {
        File lvt_2_1_ = new File(this.savesDirectory, saveName);

        if (lvt_2_1_.exists())
        {
            return false;
        }
        else
        {
            try
            {
                lvt_2_1_.mkdir();
                lvt_2_1_.delete();
                return true;
            }
            catch (Throwable var4)
            {
                logger.warn("Couldn\'t make new level", var4);
                return false;
            }
        }
    }

    /**
     * @args: Takes one argument - the name of the directory of the world to delete. @desc: Delete the world by deleting
     * the associated directory recursively.
     *  
     * @param saveName The current save's name
     */
    public boolean deleteWorldDirectory(String saveName)
    {
        File lvt_2_1_ = new File(this.savesDirectory, saveName);

        if (!lvt_2_1_.exists())
        {
            return true;
        }
        else
        {
            logger.info("Deleting level " + saveName);

            for (int lvt_3_1_ = 1; lvt_3_1_ <= 5; ++lvt_3_1_)
            {
                logger.info("Attempt " + lvt_3_1_ + "...");

                if (deleteFiles(lvt_2_1_.listFiles()))
                {
                    break;
                }

                logger.warn("Unsuccessful in deleting contents.");

                if (lvt_3_1_ < 5)
                {
                    try
                    {
                        Thread.sleep(500L);
                    }
                    catch (InterruptedException var5)
                    {
                        ;
                    }
                }
            }

            return lvt_2_1_.delete();
        }
    }

    /**
     * @args: Takes one argument - the list of files and directories to delete. @desc: Deletes the files and directory
     * listed in the list recursively.
     */
    protected static boolean deleteFiles(File[] files)
    {
        for (int lvt_1_1_ = 0; lvt_1_1_ < files.length; ++lvt_1_1_)
        {
            File lvt_2_1_ = files[lvt_1_1_];
            logger.debug("Deleting " + lvt_2_1_);

            if (lvt_2_1_.isDirectory() && !deleteFiles(lvt_2_1_.listFiles()))
            {
                logger.warn("Couldn\'t delete directory " + lvt_2_1_);
                return false;
            }

            if (!lvt_2_1_.delete())
            {
                logger.warn("Couldn\'t delete file " + lvt_2_1_);
                return false;
            }
        }

        return true;
    }

    /**
     * Returns back a loader for the specified save directory
     */
    public ISaveHandler getSaveLoader(String saveName, boolean storePlayerdata)
    {
        return new SaveHandler(this.savesDirectory, saveName, storePlayerdata);
    }

    public boolean isConvertible(String saveName)
    {
        return false;
    }

    /**
     * gets if the map is old chunk saving (true) or McRegion (false)
     */
    public boolean isOldMapFormat(String saveName)
    {
        return false;
    }

    /**
     * converts the map to mcRegion
     */
    public boolean convertMapFormat(String filename, IProgressUpdate progressCallback)
    {
        return false;
    }

    /**
     * Return whether the given world can be loaded.
     *  
     * @param saveName The current save's name
     */
    public boolean canLoadWorld(String saveName)
    {
        File lvt_2_1_ = new File(this.savesDirectory, saveName);
        return lvt_2_1_.isDirectory();
    }
}
