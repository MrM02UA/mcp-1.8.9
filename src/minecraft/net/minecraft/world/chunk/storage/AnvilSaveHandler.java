package net.minecraft.world.chunk.storage;

import java.io.File;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.ThreadedFileIOBase;
import net.minecraft.world.storage.WorldInfo;

public class AnvilSaveHandler extends SaveHandler
{
    public AnvilSaveHandler(File savesDirectory, String directoryName, boolean storePlayerdata)
    {
        super(savesDirectory, directoryName, storePlayerdata);
    }

    /**
     * initializes and returns the chunk loader for the specified world provider
     */
    public IChunkLoader getChunkLoader(WorldProvider provider)
    {
        File lvt_2_1_ = this.getWorldDirectory();

        if (provider instanceof WorldProviderHell)
        {
            File lvt_3_1_ = new File(lvt_2_1_, "DIM-1");
            lvt_3_1_.mkdirs();
            return new AnvilChunkLoader(lvt_3_1_);
        }
        else if (provider instanceof WorldProviderEnd)
        {
            File lvt_3_2_ = new File(lvt_2_1_, "DIM1");
            lvt_3_2_.mkdirs();
            return new AnvilChunkLoader(lvt_3_2_);
        }
        else
        {
            return new AnvilChunkLoader(lvt_2_1_);
        }
    }

    /**
     * Saves the given World Info with the given NBTTagCompound as the Player.
     */
    public void saveWorldInfoWithPlayer(WorldInfo worldInformation, NBTTagCompound tagCompound)
    {
        worldInformation.setSaveVersion(19133);
        super.saveWorldInfoWithPlayer(worldInformation, tagCompound);
    }

    /**
     * Called to flush all changes to disk, waiting for them to complete.
     */
    public void flush()
    {
        try
        {
            ThreadedFileIOBase.getThreadedIOInstance().waitForFinish();
        }
        catch (InterruptedException var2)
        {
            var2.printStackTrace();
        }

        RegionFileCache.clearRegionFileReferences();
    }
}
