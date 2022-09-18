package net.minecraft.item.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.world.World;

public class RecipesBanners
{
    /**
     * Adds the banner recipes to the CraftingManager.
     */
    void addRecipes(CraftingManager p_179534_1_)
    {
        for (EnumDyeColor lvt_5_1_ : EnumDyeColor.values())
        {
            p_179534_1_.addRecipe(new ItemStack(Items.banner, 1, lvt_5_1_.getDyeDamage()), new Object[] {"###", "###", " | ", '#', new ItemStack(Blocks.wool, 1, lvt_5_1_.getMetadata()), '|', Items.stick});
        }

        p_179534_1_.addRecipe(new RecipesBanners.RecipeDuplicatePattern());
        p_179534_1_.addRecipe(new RecipesBanners.RecipeAddPattern());
    }

    static class RecipeAddPattern implements IRecipe
    {
        private RecipeAddPattern()
        {
        }

        public boolean matches(InventoryCrafting inv, World worldIn)
        {
            boolean lvt_3_1_ = false;

            for (int lvt_4_1_ = 0; lvt_4_1_ < inv.getSizeInventory(); ++lvt_4_1_)
            {
                ItemStack lvt_5_1_ = inv.getStackInSlot(lvt_4_1_);

                if (lvt_5_1_ != null && lvt_5_1_.getItem() == Items.banner)
                {
                    if (lvt_3_1_)
                    {
                        return false;
                    }

                    if (TileEntityBanner.getPatterns(lvt_5_1_) >= 6)
                    {
                        return false;
                    }

                    lvt_3_1_ = true;
                }
            }

            if (!lvt_3_1_)
            {
                return false;
            }
            else
            {
                return this.func_179533_c(inv) != null;
            }
        }

        public ItemStack getCraftingResult(InventoryCrafting inv)
        {
            ItemStack lvt_2_1_ = null;

            for (int lvt_3_1_ = 0; lvt_3_1_ < inv.getSizeInventory(); ++lvt_3_1_)
            {
                ItemStack lvt_4_1_ = inv.getStackInSlot(lvt_3_1_);

                if (lvt_4_1_ != null && lvt_4_1_.getItem() == Items.banner)
                {
                    lvt_2_1_ = lvt_4_1_.copy();
                    lvt_2_1_.stackSize = 1;
                    break;
                }
            }

            TileEntityBanner.EnumBannerPattern lvt_3_2_ = this.func_179533_c(inv);

            if (lvt_3_2_ != null)
            {
                int lvt_4_2_ = 0;

                for (int lvt_5_1_ = 0; lvt_5_1_ < inv.getSizeInventory(); ++lvt_5_1_)
                {
                    ItemStack lvt_6_1_ = inv.getStackInSlot(lvt_5_1_);

                    if (lvt_6_1_ != null && lvt_6_1_.getItem() == Items.dye)
                    {
                        lvt_4_2_ = lvt_6_1_.getMetadata();
                        break;
                    }
                }

                NBTTagCompound lvt_5_2_ = lvt_2_1_.getSubCompound("BlockEntityTag", true);
                NBTTagList lvt_6_2_ = null;

                if (lvt_5_2_.hasKey("Patterns", 9))
                {
                    lvt_6_2_ = lvt_5_2_.getTagList("Patterns", 10);
                }
                else
                {
                    lvt_6_2_ = new NBTTagList();
                    lvt_5_2_.setTag("Patterns", lvt_6_2_);
                }

                NBTTagCompound lvt_7_1_ = new NBTTagCompound();
                lvt_7_1_.setString("Pattern", lvt_3_2_.getPatternID());
                lvt_7_1_.setInteger("Color", lvt_4_2_);
                lvt_6_2_.appendTag(lvt_7_1_);
            }

            return lvt_2_1_;
        }

        public int getRecipeSize()
        {
            return 10;
        }

        public ItemStack getRecipeOutput()
        {
            return null;
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

        private TileEntityBanner.EnumBannerPattern func_179533_c(InventoryCrafting p_179533_1_)
        {
            for (TileEntityBanner.EnumBannerPattern lvt_5_1_ : TileEntityBanner.EnumBannerPattern.values())
            {
                if (lvt_5_1_.hasValidCrafting())
                {
                    boolean lvt_6_1_ = true;

                    if (lvt_5_1_.hasCraftingStack())
                    {
                        boolean lvt_7_1_ = false;
                        boolean lvt_8_1_ = false;

                        for (int lvt_9_1_ = 0; lvt_9_1_ < p_179533_1_.getSizeInventory() && lvt_6_1_; ++lvt_9_1_)
                        {
                            ItemStack lvt_10_1_ = p_179533_1_.getStackInSlot(lvt_9_1_);

                            if (lvt_10_1_ != null && lvt_10_1_.getItem() != Items.banner)
                            {
                                if (lvt_10_1_.getItem() == Items.dye)
                                {
                                    if (lvt_8_1_)
                                    {
                                        lvt_6_1_ = false;
                                        break;
                                    }

                                    lvt_8_1_ = true;
                                }
                                else
                                {
                                    if (lvt_7_1_ || !lvt_10_1_.isItemEqual(lvt_5_1_.getCraftingStack()))
                                    {
                                        lvt_6_1_ = false;
                                        break;
                                    }

                                    lvt_7_1_ = true;
                                }
                            }
                        }

                        if (!lvt_7_1_)
                        {
                            lvt_6_1_ = false;
                        }
                    }
                    else if (p_179533_1_.getSizeInventory() == lvt_5_1_.getCraftingLayers().length * lvt_5_1_.getCraftingLayers()[0].length())
                    {
                        int lvt_7_2_ = -1;

                        for (int lvt_8_2_ = 0; lvt_8_2_ < p_179533_1_.getSizeInventory() && lvt_6_1_; ++lvt_8_2_)
                        {
                            int lvt_9_2_ = lvt_8_2_ / 3;
                            int lvt_10_2_ = lvt_8_2_ % 3;
                            ItemStack lvt_11_1_ = p_179533_1_.getStackInSlot(lvt_8_2_);

                            if (lvt_11_1_ != null && lvt_11_1_.getItem() != Items.banner)
                            {
                                if (lvt_11_1_.getItem() != Items.dye)
                                {
                                    lvt_6_1_ = false;
                                    break;
                                }

                                if (lvt_7_2_ != -1 && lvt_7_2_ != lvt_11_1_.getMetadata())
                                {
                                    lvt_6_1_ = false;
                                    break;
                                }

                                if (lvt_5_1_.getCraftingLayers()[lvt_9_2_].charAt(lvt_10_2_) == 32)
                                {
                                    lvt_6_1_ = false;
                                    break;
                                }

                                lvt_7_2_ = lvt_11_1_.getMetadata();
                            }
                            else if (lvt_5_1_.getCraftingLayers()[lvt_9_2_].charAt(lvt_10_2_) != 32)
                            {
                                lvt_6_1_ = false;
                                break;
                            }
                        }
                    }
                    else
                    {
                        lvt_6_1_ = false;
                    }

                    if (lvt_6_1_)
                    {
                        return lvt_5_1_;
                    }
                }
            }

            return null;
        }
    }

    static class RecipeDuplicatePattern implements IRecipe
    {
        private RecipeDuplicatePattern()
        {
        }

        public boolean matches(InventoryCrafting inv, World worldIn)
        {
            ItemStack lvt_3_1_ = null;
            ItemStack lvt_4_1_ = null;

            for (int lvt_5_1_ = 0; lvt_5_1_ < inv.getSizeInventory(); ++lvt_5_1_)
            {
                ItemStack lvt_6_1_ = inv.getStackInSlot(lvt_5_1_);

                if (lvt_6_1_ != null)
                {
                    if (lvt_6_1_.getItem() != Items.banner)
                    {
                        return false;
                    }

                    if (lvt_3_1_ != null && lvt_4_1_ != null)
                    {
                        return false;
                    }

                    int lvt_7_1_ = TileEntityBanner.getBaseColor(lvt_6_1_);
                    boolean lvt_8_1_ = TileEntityBanner.getPatterns(lvt_6_1_) > 0;

                    if (lvt_3_1_ != null)
                    {
                        if (lvt_8_1_)
                        {
                            return false;
                        }

                        if (lvt_7_1_ != TileEntityBanner.getBaseColor(lvt_3_1_))
                        {
                            return false;
                        }

                        lvt_4_1_ = lvt_6_1_;
                    }
                    else if (lvt_4_1_ != null)
                    {
                        if (!lvt_8_1_)
                        {
                            return false;
                        }

                        if (lvt_7_1_ != TileEntityBanner.getBaseColor(lvt_4_1_))
                        {
                            return false;
                        }

                        lvt_3_1_ = lvt_6_1_;
                    }
                    else if (lvt_8_1_)
                    {
                        lvt_3_1_ = lvt_6_1_;
                    }
                    else
                    {
                        lvt_4_1_ = lvt_6_1_;
                    }
                }
            }

            return lvt_3_1_ != null && lvt_4_1_ != null;
        }

        public ItemStack getCraftingResult(InventoryCrafting inv)
        {
            for (int lvt_2_1_ = 0; lvt_2_1_ < inv.getSizeInventory(); ++lvt_2_1_)
            {
                ItemStack lvt_3_1_ = inv.getStackInSlot(lvt_2_1_);

                if (lvt_3_1_ != null && TileEntityBanner.getPatterns(lvt_3_1_) > 0)
                {
                    ItemStack lvt_4_1_ = lvt_3_1_.copy();
                    lvt_4_1_.stackSize = 1;
                    return lvt_4_1_;
                }
            }

            return null;
        }

        public int getRecipeSize()
        {
            return 2;
        }

        public ItemStack getRecipeOutput()
        {
            return null;
        }

        public ItemStack[] getRemainingItems(InventoryCrafting inv)
        {
            ItemStack[] lvt_2_1_ = new ItemStack[inv.getSizeInventory()];

            for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.length; ++lvt_3_1_)
            {
                ItemStack lvt_4_1_ = inv.getStackInSlot(lvt_3_1_);

                if (lvt_4_1_ != null)
                {
                    if (lvt_4_1_.getItem().hasContainerItem())
                    {
                        lvt_2_1_[lvt_3_1_] = new ItemStack(lvt_4_1_.getItem().getContainerItem());
                    }
                    else if (lvt_4_1_.hasTagCompound() && TileEntityBanner.getPatterns(lvt_4_1_) > 0)
                    {
                        lvt_2_1_[lvt_3_1_] = lvt_4_1_.copy();
                        lvt_2_1_[lvt_3_1_].stackSize = 1;
                    }
                }
            }

            return lvt_2_1_;
        }
    }
}
