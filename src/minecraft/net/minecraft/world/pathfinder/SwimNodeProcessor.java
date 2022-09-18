package net.minecraft.world.pathfinder;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public class SwimNodeProcessor extends NodeProcessor
{
    public void initProcessor(IBlockAccess iblockaccessIn, Entity entityIn)
    {
        super.initProcessor(iblockaccessIn, entityIn);
    }

    /**
     * This method is called when all nodes have been processed and PathEntity is created.
     *  {@link net.minecraft.world.pathfinder.WalkNodeProcessor WalkNodeProcessor} uses this to change its field {@link
     * net.minecraft.world.pathfinder.WalkNodeProcessor#avoidsWater avoidsWater}
     */
    public void postProcess()
    {
        super.postProcess();
    }

    /**
     * Returns given entity's position as PathPoint
     */
    public PathPoint getPathPointTo(Entity entityIn)
    {
        return this.openPoint(MathHelper.floor_double(entityIn.getEntityBoundingBox().minX), MathHelper.floor_double(entityIn.getEntityBoundingBox().minY + 0.5D), MathHelper.floor_double(entityIn.getEntityBoundingBox().minZ));
    }

    /**
     * Returns PathPoint for given coordinates
     */
    public PathPoint getPathPointToCoords(Entity entityIn, double x, double y, double target)
    {
        return this.openPoint(MathHelper.floor_double(x - (double)(entityIn.width / 2.0F)), MathHelper.floor_double(y + 0.5D), MathHelper.floor_double(target - (double)(entityIn.width / 2.0F)));
    }

    public int findPathOptions(PathPoint[] pathOptions, Entity entityIn, PathPoint currentPoint, PathPoint targetPoint, float maxDistance)
    {
        int lvt_6_1_ = 0;

        for (EnumFacing lvt_10_1_ : EnumFacing.values())
        {
            PathPoint lvt_11_1_ = this.getSafePoint(entityIn, currentPoint.xCoord + lvt_10_1_.getFrontOffsetX(), currentPoint.yCoord + lvt_10_1_.getFrontOffsetY(), currentPoint.zCoord + lvt_10_1_.getFrontOffsetZ());

            if (lvt_11_1_ != null && !lvt_11_1_.visited && lvt_11_1_.distanceTo(targetPoint) < maxDistance)
            {
                pathOptions[lvt_6_1_++] = lvt_11_1_;
            }
        }

        return lvt_6_1_;
    }

    /**
     * Returns a point that the entity can safely move to
     */
    private PathPoint getSafePoint(Entity entityIn, int x, int y, int z)
    {
        int lvt_5_1_ = this.func_176186_b(entityIn, x, y, z);
        return lvt_5_1_ == -1 ? this.openPoint(x, y, z) : null;
    }

    private int func_176186_b(Entity entityIn, int x, int y, int z)
    {
        BlockPos.MutableBlockPos lvt_5_1_ = new BlockPos.MutableBlockPos();

        for (int lvt_6_1_ = x; lvt_6_1_ < x + this.entitySizeX; ++lvt_6_1_)
        {
            for (int lvt_7_1_ = y; lvt_7_1_ < y + this.entitySizeY; ++lvt_7_1_)
            {
                for (int lvt_8_1_ = z; lvt_8_1_ < z + this.entitySizeZ; ++lvt_8_1_)
                {
                    Block lvt_9_1_ = this.blockaccess.getBlockState(lvt_5_1_.set(lvt_6_1_, lvt_7_1_, lvt_8_1_)).getBlock();

                    if (lvt_9_1_.getMaterial() != Material.water)
                    {
                        return 0;
                    }
                }
            }
        }

        return -1;
    }
}
