package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class Barrier extends EntityFX
{
    protected Barrier(World worldIn, double p_i46286_2_, double p_i46286_4_, double p_i46286_6_, Item p_i46286_8_)
    {
        super(worldIn, p_i46286_2_, p_i46286_4_, p_i46286_6_, 0.0D, 0.0D, 0.0D);
        this.setParticleIcon(Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getParticleIcon(p_i46286_8_));
        this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
        this.motionX = this.motionY = this.motionZ = 0.0D;
        this.particleGravity = 0.0F;
        this.particleMaxAge = 80;
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
        float lvt_9_1_ = this.particleIcon.getMinU();
        float lvt_10_1_ = this.particleIcon.getMaxU();
        float lvt_11_1_ = this.particleIcon.getMinV();
        float lvt_12_1_ = this.particleIcon.getMaxV();
        float lvt_13_1_ = 0.5F;
        float lvt_14_1_ = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
        float lvt_15_1_ = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
        float lvt_16_1_ = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
        int lvt_17_1_ = this.getBrightnessForRender(partialTicks);
        int lvt_18_1_ = lvt_17_1_ >> 16 & 65535;
        int lvt_19_1_ = lvt_17_1_ & 65535;
        worldRendererIn.pos((double)(lvt_14_1_ - rotationX * 0.5F - rotationXY * 0.5F), (double)(lvt_15_1_ - rotationZ * 0.5F), (double)(lvt_16_1_ - rotationYZ * 0.5F - rotationXZ * 0.5F)).tex((double)lvt_10_1_, (double)lvt_12_1_).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(lvt_18_1_, lvt_19_1_).endVertex();
        worldRendererIn.pos((double)(lvt_14_1_ - rotationX * 0.5F + rotationXY * 0.5F), (double)(lvt_15_1_ + rotationZ * 0.5F), (double)(lvt_16_1_ - rotationYZ * 0.5F + rotationXZ * 0.5F)).tex((double)lvt_10_1_, (double)lvt_11_1_).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(lvt_18_1_, lvt_19_1_).endVertex();
        worldRendererIn.pos((double)(lvt_14_1_ + rotationX * 0.5F + rotationXY * 0.5F), (double)(lvt_15_1_ + rotationZ * 0.5F), (double)(lvt_16_1_ + rotationYZ * 0.5F + rotationXZ * 0.5F)).tex((double)lvt_9_1_, (double)lvt_11_1_).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(lvt_18_1_, lvt_19_1_).endVertex();
        worldRendererIn.pos((double)(lvt_14_1_ + rotationX * 0.5F - rotationXY * 0.5F), (double)(lvt_15_1_ - rotationZ * 0.5F), (double)(lvt_16_1_ + rotationYZ * 0.5F - rotationXZ * 0.5F)).tex((double)lvt_9_1_, (double)lvt_12_1_).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(lvt_18_1_, lvt_19_1_).endVertex();
    }

    public static class Factory implements IParticleFactory
    {
        public EntityFX getEntityFX(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_)
        {
            return new Barrier(worldIn, xCoordIn, yCoordIn, zCoordIn, Item.getItemFromBlock(Blocks.barrier));
        }
    }
}
