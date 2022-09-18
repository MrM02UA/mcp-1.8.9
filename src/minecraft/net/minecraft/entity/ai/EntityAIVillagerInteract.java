package net.minecraft.entity.ai;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class EntityAIVillagerInteract extends EntityAIWatchClosest2
{
    /** The delay before the villager throws an itemstack (in ticks) */
    private int interactionDelay;
    private EntityVillager villager;

    public EntityAIVillagerInteract(EntityVillager villagerIn)
    {
        super(villagerIn, EntityVillager.class, 3.0F, 0.02F);
        this.villager = villagerIn;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        super.startExecuting();

        if (this.villager.canAbondonItems() && this.closestEntity instanceof EntityVillager && ((EntityVillager)this.closestEntity).func_175557_cr())
        {
            this.interactionDelay = 10;
        }
        else
        {
            this.interactionDelay = 0;
        }
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        super.updateTask();

        if (this.interactionDelay > 0)
        {
            --this.interactionDelay;

            if (this.interactionDelay == 0)
            {
                InventoryBasic lvt_1_1_ = this.villager.getVillagerInventory();

                for (int lvt_2_1_ = 0; lvt_2_1_ < lvt_1_1_.getSizeInventory(); ++lvt_2_1_)
                {
                    ItemStack lvt_3_1_ = lvt_1_1_.getStackInSlot(lvt_2_1_);
                    ItemStack lvt_4_1_ = null;

                    if (lvt_3_1_ != null)
                    {
                        Item lvt_5_1_ = lvt_3_1_.getItem();

                        if ((lvt_5_1_ == Items.bread || lvt_5_1_ == Items.potato || lvt_5_1_ == Items.carrot) && lvt_3_1_.stackSize > 3)
                        {
                            int lvt_6_1_ = lvt_3_1_.stackSize / 2;
                            lvt_3_1_.stackSize -= lvt_6_1_;
                            lvt_4_1_ = new ItemStack(lvt_5_1_, lvt_6_1_, lvt_3_1_.getMetadata());
                        }
                        else if (lvt_5_1_ == Items.wheat && lvt_3_1_.stackSize > 5)
                        {
                            int lvt_6_2_ = lvt_3_1_.stackSize / 2 / 3 * 3;
                            int lvt_7_1_ = lvt_6_2_ / 3;
                            lvt_3_1_.stackSize -= lvt_6_2_;
                            lvt_4_1_ = new ItemStack(Items.bread, lvt_7_1_, 0);
                        }

                        if (lvt_3_1_.stackSize <= 0)
                        {
                            lvt_1_1_.setInventorySlotContents(lvt_2_1_, (ItemStack)null);
                        }
                    }

                    if (lvt_4_1_ != null)
                    {
                        double lvt_5_2_ = this.villager.posY - 0.30000001192092896D + (double)this.villager.getEyeHeight();
                        EntityItem lvt_7_2_ = new EntityItem(this.villager.worldObj, this.villager.posX, lvt_5_2_, this.villager.posZ, lvt_4_1_);
                        float lvt_8_1_ = 0.3F;
                        float lvt_9_1_ = this.villager.rotationYawHead;
                        float lvt_10_1_ = this.villager.rotationPitch;
                        lvt_7_2_.motionX = (double)(-MathHelper.sin(lvt_9_1_ / 180.0F * (float)Math.PI) * MathHelper.cos(lvt_10_1_ / 180.0F * (float)Math.PI) * lvt_8_1_);
                        lvt_7_2_.motionZ = (double)(MathHelper.cos(lvt_9_1_ / 180.0F * (float)Math.PI) * MathHelper.cos(lvt_10_1_ / 180.0F * (float)Math.PI) * lvt_8_1_);
                        lvt_7_2_.motionY = (double)(-MathHelper.sin(lvt_10_1_ / 180.0F * (float)Math.PI) * lvt_8_1_ + 0.1F);
                        lvt_7_2_.setDefaultPickupDelay();
                        this.villager.worldObj.spawnEntityInWorld(lvt_7_2_);
                        break;
                    }
                }
            }
        }
    }
}
