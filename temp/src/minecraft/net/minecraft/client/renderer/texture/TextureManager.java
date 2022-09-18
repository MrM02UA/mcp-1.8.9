package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.client.renderer.texture.ITickableTextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureManager implements ITickable, IResourceManagerReloadListener {
   private static final Logger field_147646_a = LogManager.getLogger();
   private final Map<ResourceLocation, ITextureObject> field_110585_a = Maps.newHashMap();
   private final List<ITickable> field_110583_b = Lists.newArrayList();
   private final Map<String, Integer> field_110584_c = Maps.newHashMap();
   private IResourceManager field_110582_d;

   public TextureManager(IResourceManager p_i1284_1_) {
      this.field_110582_d = p_i1284_1_;
   }

   public void func_110577_a(ResourceLocation p_110577_1_) {
      ITextureObject lvt_2_1_ = (ITextureObject)this.field_110585_a.get(p_110577_1_);
      if(lvt_2_1_ == null) {
         lvt_2_1_ = new SimpleTexture(p_110577_1_);
         this.func_110579_a(p_110577_1_, lvt_2_1_);
      }

      TextureUtil.func_94277_a(lvt_2_1_.func_110552_b());
   }

   public boolean func_110580_a(ResourceLocation p_110580_1_, ITickableTextureObject p_110580_2_) {
      if(this.func_110579_a(p_110580_1_, p_110580_2_)) {
         this.field_110583_b.add(p_110580_2_);
         return true;
      } else {
         return false;
      }
   }

   public boolean func_110579_a(ResourceLocation p_110579_1_, final ITextureObject p_110579_2_) {
      boolean lvt_3_1_ = true;

      try {
         ((ITextureObject)p_110579_2_).func_110551_a(this.field_110582_d);
      } catch (IOException var8) {
         field_147646_a.warn("Failed to load texture: " + p_110579_1_, var8);
         p_110579_2_ = TextureUtil.field_111001_a;
         this.field_110585_a.put(p_110579_1_, p_110579_2_);
         lvt_3_1_ = false;
      } catch (Throwable var9) {
         CrashReport lvt_5_1_ = CrashReport.func_85055_a(var9, "Registering texture");
         CrashReportCategory lvt_6_1_ = lvt_5_1_.func_85058_a("Resource location being registered");
         lvt_6_1_.func_71507_a("Resource location", p_110579_1_);
         lvt_6_1_.func_71500_a("Texture object class", new Callable<String>() {
            public String call() throws Exception {
               return p_110579_2_.getClass().getName();
            }

            // $FF: synthetic method
            public Object call() throws Exception {
               return this.call();
            }
         });
         throw new ReportedException(lvt_5_1_);
      }

      this.field_110585_a.put(p_110579_1_, p_110579_2_);
      return lvt_3_1_;
   }

   public ITextureObject func_110581_b(ResourceLocation p_110581_1_) {
      return (ITextureObject)this.field_110585_a.get(p_110581_1_);
   }

   public ResourceLocation func_110578_a(String p_110578_1_, DynamicTexture p_110578_2_) {
      Integer lvt_3_1_ = (Integer)this.field_110584_c.get(p_110578_1_);
      if(lvt_3_1_ == null) {
         lvt_3_1_ = Integer.valueOf(1);
      } else {
         lvt_3_1_ = Integer.valueOf(lvt_3_1_.intValue() + 1);
      }

      this.field_110584_c.put(p_110578_1_, lvt_3_1_);
      ResourceLocation lvt_4_1_ = new ResourceLocation(String.format("dynamic/%s_%d", new Object[]{p_110578_1_, lvt_3_1_}));
      this.func_110579_a(lvt_4_1_, p_110578_2_);
      return lvt_4_1_;
   }

   public void func_110550_d() {
      for(ITickable lvt_2_1_ : this.field_110583_b) {
         lvt_2_1_.func_110550_d();
      }

   }

   public void func_147645_c(ResourceLocation p_147645_1_) {
      ITextureObject lvt_2_1_ = this.func_110581_b(p_147645_1_);
      if(lvt_2_1_ != null) {
         TextureUtil.func_147942_a(lvt_2_1_.func_110552_b());
      }

   }

   public void func_110549_a(IResourceManager p_110549_1_) {
      for(Entry<ResourceLocation, ITextureObject> lvt_3_1_ : this.field_110585_a.entrySet()) {
         this.func_110579_a((ResourceLocation)lvt_3_1_.getKey(), (ITextureObject)lvt_3_1_.getValue());
      }

   }
}
