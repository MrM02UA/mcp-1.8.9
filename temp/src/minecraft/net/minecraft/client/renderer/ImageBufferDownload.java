package net.minecraft.client.renderer;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import net.minecraft.client.renderer.IImageBuffer;

public class ImageBufferDownload implements IImageBuffer {
   private int[] field_78438_a;
   private int field_78436_b;
   private int field_78437_c;

   public BufferedImage func_78432_a(BufferedImage p_78432_1_) {
      if(p_78432_1_ == null) {
         return null;
      } else {
         this.field_78436_b = 64;
         this.field_78437_c = 64;
         BufferedImage lvt_2_1_ = new BufferedImage(this.field_78436_b, this.field_78437_c, 2);
         Graphics lvt_3_1_ = lvt_2_1_.getGraphics();
         lvt_3_1_.drawImage(p_78432_1_, 0, 0, (ImageObserver)null);
         if(p_78432_1_.getHeight() == 32) {
            lvt_3_1_.drawImage(lvt_2_1_, 24, 48, 20, 52, 4, 16, 8, 20, (ImageObserver)null);
            lvt_3_1_.drawImage(lvt_2_1_, 28, 48, 24, 52, 8, 16, 12, 20, (ImageObserver)null);
            lvt_3_1_.drawImage(lvt_2_1_, 20, 52, 16, 64, 8, 20, 12, 32, (ImageObserver)null);
            lvt_3_1_.drawImage(lvt_2_1_, 24, 52, 20, 64, 4, 20, 8, 32, (ImageObserver)null);
            lvt_3_1_.drawImage(lvt_2_1_, 28, 52, 24, 64, 0, 20, 4, 32, (ImageObserver)null);
            lvt_3_1_.drawImage(lvt_2_1_, 32, 52, 28, 64, 12, 20, 16, 32, (ImageObserver)null);
            lvt_3_1_.drawImage(lvt_2_1_, 40, 48, 36, 52, 44, 16, 48, 20, (ImageObserver)null);
            lvt_3_1_.drawImage(lvt_2_1_, 44, 48, 40, 52, 48, 16, 52, 20, (ImageObserver)null);
            lvt_3_1_.drawImage(lvt_2_1_, 36, 52, 32, 64, 48, 20, 52, 32, (ImageObserver)null);
            lvt_3_1_.drawImage(lvt_2_1_, 40, 52, 36, 64, 44, 20, 48, 32, (ImageObserver)null);
            lvt_3_1_.drawImage(lvt_2_1_, 44, 52, 40, 64, 40, 20, 44, 32, (ImageObserver)null);
            lvt_3_1_.drawImage(lvt_2_1_, 48, 52, 44, 64, 52, 20, 56, 32, (ImageObserver)null);
         }

         lvt_3_1_.dispose();
         this.field_78438_a = ((DataBufferInt)lvt_2_1_.getRaster().getDataBuffer()).getData();
         this.func_78433_b(0, 0, 32, 16);
         this.func_78434_a(32, 0, 64, 32);
         this.func_78433_b(0, 16, 64, 32);
         this.func_78434_a(0, 32, 16, 48);
         this.func_78434_a(16, 32, 40, 48);
         this.func_78434_a(40, 32, 56, 48);
         this.func_78434_a(0, 48, 16, 64);
         this.func_78433_b(16, 48, 48, 64);
         this.func_78434_a(48, 48, 64, 64);
         return lvt_2_1_;
      }
   }

   public void func_152634_a() {
   }

   private void func_78434_a(int p_78434_1_, int p_78434_2_, int p_78434_3_, int p_78434_4_) {
      if(!this.func_78435_c(p_78434_1_, p_78434_2_, p_78434_3_, p_78434_4_)) {
         for(int lvt_5_1_ = p_78434_1_; lvt_5_1_ < p_78434_3_; ++lvt_5_1_) {
            for(int lvt_6_1_ = p_78434_2_; lvt_6_1_ < p_78434_4_; ++lvt_6_1_) {
               this.field_78438_a[lvt_5_1_ + lvt_6_1_ * this.field_78436_b] &= 16777215;
            }
         }

      }
   }

   private void func_78433_b(int p_78433_1_, int p_78433_2_, int p_78433_3_, int p_78433_4_) {
      for(int lvt_5_1_ = p_78433_1_; lvt_5_1_ < p_78433_3_; ++lvt_5_1_) {
         for(int lvt_6_1_ = p_78433_2_; lvt_6_1_ < p_78433_4_; ++lvt_6_1_) {
            this.field_78438_a[lvt_5_1_ + lvt_6_1_ * this.field_78436_b] |= -16777216;
         }
      }

   }

   private boolean func_78435_c(int p_78435_1_, int p_78435_2_, int p_78435_3_, int p_78435_4_) {
      for(int lvt_5_1_ = p_78435_1_; lvt_5_1_ < p_78435_3_; ++lvt_5_1_) {
         for(int lvt_6_1_ = p_78435_2_; lvt_6_1_ < p_78435_4_; ++lvt_6_1_) {
            int lvt_7_1_ = this.field_78438_a[lvt_5_1_ + lvt_6_1_ * this.field_78436_b];
            if((lvt_7_1_ >> 24 & 255) < 128) {
               return true;
            }
         }
      }

      return false;
   }
}
