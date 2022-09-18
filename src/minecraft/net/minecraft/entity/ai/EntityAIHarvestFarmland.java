package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityAIHarvestFarmland extends EntityAIMoveToBlock
{
    /** Villager that is harvesting */
    private final EntityVillager theVillager;
    private boolean hasFarmItem;
    private boolean field_179503_e;
    private int field_179501_f;

    public EntityAIHarvestFarmland(EntityVillager theVillagerIn, double speedIn)
    {
        super(theVillagerIn, speedIn, 16);
        this.theVillager = theVillagerIn;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.runDelay <= 0)
        {
            if (!this.theVillager.worldObj.getGameRules().getBoolean("mobGriefing"))
            {
                return false;
            }

            this.field_179501_f = -1;
            this.hasFarmItem = this.theVillager.isFarmItemInInventory();
            this.field_179503_e = this.theVillager.func_175557_cr();
        }

        return super.shouldExecute();
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.field_179501_f >= 0 && super.continueExecuting();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        super.startExecuting();
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        super.resetTask();
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        super.updateTask();
        this.theVillager.getLookHelper().setLookPosition((double)this.destinationBlock.getX() + 0.5D, (double)(this.destinationBlock.getY() + 1), (double)this.destinationBlock.getZ() + 0.5D, 10.0F, (float)this.theVillager.getVerticalFaceSpeed());

        if (this.getIsAboveDestination())
        {
            World lvt_1_1_ = this.theVillager.worldObj;
            BlockPos lvt_2_1_ = this.destinationBlock.up();
            IBlockState lvt_3_1_ = lvt_1_1_.getBlockState(lvt_2_1_);
            Block lvt_4_1_ = lvt_3_1_.getBlock();

            if (this.field_179501_f == 0 && lvt_4_1_ instanceof BlockCrops && ((Integer)lvt_3_1_.getValue(BlockCrops.AGE)).intValue() == 7)
            {
                lvt_1_1_.destroyBlock(lvt_2_1_, true);
            }
            else if (this.field_179501_f == 1 && lvt_4_1_ == Blocks.air)
            {
                InventoryBasic lvt_5_1_ = this.theVillager.getVillagerInventory();

                for (int lvt_6_1_ = 0; lvt_6_1_ < lvt_5_1_.getSizeInventory(); ++lvt_6_1_)
                {
                    ItemStack lvt_7_1_ = lvt_5_1_.getStackInSlot(lvt_6_1_);
                    boolean lvt_8_1_ = false;

                    if (lvt_7_1_ != null)
                    {
                        if (lvt_7_1_.getItem() == Items.wheat_seeds)
                        {
                            lvt_1_1_.setBlockState(lvt_2_1_, Blocks.wheat.getDefaultState(), 3);
                            lvt_8_1_ = true;
                        }
                        else if (lvt_7_1_.getItem() == Items.potato)
                        {
                            lvt_1_1_.setBlockState(lvt_2_1_, Blocks.potatoes.getDefaultState(), 3);
                            lvt_8_1_ = true;
                        }
                        else if (lvt_7_1_.getItem() == Items.carrot)
                        {
                            lvt_1_1_.setBlockState(lvt_2_1_, Blocks.carrots.getDefaultState(), 3);
                            lvt_8_1_ = true;
                        }
                    }

                    if (lvt_8_1_)
                    {
                        --lvt_7_1_.stackSize;

                        if (lvt_7_1_.stackSize <= 0)
                        {
                            lvt_5_1_.setInventorySlotContents(lvt_6_1_, (ItemStack)null);
                        }

                        break;
                    }
                }
            }

            this.field_179501_f = -1;
            this.runDelay = 10;
        }
    }

    /**
     * Return true to set given position as destination
     */
    protected boolean shouldMoveTo(World worldIn, BlockPos pos)
    {
        Block lvt_3_1_ = worldIn.getBlockState(pos).getBlock();

        if (lvt_3_1_ == Blocks.farmland)
        {
            pos = pos.up();
            IBlockState lvt_4_1_ = worldIn.getBlockState(pos);
            lvt_3_1_ = lvt_4_1_.getBlock();

            if (lvt_3_1_ instanceof BlockCrops && ((Integer)lvt_4_1_.getValue(BlockCrops.AGE)).intValue() == 7 && this.field_179503_e && (this.field_179501_f == 0 || this.field_179501_f < 0))
            {
                this.field_179501_f = 0;
                return true;
            }

            if (lvt_3_1_ == Blocks.air && this.hasFarmItem && (this.field_179501_f == 1 || this.field_179501_f < 0))
            {
                this.field_179501_f = 1;
                return true;
            }
        }

        return false;
    }
}
