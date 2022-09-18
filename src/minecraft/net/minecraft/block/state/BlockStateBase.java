package net.minecraft.block.state;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;

public abstract class BlockStateBase implements IBlockState
{
    private static final Joiner COMMA_JOINER = Joiner.on(',');
    private static final Function<Entry<IProperty, Comparable>, String> MAP_ENTRY_TO_STRING = new Function<Entry<IProperty, Comparable>, String>()
    {
        public String apply(Entry<IProperty, Comparable> p_apply_1_)
        {
            if (p_apply_1_ == null)
            {
                return "<NULL>";
            }
            else
            {
                IProperty lvt_2_1_ = (IProperty)p_apply_1_.getKey();
                return lvt_2_1_.getName() + "=" + lvt_2_1_.getName((Comparable)p_apply_1_.getValue());
            }
        }
        public Object apply(Object p_apply_1_)
        {
            return this.apply((Entry)p_apply_1_);
        }
    };

    public <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> property)
    {
        return this.withProperty(property, cyclePropertyValue(property.getAllowedValues(), this.getValue(property)));
    }

    protected static <T> T cyclePropertyValue(Collection<T> values, T currentValue)
    {
        Iterator<T> lvt_2_1_ = values.iterator();

        while (lvt_2_1_.hasNext())
        {
            if (lvt_2_1_.next().equals(currentValue))
            {
                if (lvt_2_1_.hasNext())
                {
                    return (T)lvt_2_1_.next();
                }

                return (T)values.iterator().next();
            }
        }

        return (T)lvt_2_1_.next();
    }

    public String toString()
    {
        StringBuilder lvt_1_1_ = new StringBuilder();
        lvt_1_1_.append(Block.blockRegistry.getNameForObject(this.getBlock()));

        if (!this.getProperties().isEmpty())
        {
            lvt_1_1_.append("[");
            COMMA_JOINER.appendTo(lvt_1_1_, Iterables.transform(this.getProperties().entrySet(), MAP_ENTRY_TO_STRING));
            lvt_1_1_.append("]");
        }

        return lvt_1_1_.toString();
    }
}
