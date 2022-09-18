package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;

public class ItemSaddle extends Item
{
    public ItemSaddle()
    {
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabTransport);
    }

    /**
     * Returns true if the item can be used on the given entity, e.g. shears on sheep.
     */
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target)
    {
        if (target instanceof EntityPig)
        {
            EntityPig lvt_4_1_ = (EntityPig)target;

            if (!lvt_4_1_.getSaddled() && !lvt_4_1_.isChild())
            {
                lvt_4_1_.setSaddled(true);
                lvt_4_1_.worldObj.playSoundAtEntity(lvt_4_1_, "mob.horse.leather", 0.5F, 1.0F);
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
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     */
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
    {
        this.itemInteractionForEntity(stack, (EntityPlayer)null, target);
        return true;
    }
}
