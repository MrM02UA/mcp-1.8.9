package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.IIconCreator;
import net.minecraft.client.renderer.texture.ITickableTextureObject;
import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureMap extends AbstractTexture implements ITickableTextureObject {
   private static final Logger field_147635_d = LogManager.getLogger();
   public static final ResourceLocation field_174945_f = new ResourceLocation("missingno");
   public static final ResourceLocation field_110575_b = new ResourceLocation("textures/atlas/blocks.png");
   private final List<TextureAtlasSprite> field_94258_i;
   private final Map<String, TextureAtlasSprite> field_110574_e;
   private final Map<String, TextureAtlasSprite> field_94252_e;
   private final String field_94254_c;
   private final IIconCreator field_174946_m;
   private int field_147636_j;
   private final TextureAtlasSprite field_94249_f;

   public TextureMap(String p_i46099_1_) {
      this(p_i46099_1_, (IIconCreator)null);
   }

   public TextureMap(String p_i46100_1_, IIconCreator p_i46100_2_) {
      this.field_94258_i = Lists.newArrayList();
      this.field_110574_e = Maps.newHashMap();
      this.field_94252_e = Maps.newHashMap();
      this.field_94249_f = new TextureAtlasSprite("missingno");
      this.field_94254_c = p_i46100_1_;
      this.field_174946_m = p_i46100_2_;
   }

   private void func_110569_e() {
      int[] lvt_1_1_ = TextureUtil.field_110999_b;
      this.field_94249_f.func_110966_b(16);
      this.field_94249_f.func_110969_c(16);
      int[][] lvt_2_1_ = new int[this.field_147636_j + 1][];
      lvt_2_1_[0] = lvt_1_1_;
      this.field_94249_f.func_110968_a(Lists.newArrayList(new int[][][]{lvt_2_1_}));
   }

   public void func_110551_a(IResourceManager p_110551_1_) throws IOException {
      if(this.field_174946_m != null) {
         this.func_174943_a(p_110551_1_, this.field_174946_m);
      }

   }

   public void func_174943_a(IResourceManager p_174943_1_, IIconCreator p_174943_2_) {
      this.field_110574_e.clear();
      p_174943_2_.func_177059_a(this);
      this.func_110569_e();
      this.func_147631_c();
      this.func_110571_b(p_174943_1_);
   }

   public void func_110571_b(IResourceManager p_110571_1_) {
      int lvt_2_1_ = Minecraft.func_71369_N();
      Stitcher lvt_3_1_ = new Stitcher(lvt_2_1_, lvt_2_1_, true, 0, this.field_147636_j);
      this.field_94252_e.clear();
      this.field_94258_i.clear();
      int lvt_4_1_ = Integer.MAX_VALUE;
      int lvt_5_1_ = 1 << this.field_147636_j;

      for(Entry<String, TextureAtlasSprite> lvt_7_1_ : this.field_110574_e.entrySet()) {
         TextureAtlasSprite lvt_8_1_ = (TextureAtlasSprite)lvt_7_1_.getValue();
         ResourceLocation lvt_9_1_ = new ResourceLocation(lvt_8_1_.func_94215_i());
         ResourceLocation lvt_10_1_ = this.func_147634_a(lvt_9_1_, 0);

         try {
            IResource lvt_11_1_ = p_110571_1_.func_110536_a(lvt_10_1_);
            BufferedImage[] lvt_12_1_ = new BufferedImage[1 + this.field_147636_j];
            lvt_12_1_[0] = TextureUtil.func_177053_a(lvt_11_1_.func_110527_b());
            TextureMetadataSection lvt_13_1_ = (TextureMetadataSection)lvt_11_1_.func_110526_a("texture");
            if(lvt_13_1_ != null) {
               List<Integer> lvt_14_1_ = lvt_13_1_.func_148535_c();
               if(!lvt_14_1_.isEmpty()) {
                  int lvt_15_1_ = lvt_12_1_[0].getWidth();
                  int lvt_16_1_ = lvt_12_1_[0].getHeight();
                  if(MathHelper.func_151236_b(lvt_15_1_) != lvt_15_1_ || MathHelper.func_151236_b(lvt_16_1_) != lvt_16_1_) {
                     throw new RuntimeException("Unable to load extra miplevels, source-texture is not power of two");
                  }
               }

               Iterator lvt_15_2_ = lvt_14_1_.iterator();

               while(lvt_15_2_.hasNext()) {
                  int lvt_16_2_ = ((Integer)lvt_15_2_.next()).intValue();
                  if(lvt_16_2_ > 0 && lvt_16_2_ < lvt_12_1_.length - 1 && lvt_12_1_[lvt_16_2_] == null) {
                     ResourceLocation lvt_17_1_ = this.func_147634_a(lvt_9_1_, lvt_16_2_);

                     try {
                        lvt_12_1_[lvt_16_2_] = TextureUtil.func_177053_a(p_110571_1_.func_110536_a(lvt_17_1_).func_110527_b());
                     } catch (IOException var22) {
                        field_147635_d.error("Unable to load miplevel {} from: {}", new Object[]{Integer.valueOf(lvt_16_2_), lvt_17_1_, var22});
                     }
                  }
               }
            }

            AnimationMetadataSection lvt_14_2_ = (AnimationMetadataSection)lvt_11_1_.func_110526_a("animation");
            lvt_8_1_.func_180598_a(lvt_12_1_, lvt_14_2_);
         } catch (RuntimeException var23) {
            field_147635_d.error("Unable to parse metadata from " + lvt_10_1_, var23);
            continue;
         } catch (IOException var24) {
            field_147635_d.error("Using missing texture, unable to load " + lvt_10_1_, var24);
            continue;
         }

         lvt_4_1_ = Math.min(lvt_4_1_, Math.min(lvt_8_1_.func_94211_a(), lvt_8_1_.func_94216_b()));
         int lvt_11_4_ = Math.min(Integer.lowestOneBit(lvt_8_1_.func_94211_a()), Integer.lowestOneBit(lvt_8_1_.func_94216_b()));
         if(lvt_11_4_ < lvt_5_1_) {
            field_147635_d.warn("Texture {} with size {}x{} limits mip level from {} to {}", new Object[]{lvt_10_1_, Integer.valueOf(lvt_8_1_.func_94211_a()), Integer.valueOf(lvt_8_1_.func_94216_b()), Integer.valueOf(MathHelper.func_151239_c(lvt_5_1_)), Integer.valueOf(MathHelper.func_151239_c(lvt_11_4_))});
            lvt_5_1_ = lvt_11_4_;
         }

         lvt_3_1_.func_110934_a(lvt_8_1_);
      }

      int lvt_6_2_ = Math.min(lvt_4_1_, lvt_5_1_);
      int lvt_7_2_ = MathHelper.func_151239_c(lvt_6_2_);
      if(lvt_7_2_ < this.field_147636_j) {
         field_147635_d.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", new Object[]{this.field_94254_c, Integer.valueOf(this.field_147636_j), Integer.valueOf(lvt_7_2_), Integer.valueOf(lvt_6_2_)});
         this.field_147636_j = lvt_7_2_;
      }

      for(final TextureAtlasSprite lvt_9_2_ : this.field_110574_e.values()) {
         try {
            lvt_9_2_.func_147963_d(this.field_147636_j);
         } catch (Throwable var21) {
            CrashReport lvt_11_5_ = CrashReport.func_85055_a(var21, "Applying mipmap");
            CrashReportCategory lvt_12_2_ = lvt_11_5_.func_85058_a("Sprite being mipmapped");
            lvt_12_2_.func_71500_a("Sprite name", new Callable<String>() {
               public String call() throws Exception {
                  return lvt_9_2_.func_94215_i();
               }

               // $FF: synthetic method
               public Object call() throws Exception {
                  return this.call();
               }
            });
            lvt_12_2_.func_71500_a("Sprite size", new Callable<String>() {
               public String call() throws Exception {
                  return lvt_9_2_.func_94211_a() + " x " + lvt_9_2_.func_94216_b();
               }

               // $FF: synthetic method
               public Object call() throws Exception {
                  return this.call();
               }
            });
            lvt_12_2_.func_71500_a("Sprite frames", new Callable<String>() {
               public String call() throws Exception {
                  return lvt_9_2_.func_110970_k() + " frames";
               }

               // $FF: synthetic method
               public Object call() throws Exception {
                  return this.call();
               }
            });
            lvt_12_2_.func_71507_a("Mipmap levels", Integer.valueOf(this.field_147636_j));
            throw new ReportedException(lvt_11_5_);
         }
      }

      this.field_94249_f.func_147963_d(this.field_147636_j);
      lvt_3_1_.func_110934_a(this.field_94249_f);

      try {
         lvt_3_1_.func_94305_f();
      } catch (StitcherException var20) {
         throw var20;
      }

      field_147635_d.info("Created: {}x{} {}-atlas", new Object[]{Integer.valueOf(lvt_3_1_.func_110935_a()), Integer.valueOf(lvt_3_1_.func_110936_b()), this.field_94254_c});
      TextureUtil.func_180600_a(this.func_110552_b(), this.field_147636_j, lvt_3_1_.func_110935_a(), lvt_3_1_.func_110936_b());
      Map<String, TextureAtlasSprite> lvt_8_4_ = Maps.newHashMap(this.field_110574_e);

      for(TextureAtlasSprite lvt_10_3_ : lvt_3_1_.func_94309_g()) {
         String lvt_11_6_ = lvt_10_3_.func_94215_i();
         lvt_8_4_.remove(lvt_11_6_);
         this.field_94252_e.put(lvt_11_6_, lvt_10_3_);

         try {
            TextureUtil.func_147955_a(lvt_10_3_.func_147965_a(0), lvt_10_3_.func_94211_a(), lvt_10_3_.func_94216_b(), lvt_10_3_.func_130010_a(), lvt_10_3_.func_110967_i(), false, false);
         } catch (Throwable var19) {
            CrashReport lvt_13_2_ = CrashReport.func_85055_a(var19, "Stitching texture atlas");
            CrashReportCategory lvt_14_3_ = lvt_13_2_.func_85058_a("Texture being stitched together");
            lvt_14_3_.func_71507_a("Atlas path", this.field_94254_c);
            lvt_14_3_.func_71507_a("Sprite", lvt_10_3_);
            throw new ReportedException(lvt_13_2_);
         }

         if(lvt_10_3_.func_130098_m()) {
            this.field_94258_i.add(lvt_10_3_);
         }
      }

      for(TextureAtlasSprite lvt_10_4_ : lvt_8_4_.values()) {
         lvt_10_4_.func_94217_a(this.field_94249_f);
      }

   }

   private ResourceLocation func_147634_a(ResourceLocation p_147634_1_, int p_147634_2_) {
      return p_147634_2_ == 0?new ResourceLocation(p_147634_1_.func_110624_b(), String.format("%s/%s%s", new Object[]{this.field_94254_c, p_147634_1_.func_110623_a(), ".png"})):new ResourceLocation(p_147634_1_.func_110624_b(), String.format("%s/mipmaps/%s.%d%s", new Object[]{this.field_94254_c, p_147634_1_.func_110623_a(), Integer.valueOf(p_147634_2_), ".png"}));
   }

   public TextureAtlasSprite func_110572_b(String p_110572_1_) {
      TextureAtlasSprite lvt_2_1_ = (TextureAtlasSprite)this.field_94252_e.get(p_110572_1_);
      if(lvt_2_1_ == null) {
         lvt_2_1_ = this.field_94249_f;
      }

      return lvt_2_1_;
   }

   public void func_94248_c() {
      TextureUtil.func_94277_a(this.func_110552_b());

      for(TextureAtlasSprite lvt_2_1_ : this.field_94258_i) {
         lvt_2_1_.func_94219_l();
      }

   }

   public TextureAtlasSprite func_174942_a(ResourceLocation p_174942_1_) {
      if(p_174942_1_ == null) {
         throw new IllegalArgumentException("Location cannot be null!");
      } else {
         TextureAtlasSprite lvt_2_1_ = (TextureAtlasSprite)this.field_110574_e.get(p_174942_1_);
         if(lvt_2_1_ == null) {
            lvt_2_1_ = TextureAtlasSprite.func_176604_a(p_174942_1_);
            this.field_110574_e.put(p_174942_1_.toString(), lvt_2_1_);
         }

         return lvt_2_1_;
      }
   }

   public void func_110550_d() {
      this.func_94248_c();
   }

   public void func_147633_a(int p_147633_1_) {
      this.field_147636_j = p_147633_1_;
   }

   public TextureAtlasSprite func_174944_f() {
      return this.field_94249_f;
   }
}
