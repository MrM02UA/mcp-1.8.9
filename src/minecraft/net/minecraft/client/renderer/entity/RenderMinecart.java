package net.minecraft.client.renderer.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelMinecart;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class RenderMinecart<T extends EntityMinecart> extends Render<T>
{
    private static final ResourceLocation minecartTextures = new ResourceLocation("textures/entity/minecart.png");

    /** instance of ModelMinecart for rendering */
    protected ModelBase modelMinecart = new ModelMinecart();

    public RenderMinecart(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
        this.shadowSize = 0.5F;
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        GlStateManager.pushMatrix();
        this.bindEntityTexture(entity);
        long lvt_10_1_ = (long)entity.getEntityId() * 493286711L;
        lvt_10_1_ = lvt_10_1_ * lvt_10_1_ * 4392167121L + lvt_10_1_ * 98761L;
        float lvt_12_1_ = (((float)(lvt_10_1_ >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float lvt_13_1_ = (((float)(lvt_10_1_ >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float lvt_14_1_ = (((float)(lvt_10_1_ >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        GlStateManager.translate(lvt_12_1_, lvt_13_1_, lvt_14_1_);
        double lvt_15_1_ = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double lvt_17_1_ = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double lvt_19_1_ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
        double lvt_21_1_ = 0.30000001192092896D;
        Vec3 lvt_23_1_ = entity.func_70489_a(lvt_15_1_, lvt_17_1_, lvt_19_1_);
        float lvt_24_1_ = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;

        if (lvt_23_1_ != null)
        {
            Vec3 lvt_25_1_ = entity.func_70495_a(lvt_15_1_, lvt_17_1_, lvt_19_1_, lvt_21_1_);
            Vec3 lvt_26_1_ = entity.func_70495_a(lvt_15_1_, lvt_17_1_, lvt_19_1_, -lvt_21_1_);

            if (lvt_25_1_ == null)
            {
                lvt_25_1_ = lvt_23_1_;
            }

            if (lvt_26_1_ == null)
            {
                lvt_26_1_ = lvt_23_1_;
            }

            x += lvt_23_1_.xCoord - lvt_15_1_;
            y += (lvt_25_1_.yCoord + lvt_26_1_.yCoord) / 2.0D - lvt_17_1_;
            z += lvt_23_1_.zCoord - lvt_19_1_;
            Vec3 lvt_27_1_ = lvt_26_1_.addVector(-lvt_25_1_.xCoord, -lvt_25_1_.yCoord, -lvt_25_1_.zCoord);

            if (lvt_27_1_.lengthVector() != 0.0D)
            {
                lvt_27_1_ = lvt_27_1_.normalize();
                entityYaw = (float)(Math.atan2(lvt_27_1_.zCoord, lvt_27_1_.xCoord) * 180.0D / Math.PI);
                lvt_24_1_ = (float)(Math.atan(lvt_27_1_.yCoord) * 73.0D);
            }
        }

        GlStateManager.translate((float)x, (float)y + 0.375F, (float)z);
        GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-lvt_24_1_, 0.0F, 0.0F, 1.0F);
        float lvt_25_2_ = (float)entity.getRollingAmplitude() - partialTicks;
        float lvt_26_2_ = entity.getDamage() - partialTicks;

        if (lvt_26_2_ < 0.0F)
        {
            lvt_26_2_ = 0.0F;
        }

        if (lvt_25_2_ > 0.0F)
        {
            GlStateManager.rotate(MathHelper.sin(lvt_25_2_) * lvt_25_2_ * lvt_26_2_ / 10.0F * (float)entity.getRollingDirection(), 1.0F, 0.0F, 0.0F);
        }

        int lvt_27_2_ = entity.getDisplayTileOffset();
        IBlockState lvt_28_1_ = entity.getDisplayTile();

        if (lvt_28_1_.getBlock().getRenderType() != -1)
        {
            GlStateManager.pushMatrix();
            this.bindTexture(TextureMap.locationBlocksTexture);
            float lvt_29_1_ = 0.75F;
            GlStateManager.scale(lvt_29_1_, lvt_29_1_, lvt_29_1_);
            GlStateManager.translate(-0.5F, (float)(lvt_27_2_ - 8) / 16.0F, 0.5F);
            this.func_180560_a(entity, partialTicks, lvt_28_1_);
            GlStateManager.popMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.bindEntityTexture(entity);
        }

        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        this.modelMinecart.render(entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(T entity)
    {
        return minecartTextures;
    }

    protected void func_180560_a(T minecart, float partialTicks, IBlockState state)
    {
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(state, minecart.getBrightness(partialTicks));
        GlStateManager.popMatrix();
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return this.getEntityTexture((EntityMinecart)entity);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.doRender((EntityMinecart)entity, x, y, z, entityYaw, partialTicks);
    }
}
