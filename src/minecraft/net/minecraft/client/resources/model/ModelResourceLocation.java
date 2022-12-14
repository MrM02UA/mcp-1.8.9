package net.minecraft.client.resources.model;

import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

public class ModelResourceLocation extends ResourceLocation
{
    private final String variant;

    protected ModelResourceLocation(int p_i46078_1_, String... p_i46078_2_)
    {
        super(0, new String[] {p_i46078_2_[0], p_i46078_2_[1]});
        this.variant = StringUtils.isEmpty(p_i46078_2_[2]) ? "normal" : p_i46078_2_[2].toLowerCase();
    }

    public ModelResourceLocation(String p_i46079_1_)
    {
        this(0, parsePathString(p_i46079_1_));
    }

    public ModelResourceLocation(ResourceLocation p_i46080_1_, String p_i46080_2_)
    {
        this(p_i46080_1_.toString(), p_i46080_2_);
    }

    public ModelResourceLocation(String p_i46081_1_, String p_i46081_2_)
    {
        this(0, parsePathString(p_i46081_1_ + '#' + (p_i46081_2_ == null ? "normal" : p_i46081_2_)));
    }

    protected static String[] parsePathString(String p_177517_0_)
    {
        String[] lvt_1_1_ = new String[] {null, p_177517_0_, null};
        int lvt_2_1_ = p_177517_0_.indexOf(35);
        String lvt_3_1_ = p_177517_0_;

        if (lvt_2_1_ >= 0)
        {
            lvt_1_1_[2] = p_177517_0_.substring(lvt_2_1_ + 1, p_177517_0_.length());

            if (lvt_2_1_ > 1)
            {
                lvt_3_1_ = p_177517_0_.substring(0, lvt_2_1_);
            }
        }

        System.arraycopy(ResourceLocation.splitObjectName(lvt_3_1_), 0, lvt_1_1_, 0, 2);
        return lvt_1_1_;
    }

    public String getVariant()
    {
        return this.variant;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (p_equals_1_ instanceof ModelResourceLocation && super.equals(p_equals_1_))
        {
            ModelResourceLocation lvt_2_1_ = (ModelResourceLocation)p_equals_1_;
            return this.variant.equals(lvt_2_1_.variant);
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return 31 * super.hashCode() + this.variant.hashCode();
    }

    public String toString()
    {
        return super.toString() + '#' + this.variant;
    }
}
