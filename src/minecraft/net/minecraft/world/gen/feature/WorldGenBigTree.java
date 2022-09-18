package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenBigTree extends WorldGenAbstractTree
{
    private Random rand;
    private World world;
    private BlockPos basePos = BlockPos.ORIGIN;
    int heightLimit;
    int height;
    double heightAttenuation = 0.618D;
    double branchSlope = 0.381D;
    double scaleWidth = 1.0D;
    double leafDensity = 1.0D;
    int trunkSize = 1;
    int heightLimitLimit = 12;

    /**
     * Sets the distance limit for how far away the generator will populate leaves from the base leaf node.
     */
    int leafDistanceLimit = 4;
    List<WorldGenBigTree.FoliageCoordinates> field_175948_j;

    public WorldGenBigTree(boolean p_i2008_1_)
    {
        super(p_i2008_1_);
    }

    /**
     * Generates a list of leaf nodes for the tree, to be populated by generateLeaves.
     */
    void generateLeafNodeList()
    {
        this.height = (int)((double)this.heightLimit * this.heightAttenuation);

        if (this.height >= this.heightLimit)
        {
            this.height = this.heightLimit - 1;
        }

        int lvt_1_1_ = (int)(1.382D + Math.pow(this.leafDensity * (double)this.heightLimit / 13.0D, 2.0D));

        if (lvt_1_1_ < 1)
        {
            lvt_1_1_ = 1;
        }

        int lvt_2_1_ = this.basePos.getY() + this.height;
        int lvt_3_1_ = this.heightLimit - this.leafDistanceLimit;
        this.field_175948_j = Lists.newArrayList();
        this.field_175948_j.add(new WorldGenBigTree.FoliageCoordinates(this.basePos.up(lvt_3_1_), lvt_2_1_));

        for (; lvt_3_1_ >= 0; --lvt_3_1_)
        {
            float lvt_4_1_ = this.layerSize(lvt_3_1_);

            if (lvt_4_1_ >= 0.0F)
            {
                for (int lvt_5_1_ = 0; lvt_5_1_ < lvt_1_1_; ++lvt_5_1_)
                {
                    double lvt_6_1_ = this.scaleWidth * (double)lvt_4_1_ * ((double)this.rand.nextFloat() + 0.328D);
                    double lvt_8_1_ = (double)(this.rand.nextFloat() * 2.0F) * Math.PI;
                    double lvt_10_1_ = lvt_6_1_ * Math.sin(lvt_8_1_) + 0.5D;
                    double lvt_12_1_ = lvt_6_1_ * Math.cos(lvt_8_1_) + 0.5D;
                    BlockPos lvt_14_1_ = this.basePos.add(lvt_10_1_, (double)(lvt_3_1_ - 1), lvt_12_1_);
                    BlockPos lvt_15_1_ = lvt_14_1_.up(this.leafDistanceLimit);

                    if (this.checkBlockLine(lvt_14_1_, lvt_15_1_) == -1)
                    {
                        int lvt_16_1_ = this.basePos.getX() - lvt_14_1_.getX();
                        int lvt_17_1_ = this.basePos.getZ() - lvt_14_1_.getZ();
                        double lvt_18_1_ = (double)lvt_14_1_.getY() - Math.sqrt((double)(lvt_16_1_ * lvt_16_1_ + lvt_17_1_ * lvt_17_1_)) * this.branchSlope;
                        int lvt_20_1_ = lvt_18_1_ > (double)lvt_2_1_ ? lvt_2_1_ : (int)lvt_18_1_;
                        BlockPos lvt_21_1_ = new BlockPos(this.basePos.getX(), lvt_20_1_, this.basePos.getZ());

                        if (this.checkBlockLine(lvt_21_1_, lvt_14_1_) == -1)
                        {
                            this.field_175948_j.add(new WorldGenBigTree.FoliageCoordinates(lvt_14_1_, lvt_21_1_.getY()));
                        }
                    }
                }
            }
        }
    }

    void func_181631_a(BlockPos p_181631_1_, float p_181631_2_, IBlockState p_181631_3_)
    {
        int lvt_4_1_ = (int)((double)p_181631_2_ + 0.618D);

        for (int lvt_5_1_ = -lvt_4_1_; lvt_5_1_ <= lvt_4_1_; ++lvt_5_1_)
        {
            for (int lvt_6_1_ = -lvt_4_1_; lvt_6_1_ <= lvt_4_1_; ++lvt_6_1_)
            {
                if (Math.pow((double)Math.abs(lvt_5_1_) + 0.5D, 2.0D) + Math.pow((double)Math.abs(lvt_6_1_) + 0.5D, 2.0D) <= (double)(p_181631_2_ * p_181631_2_))
                {
                    BlockPos lvt_7_1_ = p_181631_1_.add(lvt_5_1_, 0, lvt_6_1_);
                    Material lvt_8_1_ = this.world.getBlockState(lvt_7_1_).getBlock().getMaterial();

                    if (lvt_8_1_ == Material.air || lvt_8_1_ == Material.leaves)
                    {
                        this.setBlockAndNotifyAdequately(this.world, lvt_7_1_, p_181631_3_);
                    }
                }
            }
        }
    }

    /**
     * Gets the rough size of a layer of the tree.
     */
    float layerSize(int p_76490_1_)
    {
        if ((float)p_76490_1_ < (float)this.heightLimit * 0.3F)
        {
            return -1.0F;
        }
        else
        {
            float lvt_2_1_ = (float)this.heightLimit / 2.0F;
            float lvt_3_1_ = lvt_2_1_ - (float)p_76490_1_;
            float lvt_4_1_ = MathHelper.sqrt_float(lvt_2_1_ * lvt_2_1_ - lvt_3_1_ * lvt_3_1_);

            if (lvt_3_1_ == 0.0F)
            {
                lvt_4_1_ = lvt_2_1_;
            }
            else if (Math.abs(lvt_3_1_) >= lvt_2_1_)
            {
                return 0.0F;
            }

            return lvt_4_1_ * 0.5F;
        }
    }

    float leafSize(int p_76495_1_)
    {
        return p_76495_1_ >= 0 && p_76495_1_ < this.leafDistanceLimit ? (p_76495_1_ != 0 && p_76495_1_ != this.leafDistanceLimit - 1 ? 3.0F : 2.0F) : -1.0F;
    }

    /**
     * Generates the leaves surrounding an individual entry in the leafNodes list.
     */
    void generateLeafNode(BlockPos pos)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < this.leafDistanceLimit; ++lvt_2_1_)
        {
            this.func_181631_a(pos.up(lvt_2_1_), this.leafSize(lvt_2_1_), Blocks.leaves.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false)));
        }
    }

    void func_175937_a(BlockPos p_175937_1_, BlockPos p_175937_2_, Block p_175937_3_)
    {
        BlockPos lvt_4_1_ = p_175937_2_.add(-p_175937_1_.getX(), -p_175937_1_.getY(), -p_175937_1_.getZ());
        int lvt_5_1_ = this.getGreatestDistance(lvt_4_1_);
        float lvt_6_1_ = (float)lvt_4_1_.getX() / (float)lvt_5_1_;
        float lvt_7_1_ = (float)lvt_4_1_.getY() / (float)lvt_5_1_;
        float lvt_8_1_ = (float)lvt_4_1_.getZ() / (float)lvt_5_1_;

        for (int lvt_9_1_ = 0; lvt_9_1_ <= lvt_5_1_; ++lvt_9_1_)
        {
            BlockPos lvt_10_1_ = p_175937_1_.add((double)(0.5F + (float)lvt_9_1_ * lvt_6_1_), (double)(0.5F + (float)lvt_9_1_ * lvt_7_1_), (double)(0.5F + (float)lvt_9_1_ * lvt_8_1_));
            BlockLog.EnumAxis lvt_11_1_ = this.func_175938_b(p_175937_1_, lvt_10_1_);
            this.setBlockAndNotifyAdequately(this.world, lvt_10_1_, p_175937_3_.getDefaultState().withProperty(BlockLog.LOG_AXIS, lvt_11_1_));
        }
    }

    /**
     * Returns the absolute greatest distance in the BlockPos object.
     */
    private int getGreatestDistance(BlockPos posIn)
    {
        int lvt_2_1_ = MathHelper.abs_int(posIn.getX());
        int lvt_3_1_ = MathHelper.abs_int(posIn.getY());
        int lvt_4_1_ = MathHelper.abs_int(posIn.getZ());
        return lvt_4_1_ > lvt_2_1_ && lvt_4_1_ > lvt_3_1_ ? lvt_4_1_ : (lvt_3_1_ > lvt_2_1_ ? lvt_3_1_ : lvt_2_1_);
    }

    private BlockLog.EnumAxis func_175938_b(BlockPos p_175938_1_, BlockPos p_175938_2_)
    {
        BlockLog.EnumAxis lvt_3_1_ = BlockLog.EnumAxis.Y;
        int lvt_4_1_ = Math.abs(p_175938_2_.getX() - p_175938_1_.getX());
        int lvt_5_1_ = Math.abs(p_175938_2_.getZ() - p_175938_1_.getZ());
        int lvt_6_1_ = Math.max(lvt_4_1_, lvt_5_1_);

        if (lvt_6_1_ > 0)
        {
            if (lvt_4_1_ == lvt_6_1_)
            {
                lvt_3_1_ = BlockLog.EnumAxis.X;
            }
            else if (lvt_5_1_ == lvt_6_1_)
            {
                lvt_3_1_ = BlockLog.EnumAxis.Z;
            }
        }

        return lvt_3_1_;
    }

    /**
     * Generates the leaf portion of the tree as specified by the leafNodes list.
     */
    void generateLeaves()
    {
        for (WorldGenBigTree.FoliageCoordinates lvt_2_1_ : this.field_175948_j)
        {
            this.generateLeafNode(lvt_2_1_);
        }
    }

    /**
     * Indicates whether or not a leaf node requires additional wood to be added to preserve integrity.
     */
    boolean leafNodeNeedsBase(int p_76493_1_)
    {
        return (double)p_76493_1_ >= (double)this.heightLimit * 0.2D;
    }

    /**
     * Places the trunk for the big tree that is being generated. Able to generate double-sized trunks by changing a
     * field that is always 1 to 2.
     */
    void generateTrunk()
    {
        BlockPos lvt_1_1_ = this.basePos;
        BlockPos lvt_2_1_ = this.basePos.up(this.height);
        Block lvt_3_1_ = Blocks.log;
        this.func_175937_a(lvt_1_1_, lvt_2_1_, lvt_3_1_);

        if (this.trunkSize == 2)
        {
            this.func_175937_a(lvt_1_1_.east(), lvt_2_1_.east(), lvt_3_1_);
            this.func_175937_a(lvt_1_1_.east().south(), lvt_2_1_.east().south(), lvt_3_1_);
            this.func_175937_a(lvt_1_1_.south(), lvt_2_1_.south(), lvt_3_1_);
        }
    }

    /**
     * Generates additional wood blocks to fill out the bases of different leaf nodes that would otherwise degrade.
     */
    void generateLeafNodeBases()
    {
        for (WorldGenBigTree.FoliageCoordinates lvt_2_1_ : this.field_175948_j)
        {
            int lvt_3_1_ = lvt_2_1_.func_177999_q();
            BlockPos lvt_4_1_ = new BlockPos(this.basePos.getX(), lvt_3_1_, this.basePos.getZ());

            if (!lvt_4_1_.equals(lvt_2_1_) && this.leafNodeNeedsBase(lvt_3_1_ - this.basePos.getY()))
            {
                this.func_175937_a(lvt_4_1_, lvt_2_1_, Blocks.log);
            }
        }
    }

    /**
     * Checks a line of blocks in the world from the first coordinate to triplet to the second, returning the distance
     * (in blocks) before a non-air, non-leaf block is encountered and/or the end is encountered.
     */
    int checkBlockLine(BlockPos posOne, BlockPos posTwo)
    {
        BlockPos lvt_3_1_ = posTwo.add(-posOne.getX(), -posOne.getY(), -posOne.getZ());
        int lvt_4_1_ = this.getGreatestDistance(lvt_3_1_);
        float lvt_5_1_ = (float)lvt_3_1_.getX() / (float)lvt_4_1_;
        float lvt_6_1_ = (float)lvt_3_1_.getY() / (float)lvt_4_1_;
        float lvt_7_1_ = (float)lvt_3_1_.getZ() / (float)lvt_4_1_;

        if (lvt_4_1_ == 0)
        {
            return -1;
        }
        else
        {
            for (int lvt_8_1_ = 0; lvt_8_1_ <= lvt_4_1_; ++lvt_8_1_)
            {
                BlockPos lvt_9_1_ = posOne.add((double)(0.5F + (float)lvt_8_1_ * lvt_5_1_), (double)(0.5F + (float)lvt_8_1_ * lvt_6_1_), (double)(0.5F + (float)lvt_8_1_ * lvt_7_1_));

                if (!this.func_150523_a(this.world.getBlockState(lvt_9_1_).getBlock()))
                {
                    return lvt_8_1_;
                }
            }

            return -1;
        }
    }

    public void func_175904_e()
    {
        this.leafDistanceLimit = 5;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        this.world = worldIn;
        this.basePos = position;
        this.rand = new Random(rand.nextLong());

        if (this.heightLimit == 0)
        {
            this.heightLimit = 5 + this.rand.nextInt(this.heightLimitLimit);
        }

        if (!this.validTreeLocation())
        {
            return false;
        }
        else
        {
            this.generateLeafNodeList();
            this.generateLeaves();
            this.generateTrunk();
            this.generateLeafNodeBases();
            return true;
        }
    }

    /**
     * Returns a boolean indicating whether or not the current location for the tree, spanning basePos to to the height
     * limit, is valid.
     */
    private boolean validTreeLocation()
    {
        Block lvt_1_1_ = this.world.getBlockState(this.basePos.down()).getBlock();

        if (lvt_1_1_ != Blocks.dirt && lvt_1_1_ != Blocks.grass && lvt_1_1_ != Blocks.farmland)
        {
            return false;
        }
        else
        {
            int lvt_2_1_ = this.checkBlockLine(this.basePos, this.basePos.up(this.heightLimit - 1));

            if (lvt_2_1_ == -1)
            {
                return true;
            }
            else if (lvt_2_1_ < 6)
            {
                return false;
            }
            else
            {
                this.heightLimit = lvt_2_1_;
                return true;
            }
        }
    }

    static class FoliageCoordinates extends BlockPos
    {
        private final int field_178000_b;

        public FoliageCoordinates(BlockPos p_i45635_1_, int p_i45635_2_)
        {
            super(p_i45635_1_.getX(), p_i45635_1_.getY(), p_i45635_1_.getZ());
            this.field_178000_b = p_i45635_2_;
        }

        public int func_177999_q()
        {
            return this.field_178000_b;
        }
    }
}
