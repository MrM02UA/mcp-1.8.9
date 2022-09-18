package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureOceanMonument;

public class ChunkProviderFlat implements IChunkProvider
{
    private World worldObj;
    private Random random;
    private final IBlockState[] cachedBlockIDs = new IBlockState[256];
    private final FlatGeneratorInfo flatWorldGenInfo;
    private final List<MapGenStructure> structureGenerators = Lists.newArrayList();
    private final boolean hasDecoration;
    private final boolean hasDungeons;
    private WorldGenLakes waterLakeGenerator;
    private WorldGenLakes lavaLakeGenerator;

    public ChunkProviderFlat(World worldIn, long seed, boolean generateStructures, String flatGeneratorSettings)
    {
        this.worldObj = worldIn;
        this.random = new Random(seed);
        this.flatWorldGenInfo = FlatGeneratorInfo.createFlatGeneratorFromString(flatGeneratorSettings);

        if (generateStructures)
        {
            Map<String, Map<String, String>> lvt_6_1_ = this.flatWorldGenInfo.getWorldFeatures();

            if (lvt_6_1_.containsKey("village"))
            {
                Map<String, String> lvt_7_1_ = (Map)lvt_6_1_.get("village");

                if (!lvt_7_1_.containsKey("size"))
                {
                    lvt_7_1_.put("size", "1");
                }

                this.structureGenerators.add(new MapGenVillage(lvt_7_1_));
            }

            if (lvt_6_1_.containsKey("biome_1"))
            {
                this.structureGenerators.add(new MapGenScatteredFeature((Map)lvt_6_1_.get("biome_1")));
            }

            if (lvt_6_1_.containsKey("mineshaft"))
            {
                this.structureGenerators.add(new MapGenMineshaft((Map)lvt_6_1_.get("mineshaft")));
            }

            if (lvt_6_1_.containsKey("stronghold"))
            {
                this.structureGenerators.add(new MapGenStronghold((Map)lvt_6_1_.get("stronghold")));
            }

            if (lvt_6_1_.containsKey("oceanmonument"))
            {
                this.structureGenerators.add(new StructureOceanMonument((Map)lvt_6_1_.get("oceanmonument")));
            }
        }

        if (this.flatWorldGenInfo.getWorldFeatures().containsKey("lake"))
        {
            this.waterLakeGenerator = new WorldGenLakes(Blocks.water);
        }

        if (this.flatWorldGenInfo.getWorldFeatures().containsKey("lava_lake"))
        {
            this.lavaLakeGenerator = new WorldGenLakes(Blocks.lava);
        }

        this.hasDungeons = this.flatWorldGenInfo.getWorldFeatures().containsKey("dungeon");
        int lvt_6_2_ = 0;
        int lvt_7_2_ = 0;
        boolean lvt_8_1_ = true;

        for (FlatLayerInfo lvt_10_1_ : this.flatWorldGenInfo.getFlatLayers())
        {
            for (int lvt_11_1_ = lvt_10_1_.getMinY(); lvt_11_1_ < lvt_10_1_.getMinY() + lvt_10_1_.getLayerCount(); ++lvt_11_1_)
            {
                IBlockState lvt_12_1_ = lvt_10_1_.getLayerMaterial();

                if (lvt_12_1_.getBlock() != Blocks.air)
                {
                    lvt_8_1_ = false;
                    this.cachedBlockIDs[lvt_11_1_] = lvt_12_1_;
                }
            }

            if (lvt_10_1_.getLayerMaterial().getBlock() == Blocks.air)
            {
                lvt_7_2_ += lvt_10_1_.getLayerCount();
            }
            else
            {
                lvt_6_2_ += lvt_10_1_.getLayerCount() + lvt_7_2_;
                lvt_7_2_ = 0;
            }
        }

        worldIn.setSeaLevel(lvt_6_2_);
        this.hasDecoration = lvt_8_1_ ? false : this.flatWorldGenInfo.getWorldFeatures().containsKey("decoration");
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    public Chunk provideChunk(int x, int z)
    {
        ChunkPrimer lvt_3_1_ = new ChunkPrimer();

        for (int lvt_4_1_ = 0; lvt_4_1_ < this.cachedBlockIDs.length; ++lvt_4_1_)
        {
            IBlockState lvt_5_1_ = this.cachedBlockIDs[lvt_4_1_];

            if (lvt_5_1_ != null)
            {
                for (int lvt_6_1_ = 0; lvt_6_1_ < 16; ++lvt_6_1_)
                {
                    for (int lvt_7_1_ = 0; lvt_7_1_ < 16; ++lvt_7_1_)
                    {
                        lvt_3_1_.setBlockState(lvt_6_1_, lvt_4_1_, lvt_7_1_, lvt_5_1_);
                    }
                }
            }
        }

        for (MapGenBase lvt_5_2_ : this.structureGenerators)
        {
            lvt_5_2_.generate(this, this.worldObj, x, z, lvt_3_1_);
        }

        Chunk lvt_4_3_ = new Chunk(this.worldObj, lvt_3_1_, x, z);
        BiomeGenBase[] lvt_5_3_ = this.worldObj.getWorldChunkManager().loadBlockGeneratorData((BiomeGenBase[])null, x * 16, z * 16, 16, 16);
        byte[] lvt_6_2_ = lvt_4_3_.getBiomeArray();

        for (int lvt_7_2_ = 0; lvt_7_2_ < lvt_6_2_.length; ++lvt_7_2_)
        {
            lvt_6_2_[lvt_7_2_] = (byte)lvt_5_3_[lvt_7_2_].biomeID;
        }

        lvt_4_3_.generateSkylightMap();
        return lvt_4_3_;
    }

    /**
     * Checks to see if a chunk exists at x, z
     */
    public boolean chunkExists(int x, int z)
    {
        return true;
    }

    /**
     * Populates chunk with ores etc etc
     */
    public void populate(IChunkProvider chunkProvider, int x, int z)
    {
        int lvt_4_1_ = x * 16;
        int lvt_5_1_ = z * 16;
        BlockPos lvt_6_1_ = new BlockPos(lvt_4_1_, 0, lvt_5_1_);
        BiomeGenBase lvt_7_1_ = this.worldObj.getBiomeGenForCoords(new BlockPos(lvt_4_1_ + 16, 0, lvt_5_1_ + 16));
        boolean lvt_8_1_ = false;
        this.random.setSeed(this.worldObj.getSeed());
        long lvt_9_1_ = this.random.nextLong() / 2L * 2L + 1L;
        long lvt_11_1_ = this.random.nextLong() / 2L * 2L + 1L;
        this.random.setSeed((long)x * lvt_9_1_ + (long)z * lvt_11_1_ ^ this.worldObj.getSeed());
        ChunkCoordIntPair lvt_13_1_ = new ChunkCoordIntPair(x, z);

        for (MapGenStructure lvt_15_1_ : this.structureGenerators)
        {
            boolean lvt_16_1_ = lvt_15_1_.generateStructure(this.worldObj, this.random, lvt_13_1_);

            if (lvt_15_1_ instanceof MapGenVillage)
            {
                lvt_8_1_ |= lvt_16_1_;
            }
        }

        if (this.waterLakeGenerator != null && !lvt_8_1_ && this.random.nextInt(4) == 0)
        {
            this.waterLakeGenerator.generate(this.worldObj, this.random, lvt_6_1_.add(this.random.nextInt(16) + 8, this.random.nextInt(256), this.random.nextInt(16) + 8));
        }

        if (this.lavaLakeGenerator != null && !lvt_8_1_ && this.random.nextInt(8) == 0)
        {
            BlockPos lvt_14_2_ = lvt_6_1_.add(this.random.nextInt(16) + 8, this.random.nextInt(this.random.nextInt(248) + 8), this.random.nextInt(16) + 8);

            if (lvt_14_2_.getY() < this.worldObj.getSeaLevel() || this.random.nextInt(10) == 0)
            {
                this.lavaLakeGenerator.generate(this.worldObj, this.random, lvt_14_2_);
            }
        }

        if (this.hasDungeons)
        {
            for (int lvt_14_3_ = 0; lvt_14_3_ < 8; ++lvt_14_3_)
            {
                (new WorldGenDungeons()).generate(this.worldObj, this.random, lvt_6_1_.add(this.random.nextInt(16) + 8, this.random.nextInt(256), this.random.nextInt(16) + 8));
            }
        }

        if (this.hasDecoration)
        {
            lvt_7_1_.decorate(this.worldObj, this.random, lvt_6_1_);
        }
    }

    public boolean populateChunk(IChunkProvider chunkProvider, Chunk chunkIn, int x, int z)
    {
        return false;
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
     * Return true if all chunks have been saved.
     */
    public boolean saveChunks(boolean saveAllChunks, IProgressUpdate progressCallback)
    {
        return true;
    }

    /**
     * Save extra data not associated with any Chunk.  Not saved during autosave, only during world unload.  Currently
     * unimplemented.
     */
    public void saveExtraData()
    {
    }

    /**
     * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
     */
    public boolean unloadQueuedChunks()
    {
        return false;
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    public boolean canSave()
    {
        return true;
    }

    /**
     * Converts the instance data to a readable string.
     */
    public String makeString()
    {
        return "FlatLevelSource";
    }

    public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
    {
        BiomeGenBase lvt_3_1_ = this.worldObj.getBiomeGenForCoords(pos);
        return lvt_3_1_.getSpawnableList(creatureType);
    }

    public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position)
    {
        if ("Stronghold".equals(structureName))
        {
            for (MapGenStructure lvt_5_1_ : this.structureGenerators)
            {
                if (lvt_5_1_ instanceof MapGenStronghold)
                {
                    return lvt_5_1_.getClosestStrongholdPos(worldIn, position);
                }
            }
        }

        return null;
    }

    public int getLoadedChunkCount()
    {
        return 0;
    }

    public void recreateStructures(Chunk chunkIn, int x, int z)
    {
        for (MapGenStructure lvt_5_1_ : this.structureGenerators)
        {
            lvt_5_1_.generate(this, this.worldObj, x, z, (ChunkPrimer)null);
        }
    }

    public Chunk provideChunk(BlockPos blockPosIn)
    {
        return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
    }
}
