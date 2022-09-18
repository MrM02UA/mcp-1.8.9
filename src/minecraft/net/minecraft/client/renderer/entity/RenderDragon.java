package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelDragon;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.layers.LayerEnderDragonDeath;
import net.minecraft.client.renderer.entity.layers.LayerEnderDragonEyes;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderDragon extends RenderLiving<EntityDragon>
{
    private static final ResourceLocation enderDragonCrystalBeamTextures = new ResourceLocation("textures/entity/endercrystal/endercrystal_beam.png");
    private static final ResourceLocation enderDragonExplodingTextures = new ResourceLocation("textures/entity/enderdragon/dragon_exploding.png");
    private static final ResourceLocation enderDragonTextures = new ResourceLocation("textures/entity/enderdragon/dragon.png");

    /** An instance of the dragon model in RenderDragon */
    protected ModelDragon modelDragon;

    public RenderDragon(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelDragon(0.0F), 0.5F);
        this.modelDragon = (ModelDragon)this.mainModel;
        this.addLayer(new LayerEnderDragonEyes(this));
        this.addLayer(new LayerEnderDragonDeath());
    }

    protected void rotateCorpse(EntityDragon bat, float p_77043_2_, float p_77043_3_, float partialTicks)
    {
        float lvt_5_1_ = (float)bat.getMovementOffsets(7, partialTicks)[0];
        float lvt_6_1_ = (float)(bat.getMovementOffsets(5, partialTicks)[1] - bat.getMovementOffsets(10, partialTicks)[1]);
        GlStateManager.rotate(-lvt_5_1_, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(lvt_6_1_ * 10.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0.0F, 0.0F, 1.0F);

        if (bat.deathTime > 0)
        {
            float lvt_7_1_ = ((float)bat.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
            lvt_7_1_ = MathHelper.sqrt_float(lvt_7_1_);

            if (lvt_7_1_ > 1.0F)
            {
                lvt_7_1_ = 1.0F;
            }

            GlStateManager.rotate(lvt_7_1_ * this.getDeathMaxRotation(bat), 0.0F, 0.0F, 1.0F);
        }
    }

    /**
     * Renders the model in RenderLiving
     */
    protected void renderModel(EntityDragon entitylivingbaseIn, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float scaleFactor)
    {
        if (entitylivingbaseIn.deathTicks > 0)
        {
            float lvt_8_1_ = (float)entitylivingbaseIn.deathTicks / 200.0F;
            GlStateManager.depthFunc(515);
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc(516, lvt_8_1_);
            this.bindTexture(enderDragonExplodingTextures);
            this.mainModel.render(entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, scaleFactor);
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.depthFunc(514);
        }

        this.bindEntityTexture(entitylivingbaseIn);
        this.mainModel.render(entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, scaleFactor);

        if (entitylivingbaseIn.hurtTime > 0)
        {
            GlStateManager.depthFunc(514);
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.color(1.0F, 0.0F, 0.0F, 0.5F);
            this.mainModel.render(entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, scaleFactor);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.depthFunc(515);
        }
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityDragon entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        BossStatus.setBossStatus(entity, false);
        super.doRender(entity, x, y, z, entityYaw, partialTicks);

        if (entity.healingEnderCrystal != null)
        {
            this.drawRechargeRay(entity, x, y, z, partialTicks);
        }
    }

    /**
     * Draws the ray from the dragon to it's crystal
     */
    protected void drawRechargeRay(EntityDragon dragon, double p_180574_2_, double p_180574_4_, double p_180574_6_, float p_180574_8_)
    {
        float lvt_9_1_ = (float)dragon.healingEnderCrystal.innerRotation + p_180574_8_;
        float lvt_10_1_ = MathHelper.sin(lvt_9_1_ * 0.2F) / 2.0F + 0.5F;
        lvt_10_1_ = (lvt_10_1_ * lvt_10_1_ + lvt_10_1_) * 0.2F;
        float lvt_11_1_ = (float)(dragon.healingEnderCrystal.posX - dragon.posX - (dragon.prevPosX - dragon.posX) * (double)(1.0F - p_180574_8_));
        float lvt_12_1_ = (float)((double)lvt_10_1_ + dragon.healingEnderCrystal.posY - 1.0D - dragon.posY - (dragon.prevPosY - dragon.posY) * (double)(1.0F - p_180574_8_));
        float lvt_13_1_ = (float)(dragon.healingEnderCrystal.posZ - dragon.posZ - (dragon.prevPosZ - dragon.posZ) * (double)(1.0F - p_180574_8_));
        float lvt_14_1_ = MathHelper.sqrt_float(lvt_11_1_ * lvt_11_1_ + lvt_13_1_ * lvt_13_1_);
        float lvt_15_1_ = MathHelper.sqrt_float(lvt_11_1_ * lvt_11_1_ + lvt_12_1_ * lvt_12_1_ + lvt_13_1_ * lvt_13_1_);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)p_180574_2_, (float)p_180574_4_ + 2.0F, (float)p_180574_6_);
        GlStateManager.rotate((float)(-Math.atan2((double)lvt_13_1_, (double)lvt_11_1_)) * 180.0F / (float)Math.PI - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float)(-Math.atan2((double)lvt_14_1_, (double)lvt_12_1_)) * 180.0F / (float)Math.PI - 90.0F, 1.0F, 0.0F, 0.0F);
        Tessellator lvt_16_1_ = Tessellator.getInstance();
        WorldRenderer lvt_17_1_ = lvt_16_1_.getWorldRenderer();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableCull();
        this.bindTexture(enderDragonCrystalBeamTextures);
        GlStateManager.shadeModel(7425);
        float lvt_18_1_ = 0.0F - ((float)dragon.ticksExisted + p_180574_8_) * 0.01F;
        float lvt_19_1_ = MathHelper.sqrt_float(lvt_11_1_ * lvt_11_1_ + lvt_12_1_ * lvt_12_1_ + lvt_13_1_ * lvt_13_1_) / 32.0F - ((float)dragon.ticksExisted + p_180574_8_) * 0.01F;
        lvt_17_1_.begin(5, DefaultVertexFormats.POSITION_TEX_COLOR);
        int lvt_20_1_ = 8;

        for (int lvt_21_1_ = 0; lvt_21_1_ <= 8; ++lvt_21_1_)
        {
            float lvt_22_1_ = MathHelper.sin((float)(lvt_21_1_ % 8) * (float)Math.PI * 2.0F / 8.0F) * 0.75F;
            float lvt_23_1_ = MathHelper.cos((float)(lvt_21_1_ % 8) * (float)Math.PI * 2.0F / 8.0F) * 0.75F;
            float lvt_24_1_ = (float)(lvt_21_1_ % 8) * 1.0F / 8.0F;
            lvt_17_1_.pos((double)(lvt_22_1_ * 0.2F), (double)(lvt_23_1_ * 0.2F), 0.0D).tex((double)lvt_24_1_, (double)lvt_19_1_).color(0, 0, 0, 255).endVertex();
            lvt_17_1_.pos((double)lvt_22_1_, (double)lvt_23_1_, (double)lvt_15_1_).tex((double)lvt_24_1_, (double)lvt_18_1_).color(255, 255, 255, 255).endVertex();
        }

        lvt_16_1_.draw();
        GlStateManager.enableCull();
        GlStateManager.shadeModel(7424);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityDragon entity)
    {
        return enderDragonTextures;
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityLiving entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.doRender((EntityDragon)entity, x, y, z, entityYaw, partialTicks);
    }

    protected void rotateCorpse(EntityLivingBase bat, float p_77043_2_, float p_77043_3_, float partialTicks)
    {
        this.rotateCorpse((EntityDragon)bat, p_77043_2_, p_77043_3_, partialTicks);
    }

    /**
     * Renders the model in RenderLiving
     */
    protected void renderModel(EntityLivingBase entitylivingbaseIn, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float scaleFactor)
    {
        this.renderModel((EntityDragon)entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, scaleFactor);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.doRender((EntityDragon)entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return this.getEntityTexture((EntityDragon)entity);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.doRender((EntityDragon)entity, x, y, z, entityYaw, partialTicks);
    }
}
