package net.minecraft.client.particle;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityDiggingFX extends EntityFX
{
    private IBlockState sourceState;
    private BlockPos sourcePos;

    protected EntityDiggingFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, IBlockState state)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.sourceState = state;
        this.setParticleIcon(Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state));
        this.particleGravity = state.getBlock().blockParticleGravity;
        this.particleRed = this.particleGreen = this.particleBlue = 0.6F;
        this.particleScale /= 2.0F;
    }

    /**
     * Sets the position of the block that this particle came from. Used for calculating texture and color multiplier.
     */
    public EntityDiggingFX setBlockPos(BlockPos pos)
    {
        this.sourcePos = pos;

        if (this.sourceState.getBlock() == Blocks.grass)
        {
            return this;
        }
        else
        {
            int lvt_2_1_ = this.sourceState.getBlock().colorMultiplier(this.worldObj, pos);
            this.particleRed *= (float)(lvt_2_1_ >> 16 & 255) / 255.0F;
            this.particleGreen *= (float)(lvt_2_1_ >> 8 & 255) / 255.0F;
            this.particleBlue *= (float)(lvt_2_1_ & 255) / 255.0F;
            return this;
        }
    }

    public EntityDiggingFX func_174845_l()
    {
        this.sourcePos = new BlockPos(this.posX, this.posY, this.posZ);
        Block lvt_1_1_ = this.sourceState.getBlock();

        if (lvt_1_1_ == Blocks.grass)
        {
            return this;
        }
        else
        {
            int lvt_2_1_ = lvt_1_1_.getRenderColor(this.sourceState);
            this.particleRed *= (float)(lvt_2_1_ >> 16 & 255) / 255.0F;
            this.particleGreen *= (float)(lvt_2_1_ >> 8 & 255) / 255.0F;
            this.particleBlue *= (float)(lvt_2_1_ & 255) / 255.0F;
            return this;
        }
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

    public int getBrightnessForRender(float partialTicks)
    {
        int lvt_2_1_ = super.getBrightnessForRender(partialTicks);
        int lvt_3_1_ = 0;

        if (this.worldObj.isBlockLoaded(this.sourcePos))
        {
            lvt_3_1_ = this.worldObj.getCombinedLight(this.sourcePos, 0);
        }

        return lvt_2_1_ == 0 ? lvt_3_1_ : lvt_2_1_;
    }

    public static class Factory implements IParticleFactory
    {
        public EntityFX getEntityFX(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_)
        {
            return (new EntityDiggingFX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, Block.getStateById(p_178902_15_[0]))).func_174845_l();
        }
    }
}
