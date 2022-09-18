package net.minecraft.world.border;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ChunkCoordIntPair;

public class WorldBorder
{
    private final List<IBorderListener> listeners = Lists.newArrayList();
    private double centerX = 0.0D;
    private double centerZ = 0.0D;
    private double startDiameter = 6.0E7D;
    private double endDiameter;
    private long endTime;
    private long startTime;
    private int worldSize;
    private double damageAmount;
    private double damageBuffer;
    private int warningTime;
    private int warningDistance;

    public WorldBorder()
    {
        this.endDiameter = this.startDiameter;
        this.worldSize = 29999984;
        this.damageAmount = 0.2D;
        this.damageBuffer = 5.0D;
        this.warningTime = 15;
        this.warningDistance = 5;
    }

    public boolean contains(BlockPos pos)
    {
        return (double)(pos.getX() + 1) > this.minX() && (double)pos.getX() < this.maxX() && (double)(pos.getZ() + 1) > this.minZ() && (double)pos.getZ() < this.maxZ();
    }

    public boolean contains(ChunkCoordIntPair range)
    {
        return (double)range.getXEnd() > this.minX() && (double)range.getXStart() < this.maxX() && (double)range.getZEnd() > this.minZ() && (double)range.getZStart() < this.maxZ();
    }

    public boolean contains(AxisAlignedBB bb)
    {
        return bb.maxX > this.minX() && bb.minX < this.maxX() && bb.maxZ > this.minZ() && bb.minZ < this.maxZ();
    }

    public double getClosestDistance(Entity entityIn)
    {
        return this.getClosestDistance(entityIn.posX, entityIn.posZ);
    }

    public double getClosestDistance(double x, double z)
    {
        double lvt_5_1_ = z - this.minZ();
        double lvt_7_1_ = this.maxZ() - z;
        double lvt_9_1_ = x - this.minX();
        double lvt_11_1_ = this.maxX() - x;
        double lvt_13_1_ = Math.min(lvt_9_1_, lvt_11_1_);
        lvt_13_1_ = Math.min(lvt_13_1_, lvt_5_1_);
        return Math.min(lvt_13_1_, lvt_7_1_);
    }

    public EnumBorderStatus getStatus()
    {
        return this.endDiameter < this.startDiameter ? EnumBorderStatus.SHRINKING : (this.endDiameter > this.startDiameter ? EnumBorderStatus.GROWING : EnumBorderStatus.STATIONARY);
    }

    public double minX()
    {
        double lvt_1_1_ = this.getCenterX() - this.getDiameter() / 2.0D;

        if (lvt_1_1_ < (double)(-this.worldSize))
        {
            lvt_1_1_ = (double)(-this.worldSize);
        }

        return lvt_1_1_;
    }

    public double minZ()
    {
        double lvt_1_1_ = this.getCenterZ() - this.getDiameter() / 2.0D;

        if (lvt_1_1_ < (double)(-this.worldSize))
        {
            lvt_1_1_ = (double)(-this.worldSize);
        }

        return lvt_1_1_;
    }

    public double maxX()
    {
        double lvt_1_1_ = this.getCenterX() + this.getDiameter() / 2.0D;

        if (lvt_1_1_ > (double)this.worldSize)
        {
            lvt_1_1_ = (double)this.worldSize;
        }

        return lvt_1_1_;
    }

    public double maxZ()
    {
        double lvt_1_1_ = this.getCenterZ() + this.getDiameter() / 2.0D;

        if (lvt_1_1_ > (double)this.worldSize)
        {
            lvt_1_1_ = (double)this.worldSize;
        }

        return lvt_1_1_;
    }

    public double getCenterX()
    {
        return this.centerX;
    }

    public double getCenterZ()
    {
        return this.centerZ;
    }

    public void setCenter(double x, double z)
    {
        this.centerX = x;
        this.centerZ = z;

        for (IBorderListener lvt_6_1_ : this.getListeners())
        {
            lvt_6_1_.onCenterChanged(this, x, z);
        }
    }

    public double getDiameter()
    {
        if (this.getStatus() != EnumBorderStatus.STATIONARY)
        {
            double lvt_1_1_ = (double)((float)(System.currentTimeMillis() - this.startTime) / (float)(this.endTime - this.startTime));

            if (lvt_1_1_ < 1.0D)
            {
                return this.startDiameter + (this.endDiameter - this.startDiameter) * lvt_1_1_;
            }

            this.setTransition(this.endDiameter);
        }

        return this.startDiameter;
    }

    public long getTimeUntilTarget()
    {
        return this.getStatus() != EnumBorderStatus.STATIONARY ? this.endTime - System.currentTimeMillis() : 0L;
    }

    public double getTargetSize()
    {
        return this.endDiameter;
    }

    public void setTransition(double newSize)
    {
        this.startDiameter = newSize;
        this.endDiameter = newSize;
        this.endTime = System.currentTimeMillis();
        this.startTime = this.endTime;

        for (IBorderListener lvt_4_1_ : this.getListeners())
        {
            lvt_4_1_.onSizeChanged(this, newSize);
        }
    }

    public void setTransition(double oldSize, double newSize, long time)
    {
        this.startDiameter = oldSize;
        this.endDiameter = newSize;
        this.startTime = System.currentTimeMillis();
        this.endTime = this.startTime + time;

        for (IBorderListener lvt_8_1_ : this.getListeners())
        {
            lvt_8_1_.onTransitionStarted(this, oldSize, newSize, time);
        }
    }

    protected List<IBorderListener> getListeners()
    {
        return Lists.newArrayList(this.listeners);
    }

    public void addListener(IBorderListener listener)
    {
        this.listeners.add(listener);
    }

    public void setSize(int size)
    {
        this.worldSize = size;
    }

    public int getSize()
    {
        return this.worldSize;
    }

    public double getDamageBuffer()
    {
        return this.damageBuffer;
    }

    public void setDamageBuffer(double bufferSize)
    {
        this.damageBuffer = bufferSize;

        for (IBorderListener lvt_4_1_ : this.getListeners())
        {
            lvt_4_1_.onDamageBufferChanged(this, bufferSize);
        }
    }

    public double getDamageAmount()
    {
        return this.damageAmount;
    }

    public void setDamageAmount(double newAmount)
    {
        this.damageAmount = newAmount;

        for (IBorderListener lvt_4_1_ : this.getListeners())
        {
            lvt_4_1_.onDamageAmountChanged(this, newAmount);
        }
    }

    public double getResizeSpeed()
    {
        return this.endTime == this.startTime ? 0.0D : Math.abs(this.startDiameter - this.endDiameter) / (double)(this.endTime - this.startTime);
    }

    public int getWarningTime()
    {
        return this.warningTime;
    }

    public void setWarningTime(int warningTime)
    {
        this.warningTime = warningTime;

        for (IBorderListener lvt_3_1_ : this.getListeners())
        {
            lvt_3_1_.onWarningTimeChanged(this, warningTime);
        }
    }

    public int getWarningDistance()
    {
        return this.warningDistance;
    }

    public void setWarningDistance(int warningDistance)
    {
        this.warningDistance = warningDistance;

        for (IBorderListener lvt_3_1_ : this.getListeners())
        {
            lvt_3_1_.onWarningDistanceChanged(this, warningDistance);
        }
    }
}
