package net.minecraft.client.particle;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Barrier;
import net.minecraft.client.particle.EntityAuraFX;
import net.minecraft.client.particle.EntityBlockDustFX;
import net.minecraft.client.particle.EntityBreakingFX;
import net.minecraft.client.particle.EntityBubbleFX;
import net.minecraft.client.particle.EntityCloudFX;
import net.minecraft.client.particle.EntityCrit2FX;
import net.minecraft.client.particle.EntityCritFX;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityDropParticleFX;
import net.minecraft.client.particle.EntityEnchantmentTableParticleFX;
import net.minecraft.client.particle.EntityExplodeFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFirework;
import net.minecraft.client.particle.EntityFishWakeFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.particle.EntityFootStepFX;
import net.minecraft.client.particle.EntityHeartFX;
import net.minecraft.client.particle.EntityHugeExplodeFX;
import net.minecraft.client.particle.EntityLargeExplodeFX;
import net.minecraft.client.particle.EntityLavaFX;
import net.minecraft.client.particle.EntityNoteFX;
import net.minecraft.client.particle.EntityParticleEmitter;
import net.minecraft.client.particle.EntityPortalFX;
import net.minecraft.client.particle.EntityRainFX;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.particle.EntitySnowShovelFX;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraft.client.particle.EntitySplashFX;
import net.minecraft.client.particle.EntitySuspendFX;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.MobAppearance;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EffectRenderer {
   private static final ResourceLocation field_110737_b = new ResourceLocation("textures/particle/particles.png");
   protected World field_78878_a;
   private List<EntityFX>[][] field_78876_b = new List[4][];
   private List<EntityParticleEmitter> field_178933_d = Lists.newArrayList();
   private TextureManager field_78877_c;
   private Random field_78875_d = new Random();
   private Map<Integer, IParticleFactory> field_178932_g = Maps.newHashMap();

   public EffectRenderer(World p_i1220_1_, TextureManager p_i1220_2_) {
      this.field_78878_a = p_i1220_1_;
      this.field_78877_c = p_i1220_2_;

      for(int lvt_3_1_ = 0; lvt_3_1_ < 4; ++lvt_3_1_) {
         this.field_78876_b[lvt_3_1_] = new List[2];

         for(int lvt_4_1_ = 0; lvt_4_1_ < 2; ++lvt_4_1_) {
            this.field_78876_b[lvt_3_1_][lvt_4_1_] = Lists.newArrayList();
         }
      }

      this.func_178930_c();
   }

   private void func_178930_c() {
      this.func_178929_a(EnumParticleTypes.EXPLOSION_NORMAL.func_179348_c(), new EntityExplodeFX.Factory());
      this.func_178929_a(EnumParticleTypes.WATER_BUBBLE.func_179348_c(), new EntityBubbleFX.Factory());
      this.func_178929_a(EnumParticleTypes.WATER_SPLASH.func_179348_c(), new EntitySplashFX.Factory());
      this.func_178929_a(EnumParticleTypes.WATER_WAKE.func_179348_c(), new EntityFishWakeFX.Factory());
      this.func_178929_a(EnumParticleTypes.WATER_DROP.func_179348_c(), new EntityRainFX.Factory());
      this.func_178929_a(EnumParticleTypes.SUSPENDED.func_179348_c(), new EntitySuspendFX.Factory());
      this.func_178929_a(EnumParticleTypes.SUSPENDED_DEPTH.func_179348_c(), new EntityAuraFX.Factory());
      this.func_178929_a(EnumParticleTypes.CRIT.func_179348_c(), new EntityCrit2FX.Factory());
      this.func_178929_a(EnumParticleTypes.CRIT_MAGIC.func_179348_c(), new EntityCrit2FX.MagicFactory());
      this.func_178929_a(EnumParticleTypes.SMOKE_NORMAL.func_179348_c(), new EntitySmokeFX.Factory());
      this.func_178929_a(EnumParticleTypes.SMOKE_LARGE.func_179348_c(), new EntityCritFX.Factory());
      this.func_178929_a(EnumParticleTypes.SPELL.func_179348_c(), new EntitySpellParticleFX.Factory());
      this.func_178929_a(EnumParticleTypes.SPELL_INSTANT.func_179348_c(), new EntitySpellParticleFX.InstantFactory());
      this.func_178929_a(EnumParticleTypes.SPELL_MOB.func_179348_c(), new EntitySpellParticleFX.MobFactory());
      this.func_178929_a(EnumParticleTypes.SPELL_MOB_AMBIENT.func_179348_c(), new EntitySpellParticleFX.AmbientMobFactory());
      this.func_178929_a(EnumParticleTypes.SPELL_WITCH.func_179348_c(), new EntitySpellParticleFX.WitchFactory());
      this.func_178929_a(EnumParticleTypes.DRIP_WATER.func_179348_c(), new EntityDropParticleFX.WaterFactory());
      this.func_178929_a(EnumParticleTypes.DRIP_LAVA.func_179348_c(), new EntityDropParticleFX.LavaFactory());
      this.func_178929_a(EnumParticleTypes.VILLAGER_ANGRY.func_179348_c(), new EntityHeartFX.AngryVillagerFactory());
      this.func_178929_a(EnumParticleTypes.VILLAGER_HAPPY.func_179348_c(), new EntityAuraFX.HappyVillagerFactory());
      this.func_178929_a(EnumParticleTypes.TOWN_AURA.func_179348_c(), new EntityAuraFX.Factory());
      this.func_178929_a(EnumParticleTypes.NOTE.func_179348_c(), new EntityNoteFX.Factory());
      this.func_178929_a(EnumParticleTypes.PORTAL.func_179348_c(), new EntityPortalFX.Factory());
      this.func_178929_a(EnumParticleTypes.ENCHANTMENT_TABLE.func_179348_c(), new EntityEnchantmentTableParticleFX.EnchantmentTable());
      this.func_178929_a(EnumParticleTypes.FLAME.func_179348_c(), new EntityFlameFX.Factory());
      this.func_178929_a(EnumParticleTypes.LAVA.func_179348_c(), new EntityLavaFX.Factory());
      this.func_178929_a(EnumParticleTypes.FOOTSTEP.func_179348_c(), new EntityFootStepFX.Factory());
      this.func_178929_a(EnumParticleTypes.CLOUD.func_179348_c(), new EntityCloudFX.Factory());
      this.func_178929_a(EnumParticleTypes.REDSTONE.func_179348_c(), new EntityReddustFX.Factory());
      this.func_178929_a(EnumParticleTypes.SNOWBALL.func_179348_c(), new EntityBreakingFX.SnowballFactory());
      this.func_178929_a(EnumParticleTypes.SNOW_SHOVEL.func_179348_c(), new EntitySnowShovelFX.Factory());
      this.func_178929_a(EnumParticleTypes.SLIME.func_179348_c(), new EntityBreakingFX.SlimeFactory());
      this.func_178929_a(EnumParticleTypes.HEART.func_179348_c(), new EntityHeartFX.Factory());
      this.func_178929_a(EnumParticleTypes.BARRIER.func_179348_c(), new Barrier.Factory());
      this.func_178929_a(EnumParticleTypes.ITEM_CRACK.func_179348_c(), new EntityBreakingFX.Factory());
      this.func_178929_a(EnumParticleTypes.BLOCK_CRACK.func_179348_c(), new EntityDiggingFX.Factory());
      this.func_178929_a(EnumParticleTypes.BLOCK_DUST.func_179348_c(), new EntityBlockDustFX.Factory());
      this.func_178929_a(EnumParticleTypes.EXPLOSION_HUGE.func_179348_c(), new EntityHugeExplodeFX.Factory());
      this.func_178929_a(EnumParticleTypes.EXPLOSION_LARGE.func_179348_c(), new EntityLargeExplodeFX.Factory());
      this.func_178929_a(EnumParticleTypes.FIREWORKS_SPARK.func_179348_c(), new EntityFirework.Factory());
      this.func_178929_a(EnumParticleTypes.MOB_APPEARANCE.func_179348_c(), new MobAppearance.Factory());
   }

   public void func_178929_a(int p_178929_1_, IParticleFactory p_178929_2_) {
      this.field_178932_g.put(Integer.valueOf(p_178929_1_), p_178929_2_);
   }

   public void func_178926_a(Entity p_178926_1_, EnumParticleTypes p_178926_2_) {
      this.field_178933_d.add(new EntityParticleEmitter(this.field_78878_a, p_178926_1_, p_178926_2_));
   }

   public EntityFX func_178927_a(int p_178927_1_, double p_178927_2_, double p_178927_4_, double p_178927_6_, double p_178927_8_, double p_178927_10_, double p_178927_12_, int... p_178927_14_) {
      IParticleFactory lvt_15_1_ = (IParticleFactory)this.field_178932_g.get(Integer.valueOf(p_178927_1_));
      if(lvt_15_1_ != null) {
         EntityFX lvt_16_1_ = lvt_15_1_.func_178902_a(p_178927_1_, this.field_78878_a, p_178927_2_, p_178927_4_, p_178927_6_, p_178927_8_, p_178927_10_, p_178927_12_, p_178927_14_);
         if(lvt_16_1_ != null) {
            this.func_78873_a(lvt_16_1_);
            return lvt_16_1_;
         }
      }

      return null;
   }

   public void func_78873_a(EntityFX p_78873_1_) {
      int lvt_2_1_ = p_78873_1_.func_70537_b();
      int lvt_3_1_ = p_78873_1_.func_174838_j() != 1.0F?0:1;
      if(this.field_78876_b[lvt_2_1_][lvt_3_1_].size() >= 4000) {
         this.field_78876_b[lvt_2_1_][lvt_3_1_].remove(0);
      }

      this.field_78876_b[lvt_2_1_][lvt_3_1_].add(p_78873_1_);
   }

   public void func_78868_a() {
      for(int lvt_1_1_ = 0; lvt_1_1_ < 4; ++lvt_1_1_) {
         this.func_178922_a(lvt_1_1_);
      }

      List<EntityParticleEmitter> lvt_1_2_ = Lists.newArrayList();

      for(EntityParticleEmitter lvt_3_1_ : this.field_178933_d) {
         lvt_3_1_.func_70071_h_();
         if(lvt_3_1_.field_70128_L) {
            lvt_1_2_.add(lvt_3_1_);
         }
      }

      this.field_178933_d.removeAll(lvt_1_2_);
   }

   private void func_178922_a(int p_178922_1_) {
      for(int lvt_2_1_ = 0; lvt_2_1_ < 2; ++lvt_2_1_) {
         this.func_178925_a(this.field_78876_b[p_178922_1_][lvt_2_1_]);
      }

   }

   private void func_178925_a(List<EntityFX> p_178925_1_) {
      List<EntityFX> lvt_2_1_ = Lists.newArrayList();

      for(int lvt_3_1_ = 0; lvt_3_1_ < p_178925_1_.size(); ++lvt_3_1_) {
         EntityFX lvt_4_1_ = (EntityFX)p_178925_1_.get(lvt_3_1_);
         this.func_178923_d(lvt_4_1_);
         if(lvt_4_1_.field_70128_L) {
            lvt_2_1_.add(lvt_4_1_);
         }
      }

      p_178925_1_.removeAll(lvt_2_1_);
   }

   private void func_178923_d(final EntityFX p_178923_1_) {
      try {
         p_178923_1_.func_70071_h_();
      } catch (Throwable var6) {
         CrashReport lvt_3_1_ = CrashReport.func_85055_a(var6, "Ticking Particle");
         CrashReportCategory lvt_4_1_ = lvt_3_1_.func_85058_a("Particle being ticked");
         final int lvt_5_1_ = p_178923_1_.func_70537_b();
         lvt_4_1_.func_71500_a("Particle", new Callable<String>() {
            public String call() throws Exception {
               return p_178923_1_.toString();
            }

            // $FF: synthetic method
            public Object call() throws Exception {
               return this.call();
            }
         });
         lvt_4_1_.func_71500_a("Particle Type", new Callable<String>() {
            public String call() throws Exception {
               return lvt_5_1_ == 0?"MISC_TEXTURE":(lvt_5_1_ == 1?"TERRAIN_TEXTURE":(lvt_5_1_ == 3?"ENTITY_PARTICLE_TEXTURE":"Unknown - " + lvt_5_1_));
            }

            // $FF: synthetic method
            public Object call() throws Exception {
               return this.call();
            }
         });
         throw new ReportedException(lvt_3_1_);
      }
   }

   public void func_78874_a(Entity p_78874_1_, float p_78874_2_) {
      float lvt_3_1_ = ActiveRenderInfo.func_178808_b();
      float lvt_4_1_ = ActiveRenderInfo.func_178803_d();
      float lvt_5_1_ = ActiveRenderInfo.func_178805_e();
      float lvt_6_1_ = ActiveRenderInfo.func_178807_f();
      float lvt_7_1_ = ActiveRenderInfo.func_178809_c();
      EntityFX.field_70556_an = p_78874_1_.field_70142_S + (p_78874_1_.field_70165_t - p_78874_1_.field_70142_S) * (double)p_78874_2_;
      EntityFX.field_70554_ao = p_78874_1_.field_70137_T + (p_78874_1_.field_70163_u - p_78874_1_.field_70137_T) * (double)p_78874_2_;
      EntityFX.field_70555_ap = p_78874_1_.field_70136_U + (p_78874_1_.field_70161_v - p_78874_1_.field_70136_U) * (double)p_78874_2_;
      GlStateManager.func_179147_l();
      GlStateManager.func_179112_b(770, 771);
      GlStateManager.func_179092_a(516, 0.003921569F);

      for(final int lvt_8_1_ = 0; lvt_8_1_ < 3; ++lvt_8_1_) {
         for(int lvt_9_1_ = 0; lvt_9_1_ < 2; ++lvt_9_1_) {
            if(!this.field_78876_b[lvt_8_1_][lvt_9_1_].isEmpty()) {
               switch(lvt_9_1_) {
               case 0:
                  GlStateManager.func_179132_a(false);
                  break;
               case 1:
                  GlStateManager.func_179132_a(true);
               }

               switch(lvt_8_1_) {
               case 0:
               default:
                  this.field_78877_c.func_110577_a(field_110737_b);
                  break;
               case 1:
                  this.field_78877_c.func_110577_a(TextureMap.field_110575_b);
               }

               GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
               Tessellator lvt_10_1_ = Tessellator.func_178181_a();
               WorldRenderer lvt_11_1_ = lvt_10_1_.func_178180_c();
               lvt_11_1_.func_181668_a(7, DefaultVertexFormats.field_181704_d);

               for(int lvt_12_1_ = 0; lvt_12_1_ < this.field_78876_b[lvt_8_1_][lvt_9_1_].size(); ++lvt_12_1_) {
                  final EntityFX lvt_13_1_ = (EntityFX)this.field_78876_b[lvt_8_1_][lvt_9_1_].get(lvt_12_1_);

                  try {
                     lvt_13_1_.func_180434_a(lvt_11_1_, p_78874_1_, p_78874_2_, lvt_3_1_, lvt_7_1_, lvt_4_1_, lvt_5_1_, lvt_6_1_);
                  } catch (Throwable var18) {
                     CrashReport lvt_15_1_ = CrashReport.func_85055_a(var18, "Rendering Particle");
                     CrashReportCategory lvt_16_1_ = lvt_15_1_.func_85058_a("Particle being rendered");
                     lvt_16_1_.func_71500_a("Particle", new Callable<String>() {
                        public String call() throws Exception {
                           return lvt_13_1_.toString();
                        }

                        // $FF: synthetic method
                        public Object call() throws Exception {
                           return this.call();
                        }
                     });
                     lvt_16_1_.func_71500_a("Particle Type", new Callable<String>() {
                        public String call() throws Exception {
                           return lvt_8_1_ == 0?"MISC_TEXTURE":(lvt_8_1_ == 1?"TERRAIN_TEXTURE":(lvt_8_1_ == 3?"ENTITY_PARTICLE_TEXTURE":"Unknown - " + lvt_8_1_));
                        }

                        // $FF: synthetic method
                        public Object call() throws Exception {
                           return this.call();
                        }
                     });
                     throw new ReportedException(lvt_15_1_);
                  }
               }

               lvt_10_1_.func_78381_a();
            }
         }
      }

      GlStateManager.func_179132_a(true);
      GlStateManager.func_179084_k();
      GlStateManager.func_179092_a(516, 0.1F);
   }

   public void func_78872_b(Entity p_78872_1_, float p_78872_2_) {
      float lvt_3_1_ = 0.017453292F;
      float lvt_4_1_ = MathHelper.func_76134_b(p_78872_1_.field_70177_z * 0.017453292F);
      float lvt_5_1_ = MathHelper.func_76126_a(p_78872_1_.field_70177_z * 0.017453292F);
      float lvt_6_1_ = -lvt_5_1_ * MathHelper.func_76126_a(p_78872_1_.field_70125_A * 0.017453292F);
      float lvt_7_1_ = lvt_4_1_ * MathHelper.func_76126_a(p_78872_1_.field_70125_A * 0.017453292F);
      float lvt_8_1_ = MathHelper.func_76134_b(p_78872_1_.field_70125_A * 0.017453292F);

      for(int lvt_9_1_ = 0; lvt_9_1_ < 2; ++lvt_9_1_) {
         List<EntityFX> lvt_10_1_ = this.field_78876_b[3][lvt_9_1_];
         if(!lvt_10_1_.isEmpty()) {
            Tessellator lvt_11_1_ = Tessellator.func_178181_a();
            WorldRenderer lvt_12_1_ = lvt_11_1_.func_178180_c();

            for(int lvt_13_1_ = 0; lvt_13_1_ < lvt_10_1_.size(); ++lvt_13_1_) {
               EntityFX lvt_14_1_ = (EntityFX)lvt_10_1_.get(lvt_13_1_);
               lvt_14_1_.func_180434_a(lvt_12_1_, p_78872_1_, p_78872_2_, lvt_4_1_, lvt_8_1_, lvt_5_1_, lvt_6_1_, lvt_7_1_);
            }
         }
      }

   }

   public void func_78870_a(World p_78870_1_) {
      this.field_78878_a = p_78870_1_;

      for(int lvt_2_1_ = 0; lvt_2_1_ < 4; ++lvt_2_1_) {
         for(int lvt_3_1_ = 0; lvt_3_1_ < 2; ++lvt_3_1_) {
            this.field_78876_b[lvt_2_1_][lvt_3_1_].clear();
         }
      }

      this.field_178933_d.clear();
   }

   public void func_180533_a(BlockPos p_180533_1_, IBlockState p_180533_2_) {
      if(p_180533_2_.func_177230_c().func_149688_o() != Material.field_151579_a) {
         p_180533_2_ = p_180533_2_.func_177230_c().func_176221_a(p_180533_2_, this.field_78878_a, p_180533_1_);
         int lvt_3_1_ = 4;

         for(int lvt_4_1_ = 0; lvt_4_1_ < lvt_3_1_; ++lvt_4_1_) {
            for(int lvt_5_1_ = 0; lvt_5_1_ < lvt_3_1_; ++lvt_5_1_) {
               for(int lvt_6_1_ = 0; lvt_6_1_ < lvt_3_1_; ++lvt_6_1_) {
                  double lvt_7_1_ = (double)p_180533_1_.func_177958_n() + ((double)lvt_4_1_ + 0.5D) / (double)lvt_3_1_;
                  double lvt_9_1_ = (double)p_180533_1_.func_177956_o() + ((double)lvt_5_1_ + 0.5D) / (double)lvt_3_1_;
                  double lvt_11_1_ = (double)p_180533_1_.func_177952_p() + ((double)lvt_6_1_ + 0.5D) / (double)lvt_3_1_;
                  this.func_78873_a((new EntityDiggingFX(this.field_78878_a, lvt_7_1_, lvt_9_1_, lvt_11_1_, lvt_7_1_ - (double)p_180533_1_.func_177958_n() - 0.5D, lvt_9_1_ - (double)p_180533_1_.func_177956_o() - 0.5D, lvt_11_1_ - (double)p_180533_1_.func_177952_p() - 0.5D, p_180533_2_)).func_174846_a(p_180533_1_));
               }
            }
         }

      }
   }

   public void func_180532_a(BlockPos p_180532_1_, EnumFacing p_180532_2_) {
      IBlockState lvt_3_1_ = this.field_78878_a.func_180495_p(p_180532_1_);
      Block lvt_4_1_ = lvt_3_1_.func_177230_c();
      if(lvt_4_1_.func_149645_b() != -1) {
         int lvt_5_1_ = p_180532_1_.func_177958_n();
         int lvt_6_1_ = p_180532_1_.func_177956_o();
         int lvt_7_1_ = p_180532_1_.func_177952_p();
         float lvt_8_1_ = 0.1F;
         double lvt_9_1_ = (double)lvt_5_1_ + this.field_78875_d.nextDouble() * (lvt_4_1_.func_149753_y() - lvt_4_1_.func_149704_x() - (double)(lvt_8_1_ * 2.0F)) + (double)lvt_8_1_ + lvt_4_1_.func_149704_x();
         double lvt_11_1_ = (double)lvt_6_1_ + this.field_78875_d.nextDouble() * (lvt_4_1_.func_149669_A() - lvt_4_1_.func_149665_z() - (double)(lvt_8_1_ * 2.0F)) + (double)lvt_8_1_ + lvt_4_1_.func_149665_z();
         double lvt_13_1_ = (double)lvt_7_1_ + this.field_78875_d.nextDouble() * (lvt_4_1_.func_149693_C() - lvt_4_1_.func_149706_B() - (double)(lvt_8_1_ * 2.0F)) + (double)lvt_8_1_ + lvt_4_1_.func_149706_B();
         if(p_180532_2_ == EnumFacing.DOWN) {
            lvt_11_1_ = (double)lvt_6_1_ + lvt_4_1_.func_149665_z() - (double)lvt_8_1_;
         }

         if(p_180532_2_ == EnumFacing.UP) {
            lvt_11_1_ = (double)lvt_6_1_ + lvt_4_1_.func_149669_A() + (double)lvt_8_1_;
         }

         if(p_180532_2_ == EnumFacing.NORTH) {
            lvt_13_1_ = (double)lvt_7_1_ + lvt_4_1_.func_149706_B() - (double)lvt_8_1_;
         }

         if(p_180532_2_ == EnumFacing.SOUTH) {
            lvt_13_1_ = (double)lvt_7_1_ + lvt_4_1_.func_149693_C() + (double)lvt_8_1_;
         }

         if(p_180532_2_ == EnumFacing.WEST) {
            lvt_9_1_ = (double)lvt_5_1_ + lvt_4_1_.func_149704_x() - (double)lvt_8_1_;
         }

         if(p_180532_2_ == EnumFacing.EAST) {
            lvt_9_1_ = (double)lvt_5_1_ + lvt_4_1_.func_149753_y() + (double)lvt_8_1_;
         }

         this.func_78873_a((new EntityDiggingFX(this.field_78878_a, lvt_9_1_, lvt_11_1_, lvt_13_1_, 0.0D, 0.0D, 0.0D, lvt_3_1_)).func_174846_a(p_180532_1_).func_70543_e(0.2F).func_70541_f(0.6F));
      }
   }

   public void func_178928_b(EntityFX p_178928_1_) {
      this.func_178924_a(p_178928_1_, 1, 0);
   }

   public void func_178931_c(EntityFX p_178931_1_) {
      this.func_178924_a(p_178931_1_, 0, 1);
   }

   private void func_178924_a(EntityFX p_178924_1_, int p_178924_2_, int p_178924_3_) {
      for(int lvt_4_1_ = 0; lvt_4_1_ < 4; ++lvt_4_1_) {
         if(this.field_78876_b[lvt_4_1_][p_178924_2_].contains(p_178924_1_)) {
            this.field_78876_b[lvt_4_1_][p_178924_2_].remove(p_178924_1_);
            this.field_78876_b[lvt_4_1_][p_178924_3_].add(p_178924_1_);
         }
      }

   }

   public String func_78869_b() {
      int lvt_1_1_ = 0;

      for(int lvt_2_1_ = 0; lvt_2_1_ < 4; ++lvt_2_1_) {
         for(int lvt_3_1_ = 0; lvt_3_1_ < 2; ++lvt_3_1_) {
            lvt_1_1_ += this.field_78876_b[lvt_2_1_][lvt_3_1_].size();
         }
      }

      return "" + lvt_1_1_;
   }
}
