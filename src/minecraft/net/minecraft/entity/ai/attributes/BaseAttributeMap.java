package net.minecraft.entity.ai.attributes;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.server.management.LowerStringMap;

public abstract class BaseAttributeMap
{
    protected final Map<IAttribute, IAttributeInstance> attributes = Maps.newHashMap();
    protected final Map<String, IAttributeInstance> attributesByName = new LowerStringMap();
    protected final Multimap<IAttribute, IAttribute> field_180377_c = HashMultimap.create();

    public IAttributeInstance getAttributeInstance(IAttribute attribute)
    {
        return (IAttributeInstance)this.attributes.get(attribute);
    }

    public IAttributeInstance getAttributeInstanceByName(String attributeName)
    {
        return (IAttributeInstance)this.attributesByName.get(attributeName);
    }

    /**
     * Registers an attribute with this AttributeMap, returns a modifiable AttributeInstance associated with this map
     */
    public IAttributeInstance registerAttribute(IAttribute attribute)
    {
        if (this.attributesByName.containsKey(attribute.getAttributeUnlocalizedName()))
        {
            throw new IllegalArgumentException("Attribute is already registered!");
        }
        else
        {
            IAttributeInstance lvt_2_1_ = this.func_180376_c(attribute);
            this.attributesByName.put(attribute.getAttributeUnlocalizedName(), lvt_2_1_);
            this.attributes.put(attribute, lvt_2_1_);

            for (IAttribute lvt_3_1_ = attribute.func_180372_d(); lvt_3_1_ != null; lvt_3_1_ = lvt_3_1_.func_180372_d())
            {
                this.field_180377_c.put(lvt_3_1_, attribute);
            }

            return lvt_2_1_;
        }
    }

    protected abstract IAttributeInstance func_180376_c(IAttribute attribute);

    public Collection<IAttributeInstance> getAllAttributes()
    {
        return this.attributesByName.values();
    }

    public void func_180794_a(IAttributeInstance instance)
    {
    }

    public void removeAttributeModifiers(Multimap<String, AttributeModifier> modifiers)
    {
        for (Entry<String, AttributeModifier> lvt_3_1_ : modifiers.entries())
        {
            IAttributeInstance lvt_4_1_ = this.getAttributeInstanceByName((String)lvt_3_1_.getKey());

            if (lvt_4_1_ != null)
            {
                lvt_4_1_.removeModifier((AttributeModifier)lvt_3_1_.getValue());
            }
        }
    }

    public void applyAttributeModifiers(Multimap<String, AttributeModifier> modifiers)
    {
        for (Entry<String, AttributeModifier> lvt_3_1_ : modifiers.entries())
        {
            IAttributeInstance lvt_4_1_ = this.getAttributeInstanceByName((String)lvt_3_1_.getKey());

            if (lvt_4_1_ != null)
            {
                lvt_4_1_.removeModifier((AttributeModifier)lvt_3_1_.getValue());
                lvt_4_1_.applyModifier((AttributeModifier)lvt_3_1_.getValue());
            }
        }
    }
}
