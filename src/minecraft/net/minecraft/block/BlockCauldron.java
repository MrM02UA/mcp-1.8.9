package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockCauldron extends Block
{
    public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 3);

    public BlockCauldron()
    {
        super(Material.iron, MapColor.stoneColor);
        this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, Integer.valueOf(0)));
    }

    /**
     * Add all collision boxes of this Block to the list that intersect with the given mask.
     */
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity)
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.3125F, 1.0F);
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        float lvt_7_1_ = 0.125F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, lvt_7_1_, 1.0F, 1.0F);
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, lvt_7_1_);
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        this.setBlockBounds(1.0F - lvt_7_1_, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        this.setBlockBounds(0.0F, 0.0F, 1.0F - lvt_7_1_, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        this.setBlockBoundsForItemRender();
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean isFullCube()
    {
        return false;
    }

    /**
     * Called When an Entity Collided with the Block
     */
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        int lvt_5_1_ = ((Integer)state.getValue(LEVEL)).intValue();
        float lvt_6_1_ = (float)pos.getY() + (6.0F + (float)(3 * lvt_5_1_)) / 16.0F;

        if (!worldIn.isRemote && entityIn.isBurning() && lvt_5_1_ > 0 && entityIn.getEntityBoundingBox().minY <= (double)lvt_6_1_)
        {
            entityIn.extinguish();
            this.setWaterLevel(worldIn, pos, state, lvt_5_1_ - 1);
        }
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        else
        {
            ItemStack lvt_9_1_ = playerIn.inventory.getCurrentItem();

            if (lvt_9_1_ == null)
            {
                return true;
            }
            else
            {
                int lvt_10_1_ = ((Integer)state.getValue(LEVEL)).intValue();
                Item lvt_11_1_ = lvt_9_1_.getItem();

                if (lvt_11_1_ == Items.water_bucket)
                {
                    if (lvt_10_1_ < 3)
                    {
                        if (!playerIn.capabilities.isCreativeMode)
                        {
                            playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, new ItemStack(Items.bucket));
                        }

                        playerIn.triggerAchievement(StatList.field_181725_I);
                        this.setWaterLevel(worldIn, pos, state, 3);
                    }

                    return true;
                }
                else if (lvt_11_1_ == Items.glass_bottle)
                {
                    if (lvt_10_1_ > 0)
                    {
                        if (!playerIn.capabilities.isCreativeMode)
                        {
                            ItemStack lvt_12_1_ = new ItemStack(Items.potionitem, 1, 0);

                            if (!playerIn.inventory.addItemStackToInventory(lvt_12_1_))
                            {
                                worldIn.spawnEntityInWorld(new EntityItem(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY() + 1.5D, (double)pos.getZ() + 0.5D, lvt_12_1_));
                            }
                            else if (playerIn instanceof EntityPlayerMP)
                            {
                                ((EntityPlayerMP)playerIn).sendContainerToPlayer(playerIn.inventoryContainer);
                            }

                            playerIn.triggerAchievement(StatList.field_181726_J);
                            --lvt_9_1_.stackSize;

                            if (lvt_9_1_.stackSize <= 0)
                            {
                                playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, (ItemStack)null);
                            }
                        }

                        this.setWaterLevel(worldIn, pos, state, lvt_10_1_ - 1);
                    }

                    return true;
                }
                else
                {
                    if (lvt_10_1_ > 0 && lvt_11_1_ instanceof ItemArmor)
                    {
                        ItemArmor lvt_12_2_ = (ItemArmor)lvt_11_1_;

                        if (lvt_12_2_.getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER && lvt_12_2_.hasColor(lvt_9_1_))
                        {
                            lvt_12_2_.removeColor(lvt_9_1_);
                            this.setWaterLevel(worldIn, pos, state, lvt_10_1_ - 1);
                            playerIn.triggerAchievement(StatList.field_181727_K);
                            return true;
                        }
                    }

                    if (lvt_10_1_ > 0 && lvt_11_1_ instanceof ItemBanner && TileEntityBanner.getPatterns(lvt_9_1_) > 0)
                    {
                        ItemStack lvt_12_3_ = lvt_9_1_.copy();
                        lvt_12_3_.stackSize = 1;
                        TileEntityBanner.removeBannerData(lvt_12_3_);

                        if (lvt_9_1_.stackSize <= 1 && !playerIn.capabilities.isCreativeMode)
                        {
                            playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, lvt_12_3_);
                        }
                        else
                        {
                            if (!playerIn.inventory.addItemStackToInventory(lvt_12_3_))
                            {
                                worldIn.spawnEntityInWorld(new EntityItem(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY() + 1.5D, (double)pos.getZ() + 0.5D, lvt_12_3_));
                            }
                            else if (playerIn instanceof EntityPlayerMP)
                            {
                                ((EntityPlayerMP)playerIn).sendContainerToPlayer(playerIn.inventoryContainer);
                            }

                            playerIn.triggerAchievement(StatList.field_181728_L);

                            if (!playerIn.capabilities.isCreativeMode)
                            {
                                --lvt_9_1_.stackSize;
                            }
                        }

                        if (!playerIn.capabilities.isCreativeMode)
                        {
                            this.setWaterLevel(worldIn, pos, state, lvt_10_1_ - 1);
                        }

                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
            }
        }
    }

    public void setWaterLevel(World worldIn, BlockPos pos, IBlockState state, int level)
    {
        worldIn.setBlockState(pos, state.withProperty(LEVEL, Integer.valueOf(MathHelper.clamp_int(level, 0, 3))), 2);
        worldIn.updateComparatorOutputLevel(pos, this);
    }

    /**
     * Called similar to random ticks, but only when it is raining.
     */
    public void fillWithRain(World worldIn, BlockPos pos)
    {
        if (worldIn.rand.nextInt(20) == 1)
        {
            IBlockState lvt_3_1_ = worldIn.getBlockState(pos);

            if (((Integer)lvt_3_1_.getValue(LEVEL)).intValue() < 3)
            {
                worldIn.setBlockState(pos, lvt_3_1_.cycleProperty(LEVEL), 2);
            }
        }
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.cauldron;
    }

    public Item getItem(World worldIn, BlockPos pos)
    {
        return Items.cauldron;
    }

    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    public int getComparatorInputOverride(World worldIn, BlockPos pos)
    {
        return ((Integer)worldIn.getBlockState(pos).getValue(LEVEL)).intValue();
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(LEVEL, Integer.valueOf(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((Integer)state.getValue(LEVEL)).intValue();
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {LEVEL});
    }
}
