package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerMerchant extends Container
{
    /** Instance of Merchant. */
    private IMerchant theMerchant;
    private InventoryMerchant merchantInventory;

    /** Instance of World. */
    private final World theWorld;

    public ContainerMerchant(InventoryPlayer playerInventory, IMerchant merchant, World worldIn)
    {
        this.theMerchant = merchant;
        this.theWorld = worldIn;
        this.merchantInventory = new InventoryMerchant(playerInventory.player, merchant);
        this.addSlotToContainer(new Slot(this.merchantInventory, 0, 36, 53));
        this.addSlotToContainer(new Slot(this.merchantInventory, 1, 62, 53));
        this.addSlotToContainer(new SlotMerchantResult(playerInventory.player, merchant, this.merchantInventory, 2, 120, 53));

        for (int lvt_4_1_ = 0; lvt_4_1_ < 3; ++lvt_4_1_)
        {
            for (int lvt_5_1_ = 0; lvt_5_1_ < 9; ++lvt_5_1_)
            {
                this.addSlotToContainer(new Slot(playerInventory, lvt_5_1_ + lvt_4_1_ * 9 + 9, 8 + lvt_5_1_ * 18, 84 + lvt_4_1_ * 18));
            }
        }

        for (int lvt_4_2_ = 0; lvt_4_2_ < 9; ++lvt_4_2_)
        {
            this.addSlotToContainer(new Slot(playerInventory, lvt_4_2_, 8 + lvt_4_2_ * 18, 142));
        }
    }

    public InventoryMerchant getMerchantInventory()
    {
        return this.merchantInventory;
    }

    public void onCraftGuiOpened(ICrafting listener)
    {
        super.onCraftGuiOpened(listener);
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        this.merchantInventory.resetRecipeAndSlots();
        super.onCraftMatrixChanged(inventoryIn);
    }

    public void setCurrentRecipeIndex(int currentRecipeIndex)
    {
        this.merchantInventory.setCurrentRecipeIndex(currentRecipeIndex);
    }

    public void updateProgressBar(int id, int data)
    {
    }

    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.theMerchant.getCustomer() == playerIn;
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
            else if (index != 0 && index != 1)
            {
                if (index >= 3 && index < 30)
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

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);
        this.theMerchant.setCustomer((EntityPlayer)null);
        super.onContainerClosed(playerIn);

        if (!this.theWorld.isRemote)
        {
            ItemStack lvt_2_1_ = this.merchantInventory.removeStackFromSlot(0);

            if (lvt_2_1_ != null)
            {
                playerIn.dropPlayerItemWithRandomChoice(lvt_2_1_, false);
            }

            lvt_2_1_ = this.merchantInventory.removeStackFromSlot(1);

            if (lvt_2_1_ != null)
            {
                playerIn.dropPlayerItemWithRandomChoice(lvt_2_1_, false);
            }
        }
    }
}
