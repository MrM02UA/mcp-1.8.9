package net.minecraft.item.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipesWeapons
{
    private String[][] recipePatterns = new String[][] {{"X", "X", "#"}};
    private Object[][] recipeItems = new Object[][] {{Blocks.planks, Blocks.cobblestone, Items.iron_ingot, Items.diamond, Items.gold_ingot}, {Items.wooden_sword, Items.stone_sword, Items.iron_sword, Items.diamond_sword, Items.golden_sword}};

    /**
     * Adds the weapon recipes to the CraftingManager.
     */
    public void addRecipes(CraftingManager p_77583_1_)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < this.recipeItems[0].length; ++lvt_2_1_)
        {
            Object lvt_3_1_ = this.recipeItems[0][lvt_2_1_];

            for (int lvt_4_1_ = 0; lvt_4_1_ < this.recipeItems.length - 1; ++lvt_4_1_)
            {
                Item lvt_5_1_ = (Item)this.recipeItems[lvt_4_1_ + 1][lvt_2_1_];
                p_77583_1_.addRecipe(new ItemStack(lvt_5_1_), new Object[] {this.recipePatterns[lvt_4_1_], '#', Items.stick, 'X', lvt_3_1_});
            }
        }

        p_77583_1_.addRecipe(new ItemStack(Items.bow, 1), new Object[] {" #X", "# X", " #X", 'X', Items.string, '#', Items.stick});
        p_77583_1_.addRecipe(new ItemStack(Items.arrow, 4), new Object[] {"X", "#", "Y", 'Y', Items.feather, 'X', Items.flint, '#', Items.stick});
    }
}
