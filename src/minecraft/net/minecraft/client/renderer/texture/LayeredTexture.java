package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LayeredTexture extends AbstractTexture
{
    private static final Logger logger = LogManager.getLogger();
    public final List<String> layeredTextureNames;

    public LayeredTexture(String... textureNames)
    {
        this.layeredTextureNames = Lists.newArrayList(textureNames);
    }

    public void loadTexture(IResourceManager resourceManager) throws IOException
    {
        this.deleteGlTexture();
        BufferedImage lvt_2_1_ = null;

        try
        {
            for (String lvt_4_1_ : this.layeredTextureNames)
            {
                if (lvt_4_1_ != null)
                {
                    InputStream lvt_5_1_ = resourceManager.getResource(new ResourceLocation(lvt_4_1_)).getInputStream();
                    BufferedImage lvt_6_1_ = TextureUtil.readBufferedImage(lvt_5_1_);

                    if (lvt_2_1_ == null)
                    {
                        lvt_2_1_ = new BufferedImage(lvt_6_1_.getWidth(), lvt_6_1_.getHeight(), 2);
                    }

                    lvt_2_1_.getGraphics().drawImage(lvt_6_1_, 0, 0, (ImageObserver)null);
                }
            }
        }
        catch (IOException var7)
        {
            logger.error("Couldn\'t load layered image", var7);
            return;
        }

        TextureUtil.uploadTextureImage(this.getGlTextureId(), lvt_2_1_);
    }
}
