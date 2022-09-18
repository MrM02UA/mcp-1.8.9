package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class ItemBow extends Item
{
    public static final String[] bowPullIconNameArray = new String[] {"pulling_0", "pulling_1", "pulling_2"};

    public ItemBow()
    {
        this.maxStackSize = 1;
        this.setMaxDamage(384);
        this.setCreativeTab(CreativeTabs.tabCombat);
    }

    /**
     * Called when the player stops using an Item (stops holding the right mouse button).
     */
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityPlayer playerIn, int timeLeft)
    {
        boolean lvt_5_1_ = playerIn.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;

        if (lvt_5_1_ || playerIn.inventory.hasItem(Items.arrow))
        {
            int lvt_6_1_ = this.getMaxItemUseDuration(stack) - timeLeft;
            float lvt_7_1_ = (float)lvt_6_1_ / 20.0F;
            lvt_7_1_ = (lvt_7_1_ * lvt_7_1_ + lvt_7_1_ * 2.0F) / 3.0F;

            if ((double)lvt_7_1_ < 0.1D)
            {
                return;
            }

            if (lvt_7_1_ > 1.0F)
            {
                lvt_7_1_ = 1.0F;
            }

            EntityArrow lvt_8_1_ = new EntityArrow(worldIn, playerIn, lvt_7_1_ * 2.0F);

            if (lvt_7_1_ == 1.0F)
            {
                lvt_8_1_.setIsCritical(true);
            }

            int lvt_9_1_ = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);

            if (lvt_9_1_ > 0)
            {
                lvt_8_1_.setDamage(lvt_8_1_.getDamage() + (double)lvt_9_1_ * 0.5D + 0.5D);
            }

            int lvt_10_1_ = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);

            if (lvt_10_1_ > 0)
            {
                lvt_8_1_.setKnockbackStrength(lvt_10_1_);
            }

            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0)
            {
                lvt_8_1_.setFire(100);
            }

            stack.damageItem(1, playerIn);
            worldIn.playSoundAtEntity(playerIn, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + lvt_7_1_ * 0.5F);

            if (lvt_5_1_)
            {
                lvt_8_1_.canBePickedUp = 2;
            }
            else
            {
                playerIn.inventory.consumeInventoryItem(Items.arrow);
            }

            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);

            if (!worldIn.isRemote)
            {
                worldIn.spawnEntityInWorld(lvt_8_1_);
            }
        }
    }

    /**
     * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
     * the Item before the action is complete.
     */
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn)
    {
        return stack;
    }

    /**
     * How long it takes to use or consume an item
     */
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 72000;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.BOW;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
        if (playerIn.capabilities.isCreativeMode || playerIn.inventory.hasItem(Items.arrow))
        {
            playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
        }

        return itemStackIn;
    }

    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    public int getItemEnchantability()
    {
        return 1;
    }
}
