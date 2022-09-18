package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemBlock extends Item
{
    protected final Block block;

    public ItemBlock(Block block)
    {
        this.block = block;
    }

    /**
     * Sets the unlocalized name of this item to the string passed as the parameter, prefixed by "item."
     */
    public ItemBlock setUnlocalizedName(String unlocalizedName)
    {
        super.setUnlocalizedName(unlocalizedName);
        return this;
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        IBlockState lvt_9_1_ = worldIn.getBlockState(pos);
        Block lvt_10_1_ = lvt_9_1_.getBlock();

        if (!lvt_10_1_.isReplaceable(worldIn, pos))
        {
            pos = pos.offset(side);
        }

        if (stack.stackSize == 0)
        {
            return false;
        }
        else if (!playerIn.canPlayerEdit(pos, side, stack))
        {
            return false;
        }
        else if (worldIn.canBlockBePlaced(this.block, pos, false, side, (Entity)null, stack))
        {
            int lvt_11_1_ = this.getMetadata(stack.getMetadata());
            IBlockState lvt_12_1_ = this.block.onBlockPlaced(worldIn, pos, side, hitX, hitY, hitZ, lvt_11_1_, playerIn);

            if (worldIn.setBlockState(pos, lvt_12_1_, 3))
            {
                lvt_12_1_ = worldIn.getBlockState(pos);

                if (lvt_12_1_.getBlock() == this.block)
                {
                    setTileEntityNBT(worldIn, playerIn, pos, stack);
                    this.block.onBlockPlacedBy(worldIn, pos, lvt_12_1_, playerIn, stack);
                }

                worldIn.playSoundEffect((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F), this.block.stepSound.getPlaceSound(), (this.block.stepSound.getVolume() + 1.0F) / 2.0F, this.block.stepSound.getFrequency() * 0.8F);
                --stack.stackSize;
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    public static boolean setTileEntityNBT(World worldIn, EntityPlayer pos, BlockPos stack, ItemStack p_179224_3_)
    {
        MinecraftServer lvt_4_1_ = MinecraftServer.getServer();

        if (lvt_4_1_ == null)
        {
            return false;
        }
        else
        {
            if (p_179224_3_.hasTagCompound() && p_179224_3_.getTagCompound().hasKey("BlockEntityTag", 10))
            {
                TileEntity lvt_5_1_ = worldIn.getTileEntity(stack);

                if (lvt_5_1_ != null)
                {
                    if (!worldIn.isRemote && lvt_5_1_.func_183000_F() && !lvt_4_1_.getConfigurationManager().canSendCommands(pos.getGameProfile()))
                    {
                        return false;
                    }

                    NBTTagCompound lvt_6_1_ = new NBTTagCompound();
                    NBTTagCompound lvt_7_1_ = (NBTTagCompound)lvt_6_1_.copy();
                    lvt_5_1_.writeToNBT(lvt_6_1_);
                    NBTTagCompound lvt_8_1_ = (NBTTagCompound)p_179224_3_.getTagCompound().getTag("BlockEntityTag");
                    lvt_6_1_.merge(lvt_8_1_);
                    lvt_6_1_.setInteger("x", stack.getX());
                    lvt_6_1_.setInteger("y", stack.getY());
                    lvt_6_1_.setInteger("z", stack.getZ());

                    if (!lvt_6_1_.equals(lvt_7_1_))
                    {
                        lvt_5_1_.readFromNBT(lvt_6_1_);
                        lvt_5_1_.markDirty();
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack)
    {
        Block lvt_6_1_ = worldIn.getBlockState(pos).getBlock();

        if (lvt_6_1_ == Blocks.snow_layer)
        {
            side = EnumFacing.UP;
        }
        else if (!lvt_6_1_.isReplaceable(worldIn, pos))
        {
            pos = pos.offset(side);
        }

        return worldIn.canBlockBePlaced(this.block, pos, false, side, (Entity)null, stack);
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(ItemStack stack)
    {
        return this.block.getUnlocalizedName();
    }

    /**
     * Returns the unlocalized name of this item.
     */
    public String getUnlocalizedName()
    {
        return this.block.getUnlocalizedName();
    }

    /**
     * gets the CreativeTab this item is displayed on
     */
    public CreativeTabs getCreativeTab()
    {
        return this.block.getCreativeTabToDisplayOn();
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
    {
        this.block.getSubBlocks(itemIn, tab, subItems);
    }

    public Block getBlock()
    {
        return this.block;
    }

    /**
     * Sets the unlocalized name of this item to the string passed as the parameter, prefixed by "item."
     */
    public Item setUnlocalizedName(String unlocalizedName)
    {
        return this.setUnlocalizedName(unlocalizedName);
    }
}
