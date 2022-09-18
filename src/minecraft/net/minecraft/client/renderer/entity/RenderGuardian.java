package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelGuardian;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class RenderGuardian extends RenderLiving<EntityGuardian>
{
    private static final ResourceLocation GUARDIAN_TEXTURE = new ResourceLocation("textures/entity/guardian.png");
    private static final ResourceLocation GUARDIAN_ELDER_TEXTURE = new ResourceLocation("textures/entity/guardian_elder.png");
    private static final ResourceLocation GUARDIAN_BEAM_TEXTURE = new ResourceLocation("textures/entity/guardian_beam.png");
    int field_177115_a;

    public RenderGuardian(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelGuardian(), 0.5F);
        this.field_177115_a = ((ModelGuardian)this.mainModel).func_178706_a();
    }

    public boolean shouldRender(EntityGuardian livingEntity, ICamera camera, double camX, double camY, double camZ)
    {
        if (super.shouldRender(livingEntity, camera, camX, camY, camZ))
        {
            return true;
        }
        else
        {
            if (livingEntity.hasTargetedEntity())
            {
                EntityLivingBase lvt_9_1_ = livingEntity.getTargetedEntity();

                if (lvt_9_1_ != null)
                {
                    Vec3 lvt_10_1_ = this.func_177110_a(lvt_9_1_, (double)lvt_9_1_.height * 0.5D, 1.0F);
                    Vec3 lvt_11_1_ = this.func_177110_a(livingEntity, (double)livingEntity.getEyeHeight(), 1.0F);

                    if (camera.isBoundingBoxInFrustum(AxisAlignedBB.fromBounds(lvt_11_1_.xCoord, lvt_11_1_.yCoord, lvt_11_1_.zCoord, lvt_10_1_.xCoord, lvt_10_1_.yCoord, lvt_10_1_.zCoord)))
                    {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    private Vec3 func_177110_a(EntityLivingBase entityLivingBaseIn, double p_177110_2_, float p_177110_4_)
    {
        double lvt_5_1_ = entityLivingBaseIn.lastTickPosX + (entityLivingBaseIn.posX - entityLivingBaseIn.lastTickPosX) * (double)p_177110_4_;
        double lvt_7_1_ = p_177110_2_ + entityLivingBaseIn.lastTickPosY + (entityLivingBaseIn.posY - entityLivingBaseIn.lastTickPosY) * (double)p_177110_4_;
        double lvt_9_1_ = entityLivingBaseIn.lastTickPosZ + (entityLivingBaseIn.posZ - entityLivingBaseIn.lastTickPosZ) * (double)p_177110_4_;
        return new Vec3(lvt_5_1_, lvt_7_1_, lvt_9_1_);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityGuardian entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if (this.field_177115_a != ((ModelGuardian)this.mainModel).func_178706_a())
        {
            this.mainModel = new ModelGuardian();
            this.field_177115_a = ((ModelGuardian)this.mainModel).func_178706_a();
        }

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        EntityLivingBase lvt_10_1_ = entity.getTargetedEntity();

        if (lvt_10_1_ != null)
        {
            float lvt_11_1_ = entity.func_175477_p(partialTicks);
            Tessellator lvt_12_1_ = Tessellator.getInstance();
            WorldRenderer lvt_13_1_ = lvt_12_1_.getWorldRenderer();
            this.bindTexture(GUARDIAN_BEAM_TEXTURE);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            float lvt_14_1_ = 240.0F;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lvt_14_1_, lvt_14_1_);
            GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
            float lvt_15_1_ = (float)entity.worldObj.getTotalWorldTime() + partialTicks;
            float lvt_16_1_ = lvt_15_1_ * 0.5F % 1.0F;
            float lvt_17_1_ = entity.getEyeHeight();
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)x, (float)y + lvt_17_1_, (float)z);
            Vec3 lvt_18_1_ = this.func_177110_a(lvt_10_1_, (double)lvt_10_1_.height * 0.5D, partialTicks);
            Vec3 lvt_19_1_ = this.func_177110_a(entity, (double)lvt_17_1_, partialTicks);
            Vec3 lvt_20_1_ = lvt_18_1_.subtract(lvt_19_1_);
            double lvt_21_1_ = lvt_20_1_.lengthVector() + 1.0D;
            lvt_20_1_ = lvt_20_1_.normalize();
            float lvt_23_1_ = (float)Math.acos(lvt_20_1_.yCoord);
            float lvt_24_1_ = (float)Math.atan2(lvt_20_1_.zCoord, lvt_20_1_.xCoord);
            GlStateManager.rotate((((float)Math.PI / 2F) + -lvt_24_1_) * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(lvt_23_1_ * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
            int lvt_25_1_ = 1;
            double lvt_26_1_ = (double)lvt_15_1_ * 0.05D * (1.0D - (double)(lvt_25_1_ & 1) * 2.5D);
            lvt_13_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            float lvt_28_1_ = lvt_11_1_ * lvt_11_1_;
            int lvt_29_1_ = 64 + (int)(lvt_28_1_ * 240.0F);
            int lvt_30_1_ = 32 + (int)(lvt_28_1_ * 192.0F);
            int lvt_31_1_ = 128 - (int)(lvt_28_1_ * 64.0F);
            double lvt_32_1_ = (double)lvt_25_1_ * 0.2D;
            double lvt_34_1_ = lvt_32_1_ * 1.41D;
            double lvt_36_1_ = 0.0D + Math.cos(lvt_26_1_ + 2.356194490192345D) * lvt_34_1_;
            double lvt_38_1_ = 0.0D + Math.sin(lvt_26_1_ + 2.356194490192345D) * lvt_34_1_;
            double lvt_40_1_ = 0.0D + Math.cos(lvt_26_1_ + (Math.PI / 4D)) * lvt_34_1_;
            double lvt_42_1_ = 0.0D + Math.sin(lvt_26_1_ + (Math.PI / 4D)) * lvt_34_1_;
            double lvt_44_1_ = 0.0D + Math.cos(lvt_26_1_ + 3.9269908169872414D) * lvt_34_1_;
            double lvt_46_1_ = 0.0D + Math.sin(lvt_26_1_ + 3.9269908169872414D) * lvt_34_1_;
            double lvt_48_1_ = 0.0D + Math.cos(lvt_26_1_ + 5.497787143782138D) * lvt_34_1_;
            double lvt_50_1_ = 0.0D + Math.sin(lvt_26_1_ + 5.497787143782138D) * lvt_34_1_;
            double lvt_52_1_ = 0.0D + Math.cos(lvt_26_1_ + Math.PI) * lvt_32_1_;
            double lvt_54_1_ = 0.0D + Math.sin(lvt_26_1_ + Math.PI) * lvt_32_1_;
            double lvt_56_1_ = 0.0D + Math.cos(lvt_26_1_ + 0.0D) * lvt_32_1_;
            double lvt_58_1_ = 0.0D + Math.sin(lvt_26_1_ + 0.0D) * lvt_32_1_;
            double lvt_60_1_ = 0.0D + Math.cos(lvt_26_1_ + (Math.PI / 2D)) * lvt_32_1_;
            double lvt_62_1_ = 0.0D + Math.sin(lvt_26_1_ + (Math.PI / 2D)) * lvt_32_1_;
            double lvt_64_1_ = 0.0D + Math.cos(lvt_26_1_ + (Math.PI * 3D / 2D)) * lvt_32_1_;
            double lvt_66_1_ = 0.0D + Math.sin(lvt_26_1_ + (Math.PI * 3D / 2D)) * lvt_32_1_;
            double lvt_70_1_ = 0.0D;
            double lvt_72_1_ = 0.4999D;
            double lvt_74_1_ = (double)(-1.0F + lvt_16_1_);
            double lvt_76_1_ = lvt_21_1_ * (0.5D / lvt_32_1_) + lvt_74_1_;
            lvt_13_1_.pos(lvt_52_1_, lvt_21_1_, lvt_54_1_).tex(0.4999D, lvt_76_1_).color(lvt_29_1_, lvt_30_1_, lvt_31_1_, 255).endVertex();
            lvt_13_1_.pos(lvt_52_1_, 0.0D, lvt_54_1_).tex(0.4999D, lvt_74_1_).color(lvt_29_1_, lvt_30_1_, lvt_31_1_, 255).endVertex();
            lvt_13_1_.pos(lvt_56_1_, 0.0D, lvt_58_1_).tex(0.0D, lvt_74_1_).color(lvt_29_1_, lvt_30_1_, lvt_31_1_, 255).endVertex();
            lvt_13_1_.pos(lvt_56_1_, lvt_21_1_, lvt_58_1_).tex(0.0D, lvt_76_1_).color(lvt_29_1_, lvt_30_1_, lvt_31_1_, 255).endVertex();
            lvt_13_1_.pos(lvt_60_1_, lvt_21_1_, lvt_62_1_).tex(0.4999D, lvt_76_1_).color(lvt_29_1_, lvt_30_1_, lvt_31_1_, 255).endVertex();
            lvt_13_1_.pos(lvt_60_1_, 0.0D, lvt_62_1_).tex(0.4999D, lvt_74_1_).color(lvt_29_1_, lvt_30_1_, lvt_31_1_, 255).endVertex();
            lvt_13_1_.pos(lvt_64_1_, 0.0D, lvt_66_1_).tex(0.0D, lvt_74_1_).color(lvt_29_1_, lvt_30_1_, lvt_31_1_, 255).endVertex();
            lvt_13_1_.pos(lvt_64_1_, lvt_21_1_, lvt_66_1_).tex(0.0D, lvt_76_1_).color(lvt_29_1_, lvt_30_1_, lvt_31_1_, 255).endVertex();
            double lvt_78_1_ = 0.0D;

            if (entity.ticksExisted % 2 == 0)
            {
                lvt_78_1_ = 0.5D;
            }

            lvt_13_1_.pos(lvt_36_1_, lvt_21_1_, lvt_38_1_).tex(0.5D, lvt_78_1_ + 0.5D).color(lvt_29_1_, lvt_30_1_, lvt_31_1_, 255).endVertex();
            lvt_13_1_.pos(lvt_40_1_, lvt_21_1_, lvt_42_1_).tex(1.0D, lvt_78_1_ + 0.5D).color(lvt_29_1_, lvt_30_1_, lvt_31_1_, 255).endVertex();
            lvt_13_1_.pos(lvt_48_1_, lvt_21_1_, lvt_50_1_).tex(1.0D, lvt_78_1_).color(lvt_29_1_, lvt_30_1_, lvt_31_1_, 255).endVertex();
            lvt_13_1_.pos(lvt_44_1_, lvt_21_1_, lvt_46_1_).tex(0.5D, lvt_78_1_).color(lvt_29_1_, lvt_30_1_, lvt_31_1_, 255).endVertex();
            lvt_12_1_.draw();
            GlStateManager.popMatrix();
        }
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(EntityGuardian entitylivingbaseIn, float partialTickTime)
    {
        if (entitylivingbaseIn.isElder())
        {
            GlStateManager.scale(2.35F, 2.35F, 2.35F);
        }
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityGuardian entity)
    {
        return entity.isElder() ? GUARDIAN_ELDER_TEXTURE : GUARDIAN_TEXTURE;
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityLiving entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.doRender((EntityGuardian)entity, x, y, z, entityYaw, partialTicks);
    }

    public boolean shouldRender(EntityLiving livingEntity, ICamera camera, double camX, double camY, double camZ)
    {
        return this.shouldRender((EntityGuardian)livingEntity, camera, camX, camY, camZ);
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(EntityLivingBase entitylivingbaseIn, float partialTickTime)
    {
        this.preRenderCallback((EntityGuardian)entitylivingbaseIn, partialTickTime);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.doRender((EntityGuardian)entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return this.getEntityTexture((EntityGuardian)entity);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.doRender((EntityGuardian)entity, x, y, z, entityYaw, partialTicks);
    }

    public boolean shouldRender(Entity livingEntity, ICamera camera, double camX, double camY, double camZ)
    {
        return this.shouldRender((EntityGuardian)livingEntity, camera, camX, camY, camZ);
    }
}
