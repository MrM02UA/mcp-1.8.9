package net.minecraft.nbt;

import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NBTTagList extends NBTBase
{
    private static final Logger LOGGER = LogManager.getLogger();
    private List<NBTBase> tagList = Lists.newArrayList();

    /**
     * The type byte for the tags in the list - they must all be of the same type.
     */
    private byte tagType = 0;

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(DataOutput output) throws IOException
    {
        if (!this.tagList.isEmpty())
        {
            this.tagType = ((NBTBase)this.tagList.get(0)).getId();
        }
        else
        {
            this.tagType = 0;
        }

        output.writeByte(this.tagType);
        output.writeInt(this.tagList.size());

        for (int lvt_2_1_ = 0; lvt_2_1_ < this.tagList.size(); ++lvt_2_1_)
        {
            ((NBTBase)this.tagList.get(lvt_2_1_)).write(output);
        }
    }

    void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException
    {
        sizeTracker.read(296L);

        if (depth > 512)
        {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
        }
        else
        {
            this.tagType = input.readByte();
            int lvt_4_1_ = input.readInt();

            if (this.tagType == 0 && lvt_4_1_ > 0)
            {
                throw new RuntimeException("Missing type on ListTag");
            }
            else
            {
                sizeTracker.read(32L * (long)lvt_4_1_);
                this.tagList = Lists.newArrayListWithCapacity(lvt_4_1_);

                for (int lvt_5_1_ = 0; lvt_5_1_ < lvt_4_1_; ++lvt_5_1_)
                {
                    NBTBase lvt_6_1_ = NBTBase.createNewByType(this.tagType);
                    lvt_6_1_.read(input, depth + 1, sizeTracker);
                    this.tagList.add(lvt_6_1_);
                }
            }
        }
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return (byte)9;
    }

    public String toString()
    {
        StringBuilder lvt_1_1_ = new StringBuilder("[");

        for (int lvt_2_1_ = 0; lvt_2_1_ < this.tagList.size(); ++lvt_2_1_)
        {
            if (lvt_2_1_ != 0)
            {
                lvt_1_1_.append(',');
            }

            lvt_1_1_.append(lvt_2_1_).append(':').append(this.tagList.get(lvt_2_1_));
        }

        return lvt_1_1_.append(']').toString();
    }

    /**
     * Adds the provided tag to the end of the list. There is no check to verify this tag is of the same type as any
     * previous tag.
     */
    public void appendTag(NBTBase nbt)
    {
        if (nbt.getId() == 0)
        {
            LOGGER.warn("Invalid TagEnd added to ListTag");
        }
        else
        {
            if (this.tagType == 0)
            {
                this.tagType = nbt.getId();
            }
            else if (this.tagType != nbt.getId())
            {
                LOGGER.warn("Adding mismatching tag types to tag list");
                return;
            }

            this.tagList.add(nbt);
        }
    }

    /**
     * Set the given index to the given tag
     */
    public void set(int idx, NBTBase nbt)
    {
        if (nbt.getId() == 0)
        {
            LOGGER.warn("Invalid TagEnd added to ListTag");
        }
        else if (idx >= 0 && idx < this.tagList.size())
        {
            if (this.tagType == 0)
            {
                this.tagType = nbt.getId();
            }
            else if (this.tagType != nbt.getId())
            {
                LOGGER.warn("Adding mismatching tag types to tag list");
                return;
            }

            this.tagList.set(idx, nbt);
        }
        else
        {
            LOGGER.warn("index out of bounds to set tag in tag list");
        }
    }

    /**
     * Removes a tag at the given index.
     */
    public NBTBase removeTag(int i)
    {
        return (NBTBase)this.tagList.remove(i);
    }

    /**
     * Return whether this compound has no tags.
     */
    public boolean hasNoTags()
    {
        return this.tagList.isEmpty();
    }

    /**
     * Retrieves the NBTTagCompound at the specified index in the list
     */
    public NBTTagCompound getCompoundTagAt(int i)
    {
        if (i >= 0 && i < this.tagList.size())
        {
            NBTBase lvt_2_1_ = (NBTBase)this.tagList.get(i);
            return lvt_2_1_.getId() == 10 ? (NBTTagCompound)lvt_2_1_ : new NBTTagCompound();
        }
        else
        {
            return new NBTTagCompound();
        }
    }

    public int[] getIntArrayAt(int i)
    {
        if (i >= 0 && i < this.tagList.size())
        {
            NBTBase lvt_2_1_ = (NBTBase)this.tagList.get(i);
            return lvt_2_1_.getId() == 11 ? ((NBTTagIntArray)lvt_2_1_).getIntArray() : new int[0];
        }
        else
        {
            return new int[0];
        }
    }

    public double getDoubleAt(int i)
    {
        if (i >= 0 && i < this.tagList.size())
        {
            NBTBase lvt_2_1_ = (NBTBase)this.tagList.get(i);
            return lvt_2_1_.getId() == 6 ? ((NBTTagDouble)lvt_2_1_).getDouble() : 0.0D;
        }
        else
        {
            return 0.0D;
        }
    }

    public float getFloatAt(int i)
    {
        if (i >= 0 && i < this.tagList.size())
        {
            NBTBase lvt_2_1_ = (NBTBase)this.tagList.get(i);
            return lvt_2_1_.getId() == 5 ? ((NBTTagFloat)lvt_2_1_).getFloat() : 0.0F;
        }
        else
        {
            return 0.0F;
        }
    }

    /**
     * Retrieves the tag String value at the specified index in the list
     */
    public String getStringTagAt(int i)
    {
        if (i >= 0 && i < this.tagList.size())
        {
            NBTBase lvt_2_1_ = (NBTBase)this.tagList.get(i);
            return lvt_2_1_.getId() == 8 ? lvt_2_1_.getString() : lvt_2_1_.toString();
        }
        else
        {
            return "";
        }
    }

    /**
     * Get the tag at the given position
     */
    public NBTBase get(int idx)
    {
        return (NBTBase)(idx >= 0 && idx < this.tagList.size() ? (NBTBase)this.tagList.get(idx) : new NBTTagEnd());
    }

    /**
     * Returns the number of tags in the list.
     */
    public int tagCount()
    {
        return this.tagList.size();
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy()
    {
        NBTTagList lvt_1_1_ = new NBTTagList();
        lvt_1_1_.tagType = this.tagType;

        for (NBTBase lvt_3_1_ : this.tagList)
        {
            NBTBase lvt_4_1_ = lvt_3_1_.copy();
            lvt_1_1_.tagList.add(lvt_4_1_);
        }

        return lvt_1_1_;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (super.equals(p_equals_1_))
        {
            NBTTagList lvt_2_1_ = (NBTTagList)p_equals_1_;

            if (this.tagType == lvt_2_1_.tagType)
            {
                return this.tagList.equals(lvt_2_1_.tagList);
            }
        }

        return false;
    }

    public int hashCode()
    {
        return super.hashCode() ^ this.tagList.hashCode();
    }

    public int getTagType()
    {
        return this.tagType;
    }
}
