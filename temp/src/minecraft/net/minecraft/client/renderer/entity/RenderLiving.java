package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

public abstract class RenderLiving<T extends EntityLiving> extends RendererLivingEntity<T> {
   public RenderLiving(RenderManager p_i46153_1_, ModelBase p_i46153_2_, float p_i46153_3_) {
      super(p_i46153_1_, p_i46153_2_, p_i46153_3_);
   }

   protected boolean func_177070_b(T p_177070_1_) {
      return super.func_177070_b(p_177070_1_) && (p_177070_1_.func_94059_bO() || p_177070_1_.func_145818_k_() && p_177070_1_ == this.field_76990_c.field_147941_i);
   }

   public boolean func_177071_a(T p_177071_1_, ICamera p_177071_2_, double p_177071_3_, double p_177071_5_, double p_177071_7_) {
      if(super.func_177071_a(p_177071_1_, p_177071_2_, p_177071_3_, p_177071_5_, p_177071_7_)) {
         return true;
      } else if(p_177071_1_.func_110167_bD() && p_177071_1_.func_110166_bE() != null) {
         Entity lvt_9_1_ = p_177071_1_.func_110166_bE();
         return p_177071_2_.func_78546_a(lvt_9_1_.func_174813_aQ());
      } else {
         return false;
      }
   }

   public void func_76986_a(T p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      super.func_76986_a(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
      this.func_110827_b(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
   }

   public void func_177105_a(T p_177105_1_, float p_177105_2_) {
      int lvt_3_1_ = p_177105_1_.func_70070_b(p_177105_2_);
      int lvt_4_1_ = lvt_3_1_ % 65536;
      int lvt_5_1_ = lvt_3_1_ / 65536;
      OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, (float)lvt_4_1_ / 1.0F, (float)lvt_5_1_ / 1.0F);
   }

   private double func_110828_a(double p_110828_1_, double p_110828_3_, double p_110828_5_) {
      return p_110828_1_ + (p_110828_3_ - p_110828_1_) * p_110828_5_;
   }

   protected void func_110827_b(T p_110827_1_, double p_110827_2_, double p_110827_4_, double p_110827_6_, float p_110827_8_, float p_110827_9_) {
      Entity lvt_10_1_ = p_110827_1_.func_110166_bE();
      if(lvt_10_1_ != null) {
         p_110827_4_ = p_110827_4_ - (1.6D - (double)p_110827_1_.field_70131_O) * 0.5D;
         Tessellator lvt_11_1_ = Tessellator.func_178181_a();
         WorldRenderer lvt_12_1_ = lvt_11_1_.func_178180_c();
         double lvt_13_1_ = this.func_110828_a((double)lvt_10_1_.field_70126_B, (double)lvt_10_1_.field_70177_z, (double)(p_110827_9_ * 0.5F)) * 0.01745329238474369D;
         double lvt_15_1_ = this.func_110828_a((double)lvt_10_1_.field_70127_C, (double)lvt_10_1_.field_70125_A, (double)(p_110827_9_ * 0.5F)) * 0.01745329238474369D;
         double lvt_17_1_ = Math.cos(lvt_13_1_);
         double lvt_19_1_ = Math.sin(lvt_13_1_);
         double lvt_21_1_ = Math.sin(lvt_15_1_);
         if(lvt_10_1_ instanceof EntityHanging) {
            lvt_17_1_ = 0.0D;
            lvt_19_1_ = 0.0D;
            lvt_21_1_ = -1.0D;
         }

         double lvt_23_1_ = Math.cos(lvt_15_1_);
         double lvt_25_1_ = this.func_110828_a(lvt_10_1_.field_70169_q, lvt_10_1_.field_70165_t, (double)p_110827_9_) - lvt_17_1_ * 0.7D - lvt_19_1_ * 0.5D * lvt_23_1_;
         double lvt_27_1_ = this.func_110828_a(lvt_10_1_.field_70167_r + (double)lvt_10_1_.func_70047_e() * 0.7D, lvt_10_1_.field_70163_u + (double)lvt_10_1_.func_70047_e() * 0.7D, (double)p_110827_9_) - lvt_21_1_ * 0.5D - 0.25D;
         double lvt_29_1_ = this.func_110828_a(lvt_10_1_.field_70166_s, lvt_10_1_.field_70161_v, (double)p_110827_9_) - lvt_19_1_ * 0.7D + lvt_17_1_ * 0.5D * lvt_23_1_;
         double lvt_31_1_ = this.func_110828_a((double)p_110827_1_.field_70760_ar, (double)p_110827_1_.field_70761_aq, (double)p_110827_9_) * 0.01745329238474369D + 1.5707963267948966D;
         lvt_17_1_ = Math.cos(lvt_31_1_) * (double)p_110827_1_.field_70130_N * 0.4D;
         lvt_19_1_ = Math.sin(lvt_31_1_) * (double)p_110827_1_.field_70130_N * 0.4D;
         double lvt_33_1_ = this.func_110828_a(p_110827_1_.field_70169_q, p_110827_1_.field_70165_t, (double)p_110827_9_) + lvt_17_1_;
         double lvt_35_1_ = this.func_110828_a(p_110827_1_.field_70167_r, p_110827_1_.field_70163_u, (double)p_110827_9_);
         double lvt_37_1_ = this.func_110828_a(p_110827_1_.field_70166_s, p_110827_1_.field_70161_v, (double)p_110827_9_) + lvt_19_1_;
         p_110827_2_ = p_110827_2_ + lvt_17_1_;
         p_110827_6_ = p_110827_6_ + lvt_19_1_;
         double lvt_39_1_ = (double)((float)(lvt_25_1_ - lvt_33_1_));
         double lvt_41_1_ = (double)((float)(lvt_27_1_ - lvt_35_1_));
         double lvt_43_1_ = (double)((float)(lvt_29_1_ - lvt_37_1_));
         GlStateManager.func_179090_x();
         GlStateManager.func_179140_f();
         GlStateManager.func_179129_p();
         int lvt_45_1_ = 24;
         double lvt_46_1_ = 0.025D;
         lvt_12_1_.func_181668_a(5, DefaultVertexFormats.field_181706_f);

         for(int lvt_48_1_ = 0; lvt_48_1_ <= 24; ++lvt_48_1_) {
            float lvt_49_1_ = 0.5F;
            float lvt_50_1_ = 0.4F;
            float lvt_51_1_ = 0.3F;
            if(lvt_48_1_ % 2 == 0) {
               lvt_49_1_ *= 0.7F;
               lvt_50_1_ *= 0.7F;
               lvt_51_1_ *= 0.7F;
            }

            float lvt_52_1_ = (float)lvt_48_1_ / 24.0F;
            lvt_12_1_.func_181662_b(p_110827_2_ + lvt_39_1_ * (double)lvt_52_1_ + 0.0D, p_110827_4_ + lvt_41_1_ * (double)(lvt_52_1_ * lvt_52_1_ + lvt_52_1_) * 0.5D + (double)((24.0F - (float)lvt_48_1_) / 18.0F + 0.125F), p_110827_6_ + lvt_43_1_ * (double)lvt_52_1_).func_181666_a(lvt_49_1_, lvt_50_1_, lvt_51_1_, 1.0F).func_181675_d();
            lvt_12_1_.func_181662_b(p_110827_2_ + lvt_39_1_ * (double)lvt_52_1_ + 0.025D, p_110827_4_ + lvt_41_1_ * (double)(lvt_52_1_ * lvt_52_1_ + lvt_52_1_) * 0.5D + (double)((24.0F - (float)lvt_48_1_) / 18.0F + 0.125F) + 0.025D, p_110827_6_ + lvt_43_1_ * (double)lvt_52_1_).func_181666_a(lvt_49_1_, lvt_50_1_, lvt_51_1_, 1.0F).func_181675_d();
         }

         lvt_11_1_.func_78381_a();
         lvt_12_1_.func_181668_a(5, DefaultVertexFormats.field_181706_f);

         for(int lvt_48_2_ = 0; lvt_48_2_ <= 24; ++lvt_48_2_) {
            float lvt_49_2_ = 0.5F;
            float lvt_50_2_ = 0.4F;
            float lvt_51_2_ = 0.3F;
            if(lvt_48_2_ % 2 == 0) {
               lvt_49_2_ *= 0.7F;
               lvt_50_2_ *= 0.7F;
               lvt_51_2_ *= 0.7F;
            }

            float lvt_52_2_ = (float)lvt_48_2_ / 24.0F;
            lvt_12_1_.func_181662_b(p_110827_2_ + lvt_39_1_ * (double)lvt_52_2_ + 0.0D, p_110827_4_ + lvt_41_1_ * (double)(lvt_52_2_ * lvt_52_2_ + lvt_52_2_) * 0.5D + (double)((24.0F - (float)lvt_48_2_) / 18.0F + 0.125F) + 0.025D, p_110827_6_ + lvt_43_1_ * (double)lvt_52_2_).func_181666_a(lvt_49_2_, lvt_50_2_, lvt_51_2_, 1.0F).func_181675_d();
            lvt_12_1_.func_181662_b(p_110827_2_ + lvt_39_1_ * (double)lvt_52_2_ + 0.025D, p_110827_4_ + lvt_41_1_ * (double)(lvt_52_2_ * lvt_52_2_ + lvt_52_2_) * 0.5D + (double)((24.0F - (float)lvt_48_2_) / 18.0F + 0.125F), p_110827_6_ + lvt_43_1_ * (double)lvt_52_2_ + 0.025D).func_181666_a(lvt_49_2_, lvt_50_2_, lvt_51_2_, 1.0F).func_181675_d();
         }

         lvt_11_1_.func_78381_a();
         GlStateManager.func_179145_e();
         GlStateManager.func_179098_w();
         GlStateManager.func_179089_o();
      }

   }

   // $FF: synthetic method
   protected boolean func_177070_b(EntityLivingBase p_177070_1_) {
      return this.func_177070_b((EntityLiving)p_177070_1_);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void func_76986_a(EntityLivingBase p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      this.func_76986_a((EntityLiving)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected boolean func_177070_b(Entity p_177070_1_) {
      return this.func_177070_b((EntityLiving)p_177070_1_);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void func_76986_a(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      this.func_76986_a((EntityLiving)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public boolean func_177071_a(Entity p_177071_1_, ICamera p_177071_2_, double p_177071_3_, double p_177071_5_, double p_177071_7_) {
      return this.func_177071_a((EntityLiving)p_177071_1_, p_177071_2_, p_177071_3_, p_177071_5_, p_177071_7_);
   }
}
