package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class EntityItemFrame extends EntityHanging
{
    /** Chance for this item frame's item to drop from the frame. */
    private float itemDropChance = 1.0F;

    public EntityItemFrame(World worldIn)
    {
        super(worldIn);
    }

    public EntityItemFrame(World worldIn, BlockPos p_i45852_2_, EnumFacing p_i45852_3_)
    {
        super(worldIn, p_i45852_2_);
        this.updateFacingWithBoundingBox(p_i45852_3_);
    }

    protected void entityInit()
    {
        this.getDataWatcher().addObjectByDataType(8, 5);
        this.getDataWatcher().addObject(9, Byte.valueOf((byte)0));
    }

    public float getCollisionBorderSize()
    {
        return 0.0F;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isEntityInvulnerable(source))
        {
            return false;
        }
        else if (!source.isExplosion() && this.getDisplayedItem() != null)
        {
            if (!this.worldObj.isRemote)
            {
                this.dropItemOrSelf(source.getEntity(), false);
                this.setDisplayedItem((ItemStack)null);
            }

            return true;
        }
        else
        {
            return super.attackEntityFrom(source, amount);
        }
    }

    public int getWidthPixels()
    {
        return 12;
    }

    public int getHeightPixels()
    {
        return 12;
    }

    /**
     * Checks if the entity is in range to render by using the past in distance and comparing it to its average edge
     * length * 64 * renderDistanceWeight Args: distance
     */
    public boolean isInRangeToRenderDist(double distance)
    {
        double lvt_3_1_ = 16.0D;
        lvt_3_1_ = lvt_3_1_ * 64.0D * this.renderDistanceWeight;
        return distance < lvt_3_1_ * lvt_3_1_;
    }

    /**
     * Called when this entity is broken. Entity parameter may be null.
     */
    public void onBroken(Entity brokenEntity)
    {
        this.dropItemOrSelf(brokenEntity, true);
    }

    public void dropItemOrSelf(Entity p_146065_1_, boolean p_146065_2_)
    {
        if (this.worldObj.getGameRules().getBoolean("doEntityDrops"))
        {
            ItemStack lvt_3_1_ = this.getDisplayedItem();

            if (p_146065_1_ instanceof EntityPlayer)
            {
                EntityPlayer lvt_4_1_ = (EntityPlayer)p_146065_1_;

                if (lvt_4_1_.capabilities.isCreativeMode)
                {
                    this.removeFrameFromMap(lvt_3_1_);
                    return;
                }
            }

            if (p_146065_2_)
            {
                this.entityDropItem(new ItemStack(Items.item_frame), 0.0F);
            }

            if (lvt_3_1_ != null && this.rand.nextFloat() < this.itemDropChance)
            {
                lvt_3_1_ = lvt_3_1_.copy();
                this.removeFrameFromMap(lvt_3_1_);
                this.entityDropItem(lvt_3_1_, 0.0F);
            }
        }
    }

    /**
     * Removes the dot representing this frame's position from the map when the item frame is broken.
     */
    private void removeFrameFromMap(ItemStack p_110131_1_)
    {
        if (p_110131_1_ != null)
        {
            if (p_110131_1_.getItem() == Items.filled_map)
            {
                MapData lvt_2_1_ = ((ItemMap)p_110131_1_.getItem()).getMapData(p_110131_1_, this.worldObj);
                lvt_2_1_.mapDecorations.remove("frame-" + this.getEntityId());
            }

            p_110131_1_.setItemFrame((EntityItemFrame)null);
        }
    }

    public ItemStack getDisplayedItem()
    {
        return this.getDataWatcher().getWatchableObjectItemStack(8);
    }

    public void setDisplayedItem(ItemStack p_82334_1_)
    {
        this.setDisplayedItemWithUpdate(p_82334_1_, true);
    }

    private void setDisplayedItemWithUpdate(ItemStack p_174864_1_, boolean p_174864_2_)
    {
        if (p_174864_1_ != null)
        {
            p_174864_1_ = p_174864_1_.copy();
            p_174864_1_.stackSize = 1;
            p_174864_1_.setItemFrame(this);
        }

        this.getDataWatcher().updateObject(8, p_174864_1_);
        this.getDataWatcher().setObjectWatched(8);

        if (p_174864_2_ && this.hangingPosition != null)
        {
            this.worldObj.updateComparatorOutputLevel(this.hangingPosition, Blocks.air);
        }
    }

    /**
     * Return the rotation of the item currently on this frame.
     */
    public int getRotation()
    {
        return this.getDataWatcher().getWatchableObjectByte(9);
    }

    public void setItemRotation(int p_82336_1_)
    {
        this.func_174865_a(p_82336_1_, true);
    }

    private void func_174865_a(int p_174865_1_, boolean p_174865_2_)
    {
        this.getDataWatcher().updateObject(9, Byte.valueOf((byte)(p_174865_1_ % 8)));

        if (p_174865_2_ && this.hangingPosition != null)
        {
            this.worldObj.updateComparatorOutputLevel(this.hangingPosition, Blocks.air);
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        if (this.getDisplayedItem() != null)
        {
            tagCompound.setTag("Item", this.getDisplayedItem().writeToNBT(new NBTTagCompound()));
            tagCompound.setByte("ItemRotation", (byte)this.getRotation());
            tagCompound.setFloat("ItemDropChance", this.itemDropChance);
        }

        super.writeEntityToNBT(tagCompound);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        NBTTagCompound lvt_2_1_ = tagCompund.getCompoundTag("Item");

        if (lvt_2_1_ != null && !lvt_2_1_.hasNoTags())
        {
            this.setDisplayedItemWithUpdate(ItemStack.loadItemStackFromNBT(lvt_2_1_), false);
            this.func_174865_a(tagCompund.getByte("ItemRotation"), false);

            if (tagCompund.hasKey("ItemDropChance", 99))
            {
                this.itemDropChance = tagCompund.getFloat("ItemDropChance");
            }

            if (tagCompund.hasKey("Direction"))
            {
                this.func_174865_a(this.getRotation() * 2, false);
            }
        }

        super.readEntityFromNBT(tagCompund);
    }

    /**
     * First layer of player interaction
     */
    public boolean interactFirst(EntityPlayer playerIn)
    {
        if (this.getDisplayedItem() == null)
        {
            ItemStack lvt_2_1_ = playerIn.getHeldItem();

            if (lvt_2_1_ != null && !this.worldObj.isRemote)
            {
                this.setDisplayedItem(lvt_2_1_);

                if (!playerIn.capabilities.isCreativeMode && --lvt_2_1_.stackSize <= 0)
                {
                    playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, (ItemStack)null);
                }
            }
        }
        else if (!this.worldObj.isRemote)
        {
            this.setItemRotation(this.getRotation() + 1);
        }

        return true;
    }

    public int func_174866_q()
    {
        return this.getDisplayedItem() == null ? 0 : this.getRotation() % 8 + 1;
    }
}
