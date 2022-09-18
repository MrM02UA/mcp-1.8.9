package net.minecraft.world;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;

public abstract class World implements IBlockAccess
{
    private int seaLevel = 63;

    /**
     * boolean; if true updates scheduled by scheduleBlockUpdate happen immediately
     */
    protected boolean scheduledUpdatesAreImmediate;
    public final List<Entity> loadedEntityList = Lists.newArrayList();
    protected final List<Entity> unloadedEntityList = Lists.newArrayList();
    public final List<TileEntity> loadedTileEntityList = Lists.newArrayList();
    public final List<TileEntity> tickableTileEntities = Lists.newArrayList();
    private final List<TileEntity> addedTileEntityList = Lists.newArrayList();
    private final List<TileEntity> tileEntitiesToBeRemoved = Lists.newArrayList();
    public final List<EntityPlayer> playerEntities = Lists.newArrayList();
    public final List<Entity> weatherEffects = Lists.newArrayList();
    protected final IntHashMap<Entity> entitiesById = new IntHashMap();
    private long cloudColour = 16777215L;

    /** How much light is subtracted from full daylight */
    private int skylightSubtracted;

    /**
     * Contains the current Linear Congruential Generator seed for block updates. Used with an A value of 3 and a C
     * value of 0x3c6ef35f, producing a highly planar series of values ill-suited for choosing random blocks in a
     * 16x128x16 field.
     */
    protected int updateLCG = (new Random()).nextInt();

    /**
     * magic number used to generate fast random numbers for 3d distribution within a chunk
     */
    protected final int DIST_HASH_MAGIC = 1013904223;
    protected float prevRainingStrength;
    protected float rainingStrength;
    protected float prevThunderingStrength;
    protected float thunderingStrength;

    /**
     * Set to 2 whenever a lightning bolt is generated in SSP. Decrements if > 0 in updateWeather(). Value appears to be
     * unused.
     */
    private int lastLightningBolt;

    /** RNG for World. */
    public final Random rand = new Random();

    /** The WorldProvider instance that World uses. */
    public final WorldProvider provider;
    protected List<IWorldAccess> worldAccesses = Lists.newArrayList();

    /** Handles chunk operations and caching */
    protected IChunkProvider chunkProvider;
    protected final ISaveHandler saveHandler;

    /**
     * holds information about a world (size on disk, time, spawn point, seed, ...)
     */
    protected WorldInfo worldInfo;

    /**
     * if set, this flag forces a request to load a chunk to load the chunk rather than defaulting to the world's
     * chunkprovider's dummy if possible
     */
    protected boolean findingSpawnPoint;
    protected MapStorage mapStorage;
    protected VillageCollection villageCollectionObj;
    public final Profiler theProfiler;
    private final Calendar theCalendar = Calendar.getInstance();
    protected Scoreboard worldScoreboard = new Scoreboard();

    /**
     * True if the world is a 'slave' client; changes will not be saved or propagated from this world. For example,
     * server worlds have this set to false, client worlds have this set to true.
     */
    public final boolean isRemote;
    protected Set<ChunkCoordIntPair> activeChunkSet = Sets.newHashSet();

    /** number of ticks until the next random ambients play */
    private int ambientTickCountdown;

    /** indicates if enemies are spawned or not */
    protected boolean spawnHostileMobs;

    /** A flag indicating whether we should spawn peaceful mobs. */
    protected boolean spawnPeacefulMobs;
    private boolean processingLoadedTiles;
    private final WorldBorder worldBorder;

    /**
     * is a temporary list of blocks and light values used when updating light levels. Holds up to 32x32x32 blocks (the
     * maximum influence of a light source.) Every element is a packed bit value: 0000000000LLLLzzzzzzyyyyyyxxxxxx. The
     * 4-bit L is a light level used when darkening blocks. 6-bit numbers x, y and z represent the block's offset from
     * the original block, plus 32 (i.e. value of 31 would mean a -1 offset
     */
    int[] lightUpdateBlockList;

    protected World(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client)
    {
        this.ambientTickCountdown = this.rand.nextInt(12000);
        this.spawnHostileMobs = true;
        this.spawnPeacefulMobs = true;
        this.lightUpdateBlockList = new int[32768];
        this.saveHandler = saveHandlerIn;
        this.theProfiler = profilerIn;
        this.worldInfo = info;
        this.provider = providerIn;
        this.isRemote = client;
        this.worldBorder = providerIn.getWorldBorder();
    }

    public World init()
    {
        return this;
    }

    public BiomeGenBase getBiomeGenForCoords(final BlockPos pos)
    {
        if (this.isBlockLoaded(pos))
        {
            Chunk lvt_2_1_ = this.getChunkFromBlockCoords(pos);

            try
            {
                return lvt_2_1_.getBiome(pos, this.provider.getWorldChunkManager());
            }
            catch (Throwable var6)
            {
                CrashReport lvt_4_1_ = CrashReport.makeCrashReport(var6, "Getting biome");
                CrashReportCategory lvt_5_1_ = lvt_4_1_.makeCategory("Coordinates of biome request");
                lvt_5_1_.addCrashSectionCallable("Location", new Callable<String>()
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
                throw new ReportedException(lvt_4_1_);
            }
        }
        else
        {
            return this.provider.getWorldChunkManager().getBiomeGenerator(pos, BiomeGenBase.plains);
        }
    }

    public WorldChunkManager getWorldChunkManager()
    {
        return this.provider.getWorldChunkManager();
    }

    /**
     * Creates the chunk provider for this world. Called in the constructor. Retrieves provider from worldProvider?
     */
    protected abstract IChunkProvider createChunkProvider();

    public void initialize(WorldSettings settings)
    {
        this.worldInfo.setServerInitialized(true);
    }

    /**
     * Sets a new spawn location by finding an uncovered block at a random (x,z) location in the chunk.
     */
    public void setInitialSpawnLocation()
    {
        this.setSpawnPoint(new BlockPos(8, 64, 8));
    }

    public Block getGroundAboveSeaLevel(BlockPos pos)
    {
        BlockPos lvt_2_1_;

        for (lvt_2_1_ = new BlockPos(pos.getX(), this.getSeaLevel(), pos.getZ()); !this.isAirBlock(lvt_2_1_.up()); lvt_2_1_ = lvt_2_1_.up())
        {
            ;
        }

        return this.getBlockState(lvt_2_1_).getBlock();
    }

    /**
     * Check if the given BlockPos has valid coordinates
     */
    private boolean isValid(BlockPos pos)
    {
        return pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000 && pos.getY() >= 0 && pos.getY() < 256;
    }

    /**
     * Checks to see if an air block exists at the provided location. Note that this only checks to see if the blocks
     * material is set to air, meaning it is possible for non-vanilla blocks to still pass this check.
     */
    public boolean isAirBlock(BlockPos pos)
    {
        return this.getBlockState(pos).getBlock().getMaterial() == Material.air;
    }

    public boolean isBlockLoaded(BlockPos pos)
    {
        return this.isBlockLoaded(pos, true);
    }

    public boolean isBlockLoaded(BlockPos pos, boolean allowEmpty)
    {
        return !this.isValid(pos) ? false : this.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4, allowEmpty);
    }

    public boolean isAreaLoaded(BlockPos center, int radius)
    {
        return this.isAreaLoaded(center, radius, true);
    }

    public boolean isAreaLoaded(BlockPos center, int radius, boolean allowEmpty)
    {
        return this.isAreaLoaded(center.getX() - radius, center.getY() - radius, center.getZ() - radius, center.getX() + radius, center.getY() + radius, center.getZ() + radius, allowEmpty);
    }

    public boolean isAreaLoaded(BlockPos from, BlockPos to)
    {
        return this.isAreaLoaded(from, to, true);
    }

    public boolean isAreaLoaded(BlockPos from, BlockPos to, boolean allowEmpty)
    {
        return this.isAreaLoaded(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ(), allowEmpty);
    }

    public boolean isAreaLoaded(StructureBoundingBox box)
    {
        return this.isAreaLoaded(box, true);
    }

    public boolean isAreaLoaded(StructureBoundingBox box, boolean allowEmpty)
    {
        return this.isAreaLoaded(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, allowEmpty);
    }

    private boolean isAreaLoaded(int xStart, int yStart, int zStart, int xEnd, int yEnd, int zEnd, boolean allowEmpty)
    {
        if (yEnd >= 0 && yStart < 256)
        {
            xStart = xStart >> 4;
            zStart = zStart >> 4;
            xEnd = xEnd >> 4;
            zEnd = zEnd >> 4;

            for (int lvt_8_1_ = xStart; lvt_8_1_ <= xEnd; ++lvt_8_1_)
            {
                for (int lvt_9_1_ = zStart; lvt_9_1_ <= zEnd; ++lvt_9_1_)
                {
                    if (!this.isChunkLoaded(lvt_8_1_, lvt_9_1_, allowEmpty))
                    {
                        return false;
                    }
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    protected boolean isChunkLoaded(int x, int z, boolean allowEmpty)
    {
        return this.chunkProvider.chunkExists(x, z) && (allowEmpty || !this.chunkProvider.provideChunk(x, z).isEmpty());
    }

    public Chunk getChunkFromBlockCoords(BlockPos pos)
    {
        return this.getChunkFromChunkCoords(pos.getX() >> 4, pos.getZ() >> 4);
    }

    /**
     * Returns back a chunk looked up by chunk coordinates Args: x, y
     */
    public Chunk getChunkFromChunkCoords(int chunkX, int chunkZ)
    {
        return this.chunkProvider.provideChunk(chunkX, chunkZ);
    }

    /**
     * Sets the block state at a given location. Flag 1 will cause a block update. Flag 2 will send the change to
     * clients (you almost always want this). Flag 4 prevents the block from being re-rendered, if this is a client
     * world. Flags can be added together.
     */
    public boolean setBlockState(BlockPos pos, IBlockState newState, int flags)
    {
        if (!this.isValid(pos))
        {
            return false;
        }
        else if (!this.isRemote && this.worldInfo.getTerrainType() == WorldType.DEBUG_WORLD)
        {
            return false;
        }
        else
        {
            Chunk lvt_4_1_ = this.getChunkFromBlockCoords(pos);
            Block lvt_5_1_ = newState.getBlock();
            IBlockState lvt_6_1_ = lvt_4_1_.setBlockState(pos, newState);

            if (lvt_6_1_ == null)
            {
                return false;
            }
            else
            {
                Block lvt_7_1_ = lvt_6_1_.getBlock();

                if (lvt_5_1_.getLightOpacity() != lvt_7_1_.getLightOpacity() || lvt_5_1_.getLightValue() != lvt_7_1_.getLightValue())
                {
                    this.theProfiler.startSection("checkLight");
                    this.checkLight(pos);
                    this.theProfiler.endSection();
                }

                if ((flags & 2) != 0 && (!this.isRemote || (flags & 4) == 0) && lvt_4_1_.isPopulated())
                {
                    this.markBlockForUpdate(pos);
                }

                if (!this.isRemote && (flags & 1) != 0)
                {
                    this.notifyNeighborsRespectDebug(pos, lvt_6_1_.getBlock());

                    if (lvt_5_1_.hasComparatorInputOverride())
                    {
                        this.updateComparatorOutputLevel(pos, lvt_5_1_);
                    }
                }

                return true;
            }
        }
    }

    public boolean setBlockToAir(BlockPos pos)
    {
        return this.setBlockState(pos, Blocks.air.getDefaultState(), 3);
    }

    /**
     * Sets a block to air, but also plays the sound and particles and can spawn drops
     */
    public boolean destroyBlock(BlockPos pos, boolean dropBlock)
    {
        IBlockState lvt_3_1_ = this.getBlockState(pos);
        Block lvt_4_1_ = lvt_3_1_.getBlock();

        if (lvt_4_1_.getMaterial() == Material.air)
        {
            return false;
        }
        else
        {
            this.playAuxSFX(2001, pos, Block.getStateId(lvt_3_1_));

            if (dropBlock)
            {
                lvt_4_1_.dropBlockAsItem(this, pos, lvt_3_1_, 0);
            }

            return this.setBlockState(pos, Blocks.air.getDefaultState(), 3);
        }
    }

    /**
     * Convenience method to update the block on both the client and server
     */
    public boolean setBlockState(BlockPos pos, IBlockState state)
    {
        return this.setBlockState(pos, state, 3);
    }

    public void markBlockForUpdate(BlockPos pos)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < this.worldAccesses.size(); ++lvt_2_1_)
        {
            ((IWorldAccess)this.worldAccesses.get(lvt_2_1_)).markBlockForUpdate(pos);
        }
    }

    public void notifyNeighborsRespectDebug(BlockPos pos, Block blockType)
    {
        if (this.worldInfo.getTerrainType() != WorldType.DEBUG_WORLD)
        {
            this.notifyNeighborsOfStateChange(pos, blockType);
        }
    }

    /**
     * marks a vertical line of blocks as dirty
     */
    public void markBlocksDirtyVertical(int x1, int z1, int x2, int z2)
    {
        if (x2 > z2)
        {
            int lvt_5_1_ = z2;
            z2 = x2;
            x2 = lvt_5_1_;
        }

        if (!this.provider.getHasNoSky())
        {
            for (int lvt_5_2_ = x2; lvt_5_2_ <= z2; ++lvt_5_2_)
            {
                this.checkLightFor(EnumSkyBlock.SKY, new BlockPos(x1, lvt_5_2_, z1));
            }
        }

        this.markBlockRangeForRenderUpdate(x1, x2, z1, x1, z2, z1);
    }

    public void markBlockRangeForRenderUpdate(BlockPos rangeMin, BlockPos rangeMax)
    {
        this.markBlockRangeForRenderUpdate(rangeMin.getX(), rangeMin.getY(), rangeMin.getZ(), rangeMax.getX(), rangeMax.getY(), rangeMax.getZ());
    }

    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2)
    {
        for (int lvt_7_1_ = 0; lvt_7_1_ < this.worldAccesses.size(); ++lvt_7_1_)
        {
            ((IWorldAccess)this.worldAccesses.get(lvt_7_1_)).markBlockRangeForRenderUpdate(x1, y1, z1, x2, y2, z2);
        }
    }

    public void notifyNeighborsOfStateChange(BlockPos pos, Block blockType)
    {
        this.notifyBlockOfStateChange(pos.west(), blockType);
        this.notifyBlockOfStateChange(pos.east(), blockType);
        this.notifyBlockOfStateChange(pos.down(), blockType);
        this.notifyBlockOfStateChange(pos.up(), blockType);
        this.notifyBlockOfStateChange(pos.north(), blockType);
        this.notifyBlockOfStateChange(pos.south(), blockType);
    }

    public void notifyNeighborsOfStateExcept(BlockPos pos, Block blockType, EnumFacing skipSide)
    {
        if (skipSide != EnumFacing.WEST)
        {
            this.notifyBlockOfStateChange(pos.west(), blockType);
        }

        if (skipSide != EnumFacing.EAST)
        {
            this.notifyBlockOfStateChange(pos.east(), blockType);
        }

        if (skipSide != EnumFacing.DOWN)
        {
            this.notifyBlockOfStateChange(pos.down(), blockType);
        }

        if (skipSide != EnumFacing.UP)
        {
            this.notifyBlockOfStateChange(pos.up(), blockType);
        }

        if (skipSide != EnumFacing.NORTH)
        {
            this.notifyBlockOfStateChange(pos.north(), blockType);
        }

        if (skipSide != EnumFacing.SOUTH)
        {
            this.notifyBlockOfStateChange(pos.south(), blockType);
        }
    }

    public void notifyBlockOfStateChange(BlockPos pos, final Block blockIn)
    {
        if (!this.isRemote)
        {
            IBlockState lvt_3_1_ = this.getBlockState(pos);

            try
            {
                lvt_3_1_.getBlock().onNeighborBlockChange(this, pos, lvt_3_1_, blockIn);
            }
            catch (Throwable var7)
            {
                CrashReport lvt_5_1_ = CrashReport.makeCrashReport(var7, "Exception while updating neighbours");
                CrashReportCategory lvt_6_1_ = lvt_5_1_.makeCategory("Block being updated");
                lvt_6_1_.addCrashSectionCallable("Source block type", new Callable<String>()
                {
                    public String call() throws Exception
                    {
                        try
                        {
                            return String.format("ID #%d (%s // %s)", new Object[] {Integer.valueOf(Block.getIdFromBlock(blockIn)), blockIn.getUnlocalizedName(), blockIn.getClass().getCanonicalName()});
                        }
                        catch (Throwable var2)
                        {
                            return "ID #" + Block.getIdFromBlock(blockIn);
                        }
                    }
                    public Object call() throws Exception
                    {
                        return this.call();
                    }
                });
                CrashReportCategory.addBlockInfo(lvt_6_1_, pos, lvt_3_1_);
                throw new ReportedException(lvt_5_1_);
            }
        }
    }

    public boolean isBlockTickPending(BlockPos pos, Block blockType)
    {
        return false;
    }

    public boolean canSeeSky(BlockPos pos)
    {
        return this.getChunkFromBlockCoords(pos).canSeeSky(pos);
    }

    public boolean canBlockSeeSky(BlockPos pos)
    {
        if (pos.getY() >= this.getSeaLevel())
        {
            return this.canSeeSky(pos);
        }
        else
        {
            BlockPos lvt_2_1_ = new BlockPos(pos.getX(), this.getSeaLevel(), pos.getZ());

            if (!this.canSeeSky(lvt_2_1_))
            {
                return false;
            }
            else
            {
                for (lvt_2_1_ = lvt_2_1_.down(); lvt_2_1_.getY() > pos.getY(); lvt_2_1_ = lvt_2_1_.down())
                {
                    Block lvt_3_1_ = this.getBlockState(lvt_2_1_).getBlock();

                    if (lvt_3_1_.getLightOpacity() > 0 && !lvt_3_1_.getMaterial().isLiquid())
                    {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    public int getLight(BlockPos pos)
    {
        if (pos.getY() < 0)
        {
            return 0;
        }
        else
        {
            if (pos.getY() >= 256)
            {
                pos = new BlockPos(pos.getX(), 255, pos.getZ());
            }

            return this.getChunkFromBlockCoords(pos).getLightSubtracted(pos, 0);
        }
    }

    public int getLightFromNeighbors(BlockPos pos)
    {
        return this.getLight(pos, true);
    }

    public int getLight(BlockPos pos, boolean checkNeighbors)
    {
        if (pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000)
        {
            if (checkNeighbors && this.getBlockState(pos).getBlock().getUseNeighborBrightness())
            {
                int lvt_3_1_ = this.getLight(pos.up(), false);
                int lvt_4_1_ = this.getLight(pos.east(), false);
                int lvt_5_1_ = this.getLight(pos.west(), false);
                int lvt_6_1_ = this.getLight(pos.south(), false);
                int lvt_7_1_ = this.getLight(pos.north(), false);

                if (lvt_4_1_ > lvt_3_1_)
                {
                    lvt_3_1_ = lvt_4_1_;
                }

                if (lvt_5_1_ > lvt_3_1_)
                {
                    lvt_3_1_ = lvt_5_1_;
                }

                if (lvt_6_1_ > lvt_3_1_)
                {
                    lvt_3_1_ = lvt_6_1_;
                }

                if (lvt_7_1_ > lvt_3_1_)
                {
                    lvt_3_1_ = lvt_7_1_;
                }

                return lvt_3_1_;
            }
            else if (pos.getY() < 0)
            {
                return 0;
            }
            else
            {
                if (pos.getY() >= 256)
                {
                    pos = new BlockPos(pos.getX(), 255, pos.getZ());
                }

                Chunk lvt_3_2_ = this.getChunkFromBlockCoords(pos);
                return lvt_3_2_.getLightSubtracted(pos, this.skylightSubtracted);
            }
        }
        else
        {
            return 15;
        }
    }

    /**
     * Returns the position at this x, z coordinate in the chunk with y set to the value from the height map.
     */
    public BlockPos getHeight(BlockPos pos)
    {
        int lvt_2_2_;

        if (pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000)
        {
            if (this.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4, true))
            {
                lvt_2_2_ = this.getChunkFromChunkCoords(pos.getX() >> 4, pos.getZ() >> 4).getHeightValue(pos.getX() & 15, pos.getZ() & 15);
            }
            else
            {
                lvt_2_2_ = 0;
            }
        }
        else
        {
            lvt_2_2_ = this.getSeaLevel() + 1;
        }

        return new BlockPos(pos.getX(), lvt_2_2_, pos.getZ());
    }

    /**
     * Gets the lowest height of the chunk where sunlight directly reaches
     */
    public int getChunksLowestHorizon(int x, int z)
    {
        if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000)
        {
            if (!this.isChunkLoaded(x >> 4, z >> 4, true))
            {
                return 0;
            }
            else
            {
                Chunk lvt_3_1_ = this.getChunkFromChunkCoords(x >> 4, z >> 4);
                return lvt_3_1_.getLowestHeight();
            }
        }
        else
        {
            return this.getSeaLevel() + 1;
        }
    }

    public int getLightFromNeighborsFor(EnumSkyBlock type, BlockPos pos)
    {
        if (this.provider.getHasNoSky() && type == EnumSkyBlock.SKY)
        {
            return 0;
        }
        else
        {
            if (pos.getY() < 0)
            {
                pos = new BlockPos(pos.getX(), 0, pos.getZ());
            }

            if (!this.isValid(pos))
            {
                return type.defaultLightValue;
            }
            else if (!this.isBlockLoaded(pos))
            {
                return type.defaultLightValue;
            }
            else if (this.getBlockState(pos).getBlock().getUseNeighborBrightness())
            {
                int lvt_3_1_ = this.getLightFor(type, pos.up());
                int lvt_4_1_ = this.getLightFor(type, pos.east());
                int lvt_5_1_ = this.getLightFor(type, pos.west());
                int lvt_6_1_ = this.getLightFor(type, pos.south());
                int lvt_7_1_ = this.getLightFor(type, pos.north());

                if (lvt_4_1_ > lvt_3_1_)
                {
                    lvt_3_1_ = lvt_4_1_;
                }

                if (lvt_5_1_ > lvt_3_1_)
                {
                    lvt_3_1_ = lvt_5_1_;
                }

                if (lvt_6_1_ > lvt_3_1_)
                {
                    lvt_3_1_ = lvt_6_1_;
                }

                if (lvt_7_1_ > lvt_3_1_)
                {
                    lvt_3_1_ = lvt_7_1_;
                }

                return lvt_3_1_;
            }
            else
            {
                Chunk lvt_3_2_ = this.getChunkFromBlockCoords(pos);
                return lvt_3_2_.getLightFor(type, pos);
            }
        }
    }

    public int getLightFor(EnumSkyBlock type, BlockPos pos)
    {
        if (pos.getY() < 0)
        {
            pos = new BlockPos(pos.getX(), 0, pos.getZ());
        }

        if (!this.isValid(pos))
        {
            return type.defaultLightValue;
        }
        else if (!this.isBlockLoaded(pos))
        {
            return type.defaultLightValue;
        }
        else
        {
            Chunk lvt_3_1_ = this.getChunkFromBlockCoords(pos);
            return lvt_3_1_.getLightFor(type, pos);
        }
    }

    public void setLightFor(EnumSkyBlock type, BlockPos pos, int lightValue)
    {
        if (this.isValid(pos))
        {
            if (this.isBlockLoaded(pos))
            {
                Chunk lvt_4_1_ = this.getChunkFromBlockCoords(pos);
                lvt_4_1_.setLightFor(type, pos, lightValue);
                this.notifyLightSet(pos);
            }
        }
    }

    public void notifyLightSet(BlockPos pos)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < this.worldAccesses.size(); ++lvt_2_1_)
        {
            ((IWorldAccess)this.worldAccesses.get(lvt_2_1_)).notifyLightSet(pos);
        }
    }

    public int getCombinedLight(BlockPos pos, int lightValue)
    {
        int lvt_3_1_ = this.getLightFromNeighborsFor(EnumSkyBlock.SKY, pos);
        int lvt_4_1_ = this.getLightFromNeighborsFor(EnumSkyBlock.BLOCK, pos);

        if (lvt_4_1_ < lightValue)
        {
            lvt_4_1_ = lightValue;
        }

        return lvt_3_1_ << 20 | lvt_4_1_ << 4;
    }

    public float getLightBrightness(BlockPos pos)
    {
        return this.provider.getLightBrightnessTable()[this.getLightFromNeighbors(pos)];
    }

    public IBlockState getBlockState(BlockPos pos)
    {
        if (!this.isValid(pos))
        {
            return Blocks.air.getDefaultState();
        }
        else
        {
            Chunk lvt_2_1_ = this.getChunkFromBlockCoords(pos);
            return lvt_2_1_.getBlockState(pos);
        }
    }

    /**
     * Checks whether its daytime by seeing if the light subtracted from the skylight is less than 4
     */
    public boolean isDaytime()
    {
        return this.skylightSubtracted < 4;
    }

    /**
     * ray traces all blocks, including non-collideable ones
     */
    public MovingObjectPosition rayTraceBlocks(Vec3 p_72933_1_, Vec3 p_72933_2_)
    {
        return this.rayTraceBlocks(p_72933_1_, p_72933_2_, false, false, false);
    }

    public MovingObjectPosition rayTraceBlocks(Vec3 start, Vec3 end, boolean stopOnLiquid)
    {
        return this.rayTraceBlocks(start, end, stopOnLiquid, false, false);
    }

    /**
     * Performs a raycast against all blocks in the world. Args : Vec1, Vec2, stopOnLiquid,
     * ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock
     */
    public MovingObjectPosition rayTraceBlocks(Vec3 vec31, Vec3 vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock)
    {
        if (!Double.isNaN(vec31.xCoord) && !Double.isNaN(vec31.yCoord) && !Double.isNaN(vec31.zCoord))
        {
            if (!Double.isNaN(vec32.xCoord) && !Double.isNaN(vec32.yCoord) && !Double.isNaN(vec32.zCoord))
            {
                int lvt_6_1_ = MathHelper.floor_double(vec32.xCoord);
                int lvt_7_1_ = MathHelper.floor_double(vec32.yCoord);
                int lvt_8_1_ = MathHelper.floor_double(vec32.zCoord);
                int lvt_9_1_ = MathHelper.floor_double(vec31.xCoord);
                int lvt_10_1_ = MathHelper.floor_double(vec31.yCoord);
                int lvt_11_1_ = MathHelper.floor_double(vec31.zCoord);
                BlockPos lvt_12_1_ = new BlockPos(lvt_9_1_, lvt_10_1_, lvt_11_1_);
                IBlockState lvt_13_1_ = this.getBlockState(lvt_12_1_);
                Block lvt_14_1_ = lvt_13_1_.getBlock();

                if ((!ignoreBlockWithoutBoundingBox || lvt_14_1_.getCollisionBoundingBox(this, lvt_12_1_, lvt_13_1_) != null) && lvt_14_1_.canCollideCheck(lvt_13_1_, stopOnLiquid))
                {
                    MovingObjectPosition lvt_15_1_ = lvt_14_1_.collisionRayTrace(this, lvt_12_1_, vec31, vec32);

                    if (lvt_15_1_ != null)
                    {
                        return lvt_15_1_;
                    }
                }

                MovingObjectPosition lvt_13_2_ = null;
                int lvt_14_2_ = 200;

                while (lvt_14_2_-- >= 0)
                {
                    if (Double.isNaN(vec31.xCoord) || Double.isNaN(vec31.yCoord) || Double.isNaN(vec31.zCoord))
                    {
                        return null;
                    }

                    if (lvt_9_1_ == lvt_6_1_ && lvt_10_1_ == lvt_7_1_ && lvt_11_1_ == lvt_8_1_)
                    {
                        return returnLastUncollidableBlock ? lvt_13_2_ : null;
                    }

                    boolean lvt_15_2_ = true;
                    boolean lvt_16_1_ = true;
                    boolean lvt_17_1_ = true;
                    double lvt_18_1_ = 999.0D;
                    double lvt_20_1_ = 999.0D;
                    double lvt_22_1_ = 999.0D;

                    if (lvt_6_1_ > lvt_9_1_)
                    {
                        lvt_18_1_ = (double)lvt_9_1_ + 1.0D;
                    }
                    else if (lvt_6_1_ < lvt_9_1_)
                    {
                        lvt_18_1_ = (double)lvt_9_1_ + 0.0D;
                    }
                    else
                    {
                        lvt_15_2_ = false;
                    }

                    if (lvt_7_1_ > lvt_10_1_)
                    {
                        lvt_20_1_ = (double)lvt_10_1_ + 1.0D;
                    }
                    else if (lvt_7_1_ < lvt_10_1_)
                    {
                        lvt_20_1_ = (double)lvt_10_1_ + 0.0D;
                    }
                    else
                    {
                        lvt_16_1_ = false;
                    }

                    if (lvt_8_1_ > lvt_11_1_)
                    {
                        lvt_22_1_ = (double)lvt_11_1_ + 1.0D;
                    }
                    else if (lvt_8_1_ < lvt_11_1_)
                    {
                        lvt_22_1_ = (double)lvt_11_1_ + 0.0D;
                    }
                    else
                    {
                        lvt_17_1_ = false;
                    }

                    double lvt_24_1_ = 999.0D;
                    double lvt_26_1_ = 999.0D;
                    double lvt_28_1_ = 999.0D;
                    double lvt_30_1_ = vec32.xCoord - vec31.xCoord;
                    double lvt_32_1_ = vec32.yCoord - vec31.yCoord;
                    double lvt_34_1_ = vec32.zCoord - vec31.zCoord;

                    if (lvt_15_2_)
                    {
                        lvt_24_1_ = (lvt_18_1_ - vec31.xCoord) / lvt_30_1_;
                    }

                    if (lvt_16_1_)
                    {
                        lvt_26_1_ = (lvt_20_1_ - vec31.yCoord) / lvt_32_1_;
                    }

                    if (lvt_17_1_)
                    {
                        lvt_28_1_ = (lvt_22_1_ - vec31.zCoord) / lvt_34_1_;
                    }

                    if (lvt_24_1_ == -0.0D)
                    {
                        lvt_24_1_ = -1.0E-4D;
                    }

                    if (lvt_26_1_ == -0.0D)
                    {
                        lvt_26_1_ = -1.0E-4D;
                    }

                    if (lvt_28_1_ == -0.0D)
                    {
                        lvt_28_1_ = -1.0E-4D;
                    }

                    EnumFacing lvt_36_1_;

                    if (lvt_24_1_ < lvt_26_1_ && lvt_24_1_ < lvt_28_1_)
                    {
                        lvt_36_1_ = lvt_6_1_ > lvt_9_1_ ? EnumFacing.WEST : EnumFacing.EAST;
                        vec31 = new Vec3(lvt_18_1_, vec31.yCoord + lvt_32_1_ * lvt_24_1_, vec31.zCoord + lvt_34_1_ * lvt_24_1_);
                    }
                    else if (lvt_26_1_ < lvt_28_1_)
                    {
                        lvt_36_1_ = lvt_7_1_ > lvt_10_1_ ? EnumFacing.DOWN : EnumFacing.UP;
                        vec31 = new Vec3(vec31.xCoord + lvt_30_1_ * lvt_26_1_, lvt_20_1_, vec31.zCoord + lvt_34_1_ * lvt_26_1_);
                    }
                    else
                    {
                        lvt_36_1_ = lvt_8_1_ > lvt_11_1_ ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        vec31 = new Vec3(vec31.xCoord + lvt_30_1_ * lvt_28_1_, vec31.yCoord + lvt_32_1_ * lvt_28_1_, lvt_22_1_);
                    }

                    lvt_9_1_ = MathHelper.floor_double(vec31.xCoord) - (lvt_36_1_ == EnumFacing.EAST ? 1 : 0);
                    lvt_10_1_ = MathHelper.floor_double(vec31.yCoord) - (lvt_36_1_ == EnumFacing.UP ? 1 : 0);
                    lvt_11_1_ = MathHelper.floor_double(vec31.zCoord) - (lvt_36_1_ == EnumFacing.SOUTH ? 1 : 0);
                    lvt_12_1_ = new BlockPos(lvt_9_1_, lvt_10_1_, lvt_11_1_);
                    IBlockState lvt_37_1_ = this.getBlockState(lvt_12_1_);
                    Block lvt_38_1_ = lvt_37_1_.getBlock();

                    if (!ignoreBlockWithoutBoundingBox || lvt_38_1_.getCollisionBoundingBox(this, lvt_12_1_, lvt_37_1_) != null)
                    {
                        if (lvt_38_1_.canCollideCheck(lvt_37_1_, stopOnLiquid))
                        {
                            MovingObjectPosition lvt_39_1_ = lvt_38_1_.collisionRayTrace(this, lvt_12_1_, vec31, vec32);

                            if (lvt_39_1_ != null)
                            {
                                return lvt_39_1_;
                            }
                        }
                        else
                        {
                            lvt_13_2_ = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec31, lvt_36_1_, lvt_12_1_);
                        }
                    }
                }

                return returnLastUncollidableBlock ? lvt_13_2_ : null;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * Plays a sound at the entity's position. Args: entity, sound, volume (relative to 1.0), and frequency (or pitch,
     * also relative to 1.0).
     */
    public void playSoundAtEntity(Entity entityIn, String name, float volume, float pitch)
    {
        for (int lvt_5_1_ = 0; lvt_5_1_ < this.worldAccesses.size(); ++lvt_5_1_)
        {
            ((IWorldAccess)this.worldAccesses.get(lvt_5_1_)).playSound(name, entityIn.posX, entityIn.posY, entityIn.posZ, volume, pitch);
        }
    }

    /**
     * Plays sound to all near players except the player reference given
     */
    public void playSoundToNearExcept(EntityPlayer player, String name, float volume, float pitch)
    {
        for (int lvt_5_1_ = 0; lvt_5_1_ < this.worldAccesses.size(); ++lvt_5_1_)
        {
            ((IWorldAccess)this.worldAccesses.get(lvt_5_1_)).playSoundToNearExcept(player, name, player.posX, player.posY, player.posZ, volume, pitch);
        }
    }

    /**
     * Play a sound effect. Many many parameters for this function. Not sure what they do, but a classic call is :
     * (double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D, 'random.door_open', 1.0F, world.rand.nextFloat() * 0.1F +
     * 0.9F with i,j,k position of the block.
     */
    public void playSoundEffect(double x, double y, double z, String soundName, float volume, float pitch)
    {
        for (int lvt_10_1_ = 0; lvt_10_1_ < this.worldAccesses.size(); ++lvt_10_1_)
        {
            ((IWorldAccess)this.worldAccesses.get(lvt_10_1_)).playSound(soundName, x, y, z, volume, pitch);
        }
    }

    /**
     * par8 is loudness, all pars passed to minecraftInstance.sndManager.playSound
     */
    public void playSound(double x, double y, double z, String soundName, float volume, float pitch, boolean distanceDelay)
    {
    }

    public void playRecord(BlockPos pos, String name)
    {
        for (int lvt_3_1_ = 0; lvt_3_1_ < this.worldAccesses.size(); ++lvt_3_1_)
        {
            ((IWorldAccess)this.worldAccesses.get(lvt_3_1_)).playRecord(name, pos);
        }
    }

    public void spawnParticle(EnumParticleTypes particleType, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... p_175688_14_)
    {
        this.spawnParticle(particleType.getParticleID(), particleType.getShouldIgnoreRange(), xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, p_175688_14_);
    }

    public void spawnParticle(EnumParticleTypes particleType, boolean p_175682_2_, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... p_175682_15_)
    {
        this.spawnParticle(particleType.getParticleID(), particleType.getShouldIgnoreRange() | p_175682_2_, xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, p_175682_15_);
    }

    private void spawnParticle(int particleID, boolean p_175720_2_, double xCood, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... p_175720_15_)
    {
        for (int lvt_16_1_ = 0; lvt_16_1_ < this.worldAccesses.size(); ++lvt_16_1_)
        {
            ((IWorldAccess)this.worldAccesses.get(lvt_16_1_)).spawnParticle(particleID, p_175720_2_, xCood, yCoord, zCoord, xOffset, yOffset, zOffset, p_175720_15_);
        }
    }

    /**
     * adds a lightning bolt to the list of lightning bolts in this world.
     */
    public boolean addWeatherEffect(Entity entityIn)
    {
        this.weatherEffects.add(entityIn);
        return true;
    }

    /**
     * Called when an entity is spawned in the world. This includes players.
     */
    public boolean spawnEntityInWorld(Entity entityIn)
    {
        int lvt_2_1_ = MathHelper.floor_double(entityIn.posX / 16.0D);
        int lvt_3_1_ = MathHelper.floor_double(entityIn.posZ / 16.0D);
        boolean lvt_4_1_ = entityIn.forceSpawn;

        if (entityIn instanceof EntityPlayer)
        {
            lvt_4_1_ = true;
        }

        if (!lvt_4_1_ && !this.isChunkLoaded(lvt_2_1_, lvt_3_1_, true))
        {
            return false;
        }
        else
        {
            if (entityIn instanceof EntityPlayer)
            {
                EntityPlayer lvt_5_1_ = (EntityPlayer)entityIn;
                this.playerEntities.add(lvt_5_1_);
                this.updateAllPlayersSleepingFlag();
            }

            this.getChunkFromChunkCoords(lvt_2_1_, lvt_3_1_).addEntity(entityIn);
            this.loadedEntityList.add(entityIn);
            this.onEntityAdded(entityIn);
            return true;
        }
    }

    protected void onEntityAdded(Entity entityIn)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < this.worldAccesses.size(); ++lvt_2_1_)
        {
            ((IWorldAccess)this.worldAccesses.get(lvt_2_1_)).onEntityAdded(entityIn);
        }
    }

    protected void onEntityRemoved(Entity entityIn)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < this.worldAccesses.size(); ++lvt_2_1_)
        {
            ((IWorldAccess)this.worldAccesses.get(lvt_2_1_)).onEntityRemoved(entityIn);
        }
    }

    /**
     * Schedule the entity for removal during the next tick. Marks the entity dead in anticipation.
     */
    public void removeEntity(Entity entityIn)
    {
        if (entityIn.riddenByEntity != null)
        {
            entityIn.riddenByEntity.mountEntity((Entity)null);
        }

        if (entityIn.ridingEntity != null)
        {
            entityIn.mountEntity((Entity)null);
        }

        entityIn.setDead();

        if (entityIn instanceof EntityPlayer)
        {
            this.playerEntities.remove(entityIn);
            this.updateAllPlayersSleepingFlag();
            this.onEntityRemoved(entityIn);
        }
    }

    /**
     * Do NOT use this method to remove normal entities- use normal removeEntity
     */
    public void removePlayerEntityDangerously(Entity entityIn)
    {
        entityIn.setDead();

        if (entityIn instanceof EntityPlayer)
        {
            this.playerEntities.remove(entityIn);
            this.updateAllPlayersSleepingFlag();
        }

        int lvt_2_1_ = entityIn.chunkCoordX;
        int lvt_3_1_ = entityIn.chunkCoordZ;

        if (entityIn.addedToChunk && this.isChunkLoaded(lvt_2_1_, lvt_3_1_, true))
        {
            this.getChunkFromChunkCoords(lvt_2_1_, lvt_3_1_).removeEntity(entityIn);
        }

        this.loadedEntityList.remove(entityIn);
        this.onEntityRemoved(entityIn);
    }

    /**
     * Adds a IWorldAccess to the list of worldAccesses
     */
    public void addWorldAccess(IWorldAccess worldAccess)
    {
        this.worldAccesses.add(worldAccess);
    }

    /**
     * Removes a worldAccess from the worldAccesses object
     */
    public void removeWorldAccess(IWorldAccess worldAccess)
    {
        this.worldAccesses.remove(worldAccess);
    }

    public List<AxisAlignedBB> getCollidingBoundingBoxes(Entity entityIn, AxisAlignedBB bb)
    {
        List<AxisAlignedBB> lvt_3_1_ = Lists.newArrayList();
        int lvt_4_1_ = MathHelper.floor_double(bb.minX);
        int lvt_5_1_ = MathHelper.floor_double(bb.maxX + 1.0D);
        int lvt_6_1_ = MathHelper.floor_double(bb.minY);
        int lvt_7_1_ = MathHelper.floor_double(bb.maxY + 1.0D);
        int lvt_8_1_ = MathHelper.floor_double(bb.minZ);
        int lvt_9_1_ = MathHelper.floor_double(bb.maxZ + 1.0D);
        WorldBorder lvt_10_1_ = this.getWorldBorder();
        boolean lvt_11_1_ = entityIn.isOutsideBorder();
        boolean lvt_12_1_ = this.isInsideBorder(lvt_10_1_, entityIn);
        IBlockState lvt_13_1_ = Blocks.stone.getDefaultState();
        BlockPos.MutableBlockPos lvt_14_1_ = new BlockPos.MutableBlockPos();

        for (int lvt_15_1_ = lvt_4_1_; lvt_15_1_ < lvt_5_1_; ++lvt_15_1_)
        {
            for (int lvt_16_1_ = lvt_8_1_; lvt_16_1_ < lvt_9_1_; ++lvt_16_1_)
            {
                if (this.isBlockLoaded(lvt_14_1_.set(lvt_15_1_, 64, lvt_16_1_)))
                {
                    for (int lvt_17_1_ = lvt_6_1_ - 1; lvt_17_1_ < lvt_7_1_; ++lvt_17_1_)
                    {
                        lvt_14_1_.set(lvt_15_1_, lvt_17_1_, lvt_16_1_);

                        if (lvt_11_1_ && lvt_12_1_)
                        {
                            entityIn.setOutsideBorder(false);
                        }
                        else if (!lvt_11_1_ && !lvt_12_1_)
                        {
                            entityIn.setOutsideBorder(true);
                        }

                        IBlockState lvt_18_1_ = lvt_13_1_;

                        if (lvt_10_1_.contains(lvt_14_1_) || !lvt_12_1_)
                        {
                            lvt_18_1_ = this.getBlockState(lvt_14_1_);
                        }

                        lvt_18_1_.getBlock().addCollisionBoxesToList(this, lvt_14_1_, lvt_18_1_, bb, lvt_3_1_, entityIn);
                    }
                }
            }
        }

        double lvt_15_2_ = 0.25D;
        List<Entity> lvt_17_2_ = this.getEntitiesWithinAABBExcludingEntity(entityIn, bb.expand(lvt_15_2_, lvt_15_2_, lvt_15_2_));

        for (int lvt_18_2_ = 0; lvt_18_2_ < lvt_17_2_.size(); ++lvt_18_2_)
        {
            if (entityIn.riddenByEntity != lvt_17_2_ && entityIn.ridingEntity != lvt_17_2_)
            {
                AxisAlignedBB lvt_19_1_ = ((Entity)lvt_17_2_.get(lvt_18_2_)).getCollisionBoundingBox();

                if (lvt_19_1_ != null && lvt_19_1_.intersectsWith(bb))
                {
                    lvt_3_1_.add(lvt_19_1_);
                }

                lvt_19_1_ = entityIn.getCollisionBox((Entity)lvt_17_2_.get(lvt_18_2_));

                if (lvt_19_1_ != null && lvt_19_1_.intersectsWith(bb))
                {
                    lvt_3_1_.add(lvt_19_1_);
                }
            }
        }

        return lvt_3_1_;
    }

    public boolean isInsideBorder(WorldBorder worldBorderIn, Entity entityIn)
    {
        double lvt_3_1_ = worldBorderIn.minX();
        double lvt_5_1_ = worldBorderIn.minZ();
        double lvt_7_1_ = worldBorderIn.maxX();
        double lvt_9_1_ = worldBorderIn.maxZ();

        if (entityIn.isOutsideBorder())
        {
            ++lvt_3_1_;
            ++lvt_5_1_;
            --lvt_7_1_;
            --lvt_9_1_;
        }
        else
        {
            --lvt_3_1_;
            --lvt_5_1_;
            ++lvt_7_1_;
            ++lvt_9_1_;
        }

        return entityIn.posX > lvt_3_1_ && entityIn.posX < lvt_7_1_ && entityIn.posZ > lvt_5_1_ && entityIn.posZ < lvt_9_1_;
    }

    public List<AxisAlignedBB> getCollisionBoxes(AxisAlignedBB bb)
    {
        List<AxisAlignedBB> lvt_2_1_ = Lists.newArrayList();
        int lvt_3_1_ = MathHelper.floor_double(bb.minX);
        int lvt_4_1_ = MathHelper.floor_double(bb.maxX + 1.0D);
        int lvt_5_1_ = MathHelper.floor_double(bb.minY);
        int lvt_6_1_ = MathHelper.floor_double(bb.maxY + 1.0D);
        int lvt_7_1_ = MathHelper.floor_double(bb.minZ);
        int lvt_8_1_ = MathHelper.floor_double(bb.maxZ + 1.0D);
        BlockPos.MutableBlockPos lvt_9_1_ = new BlockPos.MutableBlockPos();

        for (int lvt_10_1_ = lvt_3_1_; lvt_10_1_ < lvt_4_1_; ++lvt_10_1_)
        {
            for (int lvt_11_1_ = lvt_7_1_; lvt_11_1_ < lvt_8_1_; ++lvt_11_1_)
            {
                if (this.isBlockLoaded(lvt_9_1_.set(lvt_10_1_, 64, lvt_11_1_)))
                {
                    for (int lvt_12_1_ = lvt_5_1_ - 1; lvt_12_1_ < lvt_6_1_; ++lvt_12_1_)
                    {
                        lvt_9_1_.set(lvt_10_1_, lvt_12_1_, lvt_11_1_);
                        IBlockState lvt_13_2_;

                        if (lvt_10_1_ >= -30000000 && lvt_10_1_ < 30000000 && lvt_11_1_ >= -30000000 && lvt_11_1_ < 30000000)
                        {
                            lvt_13_2_ = this.getBlockState(lvt_9_1_);
                        }
                        else
                        {
                            lvt_13_2_ = Blocks.bedrock.getDefaultState();
                        }

                        lvt_13_2_.getBlock().addCollisionBoxesToList(this, lvt_9_1_, lvt_13_2_, bb, lvt_2_1_, (Entity)null);
                    }
                }
            }
        }

        return lvt_2_1_;
    }

    /**
     * Returns the amount of skylight subtracted for the current time
     */
    public int calculateSkylightSubtracted(float p_72967_1_)
    {
        float lvt_2_1_ = this.getCelestialAngle(p_72967_1_);
        float lvt_3_1_ = 1.0F - (MathHelper.cos(lvt_2_1_ * (float)Math.PI * 2.0F) * 2.0F + 0.5F);
        lvt_3_1_ = MathHelper.clamp_float(lvt_3_1_, 0.0F, 1.0F);
        lvt_3_1_ = 1.0F - lvt_3_1_;
        lvt_3_1_ = (float)((double)lvt_3_1_ * (1.0D - (double)(this.getRainStrength(p_72967_1_) * 5.0F) / 16.0D));
        lvt_3_1_ = (float)((double)lvt_3_1_ * (1.0D - (double)(this.getThunderStrength(p_72967_1_) * 5.0F) / 16.0D));
        lvt_3_1_ = 1.0F - lvt_3_1_;
        return (int)(lvt_3_1_ * 11.0F);
    }

    /**
     * Returns the sun brightness - checks time of day, rain and thunder
     */
    public float getSunBrightness(float p_72971_1_)
    {
        float lvt_2_1_ = this.getCelestialAngle(p_72971_1_);
        float lvt_3_1_ = 1.0F - (MathHelper.cos(lvt_2_1_ * (float)Math.PI * 2.0F) * 2.0F + 0.2F);
        lvt_3_1_ = MathHelper.clamp_float(lvt_3_1_, 0.0F, 1.0F);
        lvt_3_1_ = 1.0F - lvt_3_1_;
        lvt_3_1_ = (float)((double)lvt_3_1_ * (1.0D - (double)(this.getRainStrength(p_72971_1_) * 5.0F) / 16.0D));
        lvt_3_1_ = (float)((double)lvt_3_1_ * (1.0D - (double)(this.getThunderStrength(p_72971_1_) * 5.0F) / 16.0D));
        return lvt_3_1_ * 0.8F + 0.2F;
    }

    /**
     * Calculates the color for the skybox
     */
    public Vec3 getSkyColor(Entity entityIn, float partialTicks)
    {
        float lvt_3_1_ = this.getCelestialAngle(partialTicks);
        float lvt_4_1_ = MathHelper.cos(lvt_3_1_ * (float)Math.PI * 2.0F) * 2.0F + 0.5F;
        lvt_4_1_ = MathHelper.clamp_float(lvt_4_1_, 0.0F, 1.0F);
        int lvt_5_1_ = MathHelper.floor_double(entityIn.posX);
        int lvt_6_1_ = MathHelper.floor_double(entityIn.posY);
        int lvt_7_1_ = MathHelper.floor_double(entityIn.posZ);
        BlockPos lvt_8_1_ = new BlockPos(lvt_5_1_, lvt_6_1_, lvt_7_1_);
        BiomeGenBase lvt_9_1_ = this.getBiomeGenForCoords(lvt_8_1_);
        float lvt_10_1_ = lvt_9_1_.getFloatTemperature(lvt_8_1_);
        int lvt_11_1_ = lvt_9_1_.getSkyColorByTemp(lvt_10_1_);
        float lvt_12_1_ = (float)(lvt_11_1_ >> 16 & 255) / 255.0F;
        float lvt_13_1_ = (float)(lvt_11_1_ >> 8 & 255) / 255.0F;
        float lvt_14_1_ = (float)(lvt_11_1_ & 255) / 255.0F;
        lvt_12_1_ = lvt_12_1_ * lvt_4_1_;
        lvt_13_1_ = lvt_13_1_ * lvt_4_1_;
        lvt_14_1_ = lvt_14_1_ * lvt_4_1_;
        float lvt_15_1_ = this.getRainStrength(partialTicks);

        if (lvt_15_1_ > 0.0F)
        {
            float lvt_16_1_ = (lvt_12_1_ * 0.3F + lvt_13_1_ * 0.59F + lvt_14_1_ * 0.11F) * 0.6F;
            float lvt_17_1_ = 1.0F - lvt_15_1_ * 0.75F;
            lvt_12_1_ = lvt_12_1_ * lvt_17_1_ + lvt_16_1_ * (1.0F - lvt_17_1_);
            lvt_13_1_ = lvt_13_1_ * lvt_17_1_ + lvt_16_1_ * (1.0F - lvt_17_1_);
            lvt_14_1_ = lvt_14_1_ * lvt_17_1_ + lvt_16_1_ * (1.0F - lvt_17_1_);
        }

        float lvt_16_2_ = this.getThunderStrength(partialTicks);

        if (lvt_16_2_ > 0.0F)
        {
            float lvt_17_2_ = (lvt_12_1_ * 0.3F + lvt_13_1_ * 0.59F + lvt_14_1_ * 0.11F) * 0.2F;
            float lvt_18_1_ = 1.0F - lvt_16_2_ * 0.75F;
            lvt_12_1_ = lvt_12_1_ * lvt_18_1_ + lvt_17_2_ * (1.0F - lvt_18_1_);
            lvt_13_1_ = lvt_13_1_ * lvt_18_1_ + lvt_17_2_ * (1.0F - lvt_18_1_);
            lvt_14_1_ = lvt_14_1_ * lvt_18_1_ + lvt_17_2_ * (1.0F - lvt_18_1_);
        }

        if (this.lastLightningBolt > 0)
        {
            float lvt_17_3_ = (float)this.lastLightningBolt - partialTicks;

            if (lvt_17_3_ > 1.0F)
            {
                lvt_17_3_ = 1.0F;
            }

            lvt_17_3_ = lvt_17_3_ * 0.45F;
            lvt_12_1_ = lvt_12_1_ * (1.0F - lvt_17_3_) + 0.8F * lvt_17_3_;
            lvt_13_1_ = lvt_13_1_ * (1.0F - lvt_17_3_) + 0.8F * lvt_17_3_;
            lvt_14_1_ = lvt_14_1_ * (1.0F - lvt_17_3_) + 1.0F * lvt_17_3_;
        }

        return new Vec3((double)lvt_12_1_, (double)lvt_13_1_, (double)lvt_14_1_);
    }

    /**
     * calls calculateCelestialAngle
     */
    public float getCelestialAngle(float partialTicks)
    {
        return this.provider.calculateCelestialAngle(this.worldInfo.getWorldTime(), partialTicks);
    }

    public int getMoonPhase()
    {
        return this.provider.getMoonPhase(this.worldInfo.getWorldTime());
    }

    /**
     * gets the current fullness of the moon expressed as a float between 1.0 and 0.0, in steps of .25
     */
    public float getCurrentMoonPhaseFactor()
    {
        return WorldProvider.moonPhaseFactors[this.provider.getMoonPhase(this.worldInfo.getWorldTime())];
    }

    /**
     * Return getCelestialAngle()*2*PI
     */
    public float getCelestialAngleRadians(float partialTicks)
    {
        float lvt_2_1_ = this.getCelestialAngle(partialTicks);
        return lvt_2_1_ * (float)Math.PI * 2.0F;
    }

    public Vec3 getCloudColour(float partialTicks)
    {
        float lvt_2_1_ = this.getCelestialAngle(partialTicks);
        float lvt_3_1_ = MathHelper.cos(lvt_2_1_ * (float)Math.PI * 2.0F) * 2.0F + 0.5F;
        lvt_3_1_ = MathHelper.clamp_float(lvt_3_1_, 0.0F, 1.0F);
        float lvt_4_1_ = (float)(this.cloudColour >> 16 & 255L) / 255.0F;
        float lvt_5_1_ = (float)(this.cloudColour >> 8 & 255L) / 255.0F;
        float lvt_6_1_ = (float)(this.cloudColour & 255L) / 255.0F;
        float lvt_7_1_ = this.getRainStrength(partialTicks);

        if (lvt_7_1_ > 0.0F)
        {
            float lvt_8_1_ = (lvt_4_1_ * 0.3F + lvt_5_1_ * 0.59F + lvt_6_1_ * 0.11F) * 0.6F;
            float lvt_9_1_ = 1.0F - lvt_7_1_ * 0.95F;
            lvt_4_1_ = lvt_4_1_ * lvt_9_1_ + lvt_8_1_ * (1.0F - lvt_9_1_);
            lvt_5_1_ = lvt_5_1_ * lvt_9_1_ + lvt_8_1_ * (1.0F - lvt_9_1_);
            lvt_6_1_ = lvt_6_1_ * lvt_9_1_ + lvt_8_1_ * (1.0F - lvt_9_1_);
        }

        lvt_4_1_ = lvt_4_1_ * (lvt_3_1_ * 0.9F + 0.1F);
        lvt_5_1_ = lvt_5_1_ * (lvt_3_1_ * 0.9F + 0.1F);
        lvt_6_1_ = lvt_6_1_ * (lvt_3_1_ * 0.85F + 0.15F);
        float lvt_8_2_ = this.getThunderStrength(partialTicks);

        if (lvt_8_2_ > 0.0F)
        {
            float lvt_9_2_ = (lvt_4_1_ * 0.3F + lvt_5_1_ * 0.59F + lvt_6_1_ * 0.11F) * 0.2F;
            float lvt_10_1_ = 1.0F - lvt_8_2_ * 0.95F;
            lvt_4_1_ = lvt_4_1_ * lvt_10_1_ + lvt_9_2_ * (1.0F - lvt_10_1_);
            lvt_5_1_ = lvt_5_1_ * lvt_10_1_ + lvt_9_2_ * (1.0F - lvt_10_1_);
            lvt_6_1_ = lvt_6_1_ * lvt_10_1_ + lvt_9_2_ * (1.0F - lvt_10_1_);
        }

        return new Vec3((double)lvt_4_1_, (double)lvt_5_1_, (double)lvt_6_1_);
    }

    /**
     * Returns vector(ish) with R/G/B for fog
     */
    public Vec3 getFogColor(float partialTicks)
    {
        float lvt_2_1_ = this.getCelestialAngle(partialTicks);
        return this.provider.getFogColor(lvt_2_1_, partialTicks);
    }

    public BlockPos getPrecipitationHeight(BlockPos pos)
    {
        return this.getChunkFromBlockCoords(pos).getPrecipitationHeight(pos);
    }

    /**
     * Finds the highest block on the x and z coordinate that is solid or liquid, and returns its y coord.
     */
    public BlockPos getTopSolidOrLiquidBlock(BlockPos pos)
    {
        Chunk lvt_2_1_ = this.getChunkFromBlockCoords(pos);
        BlockPos lvt_3_1_;
        BlockPos lvt_4_1_;

        for (lvt_3_1_ = new BlockPos(pos.getX(), lvt_2_1_.getTopFilledSegment() + 16, pos.getZ()); lvt_3_1_.getY() >= 0; lvt_3_1_ = lvt_4_1_)
        {
            lvt_4_1_ = lvt_3_1_.down();
            Material lvt_5_1_ = lvt_2_1_.getBlock(lvt_4_1_).getMaterial();

            if (lvt_5_1_.blocksMovement() && lvt_5_1_ != Material.leaves)
            {
                break;
            }
        }

        return lvt_3_1_;
    }

    /**
     * How bright are stars in the sky
     */
    public float getStarBrightness(float partialTicks)
    {
        float lvt_2_1_ = this.getCelestialAngle(partialTicks);
        float lvt_3_1_ = 1.0F - (MathHelper.cos(lvt_2_1_ * (float)Math.PI * 2.0F) * 2.0F + 0.25F);
        lvt_3_1_ = MathHelper.clamp_float(lvt_3_1_, 0.0F, 1.0F);
        return lvt_3_1_ * lvt_3_1_ * 0.5F;
    }

    public void scheduleUpdate(BlockPos pos, Block blockIn, int delay)
    {
    }

    public void updateBlockTick(BlockPos pos, Block blockIn, int delay, int priority)
    {
    }

    public void scheduleBlockUpdate(BlockPos pos, Block blockIn, int delay, int priority)
    {
    }

    /**
     * Updates (and cleans up) entities and tile entities
     */
    public void updateEntities()
    {
        this.theProfiler.startSection("entities");
        this.theProfiler.startSection("global");

        for (int lvt_1_1_ = 0; lvt_1_1_ < this.weatherEffects.size(); ++lvt_1_1_)
        {
            Entity lvt_2_1_ = (Entity)this.weatherEffects.get(lvt_1_1_);

            try
            {
                ++lvt_2_1_.ticksExisted;
                lvt_2_1_.onUpdate();
            }
            catch (Throwable var9)
            {
                CrashReport lvt_4_1_ = CrashReport.makeCrashReport(var9, "Ticking entity");
                CrashReportCategory lvt_5_1_ = lvt_4_1_.makeCategory("Entity being ticked");

                if (lvt_2_1_ == null)
                {
                    lvt_5_1_.addCrashSection("Entity", "~~NULL~~");
                }
                else
                {
                    lvt_2_1_.addEntityCrashInfo(lvt_5_1_);
                }

                throw new ReportedException(lvt_4_1_);
            }

            if (lvt_2_1_.isDead)
            {
                this.weatherEffects.remove(lvt_1_1_--);
            }
        }

        this.theProfiler.endStartSection("remove");
        this.loadedEntityList.removeAll(this.unloadedEntityList);

        for (int lvt_1_2_ = 0; lvt_1_2_ < this.unloadedEntityList.size(); ++lvt_1_2_)
        {
            Entity lvt_2_2_ = (Entity)this.unloadedEntityList.get(lvt_1_2_);
            int lvt_3_2_ = lvt_2_2_.chunkCoordX;
            int lvt_4_2_ = lvt_2_2_.chunkCoordZ;

            if (lvt_2_2_.addedToChunk && this.isChunkLoaded(lvt_3_2_, lvt_4_2_, true))
            {
                this.getChunkFromChunkCoords(lvt_3_2_, lvt_4_2_).removeEntity(lvt_2_2_);
            }
        }

        for (int lvt_1_3_ = 0; lvt_1_3_ < this.unloadedEntityList.size(); ++lvt_1_3_)
        {
            this.onEntityRemoved((Entity)this.unloadedEntityList.get(lvt_1_3_));
        }

        this.unloadedEntityList.clear();
        this.theProfiler.endStartSection("regular");

        for (int lvt_1_4_ = 0; lvt_1_4_ < this.loadedEntityList.size(); ++lvt_1_4_)
        {
            Entity lvt_2_3_ = (Entity)this.loadedEntityList.get(lvt_1_4_);

            if (lvt_2_3_.ridingEntity != null)
            {
                if (!lvt_2_3_.ridingEntity.isDead && lvt_2_3_.ridingEntity.riddenByEntity == lvt_2_3_)
                {
                    continue;
                }

                lvt_2_3_.ridingEntity.riddenByEntity = null;
                lvt_2_3_.ridingEntity = null;
            }

            this.theProfiler.startSection("tick");

            if (!lvt_2_3_.isDead)
            {
                try
                {
                    this.updateEntity(lvt_2_3_);
                }
                catch (Throwable var8)
                {
                    CrashReport lvt_4_3_ = CrashReport.makeCrashReport(var8, "Ticking entity");
                    CrashReportCategory lvt_5_2_ = lvt_4_3_.makeCategory("Entity being ticked");
                    lvt_2_3_.addEntityCrashInfo(lvt_5_2_);
                    throw new ReportedException(lvt_4_3_);
                }
            }

            this.theProfiler.endSection();
            this.theProfiler.startSection("remove");

            if (lvt_2_3_.isDead)
            {
                int lvt_3_4_ = lvt_2_3_.chunkCoordX;
                int lvt_4_4_ = lvt_2_3_.chunkCoordZ;

                if (lvt_2_3_.addedToChunk && this.isChunkLoaded(lvt_3_4_, lvt_4_4_, true))
                {
                    this.getChunkFromChunkCoords(lvt_3_4_, lvt_4_4_).removeEntity(lvt_2_3_);
                }

                this.loadedEntityList.remove(lvt_1_4_--);
                this.onEntityRemoved(lvt_2_3_);
            }

            this.theProfiler.endSection();
        }

        this.theProfiler.endStartSection("blockEntities");
        this.processingLoadedTiles = true;
        Iterator<TileEntity> lvt_1_5_ = this.tickableTileEntities.iterator();

        while (lvt_1_5_.hasNext())
        {
            TileEntity lvt_2_4_ = (TileEntity)lvt_1_5_.next();

            if (!lvt_2_4_.isInvalid() && lvt_2_4_.hasWorldObj())
            {
                BlockPos lvt_3_5_ = lvt_2_4_.getPos();

                if (this.isBlockLoaded(lvt_3_5_) && this.worldBorder.contains(lvt_3_5_))
                {
                    try
                    {
                        ((ITickable)lvt_2_4_).update();
                    }
                    catch (Throwable var7)
                    {
                        CrashReport lvt_5_3_ = CrashReport.makeCrashReport(var7, "Ticking block entity");
                        CrashReportCategory lvt_6_1_ = lvt_5_3_.makeCategory("Block entity being ticked");
                        lvt_2_4_.addInfoToCrashReport(lvt_6_1_);
                        throw new ReportedException(lvt_5_3_);
                    }
                }
            }

            if (lvt_2_4_.isInvalid())
            {
                lvt_1_5_.remove();
                this.loadedTileEntityList.remove(lvt_2_4_);

                if (this.isBlockLoaded(lvt_2_4_.getPos()))
                {
                    this.getChunkFromBlockCoords(lvt_2_4_.getPos()).removeTileEntity(lvt_2_4_.getPos());
                }
            }
        }

        this.processingLoadedTiles = false;

        if (!this.tileEntitiesToBeRemoved.isEmpty())
        {
            this.tickableTileEntities.removeAll(this.tileEntitiesToBeRemoved);
            this.loadedTileEntityList.removeAll(this.tileEntitiesToBeRemoved);
            this.tileEntitiesToBeRemoved.clear();
        }

        this.theProfiler.endStartSection("pendingBlockEntities");

        if (!this.addedTileEntityList.isEmpty())
        {
            for (int lvt_2_5_ = 0; lvt_2_5_ < this.addedTileEntityList.size(); ++lvt_2_5_)
            {
                TileEntity lvt_3_6_ = (TileEntity)this.addedTileEntityList.get(lvt_2_5_);

                if (!lvt_3_6_.isInvalid())
                {
                    if (!this.loadedTileEntityList.contains(lvt_3_6_))
                    {
                        this.addTileEntity(lvt_3_6_);
                    }

                    if (this.isBlockLoaded(lvt_3_6_.getPos()))
                    {
                        this.getChunkFromBlockCoords(lvt_3_6_.getPos()).addTileEntity(lvt_3_6_.getPos(), lvt_3_6_);
                    }

                    this.markBlockForUpdate(lvt_3_6_.getPos());
                }
            }

            this.addedTileEntityList.clear();
        }

        this.theProfiler.endSection();
        this.theProfiler.endSection();
    }

    public boolean addTileEntity(TileEntity tile)
    {
        boolean lvt_2_1_ = this.loadedTileEntityList.add(tile);

        if (lvt_2_1_ && tile instanceof ITickable)
        {
            this.tickableTileEntities.add(tile);
        }

        return lvt_2_1_;
    }

    public void addTileEntities(Collection<TileEntity> tileEntityCollection)
    {
        if (this.processingLoadedTiles)
        {
            this.addedTileEntityList.addAll(tileEntityCollection);
        }
        else
        {
            for (TileEntity lvt_3_1_ : tileEntityCollection)
            {
                this.loadedTileEntityList.add(lvt_3_1_);

                if (lvt_3_1_ instanceof ITickable)
                {
                    this.tickableTileEntities.add(lvt_3_1_);
                }
            }
        }
    }

    /**
     * Will update the entity in the world if the chunk the entity is in is currently loaded. Args: entity
     */
    public void updateEntity(Entity ent)
    {
        this.updateEntityWithOptionalForce(ent, true);
    }

    /**
     * Will update the entity in the world if the chunk the entity is in is currently loaded or its forced to update.
     * Args: entity, forceUpdate
     */
    public void updateEntityWithOptionalForce(Entity entityIn, boolean forceUpdate)
    {
        int lvt_3_1_ = MathHelper.floor_double(entityIn.posX);
        int lvt_4_1_ = MathHelper.floor_double(entityIn.posZ);
        int lvt_5_1_ = 32;

        if (!forceUpdate || this.isAreaLoaded(lvt_3_1_ - lvt_5_1_, 0, lvt_4_1_ - lvt_5_1_, lvt_3_1_ + lvt_5_1_, 0, lvt_4_1_ + lvt_5_1_, true))
        {
            entityIn.lastTickPosX = entityIn.posX;
            entityIn.lastTickPosY = entityIn.posY;
            entityIn.lastTickPosZ = entityIn.posZ;
            entityIn.prevRotationYaw = entityIn.rotationYaw;
            entityIn.prevRotationPitch = entityIn.rotationPitch;

            if (forceUpdate && entityIn.addedToChunk)
            {
                ++entityIn.ticksExisted;

                if (entityIn.ridingEntity != null)
                {
                    entityIn.updateRidden();
                }
                else
                {
                    entityIn.onUpdate();
                }
            }

            this.theProfiler.startSection("chunkCheck");

            if (Double.isNaN(entityIn.posX) || Double.isInfinite(entityIn.posX))
            {
                entityIn.posX = entityIn.lastTickPosX;
            }

            if (Double.isNaN(entityIn.posY) || Double.isInfinite(entityIn.posY))
            {
                entityIn.posY = entityIn.lastTickPosY;
            }

            if (Double.isNaN(entityIn.posZ) || Double.isInfinite(entityIn.posZ))
            {
                entityIn.posZ = entityIn.lastTickPosZ;
            }

            if (Double.isNaN((double)entityIn.rotationPitch) || Double.isInfinite((double)entityIn.rotationPitch))
            {
                entityIn.rotationPitch = entityIn.prevRotationPitch;
            }

            if (Double.isNaN((double)entityIn.rotationYaw) || Double.isInfinite((double)entityIn.rotationYaw))
            {
                entityIn.rotationYaw = entityIn.prevRotationYaw;
            }

            int lvt_6_1_ = MathHelper.floor_double(entityIn.posX / 16.0D);
            int lvt_7_1_ = MathHelper.floor_double(entityIn.posY / 16.0D);
            int lvt_8_1_ = MathHelper.floor_double(entityIn.posZ / 16.0D);

            if (!entityIn.addedToChunk || entityIn.chunkCoordX != lvt_6_1_ || entityIn.chunkCoordY != lvt_7_1_ || entityIn.chunkCoordZ != lvt_8_1_)
            {
                if (entityIn.addedToChunk && this.isChunkLoaded(entityIn.chunkCoordX, entityIn.chunkCoordZ, true))
                {
                    this.getChunkFromChunkCoords(entityIn.chunkCoordX, entityIn.chunkCoordZ).removeEntityAtIndex(entityIn, entityIn.chunkCoordY);
                }

                if (this.isChunkLoaded(lvt_6_1_, lvt_8_1_, true))
                {
                    entityIn.addedToChunk = true;
                    this.getChunkFromChunkCoords(lvt_6_1_, lvt_8_1_).addEntity(entityIn);
                }
                else
                {
                    entityIn.addedToChunk = false;
                }
            }

            this.theProfiler.endSection();

            if (forceUpdate && entityIn.addedToChunk && entityIn.riddenByEntity != null)
            {
                if (!entityIn.riddenByEntity.isDead && entityIn.riddenByEntity.ridingEntity == entityIn)
                {
                    this.updateEntity(entityIn.riddenByEntity);
                }
                else
                {
                    entityIn.riddenByEntity.ridingEntity = null;
                    entityIn.riddenByEntity = null;
                }
            }
        }
    }

    /**
     * Returns true if there are no solid, live entities in the specified AxisAlignedBB
     */
    public boolean checkNoEntityCollision(AxisAlignedBB bb)
    {
        return this.checkNoEntityCollision(bb, (Entity)null);
    }

    /**
     * Returns true if there are no solid, live entities in the specified AxisAlignedBB, excluding the given entity
     */
    public boolean checkNoEntityCollision(AxisAlignedBB bb, Entity entityIn)
    {
        List<Entity> lvt_3_1_ = this.getEntitiesWithinAABBExcludingEntity((Entity)null, bb);

        for (int lvt_4_1_ = 0; lvt_4_1_ < lvt_3_1_.size(); ++lvt_4_1_)
        {
            Entity lvt_5_1_ = (Entity)lvt_3_1_.get(lvt_4_1_);

            if (!lvt_5_1_.isDead && lvt_5_1_.preventEntitySpawning && lvt_5_1_ != entityIn && (entityIn == null || entityIn.ridingEntity != lvt_5_1_ && entityIn.riddenByEntity != lvt_5_1_))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns true if there are any blocks in the region constrained by an AxisAlignedBB
     */
    public boolean checkBlockCollision(AxisAlignedBB bb)
    {
        int lvt_2_1_ = MathHelper.floor_double(bb.minX);
        int lvt_3_1_ = MathHelper.floor_double(bb.maxX);
        int lvt_4_1_ = MathHelper.floor_double(bb.minY);
        int lvt_5_1_ = MathHelper.floor_double(bb.maxY);
        int lvt_6_1_ = MathHelper.floor_double(bb.minZ);
        int lvt_7_1_ = MathHelper.floor_double(bb.maxZ);
        BlockPos.MutableBlockPos lvt_8_1_ = new BlockPos.MutableBlockPos();

        for (int lvt_9_1_ = lvt_2_1_; lvt_9_1_ <= lvt_3_1_; ++lvt_9_1_)
        {
            for (int lvt_10_1_ = lvt_4_1_; lvt_10_1_ <= lvt_5_1_; ++lvt_10_1_)
            {
                for (int lvt_11_1_ = lvt_6_1_; lvt_11_1_ <= lvt_7_1_; ++lvt_11_1_)
                {
                    Block lvt_12_1_ = this.getBlockState(lvt_8_1_.set(lvt_9_1_, lvt_10_1_, lvt_11_1_)).getBlock();

                    if (lvt_12_1_.getMaterial() != Material.air)
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Returns if any of the blocks within the aabb are liquids. Args: aabb
     */
    public boolean isAnyLiquid(AxisAlignedBB bb)
    {
        int lvt_2_1_ = MathHelper.floor_double(bb.minX);
        int lvt_3_1_ = MathHelper.floor_double(bb.maxX);
        int lvt_4_1_ = MathHelper.floor_double(bb.minY);
        int lvt_5_1_ = MathHelper.floor_double(bb.maxY);
        int lvt_6_1_ = MathHelper.floor_double(bb.minZ);
        int lvt_7_1_ = MathHelper.floor_double(bb.maxZ);
        BlockPos.MutableBlockPos lvt_8_1_ = new BlockPos.MutableBlockPos();

        for (int lvt_9_1_ = lvt_2_1_; lvt_9_1_ <= lvt_3_1_; ++lvt_9_1_)
        {
            for (int lvt_10_1_ = lvt_4_1_; lvt_10_1_ <= lvt_5_1_; ++lvt_10_1_)
            {
                for (int lvt_11_1_ = lvt_6_1_; lvt_11_1_ <= lvt_7_1_; ++lvt_11_1_)
                {
                    Block lvt_12_1_ = this.getBlockState(lvt_8_1_.set(lvt_9_1_, lvt_10_1_, lvt_11_1_)).getBlock();

                    if (lvt_12_1_.getMaterial().isLiquid())
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isFlammableWithin(AxisAlignedBB bb)
    {
        int lvt_2_1_ = MathHelper.floor_double(bb.minX);
        int lvt_3_1_ = MathHelper.floor_double(bb.maxX + 1.0D);
        int lvt_4_1_ = MathHelper.floor_double(bb.minY);
        int lvt_5_1_ = MathHelper.floor_double(bb.maxY + 1.0D);
        int lvt_6_1_ = MathHelper.floor_double(bb.minZ);
        int lvt_7_1_ = MathHelper.floor_double(bb.maxZ + 1.0D);

        if (this.isAreaLoaded(lvt_2_1_, lvt_4_1_, lvt_6_1_, lvt_3_1_, lvt_5_1_, lvt_7_1_, true))
        {
            BlockPos.MutableBlockPos lvt_8_1_ = new BlockPos.MutableBlockPos();

            for (int lvt_9_1_ = lvt_2_1_; lvt_9_1_ < lvt_3_1_; ++lvt_9_1_)
            {
                for (int lvt_10_1_ = lvt_4_1_; lvt_10_1_ < lvt_5_1_; ++lvt_10_1_)
                {
                    for (int lvt_11_1_ = lvt_6_1_; lvt_11_1_ < lvt_7_1_; ++lvt_11_1_)
                    {
                        Block lvt_12_1_ = this.getBlockState(lvt_8_1_.set(lvt_9_1_, lvt_10_1_, lvt_11_1_)).getBlock();

                        if (lvt_12_1_ == Blocks.fire || lvt_12_1_ == Blocks.flowing_lava || lvt_12_1_ == Blocks.lava)
                        {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * handles the acceleration of an object whilst in water. Not sure if it is used elsewhere.
     */
    public boolean handleMaterialAcceleration(AxisAlignedBB bb, Material materialIn, Entity entityIn)
    {
        int lvt_4_1_ = MathHelper.floor_double(bb.minX);
        int lvt_5_1_ = MathHelper.floor_double(bb.maxX + 1.0D);
        int lvt_6_1_ = MathHelper.floor_double(bb.minY);
        int lvt_7_1_ = MathHelper.floor_double(bb.maxY + 1.0D);
        int lvt_8_1_ = MathHelper.floor_double(bb.minZ);
        int lvt_9_1_ = MathHelper.floor_double(bb.maxZ + 1.0D);

        if (!this.isAreaLoaded(lvt_4_1_, lvt_6_1_, lvt_8_1_, lvt_5_1_, lvt_7_1_, lvt_9_1_, true))
        {
            return false;
        }
        else
        {
            boolean lvt_10_1_ = false;
            Vec3 lvt_11_1_ = new Vec3(0.0D, 0.0D, 0.0D);
            BlockPos.MutableBlockPos lvt_12_1_ = new BlockPos.MutableBlockPos();

            for (int lvt_13_1_ = lvt_4_1_; lvt_13_1_ < lvt_5_1_; ++lvt_13_1_)
            {
                for (int lvt_14_1_ = lvt_6_1_; lvt_14_1_ < lvt_7_1_; ++lvt_14_1_)
                {
                    for (int lvt_15_1_ = lvt_8_1_; lvt_15_1_ < lvt_9_1_; ++lvt_15_1_)
                    {
                        lvt_12_1_.set(lvt_13_1_, lvt_14_1_, lvt_15_1_);
                        IBlockState lvt_16_1_ = this.getBlockState(lvt_12_1_);
                        Block lvt_17_1_ = lvt_16_1_.getBlock();

                        if (lvt_17_1_.getMaterial() == materialIn)
                        {
                            double lvt_18_1_ = (double)((float)(lvt_14_1_ + 1) - BlockLiquid.getLiquidHeightPercent(((Integer)lvt_16_1_.getValue(BlockLiquid.LEVEL)).intValue()));

                            if ((double)lvt_7_1_ >= lvt_18_1_)
                            {
                                lvt_10_1_ = true;
                                lvt_11_1_ = lvt_17_1_.modifyAcceleration(this, lvt_12_1_, entityIn, lvt_11_1_);
                            }
                        }
                    }
                }
            }

            if (lvt_11_1_.lengthVector() > 0.0D && entityIn.isPushedByWater())
            {
                lvt_11_1_ = lvt_11_1_.normalize();
                double lvt_13_2_ = 0.014D;
                entityIn.motionX += lvt_11_1_.xCoord * lvt_13_2_;
                entityIn.motionY += lvt_11_1_.yCoord * lvt_13_2_;
                entityIn.motionZ += lvt_11_1_.zCoord * lvt_13_2_;
            }

            return lvt_10_1_;
        }
    }

    /**
     * Returns true if the given bounding box contains the given material
     */
    public boolean isMaterialInBB(AxisAlignedBB bb, Material materialIn)
    {
        int lvt_3_1_ = MathHelper.floor_double(bb.minX);
        int lvt_4_1_ = MathHelper.floor_double(bb.maxX + 1.0D);
        int lvt_5_1_ = MathHelper.floor_double(bb.minY);
        int lvt_6_1_ = MathHelper.floor_double(bb.maxY + 1.0D);
        int lvt_7_1_ = MathHelper.floor_double(bb.minZ);
        int lvt_8_1_ = MathHelper.floor_double(bb.maxZ + 1.0D);
        BlockPos.MutableBlockPos lvt_9_1_ = new BlockPos.MutableBlockPos();

        for (int lvt_10_1_ = lvt_3_1_; lvt_10_1_ < lvt_4_1_; ++lvt_10_1_)
        {
            for (int lvt_11_1_ = lvt_5_1_; lvt_11_1_ < lvt_6_1_; ++lvt_11_1_)
            {
                for (int lvt_12_1_ = lvt_7_1_; lvt_12_1_ < lvt_8_1_; ++lvt_12_1_)
                {
                    if (this.getBlockState(lvt_9_1_.set(lvt_10_1_, lvt_11_1_, lvt_12_1_)).getBlock().getMaterial() == materialIn)
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * checks if the given AABB is in the material given. Used while swimming.
     */
    public boolean isAABBInMaterial(AxisAlignedBB bb, Material materialIn)
    {
        int lvt_3_1_ = MathHelper.floor_double(bb.minX);
        int lvt_4_1_ = MathHelper.floor_double(bb.maxX + 1.0D);
        int lvt_5_1_ = MathHelper.floor_double(bb.minY);
        int lvt_6_1_ = MathHelper.floor_double(bb.maxY + 1.0D);
        int lvt_7_1_ = MathHelper.floor_double(bb.minZ);
        int lvt_8_1_ = MathHelper.floor_double(bb.maxZ + 1.0D);
        BlockPos.MutableBlockPos lvt_9_1_ = new BlockPos.MutableBlockPos();

        for (int lvt_10_1_ = lvt_3_1_; lvt_10_1_ < lvt_4_1_; ++lvt_10_1_)
        {
            for (int lvt_11_1_ = lvt_5_1_; lvt_11_1_ < lvt_6_1_; ++lvt_11_1_)
            {
                for (int lvt_12_1_ = lvt_7_1_; lvt_12_1_ < lvt_8_1_; ++lvt_12_1_)
                {
                    IBlockState lvt_13_1_ = this.getBlockState(lvt_9_1_.set(lvt_10_1_, lvt_11_1_, lvt_12_1_));
                    Block lvt_14_1_ = lvt_13_1_.getBlock();

                    if (lvt_14_1_.getMaterial() == materialIn)
                    {
                        int lvt_15_1_ = ((Integer)lvt_13_1_.getValue(BlockLiquid.LEVEL)).intValue();
                        double lvt_16_1_ = (double)(lvt_11_1_ + 1);

                        if (lvt_15_1_ < 8)
                        {
                            lvt_16_1_ = (double)(lvt_11_1_ + 1) - (double)lvt_15_1_ / 8.0D;
                        }

                        if (lvt_16_1_ >= bb.minY)
                        {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Creates an explosion. Args: entity, x, y, z, strength
     */
    public Explosion createExplosion(Entity entityIn, double x, double y, double z, float strength, boolean isSmoking)
    {
        return this.newExplosion(entityIn, x, y, z, strength, false, isSmoking);
    }

    /**
     * returns a new explosion. Does initiation (at time of writing Explosion is not finished)
     */
    public Explosion newExplosion(Entity entityIn, double x, double y, double z, float strength, boolean isFlaming, boolean isSmoking)
    {
        Explosion lvt_11_1_ = new Explosion(this, entityIn, x, y, z, strength, isFlaming, isSmoking);
        lvt_11_1_.doExplosionA();
        lvt_11_1_.doExplosionB(true);
        return lvt_11_1_;
    }

    /**
     * Gets the percentage of real blocks within within a bounding box, along a specified vector.
     */
    public float getBlockDensity(Vec3 vec, AxisAlignedBB bb)
    {
        double lvt_3_1_ = 1.0D / ((bb.maxX - bb.minX) * 2.0D + 1.0D);
        double lvt_5_1_ = 1.0D / ((bb.maxY - bb.minY) * 2.0D + 1.0D);
        double lvt_7_1_ = 1.0D / ((bb.maxZ - bb.minZ) * 2.0D + 1.0D);
        double lvt_9_1_ = (1.0D - Math.floor(1.0D / lvt_3_1_) * lvt_3_1_) / 2.0D;
        double lvt_11_1_ = (1.0D - Math.floor(1.0D / lvt_7_1_) * lvt_7_1_) / 2.0D;

        if (lvt_3_1_ >= 0.0D && lvt_5_1_ >= 0.0D && lvt_7_1_ >= 0.0D)
        {
            int lvt_13_1_ = 0;
            int lvt_14_1_ = 0;

            for (float lvt_15_1_ = 0.0F; lvt_15_1_ <= 1.0F; lvt_15_1_ = (float)((double)lvt_15_1_ + lvt_3_1_))
            {
                for (float lvt_16_1_ = 0.0F; lvt_16_1_ <= 1.0F; lvt_16_1_ = (float)((double)lvt_16_1_ + lvt_5_1_))
                {
                    for (float lvt_17_1_ = 0.0F; lvt_17_1_ <= 1.0F; lvt_17_1_ = (float)((double)lvt_17_1_ + lvt_7_1_))
                    {
                        double lvt_18_1_ = bb.minX + (bb.maxX - bb.minX) * (double)lvt_15_1_;
                        double lvt_20_1_ = bb.minY + (bb.maxY - bb.minY) * (double)lvt_16_1_;
                        double lvt_22_1_ = bb.minZ + (bb.maxZ - bb.minZ) * (double)lvt_17_1_;

                        if (this.rayTraceBlocks(new Vec3(lvt_18_1_ + lvt_9_1_, lvt_20_1_, lvt_22_1_ + lvt_11_1_), vec) == null)
                        {
                            ++lvt_13_1_;
                        }

                        ++lvt_14_1_;
                    }
                }
            }

            return (float)lvt_13_1_ / (float)lvt_14_1_;
        }
        else
        {
            return 0.0F;
        }
    }

    /**
     * Attempts to extinguish a fire
     */
    public boolean extinguishFire(EntityPlayer player, BlockPos pos, EnumFacing side)
    {
        pos = pos.offset(side);

        if (this.getBlockState(pos).getBlock() == Blocks.fire)
        {
            this.playAuxSFXAtEntity(player, 1004, pos, 0);
            this.setBlockToAir(pos);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * This string is 'All: (number of loaded entities)' Viewable by press ing F3
     */
    public String getDebugLoadedEntities()
    {
        return "All: " + this.loadedEntityList.size();
    }

    /**
     * Returns the name of the current chunk provider, by calling chunkprovider.makeString()
     */
    public String getProviderName()
    {
        return this.chunkProvider.makeString();
    }

    public TileEntity getTileEntity(BlockPos pos)
    {
        if (!this.isValid(pos))
        {
            return null;
        }
        else
        {
            TileEntity lvt_2_1_ = null;

            if (this.processingLoadedTiles)
            {
                for (int lvt_3_1_ = 0; lvt_3_1_ < this.addedTileEntityList.size(); ++lvt_3_1_)
                {
                    TileEntity lvt_4_1_ = (TileEntity)this.addedTileEntityList.get(lvt_3_1_);

                    if (!lvt_4_1_.isInvalid() && lvt_4_1_.getPos().equals(pos))
                    {
                        lvt_2_1_ = lvt_4_1_;
                        break;
                    }
                }
            }

            if (lvt_2_1_ == null)
            {
                lvt_2_1_ = this.getChunkFromBlockCoords(pos).getTileEntity(pos, Chunk.EnumCreateEntityType.IMMEDIATE);
            }

            if (lvt_2_1_ == null)
            {
                for (int lvt_3_2_ = 0; lvt_3_2_ < this.addedTileEntityList.size(); ++lvt_3_2_)
                {
                    TileEntity lvt_4_2_ = (TileEntity)this.addedTileEntityList.get(lvt_3_2_);

                    if (!lvt_4_2_.isInvalid() && lvt_4_2_.getPos().equals(pos))
                    {
                        lvt_2_1_ = lvt_4_2_;
                        break;
                    }
                }
            }

            return lvt_2_1_;
        }
    }

    public void setTileEntity(BlockPos pos, TileEntity tileEntityIn)
    {
        if (tileEntityIn != null && !tileEntityIn.isInvalid())
        {
            if (this.processingLoadedTiles)
            {
                tileEntityIn.setPos(pos);
                Iterator<TileEntity> lvt_3_1_ = this.addedTileEntityList.iterator();

                while (lvt_3_1_.hasNext())
                {
                    TileEntity lvt_4_1_ = (TileEntity)lvt_3_1_.next();

                    if (lvt_4_1_.getPos().equals(pos))
                    {
                        lvt_4_1_.invalidate();
                        lvt_3_1_.remove();
                    }
                }

                this.addedTileEntityList.add(tileEntityIn);
            }
            else
            {
                this.addTileEntity(tileEntityIn);
                this.getChunkFromBlockCoords(pos).addTileEntity(pos, tileEntityIn);
            }
        }
    }

    public void removeTileEntity(BlockPos pos)
    {
        TileEntity lvt_2_1_ = this.getTileEntity(pos);

        if (lvt_2_1_ != null && this.processingLoadedTiles)
        {
            lvt_2_1_.invalidate();
            this.addedTileEntityList.remove(lvt_2_1_);
        }
        else
        {
            if (lvt_2_1_ != null)
            {
                this.addedTileEntityList.remove(lvt_2_1_);
                this.loadedTileEntityList.remove(lvt_2_1_);
                this.tickableTileEntities.remove(lvt_2_1_);
            }

            this.getChunkFromBlockCoords(pos).removeTileEntity(pos);
        }
    }

    /**
     * Adds the specified TileEntity to the pending removal list.
     */
    public void markTileEntityForRemoval(TileEntity tileEntityIn)
    {
        this.tileEntitiesToBeRemoved.add(tileEntityIn);
    }

    public boolean isBlockFullCube(BlockPos pos)
    {
        IBlockState lvt_2_1_ = this.getBlockState(pos);
        AxisAlignedBB lvt_3_1_ = lvt_2_1_.getBlock().getCollisionBoundingBox(this, pos, lvt_2_1_);
        return lvt_3_1_ != null && lvt_3_1_.getAverageEdgeLength() >= 1.0D;
    }

    public static boolean doesBlockHaveSolidTopSurface(IBlockAccess blockAccess, BlockPos pos)
    {
        IBlockState lvt_2_1_ = blockAccess.getBlockState(pos);
        Block lvt_3_1_ = lvt_2_1_.getBlock();
        return lvt_3_1_.getMaterial().isOpaque() && lvt_3_1_.isFullCube() ? true : (lvt_3_1_ instanceof BlockStairs ? lvt_2_1_.getValue(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP : (lvt_3_1_ instanceof BlockSlab ? lvt_2_1_.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP : (lvt_3_1_ instanceof BlockHopper ? true : (lvt_3_1_ instanceof BlockSnow ? ((Integer)lvt_2_1_.getValue(BlockSnow.LAYERS)).intValue() == 7 : false))));
    }

    /**
     * Checks if a block's material is opaque, and that it takes up a full cube
     */
    public boolean isBlockNormalCube(BlockPos pos, boolean _default)
    {
        if (!this.isValid(pos))
        {
            return _default;
        }
        else
        {
            Chunk lvt_3_1_ = this.chunkProvider.provideChunk(pos);

            if (lvt_3_1_.isEmpty())
            {
                return _default;
            }
            else
            {
                Block lvt_4_1_ = this.getBlockState(pos).getBlock();
                return lvt_4_1_.getMaterial().isOpaque() && lvt_4_1_.isFullCube();
            }
        }
    }

    /**
     * Called on construction of the World class to setup the initial skylight values
     */
    public void calculateInitialSkylight()
    {
        int lvt_1_1_ = this.calculateSkylightSubtracted(1.0F);

        if (lvt_1_1_ != this.skylightSubtracted)
        {
            this.skylightSubtracted = lvt_1_1_;
        }
    }

    /**
     * first boolean for hostile mobs and second for peaceful mobs
     */
    public void setAllowedSpawnTypes(boolean hostile, boolean peaceful)
    {
        this.spawnHostileMobs = hostile;
        this.spawnPeacefulMobs = peaceful;
    }

    /**
     * Runs a single tick for the world
     */
    public void tick()
    {
        this.updateWeather();
    }

    /**
     * Called from World constructor to set rainingStrength and thunderingStrength
     */
    protected void calculateInitialWeather()
    {
        if (this.worldInfo.isRaining())
        {
            this.rainingStrength = 1.0F;

            if (this.worldInfo.isThundering())
            {
                this.thunderingStrength = 1.0F;
            }
        }
    }

    /**
     * Updates all weather states.
     */
    protected void updateWeather()
    {
        if (!this.provider.getHasNoSky())
        {
            if (!this.isRemote)
            {
                int lvt_1_1_ = this.worldInfo.getCleanWeatherTime();

                if (lvt_1_1_ > 0)
                {
                    --lvt_1_1_;
                    this.worldInfo.setCleanWeatherTime(lvt_1_1_);
                    this.worldInfo.setThunderTime(this.worldInfo.isThundering() ? 1 : 2);
                    this.worldInfo.setRainTime(this.worldInfo.isRaining() ? 1 : 2);
                }

                int lvt_2_1_ = this.worldInfo.getThunderTime();

                if (lvt_2_1_ <= 0)
                {
                    if (this.worldInfo.isThundering())
                    {
                        this.worldInfo.setThunderTime(this.rand.nextInt(12000) + 3600);
                    }
                    else
                    {
                        this.worldInfo.setThunderTime(this.rand.nextInt(168000) + 12000);
                    }
                }
                else
                {
                    --lvt_2_1_;
                    this.worldInfo.setThunderTime(lvt_2_1_);

                    if (lvt_2_1_ <= 0)
                    {
                        this.worldInfo.setThundering(!this.worldInfo.isThundering());
                    }
                }

                this.prevThunderingStrength = this.thunderingStrength;

                if (this.worldInfo.isThundering())
                {
                    this.thunderingStrength = (float)((double)this.thunderingStrength + 0.01D);
                }
                else
                {
                    this.thunderingStrength = (float)((double)this.thunderingStrength - 0.01D);
                }

                this.thunderingStrength = MathHelper.clamp_float(this.thunderingStrength, 0.0F, 1.0F);
                int lvt_3_1_ = this.worldInfo.getRainTime();

                if (lvt_3_1_ <= 0)
                {
                    if (this.worldInfo.isRaining())
                    {
                        this.worldInfo.setRainTime(this.rand.nextInt(12000) + 12000);
                    }
                    else
                    {
                        this.worldInfo.setRainTime(this.rand.nextInt(168000) + 12000);
                    }
                }
                else
                {
                    --lvt_3_1_;
                    this.worldInfo.setRainTime(lvt_3_1_);

                    if (lvt_3_1_ <= 0)
                    {
                        this.worldInfo.setRaining(!this.worldInfo.isRaining());
                    }
                }

                this.prevRainingStrength = this.rainingStrength;

                if (this.worldInfo.isRaining())
                {
                    this.rainingStrength = (float)((double)this.rainingStrength + 0.01D);
                }
                else
                {
                    this.rainingStrength = (float)((double)this.rainingStrength - 0.01D);
                }

                this.rainingStrength = MathHelper.clamp_float(this.rainingStrength, 0.0F, 1.0F);
            }
        }
    }

    protected void setActivePlayerChunksAndCheckLight()
    {
        this.activeChunkSet.clear();
        this.theProfiler.startSection("buildList");

        for (int lvt_1_1_ = 0; lvt_1_1_ < this.playerEntities.size(); ++lvt_1_1_)
        {
            EntityPlayer lvt_2_1_ = (EntityPlayer)this.playerEntities.get(lvt_1_1_);
            int lvt_3_1_ = MathHelper.floor_double(lvt_2_1_.posX / 16.0D);
            int lvt_4_1_ = MathHelper.floor_double(lvt_2_1_.posZ / 16.0D);
            int lvt_5_1_ = this.getRenderDistanceChunks();

            for (int lvt_6_1_ = -lvt_5_1_; lvt_6_1_ <= lvt_5_1_; ++lvt_6_1_)
            {
                for (int lvt_7_1_ = -lvt_5_1_; lvt_7_1_ <= lvt_5_1_; ++lvt_7_1_)
                {
                    this.activeChunkSet.add(new ChunkCoordIntPair(lvt_6_1_ + lvt_3_1_, lvt_7_1_ + lvt_4_1_));
                }
            }
        }

        this.theProfiler.endSection();

        if (this.ambientTickCountdown > 0)
        {
            --this.ambientTickCountdown;
        }

        this.theProfiler.startSection("playerCheckLight");

        if (!this.playerEntities.isEmpty())
        {
            int lvt_1_2_ = this.rand.nextInt(this.playerEntities.size());
            EntityPlayer lvt_2_2_ = (EntityPlayer)this.playerEntities.get(lvt_1_2_);
            int lvt_3_2_ = MathHelper.floor_double(lvt_2_2_.posX) + this.rand.nextInt(11) - 5;
            int lvt_4_2_ = MathHelper.floor_double(lvt_2_2_.posY) + this.rand.nextInt(11) - 5;
            int lvt_5_2_ = MathHelper.floor_double(lvt_2_2_.posZ) + this.rand.nextInt(11) - 5;
            this.checkLight(new BlockPos(lvt_3_2_, lvt_4_2_, lvt_5_2_));
        }

        this.theProfiler.endSection();
    }

    protected abstract int getRenderDistanceChunks();

    protected void playMoodSoundAndCheckLight(int p_147467_1_, int p_147467_2_, Chunk chunkIn)
    {
        this.theProfiler.endStartSection("moodSound");

        if (this.ambientTickCountdown == 0 && !this.isRemote)
        {
            this.updateLCG = this.updateLCG * 3 + 1013904223;
            int lvt_4_1_ = this.updateLCG >> 2;
            int lvt_5_1_ = lvt_4_1_ & 15;
            int lvt_6_1_ = lvt_4_1_ >> 8 & 15;
            int lvt_7_1_ = lvt_4_1_ >> 16 & 255;
            BlockPos lvt_8_1_ = new BlockPos(lvt_5_1_, lvt_7_1_, lvt_6_1_);
            Block lvt_9_1_ = chunkIn.getBlock(lvt_8_1_);
            lvt_5_1_ = lvt_5_1_ + p_147467_1_;
            lvt_6_1_ = lvt_6_1_ + p_147467_2_;

            if (lvt_9_1_.getMaterial() == Material.air && this.getLight(lvt_8_1_) <= this.rand.nextInt(8) && this.getLightFor(EnumSkyBlock.SKY, lvt_8_1_) <= 0)
            {
                EntityPlayer lvt_10_1_ = this.getClosestPlayer((double)lvt_5_1_ + 0.5D, (double)lvt_7_1_ + 0.5D, (double)lvt_6_1_ + 0.5D, 8.0D);

                if (lvt_10_1_ != null && lvt_10_1_.getDistanceSq((double)lvt_5_1_ + 0.5D, (double)lvt_7_1_ + 0.5D, (double)lvt_6_1_ + 0.5D) > 4.0D)
                {
                    this.playSoundEffect((double)lvt_5_1_ + 0.5D, (double)lvt_7_1_ + 0.5D, (double)lvt_6_1_ + 0.5D, "ambient.cave.cave", 0.7F, 0.8F + this.rand.nextFloat() * 0.2F);
                    this.ambientTickCountdown = this.rand.nextInt(12000) + 6000;
                }
            }
        }

        this.theProfiler.endStartSection("checkLight");
        chunkIn.enqueueRelightChecks();
    }

    protected void updateBlocks()
    {
        this.setActivePlayerChunksAndCheckLight();
    }

    public void forceBlockUpdateTick(Block blockType, BlockPos pos, Random random)
    {
        this.scheduledUpdatesAreImmediate = true;
        blockType.updateTick(this, pos, this.getBlockState(pos), random);
        this.scheduledUpdatesAreImmediate = false;
    }

    public boolean canBlockFreezeWater(BlockPos pos)
    {
        return this.canBlockFreeze(pos, false);
    }

    public boolean canBlockFreezeNoWater(BlockPos pos)
    {
        return this.canBlockFreeze(pos, true);
    }

    /**
     * Checks to see if a given block is both water and cold enough to freeze.
     */
    public boolean canBlockFreeze(BlockPos pos, boolean noWaterAdj)
    {
        BiomeGenBase lvt_3_1_ = this.getBiomeGenForCoords(pos);
        float lvt_4_1_ = lvt_3_1_.getFloatTemperature(pos);

        if (lvt_4_1_ > 0.15F)
        {
            return false;
        }
        else
        {
            if (pos.getY() >= 0 && pos.getY() < 256 && this.getLightFor(EnumSkyBlock.BLOCK, pos) < 10)
            {
                IBlockState lvt_5_1_ = this.getBlockState(pos);
                Block lvt_6_1_ = lvt_5_1_.getBlock();

                if ((lvt_6_1_ == Blocks.water || lvt_6_1_ == Blocks.flowing_water) && ((Integer)lvt_5_1_.getValue(BlockLiquid.LEVEL)).intValue() == 0)
                {
                    if (!noWaterAdj)
                    {
                        return true;
                    }

                    boolean lvt_7_1_ = this.isWater(pos.west()) && this.isWater(pos.east()) && this.isWater(pos.north()) && this.isWater(pos.south());

                    if (!lvt_7_1_)
                    {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    private boolean isWater(BlockPos pos)
    {
        return this.getBlockState(pos).getBlock().getMaterial() == Material.water;
    }

    /**
     * Checks to see if a given block can accumulate snow from it snowing
     */
    public boolean canSnowAt(BlockPos pos, boolean checkLight)
    {
        BiomeGenBase lvt_3_1_ = this.getBiomeGenForCoords(pos);
        float lvt_4_1_ = lvt_3_1_.getFloatTemperature(pos);

        if (lvt_4_1_ > 0.15F)
        {
            return false;
        }
        else if (!checkLight)
        {
            return true;
        }
        else
        {
            if (pos.getY() >= 0 && pos.getY() < 256 && this.getLightFor(EnumSkyBlock.BLOCK, pos) < 10)
            {
                Block lvt_5_1_ = this.getBlockState(pos).getBlock();

                if (lvt_5_1_.getMaterial() == Material.air && Blocks.snow_layer.canPlaceBlockAt(this, pos))
                {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean checkLight(BlockPos pos)
    {
        boolean lvt_2_1_ = false;

        if (!this.provider.getHasNoSky())
        {
            lvt_2_1_ |= this.checkLightFor(EnumSkyBlock.SKY, pos);
        }

        lvt_2_1_ = lvt_2_1_ | this.checkLightFor(EnumSkyBlock.BLOCK, pos);
        return lvt_2_1_;
    }

    /**
     * gets the light level at the supplied position
     */
    private int getRawLight(BlockPos pos, EnumSkyBlock lightType)
    {
        if (lightType == EnumSkyBlock.SKY && this.canSeeSky(pos))
        {
            return 15;
        }
        else
        {
            Block lvt_3_1_ = this.getBlockState(pos).getBlock();
            int lvt_4_1_ = lightType == EnumSkyBlock.SKY ? 0 : lvt_3_1_.getLightValue();
            int lvt_5_1_ = lvt_3_1_.getLightOpacity();

            if (lvt_5_1_ >= 15 && lvt_3_1_.getLightValue() > 0)
            {
                lvt_5_1_ = 1;
            }

            if (lvt_5_1_ < 1)
            {
                lvt_5_1_ = 1;
            }

            if (lvt_5_1_ >= 15)
            {
                return 0;
            }
            else if (lvt_4_1_ >= 14)
            {
                return lvt_4_1_;
            }
            else
            {
                for (EnumFacing lvt_9_1_ : EnumFacing.values())
                {
                    BlockPos lvt_10_1_ = pos.offset(lvt_9_1_);
                    int lvt_11_1_ = this.getLightFor(lightType, lvt_10_1_) - lvt_5_1_;

                    if (lvt_11_1_ > lvt_4_1_)
                    {
                        lvt_4_1_ = lvt_11_1_;
                    }

                    if (lvt_4_1_ >= 14)
                    {
                        return lvt_4_1_;
                    }
                }

                return lvt_4_1_;
            }
        }
    }

    public boolean checkLightFor(EnumSkyBlock lightType, BlockPos pos)
    {
        if (!this.isAreaLoaded(pos, 17, false))
        {
            return false;
        }
        else
        {
            int lvt_3_1_ = 0;
            int lvt_4_1_ = 0;
            this.theProfiler.startSection("getBrightness");
            int lvt_5_1_ = this.getLightFor(lightType, pos);
            int lvt_6_1_ = this.getRawLight(pos, lightType);
            int lvt_7_1_ = pos.getX();
            int lvt_8_1_ = pos.getY();
            int lvt_9_1_ = pos.getZ();

            if (lvt_6_1_ > lvt_5_1_)
            {
                this.lightUpdateBlockList[lvt_4_1_++] = 133152;
            }
            else if (lvt_6_1_ < lvt_5_1_)
            {
                this.lightUpdateBlockList[lvt_4_1_++] = 133152 | lvt_5_1_ << 18;

                while (lvt_3_1_ < lvt_4_1_)
                {
                    int lvt_10_1_ = this.lightUpdateBlockList[lvt_3_1_++];
                    int lvt_11_1_ = (lvt_10_1_ & 63) - 32 + lvt_7_1_;
                    int lvt_12_1_ = (lvt_10_1_ >> 6 & 63) - 32 + lvt_8_1_;
                    int lvt_13_1_ = (lvt_10_1_ >> 12 & 63) - 32 + lvt_9_1_;
                    int lvt_14_1_ = lvt_10_1_ >> 18 & 15;
                    BlockPos lvt_15_1_ = new BlockPos(lvt_11_1_, lvt_12_1_, lvt_13_1_);
                    int lvt_16_1_ = this.getLightFor(lightType, lvt_15_1_);

                    if (lvt_16_1_ == lvt_14_1_)
                    {
                        this.setLightFor(lightType, lvt_15_1_, 0);

                        if (lvt_14_1_ > 0)
                        {
                            int lvt_17_1_ = MathHelper.abs_int(lvt_11_1_ - lvt_7_1_);
                            int lvt_18_1_ = MathHelper.abs_int(lvt_12_1_ - lvt_8_1_);
                            int lvt_19_1_ = MathHelper.abs_int(lvt_13_1_ - lvt_9_1_);

                            if (lvt_17_1_ + lvt_18_1_ + lvt_19_1_ < 17)
                            {
                                BlockPos.MutableBlockPos lvt_20_1_ = new BlockPos.MutableBlockPos();

                                for (EnumFacing lvt_24_1_ : EnumFacing.values())
                                {
                                    int lvt_25_1_ = lvt_11_1_ + lvt_24_1_.getFrontOffsetX();
                                    int lvt_26_1_ = lvt_12_1_ + lvt_24_1_.getFrontOffsetY();
                                    int lvt_27_1_ = lvt_13_1_ + lvt_24_1_.getFrontOffsetZ();
                                    lvt_20_1_.set(lvt_25_1_, lvt_26_1_, lvt_27_1_);
                                    int lvt_28_1_ = Math.max(1, this.getBlockState(lvt_20_1_).getBlock().getLightOpacity());
                                    lvt_16_1_ = this.getLightFor(lightType, lvt_20_1_);

                                    if (lvt_16_1_ == lvt_14_1_ - lvt_28_1_ && lvt_4_1_ < this.lightUpdateBlockList.length)
                                    {
                                        this.lightUpdateBlockList[lvt_4_1_++] = lvt_25_1_ - lvt_7_1_ + 32 | lvt_26_1_ - lvt_8_1_ + 32 << 6 | lvt_27_1_ - lvt_9_1_ + 32 << 12 | lvt_14_1_ - lvt_28_1_ << 18;
                                    }
                                }
                            }
                        }
                    }
                }

                lvt_3_1_ = 0;
            }

            this.theProfiler.endSection();
            this.theProfiler.startSection("checkedPosition < toCheckCount");

            while (lvt_3_1_ < lvt_4_1_)
            {
                int lvt_10_2_ = this.lightUpdateBlockList[lvt_3_1_++];
                int lvt_11_2_ = (lvt_10_2_ & 63) - 32 + lvt_7_1_;
                int lvt_12_2_ = (lvt_10_2_ >> 6 & 63) - 32 + lvt_8_1_;
                int lvt_13_2_ = (lvt_10_2_ >> 12 & 63) - 32 + lvt_9_1_;
                BlockPos lvt_14_2_ = new BlockPos(lvt_11_2_, lvt_12_2_, lvt_13_2_);
                int lvt_15_2_ = this.getLightFor(lightType, lvt_14_2_);
                int lvt_16_2_ = this.getRawLight(lvt_14_2_, lightType);

                if (lvt_16_2_ != lvt_15_2_)
                {
                    this.setLightFor(lightType, lvt_14_2_, lvt_16_2_);

                    if (lvt_16_2_ > lvt_15_2_)
                    {
                        int lvt_17_2_ = Math.abs(lvt_11_2_ - lvt_7_1_);
                        int lvt_18_2_ = Math.abs(lvt_12_2_ - lvt_8_1_);
                        int lvt_19_2_ = Math.abs(lvt_13_2_ - lvt_9_1_);
                        boolean lvt_20_2_ = lvt_4_1_ < this.lightUpdateBlockList.length - 6;

                        if (lvt_17_2_ + lvt_18_2_ + lvt_19_2_ < 17 && lvt_20_2_)
                        {
                            if (this.getLightFor(lightType, lvt_14_2_.west()) < lvt_16_2_)
                            {
                                this.lightUpdateBlockList[lvt_4_1_++] = lvt_11_2_ - 1 - lvt_7_1_ + 32 + (lvt_12_2_ - lvt_8_1_ + 32 << 6) + (lvt_13_2_ - lvt_9_1_ + 32 << 12);
                            }

                            if (this.getLightFor(lightType, lvt_14_2_.east()) < lvt_16_2_)
                            {
                                this.lightUpdateBlockList[lvt_4_1_++] = lvt_11_2_ + 1 - lvt_7_1_ + 32 + (lvt_12_2_ - lvt_8_1_ + 32 << 6) + (lvt_13_2_ - lvt_9_1_ + 32 << 12);
                            }

                            if (this.getLightFor(lightType, lvt_14_2_.down()) < lvt_16_2_)
                            {
                                this.lightUpdateBlockList[lvt_4_1_++] = lvt_11_2_ - lvt_7_1_ + 32 + (lvt_12_2_ - 1 - lvt_8_1_ + 32 << 6) + (lvt_13_2_ - lvt_9_1_ + 32 << 12);
                            }

                            if (this.getLightFor(lightType, lvt_14_2_.up()) < lvt_16_2_)
                            {
                                this.lightUpdateBlockList[lvt_4_1_++] = lvt_11_2_ - lvt_7_1_ + 32 + (lvt_12_2_ + 1 - lvt_8_1_ + 32 << 6) + (lvt_13_2_ - lvt_9_1_ + 32 << 12);
                            }

                            if (this.getLightFor(lightType, lvt_14_2_.north()) < lvt_16_2_)
                            {
                                this.lightUpdateBlockList[lvt_4_1_++] = lvt_11_2_ - lvt_7_1_ + 32 + (lvt_12_2_ - lvt_8_1_ + 32 << 6) + (lvt_13_2_ - 1 - lvt_9_1_ + 32 << 12);
                            }

                            if (this.getLightFor(lightType, lvt_14_2_.south()) < lvt_16_2_)
                            {
                                this.lightUpdateBlockList[lvt_4_1_++] = lvt_11_2_ - lvt_7_1_ + 32 + (lvt_12_2_ - lvt_8_1_ + 32 << 6) + (lvt_13_2_ + 1 - lvt_9_1_ + 32 << 12);
                            }
                        }
                    }
                }
            }

            this.theProfiler.endSection();
            return true;
        }
    }

    /**
     * Runs through the list of updates to run and ticks them
     */
    public boolean tickUpdates(boolean p_72955_1_)
    {
        return false;
    }

    public List<NextTickListEntry> getPendingBlockUpdates(Chunk chunkIn, boolean p_72920_2_)
    {
        return null;
    }

    public List<NextTickListEntry> func_175712_a(StructureBoundingBox structureBB, boolean p_175712_2_)
    {
        return null;
    }

    public List<Entity> getEntitiesWithinAABBExcludingEntity(Entity entityIn, AxisAlignedBB bb)
    {
        return this.getEntitiesInAABBexcluding(entityIn, bb, EntitySelectors.NOT_SPECTATING);
    }

    public List<Entity> getEntitiesInAABBexcluding(Entity entityIn, AxisAlignedBB boundingBox, Predicate <? super Entity > predicate)
    {
        List<Entity> lvt_4_1_ = Lists.newArrayList();
        int lvt_5_1_ = MathHelper.floor_double((boundingBox.minX - 2.0D) / 16.0D);
        int lvt_6_1_ = MathHelper.floor_double((boundingBox.maxX + 2.0D) / 16.0D);
        int lvt_7_1_ = MathHelper.floor_double((boundingBox.minZ - 2.0D) / 16.0D);
        int lvt_8_1_ = MathHelper.floor_double((boundingBox.maxZ + 2.0D) / 16.0D);

        for (int lvt_9_1_ = lvt_5_1_; lvt_9_1_ <= lvt_6_1_; ++lvt_9_1_)
        {
            for (int lvt_10_1_ = lvt_7_1_; lvt_10_1_ <= lvt_8_1_; ++lvt_10_1_)
            {
                if (this.isChunkLoaded(lvt_9_1_, lvt_10_1_, true))
                {
                    this.getChunkFromChunkCoords(lvt_9_1_, lvt_10_1_).getEntitiesWithinAABBForEntity(entityIn, boundingBox, lvt_4_1_, predicate);
                }
            }
        }

        return lvt_4_1_;
    }

    public <T extends Entity> List<T> getEntities(Class <? extends T > entityType, Predicate <? super T > filter)
    {
        List<T> lvt_3_1_ = Lists.newArrayList();

        for (Entity lvt_5_1_ : this.loadedEntityList)
        {
            if (entityType.isAssignableFrom(lvt_5_1_.getClass()) && filter.apply(lvt_5_1_))
            {
                lvt_3_1_.add(lvt_5_1_);
            }
        }

        return lvt_3_1_;
    }

    public <T extends Entity> List<T> getPlayers(Class <? extends T > playerType, Predicate <? super T > filter)
    {
        List<T> lvt_3_1_ = Lists.newArrayList();

        for (Entity lvt_5_1_ : this.playerEntities)
        {
            if (playerType.isAssignableFrom(lvt_5_1_.getClass()) && filter.apply(lvt_5_1_))
            {
                lvt_3_1_.add(lvt_5_1_);
            }
        }

        return lvt_3_1_;
    }

    public <T extends Entity> List<T> getEntitiesWithinAABB(Class <? extends T > classEntity, AxisAlignedBB bb)
    {
        return this.<T>getEntitiesWithinAABB(classEntity, bb, EntitySelectors.NOT_SPECTATING);
    }

    public <T extends Entity> List<T> getEntitiesWithinAABB(Class <? extends T > clazz, AxisAlignedBB aabb, Predicate <? super T > filter)
    {
        int lvt_4_1_ = MathHelper.floor_double((aabb.minX - 2.0D) / 16.0D);
        int lvt_5_1_ = MathHelper.floor_double((aabb.maxX + 2.0D) / 16.0D);
        int lvt_6_1_ = MathHelper.floor_double((aabb.minZ - 2.0D) / 16.0D);
        int lvt_7_1_ = MathHelper.floor_double((aabb.maxZ + 2.0D) / 16.0D);
        List<T> lvt_8_1_ = Lists.newArrayList();

        for (int lvt_9_1_ = lvt_4_1_; lvt_9_1_ <= lvt_5_1_; ++lvt_9_1_)
        {
            for (int lvt_10_1_ = lvt_6_1_; lvt_10_1_ <= lvt_7_1_; ++lvt_10_1_)
            {
                if (this.isChunkLoaded(lvt_9_1_, lvt_10_1_, true))
                {
                    this.getChunkFromChunkCoords(lvt_9_1_, lvt_10_1_).getEntitiesOfTypeWithinAAAB(clazz, aabb, lvt_8_1_, filter);
                }
            }
        }

        return lvt_8_1_;
    }

    public <T extends Entity> T findNearestEntityWithinAABB(Class <? extends T > entityType, AxisAlignedBB aabb, T closestTo)
    {
        List<T> lvt_4_1_ = this.<T>getEntitiesWithinAABB(entityType, aabb);
        T lvt_5_1_ = null;
        double lvt_6_1_ = Double.MAX_VALUE;

        for (int lvt_8_1_ = 0; lvt_8_1_ < lvt_4_1_.size(); ++lvt_8_1_)
        {
            T lvt_9_1_ = (Entity)lvt_4_1_.get(lvt_8_1_);

            if (lvt_9_1_ != closestTo && EntitySelectors.NOT_SPECTATING.apply(lvt_9_1_))
            {
                double lvt_10_1_ = closestTo.getDistanceSqToEntity(lvt_9_1_);

                if (lvt_10_1_ <= lvt_6_1_)
                {
                    lvt_5_1_ = lvt_9_1_;
                    lvt_6_1_ = lvt_10_1_;
                }
            }
        }

        return lvt_5_1_;
    }

    /**
     * Returns the Entity with the given ID, or null if it doesn't exist in this World.
     */
    public Entity getEntityByID(int id)
    {
        return (Entity)this.entitiesById.lookup(id);
    }

    public List<Entity> getLoadedEntityList()
    {
        return this.loadedEntityList;
    }

    public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity)
    {
        if (this.isBlockLoaded(pos))
        {
            this.getChunkFromBlockCoords(pos).setChunkModified();
        }
    }

    /**
     * Counts how many entities of an entity class exist in the world. Args: entityClass
     */
    public int countEntities(Class<?> entityType)
    {
        int lvt_2_1_ = 0;

        for (Entity lvt_4_1_ : this.loadedEntityList)
        {
            if ((!(lvt_4_1_ instanceof EntityLiving) || !((EntityLiving)lvt_4_1_).isNoDespawnRequired()) && entityType.isAssignableFrom(lvt_4_1_.getClass()))
            {
                ++lvt_2_1_;
            }
        }

        return lvt_2_1_;
    }

    public void loadEntities(Collection<Entity> entityCollection)
    {
        this.loadedEntityList.addAll(entityCollection);

        for (Entity lvt_3_1_ : entityCollection)
        {
            this.onEntityAdded(lvt_3_1_);
        }
    }

    public void unloadEntities(Collection<Entity> entityCollection)
    {
        this.unloadedEntityList.addAll(entityCollection);
    }

    public boolean canBlockBePlaced(Block blockIn, BlockPos pos, boolean p_175716_3_, EnumFacing side, Entity entityIn, ItemStack itemStackIn)
    {
        Block lvt_7_1_ = this.getBlockState(pos).getBlock();
        AxisAlignedBB lvt_8_1_ = p_175716_3_ ? null : blockIn.getCollisionBoundingBox(this, pos, blockIn.getDefaultState());
        return lvt_8_1_ != null && !this.checkNoEntityCollision(lvt_8_1_, entityIn) ? false : (lvt_7_1_.getMaterial() == Material.circuits && blockIn == Blocks.anvil ? true : lvt_7_1_.getMaterial().isReplaceable() && blockIn.canReplace(this, pos, side, itemStackIn));
    }

    public int getSeaLevel()
    {
        return this.seaLevel;
    }

    /**
     * Warning this value may not be respected in all cases as it is still hardcoded in many places.
     */
    public void setSeaLevel(int p_181544_1_)
    {
        this.seaLevel = p_181544_1_;
    }

    public int getStrongPower(BlockPos pos, EnumFacing direction)
    {
        IBlockState lvt_3_1_ = this.getBlockState(pos);
        return lvt_3_1_.getBlock().getStrongPower(this, pos, lvt_3_1_, direction);
    }

    public WorldType getWorldType()
    {
        return this.worldInfo.getTerrainType();
    }

    /**
     * Returns the single highest strong power out of all directions using getStrongPower(BlockPos, EnumFacing)
     */
    public int getStrongPower(BlockPos pos)
    {
        int lvt_2_1_ = 0;
        lvt_2_1_ = Math.max(lvt_2_1_, this.getStrongPower(pos.down(), EnumFacing.DOWN));

        if (lvt_2_1_ >= 15)
        {
            return lvt_2_1_;
        }
        else
        {
            lvt_2_1_ = Math.max(lvt_2_1_, this.getStrongPower(pos.up(), EnumFacing.UP));

            if (lvt_2_1_ >= 15)
            {
                return lvt_2_1_;
            }
            else
            {
                lvt_2_1_ = Math.max(lvt_2_1_, this.getStrongPower(pos.north(), EnumFacing.NORTH));

                if (lvt_2_1_ >= 15)
                {
                    return lvt_2_1_;
                }
                else
                {
                    lvt_2_1_ = Math.max(lvt_2_1_, this.getStrongPower(pos.south(), EnumFacing.SOUTH));

                    if (lvt_2_1_ >= 15)
                    {
                        return lvt_2_1_;
                    }
                    else
                    {
                        lvt_2_1_ = Math.max(lvt_2_1_, this.getStrongPower(pos.west(), EnumFacing.WEST));

                        if (lvt_2_1_ >= 15)
                        {
                            return lvt_2_1_;
                        }
                        else
                        {
                            lvt_2_1_ = Math.max(lvt_2_1_, this.getStrongPower(pos.east(), EnumFacing.EAST));
                            return lvt_2_1_ >= 15 ? lvt_2_1_ : lvt_2_1_;
                        }
                    }
                }
            }
        }
    }

    public boolean isSidePowered(BlockPos pos, EnumFacing side)
    {
        return this.getRedstonePower(pos, side) > 0;
    }

    public int getRedstonePower(BlockPos pos, EnumFacing facing)
    {
        IBlockState lvt_3_1_ = this.getBlockState(pos);
        Block lvt_4_1_ = lvt_3_1_.getBlock();
        return lvt_4_1_.isNormalCube() ? this.getStrongPower(pos) : lvt_4_1_.getWeakPower(this, pos, lvt_3_1_, facing);
    }

    public boolean isBlockPowered(BlockPos pos)
    {
        return this.getRedstonePower(pos.down(), EnumFacing.DOWN) > 0 ? true : (this.getRedstonePower(pos.up(), EnumFacing.UP) > 0 ? true : (this.getRedstonePower(pos.north(), EnumFacing.NORTH) > 0 ? true : (this.getRedstonePower(pos.south(), EnumFacing.SOUTH) > 0 ? true : (this.getRedstonePower(pos.west(), EnumFacing.WEST) > 0 ? true : this.getRedstonePower(pos.east(), EnumFacing.EAST) > 0))));
    }

    /**
     * Checks if the specified block or its neighbors are powered by a neighboring block. Used by blocks like TNT and
     * Doors.
     */
    public int isBlockIndirectlyGettingPowered(BlockPos pos)
    {
        int lvt_2_1_ = 0;

        for (EnumFacing lvt_6_1_ : EnumFacing.values())
        {
            int lvt_7_1_ = this.getRedstonePower(pos.offset(lvt_6_1_), lvt_6_1_);

            if (lvt_7_1_ >= 15)
            {
                return 15;
            }

            if (lvt_7_1_ > lvt_2_1_)
            {
                lvt_2_1_ = lvt_7_1_;
            }
        }

        return lvt_2_1_;
    }

    /**
     * Gets the closest player to the entity within the specified distance (if distance is less than 0 then ignored).
     * Args: entity, dist
     */
    public EntityPlayer getClosestPlayerToEntity(Entity entityIn, double distance)
    {
        return this.getClosestPlayer(entityIn.posX, entityIn.posY, entityIn.posZ, distance);
    }

    /**
     * Gets the closest player to the point within the specified distance (distance can be set to less than 0 to not
     * limit the distance). Args: x, y, z, dist
     */
    public EntityPlayer getClosestPlayer(double x, double y, double z, double distance)
    {
        double lvt_9_1_ = -1.0D;
        EntityPlayer lvt_11_1_ = null;

        for (int lvt_12_1_ = 0; lvt_12_1_ < this.playerEntities.size(); ++lvt_12_1_)
        {
            EntityPlayer lvt_13_1_ = (EntityPlayer)this.playerEntities.get(lvt_12_1_);

            if (EntitySelectors.NOT_SPECTATING.apply(lvt_13_1_))
            {
                double lvt_14_1_ = lvt_13_1_.getDistanceSq(x, y, z);

                if ((distance < 0.0D || lvt_14_1_ < distance * distance) && (lvt_9_1_ == -1.0D || lvt_14_1_ < lvt_9_1_))
                {
                    lvt_9_1_ = lvt_14_1_;
                    lvt_11_1_ = lvt_13_1_;
                }
            }
        }

        return lvt_11_1_;
    }

    public boolean isAnyPlayerWithinRangeAt(double x, double y, double z, double range)
    {
        for (int lvt_9_1_ = 0; lvt_9_1_ < this.playerEntities.size(); ++lvt_9_1_)
        {
            EntityPlayer lvt_10_1_ = (EntityPlayer)this.playerEntities.get(lvt_9_1_);

            if (EntitySelectors.NOT_SPECTATING.apply(lvt_10_1_))
            {
                double lvt_11_1_ = lvt_10_1_.getDistanceSq(x, y, z);

                if (range < 0.0D || lvt_11_1_ < range * range)
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Find a player by name in this world.
     */
    public EntityPlayer getPlayerEntityByName(String name)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < this.playerEntities.size(); ++lvt_2_1_)
        {
            EntityPlayer lvt_3_1_ = (EntityPlayer)this.playerEntities.get(lvt_2_1_);

            if (name.equals(lvt_3_1_.getName()))
            {
                return lvt_3_1_;
            }
        }

        return null;
    }

    public EntityPlayer getPlayerEntityByUUID(UUID uuid)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < this.playerEntities.size(); ++lvt_2_1_)
        {
            EntityPlayer lvt_3_1_ = (EntityPlayer)this.playerEntities.get(lvt_2_1_);

            if (uuid.equals(lvt_3_1_.getUniqueID()))
            {
                return lvt_3_1_;
            }
        }

        return null;
    }

    /**
     * If on MP, sends a quitting packet.
     */
    public void sendQuittingDisconnectingPacket()
    {
    }

    /**
     * Checks whether the session lock file was modified by another process
     */
    public void checkSessionLock() throws MinecraftException
    {
        this.saveHandler.checkSessionLock();
    }

    public void setTotalWorldTime(long worldTime)
    {
        this.worldInfo.setWorldTotalTime(worldTime);
    }

    /**
     * gets the random world seed
     */
    public long getSeed()
    {
        return this.worldInfo.getSeed();
    }

    public long getTotalWorldTime()
    {
        return this.worldInfo.getWorldTotalTime();
    }

    public long getWorldTime()
    {
        return this.worldInfo.getWorldTime();
    }

    /**
     * Sets the world time.
     */
    public void setWorldTime(long time)
    {
        this.worldInfo.setWorldTime(time);
    }

    /**
     * Gets the spawn point in the world
     */
    public BlockPos getSpawnPoint()
    {
        BlockPos lvt_1_1_ = new BlockPos(this.worldInfo.getSpawnX(), this.worldInfo.getSpawnY(), this.worldInfo.getSpawnZ());

        if (!this.getWorldBorder().contains(lvt_1_1_))
        {
            lvt_1_1_ = this.getHeight(new BlockPos(this.getWorldBorder().getCenterX(), 0.0D, this.getWorldBorder().getCenterZ()));
        }

        return lvt_1_1_;
    }

    public void setSpawnPoint(BlockPos pos)
    {
        this.worldInfo.setSpawn(pos);
    }

    /**
     * spwans an entity and loads surrounding chunks
     */
    public void joinEntityInSurroundings(Entity entityIn)
    {
        int lvt_2_1_ = MathHelper.floor_double(entityIn.posX / 16.0D);
        int lvt_3_1_ = MathHelper.floor_double(entityIn.posZ / 16.0D);
        int lvt_4_1_ = 2;

        for (int lvt_5_1_ = lvt_2_1_ - lvt_4_1_; lvt_5_1_ <= lvt_2_1_ + lvt_4_1_; ++lvt_5_1_)
        {
            for (int lvt_6_1_ = lvt_3_1_ - lvt_4_1_; lvt_6_1_ <= lvt_3_1_ + lvt_4_1_; ++lvt_6_1_)
            {
                this.getChunkFromChunkCoords(lvt_5_1_, lvt_6_1_);
            }
        }

        if (!this.loadedEntityList.contains(entityIn))
        {
            this.loadedEntityList.add(entityIn);
        }
    }

    public boolean isBlockModifiable(EntityPlayer player, BlockPos pos)
    {
        return true;
    }

    /**
     * sends a Packet 38 (Entity Status) to all tracked players of that entity
     */
    public void setEntityState(Entity entityIn, byte state)
    {
    }

    /**
     * gets the world's chunk provider
     */
    public IChunkProvider getChunkProvider()
    {
        return this.chunkProvider;
    }

    public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam)
    {
        blockIn.onBlockEventReceived(this, pos, this.getBlockState(pos), eventID, eventParam);
    }

    /**
     * Returns this world's current save handler
     */
    public ISaveHandler getSaveHandler()
    {
        return this.saveHandler;
    }

    /**
     * Returns the world's WorldInfo object
     */
    public WorldInfo getWorldInfo()
    {
        return this.worldInfo;
    }

    /**
     * Gets the GameRules instance.
     */
    public GameRules getGameRules()
    {
        return this.worldInfo.getGameRulesInstance();
    }

    /**
     * Updates the flag that indicates whether or not all players in the world are sleeping.
     */
    public void updateAllPlayersSleepingFlag()
    {
    }

    public float getThunderStrength(float delta)
    {
        return (this.prevThunderingStrength + (this.thunderingStrength - this.prevThunderingStrength) * delta) * this.getRainStrength(delta);
    }

    /**
     * Sets the strength of the thunder.
     */
    public void setThunderStrength(float strength)
    {
        this.prevThunderingStrength = strength;
        this.thunderingStrength = strength;
    }

    /**
     * Returns rain strength.
     */
    public float getRainStrength(float delta)
    {
        return this.prevRainingStrength + (this.rainingStrength - this.prevRainingStrength) * delta;
    }

    /**
     * Sets the strength of the rain.
     */
    public void setRainStrength(float strength)
    {
        this.prevRainingStrength = strength;
        this.rainingStrength = strength;
    }

    /**
     * Returns true if the current thunder strength (weighted with the rain strength) is greater than 0.9
     */
    public boolean isThundering()
    {
        return (double)this.getThunderStrength(1.0F) > 0.9D;
    }

    /**
     * Returns true if the current rain strength is greater than 0.2
     */
    public boolean isRaining()
    {
        return (double)this.getRainStrength(1.0F) > 0.2D;
    }

    /**
     * Check if precipitation is currently happening at a position
     */
    public boolean isRainingAt(BlockPos strikePosition)
    {
        if (!this.isRaining())
        {
            return false;
        }
        else if (!this.canSeeSky(strikePosition))
        {
            return false;
        }
        else if (this.getPrecipitationHeight(strikePosition).getY() > strikePosition.getY())
        {
            return false;
        }
        else
        {
            BiomeGenBase lvt_2_1_ = this.getBiomeGenForCoords(strikePosition);
            return lvt_2_1_.getEnableSnow() ? false : (this.canSnowAt(strikePosition, false) ? false : lvt_2_1_.canRain());
        }
    }

    public boolean isBlockinHighHumidity(BlockPos pos)
    {
        BiomeGenBase lvt_2_1_ = this.getBiomeGenForCoords(pos);
        return lvt_2_1_.isHighHumidity();
    }

    public MapStorage getMapStorage()
    {
        return this.mapStorage;
    }

    /**
     * Assigns the given String id to the given MapDataBase using the MapStorage, removing any existing ones of the same
     * id.
     */
    public void setItemData(String dataID, WorldSavedData worldSavedDataIn)
    {
        this.mapStorage.setData(dataID, worldSavedDataIn);
    }

    /**
     * Loads an existing MapDataBase corresponding to the given String id from disk using the MapStorage, instantiating
     * the given Class, or returns null if none such file exists. args: Class to instantiate, String dataid
     */
    public WorldSavedData loadItemData(Class <? extends WorldSavedData > clazz, String dataID)
    {
        return this.mapStorage.loadData(clazz, dataID);
    }

    /**
     * Returns an unique new data id from the MapStorage for the given prefix and saves the idCounts map to the
     * 'idcounts' file.
     */
    public int getUniqueDataId(String key)
    {
        return this.mapStorage.getUniqueDataId(key);
    }

    public void playBroadcastSound(int p_175669_1_, BlockPos pos, int p_175669_3_)
    {
        for (int lvt_4_1_ = 0; lvt_4_1_ < this.worldAccesses.size(); ++lvt_4_1_)
        {
            ((IWorldAccess)this.worldAccesses.get(lvt_4_1_)).broadcastSound(p_175669_1_, pos, p_175669_3_);
        }
    }

    public void playAuxSFX(int p_175718_1_, BlockPos pos, int p_175718_3_)
    {
        this.playAuxSFXAtEntity((EntityPlayer)null, p_175718_1_, pos, p_175718_3_);
    }

    public void playAuxSFXAtEntity(EntityPlayer player, int sfxType, BlockPos pos, int p_180498_4_)
    {
        try
        {
            for (int lvt_5_1_ = 0; lvt_5_1_ < this.worldAccesses.size(); ++lvt_5_1_)
            {
                ((IWorldAccess)this.worldAccesses.get(lvt_5_1_)).playAuxSFX(player, sfxType, pos, p_180498_4_);
            }
        }
        catch (Throwable var8)
        {
            CrashReport lvt_6_1_ = CrashReport.makeCrashReport(var8, "Playing level event");
            CrashReportCategory lvt_7_1_ = lvt_6_1_.makeCategory("Level event being played");
            lvt_7_1_.addCrashSection("Block coordinates", CrashReportCategory.getCoordinateInfo(pos));
            lvt_7_1_.addCrashSection("Event source", player);
            lvt_7_1_.addCrashSection("Event type", Integer.valueOf(sfxType));
            lvt_7_1_.addCrashSection("Event data", Integer.valueOf(p_180498_4_));
            throw new ReportedException(lvt_6_1_);
        }
    }

    /**
     * Returns maximum world height.
     */
    public int getHeight()
    {
        return 256;
    }

    /**
     * Returns current world height.
     */
    public int getActualHeight()
    {
        return this.provider.getHasNoSky() ? 128 : 256;
    }

    /**
     * puts the World Random seed to a specific state dependant on the inputs
     */
    public Random setRandomSeed(int p_72843_1_, int p_72843_2_, int p_72843_3_)
    {
        long lvt_4_1_ = (long)p_72843_1_ * 341873128712L + (long)p_72843_2_ * 132897987541L + this.getWorldInfo().getSeed() + (long)p_72843_3_;
        this.rand.setSeed(lvt_4_1_);
        return this.rand;
    }

    public BlockPos getStrongholdPos(String name, BlockPos pos)
    {
        return this.getChunkProvider().getStrongholdGen(this, name, pos);
    }

    /**
     * set by !chunk.getAreLevelsEmpty
     */
    public boolean extendedLevelsInChunkCache()
    {
        return false;
    }

    /**
     * Returns horizon height for use in rendering the sky.
     */
    public double getHorizon()
    {
        return this.worldInfo.getTerrainType() == WorldType.FLAT ? 0.0D : 63.0D;
    }

    /**
     * Adds some basic stats of the world to the given crash report.
     */
    public CrashReportCategory addWorldInfoToCrashReport(CrashReport report)
    {
        CrashReportCategory lvt_2_1_ = report.makeCategoryDepth("Affected level", 1);
        lvt_2_1_.addCrashSection("Level name", this.worldInfo == null ? "????" : this.worldInfo.getWorldName());
        lvt_2_1_.addCrashSectionCallable("All players", new Callable<String>()
        {
            public String call()
            {
                return World.this.playerEntities.size() + " total; " + World.this.playerEntities.toString();
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        lvt_2_1_.addCrashSectionCallable("Chunk stats", new Callable<String>()
        {
            public String call()
            {
                return World.this.chunkProvider.makeString();
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });

        try
        {
            this.worldInfo.addToCrashReport(lvt_2_1_);
        }
        catch (Throwable var4)
        {
            lvt_2_1_.addCrashSectionThrowable("Level Data Unobtainable", var4);
        }

        return lvt_2_1_;
    }

    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress)
    {
        for (int lvt_4_1_ = 0; lvt_4_1_ < this.worldAccesses.size(); ++lvt_4_1_)
        {
            IWorldAccess lvt_5_1_ = (IWorldAccess)this.worldAccesses.get(lvt_4_1_);
            lvt_5_1_.sendBlockBreakProgress(breakerId, pos, progress);
        }
    }

    /**
     * returns a calendar object containing the current date
     */
    public Calendar getCurrentDate()
    {
        if (this.getTotalWorldTime() % 600L == 0L)
        {
            this.theCalendar.setTimeInMillis(MinecraftServer.getCurrentTimeMillis());
        }

        return this.theCalendar;
    }

    public void makeFireworks(double x, double y, double z, double motionX, double motionY, double motionZ, NBTTagCompound compund)
    {
    }

    public Scoreboard getScoreboard()
    {
        return this.worldScoreboard;
    }

    public void updateComparatorOutputLevel(BlockPos pos, Block blockIn)
    {
        for (EnumFacing lvt_4_1_ : EnumFacing.Plane.HORIZONTAL)
        {
            BlockPos lvt_5_1_ = pos.offset(lvt_4_1_);

            if (this.isBlockLoaded(lvt_5_1_))
            {
                IBlockState lvt_6_1_ = this.getBlockState(lvt_5_1_);

                if (Blocks.unpowered_comparator.isAssociated(lvt_6_1_.getBlock()))
                {
                    lvt_6_1_.getBlock().onNeighborBlockChange(this, lvt_5_1_, lvt_6_1_, blockIn);
                }
                else if (lvt_6_1_.getBlock().isNormalCube())
                {
                    lvt_5_1_ = lvt_5_1_.offset(lvt_4_1_);
                    lvt_6_1_ = this.getBlockState(lvt_5_1_);

                    if (Blocks.unpowered_comparator.isAssociated(lvt_6_1_.getBlock()))
                    {
                        lvt_6_1_.getBlock().onNeighborBlockChange(this, lvt_5_1_, lvt_6_1_, blockIn);
                    }
                }
            }
        }
    }

    public DifficultyInstance getDifficultyForLocation(BlockPos pos)
    {
        long lvt_2_1_ = 0L;
        float lvt_4_1_ = 0.0F;

        if (this.isBlockLoaded(pos))
        {
            lvt_4_1_ = this.getCurrentMoonPhaseFactor();
            lvt_2_1_ = this.getChunkFromBlockCoords(pos).getInhabitedTime();
        }

        return new DifficultyInstance(this.getDifficulty(), this.getWorldTime(), lvt_2_1_, lvt_4_1_);
    }

    public EnumDifficulty getDifficulty()
    {
        return this.getWorldInfo().getDifficulty();
    }

    public int getSkylightSubtracted()
    {
        return this.skylightSubtracted;
    }

    public void setSkylightSubtracted(int newSkylightSubtracted)
    {
        this.skylightSubtracted = newSkylightSubtracted;
    }

    public int getLastLightningBolt()
    {
        return this.lastLightningBolt;
    }

    public void setLastLightningBolt(int lastLightningBoltIn)
    {
        this.lastLightningBolt = lastLightningBoltIn;
    }

    public boolean isFindingSpawnPoint()
    {
        return this.findingSpawnPoint;
    }

    public VillageCollection getVillageCollection()
    {
        return this.villageCollectionObj;
    }

    public WorldBorder getWorldBorder()
    {
        return this.worldBorder;
    }

    /**
     * Returns true if the chunk is located near the spawn point
     */
    public boolean isSpawnChunk(int x, int z)
    {
        BlockPos lvt_3_1_ = this.getSpawnPoint();
        int lvt_4_1_ = x * 16 + 8 - lvt_3_1_.getX();
        int lvt_5_1_ = z * 16 + 8 - lvt_3_1_.getZ();
        int lvt_6_1_ = 128;
        return lvt_4_1_ >= -lvt_6_1_ && lvt_4_1_ <= lvt_6_1_ && lvt_5_1_ >= -lvt_6_1_ && lvt_5_1_ <= lvt_6_1_;
    }
}
