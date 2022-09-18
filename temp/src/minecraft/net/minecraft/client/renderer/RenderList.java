package net.minecraft.client.renderer;

import net.minecraft.client.renderer.ChunkRenderContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.chunk.ListedRenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.EnumWorldBlockLayer;
import org.lwjgl.opengl.GL11;

public class RenderList extends ChunkRenderContainer {
   public void func_178001_a(EnumWorldBlockLayer p_178001_1_) {
      if(this.field_178007_b) {
         for(RenderChunk lvt_3_1_ : this.field_178009_a) {
            ListedRenderChunk lvt_4_1_ = (ListedRenderChunk)lvt_3_1_;
            GlStateManager.func_179094_E();
            this.func_178003_a(lvt_3_1_);
            GL11.glCallList(lvt_4_1_.func_178600_a(p_178001_1_, lvt_4_1_.func_178571_g()));
            GlStateManager.func_179121_F();
         }

         GlStateManager.func_179117_G();
         this.field_178009_a.clear();
      }
   }
}
