package net.minecraft.enchantment;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public enum EnumEnchantmentType
{
    ALL,
    ARMOR,
    ARMOR_FEET,
    ARMOR_LEGS,
    ARMOR_TORSO,
    ARMOR_HEAD,
    WEAPON,
    DIGGER,
    FISHING_ROD,
    BREAKABLE,
    BOW;

    /**
     * Return true if the item passed can be enchanted by a enchantment of this type.
     */
    public boolean canEnchantItem(Item p_77557_1_)
    {
        if (this == ALL)
        {
            return true;
        }
        else if (this == BREAKABLE && p_77557_1_.isDamageable())
        {
            return true;
        }
        else if (p_77557_1_ instanceof ItemArmor)
        {
            if (this == ARMOR)
            {
                return true;
            }
            else
            {
                ItemArmor lvt_2_1_ = (ItemArmor)p_77557_1_;
                return lvt_2_1_.armorType == 0 ? this == ARMOR_HEAD : (lvt_2_1_.armorType == 2 ? this == ARMOR_LEGS : (lvt_2_1_.armorType == 1 ? this == ARMOR_TORSO : (lvt_2_1_.armorType == 3 ? this == ARMOR_FEET : false)));
            }
        }
        else
        {
            return p_77557_1_ instanceof ItemSword ? this == WEAPON : (p_77557_1_ instanceof ItemTool ? this == DIGGER : (p_77557_1_ instanceof ItemBow ? this == BOW : (p_77557_1_ instanceof ItemFishingRod ? this == FISHING_ROD : false)));
        }
    }
}
