package net.minecraft.potion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.util.IntegerCache;

public class PotionHelper
{
    public static final String unusedString = null;
    public static final String sugarEffect = "-0+1-2-3&4-4+13";
    public static final String ghastTearEffect = "+0-1-2-3&4-4+13";
    public static final String spiderEyeEffect = "-0-1+2-3&4-4+13";
    public static final String fermentedSpiderEyeEffect = "-0+3-4+13";
    public static final String speckledMelonEffect = "+0-1+2-3&4-4+13";
    public static final String blazePowderEffect = "+0-1-2+3&4-4+13";
    public static final String magmaCreamEffect = "+0+1-2-3&4-4+13";
    public static final String redstoneEffect = "-5+6-7";
    public static final String glowstoneEffect = "+5-6-7";
    public static final String gunpowderEffect = "+14&13-13";
    public static final String goldenCarrotEffect = "-0+1+2-3+13&4-4";
    public static final String pufferfishEffect = "+0-1+2+3+13&4-4";
    public static final String rabbitFootEffect = "+0+1-2+3&4-4+13";
    private static final Map<Integer, String> potionRequirements = Maps.newHashMap();
    private static final Map<Integer, String> potionAmplifiers = Maps.newHashMap();
    private static final Map<Integer, Integer> DATAVALUE_COLORS = Maps.newHashMap();

    /** An array of possible potion prefix names, as translation IDs. */
    private static final String[] potionPrefixes = new String[] {"potion.prefix.mundane", "potion.prefix.uninteresting", "potion.prefix.bland", "potion.prefix.clear", "potion.prefix.milky", "potion.prefix.diffuse", "potion.prefix.artless", "potion.prefix.thin", "potion.prefix.awkward", "potion.prefix.flat", "potion.prefix.bulky", "potion.prefix.bungling", "potion.prefix.buttered", "potion.prefix.smooth", "potion.prefix.suave", "potion.prefix.debonair", "potion.prefix.thick", "potion.prefix.elegant", "potion.prefix.fancy", "potion.prefix.charming", "potion.prefix.dashing", "potion.prefix.refined", "potion.prefix.cordial", "potion.prefix.sparkling", "potion.prefix.potent", "potion.prefix.foul", "potion.prefix.odorless", "potion.prefix.rank", "potion.prefix.harsh", "potion.prefix.acrid", "potion.prefix.gross", "potion.prefix.stinky"};

    /**
     * Checks if the bit at 1 << j is on in i.
     */
    public static boolean checkFlag(int p_77914_0_, int p_77914_1_)
    {
        return (p_77914_0_ & 1 << p_77914_1_) != 0;
    }

    /**
     * Returns 1 if the flag is set, 0 if it is not set.
     */
    private static int isFlagSet(int p_77910_0_, int p_77910_1_)
    {
        return checkFlag(p_77910_0_, p_77910_1_) ? 1 : 0;
    }

    /**
     * Returns 0 if the flag is set, 1 if it is not set.
     */
    private static int isFlagUnset(int p_77916_0_, int p_77916_1_)
    {
        return checkFlag(p_77916_0_, p_77916_1_) ? 0 : 1;
    }

    /**
     * Given a potion data value, get its prefix index number.
     */
    public static int getPotionPrefixIndex(int dataValue)
    {
        return getPotionPrefixIndexFlags(dataValue, 5, 4, 3, 2, 1);
    }

    /**
     * Given a {@link Collection}<{@link PotionEffect}> will return an Integer color.
     */
    public static int calcPotionLiquidColor(Collection<PotionEffect> p_77911_0_)
    {
        int lvt_1_1_ = 3694022;

        if (p_77911_0_ != null && !p_77911_0_.isEmpty())
        {
            float lvt_2_1_ = 0.0F;
            float lvt_3_1_ = 0.0F;
            float lvt_4_1_ = 0.0F;
            float lvt_5_1_ = 0.0F;

            for (PotionEffect lvt_7_1_ : p_77911_0_)
            {
                if (lvt_7_1_.getIsShowParticles())
                {
                    int lvt_8_1_ = Potion.potionTypes[lvt_7_1_.getPotionID()].getLiquidColor();

                    for (int lvt_9_1_ = 0; lvt_9_1_ <= lvt_7_1_.getAmplifier(); ++lvt_9_1_)
                    {
                        lvt_2_1_ += (float)(lvt_8_1_ >> 16 & 255) / 255.0F;
                        lvt_3_1_ += (float)(lvt_8_1_ >> 8 & 255) / 255.0F;
                        lvt_4_1_ += (float)(lvt_8_1_ >> 0 & 255) / 255.0F;
                        ++lvt_5_1_;
                    }
                }
            }

            if (lvt_5_1_ == 0.0F)
            {
                return 0;
            }
            else
            {
                lvt_2_1_ = lvt_2_1_ / lvt_5_1_ * 255.0F;
                lvt_3_1_ = lvt_3_1_ / lvt_5_1_ * 255.0F;
                lvt_4_1_ = lvt_4_1_ / lvt_5_1_ * 255.0F;
                return (int)lvt_2_1_ << 16 | (int)lvt_3_1_ << 8 | (int)lvt_4_1_;
            }
        }
        else
        {
            return lvt_1_1_;
        }
    }

    /**
     * Check whether a {@link Collection}<{@link PotionEffect}> are all ambient.
     */
    public static boolean getAreAmbient(Collection<PotionEffect> potionEffects)
    {
        for (PotionEffect lvt_2_1_ : potionEffects)
        {
            if (!lvt_2_1_.getIsAmbient())
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Given a potion data value, get the associated liquid color (optionally bypassing the cache)
     */
    public static int getLiquidColor(int dataValue, boolean bypassCache)
    {
        Integer lvt_2_1_ = IntegerCache.getInteger(dataValue);

        if (!bypassCache)
        {
            if (DATAVALUE_COLORS.containsKey(lvt_2_1_))
            {
                return ((Integer)DATAVALUE_COLORS.get(lvt_2_1_)).intValue();
            }
            else
            {
                int lvt_3_1_ = calcPotionLiquidColor(getPotionEffects(lvt_2_1_.intValue(), false));
                DATAVALUE_COLORS.put(lvt_2_1_, Integer.valueOf(lvt_3_1_));
                return lvt_3_1_;
            }
        }
        else
        {
            return calcPotionLiquidColor(getPotionEffects(lvt_2_1_.intValue(), true));
        }
    }

    /**
     * Given a potion data value, get its prefix as a translation ID.
     */
    public static String getPotionPrefix(int dataValue)
    {
        int lvt_1_1_ = getPotionPrefixIndex(dataValue);
        return potionPrefixes[lvt_1_1_];
    }

    private static int getPotionEffect(boolean p_77904_0_, boolean p_77904_1_, boolean p_77904_2_, int p_77904_3_, int p_77904_4_, int p_77904_5_, int p_77904_6_)
    {
        int lvt_7_1_ = 0;

        if (p_77904_0_)
        {
            lvt_7_1_ = isFlagUnset(p_77904_6_, p_77904_4_);
        }
        else if (p_77904_3_ != -1)
        {
            if (p_77904_3_ == 0 && countSetFlags(p_77904_6_) == p_77904_4_)
            {
                lvt_7_1_ = 1;
            }
            else if (p_77904_3_ == 1 && countSetFlags(p_77904_6_) > p_77904_4_)
            {
                lvt_7_1_ = 1;
            }
            else if (p_77904_3_ == 2 && countSetFlags(p_77904_6_) < p_77904_4_)
            {
                lvt_7_1_ = 1;
            }
        }
        else
        {
            lvt_7_1_ = isFlagSet(p_77904_6_, p_77904_4_);
        }

        if (p_77904_1_)
        {
            lvt_7_1_ *= p_77904_5_;
        }

        if (p_77904_2_)
        {
            lvt_7_1_ *= -1;
        }

        return lvt_7_1_;
    }

    /**
     * Returns the number of 1 bits in the given integer.
     */
    private static int countSetFlags(int p_77907_0_)
    {
        int lvt_1_1_;

        for (lvt_1_1_ = 0; p_77907_0_ > 0; ++lvt_1_1_)
        {
            p_77907_0_ &= p_77907_0_ - 1;
        }

        return lvt_1_1_;
    }

    private static int parsePotionEffects(String p_77912_0_, int p_77912_1_, int p_77912_2_, int p_77912_3_)
    {
        if (p_77912_1_ < p_77912_0_.length() && p_77912_2_ >= 0 && p_77912_1_ < p_77912_2_)
        {
            int lvt_4_1_ = p_77912_0_.indexOf(124, p_77912_1_);

            if (lvt_4_1_ >= 0 && lvt_4_1_ < p_77912_2_)
            {
                int lvt_5_1_ = parsePotionEffects(p_77912_0_, p_77912_1_, lvt_4_1_ - 1, p_77912_3_);

                if (lvt_5_1_ > 0)
                {
                    return lvt_5_1_;
                }
                else
                {
                    int lvt_6_1_ = parsePotionEffects(p_77912_0_, lvt_4_1_ + 1, p_77912_2_, p_77912_3_);
                    return lvt_6_1_ > 0 ? lvt_6_1_ : 0;
                }
            }
            else
            {
                int lvt_5_2_ = p_77912_0_.indexOf(38, p_77912_1_);

                if (lvt_5_2_ >= 0 && lvt_5_2_ < p_77912_2_)
                {
                    int lvt_6_2_ = parsePotionEffects(p_77912_0_, p_77912_1_, lvt_5_2_ - 1, p_77912_3_);

                    if (lvt_6_2_ <= 0)
                    {
                        return 0;
                    }
                    else
                    {
                        int lvt_7_1_ = parsePotionEffects(p_77912_0_, lvt_5_2_ + 1, p_77912_2_, p_77912_3_);
                        return lvt_7_1_ <= 0 ? 0 : (lvt_6_2_ > lvt_7_1_ ? lvt_6_2_ : lvt_7_1_);
                    }
                }
                else
                {
                    boolean lvt_6_3_ = false;
                    boolean lvt_7_2_ = false;
                    boolean lvt_8_1_ = false;
                    boolean lvt_9_1_ = false;
                    boolean lvt_10_1_ = false;
                    int lvt_11_1_ = -1;
                    int lvt_12_1_ = 0;
                    int lvt_13_1_ = 0;
                    int lvt_14_1_ = 0;

                    for (int lvt_15_1_ = p_77912_1_; lvt_15_1_ < p_77912_2_; ++lvt_15_1_)
                    {
                        char lvt_16_1_ = p_77912_0_.charAt(lvt_15_1_);

                        if (lvt_16_1_ >= 48 && lvt_16_1_ <= 57)
                        {
                            if (lvt_6_3_)
                            {
                                lvt_13_1_ = lvt_16_1_ - 48;
                                lvt_7_2_ = true;
                            }
                            else
                            {
                                lvt_12_1_ = lvt_12_1_ * 10;
                                lvt_12_1_ = lvt_12_1_ + (lvt_16_1_ - 48);
                                lvt_8_1_ = true;
                            }
                        }
                        else if (lvt_16_1_ == 42)
                        {
                            lvt_6_3_ = true;
                        }
                        else if (lvt_16_1_ == 33)
                        {
                            if (lvt_8_1_)
                            {
                                lvt_14_1_ += getPotionEffect(lvt_9_1_, lvt_7_2_, lvt_10_1_, lvt_11_1_, lvt_12_1_, lvt_13_1_, p_77912_3_);
                                lvt_9_1_ = false;
                                lvt_10_1_ = false;
                                lvt_6_3_ = false;
                                lvt_7_2_ = false;
                                lvt_8_1_ = false;
                                lvt_13_1_ = 0;
                                lvt_12_1_ = 0;
                                lvt_11_1_ = -1;
                            }

                            lvt_9_1_ = true;
                        }
                        else if (lvt_16_1_ == 45)
                        {
                            if (lvt_8_1_)
                            {
                                lvt_14_1_ += getPotionEffect(lvt_9_1_, lvt_7_2_, lvt_10_1_, lvt_11_1_, lvt_12_1_, lvt_13_1_, p_77912_3_);
                                lvt_9_1_ = false;
                                lvt_10_1_ = false;
                                lvt_6_3_ = false;
                                lvt_7_2_ = false;
                                lvt_8_1_ = false;
                                lvt_13_1_ = 0;
                                lvt_12_1_ = 0;
                                lvt_11_1_ = -1;
                            }

                            lvt_10_1_ = true;
                        }
                        else if (lvt_16_1_ != 61 && lvt_16_1_ != 60 && lvt_16_1_ != 62)
                        {
                            if (lvt_16_1_ == 43 && lvt_8_1_)
                            {
                                lvt_14_1_ += getPotionEffect(lvt_9_1_, lvt_7_2_, lvt_10_1_, lvt_11_1_, lvt_12_1_, lvt_13_1_, p_77912_3_);
                                lvt_9_1_ = false;
                                lvt_10_1_ = false;
                                lvt_6_3_ = false;
                                lvt_7_2_ = false;
                                lvt_8_1_ = false;
                                lvt_13_1_ = 0;
                                lvt_12_1_ = 0;
                                lvt_11_1_ = -1;
                            }
                        }
                        else
                        {
                            if (lvt_8_1_)
                            {
                                lvt_14_1_ += getPotionEffect(lvt_9_1_, lvt_7_2_, lvt_10_1_, lvt_11_1_, lvt_12_1_, lvt_13_1_, p_77912_3_);
                                lvt_9_1_ = false;
                                lvt_10_1_ = false;
                                lvt_6_3_ = false;
                                lvt_7_2_ = false;
                                lvt_8_1_ = false;
                                lvt_13_1_ = 0;
                                lvt_12_1_ = 0;
                                lvt_11_1_ = -1;
                            }

                            if (lvt_16_1_ == 61)
                            {
                                lvt_11_1_ = 0;
                            }
                            else if (lvt_16_1_ == 60)
                            {
                                lvt_11_1_ = 2;
                            }
                            else if (lvt_16_1_ == 62)
                            {
                                lvt_11_1_ = 1;
                            }
                        }
                    }

                    if (lvt_8_1_)
                    {
                        lvt_14_1_ += getPotionEffect(lvt_9_1_, lvt_7_2_, lvt_10_1_, lvt_11_1_, lvt_12_1_, lvt_13_1_, p_77912_3_);
                    }

                    return lvt_14_1_;
                }
            }
        }
        else
        {
            return 0;
        }
    }

    public static List<PotionEffect> getPotionEffects(int p_77917_0_, boolean p_77917_1_)
    {
        List<PotionEffect> lvt_2_1_ = null;

        for (Potion lvt_6_1_ : Potion.potionTypes)
        {
            if (lvt_6_1_ != null && (!lvt_6_1_.isUsable() || p_77917_1_))
            {
                String lvt_7_1_ = (String)potionRequirements.get(Integer.valueOf(lvt_6_1_.getId()));

                if (lvt_7_1_ != null)
                {
                    int lvt_8_1_ = parsePotionEffects(lvt_7_1_, 0, lvt_7_1_.length(), p_77917_0_);

                    if (lvt_8_1_ > 0)
                    {
                        int lvt_9_1_ = 0;
                        String lvt_10_1_ = (String)potionAmplifiers.get(Integer.valueOf(lvt_6_1_.getId()));

                        if (lvt_10_1_ != null)
                        {
                            lvt_9_1_ = parsePotionEffects(lvt_10_1_, 0, lvt_10_1_.length(), p_77917_0_);

                            if (lvt_9_1_ < 0)
                            {
                                lvt_9_1_ = 0;
                            }
                        }

                        if (lvt_6_1_.isInstant())
                        {
                            lvt_8_1_ = 1;
                        }
                        else
                        {
                            lvt_8_1_ = 1200 * (lvt_8_1_ * 3 + (lvt_8_1_ - 1) * 2);
                            lvt_8_1_ = lvt_8_1_ >> lvt_9_1_;
                            lvt_8_1_ = (int)Math.round((double)lvt_8_1_ * lvt_6_1_.getEffectiveness());

                            if ((p_77917_0_ & 16384) != 0)
                            {
                                lvt_8_1_ = (int)Math.round((double)lvt_8_1_ * 0.75D + 0.5D);
                            }
                        }

                        if (lvt_2_1_ == null)
                        {
                            lvt_2_1_ = Lists.newArrayList();
                        }

                        PotionEffect lvt_11_1_ = new PotionEffect(lvt_6_1_.getId(), lvt_8_1_, lvt_9_1_);

                        if ((p_77917_0_ & 16384) != 0)
                        {
                            lvt_11_1_.setSplashPotion(true);
                        }

                        lvt_2_1_.add(lvt_11_1_);
                    }
                }
            }
        }

        return lvt_2_1_;
    }

    /**
     * Manipulates the specified bit of the potion damage value according to the rules passed from applyIngredient.
     */
    private static int brewBitOperations(int p_77906_0_, int p_77906_1_, boolean p_77906_2_, boolean p_77906_3_, boolean p_77906_4_)
    {
        if (p_77906_4_)
        {
            if (!checkFlag(p_77906_0_, p_77906_1_))
            {
                return 0;
            }
        }
        else if (p_77906_2_)
        {
            p_77906_0_ &= ~(1 << p_77906_1_);
        }
        else if (p_77906_3_)
        {
            if ((p_77906_0_ & 1 << p_77906_1_) == 0)
            {
                p_77906_0_ |= 1 << p_77906_1_;
            }
            else
            {
                p_77906_0_ &= ~(1 << p_77906_1_);
            }
        }
        else
        {
            p_77906_0_ |= 1 << p_77906_1_;
        }

        return p_77906_0_;
    }

    /**
     * Returns the new potion damage value after the specified ingredient info is applied to the specified potion.
     */
    public static int applyIngredient(int p_77913_0_, String p_77913_1_)
    {
        int lvt_2_1_ = 0;
        int lvt_3_1_ = p_77913_1_.length();
        boolean lvt_4_1_ = false;
        boolean lvt_5_1_ = false;
        boolean lvt_6_1_ = false;
        boolean lvt_7_1_ = false;
        int lvt_8_1_ = 0;

        for (int lvt_9_1_ = lvt_2_1_; lvt_9_1_ < lvt_3_1_; ++lvt_9_1_)
        {
            char lvt_10_1_ = p_77913_1_.charAt(lvt_9_1_);

            if (lvt_10_1_ >= 48 && lvt_10_1_ <= 57)
            {
                lvt_8_1_ = lvt_8_1_ * 10;
                lvt_8_1_ = lvt_8_1_ + (lvt_10_1_ - 48);
                lvt_4_1_ = true;
            }
            else if (lvt_10_1_ == 33)
            {
                if (lvt_4_1_)
                {
                    p_77913_0_ = brewBitOperations(p_77913_0_, lvt_8_1_, lvt_6_1_, lvt_5_1_, lvt_7_1_);
                    lvt_7_1_ = false;
                    lvt_5_1_ = false;
                    lvt_6_1_ = false;
                    lvt_4_1_ = false;
                    lvt_8_1_ = 0;
                }

                lvt_5_1_ = true;
            }
            else if (lvt_10_1_ == 45)
            {
                if (lvt_4_1_)
                {
                    p_77913_0_ = brewBitOperations(p_77913_0_, lvt_8_1_, lvt_6_1_, lvt_5_1_, lvt_7_1_);
                    lvt_7_1_ = false;
                    lvt_5_1_ = false;
                    lvt_6_1_ = false;
                    lvt_4_1_ = false;
                    lvt_8_1_ = 0;
                }

                lvt_6_1_ = true;
            }
            else if (lvt_10_1_ == 43)
            {
                if (lvt_4_1_)
                {
                    p_77913_0_ = brewBitOperations(p_77913_0_, lvt_8_1_, lvt_6_1_, lvt_5_1_, lvt_7_1_);
                    lvt_7_1_ = false;
                    lvt_5_1_ = false;
                    lvt_6_1_ = false;
                    lvt_4_1_ = false;
                    lvt_8_1_ = 0;
                }
            }
            else if (lvt_10_1_ == 38)
            {
                if (lvt_4_1_)
                {
                    p_77913_0_ = brewBitOperations(p_77913_0_, lvt_8_1_, lvt_6_1_, lvt_5_1_, lvt_7_1_);
                    lvt_7_1_ = false;
                    lvt_5_1_ = false;
                    lvt_6_1_ = false;
                    lvt_4_1_ = false;
                    lvt_8_1_ = 0;
                }

                lvt_7_1_ = true;
            }
        }

        if (lvt_4_1_)
        {
            p_77913_0_ = brewBitOperations(p_77913_0_, lvt_8_1_, lvt_6_1_, lvt_5_1_, lvt_7_1_);
        }

        return p_77913_0_ & 32767;
    }

    public static int getPotionPrefixIndexFlags(int p_77908_0_, int p_77908_1_, int p_77908_2_, int p_77908_3_, int p_77908_4_, int p_77908_5_)
    {
        return (checkFlag(p_77908_0_, p_77908_1_) ? 16 : 0) | (checkFlag(p_77908_0_, p_77908_2_) ? 8 : 0) | (checkFlag(p_77908_0_, p_77908_3_) ? 4 : 0) | (checkFlag(p_77908_0_, p_77908_4_) ? 2 : 0) | (checkFlag(p_77908_0_, p_77908_5_) ? 1 : 0);
    }

    static
    {
        potionRequirements.put(Integer.valueOf(Potion.regeneration.getId()), "0 & !1 & !2 & !3 & 0+6");
        potionRequirements.put(Integer.valueOf(Potion.moveSpeed.getId()), "!0 & 1 & !2 & !3 & 1+6");
        potionRequirements.put(Integer.valueOf(Potion.fireResistance.getId()), "0 & 1 & !2 & !3 & 0+6");
        potionRequirements.put(Integer.valueOf(Potion.heal.getId()), "0 & !1 & 2 & !3");
        potionRequirements.put(Integer.valueOf(Potion.poison.getId()), "!0 & !1 & 2 & !3 & 2+6");
        potionRequirements.put(Integer.valueOf(Potion.weakness.getId()), "!0 & !1 & !2 & 3 & 3+6");
        potionRequirements.put(Integer.valueOf(Potion.harm.getId()), "!0 & !1 & 2 & 3");
        potionRequirements.put(Integer.valueOf(Potion.moveSlowdown.getId()), "!0 & 1 & !2 & 3 & 3+6");
        potionRequirements.put(Integer.valueOf(Potion.damageBoost.getId()), "0 & !1 & !2 & 3 & 3+6");
        potionRequirements.put(Integer.valueOf(Potion.nightVision.getId()), "!0 & 1 & 2 & !3 & 2+6");
        potionRequirements.put(Integer.valueOf(Potion.invisibility.getId()), "!0 & 1 & 2 & 3 & 2+6");
        potionRequirements.put(Integer.valueOf(Potion.waterBreathing.getId()), "0 & !1 & 2 & 3 & 2+6");
        potionRequirements.put(Integer.valueOf(Potion.jump.getId()), "0 & 1 & !2 & 3 & 3+6");
        potionAmplifiers.put(Integer.valueOf(Potion.moveSpeed.getId()), "5");
        potionAmplifiers.put(Integer.valueOf(Potion.digSpeed.getId()), "5");
        potionAmplifiers.put(Integer.valueOf(Potion.damageBoost.getId()), "5");
        potionAmplifiers.put(Integer.valueOf(Potion.regeneration.getId()), "5");
        potionAmplifiers.put(Integer.valueOf(Potion.harm.getId()), "5");
        potionAmplifiers.put(Integer.valueOf(Potion.heal.getId()), "5");
        potionAmplifiers.put(Integer.valueOf(Potion.resistance.getId()), "5");
        potionAmplifiers.put(Integer.valueOf(Potion.poison.getId()), "5");
        potionAmplifiers.put(Integer.valueOf(Potion.jump.getId()), "5");
    }
}
