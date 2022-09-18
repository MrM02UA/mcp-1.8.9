package net.minecraft.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public abstract class Render<T extends Entity> {
   private static final ResourceLocation field_110778_a = new ResourceLocation("textures/misc/shadow.png");
   protected final RenderManager field_76990_c;
   protected float field_76989_e;
   protected float field_76987_f = 1.0F;

   protected Render(RenderManager p_i46179_1_) {
      this.field_76990_c = p_i46179_1_;
   }

   public boolean func_177071_a(T p_177071_1_, ICamera p_177071_2_, double p_177071_3_, double p_177071_5_, double p_177071_7_) {
      AxisAlignedBB lvt_9_1_ = p_177071_1_.func_174813_aQ();
      if(lvt_9_1_.func_181656_b() || lvt_9_1_.func_72320_b() == 0.0D) {
         lvt_9_1_ = new AxisAlignedBB(p_177071_1_.field_70165_t - 2.0D, p_177071_1_.field_70163_u - 2.0D, p_177071_1_.field_70161_v - 2.0D, p_177071_1_.field_70165_t + 2.0D, p_177071_1_.field_70163_u + 2.0D, p_177071_1_.field_70161_v + 2.0D);
      }

      return p_177071_1_.func_145770_h(p_177071_3_, p_177071_5_, p_177071_7_) && (p_177071_1_.field_70158_ak || p_177071_2_.func_78546_a(lvt_9_1_));
   }

   public void func_76986_a(T p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      this.func_177067_a(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_);
   }

   protected void func_177067_a(T p_177067_1_, double p_177067_2_, double p_177067_4_, double p_177067_6_) {
      if(this.func_177070_b(p_177067_1_)) {
         this.func_147906_a(p_177067_1_, p_177067_1_.func_145748_c_().func_150254_d(), p_177067_2_, p_177067_4_, p_177067_6_, 64);
      }
   }

   protected boolean func_177070_b(T p_177070_1_) {
      return p_177070_1_.func_94059_bO() && p_177070_1_.func_145818_k_();
   }

   protected void func_177069_a(T p_177069_1_, double p_177069_2_, double p_177069_4_, double p_177069_6_, String p_177069_8_, float p_177069_9_, double p_177069_10_) {
      this.func_147906_a(p_177069_1_, p_177069_8_, p_177069_2_, p_177069_4_, p_177069_6_, 64);
   }

   protected abstract ResourceLocation func_110775_a(T var1);

   protected boolean func_180548_c(T p_180548_1_) {
      ResourceLocation lvt_2_1_ = this.func_110775_a(p_180548_1_);
      if(lvt_2_1_ == null) {
         return false;
      } else {
         this.func_110776_a(lvt_2_1_);
         return true;
      }
   }

   public void func_110776_a(ResourceLocation p_110776_1_) {
      this.field_76990_c.field_78724_e.func_110577_a(p_110776_1_);
   }

   private void func_76977_a(Entity p_76977_1_, double p_76977_2_, double p_76977_4_, double p_76977_6_, float p_76977_8_) {
      GlStateManager.func_179140_f();
      TextureMap lvt_9_1_ = Minecraft.func_71410_x().func_147117_R();
      TextureAtlasSprite lvt_10_1_ = lvt_9_1_.func_110572_b("minecraft:blocks/fire_layer_0");
      TextureAtlasSprite lvt_11_1_ = lvt_9_1_.func_110572_b("minecraft:blocks/fire_layer_1");
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b((float)p_76977_2_, (float)p_76977_4_, (float)p_76977_6_);
      float lvt_12_1_ = p_76977_1_.field_70130_N * 1.4F;
      GlStateManager.func_179152_a(lvt_12_1_, lvt_12_1_, lvt_12_1_);
      Tessellator lvt_13_1_ = Tessellator.func_178181_a();
      WorldRenderer lvt_14_1_ = lvt_13_1_.func_178180_c();
      float lvt_15_1_ = 0.5F;
      float lvt_16_1_ = 0.0F;
      float lvt_17_1_ = p_76977_1_.field_70131_O / lvt_12_1_;
      float lvt_18_1_ = (float)(p_76977_1_.field_70163_u - p_76977_1_.func_174813_aQ().field_72338_b);
      GlStateManager.func_179114_b(-this.field_76990_c.field_78735_i, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179109_b(0.0F, 0.0F, -0.3F + (float)((int)lvt_17_1_) * 0.02F);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      float lvt_19_1_ = 0.0F;
      int lvt_20_1_ = 0;
      lvt_14_1_.func_181668_a(7, DefaultVertexFormats.field_181707_g);

      while(lvt_17_1_ > 0.0F) {
         TextureAtlasSprite lvt_21_1_ = lvt_20_1_ % 2 == 0?lvt_10_1_:lvt_11_1_;
         this.func_110776_a(TextureMap.field_110575_b);
         float lvt_22_1_ = lvt_21_1_.func_94209_e();
         float lvt_23_1_ = lvt_21_1_.func_94206_g();
         float lvt_24_1_ = lvt_21_1_.func_94212_f();
         float lvt_25_1_ = lvt_21_1_.func_94210_h();
         if(lvt_20_1_ / 2 % 2 == 0) {
            float lvt_26_1_ = lvt_24_1_;
            lvt_24_1_ = lvt_22_1_;
            lvt_22_1_ = lvt_26_1_;
         }

         lvt_14_1_.func_181662_b((double)(lvt_15_1_ - lvt_16_1_), (double)(0.0F - lvt_18_1_), (double)lvt_19_1_).func_181673_a((double)lvt_24_1_, (double)lvt_25_1_).func_181675_d();
         lvt_14_1_.func_181662_b((double)(-lvt_15_1_ - lvt_16_1_), (double)(0.0F - lvt_18_1_), (double)lvt_19_1_).func_181673_a((double)lvt_22_1_, (double)lvt_25_1_).func_181675_d();
         lvt_14_1_.func_181662_b((double)(-lvt_15_1_ - lvt_16_1_), (double)(1.4F - lvt_18_1_), (double)lvt_19_1_).func_181673_a((double)lvt_22_1_, (double)lvt_23_1_).func_181675_d();
         lvt_14_1_.func_181662_b((double)(lvt_15_1_ - lvt_16_1_), (double)(1.4F - lvt_18_1_), (double)lvt_19_1_).func_181673_a((double)lvt_24_1_, (double)lvt_23_1_).func_181675_d();
         lvt_17_1_ -= 0.45F;
         lvt_18_1_ -= 0.45F;
         lvt_15_1_ *= 0.9F;
         lvt_19_1_ += 0.03F;
         ++lvt_20_1_;
      }

      lvt_13_1_.func_78381_a();
      GlStateManager.func_179121_F();
      GlStateManager.func_179145_e();
   }

   private void func_76975_c(Entity p_76975_1_, double p_76975_2_, double p_76975_4_, double p_76975_6_, float p_76975_8_, float p_76975_9_) {
      GlStateManager.func_179147_l();
      GlStateManager.func_179112_b(770, 771);
      this.field_76990_c.field_78724_e.func_110577_a(field_110778_a);
      World lvt_10_1_ = this.func_76982_b();
      GlStateManager.func_179132_a(false);
      float lvt_11_1_ = this.field_76989_e;
      if(p_76975_1_ instanceof EntityLiving) {
         EntityLiving lvt_12_1_ = (EntityLiving)p_76975_1_;
         lvt_11_1_ *= lvt_12_1_.func_70603_bj();
         if(lvt_12_1_.func_70631_g_()) {
            lvt_11_1_ *= 0.5F;
         }
      }

      double lvt_12_2_ = p_76975_1_.field_70142_S + (p_76975_1_.field_70165_t - p_76975_1_.field_70142_S) * (double)p_76975_9_;
      double lvt_14_1_ = p_76975_1_.field_70137_T + (p_76975_1_.field_70163_u - p_76975_1_.field_70137_T) * (double)p_76975_9_;
      double lvt_16_1_ = p_76975_1_.field_70136_U + (p_76975_1_.field_70161_v - p_76975_1_.field_70136_U) * (double)p_76975_9_;
      int lvt_18_1_ = MathHelper.func_76128_c(lvt_12_2_ - (double)lvt_11_1_);
      int lvt_19_1_ = MathHelper.func_76128_c(lvt_12_2_ + (double)lvt_11_1_);
      int lvt_20_1_ = MathHelper.func_76128_c(lvt_14_1_ - (double)lvt_11_1_);
      int lvt_21_1_ = MathHelper.func_76128_c(lvt_14_1_);
      int lvt_22_1_ = MathHelper.func_76128_c(lvt_16_1_ - (double)lvt_11_1_);
      int lvt_23_1_ = MathHelper.func_76128_c(lvt_16_1_ + (double)lvt_11_1_);
      double lvt_24_1_ = p_76975_2_ - lvt_12_2_;
      double lvt_26_1_ = p_76975_4_ - lvt_14_1_;
      double lvt_28_1_ = p_76975_6_ - lvt_16_1_;
      Tessellator lvt_30_1_ = Tessellator.func_178181_a();
      WorldRenderer lvt_31_1_ = lvt_30_1_.func_178180_c();
      lvt_31_1_.func_181668_a(7, DefaultVertexFormats.field_181709_i);

      for(BlockPos lvt_33_1_ : BlockPos.func_177975_b(new BlockPos(lvt_18_1_, lvt_20_1_, lvt_22_1_), new BlockPos(lvt_19_1_, lvt_21_1_, lvt_23_1_))) {
         Block lvt_34_1_ = lvt_10_1_.func_180495_p(lvt_33_1_.func_177977_b()).func_177230_c();
         if(lvt_34_1_.func_149645_b() != -1 && lvt_10_1_.func_175671_l(lvt_33_1_) > 3) {
            this.func_180549_a(lvt_34_1_, p_76975_2_, p_76975_4_, p_76975_6_, lvt_33_1_, p_76975_8_, lvt_11_1_, lvt_24_1_, lvt_26_1_, lvt_28_1_);
         }
      }

      lvt_30_1_.func_78381_a();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179084_k();
      GlStateManager.func_179132_a(true);
   }

   private World func_76982_b() {
      return this.field_76990_c.field_78722_g;
   }

   private void func_180549_a(Block p_180549_1_, double p_180549_2_, double p_180549_4_, double p_180549_6_, BlockPos p_180549_8_, float p_180549_9_, float p_180549_10_, double p_180549_11_, double p_180549_13_, double p_180549_15_) {
      if(p_180549_1_.func_149686_d()) {
         Tessellator lvt_17_1_ = Tessellator.func_178181_a();
         WorldRenderer lvt_18_1_ = lvt_17_1_.func_178180_c();
         double lvt_19_1_ = ((double)p_180549_9_ - (p_180549_4_ - ((double)p_180549_8_.func_177956_o() + p_180549_13_)) / 2.0D) * 0.5D * (double)this.func_76982_b().func_175724_o(p_180549_8_);
         if(lvt_19_1_ >= 0.0D) {
            if(lvt_19_1_ > 1.0D) {
               lvt_19_1_ = 1.0D;
            }

            double lvt_21_1_ = (double)p_180549_8_.func_177958_n() + p_180549_1_.func_149704_x() + p_180549_11_;
            double lvt_23_1_ = (double)p_180549_8_.func_177958_n() + p_180549_1_.func_149753_y() + p_180549_11_;
            double lvt_25_1_ = (double)p_180549_8_.func_177956_o() + p_180549_1_.func_149665_z() + p_180549_13_ + 0.015625D;
            double lvt_27_1_ = (double)p_180549_8_.func_177952_p() + p_180549_1_.func_149706_B() + p_180549_15_;
            double lvt_29_1_ = (double)p_180549_8_.func_177952_p() + p_180549_1_.func_149693_C() + p_180549_15_;
            float lvt_31_1_ = (float)((p_180549_2_ - lvt_21_1_) / 2.0D / (double)p_180549_10_ + 0.5D);
            float lvt_32_1_ = (float)((p_180549_2_ - lvt_23_1_) / 2.0D / (double)p_180549_10_ + 0.5D);
            float lvt_33_1_ = (float)((p_180549_6_ - lvt_27_1_) / 2.0D / (double)p_180549_10_ + 0.5D);
            float lvt_34_1_ = (float)((p_180549_6_ - lvt_29_1_) / 2.0D / (double)p_180549_10_ + 0.5D);
            lvt_18_1_.func_181662_b(lvt_21_1_, lvt_25_1_, lvt_27_1_).func_181673_a((double)lvt_31_1_, (double)lvt_33_1_).func_181666_a(1.0F, 1.0F, 1.0F, (float)lvt_19_1_).func_181675_d();
            lvt_18_1_.func_181662_b(lvt_21_1_, lvt_25_1_, lvt_29_1_).func_181673_a((double)lvt_31_1_, (double)lvt_34_1_).func_181666_a(1.0F, 1.0F, 1.0F, (float)lvt_19_1_).func_181675_d();
            lvt_18_1_.func_181662_b(lvt_23_1_, lvt_25_1_, lvt_29_1_).func_181673_a((double)lvt_32_1_, (double)lvt_34_1_).func_181666_a(1.0F, 1.0F, 1.0F, (float)lvt_19_1_).func_181675_d();
            lvt_18_1_.func_181662_b(lvt_23_1_, lvt_25_1_, lvt_27_1_).func_181673_a((double)lvt_32_1_, (double)lvt_33_1_).func_181666_a(1.0F, 1.0F, 1.0F, (float)lvt_19_1_).func_181675_d();
         }
      }
   }

   public static void func_76978_a(AxisAlignedBB p_76978_0_, double p_76978_1_, double p_76978_3_, double p_76978_5_) {
      GlStateManager.func_179090_x();
      Tessellator lvt_7_1_ = Tessellator.func_178181_a();
      WorldRenderer lvt_8_1_ = lvt_7_1_.func_178180_c();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      lvt_8_1_.func_178969_c(p_76978_1_, p_76978_3_, p_76978_5_);
      lvt_8_1_.func_181668_a(7, DefaultVertexFormats.field_181708_h);
      lvt_8_1_.func_181662_b(p_76978_0_.field_72340_a, p_76978_0_.field_72337_e, p_76978_0_.field_72339_c).func_181663_c(0.0F, 0.0F, -1.0F).func_181675_d();
      lvt_8_1_.func_181662_b(p_76978_0_.field_72336_d, p_76978_0_.field_72337_e, p_76978_0_.field_72339_c).func_181663_c(0.0F, 0.0F, -1.0F).func_181675_d();
      lvt_8_1_.func_181662_b(p_76978_0_.field_72336_d, p_76978_0_.field_72338_b, p_76978_0_.field_72339_c).func_181663_c(0.0F, 0.0F, -1.0F).func_181675_d();
      lvt_8_1_.func_181662_b(p_76978_0_.field_72340_a, p_76978_0_.field_72338_b, p_76978_0_.field_72339_c).func_181663_c(0.0F, 0.0F, -1.0F).func_181675_d();
      lvt_8_1_.func_181662_b(p_76978_0_.field_72340_a, p_76978_0_.field_72338_b, p_76978_0_.field_72334_f).func_181663_c(0.0F, 0.0F, 1.0F).func_181675_d();
      lvt_8_1_.func_181662_b(p_76978_0_.field_72336_d, p_76978_0_.field_72338_b, p_76978_0_.field_72334_f).func_181663_c(0.0F, 0.0F, 1.0F).func_181675_d();
      lvt_8_1_.func_181662_b(p_76978_0_.field_72336_d, p_76978_0_.field_72337_e, p_76978_0_.field_72334_f).func_181663_c(0.0F, 0.0F, 1.0F).func_181675_d();
      lvt_8_1_.func_181662_b(p_76978_0_.field_72340_a, p_76978_0_.field_72337_e, p_76978_0_.field_72334_f).func_181663_c(0.0F, 0.0F, 1.0F).func_181675_d();
      lvt_8_1_.func_181662_b(p_76978_0_.field_72340_a, p_76978_0_.field_72338_b, p_76978_0_.field_72339_c).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
      lvt_8_1_.func_181662_b(p_76978_0_.field_72336_d, p_76978_0_.field_72338_b, p_76978_0_.field_72339_c).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
      lvt_8_1_.func_181662_b(p_76978_0_.field_72336_d, p_76978_0_.field_72338_b, p_76978_0_.field_72334_f).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
      lvt_8_1_.func_181662_b(p_76978_0_.field_72340_a, p_76978_0_.field_72338_b, p_76978_0_.field_72334_f).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
      lvt_8_1_.func_181662_b(p_76978_0_.field_72340_a, p_76978_0_.field_72337_e, p_76978_0_.field_72334_f).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      lvt_8_1_.func_181662_b(p_76978_0_.field_72336_d, p_76978_0_.field_72337_e, p_76978_0_.field_72334_f).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      lvt_8_1_.func_181662_b(p_76978_0_.field_72336_d, p_76978_0_.field_72337_e, p_76978_0_.field_72339_c).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      lvt_8_1_.func_181662_b(p_76978_0_.field_72340_a, p_76978_0_.field_72337_e, p_76978_0_.field_72339_c).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
      lvt_8_1_.func_181662_b(p_76978_0_.field_72340_a, p_76978_0_.field_72338_b, p_76978_0_.field_72334_f).func_181663_c(-1.0F, 0.0F, 0.0F).func_181675_d();
      lvt_8_1_.func_181662_b(p_76978_0_.field_72340_a, p_76978_0_.field_72337_e, p_76978_0_.field_72334_f).func_181663_c(-1.0F, 0.0F, 0.0F).func_181675_d();
      lvt_8_1_.func_181662_b(p_76978_0_.field_72340_a, p_76978_0_.field_72337_e, p_76978_0_.field_72339_c).func_181663_c(-1.0F, 0.0F, 0.0F).func_181675_d();
      lvt_8_1_.func_181662_b(p_76978_0_.field_72340_a, p_76978_0_.field_72338_b, p_76978_0_.field_72339_c).func_181663_c(-1.0F, 0.0F, 0.0F).func_181675_d();
      lvt_8_1_.func_181662_b(p_76978_0_.field_72336_d, p_76978_0_.field_72338_b, p_76978_0_.field_72339_c).func_181663_c(1.0F, 0.0F, 0.0F).func_181675_d();
      lvt_8_1_.func_181662_b(p_76978_0_.field_72336_d, p_76978_0_.field_72337_e, p_76978_0_.field_72339_c).func_181663_c(1.0F, 0.0F, 0.0F).func_181675_d();
      lvt_8_1_.func_181662_b(p_76978_0_.field_72336_d, p_76978_0_.field_72337_e, p_76978_0_.field_72334_f).func_181663_c(1.0F, 0.0F, 0.0F).func_181675_d();
      lvt_8_1_.func_181662_b(p_76978_0_.field_72336_d, p_76978_0_.field_72338_b, p_76978_0_.field_72334_f).func_181663_c(1.0F, 0.0F, 0.0F).func_181675_d();
      lvt_7_1_.func_78381_a();
      lvt_8_1_.func_178969_c(0.0D, 0.0D, 0.0D);
      GlStateManager.func_179098_w();
   }

   public void func_76979_b(Entity p_76979_1_, double p_76979_2_, double p_76979_4_, double p_76979_6_, float p_76979_8_, float p_76979_9_) {
      if(this.field_76990_c.field_78733_k != null) {
         if(this.field_76990_c.field_78733_k.field_181151_V && this.field_76989_e > 0.0F && !p_76979_1_.func_82150_aj() && this.field_76990_c.func_178627_a()) {
            double lvt_10_1_ = this.field_76990_c.func_78714_a(p_76979_1_.field_70165_t, p_76979_1_.field_70163_u, p_76979_1_.field_70161_v);
            float lvt_12_1_ = (float)((1.0D - lvt_10_1_ / 256.0D) * (double)this.field_76987_f);
            if(lvt_12_1_ > 0.0F) {
               this.func_76975_c(p_76979_1_, p_76979_2_, p_76979_4_, p_76979_6_, lvt_12_1_, p_76979_9_);
            }
         }

         if(p_76979_1_.func_90999_ad() && (!(p_76979_1_ instanceof EntityPlayer) || !((EntityPlayer)p_76979_1_).func_175149_v())) {
            this.func_76977_a(p_76979_1_, p_76979_2_, p_76979_4_, p_76979_6_, p_76979_9_);
         }

      }
   }

   public FontRenderer func_76983_a() {
      return this.field_76990_c.func_78716_a();
   }

   protected void func_147906_a(T p_147906_1_, String p_147906_2_, double p_147906_3_, double p_147906_5_, double p_147906_7_, int p_147906_9_) {
      double lvt_10_1_ = p_147906_1_.func_70068_e(this.field_76990_c.field_78734_h);
      if(lvt_10_1_ <= (double)(p_147906_9_ * p_147906_9_)) {
         FontRenderer lvt_12_1_ = this.func_76983_a();
         float lvt_13_1_ = 1.6F;
         float lvt_14_1_ = 0.016666668F * lvt_13_1_;
         GlStateManager.func_179094_E();
         GlStateManager.func_179109_b((float)p_147906_3_ + 0.0F, (float)p_147906_5_ + p_147906_1_.field_70131_O + 0.5F, (float)p_147906_7_);
         GL11.glNormal3f(0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b(-this.field_76990_c.field_78735_i, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b(this.field_76990_c.field_78732_j, 1.0F, 0.0F, 0.0F);
         GlStateManager.func_179152_a(-lvt_14_1_, -lvt_14_1_, lvt_14_1_);
         GlStateManager.func_179140_f();
         GlStateManager.func_179132_a(false);
         GlStateManager.func_179097_i();
         GlStateManager.func_179147_l();
         GlStateManager.func_179120_a(770, 771, 1, 0);
         Tessellator lvt_15_1_ = Tessellator.func_178181_a();
         WorldRenderer lvt_16_1_ = lvt_15_1_.func_178180_c();
         int lvt_17_1_ = 0;
         if(p_147906_2_.equals("deadmau5")) {
            lvt_17_1_ = -10;
         }

         int lvt_18_1_ = lvt_12_1_.func_78256_a(p_147906_2_) / 2;
         GlStateManager.func_179090_x();
         lvt_16_1_.func_181668_a(7, DefaultVertexFormats.field_181706_f);
         lvt_16_1_.func_181662_b((double)(-lvt_18_1_ - 1), (double)(-1 + lvt_17_1_), 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
         lvt_16_1_.func_181662_b((double)(-lvt_18_1_ - 1), (double)(8 + lvt_17_1_), 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
         lvt_16_1_.func_181662_b((double)(lvt_18_1_ + 1), (double)(8 + lvt_17_1_), 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
         lvt_16_1_.func_181662_b((double)(lvt_18_1_ + 1), (double)(-1 + lvt_17_1_), 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
         lvt_15_1_.func_78381_a();
         GlStateManager.func_179098_w();
         lvt_12_1_.func_78276_b(p_147906_2_, -lvt_12_1_.func_78256_a(p_147906_2_) / 2, lvt_17_1_, 553648127);
         GlStateManager.func_179126_j();
         GlStateManager.func_179132_a(true);
         lvt_12_1_.func_78276_b(p_147906_2_, -lvt_12_1_.func_78256_a(p_147906_2_) / 2, lvt_17_1_, -1);
         GlStateManager.func_179145_e();
         GlStateManager.func_179084_k();
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.func_179121_F();
      }
   }

   public RenderManager func_177068_d() {
      return this.field_76990_c;
   }
}
