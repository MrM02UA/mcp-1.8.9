package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class LayerArmorBase<T extends ModelBase> implements LayerRenderer<EntityLivingBase>
{
    protected static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    protected T modelLeggings;
    protected T modelArmor;
    private final RendererLivingEntity<?> renderer;
    private float alpha = 1.0F;
    private float colorR = 1.0F;
    private float colorG = 1.0F;
    private float colorB = 1.0F;
    private boolean skipRenderGlint;
    private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();

    public LayerArmorBase(RendererLivingEntity<?> rendererIn)
    {
        this.renderer = rendererIn;
        this.initArmor();
    }

    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
    {
        this.renderLayer(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale, 4);
        this.renderLayer(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale, 3);
        this.renderLayer(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale, 2);
        this.renderLayer(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale, 1);
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }

    private void renderLayer(EntityLivingBase entitylivingbaseIn, float p_177182_2_, float p_177182_3_, float partialTicks, float p_177182_5_, float p_177182_6_, float p_177182_7_, float scale, int armorSlot)
    {
        ItemStack lvt_10_1_ = this.getCurrentArmor(entitylivingbaseIn, armorSlot);

        if (lvt_10_1_ != null && lvt_10_1_.getItem() instanceof ItemArmor)
        {
            ItemArmor lvt_11_1_ = (ItemArmor)lvt_10_1_.getItem();
            T lvt_12_1_ = this.getArmorModel(armorSlot);
            lvt_12_1_.setModelAttributes(this.renderer.getMainModel());
            lvt_12_1_.setLivingAnimations(entitylivingbaseIn, p_177182_2_, p_177182_3_, partialTicks);
            this.setModelPartVisible(lvt_12_1_, armorSlot);
            boolean lvt_13_1_ = this.isSlotForLeggings(armorSlot);
            this.renderer.bindTexture(this.getArmorResource(lvt_11_1_, lvt_13_1_));

            switch (lvt_11_1_.getArmorMaterial())
            {
                case LEATHER:
                    int lvt_14_1_ = lvt_11_1_.getColor(lvt_10_1_);
                    float lvt_15_1_ = (float)(lvt_14_1_ >> 16 & 255) / 255.0F;
                    float lvt_16_1_ = (float)(lvt_14_1_ >> 8 & 255) / 255.0F;
                    float lvt_17_1_ = (float)(lvt_14_1_ & 255) / 255.0F;
                    GlStateManager.color(this.colorR * lvt_15_1_, this.colorG * lvt_16_1_, this.colorB * lvt_17_1_, this.alpha);
                    lvt_12_1_.render(entitylivingbaseIn, p_177182_2_, p_177182_3_, p_177182_5_, p_177182_6_, p_177182_7_, scale);
                    this.renderer.bindTexture(this.getArmorResource(lvt_11_1_, lvt_13_1_, "overlay"));

                case CHAIN:
                case IRON:
                case GOLD:
                case DIAMOND:
                    GlStateManager.color(this.colorR, this.colorG, this.colorB, this.alpha);
                    lvt_12_1_.render(entitylivingbaseIn, p_177182_2_, p_177182_3_, p_177182_5_, p_177182_6_, p_177182_7_, scale);

                default:
                    if (!this.skipRenderGlint && lvt_10_1_.isItemEnchanted())
                    {
                        this.renderGlint(entitylivingbaseIn, lvt_12_1_, p_177182_2_, p_177182_3_, partialTicks, p_177182_5_, p_177182_6_, p_177182_7_, scale);
                    }
            }
        }
    }

    public ItemStack getCurrentArmor(EntityLivingBase entitylivingbaseIn, int armorSlot)
    {
        return entitylivingbaseIn.getCurrentArmor(armorSlot - 1);
    }

    public T getArmorModel(int armorSlot)
    {
        return (T)(this.isSlotForLeggings(armorSlot) ? this.modelLeggings : this.modelArmor);
    }

    private boolean isSlotForLeggings(int armorSlot)
    {
        return armorSlot == 2;
    }

    private void renderGlint(EntityLivingBase entitylivingbaseIn, T modelbaseIn, float p_177183_3_, float p_177183_4_, float partialTicks, float p_177183_6_, float p_177183_7_, float p_177183_8_, float scale)
    {
        float lvt_10_1_ = (float)entitylivingbaseIn.ticksExisted + partialTicks;
        this.renderer.bindTexture(ENCHANTED_ITEM_GLINT_RES);
        GlStateManager.enableBlend();
        GlStateManager.depthFunc(514);
        GlStateManager.depthMask(false);
        float lvt_11_1_ = 0.5F;
        GlStateManager.color(lvt_11_1_, lvt_11_1_, lvt_11_1_, 1.0F);

        for (int lvt_12_1_ = 0; lvt_12_1_ < 2; ++lvt_12_1_)
        {
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(768, 1);
            float lvt_13_1_ = 0.76F;
            GlStateManager.color(0.5F * lvt_13_1_, 0.25F * lvt_13_1_, 0.8F * lvt_13_1_, 1.0F);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            float lvt_14_1_ = 0.33333334F;
            GlStateManager.scale(lvt_14_1_, lvt_14_1_, lvt_14_1_);
            GlStateManager.rotate(30.0F - (float)lvt_12_1_ * 60.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0.0F, lvt_10_1_ * (0.001F + (float)lvt_12_1_ * 0.003F) * 20.0F, 0.0F);
            GlStateManager.matrixMode(5888);
            modelbaseIn.render(entitylivingbaseIn, p_177183_3_, p_177183_4_, p_177183_6_, p_177183_7_, p_177183_8_, scale);
        }

        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
        GlStateManager.disableBlend();
    }

    private ResourceLocation getArmorResource(ItemArmor p_177181_1_, boolean p_177181_2_)
    {
        return this.getArmorResource(p_177181_1_, p_177181_2_, (String)null);
    }

    private ResourceLocation getArmorResource(ItemArmor p_177178_1_, boolean p_177178_2_, String p_177178_3_)
    {
        String lvt_4_1_ = String.format("textures/models/armor/%s_layer_%d%s.png", new Object[] {p_177178_1_.getArmorMaterial().getName(), Integer.valueOf(p_177178_2_ ? 2 : 1), p_177178_3_ == null ? "" : String.format("_%s", new Object[]{p_177178_3_})});
        ResourceLocation lvt_5_1_ = (ResourceLocation)ARMOR_TEXTURE_RES_MAP.get(lvt_4_1_);

        if (lvt_5_1_ == null)
        {
            lvt_5_1_ = new ResourceLocation(lvt_4_1_);
            ARMOR_TEXTURE_RES_MAP.put(lvt_4_1_, lvt_5_1_);
        }

        return lvt_5_1_;
    }

    protected abstract void initArmor();

    protected abstract void setModelPartVisible(T model, int armorSlot);
}
