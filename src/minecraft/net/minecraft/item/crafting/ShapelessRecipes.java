package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ShapelessRecipes implements IRecipe
{
    /** Is the ItemStack that you get when craft the recipe. */
    private final ItemStack recipeOutput;
    private final List<ItemStack> recipeItems;

    public ShapelessRecipes(ItemStack output, List<ItemStack> inputList)
    {
        this.recipeOutput = output;
        this.recipeItems = inputList;
    }

    public ItemStack getRecipeOutput()
    {
        return this.recipeOutput;
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

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(InventoryCrafting inv, World worldIn)
    {
        List<ItemStack> lvt_3_1_ = Lists.newArrayList(this.recipeItems);

        for (int lvt_4_1_ = 0; lvt_4_1_ < inv.getHeight(); ++lvt_4_1_)
        {
            for (int lvt_5_1_ = 0; lvt_5_1_ < inv.getWidth(); ++lvt_5_1_)
            {
                ItemStack lvt_6_1_ = inv.getStackInRowAndColumn(lvt_5_1_, lvt_4_1_);

                if (lvt_6_1_ != null)
                {
                    boolean lvt_7_1_ = false;

                    for (ItemStack lvt_9_1_ : lvt_3_1_)
                    {
                        if (lvt_6_1_.getItem() == lvt_9_1_.getItem() && (lvt_9_1_.getMetadata() == 32767 || lvt_6_1_.getMetadata() == lvt_9_1_.getMetadata()))
                        {
                            lvt_7_1_ = true;
                            lvt_3_1_.remove(lvt_9_1_);
                            break;
                        }
                    }

                    if (!lvt_7_1_)
                    {
                        return false;
                    }
                }
            }
        }

        return lvt_3_1_.isEmpty();
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        return this.recipeOutput.copy();
    }

    /**
     * Returns the size of the recipe area
     */
    public int getRecipeSize()
    {
        return this.recipeItems.size();
    }
}
