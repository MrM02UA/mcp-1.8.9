package net.minecraft.client.renderer.block.statemap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;

public class StateMap extends StateMapperBase
{
    private final IProperty<?> name;
    private final String suffix;
    private final List < IProperty<? >> ignored;

    private StateMap(IProperty<?> name, String suffix, List < IProperty<? >> ignored)
    {
        this.name = name;
        this.suffix = suffix;
        this.ignored = ignored;
    }

    protected ModelResourceLocation getModelResourceLocation(IBlockState state)
    {
        Map<IProperty, Comparable> lvt_2_1_ = Maps.newLinkedHashMap(state.getProperties());
        String lvt_3_1_;

        if (this.name == null)
        {
            lvt_3_1_ = ((ResourceLocation)Block.blockRegistry.getNameForObject(state.getBlock())).toString();
        }
        else
        {
            lvt_3_1_ = this.name.getName((Comparable)lvt_2_1_.remove(this.name));
        }

        if (this.suffix != null)
        {
            lvt_3_1_ = lvt_3_1_ + this.suffix;
        }

        for (IProperty<?> lvt_5_1_ : this.ignored)
        {
            lvt_2_1_.remove(lvt_5_1_);
        }

        return new ModelResourceLocation(lvt_3_1_, this.getPropertyString(lvt_2_1_));
    }

    public static class Builder
    {
        private IProperty<?> name;
        private String suffix;
        private final List < IProperty<? >> ignored = Lists.newArrayList();

        public StateMap.Builder withName(IProperty<?> builderPropertyIn)
        {
            this.name = builderPropertyIn;
            return this;
        }

        public StateMap.Builder withSuffix(String builderSuffixIn)
        {
            this.suffix = builderSuffixIn;
            return this;
        }

        public StateMap.Builder ignore(IProperty<?>... p_178442_1_)
        {
            Collections.addAll(this.ignored, p_178442_1_);
            return this;
        }

        public StateMap build()
        {
            return new StateMap(this.name, this.suffix, this.ignored);
        }
    }
}
