package net.minecraft.client.renderer.tileentity;

import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEntityBeaconRenderer extends TileEntitySpecialRenderer<TileEntityBeacon> {
   private static final ResourceLocation field_147523_b = new ResourceLocation("textures/entity/beacon_beam.png");

   public void func_180535_a(TileEntityBeacon p_180535_1_, double p_180535_2_, double p_180535_4_, double p_180535_6_, float p_180535_8_, int p_180535_9_) {
      float lvt_10_1_ = p_180535_1_.func_146002_i();
      GlStateManager.func_179092_a(516, 0.1F);
      if(lvt_10_1_ > 0.0F) {
         Tessellator lvt_11_1_ = Tessellator.func_178181_a();
         WorldRenderer lvt_12_1_ = lvt_11_1_.func_178180_c();
         GlStateManager.func_179106_n();
         List<TileEntityBeacon.BeamSegment> lvt_13_1_ = p_180535_1_.func_174907_n();
         int lvt_14_1_ = 0;

         for(int lvt_15_1_ = 0; lvt_15_1_ < lvt_13_1_.size(); ++lvt_15_1_) {
            TileEntityBeacon.BeamSegment lvt_16_1_ = (TileEntityBeacon.BeamSegment)lvt_13_1_.get(lvt_15_1_);
            int lvt_17_1_ = lvt_14_1_ + lvt_16_1_.func_177264_c();
            this.func_147499_a(field_147523_b);
            GL11.glTexParameterf(3553, 10242, 10497.0F);
            GL11.glTexParameterf(3553, 10243, 10497.0F);
            GlStateManager.func_179140_f();
            GlStateManager.func_179129_p();
            GlStateManager.func_179084_k();
            GlStateManager.func_179132_a(true);
            GlStateManager.func_179120_a(770, 1, 1, 0);
            double lvt_18_1_ = (double)p_180535_1_.func_145831_w().func_82737_E() + (double)p_180535_8_;
            double lvt_20_1_ = MathHelper.func_181162_h(-lvt_18_1_ * 0.2D - (double)MathHelper.func_76128_c(-lvt_18_1_ * 0.1D));
            float lvt_22_1_ = lvt_16_1_.func_177263_b()[0];
            float lvt_23_1_ = lvt_16_1_.func_177263_b()[1];
            float lvt_24_1_ = lvt_16_1_.func_177263_b()[2];
            double lvt_25_1_ = lvt_18_1_ * 0.025D * -1.5D;
            double lvt_27_1_ = 0.2D;
            double lvt_29_1_ = 0.5D + Math.cos(lvt_25_1_ + 2.356194490192345D) * 0.2D;
            double lvt_31_1_ = 0.5D + Math.sin(lvt_25_1_ + 2.356194490192345D) * 0.2D;
            double lvt_33_1_ = 0.5D + Math.cos(lvt_25_1_ + 0.7853981633974483D) * 0.2D;
            double lvt_35_1_ = 0.5D + Math.sin(lvt_25_1_ + 0.7853981633974483D) * 0.2D;
            double lvt_37_1_ = 0.5D + Math.cos(lvt_25_1_ + 3.9269908169872414D) * 0.2D;
            double lvt_39_1_ = 0.5D + Math.sin(lvt_25_1_ + 3.9269908169872414D) * 0.2D;
            double lvt_41_1_ = 0.5D + Math.cos(lvt_25_1_ + 5.497787143782138D) * 0.2D;
            double lvt_43_1_ = 0.5D + Math.sin(lvt_25_1_ + 5.497787143782138D) * 0.2D;
            double lvt_45_1_ = 0.0D;
            double lvt_47_1_ = 1.0D;
            double lvt_49_1_ = -1.0D + lvt_20_1_;
            double lvt_51_1_ = (double)((float)lvt_16_1_.func_177264_c() * lvt_10_1_) * 2.5D + lvt_49_1_;
            lvt_12_1_.func_181668_a(7, DefaultVertexFormats.field_181709_i);
            lvt_12_1_.func_181662_b(p_180535_2_ + lvt_29_1_, p_180535_4_ + (double)lvt_17_1_, p_180535_6_ + lvt_31_1_).func_181673_a(1.0D, lvt_51_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + lvt_29_1_, p_180535_4_ + (double)lvt_14_1_, p_180535_6_ + lvt_31_1_).func_181673_a(1.0D, lvt_49_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + lvt_33_1_, p_180535_4_ + (double)lvt_14_1_, p_180535_6_ + lvt_35_1_).func_181673_a(0.0D, lvt_49_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + lvt_33_1_, p_180535_4_ + (double)lvt_17_1_, p_180535_6_ + lvt_35_1_).func_181673_a(0.0D, lvt_51_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + lvt_41_1_, p_180535_4_ + (double)lvt_17_1_, p_180535_6_ + lvt_43_1_).func_181673_a(1.0D, lvt_51_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + lvt_41_1_, p_180535_4_ + (double)lvt_14_1_, p_180535_6_ + lvt_43_1_).func_181673_a(1.0D, lvt_49_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + lvt_37_1_, p_180535_4_ + (double)lvt_14_1_, p_180535_6_ + lvt_39_1_).func_181673_a(0.0D, lvt_49_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + lvt_37_1_, p_180535_4_ + (double)lvt_17_1_, p_180535_6_ + lvt_39_1_).func_181673_a(0.0D, lvt_51_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + lvt_33_1_, p_180535_4_ + (double)lvt_17_1_, p_180535_6_ + lvt_35_1_).func_181673_a(1.0D, lvt_51_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + lvt_33_1_, p_180535_4_ + (double)lvt_14_1_, p_180535_6_ + lvt_35_1_).func_181673_a(1.0D, lvt_49_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + lvt_41_1_, p_180535_4_ + (double)lvt_14_1_, p_180535_6_ + lvt_43_1_).func_181673_a(0.0D, lvt_49_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + lvt_41_1_, p_180535_4_ + (double)lvt_17_1_, p_180535_6_ + lvt_43_1_).func_181673_a(0.0D, lvt_51_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + lvt_37_1_, p_180535_4_ + (double)lvt_17_1_, p_180535_6_ + lvt_39_1_).func_181673_a(1.0D, lvt_51_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + lvt_37_1_, p_180535_4_ + (double)lvt_14_1_, p_180535_6_ + lvt_39_1_).func_181673_a(1.0D, lvt_49_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + lvt_29_1_, p_180535_4_ + (double)lvt_14_1_, p_180535_6_ + lvt_31_1_).func_181673_a(0.0D, lvt_49_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + lvt_29_1_, p_180535_4_ + (double)lvt_17_1_, p_180535_6_ + lvt_31_1_).func_181673_a(0.0D, lvt_51_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).func_181675_d();
            lvt_11_1_.func_78381_a();
            GlStateManager.func_179147_l();
            GlStateManager.func_179120_a(770, 771, 1, 0);
            GlStateManager.func_179132_a(false);
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
            lvt_47_1_ = (double)((float)lvt_16_1_.func_177264_c() * lvt_10_1_) + lvt_45_1_;
            lvt_12_1_.func_181668_a(7, DefaultVertexFormats.field_181709_i);
            lvt_12_1_.func_181662_b(p_180535_2_ + 0.2D, p_180535_4_ + (double)lvt_17_1_, p_180535_6_ + 0.2D).func_181673_a(1.0D, lvt_47_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + 0.2D, p_180535_4_ + (double)lvt_14_1_, p_180535_6_ + 0.2D).func_181673_a(1.0D, lvt_45_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + 0.8D, p_180535_4_ + (double)lvt_14_1_, p_180535_6_ + 0.2D).func_181673_a(0.0D, lvt_45_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + 0.8D, p_180535_4_ + (double)lvt_17_1_, p_180535_6_ + 0.2D).func_181673_a(0.0D, lvt_47_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + 0.8D, p_180535_4_ + (double)lvt_17_1_, p_180535_6_ + 0.8D).func_181673_a(1.0D, lvt_47_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + 0.8D, p_180535_4_ + (double)lvt_14_1_, p_180535_6_ + 0.8D).func_181673_a(1.0D, lvt_45_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + 0.2D, p_180535_4_ + (double)lvt_14_1_, p_180535_6_ + 0.8D).func_181673_a(0.0D, lvt_45_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + 0.2D, p_180535_4_ + (double)lvt_17_1_, p_180535_6_ + 0.8D).func_181673_a(0.0D, lvt_47_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + 0.8D, p_180535_4_ + (double)lvt_17_1_, p_180535_6_ + 0.2D).func_181673_a(1.0D, lvt_47_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + 0.8D, p_180535_4_ + (double)lvt_14_1_, p_180535_6_ + 0.2D).func_181673_a(1.0D, lvt_45_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + 0.8D, p_180535_4_ + (double)lvt_14_1_, p_180535_6_ + 0.8D).func_181673_a(0.0D, lvt_45_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + 0.8D, p_180535_4_ + (double)lvt_17_1_, p_180535_6_ + 0.8D).func_181673_a(0.0D, lvt_47_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + 0.2D, p_180535_4_ + (double)lvt_17_1_, p_180535_6_ + 0.8D).func_181673_a(1.0D, lvt_47_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + 0.2D, p_180535_4_ + (double)lvt_14_1_, p_180535_6_ + 0.8D).func_181673_a(1.0D, lvt_45_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + 0.2D, p_180535_4_ + (double)lvt_14_1_, p_180535_6_ + 0.2D).func_181673_a(0.0D, lvt_45_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).func_181675_d();
            lvt_12_1_.func_181662_b(p_180535_2_ + 0.2D, p_180535_4_ + (double)lvt_17_1_, p_180535_6_ + 0.2D).func_181673_a(0.0D, lvt_47_1_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 0.125F).func_181675_d();
            lvt_11_1_.func_78381_a();
            GlStateManager.func_179145_e();
            GlStateManager.func_179098_w();
            GlStateManager.func_179132_a(true);
            lvt_14_1_ = lvt_17_1_;
         }

         GlStateManager.func_179127_m();
      }

   }

   public boolean func_181055_a() {
      return true;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void func_180535_a(TileEntity p_180535_1_, double p_180535_2_, double p_180535_4_, double p_180535_6_, float p_180535_8_, int p_180535_9_) {
      this.func_180535_a((TileEntityBeacon)p_180535_1_, p_180535_2_, p_180535_4_, p_180535_6_, p_180535_8_, p_180535_9_);
   }
}
