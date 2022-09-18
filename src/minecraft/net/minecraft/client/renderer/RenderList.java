package net.minecraft.client.renderer;

import net.minecraft.client.renderer.chunk.ListedRenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.EnumWorldBlockLayer;
import org.lwjgl.opengl.GL11;

public class RenderList extends ChunkRenderContainer
{
    public void renderChunkLayer(EnumWorldBlockLayer layer)
    {
        if (this.initialized)
        {
            for (RenderChunk lvt_3_1_ : this.renderChunks)
            {
                ListedRenderChunk lvt_4_1_ = (ListedRenderChunk)lvt_3_1_;
                GlStateManager.pushMatrix();
                this.preRenderChunk(lvt_3_1_);
                GL11.glCallList(lvt_4_1_.getDisplayList(layer, lvt_4_1_.getCompiledChunk()));
                GlStateManager.popMatrix();
            }

            GlStateManager.resetColor();
            this.renderChunks.clear();
        }
    }
}
