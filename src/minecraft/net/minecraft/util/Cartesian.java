package net.minecraft.util;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Cartesian
{
    public static <T> Iterable<T[]> cartesianProduct(Class<T> clazz, Iterable <? extends Iterable <? extends T >> sets)
    {
        return new Cartesian.Product(clazz, (Iterable[])toArray(Iterable.class, sets));
    }

    public static <T> Iterable<List<T>> cartesianProduct(Iterable <? extends Iterable <? extends T >> sets)
    {
        return arraysAsLists(cartesianProduct(Object.class, sets));
    }

    private static <T> Iterable<List<T>> arraysAsLists(Iterable<Object[]> arrays)
    {
        return Iterables.transform(arrays, new Cartesian.GetList());
    }

    private static <T> T[] toArray(Class <? super T > clazz, Iterable <? extends T > it)
    {
        List<T> lvt_2_1_ = Lists.newArrayList();

        for (T lvt_4_1_ : it)
        {
            lvt_2_1_.add(lvt_4_1_);
        }

        return (T[])((Object[])lvt_2_1_.toArray(createArray(clazz, lvt_2_1_.size())));
    }

    private static <T> T[] createArray(Class <? super T > p_179319_0_, int p_179319_1_)
    {
        return (T[])((Object[])((Object[])Array.newInstance(p_179319_0_, p_179319_1_)));
    }

    static class GetList<T> implements Function<Object[], List<T>>
    {
        private GetList()
        {
        }

        public List<T> apply(Object[] p_apply_1_)
        {
            return Arrays.asList((Object[])p_apply_1_);
        }

        public Object apply(Object p_apply_1_)
        {
            return this.apply((Object[])p_apply_1_);
        }
    }

    static class Product<T> implements Iterable<T[]>
    {
        private final Class<T> clazz;
        private final Iterable <? extends T > [] iterables;

        private Product(Class<T> clazz, Iterable <? extends T > [] iterables)
        {
            this.clazz = clazz;
            this.iterables = iterables;
        }

        public Iterator<T[]> iterator()
        {
            return (Iterator<T[]>)(this.iterables.length <= 0 ? Collections.singletonList((Object[])Cartesian.createArray(this.clazz, 0)).iterator() : new Cartesian.Product.ProductIterator(this.clazz, this.iterables));
        }

        static class ProductIterator<T> extends UnmodifiableIterator<T[]>
        {
            private int index;
            private final Iterable <? extends T > [] iterables;
            private final Iterator <? extends T > [] iterators;
            private final T[] results;

            private ProductIterator(Class<T> clazz, Iterable <? extends T > [] iterables)
            {
                this.index = -2;
                this.iterables = iterables;
                this.iterators = (Iterator[])Cartesian.createArray(Iterator.class, this.iterables.length);

                for (int lvt_3_1_ = 0; lvt_3_1_ < this.iterables.length; ++lvt_3_1_)
                {
                    this.iterators[lvt_3_1_] = iterables[lvt_3_1_].iterator();
                }

                this.results = Cartesian.createArray(clazz, this.iterators.length);
            }

            private void endOfData()
            {
                this.index = -1;
                Arrays.fill(this.iterators, (Object)null);
                Arrays.fill(this.results, (Object)null);
            }

            public boolean hasNext()
            {
                if (this.index == -2)
                {
                    this.index = 0;

                    for (Iterator <? extends T > lvt_4_1_ : this.iterators)
                    {
                        if (!lvt_4_1_.hasNext())
                        {
                            this.endOfData();
                            break;
                        }
                    }

                    return true;
                }
                else
                {
                    if (this.index >= this.iterators.length)
                    {
                        for (this.index = this.iterators.length - 1; this.index >= 0; --this.index)
                        {
                            Iterator <? extends T > lvt_1_2_ = this.iterators[this.index];

                            if (lvt_1_2_.hasNext())
                            {
                                break;
                            }

                            if (this.index == 0)
                            {
                                this.endOfData();
                                break;
                            }

                            lvt_1_2_ = this.iterables[this.index].iterator();
                            this.iterators[this.index] = lvt_1_2_;

                            if (!lvt_1_2_.hasNext())
                            {
                                this.endOfData();
                                break;
                            }
                        }
                    }

                    return this.index >= 0;
                }
            }

            public T[] next()
            {
                if (!this.hasNext())
                {
                    throw new NoSuchElementException();
                }
                else
                {
                    while (this.index < this.iterators.length)
                    {
                        this.results[this.index] = this.iterators[this.index].next();
                        ++this.index;
                    }

                    return (T[])((Object[])this.results.clone());
                }
            }

            public Object next()
            {
                return this.next();
            }
        }
    }
}
