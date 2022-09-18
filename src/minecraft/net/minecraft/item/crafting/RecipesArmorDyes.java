package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RecipesArmorDyes implements IRecipe
{
    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(InventoryCrafting inv, World worldIn)
    {
        ItemStack lvt_3_1_ = null;
        List<ItemStack> lvt_4_1_ = Lists.newArrayList();

        for (int lvt_5_1_ = 0; lvt_5_1_ < inv.getSizeInventory(); ++lvt_5_1_)
        {
            ItemStack lvt_6_1_ = inv.getStackInSlot(lvt_5_1_);

            if (lvt_6_1_ != null)
            {
                if (lvt_6_1_.getItem() instanceof ItemArmor)
                {
                    ItemArmor lvt_7_1_ = (ItemArmor)lvt_6_1_.getItem();

                    if (lvt_7_1_.getArmorMaterial() != ItemArmor.ArmorMaterial.LEATHER || lvt_3_1_ != null)
                    {
                        return false;
                    }

                    lvt_3_1_ = lvt_6_1_;
                }
                else
                {
                    if (lvt_6_1_.getItem() != Items.dye)
                    {
                        return false;
                    }

                    lvt_4_1_.add(lvt_6_1_);
                }
            }
        }

        return lvt_3_1_ != null && !lvt_4_1_.isEmpty();
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        ItemStack lvt_2_1_ = null;
        int[] lvt_3_1_ = new int[3];
        int lvt_4_1_ = 0;
        int lvt_5_1_ = 0;
        ItemArmor lvt_6_1_ = null;

        for (int lvt_7_1_ = 0; lvt_7_1_ < inv.getSizeInventory(); ++lvt_7_1_)
        {
            ItemStack lvt_8_1_ = inv.getStackInSlot(lvt_7_1_);

            if (lvt_8_1_ != null)
            {
                if (lvt_8_1_.getItem() instanceof ItemArmor)
                {
                    lvt_6_1_ = (ItemArmor)lvt_8_1_.getItem();

                    if (lvt_6_1_.getArmorMaterial() != ItemArmor.ArmorMaterial.LEATHER || lvt_2_1_ != null)
                    {
                        return null;
                    }

                    lvt_2_1_ = lvt_8_1_.copy();
                    lvt_2_1_.stackSize = 1;

                    if (lvt_6_1_.hasColor(lvt_8_1_))
                    {
                        int lvt_9_1_ = lvt_6_1_.getColor(lvt_2_1_);
                        float lvt_10_1_ = (float)(lvt_9_1_ >> 16 & 255) / 255.0F;
                        float lvt_11_1_ = (float)(lvt_9_1_ >> 8 & 255) / 255.0F;
                        float lvt_12_1_ = (float)(lvt_9_1_ & 255) / 255.0F;
                        lvt_4_1_ = (int)((float)lvt_4_1_ + Math.max(lvt_10_1_, Math.max(lvt_11_1_, lvt_12_1_)) * 255.0F);
                        lvt_3_1_[0] = (int)((float)lvt_3_1_[0] + lvt_10_1_ * 255.0F);
                        lvt_3_1_[1] = (int)((float)lvt_3_1_[1] + lvt_11_1_ * 255.0F);
                        lvt_3_1_[2] = (int)((float)lvt_3_1_[2] + lvt_12_1_ * 255.0F);
                        ++lvt_5_1_;
                    }
                }
                else
                {
                    if (lvt_8_1_.getItem() != Items.dye)
                    {
                        return null;
                    }

                    float[] lvt_9_2_ = EntitySheep.getDyeRgb(EnumDyeColor.byDyeDamage(lvt_8_1_.getMetadata()));
                    int lvt_10_2_ = (int)(lvt_9_2_[0] * 255.0F);
                    int lvt_11_2_ = (int)(lvt_9_2_[1] * 255.0F);
                    int lvt_12_2_ = (int)(lvt_9_2_[2] * 255.0F);
                    lvt_4_1_ += Math.max(lvt_10_2_, Math.max(lvt_11_2_, lvt_12_2_));
                    lvt_3_1_[0] += lvt_10_2_;
                    lvt_3_1_[1] += lvt_11_2_;
                    lvt_3_1_[2] += lvt_12_2_;
                    ++lvt_5_1_;
                }
            }
        }

        if (lvt_6_1_ == null)
        {
            return null;
        }
        else
        {
            int lvt_7_2_ = lvt_3_1_[0] / lvt_5_1_;
            int lvt_8_2_ = lvt_3_1_[1] / lvt_5_1_;
            int lvt_9_3_ = lvt_3_1_[2] / lvt_5_1_;
            float lvt_10_3_ = (float)lvt_4_1_ / (float)lvt_5_1_;
            float lvt_11_3_ = (float)Math.max(lvt_7_2_, Math.max(lvt_8_2_, lvt_9_3_));
            lvt_7_2_ = (int)((float)lvt_7_2_ * lvt_10_3_ / lvt_11_3_);
            lvt_8_2_ = (int)((float)lvt_8_2_ * lvt_10_3_ / lvt_11_3_);
            lvt_9_3_ = (int)((float)lvt_9_3_ * lvt_10_3_ / lvt_11_3_);
            int lvt_12_3_ = (lvt_7_2_ << 8) + lvt_8_2_;
            lvt_12_3_ = (lvt_12_3_ << 8) + lvt_9_3_;
            lvt_6_1_.setColor(lvt_2_1_, lvt_12_3_);
            return lvt_2_1_;
        }
    }

    /**
     * Returns the size of the recipe area
     */
    public int getRecipeSize()
    {
        return 10;
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
