package net.minecraft.util;

public class LongHashMap<V>
{
    private transient LongHashMap.Entry<V>[] hashArray = new LongHashMap.Entry[4096];

    /** the number of elements in the hash array */
    private transient int numHashElements;
    private int mask;

    /**
     * the maximum amount of elements in the hash (probably 3/4 the size due to meh hashing function)
     */
    private int capacity = 3072;

    /**
     * percent of the hasharray that can be used without hash colliding probably
     */
    private final float percentUseable = 0.75F;

    /** count of times elements have been added/removed */
    private transient volatile int modCount;

    public LongHashMap()
    {
        this.mask = this.hashArray.length - 1;
    }

    /**
     * returns the hashed key given the original key
     */
    private static int getHashedKey(long originalKey)
    {
        return hash((int)(originalKey ^ originalKey >>> 32));
    }

    /**
     * the hash function
     */
    private static int hash(int integer)
    {
        integer = integer ^ integer >>> 20 ^ integer >>> 12;
        return integer ^ integer >>> 7 ^ integer >>> 4;
    }

    /**
     * gets the index in the hash given the array length and the hashed key
     */
    private static int getHashIndex(int p_76158_0_, int p_76158_1_)
    {
        return p_76158_0_ & p_76158_1_;
    }

    public int getNumHashElements()
    {
        return this.numHashElements;
    }

    /**
     * get the value from the map given the key
     */
    public V getValueByKey(long p_76164_1_)
    {
        int lvt_3_1_ = getHashedKey(p_76164_1_);

        for (LongHashMap.Entry<V> lvt_4_1_ = this.hashArray[getHashIndex(lvt_3_1_, this.mask)]; lvt_4_1_ != null; lvt_4_1_ = lvt_4_1_.nextEntry)
        {
            if (lvt_4_1_.key == p_76164_1_)
            {
                return lvt_4_1_.value;
            }
        }

        return (V)null;
    }

    public boolean containsItem(long p_76161_1_)
    {
        return this.getEntry(p_76161_1_) != null;
    }

    final LongHashMap.Entry<V> getEntry(long p_76160_1_)
    {
        int lvt_3_1_ = getHashedKey(p_76160_1_);

        for (LongHashMap.Entry<V> lvt_4_1_ = this.hashArray[getHashIndex(lvt_3_1_, this.mask)]; lvt_4_1_ != null; lvt_4_1_ = lvt_4_1_.nextEntry)
        {
            if (lvt_4_1_.key == p_76160_1_)
            {
                return lvt_4_1_;
            }
        }

        return null;
    }

    /**
     * Add a key-value pair.
     */
    public void add(long p_76163_1_, V p_76163_3_)
    {
        int lvt_4_1_ = getHashedKey(p_76163_1_);
        int lvt_5_1_ = getHashIndex(lvt_4_1_, this.mask);

        for (LongHashMap.Entry<V> lvt_6_1_ = this.hashArray[lvt_5_1_]; lvt_6_1_ != null; lvt_6_1_ = lvt_6_1_.nextEntry)
        {
            if (lvt_6_1_.key == p_76163_1_)
            {
                lvt_6_1_.value = p_76163_3_;
                return;
            }
        }

        ++this.modCount;
        this.createKey(lvt_4_1_, p_76163_1_, p_76163_3_, lvt_5_1_);
    }

    /**
     * resizes the table
     */
    private void resizeTable(int p_76153_1_)
    {
        LongHashMap.Entry<V>[] lvt_2_1_ = this.hashArray;
        int lvt_3_1_ = lvt_2_1_.length;

        if (lvt_3_1_ == 1073741824)
        {
            this.capacity = Integer.MAX_VALUE;
        }
        else
        {
            LongHashMap.Entry<V>[] lvt_4_1_ = new LongHashMap.Entry[p_76153_1_];
            this.copyHashTableTo(lvt_4_1_);
            this.hashArray = lvt_4_1_;
            this.mask = this.hashArray.length - 1;
            this.capacity = (int)((float)p_76153_1_ * this.percentUseable);
        }
    }

    /**
     * copies the hash table to the specified array
     */
    private void copyHashTableTo(LongHashMap.Entry<V>[] p_76154_1_)
    {
        LongHashMap.Entry<V>[] lvt_2_1_ = this.hashArray;
        int lvt_3_1_ = p_76154_1_.length;

        for (int lvt_4_1_ = 0; lvt_4_1_ < lvt_2_1_.length; ++lvt_4_1_)
        {
            LongHashMap.Entry<V> lvt_5_1_ = lvt_2_1_[lvt_4_1_];

            if (lvt_5_1_ != null)
            {
                lvt_2_1_[lvt_4_1_] = null;

                while (true)
                {
                    LongHashMap.Entry<V> lvt_6_1_ = lvt_5_1_.nextEntry;
                    int lvt_7_1_ = getHashIndex(lvt_5_1_.hash, lvt_3_1_ - 1);
                    lvt_5_1_.nextEntry = p_76154_1_[lvt_7_1_];
                    p_76154_1_[lvt_7_1_] = lvt_5_1_;
                    lvt_5_1_ = lvt_6_1_;

                    if (lvt_6_1_ == null)
                    {
                        break;
                    }
                }
            }
        }
    }

    /**
     * calls the removeKey method and returns removed object
     */
    public V remove(long p_76159_1_)
    {
        LongHashMap.Entry<V> lvt_3_1_ = this.removeKey(p_76159_1_);
        return (V)(lvt_3_1_ == null ? null : lvt_3_1_.value);
    }

    final LongHashMap.Entry<V> removeKey(long p_76152_1_)
    {
        int lvt_3_1_ = getHashedKey(p_76152_1_);
        int lvt_4_1_ = getHashIndex(lvt_3_1_, this.mask);
        LongHashMap.Entry<V> lvt_5_1_ = this.hashArray[lvt_4_1_];
        LongHashMap.Entry<V> lvt_6_1_;
        LongHashMap.Entry<V> lvt_7_1_;

        for (lvt_6_1_ = lvt_5_1_; lvt_6_1_ != null; lvt_6_1_ = lvt_7_1_)
        {
            lvt_7_1_ = lvt_6_1_.nextEntry;

            if (lvt_6_1_.key == p_76152_1_)
            {
                ++this.modCount;
                --this.numHashElements;

                if (lvt_5_1_ == lvt_6_1_)
                {
                    this.hashArray[lvt_4_1_] = lvt_7_1_;
                }
                else
                {
                    lvt_5_1_.nextEntry = lvt_7_1_;
                }

                return lvt_6_1_;
            }

            lvt_5_1_ = lvt_6_1_;
        }

        return lvt_6_1_;
    }

    /**
     * creates the key in the hash table
     */
    private void createKey(int p_76156_1_, long p_76156_2_, V p_76156_4_, int p_76156_5_)
    {
        LongHashMap.Entry<V> lvt_6_1_ = this.hashArray[p_76156_5_];
        this.hashArray[p_76156_5_] = new LongHashMap.Entry(p_76156_1_, p_76156_2_, p_76156_4_, lvt_6_1_);

        if (this.numHashElements++ >= this.capacity)
        {
            this.resizeTable(2 * this.hashArray.length);
        }
    }

    static class Entry<V>
    {
        final long key;
        V value;
        LongHashMap.Entry<V> nextEntry;
        final int hash;

        Entry(int p_i1553_1_, long p_i1553_2_, V p_i1553_4_, LongHashMap.Entry<V> p_i1553_5_)
        {
            this.value = p_i1553_4_;
            this.nextEntry = p_i1553_5_;
            this.key = p_i1553_2_;
            this.hash = p_i1553_1_;
        }

        public final long getKey()
        {
            return this.key;
        }

        public final V getValue()
        {
            return this.value;
        }

        public final boolean equals(Object p_equals_1_)
        {
            if (!(p_equals_1_ instanceof LongHashMap.Entry))
            {
                return false;
            }
            else
            {
                LongHashMap.Entry<V> lvt_2_1_ = (LongHashMap.Entry)p_equals_1_;
                Object lvt_3_1_ = Long.valueOf(this.getKey());
                Object lvt_4_1_ = Long.valueOf(lvt_2_1_.getKey());

                if (lvt_3_1_ == lvt_4_1_ || lvt_3_1_ != null && lvt_3_1_.equals(lvt_4_1_))
                {
                    Object lvt_5_1_ = this.getValue();
                    Object lvt_6_1_ = lvt_2_1_.getValue();

                    if (lvt_5_1_ == lvt_6_1_ || lvt_5_1_ != null && lvt_5_1_.equals(lvt_6_1_))
                    {
                        return true;
                    }
                }

                return false;
            }
        }

        public final int hashCode()
        {
            return LongHashMap.getHashedKey(this.key);
        }

        public final String toString()
        {
            return this.getKey() + "=" + this.getValue();
        }
    }
}
