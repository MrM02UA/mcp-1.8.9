package net.minecraft.client.renderer.block.model;

import java.util.Arrays;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class BreakingFour extends BakedQuad {
   private final TextureAtlasSprite field_178218_d;

   public BreakingFour(BakedQuad p_i46217_1_, TextureAtlasSprite p_i46217_2_) {
      super(Arrays.copyOf(p_i46217_1_.func_178209_a(), p_i46217_1_.func_178209_a().length), p_i46217_1_.field_178213_b, FaceBakery.func_178410_a(p_i46217_1_.func_178209_a()));
      this.field_178218_d = p_i46217_2_;
      this.func_178217_e();
   }

   private void func_178217_e() {
      for(int lvt_1_1_ = 0; lvt_1_1_ < 4; ++lvt_1_1_) {
         this.func_178216_a(lvt_1_1_);
      }

   }

   private void func_178216_a(int p_178216_1_) {
      int lvt_2_1_ = 7 * p_178216_1_;
      float lvt_3_1_ = Float.intBitsToFloat(this.field_178215_a[lvt_2_1_]);
      float lvt_4_1_ = Float.intBitsToFloat(this.field_178215_a[lvt_2_1_ + 1]);
      float lvt_5_1_ = Float.intBitsToFloat(this.field_178215_a[lvt_2_1_ + 2]);
      float lvt_6_1_ = 0.0F;
      float lvt_7_1_ = 0.0F;
      switch(this.field_178214_c) {
      case DOWN:
         lvt_6_1_ = lvt_3_1_ * 16.0F;
         lvt_7_1_ = (1.0F - lvt_5_1_) * 16.0F;
         break;
      case UP:
         lvt_6_1_ = lvt_3_1_ * 16.0F;
         lvt_7_1_ = lvt_5_1_ * 16.0F;
         break;
      case NORTH:
         lvt_6_1_ = (1.0F - lvt_3_1_) * 16.0F;
         lvt_7_1_ = (1.0F - lvt_4_1_) * 16.0F;
         break;
      case SOUTH:
         lvt_6_1_ = lvt_3_1_ * 16.0F;
         lvt_7_1_ = (1.0F - lvt_4_1_) * 16.0F;
         break;
      case WEST:
         lvt_6_1_ = lvt_5_1_ * 16.0F;
         lvt_7_1_ = (1.0F - lvt_4_1_) * 16.0F;
         break;
      case EAST:
         lvt_6_1_ = (1.0F - lvt_5_1_) * 16.0F;
         lvt_7_1_ = (1.0F - lvt_4_1_) * 16.0F;
      }

      this.field_178215_a[lvt_2_1_ + 4] = Float.floatToRawIntBits(this.field_178218_d.func_94214_a((double)lvt_6_1_));
      this.field_178215_a[lvt_2_1_ + 4 + 1] = Float.floatToRawIntBits(this.field_178218_d.func_94207_b((double)lvt_7_1_));
   }
}
