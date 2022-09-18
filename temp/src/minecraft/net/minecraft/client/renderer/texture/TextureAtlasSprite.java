package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.client.renderer.texture.TextureClock;
import net.minecraft.client.renderer.texture.TextureCompass;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;

public class TextureAtlasSprite {
   private final String field_110984_i;
   protected List<int[][]> field_110976_a = Lists.newArrayList();
   protected int[][] field_176605_b;
   private AnimationMetadataSection field_110982_k;
   protected boolean field_130222_e;
   protected int field_110975_c;
   protected int field_110974_d;
   protected int field_130223_c;
   protected int field_130224_d;
   private float field_110979_l;
   private float field_110980_m;
   private float field_110977_n;
   private float field_110978_o;
   protected int field_110973_g;
   protected int field_110983_h;
   private static String field_176607_p = "builtin/clock";
   private static String field_176606_q = "builtin/compass";

   protected TextureAtlasSprite(String p_i1282_1_) {
      this.field_110984_i = p_i1282_1_;
   }

   protected static TextureAtlasSprite func_176604_a(ResourceLocation p_176604_0_) {
      String lvt_1_1_ = p_176604_0_.toString();
      return (TextureAtlasSprite)(field_176607_p.equals(lvt_1_1_)?new TextureClock(lvt_1_1_):(field_176606_q.equals(lvt_1_1_)?new TextureCompass(lvt_1_1_):new TextureAtlasSprite(lvt_1_1_)));
   }

   public static void func_176602_a(String p_176602_0_) {
      field_176607_p = p_176602_0_;
   }

   public static void func_176603_b(String p_176603_0_) {
      field_176606_q = p_176603_0_;
   }

   public void func_110971_a(int p_110971_1_, int p_110971_2_, int p_110971_3_, int p_110971_4_, boolean p_110971_5_) {
      this.field_110975_c = p_110971_3_;
      this.field_110974_d = p_110971_4_;
      this.field_130222_e = p_110971_5_;
      float lvt_6_1_ = (float)(0.009999999776482582D / (double)p_110971_1_);
      float lvt_7_1_ = (float)(0.009999999776482582D / (double)p_110971_2_);
      this.field_110979_l = (float)p_110971_3_ / (float)((double)p_110971_1_) + lvt_6_1_;
      this.field_110980_m = (float)(p_110971_3_ + this.field_130223_c) / (float)((double)p_110971_1_) - lvt_6_1_;
      this.field_110977_n = (float)p_110971_4_ / (float)p_110971_2_ + lvt_7_1_;
      this.field_110978_o = (float)(p_110971_4_ + this.field_130224_d) / (float)p_110971_2_ - lvt_7_1_;
   }

   public void func_94217_a(TextureAtlasSprite p_94217_1_) {
      this.field_110975_c = p_94217_1_.field_110975_c;
      this.field_110974_d = p_94217_1_.field_110974_d;
      this.field_130223_c = p_94217_1_.field_130223_c;
      this.field_130224_d = p_94217_1_.field_130224_d;
      this.field_130222_e = p_94217_1_.field_130222_e;
      this.field_110979_l = p_94217_1_.field_110979_l;
      this.field_110980_m = p_94217_1_.field_110980_m;
      this.field_110977_n = p_94217_1_.field_110977_n;
      this.field_110978_o = p_94217_1_.field_110978_o;
   }

   public int func_130010_a() {
      return this.field_110975_c;
   }

   public int func_110967_i() {
      return this.field_110974_d;
   }

   public int func_94211_a() {
      return this.field_130223_c;
   }

   public int func_94216_b() {
      return this.field_130224_d;
   }

   public float func_94209_e() {
      return this.field_110979_l;
   }

   public float func_94212_f() {
      return this.field_110980_m;
   }

   public float func_94214_a(double p_94214_1_) {
      float lvt_3_1_ = this.field_110980_m - this.field_110979_l;
      return this.field_110979_l + lvt_3_1_ * (float)p_94214_1_ / 16.0F;
   }

   public float func_94206_g() {
      return this.field_110977_n;
   }

   public float func_94210_h() {
      return this.field_110978_o;
   }

   public float func_94207_b(double p_94207_1_) {
      float lvt_3_1_ = this.field_110978_o - this.field_110977_n;
      return this.field_110977_n + lvt_3_1_ * ((float)p_94207_1_ / 16.0F);
   }

   public String func_94215_i() {
      return this.field_110984_i;
   }

   public void func_94219_l() {
      ++this.field_110983_h;
      if(this.field_110983_h >= this.field_110982_k.func_110472_a(this.field_110973_g)) {
         int lvt_1_1_ = this.field_110982_k.func_110468_c(this.field_110973_g);
         int lvt_2_1_ = this.field_110982_k.func_110473_c() == 0?this.field_110976_a.size():this.field_110982_k.func_110473_c();
         this.field_110973_g = (this.field_110973_g + 1) % lvt_2_1_;
         this.field_110983_h = 0;
         int lvt_3_1_ = this.field_110982_k.func_110468_c(this.field_110973_g);
         if(lvt_1_1_ != lvt_3_1_ && lvt_3_1_ >= 0 && lvt_3_1_ < this.field_110976_a.size()) {
            TextureUtil.func_147955_a((int[][])this.field_110976_a.get(lvt_3_1_), this.field_130223_c, this.field_130224_d, this.field_110975_c, this.field_110974_d, false, false);
         }
      } else if(this.field_110982_k.func_177219_e()) {
         this.func_180599_n();
      }

   }

   private void func_180599_n() {
      double lvt_1_1_ = 1.0D - (double)this.field_110983_h / (double)this.field_110982_k.func_110472_a(this.field_110973_g);
      int lvt_3_1_ = this.field_110982_k.func_110468_c(this.field_110973_g);
      int lvt_4_1_ = this.field_110982_k.func_110473_c() == 0?this.field_110976_a.size():this.field_110982_k.func_110473_c();
      int lvt_5_1_ = this.field_110982_k.func_110468_c((this.field_110973_g + 1) % lvt_4_1_);
      if(lvt_3_1_ != lvt_5_1_ && lvt_5_1_ >= 0 && lvt_5_1_ < this.field_110976_a.size()) {
         int[][] lvt_6_1_ = (int[][])this.field_110976_a.get(lvt_3_1_);
         int[][] lvt_7_1_ = (int[][])this.field_110976_a.get(lvt_5_1_);
         if(this.field_176605_b == null || this.field_176605_b.length != lvt_6_1_.length) {
            this.field_176605_b = new int[lvt_6_1_.length][];
         }

         for(int lvt_8_1_ = 0; lvt_8_1_ < lvt_6_1_.length; ++lvt_8_1_) {
            if(this.field_176605_b[lvt_8_1_] == null) {
               this.field_176605_b[lvt_8_1_] = new int[lvt_6_1_[lvt_8_1_].length];
            }

            if(lvt_8_1_ < lvt_7_1_.length && lvt_7_1_[lvt_8_1_].length == lvt_6_1_[lvt_8_1_].length) {
               for(int lvt_9_1_ = 0; lvt_9_1_ < lvt_6_1_[lvt_8_1_].length; ++lvt_9_1_) {
                  int lvt_10_1_ = lvt_6_1_[lvt_8_1_][lvt_9_1_];
                  int lvt_11_1_ = lvt_7_1_[lvt_8_1_][lvt_9_1_];
                  int lvt_12_1_ = (int)((double)((lvt_10_1_ & 16711680) >> 16) * lvt_1_1_ + (double)((lvt_11_1_ & 16711680) >> 16) * (1.0D - lvt_1_1_));
                  int lvt_13_1_ = (int)((double)((lvt_10_1_ & '\uff00') >> 8) * lvt_1_1_ + (double)((lvt_11_1_ & '\uff00') >> 8) * (1.0D - lvt_1_1_));
                  int lvt_14_1_ = (int)((double)(lvt_10_1_ & 255) * lvt_1_1_ + (double)(lvt_11_1_ & 255) * (1.0D - lvt_1_1_));
                  this.field_176605_b[lvt_8_1_][lvt_9_1_] = lvt_10_1_ & -16777216 | lvt_12_1_ << 16 | lvt_13_1_ << 8 | lvt_14_1_;
               }
            }
         }

         TextureUtil.func_147955_a(this.field_176605_b, this.field_130223_c, this.field_130224_d, this.field_110975_c, this.field_110974_d, false, false);
      }

   }

   public int[][] func_147965_a(int p_147965_1_) {
      return (int[][])this.field_110976_a.get(p_147965_1_);
   }

   public int func_110970_k() {
      return this.field_110976_a.size();
   }

   public void func_110966_b(int p_110966_1_) {
      this.field_130223_c = p_110966_1_;
   }

   public void func_110969_c(int p_110969_1_) {
      this.field_130224_d = p_110969_1_;
   }

   public void func_180598_a(BufferedImage[] p_180598_1_, AnimationMetadataSection p_180598_2_) throws IOException {
      this.func_130102_n();
      int lvt_3_1_ = p_180598_1_[0].getWidth();
      int lvt_4_1_ = p_180598_1_[0].getHeight();
      this.field_130223_c = lvt_3_1_;
      this.field_130224_d = lvt_4_1_;
      int[][] lvt_5_1_ = new int[p_180598_1_.length][];

      for(int lvt_6_1_ = 0; lvt_6_1_ < p_180598_1_.length; ++lvt_6_1_) {
         BufferedImage lvt_7_1_ = p_180598_1_[lvt_6_1_];
         if(lvt_7_1_ != null) {
            if(lvt_6_1_ > 0 && (lvt_7_1_.getWidth() != lvt_3_1_ >> lvt_6_1_ || lvt_7_1_.getHeight() != lvt_4_1_ >> lvt_6_1_)) {
               throw new RuntimeException(String.format("Unable to load miplevel: %d, image is size: %dx%d, expected %dx%d", new Object[]{Integer.valueOf(lvt_6_1_), Integer.valueOf(lvt_7_1_.getWidth()), Integer.valueOf(lvt_7_1_.getHeight()), Integer.valueOf(lvt_3_1_ >> lvt_6_1_), Integer.valueOf(lvt_4_1_ >> lvt_6_1_)}));
            }

            lvt_5_1_[lvt_6_1_] = new int[lvt_7_1_.getWidth() * lvt_7_1_.getHeight()];
            lvt_7_1_.getRGB(0, 0, lvt_7_1_.getWidth(), lvt_7_1_.getHeight(), lvt_5_1_[lvt_6_1_], 0, lvt_7_1_.getWidth());
         }
      }

      if(p_180598_2_ == null) {
         if(lvt_4_1_ != lvt_3_1_) {
            throw new RuntimeException("broken aspect ratio and not an animation");
         }

         this.field_110976_a.add(lvt_5_1_);
      } else {
         int lvt_6_2_ = lvt_4_1_ / lvt_3_1_;
         int lvt_7_2_ = lvt_3_1_;
         int lvt_8_1_ = lvt_3_1_;
         this.field_130224_d = this.field_130223_c;
         if(p_180598_2_.func_110473_c() > 0) {
            Iterator lvt_9_1_ = p_180598_2_.func_130073_e().iterator();

            while(lvt_9_1_.hasNext()) {
               int lvt_10_1_ = ((Integer)lvt_9_1_.next()).intValue();
               if(lvt_10_1_ >= lvt_6_2_) {
                  throw new RuntimeException("invalid frameindex " + lvt_10_1_);
               }

               this.func_130099_d(lvt_10_1_);
               this.field_110976_a.set(lvt_10_1_, func_147962_a(lvt_5_1_, lvt_7_2_, lvt_8_1_, lvt_10_1_));
            }

            this.field_110982_k = p_180598_2_;
         } else {
            List<AnimationFrame> lvt_9_2_ = Lists.newArrayList();

            for(int lvt_10_2_ = 0; lvt_10_2_ < lvt_6_2_; ++lvt_10_2_) {
               this.field_110976_a.add(func_147962_a(lvt_5_1_, lvt_7_2_, lvt_8_1_, lvt_10_2_));
               lvt_9_2_.add(new AnimationFrame(lvt_10_2_, -1));
            }

            this.field_110982_k = new AnimationMetadataSection(lvt_9_2_, this.field_130223_c, this.field_130224_d, p_180598_2_.func_110469_d(), p_180598_2_.func_177219_e());
         }
      }

   }

   public void func_147963_d(int p_147963_1_) {
      List<int[][]> lvt_2_1_ = Lists.newArrayList();

      for(int lvt_3_1_ = 0; lvt_3_1_ < this.field_110976_a.size(); ++lvt_3_1_) {
         final int[][] lvt_4_1_ = (int[][])this.field_110976_a.get(lvt_3_1_);
         if(lvt_4_1_ != null) {
            try {
               lvt_2_1_.add(TextureUtil.func_147949_a(p_147963_1_, this.field_130223_c, lvt_4_1_));
            } catch (Throwable var8) {
               CrashReport lvt_6_1_ = CrashReport.func_85055_a(var8, "Generating mipmaps for frame");
               CrashReportCategory lvt_7_1_ = lvt_6_1_.func_85058_a("Frame being iterated");
               lvt_7_1_.func_71507_a("Frame index", Integer.valueOf(lvt_3_1_));
               lvt_7_1_.func_71500_a("Frame sizes", new Callable<String>() {
                  public String call() throws Exception {
                     StringBuilder lvt_1_1_ = new StringBuilder();

                     for(int[] lvt_5_1_ : lvt_4_1_) {
                        if(lvt_1_1_.length() > 0) {
                           lvt_1_1_.append(", ");
                        }

                        lvt_1_1_.append(lvt_5_1_ == null?"null":Integer.valueOf(lvt_5_1_.length));
                     }

                     return lvt_1_1_.toString();
                  }

                  // $FF: synthetic method
                  public Object call() throws Exception {
                     return this.call();
                  }
               });
               throw new ReportedException(lvt_6_1_);
            }
         }
      }

      this.func_110968_a(lvt_2_1_);
   }

   private void func_130099_d(int p_130099_1_) {
      if(this.field_110976_a.size() <= p_130099_1_) {
         for(int lvt_2_1_ = this.field_110976_a.size(); lvt_2_1_ <= p_130099_1_; ++lvt_2_1_) {
            this.field_110976_a.add((Object)null);
         }

      }
   }

   private static int[][] func_147962_a(int[][] p_147962_0_, int p_147962_1_, int p_147962_2_, int p_147962_3_) {
      int[][] lvt_4_1_ = new int[p_147962_0_.length][];

      for(int lvt_5_1_ = 0; lvt_5_1_ < p_147962_0_.length; ++lvt_5_1_) {
         int[] lvt_6_1_ = p_147962_0_[lvt_5_1_];
         if(lvt_6_1_ != null) {
            lvt_4_1_[lvt_5_1_] = new int[(p_147962_1_ >> lvt_5_1_) * (p_147962_2_ >> lvt_5_1_)];
            System.arraycopy(lvt_6_1_, p_147962_3_ * lvt_4_1_[lvt_5_1_].length, lvt_4_1_[lvt_5_1_], 0, lvt_4_1_[lvt_5_1_].length);
         }
      }

      return lvt_4_1_;
   }

   public void func_130103_l() {
      this.field_110976_a.clear();
   }

   public boolean func_130098_m() {
      return this.field_110982_k != null;
   }

   public void func_110968_a(List<int[][]> p_110968_1_) {
      this.field_110976_a = p_110968_1_;
   }

   private void func_130102_n() {
      this.field_110982_k = null;
      this.func_110968_a(Lists.newArrayList());
      this.field_110973_g = 0;
      this.field_110983_h = 0;
   }

   public String toString() {
      return "TextureAtlasSprite{name=\'" + this.field_110984_i + '\'' + ", frameCount=" + this.field_110976_a.size() + ", rotated=" + this.field_130222_e + ", x=" + this.field_110975_c + ", y=" + this.field_110974_d + ", height=" + this.field_130224_d + ", width=" + this.field_130223_c + ", u0=" + this.field_110979_l + ", u1=" + this.field_110980_m + ", v0=" + this.field_110977_n + ", v1=" + this.field_110978_o + '}';
   }
}
