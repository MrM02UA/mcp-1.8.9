package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemDoor extends Item
{
    private Block block;

    public ItemDoor(Block block)
    {
        this.block = block;
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (side != EnumFacing.UP)
        {
            return false;
        }
        else
        {
            IBlockState lvt_9_1_ = worldIn.getBlockState(pos);
            Block lvt_10_1_ = lvt_9_1_.getBlock();

            if (!lvt_10_1_.isReplaceable(worldIn, pos))
            {
                pos = pos.offset(side);
            }

            if (!playerIn.canPlayerEdit(pos, side, stack))
            {
                return false;
            }
            else if (!this.block.canPlaceBlockAt(worldIn, pos))
            {
                return false;
            }
            else
            {
                placeDoor(worldIn, pos, EnumFacing.fromAngle((double)playerIn.rotationYaw), this.block);
                --stack.stackSize;
                return true;
            }
        }
    }

    public static void placeDoor(World worldIn, BlockPos pos, EnumFacing facing, Block door)
    {
        BlockPos lvt_4_1_ = pos.offset(facing.rotateY());
        BlockPos lvt_5_1_ = pos.offset(facing.rotateYCCW());
        int lvt_6_1_ = (worldIn.getBlockState(lvt_5_1_).getBlock().isNormalCube() ? 1 : 0) + (worldIn.getBlockState(lvt_5_1_.up()).getBlock().isNormalCube() ? 1 : 0);
        int lvt_7_1_ = (worldIn.getBlockState(lvt_4_1_).getBlock().isNormalCube() ? 1 : 0) + (worldIn.getBlockState(lvt_4_1_.up()).getBlock().isNormalCube() ? 1 : 0);
        boolean lvt_8_1_ = worldIn.getBlockState(lvt_5_1_).getBlock() == door || worldIn.getBlockState(lvt_5_1_.up()).getBlock() == door;
        boolean lvt_9_1_ = worldIn.getBlockState(lvt_4_1_).getBlock() == door || worldIn.getBlockState(lvt_4_1_.up()).getBlock() == door;
        boolean lvt_10_1_ = false;

        if (lvt_8_1_ && !lvt_9_1_ || lvt_7_1_ > lvt_6_1_)
        {
            lvt_10_1_ = true;
        }

        BlockPos lvt_11_1_ = pos.up();
        IBlockState lvt_12_1_ = door.getDefaultState().withProperty(BlockDoor.FACING, facing).withProperty(BlockDoor.HINGE, lvt_10_1_ ? BlockDoor.EnumHingePosition.RIGHT : BlockDoor.EnumHingePosition.LEFT);
        worldIn.setBlockState(pos, lvt_12_1_.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER), 2);
        worldIn.setBlockState(lvt_11_1_, lvt_12_1_.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER), 2);
        worldIn.notifyNeighborsOfStateChange(pos, door);
        worldIn.notifyNeighborsOfStateChange(lvt_11_1_, door);
    }
}
