package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ContainerBeacon extends Container
{
    private IInventory tileBeacon;

    /**
     * This beacon's slot where you put in Emerald, Diamond, Gold or Iron Ingot.
     */
    private final ContainerBeacon.BeaconSlot beaconSlot;

    public ContainerBeacon(IInventory playerInventory, IInventory tileBeaconIn)
    {
        this.tileBeacon = tileBeaconIn;
        this.addSlotToContainer(this.beaconSlot = new ContainerBeacon.BeaconSlot(tileBeaconIn, 0, 136, 110));
        int lvt_3_1_ = 36;
        int lvt_4_1_ = 137;

        for (int lvt_5_1_ = 0; lvt_5_1_ < 3; ++lvt_5_1_)
        {
            for (int lvt_6_1_ = 0; lvt_6_1_ < 9; ++lvt_6_1_)
            {
                this.addSlotToContainer(new Slot(playerInventory, lvt_6_1_ + lvt_5_1_ * 9 + 9, lvt_3_1_ + lvt_6_1_ * 18, lvt_4_1_ + lvt_5_1_ * 18));
            }
        }

        for (int lvt_5_2_ = 0; lvt_5_2_ < 9; ++lvt_5_2_)
        {
            this.addSlotToContainer(new Slot(playerInventory, lvt_5_2_, lvt_3_1_ + lvt_5_2_ * 18, 58 + lvt_4_1_));
        }
    }

    public void onCraftGuiOpened(ICrafting listener)
    {
        super.onCraftGuiOpened(listener);
        listener.sendAllWindowProperties(this, this.tileBeacon);
    }

    public void updateProgressBar(int id, int data)
    {
        this.tileBeacon.setField(id, data);
    }

    public IInventory func_180611_e()
    {
        return this.tileBeacon;
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);

        if (playerIn != null && !playerIn.worldObj.isRemote)
        {
            ItemStack lvt_2_1_ = this.beaconSlot.decrStackSize(this.beaconSlot.getSlotStackLimit());

            if (lvt_2_1_ != null)
            {
                playerIn.dropPlayerItemWithRandomChoice(lvt_2_1_, false);
            }
        }
    }

    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.tileBeacon.isUseableByPlayer(playerIn);
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

            if (index == 0)
            {
                if (!this.mergeItemStack(lvt_5_1_, 1, 37, true))
                {
                    return null;
                }

                lvt_4_1_.onSlotChange(lvt_5_1_, lvt_3_1_);
            }
            else if (!this.beaconSlot.getHasStack() && this.beaconSlot.isItemValid(lvt_5_1_) && lvt_5_1_.stackSize == 1)
            {
                if (!this.mergeItemStack(lvt_5_1_, 0, 1, false))
                {
                    return null;
                }
            }
            else if (index >= 1 && index < 28)
            {
                if (!this.mergeItemStack(lvt_5_1_, 28, 37, false))
                {
                    return null;
                }
            }
            else if (index >= 28 && index < 37)
            {
                if (!this.mergeItemStack(lvt_5_1_, 1, 28, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(lvt_5_1_, 1, 37, false))
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

    class BeaconSlot extends Slot
    {
        public BeaconSlot(IInventory p_i1801_2_, int p_i1801_3_, int p_i1801_4_, int p_i1801_5_)
        {
            super(p_i1801_2_, p_i1801_3_, p_i1801_4_, p_i1801_5_);
        }

        public boolean isItemValid(ItemStack stack)
        {
            return stack == null ? false : stack.getItem() == Items.emerald || stack.getItem() == Items.diamond || stack.getItem() == Items.gold_ingot || stack.getItem() == Items.iron_ingot;
        }

        public int getSlotStackLimit()
        {
            return 1;
        }
    }
}
