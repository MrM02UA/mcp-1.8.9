package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;

public class TileEntityBeacon extends TileEntityLockable implements ITickable, IInventory
{
    /** List of effects that Beacon can apply */
    public static final Potion[][] effectsList = new Potion[][] {{Potion.moveSpeed, Potion.digSpeed}, {Potion.resistance, Potion.jump}, {Potion.damageBoost}, {Potion.regeneration}};
    private final List<TileEntityBeacon.BeamSegment> beamSegments = Lists.newArrayList();
    private long beamRenderCounter;
    private float field_146014_j;
    private boolean isComplete;

    /** Level of this beacon's pyramid. */
    private int levels = -1;

    /** Primary potion effect given by this beacon. */
    private int primaryEffect;

    /** Secondary potion effect given by this beacon. */
    private int secondaryEffect;

    /** Item given to this beacon as payment. */
    private ItemStack payment;
    private String customName;

    /**
     * Like the old updateEntity(), except more generic.
     */
    public void update()
    {
        if (this.worldObj.getTotalWorldTime() % 80L == 0L)
        {
            this.updateBeacon();
        }
    }

    public void updateBeacon()
    {
        this.updateSegmentColors();
        this.addEffectsToPlayers();
    }

    private void addEffectsToPlayers()
    {
        if (this.isComplete && this.levels > 0 && !this.worldObj.isRemote && this.primaryEffect > 0)
        {
            double lvt_1_1_ = (double)(this.levels * 10 + 10);
            int lvt_3_1_ = 0;

            if (this.levels >= 4 && this.primaryEffect == this.secondaryEffect)
            {
                lvt_3_1_ = 1;
            }

            int lvt_4_1_ = this.pos.getX();
            int lvt_5_1_ = this.pos.getY();
            int lvt_6_1_ = this.pos.getZ();
            AxisAlignedBB lvt_7_1_ = (new AxisAlignedBB((double)lvt_4_1_, (double)lvt_5_1_, (double)lvt_6_1_, (double)(lvt_4_1_ + 1), (double)(lvt_5_1_ + 1), (double)(lvt_6_1_ + 1))).expand(lvt_1_1_, lvt_1_1_, lvt_1_1_).addCoord(0.0D, (double)this.worldObj.getHeight(), 0.0D);
            List<EntityPlayer> lvt_8_1_ = this.worldObj.<EntityPlayer>getEntitiesWithinAABB(EntityPlayer.class, lvt_7_1_);

            for (EntityPlayer lvt_10_1_ : lvt_8_1_)
            {
                lvt_10_1_.addPotionEffect(new PotionEffect(this.primaryEffect, 180, lvt_3_1_, true, true));
            }

            if (this.levels >= 4 && this.primaryEffect != this.secondaryEffect && this.secondaryEffect > 0)
            {
                for (EntityPlayer lvt_10_2_ : lvt_8_1_)
                {
                    lvt_10_2_.addPotionEffect(new PotionEffect(this.secondaryEffect, 180, 0, true, true));
                }
            }
        }
    }

    private void updateSegmentColors()
    {
        int lvt_1_1_ = this.levels;
        int lvt_2_1_ = this.pos.getX();
        int lvt_3_1_ = this.pos.getY();
        int lvt_4_1_ = this.pos.getZ();
        this.levels = 0;
        this.beamSegments.clear();
        this.isComplete = true;
        TileEntityBeacon.BeamSegment lvt_5_1_ = new TileEntityBeacon.BeamSegment(EntitySheep.getDyeRgb(EnumDyeColor.WHITE));
        this.beamSegments.add(lvt_5_1_);
        boolean lvt_6_1_ = true;
        BlockPos.MutableBlockPos lvt_7_1_ = new BlockPos.MutableBlockPos();

        for (int lvt_8_1_ = lvt_3_1_ + 1; lvt_8_1_ < 256; ++lvt_8_1_)
        {
            IBlockState lvt_9_1_ = this.worldObj.getBlockState(lvt_7_1_.set(lvt_2_1_, lvt_8_1_, lvt_4_1_));
            float[] lvt_10_1_;

            if (lvt_9_1_.getBlock() == Blocks.stained_glass)
            {
                lvt_10_1_ = EntitySheep.getDyeRgb((EnumDyeColor)lvt_9_1_.getValue(BlockStainedGlass.COLOR));
            }
            else
            {
                if (lvt_9_1_.getBlock() != Blocks.stained_glass_pane)
                {
                    if (lvt_9_1_.getBlock().getLightOpacity() >= 15 && lvt_9_1_.getBlock() != Blocks.bedrock)
                    {
                        this.isComplete = false;
                        this.beamSegments.clear();
                        break;
                    }

                    lvt_5_1_.incrementHeight();
                    continue;
                }

                lvt_10_1_ = EntitySheep.getDyeRgb((EnumDyeColor)lvt_9_1_.getValue(BlockStainedGlassPane.COLOR));
            }

            if (!lvt_6_1_)
            {
                lvt_10_1_ = new float[] {(lvt_5_1_.getColors()[0] + lvt_10_1_[0]) / 2.0F, (lvt_5_1_.getColors()[1] + lvt_10_1_[1]) / 2.0F, (lvt_5_1_.getColors()[2] + lvt_10_1_[2]) / 2.0F};
            }

            if (Arrays.equals(lvt_10_1_, lvt_5_1_.getColors()))
            {
                lvt_5_1_.incrementHeight();
            }
            else
            {
                lvt_5_1_ = new TileEntityBeacon.BeamSegment(lvt_10_1_);
                this.beamSegments.add(lvt_5_1_);
            }

            lvt_6_1_ = false;
        }

        if (this.isComplete)
        {
            for (int lvt_8_2_ = 1; lvt_8_2_ <= 4; this.levels = lvt_8_2_++)
            {
                int lvt_9_2_ = lvt_3_1_ - lvt_8_2_;

                if (lvt_9_2_ < 0)
                {
                    break;
                }

                boolean lvt_10_4_ = true;

                for (int lvt_11_1_ = lvt_2_1_ - lvt_8_2_; lvt_11_1_ <= lvt_2_1_ + lvt_8_2_ && lvt_10_4_; ++lvt_11_1_)
                {
                    for (int lvt_12_1_ = lvt_4_1_ - lvt_8_2_; lvt_12_1_ <= lvt_4_1_ + lvt_8_2_; ++lvt_12_1_)
                    {
                        Block lvt_13_1_ = this.worldObj.getBlockState(new BlockPos(lvt_11_1_, lvt_9_2_, lvt_12_1_)).getBlock();

                        if (lvt_13_1_ != Blocks.emerald_block && lvt_13_1_ != Blocks.gold_block && lvt_13_1_ != Blocks.diamond_block && lvt_13_1_ != Blocks.iron_block)
                        {
                            lvt_10_4_ = false;
                            break;
                        }
                    }
                }

                if (!lvt_10_4_)
                {
                    break;
                }
            }

            if (this.levels == 0)
            {
                this.isComplete = false;
            }
        }

        if (!this.worldObj.isRemote && this.levels == 4 && lvt_1_1_ < this.levels)
        {
            for (EntityPlayer lvt_9_3_ : this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, (new AxisAlignedBB((double)lvt_2_1_, (double)lvt_3_1_, (double)lvt_4_1_, (double)lvt_2_1_, (double)(lvt_3_1_ - 4), (double)lvt_4_1_)).expand(10.0D, 5.0D, 10.0D)))
            {
                lvt_9_3_.triggerAchievement(AchievementList.fullBeacon);
            }
        }
    }

    public List<TileEntityBeacon.BeamSegment> getBeamSegments()
    {
        return this.beamSegments;
    }

    public float shouldBeamRender()
    {
        if (!this.isComplete)
        {
            return 0.0F;
        }
        else
        {
            int lvt_1_1_ = (int)(this.worldObj.getTotalWorldTime() - this.beamRenderCounter);
            this.beamRenderCounter = this.worldObj.getTotalWorldTime();

            if (lvt_1_1_ > 1)
            {
                this.field_146014_j -= (float)lvt_1_1_ / 40.0F;

                if (this.field_146014_j < 0.0F)
                {
                    this.field_146014_j = 0.0F;
                }
            }

            this.field_146014_j += 0.025F;

            if (this.field_146014_j > 1.0F)
            {
                this.field_146014_j = 1.0F;
            }

            return this.field_146014_j;
        }
    }

    /**
     * Allows for a specialized description packet to be created. This is often used to sync tile entity data from the
     * server to the client easily. For example this is used by signs to synchronise the text to be displayed.
     */
    public Packet getDescriptionPacket()
    {
        NBTTagCompound lvt_1_1_ = new NBTTagCompound();
        this.writeToNBT(lvt_1_1_);
        return new S35PacketUpdateTileEntity(this.pos, 3, lvt_1_1_);
    }

    public double getMaxRenderDistanceSquared()
    {
        return 65536.0D;
    }

    private int func_183001_h(int p_183001_1_)
    {
        if (p_183001_1_ >= 0 && p_183001_1_ < Potion.potionTypes.length && Potion.potionTypes[p_183001_1_] != null)
        {
            Potion lvt_2_1_ = Potion.potionTypes[p_183001_1_];
            return lvt_2_1_ != Potion.moveSpeed && lvt_2_1_ != Potion.digSpeed && lvt_2_1_ != Potion.resistance && lvt_2_1_ != Potion.jump && lvt_2_1_ != Potion.damageBoost && lvt_2_1_ != Potion.regeneration ? 0 : p_183001_1_;
        }
        else
        {
            return 0;
        }
    }

    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.primaryEffect = this.func_183001_h(compound.getInteger("Primary"));
        this.secondaryEffect = this.func_183001_h(compound.getInteger("Secondary"));
        this.levels = compound.getInteger("Levels");
    }

    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setInteger("Primary", this.primaryEffect);
        compound.setInteger("Secondary", this.secondaryEffect);
        compound.setInteger("Levels", this.levels);
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return 1;
    }

    /**
     * Returns the stack in the given slot.
     */
    public ItemStack getStackInSlot(int index)
    {
        return index == 0 ? this.payment : null;
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    public ItemStack decrStackSize(int index, int count)
    {
        if (index == 0 && this.payment != null)
        {
            if (count >= this.payment.stackSize)
            {
                ItemStack lvt_3_1_ = this.payment;
                this.payment = null;
                return lvt_3_1_;
            }
            else
            {
                this.payment.stackSize -= count;
                return new ItemStack(this.payment.getItem(), count, this.payment.getMetadata());
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
        if (index == 0 && this.payment != null)
        {
            ItemStack lvt_2_1_ = this.payment;
            this.payment = null;
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
        if (index == 0)
        {
            this.payment = stack;
        }
    }

    /**
     * Get the name of this object. For players this returns their username
     */
    public String getName()
    {
        return this.hasCustomName() ? this.customName : "container.beacon";
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
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
     */
    public int getInventoryStackLimit()
    {
        return 1;
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
        return stack.getItem() == Items.emerald || stack.getItem() == Items.diamond || stack.getItem() == Items.gold_ingot || stack.getItem() == Items.iron_ingot;
    }

    public String getGuiID()
    {
        return "minecraft:beacon";
    }

    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
    {
        return new ContainerBeacon(playerInventory, this);
    }

    public int getField(int id)
    {
        switch (id)
        {
            case 0:
                return this.levels;

            case 1:
                return this.primaryEffect;

            case 2:
                return this.secondaryEffect;

            default:
                return 0;
        }
    }

    public void setField(int id, int value)
    {
        switch (id)
        {
            case 0:
                this.levels = value;
                break;

            case 1:
                this.primaryEffect = this.func_183001_h(value);
                break;

            case 2:
                this.secondaryEffect = this.func_183001_h(value);
        }
    }

    public int getFieldCount()
    {
        return 3;
    }

    public void clear()
    {
        this.payment = null;
    }

    public boolean receiveClientEvent(int id, int type)
    {
        if (id == 1)
        {
            this.updateBeacon();
            return true;
        }
        else
        {
            return super.receiveClientEvent(id, type);
        }
    }

    public static class BeamSegment
    {
        private final float[] colors;
        private int height;

        public BeamSegment(float[] p_i45669_1_)
        {
            this.colors = p_i45669_1_;
            this.height = 1;
        }

        protected void incrementHeight()
        {
            ++this.height;
        }

        public float[] getColors()
        {
            return this.colors;
        }

        public int getHeight()
        {
            return this.height;
        }
    }
}
