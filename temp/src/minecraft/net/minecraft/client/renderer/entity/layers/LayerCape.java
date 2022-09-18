package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;

public class LayerCape implements LayerRenderer<AbstractClientPlayer> {
   private final RenderPlayer field_177167_a;

   public LayerCape(RenderPlayer p_i46123_1_) {
      this.field_177167_a = p_i46123_1_;
   }

   public void func_177141_a(AbstractClientPlayer p_177141_1_, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_) {
      if(p_177141_1_.func_152122_n() && !p_177141_1_.func_82150_aj() && p_177141_1_.func_175148_a(EnumPlayerModelParts.CAPE) && p_177141_1_.func_110303_q() != null) {
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         this.field_177167_a.func_110776_a(p_177141_1_.func_110303_q());
         GlStateManager.func_179094_E();
         GlStateManager.func_179109_b(0.0F, 0.0F, 0.125F);
         double lvt_9_1_ = p_177141_1_.field_71091_bM + (p_177141_1_.field_71094_bP - p_177141_1_.field_71091_bM) * (double)p_177141_4_ - (p_177141_1_.field_70169_q + (p_177141_1_.field_70165_t - p_177141_1_.field_70169_q) * (double)p_177141_4_);
         double lvt_11_1_ = p_177141_1_.field_71096_bN + (p_177141_1_.field_71095_bQ - p_177141_1_.field_71096_bN) * (double)p_177141_4_ - (p_177141_1_.field_70167_r + (p_177141_1_.field_70163_u - p_177141_1_.field_70167_r) * (double)p_177141_4_);
         double lvt_13_1_ = p_177141_1_.field_71097_bO + (p_177141_1_.field_71085_bR - p_177141_1_.field_71097_bO) * (double)p_177141_4_ - (p_177141_1_.field_70166_s + (p_177141_1_.field_70161_v - p_177141_1_.field_70166_s) * (double)p_177141_4_);
         float lvt_15_1_ = p_177141_1_.field_70760_ar + (p_177141_1_.field_70761_aq - p_177141_1_.field_70760_ar) * p_177141_4_;
         double lvt_16_1_ = (double)MathHelper.func_76126_a(lvt_15_1_ * 3.1415927F / 180.0F);
         double lvt_18_1_ = (double)(-MathHelper.func_76134_b(lvt_15_1_ * 3.1415927F / 180.0F));
         float lvt_20_1_ = (float)lvt_11_1_ * 10.0F;
         lvt_20_1_ = MathHelper.func_76131_a(lvt_20_1_, -6.0F, 32.0F);
         float lvt_21_1_ = (float)(lvt_9_1_ * lvt_16_1_ + lvt_13_1_ * lvt_18_1_) * 100.0F;
         float lvt_22_1_ = (float)(lvt_9_1_ * lvt_18_1_ - lvt_13_1_ * lvt_16_1_) * 100.0F;
         if(lvt_21_1_ < 0.0F) {
            lvt_21_1_ = 0.0F;
         }

         float lvt_23_1_ = p_177141_1_.field_71107_bF + (p_177141_1_.field_71109_bG - p_177141_1_.field_71107_bF) * p_177141_4_;
         lvt_20_1_ = lvt_20_1_ + MathHelper.func_76126_a((p_177141_1_.field_70141_P + (p_177141_1_.field_70140_Q - p_177141_1_.field_70141_P) * p_177141_4_) * 6.0F) * 32.0F * lvt_23_1_;
         if(p_177141_1_.func_70093_af()) {
            lvt_20_1_ += 25.0F;
         }

         GlStateManager.func_179114_b(6.0F + lvt_21_1_ / 2.0F + lvt_20_1_, 1.0F, 0.0F, 0.0F);
         GlStateManager.func_179114_b(lvt_22_1_ / 2.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.func_179114_b(-lvt_22_1_ / 2.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b(180.0F, 0.0F, 1.0F, 0.0F);
         this.field_177167_a.func_177087_b().func_178728_c(0.0625F);
         GlStateManager.func_179121_F();
      }
   }

   public boolean func_177142_b() {
      return false;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void func_177141_a(EntityLivingBase p_177141_1_, float p_177141_2_, float p_177141_3_, float p_177141_4_, float p_177141_5_, float p_177141_6_, float p_177141_7_, float p_177141_8_) {
      this.func_177141_a((AbstractClientPlayer)p_177141_1_, p_177141_2_, p_177141_3_, p_177141_4_, p_177141_5_, p_177141_6_, p_177141_7_, p_177141_8_);
   }
}
