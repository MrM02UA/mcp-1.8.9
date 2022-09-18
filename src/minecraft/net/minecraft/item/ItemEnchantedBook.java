package net.minecraft.item;

import java.util.List;
import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.WeightedRandomChestContent;

public class ItemEnchantedBook extends Item
{
    public boolean hasEffect(ItemStack stack)
    {
        return true;
    }

    /**
     * Checks isDamagable and if it cannot be stacked
     */
    public boolean isItemTool(ItemStack stack)
    {
        return false;
    }

    /**
     * Return an item rarity from EnumRarity
     */
    public EnumRarity getRarity(ItemStack stack)
    {
        return this.getEnchantments(stack).tagCount() > 0 ? EnumRarity.UNCOMMON : super.getRarity(stack);
    }

    public NBTTagList getEnchantments(ItemStack stack)
    {
        NBTTagCompound lvt_2_1_ = stack.getTagCompound();
        return lvt_2_1_ != null && lvt_2_1_.hasKey("StoredEnchantments", 9) ? (NBTTagList)lvt_2_1_.getTag("StoredEnchantments") : new NBTTagList();
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        super.addInformation(stack, playerIn, tooltip, advanced);
        NBTTagList lvt_5_1_ = this.getEnchantments(stack);

        if (lvt_5_1_ != null)
        {
            for (int lvt_6_1_ = 0; lvt_6_1_ < lvt_5_1_.tagCount(); ++lvt_6_1_)
            {
                int lvt_7_1_ = lvt_5_1_.getCompoundTagAt(lvt_6_1_).getShort("id");
                int lvt_8_1_ = lvt_5_1_.getCompoundTagAt(lvt_6_1_).getShort("lvl");

                if (Enchantment.getEnchantmentById(lvt_7_1_) != null)
                {
                    tooltip.add(Enchantment.getEnchantmentById(lvt_7_1_).getTranslatedName(lvt_8_1_));
                }
            }
        }
    }

    /**
     * Adds an stored enchantment to an enchanted book ItemStack
     */
    public void addEnchantment(ItemStack stack, EnchantmentData enchantment)
    {
        NBTTagList lvt_3_1_ = this.getEnchantments(stack);
        boolean lvt_4_1_ = true;

        for (int lvt_5_1_ = 0; lvt_5_1_ < lvt_3_1_.tagCount(); ++lvt_5_1_)
        {
            NBTTagCompound lvt_6_1_ = lvt_3_1_.getCompoundTagAt(lvt_5_1_);

            if (lvt_6_1_.getShort("id") == enchantment.enchantmentobj.effectId)
            {
                if (lvt_6_1_.getShort("lvl") < enchantment.enchantmentLevel)
                {
                    lvt_6_1_.setShort("lvl", (short)enchantment.enchantmentLevel);
                }

                lvt_4_1_ = false;
                break;
            }
        }

        if (lvt_4_1_)
        {
            NBTTagCompound lvt_5_2_ = new NBTTagCompound();
            lvt_5_2_.setShort("id", (short)enchantment.enchantmentobj.effectId);
            lvt_5_2_.setShort("lvl", (short)enchantment.enchantmentLevel);
            lvt_3_1_.appendTag(lvt_5_2_);
        }

        if (!stack.hasTagCompound())
        {
            stack.setTagCompound(new NBTTagCompound());
        }

        stack.getTagCompound().setTag("StoredEnchantments", lvt_3_1_);
    }

    /**
     * Returns the ItemStack of an enchanted version of this item.
     */
    public ItemStack getEnchantedItemStack(EnchantmentData data)
    {
        ItemStack lvt_2_1_ = new ItemStack(this);
        this.addEnchantment(lvt_2_1_, data);
        return lvt_2_1_;
    }

    public void getAll(Enchantment enchantment, List<ItemStack> list)
    {
        for (int lvt_3_1_ = enchantment.getMinLevel(); lvt_3_1_ <= enchantment.getMaxLevel(); ++lvt_3_1_)
        {
            list.add(this.getEnchantedItemStack(new EnchantmentData(enchantment, lvt_3_1_)));
        }
    }

    public WeightedRandomChestContent getRandom(Random rand)
    {
        return this.getRandom(rand, 1, 1, 1);
    }

    public WeightedRandomChestContent getRandom(Random rand, int minChance, int maxChance, int weight)
    {
        ItemStack lvt_5_1_ = new ItemStack(Items.book, 1, 0);
        EnchantmentHelper.addRandomEnchantment(rand, lvt_5_1_, 30);
        return new WeightedRandomChestContent(lvt_5_1_, minChance, maxChance, weight);
    }
}
