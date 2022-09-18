package net.minecraft.client.renderer.tileentity;

import java.nio.FloatBuffer;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.ResourceLocation;

public class TileEntityEndPortalRenderer extends TileEntitySpecialRenderer<TileEntityEndPortal> {
   private static final ResourceLocation field_147529_c = new ResourceLocation("textures/environment/end_sky.png");
   private static final ResourceLocation field_147526_d = new ResourceLocation("textures/entity/end_portal.png");
   private static final Random field_147527_e = new Random(31100L);
   FloatBuffer field_147528_b = GLAllocation.func_74529_h(16);

   public void func_180535_a(TileEntityEndPortal p_180535_1_, double p_180535_2_, double p_180535_4_, double p_180535_6_, float p_180535_8_, int p_180535_9_) {
      float lvt_10_1_ = (float)this.field_147501_a.field_147560_j;
      float lvt_11_1_ = (float)this.field_147501_a.field_147561_k;
      float lvt_12_1_ = (float)this.field_147501_a.field_147558_l;
      GlStateManager.func_179140_f();
      field_147527_e.setSeed(31100L);
      float lvt_13_1_ = 0.75F;

      for(int lvt_14_1_ = 0; lvt_14_1_ < 16; ++lvt_14_1_) {
         GlStateManager.func_179094_E();
         float lvt_15_1_ = (float)(16 - lvt_14_1_);
         float lvt_16_1_ = 0.0625F;
         float lvt_17_1_ = 1.0F / (lvt_15_1_ + 1.0F);
         if(lvt_14_1_ == 0) {
            this.func_147499_a(field_147529_c);
            lvt_17_1_ = 0.1F;
            lvt_15_1_ = 65.0F;
            lvt_16_1_ = 0.125F;
            GlStateManager.func_179147_l();
            GlStateManager.func_179112_b(770, 771);
         }

         if(lvt_14_1_ >= 1) {
            this.func_147499_a(field_147526_d);
         }

         if(lvt_14_1_ == 1) {
            GlStateManager.func_179147_l();
            GlStateManager.func_179112_b(1, 1);
            lvt_16_1_ = 0.5F;
         }

         float lvt_18_1_ = (float)(-(p_180535_4_ + (double)lvt_13_1_));
         float lvt_19_1_ = lvt_18_1_ + (float)ActiveRenderInfo.func_178804_a().field_72448_b;
         float lvt_20_1_ = lvt_18_1_ + lvt_15_1_ + (float)ActiveRenderInfo.func_178804_a().field_72448_b;
         float lvt_21_1_ = lvt_19_1_ / lvt_20_1_;
         lvt_21_1_ = (float)(p_180535_4_ + (double)lvt_13_1_) + lvt_21_1_;
         GlStateManager.func_179109_b(lvt_10_1_, lvt_21_1_, lvt_12_1_);
         GlStateManager.func_179149_a(GlStateManager.TexGen.S, 9217);
         GlStateManager.func_179149_a(GlStateManager.TexGen.T, 9217);
         GlStateManager.func_179149_a(GlStateManager.TexGen.R, 9217);
         GlStateManager.func_179149_a(GlStateManager.TexGen.Q, 9216);
         GlStateManager.func_179105_a(GlStateManager.TexGen.S, 9473, this.func_147525_a(1.0F, 0.0F, 0.0F, 0.0F));
         GlStateManager.func_179105_a(GlStateManager.TexGen.T, 9473, this.func_147525_a(0.0F, 0.0F, 1.0F, 0.0F));
         GlStateManager.func_179105_a(GlStateManager.TexGen.R, 9473, this.func_147525_a(0.0F, 0.0F, 0.0F, 1.0F));
         GlStateManager.func_179105_a(GlStateManager.TexGen.Q, 9474, this.func_147525_a(0.0F, 1.0F, 0.0F, 0.0F));
         GlStateManager.func_179087_a(GlStateManager.TexGen.S);
         GlStateManager.func_179087_a(GlStateManager.TexGen.T);
         GlStateManager.func_179087_a(GlStateManager.TexGen.R);
         GlStateManager.func_179087_a(GlStateManager.TexGen.Q);
         GlStateManager.func_179121_F();
         GlStateManager.func_179128_n(5890);
         GlStateManager.func_179094_E();
         GlStateManager.func_179096_D();
         GlStateManager.func_179109_b(0.0F, (float)(Minecraft.func_71386_F() % 700000L) / 700000.0F, 0.0F);
         GlStateManager.func_179152_a(lvt_16_1_, lvt_16_1_, lvt_16_1_);
         GlStateManager.func_179109_b(0.5F, 0.5F, 0.0F);
         GlStateManager.func_179114_b((float)(lvt_14_1_ * lvt_14_1_ * 4321 + lvt_14_1_ * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.func_179109_b(-0.5F, -0.5F, 0.0F);
         GlStateManager.func_179109_b(-lvt_10_1_, -lvt_12_1_, -lvt_11_1_);
         lvt_19_1_ = lvt_18_1_ + (float)ActiveRenderInfo.func_178804_a().field_72448_b;
         GlStateManager.func_179109_b((float)ActiveRenderInfo.func_178804_a().field_72450_a * lvt_15_1_ / lvt_19_1_, (float)ActiveRenderInfo.func_178804_a().field_72449_c * lvt_15_1_ / lvt_19_1_, -lvt_11_1_);
         Tessellator lvt_20_2_ = Tessellator.func_178181_a();
         WorldRenderer lvt_21_2_ = lvt_20_2_.func_178180_c();
         lvt_21_2_.func_181668_a(7, DefaultVertexFormats.field_181706_f);
         float lvt_22_1_ = (field_147527_e.nextFloat() * 0.5F + 0.1F) * lvt_17_1_;
         float lvt_23_1_ = (field_147527_e.nextFloat() * 0.5F + 0.4F) * lvt_17_1_;
         float lvt_24_1_ = (field_147527_e.nextFloat() * 0.5F + 0.5F) * lvt_17_1_;
         if(lvt_14_1_ == 0) {
            lvt_22_1_ = lvt_23_1_ = lvt_24_1_ = 1.0F * lvt_17_1_;
         }

         lvt_21_2_.func_181662_b(p_180535_2_, p_180535_4_ + (double)lvt_13_1_, p_180535_6_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).func_181675_d();
         lvt_21_2_.func_181662_b(p_180535_2_, p_180535_4_ + (double)lvt_13_1_, p_180535_6_ + 1.0D).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).func_181675_d();
         lvt_21_2_.func_181662_b(p_180535_2_ + 1.0D, p_180535_4_ + (double)lvt_13_1_, p_180535_6_ + 1.0D).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).func_181675_d();
         lvt_21_2_.func_181662_b(p_180535_2_ + 1.0D, p_180535_4_ + (double)lvt_13_1_, p_180535_6_).func_181666_a(lvt_22_1_, lvt_23_1_, lvt_24_1_, 1.0F).func_181675_d();
         lvt_20_2_.func_78381_a();
         GlStateManager.func_179121_F();
         GlStateManager.func_179128_n(5888);
         this.func_147499_a(field_147529_c);
      }

      GlStateManager.func_179084_k();
      GlStateManager.func_179100_b(GlStateManager.TexGen.S);
      GlStateManager.func_179100_b(GlStateManager.TexGen.T);
      GlStateManager.func_179100_b(GlStateManager.TexGen.R);
      GlStateManager.func_179100_b(GlStateManager.TexGen.Q);
      GlStateManager.func_179145_e();
   }

   private FloatBuffer func_147525_a(float p_147525_1_, float p_147525_2_, float p_147525_3_, float p_147525_4_) {
      this.field_147528_b.clear();
      this.field_147528_b.put(p_147525_1_).put(p_147525_2_).put(p_147525_3_).put(p_147525_4_);
      this.field_147528_b.flip();
      return this.field_147528_b;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void func_180535_a(TileEntity p_180535_1_, double p_180535_2_, double p_180535_4_, double p_180535_6_, float p_180535_8_, int p_180535_9_) {
      this.func_180535_a((TileEntityEndPortal)p_180535_1_, p_180535_2_, p_180535_4_, p_180535_6_, p_180535_8_, p_180535_9_);
   }
}
