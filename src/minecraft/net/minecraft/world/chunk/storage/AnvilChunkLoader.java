package net.minecraft.world.chunk.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.storage.IThreadedFileIO;
import net.minecraft.world.storage.ThreadedFileIOBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilChunkLoader implements IChunkLoader, IThreadedFileIO
{
    private static final Logger logger = LogManager.getLogger();
    private Map<ChunkCoordIntPair, NBTTagCompound> chunksToRemove = new ConcurrentHashMap();
    private Set<ChunkCoordIntPair> pendingAnvilChunksCoordinates = Collections.newSetFromMap(new ConcurrentHashMap());

    /** Save directory for chunks using the Anvil format */
    private final File chunkSaveLocation;
    private boolean field_183014_e = false;

    public AnvilChunkLoader(File chunkSaveLocationIn)
    {
        this.chunkSaveLocation = chunkSaveLocationIn;
    }

    /**
     * Loads the specified(XZ) chunk into the specified world.
     */
    public Chunk loadChunk(World worldIn, int x, int z) throws IOException
    {
        ChunkCoordIntPair lvt_4_1_ = new ChunkCoordIntPair(x, z);
        NBTTagCompound lvt_5_1_ = (NBTTagCompound)this.chunksToRemove.get(lvt_4_1_);

        if (lvt_5_1_ == null)
        {
            DataInputStream lvt_6_1_ = RegionFileCache.getChunkInputStream(this.chunkSaveLocation, x, z);

            if (lvt_6_1_ == null)
            {
                return null;
            }

            lvt_5_1_ = CompressedStreamTools.read(lvt_6_1_);
        }

        return this.checkedReadChunkFromNBT(worldIn, x, z, lvt_5_1_);
    }

    /**
     * Wraps readChunkFromNBT. Checks the coordinates and several NBT tags.
     */
    protected Chunk checkedReadChunkFromNBT(World worldIn, int x, int z, NBTTagCompound p_75822_4_)
    {
        if (!p_75822_4_.hasKey("Level", 10))
        {
            logger.error("Chunk file at " + x + "," + z + " is missing level data, skipping");
            return null;
        }
        else
        {
            NBTTagCompound lvt_5_1_ = p_75822_4_.getCompoundTag("Level");

            if (!lvt_5_1_.hasKey("Sections", 9))
            {
                logger.error("Chunk file at " + x + "," + z + " is missing block data, skipping");
                return null;
            }
            else
            {
                Chunk lvt_6_1_ = this.readChunkFromNBT(worldIn, lvt_5_1_);

                if (!lvt_6_1_.isAtLocation(x, z))
                {
                    logger.error("Chunk file at " + x + "," + z + " is in the wrong location; relocating. (Expected " + x + ", " + z + ", got " + lvt_6_1_.xPosition + ", " + lvt_6_1_.zPosition + ")");
                    lvt_5_1_.setInteger("xPos", x);
                    lvt_5_1_.setInteger("zPos", z);
                    lvt_6_1_ = this.readChunkFromNBT(worldIn, lvt_5_1_);
                }

                return lvt_6_1_;
            }
        }
    }

    public void saveChunk(World worldIn, Chunk chunkIn) throws MinecraftException, IOException
    {
        worldIn.checkSessionLock();

        try
        {
            NBTTagCompound lvt_3_1_ = new NBTTagCompound();
            NBTTagCompound lvt_4_1_ = new NBTTagCompound();
            lvt_3_1_.setTag("Level", lvt_4_1_);
            this.writeChunkToNBT(chunkIn, worldIn, lvt_4_1_);
            this.addChunkToPending(chunkIn.getChunkCoordIntPair(), lvt_3_1_);
        }
        catch (Exception var5)
        {
            logger.error("Failed to save chunk", var5);
        }
    }

    protected void addChunkToPending(ChunkCoordIntPair p_75824_1_, NBTTagCompound p_75824_2_)
    {
        if (!this.pendingAnvilChunksCoordinates.contains(p_75824_1_))
        {
            this.chunksToRemove.put(p_75824_1_, p_75824_2_);
        }

        ThreadedFileIOBase.getThreadedIOInstance().queueIO(this);
    }

    /**
     * Returns a boolean stating if the write was unsuccessful.
     */
    public boolean writeNextIO()
    {
        if (this.chunksToRemove.isEmpty())
        {
            if (this.field_183014_e)
            {
                logger.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", new Object[] {this.chunkSaveLocation.getName()});
            }

            return false;
        }
        else
        {
            ChunkCoordIntPair lvt_1_1_ = (ChunkCoordIntPair)this.chunksToRemove.keySet().iterator().next();
            boolean lvt_3_1_;

            try
            {
                this.pendingAnvilChunksCoordinates.add(lvt_1_1_);
                NBTTagCompound lvt_2_1_ = (NBTTagCompound)this.chunksToRemove.remove(lvt_1_1_);

                if (lvt_2_1_ != null)
                {
                    try
                    {
                        this.func_183013_b(lvt_1_1_, lvt_2_1_);
                    }
                    catch (Exception var7)
                    {
                        logger.error("Failed to save chunk", var7);
                    }
                }

                lvt_3_1_ = true;
            }
            finally
            {
                this.pendingAnvilChunksCoordinates.remove(lvt_1_1_);
            }

            return lvt_3_1_;
        }
    }

    private void func_183013_b(ChunkCoordIntPair p_183013_1_, NBTTagCompound p_183013_2_) throws IOException
    {
        DataOutputStream lvt_3_1_ = RegionFileCache.getChunkOutputStream(this.chunkSaveLocation, p_183013_1_.chunkXPos, p_183013_1_.chunkZPos);
        CompressedStreamTools.write(p_183013_2_, lvt_3_1_);
        lvt_3_1_.close();
    }

    /**
     * Save extra data associated with this Chunk not normally saved during autosave, only during chunk unload.
     * Currently unused.
     */
    public void saveExtraChunkData(World worldIn, Chunk chunkIn) throws IOException
    {
    }

    /**
     * Called every World.tick()
     */
    public void chunkTick()
    {
    }

    /**
     * Save extra data not associated with any Chunk.  Not saved during autosave, only during world unload.  Currently
     * unused.
     */
    public void saveExtraData()
    {
        try
        {
            this.field_183014_e = true;

            while (true)
            {
                if (this.writeNextIO())
                {
                    continue;
                }
            }
        }
        finally
        {
            this.field_183014_e = false;
        }
    }

    /**
     * Writes the Chunk passed as an argument to the NBTTagCompound also passed, using the World argument to retrieve
     * the Chunk's last update time.
     */
    private void writeChunkToNBT(Chunk chunkIn, World worldIn, NBTTagCompound p_75820_3_)
    {
        p_75820_3_.setByte("V", (byte)1);
        p_75820_3_.setInteger("xPos", chunkIn.xPosition);
        p_75820_3_.setInteger("zPos", chunkIn.zPosition);
        p_75820_3_.setLong("LastUpdate", worldIn.getTotalWorldTime());
        p_75820_3_.setIntArray("HeightMap", chunkIn.getHeightMap());
        p_75820_3_.setBoolean("TerrainPopulated", chunkIn.isTerrainPopulated());
        p_75820_3_.setBoolean("LightPopulated", chunkIn.isLightPopulated());
        p_75820_3_.setLong("InhabitedTime", chunkIn.getInhabitedTime());
        ExtendedBlockStorage[] lvt_4_1_ = chunkIn.getBlockStorageArray();
        NBTTagList lvt_5_1_ = new NBTTagList();
        boolean lvt_6_1_ = !worldIn.provider.getHasNoSky();

        for (ExtendedBlockStorage lvt_10_1_ : lvt_4_1_)
        {
            if (lvt_10_1_ != null)
            {
                NBTTagCompound lvt_11_1_ = new NBTTagCompound();
                lvt_11_1_.setByte("Y", (byte)(lvt_10_1_.getYLocation() >> 4 & 255));
                byte[] lvt_12_1_ = new byte[lvt_10_1_.getData().length];
                NibbleArray lvt_13_1_ = new NibbleArray();
                NibbleArray lvt_14_1_ = null;

                for (int lvt_15_1_ = 0; lvt_15_1_ < lvt_10_1_.getData().length; ++lvt_15_1_)
                {
                    char lvt_16_1_ = lvt_10_1_.getData()[lvt_15_1_];
                    int lvt_17_1_ = lvt_15_1_ & 15;
                    int lvt_18_1_ = lvt_15_1_ >> 8 & 15;
                    int lvt_19_1_ = lvt_15_1_ >> 4 & 15;

                    if (lvt_16_1_ >> 12 != 0)
                    {
                        if (lvt_14_1_ == null)
                        {
                            lvt_14_1_ = new NibbleArray();
                        }

                        lvt_14_1_.set(lvt_17_1_, lvt_18_1_, lvt_19_1_, lvt_16_1_ >> 12);
                    }

                    lvt_12_1_[lvt_15_1_] = (byte)(lvt_16_1_ >> 4 & 255);
                    lvt_13_1_.set(lvt_17_1_, lvt_18_1_, lvt_19_1_, lvt_16_1_ & 15);
                }

                lvt_11_1_.setByteArray("Blocks", lvt_12_1_);
                lvt_11_1_.setByteArray("Data", lvt_13_1_.getData());

                if (lvt_14_1_ != null)
                {
                    lvt_11_1_.setByteArray("Add", lvt_14_1_.getData());
                }

                lvt_11_1_.setByteArray("BlockLight", lvt_10_1_.getBlocklightArray().getData());

                if (lvt_6_1_)
                {
                    lvt_11_1_.setByteArray("SkyLight", lvt_10_1_.getSkylightArray().getData());
                }
                else
                {
                    lvt_11_1_.setByteArray("SkyLight", new byte[lvt_10_1_.getBlocklightArray().getData().length]);
                }

                lvt_5_1_.appendTag(lvt_11_1_);
            }
        }

        p_75820_3_.setTag("Sections", lvt_5_1_);
        p_75820_3_.setByteArray("Biomes", chunkIn.getBiomeArray());
        chunkIn.setHasEntities(false);
        NBTTagList lvt_7_2_ = new NBTTagList();

        for (int lvt_8_2_ = 0; lvt_8_2_ < chunkIn.getEntityLists().length; ++lvt_8_2_)
        {
            for (Entity lvt_10_2_ : chunkIn.getEntityLists()[lvt_8_2_])
            {
                NBTTagCompound lvt_11_2_ = new NBTTagCompound();

                if (lvt_10_2_.writeToNBTOptional(lvt_11_2_))
                {
                    chunkIn.setHasEntities(true);
                    lvt_7_2_.appendTag(lvt_11_2_);
                }
            }
        }

        p_75820_3_.setTag("Entities", lvt_7_2_);
        NBTTagList lvt_8_3_ = new NBTTagList();

        for (TileEntity lvt_10_3_ : chunkIn.getTileEntityMap().values())
        {
            NBTTagCompound lvt_11_3_ = new NBTTagCompound();
            lvt_10_3_.writeToNBT(lvt_11_3_);
            lvt_8_3_.appendTag(lvt_11_3_);
        }

        p_75820_3_.setTag("TileEntities", lvt_8_3_);
        List<NextTickListEntry> lvt_9_4_ = worldIn.getPendingBlockUpdates(chunkIn, false);

        if (lvt_9_4_ != null)
        {
            long lvt_10_4_ = worldIn.getTotalWorldTime();
            NBTTagList lvt_12_2_ = new NBTTagList();

            for (NextTickListEntry lvt_14_2_ : lvt_9_4_)
            {
                NBTTagCompound lvt_15_2_ = new NBTTagCompound();
                ResourceLocation lvt_16_2_ = (ResourceLocation)Block.blockRegistry.getNameForObject(lvt_14_2_.getBlock());
                lvt_15_2_.setString("i", lvt_16_2_ == null ? "" : lvt_16_2_.toString());
                lvt_15_2_.setInteger("x", lvt_14_2_.position.getX());
                lvt_15_2_.setInteger("y", lvt_14_2_.position.getY());
                lvt_15_2_.setInteger("z", lvt_14_2_.position.getZ());
                lvt_15_2_.setInteger("t", (int)(lvt_14_2_.scheduledTime - lvt_10_4_));
                lvt_15_2_.setInteger("p", lvt_14_2_.priority);
                lvt_12_2_.appendTag(lvt_15_2_);
            }

            p_75820_3_.setTag("TileTicks", lvt_12_2_);
        }
    }

    /**
     * Reads the data stored in the passed NBTTagCompound and creates a Chunk with that data in the passed World.
     * Returns the created Chunk.
     */
    private Chunk readChunkFromNBT(World worldIn, NBTTagCompound p_75823_2_)
    {
        int lvt_3_1_ = p_75823_2_.getInteger("xPos");
        int lvt_4_1_ = p_75823_2_.getInteger("zPos");
        Chunk lvt_5_1_ = new Chunk(worldIn, lvt_3_1_, lvt_4_1_);
        lvt_5_1_.setHeightMap(p_75823_2_.getIntArray("HeightMap"));
        lvt_5_1_.setTerrainPopulated(p_75823_2_.getBoolean("TerrainPopulated"));
        lvt_5_1_.setLightPopulated(p_75823_2_.getBoolean("LightPopulated"));
        lvt_5_1_.setInhabitedTime(p_75823_2_.getLong("InhabitedTime"));
        NBTTagList lvt_6_1_ = p_75823_2_.getTagList("Sections", 10);
        int lvt_7_1_ = 16;
        ExtendedBlockStorage[] lvt_8_1_ = new ExtendedBlockStorage[lvt_7_1_];
        boolean lvt_9_1_ = !worldIn.provider.getHasNoSky();

        for (int lvt_10_1_ = 0; lvt_10_1_ < lvt_6_1_.tagCount(); ++lvt_10_1_)
        {
            NBTTagCompound lvt_11_1_ = lvt_6_1_.getCompoundTagAt(lvt_10_1_);
            int lvt_12_1_ = lvt_11_1_.getByte("Y");
            ExtendedBlockStorage lvt_13_1_ = new ExtendedBlockStorage(lvt_12_1_ << 4, lvt_9_1_);
            byte[] lvt_14_1_ = lvt_11_1_.getByteArray("Blocks");
            NibbleArray lvt_15_1_ = new NibbleArray(lvt_11_1_.getByteArray("Data"));
            NibbleArray lvt_16_1_ = lvt_11_1_.hasKey("Add", 7) ? new NibbleArray(lvt_11_1_.getByteArray("Add")) : null;
            char[] lvt_17_1_ = new char[lvt_14_1_.length];

            for (int lvt_18_1_ = 0; lvt_18_1_ < lvt_17_1_.length; ++lvt_18_1_)
            {
                int lvt_19_1_ = lvt_18_1_ & 15;
                int lvt_20_1_ = lvt_18_1_ >> 8 & 15;
                int lvt_21_1_ = lvt_18_1_ >> 4 & 15;
                int lvt_22_1_ = lvt_16_1_ != null ? lvt_16_1_.get(lvt_19_1_, lvt_20_1_, lvt_21_1_) : 0;
                lvt_17_1_[lvt_18_1_] = (char)(lvt_22_1_ << 12 | (lvt_14_1_[lvt_18_1_] & 255) << 4 | lvt_15_1_.get(lvt_19_1_, lvt_20_1_, lvt_21_1_));
            }

            lvt_13_1_.setData(lvt_17_1_);
            lvt_13_1_.setBlocklightArray(new NibbleArray(lvt_11_1_.getByteArray("BlockLight")));

            if (lvt_9_1_)
            {
                lvt_13_1_.setSkylightArray(new NibbleArray(lvt_11_1_.getByteArray("SkyLight")));
            }

            lvt_13_1_.removeInvalidBlocks();
            lvt_8_1_[lvt_12_1_] = lvt_13_1_;
        }

        lvt_5_1_.setStorageArrays(lvt_8_1_);

        if (p_75823_2_.hasKey("Biomes", 7))
        {
            lvt_5_1_.setBiomeArray(p_75823_2_.getByteArray("Biomes"));
        }

        NBTTagList lvt_10_2_ = p_75823_2_.getTagList("Entities", 10);

        if (lvt_10_2_ != null)
        {
            for (int lvt_11_2_ = 0; lvt_11_2_ < lvt_10_2_.tagCount(); ++lvt_11_2_)
            {
                NBTTagCompound lvt_12_2_ = lvt_10_2_.getCompoundTagAt(lvt_11_2_);
                Entity lvt_13_2_ = EntityList.createEntityFromNBT(lvt_12_2_, worldIn);
                lvt_5_1_.setHasEntities(true);

                if (lvt_13_2_ != null)
                {
                    lvt_5_1_.addEntity(lvt_13_2_);
                    Entity lvt_14_2_ = lvt_13_2_;

                    for (NBTTagCompound lvt_15_2_ = lvt_12_2_; lvt_15_2_.hasKey("Riding", 10); lvt_15_2_ = lvt_15_2_.getCompoundTag("Riding"))
                    {
                        Entity lvt_16_2_ = EntityList.createEntityFromNBT(lvt_15_2_.getCompoundTag("Riding"), worldIn);

                        if (lvt_16_2_ != null)
                        {
                            lvt_5_1_.addEntity(lvt_16_2_);
                            lvt_14_2_.mountEntity(lvt_16_2_);
                        }

                        lvt_14_2_ = lvt_16_2_;
                    }
                }
            }
        }

        NBTTagList lvt_11_3_ = p_75823_2_.getTagList("TileEntities", 10);

        if (lvt_11_3_ != null)
        {
            for (int lvt_12_3_ = 0; lvt_12_3_ < lvt_11_3_.tagCount(); ++lvt_12_3_)
            {
                NBTTagCompound lvt_13_3_ = lvt_11_3_.getCompoundTagAt(lvt_12_3_);
                TileEntity lvt_14_3_ = TileEntity.createAndLoadEntity(lvt_13_3_);

                if (lvt_14_3_ != null)
                {
                    lvt_5_1_.addTileEntity(lvt_14_3_);
                }
            }
        }

        if (p_75823_2_.hasKey("TileTicks", 9))
        {
            NBTTagList lvt_12_4_ = p_75823_2_.getTagList("TileTicks", 10);

            if (lvt_12_4_ != null)
            {
                for (int lvt_13_4_ = 0; lvt_13_4_ < lvt_12_4_.tagCount(); ++lvt_13_4_)
                {
                    NBTTagCompound lvt_14_4_ = lvt_12_4_.getCompoundTagAt(lvt_13_4_);
                    Block lvt_15_3_;

                    if (lvt_14_4_.hasKey("i", 8))
                    {
                        lvt_15_3_ = Block.getBlockFromName(lvt_14_4_.getString("i"));
                    }
                    else
                    {
                        lvt_15_3_ = Block.getBlockById(lvt_14_4_.getInteger("i"));
                    }

                    worldIn.scheduleBlockUpdate(new BlockPos(lvt_14_4_.getInteger("x"), lvt_14_4_.getInteger("y"), lvt_14_4_.getInteger("z")), lvt_15_3_, lvt_14_4_.getInteger("t"), lvt_14_4_.getInteger("p"));
                }
            }
        }

        return lvt_5_1_;
    }
}
