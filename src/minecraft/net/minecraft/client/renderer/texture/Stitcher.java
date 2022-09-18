package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.util.MathHelper;

public class Stitcher
{
    private final int mipmapLevelStitcher;
    private final Set<Stitcher.Holder> setStitchHolders = Sets.newHashSetWithExpectedSize(256);
    private final List<Stitcher.Slot> stitchSlots = Lists.newArrayListWithCapacity(256);
    private int currentWidth;
    private int currentHeight;
    private final int maxWidth;
    private final int maxHeight;
    private final boolean forcePowerOf2;

    /** Max size (width or height) of a single tile */
    private final int maxTileDimension;

    public Stitcher(int maxTextureWidth, int maxTextureHeight, boolean p_i45095_3_, int p_i45095_4_, int mipmapLevel)
    {
        this.mipmapLevelStitcher = mipmapLevel;
        this.maxWidth = maxTextureWidth;
        this.maxHeight = maxTextureHeight;
        this.forcePowerOf2 = p_i45095_3_;
        this.maxTileDimension = p_i45095_4_;
    }

    public int getCurrentWidth()
    {
        return this.currentWidth;
    }

    public int getCurrentHeight()
    {
        return this.currentHeight;
    }

    public void addSprite(TextureAtlasSprite p_110934_1_)
    {
        Stitcher.Holder lvt_2_1_ = new Stitcher.Holder(p_110934_1_, this.mipmapLevelStitcher);

        if (this.maxTileDimension > 0)
        {
            lvt_2_1_.setNewDimension(this.maxTileDimension);
        }

        this.setStitchHolders.add(lvt_2_1_);
    }

    public void doStitch()
    {
        Stitcher.Holder[] lvt_1_1_ = (Stitcher.Holder[])this.setStitchHolders.toArray(new Stitcher.Holder[this.setStitchHolders.size()]);
        Arrays.sort(lvt_1_1_);

        for (Stitcher.Holder lvt_5_1_ : lvt_1_1_)
        {
            if (!this.allocateSlot(lvt_5_1_))
            {
                String lvt_6_1_ = String.format("Unable to fit: %s - size: %dx%d - Maybe try a lowerresolution resourcepack?", new Object[] {lvt_5_1_.getAtlasSprite().getIconName(), Integer.valueOf(lvt_5_1_.getAtlasSprite().getIconWidth()), Integer.valueOf(lvt_5_1_.getAtlasSprite().getIconHeight())});
                throw new StitcherException(lvt_5_1_, lvt_6_1_);
            }
        }

        if (this.forcePowerOf2)
        {
            this.currentWidth = MathHelper.roundUpToPowerOfTwo(this.currentWidth);
            this.currentHeight = MathHelper.roundUpToPowerOfTwo(this.currentHeight);
        }
    }

    public List<TextureAtlasSprite> getStichSlots()
    {
        List<Stitcher.Slot> lvt_1_1_ = Lists.newArrayList();

        for (Stitcher.Slot lvt_3_1_ : this.stitchSlots)
        {
            lvt_3_1_.getAllStitchSlots(lvt_1_1_);
        }

        List<TextureAtlasSprite> lvt_2_2_ = Lists.newArrayList();

        for (Stitcher.Slot lvt_4_1_ : lvt_1_1_)
        {
            Stitcher.Holder lvt_5_1_ = lvt_4_1_.getStitchHolder();
            TextureAtlasSprite lvt_6_1_ = lvt_5_1_.getAtlasSprite();
            lvt_6_1_.initSprite(this.currentWidth, this.currentHeight, lvt_4_1_.getOriginX(), lvt_4_1_.getOriginY(), lvt_5_1_.isRotated());
            lvt_2_2_.add(lvt_6_1_);
        }

        return lvt_2_2_;
    }

    private static int getMipmapDimension(int p_147969_0_, int p_147969_1_)
    {
        return (p_147969_0_ >> p_147969_1_) + ((p_147969_0_ & (1 << p_147969_1_) - 1) == 0 ? 0 : 1) << p_147969_1_;
    }

    /**
     * Attempts to find space for specified tile
     */
    private boolean allocateSlot(Stitcher.Holder p_94310_1_)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < this.stitchSlots.size(); ++lvt_2_1_)
        {
            if (((Stitcher.Slot)this.stitchSlots.get(lvt_2_1_)).addSlot(p_94310_1_))
            {
                return true;
            }

            p_94310_1_.rotate();

            if (((Stitcher.Slot)this.stitchSlots.get(lvt_2_1_)).addSlot(p_94310_1_))
            {
                return true;
            }

            p_94310_1_.rotate();
        }

        return this.expandAndAllocateSlot(p_94310_1_);
    }

    /**
     * Expand stitched texture in order to make space for specified tile
     */
    private boolean expandAndAllocateSlot(Stitcher.Holder p_94311_1_)
    {
        int lvt_2_1_ = Math.min(p_94311_1_.getWidth(), p_94311_1_.getHeight());
        boolean lvt_3_1_ = this.currentWidth == 0 && this.currentHeight == 0;
        boolean lvt_4_1_;

        if (this.forcePowerOf2)
        {
            int lvt_5_1_ = MathHelper.roundUpToPowerOfTwo(this.currentWidth);
            int lvt_6_1_ = MathHelper.roundUpToPowerOfTwo(this.currentHeight);
            int lvt_7_1_ = MathHelper.roundUpToPowerOfTwo(this.currentWidth + lvt_2_1_);
            int lvt_8_1_ = MathHelper.roundUpToPowerOfTwo(this.currentHeight + lvt_2_1_);
            boolean lvt_9_1_ = lvt_7_1_ <= this.maxWidth;
            boolean lvt_10_1_ = lvt_8_1_ <= this.maxHeight;

            if (!lvt_9_1_ && !lvt_10_1_)
            {
                return false;
            }

            boolean lvt_11_1_ = lvt_5_1_ != lvt_7_1_;
            boolean lvt_12_1_ = lvt_6_1_ != lvt_8_1_;

            if (lvt_11_1_ ^ lvt_12_1_)
            {
                lvt_4_1_ = !lvt_11_1_;
            }
            else
            {
                lvt_4_1_ = lvt_9_1_ && lvt_5_1_ <= lvt_6_1_;
            }
        }
        else
        {
            boolean lvt_5_2_ = this.currentWidth + lvt_2_1_ <= this.maxWidth;
            boolean lvt_6_2_ = this.currentHeight + lvt_2_1_ <= this.maxHeight;

            if (!lvt_5_2_ && !lvt_6_2_)
            {
                return false;
            }

            lvt_4_1_ = lvt_5_2_ && (lvt_3_1_ || this.currentWidth <= this.currentHeight);
        }

        int lvt_5_3_ = Math.max(p_94311_1_.getWidth(), p_94311_1_.getHeight());

        if (MathHelper.roundUpToPowerOfTwo((lvt_4_1_ ? this.currentHeight : this.currentWidth) + lvt_5_3_) > (lvt_4_1_ ? this.maxHeight : this.maxWidth))
        {
            return false;
        }
        else
        {
            Stitcher.Slot lvt_6_3_;

            if (lvt_4_1_)
            {
                if (p_94311_1_.getWidth() > p_94311_1_.getHeight())
                {
                    p_94311_1_.rotate();
                }

                if (this.currentHeight == 0)
                {
                    this.currentHeight = p_94311_1_.getHeight();
                }

                lvt_6_3_ = new Stitcher.Slot(this.currentWidth, 0, p_94311_1_.getWidth(), this.currentHeight);
                this.currentWidth += p_94311_1_.getWidth();
            }
            else
            {
                lvt_6_3_ = new Stitcher.Slot(0, this.currentHeight, this.currentWidth, p_94311_1_.getHeight());
                this.currentHeight += p_94311_1_.getHeight();
            }

            lvt_6_3_.addSlot(p_94311_1_);
            this.stitchSlots.add(lvt_6_3_);
            return true;
        }
    }

    public static class Holder implements Comparable<Stitcher.Holder>
    {
        private final TextureAtlasSprite theTexture;
        private final int width;
        private final int height;
        private final int mipmapLevelHolder;
        private boolean rotated;
        private float scaleFactor = 1.0F;

        public Holder(TextureAtlasSprite p_i45094_1_, int p_i45094_2_)
        {
            this.theTexture = p_i45094_1_;
            this.width = p_i45094_1_.getIconWidth();
            this.height = p_i45094_1_.getIconHeight();
            this.mipmapLevelHolder = p_i45094_2_;
            this.rotated = Stitcher.getMipmapDimension(this.height, p_i45094_2_) > Stitcher.getMipmapDimension(this.width, p_i45094_2_);
        }

        public TextureAtlasSprite getAtlasSprite()
        {
            return this.theTexture;
        }

        public int getWidth()
        {
            return this.rotated ? Stitcher.getMipmapDimension((int)((float)this.height * this.scaleFactor), this.mipmapLevelHolder) : Stitcher.getMipmapDimension((int)((float)this.width * this.scaleFactor), this.mipmapLevelHolder);
        }

        public int getHeight()
        {
            return this.rotated ? Stitcher.getMipmapDimension((int)((float)this.width * this.scaleFactor), this.mipmapLevelHolder) : Stitcher.getMipmapDimension((int)((float)this.height * this.scaleFactor), this.mipmapLevelHolder);
        }

        public void rotate()
        {
            this.rotated = !this.rotated;
        }

        public boolean isRotated()
        {
            return this.rotated;
        }

        public void setNewDimension(int p_94196_1_)
        {
            if (this.width > p_94196_1_ && this.height > p_94196_1_)
            {
                this.scaleFactor = (float)p_94196_1_ / (float)Math.min(this.width, this.height);
            }
        }

        public String toString()
        {
            return "Holder{width=" + this.width + ", height=" + this.height + '}';
        }

        public int compareTo(Stitcher.Holder p_compareTo_1_)
        {
            int lvt_2_1_;

            if (this.getHeight() == p_compareTo_1_.getHeight())
            {
                if (this.getWidth() == p_compareTo_1_.getWidth())
                {
                    if (this.theTexture.getIconName() == null)
                    {
                        return p_compareTo_1_.theTexture.getIconName() == null ? 0 : -1;
                    }

                    return this.theTexture.getIconName().compareTo(p_compareTo_1_.theTexture.getIconName());
                }

                lvt_2_1_ = this.getWidth() < p_compareTo_1_.getWidth() ? 1 : -1;
            }
            else
            {
                lvt_2_1_ = this.getHeight() < p_compareTo_1_.getHeight() ? 1 : -1;
            }

            return lvt_2_1_;
        }

        public int compareTo(Object p_compareTo_1_)
        {
            return this.compareTo((Stitcher.Holder)p_compareTo_1_);
        }
    }

    public static class Slot
    {
        private final int originX;
        private final int originY;
        private final int width;
        private final int height;
        private List<Stitcher.Slot> subSlots;
        private Stitcher.Holder holder;

        public Slot(int p_i1277_1_, int p_i1277_2_, int widthIn, int heightIn)
        {
            this.originX = p_i1277_1_;
            this.originY = p_i1277_2_;
            this.width = widthIn;
            this.height = heightIn;
        }

        public Stitcher.Holder getStitchHolder()
        {
            return this.holder;
        }

        public int getOriginX()
        {
            return this.originX;
        }

        public int getOriginY()
        {
            return this.originY;
        }

        public boolean addSlot(Stitcher.Holder holderIn)
        {
            if (this.holder != null)
            {
                return false;
            }
            else
            {
                int lvt_2_1_ = holderIn.getWidth();
                int lvt_3_1_ = holderIn.getHeight();

                if (lvt_2_1_ <= this.width && lvt_3_1_ <= this.height)
                {
                    if (lvt_2_1_ == this.width && lvt_3_1_ == this.height)
                    {
                        this.holder = holderIn;
                        return true;
                    }
                    else
                    {
                        if (this.subSlots == null)
                        {
                            this.subSlots = Lists.newArrayListWithCapacity(1);
                            this.subSlots.add(new Stitcher.Slot(this.originX, this.originY, lvt_2_1_, lvt_3_1_));
                            int lvt_4_1_ = this.width - lvt_2_1_;
                            int lvt_5_1_ = this.height - lvt_3_1_;

                            if (lvt_5_1_ > 0 && lvt_4_1_ > 0)
                            {
                                int lvt_6_1_ = Math.max(this.height, lvt_4_1_);
                                int lvt_7_1_ = Math.max(this.width, lvt_5_1_);

                                if (lvt_6_1_ >= lvt_7_1_)
                                {
                                    this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + lvt_3_1_, lvt_2_1_, lvt_5_1_));
                                    this.subSlots.add(new Stitcher.Slot(this.originX + lvt_2_1_, this.originY, lvt_4_1_, this.height));
                                }
                                else
                                {
                                    this.subSlots.add(new Stitcher.Slot(this.originX + lvt_2_1_, this.originY, lvt_4_1_, lvt_3_1_));
                                    this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + lvt_3_1_, this.width, lvt_5_1_));
                                }
                            }
                            else if (lvt_4_1_ == 0)
                            {
                                this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + lvt_3_1_, lvt_2_1_, lvt_5_1_));
                            }
                            else if (lvt_5_1_ == 0)
                            {
                                this.subSlots.add(new Stitcher.Slot(this.originX + lvt_2_1_, this.originY, lvt_4_1_, lvt_3_1_));
                            }
                        }

                        for (Stitcher.Slot lvt_5_2_ : this.subSlots)
                        {
                            if (lvt_5_2_.addSlot(holderIn))
                            {
                                return true;
                            }
                        }

                        return false;
                    }
                }
                else
                {
                    return false;
                }
            }
        }

        public void getAllStitchSlots(List<Stitcher.Slot> p_94184_1_)
        {
            if (this.holder != null)
            {
                p_94184_1_.add(this);
            }
            else if (this.subSlots != null)
            {
                for (Stitcher.Slot lvt_3_1_ : this.subSlots)
                {
                    lvt_3_1_.getAllStitchSlots(p_94184_1_);
                }
            }
        }

        public String toString()
        {
            return "Slot{originX=" + this.originX + ", originY=" + this.originY + ", width=" + this.width + ", height=" + this.height + ", texture=" + this.holder + ", subSlots=" + this.subSlots + '}';
        }
    }
}
