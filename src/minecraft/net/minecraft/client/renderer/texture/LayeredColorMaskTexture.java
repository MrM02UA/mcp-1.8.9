package net.minecraft.client.renderer.texture;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LayeredColorMaskTexture extends AbstractTexture
{
    /** Access to the Logger, for all your logging needs. */
    private static final Logger LOG = LogManager.getLogger();

    /** The location of the texture. */
    private final ResourceLocation textureLocation;
    private final List<String> field_174949_h;
    private final List<EnumDyeColor> field_174950_i;

    public LayeredColorMaskTexture(ResourceLocation textureLocationIn, List<String> p_i46101_2_, List<EnumDyeColor> p_i46101_3_)
    {
        this.textureLocation = textureLocationIn;
        this.field_174949_h = p_i46101_2_;
        this.field_174950_i = p_i46101_3_;
    }

    public void loadTexture(IResourceManager resourceManager) throws IOException
    {
        this.deleteGlTexture();
        BufferedImage lvt_2_1_;

        try
        {
            BufferedImage lvt_3_1_ = TextureUtil.readBufferedImage(resourceManager.getResource(this.textureLocation).getInputStream());
            int lvt_4_1_ = lvt_3_1_.getType();

            if (lvt_4_1_ == 0)
            {
                lvt_4_1_ = 6;
            }

            lvt_2_1_ = new BufferedImage(lvt_3_1_.getWidth(), lvt_3_1_.getHeight(), lvt_4_1_);
            Graphics lvt_5_1_ = lvt_2_1_.getGraphics();
            lvt_5_1_.drawImage(lvt_3_1_, 0, 0, (ImageObserver)null);

            for (int lvt_6_1_ = 0; lvt_6_1_ < 17 && lvt_6_1_ < this.field_174949_h.size() && lvt_6_1_ < this.field_174950_i.size(); ++lvt_6_1_)
            {
                String lvt_7_1_ = (String)this.field_174949_h.get(lvt_6_1_);
                MapColor lvt_8_1_ = ((EnumDyeColor)this.field_174950_i.get(lvt_6_1_)).getMapColor();

                if (lvt_7_1_ != null)
                {
                    InputStream lvt_9_1_ = resourceManager.getResource(new ResourceLocation(lvt_7_1_)).getInputStream();
                    BufferedImage lvt_10_1_ = TextureUtil.readBufferedImage(lvt_9_1_);

                    if (lvt_10_1_.getWidth() == lvt_2_1_.getWidth() && lvt_10_1_.getHeight() == lvt_2_1_.getHeight() && lvt_10_1_.getType() == 6)
                    {
                        for (int lvt_11_1_ = 0; lvt_11_1_ < lvt_10_1_.getHeight(); ++lvt_11_1_)
                        {
                            for (int lvt_12_1_ = 0; lvt_12_1_ < lvt_10_1_.getWidth(); ++lvt_12_1_)
                            {
                                int lvt_13_1_ = lvt_10_1_.getRGB(lvt_12_1_, lvt_11_1_);

                                if ((lvt_13_1_ & -16777216) != 0)
                                {
                                    int lvt_14_1_ = (lvt_13_1_ & 16711680) << 8 & -16777216;
                                    int lvt_15_1_ = lvt_3_1_.getRGB(lvt_12_1_, lvt_11_1_);
                                    int lvt_16_1_ = MathHelper.func_180188_d(lvt_15_1_, lvt_8_1_.colorValue) & 16777215;
                                    lvt_10_1_.setRGB(lvt_12_1_, lvt_11_1_, lvt_14_1_ | lvt_16_1_);
                                }
                            }
                        }

                        lvt_2_1_.getGraphics().drawImage(lvt_10_1_, 0, 0, (ImageObserver)null);
                    }
                }
            }
        }
        catch (IOException var17)
        {
            LOG.error("Couldn\'t load layered image", var17);
            return;
        }

        TextureUtil.uploadTextureImage(this.getGlTextureId(), lvt_2_1_);
    }
}
