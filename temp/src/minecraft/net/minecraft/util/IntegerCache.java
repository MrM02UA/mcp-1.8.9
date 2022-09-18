package net.minecraft.util;

public class IntegerCache {
   private static final Integer[] field_181757_a = new Integer['\uffff'];

   public static Integer func_181756_a(int p_181756_0_) {
      return p_181756_0_ > 0 && p_181756_0_ < field_181757_a.length?field_181757_a[p_181756_0_]:Integer.valueOf(p_181756_0_);
   }

   static {
      int lvt_0_1_ = 0;

      for(int lvt_1_1_ = field_181757_a.length; lvt_0_1_ < lvt_1_1_; ++lvt_0_1_) {
         field_181757_a[lvt_0_1_] = Integer.valueOf(lvt_0_1_);
      }

   }
}
