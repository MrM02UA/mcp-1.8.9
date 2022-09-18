package net.minecraft.client.shader;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.util.JsonBlendingMode;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShaderManager
{
    private static final Logger logger = LogManager.getLogger();
    private static final ShaderDefault defaultShaderUniform = new ShaderDefault();
    private static ShaderManager staticShaderManager = null;
    private static int currentProgram = -1;
    private static boolean field_148000_e = true;
    private final Map<String, Object> shaderSamplers = Maps.newHashMap();
    private final List<String> samplerNames = Lists.newArrayList();
    private final List<Integer> shaderSamplerLocations = Lists.newArrayList();
    private final List<ShaderUniform> shaderUniforms = Lists.newArrayList();
    private final List<Integer> shaderUniformLocations = Lists.newArrayList();
    private final Map<String, ShaderUniform> mappedShaderUniforms = Maps.newHashMap();
    private final int program;
    private final String programFilename;
    private final boolean useFaceCulling;
    private boolean isDirty;
    private final JsonBlendingMode field_148016_p;
    private final List<Integer> attribLocations;
    private final List<String> attributes;
    private final ShaderLoader vertexShaderLoader;
    private final ShaderLoader fragmentShaderLoader;

    public ShaderManager(IResourceManager resourceManager, String programName) throws JsonException, IOException
    {
        JsonParser lvt_3_1_ = new JsonParser();
        ResourceLocation lvt_4_1_ = new ResourceLocation("shaders/program/" + programName + ".json");
        this.programFilename = programName;
        InputStream lvt_5_1_ = null;

        try
        {
            lvt_5_1_ = resourceManager.getResource(lvt_4_1_).getInputStream();
            JsonObject lvt_6_1_ = lvt_3_1_.parse(IOUtils.toString(lvt_5_1_, Charsets.UTF_8)).getAsJsonObject();
            String lvt_7_1_ = JsonUtils.getString(lvt_6_1_, "vertex");
            String lvt_8_1_ = JsonUtils.getString(lvt_6_1_, "fragment");
            JsonArray lvt_9_1_ = JsonUtils.getJsonArray(lvt_6_1_, "samplers", (JsonArray)null);

            if (lvt_9_1_ != null)
            {
                int lvt_10_1_ = 0;

                for (JsonElement lvt_12_1_ : lvt_9_1_)
                {
                    try
                    {
                        this.parseSampler(lvt_12_1_);
                    }
                    catch (Exception var25)
                    {
                        JsonException lvt_14_1_ = JsonException.func_151379_a(var25);
                        lvt_14_1_.func_151380_a("samplers[" + lvt_10_1_ + "]");
                        throw lvt_14_1_;
                    }

                    ++lvt_10_1_;
                }
            }

            JsonArray lvt_10_2_ = JsonUtils.getJsonArray(lvt_6_1_, "attributes", (JsonArray)null);

            if (lvt_10_2_ != null)
            {
                int lvt_11_2_ = 0;
                this.attribLocations = Lists.newArrayListWithCapacity(lvt_10_2_.size());
                this.attributes = Lists.newArrayListWithCapacity(lvt_10_2_.size());

                for (JsonElement lvt_13_2_ : lvt_10_2_)
                {
                    try
                    {
                        this.attributes.add(JsonUtils.getString(lvt_13_2_, "attribute"));
                    }
                    catch (Exception var24)
                    {
                        JsonException lvt_15_1_ = JsonException.func_151379_a(var24);
                        lvt_15_1_.func_151380_a("attributes[" + lvt_11_2_ + "]");
                        throw lvt_15_1_;
                    }

                    ++lvt_11_2_;
                }
            }
            else
            {
                this.attribLocations = null;
                this.attributes = null;
            }

            JsonArray lvt_11_3_ = JsonUtils.getJsonArray(lvt_6_1_, "uniforms", (JsonArray)null);

            if (lvt_11_3_ != null)
            {
                int lvt_12_3_ = 0;

                for (JsonElement lvt_14_3_ : lvt_11_3_)
                {
                    try
                    {
                        this.parseUniform(lvt_14_3_);
                    }
                    catch (Exception var23)
                    {
                        JsonException lvt_16_1_ = JsonException.func_151379_a(var23);
                        lvt_16_1_.func_151380_a("uniforms[" + lvt_12_3_ + "]");
                        throw lvt_16_1_;
                    }

                    ++lvt_12_3_;
                }
            }

            this.field_148016_p = JsonBlendingMode.func_148110_a(JsonUtils.getJsonObject(lvt_6_1_, "blend", (JsonObject)null));
            this.useFaceCulling = JsonUtils.getBoolean(lvt_6_1_, "cull", true);
            this.vertexShaderLoader = ShaderLoader.loadShader(resourceManager, ShaderLoader.ShaderType.VERTEX, lvt_7_1_);
            this.fragmentShaderLoader = ShaderLoader.loadShader(resourceManager, ShaderLoader.ShaderType.FRAGMENT, lvt_8_1_);
            this.program = ShaderLinkHelper.getStaticShaderLinkHelper().createProgram();
            ShaderLinkHelper.getStaticShaderLinkHelper().linkProgram(this);
            this.setupUniforms();

            if (this.attributes != null)
            {
                for (String lvt_13_4_ : this.attributes)
                {
                    int lvt_14_4_ = OpenGlHelper.glGetAttribLocation(this.program, lvt_13_4_);
                    this.attribLocations.add(Integer.valueOf(lvt_14_4_));
                }
            }
        }
        catch (Exception var26)
        {
            JsonException lvt_8_2_ = JsonException.func_151379_a(var26);
            lvt_8_2_.func_151381_b(lvt_4_1_.getResourcePath());
            throw lvt_8_2_;
        }
        finally
        {
            IOUtils.closeQuietly(lvt_5_1_);
        }

        this.markDirty();
    }

    public void deleteShader()
    {
        ShaderLinkHelper.getStaticShaderLinkHelper().deleteShader(this);
    }

    public void endShader()
    {
        OpenGlHelper.glUseProgram(0);
        currentProgram = -1;
        staticShaderManager = null;
        field_148000_e = true;

        for (int lvt_1_1_ = 0; lvt_1_1_ < this.shaderSamplerLocations.size(); ++lvt_1_1_)
        {
            if (this.shaderSamplers.get(this.samplerNames.get(lvt_1_1_)) != null)
            {
                GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit + lvt_1_1_);
                GlStateManager.bindTexture(0);
            }
        }
    }

    public void useShader()
    {
        this.isDirty = false;
        staticShaderManager = this;
        this.field_148016_p.func_148109_a();

        if (this.program != currentProgram)
        {
            OpenGlHelper.glUseProgram(this.program);
            currentProgram = this.program;
        }

        if (this.useFaceCulling)
        {
            GlStateManager.enableCull();
        }
        else
        {
            GlStateManager.disableCull();
        }

        for (int lvt_1_1_ = 0; lvt_1_1_ < this.shaderSamplerLocations.size(); ++lvt_1_1_)
        {
            if (this.shaderSamplers.get(this.samplerNames.get(lvt_1_1_)) != null)
            {
                GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit + lvt_1_1_);
                GlStateManager.enableTexture2D();
                Object lvt_2_1_ = this.shaderSamplers.get(this.samplerNames.get(lvt_1_1_));
                int lvt_3_1_ = -1;

                if (lvt_2_1_ instanceof Framebuffer)
                {
                    lvt_3_1_ = ((Framebuffer)lvt_2_1_).framebufferTexture;
                }
                else if (lvt_2_1_ instanceof ITextureObject)
                {
                    lvt_3_1_ = ((ITextureObject)lvt_2_1_).getGlTextureId();
                }
                else if (lvt_2_1_ instanceof Integer)
                {
                    lvt_3_1_ = ((Integer)lvt_2_1_).intValue();
                }

                if (lvt_3_1_ != -1)
                {
                    GlStateManager.bindTexture(lvt_3_1_);
                    OpenGlHelper.glUniform1i(OpenGlHelper.glGetUniformLocation(this.program, (CharSequence)this.samplerNames.get(lvt_1_1_)), lvt_1_1_);
                }
            }
        }

        for (ShaderUniform lvt_2_2_ : this.shaderUniforms)
        {
            lvt_2_2_.upload();
        }
    }

    public void markDirty()
    {
        this.isDirty = true;
    }

    /**
     * gets a shader uniform for the name given. null if not found.
     */
    public ShaderUniform getShaderUniform(String p_147991_1_)
    {
        return this.mappedShaderUniforms.containsKey(p_147991_1_) ? (ShaderUniform)this.mappedShaderUniforms.get(p_147991_1_) : null;
    }

    /**
     * gets a shader uniform for the name given. if not found, returns a default not-null value
     */
    public ShaderUniform getShaderUniformOrDefault(String p_147984_1_)
    {
        return (ShaderUniform)(this.mappedShaderUniforms.containsKey(p_147984_1_) ? (ShaderUniform)this.mappedShaderUniforms.get(p_147984_1_) : defaultShaderUniform);
    }

    /**
     * goes through the parsed uniforms and samplers and connects them to their GL counterparts.
     */
    private void setupUniforms()
    {
        int lvt_1_1_ = 0;

        for (int lvt_2_1_ = 0; lvt_1_1_ < this.samplerNames.size(); ++lvt_2_1_)
        {
            String lvt_3_1_ = (String)this.samplerNames.get(lvt_1_1_);
            int lvt_4_1_ = OpenGlHelper.glGetUniformLocation(this.program, lvt_3_1_);

            if (lvt_4_1_ == -1)
            {
                logger.warn("Shader " + this.programFilename + "could not find sampler named " + lvt_3_1_ + " in the specified shader program.");
                this.shaderSamplers.remove(lvt_3_1_);
                this.samplerNames.remove(lvt_2_1_);
                --lvt_2_1_;
            }
            else
            {
                this.shaderSamplerLocations.add(Integer.valueOf(lvt_4_1_));
            }

            ++lvt_1_1_;
        }

        for (ShaderUniform lvt_2_2_ : this.shaderUniforms)
        {
            String lvt_3_2_ = lvt_2_2_.getShaderName();
            int lvt_4_2_ = OpenGlHelper.glGetUniformLocation(this.program, lvt_3_2_);

            if (lvt_4_2_ == -1)
            {
                logger.warn("Could not find uniform named " + lvt_3_2_ + " in the specified" + " shader program.");
            }
            else
            {
                this.shaderUniformLocations.add(Integer.valueOf(lvt_4_2_));
                lvt_2_2_.setUniformLocation(lvt_4_2_);
                this.mappedShaderUniforms.put(lvt_3_2_, lvt_2_2_);
            }
        }
    }

    private void parseSampler(JsonElement p_147996_1_) throws JsonException
    {
        JsonObject lvt_2_1_ = JsonUtils.getJsonObject(p_147996_1_, "sampler");
        String lvt_3_1_ = JsonUtils.getString(lvt_2_1_, "name");

        if (!JsonUtils.isString(lvt_2_1_, "file"))
        {
            this.shaderSamplers.put(lvt_3_1_, (Object)null);
            this.samplerNames.add(lvt_3_1_);
        }
        else
        {
            this.samplerNames.add(lvt_3_1_);
        }
    }

    /**
     * adds a shader sampler texture. if it already exists, replaces it.
     */
    public void addSamplerTexture(String p_147992_1_, Object p_147992_2_)
    {
        if (this.shaderSamplers.containsKey(p_147992_1_))
        {
            this.shaderSamplers.remove(p_147992_1_);
        }

        this.shaderSamplers.put(p_147992_1_, p_147992_2_);
        this.markDirty();
    }

    private void parseUniform(JsonElement p_147987_1_) throws JsonException
    {
        JsonObject lvt_2_1_ = JsonUtils.getJsonObject(p_147987_1_, "uniform");
        String lvt_3_1_ = JsonUtils.getString(lvt_2_1_, "name");
        int lvt_4_1_ = ShaderUniform.parseType(JsonUtils.getString(lvt_2_1_, "type"));
        int lvt_5_1_ = JsonUtils.getInt(lvt_2_1_, "count");
        float[] lvt_6_1_ = new float[Math.max(lvt_5_1_, 16)];
        JsonArray lvt_7_1_ = JsonUtils.getJsonArray(lvt_2_1_, "values");

        if (lvt_7_1_.size() != lvt_5_1_ && lvt_7_1_.size() > 1)
        {
            throw new JsonException("Invalid amount of values specified (expected " + lvt_5_1_ + ", found " + lvt_7_1_.size() + ")");
        }
        else
        {
            int lvt_8_1_ = 0;

            for (JsonElement lvt_10_1_ : lvt_7_1_)
            {
                try
                {
                    lvt_6_1_[lvt_8_1_] = JsonUtils.getFloat(lvt_10_1_, "value");
                }
                catch (Exception var13)
                {
                    JsonException lvt_12_1_ = JsonException.func_151379_a(var13);
                    lvt_12_1_.func_151380_a("values[" + lvt_8_1_ + "]");
                    throw lvt_12_1_;
                }

                ++lvt_8_1_;
            }

            if (lvt_5_1_ > 1 && lvt_7_1_.size() == 1)
            {
                while (lvt_8_1_ < lvt_5_1_)
                {
                    lvt_6_1_[lvt_8_1_] = lvt_6_1_[0];
                    ++lvt_8_1_;
                }
            }

            int lvt_9_2_ = lvt_5_1_ > 1 && lvt_5_1_ <= 4 && lvt_4_1_ < 8 ? lvt_5_1_ - 1 : 0;
            ShaderUniform lvt_10_2_ = new ShaderUniform(lvt_3_1_, lvt_4_1_ + lvt_9_2_, lvt_5_1_, this);

            if (lvt_4_1_ <= 3)
            {
                lvt_10_2_.set((int)lvt_6_1_[0], (int)lvt_6_1_[1], (int)lvt_6_1_[2], (int)lvt_6_1_[3]);
            }
            else if (lvt_4_1_ <= 7)
            {
                lvt_10_2_.func_148092_b(lvt_6_1_[0], lvt_6_1_[1], lvt_6_1_[2], lvt_6_1_[3]);
            }
            else
            {
                lvt_10_2_.set(lvt_6_1_);
            }

            this.shaderUniforms.add(lvt_10_2_);
        }
    }

    public ShaderLoader getVertexShaderLoader()
    {
        return this.vertexShaderLoader;
    }

    public ShaderLoader getFragmentShaderLoader()
    {
        return this.fragmentShaderLoader;
    }

    public int getProgram()
    {
        return this.program;
    }
}
