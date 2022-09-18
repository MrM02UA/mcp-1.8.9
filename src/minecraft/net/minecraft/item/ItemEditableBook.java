package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentProcessor;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;

public class ItemEditableBook extends Item
{
    public ItemEditableBook()
    {
        this.setMaxStackSize(1);
    }

    public static boolean validBookTagContents(NBTTagCompound nbt)
    {
        if (!ItemWritableBook.isNBTValid(nbt))
        {
            return false;
        }
        else if (!nbt.hasKey("title", 8))
        {
            return false;
        }
        else
        {
            String lvt_1_1_ = nbt.getString("title");
            return lvt_1_1_ != null && lvt_1_1_.length() <= 32 ? nbt.hasKey("author", 8) : false;
        }
    }

    /**
     * Gets the generation of the book (how many times it has been cloned)
     */
    public static int getGeneration(ItemStack book)
    {
        return book.getTagCompound().getInteger("generation");
    }

    public String getItemStackDisplayName(ItemStack stack)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound lvt_2_1_ = stack.getTagCompound();
            String lvt_3_1_ = lvt_2_1_.getString("title");

            if (!StringUtils.isNullOrEmpty(lvt_3_1_))
            {
                return lvt_3_1_;
            }
        }

        return super.getItemStackDisplayName(stack);
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound lvt_5_1_ = stack.getTagCompound();
            String lvt_6_1_ = lvt_5_1_.getString("author");

            if (!StringUtils.isNullOrEmpty(lvt_6_1_))
            {
                tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocalFormatted("book.byAuthor", new Object[] {lvt_6_1_}));
            }

            tooltip.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("book.generation." + lvt_5_1_.getInteger("generation")));
        }
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
        if (!worldIn.isRemote)
        {
            this.resolveContents(itemStackIn, playerIn);
        }

        playerIn.displayGUIBook(itemStackIn);
        playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
        return itemStackIn;
    }

    private void resolveContents(ItemStack stack, EntityPlayer player)
    {
        if (stack != null && stack.getTagCompound() != null)
        {
            NBTTagCompound lvt_3_1_ = stack.getTagCompound();

            if (!lvt_3_1_.getBoolean("resolved"))
            {
                lvt_3_1_.setBoolean("resolved", true);

                if (validBookTagContents(lvt_3_1_))
                {
                    NBTTagList lvt_4_1_ = lvt_3_1_.getTagList("pages", 8);

                    for (int lvt_5_1_ = 0; lvt_5_1_ < lvt_4_1_.tagCount(); ++lvt_5_1_)
                    {
                        String lvt_6_1_ = lvt_4_1_.getStringTagAt(lvt_5_1_);
                        IChatComponent lvt_7_2_;

                        try
                        {
                            lvt_7_2_ = IChatComponent.Serializer.jsonToComponent(lvt_6_1_);
                            lvt_7_2_ = ChatComponentProcessor.processComponent(player, lvt_7_2_, player);
                        }
                        catch (Exception var9)
                        {
                            lvt_7_2_ = new ChatComponentText(lvt_6_1_);
                        }

                        lvt_4_1_.set(lvt_5_1_, new NBTTagString(IChatComponent.Serializer.componentToJson(lvt_7_2_)));
                    }

                    lvt_3_1_.setTag("pages", lvt_4_1_);

                    if (player instanceof EntityPlayerMP && player.getCurrentEquippedItem() == stack)
                    {
                        Slot lvt_5_2_ = player.openContainer.getSlotFromInventory(player.inventory, player.inventory.currentItem);
                        ((EntityPlayerMP)player).playerNetServerHandler.sendPacket(new S2FPacketSetSlot(0, lvt_5_2_.slotNumber, stack));
                    }
                }
            }
        }
    }

    public boolean hasEffect(ItemStack stack)
    {
        return true;
    }
}
