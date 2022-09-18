package net.minecraft.client.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.WorldRenderer;

public class ModelBox {
   private PositionTextureVertex[] field_78253_h;
   private TexturedQuad[] field_78254_i;
   public final float field_78252_a;
   public final float field_78250_b;
   public final float field_78251_c;
   public final float field_78248_d;
   public final float field_78249_e;
   public final float field_78246_f;
   public String field_78247_g;

   public ModelBox(ModelRenderer p_i46359_1_, int p_i46359_2_, int p_i46359_3_, float p_i46359_4_, float p_i46359_5_, float p_i46359_6_, int p_i46359_7_, int p_i46359_8_, int p_i46359_9_, float p_i46359_10_) {
      this(p_i46359_1_, p_i46359_2_, p_i46359_3_, p_i46359_4_, p_i46359_5_, p_i46359_6_, p_i46359_7_, p_i46359_8_, p_i46359_9_, p_i46359_10_, p_i46359_1_.field_78809_i);
   }

   public ModelBox(ModelRenderer p_i46301_1_, int p_i46301_2_, int p_i46301_3_, float p_i46301_4_, float p_i46301_5_, float p_i46301_6_, int p_i46301_7_, int p_i46301_8_, int p_i46301_9_, float p_i46301_10_, boolean p_i46301_11_) {
      this.field_78252_a = p_i46301_4_;
      this.field_78250_b = p_i46301_5_;
      this.field_78251_c = p_i46301_6_;
      this.field_78248_d = p_i46301_4_ + (float)p_i46301_7_;
      this.field_78249_e = p_i46301_5_ + (float)p_i46301_8_;
      this.field_78246_f = p_i46301_6_ + (float)p_i46301_9_;
      this.field_78253_h = new PositionTextureVertex[8];
      this.field_78254_i = new TexturedQuad[6];
      float lvt_12_1_ = p_i46301_4_ + (float)p_i46301_7_;
      float lvt_13_1_ = p_i46301_5_ + (float)p_i46301_8_;
      float lvt_14_1_ = p_i46301_6_ + (float)p_i46301_9_;
      p_i46301_4_ = p_i46301_4_ - p_i46301_10_;
      p_i46301_5_ = p_i46301_5_ - p_i46301_10_;
      p_i46301_6_ = p_i46301_6_ - p_i46301_10_;
      lvt_12_1_ = lvt_12_1_ + p_i46301_10_;
      lvt_13_1_ = lvt_13_1_ + p_i46301_10_;
      lvt_14_1_ = lvt_14_1_ + p_i46301_10_;
      if(p_i46301_11_) {
         float lvt_15_1_ = lvt_12_1_;
         lvt_12_1_ = p_i46301_4_;
         p_i46301_4_ = lvt_15_1_;
      }

      PositionTextureVertex lvt_15_2_ = new PositionTextureVertex(p_i46301_4_, p_i46301_5_, p_i46301_6_, 0.0F, 0.0F);
      PositionTextureVertex lvt_16_1_ = new PositionTextureVertex(lvt_12_1_, p_i46301_5_, p_i46301_6_, 0.0F, 8.0F);
      PositionTextureVertex lvt_17_1_ = new PositionTextureVertex(lvt_12_1_, lvt_13_1_, p_i46301_6_, 8.0F, 8.0F);
      PositionTextureVertex lvt_18_1_ = new PositionTextureVertex(p_i46301_4_, lvt_13_1_, p_i46301_6_, 8.0F, 0.0F);
      PositionTextureVertex lvt_19_1_ = new PositionTextureVertex(p_i46301_4_, p_i46301_5_, lvt_14_1_, 0.0F, 0.0F);
      PositionTextureVertex lvt_20_1_ = new PositionTextureVertex(lvt_12_1_, p_i46301_5_, lvt_14_1_, 0.0F, 8.0F);
      PositionTextureVertex lvt_21_1_ = new PositionTextureVertex(lvt_12_1_, lvt_13_1_, lvt_14_1_, 8.0F, 8.0F);
      PositionTextureVertex lvt_22_1_ = new PositionTextureVertex(p_i46301_4_, lvt_13_1_, lvt_14_1_, 8.0F, 0.0F);
      this.field_78253_h[0] = lvt_15_2_;
      this.field_78253_h[1] = lvt_16_1_;
      this.field_78253_h[2] = lvt_17_1_;
      this.field_78253_h[3] = lvt_18_1_;
      this.field_78253_h[4] = lvt_19_1_;
      this.field_78253_h[5] = lvt_20_1_;
      this.field_78253_h[6] = lvt_21_1_;
      this.field_78253_h[7] = lvt_22_1_;
      this.field_78254_i[0] = new TexturedQuad(new PositionTextureVertex[]{lvt_20_1_, lvt_16_1_, lvt_17_1_, lvt_21_1_}, p_i46301_2_ + p_i46301_9_ + p_i46301_7_, p_i46301_3_ + p_i46301_9_, p_i46301_2_ + p_i46301_9_ + p_i46301_7_ + p_i46301_9_, p_i46301_3_ + p_i46301_9_ + p_i46301_8_, p_i46301_1_.field_78801_a, p_i46301_1_.field_78799_b);
      this.field_78254_i[1] = new TexturedQuad(new PositionTextureVertex[]{lvt_15_2_, lvt_19_1_, lvt_22_1_, lvt_18_1_}, p_i46301_2_, p_i46301_3_ + p_i46301_9_, p_i46301_2_ + p_i46301_9_, p_i46301_3_ + p_i46301_9_ + p_i46301_8_, p_i46301_1_.field_78801_a, p_i46301_1_.field_78799_b);
      this.field_78254_i[2] = new TexturedQuad(new PositionTextureVertex[]{lvt_20_1_, lvt_19_1_, lvt_15_2_, lvt_16_1_}, p_i46301_2_ + p_i46301_9_, p_i46301_3_, p_i46301_2_ + p_i46301_9_ + p_i46301_7_, p_i46301_3_ + p_i46301_9_, p_i46301_1_.field_78801_a, p_i46301_1_.field_78799_b);
      this.field_78254_i[3] = new TexturedQuad(new PositionTextureVertex[]{lvt_17_1_, lvt_18_1_, lvt_22_1_, lvt_21_1_}, p_i46301_2_ + p_i46301_9_ + p_i46301_7_, p_i46301_3_ + p_i46301_9_, p_i46301_2_ + p_i46301_9_ + p_i46301_7_ + p_i46301_7_, p_i46301_3_, p_i46301_1_.field_78801_a, p_i46301_1_.field_78799_b);
      this.field_78254_i[4] = new TexturedQuad(new PositionTextureVertex[]{lvt_16_1_, lvt_15_2_, lvt_18_1_, lvt_17_1_}, p_i46301_2_ + p_i46301_9_, p_i46301_3_ + p_i46301_9_, p_i46301_2_ + p_i46301_9_ + p_i46301_7_, p_i46301_3_ + p_i46301_9_ + p_i46301_8_, p_i46301_1_.field_78801_a, p_i46301_1_.field_78799_b);
      this.field_78254_i[5] = new TexturedQuad(new PositionTextureVertex[]{lvt_19_1_, lvt_20_1_, lvt_21_1_, lvt_22_1_}, p_i46301_2_ + p_i46301_9_ + p_i46301_7_ + p_i46301_9_, p_i46301_3_ + p_i46301_9_, p_i46301_2_ + p_i46301_9_ + p_i46301_7_ + p_i46301_9_ + p_i46301_7_, p_i46301_3_ + p_i46301_9_ + p_i46301_8_, p_i46301_1_.field_78801_a, p_i46301_1_.field_78799_b);
      if(p_i46301_11_) {
         for(int lvt_23_1_ = 0; lvt_23_1_ < this.field_78254_i.length; ++lvt_23_1_) {
            this.field_78254_i[lvt_23_1_].func_78235_a();
         }
      }

   }

   public void func_178780_a(WorldRenderer p_178780_1_, float p_178780_2_) {
      for(int lvt_3_1_ = 0; lvt_3_1_ < this.field_78254_i.length; ++lvt_3_1_) {
         this.field_78254_i[lvt_3_1_].func_178765_a(p_178780_1_, p_178780_2_);
      }

   }

   public ModelBox func_78244_a(String p_78244_1_) {
      this.field_78247_g = p_78244_1_;
      return this;
   }
}
