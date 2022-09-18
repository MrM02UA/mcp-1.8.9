package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;

public class ContainerFurnace extends Container
{
    private final IInventory tileFurnace;
    private int cookTime;
    private int totalCookTime;
    private int furnaceBurnTime;
    private int currentItemBurnTime;

    public ContainerFurnace(InventoryPlayer playerInventory, IInventory furnaceInventory)
    {
        this.tileFurnace = furnaceInventory;
        this.addSlotToContainer(new Slot(furnaceInventory, 0, 56, 17));
        this.addSlotToContainer(new SlotFurnaceFuel(furnaceInventory, 1, 56, 53));
        this.addSlotToContainer(new SlotFurnaceOutput(playerInventory.player, furnaceInventory, 2, 116, 35));

        for (int lvt_3_1_ = 0; lvt_3_1_ < 3; ++lvt_3_1_)
        {
            for (int lvt_4_1_ = 0; lvt_4_1_ < 9; ++lvt_4_1_)
            {
                this.addSlotToContainer(new Slot(playerInventory, lvt_4_1_ + lvt_3_1_ * 9 + 9, 8 + lvt_4_1_ * 18, 84 + lvt_3_1_ * 18));
            }
        }

        for (int lvt_3_2_ = 0; lvt_3_2_ < 9; ++lvt_3_2_)
        {
            this.addSlotToContainer(new Slot(playerInventory, lvt_3_2_, 8 + lvt_3_2_ * 18, 142));
        }
    }

    public void onCraftGuiOpened(ICrafting listener)
    {
        super.onCraftGuiOpened(listener);
        listener.sendAllWindowProperties(this, this.tileFurnace);
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int lvt_1_1_ = 0; lvt_1_1_ < this.crafters.size(); ++lvt_1_1_)
        {
            ICrafting lvt_2_1_ = (ICrafting)this.crafters.get(lvt_1_1_);

            if (this.cookTime != this.tileFurnace.getField(2))
            {
                lvt_2_1_.sendProgressBarUpdate(this, 2, this.tileFurnace.getField(2));
            }

            if (this.furnaceBurnTime != this.tileFurnace.getField(0))
            {
                lvt_2_1_.sendProgressBarUpdate(this, 0, this.tileFurnace.getField(0));
            }

            if (this.currentItemBurnTime != this.tileFurnace.getField(1))
            {
                lvt_2_1_.sendProgressBarUpdate(this, 1, this.tileFurnace.getField(1));
            }

            if (this.totalCookTime != this.tileFurnace.getField(3))
            {
                lvt_2_1_.sendProgressBarUpdate(this, 3, this.tileFurnace.getField(3));
            }
        }

        this.cookTime = this.tileFurnace.getField(2);
        this.furnaceBurnTime = this.tileFurnace.getField(0);
        this.currentItemBurnTime = this.tileFurnace.getField(1);
        this.totalCookTime = this.tileFurnace.getField(3);
    }

    public void updateProgressBar(int id, int data)
    {
        this.tileFurnace.setField(id, data);
    }

    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.tileFurnace.isUseableByPlayer(playerIn);
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

            if (index == 2)
            {
                if (!this.mergeItemStack(lvt_5_1_, 3, 39, true))
                {
                    return null;
                }

                lvt_4_1_.onSlotChange(lvt_5_1_, lvt_3_1_);
            }
            else if (index != 1 && index != 0)
            {
                if (FurnaceRecipes.instance().getSmeltingResult(lvt_5_1_) != null)
                {
                    if (!this.mergeItemStack(lvt_5_1_, 0, 1, false))
                    {
                        return null;
                    }
                }
                else if (TileEntityFurnace.isItemFuel(lvt_5_1_))
                {
                    if (!this.mergeItemStack(lvt_5_1_, 1, 2, false))
                    {
                        return null;
                    }
                }
                else if (index >= 3 && index < 30)
                {
                    if (!this.mergeItemStack(lvt_5_1_, 30, 39, false))
                    {
                        return null;
                    }
                }
                else if (index >= 30 && index < 39 && !this.mergeItemStack(lvt_5_1_, 3, 30, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(lvt_5_1_, 3, 39, false))
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
