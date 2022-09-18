package net.minecraft.nbt;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import net.minecraft.util.StringUtils;

public final class NBTUtil
{
    /**
     * Reads and returns a GameProfile that has been saved to the passed in NBTTagCompound
     */
    public static GameProfile readGameProfileFromNBT(NBTTagCompound compound)
    {
        String lvt_1_1_ = null;
        String lvt_2_1_ = null;

        if (compound.hasKey("Name", 8))
        {
            lvt_1_1_ = compound.getString("Name");
        }

        if (compound.hasKey("Id", 8))
        {
            lvt_2_1_ = compound.getString("Id");
        }

        if (StringUtils.isNullOrEmpty(lvt_1_1_) && StringUtils.isNullOrEmpty(lvt_2_1_))
        {
            return null;
        }
        else
        {
            UUID lvt_3_1_;

            try
            {
                lvt_3_1_ = UUID.fromString(lvt_2_1_);
            }
            catch (Throwable var12)
            {
                lvt_3_1_ = null;
            }

            GameProfile lvt_4_2_ = new GameProfile(lvt_3_1_, lvt_1_1_);

            if (compound.hasKey("Properties", 10))
            {
                NBTTagCompound lvt_5_1_ = compound.getCompoundTag("Properties");

                for (String lvt_7_1_ : lvt_5_1_.getKeySet())
                {
                    NBTTagList lvt_8_1_ = lvt_5_1_.getTagList(lvt_7_1_, 10);

                    for (int lvt_9_1_ = 0; lvt_9_1_ < lvt_8_1_.tagCount(); ++lvt_9_1_)
                    {
                        NBTTagCompound lvt_10_1_ = lvt_8_1_.getCompoundTagAt(lvt_9_1_);
                        String lvt_11_1_ = lvt_10_1_.getString("Value");

                        if (lvt_10_1_.hasKey("Signature", 8))
                        {
                            lvt_4_2_.getProperties().put(lvt_7_1_, new Property(lvt_7_1_, lvt_11_1_, lvt_10_1_.getString("Signature")));
                        }
                        else
                        {
                            lvt_4_2_.getProperties().put(lvt_7_1_, new Property(lvt_7_1_, lvt_11_1_));
                        }
                    }
                }
            }

            return lvt_4_2_;
        }
    }

    /**
     * Writes a GameProfile to an NBTTagCompound.
     */
    public static NBTTagCompound writeGameProfile(NBTTagCompound tagCompound, GameProfile profile)
    {
        if (!StringUtils.isNullOrEmpty(profile.getName()))
        {
            tagCompound.setString("Name", profile.getName());
        }

        if (profile.getId() != null)
        {
            tagCompound.setString("Id", profile.getId().toString());
        }

        if (!profile.getProperties().isEmpty())
        {
            NBTTagCompound lvt_2_1_ = new NBTTagCompound();

            for (String lvt_4_1_ : profile.getProperties().keySet())
            {
                NBTTagList lvt_5_1_ = new NBTTagList();

                for (Property lvt_7_1_ : profile.getProperties().get(lvt_4_1_))
                {
                    NBTTagCompound lvt_8_1_ = new NBTTagCompound();
                    lvt_8_1_.setString("Value", lvt_7_1_.getValue());

                    if (lvt_7_1_.hasSignature())
                    {
                        lvt_8_1_.setString("Signature", lvt_7_1_.getSignature());
                    }

                    lvt_5_1_.appendTag(lvt_8_1_);
                }

                lvt_2_1_.setTag(lvt_4_1_, lvt_5_1_);
            }

            tagCompound.setTag("Properties", lvt_2_1_);
        }

        return tagCompound;
    }

    public static boolean func_181123_a(NBTBase p_181123_0_, NBTBase p_181123_1_, boolean p_181123_2_)
    {
        if (p_181123_0_ == p_181123_1_)
        {
            return true;
        }
        else if (p_181123_0_ == null)
        {
            return true;
        }
        else if (p_181123_1_ == null)
        {
            return false;
        }
        else if (!p_181123_0_.getClass().equals(p_181123_1_.getClass()))
        {
            return false;
        }
        else if (p_181123_0_ instanceof NBTTagCompound)
        {
            NBTTagCompound lvt_3_1_ = (NBTTagCompound)p_181123_0_;
            NBTTagCompound lvt_4_1_ = (NBTTagCompound)p_181123_1_;

            for (String lvt_6_1_ : lvt_3_1_.getKeySet())
            {
                NBTBase lvt_7_1_ = lvt_3_1_.getTag(lvt_6_1_);

                if (!func_181123_a(lvt_7_1_, lvt_4_1_.getTag(lvt_6_1_), p_181123_2_))
                {
                    return false;
                }
            }

            return true;
        }
        else if (p_181123_0_ instanceof NBTTagList && p_181123_2_)
        {
            NBTTagList lvt_3_2_ = (NBTTagList)p_181123_0_;
            NBTTagList lvt_4_2_ = (NBTTagList)p_181123_1_;

            if (lvt_3_2_.tagCount() == 0)
            {
                return lvt_4_2_.tagCount() == 0;
            }
            else
            {
                for (int lvt_5_2_ = 0; lvt_5_2_ < lvt_3_2_.tagCount(); ++lvt_5_2_)
                {
                    NBTBase lvt_6_2_ = lvt_3_2_.get(lvt_5_2_);
                    boolean lvt_7_2_ = false;

                    for (int lvt_8_1_ = 0; lvt_8_1_ < lvt_4_2_.tagCount(); ++lvt_8_1_)
                    {
                        if (func_181123_a(lvt_6_2_, lvt_4_2_.get(lvt_8_1_), p_181123_2_))
                        {
                            lvt_7_2_ = true;
                            break;
                        }
                    }

                    if (!lvt_7_2_)
                    {
                        return false;
                    }
                }

                return true;
            }
        }
        else
        {
            return p_181123_0_.equals(p_181123_1_);
        }
    }
}
