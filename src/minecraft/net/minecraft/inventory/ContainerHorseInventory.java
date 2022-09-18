package net.minecraft.inventory;

import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ContainerHorseInventory extends Container
{
    private IInventory horseInventory;
    private EntityHorse theHorse;

    public ContainerHorseInventory(IInventory playerInventory, final IInventory horseInventoryIn, final EntityHorse horse, EntityPlayer player)
    {
        this.horseInventory = horseInventoryIn;
        this.theHorse = horse;
        int lvt_5_1_ = 3;
        horseInventoryIn.openInventory(player);
        int lvt_6_1_ = (lvt_5_1_ - 4) * 18;
        this.addSlotToContainer(new Slot(horseInventoryIn, 0, 8, 18)
        {
            public boolean isItemValid(ItemStack stack)
            {
                return super.isItemValid(stack) && stack.getItem() == Items.saddle && !this.getHasStack();
            }
        });
        this.addSlotToContainer(new Slot(horseInventoryIn, 1, 8, 36)
        {
            public boolean isItemValid(ItemStack stack)
            {
                return super.isItemValid(stack) && horse.canWearArmor() && EntityHorse.isArmorItem(stack.getItem());
            }
            public boolean canBeHovered()
            {
                return horse.canWearArmor();
            }
        });

        if (horse.isChested())
        {
            for (int lvt_7_1_ = 0; lvt_7_1_ < lvt_5_1_; ++lvt_7_1_)
            {
                for (int lvt_8_1_ = 0; lvt_8_1_ < 5; ++lvt_8_1_)
                {
                    this.addSlotToContainer(new Slot(horseInventoryIn, 2 + lvt_8_1_ + lvt_7_1_ * 5, 80 + lvt_8_1_ * 18, 18 + lvt_7_1_ * 18));
                }
            }
        }

        for (int lvt_7_2_ = 0; lvt_7_2_ < 3; ++lvt_7_2_)
        {
            for (int lvt_8_2_ = 0; lvt_8_2_ < 9; ++lvt_8_2_)
            {
                this.addSlotToContainer(new Slot(playerInventory, lvt_8_2_ + lvt_7_2_ * 9 + 9, 8 + lvt_8_2_ * 18, 102 + lvt_7_2_ * 18 + lvt_6_1_));
            }
        }

        for (int lvt_7_3_ = 0; lvt_7_3_ < 9; ++lvt_7_3_)
        {
            this.addSlotToContainer(new Slot(playerInventory, lvt_7_3_, 8 + lvt_7_3_ * 18, 160 + lvt_6_1_));
        }
    }

    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.horseInventory.isUseableByPlayer(playerIn) && this.theHorse.isEntityAlive() && this.theHorse.getDistanceToEntity(playerIn) < 8.0F;
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

            if (index < this.horseInventory.getSizeInventory())
            {
                if (!this.mergeItemStack(lvt_5_1_, this.horseInventory.getSizeInventory(), this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (this.getSlot(1).isItemValid(lvt_5_1_) && !this.getSlot(1).getHasStack())
            {
                if (!this.mergeItemStack(lvt_5_1_, 1, 2, false))
                {
                    return null;
                }
            }
            else if (this.getSlot(0).isItemValid(lvt_5_1_))
            {
                if (!this.mergeItemStack(lvt_5_1_, 0, 1, false))
                {
                    return null;
                }
            }
            else if (this.horseInventory.getSizeInventory() <= 2 || !this.mergeItemStack(lvt_5_1_, 2, this.horseInventory.getSizeInventory(), false))
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
        this.horseInventory.closeInventory(playerIn);
    }
}
