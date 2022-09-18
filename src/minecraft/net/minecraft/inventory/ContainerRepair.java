package net.minecraft.inventory;

import java.util.Iterator;
import java.util.Map;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ContainerRepair extends Container
{
    private static final Logger logger = LogManager.getLogger();

    /** Here comes out item you merged and/or renamed. */
    private IInventory outputSlot;

    /**
     * The 2slots where you put your items in that you want to merge and/or rename.
     */
    private IInventory inputSlots;
    private World theWorld;
    private BlockPos selfPosition;

    /** The maximum cost of repairing/renaming in the anvil. */
    public int maximumCost;

    /** determined by damage of input item and stackSize of repair materials */
    private int materialCost;
    private String repairedItemName;

    /** The player that has this container open. */
    private final EntityPlayer thePlayer;

    public ContainerRepair(InventoryPlayer playerInventory, World worldIn, EntityPlayer player)
    {
        this(playerInventory, worldIn, BlockPos.ORIGIN, player);
    }

    public ContainerRepair(InventoryPlayer playerInventory, final World worldIn, final BlockPos blockPosIn, EntityPlayer player)
    {
        this.outputSlot = new InventoryCraftResult();
        this.inputSlots = new InventoryBasic("Repair", true, 2)
        {
            public void markDirty()
            {
                super.markDirty();
                ContainerRepair.this.onCraftMatrixChanged(this);
            }
        };
        this.selfPosition = blockPosIn;
        this.theWorld = worldIn;
        this.thePlayer = player;
        this.addSlotToContainer(new Slot(this.inputSlots, 0, 27, 47));
        this.addSlotToContainer(new Slot(this.inputSlots, 1, 76, 47));
        this.addSlotToContainer(new Slot(this.outputSlot, 2, 134, 47)
        {
            public boolean isItemValid(ItemStack stack)
            {
                return false;
            }
            public boolean canTakeStack(EntityPlayer playerIn)
            {
                return (playerIn.capabilities.isCreativeMode || playerIn.experienceLevel >= ContainerRepair.this.maximumCost) && ContainerRepair.this.maximumCost > 0 && this.getHasStack();
            }
            public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack)
            {
                if (!playerIn.capabilities.isCreativeMode)
                {
                    playerIn.addExperienceLevel(-ContainerRepair.this.maximumCost);
                }

                ContainerRepair.this.inputSlots.setInventorySlotContents(0, (ItemStack)null);

                if (ContainerRepair.this.materialCost > 0)
                {
                    ItemStack lvt_3_1_ = ContainerRepair.this.inputSlots.getStackInSlot(1);

                    if (lvt_3_1_ != null && lvt_3_1_.stackSize > ContainerRepair.this.materialCost)
                    {
                        lvt_3_1_.stackSize -= ContainerRepair.this.materialCost;
                        ContainerRepair.this.inputSlots.setInventorySlotContents(1, lvt_3_1_);
                    }
                    else
                    {
                        ContainerRepair.this.inputSlots.setInventorySlotContents(1, (ItemStack)null);
                    }
                }
                else
                {
                    ContainerRepair.this.inputSlots.setInventorySlotContents(1, (ItemStack)null);
                }

                ContainerRepair.this.maximumCost = 0;
                IBlockState lvt_3_2_ = worldIn.getBlockState(blockPosIn);

                if (!playerIn.capabilities.isCreativeMode && !worldIn.isRemote && lvt_3_2_.getBlock() == Blocks.anvil && playerIn.getRNG().nextFloat() < 0.12F)
                {
                    int lvt_4_1_ = ((Integer)lvt_3_2_.getValue(BlockAnvil.DAMAGE)).intValue();
                    ++lvt_4_1_;

                    if (lvt_4_1_ > 2)
                    {
                        worldIn.setBlockToAir(blockPosIn);
                        worldIn.playAuxSFX(1020, blockPosIn, 0);
                    }
                    else
                    {
                        worldIn.setBlockState(blockPosIn, lvt_3_2_.withProperty(BlockAnvil.DAMAGE, Integer.valueOf(lvt_4_1_)), 2);
                        worldIn.playAuxSFX(1021, blockPosIn, 0);
                    }
                }
                else if (!worldIn.isRemote)
                {
                    worldIn.playAuxSFX(1021, blockPosIn, 0);
                }
            }
        });

        for (int lvt_5_1_ = 0; lvt_5_1_ < 3; ++lvt_5_1_)
        {
            for (int lvt_6_1_ = 0; lvt_6_1_ < 9; ++lvt_6_1_)
            {
                this.addSlotToContainer(new Slot(playerInventory, lvt_6_1_ + lvt_5_1_ * 9 + 9, 8 + lvt_6_1_ * 18, 84 + lvt_5_1_ * 18));
            }
        }

        for (int lvt_5_2_ = 0; lvt_5_2_ < 9; ++lvt_5_2_)
        {
            this.addSlotToContainer(new Slot(playerInventory, lvt_5_2_, 8 + lvt_5_2_ * 18, 142));
        }
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        super.onCraftMatrixChanged(inventoryIn);

        if (inventoryIn == this.inputSlots)
        {
            this.updateRepairOutput();
        }
    }

    /**
     * called when the Anvil Input Slot changes, calculates the new result and puts it in the output slot
     */
    public void updateRepairOutput()
    {
        int lvt_1_1_ = 0;
        int lvt_2_1_ = 1;
        int lvt_3_1_ = 1;
        int lvt_4_1_ = 1;
        int lvt_5_1_ = 2;
        int lvt_6_1_ = 1;
        int lvt_7_1_ = 1;
        ItemStack lvt_8_1_ = this.inputSlots.getStackInSlot(0);
        this.maximumCost = 1;
        int lvt_9_1_ = 0;
        int lvt_10_1_ = 0;
        int lvt_11_1_ = 0;

        if (lvt_8_1_ == null)
        {
            this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
            this.maximumCost = 0;
        }
        else
        {
            ItemStack lvt_12_1_ = lvt_8_1_.copy();
            ItemStack lvt_13_1_ = this.inputSlots.getStackInSlot(1);
            Map<Integer, Integer> lvt_14_1_ = EnchantmentHelper.getEnchantments(lvt_12_1_);
            boolean lvt_15_1_ = false;
            lvt_10_1_ = lvt_10_1_ + lvt_8_1_.getRepairCost() + (lvt_13_1_ == null ? 0 : lvt_13_1_.getRepairCost());
            this.materialCost = 0;

            if (lvt_13_1_ != null)
            {
                lvt_15_1_ = lvt_13_1_.getItem() == Items.enchanted_book && Items.enchanted_book.getEnchantments(lvt_13_1_).tagCount() > 0;

                if (lvt_12_1_.isItemStackDamageable() && lvt_12_1_.getItem().getIsRepairable(lvt_8_1_, lvt_13_1_))
                {
                    int lvt_16_1_ = Math.min(lvt_12_1_.getItemDamage(), lvt_12_1_.getMaxDamage() / 4);

                    if (lvt_16_1_ <= 0)
                    {
                        this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
                        this.maximumCost = 0;
                        return;
                    }

                    int lvt_17_1_;

                    for (lvt_17_1_ = 0; lvt_16_1_ > 0 && lvt_17_1_ < lvt_13_1_.stackSize; ++lvt_17_1_)
                    {
                        int lvt_18_1_ = lvt_12_1_.getItemDamage() - lvt_16_1_;
                        lvt_12_1_.setItemDamage(lvt_18_1_);
                        ++lvt_9_1_;
                        lvt_16_1_ = Math.min(lvt_12_1_.getItemDamage(), lvt_12_1_.getMaxDamage() / 4);
                    }

                    this.materialCost = lvt_17_1_;
                }
                else
                {
                    if (!lvt_15_1_ && (lvt_12_1_.getItem() != lvt_13_1_.getItem() || !lvt_12_1_.isItemStackDamageable()))
                    {
                        this.outputSlot.setInventorySlotContents(0, (ItemStack)null);
                        this.maximumCost = 0;
                        return;
                    }

                    if (lvt_12_1_.isItemStackDamageable() && !lvt_15_1_)
                    {
                        int lvt_16_2_ = lvt_8_1_.getMaxDamage() - lvt_8_1_.getItemDamage();
                        int lvt_17_2_ = lvt_13_1_.getMaxDamage() - lvt_13_1_.getItemDamage();
                        int lvt_18_2_ = lvt_17_2_ + lvt_12_1_.getMaxDamage() * 12 / 100;
                        int lvt_19_1_ = lvt_16_2_ + lvt_18_2_;
                        int lvt_20_1_ = lvt_12_1_.getMaxDamage() - lvt_19_1_;

                        if (lvt_20_1_ < 0)
                        {
                            lvt_20_1_ = 0;
                        }

                        if (lvt_20_1_ < lvt_12_1_.getMetadata())
                        {
                            lvt_12_1_.setItemDamage(lvt_20_1_);
                            lvt_9_1_ += 2;
                        }
                    }

                    Map<Integer, Integer> lvt_16_3_ = EnchantmentHelper.getEnchantments(lvt_13_1_);
                    Iterator lvt_17_3_ = lvt_16_3_.keySet().iterator();

                    while (lvt_17_3_.hasNext())
                    {
                        int lvt_18_3_ = ((Integer)lvt_17_3_.next()).intValue();
                        Enchantment lvt_19_2_ = Enchantment.getEnchantmentById(lvt_18_3_);

                        if (lvt_19_2_ != null)
                        {
                            int lvt_20_2_ = lvt_14_1_.containsKey(Integer.valueOf(lvt_18_3_)) ? ((Integer)lvt_14_1_.get(Integer.valueOf(lvt_18_3_))).intValue() : 0;
                            int lvt_21_1_ = ((Integer)lvt_16_3_.get(Integer.valueOf(lvt_18_3_))).intValue();
                            int var10000;

                            if (lvt_20_2_ == lvt_21_1_)
                            {
                                ++lvt_21_1_;
                                var10000 = lvt_21_1_;
                            }
                            else
                            {
                                var10000 = Math.max(lvt_21_1_, lvt_20_2_);
                            }

                            lvt_21_1_ = var10000;
                            boolean lvt_22_1_ = lvt_19_2_.canApply(lvt_8_1_);

                            if (this.thePlayer.capabilities.isCreativeMode || lvt_8_1_.getItem() == Items.enchanted_book)
                            {
                                lvt_22_1_ = true;
                            }

                            Iterator lvt_23_1_ = lvt_14_1_.keySet().iterator();

                            while (lvt_23_1_.hasNext())
                            {
                                int lvt_24_1_ = ((Integer)lvt_23_1_.next()).intValue();

                                if (lvt_24_1_ != lvt_18_3_ && !lvt_19_2_.canApplyTogether(Enchantment.getEnchantmentById(lvt_24_1_)))
                                {
                                    lvt_22_1_ = false;
                                    ++lvt_9_1_;
                                }
                            }

                            if (lvt_22_1_)
                            {
                                if (lvt_21_1_ > lvt_19_2_.getMaxLevel())
                                {
                                    lvt_21_1_ = lvt_19_2_.getMaxLevel();
                                }

                                lvt_14_1_.put(Integer.valueOf(lvt_18_3_), Integer.valueOf(lvt_21_1_));
                                int lvt_23_2_ = 0;

                                switch (lvt_19_2_.getWeight())
                                {
                                    case 1:
                                        lvt_23_2_ = 8;
                                        break;

                                    case 2:
                                        lvt_23_2_ = 4;

                                    case 3:
                                    case 4:
                                    case 6:
                                    case 7:
                                    case 8:
                                    case 9:
                                    default:
                                        break;

                                    case 5:
                                        lvt_23_2_ = 2;
                                        break;

                                    case 10:
                                        lvt_23_2_ = 1;
                                }

                                if (lvt_15_1_)
                                {
                                    lvt_23_2_ = Math.max(1, lvt_23_2_ / 2);
                                }

                                lvt_9_1_ += lvt_23_2_ * lvt_21_1_;
                            }
                        }
                    }
                }
            }

            if (StringUtils.isBlank(this.repairedItemName))
            {
                if (lvt_8_1_.hasDisplayName())
                {
                    lvt_11_1_ = 1;
                    lvt_9_1_ += lvt_11_1_;
                    lvt_12_1_.clearCustomName();
                }
            }
            else if (!this.repairedItemName.equals(lvt_8_1_.getDisplayName()))
            {
                lvt_11_1_ = 1;
                lvt_9_1_ += lvt_11_1_;
                lvt_12_1_.setStackDisplayName(this.repairedItemName);
            }

            this.maximumCost = lvt_10_1_ + lvt_9_1_;

            if (lvt_9_1_ <= 0)
            {
                lvt_12_1_ = null;
            }

            if (lvt_11_1_ == lvt_9_1_ && lvt_11_1_ > 0 && this.maximumCost >= 40)
            {
                this.maximumCost = 39;
            }

            if (this.maximumCost >= 40 && !this.thePlayer.capabilities.isCreativeMode)
            {
                lvt_12_1_ = null;
            }

            if (lvt_12_1_ != null)
            {
                int lvt_16_4_ = lvt_12_1_.getRepairCost();

                if (lvt_13_1_ != null && lvt_16_4_ < lvt_13_1_.getRepairCost())
                {
                    lvt_16_4_ = lvt_13_1_.getRepairCost();
                }

                lvt_16_4_ = lvt_16_4_ * 2 + 1;
                lvt_12_1_.setRepairCost(lvt_16_4_);
                EnchantmentHelper.setEnchantments(lvt_14_1_, lvt_12_1_);
            }

            this.outputSlot.setInventorySlotContents(0, lvt_12_1_);
            this.detectAndSendChanges();
        }
    }

    public void onCraftGuiOpened(ICrafting listener)
    {
        super.onCraftGuiOpened(listener);
        listener.sendProgressBarUpdate(this, 0, this.maximumCost);
    }

    public void updateProgressBar(int id, int data)
    {
        if (id == 0)
        {
            this.maximumCost = data;
        }
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);

        if (!this.theWorld.isRemote)
        {
            for (int lvt_2_1_ = 0; lvt_2_1_ < this.inputSlots.getSizeInventory(); ++lvt_2_1_)
            {
                ItemStack lvt_3_1_ = this.inputSlots.removeStackFromSlot(lvt_2_1_);

                if (lvt_3_1_ != null)
                {
                    playerIn.dropPlayerItemWithRandomChoice(lvt_3_1_, false);
                }
            }
        }
    }

    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.theWorld.getBlockState(this.selfPosition).getBlock() != Blocks.anvil ? false : playerIn.getDistanceSq((double)this.selfPosition.getX() + 0.5D, (double)this.selfPosition.getY() + 0.5D, (double)this.selfPosition.getZ() + 0.5D) <= 64.0D;
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

            if (index == 2)
            {
                if (!this.mergeItemStack(lvt_5_1_, 3, 39, true))
                {
                    return null;
                }

                lvt_4_1_.onSlotChange(lvt_5_1_, lvt_3_1_);
            }
            else if (index != 0 && index != 1)
            {
                if (index >= 3 && index < 39 && !this.mergeItemStack(lvt_5_1_, 0, 2, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(lvt_5_1_, 3, 39, false))
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
     * used by the Anvil GUI to update the Item Name being typed by the player
     */
    public void updateItemName(String newName)
    {
        this.repairedItemName = newName;

        if (this.getSlot(2).getHasStack())
        {
            ItemStack lvt_2_1_ = this.getSlot(2).getStack();

            if (StringUtils.isBlank(newName))
            {
                lvt_2_1_.clearCustomName();
            }
            else
            {
                lvt_2_1_.setStackDisplayName(this.repairedItemName);
            }
        }

        this.updateRepairOutput();
    }
}
