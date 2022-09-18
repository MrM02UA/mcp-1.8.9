package net.minecraft.util;

public class ChatAllowedCharacters {
   public static final char[] field_71567_b = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};

   public static boolean func_71566_a(char p_71566_0_) {
      return p_71566_0_ != 167 && p_71566_0_ >= 32 && p_71566_0_ != 127;
   }

   public static String func_71565_a(String p_71565_0_) {
      StringBuilder lvt_1_1_ = new StringBuilder();

      for(char lvt_5_1_ : p_71565_0_.toCharArray()) {
         if(func_71566_a(lvt_5_1_)) {
            lvt_1_1_.append(lvt_5_1_);
         }
      }

      return lvt_1_1_.toString();
   }
}
