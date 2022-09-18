package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityLargeExplodeFX extends EntityFX
{
    private static final ResourceLocation EXPLOSION_TEXTURE = new ResourceLocation("textures/entity/explosion.png");
    private static final VertexFormat field_181549_az = (new VertexFormat()).addElement(DefaultVertexFormats.POSITION_3F).addElement(DefaultVertexFormats.TEX_2F).addElement(DefaultVertexFormats.COLOR_4UB).addElement(DefaultVertexFormats.TEX_2S).addElement(DefaultVertexFormats.NORMAL_3B).addElement(DefaultVertexFormats.PADDING_1B);
    private int field_70581_a;
    private int field_70584_aq;

    /** The Rendering Engine. */
    private TextureManager theRenderEngine;
    private float field_70582_as;

    protected EntityLargeExplodeFX(TextureManager renderEngine, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double p_i1213_9_, double p_i1213_11_, double p_i1213_13_)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);
        this.theRenderEngine = renderEngine;
        this.field_70584_aq = 6 + this.rand.nextInt(4);
        this.particleRed = this.particleGreen = this.particleBlue = this.rand.nextFloat() * 0.6F + 0.4F;
        this.field_70582_as = 1.0F - (float)p_i1213_9_ * 0.5F;
    }

    /**
     * Renders the particle
     */
    public void renderParticle(WorldRenderer worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
    {
        int lvt_9_1_ = (int)(((float)this.field_70581_a + partialTicks) * 15.0F / (float)this.field_70584_aq);

        if (lvt_9_1_ <= 15)
        {
            this.theRenderEngine.bindTexture(EXPLOSION_TEXTURE);
            float lvt_10_1_ = (float)(lvt_9_1_ % 4) / 4.0F;
            float lvt_11_1_ = lvt_10_1_ + 0.24975F;
            float lvt_12_1_ = (float)(lvt_9_1_ / 4) / 4.0F;
            float lvt_13_1_ = lvt_12_1_ + 0.24975F;
            float lvt_14_1_ = 2.0F * this.field_70582_as;
            float lvt_15_1_ = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
            float lvt_16_1_ = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
            float lvt_17_1_ = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableLighting();
            RenderHelper.disableStandardItemLighting();
            worldRendererIn.begin(7, field_181549_az);
            worldRendererIn.pos((double)(lvt_15_1_ - rotationX * lvt_14_1_ - rotationXY * lvt_14_1_), (double)(lvt_16_1_ - rotationZ * lvt_14_1_), (double)(lvt_17_1_ - rotationYZ * lvt_14_1_ - rotationXZ * lvt_14_1_)).tex((double)lvt_11_1_, (double)lvt_13_1_).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
            worldRendererIn.pos((double)(lvt_15_1_ - rotationX * lvt_14_1_ + rotationXY * lvt_14_1_), (double)(lvt_16_1_ + rotationZ * lvt_14_1_), (double)(lvt_17_1_ - rotationYZ * lvt_14_1_ + rotationXZ * lvt_14_1_)).tex((double)lvt_11_1_, (double)lvt_12_1_).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
            worldRendererIn.pos((double)(lvt_15_1_ + rotationX * lvt_14_1_ + rotationXY * lvt_14_1_), (double)(lvt_16_1_ + rotationZ * lvt_14_1_), (double)(lvt_17_1_ + rotationYZ * lvt_14_1_ + rotationXZ * lvt_14_1_)).tex((double)lvt_10_1_, (double)lvt_12_1_).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
            worldRendererIn.pos((double)(lvt_15_1_ + rotationX * lvt_14_1_ - rotationXY * lvt_14_1_), (double)(lvt_16_1_ - rotationZ * lvt_14_1_), (double)(lvt_17_1_ + rotationYZ * lvt_14_1_ - rotationXZ * lvt_14_1_)).tex((double)lvt_10_1_, (double)lvt_13_1_).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
            Tessellator.getInstance().draw();
            GlStateManager.enableLighting();
        }
    }

    public int getBrightnessForRender(float partialTicks)
    {
        return 61680;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        ++this.field_70581_a;

        if (this.field_70581_a == this.field_70584_aq)
        {
            this.setDead();
        }
    }

    public int getFXLayer()
    {
        return 3;
    }

    public static class Factory implements IParticleFactory
    {
        public EntityFX getEntityFX(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_)
        {
            return new EntityLargeExplodeFX(Minecraft.getMinecraft().getTextureManager(), worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        }
    }
}
