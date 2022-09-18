package net.minecraft.client.renderer.block.statemap;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;

public abstract class StateMapperBase implements IStateMapper
{
    protected Map<IBlockState, ModelResourceLocation> mapStateModelLocations = Maps.newLinkedHashMap();

    public String getPropertyString(Map<IProperty, Comparable> p_178131_1_)
    {
        StringBuilder lvt_2_1_ = new StringBuilder();

        for (Entry<IProperty, Comparable> lvt_4_1_ : p_178131_1_.entrySet())
        {
            if (lvt_2_1_.length() != 0)
            {
                lvt_2_1_.append(",");
            }

            IProperty lvt_5_1_ = (IProperty)lvt_4_1_.getKey();
            Comparable lvt_6_1_ = (Comparable)lvt_4_1_.getValue();
            lvt_2_1_.append(lvt_5_1_.getName());
            lvt_2_1_.append("=");
            lvt_2_1_.append(lvt_5_1_.getName(lvt_6_1_));
        }

        if (lvt_2_1_.length() == 0)
        {
            lvt_2_1_.append("normal");
        }

        return lvt_2_1_.toString();
    }

    public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block blockIn)
    {
        for (IBlockState lvt_3_1_ : blockIn.getBlockState().getValidStates())
        {
            this.mapStateModelLocations.put(lvt_3_1_, this.getModelResourceLocation(lvt_3_1_));
        }

        return this.mapStateModelLocations;
    }

    protected abstract ModelResourceLocation getModelResourceLocation(IBlockState state);
}
