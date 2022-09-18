package net.minecraft.client.resources.model;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public enum ModelRotation {
   X0_Y0(0, 0),
   X0_Y90(0, 90),
   X0_Y180(0, 180),
   X0_Y270(0, 270),
   X90_Y0(90, 0),
   X90_Y90(90, 90),
   X90_Y180(90, 180),
   X90_Y270(90, 270),
   X180_Y0(180, 0),
   X180_Y90(180, 90),
   X180_Y180(180, 180),
   X180_Y270(180, 270),
   X270_Y0(270, 0),
   X270_Y90(270, 90),
   X270_Y180(270, 180),
   X270_Y270(270, 270);

   private static final Map<Integer, ModelRotation> field_177546_q = Maps.newHashMap();
   private final int field_177545_r;
   private final Matrix4f field_177544_s;
   private final int field_177543_t;
   private final int field_177542_u;

   private static int func_177521_b(int p_177521_0_, int p_177521_1_) {
      return p_177521_0_ * 360 + p_177521_1_;
   }

   private ModelRotation(int p_i46087_3_, int p_i46087_4_) {
      this.field_177545_r = func_177521_b(p_i46087_3_, p_i46087_4_);
      this.field_177544_s = new Matrix4f();
      Matrix4f lvt_5_1_ = new Matrix4f();
      lvt_5_1_.setIdentity();
      Matrix4f.rotate((float)(-p_i46087_3_) * 0.017453292F, new Vector3f(1.0F, 0.0F, 0.0F), lvt_5_1_, lvt_5_1_);
      this.field_177543_t = MathHelper.func_76130_a(p_i46087_3_ / 90);
      Matrix4f lvt_6_1_ = new Matrix4f();
      lvt_6_1_.setIdentity();
      Matrix4f.rotate((float)(-p_i46087_4_) * 0.017453292F, new Vector3f(0.0F, 1.0F, 0.0F), lvt_6_1_, lvt_6_1_);
      this.field_177542_u = MathHelper.func_76130_a(p_i46087_4_ / 90);
      Matrix4f.mul(lvt_6_1_, lvt_5_1_, this.field_177544_s);
   }

   public Matrix4f func_177525_a() {
      return this.field_177544_s;
   }

   public EnumFacing func_177523_a(EnumFacing p_177523_1_) {
      EnumFacing lvt_2_1_ = p_177523_1_;

      for(int lvt_3_1_ = 0; lvt_3_1_ < this.field_177543_t; ++lvt_3_1_) {
         lvt_2_1_ = lvt_2_1_.func_176732_a(EnumFacing.Axis.X);
      }

      if(lvt_2_1_.func_176740_k() != EnumFacing.Axis.Y) {
         for(int lvt_3_2_ = 0; lvt_3_2_ < this.field_177542_u; ++lvt_3_2_) {
            lvt_2_1_ = lvt_2_1_.func_176732_a(EnumFacing.Axis.Y);
         }
      }

      return lvt_2_1_;
   }

   public int func_177520_a(EnumFacing p_177520_1_, int p_177520_2_) {
      int lvt_3_1_ = p_177520_2_;
      if(p_177520_1_.func_176740_k() == EnumFacing.Axis.X) {
         lvt_3_1_ = (p_177520_2_ + this.field_177543_t) % 4;
      }

      EnumFacing lvt_4_1_ = p_177520_1_;

      for(int lvt_5_1_ = 0; lvt_5_1_ < this.field_177543_t; ++lvt_5_1_) {
         lvt_4_1_ = lvt_4_1_.func_176732_a(EnumFacing.Axis.X);
      }

      if(lvt_4_1_.func_176740_k() == EnumFacing.Axis.Y) {
         lvt_3_1_ = (lvt_3_1_ + this.field_177542_u) % 4;
      }

      return lvt_3_1_;
   }

   public static ModelRotation func_177524_a(int p_177524_0_, int p_177524_1_) {
      return (ModelRotation)field_177546_q.get(Integer.valueOf(func_177521_b(MathHelper.func_180184_b(p_177524_0_, 360), MathHelper.func_180184_b(p_177524_1_, 360))));
   }

   static {
      for(ModelRotation lvt_3_1_ : values()) {
         field_177546_q.put(Integer.valueOf(lvt_3_1_.field_177545_r), lvt_3_1_);
      }

   }
}
