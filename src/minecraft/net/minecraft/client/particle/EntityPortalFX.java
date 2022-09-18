package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityPortalFX extends EntityFX
{
    private float portalParticleScale;
    private double portalPosX;
    private double portalPosY;
    private double portalPosZ;

    protected EntityPortalFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.motionX = xSpeedIn;
        this.motionY = ySpeedIn;
        this.motionZ = zSpeedIn;
        this.portalPosX = this.posX = xCoordIn;
        this.portalPosY = this.posY = yCoordIn;
        this.portalPosZ = this.posZ = zCoordIn;
        float lvt_14_1_ = this.rand.nextFloat() * 0.6F + 0.4F;
        this.portalParticleScale = this.particleScale = this.rand.nextFloat() * 0.2F + 0.5F;
        this.particleRed = this.particleGreen = this.particleBlue = 1.0F * lvt_14_1_;
        this.particleGreen *= 0.3F;
        this.particleRed *= 0.9F;
        this.particleMaxAge = (int)(Math.random() * 10.0D) + 40;
        this.noClip = true;
        this.setParticleTextureIndex((int)(Math.random() * 8.0D));
    }

    /**
     * Renders the particle
     */
    public void renderParticle(WorldRenderer worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
    {
        float lvt_9_1_ = ((float)this.particleAge + partialTicks) / (float)this.particleMaxAge;
        lvt_9_1_ = 1.0F - lvt_9_1_;
        lvt_9_1_ = lvt_9_1_ * lvt_9_1_;
        lvt_9_1_ = 1.0F - lvt_9_1_;
        this.particleScale = this.portalParticleScale * lvt_9_1_;
        super.renderParticle(worldRendererIn, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }

    public int getBrightnessForRender(float partialTicks)
    {
        int lvt_2_1_ = super.getBrightnessForRender(partialTicks);
        float lvt_3_1_ = (float)this.particleAge / (float)this.particleMaxAge;
        lvt_3_1_ = lvt_3_1_ * lvt_3_1_;
        lvt_3_1_ = lvt_3_1_ * lvt_3_1_;
        int lvt_4_1_ = lvt_2_1_ & 255;
        int lvt_5_1_ = lvt_2_1_ >> 16 & 255;
        lvt_5_1_ = lvt_5_1_ + (int)(lvt_3_1_ * 15.0F * 16.0F);

        if (lvt_5_1_ > 240)
        {
            lvt_5_1_ = 240;
        }

        return lvt_4_1_ | lvt_5_1_ << 16;
    }

    /**
     * Gets how bright this entity is.
     */
    public float getBrightness(float partialTicks)
    {
        float lvt_2_1_ = super.getBrightness(partialTicks);
        float lvt_3_1_ = (float)this.particleAge / (float)this.particleMaxAge;
        lvt_3_1_ = lvt_3_1_ * lvt_3_1_ * lvt_3_1_ * lvt_3_1_;
        return lvt_2_1_ * (1.0F - lvt_3_1_) + lvt_3_1_;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        float lvt_1_1_ = (float)this.particleAge / (float)this.particleMaxAge;
        lvt_1_1_ = -lvt_1_1_ + lvt_1_1_ * lvt_1_1_ * 2.0F;
        lvt_1_1_ = 1.0F - lvt_1_1_;
        this.posX = this.portalPosX + this.motionX * (double)lvt_1_1_;
        this.posY = this.portalPosY + this.motionY * (double)lvt_1_1_ + (double)(1.0F - lvt_1_1_);
        this.posZ = this.portalPosZ + this.motionZ * (double)lvt_1_1_;

        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setDead();
        }
    }

    public static class Factory implements IParticleFactory
    {
        public EntityFX getEntityFX(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_)
        {
            return new EntityPortalFX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        }
    }
}
