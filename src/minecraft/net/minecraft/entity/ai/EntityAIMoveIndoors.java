package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

public class EntityAIMoveIndoors extends EntityAIBase
{
    private EntityCreature entityObj;
    private VillageDoorInfo doorInfo;
    private int insidePosX = -1;
    private int insidePosZ = -1;

    public EntityAIMoveIndoors(EntityCreature entityObjIn)
    {
        this.entityObj = entityObjIn;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        BlockPos lvt_1_1_ = new BlockPos(this.entityObj);

        if ((!this.entityObj.worldObj.isDaytime() || this.entityObj.worldObj.isRaining() && !this.entityObj.worldObj.getBiomeGenForCoords(lvt_1_1_).canRain()) && !this.entityObj.worldObj.provider.getHasNoSky())
        {
            if (this.entityObj.getRNG().nextInt(50) != 0)
            {
                return false;
            }
            else if (this.insidePosX != -1 && this.entityObj.getDistanceSq((double)this.insidePosX, this.entityObj.posY, (double)this.insidePosZ) < 4.0D)
            {
                return false;
            }
            else
            {
                Village lvt_2_1_ = this.entityObj.worldObj.getVillageCollection().getNearestVillage(lvt_1_1_, 14);

                if (lvt_2_1_ == null)
                {
                    return false;
                }
                else
                {
                    this.doorInfo = lvt_2_1_.getDoorInfo(lvt_1_1_);
                    return this.doorInfo != null;
                }
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.entityObj.getNavigator().noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.insidePosX = -1;
        BlockPos lvt_1_1_ = this.doorInfo.getInsideBlockPos();
        int lvt_2_1_ = lvt_1_1_.getX();
        int lvt_3_1_ = lvt_1_1_.getY();
        int lvt_4_1_ = lvt_1_1_.getZ();

        if (this.entityObj.getDistanceSq(lvt_1_1_) > 256.0D)
        {
            Vec3 lvt_5_1_ = RandomPositionGenerator.findRandomTargetBlockTowards(this.entityObj, 14, 3, new Vec3((double)lvt_2_1_ + 0.5D, (double)lvt_3_1_, (double)lvt_4_1_ + 0.5D));

            if (lvt_5_1_ != null)
            {
                this.entityObj.getNavigator().tryMoveToXYZ(lvt_5_1_.xCoord, lvt_5_1_.yCoord, lvt_5_1_.zCoord, 1.0D);
            }
        }
        else
        {
            this.entityObj.getNavigator().tryMoveToXYZ((double)lvt_2_1_ + 0.5D, (double)lvt_3_1_, (double)lvt_4_1_ + 0.5D, 1.0D);
        }
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.insidePosX = this.doorInfo.getInsideBlockPos().getX();
        this.insidePosZ = this.doorInfo.getInsideBlockPos().getZ();
        this.doorInfo = null;
    }
}
