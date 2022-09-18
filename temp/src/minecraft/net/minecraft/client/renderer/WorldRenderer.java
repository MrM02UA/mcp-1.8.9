package net.minecraft.client.renderer;

import com.google.common.primitives.Floats;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;

public class WorldRenderer {
   private ByteBuffer field_179001_a;
   private IntBuffer field_178999_b;
   private ShortBuffer field_181676_c;
   private FloatBuffer field_179000_c;
   private int field_178997_d;
   private VertexFormatElement field_181677_f;
   private int field_181678_g;
   private boolean field_78939_q;
   private int field_179006_k;
   private double field_179004_l;
   private double field_179005_m;
   private double field_179002_n;
   private VertexFormat field_179011_q;
   private boolean field_179010_r;

   public WorldRenderer(int p_i46275_1_) {
      this.field_179001_a = GLAllocation.func_74524_c(p_i46275_1_ * 4);
      this.field_178999_b = this.field_179001_a.asIntBuffer();
      this.field_181676_c = this.field_179001_a.asShortBuffer();
      this.field_179000_c = this.field_179001_a.asFloatBuffer();
   }

   private void func_181670_b(int p_181670_1_) {
      if(p_181670_1_ > this.field_178999_b.remaining()) {
         int lvt_2_1_ = this.field_179001_a.capacity();
         int lvt_3_1_ = lvt_2_1_ % 2097152;
         int lvt_4_1_ = lvt_3_1_ + (((this.field_178999_b.position() + p_181670_1_) * 4 - lvt_3_1_) / 2097152 + 1) * 2097152;
         LogManager.getLogger().warn("Needed to grow BufferBuilder buffer: Old size " + lvt_2_1_ + " bytes, new size " + lvt_4_1_ + " bytes.");
         int lvt_5_1_ = this.field_178999_b.position();
         ByteBuffer lvt_6_1_ = GLAllocation.func_74524_c(lvt_4_1_);
         this.field_179001_a.position(0);
         lvt_6_1_.put(this.field_179001_a);
         lvt_6_1_.rewind();
         this.field_179001_a = lvt_6_1_;
         this.field_179000_c = this.field_179001_a.asFloatBuffer().asReadOnlyBuffer();
         this.field_178999_b = this.field_179001_a.asIntBuffer();
         this.field_178999_b.position(lvt_5_1_);
         this.field_181676_c = this.field_179001_a.asShortBuffer();
         this.field_181676_c.position(lvt_5_1_ << 1);
      }
   }

   public void func_181674_a(float p_181674_1_, float p_181674_2_, float p_181674_3_) {
      int lvt_4_1_ = this.field_178997_d / 4;
      final float[] lvt_5_1_ = new float[lvt_4_1_];

      for(int lvt_6_1_ = 0; lvt_6_1_ < lvt_4_1_; ++lvt_6_1_) {
         lvt_5_1_[lvt_6_1_] = func_181665_a(this.field_179000_c, (float)((double)p_181674_1_ + this.field_179004_l), (float)((double)p_181674_2_ + this.field_179005_m), (float)((double)p_181674_3_ + this.field_179002_n), this.field_179011_q.func_181719_f(), lvt_6_1_ * this.field_179011_q.func_177338_f());
      }

      Integer[] lvt_6_2_ = new Integer[lvt_4_1_];

      for(int lvt_7_1_ = 0; lvt_7_1_ < lvt_6_2_.length; ++lvt_7_1_) {
         lvt_6_2_[lvt_7_1_] = Integer.valueOf(lvt_7_1_);
      }

      Arrays.sort(lvt_6_2_, new Comparator<Integer>() {
         public int compare(Integer p_compare_1_, Integer p_compare_2_) {
            return Floats.compare(lvt_5_1_[p_compare_2_.intValue()], lvt_5_1_[p_compare_1_.intValue()]);
         }

         // $FF: synthetic method
         public int compare(Object p_compare_1_, Object p_compare_2_) {
            return this.compare((Integer)p_compare_1_, (Integer)p_compare_2_);
         }
      });
      BitSet lvt_7_2_ = new BitSet();
      int lvt_8_1_ = this.field_179011_q.func_177338_f();
      int[] lvt_9_1_ = new int[lvt_8_1_];

      for(int lvt_10_1_ = 0; (lvt_10_1_ = lvt_7_2_.nextClearBit(lvt_10_1_)) < lvt_6_2_.length; ++lvt_10_1_) {
         int lvt_11_1_ = lvt_6_2_[lvt_10_1_].intValue();
         if(lvt_11_1_ != lvt_10_1_) {
            this.field_178999_b.limit(lvt_11_1_ * lvt_8_1_ + lvt_8_1_);
            this.field_178999_b.position(lvt_11_1_ * lvt_8_1_);
            this.field_178999_b.get(lvt_9_1_);
            int lvt_12_1_ = lvt_11_1_;

            for(int lvt_13_1_ = lvt_6_2_[lvt_11_1_].intValue(); lvt_12_1_ != lvt_10_1_; lvt_13_1_ = lvt_6_2_[lvt_13_1_].intValue()) {
               this.field_178999_b.limit(lvt_13_1_ * lvt_8_1_ + lvt_8_1_);
               this.field_178999_b.position(lvt_13_1_ * lvt_8_1_);
               IntBuffer lvt_14_1_ = this.field_178999_b.slice();
               this.field_178999_b.limit(lvt_12_1_ * lvt_8_1_ + lvt_8_1_);
               this.field_178999_b.position(lvt_12_1_ * lvt_8_1_);
               this.field_178999_b.put(lvt_14_1_);
               lvt_7_2_.set(lvt_12_1_);
               lvt_12_1_ = lvt_13_1_;
            }

            this.field_178999_b.limit(lvt_10_1_ * lvt_8_1_ + lvt_8_1_);
            this.field_178999_b.position(lvt_10_1_ * lvt_8_1_);
            this.field_178999_b.put(lvt_9_1_);
         }

         lvt_7_2_.set(lvt_10_1_);
      }

   }

   public WorldRenderer.State func_181672_a() {
      this.field_178999_b.rewind();
      int lvt_1_1_ = this.func_181664_j();
      this.field_178999_b.limit(lvt_1_1_);
      int[] lvt_2_1_ = new int[lvt_1_1_];
      this.field_178999_b.get(lvt_2_1_);
      this.field_178999_b.limit(this.field_178999_b.capacity());
      this.field_178999_b.position(lvt_1_1_);
      return new WorldRenderer.State(lvt_2_1_, new VertexFormat(this.field_179011_q));
   }

   private int func_181664_j() {
      return this.field_178997_d * this.field_179011_q.func_181719_f();
   }

   private static float func_181665_a(FloatBuffer p_181665_0_, float p_181665_1_, float p_181665_2_, float p_181665_3_, int p_181665_4_, int p_181665_5_) {
      float lvt_6_1_ = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 0 + 0);
      float lvt_7_1_ = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 0 + 1);
      float lvt_8_1_ = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 0 + 2);
      float lvt_9_1_ = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 1 + 0);
      float lvt_10_1_ = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 1 + 1);
      float lvt_11_1_ = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 1 + 2);
      float lvt_12_1_ = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 2 + 0);
      float lvt_13_1_ = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 2 + 1);
      float lvt_14_1_ = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 2 + 2);
      float lvt_15_1_ = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 3 + 0);
      float lvt_16_1_ = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 3 + 1);
      float lvt_17_1_ = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 3 + 2);
      float lvt_18_1_ = (lvt_6_1_ + lvt_9_1_ + lvt_12_1_ + lvt_15_1_) * 0.25F - p_181665_1_;
      float lvt_19_1_ = (lvt_7_1_ + lvt_10_1_ + lvt_13_1_ + lvt_16_1_) * 0.25F - p_181665_2_;
      float lvt_20_1_ = (lvt_8_1_ + lvt_11_1_ + lvt_14_1_ + lvt_17_1_) * 0.25F - p_181665_3_;
      return lvt_18_1_ * lvt_18_1_ + lvt_19_1_ * lvt_19_1_ + lvt_20_1_ * lvt_20_1_;
   }

   public void func_178993_a(WorldRenderer.State p_178993_1_) {
      this.field_178999_b.clear();
      this.func_181670_b(p_178993_1_.func_179013_a().length);
      this.field_178999_b.put(p_178993_1_.func_179013_a());
      this.field_178997_d = p_178993_1_.func_179014_c();
      this.field_179011_q = new VertexFormat(p_178993_1_.func_179016_d());
   }

   public void func_178965_a() {
      this.field_178997_d = 0;
      this.field_181677_f = null;
      this.field_181678_g = 0;
   }

   public void func_181668_a(int p_181668_1_, VertexFormat p_181668_2_) {
      if(this.field_179010_r) {
         throw new IllegalStateException("Already building!");
      } else {
         this.field_179010_r = true;
         this.func_178965_a();
         this.field_179006_k = p_181668_1_;
         this.field_179011_q = p_181668_2_;
         this.field_181677_f = p_181668_2_.func_177348_c(this.field_181678_g);
         this.field_78939_q = false;
         this.field_179001_a.limit(this.field_179001_a.capacity());
      }
   }

   public WorldRenderer func_181673_a(double p_181673_1_, double p_181673_3_) {
      int lvt_5_1_ = this.field_178997_d * this.field_179011_q.func_177338_f() + this.field_179011_q.func_181720_d(this.field_181678_g);
      switch(this.field_181677_f.func_177367_b()) {
      case FLOAT:
         this.field_179001_a.putFloat(lvt_5_1_, (float)p_181673_1_);
         this.field_179001_a.putFloat(lvt_5_1_ + 4, (float)p_181673_3_);
         break;
      case UINT:
      case INT:
         this.field_179001_a.putInt(lvt_5_1_, (int)p_181673_1_);
         this.field_179001_a.putInt(lvt_5_1_ + 4, (int)p_181673_3_);
         break;
      case USHORT:
      case SHORT:
         this.field_179001_a.putShort(lvt_5_1_, (short)((int)p_181673_3_));
         this.field_179001_a.putShort(lvt_5_1_ + 2, (short)((int)p_181673_1_));
         break;
      case UBYTE:
      case BYTE:
         this.field_179001_a.put(lvt_5_1_, (byte)((int)p_181673_3_));
         this.field_179001_a.put(lvt_5_1_ + 1, (byte)((int)p_181673_1_));
      }

      this.func_181667_k();
      return this;
   }

   public WorldRenderer func_181671_a(int p_181671_1_, int p_181671_2_) {
      int lvt_3_1_ = this.field_178997_d * this.field_179011_q.func_177338_f() + this.field_179011_q.func_181720_d(this.field_181678_g);
      switch(this.field_181677_f.func_177367_b()) {
      case FLOAT:
         this.field_179001_a.putFloat(lvt_3_1_, (float)p_181671_1_);
         this.field_179001_a.putFloat(lvt_3_1_ + 4, (float)p_181671_2_);
         break;
      case UINT:
      case INT:
         this.field_179001_a.putInt(lvt_3_1_, p_181671_1_);
         this.field_179001_a.putInt(lvt_3_1_ + 4, p_181671_2_);
         break;
      case USHORT:
      case SHORT:
         this.field_179001_a.putShort(lvt_3_1_, (short)p_181671_2_);
         this.field_179001_a.putShort(lvt_3_1_ + 2, (short)p_181671_1_);
         break;
      case UBYTE:
      case BYTE:
         this.field_179001_a.put(lvt_3_1_, (byte)p_181671_2_);
         this.field_179001_a.put(lvt_3_1_ + 1, (byte)p_181671_1_);
      }

      this.func_181667_k();
      return this;
   }

   public void func_178962_a(int p_178962_1_, int p_178962_2_, int p_178962_3_, int p_178962_4_) {
      int lvt_5_1_ = (this.field_178997_d - 4) * this.field_179011_q.func_181719_f() + this.field_179011_q.func_177344_b(1) / 4;
      int lvt_6_1_ = this.field_179011_q.func_177338_f() >> 2;
      this.field_178999_b.put(lvt_5_1_, p_178962_1_);
      this.field_178999_b.put(lvt_5_1_ + lvt_6_1_, p_178962_2_);
      this.field_178999_b.put(lvt_5_1_ + lvt_6_1_ * 2, p_178962_3_);
      this.field_178999_b.put(lvt_5_1_ + lvt_6_1_ * 3, p_178962_4_);
   }

   public void func_178987_a(double p_178987_1_, double p_178987_3_, double p_178987_5_) {
      int lvt_7_1_ = this.field_179011_q.func_181719_f();
      int lvt_8_1_ = (this.field_178997_d - 4) * lvt_7_1_;

      for(int lvt_9_1_ = 0; lvt_9_1_ < 4; ++lvt_9_1_) {
         int lvt_10_1_ = lvt_8_1_ + lvt_9_1_ * lvt_7_1_;
         int lvt_11_1_ = lvt_10_1_ + 1;
         int lvt_12_1_ = lvt_11_1_ + 1;
         this.field_178999_b.put(lvt_10_1_, Float.floatToRawIntBits((float)(p_178987_1_ + this.field_179004_l) + Float.intBitsToFloat(this.field_178999_b.get(lvt_10_1_))));
         this.field_178999_b.put(lvt_11_1_, Float.floatToRawIntBits((float)(p_178987_3_ + this.field_179005_m) + Float.intBitsToFloat(this.field_178999_b.get(lvt_11_1_))));
         this.field_178999_b.put(lvt_12_1_, Float.floatToRawIntBits((float)(p_178987_5_ + this.field_179002_n) + Float.intBitsToFloat(this.field_178999_b.get(lvt_12_1_))));
      }

   }

   private int func_78909_a(int p_78909_1_) {
      return ((this.field_178997_d - p_78909_1_) * this.field_179011_q.func_177338_f() + this.field_179011_q.func_177340_e()) / 4;
   }

   public void func_178978_a(float p_178978_1_, float p_178978_2_, float p_178978_3_, int p_178978_4_) {
      int lvt_5_1_ = this.func_78909_a(p_178978_4_);
      int lvt_6_1_ = -1;
      if(!this.field_78939_q) {
         lvt_6_1_ = this.field_178999_b.get(lvt_5_1_);
         if(ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            int lvt_7_1_ = (int)((float)(lvt_6_1_ & 255) * p_178978_1_);
            int lvt_8_1_ = (int)((float)(lvt_6_1_ >> 8 & 255) * p_178978_2_);
            int lvt_9_1_ = (int)((float)(lvt_6_1_ >> 16 & 255) * p_178978_3_);
            lvt_6_1_ = lvt_6_1_ & -16777216;
            lvt_6_1_ = lvt_6_1_ | lvt_9_1_ << 16 | lvt_8_1_ << 8 | lvt_7_1_;
         } else {
            int lvt_7_2_ = (int)((float)(lvt_6_1_ >> 24 & 255) * p_178978_1_);
            int lvt_8_2_ = (int)((float)(lvt_6_1_ >> 16 & 255) * p_178978_2_);
            int lvt_9_2_ = (int)((float)(lvt_6_1_ >> 8 & 255) * p_178978_3_);
            lvt_6_1_ = lvt_6_1_ & 255;
            lvt_6_1_ = lvt_6_1_ | lvt_7_2_ << 24 | lvt_8_2_ << 16 | lvt_9_2_ << 8;
         }
      }

      this.field_178999_b.put(lvt_5_1_, lvt_6_1_);
   }

   private void func_178988_b(int p_178988_1_, int p_178988_2_) {
      int lvt_3_1_ = this.func_78909_a(p_178988_2_);
      int lvt_4_1_ = p_178988_1_ >> 16 & 255;
      int lvt_5_1_ = p_178988_1_ >> 8 & 255;
      int lvt_6_1_ = p_178988_1_ & 255;
      int lvt_7_1_ = p_178988_1_ >> 24 & 255;
      this.func_178972_a(lvt_3_1_, lvt_4_1_, lvt_5_1_, lvt_6_1_, lvt_7_1_);
   }

   public void func_178994_b(float p_178994_1_, float p_178994_2_, float p_178994_3_, int p_178994_4_) {
      int lvt_5_1_ = this.func_78909_a(p_178994_4_);
      int lvt_6_1_ = MathHelper.func_76125_a((int)(p_178994_1_ * 255.0F), 0, 255);
      int lvt_7_1_ = MathHelper.func_76125_a((int)(p_178994_2_ * 255.0F), 0, 255);
      int lvt_8_1_ = MathHelper.func_76125_a((int)(p_178994_3_ * 255.0F), 0, 255);
      this.func_178972_a(lvt_5_1_, lvt_6_1_, lvt_7_1_, lvt_8_1_, 255);
   }

   private void func_178972_a(int p_178972_1_, int p_178972_2_, int p_178972_3_, int p_178972_4_, int p_178972_5_) {
      if(ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
         this.field_178999_b.put(p_178972_1_, p_178972_5_ << 24 | p_178972_4_ << 16 | p_178972_3_ << 8 | p_178972_2_);
      } else {
         this.field_178999_b.put(p_178972_1_, p_178972_2_ << 24 | p_178972_3_ << 16 | p_178972_4_ << 8 | p_178972_5_);
      }

   }

   public void func_78914_f() {
      this.field_78939_q = true;
   }

   public WorldRenderer func_181666_a(float p_181666_1_, float p_181666_2_, float p_181666_3_, float p_181666_4_) {
      return this.func_181669_b((int)(p_181666_1_ * 255.0F), (int)(p_181666_2_ * 255.0F), (int)(p_181666_3_ * 255.0F), (int)(p_181666_4_ * 255.0F));
   }

   public WorldRenderer func_181669_b(int p_181669_1_, int p_181669_2_, int p_181669_3_, int p_181669_4_) {
      if(this.field_78939_q) {
         return this;
      } else {
         int lvt_5_1_ = this.field_178997_d * this.field_179011_q.func_177338_f() + this.field_179011_q.func_181720_d(this.field_181678_g);
         switch(this.field_181677_f.func_177367_b()) {
         case FLOAT:
            this.field_179001_a.putFloat(lvt_5_1_, (float)p_181669_1_ / 255.0F);
            this.field_179001_a.putFloat(lvt_5_1_ + 4, (float)p_181669_2_ / 255.0F);
            this.field_179001_a.putFloat(lvt_5_1_ + 8, (float)p_181669_3_ / 255.0F);
            this.field_179001_a.putFloat(lvt_5_1_ + 12, (float)p_181669_4_ / 255.0F);
            break;
         case UINT:
         case INT:
            this.field_179001_a.putFloat(lvt_5_1_, (float)p_181669_1_);
            this.field_179001_a.putFloat(lvt_5_1_ + 4, (float)p_181669_2_);
            this.field_179001_a.putFloat(lvt_5_1_ + 8, (float)p_181669_3_);
            this.field_179001_a.putFloat(lvt_5_1_ + 12, (float)p_181669_4_);
            break;
         case USHORT:
         case SHORT:
            this.field_179001_a.putShort(lvt_5_1_, (short)p_181669_1_);
            this.field_179001_a.putShort(lvt_5_1_ + 2, (short)p_181669_2_);
            this.field_179001_a.putShort(lvt_5_1_ + 4, (short)p_181669_3_);
            this.field_179001_a.putShort(lvt_5_1_ + 6, (short)p_181669_4_);
            break;
         case UBYTE:
         case BYTE:
            if(ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
               this.field_179001_a.put(lvt_5_1_, (byte)p_181669_1_);
               this.field_179001_a.put(lvt_5_1_ + 1, (byte)p_181669_2_);
               this.field_179001_a.put(lvt_5_1_ + 2, (byte)p_181669_3_);
               this.field_179001_a.put(lvt_5_1_ + 3, (byte)p_181669_4_);
            } else {
               this.field_179001_a.put(lvt_5_1_, (byte)p_181669_4_);
               this.field_179001_a.put(lvt_5_1_ + 1, (byte)p_181669_3_);
               this.field_179001_a.put(lvt_5_1_ + 2, (byte)p_181669_2_);
               this.field_179001_a.put(lvt_5_1_ + 3, (byte)p_181669_1_);
            }
         }

         this.func_181667_k();
         return this;
      }
   }

   public void func_178981_a(int[] p_178981_1_) {
      this.func_181670_b(p_178981_1_.length);
      this.field_178999_b.position(this.func_181664_j());
      this.field_178999_b.put(p_178981_1_);
      this.field_178997_d += p_178981_1_.length / this.field_179011_q.func_181719_f();
   }

   public void func_181675_d() {
      ++this.field_178997_d;
      this.func_181670_b(this.field_179011_q.func_181719_f());
   }

   public WorldRenderer func_181662_b(double p_181662_1_, double p_181662_3_, double p_181662_5_) {
      int lvt_7_1_ = this.field_178997_d * this.field_179011_q.func_177338_f() + this.field_179011_q.func_181720_d(this.field_181678_g);
      switch(this.field_181677_f.func_177367_b()) {
      case FLOAT:
         this.field_179001_a.putFloat(lvt_7_1_, (float)(p_181662_1_ + this.field_179004_l));
         this.field_179001_a.putFloat(lvt_7_1_ + 4, (float)(p_181662_3_ + this.field_179005_m));
         this.field_179001_a.putFloat(lvt_7_1_ + 8, (float)(p_181662_5_ + this.field_179002_n));
         break;
      case UINT:
      case INT:
         this.field_179001_a.putInt(lvt_7_1_, Float.floatToRawIntBits((float)(p_181662_1_ + this.field_179004_l)));
         this.field_179001_a.putInt(lvt_7_1_ + 4, Float.floatToRawIntBits((float)(p_181662_3_ + this.field_179005_m)));
         this.field_179001_a.putInt(lvt_7_1_ + 8, Float.floatToRawIntBits((float)(p_181662_5_ + this.field_179002_n)));
         break;
      case USHORT:
      case SHORT:
         this.field_179001_a.putShort(lvt_7_1_, (short)((int)(p_181662_1_ + this.field_179004_l)));
         this.field_179001_a.putShort(lvt_7_1_ + 2, (short)((int)(p_181662_3_ + this.field_179005_m)));
         this.field_179001_a.putShort(lvt_7_1_ + 4, (short)((int)(p_181662_5_ + this.field_179002_n)));
         break;
      case UBYTE:
      case BYTE:
         this.field_179001_a.put(lvt_7_1_, (byte)((int)(p_181662_1_ + this.field_179004_l)));
         this.field_179001_a.put(lvt_7_1_ + 1, (byte)((int)(p_181662_3_ + this.field_179005_m)));
         this.field_179001_a.put(lvt_7_1_ + 2, (byte)((int)(p_181662_5_ + this.field_179002_n)));
      }

      this.func_181667_k();
      return this;
   }

   public void func_178975_e(float p_178975_1_, float p_178975_2_, float p_178975_3_) {
      int lvt_4_1_ = (byte)((int)(p_178975_1_ * 127.0F)) & 255;
      int lvt_5_1_ = (byte)((int)(p_178975_2_ * 127.0F)) & 255;
      int lvt_6_1_ = (byte)((int)(p_178975_3_ * 127.0F)) & 255;
      int lvt_7_1_ = lvt_4_1_ | lvt_5_1_ << 8 | lvt_6_1_ << 16;
      int lvt_8_1_ = this.field_179011_q.func_177338_f() >> 2;
      int lvt_9_1_ = (this.field_178997_d - 4) * lvt_8_1_ + this.field_179011_q.func_177342_c() / 4;
      this.field_178999_b.put(lvt_9_1_, lvt_7_1_);
      this.field_178999_b.put(lvt_9_1_ + lvt_8_1_, lvt_7_1_);
      this.field_178999_b.put(lvt_9_1_ + lvt_8_1_ * 2, lvt_7_1_);
      this.field_178999_b.put(lvt_9_1_ + lvt_8_1_ * 3, lvt_7_1_);
   }

   private void func_181667_k() {
      ++this.field_181678_g;
      this.field_181678_g %= this.field_179011_q.func_177345_h();
      this.field_181677_f = this.field_179011_q.func_177348_c(this.field_181678_g);
      if(this.field_181677_f.func_177375_c() == VertexFormatElement.EnumUsage.PADDING) {
         this.func_181667_k();
      }

   }

   public WorldRenderer func_181663_c(float p_181663_1_, float p_181663_2_, float p_181663_3_) {
      int lvt_4_1_ = this.field_178997_d * this.field_179011_q.func_177338_f() + this.field_179011_q.func_181720_d(this.field_181678_g);
      switch(this.field_181677_f.func_177367_b()) {
      case FLOAT:
         this.field_179001_a.putFloat(lvt_4_1_, p_181663_1_);
         this.field_179001_a.putFloat(lvt_4_1_ + 4, p_181663_2_);
         this.field_179001_a.putFloat(lvt_4_1_ + 8, p_181663_3_);
         break;
      case UINT:
      case INT:
         this.field_179001_a.putInt(lvt_4_1_, (int)p_181663_1_);
         this.field_179001_a.putInt(lvt_4_1_ + 4, (int)p_181663_2_);
         this.field_179001_a.putInt(lvt_4_1_ + 8, (int)p_181663_3_);
         break;
      case USHORT:
      case SHORT:
         this.field_179001_a.putShort(lvt_4_1_, (short)((int)p_181663_1_ * 32767 & '\uffff'));
         this.field_179001_a.putShort(lvt_4_1_ + 2, (short)((int)p_181663_2_ * 32767 & '\uffff'));
         this.field_179001_a.putShort(lvt_4_1_ + 4, (short)((int)p_181663_3_ * 32767 & '\uffff'));
         break;
      case UBYTE:
      case BYTE:
         this.field_179001_a.put(lvt_4_1_, (byte)((int)p_181663_1_ * 127 & 255));
         this.field_179001_a.put(lvt_4_1_ + 1, (byte)((int)p_181663_2_ * 127 & 255));
         this.field_179001_a.put(lvt_4_1_ + 2, (byte)((int)p_181663_3_ * 127 & 255));
      }

      this.func_181667_k();
      return this;
   }

   public void func_178969_c(double p_178969_1_, double p_178969_3_, double p_178969_5_) {
      this.field_179004_l = p_178969_1_;
      this.field_179005_m = p_178969_3_;
      this.field_179002_n = p_178969_5_;
   }

   public void func_178977_d() {
      if(!this.field_179010_r) {
         throw new IllegalStateException("Not building!");
      } else {
         this.field_179010_r = false;
         this.field_179001_a.position(0);
         this.field_179001_a.limit(this.func_181664_j() * 4);
      }
   }

   public ByteBuffer func_178966_f() {
      return this.field_179001_a;
   }

   public VertexFormat func_178973_g() {
      return this.field_179011_q;
   }

   public int func_178989_h() {
      return this.field_178997_d;
   }

   public int func_178979_i() {
      return this.field_179006_k;
   }

   public void func_178968_d(int p_178968_1_) {
      for(int lvt_2_1_ = 0; lvt_2_1_ < 4; ++lvt_2_1_) {
         this.func_178988_b(p_178968_1_, lvt_2_1_ + 1);
      }

   }

   public void func_178990_f(float p_178990_1_, float p_178990_2_, float p_178990_3_) {
      for(int lvt_4_1_ = 0; lvt_4_1_ < 4; ++lvt_4_1_) {
         this.func_178994_b(p_178990_1_, p_178990_2_, p_178990_3_, lvt_4_1_ + 1);
      }

   }

   public class State {
      private final int[] field_179019_b;
      private final VertexFormat field_179018_e;

      public State(int[] p_i46453_2_, VertexFormat p_i46453_3_) {
         this.field_179019_b = p_i46453_2_;
         this.field_179018_e = p_i46453_3_;
      }

      public int[] func_179013_a() {
         return this.field_179019_b;
      }

      public int func_179014_c() {
         return this.field_179019_b.length / this.field_179018_e.func_181719_f();
      }

      public VertexFormat func_179016_d() {
         return this.field_179018_e;
      }
   }
}
