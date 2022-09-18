package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderServer implements IChunkProvider
{
    private static final Logger logger = LogManager.getLogger();
    private Set<Long> droppedChunksSet = Collections.newSetFromMap(new ConcurrentHashMap());

    /** a dummy chunk, returned in place of an actual chunk. */
    private Chunk dummyChunk;

    /**
     * chunk generator object. Calls to load nonexistent chunks are forwarded to this object.
     */
    private IChunkProvider serverChunkGenerator;
    private IChunkLoader chunkLoader;

    /**
     * if set, this flag forces a request to load a chunk to load the chunk rather than defaulting to the dummy if
     * possible
     */
    public boolean chunkLoadOverride = true;
    private LongHashMap<Chunk> id2ChunkMap = new LongHashMap();
    private List<Chunk> loadedChunks = Lists.newArrayList();
    private WorldServer worldObj;

    public ChunkProviderServer(WorldServer p_i1520_1_, IChunkLoader p_i1520_2_, IChunkProvider p_i1520_3_)
    {
        this.dummyChunk = new EmptyChunk(p_i1520_1_, 0, 0);
        this.worldObj = p_i1520_1_;
        this.chunkLoader = p_i1520_2_;
        this.serverChunkGenerator = p_i1520_3_;
    }

    /**
     * Checks to see if a chunk exists at x, z
     */
    public boolean chunkExists(int x, int z)
    {
        return this.id2ChunkMap.containsItem(ChunkCoordIntPair.chunkXZ2Int(x, z));
    }

    public List<Chunk> func_152380_a()
    {
        return this.loadedChunks;
    }

    public void dropChunk(int x, int z)
    {
        if (this.worldObj.provider.canRespawnHere())
        {
            if (!this.worldObj.isSpawnChunk(x, z))
            {
                this.droppedChunksSet.add(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(x, z)));
            }
        }
        else
        {
            this.droppedChunksSet.add(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(x, z)));
        }
    }

    /**
     * marks all chunks for unload, ignoring those near the spawn
     */
    public void unloadAllChunks()
    {
        for (Chunk lvt_2_1_ : this.loadedChunks)
        {
            this.dropChunk(lvt_2_1_.xPosition, lvt_2_1_.zPosition);
        }
    }

    /**
     * loads or generates the chunk at the chunk location specified
     *  
     * @param chunkX x coord of the chunk to load (block coord >> 4)
     * @param chunkZ z coord of the chunk to load (block coord >> 4)
     */
    public Chunk loadChunk(int chunkX, int chunkZ)
    {
        long lvt_3_1_ = ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ);
        this.droppedChunksSet.remove(Long.valueOf(lvt_3_1_));
        Chunk lvt_5_1_ = (Chunk)this.id2ChunkMap.getValueByKey(lvt_3_1_);

        if (lvt_5_1_ == null)
        {
            lvt_5_1_ = this.loadChunkFromFile(chunkX, chunkZ);

            if (lvt_5_1_ == null)
            {
                if (this.serverChunkGenerator == null)
                {
                    lvt_5_1_ = this.dummyChunk;
                }
                else
                {
                    try
                    {
                        lvt_5_1_ = this.serverChunkGenerator.provideChunk(chunkX, chunkZ);
                    }
                    catch (Throwable var9)
                    {
                        CrashReport lvt_7_1_ = CrashReport.makeCrashReport(var9, "Exception generating new chunk");
                        CrashReportCategory lvt_8_1_ = lvt_7_1_.makeCategory("Chunk to be generated");
                        lvt_8_1_.addCrashSection("Location", String.format("%d,%d", new Object[] {Integer.valueOf(chunkX), Integer.valueOf(chunkZ)}));
                        lvt_8_1_.addCrashSection("Position hash", Long.valueOf(lvt_3_1_));
                        lvt_8_1_.addCrashSection("Generator", this.serverChunkGenerator.makeString());
                        throw new ReportedException(lvt_7_1_);
                    }
                }
            }

            this.id2ChunkMap.add(lvt_3_1_, lvt_5_1_);
            this.loadedChunks.add(lvt_5_1_);
            lvt_5_1_.onChunkLoad();
            lvt_5_1_.populateChunk(this, this, chunkX, chunkZ);
        }

        return lvt_5_1_;
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    public Chunk provideChunk(int x, int z)
    {
        Chunk lvt_3_1_ = (Chunk)this.id2ChunkMap.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(x, z));
        return lvt_3_1_ == null ? (!this.worldObj.isFindingSpawnPoint() && !this.chunkLoadOverride ? this.dummyChunk : this.loadChunk(x, z)) : lvt_3_1_;
    }

    private Chunk loadChunkFromFile(int x, int z)
    {
        if (this.chunkLoader == null)
        {
            return null;
        }
        else
        {
            try
            {
                Chunk lvt_3_1_ = this.chunkLoader.loadChunk(this.worldObj, x, z);

                if (lvt_3_1_ != null)
                {
                    lvt_3_1_.setLastSaveTime(this.worldObj.getTotalWorldTime());

                    if (this.serverChunkGenerator != null)
                    {
                        this.serverChunkGenerator.recreateStructures(lvt_3_1_, x, z);
                    }
                }

                return lvt_3_1_;
            }
            catch (Exception var4)
            {
                logger.error("Couldn\'t load chunk", var4);
                return null;
            }
        }
    }

    private void saveChunkExtraData(Chunk chunkIn)
    {
        if (this.chunkLoader != null)
        {
            try
            {
                this.chunkLoader.saveExtraChunkData(this.worldObj, chunkIn);
            }
            catch (Exception var3)
            {
                logger.error("Couldn\'t save entities", var3);
            }
        }
    }

    private void saveChunkData(Chunk chunkIn)
    {
        if (this.chunkLoader != null)
        {
            try
            {
                chunkIn.setLastSaveTime(this.worldObj.getTotalWorldTime());
                this.chunkLoader.saveChunk(this.worldObj, chunkIn);
            }
            catch (IOException var3)
            {
                logger.error("Couldn\'t save chunk", var3);
            }
            catch (MinecraftException var4)
            {
                logger.error("Couldn\'t save chunk; already in use by another instance of Minecraft?", var4);
            }
        }
    }

    /**
     * Populates chunk with ores etc etc
     */
    public void populate(IChunkProvider chunkProvider, int x, int z)
    {
        Chunk lvt_4_1_ = this.provideChunk(x, z);

        if (!lvt_4_1_.isTerrainPopulated())
        {
            lvt_4_1_.func_150809_p();

            if (this.serverChunkGenerator != null)
            {
                this.serverChunkGenerator.populate(chunkProvider, x, z);
                lvt_4_1_.setChunkModified();
            }
        }
    }

    public boolean populateChunk(IChunkProvider chunkProvider, Chunk chunkIn, int x, int z)
    {
        if (this.serverChunkGenerator != null && this.serverChunkGenerator.populateChunk(chunkProvider, chunkIn, x, z))
        {
            Chunk lvt_5_1_ = this.provideChunk(x, z);
            lvt_5_1_.setChunkModified();
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
     * Return true if all chunks have been saved.
     */
    public boolean saveChunks(boolean saveAllChunks, IProgressUpdate progressCallback)
    {
        int lvt_3_1_ = 0;
        List<Chunk> lvt_4_1_ = Lists.newArrayList(this.loadedChunks);

        for (int lvt_5_1_ = 0; lvt_5_1_ < ((List)lvt_4_1_).size(); ++lvt_5_1_)
        {
            Chunk lvt_6_1_ = (Chunk)lvt_4_1_.get(lvt_5_1_);

            if (saveAllChunks)
            {
                this.saveChunkExtraData(lvt_6_1_);
            }

            if (lvt_6_1_.needsSaving(saveAllChunks))
            {
                this.saveChunkData(lvt_6_1_);
                lvt_6_1_.setModified(false);
                ++lvt_3_1_;

                if (lvt_3_1_ == 24 && !saveAllChunks)
                {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Save extra data not associated with any Chunk.  Not saved during autosave, only during world unload.  Currently
     * unimplemented.
     */
    public void saveExtraData()
    {
        if (this.chunkLoader != null)
        {
            this.chunkLoader.saveExtraData();
        }
    }

    /**
     * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
     */
    public boolean unloadQueuedChunks()
    {
        if (!this.worldObj.disableLevelSaving)
        {
            for (int lvt_1_1_ = 0; lvt_1_1_ < 100; ++lvt_1_1_)
            {
                if (!this.droppedChunksSet.isEmpty())
                {
                    Long lvt_2_1_ = (Long)this.droppedChunksSet.iterator().next();
                    Chunk lvt_3_1_ = (Chunk)this.id2ChunkMap.getValueByKey(lvt_2_1_.longValue());

                    if (lvt_3_1_ != null)
                    {
                        lvt_3_1_.onChunkUnload();
                        this.saveChunkData(lvt_3_1_);
                        this.saveChunkExtraData(lvt_3_1_);
                        this.id2ChunkMap.remove(lvt_2_1_.longValue());
                        this.loadedChunks.remove(lvt_3_1_);
                    }

                    this.droppedChunksSet.remove(lvt_2_1_);
                }
            }

            if (this.chunkLoader != null)
            {
                this.chunkLoader.chunkTick();
            }
        }

        return this.serverChunkGenerator.unloadQueuedChunks();
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    public boolean canSave()
    {
        return !this.worldObj.disableLevelSaving;
    }

    /**
     * Converts the instance data to a readable string.
     */
    public String makeString()
    {
        return "ServerChunkCache: " + this.id2ChunkMap.getNumHashElements() + " Drop: " + this.droppedChunksSet.size();
    }

    public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
    {
        return this.serverChunkGenerator.getPossibleCreatures(creatureType, pos);
    }

    public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position)
    {
        return this.serverChunkGenerator.getStrongholdGen(worldIn, structureName, position);
    }

    public int getLoadedChunkCount()
    {
        return this.id2ChunkMap.getNumHashElements();
    }

    public void recreateStructures(Chunk chunkIn, int x, int z)
    {
    }

    public Chunk provideChunk(BlockPos blockPosIn)
    {
        return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
    }
}
