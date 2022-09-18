package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemBed extends Item
{
    public ItemBed()
    {
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        else if (side != EnumFacing.UP)
        {
            return false;
        }
        else
        {
            IBlockState lvt_9_1_ = worldIn.getBlockState(pos);
            Block lvt_10_1_ = lvt_9_1_.getBlock();
            boolean lvt_11_1_ = lvt_10_1_.isReplaceable(worldIn, pos);

            if (!lvt_11_1_)
            {
                pos = pos.up();
            }

            int lvt_12_1_ = MathHelper.floor_double((double)(playerIn.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            EnumFacing lvt_13_1_ = EnumFacing.getHorizontal(lvt_12_1_);
            BlockPos lvt_14_1_ = pos.offset(lvt_13_1_);

            if (playerIn.canPlayerEdit(pos, side, stack) && playerIn.canPlayerEdit(lvt_14_1_, side, stack))
            {
                boolean lvt_15_1_ = worldIn.getBlockState(lvt_14_1_).getBlock().isReplaceable(worldIn, lvt_14_1_);
                boolean lvt_16_1_ = lvt_11_1_ || worldIn.isAirBlock(pos);
                boolean lvt_17_1_ = lvt_15_1_ || worldIn.isAirBlock(lvt_14_1_);

                if (lvt_16_1_ && lvt_17_1_ && World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && World.doesBlockHaveSolidTopSurface(worldIn, lvt_14_1_.down()))
                {
                    IBlockState lvt_18_1_ = Blocks.bed.getDefaultState().withProperty(BlockBed.OCCUPIED, Boolean.valueOf(false)).withProperty(BlockBed.FACING, lvt_13_1_).withProperty(BlockBed.PART, BlockBed.EnumPartType.FOOT);

                    if (worldIn.setBlockState(pos, lvt_18_1_, 3))
                    {
                        IBlockState lvt_19_1_ = lvt_18_1_.withProperty(BlockBed.PART, BlockBed.EnumPartType.HEAD);
                        worldIn.setBlockState(lvt_14_1_, lvt_19_1_, 3);
                    }

                    --stack.stackSize;
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
    }
}
