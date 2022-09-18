package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemRedstone extends Item
{
    public ItemRedstone()
    {
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        boolean lvt_9_1_ = worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos);
        BlockPos lvt_10_1_ = lvt_9_1_ ? pos : pos.offset(side);

        if (!playerIn.canPlayerEdit(lvt_10_1_, side, stack))
        {
            return false;
        }
        else
        {
            Block lvt_11_1_ = worldIn.getBlockState(lvt_10_1_).getBlock();

            if (!worldIn.canBlockBePlaced(lvt_11_1_, lvt_10_1_, false, side, (Entity)null, stack))
            {
                return false;
            }
            else if (Blocks.redstone_wire.canPlaceBlockAt(worldIn, lvt_10_1_))
            {
                --stack.stackSize;
                worldIn.setBlockState(lvt_10_1_, Blocks.redstone_wire.getDefaultState());
                return true;
            }
            else
            {
                return false;
            }
        }
    }
}
