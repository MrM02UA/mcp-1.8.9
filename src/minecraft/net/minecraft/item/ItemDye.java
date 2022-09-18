package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class ItemDye extends Item
{
    public static final int[] dyeColors = new int[] {1973019, 11743532, 3887386, 5320730, 2437522, 8073150, 2651799, 11250603, 4408131, 14188952, 4312372, 14602026, 6719955, 12801229, 15435844, 15790320};

    public ItemDye()
    {
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabs.tabMaterials);
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(ItemStack stack)
    {
        int lvt_2_1_ = stack.getMetadata();
        return super.getUnlocalizedName() + "." + EnumDyeColor.byDyeDamage(lvt_2_1_).getUnlocalizedName();
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!playerIn.canPlayerEdit(pos.offset(side), side, stack))
        {
            return false;
        }
        else
        {
            EnumDyeColor lvt_9_1_ = EnumDyeColor.byDyeDamage(stack.getMetadata());

            if (lvt_9_1_ == EnumDyeColor.WHITE)
            {
                if (applyBonemeal(stack, worldIn, pos))
                {
                    if (!worldIn.isRemote)
                    {
                        worldIn.playAuxSFX(2005, pos, 0);
                    }

                    return true;
                }
            }
            else if (lvt_9_1_ == EnumDyeColor.BROWN)
            {
                IBlockState lvt_10_1_ = worldIn.getBlockState(pos);
                Block lvt_11_1_ = lvt_10_1_.getBlock();

                if (lvt_11_1_ == Blocks.log && lvt_10_1_.getValue(BlockPlanks.VARIANT) == BlockPlanks.EnumType.JUNGLE)
                {
                    if (side == EnumFacing.DOWN)
                    {
                        return false;
                    }

                    if (side == EnumFacing.UP)
                    {
                        return false;
                    }

                    pos = pos.offset(side);

                    if (worldIn.isAirBlock(pos))
                    {
                        IBlockState lvt_12_1_ = Blocks.cocoa.onBlockPlaced(worldIn, pos, side, hitX, hitY, hitZ, 0, playerIn);
                        worldIn.setBlockState(pos, lvt_12_1_, 2);

                        if (!playerIn.capabilities.isCreativeMode)
                        {
                            --stack.stackSize;
                        }
                    }

                    return true;
                }
            }

            return false;
        }
    }

    public static boolean applyBonemeal(ItemStack stack, World worldIn, BlockPos target)
    {
        IBlockState lvt_3_1_ = worldIn.getBlockState(target);

        if (lvt_3_1_.getBlock() instanceof IGrowable)
        {
            IGrowable lvt_4_1_ = (IGrowable)lvt_3_1_.getBlock();

            if (lvt_4_1_.canGrow(worldIn, target, lvt_3_1_, worldIn.isRemote))
            {
                if (!worldIn.isRemote)
                {
                    if (lvt_4_1_.canUseBonemeal(worldIn, worldIn.rand, target, lvt_3_1_))
                    {
                        lvt_4_1_.grow(worldIn, worldIn.rand, target, lvt_3_1_);
                    }

                    --stack.stackSize;
                }

                return true;
            }
        }

        return false;
    }

    public static void spawnBonemealParticles(World worldIn, BlockPos pos, int amount)
    {
        if (amount == 0)
        {
            amount = 15;
        }

        Block lvt_3_1_ = worldIn.getBlockState(pos).getBlock();

        if (lvt_3_1_.getMaterial() != Material.air)
        {
            lvt_3_1_.setBlockBoundsBasedOnState(worldIn, pos);

            for (int lvt_4_1_ = 0; lvt_4_1_ < amount; ++lvt_4_1_)
            {
                double lvt_5_1_ = itemRand.nextGaussian() * 0.02D;
                double lvt_7_1_ = itemRand.nextGaussian() * 0.02D;
                double lvt_9_1_ = itemRand.nextGaussian() * 0.02D;
                worldIn.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, (double)((float)pos.getX() + itemRand.nextFloat()), (double)pos.getY() + (double)itemRand.nextFloat() * lvt_3_1_.getBlockBoundsMaxY(), (double)((float)pos.getZ() + itemRand.nextFloat()), lvt_5_1_, lvt_7_1_, lvt_9_1_, new int[0]);
            }
        }
    }

    /**
     * Returns true if the item can be used on the given entity, e.g. shears on sheep.
     */
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target)
    {
        if (target instanceof EntitySheep)
        {
            EntitySheep lvt_4_1_ = (EntitySheep)target;
            EnumDyeColor lvt_5_1_ = EnumDyeColor.byDyeDamage(stack.getMetadata());

            if (!lvt_4_1_.getSheared() && lvt_4_1_.getFleeceColor() != lvt_5_1_)
            {
                lvt_4_1_.setFleeceColor(lvt_5_1_);
                --stack.stackSize;
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
    {
        for (int lvt_4_1_ = 0; lvt_4_1_ < 16; ++lvt_4_1_)
        {
            subItems.add(new ItemStack(itemIn, 1, lvt_4_1_));
        }
    }
}
