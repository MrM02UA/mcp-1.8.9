package net.minecraft.world.gen;

import java.util.Random;

public class NoiseGeneratorImproved extends NoiseGenerator
{
    /**
     * An int[512], where the first 256 elements are the numbers 0..255, in random shuffled order,
     * and the second half of the array is identical to the first half, apparently for convenience in wrapping lookups.
     *  
     * Effectively a shuffled 0..255 that wraps once.
     */
    private int[] permutations;
    public double xCoord;
    public double yCoord;
    public double zCoord;
    private static final double[] field_152381_e = new double[] {1.0D, -1.0D, 1.0D, -1.0D, 1.0D, -1.0D, 1.0D, -1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, -1.0D, 0.0D};
    private static final double[] field_152382_f = new double[] {1.0D, 1.0D, -1.0D, -1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, -1.0D, 1.0D, -1.0D, 1.0D, -1.0D, 1.0D, -1.0D};
    private static final double[] field_152383_g = new double[] {0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, -1.0D, -1.0D, 1.0D, 1.0D, -1.0D, -1.0D, 0.0D, 1.0D, 0.0D, -1.0D};
    private static final double[] field_152384_h = new double[] {1.0D, -1.0D, 1.0D, -1.0D, 1.0D, -1.0D, 1.0D, -1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, -1.0D, 0.0D};
    private static final double[] field_152385_i = new double[] {0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, -1.0D, -1.0D, 1.0D, 1.0D, -1.0D, -1.0D, 0.0D, 1.0D, 0.0D, -1.0D};

    public NoiseGeneratorImproved()
    {
        this(new Random());
    }

    public NoiseGeneratorImproved(Random p_i45469_1_)
    {
        this.permutations = new int[512];
        this.xCoord = p_i45469_1_.nextDouble() * 256.0D;
        this.yCoord = p_i45469_1_.nextDouble() * 256.0D;
        this.zCoord = p_i45469_1_.nextDouble() * 256.0D;

        for (int lvt_2_1_ = 0; lvt_2_1_ < 256; this.permutations[lvt_2_1_] = lvt_2_1_++)
        {
            ;
        }

        for (int lvt_2_2_ = 0; lvt_2_2_ < 256; ++lvt_2_2_)
        {
            int lvt_3_1_ = p_i45469_1_.nextInt(256 - lvt_2_2_) + lvt_2_2_;
            int lvt_4_1_ = this.permutations[lvt_2_2_];
            this.permutations[lvt_2_2_] = this.permutations[lvt_3_1_];
            this.permutations[lvt_3_1_] = lvt_4_1_;
            this.permutations[lvt_2_2_ + 256] = this.permutations[lvt_2_2_];
        }
    }

    public final double lerp(double p_76311_1_, double p_76311_3_, double p_76311_5_)
    {
        return p_76311_3_ + p_76311_1_ * (p_76311_5_ - p_76311_3_);
    }

    public final double func_76309_a(int p_76309_1_, double p_76309_2_, double p_76309_4_)
    {
        int lvt_6_1_ = p_76309_1_ & 15;
        return field_152384_h[lvt_6_1_] * p_76309_2_ + field_152385_i[lvt_6_1_] * p_76309_4_;
    }

    public final double grad(int p_76310_1_, double p_76310_2_, double p_76310_4_, double p_76310_6_)
    {
        int lvt_8_1_ = p_76310_1_ & 15;
        return field_152381_e[lvt_8_1_] * p_76310_2_ + field_152382_f[lvt_8_1_] * p_76310_4_ + field_152383_g[lvt_8_1_] * p_76310_6_;
    }

    /**
     * noiseArray should be xSize*ySize*zSize in size
     */
    public void populateNoiseArray(double[] noiseArray, double xOffset, double yOffset, double zOffset, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale, double noiseScale)
    {
        if (ySize == 1)
        {
            int lvt_19_1_ = 0;
            int lvt_20_1_ = 0;
            int lvt_21_1_ = 0;
            int lvt_22_1_ = 0;
            double lvt_23_1_ = 0.0D;
            double lvt_25_1_ = 0.0D;
            int lvt_27_1_ = 0;
            double lvt_28_1_ = 1.0D / noiseScale;

            for (int lvt_30_1_ = 0; lvt_30_1_ < xSize; ++lvt_30_1_)
            {
                double lvt_31_1_ = xOffset + (double)lvt_30_1_ * xScale + this.xCoord;
                int lvt_33_1_ = (int)lvt_31_1_;

                if (lvt_31_1_ < (double)lvt_33_1_)
                {
                    --lvt_33_1_;
                }

                int lvt_34_1_ = lvt_33_1_ & 255;
                lvt_31_1_ = lvt_31_1_ - (double)lvt_33_1_;
                double lvt_35_1_ = lvt_31_1_ * lvt_31_1_ * lvt_31_1_ * (lvt_31_1_ * (lvt_31_1_ * 6.0D - 15.0D) + 10.0D);

                for (int lvt_37_1_ = 0; lvt_37_1_ < zSize; ++lvt_37_1_)
                {
                    double lvt_38_1_ = zOffset + (double)lvt_37_1_ * zScale + this.zCoord;
                    int lvt_40_1_ = (int)lvt_38_1_;

                    if (lvt_38_1_ < (double)lvt_40_1_)
                    {
                        --lvt_40_1_;
                    }

                    int lvt_41_1_ = lvt_40_1_ & 255;
                    lvt_38_1_ = lvt_38_1_ - (double)lvt_40_1_;
                    double lvt_42_1_ = lvt_38_1_ * lvt_38_1_ * lvt_38_1_ * (lvt_38_1_ * (lvt_38_1_ * 6.0D - 15.0D) + 10.0D);
                    lvt_19_1_ = this.permutations[lvt_34_1_] + 0;
                    lvt_20_1_ = this.permutations[lvt_19_1_] + lvt_41_1_;
                    lvt_21_1_ = this.permutations[lvt_34_1_ + 1] + 0;
                    lvt_22_1_ = this.permutations[lvt_21_1_] + lvt_41_1_;
                    lvt_23_1_ = this.lerp(lvt_35_1_, this.func_76309_a(this.permutations[lvt_20_1_], lvt_31_1_, lvt_38_1_), this.grad(this.permutations[lvt_22_1_], lvt_31_1_ - 1.0D, 0.0D, lvt_38_1_));
                    lvt_25_1_ = this.lerp(lvt_35_1_, this.grad(this.permutations[lvt_20_1_ + 1], lvt_31_1_, 0.0D, lvt_38_1_ - 1.0D), this.grad(this.permutations[lvt_22_1_ + 1], lvt_31_1_ - 1.0D, 0.0D, lvt_38_1_ - 1.0D));
                    double lvt_44_1_ = this.lerp(lvt_42_1_, lvt_23_1_, lvt_25_1_);
                    int var97 = lvt_27_1_++;
                    noiseArray[var97] += lvt_44_1_ * lvt_28_1_;
                }
            }
        }
        else
        {
            int lvt_19_2_ = 0;
            double lvt_20_2_ = 1.0D / noiseScale;
            int lvt_22_2_ = -1;
            int lvt_23_2_ = 0;
            int lvt_24_1_ = 0;
            int lvt_25_2_ = 0;
            int lvt_26_1_ = 0;
            int lvt_27_2_ = 0;
            int lvt_28_2_ = 0;
            double lvt_29_1_ = 0.0D;
            double lvt_31_2_ = 0.0D;
            double lvt_33_2_ = 0.0D;
            double lvt_35_2_ = 0.0D;

            for (int lvt_37_2_ = 0; lvt_37_2_ < xSize; ++lvt_37_2_)
            {
                double lvt_38_2_ = xOffset + (double)lvt_37_2_ * xScale + this.xCoord;
                int lvt_40_2_ = (int)lvt_38_2_;

                if (lvt_38_2_ < (double)lvt_40_2_)
                {
                    --lvt_40_2_;
                }

                int lvt_41_2_ = lvt_40_2_ & 255;
                lvt_38_2_ = lvt_38_2_ - (double)lvt_40_2_;
                double lvt_42_2_ = lvt_38_2_ * lvt_38_2_ * lvt_38_2_ * (lvt_38_2_ * (lvt_38_2_ * 6.0D - 15.0D) + 10.0D);

                for (int lvt_44_2_ = 0; lvt_44_2_ < zSize; ++lvt_44_2_)
                {
                    double lvt_45_1_ = zOffset + (double)lvt_44_2_ * zScale + this.zCoord;
                    int lvt_47_1_ = (int)lvt_45_1_;

                    if (lvt_45_1_ < (double)lvt_47_1_)
                    {
                        --lvt_47_1_;
                    }

                    int lvt_48_1_ = lvt_47_1_ & 255;
                    lvt_45_1_ = lvt_45_1_ - (double)lvt_47_1_;
                    double lvt_49_1_ = lvt_45_1_ * lvt_45_1_ * lvt_45_1_ * (lvt_45_1_ * (lvt_45_1_ * 6.0D - 15.0D) + 10.0D);

                    for (int lvt_51_1_ = 0; lvt_51_1_ < ySize; ++lvt_51_1_)
                    {
                        double lvt_52_1_ = yOffset + (double)lvt_51_1_ * yScale + this.yCoord;
                        int lvt_54_1_ = (int)lvt_52_1_;

                        if (lvt_52_1_ < (double)lvt_54_1_)
                        {
                            --lvt_54_1_;
                        }

                        int lvt_55_1_ = lvt_54_1_ & 255;
                        lvt_52_1_ = lvt_52_1_ - (double)lvt_54_1_;
                        double lvt_56_1_ = lvt_52_1_ * lvt_52_1_ * lvt_52_1_ * (lvt_52_1_ * (lvt_52_1_ * 6.0D - 15.0D) + 10.0D);

                        if (lvt_51_1_ == 0 || lvt_55_1_ != lvt_22_2_)
                        {
                            lvt_22_2_ = lvt_55_1_;
                            lvt_23_2_ = this.permutations[lvt_41_2_] + lvt_55_1_;
                            lvt_24_1_ = this.permutations[lvt_23_2_] + lvt_48_1_;
                            lvt_25_2_ = this.permutations[lvt_23_2_ + 1] + lvt_48_1_;
                            lvt_26_1_ = this.permutations[lvt_41_2_ + 1] + lvt_55_1_;
                            lvt_27_2_ = this.permutations[lvt_26_1_] + lvt_48_1_;
                            lvt_28_2_ = this.permutations[lvt_26_1_ + 1] + lvt_48_1_;
                            lvt_29_1_ = this.lerp(lvt_42_2_, this.grad(this.permutations[lvt_24_1_], lvt_38_2_, lvt_52_1_, lvt_45_1_), this.grad(this.permutations[lvt_27_2_], lvt_38_2_ - 1.0D, lvt_52_1_, lvt_45_1_));
                            lvt_31_2_ = this.lerp(lvt_42_2_, this.grad(this.permutations[lvt_25_2_], lvt_38_2_, lvt_52_1_ - 1.0D, lvt_45_1_), this.grad(this.permutations[lvt_28_2_], lvt_38_2_ - 1.0D, lvt_52_1_ - 1.0D, lvt_45_1_));
                            lvt_33_2_ = this.lerp(lvt_42_2_, this.grad(this.permutations[lvt_24_1_ + 1], lvt_38_2_, lvt_52_1_, lvt_45_1_ - 1.0D), this.grad(this.permutations[lvt_27_2_ + 1], lvt_38_2_ - 1.0D, lvt_52_1_, lvt_45_1_ - 1.0D));
                            lvt_35_2_ = this.lerp(lvt_42_2_, this.grad(this.permutations[lvt_25_2_ + 1], lvt_38_2_, lvt_52_1_ - 1.0D, lvt_45_1_ - 1.0D), this.grad(this.permutations[lvt_28_2_ + 1], lvt_38_2_ - 1.0D, lvt_52_1_ - 1.0D, lvt_45_1_ - 1.0D));
                        }

                        double lvt_58_1_ = this.lerp(lvt_56_1_, lvt_29_1_, lvt_31_2_);
                        double lvt_60_1_ = this.lerp(lvt_56_1_, lvt_33_2_, lvt_35_2_);
                        double lvt_62_1_ = this.lerp(lvt_49_1_, lvt_58_1_, lvt_60_1_);
                        int var10001 = lvt_19_2_++;
                        noiseArray[var10001] += lvt_62_1_ * lvt_20_2_;
                    }
                }
            }
        }
    }
}
