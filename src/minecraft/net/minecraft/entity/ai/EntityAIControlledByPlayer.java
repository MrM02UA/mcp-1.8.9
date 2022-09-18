package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.pathfinder.WalkNodeProcessor;

public class EntityAIControlledByPlayer extends EntityAIBase
{
    private final EntityLiving thisEntity;
    private final float maxSpeed;
    private float currentSpeed;

    /** Whether the entity's speed is boosted. */
    private boolean speedBoosted;

    /**
     * Counter for speed boosting, upon reaching maxSpeedBoostTime the speed boost will be disabled
     */
    private int speedBoostTime;

    /** Maximum time the entity's speed should be boosted for. */
    private int maxSpeedBoostTime;

    public EntityAIControlledByPlayer(EntityLiving entitylivingIn, float maxspeed)
    {
        this.thisEntity = entitylivingIn;
        this.maxSpeed = maxspeed;
        this.setMutexBits(7);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.currentSpeed = 0.0F;
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.speedBoosted = false;
        this.currentSpeed = 0.0F;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        return this.thisEntity.isEntityAlive() && this.thisEntity.riddenByEntity != null && this.thisEntity.riddenByEntity instanceof EntityPlayer && (this.speedBoosted || this.thisEntity.canBeSteered());
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        EntityPlayer lvt_1_1_ = (EntityPlayer)this.thisEntity.riddenByEntity;
        EntityCreature lvt_2_1_ = (EntityCreature)this.thisEntity;
        float lvt_3_1_ = MathHelper.wrapAngleTo180_float(lvt_1_1_.rotationYaw - this.thisEntity.rotationYaw) * 0.5F;

        if (lvt_3_1_ > 5.0F)
        {
            lvt_3_1_ = 5.0F;
        }

        if (lvt_3_1_ < -5.0F)
        {
            lvt_3_1_ = -5.0F;
        }

        this.thisEntity.rotationYaw = MathHelper.wrapAngleTo180_float(this.thisEntity.rotationYaw + lvt_3_1_);

        if (this.currentSpeed < this.maxSpeed)
        {
            this.currentSpeed += (this.maxSpeed - this.currentSpeed) * 0.01F;
        }

        if (this.currentSpeed > this.maxSpeed)
        {
            this.currentSpeed = this.maxSpeed;
        }

        int lvt_4_1_ = MathHelper.floor_double(this.thisEntity.posX);
        int lvt_5_1_ = MathHelper.floor_double(this.thisEntity.posY);
        int lvt_6_1_ = MathHelper.floor_double(this.thisEntity.posZ);
        float lvt_7_1_ = this.currentSpeed;

        if (this.speedBoosted)
        {
            if (this.speedBoostTime++ > this.maxSpeedBoostTime)
            {
                this.speedBoosted = false;
            }

            lvt_7_1_ += lvt_7_1_ * 1.15F * MathHelper.sin((float)this.speedBoostTime / (float)this.maxSpeedBoostTime * (float)Math.PI);
        }

        float lvt_8_1_ = 0.91F;

        if (this.thisEntity.onGround)
        {
            lvt_8_1_ = this.thisEntity.worldObj.getBlockState(new BlockPos(MathHelper.floor_float((float)lvt_4_1_), MathHelper.floor_float((float)lvt_5_1_) - 1, MathHelper.floor_float((float)lvt_6_1_))).getBlock().slipperiness * 0.91F;
        }

        float lvt_9_1_ = 0.16277136F / (lvt_8_1_ * lvt_8_1_ * lvt_8_1_);
        float lvt_10_1_ = MathHelper.sin(lvt_2_1_.rotationYaw * (float)Math.PI / 180.0F);
        float lvt_11_1_ = MathHelper.cos(lvt_2_1_.rotationYaw * (float)Math.PI / 180.0F);
        float lvt_12_1_ = lvt_2_1_.getAIMoveSpeed() * lvt_9_1_;
        float lvt_13_1_ = Math.max(lvt_7_1_, 1.0F);
        lvt_13_1_ = lvt_12_1_ / lvt_13_1_;
        float lvt_14_1_ = lvt_7_1_ * lvt_13_1_;
        float lvt_15_1_ = -(lvt_14_1_ * lvt_10_1_);
        float lvt_16_1_ = lvt_14_1_ * lvt_11_1_;

        if (MathHelper.abs(lvt_15_1_) > MathHelper.abs(lvt_16_1_))
        {
            if (lvt_15_1_ < 0.0F)
            {
                lvt_15_1_ -= this.thisEntity.width / 2.0F;
            }

            if (lvt_15_1_ > 0.0F)
            {
                lvt_15_1_ += this.thisEntity.width / 2.0F;
            }

            lvt_16_1_ = 0.0F;
        }
        else
        {
            lvt_15_1_ = 0.0F;

            if (lvt_16_1_ < 0.0F)
            {
                lvt_16_1_ -= this.thisEntity.width / 2.0F;
            }

            if (lvt_16_1_ > 0.0F)
            {
                lvt_16_1_ += this.thisEntity.width / 2.0F;
            }
        }

        int lvt_17_1_ = MathHelper.floor_double(this.thisEntity.posX + (double)lvt_15_1_);
        int lvt_18_1_ = MathHelper.floor_double(this.thisEntity.posZ + (double)lvt_16_1_);
        int lvt_19_1_ = MathHelper.floor_float(this.thisEntity.width + 1.0F);
        int lvt_20_1_ = MathHelper.floor_float(this.thisEntity.height + lvt_1_1_.height + 1.0F);
        int lvt_21_1_ = MathHelper.floor_float(this.thisEntity.width + 1.0F);

        if (lvt_4_1_ != lvt_17_1_ || lvt_6_1_ != lvt_18_1_)
        {
            Block lvt_22_1_ = this.thisEntity.worldObj.getBlockState(new BlockPos(lvt_4_1_, lvt_5_1_, lvt_6_1_)).getBlock();
            boolean lvt_23_1_ = !this.isStairOrSlab(lvt_22_1_) && (lvt_22_1_.getMaterial() != Material.air || !this.isStairOrSlab(this.thisEntity.worldObj.getBlockState(new BlockPos(lvt_4_1_, lvt_5_1_ - 1, lvt_6_1_)).getBlock()));

            if (lvt_23_1_ && 0 == WalkNodeProcessor.func_176170_a(this.thisEntity.worldObj, this.thisEntity, lvt_17_1_, lvt_5_1_, lvt_18_1_, lvt_19_1_, lvt_20_1_, lvt_21_1_, false, false, true) && 1 == WalkNodeProcessor.func_176170_a(this.thisEntity.worldObj, this.thisEntity, lvt_4_1_, lvt_5_1_ + 1, lvt_6_1_, lvt_19_1_, lvt_20_1_, lvt_21_1_, false, false, true) && 1 == WalkNodeProcessor.func_176170_a(this.thisEntity.worldObj, this.thisEntity, lvt_17_1_, lvt_5_1_ + 1, lvt_18_1_, lvt_19_1_, lvt_20_1_, lvt_21_1_, false, false, true))
            {
                lvt_2_1_.getJumpHelper().setJumping();
            }
        }

        if (!lvt_1_1_.capabilities.isCreativeMode && this.currentSpeed >= this.maxSpeed * 0.5F && this.thisEntity.getRNG().nextFloat() < 0.006F && !this.speedBoosted)
        {
            ItemStack lvt_22_2_ = lvt_1_1_.getHeldItem();

            if (lvt_22_2_ != null && lvt_22_2_.getItem() == Items.carrot_on_a_stick)
            {
                lvt_22_2_.damageItem(1, lvt_1_1_);

                if (lvt_22_2_.stackSize == 0)
                {
                    ItemStack lvt_23_2_ = new ItemStack(Items.fishing_rod);
                    lvt_23_2_.setTagCompound(lvt_22_2_.getTagCompound());
                    lvt_1_1_.inventory.mainInventory[lvt_1_1_.inventory.currentItem] = lvt_23_2_;
                }
            }
        }

        this.thisEntity.moveEntityWithHeading(0.0F, lvt_7_1_);
    }

    /**
     * True if the block is a stair block or a slab block
     */
    private boolean isStairOrSlab(Block blockIn)
    {
        return blockIn instanceof BlockStairs || blockIn instanceof BlockSlab;
    }

    /**
     * Return whether the entity's speed is boosted.
     */
    public boolean isSpeedBoosted()
    {
        return this.speedBoosted;
    }

    /**
     * Boost the entity's movement speed.
     */
    public void boostSpeed()
    {
        this.speedBoosted = true;
        this.speedBoostTime = 0;
        this.maxSpeedBoostTime = this.thisEntity.getRNG().nextInt(841) + 140;
    }

    /**
     * Return whether the entity is being controlled by a player.
     */
    public boolean isControlledByPlayer()
    {
        return !this.isSpeedBoosted() && this.currentSpeed > this.maxSpeed * 0.3F;
    }
}
