package net.minecraft.client.renderer;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;

public class ImageBufferDownload implements IImageBuffer
{
    private int[] imageData;
    private int imageWidth;
    private int imageHeight;

    public BufferedImage parseUserSkin(BufferedImage image)
    {
        if (image == null)
        {
            return null;
        }
        else
        {
            this.imageWidth = 64;
            this.imageHeight = 64;
            BufferedImage lvt_2_1_ = new BufferedImage(this.imageWidth, this.imageHeight, 2);
            Graphics lvt_3_1_ = lvt_2_1_.getGraphics();
            lvt_3_1_.drawImage(image, 0, 0, (ImageObserver)null);

            if (image.getHeight() == 32)
            {
                lvt_3_1_.drawImage(lvt_2_1_, 24, 48, 20, 52, 4, 16, 8, 20, (ImageObserver)null);
                lvt_3_1_.drawImage(lvt_2_1_, 28, 48, 24, 52, 8, 16, 12, 20, (ImageObserver)null);
                lvt_3_1_.drawImage(lvt_2_1_, 20, 52, 16, 64, 8, 20, 12, 32, (ImageObserver)null);
                lvt_3_1_.drawImage(lvt_2_1_, 24, 52, 20, 64, 4, 20, 8, 32, (ImageObserver)null);
                lvt_3_1_.drawImage(lvt_2_1_, 28, 52, 24, 64, 0, 20, 4, 32, (ImageObserver)null);
                lvt_3_1_.drawImage(lvt_2_1_, 32, 52, 28, 64, 12, 20, 16, 32, (ImageObserver)null);
                lvt_3_1_.drawImage(lvt_2_1_, 40, 48, 36, 52, 44, 16, 48, 20, (ImageObserver)null);
                lvt_3_1_.drawImage(lvt_2_1_, 44, 48, 40, 52, 48, 16, 52, 20, (ImageObserver)null);
                lvt_3_1_.drawImage(lvt_2_1_, 36, 52, 32, 64, 48, 20, 52, 32, (ImageObserver)null);
                lvt_3_1_.drawImage(lvt_2_1_, 40, 52, 36, 64, 44, 20, 48, 32, (ImageObserver)null);
                lvt_3_1_.drawImage(lvt_2_1_, 44, 52, 40, 64, 40, 20, 44, 32, (ImageObserver)null);
                lvt_3_1_.drawImage(lvt_2_1_, 48, 52, 44, 64, 52, 20, 56, 32, (ImageObserver)null);
            }

            lvt_3_1_.dispose();
            this.imageData = ((DataBufferInt)lvt_2_1_.getRaster().getDataBuffer()).getData();
            this.setAreaOpaque(0, 0, 32, 16);
            this.setAreaTransparent(32, 0, 64, 32);
            this.setAreaOpaque(0, 16, 64, 32);
            this.setAreaTransparent(0, 32, 16, 48);
            this.setAreaTransparent(16, 32, 40, 48);
            this.setAreaTransparent(40, 32, 56, 48);
            this.setAreaTransparent(0, 48, 16, 64);
            this.setAreaOpaque(16, 48, 48, 64);
            this.setAreaTransparent(48, 48, 64, 64);
            return lvt_2_1_;
        }
    }

    public void skinAvailable()
    {
    }

    /**
     * Makes the given area of the image transparent if it was previously completely opaque (used to remove the outer
     * layer of a skin around the head if it was saved all opaque; this would be redundant so it's assumed that the skin
     * maker is just using an image editor without an alpha channel)
     */
    private void setAreaTransparent(int p_78434_1_, int p_78434_2_, int p_78434_3_, int p_78434_4_)
    {
        if (!this.hasTransparency(p_78434_1_, p_78434_2_, p_78434_3_, p_78434_4_))
        {
            for (int lvt_5_1_ = p_78434_1_; lvt_5_1_ < p_78434_3_; ++lvt_5_1_)
            {
                for (int lvt_6_1_ = p_78434_2_; lvt_6_1_ < p_78434_4_; ++lvt_6_1_)
                {
                    this.imageData[lvt_5_1_ + lvt_6_1_ * this.imageWidth] &= 16777215;
                }
            }
        }
    }

    /**
     * Makes the given area of the image opaque
     */
    private void setAreaOpaque(int p_78433_1_, int p_78433_2_, int p_78433_3_, int p_78433_4_)
    {
        for (int lvt_5_1_ = p_78433_1_; lvt_5_1_ < p_78433_3_; ++lvt_5_1_)
        {
            for (int lvt_6_1_ = p_78433_2_; lvt_6_1_ < p_78433_4_; ++lvt_6_1_)
            {
                this.imageData[lvt_5_1_ + lvt_6_1_ * this.imageWidth] |= -16777216;
            }
        }
    }

    /**
     * Returns true if the given area of the image contains transparent pixels
     */
    private boolean hasTransparency(int p_78435_1_, int p_78435_2_, int p_78435_3_, int p_78435_4_)
    {
        for (int lvt_5_1_ = p_78435_1_; lvt_5_1_ < p_78435_3_; ++lvt_5_1_)
        {
            for (int lvt_6_1_ = p_78435_2_; lvt_6_1_ < p_78435_4_; ++lvt_6_1_)
            {
                int lvt_7_1_ = this.imageData[lvt_5_1_ + lvt_6_1_ * this.imageWidth];

                if ((lvt_7_1_ >> 24 & 255) < 128)
                {
                    return true;
                }
            }
        }

        return false;
    }
}
