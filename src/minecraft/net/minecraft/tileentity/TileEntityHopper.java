package net.minecraft.tileentity;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockHopper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TileEntityHopper extends TileEntityLockable implements IHopper, ITickable
{
    private ItemStack[] inventory = new ItemStack[5];
    private String customName;
    private int transferCooldown = -1;

    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        NBTTagList lvt_2_1_ = compound.getTagList("Items", 10);
        this.inventory = new ItemStack[this.getSizeInventory()];

        if (compound.hasKey("CustomName", 8))
        {
            this.customName = compound.getString("CustomName");
        }

        this.transferCooldown = compound.getInteger("TransferCooldown");

        for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.tagCount(); ++lvt_3_1_)
        {
            NBTTagCompound lvt_4_1_ = lvt_2_1_.getCompoundTagAt(lvt_3_1_);
            int lvt_5_1_ = lvt_4_1_.getByte("Slot");

            if (lvt_5_1_ >= 0 && lvt_5_1_ < this.inventory.length)
            {
                this.inventory[lvt_5_1_] = ItemStack.loadItemStackFromNBT(lvt_4_1_);
            }
        }
    }

    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        NBTTagList lvt_2_1_ = new NBTTagList();

        for (int lvt_3_1_ = 0; lvt_3_1_ < this.inventory.length; ++lvt_3_1_)
        {
            if (this.inventory[lvt_3_1_] != null)
            {
                NBTTagCompound lvt_4_1_ = new NBTTagCompound();
                lvt_4_1_.setByte("Slot", (byte)lvt_3_1_);
                this.inventory[lvt_3_1_].writeToNBT(lvt_4_1_);
                lvt_2_1_.appendTag(lvt_4_1_);
            }
        }

        compound.setTag("Items", lvt_2_1_);
        compound.setInteger("TransferCooldown", this.transferCooldown);

        if (this.hasCustomName())
        {
            compound.setString("CustomName", this.customName);
        }
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    public void markDirty()
    {
        super.markDirty();
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return this.inventory.length;
    }

    /**
     * Returns the stack in the given slot.
     */
    public ItemStack getStackInSlot(int index)
    {
        return this.inventory[index];
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    public ItemStack decrStackSize(int index, int count)
    {
        if (this.inventory[index] != null)
        {
            if (this.inventory[index].stackSize <= count)
            {
                ItemStack lvt_3_1_ = this.inventory[index];
                this.inventory[index] = null;
                return lvt_3_1_;
            }
            else
            {
                ItemStack lvt_3_2_ = this.inventory[index].splitStack(count);

                if (this.inventory[index].stackSize == 0)
                {
                    this.inventory[index] = null;
                }

                return lvt_3_2_;
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
        if (this.inventory[index] != null)
        {
            ItemStack lvt_2_1_ = this.inventory[index];
            this.inventory[index] = null;
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
        this.inventory[index] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }
    }

    /**
     * Get the name of this object. For players this returns their username
     */
    public String getName()
    {
        return this.hasCustomName() ? this.customName : "container.hopper";
    }

    /**
     * Returns true if this thing is named
     */
    public boolean hasCustomName()
    {
        return this.customName != null && this.customName.length() > 0;
    }

    public void setCustomName(String customNameIn)
    {
        this.customName = customNameIn;
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
     */
    public int getInventoryStackLimit()
    {
        return 64;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return this.worldObj.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
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
     * Like the old updateEntity(), except more generic.
     */
    public void update()
    {
        if (this.worldObj != null && !this.worldObj.isRemote)
        {
            --this.transferCooldown;

            if (!this.isOnTransferCooldown())
            {
                this.setTransferCooldown(0);
                this.updateHopper();
            }
        }
    }

    public boolean updateHopper()
    {
        if (this.worldObj != null && !this.worldObj.isRemote)
        {
            if (!this.isOnTransferCooldown() && BlockHopper.isEnabled(this.getBlockMetadata()))
            {
                boolean lvt_1_1_ = false;

                if (!this.isEmpty())
                {
                    lvt_1_1_ = this.transferItemsOut();
                }

                if (!this.isFull())
                {
                    lvt_1_1_ = captureDroppedItems(this) || lvt_1_1_;
                }

                if (lvt_1_1_)
                {
                    this.setTransferCooldown(8);
                    this.markDirty();
                    return true;
                }
            }

            return false;
        }
        else
        {
            return false;
        }
    }

    private boolean isEmpty()
    {
        for (ItemStack lvt_4_1_ : this.inventory)
        {
            if (lvt_4_1_ != null)
            {
                return false;
            }
        }

        return true;
    }

    private boolean isFull()
    {
        for (ItemStack lvt_4_1_ : this.inventory)
        {
            if (lvt_4_1_ == null || lvt_4_1_.stackSize != lvt_4_1_.getMaxStackSize())
            {
                return false;
            }
        }

        return true;
    }

    private boolean transferItemsOut()
    {
        IInventory lvt_1_1_ = this.getInventoryForHopperTransfer();

        if (lvt_1_1_ == null)
        {
            return false;
        }
        else
        {
            EnumFacing lvt_2_1_ = BlockHopper.getFacing(this.getBlockMetadata()).getOpposite();

            if (this.isInventoryFull(lvt_1_1_, lvt_2_1_))
            {
                return false;
            }
            else
            {
                for (int lvt_3_1_ = 0; lvt_3_1_ < this.getSizeInventory(); ++lvt_3_1_)
                {
                    if (this.getStackInSlot(lvt_3_1_) != null)
                    {
                        ItemStack lvt_4_1_ = this.getStackInSlot(lvt_3_1_).copy();
                        ItemStack lvt_5_1_ = putStackInInventoryAllSlots(lvt_1_1_, this.decrStackSize(lvt_3_1_, 1), lvt_2_1_);

                        if (lvt_5_1_ == null || lvt_5_1_.stackSize == 0)
                        {
                            lvt_1_1_.markDirty();
                            return true;
                        }

                        this.setInventorySlotContents(lvt_3_1_, lvt_4_1_);
                    }
                }

                return false;
            }
        }
    }

    /**
     * Returns false if the inventory has any room to place items in
     */
    private boolean isInventoryFull(IInventory inventoryIn, EnumFacing side)
    {
        if (inventoryIn instanceof ISidedInventory)
        {
            ISidedInventory lvt_3_1_ = (ISidedInventory)inventoryIn;
            int[] lvt_4_1_ = lvt_3_1_.getSlotsForFace(side);

            for (int lvt_5_1_ = 0; lvt_5_1_ < lvt_4_1_.length; ++lvt_5_1_)
            {
                ItemStack lvt_6_1_ = lvt_3_1_.getStackInSlot(lvt_4_1_[lvt_5_1_]);

                if (lvt_6_1_ == null || lvt_6_1_.stackSize != lvt_6_1_.getMaxStackSize())
                {
                    return false;
                }
            }
        }
        else
        {
            int lvt_3_2_ = inventoryIn.getSizeInventory();

            for (int lvt_4_2_ = 0; lvt_4_2_ < lvt_3_2_; ++lvt_4_2_)
            {
                ItemStack lvt_5_2_ = inventoryIn.getStackInSlot(lvt_4_2_);

                if (lvt_5_2_ == null || lvt_5_2_.stackSize != lvt_5_2_.getMaxStackSize())
                {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Returns false if the specified IInventory contains any items
     */
    private static boolean isInventoryEmpty(IInventory inventoryIn, EnumFacing side)
    {
        if (inventoryIn instanceof ISidedInventory)
        {
            ISidedInventory lvt_2_1_ = (ISidedInventory)inventoryIn;
            int[] lvt_3_1_ = lvt_2_1_.getSlotsForFace(side);

            for (int lvt_4_1_ = 0; lvt_4_1_ < lvt_3_1_.length; ++lvt_4_1_)
            {
                if (lvt_2_1_.getStackInSlot(lvt_3_1_[lvt_4_1_]) != null)
                {
                    return false;
                }
            }
        }
        else
        {
            int lvt_2_2_ = inventoryIn.getSizeInventory();

            for (int lvt_3_2_ = 0; lvt_3_2_ < lvt_2_2_; ++lvt_3_2_)
            {
                if (inventoryIn.getStackInSlot(lvt_3_2_) != null)
                {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean captureDroppedItems(IHopper p_145891_0_)
    {
        IInventory lvt_1_1_ = getHopperInventory(p_145891_0_);

        if (lvt_1_1_ != null)
        {
            EnumFacing lvt_2_1_ = EnumFacing.DOWN;

            if (isInventoryEmpty(lvt_1_1_, lvt_2_1_))
            {
                return false;
            }

            if (lvt_1_1_ instanceof ISidedInventory)
            {
                ISidedInventory lvt_3_1_ = (ISidedInventory)lvt_1_1_;
                int[] lvt_4_1_ = lvt_3_1_.getSlotsForFace(lvt_2_1_);

                for (int lvt_5_1_ = 0; lvt_5_1_ < lvt_4_1_.length; ++lvt_5_1_)
                {
                    if (pullItemFromSlot(p_145891_0_, lvt_1_1_, lvt_4_1_[lvt_5_1_], lvt_2_1_))
                    {
                        return true;
                    }
                }
            }
            else
            {
                int lvt_3_2_ = lvt_1_1_.getSizeInventory();

                for (int lvt_4_2_ = 0; lvt_4_2_ < lvt_3_2_; ++lvt_4_2_)
                {
                    if (pullItemFromSlot(p_145891_0_, lvt_1_1_, lvt_4_2_, lvt_2_1_))
                    {
                        return true;
                    }
                }
            }
        }
        else
        {
            for (EntityItem lvt_3_3_ : func_181556_a(p_145891_0_.getWorld(), p_145891_0_.getXPos(), p_145891_0_.getYPos() + 1.0D, p_145891_0_.getZPos()))
            {
                if (putDropInInventoryAllSlots(p_145891_0_, lvt_3_3_))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Pulls from the specified slot in the inventory and places in any available slot in the hopper. Returns true if
     * the entire stack was moved
     */
    private static boolean pullItemFromSlot(IHopper hopper, IInventory inventoryIn, int index, EnumFacing direction)
    {
        ItemStack lvt_4_1_ = inventoryIn.getStackInSlot(index);

        if (lvt_4_1_ != null && canExtractItemFromSlot(inventoryIn, lvt_4_1_, index, direction))
        {
            ItemStack lvt_5_1_ = lvt_4_1_.copy();
            ItemStack lvt_6_1_ = putStackInInventoryAllSlots(hopper, inventoryIn.decrStackSize(index, 1), (EnumFacing)null);

            if (lvt_6_1_ == null || lvt_6_1_.stackSize == 0)
            {
                inventoryIn.markDirty();
                return true;
            }

            inventoryIn.setInventorySlotContents(index, lvt_5_1_);
        }

        return false;
    }

    /**
     * Attempts to place the passed EntityItem's stack into the inventory using as many slots as possible. Returns false
     * if the stackSize of the drop was not depleted.
     */
    public static boolean putDropInInventoryAllSlots(IInventory p_145898_0_, EntityItem itemIn)
    {
        boolean lvt_2_1_ = false;

        if (itemIn == null)
        {
            return false;
        }
        else
        {
            ItemStack lvt_3_1_ = itemIn.getEntityItem().copy();
            ItemStack lvt_4_1_ = putStackInInventoryAllSlots(p_145898_0_, lvt_3_1_, (EnumFacing)null);

            if (lvt_4_1_ != null && lvt_4_1_.stackSize != 0)
            {
                itemIn.setEntityItemStack(lvt_4_1_);
            }
            else
            {
                lvt_2_1_ = true;
                itemIn.setDead();
            }

            return lvt_2_1_;
        }
    }

    /**
     * Attempts to place the passed stack in the inventory, using as many slots as required. Returns leftover items
     */
    public static ItemStack putStackInInventoryAllSlots(IInventory inventoryIn, ItemStack stack, EnumFacing side)
    {
        if (inventoryIn instanceof ISidedInventory && side != null)
        {
            ISidedInventory lvt_3_1_ = (ISidedInventory)inventoryIn;
            int[] lvt_4_1_ = lvt_3_1_.getSlotsForFace(side);

            for (int lvt_5_1_ = 0; lvt_5_1_ < lvt_4_1_.length && stack != null && stack.stackSize > 0; ++lvt_5_1_)
            {
                stack = insertStack(inventoryIn, stack, lvt_4_1_[lvt_5_1_], side);
            }
        }
        else
        {
            int lvt_3_2_ = inventoryIn.getSizeInventory();

            for (int lvt_4_2_ = 0; lvt_4_2_ < lvt_3_2_ && stack != null && stack.stackSize > 0; ++lvt_4_2_)
            {
                stack = insertStack(inventoryIn, stack, lvt_4_2_, side);
            }
        }

        if (stack != null && stack.stackSize == 0)
        {
            stack = null;
        }

        return stack;
    }

    /**
     * Can this hopper insert the specified item from the specified slot on the specified side?
     */
    private static boolean canInsertItemInSlot(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side)
    {
        return !inventoryIn.isItemValidForSlot(index, stack) ? false : !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory)inventoryIn).canInsertItem(index, stack, side);
    }

    /**
     * Can this hopper extract the specified item from the specified slot on the specified side?
     */
    private static boolean canExtractItemFromSlot(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side)
    {
        return !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory)inventoryIn).canExtractItem(index, stack, side);
    }

    /**
     * Insert the specified stack to the specified inventory and return any leftover items
     */
    private static ItemStack insertStack(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side)
    {
        ItemStack lvt_4_1_ = inventoryIn.getStackInSlot(index);

        if (canInsertItemInSlot(inventoryIn, stack, index, side))
        {
            boolean lvt_5_1_ = false;

            if (lvt_4_1_ == null)
            {
                inventoryIn.setInventorySlotContents(index, stack);
                stack = null;
                lvt_5_1_ = true;
            }
            else if (canCombine(lvt_4_1_, stack))
            {
                int lvt_6_1_ = stack.getMaxStackSize() - lvt_4_1_.stackSize;
                int lvt_7_1_ = Math.min(stack.stackSize, lvt_6_1_);
                stack.stackSize -= lvt_7_1_;
                lvt_4_1_.stackSize += lvt_7_1_;
                lvt_5_1_ = lvt_7_1_ > 0;
            }

            if (lvt_5_1_)
            {
                if (inventoryIn instanceof TileEntityHopper)
                {
                    TileEntityHopper lvt_6_2_ = (TileEntityHopper)inventoryIn;

                    if (lvt_6_2_.mayTransfer())
                    {
                        lvt_6_2_.setTransferCooldown(8);
                    }

                    inventoryIn.markDirty();
                }

                inventoryIn.markDirty();
            }
        }

        return stack;
    }

    /**
     * Returns the IInventory that this hopper is pointing into
     */
    private IInventory getInventoryForHopperTransfer()
    {
        EnumFacing lvt_1_1_ = BlockHopper.getFacing(this.getBlockMetadata());
        return getInventoryAtPosition(this.getWorld(), (double)(this.pos.getX() + lvt_1_1_.getFrontOffsetX()), (double)(this.pos.getY() + lvt_1_1_.getFrontOffsetY()), (double)(this.pos.getZ() + lvt_1_1_.getFrontOffsetZ()));
    }

    /**
     * Returns the IInventory for the specified hopper
     */
    public static IInventory getHopperInventory(IHopper hopper)
    {
        return getInventoryAtPosition(hopper.getWorld(), hopper.getXPos(), hopper.getYPos() + 1.0D, hopper.getZPos());
    }

    public static List<EntityItem> func_181556_a(World p_181556_0_, double p_181556_1_, double p_181556_3_, double p_181556_5_)
    {
        return p_181556_0_.<EntityItem>getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(p_181556_1_ - 0.5D, p_181556_3_ - 0.5D, p_181556_5_ - 0.5D, p_181556_1_ + 0.5D, p_181556_3_ + 0.5D, p_181556_5_ + 0.5D), EntitySelectors.selectAnything);
    }

    /**
     * Returns the IInventory (if applicable) of the TileEntity at the specified position
     */
    public static IInventory getInventoryAtPosition(World worldIn, double x, double y, double z)
    {
        IInventory lvt_7_1_ = null;
        int lvt_8_1_ = MathHelper.floor_double(x);
        int lvt_9_1_ = MathHelper.floor_double(y);
        int lvt_10_1_ = MathHelper.floor_double(z);
        BlockPos lvt_11_1_ = new BlockPos(lvt_8_1_, lvt_9_1_, lvt_10_1_);
        Block lvt_12_1_ = worldIn.getBlockState(lvt_11_1_).getBlock();

        if (lvt_12_1_.hasTileEntity())
        {
            TileEntity lvt_13_1_ = worldIn.getTileEntity(lvt_11_1_);

            if (lvt_13_1_ instanceof IInventory)
            {
                lvt_7_1_ = (IInventory)lvt_13_1_;

                if (lvt_7_1_ instanceof TileEntityChest && lvt_12_1_ instanceof BlockChest)
                {
                    lvt_7_1_ = ((BlockChest)lvt_12_1_).getLockableContainer(worldIn, lvt_11_1_);
                }
            }
        }

        if (lvt_7_1_ == null)
        {
            List<Entity> lvt_13_2_ = worldIn.getEntitiesInAABBexcluding((Entity)null, new AxisAlignedBB(x - 0.5D, y - 0.5D, z - 0.5D, x + 0.5D, y + 0.5D, z + 0.5D), EntitySelectors.selectInventories);

            if (lvt_13_2_.size() > 0)
            {
                lvt_7_1_ = (IInventory)lvt_13_2_.get(worldIn.rand.nextInt(lvt_13_2_.size()));
            }
        }

        return lvt_7_1_;
    }

    private static boolean canCombine(ItemStack stack1, ItemStack stack2)
    {
        return stack1.getItem() != stack2.getItem() ? false : (stack1.getMetadata() != stack2.getMetadata() ? false : (stack1.stackSize > stack1.getMaxStackSize() ? false : ItemStack.areItemStackTagsEqual(stack1, stack2)));
    }

    /**
     * Gets the world X position for this hopper entity.
     */
    public double getXPos()
    {
        return (double)this.pos.getX() + 0.5D;
    }

    /**
     * Gets the world Y position for this hopper entity.
     */
    public double getYPos()
    {
        return (double)this.pos.getY() + 0.5D;
    }

    /**
     * Gets the world Z position for this hopper entity.
     */
    public double getZPos()
    {
        return (double)this.pos.getZ() + 0.5D;
    }

    public void setTransferCooldown(int ticks)
    {
        this.transferCooldown = ticks;
    }

    public boolean isOnTransferCooldown()
    {
        return this.transferCooldown > 0;
    }

    public boolean mayTransfer()
    {
        return this.transferCooldown <= 1;
    }

    public String getGuiID()
    {
        return "minecraft:hopper";
    }

    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
    {
        return new ContainerHopper(playerInventory, this, playerIn);
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
        for (int lvt_1_1_ = 0; lvt_1_1_ < this.inventory.length; ++lvt_1_1_)
        {
            this.inventory[lvt_1_1_] = null;
        }
    }
}
