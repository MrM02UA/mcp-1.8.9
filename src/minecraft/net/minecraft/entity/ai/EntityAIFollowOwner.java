package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIFollowOwner extends EntityAIBase
{
    private EntityTameable thePet;
    private EntityLivingBase theOwner;
    World theWorld;
    private double followSpeed;
    private PathNavigate petPathfinder;
    private int field_75343_h;
    float maxDist;
    float minDist;
    private boolean field_75344_i;

    public EntityAIFollowOwner(EntityTameable thePetIn, double followSpeedIn, float minDistIn, float maxDistIn)
    {
        this.thePet = thePetIn;
        this.theWorld = thePetIn.worldObj;
        this.followSpeed = followSpeedIn;
        this.petPathfinder = thePetIn.getNavigator();
        this.minDist = minDistIn;
        this.maxDist = maxDistIn;
        this.setMutexBits(3);

        if (!(thePetIn.getNavigator() instanceof PathNavigateGround))
        {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLivingBase lvt_1_1_ = this.thePet.getOwner();

        if (lvt_1_1_ == null)
        {
            return false;
        }
        else if (lvt_1_1_ instanceof EntityPlayer && ((EntityPlayer)lvt_1_1_).isSpectator())
        {
            return false;
        }
        else if (this.thePet.isSitting())
        {
            return false;
        }
        else if (this.thePet.getDistanceSqToEntity(lvt_1_1_) < (double)(this.minDist * this.minDist))
        {
            return false;
        }
        else
        {
            this.theOwner = lvt_1_1_;
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.petPathfinder.noPath() && this.thePet.getDistanceSqToEntity(this.theOwner) > (double)(this.maxDist * this.maxDist) && !this.thePet.isSitting();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.field_75343_h = 0;
        this.field_75344_i = ((PathNavigateGround)this.thePet.getNavigator()).getAvoidsWater();
        ((PathNavigateGround)this.thePet.getNavigator()).setAvoidsWater(false);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.theOwner = null;
        this.petPathfinder.clearPathEntity();
        ((PathNavigateGround)this.thePet.getNavigator()).setAvoidsWater(true);
    }

    private boolean func_181065_a(BlockPos p_181065_1_)
    {
        IBlockState lvt_2_1_ = this.theWorld.getBlockState(p_181065_1_);
        Block lvt_3_1_ = lvt_2_1_.getBlock();
        return lvt_3_1_ == Blocks.air ? true : !lvt_3_1_.isFullCube();
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        this.thePet.getLookHelper().setLookPositionWithEntity(this.theOwner, 10.0F, (float)this.thePet.getVerticalFaceSpeed());

        if (!this.thePet.isSitting())
        {
            if (--this.field_75343_h <= 0)
            {
                this.field_75343_h = 10;

                if (!this.petPathfinder.tryMoveToEntityLiving(this.theOwner, this.followSpeed))
                {
                    if (!this.thePet.getLeashed())
                    {
                        if (this.thePet.getDistanceSqToEntity(this.theOwner) >= 144.0D)
                        {
                            int lvt_1_1_ = MathHelper.floor_double(this.theOwner.posX) - 2;
                            int lvt_2_1_ = MathHelper.floor_double(this.theOwner.posZ) - 2;
                            int lvt_3_1_ = MathHelper.floor_double(this.theOwner.getEntityBoundingBox().minY);

                            for (int lvt_4_1_ = 0; lvt_4_1_ <= 4; ++lvt_4_1_)
                            {
                                for (int lvt_5_1_ = 0; lvt_5_1_ <= 4; ++lvt_5_1_)
                                {
                                    if ((lvt_4_1_ < 1 || lvt_5_1_ < 1 || lvt_4_1_ > 3 || lvt_5_1_ > 3) && World.doesBlockHaveSolidTopSurface(this.theWorld, new BlockPos(lvt_1_1_ + lvt_4_1_, lvt_3_1_ - 1, lvt_2_1_ + lvt_5_1_)) && this.func_181065_a(new BlockPos(lvt_1_1_ + lvt_4_1_, lvt_3_1_, lvt_2_1_ + lvt_5_1_)) && this.func_181065_a(new BlockPos(lvt_1_1_ + lvt_4_1_, lvt_3_1_ + 1, lvt_2_1_ + lvt_5_1_)))
                                    {
                                        this.thePet.setLocationAndAngles((double)((float)(lvt_1_1_ + lvt_4_1_) + 0.5F), (double)lvt_3_1_, (double)((float)(lvt_2_1_ + lvt_5_1_) + 0.5F), this.thePet.rotationYaw, this.thePet.rotationPitch);
                                        this.petPathfinder.clearPathEntity();
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
