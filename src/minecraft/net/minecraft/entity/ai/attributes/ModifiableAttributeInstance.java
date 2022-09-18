package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ModifiableAttributeInstance implements IAttributeInstance
{
    /** The BaseAttributeMap this attributeInstance can be found in */
    private final BaseAttributeMap attributeMap;

    /** The Attribute this is an instance of */
    private final IAttribute genericAttribute;
    private final Map<Integer, Set<AttributeModifier>> mapByOperation = Maps.newHashMap();
    private final Map<String, Set<AttributeModifier>> mapByName = Maps.newHashMap();
    private final Map<UUID, AttributeModifier> mapByUUID = Maps.newHashMap();
    private double baseValue;
    private boolean needsUpdate = true;
    private double cachedValue;

    public ModifiableAttributeInstance(BaseAttributeMap attributeMapIn, IAttribute genericAttributeIn)
    {
        this.attributeMap = attributeMapIn;
        this.genericAttribute = genericAttributeIn;
        this.baseValue = genericAttributeIn.getDefaultValue();

        for (int lvt_3_1_ = 0; lvt_3_1_ < 3; ++lvt_3_1_)
        {
            this.mapByOperation.put(Integer.valueOf(lvt_3_1_), Sets.newHashSet());
        }
    }

    /**
     * Get the Attribute this is an instance of
     */
    public IAttribute getAttribute()
    {
        return this.genericAttribute;
    }

    public double getBaseValue()
    {
        return this.baseValue;
    }

    public void setBaseValue(double baseValue)
    {
        if (baseValue != this.getBaseValue())
        {
            this.baseValue = baseValue;
            this.flagForUpdate();
        }
    }

    public Collection<AttributeModifier> getModifiersByOperation(int operation)
    {
        return (Collection)this.mapByOperation.get(Integer.valueOf(operation));
    }

    public Collection<AttributeModifier> func_111122_c()
    {
        Set<AttributeModifier> lvt_1_1_ = Sets.newHashSet();

        for (int lvt_2_1_ = 0; lvt_2_1_ < 3; ++lvt_2_1_)
        {
            lvt_1_1_.addAll(this.getModifiersByOperation(lvt_2_1_));
        }

        return lvt_1_1_;
    }

    /**
     * Returns attribute modifier, if any, by the given UUID
     */
    public AttributeModifier getModifier(UUID uuid)
    {
        return (AttributeModifier)this.mapByUUID.get(uuid);
    }

    public boolean hasModifier(AttributeModifier modifier)
    {
        return this.mapByUUID.get(modifier.getID()) != null;
    }

    public void applyModifier(AttributeModifier modifier)
    {
        if (this.getModifier(modifier.getID()) != null)
        {
            throw new IllegalArgumentException("Modifier is already applied on this attribute!");
        }
        else
        {
            Set<AttributeModifier> lvt_2_1_ = (Set)this.mapByName.get(modifier.getName());

            if (lvt_2_1_ == null)
            {
                lvt_2_1_ = Sets.newHashSet();
                this.mapByName.put(modifier.getName(), lvt_2_1_);
            }

            ((Set)this.mapByOperation.get(Integer.valueOf(modifier.getOperation()))).add(modifier);
            lvt_2_1_.add(modifier);
            this.mapByUUID.put(modifier.getID(), modifier);
            this.flagForUpdate();
        }
    }

    protected void flagForUpdate()
    {
        this.needsUpdate = true;
        this.attributeMap.func_180794_a(this);
    }

    public void removeModifier(AttributeModifier modifier)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < 3; ++lvt_2_1_)
        {
            Set<AttributeModifier> lvt_3_1_ = (Set)this.mapByOperation.get(Integer.valueOf(lvt_2_1_));
            lvt_3_1_.remove(modifier);
        }

        Set<AttributeModifier> lvt_2_2_ = (Set)this.mapByName.get(modifier.getName());

        if (lvt_2_2_ != null)
        {
            lvt_2_2_.remove(modifier);

            if (lvt_2_2_.isEmpty())
            {
                this.mapByName.remove(modifier.getName());
            }
        }

        this.mapByUUID.remove(modifier.getID());
        this.flagForUpdate();
    }

    public void removeAllModifiers()
    {
        Collection<AttributeModifier> lvt_1_1_ = this.func_111122_c();

        if (lvt_1_1_ != null)
        {
            for (AttributeModifier lvt_3_1_ : Lists.newArrayList(lvt_1_1_))
            {
                this.removeModifier(lvt_3_1_);
            }
        }
    }

    public double getAttributeValue()
    {
        if (this.needsUpdate)
        {
            this.cachedValue = this.computeValue();
            this.needsUpdate = false;
        }

        return this.cachedValue;
    }

    private double computeValue()
    {
        double lvt_1_1_ = this.getBaseValue();

        for (AttributeModifier lvt_4_1_ : this.func_180375_b(0))
        {
            lvt_1_1_ += lvt_4_1_.getAmount();
        }

        double lvt_3_2_ = lvt_1_1_;

        for (AttributeModifier lvt_6_1_ : this.func_180375_b(1))
        {
            lvt_3_2_ += lvt_1_1_ * lvt_6_1_.getAmount();
        }

        for (AttributeModifier lvt_6_2_ : this.func_180375_b(2))
        {
            lvt_3_2_ *= 1.0D + lvt_6_2_.getAmount();
        }

        return this.genericAttribute.clampValue(lvt_3_2_);
    }

    private Collection<AttributeModifier> func_180375_b(int operation)
    {
        Set<AttributeModifier> lvt_2_1_ = Sets.newHashSet(this.getModifiersByOperation(operation));

        for (IAttribute lvt_3_1_ = this.genericAttribute.func_180372_d(); lvt_3_1_ != null; lvt_3_1_ = lvt_3_1_.func_180372_d())
        {
            IAttributeInstance lvt_4_1_ = this.attributeMap.getAttributeInstance(lvt_3_1_);

            if (lvt_4_1_ != null)
            {
                lvt_2_1_.addAll(lvt_4_1_.getModifiersByOperation(operation));
            }
        }

        return lvt_2_1_;
    }
}
