package net.minecraft.block.properties;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import net.minecraft.util.IStringSerializable;

public class PropertyEnum<T extends Enum<T> & IStringSerializable> extends PropertyHelper<T>
{
    private final ImmutableSet<T> allowedValues;
    private final Map<String, T> nameToValue = Maps.newHashMap();

    protected PropertyEnum(String name, Class<T> valueClass, Collection<T> allowedValues)
    {
        super(name, valueClass);
        this.allowedValues = ImmutableSet.copyOf(allowedValues);

        for (T lvt_5_1_ : allowedValues)
        {
            String lvt_6_1_ = ((IStringSerializable)lvt_5_1_).getName();

            if (this.nameToValue.containsKey(lvt_6_1_))
            {
                throw new IllegalArgumentException("Multiple values have the same name \'" + lvt_6_1_ + "\'");
            }

            this.nameToValue.put(lvt_6_1_, lvt_5_1_);
        }
    }

    public Collection<T> getAllowedValues()
    {
        return this.allowedValues;
    }

    /**
     * Get the name for the given value.
     */
    public String getName(T value)
    {
        return ((IStringSerializable)value).getName();
    }

    public static <T extends Enum<T> & IStringSerializable> PropertyEnum<T> create(String name, Class<T> clazz)
    {
        return create(name, clazz, Predicates.alwaysTrue());
    }

    public static <T extends Enum<T> & IStringSerializable> PropertyEnum<T> create(String name, Class<T> clazz, Predicate<T> filter)
    {
        return create(name, clazz, Collections2.filter(Lists.newArrayList(clazz.getEnumConstants()), filter));
    }

    public static <T extends Enum<T> & IStringSerializable> PropertyEnum<T> create(String name, Class<T> clazz, T... values)
    {
        return create(name, clazz, Lists.newArrayList(values));
    }

    public static <T extends Enum<T> & IStringSerializable> PropertyEnum<T> create(String name, Class<T> clazz, Collection<T> values)
    {
        return new PropertyEnum(name, clazz, values);
    }

    /**
     * Get the name for the given value.
     */
    public String getName(Comparable value)
    {
        return this.getName((Enum)value);
    }
}
