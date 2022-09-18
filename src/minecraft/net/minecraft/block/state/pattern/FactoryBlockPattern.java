package net.minecraft.block.state.pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.state.BlockWorldState;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class FactoryBlockPattern
{
    private static final Joiner COMMA_JOIN = Joiner.on(",");
    private final List<String[]> depth = Lists.newArrayList();
    private final Map<Character, Predicate<BlockWorldState>> symbolMap = Maps.newHashMap();
    private int aisleHeight;
    private int rowWidth;

    private FactoryBlockPattern()
    {
        this.symbolMap.put(' ', Predicates.alwaysTrue());
    }

    public FactoryBlockPattern aisle(String... aisle)
    {
        if (!ArrayUtils.isEmpty(aisle) && !StringUtils.isEmpty(aisle[0]))
        {
            if (this.depth.isEmpty())
            {
                this.aisleHeight = aisle.length;
                this.rowWidth = aisle[0].length();
            }

            if (aisle.length != this.aisleHeight)
            {
                throw new IllegalArgumentException("Expected aisle with height of " + this.aisleHeight + ", but was given one with a height of " + aisle.length + ")");
            }
            else
            {
                for (String lvt_5_1_ : aisle)
                {
                    if (lvt_5_1_.length() != this.rowWidth)
                    {
                        throw new IllegalArgumentException("Not all rows in the given aisle are the correct width (expected " + this.rowWidth + ", found one with " + lvt_5_1_.length() + ")");
                    }

                    for (char lvt_9_1_ : lvt_5_1_.toCharArray())
                    {
                        if (!this.symbolMap.containsKey(Character.valueOf(lvt_9_1_)))
                        {
                            this.symbolMap.put(Character.valueOf(lvt_9_1_), (Object)null);
                        }
                    }
                }

                this.depth.add(aisle);
                return this;
            }
        }
        else
        {
            throw new IllegalArgumentException("Empty pattern for aisle");
        }
    }

    public static FactoryBlockPattern start()
    {
        return new FactoryBlockPattern();
    }

    public FactoryBlockPattern where(char symbol, Predicate<BlockWorldState> blockMatcher)
    {
        this.symbolMap.put(Character.valueOf(symbol), blockMatcher);
        return this;
    }

    public BlockPattern build()
    {
        return new BlockPattern(this.makePredicateArray());
    }

    private Predicate<BlockWorldState>[][][] makePredicateArray()
    {
        this.checkMissingPredicates();
        Predicate<BlockWorldState>[][][] lvt_1_1_ = (Predicate[][][])((Predicate[][][])Array.newInstance(Predicate.class, new int[] {this.depth.size(), this.aisleHeight, this.rowWidth}));

        for (int lvt_2_1_ = 0; lvt_2_1_ < this.depth.size(); ++lvt_2_1_)
        {
            for (int lvt_3_1_ = 0; lvt_3_1_ < this.aisleHeight; ++lvt_3_1_)
            {
                for (int lvt_4_1_ = 0; lvt_4_1_ < this.rowWidth; ++lvt_4_1_)
                {
                    lvt_1_1_[lvt_2_1_][lvt_3_1_][lvt_4_1_] = (Predicate)this.symbolMap.get(Character.valueOf(((String[])this.depth.get(lvt_2_1_))[lvt_3_1_].charAt(lvt_4_1_)));
                }
            }
        }

        return lvt_1_1_;
    }

    private void checkMissingPredicates()
    {
        List<Character> lvt_1_1_ = Lists.newArrayList();

        for (Entry<Character, Predicate<BlockWorldState>> lvt_3_1_ : this.symbolMap.entrySet())
        {
            if (lvt_3_1_.getValue() == null)
            {
                lvt_1_1_.add(lvt_3_1_.getKey());
            }
        }

        if (!lvt_1_1_.isEmpty())
        {
            throw new IllegalStateException("Predicates for character(s) " + COMMA_JOIN.join(lvt_1_1_) + " are missing");
        }
    }
}
