package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class ItemWritableBook extends Item
{
    public ItemWritableBook()
    {
        this.setMaxStackSize(1);
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
        playerIn.displayGUIBook(itemStackIn);
        playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
        return itemStackIn;
    }

    /**
     * this method returns true if the book's NBT Tag List "pages" is valid
     */
    public static boolean isNBTValid(NBTTagCompound nbt)
    {
        if (nbt == null)
        {
            return false;
        }
        else if (!nbt.hasKey("pages", 9))
        {
            return false;
        }
        else
        {
            NBTTagList lvt_1_1_ = nbt.getTagList("pages", 8);

            for (int lvt_2_1_ = 0; lvt_2_1_ < lvt_1_1_.tagCount(); ++lvt_2_1_)
            {
                String lvt_3_1_ = lvt_1_1_.getStringTagAt(lvt_2_1_);

                if (lvt_3_1_ == null)
                {
                    return false;
                }

                if (lvt_3_1_.length() > 32767)
                {
                    return false;
                }
            }

            return true;
        }
    }
}
