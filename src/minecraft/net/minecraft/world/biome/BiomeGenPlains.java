package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BiomeGenPlains extends BiomeGenBase
{
    protected boolean field_150628_aC;

    protected BiomeGenPlains(int id)
    {
        super(id);
        this.setTemperatureRainfall(0.8F, 0.4F);
        this.setHeight(height_LowPlains);
        this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityHorse.class, 5, 2, 6));
        this.theBiomeDecorator.treesPerChunk = -999;
        this.theBiomeDecorator.flowersPerChunk = 4;
        this.theBiomeDecorator.grassPerChunk = 10;
    }

    public BlockFlower.EnumFlowerType pickRandomFlower(Random rand, BlockPos pos)
    {
        double lvt_3_1_ = GRASS_COLOR_NOISE.func_151601_a((double)pos.getX() / 200.0D, (double)pos.getZ() / 200.0D);

        if (lvt_3_1_ < -0.8D)
        {
            int lvt_5_1_ = rand.nextInt(4);

            switch (lvt_5_1_)
            {
                case 0:
                    return BlockFlower.EnumFlowerType.ORANGE_TULIP;

                case 1:
                    return BlockFlower.EnumFlowerType.RED_TULIP;

                case 2:
                    return BlockFlower.EnumFlowerType.PINK_TULIP;

                case 3:
                default:
                    return BlockFlower.EnumFlowerType.WHITE_TULIP;
            }
        }
        else if (rand.nextInt(3) > 0)
        {
            int lvt_5_2_ = rand.nextInt(3);
            return lvt_5_2_ == 0 ? BlockFlower.EnumFlowerType.POPPY : (lvt_5_2_ == 1 ? BlockFlower.EnumFlowerType.HOUSTONIA : BlockFlower.EnumFlowerType.OXEYE_DAISY);
        }
        else
        {
            return BlockFlower.EnumFlowerType.DANDELION;
        }
    }

    public void decorate(World worldIn, Random rand, BlockPos pos)
    {
        double lvt_4_1_ = GRASS_COLOR_NOISE.func_151601_a((double)(pos.getX() + 8) / 200.0D, (double)(pos.getZ() + 8) / 200.0D);

        if (lvt_4_1_ < -0.8D)
        {
            this.theBiomeDecorator.flowersPerChunk = 15;
            this.theBiomeDecorator.grassPerChunk = 5;
        }
        else
        {
            this.theBiomeDecorator.flowersPerChunk = 4;
            this.theBiomeDecorator.grassPerChunk = 10;
            DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.GRASS);

            for (int lvt_6_1_ = 0; lvt_6_1_ < 7; ++lvt_6_1_)
            {
                int lvt_7_1_ = rand.nextInt(16) + 8;
                int lvt_8_1_ = rand.nextInt(16) + 8;
                int lvt_9_1_ = rand.nextInt(worldIn.getHeight(pos.add(lvt_7_1_, 0, lvt_8_1_)).getY() + 32);
                DOUBLE_PLANT_GENERATOR.generate(worldIn, rand, pos.add(lvt_7_1_, lvt_9_1_, lvt_8_1_));
            }
        }

        if (this.field_150628_aC)
        {
            DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.SUNFLOWER);

            for (int lvt_6_2_ = 0; lvt_6_2_ < 10; ++lvt_6_2_)
            {
                int lvt_7_2_ = rand.nextInt(16) + 8;
                int lvt_8_2_ = rand.nextInt(16) + 8;
                int lvt_9_2_ = rand.nextInt(worldIn.getHeight(pos.add(lvt_7_2_, 0, lvt_8_2_)).getY() + 32);
                DOUBLE_PLANT_GENERATOR.generate(worldIn, rand, pos.add(lvt_7_2_, lvt_9_2_, lvt_8_2_));
            }
        }

        super.decorate(worldIn, rand, pos);
    }

    protected BiomeGenBase createMutatedBiome(int p_180277_1_)
    {
        BiomeGenPlains lvt_2_1_ = new BiomeGenPlains(p_180277_1_);
        lvt_2_1_.setBiomeName("Sunflower Plains");
        lvt_2_1_.field_150628_aC = true;
        lvt_2_1_.setColor(9286496);
        lvt_2_1_.field_150609_ah = 14273354;
        return lvt_2_1_;
    }
}
