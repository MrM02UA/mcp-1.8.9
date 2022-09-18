package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RecipeRepairItem implements IRecipe
{
    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(InventoryCrafting inv, World worldIn)
    {
        List<ItemStack> lvt_3_1_ = Lists.newArrayList();

        for (int lvt_4_1_ = 0; lvt_4_1_ < inv.getSizeInventory(); ++lvt_4_1_)
        {
            ItemStack lvt_5_1_ = inv.getStackInSlot(lvt_4_1_);

            if (lvt_5_1_ != null)
            {
                lvt_3_1_.add(lvt_5_1_);

                if (lvt_3_1_.size() > 1)
                {
                    ItemStack lvt_6_1_ = (ItemStack)lvt_3_1_.get(0);

                    if (lvt_5_1_.getItem() != lvt_6_1_.getItem() || lvt_6_1_.stackSize != 1 || lvt_5_1_.stackSize != 1 || !lvt_6_1_.getItem().isDamageable())
                    {
                        return false;
                    }
                }
            }
        }

        return lvt_3_1_.size() == 2;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        List<ItemStack> lvt_2_1_ = Lists.newArrayList();

        for (int lvt_3_1_ = 0; lvt_3_1_ < inv.getSizeInventory(); ++lvt_3_1_)
        {
            ItemStack lvt_4_1_ = inv.getStackInSlot(lvt_3_1_);

            if (lvt_4_1_ != null)
            {
                lvt_2_1_.add(lvt_4_1_);

                if (lvt_2_1_.size() > 1)
                {
                    ItemStack lvt_5_1_ = (ItemStack)lvt_2_1_.get(0);

                    if (lvt_4_1_.getItem() != lvt_5_1_.getItem() || lvt_5_1_.stackSize != 1 || lvt_4_1_.stackSize != 1 || !lvt_5_1_.getItem().isDamageable())
                    {
                        return null;
                    }
                }
            }
        }

        if (lvt_2_1_.size() == 2)
        {
            ItemStack lvt_3_2_ = (ItemStack)lvt_2_1_.get(0);
            ItemStack lvt_4_2_ = (ItemStack)lvt_2_1_.get(1);

            if (lvt_3_2_.getItem() == lvt_4_2_.getItem() && lvt_3_2_.stackSize == 1 && lvt_4_2_.stackSize == 1 && lvt_3_2_.getItem().isDamageable())
            {
                Item lvt_5_2_ = lvt_3_2_.getItem();
                int lvt_6_1_ = lvt_5_2_.getMaxDamage() - lvt_3_2_.getItemDamage();
                int lvt_7_1_ = lvt_5_2_.getMaxDamage() - lvt_4_2_.getItemDamage();
                int lvt_8_1_ = lvt_6_1_ + lvt_7_1_ + lvt_5_2_.getMaxDamage() * 5 / 100;
                int lvt_9_1_ = lvt_5_2_.getMaxDamage() - lvt_8_1_;

                if (lvt_9_1_ < 0)
                {
                    lvt_9_1_ = 0;
                }

                return new ItemStack(lvt_3_2_.getItem(), 1, lvt_9_1_);
            }
        }

        return null;
    }

    /**
     * Returns the size of the recipe area
     */
    public int getRecipeSize()
    {
        return 4;
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
