package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenDesertWells;

public class BiomeGenDesert extends BiomeGenBase
{
    public BiomeGenDesert(int id)
    {
        super(id);
        this.spawnableCreatureList.clear();
        this.topBlock = Blocks.sand.getDefaultState();
        this.fillerBlock = Blocks.sand.getDefaultState();
        this.theBiomeDecorator.treesPerChunk = -999;
        this.theBiomeDecorator.deadBushPerChunk = 2;
        this.theBiomeDecorator.reedsPerChunk = 50;
        this.theBiomeDecorator.cactiPerChunk = 10;
        this.spawnableCreatureList.clear();
    }

    public void decorate(World worldIn, Random rand, BlockPos pos)
    {
        super.decorate(worldIn, rand, pos);

        if (rand.nextInt(1000) == 0)
        {
            int lvt_4_1_ = rand.nextInt(16) + 8;
            int lvt_5_1_ = rand.nextInt(16) + 8;
            BlockPos lvt_6_1_ = worldIn.getHeight(pos.add(lvt_4_1_, 0, lvt_5_1_)).up();
            (new WorldGenDesertWells()).generate(worldIn, rand, lvt_6_1_);
        }
    }
}
