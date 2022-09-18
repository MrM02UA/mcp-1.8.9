package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class RecipeBookCloning implements IRecipe
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
                if (lvt_6_1_.getItem() == Items.written_book)
                {
                    if (lvt_4_1_ != null)
                    {
                        return false;
                    }

                    lvt_4_1_ = lvt_6_1_;
                }
                else
                {
                    if (lvt_6_1_.getItem() != Items.writable_book)
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
                if (lvt_5_1_.getItem() == Items.written_book)
                {
                    if (lvt_3_1_ != null)
                    {
                        return null;
                    }

                    lvt_3_1_ = lvt_5_1_;
                }
                else
                {
                    if (lvt_5_1_.getItem() != Items.writable_book)
                    {
                        return null;
                    }

                    ++lvt_2_1_;
                }
            }
        }

        if (lvt_3_1_ != null && lvt_2_1_ >= 1 && ItemEditableBook.getGeneration(lvt_3_1_) < 2)
        {
            ItemStack lvt_4_2_ = new ItemStack(Items.written_book, lvt_2_1_);
            lvt_4_2_.setTagCompound((NBTTagCompound)lvt_3_1_.getTagCompound().copy());
            lvt_4_2_.getTagCompound().setInteger("generation", ItemEditableBook.getGeneration(lvt_3_1_) + 1);

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

            if (lvt_4_1_ != null && lvt_4_1_.getItem() instanceof ItemEditableBook)
            {
                lvt_2_1_[lvt_3_1_] = lvt_4_1_;
                break;
            }
        }

        return lvt_2_1_;
    }
}
