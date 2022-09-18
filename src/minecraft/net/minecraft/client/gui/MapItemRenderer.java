package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec4b;
import net.minecraft.world.storage.MapData;

public class MapItemRenderer
{
    private static final ResourceLocation mapIcons = new ResourceLocation("textures/map/map_icons.png");
    private final TextureManager textureManager;
    private final Map<String, MapItemRenderer.Instance> loadedMaps = Maps.newHashMap();

    public MapItemRenderer(TextureManager textureManagerIn)
    {
        this.textureManager = textureManagerIn;
    }

    /**
     * Updates a map texture
     */
    public void updateMapTexture(MapData mapdataIn)
    {
        this.getMapRendererInstance(mapdataIn).updateMapTexture();
    }

    public void renderMap(MapData mapdataIn, boolean p_148250_2_)
    {
        this.getMapRendererInstance(mapdataIn).render(p_148250_2_);
    }

    /**
     * Returns {@link net.minecraft.client.gui.MapItemRenderer.Instance MapItemRenderer.Instance} with given map data
     */
    private MapItemRenderer.Instance getMapRendererInstance(MapData mapdataIn)
    {
        MapItemRenderer.Instance lvt_2_1_ = (MapItemRenderer.Instance)this.loadedMaps.get(mapdataIn.mapName);

        if (lvt_2_1_ == null)
        {
            lvt_2_1_ = new MapItemRenderer.Instance(mapdataIn);
            this.loadedMaps.put(mapdataIn.mapName, lvt_2_1_);
        }

        return lvt_2_1_;
    }

    /**
     * Clears the currently loaded maps and removes their corresponding textures
     */
    public void clearLoadedMaps()
    {
        for (MapItemRenderer.Instance lvt_2_1_ : this.loadedMaps.values())
        {
            this.textureManager.deleteTexture(lvt_2_1_.location);
        }

        this.loadedMaps.clear();
    }

    class Instance
    {
        private final MapData mapData;
        private final DynamicTexture mapTexture;
        private final ResourceLocation location;
        private final int[] mapTextureData;

        private Instance(MapData mapdataIn)
        {
            this.mapData = mapdataIn;
            this.mapTexture = new DynamicTexture(128, 128);
            this.mapTextureData = this.mapTexture.getTextureData();
            this.location = MapItemRenderer.this.textureManager.getDynamicTextureLocation("map/" + mapdataIn.mapName, this.mapTexture);

            for (int lvt_3_1_ = 0; lvt_3_1_ < this.mapTextureData.length; ++lvt_3_1_)
            {
                this.mapTextureData[lvt_3_1_] = 0;
            }
        }

        private void updateMapTexture()
        {
            for (int lvt_1_1_ = 0; lvt_1_1_ < 16384; ++lvt_1_1_)
            {
                int lvt_2_1_ = this.mapData.colors[lvt_1_1_] & 255;

                if (lvt_2_1_ / 4 == 0)
                {
                    this.mapTextureData[lvt_1_1_] = (lvt_1_1_ + lvt_1_1_ / 128 & 1) * 8 + 16 << 24;
                }
                else
                {
                    this.mapTextureData[lvt_1_1_] = MapColor.mapColorArray[lvt_2_1_ / 4].getMapColor(lvt_2_1_ & 3);
                }
            }

            this.mapTexture.updateDynamicTexture();
        }

        private void render(boolean noOverlayRendering)
        {
            int lvt_2_1_ = 0;
            int lvt_3_1_ = 0;
            Tessellator lvt_4_1_ = Tessellator.getInstance();
            WorldRenderer lvt_5_1_ = lvt_4_1_.getWorldRenderer();
            float lvt_6_1_ = 0.0F;
            MapItemRenderer.this.textureManager.bindTexture(this.location);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(1, 771, 0, 1);
            GlStateManager.disableAlpha();
            lvt_5_1_.begin(7, DefaultVertexFormats.POSITION_TEX);
            lvt_5_1_.pos((double)((float)(lvt_2_1_ + 0) + lvt_6_1_), (double)((float)(lvt_3_1_ + 128) - lvt_6_1_), -0.009999999776482582D).tex(0.0D, 1.0D).endVertex();
            lvt_5_1_.pos((double)((float)(lvt_2_1_ + 128) - lvt_6_1_), (double)((float)(lvt_3_1_ + 128) - lvt_6_1_), -0.009999999776482582D).tex(1.0D, 1.0D).endVertex();
            lvt_5_1_.pos((double)((float)(lvt_2_1_ + 128) - lvt_6_1_), (double)((float)(lvt_3_1_ + 0) + lvt_6_1_), -0.009999999776482582D).tex(1.0D, 0.0D).endVertex();
            lvt_5_1_.pos((double)((float)(lvt_2_1_ + 0) + lvt_6_1_), (double)((float)(lvt_3_1_ + 0) + lvt_6_1_), -0.009999999776482582D).tex(0.0D, 0.0D).endVertex();
            lvt_4_1_.draw();
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
            MapItemRenderer.this.textureManager.bindTexture(MapItemRenderer.mapIcons);
            int lvt_7_1_ = 0;

            for (Vec4b lvt_9_1_ : this.mapData.mapDecorations.values())
            {
                if (!noOverlayRendering || lvt_9_1_.func_176110_a() == 1)
                {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate((float)lvt_2_1_ + (float)lvt_9_1_.func_176112_b() / 2.0F + 64.0F, (float)lvt_3_1_ + (float)lvt_9_1_.func_176113_c() / 2.0F + 64.0F, -0.02F);
                    GlStateManager.rotate((float)(lvt_9_1_.func_176111_d() * 360) / 16.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.scale(4.0F, 4.0F, 3.0F);
                    GlStateManager.translate(-0.125F, 0.125F, 0.0F);
                    byte lvt_10_1_ = lvt_9_1_.func_176110_a();
                    float lvt_11_1_ = (float)(lvt_10_1_ % 4 + 0) / 4.0F;
                    float lvt_12_1_ = (float)(lvt_10_1_ / 4 + 0) / 4.0F;
                    float lvt_13_1_ = (float)(lvt_10_1_ % 4 + 1) / 4.0F;
                    float lvt_14_1_ = (float)(lvt_10_1_ / 4 + 1) / 4.0F;
                    lvt_5_1_.begin(7, DefaultVertexFormats.POSITION_TEX);
                    float lvt_15_1_ = -0.001F;
                    lvt_5_1_.pos(-1.0D, 1.0D, (double)((float)lvt_7_1_ * -0.001F)).tex((double)lvt_11_1_, (double)lvt_12_1_).endVertex();
                    lvt_5_1_.pos(1.0D, 1.0D, (double)((float)lvt_7_1_ * -0.001F)).tex((double)lvt_13_1_, (double)lvt_12_1_).endVertex();
                    lvt_5_1_.pos(1.0D, -1.0D, (double)((float)lvt_7_1_ * -0.001F)).tex((double)lvt_13_1_, (double)lvt_14_1_).endVertex();
                    lvt_5_1_.pos(-1.0D, -1.0D, (double)((float)lvt_7_1_ * -0.001F)).tex((double)lvt_11_1_, (double)lvt_14_1_).endVertex();
                    lvt_4_1_.draw();
                    GlStateManager.popMatrix();
                    ++lvt_7_1_;
                }
            }

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, -0.04F);
            GlStateManager.scale(1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }
}
