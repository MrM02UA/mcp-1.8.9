package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityPickupFX extends EntityFX {
   private Entity field_174840_a;
   private Entity field_174843_ax;
   private int field_70594_ar;
   private int field_70593_as;
   private float field_174841_aA;
   private RenderManager field_174842_aB = Minecraft.func_71410_x().func_175598_ae();

   public EntityPickupFX(World p_i1233_1_, Entity p_i1233_2_, Entity p_i1233_3_, float p_i1233_4_) {
      super(p_i1233_1_, p_i1233_2_.field_70165_t, p_i1233_2_.field_70163_u, p_i1233_2_.field_70161_v, p_i1233_2_.field_70159_w, p_i1233_2_.field_70181_x, p_i1233_2_.field_70179_y);
      this.field_174840_a = p_i1233_2_;
      this.field_174843_ax = p_i1233_3_;
      this.field_70593_as = 3;
      this.field_174841_aA = p_i1233_4_;
   }

   public void func_180434_a(WorldRenderer p_180434_1_, Entity p_180434_2_, float p_180434_3_, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
      float lvt_9_1_ = ((float)this.field_70594_ar + p_180434_3_) / (float)this.field_70593_as;
      lvt_9_1_ = lvt_9_1_ * lvt_9_1_;
      double lvt_10_1_ = this.field_174840_a.field_70165_t;
      double lvt_12_1_ = this.field_174840_a.field_70163_u;
      double lvt_14_1_ = this.field_174840_a.field_70161_v;
      double lvt_16_1_ = this.field_174843_ax.field_70142_S + (this.field_174843_ax.field_70165_t - this.field_174843_ax.field_70142_S) * (double)p_180434_3_;
      double lvt_18_1_ = this.field_174843_ax.field_70137_T + (this.field_174843_ax.field_70163_u - this.field_174843_ax.field_70137_T) * (double)p_180434_3_ + (double)this.field_174841_aA;
      double lvt_20_1_ = this.field_174843_ax.field_70136_U + (this.field_174843_ax.field_70161_v - this.field_174843_ax.field_70136_U) * (double)p_180434_3_;
      double lvt_22_1_ = lvt_10_1_ + (lvt_16_1_ - lvt_10_1_) * (double)lvt_9_1_;
      double lvt_24_1_ = lvt_12_1_ + (lvt_18_1_ - lvt_12_1_) * (double)lvt_9_1_;
      double lvt_26_1_ = lvt_14_1_ + (lvt_20_1_ - lvt_14_1_) * (double)lvt_9_1_;
      int lvt_28_1_ = this.func_70070_b(p_180434_3_);
      int lvt_29_1_ = lvt_28_1_ % 65536;
      int lvt_30_1_ = lvt_28_1_ / 65536;
      OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, (float)lvt_29_1_ / 1.0F, (float)lvt_30_1_ / 1.0F);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      lvt_22_1_ = lvt_22_1_ - field_70556_an;
      lvt_24_1_ = lvt_24_1_ - field_70554_ao;
      lvt_26_1_ = lvt_26_1_ - field_70555_ap;
      this.field_174842_aB.func_147940_a(this.field_174840_a, (double)((float)lvt_22_1_), (double)((float)lvt_24_1_), (double)((float)lvt_26_1_), this.field_174840_a.field_70177_z, p_180434_3_);
   }

   public void func_70071_h_() {
      ++this.field_70594_ar;
      if(this.field_70594_ar == this.field_70593_as) {
         this.func_70106_y();
      }

   }

   public int func_70537_b() {
      return 3;
   }
}
