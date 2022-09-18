package net.minecraft.item;

import com.google.common.base.Predicates;
import java.util.List;
import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.world.World;

public class ItemArmor extends Item
{
    /** Holds the 'base' maxDamage that each armorType have. */
    private static final int[] maxDamageArray = new int[] {11, 16, 15, 13};
    public static final String[] EMPTY_SLOT_NAMES = new String[] {"minecraft:items/empty_armor_slot_helmet", "minecraft:items/empty_armor_slot_chestplate", "minecraft:items/empty_armor_slot_leggings", "minecraft:items/empty_armor_slot_boots"};
    private static final IBehaviorDispenseItem dispenserBehavior = new BehaviorDefaultDispenseItem()
    {
        protected ItemStack dispenseStack(IBlockSource source, ItemStack stack)
        {
            BlockPos lvt_3_1_ = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
            int lvt_4_1_ = lvt_3_1_.getX();
            int lvt_5_1_ = lvt_3_1_.getY();
            int lvt_6_1_ = lvt_3_1_.getZ();
            AxisAlignedBB lvt_7_1_ = new AxisAlignedBB((double)lvt_4_1_, (double)lvt_5_1_, (double)lvt_6_1_, (double)(lvt_4_1_ + 1), (double)(lvt_5_1_ + 1), (double)(lvt_6_1_ + 1));
            List<EntityLivingBase> lvt_8_1_ = source.getWorld().<EntityLivingBase>getEntitiesWithinAABB(EntityLivingBase.class, lvt_7_1_, Predicates.and(EntitySelectors.NOT_SPECTATING, new EntitySelectors.ArmoredMob(stack)));

            if (lvt_8_1_.size() > 0)
            {
                EntityLivingBase lvt_9_1_ = (EntityLivingBase)lvt_8_1_.get(0);
                int lvt_10_1_ = lvt_9_1_ instanceof EntityPlayer ? 1 : 0;
                int lvt_11_1_ = EntityLiving.getArmorPosition(stack);
                ItemStack lvt_12_1_ = stack.copy();
                lvt_12_1_.stackSize = 1;
                lvt_9_1_.setCurrentItemOrArmor(lvt_11_1_ - lvt_10_1_, lvt_12_1_);

                if (lvt_9_1_ instanceof EntityLiving)
                {
                    ((EntityLiving)lvt_9_1_).setEquipmentDropChance(lvt_11_1_, 2.0F);
                }

                --stack.stackSize;
                return stack;
            }
            else
            {
                return super.dispenseStack(source, stack);
            }
        }
    };

    /**
     * Stores the armor type: 0 is helmet, 1 is plate, 2 is legs and 3 is boots
     */
    public final int armorType;

    /** Holds the amount of damage that the armor reduces at full durability. */
    public final int damageReduceAmount;

    /**
     * Used on RenderPlayer to select the correspondent armor to be rendered on the player: 0 is cloth, 1 is chain, 2 is
     * iron, 3 is diamond and 4 is gold.
     */
    public final int renderIndex;

    /** The EnumArmorMaterial used for this ItemArmor */
    private final ItemArmor.ArmorMaterial material;

    public ItemArmor(ItemArmor.ArmorMaterial material, int renderIndex, int armorType)
    {
        this.material = material;
        this.armorType = armorType;
        this.renderIndex = renderIndex;
        this.damageReduceAmount = material.getDamageReductionAmount(armorType);
        this.setMaxDamage(material.getDurability(armorType));
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabCombat);
        BlockDispenser.dispenseBehaviorRegistry.putObject(this, dispenserBehavior);
    }

    public int getColorFromItemStack(ItemStack stack, int renderPass)
    {
        if (renderPass > 0)
        {
            return 16777215;
        }
        else
        {
            int lvt_3_1_ = this.getColor(stack);

            if (lvt_3_1_ < 0)
            {
                lvt_3_1_ = 16777215;
            }

            return lvt_3_1_;
        }
    }

    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    public int getItemEnchantability()
    {
        return this.material.getEnchantability();
    }

    /**
     * Return the armor material for this armor item.
     */
    public ItemArmor.ArmorMaterial getArmorMaterial()
    {
        return this.material;
    }

    /**
     * Return whether the specified armor ItemStack has a color.
     */
    public boolean hasColor(ItemStack stack)
    {
        return this.material != ItemArmor.ArmorMaterial.LEATHER ? false : (!stack.hasTagCompound() ? false : (!stack.getTagCompound().hasKey("display", 10) ? false : stack.getTagCompound().getCompoundTag("display").hasKey("color", 3)));
    }

    /**
     * Return the color for the specified armor ItemStack.
     */
    public int getColor(ItemStack stack)
    {
        if (this.material != ItemArmor.ArmorMaterial.LEATHER)
        {
            return -1;
        }
        else
        {
            NBTTagCompound lvt_2_1_ = stack.getTagCompound();

            if (lvt_2_1_ != null)
            {
                NBTTagCompound lvt_3_1_ = lvt_2_1_.getCompoundTag("display");

                if (lvt_3_1_ != null && lvt_3_1_.hasKey("color", 3))
                {
                    return lvt_3_1_.getInteger("color");
                }
            }

            return 10511680;
        }
    }

    /**
     * Remove the color from the specified armor ItemStack.
     */
    public void removeColor(ItemStack stack)
    {
        if (this.material == ItemArmor.ArmorMaterial.LEATHER)
        {
            NBTTagCompound lvt_2_1_ = stack.getTagCompound();

            if (lvt_2_1_ != null)
            {
                NBTTagCompound lvt_3_1_ = lvt_2_1_.getCompoundTag("display");

                if (lvt_3_1_.hasKey("color"))
                {
                    lvt_3_1_.removeTag("color");
                }
            }
        }
    }

    /**
     * Sets the color of the specified armor ItemStack
     */
    public void setColor(ItemStack stack, int color)
    {
        if (this.material != ItemArmor.ArmorMaterial.LEATHER)
        {
            throw new UnsupportedOperationException("Can\'t dye non-leather!");
        }
        else
        {
            NBTTagCompound lvt_3_1_ = stack.getTagCompound();

            if (lvt_3_1_ == null)
            {
                lvt_3_1_ = new NBTTagCompound();
                stack.setTagCompound(lvt_3_1_);
            }

            NBTTagCompound lvt_4_1_ = lvt_3_1_.getCompoundTag("display");

            if (!lvt_3_1_.hasKey("display", 10))
            {
                lvt_3_1_.setTag("display", lvt_4_1_);
            }

            lvt_4_1_.setInteger("color", color);
        }
    }

    /**
     * Return whether this item is repairable in an anvil.
     */
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
    {
        return this.material.getRepairItem() == repair.getItem() ? true : super.getIsRepairable(toRepair, repair);
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
        int lvt_4_1_ = EntityLiving.getArmorPosition(itemStackIn) - 1;
        ItemStack lvt_5_1_ = playerIn.getCurrentArmor(lvt_4_1_);

        if (lvt_5_1_ == null)
        {
            playerIn.setCurrentItemOrArmor(lvt_4_1_, itemStackIn.copy());
            itemStackIn.stackSize = 0;
        }

        return itemStackIn;
    }

    public static enum ArmorMaterial
    {
        LEATHER("leather", 5, new int[]{1, 3, 2, 1}, 15),
        CHAIN("chainmail", 15, new int[]{2, 5, 4, 1}, 12),
        IRON("iron", 15, new int[]{2, 6, 5, 2}, 9),
        GOLD("gold", 7, new int[]{2, 5, 3, 1}, 25),
        DIAMOND("diamond", 33, new int[]{3, 8, 6, 3}, 10);

        private final String name;
        private final int maxDamageFactor;
        private final int[] damageReductionAmountArray;
        private final int enchantability;

        private ArmorMaterial(String name, int maxDamage, int[] reductionAmounts, int enchantability)
        {
            this.name = name;
            this.maxDamageFactor = maxDamage;
            this.damageReductionAmountArray = reductionAmounts;
            this.enchantability = enchantability;
        }

        public int getDurability(int armorType)
        {
            return ItemArmor.maxDamageArray[armorType] * this.maxDamageFactor;
        }

        public int getDamageReductionAmount(int armorType)
        {
            return this.damageReductionAmountArray[armorType];
        }

        public int getEnchantability()
        {
            return this.enchantability;
        }

        public Item getRepairItem()
        {
            return this == LEATHER ? Items.leather : (this == CHAIN ? Items.iron_ingot : (this == GOLD ? Items.gold_ingot : (this == IRON ? Items.iron_ingot : (this == DIAMOND ? Items.diamond : null))));
        }

        public String getName()
        {
            return this.name;
        }
    }
}
