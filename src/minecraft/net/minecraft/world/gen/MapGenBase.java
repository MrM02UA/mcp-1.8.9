package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;

public class MapGenBase
{
    /** The number of Chunks to gen-check in any given direction. */
    protected int range = 8;

    /** The RNG used by the MapGen classes. */
    protected Random rand = new Random();

    /** This world object. */
    protected World worldObj;

    public void generate(IChunkProvider chunkProviderIn, World worldIn, int x, int z, ChunkPrimer chunkPrimerIn)
    {
        int lvt_6_1_ = this.range;
        this.worldObj = worldIn;
        this.rand.setSeed(worldIn.getSeed());
        long lvt_7_1_ = this.rand.nextLong();
        long lvt_9_1_ = this.rand.nextLong();

        for (int lvt_11_1_ = x - lvt_6_1_; lvt_11_1_ <= x + lvt_6_1_; ++lvt_11_1_)
        {
            for (int lvt_12_1_ = z - lvt_6_1_; lvt_12_1_ <= z + lvt_6_1_; ++lvt_12_1_)
            {
                long lvt_13_1_ = (long)lvt_11_1_ * lvt_7_1_;
                long lvt_15_1_ = (long)lvt_12_1_ * lvt_9_1_;
                this.rand.setSeed(lvt_13_1_ ^ lvt_15_1_ ^ worldIn.getSeed());
                this.recursiveGenerate(worldIn, lvt_11_1_, lvt_12_1_, x, z, chunkPrimerIn);
            }
        }
    }

    /**
     * Recursively called by generate()
     */
    protected void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int p_180701_4_, int p_180701_5_, ChunkPrimer chunkPrimerIn)
    {
    }
}
