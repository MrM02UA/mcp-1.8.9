package net.minecraft.client.renderer.entity;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerDeadmau5Head;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ResourceLocation;

public class RenderPlayer extends RendererLivingEntity<AbstractClientPlayer>
{
    /** this field is used to indicate the 3-pixel wide arms */
    private boolean smallArms;

    public RenderPlayer(RenderManager renderManager)
    {
        this(renderManager, false);
    }

    public RenderPlayer(RenderManager renderManager, boolean useSmallArms)
    {
        super(renderManager, new ModelPlayer(0.0F, useSmallArms), 0.5F);
        this.smallArms = useSmallArms;
        this.addLayer(new LayerBipedArmor(this));
        this.addLayer(new LayerHeldItem(this));
        this.addLayer(new LayerArrow(this));
        this.addLayer(new LayerDeadmau5Head(this));
        this.addLayer(new LayerCape(this));
        this.addLayer(new LayerCustomHead(this.getMainModel().bipedHead));
    }

    public ModelPlayer getMainModel()
    {
        return (ModelPlayer)super.getMainModel();
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if (!entity.isUser() || this.renderManager.livingPlayer == entity)
        {
            double lvt_10_1_ = y;

            if (entity.isSneaking() && !(entity instanceof EntityPlayerSP))
            {
                lvt_10_1_ = y - 0.125D;
            }

            this.setModelVisibilities(entity);
            super.doRender(entity, x, lvt_10_1_, z, entityYaw, partialTicks);
        }
    }

    private void setModelVisibilities(AbstractClientPlayer clientPlayer)
    {
        ModelPlayer lvt_2_1_ = this.getMainModel();

        if (clientPlayer.isSpectator())
        {
            lvt_2_1_.setInvisible(false);
            lvt_2_1_.bipedHead.showModel = true;
            lvt_2_1_.bipedHeadwear.showModel = true;
        }
        else
        {
            ItemStack lvt_3_1_ = clientPlayer.inventory.getCurrentItem();
            lvt_2_1_.setInvisible(true);
            lvt_2_1_.bipedHeadwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.HAT);
            lvt_2_1_.bipedBodyWear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.JACKET);
            lvt_2_1_.bipedLeftLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
            lvt_2_1_.bipedRightLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
            lvt_2_1_.bipedLeftArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
            lvt_2_1_.bipedRightArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
            lvt_2_1_.heldItemLeft = 0;
            lvt_2_1_.aimedBow = false;
            lvt_2_1_.isSneak = clientPlayer.isSneaking();

            if (lvt_3_1_ == null)
            {
                lvt_2_1_.heldItemRight = 0;
            }
            else
            {
                lvt_2_1_.heldItemRight = 1;

                if (clientPlayer.getItemInUseCount() > 0)
                {
                    EnumAction lvt_4_1_ = lvt_3_1_.getItemUseAction();

                    if (lvt_4_1_ == EnumAction.BLOCK)
                    {
                        lvt_2_1_.heldItemRight = 3;
                    }
                    else if (lvt_4_1_ == EnumAction.BOW)
                    {
                        lvt_2_1_.aimedBow = true;
                    }
                }
            }
        }
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(AbstractClientPlayer entity)
    {
        return entity.getLocationSkin();
    }

    public void transformHeldFull3DItemLayer()
    {
        GlStateManager.translate(0.0F, 0.1875F, 0.0F);
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(AbstractClientPlayer entitylivingbaseIn, float partialTickTime)
    {
        float lvt_3_1_ = 0.9375F;
        GlStateManager.scale(lvt_3_1_, lvt_3_1_, lvt_3_1_);
    }

    protected void renderOffsetLivingLabel(AbstractClientPlayer entityIn, double x, double y, double z, String str, float p_177069_9_, double p_177069_10_)
    {
        if (p_177069_10_ < 100.0D)
        {
            Scoreboard lvt_12_1_ = entityIn.getWorldScoreboard();
            ScoreObjective lvt_13_1_ = lvt_12_1_.getObjectiveInDisplaySlot(2);

            if (lvt_13_1_ != null)
            {
                Score lvt_14_1_ = lvt_12_1_.getValueFromObjective(entityIn.getName(), lvt_13_1_);
                this.renderLivingLabel(entityIn, lvt_14_1_.getScorePoints() + " " + lvt_13_1_.getDisplayName(), x, y, z, 64);
                y += (double)((float)this.getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * p_177069_9_);
            }
        }

        super.renderOffsetLivingLabel(entityIn, x, y, z, str, p_177069_9_, p_177069_10_);
    }

    public void renderRightArm(AbstractClientPlayer clientPlayer)
    {
        float lvt_2_1_ = 1.0F;
        GlStateManager.color(lvt_2_1_, lvt_2_1_, lvt_2_1_);
        ModelPlayer lvt_3_1_ = this.getMainModel();
        this.setModelVisibilities(clientPlayer);
        lvt_3_1_.swingProgress = 0.0F;
        lvt_3_1_.isSneak = false;
        lvt_3_1_.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
        lvt_3_1_.renderRightArm();
    }

    public void renderLeftArm(AbstractClientPlayer clientPlayer)
    {
        float lvt_2_1_ = 1.0F;
        GlStateManager.color(lvt_2_1_, lvt_2_1_, lvt_2_1_);
        ModelPlayer lvt_3_1_ = this.getMainModel();
        this.setModelVisibilities(clientPlayer);
        lvt_3_1_.isSneak = false;
        lvt_3_1_.swingProgress = 0.0F;
        lvt_3_1_.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
        lvt_3_1_.renderLeftArm();
    }

    /**
     * Sets a simple glTranslate on a LivingEntity.
     */
    protected void renderLivingAt(AbstractClientPlayer entityLivingBaseIn, double x, double y, double z)
    {
        if (entityLivingBaseIn.isEntityAlive() && entityLivingBaseIn.isPlayerSleeping())
        {
            super.renderLivingAt(entityLivingBaseIn, x + (double)entityLivingBaseIn.renderOffsetX, y + (double)entityLivingBaseIn.renderOffsetY, z + (double)entityLivingBaseIn.renderOffsetZ);
        }
        else
        {
            super.renderLivingAt(entityLivingBaseIn, x, y, z);
        }
    }

    protected void rotateCorpse(AbstractClientPlayer bat, float p_77043_2_, float p_77043_3_, float partialTicks)
    {
        if (bat.isEntityAlive() && bat.isPlayerSleeping())
        {
            GlStateManager.rotate(bat.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(this.getDeathMaxRotation(bat), 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
        }
        else
        {
            super.rotateCorpse(bat, p_77043_2_, p_77043_3_, partialTicks);
        }
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(EntityLivingBase entitylivingbaseIn, float partialTickTime)
    {
        this.preRenderCallback((AbstractClientPlayer)entitylivingbaseIn, partialTickTime);
    }

    protected void rotateCorpse(EntityLivingBase bat, float p_77043_2_, float p_77043_3_, float partialTicks)
    {
        this.rotateCorpse((AbstractClientPlayer)bat, p_77043_2_, p_77043_3_, partialTicks);
    }

    /**
     * Sets a simple glTranslate on a LivingEntity.
     */
    protected void renderLivingAt(EntityLivingBase entityLivingBaseIn, double x, double y, double z)
    {
        this.renderLivingAt((AbstractClientPlayer)entityLivingBaseIn, x, y, z);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.doRender((AbstractClientPlayer)entity, x, y, z, entityYaw, partialTicks);
    }

    public ModelBase getMainModel()
    {
        return this.getMainModel();
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return this.getEntityTexture((AbstractClientPlayer)entity);
    }

    protected void renderOffsetLivingLabel(Entity entityIn, double x, double y, double z, String str, float p_177069_9_, double p_177069_10_)
    {
        this.renderOffsetLivingLabel((AbstractClientPlayer)entityIn, x, y, z, str, p_177069_9_, p_177069_10_);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.doRender((AbstractClientPlayer)entity, x, y, z, entityYaw, partialTicks);
    }
}
