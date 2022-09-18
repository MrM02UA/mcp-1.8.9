package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class WorldGenTrees extends WorldGenAbstractTree
{
    private static final IBlockState field_181653_a = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.OAK);
    private static final IBlockState field_181654_b = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));

    /** The minimum height of a generated tree. */
    private final int minTreeHeight;

    /** True if this tree should grow Vines. */
    private final boolean vinesGrow;

    /** The metadata value of the wood to use in tree generation. */
    private final IBlockState metaWood;

    /** The metadata value of the leaves to use in tree generation. */
    private final IBlockState metaLeaves;

    public WorldGenTrees(boolean p_i2027_1_)
    {
        this(p_i2027_1_, 4, field_181653_a, field_181654_b, false);
    }

    public WorldGenTrees(boolean p_i46446_1_, int p_i46446_2_, IBlockState p_i46446_3_, IBlockState p_i46446_4_, boolean p_i46446_5_)
    {
        super(p_i46446_1_);
        this.minTreeHeight = p_i46446_2_;
        this.metaWood = p_i46446_3_;
        this.metaLeaves = p_i46446_4_;
        this.vinesGrow = p_i46446_5_;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        int lvt_4_1_ = rand.nextInt(3) + this.minTreeHeight;
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
                    lvt_7_1_ = 2;
                }

                BlockPos.MutableBlockPos lvt_8_1_ = new BlockPos.MutableBlockPos();

                for (int lvt_9_1_ = position.getX() - lvt_7_1_; lvt_9_1_ <= position.getX() + lvt_7_1_ && lvt_5_1_; ++lvt_9_1_)
                {
                    for (int lvt_10_1_ = position.getZ() - lvt_7_1_; lvt_10_1_ <= position.getZ() + lvt_7_1_ && lvt_5_1_; ++lvt_10_1_)
                    {
                        if (lvt_6_1_ >= 0 && lvt_6_1_ < 256)
                        {
                            if (!this.func_150523_a(worldIn.getBlockState(lvt_8_1_.set(lvt_9_1_, lvt_6_1_, lvt_10_1_)).getBlock()))
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

                if ((lvt_6_2_ == Blocks.grass || lvt_6_2_ == Blocks.dirt || lvt_6_2_ == Blocks.farmland) && position.getY() < 256 - lvt_4_1_ - 1)
                {
                    this.func_175921_a(worldIn, position.down());
                    int lvt_7_2_ = 3;
                    int lvt_8_2_ = 0;

                    for (int lvt_9_2_ = position.getY() - lvt_7_2_ + lvt_4_1_; lvt_9_2_ <= position.getY() + lvt_4_1_; ++lvt_9_2_)
                    {
                        int lvt_10_2_ = lvt_9_2_ - (position.getY() + lvt_4_1_);
                        int lvt_11_1_ = lvt_8_2_ + 1 - lvt_10_2_ / 2;

                        for (int lvt_12_1_ = position.getX() - lvt_11_1_; lvt_12_1_ <= position.getX() + lvt_11_1_; ++lvt_12_1_)
                        {
                            int lvt_13_1_ = lvt_12_1_ - position.getX();

                            for (int lvt_14_1_ = position.getZ() - lvt_11_1_; lvt_14_1_ <= position.getZ() + lvt_11_1_; ++lvt_14_1_)
                            {
                                int lvt_15_1_ = lvt_14_1_ - position.getZ();

                                if (Math.abs(lvt_13_1_) != lvt_11_1_ || Math.abs(lvt_15_1_) != lvt_11_1_ || rand.nextInt(2) != 0 && lvt_10_2_ != 0)
                                {
                                    BlockPos lvt_16_1_ = new BlockPos(lvt_12_1_, lvt_9_2_, lvt_14_1_);
                                    Block lvt_17_1_ = worldIn.getBlockState(lvt_16_1_).getBlock();

                                    if (lvt_17_1_.getMaterial() == Material.air || lvt_17_1_.getMaterial() == Material.leaves || lvt_17_1_.getMaterial() == Material.vine)
                                    {
                                        this.setBlockAndNotifyAdequately(worldIn, lvt_16_1_, this.metaLeaves);
                                    }
                                }
                            }
                        }
                    }

                    for (int lvt_9_3_ = 0; lvt_9_3_ < lvt_4_1_; ++lvt_9_3_)
                    {
                        Block lvt_10_3_ = worldIn.getBlockState(position.up(lvt_9_3_)).getBlock();

                        if (lvt_10_3_.getMaterial() == Material.air || lvt_10_3_.getMaterial() == Material.leaves || lvt_10_3_.getMaterial() == Material.vine)
                        {
                            this.setBlockAndNotifyAdequately(worldIn, position.up(lvt_9_3_), this.metaWood);

                            if (this.vinesGrow && lvt_9_3_ > 0)
                            {
                                if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(-1, lvt_9_3_, 0)))
                                {
                                    this.func_181651_a(worldIn, position.add(-1, lvt_9_3_, 0), BlockVine.EAST);
                                }

                                if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(1, lvt_9_3_, 0)))
                                {
                                    this.func_181651_a(worldIn, position.add(1, lvt_9_3_, 0), BlockVine.WEST);
                                }

                                if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(0, lvt_9_3_, -1)))
                                {
                                    this.func_181651_a(worldIn, position.add(0, lvt_9_3_, -1), BlockVine.SOUTH);
                                }

                                if (rand.nextInt(3) > 0 && worldIn.isAirBlock(position.add(0, lvt_9_3_, 1)))
                                {
                                    this.func_181651_a(worldIn, position.add(0, lvt_9_3_, 1), BlockVine.NORTH);
                                }
                            }
                        }
                    }

                    if (this.vinesGrow)
                    {
                        for (int lvt_9_4_ = position.getY() - 3 + lvt_4_1_; lvt_9_4_ <= position.getY() + lvt_4_1_; ++lvt_9_4_)
                        {
                            int lvt_10_4_ = lvt_9_4_ - (position.getY() + lvt_4_1_);
                            int lvt_11_2_ = 2 - lvt_10_4_ / 2;
                            BlockPos.MutableBlockPos lvt_12_2_ = new BlockPos.MutableBlockPos();

                            for (int lvt_13_2_ = position.getX() - lvt_11_2_; lvt_13_2_ <= position.getX() + lvt_11_2_; ++lvt_13_2_)
                            {
                                for (int lvt_14_2_ = position.getZ() - lvt_11_2_; lvt_14_2_ <= position.getZ() + lvt_11_2_; ++lvt_14_2_)
                                {
                                    lvt_12_2_.set(lvt_13_2_, lvt_9_4_, lvt_14_2_);

                                    if (worldIn.getBlockState(lvt_12_2_).getBlock().getMaterial() == Material.leaves)
                                    {
                                        BlockPos lvt_15_2_ = lvt_12_2_.west();
                                        BlockPos lvt_16_2_ = lvt_12_2_.east();
                                        BlockPos lvt_17_2_ = lvt_12_2_.north();
                                        BlockPos lvt_18_1_ = lvt_12_2_.south();

                                        if (rand.nextInt(4) == 0 && worldIn.getBlockState(lvt_15_2_).getBlock().getMaterial() == Material.air)
                                        {
                                            this.func_181650_b(worldIn, lvt_15_2_, BlockVine.EAST);
                                        }

                                        if (rand.nextInt(4) == 0 && worldIn.getBlockState(lvt_16_2_).getBlock().getMaterial() == Material.air)
                                        {
                                            this.func_181650_b(worldIn, lvt_16_2_, BlockVine.WEST);
                                        }

                                        if (rand.nextInt(4) == 0 && worldIn.getBlockState(lvt_17_2_).getBlock().getMaterial() == Material.air)
                                        {
                                            this.func_181650_b(worldIn, lvt_17_2_, BlockVine.SOUTH);
                                        }

                                        if (rand.nextInt(4) == 0 && worldIn.getBlockState(lvt_18_1_).getBlock().getMaterial() == Material.air)
                                        {
                                            this.func_181650_b(worldIn, lvt_18_1_, BlockVine.NORTH);
                                        }
                                    }
                                }
                            }
                        }

                        if (rand.nextInt(5) == 0 && lvt_4_1_ > 5)
                        {
                            for (int lvt_9_5_ = 0; lvt_9_5_ < 2; ++lvt_9_5_)
                            {
                                for (EnumFacing lvt_11_3_ : EnumFacing.Plane.HORIZONTAL)
                                {
                                    if (rand.nextInt(4 - lvt_9_5_) == 0)
                                    {
                                        EnumFacing lvt_12_3_ = lvt_11_3_.getOpposite();
                                        this.func_181652_a(worldIn, rand.nextInt(3), position.add(lvt_12_3_.getFrontOffsetX(), lvt_4_1_ - 5 + lvt_9_5_, lvt_12_3_.getFrontOffsetZ()), lvt_11_3_);
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

    private void func_181652_a(World p_181652_1_, int p_181652_2_, BlockPos p_181652_3_, EnumFacing p_181652_4_)
    {
        this.setBlockAndNotifyAdequately(p_181652_1_, p_181652_3_, Blocks.cocoa.getDefaultState().withProperty(BlockCocoa.AGE, Integer.valueOf(p_181652_2_)).withProperty(BlockCocoa.FACING, p_181652_4_));
    }

    private void func_181651_a(World p_181651_1_, BlockPos p_181651_2_, PropertyBool p_181651_3_)
    {
        this.setBlockAndNotifyAdequately(p_181651_1_, p_181651_2_, Blocks.vine.getDefaultState().withProperty(p_181651_3_, Boolean.valueOf(true)));
    }

    private void func_181650_b(World p_181650_1_, BlockPos p_181650_2_, PropertyBool p_181650_3_)
    {
        this.func_181651_a(p_181650_1_, p_181650_2_, p_181650_3_);
        int lvt_4_1_ = 4;

        for (p_181650_2_ = p_181650_2_.down(); p_181650_1_.getBlockState(p_181650_2_).getBlock().getMaterial() == Material.air && lvt_4_1_ > 0; --lvt_4_1_)
        {
            this.func_181651_a(p_181650_1_, p_181650_2_, p_181650_3_);
            p_181650_2_ = p_181650_2_.down();
        }
    }
}
