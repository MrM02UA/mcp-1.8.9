package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.LongHashMap;

public class BiomeCache
{
    /** Reference to the WorldChunkManager */
    private final WorldChunkManager chunkManager;

    /** The last time this BiomeCache was cleaned, in milliseconds. */
    private long lastCleanupTime;
    private LongHashMap<BiomeCache.Block> cacheMap = new LongHashMap();
    private List<BiomeCache.Block> cache = Lists.newArrayList();

    public BiomeCache(WorldChunkManager chunkManagerIn)
    {
        this.chunkManager = chunkManagerIn;
    }

    /**
     * Returns a biome cache block at location specified.
     */
    public BiomeCache.Block getBiomeCacheBlock(int x, int z)
    {
        x = x >> 4;
        z = z >> 4;
        long lvt_3_1_ = (long)x & 4294967295L | ((long)z & 4294967295L) << 32;
        BiomeCache.Block lvt_5_1_ = (BiomeCache.Block)this.cacheMap.getValueByKey(lvt_3_1_);

        if (lvt_5_1_ == null)
        {
            lvt_5_1_ = new BiomeCache.Block(x, z);
            this.cacheMap.add(lvt_3_1_, lvt_5_1_);
            this.cache.add(lvt_5_1_);
        }

        lvt_5_1_.lastAccessTime = MinecraftServer.getCurrentTimeMillis();
        return lvt_5_1_;
    }

    public BiomeGenBase func_180284_a(int x, int z, BiomeGenBase p_180284_3_)
    {
        BiomeGenBase lvt_4_1_ = this.getBiomeCacheBlock(x, z).getBiomeGenAt(x, z);
        return lvt_4_1_ == null ? p_180284_3_ : lvt_4_1_;
    }

    /**
     * Removes BiomeCacheBlocks from this cache that haven't been accessed in at least 30 seconds.
     */
    public void cleanupCache()
    {
        long lvt_1_1_ = MinecraftServer.getCurrentTimeMillis();
        long lvt_3_1_ = lvt_1_1_ - this.lastCleanupTime;

        if (lvt_3_1_ > 7500L || lvt_3_1_ < 0L)
        {
            this.lastCleanupTime = lvt_1_1_;

            for (int lvt_5_1_ = 0; lvt_5_1_ < this.cache.size(); ++lvt_5_1_)
            {
                BiomeCache.Block lvt_6_1_ = (BiomeCache.Block)this.cache.get(lvt_5_1_);
                long lvt_7_1_ = lvt_1_1_ - lvt_6_1_.lastAccessTime;

                if (lvt_7_1_ > 30000L || lvt_7_1_ < 0L)
                {
                    this.cache.remove(lvt_5_1_--);
                    long lvt_9_1_ = (long)lvt_6_1_.xPosition & 4294967295L | ((long)lvt_6_1_.zPosition & 4294967295L) << 32;
                    this.cacheMap.remove(lvt_9_1_);
                }
            }
        }
    }

    /**
     * Returns the array of cached biome types in the BiomeCacheBlock at the given location.
     */
    public BiomeGenBase[] getCachedBiomes(int x, int z)
    {
        return this.getBiomeCacheBlock(x, z).biomes;
    }

    public class Block
    {
        public float[] rainfallValues = new float[256];
        public BiomeGenBase[] biomes = new BiomeGenBase[256];
        public int xPosition;
        public int zPosition;
        public long lastAccessTime;

        public Block(int x, int z)
        {
            this.xPosition = x;
            this.zPosition = z;
            BiomeCache.this.chunkManager.getRainfall(this.rainfallValues, x << 4, z << 4, 16, 16);
            BiomeCache.this.chunkManager.getBiomeGenAt(this.biomes, x << 4, z << 4, 16, 16, false);
        }

        public BiomeGenBase getBiomeGenAt(int x, int z)
        {
            return this.biomes[x & 15 | (z & 15) << 4];
        }
    }
}
