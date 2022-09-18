package net.minecraft.village;

import java.io.IOException;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;

public class MerchantRecipeList extends ArrayList<MerchantRecipe>
{
    public MerchantRecipeList()
    {
    }

    public MerchantRecipeList(NBTTagCompound compound)
    {
        this.readRecipiesFromTags(compound);
    }

    /**
     * can par1,par2 be used to in crafting recipe par3
     */
    public MerchantRecipe canRecipeBeUsed(ItemStack p_77203_1_, ItemStack p_77203_2_, int p_77203_3_)
    {
        if (p_77203_3_ > 0 && p_77203_3_ < this.size())
        {
            MerchantRecipe lvt_4_1_ = (MerchantRecipe)this.get(p_77203_3_);
            return !this.func_181078_a(p_77203_1_, lvt_4_1_.getItemToBuy()) || (p_77203_2_ != null || lvt_4_1_.hasSecondItemToBuy()) && (!lvt_4_1_.hasSecondItemToBuy() || !this.func_181078_a(p_77203_2_, lvt_4_1_.getSecondItemToBuy())) || p_77203_1_.stackSize < lvt_4_1_.getItemToBuy().stackSize || lvt_4_1_.hasSecondItemToBuy() && p_77203_2_.stackSize < lvt_4_1_.getSecondItemToBuy().stackSize ? null : lvt_4_1_;
        }
        else
        {
            for (int lvt_4_2_ = 0; lvt_4_2_ < this.size(); ++lvt_4_2_)
            {
                MerchantRecipe lvt_5_1_ = (MerchantRecipe)this.get(lvt_4_2_);

                if (this.func_181078_a(p_77203_1_, lvt_5_1_.getItemToBuy()) && p_77203_1_.stackSize >= lvt_5_1_.getItemToBuy().stackSize && (!lvt_5_1_.hasSecondItemToBuy() && p_77203_2_ == null || lvt_5_1_.hasSecondItemToBuy() && this.func_181078_a(p_77203_2_, lvt_5_1_.getSecondItemToBuy()) && p_77203_2_.stackSize >= lvt_5_1_.getSecondItemToBuy().stackSize))
                {
                    return lvt_5_1_;
                }
            }

            return null;
        }
    }

    private boolean func_181078_a(ItemStack p_181078_1_, ItemStack p_181078_2_)
    {
        return ItemStack.areItemsEqual(p_181078_1_, p_181078_2_) && (!p_181078_2_.hasTagCompound() || p_181078_1_.hasTagCompound() && NBTUtil.func_181123_a(p_181078_2_.getTagCompound(), p_181078_1_.getTagCompound(), false));
    }

    public void writeToBuf(PacketBuffer buffer)
    {
        buffer.writeByte((byte)(this.size() & 255));

        for (int lvt_2_1_ = 0; lvt_2_1_ < this.size(); ++lvt_2_1_)
        {
            MerchantRecipe lvt_3_1_ = (MerchantRecipe)this.get(lvt_2_1_);
            buffer.writeItemStackToBuffer(lvt_3_1_.getItemToBuy());
            buffer.writeItemStackToBuffer(lvt_3_1_.getItemToSell());
            ItemStack lvt_4_1_ = lvt_3_1_.getSecondItemToBuy();
            buffer.writeBoolean(lvt_4_1_ != null);

            if (lvt_4_1_ != null)
            {
                buffer.writeItemStackToBuffer(lvt_4_1_);
            }

            buffer.writeBoolean(lvt_3_1_.isRecipeDisabled());
            buffer.writeInt(lvt_3_1_.getToolUses());
            buffer.writeInt(lvt_3_1_.getMaxTradeUses());
        }
    }

    public static MerchantRecipeList readFromBuf(PacketBuffer buffer) throws IOException
    {
        MerchantRecipeList lvt_1_1_ = new MerchantRecipeList();
        int lvt_2_1_ = buffer.readByte() & 255;

        for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_; ++lvt_3_1_)
        {
            ItemStack lvt_4_1_ = buffer.readItemStackFromBuffer();
            ItemStack lvt_5_1_ = buffer.readItemStackFromBuffer();
            ItemStack lvt_6_1_ = null;

            if (buffer.readBoolean())
            {
                lvt_6_1_ = buffer.readItemStackFromBuffer();
            }

            boolean lvt_7_1_ = buffer.readBoolean();
            int lvt_8_1_ = buffer.readInt();
            int lvt_9_1_ = buffer.readInt();
            MerchantRecipe lvt_10_1_ = new MerchantRecipe(lvt_4_1_, lvt_6_1_, lvt_5_1_, lvt_8_1_, lvt_9_1_);

            if (lvt_7_1_)
            {
                lvt_10_1_.compensateToolUses();
            }

            lvt_1_1_.add(lvt_10_1_);
        }

        return lvt_1_1_;
    }

    public void readRecipiesFromTags(NBTTagCompound compound)
    {
        NBTTagList lvt_2_1_ = compound.getTagList("Recipes", 10);

        for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.tagCount(); ++lvt_3_1_)
        {
            NBTTagCompound lvt_4_1_ = lvt_2_1_.getCompoundTagAt(lvt_3_1_);
            this.add(new MerchantRecipe(lvt_4_1_));
        }
    }

    public NBTTagCompound getRecipiesAsTags()
    {
        NBTTagCompound lvt_1_1_ = new NBTTagCompound();
        NBTTagList lvt_2_1_ = new NBTTagList();

        for (int lvt_3_1_ = 0; lvt_3_1_ < this.size(); ++lvt_3_1_)
        {
            MerchantRecipe lvt_4_1_ = (MerchantRecipe)this.get(lvt_3_1_);
            lvt_2_1_.appendTag(lvt_4_1_.writeToTags());
        }

        lvt_1_1_.setTag("Recipes", lvt_2_1_);
        return lvt_1_1_;
    }
}
