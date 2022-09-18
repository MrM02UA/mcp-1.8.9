package net.minecraft.util;

public class IntHashMap<V>
{
    private transient IntHashMap.Entry<V>[] slots = new IntHashMap.Entry[16];

    /** The number of items stored in this map */
    private transient int count;

    /** The grow threshold */
    private int threshold = 12;

    /** The scale factor used to determine when to grow the table */
    private final float growFactor = 0.75F;

    /**
     * Makes the passed in integer suitable for hashing by a number of shifts
     */
    private static int computeHash(int integer)
    {
        integer = integer ^ integer >>> 20 ^ integer >>> 12;
        return integer ^ integer >>> 7 ^ integer >>> 4;
    }

    /**
     * Computes the index of the slot for the hash and slot count passed in.
     */
    private static int getSlotIndex(int hash, int slotCount)
    {
        return hash & slotCount - 1;
    }

    /**
     * Returns the object associated to a key
     */
    public V lookup(int p_76041_1_)
    {
        int lvt_2_1_ = computeHash(p_76041_1_);

        for (IntHashMap.Entry<V> lvt_3_1_ = this.slots[getSlotIndex(lvt_2_1_, this.slots.length)]; lvt_3_1_ != null; lvt_3_1_ = lvt_3_1_.nextEntry)
        {
            if (lvt_3_1_.hashEntry == p_76041_1_)
            {
                return lvt_3_1_.valueEntry;
            }
        }

        return (V)null;
    }

    /**
     * Returns true if this hash table contains the specified item.
     */
    public boolean containsItem(int p_76037_1_)
    {
        return this.lookupEntry(p_76037_1_) != null;
    }

    final IntHashMap.Entry<V> lookupEntry(int p_76045_1_)
    {
        int lvt_2_1_ = computeHash(p_76045_1_);

        for (IntHashMap.Entry<V> lvt_3_1_ = this.slots[getSlotIndex(lvt_2_1_, this.slots.length)]; lvt_3_1_ != null; lvt_3_1_ = lvt_3_1_.nextEntry)
        {
            if (lvt_3_1_.hashEntry == p_76045_1_)
            {
                return lvt_3_1_;
            }
        }

        return null;
    }

    /**
     * Adds a key and associated value to this map
     */
    public void addKey(int p_76038_1_, V p_76038_2_)
    {
        int lvt_3_1_ = computeHash(p_76038_1_);
        int lvt_4_1_ = getSlotIndex(lvt_3_1_, this.slots.length);

        for (IntHashMap.Entry<V> lvt_5_1_ = this.slots[lvt_4_1_]; lvt_5_1_ != null; lvt_5_1_ = lvt_5_1_.nextEntry)
        {
            if (lvt_5_1_.hashEntry == p_76038_1_)
            {
                lvt_5_1_.valueEntry = p_76038_2_;
                return;
            }
        }

        this.insert(lvt_3_1_, p_76038_1_, p_76038_2_, lvt_4_1_);
    }

    /**
     * Increases the number of hash slots
     */
    private void grow(int p_76047_1_)
    {
        IntHashMap.Entry<V>[] lvt_2_1_ = this.slots;
        int lvt_3_1_ = lvt_2_1_.length;

        if (lvt_3_1_ == 1073741824)
        {
            this.threshold = Integer.MAX_VALUE;
        }
        else
        {
            IntHashMap.Entry<V>[] lvt_4_1_ = new IntHashMap.Entry[p_76047_1_];
            this.copyTo(lvt_4_1_);
            this.slots = lvt_4_1_;
            this.threshold = (int)((float)p_76047_1_ * this.growFactor);
        }
    }

    /**
     * Copies the hash slots to a new array
     */
    private void copyTo(IntHashMap.Entry<V>[] p_76048_1_)
    {
        IntHashMap.Entry<V>[] lvt_2_1_ = this.slots;
        int lvt_3_1_ = p_76048_1_.length;

        for (int lvt_4_1_ = 0; lvt_4_1_ < lvt_2_1_.length; ++lvt_4_1_)
        {
            IntHashMap.Entry<V> lvt_5_1_ = lvt_2_1_[lvt_4_1_];

            if (lvt_5_1_ != null)
            {
                lvt_2_1_[lvt_4_1_] = null;

                while (true)
                {
                    IntHashMap.Entry<V> lvt_6_1_ = lvt_5_1_.nextEntry;
                    int lvt_7_1_ = getSlotIndex(lvt_5_1_.slotHash, lvt_3_1_);
                    lvt_5_1_.nextEntry = p_76048_1_[lvt_7_1_];
                    p_76048_1_[lvt_7_1_] = lvt_5_1_;
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
     * Removes the specified object from the map and returns it
     */
    public V removeObject(int p_76049_1_)
    {
        IntHashMap.Entry<V> lvt_2_1_ = this.removeEntry(p_76049_1_);
        return (V)(lvt_2_1_ == null ? null : lvt_2_1_.valueEntry);
    }

    final IntHashMap.Entry<V> removeEntry(int p_76036_1_)
    {
        int lvt_2_1_ = computeHash(p_76036_1_);
        int lvt_3_1_ = getSlotIndex(lvt_2_1_, this.slots.length);
        IntHashMap.Entry<V> lvt_4_1_ = this.slots[lvt_3_1_];
        IntHashMap.Entry<V> lvt_5_1_;
        IntHashMap.Entry<V> lvt_6_1_;

        for (lvt_5_1_ = lvt_4_1_; lvt_5_1_ != null; lvt_5_1_ = lvt_6_1_)
        {
            lvt_6_1_ = lvt_5_1_.nextEntry;

            if (lvt_5_1_.hashEntry == p_76036_1_)
            {
                --this.count;

                if (lvt_4_1_ == lvt_5_1_)
                {
                    this.slots[lvt_3_1_] = lvt_6_1_;
                }
                else
                {
                    lvt_4_1_.nextEntry = lvt_6_1_;
                }

                return lvt_5_1_;
            }

            lvt_4_1_ = lvt_5_1_;
        }

        return lvt_5_1_;
    }

    /**
     * Removes all entries from the map
     */
    public void clearMap()
    {
        IntHashMap.Entry<V>[] lvt_1_1_ = this.slots;

        for (int lvt_2_1_ = 0; lvt_2_1_ < lvt_1_1_.length; ++lvt_2_1_)
        {
            lvt_1_1_[lvt_2_1_] = null;
        }

        this.count = 0;
    }

    /**
     * Adds an object to a slot
     */
    private void insert(int p_76040_1_, int p_76040_2_, V p_76040_3_, int p_76040_4_)
    {
        IntHashMap.Entry<V> lvt_5_1_ = this.slots[p_76040_4_];
        this.slots[p_76040_4_] = new IntHashMap.Entry(p_76040_1_, p_76040_2_, p_76040_3_, lvt_5_1_);

        if (this.count++ >= this.threshold)
        {
            this.grow(2 * this.slots.length);
        }
    }

    static class Entry<V>
    {
        final int hashEntry;
        V valueEntry;
        IntHashMap.Entry<V> nextEntry;
        final int slotHash;

        Entry(int p_i1552_1_, int p_i1552_2_, V p_i1552_3_, IntHashMap.Entry<V> p_i1552_4_)
        {
            this.valueEntry = p_i1552_3_;
            this.nextEntry = p_i1552_4_;
            this.hashEntry = p_i1552_2_;
            this.slotHash = p_i1552_1_;
        }

        public final int getHash()
        {
            return this.hashEntry;
        }

        public final V getValue()
        {
            return this.valueEntry;
        }

        public final boolean equals(Object p_equals_1_)
        {
            if (!(p_equals_1_ instanceof IntHashMap.Entry))
            {
                return false;
            }
            else
            {
                IntHashMap.Entry<V> lvt_2_1_ = (IntHashMap.Entry)p_equals_1_;
                Object lvt_3_1_ = Integer.valueOf(this.getHash());
                Object lvt_4_1_ = Integer.valueOf(lvt_2_1_.getHash());

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
            return IntHashMap.computeHash(this.hashEntry);
        }

        public final String toString()
        {
            return this.getHash() + "=" + this.getValue();
        }
    }
}
