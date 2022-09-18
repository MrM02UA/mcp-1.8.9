package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;

public class WeightedRandomChestContent extends WeightedRandom.Item
{
    /** The Item/Block ID to generate in the Chest. */
    private ItemStack theItemId;

    /** The minimum stack size of generated item. */
    private int minStackSize;

    /** The maximum stack size of generated item. */
    private int maxStackSize;

    public WeightedRandomChestContent(Item p_i45311_1_, int p_i45311_2_, int minimumChance, int maximumChance, int itemWeightIn)
    {
        super(itemWeightIn);
        this.theItemId = new ItemStack(p_i45311_1_, 1, p_i45311_2_);
        this.minStackSize = minimumChance;
        this.maxStackSize = maximumChance;
    }

    public WeightedRandomChestContent(ItemStack stack, int minimumChance, int maximumChance, int itemWeightIn)
    {
        super(itemWeightIn);
        this.theItemId = stack;
        this.minStackSize = minimumChance;
        this.maxStackSize = maximumChance;
    }

    public static void generateChestContents(Random random, List<WeightedRandomChestContent> listIn, IInventory inv, int max)
    {
        for (int lvt_4_1_ = 0; lvt_4_1_ < max; ++lvt_4_1_)
        {
            WeightedRandomChestContent lvt_5_1_ = (WeightedRandomChestContent)WeightedRandom.getRandomItem(random, listIn);
            int lvt_6_1_ = lvt_5_1_.minStackSize + random.nextInt(lvt_5_1_.maxStackSize - lvt_5_1_.minStackSize + 1);

            if (lvt_5_1_.theItemId.getMaxStackSize() >= lvt_6_1_)
            {
                ItemStack lvt_7_1_ = lvt_5_1_.theItemId.copy();
                lvt_7_1_.stackSize = lvt_6_1_;
                inv.setInventorySlotContents(random.nextInt(inv.getSizeInventory()), lvt_7_1_);
            }
            else
            {
                for (int lvt_7_2_ = 0; lvt_7_2_ < lvt_6_1_; ++lvt_7_2_)
                {
                    ItemStack lvt_8_1_ = lvt_5_1_.theItemId.copy();
                    lvt_8_1_.stackSize = 1;
                    inv.setInventorySlotContents(random.nextInt(inv.getSizeInventory()), lvt_8_1_);
                }
            }
        }
    }

    public static void generateDispenserContents(Random random, List<WeightedRandomChestContent> listIn, TileEntityDispenser dispenser, int max)
    {
        for (int lvt_4_1_ = 0; lvt_4_1_ < max; ++lvt_4_1_)
        {
            WeightedRandomChestContent lvt_5_1_ = (WeightedRandomChestContent)WeightedRandom.getRandomItem(random, listIn);
            int lvt_6_1_ = lvt_5_1_.minStackSize + random.nextInt(lvt_5_1_.maxStackSize - lvt_5_1_.minStackSize + 1);

            if (lvt_5_1_.theItemId.getMaxStackSize() >= lvt_6_1_)
            {
                ItemStack lvt_7_1_ = lvt_5_1_.theItemId.copy();
                lvt_7_1_.stackSize = lvt_6_1_;
                dispenser.setInventorySlotContents(random.nextInt(dispenser.getSizeInventory()), lvt_7_1_);
            }
            else
            {
                for (int lvt_7_2_ = 0; lvt_7_2_ < lvt_6_1_; ++lvt_7_2_)
                {
                    ItemStack lvt_8_1_ = lvt_5_1_.theItemId.copy();
                    lvt_8_1_.stackSize = 1;
                    dispenser.setInventorySlotContents(random.nextInt(dispenser.getSizeInventory()), lvt_8_1_);
                }
            }
        }
    }

    public static List<WeightedRandomChestContent> func_177629_a(List<WeightedRandomChestContent> p_177629_0_, WeightedRandomChestContent... p_177629_1_)
    {
        List<WeightedRandomChestContent> lvt_2_1_ = Lists.newArrayList(p_177629_0_);
        Collections.addAll(lvt_2_1_, p_177629_1_);
        return lvt_2_1_;
    }
}
