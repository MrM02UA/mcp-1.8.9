package net.minecraft.item;

import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemBoat extends Item
{
    public ItemBoat()
    {
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabTransport);
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
        float lvt_4_1_ = 1.0F;
        float lvt_5_1_ = playerIn.prevRotationPitch + (playerIn.rotationPitch - playerIn.prevRotationPitch) * lvt_4_1_;
        float lvt_6_1_ = playerIn.prevRotationYaw + (playerIn.rotationYaw - playerIn.prevRotationYaw) * lvt_4_1_;
        double lvt_7_1_ = playerIn.prevPosX + (playerIn.posX - playerIn.prevPosX) * (double)lvt_4_1_;
        double lvt_9_1_ = playerIn.prevPosY + (playerIn.posY - playerIn.prevPosY) * (double)lvt_4_1_ + (double)playerIn.getEyeHeight();
        double lvt_11_1_ = playerIn.prevPosZ + (playerIn.posZ - playerIn.prevPosZ) * (double)lvt_4_1_;
        Vec3 lvt_13_1_ = new Vec3(lvt_7_1_, lvt_9_1_, lvt_11_1_);
        float lvt_14_1_ = MathHelper.cos(-lvt_6_1_ * 0.017453292F - (float)Math.PI);
        float lvt_15_1_ = MathHelper.sin(-lvt_6_1_ * 0.017453292F - (float)Math.PI);
        float lvt_16_1_ = -MathHelper.cos(-lvt_5_1_ * 0.017453292F);
        float lvt_17_1_ = MathHelper.sin(-lvt_5_1_ * 0.017453292F);
        float lvt_18_1_ = lvt_15_1_ * lvt_16_1_;
        float lvt_20_1_ = lvt_14_1_ * lvt_16_1_;
        double lvt_21_1_ = 5.0D;
        Vec3 lvt_23_1_ = lvt_13_1_.addVector((double)lvt_18_1_ * lvt_21_1_, (double)lvt_17_1_ * lvt_21_1_, (double)lvt_20_1_ * lvt_21_1_);
        MovingObjectPosition lvt_24_1_ = worldIn.rayTraceBlocks(lvt_13_1_, lvt_23_1_, true);

        if (lvt_24_1_ == null)
        {
            return itemStackIn;
        }
        else
        {
            Vec3 lvt_25_1_ = playerIn.getLook(lvt_4_1_);
            boolean lvt_26_1_ = false;
            float lvt_27_1_ = 1.0F;
            List<Entity> lvt_28_1_ = worldIn.getEntitiesWithinAABBExcludingEntity(playerIn, playerIn.getEntityBoundingBox().addCoord(lvt_25_1_.xCoord * lvt_21_1_, lvt_25_1_.yCoord * lvt_21_1_, lvt_25_1_.zCoord * lvt_21_1_).expand((double)lvt_27_1_, (double)lvt_27_1_, (double)lvt_27_1_));

            for (int lvt_29_1_ = 0; lvt_29_1_ < lvt_28_1_.size(); ++lvt_29_1_)
            {
                Entity lvt_30_1_ = (Entity)lvt_28_1_.get(lvt_29_1_);

                if (lvt_30_1_.canBeCollidedWith())
                {
                    float lvt_31_1_ = lvt_30_1_.getCollisionBorderSize();
                    AxisAlignedBB lvt_32_1_ = lvt_30_1_.getEntityBoundingBox().expand((double)lvt_31_1_, (double)lvt_31_1_, (double)lvt_31_1_);

                    if (lvt_32_1_.isVecInside(lvt_13_1_))
                    {
                        lvt_26_1_ = true;
                    }
                }
            }

            if (lvt_26_1_)
            {
                return itemStackIn;
            }
            else
            {
                if (lvt_24_1_.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
                {
                    BlockPos lvt_29_2_ = lvt_24_1_.getBlockPos();

                    if (worldIn.getBlockState(lvt_29_2_).getBlock() == Blocks.snow_layer)
                    {
                        lvt_29_2_ = lvt_29_2_.down();
                    }

                    EntityBoat lvt_30_2_ = new EntityBoat(worldIn, (double)((float)lvt_29_2_.getX() + 0.5F), (double)((float)lvt_29_2_.getY() + 1.0F), (double)((float)lvt_29_2_.getZ() + 0.5F));
                    lvt_30_2_.rotationYaw = (float)(((MathHelper.floor_double((double)(playerIn.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) - 1) * 90);

                    if (!worldIn.getCollidingBoundingBoxes(lvt_30_2_, lvt_30_2_.getEntityBoundingBox().expand(-0.1D, -0.1D, -0.1D)).isEmpty())
                    {
                        return itemStackIn;
                    }

                    if (!worldIn.isRemote)
                    {
                        worldIn.spawnEntityInWorld(lvt_30_2_);
                    }

                    if (!playerIn.capabilities.isCreativeMode)
                    {
                        --itemStackIn.stackSize;
                    }

                    playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
                }

                return itemStackIn;
            }
        }
    }
}
