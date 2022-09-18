package net.minecraft.world.chunk;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.ChunkProviderDebug;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chunk
{
    private static final Logger logger = LogManager.getLogger();

    /**
     * Used to store block IDs, block MSBs, Sky-light maps, Block-light maps, and metadata. Each entry corresponds to a
     * logical segment of 16x16x16 blocks, stacked vertically.
     */
    private final ExtendedBlockStorage[] storageArrays;

    /**
     * Contains a 16x16 mapping on the X/Z plane of the biome ID to which each colum belongs.
     */
    private final byte[] blockBiomeArray;

    /**
     * A map, similar to heightMap, that tracks how far down precipitation can fall.
     */
    private final int[] precipitationHeightMap;

    /** Which columns need their skylightMaps updated. */
    private final boolean[] updateSkylightColumns;

    /** Whether or not this Chunk is currently loaded into the World */
    private boolean isChunkLoaded;

    /** Reference to the World object. */
    private final World worldObj;
    private final int[] heightMap;

    /** The x coordinate of the chunk. */
    public final int xPosition;

    /** The z coordinate of the chunk. */
    public final int zPosition;
    private boolean isGapLightingUpdated;
    private final Map<BlockPos, TileEntity> chunkTileEntityMap;
    private final ClassInheritanceMultiMap<Entity>[] entityLists;

    /** Boolean value indicating if the terrain is populated. */
    private boolean isTerrainPopulated;
    private boolean isLightPopulated;
    private boolean field_150815_m;

    /**
     * Set to true if the chunk has been modified and needs to be updated internally.
     */
    private boolean isModified;

    /**
     * Whether this Chunk has any Entities and thus requires saving on every tick
     */
    private boolean hasEntities;

    /** The time according to World.worldTime when this chunk was last saved */
    private long lastSaveTime;

    /** Lowest value in the heightmap. */
    private int heightMapMinimum;

    /** the cumulative number of ticks players have been in this chunk */
    private long inhabitedTime;

    /**
     * Contains the current round-robin relight check index, and is implied as the relight check location as well.
     */
    private int queuedLightChecks;
    private ConcurrentLinkedQueue<BlockPos> tileEntityPosQueue;

    public Chunk(World worldIn, int x, int z)
    {
        this.storageArrays = new ExtendedBlockStorage[16];
        this.blockBiomeArray = new byte[256];
        this.precipitationHeightMap = new int[256];
        this.updateSkylightColumns = new boolean[256];
        this.chunkTileEntityMap = Maps.newHashMap();
        this.queuedLightChecks = 4096;
        this.tileEntityPosQueue = Queues.newConcurrentLinkedQueue();
        this.entityLists = (ClassInheritanceMultiMap[])(new ClassInheritanceMultiMap[16]);
        this.worldObj = worldIn;
        this.xPosition = x;
        this.zPosition = z;
        this.heightMap = new int[256];

        for (int lvt_4_1_ = 0; lvt_4_1_ < this.entityLists.length; ++lvt_4_1_)
        {
            this.entityLists[lvt_4_1_] = new ClassInheritanceMultiMap(Entity.class);
        }

        Arrays.fill(this.precipitationHeightMap, -999);
        Arrays.fill(this.blockBiomeArray, (byte) - 1);
    }

    public Chunk(World worldIn, ChunkPrimer primer, int x, int z)
    {
        this(worldIn, x, z);
        int lvt_5_1_ = 256;
        boolean lvt_6_1_ = !worldIn.provider.getHasNoSky();

        for (int lvt_7_1_ = 0; lvt_7_1_ < 16; ++lvt_7_1_)
        {
            for (int lvt_8_1_ = 0; lvt_8_1_ < 16; ++lvt_8_1_)
            {
                for (int lvt_9_1_ = 0; lvt_9_1_ < lvt_5_1_; ++lvt_9_1_)
                {
                    int lvt_10_1_ = lvt_7_1_ * lvt_5_1_ * 16 | lvt_8_1_ * lvt_5_1_ | lvt_9_1_;
                    IBlockState lvt_11_1_ = primer.getBlockState(lvt_10_1_);

                    if (lvt_11_1_.getBlock().getMaterial() != Material.air)
                    {
                        int lvt_12_1_ = lvt_9_1_ >> 4;

                        if (this.storageArrays[lvt_12_1_] == null)
                        {
                            this.storageArrays[lvt_12_1_] = new ExtendedBlockStorage(lvt_12_1_ << 4, lvt_6_1_);
                        }

                        this.storageArrays[lvt_12_1_].set(lvt_7_1_, lvt_9_1_ & 15, lvt_8_1_, lvt_11_1_);
                    }
                }
            }
        }
    }

    /**
     * Checks whether the chunk is at the X/Z location specified
     */
    public boolean isAtLocation(int x, int z)
    {
        return x == this.xPosition && z == this.zPosition;
    }

    public int getHeight(BlockPos pos)
    {
        return this.getHeightValue(pos.getX() & 15, pos.getZ() & 15);
    }

    /**
     * Returns the value in the height map at this x, z coordinate in the chunk
     */
    public int getHeightValue(int x, int z)
    {
        return this.heightMap[z << 4 | x];
    }

    /**
     * Returns the topmost ExtendedBlockStorage instance for this Chunk that actually contains a block.
     */
    public int getTopFilledSegment()
    {
        for (int lvt_1_1_ = this.storageArrays.length - 1; lvt_1_1_ >= 0; --lvt_1_1_)
        {
            if (this.storageArrays[lvt_1_1_] != null)
            {
                return this.storageArrays[lvt_1_1_].getYLocation();
            }
        }

        return 0;
    }

    /**
     * Returns the ExtendedBlockStorage array for this Chunk.
     */
    public ExtendedBlockStorage[] getBlockStorageArray()
    {
        return this.storageArrays;
    }

    /**
     * Generates the height map for a chunk from scratch
     */
    protected void generateHeightMap()
    {
        int lvt_1_1_ = this.getTopFilledSegment();
        this.heightMapMinimum = Integer.MAX_VALUE;

        for (int lvt_2_1_ = 0; lvt_2_1_ < 16; ++lvt_2_1_)
        {
            for (int lvt_3_1_ = 0; lvt_3_1_ < 16; ++lvt_3_1_)
            {
                this.precipitationHeightMap[lvt_2_1_ + (lvt_3_1_ << 4)] = -999;

                for (int lvt_4_1_ = lvt_1_1_ + 16; lvt_4_1_ > 0; --lvt_4_1_)
                {
                    Block lvt_5_1_ = this.getBlock0(lvt_2_1_, lvt_4_1_ - 1, lvt_3_1_);

                    if (lvt_5_1_.getLightOpacity() != 0)
                    {
                        this.heightMap[lvt_3_1_ << 4 | lvt_2_1_] = lvt_4_1_;

                        if (lvt_4_1_ < this.heightMapMinimum)
                        {
                            this.heightMapMinimum = lvt_4_1_;
                        }

                        break;
                    }
                }
            }
        }

        this.isModified = true;
    }

    /**
     * Generates the initial skylight map for the chunk upon generation or load.
     */
    public void generateSkylightMap()
    {
        int lvt_1_1_ = this.getTopFilledSegment();
        this.heightMapMinimum = Integer.MAX_VALUE;

        for (int lvt_2_1_ = 0; lvt_2_1_ < 16; ++lvt_2_1_)
        {
            for (int lvt_3_1_ = 0; lvt_3_1_ < 16; ++lvt_3_1_)
            {
                this.precipitationHeightMap[lvt_2_1_ + (lvt_3_1_ << 4)] = -999;

                for (int lvt_4_1_ = lvt_1_1_ + 16; lvt_4_1_ > 0; --lvt_4_1_)
                {
                    if (this.getBlockLightOpacity(lvt_2_1_, lvt_4_1_ - 1, lvt_3_1_) != 0)
                    {
                        this.heightMap[lvt_3_1_ << 4 | lvt_2_1_] = lvt_4_1_;

                        if (lvt_4_1_ < this.heightMapMinimum)
                        {
                            this.heightMapMinimum = lvt_4_1_;
                        }

                        break;
                    }
                }

                if (!this.worldObj.provider.getHasNoSky())
                {
                    int lvt_4_2_ = 15;
                    int lvt_5_1_ = lvt_1_1_ + 16 - 1;

                    while (true)
                    {
                        int lvt_6_1_ = this.getBlockLightOpacity(lvt_2_1_, lvt_5_1_, lvt_3_1_);

                        if (lvt_6_1_ == 0 && lvt_4_2_ != 15)
                        {
                            lvt_6_1_ = 1;
                        }

                        lvt_4_2_ -= lvt_6_1_;

                        if (lvt_4_2_ > 0)
                        {
                            ExtendedBlockStorage lvt_7_1_ = this.storageArrays[lvt_5_1_ >> 4];

                            if (lvt_7_1_ != null)
                            {
                                lvt_7_1_.setExtSkylightValue(lvt_2_1_, lvt_5_1_ & 15, lvt_3_1_, lvt_4_2_);
                                this.worldObj.notifyLightSet(new BlockPos((this.xPosition << 4) + lvt_2_1_, lvt_5_1_, (this.zPosition << 4) + lvt_3_1_));
                            }
                        }

                        --lvt_5_1_;

                        if (lvt_5_1_ <= 0 || lvt_4_2_ <= 0)
                        {
                            break;
                        }
                    }
                }
            }
        }

        this.isModified = true;
    }

    /**
     * Propagates a given sky-visible block's light value downward and upward to neighboring blocks as necessary.
     */
    private void propagateSkylightOcclusion(int x, int z)
    {
        this.updateSkylightColumns[x + z * 16] = true;
        this.isGapLightingUpdated = true;
    }

    private void recheckGaps(boolean p_150803_1_)
    {
        this.worldObj.theProfiler.startSection("recheckGaps");

        if (this.worldObj.isAreaLoaded(new BlockPos(this.xPosition * 16 + 8, 0, this.zPosition * 16 + 8), 16))
        {
            for (int lvt_2_1_ = 0; lvt_2_1_ < 16; ++lvt_2_1_)
            {
                for (int lvt_3_1_ = 0; lvt_3_1_ < 16; ++lvt_3_1_)
                {
                    if (this.updateSkylightColumns[lvt_2_1_ + lvt_3_1_ * 16])
                    {
                        this.updateSkylightColumns[lvt_2_1_ + lvt_3_1_ * 16] = false;
                        int lvt_4_1_ = this.getHeightValue(lvt_2_1_, lvt_3_1_);
                        int lvt_5_1_ = this.xPosition * 16 + lvt_2_1_;
                        int lvt_6_1_ = this.zPosition * 16 + lvt_3_1_;
                        int lvt_7_1_ = Integer.MAX_VALUE;

                        for (EnumFacing lvt_9_1_ : EnumFacing.Plane.HORIZONTAL)
                        {
                            lvt_7_1_ = Math.min(lvt_7_1_, this.worldObj.getChunksLowestHorizon(lvt_5_1_ + lvt_9_1_.getFrontOffsetX(), lvt_6_1_ + lvt_9_1_.getFrontOffsetZ()));
                        }

                        this.checkSkylightNeighborHeight(lvt_5_1_, lvt_6_1_, lvt_7_1_);

                        for (EnumFacing lvt_9_2_ : EnumFacing.Plane.HORIZONTAL)
                        {
                            this.checkSkylightNeighborHeight(lvt_5_1_ + lvt_9_2_.getFrontOffsetX(), lvt_6_1_ + lvt_9_2_.getFrontOffsetZ(), lvt_4_1_);
                        }

                        if (p_150803_1_)
                        {
                            this.worldObj.theProfiler.endSection();
                            return;
                        }
                    }
                }
            }

            this.isGapLightingUpdated = false;
        }

        this.worldObj.theProfiler.endSection();
    }

    /**
     * Checks the height of a block next to a sky-visible block and schedules a lighting update as necessary.
     */
    private void checkSkylightNeighborHeight(int x, int z, int maxValue)
    {
        int lvt_4_1_ = this.worldObj.getHeight(new BlockPos(x, 0, z)).getY();

        if (lvt_4_1_ > maxValue)
        {
            this.updateSkylightNeighborHeight(x, z, maxValue, lvt_4_1_ + 1);
        }
        else if (lvt_4_1_ < maxValue)
        {
            this.updateSkylightNeighborHeight(x, z, lvt_4_1_, maxValue + 1);
        }
    }

    private void updateSkylightNeighborHeight(int x, int z, int startY, int endY)
    {
        if (endY > startY && this.worldObj.isAreaLoaded(new BlockPos(x, 0, z), 16))
        {
            for (int lvt_5_1_ = startY; lvt_5_1_ < endY; ++lvt_5_1_)
            {
                this.worldObj.checkLightFor(EnumSkyBlock.SKY, new BlockPos(x, lvt_5_1_, z));
            }

            this.isModified = true;
        }
    }

    /**
     * Initiates the recalculation of both the block-light and sky-light for a given block inside a chunk.
     */
    private void relightBlock(int x, int y, int z)
    {
        int lvt_4_1_ = this.heightMap[z << 4 | x] & 255;
        int lvt_5_1_ = lvt_4_1_;

        if (y > lvt_4_1_)
        {
            lvt_5_1_ = y;
        }

        while (lvt_5_1_ > 0 && this.getBlockLightOpacity(x, lvt_5_1_ - 1, z) == 0)
        {
            --lvt_5_1_;
        }

        if (lvt_5_1_ != lvt_4_1_)
        {
            this.worldObj.markBlocksDirtyVertical(x + this.xPosition * 16, z + this.zPosition * 16, lvt_5_1_, lvt_4_1_);
            this.heightMap[z << 4 | x] = lvt_5_1_;
            int lvt_6_1_ = this.xPosition * 16 + x;
            int lvt_7_1_ = this.zPosition * 16 + z;

            if (!this.worldObj.provider.getHasNoSky())
            {
                if (lvt_5_1_ < lvt_4_1_)
                {
                    for (int lvt_8_1_ = lvt_5_1_; lvt_8_1_ < lvt_4_1_; ++lvt_8_1_)
                    {
                        ExtendedBlockStorage lvt_9_1_ = this.storageArrays[lvt_8_1_ >> 4];

                        if (lvt_9_1_ != null)
                        {
                            lvt_9_1_.setExtSkylightValue(x, lvt_8_1_ & 15, z, 15);
                            this.worldObj.notifyLightSet(new BlockPos((this.xPosition << 4) + x, lvt_8_1_, (this.zPosition << 4) + z));
                        }
                    }
                }
                else
                {
                    for (int lvt_8_2_ = lvt_4_1_; lvt_8_2_ < lvt_5_1_; ++lvt_8_2_)
                    {
                        ExtendedBlockStorage lvt_9_2_ = this.storageArrays[lvt_8_2_ >> 4];

                        if (lvt_9_2_ != null)
                        {
                            lvt_9_2_.setExtSkylightValue(x, lvt_8_2_ & 15, z, 0);
                            this.worldObj.notifyLightSet(new BlockPos((this.xPosition << 4) + x, lvt_8_2_, (this.zPosition << 4) + z));
                        }
                    }
                }

                int lvt_8_3_ = 15;

                while (lvt_5_1_ > 0 && lvt_8_3_ > 0)
                {
                    --lvt_5_1_;
                    int lvt_9_3_ = this.getBlockLightOpacity(x, lvt_5_1_, z);

                    if (lvt_9_3_ == 0)
                    {
                        lvt_9_3_ = 1;
                    }

                    lvt_8_3_ -= lvt_9_3_;

                    if (lvt_8_3_ < 0)
                    {
                        lvt_8_3_ = 0;
                    }

                    ExtendedBlockStorage lvt_10_1_ = this.storageArrays[lvt_5_1_ >> 4];

                    if (lvt_10_1_ != null)
                    {
                        lvt_10_1_.setExtSkylightValue(x, lvt_5_1_ & 15, z, lvt_8_3_);
                    }
                }
            }

            int lvt_8_4_ = this.heightMap[z << 4 | x];
            int lvt_9_4_ = lvt_4_1_;
            int lvt_10_2_ = lvt_8_4_;

            if (lvt_8_4_ < lvt_4_1_)
            {
                lvt_9_4_ = lvt_8_4_;
                lvt_10_2_ = lvt_4_1_;
            }

            if (lvt_8_4_ < this.heightMapMinimum)
            {
                this.heightMapMinimum = lvt_8_4_;
            }

            if (!this.worldObj.provider.getHasNoSky())
            {
                for (EnumFacing lvt_12_1_ : EnumFacing.Plane.HORIZONTAL)
                {
                    this.updateSkylightNeighborHeight(lvt_6_1_ + lvt_12_1_.getFrontOffsetX(), lvt_7_1_ + lvt_12_1_.getFrontOffsetZ(), lvt_9_4_, lvt_10_2_);
                }

                this.updateSkylightNeighborHeight(lvt_6_1_, lvt_7_1_, lvt_9_4_, lvt_10_2_);
            }

            this.isModified = true;
        }
    }

    public int getBlockLightOpacity(BlockPos pos)
    {
        return this.getBlock(pos).getLightOpacity();
    }

    private int getBlockLightOpacity(int x, int y, int z)
    {
        return this.getBlock0(x, y, z).getLightOpacity();
    }

    /**
     * Returns the block corresponding to the given coordinates inside a chunk.
     */
    private Block getBlock0(int x, int y, int z)
    {
        Block lvt_4_1_ = Blocks.air;

        if (y >= 0 && y >> 4 < this.storageArrays.length)
        {
            ExtendedBlockStorage lvt_5_1_ = this.storageArrays[y >> 4];

            if (lvt_5_1_ != null)
            {
                try
                {
                    lvt_4_1_ = lvt_5_1_.getBlockByExtId(x, y & 15, z);
                }
                catch (Throwable var8)
                {
                    CrashReport lvt_7_1_ = CrashReport.makeCrashReport(var8, "Getting block");
                    throw new ReportedException(lvt_7_1_);
                }
            }
        }

        return lvt_4_1_;
    }

    public Block getBlock(final int x, final int y, final int z)
    {
        try
        {
            return this.getBlock0(x & 15, y, z & 15);
        }
        catch (ReportedException var6)
        {
            CrashReportCategory lvt_5_1_ = var6.getCrashReport().makeCategory("Block being got");
            lvt_5_1_.addCrashSectionCallable("Location", new Callable<String>()
            {
                public String call() throws Exception
                {
                    return CrashReportCategory.getCoordinateInfo(new BlockPos(Chunk.this.xPosition * 16 + x, y, Chunk.this.zPosition * 16 + z));
                }
                public Object call() throws Exception
                {
                    return this.call();
                }
            });
            throw var6;
        }
    }

    public Block getBlock(final BlockPos pos)
    {
        try
        {
            return this.getBlock0(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
        }
        catch (ReportedException var4)
        {
            CrashReportCategory lvt_3_1_ = var4.getCrashReport().makeCategory("Block being got");
            lvt_3_1_.addCrashSectionCallable("Location", new Callable<String>()
            {
                public String call() throws Exception
                {
                    return CrashReportCategory.getCoordinateInfo(pos);
                }
                public Object call() throws Exception
                {
                    return this.call();
                }
            });
            throw var4;
        }
    }

    public IBlockState getBlockState(final BlockPos pos)
    {
        if (this.worldObj.getWorldType() == WorldType.DEBUG_WORLD)
        {
            IBlockState lvt_2_1_ = null;

            if (pos.getY() == 60)
            {
                lvt_2_1_ = Blocks.barrier.getDefaultState();
            }

            if (pos.getY() == 70)
            {
                lvt_2_1_ = ChunkProviderDebug.func_177461_b(pos.getX(), pos.getZ());
            }

            return lvt_2_1_ == null ? Blocks.air.getDefaultState() : lvt_2_1_;
        }
        else
        {
            try
            {
                if (pos.getY() >= 0 && pos.getY() >> 4 < this.storageArrays.length)
                {
                    ExtendedBlockStorage lvt_2_2_ = this.storageArrays[pos.getY() >> 4];

                    if (lvt_2_2_ != null)
                    {
                        int lvt_3_1_ = pos.getX() & 15;
                        int lvt_4_1_ = pos.getY() & 15;
                        int lvt_5_1_ = pos.getZ() & 15;
                        return lvt_2_2_.get(lvt_3_1_, lvt_4_1_, lvt_5_1_);
                    }
                }

                return Blocks.air.getDefaultState();
            }
            catch (Throwable var6)
            {
                CrashReport lvt_3_2_ = CrashReport.makeCrashReport(var6, "Getting block state");
                CrashReportCategory lvt_4_2_ = lvt_3_2_.makeCategory("Block being got");
                lvt_4_2_.addCrashSectionCallable("Location", new Callable<String>()
                {
                    public String call() throws Exception
                    {
                        return CrashReportCategory.getCoordinateInfo(pos);
                    }
                    public Object call() throws Exception
                    {
                        return this.call();
                    }
                });
                throw new ReportedException(lvt_3_2_);
            }
        }
    }

    /**
     * Return the metadata corresponding to the given coordinates inside a chunk.
     */
    private int getBlockMetadata(int x, int y, int z)
    {
        if (y >> 4 >= this.storageArrays.length)
        {
            return 0;
        }
        else
        {
            ExtendedBlockStorage lvt_4_1_ = this.storageArrays[y >> 4];
            return lvt_4_1_ != null ? lvt_4_1_.getExtBlockMetadata(x, y & 15, z) : 0;
        }
    }

    public int getBlockMetadata(BlockPos pos)
    {
        return this.getBlockMetadata(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
    }

    public IBlockState setBlockState(BlockPos pos, IBlockState state)
    {
        int lvt_3_1_ = pos.getX() & 15;
        int lvt_4_1_ = pos.getY();
        int lvt_5_1_ = pos.getZ() & 15;
        int lvt_6_1_ = lvt_5_1_ << 4 | lvt_3_1_;

        if (lvt_4_1_ >= this.precipitationHeightMap[lvt_6_1_] - 1)
        {
            this.precipitationHeightMap[lvt_6_1_] = -999;
        }

        int lvt_7_1_ = this.heightMap[lvt_6_1_];
        IBlockState lvt_8_1_ = this.getBlockState(pos);

        if (lvt_8_1_ == state)
        {
            return null;
        }
        else
        {
            Block lvt_9_1_ = state.getBlock();
            Block lvt_10_1_ = lvt_8_1_.getBlock();
            ExtendedBlockStorage lvt_11_1_ = this.storageArrays[lvt_4_1_ >> 4];
            boolean lvt_12_1_ = false;

            if (lvt_11_1_ == null)
            {
                if (lvt_9_1_ == Blocks.air)
                {
                    return null;
                }

                lvt_11_1_ = this.storageArrays[lvt_4_1_ >> 4] = new ExtendedBlockStorage(lvt_4_1_ >> 4 << 4, !this.worldObj.provider.getHasNoSky());
                lvt_12_1_ = lvt_4_1_ >= lvt_7_1_;
            }

            lvt_11_1_.set(lvt_3_1_, lvt_4_1_ & 15, lvt_5_1_, state);

            if (lvt_10_1_ != lvt_9_1_)
            {
                if (!this.worldObj.isRemote)
                {
                    lvt_10_1_.breakBlock(this.worldObj, pos, lvt_8_1_);
                }
                else if (lvt_10_1_ instanceof ITileEntityProvider)
                {
                    this.worldObj.removeTileEntity(pos);
                }
            }

            if (lvt_11_1_.getBlockByExtId(lvt_3_1_, lvt_4_1_ & 15, lvt_5_1_) != lvt_9_1_)
            {
                return null;
            }
            else
            {
                if (lvt_12_1_)
                {
                    this.generateSkylightMap();
                }
                else
                {
                    int lvt_13_1_ = lvt_9_1_.getLightOpacity();
                    int lvt_14_1_ = lvt_10_1_.getLightOpacity();

                    if (lvt_13_1_ > 0)
                    {
                        if (lvt_4_1_ >= lvt_7_1_)
                        {
                            this.relightBlock(lvt_3_1_, lvt_4_1_ + 1, lvt_5_1_);
                        }
                    }
                    else if (lvt_4_1_ == lvt_7_1_ - 1)
                    {
                        this.relightBlock(lvt_3_1_, lvt_4_1_, lvt_5_1_);
                    }

                    if (lvt_13_1_ != lvt_14_1_ && (lvt_13_1_ < lvt_14_1_ || this.getLightFor(EnumSkyBlock.SKY, pos) > 0 || this.getLightFor(EnumSkyBlock.BLOCK, pos) > 0))
                    {
                        this.propagateSkylightOcclusion(lvt_3_1_, lvt_5_1_);
                    }
                }

                if (lvt_10_1_ instanceof ITileEntityProvider)
                {
                    TileEntity lvt_13_2_ = this.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);

                    if (lvt_13_2_ != null)
                    {
                        lvt_13_2_.updateContainingBlockInfo();
                    }
                }

                if (!this.worldObj.isRemote && lvt_10_1_ != lvt_9_1_)
                {
                    lvt_9_1_.onBlockAdded(this.worldObj, pos, state);
                }

                if (lvt_9_1_ instanceof ITileEntityProvider)
                {
                    TileEntity lvt_13_3_ = this.getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);

                    if (lvt_13_3_ == null)
                    {
                        lvt_13_3_ = ((ITileEntityProvider)lvt_9_1_).createNewTileEntity(this.worldObj, lvt_9_1_.getMetaFromState(state));
                        this.worldObj.setTileEntity(pos, lvt_13_3_);
                    }

                    if (lvt_13_3_ != null)
                    {
                        lvt_13_3_.updateContainingBlockInfo();
                    }
                }

                this.isModified = true;
                return lvt_8_1_;
            }
        }
    }

    public int getLightFor(EnumSkyBlock p_177413_1_, BlockPos pos)
    {
        int lvt_3_1_ = pos.getX() & 15;
        int lvt_4_1_ = pos.getY();
        int lvt_5_1_ = pos.getZ() & 15;
        ExtendedBlockStorage lvt_6_1_ = this.storageArrays[lvt_4_1_ >> 4];
        return lvt_6_1_ == null ? (this.canSeeSky(pos) ? p_177413_1_.defaultLightValue : 0) : (p_177413_1_ == EnumSkyBlock.SKY ? (this.worldObj.provider.getHasNoSky() ? 0 : lvt_6_1_.getExtSkylightValue(lvt_3_1_, lvt_4_1_ & 15, lvt_5_1_)) : (p_177413_1_ == EnumSkyBlock.BLOCK ? lvt_6_1_.getExtBlocklightValue(lvt_3_1_, lvt_4_1_ & 15, lvt_5_1_) : p_177413_1_.defaultLightValue));
    }

    public void setLightFor(EnumSkyBlock p_177431_1_, BlockPos pos, int value)
    {
        int lvt_4_1_ = pos.getX() & 15;
        int lvt_5_1_ = pos.getY();
        int lvt_6_1_ = pos.getZ() & 15;
        ExtendedBlockStorage lvt_7_1_ = this.storageArrays[lvt_5_1_ >> 4];

        if (lvt_7_1_ == null)
        {
            lvt_7_1_ = this.storageArrays[lvt_5_1_ >> 4] = new ExtendedBlockStorage(lvt_5_1_ >> 4 << 4, !this.worldObj.provider.getHasNoSky());
            this.generateSkylightMap();
        }

        this.isModified = true;

        if (p_177431_1_ == EnumSkyBlock.SKY)
        {
            if (!this.worldObj.provider.getHasNoSky())
            {
                lvt_7_1_.setExtSkylightValue(lvt_4_1_, lvt_5_1_ & 15, lvt_6_1_, value);
            }
        }
        else if (p_177431_1_ == EnumSkyBlock.BLOCK)
        {
            lvt_7_1_.setExtBlocklightValue(lvt_4_1_, lvt_5_1_ & 15, lvt_6_1_, value);
        }
    }

    public int getLightSubtracted(BlockPos pos, int amount)
    {
        int lvt_3_1_ = pos.getX() & 15;
        int lvt_4_1_ = pos.getY();
        int lvt_5_1_ = pos.getZ() & 15;
        ExtendedBlockStorage lvt_6_1_ = this.storageArrays[lvt_4_1_ >> 4];

        if (lvt_6_1_ == null)
        {
            return !this.worldObj.provider.getHasNoSky() && amount < EnumSkyBlock.SKY.defaultLightValue ? EnumSkyBlock.SKY.defaultLightValue - amount : 0;
        }
        else
        {
            int lvt_7_1_ = this.worldObj.provider.getHasNoSky() ? 0 : lvt_6_1_.getExtSkylightValue(lvt_3_1_, lvt_4_1_ & 15, lvt_5_1_);
            lvt_7_1_ = lvt_7_1_ - amount;
            int lvt_8_1_ = lvt_6_1_.getExtBlocklightValue(lvt_3_1_, lvt_4_1_ & 15, lvt_5_1_);

            if (lvt_8_1_ > lvt_7_1_)
            {
                lvt_7_1_ = lvt_8_1_;
            }

            return lvt_7_1_;
        }
    }

    /**
     * Adds an entity to the chunk. Args: entity
     */
    public void addEntity(Entity entityIn)
    {
        this.hasEntities = true;
        int lvt_2_1_ = MathHelper.floor_double(entityIn.posX / 16.0D);
        int lvt_3_1_ = MathHelper.floor_double(entityIn.posZ / 16.0D);

        if (lvt_2_1_ != this.xPosition || lvt_3_1_ != this.zPosition)
        {
            logger.warn("Wrong location! (" + lvt_2_1_ + ", " + lvt_3_1_ + ") should be (" + this.xPosition + ", " + this.zPosition + "), " + entityIn, new Object[] {entityIn});
            entityIn.setDead();
        }

        int lvt_4_1_ = MathHelper.floor_double(entityIn.posY / 16.0D);

        if (lvt_4_1_ < 0)
        {
            lvt_4_1_ = 0;
        }

        if (lvt_4_1_ >= this.entityLists.length)
        {
            lvt_4_1_ = this.entityLists.length - 1;
        }

        entityIn.addedToChunk = true;
        entityIn.chunkCoordX = this.xPosition;
        entityIn.chunkCoordY = lvt_4_1_;
        entityIn.chunkCoordZ = this.zPosition;
        this.entityLists[lvt_4_1_].add(entityIn);
    }

    /**
     * removes entity using its y chunk coordinate as its index
     */
    public void removeEntity(Entity entityIn)
    {
        this.removeEntityAtIndex(entityIn, entityIn.chunkCoordY);
    }

    /**
     * Removes entity at the specified index from the entity array.
     */
    public void removeEntityAtIndex(Entity entityIn, int p_76608_2_)
    {
        if (p_76608_2_ < 0)
        {
            p_76608_2_ = 0;
        }

        if (p_76608_2_ >= this.entityLists.length)
        {
            p_76608_2_ = this.entityLists.length - 1;
        }

        this.entityLists[p_76608_2_].remove(entityIn);
    }

    public boolean canSeeSky(BlockPos pos)
    {
        int lvt_2_1_ = pos.getX() & 15;
        int lvt_3_1_ = pos.getY();
        int lvt_4_1_ = pos.getZ() & 15;
        return lvt_3_1_ >= this.heightMap[lvt_4_1_ << 4 | lvt_2_1_];
    }

    private TileEntity createNewTileEntity(BlockPos pos)
    {
        Block lvt_2_1_ = this.getBlock(pos);
        return !lvt_2_1_.hasTileEntity() ? null : ((ITileEntityProvider)lvt_2_1_).createNewTileEntity(this.worldObj, this.getBlockMetadata(pos));
    }

    public TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType p_177424_2_)
    {
        TileEntity lvt_3_1_ = (TileEntity)this.chunkTileEntityMap.get(pos);

        if (lvt_3_1_ == null)
        {
            if (p_177424_2_ == Chunk.EnumCreateEntityType.IMMEDIATE)
            {
                lvt_3_1_ = this.createNewTileEntity(pos);
                this.worldObj.setTileEntity(pos, lvt_3_1_);
            }
            else if (p_177424_2_ == Chunk.EnumCreateEntityType.QUEUED)
            {
                this.tileEntityPosQueue.add(pos);
            }
        }
        else if (lvt_3_1_.isInvalid())
        {
            this.chunkTileEntityMap.remove(pos);
            return null;
        }

        return lvt_3_1_;
    }

    public void addTileEntity(TileEntity tileEntityIn)
    {
        this.addTileEntity(tileEntityIn.getPos(), tileEntityIn);

        if (this.isChunkLoaded)
        {
            this.worldObj.addTileEntity(tileEntityIn);
        }
    }

    public void addTileEntity(BlockPos pos, TileEntity tileEntityIn)
    {
        tileEntityIn.setWorldObj(this.worldObj);
        tileEntityIn.setPos(pos);

        if (this.getBlock(pos) instanceof ITileEntityProvider)
        {
            if (this.chunkTileEntityMap.containsKey(pos))
            {
                ((TileEntity)this.chunkTileEntityMap.get(pos)).invalidate();
            }

            tileEntityIn.validate();
            this.chunkTileEntityMap.put(pos, tileEntityIn);
        }
    }

    public void removeTileEntity(BlockPos pos)
    {
        if (this.isChunkLoaded)
        {
            TileEntity lvt_2_1_ = (TileEntity)this.chunkTileEntityMap.remove(pos);

            if (lvt_2_1_ != null)
            {
                lvt_2_1_.invalidate();
            }
        }
    }

    /**
     * Called when this Chunk is loaded by the ChunkProvider
     */
    public void onChunkLoad()
    {
        this.isChunkLoaded = true;
        this.worldObj.addTileEntities(this.chunkTileEntityMap.values());

        for (int lvt_1_1_ = 0; lvt_1_1_ < this.entityLists.length; ++lvt_1_1_)
        {
            for (Entity lvt_3_1_ : this.entityLists[lvt_1_1_])
            {
                lvt_3_1_.onChunkLoad();
            }

            this.worldObj.loadEntities(this.entityLists[lvt_1_1_]);
        }
    }

    /**
     * Called when this Chunk is unloaded by the ChunkProvider
     */
    public void onChunkUnload()
    {
        this.isChunkLoaded = false;

        for (TileEntity lvt_2_1_ : this.chunkTileEntityMap.values())
        {
            this.worldObj.markTileEntityForRemoval(lvt_2_1_);
        }

        for (int lvt_1_2_ = 0; lvt_1_2_ < this.entityLists.length; ++lvt_1_2_)
        {
            this.worldObj.unloadEntities(this.entityLists[lvt_1_2_]);
        }
    }

    /**
     * Sets the isModified flag for this Chunk
     */
    public void setChunkModified()
    {
        this.isModified = true;
    }

    /**
     * Fills the given list of all entities that intersect within the given bounding box that aren't the passed entity.
     */
    public void getEntitiesWithinAABBForEntity(Entity entityIn, AxisAlignedBB aabb, List<Entity> listToFill, Predicate <? super Entity > p_177414_4_)
    {
        int lvt_5_1_ = MathHelper.floor_double((aabb.minY - 2.0D) / 16.0D);
        int lvt_6_1_ = MathHelper.floor_double((aabb.maxY + 2.0D) / 16.0D);
        lvt_5_1_ = MathHelper.clamp_int(lvt_5_1_, 0, this.entityLists.length - 1);
        lvt_6_1_ = MathHelper.clamp_int(lvt_6_1_, 0, this.entityLists.length - 1);

        for (int lvt_7_1_ = lvt_5_1_; lvt_7_1_ <= lvt_6_1_; ++lvt_7_1_)
        {
            if (!this.entityLists[lvt_7_1_].isEmpty())
            {
                for (Entity lvt_9_1_ : this.entityLists[lvt_7_1_])
                {
                    if (lvt_9_1_.getEntityBoundingBox().intersectsWith(aabb) && lvt_9_1_ != entityIn)
                    {
                        if (p_177414_4_ == null || p_177414_4_.apply(lvt_9_1_))
                        {
                            listToFill.add(lvt_9_1_);
                        }

                        Entity[] lvt_10_1_ = lvt_9_1_.getParts();

                        if (lvt_10_1_ != null)
                        {
                            for (int lvt_11_1_ = 0; lvt_11_1_ < lvt_10_1_.length; ++lvt_11_1_)
                            {
                                lvt_9_1_ = lvt_10_1_[lvt_11_1_];

                                if (lvt_9_1_ != entityIn && lvt_9_1_.getEntityBoundingBox().intersectsWith(aabb) && (p_177414_4_ == null || p_177414_4_.apply(lvt_9_1_)))
                                {
                                    listToFill.add(lvt_9_1_);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public <T extends Entity> void getEntitiesOfTypeWithinAAAB(Class <? extends T > entityClass, AxisAlignedBB aabb, List<T> listToFill, Predicate <? super T > p_177430_4_)
    {
        int lvt_5_1_ = MathHelper.floor_double((aabb.minY - 2.0D) / 16.0D);
        int lvt_6_1_ = MathHelper.floor_double((aabb.maxY + 2.0D) / 16.0D);
        lvt_5_1_ = MathHelper.clamp_int(lvt_5_1_, 0, this.entityLists.length - 1);
        lvt_6_1_ = MathHelper.clamp_int(lvt_6_1_, 0, this.entityLists.length - 1);

        for (int lvt_7_1_ = lvt_5_1_; lvt_7_1_ <= lvt_6_1_; ++lvt_7_1_)
        {
            for (T lvt_9_1_ : this.entityLists[lvt_7_1_].getByClass(entityClass))
            {
                if (lvt_9_1_.getEntityBoundingBox().intersectsWith(aabb) && (p_177430_4_ == null || p_177430_4_.apply(lvt_9_1_)))
                {
                    listToFill.add(lvt_9_1_);
                }
            }
        }
    }

    /**
     * Returns true if this Chunk needs to be saved
     */
    public boolean needsSaving(boolean p_76601_1_)
    {
        if (p_76601_1_)
        {
            if (this.hasEntities && this.worldObj.getTotalWorldTime() != this.lastSaveTime || this.isModified)
            {
                return true;
            }
        }
        else if (this.hasEntities && this.worldObj.getTotalWorldTime() >= this.lastSaveTime + 600L)
        {
            return true;
        }

        return this.isModified;
    }

    public Random getRandomWithSeed(long seed)
    {
        return new Random(this.worldObj.getSeed() + (long)(this.xPosition * this.xPosition * 4987142) + (long)(this.xPosition * 5947611) + (long)(this.zPosition * this.zPosition) * 4392871L + (long)(this.zPosition * 389711) ^ seed);
    }

    public boolean isEmpty()
    {
        return false;
    }

    public void populateChunk(IChunkProvider p_76624_1_, IChunkProvider p_76624_2_, int x, int z)
    {
        boolean lvt_5_1_ = p_76624_1_.chunkExists(x, z - 1);
        boolean lvt_6_1_ = p_76624_1_.chunkExists(x + 1, z);
        boolean lvt_7_1_ = p_76624_1_.chunkExists(x, z + 1);
        boolean lvt_8_1_ = p_76624_1_.chunkExists(x - 1, z);
        boolean lvt_9_1_ = p_76624_1_.chunkExists(x - 1, z - 1);
        boolean lvt_10_1_ = p_76624_1_.chunkExists(x + 1, z + 1);
        boolean lvt_11_1_ = p_76624_1_.chunkExists(x - 1, z + 1);
        boolean lvt_12_1_ = p_76624_1_.chunkExists(x + 1, z - 1);

        if (lvt_6_1_ && lvt_7_1_ && lvt_10_1_)
        {
            if (!this.isTerrainPopulated)
            {
                p_76624_1_.populate(p_76624_2_, x, z);
            }
            else
            {
                p_76624_1_.populateChunk(p_76624_2_, this, x, z);
            }
        }

        if (lvt_8_1_ && lvt_7_1_ && lvt_11_1_)
        {
            Chunk lvt_13_1_ = p_76624_1_.provideChunk(x - 1, z);

            if (!lvt_13_1_.isTerrainPopulated)
            {
                p_76624_1_.populate(p_76624_2_, x - 1, z);
            }
            else
            {
                p_76624_1_.populateChunk(p_76624_2_, lvt_13_1_, x - 1, z);
            }
        }

        if (lvt_5_1_ && lvt_6_1_ && lvt_12_1_)
        {
            Chunk lvt_13_2_ = p_76624_1_.provideChunk(x, z - 1);

            if (!lvt_13_2_.isTerrainPopulated)
            {
                p_76624_1_.populate(p_76624_2_, x, z - 1);
            }
            else
            {
                p_76624_1_.populateChunk(p_76624_2_, lvt_13_2_, x, z - 1);
            }
        }

        if (lvt_9_1_ && lvt_5_1_ && lvt_8_1_)
        {
            Chunk lvt_13_3_ = p_76624_1_.provideChunk(x - 1, z - 1);

            if (!lvt_13_3_.isTerrainPopulated)
            {
                p_76624_1_.populate(p_76624_2_, x - 1, z - 1);
            }
            else
            {
                p_76624_1_.populateChunk(p_76624_2_, lvt_13_3_, x - 1, z - 1);
            }
        }
    }

    public BlockPos getPrecipitationHeight(BlockPos pos)
    {
        int lvt_2_1_ = pos.getX() & 15;
        int lvt_3_1_ = pos.getZ() & 15;
        int lvt_4_1_ = lvt_2_1_ | lvt_3_1_ << 4;
        BlockPos lvt_5_1_ = new BlockPos(pos.getX(), this.precipitationHeightMap[lvt_4_1_], pos.getZ());

        if (lvt_5_1_.getY() == -999)
        {
            int lvt_6_1_ = this.getTopFilledSegment() + 15;
            lvt_5_1_ = new BlockPos(pos.getX(), lvt_6_1_, pos.getZ());
            int lvt_7_1_ = -1;

            while (lvt_5_1_.getY() > 0 && lvt_7_1_ == -1)
            {
                Block lvt_8_1_ = this.getBlock(lvt_5_1_);
                Material lvt_9_1_ = lvt_8_1_.getMaterial();

                if (!lvt_9_1_.blocksMovement() && !lvt_9_1_.isLiquid())
                {
                    lvt_5_1_ = lvt_5_1_.down();
                }
                else
                {
                    lvt_7_1_ = lvt_5_1_.getY() + 1;
                }
            }

            this.precipitationHeightMap[lvt_4_1_] = lvt_7_1_;
        }

        return new BlockPos(pos.getX(), this.precipitationHeightMap[lvt_4_1_], pos.getZ());
    }

    public void func_150804_b(boolean p_150804_1_)
    {
        if (this.isGapLightingUpdated && !this.worldObj.provider.getHasNoSky() && !p_150804_1_)
        {
            this.recheckGaps(this.worldObj.isRemote);
        }

        this.field_150815_m = true;

        if (!this.isLightPopulated && this.isTerrainPopulated)
        {
            this.func_150809_p();
        }

        while (!this.tileEntityPosQueue.isEmpty())
        {
            BlockPos lvt_2_1_ = (BlockPos)this.tileEntityPosQueue.poll();

            if (this.getTileEntity(lvt_2_1_, Chunk.EnumCreateEntityType.CHECK) == null && this.getBlock(lvt_2_1_).hasTileEntity())
            {
                TileEntity lvt_3_1_ = this.createNewTileEntity(lvt_2_1_);
                this.worldObj.setTileEntity(lvt_2_1_, lvt_3_1_);
                this.worldObj.markBlockRangeForRenderUpdate(lvt_2_1_, lvt_2_1_);
            }
        }
    }

    public boolean isPopulated()
    {
        return this.field_150815_m && this.isTerrainPopulated && this.isLightPopulated;
    }

    /**
     * Gets a ChunkCoordIntPair representing the Chunk's position.
     */
    public ChunkCoordIntPair getChunkCoordIntPair()
    {
        return new ChunkCoordIntPair(this.xPosition, this.zPosition);
    }

    /**
     * Returns whether the ExtendedBlockStorages containing levels (in blocks) from arg 1 to arg 2 are fully empty
     * (true) or not (false).
     */
    public boolean getAreLevelsEmpty(int startY, int endY)
    {
        if (startY < 0)
        {
            startY = 0;
        }

        if (endY >= 256)
        {
            endY = 255;
        }

        for (int lvt_3_1_ = startY; lvt_3_1_ <= endY; lvt_3_1_ += 16)
        {
            ExtendedBlockStorage lvt_4_1_ = this.storageArrays[lvt_3_1_ >> 4];

            if (lvt_4_1_ != null && !lvt_4_1_.isEmpty())
            {
                return false;
            }
        }

        return true;
    }

    public void setStorageArrays(ExtendedBlockStorage[] newStorageArrays)
    {
        if (this.storageArrays.length != newStorageArrays.length)
        {
            logger.warn("Could not set level chunk sections, array length is " + newStorageArrays.length + " instead of " + this.storageArrays.length);
        }
        else
        {
            for (int lvt_2_1_ = 0; lvt_2_1_ < this.storageArrays.length; ++lvt_2_1_)
            {
                this.storageArrays[lvt_2_1_] = newStorageArrays[lvt_2_1_];
            }
        }
    }

    /**
     * Initialize this chunk with new binary data.
     */
    public void fillChunk(byte[] p_177439_1_, int p_177439_2_, boolean p_177439_3_)
    {
        int lvt_4_1_ = 0;
        boolean lvt_5_1_ = !this.worldObj.provider.getHasNoSky();

        for (int lvt_6_1_ = 0; lvt_6_1_ < this.storageArrays.length; ++lvt_6_1_)
        {
            if ((p_177439_2_ & 1 << lvt_6_1_) != 0)
            {
                if (this.storageArrays[lvt_6_1_] == null)
                {
                    this.storageArrays[lvt_6_1_] = new ExtendedBlockStorage(lvt_6_1_ << 4, lvt_5_1_);
                }

                char[] lvt_7_1_ = this.storageArrays[lvt_6_1_].getData();

                for (int lvt_8_1_ = 0; lvt_8_1_ < lvt_7_1_.length; ++lvt_8_1_)
                {
                    lvt_7_1_[lvt_8_1_] = (char)((p_177439_1_[lvt_4_1_ + 1] & 255) << 8 | p_177439_1_[lvt_4_1_] & 255);
                    lvt_4_1_ += 2;
                }
            }
            else if (p_177439_3_ && this.storageArrays[lvt_6_1_] != null)
            {
                this.storageArrays[lvt_6_1_] = null;
            }
        }

        for (int lvt_6_2_ = 0; lvt_6_2_ < this.storageArrays.length; ++lvt_6_2_)
        {
            if ((p_177439_2_ & 1 << lvt_6_2_) != 0 && this.storageArrays[lvt_6_2_] != null)
            {
                NibbleArray lvt_7_2_ = this.storageArrays[lvt_6_2_].getBlocklightArray();
                System.arraycopy(p_177439_1_, lvt_4_1_, lvt_7_2_.getData(), 0, lvt_7_2_.getData().length);
                lvt_4_1_ += lvt_7_2_.getData().length;
            }
        }

        if (lvt_5_1_)
        {
            for (int lvt_6_3_ = 0; lvt_6_3_ < this.storageArrays.length; ++lvt_6_3_)
            {
                if ((p_177439_2_ & 1 << lvt_6_3_) != 0 && this.storageArrays[lvt_6_3_] != null)
                {
                    NibbleArray lvt_7_3_ = this.storageArrays[lvt_6_3_].getSkylightArray();
                    System.arraycopy(p_177439_1_, lvt_4_1_, lvt_7_3_.getData(), 0, lvt_7_3_.getData().length);
                    lvt_4_1_ += lvt_7_3_.getData().length;
                }
            }
        }

        if (p_177439_3_)
        {
            System.arraycopy(p_177439_1_, lvt_4_1_, this.blockBiomeArray, 0, this.blockBiomeArray.length);
            int var10000 = lvt_4_1_ + this.blockBiomeArray.length;
        }

        for (int lvt_6_4_ = 0; lvt_6_4_ < this.storageArrays.length; ++lvt_6_4_)
        {
            if (this.storageArrays[lvt_6_4_] != null && (p_177439_2_ & 1 << lvt_6_4_) != 0)
            {
                this.storageArrays[lvt_6_4_].removeInvalidBlocks();
            }
        }

        this.isLightPopulated = true;
        this.isTerrainPopulated = true;
        this.generateHeightMap();

        for (TileEntity lvt_7_4_ : this.chunkTileEntityMap.values())
        {
            lvt_7_4_.updateContainingBlockInfo();
        }
    }

    public BiomeGenBase getBiome(BlockPos pos, WorldChunkManager chunkManager)
    {
        int lvt_3_1_ = pos.getX() & 15;
        int lvt_4_1_ = pos.getZ() & 15;
        int lvt_5_1_ = this.blockBiomeArray[lvt_4_1_ << 4 | lvt_3_1_] & 255;

        if (lvt_5_1_ == 255)
        {
            BiomeGenBase lvt_6_1_ = chunkManager.getBiomeGenerator(pos, BiomeGenBase.plains);
            lvt_5_1_ = lvt_6_1_.biomeID;
            this.blockBiomeArray[lvt_4_1_ << 4 | lvt_3_1_] = (byte)(lvt_5_1_ & 255);
        }

        BiomeGenBase lvt_6_2_ = BiomeGenBase.getBiome(lvt_5_1_);
        return lvt_6_2_ == null ? BiomeGenBase.plains : lvt_6_2_;
    }

    /**
     * Returns an array containing a 16x16 mapping on the X/Z of block positions in this Chunk to biome IDs.
     */
    public byte[] getBiomeArray()
    {
        return this.blockBiomeArray;
    }

    /**
     * Accepts a 256-entry array that contains a 16x16 mapping on the X/Z plane of block positions in this Chunk to
     * biome IDs.
     */
    public void setBiomeArray(byte[] biomeArray)
    {
        if (this.blockBiomeArray.length != biomeArray.length)
        {
            logger.warn("Could not set level chunk biomes, array length is " + biomeArray.length + " instead of " + this.blockBiomeArray.length);
        }
        else
        {
            for (int lvt_2_1_ = 0; lvt_2_1_ < this.blockBiomeArray.length; ++lvt_2_1_)
            {
                this.blockBiomeArray[lvt_2_1_] = biomeArray[lvt_2_1_];
            }
        }
    }

    /**
     * Resets the relight check index to 0 for this Chunk.
     */
    public void resetRelightChecks()
    {
        this.queuedLightChecks = 0;
    }

    /**
     * Called once-per-chunk-per-tick, and advances the round-robin relight check index by up to 8 blocks at a time. In
     * a worst-case scenario, can potentially take up to 25.6 seconds, calculated via (4096/8)/20, to re-check all
     * blocks in a chunk, which may explain lagging light updates on initial world generation.
     */
    public void enqueueRelightChecks()
    {
        BlockPos lvt_1_1_ = new BlockPos(this.xPosition << 4, 0, this.zPosition << 4);

        for (int lvt_2_1_ = 0; lvt_2_1_ < 8; ++lvt_2_1_)
        {
            if (this.queuedLightChecks >= 4096)
            {
                return;
            }

            int lvt_3_1_ = this.queuedLightChecks % 16;
            int lvt_4_1_ = this.queuedLightChecks / 16 % 16;
            int lvt_5_1_ = this.queuedLightChecks / 256;
            ++this.queuedLightChecks;

            for (int lvt_6_1_ = 0; lvt_6_1_ < 16; ++lvt_6_1_)
            {
                BlockPos lvt_7_1_ = lvt_1_1_.add(lvt_4_1_, (lvt_3_1_ << 4) + lvt_6_1_, lvt_5_1_);
                boolean lvt_8_1_ = lvt_6_1_ == 0 || lvt_6_1_ == 15 || lvt_4_1_ == 0 || lvt_4_1_ == 15 || lvt_5_1_ == 0 || lvt_5_1_ == 15;

                if (this.storageArrays[lvt_3_1_] == null && lvt_8_1_ || this.storageArrays[lvt_3_1_] != null && this.storageArrays[lvt_3_1_].getBlockByExtId(lvt_4_1_, lvt_6_1_, lvt_5_1_).getMaterial() == Material.air)
                {
                    for (EnumFacing lvt_12_1_ : EnumFacing.values())
                    {
                        BlockPos lvt_13_1_ = lvt_7_1_.offset(lvt_12_1_);

                        if (this.worldObj.getBlockState(lvt_13_1_).getBlock().getLightValue() > 0)
                        {
                            this.worldObj.checkLight(lvt_13_1_);
                        }
                    }

                    this.worldObj.checkLight(lvt_7_1_);
                }
            }
        }
    }

    public void func_150809_p()
    {
        this.isTerrainPopulated = true;
        this.isLightPopulated = true;
        BlockPos lvt_1_1_ = new BlockPos(this.xPosition << 4, 0, this.zPosition << 4);

        if (!this.worldObj.provider.getHasNoSky())
        {
            if (this.worldObj.isAreaLoaded(lvt_1_1_.add(-1, 0, -1), lvt_1_1_.add(16, this.worldObj.getSeaLevel(), 16)))
            {
                label92:

                for (int lvt_2_1_ = 0; lvt_2_1_ < 16; ++lvt_2_1_)
                {
                    for (int lvt_3_1_ = 0; lvt_3_1_ < 16; ++lvt_3_1_)
                    {
                        if (!this.func_150811_f(lvt_2_1_, lvt_3_1_))
                        {
                            this.isLightPopulated = false;
                            break label92;
                        }
                    }
                }

                if (this.isLightPopulated)
                {
                    for (EnumFacing lvt_3_2_ : EnumFacing.Plane.HORIZONTAL)
                    {
                        int lvt_4_1_ = lvt_3_2_.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? 16 : 1;
                        this.worldObj.getChunkFromBlockCoords(lvt_1_1_.offset(lvt_3_2_, lvt_4_1_)).func_180700_a(lvt_3_2_.getOpposite());
                    }

                    this.func_177441_y();
                }
            }
            else
            {
                this.isLightPopulated = false;
            }
        }
    }

    private void func_177441_y()
    {
        for (int lvt_1_1_ = 0; lvt_1_1_ < this.updateSkylightColumns.length; ++lvt_1_1_)
        {
            this.updateSkylightColumns[lvt_1_1_] = true;
        }

        this.recheckGaps(false);
    }

    private void func_180700_a(EnumFacing facing)
    {
        if (this.isTerrainPopulated)
        {
            if (facing == EnumFacing.EAST)
            {
                for (int lvt_2_1_ = 0; lvt_2_1_ < 16; ++lvt_2_1_)
                {
                    this.func_150811_f(15, lvt_2_1_);
                }
            }
            else if (facing == EnumFacing.WEST)
            {
                for (int lvt_2_2_ = 0; lvt_2_2_ < 16; ++lvt_2_2_)
                {
                    this.func_150811_f(0, lvt_2_2_);
                }
            }
            else if (facing == EnumFacing.SOUTH)
            {
                for (int lvt_2_3_ = 0; lvt_2_3_ < 16; ++lvt_2_3_)
                {
                    this.func_150811_f(lvt_2_3_, 15);
                }
            }
            else if (facing == EnumFacing.NORTH)
            {
                for (int lvt_2_4_ = 0; lvt_2_4_ < 16; ++lvt_2_4_)
                {
                    this.func_150811_f(lvt_2_4_, 0);
                }
            }
        }
    }

    private boolean func_150811_f(int x, int z)
    {
        int lvt_3_1_ = this.getTopFilledSegment();
        boolean lvt_4_1_ = false;
        boolean lvt_5_1_ = false;
        BlockPos.MutableBlockPos lvt_6_1_ = new BlockPos.MutableBlockPos((this.xPosition << 4) + x, 0, (this.zPosition << 4) + z);

        for (int lvt_7_1_ = lvt_3_1_ + 16 - 1; lvt_7_1_ > this.worldObj.getSeaLevel() || lvt_7_1_ > 0 && !lvt_5_1_; --lvt_7_1_)
        {
            lvt_6_1_.set(lvt_6_1_.getX(), lvt_7_1_, lvt_6_1_.getZ());
            int lvt_8_1_ = this.getBlockLightOpacity(lvt_6_1_);

            if (lvt_8_1_ == 255 && lvt_6_1_.getY() < this.worldObj.getSeaLevel())
            {
                lvt_5_1_ = true;
            }

            if (!lvt_4_1_ && lvt_8_1_ > 0)
            {
                lvt_4_1_ = true;
            }
            else if (lvt_4_1_ && lvt_8_1_ == 0 && !this.worldObj.checkLight(lvt_6_1_))
            {
                return false;
            }
        }

        for (int lvt_7_2_ = lvt_6_1_.getY(); lvt_7_2_ > 0; --lvt_7_2_)
        {
            lvt_6_1_.set(lvt_6_1_.getX(), lvt_7_2_, lvt_6_1_.getZ());

            if (this.getBlock(lvt_6_1_).getLightValue() > 0)
            {
                this.worldObj.checkLight(lvt_6_1_);
            }
        }

        return true;
    }

    public boolean isLoaded()
    {
        return this.isChunkLoaded;
    }

    public void setChunkLoaded(boolean loaded)
    {
        this.isChunkLoaded = loaded;
    }

    public World getWorld()
    {
        return this.worldObj;
    }

    public int[] getHeightMap()
    {
        return this.heightMap;
    }

    public void setHeightMap(int[] newHeightMap)
    {
        if (this.heightMap.length != newHeightMap.length)
        {
            logger.warn("Could not set level chunk heightmap, array length is " + newHeightMap.length + " instead of " + this.heightMap.length);
        }
        else
        {
            for (int lvt_2_1_ = 0; lvt_2_1_ < this.heightMap.length; ++lvt_2_1_)
            {
                this.heightMap[lvt_2_1_] = newHeightMap[lvt_2_1_];
            }
        }
    }

    public Map<BlockPos, TileEntity> getTileEntityMap()
    {
        return this.chunkTileEntityMap;
    }

    public ClassInheritanceMultiMap<Entity>[] getEntityLists()
    {
        return this.entityLists;
    }

    public boolean isTerrainPopulated()
    {
        return this.isTerrainPopulated;
    }

    public void setTerrainPopulated(boolean terrainPopulated)
    {
        this.isTerrainPopulated = terrainPopulated;
    }

    public boolean isLightPopulated()
    {
        return this.isLightPopulated;
    }

    public void setLightPopulated(boolean lightPopulated)
    {
        this.isLightPopulated = lightPopulated;
    }

    public void setModified(boolean modified)
    {
        this.isModified = modified;
    }

    public void setHasEntities(boolean hasEntitiesIn)
    {
        this.hasEntities = hasEntitiesIn;
    }

    public void setLastSaveTime(long saveTime)
    {
        this.lastSaveTime = saveTime;
    }

    public int getLowestHeight()
    {
        return this.heightMapMinimum;
    }

    public long getInhabitedTime()
    {
        return this.inhabitedTime;
    }

    public void setInhabitedTime(long newInhabitedTime)
    {
        this.inhabitedTime = newInhabitedTime;
    }

    public static enum EnumCreateEntityType
    {
        IMMEDIATE,
        QUEUED,
        CHECK;
    }
}
