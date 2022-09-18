package net.minecraft.world.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveHandler implements ISaveHandler, IPlayerFileData
{
    private static final Logger logger = LogManager.getLogger();

    /** The directory in which to save world data. */
    private final File worldDirectory;

    /** The directory in which to save player data. */
    private final File playersDirectory;
    private final File mapDataDir;

    /**
     * The time in milliseconds when this field was initialized. Stored in the session lock file.
     */
    private final long initializationTime = MinecraftServer.getCurrentTimeMillis();

    /** The directory name of the world */
    private final String saveDirectoryName;

    public SaveHandler(File savesDirectory, String directoryName, boolean playersDirectoryIn)
    {
        this.worldDirectory = new File(savesDirectory, directoryName);
        this.worldDirectory.mkdirs();
        this.playersDirectory = new File(this.worldDirectory, "playerdata");
        this.mapDataDir = new File(this.worldDirectory, "data");
        this.mapDataDir.mkdirs();
        this.saveDirectoryName = directoryName;

        if (playersDirectoryIn)
        {
            this.playersDirectory.mkdirs();
        }

        this.setSessionLock();
    }

    /**
     * Creates a session lock file for this process
     */
    private void setSessionLock()
    {
        try
        {
            File lvt_1_1_ = new File(this.worldDirectory, "session.lock");
            DataOutputStream lvt_2_1_ = new DataOutputStream(new FileOutputStream(lvt_1_1_));

            try
            {
                lvt_2_1_.writeLong(this.initializationTime);
            }
            finally
            {
                lvt_2_1_.close();
            }
        }
        catch (IOException var7)
        {
            var7.printStackTrace();
            throw new RuntimeException("Failed to check session lock, aborting");
        }
    }

    /**
     * Gets the File object corresponding to the base directory of this world.
     */
    public File getWorldDirectory()
    {
        return this.worldDirectory;
    }

    /**
     * Checks the session lock to prevent save collisions
     */
    public void checkSessionLock() throws MinecraftException
    {
        try
        {
            File lvt_1_1_ = new File(this.worldDirectory, "session.lock");
            DataInputStream lvt_2_1_ = new DataInputStream(new FileInputStream(lvt_1_1_));

            try
            {
                if (lvt_2_1_.readLong() != this.initializationTime)
                {
                    throw new MinecraftException("The save is being accessed from another location, aborting");
                }
            }
            finally
            {
                lvt_2_1_.close();
            }
        }
        catch (IOException var7)
        {
            throw new MinecraftException("Failed to check session lock, aborting");
        }
    }

    /**
     * initializes and returns the chunk loader for the specified world provider
     */
    public IChunkLoader getChunkLoader(WorldProvider provider)
    {
        throw new RuntimeException("Old Chunk Storage is no longer supported.");
    }

    /**
     * Loads and returns the world info
     */
    public WorldInfo loadWorldInfo()
    {
        File lvt_1_1_ = new File(this.worldDirectory, "level.dat");

        if (lvt_1_1_.exists())
        {
            try
            {
                NBTTagCompound lvt_2_1_ = CompressedStreamTools.readCompressed(new FileInputStream(lvt_1_1_));
                NBTTagCompound lvt_3_1_ = lvt_2_1_.getCompoundTag("Data");
                return new WorldInfo(lvt_3_1_);
            }
            catch (Exception var5)
            {
                var5.printStackTrace();
            }
        }

        lvt_1_1_ = new File(this.worldDirectory, "level.dat_old");

        if (lvt_1_1_.exists())
        {
            try
            {
                NBTTagCompound lvt_2_3_ = CompressedStreamTools.readCompressed(new FileInputStream(lvt_1_1_));
                NBTTagCompound lvt_3_2_ = lvt_2_3_.getCompoundTag("Data");
                return new WorldInfo(lvt_3_2_);
            }
            catch (Exception var4)
            {
                var4.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Saves the given World Info with the given NBTTagCompound as the Player.
     */
    public void saveWorldInfoWithPlayer(WorldInfo worldInformation, NBTTagCompound tagCompound)
    {
        NBTTagCompound lvt_3_1_ = worldInformation.cloneNBTCompound(tagCompound);
        NBTTagCompound lvt_4_1_ = new NBTTagCompound();
        lvt_4_1_.setTag("Data", lvt_3_1_);

        try
        {
            File lvt_5_1_ = new File(this.worldDirectory, "level.dat_new");
            File lvt_6_1_ = new File(this.worldDirectory, "level.dat_old");
            File lvt_7_1_ = new File(this.worldDirectory, "level.dat");
            CompressedStreamTools.writeCompressed(lvt_4_1_, new FileOutputStream(lvt_5_1_));

            if (lvt_6_1_.exists())
            {
                lvt_6_1_.delete();
            }

            lvt_7_1_.renameTo(lvt_6_1_);

            if (lvt_7_1_.exists())
            {
                lvt_7_1_.delete();
            }

            lvt_5_1_.renameTo(lvt_7_1_);

            if (lvt_5_1_.exists())
            {
                lvt_5_1_.delete();
            }
        }
        catch (Exception var8)
        {
            var8.printStackTrace();
        }
    }

    /**
     * used to update level.dat from old format to MCRegion format
     */
    public void saveWorldInfo(WorldInfo worldInformation)
    {
        NBTTagCompound lvt_2_1_ = worldInformation.getNBTTagCompound();
        NBTTagCompound lvt_3_1_ = new NBTTagCompound();
        lvt_3_1_.setTag("Data", lvt_2_1_);

        try
        {
            File lvt_4_1_ = new File(this.worldDirectory, "level.dat_new");
            File lvt_5_1_ = new File(this.worldDirectory, "level.dat_old");
            File lvt_6_1_ = new File(this.worldDirectory, "level.dat");
            CompressedStreamTools.writeCompressed(lvt_3_1_, new FileOutputStream(lvt_4_1_));

            if (lvt_5_1_.exists())
            {
                lvt_5_1_.delete();
            }

            lvt_6_1_.renameTo(lvt_5_1_);

            if (lvt_6_1_.exists())
            {
                lvt_6_1_.delete();
            }

            lvt_4_1_.renameTo(lvt_6_1_);

            if (lvt_4_1_.exists())
            {
                lvt_4_1_.delete();
            }
        }
        catch (Exception var7)
        {
            var7.printStackTrace();
        }
    }

    /**
     * Writes the player data to disk from the specified PlayerEntityMP.
     */
    public void writePlayerData(EntityPlayer player)
    {
        try
        {
            NBTTagCompound lvt_2_1_ = new NBTTagCompound();
            player.writeToNBT(lvt_2_1_);
            File lvt_3_1_ = new File(this.playersDirectory, player.getUniqueID().toString() + ".dat.tmp");
            File lvt_4_1_ = new File(this.playersDirectory, player.getUniqueID().toString() + ".dat");
            CompressedStreamTools.writeCompressed(lvt_2_1_, new FileOutputStream(lvt_3_1_));

            if (lvt_4_1_.exists())
            {
                lvt_4_1_.delete();
            }

            lvt_3_1_.renameTo(lvt_4_1_);
        }
        catch (Exception var5)
        {
            logger.warn("Failed to save player data for " + player.getName());
        }
    }

    /**
     * Reads the player data from disk into the specified PlayerEntityMP.
     */
    public NBTTagCompound readPlayerData(EntityPlayer player)
    {
        NBTTagCompound lvt_2_1_ = null;

        try
        {
            File lvt_3_1_ = new File(this.playersDirectory, player.getUniqueID().toString() + ".dat");

            if (lvt_3_1_.exists() && lvt_3_1_.isFile())
            {
                lvt_2_1_ = CompressedStreamTools.readCompressed(new FileInputStream(lvt_3_1_));
            }
        }
        catch (Exception var4)
        {
            logger.warn("Failed to load player data for " + player.getName());
        }

        if (lvt_2_1_ != null)
        {
            player.readFromNBT(lvt_2_1_);
        }

        return lvt_2_1_;
    }

    public IPlayerFileData getPlayerNBTManager()
    {
        return this;
    }

    /**
     * Returns an array of usernames for which player.dat exists for.
     */
    public String[] getAvailablePlayerDat()
    {
        String[] lvt_1_1_ = this.playersDirectory.list();

        if (lvt_1_1_ == null)
        {
            lvt_1_1_ = new String[0];
        }

        for (int lvt_2_1_ = 0; lvt_2_1_ < lvt_1_1_.length; ++lvt_2_1_)
        {
            if (lvt_1_1_[lvt_2_1_].endsWith(".dat"))
            {
                lvt_1_1_[lvt_2_1_] = lvt_1_1_[lvt_2_1_].substring(0, lvt_1_1_[lvt_2_1_].length() - 4);
            }
        }

        return lvt_1_1_;
    }

    /**
     * Called to flush all changes to disk, waiting for them to complete.
     */
    public void flush()
    {
    }

    /**
     * Gets the file location of the given map
     */
    public File getMapFileFromName(String mapName)
    {
        return new File(this.mapDataDir, mapName + ".dat");
    }

    /**
     * Returns the name of the directory where world information is saved.
     */
    public String getWorldDirectoryName()
    {
        return this.saveDirectoryName;
    }
}
