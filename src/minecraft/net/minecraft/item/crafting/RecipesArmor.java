package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipesArmor
{
    private String[][] recipePatterns = new String[][] {{"XXX", "X X"}, {"X X", "XXX", "XXX"}, {"XXX", "X X", "X X"}, {"X X", "X X"}};
    private Item[][] recipeItems = new Item[][] {{Items.leather, Items.iron_ingot, Items.diamond, Items.gold_ingot}, {Items.leather_helmet, Items.iron_helmet, Items.diamond_helmet, Items.golden_helmet}, {Items.leather_chestplate, Items.iron_chestplate, Items.diamond_chestplate, Items.golden_chestplate}, {Items.leather_leggings, Items.iron_leggings, Items.diamond_leggings, Items.golden_leggings}, {Items.leather_boots, Items.iron_boots, Items.diamond_boots, Items.golden_boots}};

    /**
     * Adds the armor recipes to the CraftingManager.
     */
    public void addRecipes(CraftingManager craftManager)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < this.recipeItems[0].length; ++lvt_2_1_)
        {
            Item lvt_3_1_ = this.recipeItems[0][lvt_2_1_];

            for (int lvt_4_1_ = 0; lvt_4_1_ < this.recipeItems.length - 1; ++lvt_4_1_)
            {
                Item lvt_5_1_ = this.recipeItems[lvt_4_1_ + 1][lvt_2_1_];
                craftManager.addRecipe(new ItemStack(lvt_5_1_), new Object[] {this.recipePatterns[lvt_4_1_], 'X', lvt_3_1_});
            }
        }
    }
}
