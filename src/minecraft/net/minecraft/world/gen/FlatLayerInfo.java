package net.minecraft.world.gen;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

public class FlatLayerInfo
{
    private final int field_175902_a;
    private IBlockState layerMaterial;

    /** Amount of layers for this set of layers. */
    private int layerCount;
    private int layerMinimumY;

    public FlatLayerInfo(int p_i45467_1_, Block p_i45467_2_)
    {
        this(3, p_i45467_1_, p_i45467_2_);
    }

    public FlatLayerInfo(int p_i45627_1_, int height, Block layerMaterialIn)
    {
        this.layerCount = 1;
        this.field_175902_a = p_i45627_1_;
        this.layerCount = height;
        this.layerMaterial = layerMaterialIn.getDefaultState();
    }

    public FlatLayerInfo(int p_i45628_1_, int p_i45628_2_, Block p_i45628_3_, int p_i45628_4_)
    {
        this(p_i45628_1_, p_i45628_2_, p_i45628_3_);
        this.layerMaterial = p_i45628_3_.getStateFromMeta(p_i45628_4_);
    }

    /**
     * Return the amount of layers for this set of layers.
     */
    public int getLayerCount()
    {
        return this.layerCount;
    }

    public IBlockState getLayerMaterial()
    {
        return this.layerMaterial;
    }

    private Block getLayerMaterialBlock()
    {
        return this.layerMaterial.getBlock();
    }

    /**
     * Return the block metadata used on this set of layers.
     */
    private int getFillBlockMeta()
    {
        return this.layerMaterial.getBlock().getMetaFromState(this.layerMaterial);
    }

    /**
     * Return the minimum Y coordinate for this layer, set during generation.
     */
    public int getMinY()
    {
        return this.layerMinimumY;
    }

    /**
     * Set the minimum Y coordinate for this layer.
     */
    public void setMinY(int minY)
    {
        this.layerMinimumY = minY;
    }

    public String toString()
    {
        String lvt_1_1_;

        if (this.field_175902_a >= 3)
        {
            ResourceLocation lvt_2_1_ = (ResourceLocation)Block.blockRegistry.getNameForObject(this.getLayerMaterialBlock());
            lvt_1_1_ = lvt_2_1_ == null ? "null" : lvt_2_1_.toString();

            if (this.layerCount > 1)
            {
                lvt_1_1_ = this.layerCount + "*" + lvt_1_1_;
            }
        }
        else
        {
            lvt_1_1_ = Integer.toString(Block.getIdFromBlock(this.getLayerMaterialBlock()));

            if (this.layerCount > 1)
            {
                lvt_1_1_ = this.layerCount + "x" + lvt_1_1_;
            }
        }

        int lvt_2_2_ = this.getFillBlockMeta();

        if (lvt_2_2_ > 0)
        {
            lvt_1_1_ = lvt_1_1_ + ":" + lvt_2_2_;
        }

        return lvt_1_1_;
    }
}
