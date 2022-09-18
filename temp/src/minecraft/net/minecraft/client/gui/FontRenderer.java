package net.minecraft.client.gui;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

public class FontRenderer implements IResourceManagerReloadListener {
   private static final ResourceLocation[] field_111274_c = new ResourceLocation[256];
   private int[] field_78286_d = new int[256];
   public int field_78288_b = 9;
   public Random field_78289_c = new Random();
   private byte[] field_78287_e = new byte[65536];
   private int[] field_78285_g = new int[32];
   private final ResourceLocation field_111273_g;
   private final TextureManager field_78298_i;
   private float field_78295_j;
   private float field_78296_k;
   private boolean field_78293_l;
   private boolean field_78294_m;
   private float field_78291_n;
   private float field_78292_o;
   private float field_78306_p;
   private float field_78305_q;
   private int field_78304_r;
   private boolean field_78303_s;
   private boolean field_78302_t;
   private boolean field_78301_u;
   private boolean field_78300_v;
   private boolean field_78299_w;

   public FontRenderer(GameSettings p_i1035_1_, ResourceLocation p_i1035_2_, TextureManager p_i1035_3_, boolean p_i1035_4_) {
      this.field_111273_g = p_i1035_2_;
      this.field_78298_i = p_i1035_3_;
      this.field_78293_l = p_i1035_4_;
      p_i1035_3_.func_110577_a(this.field_111273_g);

      for(int lvt_5_1_ = 0; lvt_5_1_ < 32; ++lvt_5_1_) {
         int lvt_6_1_ = (lvt_5_1_ >> 3 & 1) * 85;
         int lvt_7_1_ = (lvt_5_1_ >> 2 & 1) * 170 + lvt_6_1_;
         int lvt_8_1_ = (lvt_5_1_ >> 1 & 1) * 170 + lvt_6_1_;
         int lvt_9_1_ = (lvt_5_1_ >> 0 & 1) * 170 + lvt_6_1_;
         if(lvt_5_1_ == 6) {
            lvt_7_1_ += 85;
         }

         if(p_i1035_1_.field_74337_g) {
            int lvt_10_1_ = (lvt_7_1_ * 30 + lvt_8_1_ * 59 + lvt_9_1_ * 11) / 100;
            int lvt_11_1_ = (lvt_7_1_ * 30 + lvt_8_1_ * 70) / 100;
            int lvt_12_1_ = (lvt_7_1_ * 30 + lvt_9_1_ * 70) / 100;
            lvt_7_1_ = lvt_10_1_;
            lvt_8_1_ = lvt_11_1_;
            lvt_9_1_ = lvt_12_1_;
         }

         if(lvt_5_1_ >= 16) {
            lvt_7_1_ /= 4;
            lvt_8_1_ /= 4;
            lvt_9_1_ /= 4;
         }

         this.field_78285_g[lvt_5_1_] = (lvt_7_1_ & 255) << 16 | (lvt_8_1_ & 255) << 8 | lvt_9_1_ & 255;
      }

      this.func_98306_d();
   }

   public void func_110549_a(IResourceManager p_110549_1_) {
      this.func_111272_d();
   }

   private void func_111272_d() {
      BufferedImage lvt_1_1_;
      try {
         lvt_1_1_ = TextureUtil.func_177053_a(Minecraft.func_71410_x().func_110442_L().func_110536_a(this.field_111273_g).func_110527_b());
      } catch (IOException var17) {
         throw new RuntimeException(var17);
      }

      int lvt_2_2_ = lvt_1_1_.getWidth();
      int lvt_3_1_ = lvt_1_1_.getHeight();
      int[] lvt_4_1_ = new int[lvt_2_2_ * lvt_3_1_];
      lvt_1_1_.getRGB(0, 0, lvt_2_2_, lvt_3_1_, lvt_4_1_, 0, lvt_2_2_);
      int lvt_5_1_ = lvt_3_1_ / 16;
      int lvt_6_1_ = lvt_2_2_ / 16;
      int lvt_7_1_ = 1;
      float lvt_8_1_ = 8.0F / (float)lvt_6_1_;

      for(int lvt_9_1_ = 0; lvt_9_1_ < 256; ++lvt_9_1_) {
         int lvt_10_1_ = lvt_9_1_ % 16;
         int lvt_11_1_ = lvt_9_1_ / 16;
         if(lvt_9_1_ == 32) {
            this.field_78286_d[lvt_9_1_] = 3 + lvt_7_1_;
         }

         int lvt_12_1_;
         for(lvt_12_1_ = lvt_6_1_ - 1; lvt_12_1_ >= 0; --lvt_12_1_) {
            int lvt_13_1_ = lvt_10_1_ * lvt_6_1_ + lvt_12_1_;
            boolean lvt_14_1_ = true;

            for(int lvt_15_1_ = 0; lvt_15_1_ < lvt_5_1_ && lvt_14_1_; ++lvt_15_1_) {
               int lvt_16_1_ = (lvt_11_1_ * lvt_6_1_ + lvt_15_1_) * lvt_2_2_;
               if((lvt_4_1_[lvt_13_1_ + lvt_16_1_] >> 24 & 255) != 0) {
                  lvt_14_1_ = false;
               }
            }

            if(!lvt_14_1_) {
               break;
            }
         }

         ++lvt_12_1_;
         this.field_78286_d[lvt_9_1_] = (int)(0.5D + (double)((float)lvt_12_1_ * lvt_8_1_)) + lvt_7_1_;
      }

   }

   private void func_98306_d() {
      InputStream lvt_1_1_ = null;

      try {
         lvt_1_1_ = Minecraft.func_71410_x().func_110442_L().func_110536_a(new ResourceLocation("font/glyph_sizes.bin")).func_110527_b();
         lvt_1_1_.read(this.field_78287_e);
      } catch (IOException var6) {
         throw new RuntimeException(var6);
      } finally {
         IOUtils.closeQuietly(lvt_1_1_);
      }

   }

   private float func_181559_a(char p_181559_1_, boolean p_181559_2_) {
      if(p_181559_1_ == 32) {
         return 4.0F;
      } else {
         int lvt_3_1_ = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(p_181559_1_);
         return lvt_3_1_ != -1 && !this.field_78293_l?this.func_78266_a(lvt_3_1_, p_181559_2_):this.func_78277_a(p_181559_1_, p_181559_2_);
      }
   }

   private float func_78266_a(int p_78266_1_, boolean p_78266_2_) {
      int lvt_3_1_ = p_78266_1_ % 16 * 8;
      int lvt_4_1_ = p_78266_1_ / 16 * 8;
      int lvt_5_1_ = p_78266_2_?1:0;
      this.field_78298_i.func_110577_a(this.field_111273_g);
      int lvt_6_1_ = this.field_78286_d[p_78266_1_];
      float lvt_7_1_ = (float)lvt_6_1_ - 0.01F;
      GL11.glBegin(5);
      GL11.glTexCoord2f((float)lvt_3_1_ / 128.0F, (float)lvt_4_1_ / 128.0F);
      GL11.glVertex3f(this.field_78295_j + (float)lvt_5_1_, this.field_78296_k, 0.0F);
      GL11.glTexCoord2f((float)lvt_3_1_ / 128.0F, ((float)lvt_4_1_ + 7.99F) / 128.0F);
      GL11.glVertex3f(this.field_78295_j - (float)lvt_5_1_, this.field_78296_k + 7.99F, 0.0F);
      GL11.glTexCoord2f(((float)lvt_3_1_ + lvt_7_1_ - 1.0F) / 128.0F, (float)lvt_4_1_ / 128.0F);
      GL11.glVertex3f(this.field_78295_j + lvt_7_1_ - 1.0F + (float)lvt_5_1_, this.field_78296_k, 0.0F);
      GL11.glTexCoord2f(((float)lvt_3_1_ + lvt_7_1_ - 1.0F) / 128.0F, ((float)lvt_4_1_ + 7.99F) / 128.0F);
      GL11.glVertex3f(this.field_78295_j + lvt_7_1_ - 1.0F - (float)lvt_5_1_, this.field_78296_k + 7.99F, 0.0F);
      GL11.glEnd();
      return (float)lvt_6_1_;
   }

   private ResourceLocation func_111271_a(int p_111271_1_) {
      if(field_111274_c[p_111271_1_] == null) {
         field_111274_c[p_111271_1_] = new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", new Object[]{Integer.valueOf(p_111271_1_)}));
      }

      return field_111274_c[p_111271_1_];
   }

   private void func_78257_a(int p_78257_1_) {
      this.field_78298_i.func_110577_a(this.func_111271_a(p_78257_1_));
   }

   private float func_78277_a(char p_78277_1_, boolean p_78277_2_) {
      if(this.field_78287_e[p_78277_1_] == 0) {
         return 0.0F;
      } else {
         int lvt_3_1_ = p_78277_1_ / 256;
         this.func_78257_a(lvt_3_1_);
         int lvt_4_1_ = this.field_78287_e[p_78277_1_] >>> 4;
         int lvt_5_1_ = this.field_78287_e[p_78277_1_] & 15;
         float lvt_6_1_ = (float)lvt_4_1_;
         float lvt_7_1_ = (float)(lvt_5_1_ + 1);
         float lvt_8_1_ = (float)(p_78277_1_ % 16 * 16) + lvt_6_1_;
         float lvt_9_1_ = (float)((p_78277_1_ & 255) / 16 * 16);
         float lvt_10_1_ = lvt_7_1_ - lvt_6_1_ - 0.02F;
         float lvt_11_1_ = p_78277_2_?1.0F:0.0F;
         GL11.glBegin(5);
         GL11.glTexCoord2f(lvt_8_1_ / 256.0F, lvt_9_1_ / 256.0F);
         GL11.glVertex3f(this.field_78295_j + lvt_11_1_, this.field_78296_k, 0.0F);
         GL11.glTexCoord2f(lvt_8_1_ / 256.0F, (lvt_9_1_ + 15.98F) / 256.0F);
         GL11.glVertex3f(this.field_78295_j - lvt_11_1_, this.field_78296_k + 7.99F, 0.0F);
         GL11.glTexCoord2f((lvt_8_1_ + lvt_10_1_) / 256.0F, lvt_9_1_ / 256.0F);
         GL11.glVertex3f(this.field_78295_j + lvt_10_1_ / 2.0F + lvt_11_1_, this.field_78296_k, 0.0F);
         GL11.glTexCoord2f((lvt_8_1_ + lvt_10_1_) / 256.0F, (lvt_9_1_ + 15.98F) / 256.0F);
         GL11.glVertex3f(this.field_78295_j + lvt_10_1_ / 2.0F - lvt_11_1_, this.field_78296_k + 7.99F, 0.0F);
         GL11.glEnd();
         return (lvt_7_1_ - lvt_6_1_) / 2.0F + 1.0F;
      }
   }

   public int func_175063_a(String p_175063_1_, float p_175063_2_, float p_175063_3_, int p_175063_4_) {
      return this.func_175065_a(p_175063_1_, p_175063_2_, p_175063_3_, p_175063_4_, true);
   }

   public int func_78276_b(String p_78276_1_, int p_78276_2_, int p_78276_3_, int p_78276_4_) {
      return this.func_175065_a(p_78276_1_, (float)p_78276_2_, (float)p_78276_3_, p_78276_4_, false);
   }

   public int func_175065_a(String p_175065_1_, float p_175065_2_, float p_175065_3_, int p_175065_4_, boolean p_175065_5_) {
      GlStateManager.func_179141_d();
      this.func_78265_b();
      int lvt_6_2_;
      if(p_175065_5_) {
         lvt_6_2_ = this.func_180455_b(p_175065_1_, p_175065_2_ + 1.0F, p_175065_3_ + 1.0F, p_175065_4_, true);
         lvt_6_2_ = Math.max(lvt_6_2_, this.func_180455_b(p_175065_1_, p_175065_2_, p_175065_3_, p_175065_4_, false));
      } else {
         lvt_6_2_ = this.func_180455_b(p_175065_1_, p_175065_2_, p_175065_3_, p_175065_4_, false);
      }

      return lvt_6_2_;
   }

   private String func_147647_b(String p_147647_1_) {
      try {
         Bidi lvt_2_1_ = new Bidi((new ArabicShaping(8)).shape(p_147647_1_), 127);
         lvt_2_1_.setReorderingMode(0);
         return lvt_2_1_.writeReordered(2);
      } catch (ArabicShapingException var3) {
         return p_147647_1_;
      }
   }

   private void func_78265_b() {
      this.field_78303_s = false;
      this.field_78302_t = false;
      this.field_78301_u = false;
      this.field_78300_v = false;
      this.field_78299_w = false;
   }

   private void func_78255_a(String p_78255_1_, boolean p_78255_2_) {
      for(int lvt_3_1_ = 0; lvt_3_1_ < p_78255_1_.length(); ++lvt_3_1_) {
         char lvt_4_1_ = p_78255_1_.charAt(lvt_3_1_);
         if(lvt_4_1_ == 167 && lvt_3_1_ + 1 < p_78255_1_.length()) {
            int lvt_5_1_ = "0123456789abcdefklmnor".indexOf(p_78255_1_.toLowerCase(Locale.ENGLISH).charAt(lvt_3_1_ + 1));
            if(lvt_5_1_ < 16) {
               this.field_78303_s = false;
               this.field_78302_t = false;
               this.field_78299_w = false;
               this.field_78300_v = false;
               this.field_78301_u = false;
               if(lvt_5_1_ < 0 || lvt_5_1_ > 15) {
                  lvt_5_1_ = 15;
               }

               if(p_78255_2_) {
                  lvt_5_1_ += 16;
               }

               int lvt_6_1_ = this.field_78285_g[lvt_5_1_];
               this.field_78304_r = lvt_6_1_;
               GlStateManager.func_179131_c((float)(lvt_6_1_ >> 16) / 255.0F, (float)(lvt_6_1_ >> 8 & 255) / 255.0F, (float)(lvt_6_1_ & 255) / 255.0F, this.field_78305_q);
            } else if(lvt_5_1_ == 16) {
               this.field_78303_s = true;
            } else if(lvt_5_1_ == 17) {
               this.field_78302_t = true;
            } else if(lvt_5_1_ == 18) {
               this.field_78299_w = true;
            } else if(lvt_5_1_ == 19) {
               this.field_78300_v = true;
            } else if(lvt_5_1_ == 20) {
               this.field_78301_u = true;
            } else if(lvt_5_1_ == 21) {
               this.field_78303_s = false;
               this.field_78302_t = false;
               this.field_78299_w = false;
               this.field_78300_v = false;
               this.field_78301_u = false;
               GlStateManager.func_179131_c(this.field_78291_n, this.field_78292_o, this.field_78306_p, this.field_78305_q);
            }

            ++lvt_3_1_;
         } else {
            int lvt_5_2_ = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(lvt_4_1_);
            if(this.field_78303_s && lvt_5_2_ != -1) {
               int lvt_6_2_ = this.func_78263_a(lvt_4_1_);

               char lvt_7_1_;
               while(true) {
                  lvt_5_2_ = this.field_78289_c.nextInt("\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".length());
                  lvt_7_1_ = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".charAt(lvt_5_2_);
                  if(lvt_6_2_ == this.func_78263_a(lvt_7_1_)) {
                     break;
                  }
               }

               lvt_4_1_ = lvt_7_1_;
            }

            float lvt_6_3_ = this.field_78293_l?0.5F:1.0F;
            boolean lvt_7_2_ = (lvt_4_1_ == 0 || lvt_5_2_ == -1 || this.field_78293_l) && p_78255_2_;
            if(lvt_7_2_) {
               this.field_78295_j -= lvt_6_3_;
               this.field_78296_k -= lvt_6_3_;
            }

            float lvt_8_1_ = this.func_181559_a(lvt_4_1_, this.field_78301_u);
            if(lvt_7_2_) {
               this.field_78295_j += lvt_6_3_;
               this.field_78296_k += lvt_6_3_;
            }

            if(this.field_78302_t) {
               this.field_78295_j += lvt_6_3_;
               if(lvt_7_2_) {
                  this.field_78295_j -= lvt_6_3_;
                  this.field_78296_k -= lvt_6_3_;
               }

               this.func_181559_a(lvt_4_1_, this.field_78301_u);
               this.field_78295_j -= lvt_6_3_;
               if(lvt_7_2_) {
                  this.field_78295_j += lvt_6_3_;
                  this.field_78296_k += lvt_6_3_;
               }

               ++lvt_8_1_;
            }

            if(this.field_78299_w) {
               Tessellator lvt_9_1_ = Tessellator.func_178181_a();
               WorldRenderer lvt_10_1_ = lvt_9_1_.func_178180_c();
               GlStateManager.func_179090_x();
               lvt_10_1_.func_181668_a(7, DefaultVertexFormats.field_181705_e);
               lvt_10_1_.func_181662_b((double)this.field_78295_j, (double)(this.field_78296_k + (float)(this.field_78288_b / 2)), 0.0D).func_181675_d();
               lvt_10_1_.func_181662_b((double)(this.field_78295_j + lvt_8_1_), (double)(this.field_78296_k + (float)(this.field_78288_b / 2)), 0.0D).func_181675_d();
               lvt_10_1_.func_181662_b((double)(this.field_78295_j + lvt_8_1_), (double)(this.field_78296_k + (float)(this.field_78288_b / 2) - 1.0F), 0.0D).func_181675_d();
               lvt_10_1_.func_181662_b((double)this.field_78295_j, (double)(this.field_78296_k + (float)(this.field_78288_b / 2) - 1.0F), 0.0D).func_181675_d();
               lvt_9_1_.func_78381_a();
               GlStateManager.func_179098_w();
            }

            if(this.field_78300_v) {
               Tessellator lvt_9_2_ = Tessellator.func_178181_a();
               WorldRenderer lvt_10_2_ = lvt_9_2_.func_178180_c();
               GlStateManager.func_179090_x();
               lvt_10_2_.func_181668_a(7, DefaultVertexFormats.field_181705_e);
               int lvt_11_1_ = this.field_78300_v?-1:0;
               lvt_10_2_.func_181662_b((double)(this.field_78295_j + (float)lvt_11_1_), (double)(this.field_78296_k + (float)this.field_78288_b), 0.0D).func_181675_d();
               lvt_10_2_.func_181662_b((double)(this.field_78295_j + lvt_8_1_), (double)(this.field_78296_k + (float)this.field_78288_b), 0.0D).func_181675_d();
               lvt_10_2_.func_181662_b((double)(this.field_78295_j + lvt_8_1_), (double)(this.field_78296_k + (float)this.field_78288_b - 1.0F), 0.0D).func_181675_d();
               lvt_10_2_.func_181662_b((double)(this.field_78295_j + (float)lvt_11_1_), (double)(this.field_78296_k + (float)this.field_78288_b - 1.0F), 0.0D).func_181675_d();
               lvt_9_2_.func_78381_a();
               GlStateManager.func_179098_w();
            }

            this.field_78295_j += (float)((int)lvt_8_1_);
         }
      }

   }

   private int func_78274_b(String p_78274_1_, int p_78274_2_, int p_78274_3_, int p_78274_4_, int p_78274_5_, boolean p_78274_6_) {
      if(this.field_78294_m) {
         int lvt_7_1_ = this.func_78256_a(this.func_147647_b(p_78274_1_));
         p_78274_2_ = p_78274_2_ + p_78274_4_ - lvt_7_1_;
      }

      return this.func_180455_b(p_78274_1_, (float)p_78274_2_, (float)p_78274_3_, p_78274_5_, p_78274_6_);
   }

   private int func_180455_b(String p_180455_1_, float p_180455_2_, float p_180455_3_, int p_180455_4_, boolean p_180455_5_) {
      if(p_180455_1_ == null) {
         return 0;
      } else {
         if(this.field_78294_m) {
            p_180455_1_ = this.func_147647_b(p_180455_1_);
         }

         if((p_180455_4_ & -67108864) == 0) {
            p_180455_4_ |= -16777216;
         }

         if(p_180455_5_) {
            p_180455_4_ = (p_180455_4_ & 16579836) >> 2 | p_180455_4_ & -16777216;
         }

         this.field_78291_n = (float)(p_180455_4_ >> 16 & 255) / 255.0F;
         this.field_78292_o = (float)(p_180455_4_ >> 8 & 255) / 255.0F;
         this.field_78306_p = (float)(p_180455_4_ & 255) / 255.0F;
         this.field_78305_q = (float)(p_180455_4_ >> 24 & 255) / 255.0F;
         GlStateManager.func_179131_c(this.field_78291_n, this.field_78292_o, this.field_78306_p, this.field_78305_q);
         this.field_78295_j = p_180455_2_;
         this.field_78296_k = p_180455_3_;
         this.func_78255_a(p_180455_1_, p_180455_5_);
         return (int)this.field_78295_j;
      }
   }

   public int func_78256_a(String p_78256_1_) {
      if(p_78256_1_ == null) {
         return 0;
      } else {
         int lvt_2_1_ = 0;
         boolean lvt_3_1_ = false;

         for(int lvt_4_1_ = 0; lvt_4_1_ < p_78256_1_.length(); ++lvt_4_1_) {
            char lvt_5_1_ = p_78256_1_.charAt(lvt_4_1_);
            int lvt_6_1_ = this.func_78263_a(lvt_5_1_);
            if(lvt_6_1_ < 0 && lvt_4_1_ < p_78256_1_.length() - 1) {
               ++lvt_4_1_;
               lvt_5_1_ = p_78256_1_.charAt(lvt_4_1_);
               if(lvt_5_1_ != 108 && lvt_5_1_ != 76) {
                  if(lvt_5_1_ == 114 || lvt_5_1_ == 82) {
                     lvt_3_1_ = false;
                  }
               } else {
                  lvt_3_1_ = true;
               }

               lvt_6_1_ = 0;
            }

            lvt_2_1_ += lvt_6_1_;
            if(lvt_3_1_ && lvt_6_1_ > 0) {
               ++lvt_2_1_;
            }
         }

         return lvt_2_1_;
      }
   }

   public int func_78263_a(char p_78263_1_) {
      if(p_78263_1_ == 167) {
         return -1;
      } else if(p_78263_1_ == 32) {
         return 4;
      } else {
         int lvt_2_1_ = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(p_78263_1_);
         if(p_78263_1_ > 0 && lvt_2_1_ != -1 && !this.field_78293_l) {
            return this.field_78286_d[lvt_2_1_];
         } else if(this.field_78287_e[p_78263_1_] != 0) {
            int lvt_3_1_ = this.field_78287_e[p_78263_1_] >>> 4;
            int lvt_4_1_ = this.field_78287_e[p_78263_1_] & 15;
            if(lvt_4_1_ > 7) {
               lvt_4_1_ = 15;
               lvt_3_1_ = 0;
            }

            ++lvt_4_1_;
            return (lvt_4_1_ - lvt_3_1_) / 2 + 1;
         } else {
            return 0;
         }
      }
   }

   public String func_78269_a(String p_78269_1_, int p_78269_2_) {
      return this.func_78262_a(p_78269_1_, p_78269_2_, false);
   }

   public String func_78262_a(String p_78262_1_, int p_78262_2_, boolean p_78262_3_) {
      StringBuilder lvt_4_1_ = new StringBuilder();
      int lvt_5_1_ = 0;
      int lvt_6_1_ = p_78262_3_?p_78262_1_.length() - 1:0;
      int lvt_7_1_ = p_78262_3_?-1:1;
      boolean lvt_8_1_ = false;
      boolean lvt_9_1_ = false;

      for(int lvt_10_1_ = lvt_6_1_; lvt_10_1_ >= 0 && lvt_10_1_ < p_78262_1_.length() && lvt_5_1_ < p_78262_2_; lvt_10_1_ += lvt_7_1_) {
         char lvt_11_1_ = p_78262_1_.charAt(lvt_10_1_);
         int lvt_12_1_ = this.func_78263_a(lvt_11_1_);
         if(lvt_8_1_) {
            lvt_8_1_ = false;
            if(lvt_11_1_ != 108 && lvt_11_1_ != 76) {
               if(lvt_11_1_ == 114 || lvt_11_1_ == 82) {
                  lvt_9_1_ = false;
               }
            } else {
               lvt_9_1_ = true;
            }
         } else if(lvt_12_1_ < 0) {
            lvt_8_1_ = true;
         } else {
            lvt_5_1_ += lvt_12_1_;
            if(lvt_9_1_) {
               ++lvt_5_1_;
            }
         }

         if(lvt_5_1_ > p_78262_2_) {
            break;
         }

         if(p_78262_3_) {
            lvt_4_1_.insert(0, lvt_11_1_);
         } else {
            lvt_4_1_.append(lvt_11_1_);
         }
      }

      return lvt_4_1_.toString();
   }

   private String func_78273_d(String p_78273_1_) {
      while(p_78273_1_ != null && p_78273_1_.endsWith("\n")) {
         p_78273_1_ = p_78273_1_.substring(0, p_78273_1_.length() - 1);
      }

      return p_78273_1_;
   }

   public void func_78279_b(String p_78279_1_, int p_78279_2_, int p_78279_3_, int p_78279_4_, int p_78279_5_) {
      this.func_78265_b();
      this.field_78304_r = p_78279_5_;
      p_78279_1_ = this.func_78273_d(p_78279_1_);
      this.func_78268_b(p_78279_1_, p_78279_2_, p_78279_3_, p_78279_4_, false);
   }

   private void func_78268_b(String p_78268_1_, int p_78268_2_, int p_78268_3_, int p_78268_4_, boolean p_78268_5_) {
      for(String lvt_8_1_ : this.func_78271_c(p_78268_1_, p_78268_4_)) {
         this.func_78274_b(lvt_8_1_, p_78268_2_, p_78268_3_, p_78268_4_, this.field_78304_r, p_78268_5_);
         p_78268_3_ += this.field_78288_b;
      }

   }

   public int func_78267_b(String p_78267_1_, int p_78267_2_) {
      return this.field_78288_b * this.func_78271_c(p_78267_1_, p_78267_2_).size();
   }

   public void func_78264_a(boolean p_78264_1_) {
      this.field_78293_l = p_78264_1_;
   }

   public boolean func_82883_a() {
      return this.field_78293_l;
   }

   public void func_78275_b(boolean p_78275_1_) {
      this.field_78294_m = p_78275_1_;
   }

   public List<String> func_78271_c(String p_78271_1_, int p_78271_2_) {
      return Arrays.asList(this.func_78280_d(p_78271_1_, p_78271_2_).split("\n"));
   }

   String func_78280_d(String p_78280_1_, int p_78280_2_) {
      int lvt_3_1_ = this.func_78259_e(p_78280_1_, p_78280_2_);
      if(p_78280_1_.length() <= lvt_3_1_) {
         return p_78280_1_;
      } else {
         String lvt_4_1_ = p_78280_1_.substring(0, lvt_3_1_);
         char lvt_5_1_ = p_78280_1_.charAt(lvt_3_1_);
         boolean lvt_6_1_ = lvt_5_1_ == 32 || lvt_5_1_ == 10;
         String lvt_7_1_ = func_78282_e(lvt_4_1_) + p_78280_1_.substring(lvt_3_1_ + (lvt_6_1_?1:0));
         return lvt_4_1_ + "\n" + this.func_78280_d(lvt_7_1_, p_78280_2_);
      }
   }

   private int func_78259_e(String p_78259_1_, int p_78259_2_) {
      int lvt_3_1_ = p_78259_1_.length();
      int lvt_4_1_ = 0;
      int lvt_5_1_ = 0;
      int lvt_6_1_ = -1;

      for(boolean lvt_7_1_ = false; lvt_5_1_ < lvt_3_1_; ++lvt_5_1_) {
         char lvt_8_1_ = p_78259_1_.charAt(lvt_5_1_);
         switch(lvt_8_1_) {
         case '\n':
            --lvt_5_1_;
            break;
         case ' ':
            lvt_6_1_ = lvt_5_1_;
         default:
            lvt_4_1_ += this.func_78263_a(lvt_8_1_);
            if(lvt_7_1_) {
               ++lvt_4_1_;
            }
            break;
         case '\u00a7':
            if(lvt_5_1_ < lvt_3_1_ - 1) {
               ++lvt_5_1_;
               char lvt_9_1_ = p_78259_1_.charAt(lvt_5_1_);
               if(lvt_9_1_ != 108 && lvt_9_1_ != 76) {
                  if(lvt_9_1_ == 114 || lvt_9_1_ == 82 || func_78272_b(lvt_9_1_)) {
                     lvt_7_1_ = false;
                  }
               } else {
                  lvt_7_1_ = true;
               }
            }
         }

         if(lvt_8_1_ == 10) {
            ++lvt_5_1_;
            lvt_6_1_ = lvt_5_1_;
            break;
         }

         if(lvt_4_1_ > p_78259_2_) {
            break;
         }
      }

      return lvt_5_1_ != lvt_3_1_ && lvt_6_1_ != -1 && lvt_6_1_ < lvt_5_1_?lvt_6_1_:lvt_5_1_;
   }

   private static boolean func_78272_b(char p_78272_0_) {
      return p_78272_0_ >= 48 && p_78272_0_ <= 57 || p_78272_0_ >= 97 && p_78272_0_ <= 102 || p_78272_0_ >= 65 && p_78272_0_ <= 70;
   }

   private static boolean func_78270_c(char p_78270_0_) {
      return p_78270_0_ >= 107 && p_78270_0_ <= 111 || p_78270_0_ >= 75 && p_78270_0_ <= 79 || p_78270_0_ == 114 || p_78270_0_ == 82;
   }

   public static String func_78282_e(String p_78282_0_) {
      String lvt_1_1_ = "";
      int lvt_2_1_ = -1;
      int lvt_3_1_ = p_78282_0_.length();

      while((lvt_2_1_ = p_78282_0_.indexOf(167, lvt_2_1_ + 1)) != -1) {
         if(lvt_2_1_ < lvt_3_1_ - 1) {
            char lvt_4_1_ = p_78282_0_.charAt(lvt_2_1_ + 1);
            if(func_78272_b(lvt_4_1_)) {
               lvt_1_1_ = "\u00a7" + lvt_4_1_;
            } else if(func_78270_c(lvt_4_1_)) {
               lvt_1_1_ = lvt_1_1_ + "\u00a7" + lvt_4_1_;
            }
         }
      }

      return lvt_1_1_;
   }

   public boolean func_78260_a() {
      return this.field_78294_m;
   }

   public int func_175064_b(char p_175064_1_) {
      return this.field_78285_g["0123456789abcdef".indexOf(p_175064_1_)];
   }
}
