package net.minecraft.block.state.pattern;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;

public class BlockStateHelper implements Predicate<IBlockState>
{
    private final BlockState blockstate;
    private final Map<IProperty, Predicate> propertyPredicates = Maps.newHashMap();

    private BlockStateHelper(BlockState blockStateIn)
    {
        this.blockstate = blockStateIn;
    }

    public static BlockStateHelper forBlock(Block blockIn)
    {
        return new BlockStateHelper(blockIn.getBlockState());
    }

    public boolean apply(IBlockState p_apply_1_)
    {
        if (p_apply_1_ != null && p_apply_1_.getBlock().equals(this.blockstate.getBlock()))
        {
            for (Entry<IProperty, Predicate> lvt_3_1_ : this.propertyPredicates.entrySet())
            {
                Object lvt_4_1_ = p_apply_1_.getValue((IProperty)lvt_3_1_.getKey());

                if (!((Predicate)lvt_3_1_.getValue()).apply(lvt_4_1_))
                {
                    return false;
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    public <V extends Comparable<V>> BlockStateHelper where(IProperty<V> property, Predicate <? extends V > is)
    {
        if (!this.blockstate.getProperties().contains(property))
        {
            throw new IllegalArgumentException(this.blockstate + " cannot support property " + property);
        }
        else
        {
            this.propertyPredicates.put(property, is);
            return this;
        }
    }

    public boolean apply(Object p_apply_1_)
    {
        return this.apply((IBlockState)p_apply_1_);
    }
}
