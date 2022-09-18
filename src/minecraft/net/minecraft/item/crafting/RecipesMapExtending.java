package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class RecipesMapExtending extends ShapedRecipes
{
    public RecipesMapExtending()
    {
        super(3, 3, new ItemStack[] {new ItemStack(Items.paper), new ItemStack(Items.paper), new ItemStack(Items.paper), new ItemStack(Items.paper), new ItemStack(Items.filled_map, 0, 32767), new ItemStack(Items.paper), new ItemStack(Items.paper), new ItemStack(Items.paper), new ItemStack(Items.paper)}, new ItemStack(Items.map, 0, 0));
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(InventoryCrafting inv, World worldIn)
    {
        if (!super.matches(inv, worldIn))
        {
            return false;
        }
        else
        {
            ItemStack lvt_3_1_ = null;

            for (int lvt_4_1_ = 0; lvt_4_1_ < inv.getSizeInventory() && lvt_3_1_ == null; ++lvt_4_1_)
            {
                ItemStack lvt_5_1_ = inv.getStackInSlot(lvt_4_1_);

                if (lvt_5_1_ != null && lvt_5_1_.getItem() == Items.filled_map)
                {
                    lvt_3_1_ = lvt_5_1_;
                }
            }

            if (lvt_3_1_ == null)
            {
                return false;
            }
            else
            {
                MapData lvt_4_2_ = Items.filled_map.getMapData(lvt_3_1_, worldIn);
                return lvt_4_2_ == null ? false : lvt_4_2_.scale < 4;
            }
        }
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        ItemStack lvt_2_1_ = null;

        for (int lvt_3_1_ = 0; lvt_3_1_ < inv.getSizeInventory() && lvt_2_1_ == null; ++lvt_3_1_)
        {
            ItemStack lvt_4_1_ = inv.getStackInSlot(lvt_3_1_);

            if (lvt_4_1_ != null && lvt_4_1_.getItem() == Items.filled_map)
            {
                lvt_2_1_ = lvt_4_1_;
            }
        }

        lvt_2_1_ = lvt_2_1_.copy();
        lvt_2_1_.stackSize = 1;

        if (lvt_2_1_.getTagCompound() == null)
        {
            lvt_2_1_.setTagCompound(new NBTTagCompound());
        }

        lvt_2_1_.getTagCompound().setBoolean("map_is_scaling", true);
        return lvt_2_1_;
    }
}
