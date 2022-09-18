package net.minecraft.world.pathfinder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public class WalkNodeProcessor extends NodeProcessor
{
    private boolean canEnterDoors;
    private boolean canBreakDoors;
    private boolean avoidsWater;
    private boolean canSwim;
    private boolean shouldAvoidWater;

    public void initProcessor(IBlockAccess iblockaccessIn, Entity entityIn)
    {
        super.initProcessor(iblockaccessIn, entityIn);
        this.shouldAvoidWater = this.avoidsWater;
    }

    /**
     * This method is called when all nodes have been processed and PathEntity is created.
     *  {@link net.minecraft.world.pathfinder.WalkNodeProcessor WalkNodeProcessor} uses this to change its field {@link
     * net.minecraft.world.pathfinder.WalkNodeProcessor#avoidsWater avoidsWater}
     */
    public void postProcess()
    {
        super.postProcess();
        this.avoidsWater = this.shouldAvoidWater;
    }

    /**
     * Returns given entity's position as PathPoint
     */
    public PathPoint getPathPointTo(Entity entityIn)
    {
        int lvt_2_1_;

        if (this.canSwim && entityIn.isInWater())
        {
            lvt_2_1_ = (int)entityIn.getEntityBoundingBox().minY;
            BlockPos.MutableBlockPos lvt_3_1_ = new BlockPos.MutableBlockPos(MathHelper.floor_double(entityIn.posX), lvt_2_1_, MathHelper.floor_double(entityIn.posZ));

            for (Block lvt_4_1_ = this.blockaccess.getBlockState(lvt_3_1_).getBlock(); lvt_4_1_ == Blocks.flowing_water || lvt_4_1_ == Blocks.water; lvt_4_1_ = this.blockaccess.getBlockState(lvt_3_1_).getBlock())
            {
                ++lvt_2_1_;
                lvt_3_1_.set(MathHelper.floor_double(entityIn.posX), lvt_2_1_, MathHelper.floor_double(entityIn.posZ));
            }

            this.avoidsWater = false;
        }
        else
        {
            lvt_2_1_ = MathHelper.floor_double(entityIn.getEntityBoundingBox().minY + 0.5D);
        }

        return this.openPoint(MathHelper.floor_double(entityIn.getEntityBoundingBox().minX), lvt_2_1_, MathHelper.floor_double(entityIn.getEntityBoundingBox().minZ));
    }

    /**
     * Returns PathPoint for given coordinates
     */
    public PathPoint getPathPointToCoords(Entity entityIn, double x, double y, double target)
    {
        return this.openPoint(MathHelper.floor_double(x - (double)(entityIn.width / 2.0F)), MathHelper.floor_double(y), MathHelper.floor_double(target - (double)(entityIn.width / 2.0F)));
    }

    public int findPathOptions(PathPoint[] pathOptions, Entity entityIn, PathPoint currentPoint, PathPoint targetPoint, float maxDistance)
    {
        int lvt_6_1_ = 0;
        int lvt_7_1_ = 0;

        if (this.getVerticalOffset(entityIn, currentPoint.xCoord, currentPoint.yCoord + 1, currentPoint.zCoord) == 1)
        {
            lvt_7_1_ = 1;
        }

        PathPoint lvt_8_1_ = this.getSafePoint(entityIn, currentPoint.xCoord, currentPoint.yCoord, currentPoint.zCoord + 1, lvt_7_1_);
        PathPoint lvt_9_1_ = this.getSafePoint(entityIn, currentPoint.xCoord - 1, currentPoint.yCoord, currentPoint.zCoord, lvt_7_1_);
        PathPoint lvt_10_1_ = this.getSafePoint(entityIn, currentPoint.xCoord + 1, currentPoint.yCoord, currentPoint.zCoord, lvt_7_1_);
        PathPoint lvt_11_1_ = this.getSafePoint(entityIn, currentPoint.xCoord, currentPoint.yCoord, currentPoint.zCoord - 1, lvt_7_1_);

        if (lvt_8_1_ != null && !lvt_8_1_.visited && lvt_8_1_.distanceTo(targetPoint) < maxDistance)
        {
            pathOptions[lvt_6_1_++] = lvt_8_1_;
        }

        if (lvt_9_1_ != null && !lvt_9_1_.visited && lvt_9_1_.distanceTo(targetPoint) < maxDistance)
        {
            pathOptions[lvt_6_1_++] = lvt_9_1_;
        }

        if (lvt_10_1_ != null && !lvt_10_1_.visited && lvt_10_1_.distanceTo(targetPoint) < maxDistance)
        {
            pathOptions[lvt_6_1_++] = lvt_10_1_;
        }

        if (lvt_11_1_ != null && !lvt_11_1_.visited && lvt_11_1_.distanceTo(targetPoint) < maxDistance)
        {
            pathOptions[lvt_6_1_++] = lvt_11_1_;
        }

        return lvt_6_1_;
    }

    /**
     * Returns a point that the entity can safely move to
     */
    private PathPoint getSafePoint(Entity entityIn, int x, int y, int z, int p_176171_5_)
    {
        PathPoint lvt_6_1_ = null;
        int lvt_7_1_ = this.getVerticalOffset(entityIn, x, y, z);

        if (lvt_7_1_ == 2)
        {
            return this.openPoint(x, y, z);
        }
        else
        {
            if (lvt_7_1_ == 1)
            {
                lvt_6_1_ = this.openPoint(x, y, z);
            }

            if (lvt_6_1_ == null && p_176171_5_ > 0 && lvt_7_1_ != -3 && lvt_7_1_ != -4 && this.getVerticalOffset(entityIn, x, y + p_176171_5_, z) == 1)
            {
                lvt_6_1_ = this.openPoint(x, y + p_176171_5_, z);
                y += p_176171_5_;
            }

            if (lvt_6_1_ != null)
            {
                int lvt_8_1_ = 0;
                int lvt_9_1_;

                for (lvt_9_1_ = 0; y > 0; lvt_6_1_ = this.openPoint(x, y, z))
                {
                    lvt_9_1_ = this.getVerticalOffset(entityIn, x, y - 1, z);

                    if (this.avoidsWater && lvt_9_1_ == -1)
                    {
                        return null;
                    }

                    if (lvt_9_1_ != 1)
                    {
                        break;
                    }

                    if (lvt_8_1_++ >= entityIn.getMaxFallHeight())
                    {
                        return null;
                    }

                    --y;

                    if (y <= 0)
                    {
                        return null;
                    }
                }

                if (lvt_9_1_ == -2)
                {
                    return null;
                }
            }

            return lvt_6_1_;
        }
    }

    /**
     * Checks if an entity collides with blocks at a position.
     * Returns 1 if clear, 0 for colliding with any solid block, -1 for water(if avoids water),
     * -2 for lava, -3 for fence and wall, -4 for closed trapdoor, 2 if otherwise clear except for open trapdoor or
     * water(if not avoiding)
     */
    private int getVerticalOffset(Entity entityIn, int x, int y, int z)
    {
        return func_176170_a(this.blockaccess, entityIn, x, y, z, this.entitySizeX, this.entitySizeY, this.entitySizeZ, this.avoidsWater, this.canBreakDoors, this.canEnterDoors);
    }

    public static int func_176170_a(IBlockAccess blockaccessIn, Entity entityIn, int x, int y, int z, int sizeX, int sizeY, int sizeZ, boolean avoidWater, boolean breakDoors, boolean enterDoors)
    {
        boolean lvt_11_1_ = false;
        BlockPos lvt_12_1_ = new BlockPos(entityIn);
        BlockPos.MutableBlockPos lvt_13_1_ = new BlockPos.MutableBlockPos();

        for (int lvt_14_1_ = x; lvt_14_1_ < x + sizeX; ++lvt_14_1_)
        {
            for (int lvt_15_1_ = y; lvt_15_1_ < y + sizeY; ++lvt_15_1_)
            {
                for (int lvt_16_1_ = z; lvt_16_1_ < z + sizeZ; ++lvt_16_1_)
                {
                    lvt_13_1_.set(lvt_14_1_, lvt_15_1_, lvt_16_1_);
                    Block lvt_17_1_ = blockaccessIn.getBlockState(lvt_13_1_).getBlock();

                    if (lvt_17_1_.getMaterial() != Material.air)
                    {
                        if (lvt_17_1_ != Blocks.trapdoor && lvt_17_1_ != Blocks.iron_trapdoor)
                        {
                            if (lvt_17_1_ != Blocks.flowing_water && lvt_17_1_ != Blocks.water)
                            {
                                if (!enterDoors && lvt_17_1_ instanceof BlockDoor && lvt_17_1_.getMaterial() == Material.wood)
                                {
                                    return 0;
                                }
                            }
                            else
                            {
                                if (avoidWater)
                                {
                                    return -1;
                                }

                                lvt_11_1_ = true;
                            }
                        }
                        else
                        {
                            lvt_11_1_ = true;
                        }

                        if (entityIn.worldObj.getBlockState(lvt_13_1_).getBlock() instanceof BlockRailBase)
                        {
                            if (!(entityIn.worldObj.getBlockState(lvt_12_1_).getBlock() instanceof BlockRailBase) && !(entityIn.worldObj.getBlockState(lvt_12_1_.down()).getBlock() instanceof BlockRailBase))
                            {
                                return -3;
                            }
                        }
                        else if (!lvt_17_1_.isPassable(blockaccessIn, lvt_13_1_) && (!breakDoors || !(lvt_17_1_ instanceof BlockDoor) || lvt_17_1_.getMaterial() != Material.wood))
                        {
                            if (lvt_17_1_ instanceof BlockFence || lvt_17_1_ instanceof BlockFenceGate || lvt_17_1_ instanceof BlockWall)
                            {
                                return -3;
                            }

                            if (lvt_17_1_ == Blocks.trapdoor || lvt_17_1_ == Blocks.iron_trapdoor)
                            {
                                return -4;
                            }

                            Material lvt_18_1_ = lvt_17_1_.getMaterial();

                            if (lvt_18_1_ != Material.lava)
                            {
                                return 0;
                            }

                            if (!entityIn.isInLava())
                            {
                                return -2;
                            }
                        }
                    }
                }
            }
        }

        return lvt_11_1_ ? 2 : 1;
    }

    public void setEnterDoors(boolean canEnterDoorsIn)
    {
        this.canEnterDoors = canEnterDoorsIn;
    }

    public void setBreakDoors(boolean canBreakDoorsIn)
    {
        this.canBreakDoors = canBreakDoorsIn;
    }

    public void setAvoidsWater(boolean avoidsWaterIn)
    {
        this.avoidsWater = avoidsWaterIn;
    }

    public void setCanSwim(boolean canSwimIn)
    {
        this.canSwim = canSwimIn;
    }

    public boolean getEnterDoors()
    {
        return this.canEnterDoors;
    }

    public boolean getCanSwim()
    {
        return this.canSwim;
    }

    public boolean getAvoidsWater()
    {
        return this.avoidsWater;
    }
}
