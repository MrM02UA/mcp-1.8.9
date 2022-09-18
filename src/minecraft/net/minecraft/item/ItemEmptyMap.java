package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class ItemEmptyMap extends ItemMapBase
{
    protected ItemEmptyMap()
    {
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
        ItemStack lvt_4_1_ = new ItemStack(Items.filled_map, 1, worldIn.getUniqueDataId("map"));
        String lvt_5_1_ = "map_" + lvt_4_1_.getMetadata();
        MapData lvt_6_1_ = new MapData(lvt_5_1_);
        worldIn.setItemData(lvt_5_1_, lvt_6_1_);
        lvt_6_1_.scale = 0;
        lvt_6_1_.calculateMapCenter(playerIn.posX, playerIn.posZ, lvt_6_1_.scale);
        lvt_6_1_.dimension = (byte)worldIn.provider.getDimensionId();
        lvt_6_1_.markDirty();
        --itemStackIn.stackSize;

        if (itemStackIn.stackSize <= 0)
        {
            return lvt_4_1_;
        }
        else
        {
            if (!playerIn.inventory.addItemStackToInventory(lvt_4_1_.copy()))
            {
                playerIn.dropPlayerItemWithRandomChoice(lvt_4_1_, false);
            }

            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
            return itemStackIn;
        }
    }
}
