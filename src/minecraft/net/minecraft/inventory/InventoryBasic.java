package net.minecraft.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class InventoryBasic implements IInventory
{
    private String inventoryTitle;
    private int slotsCount;
    private ItemStack[] inventoryContents;
    private List<IInvBasic> changeListeners;
    private boolean hasCustomName;

    public InventoryBasic(String title, boolean customName, int slotCount)
    {
        this.inventoryTitle = title;
        this.hasCustomName = customName;
        this.slotsCount = slotCount;
        this.inventoryContents = new ItemStack[slotCount];
    }

    public InventoryBasic(IChatComponent title, int slotCount)
    {
        this(title.getUnformattedText(), true, slotCount);
    }

    /**
     * Add a listener that will be notified when any item in this inventory is modified.
     *  
     * @param listener the listener to add
     */
    public void addInventoryChangeListener(IInvBasic listener)
    {
        if (this.changeListeners == null)
        {
            this.changeListeners = Lists.newArrayList();
        }

        this.changeListeners.add(listener);
    }

    /**
     * removes the specified IInvBasic from receiving further change notices
     *  
     * @param listener the listener to remove
     */
    public void removeInventoryChangeListener(IInvBasic listener)
    {
        this.changeListeners.remove(listener);
    }

    /**
     * Returns the stack in the given slot.
     */
    public ItemStack getStackInSlot(int index)
    {
        return index >= 0 && index < this.inventoryContents.length ? this.inventoryContents[index] : null;
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    public ItemStack decrStackSize(int index, int count)
    {
        if (this.inventoryContents[index] != null)
        {
            if (this.inventoryContents[index].stackSize <= count)
            {
                ItemStack lvt_3_1_ = this.inventoryContents[index];
                this.inventoryContents[index] = null;
                this.markDirty();
                return lvt_3_1_;
            }
            else
            {
                ItemStack lvt_3_2_ = this.inventoryContents[index].splitStack(count);

                if (this.inventoryContents[index].stackSize == 0)
                {
                    this.inventoryContents[index] = null;
                }

                this.markDirty();
                return lvt_3_2_;
            }
        }
        else
        {
            return null;
        }
    }

    public ItemStack func_174894_a(ItemStack stack)
    {
        ItemStack lvt_2_1_ = stack.copy();

        for (int lvt_3_1_ = 0; lvt_3_1_ < this.slotsCount; ++lvt_3_1_)
        {
            ItemStack lvt_4_1_ = this.getStackInSlot(lvt_3_1_);

            if (lvt_4_1_ == null)
            {
                this.setInventorySlotContents(lvt_3_1_, lvt_2_1_);
                this.markDirty();
                return null;
            }

            if (ItemStack.areItemsEqual(lvt_4_1_, lvt_2_1_))
            {
                int lvt_5_1_ = Math.min(this.getInventoryStackLimit(), lvt_4_1_.getMaxStackSize());
                int lvt_6_1_ = Math.min(lvt_2_1_.stackSize, lvt_5_1_ - lvt_4_1_.stackSize);

                if (lvt_6_1_ > 0)
                {
                    lvt_4_1_.stackSize += lvt_6_1_;
                    lvt_2_1_.stackSize -= lvt_6_1_;

                    if (lvt_2_1_.stackSize <= 0)
                    {
                        this.markDirty();
                        return null;
                    }
                }
            }
        }

        if (lvt_2_1_.stackSize != stack.stackSize)
        {
            this.markDirty();
        }

        return lvt_2_1_;
    }

    /**
     * Removes a stack from the given slot and returns it.
     */
    public ItemStack removeStackFromSlot(int index)
    {
        if (this.inventoryContents[index] != null)
        {
            ItemStack lvt_2_1_ = this.inventoryContents[index];
            this.inventoryContents[index] = null;
            return lvt_2_1_;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.inventoryContents[index] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }

        this.markDirty();
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return this.slotsCount;
    }

    /**
     * Get the name of this object. For players this returns their username
     */
    public String getName()
    {
        return this.inventoryTitle;
    }

    /**
     * Returns true if this thing is named
     */
    public boolean hasCustomName()
    {
        return this.hasCustomName;
    }

    /**
     * Sets the name of this inventory. This is displayed to the client on opening.
     */
    public void setCustomName(String inventoryTitleIn)
    {
        this.hasCustomName = true;
        this.inventoryTitle = inventoryTitleIn;
    }

    /**
     * Get the formatted ChatComponent that will be used for the sender's username in chat
     */
    public IChatComponent getDisplayName()
    {
        return (IChatComponent)(this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatComponentTranslation(this.getName(), new Object[0]));
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
     */
    public int getInventoryStackLimit()
    {
        return 64;
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    public void markDirty()
    {
        if (this.changeListeners != null)
        {
            for (int lvt_1_1_ = 0; lvt_1_1_ < this.changeListeners.size(); ++lvt_1_1_)
            {
                ((IInvBasic)this.changeListeners.get(lvt_1_1_)).onInventoryChanged(this);
            }
        }
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return true;
    }

    public void openInventory(EntityPlayer player)
    {
    }

    public void closeInventory(EntityPlayer player)
    {
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return true;
    }

    public int getField(int id)
    {
        return 0;
    }

    public void setField(int id, int value)
    {
    }

    public int getFieldCount()
    {
        return 0;
    }

    public void clear()
    {
        for (int lvt_1_1_ = 0; lvt_1_1_ < this.inventoryContents.length; ++lvt_1_1_)
        {
            this.inventoryContents[lvt_1_1_] = null;
        }
    }
}
