package net.minecraft.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ITickable;

public class TileEntityEnderChest extends TileEntity implements ITickable
{
    public float lidAngle;

    /** The angle of the ender chest lid last tick */
    public float prevLidAngle;
    public int numPlayersUsing;
    private int ticksSinceSync;

    /**
     * Like the old updateEntity(), except more generic.
     */
    public void update()
    {
        if (++this.ticksSinceSync % 20 * 4 == 0)
        {
            this.worldObj.addBlockEvent(this.pos, Blocks.ender_chest, 1, this.numPlayersUsing);
        }

        this.prevLidAngle = this.lidAngle;
        int lvt_1_1_ = this.pos.getX();
        int lvt_2_1_ = this.pos.getY();
        int lvt_3_1_ = this.pos.getZ();
        float lvt_4_1_ = 0.1F;

        if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F)
        {
            double lvt_5_1_ = (double)lvt_1_1_ + 0.5D;
            double lvt_7_1_ = (double)lvt_3_1_ + 0.5D;
            this.worldObj.playSoundEffect(lvt_5_1_, (double)lvt_2_1_ + 0.5D, lvt_7_1_, "random.chestopen", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }

        if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F)
        {
            float lvt_5_2_ = this.lidAngle;

            if (this.numPlayersUsing > 0)
            {
                this.lidAngle += lvt_4_1_;
            }
            else
            {
                this.lidAngle -= lvt_4_1_;
            }

            if (this.lidAngle > 1.0F)
            {
                this.lidAngle = 1.0F;
            }

            float lvt_6_1_ = 0.5F;

            if (this.lidAngle < lvt_6_1_ && lvt_5_2_ >= lvt_6_1_)
            {
                double lvt_7_2_ = (double)lvt_1_1_ + 0.5D;
                double lvt_9_1_ = (double)lvt_3_1_ + 0.5D;
                this.worldObj.playSoundEffect(lvt_7_2_, (double)lvt_2_1_ + 0.5D, lvt_9_1_, "random.chestclosed", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
            }

            if (this.lidAngle < 0.0F)
            {
                this.lidAngle = 0.0F;
            }
        }
    }

    public boolean receiveClientEvent(int id, int type)
    {
        if (id == 1)
        {
            this.numPlayersUsing = type;
            return true;
        }
        else
        {
            return super.receiveClientEvent(id, type);
        }
    }

    /**
     * invalidates a tile entity
     */
    public void invalidate()
    {
        this.updateContainingBlockInfo();
        super.invalidate();
    }

    public void openChest()
    {
        ++this.numPlayersUsing;
        this.worldObj.addBlockEvent(this.pos, Blocks.ender_chest, 1, this.numPlayersUsing);
    }

    public void closeChest()
    {
        --this.numPlayersUsing;
        this.worldObj.addBlockEvent(this.pos, Blocks.ender_chest, 1, this.numPlayersUsing);
    }

    public boolean canBeUsed(EntityPlayer p_145971_1_)
    {
        return this.worldObj.getTileEntity(this.pos) != this ? false : p_145971_1_.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
    }
}
