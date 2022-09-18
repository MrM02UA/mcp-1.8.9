package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderEntityItem extends Render<EntityItem>
{
    private final RenderItem itemRenderer;
    private Random field_177079_e = new Random();

    public RenderEntityItem(RenderManager renderManagerIn, RenderItem p_i46167_2_)
    {
        super(renderManagerIn);
        this.itemRenderer = p_i46167_2_;
        this.shadowSize = 0.15F;
        this.shadowOpaque = 0.75F;
    }

    private int func_177077_a(EntityItem itemIn, double p_177077_2_, double p_177077_4_, double p_177077_6_, float p_177077_8_, IBakedModel p_177077_9_)
    {
        ItemStack lvt_10_1_ = itemIn.getEntityItem();
        Item lvt_11_1_ = lvt_10_1_.getItem();

        if (lvt_11_1_ == null)
        {
            return 0;
        }
        else
        {
            boolean lvt_12_1_ = p_177077_9_.isGui3d();
            int lvt_13_1_ = this.func_177078_a(lvt_10_1_);
            float lvt_14_1_ = 0.25F;
            float lvt_15_1_ = MathHelper.sin(((float)itemIn.getAge() + p_177077_8_) / 10.0F + itemIn.hoverStart) * 0.1F + 0.1F;
            float lvt_16_1_ = p_177077_9_.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;
            GlStateManager.translate((float)p_177077_2_, (float)p_177077_4_ + lvt_15_1_ + 0.25F * lvt_16_1_, (float)p_177077_6_);

            if (lvt_12_1_ || this.renderManager.options != null)
            {
                float lvt_17_1_ = (((float)itemIn.getAge() + p_177077_8_) / 20.0F + itemIn.hoverStart) * (180F / (float)Math.PI);
                GlStateManager.rotate(lvt_17_1_, 0.0F, 1.0F, 0.0F);
            }

            if (!lvt_12_1_)
            {
                float lvt_17_2_ = -0.0F * (float)(lvt_13_1_ - 1) * 0.5F;
                float lvt_18_1_ = -0.0F * (float)(lvt_13_1_ - 1) * 0.5F;
                float lvt_19_1_ = -0.046875F * (float)(lvt_13_1_ - 1) * 0.5F;
                GlStateManager.translate(lvt_17_2_, lvt_18_1_, lvt_19_1_);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            return lvt_13_1_;
        }
    }

    private int func_177078_a(ItemStack stack)
    {
        int lvt_2_1_ = 1;

        if (stack.stackSize > 48)
        {
            lvt_2_1_ = 5;
        }
        else if (stack.stackSize > 32)
        {
            lvt_2_1_ = 4;
        }
        else if (stack.stackSize > 16)
        {
            lvt_2_1_ = 3;
        }
        else if (stack.stackSize > 1)
        {
            lvt_2_1_ = 2;
        }

        return lvt_2_1_;
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        ItemStack lvt_10_1_ = entity.getEntityItem();
        this.field_177079_e.setSeed(187L);
        boolean lvt_11_1_ = false;

        if (this.bindEntityTexture(entity))
        {
            this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).setBlurMipmap(false, false);
            lvt_11_1_ = true;
        }

        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.pushMatrix();
        IBakedModel lvt_12_1_ = this.itemRenderer.getItemModelMesher().getItemModel(lvt_10_1_);
        int lvt_13_1_ = this.func_177077_a(entity, x, y, z, partialTicks, lvt_12_1_);

        for (int lvt_14_1_ = 0; lvt_14_1_ < lvt_13_1_; ++lvt_14_1_)
        {
            if (lvt_12_1_.isGui3d())
            {
                GlStateManager.pushMatrix();

                if (lvt_14_1_ > 0)
                {
                    float lvt_15_1_ = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float lvt_16_1_ = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float lvt_17_1_ = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    GlStateManager.translate(lvt_15_1_, lvt_16_1_, lvt_17_1_);
                }

                GlStateManager.scale(0.5F, 0.5F, 0.5F);
                lvt_12_1_.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GROUND);
                this.itemRenderer.renderItem(lvt_10_1_, lvt_12_1_);
                GlStateManager.popMatrix();
            }
            else
            {
                GlStateManager.pushMatrix();
                lvt_12_1_.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GROUND);
                this.itemRenderer.renderItem(lvt_10_1_, lvt_12_1_);
                GlStateManager.popMatrix();
                float lvt_15_2_ = lvt_12_1_.getItemCameraTransforms().ground.scale.x;
                float lvt_16_2_ = lvt_12_1_.getItemCameraTransforms().ground.scale.y;
                float lvt_17_2_ = lvt_12_1_.getItemCameraTransforms().ground.scale.z;
                GlStateManager.translate(0.0F * lvt_15_2_, 0.0F * lvt_16_2_, 0.046875F * lvt_17_2_);
            }
        }

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        this.bindEntityTexture(entity);

        if (lvt_11_1_)
        {
            this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).restoreLastBlurMipmap();
        }

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityItem entity)
    {
        return TextureMap.locationBlocksTexture;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return this.getEntityTexture((EntityItem)entity);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.doRender((EntityItem)entity, x, y, z, entityYaw, partialTicks);
    }
}
