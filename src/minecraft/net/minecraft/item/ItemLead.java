package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemLead extends Item
{
    public ItemLead()
    {
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        Block lvt_9_1_ = worldIn.getBlockState(pos).getBlock();

        if (lvt_9_1_ instanceof BlockFence)
        {
            if (worldIn.isRemote)
            {
                return true;
            }
            else
            {
                attachToFence(playerIn, worldIn, pos);
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    public static boolean attachToFence(EntityPlayer player, World worldIn, BlockPos fence)
    {
        EntityLeashKnot lvt_3_1_ = EntityLeashKnot.getKnotForPosition(worldIn, fence);
        boolean lvt_4_1_ = false;
        double lvt_5_1_ = 7.0D;
        int lvt_7_1_ = fence.getX();
        int lvt_8_1_ = fence.getY();
        int lvt_9_1_ = fence.getZ();

        for (EntityLiving lvt_12_1_ : worldIn.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB((double)lvt_7_1_ - lvt_5_1_, (double)lvt_8_1_ - lvt_5_1_, (double)lvt_9_1_ - lvt_5_1_, (double)lvt_7_1_ + lvt_5_1_, (double)lvt_8_1_ + lvt_5_1_, (double)lvt_9_1_ + lvt_5_1_)))
        {
            if (lvt_12_1_.getLeashed() && lvt_12_1_.getLeashedToEntity() == player)
            {
                if (lvt_3_1_ == null)
                {
                    lvt_3_1_ = EntityLeashKnot.createKnot(worldIn, fence);
                }

                lvt_12_1_.setLeashedToEntity(lvt_3_1_, true);
                lvt_4_1_ = true;
            }
        }

        return lvt_4_1_;
    }
}
