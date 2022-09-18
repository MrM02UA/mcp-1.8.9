package net.minecraft.inventory;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class InventoryHelper
{
    private static final Random RANDOM = new Random();

    public static void dropInventoryItems(World worldIn, BlockPos pos, IInventory inventory)
    {
        dropInventoryItems(worldIn, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), inventory);
    }

    public static void dropInventoryItems(World worldIn, Entity entityAt, IInventory inventory)
    {
        dropInventoryItems(worldIn, entityAt.posX, entityAt.posY, entityAt.posZ, inventory);
    }

    private static void dropInventoryItems(World worldIn, double x, double y, double z, IInventory inventory)
    {
        for (int lvt_8_1_ = 0; lvt_8_1_ < inventory.getSizeInventory(); ++lvt_8_1_)
        {
            ItemStack lvt_9_1_ = inventory.getStackInSlot(lvt_8_1_);

            if (lvt_9_1_ != null)
            {
                spawnItemStack(worldIn, x, y, z, lvt_9_1_);
            }
        }
    }

    private static void spawnItemStack(World worldIn, double x, double y, double z, ItemStack stack)
    {
        float lvt_8_1_ = RANDOM.nextFloat() * 0.8F + 0.1F;
        float lvt_9_1_ = RANDOM.nextFloat() * 0.8F + 0.1F;
        float lvt_10_1_ = RANDOM.nextFloat() * 0.8F + 0.1F;

        while (stack.stackSize > 0)
        {
            int lvt_11_1_ = RANDOM.nextInt(21) + 10;

            if (lvt_11_1_ > stack.stackSize)
            {
                lvt_11_1_ = stack.stackSize;
            }

            stack.stackSize -= lvt_11_1_;
            EntityItem lvt_12_1_ = new EntityItem(worldIn, x + (double)lvt_8_1_, y + (double)lvt_9_1_, z + (double)lvt_10_1_, new ItemStack(stack.getItem(), lvt_11_1_, stack.getMetadata()));

            if (stack.hasTagCompound())
            {
                lvt_12_1_.getEntityItem().setTagCompound((NBTTagCompound)stack.getTagCompound().copy());
            }

            float lvt_13_1_ = 0.05F;
            lvt_12_1_.motionX = RANDOM.nextGaussian() * (double)lvt_13_1_;
            lvt_12_1_.motionY = RANDOM.nextGaussian() * (double)lvt_13_1_ + 0.20000000298023224D;
            lvt_12_1_.motionZ = RANDOM.nextGaussian() * (double)lvt_13_1_;
            worldIn.spawnEntityInWorld(lvt_12_1_);
        }
    }
}
