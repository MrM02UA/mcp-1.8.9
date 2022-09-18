package net.minecraft.inventory;

import java.util.List;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ContainerEnchantment extends Container
{
    /** SlotEnchantmentTable object with ItemStack to be enchanted */
    public IInventory tableInventory;

    /** current world (for bookshelf counting) */
    private World worldPointer;
    private BlockPos position;
    private Random rand;
    public int xpSeed;

    /** 3-member array storing the enchantment levels of each slot */
    public int[] enchantLevels;
    public int[] enchantmentIds;

    public ContainerEnchantment(InventoryPlayer playerInv, World worldIn)
    {
        this(playerInv, worldIn, BlockPos.ORIGIN);
    }

    public ContainerEnchantment(InventoryPlayer playerInv, World worldIn, BlockPos pos)
    {
        this.tableInventory = new InventoryBasic("Enchant", true, 2)
        {
            public int getInventoryStackLimit()
            {
                return 64;
            }
            public void markDirty()
            {
                super.markDirty();
                ContainerEnchantment.this.onCraftMatrixChanged(this);
            }
        };
        this.rand = new Random();
        this.enchantLevels = new int[3];
        this.enchantmentIds = new int[] { -1, -1, -1};
        this.worldPointer = worldIn;
        this.position = pos;
        this.xpSeed = playerInv.player.getXPSeed();
        this.addSlotToContainer(new Slot(this.tableInventory, 0, 15, 47)
        {
            public boolean isItemValid(ItemStack stack)
            {
                return true;
            }
            public int getSlotStackLimit()
            {
                return 1;
            }
        });
        this.addSlotToContainer(new Slot(this.tableInventory, 1, 35, 47)
        {
            public boolean isItemValid(ItemStack stack)
            {
                return stack.getItem() == Items.dye && EnumDyeColor.byDyeDamage(stack.getMetadata()) == EnumDyeColor.BLUE;
            }
        });

        for (int lvt_4_1_ = 0; lvt_4_1_ < 3; ++lvt_4_1_)
        {
            for (int lvt_5_1_ = 0; lvt_5_1_ < 9; ++lvt_5_1_)
            {
                this.addSlotToContainer(new Slot(playerInv, lvt_5_1_ + lvt_4_1_ * 9 + 9, 8 + lvt_5_1_ * 18, 84 + lvt_4_1_ * 18));
            }
        }

        for (int lvt_4_2_ = 0; lvt_4_2_ < 9; ++lvt_4_2_)
        {
            this.addSlotToContainer(new Slot(playerInv, lvt_4_2_, 8 + lvt_4_2_ * 18, 142));
        }
    }

    public void onCraftGuiOpened(ICrafting listener)
    {
        super.onCraftGuiOpened(listener);
        listener.sendProgressBarUpdate(this, 0, this.enchantLevels[0]);
        listener.sendProgressBarUpdate(this, 1, this.enchantLevels[1]);
        listener.sendProgressBarUpdate(this, 2, this.enchantLevels[2]);
        listener.sendProgressBarUpdate(this, 3, this.xpSeed & -16);
        listener.sendProgressBarUpdate(this, 4, this.enchantmentIds[0]);
        listener.sendProgressBarUpdate(this, 5, this.enchantmentIds[1]);
        listener.sendProgressBarUpdate(this, 6, this.enchantmentIds[2]);
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int lvt_1_1_ = 0; lvt_1_1_ < this.crafters.size(); ++lvt_1_1_)
        {
            ICrafting lvt_2_1_ = (ICrafting)this.crafters.get(lvt_1_1_);
            lvt_2_1_.sendProgressBarUpdate(this, 0, this.enchantLevels[0]);
            lvt_2_1_.sendProgressBarUpdate(this, 1, this.enchantLevels[1]);
            lvt_2_1_.sendProgressBarUpdate(this, 2, this.enchantLevels[2]);
            lvt_2_1_.sendProgressBarUpdate(this, 3, this.xpSeed & -16);
            lvt_2_1_.sendProgressBarUpdate(this, 4, this.enchantmentIds[0]);
            lvt_2_1_.sendProgressBarUpdate(this, 5, this.enchantmentIds[1]);
            lvt_2_1_.sendProgressBarUpdate(this, 6, this.enchantmentIds[2]);
        }
    }

    public void updateProgressBar(int id, int data)
    {
        if (id >= 0 && id <= 2)
        {
            this.enchantLevels[id] = data;
        }
        else if (id == 3)
        {
            this.xpSeed = data;
        }
        else if (id >= 4 && id <= 6)
        {
            this.enchantmentIds[id - 4] = data;
        }
        else
        {
            super.updateProgressBar(id, data);
        }
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        if (inventoryIn == this.tableInventory)
        {
            ItemStack lvt_2_1_ = inventoryIn.getStackInSlot(0);

            if (lvt_2_1_ != null && lvt_2_1_.isItemEnchantable())
            {
                if (!this.worldPointer.isRemote)
                {
                    int lvt_3_2_ = 0;

                    for (int lvt_4_1_ = -1; lvt_4_1_ <= 1; ++lvt_4_1_)
                    {
                        for (int lvt_5_1_ = -1; lvt_5_1_ <= 1; ++lvt_5_1_)
                        {
                            if ((lvt_4_1_ != 0 || lvt_5_1_ != 0) && this.worldPointer.isAirBlock(this.position.add(lvt_5_1_, 0, lvt_4_1_)) && this.worldPointer.isAirBlock(this.position.add(lvt_5_1_, 1, lvt_4_1_)))
                            {
                                if (this.worldPointer.getBlockState(this.position.add(lvt_5_1_ * 2, 0, lvt_4_1_ * 2)).getBlock() == Blocks.bookshelf)
                                {
                                    ++lvt_3_2_;
                                }

                                if (this.worldPointer.getBlockState(this.position.add(lvt_5_1_ * 2, 1, lvt_4_1_ * 2)).getBlock() == Blocks.bookshelf)
                                {
                                    ++lvt_3_2_;
                                }

                                if (lvt_5_1_ != 0 && lvt_4_1_ != 0)
                                {
                                    if (this.worldPointer.getBlockState(this.position.add(lvt_5_1_ * 2, 0, lvt_4_1_)).getBlock() == Blocks.bookshelf)
                                    {
                                        ++lvt_3_2_;
                                    }

                                    if (this.worldPointer.getBlockState(this.position.add(lvt_5_1_ * 2, 1, lvt_4_1_)).getBlock() == Blocks.bookshelf)
                                    {
                                        ++lvt_3_2_;
                                    }

                                    if (this.worldPointer.getBlockState(this.position.add(lvt_5_1_, 0, lvt_4_1_ * 2)).getBlock() == Blocks.bookshelf)
                                    {
                                        ++lvt_3_2_;
                                    }

                                    if (this.worldPointer.getBlockState(this.position.add(lvt_5_1_, 1, lvt_4_1_ * 2)).getBlock() == Blocks.bookshelf)
                                    {
                                        ++lvt_3_2_;
                                    }
                                }
                            }
                        }
                    }

                    this.rand.setSeed((long)this.xpSeed);

                    for (int lvt_4_2_ = 0; lvt_4_2_ < 3; ++lvt_4_2_)
                    {
                        this.enchantLevels[lvt_4_2_] = EnchantmentHelper.calcItemStackEnchantability(this.rand, lvt_4_2_, lvt_3_2_, lvt_2_1_);
                        this.enchantmentIds[lvt_4_2_] = -1;

                        if (this.enchantLevels[lvt_4_2_] < lvt_4_2_ + 1)
                        {
                            this.enchantLevels[lvt_4_2_] = 0;
                        }
                    }

                    for (int lvt_4_3_ = 0; lvt_4_3_ < 3; ++lvt_4_3_)
                    {
                        if (this.enchantLevels[lvt_4_3_] > 0)
                        {
                            List<EnchantmentData> lvt_5_2_ = this.func_178148_a(lvt_2_1_, lvt_4_3_, this.enchantLevels[lvt_4_3_]);

                            if (lvt_5_2_ != null && !lvt_5_2_.isEmpty())
                            {
                                EnchantmentData lvt_6_1_ = (EnchantmentData)lvt_5_2_.get(this.rand.nextInt(lvt_5_2_.size()));
                                this.enchantmentIds[lvt_4_3_] = lvt_6_1_.enchantmentobj.effectId | lvt_6_1_.enchantmentLevel << 8;
                            }
                        }
                    }

                    this.detectAndSendChanges();
                }
            }
            else
            {
                for (int lvt_3_1_ = 0; lvt_3_1_ < 3; ++lvt_3_1_)
                {
                    this.enchantLevels[lvt_3_1_] = 0;
                    this.enchantmentIds[lvt_3_1_] = -1;
                }
            }
        }
    }

    /**
     * Handles the given Button-click on the server, currently only used by enchanting. Name is for legacy.
     */
    public boolean enchantItem(EntityPlayer playerIn, int id)
    {
        ItemStack lvt_3_1_ = this.tableInventory.getStackInSlot(0);
        ItemStack lvt_4_1_ = this.tableInventory.getStackInSlot(1);
        int lvt_5_1_ = id + 1;

        if ((lvt_4_1_ == null || lvt_4_1_.stackSize < lvt_5_1_) && !playerIn.capabilities.isCreativeMode)
        {
            return false;
        }
        else if (this.enchantLevels[id] > 0 && lvt_3_1_ != null && (playerIn.experienceLevel >= lvt_5_1_ && playerIn.experienceLevel >= this.enchantLevels[id] || playerIn.capabilities.isCreativeMode))
        {
            if (!this.worldPointer.isRemote)
            {
                List<EnchantmentData> lvt_6_1_ = this.func_178148_a(lvt_3_1_, id, this.enchantLevels[id]);
                boolean lvt_7_1_ = lvt_3_1_.getItem() == Items.book;

                if (lvt_6_1_ != null)
                {
                    playerIn.removeExperienceLevel(lvt_5_1_);

                    if (lvt_7_1_)
                    {
                        lvt_3_1_.setItem(Items.enchanted_book);
                    }

                    for (int lvt_8_1_ = 0; lvt_8_1_ < lvt_6_1_.size(); ++lvt_8_1_)
                    {
                        EnchantmentData lvt_9_1_ = (EnchantmentData)lvt_6_1_.get(lvt_8_1_);

                        if (lvt_7_1_)
                        {
                            Items.enchanted_book.addEnchantment(lvt_3_1_, lvt_9_1_);
                        }
                        else
                        {
                            lvt_3_1_.addEnchantment(lvt_9_1_.enchantmentobj, lvt_9_1_.enchantmentLevel);
                        }
                    }

                    if (!playerIn.capabilities.isCreativeMode)
                    {
                        lvt_4_1_.stackSize -= lvt_5_1_;

                        if (lvt_4_1_.stackSize <= 0)
                        {
                            this.tableInventory.setInventorySlotContents(1, (ItemStack)null);
                        }
                    }

                    playerIn.triggerAchievement(StatList.field_181739_W);
                    this.tableInventory.markDirty();
                    this.xpSeed = playerIn.getXPSeed();
                    this.onCraftMatrixChanged(this.tableInventory);
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    private List<EnchantmentData> func_178148_a(ItemStack stack, int p_178148_2_, int p_178148_3_)
    {
        this.rand.setSeed((long)(this.xpSeed + p_178148_2_));
        List<EnchantmentData> lvt_4_1_ = EnchantmentHelper.buildEnchantmentList(this.rand, stack, p_178148_3_);

        if (stack.getItem() == Items.book && lvt_4_1_ != null && lvt_4_1_.size() > 1)
        {
            lvt_4_1_.remove(this.rand.nextInt(lvt_4_1_.size()));
        }

        return lvt_4_1_;
    }

    public int getLapisAmount()
    {
        ItemStack lvt_1_1_ = this.tableInventory.getStackInSlot(1);
        return lvt_1_1_ == null ? 0 : lvt_1_1_.stackSize;
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);

        if (!this.worldPointer.isRemote)
        {
            for (int lvt_2_1_ = 0; lvt_2_1_ < this.tableInventory.getSizeInventory(); ++lvt_2_1_)
            {
                ItemStack lvt_3_1_ = this.tableInventory.removeStackFromSlot(lvt_2_1_);

                if (lvt_3_1_ != null)
                {
                    playerIn.dropPlayerItemWithRandomChoice(lvt_3_1_, false);
                }
            }
        }
    }

    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.worldPointer.getBlockState(this.position).getBlock() != Blocks.enchanting_table ? false : playerIn.getDistanceSq((double)this.position.getX() + 0.5D, (double)this.position.getY() + 0.5D, (double)this.position.getZ() + 0.5D) <= 64.0D;
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
                if (!this.mergeItemStack(lvt_5_1_, 2, 38, true))
                {
                    return null;
                }
            }
            else if (index == 1)
            {
                if (!this.mergeItemStack(lvt_5_1_, 2, 38, true))
                {
                    return null;
                }
            }
            else if (lvt_5_1_.getItem() == Items.dye && EnumDyeColor.byDyeDamage(lvt_5_1_.getMetadata()) == EnumDyeColor.BLUE)
            {
                if (!this.mergeItemStack(lvt_5_1_, 1, 2, true))
                {
                    return null;
                }
            }
            else
            {
                if (((Slot)this.inventorySlots.get(0)).getHasStack() || !((Slot)this.inventorySlots.get(0)).isItemValid(lvt_5_1_))
                {
                    return null;
                }

                if (lvt_5_1_.hasTagCompound() && lvt_5_1_.stackSize == 1)
                {
                    ((Slot)this.inventorySlots.get(0)).putStack(lvt_5_1_.copy());
                    lvt_5_1_.stackSize = 0;
                }
                else if (lvt_5_1_.stackSize >= 1)
                {
                    ((Slot)this.inventorySlots.get(0)).putStack(new ItemStack(lvt_5_1_.getItem(), 1, lvt_5_1_.getMetadata()));
                    --lvt_5_1_.stackSize;
                }
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
}
