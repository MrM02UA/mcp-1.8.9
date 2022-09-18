package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import net.minecraft.world.gen.feature.WorldGenForest;

public class BiomeGenForest extends BiomeGenBase
{
    private int field_150632_aF;
    protected static final WorldGenForest field_150629_aC = new WorldGenForest(false, true);
    protected static final WorldGenForest field_150630_aD = new WorldGenForest(false, false);
    protected static final WorldGenCanopyTree field_150631_aE = new WorldGenCanopyTree(false);

    public BiomeGenForest(int id, int p_i45377_2_)
    {
        super(id);
        this.field_150632_aF = p_i45377_2_;
        this.theBiomeDecorator.treesPerChunk = 10;
        this.theBiomeDecorator.grassPerChunk = 2;

        if (this.field_150632_aF == 1)
        {
            this.theBiomeDecorator.treesPerChunk = 6;
            this.theBiomeDecorator.flowersPerChunk = 100;
            this.theBiomeDecorator.grassPerChunk = 1;
        }

        this.setFillerBlockMetadata(5159473);
        this.setTemperatureRainfall(0.7F, 0.8F);

        if (this.field_150632_aF == 2)
        {
            this.field_150609_ah = 353825;
            this.color = 3175492;
            this.setTemperatureRainfall(0.6F, 0.6F);
        }

        if (this.field_150632_aF == 0)
        {
            this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityWolf.class, 5, 4, 4));
        }

        if (this.field_150632_aF == 3)
        {
            this.theBiomeDecorator.treesPerChunk = -999;
        }
    }

    protected BiomeGenBase func_150557_a(int colorIn, boolean p_150557_2_)
    {
        if (this.field_150632_aF == 2)
        {
            this.field_150609_ah = 353825;
            this.color = colorIn;

            if (p_150557_2_)
            {
                this.field_150609_ah = (this.field_150609_ah & 16711422) >> 1;
            }

            return this;
        }
        else
        {
            return super.func_150557_a(colorIn, p_150557_2_);
        }
    }

    public WorldGenAbstractTree genBigTreeChance(Random rand)
    {
        return (WorldGenAbstractTree)(this.field_150632_aF == 3 && rand.nextInt(3) > 0 ? field_150631_aE : (this.field_150632_aF != 2 && rand.nextInt(5) != 0 ? this.worldGeneratorTrees : field_150630_aD));
    }

    public BlockFlower.EnumFlowerType pickRandomFlower(Random rand, BlockPos pos)
    {
        if (this.field_150632_aF == 1)
        {
            double lvt_3_1_ = MathHelper.clamp_double((1.0D + GRASS_COLOR_NOISE.func_151601_a((double)pos.getX() / 48.0D, (double)pos.getZ() / 48.0D)) / 2.0D, 0.0D, 0.9999D);
            BlockFlower.EnumFlowerType lvt_5_1_ = BlockFlower.EnumFlowerType.values()[(int)(lvt_3_1_ * (double)BlockFlower.EnumFlowerType.values().length)];
            return lvt_5_1_ == BlockFlower.EnumFlowerType.BLUE_ORCHID ? BlockFlower.EnumFlowerType.POPPY : lvt_5_1_;
        }
        else
        {
            return super.pickRandomFlower(rand, pos);
        }
    }

    public void decorate(World worldIn, Random rand, BlockPos pos)
    {
        if (this.field_150632_aF == 3)
        {
            for (int lvt_4_1_ = 0; lvt_4_1_ < 4; ++lvt_4_1_)
            {
                for (int lvt_5_1_ = 0; lvt_5_1_ < 4; ++lvt_5_1_)
                {
                    int lvt_6_1_ = lvt_4_1_ * 4 + 1 + 8 + rand.nextInt(3);
                    int lvt_7_1_ = lvt_5_1_ * 4 + 1 + 8 + rand.nextInt(3);
                    BlockPos lvt_8_1_ = worldIn.getHeight(pos.add(lvt_6_1_, 0, lvt_7_1_));

                    if (rand.nextInt(20) == 0)
                    {
                        WorldGenBigMushroom lvt_9_1_ = new WorldGenBigMushroom();
                        lvt_9_1_.generate(worldIn, rand, lvt_8_1_);
                    }
                    else
                    {
                        WorldGenAbstractTree lvt_9_2_ = this.genBigTreeChance(rand);
                        lvt_9_2_.func_175904_e();

                        if (lvt_9_2_.generate(worldIn, rand, lvt_8_1_))
                        {
                            lvt_9_2_.func_180711_a(worldIn, rand, lvt_8_1_);
                        }
                    }
                }
            }
        }

        int lvt_4_2_ = rand.nextInt(5) - 3;

        if (this.field_150632_aF == 1)
        {
            lvt_4_2_ += 2;
        }

        for (int lvt_5_2_ = 0; lvt_5_2_ < lvt_4_2_; ++lvt_5_2_)
        {
            int lvt_6_2_ = rand.nextInt(3);

            if (lvt_6_2_ == 0)
            {
                DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.SYRINGA);
            }
            else if (lvt_6_2_ == 1)
            {
                DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.ROSE);
            }
            else if (lvt_6_2_ == 2)
            {
                DOUBLE_PLANT_GENERATOR.setPlantType(BlockDoublePlant.EnumPlantType.PAEONIA);
            }

            for (int lvt_7_2_ = 0; lvt_7_2_ < 5; ++lvt_7_2_)
            {
                int lvt_8_2_ = rand.nextInt(16) + 8;
                int lvt_9_3_ = rand.nextInt(16) + 8;
                int lvt_10_1_ = rand.nextInt(worldIn.getHeight(pos.add(lvt_8_2_, 0, lvt_9_3_)).getY() + 32);

                if (DOUBLE_PLANT_GENERATOR.generate(worldIn, rand, new BlockPos(pos.getX() + lvt_8_2_, lvt_10_1_, pos.getZ() + lvt_9_3_)))
                {
                    break;
                }
            }
        }

        super.decorate(worldIn, rand, pos);
    }

    public int getGrassColorAtPos(BlockPos pos)
    {
        int lvt_2_1_ = super.getGrassColorAtPos(pos);
        return this.field_150632_aF == 3 ? (lvt_2_1_ & 16711422) + 2634762 >> 1 : lvt_2_1_;
    }

    protected BiomeGenBase createMutatedBiome(final int p_180277_1_)
    {
        if (this.biomeID == BiomeGenBase.forest.biomeID)
        {
            BiomeGenForest lvt_2_1_ = new BiomeGenForest(p_180277_1_, 1);
            lvt_2_1_.setHeight(new BiomeGenBase.Height(this.minHeight, this.maxHeight + 0.2F));
            lvt_2_1_.setBiomeName("Flower Forest");
            lvt_2_1_.func_150557_a(6976549, true);
            lvt_2_1_.setFillerBlockMetadata(8233509);
            return lvt_2_1_;
        }
        else
        {
            return this.biomeID != BiomeGenBase.birchForest.biomeID && this.biomeID != BiomeGenBase.birchForestHills.biomeID ? new BiomeGenMutated(p_180277_1_, this)
            {
                public void decorate(World worldIn, Random rand, BlockPos pos)
                {
                    this.baseBiome.decorate(worldIn, rand, pos);
                }
            }: new BiomeGenMutated(p_180277_1_, this)
            {
                public WorldGenAbstractTree genBigTreeChance(Random rand)
                {
                    return rand.nextBoolean() ? BiomeGenForest.field_150629_aC : BiomeGenForest.field_150630_aD;
                }
            };
        }
    }
}
