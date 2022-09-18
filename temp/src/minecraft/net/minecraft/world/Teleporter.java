package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;

public class Teleporter {
   private final WorldServer field_85192_a;
   private final Random field_77187_a;
   private final LongHashMap<Teleporter.PortalPosition> field_85191_c = new LongHashMap();
   private final List<Long> field_85190_d = Lists.newArrayList();

   public Teleporter(WorldServer p_i1963_1_) {
      this.field_85192_a = p_i1963_1_;
      this.field_77187_a = new Random(p_i1963_1_.func_72905_C());
   }

   public void func_180266_a(Entity p_180266_1_, float p_180266_2_) {
      if(this.field_85192_a.field_73011_w.func_177502_q() != 1) {
         if(!this.func_180620_b(p_180266_1_, p_180266_2_)) {
            this.func_85188_a(p_180266_1_);
            this.func_180620_b(p_180266_1_, p_180266_2_);
         }
      } else {
         int lvt_3_1_ = MathHelper.func_76128_c(p_180266_1_.field_70165_t);
         int lvt_4_1_ = MathHelper.func_76128_c(p_180266_1_.field_70163_u) - 1;
         int lvt_5_1_ = MathHelper.func_76128_c(p_180266_1_.field_70161_v);
         int lvt_6_1_ = 1;
         int lvt_7_1_ = 0;

         for(int lvt_8_1_ = -2; lvt_8_1_ <= 2; ++lvt_8_1_) {
            for(int lvt_9_1_ = -2; lvt_9_1_ <= 2; ++lvt_9_1_) {
               for(int lvt_10_1_ = -1; lvt_10_1_ < 3; ++lvt_10_1_) {
                  int lvt_11_1_ = lvt_3_1_ + lvt_9_1_ * lvt_6_1_ + lvt_8_1_ * lvt_7_1_;
                  int lvt_12_1_ = lvt_4_1_ + lvt_10_1_;
                  int lvt_13_1_ = lvt_5_1_ + lvt_9_1_ * lvt_7_1_ - lvt_8_1_ * lvt_6_1_;
                  boolean lvt_14_1_ = lvt_10_1_ < 0;
                  this.field_85192_a.func_175656_a(new BlockPos(lvt_11_1_, lvt_12_1_, lvt_13_1_), lvt_14_1_?Blocks.field_150343_Z.func_176223_P():Blocks.field_150350_a.func_176223_P());
               }
            }
         }

         p_180266_1_.func_70012_b((double)lvt_3_1_, (double)lvt_4_1_, (double)lvt_5_1_, p_180266_1_.field_70177_z, 0.0F);
         p_180266_1_.field_70159_w = p_180266_1_.field_70181_x = p_180266_1_.field_70179_y = 0.0D;
      }
   }

   public boolean func_180620_b(Entity p_180620_1_, float p_180620_2_) {
      int lvt_3_1_ = 128;
      double lvt_4_1_ = -1.0D;
      int lvt_6_1_ = MathHelper.func_76128_c(p_180620_1_.field_70165_t);
      int lvt_7_1_ = MathHelper.func_76128_c(p_180620_1_.field_70161_v);
      boolean lvt_8_1_ = true;
      BlockPos lvt_9_1_ = BlockPos.field_177992_a;
      long lvt_10_1_ = ChunkCoordIntPair.func_77272_a(lvt_6_1_, lvt_7_1_);
      if(this.field_85191_c.func_76161_b(lvt_10_1_)) {
         Teleporter.PortalPosition lvt_12_1_ = (Teleporter.PortalPosition)this.field_85191_c.func_76164_a(lvt_10_1_);
         lvt_4_1_ = 0.0D;
         lvt_9_1_ = lvt_12_1_;
         lvt_12_1_.field_85087_d = this.field_85192_a.func_82737_E();
         lvt_8_1_ = false;
      } else {
         BlockPos lvt_12_2_ = new BlockPos(p_180620_1_);

         for(int lvt_13_1_ = -128; lvt_13_1_ <= 128; ++lvt_13_1_) {
            BlockPos lvt_16_1_;
            for(int lvt_14_1_ = -128; lvt_14_1_ <= 128; ++lvt_14_1_) {
               for(BlockPos lvt_15_1_ = lvt_12_2_.func_177982_a(lvt_13_1_, this.field_85192_a.func_72940_L() - 1 - lvt_12_2_.func_177956_o(), lvt_14_1_); lvt_15_1_.func_177956_o() >= 0; lvt_15_1_ = lvt_16_1_) {
                  lvt_16_1_ = lvt_15_1_.func_177977_b();
                  if(this.field_85192_a.func_180495_p(lvt_15_1_).func_177230_c() == Blocks.field_150427_aO) {
                     while(this.field_85192_a.func_180495_p(lvt_16_1_ = lvt_15_1_.func_177977_b()).func_177230_c() == Blocks.field_150427_aO) {
                        lvt_15_1_ = lvt_16_1_;
                     }

                     double lvt_17_1_ = lvt_15_1_.func_177951_i(lvt_12_2_);
                     if(lvt_4_1_ < 0.0D || lvt_17_1_ < lvt_4_1_) {
                        lvt_4_1_ = lvt_17_1_;
                        lvt_9_1_ = lvt_15_1_;
                     }
                  }
               }
            }
         }
      }

      if(lvt_4_1_ >= 0.0D) {
         if(lvt_8_1_) {
            this.field_85191_c.func_76163_a(lvt_10_1_, new Teleporter.PortalPosition(lvt_9_1_, this.field_85192_a.func_82737_E()));
            this.field_85190_d.add(Long.valueOf(lvt_10_1_));
         }

         double lvt_12_3_ = (double)lvt_9_1_.func_177958_n() + 0.5D;
         double lvt_14_2_ = (double)lvt_9_1_.func_177956_o() + 0.5D;
         double lvt_16_2_ = (double)lvt_9_1_.func_177952_p() + 0.5D;
         BlockPattern.PatternHelper lvt_18_1_ = Blocks.field_150427_aO.func_181089_f(this.field_85192_a, lvt_9_1_);
         boolean lvt_19_1_ = lvt_18_1_.func_177669_b().func_176746_e().func_176743_c() == EnumFacing.AxisDirection.NEGATIVE;
         double lvt_20_1_ = lvt_18_1_.func_177669_b().func_176740_k() == EnumFacing.Axis.X?(double)lvt_18_1_.func_181117_a().func_177952_p():(double)lvt_18_1_.func_181117_a().func_177958_n();
         lvt_14_2_ = (double)(lvt_18_1_.func_181117_a().func_177956_o() + 1) - p_180620_1_.func_181014_aG().field_72448_b * (double)lvt_18_1_.func_181119_e();
         if(lvt_19_1_) {
            ++lvt_20_1_;
         }

         if(lvt_18_1_.func_177669_b().func_176740_k() == EnumFacing.Axis.X) {
            lvt_16_2_ = lvt_20_1_ + (1.0D - p_180620_1_.func_181014_aG().field_72450_a) * (double)lvt_18_1_.func_181118_d() * (double)lvt_18_1_.func_177669_b().func_176746_e().func_176743_c().func_179524_a();
         } else {
            lvt_12_3_ = lvt_20_1_ + (1.0D - p_180620_1_.func_181014_aG().field_72450_a) * (double)lvt_18_1_.func_181118_d() * (double)lvt_18_1_.func_177669_b().func_176746_e().func_176743_c().func_179524_a();
         }

         float lvt_22_1_ = 0.0F;
         float lvt_23_1_ = 0.0F;
         float lvt_24_1_ = 0.0F;
         float lvt_25_1_ = 0.0F;
         if(lvt_18_1_.func_177669_b().func_176734_d() == p_180620_1_.func_181012_aH()) {
            lvt_22_1_ = 1.0F;
            lvt_23_1_ = 1.0F;
         } else if(lvt_18_1_.func_177669_b().func_176734_d() == p_180620_1_.func_181012_aH().func_176734_d()) {
            lvt_22_1_ = -1.0F;
            lvt_23_1_ = -1.0F;
         } else if(lvt_18_1_.func_177669_b().func_176734_d() == p_180620_1_.func_181012_aH().func_176746_e()) {
            lvt_24_1_ = 1.0F;
            lvt_25_1_ = -1.0F;
         } else {
            lvt_24_1_ = -1.0F;
            lvt_25_1_ = 1.0F;
         }

         double lvt_26_1_ = p_180620_1_.field_70159_w;
         double lvt_28_1_ = p_180620_1_.field_70179_y;
         p_180620_1_.field_70159_w = lvt_26_1_ * (double)lvt_22_1_ + lvt_28_1_ * (double)lvt_25_1_;
         p_180620_1_.field_70179_y = lvt_26_1_ * (double)lvt_24_1_ + lvt_28_1_ * (double)lvt_23_1_;
         p_180620_1_.field_70177_z = p_180620_2_ - (float)(p_180620_1_.func_181012_aH().func_176734_d().func_176736_b() * 90) + (float)(lvt_18_1_.func_177669_b().func_176736_b() * 90);
         p_180620_1_.func_70012_b(lvt_12_3_, lvt_14_2_, lvt_16_2_, p_180620_1_.field_70177_z, p_180620_1_.field_70125_A);
         return true;
      } else {
         return false;
      }
   }

   public boolean func_85188_a(Entity p_85188_1_) {
      int lvt_2_1_ = 16;
      double lvt_3_1_ = -1.0D;
      int lvt_5_1_ = MathHelper.func_76128_c(p_85188_1_.field_70165_t);
      int lvt_6_1_ = MathHelper.func_76128_c(p_85188_1_.field_70163_u);
      int lvt_7_1_ = MathHelper.func_76128_c(p_85188_1_.field_70161_v);
      int lvt_8_1_ = lvt_5_1_;
      int lvt_9_1_ = lvt_6_1_;
      int lvt_10_1_ = lvt_7_1_;
      int lvt_11_1_ = 0;
      int lvt_12_1_ = this.field_77187_a.nextInt(4);
      BlockPos.MutableBlockPos lvt_13_1_ = new BlockPos.MutableBlockPos();

      for(int lvt_14_1_ = lvt_5_1_ - lvt_2_1_; lvt_14_1_ <= lvt_5_1_ + lvt_2_1_; ++lvt_14_1_) {
         double lvt_15_1_ = (double)lvt_14_1_ + 0.5D - p_85188_1_.field_70165_t;

         for(int lvt_17_1_ = lvt_7_1_ - lvt_2_1_; lvt_17_1_ <= lvt_7_1_ + lvt_2_1_; ++lvt_17_1_) {
            double lvt_18_1_ = (double)lvt_17_1_ + 0.5D - p_85188_1_.field_70161_v;

            label142:
            for(int lvt_20_1_ = this.field_85192_a.func_72940_L() - 1; lvt_20_1_ >= 0; --lvt_20_1_) {
               if(this.field_85192_a.func_175623_d(lvt_13_1_.func_181079_c(lvt_14_1_, lvt_20_1_, lvt_17_1_))) {
                  while(lvt_20_1_ > 0 && this.field_85192_a.func_175623_d(lvt_13_1_.func_181079_c(lvt_14_1_, lvt_20_1_ - 1, lvt_17_1_))) {
                     --lvt_20_1_;
                  }

                  for(int lvt_21_1_ = lvt_12_1_; lvt_21_1_ < lvt_12_1_ + 4; ++lvt_21_1_) {
                     int lvt_22_1_ = lvt_21_1_ % 2;
                     int lvt_23_1_ = 1 - lvt_22_1_;
                     if(lvt_21_1_ % 4 >= 2) {
                        lvt_22_1_ = -lvt_22_1_;
                        lvt_23_1_ = -lvt_23_1_;
                     }

                     for(int lvt_24_1_ = 0; lvt_24_1_ < 3; ++lvt_24_1_) {
                        for(int lvt_25_1_ = 0; lvt_25_1_ < 4; ++lvt_25_1_) {
                           for(int lvt_26_1_ = -1; lvt_26_1_ < 4; ++lvt_26_1_) {
                              int lvt_27_1_ = lvt_14_1_ + (lvt_25_1_ - 1) * lvt_22_1_ + lvt_24_1_ * lvt_23_1_;
                              int lvt_28_1_ = lvt_20_1_ + lvt_26_1_;
                              int lvt_29_1_ = lvt_17_1_ + (lvt_25_1_ - 1) * lvt_23_1_ - lvt_24_1_ * lvt_22_1_;
                              lvt_13_1_.func_181079_c(lvt_27_1_, lvt_28_1_, lvt_29_1_);
                              if(lvt_26_1_ < 0 && !this.field_85192_a.func_180495_p(lvt_13_1_).func_177230_c().func_149688_o().func_76220_a() || lvt_26_1_ >= 0 && !this.field_85192_a.func_175623_d(lvt_13_1_)) {
                                 continue label142;
                              }
                           }
                        }
                     }

                     double lvt_24_2_ = (double)lvt_20_1_ + 0.5D - p_85188_1_.field_70163_u;
                     double lvt_26_2_ = lvt_15_1_ * lvt_15_1_ + lvt_24_2_ * lvt_24_2_ + lvt_18_1_ * lvt_18_1_;
                     if(lvt_3_1_ < 0.0D || lvt_26_2_ < lvt_3_1_) {
                        lvt_3_1_ = lvt_26_2_;
                        lvt_8_1_ = lvt_14_1_;
                        lvt_9_1_ = lvt_20_1_;
                        lvt_10_1_ = lvt_17_1_;
                        lvt_11_1_ = lvt_21_1_ % 4;
                     }
                  }
               }
            }
         }
      }

      if(lvt_3_1_ < 0.0D) {
         for(int lvt_14_2_ = lvt_5_1_ - lvt_2_1_; lvt_14_2_ <= lvt_5_1_ + lvt_2_1_; ++lvt_14_2_) {
            double lvt_15_2_ = (double)lvt_14_2_ + 0.5D - p_85188_1_.field_70165_t;

            for(int lvt_17_2_ = lvt_7_1_ - lvt_2_1_; lvt_17_2_ <= lvt_7_1_ + lvt_2_1_; ++lvt_17_2_) {
               double lvt_18_2_ = (double)lvt_17_2_ + 0.5D - p_85188_1_.field_70161_v;

               label562:
               for(int lvt_20_2_ = this.field_85192_a.func_72940_L() - 1; lvt_20_2_ >= 0; --lvt_20_2_) {
                  if(this.field_85192_a.func_175623_d(lvt_13_1_.func_181079_c(lvt_14_2_, lvt_20_2_, lvt_17_2_))) {
                     while(lvt_20_2_ > 0 && this.field_85192_a.func_175623_d(lvt_13_1_.func_181079_c(lvt_14_2_, lvt_20_2_ - 1, lvt_17_2_))) {
                        --lvt_20_2_;
                     }

                     for(int lvt_21_2_ = lvt_12_1_; lvt_21_2_ < lvt_12_1_ + 2; ++lvt_21_2_) {
                        int lvt_22_2_ = lvt_21_2_ % 2;
                        int lvt_23_2_ = 1 - lvt_22_2_;

                        for(int lvt_24_3_ = 0; lvt_24_3_ < 4; ++lvt_24_3_) {
                           for(int lvt_25_2_ = -1; lvt_25_2_ < 4; ++lvt_25_2_) {
                              int lvt_26_3_ = lvt_14_2_ + (lvt_24_3_ - 1) * lvt_22_2_;
                              int lvt_27_2_ = lvt_20_2_ + lvt_25_2_;
                              int lvt_28_2_ = lvt_17_2_ + (lvt_24_3_ - 1) * lvt_23_2_;
                              lvt_13_1_.func_181079_c(lvt_26_3_, lvt_27_2_, lvt_28_2_);
                              if(lvt_25_2_ < 0 && !this.field_85192_a.func_180495_p(lvt_13_1_).func_177230_c().func_149688_o().func_76220_a() || lvt_25_2_ >= 0 && !this.field_85192_a.func_175623_d(lvt_13_1_)) {
                                 continue label562;
                              }
                           }
                        }

                        double lvt_24_4_ = (double)lvt_20_2_ + 0.5D - p_85188_1_.field_70163_u;
                        double lvt_26_4_ = lvt_15_2_ * lvt_15_2_ + lvt_24_4_ * lvt_24_4_ + lvt_18_2_ * lvt_18_2_;
                        if(lvt_3_1_ < 0.0D || lvt_26_4_ < lvt_3_1_) {
                           lvt_3_1_ = lvt_26_4_;
                           lvt_8_1_ = lvt_14_2_;
                           lvt_9_1_ = lvt_20_2_;
                           lvt_10_1_ = lvt_17_2_;
                           lvt_11_1_ = lvt_21_2_ % 2;
                        }
                     }
                  }
               }
            }
         }
      }

      int lvt_15_3_ = lvt_8_1_;
      int lvt_16_1_ = lvt_9_1_;
      int lvt_17_3_ = lvt_10_1_;
      int lvt_18_3_ = lvt_11_1_ % 2;
      int lvt_19_1_ = 1 - lvt_18_3_;
      if(lvt_11_1_ % 4 >= 2) {
         lvt_18_3_ = -lvt_18_3_;
         lvt_19_1_ = -lvt_19_1_;
      }

      if(lvt_3_1_ < 0.0D) {
         lvt_9_1_ = MathHelper.func_76125_a(lvt_9_1_, 70, this.field_85192_a.func_72940_L() - 10);
         lvt_16_1_ = lvt_9_1_;

         for(int lvt_20_3_ = -1; lvt_20_3_ <= 1; ++lvt_20_3_) {
            for(int lvt_21_3_ = 1; lvt_21_3_ < 3; ++lvt_21_3_) {
               for(int lvt_22_3_ = -1; lvt_22_3_ < 3; ++lvt_22_3_) {
                  int lvt_23_3_ = lvt_15_3_ + (lvt_21_3_ - 1) * lvt_18_3_ + lvt_20_3_ * lvt_19_1_;
                  int lvt_24_5_ = lvt_16_1_ + lvt_22_3_;
                  int lvt_25_3_ = lvt_17_3_ + (lvt_21_3_ - 1) * lvt_19_1_ - lvt_20_3_ * lvt_18_3_;
                  boolean lvt_26_5_ = lvt_22_3_ < 0;
                  this.field_85192_a.func_175656_a(new BlockPos(lvt_23_3_, lvt_24_5_, lvt_25_3_), lvt_26_5_?Blocks.field_150343_Z.func_176223_P():Blocks.field_150350_a.func_176223_P());
               }
            }
         }
      }

      IBlockState lvt_20_4_ = Blocks.field_150427_aO.func_176223_P().func_177226_a(BlockPortal.field_176550_a, lvt_18_3_ != 0?EnumFacing.Axis.X:EnumFacing.Axis.Z);

      for(int lvt_21_4_ = 0; lvt_21_4_ < 4; ++lvt_21_4_) {
         for(int lvt_22_4_ = 0; lvt_22_4_ < 4; ++lvt_22_4_) {
            for(int lvt_23_4_ = -1; lvt_23_4_ < 4; ++lvt_23_4_) {
               int lvt_24_6_ = lvt_15_3_ + (lvt_22_4_ - 1) * lvt_18_3_;
               int lvt_25_4_ = lvt_16_1_ + lvt_23_4_;
               int lvt_26_6_ = lvt_17_3_ + (lvt_22_4_ - 1) * lvt_19_1_;
               boolean lvt_27_3_ = lvt_22_4_ == 0 || lvt_22_4_ == 3 || lvt_23_4_ == -1 || lvt_23_4_ == 3;
               this.field_85192_a.func_180501_a(new BlockPos(lvt_24_6_, lvt_25_4_, lvt_26_6_), lvt_27_3_?Blocks.field_150343_Z.func_176223_P():lvt_20_4_, 2);
            }
         }

         for(int lvt_22_5_ = 0; lvt_22_5_ < 4; ++lvt_22_5_) {
            for(int lvt_23_5_ = -1; lvt_23_5_ < 4; ++lvt_23_5_) {
               int lvt_24_7_ = lvt_15_3_ + (lvt_22_5_ - 1) * lvt_18_3_;
               int lvt_25_5_ = lvt_16_1_ + lvt_23_5_;
               int lvt_26_7_ = lvt_17_3_ + (lvt_22_5_ - 1) * lvt_19_1_;
               BlockPos lvt_27_4_ = new BlockPos(lvt_24_7_, lvt_25_5_, lvt_26_7_);
               this.field_85192_a.func_175685_c(lvt_27_4_, this.field_85192_a.func_180495_p(lvt_27_4_).func_177230_c());
            }
         }
      }

      return true;
   }

   public void func_85189_a(long p_85189_1_) {
      if(p_85189_1_ % 100L == 0L) {
         Iterator<Long> lvt_3_1_ = this.field_85190_d.iterator();
         long lvt_4_1_ = p_85189_1_ - 300L;

         while(lvt_3_1_.hasNext()) {
            Long lvt_6_1_ = (Long)lvt_3_1_.next();
            Teleporter.PortalPosition lvt_7_1_ = (Teleporter.PortalPosition)this.field_85191_c.func_76164_a(lvt_6_1_.longValue());
            if(lvt_7_1_ == null || lvt_7_1_.field_85087_d < lvt_4_1_) {
               lvt_3_1_.remove();
               this.field_85191_c.func_76159_d(lvt_6_1_.longValue());
            }
         }
      }

   }

   public class PortalPosition extends BlockPos {
      public long field_85087_d;

      public PortalPosition(BlockPos p_i45747_2_, long p_i45747_3_) {
         super(p_i45747_2_.func_177958_n(), p_i45747_2_.func_177956_o(), p_i45747_2_.func_177952_p());
         this.field_85087_d = p_i45747_3_;
      }
   }
}
