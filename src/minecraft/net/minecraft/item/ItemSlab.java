package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemSlab extends ItemBlock
{
    private final BlockSlab singleSlab;
    private final BlockSlab doubleSlab;

    public ItemSlab(Block block, BlockSlab singleSlab, BlockSlab doubleSlab)
    {
        super(block);
        this.singleSlab = singleSlab;
        this.doubleSlab = doubleSlab;
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    /**
     * Converts the given ItemStack damage value into a metadata value to be placed in the world when this Item is
     * placed as a Block (mostly used with ItemBlocks).
     */
    public int getMetadata(int damage)
    {
        return damage;
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(ItemStack stack)
    {
        return this.singleSlab.getUnlocalizedName(stack.getMetadata());
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
        else if (!playerIn.canPlayerEdit(pos.offset(side), side, stack))
        {
            return false;
        }
        else
        {
            Object lvt_9_1_ = this.singleSlab.getVariant(stack);
            IBlockState lvt_10_1_ = worldIn.getBlockState(pos);

            if (lvt_10_1_.getBlock() == this.singleSlab)
            {
                IProperty lvt_11_1_ = this.singleSlab.getVariantProperty();
                Comparable lvt_12_1_ = lvt_10_1_.getValue(lvt_11_1_);
                BlockSlab.EnumBlockHalf lvt_13_1_ = (BlockSlab.EnumBlockHalf)lvt_10_1_.getValue(BlockSlab.HALF);

                if ((side == EnumFacing.UP && lvt_13_1_ == BlockSlab.EnumBlockHalf.BOTTOM || side == EnumFacing.DOWN && lvt_13_1_ == BlockSlab.EnumBlockHalf.TOP) && lvt_12_1_ == lvt_9_1_)
                {
                    IBlockState lvt_14_1_ = this.doubleSlab.getDefaultState().withProperty(lvt_11_1_, lvt_12_1_);

                    if (worldIn.checkNoEntityCollision(this.doubleSlab.getCollisionBoundingBox(worldIn, pos, lvt_14_1_)) && worldIn.setBlockState(pos, lvt_14_1_, 3))
                    {
                        worldIn.playSoundEffect((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F), this.doubleSlab.stepSound.getPlaceSound(), (this.doubleSlab.stepSound.getVolume() + 1.0F) / 2.0F, this.doubleSlab.stepSound.getFrequency() * 0.8F);
                        --stack.stackSize;
                    }

                    return true;
                }
            }

            return this.tryPlace(stack, worldIn, pos.offset(side), lvt_9_1_) ? true : super.onItemUse(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ);
        }
    }

    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack)
    {
        BlockPos lvt_6_1_ = pos;
        IProperty lvt_7_1_ = this.singleSlab.getVariantProperty();
        Object lvt_8_1_ = this.singleSlab.getVariant(stack);
        IBlockState lvt_9_1_ = worldIn.getBlockState(pos);

        if (lvt_9_1_.getBlock() == this.singleSlab)
        {
            boolean lvt_10_1_ = lvt_9_1_.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP;

            if ((side == EnumFacing.UP && !lvt_10_1_ || side == EnumFacing.DOWN && lvt_10_1_) && lvt_8_1_ == lvt_9_1_.getValue(lvt_7_1_))
            {
                return true;
            }
        }

        pos = pos.offset(side);
        IBlockState lvt_10_2_ = worldIn.getBlockState(pos);
        return lvt_10_2_.getBlock() == this.singleSlab && lvt_8_1_ == lvt_10_2_.getValue(lvt_7_1_) ? true : super.canPlaceBlockOnSide(worldIn, lvt_6_1_, side, player, stack);
    }

    private boolean tryPlace(ItemStack stack, World worldIn, BlockPos pos, Object variantInStack)
    {
        IBlockState lvt_5_1_ = worldIn.getBlockState(pos);

        if (lvt_5_1_.getBlock() == this.singleSlab)
        {
            Comparable lvt_6_1_ = lvt_5_1_.getValue(this.singleSlab.getVariantProperty());

            if (lvt_6_1_ == variantInStack)
            {
                IBlockState lvt_7_1_ = this.doubleSlab.getDefaultState().withProperty(this.singleSlab.getVariantProperty(), lvt_6_1_);

                if (worldIn.checkNoEntityCollision(this.doubleSlab.getCollisionBoundingBox(worldIn, pos, lvt_7_1_)) && worldIn.setBlockState(pos, lvt_7_1_, 3))
                {
                    worldIn.playSoundEffect((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F), this.doubleSlab.stepSound.getPlaceSound(), (this.doubleSlab.stepSound.getVolume() + 1.0F) / 2.0F, this.doubleSlab.stepSound.getFrequency() * 0.8F);
                    --stack.stackSize;
                }

                return true;
            }
        }

        return false;
    }
}
