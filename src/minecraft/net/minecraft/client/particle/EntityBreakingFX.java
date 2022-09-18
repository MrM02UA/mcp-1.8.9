package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class EntityBreakingFX extends EntityFX
{
    protected EntityBreakingFX(World worldIn, double posXIn, double posYIn, double posZIn, Item p_i1195_8_)
    {
        this(worldIn, posXIn, posYIn, posZIn, p_i1195_8_, 0);
    }

    protected EntityBreakingFX(World worldIn, double posXIn, double posYIn, double posZIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, Item p_i1197_14_, int p_i1197_15_)
    {
        this(worldIn, posXIn, posYIn, posZIn, p_i1197_14_, p_i1197_15_);
        this.motionX *= 0.10000000149011612D;
        this.motionY *= 0.10000000149011612D;
        this.motionZ *= 0.10000000149011612D;
        this.motionX += xSpeedIn;
        this.motionY += ySpeedIn;
        this.motionZ += zSpeedIn;
    }

    protected EntityBreakingFX(World worldIn, double posXIn, double posYIn, double posZIn, Item p_i1196_8_, int p_i1196_9_)
    {
        super(worldIn, posXIn, posYIn, posZIn, 0.0D, 0.0D, 0.0D);
        this.setParticleIcon(Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(p_i1196_8_, p_i1196_9_));
        this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
        this.particleGravity = Blocks.snow.blockParticleGravity;
        this.particleScale /= 2.0F;
    }

    public int getFXLayer()
    {
        return 1;
    }

    /**
     * Renders the particle
     */
    public void renderParticle(WorldRenderer worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
    {
        float lvt_9_1_ = ((float)this.particleTextureIndexX + this.particleTextureJitterX / 4.0F) / 16.0F;
        float lvt_10_1_ = lvt_9_1_ + 0.015609375F;
        float lvt_11_1_ = ((float)this.particleTextureIndexY + this.particleTextureJitterY / 4.0F) / 16.0F;
        float lvt_12_1_ = lvt_11_1_ + 0.015609375F;
        float lvt_13_1_ = 0.1F * this.particleScale;

        if (this.particleIcon != null)
        {
            lvt_9_1_ = this.particleIcon.getInterpolatedU((double)(this.particleTextureJitterX / 4.0F * 16.0F));
            lvt_10_1_ = this.particleIcon.getInterpolatedU((double)((this.particleTextureJitterX + 1.0F) / 4.0F * 16.0F));
            lvt_11_1_ = this.particleIcon.getInterpolatedV((double)(this.particleTextureJitterY / 4.0F * 16.0F));
            lvt_12_1_ = this.particleIcon.getInterpolatedV((double)((this.particleTextureJitterY + 1.0F) / 4.0F * 16.0F));
        }

        float lvt_14_1_ = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
        float lvt_15_1_ = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
        float lvt_16_1_ = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
        int lvt_17_1_ = this.getBrightnessForRender(partialTicks);
        int lvt_18_1_ = lvt_17_1_ >> 16 & 65535;
        int lvt_19_1_ = lvt_17_1_ & 65535;
        worldRendererIn.pos((double)(lvt_14_1_ - rotationX * lvt_13_1_ - rotationXY * lvt_13_1_), (double)(lvt_15_1_ - rotationZ * lvt_13_1_), (double)(lvt_16_1_ - rotationYZ * lvt_13_1_ - rotationXZ * lvt_13_1_)).tex((double)lvt_9_1_, (double)lvt_12_1_).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(lvt_18_1_, lvt_19_1_).endVertex();
        worldRendererIn.pos((double)(lvt_14_1_ - rotationX * lvt_13_1_ + rotationXY * lvt_13_1_), (double)(lvt_15_1_ + rotationZ * lvt_13_1_), (double)(lvt_16_1_ - rotationYZ * lvt_13_1_ + rotationXZ * lvt_13_1_)).tex((double)lvt_9_1_, (double)lvt_11_1_).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(lvt_18_1_, lvt_19_1_).endVertex();
        worldRendererIn.pos((double)(lvt_14_1_ + rotationX * lvt_13_1_ + rotationXY * lvt_13_1_), (double)(lvt_15_1_ + rotationZ * lvt_13_1_), (double)(lvt_16_1_ + rotationYZ * lvt_13_1_ + rotationXZ * lvt_13_1_)).tex((double)lvt_10_1_, (double)lvt_11_1_).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(lvt_18_1_, lvt_19_1_).endVertex();
        worldRendererIn.pos((double)(lvt_14_1_ + rotationX * lvt_13_1_ - rotationXY * lvt_13_1_), (double)(lvt_15_1_ - rotationZ * lvt_13_1_), (double)(lvt_16_1_ + rotationYZ * lvt_13_1_ - rotationXZ * lvt_13_1_)).tex((double)lvt_10_1_, (double)lvt_12_1_).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(lvt_18_1_, lvt_19_1_).endVertex();
    }

    public static class Factory implements IParticleFactory
    {
        public EntityFX getEntityFX(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_)
        {
            int lvt_16_1_ = p_178902_15_.length > 1 ? p_178902_15_[1] : 0;
            return new EntityBreakingFX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, Item.getItemById(p_178902_15_[0]), lvt_16_1_);
        }
    }

    public static class SlimeFactory implements IParticleFactory
    {
        public EntityFX getEntityFX(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_)
        {
            return new EntityBreakingFX(worldIn, xCoordIn, yCoordIn, zCoordIn, Items.slime_ball);
        }
    }

    public static class SnowballFactory implements IParticleFactory
    {
        public EntityFX getEntityFX(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_)
        {
            return new EntityBreakingFX(worldIn, xCoordIn, yCoordIn, zCoordIn, Items.snowball);
        }
    }
}
