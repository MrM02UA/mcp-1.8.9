package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class BiomeGenSwamp extends BiomeGenBase
{
    protected BiomeGenSwamp(int id)
    {
        super(id);
        this.theBiomeDecorator.treesPerChunk = 2;
        this.theBiomeDecorator.flowersPerChunk = 1;
        this.theBiomeDecorator.deadBushPerChunk = 1;
        this.theBiomeDecorator.mushroomsPerChunk = 8;
        this.theBiomeDecorator.reedsPerChunk = 10;
        this.theBiomeDecorator.clayPerChunk = 1;
        this.theBiomeDecorator.waterlilyPerChunk = 4;
        this.theBiomeDecorator.sandPerChunk2 = 0;
        this.theBiomeDecorator.sandPerChunk = 0;
        this.theBiomeDecorator.grassPerChunk = 5;
        this.waterColorMultiplier = 14745518;
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySlime.class, 1, 1, 1));
    }

    public WorldGenAbstractTree genBigTreeChance(Random rand)
    {
        return this.worldGeneratorSwamp;
    }

    public int getGrassColorAtPos(BlockPos pos)
    {
        double lvt_2_1_ = GRASS_COLOR_NOISE.func_151601_a((double)pos.getX() * 0.0225D, (double)pos.getZ() * 0.0225D);
        return lvt_2_1_ < -0.1D ? 5011004 : 6975545;
    }

    public int getFoliageColorAtPos(BlockPos pos)
    {
        return 6975545;
    }

    public BlockFlower.EnumFlowerType pickRandomFlower(Random rand, BlockPos pos)
    {
        return BlockFlower.EnumFlowerType.BLUE_ORCHID;
    }

    public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal)
    {
        double lvt_8_1_ = GRASS_COLOR_NOISE.func_151601_a((double)x * 0.25D, (double)z * 0.25D);

        if (lvt_8_1_ > 0.0D)
        {
            int lvt_10_1_ = x & 15;
            int lvt_11_1_ = z & 15;

            for (int lvt_12_1_ = 255; lvt_12_1_ >= 0; --lvt_12_1_)
            {
                if (chunkPrimerIn.getBlockState(lvt_11_1_, lvt_12_1_, lvt_10_1_).getBlock().getMaterial() != Material.air)
                {
                    if (lvt_12_1_ == 62 && chunkPrimerIn.getBlockState(lvt_11_1_, lvt_12_1_, lvt_10_1_).getBlock() != Blocks.water)
                    {
                        chunkPrimerIn.setBlockState(lvt_11_1_, lvt_12_1_, lvt_10_1_, Blocks.water.getDefaultState());

                        if (lvt_8_1_ < 0.12D)
                        {
                            chunkPrimerIn.setBlockState(lvt_11_1_, lvt_12_1_ + 1, lvt_10_1_, Blocks.waterlily.getDefaultState());
                        }
                    }

                    break;
                }
            }
        }

        this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
    }
}
