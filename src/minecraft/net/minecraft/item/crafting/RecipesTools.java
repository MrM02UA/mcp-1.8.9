package net.minecraft.item.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipesTools
{
    private String[][] recipePatterns = new String[][] {{"XXX", " # ", " # "}, {"X", "#", "#"}, {"XX", "X#", " #"}, {"XX", " #", " #"}};
    private Object[][] recipeItems = new Object[][] {{Blocks.planks, Blocks.cobblestone, Items.iron_ingot, Items.diamond, Items.gold_ingot}, {Items.wooden_pickaxe, Items.stone_pickaxe, Items.iron_pickaxe, Items.diamond_pickaxe, Items.golden_pickaxe}, {Items.wooden_shovel, Items.stone_shovel, Items.iron_shovel, Items.diamond_shovel, Items.golden_shovel}, {Items.wooden_axe, Items.stone_axe, Items.iron_axe, Items.diamond_axe, Items.golden_axe}, {Items.wooden_hoe, Items.stone_hoe, Items.iron_hoe, Items.diamond_hoe, Items.golden_hoe}};

    /**
     * Adds the tool recipes to the CraftingManager.
     */
    public void addRecipes(CraftingManager p_77586_1_)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < this.recipeItems[0].length; ++lvt_2_1_)
        {
            Object lvt_3_1_ = this.recipeItems[0][lvt_2_1_];

            for (int lvt_4_1_ = 0; lvt_4_1_ < this.recipeItems.length - 1; ++lvt_4_1_)
            {
                Item lvt_5_1_ = (Item)this.recipeItems[lvt_4_1_ + 1][lvt_2_1_];
                p_77586_1_.addRecipe(new ItemStack(lvt_5_1_), new Object[] {this.recipePatterns[lvt_4_1_], '#', Items.stick, 'X', lvt_3_1_});
            }
        }

        p_77586_1_.addRecipe(new ItemStack(Items.shears), new Object[] {" #", "# ", '#', Items.iron_ingot});
    }
}
