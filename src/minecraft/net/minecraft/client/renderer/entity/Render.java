package net.minecraft.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public abstract class Render<T extends Entity>
{
    private static final ResourceLocation shadowTextures = new ResourceLocation("textures/misc/shadow.png");
    protected final RenderManager renderManager;
    protected float shadowSize;

    /**
     * Determines the darkness of the object's shadow. Higher value makes a darker shadow.
     */
    protected float shadowOpaque = 1.0F;

    protected Render(RenderManager renderManager)
    {
        this.renderManager = renderManager;
    }

    public boolean shouldRender(T livingEntity, ICamera camera, double camX, double camY, double camZ)
    {
        AxisAlignedBB lvt_9_1_ = livingEntity.getEntityBoundingBox();

        if (lvt_9_1_.hasNaN() || lvt_9_1_.getAverageEdgeLength() == 0.0D)
        {
            lvt_9_1_ = new AxisAlignedBB(livingEntity.posX - 2.0D, livingEntity.posY - 2.0D, livingEntity.posZ - 2.0D, livingEntity.posX + 2.0D, livingEntity.posY + 2.0D, livingEntity.posZ + 2.0D);
        }

        return livingEntity.isInRangeToRender3d(camX, camY, camZ) && (livingEntity.ignoreFrustumCheck || camera.isBoundingBoxInFrustum(lvt_9_1_));
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.renderName(entity, x, y, z);
    }

    protected void renderName(T entity, double x, double y, double z)
    {
        if (this.canRenderName(entity))
        {
            this.renderLivingLabel(entity, entity.getDisplayName().getFormattedText(), x, y, z, 64);
        }
    }

    protected boolean canRenderName(T entity)
    {
        return entity.getAlwaysRenderNameTagForRender() && entity.hasCustomName();
    }

    protected void renderOffsetLivingLabel(T entityIn, double x, double y, double z, String str, float p_177069_9_, double p_177069_10_)
    {
        this.renderLivingLabel(entityIn, str, x, y, z, 64);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected abstract ResourceLocation getEntityTexture(T entity);

    protected boolean bindEntityTexture(T entity)
    {
        ResourceLocation lvt_2_1_ = this.getEntityTexture(entity);

        if (lvt_2_1_ == null)
        {
            return false;
        }
        else
        {
            this.bindTexture(lvt_2_1_);
            return true;
        }
    }

    public void bindTexture(ResourceLocation location)
    {
        this.renderManager.renderEngine.bindTexture(location);
    }

    /**
     * Renders fire on top of the entity. Args: entity, x, y, z, partialTickTime
     */
    private void renderEntityOnFire(Entity entity, double x, double y, double z, float partialTicks)
    {
        GlStateManager.disableLighting();
        TextureMap lvt_9_1_ = Minecraft.getMinecraft().getTextureMapBlocks();
        TextureAtlasSprite lvt_10_1_ = lvt_9_1_.getAtlasSprite("minecraft:blocks/fire_layer_0");
        TextureAtlasSprite lvt_11_1_ = lvt_9_1_.getAtlasSprite("minecraft:blocks/fire_layer_1");
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y, (float)z);
        float lvt_12_1_ = entity.width * 1.4F;
        GlStateManager.scale(lvt_12_1_, lvt_12_1_, lvt_12_1_);
        Tessellator lvt_13_1_ = Tessellator.getInstance();
        WorldRenderer lvt_14_1_ = lvt_13_1_.getWorldRenderer();
        float lvt_15_1_ = 0.5F;
        float lvt_16_1_ = 0.0F;
        float lvt_17_1_ = entity.height / lvt_12_1_;
        float lvt_18_1_ = (float)(entity.posY - entity.getEntityBoundingBox().minY);
        GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0.0F, 0.0F, -0.3F + (float)((int)lvt_17_1_) * 0.02F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        float lvt_19_1_ = 0.0F;
        int lvt_20_1_ = 0;
        lvt_14_1_.begin(7, DefaultVertexFormats.POSITION_TEX);

        while (lvt_17_1_ > 0.0F)
        {
            TextureAtlasSprite lvt_21_1_ = lvt_20_1_ % 2 == 0 ? lvt_10_1_ : lvt_11_1_;
            this.bindTexture(TextureMap.locationBlocksTexture);
            float lvt_22_1_ = lvt_21_1_.getMinU();
            float lvt_23_1_ = lvt_21_1_.getMinV();
            float lvt_24_1_ = lvt_21_1_.getMaxU();
            float lvt_25_1_ = lvt_21_1_.getMaxV();

            if (lvt_20_1_ / 2 % 2 == 0)
            {
                float lvt_26_1_ = lvt_24_1_;
                lvt_24_1_ = lvt_22_1_;
                lvt_22_1_ = lvt_26_1_;
            }

            lvt_14_1_.pos((double)(lvt_15_1_ - lvt_16_1_), (double)(0.0F - lvt_18_1_), (double)lvt_19_1_).tex((double)lvt_24_1_, (double)lvt_25_1_).endVertex();
            lvt_14_1_.pos((double)(-lvt_15_1_ - lvt_16_1_), (double)(0.0F - lvt_18_1_), (double)lvt_19_1_).tex((double)lvt_22_1_, (double)lvt_25_1_).endVertex();
            lvt_14_1_.pos((double)(-lvt_15_1_ - lvt_16_1_), (double)(1.4F - lvt_18_1_), (double)lvt_19_1_).tex((double)lvt_22_1_, (double)lvt_23_1_).endVertex();
            lvt_14_1_.pos((double)(lvt_15_1_ - lvt_16_1_), (double)(1.4F - lvt_18_1_), (double)lvt_19_1_).tex((double)lvt_24_1_, (double)lvt_23_1_).endVertex();
            lvt_17_1_ -= 0.45F;
            lvt_18_1_ -= 0.45F;
            lvt_15_1_ *= 0.9F;
            lvt_19_1_ += 0.03F;
            ++lvt_20_1_;
        }

        lvt_13_1_.draw();
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
    }

    /**
     * Renders the entity shadows at the position, shadow alpha and partialTickTime. Args: entity, x, y, z, shadowAlpha,
     * partialTickTime
     */
    private void renderShadow(Entity entityIn, double x, double y, double z, float shadowAlpha, float partialTicks)
    {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        this.renderManager.renderEngine.bindTexture(shadowTextures);
        World lvt_10_1_ = this.getWorldFromRenderManager();
        GlStateManager.depthMask(false);
        float lvt_11_1_ = this.shadowSize;

        if (entityIn instanceof EntityLiving)
        {
            EntityLiving lvt_12_1_ = (EntityLiving)entityIn;
            lvt_11_1_ *= lvt_12_1_.getRenderSizeModifier();

            if (lvt_12_1_.isChild())
            {
                lvt_11_1_ *= 0.5F;
            }
        }

        double lvt_12_2_ = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double)partialTicks;
        double lvt_14_1_ = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double)partialTicks;
        double lvt_16_1_ = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double)partialTicks;
        int lvt_18_1_ = MathHelper.floor_double(lvt_12_2_ - (double)lvt_11_1_);
        int lvt_19_1_ = MathHelper.floor_double(lvt_12_2_ + (double)lvt_11_1_);
        int lvt_20_1_ = MathHelper.floor_double(lvt_14_1_ - (double)lvt_11_1_);
        int lvt_21_1_ = MathHelper.floor_double(lvt_14_1_);
        int lvt_22_1_ = MathHelper.floor_double(lvt_16_1_ - (double)lvt_11_1_);
        int lvt_23_1_ = MathHelper.floor_double(lvt_16_1_ + (double)lvt_11_1_);
        double lvt_24_1_ = x - lvt_12_2_;
        double lvt_26_1_ = y - lvt_14_1_;
        double lvt_28_1_ = z - lvt_16_1_;
        Tessellator lvt_30_1_ = Tessellator.getInstance();
        WorldRenderer lvt_31_1_ = lvt_30_1_.getWorldRenderer();
        lvt_31_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

        for (BlockPos lvt_33_1_ : BlockPos.getAllInBoxMutable(new BlockPos(lvt_18_1_, lvt_20_1_, lvt_22_1_), new BlockPos(lvt_19_1_, lvt_21_1_, lvt_23_1_)))
        {
            Block lvt_34_1_ = lvt_10_1_.getBlockState(lvt_33_1_.down()).getBlock();

            if (lvt_34_1_.getRenderType() != -1 && lvt_10_1_.getLightFromNeighbors(lvt_33_1_) > 3)
            {
                this.renderShadowBlock(lvt_34_1_, x, y, z, lvt_33_1_, shadowAlpha, lvt_11_1_, lvt_24_1_, lvt_26_1_, lvt_28_1_);
            }
        }

        lvt_30_1_.draw();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
    }

    /**
     * Returns the render manager's world object
     */
    private World getWorldFromRenderManager()
    {
        return this.renderManager.worldObj;
    }

    private void renderShadowBlock(Block blockIn, double p_180549_2_, double p_180549_4_, double p_180549_6_, BlockPos pos, float p_180549_9_, float p_180549_10_, double p_180549_11_, double p_180549_13_, double p_180549_15_)
    {
        if (blockIn.isFullCube())
        {
            Tessellator lvt_17_1_ = Tessellator.getInstance();
            WorldRenderer lvt_18_1_ = lvt_17_1_.getWorldRenderer();
            double lvt_19_1_ = ((double)p_180549_9_ - (p_180549_4_ - ((double)pos.getY() + p_180549_13_)) / 2.0D) * 0.5D * (double)this.getWorldFromRenderManager().getLightBrightness(pos);

            if (lvt_19_1_ >= 0.0D)
            {
                if (lvt_19_1_ > 1.0D)
                {
                    lvt_19_1_ = 1.0D;
                }

                double lvt_21_1_ = (double)pos.getX() + blockIn.getBlockBoundsMinX() + p_180549_11_;
                double lvt_23_1_ = (double)pos.getX() + blockIn.getBlockBoundsMaxX() + p_180549_11_;
                double lvt_25_1_ = (double)pos.getY() + blockIn.getBlockBoundsMinY() + p_180549_13_ + 0.015625D;
                double lvt_27_1_ = (double)pos.getZ() + blockIn.getBlockBoundsMinZ() + p_180549_15_;
                double lvt_29_1_ = (double)pos.getZ() + blockIn.getBlockBoundsMaxZ() + p_180549_15_;
                float lvt_31_1_ = (float)((p_180549_2_ - lvt_21_1_) / 2.0D / (double)p_180549_10_ + 0.5D);
                float lvt_32_1_ = (float)((p_180549_2_ - lvt_23_1_) / 2.0D / (double)p_180549_10_ + 0.5D);
                float lvt_33_1_ = (float)((p_180549_6_ - lvt_27_1_) / 2.0D / (double)p_180549_10_ + 0.5D);
                float lvt_34_1_ = (float)((p_180549_6_ - lvt_29_1_) / 2.0D / (double)p_180549_10_ + 0.5D);
                lvt_18_1_.pos(lvt_21_1_, lvt_25_1_, lvt_27_1_).tex((double)lvt_31_1_, (double)lvt_33_1_).color(1.0F, 1.0F, 1.0F, (float)lvt_19_1_).endVertex();
                lvt_18_1_.pos(lvt_21_1_, lvt_25_1_, lvt_29_1_).tex((double)lvt_31_1_, (double)lvt_34_1_).color(1.0F, 1.0F, 1.0F, (float)lvt_19_1_).endVertex();
                lvt_18_1_.pos(lvt_23_1_, lvt_25_1_, lvt_29_1_).tex((double)lvt_32_1_, (double)lvt_34_1_).color(1.0F, 1.0F, 1.0F, (float)lvt_19_1_).endVertex();
                lvt_18_1_.pos(lvt_23_1_, lvt_25_1_, lvt_27_1_).tex((double)lvt_32_1_, (double)lvt_33_1_).color(1.0F, 1.0F, 1.0F, (float)lvt_19_1_).endVertex();
            }
        }
    }

    /**
     * Renders a white box with the bounds of the AABB translated by the offset. Args: aabb, x, y, z
     */
    public static void renderOffsetAABB(AxisAlignedBB boundingBox, double x, double y, double z)
    {
        GlStateManager.disableTexture2D();
        Tessellator lvt_7_1_ = Tessellator.getInstance();
        WorldRenderer lvt_8_1_ = lvt_7_1_.getWorldRenderer();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        lvt_8_1_.setTranslation(x, y, z);
        lvt_8_1_.begin(7, DefaultVertexFormats.POSITION_NORMAL);
        lvt_8_1_.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
        lvt_8_1_.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
        lvt_8_1_.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
        lvt_8_1_.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
        lvt_8_1_.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
        lvt_8_1_.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
        lvt_8_1_.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
        lvt_8_1_.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
        lvt_8_1_.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).normal(0.0F, -1.0F, 0.0F).endVertex();
        lvt_8_1_.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).normal(0.0F, -1.0F, 0.0F).endVertex();
        lvt_8_1_.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).normal(0.0F, -1.0F, 0.0F).endVertex();
        lvt_8_1_.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).normal(0.0F, -1.0F, 0.0F).endVertex();
        lvt_8_1_.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).normal(0.0F, 1.0F, 0.0F).endVertex();
        lvt_8_1_.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).normal(0.0F, 1.0F, 0.0F).endVertex();
        lvt_8_1_.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).normal(0.0F, 1.0F, 0.0F).endVertex();
        lvt_8_1_.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).normal(0.0F, 1.0F, 0.0F).endVertex();
        lvt_8_1_.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
        lvt_8_1_.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
        lvt_8_1_.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
        lvt_8_1_.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
        lvt_8_1_.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).normal(1.0F, 0.0F, 0.0F).endVertex();
        lvt_8_1_.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).normal(1.0F, 0.0F, 0.0F).endVertex();
        lvt_8_1_.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).normal(1.0F, 0.0F, 0.0F).endVertex();
        lvt_8_1_.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).normal(1.0F, 0.0F, 0.0F).endVertex();
        lvt_7_1_.draw();
        lvt_8_1_.setTranslation(0.0D, 0.0D, 0.0D);
        GlStateManager.enableTexture2D();
    }

    /**
     * Renders the entity's shadow and fire (if its on fire). Args: entity, x, y, z, yaw, partialTickTime
     */
    public void doRenderShadowAndFire(Entity entityIn, double x, double y, double z, float yaw, float partialTicks)
    {
        if (this.renderManager.options != null)
        {
            if (this.renderManager.options.entityShadows && this.shadowSize > 0.0F && !entityIn.isInvisible() && this.renderManager.isRenderShadow())
            {
                double lvt_10_1_ = this.renderManager.getDistanceToCamera(entityIn.posX, entityIn.posY, entityIn.posZ);
                float lvt_12_1_ = (float)((1.0D - lvt_10_1_ / 256.0D) * (double)this.shadowOpaque);

                if (lvt_12_1_ > 0.0F)
                {
                    this.renderShadow(entityIn, x, y, z, lvt_12_1_, partialTicks);
                }
            }

            if (entityIn.canRenderOnFire() && (!(entityIn instanceof EntityPlayer) || !((EntityPlayer)entityIn).isSpectator()))
            {
                this.renderEntityOnFire(entityIn, x, y, z, partialTicks);
            }
        }
    }

    /**
     * Returns the font renderer from the set render manager
     */
    public FontRenderer getFontRendererFromRenderManager()
    {
        return this.renderManager.getFontRenderer();
    }

    /**
     * Renders an entity's name above its head
     */
    protected void renderLivingLabel(T entityIn, String str, double x, double y, double z, int maxDistance)
    {
        double lvt_10_1_ = entityIn.getDistanceSqToEntity(this.renderManager.livingPlayer);

        if (lvt_10_1_ <= (double)(maxDistance * maxDistance))
        {
            FontRenderer lvt_12_1_ = this.getFontRendererFromRenderManager();
            float lvt_13_1_ = 1.6F;
            float lvt_14_1_ = 0.016666668F * lvt_13_1_;
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)x + 0.0F, (float)y + entityIn.height + 0.5F, (float)z);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(-lvt_14_1_, -lvt_14_1_, lvt_14_1_);
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            Tessellator lvt_15_1_ = Tessellator.getInstance();
            WorldRenderer lvt_16_1_ = lvt_15_1_.getWorldRenderer();
            int lvt_17_1_ = 0;

            if (str.equals("deadmau5"))
            {
                lvt_17_1_ = -10;
            }

            int lvt_18_1_ = lvt_12_1_.getStringWidth(str) / 2;
            GlStateManager.disableTexture2D();
            lvt_16_1_.begin(7, DefaultVertexFormats.POSITION_COLOR);
            lvt_16_1_.pos((double)(-lvt_18_1_ - 1), (double)(-1 + lvt_17_1_), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            lvt_16_1_.pos((double)(-lvt_18_1_ - 1), (double)(8 + lvt_17_1_), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            lvt_16_1_.pos((double)(lvt_18_1_ + 1), (double)(8 + lvt_17_1_), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            lvt_16_1_.pos((double)(lvt_18_1_ + 1), (double)(-1 + lvt_17_1_), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            lvt_15_1_.draw();
            GlStateManager.enableTexture2D();
            lvt_12_1_.drawString(str, -lvt_12_1_.getStringWidth(str) / 2, lvt_17_1_, 553648127);
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            lvt_12_1_.drawString(str, -lvt_12_1_.getStringWidth(str) / 2, lvt_17_1_, -1);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }

    public RenderManager getRenderManager()
    {
        return this.renderManager;
    }
}
