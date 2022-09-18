package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.village.MerchantRecipe;

public class SlotMerchantResult extends Slot
{
    /** Merchant's inventory. */
    private final InventoryMerchant theMerchantInventory;

    /** The Player whos trying to buy/sell stuff. */
    private EntityPlayer thePlayer;
    private int field_75231_g;

    /** "Instance" of the Merchant. */
    private final IMerchant theMerchant;

    public SlotMerchantResult(EntityPlayer player, IMerchant merchant, InventoryMerchant merchantInventory, int slotIndex, int xPosition, int yPosition)
    {
        super(merchantInventory, slotIndex, xPosition, yPosition);
        this.thePlayer = player;
        this.theMerchant = merchant;
        this.theMerchantInventory = merchantInventory;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(ItemStack stack)
    {
        return false;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    public ItemStack decrStackSize(int amount)
    {
        if (this.getHasStack())
        {
            this.field_75231_g += Math.min(amount, this.getStack().stackSize);
        }

        return super.decrStackSize(amount);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
     * internal count then calls onCrafting(item).
     */
    protected void onCrafting(ItemStack stack, int amount)
    {
        this.field_75231_g += amount;
        this.onCrafting(stack);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
     */
    protected void onCrafting(ItemStack stack)
    {
        stack.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.field_75231_g);
        this.field_75231_g = 0;
    }

    public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack)
    {
        this.onCrafting(stack);
        MerchantRecipe lvt_3_1_ = this.theMerchantInventory.getCurrentRecipe();

        if (lvt_3_1_ != null)
        {
            ItemStack lvt_4_1_ = this.theMerchantInventory.getStackInSlot(0);
            ItemStack lvt_5_1_ = this.theMerchantInventory.getStackInSlot(1);

            if (this.doTrade(lvt_3_1_, lvt_4_1_, lvt_5_1_) || this.doTrade(lvt_3_1_, lvt_5_1_, lvt_4_1_))
            {
                this.theMerchant.useRecipe(lvt_3_1_);
                playerIn.triggerAchievement(StatList.timesTradedWithVillagerStat);

                if (lvt_4_1_ != null && lvt_4_1_.stackSize <= 0)
                {
                    lvt_4_1_ = null;
                }

                if (lvt_5_1_ != null && lvt_5_1_.stackSize <= 0)
                {
                    lvt_5_1_ = null;
                }

                this.theMerchantInventory.setInventorySlotContents(0, lvt_4_1_);
                this.theMerchantInventory.setInventorySlotContents(1, lvt_5_1_);
            }
        }
    }

    private boolean doTrade(MerchantRecipe trade, ItemStack firstItem, ItemStack secondItem)
    {
        ItemStack lvt_4_1_ = trade.getItemToBuy();
        ItemStack lvt_5_1_ = trade.getSecondItemToBuy();

        if (firstItem != null && firstItem.getItem() == lvt_4_1_.getItem())
        {
            if (lvt_5_1_ != null && secondItem != null && lvt_5_1_.getItem() == secondItem.getItem())
            {
                firstItem.stackSize -= lvt_4_1_.stackSize;
                secondItem.stackSize -= lvt_5_1_.stackSize;
                return true;
            }

            if (lvt_5_1_ == null && secondItem == null)
            {
                firstItem.stackSize -= lvt_4_1_.stackSize;
                return true;
            }
        }

        return false;
    }
}
