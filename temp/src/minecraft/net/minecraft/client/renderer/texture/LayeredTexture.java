package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LayeredTexture extends AbstractTexture {
   private static final Logger field_147638_c = LogManager.getLogger();
   public final List<String> field_110567_b;

   public LayeredTexture(String... p_i1274_1_) {
      this.field_110567_b = Lists.newArrayList(p_i1274_1_);
   }

   public void func_110551_a(IResourceManager p_110551_1_) throws IOException {
      this.func_147631_c();
      BufferedImage lvt_2_1_ = null;

      try {
         for(String lvt_4_1_ : this.field_110567_b) {
            if(lvt_4_1_ != null) {
               InputStream lvt_5_1_ = p_110551_1_.func_110536_a(new ResourceLocation(lvt_4_1_)).func_110527_b();
               BufferedImage lvt_6_1_ = TextureUtil.func_177053_a(lvt_5_1_);
               if(lvt_2_1_ == null) {
                  lvt_2_1_ = new BufferedImage(lvt_6_1_.getWidth(), lvt_6_1_.getHeight(), 2);
               }

               lvt_2_1_.getGraphics().drawImage(lvt_6_1_, 0, 0, (ImageObserver)null);
            }
         }
      } catch (IOException var7) {
         field_147638_c.error("Couldn\'t load layered image", var7);
         return;
      }

      TextureUtil.func_110987_a(this.func_110552_b(), lvt_2_1_);
   }
}
