package net.minecraft.world.gen.feature;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class WorldGeneratorBonusChest extends WorldGenerator
{
    private final List<WeightedRandomChestContent> chestItems;

    /**
     * Value of this int will determine how much items gonna generate in Bonus Chest.
     */
    private final int itemsToGenerateInBonusChest;

    public WorldGeneratorBonusChest(List<WeightedRandomChestContent> p_i45634_1_, int p_i45634_2_)
    {
        this.chestItems = p_i45634_1_;
        this.itemsToGenerateInBonusChest = p_i45634_2_;
    }

    public boolean generate(World worldIn, Random rand, BlockPos position)
    {
        Block lvt_4_1_;

        while (((lvt_4_1_ = worldIn.getBlockState(position).getBlock()).getMaterial() == Material.air || lvt_4_1_.getMaterial() == Material.leaves) && position.getY() > 1)
        {
            position = position.down();
        }

        if (position.getY() < 1)
        {
            return false;
        }
        else
        {
            position = position.up();

            for (int lvt_5_1_ = 0; lvt_5_1_ < 4; ++lvt_5_1_)
            {
                BlockPos lvt_6_1_ = position.add(rand.nextInt(4) - rand.nextInt(4), rand.nextInt(3) - rand.nextInt(3), rand.nextInt(4) - rand.nextInt(4));

                if (worldIn.isAirBlock(lvt_6_1_) && World.doesBlockHaveSolidTopSurface(worldIn, lvt_6_1_.down()))
                {
                    worldIn.setBlockState(lvt_6_1_, Blocks.chest.getDefaultState(), 2);
                    TileEntity lvt_7_1_ = worldIn.getTileEntity(lvt_6_1_);

                    if (lvt_7_1_ instanceof TileEntityChest)
                    {
                        WeightedRandomChestContent.generateChestContents(rand, this.chestItems, (TileEntityChest)lvt_7_1_, this.itemsToGenerateInBonusChest);
                    }

                    BlockPos lvt_8_1_ = lvt_6_1_.east();
                    BlockPos lvt_9_1_ = lvt_6_1_.west();
                    BlockPos lvt_10_1_ = lvt_6_1_.north();
                    BlockPos lvt_11_1_ = lvt_6_1_.south();

                    if (worldIn.isAirBlock(lvt_9_1_) && World.doesBlockHaveSolidTopSurface(worldIn, lvt_9_1_.down()))
                    {
                        worldIn.setBlockState(lvt_9_1_, Blocks.torch.getDefaultState(), 2);
                    }

                    if (worldIn.isAirBlock(lvt_8_1_) && World.doesBlockHaveSolidTopSurface(worldIn, lvt_8_1_.down()))
                    {
                        worldIn.setBlockState(lvt_8_1_, Blocks.torch.getDefaultState(), 2);
                    }

                    if (worldIn.isAirBlock(lvt_10_1_) && World.doesBlockHaveSolidTopSurface(worldIn, lvt_10_1_.down()))
                    {
                        worldIn.setBlockState(lvt_10_1_, Blocks.torch.getDefaultState(), 2);
                    }

                    if (worldIn.isAirBlock(lvt_11_1_) && World.doesBlockHaveSolidTopSurface(worldIn, lvt_11_1_.down()))
                    {
                        worldIn.setBlockState(lvt_11_1_, Blocks.torch.getDefaultState(), 2);
                    }

                    return true;
                }
            }

            return false;
        }
    }
}
