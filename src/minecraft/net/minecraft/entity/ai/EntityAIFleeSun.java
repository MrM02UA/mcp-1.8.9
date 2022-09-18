package net.minecraft.entity.ai;

import java.util.Random;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityAIFleeSun extends EntityAIBase
{
    private EntityCreature theCreature;
    private double shelterX;
    private double shelterY;
    private double shelterZ;
    private double movementSpeed;
    private World theWorld;

    public EntityAIFleeSun(EntityCreature theCreatureIn, double movementSpeedIn)
    {
        this.theCreature = theCreatureIn;
        this.movementSpeed = movementSpeedIn;
        this.theWorld = theCreatureIn.worldObj;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!this.theWorld.isDaytime())
        {
            return false;
        }
        else if (!this.theCreature.isBurning())
        {
            return false;
        }
        else if (!this.theWorld.canSeeSky(new BlockPos(this.theCreature.posX, this.theCreature.getEntityBoundingBox().minY, this.theCreature.posZ)))
        {
            return false;
        }
        else
        {
            Vec3 lvt_1_1_ = this.findPossibleShelter();

            if (lvt_1_1_ == null)
            {
                return false;
            }
            else
            {
                this.shelterX = lvt_1_1_.xCoord;
                this.shelterY = lvt_1_1_.yCoord;
                this.shelterZ = lvt_1_1_.zCoord;
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.theCreature.getNavigator().noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.theCreature.getNavigator().tryMoveToXYZ(this.shelterX, this.shelterY, this.shelterZ, this.movementSpeed);
    }

    private Vec3 findPossibleShelter()
    {
        Random lvt_1_1_ = this.theCreature.getRNG();
        BlockPos lvt_2_1_ = new BlockPos(this.theCreature.posX, this.theCreature.getEntityBoundingBox().minY, this.theCreature.posZ);

        for (int lvt_3_1_ = 0; lvt_3_1_ < 10; ++lvt_3_1_)
        {
            BlockPos lvt_4_1_ = lvt_2_1_.add(lvt_1_1_.nextInt(20) - 10, lvt_1_1_.nextInt(6) - 3, lvt_1_1_.nextInt(20) - 10);

            if (!this.theWorld.canSeeSky(lvt_4_1_) && this.theCreature.getBlockPathWeight(lvt_4_1_) < 0.0F)
            {
                return new Vec3((double)lvt_4_1_.getX(), (double)lvt_4_1_.getY(), (double)lvt_4_1_.getZ());
            }
        }

        return null;
    }
}
