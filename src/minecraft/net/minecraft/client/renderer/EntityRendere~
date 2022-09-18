package net.minecraft.client.renderer;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MouseFilter;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.BiomeGenBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

public class EntityRenderer implements IResourceManagerReloadListener {
   private static final Logger field_147710_q = LogManager.getLogger();
   private static final ResourceLocation field_110924_q = new ResourceLocation("textures/environment/rain.png");
   private static final ResourceLocation field_110923_r = new ResourceLocation("textures/environment/snow.png");
   public static boolean field_78517_a;
   public static int field_78515_b;
   private Minecraft field_78531_r;
   private final IResourceManager field_147711_ac;
   private Random field_78537_ab = new Random();
   private float field_78530_s;
   public final ItemRenderer field_78516_c;
   private final MapItemRenderer field_147709_v;
   private int field_78529_t;
   private Entity field_78528_u;
   private MouseFilter field_78527_v = new MouseFilter();
   private MouseFilter field_78526_w = new MouseFilter();
   private float field_78490_B = 4.0F;
   private float field_78491_C = 4.0F;
   private float field_78496_H;
   private float field_78497_I;
   private float field_78498_J;
   private float field_78499_K;
   private float field_78492_L;
   private float field_78507_R;
   private float field_78506_S;
   private float field_82831_U;
   private float field_82832_V;
   private boolean field_78500_U;
   private boolean field_175074_C = true;
   private boolean field_175073_D = true;
   private long field_78508_Y = Minecraft.func_71386_F();
   private long field_78510_Z;
   private final DynamicTexture field_78513_d;
   private final int[] field_78504_Q;
   private final ResourceLocation field_110922_T;
   private boolean field_78536_aa;
   private float field_78514_e;
   private float field_175075_L;
   private int field_78534_ac;
   private float[] field_175076_N = new float[1024];
   private float[] field_175077_O = new float[1024];
   private FloatBuffer field_78521_m = GLAllocation.func_74529_h(16);
   private float field_175080_Q;
   private float field_175082_R;
   private float field_175081_S;
   private float field_78535_ad;
   private float field_78539_ae;
   private int field_175079_V = 0;
   private boolean field_175078_W = false;
   private double field_78503_V = 1.0D;
   private double field_78502_W;
   private double field_78509_X;
   private ShaderGroup field_147707_d;
   private static final ResourceLocation[] field_147712_ad = new ResourceLocation[]{new ResourceLocation("shaders/post/notch.json"), new ResourceLocation("shaders/post/fxaa.json"), new ResourceLocation("shaders/post/art.json"), new ResourceLocation("shaders/post/bumpy.json"), new ResourceLocation("shaders/post/blobs2.json"), new ResourceLocation("shaders/post/pencil.json"), new ResourceLocation("shaders/post/color_convolve.json"), new ResourceLocation("shaders/post/deconverge.json"), new ResourceLocation("shaders/post/flip.json"), new ResourceLocation("shaders/post/invert.json"), new ResourceLocation("shaders/post/ntsc.json"), new ResourceLocation("shaders/post/outline.json"), new ResourceLocation("shaders/post/phosphor.json"), new ResourceLocation("shaders/post/scan_pincushion.json"), new ResourceLocation("shaders/post/sobel.json"), new ResourceLocation("shaders/post/bits.json"), new ResourceLocation("shaders/post/desaturate.json"), new ResourceLocation("shaders/post/green.json"), new ResourceLocation("shaders/post/blur.json"), new ResourceLocation("shaders/post/wobble.json"), new ResourceLocation("shaders/post/blobs.json"), new ResourceLocation("shaders/post/antialias.json"), new ResourceLocation("shaders/post/creeper.json"), new ResourceLocation("shaders/post/spider.json")};
   public static final int field_147708_e = field_147712_ad.length;
   private int field_147713_ae;
   private boolean field_175083_ad;
   private int field_175084_ae;

   public EntityRenderer(Minecraft p_i45076_1_, IResourceManager p_i45076_2_) {
      this.field_147713_ae = field_147708_e;
      this.field_175083_ad = false;
      this.field_175084_ae = 0;
      this.field_78531_r = p_i45076_1_;
      this.field_147711_ac = p_i45076_2_;
      this.field_78516_c = p_i45076_1_.func_175597_ag();
      this.field_147709_v = new MapItemRenderer(p_i45076_1_.func_110434_K());
      this.field_78513_d = new DynamicTexture(16, 16);
      this.field_110922_T = p_i45076_1_.func_110434_K().func_110578_a("lightMap", this.field_78513_d);
      this.field_78504_Q = this.field_78513_d.func_110565_c();
      this.field_147707_d = null;

      for(int lvt_3_1_ = 0; lvt_3_1_ < 32; ++lvt_3_1_) {
         for(int lvt_4_1_ = 0; lvt_4_1_ < 32; ++lvt_4_1_) {
            float lvt_5_1_ = (float)(lvt_4_1_ - 16);
            float lvt_6_1_ = (float)(lvt_3_1_ - 16);
            float lvt_7_1_ = MathHelper.func_76129_c(lvt_5_1_ * lvt_5_1_ + lvt_6_1_ * lvt_6_1_);
            this.field_175076_N[lvt_3_1_ << 5 | lvt_4_1_] = -lvt_6_1_ / lvt_7_1_;
            this.field_175077_O[lvt_3_1_ << 5 | lvt_4_1_] = lvt_5_1_ / lvt_7_1_;
         }
      }

   }

   public boolean func_147702_a() {
      return OpenGlHelper.field_148824_g && this.field_147707_d != null;
   }

   public void func_181022_b() {
      if(this.field_147707_d != null) {
         this.field_147707_d.func_148021_a();
      }

      this.field_147707_d = null;
      this.field_147713_ae = field_147708_e;
   }

   public void func_175071_c() {
      this.field_175083_ad = !this.field_175083_ad;
   }

   public void func_175066_a(Entity p_175066_1_) {
      if(OpenGlHelper.field_148824_g) {
         if(this.field_147707_d != null) {
            this.field_147707_d.func_148021_a();
         }

         this.field_147707_d = null;
         if(p_175066_1_ instanceof EntityCreeper) {
            this.func_175069_a(new ResourceLocation("shaders/post/creeper.json"));
         } else if(p_175066_1_ instanceof EntitySpider) {
            this.func_175069_a(new ResourceLocation("shaders/post/spider.json"));
         } else if(p_175066_1_ instanceof EntityEnderman) {
            this.func_175069_a(new ResourceLocation("shaders/post/invert.json"));
         }

      }
   }

   public void func_147705_c() {
      if(OpenGlHelper.field_148824_g) {
         if(this.field_78531_r.func_175606_aa() instanceof EntityPlayer) {
            if(this.field_147707_d != null) {
               this.field_147707_d.func_148021_a();
            }

            this.field_147713_ae = (this.field_147713_ae + 1) % (field_147712_ad.length + 1);
            if(this.field_147713_ae != field_147708_e) {
               this.func_175069_a(field_147712_ad[this.field_147713_ae]);
            } else {
               this.field_147707_d = null;
            }

         }
      }
   }

   private void func_175069_a(ResourceLocation p_175069_1_) {
      try {
         this.field_147707_d = new ShaderGroup(this.field_78531_r.func_110434_K(), this.field_147711_ac, this.field_78531_r.func_147110_a(), p_175069_1_);
         this.field_147707_d.func_148026_a(this.field_78531_r.field_71443_c, this.field_78531_r.field_71440_d);
         this.field_175083_ad = true;
      } catch (IOException var3) {
         field_147710_q.warn("Failed to load shader: " + p_175069_1_, var3);
         this.field_147713_ae = field_147708_e;
         this.field_175083_ad = false;
      } catch (JsonSyntaxException var4) {
         field_147710_q.warn("Failed to load shader: " + p_175069_1_, var4);
         this.field_147713_ae = field_147708_e;
         this.field_175083_ad = false;
      }

   }

   public void func_110549_a(IResourceManager p_110549_1_) {
      if(this.field_147707_d != null) {
         this.field_147707_d.func_148021_a();
      }

      this.field_147707_d = null;
      if(this.field_147713_ae != field_147708_e) {
         this.func_175069_a(field_147712_ad[this.field_147713_ae]);
      } else {
         this.func_175066_a(this.field_78531_r.func_175606_aa());
      }

   }

   public void func_78464_a() {
      if(OpenGlHelper.field_148824_g && ShaderLinkHelper.func_148074_b() == null) {
         ShaderLinkHelper.func_148076_a();
      }

      this.func_78477_e();
      this.func_78470_f();
      this.field_78535_ad = this.field_78539_ae;
      this.field_78491_C = this.field_78490_B;
      if(this.field_78531_r.field_71474_y.field_74326_T) {
         float lvt_1_1_ = this.field_78531_r.field_71474_y.field_74341_c * 0.6F + 0.2F;
         float lvt_2_1_ = lvt_1_1_ * lvt_1_1_ * lvt_1_1_ * 8.0F;
         this.field_78498_J = this.field_78527_v.func_76333_a(this.field_78496_H, 0.05F * lvt_2_1_);
         this.field_78499_K = this.field_78526_w.func_76333_a(this.field_78497_I, 0.05F * lvt_2_1_);
         this.field_78492_L = 0.0F;
         this.field_78496_H = 0.0F;
         this.field_78497_I = 0.0F;
      } else {
         this.field_78498_J = 0.0F;
         this.field_78499_K = 0.0F;
         this.field_78527_v.func_180179_a();
         this.field_78526_w.func_180179_a();
      }

      if(this.field_78531_r.func_175606_aa() == null) {
         this.field_78531_r.func_175607_a(this.field_78531_r.field_71439_g);
      }

      float lvt_1_2_ = this.field_78531_r.field_71441_e.func_175724_o(new BlockPos(this.field_78531_r.func_175606_aa()));
      float lvt_2_2_ = (float)this.field_78531_r.field_71474_y.field_151451_c / 32.0F;
      float lvt_3_1_ = lvt_1_2_ * (1.0F - lvt_2_2_) + lvt_2_2_;
      this.field_78539_ae += (lvt_3_1_ - this.field_78539_ae) * 0.1F;
      ++this.field_78529_t;
      this.field_78516_c.func_78441_a();
      this.func_78484_h();
      this.field_82832_V = this.field_82831_U;
      if(BossStatus.field_82825_d) {
         this.field_82831_U += 0.05F;
         if(this.field_82831_U > 1.0F) {
            this.field_82831_U = 1.0F;
         }

         BossStatus.field_82825_d = false;
      } else if(this.field_82831_U > 0.0F) {
         this.field_82831_U -= 0.0125F;
      }

   }

   public ShaderGroup func_147706_e() {
      return this.field_147707_d;
   }

   public void func_147704_a(int p_147704_1_, int p_147704_2_) {
      if(OpenGlHelper.field_148824_g) {
         if(this.field_147707_d != null) {
            this.field_147707_d.func_148026_a(p_147704_1_, p_147704_2_);
         }

         this.field_78531_r.field_71438_f.func_72720_a(p_147704_1_, p_147704_2_);
      }
   }

   public void func_78473_a(float p_78473_1_) {
      Entity lvt_2_1_ = this.field_78531_r.func_175606_aa();
      if(lvt_2_1_ != null) {
         if(this.field_78531_r.field_71441_e != null) {
            this.field_78531_r.field_71424_I.func_76320_a("pick");
            this.field_78531_r.field_147125_j = null;
            double lvt_3_1_ = (double)this.field_78531_r.field_71442_b.func_78757_d();
            this.field_78531_r.field_71476_x = lvt_2_1_.func_174822_a(lvt_3_1_, p_78473_1_);
            double lvt_5_1_ = lvt_3_1_;
            Vec3 lvt_7_1_ = lvt_2_1_.func_174824_e(p_78473_1_);
            boolean lvt_8_1_ = false;
            int lvt_9_1_ = 3;
            if(this.field_78531_r.field_71442_b.func_78749_i()) {
               lvt_3_1_ = 6.0D;
               lvt_5_1_ = 6.0D;
            } else {
               if(lvt_3_1_ > 3.0D) {
                  lvt_8_1_ = true;
               }

               lvt_3_1_ = lvt_3_1_;
            }

            if(this.field_78531_r.field_71476_x != null) {
               lvt_5_1_ = this.field_78531_r.field_71476_x.field_72307_f.func_72438_d(lvt_7_1_);
            }

            Vec3 lvt_10_1_ = lvt_2_1_.func_70676_i(p_78473_1_);
            Vec3 lvt_11_1_ = lvt_7_1_.func_72441_c(lvt_10_1_.field_72450_a * lvt_3_1_, lvt_10_1_.field_72448_b * lvt_3_1_, lvt_10_1_.field_72449_c * lvt_3_1_);
            this.field_78528_u = null;
            Vec3 lvt_12_1_ = null;
            float lvt_13_1_ = 1.0F;
            List<Entity> lvt_14_1_ = this.field_78531_r.field_71441_e.func_175674_a(lvt_2_1_, lvt_2_1_.func_174813_aQ().func_72321_a(lvt_10_1_.field_72450_a * lvt_3_1_, lvt_10_1_.field_72448_b * lvt_3_1_, lvt_10_1_.field_72449_c * lvt_3_1_).func_72314_b((double)lvt_13_1_, (double)lvt_13_1_, (double)lvt_13_1_), Predicates.and(EntitySelectors.field_180132_d, new Predicate<Entity>() {
               public boolean apply(Entity p_apply_1_) {
                  return p_apply_1_.func_70067_L();
               }

               // $FF: synthetic method
               public boolean apply(Object p_apply_1_) {
                  return this.apply((Entity)p_apply_1_);
               }
            }));
            double lvt_15_1_ = lvt_5_1_;

            for(int lvt_17_1_ = 0; lvt_17_1_ < lvt_14_1_.size(); ++lvt_17_1_) {
               Entity lvt_18_1_ = (Entity)lvt_14_1_.get(lvt_17_1_);
               float lvt_19_1_ = lvt_18_1_.func_70111_Y();
               AxisAlignedBB lvt_20_1_ = lvt_18_1_.func_174813_aQ().func_72314_b((double)lvt_19_1_, (double)lvt_19_1_, (double)lvt_19_1_);
               MovingObjectPosition lvt_21_1_ = lvt_20_1_.func_72327_a(lvt_7_1_, lvt_11_1_);
               if(lvt_20_1_.func_72318_a(lvt_7_1_)) {
                  if(lvt_15_1_ >= 0.0D) {
                     this.field_78528_u = lvt_18_1_;
                     lvt_12_1_ = lvt_21_1_ == null?lvt_7_1_:lvt_21_1_.field_72307_f;
                     lvt_15_1_ = 0.0D;
                  }
               } else if(lvt_21_1_ != null) {
                  double lvt_22_1_ = lvt_7_1_.func_72438_d(lvt_21_1_.field_72307_f);
                  if(lvt_22_1_ < lvt_15_1_ || lvt_15_1_ == 0.0D) {
                     if(lvt_18_1_ == lvt_2_1_.field_70154_o) {
                        if(lvt_15_1_ == 0.0D) {
                           this.field_78528_u = lvt_18_1_;
                           lvt_12_1_ = lvt_21_1_.field_72307_f;
                        }
                     } else {
                        this.field_78528_u = lvt_18_1_;
                        lvt_12_1_ = lvt_21_1_.field_72307_f;
                        lvt_15_1_ = lvt_22_1_;
                     }
                  }
               }
            }

            if(this.field_78528_u != null && lvt_8_1_ && lvt_7_1_.func_72438_d(lvt_12_1_) > 3.0D) {
               this.field_78528_u = null;
               this.field_78531_r.field_71476_x = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, lvt_12_1_, (EnumFacing)null, new BlockPos(lvt_12_1_));
            }

            if(this.field_78528_u != null && (lvt_15_1_ < lvt_5_1_ || this.field_78531_r.field_71476_x == null)) {
               this.field_78531_r.field_71476_x = new MovingObjectPosition(this.field_78528_u, lvt_12_1_);
               if(this.field_78528_u instanceof EntityLivingBase || this.field_78528_u instanceof EntityItemFrame) {
                  this.field_78531_r.field_147125_j = this.field_78528_u;
               }
            }

            this.field_78531_r.field_71424_I.func_76319_b();
         }
      }
   }

   private void func_78477_e() {
      float lvt_1_1_ = 1.0F;
      if(this.field_78531_r.func_175606_aa() instanceof AbstractClientPlayer) {
         AbstractClientPlayer lvt_2_1_ = (AbstractClientPlayer)this.field_78531_r.func_175606_aa();
         lvt_1_1_ = lvt_2_1_.func_175156_o();
      }

      this.field_78506_S = this.field_78507_R;
      this.field_78507_R += (lvt_1_1_ - this.field_78507_R) * 0.5F;
      if(this.field_78507_R > 1.5F) {
         this.field_78507_R = 1.5F;
      }

      if(this.field_78507_R < 0.1F) {
         this.field_78507_R = 0.1F;
      }

   }

   private float func_78481_a(float p_78481_1_, boolean p_78481_2_) {
      if(this.field_175078_W) {
         return 90.0F;
      } else {
         Entity lvt_3_1_ = this.field_78531_r.func_175606_aa();
         float lvt_4_1_ = 70.0F;
         if(p_78481_2_) {
            lvt_4_1_ = this.field_78531_r.field_71474_y.field_74334_X;
            lvt_4_1_ = lvt_4_1_ * (this.field_78506_S + (this.field_78507_R - this.field_78506_S) * p_78481_1_);
         }

         if(lvt_3_1_ instanceof EntityLivingBase && ((EntityLivingBase)lvt_3_1_).func_110143_aJ() <= 0.0F) {
            float lvt_5_1_ = (float)((EntityLivingBase)lvt_3_1_).field_70725_aQ + p_78481_1_;
            lvt_4_1_ /= (1.0F - 500.0F / (lvt_5_1_ + 500.0F)) * 2.0F + 1.0F;
         }

         Block lvt_5_2_ = ActiveRenderInfo.func_180786_a(this.field_78531_r.field_71441_e, lvt_3_1_, p_78481_1_);
         if(lvt_5_2_.func_149688_o() == Material.field_151586_h) {
            lvt_4_1_ = lvt_4_1_ * 60.0F / 70.0F;
         }

         return lvt_4_1_;
      }
   }

   private void func_78482_e(float p_78482_1_) {
      if(this.field_78531_r.func_175606_aa() instanceof EntityLivingBase) {
         EntityLivingBase lvt_2_1_ = (EntityLivingBase)this.field_78531_r.func_175606_aa();
         float lvt_3_1_ = (float)lvt_2_1_.field_70737_aN - p_78482_1_;
         if(lvt_2_1_.func_110143_aJ() <= 0.0F) {
            float lvt_4_1_ = (float)lvt_2_1_.field_70725_aQ + p_78482_1_;
            GlStateManager.func_179114_b(40.0F - 8000.0F / (lvt_4_1_ + 200.0F), 0.0F, 0.0F, 1.0F);
         }

         if(lvt_3_1_ < 0.0F) {
            return;
         }

         lvt_3_1_ = lvt_3_1_ / (float)lvt_2_1_.field_70738_aO;
         lvt_3_1_ = MathHelper.func_76126_a(lvt_3_1_ * lvt_3_1_ * lvt_3_1_ * lvt_3_1_ * 3.1415927F);
         float lvt_4_2_ = lvt_2_1_.field_70739_aP;
         GlStateManager.func_179114_b(-lvt_4_2_, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b(-lvt_3_1_ * 14.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.func_179114_b(lvt_4_2_, 0.0F, 1.0F, 0.0F);
      }

   }

   private void func_78475_f(float p_78475_1_) {
      if(this.field_78531_r.func_175606_aa() instanceof EntityPlayer) {
         EntityPlayer lvt_2_1_ = (EntityPlayer)this.field_78531_r.func_175606_aa();
         float lvt_3_1_ = lvt_2_1_.field_70140_Q - lvt_2_1_.field_70141_P;
         float lvt_4_1_ = -(lvt_2_1_.field_70140_Q + lvt_3_1_ * p_78475_1_);
         float lvt_5_1_ = lvt_2_1_.field_71107_bF + (lvt_2_1_.field_71109_bG - lvt_2_1_.field_71107_bF) * p_78475_1_;
         float lvt_6_1_ = lvt_2_1_.field_70727_aS + (lvt_2_1_.field_70726_aT - lvt_2_1_.field_70727_aS) * p_78475_1_;
         GlStateManager.func_179109_b(MathHelper.func_76126_a(lvt_4_1_ * 3.1415927F) * lvt_5_1_ * 0.5F, -Math.abs(MathHelper.func_76134_b(lvt_4_1_ * 3.1415927F) * lvt_5_1_), 0.0F);
         GlStateManager.func_179114_b(MathHelper.func_76126_a(lvt_4_1_ * 3.1415927F) * lvt_5_1_ * 3.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.func_179114_b(Math.abs(MathHelper.func_76134_b(lvt_4_1_ * 3.1415927F - 0.2F) * lvt_5_1_) * 5.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.func_179114_b(lvt_6_1_, 1.0F, 0.0F, 0.0F);
      }
   }

   private void func_78467_g(float p_78467_1_) {
      Entity lvt_2_1_ = this.field_78531_r.func_175606_aa();
      float lvt_3_1_ = lvt_2_1_.func_70047_e();
      double lvt_4_1_ = lvt_2_1_.field_70169_q + (lvt_2_1_.field_70165_t - lvt_2_1_.field_70169_q) * (double)p_78467_1_;
      double lvt_6_1_ = lvt_2_1_.field_70167_r + (lvt_2_1_.field_70163_u - lvt_2_1_.field_70167_r) * (double)p_78467_1_ + (double)lvt_3_1_;
      double lvt_8_1_ = lvt_2_1_.field_70166_s + (lvt_2_1_.field_70161_v - lvt_2_1_.field_70166_s) * (double)p_78467_1_;
      if(lvt_2_1_ instanceof EntityLivingBase && ((EntityLivingBase)lvt_2_1_).func_70608_bn()) {
         lvt_3_1_ = (float)((double)lvt_3_1_ + 1.0D);
         GlStateManager.func_179109_b(0.0F, 0.3F, 0.0F);
         if(!this.field_78531_r.field_71474_y.field_74325_U) {
            BlockPos lvt_10_1_ = new BlockPos(lvt_2_1_);
            IBlockState lvt_11_1_ = this.field_78531_r.field_71441_e.func_180495_p(lvt_10_1_);
            Block lvt_12_1_ = lvt_11_1_.func_177230_c();
            if(lvt_12_1_ == Blocks.field_150324_C) {
               int lvt_13_1_ = ((EnumFacing)lvt_11_1_.func_177229_b(BlockBed.field_176387_N)).func_176736_b();
               GlStateManager.func_179114_b((float)(lvt_13_1_ * 90), 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.func_179114_b(lvt_2_1_.field_70126_B + (lvt_2_1_.field_70177_z - lvt_2_1_.field_70126_B) * p_78467_1_ + 180.0F, 0.0F, -1.0F, 0.0F);
            GlStateManager.func_179114_b(lvt_2_1_.field_70127_C + (lvt_2_1_.field_70125_A - lvt_2_1_.field_70127_C) * p_78467_1_, -1.0F, 0.0F, 0.0F);
         }
      } else if(this.field_78531_r.field_71474_y.field_74320_O > 0) {
         double lvt_10_2_ = (double)(this.field_78491_C + (this.field_78490_B - this.field_78491_C) * p_78467_1_);
         if(this.field_78531_r.field_71474_y.field_74325_U) {
            GlStateManager.func_179109_b(0.0F, 0.0F, (float)(-lvt_10_2_));
         } else {
            float lvt_12_2_ = lvt_2_1_.field_70177_z;
            float lvt_13_2_ = lvt_2_1_.field_70125_A;
            if(this.field_78531_r.field_71474_y.field_74320_O == 2) {
               lvt_13_2_ += 180.0F;
            }

            double lvt_14_1_ = (double)(-MathHelper.func_76126_a(lvt_12_2_ / 180.0F * 3.1415927F) * MathHelper.func_76134_b(lvt_13_2_ / 180.0F * 3.1415927F)) * lvt_10_2_;
            double lvt_16_1_ = (double)(MathHelper.func_76134_b(lvt_12_2_ / 180.0F * 3.1415927F) * MathHelper.func_76134_b(lvt_13_2_ / 180.0F * 3.1415927F)) * lvt_10_2_;
            double lvt_18_1_ = (double)(-MathHelper.func_76126_a(lvt_13_2_ / 180.0F * 3.1415927F)) * lvt_10_2_;

            for(int lvt_20_1_ = 0; lvt_20_1_ < 8; ++lvt_20_1_) {
               float lvt_21_1_ = (float)((lvt_20_1_ & 1) * 2 - 1);
               float lvt_22_1_ = (float)((lvt_20_1_ >> 1 & 1) * 2 - 1);
               float lvt_23_1_ = (float)((lvt_20_1_ >> 2 & 1) * 2 - 1);
               lvt_21_1_ = lvt_21_1_ * 0.1F;
               lvt_22_1_ = lvt_22_1_ * 0.1F;
               lvt_23_1_ = lvt_23_1_ * 0.1F;
               MovingObjectPosition lvt_24_1_ = this.field_78531_r.field_71441_e.func_72933_a(new Vec3(lvt_4_1_ + (double)lvt_21_1_, lvt_6_1_ + (double)lvt_22_1_, lvt_8_1_ + (double)lvt_23_1_), new Vec3(lvt_4_1_ - lvt_14_1_ + (double)lvt_21_1_ + (double)lvt_23_1_, lvt_6_1_ - lvt_18_1_ + (double)lvt_22_1_, lvt_8_1_ - lvt_16_1_ + (double)lvt_23_1_));
               if(lvt_24_1_ != null) {
                  double lvt_25_1_ = lvt_24_1_.field_72307_f.func_72438_d(new Vec3(lvt_4_1_, lvt_6_1_, lvt_8_1_));
                  if(lvt_25_1_ < lvt_10_2_) {
                     lvt_10_2_ = lvt_25_1_;
                  }
               }
            }

            if(this.field_78531_r.field_71474_y.field_74320_O == 2) {
               GlStateManager.func_179114_b(180.0F, 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.func_179114_b(lvt_2_1_.field_70125_A - lvt_13_2_, 1.0F, 0.0F, 0.0F);
            GlStateManager.func_179114_b(lvt_2_1_.field_70177_z - lvt_12_2_, 0.0F, 1.0F, 0.0F);
            GlStateManager.func_179109_b(0.0F, 0.0F, (float)(-lvt_10_2_));
            GlStateManager.func_179114_b(lvt_12_2_ - lvt_2_1_.field_70177_z, 0.0F, 1.0F, 0.0F);
            GlStateManager.func_179114_b(lvt_13_2_ - lvt_2_1_.field_70125_A, 1.0F, 0.0F, 0.0F);
         }
      } else {
         GlStateManager.func_179109_b(0.0F, 0.0F, -0.1F);
      }

      if(!this.field_78531_r.field_71474_y.field_74325_U) {
         GlStateManager.func_179114_b(lvt_2_1_.field_70127_C + (lvt_2_1_.field_70125_A - lvt_2_1_.field_70127_C) * p_78467_1_, 1.0F, 0.0F, 0.0F);
         if(lvt_2_1_ instanceof EntityAnimal) {
            EntityAnimal lvt_10_3_ = (EntityAnimal)lvt_2_1_;
            GlStateManager.func_179114_b(lvt_10_3_.field_70758_at + (lvt_10_3_.field_70759_as - lvt_10_3_.field_70758_at) * p_78467_1_ + 180.0F, 0.0F, 1.0F, 0.0F);
         } else {
            GlStateManager.func_179114_b(lvt_2_1_.field_70126_B + (lvt_2_1_.field_70177_z - lvt_2_1_.field_70126_B) * p_78467_1_ + 180.0F, 0.0F, 1.0F, 0.0F);
         }
      }

      GlStateManager.func_179109_b(0.0F, -lvt_3_1_, 0.0F);
      lvt_4_1_ = lvt_2_1_.field_70169_q + (lvt_2_1_.field_70165_t - lvt_2_1_.field_70169_q) * (double)p_78467_1_;
      lvt_6_1_ = lvt_2_1_.field_70167_r + (lvt_2_1_.field_70163_u - lvt_2_1_.field_70167_r) * (double)p_78467_1_ + (double)lvt_3_1_;
      lvt_8_1_ = lvt_2_1_.field_70166_s + (lvt_2_1_.field_70161_v - lvt_2_1_.field_70166_s) * (double)p_78467_1_;
      this.field_78500_U = this.field_78531_r.field_71438_f.func_72721_a(lvt_4_1_, lvt_6_1_, lvt_8_1_, p_78467_1_);
   }

   private void func_78479_a(float p_78479_1_, int p_78479_2_) {
      this.field_78530_s = (float)(this.field_78531_r.field_71474_y.field_151451_c * 16);
      GlStateManager.func_179128_n(5889);
      GlStateManager.func_179096_D();
      float lvt_3_1_ = 0.07F;
      if(this.field_78531_r.field_71474_y.field_74337_g) {
         GlStateManager.func_179109_b((float)(-(p_78479_2_ * 2 - 1)) * lvt_3_1_, 0.0F, 0.0F);
      }

      if(this.field_78503_V != 1.0D) {
         GlStateManager.func_179109_b((float)this.field_78502_W, (float)(-this.field_78509_X), 0.0F);
         GlStateManager.func_179139_a(this.field_78503_V, this.field_78503_V, 1.0D);
      }

      Project.gluPerspective(this.func_78481_a(p_78479_1_, true), (float)this.field_78531_r.field_71443_c / (float)this.field_78531_r.field_71440_d, 0.05F, this.field_78530_s * MathHelper.field_180189_a);
      GlStateManager.func_179128_n(5888);
      GlStateManager.func_179096_D();
      if(this.field_78531_r.field_71474_y.field_74337_g) {
         GlStateManager.func_179109_b((float)(p_78479_2_ * 2 - 1) * 0.1F, 0.0F, 0.0F);
      }

      this.func_78482_e(p_78479_1_);
      if(this.field_78531_r.field_71474_y.field_74336_f) {
         this.func_78475_f(p_78479_1_);
      }

      float lvt_4_1_ = this.field_78531_r.field_71439_g.field_71080_cy + (this.field_78531_r.field_71439_g.field_71086_bY - this.field_78531_r.field_71439_g.field_71080_cy) * p_78479_1_;
      if(lvt_4_1_ > 0.0F) {
         int lvt_5_1_ = 20;
         if(this.field_78531_r.field_71439_g.func_70644_a(Potion.field_76431_k)) {
            lvt_5_1_ = 7;
         }

         float lvt_6_1_ = 5.0F / (lvt_4_1_ * lvt_4_1_ + 5.0F) - lvt_4_1_ * 0.04F;
         lvt_6_1_ = lvt_6_1_ * lvt_6_1_;
         GlStateManager.func_179114_b(((float)this.field_78529_t + p_78479_1_) * (float)lvt_5_1_, 0.0F, 1.0F, 1.0F);
         GlStateManager.func_179152_a(1.0F / lvt_6_1_, 1.0F, 1.0F);
         GlStateManager.func_179114_b(-((float)this.field_78529_t + p_78479_1_) * (float)lvt_5_1_, 0.0F, 1.0F, 1.0F);
      }

      this.func_78467_g(p_78479_1_);
      if(this.field_175078_W) {
         switch(this.field_175079_V) {
         case 0:
            GlStateManager.func_179114_b(90.0F, 0.0F, 1.0F, 0.0F);
            break;
         case 1:
            GlStateManager.func_179114_b(180.0F, 0.0F, 1.0F, 0.0F);
            break;
         case 2:
            GlStateManager.func_179114_b(-90.0F, 0.0F, 1.0F, 0.0F);
            break;
         case 3:
            GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
            break;
         case 4:
            GlStateManager.func_179114_b(-90.0F, 1.0F, 0.0F, 0.0F);
         }
      }

   }

   private void func_78476_b(float p_78476_1_, int p_78476_2_) {
      if(!this.field_175078_W) {
         GlStateManager.func_179128_n(5889);
         GlStateManager.func_179096_D();
         float lvt_3_1_ = 0.07F;
         if(this.field_78531_r.field_71474_y.field_74337_g) {
            GlStateManager.func_179109_b((float)(-(p_78476_2_ * 2 - 1)) * lvt_3_1_, 0.0F, 0.0F);
         }

         Project.gluPerspective(this.func_78481_a(p_78476_1_, false), (float)this.field_78531_r.field_71443_c / (float)this.field_78531_r.field_71440_d, 0.05F, this.field_78530_s * 2.0F);
         GlStateManager.func_179128_n(5888);
         GlStateManager.func_179096_D();
         if(this.field_78531_r.field_71474_y.field_74337_g) {
            GlStateManager.func_179109_b((float)(p_78476_2_ * 2 - 1) * 0.1F, 0.0F, 0.0F);
         }

         GlStateManager.func_179094_E();
         this.func_78482_e(p_78476_1_);
         if(this.field_78531_r.field_71474_y.field_74336_f) {
            this.func_78475_f(p_78476_1_);
         }

         boolean lvt_4_1_ = this.field_78531_r.func_175606_aa() instanceof EntityLivingBase && ((EntityLivingBase)this.field_78531_r.func_175606_aa()).func_70608_bn();
         if(this.field_78531_r.field_71474_y.field_74320_O == 0 && !lvt_4_1_ && !this.field_78531_r.field_71474_y.field_74319_N && !this.field_78531_r.field_71442_b.func_78747_a()) {
            this.func_180436_i();
            this.field_78516_c.func_78440_a(p_78476_1_);
            this.func_175072_h();
         }

         GlStateManager.func_179121_F();
         if(this.field_78531_r.field_71474_y.field_74320_O == 0 && !lvt_4_1_) {
            this.field_78516_c.func_78447_b(p_78476_1_);
            this.func_78482_e(p_78476_1_);
         }

         if(this.field_78531_r.field_71474_y.field_74336_f) {
            this.func_78475_f(p_78476_1_);
         }

      }
   }

   public void func_175072_h() {
      GlStateManager.func_179138_g(OpenGlHelper.field_77476_b);
      GlStateManager.func_179090_x();
      GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
   }

   public void func_180436_i() {
      GlStateManager.func_179138_g(OpenGlHelper.field_77476_b);
      GlStateManager.func_179128_n(5890);
      GlStateManager.func_179096_D();
      float lvt_1_1_ = 0.00390625F;
      GlStateManager.func_179152_a(lvt_1_1_, lvt_1_1_, lvt_1_1_);
      GlStateManager.func_179109_b(8.0F, 8.0F, 8.0F);
      GlStateManager.func_179128_n(5888);
      this.field_78531_r.func_110434_K().func_110577_a(this.field_110922_T);
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glTexParameteri(3553, 10242, 10496);
      GL11.glTexParameteri(3553, 10243, 10496);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179098_w();
      GlStateManager.func_179138_g(OpenGlHelper.field_77478_a);
   }

   private void func_78470_f() {
      this.field_175075_L = (float)((double)this.field_175075_L + (Math.random() - Math.random()) * Math.random() * Math.random());
      this.field_175075_L = (float)((double)this.field_175075_L * 0.9D);
      this.field_78514_e += (this.field_175075_L - this.field_78514_e) * 1.0F;
      this.field_78536_aa = true;
   }

   private void func_78472_g(float p_78472_1_) {
      if(this.field_78536_aa) {
         this.field_78531_r.field_71424_I.func_76320_a("lightTex");
         World lvt_2_1_ = this.field_78531_r.field_71441_e;
         if(lvt_2_1_ != null) {
            float lvt_3_1_ = lvt_2_1_.func_72971_b(1.0F);
            float lvt_4_1_ = lvt_3_1_ * 0.95F + 0.05F;

            for(int lvt_5_1_ = 0; lvt_5_1_ < 256; ++lvt_5_1_) {
               float lvt_6_1_ = lvt_2_1_.field_73011_w.func_177497_p()[lvt_5_1_ / 16] * lvt_4_1_;
               float lvt_7_1_ = lvt_2_1_.field_73011_w.func_177497_p()[lvt_5_1_ % 16] * (this.field_78514_e * 0.1F + 1.5F);
               if(lvt_2_1_.func_175658_ac() > 0) {
                  lvt_6_1_ = lvt_2_1_.field_73011_w.func_177497_p()[lvt_5_1_ / 16];
               }

               float lvt_8_1_ = lvt_6_1_ * (lvt_3_1_ * 0.65F + 0.35F);
               float lvt_9_1_ = lvt_6_1_ * (lvt_3_1_ * 0.65F + 0.35F);
               float lvt_12_1_ = lvt_7_1_ * ((lvt_7_1_ * 0.6F + 0.4F) * 0.6F + 0.4F);
               float lvt_13_1_ = lvt_7_1_ * (lvt_7_1_ * lvt_7_1_ * 0.6F + 0.4F);
               float lvt_14_1_ = lvt_8_1_ + lvt_7_1_;
               float lvt_15_1_ = lvt_9_1_ + lvt_12_1_;
               float lvt_16_1_ = lvt_6_1_ + lvt_13_1_;
               lvt_14_1_ = lvt_14_1_ * 0.96F + 0.03F;
               lvt_15_1_ = lvt_15_1_ * 0.96F + 0.03F;
               lvt_16_1_ = lvt_16_1_ * 0.96F + 0.03F;
               if(this.field_82831_U > 0.0F) {
                  float lvt_17_1_ = this.field_82832_V + (this.field_82831_U - this.field_82832_V) * p_78472_1_;
                  lvt_14_1_ = lvt_14_1_ * (1.0F - lvt_17_1_) + lvt_14_1_ * 0.7F * lvt_17_1_;
                  lvt_15_1_ = lvt_15_1_ * (1.0F - lvt_17_1_) + lvt_15_1_ * 0.6F * lvt_17_1_;
                  lvt_16_1_ = lvt_16_1_ * (1.0F - lvt_17_1_) + lvt_16_1_ * 0.6F * lvt_17_1_;
               }

               if(lvt_2_1_.field_73011_w.func_177502_q() == 1) {
                  lvt_14_1_ = 0.22F + lvt_7_1_ * 0.75F;
                  lvt_15_1_ = 0.28F + lvt_12_1_ * 0.75F;
                  lvt_16_1_ = 0.25F + lvt_13_1_ * 0.75F;
               }

               if(this.field_78531_r.field_71439_g.func_70644_a(Potion.field_76439_r)) {
                  float lvt_17_2_ = this.func_180438_a(this.field_78531_r.field_71439_g, p_78472_1_);
                  float lvt_18_1_ = 1.0F / lvt_14_1_;
                  if(lvt_18_1_ > 1.0F / lvt_15_1_) {
                     lvt_18_1_ = 1.0F / lvt_15_1_;
                  }

                  if(lvt_18_1_ > 1.0F / lvt_16_1_) {
                     lvt_18_1_ = 1.0F / lvt_16_1_;
                  }

                  lvt_14_1_ = lvt_14_1_ * (1.0F - lvt_17_2_) + lvt_14_1_ * lvt_18_1_ * lvt_17_2_;
                  lvt_15_1_ = lvt_15_1_ * (1.0F - lvt_17_2_) + lvt_15_1_ * lvt_18_1_ * lvt_17_2_;
                  lvt_16_1_ = lvt_16_1_ * (1.0F - lvt_17_2_) + lvt_16_1_ * lvt_18_1_ * lvt_17_2_;
               }

               if(lvt_14_1_ > 1.0F) {
                  lvt_14_1_ = 1.0F;
               }

               if(lvt_15_1_ > 1.0F) {
                  lvt_15_1_ = 1.0F;
               }

               if(lvt_16_1_ > 1.0F) {
                  lvt_16_1_ = 1.0F;
               }

               float lvt_17_3_ = this.field_78531_r.field_71474_y.field_74333_Y;
               float lvt_18_2_ = 1.0F - lvt_14_1_;
               float lvt_19_1_ = 1.0F - lvt_15_1_;
               float lvt_20_1_ = 1.0F - lvt_16_1_;
               lvt_18_2_ = 1.0F - lvt_18_2_ * lvt_18_2_ * lvt_18_2_ * lvt_18_2_;
               lvt_19_1_ = 1.0F - lvt_19_1_ * lvt_19_1_ * lvt_19_1_ * lvt_19_1_;
               lvt_20_1_ = 1.0F - lvt_20_1_ * lvt_20_1_ * lvt_20_1_ * lvt_20_1_;
               lvt_14_1_ = lvt_14_1_ * (1.0F - lvt_17_3_) + lvt_18_2_ * lvt_17_3_;
               lvt_15_1_ = lvt_15_1_ * (1.0F - lvt_17_3_) + lvt_19_1_ * lvt_17_3_;
               lvt_16_1_ = lvt_16_1_ * (1.0F - lvt_17_3_) + lvt_20_1_ * lvt_17_3_;
               lvt_14_1_ = lvt_14_1_ * 0.96F + 0.03F;
               lvt_15_1_ = lvt_15_1_ * 0.96F + 0.03F;
               lvt_16_1_ = lvt_16_1_ * 0.96F + 0.03F;
               if(lvt_14_1_ > 1.0F) {
                  lvt_14_1_ = 1.0F;
               }

               if(lvt_15_1_ > 1.0F) {
                  lvt_15_1_ = 1.0F;
               }

               if(lvt_16_1_ > 1.0F) {
                  lvt_16_1_ = 1.0F;
               }

               if(lvt_14_1_ < 0.0F) {
                  lvt_14_1_ = 0.0F;
               }

               if(lvt_15_1_ < 0.0F) {
                  lvt_15_1_ = 0.0F;
               }

               if(lvt_16_1_ < 0.0F) {
                  lvt_16_1_ = 0.0F;
               }

               int lvt_21_1_ = 255;
               int lvt_22_1_ = (int)(lvt_14_1_ * 255.0F);
               int lvt_23_1_ = (int)(lvt_15_1_ * 255.0F);
               int lvt_24_1_ = (int)(lvt_16_1_ * 255.0F);
               this.field_78504_Q[lvt_5_1_] = lvt_21_1_ << 24 | lvt_22_1_ << 16 | lvt_23_1_ << 8 | lvt_24_1_;
            }

            this.field_78513_d.func_110564_a();
            this.field_78536_aa = false;
            this.field_78531_r.field_71424_I.func_76319_b();
         }
      }
   }

   private float func_180438_a(EntityLivingBase p_180438_1_, float p_180438_2_) {
      int lvt_3_1_ = p_180438_1_.func_70660_b(Potion.field_76439_r).func_76459_b();
      return lvt_3_1_ > 200?1.0F:0.7F + MathHelper.func_76126_a(((float)lvt_3_1_ - p_180438_2_) * 3.1415927F * 0.2F) * 0.3F;
   }

   public void func_181560_a(float p_181560_1_, long p_181560_2_) {
      boolean lvt_4_1_ = Display.isActive();
      if(!lvt_4_1_ && this.field_78531_r.field_71474_y.field_82881_y && (!this.field_78531_r.field_71474_y.field_85185_A || !Mouse.isButtonDown(1))) {
         if(Minecraft.func_71386_F() - this.field_78508_Y > 500L) {
            this.field_78531_r.func_71385_j();
         }
      } else {
         this.field_78508_Y = Minecraft.func_71386_F();
      }

      this.field_78531_r.field_71424_I.func_76320_a("mouse");
      if(lvt_4_1_ && Minecraft.field_142025_a && this.field_78531_r.field_71415_G && !Mouse.isInsideWindow()) {
         Mouse.setGrabbed(false);
         Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
         Mouse.setGrabbed(true);
      }

      if(this.field_78531_r.field_71415_G && lvt_4_1_) {
         this.field_78531_r.field_71417_B.func_74374_c();
         float lvt_5_1_ = this.field_78531_r.field_71474_y.field_74341_c * 0.6F + 0.2F;
         float lvt_6_1_ = lvt_5_1_ * lvt_5_1_ * lvt_5_1_ * 8.0F;
         float lvt_7_1_ = (float)this.field_78531_r.field_71417_B.field_74377_a * lvt_6_1_;
         float lvt_8_1_ = (float)this.field_78531_r.field_71417_B.field_74375_b * lvt_6_1_;
         int lvt_9_1_ = 1;
         if(this.field_78531_r.field_71474_y.field_74338_d) {
            lvt_9_1_ = -1;
         }

         if(this.field_78531_r.field_71474_y.field_74326_T) {
            this.field_78496_H += lvt_7_1_;
            this.field_78497_I += lvt_8_1_;
            float lvt_10_1_ = p_181560_1_ - this.field_78492_L;
            this.field_78492_L = p_181560_1_;
            lvt_7_1_ = this.field_78498_J * lvt_10_1_;
            lvt_8_1_ = this.field_78499_K * lvt_10_1_;
            this.field_78531_r.field_71439_g.func_70082_c(lvt_7_1_, lvt_8_1_ * (float)lvt_9_1_);
         } else {
            this.field_78496_H = 0.0F;
            this.field_78497_I = 0.0F;
            this.field_78531_r.field_71439_g.func_70082_c(lvt_7_1_, lvt_8_1_ * (float)lvt_9_1_);
         }
      }

      this.field_78531_r.field_71424_I.func_76319_b();
      if(!this.field_78531_r.field_71454_w) {
         field_78517_a = this.field_78531_r.field_71474_y.field_74337_g;
         final ScaledResolution lvt_5_2_ = new ScaledResolution(this.field_78531_r);
         int lvt_6_2_ = lvt_5_2_.func_78326_a();
         int lvt_7_2_ = lvt_5_2_.func_78328_b();
         final int lvt_8_2_ = Mouse.getX() * lvt_6_2_ / this.field_78531_r.field_71443_c;
         final int lvt_9_2_ = lvt_7_2_ - Mouse.getY() * lvt_7_2_ / this.field_78531_r.field_71440_d - 1;
         int lvt_10_2_ = this.field_78531_r.field_71474_y.field_74350_i;
         if(this.field_78531_r.field_71441_e != null) {
            this.field_78531_r.field_71424_I.func_76320_a("level");
            int lvt_11_1_ = Math.min(Minecraft.func_175610_ah(), lvt_10_2_);
            lvt_11_1_ = Math.max(lvt_11_1_, 60);
            long lvt_12_1_ = System.nanoTime() - p_181560_2_;
            long lvt_14_1_ = Math.max((long)(1000000000 / lvt_11_1_ / 4) - lvt_12_1_, 0L);
            this.func_78471_a(p_181560_1_, System.nanoTime() + lvt_14_1_);
            if(OpenGlHelper.field_148824_g) {
               this.field_78531_r.field_71438_f.func_174975_c();
               if(this.field_147707_d != null && this.field_175083_ad) {
                  GlStateManager.func_179128_n(5890);
                  GlStateManager.func_179094_E();
                  GlStateManager.func_179096_D();
                  this.field_147707_d.func_148018_a(p_181560_1_);
                  GlStateManager.func_179121_F();
               }

               this.field_78531_r.func_147110_a().func_147610_a(true);
            }

            this.field_78510_Z = System.nanoTime();
            this.field_78531_r.field_71424_I.func_76318_c("gui");
            if(!this.field_78531_r.field_71474_y.field_74319_N || this.field_78531_r.field_71462_r != null) {
               GlStateManager.func_179092_a(516, 0.1F);
               this.field_78531_r.field_71456_v.func_175180_a(p_181560_1_);
            }

            this.field_78531_r.field_71424_I.func_76319_b();
         } else {
            GlStateManager.func_179083_b(0, 0, this.field_78531_r.field_71443_c, this.field_78531_r.field_71440_d);
            GlStateManager.func_179128_n(5889);
            GlStateManager.func_179096_D();
            GlStateManager.func_179128_n(5888);
            GlStateManager.func_179096_D();
            this.func_78478_c();
            this.field_78510_Z = System.nanoTime();
         }

         if(this.field_78531_r.field_71462_r != null) {
            GlStateManager.func_179086_m(256);

            try {
               this.field_78531_r.field_71462_r.func_73863_a(lvt_8_2_, lvt_9_2_, p_181560_1_);
            } catch (Throwable var16) {
               CrashReport lvt_12_2_ = CrashReport.func_85055_a(var16, "Rendering screen");
               CrashReportCategory lvt_13_1_ = lvt_12_2_.func_85058_a("Screen render details");
               lvt_13_1_.func_71500_a("Screen name", new Callable<String>() {
                  public String call() throws Exception {
                     return EntityRenderer.this.field_78531_r.field_71462_r.getClass().getCanonicalName();
                  }

                  // $FF: synthetic method
                  public Object call() throws Exception {
                     return this.call();
                  }
               });
               lvt_13_1_.func_71500_a("Mouse location", new Callable<String>() {
                  public String call() throws Exception {
                     return String.format("Scaled: (%d, %d). Absolute: (%d, %d)", new Object[]{Integer.valueOf(lvt_8_2_), Integer.valueOf(lvt_9_2_), Integer.valueOf(Mouse.getX()), Integer.valueOf(Mouse.getY())});
                  }

                  // $FF: synthetic method
                  public Object call() throws Exception {
                     return this.call();
                  }
               });
               lvt_13_1_.func_71500_a("Screen size", new Callable<String>() {
                  public String call() throws Exception {
                     return String.format("Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %d", new Object[]{Integer.valueOf(lvt_5_2_.func_78326_a()), Integer.valueOf(lvt_5_2_.func_78328_b()), Integer.valueOf(EntityRenderer.this.field_78531_r.field_71443_c), Integer.valueOf(EntityRenderer.this.field_78531_r.field_71440_d), Integer.valueOf(lvt_5_2_.func_78325_e())});
                  }

                  // $FF: synthetic method
                  public Object call() throws Exception {
                     return this.call();
                  }
               });
               throw new ReportedException(lvt_12_2_);
            }
         }

      }
   }

   public void func_152430_c(float p_152430_1_) {
      this.func_78478_c();
      this.field_78531_r.field_71456_v.func_180478_c(new ScaledResolution(this.field_78531_r));
   }

   private boolean func_175070_n() {
      if(!this.field_175073_D) {
         return false;
      } else {
         Entity lvt_1_1_ = this.field_78531_r.func_175606_aa();
         boolean lvt_2_1_ = lvt_1_1_ instanceof EntityPlayer && !this.field_78531_r.field_71474_y.field_74319_N;
         if(lvt_2_1_ && !((EntityPlayer)lvt_1_1_).field_71075_bZ.field_75099_e) {
            ItemStack lvt_3_1_ = ((EntityPlayer)lvt_1_1_).func_71045_bC();
            if(this.field_78531_r.field_71476_x != null && this.field_78531_r.field_71476_x.field_72313_a == MovingObjectPosition.MovingObjectType.BLOCK) {
               BlockPos lvt_4_1_ = this.field_78531_r.field_71476_x.func_178782_a();
               Block lvt_5_1_ = this.field_78531_r.field_71441_e.func_180495_p(lvt_4_1_).func_177230_c();
               if(this.field_78531_r.field_71442_b.func_178889_l() == WorldSettings.GameType.SPECTATOR) {
                  lvt_2_1_ = lvt_5_1_.func_149716_u() && this.field_78531_r.field_71441_e.func_175625_s(lvt_4_1_) instanceof IInventory;
               } else {
                  lvt_2_1_ = lvt_3_1_ != null && (lvt_3_1_.func_179544_c(lvt_5_1_) || lvt_3_1_.func_179547_d(lvt_5_1_));
               }
            }
         }

         return lvt_2_1_;
      }
   }

   private void func_175067_i(float p_175067_1_) {
      if(this.field_78531_r.field_71474_y.field_74330_P && !this.field_78531_r.field_71474_y.field_74319_N && !this.field_78531_r.field_71439_g.func_175140_cp() && !this.field_78531_r.field_71474_y.field_178879_v) {
         Entity lvt_2_1_ = this.field_78531_r.func_175606_aa();
         GlStateManager.func_179147_l();
         GlStateManager.func_179120_a(770, 771, 1, 0);
         GL11.glLineWidth(1.0F);
         GlStateManager.func_179090_x();
         GlStateManager.func_179132_a(false);
         GlStateManager.func_179094_E();
         GlStateManager.func_179128_n(5888);
         GlStateManager.func_179096_D();
         this.func_78467_g(p_175067_1_);
         GlStateManager.func_179109_b(0.0F, lvt_2_1_.func_70047_e(), 0.0F);
         RenderGlobal.func_181563_a(new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.005D, 1.0E-4D, 1.0E-4D), 255, 0, 0, 255);
         RenderGlobal.func_181563_a(new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0E-4D, 1.0E-4D, 0.005D), 0, 0, 255, 255);
         RenderGlobal.func_181563_a(new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0E-4D, 0.0033D, 1.0E-4D), 0, 255, 0, 255);
         GlStateManager.func_179121_F();
         GlStateManager.func_179132_a(true);
         GlStateManager.func_179098_w();
         GlStateManager.func_179084_k();
      }

   }

   public void func_78471_a(float p_78471_1_, long p_78471_2_) {
      this.func_78472_g(p_78471_1_);
      if(this.field_78531_r.func_175606_aa() == null) {
         this.field_78531_r.func_175607_a(this.field_78531_r.field_71439_g);
      }

      this.func_78473_a(p_78471_1_);
      GlStateManager.func_179126_j();
      GlStateManager.func_179141_d();
      GlStateManager.func_179092_a(516, 0.5F);
      this.field_78531_r.field_71424_I.func_76320_a("center");
      if(this.field_78531_r.field_71474_y.field_74337_g) {
         field_78515_b = 0;
         GlStateManager.func_179135_a(false, true, true, false);
         this.func_175068_a(0, p_78471_1_, p_78471_2_);
         field_78515_b = 1;
         GlStateManager.func_179135_a(true, false, false, false);
         this.func_175068_a(1, p_78471_1_, p_78471_2_);
         GlStateManager.func_179135_a(true, true, true, false);
      } else {
         this.func_175068_a(2, p_78471_1_, p_78471_2_);
      }

      this.field_78531_r.field_71424_I.func_76319_b();
   }

   private void func_175068_a(int p_175068_1_, float p_175068_2_, long p_175068_3_) {
      RenderGlobal lvt_5_1_ = this.field_78531_r.field_71438_f;
      EffectRenderer lvt_6_1_ = this.field_78531_r.field_71452_i;
      boolean lvt_7_1_ = this.func_175070_n();
      GlStateManager.func_179089_o();
      this.field_78531_r.field_71424_I.func_76318_c("clear");
      GlStateManager.func_179083_b(0, 0, this.field_78531_r.field_71443_c, this.field_78531_r.field_71440_d);
      this.func_78466_h(p_175068_2_);
      GlStateManager.func_179086_m(16640);
      this.field_78531_r.field_71424_I.func_76318_c("camera");
      this.func_78479_a(p_175068_2_, p_175068_1_);
      ActiveRenderInfo.func_74583_a(this.field_78531_r.field_71439_g, this.field_78531_r.field_71474_y.field_74320_O == 2);
      this.field_78531_r.field_71424_I.func_76318_c("frustum");
      ClippingHelperImpl.func_78558_a();
      this.field_78531_r.field_71424_I.func_76318_c("culling");
      ICamera lvt_8_1_ = new Frustum();
      Entity lvt_9_1_ = this.field_78531_r.func_175606_aa();
      double lvt_10_1_ = lvt_9_1_.field_70142_S + (lvt_9_1_.field_70165_t - lvt_9_1_.field_70142_S) * (double)p_175068_2_;
      double lvt_12_1_ = lvt_9_1_.field_70137_T + (lvt_9_1_.field_70163_u - lvt_9_1_.field_70137_T) * (double)p_175068_2_;
      double lvt_14_1_ = lvt_9_1_.field_70136_U + (lvt_9_1_.field_70161_v - lvt_9_1_.field_70136_U) * (double)p_175068_2_;
      lvt_8_1_.func_78547_a(lvt_10_1_, lvt_12_1_, lvt_14_1_);
      if(this.field_78531_r.field_71474_y.field_151451_c >= 4) {
         this.func_78468_a(-1, p_175068_2_);
         this.field_78531_r.field_71424_I.func_76318_c("sky");
         GlStateManager.func_179128_n(5889);
         GlStateManager.func_179096_D();
         Project.gluPerspective(this.func_78481_a(p_175068_2_, true), (float)this.field_78531_r.field_71443_c / (float)this.field_78531_r.field_71440_d, 0.05F, this.field_78530_s * 2.0F);
         GlStateManager.func_179128_n(5888);
         lvt_5_1_.func_174976_a(p_175068_2_, p_175068_1_);
         GlStateManager.func_179128_n(5889);
         GlStateManager.func_179096_D();
         Project.gluPerspective(this.func_78481_a(p_175068_2_, true), (float)this.field_78531_r.field_71443_c / (float)this.field_78531_r.field_71440_d, 0.05F, this.field_78530_s * MathHelper.field_180189_a);
         GlStateManager.func_179128_n(5888);
      }

      this.func_78468_a(0, p_175068_2_);
      GlStateManager.func_179103_j(7425);
      if(lvt_9_1_.field_70163_u + (double)lvt_9_1_.func_70047_e() < 128.0D) {
         this.func_180437_a(lvt_5_1_, p_175068_2_, p_175068_1_);
      }

      this.field_78531_r.field_71424_I.func_76318_c("prepareterrain");
      this.func_78468_a(0, p_175068_2_);
      this.field_78531_r.func_110434_K().func_110577_a(TextureMap.field_110575_b);
      RenderHelper.func_74518_a();
      this.field_78531_r.field_71424_I.func_76318_c("terrain_setup");
      lvt_5_1_.func_174970_a(lvt_9_1_, (double)p_175068_2_, lvt_8_1_, this.field_175084_ae++, this.field_78531_r.field_71439_g.func_175149_v());
      if(p_175068_1_ == 0 || p_175068_1_ == 2) {
         this.field_78531_r.field_71424_I.func_76318_c("updatechunks");
         this.field_78531_r.field_71438_f.func_174967_a(p_175068_3_);
      }

      this.field_78531_r.field_71424_I.func_76318_c("terrain");
      GlStateManager.func_179128_n(5888);
      GlStateManager.func_179094_E();
      GlStateManager.func_179118_c();
      lvt_5_1_.func_174977_a(EnumWorldBlockLayer.SOLID, (double)p_175068_2_, p_175068_1_, lvt_9_1_);
      GlStateManager.func_179141_d();
      lvt_5_1_.func_174977_a(EnumWorldBlockLayer.CUTOUT_MIPPED, (double)p_175068_2_, p_175068_1_, lvt_9_1_);
      this.field_78531_r.func_110434_K().func_110581_b(TextureMap.field_110575_b).func_174936_b(false, false);
      lvt_5_1_.func_174977_a(EnumWorldBlockLayer.CUTOUT, (double)p_175068_2_, p_175068_1_, lvt_9_1_);
      this.field_78531_r.func_110434_K().func_110581_b(TextureMap.field_110575_b).func_174935_a();
      GlStateManager.func_179103_j(7424);
      GlStateManager.func_179092_a(516, 0.1F);
      if(!this.field_175078_W) {
         GlStateManager.func_179128_n(5888);
         GlStateManager.func_179121_F();
         GlStateManager.func_179094_E();
         RenderHelper.func_74519_b();
         this.field_78531_r.field_71424_I.func_76318_c("entities");
         lvt_5_1_.func_180446_a(lvt_9_1_, lvt_8_1_, p_175068_2_);
         RenderHelper.func_74518_a();
         this.func_175072_h();
         GlStateManager.func_179128_n(5888);
         GlStateManager.func_179121_F();
         GlStateManager.func_179094_E();
         if(this.field_78531_r.field_71476_x != null && lvt_9_1_.func_70055_a(Material.field_151586_h) && lvt_7_1_) {
            EntityPlayer lvt_16_1_ = (EntityPlayer)lvt_9_1_;
            GlStateManager.func_179118_c();
            this.field_78531_r.field_71424_I.func_76318_c("outline");
            lvt_5_1_.func_72731_b(lvt_16_1_, this.field_78531_r.field_71476_x, 0, p_175068_2_);
            GlStateManager.func_179141_d();
         }
      }

      GlStateManager.func_179128_n(5888);
      GlStateManager.func_179121_F();
      if(lvt_7_1_ && this.field_78531_r.field_71476_x != null && !lvt_9_1_.func_70055_a(Material.field_151586_h)) {
         EntityPlayer lvt_16_2_ = (EntityPlayer)lvt_9_1_;
         GlStateManager.func_179118_c();
         this.field_78531_r.field_71424_I.func_76318_c("outline");
         lvt_5_1_.func_72731_b(lvt_16_2_, this.field_78531_r.field_71476_x, 0, p_175068_2_);
         GlStateManager.func_179141_d();
      }

      this.field_78531_r.field_71424_I.func_76318_c("destroyProgress");
      GlStateManager.func_179147_l();
      GlStateManager.func_179120_a(770, 1, 1, 0);
      this.field_78531_r.func_110434_K().func_110581_b(TextureMap.field_110575_b).func_174936_b(false, false);
      lvt_5_1_.func_174981_a(Tessellator.func_178181_a(), Tessellator.func_178181_a().func_178180_c(), lvt_9_1_, p_175068_2_);
      this.field_78531_r.func_110434_K().func_110581_b(TextureMap.field_110575_b).func_174935_a();
      GlStateManager.func_179084_k();
      if(!this.field_175078_W) {
         this.func_180436_i();
         this.field_78531_r.field_71424_I.func_76318_c("litParticles");
         lvt_6_1_.func_78872_b(lvt_9_1_, p_175068_2_);
         RenderHelper.func_74518_a();
         this.func_78468_a(0, p_175068_2_);
         this.field_78531_r.field_71424_I.func_76318_c("particles");
         lvt_6_1_.func_78874_a(lvt_9_1_, p_175068_2_);
         this.func_175072_h();
      }

      GlStateManager.func_179132_a(false);
      GlStateManager.func_179089_o();
      this.field_78531_r.field_71424_I.func_76318_c("weather");
      this.func_78474_d(p_175068_2_);
      GlStateManager.func_179132_a(true);
      lvt_5_1_.func_180449_a(lvt_9_1_, p_175068_2_);
      GlStateManager.func_179084_k();
      GlStateManager.func_179089_o();
      GlStateManager.func_179120_a(770, 771, 1, 0);
      GlStateManager.func_179092_a(516, 0.1F);
      this.func_78468_a(0, p_175068_2_);
      GlStateManager.func_179147_l();
      GlStateManager.func_179132_a(false);
      this.field_78531_r.func_110434_K().func_110577_a(TextureMap.field_110575_b);
      GlStateManager.func_179103_j(7425);
      this.field_78531_r.field_71424_I.func_76318_c("translucent");
      lvt_5_1_.func_174977_a(EnumWorldBlockLayer.TRANSLUCENT, (double)p_175068_2_, p_175068_1_, lvt_9_1_);
      GlStateManager.func_179103_j(7424);
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179089_o();
      GlStateManager.func_179084_k();
      GlStateManager.func_179106_n();
      if(lvt_9_1_.field_70163_u + (double)lvt_9_1_.func_70047_e() >= 128.0D) {
         this.field_78531_r.field_71424_I.func_76318_c("aboveClouds");
         this.func_180437_a(lvt_5_1_, p_175068_2_, p_175068_1_);
      }

      this.field_78531_r.field_71424_I.func_76318_c("hand");
      if(this.field_175074_C) {
         GlStateManager.func_179086_m(256);
         this.func_78476_b(p_175068_2_, p_175068_1_);
         this.func_175067_i(p_175068_2_);
      }

   }

   private void func_180437_a(RenderGlobal p_180437_1_, float p_180437_2_, int p_180437_3_) {
      if(this.field_78531_r.field_71474_y.func_181147_e() != 0) {
         this.field_78531_r.field_71424_I.func_76318_c("clouds");
         GlStateManager.func_179128_n(5889);
         GlStateManager.func_179096_D();
         Project.gluPerspective(this.func_78481_a(p_180437_2_, true), (float)this.field_78531_r.field_71443_c / (float)this.field_78531_r.field_71440_d, 0.05F, this.field_78530_s * 4.0F);
         GlStateManager.func_179128_n(5888);
         GlStateManager.func_179094_E();
         this.func_78468_a(0, p_180437_2_);
         p_180437_1_.func_180447_b(p_180437_2_, p_180437_3_);
         GlStateManager.func_179106_n();
         GlStateManager.func_179121_F();
         GlStateManager.func_179128_n(5889);
         GlStateManager.func_179096_D();
         Project.gluPerspective(this.func_78481_a(p_180437_2_, true), (float)this.field_78531_r.field_71443_c / (float)this.field_78531_r.field_71440_d, 0.05F, this.field_78530_s * MathHelper.field_180189_a);
         GlStateManager.func_179128_n(5888);
      }

   }

   private void func_78484_h() {
      float lvt_1_1_ = this.field_78531_r.field_71441_e.func_72867_j(1.0F);
      if(!this.field_78531_r.field_71474_y.field_74347_j) {
         lvt_1_1_ /= 2.0F;
      }

      if(lvt_1_1_ != 0.0F) {
         this.field_78537_ab.setSeed((long)this.field_78529_t * 312987231L);
         Entity lvt_2_1_ = this.field_78531_r.func_175606_aa();
         World lvt_3_1_ = this.field_78531_r.field_71441_e;
         BlockPos lvt_4_1_ = new BlockPos(lvt_2_1_);
         int lvt_5_1_ = 10;
         double lvt_6_1_ = 0.0D;
         double lvt_8_1_ = 0.0D;
         double lvt_10_1_ = 0.0D;
         int lvt_12_1_ = 0;
         int lvt_13_1_ = (int)(100.0F * lvt_1_1_ * lvt_1_1_);
         if(this.field_78531_r.field_71474_y.field_74362_aa == 1) {
            lvt_13_1_ >>= 1;
         } else if(this.field_78531_r.field_71474_y.field_74362_aa == 2) {
            lvt_13_1_ = 0;
         }

         for(int lvt_14_1_ = 0; lvt_14_1_ < lvt_13_1_; ++lvt_14_1_) {
            BlockPos lvt_15_1_ = lvt_3_1_.func_175725_q(lvt_4_1_.func_177982_a(this.field_78537_ab.nextInt(lvt_5_1_) - this.field_78537_ab.nextInt(lvt_5_1_), 0, this.field_78537_ab.nextInt(lvt_5_1_) - this.field_78537_ab.nextInt(lvt_5_1_)));
            BiomeGenBase lvt_16_1_ = lvt_3_1_.func_180494_b(lvt_15_1_);
            BlockPos lvt_17_1_ = lvt_15_1_.func_177977_b();
            Block lvt_18_1_ = lvt_3_1_.func_180495_p(lvt_17_1_).func_177230_c();
            if(lvt_15_1_.func_177956_o() <= lvt_4_1_.func_177956_o() + lvt_5_1_ && lvt_15_1_.func_177956_o() >= lvt_4_1_.func_177956_o() - lvt_5_1_ && lvt_16_1_.func_76738_d() && lvt_16_1_.func_180626_a(lvt_15_1_) >= 0.15F) {
               double lvt_19_1_ = this.field_78537_ab.nextDouble();
               double lvt_21_1_ = this.field_78537_ab.nextDouble();
               if(lvt_18_1_.func_149688_o() == Material.field_151587_i) {
                  this.field_78531_r.field_71441_e.func_175688_a(EnumParticleTypes.SMOKE_NORMAL, (double)lvt_15_1_.func_177958_n() + lvt_19_1_, (double)((float)lvt_15_1_.func_177956_o() + 0.1F) - lvt_18_1_.func_149665_z(), (double)lvt_15_1_.func_177952_p() + lvt_21_1_, 0.0D, 0.0D, 0.0D, new int[0]);
               } else if(lvt_18_1_.func_149688_o() != Material.field_151579_a) {
                  lvt_18_1_.func_180654_a(lvt_3_1_, lvt_17_1_);
                  ++lvt_12_1_;
                  if(this.field_78537_ab.nextInt(lvt_12_1_) == 0) {
                     lvt_6_1_ = (double)lvt_17_1_.func_177958_n() + lvt_19_1_;
                     lvt_8_1_ = (double)((float)lvt_17_1_.func_177956_o() + 0.1F) + lvt_18_1_.func_149669_A() - 1.0D;
                     lvt_10_1_ = (double)lvt_17_1_.func_177952_p() + lvt_21_1_;
                  }

                  this.field_78531_r.field_71441_e.func_175688_a(EnumParticleTypes.WATER_DROP, (double)lvt_17_1_.func_177958_n() + lvt_19_1_, (double)((float)lvt_17_1_.func_177956_o() + 0.1F) + lvt_18_1_.func_149669_A(), (double)lvt_17_1_.func_177952_p() + lvt_21_1_, 0.0D, 0.0D, 0.0D, new int[0]);
               }
            }
         }

         if(lvt_12_1_ > 0 && this.field_78537_ab.nextInt(3) < this.field_78534_ac++) {
            this.field_78534_ac = 0;
            if(lvt_8_1_ > (double)(lvt_4_1_.func_177956_o() + 1) && lvt_3_1_.func_175725_q(lvt_4_1_).func_177956_o() > MathHelper.func_76141_d((float)lvt_4_1_.func_177956_o())) {
               this.field_78531_r.field_71441_e.func_72980_b(lvt_6_1_, lvt_8_1_, lvt_10_1_, "ambient.weather.rain", 0.1F, 0.5F, false);
            } else {
               this.field_78531_r.field_71441_e.func_72980_b(lvt_6_1_, lvt_8_1_, lvt_10_1_, "ambient.weather.rain", 0.2F, 1.0F, false);
            }
         }

      }
   }

   protected void func_78474_d(float p_78474_1_) {
      float lvt_2_1_ = this.field_78531_r.field_71441_e.func_72867_j(p_78474_1_);
      if(lvt_2_1_ > 0.0F) {
         this.func_180436_i();
         Entity lvt_3_1_ = this.field_78531_r.func_175606_aa();
         World lvt_4_1_ = this.field_78531_r.field_71441_e;
         int lvt_5_1_ = MathHelper.func_76128_c(lvt_3_1_.field_70165_t);
         int lvt_6_1_ = MathHelper.func_76128_c(lvt_3_1_.field_70163_u);
         int lvt_7_1_ = MathHelper.func_76128_c(lvt_3_1_.field_70161_v);
         Tessellator lvt_8_1_ = Tessellator.func_178181_a();
         WorldRenderer lvt_9_1_ = lvt_8_1_.func_178180_c();
         GlStateManager.func_179129_p();
         GL11.glNormal3f(0.0F, 1.0F, 0.0F);
         GlStateManager.func_179147_l();
         GlStateManager.func_179120_a(770, 771, 1, 0);
         GlStateManager.func_179092_a(516, 0.1F);
         double lvt_10_1_ = lvt_3_1_.field_70142_S + (lvt_3_1_.field_70165_t - lvt_3_1_.field_70142_S) * (double)p_78474_1_;
         double lvt_12_1_ = lvt_3_1_.field_70137_T + (lvt_3_1_.field_70163_u - lvt_3_1_.field_70137_T) * (double)p_78474_1_;
         double lvt_14_1_ = lvt_3_1_.field_70136_U + (lvt_3_1_.field_70161_v - lvt_3_1_.field_70136_U) * (double)p_78474_1_;
         int lvt_16_1_ = MathHelper.func_76128_c(lvt_12_1_);
         int lvt_17_1_ = 5;
         if(this.field_78531_r.field_71474_y.field_74347_j) {
            lvt_17_1_ = 10;
         }

         int lvt_18_1_ = -1;
         float lvt_19_1_ = (float)this.field_78529_t + p_78474_1_;
         lvt_9_1_.func_178969_c(-lvt_10_1_, -lvt_12_1_, -lvt_14_1_);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         BlockPos.MutableBlockPos lvt_20_1_ = new BlockPos.MutableBlockPos();

         for(int lvt_21_1_ = lvt_7_1_ - lvt_17_1_; lvt_21_1_ <= lvt_7_1_ + lvt_17_1_; ++lvt_21_1_) {
            for(int lvt_22_1_ = lvt_5_1_ - lvt_17_1_; lvt_22_1_ <= lvt_5_1_ + lvt_17_1_; ++lvt_22_1_) {
               int lvt_23_1_ = (lvt_21_1_ - lvt_7_1_ + 16) * 32 + lvt_22_1_ - lvt_5_1_ + 16;
               double lvt_24_1_ = (double)this.field_175076_N[lvt_23_1_] * 0.5D;
               double lvt_26_1_ = (double)this.field_175077_O[lvt_23_1_] * 0.5D;
               lvt_20_1_.func_181079_c(lvt_22_1_, 0, lvt_21_1_);
               BiomeGenBase lvt_28_1_ = lvt_4_1_.func_180494_b(lvt_20_1_);
               if(lvt_28_1_.func_76738_d() || lvt_28_1_.func_76746_c()) {
                  int lvt_29_1_ = lvt_4_1_.func_175725_q(lvt_20_1_).func_177956_o();
                  int lvt_30_1_ = lvt_6_1_ - lvt_17_1_;
                  int lvt_31_1_ = lvt_6_1_ + lvt_17_1_;
                  if(lvt_30_1_ < lvt_29_1_) {
                     lvt_30_1_ = lvt_29_1_;
                  }

                  if(lvt_31_1_ < lvt_29_1_) {
                     lvt_31_1_ = lvt_29_1_;
                  }

                  int lvt_32_1_ = lvt_29_1_;
                  if(lvt_29_1_ < lvt_16_1_) {
                     lvt_32_1_ = lvt_16_1_;
                  }

                  if(lvt_30_1_ != lvt_31_1_) {
                     this.field_78537_ab.setSeed((long)(lvt_22_1_ * lvt_22_1_ * 3121 + lvt_22_1_ * 45238971 ^ lvt_21_1_ * lvt_21_1_ * 418711 + lvt_21_1_ * 13761));
                     lvt_20_1_.func_181079_c(lvt_22_1_, lvt_30_1_, lvt_21_1_);
                     float lvt_33_1_ = lvt_28_1_.func_180626_a(lvt_20_1_);
                     if(lvt_4_1_.func_72959_q().func_76939_a(lvt_33_1_, lvt_29_1_) >= 0.15F) {
                        if(lvt_18_1_ != 0) {
                           if(lvt_18_1_ >= 0) {
                              lvt_8_1_.func_78381_a();
                           }

                           lvt_18_1_ = 0;
                           this.field_78531_r.func_110434_K().func_110577_a(field_110924_q);
                           lvt_9_1_.func_181668_a(7, DefaultVertexFormats.field_181704_d);
                        }

                        double lvt_34_1_ = ((double)(this.field_78529_t + lvt_22_1_ * lvt_22_1_ * 3121 + lvt_22_1_ * 45238971 + lvt_21_1_ * lvt_21_1_ * 418711 + lvt_21_1_ * 13761 & 31) + (double)p_78474_1_) / 32.0D * (3.0D + this.field_78537_ab.nextDouble());
                        double lvt_36_1_ = (double)((float)lvt_22_1_ + 0.5F) - lvt_3_1_.field_70165_t;
                        double lvt_38_1_ = (double)((float)lvt_21_1_ + 0.5F) - lvt_3_1_.field_70161_v;
                        float lvt_40_1_ = MathHelper.func_76133_a(lvt_36_1_ * lvt_36_1_ + lvt_38_1_ * lvt_38_1_) / (float)lvt_17_1_;
                        float lvt_41_1_ = ((1.0F - lvt_40_1_ * lvt_40_1_) * 0.5F + 0.5F) * lvt_2_1_;
                        lvt_20_1_.func_181079_c(lvt_22_1_, lvt_32_1_, lvt_21_1_);
                        int lvt_42_1_ = lvt_4_1_.func_175626_b(lvt_20_1_, 0);
                        int lvt_43_1_ = lvt_42_1_ >> 16 & '\uffff';
                        int lvt_44_1_ = lvt_42_1_ & '\uffff';
                        lvt_9_1_.func_181662_b((double)lvt_22_1_ - lvt_24_1_ + 0.5D, (double)lvt_30_1_, (double)lvt_21_1_ - lvt_26_1_ + 0.5D).func_181673_a(0.0D, (double)lvt_30_1_ * 0.25D + lvt_34_1_).func_181666_a(1.0F, 1.0F, 1.0F, lvt_41_1_).func_181671_a(lvt_43_1_, lvt_44_1_).func_181675_d();
                        lvt_9_1_.func_181662_b((double)lvt_22_1_ + lvt_24_1_ + 0.5D, (double)lvt_30_1_, (double)lvt_21_1_ + lvt_26_1_ + 0.5D).func_181673_a(1.0D, (double)lvt_30_1_ * 0.25D + lvt_34_1_).func_181666_a(1.0F, 1.0F, 1.0F, lvt_41_1_).func_181671_a(lvt_43_1_, lvt_44_1_).func_181675_d();
                        lvt_9_1_.func_181662_b((double)lvt_22_1_ + lvt_24_1_ + 0.5D, (double)lvt_31_1_, (double)lvt_21_1_ + lvt_26_1_ + 0.5D).func_181673_a(1.0D, (double)lvt_31_1_ * 0.25D + lvt_34_1_).func_181666_a(1.0F, 1.0F, 1.0F, lvt_41_1_).func_181671_a(lvt_43_1_, lvt_44_1_).func_181675_d();
                        lvt_9_1_.func_181662_b((double)lvt_22_1_ - lvt_24_1_ + 0.5D, (double)lvt_31_1_, (double)lvt_21_1_ - lvt_26_1_ + 0.5D).func_181673_a(0.0D, (double)lvt_31_1_ * 0.25D + lvt_34_1_).func_181666_a(1.0F, 1.0F, 1.0F, lvt_41_1_).func_181671_a(lvt_43_1_, lvt_44_1_).func_181675_d();
                     } else {
                        if(lvt_18_1_ != 1) {
                           if(lvt_18_1_ >= 0) {
                              lvt_8_1_.func_78381_a();
                           }

                           lvt_18_1_ = 1;
                           this.field_78531_r.func_110434_K().func_110577_a(field_110923_r);
                           lvt_9_1_.func_181668_a(7, DefaultVertexFormats.field_181704_d);
                        }

                        double lvt_34_2_ = (double)(((float)(this.field_78529_t & 511) + p_78474_1_) / 512.0F);
                        double lvt_36_2_ = this.field_78537_ab.nextDouble() + (double)lvt_19_1_ * 0.01D * (double)((float)this.field_78537_ab.nextGaussian());
                        double lvt_38_2_ = this.field_78537_ab.nextDouble() + (double)(lvt_19_1_ * (float)this.field_78537_ab.nextGaussian()) * 0.001D;
                        double lvt_40_2_ = (double)((float)lvt_22_1_ + 0.5F) - lvt_3_1_.field_70165_t;
                        double lvt_42_2_ = (double)((float)lvt_21_1_ + 0.5F) - lvt_3_1_.field_70161_v;
                        float lvt_44_2_ = MathHelper.func_76133_a(lvt_40_2_ * lvt_40_2_ + lvt_42_2_ * lvt_42_2_) / (float)lvt_17_1_;
                        float lvt_45_1_ = ((1.0F - lvt_44_2_ * lvt_44_2_) * 0.3F + 0.5F) * lvt_2_1_;
                        lvt_20_1_.func_181079_c(lvt_22_1_, lvt_32_1_, lvt_21_1_);
                        int lvt_46_1_ = (lvt_4_1_.func_175626_b(lvt_20_1_, 0) * 3 + 15728880) / 4;
                        int lvt_47_1_ = lvt_46_1_ >> 16 & '\uffff';
                        int lvt_48_1_ = lvt_46_1_ & '\uffff';
                        lvt_9_1_.func_181662_b((double)lvt_22_1_ - lvt_24_1_ + 0.5D, (double)lvt_30_1_, (double)lvt_21_1_ - lvt_26_1_ + 0.5D).func_181673_a(0.0D + lvt_36_2_, (double)lvt_30_1_ * 0.25D + lvt_34_2_ + lvt_38_2_).func_181666_a(1.0F, 1.0F, 1.0F, lvt_45_1_).func_181671_a(lvt_47_1_, lvt_48_1_).func_181675_d();
                        lvt_9_1_.func_181662_b((double)lvt_22_1_ + lvt_24_1_ + 0.5D, (double)lvt_30_1_, (double)lvt_21_1_ + lvt_26_1_ + 0.5D).func_181673_a(1.0D + lvt_36_2_, (double)lvt_30_1_ * 0.25D + lvt_34_2_ + lvt_38_2_).func_181666_a(1.0F, 1.0F, 1.0F, lvt_45_1_).func_181671_a(lvt_47_1_, lvt_48_1_).func_181675_d();
                        lvt_9_1_.func_181662_b((double)lvt_22_1_ + lvt_24_1_ + 0.5D, (double)lvt_31_1_, (double)lvt_21_1_ + lvt_26_1_ + 0.5D).func_181673_a(1.0D + lvt_36_2_, (double)lvt_31_1_ * 0.25D + lvt_34_2_ + lvt_38_2_).func_181666_a(1.0F, 1.0F, 1.0F, lvt_45_1_).func_181671_a(lvt_47_1_, lvt_48_1_).func_181675_d();
                        lvt_9_1_.func_181662_b((double)lvt_22_1_ - lvt_24_1_ + 0.5D, (double)lvt_31_1_, (double)lvt_21_1_ - lvt_26_1_ + 0.5D).func_181673_a(0.0D + lvt_36_2_, (double)lvt_31_1_ * 0.25D + lvt_34_2_ + lvt_38_2_).func_181666_a(1.0F, 1.0F, 1.0F, lvt_45_1_).func_181671_a(lvt_47_1_, lvt_48_1_).func_181675_d();
                     }
                  }
               }
            }
         }

         if(lvt_18_1_ >= 0) {
            lvt_8_1_.func_78381_a();
         }

         lvt_9_1_.func_178969_c(0.0D, 0.0D, 0.0D);
         GlStateManager.func_179089_o();
         GlStateManager.func_179084_k();
         GlStateManager.func_179092_a(516, 0.1F);
         this.func_175072_h();
      }
   }

   public void func_78478_c() {
      ScaledResolution lvt_1_1_ = new ScaledResolution(this.field_78531_r);
      GlStateManager.func_179086_m(256);
      GlStateManager.func_179128_n(5889);
      GlStateManager.func_179096_D();
      GlStateManager.func_179130_a(0.0D, lvt_1_1_.func_78327_c(), lvt_1_1_.func_78324_d(), 0.0D, 1000.0D, 3000.0D);
      GlStateManager.func_179128_n(5888);
      GlStateManager.func_179096_D();
      GlStateManager.func_179109_b(0.0F, 0.0F, -2000.0F);
   }

   private void func_78466_h(float p_78466_1_) {
      World lvt_2_1_ = this.field_78531_r.field_71441_e;
      Entity lvt_3_1_ = this.field_78531_r.func_175606_aa();
      float lvt_4_1_ = 0.25F + 0.75F * (float)this.field_78531_r.field_71474_y.field_151451_c / 32.0F;
      lvt_4_1_ = 1.0F - (float)Math.pow((double)lvt_4_1_, 0.25D);
      Vec3 lvt_5_1_ = lvt_2_1_.func_72833_a(this.field_78531_r.func_175606_aa(), p_78466_1_);
      float lvt_6_1_ = (float)lvt_5_1_.field_72450_a;
      float lvt_7_1_ = (float)lvt_5_1_.field_72448_b;
      float lvt_8_1_ = (float)lvt_5_1_.field_72449_c;
      Vec3 lvt_9_1_ = lvt_2_1_.func_72948_g(p_78466_1_);
      this.field_175080_Q = (float)lvt_9_1_.field_72450_a;
      this.field_175082_R = (float)lvt_9_1_.field_72448_b;
      this.field_175081_S = (float)lvt_9_1_.field_72449_c;
      if(this.field_78531_r.field_71474_y.field_151451_c >= 4) {
         double lvt_10_1_ = -1.0D;
         Vec3 lvt_12_1_ = MathHelper.func_76126_a(lvt_2_1_.func_72929_e(p_78466_1_)) > 0.0F?new Vec3(lvt_10_1_, 0.0D, 0.0D):new Vec3(1.0D, 0.0D, 0.0D);
         float lvt_13_1_ = (float)lvt_3_1_.func_70676_i(p_78466_1_).func_72430_b(lvt_12_1_);
         if(lvt_13_1_ < 0.0F) {
            lvt_13_1_ = 0.0F;
         }

         if(lvt_13_1_ > 0.0F) {
            float[] lvt_14_1_ = lvt_2_1_.field_73011_w.func_76560_a(lvt_2_1_.func_72826_c(p_78466_1_), p_78466_1_);
            if(lvt_14_1_ != null) {
               lvt_13_1_ = lvt_13_1_ * lvt_14_1_[3];
               this.field_175080_Q = this.field_175080_Q * (1.0F - lvt_13_1_) + lvt_14_1_[0] * lvt_13_1_;
               this.field_175082_R = this.field_175082_R * (1.0F - lvt_13_1_) + lvt_14_1_[1] * lvt_13_1_;
               this.field_175081_S = this.field_175081_S * (1.0F - lvt_13_1_) + lvt_14_1_[2] * lvt_13_1_;
            }
         }
      }

      this.field_175080_Q += (lvt_6_1_ - this.field_175080_Q) * lvt_4_1_;
      this.field_175082_R += (lvt_7_1_ - this.field_175082_R) * lvt_4_1_;
      this.field_175081_S += (lvt_8_1_ - this.field_175081_S) * lvt_4_1_;
      float lvt_10_2_ = lvt_2_1_.func_72867_j(p_78466_1_);
      if(lvt_10_2_ > 0.0F) {
         float lvt_11_1_ = 1.0F - lvt_10_2_ * 0.5F;
         float lvt_12_2_ = 1.0F - lvt_10_2_ * 0.4F;
         this.field_175080_Q *= lvt_11_1_;
         this.field_175082_R *= lvt_11_1_;
         this.field_175081_S *= lvt_12_2_;
      }

      float lvt_11_2_ = lvt_2_1_.func_72819_i(p_78466_1_);
      if(lvt_11_2_ > 0.0F) {
         float lvt_12_3_ = 1.0F - lvt_11_2_ * 0.5F;
         this.field_175080_Q *= lvt_12_3_;
         this.field_175082_R *= lvt_12_3_;
         this.field_175081_S *= lvt_12_3_;
      }

      Block lvt_12_4_ = ActiveRenderInfo.func_180786_a(this.field_78531_r.field_71441_e, lvt_3_1_, p_78466_1_);
      if(this.field_78500_U) {
         Vec3 lvt_13_2_ = lvt_2_1_.func_72824_f(p_78466_1_);
         this.field_175080_Q = (float)lvt_13_2_.field_72450_a;
         this.field_175082_R = (float)lvt_13_2_.field_72448_b;
         this.field_175081_S = (float)lvt_13_2_.field_72449_c;
      } else if(lvt_12_4_.func_149688_o() == Material.field_151586_h) {
         float lvt_13_3_ = (float)EnchantmentHelper.func_180319_a(lvt_3_1_) * 0.2F;
         if(lvt_3_1_ instanceof EntityLivingBase && ((EntityLivingBase)lvt_3_1_).func_70644_a(Potion.field_76427_o)) {
            lvt_13_3_ = lvt_13_3_ * 0.3F + 0.6F;
         }

         this.field_175080_Q = 0.02F + lvt_13_3_;
         this.field_175082_R = 0.02F + lvt_13_3_;
         this.field_175081_S = 0.2F + lvt_13_3_;
      } else if(lvt_12_4_.func_149688_o() == Material.field_151587_i) {
         this.field_175080_Q = 0.6F;
         this.field_175082_R = 0.1F;
         this.field_175081_S = 0.0F;
      }

      float lvt_13_4_ = this.field_78535_ad + (this.field_78539_ae - this.field_78535_ad) * p_78466_1_;
      this.field_175080_Q *= lvt_13_4_;
      this.field_175082_R *= lvt_13_4_;
      this.field_175081_S *= lvt_13_4_;
      double lvt_14_2_ = (lvt_3_1_.field_70137_T + (lvt_3_1_.field_70163_u - lvt_3_1_.field_70137_T) * (double)p_78466_1_) * lvt_2_1_.field_73011_w.func_76565_k();
      if(lvt_3_1_ instanceof EntityLivingBase && ((EntityLivingBase)lvt_3_1_).func_70644_a(Potion.field_76440_q)) {
         int lvt_16_1_ = ((EntityLivingBase)lvt_3_1_).func_70660_b(Potion.field_76440_q).func_76459_b();
         if(lvt_16_1_ < 20) {
            lvt_14_2_ *= (double)(1.0F - (float)lvt_16_1_ / 20.0F);
         } else {
            lvt_14_2_ = 0.0D;
         }
      }

      if(lvt_14_2_ < 1.0D) {
         if(lvt_14_2_ < 0.0D) {
            lvt_14_2_ = 0.0D;
         }

         lvt_14_2_ = lvt_14_2_ * lvt_14_2_;
         this.field_175080_Q = (float)((double)this.field_175080_Q * lvt_14_2_);
         this.field_175082_R = (float)((double)this.field_175082_R * lvt_14_2_);
         this.field_175081_S = (float)((double)this.field_175081_S * lvt_14_2_);
      }

      if(this.field_82831_U > 0.0F) {
         float lvt_16_2_ = this.field_82832_V + (this.field_82831_U - this.field_82832_V) * p_78466_1_;
         this.field_175080_Q = this.field_175080_Q * (1.0F - lvt_16_2_) + this.field_175080_Q * 0.7F * lvt_16_2_;
         this.field_175082_R = this.field_175082_R * (1.0F - lvt_16_2_) + this.field_175082_R * 0.6F * lvt_16_2_;
         this.field_175081_S = this.field_175081_S * (1.0F - lvt_16_2_) + this.field_175081_S * 0.6F * lvt_16_2_;
      }

      if(lvt_3_1_ instanceof EntityLivingBase && ((EntityLivingBase)lvt_3_1_).func_70644_a(Potion.field_76439_r)) {
         float lvt_16_3_ = this.func_180438_a((EntityLivingBase)lvt_3_1_, p_78466_1_);
         float lvt_17_1_ = 1.0F / this.field_175080_Q;
         if(lvt_17_1_ > 1.0F / this.field_175082_R) {
            lvt_17_1_ = 1.0F / this.field_175082_R;
         }

         if(lvt_17_1_ > 1.0F / this.field_175081_S) {
            lvt_17_1_ = 1.0F / this.field_175081_S;
         }

         this.field_175080_Q = this.field_175080_Q * (1.0F - lvt_16_3_) + this.field_175080_Q * lvt_17_1_ * lvt_16_3_;
         this.field_175082_R = this.field_175082_R * (1.0F - lvt_16_3_) + this.field_175082_R * lvt_17_1_ * lvt_16_3_;
         this.field_175081_S = this.field_175081_S * (1.0F - lvt_16_3_) + this.field_175081_S * lvt_17_1_ * lvt_16_3_;
      }

      if(this.field_78531_r.field_71474_y.field_74337_g) {
         float lvt_16_4_ = (this.field_175080_Q * 30.0F + this.field_175082_R * 59.0F + this.field_175081_S * 11.0F) / 100.0F;
         float lvt_17_2_ = (this.field_175080_Q * 30.0F + this.field_175082_R * 70.0F) / 100.0F;
         float lvt_18_1_ = (this.field_175080_Q * 30.0F + this.field_175081_S * 70.0F) / 100.0F;
         this.field_175080_Q = lvt_16_4_;
         this.field_175082_R = lvt_17_2_;
         this.field_175081_S = lvt_18_1_;
      }

      GlStateManager.func_179082_a(this.field_175080_Q, this.field_175082_R, this.field_175081_S, 0.0F);
   }

   private void func_78468_a(int p_78468_1_, float p_78468_2_) {
      Entity lvt_3_1_ = this.field_78531_r.func_175606_aa();
      boolean lvt_4_1_ = false;
      if(lvt_3_1_ instanceof EntityPlayer) {
         lvt_4_1_ = ((EntityPlayer)lvt_3_1_).field_71075_bZ.field_75098_d;
      }

      GL11.glFog(2918, this.func_78469_a(this.field_175080_Q, this.field_175082_R, this.field_175081_S, 1.0F));
      GL11.glNormal3f(0.0F, -1.0F, 0.0F);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      Block lvt_5_1_ = ActiveRenderInfo.func_180786_a(this.field_78531_r.field_71441_e, lvt_3_1_, p_78468_2_);
      if(lvt_3_1_ instanceof EntityLivingBase && ((EntityLivingBase)lvt_3_1_).func_70644_a(Potion.field_76440_q)) {
         float lvt_6_1_ = 5.0F;
         int lvt_7_1_ = ((EntityLivingBase)lvt_3_1_).func_70660_b(Potion.field_76440_q).func_76459_b();
         if(lvt_7_1_ < 20) {
            lvt_6_1_ = 5.0F + (this.field_78530_s - 5.0F) * (1.0F - (float)lvt_7_1_ / 20.0F);
         }

         GlStateManager.func_179093_d(9729);
         if(p_78468_1_ == -1) {
            GlStateManager.func_179102_b(0.0F);
            GlStateManager.func_179153_c(lvt_6_1_ * 0.8F);
         } else {
            GlStateManager.func_179102_b(lvt_6_1_ * 0.25F);
            GlStateManager.func_179153_c(lvt_6_1_);
         }

         if(GLContext.getCapabilities().GL_NV_fog_distance) {
            GL11.glFogi('\u855a', '\u855b');
         }
      } else if(this.field_78500_U) {
         GlStateManager.func_179093_d(2048);
         GlStateManager.func_179095_a(0.1F);
      } else if(lvt_5_1_.func_149688_o() == Material.field_151586_h) {
         GlStateManager.func_179093_d(2048);
         if(lvt_3_1_ instanceof EntityLivingBase && ((EntityLivingBase)lvt_3_1_).func_70644_a(Potion.field_76427_o)) {
            GlStateManager.func_179095_a(0.01F);
         } else {
            GlStateManager.func_179095_a(0.1F - (float)EnchantmentHelper.func_180319_a(lvt_3_1_) * 0.03F);
         }
      } else if(lvt_5_1_.func_149688_o() == Material.field_151587_i) {
         GlStateManager.func_179093_d(2048);
         GlStateManager.func_179095_a(2.0F);
      } else {
         float lvt_6_2_ = this.field_78530_s;
         GlStateManager.func_179093_d(9729);
         if(p_78468_1_ == -1) {
            GlStateManager.func_179102_b(0.0F);
            GlStateManager.func_179153_c(lvt_6_2_);
         } else {
            GlStateManager.func_179102_b(lvt_6_2_ * 0.75F);
            GlStateManager.func_179153_c(lvt_6_2_);
         }

         if(GLContext.getCapabilities().GL_NV_fog_distance) {
            GL11.glFogi('\u855a', '\u855b');
         }

         if(this.field_78531_r.field_71441_e.field_73011_w.func_76568_b((int)lvt_3_1_.field_70165_t, (int)lvt_3_1_.field_70161_v)) {
            GlStateManager.func_179102_b(lvt_6_2_ * 0.05F);
            GlStateManager.func_179153_c(Math.min(lvt_6_2_, 192.0F) * 0.5F);
         }
      }

      GlStateManager.func_179142_g();
      GlStateManager.func_179127_m();
      GlStateManager.func_179104_a(1028, 4608);
   }

   private FloatBuffer func_78469_a(float p_78469_1_, float p_78469_2_, float p_78469_3_, float p_78469_4_) {
      this.field_78521_m.clear();
      this.field_78521_m.put(p_78469_1_).put(p_78469_2_).put(p_78469_3_).put(p_78469_4_);
      this.field_78521_m.flip();
      return this.field_78521_m;
   }

   public MapItemRenderer func_147701_i() {
      return this.field_147709_v;
   }
}
