package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class RecipeFireworks implements IRecipe
{
    private ItemStack field_92102_a;

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(InventoryCrafting inv, World worldIn)
    {
        this.field_92102_a = null;
        int lvt_3_1_ = 0;
        int lvt_4_1_ = 0;
        int lvt_5_1_ = 0;
        int lvt_6_1_ = 0;
        int lvt_7_1_ = 0;
        int lvt_8_1_ = 0;

        for (int lvt_9_1_ = 0; lvt_9_1_ < inv.getSizeInventory(); ++lvt_9_1_)
        {
            ItemStack lvt_10_1_ = inv.getStackInSlot(lvt_9_1_);

            if (lvt_10_1_ != null)
            {
                if (lvt_10_1_.getItem() == Items.gunpowder)
                {
                    ++lvt_4_1_;
                }
                else if (lvt_10_1_.getItem() == Items.firework_charge)
                {
                    ++lvt_6_1_;
                }
                else if (lvt_10_1_.getItem() == Items.dye)
                {
                    ++lvt_5_1_;
                }
                else if (lvt_10_1_.getItem() == Items.paper)
                {
                    ++lvt_3_1_;
                }
                else if (lvt_10_1_.getItem() == Items.glowstone_dust)
                {
                    ++lvt_7_1_;
                }
                else if (lvt_10_1_.getItem() == Items.diamond)
                {
                    ++lvt_7_1_;
                }
                else if (lvt_10_1_.getItem() == Items.fire_charge)
                {
                    ++lvt_8_1_;
                }
                else if (lvt_10_1_.getItem() == Items.feather)
                {
                    ++lvt_8_1_;
                }
                else if (lvt_10_1_.getItem() == Items.gold_nugget)
                {
                    ++lvt_8_1_;
                }
                else
                {
                    if (lvt_10_1_.getItem() != Items.skull)
                    {
                        return false;
                    }

                    ++lvt_8_1_;
                }
            }
        }

        lvt_7_1_ = lvt_7_1_ + lvt_5_1_ + lvt_8_1_;

        if (lvt_4_1_ <= 3 && lvt_3_1_ <= 1)
        {
            if (lvt_4_1_ >= 1 && lvt_3_1_ == 1 && lvt_7_1_ == 0)
            {
                this.field_92102_a = new ItemStack(Items.fireworks);

                if (lvt_6_1_ > 0)
                {
                    NBTTagCompound lvt_9_2_ = new NBTTagCompound();
                    NBTTagCompound lvt_10_2_ = new NBTTagCompound();
                    NBTTagList lvt_11_1_ = new NBTTagList();

                    for (int lvt_12_1_ = 0; lvt_12_1_ < inv.getSizeInventory(); ++lvt_12_1_)
                    {
                        ItemStack lvt_13_1_ = inv.getStackInSlot(lvt_12_1_);

                        if (lvt_13_1_ != null && lvt_13_1_.getItem() == Items.firework_charge && lvt_13_1_.hasTagCompound() && lvt_13_1_.getTagCompound().hasKey("Explosion", 10))
                        {
                            lvt_11_1_.appendTag(lvt_13_1_.getTagCompound().getCompoundTag("Explosion"));
                        }
                    }

                    lvt_10_2_.setTag("Explosions", lvt_11_1_);
                    lvt_10_2_.setByte("Flight", (byte)lvt_4_1_);
                    lvt_9_2_.setTag("Fireworks", lvt_10_2_);
                    this.field_92102_a.setTagCompound(lvt_9_2_);
                }

                return true;
            }
            else if (lvt_4_1_ == 1 && lvt_3_1_ == 0 && lvt_6_1_ == 0 && lvt_5_1_ > 0 && lvt_8_1_ <= 1)
            {
                this.field_92102_a = new ItemStack(Items.firework_charge);
                NBTTagCompound lvt_9_3_ = new NBTTagCompound();
                NBTTagCompound lvt_10_3_ = new NBTTagCompound();
                byte lvt_11_2_ = 0;
                List<Integer> lvt_12_2_ = Lists.newArrayList();

                for (int lvt_13_2_ = 0; lvt_13_2_ < inv.getSizeInventory(); ++lvt_13_2_)
                {
                    ItemStack lvt_14_1_ = inv.getStackInSlot(lvt_13_2_);

                    if (lvt_14_1_ != null)
                    {
                        if (lvt_14_1_.getItem() == Items.dye)
                        {
                            lvt_12_2_.add(Integer.valueOf(ItemDye.dyeColors[lvt_14_1_.getMetadata() & 15]));
                        }
                        else if (lvt_14_1_.getItem() == Items.glowstone_dust)
                        {
                            lvt_10_3_.setBoolean("Flicker", true);
                        }
                        else if (lvt_14_1_.getItem() == Items.diamond)
                        {
                            lvt_10_3_.setBoolean("Trail", true);
                        }
                        else if (lvt_14_1_.getItem() == Items.fire_charge)
                        {
                            lvt_11_2_ = 1;
                        }
                        else if (lvt_14_1_.getItem() == Items.feather)
                        {
                            lvt_11_2_ = 4;
                        }
                        else if (lvt_14_1_.getItem() == Items.gold_nugget)
                        {
                            lvt_11_2_ = 2;
                        }
                        else if (lvt_14_1_.getItem() == Items.skull)
                        {
                            lvt_11_2_ = 3;
                        }
                    }
                }

                int[] lvt_13_3_ = new int[lvt_12_2_.size()];

                for (int lvt_14_2_ = 0; lvt_14_2_ < lvt_13_3_.length; ++lvt_14_2_)
                {
                    lvt_13_3_[lvt_14_2_] = ((Integer)lvt_12_2_.get(lvt_14_2_)).intValue();
                }

                lvt_10_3_.setIntArray("Colors", lvt_13_3_);
                lvt_10_3_.setByte("Type", lvt_11_2_);
                lvt_9_3_.setTag("Explosion", lvt_10_3_);
                this.field_92102_a.setTagCompound(lvt_9_3_);
                return true;
            }
            else if (lvt_4_1_ == 0 && lvt_3_1_ == 0 && lvt_6_1_ == 1 && lvt_5_1_ > 0 && lvt_5_1_ == lvt_7_1_)
            {
                List<Integer> lvt_9_4_ = Lists.newArrayList();

                for (int lvt_10_4_ = 0; lvt_10_4_ < inv.getSizeInventory(); ++lvt_10_4_)
                {
                    ItemStack lvt_11_3_ = inv.getStackInSlot(lvt_10_4_);

                    if (lvt_11_3_ != null)
                    {
                        if (lvt_11_3_.getItem() == Items.dye)
                        {
                            lvt_9_4_.add(Integer.valueOf(ItemDye.dyeColors[lvt_11_3_.getMetadata() & 15]));
                        }
                        else if (lvt_11_3_.getItem() == Items.firework_charge)
                        {
                            this.field_92102_a = lvt_11_3_.copy();
                            this.field_92102_a.stackSize = 1;
                        }
                    }
                }

                int[] lvt_10_5_ = new int[lvt_9_4_.size()];

                for (int lvt_11_4_ = 0; lvt_11_4_ < lvt_10_5_.length; ++lvt_11_4_)
                {
                    lvt_10_5_[lvt_11_4_] = ((Integer)lvt_9_4_.get(lvt_11_4_)).intValue();
                }

                if (this.field_92102_a != null && this.field_92102_a.hasTagCompound())
                {
                    NBTTagCompound lvt_11_5_ = this.field_92102_a.getTagCompound().getCompoundTag("Explosion");

                    if (lvt_11_5_ == null)
                    {
                        return false;
                    }
                    else
                    {
                        lvt_11_5_.setIntArray("FadeColors", lvt_10_5_);
                        return true;
                    }
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        return this.field_92102_a.copy();
    }

    /**
     * Returns the size of the recipe area
     */
    public int getRecipeSize()
    {
        return 10;
    }

    public ItemStack getRecipeOutput()
    {
        return this.field_92102_a;
    }

    public ItemStack[] getRemainingItems(InventoryCrafting inv)
    {
        ItemStack[] lvt_2_1_ = new ItemStack[inv.getSizeInventory()];

        for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.length; ++lvt_3_1_)
        {
            ItemStack lvt_4_1_ = inv.getStackInSlot(lvt_3_1_);

            if (lvt_4_1_ != null && lvt_4_1_.getItem().hasContainerItem())
            {
                lvt_2_1_[lvt_3_1_] = new ItemStack(lvt_4_1_.getItem().getContainerItem());
            }
        }

        return lvt_2_1_;
    }
}
