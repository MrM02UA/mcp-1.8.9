package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.WeightedRandom;

public class EnchantmentHelper
{
    /** Is the random seed of enchantment effects. */
    private static final Random enchantmentRand = new Random();

    /**
     * Used to calculate the extra armor of enchantments on armors equipped on player.
     */
    private static final EnchantmentHelper.ModifierDamage enchantmentModifierDamage = new EnchantmentHelper.ModifierDamage();

    /**
     * Used to calculate the (magic) extra damage done by enchantments on current equipped item of player.
     */
    private static final EnchantmentHelper.ModifierLiving enchantmentModifierLiving = new EnchantmentHelper.ModifierLiving();
    private static final EnchantmentHelper.HurtIterator ENCHANTMENT_ITERATOR_HURT = new EnchantmentHelper.HurtIterator();
    private static final EnchantmentHelper.DamageIterator ENCHANTMENT_ITERATOR_DAMAGE = new EnchantmentHelper.DamageIterator();

    /**
     * Returns the level of enchantment on the ItemStack passed.
     */
    public static int getEnchantmentLevel(int enchID, ItemStack stack)
    {
        if (stack == null)
        {
            return 0;
        }
        else
        {
            NBTTagList lvt_2_1_ = stack.getEnchantmentTagList();

            if (lvt_2_1_ == null)
            {
                return 0;
            }
            else
            {
                for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.tagCount(); ++lvt_3_1_)
                {
                    int lvt_4_1_ = lvt_2_1_.getCompoundTagAt(lvt_3_1_).getShort("id");
                    int lvt_5_1_ = lvt_2_1_.getCompoundTagAt(lvt_3_1_).getShort("lvl");

                    if (lvt_4_1_ == enchID)
                    {
                        return lvt_5_1_;
                    }
                }

                return 0;
            }
        }
    }

    public static Map<Integer, Integer> getEnchantments(ItemStack stack)
    {
        Map<Integer, Integer> lvt_1_1_ = Maps.newLinkedHashMap();
        NBTTagList lvt_2_1_ = stack.getItem() == Items.enchanted_book ? Items.enchanted_book.getEnchantments(stack) : stack.getEnchantmentTagList();

        if (lvt_2_1_ != null)
        {
            for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.tagCount(); ++lvt_3_1_)
            {
                int lvt_4_1_ = lvt_2_1_.getCompoundTagAt(lvt_3_1_).getShort("id");
                int lvt_5_1_ = lvt_2_1_.getCompoundTagAt(lvt_3_1_).getShort("lvl");
                lvt_1_1_.put(Integer.valueOf(lvt_4_1_), Integer.valueOf(lvt_5_1_));
            }
        }

        return lvt_1_1_;
    }

    /**
     * Set the enchantments for the specified stack.
     */
    public static void setEnchantments(Map<Integer, Integer> enchMap, ItemStack stack)
    {
        NBTTagList lvt_2_1_ = new NBTTagList();
        Iterator lvt_3_1_ = enchMap.keySet().iterator();

        while (lvt_3_1_.hasNext())
        {
            int lvt_4_1_ = ((Integer)lvt_3_1_.next()).intValue();
            Enchantment lvt_5_1_ = Enchantment.getEnchantmentById(lvt_4_1_);

            if (lvt_5_1_ != null)
            {
                NBTTagCompound lvt_6_1_ = new NBTTagCompound();
                lvt_6_1_.setShort("id", (short)lvt_4_1_);
                lvt_6_1_.setShort("lvl", (short)((Integer)enchMap.get(Integer.valueOf(lvt_4_1_))).intValue());
                lvt_2_1_.appendTag(lvt_6_1_);

                if (stack.getItem() == Items.enchanted_book)
                {
                    Items.enchanted_book.addEnchantment(stack, new EnchantmentData(lvt_5_1_, ((Integer)enchMap.get(Integer.valueOf(lvt_4_1_))).intValue()));
                }
            }
        }

        if (lvt_2_1_.tagCount() > 0)
        {
            if (stack.getItem() != Items.enchanted_book)
            {
                stack.setTagInfo("ench", lvt_2_1_);
            }
        }
        else if (stack.hasTagCompound())
        {
            stack.getTagCompound().removeTag("ench");
        }
    }

    /**
     * Returns the biggest level of the enchantment on the array of ItemStack passed.
     */
    public static int getMaxEnchantmentLevel(int enchID, ItemStack[] stacks)
    {
        if (stacks == null)
        {
            return 0;
        }
        else
        {
            int lvt_2_1_ = 0;

            for (ItemStack lvt_6_1_ : stacks)
            {
                int lvt_7_1_ = getEnchantmentLevel(enchID, lvt_6_1_);

                if (lvt_7_1_ > lvt_2_1_)
                {
                    lvt_2_1_ = lvt_7_1_;
                }
            }

            return lvt_2_1_;
        }
    }

    /**
     * Executes the enchantment modifier on the ItemStack passed.
     */
    private static void applyEnchantmentModifier(EnchantmentHelper.IModifier modifier, ItemStack stack)
    {
        if (stack != null)
        {
            NBTTagList lvt_2_1_ = stack.getEnchantmentTagList();

            if (lvt_2_1_ != null)
            {
                for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.tagCount(); ++lvt_3_1_)
                {
                    int lvt_4_1_ = lvt_2_1_.getCompoundTagAt(lvt_3_1_).getShort("id");
                    int lvt_5_1_ = lvt_2_1_.getCompoundTagAt(lvt_3_1_).getShort("lvl");

                    if (Enchantment.getEnchantmentById(lvt_4_1_) != null)
                    {
                        modifier.calculateModifier(Enchantment.getEnchantmentById(lvt_4_1_), lvt_5_1_);
                    }
                }
            }
        }
    }

    /**
     * Executes the enchantment modifier on the array of ItemStack passed.
     */
    private static void applyEnchantmentModifierArray(EnchantmentHelper.IModifier modifier, ItemStack[] stacks)
    {
        for (ItemStack lvt_5_1_ : stacks)
        {
            applyEnchantmentModifier(modifier, lvt_5_1_);
        }
    }

    /**
     * Returns the modifier of protection enchantments on armors equipped on player.
     */
    public static int getEnchantmentModifierDamage(ItemStack[] stacks, DamageSource source)
    {
        enchantmentModifierDamage.damageModifier = 0;
        enchantmentModifierDamage.source = source;
        applyEnchantmentModifierArray(enchantmentModifierDamage, stacks);

        if (enchantmentModifierDamage.damageModifier > 25)
        {
            enchantmentModifierDamage.damageModifier = 25;
        }
        else if (enchantmentModifierDamage.damageModifier < 0)
        {
            enchantmentModifierDamage.damageModifier = 0;
        }

        return (enchantmentModifierDamage.damageModifier + 1 >> 1) + enchantmentRand.nextInt((enchantmentModifierDamage.damageModifier >> 1) + 1);
    }

    public static float getModifierForCreature(ItemStack p_152377_0_, EnumCreatureAttribute p_152377_1_)
    {
        enchantmentModifierLiving.livingModifier = 0.0F;
        enchantmentModifierLiving.entityLiving = p_152377_1_;
        applyEnchantmentModifier(enchantmentModifierLiving, p_152377_0_);
        return enchantmentModifierLiving.livingModifier;
    }

    public static void applyThornEnchantments(EntityLivingBase p_151384_0_, Entity p_151384_1_)
    {
        ENCHANTMENT_ITERATOR_HURT.attacker = p_151384_1_;
        ENCHANTMENT_ITERATOR_HURT.user = p_151384_0_;

        if (p_151384_0_ != null)
        {
            applyEnchantmentModifierArray(ENCHANTMENT_ITERATOR_HURT, p_151384_0_.getInventory());
        }

        if (p_151384_1_ instanceof EntityPlayer)
        {
            applyEnchantmentModifier(ENCHANTMENT_ITERATOR_HURT, p_151384_0_.getHeldItem());
        }
    }

    public static void applyArthropodEnchantments(EntityLivingBase p_151385_0_, Entity p_151385_1_)
    {
        ENCHANTMENT_ITERATOR_DAMAGE.user = p_151385_0_;
        ENCHANTMENT_ITERATOR_DAMAGE.target = p_151385_1_;

        if (p_151385_0_ != null)
        {
            applyEnchantmentModifierArray(ENCHANTMENT_ITERATOR_DAMAGE, p_151385_0_.getInventory());
        }

        if (p_151385_0_ instanceof EntityPlayer)
        {
            applyEnchantmentModifier(ENCHANTMENT_ITERATOR_DAMAGE, p_151385_0_.getHeldItem());
        }
    }

    /**
     * Returns the Knockback modifier of the enchantment on the players held item.
     */
    public static int getKnockbackModifier(EntityLivingBase player)
    {
        return getEnchantmentLevel(Enchantment.knockback.effectId, player.getHeldItem());
    }

    /**
     * Returns the fire aspect modifier of the players held item.
     */
    public static int getFireAspectModifier(EntityLivingBase player)
    {
        return getEnchantmentLevel(Enchantment.fireAspect.effectId, player.getHeldItem());
    }

    /**
     * Returns the 'Water Breathing' modifier of enchantments on player equipped armors.
     */
    public static int getRespiration(Entity player)
    {
        return getMaxEnchantmentLevel(Enchantment.respiration.effectId, player.getInventory());
    }

    /**
     * Returns the level of the Depth Strider enchantment.
     */
    public static int getDepthStriderModifier(Entity player)
    {
        return getMaxEnchantmentLevel(Enchantment.depthStrider.effectId, player.getInventory());
    }

    /**
     * Return the extra efficiency of tools based on enchantments on equipped player item.
     */
    public static int getEfficiencyModifier(EntityLivingBase player)
    {
        return getEnchantmentLevel(Enchantment.efficiency.effectId, player.getHeldItem());
    }

    /**
     * Returns the silk touch status of enchantments on current equipped item of player.
     */
    public static boolean getSilkTouchModifier(EntityLivingBase player)
    {
        return getEnchantmentLevel(Enchantment.silkTouch.effectId, player.getHeldItem()) > 0;
    }

    /**
     * Returns the fortune enchantment modifier of the current equipped item of player.
     */
    public static int getFortuneModifier(EntityLivingBase player)
    {
        return getEnchantmentLevel(Enchantment.fortune.effectId, player.getHeldItem());
    }

    /**
     * Returns the level of the 'Luck Of The Sea' enchantment.
     */
    public static int getLuckOfSeaModifier(EntityLivingBase player)
    {
        return getEnchantmentLevel(Enchantment.luckOfTheSea.effectId, player.getHeldItem());
    }

    /**
     * Returns the level of the 'Lure' enchantment on the players held item.
     */
    public static int getLureModifier(EntityLivingBase player)
    {
        return getEnchantmentLevel(Enchantment.lure.effectId, player.getHeldItem());
    }

    /**
     * Returns the looting enchantment modifier of the current equipped item of player.
     */
    public static int getLootingModifier(EntityLivingBase player)
    {
        return getEnchantmentLevel(Enchantment.looting.effectId, player.getHeldItem());
    }

    /**
     * Returns the aqua affinity status of enchantments on current equipped item of player.
     */
    public static boolean getAquaAffinityModifier(EntityLivingBase player)
    {
        return getMaxEnchantmentLevel(Enchantment.aquaAffinity.effectId, player.getInventory()) > 0;
    }

    public static ItemStack getEnchantedItem(Enchantment p_92099_0_, EntityLivingBase p_92099_1_)
    {
        for (ItemStack lvt_5_1_ : p_92099_1_.getInventory())
        {
            if (lvt_5_1_ != null && getEnchantmentLevel(p_92099_0_.effectId, lvt_5_1_) > 0)
            {
                return lvt_5_1_;
            }
        }

        return null;
    }

    /**
     * Returns the enchantability of itemstack, using a separate calculation for each enchantNum (0, 1 or 2), cutting to
     * the max enchantability power of the table, which is locked to a max of 15.
     */
    public static int calcItemStackEnchantability(Random rand, int enchantNum, int power, ItemStack stack)
    {
        Item lvt_4_1_ = stack.getItem();
        int lvt_5_1_ = lvt_4_1_.getItemEnchantability();

        if (lvt_5_1_ <= 0)
        {
            return 0;
        }
        else
        {
            if (power > 15)
            {
                power = 15;
            }

            int lvt_6_1_ = rand.nextInt(8) + 1 + (power >> 1) + rand.nextInt(power + 1);
            return enchantNum == 0 ? Math.max(lvt_6_1_ / 3, 1) : (enchantNum == 1 ? lvt_6_1_ * 2 / 3 + 1 : Math.max(lvt_6_1_, power * 2));
        }
    }

    /**
     * Adds a random enchantment to the specified item. Args: random, itemStack, enchantabilityLevel
     */
    public static ItemStack addRandomEnchantment(Random p_77504_0_, ItemStack p_77504_1_, int p_77504_2_)
    {
        List<EnchantmentData> lvt_3_1_ = buildEnchantmentList(p_77504_0_, p_77504_1_, p_77504_2_);
        boolean lvt_4_1_ = p_77504_1_.getItem() == Items.book;

        if (lvt_4_1_)
        {
            p_77504_1_.setItem(Items.enchanted_book);
        }

        if (lvt_3_1_ != null)
        {
            for (EnchantmentData lvt_6_1_ : lvt_3_1_)
            {
                if (lvt_4_1_)
                {
                    Items.enchanted_book.addEnchantment(p_77504_1_, lvt_6_1_);
                }
                else
                {
                    p_77504_1_.addEnchantment(lvt_6_1_.enchantmentobj, lvt_6_1_.enchantmentLevel);
                }
            }
        }

        return p_77504_1_;
    }

    public static List<EnchantmentData> buildEnchantmentList(Random randomIn, ItemStack itemStackIn, int p_77513_2_)
    {
        Item lvt_3_1_ = itemStackIn.getItem();
        int lvt_4_1_ = lvt_3_1_.getItemEnchantability();

        if (lvt_4_1_ <= 0)
        {
            return null;
        }
        else
        {
            lvt_4_1_ = lvt_4_1_ / 2;
            lvt_4_1_ = 1 + randomIn.nextInt((lvt_4_1_ >> 1) + 1) + randomIn.nextInt((lvt_4_1_ >> 1) + 1);
            int lvt_5_1_ = lvt_4_1_ + p_77513_2_;
            float lvt_6_1_ = (randomIn.nextFloat() + randomIn.nextFloat() - 1.0F) * 0.15F;
            int lvt_7_1_ = (int)((float)lvt_5_1_ * (1.0F + lvt_6_1_) + 0.5F);

            if (lvt_7_1_ < 1)
            {
                lvt_7_1_ = 1;
            }

            List<EnchantmentData> lvt_8_1_ = null;
            Map<Integer, EnchantmentData> lvt_9_1_ = mapEnchantmentData(lvt_7_1_, itemStackIn);

            if (lvt_9_1_ != null && !lvt_9_1_.isEmpty())
            {
                EnchantmentData lvt_10_1_ = (EnchantmentData)WeightedRandom.getRandomItem(randomIn, lvt_9_1_.values());

                if (lvt_10_1_ != null)
                {
                    lvt_8_1_ = Lists.newArrayList();
                    lvt_8_1_.add(lvt_10_1_);

                    for (int lvt_11_1_ = lvt_7_1_; randomIn.nextInt(50) <= lvt_11_1_; lvt_11_1_ >>= 1)
                    {
                        Iterator<Integer> lvt_12_1_ = lvt_9_1_.keySet().iterator();

                        while (lvt_12_1_.hasNext())
                        {
                            Integer lvt_13_1_ = (Integer)lvt_12_1_.next();
                            boolean lvt_14_1_ = true;

                            for (EnchantmentData lvt_16_1_ : lvt_8_1_)
                            {
                                if (!lvt_16_1_.enchantmentobj.canApplyTogether(Enchantment.getEnchantmentById(lvt_13_1_.intValue())))
                                {
                                    lvt_14_1_ = false;
                                    break;
                                }
                            }

                            if (!lvt_14_1_)
                            {
                                lvt_12_1_.remove();
                            }
                        }

                        if (!lvt_9_1_.isEmpty())
                        {
                            EnchantmentData lvt_13_2_ = (EnchantmentData)WeightedRandom.getRandomItem(randomIn, lvt_9_1_.values());
                            lvt_8_1_.add(lvt_13_2_);
                        }
                    }
                }
            }

            return lvt_8_1_;
        }
    }

    public static Map<Integer, EnchantmentData> mapEnchantmentData(int p_77505_0_, ItemStack p_77505_1_)
    {
        Item lvt_2_1_ = p_77505_1_.getItem();
        Map<Integer, EnchantmentData> lvt_3_1_ = null;
        boolean lvt_4_1_ = p_77505_1_.getItem() == Items.book;

        for (Enchantment lvt_8_1_ : Enchantment.enchantmentsBookList)
        {
            if (lvt_8_1_ != null && (lvt_8_1_.type.canEnchantItem(lvt_2_1_) || lvt_4_1_))
            {
                for (int lvt_9_1_ = lvt_8_1_.getMinLevel(); lvt_9_1_ <= lvt_8_1_.getMaxLevel(); ++lvt_9_1_)
                {
                    if (p_77505_0_ >= lvt_8_1_.getMinEnchantability(lvt_9_1_) && p_77505_0_ <= lvt_8_1_.getMaxEnchantability(lvt_9_1_))
                    {
                        if (lvt_3_1_ == null)
                        {
                            lvt_3_1_ = Maps.newHashMap();
                        }

                        lvt_3_1_.put(Integer.valueOf(lvt_8_1_.effectId), new EnchantmentData(lvt_8_1_, lvt_9_1_));
                    }
                }
            }
        }

        return lvt_3_1_;
    }

    static final class DamageIterator implements EnchantmentHelper.IModifier
    {
        public EntityLivingBase user;
        public Entity target;

        private DamageIterator()
        {
        }

        public void calculateModifier(Enchantment enchantmentIn, int enchantmentLevel)
        {
            enchantmentIn.onEntityDamaged(this.user, this.target, enchantmentLevel);
        }
    }

    static final class HurtIterator implements EnchantmentHelper.IModifier
    {
        public EntityLivingBase user;
        public Entity attacker;

        private HurtIterator()
        {
        }

        public void calculateModifier(Enchantment enchantmentIn, int enchantmentLevel)
        {
            enchantmentIn.onUserHurt(this.user, this.attacker, enchantmentLevel);
        }
    }

    interface IModifier
    {
        void calculateModifier(Enchantment enchantmentIn, int enchantmentLevel);
    }

    static final class ModifierDamage implements EnchantmentHelper.IModifier
    {
        public int damageModifier;
        public DamageSource source;

        private ModifierDamage()
        {
        }

        public void calculateModifier(Enchantment enchantmentIn, int enchantmentLevel)
        {
            this.damageModifier += enchantmentIn.calcModifierDamage(enchantmentLevel, this.source);
        }
    }

    static final class ModifierLiving implements EnchantmentHelper.IModifier
    {
        public float livingModifier;
        public EnumCreatureAttribute entityLiving;

        private ModifierLiving()
        {
        }

        public void calculateModifier(Enchantment enchantmentIn, int enchantmentLevel)
        {
            this.livingModifier += enchantmentIn.calcDamageByCreature(enchantmentLevel, this.entityLiving);
        }
    }
}
