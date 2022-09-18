package net.minecraft.tileentity;

import java.util.Arrays;
import java.util.List;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileEntityBrewingStand extends TileEntityLockable implements ITickable, ISidedInventory
{
    /** an array of the input slot indices */
    private static final int[] inputSlots = new int[] {3};

    /** an array of the output slot indices */
    private static final int[] outputSlots = new int[] {0, 1, 2};

    /** The ItemStacks currently placed in the slots of the brewing stand */
    private ItemStack[] brewingItemStacks = new ItemStack[4];
    private int brewTime;

    /**
     * an integer with each bit specifying whether that slot of the stand contains a potion
     */
    private boolean[] filledSlots;

    /**
     * used to check if the current ingredient has been removed from the brewing stand during brewing
     */
    private Item ingredientID;
    private String customName;

    /**
     * Get the name of this object. For players this returns their username
     */
    public String getName()
    {
        return this.hasCustomName() ? this.customName : "container.brewing";
    }

    /**
     * Returns true if this thing is named
     */
    public boolean hasCustomName()
    {
        return this.customName != null && this.customName.length() > 0;
    }

    public void setName(String name)
    {
        this.customName = name;
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return this.brewingItemStacks.length;
    }

    /**
     * Like the old updateEntity(), except more generic.
     */
    public void update()
    {
        if (this.brewTime > 0)
        {
            --this.brewTime;

            if (this.brewTime == 0)
            {
                this.brewPotions();
                this.markDirty();
            }
            else if (!this.canBrew())
            {
                this.brewTime = 0;
                this.markDirty();
            }
            else if (this.ingredientID != this.brewingItemStacks[3].getItem())
            {
                this.brewTime = 0;
                this.markDirty();
            }
        }
        else if (this.canBrew())
        {
            this.brewTime = 400;
            this.ingredientID = this.brewingItemStacks[3].getItem();
        }

        if (!this.worldObj.isRemote)
        {
            boolean[] lvt_1_1_ = this.func_174902_m();

            if (!Arrays.equals(lvt_1_1_, this.filledSlots))
            {
                this.filledSlots = lvt_1_1_;
                IBlockState lvt_2_1_ = this.worldObj.getBlockState(this.getPos());

                if (!(lvt_2_1_.getBlock() instanceof BlockBrewingStand))
                {
                    return;
                }

                for (int lvt_3_1_ = 0; lvt_3_1_ < BlockBrewingStand.HAS_BOTTLE.length; ++lvt_3_1_)
                {
                    lvt_2_1_ = lvt_2_1_.withProperty(BlockBrewingStand.HAS_BOTTLE[lvt_3_1_], Boolean.valueOf(lvt_1_1_[lvt_3_1_]));
                }

                this.worldObj.setBlockState(this.pos, lvt_2_1_, 2);
            }
        }
    }

    private boolean canBrew()
    {
        if (this.brewingItemStacks[3] != null && this.brewingItemStacks[3].stackSize > 0)
        {
            ItemStack lvt_1_1_ = this.brewingItemStacks[3];

            if (!lvt_1_1_.getItem().isPotionIngredient(lvt_1_1_))
            {
                return false;
            }
            else
            {
                boolean lvt_2_1_ = false;

                for (int lvt_3_1_ = 0; lvt_3_1_ < 3; ++lvt_3_1_)
                {
                    if (this.brewingItemStacks[lvt_3_1_] != null && this.brewingItemStacks[lvt_3_1_].getItem() == Items.potionitem)
                    {
                        int lvt_4_1_ = this.brewingItemStacks[lvt_3_1_].getMetadata();
                        int lvt_5_1_ = this.getPotionResult(lvt_4_1_, lvt_1_1_);

                        if (!ItemPotion.isSplash(lvt_4_1_) && ItemPotion.isSplash(lvt_5_1_))
                        {
                            lvt_2_1_ = true;
                            break;
                        }

                        List<PotionEffect> lvt_6_1_ = Items.potionitem.getEffects(lvt_4_1_);
                        List<PotionEffect> lvt_7_1_ = Items.potionitem.getEffects(lvt_5_1_);

                        if ((lvt_4_1_ <= 0 || lvt_6_1_ != lvt_7_1_) && (lvt_6_1_ == null || !lvt_6_1_.equals(lvt_7_1_) && lvt_7_1_ != null) && lvt_4_1_ != lvt_5_1_)
                        {
                            lvt_2_1_ = true;
                            break;
                        }
                    }
                }

                return lvt_2_1_;
            }
        }
        else
        {
            return false;
        }
    }

    private void brewPotions()
    {
        if (this.canBrew())
        {
            ItemStack lvt_1_1_ = this.brewingItemStacks[3];

            for (int lvt_2_1_ = 0; lvt_2_1_ < 3; ++lvt_2_1_)
            {
                if (this.brewingItemStacks[lvt_2_1_] != null && this.brewingItemStacks[lvt_2_1_].getItem() == Items.potionitem)
                {
                    int lvt_3_1_ = this.brewingItemStacks[lvt_2_1_].getMetadata();
                    int lvt_4_1_ = this.getPotionResult(lvt_3_1_, lvt_1_1_);
                    List<PotionEffect> lvt_5_1_ = Items.potionitem.getEffects(lvt_3_1_);
                    List<PotionEffect> lvt_6_1_ = Items.potionitem.getEffects(lvt_4_1_);

                    if (lvt_3_1_ > 0 && lvt_5_1_ == lvt_6_1_ || lvt_5_1_ != null && (lvt_5_1_.equals(lvt_6_1_) || lvt_6_1_ == null))
                    {
                        if (!ItemPotion.isSplash(lvt_3_1_) && ItemPotion.isSplash(lvt_4_1_))
                        {
                            this.brewingItemStacks[lvt_2_1_].setItemDamage(lvt_4_1_);
                        }
                    }
                    else if (lvt_3_1_ != lvt_4_1_)
                    {
                        this.brewingItemStacks[lvt_2_1_].setItemDamage(lvt_4_1_);
                    }
                }
            }

            if (lvt_1_1_.getItem().hasContainerItem())
            {
                this.brewingItemStacks[3] = new ItemStack(lvt_1_1_.getItem().getContainerItem());
            }
            else
            {
                --this.brewingItemStacks[3].stackSize;

                if (this.brewingItemStacks[3].stackSize <= 0)
                {
                    this.brewingItemStacks[3] = null;
                }
            }
        }
    }

    /**
     * The result of brewing a potion of the specified damage value with an ingredient itemstack.
     */
    private int getPotionResult(int meta, ItemStack stack)
    {
        return stack == null ? meta : (stack.getItem().isPotionIngredient(stack) ? PotionHelper.applyIngredient(meta, stack.getItem().getPotionEffect(stack)) : meta);
    }

    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        NBTTagList lvt_2_1_ = compound.getTagList("Items", 10);
        this.brewingItemStacks = new ItemStack[this.getSizeInventory()];

        for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.tagCount(); ++lvt_3_1_)
        {
            NBTTagCompound lvt_4_1_ = lvt_2_1_.getCompoundTagAt(lvt_3_1_);
            int lvt_5_1_ = lvt_4_1_.getByte("Slot");

            if (lvt_5_1_ >= 0 && lvt_5_1_ < this.brewingItemStacks.length)
            {
                this.brewingItemStacks[lvt_5_1_] = ItemStack.loadItemStackFromNBT(lvt_4_1_);
            }
        }

        this.brewTime = compound.getShort("BrewTime");

        if (compound.hasKey("CustomName", 8))
        {
            this.customName = compound.getString("CustomName");
        }
    }

    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setShort("BrewTime", (short)this.brewTime);
        NBTTagList lvt_2_1_ = new NBTTagList();

        for (int lvt_3_1_ = 0; lvt_3_1_ < this.brewingItemStacks.length; ++lvt_3_1_)
        {
            if (this.brewingItemStacks[lvt_3_1_] != null)
            {
                NBTTagCompound lvt_4_1_ = new NBTTagCompound();
                lvt_4_1_.setByte("Slot", (byte)lvt_3_1_);
                this.brewingItemStacks[lvt_3_1_].writeToNBT(lvt_4_1_);
                lvt_2_1_.appendTag(lvt_4_1_);
            }
        }

        compound.setTag("Items", lvt_2_1_);

        if (this.hasCustomName())
        {
            compound.setString("CustomName", this.customName);
        }
    }

    /**
     * Returns the stack in the given slot.
     */
    public ItemStack getStackInSlot(int index)
    {
        return index >= 0 && index < this.brewingItemStacks.length ? this.brewingItemStacks[index] : null;
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    public ItemStack decrStackSize(int index, int count)
    {
        if (index >= 0 && index < this.brewingItemStacks.length)
        {
            ItemStack lvt_3_1_ = this.brewingItemStacks[index];
            this.brewingItemStacks[index] = null;
            return lvt_3_1_;
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
        if (index >= 0 && index < this.brewingItemStacks.length)
        {
            ItemStack lvt_2_1_ = this.brewingItemStacks[index];
            this.brewingItemStacks[index] = null;
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
        if (index >= 0 && index < this.brewingItemStacks.length)
        {
            this.brewingItemStacks[index] = stack;
        }
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
        return index == 3 ? stack.getItem().isPotionIngredient(stack) : stack.getItem() == Items.potionitem || stack.getItem() == Items.glass_bottle;
    }

    public boolean[] func_174902_m()
    {
        boolean[] lvt_1_1_ = new boolean[3];

        for (int lvt_2_1_ = 0; lvt_2_1_ < 3; ++lvt_2_1_)
        {
            if (this.brewingItemStacks[lvt_2_1_] != null)
            {
                lvt_1_1_[lvt_2_1_] = true;
            }
        }

        return lvt_1_1_;
    }

    public int[] getSlotsForFace(EnumFacing side)
    {
        return side == EnumFacing.UP ? inputSlots : outputSlots;
    }

    /**
     * Returns true if automation can insert the given item in the given slot from the given side. Args: slot, item,
     * side
     */
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction)
    {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    /**
     * Returns true if automation can extract the given item in the given slot from the given side. Args: slot, item,
     * side
     */
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
    {
        return true;
    }

    public String getGuiID()
    {
        return "minecraft:brewing_stand";
    }

    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
    {
        return new ContainerBrewingStand(playerInventory, this);
    }

    public int getField(int id)
    {
        switch (id)
        {
            case 0:
                return this.brewTime;

            default:
                return 0;
        }
    }

    public void setField(int id, int value)
    {
        switch (id)
        {
            case 0:
                this.brewTime = value;

            default:
        }
    }

    public int getFieldCount()
    {
        return 1;
    }

    public void clear()
    {
        for (int lvt_1_1_ = 0; lvt_1_1_ < this.brewingItemStacks.length; ++lvt_1_1_)
        {
            this.brewingItemStacks[lvt_1_1_] = null;
        }
    }
}
