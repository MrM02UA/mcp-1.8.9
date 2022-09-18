package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

public class ContainerPlayer extends Container
{
    /** The crafting matrix inventory. */
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
    public IInventory craftResult = new InventoryCraftResult();

    /** Determines if inventory manipulation should be handled. */
    public boolean isLocalWorld;
    private final EntityPlayer thePlayer;

    public ContainerPlayer(final InventoryPlayer playerInventory, boolean localWorld, EntityPlayer player)
    {
        this.isLocalWorld = localWorld;
        this.thePlayer = player;
        this.addSlotToContainer(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 144, 36));

        for (int lvt_4_1_ = 0; lvt_4_1_ < 2; ++lvt_4_1_)
        {
            for (int lvt_5_1_ = 0; lvt_5_1_ < 2; ++lvt_5_1_)
            {
                this.addSlotToContainer(new Slot(this.craftMatrix, lvt_5_1_ + lvt_4_1_ * 2, 88 + lvt_5_1_ * 18, 26 + lvt_4_1_ * 18));
            }
        }

        for (final int lvt_4_2_ = 0; lvt_4_2_ < 4; ++lvt_4_2_)
        {
            this.addSlotToContainer(new Slot(playerInventory, playerInventory.getSizeInventory() - 1 - lvt_4_2_, 8, 8 + lvt_4_2_ * 18)
            {
                public int getSlotStackLimit()
                {
                    return 1;
                }
                public boolean isItemValid(ItemStack stack)
                {
                    return stack == null ? false : (stack.getItem() instanceof ItemArmor ? ((ItemArmor)stack.getItem()).armorType == lvt_4_2_ : (stack.getItem() != Item.getItemFromBlock(Blocks.pumpkin) && stack.getItem() != Items.skull ? false : lvt_4_2_ == 0));
                }
                public String getSlotTexture()
                {
                    return ItemArmor.EMPTY_SLOT_NAMES[lvt_4_2_];
                }
            });
        }

        for (int lvt_4_3_ = 0; lvt_4_3_ < 3; ++lvt_4_3_)
        {
            for (int lvt_5_3_ = 0; lvt_5_3_ < 9; ++lvt_5_3_)
            {
                this.addSlotToContainer(new Slot(playerInventory, lvt_5_3_ + (lvt_4_3_ + 1) * 9, 8 + lvt_5_3_ * 18, 84 + lvt_4_3_ * 18));
            }
        }

        for (int lvt_4_4_ = 0; lvt_4_4_ < 9; ++lvt_4_4_)
        {
            this.addSlotToContainer(new Slot(playerInventory, lvt_4_4_, 8 + lvt_4_4_ * 18, 142));
        }

        this.onCraftMatrixChanged(this.craftMatrix);
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.thePlayer.worldObj));
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);

        for (int lvt_2_1_ = 0; lvt_2_1_ < 4; ++lvt_2_1_)
        {
            ItemStack lvt_3_1_ = this.craftMatrix.removeStackFromSlot(lvt_2_1_);

            if (lvt_3_1_ != null)
            {
                playerIn.dropPlayerItemWithRandomChoice(lvt_3_1_, false);
            }
        }

        this.craftResult.setInventorySlotContents(0, (ItemStack)null);
    }

    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return true;
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
                if (!this.mergeItemStack(lvt_5_1_, 9, 45, true))
                {
                    return null;
                }

                lvt_4_1_.onSlotChange(lvt_5_1_, lvt_3_1_);
            }
            else if (index >= 1 && index < 5)
            {
                if (!this.mergeItemStack(lvt_5_1_, 9, 45, false))
                {
                    return null;
                }
            }
            else if (index >= 5 && index < 9)
            {
                if (!this.mergeItemStack(lvt_5_1_, 9, 45, false))
                {
                    return null;
                }
            }
            else if (lvt_3_1_.getItem() instanceof ItemArmor && !((Slot)this.inventorySlots.get(5 + ((ItemArmor)lvt_3_1_.getItem()).armorType)).getHasStack())
            {
                int lvt_6_1_ = 5 + ((ItemArmor)lvt_3_1_.getItem()).armorType;

                if (!this.mergeItemStack(lvt_5_1_, lvt_6_1_, lvt_6_1_ + 1, false))
                {
                    return null;
                }
            }
            else if (index >= 9 && index < 36)
            {
                if (!this.mergeItemStack(lvt_5_1_, 36, 45, false))
                {
                    return null;
                }
            }
            else if (index >= 36 && index < 45)
            {
                if (!this.mergeItemStack(lvt_5_1_, 9, 36, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(lvt_5_1_, 9, 45, false))
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
     * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in
     * is null for the initial slot that was double-clicked.
     */
    public boolean canMergeSlot(ItemStack stack, Slot slotIn)
    {
        return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
    }
}
