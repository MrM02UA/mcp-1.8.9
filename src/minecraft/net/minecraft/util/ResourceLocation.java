package net.minecraft.util;

import org.apache.commons.lang3.Validate;

public class ResourceLocation
{
    protected final String resourceDomain;
    protected final String resourcePath;

    protected ResourceLocation(int p_i45928_1_, String... resourceName)
    {
        this.resourceDomain = org.apache.commons.lang3.StringUtils.isEmpty(resourceName[0]) ? "minecraft" : resourceName[0].toLowerCase();
        this.resourcePath = resourceName[1];
        Validate.notNull(this.resourcePath);
    }

    public ResourceLocation(String resourceName)
    {
        this(0, splitObjectName(resourceName));
    }

    public ResourceLocation(String resourceDomainIn, String resourcePathIn)
    {
        this(0, new String[] {resourceDomainIn, resourcePathIn});
    }

    /**
     * Splits an object name (such as minecraft:apple) into the domain and path parts and returns these as an array of
     * length 2. If no colon is present in the passed value the returned array will contain {null, toSplit}.
     */
    protected static String[] splitObjectName(String toSplit)
    {
        String[] lvt_1_1_ = new String[] {null, toSplit};
        int lvt_2_1_ = toSplit.indexOf(58);

        if (lvt_2_1_ >= 0)
        {
            lvt_1_1_[1] = toSplit.substring(lvt_2_1_ + 1, toSplit.length());

            if (lvt_2_1_ > 1)
            {
                lvt_1_1_[0] = toSplit.substring(0, lvt_2_1_);
            }
        }

        return lvt_1_1_;
    }

    public String getResourcePath()
    {
        return this.resourcePath;
    }

    public String getResourceDomain()
    {
        return this.resourceDomain;
    }

    public String toString()
    {
        return this.resourceDomain + ':' + this.resourcePath;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof ResourceLocation))
        {
            return false;
        }
        else
        {
            ResourceLocation lvt_2_1_ = (ResourceLocation)p_equals_1_;
            return this.resourceDomain.equals(lvt_2_1_.resourceDomain) && this.resourcePath.equals(lvt_2_1_.resourcePath);
        }
    }

    public int hashCode()
    {
        return 31 * this.resourceDomain.hashCode() + this.resourcePath.hashCode();
    }
}
