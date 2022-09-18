package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.world.WorldSavedData;

public class MapStorage
{
    private ISaveHandler saveHandler;
    protected Map<String, WorldSavedData> loadedDataMap = Maps.newHashMap();
    private List<WorldSavedData> loadedDataList = Lists.newArrayList();
    private Map<String, Short> idCounts = Maps.newHashMap();

    public MapStorage(ISaveHandler saveHandlerIn)
    {
        this.saveHandler = saveHandlerIn;
        this.loadIdCounts();
    }

    /**
     * Loads an existing MapDataBase corresponding to the given String id from disk, instantiating the given Class, or
     * returns null if none such file exists. args: Class to instantiate, String dataid
     */
    public WorldSavedData loadData(Class <? extends WorldSavedData > clazz, String dataIdentifier)
    {
        WorldSavedData lvt_3_1_ = (WorldSavedData)this.loadedDataMap.get(dataIdentifier);

        if (lvt_3_1_ != null)
        {
            return lvt_3_1_;
        }
        else
        {
            if (this.saveHandler != null)
            {
                try
                {
                    File lvt_4_1_ = this.saveHandler.getMapFileFromName(dataIdentifier);

                    if (lvt_4_1_ != null && lvt_4_1_.exists())
                    {
                        try
                        {
                            lvt_3_1_ = (WorldSavedData)clazz.getConstructor(new Class[] {String.class}).newInstance(new Object[] {dataIdentifier});
                        }
                        catch (Exception var7)
                        {
                            throw new RuntimeException("Failed to instantiate " + clazz.toString(), var7);
                        }

                        FileInputStream lvt_5_2_ = new FileInputStream(lvt_4_1_);
                        NBTTagCompound lvt_6_1_ = CompressedStreamTools.readCompressed(lvt_5_2_);
                        lvt_5_2_.close();
                        lvt_3_1_.readFromNBT(lvt_6_1_.getCompoundTag("data"));
                    }
                }
                catch (Exception var8)
                {
                    var8.printStackTrace();
                }
            }

            if (lvt_3_1_ != null)
            {
                this.loadedDataMap.put(dataIdentifier, lvt_3_1_);
                this.loadedDataList.add(lvt_3_1_);
            }

            return lvt_3_1_;
        }
    }

    /**
     * Assigns the given String id to the given MapDataBase, removing any existing ones of the same id.
     */
    public void setData(String dataIdentifier, WorldSavedData data)
    {
        if (this.loadedDataMap.containsKey(dataIdentifier))
        {
            this.loadedDataList.remove(this.loadedDataMap.remove(dataIdentifier));
        }

        this.loadedDataMap.put(dataIdentifier, data);
        this.loadedDataList.add(data);
    }

    /**
     * Saves all dirty loaded MapDataBases to disk.
     */
    public void saveAllData()
    {
        for (int lvt_1_1_ = 0; lvt_1_1_ < this.loadedDataList.size(); ++lvt_1_1_)
        {
            WorldSavedData lvt_2_1_ = (WorldSavedData)this.loadedDataList.get(lvt_1_1_);

            if (lvt_2_1_.isDirty())
            {
                this.saveData(lvt_2_1_);
                lvt_2_1_.setDirty(false);
            }
        }
    }

    /**
     * Saves the given MapDataBase to disk.
     */
    private void saveData(WorldSavedData p_75747_1_)
    {
        if (this.saveHandler != null)
        {
            try
            {
                File lvt_2_1_ = this.saveHandler.getMapFileFromName(p_75747_1_.mapName);

                if (lvt_2_1_ != null)
                {
                    NBTTagCompound lvt_3_1_ = new NBTTagCompound();
                    p_75747_1_.writeToNBT(lvt_3_1_);
                    NBTTagCompound lvt_4_1_ = new NBTTagCompound();
                    lvt_4_1_.setTag("data", lvt_3_1_);
                    FileOutputStream lvt_5_1_ = new FileOutputStream(lvt_2_1_);
                    CompressedStreamTools.writeCompressed(lvt_4_1_, lvt_5_1_);
                    lvt_5_1_.close();
                }
            }
            catch (Exception var6)
            {
                var6.printStackTrace();
            }
        }
    }

    /**
     * Loads the idCounts Map from the 'idcounts' file.
     */
    private void loadIdCounts()
    {
        try
        {
            this.idCounts.clear();

            if (this.saveHandler == null)
            {
                return;
            }

            File lvt_1_1_ = this.saveHandler.getMapFileFromName("idcounts");

            if (lvt_1_1_ != null && lvt_1_1_.exists())
            {
                DataInputStream lvt_2_1_ = new DataInputStream(new FileInputStream(lvt_1_1_));
                NBTTagCompound lvt_3_1_ = CompressedStreamTools.read(lvt_2_1_);
                lvt_2_1_.close();

                for (String lvt_5_1_ : lvt_3_1_.getKeySet())
                {
                    NBTBase lvt_6_1_ = lvt_3_1_.getTag(lvt_5_1_);

                    if (lvt_6_1_ instanceof NBTTagShort)
                    {
                        NBTTagShort lvt_7_1_ = (NBTTagShort)lvt_6_1_;
                        short lvt_9_1_ = lvt_7_1_.getShort();
                        this.idCounts.put(lvt_5_1_, Short.valueOf(lvt_9_1_));
                    }
                }
            }
        }
        catch (Exception var10)
        {
            var10.printStackTrace();
        }
    }

    /**
     * Returns an unique new data id for the given prefix and saves the idCounts map to the 'idcounts' file.
     */
    public int getUniqueDataId(String key)
    {
        Short lvt_2_1_ = (Short)this.idCounts.get(key);

        if (lvt_2_1_ == null)
        {
            lvt_2_1_ = Short.valueOf((short)0);
        }
        else
        {
            lvt_2_1_ = Short.valueOf((short)(lvt_2_1_.shortValue() + 1));
        }

        this.idCounts.put(key, lvt_2_1_);

        if (this.saveHandler == null)
        {
            return lvt_2_1_.shortValue();
        }
        else
        {
            try
            {
                File lvt_3_1_ = this.saveHandler.getMapFileFromName("idcounts");

                if (lvt_3_1_ != null)
                {
                    NBTTagCompound lvt_4_1_ = new NBTTagCompound();

                    for (String lvt_6_1_ : this.idCounts.keySet())
                    {
                        short lvt_7_1_ = ((Short)this.idCounts.get(lvt_6_1_)).shortValue();
                        lvt_4_1_.setShort(lvt_6_1_, lvt_7_1_);
                    }

                    DataOutputStream lvt_5_2_ = new DataOutputStream(new FileOutputStream(lvt_3_1_));
                    CompressedStreamTools.write(lvt_4_1_, lvt_5_2_);
                    lvt_5_2_.close();
                }
            }
            catch (Exception var8)
            {
                var8.printStackTrace();
            }

            return lvt_2_1_.shortValue();
        }
    }
}
