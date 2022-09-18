package net.minecraft.item;

import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemEnderEye extends Item
{
    public ItemEnderEye()
    {
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        IBlockState lvt_9_1_ = worldIn.getBlockState(pos);

        if (playerIn.canPlayerEdit(pos.offset(side), side, stack) && lvt_9_1_.getBlock() == Blocks.end_portal_frame && !((Boolean)lvt_9_1_.getValue(BlockEndPortalFrame.EYE)).booleanValue())
        {
            if (worldIn.isRemote)
            {
                return true;
            }
            else
            {
                worldIn.setBlockState(pos, lvt_9_1_.withProperty(BlockEndPortalFrame.EYE, Boolean.valueOf(true)), 2);
                worldIn.updateComparatorOutputLevel(pos, Blocks.end_portal_frame);
                --stack.stackSize;

                for (int lvt_10_1_ = 0; lvt_10_1_ < 16; ++lvt_10_1_)
                {
                    double lvt_11_1_ = (double)((float)pos.getX() + (5.0F + itemRand.nextFloat() * 6.0F) / 16.0F);
                    double lvt_13_1_ = (double)((float)pos.getY() + 0.8125F);
                    double lvt_15_1_ = (double)((float)pos.getZ() + (5.0F + itemRand.nextFloat() * 6.0F) / 16.0F);
                    double lvt_17_1_ = 0.0D;
                    double lvt_19_1_ = 0.0D;
                    double lvt_21_1_ = 0.0D;
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, lvt_11_1_, lvt_13_1_, lvt_15_1_, lvt_17_1_, lvt_19_1_, lvt_21_1_, new int[0]);
                }

                EnumFacing lvt_10_2_ = (EnumFacing)lvt_9_1_.getValue(BlockEndPortalFrame.FACING);
                int lvt_11_2_ = 0;
                int lvt_12_1_ = 0;
                boolean lvt_13_2_ = false;
                boolean lvt_14_1_ = true;
                EnumFacing lvt_15_2_ = lvt_10_2_.rotateY();

                for (int lvt_16_1_ = -2; lvt_16_1_ <= 2; ++lvt_16_1_)
                {
                    BlockPos lvt_17_2_ = pos.offset(lvt_15_2_, lvt_16_1_);
                    IBlockState lvt_18_1_ = worldIn.getBlockState(lvt_17_2_);

                    if (lvt_18_1_.getBlock() == Blocks.end_portal_frame)
                    {
                        if (!((Boolean)lvt_18_1_.getValue(BlockEndPortalFrame.EYE)).booleanValue())
                        {
                            lvt_14_1_ = false;
                            break;
                        }

                        lvt_12_1_ = lvt_16_1_;

                        if (!lvt_13_2_)
                        {
                            lvt_11_2_ = lvt_16_1_;
                            lvt_13_2_ = true;
                        }
                    }
                }

                if (lvt_14_1_ && lvt_12_1_ == lvt_11_2_ + 2)
                {
                    BlockPos lvt_16_2_ = pos.offset(lvt_10_2_, 4);

                    for (int lvt_17_3_ = lvt_11_2_; lvt_17_3_ <= lvt_12_1_; ++lvt_17_3_)
                    {
                        BlockPos lvt_18_2_ = lvt_16_2_.offset(lvt_15_2_, lvt_17_3_);
                        IBlockState lvt_19_2_ = worldIn.getBlockState(lvt_18_2_);

                        if (lvt_19_2_.getBlock() != Blocks.end_portal_frame || !((Boolean)lvt_19_2_.getValue(BlockEndPortalFrame.EYE)).booleanValue())
                        {
                            lvt_14_1_ = false;
                            break;
                        }
                    }

                    for (int lvt_17_4_ = lvt_11_2_ - 1; lvt_17_4_ <= lvt_12_1_ + 1; lvt_17_4_ += 4)
                    {
                        lvt_16_2_ = pos.offset(lvt_15_2_, lvt_17_4_);

                        for (int lvt_18_3_ = 1; lvt_18_3_ <= 3; ++lvt_18_3_)
                        {
                            BlockPos lvt_19_3_ = lvt_16_2_.offset(lvt_10_2_, lvt_18_3_);
                            IBlockState lvt_20_1_ = worldIn.getBlockState(lvt_19_3_);

                            if (lvt_20_1_.getBlock() != Blocks.end_portal_frame || !((Boolean)lvt_20_1_.getValue(BlockEndPortalFrame.EYE)).booleanValue())
                            {
                                lvt_14_1_ = false;
                                break;
                            }
                        }
                    }

                    if (lvt_14_1_)
                    {
                        for (int lvt_17_5_ = lvt_11_2_; lvt_17_5_ <= lvt_12_1_; ++lvt_17_5_)
                        {
                            lvt_16_2_ = pos.offset(lvt_15_2_, lvt_17_5_);

                            for (int lvt_18_4_ = 1; lvt_18_4_ <= 3; ++lvt_18_4_)
                            {
                                BlockPos lvt_19_4_ = lvt_16_2_.offset(lvt_10_2_, lvt_18_4_);
                                worldIn.setBlockState(lvt_19_4_, Blocks.end_portal.getDefaultState(), 2);
                            }
                        }
                    }
                }

                return true;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
        MovingObjectPosition lvt_4_1_ = this.getMovingObjectPositionFromPlayer(worldIn, playerIn, false);

        if (lvt_4_1_ != null && lvt_4_1_.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && worldIn.getBlockState(lvt_4_1_.getBlockPos()).getBlock() == Blocks.end_portal_frame)
        {
            return itemStackIn;
        }
        else
        {
            if (!worldIn.isRemote)
            {
                BlockPos lvt_5_1_ = worldIn.getStrongholdPos("Stronghold", new BlockPos(playerIn));

                if (lvt_5_1_ != null)
                {
                    EntityEnderEye lvt_6_1_ = new EntityEnderEye(worldIn, playerIn.posX, playerIn.posY, playerIn.posZ);
                    lvt_6_1_.moveTowards(lvt_5_1_);
                    worldIn.spawnEntityInWorld(lvt_6_1_);
                    worldIn.playSoundAtEntity(playerIn, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
                    worldIn.playAuxSFXAtEntity((EntityPlayer)null, 1002, new BlockPos(playerIn), 0);

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
}
