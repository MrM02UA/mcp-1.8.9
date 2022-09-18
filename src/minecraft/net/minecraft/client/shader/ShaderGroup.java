package net.minecraft.client.shader;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

public class ShaderGroup
{
    private Framebuffer mainFramebuffer;
    private IResourceManager resourceManager;
    private String shaderGroupName;
    private final List<Shader> listShaders = Lists.newArrayList();
    private final Map<String, Framebuffer> mapFramebuffers = Maps.newHashMap();
    private final List<Framebuffer> listFramebuffers = Lists.newArrayList();
    private Matrix4f projectionMatrix;
    private int mainFramebufferWidth;
    private int mainFramebufferHeight;
    private float field_148036_j;
    private float field_148037_k;

    public ShaderGroup(TextureManager p_i1050_1_, IResourceManager p_i1050_2_, Framebuffer p_i1050_3_, ResourceLocation p_i1050_4_) throws JsonException, IOException, JsonSyntaxException
    {
        this.resourceManager = p_i1050_2_;
        this.mainFramebuffer = p_i1050_3_;
        this.field_148036_j = 0.0F;
        this.field_148037_k = 0.0F;
        this.mainFramebufferWidth = p_i1050_3_.framebufferWidth;
        this.mainFramebufferHeight = p_i1050_3_.framebufferHeight;
        this.shaderGroupName = p_i1050_4_.toString();
        this.resetProjectionMatrix();
        this.parseGroup(p_i1050_1_, p_i1050_4_);
    }

    public void parseGroup(TextureManager p_152765_1_, ResourceLocation p_152765_2_) throws JsonException, IOException, JsonSyntaxException
    {
        JsonParser lvt_3_1_ = new JsonParser();
        InputStream lvt_4_1_ = null;

        try
        {
            IResource lvt_5_1_ = this.resourceManager.getResource(p_152765_2_);
            lvt_4_1_ = lvt_5_1_.getInputStream();
            JsonObject lvt_6_1_ = lvt_3_1_.parse(IOUtils.toString(lvt_4_1_, Charsets.UTF_8)).getAsJsonObject();

            if (JsonUtils.isJsonArray(lvt_6_1_, "targets"))
            {
                JsonArray lvt_7_1_ = lvt_6_1_.getAsJsonArray("targets");
                int lvt_8_1_ = 0;

                for (JsonElement lvt_10_1_ : lvt_7_1_)
                {
                    try
                    {
                        this.initTarget(lvt_10_1_);
                    }
                    catch (Exception var19)
                    {
                        JsonException lvt_12_1_ = JsonException.func_151379_a(var19);
                        lvt_12_1_.func_151380_a("targets[" + lvt_8_1_ + "]");
                        throw lvt_12_1_;
                    }

                    ++lvt_8_1_;
                }
            }

            if (JsonUtils.isJsonArray(lvt_6_1_, "passes"))
            {
                JsonArray lvt_7_2_ = lvt_6_1_.getAsJsonArray("passes");
                int lvt_8_2_ = 0;

                for (JsonElement lvt_10_2_ : lvt_7_2_)
                {
                    try
                    {
                        this.parsePass(p_152765_1_, lvt_10_2_);
                    }
                    catch (Exception var18)
                    {
                        JsonException lvt_12_2_ = JsonException.func_151379_a(var18);
                        lvt_12_2_.func_151380_a("passes[" + lvt_8_2_ + "]");
                        throw lvt_12_2_;
                    }

                    ++lvt_8_2_;
                }
            }
        }
        catch (Exception var20)
        {
            JsonException lvt_6_2_ = JsonException.func_151379_a(var20);
            lvt_6_2_.func_151381_b(p_152765_2_.getResourcePath());
            throw lvt_6_2_;
        }
        finally
        {
            IOUtils.closeQuietly(lvt_4_1_);
        }
    }

    private void initTarget(JsonElement p_148027_1_) throws JsonException
    {
        if (JsonUtils.isString(p_148027_1_))
        {
            this.addFramebuffer(p_148027_1_.getAsString(), this.mainFramebufferWidth, this.mainFramebufferHeight);
        }
        else
        {
            JsonObject lvt_2_1_ = JsonUtils.getJsonObject(p_148027_1_, "target");
            String lvt_3_1_ = JsonUtils.getString(lvt_2_1_, "name");
            int lvt_4_1_ = JsonUtils.getInt(lvt_2_1_, "width", this.mainFramebufferWidth);
            int lvt_5_1_ = JsonUtils.getInt(lvt_2_1_, "height", this.mainFramebufferHeight);

            if (this.mapFramebuffers.containsKey(lvt_3_1_))
            {
                throw new JsonException(lvt_3_1_ + " is already defined");
            }

            this.addFramebuffer(lvt_3_1_, lvt_4_1_, lvt_5_1_);
        }
    }

    private void parsePass(TextureManager p_152764_1_, JsonElement p_152764_2_) throws JsonException, IOException
    {
        JsonObject lvt_3_1_ = JsonUtils.getJsonObject(p_152764_2_, "pass");
        String lvt_4_1_ = JsonUtils.getString(lvt_3_1_, "name");
        String lvt_5_1_ = JsonUtils.getString(lvt_3_1_, "intarget");
        String lvt_6_1_ = JsonUtils.getString(lvt_3_1_, "outtarget");
        Framebuffer lvt_7_1_ = this.getFramebuffer(lvt_5_1_);
        Framebuffer lvt_8_1_ = this.getFramebuffer(lvt_6_1_);

        if (lvt_7_1_ == null)
        {
            throw new JsonException("Input target \'" + lvt_5_1_ + "\' does not exist");
        }
        else if (lvt_8_1_ == null)
        {
            throw new JsonException("Output target \'" + lvt_6_1_ + "\' does not exist");
        }
        else
        {
            Shader lvt_9_1_ = this.addShader(lvt_4_1_, lvt_7_1_, lvt_8_1_);
            JsonArray lvt_10_1_ = JsonUtils.getJsonArray(lvt_3_1_, "auxtargets", (JsonArray)null);

            if (lvt_10_1_ != null)
            {
                int lvt_11_1_ = 0;

                for (JsonElement lvt_13_1_ : lvt_10_1_)
                {
                    try
                    {
                        JsonObject lvt_14_1_ = JsonUtils.getJsonObject(lvt_13_1_, "auxtarget");
                        String lvt_15_1_ = JsonUtils.getString(lvt_14_1_, "name");
                        String lvt_16_1_ = JsonUtils.getString(lvt_14_1_, "id");
                        Framebuffer lvt_17_1_ = this.getFramebuffer(lvt_16_1_);

                        if (lvt_17_1_ == null)
                        {
                            ResourceLocation lvt_18_1_ = new ResourceLocation("textures/effect/" + lvt_16_1_ + ".png");

                            try
                            {
                                this.resourceManager.getResource(lvt_18_1_);
                            }
                            catch (FileNotFoundException var24)
                            {
                                throw new JsonException("Render target or texture \'" + lvt_16_1_ + "\' does not exist");
                            }

                            p_152764_1_.bindTexture(lvt_18_1_);
                            ITextureObject lvt_19_2_ = p_152764_1_.getTexture(lvt_18_1_);
                            int lvt_20_1_ = JsonUtils.getInt(lvt_14_1_, "width");
                            int lvt_21_1_ = JsonUtils.getInt(lvt_14_1_, "height");
                            boolean lvt_22_1_ = JsonUtils.getBoolean(lvt_14_1_, "bilinear");

                            if (lvt_22_1_)
                            {
                                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                            }
                            else
                            {
                                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                            }

                            lvt_9_1_.addAuxFramebuffer(lvt_15_1_, Integer.valueOf(lvt_19_2_.getGlTextureId()), lvt_20_1_, lvt_21_1_);
                        }
                        else
                        {
                            lvt_9_1_.addAuxFramebuffer(lvt_15_1_, lvt_17_1_, lvt_17_1_.framebufferTextureWidth, lvt_17_1_.framebufferTextureHeight);
                        }
                    }
                    catch (Exception var25)
                    {
                        JsonException lvt_15_2_ = JsonException.func_151379_a(var25);
                        lvt_15_2_.func_151380_a("auxtargets[" + lvt_11_1_ + "]");
                        throw lvt_15_2_;
                    }

                    ++lvt_11_1_;
                }
            }

            JsonArray lvt_11_2_ = JsonUtils.getJsonArray(lvt_3_1_, "uniforms", (JsonArray)null);

            if (lvt_11_2_ != null)
            {
                int lvt_12_2_ = 0;

                for (JsonElement lvt_14_3_ : lvt_11_2_)
                {
                    try
                    {
                        this.initUniform(lvt_14_3_);
                    }
                    catch (Exception var23)
                    {
                        JsonException lvt_16_2_ = JsonException.func_151379_a(var23);
                        lvt_16_2_.func_151380_a("uniforms[" + lvt_12_2_ + "]");
                        throw lvt_16_2_;
                    }

                    ++lvt_12_2_;
                }
            }
        }
    }

    private void initUniform(JsonElement p_148028_1_) throws JsonException
    {
        JsonObject lvt_2_1_ = JsonUtils.getJsonObject(p_148028_1_, "uniform");
        String lvt_3_1_ = JsonUtils.getString(lvt_2_1_, "name");
        ShaderUniform lvt_4_1_ = ((Shader)this.listShaders.get(this.listShaders.size() - 1)).getShaderManager().getShaderUniform(lvt_3_1_);

        if (lvt_4_1_ == null)
        {
            throw new JsonException("Uniform \'" + lvt_3_1_ + "\' does not exist");
        }
        else
        {
            float[] lvt_5_1_ = new float[4];
            int lvt_6_1_ = 0;

            for (JsonElement lvt_9_1_ : JsonUtils.getJsonArray(lvt_2_1_, "values"))
            {
                try
                {
                    lvt_5_1_[lvt_6_1_] = JsonUtils.getFloat(lvt_9_1_, "value");
                }
                catch (Exception var12)
                {
                    JsonException lvt_11_1_ = JsonException.func_151379_a(var12);
                    lvt_11_1_.func_151380_a("values[" + lvt_6_1_ + "]");
                    throw lvt_11_1_;
                }

                ++lvt_6_1_;
            }

            switch (lvt_6_1_)
            {
                case 0:
                default:
                    break;

                case 1:
                    lvt_4_1_.set(lvt_5_1_[0]);
                    break;

                case 2:
                    lvt_4_1_.set(lvt_5_1_[0], lvt_5_1_[1]);
                    break;

                case 3:
                    lvt_4_1_.set(lvt_5_1_[0], lvt_5_1_[1], lvt_5_1_[2]);
                    break;

                case 4:
                    lvt_4_1_.set(lvt_5_1_[0], lvt_5_1_[1], lvt_5_1_[2], lvt_5_1_[3]);
            }
        }
    }

    public Framebuffer getFramebufferRaw(String p_177066_1_)
    {
        return (Framebuffer)this.mapFramebuffers.get(p_177066_1_);
    }

    public void addFramebuffer(String p_148020_1_, int p_148020_2_, int p_148020_3_)
    {
        Framebuffer lvt_4_1_ = new Framebuffer(p_148020_2_, p_148020_3_, true);
        lvt_4_1_.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
        this.mapFramebuffers.put(p_148020_1_, lvt_4_1_);

        if (p_148020_2_ == this.mainFramebufferWidth && p_148020_3_ == this.mainFramebufferHeight)
        {
            this.listFramebuffers.add(lvt_4_1_);
        }
    }

    public void deleteShaderGroup()
    {
        for (Framebuffer lvt_2_1_ : this.mapFramebuffers.values())
        {
            lvt_2_1_.deleteFramebuffer();
        }

        for (Shader lvt_2_2_ : this.listShaders)
        {
            lvt_2_2_.deleteShader();
        }

        this.listShaders.clear();
    }

    public Shader addShader(String p_148023_1_, Framebuffer p_148023_2_, Framebuffer p_148023_3_) throws JsonException, IOException
    {
        Shader lvt_4_1_ = new Shader(this.resourceManager, p_148023_1_, p_148023_2_, p_148023_3_);
        this.listShaders.add(this.listShaders.size(), lvt_4_1_);
        return lvt_4_1_;
    }

    private void resetProjectionMatrix()
    {
        this.projectionMatrix = new Matrix4f();
        this.projectionMatrix.setIdentity();
        this.projectionMatrix.m00 = 2.0F / (float)this.mainFramebuffer.framebufferTextureWidth;
        this.projectionMatrix.m11 = 2.0F / (float)(-this.mainFramebuffer.framebufferTextureHeight);
        this.projectionMatrix.m22 = -0.0020001999F;
        this.projectionMatrix.m33 = 1.0F;
        this.projectionMatrix.m03 = -1.0F;
        this.projectionMatrix.m13 = 1.0F;
        this.projectionMatrix.m23 = -1.0001999F;
    }

    public void createBindFramebuffers(int width, int height)
    {
        this.mainFramebufferWidth = this.mainFramebuffer.framebufferTextureWidth;
        this.mainFramebufferHeight = this.mainFramebuffer.framebufferTextureHeight;
        this.resetProjectionMatrix();

        for (Shader lvt_4_1_ : this.listShaders)
        {
            lvt_4_1_.setProjectionMatrix(this.projectionMatrix);
        }

        for (Framebuffer lvt_4_2_ : this.listFramebuffers)
        {
            lvt_4_2_.createBindFramebuffer(width, height);
        }
    }

    public void loadShaderGroup(float partialTicks)
    {
        if (partialTicks < this.field_148037_k)
        {
            this.field_148036_j += 1.0F - this.field_148037_k;
            this.field_148036_j += partialTicks;
        }
        else
        {
            this.field_148036_j += partialTicks - this.field_148037_k;
        }

        for (this.field_148037_k = partialTicks; this.field_148036_j > 20.0F; this.field_148036_j -= 20.0F)
        {
            ;
        }

        for (Shader lvt_3_1_ : this.listShaders)
        {
            lvt_3_1_.loadShader(this.field_148036_j / 20.0F);
        }
    }

    public final String getShaderGroupName()
    {
        return this.shaderGroupName;
    }

    private Framebuffer getFramebuffer(String p_148017_1_)
    {
        return p_148017_1_ == null ? null : (p_148017_1_.equals("minecraft:main") ? this.mainFramebuffer : (Framebuffer)this.mapFramebuffers.get(p_148017_1_));
    }
}
