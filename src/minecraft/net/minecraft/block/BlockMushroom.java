package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BlockMushroom extends BlockBush implements IGrowable
{
    protected BlockMushroom()
    {
        float lvt_1_1_ = 0.2F;
        this.setBlockBounds(0.5F - lvt_1_1_, 0.0F, 0.5F - lvt_1_1_, 0.5F + lvt_1_1_, lvt_1_1_ * 2.0F, 0.5F + lvt_1_1_);
        this.setTickRandomly(true);
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (rand.nextInt(25) == 0)
        {
            int lvt_5_1_ = 5;
            int lvt_6_1_ = 4;

            for (BlockPos lvt_8_1_ : BlockPos.getAllInBoxMutable(pos.add(-4, -1, -4), pos.add(4, 1, 4)))
            {
                if (worldIn.getBlockState(lvt_8_1_).getBlock() == this)
                {
                    --lvt_5_1_;

                    if (lvt_5_1_ <= 0)
                    {
                        return;
                    }
                }
            }

            BlockPos lvt_7_2_ = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);

            for (int lvt_8_2_ = 0; lvt_8_2_ < 4; ++lvt_8_2_)
            {
                if (worldIn.isAirBlock(lvt_7_2_) && this.canBlockStay(worldIn, lvt_7_2_, this.getDefaultState()))
                {
                    pos = lvt_7_2_;
                }

                lvt_7_2_ = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);
            }

            if (worldIn.isAirBlock(lvt_7_2_) && this.canBlockStay(worldIn, lvt_7_2_, this.getDefaultState()))
            {
                worldIn.setBlockState(lvt_7_2_, this.getDefaultState(), 2);
            }
        }
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return super.canPlaceBlockAt(worldIn, pos) && this.canBlockStay(worldIn, pos, this.getDefaultState());
    }

    /**
     * is the block grass, dirt or farmland
     */
    protected boolean canPlaceBlockOn(Block ground)
    {
        return ground.isFullBlock();
    }

    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        if (pos.getY() >= 0 && pos.getY() < 256)
        {
            IBlockState lvt_4_1_ = worldIn.getBlockState(pos.down());
            return lvt_4_1_.getBlock() == Blocks.mycelium ? true : (lvt_4_1_.getBlock() == Blocks.dirt && lvt_4_1_.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.PODZOL ? true : worldIn.getLight(pos) < 13 && this.canPlaceBlockOn(lvt_4_1_.getBlock()));
        }
        else
        {
            return false;
        }
    }

    public boolean generateBigMushroom(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        worldIn.setBlockToAir(pos);
        WorldGenerator lvt_5_1_ = null;

        if (this == Blocks.brown_mushroom)
        {
            lvt_5_1_ = new WorldGenBigMushroom(Blocks.brown_mushroom_block);
        }
        else if (this == Blocks.red_mushroom)
        {
            lvt_5_1_ = new WorldGenBigMushroom(Blocks.red_mushroom_block);
        }

        if (lvt_5_1_ != null && lvt_5_1_.generate(worldIn, rand, pos))
        {
            return true;
        }
        else
        {
            worldIn.setBlockState(pos, state, 3);
            return false;
        }
    }

    /**
     * Whether this IGrowable can grow
     */
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        return true;
    }

    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        return (double)rand.nextFloat() < 0.4D;
    }

    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        this.generateBigMushroom(worldIn, pos, state, rand);
    }
}
