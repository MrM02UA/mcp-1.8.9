package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.HoverEvent;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public final class ItemStack
{
    public static final DecimalFormat DECIMALFORMAT = new DecimalFormat("#.###");

    /** Size of the stack. */
    public int stackSize;

    /**
     * Number of animation frames to go when receiving an item (by walking into it, for example).
     */
    public int animationsToGo;
    private Item item;

    /**
     * A NBTTagMap containing data about an ItemStack. Can only be used for non stackable items
     */
    private NBTTagCompound stackTagCompound;
    private int itemDamage;

    /** Item frame this stack is on, or null if not on an item frame. */
    private EntityItemFrame itemFrame;
    private Block canDestroyCacheBlock;
    private boolean canDestroyCacheResult;
    private Block canPlaceOnCacheBlock;
    private boolean canPlaceOnCacheResult;

    public ItemStack(Block blockIn)
    {
        this((Block)blockIn, 1);
    }

    public ItemStack(Block blockIn, int amount)
    {
        this((Block)blockIn, amount, 0);
    }

    public ItemStack(Block blockIn, int amount, int meta)
    {
        this(Item.getItemFromBlock(blockIn), amount, meta);
    }

    public ItemStack(Item itemIn)
    {
        this((Item)itemIn, 1);
    }

    public ItemStack(Item itemIn, int amount)
    {
        this((Item)itemIn, amount, 0);
    }

    public ItemStack(Item itemIn, int amount, int meta)
    {
        this.canDestroyCacheBlock = null;
        this.canDestroyCacheResult = false;
        this.canPlaceOnCacheBlock = null;
        this.canPlaceOnCacheResult = false;
        this.item = itemIn;
        this.stackSize = amount;
        this.itemDamage = meta;

        if (this.itemDamage < 0)
        {
            this.itemDamage = 0;
        }
    }

    public static ItemStack loadItemStackFromNBT(NBTTagCompound nbt)
    {
        ItemStack lvt_1_1_ = new ItemStack();
        lvt_1_1_.readFromNBT(nbt);
        return lvt_1_1_.getItem() != null ? lvt_1_1_ : null;
    }

    private ItemStack()
    {
        this.canDestroyCacheBlock = null;
        this.canDestroyCacheResult = false;
        this.canPlaceOnCacheBlock = null;
        this.canPlaceOnCacheResult = false;
    }

    /**
     * Splits off a stack of the given amount of this stack and reduces this stack by the amount.
     */
    public ItemStack splitStack(int amount)
    {
        ItemStack lvt_2_1_ = new ItemStack(this.item, amount, this.itemDamage);

        if (this.stackTagCompound != null)
        {
            lvt_2_1_.stackTagCompound = (NBTTagCompound)this.stackTagCompound.copy();
        }

        this.stackSize -= amount;
        return lvt_2_1_;
    }

    /**
     * Returns the object corresponding to the stack.
     */
    public Item getItem()
    {
        return this.item;
    }

    /**
     * Called when the player uses this ItemStack on a Block (right-click). Places blocks, etc. (Legacy name:
     * tryPlaceItemIntoWorld)
     */
    public boolean onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        boolean lvt_8_1_ = this.getItem().onItemUse(this, playerIn, worldIn, pos, side, hitX, hitY, hitZ);

        if (lvt_8_1_)
        {
            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this.item)]);
        }

        return lvt_8_1_;
    }

    public float getStrVsBlock(Block blockIn)
    {
        return this.getItem().getStrVsBlock(this, blockIn);
    }

    /**
     * Called whenever this item stack is equipped and right clicked. Returns the new item stack to put in the position
     * where this item is. Args: world, player
     */
    public ItemStack useItemRightClick(World worldIn, EntityPlayer playerIn)
    {
        return this.getItem().onItemRightClick(this, worldIn, playerIn);
    }

    /**
     * Called when the item in use count reach 0, e.g. item food eaten. Return the new ItemStack. Args : world, entity
     */
    public ItemStack onItemUseFinish(World worldIn, EntityPlayer playerIn)
    {
        return this.getItem().onItemUseFinish(this, worldIn, playerIn);
    }

    /**
     * Write the stack fields to a NBT object. Return the new NBT object.
     */
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        ResourceLocation lvt_2_1_ = (ResourceLocation)Item.itemRegistry.getNameForObject(this.item);
        nbt.setString("id", lvt_2_1_ == null ? "minecraft:air" : lvt_2_1_.toString());
        nbt.setByte("Count", (byte)this.stackSize);
        nbt.setShort("Damage", (short)this.itemDamage);

        if (this.stackTagCompound != null)
        {
            nbt.setTag("tag", this.stackTagCompound);
        }

        return nbt;
    }

    /**
     * Read the stack fields from a NBT object.
     */
    public void readFromNBT(NBTTagCompound nbt)
    {
        if (nbt.hasKey("id", 8))
        {
            this.item = Item.getByNameOrId(nbt.getString("id"));
        }
        else
        {
            this.item = Item.getItemById(nbt.getShort("id"));
        }

        this.stackSize = nbt.getByte("Count");
        this.itemDamage = nbt.getShort("Damage");

        if (this.itemDamage < 0)
        {
            this.itemDamage = 0;
        }

        if (nbt.hasKey("tag", 10))
        {
            this.stackTagCompound = nbt.getCompoundTag("tag");

            if (this.item != null)
            {
                this.item.updateItemStackNBT(this.stackTagCompound);
            }
        }
    }

    /**
     * Returns maximum size of the stack.
     */
    public int getMaxStackSize()
    {
        return this.getItem().getItemStackLimit();
    }

    /**
     * Returns true if the ItemStack can hold 2 or more units of the item.
     */
    public boolean isStackable()
    {
        return this.getMaxStackSize() > 1 && (!this.isItemStackDamageable() || !this.isItemDamaged());
    }

    /**
     * true if this itemStack is damageable
     */
    public boolean isItemStackDamageable()
    {
        return this.item == null ? false : (this.item.getMaxDamage() <= 0 ? false : !this.hasTagCompound() || !this.getTagCompound().getBoolean("Unbreakable"));
    }

    public boolean getHasSubtypes()
    {
        return this.item.getHasSubtypes();
    }

    /**
     * returns true when a damageable item is damaged
     */
    public boolean isItemDamaged()
    {
        return this.isItemStackDamageable() && this.itemDamage > 0;
    }

    public int getItemDamage()
    {
        return this.itemDamage;
    }

    public int getMetadata()
    {
        return this.itemDamage;
    }

    public void setItemDamage(int meta)
    {
        this.itemDamage = meta;

        if (this.itemDamage < 0)
        {
            this.itemDamage = 0;
        }
    }

    /**
     * Returns the max damage an item in the stack can take.
     */
    public int getMaxDamage()
    {
        return this.item.getMaxDamage();
    }

    /**
     * Attempts to damage the ItemStack with par1 amount of damage, If the ItemStack has the Unbreaking enchantment
     * there is a chance for each point of damage to be negated. Returns true if it takes more damage than
     * getMaxDamage(). Returns false otherwise or if the ItemStack can't be damaged or if all points of damage are
     * negated.
     */
    public boolean attemptDamageItem(int amount, Random rand)
    {
        if (!this.isItemStackDamageable())
        {
            return false;
        }
        else
        {
            if (amount > 0)
            {
                int lvt_3_1_ = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, this);
                int lvt_4_1_ = 0;

                for (int lvt_5_1_ = 0; lvt_3_1_ > 0 && lvt_5_1_ < amount; ++lvt_5_1_)
                {
                    if (EnchantmentDurability.negateDamage(this, lvt_3_1_, rand))
                    {
                        ++lvt_4_1_;
                    }
                }

                amount -= lvt_4_1_;

                if (amount <= 0)
                {
                    return false;
                }
            }

            this.itemDamage += amount;
            return this.itemDamage > this.getMaxDamage();
        }
    }

    /**
     * Damages the item in the ItemStack
     */
    public void damageItem(int amount, EntityLivingBase entityIn)
    {
        if (!(entityIn instanceof EntityPlayer) || !((EntityPlayer)entityIn).capabilities.isCreativeMode)
        {
            if (this.isItemStackDamageable())
            {
                if (this.attemptDamageItem(amount, entityIn.getRNG()))
                {
                    entityIn.renderBrokenItemStack(this);
                    --this.stackSize;

                    if (entityIn instanceof EntityPlayer)
                    {
                        EntityPlayer lvt_3_1_ = (EntityPlayer)entityIn;
                        lvt_3_1_.triggerAchievement(StatList.objectBreakStats[Item.getIdFromItem(this.item)]);

                        if (this.stackSize == 0 && this.getItem() instanceof ItemBow)
                        {
                            lvt_3_1_.destroyCurrentEquippedItem();
                        }
                    }

                    if (this.stackSize < 0)
                    {
                        this.stackSize = 0;
                    }

                    this.itemDamage = 0;
                }
            }
        }
    }

    /**
     * Calls the corresponding fct in di
     */
    public void hitEntity(EntityLivingBase entityIn, EntityPlayer playerIn)
    {
        boolean lvt_3_1_ = this.item.hitEntity(this, entityIn, playerIn);

        if (lvt_3_1_)
        {
            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this.item)]);
        }
    }

    /**
     * Called when a Block is destroyed using this ItemStack
     */
    public void onBlockDestroyed(World worldIn, Block blockIn, BlockPos pos, EntityPlayer playerIn)
    {
        boolean lvt_5_1_ = this.item.onBlockDestroyed(this, worldIn, blockIn, pos, playerIn);

        if (lvt_5_1_)
        {
            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this.item)]);
        }
    }

    /**
     * Check whether the given Block can be harvested using this ItemStack.
     */
    public boolean canHarvestBlock(Block blockIn)
    {
        return this.item.canHarvestBlock(blockIn);
    }

    public boolean interactWithEntity(EntityPlayer playerIn, EntityLivingBase entityIn)
    {
        return this.item.itemInteractionForEntity(this, playerIn, entityIn);
    }

    /**
     * Returns a new stack with the same properties.
     */
    public ItemStack copy()
    {
        ItemStack lvt_1_1_ = new ItemStack(this.item, this.stackSize, this.itemDamage);

        if (this.stackTagCompound != null)
        {
            lvt_1_1_.stackTagCompound = (NBTTagCompound)this.stackTagCompound.copy();
        }

        return lvt_1_1_;
    }

    public static boolean areItemStackTagsEqual(ItemStack stackA, ItemStack stackB)
    {
        return stackA == null && stackB == null ? true : (stackA != null && stackB != null ? (stackA.stackTagCompound == null && stackB.stackTagCompound != null ? false : stackA.stackTagCompound == null || stackA.stackTagCompound.equals(stackB.stackTagCompound)) : false);
    }

    /**
     * compares ItemStack argument1 with ItemStack argument2; returns true if both ItemStacks are equal
     */
    public static boolean areItemStacksEqual(ItemStack stackA, ItemStack stackB)
    {
        return stackA == null && stackB == null ? true : (stackA != null && stackB != null ? stackA.isItemStackEqual(stackB) : false);
    }

    /**
     * compares ItemStack argument to the instance ItemStack; returns true if both ItemStacks are equal
     */
    private boolean isItemStackEqual(ItemStack other)
    {
        return this.stackSize != other.stackSize ? false : (this.item != other.item ? false : (this.itemDamage != other.itemDamage ? false : (this.stackTagCompound == null && other.stackTagCompound != null ? false : this.stackTagCompound == null || this.stackTagCompound.equals(other.stackTagCompound))));
    }

    /**
     * Compares Item and damage value of the two stacks
     */
    public static boolean areItemsEqual(ItemStack stackA, ItemStack stackB)
    {
        return stackA == null && stackB == null ? true : (stackA != null && stackB != null ? stackA.isItemEqual(stackB) : false);
    }

    /**
     * compares ItemStack argument to the instance ItemStack; returns true if the Items contained in both ItemStacks are
     * equal
     */
    public boolean isItemEqual(ItemStack other)
    {
        return other != null && this.item == other.item && this.itemDamage == other.itemDamage;
    }

    public String getUnlocalizedName()
    {
        return this.item.getUnlocalizedName(this);
    }

    /**
     * Creates a copy of a ItemStack, a null parameters will return a null.
     */
    public static ItemStack copyItemStack(ItemStack stack)
    {
        return stack == null ? null : stack.copy();
    }

    public String toString()
    {
        return this.stackSize + "x" + this.item.getUnlocalizedName() + "@" + this.itemDamage;
    }

    /**
     * Called each tick as long the ItemStack in on player inventory. Used to progress the pickup animation and update
     * maps.
     */
    public void updateAnimation(World worldIn, Entity entityIn, int inventorySlot, boolean isCurrentItem)
    {
        if (this.animationsToGo > 0)
        {
            --this.animationsToGo;
        }

        this.item.onUpdate(this, worldIn, entityIn, inventorySlot, isCurrentItem);
    }

    public void onCrafting(World worldIn, EntityPlayer playerIn, int amount)
    {
        playerIn.addStat(StatList.objectCraftStats[Item.getIdFromItem(this.item)], amount);
        this.item.onCreated(this, worldIn, playerIn);
    }

    public boolean getIsItemStackEqual(ItemStack p_179549_1_)
    {
        return this.isItemStackEqual(p_179549_1_);
    }

    public int getMaxItemUseDuration()
    {
        return this.getItem().getMaxItemUseDuration(this);
    }

    public EnumAction getItemUseAction()
    {
        return this.getItem().getItemUseAction(this);
    }

    /**
     * Called when the player releases the use item button. Args: world, entityplayer, itemInUseCount
     */
    public void onPlayerStoppedUsing(World worldIn, EntityPlayer playerIn, int timeLeft)
    {
        this.getItem().onPlayerStoppedUsing(this, worldIn, playerIn, timeLeft);
    }

    /**
     * Returns true if the ItemStack has an NBTTagCompound. Currently used to store enchantments.
     */
    public boolean hasTagCompound()
    {
        return this.stackTagCompound != null;
    }

    /**
     * Returns the NBTTagCompound of the ItemStack.
     */
    public NBTTagCompound getTagCompound()
    {
        return this.stackTagCompound;
    }

    /**
     * Get an NBTTagCompound from this stack's NBT data.
     */
    public NBTTagCompound getSubCompound(String key, boolean create)
    {
        if (this.stackTagCompound != null && this.stackTagCompound.hasKey(key, 10))
        {
            return this.stackTagCompound.getCompoundTag(key);
        }
        else if (create)
        {
            NBTTagCompound lvt_3_1_ = new NBTTagCompound();
            this.setTagInfo(key, lvt_3_1_);
            return lvt_3_1_;
        }
        else
        {
            return null;
        }
    }

    public NBTTagList getEnchantmentTagList()
    {
        return this.stackTagCompound == null ? null : this.stackTagCompound.getTagList("ench", 10);
    }

    /**
     * Assigns a NBTTagCompound to the ItemStack, minecraft validates that only non-stackable items can have it.
     */
    public void setTagCompound(NBTTagCompound nbt)
    {
        this.stackTagCompound = nbt;
    }

    /**
     * returns the display name of the itemstack
     */
    public String getDisplayName()
    {
        String lvt_1_1_ = this.getItem().getItemStackDisplayName(this);

        if (this.stackTagCompound != null && this.stackTagCompound.hasKey("display", 10))
        {
            NBTTagCompound lvt_2_1_ = this.stackTagCompound.getCompoundTag("display");

            if (lvt_2_1_.hasKey("Name", 8))
            {
                lvt_1_1_ = lvt_2_1_.getString("Name");
            }
        }

        return lvt_1_1_;
    }

    public ItemStack setStackDisplayName(String displayName)
    {
        if (this.stackTagCompound == null)
        {
            this.stackTagCompound = new NBTTagCompound();
        }

        if (!this.stackTagCompound.hasKey("display", 10))
        {
            this.stackTagCompound.setTag("display", new NBTTagCompound());
        }

        this.stackTagCompound.getCompoundTag("display").setString("Name", displayName);
        return this;
    }

    /**
     * Clear any custom name set for this ItemStack
     */
    public void clearCustomName()
    {
        if (this.stackTagCompound != null)
        {
            if (this.stackTagCompound.hasKey("display", 10))
            {
                NBTTagCompound lvt_1_1_ = this.stackTagCompound.getCompoundTag("display");
                lvt_1_1_.removeTag("Name");

                if (lvt_1_1_.hasNoTags())
                {
                    this.stackTagCompound.removeTag("display");

                    if (this.stackTagCompound.hasNoTags())
                    {
                        this.setTagCompound((NBTTagCompound)null);
                    }
                }
            }
        }
    }

    /**
     * Returns true if the itemstack has a display name
     */
    public boolean hasDisplayName()
    {
        return this.stackTagCompound == null ? false : (!this.stackTagCompound.hasKey("display", 10) ? false : this.stackTagCompound.getCompoundTag("display").hasKey("Name", 8));
    }

    public List<String> getTooltip(EntityPlayer playerIn, boolean advanced)
    {
        List<String> lvt_3_1_ = Lists.newArrayList();
        String lvt_4_1_ = this.getDisplayName();

        if (this.hasDisplayName())
        {
            lvt_4_1_ = EnumChatFormatting.ITALIC + lvt_4_1_;
        }

        lvt_4_1_ = lvt_4_1_ + EnumChatFormatting.RESET;

        if (advanced)
        {
            String lvt_5_1_ = "";

            if (lvt_4_1_.length() > 0)
            {
                lvt_4_1_ = lvt_4_1_ + " (";
                lvt_5_1_ = ")";
            }

            int lvt_6_1_ = Item.getIdFromItem(this.item);

            if (this.getHasSubtypes())
            {
                lvt_4_1_ = lvt_4_1_ + String.format("#%04d/%d%s", new Object[] {Integer.valueOf(lvt_6_1_), Integer.valueOf(this.itemDamage), lvt_5_1_});
            }
            else
            {
                lvt_4_1_ = lvt_4_1_ + String.format("#%04d%s", new Object[] {Integer.valueOf(lvt_6_1_), lvt_5_1_});
            }
        }
        else if (!this.hasDisplayName() && this.item == Items.filled_map)
        {
            lvt_4_1_ = lvt_4_1_ + " #" + this.itemDamage;
        }

        lvt_3_1_.add(lvt_4_1_);
        int lvt_5_2_ = 0;

        if (this.hasTagCompound() && this.stackTagCompound.hasKey("HideFlags", 99))
        {
            lvt_5_2_ = this.stackTagCompound.getInteger("HideFlags");
        }

        if ((lvt_5_2_ & 32) == 0)
        {
            this.item.addInformation(this, playerIn, lvt_3_1_, advanced);
        }

        if (this.hasTagCompound())
        {
            if ((lvt_5_2_ & 1) == 0)
            {
                NBTTagList lvt_6_2_ = this.getEnchantmentTagList();

                if (lvt_6_2_ != null)
                {
                    for (int lvt_7_1_ = 0; lvt_7_1_ < lvt_6_2_.tagCount(); ++lvt_7_1_)
                    {
                        int lvt_8_1_ = lvt_6_2_.getCompoundTagAt(lvt_7_1_).getShort("id");
                        int lvt_9_1_ = lvt_6_2_.getCompoundTagAt(lvt_7_1_).getShort("lvl");

                        if (Enchantment.getEnchantmentById(lvt_8_1_) != null)
                        {
                            lvt_3_1_.add(Enchantment.getEnchantmentById(lvt_8_1_).getTranslatedName(lvt_9_1_));
                        }
                    }
                }
            }

            if (this.stackTagCompound.hasKey("display", 10))
            {
                NBTTagCompound lvt_6_3_ = this.stackTagCompound.getCompoundTag("display");

                if (lvt_6_3_.hasKey("color", 3))
                {
                    if (advanced)
                    {
                        lvt_3_1_.add("Color: #" + Integer.toHexString(lvt_6_3_.getInteger("color")).toUpperCase());
                    }
                    else
                    {
                        lvt_3_1_.add(EnumChatFormatting.ITALIC + StatCollector.translateToLocal("item.dyed"));
                    }
                }

                if (lvt_6_3_.getTagId("Lore") == 9)
                {
                    NBTTagList lvt_7_2_ = lvt_6_3_.getTagList("Lore", 8);

                    if (lvt_7_2_.tagCount() > 0)
                    {
                        for (int lvt_8_2_ = 0; lvt_8_2_ < lvt_7_2_.tagCount(); ++lvt_8_2_)
                        {
                            lvt_3_1_.add(EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.ITALIC + lvt_7_2_.getStringTagAt(lvt_8_2_));
                        }
                    }
                }
            }
        }

        Multimap<String, AttributeModifier> lvt_6_4_ = this.getAttributeModifiers();

        if (!lvt_6_4_.isEmpty() && (lvt_5_2_ & 2) == 0)
        {
            lvt_3_1_.add("");

            for (Entry<String, AttributeModifier> lvt_8_3_ : lvt_6_4_.entries())
            {
                AttributeModifier lvt_9_2_ = (AttributeModifier)lvt_8_3_.getValue();
                double lvt_10_1_ = lvt_9_2_.getAmount();

                if (lvt_9_2_.getID() == Item.itemModifierUUID)
                {
                    lvt_10_1_ += (double)EnchantmentHelper.getModifierForCreature(this, EnumCreatureAttribute.UNDEFINED);
                }

                double lvt_12_2_;

                if (lvt_9_2_.getOperation() != 1 && lvt_9_2_.getOperation() != 2)
                {
                    lvt_12_2_ = lvt_10_1_;
                }
                else
                {
                    lvt_12_2_ = lvt_10_1_ * 100.0D;
                }

                if (lvt_10_1_ > 0.0D)
                {
                    lvt_3_1_.add(EnumChatFormatting.BLUE + StatCollector.translateToLocalFormatted("attribute.modifier.plus." + lvt_9_2_.getOperation(), new Object[] {DECIMALFORMAT.format(lvt_12_2_), StatCollector.translateToLocal("attribute.name." + (String)lvt_8_3_.getKey())}));
                }
                else if (lvt_10_1_ < 0.0D)
                {
                    lvt_12_2_ = lvt_12_2_ * -1.0D;
                    lvt_3_1_.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("attribute.modifier.take." + lvt_9_2_.getOperation(), new Object[] {DECIMALFORMAT.format(lvt_12_2_), StatCollector.translateToLocal("attribute.name." + (String)lvt_8_3_.getKey())}));
                }
            }
        }

        if (this.hasTagCompound() && this.getTagCompound().getBoolean("Unbreakable") && (lvt_5_2_ & 4) == 0)
        {
            lvt_3_1_.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("item.unbreakable"));
        }

        if (this.hasTagCompound() && this.stackTagCompound.hasKey("CanDestroy", 9) && (lvt_5_2_ & 8) == 0)
        {
            NBTTagList lvt_7_4_ = this.stackTagCompound.getTagList("CanDestroy", 8);

            if (lvt_7_4_.tagCount() > 0)
            {
                lvt_3_1_.add("");
                lvt_3_1_.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("item.canBreak"));

                for (int lvt_8_4_ = 0; lvt_8_4_ < lvt_7_4_.tagCount(); ++lvt_8_4_)
                {
                    Block lvt_9_3_ = Block.getBlockFromName(lvt_7_4_.getStringTagAt(lvt_8_4_));

                    if (lvt_9_3_ != null)
                    {
                        lvt_3_1_.add(EnumChatFormatting.DARK_GRAY + lvt_9_3_.getLocalizedName());
                    }
                    else
                    {
                        lvt_3_1_.add(EnumChatFormatting.DARK_GRAY + "missingno");
                    }
                }
            }
        }

        if (this.hasTagCompound() && this.stackTagCompound.hasKey("CanPlaceOn", 9) && (lvt_5_2_ & 16) == 0)
        {
            NBTTagList lvt_7_5_ = this.stackTagCompound.getTagList("CanPlaceOn", 8);

            if (lvt_7_5_.tagCount() > 0)
            {
                lvt_3_1_.add("");
                lvt_3_1_.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("item.canPlace"));

                for (int lvt_8_5_ = 0; lvt_8_5_ < lvt_7_5_.tagCount(); ++lvt_8_5_)
                {
                    Block lvt_9_4_ = Block.getBlockFromName(lvt_7_5_.getStringTagAt(lvt_8_5_));

                    if (lvt_9_4_ != null)
                    {
                        lvt_3_1_.add(EnumChatFormatting.DARK_GRAY + lvt_9_4_.getLocalizedName());
                    }
                    else
                    {
                        lvt_3_1_.add(EnumChatFormatting.DARK_GRAY + "missingno");
                    }
                }
            }
        }

        if (advanced)
        {
            if (this.isItemDamaged())
            {
                lvt_3_1_.add("Durability: " + (this.getMaxDamage() - this.getItemDamage()) + " / " + this.getMaxDamage());
            }

            lvt_3_1_.add(EnumChatFormatting.DARK_GRAY + ((ResourceLocation)Item.itemRegistry.getNameForObject(this.item)).toString());

            if (this.hasTagCompound())
            {
                lvt_3_1_.add(EnumChatFormatting.DARK_GRAY + "NBT: " + this.getTagCompound().getKeySet().size() + " tag(s)");
            }
        }

        return lvt_3_1_;
    }

    public boolean hasEffect()
    {
        return this.getItem().hasEffect(this);
    }

    public EnumRarity getRarity()
    {
        return this.getItem().getRarity(this);
    }

    /**
     * True if it is a tool and has no enchantments to begin with
     */
    public boolean isItemEnchantable()
    {
        return !this.getItem().isItemTool(this) ? false : !this.isItemEnchanted();
    }

    /**
     * Adds an enchantment with a desired level on the ItemStack.
     */
    public void addEnchantment(Enchantment ench, int level)
    {
        if (this.stackTagCompound == null)
        {
            this.setTagCompound(new NBTTagCompound());
        }

        if (!this.stackTagCompound.hasKey("ench", 9))
        {
            this.stackTagCompound.setTag("ench", new NBTTagList());
        }

        NBTTagList lvt_3_1_ = this.stackTagCompound.getTagList("ench", 10);
        NBTTagCompound lvt_4_1_ = new NBTTagCompound();
        lvt_4_1_.setShort("id", (short)ench.effectId);
        lvt_4_1_.setShort("lvl", (short)((byte)level));
        lvt_3_1_.appendTag(lvt_4_1_);
    }

    /**
     * True if the item has enchantment data
     */
    public boolean isItemEnchanted()
    {
        return this.stackTagCompound != null && this.stackTagCompound.hasKey("ench", 9);
    }

    public void setTagInfo(String key, NBTBase value)
    {
        if (this.stackTagCompound == null)
        {
            this.setTagCompound(new NBTTagCompound());
        }

        this.stackTagCompound.setTag(key, value);
    }

    public boolean canEditBlocks()
    {
        return this.getItem().canItemEditBlocks();
    }

    /**
     * Return whether this stack is on an item frame.
     */
    public boolean isOnItemFrame()
    {
        return this.itemFrame != null;
    }

    /**
     * Set the item frame this stack is on.
     */
    public void setItemFrame(EntityItemFrame frame)
    {
        this.itemFrame = frame;
    }

    /**
     * Return the item frame this stack is on. Returns null if not on an item frame.
     */
    public EntityItemFrame getItemFrame()
    {
        return this.itemFrame;
    }

    /**
     * Get this stack's repair cost, or 0 if no repair cost is defined.
     */
    public int getRepairCost()
    {
        return this.hasTagCompound() && this.stackTagCompound.hasKey("RepairCost", 3) ? this.stackTagCompound.getInteger("RepairCost") : 0;
    }

    /**
     * Set this stack's repair cost.
     */
    public void setRepairCost(int cost)
    {
        if (!this.hasTagCompound())
        {
            this.stackTagCompound = new NBTTagCompound();
        }

        this.stackTagCompound.setInteger("RepairCost", cost);
    }

    public Multimap<String, AttributeModifier> getAttributeModifiers()
    {
        Multimap<String, AttributeModifier> lvt_1_1_;

        if (this.hasTagCompound() && this.stackTagCompound.hasKey("AttributeModifiers", 9))
        {
            lvt_1_1_ = HashMultimap.create();
            NBTTagList lvt_2_1_ = this.stackTagCompound.getTagList("AttributeModifiers", 10);

            for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.tagCount(); ++lvt_3_1_)
            {
                NBTTagCompound lvt_4_1_ = lvt_2_1_.getCompoundTagAt(lvt_3_1_);
                AttributeModifier lvt_5_1_ = SharedMonsterAttributes.readAttributeModifierFromNBT(lvt_4_1_);

                if (lvt_5_1_ != null && lvt_5_1_.getID().getLeastSignificantBits() != 0L && lvt_5_1_.getID().getMostSignificantBits() != 0L)
                {
                    lvt_1_1_.put(lvt_4_1_.getString("AttributeName"), lvt_5_1_);
                }
            }
        }
        else
        {
            lvt_1_1_ = this.getItem().getItemAttributeModifiers();
        }

        return lvt_1_1_;
    }

    public void setItem(Item newItem)
    {
        this.item = newItem;
    }

    /**
     * Get a ChatComponent for this Item's display name that shows this Item on hover
     */
    public IChatComponent getChatComponent()
    {
        ChatComponentText lvt_1_1_ = new ChatComponentText(this.getDisplayName());

        if (this.hasDisplayName())
        {
            lvt_1_1_.getChatStyle().setItalic(Boolean.valueOf(true));
        }

        IChatComponent lvt_2_1_ = (new ChatComponentText("[")).appendSibling(lvt_1_1_).appendText("]");

        if (this.item != null)
        {
            NBTTagCompound lvt_3_1_ = new NBTTagCompound();
            this.writeToNBT(lvt_3_1_);
            lvt_2_1_.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ChatComponentText(lvt_3_1_.toString())));
            lvt_2_1_.getChatStyle().setColor(this.getRarity().rarityColor);
        }

        return lvt_2_1_;
    }

    public boolean canDestroy(Block blockIn)
    {
        if (blockIn == this.canDestroyCacheBlock)
        {
            return this.canDestroyCacheResult;
        }
        else
        {
            this.canDestroyCacheBlock = blockIn;

            if (this.hasTagCompound() && this.stackTagCompound.hasKey("CanDestroy", 9))
            {
                NBTTagList lvt_2_1_ = this.stackTagCompound.getTagList("CanDestroy", 8);

                for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.tagCount(); ++lvt_3_1_)
                {
                    Block lvt_4_1_ = Block.getBlockFromName(lvt_2_1_.getStringTagAt(lvt_3_1_));

                    if (lvt_4_1_ == blockIn)
                    {
                        this.canDestroyCacheResult = true;
                        return true;
                    }
                }
            }

            this.canDestroyCacheResult = false;
            return false;
        }
    }

    public boolean canPlaceOn(Block blockIn)
    {
        if (blockIn == this.canPlaceOnCacheBlock)
        {
            return this.canPlaceOnCacheResult;
        }
        else
        {
            this.canPlaceOnCacheBlock = blockIn;

            if (this.hasTagCompound() && this.stackTagCompound.hasKey("CanPlaceOn", 9))
            {
                NBTTagList lvt_2_1_ = this.stackTagCompound.getTagList("CanPlaceOn", 8);

                for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.tagCount(); ++lvt_3_1_)
                {
                    Block lvt_4_1_ = Block.getBlockFromName(lvt_2_1_.getStringTagAt(lvt_3_1_));

                    if (lvt_4_1_ == blockIn)
                    {
                        this.canPlaceOnCacheResult = true;
                        return true;
                    }
                }
            }

            this.canPlaceOnCacheResult = false;
            return false;
        }
    }
}
