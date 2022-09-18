package net.minecraft.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleTexture extends AbstractTexture {
   private static final Logger field_147639_c = LogManager.getLogger();
   protected final ResourceLocation field_110568_b;

   public SimpleTexture(ResourceLocation p_i1275_1_) {
      this.field_110568_b = p_i1275_1_;
   }

   public void func_110551_a(IResourceManager p_110551_1_) throws IOException {
      this.func_147631_c();
      InputStream lvt_2_1_ = null;

      try {
         IResource lvt_3_1_ = p_110551_1_.func_110536_a(this.field_110568_b);
         lvt_2_1_ = lvt_3_1_.func_110527_b();
         BufferedImage lvt_4_1_ = TextureUtil.func_177053_a(lvt_2_1_);
         boolean lvt_5_1_ = false;
         boolean lvt_6_1_ = false;
         if(lvt_3_1_.func_110528_c()) {
            try {
               TextureMetadataSection lvt_7_1_ = (TextureMetadataSection)lvt_3_1_.func_110526_a("texture");
               if(lvt_7_1_ != null) {
                  lvt_5_1_ = lvt_7_1_.func_110479_a();
                  lvt_6_1_ = lvt_7_1_.func_110480_b();
               }
            } catch (RuntimeException var11) {
               field_147639_c.warn("Failed reading metadata of: " + this.field_110568_b, var11);
            }
         }

         TextureUtil.func_110989_a(this.func_110552_b(), lvt_4_1_, lvt_5_1_, lvt_6_1_);
      } finally {
         if(lvt_2_1_ != null) {
            lvt_2_1_.close();
         }

      }

   }
}
