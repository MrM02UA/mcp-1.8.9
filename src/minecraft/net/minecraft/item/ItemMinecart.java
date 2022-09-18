package net.minecraft.item;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemMinecart extends Item
{
    private static final IBehaviorDispenseItem dispenserMinecartBehavior = new BehaviorDefaultDispenseItem()
    {
        private final BehaviorDefaultDispenseItem behaviourDefaultDispenseItem = new BehaviorDefaultDispenseItem();
        public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
        {
            EnumFacing lvt_3_1_ = BlockDispenser.getFacing(source.getBlockMetadata());
            World lvt_4_1_ = source.getWorld();
            double lvt_5_1_ = source.getX() + (double)lvt_3_1_.getFrontOffsetX() * 1.125D;
            double lvt_7_1_ = Math.floor(source.getY()) + (double)lvt_3_1_.getFrontOffsetY();
            double lvt_9_1_ = source.getZ() + (double)lvt_3_1_.getFrontOffsetZ() * 1.125D;
            BlockPos lvt_11_1_ = source.getBlockPos().offset(lvt_3_1_);
            IBlockState lvt_12_1_ = lvt_4_1_.getBlockState(lvt_11_1_);
            BlockRailBase.EnumRailDirection lvt_13_1_ = lvt_12_1_.getBlock() instanceof BlockRailBase ? (BlockRailBase.EnumRailDirection)lvt_12_1_.getValue(((BlockRailBase)lvt_12_1_.getBlock()).getShapeProperty()) : BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            double lvt_14_1_;

            if (BlockRailBase.isRailBlock(lvt_12_1_))
            {
                if (lvt_13_1_.isAscending())
                {
                    lvt_14_1_ = 0.6D;
                }
                else
                {
                    lvt_14_1_ = 0.1D;
                }
            }
            else
            {
                if (lvt_12_1_.getBlock().getMaterial() != Material.air || !BlockRailBase.isRailBlock(lvt_4_1_.getBlockState(lvt_11_1_.down())))
                {
                    return this.behaviourDefaultDispenseItem.dispense(source, stack);
                }

                IBlockState lvt_16_1_ = lvt_4_1_.getBlockState(lvt_11_1_.down());
                BlockRailBase.EnumRailDirection lvt_17_1_ = lvt_16_1_.getBlock() instanceof BlockRailBase ? (BlockRailBase.EnumRailDirection)lvt_16_1_.getValue(((BlockRailBase)lvt_16_1_.getBlock()).getShapeProperty()) : BlockRailBase.EnumRailDirection.NORTH_SOUTH;

                if (lvt_3_1_ != EnumFacing.DOWN && lvt_17_1_.isAscending())
                {
                    lvt_14_1_ = -0.4D;
                }
                else
                {
                    lvt_14_1_ = -0.9D;
                }
            }

            EntityMinecart lvt_16_2_ = EntityMinecart.getMinecart(lvt_4_1_, lvt_5_1_, lvt_7_1_ + lvt_14_1_, lvt_9_1_, ((ItemMinecart)stack.getItem()).minecartType);

            if (stack.hasDisplayName())
            {
                lvt_16_2_.setCustomNameTag(stack.getDisplayName());
            }

            lvt_4_1_.spawnEntityInWorld(lvt_16_2_);
            stack.splitStack(1);
            return stack;
        }
        protected void playDispenseSound(IBlockSource source)
        {
            source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
        }
    };
    private final EntityMinecart.EnumMinecartType minecartType;

    public ItemMinecart(EntityMinecart.EnumMinecartType type)
    {
        this.maxStackSize = 1;
        this.minecartType = type;
        this.setCreativeTab(CreativeTabs.tabTransport);
        BlockDispenser.dispenseBehaviorRegistry.putObject(this, dispenserMinecartBehavior);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        IBlockState lvt_9_1_ = worldIn.getBlockState(pos);

        if (BlockRailBase.isRailBlock(lvt_9_1_))
        {
            if (!worldIn.isRemote)
            {
                BlockRailBase.EnumRailDirection lvt_10_1_ = lvt_9_1_.getBlock() instanceof BlockRailBase ? (BlockRailBase.EnumRailDirection)lvt_9_1_.getValue(((BlockRailBase)lvt_9_1_.getBlock()).getShapeProperty()) : BlockRailBase.EnumRailDirection.NORTH_SOUTH;
                double lvt_11_1_ = 0.0D;

                if (lvt_10_1_.isAscending())
                {
                    lvt_11_1_ = 0.5D;
                }

                EntityMinecart lvt_13_1_ = EntityMinecart.getMinecart(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.0625D + lvt_11_1_, (double)pos.getZ() + 0.5D, this.minecartType);

                if (stack.hasDisplayName())
                {
                    lvt_13_1_.setCustomNameTag(stack.getDisplayName());
                }

                worldIn.spawnEntityInWorld(lvt_13_1_);
            }

            --stack.stackSize;
            return true;
        }
        else
        {
            return false;
        }
    }
}
