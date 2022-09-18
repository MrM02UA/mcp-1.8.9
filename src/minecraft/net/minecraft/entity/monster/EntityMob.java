package net.minecraft.entity.monster;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public abstract class EntityMob extends EntityCreature implements IMob
{
    public EntityMob(World worldIn)
    {
        super(worldIn);
        this.experienceValue = 5;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate()
    {
        this.updateArmSwingProgress();
        float lvt_1_1_ = this.getBrightness(1.0F);

        if (lvt_1_1_ > 0.5F)
        {
            this.entityAge += 2;
        }

        super.onLivingUpdate();
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (!this.worldObj.isRemote && this.worldObj.getDifficulty() == EnumDifficulty.PEACEFUL)
        {
            this.setDead();
        }
    }

    protected String getSwimSound()
    {
        return "game.hostile.swim";
    }

    protected String getSplashSound()
    {
        return "game.hostile.swim.splash";
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isEntityInvulnerable(source))
        {
            return false;
        }
        else if (super.attackEntityFrom(source, amount))
        {
            Entity lvt_3_1_ = source.getEntity();
            return this.riddenByEntity != lvt_3_1_ && this.ridingEntity != lvt_3_1_ ? true : true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound()
    {
        return "game.hostile.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound()
    {
        return "game.hostile.die";
    }

    protected String getFallSoundString(int damageValue)
    {
        return damageValue > 4 ? "game.hostile.hurt.fall.big" : "game.hostile.hurt.fall.small";
    }

    public boolean attackEntityAsMob(Entity entityIn)
    {
        float lvt_2_1_ = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
        int lvt_3_1_ = 0;

        if (entityIn instanceof EntityLivingBase)
        {
            lvt_2_1_ += EnchantmentHelper.getModifierForCreature(this.getHeldItem(), ((EntityLivingBase)entityIn).getCreatureAttribute());
            lvt_3_1_ += EnchantmentHelper.getKnockbackModifier(this);
        }

        boolean lvt_4_1_ = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), lvt_2_1_);

        if (lvt_4_1_)
        {
            if (lvt_3_1_ > 0)
            {
                entityIn.addVelocity((double)(-MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F) * (float)lvt_3_1_ * 0.5F), 0.1D, (double)(MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F) * (float)lvt_3_1_ * 0.5F));
                this.motionX *= 0.6D;
                this.motionZ *= 0.6D;
            }

            int lvt_5_1_ = EnchantmentHelper.getFireAspectModifier(this);

            if (lvt_5_1_ > 0)
            {
                entityIn.setFire(lvt_5_1_ * 4);
            }

            this.applyEnchantments(this, entityIn);
        }

        return lvt_4_1_;
    }

    public float getBlockPathWeight(BlockPos pos)
    {
        return 0.5F - this.worldObj.getLightBrightness(pos);
    }

    /**
     * Checks to make sure the light is not too bright where the mob is spawning
     */
    protected boolean isValidLightLevel()
    {
        BlockPos lvt_1_1_ = new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ);

        if (this.worldObj.getLightFor(EnumSkyBlock.SKY, lvt_1_1_) > this.rand.nextInt(32))
        {
            return false;
        }
        else
        {
            int lvt_2_1_ = this.worldObj.getLightFromNeighbors(lvt_1_1_);

            if (this.worldObj.isThundering())
            {
                int lvt_3_1_ = this.worldObj.getSkylightSubtracted();
                this.worldObj.setSkylightSubtracted(10);
                lvt_2_1_ = this.worldObj.getLightFromNeighbors(lvt_1_1_);
                this.worldObj.setSkylightSubtracted(lvt_3_1_);
            }

            return lvt_2_1_ <= this.rand.nextInt(8);
        }
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
        return this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL && this.isValidLightLevel() && super.getCanSpawnHere();
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
    }

    /**
     * Entity won't drop items or experience points if this returns false
     */
    protected boolean canDropLoot()
    {
        return true;
    }
}
