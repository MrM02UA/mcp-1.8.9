package net.minecraft.pathfinding;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.pathfinder.SwimNodeProcessor;

public class PathNavigateSwimmer extends PathNavigate
{
    public PathNavigateSwimmer(EntityLiving entitylivingIn, World worldIn)
    {
        super(entitylivingIn, worldIn);
    }

    protected PathFinder getPathFinder()
    {
        return new PathFinder(new SwimNodeProcessor());
    }

    /**
     * If on ground or swimming and can swim
     */
    protected boolean canNavigate()
    {
        return this.isInLiquid();
    }

    protected Vec3 getEntityPosition()
    {
        return new Vec3(this.theEntity.posX, this.theEntity.posY + (double)this.theEntity.height * 0.5D, this.theEntity.posZ);
    }

    protected void pathFollow()
    {
        Vec3 lvt_1_1_ = this.getEntityPosition();
        float lvt_2_1_ = this.theEntity.width * this.theEntity.width;
        int lvt_3_1_ = 6;

        if (lvt_1_1_.squareDistanceTo(this.currentPath.getVectorFromIndex(this.theEntity, this.currentPath.getCurrentPathIndex())) < (double)lvt_2_1_)
        {
            this.currentPath.incrementPathIndex();
        }

        for (int lvt_4_1_ = Math.min(this.currentPath.getCurrentPathIndex() + lvt_3_1_, this.currentPath.getCurrentPathLength() - 1); lvt_4_1_ > this.currentPath.getCurrentPathIndex(); --lvt_4_1_)
        {
            Vec3 lvt_5_1_ = this.currentPath.getVectorFromIndex(this.theEntity, lvt_4_1_);

            if (lvt_5_1_.squareDistanceTo(lvt_1_1_) <= 36.0D && this.isDirectPathBetweenPoints(lvt_1_1_, lvt_5_1_, 0, 0, 0))
            {
                this.currentPath.setCurrentPathIndex(lvt_4_1_);
                break;
            }
        }

        this.checkForStuck(lvt_1_1_);
    }

    /**
     * Trims path data from the end to the first sun covered block
     */
    protected void removeSunnyPath()
    {
        super.removeSunnyPath();
    }

    /**
     * Returns true when an entity of specified size could safely walk in a straight line between the two points. Args:
     * pos1, pos2, entityXSize, entityYSize, entityZSize
     */
    protected boolean isDirectPathBetweenPoints(Vec3 posVec31, Vec3 posVec32, int sizeX, int sizeY, int sizeZ)
    {
        MovingObjectPosition lvt_6_1_ = this.worldObj.rayTraceBlocks(posVec31, new Vec3(posVec32.xCoord, posVec32.yCoord + (double)this.theEntity.height * 0.5D, posVec32.zCoord), false, true, false);
        return lvt_6_1_ == null || lvt_6_1_.typeOfHit == MovingObjectPosition.MovingObjectType.MISS;
    }
}
