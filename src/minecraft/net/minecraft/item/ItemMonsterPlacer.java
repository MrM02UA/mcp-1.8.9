package net.minecraft.item;

import java.util.List;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemMonsterPlacer extends Item
{
    public ItemMonsterPlacer()
    {
        this.setHasSubtypes(true);
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    public String getItemStackDisplayName(ItemStack stack)
    {
        String lvt_2_1_ = ("" + StatCollector.translateToLocal(this.getUnlocalizedName() + ".name")).trim();
        String lvt_3_1_ = EntityList.getStringFromID(stack.getMetadata());

        if (lvt_3_1_ != null)
        {
            lvt_2_1_ = lvt_2_1_ + " " + StatCollector.translateToLocal("entity." + lvt_3_1_ + ".name");
        }

        return lvt_2_1_;
    }

    public int getColorFromItemStack(ItemStack stack, int renderPass)
    {
        EntityList.EntityEggInfo lvt_3_1_ = (EntityList.EntityEggInfo)EntityList.entityEggs.get(Integer.valueOf(stack.getMetadata()));
        return lvt_3_1_ != null ? (renderPass == 0 ? lvt_3_1_.primaryColor : lvt_3_1_.secondaryColor) : 16777215;
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        else if (!playerIn.canPlayerEdit(pos.offset(side), side, stack))
        {
            return false;
        }
        else
        {
            IBlockState lvt_9_1_ = worldIn.getBlockState(pos);

            if (lvt_9_1_.getBlock() == Blocks.mob_spawner)
            {
                TileEntity lvt_10_1_ = worldIn.getTileEntity(pos);

                if (lvt_10_1_ instanceof TileEntityMobSpawner)
                {
                    MobSpawnerBaseLogic lvt_11_1_ = ((TileEntityMobSpawner)lvt_10_1_).getSpawnerBaseLogic();
                    lvt_11_1_.setEntityName(EntityList.getStringFromID(stack.getMetadata()));
                    lvt_10_1_.markDirty();
                    worldIn.markBlockForUpdate(pos);

                    if (!playerIn.capabilities.isCreativeMode)
                    {
                        --stack.stackSize;
                    }

                    return true;
                }
            }

            pos = pos.offset(side);
            double lvt_10_2_ = 0.0D;

            if (side == EnumFacing.UP && lvt_9_1_ instanceof BlockFence)
            {
                lvt_10_2_ = 0.5D;
            }

            Entity lvt_12_1_ = spawnCreature(worldIn, stack.getMetadata(), (double)pos.getX() + 0.5D, (double)pos.getY() + lvt_10_2_, (double)pos.getZ() + 0.5D);

            if (lvt_12_1_ != null)
            {
                if (lvt_12_1_ instanceof EntityLivingBase && stack.hasDisplayName())
                {
                    lvt_12_1_.setCustomNameTag(stack.getDisplayName());
                }

                if (!playerIn.capabilities.isCreativeMode)
                {
                    --stack.stackSize;
                }
            }

            return true;
        }
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
        if (worldIn.isRemote)
        {
            return itemStackIn;
        }
        else
        {
            MovingObjectPosition lvt_4_1_ = this.getMovingObjectPositionFromPlayer(worldIn, playerIn, true);

            if (lvt_4_1_ == null)
            {
                return itemStackIn;
            }
            else
            {
                if (lvt_4_1_.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
                {
                    BlockPos lvt_5_1_ = lvt_4_1_.getBlockPos();

                    if (!worldIn.isBlockModifiable(playerIn, lvt_5_1_))
                    {
                        return itemStackIn;
                    }

                    if (!playerIn.canPlayerEdit(lvt_5_1_, lvt_4_1_.sideHit, itemStackIn))
                    {
                        return itemStackIn;
                    }

                    if (worldIn.getBlockState(lvt_5_1_).getBlock() instanceof BlockLiquid)
                    {
                        Entity lvt_6_1_ = spawnCreature(worldIn, itemStackIn.getMetadata(), (double)lvt_5_1_.getX() + 0.5D, (double)lvt_5_1_.getY() + 0.5D, (double)lvt_5_1_.getZ() + 0.5D);

                        if (lvt_6_1_ != null)
                        {
                            if (lvt_6_1_ instanceof EntityLivingBase && itemStackIn.hasDisplayName())
                            {
                                ((EntityLiving)lvt_6_1_).setCustomNameTag(itemStackIn.getDisplayName());
                            }

                            if (!playerIn.capabilities.isCreativeMode)
                            {
                                --itemStackIn.stackSize;
                            }

                            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
                        }
                    }
                }

                return itemStackIn;
            }
        }
    }

    /**
     * Spawns the creature specified by the egg's type in the location specified by the last three parameters.
     * Parameters: world, entityID, x, y, z.
     */
    public static Entity spawnCreature(World worldIn, int entityID, double x, double y, double z)
    {
        if (!EntityList.entityEggs.containsKey(Integer.valueOf(entityID)))
        {
            return null;
        }
        else
        {
            Entity lvt_8_1_ = null;

            for (int lvt_9_1_ = 0; lvt_9_1_ < 1; ++lvt_9_1_)
            {
                lvt_8_1_ = EntityList.createEntityByID(entityID, worldIn);

                if (lvt_8_1_ instanceof EntityLivingBase)
                {
                    EntityLiving lvt_10_1_ = (EntityLiving)lvt_8_1_;
                    lvt_8_1_.setLocationAndAngles(x, y, z, MathHelper.wrapAngleTo180_float(worldIn.rand.nextFloat() * 360.0F), 0.0F);
                    lvt_10_1_.rotationYawHead = lvt_10_1_.rotationYaw;
                    lvt_10_1_.renderYawOffset = lvt_10_1_.rotationYaw;
                    lvt_10_1_.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(lvt_10_1_)), (IEntityLivingData)null);
                    worldIn.spawnEntityInWorld(lvt_8_1_);
                    lvt_10_1_.playLivingSound();
                }
            }

            return lvt_8_1_;
        }
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
    {
        for (EntityList.EntityEggInfo lvt_5_1_ : EntityList.entityEggs.values())
        {
            subItems.add(new ItemStack(itemIn, 1, lvt_5_1_.spawnedID));
        }
    }
}
