package net.minecraft.client.particle;

import net.minecraft.world.World;

public class EntityEnchantmentTableParticleFX extends EntityFX
{
    private float field_70565_a;
    private double coordX;
    private double coordY;
    private double coordZ;

    protected EntityEnchantmentTableParticleFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.motionX = xSpeedIn;
        this.motionY = ySpeedIn;
        this.motionZ = zSpeedIn;
        this.coordX = xCoordIn;
        this.coordY = yCoordIn;
        this.coordZ = zCoordIn;
        this.posX = this.prevPosX = xCoordIn + xSpeedIn;
        this.posY = this.prevPosY = yCoordIn + ySpeedIn;
        this.posZ = this.prevPosZ = zCoordIn + zSpeedIn;
        float lvt_14_1_ = this.rand.nextFloat() * 0.6F + 0.4F;
        this.field_70565_a = this.particleScale = this.rand.nextFloat() * 0.5F + 0.2F;
        this.particleRed = this.particleGreen = this.particleBlue = 1.0F * lvt_14_1_;
        this.particleGreen *= 0.9F;
        this.particleRed *= 0.9F;
        this.particleMaxAge = (int)(Math.random() * 10.0D) + 30;
        this.noClip = true;
        this.setParticleTextureIndex((int)(Math.random() * 26.0D + 1.0D + 224.0D));
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
        lvt_3_1_ = lvt_3_1_ * lvt_3_1_;
        lvt_3_1_ = lvt_3_1_ * lvt_3_1_;
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
        lvt_1_1_ = 1.0F - lvt_1_1_;
        float lvt_2_1_ = 1.0F - lvt_1_1_;
        lvt_2_1_ = lvt_2_1_ * lvt_2_1_;
        lvt_2_1_ = lvt_2_1_ * lvt_2_1_;
        this.posX = this.coordX + this.motionX * (double)lvt_1_1_;
        this.posY = this.coordY + this.motionY * (double)lvt_1_1_ - (double)(lvt_2_1_ * 1.2F);
        this.posZ = this.coordZ + this.motionZ * (double)lvt_1_1_;

        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setDead();
        }
    }

    public static class EnchantmentTable implements IParticleFactory
    {
        public EntityFX getEntityFX(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_)
        {
            return new EntityEnchantmentTableParticleFX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        }
    }
}
