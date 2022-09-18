package net.minecraft.item.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ShapedRecipes implements IRecipe
{
    /** How many horizontal slots this recipe is wide. */
    private final int recipeWidth;

    /** How many vertical slots this recipe uses. */
    private final int recipeHeight;

    /** Is a array of ItemStack that composes the recipe. */
    private final ItemStack[] recipeItems;

    /** Is the ItemStack that you get when craft the recipe. */
    private final ItemStack recipeOutput;
    private boolean copyIngredientNBT;

    public ShapedRecipes(int width, int height, ItemStack[] p_i1917_3_, ItemStack output)
    {
        this.recipeWidth = width;
        this.recipeHeight = height;
        this.recipeItems = p_i1917_3_;
        this.recipeOutput = output;
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
        for (int lvt_3_1_ = 0; lvt_3_1_ <= 3 - this.recipeWidth; ++lvt_3_1_)
        {
            for (int lvt_4_1_ = 0; lvt_4_1_ <= 3 - this.recipeHeight; ++lvt_4_1_)
            {
                if (this.checkMatch(inv, lvt_3_1_, lvt_4_1_, true))
                {
                    return true;
                }

                if (this.checkMatch(inv, lvt_3_1_, lvt_4_1_, false))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if the region of a crafting inventory is match for the recipe.
     */
    private boolean checkMatch(InventoryCrafting p_77573_1_, int p_77573_2_, int p_77573_3_, boolean p_77573_4_)
    {
        for (int lvt_5_1_ = 0; lvt_5_1_ < 3; ++lvt_5_1_)
        {
            for (int lvt_6_1_ = 0; lvt_6_1_ < 3; ++lvt_6_1_)
            {
                int lvt_7_1_ = lvt_5_1_ - p_77573_2_;
                int lvt_8_1_ = lvt_6_1_ - p_77573_3_;
                ItemStack lvt_9_1_ = null;

                if (lvt_7_1_ >= 0 && lvt_8_1_ >= 0 && lvt_7_1_ < this.recipeWidth && lvt_8_1_ < this.recipeHeight)
                {
                    if (p_77573_4_)
                    {
                        lvt_9_1_ = this.recipeItems[this.recipeWidth - lvt_7_1_ - 1 + lvt_8_1_ * this.recipeWidth];
                    }
                    else
                    {
                        lvt_9_1_ = this.recipeItems[lvt_7_1_ + lvt_8_1_ * this.recipeWidth];
                    }
                }

                ItemStack lvt_10_1_ = p_77573_1_.getStackInRowAndColumn(lvt_5_1_, lvt_6_1_);

                if (lvt_10_1_ != null || lvt_9_1_ != null)
                {
                    if (lvt_10_1_ == null && lvt_9_1_ != null || lvt_10_1_ != null && lvt_9_1_ == null)
                    {
                        return false;
                    }

                    if (lvt_9_1_.getItem() != lvt_10_1_.getItem())
                    {
                        return false;
                    }

                    if (lvt_9_1_.getMetadata() != 32767 && lvt_9_1_.getMetadata() != lvt_10_1_.getMetadata())
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        ItemStack lvt_2_1_ = this.getRecipeOutput().copy();

        if (this.copyIngredientNBT)
        {
            for (int lvt_3_1_ = 0; lvt_3_1_ < inv.getSizeInventory(); ++lvt_3_1_)
            {
                ItemStack lvt_4_1_ = inv.getStackInSlot(lvt_3_1_);

                if (lvt_4_1_ != null && lvt_4_1_.hasTagCompound())
                {
                    lvt_2_1_.setTagCompound((NBTTagCompound)lvt_4_1_.getTagCompound().copy());
                }
            }
        }

        return lvt_2_1_;
    }

    /**
     * Returns the size of the recipe area
     */
    public int getRecipeSize()
    {
        return this.recipeWidth * this.recipeHeight;
    }
}
