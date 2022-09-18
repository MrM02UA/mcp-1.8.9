package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkProviderSettings;
import net.minecraft.world.gen.GeneratorBushFeature;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenCactus;
import net.minecraft.world.gen.feature.WorldGenClay;
import net.minecraft.world.gen.feature.WorldGenDeadBush;
import net.minecraft.world.gen.feature.WorldGenFlowers;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenPumpkin;
import net.minecraft.world.gen.feature.WorldGenReed;
import net.minecraft.world.gen.feature.WorldGenSand;
import net.minecraft.world.gen.feature.WorldGenWaterlily;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeDecorator
{
    /** The world the BiomeDecorator is currently decorating */
    protected World currentWorld;

    /** The Biome Decorator's random number generator. */
    protected Random randomGenerator;
    protected BlockPos field_180294_c;
    protected ChunkProviderSettings chunkProviderSettings;

    /** The clay generator. */
    protected WorldGenerator clayGen = new WorldGenClay(4);

    /** The sand generator. */
    protected WorldGenerator sandGen = new WorldGenSand(Blocks.sand, 7);

    /** The gravel generator. */
    protected WorldGenerator gravelAsSandGen = new WorldGenSand(Blocks.gravel, 6);

    /** The dirt generator. */
    protected WorldGenerator dirtGen;
    protected WorldGenerator gravelGen;
    protected WorldGenerator graniteGen;
    protected WorldGenerator dioriteGen;
    protected WorldGenerator andesiteGen;
    protected WorldGenerator coalGen;
    protected WorldGenerator ironGen;

    /** Field that holds gold WorldGenMinable */
    protected WorldGenerator goldGen;
    protected WorldGenerator redstoneGen;
    protected WorldGenerator diamondGen;

    /** Field that holds Lapis WorldGenMinable */
    protected WorldGenerator lapisGen;
    protected WorldGenFlowers yellowFlowerGen = new WorldGenFlowers(Blocks.yellow_flower, BlockFlower.EnumFlowerType.DANDELION);

    /** Field that holds mushroomBrown WorldGenFlowers */
    protected WorldGenerator mushroomBrownGen = new GeneratorBushFeature(Blocks.brown_mushroom);

    /** Field that holds mushroomRed WorldGenFlowers */
    protected WorldGenerator mushroomRedGen = new GeneratorBushFeature(Blocks.red_mushroom);

    /** Field that holds big mushroom generator */
    protected WorldGenerator bigMushroomGen = new WorldGenBigMushroom();

    /** Field that holds WorldGenReed */
    protected WorldGenerator reedGen = new WorldGenReed();

    /** Field that holds WorldGenCactus */
    protected WorldGenerator cactusGen = new WorldGenCactus();

    /** The water lily generation! */
    protected WorldGenerator waterlilyGen = new WorldGenWaterlily();

    /** Amount of waterlilys per chunk. */
    protected int waterlilyPerChunk;

    /**
     * The number of trees to attempt to generate per chunk. Up to 10 in forests, none in deserts.
     */
    protected int treesPerChunk;

    /**
     * The number of yellow flower patches to generate per chunk. The game generates much less than this number, since
     * it attempts to generate them at a random altitude.
     */
    protected int flowersPerChunk = 2;

    /** The amount of tall grass to generate per chunk. */
    protected int grassPerChunk = 1;

    /**
     * The number of dead bushes to generate per chunk. Used in deserts and swamps.
     */
    protected int deadBushPerChunk;

    /**
     * The number of extra mushroom patches per chunk. It generates 1/4 this number in brown mushroom patches, and 1/8
     * this number in red mushroom patches. These mushrooms go beyond the default base number of mushrooms.
     */
    protected int mushroomsPerChunk;

    /**
     * The number of reeds to generate per chunk. Reeds won't generate if the randomly selected placement is unsuitable.
     */
    protected int reedsPerChunk;

    /**
     * The number of cactus plants to generate per chunk. Cacti only work on sand.
     */
    protected int cactiPerChunk;

    /**
     * The number of sand patches to generate per chunk. Sand patches only generate when part of it is underwater.
     */
    protected int sandPerChunk = 1;

    /**
     * The number of sand patches to generate per chunk. Sand patches only generate when part of it is underwater. There
     * appear to be two separate fields for this.
     */
    protected int sandPerChunk2 = 3;

    /**
     * The number of clay patches to generate per chunk. Only generates when part of it is underwater.
     */
    protected int clayPerChunk = 1;

    /** Amount of big mushrooms per chunk */
    protected int bigMushroomsPerChunk;

    /** True if decorator should generate surface lava & water */
    public boolean generateLakes = true;

    public void decorate(World worldIn, Random random, BiomeGenBase biome, BlockPos p_180292_4_)
    {
        if (this.currentWorld != null)
        {
            throw new RuntimeException("Already decorating");
        }
        else
        {
            this.currentWorld = worldIn;
            String lvt_5_1_ = worldIn.getWorldInfo().getGeneratorOptions();

            if (lvt_5_1_ != null)
            {
                this.chunkProviderSettings = ChunkProviderSettings.Factory.jsonToFactory(lvt_5_1_).func_177864_b();
            }
            else
            {
                this.chunkProviderSettings = ChunkProviderSettings.Factory.jsonToFactory("").func_177864_b();
            }

            this.randomGenerator = random;
            this.field_180294_c = p_180292_4_;
            this.dirtGen = new WorldGenMinable(Blocks.dirt.getDefaultState(), this.chunkProviderSettings.dirtSize);
            this.gravelGen = new WorldGenMinable(Blocks.gravel.getDefaultState(), this.chunkProviderSettings.gravelSize);
            this.graniteGen = new WorldGenMinable(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE), this.chunkProviderSettings.graniteSize);
            this.dioriteGen = new WorldGenMinable(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE), this.chunkProviderSettings.dioriteSize);
            this.andesiteGen = new WorldGenMinable(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE), this.chunkProviderSettings.andesiteSize);
            this.coalGen = new WorldGenMinable(Blocks.coal_ore.getDefaultState(), this.chunkProviderSettings.coalSize);
            this.ironGen = new WorldGenMinable(Blocks.iron_ore.getDefaultState(), this.chunkProviderSettings.ironSize);
            this.goldGen = new WorldGenMinable(Blocks.gold_ore.getDefaultState(), this.chunkProviderSettings.goldSize);
            this.redstoneGen = new WorldGenMinable(Blocks.redstone_ore.getDefaultState(), this.chunkProviderSettings.redstoneSize);
            this.diamondGen = new WorldGenMinable(Blocks.diamond_ore.getDefaultState(), this.chunkProviderSettings.diamondSize);
            this.lapisGen = new WorldGenMinable(Blocks.lapis_ore.getDefaultState(), this.chunkProviderSettings.lapisSize);
            this.genDecorations(biome);
            this.currentWorld = null;
            this.randomGenerator = null;
        }
    }

    protected void genDecorations(BiomeGenBase biomeGenBaseIn)
    {
        this.generateOres();

        for (int lvt_2_1_ = 0; lvt_2_1_ < this.sandPerChunk2; ++lvt_2_1_)
        {
            int lvt_3_1_ = this.randomGenerator.nextInt(16) + 8;
            int lvt_4_1_ = this.randomGenerator.nextInt(16) + 8;
            this.sandGen.generate(this.currentWorld, this.randomGenerator, this.currentWorld.getTopSolidOrLiquidBlock(this.field_180294_c.add(lvt_3_1_, 0, lvt_4_1_)));
        }

        for (int lvt_2_2_ = 0; lvt_2_2_ < this.clayPerChunk; ++lvt_2_2_)
        {
            int lvt_3_2_ = this.randomGenerator.nextInt(16) + 8;
            int lvt_4_2_ = this.randomGenerator.nextInt(16) + 8;
            this.clayGen.generate(this.currentWorld, this.randomGenerator, this.currentWorld.getTopSolidOrLiquidBlock(this.field_180294_c.add(lvt_3_2_, 0, lvt_4_2_)));
        }

        for (int lvt_2_3_ = 0; lvt_2_3_ < this.sandPerChunk; ++lvt_2_3_)
        {
            int lvt_3_3_ = this.randomGenerator.nextInt(16) + 8;
            int lvt_4_3_ = this.randomGenerator.nextInt(16) + 8;
            this.gravelAsSandGen.generate(this.currentWorld, this.randomGenerator, this.currentWorld.getTopSolidOrLiquidBlock(this.field_180294_c.add(lvt_3_3_, 0, lvt_4_3_)));
        }

        int lvt_2_4_ = this.treesPerChunk;

        if (this.randomGenerator.nextInt(10) == 0)
        {
            ++lvt_2_4_;
        }

        for (int lvt_3_4_ = 0; lvt_3_4_ < lvt_2_4_; ++lvt_3_4_)
        {
            int lvt_4_4_ = this.randomGenerator.nextInt(16) + 8;
            int lvt_5_1_ = this.randomGenerator.nextInt(16) + 8;
            WorldGenAbstractTree lvt_6_1_ = biomeGenBaseIn.genBigTreeChance(this.randomGenerator);
            lvt_6_1_.func_175904_e();
            BlockPos lvt_7_1_ = this.currentWorld.getHeight(this.field_180294_c.add(lvt_4_4_, 0, lvt_5_1_));

            if (lvt_6_1_.generate(this.currentWorld, this.randomGenerator, lvt_7_1_))
            {
                lvt_6_1_.func_180711_a(this.currentWorld, this.randomGenerator, lvt_7_1_);
            }
        }

        for (int lvt_3_5_ = 0; lvt_3_5_ < this.bigMushroomsPerChunk; ++lvt_3_5_)
        {
            int lvt_4_5_ = this.randomGenerator.nextInt(16) + 8;
            int lvt_5_2_ = this.randomGenerator.nextInt(16) + 8;
            this.bigMushroomGen.generate(this.currentWorld, this.randomGenerator, this.currentWorld.getHeight(this.field_180294_c.add(lvt_4_5_, 0, lvt_5_2_)));
        }

        for (int lvt_3_6_ = 0; lvt_3_6_ < this.flowersPerChunk; ++lvt_3_6_)
        {
            int lvt_4_6_ = this.randomGenerator.nextInt(16) + 8;
            int lvt_5_3_ = this.randomGenerator.nextInt(16) + 8;
            int lvt_6_2_ = this.currentWorld.getHeight(this.field_180294_c.add(lvt_4_6_, 0, lvt_5_3_)).getY() + 32;

            if (lvt_6_2_ > 0)
            {
                int lvt_7_2_ = this.randomGenerator.nextInt(lvt_6_2_);
                BlockPos lvt_8_1_ = this.field_180294_c.add(lvt_4_6_, lvt_7_2_, lvt_5_3_);
                BlockFlower.EnumFlowerType lvt_9_1_ = biomeGenBaseIn.pickRandomFlower(this.randomGenerator, lvt_8_1_);
                BlockFlower lvt_10_1_ = lvt_9_1_.getBlockType().getBlock();

                if (lvt_10_1_.getMaterial() != Material.air)
                {
                    this.yellowFlowerGen.setGeneratedBlock(lvt_10_1_, lvt_9_1_);
                    this.yellowFlowerGen.generate(this.currentWorld, this.randomGenerator, lvt_8_1_);
                }
            }
        }

        for (int lvt_3_7_ = 0; lvt_3_7_ < this.grassPerChunk; ++lvt_3_7_)
        {
            int lvt_4_7_ = this.randomGenerator.nextInt(16) + 8;
            int lvt_5_4_ = this.randomGenerator.nextInt(16) + 8;
            int lvt_6_3_ = this.currentWorld.getHeight(this.field_180294_c.add(lvt_4_7_, 0, lvt_5_4_)).getY() * 2;

            if (lvt_6_3_ > 0)
            {
                int lvt_7_3_ = this.randomGenerator.nextInt(lvt_6_3_);
                biomeGenBaseIn.getRandomWorldGenForGrass(this.randomGenerator).generate(this.currentWorld, this.randomGenerator, this.field_180294_c.add(lvt_4_7_, lvt_7_3_, lvt_5_4_));
            }
        }

        for (int lvt_3_8_ = 0; lvt_3_8_ < this.deadBushPerChunk; ++lvt_3_8_)
        {
            int lvt_4_8_ = this.randomGenerator.nextInt(16) + 8;
            int lvt_5_5_ = this.randomGenerator.nextInt(16) + 8;
            int lvt_6_4_ = this.currentWorld.getHeight(this.field_180294_c.add(lvt_4_8_, 0, lvt_5_5_)).getY() * 2;

            if (lvt_6_4_ > 0)
            {
                int lvt_7_4_ = this.randomGenerator.nextInt(lvt_6_4_);
                (new WorldGenDeadBush()).generate(this.currentWorld, this.randomGenerator, this.field_180294_c.add(lvt_4_8_, lvt_7_4_, lvt_5_5_));
            }
        }

        for (int lvt_3_9_ = 0; lvt_3_9_ < this.waterlilyPerChunk; ++lvt_3_9_)
        {
            int lvt_4_9_ = this.randomGenerator.nextInt(16) + 8;
            int lvt_5_6_ = this.randomGenerator.nextInt(16) + 8;
            int lvt_6_5_ = this.currentWorld.getHeight(this.field_180294_c.add(lvt_4_9_, 0, lvt_5_6_)).getY() * 2;

            if (lvt_6_5_ > 0)
            {
                int lvt_7_5_ = this.randomGenerator.nextInt(lvt_6_5_);
                BlockPos lvt_8_2_;
                BlockPos lvt_9_2_;

                for (lvt_8_2_ = this.field_180294_c.add(lvt_4_9_, lvt_7_5_, lvt_5_6_); lvt_8_2_.getY() > 0; lvt_8_2_ = lvt_9_2_)
                {
                    lvt_9_2_ = lvt_8_2_.down();

                    if (!this.currentWorld.isAirBlock(lvt_9_2_))
                    {
                        break;
                    }
                }

                this.waterlilyGen.generate(this.currentWorld, this.randomGenerator, lvt_8_2_);
            }
        }

        for (int lvt_3_10_ = 0; lvt_3_10_ < this.mushroomsPerChunk; ++lvt_3_10_)
        {
            if (this.randomGenerator.nextInt(4) == 0)
            {
                int lvt_4_10_ = this.randomGenerator.nextInt(16) + 8;
                int lvt_5_7_ = this.randomGenerator.nextInt(16) + 8;
                BlockPos lvt_6_6_ = this.currentWorld.getHeight(this.field_180294_c.add(lvt_4_10_, 0, lvt_5_7_));
                this.mushroomBrownGen.generate(this.currentWorld, this.randomGenerator, lvt_6_6_);
            }

            if (this.randomGenerator.nextInt(8) == 0)
            {
                int lvt_4_11_ = this.randomGenerator.nextInt(16) + 8;
                int lvt_5_8_ = this.randomGenerator.nextInt(16) + 8;
                int lvt_6_7_ = this.currentWorld.getHeight(this.field_180294_c.add(lvt_4_11_, 0, lvt_5_8_)).getY() * 2;

                if (lvt_6_7_ > 0)
                {
                    int lvt_7_6_ = this.randomGenerator.nextInt(lvt_6_7_);
                    BlockPos lvt_8_3_ = this.field_180294_c.add(lvt_4_11_, lvt_7_6_, lvt_5_8_);
                    this.mushroomRedGen.generate(this.currentWorld, this.randomGenerator, lvt_8_3_);
                }
            }
        }

        if (this.randomGenerator.nextInt(4) == 0)
        {
            int lvt_3_11_ = this.randomGenerator.nextInt(16) + 8;
            int lvt_4_12_ = this.randomGenerator.nextInt(16) + 8;
            int lvt_5_9_ = this.currentWorld.getHeight(this.field_180294_c.add(lvt_3_11_, 0, lvt_4_12_)).getY() * 2;

            if (lvt_5_9_ > 0)
            {
                int lvt_6_8_ = this.randomGenerator.nextInt(lvt_5_9_);
                this.mushroomBrownGen.generate(this.currentWorld, this.randomGenerator, this.field_180294_c.add(lvt_3_11_, lvt_6_8_, lvt_4_12_));
            }
        }

        if (this.randomGenerator.nextInt(8) == 0)
        {
            int lvt_3_12_ = this.randomGenerator.nextInt(16) + 8;
            int lvt_4_13_ = this.randomGenerator.nextInt(16) + 8;
            int lvt_5_10_ = this.currentWorld.getHeight(this.field_180294_c.add(lvt_3_12_, 0, lvt_4_13_)).getY() * 2;

            if (lvt_5_10_ > 0)
            {
                int lvt_6_9_ = this.randomGenerator.nextInt(lvt_5_10_);
                this.mushroomRedGen.generate(this.currentWorld, this.randomGenerator, this.field_180294_c.add(lvt_3_12_, lvt_6_9_, lvt_4_13_));
            }
        }

        for (int lvt_3_13_ = 0; lvt_3_13_ < this.reedsPerChunk; ++lvt_3_13_)
        {
            int lvt_4_14_ = this.randomGenerator.nextInt(16) + 8;
            int lvt_5_11_ = this.randomGenerator.nextInt(16) + 8;
            int lvt_6_10_ = this.currentWorld.getHeight(this.field_180294_c.add(lvt_4_14_, 0, lvt_5_11_)).getY() * 2;

            if (lvt_6_10_ > 0)
            {
                int lvt_7_7_ = this.randomGenerator.nextInt(lvt_6_10_);
                this.reedGen.generate(this.currentWorld, this.randomGenerator, this.field_180294_c.add(lvt_4_14_, lvt_7_7_, lvt_5_11_));
            }
        }

        for (int lvt_3_14_ = 0; lvt_3_14_ < 10; ++lvt_3_14_)
        {
            int lvt_4_15_ = this.randomGenerator.nextInt(16) + 8;
            int lvt_5_12_ = this.randomGenerator.nextInt(16) + 8;
            int lvt_6_11_ = this.currentWorld.getHeight(this.field_180294_c.add(lvt_4_15_, 0, lvt_5_12_)).getY() * 2;

            if (lvt_6_11_ > 0)
            {
                int lvt_7_8_ = this.randomGenerator.nextInt(lvt_6_11_);
                this.reedGen.generate(this.currentWorld, this.randomGenerator, this.field_180294_c.add(lvt_4_15_, lvt_7_8_, lvt_5_12_));
            }
        }

        if (this.randomGenerator.nextInt(32) == 0)
        {
            int lvt_3_15_ = this.randomGenerator.nextInt(16) + 8;
            int lvt_4_16_ = this.randomGenerator.nextInt(16) + 8;
            int lvt_5_13_ = this.currentWorld.getHeight(this.field_180294_c.add(lvt_3_15_, 0, lvt_4_16_)).getY() * 2;

            if (lvt_5_13_ > 0)
            {
                int lvt_6_12_ = this.randomGenerator.nextInt(lvt_5_13_);
                (new WorldGenPumpkin()).generate(this.currentWorld, this.randomGenerator, this.field_180294_c.add(lvt_3_15_, lvt_6_12_, lvt_4_16_));
            }
        }

        for (int lvt_3_16_ = 0; lvt_3_16_ < this.cactiPerChunk; ++lvt_3_16_)
        {
            int lvt_4_17_ = this.randomGenerator.nextInt(16) + 8;
            int lvt_5_14_ = this.randomGenerator.nextInt(16) + 8;
            int lvt_6_13_ = this.currentWorld.getHeight(this.field_180294_c.add(lvt_4_17_, 0, lvt_5_14_)).getY() * 2;

            if (lvt_6_13_ > 0)
            {
                int lvt_7_9_ = this.randomGenerator.nextInt(lvt_6_13_);
                this.cactusGen.generate(this.currentWorld, this.randomGenerator, this.field_180294_c.add(lvt_4_17_, lvt_7_9_, lvt_5_14_));
            }
        }

        if (this.generateLakes)
        {
            for (int lvt_3_17_ = 0; lvt_3_17_ < 50; ++lvt_3_17_)
            {
                int lvt_4_18_ = this.randomGenerator.nextInt(16) + 8;
                int lvt_5_15_ = this.randomGenerator.nextInt(16) + 8;
                int lvt_6_14_ = this.randomGenerator.nextInt(248) + 8;

                if (lvt_6_14_ > 0)
                {
                    int lvt_7_10_ = this.randomGenerator.nextInt(lvt_6_14_);
                    BlockPos lvt_8_4_ = this.field_180294_c.add(lvt_4_18_, lvt_7_10_, lvt_5_15_);
                    (new WorldGenLiquids(Blocks.flowing_water)).generate(this.currentWorld, this.randomGenerator, lvt_8_4_);
                }
            }

            for (int lvt_3_18_ = 0; lvt_3_18_ < 20; ++lvt_3_18_)
            {
                int lvt_4_19_ = this.randomGenerator.nextInt(16) + 8;
                int lvt_5_16_ = this.randomGenerator.nextInt(16) + 8;
                int lvt_6_15_ = this.randomGenerator.nextInt(this.randomGenerator.nextInt(this.randomGenerator.nextInt(240) + 8) + 8);
                BlockPos lvt_7_11_ = this.field_180294_c.add(lvt_4_19_, lvt_6_15_, lvt_5_16_);
                (new WorldGenLiquids(Blocks.flowing_lava)).generate(this.currentWorld, this.randomGenerator, lvt_7_11_);
            }
        }
    }

    /**
     * Standard ore generation helper. Generates most ores.
     */
    protected void genStandardOre1(int blockCount, WorldGenerator generator, int minHeight, int maxHeight)
    {
        if (maxHeight < minHeight)
        {
            int lvt_5_1_ = minHeight;
            minHeight = maxHeight;
            maxHeight = lvt_5_1_;
        }
        else if (maxHeight == minHeight)
        {
            if (minHeight < 255)
            {
                ++maxHeight;
            }
            else
            {
                --minHeight;
            }
        }

        for (int lvt_5_2_ = 0; lvt_5_2_ < blockCount; ++lvt_5_2_)
        {
            BlockPos lvt_6_1_ = this.field_180294_c.add(this.randomGenerator.nextInt(16), this.randomGenerator.nextInt(maxHeight - minHeight) + minHeight, this.randomGenerator.nextInt(16));
            generator.generate(this.currentWorld, this.randomGenerator, lvt_6_1_);
        }
    }

    /**
     * Standard ore generation helper. Generates Lapis Lazuli.
     */
    protected void genStandardOre2(int blockCount, WorldGenerator generator, int centerHeight, int spread)
    {
        for (int lvt_5_1_ = 0; lvt_5_1_ < blockCount; ++lvt_5_1_)
        {
            BlockPos lvt_6_1_ = this.field_180294_c.add(this.randomGenerator.nextInt(16), this.randomGenerator.nextInt(spread) + this.randomGenerator.nextInt(spread) + centerHeight - spread, this.randomGenerator.nextInt(16));
            generator.generate(this.currentWorld, this.randomGenerator, lvt_6_1_);
        }
    }

    /**
     * Generates ores in the current chunk
     */
    protected void generateOres()
    {
        this.genStandardOre1(this.chunkProviderSettings.dirtCount, this.dirtGen, this.chunkProviderSettings.dirtMinHeight, this.chunkProviderSettings.dirtMaxHeight);
        this.genStandardOre1(this.chunkProviderSettings.gravelCount, this.gravelGen, this.chunkProviderSettings.gravelMinHeight, this.chunkProviderSettings.gravelMaxHeight);
        this.genStandardOre1(this.chunkProviderSettings.dioriteCount, this.dioriteGen, this.chunkProviderSettings.dioriteMinHeight, this.chunkProviderSettings.dioriteMaxHeight);
        this.genStandardOre1(this.chunkProviderSettings.graniteCount, this.graniteGen, this.chunkProviderSettings.graniteMinHeight, this.chunkProviderSettings.graniteMaxHeight);
        this.genStandardOre1(this.chunkProviderSettings.andesiteCount, this.andesiteGen, this.chunkProviderSettings.andesiteMinHeight, this.chunkProviderSettings.andesiteMaxHeight);
        this.genStandardOre1(this.chunkProviderSettings.coalCount, this.coalGen, this.chunkProviderSettings.coalMinHeight, this.chunkProviderSettings.coalMaxHeight);
        this.genStandardOre1(this.chunkProviderSettings.ironCount, this.ironGen, this.chunkProviderSettings.ironMinHeight, this.chunkProviderSettings.ironMaxHeight);
        this.genStandardOre1(this.chunkProviderSettings.goldCount, this.goldGen, this.chunkProviderSettings.goldMinHeight, this.chunkProviderSettings.goldMaxHeight);
        this.genStandardOre1(this.chunkProviderSettings.redstoneCount, this.redstoneGen, this.chunkProviderSettings.redstoneMinHeight, this.chunkProviderSettings.redstoneMaxHeight);
        this.genStandardOre1(this.chunkProviderSettings.diamondCount, this.diamondGen, this.chunkProviderSettings.diamondMinHeight, this.chunkProviderSettings.diamondMaxHeight);
        this.genStandardOre2(this.chunkProviderSettings.lapisCount, this.lapisGen, this.chunkProviderSettings.lapisCenterHeight, this.chunkProviderSettings.lapisSpread);
    }
}
