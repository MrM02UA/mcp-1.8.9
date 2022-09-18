package net.minecraft.block;

import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockDropper extends BlockDispenser
{
    private final IBehaviorDispenseItem dropBehavior = new BehaviorDefaultDispenseItem();

    protected IBehaviorDispenseItem getBehavior(ItemStack stack)
    {
        return this.dropBehavior;
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityDropper();
    }

    protected void dispense(World worldIn, BlockPos pos)
    {
        BlockSourceImpl lvt_3_1_ = new BlockSourceImpl(worldIn, pos);
        TileEntityDispenser lvt_4_1_ = (TileEntityDispenser)lvt_3_1_.getBlockTileEntity();

        if (lvt_4_1_ != null)
        {
            int lvt_5_1_ = lvt_4_1_.getDispenseSlot();

            if (lvt_5_1_ < 0)
            {
                worldIn.playAuxSFX(1001, pos, 0);
            }
            else
            {
                ItemStack lvt_6_1_ = lvt_4_1_.getStackInSlot(lvt_5_1_);

                if (lvt_6_1_ != null)
                {
                    EnumFacing lvt_7_1_ = (EnumFacing)worldIn.getBlockState(pos).getValue(FACING);
                    BlockPos lvt_8_1_ = pos.offset(lvt_7_1_);
                    IInventory lvt_9_1_ = TileEntityHopper.getInventoryAtPosition(worldIn, (double)lvt_8_1_.getX(), (double)lvt_8_1_.getY(), (double)lvt_8_1_.getZ());
                    ItemStack lvt_10_1_;

                    if (lvt_9_1_ == null)
                    {
                        lvt_10_1_ = this.dropBehavior.dispense(lvt_3_1_, lvt_6_1_);

                        if (lvt_10_1_ != null && lvt_10_1_.stackSize <= 0)
                        {
                            lvt_10_1_ = null;
                        }
                    }
                    else
                    {
                        lvt_10_1_ = TileEntityHopper.putStackInInventoryAllSlots(lvt_9_1_, lvt_6_1_.copy().splitStack(1), lvt_7_1_.getOpposite());

                        if (lvt_10_1_ == null)
                        {
                            lvt_10_1_ = lvt_6_1_.copy();

                            if (--lvt_10_1_.stackSize <= 0)
                            {
                                lvt_10_1_ = null;
                            }
                        }
                        else
                        {
                            lvt_10_1_ = lvt_6_1_.copy();
                        }
                    }

                    lvt_4_1_.setInventorySlotContents(lvt_5_1_, lvt_10_1_);
                }
            }
        }
    }
}
