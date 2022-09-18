package net.minecraft.client.renderer.block.statemap;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;

public class BlockStateMapper
{
    private Map<Block, IStateMapper> blockStateMap = Maps.newIdentityHashMap();
    private Set<Block> setBuiltInBlocks = Sets.newIdentityHashSet();

    public void registerBlockStateMapper(Block p_178447_1_, IStateMapper p_178447_2_)
    {
        this.blockStateMap.put(p_178447_1_, p_178447_2_);
    }

    public void registerBuiltInBlocks(Block... p_178448_1_)
    {
        Collections.addAll(this.setBuiltInBlocks, p_178448_1_);
    }

    public Map<IBlockState, ModelResourceLocation> putAllStateModelLocations()
    {
        Map<IBlockState, ModelResourceLocation> lvt_1_1_ = Maps.newIdentityHashMap();

        for (Block lvt_3_1_ : Block.blockRegistry)
        {
            if (!this.setBuiltInBlocks.contains(lvt_3_1_))
            {
                lvt_1_1_.putAll(((IStateMapper)Objects.firstNonNull(this.blockStateMap.get(lvt_3_1_), new DefaultStateMapper())).putStateModelLocations(lvt_3_1_));
            }
        }

        return lvt_1_1_;
    }
}
