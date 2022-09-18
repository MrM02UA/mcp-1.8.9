package net.minecraft.pathfinding;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;

public abstract class PathNavigate
{
    protected EntityLiving theEntity;
    protected World worldObj;

    /** The PathEntity being followed. */
    protected PathEntity currentPath;
    protected double speed;

    /**
     * The number of blocks (extra) +/- in each axis that get pulled out as cache for the pathfinder's search space
     */
    private final IAttributeInstance pathSearchRange;

    /** Time, in number of ticks, following the current path */
    private int totalTicks;

    /**
     * The time when the last position check was done (to detect successful movement)
     */
    private int ticksAtLastPos;

    /**
     * Coordinates of the entity's position last time a check was done (part of monitoring getting 'stuck')
     */
    private Vec3 lastPosCheck = new Vec3(0.0D, 0.0D, 0.0D);
    private float heightRequirement = 1.0F;
    private final PathFinder pathFinder;

    public PathNavigate(EntityLiving entitylivingIn, World worldIn)
    {
        this.theEntity = entitylivingIn;
        this.worldObj = worldIn;
        this.pathSearchRange = entitylivingIn.getEntityAttribute(SharedMonsterAttributes.followRange);
        this.pathFinder = this.getPathFinder();
    }

    protected abstract PathFinder getPathFinder();

    /**
     * Sets the speed
     */
    public void setSpeed(double speedIn)
    {
        this.speed = speedIn;
    }

    /**
     * Gets the maximum distance that the path finding will search in.
     */
    public float getPathSearchRange()
    {
        return (float)this.pathSearchRange.getAttributeValue();
    }

    /**
     * Returns the path to the given coordinates. Args : x, y, z
     */
    public final PathEntity getPathToXYZ(double x, double y, double z)
    {
        return this.getPathToPos(new BlockPos(MathHelper.floor_double(x), (int)y, MathHelper.floor_double(z)));
    }

    /**
     * Returns path to given BlockPos
     */
    public PathEntity getPathToPos(BlockPos pos)
    {
        if (!this.canNavigate())
        {
            return null;
        }
        else
        {
            float lvt_2_1_ = this.getPathSearchRange();
            this.worldObj.theProfiler.startSection("pathfind");
            BlockPos lvt_3_1_ = new BlockPos(this.theEntity);
            int lvt_4_1_ = (int)(lvt_2_1_ + 8.0F);
            ChunkCache lvt_5_1_ = new ChunkCache(this.worldObj, lvt_3_1_.add(-lvt_4_1_, -lvt_4_1_, -lvt_4_1_), lvt_3_1_.add(lvt_4_1_, lvt_4_1_, lvt_4_1_), 0);
            PathEntity lvt_6_1_ = this.pathFinder.createEntityPathTo(lvt_5_1_, this.theEntity, pos, lvt_2_1_);
            this.worldObj.theProfiler.endSection();
            return lvt_6_1_;
        }
    }

    /**
     * Try to find and set a path to XYZ. Returns true if successful. Args : x, y, z, speed
     */
    public boolean tryMoveToXYZ(double x, double y, double z, double speedIn)
    {
        PathEntity lvt_9_1_ = this.getPathToXYZ((double)MathHelper.floor_double(x), (double)((int)y), (double)MathHelper.floor_double(z));
        return this.setPath(lvt_9_1_, speedIn);
    }

    /**
     * Sets vertical space requirement for path
     */
    public void setHeightRequirement(float jumpHeight)
    {
        this.heightRequirement = jumpHeight;
    }

    /**
     * Returns the path to the given EntityLiving. Args : entity
     */
    public PathEntity getPathToEntityLiving(Entity entityIn)
    {
        if (!this.canNavigate())
        {
            return null;
        }
        else
        {
            float lvt_2_1_ = this.getPathSearchRange();
            this.worldObj.theProfiler.startSection("pathfind");
            BlockPos lvt_3_1_ = (new BlockPos(this.theEntity)).up();
            int lvt_4_1_ = (int)(lvt_2_1_ + 16.0F);
            ChunkCache lvt_5_1_ = new ChunkCache(this.worldObj, lvt_3_1_.add(-lvt_4_1_, -lvt_4_1_, -lvt_4_1_), lvt_3_1_.add(lvt_4_1_, lvt_4_1_, lvt_4_1_), 0);
            PathEntity lvt_6_1_ = this.pathFinder.createEntityPathTo(lvt_5_1_, this.theEntity, entityIn, lvt_2_1_);
            this.worldObj.theProfiler.endSection();
            return lvt_6_1_;
        }
    }

    /**
     * Try to find and set a path to EntityLiving. Returns true if successful. Args : entity, speed
     */
    public boolean tryMoveToEntityLiving(Entity entityIn, double speedIn)
    {
        PathEntity lvt_4_1_ = this.getPathToEntityLiving(entityIn);
        return lvt_4_1_ != null ? this.setPath(lvt_4_1_, speedIn) : false;
    }

    /**
     * Sets a new path. If it's diferent from the old path. Checks to adjust path for sun avoiding, and stores start
     * coords. Args : path, speed
     */
    public boolean setPath(PathEntity pathentityIn, double speedIn)
    {
        if (pathentityIn == null)
        {
            this.currentPath = null;
            return false;
        }
        else
        {
            if (!pathentityIn.isSamePath(this.currentPath))
            {
                this.currentPath = pathentityIn;
            }

            this.removeSunnyPath();

            if (this.currentPath.getCurrentPathLength() == 0)
            {
                return false;
            }
            else
            {
                this.speed = speedIn;
                Vec3 lvt_4_1_ = this.getEntityPosition();
                this.ticksAtLastPos = this.totalTicks;
                this.lastPosCheck = lvt_4_1_;
                return true;
            }
        }
    }

    /**
     * gets the actively used PathEntity
     */
    public PathEntity getPath()
    {
        return this.currentPath;
    }

    public void onUpdateNavigation()
    {
        ++this.totalTicks;

        if (!this.noPath())
        {
            if (this.canNavigate())
            {
                this.pathFollow();
            }
            else if (this.currentPath != null && this.currentPath.getCurrentPathIndex() < this.currentPath.getCurrentPathLength())
            {
                Vec3 lvt_1_1_ = this.getEntityPosition();
                Vec3 lvt_2_1_ = this.currentPath.getVectorFromIndex(this.theEntity, this.currentPath.getCurrentPathIndex());

                if (lvt_1_1_.yCoord > lvt_2_1_.yCoord && !this.theEntity.onGround && MathHelper.floor_double(lvt_1_1_.xCoord) == MathHelper.floor_double(lvt_2_1_.xCoord) && MathHelper.floor_double(lvt_1_1_.zCoord) == MathHelper.floor_double(lvt_2_1_.zCoord))
                {
                    this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
                }
            }

            if (!this.noPath())
            {
                Vec3 lvt_1_2_ = this.currentPath.getPosition(this.theEntity);

                if (lvt_1_2_ != null)
                {
                    AxisAlignedBB lvt_2_2_ = (new AxisAlignedBB(lvt_1_2_.xCoord, lvt_1_2_.yCoord, lvt_1_2_.zCoord, lvt_1_2_.xCoord, lvt_1_2_.yCoord, lvt_1_2_.zCoord)).expand(0.5D, 0.5D, 0.5D);
                    List<AxisAlignedBB> lvt_3_1_ = this.worldObj.getCollidingBoundingBoxes(this.theEntity, lvt_2_2_.addCoord(0.0D, -1.0D, 0.0D));
                    double lvt_4_1_ = -1.0D;
                    lvt_2_2_ = lvt_2_2_.offset(0.0D, 1.0D, 0.0D);

                    for (AxisAlignedBB lvt_7_1_ : lvt_3_1_)
                    {
                        lvt_4_1_ = lvt_7_1_.calculateYOffset(lvt_2_2_, lvt_4_1_);
                    }

                    this.theEntity.getMoveHelper().setMoveTo(lvt_1_2_.xCoord, lvt_1_2_.yCoord + lvt_4_1_, lvt_1_2_.zCoord, this.speed);
                }
            }
        }
    }

    protected void pathFollow()
    {
        Vec3 lvt_1_1_ = this.getEntityPosition();
        int lvt_2_1_ = this.currentPath.getCurrentPathLength();

        for (int lvt_3_1_ = this.currentPath.getCurrentPathIndex(); lvt_3_1_ < this.currentPath.getCurrentPathLength(); ++lvt_3_1_)
        {
            if (this.currentPath.getPathPointFromIndex(lvt_3_1_).yCoord != (int)lvt_1_1_.yCoord)
            {
                lvt_2_1_ = lvt_3_1_;
                break;
            }
        }

        float lvt_3_2_ = this.theEntity.width * this.theEntity.width * this.heightRequirement;

        for (int lvt_4_1_ = this.currentPath.getCurrentPathIndex(); lvt_4_1_ < lvt_2_1_; ++lvt_4_1_)
        {
            Vec3 lvt_5_1_ = this.currentPath.getVectorFromIndex(this.theEntity, lvt_4_1_);

            if (lvt_1_1_.squareDistanceTo(lvt_5_1_) < (double)lvt_3_2_)
            {
                this.currentPath.setCurrentPathIndex(lvt_4_1_ + 1);
            }
        }

        int lvt_4_2_ = MathHelper.ceiling_float_int(this.theEntity.width);
        int lvt_5_2_ = (int)this.theEntity.height + 1;
        int lvt_6_1_ = lvt_4_2_;

        for (int lvt_7_1_ = lvt_2_1_ - 1; lvt_7_1_ >= this.currentPath.getCurrentPathIndex(); --lvt_7_1_)
        {
            if (this.isDirectPathBetweenPoints(lvt_1_1_, this.currentPath.getVectorFromIndex(this.theEntity, lvt_7_1_), lvt_4_2_, lvt_5_2_, lvt_6_1_))
            {
                this.currentPath.setCurrentPathIndex(lvt_7_1_);
                break;
            }
        }

        this.checkForStuck(lvt_1_1_);
    }

    /**
     * Checks if entity haven't been moved when last checked and if so, clears current {@link
     * net.minecraft.pathfinding.PathEntity}
     */
    protected void checkForStuck(Vec3 positionVec3)
    {
        if (this.totalTicks - this.ticksAtLastPos > 100)
        {
            if (positionVec3.squareDistanceTo(this.lastPosCheck) < 2.25D)
            {
                this.clearPathEntity();
            }

            this.ticksAtLastPos = this.totalTicks;
            this.lastPosCheck = positionVec3;
        }
    }

    /**
     * If null path or reached the end
     */
    public boolean noPath()
    {
        return this.currentPath == null || this.currentPath.isFinished();
    }

    /**
     * sets active PathEntity to null
     */
    public void clearPathEntity()
    {
        this.currentPath = null;
    }

    protected abstract Vec3 getEntityPosition();

    /**
     * If on ground or swimming and can swim
     */
    protected abstract boolean canNavigate();

    /**
     * Returns true if the entity is in water or lava, false otherwise
     */
    protected boolean isInLiquid()
    {
        return this.theEntity.isInWater() || this.theEntity.isInLava();
    }

    /**
     * Trims path data from the end to the first sun covered block
     */
    protected void removeSunnyPath()
    {
    }

    /**
     * Returns true when an entity of specified size could safely walk in a straight line between the two points. Args:
     * pos1, pos2, entityXSize, entityYSize, entityZSize
     */
    protected abstract boolean isDirectPathBetweenPoints(Vec3 posVec31, Vec3 posVec32, int sizeX, int sizeY, int sizeZ);
}
