package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityParticleEmitter extends EntityFX
{
    private Entity attachedEntity;
    private int age;
    private int lifetime;
    private EnumParticleTypes particleTypes;

    public EntityParticleEmitter(World worldIn, Entity p_i46279_2_, EnumParticleTypes particleTypesIn)
    {
        super(worldIn, p_i46279_2_.posX, p_i46279_2_.getEntityBoundingBox().minY + (double)(p_i46279_2_.height / 2.0F), p_i46279_2_.posZ, p_i46279_2_.motionX, p_i46279_2_.motionY, p_i46279_2_.motionZ);
        this.attachedEntity = p_i46279_2_;
        this.lifetime = 3;
        this.particleTypes = particleTypesIn;
        this.onUpdate();
    }

    /**
     * Renders the particle
     */
    public void renderParticle(WorldRenderer worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
    {
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        for (int lvt_1_1_ = 0; lvt_1_1_ < 16; ++lvt_1_1_)
        {
            double lvt_2_1_ = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
            double lvt_4_1_ = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
            double lvt_6_1_ = (double)(this.rand.nextFloat() * 2.0F - 1.0F);

            if (lvt_2_1_ * lvt_2_1_ + lvt_4_1_ * lvt_4_1_ + lvt_6_1_ * lvt_6_1_ <= 1.0D)
            {
                double lvt_8_1_ = this.attachedEntity.posX + lvt_2_1_ * (double)this.attachedEntity.width / 4.0D;
                double lvt_10_1_ = this.attachedEntity.getEntityBoundingBox().minY + (double)(this.attachedEntity.height / 2.0F) + lvt_4_1_ * (double)this.attachedEntity.height / 4.0D;
                double lvt_12_1_ = this.attachedEntity.posZ + lvt_6_1_ * (double)this.attachedEntity.width / 4.0D;
                this.worldObj.spawnParticle(this.particleTypes, false, lvt_8_1_, lvt_10_1_, lvt_12_1_, lvt_2_1_, lvt_4_1_ + 0.2D, lvt_6_1_, new int[0]);
            }
        }

        ++this.age;

        if (this.age >= this.lifetime)
        {
            this.setDead();
        }
    }

    public int getFXLayer()
    {
        return 3;
    }
}
