package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.util.MathHelper;

public class NoiseGeneratorOctaves extends NoiseGenerator
{
    /**
     * Collection of noise generation functions.  Output is combined to produce different octaves of noise.
     */
    private NoiseGeneratorImproved[] generatorCollection;
    private int octaves;

    public NoiseGeneratorOctaves(Random seed, int octavesIn)
    {
        this.octaves = octavesIn;
        this.generatorCollection = new NoiseGeneratorImproved[octavesIn];

        for (int lvt_3_1_ = 0; lvt_3_1_ < octavesIn; ++lvt_3_1_)
        {
            this.generatorCollection[lvt_3_1_] = new NoiseGeneratorImproved(seed);
        }
    }

    /**
     * pars:(par2,3,4=noiseOffset ; so that adjacent noise segments connect) (pars5,6,7=x,y,zArraySize),(pars8,10,12 =
     * x,y,z noiseScale)
     */
    public double[] generateNoiseOctaves(double[] noiseArray, int xOffset, int yOffset, int zOffset, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale)
    {
        if (noiseArray == null)
        {
            noiseArray = new double[xSize * ySize * zSize];
        }
        else
        {
            for (int lvt_14_1_ = 0; lvt_14_1_ < noiseArray.length; ++lvt_14_1_)
            {
                noiseArray[lvt_14_1_] = 0.0D;
            }
        }

        double lvt_14_2_ = 1.0D;

        for (int lvt_16_1_ = 0; lvt_16_1_ < this.octaves; ++lvt_16_1_)
        {
            double lvt_17_1_ = (double)xOffset * lvt_14_2_ * xScale;
            double lvt_19_1_ = (double)yOffset * lvt_14_2_ * yScale;
            double lvt_21_1_ = (double)zOffset * lvt_14_2_ * zScale;
            long lvt_23_1_ = MathHelper.floor_double_long(lvt_17_1_);
            long lvt_25_1_ = MathHelper.floor_double_long(lvt_21_1_);
            lvt_17_1_ = lvt_17_1_ - (double)lvt_23_1_;
            lvt_21_1_ = lvt_21_1_ - (double)lvt_25_1_;
            lvt_23_1_ = lvt_23_1_ % 16777216L;
            lvt_25_1_ = lvt_25_1_ % 16777216L;
            lvt_17_1_ = lvt_17_1_ + (double)lvt_23_1_;
            lvt_21_1_ = lvt_21_1_ + (double)lvt_25_1_;
            this.generatorCollection[lvt_16_1_].populateNoiseArray(noiseArray, lvt_17_1_, lvt_19_1_, lvt_21_1_, xSize, ySize, zSize, xScale * lvt_14_2_, yScale * lvt_14_2_, zScale * lvt_14_2_, lvt_14_2_);
            lvt_14_2_ /= 2.0D;
        }

        return noiseArray;
    }

    /**
     * Bouncer function to the main one with some default arguments.
     */
    public double[] generateNoiseOctaves(double[] noiseArray, int xOffset, int zOffset, int xSize, int zSize, double xScale, double zScale, double p_76305_10_)
    {
        return this.generateNoiseOctaves(noiseArray, xOffset, 10, zOffset, xSize, 1, zSize, xScale, 1.0D, zScale);
    }
}
