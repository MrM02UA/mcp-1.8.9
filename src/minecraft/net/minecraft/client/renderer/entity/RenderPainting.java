package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderPainting extends Render<EntityPainting>
{
    private static final ResourceLocation KRISTOFFER_PAINTING_TEXTURE = new ResourceLocation("textures/painting/paintings_kristoffer_zetterstrand.png");

    public RenderPainting(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityPainting entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.enableRescaleNormal();
        this.bindEntityTexture(entity);
        EntityPainting.EnumArt lvt_10_1_ = entity.art;
        float lvt_11_1_ = 0.0625F;
        GlStateManager.scale(lvt_11_1_, lvt_11_1_, lvt_11_1_);
        this.renderPainting(entity, lvt_10_1_.sizeX, lvt_10_1_.sizeY, lvt_10_1_.offsetX, lvt_10_1_.offsetY);
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityPainting entity)
    {
        return KRISTOFFER_PAINTING_TEXTURE;
    }

    private void renderPainting(EntityPainting painting, int width, int height, int textureU, int textureV)
    {
        float lvt_6_1_ = (float)(-width) / 2.0F;
        float lvt_7_1_ = (float)(-height) / 2.0F;
        float lvt_8_1_ = 0.5F;
        float lvt_9_1_ = 0.75F;
        float lvt_10_1_ = 0.8125F;
        float lvt_11_1_ = 0.0F;
        float lvt_12_1_ = 0.0625F;
        float lvt_13_1_ = 0.75F;
        float lvt_14_1_ = 0.8125F;
        float lvt_15_1_ = 0.001953125F;
        float lvt_16_1_ = 0.001953125F;
        float lvt_17_1_ = 0.7519531F;
        float lvt_18_1_ = 0.7519531F;
        float lvt_19_1_ = 0.0F;
        float lvt_20_1_ = 0.0625F;

        for (int lvt_21_1_ = 0; lvt_21_1_ < width / 16; ++lvt_21_1_)
        {
            for (int lvt_22_1_ = 0; lvt_22_1_ < height / 16; ++lvt_22_1_)
            {
                float lvt_23_1_ = lvt_6_1_ + (float)((lvt_21_1_ + 1) * 16);
                float lvt_24_1_ = lvt_6_1_ + (float)(lvt_21_1_ * 16);
                float lvt_25_1_ = lvt_7_1_ + (float)((lvt_22_1_ + 1) * 16);
                float lvt_26_1_ = lvt_7_1_ + (float)(lvt_22_1_ * 16);
                this.setLightmap(painting, (lvt_23_1_ + lvt_24_1_) / 2.0F, (lvt_25_1_ + lvt_26_1_) / 2.0F);
                float lvt_27_1_ = (float)(textureU + width - lvt_21_1_ * 16) / 256.0F;
                float lvt_28_1_ = (float)(textureU + width - (lvt_21_1_ + 1) * 16) / 256.0F;
                float lvt_29_1_ = (float)(textureV + height - lvt_22_1_ * 16) / 256.0F;
                float lvt_30_1_ = (float)(textureV + height - (lvt_22_1_ + 1) * 16) / 256.0F;
                Tessellator lvt_31_1_ = Tessellator.getInstance();
                WorldRenderer lvt_32_1_ = lvt_31_1_.getWorldRenderer();
                lvt_32_1_.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
                lvt_32_1_.pos((double)lvt_23_1_, (double)lvt_26_1_, (double)(-lvt_8_1_)).tex((double)lvt_28_1_, (double)lvt_29_1_).normal(0.0F, 0.0F, -1.0F).endVertex();
                lvt_32_1_.pos((double)lvt_24_1_, (double)lvt_26_1_, (double)(-lvt_8_1_)).tex((double)lvt_27_1_, (double)lvt_29_1_).normal(0.0F, 0.0F, -1.0F).endVertex();
                lvt_32_1_.pos((double)lvt_24_1_, (double)lvt_25_1_, (double)(-lvt_8_1_)).tex((double)lvt_27_1_, (double)lvt_30_1_).normal(0.0F, 0.0F, -1.0F).endVertex();
                lvt_32_1_.pos((double)lvt_23_1_, (double)lvt_25_1_, (double)(-lvt_8_1_)).tex((double)lvt_28_1_, (double)lvt_30_1_).normal(0.0F, 0.0F, -1.0F).endVertex();
                lvt_32_1_.pos((double)lvt_23_1_, (double)lvt_25_1_, (double)lvt_8_1_).tex((double)lvt_9_1_, (double)lvt_11_1_).normal(0.0F, 0.0F, 1.0F).endVertex();
                lvt_32_1_.pos((double)lvt_24_1_, (double)lvt_25_1_, (double)lvt_8_1_).tex((double)lvt_10_1_, (double)lvt_11_1_).normal(0.0F, 0.0F, 1.0F).endVertex();
                lvt_32_1_.pos((double)lvt_24_1_, (double)lvt_26_1_, (double)lvt_8_1_).tex((double)lvt_10_1_, (double)lvt_12_1_).normal(0.0F, 0.0F, 1.0F).endVertex();
                lvt_32_1_.pos((double)lvt_23_1_, (double)lvt_26_1_, (double)lvt_8_1_).tex((double)lvt_9_1_, (double)lvt_12_1_).normal(0.0F, 0.0F, 1.0F).endVertex();
                lvt_32_1_.pos((double)lvt_23_1_, (double)lvt_25_1_, (double)(-lvt_8_1_)).tex((double)lvt_13_1_, (double)lvt_15_1_).normal(0.0F, 1.0F, 0.0F).endVertex();
                lvt_32_1_.pos((double)lvt_24_1_, (double)lvt_25_1_, (double)(-lvt_8_1_)).tex((double)lvt_14_1_, (double)lvt_15_1_).normal(0.0F, 1.0F, 0.0F).endVertex();
                lvt_32_1_.pos((double)lvt_24_1_, (double)lvt_25_1_, (double)lvt_8_1_).tex((double)lvt_14_1_, (double)lvt_16_1_).normal(0.0F, 1.0F, 0.0F).endVertex();
                lvt_32_1_.pos((double)lvt_23_1_, (double)lvt_25_1_, (double)lvt_8_1_).tex((double)lvt_13_1_, (double)lvt_16_1_).normal(0.0F, 1.0F, 0.0F).endVertex();
                lvt_32_1_.pos((double)lvt_23_1_, (double)lvt_26_1_, (double)lvt_8_1_).tex((double)lvt_13_1_, (double)lvt_15_1_).normal(0.0F, -1.0F, 0.0F).endVertex();
                lvt_32_1_.pos((double)lvt_24_1_, (double)lvt_26_1_, (double)lvt_8_1_).tex((double)lvt_14_1_, (double)lvt_15_1_).normal(0.0F, -1.0F, 0.0F).endVertex();
                lvt_32_1_.pos((double)lvt_24_1_, (double)lvt_26_1_, (double)(-lvt_8_1_)).tex((double)lvt_14_1_, (double)lvt_16_1_).normal(0.0F, -1.0F, 0.0F).endVertex();
                lvt_32_1_.pos((double)lvt_23_1_, (double)lvt_26_1_, (double)(-lvt_8_1_)).tex((double)lvt_13_1_, (double)lvt_16_1_).normal(0.0F, -1.0F, 0.0F).endVertex();
                lvt_32_1_.pos((double)lvt_23_1_, (double)lvt_25_1_, (double)lvt_8_1_).tex((double)lvt_18_1_, (double)lvt_19_1_).normal(-1.0F, 0.0F, 0.0F).endVertex();
                lvt_32_1_.pos((double)lvt_23_1_, (double)lvt_26_1_, (double)lvt_8_1_).tex((double)lvt_18_1_, (double)lvt_20_1_).normal(-1.0F, 0.0F, 0.0F).endVertex();
                lvt_32_1_.pos((double)lvt_23_1_, (double)lvt_26_1_, (double)(-lvt_8_1_)).tex((double)lvt_17_1_, (double)lvt_20_1_).normal(-1.0F, 0.0F, 0.0F).endVertex();
                lvt_32_1_.pos((double)lvt_23_1_, (double)lvt_25_1_, (double)(-lvt_8_1_)).tex((double)lvt_17_1_, (double)lvt_19_1_).normal(-1.0F, 0.0F, 0.0F).endVertex();
                lvt_32_1_.pos((double)lvt_24_1_, (double)lvt_25_1_, (double)(-lvt_8_1_)).tex((double)lvt_18_1_, (double)lvt_19_1_).normal(1.0F, 0.0F, 0.0F).endVertex();
                lvt_32_1_.pos((double)lvt_24_1_, (double)lvt_26_1_, (double)(-lvt_8_1_)).tex((double)lvt_18_1_, (double)lvt_20_1_).normal(1.0F, 0.0F, 0.0F).endVertex();
                lvt_32_1_.pos((double)lvt_24_1_, (double)lvt_26_1_, (double)lvt_8_1_).tex((double)lvt_17_1_, (double)lvt_20_1_).normal(1.0F, 0.0F, 0.0F).endVertex();
                lvt_32_1_.pos((double)lvt_24_1_, (double)lvt_25_1_, (double)lvt_8_1_).tex((double)lvt_17_1_, (double)lvt_19_1_).normal(1.0F, 0.0F, 0.0F).endVertex();
                lvt_31_1_.draw();
            }
        }
    }

    private void setLightmap(EntityPainting painting, float p_77008_2_, float p_77008_3_)
    {
        int lvt_4_1_ = MathHelper.floor_double(painting.posX);
        int lvt_5_1_ = MathHelper.floor_double(painting.posY + (double)(p_77008_3_ / 16.0F));
        int lvt_6_1_ = MathHelper.floor_double(painting.posZ);
        EnumFacing lvt_7_1_ = painting.facingDirection;

        if (lvt_7_1_ == EnumFacing.NORTH)
        {
            lvt_4_1_ = MathHelper.floor_double(painting.posX + (double)(p_77008_2_ / 16.0F));
        }

        if (lvt_7_1_ == EnumFacing.WEST)
        {
            lvt_6_1_ = MathHelper.floor_double(painting.posZ - (double)(p_77008_2_ / 16.0F));
        }

        if (lvt_7_1_ == EnumFacing.SOUTH)
        {
            lvt_4_1_ = MathHelper.floor_double(painting.posX - (double)(p_77008_2_ / 16.0F));
        }

        if (lvt_7_1_ == EnumFacing.EAST)
        {
            lvt_6_1_ = MathHelper.floor_double(painting.posZ + (double)(p_77008_2_ / 16.0F));
        }

        int lvt_8_1_ = this.renderManager.worldObj.getCombinedLight(new BlockPos(lvt_4_1_, lvt_5_1_, lvt_6_1_), 0);
        int lvt_9_1_ = lvt_8_1_ % 65536;
        int lvt_10_1_ = lvt_8_1_ / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)lvt_9_1_, (float)lvt_10_1_);
        GlStateManager.color(1.0F, 1.0F, 1.0F);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return this.getEntityTexture((EntityPainting)entity);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.doRender((EntityPainting)entity, x, y, z, entityYaw, partialTicks);
    }
}
