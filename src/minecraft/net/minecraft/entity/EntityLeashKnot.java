package net.minecraft.entity;

import net.minecraft.block.BlockFence;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class EntityLeashKnot extends EntityHanging
{
    public EntityLeashKnot(World worldIn)
    {
        super(worldIn);
    }

    public EntityLeashKnot(World worldIn, BlockPos hangingPositionIn)
    {
        super(worldIn, hangingPositionIn);
        this.setPosition((double)hangingPositionIn.getX() + 0.5D, (double)hangingPositionIn.getY() + 0.5D, (double)hangingPositionIn.getZ() + 0.5D);
        float lvt_3_1_ = 0.125F;
        float lvt_4_1_ = 0.1875F;
        float lvt_5_1_ = 0.25F;
        this.setEntityBoundingBox(new AxisAlignedBB(this.posX - 0.1875D, this.posY - 0.25D + 0.125D, this.posZ - 0.1875D, this.posX + 0.1875D, this.posY + 0.25D + 0.125D, this.posZ + 0.1875D));
    }

    protected void entityInit()
    {
        super.entityInit();
    }

    /**
     * Updates facing and bounding box based on it
     */
    public void updateFacingWithBoundingBox(EnumFacing facingDirectionIn)
    {
    }

    public int getWidthPixels()
    {
        return 9;
    }

    public int getHeightPixels()
    {
        return 9;
    }

    public float getEyeHeight()
    {
        return -0.0625F;
    }

    /**
     * Checks if the entity is in range to render by using the past in distance and comparing it to its average edge
     * length * 64 * renderDistanceWeight Args: distance
     */
    public boolean isInRangeToRenderDist(double distance)
    {
        return distance < 1024.0D;
    }

    /**
     * Called when this entity is broken. Entity parameter may be null.
     */
    public void onBroken(Entity brokenEntity)
    {
    }

    /**
     * Either write this entity to the NBT tag given and return true, or return false without doing anything. If this
     * returns false the entity is not saved on disk. Ridden entities return false here as they are saved with their
     * rider.
     */
    public boolean writeToNBTOptional(NBTTagCompound tagCompund)
    {
        return false;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tagCompund)
    {
    }

    /**
     * First layer of player interaction
     */
    public boolean interactFirst(EntityPlayer playerIn)
    {
        ItemStack lvt_2_1_ = playerIn.getHeldItem();
        boolean lvt_3_1_ = false;

        if (lvt_2_1_ != null && lvt_2_1_.getItem() == Items.lead && !this.worldObj.isRemote)
        {
            double lvt_4_1_ = 7.0D;

            for (EntityLiving lvt_8_1_ : this.worldObj.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(this.posX - lvt_4_1_, this.posY - lvt_4_1_, this.posZ - lvt_4_1_, this.posX + lvt_4_1_, this.posY + lvt_4_1_, this.posZ + lvt_4_1_)))
            {
                if (lvt_8_1_.getLeashed() && lvt_8_1_.getLeashedToEntity() == playerIn)
                {
                    lvt_8_1_.setLeashedToEntity(this, true);
                    lvt_3_1_ = true;
                }
            }
        }

        if (!this.worldObj.isRemote && !lvt_3_1_)
        {
            this.setDead();

            if (playerIn.capabilities.isCreativeMode)
            {
                double lvt_4_2_ = 7.0D;

                for (EntityLiving lvt_8_2_ : this.worldObj.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(this.posX - lvt_4_2_, this.posY - lvt_4_2_, this.posZ - lvt_4_2_, this.posX + lvt_4_2_, this.posY + lvt_4_2_, this.posZ + lvt_4_2_)))
                {
                    if (lvt_8_2_.getLeashed() && lvt_8_2_.getLeashedToEntity() == this)
                    {
                        lvt_8_2_.clearLeashed(true, false);
                    }
                }
            }
        }

        return true;
    }

    /**
     * checks to make sure painting can be placed there
     */
    public boolean onValidSurface()
    {
        return this.worldObj.getBlockState(this.hangingPosition).getBlock() instanceof BlockFence;
    }

    public static EntityLeashKnot createKnot(World worldIn, BlockPos fence)
    {
        EntityLeashKnot lvt_2_1_ = new EntityLeashKnot(worldIn, fence);
        lvt_2_1_.forceSpawn = true;
        worldIn.spawnEntityInWorld(lvt_2_1_);
        return lvt_2_1_;
    }

    public static EntityLeashKnot getKnotForPosition(World worldIn, BlockPos pos)
    {
        int lvt_2_1_ = pos.getX();
        int lvt_3_1_ = pos.getY();
        int lvt_4_1_ = pos.getZ();

        for (EntityLeashKnot lvt_7_1_ : worldIn.getEntitiesWithinAABB(EntityLeashKnot.class, new AxisAlignedBB((double)lvt_2_1_ - 1.0D, (double)lvt_3_1_ - 1.0D, (double)lvt_4_1_ - 1.0D, (double)lvt_2_1_ + 1.0D, (double)lvt_3_1_ + 1.0D, (double)lvt_4_1_ + 1.0D)))
        {
            if (lvt_7_1_.getHangingPosition().equals(pos))
            {
                return lvt_7_1_;
            }
        }

        return null;
    }
}
