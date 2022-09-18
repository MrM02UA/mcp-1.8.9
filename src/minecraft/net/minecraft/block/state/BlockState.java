package net.minecraft.block.state;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.Cartesian;
import net.minecraft.util.MapPopulator;

public class BlockState
{
    private static final Joiner COMMA_JOINER = Joiner.on(", ");
    private static final Function<IProperty, String> GET_NAME_FUNC = new Function<IProperty, String>()
    {
        public String apply(IProperty p_apply_1_)
        {
            return p_apply_1_ == null ? "<NULL>" : p_apply_1_.getName();
        }
        public Object apply(Object p_apply_1_)
        {
            return this.apply((IProperty)p_apply_1_);
        }
    };
    private final Block block;
    private final ImmutableList<IProperty> properties;
    private final ImmutableList<IBlockState> validStates;

    public BlockState(Block blockIn, IProperty... properties)
    {
        this.block = blockIn;
        Arrays.sort(properties, new Comparator<IProperty>()
        {
            public int compare(IProperty p_compare_1_, IProperty p_compare_2_)
            {
                return p_compare_1_.getName().compareTo(p_compare_2_.getName());
            }
            public int compare(Object p_compare_1_, Object p_compare_2_)
            {
                return this.compare((IProperty)p_compare_1_, (IProperty)p_compare_2_);
            }
        });
        this.properties = ImmutableList.copyOf(properties);
        Map<Map<IProperty, Comparable>, BlockState.StateImplementation> lvt_3_1_ = Maps.newLinkedHashMap();
        List<BlockState.StateImplementation> lvt_4_1_ = Lists.newArrayList();

        for (List<Comparable> lvt_7_1_ : Cartesian.cartesianProduct(this.getAllowedValues()))
        {
            Map<IProperty, Comparable> lvt_8_1_ = MapPopulator.<IProperty, Comparable>createMap(this.properties, lvt_7_1_);
            BlockState.StateImplementation lvt_9_1_ = new BlockState.StateImplementation(blockIn, ImmutableMap.copyOf(lvt_8_1_));
            lvt_3_1_.put(lvt_8_1_, lvt_9_1_);
            lvt_4_1_.add(lvt_9_1_);
        }

        for (BlockState.StateImplementation lvt_7_2_ : lvt_4_1_)
        {
            lvt_7_2_.buildPropertyValueTable(lvt_3_1_);
        }

        this.validStates = ImmutableList.copyOf(lvt_4_1_);
    }

    public ImmutableList<IBlockState> getValidStates()
    {
        return this.validStates;
    }

    private List<Iterable<Comparable>> getAllowedValues()
    {
        List<Iterable<Comparable>> lvt_1_1_ = Lists.newArrayList();

        for (int lvt_2_1_ = 0; lvt_2_1_ < this.properties.size(); ++lvt_2_1_)
        {
            lvt_1_1_.add(((IProperty)this.properties.get(lvt_2_1_)).getAllowedValues());
        }

        return lvt_1_1_;
    }

    public IBlockState getBaseState()
    {
        return (IBlockState)this.validStates.get(0);
    }

    public Block getBlock()
    {
        return this.block;
    }

    public Collection<IProperty> getProperties()
    {
        return this.properties;
    }

    public String toString()
    {
        return Objects.toStringHelper(this).add("block", Block.blockRegistry.getNameForObject(this.block)).add("properties", Iterables.transform(this.properties, GET_NAME_FUNC)).toString();
    }

    static class StateImplementation extends BlockStateBase
    {
        private final Block block;
        private final ImmutableMap<IProperty, Comparable> properties;
        private ImmutableTable<IProperty, Comparable, IBlockState> propertyValueTable;

        private StateImplementation(Block blockIn, ImmutableMap<IProperty, Comparable> propertiesIn)
        {
            this.block = blockIn;
            this.properties = propertiesIn;
        }

        public Collection<IProperty> getPropertyNames()
        {
            return Collections.unmodifiableCollection(this.properties.keySet());
        }

        public <T extends Comparable<T>> T getValue(IProperty<T> property)
        {
            if (!this.properties.containsKey(property))
            {
                throw new IllegalArgumentException("Cannot get property " + property + " as it does not exist in " + this.block.getBlockState());
            }
            else
            {
                return (T)((Comparable)property.getValueClass().cast(this.properties.get(property)));
            }
        }

        public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value)
        {
            if (!this.properties.containsKey(property))
            {
                throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + this.block.getBlockState());
            }
            else if (!property.getAllowedValues().contains(value))
            {
                throw new IllegalArgumentException("Cannot set property " + property + " to " + value + " on block " + Block.blockRegistry.getNameForObject(this.block) + ", it is not an allowed value");
            }
            else
            {
                return (IBlockState)(this.properties.get(property) == value ? this : (IBlockState)this.propertyValueTable.get(property, value));
            }
        }

        public ImmutableMap<IProperty, Comparable> getProperties()
        {
            return this.properties;
        }

        public Block getBlock()
        {
            return this.block;
        }

        public boolean equals(Object p_equals_1_)
        {
            return this == p_equals_1_;
        }

        public int hashCode()
        {
            return this.properties.hashCode();
        }

        public void buildPropertyValueTable(Map<Map<IProperty, Comparable>, BlockState.StateImplementation> map)
        {
            if (this.propertyValueTable != null)
            {
                throw new IllegalStateException();
            }
            else
            {
                Table<IProperty, Comparable, IBlockState> lvt_2_1_ = HashBasedTable.create();

                for (IProperty <? extends Comparable > lvt_4_1_ : this.properties.keySet())
                {
                    for (Comparable lvt_6_1_ : lvt_4_1_.getAllowedValues())
                    {
                        if (lvt_6_1_ != this.properties.get(lvt_4_1_))
                        {
                            lvt_2_1_.put(lvt_4_1_, lvt_6_1_, map.get(this.getPropertiesWithValue(lvt_4_1_, lvt_6_1_)));
                        }
                    }
                }

                this.propertyValueTable = ImmutableTable.copyOf(lvt_2_1_);
            }
        }

        private Map<IProperty, Comparable> getPropertiesWithValue(IProperty property, Comparable value)
        {
            Map<IProperty, Comparable> lvt_3_1_ = Maps.newHashMap(this.properties);
            lvt_3_1_.put(property, value);
            return lvt_3_1_;
        }
    }
}
