package net.minecraft.entity;

import java.util.UUID;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public abstract class EntityCreature extends EntityLiving
{
    public static final UUID FLEEING_SPEED_MODIFIER_UUID = UUID.fromString("E199AD21-BA8A-4C53-8D13-6182D5C69D3A");
    public static final AttributeModifier FLEEING_SPEED_MODIFIER = (new AttributeModifier(FLEEING_SPEED_MODIFIER_UUID, "Fleeing speed bonus", 2.0D, 2)).setSaved(false);
    private BlockPos homePosition = BlockPos.ORIGIN;

    /** If -1 there is no maximum distance */
    private float maximumHomeDistance = -1.0F;
    private EntityAIBase aiBase = new EntityAIMoveTowardsRestriction(this, 1.0D);
    private boolean isMovementAITaskSet;

    public EntityCreature(World worldIn)
    {
        super(worldIn);
    }

    public float getBlockPathWeight(BlockPos pos)
    {
        return 0.0F;
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
        return super.getCanSpawnHere() && this.getBlockPathWeight(new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ)) >= 0.0F;
    }

    /**
     * if the entity got a PathEntity it returns true, else false
     */
    public boolean hasPath()
    {
        return !this.navigator.noPath();
    }

    public boolean isWithinHomeDistanceCurrentPosition()
    {
        return this.isWithinHomeDistanceFromPosition(new BlockPos(this));
    }

    public boolean isWithinHomeDistanceFromPosition(BlockPos pos)
    {
        return this.maximumHomeDistance == -1.0F ? true : this.homePosition.distanceSq(pos) < (double)(this.maximumHomeDistance * this.maximumHomeDistance);
    }

    /**
     * Sets home position and max distance for it
     */
    public void setHomePosAndDistance(BlockPos pos, int distance)
    {
        this.homePosition = pos;
        this.maximumHomeDistance = (float)distance;
    }

    public BlockPos getHomePosition()
    {
        return this.homePosition;
    }

    public float getMaximumHomeDistance()
    {
        return this.maximumHomeDistance;
    }

    public void detachHome()
    {
        this.maximumHomeDistance = -1.0F;
    }

    /**
     * Returns whether a home area is defined for this entity.
     */
    public boolean hasHome()
    {
        return this.maximumHomeDistance != -1.0F;
    }

    /**
     * Applies logic related to leashes, for example dragging the entity or breaking the leash.
     */
    protected void updateLeashedState()
    {
        super.updateLeashedState();

        if (this.getLeashed() && this.getLeashedToEntity() != null && this.getLeashedToEntity().worldObj == this.worldObj)
        {
            Entity lvt_1_1_ = this.getLeashedToEntity();
            this.setHomePosAndDistance(new BlockPos((int)lvt_1_1_.posX, (int)lvt_1_1_.posY, (int)lvt_1_1_.posZ), 5);
            float lvt_2_1_ = this.getDistanceToEntity(lvt_1_1_);

            if (this instanceof EntityTameable && ((EntityTameable)this).isSitting())
            {
                if (lvt_2_1_ > 10.0F)
                {
                    this.clearLeashed(true, true);
                }

                return;
            }

            if (!this.isMovementAITaskSet)
            {
                this.tasks.addTask(2, this.aiBase);

                if (this.getNavigator() instanceof PathNavigateGround)
                {
                    ((PathNavigateGround)this.getNavigator()).setAvoidsWater(false);
                }

                this.isMovementAITaskSet = true;
            }

            this.func_142017_o(lvt_2_1_);

            if (lvt_2_1_ > 4.0F)
            {
                this.getNavigator().tryMoveToEntityLiving(lvt_1_1_, 1.0D);
            }

            if (lvt_2_1_ > 6.0F)
            {
                double lvt_3_1_ = (lvt_1_1_.posX - this.posX) / (double)lvt_2_1_;
                double lvt_5_1_ = (lvt_1_1_.posY - this.posY) / (double)lvt_2_1_;
                double lvt_7_1_ = (lvt_1_1_.posZ - this.posZ) / (double)lvt_2_1_;
                this.motionX += lvt_3_1_ * Math.abs(lvt_3_1_) * 0.4D;
                this.motionY += lvt_5_1_ * Math.abs(lvt_5_1_) * 0.4D;
                this.motionZ += lvt_7_1_ * Math.abs(lvt_7_1_) * 0.4D;
            }

            if (lvt_2_1_ > 10.0F)
            {
                this.clearLeashed(true, true);
            }
        }
        else if (!this.getLeashed() && this.isMovementAITaskSet)
        {
            this.isMovementAITaskSet = false;
            this.tasks.removeTask(this.aiBase);

            if (this.getNavigator() instanceof PathNavigateGround)
            {
                ((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
            }

            this.detachHome();
        }
    }

    protected void func_142017_o(float p_142017_1_)
    {
    }
}
