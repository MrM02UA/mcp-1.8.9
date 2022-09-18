package net.minecraft.entity.passive;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityMooshroom extends EntityCow
{
    public EntityMooshroom(World worldIn)
    {
        super(worldIn);
        this.setSize(0.9F, 1.3F);
        this.spawnableBlock = Blocks.mycelium;
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    public boolean interact(EntityPlayer player)
    {
        ItemStack lvt_2_1_ = player.inventory.getCurrentItem();

        if (lvt_2_1_ != null && lvt_2_1_.getItem() == Items.bowl && this.getGrowingAge() >= 0)
        {
            if (lvt_2_1_.stackSize == 1)
            {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.mushroom_stew));
                return true;
            }

            if (player.inventory.addItemStackToInventory(new ItemStack(Items.mushroom_stew)) && !player.capabilities.isCreativeMode)
            {
                player.inventory.decrStackSize(player.inventory.currentItem, 1);
                return true;
            }
        }

        if (lvt_2_1_ != null && lvt_2_1_.getItem() == Items.shears && this.getGrowingAge() >= 0)
        {
            this.setDead();
            this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.posX, this.posY + (double)(this.height / 2.0F), this.posZ, 0.0D, 0.0D, 0.0D, new int[0]);

            if (!this.worldObj.isRemote)
            {
                EntityCow lvt_3_1_ = new EntityCow(this.worldObj);
                lvt_3_1_.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
                lvt_3_1_.setHealth(this.getHealth());
                lvt_3_1_.renderYawOffset = this.renderYawOffset;

                if (this.hasCustomName())
                {
                    lvt_3_1_.setCustomNameTag(this.getCustomNameTag());
                }

                this.worldObj.spawnEntityInWorld(lvt_3_1_);

                for (int lvt_4_1_ = 0; lvt_4_1_ < 5; ++lvt_4_1_)
                {
                    this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.posX, this.posY + (double)this.height, this.posZ, new ItemStack(Blocks.red_mushroom)));
                }

                lvt_2_1_.damageItem(1, player);
                this.playSound("mob.sheep.shear", 1.0F, 1.0F);
            }

            return true;
        }
        else
        {
            return super.interact(player);
        }
    }

    public EntityMooshroom createChild(EntityAgeable ageable)
    {
        return new EntityMooshroom(this.worldObj);
    }

    public EntityCow createChild(EntityAgeable ageable)
    {
        return this.createChild(ageable);
    }

    public EntityAgeable createChild(EntityAgeable ageable)
    {
        return this.createChild(ageable);
    }
}
