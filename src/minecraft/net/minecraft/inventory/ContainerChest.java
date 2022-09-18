package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerChest extends Container
{
    private IInventory lowerChestInventory;
    private int numRows;

    public ContainerChest(IInventory playerInventory, IInventory chestInventory, EntityPlayer player)
    {
        this.lowerChestInventory = chestInventory;
        this.numRows = chestInventory.getSizeInventory() / 9;
        chestInventory.openInventory(player);
        int lvt_4_1_ = (this.numRows - 4) * 18;

        for (int lvt_5_1_ = 0; lvt_5_1_ < this.numRows; ++lvt_5_1_)
        {
            for (int lvt_6_1_ = 0; lvt_6_1_ < 9; ++lvt_6_1_)
            {
                this.addSlotToContainer(new Slot(chestInventory, lvt_6_1_ + lvt_5_1_ * 9, 8 + lvt_6_1_ * 18, 18 + lvt_5_1_ * 18));
            }
        }

        for (int lvt_5_2_ = 0; lvt_5_2_ < 3; ++lvt_5_2_)
        {
            for (int lvt_6_2_ = 0; lvt_6_2_ < 9; ++lvt_6_2_)
            {
                this.addSlotToContainer(new Slot(playerInventory, lvt_6_2_ + lvt_5_2_ * 9 + 9, 8 + lvt_6_2_ * 18, 103 + lvt_5_2_ * 18 + lvt_4_1_));
            }
        }

        for (int lvt_5_3_ = 0; lvt_5_3_ < 9; ++lvt_5_3_)
        {
            this.addSlotToContainer(new Slot(playerInventory, lvt_5_3_, 8 + lvt_5_3_ * 18, 161 + lvt_4_1_));
        }
    }

    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.lowerChestInventory.isUseableByPlayer(playerIn);
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

            if (index < this.numRows * 9)
            {
                if (!this.mergeItemStack(lvt_5_1_, this.numRows * 9, this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(lvt_5_1_, 0, this.numRows * 9, false))
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
        this.lowerChestInventory.closeInventory(playerIn);
    }

    /**
     * Return this chest container's lower chest inventory.
     */
    public IInventory getLowerChestInventory()
    {
        return this.lowerChestInventory;
    }
}
