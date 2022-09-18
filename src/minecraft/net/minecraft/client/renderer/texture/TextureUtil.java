package net.minecraft.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL12;

public class TextureUtil
{
    private static final Logger logger = LogManager.getLogger();
    private static final IntBuffer dataBuffer = GLAllocation.createDirectIntBuffer(4194304);
    public static final DynamicTexture missingTexture = new DynamicTexture(16, 16);
    public static final int[] missingTextureData = missingTexture.getTextureData();
    private static final int[] mipmapBuffer;

    public static int glGenTextures()
    {
        return GlStateManager.generateTexture();
    }

    public static void deleteTexture(int textureId)
    {
        GlStateManager.deleteTexture(textureId);
    }

    public static int uploadTextureImage(int p_110987_0_, BufferedImage p_110987_1_)
    {
        return uploadTextureImageAllocate(p_110987_0_, p_110987_1_, false, false);
    }

    public static void uploadTexture(int textureId, int[] p_110988_1_, int p_110988_2_, int p_110988_3_)
    {
        bindTexture(textureId);
        uploadTextureSub(0, p_110988_1_, p_110988_2_, p_110988_3_, 0, 0, false, false, false);
    }

    public static int[][] generateMipmapData(int p_147949_0_, int p_147949_1_, int[][] p_147949_2_)
    {
        int[][] lvt_3_1_ = new int[p_147949_0_ + 1][];
        lvt_3_1_[0] = p_147949_2_[0];

        if (p_147949_0_ > 0)
        {
            boolean lvt_4_1_ = false;

            for (int lvt_5_1_ = 0; lvt_5_1_ < p_147949_2_.length; ++lvt_5_1_)
            {
                if (p_147949_2_[0][lvt_5_1_] >> 24 == 0)
                {
                    lvt_4_1_ = true;
                    break;
                }
            }

            for (int lvt_5_2_ = 1; lvt_5_2_ <= p_147949_0_; ++lvt_5_2_)
            {
                if (p_147949_2_[lvt_5_2_] != null)
                {
                    lvt_3_1_[lvt_5_2_] = p_147949_2_[lvt_5_2_];
                }
                else
                {
                    int[] lvt_6_1_ = lvt_3_1_[lvt_5_2_ - 1];
                    int[] lvt_7_1_ = new int[lvt_6_1_.length >> 2];
                    int lvt_8_1_ = p_147949_1_ >> lvt_5_2_;
                    int lvt_9_1_ = lvt_7_1_.length / lvt_8_1_;
                    int lvt_10_1_ = lvt_8_1_ << 1;

                    for (int lvt_11_1_ = 0; lvt_11_1_ < lvt_8_1_; ++lvt_11_1_)
                    {
                        for (int lvt_12_1_ = 0; lvt_12_1_ < lvt_9_1_; ++lvt_12_1_)
                        {
                            int lvt_13_1_ = 2 * (lvt_11_1_ + lvt_12_1_ * lvt_10_1_);
                            lvt_7_1_[lvt_11_1_ + lvt_12_1_ * lvt_8_1_] = blendColors(lvt_6_1_[lvt_13_1_ + 0], lvt_6_1_[lvt_13_1_ + 1], lvt_6_1_[lvt_13_1_ + 0 + lvt_10_1_], lvt_6_1_[lvt_13_1_ + 1 + lvt_10_1_], lvt_4_1_);
                        }
                    }

                    lvt_3_1_[lvt_5_2_] = lvt_7_1_;
                }
            }
        }

        return lvt_3_1_;
    }

    private static int blendColors(int p_147943_0_, int p_147943_1_, int p_147943_2_, int p_147943_3_, boolean p_147943_4_)
    {
        if (!p_147943_4_)
        {
            int lvt_5_1_ = blendColorComponent(p_147943_0_, p_147943_1_, p_147943_2_, p_147943_3_, 24);
            int lvt_6_1_ = blendColorComponent(p_147943_0_, p_147943_1_, p_147943_2_, p_147943_3_, 16);
            int lvt_7_1_ = blendColorComponent(p_147943_0_, p_147943_1_, p_147943_2_, p_147943_3_, 8);
            int lvt_8_1_ = blendColorComponent(p_147943_0_, p_147943_1_, p_147943_2_, p_147943_3_, 0);
            return lvt_5_1_ << 24 | lvt_6_1_ << 16 | lvt_7_1_ << 8 | lvt_8_1_;
        }
        else
        {
            mipmapBuffer[0] = p_147943_0_;
            mipmapBuffer[1] = p_147943_1_;
            mipmapBuffer[2] = p_147943_2_;
            mipmapBuffer[3] = p_147943_3_;
            float lvt_5_2_ = 0.0F;
            float lvt_6_2_ = 0.0F;
            float lvt_7_2_ = 0.0F;
            float lvt_8_2_ = 0.0F;

            for (int lvt_9_1_ = 0; lvt_9_1_ < 4; ++lvt_9_1_)
            {
                if (mipmapBuffer[lvt_9_1_] >> 24 != 0)
                {
                    lvt_5_2_ += (float)Math.pow((double)((float)(mipmapBuffer[lvt_9_1_] >> 24 & 255) / 255.0F), 2.2D);
                    lvt_6_2_ += (float)Math.pow((double)((float)(mipmapBuffer[lvt_9_1_] >> 16 & 255) / 255.0F), 2.2D);
                    lvt_7_2_ += (float)Math.pow((double)((float)(mipmapBuffer[lvt_9_1_] >> 8 & 255) / 255.0F), 2.2D);
                    lvt_8_2_ += (float)Math.pow((double)((float)(mipmapBuffer[lvt_9_1_] >> 0 & 255) / 255.0F), 2.2D);
                }
            }

            lvt_5_2_ = lvt_5_2_ / 4.0F;
            lvt_6_2_ = lvt_6_2_ / 4.0F;
            lvt_7_2_ = lvt_7_2_ / 4.0F;
            lvt_8_2_ = lvt_8_2_ / 4.0F;
            int lvt_9_2_ = (int)(Math.pow((double)lvt_5_2_, 0.45454545454545453D) * 255.0D);
            int lvt_10_1_ = (int)(Math.pow((double)lvt_6_2_, 0.45454545454545453D) * 255.0D);
            int lvt_11_1_ = (int)(Math.pow((double)lvt_7_2_, 0.45454545454545453D) * 255.0D);
            int lvt_12_1_ = (int)(Math.pow((double)lvt_8_2_, 0.45454545454545453D) * 255.0D);

            if (lvt_9_2_ < 96)
            {
                lvt_9_2_ = 0;
            }

            return lvt_9_2_ << 24 | lvt_10_1_ << 16 | lvt_11_1_ << 8 | lvt_12_1_;
        }
    }

    private static int blendColorComponent(int p_147944_0_, int p_147944_1_, int p_147944_2_, int p_147944_3_, int p_147944_4_)
    {
        float lvt_5_1_ = (float)Math.pow((double)((float)(p_147944_0_ >> p_147944_4_ & 255) / 255.0F), 2.2D);
        float lvt_6_1_ = (float)Math.pow((double)((float)(p_147944_1_ >> p_147944_4_ & 255) / 255.0F), 2.2D);
        float lvt_7_1_ = (float)Math.pow((double)((float)(p_147944_2_ >> p_147944_4_ & 255) / 255.0F), 2.2D);
        float lvt_8_1_ = (float)Math.pow((double)((float)(p_147944_3_ >> p_147944_4_ & 255) / 255.0F), 2.2D);
        float lvt_9_1_ = (float)Math.pow((double)(lvt_5_1_ + lvt_6_1_ + lvt_7_1_ + lvt_8_1_) * 0.25D, 0.45454545454545453D);
        return (int)((double)lvt_9_1_ * 255.0D);
    }

    public static void uploadTextureMipmap(int[][] p_147955_0_, int p_147955_1_, int p_147955_2_, int p_147955_3_, int p_147955_4_, boolean p_147955_5_, boolean p_147955_6_)
    {
        for (int lvt_7_1_ = 0; lvt_7_1_ < p_147955_0_.length; ++lvt_7_1_)
        {
            int[] lvt_8_1_ = p_147955_0_[lvt_7_1_];
            uploadTextureSub(lvt_7_1_, lvt_8_1_, p_147955_1_ >> lvt_7_1_, p_147955_2_ >> lvt_7_1_, p_147955_3_ >> lvt_7_1_, p_147955_4_ >> lvt_7_1_, p_147955_5_, p_147955_6_, p_147955_0_.length > 1);
        }
    }

    private static void uploadTextureSub(int p_147947_0_, int[] p_147947_1_, int p_147947_2_, int p_147947_3_, int p_147947_4_, int p_147947_5_, boolean p_147947_6_, boolean p_147947_7_, boolean p_147947_8_)
    {
        int lvt_9_1_ = 4194304 / p_147947_2_;
        setTextureBlurMipmap(p_147947_6_, p_147947_8_);
        setTextureClamped(p_147947_7_);
        int lvt_12_1_;

        for (int lvt_10_1_ = 0; lvt_10_1_ < p_147947_2_ * p_147947_3_; lvt_10_1_ += p_147947_2_ * lvt_12_1_)
        {
            int lvt_11_1_ = lvt_10_1_ / p_147947_2_;
            lvt_12_1_ = Math.min(lvt_9_1_, p_147947_3_ - lvt_11_1_);
            int lvt_13_1_ = p_147947_2_ * lvt_12_1_;
            copyToBufferPos(p_147947_1_, lvt_10_1_, lvt_13_1_);
            GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, p_147947_0_, p_147947_4_, p_147947_5_ + lvt_11_1_, p_147947_2_, lvt_12_1_, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, dataBuffer);
        }
    }

    public static int uploadTextureImageAllocate(int p_110989_0_, BufferedImage p_110989_1_, boolean p_110989_2_, boolean p_110989_3_)
    {
        allocateTexture(p_110989_0_, p_110989_1_.getWidth(), p_110989_1_.getHeight());
        return uploadTextureImageSub(p_110989_0_, p_110989_1_, 0, 0, p_110989_2_, p_110989_3_);
    }

    public static void allocateTexture(int p_110991_0_, int p_110991_1_, int p_110991_2_)
    {
        allocateTextureImpl(p_110991_0_, 0, p_110991_1_, p_110991_2_);
    }

    public static void allocateTextureImpl(int p_180600_0_, int p_180600_1_, int p_180600_2_, int p_180600_3_)
    {
        deleteTexture(p_180600_0_);
        bindTexture(p_180600_0_);

        if (p_180600_1_ >= 0)
        {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, p_180600_1_);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MIN_LOD, 0.0F);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LOD, (float)p_180600_1_);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0.0F);
        }

        for (int lvt_4_1_ = 0; lvt_4_1_ <= p_180600_1_; ++lvt_4_1_)
        {
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, lvt_4_1_, GL11.GL_RGBA, p_180600_2_ >> lvt_4_1_, p_180600_3_ >> lvt_4_1_, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (IntBuffer)null);
        }
    }

    public static int uploadTextureImageSub(int textureId, BufferedImage p_110995_1_, int p_110995_2_, int p_110995_3_, boolean p_110995_4_, boolean p_110995_5_)
    {
        bindTexture(textureId);
        uploadTextureImageSubImpl(p_110995_1_, p_110995_2_, p_110995_3_, p_110995_4_, p_110995_5_);
        return textureId;
    }

    private static void uploadTextureImageSubImpl(BufferedImage p_110993_0_, int p_110993_1_, int p_110993_2_, boolean p_110993_3_, boolean p_110993_4_)
    {
        int lvt_5_1_ = p_110993_0_.getWidth();
        int lvt_6_1_ = p_110993_0_.getHeight();
        int lvt_7_1_ = 4194304 / lvt_5_1_;
        int[] lvt_8_1_ = new int[lvt_7_1_ * lvt_5_1_];
        setTextureBlurred(p_110993_3_);
        setTextureClamped(p_110993_4_);

        for (int lvt_9_1_ = 0; lvt_9_1_ < lvt_5_1_ * lvt_6_1_; lvt_9_1_ += lvt_5_1_ * lvt_7_1_)
        {
            int lvt_10_1_ = lvt_9_1_ / lvt_5_1_;
            int lvt_11_1_ = Math.min(lvt_7_1_, lvt_6_1_ - lvt_10_1_);
            int lvt_12_1_ = lvt_5_1_ * lvt_11_1_;
            p_110993_0_.getRGB(0, lvt_10_1_, lvt_5_1_, lvt_11_1_, lvt_8_1_, 0, lvt_5_1_);
            copyToBuffer(lvt_8_1_, lvt_12_1_);
            GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, p_110993_1_, p_110993_2_ + lvt_10_1_, lvt_5_1_, lvt_11_1_, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, dataBuffer);
        }
    }

    private static void setTextureClamped(boolean p_110997_0_)
    {
        if (p_110997_0_)
        {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
        }
        else
        {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        }
    }

    private static void setTextureBlurred(boolean p_147951_0_)
    {
        setTextureBlurMipmap(p_147951_0_, false);
    }

    private static void setTextureBlurMipmap(boolean p_147954_0_, boolean p_147954_1_)
    {
        if (p_147954_0_)
        {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, p_147954_1_ ? GL11.GL_LINEAR_MIPMAP_LINEAR : GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        }
        else
        {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, p_147954_1_ ? GL11.GL_NEAREST_MIPMAP_LINEAR : GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        }
    }

    private static void copyToBuffer(int[] p_110990_0_, int p_110990_1_)
    {
        copyToBufferPos(p_110990_0_, 0, p_110990_1_);
    }

    private static void copyToBufferPos(int[] p_110994_0_, int p_110994_1_, int p_110994_2_)
    {
        int[] lvt_3_1_ = p_110994_0_;

        if (Minecraft.getMinecraft().gameSettings.anaglyph)
        {
            lvt_3_1_ = updateAnaglyph(p_110994_0_);
        }

        dataBuffer.clear();
        dataBuffer.put(lvt_3_1_, p_110994_1_, p_110994_2_);
        dataBuffer.position(0).limit(p_110994_2_);
    }

    static void bindTexture(int p_94277_0_)
    {
        GlStateManager.bindTexture(p_94277_0_);
    }

    public static int[] readImageData(IResourceManager resourceManager, ResourceLocation imageLocation) throws IOException
    {
        BufferedImage lvt_2_1_ = readBufferedImage(resourceManager.getResource(imageLocation).getInputStream());
        int lvt_3_1_ = lvt_2_1_.getWidth();
        int lvt_4_1_ = lvt_2_1_.getHeight();
        int[] lvt_5_1_ = new int[lvt_3_1_ * lvt_4_1_];
        lvt_2_1_.getRGB(0, 0, lvt_3_1_, lvt_4_1_, lvt_5_1_, 0, lvt_3_1_);
        return lvt_5_1_;
    }

    public static BufferedImage readBufferedImage(InputStream imageStream) throws IOException
    {
        BufferedImage var1;

        try
        {
            var1 = ImageIO.read(imageStream);
        }
        finally
        {
            IOUtils.closeQuietly(imageStream);
        }

        return var1;
    }

    public static int[] updateAnaglyph(int[] p_110985_0_)
    {
        int[] lvt_1_1_ = new int[p_110985_0_.length];

        for (int lvt_2_1_ = 0; lvt_2_1_ < p_110985_0_.length; ++lvt_2_1_)
        {
            lvt_1_1_[lvt_2_1_] = anaglyphColor(p_110985_0_[lvt_2_1_]);
        }

        return lvt_1_1_;
    }

    public static int anaglyphColor(int p_177054_0_)
    {
        int lvt_1_1_ = p_177054_0_ >> 24 & 255;
        int lvt_2_1_ = p_177054_0_ >> 16 & 255;
        int lvt_3_1_ = p_177054_0_ >> 8 & 255;
        int lvt_4_1_ = p_177054_0_ & 255;
        int lvt_5_1_ = (lvt_2_1_ * 30 + lvt_3_1_ * 59 + lvt_4_1_ * 11) / 100;
        int lvt_6_1_ = (lvt_2_1_ * 30 + lvt_3_1_ * 70) / 100;
        int lvt_7_1_ = (lvt_2_1_ * 30 + lvt_4_1_ * 70) / 100;
        return lvt_1_1_ << 24 | lvt_5_1_ << 16 | lvt_6_1_ << 8 | lvt_7_1_;
    }

    public static void processPixelValues(int[] p_147953_0_, int p_147953_1_, int p_147953_2_)
    {
        int[] lvt_3_1_ = new int[p_147953_1_];
        int lvt_4_1_ = p_147953_2_ / 2;

        for (int lvt_5_1_ = 0; lvt_5_1_ < lvt_4_1_; ++lvt_5_1_)
        {
            System.arraycopy(p_147953_0_, lvt_5_1_ * p_147953_1_, lvt_3_1_, 0, p_147953_1_);
            System.arraycopy(p_147953_0_, (p_147953_2_ - 1 - lvt_5_1_) * p_147953_1_, p_147953_0_, lvt_5_1_ * p_147953_1_, p_147953_1_);
            System.arraycopy(lvt_3_1_, 0, p_147953_0_, (p_147953_2_ - 1 - lvt_5_1_) * p_147953_1_, p_147953_1_);
        }
    }

    static
    {
        int lvt_0_1_ = -16777216;
        int lvt_1_1_ = -524040;
        int[] lvt_2_1_ = new int[] { -524040, -524040, -524040, -524040, -524040, -524040, -524040, -524040};
        int[] lvt_3_1_ = new int[] { -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216};
        int lvt_4_1_ = lvt_2_1_.length;

        for (int lvt_5_1_ = 0; lvt_5_1_ < 16; ++lvt_5_1_)
        {
            System.arraycopy(lvt_5_1_ < lvt_4_1_ ? lvt_2_1_ : lvt_3_1_, 0, missingTextureData, 16 * lvt_5_1_, lvt_4_1_);
            System.arraycopy(lvt_5_1_ < lvt_4_1_ ? lvt_3_1_ : lvt_2_1_, 0, missingTextureData, 16 * lvt_5_1_ + lvt_4_1_, lvt_4_1_);
        }

        missingTexture.updateDynamicTexture();
        mipmapBuffer = new int[4];
    }
}
