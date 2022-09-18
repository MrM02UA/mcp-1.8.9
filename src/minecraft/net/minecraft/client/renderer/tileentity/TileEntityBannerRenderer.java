package net.minecraft.client.renderer.tileentity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBanner;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.LayeredColorMaskTexture;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class TileEntityBannerRenderer extends TileEntitySpecialRenderer<TileEntityBanner>
{
    private static final Map<String, TileEntityBannerRenderer.TimedBannerTexture> DESIGNS = Maps.newHashMap();
    private static final ResourceLocation BANNERTEXTURES = new ResourceLocation("textures/entity/banner_base.png");
    private ModelBanner bannerModel = new ModelBanner();

    public void renderTileEntityAt(TileEntityBanner te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        boolean lvt_10_1_ = te.getWorld() != null;
        boolean lvt_11_1_ = !lvt_10_1_ || te.getBlockType() == Blocks.standing_banner;
        int lvt_12_1_ = lvt_10_1_ ? te.getBlockMetadata() : 0;
        long lvt_13_1_ = lvt_10_1_ ? te.getWorld().getTotalWorldTime() : 0L;
        GlStateManager.pushMatrix();
        float lvt_15_1_ = 0.6666667F;

        if (lvt_11_1_)
        {
            GlStateManager.translate((float)x + 0.5F, (float)y + 0.75F * lvt_15_1_, (float)z + 0.5F);
            float lvt_16_1_ = (float)(lvt_12_1_ * 360) / 16.0F;
            GlStateManager.rotate(-lvt_16_1_, 0.0F, 1.0F, 0.0F);
            this.bannerModel.bannerStand.showModel = true;
        }
        else
        {
            float lvt_17_1_ = 0.0F;

            if (lvt_12_1_ == 2)
            {
                lvt_17_1_ = 180.0F;
            }

            if (lvt_12_1_ == 4)
            {
                lvt_17_1_ = 90.0F;
            }

            if (lvt_12_1_ == 5)
            {
                lvt_17_1_ = -90.0F;
            }

            GlStateManager.translate((float)x + 0.5F, (float)y - 0.25F * lvt_15_1_, (float)z + 0.5F);
            GlStateManager.rotate(-lvt_17_1_, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, -0.3125F, -0.4375F);
            this.bannerModel.bannerStand.showModel = false;
        }

        BlockPos lvt_16_3_ = te.getPos();
        float lvt_17_2_ = (float)(lvt_16_3_.getX() * 7 + lvt_16_3_.getY() * 9 + lvt_16_3_.getZ() * 13) + (float)lvt_13_1_ + partialTicks;
        this.bannerModel.bannerSlate.rotateAngleX = (-0.0125F + 0.01F * MathHelper.cos(lvt_17_2_ * (float)Math.PI * 0.02F)) * (float)Math.PI;
        GlStateManager.enableRescaleNormal();
        ResourceLocation lvt_18_1_ = this.func_178463_a(te);

        if (lvt_18_1_ != null)
        {
            this.bindTexture(lvt_18_1_);
            GlStateManager.pushMatrix();
            GlStateManager.scale(lvt_15_1_, -lvt_15_1_, -lvt_15_1_);
            this.bannerModel.renderBanner();
            GlStateManager.popMatrix();
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private ResourceLocation func_178463_a(TileEntityBanner bannerObj)
    {
        String lvt_2_1_ = bannerObj.getPatternResourceLocation();

        if (lvt_2_1_.isEmpty())
        {
            return null;
        }
        else
        {
            TileEntityBannerRenderer.TimedBannerTexture lvt_3_1_ = (TileEntityBannerRenderer.TimedBannerTexture)DESIGNS.get(lvt_2_1_);

            if (lvt_3_1_ == null)
            {
                if (DESIGNS.size() >= 256)
                {
                    long lvt_4_1_ = System.currentTimeMillis();
                    Iterator<String> lvt_6_1_ = DESIGNS.keySet().iterator();

                    while (lvt_6_1_.hasNext())
                    {
                        String lvt_7_1_ = (String)lvt_6_1_.next();
                        TileEntityBannerRenderer.TimedBannerTexture lvt_8_1_ = (TileEntityBannerRenderer.TimedBannerTexture)DESIGNS.get(lvt_7_1_);

                        if (lvt_4_1_ - lvt_8_1_.systemTime > 60000L)
                        {
                            Minecraft.getMinecraft().getTextureManager().deleteTexture(lvt_8_1_.bannerTexture);
                            lvt_6_1_.remove();
                        }
                    }

                    if (DESIGNS.size() >= 256)
                    {
                        return null;
                    }
                }

                List<TileEntityBanner.EnumBannerPattern> lvt_4_2_ = bannerObj.getPatternList();
                List<EnumDyeColor> lvt_5_1_ = bannerObj.getColorList();
                List<String> lvt_6_2_ = Lists.newArrayList();

                for (TileEntityBanner.EnumBannerPattern lvt_8_2_ : lvt_4_2_)
                {
                    lvt_6_2_.add("textures/entity/banner/" + lvt_8_2_.getPatternName() + ".png");
                }

                lvt_3_1_ = new TileEntityBannerRenderer.TimedBannerTexture();
                lvt_3_1_.bannerTexture = new ResourceLocation(lvt_2_1_);
                Minecraft.getMinecraft().getTextureManager().loadTexture(lvt_3_1_.bannerTexture, new LayeredColorMaskTexture(BANNERTEXTURES, lvt_6_2_, lvt_5_1_));
                DESIGNS.put(lvt_2_1_, lvt_3_1_);
            }

            lvt_3_1_.systemTime = System.currentTimeMillis();
            return lvt_3_1_.bannerTexture;
        }
    }

    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        this.renderTileEntityAt((TileEntityBanner)te, x, y, z, partialTicks, destroyStage);
    }

    static class TimedBannerTexture
    {
        public long systemTime;
        public ResourceLocation bannerTexture;

        private TimedBannerTexture()
        {
        }
    }
}
