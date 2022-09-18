package net.minecraft.world.gen.layer;

import com.google.common.collect.Lists;
import java.util.List;

public class IntCache
{
    private static int intCacheSize = 256;
    private static List<int[]> freeSmallArrays = Lists.newArrayList();
    private static List<int[]> inUseSmallArrays = Lists.newArrayList();
    private static List<int[]> freeLargeArrays = Lists.newArrayList();
    private static List<int[]> inUseLargeArrays = Lists.newArrayList();

    public static synchronized int[] getIntCache(int p_76445_0_)
    {
        if (p_76445_0_ <= 256)
        {
            if (freeSmallArrays.isEmpty())
            {
                int[] lvt_1_1_ = new int[256];
                inUseSmallArrays.add(lvt_1_1_);
                return lvt_1_1_;
            }
            else
            {
                int[] lvt_1_2_ = (int[])freeSmallArrays.remove(freeSmallArrays.size() - 1);
                inUseSmallArrays.add(lvt_1_2_);
                return lvt_1_2_;
            }
        }
        else if (p_76445_0_ > intCacheSize)
        {
            intCacheSize = p_76445_0_;
            freeLargeArrays.clear();
            inUseLargeArrays.clear();
            int[] lvt_1_3_ = new int[intCacheSize];
            inUseLargeArrays.add(lvt_1_3_);
            return lvt_1_3_;
        }
        else if (freeLargeArrays.isEmpty())
        {
            int[] lvt_1_4_ = new int[intCacheSize];
            inUseLargeArrays.add(lvt_1_4_);
            return lvt_1_4_;
        }
        else
        {
            int[] lvt_1_5_ = (int[])freeLargeArrays.remove(freeLargeArrays.size() - 1);
            inUseLargeArrays.add(lvt_1_5_);
            return lvt_1_5_;
        }
    }

    /**
     * Mark all pre-allocated arrays as available for re-use by moving them to the appropriate free lists.
     */
    public static synchronized void resetIntCache()
    {
        if (!freeLargeArrays.isEmpty())
        {
            freeLargeArrays.remove(freeLargeArrays.size() - 1);
        }

        if (!freeSmallArrays.isEmpty())
        {
            freeSmallArrays.remove(freeSmallArrays.size() - 1);
        }

        freeLargeArrays.addAll(inUseLargeArrays);
        freeSmallArrays.addAll(inUseSmallArrays);
        inUseLargeArrays.clear();
        inUseSmallArrays.clear();
    }

    /**
     * Gets a human-readable string that indicates the sizes of all the cache fields.  Basically a synchronized static
     * toString.
     */
    public static synchronized String getCacheSizes()
    {
        return "cache: " + freeLargeArrays.size() + ", tcache: " + freeSmallArrays.size() + ", allocated: " + inUseLargeArrays.size() + ", tallocated: " + inUseSmallArrays.size();
    }
}
