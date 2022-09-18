package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.MathHelper;

public class Stitcher {
   private final int field_147971_a;
   private final Set<Stitcher.Holder> field_94319_a = Sets.newHashSetWithExpectedSize(256);
   private final List<Stitcher.Slot> field_94317_b = Lists.newArrayListWithCapacity(256);
   private int field_94318_c;
   private int field_94315_d;
   private final int field_94316_e;
   private final int field_94313_f;
   private final boolean field_94314_g;
   private final int field_94323_h;

   public Stitcher(int p_i45095_1_, int p_i45095_2_, boolean p_i45095_3_, int p_i45095_4_, int p_i45095_5_) {
      this.field_147971_a = p_i45095_5_;
      this.field_94316_e = p_i45095_1_;
      this.field_94313_f = p_i45095_2_;
      this.field_94314_g = p_i45095_3_;
      this.field_94323_h = p_i45095_4_;
   }

   public int func_110935_a() {
      return this.field_94318_c;
   }

   public int func_110936_b() {
      return this.field_94315_d;
   }

   public void func_110934_a(TextureAtlasSprite p_110934_1_) {
      Stitcher.Holder lvt_2_1_ = new Stitcher.Holder(p_110934_1_, this.field_147971_a);
      if(this.field_94323_h > 0) {
         lvt_2_1_.func_94196_a(this.field_94323_h);
      }

      this.field_94319_a.add(lvt_2_1_);
   }

   public void func_94305_f() {
      Stitcher.Holder[] lvt_1_1_ = (Stitcher.Holder[])this.field_94319_a.toArray(new Stitcher.Holder[this.field_94319_a.size()]);
      Arrays.sort(lvt_1_1_);

      for(Stitcher.Holder lvt_5_1_ : lvt_1_1_) {
         if(!this.func_94310_b(lvt_5_1_)) {
            String lvt_6_1_ = String.format("Unable to fit: %s - size: %dx%d - Maybe try a lowerresolution resourcepack?", new Object[]{lvt_5_1_.func_98150_a().func_94215_i(), Integer.valueOf(lvt_5_1_.func_98150_a().func_94211_a()), Integer.valueOf(lvt_5_1_.func_98150_a().func_94216_b())});
            throw new StitcherException(lvt_5_1_, lvt_6_1_);
         }
      }

      if(this.field_94314_g) {
         this.field_94318_c = MathHelper.func_151236_b(this.field_94318_c);
         this.field_94315_d = MathHelper.func_151236_b(this.field_94315_d);
      }

   }

   public List<TextureAtlasSprite> func_94309_g() {
      List<Stitcher.Slot> lvt_1_1_ = Lists.newArrayList();

      for(Stitcher.Slot lvt_3_1_ : this.field_94317_b) {
         lvt_3_1_.func_94184_a(lvt_1_1_);
      }

      List<TextureAtlasSprite> lvt_2_2_ = Lists.newArrayList();

      for(Stitcher.Slot lvt_4_1_ : lvt_1_1_) {
         Stitcher.Holder lvt_5_1_ = lvt_4_1_.func_94183_a();
         TextureAtlasSprite lvt_6_1_ = lvt_5_1_.func_98150_a();
         lvt_6_1_.func_110971_a(this.field_94318_c, this.field_94315_d, lvt_4_1_.func_94186_b(), lvt_4_1_.func_94185_c(), lvt_5_1_.func_94195_e());
         lvt_2_2_.add(lvt_6_1_);
      }

      return lvt_2_2_;
   }

   private static int func_147969_b(int p_147969_0_, int p_147969_1_) {
      return (p_147969_0_ >> p_147969_1_) + ((p_147969_0_ & (1 << p_147969_1_) - 1) == 0?0:1) << p_147969_1_;
   }

   private boolean func_94310_b(Stitcher.Holder p_94310_1_) {
      for(int lvt_2_1_ = 0; lvt_2_1_ < this.field_94317_b.size(); ++lvt_2_1_) {
         if(((Stitcher.Slot)this.field_94317_b.get(lvt_2_1_)).func_94182_a(p_94310_1_)) {
            return true;
         }

         p_94310_1_.func_94194_d();
         if(((Stitcher.Slot)this.field_94317_b.get(lvt_2_1_)).func_94182_a(p_94310_1_)) {
            return true;
         }

         p_94310_1_.func_94194_d();
      }

      return this.func_94311_c(p_94310_1_);
   }

   private boolean func_94311_c(Stitcher.Holder p_94311_1_) {
      int lvt_2_1_ = Math.min(p_94311_1_.func_94197_a(), p_94311_1_.func_94199_b());
      boolean lvt_3_1_ = this.field_94318_c == 0 && this.field_94315_d == 0;
      boolean lvt_4_1_;
      if(this.field_94314_g) {
         int lvt_5_1_ = MathHelper.func_151236_b(this.field_94318_c);
         int lvt_6_1_ = MathHelper.func_151236_b(this.field_94315_d);
         int lvt_7_1_ = MathHelper.func_151236_b(this.field_94318_c + lvt_2_1_);
         int lvt_8_1_ = MathHelper.func_151236_b(this.field_94315_d + lvt_2_1_);
         boolean lvt_9_1_ = lvt_7_1_ <= this.field_94316_e;
         boolean lvt_10_1_ = lvt_8_1_ <= this.field_94313_f;
         if(!lvt_9_1_ && !lvt_10_1_) {
            return false;
         }

         boolean lvt_11_1_ = lvt_5_1_ != lvt_7_1_;
         boolean lvt_12_1_ = lvt_6_1_ != lvt_8_1_;
         if(lvt_11_1_ ^ lvt_12_1_) {
            lvt_4_1_ = !lvt_11_1_;
         } else {
            lvt_4_1_ = lvt_9_1_ && lvt_5_1_ <= lvt_6_1_;
         }
      } else {
         boolean lvt_5_2_ = this.field_94318_c + lvt_2_1_ <= this.field_94316_e;
         boolean lvt_6_2_ = this.field_94315_d + lvt_2_1_ <= this.field_94313_f;
         if(!lvt_5_2_ && !lvt_6_2_) {
            return false;
         }

         lvt_4_1_ = lvt_5_2_ && (lvt_3_1_ || this.field_94318_c <= this.field_94315_d);
      }

      int lvt_5_3_ = Math.max(p_94311_1_.func_94197_a(), p_94311_1_.func_94199_b());
      if(MathHelper.func_151236_b((lvt_4_1_?this.field_94315_d:this.field_94318_c) + lvt_5_3_) > (lvt_4_1_?this.field_94313_f:this.field_94316_e)) {
         return false;
      } else {
         Stitcher.Slot lvt_6_3_;
         if(lvt_4_1_) {
            if(p_94311_1_.func_94197_a() > p_94311_1_.func_94199_b()) {
               p_94311_1_.func_94194_d();
            }

            if(this.field_94315_d == 0) {
               this.field_94315_d = p_94311_1_.func_94199_b();
            }

            lvt_6_3_ = new Stitcher.Slot(this.field_94318_c, 0, p_94311_1_.func_94197_a(), this.field_94315_d);
            this.field_94318_c += p_94311_1_.func_94197_a();
         } else {
            lvt_6_3_ = new Stitcher.Slot(0, this.field_94315_d, this.field_94318_c, p_94311_1_.func_94199_b());
            this.field_94315_d += p_94311_1_.func_94199_b();
         }

         lvt_6_3_.func_94182_a(p_94311_1_);
         this.field_94317_b.add(lvt_6_3_);
         return true;
      }
   }

   public static class Holder implements Comparable<Stitcher.Holder> {
      private final TextureAtlasSprite field_98151_a;
      private final int field_94204_c;
      private final int field_94201_d;
      private final int field_147968_d;
      private boolean field_94202_e;
      private float field_94205_a = 1.0F;

      public Holder(TextureAtlasSprite p_i45094_1_, int p_i45094_2_) {
         this.field_98151_a = p_i45094_1_;
         this.field_94204_c = p_i45094_1_.func_94211_a();
         this.field_94201_d = p_i45094_1_.func_94216_b();
         this.field_147968_d = p_i45094_2_;
         this.field_94202_e = Stitcher.func_147969_b(this.field_94201_d, p_i45094_2_) > Stitcher.func_147969_b(this.field_94204_c, p_i45094_2_);
      }

      public TextureAtlasSprite func_98150_a() {
         return this.field_98151_a;
      }

      public int func_94197_a() {
         return this.field_94202_e?Stitcher.func_147969_b((int)((float)this.field_94201_d * this.field_94205_a), this.field_147968_d):Stitcher.func_147969_b((int)((float)this.field_94204_c * this.field_94205_a), this.field_147968_d);
      }

      public int func_94199_b() {
         return this.field_94202_e?Stitcher.func_147969_b((int)((float)this.field_94204_c * this.field_94205_a), this.field_147968_d):Stitcher.func_147969_b((int)((float)this.field_94201_d * this.field_94205_a), this.field_147968_d);
      }

      public void func_94194_d() {
         this.field_94202_e = !this.field_94202_e;
      }

      public boolean func_94195_e() {
         return this.field_94202_e;
      }

      public void func_94196_a(int p_94196_1_) {
         if(this.field_94204_c > p_94196_1_ && this.field_94201_d > p_94196_1_) {
            this.field_94205_a = (float)p_94196_1_ / (float)Math.min(this.field_94204_c, this.field_94201_d);
         }
      }

      public String toString() {
         return "Holder{width=" + this.field_94204_c + ", height=" + this.field_94201_d + '}';
      }

      public int compareTo(Stitcher.Holder p_compareTo_1_) {
         int lvt_2_1_;
         if(this.func_94199_b() == p_compareTo_1_.func_94199_b()) {
            if(this.func_94197_a() == p_compareTo_1_.func_94197_a()) {
               if(this.field_98151_a.func_94215_i() == null) {
                  return p_compareTo_1_.field_98151_a.func_94215_i() == null?0:-1;
               }

               return this.field_98151_a.func_94215_i().compareTo(p_compareTo_1_.field_98151_a.func_94215_i());
            }

            lvt_2_1_ = this.func_94197_a() < p_compareTo_1_.func_94197_a()?1:-1;
         } else {
            lvt_2_1_ = this.func_94199_b() < p_compareTo_1_.func_94199_b()?1:-1;
         }

         return lvt_2_1_;
      }

      // $FF: synthetic method
      public int compareTo(Object p_compareTo_1_) {
         return this.compareTo((Stitcher.Holder)p_compareTo_1_);
      }
   }

   public static class Slot {
      private final int field_94192_a;
      private final int field_94190_b;
      private final int field_94191_c;
      private final int field_94188_d;
      private List<Stitcher.Slot> field_94189_e;
      private Stitcher.Holder field_94187_f;

      public Slot(int p_i1277_1_, int p_i1277_2_, int p_i1277_3_, int p_i1277_4_) {
         this.field_94192_a = p_i1277_1_;
         this.field_94190_b = p_i1277_2_;
         this.field_94191_c = p_i1277_3_;
         this.field_94188_d = p_i1277_4_;
      }

      public Stitcher.Holder func_94183_a() {
         return this.field_94187_f;
      }

      public int func_94186_b() {
         return this.field_94192_a;
      }

      public int func_94185_c() {
         return this.field_94190_b;
      }

      public boolean func_94182_a(Stitcher.Holder p_94182_1_) {
         if(this.field_94187_f != null) {
            return false;
         } else {
            int lvt_2_1_ = p_94182_1_.func_94197_a();
            int lvt_3_1_ = p_94182_1_.func_94199_b();
            if(lvt_2_1_ <= this.field_94191_c && lvt_3_1_ <= this.field_94188_d) {
               if(lvt_2_1_ == this.field_94191_c && lvt_3_1_ == this.field_94188_d) {
                  this.field_94187_f = p_94182_1_;
                  return true;
               } else {
                  if(this.field_94189_e == null) {
                     this.field_94189_e = Lists.newArrayListWithCapacity(1);
                     this.field_94189_e.add(new Stitcher.Slot(this.field_94192_a, this.field_94190_b, lvt_2_1_, lvt_3_1_));
                     int lvt_4_1_ = this.field_94191_c - lvt_2_1_;
                     int lvt_5_1_ = this.field_94188_d - lvt_3_1_;
                     if(lvt_5_1_ > 0 && lvt_4_1_ > 0) {
                        int lvt_6_1_ = Math.max(this.field_94188_d, lvt_4_1_);
                        int lvt_7_1_ = Math.max(this.field_94191_c, lvt_5_1_);
                        if(lvt_6_1_ >= lvt_7_1_) {
                           this.field_94189_e.add(new Stitcher.Slot(this.field_94192_a, this.field_94190_b + lvt_3_1_, lvt_2_1_, lvt_5_1_));
                           this.field_94189_e.add(new Stitcher.Slot(this.field_94192_a + lvt_2_1_, this.field_94190_b, lvt_4_1_, this.field_94188_d));
                        } else {
                           this.field_94189_e.add(new Stitcher.Slot(this.field_94192_a + lvt_2_1_, this.field_94190_b, lvt_4_1_, lvt_3_1_));
                           this.field_94189_e.add(new Stitcher.Slot(this.field_94192_a, this.field_94190_b + lvt_3_1_, this.field_94191_c, lvt_5_1_));
                        }
                     } else if(lvt_4_1_ == 0) {
                        this.field_94189_e.add(new Stitcher.Slot(this.field_94192_a, this.field_94190_b + lvt_3_1_, lvt_2_1_, lvt_5_1_));
                     } else if(lvt_5_1_ == 0) {
                        this.field_94189_e.add(new Stitcher.Slot(this.field_94192_a + lvt_2_1_, this.field_94190_b, lvt_4_1_, lvt_3_1_));
                     }
                  }

                  for(Stitcher.Slot lvt_5_2_ : this.field_94189_e) {
                     if(lvt_5_2_.func_94182_a(p_94182_1_)) {
                        return true;
                     }
                  }

                  return false;
               }
            } else {
               return false;
            }
         }
      }

      public void func_94184_a(List<Stitcher.Slot> p_94184_1_) {
         if(this.field_94187_f != null) {
            p_94184_1_.add(this);
         } else if(this.field_94189_e != null) {
            for(Stitcher.Slot lvt_3_1_ : this.field_94189_e) {
               lvt_3_1_.func_94184_a(p_94184_1_);
            }
         }

      }

      public String toString() {
         return "Slot{originX=" + this.field_94192_a + ", originY=" + this.field_94190_b + ", width=" + this.field_94191_c + ", height=" + this.field_94188_d + ", texture=" + this.field_94187_f + ", subSlots=" + this.field_94189_e + '}';
      }
   }
}
