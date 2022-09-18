package net.minecraft.potion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IntegerCache;

public class PotionHelper {
   public static final String field_77924_a = null;
   public static final String field_77922_b = "-0+1-2-3&4-4+13";
   public static final String field_77923_c = "+0-1-2-3&4-4+13";
   public static final String field_77920_d = "-0-1+2-3&4-4+13";
   public static final String field_77921_e = "-0+3-4+13";
   public static final String field_77918_f = "+0-1+2-3&4-4+13";
   public static final String field_77919_g = "+0-1-2+3&4-4+13";
   public static final String field_77931_h = "+0+1-2-3&4-4+13";
   public static final String field_77932_i = "-5+6-7";
   public static final String field_77929_j = "+5-6-7";
   public static final String field_77930_k = "+14&13-13";
   public static final String field_82818_l = "-0+1+2-3+13&4-4";
   public static final String field_151423_m = "+0-1+2+3+13&4-4";
   public static final String field_179538_n = "+0+1-2+3&4-4+13";
   private static final Map<Integer, String> field_179539_o = Maps.newHashMap();
   private static final Map<Integer, String> field_179540_p = Maps.newHashMap();
   private static final Map<Integer, Integer> field_77925_n = Maps.newHashMap();
   private static final String[] field_77926_o = new String[]{"potion.prefix.mundane", "potion.prefix.uninteresting", "potion.prefix.bland", "potion.prefix.clear", "potion.prefix.milky", "potion.prefix.diffuse", "potion.prefix.artless", "potion.prefix.thin", "potion.prefix.awkward", "potion.prefix.flat", "potion.prefix.bulky", "potion.prefix.bungling", "potion.prefix.buttered", "potion.prefix.smooth", "potion.prefix.suave", "potion.prefix.debonair", "potion.prefix.thick", "potion.prefix.elegant", "potion.prefix.fancy", "potion.prefix.charming", "potion.prefix.dashing", "potion.prefix.refined", "potion.prefix.cordial", "potion.prefix.sparkling", "potion.prefix.potent", "potion.prefix.foul", "potion.prefix.odorless", "potion.prefix.rank", "potion.prefix.harsh", "potion.prefix.acrid", "potion.prefix.gross", "potion.prefix.stinky"};

   public static boolean func_77914_a(int p_77914_0_, int p_77914_1_) {
      return (p_77914_0_ & 1 << p_77914_1_) != 0;
   }

   private static int func_77910_c(int p_77910_0_, int p_77910_1_) {
      return func_77914_a(p_77910_0_, p_77910_1_)?1:0;
   }

   private static int func_77916_d(int p_77916_0_, int p_77916_1_) {
      return func_77914_a(p_77916_0_, p_77916_1_)?0:1;
   }

   public static int func_77909_a(int p_77909_0_) {
      return func_77908_a(p_77909_0_, 5, 4, 3, 2, 1);
   }

   public static int func_77911_a(Collection<PotionEffect> p_77911_0_) {
      int lvt_1_1_ = 3694022;
      if(p_77911_0_ != null && !p_77911_0_.isEmpty()) {
         float lvt_2_1_ = 0.0F;
         float lvt_3_1_ = 0.0F;
         float lvt_4_1_ = 0.0F;
         float lvt_5_1_ = 0.0F;

         for(PotionEffect lvt_7_1_ : p_77911_0_) {
            if(lvt_7_1_.func_180154_f()) {
               int lvt_8_1_ = Potion.field_76425_a[lvt_7_1_.func_76456_a()].func_76401_j();

               for(int lvt_9_1_ = 0; lvt_9_1_ <= lvt_7_1_.func_76458_c(); ++lvt_9_1_) {
                  lvt_2_1_ += (float)(lvt_8_1_ >> 16 & 255) / 255.0F;
                  lvt_3_1_ += (float)(lvt_8_1_ >> 8 & 255) / 255.0F;
                  lvt_4_1_ += (float)(lvt_8_1_ >> 0 & 255) / 255.0F;
                  ++lvt_5_1_;
               }
            }
         }

         if(lvt_5_1_ == 0.0F) {
            return 0;
         } else {
            lvt_2_1_ = lvt_2_1_ / lvt_5_1_ * 255.0F;
            lvt_3_1_ = lvt_3_1_ / lvt_5_1_ * 255.0F;
            lvt_4_1_ = lvt_4_1_ / lvt_5_1_ * 255.0F;
            return (int)lvt_2_1_ << 16 | (int)lvt_3_1_ << 8 | (int)lvt_4_1_;
         }
      } else {
         return lvt_1_1_;
      }
   }

   public static boolean func_82817_b(Collection<PotionEffect> p_82817_0_) {
      for(PotionEffect lvt_2_1_ : p_82817_0_) {
         if(!lvt_2_1_.func_82720_e()) {
            return false;
         }
      }

      return true;
   }

   public static int func_77915_a(int p_77915_0_, boolean p_77915_1_) {
      Integer lvt_2_1_ = IntegerCache.func_181756_a(p_77915_0_);
      if(!p_77915_1_) {
         if(field_77925_n.containsKey(lvt_2_1_)) {
            return ((Integer)field_77925_n.get(lvt_2_1_)).intValue();
         } else {
            int lvt_3_1_ = func_77911_a(func_77917_b(lvt_2_1_.intValue(), false));
            field_77925_n.put(lvt_2_1_, Integer.valueOf(lvt_3_1_));
            return lvt_3_1_;
         }
      } else {
         return func_77911_a(func_77917_b(lvt_2_1_.intValue(), true));
      }
   }

   public static String func_77905_c(int p_77905_0_) {
      int lvt_1_1_ = func_77909_a(p_77905_0_);
      return field_77926_o[lvt_1_1_];
   }

   private static int func_77904_a(boolean p_77904_0_, boolean p_77904_1_, boolean p_77904_2_, int p_77904_3_, int p_77904_4_, int p_77904_5_, int p_77904_6_) {
      int lvt_7_1_ = 0;
      if(p_77904_0_) {
         lvt_7_1_ = func_77916_d(p_77904_6_, p_77904_4_);
      } else if(p_77904_3_ != -1) {
         if(p_77904_3_ == 0 && func_77907_h(p_77904_6_) == p_77904_4_) {
            lvt_7_1_ = 1;
         } else if(p_77904_3_ == 1 && func_77907_h(p_77904_6_) > p_77904_4_) {
            lvt_7_1_ = 1;
         } else if(p_77904_3_ == 2 && func_77907_h(p_77904_6_) < p_77904_4_) {
            lvt_7_1_ = 1;
         }
      } else {
         lvt_7_1_ = func_77910_c(p_77904_6_, p_77904_4_);
      }

      if(p_77904_1_) {
         lvt_7_1_ *= p_77904_5_;
      }

      if(p_77904_2_) {
         lvt_7_1_ *= -1;
      }

      return lvt_7_1_;
   }

   private static int func_77907_h(int p_77907_0_) {
      int lvt_1_1_;
      for(lvt_1_1_ = 0; p_77907_0_ > 0; ++lvt_1_1_) {
         p_77907_0_ &= p_77907_0_ - 1;
      }

      return lvt_1_1_;
   }

   private static int func_77912_a(String p_77912_0_, int p_77912_1_, int p_77912_2_, int p_77912_3_) {
      if(p_77912_1_ < p_77912_0_.length() && p_77912_2_ >= 0 && p_77912_1_ < p_77912_2_) {
         int lvt_4_1_ = p_77912_0_.indexOf(124, p_77912_1_);
         if(lvt_4_1_ >= 0 && lvt_4_1_ < p_77912_2_) {
            int lvt_5_1_ = func_77912_a(p_77912_0_, p_77912_1_, lvt_4_1_ - 1, p_77912_3_);
            if(lvt_5_1_ > 0) {
               return lvt_5_1_;
            } else {
               int lvt_6_1_ = func_77912_a(p_77912_0_, lvt_4_1_ + 1, p_77912_2_, p_77912_3_);
               return lvt_6_1_ > 0?lvt_6_1_:0;
            }
         } else {
            int lvt_5_2_ = p_77912_0_.indexOf(38, p_77912_1_);
            if(lvt_5_2_ >= 0 && lvt_5_2_ < p_77912_2_) {
               int lvt_6_2_ = func_77912_a(p_77912_0_, p_77912_1_, lvt_5_2_ - 1, p_77912_3_);
               if(lvt_6_2_ <= 0) {
                  return 0;
               } else {
                  int lvt_7_1_ = func_77912_a(p_77912_0_, lvt_5_2_ + 1, p_77912_2_, p_77912_3_);
                  return lvt_7_1_ <= 0?0:(lvt_6_2_ > lvt_7_1_?lvt_6_2_:lvt_7_1_);
               }
            } else {
               boolean lvt_6_3_ = false;
               boolean lvt_7_2_ = false;
               boolean lvt_8_1_ = false;
               boolean lvt_9_1_ = false;
               boolean lvt_10_1_ = false;
               int lvt_11_1_ = -1;
               int lvt_12_1_ = 0;
               int lvt_13_1_ = 0;
               int lvt_14_1_ = 0;

               for(int lvt_15_1_ = p_77912_1_; lvt_15_1_ < p_77912_2_; ++lvt_15_1_) {
                  char lvt_16_1_ = p_77912_0_.charAt(lvt_15_1_);
                  if(lvt_16_1_ >= 48 && lvt_16_1_ <= 57) {
                     if(lvt_6_3_) {
                        lvt_13_1_ = lvt_16_1_ - 48;
                        lvt_7_2_ = true;
                     } else {
                        lvt_12_1_ = lvt_12_1_ * 10;
                        lvt_12_1_ = lvt_12_1_ + (lvt_16_1_ - 48);
                        lvt_8_1_ = true;
                     }
                  } else if(lvt_16_1_ == 42) {
                     lvt_6_3_ = true;
                  } else if(lvt_16_1_ == 33) {
                     if(lvt_8_1_) {
                        lvt_14_1_ += func_77904_a(lvt_9_1_, lvt_7_2_, lvt_10_1_, lvt_11_1_, lvt_12_1_, lvt_13_1_, p_77912_3_);
                        lvt_9_1_ = false;
                        lvt_10_1_ = false;
                        lvt_6_3_ = false;
                        lvt_7_2_ = false;
                        lvt_8_1_ = false;
                        lvt_13_1_ = 0;
                        lvt_12_1_ = 0;
                        lvt_11_1_ = -1;
                     }

                     lvt_9_1_ = true;
                  } else if(lvt_16_1_ == 45) {
                     if(lvt_8_1_) {
                        lvt_14_1_ += func_77904_a(lvt_9_1_, lvt_7_2_, lvt_10_1_, lvt_11_1_, lvt_12_1_, lvt_13_1_, p_77912_3_);
                        lvt_9_1_ = false;
                        lvt_10_1_ = false;
                        lvt_6_3_ = false;
                        lvt_7_2_ = false;
                        lvt_8_1_ = false;
                        lvt_13_1_ = 0;
                        lvt_12_1_ = 0;
                        lvt_11_1_ = -1;
                     }

                     lvt_10_1_ = true;
                  } else if(lvt_16_1_ != 61 && lvt_16_1_ != 60 && lvt_16_1_ != 62) {
                     if(lvt_16_1_ == 43 && lvt_8_1_) {
                        lvt_14_1_ += func_77904_a(lvt_9_1_, lvt_7_2_, lvt_10_1_, lvt_11_1_, lvt_12_1_, lvt_13_1_, p_77912_3_);
                        lvt_9_1_ = false;
                        lvt_10_1_ = false;
                        lvt_6_3_ = false;
                        lvt_7_2_ = false;
                        lvt_8_1_ = false;
                        lvt_13_1_ = 0;
                        lvt_12_1_ = 0;
                        lvt_11_1_ = -1;
                     }
                  } else {
                     if(lvt_8_1_) {
                        lvt_14_1_ += func_77904_a(lvt_9_1_, lvt_7_2_, lvt_10_1_, lvt_11_1_, lvt_12_1_, lvt_13_1_, p_77912_3_);
                        lvt_9_1_ = false;
                        lvt_10_1_ = false;
                        lvt_6_3_ = false;
                        lvt_7_2_ = false;
                        lvt_8_1_ = false;
                        lvt_13_1_ = 0;
                        lvt_12_1_ = 0;
                        lvt_11_1_ = -1;
                     }

                     if(lvt_16_1_ == 61) {
                        lvt_11_1_ = 0;
                     } else if(lvt_16_1_ == 60) {
                        lvt_11_1_ = 2;
                     } else if(lvt_16_1_ == 62) {
                        lvt_11_1_ = 1;
                     }
                  }
               }

               if(lvt_8_1_) {
                  lvt_14_1_ += func_77904_a(lvt_9_1_, lvt_7_2_, lvt_10_1_, lvt_11_1_, lvt_12_1_, lvt_13_1_, p_77912_3_);
               }

               return lvt_14_1_;
            }
         }
      } else {
         return 0;
      }
   }

   public static List<PotionEffect> func_77917_b(int p_77917_0_, boolean p_77917_1_) {
      List<PotionEffect> lvt_2_1_ = null;

      for(Potion lvt_6_1_ : Potion.field_76425_a) {
         if(lvt_6_1_ != null && (!lvt_6_1_.func_76395_i() || p_77917_1_)) {
            String lvt_7_1_ = (String)field_179539_o.get(Integer.valueOf(lvt_6_1_.func_76396_c()));
            if(lvt_7_1_ != null) {
               int lvt_8_1_ = func_77912_a(lvt_7_1_, 0, lvt_7_1_.length(), p_77917_0_);
               if(lvt_8_1_ > 0) {
                  int lvt_9_1_ = 0;
                  String lvt_10_1_ = (String)field_179540_p.get(Integer.valueOf(lvt_6_1_.func_76396_c()));
                  if(lvt_10_1_ != null) {
                     lvt_9_1_ = func_77912_a(lvt_10_1_, 0, lvt_10_1_.length(), p_77917_0_);
                     if(lvt_9_1_ < 0) {
                        lvt_9_1_ = 0;
                     }
                  }

                  if(lvt_6_1_.func_76403_b()) {
                     lvt_8_1_ = 1;
                  } else {
                     lvt_8_1_ = 1200 * (lvt_8_1_ * 3 + (lvt_8_1_ - 1) * 2);
                     lvt_8_1_ = lvt_8_1_ >> lvt_9_1_;
                     lvt_8_1_ = (int)Math.round((double)lvt_8_1_ * lvt_6_1_.func_76388_g());
                     if((p_77917_0_ & 16384) != 0) {
                        lvt_8_1_ = (int)Math.round((double)lvt_8_1_ * 0.75D + 0.5D);
                     }
                  }

                  if(lvt_2_1_ == null) {
                     lvt_2_1_ = Lists.newArrayList();
                  }

                  PotionEffect lvt_11_1_ = new PotionEffect(lvt_6_1_.func_76396_c(), lvt_8_1_, lvt_9_1_);
                  if((p_77917_0_ & 16384) != 0) {
                     lvt_11_1_.func_82721_a(true);
                  }

                  lvt_2_1_.add(lvt_11_1_);
               }
            }
         }
      }

      return lvt_2_1_;
   }

   private static int func_77906_a(int p_77906_0_, int p_77906_1_, boolean p_77906_2_, boolean p_77906_3_, boolean p_77906_4_) {
      if(p_77906_4_) {
         if(!func_77914_a(p_77906_0_, p_77906_1_)) {
            return 0;
         }
      } else if(p_77906_2_) {
         p_77906_0_ &= ~(1 << p_77906_1_);
      } else if(p_77906_3_) {
         if((p_77906_0_ & 1 << p_77906_1_) == 0) {
            p_77906_0_ |= 1 << p_77906_1_;
         } else {
            p_77906_0_ &= ~(1 << p_77906_1_);
         }
      } else {
         p_77906_0_ |= 1 << p_77906_1_;
      }

      return p_77906_0_;
   }

   public static int func_77913_a(int p_77913_0_, String p_77913_1_) {
      int lvt_2_1_ = 0;
      int lvt_3_1_ = p_77913_1_.length();
      boolean lvt_4_1_ = false;
      boolean lvt_5_1_ = false;
      boolean lvt_6_1_ = false;
      boolean lvt_7_1_ = false;
      int lvt_8_1_ = 0;

      for(int lvt_9_1_ = lvt_2_1_; lvt_9_1_ < lvt_3_1_; ++lvt_9_1_) {
         char lvt_10_1_ = p_77913_1_.charAt(lvt_9_1_);
         if(lvt_10_1_ >= 48 && lvt_10_1_ <= 57) {
            lvt_8_1_ = lvt_8_1_ * 10;
            lvt_8_1_ = lvt_8_1_ + (lvt_10_1_ - 48);
            lvt_4_1_ = true;
         } else if(lvt_10_1_ == 33) {
            if(lvt_4_1_) {
               p_77913_0_ = func_77906_a(p_77913_0_, lvt_8_1_, lvt_6_1_, lvt_5_1_, lvt_7_1_);
               lvt_7_1_ = false;
               lvt_5_1_ = false;
               lvt_6_1_ = false;
               lvt_4_1_ = false;
               lvt_8_1_ = 0;
            }

            lvt_5_1_ = true;
         } else if(lvt_10_1_ == 45) {
            if(lvt_4_1_) {
               p_77913_0_ = func_77906_a(p_77913_0_, lvt_8_1_, lvt_6_1_, lvt_5_1_, lvt_7_1_);
               lvt_7_1_ = false;
               lvt_5_1_ = false;
               lvt_6_1_ = false;
               lvt_4_1_ = false;
               lvt_8_1_ = 0;
            }

            lvt_6_1_ = true;
         } else if(lvt_10_1_ == 43) {
            if(lvt_4_1_) {
               p_77913_0_ = func_77906_a(p_77913_0_, lvt_8_1_, lvt_6_1_, lvt_5_1_, lvt_7_1_);
               lvt_7_1_ = false;
               lvt_5_1_ = false;
               lvt_6_1_ = false;
               lvt_4_1_ = false;
               lvt_8_1_ = 0;
            }
         } else if(lvt_10_1_ == 38) {
            if(lvt_4_1_) {
               p_77913_0_ = func_77906_a(p_77913_0_, lvt_8_1_, lvt_6_1_, lvt_5_1_, lvt_7_1_);
               lvt_7_1_ = false;
               lvt_5_1_ = false;
               lvt_6_1_ = false;
               lvt_4_1_ = false;
               lvt_8_1_ = 0;
            }

            lvt_7_1_ = true;
         }
      }

      if(lvt_4_1_) {
         p_77913_0_ = func_77906_a(p_77913_0_, lvt_8_1_, lvt_6_1_, lvt_5_1_, lvt_7_1_);
      }

      return p_77913_0_ & 32767;
   }

   public static int func_77908_a(int p_77908_0_, int p_77908_1_, int p_77908_2_, int p_77908_3_, int p_77908_4_, int p_77908_5_) {
      return (func_77914_a(p_77908_0_, p_77908_1_)?16:0) | (func_77914_a(p_77908_0_, p_77908_2_)?8:0) | (func_77914_a(p_77908_0_, p_77908_3_)?4:0) | (func_77914_a(p_77908_0_, p_77908_4_)?2:0) | (func_77914_a(p_77908_0_, p_77908_5_)?1:0);
   }

   static {
      field_179539_o.put(Integer.valueOf(Potion.field_76428_l.func_76396_c()), "0 & !1 & !2 & !3 & 0+6");
      field_179539_o.put(Integer.valueOf(Potion.field_76424_c.func_76396_c()), "!0 & 1 & !2 & !3 & 1+6");
      field_179539_o.put(Integer.valueOf(Potion.field_76426_n.func_76396_c()), "0 & 1 & !2 & !3 & 0+6");
      field_179539_o.put(Integer.valueOf(Potion.field_76432_h.func_76396_c()), "0 & !1 & 2 & !3");
      field_179539_o.put(Integer.valueOf(Potion.field_76436_u.func_76396_c()), "!0 & !1 & 2 & !3 & 2+6");
      field_179539_o.put(Integer.valueOf(Potion.field_76437_t.func_76396_c()), "!0 & !1 & !2 & 3 & 3+6");
      field_179539_o.put(Integer.valueOf(Potion.field_76433_i.func_76396_c()), "!0 & !1 & 2 & 3");
      field_179539_o.put(Integer.valueOf(Potion.field_76421_d.func_76396_c()), "!0 & 1 & !2 & 3 & 3+6");
      field_179539_o.put(Integer.valueOf(Potion.field_76420_g.func_76396_c()), "0 & !1 & !2 & 3 & 3+6");
      field_179539_o.put(Integer.valueOf(Potion.field_76439_r.func_76396_c()), "!0 & 1 & 2 & !3 & 2+6");
      field_179539_o.put(Integer.valueOf(Potion.field_76441_p.func_76396_c()), "!0 & 1 & 2 & 3 & 2+6");
      field_179539_o.put(Integer.valueOf(Potion.field_76427_o.func_76396_c()), "0 & !1 & 2 & 3 & 2+6");
      field_179539_o.put(Integer.valueOf(Potion.field_76430_j.func_76396_c()), "0 & 1 & !2 & 3 & 3+6");
      field_179540_p.put(Integer.valueOf(Potion.field_76424_c.func_76396_c()), "5");
      field_179540_p.put(Integer.valueOf(Potion.field_76422_e.func_76396_c()), "5");
      field_179540_p.put(Integer.valueOf(Potion.field_76420_g.func_76396_c()), "5");
      field_179540_p.put(Integer.valueOf(Potion.field_76428_l.func_76396_c()), "5");
      field_179540_p.put(Integer.valueOf(Potion.field_76433_i.func_76396_c()), "5");
      field_179540_p.put(Integer.valueOf(Potion.field_76432_h.func_76396_c()), "5");
      field_179540_p.put(Integer.valueOf(Potion.field_76429_m.func_76396_c()), "5");
      field_179540_p.put(Integer.valueOf(Potion.field_76436_u.func_76396_c()), "5");
      field_179540_p.put(Integer.valueOf(Potion.field_76430_j.func_76396_c()), "5");
   }
}
