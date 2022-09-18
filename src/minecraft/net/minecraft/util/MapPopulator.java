package net.minecraft.util;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class MapPopulator
{
    public static <K, V> Map<K, V> createMap(Iterable<K> keys, Iterable<V> values)
    {
        return populateMap(keys, values, Maps.newLinkedHashMap());
    }

    public static <K, V> Map<K, V> populateMap(Iterable<K> keys, Iterable<V> values, Map<K, V> map)
    {
        Iterator<V> lvt_3_1_ = values.iterator();

        for (K lvt_5_1_ : keys)
        {
            map.put(lvt_5_1_, lvt_3_1_.next());
        }

        if (lvt_3_1_.hasNext())
        {
            throw new NoSuchElementException();
        }
        else
        {
            return map;
        }
    }
}
