package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemFirework extends Item
{
    /**
     * Called when a Block is right-clicked with this Item
     */
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            EntityFireworkRocket lvt_9_1_ = new EntityFireworkRocket(worldIn, (double)((float)pos.getX() + hitX), (double)((float)pos.getY() + hitY), (double)((float)pos.getZ() + hitZ), stack);
            worldIn.spawnEntityInWorld(lvt_9_1_);

            if (!playerIn.capabilities.isCreativeMode)
            {
                --stack.stackSize;
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound lvt_5_1_ = stack.getTagCompound().getCompoundTag("Fireworks");

            if (lvt_5_1_ != null)
            {
                if (lvt_5_1_.hasKey("Flight", 99))
                {
                    tooltip.add(StatCollector.translateToLocal("item.fireworks.flight") + " " + lvt_5_1_.getByte("Flight"));
                }

                NBTTagList lvt_6_1_ = lvt_5_1_.getTagList("Explosions", 10);

                if (lvt_6_1_ != null && lvt_6_1_.tagCount() > 0)
                {
                    for (int lvt_7_1_ = 0; lvt_7_1_ < lvt_6_1_.tagCount(); ++lvt_7_1_)
                    {
                        NBTTagCompound lvt_8_1_ = lvt_6_1_.getCompoundTagAt(lvt_7_1_);
                        List<String> lvt_9_1_ = Lists.newArrayList();
                        ItemFireworkCharge.addExplosionInfo(lvt_8_1_, lvt_9_1_);

                        if (lvt_9_1_.size() > 0)
                        {
                            for (int lvt_10_1_ = 1; lvt_10_1_ < ((List)lvt_9_1_).size(); ++lvt_10_1_)
                            {
                                lvt_9_1_.set(lvt_10_1_, "  " + (String)lvt_9_1_.get(lvt_10_1_));
                            }

                            tooltip.addAll(lvt_9_1_);
                        }
                    }
                }
            }
        }
    }
}
