package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemBucket extends Item
{
    /** field for checking if the bucket has been filled. */
    private Block isFull;

    public ItemBucket(Block containedBlock)
    {
        this.maxStackSize = 1;
        this.isFull = containedBlock;
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
        boolean lvt_4_1_ = this.isFull == Blocks.air;
        MovingObjectPosition lvt_5_1_ = this.getMovingObjectPositionFromPlayer(worldIn, playerIn, lvt_4_1_);

        if (lvt_5_1_ == null)
        {
            return itemStackIn;
        }
        else
        {
            if (lvt_5_1_.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            {
                BlockPos lvt_6_1_ = lvt_5_1_.getBlockPos();

                if (!worldIn.isBlockModifiable(playerIn, lvt_6_1_))
                {
                    return itemStackIn;
                }

                if (lvt_4_1_)
                {
                    if (!playerIn.canPlayerEdit(lvt_6_1_.offset(lvt_5_1_.sideHit), lvt_5_1_.sideHit, itemStackIn))
                    {
                        return itemStackIn;
                    }

                    IBlockState lvt_7_1_ = worldIn.getBlockState(lvt_6_1_);
                    Material lvt_8_1_ = lvt_7_1_.getBlock().getMaterial();

                    if (lvt_8_1_ == Material.water && ((Integer)lvt_7_1_.getValue(BlockLiquid.LEVEL)).intValue() == 0)
                    {
                        worldIn.setBlockToAir(lvt_6_1_);
                        playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
                        return this.fillBucket(itemStackIn, playerIn, Items.water_bucket);
                    }

                    if (lvt_8_1_ == Material.lava && ((Integer)lvt_7_1_.getValue(BlockLiquid.LEVEL)).intValue() == 0)
                    {
                        worldIn.setBlockToAir(lvt_6_1_);
                        playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
                        return this.fillBucket(itemStackIn, playerIn, Items.lava_bucket);
                    }
                }
                else
                {
                    if (this.isFull == Blocks.air)
                    {
                        return new ItemStack(Items.bucket);
                    }

                    BlockPos lvt_7_2_ = lvt_6_1_.offset(lvt_5_1_.sideHit);

                    if (!playerIn.canPlayerEdit(lvt_7_2_, lvt_5_1_.sideHit, itemStackIn))
                    {
                        return itemStackIn;
                    }

                    if (this.tryPlaceContainedLiquid(worldIn, lvt_7_2_) && !playerIn.capabilities.isCreativeMode)
                    {
                        playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
                        return new ItemStack(Items.bucket);
                    }
                }
            }

            return itemStackIn;
        }
    }

    private ItemStack fillBucket(ItemStack emptyBuckets, EntityPlayer player, Item fullBucket)
    {
        if (player.capabilities.isCreativeMode)
        {
            return emptyBuckets;
        }
        else if (--emptyBuckets.stackSize <= 0)
        {
            return new ItemStack(fullBucket);
        }
        else
        {
            if (!player.inventory.addItemStackToInventory(new ItemStack(fullBucket)))
            {
                player.dropPlayerItemWithRandomChoice(new ItemStack(fullBucket, 1, 0), false);
            }

            return emptyBuckets;
        }
    }

    public boolean tryPlaceContainedLiquid(World worldIn, BlockPos pos)
    {
        if (this.isFull == Blocks.air)
        {
            return false;
        }
        else
        {
            Material lvt_3_1_ = worldIn.getBlockState(pos).getBlock().getMaterial();
            boolean lvt_4_1_ = !lvt_3_1_.isSolid();

            if (!worldIn.isAirBlock(pos) && !lvt_4_1_)
            {
                return false;
            }
            else
            {
                if (worldIn.provider.doesWaterVaporize() && this.isFull == Blocks.flowing_water)
                {
                    int lvt_5_1_ = pos.getX();
                    int lvt_6_1_ = pos.getY();
                    int lvt_7_1_ = pos.getZ();
                    worldIn.playSoundEffect((double)((float)lvt_5_1_ + 0.5F), (double)((float)lvt_6_1_ + 0.5F), (double)((float)lvt_7_1_ + 0.5F), "random.fizz", 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

                    for (int lvt_8_1_ = 0; lvt_8_1_ < 8; ++lvt_8_1_)
                    {
                        worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, (double)lvt_5_1_ + Math.random(), (double)lvt_6_1_ + Math.random(), (double)lvt_7_1_ + Math.random(), 0.0D, 0.0D, 0.0D, new int[0]);
                    }
                }
                else
                {
                    if (!worldIn.isRemote && lvt_4_1_ && !lvt_3_1_.isLiquid())
                    {
                        worldIn.destroyBlock(pos, true);
                    }

                    worldIn.setBlockState(pos, this.isFull.getDefaultState(), 3);
                }

                return true;
            }
        }
    }
}
