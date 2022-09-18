package net.minecraft.pathfinding;

import net.minecraft.util.MathHelper;

public class PathPoint
{
    /** The x coordinate of this point */
    public final int xCoord;

    /** The y coordinate of this point */
    public final int yCoord;

    /** The z coordinate of this point */
    public final int zCoord;

    /** A hash of the coordinates used to identify this point */
    private final int hash;

    /** The index of this point in its assigned path */
    int index = -1;

    /** The distance along the path to this point */
    float totalPathDistance;

    /** The linear distance to the next point */
    float distanceToNext;

    /** The distance to the target */
    float distanceToTarget;

    /** The point preceding this in its assigned path */
    PathPoint previous;

    /** True if the pathfinder has already visited this point */
    public boolean visited;

    public PathPoint(int x, int y, int z)
    {
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
        this.hash = makeHash(x, y, z);
    }

    public static int makeHash(int x, int y, int z)
    {
        return y & 255 | (x & 32767) << 8 | (z & 32767) << 24 | (x < 0 ? Integer.MIN_VALUE : 0) | (z < 0 ? 32768 : 0);
    }

    /**
     * Returns the linear distance to another path point
     */
    public float distanceTo(PathPoint pathpointIn)
    {
        float lvt_2_1_ = (float)(pathpointIn.xCoord - this.xCoord);
        float lvt_3_1_ = (float)(pathpointIn.yCoord - this.yCoord);
        float lvt_4_1_ = (float)(pathpointIn.zCoord - this.zCoord);
        return MathHelper.sqrt_float(lvt_2_1_ * lvt_2_1_ + lvt_3_1_ * lvt_3_1_ + lvt_4_1_ * lvt_4_1_);
    }

    /**
     * Returns the squared distance to another path point
     */
    public float distanceToSquared(PathPoint pathpointIn)
    {
        float lvt_2_1_ = (float)(pathpointIn.xCoord - this.xCoord);
        float lvt_3_1_ = (float)(pathpointIn.yCoord - this.yCoord);
        float lvt_4_1_ = (float)(pathpointIn.zCoord - this.zCoord);
        return lvt_2_1_ * lvt_2_1_ + lvt_3_1_ * lvt_3_1_ + lvt_4_1_ * lvt_4_1_;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (!(p_equals_1_ instanceof PathPoint))
        {
            return false;
        }
        else
        {
            PathPoint lvt_2_1_ = (PathPoint)p_equals_1_;
            return this.hash == lvt_2_1_.hash && this.xCoord == lvt_2_1_.xCoord && this.yCoord == lvt_2_1_.yCoord && this.zCoord == lvt_2_1_.zCoord;
        }
    }

    public int hashCode()
    {
        return this.hash;
    }

    /**
     * Returns true if this point has already been assigned to a path
     */
    public boolean isAssigned()
    {
        return this.index >= 0;
    }

    public String toString()
    {
        return this.xCoord + ", " + this.yCoord + ", " + this.zCoord;
    }
}
