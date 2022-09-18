package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureCompass;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import org.lwjgl.opengl.GL11;

public class RenderItemFrame extends Render<EntityItemFrame>
{
    private static final ResourceLocation mapBackgroundTextures = new ResourceLocation("textures/map/map_background.png");
    private final Minecraft mc = Minecraft.getMinecraft();
    private final ModelResourceLocation itemFrameModel = new ModelResourceLocation("item_frame", "normal");
    private final ModelResourceLocation mapModel = new ModelResourceLocation("item_frame", "map");
    private RenderItem itemRenderer;

    public RenderItemFrame(RenderManager renderManagerIn, RenderItem itemRendererIn)
    {
        super(renderManagerIn);
        this.itemRenderer = itemRendererIn;
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityItemFrame entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        GlStateManager.pushMatrix();
        BlockPos lvt_10_1_ = entity.getHangingPosition();
        double lvt_11_1_ = (double)lvt_10_1_.getX() - entity.posX + x;
        double lvt_13_1_ = (double)lvt_10_1_.getY() - entity.posY + y;
        double lvt_15_1_ = (double)lvt_10_1_.getZ() - entity.posZ + z;
        GlStateManager.translate(lvt_11_1_ + 0.5D, lvt_13_1_ + 0.5D, lvt_15_1_ + 0.5D);
        GlStateManager.rotate(180.0F - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
        this.renderManager.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        BlockRendererDispatcher lvt_17_1_ = this.mc.getBlockRendererDispatcher();
        ModelManager lvt_18_1_ = lvt_17_1_.getBlockModelShapes().getModelManager();
        IBakedModel lvt_19_1_;

        if (entity.getDisplayedItem() != null && entity.getDisplayedItem().getItem() == Items.filled_map)
        {
            lvt_19_1_ = lvt_18_1_.getModel(this.mapModel);
        }
        else
        {
            lvt_19_1_ = lvt_18_1_.getModel(this.itemFrameModel);
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
        lvt_17_1_.getBlockModelRenderer().renderModelBrightnessColor(lvt_19_1_, 1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
        GlStateManager.translate(0.0F, 0.0F, 0.4375F);
        this.renderItem(entity);
        GlStateManager.popMatrix();
        this.renderName(entity, x + (double)((float)entity.facingDirection.getFrontOffsetX() * 0.3F), y - 0.25D, z + (double)((float)entity.facingDirection.getFrontOffsetZ() * 0.3F));
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityItemFrame entity)
    {
        return null;
    }

    private void renderItem(EntityItemFrame itemFrame)
    {
        ItemStack lvt_2_1_ = itemFrame.getDisplayedItem();

        if (lvt_2_1_ != null)
        {
            EntityItem lvt_3_1_ = new EntityItem(itemFrame.worldObj, 0.0D, 0.0D, 0.0D, lvt_2_1_);
            Item lvt_4_1_ = lvt_3_1_.getEntityItem().getItem();
            lvt_3_1_.getEntityItem().stackSize = 1;
            lvt_3_1_.hoverStart = 0.0F;
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            int lvt_5_1_ = itemFrame.getRotation();

            if (lvt_4_1_ == Items.filled_map)
            {
                lvt_5_1_ = lvt_5_1_ % 4 * 2;
            }

            GlStateManager.rotate((float)lvt_5_1_ * 360.0F / 8.0F, 0.0F, 0.0F, 1.0F);

            if (lvt_4_1_ == Items.filled_map)
            {
                this.renderManager.renderEngine.bindTexture(mapBackgroundTextures);
                GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
                float lvt_6_1_ = 0.0078125F;
                GlStateManager.scale(lvt_6_1_, lvt_6_1_, lvt_6_1_);
                GlStateManager.translate(-64.0F, -64.0F, 0.0F);
                MapData lvt_7_1_ = Items.filled_map.getMapData(lvt_3_1_.getEntityItem(), itemFrame.worldObj);
                GlStateManager.translate(0.0F, 0.0F, -1.0F);

                if (lvt_7_1_ != null)
                {
                    this.mc.entityRenderer.getMapItemRenderer().renderMap(lvt_7_1_, true);
                }
            }
            else
            {
                TextureAtlasSprite lvt_6_2_ = null;

                if (lvt_4_1_ == Items.compass)
                {
                    lvt_6_2_ = this.mc.getTextureMapBlocks().getAtlasSprite(TextureCompass.locationSprite);
                    this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);

                    if (lvt_6_2_ instanceof TextureCompass)
                    {
                        TextureCompass lvt_7_2_ = (TextureCompass)lvt_6_2_;
                        double lvt_8_1_ = lvt_7_2_.currentAngle;
                        double lvt_10_1_ = lvt_7_2_.angleDelta;
                        lvt_7_2_.currentAngle = 0.0D;
                        lvt_7_2_.angleDelta = 0.0D;
                        lvt_7_2_.updateCompass(itemFrame.worldObj, itemFrame.posX, itemFrame.posZ, (double)MathHelper.wrapAngleTo180_float((float)(180 + itemFrame.facingDirection.getHorizontalIndex() * 90)), false, true);
                        lvt_7_2_.currentAngle = lvt_8_1_;
                        lvt_7_2_.angleDelta = lvt_10_1_;
                    }
                    else
                    {
                        lvt_6_2_ = null;
                    }
                }

                GlStateManager.scale(0.5F, 0.5F, 0.5F);

                if (!this.itemRenderer.shouldRenderItemIn3D(lvt_3_1_.getEntityItem()) || lvt_4_1_ instanceof ItemSkull)
                {
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                }

                GlStateManager.pushAttrib();
                RenderHelper.enableStandardItemLighting();
                this.itemRenderer.renderItem(lvt_3_1_.getEntityItem(), ItemCameraTransforms.TransformType.FIXED);
                RenderHelper.disableStandardItemLighting();
                GlStateManager.popAttrib();

                if (lvt_6_2_ != null && lvt_6_2_.getFrameCount() > 0)
                {
                    lvt_6_2_.updateAnimation();
                }
            }

            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }

    protected void renderName(EntityItemFrame entity, double x, double y, double z)
    {
        if (Minecraft.isGuiEnabled() && entity.getDisplayedItem() != null && entity.getDisplayedItem().hasDisplayName() && this.renderManager.pointedEntity == entity)
        {
            float lvt_8_1_ = 1.6F;
            float lvt_9_1_ = 0.016666668F * lvt_8_1_;
            double lvt_10_1_ = entity.getDistanceSqToEntity(this.renderManager.livingPlayer);
            float lvt_12_1_ = entity.isSneaking() ? 32.0F : 64.0F;

            if (lvt_10_1_ < (double)(lvt_12_1_ * lvt_12_1_))
            {
                String lvt_13_1_ = entity.getDisplayedItem().getDisplayName();

                if (entity.isSneaking())
                {
                    FontRenderer lvt_14_1_ = this.getFontRendererFromRenderManager();
                    GlStateManager.pushMatrix();
                    GlStateManager.translate((float)x + 0.0F, (float)y + entity.height + 0.5F, (float)z);
                    GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                    GlStateManager.scale(-lvt_9_1_, -lvt_9_1_, lvt_9_1_);
                    GlStateManager.disableLighting();
                    GlStateManager.translate(0.0F, 0.25F / lvt_9_1_, 0.0F);
                    GlStateManager.depthMask(false);
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(770, 771);
                    Tessellator lvt_15_1_ = Tessellator.getInstance();
                    WorldRenderer lvt_16_1_ = lvt_15_1_.getWorldRenderer();
                    int lvt_17_1_ = lvt_14_1_.getStringWidth(lvt_13_1_) / 2;
                    GlStateManager.disableTexture2D();
                    lvt_16_1_.begin(7, DefaultVertexFormats.POSITION_COLOR);
                    lvt_16_1_.pos((double)(-lvt_17_1_ - 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                    lvt_16_1_.pos((double)(-lvt_17_1_ - 1), 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                    lvt_16_1_.pos((double)(lvt_17_1_ + 1), 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                    lvt_16_1_.pos((double)(lvt_17_1_ + 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                    lvt_15_1_.draw();
                    GlStateManager.enableTexture2D();
                    GlStateManager.depthMask(true);
                    lvt_14_1_.drawString(lvt_13_1_, -lvt_14_1_.getStringWidth(lvt_13_1_) / 2, 0, 553648127);
                    GlStateManager.enableLighting();
                    GlStateManager.disableBlend();
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.popMatrix();
                }
                else
                {
                    this.renderLivingLabel(entity, lvt_13_1_, x, y, z, 64);
                }
            }
        }
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return this.getEntityTexture((EntityItemFrame)entity);
    }

    protected void renderName(Entity entity, double x, double y, double z)
    {
        this.renderName((EntityItemFrame)entity, x, y, z);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.doRender((EntityItemFrame)entity, x, y, z, entityYaw, partialTicks);
    }
}
