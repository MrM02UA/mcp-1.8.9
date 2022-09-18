package net.minecraft.world.gen.layer;

import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.ChunkProviderSettings;

public abstract class GenLayer
{
    /** seed from World#getWorldSeed that is used in the LCG prng */
    private long worldGenSeed;

    /** parent GenLayer that was provided via the constructor */
    protected GenLayer parent;

    /**
     * final part of the LCG prng that uses the chunk X, Z coords along with the other two seeds to generate
     * pseudorandom numbers
     */
    private long chunkSeed;

    /** base seed to the LCG prng provided via the constructor */
    protected long baseSeed;

    public static GenLayer[] initializeAllBiomeGenerators(long seed, WorldType p_180781_2_, String p_180781_3_)
    {
        GenLayer lvt_4_1_ = new GenLayerIsland(1L);
        lvt_4_1_ = new GenLayerFuzzyZoom(2000L, lvt_4_1_);
        GenLayerAddIsland var14 = new GenLayerAddIsland(1L, lvt_4_1_);
        GenLayerZoom var15 = new GenLayerZoom(2001L, var14);
        GenLayerAddIsland var16 = new GenLayerAddIsland(2L, var15);
        var16 = new GenLayerAddIsland(50L, var16);
        var16 = new GenLayerAddIsland(70L, var16);
        GenLayerRemoveTooMuchOcean var19 = new GenLayerRemoveTooMuchOcean(2L, var16);
        GenLayerAddSnow var20 = new GenLayerAddSnow(2L, var19);
        GenLayerAddIsland var21 = new GenLayerAddIsland(3L, var20);
        GenLayerEdge var22 = new GenLayerEdge(2L, var21, GenLayerEdge.Mode.COOL_WARM);
        var22 = new GenLayerEdge(2L, var22, GenLayerEdge.Mode.HEAT_ICE);
        var22 = new GenLayerEdge(3L, var22, GenLayerEdge.Mode.SPECIAL);
        GenLayerZoom var25 = new GenLayerZoom(2002L, var22);
        var25 = new GenLayerZoom(2003L, var25);
        GenLayerAddIsland var27 = new GenLayerAddIsland(4L, var25);
        GenLayerAddMushroomIsland var28 = new GenLayerAddMushroomIsland(5L, var27);
        GenLayerDeepOcean var29 = new GenLayerDeepOcean(4L, var28);
        GenLayer var30 = GenLayerZoom.magnify(1000L, var29, 0);
        ChunkProviderSettings lvt_5_1_ = null;
        int lvt_6_1_ = 4;
        int lvt_7_1_ = lvt_6_1_;

        if (p_180781_2_ == WorldType.CUSTOMIZED && p_180781_3_.length() > 0)
        {
            lvt_5_1_ = ChunkProviderSettings.Factory.jsonToFactory(p_180781_3_).func_177864_b();
            lvt_6_1_ = lvt_5_1_.biomeSize;
            lvt_7_1_ = lvt_5_1_.riverSize;
        }

        if (p_180781_2_ == WorldType.LARGE_BIOMES)
        {
            lvt_6_1_ = 6;
        }

        GenLayer lvt_8_1_ = GenLayerZoom.magnify(1000L, var30, 0);
        GenLayerRiverInit var32 = new GenLayerRiverInit(100L, lvt_8_1_);
        GenLayerBiome lvt_9_1_ = new GenLayerBiome(200L, var30, p_180781_2_, p_180781_3_);
        GenLayer var37 = GenLayerZoom.magnify(1000L, lvt_9_1_, 2);
        GenLayerBiomeEdge var38 = new GenLayerBiomeEdge(1000L, var37);
        GenLayer lvt_10_1_ = GenLayerZoom.magnify(1000L, var32, 2);
        GenLayerHills var39 = new GenLayerHills(1000L, var38, lvt_10_1_);
        GenLayer var33 = GenLayerZoom.magnify(1000L, var32, 2);
        var33 = GenLayerZoom.magnify(1000L, var33, lvt_7_1_);
        GenLayerRiver var35 = new GenLayerRiver(1L, var33);
        GenLayerSmooth var36 = new GenLayerSmooth(1000L, var35);
        var39 = new GenLayerRareBiome(1001L, var39);

        for (int lvt_11_1_ = 0; lvt_11_1_ < lvt_6_1_; ++lvt_11_1_)
        {
            var39 = new GenLayerZoom((long)(1000 + lvt_11_1_), var39);

            if (lvt_11_1_ == 0)
            {
                var39 = new GenLayerAddIsland(3L, var39);
            }

            if (lvt_11_1_ == 1 || lvt_6_1_ == 1)
            {
                var39 = new GenLayerShore(1000L, var39);
            }
        }

        GenLayerSmooth var41 = new GenLayerSmooth(1000L, var39);
        GenLayerRiverMix var42 = new GenLayerRiverMix(100L, var41, var36);
        GenLayer lvt_12_1_ = new GenLayerVoronoiZoom(10L, var42);
        var42.initWorldGenSeed(seed);
        lvt_12_1_.initWorldGenSeed(seed);
        return new GenLayer[] {var42, lvt_12_1_, var42};
    }

    public GenLayer(long p_i2125_1_)
    {
        this.baseSeed = p_i2125_1_;
        this.baseSeed *= this.baseSeed * 6364136223846793005L + 1442695040888963407L;
        this.baseSeed += p_i2125_1_;
        this.baseSeed *= this.baseSeed * 6364136223846793005L + 1442695040888963407L;
        this.baseSeed += p_i2125_1_;
        this.baseSeed *= this.baseSeed * 6364136223846793005L + 1442695040888963407L;
        this.baseSeed += p_i2125_1_;
    }

    /**
     * Initialize layer's local worldGenSeed based on its own baseSeed and the world's global seed (passed in as an
     * argument).
     */
    public void initWorldGenSeed(long seed)
    {
        this.worldGenSeed = seed;

        if (this.parent != null)
        {
            this.parent.initWorldGenSeed(seed);
        }

        this.worldGenSeed *= this.worldGenSeed * 6364136223846793005L + 1442695040888963407L;
        this.worldGenSeed += this.baseSeed;
        this.worldGenSeed *= this.worldGenSeed * 6364136223846793005L + 1442695040888963407L;
        this.worldGenSeed += this.baseSeed;
        this.worldGenSeed *= this.worldGenSeed * 6364136223846793005L + 1442695040888963407L;
        this.worldGenSeed += this.baseSeed;
    }

    /**
     * Initialize layer's current chunkSeed based on the local worldGenSeed and the (x,z) chunk coordinates.
     */
    public void initChunkSeed(long p_75903_1_, long p_75903_3_)
    {
        this.chunkSeed = this.worldGenSeed;
        this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
        this.chunkSeed += p_75903_1_;
        this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
        this.chunkSeed += p_75903_3_;
        this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
        this.chunkSeed += p_75903_1_;
        this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
        this.chunkSeed += p_75903_3_;
    }

    /**
     * returns a LCG pseudo random number from [0, x). Args: int x
     */
    protected int nextInt(int p_75902_1_)
    {
        int lvt_2_1_ = (int)((this.chunkSeed >> 24) % (long)p_75902_1_);

        if (lvt_2_1_ < 0)
        {
            lvt_2_1_ += p_75902_1_;
        }

        this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
        this.chunkSeed += this.worldGenSeed;
        return lvt_2_1_;
    }

    /**
     * Returns a list of integer values generated by this layer. These may be interpreted as temperatures, rainfall
     * amounts, or biomeList[] indices based on the particular GenLayer subclass.
     */
    public abstract int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight);

    protected static boolean biomesEqualOrMesaPlateau(int biomeIDA, int biomeIDB)
    {
        if (biomeIDA == biomeIDB)
        {
            return true;
        }
        else if (biomeIDA != BiomeGenBase.mesaPlateau_F.biomeID && biomeIDA != BiomeGenBase.mesaPlateau.biomeID)
        {
            final BiomeGenBase lvt_2_1_ = BiomeGenBase.getBiome(biomeIDA);
            final BiomeGenBase lvt_3_1_ = BiomeGenBase.getBiome(biomeIDB);

            try
            {
                return lvt_2_1_ != null && lvt_3_1_ != null ? lvt_2_1_.isEqualTo(lvt_3_1_) : false;
            }
            catch (Throwable var7)
            {
                CrashReport lvt_5_1_ = CrashReport.makeCrashReport(var7, "Comparing biomes");
                CrashReportCategory lvt_6_1_ = lvt_5_1_.makeCategory("Biomes being compared");
                lvt_6_1_.addCrashSection("Biome A ID", Integer.valueOf(biomeIDA));
                lvt_6_1_.addCrashSection("Biome B ID", Integer.valueOf(biomeIDB));
                lvt_6_1_.addCrashSectionCallable("Biome A", new Callable<String>()
                {
                    public String call() throws Exception
                    {
                        return String.valueOf(lvt_2_1_);
                    }
                    public Object call() throws Exception
                    {
                        return this.call();
                    }
                });
                lvt_6_1_.addCrashSectionCallable("Biome B", new Callable<String>()
                {
                    public String call() throws Exception
                    {
                        return String.valueOf(lvt_3_1_);
                    }
                    public Object call() throws Exception
                    {
                        return this.call();
                    }
                });
                throw new ReportedException(lvt_5_1_);
            }
        }
        else
        {
            return biomeIDB == BiomeGenBase.mesaPlateau_F.biomeID || biomeIDB == BiomeGenBase.mesaPlateau.biomeID;
        }
    }

    /**
     * returns true if the biomeId is one of the various ocean biomes.
     */
    protected static boolean isBiomeOceanic(int p_151618_0_)
    {
        return p_151618_0_ == BiomeGenBase.ocean.biomeID || p_151618_0_ == BiomeGenBase.deepOcean.biomeID || p_151618_0_ == BiomeGenBase.frozenOcean.biomeID;
    }

    /**
     * selects a random integer from a set of provided integers
     */
    protected int selectRandom(int... p_151619_1_)
    {
        return p_151619_1_[this.nextInt(p_151619_1_.length)];
    }

    /**
     * returns the most frequently occurring number of the set, or a random number from those provided
     */
    protected int selectModeOrRandom(int p_151617_1_, int p_151617_2_, int p_151617_3_, int p_151617_4_)
    {
        return p_151617_2_ == p_151617_3_ && p_151617_3_ == p_151617_4_ ? p_151617_2_ : (p_151617_1_ == p_151617_2_ && p_151617_1_ == p_151617_3_ ? p_151617_1_ : (p_151617_1_ == p_151617_2_ && p_151617_1_ == p_151617_4_ ? p_151617_1_ : (p_151617_1_ == p_151617_3_ && p_151617_1_ == p_151617_4_ ? p_151617_1_ : (p_151617_1_ == p_151617_2_ && p_151617_3_ != p_151617_4_ ? p_151617_1_ : (p_151617_1_ == p_151617_3_ && p_151617_2_ != p_151617_4_ ? p_151617_1_ : (p_151617_1_ == p_151617_4_ && p_151617_2_ != p_151617_3_ ? p_151617_1_ : (p_151617_2_ == p_151617_3_ && p_151617_1_ != p_151617_4_ ? p_151617_2_ : (p_151617_2_ == p_151617_4_ && p_151617_1_ != p_151617_3_ ? p_151617_2_ : (p_151617_3_ == p_151617_4_ && p_151617_1_ != p_151617_2_ ? p_151617_3_ : this.selectRandom(new int[] {p_151617_1_, p_151617_2_, p_151617_3_, p_151617_4_}))))))))));
    }
}
