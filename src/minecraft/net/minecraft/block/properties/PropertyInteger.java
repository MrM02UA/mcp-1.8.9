package net.minecraft.block.properties;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;

public class PropertyInteger extends PropertyHelper<Integer>
{
    private final ImmutableSet<Integer> allowedValues;

    protected PropertyInteger(String name, int min, int max)
    {
        super(name, Integer.class);

        if (min < 0)
        {
            throw new IllegalArgumentException("Min value of " + name + " must be 0 or greater");
        }
        else if (max <= min)
        {
            throw new IllegalArgumentException("Max value of " + name + " must be greater than min (" + min + ")");
        }
        else
        {
            Set<Integer> lvt_4_1_ = Sets.newHashSet();

            for (int lvt_5_1_ = min; lvt_5_1_ <= max; ++lvt_5_1_)
            {
                lvt_4_1_.add(Integer.valueOf(lvt_5_1_));
            }

            this.allowedValues = ImmutableSet.copyOf(lvt_4_1_);
        }
    }

    public Collection<Integer> getAllowedValues()
    {
        return this.allowedValues;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass())
        {
            if (!super.equals(p_equals_1_))
            {
                return false;
            }
            else
            {
                PropertyInteger lvt_2_1_ = (PropertyInteger)p_equals_1_;
                return this.allowedValues.equals(lvt_2_1_.allowedValues);
            }
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        int lvt_1_1_ = super.hashCode();
        lvt_1_1_ = 31 * lvt_1_1_ + this.allowedValues.hashCode();
        return lvt_1_1_;
    }

    public static PropertyInteger create(String name, int min, int max)
    {
        return new PropertyInteger(name, min, max);
    }

    /**
     * Get the name for the given value.
     */
    public String getName(Integer value)
    {
        return value.toString();
    }

    /**
     * Get the name for the given value.
     */
    public String getName(Comparable value)
    {
        return this.getName((Integer)value);
    }
}
