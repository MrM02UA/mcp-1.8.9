package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityFootStepFX extends EntityFX
{
    private static final ResourceLocation FOOTPRINT_TEXTURE = new ResourceLocation("textures/particle/footprint.png");
    private int footstepAge;
    private int footstepMaxAge;
    private TextureManager currentFootSteps;

    protected EntityFootStepFX(TextureManager currentFootStepsIn, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);
        this.currentFootSteps = currentFootStepsIn;
        this.motionX = this.motionY = this.motionZ = 0.0D;
        this.footstepMaxAge = 200;
    }

    /**
     * Renders the particle
     */
    public void renderParticle(WorldRenderer worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
    {
        float lvt_9_1_ = ((float)this.footstepAge + partialTicks) / (float)this.footstepMaxAge;
        lvt_9_1_ = lvt_9_1_ * lvt_9_1_;
        float lvt_10_1_ = 2.0F - lvt_9_1_ * 2.0F;

        if (lvt_10_1_ > 1.0F)
        {
            lvt_10_1_ = 1.0F;
        }

        lvt_10_1_ = lvt_10_1_ * 0.2F;
        GlStateManager.disableLighting();
        float lvt_11_1_ = 0.125F;
        float lvt_12_1_ = (float)(this.posX - interpPosX);
        float lvt_13_1_ = (float)(this.posY - interpPosY);
        float lvt_14_1_ = (float)(this.posZ - interpPosZ);
        float lvt_15_1_ = this.worldObj.getLightBrightness(new BlockPos(this));
        this.currentFootSteps.bindTexture(FOOTPRINT_TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        worldRendererIn.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldRendererIn.pos((double)(lvt_12_1_ - 0.125F), (double)lvt_13_1_, (double)(lvt_14_1_ + 0.125F)).tex(0.0D, 1.0D).color(lvt_15_1_, lvt_15_1_, lvt_15_1_, lvt_10_1_).endVertex();
        worldRendererIn.pos((double)(lvt_12_1_ + 0.125F), (double)lvt_13_1_, (double)(lvt_14_1_ + 0.125F)).tex(1.0D, 1.0D).color(lvt_15_1_, lvt_15_1_, lvt_15_1_, lvt_10_1_).endVertex();
        worldRendererIn.pos((double)(lvt_12_1_ + 0.125F), (double)lvt_13_1_, (double)(lvt_14_1_ - 0.125F)).tex(1.0D, 0.0D).color(lvt_15_1_, lvt_15_1_, lvt_15_1_, lvt_10_1_).endVertex();
        worldRendererIn.pos((double)(lvt_12_1_ - 0.125F), (double)lvt_13_1_, (double)(lvt_14_1_ - 0.125F)).tex(0.0D, 0.0D).color(lvt_15_1_, lvt_15_1_, lvt_15_1_, lvt_10_1_).endVertex();
        Tessellator.getInstance().draw();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        ++this.footstepAge;

        if (this.footstepAge == this.footstepMaxAge)
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
            return new EntityFootStepFX(Minecraft.getMinecraft().getTextureManager(), worldIn, xCoordIn, yCoordIn, zCoordIn);
        }
    }
}
