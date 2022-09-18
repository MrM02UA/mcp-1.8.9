package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemSnow extends ItemBlock
{
    public ItemSnow(Block block)
    {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (stack.stackSize == 0)
        {
            return false;
        }
        else if (!playerIn.canPlayerEdit(pos, side, stack))
        {
            return false;
        }
        else
        {
            IBlockState lvt_9_1_ = worldIn.getBlockState(pos);
            Block lvt_10_1_ = lvt_9_1_.getBlock();
            BlockPos lvt_11_1_ = pos;

            if ((side != EnumFacing.UP || lvt_10_1_ != this.block) && !lvt_10_1_.isReplaceable(worldIn, pos))
            {
                lvt_11_1_ = pos.offset(side);
                lvt_9_1_ = worldIn.getBlockState(lvt_11_1_);
                lvt_10_1_ = lvt_9_1_.getBlock();
            }

            if (lvt_10_1_ == this.block)
            {
                int lvt_12_1_ = ((Integer)lvt_9_1_.getValue(BlockSnow.LAYERS)).intValue();

                if (lvt_12_1_ <= 7)
                {
                    IBlockState lvt_13_1_ = lvt_9_1_.withProperty(BlockSnow.LAYERS, Integer.valueOf(lvt_12_1_ + 1));
                    AxisAlignedBB lvt_14_1_ = this.block.getCollisionBoundingBox(worldIn, lvt_11_1_, lvt_13_1_);

                    if (lvt_14_1_ != null && worldIn.checkNoEntityCollision(lvt_14_1_) && worldIn.setBlockState(lvt_11_1_, lvt_13_1_, 2))
                    {
                        worldIn.playSoundEffect((double)((float)lvt_11_1_.getX() + 0.5F), (double)((float)lvt_11_1_.getY() + 0.5F), (double)((float)lvt_11_1_.getZ() + 0.5F), this.block.stepSound.getPlaceSound(), (this.block.stepSound.getVolume() + 1.0F) / 2.0F, this.block.stepSound.getFrequency() * 0.8F);
                        --stack.stackSize;
                        return true;
                    }
                }
            }

            return super.onItemUse(stack, playerIn, worldIn, lvt_11_1_, side, hitX, hitY, hitZ);
        }
    }

    /**
     * Converts the given ItemStack damage value into a metadata value to be placed in the world when this Item is
     * placed as a Block (mostly used with ItemBlocks).
     */
    public int getMetadata(int damage)
    {
        return damage;
    }
}
