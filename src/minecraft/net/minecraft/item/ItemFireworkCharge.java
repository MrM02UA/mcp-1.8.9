package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.StatCollector;

public class ItemFireworkCharge extends Item
{
    public int getColorFromItemStack(ItemStack stack, int renderPass)
    {
        if (renderPass != 1)
        {
            return super.getColorFromItemStack(stack, renderPass);
        }
        else
        {
            NBTBase lvt_3_1_ = getExplosionTag(stack, "Colors");

            if (!(lvt_3_1_ instanceof NBTTagIntArray))
            {
                return 9079434;
            }
            else
            {
                NBTTagIntArray lvt_4_1_ = (NBTTagIntArray)lvt_3_1_;
                int[] lvt_5_1_ = lvt_4_1_.getIntArray();

                if (lvt_5_1_.length == 1)
                {
                    return lvt_5_1_[0];
                }
                else
                {
                    int lvt_6_1_ = 0;
                    int lvt_7_1_ = 0;
                    int lvt_8_1_ = 0;

                    for (int lvt_12_1_ : lvt_5_1_)
                    {
                        lvt_6_1_ += (lvt_12_1_ & 16711680) >> 16;
                        lvt_7_1_ += (lvt_12_1_ & 65280) >> 8;
                        lvt_8_1_ += (lvt_12_1_ & 255) >> 0;
                    }

                    lvt_6_1_ = lvt_6_1_ / lvt_5_1_.length;
                    lvt_7_1_ = lvt_7_1_ / lvt_5_1_.length;
                    lvt_8_1_ = lvt_8_1_ / lvt_5_1_.length;
                    return lvt_6_1_ << 16 | lvt_7_1_ << 8 | lvt_8_1_;
                }
            }
        }
    }

    public static NBTBase getExplosionTag(ItemStack stack, String key)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound lvt_2_1_ = stack.getTagCompound().getCompoundTag("Explosion");

            if (lvt_2_1_ != null)
            {
                return lvt_2_1_.getTag(key);
            }
        }

        return null;
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound lvt_5_1_ = stack.getTagCompound().getCompoundTag("Explosion");

            if (lvt_5_1_ != null)
            {
                addExplosionInfo(lvt_5_1_, tooltip);
            }
        }
    }

    public static void addExplosionInfo(NBTTagCompound nbt, List<String> tooltip)
    {
        byte lvt_2_1_ = nbt.getByte("Type");

        if (lvt_2_1_ >= 0 && lvt_2_1_ <= 4)
        {
            tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.type." + lvt_2_1_).trim());
        }
        else
        {
            tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.type").trim());
        }

        int[] lvt_3_1_ = nbt.getIntArray("Colors");

        if (lvt_3_1_.length > 0)
        {
            boolean lvt_4_1_ = true;
            String lvt_5_1_ = "";

            for (int lvt_9_1_ : lvt_3_1_)
            {
                if (!lvt_4_1_)
                {
                    lvt_5_1_ = lvt_5_1_ + ", ";
                }

                lvt_4_1_ = false;
                boolean lvt_10_1_ = false;

                for (int lvt_11_1_ = 0; lvt_11_1_ < ItemDye.dyeColors.length; ++lvt_11_1_)
                {
                    if (lvt_9_1_ == ItemDye.dyeColors[lvt_11_1_])
                    {
                        lvt_10_1_ = true;
                        lvt_5_1_ = lvt_5_1_ + StatCollector.translateToLocal("item.fireworksCharge." + EnumDyeColor.byDyeDamage(lvt_11_1_).getUnlocalizedName());
                        break;
                    }
                }

                if (!lvt_10_1_)
                {
                    lvt_5_1_ = lvt_5_1_ + StatCollector.translateToLocal("item.fireworksCharge.customColor");
                }
            }

            tooltip.add(lvt_5_1_);
        }

        int[] lvt_4_2_ = nbt.getIntArray("FadeColors");

        if (lvt_4_2_.length > 0)
        {
            boolean lvt_5_2_ = true;
            String lvt_6_2_ = StatCollector.translateToLocal("item.fireworksCharge.fadeTo") + " ";

            for (int lvt_10_2_ : lvt_4_2_)
            {
                if (!lvt_5_2_)
                {
                    lvt_6_2_ = lvt_6_2_ + ", ";
                }

                lvt_5_2_ = false;
                boolean lvt_11_2_ = false;

                for (int lvt_12_1_ = 0; lvt_12_1_ < 16; ++lvt_12_1_)
                {
                    if (lvt_10_2_ == ItemDye.dyeColors[lvt_12_1_])
                    {
                        lvt_11_2_ = true;
                        lvt_6_2_ = lvt_6_2_ + StatCollector.translateToLocal("item.fireworksCharge." + EnumDyeColor.byDyeDamage(lvt_12_1_).getUnlocalizedName());
                        break;
                    }
                }

                if (!lvt_11_2_)
                {
                    lvt_6_2_ = lvt_6_2_ + StatCollector.translateToLocal("item.fireworksCharge.customColor");
                }
            }

            tooltip.add(lvt_6_2_);
        }

        boolean lvt_5_3_ = nbt.getBoolean("Trail");

        if (lvt_5_3_)
        {
            tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.trail"));
        }

        boolean lvt_6_3_ = nbt.getBoolean("Flicker");

        if (lvt_6_3_)
        {
            tooltip.add(StatCollector.translateToLocal("item.fireworksCharge.flicker"));
        }
    }
}
