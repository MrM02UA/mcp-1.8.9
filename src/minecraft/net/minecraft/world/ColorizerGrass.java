package net.minecraft.world;

public class ColorizerGrass
{
    /** Color buffer for grass */
    private static int[] grassBuffer = new int[65536];

    public static void setGrassBiomeColorizer(int[] p_77479_0_)
    {
        grassBuffer = p_77479_0_;
    }

    /**
     * Gets grass color from temperature and humidity. Args: temperature, humidity
     */
    public static int getGrassColor(double p_77480_0_, double p_77480_2_)
    {
        p_77480_2_ = p_77480_2_ * p_77480_0_;
        int lvt_4_1_ = (int)((1.0D - p_77480_0_) * 255.0D);
        int lvt_5_1_ = (int)((1.0D - p_77480_2_) * 255.0D);
        int lvt_6_1_ = lvt_5_1_ << 8 | lvt_4_1_;
        return lvt_6_1_ > grassBuffer.length ? -65281 : grassBuffer[lvt_6_1_];
    }
}
