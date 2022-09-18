package net.minecraft.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import org.lwjgl.opengl.GL11;

public class ItemRenderer
{
    private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
    private static final ResourceLocation RES_UNDERWATER_OVERLAY = new ResourceLocation("textures/misc/underwater.png");

    /** A reference to the Minecraft object. */
    private final Minecraft mc;
    private ItemStack itemToRender;

    /**
     * How far the current item has been equipped (0 disequipped and 1 fully up)
     */
    private float equippedProgress;
    private float prevEquippedProgress;
    private final RenderManager renderManager;
    private final RenderItem itemRenderer;

    /** The index of the currently held item (0-8, or -1 if not yet updated) */
    private int equippedItemSlot = -1;

    public ItemRenderer(Minecraft mcIn)
    {
        this.mc = mcIn;
        this.renderManager = mcIn.getRenderManager();
        this.itemRenderer = mcIn.getRenderItem();
    }

    public void renderItem(EntityLivingBase entityIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform)
    {
        if (heldStack != null)
        {
            Item lvt_4_1_ = heldStack.getItem();
            Block lvt_5_1_ = Block.getBlockFromItem(lvt_4_1_);
            GlStateManager.pushMatrix();

            if (this.itemRenderer.shouldRenderItemIn3D(heldStack))
            {
                GlStateManager.scale(2.0F, 2.0F, 2.0F);

                if (this.isBlockTranslucent(lvt_5_1_))
                {
                    GlStateManager.depthMask(false);
                }
            }

            this.itemRenderer.renderItemModelForEntity(heldStack, entityIn, transform);

            if (this.isBlockTranslucent(lvt_5_1_))
            {
                GlStateManager.depthMask(true);
            }

            GlStateManager.popMatrix();
        }
    }

    /**
     * Returns true if given block is translucent
     */
    private boolean isBlockTranslucent(Block blockIn)
    {
        return blockIn != null && blockIn.getBlockLayer() == EnumWorldBlockLayer.TRANSLUCENT;
    }

    /**
     * Rotate the render around X and Y
     *  
     * @param angleY The angle for the rotation arround Y
     */
    private void rotateArroundXAndY(float angle, float angleY)
    {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(angle, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(angleY, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    /**
     * Set the OpenGL LightMapTextureCoords based on the AbstractClientPlayer
     */
    private void setLightMapFromPlayer(AbstractClientPlayer clientPlayer)
    {
        int lvt_2_1_ = this.mc.theWorld.getCombinedLight(new BlockPos(clientPlayer.posX, clientPlayer.posY + (double)clientPlayer.getEyeHeight(), clientPlayer.posZ), 0);
        float lvt_3_1_ = (float)(lvt_2_1_ & 65535);
        float lvt_4_1_ = (float)(lvt_2_1_ >> 16);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lvt_3_1_, lvt_4_1_);
    }

    /**
     * Rotate the render according to the player's yaw and pitch
     */
    private void rotateWithPlayerRotations(EntityPlayerSP entityplayerspIn, float partialTicks)
    {
        float lvt_3_1_ = entityplayerspIn.prevRenderArmPitch + (entityplayerspIn.renderArmPitch - entityplayerspIn.prevRenderArmPitch) * partialTicks;
        float lvt_4_1_ = entityplayerspIn.prevRenderArmYaw + (entityplayerspIn.renderArmYaw - entityplayerspIn.prevRenderArmYaw) * partialTicks;
        GlStateManager.rotate((entityplayerspIn.rotationPitch - lvt_3_1_) * 0.1F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((entityplayerspIn.rotationYaw - lvt_4_1_) * 0.1F, 0.0F, 1.0F, 0.0F);
    }

    /**
     * Return the angle to render the Map
     *  
     * @param pitch The player's pitch
     */
    private float getMapAngleFromPitch(float pitch)
    {
        float lvt_2_1_ = 1.0F - pitch / 45.0F + 0.1F;
        lvt_2_1_ = MathHelper.clamp_float(lvt_2_1_, 0.0F, 1.0F);
        lvt_2_1_ = -MathHelper.cos(lvt_2_1_ * (float)Math.PI) * 0.5F + 0.5F;
        return lvt_2_1_;
    }

    private void renderRightArm(RenderPlayer renderPlayerIn)
    {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(54.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(64.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-62.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(0.25F, -0.85F, 0.75F);
        renderPlayerIn.renderRightArm(this.mc.thePlayer);
        GlStateManager.popMatrix();
    }

    private void renderLeftArm(RenderPlayer renderPlayerIn)
    {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(92.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(41.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(-0.3F, -1.1F, 0.45F);
        renderPlayerIn.renderLeftArm(this.mc.thePlayer);
        GlStateManager.popMatrix();
    }

    private void renderPlayerArms(AbstractClientPlayer clientPlayer)
    {
        this.mc.getTextureManager().bindTexture(clientPlayer.getLocationSkin());
        Render<AbstractClientPlayer> lvt_2_1_ = this.renderManager.<AbstractClientPlayer>getEntityRenderObject(this.mc.thePlayer);
        RenderPlayer lvt_3_1_ = (RenderPlayer)lvt_2_1_;

        if (!clientPlayer.isInvisible())
        {
            GlStateManager.disableCull();
            this.renderRightArm(lvt_3_1_);
            this.renderLeftArm(lvt_3_1_);
            GlStateManager.enableCull();
        }
    }

    private void renderItemMap(AbstractClientPlayer clientPlayer, float pitch, float equipmentProgress, float swingProgress)
    {
        float lvt_5_1_ = -0.4F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
        float lvt_6_1_ = 0.2F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI * 2.0F);
        float lvt_7_1_ = -0.2F * MathHelper.sin(swingProgress * (float)Math.PI);
        GlStateManager.translate(lvt_5_1_, lvt_6_1_, lvt_7_1_);
        float lvt_8_1_ = this.getMapAngleFromPitch(pitch);
        GlStateManager.translate(0.0F, 0.04F, -0.72F);
        GlStateManager.translate(0.0F, equipmentProgress * -1.2F, 0.0F);
        GlStateManager.translate(0.0F, lvt_8_1_ * -0.5F, 0.0F);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(lvt_8_1_ * -85.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
        this.renderPlayerArms(clientPlayer);
        float lvt_9_1_ = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float lvt_10_1_ = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
        GlStateManager.rotate(lvt_9_1_ * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(lvt_10_1_ * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(lvt_10_1_ * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.38F, 0.38F, 0.38F);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(-1.0F, -1.0F, 0.0F);
        GlStateManager.scale(0.015625F, 0.015625F, 0.015625F);
        this.mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
        Tessellator lvt_11_1_ = Tessellator.getInstance();
        WorldRenderer lvt_12_1_ = lvt_11_1_.getWorldRenderer();
        GL11.glNormal3f(0.0F, 0.0F, -1.0F);
        lvt_12_1_.begin(7, DefaultVertexFormats.POSITION_TEX);
        lvt_12_1_.pos(-7.0D, 135.0D, 0.0D).tex(0.0D, 1.0D).endVertex();
        lvt_12_1_.pos(135.0D, 135.0D, 0.0D).tex(1.0D, 1.0D).endVertex();
        lvt_12_1_.pos(135.0D, -7.0D, 0.0D).tex(1.0D, 0.0D).endVertex();
        lvt_12_1_.pos(-7.0D, -7.0D, 0.0D).tex(0.0D, 0.0D).endVertex();
        lvt_11_1_.draw();
        MapData lvt_13_1_ = Items.filled_map.getMapData(this.itemToRender, this.mc.theWorld);

        if (lvt_13_1_ != null)
        {
            this.mc.entityRenderer.getMapItemRenderer().renderMap(lvt_13_1_, false);
        }
    }

    /**
     * Render the player's arm
     *  
     * @param equipProgress The progress of equiping the item
     * @param swingProgress The swing movement progression
     */
    private void renderPlayerArm(AbstractClientPlayer clientPlayer, float equipProgress, float swingProgress)
    {
        float lvt_4_1_ = -0.3F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
        float lvt_5_1_ = 0.4F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI * 2.0F);
        float lvt_6_1_ = -0.4F * MathHelper.sin(swingProgress * (float)Math.PI);
        GlStateManager.translate(lvt_4_1_, lvt_5_1_, lvt_6_1_);
        GlStateManager.translate(0.64000005F, -0.6F, -0.71999997F);
        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float lvt_7_1_ = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float lvt_8_1_ = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
        GlStateManager.rotate(lvt_8_1_ * 70.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(lvt_7_1_ * -20.0F, 0.0F, 0.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(clientPlayer.getLocationSkin());
        GlStateManager.translate(-1.0F, 3.6F, 3.5F);
        GlStateManager.rotate(120.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(1.0F, 1.0F, 1.0F);
        GlStateManager.translate(5.6F, 0.0F, 0.0F);
        Render<AbstractClientPlayer> lvt_9_1_ = this.renderManager.<AbstractClientPlayer>getEntityRenderObject(this.mc.thePlayer);
        GlStateManager.disableCull();
        RenderPlayer lvt_10_1_ = (RenderPlayer)lvt_9_1_;
        lvt_10_1_.renderRightArm(this.mc.thePlayer);
        GlStateManager.enableCull();
    }

    /**
     * Rotate and translate render to show item consumption
     *  
     * @param swingProgress The swing movement progress
     */
    private void doItemUsedTransformations(float swingProgress)
    {
        float lvt_2_1_ = -0.4F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
        float lvt_3_1_ = 0.2F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI * 2.0F);
        float lvt_4_1_ = -0.2F * MathHelper.sin(swingProgress * (float)Math.PI);
        GlStateManager.translate(lvt_2_1_, lvt_3_1_, lvt_4_1_);
    }

    /**
     * Perform the drinking animation movement
     *  
     * @param partialTicks Partials ticks
     */
    private void performDrinking(AbstractClientPlayer clientPlayer, float partialTicks)
    {
        float lvt_3_1_ = (float)clientPlayer.getItemInUseCount() - partialTicks + 1.0F;
        float lvt_4_1_ = lvt_3_1_ / (float)this.itemToRender.getMaxItemUseDuration();
        float lvt_5_1_ = MathHelper.abs(MathHelper.cos(lvt_3_1_ / 4.0F * (float)Math.PI) * 0.1F);

        if (lvt_4_1_ >= 0.8F)
        {
            lvt_5_1_ = 0.0F;
        }

        GlStateManager.translate(0.0F, lvt_5_1_, 0.0F);
        float lvt_6_1_ = 1.0F - (float)Math.pow((double)lvt_4_1_, 27.0D);
        GlStateManager.translate(lvt_6_1_ * 0.6F, lvt_6_1_ * -0.5F, lvt_6_1_ * 0.0F);
        GlStateManager.rotate(lvt_6_1_ * 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(lvt_6_1_ * 10.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(lvt_6_1_ * 30.0F, 0.0F, 0.0F, 1.0F);
    }

    /**
     * Performs transformations prior to the rendering of a held item in first person.
     */
    private void transformFirstPersonItem(float equipProgress, float swingProgress)
    {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float lvt_3_1_ = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float lvt_4_1_ = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
        GlStateManager.rotate(lvt_3_1_ * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(lvt_4_1_ * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(lvt_4_1_ * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }

    /**
     * Translate and rotate the render to look like holding a bow
     *  
     * @param partialTicks Partial ticks
     */
    private void doBowTransformations(float partialTicks, AbstractClientPlayer clientPlayer)
    {
        GlStateManager.rotate(-18.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(-12.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-8.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(-0.9F, 0.2F, 0.0F);
        float lvt_3_1_ = (float)this.itemToRender.getMaxItemUseDuration() - ((float)clientPlayer.getItemInUseCount() - partialTicks + 1.0F);
        float lvt_4_1_ = lvt_3_1_ / 20.0F;
        lvt_4_1_ = (lvt_4_1_ * lvt_4_1_ + lvt_4_1_ * 2.0F) / 3.0F;

        if (lvt_4_1_ > 1.0F)
        {
            lvt_4_1_ = 1.0F;
        }

        if (lvt_4_1_ > 0.1F)
        {
            float lvt_5_1_ = MathHelper.sin((lvt_3_1_ - 0.1F) * 1.3F);
            float lvt_6_1_ = lvt_4_1_ - 0.1F;
            float lvt_7_1_ = lvt_5_1_ * lvt_6_1_;
            GlStateManager.translate(lvt_7_1_ * 0.0F, lvt_7_1_ * 0.01F, lvt_7_1_ * 0.0F);
        }

        GlStateManager.translate(lvt_4_1_ * 0.0F, lvt_4_1_ * 0.0F, lvt_4_1_ * 0.1F);
        GlStateManager.scale(1.0F, 1.0F, 1.0F + lvt_4_1_ * 0.2F);
    }

    /**
     * Translate and rotate the render for holding a block
     */
    private void doBlockTransformations()
    {
        GlStateManager.translate(-0.5F, 0.2F, 0.0F);
        GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
    }

    /**
     * Renders the active item in the player's hand when in first person mode. Args: partialTickTime
     */
    public void renderItemInFirstPerson(float partialTicks)
    {
        float lvt_2_1_ = 1.0F - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
        AbstractClientPlayer lvt_3_1_ = this.mc.thePlayer;
        float lvt_4_1_ = lvt_3_1_.getSwingProgress(partialTicks);
        float lvt_5_1_ = lvt_3_1_.prevRotationPitch + (lvt_3_1_.rotationPitch - lvt_3_1_.prevRotationPitch) * partialTicks;
        float lvt_6_1_ = lvt_3_1_.prevRotationYaw + (lvt_3_1_.rotationYaw - lvt_3_1_.prevRotationYaw) * partialTicks;
        this.rotateArroundXAndY(lvt_5_1_, lvt_6_1_);
        this.setLightMapFromPlayer(lvt_3_1_);
        this.rotateWithPlayerRotations((EntityPlayerSP)lvt_3_1_, partialTicks);
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();

        if (this.itemToRender != null)
        {
            if (this.itemToRender.getItem() == Items.filled_map)
            {
                this.renderItemMap(lvt_3_1_, lvt_5_1_, lvt_2_1_, lvt_4_1_);
            }
            else if (lvt_3_1_.getItemInUseCount() > 0)
            {
                EnumAction lvt_7_1_ = this.itemToRender.getItemUseAction();

                switch (lvt_7_1_)
                {
                    case NONE:
                        this.transformFirstPersonItem(lvt_2_1_, 0.0F);
                        break;

                    case EAT:
                    case DRINK:
                        this.performDrinking(lvt_3_1_, partialTicks);
                        this.transformFirstPersonItem(lvt_2_1_, 0.0F);
                        break;

                    case BLOCK:
                        this.transformFirstPersonItem(lvt_2_1_, 0.0F);
                        this.doBlockTransformations();
                        break;

                    case BOW:
                        this.transformFirstPersonItem(lvt_2_1_, 0.0F);
                        this.doBowTransformations(partialTicks, lvt_3_1_);
                }
            }
            else
            {
                this.doItemUsedTransformations(lvt_4_1_);
                this.transformFirstPersonItem(lvt_2_1_, lvt_4_1_);
            }

            this.renderItem(lvt_3_1_, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
        }
        else if (!lvt_3_1_.isInvisible())
        {
            this.renderPlayerArm(lvt_3_1_, lvt_2_1_, lvt_4_1_);
        }

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
    }

    /**
     * Renders all the overlays that are in first person mode. Args: partialTickTime
     */
    public void renderOverlays(float partialTicks)
    {
        GlStateManager.disableAlpha();

        if (this.mc.thePlayer.isEntityInsideOpaqueBlock())
        {
            IBlockState lvt_2_1_ = this.mc.theWorld.getBlockState(new BlockPos(this.mc.thePlayer));
            EntityPlayer lvt_3_1_ = this.mc.thePlayer;

            for (int lvt_4_1_ = 0; lvt_4_1_ < 8; ++lvt_4_1_)
            {
                double lvt_5_1_ = lvt_3_1_.posX + (double)(((float)((lvt_4_1_ >> 0) % 2) - 0.5F) * lvt_3_1_.width * 0.8F);
                double lvt_7_1_ = lvt_3_1_.posY + (double)(((float)((lvt_4_1_ >> 1) % 2) - 0.5F) * 0.1F);
                double lvt_9_1_ = lvt_3_1_.posZ + (double)(((float)((lvt_4_1_ >> 2) % 2) - 0.5F) * lvt_3_1_.width * 0.8F);
                BlockPos lvt_11_1_ = new BlockPos(lvt_5_1_, lvt_7_1_ + (double)lvt_3_1_.getEyeHeight(), lvt_9_1_);
                IBlockState lvt_12_1_ = this.mc.theWorld.getBlockState(lvt_11_1_);

                if (lvt_12_1_.getBlock().isVisuallyOpaque())
                {
                    lvt_2_1_ = lvt_12_1_;
                }
            }

            if (lvt_2_1_.getBlock().getRenderType() != -1)
            {
                this.renderBlockInHand(partialTicks, this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(lvt_2_1_));
            }
        }

        if (!this.mc.thePlayer.isSpectator())
        {
            if (this.mc.thePlayer.isInsideOfMaterial(Material.water))
            {
                this.renderWaterOverlayTexture(partialTicks);
            }

            if (this.mc.thePlayer.isBurning())
            {
                this.renderFireInFirstPerson(partialTicks);
            }
        }

        GlStateManager.enableAlpha();
    }

    /**
     * Render the block in the player's hand
     *  
     * @param partialTicks Partial ticks
     * @param atlas The TextureAtlasSprite to render
     */
    private void renderBlockInHand(float partialTicks, TextureAtlasSprite atlas)
    {
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        Tessellator lvt_3_1_ = Tessellator.getInstance();
        WorldRenderer lvt_4_1_ = lvt_3_1_.getWorldRenderer();
        float lvt_5_1_ = 0.1F;
        GlStateManager.color(0.1F, 0.1F, 0.1F, 0.5F);
        GlStateManager.pushMatrix();
        float lvt_6_1_ = -1.0F;
        float lvt_7_1_ = 1.0F;
        float lvt_8_1_ = -1.0F;
        float lvt_9_1_ = 1.0F;
        float lvt_10_1_ = -0.5F;
        float lvt_11_1_ = atlas.getMinU();
        float lvt_12_1_ = atlas.getMaxU();
        float lvt_13_1_ = atlas.getMinV();
        float lvt_14_1_ = atlas.getMaxV();
        lvt_4_1_.begin(7, DefaultVertexFormats.POSITION_TEX);
        lvt_4_1_.pos(-1.0D, -1.0D, -0.5D).tex((double)lvt_12_1_, (double)lvt_14_1_).endVertex();
        lvt_4_1_.pos(1.0D, -1.0D, -0.5D).tex((double)lvt_11_1_, (double)lvt_14_1_).endVertex();
        lvt_4_1_.pos(1.0D, 1.0D, -0.5D).tex((double)lvt_11_1_, (double)lvt_13_1_).endVertex();
        lvt_4_1_.pos(-1.0D, 1.0D, -0.5D).tex((double)lvt_12_1_, (double)lvt_13_1_).endVertex();
        lvt_3_1_.draw();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Renders a texture that warps around based on the direction the player is looking. Texture needs to be bound
     * before being called. Used for the water overlay. Args: parialTickTime
     *  
     * @param partialTicks Partial ticks
     */
    private void renderWaterOverlayTexture(float partialTicks)
    {
        this.mc.getTextureManager().bindTexture(RES_UNDERWATER_OVERLAY);
        Tessellator lvt_2_1_ = Tessellator.getInstance();
        WorldRenderer lvt_3_1_ = lvt_2_1_.getWorldRenderer();
        float lvt_4_1_ = this.mc.thePlayer.getBrightness(partialTicks);
        GlStateManager.color(lvt_4_1_, lvt_4_1_, lvt_4_1_, 0.5F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.pushMatrix();
        float lvt_5_1_ = 4.0F;
        float lvt_6_1_ = -1.0F;
        float lvt_7_1_ = 1.0F;
        float lvt_8_1_ = -1.0F;
        float lvt_9_1_ = 1.0F;
        float lvt_10_1_ = -0.5F;
        float lvt_11_1_ = -this.mc.thePlayer.rotationYaw / 64.0F;
        float lvt_12_1_ = this.mc.thePlayer.rotationPitch / 64.0F;
        lvt_3_1_.begin(7, DefaultVertexFormats.POSITION_TEX);
        lvt_3_1_.pos(-1.0D, -1.0D, -0.5D).tex((double)(4.0F + lvt_11_1_), (double)(4.0F + lvt_12_1_)).endVertex();
        lvt_3_1_.pos(1.0D, -1.0D, -0.5D).tex((double)(0.0F + lvt_11_1_), (double)(4.0F + lvt_12_1_)).endVertex();
        lvt_3_1_.pos(1.0D, 1.0D, -0.5D).tex((double)(0.0F + lvt_11_1_), (double)(0.0F + lvt_12_1_)).endVertex();
        lvt_3_1_.pos(-1.0D, 1.0D, -0.5D).tex((double)(4.0F + lvt_11_1_), (double)(0.0F + lvt_12_1_)).endVertex();
        lvt_2_1_.draw();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
    }

    /**
     * Renders the fire on the screen for first person mode. Arg: partialTickTime
     *  
     * @param partialTicks Partial ticks
     */
    private void renderFireInFirstPerson(float partialTicks)
    {
        Tessellator lvt_2_1_ = Tessellator.getInstance();
        WorldRenderer lvt_3_1_ = lvt_2_1_.getWorldRenderer();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
        GlStateManager.depthFunc(519);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        float lvt_4_1_ = 1.0F;

        for (int lvt_5_1_ = 0; lvt_5_1_ < 2; ++lvt_5_1_)
        {
            GlStateManager.pushMatrix();
            TextureAtlasSprite lvt_6_1_ = this.mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/fire_layer_1");
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            float lvt_7_1_ = lvt_6_1_.getMinU();
            float lvt_8_1_ = lvt_6_1_.getMaxU();
            float lvt_9_1_ = lvt_6_1_.getMinV();
            float lvt_10_1_ = lvt_6_1_.getMaxV();
            float lvt_11_1_ = (0.0F - lvt_4_1_) / 2.0F;
            float lvt_12_1_ = lvt_11_1_ + lvt_4_1_;
            float lvt_13_1_ = 0.0F - lvt_4_1_ / 2.0F;
            float lvt_14_1_ = lvt_13_1_ + lvt_4_1_;
            float lvt_15_1_ = -0.5F;
            GlStateManager.translate((float)(-(lvt_5_1_ * 2 - 1)) * 0.24F, -0.3F, 0.0F);
            GlStateManager.rotate((float)(lvt_5_1_ * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
            lvt_3_1_.begin(7, DefaultVertexFormats.POSITION_TEX);
            lvt_3_1_.pos((double)lvt_11_1_, (double)lvt_13_1_, (double)lvt_15_1_).tex((double)lvt_8_1_, (double)lvt_10_1_).endVertex();
            lvt_3_1_.pos((double)lvt_12_1_, (double)lvt_13_1_, (double)lvt_15_1_).tex((double)lvt_7_1_, (double)lvt_10_1_).endVertex();
            lvt_3_1_.pos((double)lvt_12_1_, (double)lvt_14_1_, (double)lvt_15_1_).tex((double)lvt_7_1_, (double)lvt_9_1_).endVertex();
            lvt_3_1_.pos((double)lvt_11_1_, (double)lvt_14_1_, (double)lvt_15_1_).tex((double)lvt_8_1_, (double)lvt_9_1_).endVertex();
            lvt_2_1_.draw();
            GlStateManager.popMatrix();
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
    }

    public void updateEquippedItem()
    {
        this.prevEquippedProgress = this.equippedProgress;
        EntityPlayer lvt_1_1_ = this.mc.thePlayer;
        ItemStack lvt_2_1_ = lvt_1_1_.inventory.getCurrentItem();
        boolean lvt_3_1_ = false;

        if (this.itemToRender != null && lvt_2_1_ != null)
        {
            if (!this.itemToRender.getIsItemStackEqual(lvt_2_1_))
            {
                lvt_3_1_ = true;
            }
        }
        else if (this.itemToRender == null && lvt_2_1_ == null)
        {
            lvt_3_1_ = false;
        }
        else
        {
            lvt_3_1_ = true;
        }

        float lvt_4_1_ = 0.4F;
        float lvt_5_1_ = lvt_3_1_ ? 0.0F : 1.0F;
        float lvt_6_1_ = MathHelper.clamp_float(lvt_5_1_ - this.equippedProgress, -lvt_4_1_, lvt_4_1_);
        this.equippedProgress += lvt_6_1_;

        if (this.equippedProgress < 0.1F)
        {
            this.itemToRender = lvt_2_1_;
            this.equippedItemSlot = lvt_1_1_.inventory.currentItem;
        }
    }

    /**
     * Resets equippedProgress
     */
    public void resetEquippedProgress()
    {
        this.equippedProgress = 0.0F;
    }

    /**
     * Resets equippedProgress
     */
    public void resetEquippedProgress2()
    {
        this.equippedProgress = 0.0F;
    }
}
