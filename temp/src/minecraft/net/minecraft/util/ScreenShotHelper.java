package net.minecraft.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class ScreenShotHelper {
   private static final Logger field_148261_a = LogManager.getLogger();
   private static final DateFormat field_74295_a = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
   private static IntBuffer field_74293_b;
   private static int[] field_74294_c;

   public static IChatComponent func_148260_a(File p_148260_0_, int p_148260_1_, int p_148260_2_, Framebuffer p_148260_3_) {
      return func_148259_a(p_148260_0_, (String)null, p_148260_1_, p_148260_2_, p_148260_3_);
   }

   public static IChatComponent func_148259_a(File p_148259_0_, String p_148259_1_, int p_148259_2_, int p_148259_3_, Framebuffer p_148259_4_) {
      try {
         File lvt_5_1_ = new File(p_148259_0_, "screenshots");
         lvt_5_1_.mkdir();
         if(OpenGlHelper.func_148822_b()) {
            p_148259_2_ = p_148259_4_.field_147622_a;
            p_148259_3_ = p_148259_4_.field_147620_b;
         }

         int lvt_6_1_ = p_148259_2_ * p_148259_3_;
         if(field_74293_b == null || field_74293_b.capacity() < lvt_6_1_) {
            field_74293_b = BufferUtils.createIntBuffer(lvt_6_1_);
            field_74294_c = new int[lvt_6_1_];
         }

         GL11.glPixelStorei(3333, 1);
         GL11.glPixelStorei(3317, 1);
         field_74293_b.clear();
         if(OpenGlHelper.func_148822_b()) {
            GlStateManager.func_179144_i(p_148259_4_.field_147617_g);
            GL11.glGetTexImage(3553, 0, '\u80e1', '\u8367', field_74293_b);
         } else {
            GL11.glReadPixels(0, 0, p_148259_2_, p_148259_3_, '\u80e1', '\u8367', field_74293_b);
         }

         field_74293_b.get(field_74294_c);
         TextureUtil.func_147953_a(field_74294_c, p_148259_2_, p_148259_3_);
         BufferedImage lvt_7_1_ = null;
         if(OpenGlHelper.func_148822_b()) {
            lvt_7_1_ = new BufferedImage(p_148259_4_.field_147621_c, p_148259_4_.field_147618_d, 1);
            int lvt_8_1_ = p_148259_4_.field_147620_b - p_148259_4_.field_147618_d;

            for(int lvt_9_1_ = lvt_8_1_; lvt_9_1_ < p_148259_4_.field_147620_b; ++lvt_9_1_) {
               for(int lvt_10_1_ = 0; lvt_10_1_ < p_148259_4_.field_147621_c; ++lvt_10_1_) {
                  lvt_7_1_.setRGB(lvt_10_1_, lvt_9_1_ - lvt_8_1_, field_74294_c[lvt_9_1_ * p_148259_4_.field_147622_a + lvt_10_1_]);
               }
            }
         } else {
            lvt_7_1_ = new BufferedImage(p_148259_2_, p_148259_3_, 1);
            lvt_7_1_.setRGB(0, 0, p_148259_2_, p_148259_3_, field_74294_c, 0, p_148259_2_);
         }

         File lvt_8_2_;
         if(p_148259_1_ == null) {
            lvt_8_2_ = func_74290_a(lvt_5_1_);
         } else {
            lvt_8_2_ = new File(lvt_5_1_, p_148259_1_);
         }

         ImageIO.write(lvt_7_1_, "png", lvt_8_2_);
         IChatComponent lvt_9_2_ = new ChatComponentText(lvt_8_2_.getName());
         lvt_9_2_.func_150256_b().func_150241_a(new ClickEvent(ClickEvent.Action.OPEN_FILE, lvt_8_2_.getAbsolutePath()));
         lvt_9_2_.func_150256_b().func_150228_d(Boolean.valueOf(true));
         return new ChatComponentTranslation("screenshot.success", new Object[]{lvt_9_2_});
      } catch (Exception var11) {
         field_148261_a.warn("Couldn\'t save screenshot", var11);
         return new ChatComponentTranslation("screenshot.failure", new Object[]{var11.getMessage()});
      }
   }

   private static File func_74290_a(File p_74290_0_) {
      String lvt_2_1_ = field_74295_a.format(new Date()).toString();
      int lvt_3_1_ = 1;

      while(true) {
         File lvt_1_1_ = new File(p_74290_0_, lvt_2_1_ + (lvt_3_1_ == 1?"":"_" + lvt_3_1_) + ".png");
         if(!lvt_1_1_.exists()) {
            return lvt_1_1_;
         }

         ++lvt_3_1_;
      }
   }
}
