package net.minecraft.entity.ai;

import java.util.Random;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RandomPositionGenerator
{
    /**
     * used to store a driection when the user passes a point to move towards or away from. WARNING: NEVER THREAD SAFE.
     * MULTIPLE findTowards and findAway calls, will share this var
     */
    private static Vec3 staticVector = new Vec3(0.0D, 0.0D, 0.0D);

    /**
     * finds a random target within par1(x,z) and par2 (y) blocks
     */
    public static Vec3 findRandomTarget(EntityCreature entitycreatureIn, int xz, int y)
    {
        return findRandomTargetBlock(entitycreatureIn, xz, y, (Vec3)null);
    }

    /**
     * finds a random target within par1(x,z) and par2 (y) blocks in the direction of the point par3
     */
    public static Vec3 findRandomTargetBlockTowards(EntityCreature entitycreatureIn, int xz, int y, Vec3 targetVec3)
    {
        staticVector = targetVec3.subtract(entitycreatureIn.posX, entitycreatureIn.posY, entitycreatureIn.posZ);
        return findRandomTargetBlock(entitycreatureIn, xz, y, staticVector);
    }

    /**
     * finds a random target within par1(x,z) and par2 (y) blocks in the reverse direction of the point par3
     */
    public static Vec3 findRandomTargetBlockAwayFrom(EntityCreature entitycreatureIn, int xz, int y, Vec3 targetVec3)
    {
        staticVector = (new Vec3(entitycreatureIn.posX, entitycreatureIn.posY, entitycreatureIn.posZ)).subtract(targetVec3);
        return findRandomTargetBlock(entitycreatureIn, xz, y, staticVector);
    }

    /**
     * searches 10 blocks at random in a within par1(x,z) and par2 (y) distance, ignores those not in the direction of
     * par3Vec3, then points to the tile for which creature.getBlockPathWeight returns the highest number
     */
    private static Vec3 findRandomTargetBlock(EntityCreature entitycreatureIn, int xz, int y, Vec3 targetVec3)
    {
        Random lvt_4_1_ = entitycreatureIn.getRNG();
        boolean lvt_5_1_ = false;
        int lvt_6_1_ = 0;
        int lvt_7_1_ = 0;
        int lvt_8_1_ = 0;
        float lvt_9_1_ = -99999.0F;
        boolean lvt_10_1_;

        if (entitycreatureIn.hasHome())
        {
            double lvt_11_1_ = entitycreatureIn.getHomePosition().distanceSq((double)MathHelper.floor_double(entitycreatureIn.posX), (double)MathHelper.floor_double(entitycreatureIn.posY), (double)MathHelper.floor_double(entitycreatureIn.posZ)) + 4.0D;
            double lvt_13_1_ = (double)(entitycreatureIn.getMaximumHomeDistance() + (float)xz);
            lvt_10_1_ = lvt_11_1_ < lvt_13_1_ * lvt_13_1_;
        }
        else
        {
            lvt_10_1_ = false;
        }

        for (int lvt_11_2_ = 0; lvt_11_2_ < 10; ++lvt_11_2_)
        {
            int lvt_12_1_ = lvt_4_1_.nextInt(2 * xz + 1) - xz;
            int lvt_13_2_ = lvt_4_1_.nextInt(2 * y + 1) - y;
            int lvt_14_1_ = lvt_4_1_.nextInt(2 * xz + 1) - xz;

            if (targetVec3 == null || (double)lvt_12_1_ * targetVec3.xCoord + (double)lvt_14_1_ * targetVec3.zCoord >= 0.0D)
            {
                if (entitycreatureIn.hasHome() && xz > 1)
                {
                    BlockPos lvt_15_1_ = entitycreatureIn.getHomePosition();

                    if (entitycreatureIn.posX > (double)lvt_15_1_.getX())
                    {
                        lvt_12_1_ -= lvt_4_1_.nextInt(xz / 2);
                    }
                    else
                    {
                        lvt_12_1_ += lvt_4_1_.nextInt(xz / 2);
                    }

                    if (entitycreatureIn.posZ > (double)lvt_15_1_.getZ())
                    {
                        lvt_14_1_ -= lvt_4_1_.nextInt(xz / 2);
                    }
                    else
                    {
                        lvt_14_1_ += lvt_4_1_.nextInt(xz / 2);
                    }
                }

                lvt_12_1_ = lvt_12_1_ + MathHelper.floor_double(entitycreatureIn.posX);
                lvt_13_2_ = lvt_13_2_ + MathHelper.floor_double(entitycreatureIn.posY);
                lvt_14_1_ = lvt_14_1_ + MathHelper.floor_double(entitycreatureIn.posZ);
                BlockPos lvt_15_2_ = new BlockPos(lvt_12_1_, lvt_13_2_, lvt_14_1_);

                if (!lvt_10_1_ || entitycreatureIn.isWithinHomeDistanceFromPosition(lvt_15_2_))
                {
                    float lvt_16_1_ = entitycreatureIn.getBlockPathWeight(lvt_15_2_);

                    if (lvt_16_1_ > lvt_9_1_)
                    {
                        lvt_9_1_ = lvt_16_1_;
                        lvt_6_1_ = lvt_12_1_;
                        lvt_7_1_ = lvt_13_2_;
                        lvt_8_1_ = lvt_14_1_;
                        lvt_5_1_ = true;
                    }
                }
            }
        }

        if (lvt_5_1_)
        {
            return new Vec3((double)lvt_6_1_, (double)lvt_7_1_, (double)lvt_8_1_);
        }
        else
        {
            return null;
        }
    }
}
