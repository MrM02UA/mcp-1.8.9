package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenSpikes extends WorldGenerator
{
    private Block baseBlockRequired;

    public WorldGenSpikes(Block p_i45464_1_)
    {
        this.baseBlockRequired = p_i45464_1_;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        if (worldIn.isAirBlock(position) && worldIn.getBlockState(position.down()).getBlock() == this.baseBlockRequired)
        {
            int lvt_4_1_ = rand.nextInt(32) + 6;
            int lvt_5_1_ = rand.nextInt(4) + 1;
            BlockPos.MutableBlockPos lvt_6_1_ = new BlockPos.MutableBlockPos();

            for (int lvt_7_1_ = position.getX() - lvt_5_1_; lvt_7_1_ <= position.getX() + lvt_5_1_; ++lvt_7_1_)
            {
                for (int lvt_8_1_ = position.getZ() - lvt_5_1_; lvt_8_1_ <= position.getZ() + lvt_5_1_; ++lvt_8_1_)
                {
                    int lvt_9_1_ = lvt_7_1_ - position.getX();
                    int lvt_10_1_ = lvt_8_1_ - position.getZ();

                    if (lvt_9_1_ * lvt_9_1_ + lvt_10_1_ * lvt_10_1_ <= lvt_5_1_ * lvt_5_1_ + 1 && worldIn.getBlockState(lvt_6_1_.set(lvt_7_1_, position.getY() - 1, lvt_8_1_)).getBlock() != this.baseBlockRequired)
                    {
                        return false;
                    }
                }
            }

            for (int lvt_7_2_ = position.getY(); lvt_7_2_ < position.getY() + lvt_4_1_ && lvt_7_2_ < 256; ++lvt_7_2_)
            {
                for (int lvt_8_2_ = position.getX() - lvt_5_1_; lvt_8_2_ <= position.getX() + lvt_5_1_; ++lvt_8_2_)
                {
                    for (int lvt_9_2_ = position.getZ() - lvt_5_1_; lvt_9_2_ <= position.getZ() + lvt_5_1_; ++lvt_9_2_)
                    {
                        int lvt_10_2_ = lvt_8_2_ - position.getX();
                        int lvt_11_1_ = lvt_9_2_ - position.getZ();

                        if (lvt_10_2_ * lvt_10_2_ + lvt_11_1_ * lvt_11_1_ <= lvt_5_1_ * lvt_5_1_ + 1)
                        {
                            worldIn.setBlockState(new BlockPos(lvt_8_2_, lvt_7_2_, lvt_9_2_), Blocks.obsidian.getDefaultState(), 2);
                        }
                    }
                }
            }

            Entity lvt_7_3_ = new EntityEnderCrystal(worldIn);
            lvt_7_3_.setLocationAndAngles((double)((float)position.getX() + 0.5F), (double)(position.getY() + lvt_4_1_), (double)((float)position.getZ() + 0.5F), rand.nextFloat() * 360.0F, 0.0F);
            worldIn.spawnEntityInWorld(lvt_7_3_);
            worldIn.setBlockState(position.up(lvt_4_1_), Blocks.bedrock.getDefaultState(), 2);
            return true;
        }
        else
        {
            return false;
        }
    }
}
