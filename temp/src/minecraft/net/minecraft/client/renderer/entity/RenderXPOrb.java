package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderXPOrb extends Render<EntityXPOrb> {
   private static final ResourceLocation field_110785_a = new ResourceLocation("textures/entity/experience_orb.png");

   public RenderXPOrb(RenderManager p_i46178_1_) {
      super(p_i46178_1_);
      this.field_76989_e = 0.15F;
      this.field_76987_f = 0.75F;
   }

   public void func_76986_a(EntityXPOrb p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b((float)p_76986_2_, (float)p_76986_4_, (float)p_76986_6_);
      this.func_180548_c(p_76986_1_);
      int lvt_10_1_ = p_76986_1_.func_70528_g();
      float lvt_11_1_ = (float)(lvt_10_1_ % 4 * 16 + 0) / 64.0F;
      float lvt_12_1_ = (float)(lvt_10_1_ % 4 * 16 + 16) / 64.0F;
      float lvt_13_1_ = (float)(lvt_10_1_ / 4 * 16 + 0) / 64.0F;
      float lvt_14_1_ = (float)(lvt_10_1_ / 4 * 16 + 16) / 64.0F;
      float lvt_15_1_ = 1.0F;
      float lvt_16_1_ = 0.5F;
      float lvt_17_1_ = 0.25F;
      int lvt_18_1_ = p_76986_1_.func_70070_b(p_76986_9_);
      int lvt_19_1_ = lvt_18_1_ % 65536;
      int lvt_20_1_ = lvt_18_1_ / 65536;
      OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, (float)lvt_19_1_ / 1.0F, (float)lvt_20_1_ / 1.0F);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      float lvt_18_2_ = 255.0F;
      float lvt_19_2_ = ((float)p_76986_1_.field_70533_a + p_76986_9_) / 2.0F;
      lvt_20_1_ = (int)((MathHelper.func_76126_a(lvt_19_2_ + 0.0F) + 1.0F) * 0.5F * 255.0F);
      int lvt_21_1_ = 255;
      int lvt_22_1_ = (int)((MathHelper.func_76126_a(lvt_19_2_ + 4.1887903F) + 1.0F) * 0.1F * 255.0F);
      GlStateManager.func_179114_b(180.0F - this.field_76990_c.field_78735_i, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(-this.field_76990_c.field_78732_j, 1.0F, 0.0F, 0.0F);
      float lvt_23_1_ = 0.3F;
      GlStateManager.func_179152_a(0.3F, 0.3F, 0.3F);
      Tessellator lvt_24_1_ = Tessellator.func_178181_a();
      WorldRenderer lvt_25_1_ = lvt_24_1_.func_178180_c();
      lvt_25_1_.func_181668_a(7, DefaultVertexFormats.field_181712_l);
      lvt_25_1_.func_181662_b((double)(0.0F - lvt_16_1_), (double)(0.0F - lvt_17_1_), 0.0D).func_181673_a((double)lvt_11_1_, (double)lvt_14_1_).func_181669_b(lvt_20_1_, 255, lvt_22_1_, 128).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      lvt_25_1_.func_181662_b((double)(lvt_15_1_ - lvt_16_1_), (double)(0.0F - lvt_17_1_), 0.0D).func_181673_a((double)lvt_12_1_, (double)lvt_14_1_).func_181669_b(lvt_20_1_, 255, lvt_22_1_, 128).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      lvt_25_1_.func_181662_b((double)(lvt_15_1_ - lvt_16_1_), (double)(1.0F - lvt_17_1_), 0.0D).func_181673_a((double)lvt_12_1_, (double)lvt_13_1_).func_181669_b(lvt_20_1_, 255, lvt_22_1_, 128).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      lvt_25_1_.func_181662_b((double)(0.0F - lvt_16_1_), (double)(1.0F - lvt_17_1_), 0.0D).func_181673_a((double)lvt_11_1_, (double)lvt_13_1_).func_181669_b(lvt_20_1_, 255, lvt_22_1_, 128).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      lvt_24_1_.func_78381_a();
      GlStateManager.func_179084_k();
      GlStateManager.func_179101_C();
      GlStateManager.func_179121_F();
      super.func_76986_a(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
   }

   protected ResourceLocation func_110775_a(EntityXPOrb p_110775_1_) {
      return field_110785_a;
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected ResourceLocation func_110775_a(Entity p_110775_1_) {
      return this.func_110775_a((EntityXPOrb)p_110775_1_);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void func_76986_a(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      this.func_76986_a((EntityXPOrb)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
   }
}
