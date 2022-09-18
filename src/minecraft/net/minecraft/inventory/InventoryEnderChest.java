package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityEnderChest;

public class InventoryEnderChest extends InventoryBasic
{
    private TileEntityEnderChest associatedChest;

    public InventoryEnderChest()
    {
        super("container.enderchest", false, 27);
    }

    public void setChestTileEntity(TileEntityEnderChest chestTileEntity)
    {
        this.associatedChest = chestTileEntity;
    }

    public void loadInventoryFromNBT(NBTTagList p_70486_1_)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < this.getSizeInventory(); ++lvt_2_1_)
        {
            this.setInventorySlotContents(lvt_2_1_, (ItemStack)null);
        }

        for (int lvt_2_2_ = 0; lvt_2_2_ < p_70486_1_.tagCount(); ++lvt_2_2_)
        {
            NBTTagCompound lvt_3_1_ = p_70486_1_.getCompoundTagAt(lvt_2_2_);
            int lvt_4_1_ = lvt_3_1_.getByte("Slot") & 255;

            if (lvt_4_1_ >= 0 && lvt_4_1_ < this.getSizeInventory())
            {
                this.setInventorySlotContents(lvt_4_1_, ItemStack.loadItemStackFromNBT(lvt_3_1_));
            }
        }
    }

    public NBTTagList saveInventoryToNBT()
    {
        NBTTagList lvt_1_1_ = new NBTTagList();

        for (int lvt_2_1_ = 0; lvt_2_1_ < this.getSizeInventory(); ++lvt_2_1_)
        {
            ItemStack lvt_3_1_ = this.getStackInSlot(lvt_2_1_);

            if (lvt_3_1_ != null)
            {
                NBTTagCompound lvt_4_1_ = new NBTTagCompound();
                lvt_4_1_.setByte("Slot", (byte)lvt_2_1_);
                lvt_3_1_.writeToNBT(lvt_4_1_);
                lvt_1_1_.appendTag(lvt_4_1_);
            }
        }

        return lvt_1_1_;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return this.associatedChest != null && !this.associatedChest.canBeUsed(player) ? false : super.isUseableByPlayer(player);
    }

    public void openInventory(EntityPlayer player)
    {
        if (this.associatedChest != null)
        {
            this.associatedChest.openChest();
        }

        super.openInventory(player);
    }

    public void closeInventory(EntityPlayer player)
    {
        if (this.associatedChest != null)
        {
            this.associatedChest.closeChest();
        }

        super.closeInventory(player);
        this.associatedChest = null;
    }
}
