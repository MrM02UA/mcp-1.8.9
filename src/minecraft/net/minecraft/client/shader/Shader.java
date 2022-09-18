package net.minecraft.client.shader;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.util.JsonException;
import org.lwjgl.util.vector.Matrix4f;

public class Shader
{
    private final ShaderManager manager;
    public final Framebuffer framebufferIn;
    public final Framebuffer framebufferOut;
    private final List<Object> listAuxFramebuffers = Lists.newArrayList();
    private final List<String> listAuxNames = Lists.newArrayList();
    private final List<Integer> listAuxWidths = Lists.newArrayList();
    private final List<Integer> listAuxHeights = Lists.newArrayList();
    private Matrix4f projectionMatrix;

    public Shader(IResourceManager p_i45089_1_, String p_i45089_2_, Framebuffer p_i45089_3_, Framebuffer p_i45089_4_) throws JsonException, IOException
    {
        this.manager = new ShaderManager(p_i45089_1_, p_i45089_2_);
        this.framebufferIn = p_i45089_3_;
        this.framebufferOut = p_i45089_4_;
    }

    public void deleteShader()
    {
        this.manager.deleteShader();
    }

    public void addAuxFramebuffer(String p_148041_1_, Object p_148041_2_, int p_148041_3_, int p_148041_4_)
    {
        this.listAuxNames.add(this.listAuxNames.size(), p_148041_1_);
        this.listAuxFramebuffers.add(this.listAuxFramebuffers.size(), p_148041_2_);
        this.listAuxWidths.add(this.listAuxWidths.size(), Integer.valueOf(p_148041_3_));
        this.listAuxHeights.add(this.listAuxHeights.size(), Integer.valueOf(p_148041_4_));
    }

    private void preLoadShader()
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableAlpha();
        GlStateManager.disableFog();
        GlStateManager.disableLighting();
        GlStateManager.disableColorMaterial();
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(0);
    }

    public void setProjectionMatrix(Matrix4f p_148045_1_)
    {
        this.projectionMatrix = p_148045_1_;
    }

    public void loadShader(float p_148042_1_)
    {
        this.preLoadShader();
        this.framebufferIn.unbindFramebuffer();
        float lvt_2_1_ = (float)this.framebufferOut.framebufferTextureWidth;
        float lvt_3_1_ = (float)this.framebufferOut.framebufferTextureHeight;
        GlStateManager.viewport(0, 0, (int)lvt_2_1_, (int)lvt_3_1_);
        this.manager.addSamplerTexture("DiffuseSampler", this.framebufferIn);

        for (int lvt_4_1_ = 0; lvt_4_1_ < this.listAuxFramebuffers.size(); ++lvt_4_1_)
        {
            this.manager.addSamplerTexture((String)this.listAuxNames.get(lvt_4_1_), this.listAuxFramebuffers.get(lvt_4_1_));
            this.manager.getShaderUniformOrDefault("AuxSize" + lvt_4_1_).set((float)((Integer)this.listAuxWidths.get(lvt_4_1_)).intValue(), (float)((Integer)this.listAuxHeights.get(lvt_4_1_)).intValue());
        }

        this.manager.getShaderUniformOrDefault("ProjMat").set(this.projectionMatrix);
        this.manager.getShaderUniformOrDefault("InSize").set((float)this.framebufferIn.framebufferTextureWidth, (float)this.framebufferIn.framebufferTextureHeight);
        this.manager.getShaderUniformOrDefault("OutSize").set(lvt_2_1_, lvt_3_1_);
        this.manager.getShaderUniformOrDefault("Time").set(p_148042_1_);
        Minecraft lvt_4_2_ = Minecraft.getMinecraft();
        this.manager.getShaderUniformOrDefault("ScreenSize").set((float)lvt_4_2_.displayWidth, (float)lvt_4_2_.displayHeight);
        this.manager.useShader();
        this.framebufferOut.framebufferClear();
        this.framebufferOut.bindFramebuffer(false);
        GlStateManager.depthMask(false);
        GlStateManager.colorMask(true, true, true, true);
        Tessellator lvt_5_1_ = Tessellator.getInstance();
        WorldRenderer lvt_6_1_ = lvt_5_1_.getWorldRenderer();
        lvt_6_1_.begin(7, DefaultVertexFormats.POSITION_COLOR);
        lvt_6_1_.pos(0.0D, (double)lvt_3_1_, 500.0D).color(255, 255, 255, 255).endVertex();
        lvt_6_1_.pos((double)lvt_2_1_, (double)lvt_3_1_, 500.0D).color(255, 255, 255, 255).endVertex();
        lvt_6_1_.pos((double)lvt_2_1_, 0.0D, 500.0D).color(255, 255, 255, 255).endVertex();
        lvt_6_1_.pos(0.0D, 0.0D, 500.0D).color(255, 255, 255, 255).endVertex();
        lvt_5_1_.draw();
        GlStateManager.depthMask(true);
        GlStateManager.colorMask(true, true, true, true);
        this.manager.endShader();
        this.framebufferOut.unbindFramebuffer();
        this.framebufferIn.unbindFramebufferTexture();

        for (Object lvt_8_1_ : this.listAuxFramebuffers)
        {
            if (lvt_8_1_ instanceof Framebuffer)
            {
                ((Framebuffer)lvt_8_1_).unbindFramebufferTexture();
            }
        }
    }

    public ShaderManager getShaderManager()
    {
        return this.manager;
    }
}
