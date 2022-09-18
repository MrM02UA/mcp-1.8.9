package net.minecraft.item.crafting;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

public class RecipesIngots
{
    private Object[][] recipeItems = new Object[][] {{Blocks.gold_block, new ItemStack(Items.gold_ingot, 9)}, {Blocks.iron_block, new ItemStack(Items.iron_ingot, 9)}, {Blocks.diamond_block, new ItemStack(Items.diamond, 9)}, {Blocks.emerald_block, new ItemStack(Items.emerald, 9)}, {Blocks.lapis_block, new ItemStack(Items.dye, 9, EnumDyeColor.BLUE.getDyeDamage())}, {Blocks.redstone_block, new ItemStack(Items.redstone, 9)}, {Blocks.coal_block, new ItemStack(Items.coal, 9, 0)}, {Blocks.hay_block, new ItemStack(Items.wheat, 9)}, {Blocks.slime_block, new ItemStack(Items.slime_ball, 9)}};

    /**
     * Adds the ingot recipes to the CraftingManager.
     */
    public void addRecipes(CraftingManager p_77590_1_)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < this.recipeItems.length; ++lvt_2_1_)
        {
            Block lvt_3_1_ = (Block)this.recipeItems[lvt_2_1_][0];
            ItemStack lvt_4_1_ = (ItemStack)this.recipeItems[lvt_2_1_][1];
            p_77590_1_.addRecipe(new ItemStack(lvt_3_1_), new Object[] {"###", "###", "###", '#', lvt_4_1_});
            p_77590_1_.addRecipe(lvt_4_1_, new Object[] {"#", '#', lvt_3_1_});
        }

        p_77590_1_.addRecipe(new ItemStack(Items.gold_ingot), new Object[] {"###", "###", "###", '#', Items.gold_nugget});
        p_77590_1_.addRecipe(new ItemStack(Items.gold_nugget, 9), new Object[] {"#", '#', Items.gold_ingot});
    }
}
