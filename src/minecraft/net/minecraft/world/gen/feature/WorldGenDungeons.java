package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenDungeons extends WorldGenerator
{
    private static final Logger field_175918_a = LogManager.getLogger();
    private static final String[] SPAWNERTYPES = new String[] {"Skeleton", "Zombie", "Zombie", "Spider"};
    private static final List<WeightedRandomChestContent> CHESTCONTENT = Lists.newArrayList(new WeightedRandomChestContent[] {new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 10), new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 4, 10), new WeightedRandomChestContent(Items.bread, 0, 1, 1, 10), new WeightedRandomChestContent(Items.wheat, 0, 1, 4, 10), new WeightedRandomChestContent(Items.gunpowder, 0, 1, 4, 10), new WeightedRandomChestContent(Items.string, 0, 1, 4, 10), new WeightedRandomChestContent(Items.bucket, 0, 1, 1, 10), new WeightedRandomChestContent(Items.golden_apple, 0, 1, 1, 1), new WeightedRandomChestContent(Items.redstone, 0, 1, 4, 10), new WeightedRandomChestContent(Items.record_13, 0, 1, 1, 4), new WeightedRandomChestContent(Items.record_cat, 0, 1, 1, 4), new WeightedRandomChestContent(Items.name_tag, 0, 1, 1, 10), new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 2), new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 5), new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 1)});

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        int lvt_4_1_ = 3;
        int lvt_5_1_ = rand.nextInt(2) + 2;
        int lvt_6_1_ = -lvt_5_1_ - 1;
        int lvt_7_1_ = lvt_5_1_ + 1;
        int lvt_8_1_ = -1;
        int lvt_9_1_ = 4;
        int lvt_10_1_ = rand.nextInt(2) + 2;
        int lvt_11_1_ = -lvt_10_1_ - 1;
        int lvt_12_1_ = lvt_10_1_ + 1;
        int lvt_13_1_ = 0;

        for (int lvt_14_1_ = lvt_6_1_; lvt_14_1_ <= lvt_7_1_; ++lvt_14_1_)
        {
            for (int lvt_15_1_ = -1; lvt_15_1_ <= 4; ++lvt_15_1_)
            {
                for (int lvt_16_1_ = lvt_11_1_; lvt_16_1_ <= lvt_12_1_; ++lvt_16_1_)
                {
                    BlockPos lvt_17_1_ = position.add(lvt_14_1_, lvt_15_1_, lvt_16_1_);
                    Material lvt_18_1_ = worldIn.getBlockState(lvt_17_1_).getBlock().getMaterial();
                    boolean lvt_19_1_ = lvt_18_1_.isSolid();

                    if (lvt_15_1_ == -1 && !lvt_19_1_)
                    {
                        return false;
                    }

                    if (lvt_15_1_ == 4 && !lvt_19_1_)
                    {
                        return false;
                    }

                    if ((lvt_14_1_ == lvt_6_1_ || lvt_14_1_ == lvt_7_1_ || lvt_16_1_ == lvt_11_1_ || lvt_16_1_ == lvt_12_1_) && lvt_15_1_ == 0 && worldIn.isAirBlock(lvt_17_1_) && worldIn.isAirBlock(lvt_17_1_.up()))
                    {
                        ++lvt_13_1_;
                    }
                }
            }
        }

        if (lvt_13_1_ >= 1 && lvt_13_1_ <= 5)
        {
            for (int lvt_14_2_ = lvt_6_1_; lvt_14_2_ <= lvt_7_1_; ++lvt_14_2_)
            {
                for (int lvt_15_2_ = 3; lvt_15_2_ >= -1; --lvt_15_2_)
                {
                    for (int lvt_16_2_ = lvt_11_1_; lvt_16_2_ <= lvt_12_1_; ++lvt_16_2_)
                    {
                        BlockPos lvt_17_2_ = position.add(lvt_14_2_, lvt_15_2_, lvt_16_2_);

                        if (lvt_14_2_ != lvt_6_1_ && lvt_15_2_ != -1 && lvt_16_2_ != lvt_11_1_ && lvt_14_2_ != lvt_7_1_ && lvt_15_2_ != 4 && lvt_16_2_ != lvt_12_1_)
                        {
                            if (worldIn.getBlockState(lvt_17_2_).getBlock() != Blocks.chest)
                            {
                                worldIn.setBlockToAir(lvt_17_2_);
                            }
                        }
                        else if (lvt_17_2_.getY() >= 0 && !worldIn.getBlockState(lvt_17_2_.down()).getBlock().getMaterial().isSolid())
                        {
                            worldIn.setBlockToAir(lvt_17_2_);
                        }
                        else if (worldIn.getBlockState(lvt_17_2_).getBlock().getMaterial().isSolid() && worldIn.getBlockState(lvt_17_2_).getBlock() != Blocks.chest)
                        {
                            if (lvt_15_2_ == -1 && rand.nextInt(4) != 0)
                            {
                                worldIn.setBlockState(lvt_17_2_, Blocks.mossy_cobblestone.getDefaultState(), 2);
                            }
                            else
                            {
                                worldIn.setBlockState(lvt_17_2_, Blocks.cobblestone.getDefaultState(), 2);
                            }
                        }
                    }
                }
            }

            for (int lvt_14_3_ = 0; lvt_14_3_ < 2; ++lvt_14_3_)
            {
                for (int lvt_15_3_ = 0; lvt_15_3_ < 3; ++lvt_15_3_)
                {
                    int lvt_16_3_ = position.getX() + rand.nextInt(lvt_5_1_ * 2 + 1) - lvt_5_1_;
                    int lvt_17_3_ = position.getY();
                    int lvt_18_2_ = position.getZ() + rand.nextInt(lvt_10_1_ * 2 + 1) - lvt_10_1_;
                    BlockPos lvt_19_2_ = new BlockPos(lvt_16_3_, lvt_17_3_, lvt_18_2_);

                    if (worldIn.isAirBlock(lvt_19_2_))
                    {
                        int lvt_20_1_ = 0;

                        for (EnumFacing lvt_22_1_ : EnumFacing.Plane.HORIZONTAL)
                        {
                            if (worldIn.getBlockState(lvt_19_2_.offset(lvt_22_1_)).getBlock().getMaterial().isSolid())
                            {
                                ++lvt_20_1_;
                            }
                        }

                        if (lvt_20_1_ == 1)
                        {
                            worldIn.setBlockState(lvt_19_2_, Blocks.chest.correctFacing(worldIn, lvt_19_2_, Blocks.chest.getDefaultState()), 2);
                            List<WeightedRandomChestContent> lvt_21_2_ = WeightedRandomChestContent.func_177629_a(CHESTCONTENT, new WeightedRandomChestContent[] {Items.enchanted_book.getRandom(rand)});
                            TileEntity lvt_22_2_ = worldIn.getTileEntity(lvt_19_2_);

                            if (lvt_22_2_ instanceof TileEntityChest)
                            {
                                WeightedRandomChestContent.generateChestContents(rand, lvt_21_2_, (TileEntityChest)lvt_22_2_, 8);
                            }

                            break;
                        }
                    }
                }
            }

            worldIn.setBlockState(position, Blocks.mob_spawner.getDefaultState(), 2);
            TileEntity lvt_14_4_ = worldIn.getTileEntity(position);

            if (lvt_14_4_ instanceof TileEntityMobSpawner)
            {
                ((TileEntityMobSpawner)lvt_14_4_).getSpawnerBaseLogic().setEntityName(this.pickMobSpawner(rand));
            }
            else
            {
                field_175918_a.error("Failed to fetch mob spawner entity at (" + position.getX() + ", " + position.getY() + ", " + position.getZ() + ")");
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Randomly decides which spawner to use in a dungeon
     */
    private String pickMobSpawner(Random p_76543_1_)
    {
        return SPAWNERTYPES[p_76543_1_.nextInt(SPAWNERTYPES.length)];
    }
}
