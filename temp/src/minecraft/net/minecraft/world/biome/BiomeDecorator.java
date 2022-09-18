package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.ChunkProviderSettings;
import net.minecraft.world.gen.GeneratorBushFeature;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenCactus;
import net.minecraft.world.gen.feature.WorldGenClay;
import net.minecraft.world.gen.feature.WorldGenDeadBush;
import net.minecraft.world.gen.feature.WorldGenFlowers;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenPumpkin;
import net.minecraft.world.gen.feature.WorldGenReed;
import net.minecraft.world.gen.feature.WorldGenSand;
import net.minecraft.world.gen.feature.WorldGenWaterlily;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeDecorator {
   protected World field_76815_a;
   protected Random field_76813_b;
   protected BlockPos field_180294_c;
   protected ChunkProviderSettings field_180293_d;
   protected WorldGenerator field_76809_f = new WorldGenClay(4);
   protected WorldGenerator field_76810_g = new WorldGenSand(Blocks.field_150354_m, 7);
   protected WorldGenerator field_76822_h = new WorldGenSand(Blocks.field_150351_n, 6);
   protected WorldGenerator field_76823_i;
   protected WorldGenerator field_76820_j;
   protected WorldGenerator field_180296_j;
   protected WorldGenerator field_180297_k;
   protected WorldGenerator field_180295_l;
   protected WorldGenerator field_76821_k;
   protected WorldGenerator field_76818_l;
   protected WorldGenerator field_76819_m;
   protected WorldGenerator field_180299_p;
   protected WorldGenerator field_180298_q;
   protected WorldGenerator field_76831_p;
   protected WorldGenFlowers field_150514_p = new WorldGenFlowers(Blocks.field_150327_N, BlockFlower.EnumFlowerType.DANDELION);
   protected WorldGenerator field_76828_s = new GeneratorBushFeature(Blocks.field_150338_P);
   protected WorldGenerator field_76827_t = new GeneratorBushFeature(Blocks.field_150337_Q);
   protected WorldGenerator field_76826_u = new WorldGenBigMushroom();
   protected WorldGenerator field_76825_v = new WorldGenReed();
   protected WorldGenerator field_76824_w = new WorldGenCactus();
   protected WorldGenerator field_76834_x = new WorldGenWaterlily();
   protected int field_76833_y;
   protected int field_76832_z;
   protected int field_76802_A = 2;
   protected int field_76803_B = 1;
   protected int field_76804_C;
   protected int field_76798_D;
   protected int field_76799_E;
   protected int field_76800_F;
   protected int field_76801_G = 1;
   protected int field_76805_H = 3;
   protected int field_76806_I = 1;
   protected int field_76807_J;
   public boolean field_76808_K = true;

   public void func_180292_a(World p_180292_1_, Random p_180292_2_, BiomeGenBase p_180292_3_, BlockPos p_180292_4_) {
      if(this.field_76815_a != null) {
         throw new RuntimeException("Already decorating");
      } else {
         this.field_76815_a = p_180292_1_;
         String lvt_5_1_ = p_180292_1_.func_72912_H().func_82571_y();
         if(lvt_5_1_ != null) {
            this.field_180293_d = ChunkProviderSettings.Factory.func_177865_a(lvt_5_1_).func_177864_b();
         } else {
            this.field_180293_d = ChunkProviderSettings.Factory.func_177865_a("").func_177864_b();
         }

         this.field_76813_b = p_180292_2_;
         this.field_180294_c = p_180292_4_;
         this.field_76823_i = new WorldGenMinable(Blocks.field_150346_d.func_176223_P(), this.field_180293_d.field_177789_I);
         this.field_76820_j = new WorldGenMinable(Blocks.field_150351_n.func_176223_P(), this.field_180293_d.field_177785_M);
         this.field_180296_j = new WorldGenMinable(Blocks.field_150348_b.func_176223_P().func_177226_a(BlockStone.field_176247_a, BlockStone.EnumType.GRANITE), this.field_180293_d.field_177796_Q);
         this.field_180297_k = new WorldGenMinable(Blocks.field_150348_b.func_176223_P().func_177226_a(BlockStone.field_176247_a, BlockStone.EnumType.DIORITE), this.field_180293_d.field_177792_U);
         this.field_180295_l = new WorldGenMinable(Blocks.field_150348_b.func_176223_P().func_177226_a(BlockStone.field_176247_a, BlockStone.EnumType.ANDESITE), this.field_180293_d.field_177800_Y);
         this.field_76821_k = new WorldGenMinable(Blocks.field_150365_q.func_176223_P(), this.field_180293_d.field_177844_ac);
         this.field_76818_l = new WorldGenMinable(Blocks.field_150366_p.func_176223_P(), this.field_180293_d.field_177848_ag);
         this.field_76819_m = new WorldGenMinable(Blocks.field_150352_o.func_176223_P(), this.field_180293_d.field_177828_ak);
         this.field_180299_p = new WorldGenMinable(Blocks.field_150450_ax.func_176223_P(), this.field_180293_d.field_177836_ao);
         this.field_180298_q = new WorldGenMinable(Blocks.field_150482_ag.func_176223_P(), this.field_180293_d.field_177814_as);
         this.field_76831_p = new WorldGenMinable(Blocks.field_150369_x.func_176223_P(), this.field_180293_d.field_177822_aw);
         this.func_150513_a(p_180292_3_);
         this.field_76815_a = null;
         this.field_76813_b = null;
      }
   }

   protected void func_150513_a(BiomeGenBase p_150513_1_) {
      this.func_76797_b();

      for(int lvt_2_1_ = 0; lvt_2_1_ < this.field_76805_H; ++lvt_2_1_) {
         int lvt_3_1_ = this.field_76813_b.nextInt(16) + 8;
         int lvt_4_1_ = this.field_76813_b.nextInt(16) + 8;
         this.field_76810_g.func_180709_b(this.field_76815_a, this.field_76813_b, this.field_76815_a.func_175672_r(this.field_180294_c.func_177982_a(lvt_3_1_, 0, lvt_4_1_)));
      }

      for(int lvt_2_2_ = 0; lvt_2_2_ < this.field_76806_I; ++lvt_2_2_) {
         int lvt_3_2_ = this.field_76813_b.nextInt(16) + 8;
         int lvt_4_2_ = this.field_76813_b.nextInt(16) + 8;
         this.field_76809_f.func_180709_b(this.field_76815_a, this.field_76813_b, this.field_76815_a.func_175672_r(this.field_180294_c.func_177982_a(lvt_3_2_, 0, lvt_4_2_)));
      }

      for(int lvt_2_3_ = 0; lvt_2_3_ < this.field_76801_G; ++lvt_2_3_) {
         int lvt_3_3_ = this.field_76813_b.nextInt(16) + 8;
         int lvt_4_3_ = this.field_76813_b.nextInt(16) + 8;
         this.field_76822_h.func_180709_b(this.field_76815_a, this.field_76813_b, this.field_76815_a.func_175672_r(this.field_180294_c.func_177982_a(lvt_3_3_, 0, lvt_4_3_)));
      }

      int lvt_2_4_ = this.field_76832_z;
      if(this.field_76813_b.nextInt(10) == 0) {
         ++lvt_2_4_;
      }

      for(int lvt_3_4_ = 0; lvt_3_4_ < lvt_2_4_; ++lvt_3_4_) {
         int lvt_4_4_ = this.field_76813_b.nextInt(16) + 8;
         int lvt_5_1_ = this.field_76813_b.nextInt(16) + 8;
         WorldGenAbstractTree lvt_6_1_ = p_150513_1_.func_150567_a(this.field_76813_b);
         lvt_6_1_.func_175904_e();
         BlockPos lvt_7_1_ = this.field_76815_a.func_175645_m(this.field_180294_c.func_177982_a(lvt_4_4_, 0, lvt_5_1_));
         if(lvt_6_1_.func_180709_b(this.field_76815_a, this.field_76813_b, lvt_7_1_)) {
            lvt_6_1_.func_180711_a(this.field_76815_a, this.field_76813_b, lvt_7_1_);
         }
      }

      for(int lvt_3_5_ = 0; lvt_3_5_ < this.field_76807_J; ++lvt_3_5_) {
         int lvt_4_5_ = this.field_76813_b.nextInt(16) + 8;
         int lvt_5_2_ = this.field_76813_b.nextInt(16) + 8;
         this.field_76826_u.func_180709_b(this.field_76815_a, this.field_76813_b, this.field_76815_a.func_175645_m(this.field_180294_c.func_177982_a(lvt_4_5_, 0, lvt_5_2_)));
      }

      for(int lvt_3_6_ = 0; lvt_3_6_ < this.field_76802_A; ++lvt_3_6_) {
         int lvt_4_6_ = this.field_76813_b.nextInt(16) + 8;
         int lvt_5_3_ = this.field_76813_b.nextInt(16) + 8;
         int lvt_6_2_ = this.field_76815_a.func_175645_m(this.field_180294_c.func_177982_a(lvt_4_6_, 0, lvt_5_3_)).func_177956_o() + 32;
         if(lvt_6_2_ > 0) {
            int lvt_7_2_ = this.field_76813_b.nextInt(lvt_6_2_);
            BlockPos lvt_8_1_ = this.field_180294_c.func_177982_a(lvt_4_6_, lvt_7_2_, lvt_5_3_);
            BlockFlower.EnumFlowerType lvt_9_1_ = p_150513_1_.func_180623_a(this.field_76813_b, lvt_8_1_);
            BlockFlower lvt_10_1_ = lvt_9_1_.func_176964_a().func_180346_a();
            if(lvt_10_1_.func_149688_o() != Material.field_151579_a) {
               this.field_150514_p.func_175914_a(lvt_10_1_, lvt_9_1_);
               this.field_150514_p.func_180709_b(this.field_76815_a, this.field_76813_b, lvt_8_1_);
            }
         }
      }

      for(int lvt_3_7_ = 0; lvt_3_7_ < this.field_76803_B; ++lvt_3_7_) {
         int lvt_4_7_ = this.field_76813_b.nextInt(16) + 8;
         int lvt_5_4_ = this.field_76813_b.nextInt(16) + 8;
         int lvt_6_3_ = this.field_76815_a.func_175645_m(this.field_180294_c.func_177982_a(lvt_4_7_, 0, lvt_5_4_)).func_177956_o() * 2;
         if(lvt_6_3_ > 0) {
            int lvt_7_3_ = this.field_76813_b.nextInt(lvt_6_3_);
            p_150513_1_.func_76730_b(this.field_76813_b).func_180709_b(this.field_76815_a, this.field_76813_b, this.field_180294_c.func_177982_a(lvt_4_7_, lvt_7_3_, lvt_5_4_));
         }
      }

      for(int lvt_3_8_ = 0; lvt_3_8_ < this.field_76804_C; ++lvt_3_8_) {
         int lvt_4_8_ = this.field_76813_b.nextInt(16) + 8;
         int lvt_5_5_ = this.field_76813_b.nextInt(16) + 8;
         int lvt_6_4_ = this.field_76815_a.func_175645_m(this.field_180294_c.func_177982_a(lvt_4_8_, 0, lvt_5_5_)).func_177956_o() * 2;
         if(lvt_6_4_ > 0) {
            int lvt_7_4_ = this.field_76813_b.nextInt(lvt_6_4_);
            (new WorldGenDeadBush()).func_180709_b(this.field_76815_a, this.field_76813_b, this.field_180294_c.func_177982_a(lvt_4_8_, lvt_7_4_, lvt_5_5_));
         }
      }

      for(int lvt_3_9_ = 0; lvt_3_9_ < this.field_76833_y; ++lvt_3_9_) {
         int lvt_4_9_ = this.field_76813_b.nextInt(16) + 8;
         int lvt_5_6_ = this.field_76813_b.nextInt(16) + 8;
         int lvt_6_5_ = this.field_76815_a.func_175645_m(this.field_180294_c.func_177982_a(lvt_4_9_, 0, lvt_5_6_)).func_177956_o() * 2;
         if(lvt_6_5_ > 0) {
            int lvt_7_5_ = this.field_76813_b.nextInt(lvt_6_5_);

            BlockPos lvt_8_2_;
            BlockPos lvt_9_2_;
            for(lvt_8_2_ = this.field_180294_c.func_177982_a(lvt_4_9_, lvt_7_5_, lvt_5_6_); lvt_8_2_.func_177956_o() > 0; lvt_8_2_ = lvt_9_2_) {
               lvt_9_2_ = lvt_8_2_.func_177977_b();
               if(!this.field_76815_a.func_175623_d(lvt_9_2_)) {
                  break;
               }
            }

            this.field_76834_x.func_180709_b(this.field_76815_a, this.field_76813_b, lvt_8_2_);
         }
      }

      for(int lvt_3_10_ = 0; lvt_3_10_ < this.field_76798_D; ++lvt_3_10_) {
         if(this.field_76813_b.nextInt(4) == 0) {
            int lvt_4_10_ = this.field_76813_b.nextInt(16) + 8;
            int lvt_5_7_ = this.field_76813_b.nextInt(16) + 8;
            BlockPos lvt_6_6_ = this.field_76815_a.func_175645_m(this.field_180294_c.func_177982_a(lvt_4_10_, 0, lvt_5_7_));
            this.field_76828_s.func_180709_b(this.field_76815_a, this.field_76813_b, lvt_6_6_);
         }

         if(this.field_76813_b.nextInt(8) == 0) {
            int lvt_4_11_ = this.field_76813_b.nextInt(16) + 8;
            int lvt_5_8_ = this.field_76813_b.nextInt(16) + 8;
            int lvt_6_7_ = this.field_76815_a.func_175645_m(this.field_180294_c.func_177982_a(lvt_4_11_, 0, lvt_5_8_)).func_177956_o() * 2;
            if(lvt_6_7_ > 0) {
               int lvt_7_6_ = this.field_76813_b.nextInt(lvt_6_7_);
               BlockPos lvt_8_3_ = this.field_180294_c.func_177982_a(lvt_4_11_, lvt_7_6_, lvt_5_8_);
               this.field_76827_t.func_180709_b(this.field_76815_a, this.field_76813_b, lvt_8_3_);
            }
         }
      }

      if(this.field_76813_b.nextInt(4) == 0) {
         int lvt_3_11_ = this.field_76813_b.nextInt(16) + 8;
         int lvt_4_12_ = this.field_76813_b.nextInt(16) + 8;
         int lvt_5_9_ = this.field_76815_a.func_175645_m(this.field_180294_c.func_177982_a(lvt_3_11_, 0, lvt_4_12_)).func_177956_o() * 2;
         if(lvt_5_9_ > 0) {
            int lvt_6_8_ = this.field_76813_b.nextInt(lvt_5_9_);
            this.field_76828_s.func_180709_b(this.field_76815_a, this.field_76813_b, this.field_180294_c.func_177982_a(lvt_3_11_, lvt_6_8_, lvt_4_12_));
         }
      }

      if(this.field_76813_b.nextInt(8) == 0) {
         int lvt_3_12_ = this.field_76813_b.nextInt(16) + 8;
         int lvt_4_13_ = this.field_76813_b.nextInt(16) + 8;
         int lvt_5_10_ = this.field_76815_a.func_175645_m(this.field_180294_c.func_177982_a(lvt_3_12_, 0, lvt_4_13_)).func_177956_o() * 2;
         if(lvt_5_10_ > 0) {
            int lvt_6_9_ = this.field_76813_b.nextInt(lvt_5_10_);
            this.field_76827_t.func_180709_b(this.field_76815_a, this.field_76813_b, this.field_180294_c.func_177982_a(lvt_3_12_, lvt_6_9_, lvt_4_13_));
         }
      }

      for(int lvt_3_13_ = 0; lvt_3_13_ < this.field_76799_E; ++lvt_3_13_) {
         int lvt_4_14_ = this.field_76813_b.nextInt(16) + 8;
         int lvt_5_11_ = this.field_76813_b.nextInt(16) + 8;
         int lvt_6_10_ = this.field_76815_a.func_175645_m(this.field_180294_c.func_177982_a(lvt_4_14_, 0, lvt_5_11_)).func_177956_o() * 2;
         if(lvt_6_10_ > 0) {
            int lvt_7_7_ = this.field_76813_b.nextInt(lvt_6_10_);
            this.field_76825_v.func_180709_b(this.field_76815_a, this.field_76813_b, this.field_180294_c.func_177982_a(lvt_4_14_, lvt_7_7_, lvt_5_11_));
         }
      }

      for(int lvt_3_14_ = 0; lvt_3_14_ < 10; ++lvt_3_14_) {
         int lvt_4_15_ = this.field_76813_b.nextInt(16) + 8;
         int lvt_5_12_ = this.field_76813_b.nextInt(16) + 8;
         int lvt_6_11_ = this.field_76815_a.func_175645_m(this.field_180294_c.func_177982_a(lvt_4_15_, 0, lvt_5_12_)).func_177956_o() * 2;
         if(lvt_6_11_ > 0) {
            int lvt_7_8_ = this.field_76813_b.nextInt(lvt_6_11_);
            this.field_76825_v.func_180709_b(this.field_76815_a, this.field_76813_b, this.field_180294_c.func_177982_a(lvt_4_15_, lvt_7_8_, lvt_5_12_));
         }
      }

      if(this.field_76813_b.nextInt(32) == 0) {
         int lvt_3_15_ = this.field_76813_b.nextInt(16) + 8;
         int lvt_4_16_ = this.field_76813_b.nextInt(16) + 8;
         int lvt_5_13_ = this.field_76815_a.func_175645_m(this.field_180294_c.func_177982_a(lvt_3_15_, 0, lvt_4_16_)).func_177956_o() * 2;
         if(lvt_5_13_ > 0) {
            int lvt_6_12_ = this.field_76813_b.nextInt(lvt_5_13_);
            (new WorldGenPumpkin()).func_180709_b(this.field_76815_a, this.field_76813_b, this.field_180294_c.func_177982_a(lvt_3_15_, lvt_6_12_, lvt_4_16_));
         }
      }

      for(int lvt_3_16_ = 0; lvt_3_16_ < this.field_76800_F; ++lvt_3_16_) {
         int lvt_4_17_ = this.field_76813_b.nextInt(16) + 8;
         int lvt_5_14_ = this.field_76813_b.nextInt(16) + 8;
         int lvt_6_13_ = this.field_76815_a.func_175645_m(this.field_180294_c.func_177982_a(lvt_4_17_, 0, lvt_5_14_)).func_177956_o() * 2;
         if(lvt_6_13_ > 0) {
            int lvt_7_9_ = this.field_76813_b.nextInt(lvt_6_13_);
            this.field_76824_w.func_180709_b(this.field_76815_a, this.field_76813_b, this.field_180294_c.func_177982_a(lvt_4_17_, lvt_7_9_, lvt_5_14_));
         }
      }

      if(this.field_76808_K) {
         for(int lvt_3_17_ = 0; lvt_3_17_ < 50; ++lvt_3_17_) {
            int lvt_4_18_ = this.field_76813_b.nextInt(16) + 8;
            int lvt_5_15_ = this.field_76813_b.nextInt(16) + 8;
            int lvt_6_14_ = this.field_76813_b.nextInt(248) + 8;
            if(lvt_6_14_ > 0) {
               int lvt_7_10_ = this.field_76813_b.nextInt(lvt_6_14_);
               BlockPos lvt_8_4_ = this.field_180294_c.func_177982_a(lvt_4_18_, lvt_7_10_, lvt_5_15_);
               (new WorldGenLiquids(Blocks.field_150358_i)).func_180709_b(this.field_76815_a, this.field_76813_b, lvt_8_4_);
            }
         }

         for(int lvt_3_18_ = 0; lvt_3_18_ < 20; ++lvt_3_18_) {
            int lvt_4_19_ = this.field_76813_b.nextInt(16) + 8;
            int lvt_5_16_ = this.field_76813_b.nextInt(16) + 8;
            int lvt_6_15_ = this.field_76813_b.nextInt(this.field_76813_b.nextInt(this.field_76813_b.nextInt(240) + 8) + 8);
            BlockPos lvt_7_11_ = this.field_180294_c.func_177982_a(lvt_4_19_, lvt_6_15_, lvt_5_16_);
            (new WorldGenLiquids(Blocks.field_150356_k)).func_180709_b(this.field_76815_a, this.field_76813_b, lvt_7_11_);
         }
      }

   }

   protected void func_76795_a(int p_76795_1_, WorldGenerator p_76795_2_, int p_76795_3_, int p_76795_4_) {
      if(p_76795_4_ < p_76795_3_) {
         int lvt_5_1_ = p_76795_3_;
         p_76795_3_ = p_76795_4_;
         p_76795_4_ = lvt_5_1_;
      } else if(p_76795_4_ == p_76795_3_) {
         if(p_76795_3_ < 255) {
            ++p_76795_4_;
         } else {
            --p_76795_3_;
         }
      }

      for(int lvt_5_2_ = 0; lvt_5_2_ < p_76795_1_; ++lvt_5_2_) {
         BlockPos lvt_6_1_ = this.field_180294_c.func_177982_a(this.field_76813_b.nextInt(16), this.field_76813_b.nextInt(p_76795_4_ - p_76795_3_) + p_76795_3_, this.field_76813_b.nextInt(16));
         p_76795_2_.func_180709_b(this.field_76815_a, this.field_76813_b, lvt_6_1_);
      }

   }

   protected void func_76793_b(int p_76793_1_, WorldGenerator p_76793_2_, int p_76793_3_, int p_76793_4_) {
      for(int lvt_5_1_ = 0; lvt_5_1_ < p_76793_1_; ++lvt_5_1_) {
         BlockPos lvt_6_1_ = this.field_180294_c.func_177982_a(this.field_76813_b.nextInt(16), this.field_76813_b.nextInt(p_76793_4_) + this.field_76813_b.nextInt(p_76793_4_) + p_76793_3_ - p_76793_4_, this.field_76813_b.nextInt(16));
         p_76793_2_.func_180709_b(this.field_76815_a, this.field_76813_b, lvt_6_1_);
      }

   }

   protected void func_76797_b() {
      this.func_76795_a(this.field_180293_d.field_177790_J, this.field_76823_i, this.field_180293_d.field_177791_K, this.field_180293_d.field_177784_L);
      this.func_76795_a(this.field_180293_d.field_177786_N, this.field_76820_j, this.field_180293_d.field_177787_O, this.field_180293_d.field_177797_P);
      this.func_76795_a(this.field_180293_d.field_177795_V, this.field_180297_k, this.field_180293_d.field_177794_W, this.field_180293_d.field_177801_X);
      this.func_76795_a(this.field_180293_d.field_177799_R, this.field_180296_j, this.field_180293_d.field_177798_S, this.field_180293_d.field_177793_T);
      this.func_76795_a(this.field_180293_d.field_177802_Z, this.field_180295_l, this.field_180293_d.field_177846_aa, this.field_180293_d.field_177847_ab);
      this.func_76795_a(this.field_180293_d.field_177845_ad, this.field_76821_k, this.field_180293_d.field_177851_ae, this.field_180293_d.field_177853_af);
      this.func_76795_a(this.field_180293_d.field_177849_ah, this.field_76818_l, this.field_180293_d.field_177832_ai, this.field_180293_d.field_177834_aj);
      this.func_76795_a(this.field_180293_d.field_177830_al, this.field_76819_m, this.field_180293_d.field_177840_am, this.field_180293_d.field_177842_an);
      this.func_76795_a(this.field_180293_d.field_177838_ap, this.field_180299_p, this.field_180293_d.field_177818_aq, this.field_180293_d.field_177816_ar);
      this.func_76795_a(this.field_180293_d.field_177812_at, this.field_180298_q, this.field_180293_d.field_177826_au, this.field_180293_d.field_177824_av);
      this.func_76793_b(this.field_180293_d.field_177820_ax, this.field_76831_p, this.field_180293_d.field_177807_ay, this.field_180293_d.field_177805_az);
   }
}
