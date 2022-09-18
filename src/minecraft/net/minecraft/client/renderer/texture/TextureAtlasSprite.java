package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;

public class TextureAtlasSprite
{
    private final String iconName;
    protected List<int[][]> framesTextureData = Lists.newArrayList();
    protected int[][] interpolatedFrameData;
    private AnimationMetadataSection animationMetadata;
    protected boolean rotated;
    protected int originX;
    protected int originY;
    protected int width;
    protected int height;
    private float minU;
    private float maxU;
    private float minV;
    private float maxV;
    protected int frameCounter;
    protected int tickCounter;
    private static String locationNameClock = "builtin/clock";
    private static String locationNameCompass = "builtin/compass";

    protected TextureAtlasSprite(String spriteName)
    {
        this.iconName = spriteName;
    }

    protected static TextureAtlasSprite makeAtlasSprite(ResourceLocation spriteResourceLocation)
    {
        String lvt_1_1_ = spriteResourceLocation.toString();
        return (TextureAtlasSprite)(locationNameClock.equals(lvt_1_1_) ? new TextureClock(lvt_1_1_) : (locationNameCompass.equals(lvt_1_1_) ? new TextureCompass(lvt_1_1_) : new TextureAtlasSprite(lvt_1_1_)));
    }

    public static void setLocationNameClock(String clockName)
    {
        locationNameClock = clockName;
    }

    public static void setLocationNameCompass(String compassName)
    {
        locationNameCompass = compassName;
    }

    public void initSprite(int inX, int inY, int originInX, int originInY, boolean rotatedIn)
    {
        this.originX = originInX;
        this.originY = originInY;
        this.rotated = rotatedIn;
        float lvt_6_1_ = (float)(0.009999999776482582D / (double)inX);
        float lvt_7_1_ = (float)(0.009999999776482582D / (double)inY);
        this.minU = (float)originInX / (float)((double)inX) + lvt_6_1_;
        this.maxU = (float)(originInX + this.width) / (float)((double)inX) - lvt_6_1_;
        this.minV = (float)originInY / (float)inY + lvt_7_1_;
        this.maxV = (float)(originInY + this.height) / (float)inY - lvt_7_1_;
    }

    public void copyFrom(TextureAtlasSprite atlasSpirit)
    {
        this.originX = atlasSpirit.originX;
        this.originY = atlasSpirit.originY;
        this.width = atlasSpirit.width;
        this.height = atlasSpirit.height;
        this.rotated = atlasSpirit.rotated;
        this.minU = atlasSpirit.minU;
        this.maxU = atlasSpirit.maxU;
        this.minV = atlasSpirit.minV;
        this.maxV = atlasSpirit.maxV;
    }

    /**
     * Returns the X position of this icon on its texture sheet, in pixels.
     */
    public int getOriginX()
    {
        return this.originX;
    }

    /**
     * Returns the Y position of this icon on its texture sheet, in pixels.
     */
    public int getOriginY()
    {
        return this.originY;
    }

    /**
     * Returns the width of the icon, in pixels.
     */
    public int getIconWidth()
    {
        return this.width;
    }

    /**
     * Returns the height of the icon, in pixels.
     */
    public int getIconHeight()
    {
        return this.height;
    }

    /**
     * Returns the minimum U coordinate to use when rendering with this icon.
     */
    public float getMinU()
    {
        return this.minU;
    }

    /**
     * Returns the maximum U coordinate to use when rendering with this icon.
     */
    public float getMaxU()
    {
        return this.maxU;
    }

    /**
     * Gets a U coordinate on the icon. 0 returns uMin and 16 returns uMax. Other arguments return in-between values.
     */
    public float getInterpolatedU(double u)
    {
        float lvt_3_1_ = this.maxU - this.minU;
        return this.minU + lvt_3_1_ * (float)u / 16.0F;
    }

    /**
     * Returns the minimum V coordinate to use when rendering with this icon.
     */
    public float getMinV()
    {
        return this.minV;
    }

    /**
     * Returns the maximum V coordinate to use when rendering with this icon.
     */
    public float getMaxV()
    {
        return this.maxV;
    }

    /**
     * Gets a V coordinate on the icon. 0 returns vMin and 16 returns vMax. Other arguments return in-between values.
     */
    public float getInterpolatedV(double v)
    {
        float lvt_3_1_ = this.maxV - this.minV;
        return this.minV + lvt_3_1_ * ((float)v / 16.0F);
    }

    public String getIconName()
    {
        return this.iconName;
    }

    public void updateAnimation()
    {
        ++this.tickCounter;

        if (this.tickCounter >= this.animationMetadata.getFrameTimeSingle(this.frameCounter))
        {
            int lvt_1_1_ = this.animationMetadata.getFrameIndex(this.frameCounter);
            int lvt_2_1_ = this.animationMetadata.getFrameCount() == 0 ? this.framesTextureData.size() : this.animationMetadata.getFrameCount();
            this.frameCounter = (this.frameCounter + 1) % lvt_2_1_;
            this.tickCounter = 0;
            int lvt_3_1_ = this.animationMetadata.getFrameIndex(this.frameCounter);

            if (lvt_1_1_ != lvt_3_1_ && lvt_3_1_ >= 0 && lvt_3_1_ < this.framesTextureData.size())
            {
                TextureUtil.uploadTextureMipmap((int[][])this.framesTextureData.get(lvt_3_1_), this.width, this.height, this.originX, this.originY, false, false);
            }
        }
        else if (this.animationMetadata.isInterpolate())
        {
            this.updateAnimationInterpolated();
        }
    }

    private void updateAnimationInterpolated()
    {
        double lvt_1_1_ = 1.0D - (double)this.tickCounter / (double)this.animationMetadata.getFrameTimeSingle(this.frameCounter);
        int lvt_3_1_ = this.animationMetadata.getFrameIndex(this.frameCounter);
        int lvt_4_1_ = this.animationMetadata.getFrameCount() == 0 ? this.framesTextureData.size() : this.animationMetadata.getFrameCount();
        int lvt_5_1_ = this.animationMetadata.getFrameIndex((this.frameCounter + 1) % lvt_4_1_);

        if (lvt_3_1_ != lvt_5_1_ && lvt_5_1_ >= 0 && lvt_5_1_ < this.framesTextureData.size())
        {
            int[][] lvt_6_1_ = (int[][])this.framesTextureData.get(lvt_3_1_);
            int[][] lvt_7_1_ = (int[][])this.framesTextureData.get(lvt_5_1_);

            if (this.interpolatedFrameData == null || this.interpolatedFrameData.length != lvt_6_1_.length)
            {
                this.interpolatedFrameData = new int[lvt_6_1_.length][];
            }

            for (int lvt_8_1_ = 0; lvt_8_1_ < lvt_6_1_.length; ++lvt_8_1_)
            {
                if (this.interpolatedFrameData[lvt_8_1_] == null)
                {
                    this.interpolatedFrameData[lvt_8_1_] = new int[lvt_6_1_[lvt_8_1_].length];
                }

                if (lvt_8_1_ < lvt_7_1_.length && lvt_7_1_[lvt_8_1_].length == lvt_6_1_[lvt_8_1_].length)
                {
                    for (int lvt_9_1_ = 0; lvt_9_1_ < lvt_6_1_[lvt_8_1_].length; ++lvt_9_1_)
                    {
                        int lvt_10_1_ = lvt_6_1_[lvt_8_1_][lvt_9_1_];
                        int lvt_11_1_ = lvt_7_1_[lvt_8_1_][lvt_9_1_];
                        int lvt_12_1_ = (int)((double)((lvt_10_1_ & 16711680) >> 16) * lvt_1_1_ + (double)((lvt_11_1_ & 16711680) >> 16) * (1.0D - lvt_1_1_));
                        int lvt_13_1_ = (int)((double)((lvt_10_1_ & 65280) >> 8) * lvt_1_1_ + (double)((lvt_11_1_ & 65280) >> 8) * (1.0D - lvt_1_1_));
                        int lvt_14_1_ = (int)((double)(lvt_10_1_ & 255) * lvt_1_1_ + (double)(lvt_11_1_ & 255) * (1.0D - lvt_1_1_));
                        this.interpolatedFrameData[lvt_8_1_][lvt_9_1_] = lvt_10_1_ & -16777216 | lvt_12_1_ << 16 | lvt_13_1_ << 8 | lvt_14_1_;
                    }
                }
            }

            TextureUtil.uploadTextureMipmap(this.interpolatedFrameData, this.width, this.height, this.originX, this.originY, false, false);
        }
    }

    public int[][] getFrameTextureData(int index)
    {
        return (int[][])this.framesTextureData.get(index);
    }

    public int getFrameCount()
    {
        return this.framesTextureData.size();
    }

    public void setIconWidth(int newWidth)
    {
        this.width = newWidth;
    }

    public void setIconHeight(int newHeight)
    {
        this.height = newHeight;
    }

    public void loadSprite(BufferedImage[] images, AnimationMetadataSection meta) throws IOException
    {
        this.resetSprite();
        int lvt_3_1_ = images[0].getWidth();
        int lvt_4_1_ = images[0].getHeight();
        this.width = lvt_3_1_;
        this.height = lvt_4_1_;
        int[][] lvt_5_1_ = new int[images.length][];

        for (int lvt_6_1_ = 0; lvt_6_1_ < images.length; ++lvt_6_1_)
        {
            BufferedImage lvt_7_1_ = images[lvt_6_1_];

            if (lvt_7_1_ != null)
            {
                if (lvt_6_1_ > 0 && (lvt_7_1_.getWidth() != lvt_3_1_ >> lvt_6_1_ || lvt_7_1_.getHeight() != lvt_4_1_ >> lvt_6_1_))
                {
                    throw new RuntimeException(String.format("Unable to load miplevel: %d, image is size: %dx%d, expected %dx%d", new Object[] {Integer.valueOf(lvt_6_1_), Integer.valueOf(lvt_7_1_.getWidth()), Integer.valueOf(lvt_7_1_.getHeight()), Integer.valueOf(lvt_3_1_ >> lvt_6_1_), Integer.valueOf(lvt_4_1_ >> lvt_6_1_)}));
                }

                lvt_5_1_[lvt_6_1_] = new int[lvt_7_1_.getWidth() * lvt_7_1_.getHeight()];
                lvt_7_1_.getRGB(0, 0, lvt_7_1_.getWidth(), lvt_7_1_.getHeight(), lvt_5_1_[lvt_6_1_], 0, lvt_7_1_.getWidth());
            }
        }

        if (meta == null)
        {
            if (lvt_4_1_ != lvt_3_1_)
            {
                throw new RuntimeException("broken aspect ratio and not an animation");
            }

            this.framesTextureData.add(lvt_5_1_);
        }
        else
        {
            int lvt_6_2_ = lvt_4_1_ / lvt_3_1_;
            int lvt_7_2_ = lvt_3_1_;
            int lvt_8_1_ = lvt_3_1_;
            this.height = this.width;

            if (meta.getFrameCount() > 0)
            {
                Iterator lvt_9_1_ = meta.getFrameIndexSet().iterator();

                while (lvt_9_1_.hasNext())
                {
                    int lvt_10_1_ = ((Integer)lvt_9_1_.next()).intValue();

                    if (lvt_10_1_ >= lvt_6_2_)
                    {
                        throw new RuntimeException("invalid frameindex " + lvt_10_1_);
                    }

                    this.allocateFrameTextureData(lvt_10_1_);
                    this.framesTextureData.set(lvt_10_1_, getFrameTextureData(lvt_5_1_, lvt_7_2_, lvt_8_1_, lvt_10_1_));
                }

                this.animationMetadata = meta;
            }
            else
            {
                List<AnimationFrame> lvt_9_2_ = Lists.newArrayList();

                for (int lvt_10_2_ = 0; lvt_10_2_ < lvt_6_2_; ++lvt_10_2_)
                {
                    this.framesTextureData.add(getFrameTextureData(lvt_5_1_, lvt_7_2_, lvt_8_1_, lvt_10_2_));
                    lvt_9_2_.add(new AnimationFrame(lvt_10_2_, -1));
                }

                this.animationMetadata = new AnimationMetadataSection(lvt_9_2_, this.width, this.height, meta.getFrameTime(), meta.isInterpolate());
            }
        }
    }

    public void generateMipmaps(int level)
    {
        List<int[][]> lvt_2_1_ = Lists.newArrayList();

        for (int lvt_3_1_ = 0; lvt_3_1_ < this.framesTextureData.size(); ++lvt_3_1_)
        {
            final int[][] lvt_4_1_ = (int[][])this.framesTextureData.get(lvt_3_1_);

            if (lvt_4_1_ != null)
            {
                try
                {
                    lvt_2_1_.add(TextureUtil.generateMipmapData(level, this.width, lvt_4_1_));
                }
                catch (Throwable var8)
                {
                    CrashReport lvt_6_1_ = CrashReport.makeCrashReport(var8, "Generating mipmaps for frame");
                    CrashReportCategory lvt_7_1_ = lvt_6_1_.makeCategory("Frame being iterated");
                    lvt_7_1_.addCrashSection("Frame index", Integer.valueOf(lvt_3_1_));
                    lvt_7_1_.addCrashSectionCallable("Frame sizes", new Callable<String>()
                    {
                        public String call() throws Exception
                        {
                            StringBuilder lvt_1_1_ = new StringBuilder();

                            for (int[] lvt_5_1_ : lvt_4_1_)
                            {
                                if (lvt_1_1_.length() > 0)
                                {
                                    lvt_1_1_.append(", ");
                                }

                                lvt_1_1_.append(lvt_5_1_ == null ? "null" : Integer.valueOf(lvt_5_1_.length));
                            }

                            return lvt_1_1_.toString();
                        }
                        public Object call() throws Exception
                        {
                            return this.call();
                        }
                    });
                    throw new ReportedException(lvt_6_1_);
                }
            }
        }

        this.setFramesTextureData(lvt_2_1_);
    }

    private void allocateFrameTextureData(int index)
    {
        if (this.framesTextureData.size() <= index)
        {
            for (int lvt_2_1_ = this.framesTextureData.size(); lvt_2_1_ <= index; ++lvt_2_1_)
            {
                this.framesTextureData.add((Object)null);
            }
        }
    }

    private static int[][] getFrameTextureData(int[][] data, int rows, int columns, int p_147962_3_)
    {
        int[][] lvt_4_1_ = new int[data.length][];

        for (int lvt_5_1_ = 0; lvt_5_1_ < data.length; ++lvt_5_1_)
        {
            int[] lvt_6_1_ = data[lvt_5_1_];

            if (lvt_6_1_ != null)
            {
                lvt_4_1_[lvt_5_1_] = new int[(rows >> lvt_5_1_) * (columns >> lvt_5_1_)];
                System.arraycopy(lvt_6_1_, p_147962_3_ * lvt_4_1_[lvt_5_1_].length, lvt_4_1_[lvt_5_1_], 0, lvt_4_1_[lvt_5_1_].length);
            }
        }

        return lvt_4_1_;
    }

    public void clearFramesTextureData()
    {
        this.framesTextureData.clear();
    }

    public boolean hasAnimationMetadata()
    {
        return this.animationMetadata != null;
    }

    public void setFramesTextureData(List<int[][]> newFramesTextureData)
    {
        this.framesTextureData = newFramesTextureData;
    }

    private void resetSprite()
    {
        this.animationMetadata = null;
        this.setFramesTextureData(Lists.newArrayList());
        this.frameCounter = 0;
        this.tickCounter = 0;
    }

    public String toString()
    {
        return "TextureAtlasSprite{name=\'" + this.iconName + '\'' + ", frameCount=" + this.framesTextureData.size() + ", rotated=" + this.rotated + ", x=" + this.originX + ", y=" + this.originY + ", height=" + this.height + ", width=" + this.width + ", u0=" + this.minU + ", u1=" + this.maxU + ", v0=" + this.minV + ", v1=" + this.maxV + '}';
    }
}
