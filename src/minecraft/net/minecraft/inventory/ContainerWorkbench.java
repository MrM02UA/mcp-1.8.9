package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ContainerWorkbench extends Container
{
    /** The crafting matrix inventory (3x3). */
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
    public IInventory craftResult = new InventoryCraftResult();
    private World worldObj;

    /** Position of the workbench */
    private BlockPos pos;

    public ContainerWorkbench(InventoryPlayer playerInventory, World worldIn, BlockPos posIn)
    {
        this.worldObj = worldIn;
        this.pos = posIn;
        this.addSlotToContainer(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 124, 35));

        for (int lvt_4_1_ = 0; lvt_4_1_ < 3; ++lvt_4_1_)
        {
            for (int lvt_5_1_ = 0; lvt_5_1_ < 3; ++lvt_5_1_)
            {
                this.addSlotToContainer(new Slot(this.craftMatrix, lvt_5_1_ + lvt_4_1_ * 3, 30 + lvt_5_1_ * 18, 17 + lvt_4_1_ * 18));
            }
        }

        for (int lvt_4_2_ = 0; lvt_4_2_ < 3; ++lvt_4_2_)
        {
            for (int lvt_5_2_ = 0; lvt_5_2_ < 9; ++lvt_5_2_)
            {
                this.addSlotToContainer(new Slot(playerInventory, lvt_5_2_ + lvt_4_2_ * 9 + 9, 8 + lvt_5_2_ * 18, 84 + lvt_4_2_ * 18));
            }
        }

        for (int lvt_4_3_ = 0; lvt_4_3_ < 9; ++lvt_4_3_)
        {
            this.addSlotToContainer(new Slot(playerInventory, lvt_4_3_, 8 + lvt_4_3_ * 18, 142));
        }

        this.onCraftMatrixChanged(this.craftMatrix);
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.worldObj));
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);

        if (!this.worldObj.isRemote)
        {
            for (int lvt_2_1_ = 0; lvt_2_1_ < 9; ++lvt_2_1_)
            {
                ItemStack lvt_3_1_ = this.craftMatrix.removeStackFromSlot(lvt_2_1_);

                if (lvt_3_1_ != null)
                {
                    playerIn.dropPlayerItemWithRandomChoice(lvt_3_1_, false);
                }
            }
        }
    }

    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.worldObj.getBlockState(this.pos).getBlock() != Blocks.crafting_table ? false : playerIn.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
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
                if (!this.mergeItemStack(lvt_5_1_, 10, 46, true))
                {
                    return null;
                }

                lvt_4_1_.onSlotChange(lvt_5_1_, lvt_3_1_);
            }
            else if (index >= 10 && index < 37)
            {
                if (!this.mergeItemStack(lvt_5_1_, 37, 46, false))
                {
                    return null;
                }
            }
            else if (index >= 37 && index < 46)
            {
                if (!this.mergeItemStack(lvt_5_1_, 10, 37, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(lvt_5_1_, 10, 46, false))
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
