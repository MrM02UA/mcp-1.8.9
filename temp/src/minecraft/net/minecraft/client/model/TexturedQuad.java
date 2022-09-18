package net.minecraft.client.model;

import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Vec3;

public class TexturedQuad {
   public PositionTextureVertex[] field_78239_a;
   public int field_78237_b;
   private boolean field_78238_c;

   public TexturedQuad(PositionTextureVertex[] p_i46364_1_) {
      this.field_78239_a = p_i46364_1_;
      this.field_78237_b = p_i46364_1_.length;
   }

   public TexturedQuad(PositionTextureVertex[] p_i1153_1_, int p_i1153_2_, int p_i1153_3_, int p_i1153_4_, int p_i1153_5_, float p_i1153_6_, float p_i1153_7_) {
      this(p_i1153_1_);
      float lvt_8_1_ = 0.0F / p_i1153_6_;
      float lvt_9_1_ = 0.0F / p_i1153_7_;
      p_i1153_1_[0] = p_i1153_1_[0].func_78240_a((float)p_i1153_4_ / p_i1153_6_ - lvt_8_1_, (float)p_i1153_3_ / p_i1153_7_ + lvt_9_1_);
      p_i1153_1_[1] = p_i1153_1_[1].func_78240_a((float)p_i1153_2_ / p_i1153_6_ + lvt_8_1_, (float)p_i1153_3_ / p_i1153_7_ + lvt_9_1_);
      p_i1153_1_[2] = p_i1153_1_[2].func_78240_a((float)p_i1153_2_ / p_i1153_6_ + lvt_8_1_, (float)p_i1153_5_ / p_i1153_7_ - lvt_9_1_);
      p_i1153_1_[3] = p_i1153_1_[3].func_78240_a((float)p_i1153_4_ / p_i1153_6_ - lvt_8_1_, (float)p_i1153_5_ / p_i1153_7_ - lvt_9_1_);
   }

   public void func_78235_a() {
      PositionTextureVertex[] lvt_1_1_ = new PositionTextureVertex[this.field_78239_a.length];

      for(int lvt_2_1_ = 0; lvt_2_1_ < this.field_78239_a.length; ++lvt_2_1_) {
         lvt_1_1_[lvt_2_1_] = this.field_78239_a[this.field_78239_a.length - lvt_2_1_ - 1];
      }

      this.field_78239_a = lvt_1_1_;
   }

   public void func_178765_a(WorldRenderer p_178765_1_, float p_178765_2_) {
      Vec3 lvt_3_1_ = this.field_78239_a[1].field_78243_a.func_72444_a(this.field_78239_a[0].field_78243_a);
      Vec3 lvt_4_1_ = this.field_78239_a[1].field_78243_a.func_72444_a(this.field_78239_a[2].field_78243_a);
      Vec3 lvt_5_1_ = lvt_4_1_.func_72431_c(lvt_3_1_).func_72432_b();
      float lvt_6_1_ = (float)lvt_5_1_.field_72450_a;
      float lvt_7_1_ = (float)lvt_5_1_.field_72448_b;
      float lvt_8_1_ = (float)lvt_5_1_.field_72449_c;
      if(this.field_78238_c) {
         lvt_6_1_ = -lvt_6_1_;
         lvt_7_1_ = -lvt_7_1_;
         lvt_8_1_ = -lvt_8_1_;
      }

      p_178765_1_.func_181668_a(7, DefaultVertexFormats.field_181703_c);

      for(int lvt_9_1_ = 0; lvt_9_1_ < 4; ++lvt_9_1_) {
         PositionTextureVertex lvt_10_1_ = this.field_78239_a[lvt_9_1_];
         p_178765_1_.func_181662_b(lvt_10_1_.field_78243_a.field_72450_a * (double)p_178765_2_, lvt_10_1_.field_78243_a.field_72448_b * (double)p_178765_2_, lvt_10_1_.field_78243_a.field_72449_c * (double)p_178765_2_).func_181673_a((double)lvt_10_1_.field_78241_b, (double)lvt_10_1_.field_78242_c).func_181663_c(lvt_6_1_, lvt_7_1_, lvt_8_1_).func_181675_d();
      }

      Tessellator.func_178181_a().func_78381_a();
   }
}
