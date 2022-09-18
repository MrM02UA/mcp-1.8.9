package net.minecraft.item;

import java.util.List;
import java.util.Random;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Rotations;
import net.minecraft.world.World;

public class ItemArmorStand extends Item
{
    public ItemArmorStand()
    {
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (side == EnumFacing.DOWN)
        {
            return false;
        }
        else
        {
            boolean lvt_9_1_ = worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos);
            BlockPos lvt_10_1_ = lvt_9_1_ ? pos : pos.offset(side);

            if (!playerIn.canPlayerEdit(lvt_10_1_, side, stack))
            {
                return false;
            }
            else
            {
                BlockPos lvt_11_1_ = lvt_10_1_.up();
                boolean lvt_12_1_ = !worldIn.isAirBlock(lvt_10_1_) && !worldIn.getBlockState(lvt_10_1_).getBlock().isReplaceable(worldIn, lvt_10_1_);
                lvt_12_1_ = lvt_12_1_ | (!worldIn.isAirBlock(lvt_11_1_) && !worldIn.getBlockState(lvt_11_1_).getBlock().isReplaceable(worldIn, lvt_11_1_));

                if (lvt_12_1_)
                {
                    return false;
                }
                else
                {
                    double lvt_13_1_ = (double)lvt_10_1_.getX();
                    double lvt_15_1_ = (double)lvt_10_1_.getY();
                    double lvt_17_1_ = (double)lvt_10_1_.getZ();
                    List<Entity> lvt_19_1_ = worldIn.getEntitiesWithinAABBExcludingEntity((Entity)null, AxisAlignedBB.fromBounds(lvt_13_1_, lvt_15_1_, lvt_17_1_, lvt_13_1_ + 1.0D, lvt_15_1_ + 2.0D, lvt_17_1_ + 1.0D));

                    if (lvt_19_1_.size() > 0)
                    {
                        return false;
                    }
                    else
                    {
                        if (!worldIn.isRemote)
                        {
                            worldIn.setBlockToAir(lvt_10_1_);
                            worldIn.setBlockToAir(lvt_11_1_);
                            EntityArmorStand lvt_20_1_ = new EntityArmorStand(worldIn, lvt_13_1_ + 0.5D, lvt_15_1_, lvt_17_1_ + 0.5D);
                            float lvt_21_1_ = (float)MathHelper.floor_float((MathHelper.wrapAngleTo180_float(playerIn.rotationYaw - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                            lvt_20_1_.setLocationAndAngles(lvt_13_1_ + 0.5D, lvt_15_1_, lvt_17_1_ + 0.5D, lvt_21_1_, 0.0F);
                            this.applyRandomRotations(lvt_20_1_, worldIn.rand);
                            NBTTagCompound lvt_22_1_ = stack.getTagCompound();

                            if (lvt_22_1_ != null && lvt_22_1_.hasKey("EntityTag", 10))
                            {
                                NBTTagCompound lvt_23_1_ = new NBTTagCompound();
                                lvt_20_1_.writeToNBTOptional(lvt_23_1_);
                                lvt_23_1_.merge(lvt_22_1_.getCompoundTag("EntityTag"));
                                lvt_20_1_.readFromNBT(lvt_23_1_);
                            }

                            worldIn.spawnEntityInWorld(lvt_20_1_);
                        }

                        --stack.stackSize;
                        return true;
                    }
                }
            }
        }
    }

    private void applyRandomRotations(EntityArmorStand armorStand, Random rand)
    {
        Rotations lvt_3_1_ = armorStand.getHeadRotation();
        float lvt_5_1_ = rand.nextFloat() * 5.0F;
        float lvt_6_1_ = rand.nextFloat() * 20.0F - 10.0F;
        Rotations lvt_4_1_ = new Rotations(lvt_3_1_.getX() + lvt_5_1_, lvt_3_1_.getY() + lvt_6_1_, lvt_3_1_.getZ());
        armorStand.setHeadRotation(lvt_4_1_);
        lvt_3_1_ = armorStand.getBodyRotation();
        lvt_5_1_ = rand.nextFloat() * 10.0F - 5.0F;
        lvt_4_1_ = new Rotations(lvt_3_1_.getX(), lvt_3_1_.getY() + lvt_5_1_, lvt_3_1_.getZ());
        armorStand.setBodyRotation(lvt_4_1_);
    }
}
