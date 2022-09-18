package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFX extends Entity
{
    protected int particleTextureIndexX;
    protected int particleTextureIndexY;
    protected float particleTextureJitterX;
    protected float particleTextureJitterY;
    protected int particleAge;
    protected int particleMaxAge;
    protected float particleScale;
    protected float particleGravity;

    /** The red amount of color. Used as a percentage, 1.0 = 255 and 0.0 = 0. */
    protected float particleRed;

    /**
     * The green amount of color. Used as a percentage, 1.0 = 255 and 0.0 = 0.
     */
    protected float particleGreen;

    /**
     * The blue amount of color. Used as a percentage, 1.0 = 255 and 0.0 = 0.
     */
    protected float particleBlue;

    /** Particle alpha */
    protected float particleAlpha;

    /** The icon field from which the given particle pulls its texture. */
    protected TextureAtlasSprite particleIcon;
    public static double interpPosX;
    public static double interpPosY;
    public static double interpPosZ;

    protected EntityFX(World worldIn, double posXIn, double posYIn, double posZIn)
    {
        super(worldIn);
        this.particleAlpha = 1.0F;
        this.setSize(0.2F, 0.2F);
        this.setPosition(posXIn, posYIn, posZIn);
        this.lastTickPosX = this.prevPosX = posXIn;
        this.lastTickPosY = this.prevPosY = posYIn;
        this.lastTickPosZ = this.prevPosZ = posZIn;
        this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
        this.particleTextureJitterX = this.rand.nextFloat() * 3.0F;
        this.particleTextureJitterY = this.rand.nextFloat() * 3.0F;
        this.particleScale = (this.rand.nextFloat() * 0.5F + 0.5F) * 2.0F;
        this.particleMaxAge = (int)(4.0F / (this.rand.nextFloat() * 0.9F + 0.1F));
        this.particleAge = 0;
    }

    public EntityFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn)
    {
        this(worldIn, xCoordIn, yCoordIn, zCoordIn);
        this.motionX = xSpeedIn + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
        this.motionY = ySpeedIn + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
        this.motionZ = zSpeedIn + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
        float lvt_14_1_ = (float)(Math.random() + Math.random() + 1.0D) * 0.15F;
        float lvt_15_1_ = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        this.motionX = this.motionX / (double)lvt_15_1_ * (double)lvt_14_1_ * 0.4000000059604645D;
        this.motionY = this.motionY / (double)lvt_15_1_ * (double)lvt_14_1_ * 0.4000000059604645D + 0.10000000149011612D;
        this.motionZ = this.motionZ / (double)lvt_15_1_ * (double)lvt_14_1_ * 0.4000000059604645D;
    }

    public EntityFX multiplyVelocity(float multiplier)
    {
        this.motionX *= (double)multiplier;
        this.motionY = (this.motionY - 0.10000000149011612D) * (double)multiplier + 0.10000000149011612D;
        this.motionZ *= (double)multiplier;
        return this;
    }

    public EntityFX multipleParticleScaleBy(float scale)
    {
        this.setSize(0.2F * scale, 0.2F * scale);
        this.particleScale *= scale;
        return this;
    }

    public void setRBGColorF(float particleRedIn, float particleGreenIn, float particleBlueIn)
    {
        this.particleRed = particleRedIn;
        this.particleGreen = particleGreenIn;
        this.particleBlue = particleBlueIn;
    }

    /**
     * Sets the particle alpha (float)
     */
    public void setAlphaF(float alpha)
    {
        if (this.particleAlpha == 1.0F && alpha < 1.0F)
        {
            Minecraft.getMinecraft().effectRenderer.moveToAlphaLayer(this);
        }
        else if (this.particleAlpha < 1.0F && alpha == 1.0F)
        {
            Minecraft.getMinecraft().effectRenderer.moveToNoAlphaLayer(this);
        }

        this.particleAlpha = alpha;
    }

    public float getRedColorF()
    {
        return this.particleRed;
    }

    public float getGreenColorF()
    {
        return this.particleGreen;
    }

    public float getBlueColorF()
    {
        return this.particleBlue;
    }

    public float getAlpha()
    {
        return this.particleAlpha;
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    protected void entityInit()
    {
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setDead();
        }

        this.motionY -= 0.04D * (double)this.particleGravity;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;

        if (this.onGround)
        {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }

    /**
     * Renders the particle
     */
    public void renderParticle(WorldRenderer worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
    {
        float lvt_9_1_ = (float)this.particleTextureIndexX / 16.0F;
        float lvt_10_1_ = lvt_9_1_ + 0.0624375F;
        float lvt_11_1_ = (float)this.particleTextureIndexY / 16.0F;
        float lvt_12_1_ = lvt_11_1_ + 0.0624375F;
        float lvt_13_1_ = 0.1F * this.particleScale;

        if (this.particleIcon != null)
        {
            lvt_9_1_ = this.particleIcon.getMinU();
            lvt_10_1_ = this.particleIcon.getMaxU();
            lvt_11_1_ = this.particleIcon.getMinV();
            lvt_12_1_ = this.particleIcon.getMaxV();
        }

        float lvt_14_1_ = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
        float lvt_15_1_ = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
        float lvt_16_1_ = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
        int lvt_17_1_ = this.getBrightnessForRender(partialTicks);
        int lvt_18_1_ = lvt_17_1_ >> 16 & 65535;
        int lvt_19_1_ = lvt_17_1_ & 65535;
        worldRendererIn.pos((double)(lvt_14_1_ - rotationX * lvt_13_1_ - rotationXY * lvt_13_1_), (double)(lvt_15_1_ - rotationZ * lvt_13_1_), (double)(lvt_16_1_ - rotationYZ * lvt_13_1_ - rotationXZ * lvt_13_1_)).tex((double)lvt_10_1_, (double)lvt_12_1_).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lvt_18_1_, lvt_19_1_).endVertex();
        worldRendererIn.pos((double)(lvt_14_1_ - rotationX * lvt_13_1_ + rotationXY * lvt_13_1_), (double)(lvt_15_1_ + rotationZ * lvt_13_1_), (double)(lvt_16_1_ - rotationYZ * lvt_13_1_ + rotationXZ * lvt_13_1_)).tex((double)lvt_10_1_, (double)lvt_11_1_).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lvt_18_1_, lvt_19_1_).endVertex();
        worldRendererIn.pos((double)(lvt_14_1_ + rotationX * lvt_13_1_ + rotationXY * lvt_13_1_), (double)(lvt_15_1_ + rotationZ * lvt_13_1_), (double)(lvt_16_1_ + rotationYZ * lvt_13_1_ + rotationXZ * lvt_13_1_)).tex((double)lvt_9_1_, (double)lvt_11_1_).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lvt_18_1_, lvt_19_1_).endVertex();
        worldRendererIn.pos((double)(lvt_14_1_ + rotationX * lvt_13_1_ - rotationXY * lvt_13_1_), (double)(lvt_15_1_ - rotationZ * lvt_13_1_), (double)(lvt_16_1_ + rotationYZ * lvt_13_1_ - rotationXZ * lvt_13_1_)).tex((double)lvt_9_1_, (double)lvt_12_1_).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(lvt_18_1_, lvt_19_1_).endVertex();
    }

    public int getFXLayer()
    {
        return 0;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tagCompund)
    {
    }

    /**
     * Sets the particle's icon.
     */
    public void setParticleIcon(TextureAtlasSprite icon)
    {
        int lvt_2_1_ = this.getFXLayer();

        if (lvt_2_1_ == 1)
        {
            this.particleIcon = icon;
        }
        else
        {
            throw new RuntimeException("Invalid call to Particle.setTex, use coordinate methods");
        }
    }

    /**
     * Public method to set private field particleTextureIndex.
     */
    public void setParticleTextureIndex(int particleTextureIndex)
    {
        if (this.getFXLayer() != 0)
        {
            throw new RuntimeException("Invalid call to Particle.setMiscTex");
        }
        else
        {
            this.particleTextureIndexX = particleTextureIndex % 16;
            this.particleTextureIndexY = particleTextureIndex / 16;
        }
    }

    public void nextTextureIndexX()
    {
        ++this.particleTextureIndexX;
    }

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    public boolean canAttackWithItem()
    {
        return false;
    }

    public String toString()
    {
        return this.getClass().getSimpleName() + ", Pos (" + this.posX + "," + this.posY + "," + this.posZ + "), RGBA (" + this.particleRed + "," + this.particleGreen + "," + this.particleBlue + "," + this.particleAlpha + "), Age " + this.particleAge;
    }
}
