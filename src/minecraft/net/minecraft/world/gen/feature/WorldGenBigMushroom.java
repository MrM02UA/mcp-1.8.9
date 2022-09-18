package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenBigMushroom extends WorldGenerator
{
    /** The mushroom type. 0 for brown, 1 for red. */
    private Block mushroomType;

    public WorldGenBigMushroom(Block p_i46449_1_)
    {
        super(true);
        this.mushroomType = p_i46449_1_;
    }

    public WorldGenBigMushroom()
    {
        super(false);
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        if (this.mushroomType == null)
        {
            this.mushroomType = rand.nextBoolean() ? Blocks.brown_mushroom_block : Blocks.red_mushroom_block;
        }

        int lvt_4_1_ = rand.nextInt(3) + 4;
        boolean lvt_5_1_ = true;

        if (position.getY() >= 1 && position.getY() + lvt_4_1_ + 1 < 256)
        {
            for (int lvt_6_1_ = position.getY(); lvt_6_1_ <= position.getY() + 1 + lvt_4_1_; ++lvt_6_1_)
            {
                int lvt_7_1_ = 3;

                if (lvt_6_1_ <= position.getY() + 3)
                {
                    lvt_7_1_ = 0;
                }

                BlockPos.MutableBlockPos lvt_8_1_ = new BlockPos.MutableBlockPos();

                for (int lvt_9_1_ = position.getX() - lvt_7_1_; lvt_9_1_ <= position.getX() + lvt_7_1_ && lvt_5_1_; ++lvt_9_1_)
                {
                    for (int lvt_10_1_ = position.getZ() - lvt_7_1_; lvt_10_1_ <= position.getZ() + lvt_7_1_ && lvt_5_1_; ++lvt_10_1_)
                    {
                        if (lvt_6_1_ >= 0 && lvt_6_1_ < 256)
                        {
                            Block lvt_11_1_ = worldIn.getBlockState(lvt_8_1_.set(lvt_9_1_, lvt_6_1_, lvt_10_1_)).getBlock();

                            if (lvt_11_1_.getMaterial() != Material.air && lvt_11_1_.getMaterial() != Material.leaves)
                            {
                                lvt_5_1_ = false;
                            }
                        }
                        else
                        {
                            lvt_5_1_ = false;
                        }
                    }
                }
            }

            if (!lvt_5_1_)
            {
                return false;
            }
            else
            {
                Block lvt_6_2_ = worldIn.getBlockState(position.down()).getBlock();

                if (lvt_6_2_ != Blocks.dirt && lvt_6_2_ != Blocks.grass && lvt_6_2_ != Blocks.mycelium)
                {
                    return false;
                }
                else
                {
                    int lvt_7_2_ = position.getY() + lvt_4_1_;

                    if (this.mushroomType == Blocks.red_mushroom_block)
                    {
                        lvt_7_2_ = position.getY() + lvt_4_1_ - 3;
                    }

                    for (int lvt_8_2_ = lvt_7_2_; lvt_8_2_ <= position.getY() + lvt_4_1_; ++lvt_8_2_)
                    {
                        int lvt_9_2_ = 1;

                        if (lvt_8_2_ < position.getY() + lvt_4_1_)
                        {
                            ++lvt_9_2_;
                        }

                        if (this.mushroomType == Blocks.brown_mushroom_block)
                        {
                            lvt_9_2_ = 3;
                        }

                        int lvt_10_2_ = position.getX() - lvt_9_2_;
                        int lvt_11_2_ = position.getX() + lvt_9_2_;
                        int lvt_12_1_ = position.getZ() - lvt_9_2_;
                        int lvt_13_1_ = position.getZ() + lvt_9_2_;

                        for (int lvt_14_1_ = lvt_10_2_; lvt_14_1_ <= lvt_11_2_; ++lvt_14_1_)
                        {
                            for (int lvt_15_1_ = lvt_12_1_; lvt_15_1_ <= lvt_13_1_; ++lvt_15_1_)
                            {
                                int lvt_16_1_ = 5;

                                if (lvt_14_1_ == lvt_10_2_)
                                {
                                    --lvt_16_1_;
                                }
                                else if (lvt_14_1_ == lvt_11_2_)
                                {
                                    ++lvt_16_1_;
                                }

                                if (lvt_15_1_ == lvt_12_1_)
                                {
                                    lvt_16_1_ -= 3;
                                }
                                else if (lvt_15_1_ == lvt_13_1_)
                                {
                                    lvt_16_1_ += 3;
                                }

                                BlockHugeMushroom.EnumType lvt_17_1_ = BlockHugeMushroom.EnumType.byMetadata(lvt_16_1_);

                                if (this.mushroomType == Blocks.brown_mushroom_block || lvt_8_2_ < position.getY() + lvt_4_1_)
                                {
                                    if ((lvt_14_1_ == lvt_10_2_ || lvt_14_1_ == lvt_11_2_) && (lvt_15_1_ == lvt_12_1_ || lvt_15_1_ == lvt_13_1_))
                                    {
                                        continue;
                                    }

                                    if (lvt_14_1_ == position.getX() - (lvt_9_2_ - 1) && lvt_15_1_ == lvt_12_1_)
                                    {
                                        lvt_17_1_ = BlockHugeMushroom.EnumType.NORTH_WEST;
                                    }

                                    if (lvt_14_1_ == lvt_10_2_ && lvt_15_1_ == position.getZ() - (lvt_9_2_ - 1))
                                    {
                                        lvt_17_1_ = BlockHugeMushroom.EnumType.NORTH_WEST;
                                    }

                                    if (lvt_14_1_ == position.getX() + (lvt_9_2_ - 1) && lvt_15_1_ == lvt_12_1_)
                                    {
                                        lvt_17_1_ = BlockHugeMushroom.EnumType.NORTH_EAST;
                                    }

                                    if (lvt_14_1_ == lvt_11_2_ && lvt_15_1_ == position.getZ() - (lvt_9_2_ - 1))
                                    {
                                        lvt_17_1_ = BlockHugeMushroom.EnumType.NORTH_EAST;
                                    }

                                    if (lvt_14_1_ == position.getX() - (lvt_9_2_ - 1) && lvt_15_1_ == lvt_13_1_)
                                    {
                                        lvt_17_1_ = BlockHugeMushroom.EnumType.SOUTH_WEST;
                                    }

                                    if (lvt_14_1_ == lvt_10_2_ && lvt_15_1_ == position.getZ() + (lvt_9_2_ - 1))
                                    {
                                        lvt_17_1_ = BlockHugeMushroom.EnumType.SOUTH_WEST;
                                    }

                                    if (lvt_14_1_ == position.getX() + (lvt_9_2_ - 1) && lvt_15_1_ == lvt_13_1_)
                                    {
                                        lvt_17_1_ = BlockHugeMushroom.EnumType.SOUTH_EAST;
                                    }

                                    if (lvt_14_1_ == lvt_11_2_ && lvt_15_1_ == position.getZ() + (lvt_9_2_ - 1))
                                    {
                                        lvt_17_1_ = BlockHugeMushroom.EnumType.SOUTH_EAST;
                                    }
                                }

                                if (lvt_17_1_ == BlockHugeMushroom.EnumType.CENTER && lvt_8_2_ < position.getY() + lvt_4_1_)
                                {
                                    lvt_17_1_ = BlockHugeMushroom.EnumType.ALL_INSIDE;
                                }

                                if (position.getY() >= position.getY() + lvt_4_1_ - 1 || lvt_17_1_ != BlockHugeMushroom.EnumType.ALL_INSIDE)
                                {
                                    BlockPos lvt_18_1_ = new BlockPos(lvt_14_1_, lvt_8_2_, lvt_15_1_);

                                    if (!worldIn.getBlockState(lvt_18_1_).getBlock().isFullBlock())
                                    {
                                        this.setBlockAndNotifyAdequately(worldIn, lvt_18_1_, this.mushroomType.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, lvt_17_1_));
                                    }
                                }
                            }
                        }
                    }

                    for (int lvt_8_3_ = 0; lvt_8_3_ < lvt_4_1_; ++lvt_8_3_)
                    {
                        Block lvt_9_3_ = worldIn.getBlockState(position.up(lvt_8_3_)).getBlock();

                        if (!lvt_9_3_.isFullBlock())
                        {
                            this.setBlockAndNotifyAdequately(worldIn, position.up(lvt_8_3_), this.mushroomType.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.STEM));
                        }
                    }

                    return true;
                }
            }
        }
        else
        {
            return false;
        }
    }
}
