package net.minecraft.util;

import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;

public class Rotations
{
    /** Rotation on the X axis */
    protected final float x;

    /** Rotation on the Y axis */
    protected final float y;

    /** Rotation on the Z axis */
    protected final float z;

    public Rotations(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Rotations(NBTTagList nbt)
    {
        this.x = nbt.getFloatAt(0);
        this.y = nbt.getFloatAt(1);
        this.z = nbt.getFloatAt(2);
    }

    public NBTTagList writeToNBT()
    {
        NBTTagList lvt_1_1_ = new NBTTagList();
        lvt_1_1_.appendTag(new NBTTagFloat(this.x));
        lvt_1_1_.appendTag(new NBTTagFloat(this.y));
        lvt_1_1_.appendTag(new NBTTagFloat(this.z));
        return lvt_1_1_;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (!(p_equals_1_ instanceof Rotations))
        {
            return false;
        }
        else
        {
            Rotations lvt_2_1_ = (Rotations)p_equals_1_;
            return this.x == lvt_2_1_.x && this.y == lvt_2_1_.y && this.z == lvt_2_1_.z;
        }
    }

    /**
     * Gets the X axis rotation
     */
    public float getX()
    {
        return this.x;
    }

    /**
     * Gets the Y axis rotation
     */
    public float getY()
    {
        return this.y;
    }

    /**
     * Gets the Z axis rotation
     */
    public float getZ()
    {
        return this.z;
    }
}
