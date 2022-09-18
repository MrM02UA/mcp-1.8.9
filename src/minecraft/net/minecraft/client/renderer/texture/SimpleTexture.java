package net.minecraft.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleTexture extends AbstractTexture
{
    private static final Logger logger = LogManager.getLogger();
    protected final ResourceLocation textureLocation;

    public SimpleTexture(ResourceLocation textureResourceLocation)
    {
        this.textureLocation = textureResourceLocation;
    }

    public void loadTexture(IResourceManager resourceManager) throws IOException
    {
        this.deleteGlTexture();
        InputStream lvt_2_1_ = null;

        try
        {
            IResource lvt_3_1_ = resourceManager.getResource(this.textureLocation);
            lvt_2_1_ = lvt_3_1_.getInputStream();
            BufferedImage lvt_4_1_ = TextureUtil.readBufferedImage(lvt_2_1_);
            boolean lvt_5_1_ = false;
            boolean lvt_6_1_ = false;

            if (lvt_3_1_.hasMetadata())
            {
                try
                {
                    TextureMetadataSection lvt_7_1_ = (TextureMetadataSection)lvt_3_1_.getMetadata("texture");

                    if (lvt_7_1_ != null)
                    {
                        lvt_5_1_ = lvt_7_1_.getTextureBlur();
                        lvt_6_1_ = lvt_7_1_.getTextureClamp();
                    }
                }
                catch (RuntimeException var11)
                {
                    logger.warn("Failed reading metadata of: " + this.textureLocation, var11);
                }
            }

            TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), lvt_4_1_, lvt_5_1_, lvt_6_1_);
        }
        finally
        {
            if (lvt_2_1_ != null)
            {
                lvt_2_1_.close();
            }
        }
    }
}
