package net.minecraft.entity.ai;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

public class EntityAIMoveThroughVillage extends EntityAIBase
{
    private EntityCreature theEntity;
    private double movementSpeed;

    /** The PathNavigate of our entity. */
    private PathEntity entityPathNavigate;
    private VillageDoorInfo doorInfo;
    private boolean isNocturnal;
    private List<VillageDoorInfo> doorList = Lists.newArrayList();

    public EntityAIMoveThroughVillage(EntityCreature theEntityIn, double movementSpeedIn, boolean isNocturnalIn)
    {
        this.theEntity = theEntityIn;
        this.movementSpeed = movementSpeedIn;
        this.isNocturnal = isNocturnalIn;
        this.setMutexBits(1);

        if (!(theEntityIn.getNavigator() instanceof PathNavigateGround))
        {
            throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
        }
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        this.resizeDoorList();

        if (this.isNocturnal && this.theEntity.worldObj.isDaytime())
        {
            return false;
        }
        else
        {
            Village lvt_1_1_ = this.theEntity.worldObj.getVillageCollection().getNearestVillage(new BlockPos(this.theEntity), 0);

            if (lvt_1_1_ == null)
            {
                return false;
            }
            else
            {
                this.doorInfo = this.findNearestDoor(lvt_1_1_);

                if (this.doorInfo == null)
                {
                    return false;
                }
                else
                {
                    PathNavigateGround lvt_2_1_ = (PathNavigateGround)this.theEntity.getNavigator();
                    boolean lvt_3_1_ = lvt_2_1_.getEnterDoors();
                    lvt_2_1_.setBreakDoors(false);
                    this.entityPathNavigate = lvt_2_1_.getPathToPos(this.doorInfo.getDoorBlockPos());
                    lvt_2_1_.setBreakDoors(lvt_3_1_);

                    if (this.entityPathNavigate != null)
                    {
                        return true;
                    }
                    else
                    {
                        Vec3 lvt_4_1_ = RandomPositionGenerator.findRandomTargetBlockTowards(this.theEntity, 10, 7, new Vec3((double)this.doorInfo.getDoorBlockPos().getX(), (double)this.doorInfo.getDoorBlockPos().getY(), (double)this.doorInfo.getDoorBlockPos().getZ()));

                        if (lvt_4_1_ == null)
                        {
                            return false;
                        }
                        else
                        {
                            lvt_2_1_.setBreakDoors(false);
                            this.entityPathNavigate = this.theEntity.getNavigator().getPathToXYZ(lvt_4_1_.xCoord, lvt_4_1_.yCoord, lvt_4_1_.zCoord);
                            lvt_2_1_.setBreakDoors(lvt_3_1_);
                            return this.entityPathNavigate != null;
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        if (this.theEntity.getNavigator().noPath())
        {
            return false;
        }
        else
        {
            float lvt_1_1_ = this.theEntity.width + 4.0F;
            return this.theEntity.getDistanceSq(this.doorInfo.getDoorBlockPos()) > (double)(lvt_1_1_ * lvt_1_1_);
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.theEntity.getNavigator().setPath(this.entityPathNavigate, this.movementSpeed);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        if (this.theEntity.getNavigator().noPath() || this.theEntity.getDistanceSq(this.doorInfo.getDoorBlockPos()) < 16.0D)
        {
            this.doorList.add(this.doorInfo);
        }
    }

    private VillageDoorInfo findNearestDoor(Village villageIn)
    {
        VillageDoorInfo lvt_2_1_ = null;
        int lvt_3_1_ = Integer.MAX_VALUE;

        for (VillageDoorInfo lvt_6_1_ : villageIn.getVillageDoorInfoList())
        {
            int lvt_7_1_ = lvt_6_1_.getDistanceSquared(MathHelper.floor_double(this.theEntity.posX), MathHelper.floor_double(this.theEntity.posY), MathHelper.floor_double(this.theEntity.posZ));

            if (lvt_7_1_ < lvt_3_1_ && !this.doesDoorListContain(lvt_6_1_))
            {
                lvt_2_1_ = lvt_6_1_;
                lvt_3_1_ = lvt_7_1_;
            }
        }

        return lvt_2_1_;
    }

    private boolean doesDoorListContain(VillageDoorInfo doorInfoIn)
    {
        for (VillageDoorInfo lvt_3_1_ : this.doorList)
        {
            if (doorInfoIn.getDoorBlockPos().equals(lvt_3_1_.getDoorBlockPos()))
            {
                return true;
            }
        }

        return false;
    }

    private void resizeDoorList()
    {
        if (this.doorList.size() > 15)
        {
            this.doorList.remove(0);
        }
    }
}
