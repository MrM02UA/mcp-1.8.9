package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.minecraft.server.management.LowerStringMap;

public class ServersideAttributeMap extends BaseAttributeMap
{
    private final Set<IAttributeInstance> attributeInstanceSet = Sets.newHashSet();
    protected final Map<String, IAttributeInstance> descriptionToAttributeInstanceMap = new LowerStringMap();

    public ModifiableAttributeInstance getAttributeInstance(IAttribute attribute)
    {
        return (ModifiableAttributeInstance)super.getAttributeInstance(attribute);
    }

    public ModifiableAttributeInstance getAttributeInstanceByName(String attributeName)
    {
        IAttributeInstance lvt_2_1_ = super.getAttributeInstanceByName(attributeName);

        if (lvt_2_1_ == null)
        {
            lvt_2_1_ = (IAttributeInstance)this.descriptionToAttributeInstanceMap.get(attributeName);
        }

        return (ModifiableAttributeInstance)lvt_2_1_;
    }

    /**
     * Registers an attribute with this AttributeMap, returns a modifiable AttributeInstance associated with this map
     */
    public IAttributeInstance registerAttribute(IAttribute attribute)
    {
        IAttributeInstance lvt_2_1_ = super.registerAttribute(attribute);

        if (attribute instanceof RangedAttribute && ((RangedAttribute)attribute).getDescription() != null)
        {
            this.descriptionToAttributeInstanceMap.put(((RangedAttribute)attribute).getDescription(), lvt_2_1_);
        }

        return lvt_2_1_;
    }

    protected IAttributeInstance func_180376_c(IAttribute attribute)
    {
        return new ModifiableAttributeInstance(this, attribute);
    }

    public void func_180794_a(IAttributeInstance instance)
    {
        if (instance.getAttribute().getShouldWatch())
        {
            this.attributeInstanceSet.add(instance);
        }

        for (IAttribute lvt_3_1_ : this.field_180377_c.get(instance.getAttribute()))
        {
            ModifiableAttributeInstance lvt_4_1_ = this.getAttributeInstance(lvt_3_1_);

            if (lvt_4_1_ != null)
            {
                lvt_4_1_.flagForUpdate();
            }
        }
    }

    public Set<IAttributeInstance> getAttributeInstanceSet()
    {
        return this.attributeInstanceSet;
    }

    public Collection<IAttributeInstance> getWatchedAttributes()
    {
        Set<IAttributeInstance> lvt_1_1_ = Sets.newHashSet();

        for (IAttributeInstance lvt_3_1_ : this.getAllAttributes())
        {
            if (lvt_3_1_.getAttribute().getShouldWatch())
            {
                lvt_1_1_.add(lvt_3_1_);
            }
        }

        return lvt_1_1_;
    }

    public IAttributeInstance getAttributeInstanceByName(String attributeName)
    {
        return this.getAttributeInstanceByName(attributeName);
    }

    public IAttributeInstance getAttributeInstance(IAttribute attribute)
    {
        return this.getAttributeInstance(attribute);
    }
}
