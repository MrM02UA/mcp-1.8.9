package net.minecraft.world;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

public final class SpawnerAnimals {
   private static final int field_180268_a = (int)Math.pow(17.0D, 2.0D);
   private final Set<ChunkCoordIntPair> field_77193_b = Sets.newHashSet();

   public int func_77192_a(WorldServer p_77192_1_, boolean p_77192_2_, boolean p_77192_3_, boolean p_77192_4_) {
      if(!p_77192_2_ && !p_77192_3_) {
         return 0;
      } else {
         this.field_77193_b.clear();
         int lvt_5_1_ = 0;

         for(EntityPlayer lvt_7_1_ : p_77192_1_.field_73010_i) {
            if(!lvt_7_1_.func_175149_v()) {
               int lvt_8_1_ = MathHelper.func_76128_c(lvt_7_1_.field_70165_t / 16.0D);
               int lvt_9_1_ = MathHelper.func_76128_c(lvt_7_1_.field_70161_v / 16.0D);
               int lvt_10_1_ = 8;

               for(int lvt_11_1_ = -lvt_10_1_; lvt_11_1_ <= lvt_10_1_; ++lvt_11_1_) {
                  for(int lvt_12_1_ = -lvt_10_1_; lvt_12_1_ <= lvt_10_1_; ++lvt_12_1_) {
                     boolean lvt_13_1_ = lvt_11_1_ == -lvt_10_1_ || lvt_11_1_ == lvt_10_1_ || lvt_12_1_ == -lvt_10_1_ || lvt_12_1_ == lvt_10_1_;
                     ChunkCoordIntPair lvt_14_1_ = new ChunkCoordIntPair(lvt_11_1_ + lvt_8_1_, lvt_12_1_ + lvt_9_1_);
                     if(!this.field_77193_b.contains(lvt_14_1_)) {
                        ++lvt_5_1_;
                        if(!lvt_13_1_ && p_77192_1_.func_175723_af().func_177730_a(lvt_14_1_)) {
                           this.field_77193_b.add(lvt_14_1_);
                        }
                     }
                  }
               }
            }
         }

         int lvt_6_2_ = 0;
         BlockPos lvt_7_2_ = p_77192_1_.func_175694_M();

         for(EnumCreatureType lvt_11_2_ : EnumCreatureType.values()) {
            if((!lvt_11_2_.func_75599_d() || p_77192_3_) && (lvt_11_2_.func_75599_d() || p_77192_2_) && (!lvt_11_2_.func_82705_e() || p_77192_4_)) {
               int lvt_12_2_ = p_77192_1_.func_72907_a(lvt_11_2_.func_75598_a());
               int lvt_13_2_ = lvt_11_2_.func_75601_b() * lvt_5_1_ / field_180268_a;
               if(lvt_12_2_ <= lvt_13_2_) {
                  label374:
                  for(ChunkCoordIntPair lvt_15_1_ : this.field_77193_b) {
                     BlockPos lvt_16_1_ = func_180621_a(p_77192_1_, lvt_15_1_.field_77276_a, lvt_15_1_.field_77275_b);
                     int lvt_17_1_ = lvt_16_1_.func_177958_n();
                     int lvt_18_1_ = lvt_16_1_.func_177956_o();
                     int lvt_19_1_ = lvt_16_1_.func_177952_p();
                     Block lvt_20_1_ = p_77192_1_.func_180495_p(lvt_16_1_).func_177230_c();
                     if(!lvt_20_1_.func_149721_r()) {
                        int lvt_21_1_ = 0;

                        for(int lvt_22_1_ = 0; lvt_22_1_ < 3; ++lvt_22_1_) {
                           int lvt_23_1_ = lvt_17_1_;
                           int lvt_24_1_ = lvt_18_1_;
                           int lvt_25_1_ = lvt_19_1_;
                           int lvt_26_1_ = 6;
                           BiomeGenBase.SpawnListEntry lvt_27_1_ = null;
                           IEntityLivingData lvt_28_1_ = null;

                           for(int lvt_29_1_ = 0; lvt_29_1_ < 4; ++lvt_29_1_) {
                              lvt_23_1_ += p_77192_1_.field_73012_v.nextInt(lvt_26_1_) - p_77192_1_.field_73012_v.nextInt(lvt_26_1_);
                              lvt_24_1_ += p_77192_1_.field_73012_v.nextInt(1) - p_77192_1_.field_73012_v.nextInt(1);
                              lvt_25_1_ += p_77192_1_.field_73012_v.nextInt(lvt_26_1_) - p_77192_1_.field_73012_v.nextInt(lvt_26_1_);
                              BlockPos lvt_30_1_ = new BlockPos(lvt_23_1_, lvt_24_1_, lvt_25_1_);
                              float lvt_31_1_ = (float)lvt_23_1_ + 0.5F;
                              float lvt_32_1_ = (float)lvt_25_1_ + 0.5F;
                              if(!p_77192_1_.func_175636_b((double)lvt_31_1_, (double)lvt_24_1_, (double)lvt_32_1_, 24.0D) && lvt_7_2_.func_177954_c((double)lvt_31_1_, (double)lvt_24_1_, (double)lvt_32_1_) >= 576.0D) {
                                 if(lvt_27_1_ == null) {
                                    lvt_27_1_ = p_77192_1_.func_175734_a(lvt_11_2_, lvt_30_1_);
                                    if(lvt_27_1_ == null) {
                                       break;
                                    }
                                 }

                                 if(p_77192_1_.func_175732_a(lvt_11_2_, lvt_27_1_, lvt_30_1_) && func_180267_a(EntitySpawnPlacementRegistry.func_180109_a(lvt_27_1_.field_76300_b), p_77192_1_, lvt_30_1_)) {
                                    EntityLiving lvt_33_1_;
                                    try {
                                       lvt_33_1_ = (EntityLiving)lvt_27_1_.field_76300_b.getConstructor(new Class[]{World.class}).newInstance(new Object[]{p_77192_1_});
                                    } catch (Exception var35) {
                                       var35.printStackTrace();
                                       return lvt_6_2_;
                                    }

                                    lvt_33_1_.func_70012_b((double)lvt_31_1_, (double)lvt_24_1_, (double)lvt_32_1_, p_77192_1_.field_73012_v.nextFloat() * 360.0F, 0.0F);
                                    if(lvt_33_1_.func_70601_bi() && lvt_33_1_.func_70058_J()) {
                                       lvt_28_1_ = lvt_33_1_.func_180482_a(p_77192_1_.func_175649_E(new BlockPos(lvt_33_1_)), lvt_28_1_);
                                       if(lvt_33_1_.func_70058_J()) {
                                          ++lvt_21_1_;
                                          p_77192_1_.func_72838_d(lvt_33_1_);
                                       }

                                       if(lvt_21_1_ >= lvt_33_1_.func_70641_bl()) {
                                          continue label374;
                                       }
                                    }

                                    lvt_6_2_ += lvt_21_1_;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         return lvt_6_2_;
      }
   }

   protected static BlockPos func_180621_a(World p_180621_0_, int p_180621_1_, int p_180621_2_) {
      Chunk lvt_3_1_ = p_180621_0_.func_72964_e(p_180621_1_, p_180621_2_);
      int lvt_4_1_ = p_180621_1_ * 16 + p_180621_0_.field_73012_v.nextInt(16);
      int lvt_5_1_ = p_180621_2_ * 16 + p_180621_0_.field_73012_v.nextInt(16);
      int lvt_6_1_ = MathHelper.func_154354_b(lvt_3_1_.func_177433_f(new BlockPos(lvt_4_1_, 0, lvt_5_1_)) + 1, 16);
      int lvt_7_1_ = p_180621_0_.field_73012_v.nextInt(lvt_6_1_ > 0?lvt_6_1_:lvt_3_1_.func_76625_h() + 16 - 1);
      return new BlockPos(lvt_4_1_, lvt_7_1_, lvt_5_1_);
   }

   public static boolean func_180267_a(EntityLiving.SpawnPlacementType p_180267_0_, World p_180267_1_, BlockPos p_180267_2_) {
      if(!p_180267_1_.func_175723_af().func_177746_a(p_180267_2_)) {
         return false;
      } else {
         Block lvt_3_1_ = p_180267_1_.func_180495_p(p_180267_2_).func_177230_c();
         if(p_180267_0_ == EntityLiving.SpawnPlacementType.IN_WATER) {
            return lvt_3_1_.func_149688_o().func_76224_d() && p_180267_1_.func_180495_p(p_180267_2_.func_177977_b()).func_177230_c().func_149688_o().func_76224_d() && !p_180267_1_.func_180495_p(p_180267_2_.func_177984_a()).func_177230_c().func_149721_r();
         } else {
            BlockPos lvt_4_1_ = p_180267_2_.func_177977_b();
            if(!World.func_175683_a(p_180267_1_, lvt_4_1_)) {
               return false;
            } else {
               Block lvt_5_1_ = p_180267_1_.func_180495_p(lvt_4_1_).func_177230_c();
               boolean lvt_6_1_ = lvt_5_1_ != Blocks.field_150357_h && lvt_5_1_ != Blocks.field_180401_cv;
               return lvt_6_1_ && !lvt_3_1_.func_149721_r() && !lvt_3_1_.func_149688_o().func_76224_d() && !p_180267_1_.func_180495_p(p_180267_2_.func_177984_a()).func_177230_c().func_149721_r();
            }
         }
      }
   }

   public static void func_77191_a(World p_77191_0_, BiomeGenBase p_77191_1_, int p_77191_2_, int p_77191_3_, int p_77191_4_, int p_77191_5_, Random p_77191_6_) {
      List<BiomeGenBase.SpawnListEntry> lvt_7_1_ = p_77191_1_.func_76747_a(EnumCreatureType.CREATURE);
      if(!lvt_7_1_.isEmpty()) {
         while(p_77191_6_.nextFloat() < p_77191_1_.func_76741_f()) {
            BiomeGenBase.SpawnListEntry lvt_8_1_ = (BiomeGenBase.SpawnListEntry)WeightedRandom.func_76271_a(p_77191_0_.field_73012_v, lvt_7_1_);
            int lvt_9_1_ = lvt_8_1_.field_76301_c + p_77191_6_.nextInt(1 + lvt_8_1_.field_76299_d - lvt_8_1_.field_76301_c);
            IEntityLivingData lvt_10_1_ = null;
            int lvt_11_1_ = p_77191_2_ + p_77191_6_.nextInt(p_77191_4_);
            int lvt_12_1_ = p_77191_3_ + p_77191_6_.nextInt(p_77191_5_);
            int lvt_13_1_ = lvt_11_1_;
            int lvt_14_1_ = lvt_12_1_;

            for(int lvt_15_1_ = 0; lvt_15_1_ < lvt_9_1_; ++lvt_15_1_) {
               boolean lvt_16_1_ = false;

               for(int lvt_17_1_ = 0; !lvt_16_1_ && lvt_17_1_ < 4; ++lvt_17_1_) {
                  BlockPos lvt_18_1_ = p_77191_0_.func_175672_r(new BlockPos(lvt_11_1_, 0, lvt_12_1_));
                  if(func_180267_a(EntityLiving.SpawnPlacementType.ON_GROUND, p_77191_0_, lvt_18_1_)) {
                     EntityLiving lvt_19_1_;
                     try {
                        lvt_19_1_ = (EntityLiving)lvt_8_1_.field_76300_b.getConstructor(new Class[]{World.class}).newInstance(new Object[]{p_77191_0_});
                     } catch (Exception var21) {
                        var21.printStackTrace();
                        continue;
                     }

                     lvt_19_1_.func_70012_b((double)((float)lvt_11_1_ + 0.5F), (double)lvt_18_1_.func_177956_o(), (double)((float)lvt_12_1_ + 0.5F), p_77191_6_.nextFloat() * 360.0F, 0.0F);
                     p_77191_0_.func_72838_d(lvt_19_1_);
                     lvt_10_1_ = lvt_19_1_.func_180482_a(p_77191_0_.func_175649_E(new BlockPos(lvt_19_1_)), lvt_10_1_);
                     lvt_16_1_ = true;
                  }

                  lvt_11_1_ += p_77191_6_.nextInt(5) - p_77191_6_.nextInt(5);

                  for(lvt_12_1_ += p_77191_6_.nextInt(5) - p_77191_6_.nextInt(5); lvt_11_1_ < p_77191_2_ || lvt_11_1_ >= p_77191_2_ + p_77191_4_ || lvt_12_1_ < p_77191_3_ || lvt_12_1_ >= p_77191_3_ + p_77191_4_; lvt_12_1_ = lvt_14_1_ + p_77191_6_.nextInt(5) - p_77191_6_.nextInt(5)) {
                     lvt_11_1_ = lvt_13_1_ + p_77191_6_.nextInt(5) - p_77191_6_.nextInt(5);
                  }
               }
            }
         }

      }
   }
}
