package net.minecraft.client.renderer;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public class BlockFluidRenderer {
   private TextureAtlasSprite[] field_178272_a = new TextureAtlasSprite[2];
   private TextureAtlasSprite[] field_178271_b = new TextureAtlasSprite[2];

   public BlockFluidRenderer() {
      this.func_178268_a();
   }

   protected void func_178268_a() {
      TextureMap lvt_1_1_ = Minecraft.func_71410_x().func_147117_R();
      this.field_178272_a[0] = lvt_1_1_.func_110572_b("minecraft:blocks/lava_still");
      this.field_178272_a[1] = lvt_1_1_.func_110572_b("minecraft:blocks/lava_flow");
      this.field_178271_b[0] = lvt_1_1_.func_110572_b("minecraft:blocks/water_still");
      this.field_178271_b[1] = lvt_1_1_.func_110572_b("minecraft:blocks/water_flow");
   }

   public boolean func_178270_a(IBlockAccess p_178270_1_, IBlockState p_178270_2_, BlockPos p_178270_3_, WorldRenderer p_178270_4_) {
      BlockLiquid lvt_5_1_ = (BlockLiquid)p_178270_2_.func_177230_c();
      lvt_5_1_.func_180654_a(p_178270_1_, p_178270_3_);
      TextureAtlasSprite[] lvt_6_1_ = lvt_5_1_.func_149688_o() == Material.field_151587_i?this.field_178272_a:this.field_178271_b;
      int lvt_7_1_ = lvt_5_1_.func_176202_d(p_178270_1_, p_178270_3_);
      float lvt_8_1_ = (float)(lvt_7_1_ >> 16 & 255) / 255.0F;
      float lvt_9_1_ = (float)(lvt_7_1_ >> 8 & 255) / 255.0F;
      float lvt_10_1_ = (float)(lvt_7_1_ & 255) / 255.0F;
      boolean lvt_11_1_ = lvt_5_1_.func_176225_a(p_178270_1_, p_178270_3_.func_177984_a(), EnumFacing.UP);
      boolean lvt_12_1_ = lvt_5_1_.func_176225_a(p_178270_1_, p_178270_3_.func_177977_b(), EnumFacing.DOWN);
      boolean[] lvt_13_1_ = new boolean[]{lvt_5_1_.func_176225_a(p_178270_1_, p_178270_3_.func_177978_c(), EnumFacing.NORTH), lvt_5_1_.func_176225_a(p_178270_1_, p_178270_3_.func_177968_d(), EnumFacing.SOUTH), lvt_5_1_.func_176225_a(p_178270_1_, p_178270_3_.func_177976_e(), EnumFacing.WEST), lvt_5_1_.func_176225_a(p_178270_1_, p_178270_3_.func_177974_f(), EnumFacing.EAST)};
      if(!lvt_11_1_ && !lvt_12_1_ && !lvt_13_1_[0] && !lvt_13_1_[1] && !lvt_13_1_[2] && !lvt_13_1_[3]) {
         return false;
      } else {
         boolean lvt_14_1_ = false;
         float lvt_15_1_ = 0.5F;
         float lvt_16_1_ = 1.0F;
         float lvt_17_1_ = 0.8F;
         float lvt_18_1_ = 0.6F;
         Material lvt_19_1_ = lvt_5_1_.func_149688_o();
         float lvt_20_1_ = this.func_178269_a(p_178270_1_, p_178270_3_, lvt_19_1_);
         float lvt_21_1_ = this.func_178269_a(p_178270_1_, p_178270_3_.func_177968_d(), lvt_19_1_);
         float lvt_22_1_ = this.func_178269_a(p_178270_1_, p_178270_3_.func_177974_f().func_177968_d(), lvt_19_1_);
         float lvt_23_1_ = this.func_178269_a(p_178270_1_, p_178270_3_.func_177974_f(), lvt_19_1_);
         double lvt_24_1_ = (double)p_178270_3_.func_177958_n();
         double lvt_26_1_ = (double)p_178270_3_.func_177956_o();
         double lvt_28_1_ = (double)p_178270_3_.func_177952_p();
         float lvt_30_1_ = 0.001F;
         if(lvt_11_1_) {
            lvt_14_1_ = true;
            TextureAtlasSprite lvt_31_1_ = lvt_6_1_[0];
            float lvt_32_1_ = (float)BlockLiquid.func_180689_a(p_178270_1_, p_178270_3_, lvt_19_1_);
            if(lvt_32_1_ > -999.0F) {
               lvt_31_1_ = lvt_6_1_[1];
            }

            lvt_20_1_ -= lvt_30_1_;
            lvt_21_1_ -= lvt_30_1_;
            lvt_22_1_ -= lvt_30_1_;
            lvt_23_1_ -= lvt_30_1_;
            float lvt_33_1_;
            float lvt_34_1_;
            float lvt_35_1_;
            float lvt_36_1_;
            float lvt_37_1_;
            float lvt_38_1_;
            float lvt_39_1_;
            float lvt_40_1_;
            if(lvt_32_1_ < -999.0F) {
               lvt_33_1_ = lvt_31_1_.func_94214_a(0.0D);
               lvt_37_1_ = lvt_31_1_.func_94207_b(0.0D);
               lvt_34_1_ = lvt_33_1_;
               lvt_38_1_ = lvt_31_1_.func_94207_b(16.0D);
               lvt_35_1_ = lvt_31_1_.func_94214_a(16.0D);
               lvt_39_1_ = lvt_38_1_;
               lvt_36_1_ = lvt_35_1_;
               lvt_40_1_ = lvt_37_1_;
            } else {
               float lvt_41_1_ = MathHelper.func_76126_a(lvt_32_1_) * 0.25F;
               float lvt_42_1_ = MathHelper.func_76134_b(lvt_32_1_) * 0.25F;
               float lvt_43_1_ = 8.0F;
               lvt_33_1_ = lvt_31_1_.func_94214_a((double)(8.0F + (-lvt_42_1_ - lvt_41_1_) * 16.0F));
               lvt_37_1_ = lvt_31_1_.func_94207_b((double)(8.0F + (-lvt_42_1_ + lvt_41_1_) * 16.0F));
               lvt_34_1_ = lvt_31_1_.func_94214_a((double)(8.0F + (-lvt_42_1_ + lvt_41_1_) * 16.0F));
               lvt_38_1_ = lvt_31_1_.func_94207_b((double)(8.0F + (lvt_42_1_ + lvt_41_1_) * 16.0F));
               lvt_35_1_ = lvt_31_1_.func_94214_a((double)(8.0F + (lvt_42_1_ + lvt_41_1_) * 16.0F));
               lvt_39_1_ = lvt_31_1_.func_94207_b((double)(8.0F + (lvt_42_1_ - lvt_41_1_) * 16.0F));
               lvt_36_1_ = lvt_31_1_.func_94214_a((double)(8.0F + (lvt_42_1_ - lvt_41_1_) * 16.0F));
               lvt_40_1_ = lvt_31_1_.func_94207_b((double)(8.0F + (-lvt_42_1_ - lvt_41_1_) * 16.0F));
            }

            int lvt_41_2_ = lvt_5_1_.func_176207_c(p_178270_1_, p_178270_3_);
            int lvt_42_2_ = lvt_41_2_ >> 16 & '\uffff';
            int lvt_43_2_ = lvt_41_2_ & '\uffff';
            float lvt_44_1_ = lvt_16_1_ * lvt_8_1_;
            float lvt_45_1_ = lvt_16_1_ * lvt_9_1_;
            float lvt_46_1_ = lvt_16_1_ * lvt_10_1_;
            p_178270_4_.func_181662_b(lvt_24_1_ + 0.0D, lvt_26_1_ + (double)lvt_20_1_, lvt_28_1_ + 0.0D).func_181666_a(lvt_44_1_, lvt_45_1_, lvt_46_1_, 1.0F).func_181673_a((double)lvt_33_1_, (double)lvt_37_1_).func_181671_a(lvt_42_2_, lvt_43_2_).func_181675_d();
            p_178270_4_.func_181662_b(lvt_24_1_ + 0.0D, lvt_26_1_ + (double)lvt_21_1_, lvt_28_1_ + 1.0D).func_181666_a(lvt_44_1_, lvt_45_1_, lvt_46_1_, 1.0F).func_181673_a((double)lvt_34_1_, (double)lvt_38_1_).func_181671_a(lvt_42_2_, lvt_43_2_).func_181675_d();
            p_178270_4_.func_181662_b(lvt_24_1_ + 1.0D, lvt_26_1_ + (double)lvt_22_1_, lvt_28_1_ + 1.0D).func_181666_a(lvt_44_1_, lvt_45_1_, lvt_46_1_, 1.0F).func_181673_a((double)lvt_35_1_, (double)lvt_39_1_).func_181671_a(lvt_42_2_, lvt_43_2_).func_181675_d();
            p_178270_4_.func_181662_b(lvt_24_1_ + 1.0D, lvt_26_1_ + (double)lvt_23_1_, lvt_28_1_ + 0.0D).func_181666_a(lvt_44_1_, lvt_45_1_, lvt_46_1_, 1.0F).func_181673_a((double)lvt_36_1_, (double)lvt_40_1_).func_181671_a(lvt_42_2_, lvt_43_2_).func_181675_d();
            if(lvt_5_1_.func_176364_g(p_178270_1_, p_178270_3_.func_177984_a())) {
               p_178270_4_.func_181662_b(lvt_24_1_ + 0.0D, lvt_26_1_ + (double)lvt_20_1_, lvt_28_1_ + 0.0D).func_181666_a(lvt_44_1_, lvt_45_1_, lvt_46_1_, 1.0F).func_181673_a((double)lvt_33_1_, (double)lvt_37_1_).func_181671_a(lvt_42_2_, lvt_43_2_).func_181675_d();
               p_178270_4_.func_181662_b(lvt_24_1_ + 1.0D, lvt_26_1_ + (double)lvt_23_1_, lvt_28_1_ + 0.0D).func_181666_a(lvt_44_1_, lvt_45_1_, lvt_46_1_, 1.0F).func_181673_a((double)lvt_36_1_, (double)lvt_40_1_).func_181671_a(lvt_42_2_, lvt_43_2_).func_181675_d();
               p_178270_4_.func_181662_b(lvt_24_1_ + 1.0D, lvt_26_1_ + (double)lvt_22_1_, lvt_28_1_ + 1.0D).func_181666_a(lvt_44_1_, lvt_45_1_, lvt_46_1_, 1.0F).func_181673_a((double)lvt_35_1_, (double)lvt_39_1_).func_181671_a(lvt_42_2_, lvt_43_2_).func_181675_d();
               p_178270_4_.func_181662_b(lvt_24_1_ + 0.0D, lvt_26_1_ + (double)lvt_21_1_, lvt_28_1_ + 1.0D).func_181666_a(lvt_44_1_, lvt_45_1_, lvt_46_1_, 1.0F).func_181673_a((double)lvt_34_1_, (double)lvt_38_1_).func_181671_a(lvt_42_2_, lvt_43_2_).func_181675_d();
            }
         }

         if(lvt_12_1_) {
            float lvt_32_2_ = lvt_6_1_[0].func_94209_e();
            float lvt_33_3_ = lvt_6_1_[0].func_94212_f();
            float lvt_34_3_ = lvt_6_1_[0].func_94206_g();
            float lvt_35_3_ = lvt_6_1_[0].func_94210_h();
            int lvt_36_3_ = lvt_5_1_.func_176207_c(p_178270_1_, p_178270_3_.func_177977_b());
            int lvt_37_3_ = lvt_36_3_ >> 16 & '\uffff';
            int lvt_38_3_ = lvt_36_3_ & '\uffff';
            p_178270_4_.func_181662_b(lvt_24_1_, lvt_26_1_, lvt_28_1_ + 1.0D).func_181666_a(lvt_15_1_, lvt_15_1_, lvt_15_1_, 1.0F).func_181673_a((double)lvt_32_2_, (double)lvt_35_3_).func_181671_a(lvt_37_3_, lvt_38_3_).func_181675_d();
            p_178270_4_.func_181662_b(lvt_24_1_, lvt_26_1_, lvt_28_1_).func_181666_a(lvt_15_1_, lvt_15_1_, lvt_15_1_, 1.0F).func_181673_a((double)lvt_32_2_, (double)lvt_34_3_).func_181671_a(lvt_37_3_, lvt_38_3_).func_181675_d();
            p_178270_4_.func_181662_b(lvt_24_1_ + 1.0D, lvt_26_1_, lvt_28_1_).func_181666_a(lvt_15_1_, lvt_15_1_, lvt_15_1_, 1.0F).func_181673_a((double)lvt_33_3_, (double)lvt_34_3_).func_181671_a(lvt_37_3_, lvt_38_3_).func_181675_d();
            p_178270_4_.func_181662_b(lvt_24_1_ + 1.0D, lvt_26_1_, lvt_28_1_ + 1.0D).func_181666_a(lvt_15_1_, lvt_15_1_, lvt_15_1_, 1.0F).func_181673_a((double)lvt_33_3_, (double)lvt_35_3_).func_181671_a(lvt_37_3_, lvt_38_3_).func_181675_d();
            lvt_14_1_ = true;
         }

         for(int lvt_32_3_ = 0; lvt_32_3_ < 4; ++lvt_32_3_) {
            int lvt_33_4_ = 0;
            int lvt_34_4_ = 0;
            if(lvt_32_3_ == 0) {
               --lvt_34_4_;
            }

            if(lvt_32_3_ == 1) {
               ++lvt_34_4_;
            }

            if(lvt_32_3_ == 2) {
               --lvt_33_4_;
            }

            if(lvt_32_3_ == 3) {
               ++lvt_33_4_;
            }

            BlockPos lvt_35_4_ = p_178270_3_.func_177982_a(lvt_33_4_, 0, lvt_34_4_);
            TextureAtlasSprite lvt_31_2_ = lvt_6_1_[1];
            if(lvt_13_1_[lvt_32_3_]) {
               float lvt_36_4_;
               float lvt_37_4_;
               double lvt_38_4_;
               double lvt_40_3_;
               double lvt_42_3_;
               double lvt_44_2_;
               if(lvt_32_3_ == 0) {
                  lvt_36_4_ = lvt_20_1_;
                  lvt_37_4_ = lvt_23_1_;
                  lvt_38_4_ = lvt_24_1_;
                  lvt_42_3_ = lvt_24_1_ + 1.0D;
                  lvt_40_3_ = lvt_28_1_ + (double)lvt_30_1_;
                  lvt_44_2_ = lvt_28_1_ + (double)lvt_30_1_;
               } else if(lvt_32_3_ == 1) {
                  lvt_36_4_ = lvt_22_1_;
                  lvt_37_4_ = lvt_21_1_;
                  lvt_38_4_ = lvt_24_1_ + 1.0D;
                  lvt_42_3_ = lvt_24_1_;
                  lvt_40_3_ = lvt_28_1_ + 1.0D - (double)lvt_30_1_;
                  lvt_44_2_ = lvt_28_1_ + 1.0D - (double)lvt_30_1_;
               } else if(lvt_32_3_ == 2) {
                  lvt_36_4_ = lvt_21_1_;
                  lvt_37_4_ = lvt_20_1_;
                  lvt_38_4_ = lvt_24_1_ + (double)lvt_30_1_;
                  lvt_42_3_ = lvt_24_1_ + (double)lvt_30_1_;
                  lvt_40_3_ = lvt_28_1_ + 1.0D;
                  lvt_44_2_ = lvt_28_1_;
               } else {
                  lvt_36_4_ = lvt_23_1_;
                  lvt_37_4_ = lvt_22_1_;
                  lvt_38_4_ = lvt_24_1_ + 1.0D - (double)lvt_30_1_;
                  lvt_42_3_ = lvt_24_1_ + 1.0D - (double)lvt_30_1_;
                  lvt_40_3_ = lvt_28_1_;
                  lvt_44_2_ = lvt_28_1_ + 1.0D;
               }

               lvt_14_1_ = true;
               float lvt_46_2_ = lvt_31_2_.func_94214_a(0.0D);
               float lvt_47_1_ = lvt_31_2_.func_94214_a(8.0D);
               float lvt_48_1_ = lvt_31_2_.func_94207_b((double)((1.0F - lvt_36_4_) * 16.0F * 0.5F));
               float lvt_49_1_ = lvt_31_2_.func_94207_b((double)((1.0F - lvt_37_4_) * 16.0F * 0.5F));
               float lvt_50_1_ = lvt_31_2_.func_94207_b(8.0D);
               int lvt_51_1_ = lvt_5_1_.func_176207_c(p_178270_1_, lvt_35_4_);
               int lvt_52_1_ = lvt_51_1_ >> 16 & '\uffff';
               int lvt_53_1_ = lvt_51_1_ & '\uffff';
               float lvt_54_1_ = lvt_32_3_ < 2?lvt_17_1_:lvt_18_1_;
               float lvt_55_1_ = lvt_16_1_ * lvt_54_1_ * lvt_8_1_;
               float lvt_56_1_ = lvt_16_1_ * lvt_54_1_ * lvt_9_1_;
               float lvt_57_1_ = lvt_16_1_ * lvt_54_1_ * lvt_10_1_;
               p_178270_4_.func_181662_b(lvt_38_4_, lvt_26_1_ + (double)lvt_36_4_, lvt_40_3_).func_181666_a(lvt_55_1_, lvt_56_1_, lvt_57_1_, 1.0F).func_181673_a((double)lvt_46_2_, (double)lvt_48_1_).func_181671_a(lvt_52_1_, lvt_53_1_).func_181675_d();
               p_178270_4_.func_181662_b(lvt_42_3_, lvt_26_1_ + (double)lvt_37_4_, lvt_44_2_).func_181666_a(lvt_55_1_, lvt_56_1_, lvt_57_1_, 1.0F).func_181673_a((double)lvt_47_1_, (double)lvt_49_1_).func_181671_a(lvt_52_1_, lvt_53_1_).func_181675_d();
               p_178270_4_.func_181662_b(lvt_42_3_, lvt_26_1_ + 0.0D, lvt_44_2_).func_181666_a(lvt_55_1_, lvt_56_1_, lvt_57_1_, 1.0F).func_181673_a((double)lvt_47_1_, (double)lvt_50_1_).func_181671_a(lvt_52_1_, lvt_53_1_).func_181675_d();
               p_178270_4_.func_181662_b(lvt_38_4_, lvt_26_1_ + 0.0D, lvt_40_3_).func_181666_a(lvt_55_1_, lvt_56_1_, lvt_57_1_, 1.0F).func_181673_a((double)lvt_46_2_, (double)lvt_50_1_).func_181671_a(lvt_52_1_, lvt_53_1_).func_181675_d();
               p_178270_4_.func_181662_b(lvt_38_4_, lvt_26_1_ + 0.0D, lvt_40_3_).func_181666_a(lvt_55_1_, lvt_56_1_, lvt_57_1_, 1.0F).func_181673_a((double)lvt_46_2_, (double)lvt_50_1_).func_181671_a(lvt_52_1_, lvt_53_1_).func_181675_d();
               p_178270_4_.func_181662_b(lvt_42_3_, lvt_26_1_ + 0.0D, lvt_44_2_).func_181666_a(lvt_55_1_, lvt_56_1_, lvt_57_1_, 1.0F).func_181673_a((double)lvt_47_1_, (double)lvt_50_1_).func_181671_a(lvt_52_1_, lvt_53_1_).func_181675_d();
               p_178270_4_.func_181662_b(lvt_42_3_, lvt_26_1_ + (double)lvt_37_4_, lvt_44_2_).func_181666_a(lvt_55_1_, lvt_56_1_, lvt_57_1_, 1.0F).func_181673_a((double)lvt_47_1_, (double)lvt_49_1_).func_181671_a(lvt_52_1_, lvt_53_1_).func_181675_d();
               p_178270_4_.func_181662_b(lvt_38_4_, lvt_26_1_ + (double)lvt_36_4_, lvt_40_3_).func_181666_a(lvt_55_1_, lvt_56_1_, lvt_57_1_, 1.0F).func_181673_a((double)lvt_46_2_, (double)lvt_48_1_).func_181671_a(lvt_52_1_, lvt_53_1_).func_181675_d();
            }
         }

         return lvt_14_1_;
      }
   }

   private float func_178269_a(IBlockAccess p_178269_1_, BlockPos p_178269_2_, Material p_178269_3_) {
      int lvt_4_1_ = 0;
      float lvt_5_1_ = 0.0F;

      for(int lvt_6_1_ = 0; lvt_6_1_ < 4; ++lvt_6_1_) {
         BlockPos lvt_7_1_ = p_178269_2_.func_177982_a(-(lvt_6_1_ & 1), 0, -(lvt_6_1_ >> 1 & 1));
         if(p_178269_1_.func_180495_p(lvt_7_1_.func_177984_a()).func_177230_c().func_149688_o() == p_178269_3_) {
            return 1.0F;
         }

         IBlockState lvt_8_1_ = p_178269_1_.func_180495_p(lvt_7_1_);
         Material lvt_9_1_ = lvt_8_1_.func_177230_c().func_149688_o();
         if(lvt_9_1_ != p_178269_3_) {
            if(!lvt_9_1_.func_76220_a()) {
               ++lvt_5_1_;
               ++lvt_4_1_;
            }
         } else {
            int lvt_10_1_ = ((Integer)lvt_8_1_.func_177229_b(BlockLiquid.field_176367_b)).intValue();
            if(lvt_10_1_ >= 8 || lvt_10_1_ == 0) {
               lvt_5_1_ += BlockLiquid.func_149801_b(lvt_10_1_) * 10.0F;
               lvt_4_1_ += 10;
            }

            lvt_5_1_ += BlockLiquid.func_149801_b(lvt_10_1_);
            ++lvt_4_1_;
         }
      }

      return 1.0F - lvt_5_1_ / (float)lvt_4_1_;
   }
}
