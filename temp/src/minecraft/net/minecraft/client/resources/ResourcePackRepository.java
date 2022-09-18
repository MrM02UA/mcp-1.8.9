package net.minecraft.client.resources;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.FileResourcePack;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackRepository {
   private static final Logger field_177320_c = LogManager.getLogger();
   private static final FileFilter field_110622_a = new FileFilter() {
      public boolean accept(File p_accept_1_) {
         boolean lvt_2_1_ = p_accept_1_.isFile() && p_accept_1_.getName().endsWith(".zip");
         boolean lvt_3_1_ = p_accept_1_.isDirectory() && (new File(p_accept_1_, "pack.mcmeta")).isFile();
         return lvt_2_1_ || lvt_3_1_;
      }
   };
   private final File field_110618_d;
   public final IResourcePack field_110620_b;
   private final File field_148534_e;
   public final IMetadataSerializer field_110621_c;
   private IResourcePack field_148532_f;
   private final ReentrantLock field_177321_h = new ReentrantLock();
   private ListenableFuture<Object> field_177322_i;
   private List<ResourcePackRepository.Entry> field_110619_e = Lists.newArrayList();
   private List<ResourcePackRepository.Entry> field_110617_f = Lists.newArrayList();

   public ResourcePackRepository(File p_i45101_1_, File p_i45101_2_, IResourcePack p_i45101_3_, IMetadataSerializer p_i45101_4_, GameSettings p_i45101_5_) {
      this.field_110618_d = p_i45101_1_;
      this.field_148534_e = p_i45101_2_;
      this.field_110620_b = p_i45101_3_;
      this.field_110621_c = p_i45101_4_;
      this.func_110616_f();
      this.func_110611_a();
      Iterator<String> lvt_6_1_ = p_i45101_5_.field_151453_l.iterator();

      while(lvt_6_1_.hasNext()) {
         String lvt_7_1_ = (String)lvt_6_1_.next();

         for(ResourcePackRepository.Entry lvt_9_1_ : this.field_110619_e) {
            if(lvt_9_1_.func_110515_d().equals(lvt_7_1_)) {
               if(lvt_9_1_.func_183027_f() == 1 || p_i45101_5_.field_183018_l.contains(lvt_9_1_.func_110515_d())) {
                  this.field_110617_f.add(lvt_9_1_);
                  break;
               }

               lvt_6_1_.remove();
               field_177320_c.warn("Removed selected resource pack {} because it\'s no longer compatible", new Object[]{lvt_9_1_.func_110515_d()});
            }
         }
      }

   }

   private void func_110616_f() {
      if(this.field_110618_d.exists()) {
         if(!this.field_110618_d.isDirectory() && (!this.field_110618_d.delete() || !this.field_110618_d.mkdirs())) {
            field_177320_c.warn("Unable to recreate resourcepack folder, it exists but is not a directory: " + this.field_110618_d);
         }
      } else if(!this.field_110618_d.mkdirs()) {
         field_177320_c.warn("Unable to create resourcepack folder: " + this.field_110618_d);
      }

   }

   private List<File> func_110614_g() {
      return this.field_110618_d.isDirectory()?Arrays.asList(this.field_110618_d.listFiles(field_110622_a)):Collections.emptyList();
   }

   public void func_110611_a() {
      List<ResourcePackRepository.Entry> lvt_1_1_ = Lists.newArrayList();

      for(File lvt_3_1_ : this.func_110614_g()) {
         ResourcePackRepository.Entry lvt_4_1_ = new ResourcePackRepository.Entry(lvt_3_1_);
         if(!this.field_110619_e.contains(lvt_4_1_)) {
            try {
               lvt_4_1_.func_110516_a();
               lvt_1_1_.add(lvt_4_1_);
            } catch (Exception var6) {
               lvt_1_1_.remove(lvt_4_1_);
            }
         } else {
            int lvt_5_2_ = this.field_110619_e.indexOf(lvt_4_1_);
            if(lvt_5_2_ > -1 && lvt_5_2_ < this.field_110619_e.size()) {
               lvt_1_1_.add(this.field_110619_e.get(lvt_5_2_));
            }
         }
      }

      this.field_110619_e.removeAll(lvt_1_1_);

      for(ResourcePackRepository.Entry lvt_3_2_ : this.field_110619_e) {
         lvt_3_2_.func_110517_b();
      }

      this.field_110619_e = lvt_1_1_;
   }

   public List<ResourcePackRepository.Entry> func_110609_b() {
      return ImmutableList.copyOf(this.field_110619_e);
   }

   public List<ResourcePackRepository.Entry> func_110613_c() {
      return ImmutableList.copyOf(this.field_110617_f);
   }

   public void func_148527_a(List<ResourcePackRepository.Entry> p_148527_1_) {
      this.field_110617_f.clear();
      this.field_110617_f.addAll(p_148527_1_);
   }

   public File func_110612_e() {
      return this.field_110618_d;
   }

   public ListenableFuture<Object> func_180601_a(String p_180601_1_, String p_180601_2_) {
      String lvt_3_1_;
      if(p_180601_2_.matches("^[a-f0-9]{40}$")) {
         lvt_3_1_ = p_180601_2_;
      } else {
         lvt_3_1_ = "legacy";
      }

      final File lvt_4_1_ = new File(this.field_148534_e, lvt_3_1_);
      this.field_177321_h.lock();

      try {
         this.func_148529_f();
         if(lvt_4_1_.exists() && p_180601_2_.length() == 40) {
            try {
               String lvt_5_1_ = Hashing.sha1().hashBytes(Files.toByteArray(lvt_4_1_)).toString();
               if(lvt_5_1_.equals(p_180601_2_)) {
                  ListenableFuture var16 = this.func_177319_a(lvt_4_1_);
                  return var16;
               }

               field_177320_c.warn("File " + lvt_4_1_ + " had wrong hash (expected " + p_180601_2_ + ", found " + lvt_5_1_ + "). Deleting it.");
               FileUtils.deleteQuietly(lvt_4_1_);
            } catch (IOException var13) {
               field_177320_c.warn("File " + lvt_4_1_ + " couldn\'t be hashed. Deleting it.", var13);
               FileUtils.deleteQuietly(lvt_4_1_);
            }
         }

         this.func_183028_i();
         final GuiScreenWorking lvt_5_3_ = new GuiScreenWorking();
         Map<String, String> lvt_6_1_ = Minecraft.func_175596_ai();
         final Minecraft lvt_7_1_ = Minecraft.func_71410_x();
         Futures.getUnchecked(lvt_7_1_.func_152344_a(new Runnable() {
            public void run() {
               lvt_7_1_.func_147108_a(lvt_5_3_);
            }
         }));
         final SettableFuture<Object> lvt_8_1_ = SettableFuture.create();
         this.field_177322_i = HttpUtil.func_180192_a(lvt_4_1_, p_180601_1_, lvt_6_1_, 52428800, lvt_5_3_, lvt_7_1_.func_110437_J());
         Futures.addCallback(this.field_177322_i, new FutureCallback<Object>() {
            public void onSuccess(Object p_onSuccess_1_) {
               ResourcePackRepository.this.func_177319_a(lvt_4_1_);
               lvt_8_1_.set((Object)null);
            }

            public void onFailure(Throwable p_onFailure_1_) {
               lvt_8_1_.setException(p_onFailure_1_);
            }
         });
         ListenableFuture var9 = this.field_177322_i;
         return var9;
      } finally {
         this.field_177321_h.unlock();
      }
   }

   private void func_183028_i() {
      List<File> lvt_1_1_ = Lists.newArrayList(FileUtils.listFiles(this.field_148534_e, TrueFileFilter.TRUE, (IOFileFilter)null));
      Collections.sort(lvt_1_1_, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
      int lvt_2_1_ = 0;

      for(File lvt_4_1_ : lvt_1_1_) {
         if(lvt_2_1_++ >= 10) {
            field_177320_c.info("Deleting old server resource pack " + lvt_4_1_.getName());
            FileUtils.deleteQuietly(lvt_4_1_);
         }
      }

   }

   public ListenableFuture<Object> func_177319_a(File p_177319_1_) {
      this.field_148532_f = new FileResourcePack(p_177319_1_);
      return Minecraft.func_71410_x().func_175603_A();
   }

   public IResourcePack func_148530_e() {
      return this.field_148532_f;
   }

   public void func_148529_f() {
      this.field_177321_h.lock();

      try {
         if(this.field_177322_i != null) {
            this.field_177322_i.cancel(true);
         }

         this.field_177322_i = null;
         if(this.field_148532_f != null) {
            this.field_148532_f = null;
            Minecraft.func_71410_x().func_175603_A();
         }
      } finally {
         this.field_177321_h.unlock();
      }

   }

   public class Entry {
      private final File field_110523_b;
      private IResourcePack field_110524_c;
      private PackMetadataSection field_110521_d;
      private BufferedImage field_110522_e;
      private ResourceLocation field_110520_f;

      private Entry(File p_i1295_2_) {
         this.field_110523_b = p_i1295_2_;
      }

      public void func_110516_a() throws IOException {
         this.field_110524_c = (IResourcePack)(this.field_110523_b.isDirectory()?new FolderResourcePack(this.field_110523_b):new FileResourcePack(this.field_110523_b));
         this.field_110521_d = (PackMetadataSection)this.field_110524_c.func_135058_a(ResourcePackRepository.this.field_110621_c, "pack");

         try {
            this.field_110522_e = this.field_110524_c.func_110586_a();
         } catch (IOException var2) {
            ;
         }

         if(this.field_110522_e == null) {
            this.field_110522_e = ResourcePackRepository.this.field_110620_b.func_110586_a();
         }

         this.func_110517_b();
      }

      public void func_110518_a(TextureManager p_110518_1_) {
         if(this.field_110520_f == null) {
            this.field_110520_f = p_110518_1_.func_110578_a("texturepackicon", new DynamicTexture(this.field_110522_e));
         }

         p_110518_1_.func_110577_a(this.field_110520_f);
      }

      public void func_110517_b() {
         if(this.field_110524_c instanceof Closeable) {
            IOUtils.closeQuietly((Closeable)this.field_110524_c);
         }

      }

      public IResourcePack func_110514_c() {
         return this.field_110524_c;
      }

      public String func_110515_d() {
         return this.field_110524_c.func_130077_b();
      }

      public String func_110519_e() {
         return this.field_110521_d == null?EnumChatFormatting.RED + "Invalid pack.mcmeta (or missing \'pack\' section)":this.field_110521_d.func_152805_a().func_150254_d();
      }

      public int func_183027_f() {
         return this.field_110521_d.func_110462_b();
      }

      public boolean equals(Object p_equals_1_) {
         return this == p_equals_1_?true:(p_equals_1_ instanceof ResourcePackRepository.Entry?this.toString().equals(p_equals_1_.toString()):false);
      }

      public int hashCode() {
         return this.toString().hashCode();
      }

      public String toString() {
         return String.format("%s:%s:%d", new Object[]{this.field_110523_b.getName(), this.field_110523_b.isDirectory()?"folder":"zip", Long.valueOf(this.field_110523_b.lastModified())});
      }
   }
}
