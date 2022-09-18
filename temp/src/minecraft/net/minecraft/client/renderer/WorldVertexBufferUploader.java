package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import org.lwjgl.opengl.GL11;

public class WorldVertexBufferUploader {
   public void func_181679_a(WorldRenderer p_181679_1_) {
      if(p_181679_1_.func_178989_h() > 0) {
         VertexFormat lvt_2_1_ = p_181679_1_.func_178973_g();
         int lvt_3_1_ = lvt_2_1_.func_177338_f();
         ByteBuffer lvt_4_1_ = p_181679_1_.func_178966_f();
         List<VertexFormatElement> lvt_5_1_ = lvt_2_1_.func_177343_g();

         for(int lvt_6_1_ = 0; lvt_6_1_ < lvt_5_1_.size(); ++lvt_6_1_) {
            VertexFormatElement lvt_7_1_ = (VertexFormatElement)lvt_5_1_.get(lvt_6_1_);
            VertexFormatElement.EnumUsage lvt_8_1_ = lvt_7_1_.func_177375_c();
            int lvt_9_1_ = lvt_7_1_.func_177367_b().func_177397_c();
            int lvt_10_1_ = lvt_7_1_.func_177369_e();
            lvt_4_1_.position(lvt_2_1_.func_181720_d(lvt_6_1_));
            switch(lvt_8_1_) {
            case POSITION:
               GL11.glVertexPointer(lvt_7_1_.func_177370_d(), lvt_9_1_, lvt_3_1_, lvt_4_1_);
               GL11.glEnableClientState('\u8074');
               break;
            case UV:
               OpenGlHelper.func_77472_b(OpenGlHelper.field_77478_a + lvt_10_1_);
               GL11.glTexCoordPointer(lvt_7_1_.func_177370_d(), lvt_9_1_, lvt_3_1_, lvt_4_1_);
               GL11.glEnableClientState('\u8078');
               OpenGlHelper.func_77472_b(OpenGlHelper.field_77478_a);
               break;
            case COLOR:
               GL11.glColorPointer(lvt_7_1_.func_177370_d(), lvt_9_1_, lvt_3_1_, lvt_4_1_);
               GL11.glEnableClientState('\u8076');
               break;
            case NORMAL:
               GL11.glNormalPointer(lvt_9_1_, lvt_3_1_, lvt_4_1_);
               GL11.glEnableClientState('\u8075');
            }
         }

         GL11.glDrawArrays(p_181679_1_.func_178979_i(), 0, p_181679_1_.func_178989_h());
         int lvt_6_2_ = 0;

         for(int lvt_7_2_ = lvt_5_1_.size(); lvt_6_2_ < lvt_7_2_; ++lvt_6_2_) {
            VertexFormatElement lvt_8_2_ = (VertexFormatElement)lvt_5_1_.get(lvt_6_2_);
            VertexFormatElement.EnumUsage lvt_9_2_ = lvt_8_2_.func_177375_c();
            int lvt_10_2_ = lvt_8_2_.func_177369_e();
            switch(lvt_9_2_) {
            case POSITION:
               GL11.glDisableClientState('\u8074');
               break;
            case UV:
               OpenGlHelper.func_77472_b(OpenGlHelper.field_77478_a + lvt_10_2_);
               GL11.glDisableClientState('\u8078');
               OpenGlHelper.func_77472_b(OpenGlHelper.field_77478_a);
               break;
            case COLOR:
               GL11.glDisableClientState('\u8076');
               GlStateManager.func_179117_G();
               break;
            case NORMAL:
               GL11.glDisableClientState('\u8075');
            }
         }
      }

      p_181679_1_.func_178965_a();
   }
}
