package net.minecraft.client.renderer;

import java.util.BitSet;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3i;
import net.minecraft.world.IBlockAccess;

public class BlockModelRenderer {
   public boolean func_178259_a(IBlockAccess p_178259_1_, IBakedModel p_178259_2_, IBlockState p_178259_3_, BlockPos p_178259_4_, WorldRenderer p_178259_5_) {
      Block lvt_6_1_ = p_178259_3_.func_177230_c();
      lvt_6_1_.func_180654_a(p_178259_1_, p_178259_4_);
      return this.func_178267_a(p_178259_1_, p_178259_2_, p_178259_3_, p_178259_4_, p_178259_5_, true);
   }

   public boolean func_178267_a(IBlockAccess p_178267_1_, IBakedModel p_178267_2_, IBlockState p_178267_3_, BlockPos p_178267_4_, WorldRenderer p_178267_5_, boolean p_178267_6_) {
      boolean lvt_7_1_ = Minecraft.func_71379_u() && p_178267_3_.func_177230_c().func_149750_m() == 0 && p_178267_2_.func_177555_b();

      try {
         Block lvt_8_1_ = p_178267_3_.func_177230_c();
         return lvt_7_1_?this.func_178265_a(p_178267_1_, p_178267_2_, lvt_8_1_, p_178267_4_, p_178267_5_, p_178267_6_):this.func_178258_b(p_178267_1_, p_178267_2_, lvt_8_1_, p_178267_4_, p_178267_5_, p_178267_6_);
      } catch (Throwable var11) {
         CrashReport lvt_9_1_ = CrashReport.func_85055_a(var11, "Tesselating block model");
         CrashReportCategory lvt_10_1_ = lvt_9_1_.func_85058_a("Block model being tesselated");
         CrashReportCategory.func_175750_a(lvt_10_1_, p_178267_4_, p_178267_3_);
         lvt_10_1_.func_71507_a("Using AO", Boolean.valueOf(lvt_7_1_));
         throw new ReportedException(lvt_9_1_);
      }
   }

   public boolean func_178265_a(IBlockAccess p_178265_1_, IBakedModel p_178265_2_, Block p_178265_3_, BlockPos p_178265_4_, WorldRenderer p_178265_5_, boolean p_178265_6_) {
      boolean lvt_7_1_ = false;
      float[] lvt_8_1_ = new float[EnumFacing.values().length * 2];
      BitSet lvt_9_1_ = new BitSet(3);
      BlockModelRenderer.AmbientOcclusionFace lvt_10_1_ = new BlockModelRenderer.AmbientOcclusionFace();

      for(EnumFacing lvt_14_1_ : EnumFacing.values()) {
         List<BakedQuad> lvt_15_1_ = p_178265_2_.func_177551_a(lvt_14_1_);
         if(!lvt_15_1_.isEmpty()) {
            BlockPos lvt_16_1_ = p_178265_4_.func_177972_a(lvt_14_1_);
            if(!p_178265_6_ || p_178265_3_.func_176225_a(p_178265_1_, lvt_16_1_, lvt_14_1_)) {
               this.func_178263_a(p_178265_1_, p_178265_3_, p_178265_4_, p_178265_5_, lvt_15_1_, lvt_8_1_, lvt_9_1_, lvt_10_1_);
               lvt_7_1_ = true;
            }
         }
      }

      List<BakedQuad> lvt_11_2_ = p_178265_2_.func_177550_a();
      if(lvt_11_2_.size() > 0) {
         this.func_178263_a(p_178265_1_, p_178265_3_, p_178265_4_, p_178265_5_, lvt_11_2_, lvt_8_1_, lvt_9_1_, lvt_10_1_);
         lvt_7_1_ = true;
      }

      return lvt_7_1_;
   }

   public boolean func_178258_b(IBlockAccess p_178258_1_, IBakedModel p_178258_2_, Block p_178258_3_, BlockPos p_178258_4_, WorldRenderer p_178258_5_, boolean p_178258_6_) {
      boolean lvt_7_1_ = false;
      BitSet lvt_8_1_ = new BitSet(3);

      for(EnumFacing lvt_12_1_ : EnumFacing.values()) {
         List<BakedQuad> lvt_13_1_ = p_178258_2_.func_177551_a(lvt_12_1_);
         if(!lvt_13_1_.isEmpty()) {
            BlockPos lvt_14_1_ = p_178258_4_.func_177972_a(lvt_12_1_);
            if(!p_178258_6_ || p_178258_3_.func_176225_a(p_178258_1_, lvt_14_1_, lvt_12_1_)) {
               int lvt_15_1_ = p_178258_3_.func_176207_c(p_178258_1_, lvt_14_1_);
               this.func_178260_a(p_178258_1_, p_178258_3_, p_178258_4_, lvt_12_1_, lvt_15_1_, false, p_178258_5_, lvt_13_1_, lvt_8_1_);
               lvt_7_1_ = true;
            }
         }
      }

      List<BakedQuad> lvt_9_2_ = p_178258_2_.func_177550_a();
      if(lvt_9_2_.size() > 0) {
         this.func_178260_a(p_178258_1_, p_178258_3_, p_178258_4_, (EnumFacing)null, -1, true, p_178258_5_, lvt_9_2_, lvt_8_1_);
         lvt_7_1_ = true;
      }

      return lvt_7_1_;
   }

   private void func_178263_a(IBlockAccess p_178263_1_, Block p_178263_2_, BlockPos p_178263_3_, WorldRenderer p_178263_4_, List<BakedQuad> p_178263_5_, float[] p_178263_6_, BitSet p_178263_7_, BlockModelRenderer.AmbientOcclusionFace p_178263_8_) {
      double lvt_9_1_ = (double)p_178263_3_.func_177958_n();
      double lvt_11_1_ = (double)p_178263_3_.func_177956_o();
      double lvt_13_1_ = (double)p_178263_3_.func_177952_p();
      Block.EnumOffsetType lvt_15_1_ = p_178263_2_.func_176218_Q();
      if(lvt_15_1_ != Block.EnumOffsetType.NONE) {
         long lvt_16_1_ = MathHelper.func_180186_a(p_178263_3_);
         lvt_9_1_ += ((double)((float)(lvt_16_1_ >> 16 & 15L) / 15.0F) - 0.5D) * 0.5D;
         lvt_13_1_ += ((double)((float)(lvt_16_1_ >> 24 & 15L) / 15.0F) - 0.5D) * 0.5D;
         if(lvt_15_1_ == Block.EnumOffsetType.XYZ) {
            lvt_11_1_ += ((double)((float)(lvt_16_1_ >> 20 & 15L) / 15.0F) - 1.0D) * 0.2D;
         }
      }

      for(BakedQuad lvt_17_1_ : p_178263_5_) {
         this.func_178261_a(p_178263_2_, lvt_17_1_.func_178209_a(), lvt_17_1_.func_178210_d(), p_178263_6_, p_178263_7_);
         p_178263_8_.func_178204_a(p_178263_1_, p_178263_2_, p_178263_3_, lvt_17_1_.func_178210_d(), p_178263_6_, p_178263_7_);
         p_178263_4_.func_178981_a(lvt_17_1_.func_178209_a());
         p_178263_4_.func_178962_a(p_178263_8_.field_178207_c[0], p_178263_8_.field_178207_c[1], p_178263_8_.field_178207_c[2], p_178263_8_.field_178207_c[3]);
         if(lvt_17_1_.func_178212_b()) {
            int lvt_18_1_ = p_178263_2_.func_180662_a(p_178263_1_, p_178263_3_, lvt_17_1_.func_178211_c());
            if(EntityRenderer.field_78517_a) {
               lvt_18_1_ = TextureUtil.func_177054_c(lvt_18_1_);
            }

            float lvt_19_1_ = (float)(lvt_18_1_ >> 16 & 255) / 255.0F;
            float lvt_20_1_ = (float)(lvt_18_1_ >> 8 & 255) / 255.0F;
            float lvt_21_1_ = (float)(lvt_18_1_ & 255) / 255.0F;
            p_178263_4_.func_178978_a(p_178263_8_.field_178206_b[0] * lvt_19_1_, p_178263_8_.field_178206_b[0] * lvt_20_1_, p_178263_8_.field_178206_b[0] * lvt_21_1_, 4);
            p_178263_4_.func_178978_a(p_178263_8_.field_178206_b[1] * lvt_19_1_, p_178263_8_.field_178206_b[1] * lvt_20_1_, p_178263_8_.field_178206_b[1] * lvt_21_1_, 3);
            p_178263_4_.func_178978_a(p_178263_8_.field_178206_b[2] * lvt_19_1_, p_178263_8_.field_178206_b[2] * lvt_20_1_, p_178263_8_.field_178206_b[2] * lvt_21_1_, 2);
            p_178263_4_.func_178978_a(p_178263_8_.field_178206_b[3] * lvt_19_1_, p_178263_8_.field_178206_b[3] * lvt_20_1_, p_178263_8_.field_178206_b[3] * lvt_21_1_, 1);
         } else {
            p_178263_4_.func_178978_a(p_178263_8_.field_178206_b[0], p_178263_8_.field_178206_b[0], p_178263_8_.field_178206_b[0], 4);
            p_178263_4_.func_178978_a(p_178263_8_.field_178206_b[1], p_178263_8_.field_178206_b[1], p_178263_8_.field_178206_b[1], 3);
            p_178263_4_.func_178978_a(p_178263_8_.field_178206_b[2], p_178263_8_.field_178206_b[2], p_178263_8_.field_178206_b[2], 2);
            p_178263_4_.func_178978_a(p_178263_8_.field_178206_b[3], p_178263_8_.field_178206_b[3], p_178263_8_.field_178206_b[3], 1);
         }

         p_178263_4_.func_178987_a(lvt_9_1_, lvt_11_1_, lvt_13_1_);
      }

   }

   private void func_178261_a(Block p_178261_1_, int[] p_178261_2_, EnumFacing p_178261_3_, float[] p_178261_4_, BitSet p_178261_5_) {
      float lvt_6_1_ = 32.0F;
      float lvt_7_1_ = 32.0F;
      float lvt_8_1_ = 32.0F;
      float lvt_9_1_ = -32.0F;
      float lvt_10_1_ = -32.0F;
      float lvt_11_1_ = -32.0F;

      for(int lvt_12_1_ = 0; lvt_12_1_ < 4; ++lvt_12_1_) {
         float lvt_13_1_ = Float.intBitsToFloat(p_178261_2_[lvt_12_1_ * 7]);
         float lvt_14_1_ = Float.intBitsToFloat(p_178261_2_[lvt_12_1_ * 7 + 1]);
         float lvt_15_1_ = Float.intBitsToFloat(p_178261_2_[lvt_12_1_ * 7 + 2]);
         lvt_6_1_ = Math.min(lvt_6_1_, lvt_13_1_);
         lvt_7_1_ = Math.min(lvt_7_1_, lvt_14_1_);
         lvt_8_1_ = Math.min(lvt_8_1_, lvt_15_1_);
         lvt_9_1_ = Math.max(lvt_9_1_, lvt_13_1_);
         lvt_10_1_ = Math.max(lvt_10_1_, lvt_14_1_);
         lvt_11_1_ = Math.max(lvt_11_1_, lvt_15_1_);
      }

      if(p_178261_4_ != null) {
         p_178261_4_[EnumFacing.WEST.func_176745_a()] = lvt_6_1_;
         p_178261_4_[EnumFacing.EAST.func_176745_a()] = lvt_9_1_;
         p_178261_4_[EnumFacing.DOWN.func_176745_a()] = lvt_7_1_;
         p_178261_4_[EnumFacing.UP.func_176745_a()] = lvt_10_1_;
         p_178261_4_[EnumFacing.NORTH.func_176745_a()] = lvt_8_1_;
         p_178261_4_[EnumFacing.SOUTH.func_176745_a()] = lvt_11_1_;
         p_178261_4_[EnumFacing.WEST.func_176745_a() + EnumFacing.values().length] = 1.0F - lvt_6_1_;
         p_178261_4_[EnumFacing.EAST.func_176745_a() + EnumFacing.values().length] = 1.0F - lvt_9_1_;
         p_178261_4_[EnumFacing.DOWN.func_176745_a() + EnumFacing.values().length] = 1.0F - lvt_7_1_;
         p_178261_4_[EnumFacing.UP.func_176745_a() + EnumFacing.values().length] = 1.0F - lvt_10_1_;
         p_178261_4_[EnumFacing.NORTH.func_176745_a() + EnumFacing.values().length] = 1.0F - lvt_8_1_;
         p_178261_4_[EnumFacing.SOUTH.func_176745_a() + EnumFacing.values().length] = 1.0F - lvt_11_1_;
      }

      float lvt_12_2_ = 1.0E-4F;
      float lvt_13_2_ = 0.9999F;
      switch(p_178261_3_) {
      case DOWN:
         p_178261_5_.set(1, lvt_6_1_ >= 1.0E-4F || lvt_8_1_ >= 1.0E-4F || lvt_9_1_ <= 0.9999F || lvt_11_1_ <= 0.9999F);
         p_178261_5_.set(0, (lvt_7_1_ < 1.0E-4F || p_178261_1_.func_149686_d()) && lvt_7_1_ == lvt_10_1_);
         break;
      case UP:
         p_178261_5_.set(1, lvt_6_1_ >= 1.0E-4F || lvt_8_1_ >= 1.0E-4F || lvt_9_1_ <= 0.9999F || lvt_11_1_ <= 0.9999F);
         p_178261_5_.set(0, (lvt_10_1_ > 0.9999F || p_178261_1_.func_149686_d()) && lvt_7_1_ == lvt_10_1_);
         break;
      case NORTH:
         p_178261_5_.set(1, lvt_6_1_ >= 1.0E-4F || lvt_7_1_ >= 1.0E-4F || lvt_9_1_ <= 0.9999F || lvt_10_1_ <= 0.9999F);
         p_178261_5_.set(0, (lvt_8_1_ < 1.0E-4F || p_178261_1_.func_149686_d()) && lvt_8_1_ == lvt_11_1_);
         break;
      case SOUTH:
         p_178261_5_.set(1, lvt_6_1_ >= 1.0E-4F || lvt_7_1_ >= 1.0E-4F || lvt_9_1_ <= 0.9999F || lvt_10_1_ <= 0.9999F);
         p_178261_5_.set(0, (lvt_11_1_ > 0.9999F || p_178261_1_.func_149686_d()) && lvt_8_1_ == lvt_11_1_);
         break;
      case WEST:
         p_178261_5_.set(1, lvt_7_1_ >= 1.0E-4F || lvt_8_1_ >= 1.0E-4F || lvt_10_1_ <= 0.9999F || lvt_11_1_ <= 0.9999F);
         p_178261_5_.set(0, (lvt_6_1_ < 1.0E-4F || p_178261_1_.func_149686_d()) && lvt_6_1_ == lvt_9_1_);
         break;
      case EAST:
         p_178261_5_.set(1, lvt_7_1_ >= 1.0E-4F || lvt_8_1_ >= 1.0E-4F || lvt_10_1_ <= 0.9999F || lvt_11_1_ <= 0.9999F);
         p_178261_5_.set(0, (lvt_9_1_ > 0.9999F || p_178261_1_.func_149686_d()) && lvt_6_1_ == lvt_9_1_);
      }

   }

   private void func_178260_a(IBlockAccess p_178260_1_, Block p_178260_2_, BlockPos p_178260_3_, EnumFacing p_178260_4_, int p_178260_5_, boolean p_178260_6_, WorldRenderer p_178260_7_, List<BakedQuad> p_178260_8_, BitSet p_178260_9_) {
      double lvt_10_1_ = (double)p_178260_3_.func_177958_n();
      double lvt_12_1_ = (double)p_178260_3_.func_177956_o();
      double lvt_14_1_ = (double)p_178260_3_.func_177952_p();
      Block.EnumOffsetType lvt_16_1_ = p_178260_2_.func_176218_Q();
      if(lvt_16_1_ != Block.EnumOffsetType.NONE) {
         int lvt_17_1_ = p_178260_3_.func_177958_n();
         int lvt_18_1_ = p_178260_3_.func_177952_p();
         long lvt_19_1_ = (long)(lvt_17_1_ * 3129871) ^ (long)lvt_18_1_ * 116129781L;
         lvt_19_1_ = lvt_19_1_ * lvt_19_1_ * 42317861L + lvt_19_1_ * 11L;
         lvt_10_1_ += ((double)((float)(lvt_19_1_ >> 16 & 15L) / 15.0F) - 0.5D) * 0.5D;
         lvt_14_1_ += ((double)((float)(lvt_19_1_ >> 24 & 15L) / 15.0F) - 0.5D) * 0.5D;
         if(lvt_16_1_ == Block.EnumOffsetType.XYZ) {
            lvt_12_1_ += ((double)((float)(lvt_19_1_ >> 20 & 15L) / 15.0F) - 1.0D) * 0.2D;
         }
      }

      for(BakedQuad lvt_18_2_ : p_178260_8_) {
         if(p_178260_6_) {
            this.func_178261_a(p_178260_2_, lvt_18_2_.func_178209_a(), lvt_18_2_.func_178210_d(), (float[])null, p_178260_9_);
            p_178260_5_ = p_178260_9_.get(0)?p_178260_2_.func_176207_c(p_178260_1_, p_178260_3_.func_177972_a(lvt_18_2_.func_178210_d())):p_178260_2_.func_176207_c(p_178260_1_, p_178260_3_);
         }

         p_178260_7_.func_178981_a(lvt_18_2_.func_178209_a());
         p_178260_7_.func_178962_a(p_178260_5_, p_178260_5_, p_178260_5_, p_178260_5_);
         if(lvt_18_2_.func_178212_b()) {
            int lvt_19_2_ = p_178260_2_.func_180662_a(p_178260_1_, p_178260_3_, lvt_18_2_.func_178211_c());
            if(EntityRenderer.field_78517_a) {
               lvt_19_2_ = TextureUtil.func_177054_c(lvt_19_2_);
            }

            float lvt_20_1_ = (float)(lvt_19_2_ >> 16 & 255) / 255.0F;
            float lvt_21_1_ = (float)(lvt_19_2_ >> 8 & 255) / 255.0F;
            float lvt_22_1_ = (float)(lvt_19_2_ & 255) / 255.0F;
            p_178260_7_.func_178978_a(lvt_20_1_, lvt_21_1_, lvt_22_1_, 4);
            p_178260_7_.func_178978_a(lvt_20_1_, lvt_21_1_, lvt_22_1_, 3);
            p_178260_7_.func_178978_a(lvt_20_1_, lvt_21_1_, lvt_22_1_, 2);
            p_178260_7_.func_178978_a(lvt_20_1_, lvt_21_1_, lvt_22_1_, 1);
         }

         p_178260_7_.func_178987_a(lvt_10_1_, lvt_12_1_, lvt_14_1_);
      }

   }

   public void func_178262_a(IBakedModel p_178262_1_, float p_178262_2_, float p_178262_3_, float p_178262_4_, float p_178262_5_) {
      for(EnumFacing lvt_9_1_ : EnumFacing.values()) {
         this.func_178264_a(p_178262_2_, p_178262_3_, p_178262_4_, p_178262_5_, p_178262_1_.func_177551_a(lvt_9_1_));
      }

      this.func_178264_a(p_178262_2_, p_178262_3_, p_178262_4_, p_178262_5_, p_178262_1_.func_177550_a());
   }

   public void func_178266_a(IBakedModel p_178266_1_, IBlockState p_178266_2_, float p_178266_3_, boolean p_178266_4_) {
      Block lvt_5_1_ = p_178266_2_.func_177230_c();
      lvt_5_1_.func_149683_g();
      GlStateManager.func_179114_b(90.0F, 0.0F, 1.0F, 0.0F);
      int lvt_6_1_ = lvt_5_1_.func_180644_h(lvt_5_1_.func_176217_b(p_178266_2_));
      if(EntityRenderer.field_78517_a) {
         lvt_6_1_ = TextureUtil.func_177054_c(lvt_6_1_);
      }

      float lvt_7_1_ = (float)(lvt_6_1_ >> 16 & 255) / 255.0F;
      float lvt_8_1_ = (float)(lvt_6_1_ >> 8 & 255) / 255.0F;
      float lvt_9_1_ = (float)(lvt_6_1_ & 255) / 255.0F;
      if(!p_178266_4_) {
         GlStateManager.func_179131_c(p_178266_3_, p_178266_3_, p_178266_3_, 1.0F);
      }

      this.func_178262_a(p_178266_1_, p_178266_3_, lvt_7_1_, lvt_8_1_, lvt_9_1_);
   }

   private void func_178264_a(float p_178264_1_, float p_178264_2_, float p_178264_3_, float p_178264_4_, List<BakedQuad> p_178264_5_) {
      Tessellator lvt_6_1_ = Tessellator.func_178181_a();
      WorldRenderer lvt_7_1_ = lvt_6_1_.func_178180_c();

      for(BakedQuad lvt_9_1_ : p_178264_5_) {
         lvt_7_1_.func_181668_a(7, DefaultVertexFormats.field_176599_b);
         lvt_7_1_.func_178981_a(lvt_9_1_.func_178209_a());
         if(lvt_9_1_.func_178212_b()) {
            lvt_7_1_.func_178990_f(p_178264_2_ * p_178264_1_, p_178264_3_ * p_178264_1_, p_178264_4_ * p_178264_1_);
         } else {
            lvt_7_1_.func_178990_f(p_178264_1_, p_178264_1_, p_178264_1_);
         }

         Vec3i lvt_10_1_ = lvt_9_1_.func_178210_d().func_176730_m();
         lvt_7_1_.func_178975_e((float)lvt_10_1_.func_177958_n(), (float)lvt_10_1_.func_177956_o(), (float)lvt_10_1_.func_177952_p());
         lvt_6_1_.func_78381_a();
      }

   }

   class AmbientOcclusionFace {
      private final float[] field_178206_b = new float[4];
      private final int[] field_178207_c = new int[4];

      public void func_178204_a(IBlockAccess p_178204_1_, Block p_178204_2_, BlockPos p_178204_3_, EnumFacing p_178204_4_, float[] p_178204_5_, BitSet p_178204_6_) {
         BlockPos lvt_7_1_ = p_178204_6_.get(0)?p_178204_3_.func_177972_a(p_178204_4_):p_178204_3_;
         BlockModelRenderer.EnumNeighborInfo lvt_8_1_ = BlockModelRenderer.EnumNeighborInfo.func_178273_a(p_178204_4_);
         BlockPos lvt_9_1_ = lvt_7_1_.func_177972_a(lvt_8_1_.field_178276_g[0]);
         BlockPos lvt_10_1_ = lvt_7_1_.func_177972_a(lvt_8_1_.field_178276_g[1]);
         BlockPos lvt_11_1_ = lvt_7_1_.func_177972_a(lvt_8_1_.field_178276_g[2]);
         BlockPos lvt_12_1_ = lvt_7_1_.func_177972_a(lvt_8_1_.field_178276_g[3]);
         int lvt_13_1_ = p_178204_2_.func_176207_c(p_178204_1_, lvt_9_1_);
         int lvt_14_1_ = p_178204_2_.func_176207_c(p_178204_1_, lvt_10_1_);
         int lvt_15_1_ = p_178204_2_.func_176207_c(p_178204_1_, lvt_11_1_);
         int lvt_16_1_ = p_178204_2_.func_176207_c(p_178204_1_, lvt_12_1_);
         float lvt_17_1_ = p_178204_1_.func_180495_p(lvt_9_1_).func_177230_c().func_149685_I();
         float lvt_18_1_ = p_178204_1_.func_180495_p(lvt_10_1_).func_177230_c().func_149685_I();
         float lvt_19_1_ = p_178204_1_.func_180495_p(lvt_11_1_).func_177230_c().func_149685_I();
         float lvt_20_1_ = p_178204_1_.func_180495_p(lvt_12_1_).func_177230_c().func_149685_I();
         boolean lvt_21_1_ = p_178204_1_.func_180495_p(lvt_9_1_.func_177972_a(p_178204_4_)).func_177230_c().func_149751_l();
         boolean lvt_22_1_ = p_178204_1_.func_180495_p(lvt_10_1_.func_177972_a(p_178204_4_)).func_177230_c().func_149751_l();
         boolean lvt_23_1_ = p_178204_1_.func_180495_p(lvt_11_1_.func_177972_a(p_178204_4_)).func_177230_c().func_149751_l();
         boolean lvt_24_1_ = p_178204_1_.func_180495_p(lvt_12_1_.func_177972_a(p_178204_4_)).func_177230_c().func_149751_l();
         float lvt_25_2_;
         int lvt_29_2_;
         if(!lvt_23_1_ && !lvt_21_1_) {
            lvt_25_2_ = lvt_17_1_;
            lvt_29_2_ = lvt_13_1_;
         } else {
            BlockPos lvt_33_1_ = lvt_9_1_.func_177972_a(lvt_8_1_.field_178276_g[2]);
            lvt_25_2_ = p_178204_1_.func_180495_p(lvt_33_1_).func_177230_c().func_149685_I();
            lvt_29_2_ = p_178204_2_.func_176207_c(p_178204_1_, lvt_33_1_);
         }

         float lvt_26_2_;
         int lvt_30_2_;
         if(!lvt_24_1_ && !lvt_21_1_) {
            lvt_26_2_ = lvt_17_1_;
            lvt_30_2_ = lvt_13_1_;
         } else {
            BlockPos lvt_33_2_ = lvt_9_1_.func_177972_a(lvt_8_1_.field_178276_g[3]);
            lvt_26_2_ = p_178204_1_.func_180495_p(lvt_33_2_).func_177230_c().func_149685_I();
            lvt_30_2_ = p_178204_2_.func_176207_c(p_178204_1_, lvt_33_2_);
         }

         float lvt_27_2_;
         int lvt_31_2_;
         if(!lvt_23_1_ && !lvt_22_1_) {
            lvt_27_2_ = lvt_18_1_;
            lvt_31_2_ = lvt_14_1_;
         } else {
            BlockPos lvt_33_3_ = lvt_10_1_.func_177972_a(lvt_8_1_.field_178276_g[2]);
            lvt_27_2_ = p_178204_1_.func_180495_p(lvt_33_3_).func_177230_c().func_149685_I();
            lvt_31_2_ = p_178204_2_.func_176207_c(p_178204_1_, lvt_33_3_);
         }

         float lvt_28_2_;
         int lvt_32_2_;
         if(!lvt_24_1_ && !lvt_22_1_) {
            lvt_28_2_ = lvt_18_1_;
            lvt_32_2_ = lvt_14_1_;
         } else {
            BlockPos lvt_33_4_ = lvt_10_1_.func_177972_a(lvt_8_1_.field_178276_g[3]);
            lvt_28_2_ = p_178204_1_.func_180495_p(lvt_33_4_).func_177230_c().func_149685_I();
            lvt_32_2_ = p_178204_2_.func_176207_c(p_178204_1_, lvt_33_4_);
         }

         int lvt_33_5_ = p_178204_2_.func_176207_c(p_178204_1_, p_178204_3_);
         if(p_178204_6_.get(0) || !p_178204_1_.func_180495_p(p_178204_3_.func_177972_a(p_178204_4_)).func_177230_c().func_149662_c()) {
            lvt_33_5_ = p_178204_2_.func_176207_c(p_178204_1_, p_178204_3_.func_177972_a(p_178204_4_));
         }

         float lvt_34_1_ = p_178204_6_.get(0)?p_178204_1_.func_180495_p(lvt_7_1_).func_177230_c().func_149685_I():p_178204_1_.func_180495_p(p_178204_3_).func_177230_c().func_149685_I();
         BlockModelRenderer.VertexTranslations lvt_35_1_ = BlockModelRenderer.VertexTranslations.func_178184_a(p_178204_4_);
         if(p_178204_6_.get(1) && lvt_8_1_.field_178289_i) {
            float lvt_36_2_ = (lvt_20_1_ + lvt_17_1_ + lvt_26_2_ + lvt_34_1_) * 0.25F;
            float lvt_37_2_ = (lvt_19_1_ + lvt_17_1_ + lvt_25_2_ + lvt_34_1_) * 0.25F;
            float lvt_38_2_ = (lvt_19_1_ + lvt_18_1_ + lvt_27_2_ + lvt_34_1_) * 0.25F;
            float lvt_39_2_ = (lvt_20_1_ + lvt_18_1_ + lvt_28_2_ + lvt_34_1_) * 0.25F;
            float lvt_40_1_ = p_178204_5_[lvt_8_1_.field_178286_j[0].field_178229_m] * p_178204_5_[lvt_8_1_.field_178286_j[1].field_178229_m];
            float lvt_41_1_ = p_178204_5_[lvt_8_1_.field_178286_j[2].field_178229_m] * p_178204_5_[lvt_8_1_.field_178286_j[3].field_178229_m];
            float lvt_42_1_ = p_178204_5_[lvt_8_1_.field_178286_j[4].field_178229_m] * p_178204_5_[lvt_8_1_.field_178286_j[5].field_178229_m];
            float lvt_43_1_ = p_178204_5_[lvt_8_1_.field_178286_j[6].field_178229_m] * p_178204_5_[lvt_8_1_.field_178286_j[7].field_178229_m];
            float lvt_44_1_ = p_178204_5_[lvt_8_1_.field_178287_k[0].field_178229_m] * p_178204_5_[lvt_8_1_.field_178287_k[1].field_178229_m];
            float lvt_45_1_ = p_178204_5_[lvt_8_1_.field_178287_k[2].field_178229_m] * p_178204_5_[lvt_8_1_.field_178287_k[3].field_178229_m];
            float lvt_46_1_ = p_178204_5_[lvt_8_1_.field_178287_k[4].field_178229_m] * p_178204_5_[lvt_8_1_.field_178287_k[5].field_178229_m];
            float lvt_47_1_ = p_178204_5_[lvt_8_1_.field_178287_k[6].field_178229_m] * p_178204_5_[lvt_8_1_.field_178287_k[7].field_178229_m];
            float lvt_48_1_ = p_178204_5_[lvt_8_1_.field_178284_l[0].field_178229_m] * p_178204_5_[lvt_8_1_.field_178284_l[1].field_178229_m];
            float lvt_49_1_ = p_178204_5_[lvt_8_1_.field_178284_l[2].field_178229_m] * p_178204_5_[lvt_8_1_.field_178284_l[3].field_178229_m];
            float lvt_50_1_ = p_178204_5_[lvt_8_1_.field_178284_l[4].field_178229_m] * p_178204_5_[lvt_8_1_.field_178284_l[5].field_178229_m];
            float lvt_51_1_ = p_178204_5_[lvt_8_1_.field_178284_l[6].field_178229_m] * p_178204_5_[lvt_8_1_.field_178284_l[7].field_178229_m];
            float lvt_52_1_ = p_178204_5_[lvt_8_1_.field_178285_m[0].field_178229_m] * p_178204_5_[lvt_8_1_.field_178285_m[1].field_178229_m];
            float lvt_53_1_ = p_178204_5_[lvt_8_1_.field_178285_m[2].field_178229_m] * p_178204_5_[lvt_8_1_.field_178285_m[3].field_178229_m];
            float lvt_54_1_ = p_178204_5_[lvt_8_1_.field_178285_m[4].field_178229_m] * p_178204_5_[lvt_8_1_.field_178285_m[5].field_178229_m];
            float lvt_55_1_ = p_178204_5_[lvt_8_1_.field_178285_m[6].field_178229_m] * p_178204_5_[lvt_8_1_.field_178285_m[7].field_178229_m];
            this.field_178206_b[lvt_35_1_.field_178191_g] = lvt_36_2_ * lvt_40_1_ + lvt_37_2_ * lvt_41_1_ + lvt_38_2_ * lvt_42_1_ + lvt_39_2_ * lvt_43_1_;
            this.field_178206_b[lvt_35_1_.field_178200_h] = lvt_36_2_ * lvt_44_1_ + lvt_37_2_ * lvt_45_1_ + lvt_38_2_ * lvt_46_1_ + lvt_39_2_ * lvt_47_1_;
            this.field_178206_b[lvt_35_1_.field_178201_i] = lvt_36_2_ * lvt_48_1_ + lvt_37_2_ * lvt_49_1_ + lvt_38_2_ * lvt_50_1_ + lvt_39_2_ * lvt_51_1_;
            this.field_178206_b[lvt_35_1_.field_178198_j] = lvt_36_2_ * lvt_52_1_ + lvt_37_2_ * lvt_53_1_ + lvt_38_2_ * lvt_54_1_ + lvt_39_2_ * lvt_55_1_;
            int lvt_56_1_ = this.func_147778_a(lvt_16_1_, lvt_13_1_, lvt_30_2_, lvt_33_5_);
            int lvt_57_1_ = this.func_147778_a(lvt_15_1_, lvt_13_1_, lvt_29_2_, lvt_33_5_);
            int lvt_58_1_ = this.func_147778_a(lvt_15_1_, lvt_14_1_, lvt_31_2_, lvt_33_5_);
            int lvt_59_1_ = this.func_147778_a(lvt_16_1_, lvt_14_1_, lvt_32_2_, lvt_33_5_);
            this.field_178207_c[lvt_35_1_.field_178191_g] = this.func_178203_a(lvt_56_1_, lvt_57_1_, lvt_58_1_, lvt_59_1_, lvt_40_1_, lvt_41_1_, lvt_42_1_, lvt_43_1_);
            this.field_178207_c[lvt_35_1_.field_178200_h] = this.func_178203_a(lvt_56_1_, lvt_57_1_, lvt_58_1_, lvt_59_1_, lvt_44_1_, lvt_45_1_, lvt_46_1_, lvt_47_1_);
            this.field_178207_c[lvt_35_1_.field_178201_i] = this.func_178203_a(lvt_56_1_, lvt_57_1_, lvt_58_1_, lvt_59_1_, lvt_48_1_, lvt_49_1_, lvt_50_1_, lvt_51_1_);
            this.field_178207_c[lvt_35_1_.field_178198_j] = this.func_178203_a(lvt_56_1_, lvt_57_1_, lvt_58_1_, lvt_59_1_, lvt_52_1_, lvt_53_1_, lvt_54_1_, lvt_55_1_);
         } else {
            float lvt_36_1_ = (lvt_20_1_ + lvt_17_1_ + lvt_26_2_ + lvt_34_1_) * 0.25F;
            float lvt_37_1_ = (lvt_19_1_ + lvt_17_1_ + lvt_25_2_ + lvt_34_1_) * 0.25F;
            float lvt_38_1_ = (lvt_19_1_ + lvt_18_1_ + lvt_27_2_ + lvt_34_1_) * 0.25F;
            float lvt_39_1_ = (lvt_20_1_ + lvt_18_1_ + lvt_28_2_ + lvt_34_1_) * 0.25F;
            this.field_178207_c[lvt_35_1_.field_178191_g] = this.func_147778_a(lvt_16_1_, lvt_13_1_, lvt_30_2_, lvt_33_5_);
            this.field_178207_c[lvt_35_1_.field_178200_h] = this.func_147778_a(lvt_15_1_, lvt_13_1_, lvt_29_2_, lvt_33_5_);
            this.field_178207_c[lvt_35_1_.field_178201_i] = this.func_147778_a(lvt_15_1_, lvt_14_1_, lvt_31_2_, lvt_33_5_);
            this.field_178207_c[lvt_35_1_.field_178198_j] = this.func_147778_a(lvt_16_1_, lvt_14_1_, lvt_32_2_, lvt_33_5_);
            this.field_178206_b[lvt_35_1_.field_178191_g] = lvt_36_1_;
            this.field_178206_b[lvt_35_1_.field_178200_h] = lvt_37_1_;
            this.field_178206_b[lvt_35_1_.field_178201_i] = lvt_38_1_;
            this.field_178206_b[lvt_35_1_.field_178198_j] = lvt_39_1_;
         }

      }

      private int func_147778_a(int p_147778_1_, int p_147778_2_, int p_147778_3_, int p_147778_4_) {
         if(p_147778_1_ == 0) {
            p_147778_1_ = p_147778_4_;
         }

         if(p_147778_2_ == 0) {
            p_147778_2_ = p_147778_4_;
         }

         if(p_147778_3_ == 0) {
            p_147778_3_ = p_147778_4_;
         }

         return p_147778_1_ + p_147778_2_ + p_147778_3_ + p_147778_4_ >> 2 & 16711935;
      }

      private int func_178203_a(int p_178203_1_, int p_178203_2_, int p_178203_3_, int p_178203_4_, float p_178203_5_, float p_178203_6_, float p_178203_7_, float p_178203_8_) {
         int lvt_9_1_ = (int)((float)(p_178203_1_ >> 16 & 255) * p_178203_5_ + (float)(p_178203_2_ >> 16 & 255) * p_178203_6_ + (float)(p_178203_3_ >> 16 & 255) * p_178203_7_ + (float)(p_178203_4_ >> 16 & 255) * p_178203_8_) & 255;
         int lvt_10_1_ = (int)((float)(p_178203_1_ & 255) * p_178203_5_ + (float)(p_178203_2_ & 255) * p_178203_6_ + (float)(p_178203_3_ & 255) * p_178203_7_ + (float)(p_178203_4_ & 255) * p_178203_8_) & 255;
         return lvt_9_1_ << 16 | lvt_10_1_;
      }
   }

   public static enum EnumNeighborInfo {
      DOWN(new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.5F, false, new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0]),
      UP(new EnumFacing[]{EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH}, 1.0F, false, new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0]),
      NORTH(new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.WEST}, 0.8F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_WEST}),
      SOUTH(new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.UP}, 0.8F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.EAST}),
      WEST(new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.6F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.SOUTH}),
      EAST(new EnumFacing[]{EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.6F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.SOUTH});

      protected final EnumFacing[] field_178276_g;
      protected final float field_178288_h;
      protected final boolean field_178289_i;
      protected final BlockModelRenderer.Orientation[] field_178286_j;
      protected final BlockModelRenderer.Orientation[] field_178287_k;
      protected final BlockModelRenderer.Orientation[] field_178284_l;
      protected final BlockModelRenderer.Orientation[] field_178285_m;
      private static final BlockModelRenderer.EnumNeighborInfo[] field_178282_n = new BlockModelRenderer.EnumNeighborInfo[6];

      private EnumNeighborInfo(EnumFacing[] p_i46236_3_, float p_i46236_4_, boolean p_i46236_5_, BlockModelRenderer.Orientation[] p_i46236_6_, BlockModelRenderer.Orientation[] p_i46236_7_, BlockModelRenderer.Orientation[] p_i46236_8_, BlockModelRenderer.Orientation[] p_i46236_9_) {
         this.field_178276_g = p_i46236_3_;
         this.field_178288_h = p_i46236_4_;
         this.field_178289_i = p_i46236_5_;
         this.field_178286_j = p_i46236_6_;
         this.field_178287_k = p_i46236_7_;
         this.field_178284_l = p_i46236_8_;
         this.field_178285_m = p_i46236_9_;
      }

      public static BlockModelRenderer.EnumNeighborInfo func_178273_a(EnumFacing p_178273_0_) {
         return field_178282_n[p_178273_0_.func_176745_a()];
      }

      static {
         field_178282_n[EnumFacing.DOWN.func_176745_a()] = DOWN;
         field_178282_n[EnumFacing.UP.func_176745_a()] = UP;
         field_178282_n[EnumFacing.NORTH.func_176745_a()] = NORTH;
         field_178282_n[EnumFacing.SOUTH.func_176745_a()] = SOUTH;
         field_178282_n[EnumFacing.WEST.func_176745_a()] = WEST;
         field_178282_n[EnumFacing.EAST.func_176745_a()] = EAST;
      }
   }

   public static enum Orientation {
      DOWN(EnumFacing.DOWN, false),
      UP(EnumFacing.UP, false),
      NORTH(EnumFacing.NORTH, false),
      SOUTH(EnumFacing.SOUTH, false),
      WEST(EnumFacing.WEST, false),
      EAST(EnumFacing.EAST, false),
      FLIP_DOWN(EnumFacing.DOWN, true),
      FLIP_UP(EnumFacing.UP, true),
      FLIP_NORTH(EnumFacing.NORTH, true),
      FLIP_SOUTH(EnumFacing.SOUTH, true),
      FLIP_WEST(EnumFacing.WEST, true),
      FLIP_EAST(EnumFacing.EAST, true);

      protected final int field_178229_m;

      private Orientation(EnumFacing p_i46233_3_, boolean p_i46233_4_) {
         this.field_178229_m = p_i46233_3_.func_176745_a() + (p_i46233_4_?EnumFacing.values().length:0);
      }
   }

   static enum VertexTranslations {
      DOWN(0, 1, 2, 3),
      UP(2, 3, 0, 1),
      NORTH(3, 0, 1, 2),
      SOUTH(0, 1, 2, 3),
      WEST(3, 0, 1, 2),
      EAST(1, 2, 3, 0);

      private final int field_178191_g;
      private final int field_178200_h;
      private final int field_178201_i;
      private final int field_178198_j;
      private static final BlockModelRenderer.VertexTranslations[] field_178199_k = new BlockModelRenderer.VertexTranslations[6];

      private VertexTranslations(int p_i46234_3_, int p_i46234_4_, int p_i46234_5_, int p_i46234_6_) {
         this.field_178191_g = p_i46234_3_;
         this.field_178200_h = p_i46234_4_;
         this.field_178201_i = p_i46234_5_;
         this.field_178198_j = p_i46234_6_;
      }

      public static BlockModelRenderer.VertexTranslations func_178184_a(EnumFacing p_178184_0_) {
         return field_178199_k[p_178184_0_.func_176745_a()];
      }

      static {
         field_178199_k[EnumFacing.DOWN.func_176745_a()] = DOWN;
         field_178199_k[EnumFacing.UP.func_176745_a()] = UP;
         field_178199_k[EnumFacing.NORTH.func_176745_a()] = NORTH;
         field_178199_k[EnumFacing.SOUTH.func_176745_a()] = SOUTH;
         field_178199_k[EnumFacing.WEST.func_176745_a()] = WEST;
         field_178199_k[EnumFacing.EAST.func_176745_a()] = EAST;
      }
   }
}
