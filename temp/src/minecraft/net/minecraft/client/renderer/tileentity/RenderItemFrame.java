package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureCompass;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import org.lwjgl.opengl.GL11;

public class RenderItemFrame extends Render<EntityItemFrame> {
   private static final ResourceLocation field_110789_a = new ResourceLocation("textures/map/map_background.png");
   private final Minecraft field_147917_g = Minecraft.func_71410_x();
   private final ModelResourceLocation field_177072_f = new ModelResourceLocation("item_frame", "normal");
   private final ModelResourceLocation field_177073_g = new ModelResourceLocation("item_frame", "map");
   private RenderItem field_177074_h;

   public RenderItemFrame(RenderManager p_i46166_1_, RenderItem p_i46166_2_) {
      super(p_i46166_1_);
      this.field_177074_h = p_i46166_2_;
   }

   public void func_76986_a(EntityItemFrame p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      GlStateManager.func_179094_E();
      BlockPos lvt_10_1_ = p_76986_1_.func_174857_n();
      double lvt_11_1_ = (double)lvt_10_1_.func_177958_n() - p_76986_1_.field_70165_t + p_76986_2_;
      double lvt_13_1_ = (double)lvt_10_1_.func_177956_o() - p_76986_1_.field_70163_u + p_76986_4_;
      double lvt_15_1_ = (double)lvt_10_1_.func_177952_p() - p_76986_1_.field_70161_v + p_76986_6_;
      GlStateManager.func_179137_b(lvt_11_1_ + 0.5D, lvt_13_1_ + 0.5D, lvt_15_1_ + 0.5D);
      GlStateManager.func_179114_b(180.0F - p_76986_1_.field_70177_z, 0.0F, 1.0F, 0.0F);
      this.field_76990_c.field_78724_e.func_110577_a(TextureMap.field_110575_b);
      BlockRendererDispatcher lvt_17_1_ = this.field_147917_g.func_175602_ab();
      ModelManager lvt_18_1_ = lvt_17_1_.func_175023_a().func_178126_b();
      IBakedModel lvt_19_1_;
      if(p_76986_1_.func_82335_i() != null && p_76986_1_.func_82335_i().func_77973_b() == Items.field_151098_aY) {
         lvt_19_1_ = lvt_18_1_.func_174953_a(this.field_177073_g);
      } else {
         lvt_19_1_ = lvt_18_1_.func_174953_a(this.field_177072_f);
      }

      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b(-0.5F, -0.5F, -0.5F);
      lvt_17_1_.func_175019_b().func_178262_a(lvt_19_1_, 1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179121_F();
      GlStateManager.func_179109_b(0.0F, 0.0F, 0.4375F);
      this.func_82402_b(p_76986_1_);
      GlStateManager.func_179121_F();
      this.func_177067_a(p_76986_1_, p_76986_2_ + (double)((float)p_76986_1_.field_174860_b.func_82601_c() * 0.3F), p_76986_4_ - 0.25D, p_76986_6_ + (double)((float)p_76986_1_.field_174860_b.func_82599_e() * 0.3F));
   }

   protected ResourceLocation func_110775_a(EntityItemFrame p_110775_1_) {
      return null;
   }

   private void func_82402_b(EntityItemFrame p_82402_1_) {
      ItemStack lvt_2_1_ = p_82402_1_.func_82335_i();
      if(lvt_2_1_ != null) {
         EntityItem lvt_3_1_ = new EntityItem(p_82402_1_.field_70170_p, 0.0D, 0.0D, 0.0D, lvt_2_1_);
         Item lvt_4_1_ = lvt_3_1_.func_92059_d().func_77973_b();
         lvt_3_1_.func_92059_d().field_77994_a = 1;
         lvt_3_1_.field_70290_d = 0.0F;
         GlStateManager.func_179094_E();
         GlStateManager.func_179140_f();
         int lvt_5_1_ = p_82402_1_.func_82333_j();
         if(lvt_4_1_ == Items.field_151098_aY) {
            lvt_5_1_ = lvt_5_1_ % 4 * 2;
         }

         GlStateManager.func_179114_b((float)lvt_5_1_ * 360.0F / 8.0F, 0.0F, 0.0F, 1.0F);
         if(lvt_4_1_ == Items.field_151098_aY) {
            this.field_76990_c.field_78724_e.func_110577_a(field_110789_a);
            GlStateManager.func_179114_b(180.0F, 0.0F, 0.0F, 1.0F);
            float lvt_6_1_ = 0.0078125F;
            GlStateManager.func_179152_a(lvt_6_1_, lvt_6_1_, lvt_6_1_);
            GlStateManager.func_179109_b(-64.0F, -64.0F, 0.0F);
            MapData lvt_7_1_ = Items.field_151098_aY.func_77873_a(lvt_3_1_.func_92059_d(), p_82402_1_.field_70170_p);
            GlStateManager.func_179109_b(0.0F, 0.0F, -1.0F);
            if(lvt_7_1_ != null) {
               this.field_147917_g.field_71460_t.func_147701_i().func_148250_a(lvt_7_1_, true);
            }
         } else {
            TextureAtlasSprite lvt_6_2_ = null;
            if(lvt_4_1_ == Items.field_151111_aL) {
               lvt_6_2_ = this.field_147917_g.func_147117_R().func_110572_b(TextureCompass.field_176608_l);
               this.field_147917_g.func_110434_K().func_110577_a(TextureMap.field_110575_b);
               if(lvt_6_2_ instanceof TextureCompass) {
                  TextureCompass lvt_7_2_ = (TextureCompass)lvt_6_2_;
                  double lvt_8_1_ = lvt_7_2_.field_94244_i;
                  double lvt_10_1_ = lvt_7_2_.field_94242_j;
                  lvt_7_2_.field_94244_i = 0.0D;
                  lvt_7_2_.field_94242_j = 0.0D;
                  lvt_7_2_.func_94241_a(p_82402_1_.field_70170_p, p_82402_1_.field_70165_t, p_82402_1_.field_70161_v, (double)MathHelper.func_76142_g((float)(180 + p_82402_1_.field_174860_b.func_176736_b() * 90)), false, true);
                  lvt_7_2_.field_94244_i = lvt_8_1_;
                  lvt_7_2_.field_94242_j = lvt_10_1_;
               } else {
                  lvt_6_2_ = null;
               }
            }

            GlStateManager.func_179152_a(0.5F, 0.5F, 0.5F);
            if(!this.field_177074_h.func_175050_a(lvt_3_1_.func_92059_d()) || lvt_4_1_ instanceof ItemSkull) {
               GlStateManager.func_179114_b(180.0F, 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.func_179123_a();
            RenderHelper.func_74519_b();
            this.field_177074_h.func_181564_a(lvt_3_1_.func_92059_d(), ItemCameraTransforms.TransformType.FIXED);
            RenderHelper.func_74518_a();
            GlStateManager.func_179099_b();
            if(lvt_6_2_ != null && lvt_6_2_.func_110970_k() > 0) {
               lvt_6_2_.func_94219_l();
            }
         }

         GlStateManager.func_179145_e();
         GlStateManager.func_179121_F();
      }
   }

   protected void func_177067_a(EntityItemFrame p_177067_1_, double p_177067_2_, double p_177067_4_, double p_177067_6_) {
      if(Minecraft.func_71382_s() && p_177067_1_.func_82335_i() != null && p_177067_1_.func_82335_i().func_82837_s() && this.field_76990_c.field_147941_i == p_177067_1_) {
         float lvt_8_1_ = 1.6F;
         float lvt_9_1_ = 0.016666668F * lvt_8_1_;
         double lvt_10_1_ = p_177067_1_.func_70068_e(this.field_76990_c.field_78734_h);
         float lvt_12_1_ = p_177067_1_.func_70093_af()?32.0F:64.0F;
         if(lvt_10_1_ < (double)(lvt_12_1_ * lvt_12_1_)) {
            String lvt_13_1_ = p_177067_1_.func_82335_i().func_82833_r();
            if(p_177067_1_.func_70093_af()) {
               FontRenderer lvt_14_1_ = this.func_76983_a();
               GlStateManager.func_179094_E();
               GlStateManager.func_179109_b((float)p_177067_2_ + 0.0F, (float)p_177067_4_ + p_177067_1_.field_70131_O + 0.5F, (float)p_177067_6_);
               GL11.glNormal3f(0.0F, 1.0F, 0.0F);
               GlStateManager.func_179114_b(-this.field_76990_c.field_78735_i, 0.0F, 1.0F, 0.0F);
               GlStateManager.func_179114_b(this.field_76990_c.field_78732_j, 1.0F, 0.0F, 0.0F);
               GlStateManager.func_179152_a(-lvt_9_1_, -lvt_9_1_, lvt_9_1_);
               GlStateManager.func_179140_f();
               GlStateManager.func_179109_b(0.0F, 0.25F / lvt_9_1_, 0.0F);
               GlStateManager.func_179132_a(false);
               GlStateManager.func_179147_l();
               GlStateManager.func_179112_b(770, 771);
               Tessellator lvt_15_1_ = Tessellator.func_178181_a();
               WorldRenderer lvt_16_1_ = lvt_15_1_.func_178180_c();
               int lvt_17_1_ = lvt_14_1_.func_78256_a(lvt_13_1_) / 2;
               GlStateManager.func_179090_x();
               lvt_16_1_.func_181668_a(7, DefaultVertexFormats.field_181706_f);
               lvt_16_1_.func_181662_b((double)(-lvt_17_1_ - 1), -1.0D, 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
               lvt_16_1_.func_181662_b((double)(-lvt_17_1_ - 1), 8.0D, 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
               lvt_16_1_.func_181662_b((double)(lvt_17_1_ + 1), 8.0D, 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
               lvt_16_1_.func_181662_b((double)(lvt_17_1_ + 1), -1.0D, 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
               lvt_15_1_.func_78381_a();
               GlStateManager.func_179098_w();
               GlStateManager.func_179132_a(true);
               lvt_14_1_.func_78276_b(lvt_13_1_, -lvt_14_1_.func_78256_a(lvt_13_1_) / 2, 0, 553648127);
               GlStateManager.func_179145_e();
               GlStateManager.func_179084_k();
               GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
               GlStateManager.func_179121_F();
            } else {
               this.func_147906_a(p_177067_1_, lvt_13_1_, p_177067_2_, p_177067_4_, p_177067_6_, 64);
            }
         }
      }

   }

   // $FF: synthetic method
   // $FF: bridge method
   protected ResourceLocation func_110775_a(Entity p_110775_1_) {
      return this.func_110775_a((EntityItemFrame)p_110775_1_);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void func_177067_a(Entity p_177067_1_, double p_177067_2_, double p_177067_4_, double p_177067_6_) {
      this.func_177067_a((EntityItemFrame)p_177067_1_, p_177067_2_, p_177067_4_, p_177067_6_);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void func_76986_a(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
      this.func_76986_a((EntityItemFrame)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
   }
}
