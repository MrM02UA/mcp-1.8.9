package net.minecraft.pathfinding;

import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.pathfinder.NodeProcessor;

public class PathFinder
{
    /** The path being generated */
    private Path path = new Path();

    /** Selection of path points to add to the path */
    private PathPoint[] pathOptions = new PathPoint[32];
    private NodeProcessor nodeProcessor;

    public PathFinder(NodeProcessor nodeProcessorIn)
    {
        this.nodeProcessor = nodeProcessorIn;
    }

    /**
     * Creates a path from one entity to another within a minimum distance
     */
    public PathEntity createEntityPathTo(IBlockAccess blockaccess, Entity entityFrom, Entity entityTo, float dist)
    {
        return this.createEntityPathTo(blockaccess, entityFrom, entityTo.posX, entityTo.getEntityBoundingBox().minY, entityTo.posZ, dist);
    }

    /**
     * Creates a path from an entity to a specified location within a minimum distance
     */
    public PathEntity createEntityPathTo(IBlockAccess blockaccess, Entity entityIn, BlockPos targetPos, float dist)
    {
        return this.createEntityPathTo(blockaccess, entityIn, (double)((float)targetPos.getX() + 0.5F), (double)((float)targetPos.getY() + 0.5F), (double)((float)targetPos.getZ() + 0.5F), dist);
    }

    /**
     * Internal implementation of creating a path from an entity to a point
     */
    private PathEntity createEntityPathTo(IBlockAccess blockaccess, Entity entityIn, double x, double y, double z, float distance)
    {
        this.path.clearPath();
        this.nodeProcessor.initProcessor(blockaccess, entityIn);
        PathPoint lvt_10_1_ = this.nodeProcessor.getPathPointTo(entityIn);
        PathPoint lvt_11_1_ = this.nodeProcessor.getPathPointToCoords(entityIn, x, y, z);
        PathEntity lvt_12_1_ = this.addToPath(entityIn, lvt_10_1_, lvt_11_1_, distance);
        this.nodeProcessor.postProcess();
        return lvt_12_1_;
    }

    /**
     * Adds a path from start to end and returns the whole path
     */
    private PathEntity addToPath(Entity entityIn, PathPoint pathpointStart, PathPoint pathpointEnd, float maxDistance)
    {
        pathpointStart.totalPathDistance = 0.0F;
        pathpointStart.distanceToNext = pathpointStart.distanceToSquared(pathpointEnd);
        pathpointStart.distanceToTarget = pathpointStart.distanceToNext;
        this.path.clearPath();
        this.path.addPoint(pathpointStart);
        PathPoint lvt_5_1_ = pathpointStart;

        while (!this.path.isPathEmpty())
        {
            PathPoint lvt_6_1_ = this.path.dequeue();

            if (lvt_6_1_.equals(pathpointEnd))
            {
                return this.createEntityPath(pathpointStart, pathpointEnd);
            }

            if (lvt_6_1_.distanceToSquared(pathpointEnd) < lvt_5_1_.distanceToSquared(pathpointEnd))
            {
                lvt_5_1_ = lvt_6_1_;
            }

            lvt_6_1_.visited = true;
            int lvt_7_1_ = this.nodeProcessor.findPathOptions(this.pathOptions, entityIn, lvt_6_1_, pathpointEnd, maxDistance);

            for (int lvt_8_1_ = 0; lvt_8_1_ < lvt_7_1_; ++lvt_8_1_)
            {
                PathPoint lvt_9_1_ = this.pathOptions[lvt_8_1_];
                float lvt_10_1_ = lvt_6_1_.totalPathDistance + lvt_6_1_.distanceToSquared(lvt_9_1_);

                if (lvt_10_1_ < maxDistance * 2.0F && (!lvt_9_1_.isAssigned() || lvt_10_1_ < lvt_9_1_.totalPathDistance))
                {
                    lvt_9_1_.previous = lvt_6_1_;
                    lvt_9_1_.totalPathDistance = lvt_10_1_;
                    lvt_9_1_.distanceToNext = lvt_9_1_.distanceToSquared(pathpointEnd);

                    if (lvt_9_1_.isAssigned())
                    {
                        this.path.changeDistance(lvt_9_1_, lvt_9_1_.totalPathDistance + lvt_9_1_.distanceToNext);
                    }
                    else
                    {
                        lvt_9_1_.distanceToTarget = lvt_9_1_.totalPathDistance + lvt_9_1_.distanceToNext;
                        this.path.addPoint(lvt_9_1_);
                    }
                }
            }
        }

        if (lvt_5_1_ == pathpointStart)
        {
            return null;
        }
        else
        {
            return this.createEntityPath(pathpointStart, lvt_5_1_);
        }
    }

    /**
     * Returns a new PathEntity for a given start and end point
     */
    private PathEntity createEntityPath(PathPoint start, PathPoint end)
    {
        int lvt_3_1_ = 1;

        for (PathPoint lvt_4_1_ = end; lvt_4_1_.previous != null; lvt_4_1_ = lvt_4_1_.previous)
        {
            ++lvt_3_1_;
        }

        PathPoint[] lvt_5_1_ = new PathPoint[lvt_3_1_];
        PathPoint var7 = end;
        --lvt_3_1_;

        for (lvt_5_1_[lvt_3_1_] = end; var7.previous != null; lvt_5_1_[lvt_3_1_] = var7)
        {
            var7 = var7.previous;
            --lvt_3_1_;
        }

        return new PathEntity(lvt_5_1_);
    }
}
