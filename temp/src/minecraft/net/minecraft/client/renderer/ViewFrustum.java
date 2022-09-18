package net.minecraft.client.renderer;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ViewFrustum {
   protected final RenderGlobal field_178169_a;
   protected final World field_178167_b;
   protected int field_178168_c;
   protected int field_178165_d;
   protected int field_178166_e;
   public RenderChunk[] field_178164_f;

   public ViewFrustum(World p_i46246_1_, int p_i46246_2_, RenderGlobal p_i46246_3_, IRenderChunkFactory p_i46246_4_) {
      this.field_178169_a = p_i46246_3_;
      this.field_178167_b = p_i46246_1_;
      this.func_178159_a(p_i46246_2_);
      this.func_178158_a(p_i46246_4_);
   }

   protected void func_178158_a(IRenderChunkFactory p_178158_1_) {
      int lvt_2_1_ = this.field_178165_d * this.field_178168_c * this.field_178166_e;
      this.field_178164_f = new RenderChunk[lvt_2_1_];
      int lvt_3_1_ = 0;

      for(int lvt_4_1_ = 0; lvt_4_1_ < this.field_178165_d; ++lvt_4_1_) {
         for(int lvt_5_1_ = 0; lvt_5_1_ < this.field_178168_c; ++lvt_5_1_) {
            for(int lvt_6_1_ = 0; lvt_6_1_ < this.field_178166_e; ++lvt_6_1_) {
               int lvt_7_1_ = (lvt_6_1_ * this.field_178168_c + lvt_5_1_) * this.field_178165_d + lvt_4_1_;
               BlockPos lvt_8_1_ = new BlockPos(lvt_4_1_ * 16, lvt_5_1_ * 16, lvt_6_1_ * 16);
               this.field_178164_f[lvt_7_1_] = p_178158_1_.func_178602_a(this.field_178167_b, this.field_178169_a, lvt_8_1_, lvt_3_1_++);
            }
         }
      }

   }

   public void func_178160_a() {
      for(RenderChunk lvt_4_1_ : this.field_178164_f) {
         lvt_4_1_.func_178566_a();
      }

   }

   protected void func_178159_a(int p_178159_1_) {
      int lvt_2_1_ = p_178159_1_ * 2 + 1;
      this.field_178165_d = lvt_2_1_;
      this.field_178168_c = 16;
      this.field_178166_e = lvt_2_1_;
   }

   public void func_178163_a(double p_178163_1_, double p_178163_3_) {
      int lvt_5_1_ = MathHelper.func_76128_c(p_178163_1_) - 8;
      int lvt_6_1_ = MathHelper.func_76128_c(p_178163_3_) - 8;
      int lvt_7_1_ = this.field_178165_d * 16;

      for(int lvt_8_1_ = 0; lvt_8_1_ < this.field_178165_d; ++lvt_8_1_) {
         int lvt_9_1_ = this.func_178157_a(lvt_5_1_, lvt_7_1_, lvt_8_1_);

         for(int lvt_10_1_ = 0; lvt_10_1_ < this.field_178166_e; ++lvt_10_1_) {
            int lvt_11_1_ = this.func_178157_a(lvt_6_1_, lvt_7_1_, lvt_10_1_);

            for(int lvt_12_1_ = 0; lvt_12_1_ < this.field_178168_c; ++lvt_12_1_) {
               int lvt_13_1_ = lvt_12_1_ * 16;
               RenderChunk lvt_14_1_ = this.field_178164_f[(lvt_10_1_ * this.field_178168_c + lvt_12_1_) * this.field_178165_d + lvt_8_1_];
               BlockPos lvt_15_1_ = new BlockPos(lvt_9_1_, lvt_13_1_, lvt_11_1_);
               if(!lvt_15_1_.equals(lvt_14_1_.func_178568_j())) {
                  lvt_14_1_.func_178576_a(lvt_15_1_);
               }
            }
         }
      }

   }

   private int func_178157_a(int p_178157_1_, int p_178157_2_, int p_178157_3_) {
      int lvt_4_1_ = p_178157_3_ * 16;
      int lvt_5_1_ = lvt_4_1_ - p_178157_1_ + p_178157_2_ / 2;
      if(lvt_5_1_ < 0) {
         lvt_5_1_ -= p_178157_2_ - 1;
      }

      return lvt_4_1_ - lvt_5_1_ / p_178157_2_ * p_178157_2_;
   }

   public void func_178162_a(int p_178162_1_, int p_178162_2_, int p_178162_3_, int p_178162_4_, int p_178162_5_, int p_178162_6_) {
      int lvt_7_1_ = MathHelper.func_76137_a(p_178162_1_, 16);
      int lvt_8_1_ = MathHelper.func_76137_a(p_178162_2_, 16);
      int lvt_9_1_ = MathHelper.func_76137_a(p_178162_3_, 16);
      int lvt_10_1_ = MathHelper.func_76137_a(p_178162_4_, 16);
      int lvt_11_1_ = MathHelper.func_76137_a(p_178162_5_, 16);
      int lvt_12_1_ = MathHelper.func_76137_a(p_178162_6_, 16);

      for(int lvt_13_1_ = lvt_7_1_; lvt_13_1_ <= lvt_10_1_; ++lvt_13_1_) {
         int lvt_14_1_ = lvt_13_1_ % this.field_178165_d;
         if(lvt_14_1_ < 0) {
            lvt_14_1_ += this.field_178165_d;
         }

         for(int lvt_15_1_ = lvt_8_1_; lvt_15_1_ <= lvt_11_1_; ++lvt_15_1_) {
            int lvt_16_1_ = lvt_15_1_ % this.field_178168_c;
            if(lvt_16_1_ < 0) {
               lvt_16_1_ += this.field_178168_c;
            }

            for(int lvt_17_1_ = lvt_9_1_; lvt_17_1_ <= lvt_12_1_; ++lvt_17_1_) {
               int lvt_18_1_ = lvt_17_1_ % this.field_178166_e;
               if(lvt_18_1_ < 0) {
                  lvt_18_1_ += this.field_178166_e;
               }

               int lvt_19_1_ = (lvt_18_1_ * this.field_178168_c + lvt_16_1_) * this.field_178165_d + lvt_14_1_;
               RenderChunk lvt_20_1_ = this.field_178164_f[lvt_19_1_];
               lvt_20_1_.func_178575_a(true);
            }
         }
      }

   }

   protected RenderChunk func_178161_a(BlockPos p_178161_1_) {
      int lvt_2_1_ = MathHelper.func_76137_a(p_178161_1_.func_177958_n(), 16);
      int lvt_3_1_ = MathHelper.func_76137_a(p_178161_1_.func_177956_o(), 16);
      int lvt_4_1_ = MathHelper.func_76137_a(p_178161_1_.func_177952_p(), 16);
      if(lvt_3_1_ >= 0 && lvt_3_1_ < this.field_178168_c) {
         lvt_2_1_ = lvt_2_1_ % this.field_178165_d;
         if(lvt_2_1_ < 0) {
            lvt_2_1_ += this.field_178165_d;
         }

         lvt_4_1_ = lvt_4_1_ % this.field_178166_e;
         if(lvt_4_1_ < 0) {
            lvt_4_1_ += this.field_178166_e;
         }

         int lvt_5_1_ = (lvt_4_1_ * this.field_178168_c + lvt_3_1_) * this.field_178165_d + lvt_2_1_;
         return this.field_178164_f[lvt_5_1_];
      } else {
         return null;
      }
   }
}
