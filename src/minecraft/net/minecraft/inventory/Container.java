package net.minecraft.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;

public abstract class Container
{
    public List<ItemStack> inventoryItemStacks = Lists.newArrayList();
    public List<Slot> inventorySlots = Lists.newArrayList();
    public int windowId;
    private short transactionID;

    /**
     * The current drag mode (0 : evenly split, 1 : one item by slot, 2 : not used ?)
     */
    private int dragMode = -1;

    /** The current drag event (0 : start, 1 : add slot : 2 : end) */
    private int dragEvent;
    private final Set<Slot> dragSlots = Sets.newHashSet();
    protected List<ICrafting> crafters = Lists.newArrayList();
    private Set<EntityPlayer> playerList = Sets.newHashSet();

    /**
     * Adds an item slot to this container
     */
    protected Slot addSlotToContainer(Slot slotIn)
    {
        slotIn.slotNumber = this.inventorySlots.size();
        this.inventorySlots.add(slotIn);
        this.inventoryItemStacks.add((Object)null);
        return slotIn;
    }

    public void onCraftGuiOpened(ICrafting listener)
    {
        if (this.crafters.contains(listener))
        {
            throw new IllegalArgumentException("Listener already listening");
        }
        else
        {
            this.crafters.add(listener);
            listener.updateCraftingInventory(this, this.getInventory());
            this.detectAndSendChanges();
        }
    }

    /**
     * Remove the given Listener. Method name is for legacy.
     */
    public void removeCraftingFromCrafters(ICrafting listeners)
    {
        this.crafters.remove(listeners);
    }

    public List<ItemStack> getInventory()
    {
        List<ItemStack> lvt_1_1_ = Lists.newArrayList();

        for (int lvt_2_1_ = 0; lvt_2_1_ < this.inventorySlots.size(); ++lvt_2_1_)
        {
            lvt_1_1_.add(((Slot)this.inventorySlots.get(lvt_2_1_)).getStack());
        }

        return lvt_1_1_;
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    public void detectAndSendChanges()
    {
        for (int lvt_1_1_ = 0; lvt_1_1_ < this.inventorySlots.size(); ++lvt_1_1_)
        {
            ItemStack lvt_2_1_ = ((Slot)this.inventorySlots.get(lvt_1_1_)).getStack();
            ItemStack lvt_3_1_ = (ItemStack)this.inventoryItemStacks.get(lvt_1_1_);

            if (!ItemStack.areItemStacksEqual(lvt_3_1_, lvt_2_1_))
            {
                lvt_3_1_ = lvt_2_1_ == null ? null : lvt_2_1_.copy();
                this.inventoryItemStacks.set(lvt_1_1_, lvt_3_1_);

                for (int lvt_4_1_ = 0; lvt_4_1_ < this.crafters.size(); ++lvt_4_1_)
                {
                    ((ICrafting)this.crafters.get(lvt_4_1_)).sendSlotContents(this, lvt_1_1_, lvt_3_1_);
                }
            }
        }
    }

    /**
     * Handles the given Button-click on the server, currently only used by enchanting. Name is for legacy.
     */
    public boolean enchantItem(EntityPlayer playerIn, int id)
    {
        return false;
    }

    public Slot getSlotFromInventory(IInventory inv, int slotIn)
    {
        for (int lvt_3_1_ = 0; lvt_3_1_ < this.inventorySlots.size(); ++lvt_3_1_)
        {
            Slot lvt_4_1_ = (Slot)this.inventorySlots.get(lvt_3_1_);

            if (lvt_4_1_.isHere(inv, slotIn))
            {
                return lvt_4_1_;
            }
        }

        return null;
    }

    public Slot getSlot(int slotId)
    {
        return (Slot)this.inventorySlots.get(slotId);
    }

    /**
     * Take a stack from the specified inventory slot.
     */
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        Slot lvt_3_1_ = (Slot)this.inventorySlots.get(index);
        return lvt_3_1_ != null ? lvt_3_1_.getStack() : null;
    }

    /**
     * Handles slot click.
     */
    public ItemStack slotClick(int slotId, int clickedButton, int mode, EntityPlayer playerIn)
    {
        ItemStack lvt_5_1_ = null;
        InventoryPlayer lvt_6_1_ = playerIn.inventory;

        if (mode == 5)
        {
            int lvt_7_1_ = this.dragEvent;
            this.dragEvent = getDragEvent(clickedButton);

            if ((lvt_7_1_ != 1 || this.dragEvent != 2) && lvt_7_1_ != this.dragEvent)
            {
                this.resetDrag();
            }
            else if (lvt_6_1_.getItemStack() == null)
            {
                this.resetDrag();
            }
            else if (this.dragEvent == 0)
            {
                this.dragMode = extractDragMode(clickedButton);

                if (isValidDragMode(this.dragMode, playerIn))
                {
                    this.dragEvent = 1;
                    this.dragSlots.clear();
                }
                else
                {
                    this.resetDrag();
                }
            }
            else if (this.dragEvent == 1)
            {
                Slot lvt_8_1_ = (Slot)this.inventorySlots.get(slotId);

                if (lvt_8_1_ != null && canAddItemToSlot(lvt_8_1_, lvt_6_1_.getItemStack(), true) && lvt_8_1_.isItemValid(lvt_6_1_.getItemStack()) && lvt_6_1_.getItemStack().stackSize > this.dragSlots.size() && this.canDragIntoSlot(lvt_8_1_))
                {
                    this.dragSlots.add(lvt_8_1_);
                }
            }
            else if (this.dragEvent == 2)
            {
                if (!this.dragSlots.isEmpty())
                {
                    ItemStack lvt_8_2_ = lvt_6_1_.getItemStack().copy();
                    int lvt_9_1_ = lvt_6_1_.getItemStack().stackSize;

                    for (Slot lvt_11_1_ : this.dragSlots)
                    {
                        if (lvt_11_1_ != null && canAddItemToSlot(lvt_11_1_, lvt_6_1_.getItemStack(), true) && lvt_11_1_.isItemValid(lvt_6_1_.getItemStack()) && lvt_6_1_.getItemStack().stackSize >= this.dragSlots.size() && this.canDragIntoSlot(lvt_11_1_))
                        {
                            ItemStack lvt_12_1_ = lvt_8_2_.copy();
                            int lvt_13_1_ = lvt_11_1_.getHasStack() ? lvt_11_1_.getStack().stackSize : 0;
                            computeStackSize(this.dragSlots, this.dragMode, lvt_12_1_, lvt_13_1_);

                            if (lvt_12_1_.stackSize > lvt_12_1_.getMaxStackSize())
                            {
                                lvt_12_1_.stackSize = lvt_12_1_.getMaxStackSize();
                            }

                            if (lvt_12_1_.stackSize > lvt_11_1_.getItemStackLimit(lvt_12_1_))
                            {
                                lvt_12_1_.stackSize = lvt_11_1_.getItemStackLimit(lvt_12_1_);
                            }

                            lvt_9_1_ -= lvt_12_1_.stackSize - lvt_13_1_;
                            lvt_11_1_.putStack(lvt_12_1_);
                        }
                    }

                    lvt_8_2_.stackSize = lvt_9_1_;

                    if (lvt_8_2_.stackSize <= 0)
                    {
                        lvt_8_2_ = null;
                    }

                    lvt_6_1_.setItemStack(lvt_8_2_);
                }

                this.resetDrag();
            }
            else
            {
                this.resetDrag();
            }
        }
        else if (this.dragEvent != 0)
        {
            this.resetDrag();
        }
        else if ((mode == 0 || mode == 1) && (clickedButton == 0 || clickedButton == 1))
        {
            if (slotId == -999)
            {
                if (lvt_6_1_.getItemStack() != null)
                {
                    if (clickedButton == 0)
                    {
                        playerIn.dropPlayerItemWithRandomChoice(lvt_6_1_.getItemStack(), true);
                        lvt_6_1_.setItemStack((ItemStack)null);
                    }

                    if (clickedButton == 1)
                    {
                        playerIn.dropPlayerItemWithRandomChoice(lvt_6_1_.getItemStack().splitStack(1), true);

                        if (lvt_6_1_.getItemStack().stackSize == 0)
                        {
                            lvt_6_1_.setItemStack((ItemStack)null);
                        }
                    }
                }
            }
            else if (mode == 1)
            {
                if (slotId < 0)
                {
                    return null;
                }

                Slot lvt_7_2_ = (Slot)this.inventorySlots.get(slotId);

                if (lvt_7_2_ != null && lvt_7_2_.canTakeStack(playerIn))
                {
                    ItemStack lvt_8_3_ = this.transferStackInSlot(playerIn, slotId);

                    if (lvt_8_3_ != null)
                    {
                        Item lvt_9_2_ = lvt_8_3_.getItem();
                        lvt_5_1_ = lvt_8_3_.copy();

                        if (lvt_7_2_.getStack() != null && lvt_7_2_.getStack().getItem() == lvt_9_2_)
                        {
                            this.retrySlotClick(slotId, clickedButton, true, playerIn);
                        }
                    }
                }
            }
            else
            {
                if (slotId < 0)
                {
                    return null;
                }

                Slot lvt_7_3_ = (Slot)this.inventorySlots.get(slotId);

                if (lvt_7_3_ != null)
                {
                    ItemStack lvt_8_4_ = lvt_7_3_.getStack();
                    ItemStack lvt_9_3_ = lvt_6_1_.getItemStack();

                    if (lvt_8_4_ != null)
                    {
                        lvt_5_1_ = lvt_8_4_.copy();
                    }

                    if (lvt_8_4_ == null)
                    {
                        if (lvt_9_3_ != null && lvt_7_3_.isItemValid(lvt_9_3_))
                        {
                            int lvt_10_2_ = clickedButton == 0 ? lvt_9_3_.stackSize : 1;

                            if (lvt_10_2_ > lvt_7_3_.getItemStackLimit(lvt_9_3_))
                            {
                                lvt_10_2_ = lvt_7_3_.getItemStackLimit(lvt_9_3_);
                            }

                            if (lvt_9_3_.stackSize >= lvt_10_2_)
                            {
                                lvt_7_3_.putStack(lvt_9_3_.splitStack(lvt_10_2_));
                            }

                            if (lvt_9_3_.stackSize == 0)
                            {
                                lvt_6_1_.setItemStack((ItemStack)null);
                            }
                        }
                    }
                    else if (lvt_7_3_.canTakeStack(playerIn))
                    {
                        if (lvt_9_3_ == null)
                        {
                            int lvt_10_3_ = clickedButton == 0 ? lvt_8_4_.stackSize : (lvt_8_4_.stackSize + 1) / 2;
                            ItemStack lvt_11_2_ = lvt_7_3_.decrStackSize(lvt_10_3_);
                            lvt_6_1_.setItemStack(lvt_11_2_);

                            if (lvt_8_4_.stackSize == 0)
                            {
                                lvt_7_3_.putStack((ItemStack)null);
                            }

                            lvt_7_3_.onPickupFromSlot(playerIn, lvt_6_1_.getItemStack());
                        }
                        else if (lvt_7_3_.isItemValid(lvt_9_3_))
                        {
                            if (lvt_8_4_.getItem() == lvt_9_3_.getItem() && lvt_8_4_.getMetadata() == lvt_9_3_.getMetadata() && ItemStack.areItemStackTagsEqual(lvt_8_4_, lvt_9_3_))
                            {
                                int lvt_10_4_ = clickedButton == 0 ? lvt_9_3_.stackSize : 1;

                                if (lvt_10_4_ > lvt_7_3_.getItemStackLimit(lvt_9_3_) - lvt_8_4_.stackSize)
                                {
                                    lvt_10_4_ = lvt_7_3_.getItemStackLimit(lvt_9_3_) - lvt_8_4_.stackSize;
                                }

                                if (lvt_10_4_ > lvt_9_3_.getMaxStackSize() - lvt_8_4_.stackSize)
                                {
                                    lvt_10_4_ = lvt_9_3_.getMaxStackSize() - lvt_8_4_.stackSize;
                                }

                                lvt_9_3_.splitStack(lvt_10_4_);

                                if (lvt_9_3_.stackSize == 0)
                                {
                                    lvt_6_1_.setItemStack((ItemStack)null);
                                }

                                lvt_8_4_.stackSize += lvt_10_4_;
                            }
                            else if (lvt_9_3_.stackSize <= lvt_7_3_.getItemStackLimit(lvt_9_3_))
                            {
                                lvt_7_3_.putStack(lvt_9_3_);
                                lvt_6_1_.setItemStack(lvt_8_4_);
                            }
                        }
                        else if (lvt_8_4_.getItem() == lvt_9_3_.getItem() && lvt_9_3_.getMaxStackSize() > 1 && (!lvt_8_4_.getHasSubtypes() || lvt_8_4_.getMetadata() == lvt_9_3_.getMetadata()) && ItemStack.areItemStackTagsEqual(lvt_8_4_, lvt_9_3_))
                        {
                            int lvt_10_5_ = lvt_8_4_.stackSize;

                            if (lvt_10_5_ > 0 && lvt_10_5_ + lvt_9_3_.stackSize <= lvt_9_3_.getMaxStackSize())
                            {
                                lvt_9_3_.stackSize += lvt_10_5_;
                                lvt_8_4_ = lvt_7_3_.decrStackSize(lvt_10_5_);

                                if (lvt_8_4_.stackSize == 0)
                                {
                                    lvt_7_3_.putStack((ItemStack)null);
                                }

                                lvt_7_3_.onPickupFromSlot(playerIn, lvt_6_1_.getItemStack());
                            }
                        }
                    }

                    lvt_7_3_.onSlotChanged();
                }
            }
        }
        else if (mode == 2 && clickedButton >= 0 && clickedButton < 9)
        {
            Slot lvt_7_4_ = (Slot)this.inventorySlots.get(slotId);

            if (lvt_7_4_.canTakeStack(playerIn))
            {
                ItemStack lvt_8_5_ = lvt_6_1_.getStackInSlot(clickedButton);
                boolean lvt_9_4_ = lvt_8_5_ == null || lvt_7_4_.inventory == lvt_6_1_ && lvt_7_4_.isItemValid(lvt_8_5_);
                int lvt_10_6_ = -1;

                if (!lvt_9_4_)
                {
                    lvt_10_6_ = lvt_6_1_.getFirstEmptyStack();
                    lvt_9_4_ |= lvt_10_6_ > -1;
                }

                if (lvt_7_4_.getHasStack() && lvt_9_4_)
                {
                    ItemStack lvt_11_3_ = lvt_7_4_.getStack();
                    lvt_6_1_.setInventorySlotContents(clickedButton, lvt_11_3_.copy());

                    if ((lvt_7_4_.inventory != lvt_6_1_ || !lvt_7_4_.isItemValid(lvt_8_5_)) && lvt_8_5_ != null)
                    {
                        if (lvt_10_6_ > -1)
                        {
                            lvt_6_1_.addItemStackToInventory(lvt_8_5_);
                            lvt_7_4_.decrStackSize(lvt_11_3_.stackSize);
                            lvt_7_4_.putStack((ItemStack)null);
                            lvt_7_4_.onPickupFromSlot(playerIn, lvt_11_3_);
                        }
                    }
                    else
                    {
                        lvt_7_4_.decrStackSize(lvt_11_3_.stackSize);
                        lvt_7_4_.putStack(lvt_8_5_);
                        lvt_7_4_.onPickupFromSlot(playerIn, lvt_11_3_);
                    }
                }
                else if (!lvt_7_4_.getHasStack() && lvt_8_5_ != null && lvt_7_4_.isItemValid(lvt_8_5_))
                {
                    lvt_6_1_.setInventorySlotContents(clickedButton, (ItemStack)null);
                    lvt_7_4_.putStack(lvt_8_5_);
                }
            }
        }
        else if (mode == 3 && playerIn.capabilities.isCreativeMode && lvt_6_1_.getItemStack() == null && slotId >= 0)
        {
            Slot lvt_7_5_ = (Slot)this.inventorySlots.get(slotId);

            if (lvt_7_5_ != null && lvt_7_5_.getHasStack())
            {
                ItemStack lvt_8_6_ = lvt_7_5_.getStack().copy();
                lvt_8_6_.stackSize = lvt_8_6_.getMaxStackSize();
                lvt_6_1_.setItemStack(lvt_8_6_);
            }
        }
        else if (mode == 4 && lvt_6_1_.getItemStack() == null && slotId >= 0)
        {
            Slot lvt_7_6_ = (Slot)this.inventorySlots.get(slotId);

            if (lvt_7_6_ != null && lvt_7_6_.getHasStack() && lvt_7_6_.canTakeStack(playerIn))
            {
                ItemStack lvt_8_7_ = lvt_7_6_.decrStackSize(clickedButton == 0 ? 1 : lvt_7_6_.getStack().stackSize);
                lvt_7_6_.onPickupFromSlot(playerIn, lvt_8_7_);
                playerIn.dropPlayerItemWithRandomChoice(lvt_8_7_, true);
            }
        }
        else if (mode == 6 && slotId >= 0)
        {
            Slot lvt_7_7_ = (Slot)this.inventorySlots.get(slotId);
            ItemStack lvt_8_8_ = lvt_6_1_.getItemStack();

            if (lvt_8_8_ != null && (lvt_7_7_ == null || !lvt_7_7_.getHasStack() || !lvt_7_7_.canTakeStack(playerIn)))
            {
                int lvt_9_5_ = clickedButton == 0 ? 0 : this.inventorySlots.size() - 1;
                int lvt_10_7_ = clickedButton == 0 ? 1 : -1;

                for (int lvt_11_4_ = 0; lvt_11_4_ < 2; ++lvt_11_4_)
                {
                    for (int lvt_12_2_ = lvt_9_5_; lvt_12_2_ >= 0 && lvt_12_2_ < this.inventorySlots.size() && lvt_8_8_.stackSize < lvt_8_8_.getMaxStackSize(); lvt_12_2_ += lvt_10_7_)
                    {
                        Slot lvt_13_2_ = (Slot)this.inventorySlots.get(lvt_12_2_);

                        if (lvt_13_2_.getHasStack() && canAddItemToSlot(lvt_13_2_, lvt_8_8_, true) && lvt_13_2_.canTakeStack(playerIn) && this.canMergeSlot(lvt_8_8_, lvt_13_2_) && (lvt_11_4_ != 0 || lvt_13_2_.getStack().stackSize != lvt_13_2_.getStack().getMaxStackSize()))
                        {
                            int lvt_14_1_ = Math.min(lvt_8_8_.getMaxStackSize() - lvt_8_8_.stackSize, lvt_13_2_.getStack().stackSize);
                            ItemStack lvt_15_1_ = lvt_13_2_.decrStackSize(lvt_14_1_);
                            lvt_8_8_.stackSize += lvt_14_1_;

                            if (lvt_15_1_.stackSize <= 0)
                            {
                                lvt_13_2_.putStack((ItemStack)null);
                            }

                            lvt_13_2_.onPickupFromSlot(playerIn, lvt_15_1_);
                        }
                    }
                }
            }

            this.detectAndSendChanges();
        }

        return lvt_5_1_;
    }

    /**
     * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in
     * is null for the initial slot that was double-clicked.
     */
    public boolean canMergeSlot(ItemStack stack, Slot slotIn)
    {
        return true;
    }

    /**
     * Retries slotClick() in case of failure
     */
    protected void retrySlotClick(int slotId, int clickedButton, boolean mode, EntityPlayer playerIn)
    {
        this.slotClick(slotId, clickedButton, 1, playerIn);
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer playerIn)
    {
        InventoryPlayer lvt_2_1_ = playerIn.inventory;

        if (lvt_2_1_.getItemStack() != null)
        {
            playerIn.dropPlayerItemWithRandomChoice(lvt_2_1_.getItemStack(), false);
            lvt_2_1_.setItemStack((ItemStack)null);
        }
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        this.detectAndSendChanges();
    }

    /**
     * args: slotID, itemStack to put in slot
     */
    public void putStackInSlot(int slotID, ItemStack stack)
    {
        this.getSlot(slotID).putStack(stack);
    }

    /**
     * places itemstacks in first x slots, x being aitemstack.lenght
     */
    public void putStacksInSlots(ItemStack[] p_75131_1_)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < p_75131_1_.length; ++lvt_2_1_)
        {
            this.getSlot(lvt_2_1_).putStack(p_75131_1_[lvt_2_1_]);
        }
    }

    public void updateProgressBar(int id, int data)
    {
    }

    /**
     * Gets a unique transaction ID. Parameter is unused.
     */
    public short getNextTransactionID(InventoryPlayer p_75136_1_)
    {
        ++this.transactionID;
        return this.transactionID;
    }

    /**
     * gets whether or not the player can craft in this inventory or not
     */
    public boolean getCanCraft(EntityPlayer p_75129_1_)
    {
        return !this.playerList.contains(p_75129_1_);
    }

    /**
     * sets whether the player can craft in this inventory or not
     */
    public void setCanCraft(EntityPlayer p_75128_1_, boolean p_75128_2_)
    {
        if (p_75128_2_)
        {
            this.playerList.remove(p_75128_1_);
        }
        else
        {
            this.playerList.add(p_75128_1_);
        }
    }

    public abstract boolean canInteractWith(EntityPlayer playerIn);

    /**
     * Merges provided ItemStack with the first avaliable one in the container/player inventor between minIndex
     * (included) and maxIndex (excluded). Args : stack, minIndex, maxIndex, negativDirection. /!\ the Container
     * implementation do not check if the item is valid for the slot
     */
    protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection)
    {
        boolean lvt_5_1_ = false;
        int lvt_6_1_ = startIndex;

        if (reverseDirection)
        {
            lvt_6_1_ = endIndex - 1;
        }

        if (stack.isStackable())
        {
            while (stack.stackSize > 0 && (!reverseDirection && lvt_6_1_ < endIndex || reverseDirection && lvt_6_1_ >= startIndex))
            {
                Slot lvt_7_1_ = (Slot)this.inventorySlots.get(lvt_6_1_);
                ItemStack lvt_8_1_ = lvt_7_1_.getStack();

                if (lvt_8_1_ != null && lvt_8_1_.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getMetadata() == lvt_8_1_.getMetadata()) && ItemStack.areItemStackTagsEqual(stack, lvt_8_1_))
                {
                    int lvt_9_1_ = lvt_8_1_.stackSize + stack.stackSize;

                    if (lvt_9_1_ <= stack.getMaxStackSize())
                    {
                        stack.stackSize = 0;
                        lvt_8_1_.stackSize = lvt_9_1_;
                        lvt_7_1_.onSlotChanged();
                        lvt_5_1_ = true;
                    }
                    else if (lvt_8_1_.stackSize < stack.getMaxStackSize())
                    {
                        stack.stackSize -= stack.getMaxStackSize() - lvt_8_1_.stackSize;
                        lvt_8_1_.stackSize = stack.getMaxStackSize();
                        lvt_7_1_.onSlotChanged();
                        lvt_5_1_ = true;
                    }
                }

                if (reverseDirection)
                {
                    --lvt_6_1_;
                }
                else
                {
                    ++lvt_6_1_;
                }
            }
        }

        if (stack.stackSize > 0)
        {
            if (reverseDirection)
            {
                lvt_6_1_ = endIndex - 1;
            }
            else
            {
                lvt_6_1_ = startIndex;
            }

            while (!reverseDirection && lvt_6_1_ < endIndex || reverseDirection && lvt_6_1_ >= startIndex)
            {
                Slot lvt_7_2_ = (Slot)this.inventorySlots.get(lvt_6_1_);
                ItemStack lvt_8_2_ = lvt_7_2_.getStack();

                if (lvt_8_2_ == null)
                {
                    lvt_7_2_.putStack(stack.copy());
                    lvt_7_2_.onSlotChanged();
                    stack.stackSize = 0;
                    lvt_5_1_ = true;
                    break;
                }

                if (reverseDirection)
                {
                    --lvt_6_1_;
                }
                else
                {
                    ++lvt_6_1_;
                }
            }
        }

        return lvt_5_1_;
    }

    /**
     * Extracts the drag mode. Args : eventButton. Return (0 : evenly split, 1 : one item by slot, 2 : not used ?)
     */
    public static int extractDragMode(int p_94529_0_)
    {
        return p_94529_0_ >> 2 & 3;
    }

    /**
     * Args : clickedButton, Returns (0 : start drag, 1 : add slot, 2 : end drag)
     */
    public static int getDragEvent(int p_94532_0_)
    {
        return p_94532_0_ & 3;
    }

    public static int func_94534_d(int p_94534_0_, int p_94534_1_)
    {
        return p_94534_0_ & 3 | (p_94534_1_ & 3) << 2;
    }

    public static boolean isValidDragMode(int dragModeIn, EntityPlayer player)
    {
        return dragModeIn == 0 ? true : (dragModeIn == 1 ? true : dragModeIn == 2 && player.capabilities.isCreativeMode);
    }

    /**
     * Reset the drag fields
     */
    protected void resetDrag()
    {
        this.dragEvent = 0;
        this.dragSlots.clear();
    }

    /**
     * Checks if it's possible to add the given itemstack to the given slot.
     */
    public static boolean canAddItemToSlot(Slot slotIn, ItemStack stack, boolean stackSizeMatters)
    {
        boolean lvt_3_1_ = slotIn == null || !slotIn.getHasStack();

        if (slotIn != null && slotIn.getHasStack() && stack != null && stack.isItemEqual(slotIn.getStack()) && ItemStack.areItemStackTagsEqual(slotIn.getStack(), stack))
        {
            lvt_3_1_ |= slotIn.getStack().stackSize + (stackSizeMatters ? 0 : stack.stackSize) <= stack.getMaxStackSize();
        }

        return lvt_3_1_;
    }

    /**
     * Compute the new stack size, Returns the stack with the new size. Args : dragSlots, dragMode, dragStack,
     * slotStackSize
     */
    public static void computeStackSize(Set<Slot> p_94525_0_, int p_94525_1_, ItemStack p_94525_2_, int p_94525_3_)
    {
        switch (p_94525_1_)
        {
            case 0:
                p_94525_2_.stackSize = MathHelper.floor_float((float)p_94525_2_.stackSize / (float)p_94525_0_.size());
                break;

            case 1:
                p_94525_2_.stackSize = 1;
                break;

            case 2:
                p_94525_2_.stackSize = p_94525_2_.getItem().getItemStackLimit();
        }

        p_94525_2_.stackSize += p_94525_3_;
    }

    /**
     * Returns true if the player can "drag-spilt" items into this slot,. returns true by default. Called to check if
     * the slot can be added to a list of Slots to split the held ItemStack across.
     */
    public boolean canDragIntoSlot(Slot p_94531_1_)
    {
        return true;
    }

    /**
     * Like the version that takes an inventory. If the given TileEntity is not an Inventory, 0 is returned instead.
     */
    public static int calcRedstone(TileEntity te)
    {
        return te instanceof IInventory ? calcRedstoneFromInventory((IInventory)te) : 0;
    }

    public static int calcRedstoneFromInventory(IInventory inv)
    {
        if (inv == null)
        {
            return 0;
        }
        else
        {
            int lvt_1_1_ = 0;
            float lvt_2_1_ = 0.0F;

            for (int lvt_3_1_ = 0; lvt_3_1_ < inv.getSizeInventory(); ++lvt_3_1_)
            {
                ItemStack lvt_4_1_ = inv.getStackInSlot(lvt_3_1_);

                if (lvt_4_1_ != null)
                {
                    lvt_2_1_ += (float)lvt_4_1_.stackSize / (float)Math.min(inv.getInventoryStackLimit(), lvt_4_1_.getMaxStackSize());
                    ++lvt_1_1_;
                }
            }

            lvt_2_1_ = lvt_2_1_ / (float)inv.getSizeInventory();
            return MathHelper.floor_float(lvt_2_1_ * 14.0F) + (lvt_1_1_ > 0 ? 1 : 0);
        }
    }
}
