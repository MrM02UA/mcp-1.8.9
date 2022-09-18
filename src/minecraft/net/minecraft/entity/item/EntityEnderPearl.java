package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityEnderPearl extends EntityThrowable
{
    private EntityLivingBase field_181555_c;

    public EntityEnderPearl(World worldIn)
    {
        super(worldIn);
    }

    public EntityEnderPearl(World worldIn, EntityLivingBase p_i1783_2_)
    {
        super(worldIn, p_i1783_2_);
        this.field_181555_c = p_i1783_2_;
    }

    public EntityEnderPearl(World worldIn, double x, double y, double z)
    {
        super(worldIn, x, y, z);
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    protected void onImpact(MovingObjectPosition p_70184_1_)
    {
        EntityLivingBase lvt_2_1_ = this.getThrower();

        if (p_70184_1_.entityHit != null)
        {
            if (p_70184_1_.entityHit == this.field_181555_c)
            {
                return;
            }

            p_70184_1_.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, lvt_2_1_), 0.0F);
        }

        for (int lvt_3_1_ = 0; lvt_3_1_ < 32; ++lvt_3_1_)
        {
            this.worldObj.spawnParticle(EnumParticleTypes.PORTAL, this.posX, this.posY + this.rand.nextDouble() * 2.0D, this.posZ, this.rand.nextGaussian(), 0.0D, this.rand.nextGaussian(), new int[0]);
        }

        if (!this.worldObj.isRemote)
        {
            if (lvt_2_1_ instanceof EntityPlayerMP)
            {
                EntityPlayerMP lvt_3_2_ = (EntityPlayerMP)lvt_2_1_;

                if (lvt_3_2_.playerNetServerHandler.getNetworkManager().isChannelOpen() && lvt_3_2_.worldObj == this.worldObj && !lvt_3_2_.isPlayerSleeping())
                {
                    if (this.rand.nextFloat() < 0.05F && this.worldObj.getGameRules().getBoolean("doMobSpawning"))
                    {
                        EntityEndermite lvt_4_1_ = new EntityEndermite(this.worldObj);
                        lvt_4_1_.setSpawnedByPlayer(true);
                        lvt_4_1_.setLocationAndAngles(lvt_2_1_.posX, lvt_2_1_.posY, lvt_2_1_.posZ, lvt_2_1_.rotationYaw, lvt_2_1_.rotationPitch);
                        this.worldObj.spawnEntityInWorld(lvt_4_1_);
                    }

                    if (lvt_2_1_.isRiding())
                    {
                        lvt_2_1_.mountEntity((Entity)null);
                    }

                    lvt_2_1_.setPositionAndUpdate(this.posX, this.posY, this.posZ);
                    lvt_2_1_.fallDistance = 0.0F;
                    lvt_2_1_.attackEntityFrom(DamageSource.fall, 5.0F);
                }
            }
            else if (lvt_2_1_ != null)
            {
                lvt_2_1_.setPositionAndUpdate(this.posX, this.posY, this.posZ);
                lvt_2_1_.fallDistance = 0.0F;
            }

            this.setDead();
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        EntityLivingBase lvt_1_1_ = this.getThrower();

        if (lvt_1_1_ != null && lvt_1_1_ instanceof EntityPlayer && !lvt_1_1_.isEntityAlive())
        {
            this.setDead();
        }
        else
        {
            super.onUpdate();
        }
    }
}
