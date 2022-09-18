package net.minecraft.util;

public class AxisAlignedBB
{
    public final double minX;
    public final double minY;
    public final double minZ;
    public final double maxX;
    public final double maxY;
    public final double maxZ;

    public AxisAlignedBB(double x1, double y1, double z1, double x2, double y2, double z2)
    {
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        this.maxZ = Math.max(z1, z2);
    }

    public AxisAlignedBB(BlockPos pos1, BlockPos pos2)
    {
        this.minX = (double)pos1.getX();
        this.minY = (double)pos1.getY();
        this.minZ = (double)pos1.getZ();
        this.maxX = (double)pos2.getX();
        this.maxY = (double)pos2.getY();
        this.maxZ = (double)pos2.getZ();
    }

    /**
     * Adds the coordinates to the bounding box extending it if the point lies outside the current ranges. Args: x, y, z
     */
    public AxisAlignedBB addCoord(double x, double y, double z)
    {
        double lvt_7_1_ = this.minX;
        double lvt_9_1_ = this.minY;
        double lvt_11_1_ = this.minZ;
        double lvt_13_1_ = this.maxX;
        double lvt_15_1_ = this.maxY;
        double lvt_17_1_ = this.maxZ;

        if (x < 0.0D)
        {
            lvt_7_1_ += x;
        }
        else if (x > 0.0D)
        {
            lvt_13_1_ += x;
        }

        if (y < 0.0D)
        {
            lvt_9_1_ += y;
        }
        else if (y > 0.0D)
        {
            lvt_15_1_ += y;
        }

        if (z < 0.0D)
        {
            lvt_11_1_ += z;
        }
        else if (z > 0.0D)
        {
            lvt_17_1_ += z;
        }

        return new AxisAlignedBB(lvt_7_1_, lvt_9_1_, lvt_11_1_, lvt_13_1_, lvt_15_1_, lvt_17_1_);
    }

    /**
     * Returns a bounding box expanded by the specified vector (if negative numbers are given it will shrink). Args: x,
     * y, z
     */
    public AxisAlignedBB expand(double x, double y, double z)
    {
        double lvt_7_1_ = this.minX - x;
        double lvt_9_1_ = this.minY - y;
        double lvt_11_1_ = this.minZ - z;
        double lvt_13_1_ = this.maxX + x;
        double lvt_15_1_ = this.maxY + y;
        double lvt_17_1_ = this.maxZ + z;
        return new AxisAlignedBB(lvt_7_1_, lvt_9_1_, lvt_11_1_, lvt_13_1_, lvt_15_1_, lvt_17_1_);
    }

    public AxisAlignedBB union(AxisAlignedBB other)
    {
        double lvt_2_1_ = Math.min(this.minX, other.minX);
        double lvt_4_1_ = Math.min(this.minY, other.minY);
        double lvt_6_1_ = Math.min(this.minZ, other.minZ);
        double lvt_8_1_ = Math.max(this.maxX, other.maxX);
        double lvt_10_1_ = Math.max(this.maxY, other.maxY);
        double lvt_12_1_ = Math.max(this.maxZ, other.maxZ);
        return new AxisAlignedBB(lvt_2_1_, lvt_4_1_, lvt_6_1_, lvt_8_1_, lvt_10_1_, lvt_12_1_);
    }

    /**
     * returns an AABB with corners x1, y1, z1 and x2, y2, z2
     */
    public static AxisAlignedBB fromBounds(double x1, double y1, double z1, double x2, double y2, double z2)
    {
        double lvt_12_1_ = Math.min(x1, x2);
        double lvt_14_1_ = Math.min(y1, y2);
        double lvt_16_1_ = Math.min(z1, z2);
        double lvt_18_1_ = Math.max(x1, x2);
        double lvt_20_1_ = Math.max(y1, y2);
        double lvt_22_1_ = Math.max(z1, z2);
        return new AxisAlignedBB(lvt_12_1_, lvt_14_1_, lvt_16_1_, lvt_18_1_, lvt_20_1_, lvt_22_1_);
    }

    /**
     * Offsets the current bounding box by the specified coordinates. Args: x, y, z
     */
    public AxisAlignedBB offset(double x, double y, double z)
    {
        return new AxisAlignedBB(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
    }

    /**
     * if instance and the argument bounding boxes overlap in the Y and Z dimensions, calculate the offset between them
     * in the X dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public double calculateXOffset(AxisAlignedBB other, double offsetX)
    {
        if (other.maxY > this.minY && other.minY < this.maxY && other.maxZ > this.minZ && other.minZ < this.maxZ)
        {
            if (offsetX > 0.0D && other.maxX <= this.minX)
            {
                double lvt_4_1_ = this.minX - other.maxX;

                if (lvt_4_1_ < offsetX)
                {
                    offsetX = lvt_4_1_;
                }
            }
            else if (offsetX < 0.0D && other.minX >= this.maxX)
            {
                double lvt_4_2_ = this.maxX - other.minX;

                if (lvt_4_2_ > offsetX)
                {
                    offsetX = lvt_4_2_;
                }
            }

            return offsetX;
        }
        else
        {
            return offsetX;
        }
    }

    /**
     * if instance and the argument bounding boxes overlap in the X and Z dimensions, calculate the offset between them
     * in the Y dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public double calculateYOffset(AxisAlignedBB other, double offsetY)
    {
        if (other.maxX > this.minX && other.minX < this.maxX && other.maxZ > this.minZ && other.minZ < this.maxZ)
        {
            if (offsetY > 0.0D && other.maxY <= this.minY)
            {
                double lvt_4_1_ = this.minY - other.maxY;

                if (lvt_4_1_ < offsetY)
                {
                    offsetY = lvt_4_1_;
                }
            }
            else if (offsetY < 0.0D && other.minY >= this.maxY)
            {
                double lvt_4_2_ = this.maxY - other.minY;

                if (lvt_4_2_ > offsetY)
                {
                    offsetY = lvt_4_2_;
                }
            }

            return offsetY;
        }
        else
        {
            return offsetY;
        }
    }

    /**
     * if instance and the argument bounding boxes overlap in the Y and X dimensions, calculate the offset between them
     * in the Z dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public double calculateZOffset(AxisAlignedBB other, double offsetZ)
    {
        if (other.maxX > this.minX && other.minX < this.maxX && other.maxY > this.minY && other.minY < this.maxY)
        {
            if (offsetZ > 0.0D && other.maxZ <= this.minZ)
            {
                double lvt_4_1_ = this.minZ - other.maxZ;

                if (lvt_4_1_ < offsetZ)
                {
                    offsetZ = lvt_4_1_;
                }
            }
            else if (offsetZ < 0.0D && other.minZ >= this.maxZ)
            {
                double lvt_4_2_ = this.maxZ - other.minZ;

                if (lvt_4_2_ > offsetZ)
                {
                    offsetZ = lvt_4_2_;
                }
            }

            return offsetZ;
        }
        else
        {
            return offsetZ;
        }
    }

    /**
     * Returns whether the given bounding box intersects with this one. Args: axisAlignedBB
     */
    public boolean intersectsWith(AxisAlignedBB other)
    {
        return other.maxX > this.minX && other.minX < this.maxX ? (other.maxY > this.minY && other.minY < this.maxY ? other.maxZ > this.minZ && other.minZ < this.maxZ : false) : false;
    }

    /**
     * Returns if the supplied Vec3D is completely inside the bounding box
     */
    public boolean isVecInside(Vec3 vec)
    {
        return vec.xCoord > this.minX && vec.xCoord < this.maxX ? (vec.yCoord > this.minY && vec.yCoord < this.maxY ? vec.zCoord > this.minZ && vec.zCoord < this.maxZ : false) : false;
    }

    /**
     * Returns the average length of the edges of the bounding box.
     */
    public double getAverageEdgeLength()
    {
        double lvt_1_1_ = this.maxX - this.minX;
        double lvt_3_1_ = this.maxY - this.minY;
        double lvt_5_1_ = this.maxZ - this.minZ;
        return (lvt_1_1_ + lvt_3_1_ + lvt_5_1_) / 3.0D;
    }

    /**
     * Returns a bounding box that is inset by the specified amounts
     */
    public AxisAlignedBB contract(double x, double y, double z)
    {
        double lvt_7_1_ = this.minX + x;
        double lvt_9_1_ = this.minY + y;
        double lvt_11_1_ = this.minZ + z;
        double lvt_13_1_ = this.maxX - x;
        double lvt_15_1_ = this.maxY - y;
        double lvt_17_1_ = this.maxZ - z;
        return new AxisAlignedBB(lvt_7_1_, lvt_9_1_, lvt_11_1_, lvt_13_1_, lvt_15_1_, lvt_17_1_);
    }

    public MovingObjectPosition calculateIntercept(Vec3 vecA, Vec3 vecB)
    {
        Vec3 lvt_3_1_ = vecA.getIntermediateWithXValue(vecB, this.minX);
        Vec3 lvt_4_1_ = vecA.getIntermediateWithXValue(vecB, this.maxX);
        Vec3 lvt_5_1_ = vecA.getIntermediateWithYValue(vecB, this.minY);
        Vec3 lvt_6_1_ = vecA.getIntermediateWithYValue(vecB, this.maxY);
        Vec3 lvt_7_1_ = vecA.getIntermediateWithZValue(vecB, this.minZ);
        Vec3 lvt_8_1_ = vecA.getIntermediateWithZValue(vecB, this.maxZ);

        if (!this.isVecInYZ(lvt_3_1_))
        {
            lvt_3_1_ = null;
        }

        if (!this.isVecInYZ(lvt_4_1_))
        {
            lvt_4_1_ = null;
        }

        if (!this.isVecInXZ(lvt_5_1_))
        {
            lvt_5_1_ = null;
        }

        if (!this.isVecInXZ(lvt_6_1_))
        {
            lvt_6_1_ = null;
        }

        if (!this.isVecInXY(lvt_7_1_))
        {
            lvt_7_1_ = null;
        }

        if (!this.isVecInXY(lvt_8_1_))
        {
            lvt_8_1_ = null;
        }

        Vec3 lvt_9_1_ = null;

        if (lvt_3_1_ != null)
        {
            lvt_9_1_ = lvt_3_1_;
        }

        if (lvt_4_1_ != null && (lvt_9_1_ == null || vecA.squareDistanceTo(lvt_4_1_) < vecA.squareDistanceTo(lvt_9_1_)))
        {
            lvt_9_1_ = lvt_4_1_;
        }

        if (lvt_5_1_ != null && (lvt_9_1_ == null || vecA.squareDistanceTo(lvt_5_1_) < vecA.squareDistanceTo(lvt_9_1_)))
        {
            lvt_9_1_ = lvt_5_1_;
        }

        if (lvt_6_1_ != null && (lvt_9_1_ == null || vecA.squareDistanceTo(lvt_6_1_) < vecA.squareDistanceTo(lvt_9_1_)))
        {
            lvt_9_1_ = lvt_6_1_;
        }

        if (lvt_7_1_ != null && (lvt_9_1_ == null || vecA.squareDistanceTo(lvt_7_1_) < vecA.squareDistanceTo(lvt_9_1_)))
        {
            lvt_9_1_ = lvt_7_1_;
        }

        if (lvt_8_1_ != null && (lvt_9_1_ == null || vecA.squareDistanceTo(lvt_8_1_) < vecA.squareDistanceTo(lvt_9_1_)))
        {
            lvt_9_1_ = lvt_8_1_;
        }

        if (lvt_9_1_ == null)
        {
            return null;
        }
        else
        {
            EnumFacing lvt_10_1_ = null;

            if (lvt_9_1_ == lvt_3_1_)
            {
                lvt_10_1_ = EnumFacing.WEST;
            }
            else if (lvt_9_1_ == lvt_4_1_)
            {
                lvt_10_1_ = EnumFacing.EAST;
            }
            else if (lvt_9_1_ == lvt_5_1_)
            {
                lvt_10_1_ = EnumFacing.DOWN;
            }
            else if (lvt_9_1_ == lvt_6_1_)
            {
                lvt_10_1_ = EnumFacing.UP;
            }
            else if (lvt_9_1_ == lvt_7_1_)
            {
                lvt_10_1_ = EnumFacing.NORTH;
            }
            else
            {
                lvt_10_1_ = EnumFacing.SOUTH;
            }

            return new MovingObjectPosition(lvt_9_1_, lvt_10_1_);
        }
    }

    /**
     * Checks if the specified vector is within the YZ dimensions of the bounding box. Args: Vec3D
     */
    private boolean isVecInYZ(Vec3 vec)
    {
        return vec == null ? false : vec.yCoord >= this.minY && vec.yCoord <= this.maxY && vec.zCoord >= this.minZ && vec.zCoord <= this.maxZ;
    }

    /**
     * Checks if the specified vector is within the XZ dimensions of the bounding box. Args: Vec3D
     */
    private boolean isVecInXZ(Vec3 vec)
    {
        return vec == null ? false : vec.xCoord >= this.minX && vec.xCoord <= this.maxX && vec.zCoord >= this.minZ && vec.zCoord <= this.maxZ;
    }

    /**
     * Checks if the specified vector is within the XY dimensions of the bounding box. Args: Vec3D
     */
    private boolean isVecInXY(Vec3 vec)
    {
        return vec == null ? false : vec.xCoord >= this.minX && vec.xCoord <= this.maxX && vec.yCoord >= this.minY && vec.yCoord <= this.maxY;
    }

    public String toString()
    {
        return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
    }

    public boolean hasNaN()
    {
        return Double.isNaN(this.minX) || Double.isNaN(this.minY) || Double.isNaN(this.minZ) || Double.isNaN(this.maxX) || Double.isNaN(this.maxY) || Double.isNaN(this.maxZ);
    }
}
