package net.minecraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemGlassBottle extends Item
{
    public ItemGlassBottle()
    {
        this.setCreativeTab(CreativeTabs.tabBrewing);
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
        MovingObjectPosition lvt_4_1_ = this.getMovingObjectPositionFromPlayer(worldIn, playerIn, true);

        if (lvt_4_1_ == null)
        {
            return itemStackIn;
        }
        else
        {
            if (lvt_4_1_.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            {
                BlockPos lvt_5_1_ = lvt_4_1_.getBlockPos();

                if (!worldIn.isBlockModifiable(playerIn, lvt_5_1_))
                {
                    return itemStackIn;
                }

                if (!playerIn.canPlayerEdit(lvt_5_1_.offset(lvt_4_1_.sideHit), lvt_4_1_.sideHit, itemStackIn))
                {
                    return itemStackIn;
                }

                if (worldIn.getBlockState(lvt_5_1_).getBlock().getMaterial() == Material.water)
                {
                    --itemStackIn.stackSize;
                    playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);

                    if (itemStackIn.stackSize <= 0)
                    {
                        return new ItemStack(Items.potionitem);
                    }

                    if (!playerIn.inventory.addItemStackToInventory(new ItemStack(Items.potionitem)))
                    {
                        playerIn.dropPlayerItemWithRandomChoice(new ItemStack(Items.potionitem, 1, 0), false);
                    }
                }
            }

            return itemStackIn;
        }
    }
}
