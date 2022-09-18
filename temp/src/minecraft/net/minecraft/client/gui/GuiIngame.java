package net.minecraft.client.gui;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiOverlayDebug;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.GuiStreamIndicator;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.border.WorldBorder;

public class GuiIngame extends Gui {
   private static final ResourceLocation field_110329_b = new ResourceLocation("textures/misc/vignette.png");
   private static final ResourceLocation field_110330_c = new ResourceLocation("textures/gui/widgets.png");
   private static final ResourceLocation field_110328_d = new ResourceLocation("textures/misc/pumpkinblur.png");
   private final Random field_73842_c = new Random();
   private final Minecraft field_73839_d;
   private final RenderItem field_73841_b;
   private final GuiNewChat field_73840_e;
   private final GuiStreamIndicator field_152127_m;
   private int field_73837_f;
   private String field_73838_g = "";
   private int field_73845_h;
   private boolean field_73844_j;
   public float field_73843_a = 1.0F;
   private int field_92017_k;
   private ItemStack field_92016_l;
   private final GuiOverlayDebug field_175198_t;
   private final GuiSpectator field_175197_u;
   private final GuiPlayerTabOverlay field_175196_v;
   private int field_175195_w;
   private String field_175201_x = "";
   private String field_175200_y = "";
   private int field_175199_z;
   private int field_175192_A;
   private int field_175193_B;
   private int field_175194_C = 0;
   private int field_175189_D = 0;
   private long field_175190_E = 0L;
   private long field_175191_F = 0L;

   public GuiIngame(Minecraft p_i46325_1_) {
      this.field_73839_d = p_i46325_1_;
      this.field_73841_b = p_i46325_1_.func_175599_af();
      this.field_175198_t = new GuiOverlayDebug(p_i46325_1_);
      this.field_175197_u = new GuiSpectator(p_i46325_1_);
      this.field_73840_e = new GuiNewChat(p_i46325_1_);
      this.field_152127_m = new GuiStreamIndicator(p_i46325_1_);
      this.field_175196_v = new GuiPlayerTabOverlay(p_i46325_1_, this);
      this.func_175177_a();
   }

   public void func_175177_a() {
      this.field_175199_z = 10;
      this.field_175192_A = 70;
      this.field_175193_B = 20;
   }

   public void func_175180_a(float p_175180_1_) {
      ScaledResolution lvt_2_1_ = new ScaledResolution(this.field_73839_d);
      int lvt_3_1_ = lvt_2_1_.func_78326_a();
      int lvt_4_1_ = lvt_2_1_.func_78328_b();
      this.field_73839_d.field_71460_t.func_78478_c();
      GlStateManager.func_179147_l();
      if(Minecraft.func_71375_t()) {
         this.func_180480_a(this.field_73839_d.field_71439_g.func_70013_c(p_175180_1_), lvt_2_1_);
      } else {
         GlStateManager.func_179120_a(770, 771, 1, 0);
      }

      ItemStack lvt_5_1_ = this.field_73839_d.field_71439_g.field_71071_by.func_70440_f(3);
      if(this.field_73839_d.field_71474_y.field_74320_O == 0 && lvt_5_1_ != null && lvt_5_1_.func_77973_b() == Item.func_150898_a(Blocks.field_150423_aK)) {
         this.func_180476_e(lvt_2_1_);
      }

      if(!this.field_73839_d.field_71439_g.func_70644_a(Potion.field_76431_k)) {
         float lvt_6_1_ = this.field_73839_d.field_71439_g.field_71080_cy + (this.field_73839_d.field_71439_g.field_71086_bY - this.field_73839_d.field_71439_g.field_71080_cy) * p_175180_1_;
         if(lvt_6_1_ > 0.0F) {
            this.func_180474_b(lvt_6_1_, lvt_2_1_);
         }
      }

      if(this.field_73839_d.field_71442_b.func_78747_a()) {
         this.field_175197_u.func_175264_a(lvt_2_1_, p_175180_1_);
      } else {
         this.func_180479_a(lvt_2_1_, p_175180_1_);
      }

      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_73839_d.func_110434_K().func_110577_a(field_110324_m);
      GlStateManager.func_179147_l();
      if(this.func_175183_b()) {
         GlStateManager.func_179120_a(775, 769, 1, 0);
         GlStateManager.func_179141_d();
         this.func_73729_b(lvt_3_1_ / 2 - 7, lvt_4_1_ / 2 - 7, 0, 0, 16, 16);
      }

      GlStateManager.func_179120_a(770, 771, 1, 0);
      this.field_73839_d.field_71424_I.func_76320_a("bossHealth");
      this.func_73828_d();
      this.field_73839_d.field_71424_I.func_76319_b();
      if(this.field_73839_d.field_71442_b.func_78755_b()) {
         this.func_180477_d(lvt_2_1_);
      }

      GlStateManager.func_179084_k();
      if(this.field_73839_d.field_71439_g.func_71060_bI() > 0) {
         this.field_73839_d.field_71424_I.func_76320_a("sleep");
         GlStateManager.func_179097_i();
         GlStateManager.func_179118_c();
         int lvt_6_2_ = this.field_73839_d.field_71439_g.func_71060_bI();
         float lvt_7_1_ = (float)lvt_6_2_ / 100.0F;
         if(lvt_7_1_ > 1.0F) {
            lvt_7_1_ = 1.0F - (float)(lvt_6_2_ - 100) / 10.0F;
         }

         int lvt_8_1_ = (int)(220.0F * lvt_7_1_) << 24 | 1052704;
         func_73734_a(0, 0, lvt_3_1_, lvt_4_1_, lvt_8_1_);
         GlStateManager.func_179141_d();
         GlStateManager.func_179126_j();
         this.field_73839_d.field_71424_I.func_76319_b();
      }

      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      int lvt_6_3_ = lvt_3_1_ / 2 - 91;
      if(this.field_73839_d.field_71439_g.func_110317_t()) {
         this.func_175186_a(lvt_2_1_, lvt_6_3_);
      } else if(this.field_73839_d.field_71442_b.func_78763_f()) {
         this.func_175176_b(lvt_2_1_, lvt_6_3_);
      }

      if(this.field_73839_d.field_71474_y.field_92117_D && !this.field_73839_d.field_71442_b.func_78747_a()) {
         this.func_181551_a(lvt_2_1_);
      } else if(this.field_73839_d.field_71439_g.func_175149_v()) {
         this.field_175197_u.func_175263_a(lvt_2_1_);
      }

      if(this.field_73839_d.func_71355_q()) {
         this.func_175185_b(lvt_2_1_);
      }

      if(this.field_73839_d.field_71474_y.field_74330_P) {
         this.field_175198_t.func_175237_a(lvt_2_1_);
      }

      if(this.field_73845_h > 0) {
         this.field_73839_d.field_71424_I.func_76320_a("overlayMessage");
         float lvt_7_2_ = (float)this.field_73845_h - p_175180_1_;
         int lvt_8_2_ = (int)(lvt_7_2_ * 255.0F / 20.0F);
         if(lvt_8_2_ > 255) {
            lvt_8_2_ = 255;
         }

         if(lvt_8_2_ > 8) {
            GlStateManager.func_179094_E();
            GlStateManager.func_179109_b((float)(lvt_3_1_ / 2), (float)(lvt_4_1_ - 68), 0.0F);
            GlStateManager.func_179147_l();
            GlStateManager.func_179120_a(770, 771, 1, 0);
            int lvt_9_1_ = 16777215;
            if(this.field_73844_j) {
               lvt_9_1_ = MathHelper.func_181758_c(lvt_7_2_ / 50.0F, 0.7F, 0.6F) & 16777215;
            }

            this.func_175179_f().func_78276_b(this.field_73838_g, -this.func_175179_f().func_78256_a(this.field_73838_g) / 2, -4, lvt_9_1_ + (lvt_8_2_ << 24 & -16777216));
            GlStateManager.func_179084_k();
            GlStateManager.func_179121_F();
         }

         this.field_73839_d.field_71424_I.func_76319_b();
      }

      if(this.field_175195_w > 0) {
         this.field_73839_d.field_71424_I.func_76320_a("titleAndSubtitle");
         float lvt_7_3_ = (float)this.field_175195_w - p_175180_1_;
         int lvt_8_3_ = 255;
         if(this.field_175195_w > this.field_175193_B + this.field_175192_A) {
            float lvt_9_2_ = (float)(this.field_175199_z + this.field_175192_A + this.field_175193_B) - lvt_7_3_;
            lvt_8_3_ = (int)(lvt_9_2_ * 255.0F / (float)this.field_175199_z);
         }

         if(this.field_175195_w <= this.field_175193_B) {
            lvt_8_3_ = (int)(lvt_7_3_ * 255.0F / (float)this.field_175193_B);
         }

         lvt_8_3_ = MathHelper.func_76125_a(lvt_8_3_, 0, 255);
         if(lvt_8_3_ > 8) {
            GlStateManager.func_179094_E();
            GlStateManager.func_179109_b((float)(lvt_3_1_ / 2), (float)(lvt_4_1_ / 2), 0.0F);
            GlStateManager.func_179147_l();
            GlStateManager.func_179120_a(770, 771, 1, 0);
            GlStateManager.func_179094_E();
            GlStateManager.func_179152_a(4.0F, 4.0F, 4.0F);
            int lvt_9_4_ = lvt_8_3_ << 24 & -16777216;
            this.func_175179_f().func_175065_a(this.field_175201_x, (float)(-this.func_175179_f().func_78256_a(this.field_175201_x) / 2), -10.0F, 16777215 | lvt_9_4_, true);
            GlStateManager.func_179121_F();
            GlStateManager.func_179094_E();
            GlStateManager.func_179152_a(2.0F, 2.0F, 2.0F);
            this.func_175179_f().func_175065_a(this.field_175200_y, (float)(-this.func_175179_f().func_78256_a(this.field_175200_y) / 2), 5.0F, 16777215 | lvt_9_4_, true);
            GlStateManager.func_179121_F();
            GlStateManager.func_179084_k();
            GlStateManager.func_179121_F();
         }

         this.field_73839_d.field_71424_I.func_76319_b();
      }

      Scoreboard lvt_7_4_ = this.field_73839_d.field_71441_e.func_96441_U();
      ScoreObjective lvt_8_4_ = null;
      ScorePlayerTeam lvt_9_5_ = lvt_7_4_.func_96509_i(this.field_73839_d.field_71439_g.func_70005_c_());
      if(lvt_9_5_ != null) {
         int lvt_10_1_ = lvt_9_5_.func_178775_l().func_175746_b();
         if(lvt_10_1_ >= 0) {
            lvt_8_4_ = lvt_7_4_.func_96539_a(3 + lvt_10_1_);
         }
      }

      ScoreObjective lvt_10_2_ = lvt_8_4_ != null?lvt_8_4_:lvt_7_4_.func_96539_a(1);
      if(lvt_10_2_ != null) {
         this.func_180475_a(lvt_10_2_, lvt_2_1_);
      }

      GlStateManager.func_179147_l();
      GlStateManager.func_179120_a(770, 771, 1, 0);
      GlStateManager.func_179118_c();
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b(0.0F, (float)(lvt_4_1_ - 48), 0.0F);
      this.field_73839_d.field_71424_I.func_76320_a("chat");
      this.field_73840_e.func_146230_a(this.field_73837_f);
      this.field_73839_d.field_71424_I.func_76319_b();
      GlStateManager.func_179121_F();
      lvt_10_2_ = lvt_7_4_.func_96539_a(0);
      if(!this.field_73839_d.field_71474_y.field_74321_H.func_151470_d() || this.field_73839_d.func_71387_A() && this.field_73839_d.field_71439_g.field_71174_a.func_175106_d().size() <= 1 && lvt_10_2_ == null) {
         this.field_175196_v.func_175246_a(false);
      } else {
         this.field_175196_v.func_175246_a(true);
         this.field_175196_v.func_175249_a(lvt_3_1_, lvt_7_4_, lvt_10_2_);
      }

      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179140_f();
      GlStateManager.func_179141_d();
   }

   protected void func_180479_a(ScaledResolution p_180479_1_, float p_180479_2_) {
      if(this.field_73839_d.func_175606_aa() instanceof EntityPlayer) {
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         this.field_73839_d.func_110434_K().func_110577_a(field_110330_c);
         EntityPlayer lvt_3_1_ = (EntityPlayer)this.field_73839_d.func_175606_aa();
         int lvt_4_1_ = p_180479_1_.func_78326_a() / 2;
         float lvt_5_1_ = this.field_73735_i;
         this.field_73735_i = -90.0F;
         this.func_73729_b(lvt_4_1_ - 91, p_180479_1_.func_78328_b() - 22, 0, 0, 182, 22);
         this.func_73729_b(lvt_4_1_ - 91 - 1 + lvt_3_1_.field_71071_by.field_70461_c * 20, p_180479_1_.func_78328_b() - 22 - 1, 0, 22, 24, 22);
         this.field_73735_i = lvt_5_1_;
         GlStateManager.func_179091_B();
         GlStateManager.func_179147_l();
         GlStateManager.func_179120_a(770, 771, 1, 0);
         RenderHelper.func_74520_c();

         for(int lvt_6_1_ = 0; lvt_6_1_ < 9; ++lvt_6_1_) {
            int lvt_7_1_ = p_180479_1_.func_78326_a() / 2 - 90 + lvt_6_1_ * 20 + 2;
            int lvt_8_1_ = p_180479_1_.func_78328_b() - 16 - 3;
            this.func_175184_a(lvt_6_1_, lvt_7_1_, lvt_8_1_, p_180479_2_, lvt_3_1_);
         }

         RenderHelper.func_74518_a();
         GlStateManager.func_179101_C();
         GlStateManager.func_179084_k();
      }
   }

   public void func_175186_a(ScaledResolution p_175186_1_, int p_175186_2_) {
      this.field_73839_d.field_71424_I.func_76320_a("jumpBar");
      this.field_73839_d.func_110434_K().func_110577_a(Gui.field_110324_m);
      float lvt_3_1_ = this.field_73839_d.field_71439_g.func_110319_bJ();
      int lvt_4_1_ = 182;
      int lvt_5_1_ = (int)(lvt_3_1_ * (float)(lvt_4_1_ + 1));
      int lvt_6_1_ = p_175186_1_.func_78328_b() - 32 + 3;
      this.func_73729_b(p_175186_2_, lvt_6_1_, 0, 84, lvt_4_1_, 5);
      if(lvt_5_1_ > 0) {
         this.func_73729_b(p_175186_2_, lvt_6_1_, 0, 89, lvt_5_1_, 5);
      }

      this.field_73839_d.field_71424_I.func_76319_b();
   }

   public void func_175176_b(ScaledResolution p_175176_1_, int p_175176_2_) {
      this.field_73839_d.field_71424_I.func_76320_a("expBar");
      this.field_73839_d.func_110434_K().func_110577_a(Gui.field_110324_m);
      int lvt_3_1_ = this.field_73839_d.field_71439_g.func_71050_bK();
      if(lvt_3_1_ > 0) {
         int lvt_4_1_ = 182;
         int lvt_5_1_ = (int)(this.field_73839_d.field_71439_g.field_71106_cc * (float)(lvt_4_1_ + 1));
         int lvt_6_1_ = p_175176_1_.func_78328_b() - 32 + 3;
         this.func_73729_b(p_175176_2_, lvt_6_1_, 0, 64, lvt_4_1_, 5);
         if(lvt_5_1_ > 0) {
            this.func_73729_b(p_175176_2_, lvt_6_1_, 0, 69, lvt_5_1_, 5);
         }
      }

      this.field_73839_d.field_71424_I.func_76319_b();
      if(this.field_73839_d.field_71439_g.field_71068_ca > 0) {
         this.field_73839_d.field_71424_I.func_76320_a("expLevel");
         int lvt_4_2_ = 8453920;
         String lvt_5_2_ = "" + this.field_73839_d.field_71439_g.field_71068_ca;
         int lvt_6_2_ = (p_175176_1_.func_78326_a() - this.func_175179_f().func_78256_a(lvt_5_2_)) / 2;
         int lvt_7_1_ = p_175176_1_.func_78328_b() - 31 - 4;
         int lvt_8_1_ = 0;
         this.func_175179_f().func_78276_b(lvt_5_2_, lvt_6_2_ + 1, lvt_7_1_, 0);
         this.func_175179_f().func_78276_b(lvt_5_2_, lvt_6_2_ - 1, lvt_7_1_, 0);
         this.func_175179_f().func_78276_b(lvt_5_2_, lvt_6_2_, lvt_7_1_ + 1, 0);
         this.func_175179_f().func_78276_b(lvt_5_2_, lvt_6_2_, lvt_7_1_ - 1, 0);
         this.func_175179_f().func_78276_b(lvt_5_2_, lvt_6_2_, lvt_7_1_, lvt_4_2_);
         this.field_73839_d.field_71424_I.func_76319_b();
      }

   }

   public void func_181551_a(ScaledResolution p_181551_1_) {
      this.field_73839_d.field_71424_I.func_76320_a("selectedItemName");
      if(this.field_92017_k > 0 && this.field_92016_l != null) {
         String lvt_2_1_ = this.field_92016_l.func_82833_r();
         if(this.field_92016_l.func_82837_s()) {
            lvt_2_1_ = EnumChatFormatting.ITALIC + lvt_2_1_;
         }

         int lvt_3_1_ = (p_181551_1_.func_78326_a() - this.func_175179_f().func_78256_a(lvt_2_1_)) / 2;
         int lvt_4_1_ = p_181551_1_.func_78328_b() - 59;
         if(!this.field_73839_d.field_71442_b.func_78755_b()) {
            lvt_4_1_ += 14;
         }

         int lvt_5_1_ = (int)((float)this.field_92017_k * 256.0F / 10.0F);
         if(lvt_5_1_ > 255) {
            lvt_5_1_ = 255;
         }

         if(lvt_5_1_ > 0) {
            GlStateManager.func_179094_E();
            GlStateManager.func_179147_l();
            GlStateManager.func_179120_a(770, 771, 1, 0);
            this.func_175179_f().func_175063_a(lvt_2_1_, (float)lvt_3_1_, (float)lvt_4_1_, 16777215 + (lvt_5_1_ << 24));
            GlStateManager.func_179084_k();
            GlStateManager.func_179121_F();
         }
      }

      this.field_73839_d.field_71424_I.func_76319_b();
   }

   public void func_175185_b(ScaledResolution p_175185_1_) {
      this.field_73839_d.field_71424_I.func_76320_a("demo");
      String lvt_2_1_ = "";
      if(this.field_73839_d.field_71441_e.func_82737_E() >= 120500L) {
         lvt_2_1_ = I18n.func_135052_a("demo.demoExpired", new Object[0]);
      } else {
         lvt_2_1_ = I18n.func_135052_a("demo.remainingTime", new Object[]{StringUtils.func_76337_a((int)(120500L - this.field_73839_d.field_71441_e.func_82737_E()))});
      }

      int lvt_3_1_ = this.func_175179_f().func_78256_a(lvt_2_1_);
      this.func_175179_f().func_175063_a(lvt_2_1_, (float)(p_175185_1_.func_78326_a() - lvt_3_1_ - 10), 5.0F, 16777215);
      this.field_73839_d.field_71424_I.func_76319_b();
   }

   protected boolean func_175183_b() {
      if(this.field_73839_d.field_71474_y.field_74330_P && !this.field_73839_d.field_71439_g.func_175140_cp() && !this.field_73839_d.field_71474_y.field_178879_v) {
         return false;
      } else if(this.field_73839_d.field_71442_b.func_78747_a()) {
         if(this.field_73839_d.field_147125_j != null) {
            return true;
         } else {
            if(this.field_73839_d.field_71476_x != null && this.field_73839_d.field_71476_x.field_72313_a == MovingObjectPosition.MovingObjectType.BLOCK) {
               BlockPos lvt_1_1_ = this.field_73839_d.field_71476_x.func_178782_a();
               if(this.field_73839_d.field_71441_e.func_175625_s(lvt_1_1_) instanceof IInventory) {
                  return true;
               }
            }

            return false;
         }
      } else {
         return true;
      }
   }

   public void func_180478_c(ScaledResolution p_180478_1_) {
      this.field_152127_m.func_152437_a(p_180478_1_.func_78326_a() - 10, 10);
   }

   private void func_180475_a(ScoreObjective p_180475_1_, ScaledResolution p_180475_2_) {
      Scoreboard lvt_3_1_ = p_180475_1_.func_96682_a();
      Collection<Score> lvt_4_1_ = lvt_3_1_.func_96534_i(p_180475_1_);
      List<Score> lvt_5_1_ = Lists.newArrayList(Iterables.filter(lvt_4_1_, new Predicate<Score>() {
         public boolean apply(Score p_apply_1_) {
            return p_apply_1_.func_96653_e() != null && !p_apply_1_.func_96653_e().startsWith("#");
         }

         // $FF: synthetic method
         public boolean apply(Object p_apply_1_) {
            return this.apply((Score)p_apply_1_);
         }
      }));
      if(lvt_5_1_.size() > 15) {
         lvt_4_1_ = Lists.newArrayList(Iterables.skip(lvt_5_1_, lvt_4_1_.size() - 15));
      } else {
         lvt_4_1_ = lvt_5_1_;
      }

      int lvt_6_1_ = this.func_175179_f().func_78256_a(p_180475_1_.func_96678_d());

      for(Score lvt_8_1_ : lvt_4_1_) {
         ScorePlayerTeam lvt_9_1_ = lvt_3_1_.func_96509_i(lvt_8_1_.func_96653_e());
         String lvt_10_1_ = ScorePlayerTeam.func_96667_a(lvt_9_1_, lvt_8_1_.func_96653_e()) + ": " + EnumChatFormatting.RED + lvt_8_1_.func_96652_c();
         lvt_6_1_ = Math.max(lvt_6_1_, this.func_175179_f().func_78256_a(lvt_10_1_));
      }

      int lvt_7_2_ = lvt_4_1_.size() * this.func_175179_f().field_78288_b;
      int lvt_8_2_ = p_180475_2_.func_78328_b() / 2 + lvt_7_2_ / 3;
      int lvt_9_2_ = 3;
      int lvt_10_2_ = p_180475_2_.func_78326_a() - lvt_6_1_ - lvt_9_2_;
      int lvt_11_1_ = 0;

      for(Score lvt_13_1_ : lvt_4_1_) {
         ++lvt_11_1_;
         ScorePlayerTeam lvt_14_1_ = lvt_3_1_.func_96509_i(lvt_13_1_.func_96653_e());
         String lvt_15_1_ = ScorePlayerTeam.func_96667_a(lvt_14_1_, lvt_13_1_.func_96653_e());
         String lvt_16_1_ = EnumChatFormatting.RED + "" + lvt_13_1_.func_96652_c();
         int lvt_18_1_ = lvt_8_2_ - lvt_11_1_ * this.func_175179_f().field_78288_b;
         int lvt_19_1_ = p_180475_2_.func_78326_a() - lvt_9_2_ + 2;
         func_73734_a(lvt_10_2_ - 2, lvt_18_1_, lvt_19_1_, lvt_18_1_ + this.func_175179_f().field_78288_b, 1342177280);
         this.func_175179_f().func_78276_b(lvt_15_1_, lvt_10_2_, lvt_18_1_, 553648127);
         this.func_175179_f().func_78276_b(lvt_16_1_, lvt_19_1_ - this.func_175179_f().func_78256_a(lvt_16_1_), lvt_18_1_, 553648127);
         if(lvt_11_1_ == lvt_4_1_.size()) {
            String lvt_20_1_ = p_180475_1_.func_96678_d();
            func_73734_a(lvt_10_2_ - 2, lvt_18_1_ - this.func_175179_f().field_78288_b - 1, lvt_19_1_, lvt_18_1_ - 1, 1610612736);
            func_73734_a(lvt_10_2_ - 2, lvt_18_1_ - 1, lvt_19_1_, lvt_18_1_, 1342177280);
            this.func_175179_f().func_78276_b(lvt_20_1_, lvt_10_2_ + lvt_6_1_ / 2 - this.func_175179_f().func_78256_a(lvt_20_1_) / 2, lvt_18_1_ - this.func_175179_f().field_78288_b, 553648127);
         }
      }

   }

   private void func_180477_d(ScaledResolution p_180477_1_) {
      if(this.field_73839_d.func_175606_aa() instanceof EntityPlayer) {
         EntityPlayer lvt_2_1_ = (EntityPlayer)this.field_73839_d.func_175606_aa();
         int lvt_3_1_ = MathHelper.func_76123_f(lvt_2_1_.func_110143_aJ());
         boolean lvt_4_1_ = this.field_175191_F > (long)this.field_73837_f && (this.field_175191_F - (long)this.field_73837_f) / 3L % 2L == 1L;
         if(lvt_3_1_ < this.field_175194_C && lvt_2_1_.field_70172_ad > 0) {
            this.field_175190_E = Minecraft.func_71386_F();
            this.field_175191_F = (long)(this.field_73837_f + 20);
         } else if(lvt_3_1_ > this.field_175194_C && lvt_2_1_.field_70172_ad > 0) {
            this.field_175190_E = Minecraft.func_71386_F();
            this.field_175191_F = (long)(this.field_73837_f + 10);
         }

         if(Minecraft.func_71386_F() - this.field_175190_E > 1000L) {
            this.field_175194_C = lvt_3_1_;
            this.field_175189_D = lvt_3_1_;
            this.field_175190_E = Minecraft.func_71386_F();
         }

         this.field_175194_C = lvt_3_1_;
         int lvt_5_1_ = this.field_175189_D;
         this.field_73842_c.setSeed((long)(this.field_73837_f * 312871));
         boolean lvt_6_1_ = false;
         FoodStats lvt_7_1_ = lvt_2_1_.func_71024_bL();
         int lvt_8_1_ = lvt_7_1_.func_75116_a();
         int lvt_9_1_ = lvt_7_1_.func_75120_b();
         IAttributeInstance lvt_10_1_ = lvt_2_1_.func_110148_a(SharedMonsterAttributes.field_111267_a);
         int lvt_11_1_ = p_180477_1_.func_78326_a() / 2 - 91;
         int lvt_12_1_ = p_180477_1_.func_78326_a() / 2 + 91;
         int lvt_13_1_ = p_180477_1_.func_78328_b() - 39;
         float lvt_14_1_ = (float)lvt_10_1_.func_111126_e();
         float lvt_15_1_ = lvt_2_1_.func_110139_bj();
         int lvt_16_1_ = MathHelper.func_76123_f((lvt_14_1_ + lvt_15_1_) / 2.0F / 10.0F);
         int lvt_17_1_ = Math.max(10 - (lvt_16_1_ - 2), 3);
         int lvt_18_1_ = lvt_13_1_ - (lvt_16_1_ - 1) * lvt_17_1_ - 10;
         float lvt_19_1_ = lvt_15_1_;
         int lvt_20_1_ = lvt_2_1_.func_70658_aO();
         int lvt_21_1_ = -1;
         if(lvt_2_1_.func_70644_a(Potion.field_76428_l)) {
            lvt_21_1_ = this.field_73837_f % MathHelper.func_76123_f(lvt_14_1_ + 5.0F);
         }

         this.field_73839_d.field_71424_I.func_76320_a("armor");

         for(int lvt_22_1_ = 0; lvt_22_1_ < 10; ++lvt_22_1_) {
            if(lvt_20_1_ > 0) {
               int lvt_23_1_ = lvt_11_1_ + lvt_22_1_ * 8;
               if(lvt_22_1_ * 2 + 1 < lvt_20_1_) {
                  this.func_73729_b(lvt_23_1_, lvt_18_1_, 34, 9, 9, 9);
               }

               if(lvt_22_1_ * 2 + 1 == lvt_20_1_) {
                  this.func_73729_b(lvt_23_1_, lvt_18_1_, 25, 9, 9, 9);
               }

               if(lvt_22_1_ * 2 + 1 > lvt_20_1_) {
                  this.func_73729_b(lvt_23_1_, lvt_18_1_, 16, 9, 9, 9);
               }
            }
         }

         this.field_73839_d.field_71424_I.func_76318_c("health");

         for(int lvt_22_2_ = MathHelper.func_76123_f((lvt_14_1_ + lvt_15_1_) / 2.0F) - 1; lvt_22_2_ >= 0; --lvt_22_2_) {
            int lvt_23_2_ = 16;
            if(lvt_2_1_.func_70644_a(Potion.field_76436_u)) {
               lvt_23_2_ += 36;
            } else if(lvt_2_1_.func_70644_a(Potion.field_82731_v)) {
               lvt_23_2_ += 72;
            }

            int lvt_24_1_ = 0;
            if(lvt_4_1_) {
               lvt_24_1_ = 1;
            }

            int lvt_25_1_ = MathHelper.func_76123_f((float)(lvt_22_2_ + 1) / 10.0F) - 1;
            int lvt_26_1_ = lvt_11_1_ + lvt_22_2_ % 10 * 8;
            int lvt_27_1_ = lvt_13_1_ - lvt_25_1_ * lvt_17_1_;
            if(lvt_3_1_ <= 4) {
               lvt_27_1_ += this.field_73842_c.nextInt(2);
            }

            if(lvt_22_2_ == lvt_21_1_) {
               lvt_27_1_ -= 2;
            }

            int lvt_28_1_ = 0;
            if(lvt_2_1_.field_70170_p.func_72912_H().func_76093_s()) {
               lvt_28_1_ = 5;
            }

            this.func_73729_b(lvt_26_1_, lvt_27_1_, 16 + lvt_24_1_ * 9, 9 * lvt_28_1_, 9, 9);
            if(lvt_4_1_) {
               if(lvt_22_2_ * 2 + 1 < lvt_5_1_) {
                  this.func_73729_b(lvt_26_1_, lvt_27_1_, lvt_23_2_ + 54, 9 * lvt_28_1_, 9, 9);
               }

               if(lvt_22_2_ * 2 + 1 == lvt_5_1_) {
                  this.func_73729_b(lvt_26_1_, lvt_27_1_, lvt_23_2_ + 63, 9 * lvt_28_1_, 9, 9);
               }
            }

            if(lvt_19_1_ > 0.0F) {
               if(lvt_19_1_ == lvt_15_1_ && lvt_15_1_ % 2.0F == 1.0F) {
                  this.func_73729_b(lvt_26_1_, lvt_27_1_, lvt_23_2_ + 153, 9 * lvt_28_1_, 9, 9);
               } else {
                  this.func_73729_b(lvt_26_1_, lvt_27_1_, lvt_23_2_ + 144, 9 * lvt_28_1_, 9, 9);
               }

               lvt_19_1_ -= 2.0F;
            } else {
               if(lvt_22_2_ * 2 + 1 < lvt_3_1_) {
                  this.func_73729_b(lvt_26_1_, lvt_27_1_, lvt_23_2_ + 36, 9 * lvt_28_1_, 9, 9);
               }

               if(lvt_22_2_ * 2 + 1 == lvt_3_1_) {
                  this.func_73729_b(lvt_26_1_, lvt_27_1_, lvt_23_2_ + 45, 9 * lvt_28_1_, 9, 9);
               }
            }
         }

         Entity lvt_22_3_ = lvt_2_1_.field_70154_o;
         if(lvt_22_3_ == null) {
            this.field_73839_d.field_71424_I.func_76318_c("food");

            for(int lvt_23_3_ = 0; lvt_23_3_ < 10; ++lvt_23_3_) {
               int lvt_24_2_ = lvt_13_1_;
               int lvt_25_2_ = 16;
               int lvt_26_2_ = 0;
               if(lvt_2_1_.func_70644_a(Potion.field_76438_s)) {
                  lvt_25_2_ += 36;
                  lvt_26_2_ = 13;
               }

               if(lvt_2_1_.func_71024_bL().func_75115_e() <= 0.0F && this.field_73837_f % (lvt_8_1_ * 3 + 1) == 0) {
                  lvt_24_2_ = lvt_13_1_ + (this.field_73842_c.nextInt(3) - 1);
               }

               if(lvt_6_1_) {
                  lvt_26_2_ = 1;
               }

               int lvt_27_2_ = lvt_12_1_ - lvt_23_3_ * 8 - 9;
               this.func_73729_b(lvt_27_2_, lvt_24_2_, 16 + lvt_26_2_ * 9, 27, 9, 9);
               if(lvt_6_1_) {
                  if(lvt_23_3_ * 2 + 1 < lvt_9_1_) {
                     this.func_73729_b(lvt_27_2_, lvt_24_2_, lvt_25_2_ + 54, 27, 9, 9);
                  }

                  if(lvt_23_3_ * 2 + 1 == lvt_9_1_) {
                     this.func_73729_b(lvt_27_2_, lvt_24_2_, lvt_25_2_ + 63, 27, 9, 9);
                  }
               }

               if(lvt_23_3_ * 2 + 1 < lvt_8_1_) {
                  this.func_73729_b(lvt_27_2_, lvt_24_2_, lvt_25_2_ + 36, 27, 9, 9);
               }

               if(lvt_23_3_ * 2 + 1 == lvt_8_1_) {
                  this.func_73729_b(lvt_27_2_, lvt_24_2_, lvt_25_2_ + 45, 27, 9, 9);
               }
            }
         } else if(lvt_22_3_ instanceof EntityLivingBase) {
            this.field_73839_d.field_71424_I.func_76318_c("mountHealth");
            EntityLivingBase lvt_23_4_ = (EntityLivingBase)lvt_22_3_;
            int lvt_24_3_ = (int)Math.ceil((double)lvt_23_4_.func_110143_aJ());
            float lvt_25_3_ = lvt_23_4_.func_110138_aP();
            int lvt_26_3_ = (int)(lvt_25_3_ + 0.5F) / 2;
            if(lvt_26_3_ > 30) {
               lvt_26_3_ = 30;
            }

            int lvt_27_3_ = lvt_13_1_;

            for(int lvt_28_2_ = 0; lvt_26_3_ > 0; lvt_28_2_ += 20) {
               int lvt_29_1_ = Math.min(lvt_26_3_, 10);
               lvt_26_3_ -= lvt_29_1_;

               for(int lvt_30_1_ = 0; lvt_30_1_ < lvt_29_1_; ++lvt_30_1_) {
                  int lvt_31_1_ = 52;
                  int lvt_32_1_ = 0;
                  if(lvt_6_1_) {
                     lvt_32_1_ = 1;
                  }

                  int lvt_33_1_ = lvt_12_1_ - lvt_30_1_ * 8 - 9;
                  this.func_73729_b(lvt_33_1_, lvt_27_3_, lvt_31_1_ + lvt_32_1_ * 9, 9, 9, 9);
                  if(lvt_30_1_ * 2 + 1 + lvt_28_2_ < lvt_24_3_) {
                     this.func_73729_b(lvt_33_1_, lvt_27_3_, lvt_31_1_ + 36, 9, 9, 9);
                  }

                  if(lvt_30_1_ * 2 + 1 + lvt_28_2_ == lvt_24_3_) {
                     this.func_73729_b(lvt_33_1_, lvt_27_3_, lvt_31_1_ + 45, 9, 9, 9);
                  }
               }

               lvt_27_3_ -= 10;
            }
         }

         this.field_73839_d.field_71424_I.func_76318_c("air");
         if(lvt_2_1_.func_70055_a(Material.field_151586_h)) {
            int lvt_23_5_ = this.field_73839_d.field_71439_g.func_70086_ai();
            int lvt_24_4_ = MathHelper.func_76143_f((double)(lvt_23_5_ - 2) * 10.0D / 300.0D);
            int lvt_25_4_ = MathHelper.func_76143_f((double)lvt_23_5_ * 10.0D / 300.0D) - lvt_24_4_;

            for(int lvt_26_4_ = 0; lvt_26_4_ < lvt_24_4_ + lvt_25_4_; ++lvt_26_4_) {
               if(lvt_26_4_ < lvt_24_4_) {
                  this.func_73729_b(lvt_12_1_ - lvt_26_4_ * 8 - 9, lvt_18_1_, 16, 18, 9, 9);
               } else {
                  this.func_73729_b(lvt_12_1_ - lvt_26_4_ * 8 - 9, lvt_18_1_, 25, 18, 9, 9);
               }
            }
         }

         this.field_73839_d.field_71424_I.func_76319_b();
      }
   }

   private void func_73828_d() {
      if(BossStatus.field_82827_c != null && BossStatus.field_82826_b > 0) {
         --BossStatus.field_82826_b;
         FontRenderer lvt_1_1_ = this.field_73839_d.field_71466_p;
         ScaledResolution lvt_2_1_ = new ScaledResolution(this.field_73839_d);
         int lvt_3_1_ = lvt_2_1_.func_78326_a();
         int lvt_4_1_ = 182;
         int lvt_5_1_ = lvt_3_1_ / 2 - lvt_4_1_ / 2;
         int lvt_6_1_ = (int)(BossStatus.field_82828_a * (float)(lvt_4_1_ + 1));
         int lvt_7_1_ = 12;
         this.func_73729_b(lvt_5_1_, lvt_7_1_, 0, 74, lvt_4_1_, 5);
         this.func_73729_b(lvt_5_1_, lvt_7_1_, 0, 74, lvt_4_1_, 5);
         if(lvt_6_1_ > 0) {
            this.func_73729_b(lvt_5_1_, lvt_7_1_, 0, 79, lvt_6_1_, 5);
         }

         String lvt_8_1_ = BossStatus.field_82827_c;
         this.func_175179_f().func_175063_a(lvt_8_1_, (float)(lvt_3_1_ / 2 - this.func_175179_f().func_78256_a(lvt_8_1_) / 2), (float)(lvt_7_1_ - 10), 16777215);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         this.field_73839_d.func_110434_K().func_110577_a(field_110324_m);
      }
   }

   private void func_180476_e(ScaledResolution p_180476_1_) {
      GlStateManager.func_179097_i();
      GlStateManager.func_179132_a(false);
      GlStateManager.func_179120_a(770, 771, 1, 0);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179118_c();
      this.field_73839_d.func_110434_K().func_110577_a(field_110328_d);
      Tessellator lvt_2_1_ = Tessellator.func_178181_a();
      WorldRenderer lvt_3_1_ = lvt_2_1_.func_178180_c();
      lvt_3_1_.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      lvt_3_1_.func_181662_b(0.0D, (double)p_180476_1_.func_78328_b(), -90.0D).func_181673_a(0.0D, 1.0D).func_181675_d();
      lvt_3_1_.func_181662_b((double)p_180476_1_.func_78326_a(), (double)p_180476_1_.func_78328_b(), -90.0D).func_181673_a(1.0D, 1.0D).func_181675_d();
      lvt_3_1_.func_181662_b((double)p_180476_1_.func_78326_a(), 0.0D, -90.0D).func_181673_a(1.0D, 0.0D).func_181675_d();
      lvt_3_1_.func_181662_b(0.0D, 0.0D, -90.0D).func_181673_a(0.0D, 0.0D).func_181675_d();
      lvt_2_1_.func_78381_a();
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179126_j();
      GlStateManager.func_179141_d();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void func_180480_a(float p_180480_1_, ScaledResolution p_180480_2_) {
      p_180480_1_ = 1.0F - p_180480_1_;
      p_180480_1_ = MathHelper.func_76131_a(p_180480_1_, 0.0F, 1.0F);
      WorldBorder lvt_3_1_ = this.field_73839_d.field_71441_e.func_175723_af();
      float lvt_4_1_ = (float)lvt_3_1_.func_177745_a(this.field_73839_d.field_71439_g);
      double lvt_5_1_ = Math.min(lvt_3_1_.func_177749_o() * (double)lvt_3_1_.func_177740_p() * 1000.0D, Math.abs(lvt_3_1_.func_177751_j() - lvt_3_1_.func_177741_h()));
      double lvt_7_1_ = Math.max((double)lvt_3_1_.func_177748_q(), lvt_5_1_);
      if((double)lvt_4_1_ < lvt_7_1_) {
         lvt_4_1_ = 1.0F - (float)((double)lvt_4_1_ / lvt_7_1_);
      } else {
         lvt_4_1_ = 0.0F;
      }

      this.field_73843_a = (float)((double)this.field_73843_a + (double)(p_180480_1_ - this.field_73843_a) * 0.01D);
      GlStateManager.func_179097_i();
      GlStateManager.func_179132_a(false);
      GlStateManager.func_179120_a(0, 769, 1, 0);
      if(lvt_4_1_ > 0.0F) {
         GlStateManager.func_179131_c(0.0F, lvt_4_1_, lvt_4_1_, 1.0F);
      } else {
         GlStateManager.func_179131_c(this.field_73843_a, this.field_73843_a, this.field_73843_a, 1.0F);
      }

      this.field_73839_d.func_110434_K().func_110577_a(field_110329_b);
      Tessellator lvt_9_1_ = Tessellator.func_178181_a();
      WorldRenderer lvt_10_1_ = lvt_9_1_.func_178180_c();
      lvt_10_1_.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      lvt_10_1_.func_181662_b(0.0D, (double)p_180480_2_.func_78328_b(), -90.0D).func_181673_a(0.0D, 1.0D).func_181675_d();
      lvt_10_1_.func_181662_b((double)p_180480_2_.func_78326_a(), (double)p_180480_2_.func_78328_b(), -90.0D).func_181673_a(1.0D, 1.0D).func_181675_d();
      lvt_10_1_.func_181662_b((double)p_180480_2_.func_78326_a(), 0.0D, -90.0D).func_181673_a(1.0D, 0.0D).func_181675_d();
      lvt_10_1_.func_181662_b(0.0D, 0.0D, -90.0D).func_181673_a(0.0D, 0.0D).func_181675_d();
      lvt_9_1_.func_78381_a();
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179126_j();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179120_a(770, 771, 1, 0);
   }

   private void func_180474_b(float p_180474_1_, ScaledResolution p_180474_2_) {
      if(p_180474_1_ < 1.0F) {
         p_180474_1_ = p_180474_1_ * p_180474_1_;
         p_180474_1_ = p_180474_1_ * p_180474_1_;
         p_180474_1_ = p_180474_1_ * 0.8F + 0.2F;
      }

      GlStateManager.func_179118_c();
      GlStateManager.func_179097_i();
      GlStateManager.func_179132_a(false);
      GlStateManager.func_179120_a(770, 771, 1, 0);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, p_180474_1_);
      this.field_73839_d.func_110434_K().func_110577_a(TextureMap.field_110575_b);
      TextureAtlasSprite lvt_3_1_ = this.field_73839_d.func_175602_ab().func_175023_a().func_178122_a(Blocks.field_150427_aO.func_176223_P());
      float lvt_4_1_ = lvt_3_1_.func_94209_e();
      float lvt_5_1_ = lvt_3_1_.func_94206_g();
      float lvt_6_1_ = lvt_3_1_.func_94212_f();
      float lvt_7_1_ = lvt_3_1_.func_94210_h();
      Tessellator lvt_8_1_ = Tessellator.func_178181_a();
      WorldRenderer lvt_9_1_ = lvt_8_1_.func_178180_c();
      lvt_9_1_.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      lvt_9_1_.func_181662_b(0.0D, (double)p_180474_2_.func_78328_b(), -90.0D).func_181673_a((double)lvt_4_1_, (double)lvt_7_1_).func_181675_d();
      lvt_9_1_.func_181662_b((double)p_180474_2_.func_78326_a(), (double)p_180474_2_.func_78328_b(), -90.0D).func_181673_a((double)lvt_6_1_, (double)lvt_7_1_).func_181675_d();
      lvt_9_1_.func_181662_b((double)p_180474_2_.func_78326_a(), 0.0D, -90.0D).func_181673_a((double)lvt_6_1_, (double)lvt_5_1_).func_181675_d();
      lvt_9_1_.func_181662_b(0.0D, 0.0D, -90.0D).func_181673_a((double)lvt_4_1_, (double)lvt_5_1_).func_181675_d();
      lvt_8_1_.func_78381_a();
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179126_j();
      GlStateManager.func_179141_d();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void func_175184_a(int p_175184_1_, int p_175184_2_, int p_175184_3_, float p_175184_4_, EntityPlayer p_175184_5_) {
      ItemStack lvt_6_1_ = p_175184_5_.field_71071_by.field_70462_a[p_175184_1_];
      if(lvt_6_1_ != null) {
         float lvt_7_1_ = (float)lvt_6_1_.field_77992_b - p_175184_4_;
         if(lvt_7_1_ > 0.0F) {
            GlStateManager.func_179094_E();
            float lvt_8_1_ = 1.0F + lvt_7_1_ / 5.0F;
            GlStateManager.func_179109_b((float)(p_175184_2_ + 8), (float)(p_175184_3_ + 12), 0.0F);
            GlStateManager.func_179152_a(1.0F / lvt_8_1_, (lvt_8_1_ + 1.0F) / 2.0F, 1.0F);
            GlStateManager.func_179109_b((float)(-(p_175184_2_ + 8)), (float)(-(p_175184_3_ + 12)), 0.0F);
         }

         this.field_73841_b.func_180450_b(lvt_6_1_, p_175184_2_, p_175184_3_);
         if(lvt_7_1_ > 0.0F) {
            GlStateManager.func_179121_F();
         }

         this.field_73841_b.func_175030_a(this.field_73839_d.field_71466_p, lvt_6_1_, p_175184_2_, p_175184_3_);
      }
   }

   public void func_73831_a() {
      if(this.field_73845_h > 0) {
         --this.field_73845_h;
      }

      if(this.field_175195_w > 0) {
         --this.field_175195_w;
         if(this.field_175195_w <= 0) {
            this.field_175201_x = "";
            this.field_175200_y = "";
         }
      }

      ++this.field_73837_f;
      this.field_152127_m.func_152439_a();
      if(this.field_73839_d.field_71439_g != null) {
         ItemStack lvt_1_1_ = this.field_73839_d.field_71439_g.field_71071_by.func_70448_g();
         if(lvt_1_1_ == null) {
            this.field_92017_k = 0;
         } else if(this.field_92016_l != null && lvt_1_1_.func_77973_b() == this.field_92016_l.func_77973_b() && ItemStack.func_77970_a(lvt_1_1_, this.field_92016_l) && (lvt_1_1_.func_77984_f() || lvt_1_1_.func_77960_j() == this.field_92016_l.func_77960_j())) {
            if(this.field_92017_k > 0) {
               --this.field_92017_k;
            }
         } else {
            this.field_92017_k = 40;
         }

         this.field_92016_l = lvt_1_1_;
      }

   }

   public void func_73833_a(String p_73833_1_) {
      this.func_110326_a(I18n.func_135052_a("record.nowPlaying", new Object[]{p_73833_1_}), true);
   }

   public void func_110326_a(String p_110326_1_, boolean p_110326_2_) {
      this.field_73838_g = p_110326_1_;
      this.field_73845_h = 60;
      this.field_73844_j = p_110326_2_;
   }

   public void func_175178_a(String p_175178_1_, String p_175178_2_, int p_175178_3_, int p_175178_4_, int p_175178_5_) {
      if(p_175178_1_ == null && p_175178_2_ == null && p_175178_3_ < 0 && p_175178_4_ < 0 && p_175178_5_ < 0) {
         this.field_175201_x = "";
         this.field_175200_y = "";
         this.field_175195_w = 0;
      } else if(p_175178_1_ != null) {
         this.field_175201_x = p_175178_1_;
         this.field_175195_w = this.field_175199_z + this.field_175192_A + this.field_175193_B;
      } else if(p_175178_2_ != null) {
         this.field_175200_y = p_175178_2_;
      } else {
         if(p_175178_3_ >= 0) {
            this.field_175199_z = p_175178_3_;
         }

         if(p_175178_4_ >= 0) {
            this.field_175192_A = p_175178_4_;
         }

         if(p_175178_5_ >= 0) {
            this.field_175193_B = p_175178_5_;
         }

         if(this.field_175195_w > 0) {
            this.field_175195_w = this.field_175199_z + this.field_175192_A + this.field_175193_B;
         }

      }
   }

   public void func_175188_a(IChatComponent p_175188_1_, boolean p_175188_2_) {
      this.func_110326_a(p_175188_1_.func_150260_c(), p_175188_2_);
   }

   public GuiNewChat func_146158_b() {
      return this.field_73840_e;
   }

   public int func_73834_c() {
      return this.field_73837_f;
   }

   public FontRenderer func_175179_f() {
      return this.field_73839_d.field_71466_p;
   }

   public GuiSpectator func_175187_g() {
      return this.field_175197_u;
   }

   public GuiPlayerTabOverlay func_175181_h() {
      return this.field_175196_v;
   }

   public void func_181029_i() {
      this.field_175196_v.func_181030_a();
   }
}
