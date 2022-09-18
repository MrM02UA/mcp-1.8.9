package net.minecraft.item;

import com.mojang.authlib.GameProfile;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemSkull extends Item
{
    private static final String[] skullTypes = new String[] {"skeleton", "wither", "zombie", "char", "creeper"};

    public ItemSkull()
    {
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
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
        else
        {
            IBlockState lvt_9_1_ = worldIn.getBlockState(pos);
            Block lvt_10_1_ = lvt_9_1_.getBlock();
            boolean lvt_11_1_ = lvt_10_1_.isReplaceable(worldIn, pos);

            if (!lvt_11_1_)
            {
                if (!worldIn.getBlockState(pos).getBlock().getMaterial().isSolid())
                {
                    return false;
                }

                pos = pos.offset(side);
            }

            if (!playerIn.canPlayerEdit(pos, side, stack))
            {
                return false;
            }
            else if (!Blocks.skull.canPlaceBlockAt(worldIn, pos))
            {
                return false;
            }
            else
            {
                if (!worldIn.isRemote)
                {
                    worldIn.setBlockState(pos, Blocks.skull.getDefaultState().withProperty(BlockSkull.FACING, side), 3);
                    int lvt_12_1_ = 0;

                    if (side == EnumFacing.UP)
                    {
                        lvt_12_1_ = MathHelper.floor_double((double)(playerIn.rotationYaw * 16.0F / 360.0F) + 0.5D) & 15;
                    }

                    TileEntity lvt_13_1_ = worldIn.getTileEntity(pos);

                    if (lvt_13_1_ instanceof TileEntitySkull)
                    {
                        TileEntitySkull lvt_14_1_ = (TileEntitySkull)lvt_13_1_;

                        if (stack.getMetadata() == 3)
                        {
                            GameProfile lvt_15_1_ = null;

                            if (stack.hasTagCompound())
                            {
                                NBTTagCompound lvt_16_1_ = stack.getTagCompound();

                                if (lvt_16_1_.hasKey("SkullOwner", 10))
                                {
                                    lvt_15_1_ = NBTUtil.readGameProfileFromNBT(lvt_16_1_.getCompoundTag("SkullOwner"));
                                }
                                else if (lvt_16_1_.hasKey("SkullOwner", 8) && lvt_16_1_.getString("SkullOwner").length() > 0)
                                {
                                    lvt_15_1_ = new GameProfile((UUID)null, lvt_16_1_.getString("SkullOwner"));
                                }
                            }

                            lvt_14_1_.setPlayerProfile(lvt_15_1_);
                        }
                        else
                        {
                            lvt_14_1_.setType(stack.getMetadata());
                        }

                        lvt_14_1_.setSkullRotation(lvt_12_1_);
                        Blocks.skull.checkWitherSpawn(worldIn, pos, lvt_14_1_);
                    }

                    --stack.stackSize;
                }

                return true;
            }
        }
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
    {
        for (int lvt_4_1_ = 0; lvt_4_1_ < skullTypes.length; ++lvt_4_1_)
        {
            subItems.add(new ItemStack(itemIn, 1, lvt_4_1_));
        }
    }

    /**
     * Converts the given ItemStack damage value into a metadata value to be placed in the world when this Item is
     * placed as a Block (mostly used with ItemBlocks).
     */
    public int getMetadata(int damage)
    {
        return damage;
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(ItemStack stack)
    {
        int lvt_2_1_ = stack.getMetadata();

        if (lvt_2_1_ < 0 || lvt_2_1_ >= skullTypes.length)
        {
            lvt_2_1_ = 0;
        }

        return super.getUnlocalizedName() + "." + skullTypes[lvt_2_1_];
    }

    public String getItemStackDisplayName(ItemStack stack)
    {
        if (stack.getMetadata() == 3 && stack.hasTagCompound())
        {
            if (stack.getTagCompound().hasKey("SkullOwner", 8))
            {
                return StatCollector.translateToLocalFormatted("item.skull.player.name", new Object[] {stack.getTagCompound().getString("SkullOwner")});
            }

            if (stack.getTagCompound().hasKey("SkullOwner", 10))
            {
                NBTTagCompound lvt_2_1_ = stack.getTagCompound().getCompoundTag("SkullOwner");

                if (lvt_2_1_.hasKey("Name", 8))
                {
                    return StatCollector.translateToLocalFormatted("item.skull.player.name", new Object[] {lvt_2_1_.getString("Name")});
                }
            }
        }

        return super.getItemStackDisplayName(stack);
    }

    /**
     * Called when an ItemStack with NBT data is read to potentially that ItemStack's NBT data
     */
    public boolean updateItemStackNBT(NBTTagCompound nbt)
    {
        super.updateItemStackNBT(nbt);

        if (nbt.hasKey("SkullOwner", 8) && nbt.getString("SkullOwner").length() > 0)
        {
            GameProfile lvt_2_1_ = new GameProfile((UUID)null, nbt.getString("SkullOwner"));
            lvt_2_1_ = TileEntitySkull.updateGameprofile(lvt_2_1_);
            nbt.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), lvt_2_1_));
            return true;
        }
        else
        {
            return false;
        }
    }
}
