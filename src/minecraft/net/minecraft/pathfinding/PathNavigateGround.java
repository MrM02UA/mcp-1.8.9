package net.minecraft.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.pathfinder.WalkNodeProcessor;

public class PathNavigateGround extends PathNavigate
{
    protected WalkNodeProcessor nodeProcessor;
    private boolean shouldAvoidSun;

    public PathNavigateGround(EntityLiving entitylivingIn, World worldIn)
    {
        super(entitylivingIn, worldIn);
    }

    protected PathFinder getPathFinder()
    {
        this.nodeProcessor = new WalkNodeProcessor();
        this.nodeProcessor.setEnterDoors(true);
        return new PathFinder(this.nodeProcessor);
    }

    /**
     * If on ground or swimming and can swim
     */
    protected boolean canNavigate()
    {
        return this.theEntity.onGround || this.getCanSwim() && this.isInLiquid() || this.theEntity.isRiding() && this.theEntity instanceof EntityZombie && this.theEntity.ridingEntity instanceof EntityChicken;
    }

    protected Vec3 getEntityPosition()
    {
        return new Vec3(this.theEntity.posX, (double)this.getPathablePosY(), this.theEntity.posZ);
    }

    /**
     * Gets the safe pathing Y position for the entity depending on if it can path swim or not
     */
    private int getPathablePosY()
    {
        if (this.theEntity.isInWater() && this.getCanSwim())
        {
            int lvt_1_1_ = (int)this.theEntity.getEntityBoundingBox().minY;
            Block lvt_2_1_ = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.theEntity.posX), lvt_1_1_, MathHelper.floor_double(this.theEntity.posZ))).getBlock();
            int lvt_3_1_ = 0;

            while (lvt_2_1_ == Blocks.flowing_water || lvt_2_1_ == Blocks.water)
            {
                ++lvt_1_1_;
                lvt_2_1_ = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.theEntity.posX), lvt_1_1_, MathHelper.floor_double(this.theEntity.posZ))).getBlock();
                ++lvt_3_1_;

                if (lvt_3_1_ > 16)
                {
                    return (int)this.theEntity.getEntityBoundingBox().minY;
                }
            }

            return lvt_1_1_;
        }
        else
        {
            return (int)(this.theEntity.getEntityBoundingBox().minY + 0.5D);
        }
    }

    /**
     * Trims path data from the end to the first sun covered block
     */
    protected void removeSunnyPath()
    {
        super.removeSunnyPath();

        if (this.shouldAvoidSun)
        {
            if (this.worldObj.canSeeSky(new BlockPos(MathHelper.floor_double(this.theEntity.posX), (int)(this.theEntity.getEntityBoundingBox().minY + 0.5D), MathHelper.floor_double(this.theEntity.posZ))))
            {
                return;
            }

            for (int lvt_1_1_ = 0; lvt_1_1_ < this.currentPath.getCurrentPathLength(); ++lvt_1_1_)
            {
                PathPoint lvt_2_1_ = this.currentPath.getPathPointFromIndex(lvt_1_1_);

                if (this.worldObj.canSeeSky(new BlockPos(lvt_2_1_.xCoord, lvt_2_1_.yCoord, lvt_2_1_.zCoord)))
                {
                    this.currentPath.setCurrentPathLength(lvt_1_1_ - 1);
                    return;
                }
            }
        }
    }

    /**
     * Returns true when an entity of specified size could safely walk in a straight line between the two points. Args:
     * pos1, pos2, entityXSize, entityYSize, entityZSize
     */
    protected boolean isDirectPathBetweenPoints(Vec3 posVec31, Vec3 posVec32, int sizeX, int sizeY, int sizeZ)
    {
        int lvt_6_1_ = MathHelper.floor_double(posVec31.xCoord);
        int lvt_7_1_ = MathHelper.floor_double(posVec31.zCoord);
        double lvt_8_1_ = posVec32.xCoord - posVec31.xCoord;
        double lvt_10_1_ = posVec32.zCoord - posVec31.zCoord;
        double lvt_12_1_ = lvt_8_1_ * lvt_8_1_ + lvt_10_1_ * lvt_10_1_;

        if (lvt_12_1_ < 1.0E-8D)
        {
            return false;
        }
        else
        {
            double lvt_14_1_ = 1.0D / Math.sqrt(lvt_12_1_);
            lvt_8_1_ = lvt_8_1_ * lvt_14_1_;
            lvt_10_1_ = lvt_10_1_ * lvt_14_1_;
            sizeX = sizeX + 2;
            sizeZ = sizeZ + 2;

            if (!this.isSafeToStandAt(lvt_6_1_, (int)posVec31.yCoord, lvt_7_1_, sizeX, sizeY, sizeZ, posVec31, lvt_8_1_, lvt_10_1_))
            {
                return false;
            }
            else
            {
                sizeX = sizeX - 2;
                sizeZ = sizeZ - 2;
                double lvt_16_1_ = 1.0D / Math.abs(lvt_8_1_);
                double lvt_18_1_ = 1.0D / Math.abs(lvt_10_1_);
                double lvt_20_1_ = (double)(lvt_6_1_ * 1) - posVec31.xCoord;
                double lvt_22_1_ = (double)(lvt_7_1_ * 1) - posVec31.zCoord;

                if (lvt_8_1_ >= 0.0D)
                {
                    ++lvt_20_1_;
                }

                if (lvt_10_1_ >= 0.0D)
                {
                    ++lvt_22_1_;
                }

                lvt_20_1_ = lvt_20_1_ / lvt_8_1_;
                lvt_22_1_ = lvt_22_1_ / lvt_10_1_;
                int lvt_24_1_ = lvt_8_1_ < 0.0D ? -1 : 1;
                int lvt_25_1_ = lvt_10_1_ < 0.0D ? -1 : 1;
                int lvt_26_1_ = MathHelper.floor_double(posVec32.xCoord);
                int lvt_27_1_ = MathHelper.floor_double(posVec32.zCoord);
                int lvt_28_1_ = lvt_26_1_ - lvt_6_1_;
                int lvt_29_1_ = lvt_27_1_ - lvt_7_1_;

                while (lvt_28_1_ * lvt_24_1_ > 0 || lvt_29_1_ * lvt_25_1_ > 0)
                {
                    if (lvt_20_1_ < lvt_22_1_)
                    {
                        lvt_20_1_ += lvt_16_1_;
                        lvt_6_1_ += lvt_24_1_;
                        lvt_28_1_ = lvt_26_1_ - lvt_6_1_;
                    }
                    else
                    {
                        lvt_22_1_ += lvt_18_1_;
                        lvt_7_1_ += lvt_25_1_;
                        lvt_29_1_ = lvt_27_1_ - lvt_7_1_;
                    }

                    if (!this.isSafeToStandAt(lvt_6_1_, (int)posVec31.yCoord, lvt_7_1_, sizeX, sizeY, sizeZ, posVec31, lvt_8_1_, lvt_10_1_))
                    {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    /**
     * Returns true when an entity could stand at a position, including solid blocks under the entire entity.
     */
    private boolean isSafeToStandAt(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vec3 vec31, double p_179683_8_, double p_179683_10_)
    {
        int lvt_12_1_ = x - sizeX / 2;
        int lvt_13_1_ = z - sizeZ / 2;

        if (!this.isPositionClear(lvt_12_1_, y, lvt_13_1_, sizeX, sizeY, sizeZ, vec31, p_179683_8_, p_179683_10_))
        {
            return false;
        }
        else
        {
            for (int lvt_14_1_ = lvt_12_1_; lvt_14_1_ < lvt_12_1_ + sizeX; ++lvt_14_1_)
            {
                for (int lvt_15_1_ = lvt_13_1_; lvt_15_1_ < lvt_13_1_ + sizeZ; ++lvt_15_1_)
                {
                    double lvt_16_1_ = (double)lvt_14_1_ + 0.5D - vec31.xCoord;
                    double lvt_18_1_ = (double)lvt_15_1_ + 0.5D - vec31.zCoord;

                    if (lvt_16_1_ * p_179683_8_ + lvt_18_1_ * p_179683_10_ >= 0.0D)
                    {
                        Block lvt_20_1_ = this.worldObj.getBlockState(new BlockPos(lvt_14_1_, y - 1, lvt_15_1_)).getBlock();
                        Material lvt_21_1_ = lvt_20_1_.getMaterial();

                        if (lvt_21_1_ == Material.air)
                        {
                            return false;
                        }

                        if (lvt_21_1_ == Material.water && !this.theEntity.isInWater())
                        {
                            return false;
                        }

                        if (lvt_21_1_ == Material.lava)
                        {
                            return false;
                        }
                    }
                }
            }

            return true;
        }
    }

    /**
     * Returns true if an entity does not collide with any solid blocks at the position.
     */
    private boolean isPositionClear(int p_179692_1_, int p_179692_2_, int p_179692_3_, int p_179692_4_, int p_179692_5_, int p_179692_6_, Vec3 p_179692_7_, double p_179692_8_, double p_179692_10_)
    {
        for (BlockPos lvt_13_1_ : BlockPos.getAllInBox(new BlockPos(p_179692_1_, p_179692_2_, p_179692_3_), new BlockPos(p_179692_1_ + p_179692_4_ - 1, p_179692_2_ + p_179692_5_ - 1, p_179692_3_ + p_179692_6_ - 1)))
        {
            double lvt_14_1_ = (double)lvt_13_1_.getX() + 0.5D - p_179692_7_.xCoord;
            double lvt_16_1_ = (double)lvt_13_1_.getZ() + 0.5D - p_179692_7_.zCoord;

            if (lvt_14_1_ * p_179692_8_ + lvt_16_1_ * p_179692_10_ >= 0.0D)
            {
                Block lvt_18_1_ = this.worldObj.getBlockState(lvt_13_1_).getBlock();

                if (!lvt_18_1_.isPassable(this.worldObj, lvt_13_1_))
                {
                    return false;
                }
            }
        }

        return true;
    }

    public void setAvoidsWater(boolean avoidsWater)
    {
        this.nodeProcessor.setAvoidsWater(avoidsWater);
    }

    public boolean getAvoidsWater()
    {
        return this.nodeProcessor.getAvoidsWater();
    }

    public void setBreakDoors(boolean canBreakDoors)
    {
        this.nodeProcessor.setBreakDoors(canBreakDoors);
    }

    public void setEnterDoors(boolean par1)
    {
        this.nodeProcessor.setEnterDoors(par1);
    }

    public boolean getEnterDoors()
    {
        return this.nodeProcessor.getEnterDoors();
    }

    public void setCanSwim(boolean canSwim)
    {
        this.nodeProcessor.setCanSwim(canSwim);
    }

    public boolean getCanSwim()
    {
        return this.nodeProcessor.getCanSwim();
    }

    public void setAvoidSun(boolean par1)
    {
        this.shouldAvoidSun = par1;
    }
}
