package net.minecraft.client.renderer.tileentity;

import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEntityBeaconRenderer extends TileEntitySpecialRenderer<TileEntityBeacon>
{
    private static final ResourceLocation beaconBeam = new ResourceLocation("textures/entity/beacon_beam.png");

    public void renderTileEntityAt(TileEntityBeacon te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        float lvt_10_1_ = te.shouldBeamRender();
        GlStateManager.alphaFunc(516, 0.1F);

        if (lvt_10_1_ > 0.0F)
        {
            Tessellator lvt_11_1_ = Tessellator.getInstance();
            WorldRenderer lvt_12_1_ = lvt_11_1_.getWorldRenderer();
            GlStateManager.disableFog();
            List<TileEntityBeacon.BeamSegment> lvt_13_1_ = te.getBeamSegments();
            int lvt_14_1_ = 0;

            for (int lvt_15_1_ = 0; lvt_15_1_ < lvt_13_1_.size(); ++lvt_15_1_)
            {
                TileEntityBeacon.BeamSegment lvt_16_1_ = (TileEntityBeacon.BeamSegment)lvt_13_1_.get(lvt_15_1_);
                int lvt_17_1_ = lvt_14_1_ + lvt_16_1_.getHeight();
                this.bindTexture(beaconBeam);
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
                GlStateManager.disableLighting();
                GlStateManager.disableCull();
                GlStateManager.disableBlend();
                GlStateManager.depthMask(true);
                GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
                double lvt_18_1_ = (double)te.getWorld().getTotalWorldTime() + (double)partialTicks;
                double lvt_20_1_ = MathHelper.func_181162_h(-lvt_18_1_ * 0.2D - (double)MathHelper.floor_double(-lvt_18_1_ * 0.1D));
                float lvt_22_1_ = lvt_16_1_.getColors()[0];
                float lvt_23_1_ = lvt_16_1_.getColors()[1];
                float lvt_24_1_ = lvt_16_1_.getColors()[2];
                double lvt_25_1_ = lvt_18_1_ * 0.025D * -1.5D;
                double lvt_27_1_ = 0.2D;
                double lvt_29_1_ = 0.5D + Math.cos(lvt_25_1_ + 2.356194490192345D) * 0.2D;
                double lvt_31_1_ = 0.5D + Math.sin(lvt_25_1_ + 2.356194490192345D) * 0.2D;
                double lvt_33_1_ = 0.5D + Math.cos(lvt_25_1_ + (Math.PI / 4D)) * 0.2D;
                double lvt_35_1_ = 0.5D + Math.sin(lvt_25_1_ + (Math.PI / 4D)) * 0.2D;
                double lvt_37_1_ = 0.5D + Math.cos(lvt_25_1_ + 3.9269908169872414D) * 0.2D;
                double lvt_39_1_ = 0.5D + Math.sin(lvt_25_1_ + 3.9269908169872414D) * 0.2D;
                double lvt_41_1_ = 0.5D + Math.cos(lvt_25_1_ + 5.497787143782138D) * 0.2D;
                double lvt_43_1_ = 0.5D + Math.sin(lvt_25_1_ + 5.497787143782138D) * 0.2D;
                double lvt_45_1_ = 0.0D;
                double lvt_47_1_ = 1.0D;
                double lvt_49_1_ = -1.0D + lvt_20_1_;
                double lvt_51_1_ = (double)((float)lvt_16_1_.getHeight() * lvt_10_1_) * 2.5D + lvt_49_1_;
                lvt_12_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                lvt_12_1_.pos(x + lvt_29_1_, y + (double)lvt_17_1_, z + lvt_31_1_).tex(1.0D, lvt_51_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).endVertex();
                lvt_12_1_.pos(x + lvt_29_1_, y + (double)lvt_14_1_, z + lvt_31_1_).tex(1.0D, lvt_49_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).endVertex();
                lvt_12_1_.pos(x + lvt_33_1_, y + (double)lvt_14_1_, z + lvt_35_1_).tex(0.0D, lvt_49_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).endVertex();
                lvt_12_1_.pos(x + lvt_33_1_, y + (double)lvt_17_1_, z + lvt_35_1_).tex(0.0D, lvt_51_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).endVertex();
                lvt_12_1_.pos(x + lvt_41_1_, y + (double)lvt_17_1_, z + lvt_43_1_).tex(1.0D, lvt_51_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).endVertex();
                lvt_12_1_.pos(x + lvt_41_1_, y + (double)lvt_14_1_, z + lvt_43_1_).tex(1.0D, lvt_49_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).endVertex();
                lvt_12_1_.pos(x + lvt_37_1_, y + (double)lvt_14_1_, z + lvt_39_1_).tex(0.0D, lvt_49_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).endVertex();
                lvt_12_1_.pos(x + lvt_37_1_, y + (double)lvt_17_1_, z + lvt_39_1_).tex(0.0D, lvt_51_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).endVertex();
                lvt_12_1_.pos(x + lvt_33_1_, y + (double)lvt_17_1_, z + lvt_35_1_).tex(1.0D, lvt_51_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).endVertex();
                lvt_12_1_.pos(x + lvt_33_1_, y + (double)lvt_14_1_, z + lvt_35_1_).tex(1.0D, lvt_49_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).endVertex();
                lvt_12_1_.pos(x + lvt_41_1_, y + (double)lvt_14_1_, z + lvt_43_1_).tex(0.0D, lvt_49_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).endVertex();
                lvt_12_1_.pos(x + lvt_41_1_, y + (double)lvt_17_1_, z + lvt_43_1_).tex(0.0D, lvt_51_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).endVertex();
                lvt_12_1_.pos(x + lvt_37_1_, y + (double)lvt_17_1_, z + lvt_39_1_).tex(1.0D, lvt_51_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).endVertex();
                lvt_12_1_.pos(x + lvt_37_1_, y + (double)lvt_14_1_, z + lvt_39_1_).tex(1.0D, lvt_49_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).endVertex();
                lvt_12_1_.pos(x + lvt_29_1_, y + (double)lvt_14_1_, z + lvt_31_1_).tex(0.0D, lvt_49_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).endVertex();
                lvt_12_1_.pos(x + lvt_29_1_, y + (double)lvt_17_1_, z + lvt_31_1_).tex(0.0D, lvt_51_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).endVertex();
                lvt_11_1_.draw();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.depthMask(false);
                lvt_25_1_ = 0.2D;
                lvt_27_1_ = 0.2D;
                lvt_29_1_ = 0.8D;
                lvt_31_1_ = 0.2D;
                lvt_33_1_ = 0.2D;
                lvt_35_1_ = 0.8D;
                lvt_37_1_ = 0.8D;
                lvt_39_1_ = 0.8D;
                lvt_41_1_ = 0.0D;
                lvt_43_1_ = 1.0D;
                lvt_45_1_ = -1.0D + lvt_20_1_;
                lvt_47_1_ = (double)((float)lvt_16_1_.getHeight() * lvt_10_1_) + lvt_45_1_;
                lvt_12_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                lvt_12_1_.pos(x + 0.2D, y + (double)lvt_17_1_, z + 0.2D).tex(1.0D, lvt_47_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).endVertex();
                lvt_12_1_.pos(x + 0.2D, y + (double)lvt_14_1_, z + 0.2D).tex(1.0D, lvt_45_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).endVertex();
                lvt_12_1_.pos(x + 0.8D, y + (double)lvt_14_1_, z + 0.2D).tex(0.0D, lvt_45_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).endVertex();
                lvt_12_1_.pos(x + 0.8D, y + (double)lvt_17_1_, z + 0.2D).tex(0.0D, lvt_47_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).endVertex();
                lvt_12_1_.pos(x + 0.8D, y + (double)lvt_17_1_, z + 0.8D).tex(1.0D, lvt_47_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).endVertex();
                lvt_12_1_.pos(x + 0.8D, y + (double)lvt_14_1_, z + 0.8D).tex(1.0D, lvt_45_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).endVertex();
                lvt_12_1_.pos(x + 0.2D, y + (double)lvt_14_1_, z + 0.8D).tex(0.0D, lvt_45_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).endVertex();
                lvt_12_1_.pos(x + 0.2D, y + (double)lvt_17_1_, z + 0.8D).tex(0.0D, lvt_47_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).endVertex();
                lvt_12_1_.pos(x + 0.8D, y + (double)lvt_17_1_, z + 0.2D).tex(1.0D, lvt_47_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).endVertex();
                lvt_12_1_.pos(x + 0.8D, y + (double)lvt_14_1_, z + 0.2D).tex(1.0D, lvt_45_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).endVertex();
                lvt_12_1_.pos(x + 0.8D, y + (double)lvt_14_1_, z + 0.8D).tex(0.0D, lvt_45_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).endVertex();
                lvt_12_1_.pos(x + 0.8D, y + (double)lvt_17_1_, z + 0.8D).tex(0.0D, lvt_47_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).endVertex();
                lvt_12_1_.pos(x + 0.2D, y + (double)lvt_17_1_, z + 0.8D).tex(1.0D, lvt_47_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).endVertex();
                lvt_12_1_.pos(x + 0.2D, y + (double)lvt_14_1_, z + 0.8D).tex(1.0D, lvt_45_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).endVertex();
                lvt_12_1_.pos(x + 0.2D, y + (double)lvt_14_1_, z + 0.2D).tex(0.0D, lvt_45_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).endVertex();
                lvt_12_1_.pos(x + 0.2D, y + (double)lvt_17_1_, z + 0.2D).tex(0.0D, lvt_47_1_).color(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).endVertex();
                lvt_11_1_.draw();
                GlStateManager.enableLighting();
                GlStateManager.enableTexture2D();
                GlStateManager.depthMask(true);
                lvt_14_1_ = lvt_17_1_;
            }

            GlStateManager.enableFog();
        }
    }

    /**
     * If true the {@link TileEntitySpecialRenderer} will always be rendered while the player is in the render bounding
     * box {@link TileEntity#getRenderBoundingBox()} and his squared distance with the {@link TileEntity} is smaller
     * than {@link TileEntity#getMaxRenderDistanceSquared()}.
     */
    public boolean forceTileEntityRender()
    {
        return true;
    }

    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        this.renderTileEntityAt((TileEntityBeacon)te, x, y, z, partialTicks, destroyStage);
    }
}
