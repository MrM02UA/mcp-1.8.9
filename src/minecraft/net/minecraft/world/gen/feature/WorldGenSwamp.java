package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenSwamp extends WorldGenAbstractTree
{
    private static final IBlockState field_181648_a = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.OAK);
    private static final IBlockState field_181649_b = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK).withProperty(BlockOldLeaf.CHECK_DECAY, Boolean.valueOf(false));

    public WorldGenSwamp()
    {
        super(false);
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        int lvt_4_1_;

        for (lvt_4_1_ = rand.nextInt(4) + 5; worldIn.getBlockState(position.down()).getBlock().getMaterial() == Material.water; position = position.down())
        {
            ;
        }

        boolean lvt_5_1_ = true;

        if (position.getY() >= 1 && position.getY() + lvt_4_1_ + 1 <= 256)
        {
            for (int lvt_6_1_ = position.getY(); lvt_6_1_ <= position.getY() + 1 + lvt_4_1_; ++lvt_6_1_)
            {
                int lvt_7_1_ = 1;

                if (lvt_6_1_ == position.getY())
                {
                    lvt_7_1_ = 0;
                }

                if (lvt_6_1_ >= position.getY() + 1 + lvt_4_1_ - 2)
                {
                    lvt_7_1_ = 3;
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
                                if (lvt_11_1_ != Blocks.water && lvt_11_1_ != Blocks.flowing_water)
                                {
                                    lvt_5_1_ = false;
                                }
                                else if (lvt_6_1_ > position.getY())
                                {
                                    lvt_5_1_ = false;
                                }
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

                if ((lvt_6_2_ == Blocks.grass || lvt_6_2_ == Blocks.dirt) && position.getY() < 256 - lvt_4_1_ - 1)
                {
                    this.func_175921_a(worldIn, position.down());

                    for (int lvt_7_2_ = position.getY() - 3 + lvt_4_1_; lvt_7_2_ <= position.getY() + lvt_4_1_; ++lvt_7_2_)
                    {
                        int lvt_8_2_ = lvt_7_2_ - (position.getY() + lvt_4_1_);
                        int lvt_9_2_ = 2 - lvt_8_2_ / 2;

                        for (int lvt_10_2_ = position.getX() - lvt_9_2_; lvt_10_2_ <= position.getX() + lvt_9_2_; ++lvt_10_2_)
                        {
                            int lvt_11_2_ = lvt_10_2_ - position.getX();

                            for (int lvt_12_1_ = position.getZ() - lvt_9_2_; lvt_12_1_ <= position.getZ() + lvt_9_2_; ++lvt_12_1_)
                            {
                                int lvt_13_1_ = lvt_12_1_ - position.getZ();

                                if (Math.abs(lvt_11_2_) != lvt_9_2_ || Math.abs(lvt_13_1_) != lvt_9_2_ || rand.nextInt(2) != 0 && lvt_8_2_ != 0)
                                {
                                    BlockPos lvt_14_1_ = new BlockPos(lvt_10_2_, lvt_7_2_, lvt_12_1_);

                                    if (!worldIn.getBlockState(lvt_14_1_).getBlock().isFullBlock())
                                    {
                                        this.setBlockAndNotifyAdequately(worldIn, lvt_14_1_, field_181649_b);
                                    }
                                }
                            }
                        }
                    }

                    for (int lvt_7_3_ = 0; lvt_7_3_ < lvt_4_1_; ++lvt_7_3_)
                    {
                        Block lvt_8_3_ = worldIn.getBlockState(position.up(lvt_7_3_)).getBlock();

                        if (lvt_8_3_.getMaterial() == Material.air || lvt_8_3_.getMaterial() == Material.leaves || lvt_8_3_ == Blocks.flowing_water || lvt_8_3_ == Blocks.water)
                        {
                            this.setBlockAndNotifyAdequately(worldIn, position.up(lvt_7_3_), field_181648_a);
                        }
                    }

                    for (int lvt_7_4_ = position.getY() - 3 + lvt_4_1_; lvt_7_4_ <= position.getY() + lvt_4_1_; ++lvt_7_4_)
                    {
                        int lvt_8_4_ = lvt_7_4_ - (position.getY() + lvt_4_1_);
                        int lvt_9_3_ = 2 - lvt_8_4_ / 2;
                        BlockPos.MutableBlockPos lvt_10_3_ = new BlockPos.MutableBlockPos();

                        for (int lvt_11_3_ = position.getX() - lvt_9_3_; lvt_11_3_ <= position.getX() + lvt_9_3_; ++lvt_11_3_)
                        {
                            for (int lvt_12_2_ = position.getZ() - lvt_9_3_; lvt_12_2_ <= position.getZ() + lvt_9_3_; ++lvt_12_2_)
                            {
                                lvt_10_3_.set(lvt_11_3_, lvt_7_4_, lvt_12_2_);

                                if (worldIn.getBlockState(lvt_10_3_).getBlock().getMaterial() == Material.leaves)
                                {
                                    BlockPos lvt_13_2_ = lvt_10_3_.west();
                                    BlockPos lvt_14_2_ = lvt_10_3_.east();
                                    BlockPos lvt_15_1_ = lvt_10_3_.north();
                                    BlockPos lvt_16_1_ = lvt_10_3_.south();

                                    if (rand.nextInt(4) == 0 && worldIn.getBlockState(lvt_13_2_).getBlock().getMaterial() == Material.air)
                                    {
                                        this.func_181647_a(worldIn, lvt_13_2_, BlockVine.EAST);
                                    }

                                    if (rand.nextInt(4) == 0 && worldIn.getBlockState(lvt_14_2_).getBlock().getMaterial() == Material.air)
                                    {
                                        this.func_181647_a(worldIn, lvt_14_2_, BlockVine.WEST);
                                    }

                                    if (rand.nextInt(4) == 0 && worldIn.getBlockState(lvt_15_1_).getBlock().getMaterial() == Material.air)
                                    {
                                        this.func_181647_a(worldIn, lvt_15_1_, BlockVine.SOUTH);
                                    }

                                    if (rand.nextInt(4) == 0 && worldIn.getBlockState(lvt_16_1_).getBlock().getMaterial() == Material.air)
                                    {
                                        this.func_181647_a(worldIn, lvt_16_1_, BlockVine.NORTH);
                                    }
                                }
                            }
                        }
                    }

                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        else
        {
            return false;
        }
    }

    private void func_181647_a(World p_181647_1_, BlockPos p_181647_2_, PropertyBool p_181647_3_)
    {
        IBlockState lvt_4_1_ = Blocks.vine.getDefaultState().withProperty(p_181647_3_, Boolean.valueOf(true));
        this.setBlockAndNotifyAdequately(p_181647_1_, p_181647_2_, lvt_4_1_);
        int lvt_5_1_ = 4;

        for (p_181647_2_ = p_181647_2_.down(); p_181647_1_.getBlockState(p_181647_2_).getBlock().getMaterial() == Material.air && lvt_5_1_ > 0; --lvt_5_1_)
        {
            this.setBlockAndNotifyAdequately(p_181647_1_, p_181647_2_, lvt_4_1_);
            p_181647_2_ = p_181647_2_.down();
        }
    }
}
