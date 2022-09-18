package net.minecraft.item;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;

public class ItemMap extends ItemMapBase
{
    protected ItemMap()
    {
        this.setHasSubtypes(true);
    }

    public static MapData loadMapData(int mapId, World worldIn)
    {
        String lvt_2_1_ = "map_" + mapId;
        MapData lvt_3_1_ = (MapData)worldIn.loadItemData(MapData.class, lvt_2_1_);

        if (lvt_3_1_ == null)
        {
            lvt_3_1_ = new MapData(lvt_2_1_);
            worldIn.setItemData(lvt_2_1_, lvt_3_1_);
        }

        return lvt_3_1_;
    }

    public MapData getMapData(ItemStack stack, World worldIn)
    {
        String lvt_3_1_ = "map_" + stack.getMetadata();
        MapData lvt_4_1_ = (MapData)worldIn.loadItemData(MapData.class, lvt_3_1_);

        if (lvt_4_1_ == null && !worldIn.isRemote)
        {
            stack.setItemDamage(worldIn.getUniqueDataId("map"));
            lvt_3_1_ = "map_" + stack.getMetadata();
            lvt_4_1_ = new MapData(lvt_3_1_);
            lvt_4_1_.scale = 3;
            lvt_4_1_.calculateMapCenter((double)worldIn.getWorldInfo().getSpawnX(), (double)worldIn.getWorldInfo().getSpawnZ(), lvt_4_1_.scale);
            lvt_4_1_.dimension = (byte)worldIn.provider.getDimensionId();
            lvt_4_1_.markDirty();
            worldIn.setItemData(lvt_3_1_, lvt_4_1_);
        }

        return lvt_4_1_;
    }

    public void updateMapData(World worldIn, Entity viewer, MapData data)
    {
        if (worldIn.provider.getDimensionId() == data.dimension && viewer instanceof EntityPlayer)
        {
            int lvt_4_1_ = 1 << data.scale;
            int lvt_5_1_ = data.xCenter;
            int lvt_6_1_ = data.zCenter;
            int lvt_7_1_ = MathHelper.floor_double(viewer.posX - (double)lvt_5_1_) / lvt_4_1_ + 64;
            int lvt_8_1_ = MathHelper.floor_double(viewer.posZ - (double)lvt_6_1_) / lvt_4_1_ + 64;
            int lvt_9_1_ = 128 / lvt_4_1_;

            if (worldIn.provider.getHasNoSky())
            {
                lvt_9_1_ /= 2;
            }

            MapData.MapInfo lvt_10_1_ = data.getMapInfo((EntityPlayer)viewer);
            ++lvt_10_1_.field_82569_d;
            boolean lvt_11_1_ = false;

            for (int lvt_12_1_ = lvt_7_1_ - lvt_9_1_ + 1; lvt_12_1_ < lvt_7_1_ + lvt_9_1_; ++lvt_12_1_)
            {
                if ((lvt_12_1_ & 15) == (lvt_10_1_.field_82569_d & 15) || lvt_11_1_)
                {
                    lvt_11_1_ = false;
                    double lvt_13_1_ = 0.0D;

                    for (int lvt_15_1_ = lvt_8_1_ - lvt_9_1_ - 1; lvt_15_1_ < lvt_8_1_ + lvt_9_1_; ++lvt_15_1_)
                    {
                        if (lvt_12_1_ >= 0 && lvt_15_1_ >= -1 && lvt_12_1_ < 128 && lvt_15_1_ < 128)
                        {
                            int lvt_16_1_ = lvt_12_1_ - lvt_7_1_;
                            int lvt_17_1_ = lvt_15_1_ - lvt_8_1_;
                            boolean lvt_18_1_ = lvt_16_1_ * lvt_16_1_ + lvt_17_1_ * lvt_17_1_ > (lvt_9_1_ - 2) * (lvt_9_1_ - 2);
                            int lvt_19_1_ = (lvt_5_1_ / lvt_4_1_ + lvt_12_1_ - 64) * lvt_4_1_;
                            int lvt_20_1_ = (lvt_6_1_ / lvt_4_1_ + lvt_15_1_ - 64) * lvt_4_1_;
                            Multiset<MapColor> lvt_21_1_ = HashMultiset.create();
                            Chunk lvt_22_1_ = worldIn.getChunkFromBlockCoords(new BlockPos(lvt_19_1_, 0, lvt_20_1_));

                            if (!lvt_22_1_.isEmpty())
                            {
                                int lvt_23_1_ = lvt_19_1_ & 15;
                                int lvt_24_1_ = lvt_20_1_ & 15;
                                int lvt_25_1_ = 0;
                                double lvt_26_1_ = 0.0D;

                                if (worldIn.provider.getHasNoSky())
                                {
                                    int lvt_28_1_ = lvt_19_1_ + lvt_20_1_ * 231871;
                                    lvt_28_1_ = lvt_28_1_ * lvt_28_1_ * 31287121 + lvt_28_1_ * 11;

                                    if ((lvt_28_1_ >> 20 & 1) == 0)
                                    {
                                        lvt_21_1_.add(Blocks.dirt.getMapColor(Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT)), 10);
                                    }
                                    else
                                    {
                                        lvt_21_1_.add(Blocks.stone.getMapColor(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.STONE)), 100);
                                    }

                                    lvt_26_1_ = 100.0D;
                                }
                                else
                                {
                                    BlockPos.MutableBlockPos lvt_28_2_ = new BlockPos.MutableBlockPos();

                                    for (int lvt_29_1_ = 0; lvt_29_1_ < lvt_4_1_; ++lvt_29_1_)
                                    {
                                        for (int lvt_30_1_ = 0; lvt_30_1_ < lvt_4_1_; ++lvt_30_1_)
                                        {
                                            int lvt_31_1_ = lvt_22_1_.getHeightValue(lvt_29_1_ + lvt_23_1_, lvt_30_1_ + lvt_24_1_) + 1;
                                            IBlockState lvt_32_1_ = Blocks.air.getDefaultState();

                                            if (lvt_31_1_ > 1)
                                            {
                                                label541:
                                                {
                                                    while (true)
                                                    {
                                                        --lvt_31_1_;
                                                        lvt_32_1_ = lvt_22_1_.getBlockState(lvt_28_2_.set(lvt_29_1_ + lvt_23_1_, lvt_31_1_, lvt_30_1_ + lvt_24_1_));

                                                        if (lvt_32_1_.getBlock().getMapColor(lvt_32_1_) != MapColor.airColor || lvt_31_1_ <= 0)
                                                        {
                                                            break;
                                                        }
                                                    }

                                                    if (lvt_31_1_ > 0 && lvt_32_1_.getBlock().getMaterial().isLiquid())
                                                    {
                                                        int lvt_33_1_ = lvt_31_1_ - 1;

                                                        while (true)
                                                        {
                                                            Block lvt_34_1_ = lvt_22_1_.getBlock(lvt_29_1_ + lvt_23_1_, lvt_33_1_--, lvt_30_1_ + lvt_24_1_);
                                                            ++lvt_25_1_;

                                                            if (lvt_33_1_ <= 0 || !lvt_34_1_.getMaterial().isLiquid())
                                                            {
                                                                break label541;
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            lvt_26_1_ += (double)lvt_31_1_ / (double)(lvt_4_1_ * lvt_4_1_);
                                            lvt_21_1_.add(lvt_32_1_.getBlock().getMapColor(lvt_32_1_));
                                        }
                                    }
                                }

                                lvt_25_1_ = lvt_25_1_ / (lvt_4_1_ * lvt_4_1_);
                                double lvt_28_3_ = (lvt_26_1_ - lvt_13_1_) * 4.0D / (double)(lvt_4_1_ + 4) + ((double)(lvt_12_1_ + lvt_15_1_ & 1) - 0.5D) * 0.4D;
                                int lvt_30_2_ = 1;

                                if (lvt_28_3_ > 0.6D)
                                {
                                    lvt_30_2_ = 2;
                                }

                                if (lvt_28_3_ < -0.6D)
                                {
                                    lvt_30_2_ = 0;
                                }

                                MapColor lvt_31_2_ = (MapColor)Iterables.getFirst(Multisets.copyHighestCountFirst(lvt_21_1_), MapColor.airColor);

                                if (lvt_31_2_ == MapColor.waterColor)
                                {
                                    lvt_28_3_ = (double)lvt_25_1_ * 0.1D + (double)(lvt_12_1_ + lvt_15_1_ & 1) * 0.2D;
                                    lvt_30_2_ = 1;

                                    if (lvt_28_3_ < 0.5D)
                                    {
                                        lvt_30_2_ = 2;
                                    }

                                    if (lvt_28_3_ > 0.9D)
                                    {
                                        lvt_30_2_ = 0;
                                    }
                                }

                                lvt_13_1_ = lvt_26_1_;

                                if (lvt_15_1_ >= 0 && lvt_16_1_ * lvt_16_1_ + lvt_17_1_ * lvt_17_1_ < lvt_9_1_ * lvt_9_1_ && (!lvt_18_1_ || (lvt_12_1_ + lvt_15_1_ & 1) != 0))
                                {
                                    byte lvt_32_2_ = data.colors[lvt_12_1_ + lvt_15_1_ * 128];
                                    byte lvt_33_2_ = (byte)(lvt_31_2_.colorIndex * 4 + lvt_30_2_);

                                    if (lvt_32_2_ != lvt_33_2_)
                                    {
                                        data.colors[lvt_12_1_ + lvt_15_1_ * 128] = lvt_33_2_;
                                        data.updateMapData(lvt_12_1_, lvt_15_1_);
                                        lvt_11_1_ = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
     * update it's contents.
     */
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        if (!worldIn.isRemote)
        {
            MapData lvt_6_1_ = this.getMapData(stack, worldIn);

            if (entityIn instanceof EntityPlayer)
            {
                EntityPlayer lvt_7_1_ = (EntityPlayer)entityIn;
                lvt_6_1_.updateVisiblePlayers(lvt_7_1_, stack);
            }

            if (isSelected)
            {
                this.updateMapData(worldIn, entityIn, lvt_6_1_);
            }
        }
    }

    public Packet createMapDataPacket(ItemStack stack, World worldIn, EntityPlayer player)
    {
        return this.getMapData(stack, worldIn).getMapPacket(stack, worldIn, player);
    }

    /**
     * Called when item is crafted/smelted. Used only by maps so far.
     */
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn)
    {
        if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("map_is_scaling"))
        {
            MapData lvt_4_1_ = Items.filled_map.getMapData(stack, worldIn);
            stack.setItemDamage(worldIn.getUniqueDataId("map"));
            MapData lvt_5_1_ = new MapData("map_" + stack.getMetadata());
            lvt_5_1_.scale = (byte)(lvt_4_1_.scale + 1);

            if (lvt_5_1_.scale > 4)
            {
                lvt_5_1_.scale = 4;
            }

            lvt_5_1_.calculateMapCenter((double)lvt_4_1_.xCenter, (double)lvt_4_1_.zCenter, lvt_5_1_.scale);
            lvt_5_1_.dimension = lvt_4_1_.dimension;
            lvt_5_1_.markDirty();
            worldIn.setItemData("map_" + stack.getMetadata(), lvt_5_1_);
        }
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
        MapData lvt_5_1_ = this.getMapData(stack, playerIn.worldObj);

        if (advanced)
        {
            if (lvt_5_1_ == null)
            {
                tooltip.add("Unknown map");
            }
            else
            {
                tooltip.add("Scaling at 1:" + (1 << lvt_5_1_.scale));
                tooltip.add("(Level " + lvt_5_1_.scale + "/" + 4 + ")");
            }
        }
    }
}
