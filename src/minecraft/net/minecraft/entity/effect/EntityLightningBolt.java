package net.minecraft.entity.effect;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityLightningBolt extends EntityWeatherEffect
{
    /**
     * Declares which state the lightning bolt is in. Whether it's in the air, hit the ground, etc.
     */
    private int lightningState;

    /**
     * A random long that is used to change the vertex of the lightning rendered in RenderLightningBolt
     */
    public long boltVertex;

    /**
     * Determines the time before the EntityLightningBolt is destroyed. It is a random integer decremented over time.
     */
    private int boltLivingTime;

    public EntityLightningBolt(World worldIn, double posX, double posY, double posZ)
    {
        super(worldIn);
        this.setLocationAndAngles(posX, posY, posZ, 0.0F, 0.0F);
        this.lightningState = 2;
        this.boltVertex = this.rand.nextLong();
        this.boltLivingTime = this.rand.nextInt(3) + 1;
        BlockPos lvt_8_1_ = new BlockPos(this);

        if (!worldIn.isRemote && worldIn.getGameRules().getBoolean("doFireTick") && (worldIn.getDifficulty() == EnumDifficulty.NORMAL || worldIn.getDifficulty() == EnumDifficulty.HARD) && worldIn.isAreaLoaded(lvt_8_1_, 10))
        {
            if (worldIn.getBlockState(lvt_8_1_).getBlock().getMaterial() == Material.air && Blocks.fire.canPlaceBlockAt(worldIn, lvt_8_1_))
            {
                worldIn.setBlockState(lvt_8_1_, Blocks.fire.getDefaultState());
            }

            for (int lvt_9_1_ = 0; lvt_9_1_ < 4; ++lvt_9_1_)
            {
                BlockPos lvt_10_1_ = lvt_8_1_.add(this.rand.nextInt(3) - 1, this.rand.nextInt(3) - 1, this.rand.nextInt(3) - 1);

                if (worldIn.getBlockState(lvt_10_1_).getBlock().getMaterial() == Material.air && Blocks.fire.canPlaceBlockAt(worldIn, lvt_10_1_))
                {
                    worldIn.setBlockState(lvt_10_1_, Blocks.fire.getDefaultState());
                }
            }
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (this.lightningState == 2)
        {
            this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "ambient.weather.thunder", 10000.0F, 0.8F + this.rand.nextFloat() * 0.2F);
            this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "random.explode", 2.0F, 0.5F + this.rand.nextFloat() * 0.2F);
        }

        --this.lightningState;

        if (this.lightningState < 0)
        {
            if (this.boltLivingTime == 0)
            {
                this.setDead();
            }
            else if (this.lightningState < -this.rand.nextInt(10))
            {
                --this.boltLivingTime;
                this.lightningState = 1;
                this.boltVertex = this.rand.nextLong();
                BlockPos lvt_1_1_ = new BlockPos(this);

                if (!this.worldObj.isRemote && this.worldObj.getGameRules().getBoolean("doFireTick") && this.worldObj.isAreaLoaded(lvt_1_1_, 10) && this.worldObj.getBlockState(lvt_1_1_).getBlock().getMaterial() == Material.air && Blocks.fire.canPlaceBlockAt(this.worldObj, lvt_1_1_))
                {
                    this.worldObj.setBlockState(lvt_1_1_, Blocks.fire.getDefaultState());
                }
            }
        }

        if (this.lightningState >= 0)
        {
            if (this.worldObj.isRemote)
            {
                this.worldObj.setLastLightningBolt(2);
            }
            else
            {
                double lvt_1_2_ = 3.0D;
                List<Entity> lvt_3_1_ = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, new AxisAlignedBB(this.posX - lvt_1_2_, this.posY - lvt_1_2_, this.posZ - lvt_1_2_, this.posX + lvt_1_2_, this.posY + 6.0D + lvt_1_2_, this.posZ + lvt_1_2_));

                for (int lvt_4_1_ = 0; lvt_4_1_ < lvt_3_1_.size(); ++lvt_4_1_)
                {
                    Entity lvt_5_1_ = (Entity)lvt_3_1_.get(lvt_4_1_);
                    lvt_5_1_.onStruckByLightning(this);
                }
            }
        }
    }

    protected void entityInit()
    {
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound tagCompund)
    {
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound tagCompound)
    {
    }
}
