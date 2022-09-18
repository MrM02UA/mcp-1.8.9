package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerDispenser extends Container
{
    private IInventory dispenserInventory;

    public ContainerDispenser(IInventory playerInventory, IInventory dispenserInventoryIn)
    {
        this.dispenserInventory = dispenserInventoryIn;

        for (int lvt_3_1_ = 0; lvt_3_1_ < 3; ++lvt_3_1_)
        {
            for (int lvt_4_1_ = 0; lvt_4_1_ < 3; ++lvt_4_1_)
            {
                this.addSlotToContainer(new Slot(dispenserInventoryIn, lvt_4_1_ + lvt_3_1_ * 3, 62 + lvt_4_1_ * 18, 17 + lvt_3_1_ * 18));
            }
        }

        for (int lvt_3_2_ = 0; lvt_3_2_ < 3; ++lvt_3_2_)
        {
            for (int lvt_4_2_ = 0; lvt_4_2_ < 9; ++lvt_4_2_)
            {
                this.addSlotToContainer(new Slot(playerInventory, lvt_4_2_ + lvt_3_2_ * 9 + 9, 8 + lvt_4_2_ * 18, 84 + lvt_3_2_ * 18));
            }
        }

        for (int lvt_3_3_ = 0; lvt_3_3_ < 9; ++lvt_3_3_)
        {
            this.addSlotToContainer(new Slot(playerInventory, lvt_3_3_, 8 + lvt_3_3_ * 18, 142));
        }
    }

    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.dispenserInventory.isUseableByPlayer(playerIn);
    }

    /**
     * Take a stack from the specified inventory slot.
     */
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack lvt_3_1_ = null;
        Slot lvt_4_1_ = (Slot)this.inventorySlots.get(index);

        if (lvt_4_1_ != null && lvt_4_1_.getHasStack())
        {
            ItemStack lvt_5_1_ = lvt_4_1_.getStack();
            lvt_3_1_ = lvt_5_1_.copy();

            if (index < 9)
            {
                if (!this.mergeItemStack(lvt_5_1_, 9, 45, true))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(lvt_5_1_, 0, 9, false))
            {
                return null;
            }

            if (lvt_5_1_.stackSize == 0)
            {
                lvt_4_1_.putStack((ItemStack)null);
            }
            else
            {
                lvt_4_1_.onSlotChanged();
            }

            if (lvt_5_1_.stackSize == lvt_3_1_.stackSize)
            {
                return null;
            }

            lvt_4_1_.onPickupFromSlot(playerIn, lvt_5_1_);
        }

        return lvt_3_1_;
    }
}
