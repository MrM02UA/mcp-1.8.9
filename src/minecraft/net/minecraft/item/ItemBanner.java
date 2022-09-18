package net.minecraft.item;

import java.util.List;
import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockWallSign;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemBanner extends ItemBlock
{
    public ItemBanner()
    {
        super(Blocks.standing_banner);
        this.maxStackSize = 16;
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (side == EnumFacing.DOWN)
        {
            return false;
        }
        else if (!worldIn.getBlockState(pos).getBlock().getMaterial().isSolid())
        {
            return false;
        }
        else
        {
            pos = pos.offset(side);

            if (!playerIn.canPlayerEdit(pos, side, stack))
            {
                return false;
            }
            else if (!Blocks.standing_banner.canPlaceBlockAt(worldIn, pos))
            {
                return false;
            }
            else if (worldIn.isRemote)
            {
                return true;
            }
            else
            {
                if (side == EnumFacing.UP)
                {
                    int lvt_9_1_ = MathHelper.floor_double((double)((playerIn.rotationYaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;
                    worldIn.setBlockState(pos, Blocks.standing_banner.getDefaultState().withProperty(BlockStandingSign.ROTATION, Integer.valueOf(lvt_9_1_)), 3);
                }
                else
                {
                    worldIn.setBlockState(pos, Blocks.wall_banner.getDefaultState().withProperty(BlockWallSign.FACING, side), 3);
                }

                --stack.stackSize;
                TileEntity lvt_9_2_ = worldIn.getTileEntity(pos);

                if (lvt_9_2_ instanceof TileEntityBanner)
                {
                    ((TileEntityBanner)lvt_9_2_).setItemValues(stack);
                }

                return true;
            }
        }
    }

    public String getItemStackDisplayName(ItemStack stack)
    {
        String lvt_2_1_ = "item.banner.";
        EnumDyeColor lvt_3_1_ = this.getBaseColor(stack);
        lvt_2_1_ = lvt_2_1_ + lvt_3_1_.getUnlocalizedName() + ".name";
        return StatCollector.translateToLocal(lvt_2_1_);
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        NBTTagCompound lvt_5_1_ = stack.getSubCompound("BlockEntityTag", false);

        if (lvt_5_1_ != null && lvt_5_1_.hasKey("Patterns"))
        {
            NBTTagList lvt_6_1_ = lvt_5_1_.getTagList("Patterns", 10);

            for (int lvt_7_1_ = 0; lvt_7_1_ < lvt_6_1_.tagCount() && lvt_7_1_ < 6; ++lvt_7_1_)
            {
                NBTTagCompound lvt_8_1_ = lvt_6_1_.getCompoundTagAt(lvt_7_1_);
                EnumDyeColor lvt_9_1_ = EnumDyeColor.byDyeDamage(lvt_8_1_.getInteger("Color"));
                TileEntityBanner.EnumBannerPattern lvt_10_1_ = TileEntityBanner.EnumBannerPattern.getPatternByID(lvt_8_1_.getString("Pattern"));

                if (lvt_10_1_ != null)
                {
                    tooltip.add(StatCollector.translateToLocal("item.banner." + lvt_10_1_.getPatternName() + "." + lvt_9_1_.getUnlocalizedName()));
                }
            }
        }
    }

    public int getColorFromItemStack(ItemStack stack, int renderPass)
    {
        if (renderPass == 0)
        {
            return 16777215;
        }
        else
        {
            EnumDyeColor lvt_3_1_ = this.getBaseColor(stack);
            return lvt_3_1_.getMapColor().colorValue;
        }
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
    {
        for (EnumDyeColor lvt_7_1_ : EnumDyeColor.values())
        {
            NBTTagCompound lvt_8_1_ = new NBTTagCompound();
            TileEntityBanner.setBaseColorAndPatterns(lvt_8_1_, lvt_7_1_.getDyeDamage(), (NBTTagList)null);
            NBTTagCompound lvt_9_1_ = new NBTTagCompound();
            lvt_9_1_.setTag("BlockEntityTag", lvt_8_1_);
            ItemStack lvt_10_1_ = new ItemStack(itemIn, 1, lvt_7_1_.getDyeDamage());
            lvt_10_1_.setTagCompound(lvt_9_1_);
            subItems.add(lvt_10_1_);
        }
    }

    /**
     * gets the CreativeTab this item is displayed on
     */
    public CreativeTabs getCreativeTab()
    {
        return CreativeTabs.tabDecorations;
    }

    private EnumDyeColor getBaseColor(ItemStack stack)
    {
        NBTTagCompound lvt_2_1_ = stack.getSubCompound("BlockEntityTag", false);
        EnumDyeColor lvt_3_1_ = null;

        if (lvt_2_1_ != null && lvt_2_1_.hasKey("Base"))
        {
            lvt_3_1_ = EnumDyeColor.byDyeDamage(lvt_2_1_.getInteger("Base"));
        }
        else
        {
            lvt_3_1_ = EnumDyeColor.byDyeDamage(stack.getMetadata());
        }

        return lvt_3_1_;
    }
}
