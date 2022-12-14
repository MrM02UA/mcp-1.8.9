package net.minecraft.world.gen.layer;

public class GenLayerAddIsland extends GenLayer
{
    public GenLayerAddIsland(long p_i2119_1_, GenLayer p_i2119_3_)
    {
        super(p_i2119_1_);
        this.parent = p_i2119_3_;
    }

    /**
     * Returns a list of integer values generated by this layer. These may be interpreted as temperatures, rainfall
     * amounts, or biomeList[] indices based on the particular GenLayer subclass.
     */
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight)
    {
        int lvt_5_1_ = areaX - 1;
        int lvt_6_1_ = areaY - 1;
        int lvt_7_1_ = areaWidth + 2;
        int lvt_8_1_ = areaHeight + 2;
        int[] lvt_9_1_ = this.parent.getInts(lvt_5_1_, lvt_6_1_, lvt_7_1_, lvt_8_1_);
        int[] lvt_10_1_ = IntCache.getIntCache(areaWidth * areaHeight);

        for (int lvt_11_1_ = 0; lvt_11_1_ < areaHeight; ++lvt_11_1_)
        {
            for (int lvt_12_1_ = 0; lvt_12_1_ < areaWidth; ++lvt_12_1_)
            {
                int lvt_13_1_ = lvt_9_1_[lvt_12_1_ + 0 + (lvt_11_1_ + 0) * lvt_7_1_];
                int lvt_14_1_ = lvt_9_1_[lvt_12_1_ + 2 + (lvt_11_1_ + 0) * lvt_7_1_];
                int lvt_15_1_ = lvt_9_1_[lvt_12_1_ + 0 + (lvt_11_1_ + 2) * lvt_7_1_];
                int lvt_16_1_ = lvt_9_1_[lvt_12_1_ + 2 + (lvt_11_1_ + 2) * lvt_7_1_];
                int lvt_17_1_ = lvt_9_1_[lvt_12_1_ + 1 + (lvt_11_1_ + 1) * lvt_7_1_];
                this.initChunkSeed((long)(lvt_12_1_ + areaX), (long)(lvt_11_1_ + areaY));

                if (lvt_17_1_ != 0 || lvt_13_1_ == 0 && lvt_14_1_ == 0 && lvt_15_1_ == 0 && lvt_16_1_ == 0)
                {
                    if (lvt_17_1_ > 0 && (lvt_13_1_ == 0 || lvt_14_1_ == 0 || lvt_15_1_ == 0 || lvt_16_1_ == 0))
                    {
                        if (this.nextInt(5) == 0)
                        {
                            if (lvt_17_1_ == 4)
                            {
                                lvt_10_1_[lvt_12_1_ + lvt_11_1_ * areaWidth] = 4;
                            }
                            else
                            {
                                lvt_10_1_[lvt_12_1_ + lvt_11_1_ * areaWidth] = 0;
                            }
                        }
                        else
                        {
                            lvt_10_1_[lvt_12_1_ + lvt_11_1_ * areaWidth] = lvt_17_1_;
                        }
                    }
                    else
                    {
                        lvt_10_1_[lvt_12_1_ + lvt_11_1_ * areaWidth] = lvt_17_1_;
                    }
                }
                else
                {
                    int lvt_18_1_ = 1;
                    int lvt_19_1_ = 1;

                    if (lvt_13_1_ != 0 && this.nextInt(lvt_18_1_++) == 0)
                    {
                        lvt_19_1_ = lvt_13_1_;
                    }

                    if (lvt_14_1_ != 0 && this.nextInt(lvt_18_1_++) == 0)
                    {
                        lvt_19_1_ = lvt_14_1_;
                    }

                    if (lvt_15_1_ != 0 && this.nextInt(lvt_18_1_++) == 0)
                    {
                        lvt_19_1_ = lvt_15_1_;
                    }

                    if (lvt_16_1_ != 0 && this.nextInt(lvt_18_1_++) == 0)
                    {
                        lvt_19_1_ = lvt_16_1_;
                    }

                    if (this.nextInt(3) == 0)
                    {
                        lvt_10_1_[lvt_12_1_ + lvt_11_1_ * areaWidth] = lvt_19_1_;
                    }
                    else if (lvt_19_1_ == 4)
                    {
                        lvt_10_1_[lvt_12_1_ + lvt_11_1_ * areaWidth] = 4;
                    }
                    else
                    {
                        lvt_10_1_[lvt_12_1_ + lvt_11_1_ * areaWidth] = 0;
                    }
                }
            }
        }

        return lvt_10_1_;
    }
}
