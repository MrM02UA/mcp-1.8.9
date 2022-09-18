package net.minecraft.util;

public class Vec3
{
    /** X coordinate of Vec3D */
    public final double xCoord;

    /** Y coordinate of Vec3D */
    public final double yCoord;

    /** Z coordinate of Vec3D */
    public final double zCoord;

    public Vec3(double x, double y, double z)
    {
        if (x == -0.0D)
        {
            x = 0.0D;
        }

        if (y == -0.0D)
        {
            y = 0.0D;
        }

        if (z == -0.0D)
        {
            z = 0.0D;
        }

        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
    }

    public Vec3(Vec3i p_i46377_1_)
    {
        this((double)p_i46377_1_.getX(), (double)p_i46377_1_.getY(), (double)p_i46377_1_.getZ());
    }

    /**
     * Returns a new vector with the result of the specified vector minus this.
     */
    public Vec3 subtractReverse(Vec3 vec)
    {
        return new Vec3(vec.xCoord - this.xCoord, vec.yCoord - this.yCoord, vec.zCoord - this.zCoord);
    }

    /**
     * Normalizes the vector to a length of 1 (except if it is the zero vector)
     */
    public Vec3 normalize()
    {
        double lvt_1_1_ = (double)MathHelper.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
        return lvt_1_1_ < 1.0E-4D ? new Vec3(0.0D, 0.0D, 0.0D) : new Vec3(this.xCoord / lvt_1_1_, this.yCoord / lvt_1_1_, this.zCoord / lvt_1_1_);
    }

    public double dotProduct(Vec3 vec)
    {
        return this.xCoord * vec.xCoord + this.yCoord * vec.yCoord + this.zCoord * vec.zCoord;
    }

    /**
     * Returns a new vector with the result of this vector x the specified vector.
     */
    public Vec3 crossProduct(Vec3 vec)
    {
        return new Vec3(this.yCoord * vec.zCoord - this.zCoord * vec.yCoord, this.zCoord * vec.xCoord - this.xCoord * vec.zCoord, this.xCoord * vec.yCoord - this.yCoord * vec.xCoord);
    }

    public Vec3 subtract(Vec3 vec)
    {
        return this.subtract(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    public Vec3 subtract(double x, double y, double z)
    {
        return this.addVector(-x, -y, -z);
    }

    public Vec3 add(Vec3 vec)
    {
        return this.addVector(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    /**
     * Adds the specified x,y,z vector components to this vector and returns the resulting vector. Does not change this
     * vector.
     */
    public Vec3 addVector(double x, double y, double z)
    {
        return new Vec3(this.xCoord + x, this.yCoord + y, this.zCoord + z);
    }

    /**
     * Euclidean distance between this and the specified vector, returned as double.
     */
    public double distanceTo(Vec3 vec)
    {
        double lvt_2_1_ = vec.xCoord - this.xCoord;
        double lvt_4_1_ = vec.yCoord - this.yCoord;
        double lvt_6_1_ = vec.zCoord - this.zCoord;
        return (double)MathHelper.sqrt_double(lvt_2_1_ * lvt_2_1_ + lvt_4_1_ * lvt_4_1_ + lvt_6_1_ * lvt_6_1_);
    }

    /**
     * The square of the Euclidean distance between this and the specified vector.
     */
    public double squareDistanceTo(Vec3 vec)
    {
        double lvt_2_1_ = vec.xCoord - this.xCoord;
        double lvt_4_1_ = vec.yCoord - this.yCoord;
        double lvt_6_1_ = vec.zCoord - this.zCoord;
        return lvt_2_1_ * lvt_2_1_ + lvt_4_1_ * lvt_4_1_ + lvt_6_1_ * lvt_6_1_;
    }

    /**
     * Returns the length of the vector.
     */
    public double lengthVector()
    {
        return (double)MathHelper.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
    }

    /**
     * Returns a new vector with x value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Vec3 getIntermediateWithXValue(Vec3 vec, double x)
    {
        double lvt_4_1_ = vec.xCoord - this.xCoord;
        double lvt_6_1_ = vec.yCoord - this.yCoord;
        double lvt_8_1_ = vec.zCoord - this.zCoord;

        if (lvt_4_1_ * lvt_4_1_ < 1.0000000116860974E-7D)
        {
            return null;
        }
        else
        {
            double lvt_10_1_ = (x - this.xCoord) / lvt_4_1_;
            return lvt_10_1_ >= 0.0D && lvt_10_1_ <= 1.0D ? new Vec3(this.xCoord + lvt_4_1_ * lvt_10_1_, this.yCoord + lvt_6_1_ * lvt_10_1_, this.zCoord + lvt_8_1_ * lvt_10_1_) : null;
        }
    }

    /**
     * Returns a new vector with y value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Vec3 getIntermediateWithYValue(Vec3 vec, double y)
    {
        double lvt_4_1_ = vec.xCoord - this.xCoord;
        double lvt_6_1_ = vec.yCoord - this.yCoord;
        double lvt_8_1_ = vec.zCoord - this.zCoord;

        if (lvt_6_1_ * lvt_6_1_ < 1.0000000116860974E-7D)
        {
            return null;
        }
        else
        {
            double lvt_10_1_ = (y - this.yCoord) / lvt_6_1_;
            return lvt_10_1_ >= 0.0D && lvt_10_1_ <= 1.0D ? new Vec3(this.xCoord + lvt_4_1_ * lvt_10_1_, this.yCoord + lvt_6_1_ * lvt_10_1_, this.zCoord + lvt_8_1_ * lvt_10_1_) : null;
        }
    }

    /**
     * Returns a new vector with z value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Vec3 getIntermediateWithZValue(Vec3 vec, double z)
    {
        double lvt_4_1_ = vec.xCoord - this.xCoord;
        double lvt_6_1_ = vec.yCoord - this.yCoord;
        double lvt_8_1_ = vec.zCoord - this.zCoord;

        if (lvt_8_1_ * lvt_8_1_ < 1.0000000116860974E-7D)
        {
            return null;
        }
        else
        {
            double lvt_10_1_ = (z - this.zCoord) / lvt_8_1_;
            return lvt_10_1_ >= 0.0D && lvt_10_1_ <= 1.0D ? new Vec3(this.xCoord + lvt_4_1_ * lvt_10_1_, this.yCoord + lvt_6_1_ * lvt_10_1_, this.zCoord + lvt_8_1_ * lvt_10_1_) : null;
        }
    }

    public String toString()
    {
        return "(" + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + ")";
    }

    public Vec3 rotatePitch(float pitch)
    {
        float lvt_2_1_ = MathHelper.cos(pitch);
        float lvt_3_1_ = MathHelper.sin(pitch);
        double lvt_4_1_ = this.xCoord;
        double lvt_6_1_ = this.yCoord * (double)lvt_2_1_ + this.zCoord * (double)lvt_3_1_;
        double lvt_8_1_ = this.zCoord * (double)lvt_2_1_ - this.yCoord * (double)lvt_3_1_;
        return new Vec3(lvt_4_1_, lvt_6_1_, lvt_8_1_);
    }

    public Vec3 rotateYaw(float yaw)
    {
        float lvt_2_1_ = MathHelper.cos(yaw);
        float lvt_3_1_ = MathHelper.sin(yaw);
        double lvt_4_1_ = this.xCoord * (double)lvt_2_1_ + this.zCoord * (double)lvt_3_1_;
        double lvt_6_1_ = this.yCoord;
        double lvt_8_1_ = this.zCoord * (double)lvt_2_1_ - this.xCoord * (double)lvt_3_1_;
        return new Vec3(lvt_4_1_, lvt_6_1_, lvt_8_1_);
    }
}
