package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemLilyPad extends ItemColored
{
    public ItemLilyPad(Block block)
    {
        super(block, false);
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

                BlockPos lvt_6_1_ = lvt_5_1_.up();
                IBlockState lvt_7_1_ = worldIn.getBlockState(lvt_5_1_);

                if (lvt_7_1_.getBlock().getMaterial() == Material.water && ((Integer)lvt_7_1_.getValue(BlockLiquid.LEVEL)).intValue() == 0 && worldIn.isAirBlock(lvt_6_1_))
                {
                    worldIn.setBlockState(lvt_6_1_, Blocks.waterlily.getDefaultState());

                    if (!playerIn.capabilities.isCreativeMode)
                    {
                        --itemStackIn.stackSize;
                    }

                    playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
                }
            }

            return itemStackIn;
        }
    }

    public int getColorFromItemStack(ItemStack stack, int renderPass)
    {
        return Blocks.waterlily.getRenderColor(Blocks.waterlily.getStateFromMeta(stack.getMetadata()));
    }
}
