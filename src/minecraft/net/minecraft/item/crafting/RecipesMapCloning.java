package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RecipesMapCloning implements IRecipe
{
    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(InventoryCrafting inv, World worldIn)
    {
        int lvt_3_1_ = 0;
        ItemStack lvt_4_1_ = null;

        for (int lvt_5_1_ = 0; lvt_5_1_ < inv.getSizeInventory(); ++lvt_5_1_)
        {
            ItemStack lvt_6_1_ = inv.getStackInSlot(lvt_5_1_);

            if (lvt_6_1_ != null)
            {
                if (lvt_6_1_.getItem() == Items.filled_map)
                {
                    if (lvt_4_1_ != null)
                    {
                        return false;
                    }

                    lvt_4_1_ = lvt_6_1_;
                }
                else
                {
                    if (lvt_6_1_.getItem() != Items.map)
                    {
                        return false;
                    }

                    ++lvt_3_1_;
                }
            }
        }

        return lvt_4_1_ != null && lvt_3_1_ > 0;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        int lvt_2_1_ = 0;
        ItemStack lvt_3_1_ = null;

        for (int lvt_4_1_ = 0; lvt_4_1_ < inv.getSizeInventory(); ++lvt_4_1_)
        {
            ItemStack lvt_5_1_ = inv.getStackInSlot(lvt_4_1_);

            if (lvt_5_1_ != null)
            {
                if (lvt_5_1_.getItem() == Items.filled_map)
                {
                    if (lvt_3_1_ != null)
                    {
                        return null;
                    }

                    lvt_3_1_ = lvt_5_1_;
                }
                else
                {
                    if (lvt_5_1_.getItem() != Items.map)
                    {
                        return null;
                    }

                    ++lvt_2_1_;
                }
            }
        }

        if (lvt_3_1_ != null && lvt_2_1_ >= 1)
        {
            ItemStack lvt_4_2_ = new ItemStack(Items.filled_map, lvt_2_1_ + 1, lvt_3_1_.getMetadata());

            if (lvt_3_1_.hasDisplayName())
            {
                lvt_4_2_.setStackDisplayName(lvt_3_1_.getDisplayName());
            }

            return lvt_4_2_;
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns the size of the recipe area
     */
    public int getRecipeSize()
    {
        return 9;
    }

    public ItemStack getRecipeOutput()
    {
        return null;
    }

    public ItemStack[] getRemainingItems(InventoryCrafting inv)
    {
        ItemStack[] lvt_2_1_ = new ItemStack[inv.getSizeInventory()];

        for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.length; ++lvt_3_1_)
        {
            ItemStack lvt_4_1_ = inv.getStackInSlot(lvt_3_1_);

            if (lvt_4_1_ != null && lvt_4_1_.getItem().hasContainerItem())
            {
                lvt_2_1_[lvt_3_1_] = new ItemStack(lvt_4_1_.getItem().getContainerItem());
            }
        }

        return lvt_2_1_;
    }
}
