package net.minecraft.client.renderer.tileentity;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEntitySignRenderer extends TileEntitySpecialRenderer<TileEntitySign> {
   private static final ResourceLocation field_147513_b = new ResourceLocation("textures/entity/sign.png");
   private final ModelSign field_147514_c = new ModelSign();

   public void func_180535_a(TileEntitySign p_180535_1_, double p_180535_2_, double p_180535_4_, double p_180535_6_, float p_180535_8_, int p_180535_9_) {
      Block lvt_10_1_ = p_180535_1_.func_145838_q();
      GlStateManager.func_179094_E();
      float lvt_11_1_ = 0.6666667F;
      if(lvt_10_1_ == Blocks.field_150472_an) {
         GlStateManager.func_179109_b((float)p_180535_2_ + 0.5F, (float)p_180535_4_ + 0.75F * lvt_11_1_, (float)p_180535_6_ + 0.5F);
         float lvt_12_1_ = (float)(p_180535_1_.func_145832_p() * 360) / 16.0F;
         GlStateManager.func_179114_b(-lvt_12_1_, 0.0F, 1.0F, 0.0F);
         this.field_147514_c.field_78165_b.field_78806_j = true;
      } else {
         int lvt_12_2_ = p_180535_1_.func_145832_p();
         float lvt_13_1_ = 0.0F;
         if(lvt_12_2_ == 2) {
            lvt_13_1_ = 180.0F;
         }

         if(lvt_12_2_ == 4) {
            lvt_13_1_ = 90.0F;
         }

         if(lvt_12_2_ == 5) {
            lvt_13_1_ = -90.0F;
         }

         GlStateManager.func_179109_b((float)p_180535_2_ + 0.5F, (float)p_180535_4_ + 0.75F * lvt_11_1_, (float)p_180535_6_ + 0.5F);
         GlStateManager.func_179114_b(-lvt_13_1_, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179109_b(0.0F, -0.3125F, -0.4375F);
         this.field_147514_c.field_78165_b.field_78806_j = false;
      }

      if(p_180535_9_ >= 0) {
         this.func_147499_a(field_178460_a[p_180535_9_]);
         GlStateManager.func_179128_n(5890);
         GlStateManager.func_179094_E();
         GlStateManager.func_179152_a(4.0F, 2.0F, 1.0F);
         GlStateManager.func_179109_b(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.func_179128_n(5888);
      } else {
         this.func_147499_a(field_147513_b);
      }

      GlStateManager.func_179091_B();
      GlStateManager.func_179094_E();
      GlStateManager.func_179152_a(lvt_11_1_, -lvt_11_1_, -lvt_11_1_);
      this.field_147514_c.func_78164_a();
      GlStateManager.func_179121_F();
      FontRenderer lvt_12_3_ = this.func_147498_b();
      float lvt_13_2_ = 0.015625F * lvt_11_1_;
      GlStateManager.func_179109_b(0.0F, 0.5F * lvt_11_1_, 0.07F * lvt_11_1_);
      GlStateManager.func_179152_a(lvt_13_2_, -lvt_13_2_, lvt_13_2_);
      GL11.glNormal3f(0.0F, 0.0F, -1.0F * lvt_13_2_);
      GlStateManager.func_179132_a(false);
      int lvt_14_1_ = 0;
      if(p_180535_9_ < 0) {
         for(int lvt_15_1_ = 0; lvt_15_1_ < p_180535_1_.field_145915_a.length; ++lvt_15_1_) {
            if(p_180535_1_.field_145915_a[lvt_15_1_] != null) {
               IChatComponent lvt_16_1_ = p_180535_1_.field_145915_a[lvt_15_1_];
               List<IChatComponent> lvt_17_1_ = GuiUtilRenderComponents.func_178908_a(lvt_16_1_, 90, lvt_12_3_, false, true);
               String lvt_18_1_ = lvt_17_1_ != null && lvt_17_1_.size() > 0?((IChatComponent)lvt_17_1_.get(0)).func_150254_d():"";
               if(lvt_15_1_ == p_180535_1_.field_145918_i) {
                  lvt_18_1_ = "> " + lvt_18_1_ + " <";
                  lvt_12_3_.func_78276_b(lvt_18_1_, -lvt_12_3_.func_78256_a(lvt_18_1_) / 2, lvt_15_1_ * 10 - p_180535_1_.field_145915_a.length * 5, lvt_14_1_);
               } else {
                  lvt_12_3_.func_78276_b(lvt_18_1_, -lvt_12_3_.func_78256_a(lvt_18_1_) / 2, lvt_15_1_ * 10 - p_180535_1_.field_145915_a.length * 5, lvt_14_1_);
               }
            }
         }
      }

      GlStateManager.func_179132_a(true);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179121_F();
      if(p_180535_9_ >= 0) {
         GlStateManager.func_179128_n(5890);
         GlStateManager.func_179121_F();
         GlStateManager.func_179128_n(5888);
      }

   }

   // $FF: synthetic method
   // $FF: bridge method
   public void func_180535_a(TileEntity p_180535_1_, double p_180535_2_, double p_180535_4_, double p_180535_6_, float p_180535_8_, int p_180535_9_) {
      this.func_180535_a((TileEntitySign)p_180535_1_, p_180535_2_, p_180535_4_, p_180535_6_, p_180535_8_, p_180535_9_);
   }
}
