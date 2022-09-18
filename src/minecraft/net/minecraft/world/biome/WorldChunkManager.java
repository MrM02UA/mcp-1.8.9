package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class WorldChunkManager
{
    private GenLayer genBiomes;

    /** A GenLayer containing the indices into BiomeGenBase.biomeList[] */
    private GenLayer biomeIndexLayer;

    /** The biome list. */
    private BiomeCache biomeCache;
    private List<BiomeGenBase> biomesToSpawnIn;
    private String generatorOptions;

    protected WorldChunkManager()
    {
        this.biomeCache = new BiomeCache(this);
        this.generatorOptions = "";
        this.biomesToSpawnIn = Lists.newArrayList();
        this.biomesToSpawnIn.add(BiomeGenBase.forest);
        this.biomesToSpawnIn.add(BiomeGenBase.plains);
        this.biomesToSpawnIn.add(BiomeGenBase.taiga);
        this.biomesToSpawnIn.add(BiomeGenBase.taigaHills);
        this.biomesToSpawnIn.add(BiomeGenBase.forestHills);
        this.biomesToSpawnIn.add(BiomeGenBase.jungle);
        this.biomesToSpawnIn.add(BiomeGenBase.jungleHills);
    }

    public WorldChunkManager(long seed, WorldType worldTypeIn, String options)
    {
        this();
        this.generatorOptions = options;
        GenLayer[] lvt_5_1_ = GenLayer.initializeAllBiomeGenerators(seed, worldTypeIn, options);
        this.genBiomes = lvt_5_1_[0];
        this.biomeIndexLayer = lvt_5_1_[1];
    }

    public WorldChunkManager(World worldIn)
    {
        this(worldIn.getSeed(), worldIn.getWorldInfo().getTerrainType(), worldIn.getWorldInfo().getGeneratorOptions());
    }

    public List<BiomeGenBase> getBiomesToSpawnIn()
    {
        return this.biomesToSpawnIn;
    }

    /**
     * Returns the biome generator
     */
    public BiomeGenBase getBiomeGenerator(BlockPos pos)
    {
        return this.getBiomeGenerator(pos, (BiomeGenBase)null);
    }

    public BiomeGenBase getBiomeGenerator(BlockPos pos, BiomeGenBase biomeGenBaseIn)
    {
        return this.biomeCache.func_180284_a(pos.getX(), pos.getZ(), biomeGenBaseIn);
    }

    /**
     * Returns a list of rainfall values for the specified blocks. Args: listToReuse, x, z, width, length.
     */
    public float[] getRainfall(float[] listToReuse, int x, int z, int width, int length)
    {
        IntCache.resetIntCache();

        if (listToReuse == null || listToReuse.length < width * length)
        {
            listToReuse = new float[width * length];
        }

        int[] lvt_6_1_ = this.biomeIndexLayer.getInts(x, z, width, length);

        for (int lvt_7_1_ = 0; lvt_7_1_ < width * length; ++lvt_7_1_)
        {
            try
            {
                float lvt_8_1_ = (float)BiomeGenBase.getBiomeFromBiomeList(lvt_6_1_[lvt_7_1_], BiomeGenBase.field_180279_ad).getIntRainfall() / 65536.0F;

                if (lvt_8_1_ > 1.0F)
                {
                    lvt_8_1_ = 1.0F;
                }

                listToReuse[lvt_7_1_] = lvt_8_1_;
            }
            catch (Throwable var11)
            {
                CrashReport lvt_9_1_ = CrashReport.makeCrashReport(var11, "Invalid Biome id");
                CrashReportCategory lvt_10_1_ = lvt_9_1_.makeCategory("DownfallBlock");
                lvt_10_1_.addCrashSection("biome id", Integer.valueOf(lvt_7_1_));
                lvt_10_1_.addCrashSection("downfalls[] size", Integer.valueOf(listToReuse.length));
                lvt_10_1_.addCrashSection("x", Integer.valueOf(x));
                lvt_10_1_.addCrashSection("z", Integer.valueOf(z));
                lvt_10_1_.addCrashSection("w", Integer.valueOf(width));
                lvt_10_1_.addCrashSection("h", Integer.valueOf(length));
                throw new ReportedException(lvt_9_1_);
            }
        }

        return listToReuse;
    }

    /**
     * Return an adjusted version of a given temperature based on the y height
     */
    public float getTemperatureAtHeight(float p_76939_1_, int p_76939_2_)
    {
        return p_76939_1_;
    }

    /**
     * Returns an array of biomes for the location input.
     */
    public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] biomes, int x, int z, int width, int height)
    {
        IntCache.resetIntCache();

        if (biomes == null || biomes.length < width * height)
        {
            biomes = new BiomeGenBase[width * height];
        }

        int[] lvt_6_1_ = this.genBiomes.getInts(x, z, width, height);

        try
        {
            for (int lvt_7_1_ = 0; lvt_7_1_ < width * height; ++lvt_7_1_)
            {
                biomes[lvt_7_1_] = BiomeGenBase.getBiomeFromBiomeList(lvt_6_1_[lvt_7_1_], BiomeGenBase.field_180279_ad);
            }

            return biomes;
        }
        catch (Throwable var10)
        {
            CrashReport lvt_8_1_ = CrashReport.makeCrashReport(var10, "Invalid Biome id");
            CrashReportCategory lvt_9_1_ = lvt_8_1_.makeCategory("RawBiomeBlock");
            lvt_9_1_.addCrashSection("biomes[] size", Integer.valueOf(biomes.length));
            lvt_9_1_.addCrashSection("x", Integer.valueOf(x));
            lvt_9_1_.addCrashSection("z", Integer.valueOf(z));
            lvt_9_1_.addCrashSection("w", Integer.valueOf(width));
            lvt_9_1_.addCrashSection("h", Integer.valueOf(height));
            throw new ReportedException(lvt_8_1_);
        }
    }

    /**
     * Returns biomes to use for the blocks and loads the other data like temperature and humidity onto the
     * WorldChunkManager Args: oldBiomeList, x, z, width, depth
     */
    public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] oldBiomeList, int x, int z, int width, int depth)
    {
        return this.getBiomeGenAt(oldBiomeList, x, z, width, depth, true);
    }

    /**
     * Return a list of biomes for the specified blocks. Args: listToReuse, x, y, width, length, cacheFlag (if false,
     * don't check biomeCache to avoid infinite loop in BiomeCacheBlock)
     */
    public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] listToReuse, int x, int z, int width, int length, boolean cacheFlag)
    {
        IntCache.resetIntCache();

        if (listToReuse == null || listToReuse.length < width * length)
        {
            listToReuse = new BiomeGenBase[width * length];
        }

        if (cacheFlag && width == 16 && length == 16 && (x & 15) == 0 && (z & 15) == 0)
        {
            BiomeGenBase[] lvt_7_1_ = this.biomeCache.getCachedBiomes(x, z);
            System.arraycopy(lvt_7_1_, 0, listToReuse, 0, width * length);
            return listToReuse;
        }
        else
        {
            int[] lvt_7_2_ = this.biomeIndexLayer.getInts(x, z, width, length);

            for (int lvt_8_1_ = 0; lvt_8_1_ < width * length; ++lvt_8_1_)
            {
                listToReuse[lvt_8_1_] = BiomeGenBase.getBiomeFromBiomeList(lvt_7_2_[lvt_8_1_], BiomeGenBase.field_180279_ad);
            }

            return listToReuse;
        }
    }

    /**
     * checks given Chunk's Biomes against List of allowed ones
     */
    public boolean areBiomesViable(int p_76940_1_, int p_76940_2_, int p_76940_3_, List<BiomeGenBase> p_76940_4_)
    {
        IntCache.resetIntCache();
        int lvt_5_1_ = p_76940_1_ - p_76940_3_ >> 2;
        int lvt_6_1_ = p_76940_2_ - p_76940_3_ >> 2;
        int lvt_7_1_ = p_76940_1_ + p_76940_3_ >> 2;
        int lvt_8_1_ = p_76940_2_ + p_76940_3_ >> 2;
        int lvt_9_1_ = lvt_7_1_ - lvt_5_1_ + 1;
        int lvt_10_1_ = lvt_8_1_ - lvt_6_1_ + 1;
        int[] lvt_11_1_ = this.genBiomes.getInts(lvt_5_1_, lvt_6_1_, lvt_9_1_, lvt_10_1_);

        try
        {
            for (int lvt_12_1_ = 0; lvt_12_1_ < lvt_9_1_ * lvt_10_1_; ++lvt_12_1_)
            {
                BiomeGenBase lvt_13_1_ = BiomeGenBase.getBiome(lvt_11_1_[lvt_12_1_]);

                if (!p_76940_4_.contains(lvt_13_1_))
                {
                    return false;
                }
            }

            return true;
        }
        catch (Throwable var15)
        {
            CrashReport lvt_13_2_ = CrashReport.makeCrashReport(var15, "Invalid Biome id");
            CrashReportCategory lvt_14_1_ = lvt_13_2_.makeCategory("Layer");
            lvt_14_1_.addCrashSection("Layer", this.genBiomes.toString());
            lvt_14_1_.addCrashSection("x", Integer.valueOf(p_76940_1_));
            lvt_14_1_.addCrashSection("z", Integer.valueOf(p_76940_2_));
            lvt_14_1_.addCrashSection("radius", Integer.valueOf(p_76940_3_));
            lvt_14_1_.addCrashSection("allowed", p_76940_4_);
            throw new ReportedException(lvt_13_2_);
        }
    }

    public BlockPos findBiomePosition(int x, int z, int range, List<BiomeGenBase> biomes, Random random)
    {
        IntCache.resetIntCache();
        int lvt_6_1_ = x - range >> 2;
        int lvt_7_1_ = z - range >> 2;
        int lvt_8_1_ = x + range >> 2;
        int lvt_9_1_ = z + range >> 2;
        int lvt_10_1_ = lvt_8_1_ - lvt_6_1_ + 1;
        int lvt_11_1_ = lvt_9_1_ - lvt_7_1_ + 1;
        int[] lvt_12_1_ = this.genBiomes.getInts(lvt_6_1_, lvt_7_1_, lvt_10_1_, lvt_11_1_);
        BlockPos lvt_13_1_ = null;
        int lvt_14_1_ = 0;

        for (int lvt_15_1_ = 0; lvt_15_1_ < lvt_10_1_ * lvt_11_1_; ++lvt_15_1_)
        {
            int lvt_16_1_ = lvt_6_1_ + lvt_15_1_ % lvt_10_1_ << 2;
            int lvt_17_1_ = lvt_7_1_ + lvt_15_1_ / lvt_10_1_ << 2;
            BiomeGenBase lvt_18_1_ = BiomeGenBase.getBiome(lvt_12_1_[lvt_15_1_]);

            if (biomes.contains(lvt_18_1_) && (lvt_13_1_ == null || random.nextInt(lvt_14_1_ + 1) == 0))
            {
                lvt_13_1_ = new BlockPos(lvt_16_1_, 0, lvt_17_1_);
                ++lvt_14_1_;
            }
        }

        return lvt_13_1_;
    }

    /**
     * Calls the WorldChunkManager's biomeCache.cleanupCache()
     */
    public void cleanupCache()
    {
        this.biomeCache.cleanupCache();
    }
}
