package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;

public class FlatGeneratorInfo
{
    private final List<FlatLayerInfo> flatLayers = Lists.newArrayList();
    private final Map<String, Map<String, String>> worldFeatures = Maps.newHashMap();
    private int biomeToUse;

    /**
     * Return the biome used on this preset.
     */
    public int getBiome()
    {
        return this.biomeToUse;
    }

    /**
     * Set the biome used on this preset.
     */
    public void setBiome(int biome)
    {
        this.biomeToUse = biome;
    }

    public Map<String, Map<String, String>> getWorldFeatures()
    {
        return this.worldFeatures;
    }

    public List<FlatLayerInfo> getFlatLayers()
    {
        return this.flatLayers;
    }

    public void func_82645_d()
    {
        int lvt_1_1_ = 0;

        for (FlatLayerInfo lvt_3_1_ : this.flatLayers)
        {
            lvt_3_1_.setMinY(lvt_1_1_);
            lvt_1_1_ += lvt_3_1_.getLayerCount();
        }
    }

    public String toString()
    {
        StringBuilder lvt_1_1_ = new StringBuilder();
        lvt_1_1_.append(3);
        lvt_1_1_.append(";");

        for (int lvt_2_1_ = 0; lvt_2_1_ < this.flatLayers.size(); ++lvt_2_1_)
        {
            if (lvt_2_1_ > 0)
            {
                lvt_1_1_.append(",");
            }

            lvt_1_1_.append(((FlatLayerInfo)this.flatLayers.get(lvt_2_1_)).toString());
        }

        lvt_1_1_.append(";");
        lvt_1_1_.append(this.biomeToUse);

        if (!this.worldFeatures.isEmpty())
        {
            lvt_1_1_.append(";");
            int lvt_2_2_ = 0;

            for (Entry<String, Map<String, String>> lvt_4_1_ : this.worldFeatures.entrySet())
            {
                if (lvt_2_2_++ > 0)
                {
                    lvt_1_1_.append(",");
                }

                lvt_1_1_.append(((String)lvt_4_1_.getKey()).toLowerCase());
                Map<String, String> lvt_5_1_ = (Map)lvt_4_1_.getValue();

                if (!lvt_5_1_.isEmpty())
                {
                    lvt_1_1_.append("(");
                    int lvt_6_1_ = 0;

                    for (Entry<String, String> lvt_8_1_ : lvt_5_1_.entrySet())
                    {
                        if (lvt_6_1_++ > 0)
                        {
                            lvt_1_1_.append(" ");
                        }

                        lvt_1_1_.append((String)lvt_8_1_.getKey());
                        lvt_1_1_.append("=");
                        lvt_1_1_.append((String)lvt_8_1_.getValue());
                    }

                    lvt_1_1_.append(")");
                }
            }
        }
        else
        {
            lvt_1_1_.append(";");
        }

        return lvt_1_1_.toString();
    }

    private static FlatLayerInfo func_180715_a(int p_180715_0_, String p_180715_1_, int p_180715_2_)
    {
        String[] lvt_3_1_ = p_180715_0_ >= 3 ? p_180715_1_.split("\\*", 2) : p_180715_1_.split("x", 2);
        int lvt_4_1_ = 1;
        int lvt_5_1_ = 0;

        if (lvt_3_1_.length == 2)
        {
            try
            {
                lvt_4_1_ = Integer.parseInt(lvt_3_1_[0]);

                if (p_180715_2_ + lvt_4_1_ >= 256)
                {
                    lvt_4_1_ = 256 - p_180715_2_;
                }

                if (lvt_4_1_ < 0)
                {
                    lvt_4_1_ = 0;
                }
            }
            catch (Throwable var8)
            {
                return null;
            }
        }

        Block lvt_6_2_ = null;

        try
        {
            String lvt_7_1_ = lvt_3_1_[lvt_3_1_.length - 1];

            if (p_180715_0_ < 3)
            {
                lvt_3_1_ = lvt_7_1_.split(":", 2);

                if (lvt_3_1_.length > 1)
                {
                    lvt_5_1_ = Integer.parseInt(lvt_3_1_[1]);
                }

                lvt_6_2_ = Block.getBlockById(Integer.parseInt(lvt_3_1_[0]));
            }
            else
            {
                lvt_3_1_ = lvt_7_1_.split(":", 3);
                lvt_6_2_ = lvt_3_1_.length > 1 ? Block.getBlockFromName(lvt_3_1_[0] + ":" + lvt_3_1_[1]) : null;

                if (lvt_6_2_ != null)
                {
                    lvt_5_1_ = lvt_3_1_.length > 2 ? Integer.parseInt(lvt_3_1_[2]) : 0;
                }
                else
                {
                    lvt_6_2_ = Block.getBlockFromName(lvt_3_1_[0]);

                    if (lvt_6_2_ != null)
                    {
                        lvt_5_1_ = lvt_3_1_.length > 1 ? Integer.parseInt(lvt_3_1_[1]) : 0;
                    }
                }

                if (lvt_6_2_ == null)
                {
                    return null;
                }
            }

            if (lvt_6_2_ == Blocks.air)
            {
                lvt_5_1_ = 0;
            }

            if (lvt_5_1_ < 0 || lvt_5_1_ > 15)
            {
                lvt_5_1_ = 0;
            }
        }
        catch (Throwable var9)
        {
            return null;
        }

        FlatLayerInfo lvt_7_3_ = new FlatLayerInfo(p_180715_0_, lvt_4_1_, lvt_6_2_, lvt_5_1_);
        lvt_7_3_.setMinY(p_180715_2_);
        return lvt_7_3_;
    }

    private static List<FlatLayerInfo> func_180716_a(int p_180716_0_, String p_180716_1_)
    {
        if (p_180716_1_ != null && p_180716_1_.length() >= 1)
        {
            List<FlatLayerInfo> lvt_2_1_ = Lists.newArrayList();
            String[] lvt_3_1_ = p_180716_1_.split(",");
            int lvt_4_1_ = 0;

            for (String lvt_8_1_ : lvt_3_1_)
            {
                FlatLayerInfo lvt_9_1_ = func_180715_a(p_180716_0_, lvt_8_1_, lvt_4_1_);

                if (lvt_9_1_ == null)
                {
                    return null;
                }

                lvt_2_1_.add(lvt_9_1_);
                lvt_4_1_ += lvt_9_1_.getLayerCount();
            }

            return lvt_2_1_;
        }
        else
        {
            return null;
        }
    }

    public static FlatGeneratorInfo createFlatGeneratorFromString(String flatGeneratorSettings)
    {
        if (flatGeneratorSettings == null)
        {
            return getDefaultFlatGenerator();
        }
        else
        {
            String[] lvt_1_1_ = flatGeneratorSettings.split(";", -1);
            int lvt_2_1_ = lvt_1_1_.length == 1 ? 0 : MathHelper.parseIntWithDefault(lvt_1_1_[0], 0);

            if (lvt_2_1_ >= 0 && lvt_2_1_ <= 3)
            {
                FlatGeneratorInfo lvt_3_1_ = new FlatGeneratorInfo();
                int lvt_4_1_ = lvt_1_1_.length == 1 ? 0 : 1;
                List<FlatLayerInfo> lvt_5_1_ = func_180716_a(lvt_2_1_, lvt_1_1_[lvt_4_1_++]);

                if (lvt_5_1_ != null && !lvt_5_1_.isEmpty())
                {
                    lvt_3_1_.getFlatLayers().addAll(lvt_5_1_);
                    lvt_3_1_.func_82645_d();
                    int lvt_6_1_ = BiomeGenBase.plains.biomeID;

                    if (lvt_2_1_ > 0 && lvt_1_1_.length > lvt_4_1_)
                    {
                        lvt_6_1_ = MathHelper.parseIntWithDefault(lvt_1_1_[lvt_4_1_++], lvt_6_1_);
                    }

                    lvt_3_1_.setBiome(lvt_6_1_);

                    if (lvt_2_1_ > 0 && lvt_1_1_.length > lvt_4_1_)
                    {
                        String[] lvt_7_1_ = lvt_1_1_[lvt_4_1_++].toLowerCase().split(",");

                        for (String lvt_11_1_ : lvt_7_1_)
                        {
                            String[] lvt_12_1_ = lvt_11_1_.split("\\(", 2);
                            Map<String, String> lvt_13_1_ = Maps.newHashMap();

                            if (lvt_12_1_[0].length() > 0)
                            {
                                lvt_3_1_.getWorldFeatures().put(lvt_12_1_[0], lvt_13_1_);

                                if (lvt_12_1_.length > 1 && lvt_12_1_[1].endsWith(")") && lvt_12_1_[1].length() > 1)
                                {
                                    String[] lvt_14_1_ = lvt_12_1_[1].substring(0, lvt_12_1_[1].length() - 1).split(" ");

                                    for (int lvt_15_1_ = 0; lvt_15_1_ < lvt_14_1_.length; ++lvt_15_1_)
                                    {
                                        String[] lvt_16_1_ = lvt_14_1_[lvt_15_1_].split("=", 2);

                                        if (lvt_16_1_.length == 2)
                                        {
                                            lvt_13_1_.put(lvt_16_1_[0], lvt_16_1_[1]);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        lvt_3_1_.getWorldFeatures().put("village", Maps.newHashMap());
                    }

                    return lvt_3_1_;
                }
                else
                {
                    return getDefaultFlatGenerator();
                }
            }
            else
            {
                return getDefaultFlatGenerator();
            }
        }
    }

    public static FlatGeneratorInfo getDefaultFlatGenerator()
    {
        FlatGeneratorInfo lvt_0_1_ = new FlatGeneratorInfo();
        lvt_0_1_.setBiome(BiomeGenBase.plains.biomeID);
        lvt_0_1_.getFlatLayers().add(new FlatLayerInfo(1, Blocks.bedrock));
        lvt_0_1_.getFlatLayers().add(new FlatLayerInfo(2, Blocks.dirt));
        lvt_0_1_.getFlatLayers().add(new FlatLayerInfo(1, Blocks.grass));
        lvt_0_1_.func_82645_d();
        lvt_0_1_.getWorldFeatures().put("village", Maps.newHashMap());
        return lvt_0_1_;
    }
}
