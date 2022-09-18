package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;

public class Teleporter
{
    private final WorldServer worldServerInstance;

    /** A private Random() function in Teleporter */
    private final Random random;
    private final LongHashMap<Teleporter.PortalPosition> destinationCoordinateCache = new LongHashMap();
    private final List<Long> destinationCoordinateKeys = Lists.newArrayList();

    public Teleporter(WorldServer worldIn)
    {
        this.worldServerInstance = worldIn;
        this.random = new Random(worldIn.getSeed());
    }

    public void placeInPortal(Entity entityIn, float rotationYaw)
    {
        if (this.worldServerInstance.provider.getDimensionId() != 1)
        {
            if (!this.placeInExistingPortal(entityIn, rotationYaw))
            {
                this.makePortal(entityIn);
                this.placeInExistingPortal(entityIn, rotationYaw);
            }
        }
        else
        {
            int lvt_3_1_ = MathHelper.floor_double(entityIn.posX);
            int lvt_4_1_ = MathHelper.floor_double(entityIn.posY) - 1;
            int lvt_5_1_ = MathHelper.floor_double(entityIn.posZ);
            int lvt_6_1_ = 1;
            int lvt_7_1_ = 0;

            for (int lvt_8_1_ = -2; lvt_8_1_ <= 2; ++lvt_8_1_)
            {
                for (int lvt_9_1_ = -2; lvt_9_1_ <= 2; ++lvt_9_1_)
                {
                    for (int lvt_10_1_ = -1; lvt_10_1_ < 3; ++lvt_10_1_)
                    {
                        int lvt_11_1_ = lvt_3_1_ + lvt_9_1_ * lvt_6_1_ + lvt_8_1_ * lvt_7_1_;
                        int lvt_12_1_ = lvt_4_1_ + lvt_10_1_;
                        int lvt_13_1_ = lvt_5_1_ + lvt_9_1_ * lvt_7_1_ - lvt_8_1_ * lvt_6_1_;
                        boolean lvt_14_1_ = lvt_10_1_ < 0;
                        this.worldServerInstance.setBlockState(new BlockPos(lvt_11_1_, lvt_12_1_, lvt_13_1_), lvt_14_1_ ? Blocks.obsidian.getDefaultState() : Blocks.air.getDefaultState());
                    }
                }
            }

            entityIn.setLocationAndAngles((double)lvt_3_1_, (double)lvt_4_1_, (double)lvt_5_1_, entityIn.rotationYaw, 0.0F);
            entityIn.motionX = entityIn.motionY = entityIn.motionZ = 0.0D;
        }
    }

    public boolean placeInExistingPortal(Entity entityIn, float rotationYaw)
    {
        int lvt_3_1_ = 128;
        double lvt_4_1_ = -1.0D;
        int lvt_6_1_ = MathHelper.floor_double(entityIn.posX);
        int lvt_7_1_ = MathHelper.floor_double(entityIn.posZ);
        boolean lvt_8_1_ = true;
        BlockPos lvt_9_1_ = BlockPos.ORIGIN;
        long lvt_10_1_ = ChunkCoordIntPair.chunkXZ2Int(lvt_6_1_, lvt_7_1_);

        if (this.destinationCoordinateCache.containsItem(lvt_10_1_))
        {
            Teleporter.PortalPosition lvt_12_1_ = (Teleporter.PortalPosition)this.destinationCoordinateCache.getValueByKey(lvt_10_1_);
            lvt_4_1_ = 0.0D;
            lvt_9_1_ = lvt_12_1_;
            lvt_12_1_.lastUpdateTime = this.worldServerInstance.getTotalWorldTime();
            lvt_8_1_ = false;
        }
        else
        {
            BlockPos lvt_12_2_ = new BlockPos(entityIn);

            for (int lvt_13_1_ = -128; lvt_13_1_ <= 128; ++lvt_13_1_)
            {
                BlockPos lvt_16_1_;

                for (int lvt_14_1_ = -128; lvt_14_1_ <= 128; ++lvt_14_1_)
                {
                    for (BlockPos lvt_15_1_ = lvt_12_2_.add(lvt_13_1_, this.worldServerInstance.getActualHeight() - 1 - lvt_12_2_.getY(), lvt_14_1_); lvt_15_1_.getY() >= 0; lvt_15_1_ = lvt_16_1_)
                    {
                        lvt_16_1_ = lvt_15_1_.down();

                        if (this.worldServerInstance.getBlockState(lvt_15_1_).getBlock() == Blocks.portal)
                        {
                            while (this.worldServerInstance.getBlockState(lvt_16_1_ = lvt_15_1_.down()).getBlock() == Blocks.portal)
                            {
                                lvt_15_1_ = lvt_16_1_;
                            }

                            double lvt_17_1_ = lvt_15_1_.distanceSq(lvt_12_2_);

                            if (lvt_4_1_ < 0.0D || lvt_17_1_ < lvt_4_1_)
                            {
                                lvt_4_1_ = lvt_17_1_;
                                lvt_9_1_ = lvt_15_1_;
                            }
                        }
                    }
                }
            }
        }

        if (lvt_4_1_ >= 0.0D)
        {
            if (lvt_8_1_)
            {
                this.destinationCoordinateCache.add(lvt_10_1_, new Teleporter.PortalPosition(lvt_9_1_, this.worldServerInstance.getTotalWorldTime()));
                this.destinationCoordinateKeys.add(Long.valueOf(lvt_10_1_));
            }

            double lvt_12_3_ = (double)lvt_9_1_.getX() + 0.5D;
            double lvt_14_2_ = (double)lvt_9_1_.getY() + 0.5D;
            double lvt_16_2_ = (double)lvt_9_1_.getZ() + 0.5D;
            BlockPattern.PatternHelper lvt_18_1_ = Blocks.portal.func_181089_f(this.worldServerInstance, lvt_9_1_);
            boolean lvt_19_1_ = lvt_18_1_.getFinger().rotateY().getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE;
            double lvt_20_1_ = lvt_18_1_.getFinger().getAxis() == EnumFacing.Axis.X ? (double)lvt_18_1_.getPos().getZ() : (double)lvt_18_1_.getPos().getX();
            lvt_14_2_ = (double)(lvt_18_1_.getPos().getY() + 1) - entityIn.func_181014_aG().yCoord * (double)lvt_18_1_.func_181119_e();

            if (lvt_19_1_)
            {
                ++lvt_20_1_;
            }

            if (lvt_18_1_.getFinger().getAxis() == EnumFacing.Axis.X)
            {
                lvt_16_2_ = lvt_20_1_ + (1.0D - entityIn.func_181014_aG().xCoord) * (double)lvt_18_1_.func_181118_d() * (double)lvt_18_1_.getFinger().rotateY().getAxisDirection().getOffset();
            }
            else
            {
                lvt_12_3_ = lvt_20_1_ + (1.0D - entityIn.func_181014_aG().xCoord) * (double)lvt_18_1_.func_181118_d() * (double)lvt_18_1_.getFinger().rotateY().getAxisDirection().getOffset();
            }

            float lvt_22_1_ = 0.0F;
            float lvt_23_1_ = 0.0F;
            float lvt_24_1_ = 0.0F;
            float lvt_25_1_ = 0.0F;

            if (lvt_18_1_.getFinger().getOpposite() == entityIn.getTeleportDirection())
            {
                lvt_22_1_ = 1.0F;
                lvt_23_1_ = 1.0F;
            }
            else if (lvt_18_1_.getFinger().getOpposite() == entityIn.getTeleportDirection().getOpposite())
            {
                lvt_22_1_ = -1.0F;
                lvt_23_1_ = -1.0F;
            }
            else if (lvt_18_1_.getFinger().getOpposite() == entityIn.getTeleportDirection().rotateY())
            {
                lvt_24_1_ = 1.0F;
                lvt_25_1_ = -1.0F;
            }
            else
            {
                lvt_24_1_ = -1.0F;
                lvt_25_1_ = 1.0F;
            }

            double lvt_26_1_ = entityIn.motionX;
            double lvt_28_1_ = entityIn.motionZ;
            entityIn.motionX = lvt_26_1_ * (double)lvt_22_1_ + lvt_28_1_ * (double)lvt_25_1_;
            entityIn.motionZ = lvt_26_1_ * (double)lvt_24_1_ + lvt_28_1_ * (double)lvt_23_1_;
            entityIn.rotationYaw = rotationYaw - (float)(entityIn.getTeleportDirection().getOpposite().getHorizontalIndex() * 90) + (float)(lvt_18_1_.getFinger().getHorizontalIndex() * 90);
            entityIn.setLocationAndAngles(lvt_12_3_, lvt_14_2_, lvt_16_2_, entityIn.rotationYaw, entityIn.rotationPitch);
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean makePortal(Entity entityIn)
    {
        int lvt_2_1_ = 16;
        double lvt_3_1_ = -1.0D;
        int lvt_5_1_ = MathHelper.floor_double(entityIn.posX);
        int lvt_6_1_ = MathHelper.floor_double(entityIn.posY);
        int lvt_7_1_ = MathHelper.floor_double(entityIn.posZ);
        int lvt_8_1_ = lvt_5_1_;
        int lvt_9_1_ = lvt_6_1_;
        int lvt_10_1_ = lvt_7_1_;
        int lvt_11_1_ = 0;
        int lvt_12_1_ = this.random.nextInt(4);
        BlockPos.MutableBlockPos lvt_13_1_ = new BlockPos.MutableBlockPos();

        for (int lvt_14_1_ = lvt_5_1_ - lvt_2_1_; lvt_14_1_ <= lvt_5_1_ + lvt_2_1_; ++lvt_14_1_)
        {
            double lvt_15_1_ = (double)lvt_14_1_ + 0.5D - entityIn.posX;

            for (int lvt_17_1_ = lvt_7_1_ - lvt_2_1_; lvt_17_1_ <= lvt_7_1_ + lvt_2_1_; ++lvt_17_1_)
            {
                double lvt_18_1_ = (double)lvt_17_1_ + 0.5D - entityIn.posZ;
                label142:

                for (int lvt_20_1_ = this.worldServerInstance.getActualHeight() - 1; lvt_20_1_ >= 0; --lvt_20_1_)
                {
                    if (this.worldServerInstance.isAirBlock(lvt_13_1_.set(lvt_14_1_, lvt_20_1_, lvt_17_1_)))
                    {
                        while (lvt_20_1_ > 0 && this.worldServerInstance.isAirBlock(lvt_13_1_.set(lvt_14_1_, lvt_20_1_ - 1, lvt_17_1_)))
                        {
                            --lvt_20_1_;
                        }

                        for (int lvt_21_1_ = lvt_12_1_; lvt_21_1_ < lvt_12_1_ + 4; ++lvt_21_1_)
                        {
                            int lvt_22_1_ = lvt_21_1_ % 2;
                            int lvt_23_1_ = 1 - lvt_22_1_;

                            if (lvt_21_1_ % 4 >= 2)
                            {
                                lvt_22_1_ = -lvt_22_1_;
                                lvt_23_1_ = -lvt_23_1_;
                            }

                            for (int lvt_24_1_ = 0; lvt_24_1_ < 3; ++lvt_24_1_)
                            {
                                for (int lvt_25_1_ = 0; lvt_25_1_ < 4; ++lvt_25_1_)
                                {
                                    for (int lvt_26_1_ = -1; lvt_26_1_ < 4; ++lvt_26_1_)
                                    {
                                        int lvt_27_1_ = lvt_14_1_ + (lvt_25_1_ - 1) * lvt_22_1_ + lvt_24_1_ * lvt_23_1_;
                                        int lvt_28_1_ = lvt_20_1_ + lvt_26_1_;
                                        int lvt_29_1_ = lvt_17_1_ + (lvt_25_1_ - 1) * lvt_23_1_ - lvt_24_1_ * lvt_22_1_;
                                        lvt_13_1_.set(lvt_27_1_, lvt_28_1_, lvt_29_1_);

                                        if (lvt_26_1_ < 0 && !this.worldServerInstance.getBlockState(lvt_13_1_).getBlock().getMaterial().isSolid() || lvt_26_1_ >= 0 && !this.worldServerInstance.isAirBlock(lvt_13_1_))
                                        {
                                            continue label142;
                                        }
                                    }
                                }
                            }

                            double lvt_24_2_ = (double)lvt_20_1_ + 0.5D - entityIn.posY;
                            double lvt_26_2_ = lvt_15_1_ * lvt_15_1_ + lvt_24_2_ * lvt_24_2_ + lvt_18_1_ * lvt_18_1_;

                            if (lvt_3_1_ < 0.0D || lvt_26_2_ < lvt_3_1_)
                            {
                                lvt_3_1_ = lvt_26_2_;
                                lvt_8_1_ = lvt_14_1_;
                                lvt_9_1_ = lvt_20_1_;
                                lvt_10_1_ = lvt_17_1_;
                                lvt_11_1_ = lvt_21_1_ % 4;
                            }
                        }
                    }
                }
            }
        }

        if (lvt_3_1_ < 0.0D)
        {
            for (int lvt_14_2_ = lvt_5_1_ - lvt_2_1_; lvt_14_2_ <= lvt_5_1_ + lvt_2_1_; ++lvt_14_2_)
            {
                double lvt_15_2_ = (double)lvt_14_2_ + 0.5D - entityIn.posX;

                for (int lvt_17_2_ = lvt_7_1_ - lvt_2_1_; lvt_17_2_ <= lvt_7_1_ + lvt_2_1_; ++lvt_17_2_)
                {
                    double lvt_18_2_ = (double)lvt_17_2_ + 0.5D - entityIn.posZ;
                    label562:

                    for (int lvt_20_2_ = this.worldServerInstance.getActualHeight() - 1; lvt_20_2_ >= 0; --lvt_20_2_)
                    {
                        if (this.worldServerInstance.isAirBlock(lvt_13_1_.set(lvt_14_2_, lvt_20_2_, lvt_17_2_)))
                        {
                            while (lvt_20_2_ > 0 && this.worldServerInstance.isAirBlock(lvt_13_1_.set(lvt_14_2_, lvt_20_2_ - 1, lvt_17_2_)))
                            {
                                --lvt_20_2_;
                            }

                            for (int lvt_21_2_ = lvt_12_1_; lvt_21_2_ < lvt_12_1_ + 2; ++lvt_21_2_)
                            {
                                int lvt_22_2_ = lvt_21_2_ % 2;
                                int lvt_23_2_ = 1 - lvt_22_2_;

                                for (int lvt_24_3_ = 0; lvt_24_3_ < 4; ++lvt_24_3_)
                                {
                                    for (int lvt_25_2_ = -1; lvt_25_2_ < 4; ++lvt_25_2_)
                                    {
                                        int lvt_26_3_ = lvt_14_2_ + (lvt_24_3_ - 1) * lvt_22_2_;
                                        int lvt_27_2_ = lvt_20_2_ + lvt_25_2_;
                                        int lvt_28_2_ = lvt_17_2_ + (lvt_24_3_ - 1) * lvt_23_2_;
                                        lvt_13_1_.set(lvt_26_3_, lvt_27_2_, lvt_28_2_);

                                        if (lvt_25_2_ < 0 && !this.worldServerInstance.getBlockState(lvt_13_1_).getBlock().getMaterial().isSolid() || lvt_25_2_ >= 0 && !this.worldServerInstance.isAirBlock(lvt_13_1_))
                                        {
                                            continue label562;
                                        }
                                    }
                                }

                                double lvt_24_4_ = (double)lvt_20_2_ + 0.5D - entityIn.posY;
                                double lvt_26_4_ = lvt_15_2_ * lvt_15_2_ + lvt_24_4_ * lvt_24_4_ + lvt_18_2_ * lvt_18_2_;

                                if (lvt_3_1_ < 0.0D || lvt_26_4_ < lvt_3_1_)
                                {
                                    lvt_3_1_ = lvt_26_4_;
                                    lvt_8_1_ = lvt_14_2_;
                                    lvt_9_1_ = lvt_20_2_;
                                    lvt_10_1_ = lvt_17_2_;
                                    lvt_11_1_ = lvt_21_2_ % 2;
                                }
                            }
                        }
                    }
                }
            }
        }

        int lvt_15_3_ = lvt_8_1_;
        int lvt_16_1_ = lvt_9_1_;
        int lvt_17_3_ = lvt_10_1_;
        int lvt_18_3_ = lvt_11_1_ % 2;
        int lvt_19_1_ = 1 - lvt_18_3_;

        if (lvt_11_1_ % 4 >= 2)
        {
            lvt_18_3_ = -lvt_18_3_;
            lvt_19_1_ = -lvt_19_1_;
        }

        if (lvt_3_1_ < 0.0D)
        {
            lvt_9_1_ = MathHelper.clamp_int(lvt_9_1_, 70, this.worldServerInstance.getActualHeight() - 10);
            lvt_16_1_ = lvt_9_1_;

            for (int lvt_20_3_ = -1; lvt_20_3_ <= 1; ++lvt_20_3_)
            {
                for (int lvt_21_3_ = 1; lvt_21_3_ < 3; ++lvt_21_3_)
                {
                    for (int lvt_22_3_ = -1; lvt_22_3_ < 3; ++lvt_22_3_)
                    {
                        int lvt_23_3_ = lvt_15_3_ + (lvt_21_3_ - 1) * lvt_18_3_ + lvt_20_3_ * lvt_19_1_;
                        int lvt_24_5_ = lvt_16_1_ + lvt_22_3_;
                        int lvt_25_3_ = lvt_17_3_ + (lvt_21_3_ - 1) * lvt_19_1_ - lvt_20_3_ * lvt_18_3_;
                        boolean lvt_26_5_ = lvt_22_3_ < 0;
                        this.worldServerInstance.setBlockState(new BlockPos(lvt_23_3_, lvt_24_5_, lvt_25_3_), lvt_26_5_ ? Blocks.obsidian.getDefaultState() : Blocks.air.getDefaultState());
                    }
                }
            }
        }

        IBlockState lvt_20_4_ = Blocks.portal.getDefaultState().withProperty(BlockPortal.AXIS, lvt_18_3_ != 0 ? EnumFacing.Axis.X : EnumFacing.Axis.Z);

        for (int lvt_21_4_ = 0; lvt_21_4_ < 4; ++lvt_21_4_)
        {
            for (int lvt_22_4_ = 0; lvt_22_4_ < 4; ++lvt_22_4_)
            {
                for (int lvt_23_4_ = -1; lvt_23_4_ < 4; ++lvt_23_4_)
                {
                    int lvt_24_6_ = lvt_15_3_ + (lvt_22_4_ - 1) * lvt_18_3_;
                    int lvt_25_4_ = lvt_16_1_ + lvt_23_4_;
                    int lvt_26_6_ = lvt_17_3_ + (lvt_22_4_ - 1) * lvt_19_1_;
                    boolean lvt_27_3_ = lvt_22_4_ == 0 || lvt_22_4_ == 3 || lvt_23_4_ == -1 || lvt_23_4_ == 3;
                    this.worldServerInstance.setBlockState(new BlockPos(lvt_24_6_, lvt_25_4_, lvt_26_6_), lvt_27_3_ ? Blocks.obsidian.getDefaultState() : lvt_20_4_, 2);
                }
            }

            for (int lvt_22_5_ = 0; lvt_22_5_ < 4; ++lvt_22_5_)
            {
                for (int lvt_23_5_ = -1; lvt_23_5_ < 4; ++lvt_23_5_)
                {
                    int lvt_24_7_ = lvt_15_3_ + (lvt_22_5_ - 1) * lvt_18_3_;
                    int lvt_25_5_ = lvt_16_1_ + lvt_23_5_;
                    int lvt_26_7_ = lvt_17_3_ + (lvt_22_5_ - 1) * lvt_19_1_;
                    BlockPos lvt_27_4_ = new BlockPos(lvt_24_7_, lvt_25_5_, lvt_26_7_);
                    this.worldServerInstance.notifyNeighborsOfStateChange(lvt_27_4_, this.worldServerInstance.getBlockState(lvt_27_4_).getBlock());
                }
            }
        }

        return true;
    }

    /**
     * called periodically to remove out-of-date portal locations from the cache list. Argument par1 is a
     * WorldServer.getTotalWorldTime() value.
     */
    public void removeStalePortalLocations(long worldTime)
    {
        if (worldTime % 100L == 0L)
        {
            Iterator<Long> lvt_3_1_ = this.destinationCoordinateKeys.iterator();
            long lvt_4_1_ = worldTime - 300L;

            while (lvt_3_1_.hasNext())
            {
                Long lvt_6_1_ = (Long)lvt_3_1_.next();
                Teleporter.PortalPosition lvt_7_1_ = (Teleporter.PortalPosition)this.destinationCoordinateCache.getValueByKey(lvt_6_1_.longValue());

                if (lvt_7_1_ == null || lvt_7_1_.lastUpdateTime < lvt_4_1_)
                {
                    lvt_3_1_.remove();
                    this.destinationCoordinateCache.remove(lvt_6_1_.longValue());
                }
            }
        }
    }

    public class PortalPosition extends BlockPos
    {
        public long lastUpdateTime;

        public PortalPosition(BlockPos pos, long lastUpdate)
        {
            super(pos.getX(), pos.getY(), pos.getZ());
            this.lastUpdateTime = lastUpdate;
        }
    }
}
