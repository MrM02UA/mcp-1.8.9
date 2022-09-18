package net.minecraft.world.gen.layer;

public class GenLayerEdge extends GenLayer
{
    private final GenLayerEdge.Mode field_151627_c;

    public GenLayerEdge(long p_i45474_1_, GenLayer p_i45474_3_, GenLayerEdge.Mode p_i45474_4_)
    {
        super(p_i45474_1_);
        this.parent = p_i45474_3_;
        this.field_151627_c = p_i45474_4_;
    }

    /**
     * Returns a list of integer values generated by this layer. These may be interpreted as temperatures, rainfall
     * amounts, or biomeList[] indices based on the particular GenLayer subclass.
     */
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight)
    {
        switch (this.field_151627_c)
        {
            case COOL_WARM:
            default:
                return this.getIntsCoolWarm(areaX, areaY, areaWidth, areaHeight);

            case HEAT_ICE:
                return this.getIntsHeatIce(areaX, areaY, areaWidth, areaHeight);

            case SPECIAL:
                return this.getIntsSpecial(areaX, areaY, areaWidth, areaHeight);
        }
    }

    private int[] getIntsCoolWarm(int p_151626_1_, int p_151626_2_, int p_151626_3_, int p_151626_4_)
    {
        int lvt_5_1_ = p_151626_1_ - 1;
        int lvt_6_1_ = p_151626_2_ - 1;
        int lvt_7_1_ = 1 + p_151626_3_ + 1;
        int lvt_8_1_ = 1 + p_151626_4_ + 1;
        int[] lvt_9_1_ = this.parent.getInts(lvt_5_1_, lvt_6_1_, lvt_7_1_, lvt_8_1_);
        int[] lvt_10_1_ = IntCache.getIntCache(p_151626_3_ * p_151626_4_);

        for (int lvt_11_1_ = 0; lvt_11_1_ < p_151626_4_; ++lvt_11_1_)
        {
            for (int lvt_12_1_ = 0; lvt_12_1_ < p_151626_3_; ++lvt_12_1_)
            {
                this.initChunkSeed((long)(lvt_12_1_ + p_151626_1_), (long)(lvt_11_1_ + p_151626_2_));
                int lvt_13_1_ = lvt_9_1_[lvt_12_1_ + 1 + (lvt_11_1_ + 1) * lvt_7_1_];

                if (lvt_13_1_ == 1)
                {
                    int lvt_14_1_ = lvt_9_1_[lvt_12_1_ + 1 + (lvt_11_1_ + 1 - 1) * lvt_7_1_];
                    int lvt_15_1_ = lvt_9_1_[lvt_12_1_ + 1 + 1 + (lvt_11_1_ + 1) * lvt_7_1_];
                    int lvt_16_1_ = lvt_9_1_[lvt_12_1_ + 1 - 1 + (lvt_11_1_ + 1) * lvt_7_1_];
                    int lvt_17_1_ = lvt_9_1_[lvt_12_1_ + 1 + (lvt_11_1_ + 1 + 1) * lvt_7_1_];
                    boolean lvt_18_1_ = lvt_14_1_ == 3 || lvt_15_1_ == 3 || lvt_16_1_ == 3 || lvt_17_1_ == 3;
                    boolean lvt_19_1_ = lvt_14_1_ == 4 || lvt_15_1_ == 4 || lvt_16_1_ == 4 || lvt_17_1_ == 4;

                    if (lvt_18_1_ || lvt_19_1_)
                    {
                        lvt_13_1_ = 2;
                    }
                }

                lvt_10_1_[lvt_12_1_ + lvt_11_1_ * p_151626_3_] = lvt_13_1_;
            }
        }

        return lvt_10_1_;
    }

    private int[] getIntsHeatIce(int p_151624_1_, int p_151624_2_, int p_151624_3_, int p_151624_4_)
    {
        int lvt_5_1_ = p_151624_1_ - 1;
        int lvt_6_1_ = p_151624_2_ - 1;
        int lvt_7_1_ = 1 + p_151624_3_ + 1;
        int lvt_8_1_ = 1 + p_151624_4_ + 1;
        int[] lvt_9_1_ = this.parent.getInts(lvt_5_1_, lvt_6_1_, lvt_7_1_, lvt_8_1_);
        int[] lvt_10_1_ = IntCache.getIntCache(p_151624_3_ * p_151624_4_);

        for (int lvt_11_1_ = 0; lvt_11_1_ < p_151624_4_; ++lvt_11_1_)
        {
            for (int lvt_12_1_ = 0; lvt_12_1_ < p_151624_3_; ++lvt_12_1_)
            {
                int lvt_13_1_ = lvt_9_1_[lvt_12_1_ + 1 + (lvt_11_1_ + 1) * lvt_7_1_];

                if (lvt_13_1_ == 4)
                {
                    int lvt_14_1_ = lvt_9_1_[lvt_12_1_ + 1 + (lvt_11_1_ + 1 - 1) * lvt_7_1_];
                    int lvt_15_1_ = lvt_9_1_[lvt_12_1_ + 1 + 1 + (lvt_11_1_ + 1) * lvt_7_1_];
                    int lvt_16_1_ = lvt_9_1_[lvt_12_1_ + 1 - 1 + (lvt_11_1_ + 1) * lvt_7_1_];
                    int lvt_17_1_ = lvt_9_1_[lvt_12_1_ + 1 + (lvt_11_1_ + 1 + 1) * lvt_7_1_];
                    boolean lvt_18_1_ = lvt_14_1_ == 2 || lvt_15_1_ == 2 || lvt_16_1_ == 2 || lvt_17_1_ == 2;
                    boolean lvt_19_1_ = lvt_14_1_ == 1 || lvt_15_1_ == 1 || lvt_16_1_ == 1 || lvt_17_1_ == 1;

                    if (lvt_19_1_ || lvt_18_1_)
                    {
                        lvt_13_1_ = 3;
                    }
                }

                lvt_10_1_[lvt_12_1_ + lvt_11_1_ * p_151624_3_] = lvt_13_1_;
            }
        }

        return lvt_10_1_;
    }

    private int[] getIntsSpecial(int p_151625_1_, int p_151625_2_, int p_151625_3_, int p_151625_4_)
    {
        int[] lvt_5_1_ = this.parent.getInts(p_151625_1_, p_151625_2_, p_151625_3_, p_151625_4_);
        int[] lvt_6_1_ = IntCache.getIntCache(p_151625_3_ * p_151625_4_);

        for (int lvt_7_1_ = 0; lvt_7_1_ < p_151625_4_; ++lvt_7_1_)
        {
            for (int lvt_8_1_ = 0; lvt_8_1_ < p_151625_3_; ++lvt_8_1_)
            {
                this.initChunkSeed((long)(lvt_8_1_ + p_151625_1_), (long)(lvt_7_1_ + p_151625_2_));
                int lvt_9_1_ = lvt_5_1_[lvt_8_1_ + lvt_7_1_ * p_151625_3_];

                if (lvt_9_1_ != 0 && this.nextInt(13) == 0)
                {
                    lvt_9_1_ |= 1 + this.nextInt(15) << 8 & 3840;
                }

                lvt_6_1_[lvt_8_1_ + lvt_7_1_ * p_151625_3_] = lvt_9_1_;
            }
        }

        return lvt_6_1_;
    }

    public static enum Mode
    {
        COOL_WARM,
        HEAT_ICE,
        SPECIAL;
    }
}