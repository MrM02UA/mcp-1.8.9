package net.minecraft.client.renderer.block.model;

import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3i;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class FaceBakery {
   private static final float field_178418_a = 1.0F / (float)Math.cos(0.39269909262657166D) - 1.0F;
   private static final float field_178417_b = 1.0F / (float)Math.cos(0.7853981852531433D) - 1.0F;

   public BakedQuad func_178414_a(Vector3f p_178414_1_, Vector3f p_178414_2_, BlockPartFace p_178414_3_, TextureAtlasSprite p_178414_4_, EnumFacing p_178414_5_, ModelRotation p_178414_6_, BlockPartRotation p_178414_7_, boolean p_178414_8_, boolean p_178414_9_) {
      int[] lvt_10_1_ = this.func_178405_a(p_178414_3_, p_178414_4_, p_178414_5_, this.func_178403_a(p_178414_1_, p_178414_2_), p_178414_6_, p_178414_7_, p_178414_8_, p_178414_9_);
      EnumFacing lvt_11_1_ = func_178410_a(lvt_10_1_);
      if(p_178414_8_) {
         this.func_178409_a(lvt_10_1_, lvt_11_1_, p_178414_3_.field_178243_e, p_178414_4_);
      }

      if(p_178414_7_ == null) {
         this.func_178408_a(lvt_10_1_, lvt_11_1_);
      }

      return new BakedQuad(lvt_10_1_, p_178414_3_.field_178245_c, lvt_11_1_);
   }

   private int[] func_178405_a(BlockPartFace p_178405_1_, TextureAtlasSprite p_178405_2_, EnumFacing p_178405_3_, float[] p_178405_4_, ModelRotation p_178405_5_, BlockPartRotation p_178405_6_, boolean p_178405_7_, boolean p_178405_8_) {
      int[] lvt_9_1_ = new int[28];

      for(int lvt_10_1_ = 0; lvt_10_1_ < 4; ++lvt_10_1_) {
         this.func_178402_a(lvt_9_1_, lvt_10_1_, p_178405_3_, p_178405_1_, p_178405_4_, p_178405_2_, p_178405_5_, p_178405_6_, p_178405_7_, p_178405_8_);
      }

      return lvt_9_1_;
   }

   private int func_178413_a(EnumFacing p_178413_1_) {
      float lvt_2_1_ = this.func_178412_b(p_178413_1_);
      int lvt_3_1_ = MathHelper.func_76125_a((int)(lvt_2_1_ * 255.0F), 0, 255);
      return -16777216 | lvt_3_1_ << 16 | lvt_3_1_ << 8 | lvt_3_1_;
   }

   private float func_178412_b(EnumFacing p_178412_1_) {
      switch(p_178412_1_) {
      case DOWN:
         return 0.5F;
      case UP:
         return 1.0F;
      case NORTH:
      case SOUTH:
         return 0.8F;
      case WEST:
      case EAST:
         return 0.6F;
      default:
         return 1.0F;
      }
   }

   private float[] func_178403_a(Vector3f p_178403_1_, Vector3f p_178403_2_) {
      float[] lvt_3_1_ = new float[EnumFacing.values().length];
      lvt_3_1_[EnumFaceDirection.Constants.field_179176_f] = p_178403_1_.x / 16.0F;
      lvt_3_1_[EnumFaceDirection.Constants.field_179178_e] = p_178403_1_.y / 16.0F;
      lvt_3_1_[EnumFaceDirection.Constants.field_179177_d] = p_178403_1_.z / 16.0F;
      lvt_3_1_[EnumFaceDirection.Constants.field_179180_c] = p_178403_2_.x / 16.0F;
      lvt_3_1_[EnumFaceDirection.Constants.field_179179_b] = p_178403_2_.y / 16.0F;
      lvt_3_1_[EnumFaceDirection.Constants.field_179181_a] = p_178403_2_.z / 16.0F;
      return lvt_3_1_;
   }

   private void func_178402_a(int[] p_178402_1_, int p_178402_2_, EnumFacing p_178402_3_, BlockPartFace p_178402_4_, float[] p_178402_5_, TextureAtlasSprite p_178402_6_, ModelRotation p_178402_7_, BlockPartRotation p_178402_8_, boolean p_178402_9_, boolean p_178402_10_) {
      EnumFacing lvt_11_1_ = p_178402_7_.func_177523_a(p_178402_3_);
      int lvt_12_1_ = p_178402_10_?this.func_178413_a(lvt_11_1_):-1;
      EnumFaceDirection.VertexInformation lvt_13_1_ = EnumFaceDirection.func_179027_a(p_178402_3_).func_179025_a(p_178402_2_);
      Vector3f lvt_14_1_ = new Vector3f(p_178402_5_[lvt_13_1_.field_179184_a], p_178402_5_[lvt_13_1_.field_179182_b], p_178402_5_[lvt_13_1_.field_179183_c]);
      this.func_178407_a(lvt_14_1_, p_178402_8_);
      int lvt_15_1_ = this.func_178415_a(lvt_14_1_, p_178402_3_, p_178402_2_, p_178402_7_, p_178402_9_);
      this.func_178404_a(p_178402_1_, lvt_15_1_, p_178402_2_, lvt_14_1_, lvt_12_1_, p_178402_6_, p_178402_4_.field_178243_e);
   }

   private void func_178404_a(int[] p_178404_1_, int p_178404_2_, int p_178404_3_, Vector3f p_178404_4_, int p_178404_5_, TextureAtlasSprite p_178404_6_, BlockFaceUV p_178404_7_) {
      int lvt_8_1_ = p_178404_2_ * 7;
      p_178404_1_[lvt_8_1_] = Float.floatToRawIntBits(p_178404_4_.x);
      p_178404_1_[lvt_8_1_ + 1] = Float.floatToRawIntBits(p_178404_4_.y);
      p_178404_1_[lvt_8_1_ + 2] = Float.floatToRawIntBits(p_178404_4_.z);
      p_178404_1_[lvt_8_1_ + 3] = p_178404_5_;
      p_178404_1_[lvt_8_1_ + 4] = Float.floatToRawIntBits(p_178404_6_.func_94214_a((double)p_178404_7_.func_178348_a(p_178404_3_)));
      p_178404_1_[lvt_8_1_ + 4 + 1] = Float.floatToRawIntBits(p_178404_6_.func_94207_b((double)p_178404_7_.func_178346_b(p_178404_3_)));
   }

   private void func_178407_a(Vector3f p_178407_1_, BlockPartRotation p_178407_2_) {
      if(p_178407_2_ != null) {
         Matrix4f lvt_3_1_ = this.func_178411_a();
         Vector3f lvt_4_1_ = new Vector3f(0.0F, 0.0F, 0.0F);
         switch(p_178407_2_.field_178342_b) {
         case X:
            Matrix4f.rotate(p_178407_2_.field_178343_c * 0.017453292F, new Vector3f(1.0F, 0.0F, 0.0F), lvt_3_1_, lvt_3_1_);
            lvt_4_1_.set(0.0F, 1.0F, 1.0F);
            break;
         case Y:
            Matrix4f.rotate(p_178407_2_.field_178343_c * 0.017453292F, new Vector3f(0.0F, 1.0F, 0.0F), lvt_3_1_, lvt_3_1_);
            lvt_4_1_.set(1.0F, 0.0F, 1.0F);
            break;
         case Z:
            Matrix4f.rotate(p_178407_2_.field_178343_c * 0.017453292F, new Vector3f(0.0F, 0.0F, 1.0F), lvt_3_1_, lvt_3_1_);
            lvt_4_1_.set(1.0F, 1.0F, 0.0F);
         }

         if(p_178407_2_.field_178341_d) {
            if(Math.abs(p_178407_2_.field_178343_c) == 22.5F) {
               lvt_4_1_.scale(field_178418_a);
            } else {
               lvt_4_1_.scale(field_178417_b);
            }

            Vector3f.add(lvt_4_1_, new Vector3f(1.0F, 1.0F, 1.0F), lvt_4_1_);
         } else {
            lvt_4_1_.set(1.0F, 1.0F, 1.0F);
         }

         this.func_178406_a(p_178407_1_, new Vector3f(p_178407_2_.field_178344_a), lvt_3_1_, lvt_4_1_);
      }
   }

   public int func_178415_a(Vector3f p_178415_1_, EnumFacing p_178415_2_, int p_178415_3_, ModelRotation p_178415_4_, boolean p_178415_5_) {
      if(p_178415_4_ == ModelRotation.X0_Y0) {
         return p_178415_3_;
      } else {
         this.func_178406_a(p_178415_1_, new Vector3f(0.5F, 0.5F, 0.5F), p_178415_4_.func_177525_a(), new Vector3f(1.0F, 1.0F, 1.0F));
         return p_178415_4_.func_177520_a(p_178415_2_, p_178415_3_);
      }
   }

   private void func_178406_a(Vector3f p_178406_1_, Vector3f p_178406_2_, Matrix4f p_178406_3_, Vector3f p_178406_4_) {
      Vector4f lvt_5_1_ = new Vector4f(p_178406_1_.x - p_178406_2_.x, p_178406_1_.y - p_178406_2_.y, p_178406_1_.z - p_178406_2_.z, 1.0F);
      Matrix4f.transform(p_178406_3_, lvt_5_1_, lvt_5_1_);
      lvt_5_1_.x *= p_178406_4_.x;
      lvt_5_1_.y *= p_178406_4_.y;
      lvt_5_1_.z *= p_178406_4_.z;
      p_178406_1_.set(lvt_5_1_.x + p_178406_2_.x, lvt_5_1_.y + p_178406_2_.y, lvt_5_1_.z + p_178406_2_.z);
   }

   private Matrix4f func_178411_a() {
      Matrix4f lvt_1_1_ = new Matrix4f();
      lvt_1_1_.setIdentity();
      return lvt_1_1_;
   }

   public static EnumFacing func_178410_a(int[] p_178410_0_) {
      Vector3f lvt_1_1_ = new Vector3f(Float.intBitsToFloat(p_178410_0_[0]), Float.intBitsToFloat(p_178410_0_[1]), Float.intBitsToFloat(p_178410_0_[2]));
      Vector3f lvt_2_1_ = new Vector3f(Float.intBitsToFloat(p_178410_0_[7]), Float.intBitsToFloat(p_178410_0_[8]), Float.intBitsToFloat(p_178410_0_[9]));
      Vector3f lvt_3_1_ = new Vector3f(Float.intBitsToFloat(p_178410_0_[14]), Float.intBitsToFloat(p_178410_0_[15]), Float.intBitsToFloat(p_178410_0_[16]));
      Vector3f lvt_4_1_ = new Vector3f();
      Vector3f lvt_5_1_ = new Vector3f();
      Vector3f lvt_6_1_ = new Vector3f();
      Vector3f.sub(lvt_1_1_, lvt_2_1_, lvt_4_1_);
      Vector3f.sub(lvt_3_1_, lvt_2_1_, lvt_5_1_);
      Vector3f.cross(lvt_5_1_, lvt_4_1_, lvt_6_1_);
      float lvt_7_1_ = (float)Math.sqrt((double)(lvt_6_1_.x * lvt_6_1_.x + lvt_6_1_.y * lvt_6_1_.y + lvt_6_1_.z * lvt_6_1_.z));
      lvt_6_1_.x /= lvt_7_1_;
      lvt_6_1_.y /= lvt_7_1_;
      lvt_6_1_.z /= lvt_7_1_;
      EnumFacing lvt_8_1_ = null;
      float lvt_9_1_ = 0.0F;

      for(EnumFacing lvt_13_1_ : EnumFacing.values()) {
         Vec3i lvt_14_1_ = lvt_13_1_.func_176730_m();
         Vector3f lvt_15_1_ = new Vector3f((float)lvt_14_1_.func_177958_n(), (float)lvt_14_1_.func_177956_o(), (float)lvt_14_1_.func_177952_p());
         float lvt_16_1_ = Vector3f.dot(lvt_6_1_, lvt_15_1_);
         if(lvt_16_1_ >= 0.0F && lvt_16_1_ > lvt_9_1_) {
            lvt_9_1_ = lvt_16_1_;
            lvt_8_1_ = lvt_13_1_;
         }
      }

      if(lvt_8_1_ == null) {
         return EnumFacing.UP;
      } else {
         return lvt_8_1_;
      }
   }

   public void func_178409_a(int[] p_178409_1_, EnumFacing p_178409_2_, BlockFaceUV p_178409_3_, TextureAtlasSprite p_178409_4_) {
      for(int lvt_5_1_ = 0; lvt_5_1_ < 4; ++lvt_5_1_) {
         this.func_178401_a(lvt_5_1_, p_178409_1_, p_178409_2_, p_178409_3_, p_178409_4_);
      }

   }

   private void func_178408_a(int[] p_178408_1_, EnumFacing p_178408_2_) {
      int[] lvt_3_1_ = new int[p_178408_1_.length];
      System.arraycopy(p_178408_1_, 0, lvt_3_1_, 0, p_178408_1_.length);
      float[] lvt_4_1_ = new float[EnumFacing.values().length];
      lvt_4_1_[EnumFaceDirection.Constants.field_179176_f] = 999.0F;
      lvt_4_1_[EnumFaceDirection.Constants.field_179178_e] = 999.0F;
      lvt_4_1_[EnumFaceDirection.Constants.field_179177_d] = 999.0F;
      lvt_4_1_[EnumFaceDirection.Constants.field_179180_c] = -999.0F;
      lvt_4_1_[EnumFaceDirection.Constants.field_179179_b] = -999.0F;
      lvt_4_1_[EnumFaceDirection.Constants.field_179181_a] = -999.0F;

      for(int lvt_5_1_ = 0; lvt_5_1_ < 4; ++lvt_5_1_) {
         int lvt_6_1_ = 7 * lvt_5_1_;
         float lvt_7_1_ = Float.intBitsToFloat(lvt_3_1_[lvt_6_1_]);
         float lvt_8_1_ = Float.intBitsToFloat(lvt_3_1_[lvt_6_1_ + 1]);
         float lvt_9_1_ = Float.intBitsToFloat(lvt_3_1_[lvt_6_1_ + 2]);
         if(lvt_7_1_ < lvt_4_1_[EnumFaceDirection.Constants.field_179176_f]) {
            lvt_4_1_[EnumFaceDirection.Constants.field_179176_f] = lvt_7_1_;
         }

         if(lvt_8_1_ < lvt_4_1_[EnumFaceDirection.Constants.field_179178_e]) {
            lvt_4_1_[EnumFaceDirection.Constants.field_179178_e] = lvt_8_1_;
         }

         if(lvt_9_1_ < lvt_4_1_[EnumFaceDirection.Constants.field_179177_d]) {
            lvt_4_1_[EnumFaceDirection.Constants.field_179177_d] = lvt_9_1_;
         }

         if(lvt_7_1_ > lvt_4_1_[EnumFaceDirection.Constants.field_179180_c]) {
            lvt_4_1_[EnumFaceDirection.Constants.field_179180_c] = lvt_7_1_;
         }

         if(lvt_8_1_ > lvt_4_1_[EnumFaceDirection.Constants.field_179179_b]) {
            lvt_4_1_[EnumFaceDirection.Constants.field_179179_b] = lvt_8_1_;
         }

         if(lvt_9_1_ > lvt_4_1_[EnumFaceDirection.Constants.field_179181_a]) {
            lvt_4_1_[EnumFaceDirection.Constants.field_179181_a] = lvt_9_1_;
         }
      }

      EnumFaceDirection lvt_5_2_ = EnumFaceDirection.func_179027_a(p_178408_2_);

      for(int lvt_6_2_ = 0; lvt_6_2_ < 4; ++lvt_6_2_) {
         int lvt_7_2_ = 7 * lvt_6_2_;
         EnumFaceDirection.VertexInformation lvt_8_2_ = lvt_5_2_.func_179025_a(lvt_6_2_);
         float lvt_9_2_ = lvt_4_1_[lvt_8_2_.field_179184_a];
         float lvt_10_1_ = lvt_4_1_[lvt_8_2_.field_179182_b];
         float lvt_11_1_ = lvt_4_1_[lvt_8_2_.field_179183_c];
         p_178408_1_[lvt_7_2_] = Float.floatToRawIntBits(lvt_9_2_);
         p_178408_1_[lvt_7_2_ + 1] = Float.floatToRawIntBits(lvt_10_1_);
         p_178408_1_[lvt_7_2_ + 2] = Float.floatToRawIntBits(lvt_11_1_);

         for(int lvt_12_1_ = 0; lvt_12_1_ < 4; ++lvt_12_1_) {
            int lvt_13_1_ = 7 * lvt_12_1_;
            float lvt_14_1_ = Float.intBitsToFloat(lvt_3_1_[lvt_13_1_]);
            float lvt_15_1_ = Float.intBitsToFloat(lvt_3_1_[lvt_13_1_ + 1]);
            float lvt_16_1_ = Float.intBitsToFloat(lvt_3_1_[lvt_13_1_ + 2]);
            if(MathHelper.func_180185_a(lvt_9_2_, lvt_14_1_) && MathHelper.func_180185_a(lvt_10_1_, lvt_15_1_) && MathHelper.func_180185_a(lvt_11_1_, lvt_16_1_)) {
               p_178408_1_[lvt_7_2_ + 4] = lvt_3_1_[lvt_13_1_ + 4];
               p_178408_1_[lvt_7_2_ + 4 + 1] = lvt_3_1_[lvt_13_1_ + 4 + 1];
            }
         }
      }

   }

   private void func_178401_a(int p_178401_1_, int[] p_178401_2_, EnumFacing p_178401_3_, BlockFaceUV p_178401_4_, TextureAtlasSprite p_178401_5_) {
      int lvt_6_1_ = 7 * p_178401_1_;
      float lvt_7_1_ = Float.intBitsToFloat(p_178401_2_[lvt_6_1_]);
      float lvt_8_1_ = Float.intBitsToFloat(p_178401_2_[lvt_6_1_ + 1]);
      float lvt_9_1_ = Float.intBitsToFloat(p_178401_2_[lvt_6_1_ + 2]);
      if(lvt_7_1_ < -0.1F || lvt_7_1_ >= 1.1F) {
         lvt_7_1_ -= (float)MathHelper.func_76141_d(lvt_7_1_);
      }

      if(lvt_8_1_ < -0.1F || lvt_8_1_ >= 1.1F) {
         lvt_8_1_ -= (float)MathHelper.func_76141_d(lvt_8_1_);
      }

      if(lvt_9_1_ < -0.1F || lvt_9_1_ >= 1.1F) {
         lvt_9_1_ -= (float)MathHelper.func_76141_d(lvt_9_1_);
      }

      float lvt_10_1_ = 0.0F;
      float lvt_11_1_ = 0.0F;
      switch(p_178401_3_) {
      case DOWN:
         lvt_10_1_ = lvt_7_1_ * 16.0F;
         lvt_11_1_ = (1.0F - lvt_9_1_) * 16.0F;
         break;
      case UP:
         lvt_10_1_ = lvt_7_1_ * 16.0F;
         lvt_11_1_ = lvt_9_1_ * 16.0F;
         break;
      case NORTH:
         lvt_10_1_ = (1.0F - lvt_7_1_) * 16.0F;
         lvt_11_1_ = (1.0F - lvt_8_1_) * 16.0F;
         break;
      case SOUTH:
         lvt_10_1_ = lvt_7_1_ * 16.0F;
         lvt_11_1_ = (1.0F - lvt_8_1_) * 16.0F;
         break;
      case WEST:
         lvt_10_1_ = lvt_9_1_ * 16.0F;
         lvt_11_1_ = (1.0F - lvt_8_1_) * 16.0F;
         break;
      case EAST:
         lvt_10_1_ = (1.0F - lvt_9_1_) * 16.0F;
         lvt_11_1_ = (1.0F - lvt_8_1_) * 16.0F;
      }

      int lvt_12_1_ = p_178401_4_.func_178345_c(p_178401_1_) * 7;
      p_178401_2_[lvt_12_1_ + 4] = Float.floatToRawIntBits(p_178401_5_.func_94214_a((double)lvt_10_1_));
      p_178401_2_[lvt_12_1_ + 4 + 1] = Float.floatToRawIntBits(p_178401_5_.func_94207_b((double)lvt_11_1_));
   }
}
