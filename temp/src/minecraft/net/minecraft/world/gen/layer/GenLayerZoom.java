package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerZoom extends GenLayer {
   public GenLayerZoom(long p_i2134_1_, GenLayer p_i2134_3_) {
      super(p_i2134_1_);
      super.field_75909_a = p_i2134_3_;
   }

   public int[] func_75904_a(int p_75904_1_, int p_75904_2_, int p_75904_3_, int p_75904_4_) {
      int lvt_5_1_ = p_75904_1_ >> 1;
      int lvt_6_1_ = p_75904_2_ >> 1;
      int lvt_7_1_ = (p_75904_3_ >> 1) + 2;
      int lvt_8_1_ = (p_75904_4_ >> 1) + 2;
      int[] lvt_9_1_ = this.field_75909_a.func_75904_a(lvt_5_1_, lvt_6_1_, lvt_7_1_, lvt_8_1_);
      int lvt_10_1_ = lvt_7_1_ - 1 << 1;
      int lvt_11_1_ = lvt_8_1_ - 1 << 1;
      int[] lvt_12_1_ = IntCache.func_76445_a(lvt_10_1_ * lvt_11_1_);

      for(int lvt_13_1_ = 0; lvt_13_1_ < lvt_8_1_ - 1; ++lvt_13_1_) {
         int lvt_14_1_ = (lvt_13_1_ << 1) * lvt_10_1_;
         int lvt_15_1_ = 0;
         int lvt_16_1_ = lvt_9_1_[lvt_15_1_ + 0 + (lvt_13_1_ + 0) * lvt_7_1_];

         for(int lvt_17_1_ = lvt_9_1_[lvt_15_1_ + 0 + (lvt_13_1_ + 1) * lvt_7_1_]; lvt_15_1_ < lvt_7_1_ - 1; ++lvt_15_1_) {
            this.func_75903_a((long)(lvt_15_1_ + lvt_5_1_ << 1), (long)(lvt_13_1_ + lvt_6_1_ << 1));
            int lvt_18_1_ = lvt_9_1_[lvt_15_1_ + 1 + (lvt_13_1_ + 0) * lvt_7_1_];
            int lvt_19_1_ = lvt_9_1_[lvt_15_1_ + 1 + (lvt_13_1_ + 1) * lvt_7_1_];
            lvt_12_1_[lvt_14_1_] = lvt_16_1_;
            lvt_12_1_[lvt_14_1_++ + lvt_10_1_] = this.func_151619_a(new int[]{lvt_16_1_, lvt_17_1_});
            lvt_12_1_[lvt_14_1_] = this.func_151619_a(new int[]{lvt_16_1_, lvt_18_1_});
            lvt_12_1_[lvt_14_1_++ + lvt_10_1_] = this.func_151617_b(lvt_16_1_, lvt_18_1_, lvt_17_1_, lvt_19_1_);
            lvt_16_1_ = lvt_18_1_;
            lvt_17_1_ = lvt_19_1_;
         }
      }

      int[] lvt_13_2_ = IntCache.func_76445_a(p_75904_3_ * p_75904_4_);

      for(int lvt_14_2_ = 0; lvt_14_2_ < p_75904_4_; ++lvt_14_2_) {
         System.arraycopy(lvt_12_1_, (lvt_14_2_ + (p_75904_2_ & 1)) * lvt_10_1_ + (p_75904_1_ & 1), lvt_13_2_, lvt_14_2_ * p_75904_3_, p_75904_3_);
      }

      return lvt_13_2_;
   }

   public static GenLayer func_75915_a(long p_75915_0_, GenLayer p_75915_2_, int p_75915_3_) {
      GenLayer lvt_4_1_ = p_75915_2_;

      for(int lvt_5_1_ = 0; lvt_5_1_ < p_75915_3_; ++lvt_5_1_) {
         lvt_4_1_ = new GenLayerZoom(p_75915_0_ + (long)lvt_5_1_, lvt_4_1_);
      }

      return lvt_4_1_;
   }
}
