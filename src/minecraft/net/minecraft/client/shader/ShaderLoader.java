package net.minecraft.client.shader;

import com.google.common.collect.Maps;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.BufferUtils;

public class ShaderLoader
{
    private final ShaderLoader.ShaderType shaderType;
    private final String shaderFilename;
    private int shader;
    private int shaderAttachCount = 0;

    private ShaderLoader(ShaderLoader.ShaderType type, int shaderId, String filename)
    {
        this.shaderType = type;
        this.shader = shaderId;
        this.shaderFilename = filename;
    }

    public void attachShader(ShaderManager manager)
    {
        ++this.shaderAttachCount;
        OpenGlHelper.glAttachShader(manager.getProgram(), this.shader);
    }

    public void deleteShader(ShaderManager manager)
    {
        --this.shaderAttachCount;

        if (this.shaderAttachCount <= 0)
        {
            OpenGlHelper.glDeleteShader(this.shader);
            this.shaderType.getLoadedShaders().remove(this.shaderFilename);
        }
    }

    public String getShaderFilename()
    {
        return this.shaderFilename;
    }

    public static ShaderLoader loadShader(IResourceManager resourceManager, ShaderLoader.ShaderType type, String filename) throws IOException
    {
        ShaderLoader lvt_3_1_ = (ShaderLoader)type.getLoadedShaders().get(filename);

        if (lvt_3_1_ == null)
        {
            ResourceLocation lvt_4_1_ = new ResourceLocation("shaders/program/" + filename + type.getShaderExtension());
            BufferedInputStream lvt_5_1_ = new BufferedInputStream(resourceManager.getResource(lvt_4_1_).getInputStream());
            byte[] lvt_6_1_ = toByteArray(lvt_5_1_);
            ByteBuffer lvt_7_1_ = BufferUtils.createByteBuffer(lvt_6_1_.length);
            lvt_7_1_.put(lvt_6_1_);
            lvt_7_1_.position(0);
            int lvt_8_1_ = OpenGlHelper.glCreateShader(type.getShaderMode());
            OpenGlHelper.glShaderSource(lvt_8_1_, lvt_7_1_);
            OpenGlHelper.glCompileShader(lvt_8_1_);

            if (OpenGlHelper.glGetShaderi(lvt_8_1_, OpenGlHelper.GL_COMPILE_STATUS) == 0)
            {
                String lvt_9_1_ = StringUtils.trim(OpenGlHelper.glGetShaderInfoLog(lvt_8_1_, 32768));
                JsonException lvt_10_1_ = new JsonException("Couldn\'t compile " + type.getShaderName() + " program: " + lvt_9_1_);
                lvt_10_1_.func_151381_b(lvt_4_1_.getResourcePath());
                throw lvt_10_1_;
            }

            lvt_3_1_ = new ShaderLoader(type, lvt_8_1_, filename);
            type.getLoadedShaders().put(filename, lvt_3_1_);
        }

        return lvt_3_1_;
    }

    protected static byte[] toByteArray(BufferedInputStream p_177064_0_) throws IOException
    {
        byte[] var1;

        try
        {
            var1 = IOUtils.toByteArray(p_177064_0_);
        }
        finally
        {
            p_177064_0_.close();
        }

        return var1;
    }

    public static enum ShaderType
    {
        VERTEX("vertex", ".vsh", OpenGlHelper.GL_VERTEX_SHADER),
        FRAGMENT("fragment", ".fsh", OpenGlHelper.GL_FRAGMENT_SHADER);

        private final String shaderName;
        private final String shaderExtension;
        private final int shaderMode;
        private final Map<String, ShaderLoader> loadedShaders = Maps.newHashMap();

        private ShaderType(String p_i45090_3_, String p_i45090_4_, int p_i45090_5_)
        {
            this.shaderName = p_i45090_3_;
            this.shaderExtension = p_i45090_4_;
            this.shaderMode = p_i45090_5_;
        }

        public String getShaderName()
        {
            return this.shaderName;
        }

        protected String getShaderExtension()
        {
            return this.shaderExtension;
        }

        protected int getShaderMode()
        {
            return this.shaderMode;
        }

        protected Map<String, ShaderLoader> getLoadedShaders()
        {
            return this.loadedShaders;
        }
    }
}
