package net.minecraft.util;

import java.util.Random;
import java.util.UUID;

public class MathHelper
{
    public static final float SQRT_2 = sqrt_float(2.0F);

    /**
     * A table of sin values computed from 0 (inclusive) to 2*pi (exclusive), with steps of 2*PI / 65536.
     */
    private static final float[] SIN_TABLE = new float[65536];

    /**
     * Though it looks like an array, this is really more like a mapping.  Key (index of this array) is the upper 5 bits
     * of the result of multiplying a 32-bit unsigned integer by the B(2, 5) De Bruijn sequence 0x077CB531.  Value
     * (value stored in the array) is the unique index (from the right) of the leftmost one-bit in a 32-bit unsigned
     * integer that can cause the upper 5 bits to get that value.  Used for highly optimized "find the log-base-2 of
     * this number" calculations.
     */
    private static final int[] multiplyDeBruijnBitPosition;
    private static final double field_181163_d;
    private static final double[] field_181164_e;
    private static final double[] field_181165_f;

    /**
     * sin looked up in a table
     */
    public static float sin(float p_76126_0_)
    {
        return SIN_TABLE[(int)(p_76126_0_ * 10430.378F) & 65535];
    }

    /**
     * cos looked up in the sin table with the appropriate offset
     */
    public static float cos(float value)
    {
        return SIN_TABLE[(int)(value * 10430.378F + 16384.0F) & 65535];
    }

    public static float sqrt_float(float value)
    {
        return (float)Math.sqrt((double)value);
    }

    public static float sqrt_double(double value)
    {
        return (float)Math.sqrt(value);
    }

    /**
     * Returns the greatest integer less than or equal to the float argument
     */
    public static int floor_float(float value)
    {
        int lvt_1_1_ = (int)value;
        return value < (float)lvt_1_1_ ? lvt_1_1_ - 1 : lvt_1_1_;
    }

    /**
     * returns par0 cast as an int, and no greater than Integer.MAX_VALUE-1024
     */
    public static int truncateDoubleToInt(double value)
    {
        return (int)(value + 1024.0D) - 1024;
    }

    /**
     * Returns the greatest integer less than or equal to the double argument
     */
    public static int floor_double(double value)
    {
        int lvt_2_1_ = (int)value;
        return value < (double)lvt_2_1_ ? lvt_2_1_ - 1 : lvt_2_1_;
    }

    /**
     * Long version of floor_double
     */
    public static long floor_double_long(double value)
    {
        long lvt_2_1_ = (long)value;
        return value < (double)lvt_2_1_ ? lvt_2_1_ - 1L : lvt_2_1_;
    }

    public static int func_154353_e(double value)
    {
        return (int)(value >= 0.0D ? value : -value + 1.0D);
    }

    public static float abs(float value)
    {
        return value >= 0.0F ? value : -value;
    }

    /**
     * Returns the unsigned value of an int.
     */
    public static int abs_int(int value)
    {
        return value >= 0 ? value : -value;
    }

    public static int ceiling_float_int(float value)
    {
        int lvt_1_1_ = (int)value;
        return value > (float)lvt_1_1_ ? lvt_1_1_ + 1 : lvt_1_1_;
    }

    public static int ceiling_double_int(double value)
    {
        int lvt_2_1_ = (int)value;
        return value > (double)lvt_2_1_ ? lvt_2_1_ + 1 : lvt_2_1_;
    }

    /**
     * Returns the value of the first parameter, clamped to be within the lower and upper limits given by the second and
     * third parameters.
     */
    public static int clamp_int(int num, int min, int max)
    {
        return num < min ? min : (num > max ? max : num);
    }

    /**
     * Returns the value of the first parameter, clamped to be within the lower and upper limits given by the second and
     * third parameters
     */
    public static float clamp_float(float num, float min, float max)
    {
        return num < min ? min : (num > max ? max : num);
    }

    public static double clamp_double(double num, double min, double max)
    {
        return num < min ? min : (num > max ? max : num);
    }

    public static double denormalizeClamp(double lowerBnd, double upperBnd, double slide)
    {
        return slide < 0.0D ? lowerBnd : (slide > 1.0D ? upperBnd : lowerBnd + (upperBnd - lowerBnd) * slide);
    }

    /**
     * Maximum of the absolute value of two numbers.
     */
    public static double abs_max(double p_76132_0_, double p_76132_2_)
    {
        if (p_76132_0_ < 0.0D)
        {
            p_76132_0_ = -p_76132_0_;
        }

        if (p_76132_2_ < 0.0D)
        {
            p_76132_2_ = -p_76132_2_;
        }

        return p_76132_0_ > p_76132_2_ ? p_76132_0_ : p_76132_2_;
    }

    /**
     * Buckets an integer with specifed bucket sizes.  Args: i, bucketSize
     */
    public static int bucketInt(int p_76137_0_, int p_76137_1_)
    {
        return p_76137_0_ < 0 ? -((-p_76137_0_ - 1) / p_76137_1_) - 1 : p_76137_0_ / p_76137_1_;
    }

    public static int getRandomIntegerInRange(Random p_76136_0_, int p_76136_1_, int p_76136_2_)
    {
        return p_76136_1_ >= p_76136_2_ ? p_76136_1_ : p_76136_0_.nextInt(p_76136_2_ - p_76136_1_ + 1) + p_76136_1_;
    }

    public static float randomFloatClamp(Random p_151240_0_, float p_151240_1_, float p_151240_2_)
    {
        return p_151240_1_ >= p_151240_2_ ? p_151240_1_ : p_151240_0_.nextFloat() * (p_151240_2_ - p_151240_1_) + p_151240_1_;
    }

    public static double getRandomDoubleInRange(Random p_82716_0_, double p_82716_1_, double p_82716_3_)
    {
        return p_82716_1_ >= p_82716_3_ ? p_82716_1_ : p_82716_0_.nextDouble() * (p_82716_3_ - p_82716_1_) + p_82716_1_;
    }

    public static double average(long[] values)
    {
        long lvt_1_1_ = 0L;

        for (long lvt_6_1_ : values)
        {
            lvt_1_1_ += lvt_6_1_;
        }

        return (double)lvt_1_1_ / (double)values.length;
    }

    public static boolean epsilonEquals(float p_180185_0_, float p_180185_1_)
    {
        return abs(p_180185_1_ - p_180185_0_) < 1.0E-5F;
    }

    public static int normalizeAngle(int p_180184_0_, int p_180184_1_)
    {
        return (p_180184_0_ % p_180184_1_ + p_180184_1_) % p_180184_1_;
    }

    /**
     * the angle is reduced to an angle between -180 and +180 by mod, and a 360 check
     */
    public static float wrapAngleTo180_float(float value)
    {
        value = value % 360.0F;

        if (value >= 180.0F)
        {
            value -= 360.0F;
        }

        if (value < -180.0F)
        {
            value += 360.0F;
        }

        return value;
    }

    /**
     * the angle is reduced to an angle between -180 and +180 by mod, and a 360 check
     */
    public static double wrapAngleTo180_double(double value)
    {
        value = value % 360.0D;

        if (value >= 180.0D)
        {
            value -= 360.0D;
        }

        if (value < -180.0D)
        {
            value += 360.0D;
        }

        return value;
    }

    /**
     * parses the string as integer or returns the second parameter if it fails
     */
    public static int parseIntWithDefault(String p_82715_0_, int p_82715_1_)
    {
        try
        {
            return Integer.parseInt(p_82715_0_);
        }
        catch (Throwable var3)
        {
            return p_82715_1_;
        }
    }

    /**
     * parses the string as integer or returns the second parameter if it fails. this value is capped to par2
     */
    public static int parseIntWithDefaultAndMax(String p_82714_0_, int p_82714_1_, int p_82714_2_)
    {
        return Math.max(p_82714_2_, parseIntWithDefault(p_82714_0_, p_82714_1_));
    }

    /**
     * parses the string as double or returns the second parameter if it fails.
     */
    public static double parseDoubleWithDefault(String p_82712_0_, double p_82712_1_)
    {
        try
        {
            return Double.parseDouble(p_82712_0_);
        }
        catch (Throwable var4)
        {
            return p_82712_1_;
        }
    }

    public static double parseDoubleWithDefaultAndMax(String p_82713_0_, double p_82713_1_, double p_82713_3_)
    {
        return Math.max(p_82713_3_, parseDoubleWithDefault(p_82713_0_, p_82713_1_));
    }

    /**
     * Returns the input value rounded up to the next highest power of two.
     */
    public static int roundUpToPowerOfTwo(int value)
    {
        int lvt_1_1_ = value - 1;
        lvt_1_1_ = lvt_1_1_ | lvt_1_1_ >> 1;
        lvt_1_1_ = lvt_1_1_ | lvt_1_1_ >> 2;
        lvt_1_1_ = lvt_1_1_ | lvt_1_1_ >> 4;
        lvt_1_1_ = lvt_1_1_ | lvt_1_1_ >> 8;
        lvt_1_1_ = lvt_1_1_ | lvt_1_1_ >> 16;
        return lvt_1_1_ + 1;
    }

    /**
     * Is the given value a power of two?  (1, 2, 4, 8, 16, ...)
     */
    private static boolean isPowerOfTwo(int value)
    {
        return value != 0 && (value & value - 1) == 0;
    }

    /**
     * Uses a B(2, 5) De Bruijn sequence and a lookup table to efficiently calculate the log-base-two of the given
     * value.  Optimized for cases where the input value is a power-of-two.  If the input value is not a power-of-two,
     * then subtract 1 from the return value.
     */
    private static int calculateLogBaseTwoDeBruijn(int value)
    {
        value = isPowerOfTwo(value) ? value : roundUpToPowerOfTwo(value);
        return multiplyDeBruijnBitPosition[(int)((long)value * 125613361L >> 27) & 31];
    }

    /**
     * Efficiently calculates the floor of the base-2 log of an integer value.  This is effectively the index of the
     * highest bit that is set.  For example, if the number in binary is 0...100101, this will return 5.
     */
    public static int calculateLogBaseTwo(int value)
    {
        return calculateLogBaseTwoDeBruijn(value) - (isPowerOfTwo(value) ? 0 : 1);
    }

    public static int roundUp(int p_154354_0_, int p_154354_1_)
    {
        if (p_154354_1_ == 0)
        {
            return 0;
        }
        else if (p_154354_0_ == 0)
        {
            return p_154354_1_;
        }
        else
        {
            if (p_154354_0_ < 0)
            {
                p_154354_1_ *= -1;
            }

            int lvt_2_1_ = p_154354_0_ % p_154354_1_;
            return lvt_2_1_ == 0 ? p_154354_0_ : p_154354_0_ + p_154354_1_ - lvt_2_1_;
        }
    }

    public static int func_180183_b(float p_180183_0_, float p_180183_1_, float p_180183_2_)
    {
        return func_180181_b(floor_float(p_180183_0_ * 255.0F), floor_float(p_180183_1_ * 255.0F), floor_float(p_180183_2_ * 255.0F));
    }

    public static int func_180181_b(int p_180181_0_, int p_180181_1_, int p_180181_2_)
    {
        int lvt_3_1_ = (p_180181_0_ << 8) + p_180181_1_;
        lvt_3_1_ = (lvt_3_1_ << 8) + p_180181_2_;
        return lvt_3_1_;
    }

    public static int func_180188_d(int p_180188_0_, int p_180188_1_)
    {
        int lvt_2_1_ = (p_180188_0_ & 16711680) >> 16;
        int lvt_3_1_ = (p_180188_1_ & 16711680) >> 16;
        int lvt_4_1_ = (p_180188_0_ & 65280) >> 8;
        int lvt_5_1_ = (p_180188_1_ & 65280) >> 8;
        int lvt_6_1_ = (p_180188_0_ & 255) >> 0;
        int lvt_7_1_ = (p_180188_1_ & 255) >> 0;
        int lvt_8_1_ = (int)((float)lvt_2_1_ * (float)lvt_3_1_ / 255.0F);
        int lvt_9_1_ = (int)((float)lvt_4_1_ * (float)lvt_5_1_ / 255.0F);
        int lvt_10_1_ = (int)((float)lvt_6_1_ * (float)lvt_7_1_ / 255.0F);
        return p_180188_0_ & -16777216 | lvt_8_1_ << 16 | lvt_9_1_ << 8 | lvt_10_1_;
    }

    public static double func_181162_h(double p_181162_0_)
    {
        return p_181162_0_ - Math.floor(p_181162_0_);
    }

    public static long getPositionRandom(Vec3i pos)
    {
        return getCoordinateRandom(pos.getX(), pos.getY(), pos.getZ());
    }

    public static long getCoordinateRandom(int x, int y, int z)
    {
        long lvt_3_1_ = (long)(x * 3129871) ^ (long)z * 116129781L ^ (long)y;
        lvt_3_1_ = lvt_3_1_ * lvt_3_1_ * 42317861L + lvt_3_1_ * 11L;
        return lvt_3_1_;
    }

    public static UUID getRandomUuid(Random rand)
    {
        long lvt_1_1_ = rand.nextLong() & -61441L | 16384L;
        long lvt_3_1_ = rand.nextLong() & 4611686018427387903L | Long.MIN_VALUE;
        return new UUID(lvt_1_1_, lvt_3_1_);
    }

    public static double func_181160_c(double p_181160_0_, double p_181160_2_, double p_181160_4_)
    {
        return (p_181160_0_ - p_181160_2_) / (p_181160_4_ - p_181160_2_);
    }

    public static double atan2(double p_181159_0_, double p_181159_2_)
    {
        double lvt_4_1_ = p_181159_2_ * p_181159_2_ + p_181159_0_ * p_181159_0_;

        if (Double.isNaN(lvt_4_1_))
        {
            return Double.NaN;
        }
        else
        {
            boolean lvt_6_1_ = p_181159_0_ < 0.0D;

            if (lvt_6_1_)
            {
                p_181159_0_ = -p_181159_0_;
            }

            boolean lvt_7_1_ = p_181159_2_ < 0.0D;

            if (lvt_7_1_)
            {
                p_181159_2_ = -p_181159_2_;
            }

            boolean lvt_8_1_ = p_181159_0_ > p_181159_2_;

            if (lvt_8_1_)
            {
                double lvt_9_1_ = p_181159_2_;
                p_181159_2_ = p_181159_0_;
                p_181159_0_ = lvt_9_1_;
            }

            double lvt_9_2_ = func_181161_i(lvt_4_1_);
            p_181159_2_ = p_181159_2_ * lvt_9_2_;
            p_181159_0_ = p_181159_0_ * lvt_9_2_;
            double lvt_11_1_ = field_181163_d + p_181159_0_;
            int lvt_13_1_ = (int)Double.doubleToRawLongBits(lvt_11_1_);
            double lvt_14_1_ = field_181164_e[lvt_13_1_];
            double lvt_16_1_ = field_181165_f[lvt_13_1_];
            double lvt_18_1_ = lvt_11_1_ - field_181163_d;
            double lvt_20_1_ = p_181159_0_ * lvt_16_1_ - p_181159_2_ * lvt_18_1_;
            double lvt_22_1_ = (6.0D + lvt_20_1_ * lvt_20_1_) * lvt_20_1_ * 0.16666666666666666D;
            double lvt_24_1_ = lvt_14_1_ + lvt_22_1_;

            if (lvt_8_1_)
            {
                lvt_24_1_ = (Math.PI / 2D) - lvt_24_1_;
            }

            if (lvt_7_1_)
            {
                lvt_24_1_ = Math.PI - lvt_24_1_;
            }

            if (lvt_6_1_)
            {
                lvt_24_1_ = -lvt_24_1_;
            }

            return lvt_24_1_;
        }
    }

    public static double func_181161_i(double p_181161_0_)
    {
        double lvt_2_1_ = 0.5D * p_181161_0_;
        long lvt_4_1_ = Double.doubleToRawLongBits(p_181161_0_);
        lvt_4_1_ = 6910469410427058090L - (lvt_4_1_ >> 1);
        p_181161_0_ = Double.longBitsToDouble(lvt_4_1_);
        p_181161_0_ = p_181161_0_ * (1.5D - lvt_2_1_ * p_181161_0_ * p_181161_0_);
        return p_181161_0_;
    }

    public static int hsvToRGB(float p_181758_0_, float p_181758_1_, float p_181758_2_)
    {
        int lvt_3_1_ = (int)(p_181758_0_ * 6.0F) % 6;
        float lvt_4_1_ = p_181758_0_ * 6.0F - (float)lvt_3_1_;
        float lvt_5_1_ = p_181758_2_ * (1.0F - p_181758_1_);
        float lvt_6_1_ = p_181758_2_ * (1.0F - lvt_4_1_ * p_181758_1_);
        float lvt_7_1_ = p_181758_2_ * (1.0F - (1.0F - lvt_4_1_) * p_181758_1_);
        float lvt_8_1_;
        float lvt_9_1_;
        float lvt_10_1_;

        switch (lvt_3_1_)
        {
            case 0:
                lvt_8_1_ = p_181758_2_;
                lvt_9_1_ = lvt_7_1_;
                lvt_10_1_ = lvt_5_1_;
                break;

            case 1:
                lvt_8_1_ = lvt_6_1_;
                lvt_9_1_ = p_181758_2_;
                lvt_10_1_ = lvt_5_1_;
                break;

            case 2:
                lvt_8_1_ = lvt_5_1_;
                lvt_9_1_ = p_181758_2_;
                lvt_10_1_ = lvt_7_1_;
                break;

            case 3:
                lvt_8_1_ = lvt_5_1_;
                lvt_9_1_ = lvt_6_1_;
                lvt_10_1_ = p_181758_2_;
                break;

            case 4:
                lvt_8_1_ = lvt_7_1_;
                lvt_9_1_ = lvt_5_1_;
                lvt_10_1_ = p_181758_2_;
                break;

            case 5:
                lvt_8_1_ = p_181758_2_;
                lvt_9_1_ = lvt_5_1_;
                lvt_10_1_ = lvt_6_1_;
                break;

            default:
                throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + p_181758_0_ + ", " + p_181758_1_ + ", " + p_181758_2_);
        }

        int lvt_11_1_ = clamp_int((int)(lvt_8_1_ * 255.0F), 0, 255);
        int lvt_12_1_ = clamp_int((int)(lvt_9_1_ * 255.0F), 0, 255);
        int lvt_13_1_ = clamp_int((int)(lvt_10_1_ * 255.0F), 0, 255);
        return lvt_11_1_ << 16 | lvt_12_1_ << 8 | lvt_13_1_;
    }

    static
    {
        for (int lvt_0_1_ = 0; lvt_0_1_ < 65536; ++lvt_0_1_)
        {
            SIN_TABLE[lvt_0_1_] = (float)Math.sin((double)lvt_0_1_ * Math.PI * 2.0D / 65536.0D);
        }

        multiplyDeBruijnBitPosition = new int[] {0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
        field_181163_d = Double.longBitsToDouble(4805340802404319232L);
        field_181164_e = new double[257];
        field_181165_f = new double[257];

        for (int lvt_0_2_ = 0; lvt_0_2_ < 257; ++lvt_0_2_)
        {
            double lvt_1_1_ = (double)lvt_0_2_ / 256.0D;
            double lvt_3_1_ = Math.asin(lvt_1_1_);
            field_181165_f[lvt_0_2_] = Math.cos(lvt_3_1_);
            field_181164_e[lvt_0_2_] = lvt_3_1_;
        }
    }
}
