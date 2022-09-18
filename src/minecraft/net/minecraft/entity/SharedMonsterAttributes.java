package net.minecraft.entity;

import java.util.Collection;
import java.util.UUID;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SharedMonsterAttributes
{
    private static final Logger logger = LogManager.getLogger();
    public static final IAttribute maxHealth = (new RangedAttribute((IAttribute)null, "generic.maxHealth", 20.0D, 0.0D, 1024.0D)).setDescription("Max Health").setShouldWatch(true);
    public static final IAttribute followRange = (new RangedAttribute((IAttribute)null, "generic.followRange", 32.0D, 0.0D, 2048.0D)).setDescription("Follow Range");
    public static final IAttribute knockbackResistance = (new RangedAttribute((IAttribute)null, "generic.knockbackResistance", 0.0D, 0.0D, 1.0D)).setDescription("Knockback Resistance");
    public static final IAttribute movementSpeed = (new RangedAttribute((IAttribute)null, "generic.movementSpeed", 0.699999988079071D, 0.0D, 1024.0D)).setDescription("Movement Speed").setShouldWatch(true);
    public static final IAttribute attackDamage = new RangedAttribute((IAttribute)null, "generic.attackDamage", 2.0D, 0.0D, 2048.0D);

    /**
     * Creates an NBTTagList from a BaseAttributeMap, including all its AttributeInstances
     */
    public static NBTTagList writeBaseAttributeMapToNBT(BaseAttributeMap map)
    {
        NBTTagList lvt_1_1_ = new NBTTagList();

        for (IAttributeInstance lvt_3_1_ : map.getAllAttributes())
        {
            lvt_1_1_.appendTag(writeAttributeInstanceToNBT(lvt_3_1_));
        }

        return lvt_1_1_;
    }

    /**
     * Creates an NBTTagCompound from an AttributeInstance, including its AttributeModifiers
     */
    private static NBTTagCompound writeAttributeInstanceToNBT(IAttributeInstance instance)
    {
        NBTTagCompound lvt_1_1_ = new NBTTagCompound();
        IAttribute lvt_2_1_ = instance.getAttribute();
        lvt_1_1_.setString("Name", lvt_2_1_.getAttributeUnlocalizedName());
        lvt_1_1_.setDouble("Base", instance.getBaseValue());
        Collection<AttributeModifier> lvt_3_1_ = instance.func_111122_c();

        if (lvt_3_1_ != null && !lvt_3_1_.isEmpty())
        {
            NBTTagList lvt_4_1_ = new NBTTagList();

            for (AttributeModifier lvt_6_1_ : lvt_3_1_)
            {
                if (lvt_6_1_.isSaved())
                {
                    lvt_4_1_.appendTag(writeAttributeModifierToNBT(lvt_6_1_));
                }
            }

            lvt_1_1_.setTag("Modifiers", lvt_4_1_);
        }

        return lvt_1_1_;
    }

    /**
     * Creates an NBTTagCompound from an AttributeModifier
     */
    private static NBTTagCompound writeAttributeModifierToNBT(AttributeModifier modifier)
    {
        NBTTagCompound lvt_1_1_ = new NBTTagCompound();
        lvt_1_1_.setString("Name", modifier.getName());
        lvt_1_1_.setDouble("Amount", modifier.getAmount());
        lvt_1_1_.setInteger("Operation", modifier.getOperation());
        lvt_1_1_.setLong("UUIDMost", modifier.getID().getMostSignificantBits());
        lvt_1_1_.setLong("UUIDLeast", modifier.getID().getLeastSignificantBits());
        return lvt_1_1_;
    }

    public static void setAttributeModifiers(BaseAttributeMap map, NBTTagList list)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < list.tagCount(); ++lvt_2_1_)
        {
            NBTTagCompound lvt_3_1_ = list.getCompoundTagAt(lvt_2_1_);
            IAttributeInstance lvt_4_1_ = map.getAttributeInstanceByName(lvt_3_1_.getString("Name"));

            if (lvt_4_1_ != null)
            {
                applyModifiersToAttributeInstance(lvt_4_1_, lvt_3_1_);
            }
            else
            {
                logger.warn("Ignoring unknown attribute \'" + lvt_3_1_.getString("Name") + "\'");
            }
        }
    }

    private static void applyModifiersToAttributeInstance(IAttributeInstance instance, NBTTagCompound compound)
    {
        instance.setBaseValue(compound.getDouble("Base"));

        if (compound.hasKey("Modifiers", 9))
        {
            NBTTagList lvt_2_1_ = compound.getTagList("Modifiers", 10);

            for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.tagCount(); ++lvt_3_1_)
            {
                AttributeModifier lvt_4_1_ = readAttributeModifierFromNBT(lvt_2_1_.getCompoundTagAt(lvt_3_1_));

                if (lvt_4_1_ != null)
                {
                    AttributeModifier lvt_5_1_ = instance.getModifier(lvt_4_1_.getID());

                    if (lvt_5_1_ != null)
                    {
                        instance.removeModifier(lvt_5_1_);
                    }

                    instance.applyModifier(lvt_4_1_);
                }
            }
        }
    }

    /**
     * Creates an AttributeModifier from an NBTTagCompound
     */
    public static AttributeModifier readAttributeModifierFromNBT(NBTTagCompound compound)
    {
        UUID lvt_1_1_ = new UUID(compound.getLong("UUIDMost"), compound.getLong("UUIDLeast"));

        try
        {
            return new AttributeModifier(lvt_1_1_, compound.getString("Name"), compound.getDouble("Amount"), compound.getInteger("Operation"));
        }
        catch (Exception var3)
        {
            logger.warn("Unable to create attribute: " + var3.getMessage());
            return null;
        }
    }
}
