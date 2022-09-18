package net.minecraft.util;

import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

public class WeightedRandomFishable extends WeightedRandom.Item
{
    private final ItemStack returnStack;
    private float maxDamagePercent;
    private boolean enchantable;

    public WeightedRandomFishable(ItemStack returnStackIn, int itemWeightIn)
    {
        super(itemWeightIn);
        this.returnStack = returnStackIn;
    }

    public ItemStack getItemStack(Random random)
    {
        ItemStack lvt_2_1_ = this.returnStack.copy();

        if (this.maxDamagePercent > 0.0F)
        {
            int lvt_3_1_ = (int)(this.maxDamagePercent * (float)this.returnStack.getMaxDamage());
            int lvt_4_1_ = lvt_2_1_.getMaxDamage() - random.nextInt(random.nextInt(lvt_3_1_) + 1);

            if (lvt_4_1_ > lvt_3_1_)
            {
                lvt_4_1_ = lvt_3_1_;
            }

            if (lvt_4_1_ < 1)
            {
                lvt_4_1_ = 1;
            }

            lvt_2_1_.setItemDamage(lvt_4_1_);
        }

        if (this.enchantable)
        {
            EnchantmentHelper.addRandomEnchantment(random, lvt_2_1_, 30);
        }

        return lvt_2_1_;
    }

    public WeightedRandomFishable setMaxDamagePercent(float maxDamagePercentIn)
    {
        this.maxDamagePercent = maxDamagePercentIn;
        return this;
    }

    public WeightedRandomFishable setEnchantable()
    {
        this.enchantable = true;
        return this;
    }
}
