package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ContainerHopper extends Container
{
    private final IInventory hopperInventory;

    public ContainerHopper(InventoryPlayer playerInventory, IInventory hopperInventoryIn, EntityPlayer player)
    {
        this.hopperInventory = hopperInventoryIn;
        hopperInventoryIn.openInventory(player);
        int lvt_4_1_ = 51;

        for (int lvt_5_1_ = 0; lvt_5_1_ < hopperInventoryIn.getSizeInventory(); ++lvt_5_1_)
        {
            this.addSlotToContainer(new Slot(hopperInventoryIn, lvt_5_1_, 44 + lvt_5_1_ * 18, 20));
        }

        for (int lvt_5_2_ = 0; lvt_5_2_ < 3; ++lvt_5_2_)
        {
            for (int lvt_6_1_ = 0; lvt_6_1_ < 9; ++lvt_6_1_)
            {
                this.addSlotToContainer(new Slot(playerInventory, lvt_6_1_ + lvt_5_2_ * 9 + 9, 8 + lvt_6_1_ * 18, lvt_5_2_ * 18 + lvt_4_1_));
            }
        }

        for (int lvt_5_3_ = 0; lvt_5_3_ < 9; ++lvt_5_3_)
        {
            this.addSlotToContainer(new Slot(playerInventory, lvt_5_3_, 8 + lvt_5_3_ * 18, 58 + lvt_4_1_));
        }
    }

    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.hopperInventory.isUseableByPlayer(playerIn);
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

            if (index < this.hopperInventory.getSizeInventory())
            {
                if (!this.mergeItemStack(lvt_5_1_, this.hopperInventory.getSizeInventory(), this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(lvt_5_1_, 0, this.hopperInventory.getSizeInventory(), false))
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
        }

        return lvt_3_1_;
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);
        this.hopperInventory.closeInventory(playerIn);
    }
}
