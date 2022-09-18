package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EnumPlayerModelParts;

public class GuiCustomizeSkin extends GuiScreen {
   private final GuiScreen field_175361_a;
   private String field_175360_f;

   public GuiCustomizeSkin(GuiScreen p_i45516_1_) {
      this.field_175361_a = p_i45516_1_;
   }

   public void func_73866_w_() {
      int lvt_1_1_ = 0;
      this.field_175360_f = I18n.func_135052_a("options.skinCustomisation.title", new Object[0]);

      for(EnumPlayerModelParts lvt_5_1_ : EnumPlayerModelParts.values()) {
         this.field_146292_n.add(new GuiCustomizeSkin.ButtonPart(lvt_5_1_.func_179328_b(), this.field_146294_l / 2 - 155 + lvt_1_1_ % 2 * 160, this.field_146295_m / 6 + 24 * (lvt_1_1_ >> 1), 150, 20, lvt_5_1_));
         ++lvt_1_1_;
      }

      if(lvt_1_1_ % 2 == 1) {
         ++lvt_1_1_;
      }

      this.field_146292_n.add(new GuiButton(200, this.field_146294_l / 2 - 100, this.field_146295_m / 6 + 24 * (lvt_1_1_ >> 1), I18n.func_135052_a("gui.done", new Object[0])));
   }

   protected void func_146284_a(GuiButton p_146284_1_) throws IOException {
      if(p_146284_1_.field_146124_l) {
         if(p_146284_1_.field_146127_k == 200) {
            this.field_146297_k.field_71474_y.func_74303_b();
            this.field_146297_k.func_147108_a(this.field_175361_a);
         } else if(p_146284_1_ instanceof GuiCustomizeSkin.ButtonPart) {
            EnumPlayerModelParts lvt_2_1_ = ((GuiCustomizeSkin.ButtonPart)p_146284_1_).field_175234_p;
            this.field_146297_k.field_71474_y.func_178877_a(lvt_2_1_);
            p_146284_1_.field_146126_j = this.func_175358_a(lvt_2_1_);
         }

      }
   }

   public void func_73863_a(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.func_146276_q_();
      this.func_73732_a(this.field_146289_q, this.field_175360_f, this.field_146294_l / 2, 20, 16777215);
      super.func_73863_a(p_73863_1_, p_73863_2_, p_73863_3_);
   }

   private String func_175358_a(EnumPlayerModelParts p_175358_1_) {
      String lvt_2_1_;
      if(this.field_146297_k.field_71474_y.func_178876_d().contains(p_175358_1_)) {
         lvt_2_1_ = I18n.func_135052_a("options.on", new Object[0]);
      } else {
         lvt_2_1_ = I18n.func_135052_a("options.off", new Object[0]);
      }

      return p_175358_1_.func_179326_d().func_150254_d() + ": " + lvt_2_1_;
   }

   class ButtonPart extends GuiButton {
      private final EnumPlayerModelParts field_175234_p;

      private ButtonPart(int p_i45514_2_, int p_i45514_3_, int p_i45514_4_, int p_i45514_5_, int p_i45514_6_, EnumPlayerModelParts p_i45514_7_) {
         super(p_i45514_2_, p_i45514_3_, p_i45514_4_, p_i45514_5_, p_i45514_6_, GuiCustomizeSkin.this.func_175358_a(p_i45514_7_));
         this.field_175234_p = p_i45514_7_;
      }
   }
}
