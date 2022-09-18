package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.util.List;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import org.lwjgl.opengl.GL11;

public class WorldVertexBufferUploader
{
    @SuppressWarnings("incomplete-switch")
    public void draw(WorldRenderer p_181679_1_)
    {
        if (p_181679_1_.getVertexCount() > 0)
        {
            VertexFormat lvt_2_1_ = p_181679_1_.getVertexFormat();
            int lvt_3_1_ = lvt_2_1_.getNextOffset();
            ByteBuffer lvt_4_1_ = p_181679_1_.getByteBuffer();
            List<VertexFormatElement> lvt_5_1_ = lvt_2_1_.getElements();

            for (int lvt_6_1_ = 0; lvt_6_1_ < lvt_5_1_.size(); ++lvt_6_1_)
            {
                VertexFormatElement lvt_7_1_ = (VertexFormatElement)lvt_5_1_.get(lvt_6_1_);
                VertexFormatElement.EnumUsage lvt_8_1_ = lvt_7_1_.getUsage();
                int lvt_9_1_ = lvt_7_1_.getType().getGlConstant();
                int lvt_10_1_ = lvt_7_1_.getIndex();
                lvt_4_1_.position(lvt_2_1_.getOffset(lvt_6_1_));

                switch (lvt_8_1_)
                {
                    case POSITION:
                        GL11.glVertexPointer(lvt_7_1_.getElementCount(), lvt_9_1_, lvt_3_1_, lvt_4_1_);
                        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                        break;

                    case UV:
                        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + lvt_10_1_);
                        GL11.glTexCoordPointer(lvt_7_1_.getElementCount(), lvt_9_1_, lvt_3_1_, lvt_4_1_);
                        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                        break;

                    case COLOR:
                        GL11.glColorPointer(lvt_7_1_.getElementCount(), lvt_9_1_, lvt_3_1_, lvt_4_1_);
                        GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
                        break;

                    case NORMAL:
                        GL11.glNormalPointer(lvt_9_1_, lvt_3_1_, lvt_4_1_);
                        GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
                }
            }

            GL11.glDrawArrays(p_181679_1_.getDrawMode(), 0, p_181679_1_.getVertexCount());
            int lvt_6_2_ = 0;

            for (int lvt_7_2_ = lvt_5_1_.size(); lvt_6_2_ < lvt_7_2_; ++lvt_6_2_)
            {
                VertexFormatElement lvt_8_2_ = (VertexFormatElement)lvt_5_1_.get(lvt_6_2_);
                VertexFormatElement.EnumUsage lvt_9_2_ = lvt_8_2_.getUsage();
                int lvt_10_2_ = lvt_8_2_.getIndex();

                switch (lvt_9_2_)
                {
                    case POSITION:
                        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
                        break;

                    case UV:
                        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + lvt_10_2_);
                        GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                        break;

                    case COLOR:
                        GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
                        GlStateManager.resetColor();
                        break;

                    case NORMAL:
                        GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
                }
            }
        }

        p_181679_1_.reset();
    }
}
