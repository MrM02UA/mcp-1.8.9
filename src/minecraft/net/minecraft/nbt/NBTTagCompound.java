package net.minecraft.nbt;

import com.google.common.collect.Maps;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;

public class NBTTagCompound extends NBTBase
{
    private Map<String, NBTBase> tagMap = Maps.newHashMap();

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(DataOutput output) throws IOException
    {
        for (String lvt_3_1_ : this.tagMap.keySet())
        {
            NBTBase lvt_4_1_ = (NBTBase)this.tagMap.get(lvt_3_1_);
            writeEntry(lvt_3_1_, lvt_4_1_, output);
        }

        output.writeByte(0);
    }

    void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException
    {
        sizeTracker.read(384L);

        if (depth > 512)
        {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
        }
        else
        {
            this.tagMap.clear();
            byte lvt_4_1_;

            while ((lvt_4_1_ = readType(input, sizeTracker)) != 0)
            {
                String lvt_5_1_ = readKey(input, sizeTracker);
                sizeTracker.read((long)(224 + 16 * lvt_5_1_.length()));
                NBTBase lvt_6_1_ = readNBT(lvt_4_1_, lvt_5_1_, input, depth + 1, sizeTracker);

                if (this.tagMap.put(lvt_5_1_, lvt_6_1_) != null)
                {
                    sizeTracker.read(288L);
                }
            }
        }
    }

    public Set<String> getKeySet()
    {
        return this.tagMap.keySet();
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return (byte)10;
    }

    /**
     * Stores the given tag into the map with the given string key. This is mostly used to store tag lists.
     */
    public void setTag(String key, NBTBase value)
    {
        this.tagMap.put(key, value);
    }

    /**
     * Stores a new NBTTagByte with the given byte value into the map with the given string key.
     */
    public void setByte(String key, byte value)
    {
        this.tagMap.put(key, new NBTTagByte(value));
    }

    /**
     * Stores a new NBTTagShort with the given short value into the map with the given string key.
     */
    public void setShort(String key, short value)
    {
        this.tagMap.put(key, new NBTTagShort(value));
    }

    /**
     * Stores a new NBTTagInt with the given integer value into the map with the given string key.
     */
    public void setInteger(String key, int value)
    {
        this.tagMap.put(key, new NBTTagInt(value));
    }

    /**
     * Stores a new NBTTagLong with the given long value into the map with the given string key.
     */
    public void setLong(String key, long value)
    {
        this.tagMap.put(key, new NBTTagLong(value));
    }

    /**
     * Stores a new NBTTagFloat with the given float value into the map with the given string key.
     */
    public void setFloat(String key, float value)
    {
        this.tagMap.put(key, new NBTTagFloat(value));
    }

    /**
     * Stores a new NBTTagDouble with the given double value into the map with the given string key.
     */
    public void setDouble(String key, double value)
    {
        this.tagMap.put(key, new NBTTagDouble(value));
    }

    /**
     * Stores a new NBTTagString with the given string value into the map with the given string key.
     */
    public void setString(String key, String value)
    {
        this.tagMap.put(key, new NBTTagString(value));
    }

    /**
     * Stores a new NBTTagByteArray with the given array as data into the map with the given string key.
     */
    public void setByteArray(String key, byte[] value)
    {
        this.tagMap.put(key, new NBTTagByteArray(value));
    }

    /**
     * Stores a new NBTTagIntArray with the given array as data into the map with the given string key.
     */
    public void setIntArray(String key, int[] value)
    {
        this.tagMap.put(key, new NBTTagIntArray(value));
    }

    /**
     * Stores the given boolean value as a NBTTagByte, storing 1 for true and 0 for false, using the given string key.
     */
    public void setBoolean(String key, boolean value)
    {
        this.setByte(key, (byte)(value ? 1 : 0));
    }

    /**
     * gets a generic tag with the specified name
     */
    public NBTBase getTag(String key)
    {
        return (NBTBase)this.tagMap.get(key);
    }

    /**
     * Gets the ID byte for the given tag key
     */
    public byte getTagId(String key)
    {
        NBTBase lvt_2_1_ = (NBTBase)this.tagMap.get(key);
        return lvt_2_1_ != null ? lvt_2_1_.getId() : 0;
    }

    /**
     * Returns whether the given string has been previously stored as a key in the map.
     */
    public boolean hasKey(String key)
    {
        return this.tagMap.containsKey(key);
    }

    public boolean hasKey(String key, int type)
    {
        int lvt_3_1_ = this.getTagId(key);

        if (lvt_3_1_ == type)
        {
            return true;
        }
        else if (type != 99)
        {
            if (lvt_3_1_ > 0)
            {
                ;
            }

            return false;
        }
        else
        {
            return lvt_3_1_ == 1 || lvt_3_1_ == 2 || lvt_3_1_ == 3 || lvt_3_1_ == 4 || lvt_3_1_ == 5 || lvt_3_1_ == 6;
        }
    }

    /**
     * Retrieves a byte value using the specified key, or 0 if no such key was stored.
     */
    public byte getByte(String key)
    {
        try
        {
            return !this.hasKey(key, 99) ? 0 : ((NBTBase.NBTPrimitive)this.tagMap.get(key)).getByte();
        }
        catch (ClassCastException var3)
        {
            return (byte)0;
        }
    }

    /**
     * Retrieves a short value using the specified key, or 0 if no such key was stored.
     */
    public short getShort(String key)
    {
        try
        {
            return !this.hasKey(key, 99) ? 0 : ((NBTBase.NBTPrimitive)this.tagMap.get(key)).getShort();
        }
        catch (ClassCastException var3)
        {
            return (short)0;
        }
    }

    /**
     * Retrieves an integer value using the specified key, or 0 if no such key was stored.
     */
    public int getInteger(String key)
    {
        try
        {
            return !this.hasKey(key, 99) ? 0 : ((NBTBase.NBTPrimitive)this.tagMap.get(key)).getInt();
        }
        catch (ClassCastException var3)
        {
            return 0;
        }
    }

    /**
     * Retrieves a long value using the specified key, or 0 if no such key was stored.
     */
    public long getLong(String key)
    {
        try
        {
            return !this.hasKey(key, 99) ? 0L : ((NBTBase.NBTPrimitive)this.tagMap.get(key)).getLong();
        }
        catch (ClassCastException var3)
        {
            return 0L;
        }
    }

    /**
     * Retrieves a float value using the specified key, or 0 if no such key was stored.
     */
    public float getFloat(String key)
    {
        try
        {
            return !this.hasKey(key, 99) ? 0.0F : ((NBTBase.NBTPrimitive)this.tagMap.get(key)).getFloat();
        }
        catch (ClassCastException var3)
        {
            return 0.0F;
        }
    }

    /**
     * Retrieves a double value using the specified key, or 0 if no such key was stored.
     */
    public double getDouble(String key)
    {
        try
        {
            return !this.hasKey(key, 99) ? 0.0D : ((NBTBase.NBTPrimitive)this.tagMap.get(key)).getDouble();
        }
        catch (ClassCastException var3)
        {
            return 0.0D;
        }
    }

    /**
     * Retrieves a string value using the specified key, or an empty string if no such key was stored.
     */
    public String getString(String key)
    {
        try
        {
            return !this.hasKey(key, 8) ? "" : ((NBTBase)this.tagMap.get(key)).getString();
        }
        catch (ClassCastException var3)
        {
            return "";
        }
    }

    /**
     * Retrieves a byte array using the specified key, or a zero-length array if no such key was stored.
     */
    public byte[] getByteArray(String key)
    {
        try
        {
            return !this.hasKey(key, 7) ? new byte[0] : ((NBTTagByteArray)this.tagMap.get(key)).getByteArray();
        }
        catch (ClassCastException var3)
        {
            throw new ReportedException(this.createCrashReport(key, 7, var3));
        }
    }

    /**
     * Retrieves an int array using the specified key, or a zero-length array if no such key was stored.
     */
    public int[] getIntArray(String key)
    {
        try
        {
            return !this.hasKey(key, 11) ? new int[0] : ((NBTTagIntArray)this.tagMap.get(key)).getIntArray();
        }
        catch (ClassCastException var3)
        {
            throw new ReportedException(this.createCrashReport(key, 11, var3));
        }
    }

    /**
     * Retrieves a NBTTagCompound subtag matching the specified key, or a new empty NBTTagCompound if no such key was
     * stored.
     */
    public NBTTagCompound getCompoundTag(String key)
    {
        try
        {
            return !this.hasKey(key, 10) ? new NBTTagCompound() : (NBTTagCompound)this.tagMap.get(key);
        }
        catch (ClassCastException var3)
        {
            throw new ReportedException(this.createCrashReport(key, 10, var3));
        }
    }

    /**
     * Gets the NBTTagList object with the given name. Args: name, NBTBase type
     */
    public NBTTagList getTagList(String key, int type)
    {
        try
        {
            if (this.getTagId(key) != 9)
            {
                return new NBTTagList();
            }
            else
            {
                NBTTagList lvt_3_1_ = (NBTTagList)this.tagMap.get(key);
                return lvt_3_1_.tagCount() > 0 && lvt_3_1_.getTagType() != type ? new NBTTagList() : lvt_3_1_;
            }
        }
        catch (ClassCastException var4)
        {
            throw new ReportedException(this.createCrashReport(key, 9, var4));
        }
    }

    /**
     * Retrieves a boolean value using the specified key, or false if no such key was stored. This uses the getByte
     * method.
     */
    public boolean getBoolean(String key)
    {
        return this.getByte(key) != 0;
    }

    /**
     * Remove the specified tag.
     */
    public void removeTag(String key)
    {
        this.tagMap.remove(key);
    }

    public String toString()
    {
        StringBuilder lvt_1_1_ = new StringBuilder("{");

        for (Entry<String, NBTBase> lvt_3_1_ : this.tagMap.entrySet())
        {
            if (lvt_1_1_.length() != 1)
            {
                lvt_1_1_.append(',');
            }

            lvt_1_1_.append((String)lvt_3_1_.getKey()).append(':').append(lvt_3_1_.getValue());
        }

        return lvt_1_1_.append('}').toString();
    }

    /**
     * Return whether this compound has no tags.
     */
    public boolean hasNoTags()
    {
        return this.tagMap.isEmpty();
    }

    /**
     * Create a crash report which indicates a NBT read error.
     */
    private CrashReport createCrashReport(final String key, final int expectedType, ClassCastException ex)
    {
        CrashReport lvt_4_1_ = CrashReport.makeCrashReport(ex, "Reading NBT data");
        CrashReportCategory lvt_5_1_ = lvt_4_1_.makeCategoryDepth("Corrupt NBT tag", 1);
        lvt_5_1_.addCrashSectionCallable("Tag type found", new Callable<String>()
        {
            public String call() throws Exception
            {
                return NBTBase.NBT_TYPES[((NBTBase)NBTTagCompound.this.tagMap.get(key)).getId()];
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        lvt_5_1_.addCrashSectionCallable("Tag type expected", new Callable<String>()
        {
            public String call() throws Exception
            {
                return NBTBase.NBT_TYPES[expectedType];
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        lvt_5_1_.addCrashSection("Tag name", key);
        return lvt_4_1_;
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy()
    {
        NBTTagCompound lvt_1_1_ = new NBTTagCompound();

        for (String lvt_3_1_ : this.tagMap.keySet())
        {
            lvt_1_1_.setTag(lvt_3_1_, ((NBTBase)this.tagMap.get(lvt_3_1_)).copy());
        }

        return lvt_1_1_;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (super.equals(p_equals_1_))
        {
            NBTTagCompound lvt_2_1_ = (NBTTagCompound)p_equals_1_;
            return this.tagMap.entrySet().equals(lvt_2_1_.tagMap.entrySet());
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return super.hashCode() ^ this.tagMap.hashCode();
    }

    private static void writeEntry(String name, NBTBase data, DataOutput output) throws IOException
    {
        output.writeByte(data.getId());

        if (data.getId() != 0)
        {
            output.writeUTF(name);
            data.write(output);
        }
    }

    private static byte readType(DataInput input, NBTSizeTracker sizeTracker) throws IOException
    {
        return input.readByte();
    }

    private static String readKey(DataInput input, NBTSizeTracker sizeTracker) throws IOException
    {
        return input.readUTF();
    }

    static NBTBase readNBT(byte id, String key, DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException
    {
        NBTBase lvt_5_1_ = NBTBase.createNewByType(id);

        try
        {
            lvt_5_1_.read(input, depth, sizeTracker);
            return lvt_5_1_;
        }
        catch (IOException var9)
        {
            CrashReport lvt_7_1_ = CrashReport.makeCrashReport(var9, "Loading NBT data");
            CrashReportCategory lvt_8_1_ = lvt_7_1_.makeCategory("NBT Tag");
            lvt_8_1_.addCrashSection("Tag name", key);
            lvt_8_1_.addCrashSection("Tag type", Byte.valueOf(id));
            throw new ReportedException(lvt_7_1_);
        }
    }

    /**
     * Merges this NBTTagCompound with the given compound. Any sub-compounds are merged using the same methods, other
     * types of tags are overwritten from the given compound.
     */
    public void merge(NBTTagCompound other)
    {
        for (String lvt_3_1_ : other.tagMap.keySet())
        {
            NBTBase lvt_4_1_ = (NBTBase)other.tagMap.get(lvt_3_1_);

            if (lvt_4_1_.getId() == 10)
            {
                if (this.hasKey(lvt_3_1_, 10))
                {
                    NBTTagCompound lvt_5_1_ = this.getCompoundTag(lvt_3_1_);
                    lvt_5_1_.merge((NBTTagCompound)lvt_4_1_);
                }
                else
                {
                    this.setTag(lvt_3_1_, lvt_4_1_.copy());
                }
            }
            else
            {
                this.setTag(lvt_3_1_, lvt_4_1_.copy());
            }
        }
    }
}
