package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClassInheritanceMultiMap<T> extends AbstractSet<T>
{
    private static final Set < Class<? >> field_181158_a = Sets.newHashSet();
    private final Map < Class<?>, List<T >> map = Maps.newHashMap();
    private final Set < Class<? >> knownKeys = Sets.newIdentityHashSet();
    private final Class<T> baseClass;
    private final List<T> values = Lists.newArrayList();

    public ClassInheritanceMultiMap(Class<T> baseClassIn)
    {
        this.baseClass = baseClassIn;
        this.knownKeys.add(baseClassIn);
        this.map.put(baseClassIn, this.values);

        for (Class<?> lvt_3_1_ : field_181158_a)
        {
            this.createLookup(lvt_3_1_);
        }
    }

    protected void createLookup(Class<?> clazz)
    {
        field_181158_a.add(clazz);

        for (T lvt_3_1_ : this.values)
        {
            if (clazz.isAssignableFrom(lvt_3_1_.getClass()))
            {
                this.addForClass(lvt_3_1_, clazz);
            }
        }

        this.knownKeys.add(clazz);
    }

    protected Class<?> initializeClassLookup(Class<?> clazz)
    {
        if (this.baseClass.isAssignableFrom(clazz))
        {
            if (!this.knownKeys.contains(clazz))
            {
                this.createLookup(clazz);
            }

            return clazz;
        }
        else
        {
            throw new IllegalArgumentException("Don\'t know how to search for " + clazz);
        }
    }

    public boolean add(T p_add_1_)
    {
        for (Class<?> lvt_3_1_ : this.knownKeys)
        {
            if (lvt_3_1_.isAssignableFrom(p_add_1_.getClass()))
            {
                this.addForClass(p_add_1_, lvt_3_1_);
            }
        }

        return true;
    }

    private void addForClass(T value, Class<?> parentClass)
    {
        List<T> lvt_3_1_ = (List)this.map.get(parentClass);

        if (lvt_3_1_ == null)
        {
            this.map.put(parentClass, Lists.newArrayList(new Object[] {value}));
        }
        else
        {
            lvt_3_1_.add(value);
        }
    }

    public boolean remove(Object p_remove_1_)
    {
        T lvt_2_1_ = p_remove_1_;
        boolean lvt_3_1_ = false;

        for (Class<?> lvt_5_1_ : this.knownKeys)
        {
            if (lvt_5_1_.isAssignableFrom(lvt_2_1_.getClass()))
            {
                List<T> lvt_6_1_ = (List)this.map.get(lvt_5_1_);

                if (lvt_6_1_ != null && lvt_6_1_.remove(lvt_2_1_))
                {
                    lvt_3_1_ = true;
                }
            }
        }

        return lvt_3_1_;
    }

    public boolean contains(Object p_contains_1_)
    {
        return Iterators.contains(this.getByClass(p_contains_1_.getClass()).iterator(), p_contains_1_);
    }

    public <S> Iterable<S> getByClass(final Class<S> clazz)
    {
        return new Iterable<S>()
        {
            public Iterator<S> iterator()
            {
                List<T> lvt_1_1_ = (List)ClassInheritanceMultiMap.this.map.get(ClassInheritanceMultiMap.this.initializeClassLookup(clazz));

                if (lvt_1_1_ == null)
                {
                    return Iterators.emptyIterator();
                }
                else
                {
                    Iterator<T> lvt_2_1_ = lvt_1_1_.iterator();
                    return Iterators.filter(lvt_2_1_, clazz);
                }
            }
        };
    }

    public Iterator<T> iterator()
    {
        return this.values.isEmpty() ? Iterators.<T>emptyIterator() : Iterators.unmodifiableIterator(this.values.iterator());
    }

    public int size()
    {
        return this.values.size();
    }
}
