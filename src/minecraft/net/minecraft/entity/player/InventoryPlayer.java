package net.minecraft.entity.player;

import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ReportedException;

public class InventoryPlayer implements IInventory
{
    /**
     * An array of 36 item stacks indicating the main player inventory (including the visible bar).
     */
    public ItemStack[] mainInventory = new ItemStack[36];

    /** An array of 4 item stacks containing the currently worn armor pieces. */
    public ItemStack[] armorInventory = new ItemStack[4];

    /** The index of the currently held item (0-8). */
    public int currentItem;

    /** The player whose inventory this is. */
    public EntityPlayer player;
    private ItemStack itemStack;

    /**
     * Set true whenever the inventory changes. Nothing sets it false so you will have to write your own code to check
     * it and reset the value.
     */
    public boolean inventoryChanged;

    public InventoryPlayer(EntityPlayer playerIn)
    {
        this.player = playerIn;
    }

    /**
     * Returns the item stack currently held by the player.
     */
    public ItemStack getCurrentItem()
    {
        return this.currentItem < 9 && this.currentItem >= 0 ? this.mainInventory[this.currentItem] : null;
    }

    /**
     * Get the size of the player hotbar inventory
     */
    public static int getHotbarSize()
    {
        return 9;
    }

    private int getInventorySlotContainItem(Item itemIn)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < this.mainInventory.length; ++lvt_2_1_)
        {
            if (this.mainInventory[lvt_2_1_] != null && this.mainInventory[lvt_2_1_].getItem() == itemIn)
            {
                return lvt_2_1_;
            }
        }

        return -1;
    }

    private int getInventorySlotContainItemAndDamage(Item itemIn, int metadataIn)
    {
        for (int lvt_3_1_ = 0; lvt_3_1_ < this.mainInventory.length; ++lvt_3_1_)
        {
            if (this.mainInventory[lvt_3_1_] != null && this.mainInventory[lvt_3_1_].getItem() == itemIn && this.mainInventory[lvt_3_1_].getMetadata() == metadataIn)
            {
                return lvt_3_1_;
            }
        }

        return -1;
    }

    /**
     * stores an itemstack in the users inventory
     */
    private int storeItemStack(ItemStack itemStackIn)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < this.mainInventory.length; ++lvt_2_1_)
        {
            if (this.mainInventory[lvt_2_1_] != null && this.mainInventory[lvt_2_1_].getItem() == itemStackIn.getItem() && this.mainInventory[lvt_2_1_].isStackable() && this.mainInventory[lvt_2_1_].stackSize < this.mainInventory[lvt_2_1_].getMaxStackSize() && this.mainInventory[lvt_2_1_].stackSize < this.getInventoryStackLimit() && (!this.mainInventory[lvt_2_1_].getHasSubtypes() || this.mainInventory[lvt_2_1_].getMetadata() == itemStackIn.getMetadata()) && ItemStack.areItemStackTagsEqual(this.mainInventory[lvt_2_1_], itemStackIn))
            {
                return lvt_2_1_;
            }
        }

        return -1;
    }

    /**
     * Returns the first item stack that is empty.
     */
    public int getFirstEmptyStack()
    {
        for (int lvt_1_1_ = 0; lvt_1_1_ < this.mainInventory.length; ++lvt_1_1_)
        {
            if (this.mainInventory[lvt_1_1_] == null)
            {
                return lvt_1_1_;
            }
        }

        return -1;
    }

    public void setCurrentItem(Item itemIn, int metadataIn, boolean isMetaSpecific, boolean p_146030_4_)
    {
        ItemStack lvt_5_1_ = this.getCurrentItem();
        int lvt_6_1_ = isMetaSpecific ? this.getInventorySlotContainItemAndDamage(itemIn, metadataIn) : this.getInventorySlotContainItem(itemIn);

        if (lvt_6_1_ >= 0 && lvt_6_1_ < 9)
        {
            this.currentItem = lvt_6_1_;
        }
        else if (p_146030_4_ && itemIn != null)
        {
            int lvt_7_1_ = this.getFirstEmptyStack();

            if (lvt_7_1_ >= 0 && lvt_7_1_ < 9)
            {
                this.currentItem = lvt_7_1_;
            }

            if (lvt_5_1_ == null || !lvt_5_1_.isItemEnchantable() || this.getInventorySlotContainItemAndDamage(lvt_5_1_.getItem(), lvt_5_1_.getItemDamage()) != this.currentItem)
            {
                int lvt_8_1_ = this.getInventorySlotContainItemAndDamage(itemIn, metadataIn);
                int lvt_9_1_;

                if (lvt_8_1_ >= 0)
                {
                    lvt_9_1_ = this.mainInventory[lvt_8_1_].stackSize;
                    this.mainInventory[lvt_8_1_] = this.mainInventory[this.currentItem];
                }
                else
                {
                    lvt_9_1_ = 1;
                }

                this.mainInventory[this.currentItem] = new ItemStack(itemIn, lvt_9_1_, metadataIn);
            }
        }
    }

    /**
     * Switch the current item to the next one or the previous one
     *  
     * @param direction Direction to switch (1, 0, -1). 1 (any > 0) to select item left of current (decreasing
     * currentItem index), -1 (any < 0) to select item right of current (increasing currentItem index). 0 has no effect.
     */
    public void changeCurrentItem(int direction)
    {
        if (direction > 0)
        {
            direction = 1;
        }

        if (direction < 0)
        {
            direction = -1;
        }

        for (this.currentItem -= direction; this.currentItem < 0; this.currentItem += 9)
        {
            ;
        }

        while (this.currentItem >= 9)
        {
            this.currentItem -= 9;
        }
    }

    /**
     * Removes matching items from the inventory.
     * @param itemIn The item to match, null ignores.
     * @param metadataIn The metadata to match, -1 ignores.
     * @param removeCount The number of items to remove. If less than 1, removes all matching items.
     * @param itemNBT The NBT data to match, null ignores.
     * @return The number of items removed from the inventory.
     */
    public int clearMatchingItems(Item itemIn, int metadataIn, int removeCount, NBTTagCompound itemNBT)
    {
        int lvt_5_1_ = 0;

        for (int lvt_6_1_ = 0; lvt_6_1_ < this.mainInventory.length; ++lvt_6_1_)
        {
            ItemStack lvt_7_1_ = this.mainInventory[lvt_6_1_];

            if (lvt_7_1_ != null && (itemIn == null || lvt_7_1_.getItem() == itemIn) && (metadataIn <= -1 || lvt_7_1_.getMetadata() == metadataIn) && (itemNBT == null || NBTUtil.func_181123_a(itemNBT, lvt_7_1_.getTagCompound(), true)))
            {
                int lvt_8_1_ = removeCount <= 0 ? lvt_7_1_.stackSize : Math.min(removeCount - lvt_5_1_, lvt_7_1_.stackSize);
                lvt_5_1_ += lvt_8_1_;

                if (removeCount != 0)
                {
                    this.mainInventory[lvt_6_1_].stackSize -= lvt_8_1_;

                    if (this.mainInventory[lvt_6_1_].stackSize == 0)
                    {
                        this.mainInventory[lvt_6_1_] = null;
                    }

                    if (removeCount > 0 && lvt_5_1_ >= removeCount)
                    {
                        return lvt_5_1_;
                    }
                }
            }
        }

        for (int lvt_6_2_ = 0; lvt_6_2_ < this.armorInventory.length; ++lvt_6_2_)
        {
            ItemStack lvt_7_2_ = this.armorInventory[lvt_6_2_];

            if (lvt_7_2_ != null && (itemIn == null || lvt_7_2_.getItem() == itemIn) && (metadataIn <= -1 || lvt_7_2_.getMetadata() == metadataIn) && (itemNBT == null || NBTUtil.func_181123_a(itemNBT, lvt_7_2_.getTagCompound(), false)))
            {
                int lvt_8_2_ = removeCount <= 0 ? lvt_7_2_.stackSize : Math.min(removeCount - lvt_5_1_, lvt_7_2_.stackSize);
                lvt_5_1_ += lvt_8_2_;

                if (removeCount != 0)
                {
                    this.armorInventory[lvt_6_2_].stackSize -= lvt_8_2_;

                    if (this.armorInventory[lvt_6_2_].stackSize == 0)
                    {
                        this.armorInventory[lvt_6_2_] = null;
                    }

                    if (removeCount > 0 && lvt_5_1_ >= removeCount)
                    {
                        return lvt_5_1_;
                    }
                }
            }
        }

        if (this.itemStack != null)
        {
            if (itemIn != null && this.itemStack.getItem() != itemIn)
            {
                return lvt_5_1_;
            }

            if (metadataIn > -1 && this.itemStack.getMetadata() != metadataIn)
            {
                return lvt_5_1_;
            }

            if (itemNBT != null && !NBTUtil.func_181123_a(itemNBT, this.itemStack.getTagCompound(), false))
            {
                return lvt_5_1_;
            }

            int lvt_6_3_ = removeCount <= 0 ? this.itemStack.stackSize : Math.min(removeCount - lvt_5_1_, this.itemStack.stackSize);
            lvt_5_1_ += lvt_6_3_;

            if (removeCount != 0)
            {
                this.itemStack.stackSize -= lvt_6_3_;

                if (this.itemStack.stackSize == 0)
                {
                    this.itemStack = null;
                }

                if (removeCount > 0 && lvt_5_1_ >= removeCount)
                {
                    return lvt_5_1_;
                }
            }
        }

        return lvt_5_1_;
    }

    /**
     * This function stores as many items of an ItemStack as possible in a matching slot and returns the quantity of
     * left over items.
     */
    private int storePartialItemStack(ItemStack itemStackIn)
    {
        Item lvt_2_1_ = itemStackIn.getItem();
        int lvt_3_1_ = itemStackIn.stackSize;
        int lvt_4_1_ = this.storeItemStack(itemStackIn);

        if (lvt_4_1_ < 0)
        {
            lvt_4_1_ = this.getFirstEmptyStack();
        }

        if (lvt_4_1_ < 0)
        {
            return lvt_3_1_;
        }
        else
        {
            if (this.mainInventory[lvt_4_1_] == null)
            {
                this.mainInventory[lvt_4_1_] = new ItemStack(lvt_2_1_, 0, itemStackIn.getMetadata());

                if (itemStackIn.hasTagCompound())
                {
                    this.mainInventory[lvt_4_1_].setTagCompound((NBTTagCompound)itemStackIn.getTagCompound().copy());
                }
            }

            int lvt_5_1_ = lvt_3_1_;

            if (lvt_3_1_ > this.mainInventory[lvt_4_1_].getMaxStackSize() - this.mainInventory[lvt_4_1_].stackSize)
            {
                lvt_5_1_ = this.mainInventory[lvt_4_1_].getMaxStackSize() - this.mainInventory[lvt_4_1_].stackSize;
            }

            if (lvt_5_1_ > this.getInventoryStackLimit() - this.mainInventory[lvt_4_1_].stackSize)
            {
                lvt_5_1_ = this.getInventoryStackLimit() - this.mainInventory[lvt_4_1_].stackSize;
            }

            if (lvt_5_1_ == 0)
            {
                return lvt_3_1_;
            }
            else
            {
                lvt_3_1_ = lvt_3_1_ - lvt_5_1_;
                this.mainInventory[lvt_4_1_].stackSize += lvt_5_1_;
                this.mainInventory[lvt_4_1_].animationsToGo = 5;
                return lvt_3_1_;
            }
        }
    }

    /**
     * Decrement the number of animations remaining. Only called on client side. This is used to handle the animation of
     * receiving a block.
     */
    public void decrementAnimations()
    {
        for (int lvt_1_1_ = 0; lvt_1_1_ < this.mainInventory.length; ++lvt_1_1_)
        {
            if (this.mainInventory[lvt_1_1_] != null)
            {
                this.mainInventory[lvt_1_1_].updateAnimation(this.player.worldObj, this.player, lvt_1_1_, this.currentItem == lvt_1_1_);
            }
        }
    }

    /**
     * removed one item of specified Item from inventory (if it is in a stack, the stack size will reduce with 1)
     */
    public boolean consumeInventoryItem(Item itemIn)
    {
        int lvt_2_1_ = this.getInventorySlotContainItem(itemIn);

        if (lvt_2_1_ < 0)
        {
            return false;
        }
        else
        {
            if (--this.mainInventory[lvt_2_1_].stackSize <= 0)
            {
                this.mainInventory[lvt_2_1_] = null;
            }

            return true;
        }
    }

    /**
     * Checks if a specified Item is inside the inventory
     */
    public boolean hasItem(Item itemIn)
    {
        int lvt_2_1_ = this.getInventorySlotContainItem(itemIn);
        return lvt_2_1_ >= 0;
    }

    /**
     * Adds the item stack to the inventory, returns false if it is impossible.
     */
    public boolean addItemStackToInventory(final ItemStack itemStackIn)
    {
        if (itemStackIn != null && itemStackIn.stackSize != 0 && itemStackIn.getItem() != null)
        {
            try
            {
                if (itemStackIn.isItemDamaged())
                {
                    int lvt_2_2_ = this.getFirstEmptyStack();

                    if (lvt_2_2_ >= 0)
                    {
                        this.mainInventory[lvt_2_2_] = ItemStack.copyItemStack(itemStackIn);
                        this.mainInventory[lvt_2_2_].animationsToGo = 5;
                        itemStackIn.stackSize = 0;
                        return true;
                    }
                    else if (this.player.capabilities.isCreativeMode)
                    {
                        itemStackIn.stackSize = 0;
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    int lvt_2_1_;

                    while (true)
                    {
                        lvt_2_1_ = itemStackIn.stackSize;
                        itemStackIn.stackSize = this.storePartialItemStack(itemStackIn);

                        if (itemStackIn.stackSize <= 0 || itemStackIn.stackSize >= lvt_2_1_)
                        {
                            break;
                        }
                    }

                    if (itemStackIn.stackSize == lvt_2_1_ && this.player.capabilities.isCreativeMode)
                    {
                        itemStackIn.stackSize = 0;
                        return true;
                    }
                    else
                    {
                        return itemStackIn.stackSize < lvt_2_1_;
                    }
                }
            }
            catch (Throwable var5)
            {
                CrashReport lvt_3_1_ = CrashReport.makeCrashReport(var5, "Adding item to inventory");
                CrashReportCategory lvt_4_1_ = lvt_3_1_.makeCategory("Item being added");
                lvt_4_1_.addCrashSection("Item ID", Integer.valueOf(Item.getIdFromItem(itemStackIn.getItem())));
                lvt_4_1_.addCrashSection("Item data", Integer.valueOf(itemStackIn.getMetadata()));
                lvt_4_1_.addCrashSectionCallable("Item name", new Callable<String>()
                {
                    public String call() throws Exception
                    {
                        return itemStackIn.getDisplayName();
                    }
                    public Object call() throws Exception
                    {
                        return this.call();
                    }
                });
                throw new ReportedException(lvt_3_1_);
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    public ItemStack decrStackSize(int index, int count)
    {
        ItemStack[] lvt_3_1_ = this.mainInventory;

        if (index >= this.mainInventory.length)
        {
            lvt_3_1_ = this.armorInventory;
            index -= this.mainInventory.length;
        }

        if (lvt_3_1_[index] != null)
        {
            if (lvt_3_1_[index].stackSize <= count)
            {
                ItemStack lvt_4_1_ = lvt_3_1_[index];
                lvt_3_1_[index] = null;
                return lvt_4_1_;
            }
            else
            {
                ItemStack lvt_4_2_ = lvt_3_1_[index].splitStack(count);

                if (lvt_3_1_[index].stackSize == 0)
                {
                    lvt_3_1_[index] = null;
                }

                return lvt_4_2_;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * Removes a stack from the given slot and returns it.
     */
    public ItemStack removeStackFromSlot(int index)
    {
        ItemStack[] lvt_2_1_ = this.mainInventory;

        if (index >= this.mainInventory.length)
        {
            lvt_2_1_ = this.armorInventory;
            index -= this.mainInventory.length;
        }

        if (lvt_2_1_[index] != null)
        {
            ItemStack lvt_3_1_ = lvt_2_1_[index];
            lvt_2_1_[index] = null;
            return lvt_3_1_;
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
        ItemStack[] lvt_3_1_ = this.mainInventory;

        if (index >= lvt_3_1_.length)
        {
            index -= lvt_3_1_.length;
            lvt_3_1_ = this.armorInventory;
        }

        lvt_3_1_[index] = stack;
    }

    public float getStrVsBlock(Block blockIn)
    {
        float lvt_2_1_ = 1.0F;

        if (this.mainInventory[this.currentItem] != null)
        {
            lvt_2_1_ *= this.mainInventory[this.currentItem].getStrVsBlock(blockIn);
        }

        return lvt_2_1_;
    }

    /**
     * Writes the inventory out as a list of compound tags. This is where the slot indices are used (+100 for armor, +80
     * for crafting).
     *  
     * @param nbtTagListIn List to append tags to
     */
    public NBTTagList writeToNBT(NBTTagList nbtTagListIn)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < this.mainInventory.length; ++lvt_2_1_)
        {
            if (this.mainInventory[lvt_2_1_] != null)
            {
                NBTTagCompound lvt_3_1_ = new NBTTagCompound();
                lvt_3_1_.setByte("Slot", (byte)lvt_2_1_);
                this.mainInventory[lvt_2_1_].writeToNBT(lvt_3_1_);
                nbtTagListIn.appendTag(lvt_3_1_);
            }
        }

        for (int lvt_2_2_ = 0; lvt_2_2_ < this.armorInventory.length; ++lvt_2_2_)
        {
            if (this.armorInventory[lvt_2_2_] != null)
            {
                NBTTagCompound lvt_3_2_ = new NBTTagCompound();
                lvt_3_2_.setByte("Slot", (byte)(lvt_2_2_ + 100));
                this.armorInventory[lvt_2_2_].writeToNBT(lvt_3_2_);
                nbtTagListIn.appendTag(lvt_3_2_);
            }
        }

        return nbtTagListIn;
    }

    /**
     * Reads from the given tag list and fills the slots in the inventory with the correct items.
     *  
     * @param nbtTagListIn tagList to read from
     */
    public void readFromNBT(NBTTagList nbtTagListIn)
    {
        this.mainInventory = new ItemStack[36];
        this.armorInventory = new ItemStack[4];

        for (int lvt_2_1_ = 0; lvt_2_1_ < nbtTagListIn.tagCount(); ++lvt_2_1_)
        {
            NBTTagCompound lvt_3_1_ = nbtTagListIn.getCompoundTagAt(lvt_2_1_);
            int lvt_4_1_ = lvt_3_1_.getByte("Slot") & 255;
            ItemStack lvt_5_1_ = ItemStack.loadItemStackFromNBT(lvt_3_1_);

            if (lvt_5_1_ != null)
            {
                if (lvt_4_1_ >= 0 && lvt_4_1_ < this.mainInventory.length)
                {
                    this.mainInventory[lvt_4_1_] = lvt_5_1_;
                }

                if (lvt_4_1_ >= 100 && lvt_4_1_ < this.armorInventory.length + 100)
                {
                    this.armorInventory[lvt_4_1_ - 100] = lvt_5_1_;
                }
            }
        }
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return this.mainInventory.length + 4;
    }

    /**
     * Returns the stack in the given slot.
     */
    public ItemStack getStackInSlot(int index)
    {
        ItemStack[] lvt_2_1_ = this.mainInventory;

        if (index >= lvt_2_1_.length)
        {
            index -= lvt_2_1_.length;
            lvt_2_1_ = this.armorInventory;
        }

        return lvt_2_1_[index];
    }

    /**
     * Get the name of this object. For players this returns their username
     */
    public String getName()
    {
        return "container.inventory";
    }

    /**
     * Returns true if this thing is named
     */
    public boolean hasCustomName()
    {
        return false;
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

    public boolean canHeldItemHarvest(Block blockIn)
    {
        if (blockIn.getMaterial().isToolNotRequired())
        {
            return true;
        }
        else
        {
            ItemStack lvt_2_1_ = this.getStackInSlot(this.currentItem);
            return lvt_2_1_ != null ? lvt_2_1_.canHarvestBlock(blockIn) : false;
        }
    }

    /**
     * returns a player armor item (as itemstack) contained in specified armor slot.
     *  
     * @param slotIn the slot index requested
     */
    public ItemStack armorItemInSlot(int slotIn)
    {
        return this.armorInventory[slotIn];
    }

    /**
     * Based on the damage values and maximum damage values of each armor item, returns the current armor value.
     */
    public int getTotalArmorValue()
    {
        int lvt_1_1_ = 0;

        for (int lvt_2_1_ = 0; lvt_2_1_ < this.armorInventory.length; ++lvt_2_1_)
        {
            if (this.armorInventory[lvt_2_1_] != null && this.armorInventory[lvt_2_1_].getItem() instanceof ItemArmor)
            {
                int lvt_3_1_ = ((ItemArmor)this.armorInventory[lvt_2_1_].getItem()).damageReduceAmount;
                lvt_1_1_ += lvt_3_1_;
            }
        }

        return lvt_1_1_;
    }

    /**
     * Damages armor in each slot by the specified amount.
     */
    public void damageArmor(float damage)
    {
        damage = damage / 4.0F;

        if (damage < 1.0F)
        {
            damage = 1.0F;
        }

        for (int lvt_2_1_ = 0; lvt_2_1_ < this.armorInventory.length; ++lvt_2_1_)
        {
            if (this.armorInventory[lvt_2_1_] != null && this.armorInventory[lvt_2_1_].getItem() instanceof ItemArmor)
            {
                this.armorInventory[lvt_2_1_].damageItem((int)damage, this.player);

                if (this.armorInventory[lvt_2_1_].stackSize == 0)
                {
                    this.armorInventory[lvt_2_1_] = null;
                }
            }
        }
    }

    /**
     * Drop all armor and main inventory items.
     */
    public void dropAllItems()
    {
        for (int lvt_1_1_ = 0; lvt_1_1_ < this.mainInventory.length; ++lvt_1_1_)
        {
            if (this.mainInventory[lvt_1_1_] != null)
            {
                this.player.dropItem(this.mainInventory[lvt_1_1_], true, false);
                this.mainInventory[lvt_1_1_] = null;
            }
        }

        for (int lvt_1_2_ = 0; lvt_1_2_ < this.armorInventory.length; ++lvt_1_2_)
        {
            if (this.armorInventory[lvt_1_2_] != null)
            {
                this.player.dropItem(this.armorInventory[lvt_1_2_], true, false);
                this.armorInventory[lvt_1_2_] = null;
            }
        }
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    public void markDirty()
    {
        this.inventoryChanged = true;
    }

    /**
     * Set the stack helds by mouse, used in GUI/Container
     */
    public void setItemStack(ItemStack itemStackIn)
    {
        this.itemStack = itemStackIn;
    }

    /**
     * Stack helds by mouse, used in GUI and Containers
     */
    public ItemStack getItemStack()
    {
        return this.itemStack;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return this.player.isDead ? false : player.getDistanceSqToEntity(this.player) <= 64.0D;
    }

    /**
     * Returns true if the specified ItemStack exists in the inventory.
     */
    public boolean hasItemStack(ItemStack itemStackIn)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < this.armorInventory.length; ++lvt_2_1_)
        {
            if (this.armorInventory[lvt_2_1_] != null && this.armorInventory[lvt_2_1_].isItemEqual(itemStackIn))
            {
                return true;
            }
        }

        for (int lvt_2_2_ = 0; lvt_2_2_ < this.mainInventory.length; ++lvt_2_2_)
        {
            if (this.mainInventory[lvt_2_2_] != null && this.mainInventory[lvt_2_2_].isItemEqual(itemStackIn))
            {
                return true;
            }
        }

        return false;
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

    /**
     * Copy the ItemStack contents from another InventoryPlayer instance
     */
    public void copyInventory(InventoryPlayer playerInventory)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < this.mainInventory.length; ++lvt_2_1_)
        {
            this.mainInventory[lvt_2_1_] = ItemStack.copyItemStack(playerInventory.mainInventory[lvt_2_1_]);
        }

        for (int lvt_2_2_ = 0; lvt_2_2_ < this.armorInventory.length; ++lvt_2_2_)
        {
            this.armorInventory[lvt_2_2_] = ItemStack.copyItemStack(playerInventory.armorInventory[lvt_2_2_]);
        }

        this.currentItem = playerInventory.currentItem;
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
        for (int lvt_1_1_ = 0; lvt_1_1_ < this.mainInventory.length; ++lvt_1_1_)
        {
            this.mainInventory[lvt_1_1_] = null;
        }

        for (int lvt_1_2_ = 0; lvt_1_2_ < this.armorInventory.length; ++lvt_1_2_)
        {
            this.armorInventory[lvt_1_2_] = null;
        }
    }
}
