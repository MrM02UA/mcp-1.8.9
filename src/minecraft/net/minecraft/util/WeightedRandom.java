package net.minecraft.util;

import java.util.Collection;
import java.util.Random;

public class WeightedRandom
{
    /**
     * Returns the total weight of all items in a collection.
     */
    public static int getTotalWeight(Collection <? extends WeightedRandom.Item > collection)
    {
        int lvt_1_1_ = 0;

        for (WeightedRandom.Item lvt_3_1_ : collection)
        {
            lvt_1_1_ += lvt_3_1_.itemWeight;
        }

        return lvt_1_1_;
    }

    public static <T extends WeightedRandom.Item> T getRandomItem(Random random, Collection<T> collection, int totalWeight)
    {
        if (totalWeight <= 0)
        {
            throw new IllegalArgumentException();
        }
        else
        {
            int lvt_3_1_ = random.nextInt(totalWeight);
            return getRandomItem(collection, lvt_3_1_);
        }
    }

    public static <T extends WeightedRandom.Item> T getRandomItem(Collection<T> collection, int weight)
    {
        for (T lvt_3_1_ : collection)
        {
            weight -= lvt_3_1_.itemWeight;

            if (weight < 0)
            {
                return lvt_3_1_;
            }
        }

        return (T)null;
    }

    public static <T extends WeightedRandom.Item> T getRandomItem(Random random, Collection<T> collection)
    {
        return getRandomItem(random, collection, getTotalWeight(collection));
    }

    public static class Item
    {
        protected int itemWeight;

        public Item(int itemWeightIn)
        {
            this.itemWeight = itemWeightIn;
        }
    }
}
