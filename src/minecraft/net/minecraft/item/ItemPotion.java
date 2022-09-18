package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemPotion extends Item
{
    private Map<Integer, List<PotionEffect>> effectCache = Maps.newHashMap();
    private static final Map<List<PotionEffect>, Integer> SUB_ITEMS_CACHE = Maps.newLinkedHashMap();

    public ItemPotion()
    {
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabs.tabBrewing);
    }

    public List<PotionEffect> getEffects(ItemStack stack)
    {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("CustomPotionEffects", 9))
        {
            List<PotionEffect> lvt_2_2_ = Lists.newArrayList();
            NBTTagList lvt_3_1_ = stack.getTagCompound().getTagList("CustomPotionEffects", 10);

            for (int lvt_4_1_ = 0; lvt_4_1_ < lvt_3_1_.tagCount(); ++lvt_4_1_)
            {
                NBTTagCompound lvt_5_1_ = lvt_3_1_.getCompoundTagAt(lvt_4_1_);
                PotionEffect lvt_6_1_ = PotionEffect.readCustomPotionEffectFromNBT(lvt_5_1_);

                if (lvt_6_1_ != null)
                {
                    lvt_2_2_.add(lvt_6_1_);
                }
            }

            return lvt_2_2_;
        }
        else
        {
            List<PotionEffect> lvt_2_1_ = (List)this.effectCache.get(Integer.valueOf(stack.getMetadata()));

            if (lvt_2_1_ == null)
            {
                lvt_2_1_ = PotionHelper.getPotionEffects(stack.getMetadata(), false);
                this.effectCache.put(Integer.valueOf(stack.getMetadata()), lvt_2_1_);
            }

            return lvt_2_1_;
        }
    }

    public List<PotionEffect> getEffects(int meta)
    {
        List<PotionEffect> lvt_2_1_ = (List)this.effectCache.get(Integer.valueOf(meta));

        if (lvt_2_1_ == null)
        {
            lvt_2_1_ = PotionHelper.getPotionEffects(meta, false);
            this.effectCache.put(Integer.valueOf(meta), lvt_2_1_);
        }

        return lvt_2_1_;
    }

    /**
     * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
     * the Item before the action is complete.
     */
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn)
    {
        if (!playerIn.capabilities.isCreativeMode)
        {
            --stack.stackSize;
        }

        if (!worldIn.isRemote)
        {
            List<PotionEffect> lvt_4_1_ = this.getEffects(stack);

            if (lvt_4_1_ != null)
            {
                for (PotionEffect lvt_6_1_ : lvt_4_1_)
                {
                    playerIn.addPotionEffect(new PotionEffect(lvt_6_1_));
                }
            }
        }

        playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);

        if (!playerIn.capabilities.isCreativeMode)
        {
            if (stack.stackSize <= 0)
            {
                return new ItemStack(Items.glass_bottle);
            }

            playerIn.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
        }

        return stack;
    }

    /**
     * How long it takes to use or consume an item
     */
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 32;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.DRINK;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
        if (isSplash(itemStackIn.getMetadata()))
        {
            if (!playerIn.capabilities.isCreativeMode)
            {
                --itemStackIn.stackSize;
            }

            worldIn.playSoundAtEntity(playerIn, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

            if (!worldIn.isRemote)
            {
                worldIn.spawnEntityInWorld(new EntityPotion(worldIn, playerIn, itemStackIn));
            }

            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
            return itemStackIn;
        }
        else
        {
            playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
            return itemStackIn;
        }
    }

    /**
     * returns wether or not a potion is a throwable splash potion based on damage value
     */
    public static boolean isSplash(int meta)
    {
        return (meta & 16384) != 0;
    }

    public int getColorFromDamage(int meta)
    {
        return PotionHelper.getLiquidColor(meta, false);
    }

    public int getColorFromItemStack(ItemStack stack, int renderPass)
    {
        return renderPass > 0 ? 16777215 : this.getColorFromDamage(stack.getMetadata());
    }

    public boolean isEffectInstant(int meta)
    {
        List<PotionEffect> lvt_2_1_ = this.getEffects(meta);

        if (lvt_2_1_ != null && !lvt_2_1_.isEmpty())
        {
            for (PotionEffect lvt_4_1_ : lvt_2_1_)
            {
                if (Potion.potionTypes[lvt_4_1_.getPotionID()].isInstant())
                {
                    return true;
                }
            }

            return false;
        }
        else
        {
            return false;
        }
    }

    public String getItemStackDisplayName(ItemStack stack)
    {
        if (stack.getMetadata() == 0)
        {
            return StatCollector.translateToLocal("item.emptyPotion.name").trim();
        }
        else
        {
            String lvt_2_1_ = "";

            if (isSplash(stack.getMetadata()))
            {
                lvt_2_1_ = StatCollector.translateToLocal("potion.prefix.grenade").trim() + " ";
            }

            List<PotionEffect> lvt_3_1_ = Items.potionitem.getEffects(stack);

            if (lvt_3_1_ != null && !lvt_3_1_.isEmpty())
            {
                String lvt_4_1_ = ((PotionEffect)lvt_3_1_.get(0)).getEffectName();
                lvt_4_1_ = lvt_4_1_ + ".postfix";
                return lvt_2_1_ + StatCollector.translateToLocal(lvt_4_1_).trim();
            }
            else
            {
                String lvt_4_2_ = PotionHelper.getPotionPrefix(stack.getMetadata());
                return StatCollector.translateToLocal(lvt_4_2_).trim() + " " + super.getItemStackDisplayName(stack);
            }
        }
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        if (stack.getMetadata() != 0)
        {
            List<PotionEffect> lvt_5_1_ = Items.potionitem.getEffects(stack);
            Multimap<String, AttributeModifier> lvt_6_1_ = HashMultimap.create();

            if (lvt_5_1_ != null && !lvt_5_1_.isEmpty())
            {
                for (PotionEffect lvt_8_1_ : lvt_5_1_)
                {
                    String lvt_9_1_ = StatCollector.translateToLocal(lvt_8_1_.getEffectName()).trim();
                    Potion lvt_10_1_ = Potion.potionTypes[lvt_8_1_.getPotionID()];
                    Map<IAttribute, AttributeModifier> lvt_11_1_ = lvt_10_1_.getAttributeModifierMap();

                    if (lvt_11_1_ != null && lvt_11_1_.size() > 0)
                    {
                        for (Entry<IAttribute, AttributeModifier> lvt_13_1_ : lvt_11_1_.entrySet())
                        {
                            AttributeModifier lvt_14_1_ = (AttributeModifier)lvt_13_1_.getValue();
                            AttributeModifier lvt_15_1_ = new AttributeModifier(lvt_14_1_.getName(), lvt_10_1_.getAttributeModifierAmount(lvt_8_1_.getAmplifier(), lvt_14_1_), lvt_14_1_.getOperation());
                            lvt_6_1_.put(((IAttribute)lvt_13_1_.getKey()).getAttributeUnlocalizedName(), lvt_15_1_);
                        }
                    }

                    if (lvt_8_1_.getAmplifier() > 0)
                    {
                        lvt_9_1_ = lvt_9_1_ + " " + StatCollector.translateToLocal("potion.potency." + lvt_8_1_.getAmplifier()).trim();
                    }

                    if (lvt_8_1_.getDuration() > 20)
                    {
                        lvt_9_1_ = lvt_9_1_ + " (" + Potion.getDurationString(lvt_8_1_) + ")";
                    }

                    if (lvt_10_1_.isBadEffect())
                    {
                        tooltip.add(EnumChatFormatting.RED + lvt_9_1_);
                    }
                    else
                    {
                        tooltip.add(EnumChatFormatting.GRAY + lvt_9_1_);
                    }
                }
            }
            else
            {
                String lvt_7_2_ = StatCollector.translateToLocal("potion.empty").trim();
                tooltip.add(EnumChatFormatting.GRAY + lvt_7_2_);
            }

            if (!lvt_6_1_.isEmpty())
            {
                tooltip.add("");
                tooltip.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("potion.effects.whenDrank"));

                for (Entry<String, AttributeModifier> lvt_8_2_ : lvt_6_1_.entries())
                {
                    AttributeModifier lvt_9_2_ = (AttributeModifier)lvt_8_2_.getValue();
                    double lvt_10_2_ = lvt_9_2_.getAmount();
                    double lvt_12_3_;

                    if (lvt_9_2_.getOperation() != 1 && lvt_9_2_.getOperation() != 2)
                    {
                        lvt_12_3_ = lvt_9_2_.getAmount();
                    }
                    else
                    {
                        lvt_12_3_ = lvt_9_2_.getAmount() * 100.0D;
                    }

                    if (lvt_10_2_ > 0.0D)
                    {
                        tooltip.add(EnumChatFormatting.BLUE + StatCollector.translateToLocalFormatted("attribute.modifier.plus." + lvt_9_2_.getOperation(), new Object[] {ItemStack.DECIMALFORMAT.format(lvt_12_3_), StatCollector.translateToLocal("attribute.name." + (String)lvt_8_2_.getKey())}));
                    }
                    else if (lvt_10_2_ < 0.0D)
                    {
                        lvt_12_3_ = lvt_12_3_ * -1.0D;
                        tooltip.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("attribute.modifier.take." + lvt_9_2_.getOperation(), new Object[] {ItemStack.DECIMALFORMAT.format(lvt_12_3_), StatCollector.translateToLocal("attribute.name." + (String)lvt_8_2_.getKey())}));
                    }
                }
            }
        }
    }

    public boolean hasEffect(ItemStack stack)
    {
        List<PotionEffect> lvt_2_1_ = this.getEffects(stack);
        return lvt_2_1_ != null && !lvt_2_1_.isEmpty();
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
    {
        super.getSubItems(itemIn, tab, subItems);

        if (SUB_ITEMS_CACHE.isEmpty())
        {
            for (int lvt_4_1_ = 0; lvt_4_1_ <= 15; ++lvt_4_1_)
            {
                for (int lvt_5_1_ = 0; lvt_5_1_ <= 1; ++lvt_5_1_)
                {
                    int lvt_6_1_;

                    if (lvt_5_1_ == 0)
                    {
                        lvt_6_1_ = lvt_4_1_ | 8192;
                    }
                    else
                    {
                        lvt_6_1_ = lvt_4_1_ | 16384;
                    }

                    for (int lvt_7_1_ = 0; lvt_7_1_ <= 2; ++lvt_7_1_)
                    {
                        int lvt_8_1_ = lvt_6_1_;

                        if (lvt_7_1_ != 0)
                        {
                            if (lvt_7_1_ == 1)
                            {
                                lvt_8_1_ = lvt_6_1_ | 32;
                            }
                            else if (lvt_7_1_ == 2)
                            {
                                lvt_8_1_ = lvt_6_1_ | 64;
                            }
                        }

                        List<PotionEffect> lvt_9_1_ = PotionHelper.getPotionEffects(lvt_8_1_, false);

                        if (lvt_9_1_ != null && !lvt_9_1_.isEmpty())
                        {
                            SUB_ITEMS_CACHE.put(lvt_9_1_, Integer.valueOf(lvt_8_1_));
                        }
                    }
                }
            }
        }

        Iterator lvt_4_2_ = SUB_ITEMS_CACHE.values().iterator();

        while (lvt_4_2_.hasNext())
        {
            int lvt_5_2_ = ((Integer)lvt_4_2_.next()).intValue();
            subItems.add(new ItemStack(itemIn, 1, lvt_5_2_));
        }
    }
}
