package net.minecraft.world;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEventData;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.INpc;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.ScoreboardSaveData;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.village.VillageCollection;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.feature.WorldGeneratorBonusChest;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldServer extends World implements IThreadListener
{
    private static final Logger logger = LogManager.getLogger();
    private final MinecraftServer mcServer;
    private final EntityTracker theEntityTracker;
    private final PlayerManager thePlayerManager;
    private final Set<NextTickListEntry> pendingTickListEntriesHashSet = Sets.newHashSet();
    private final TreeSet<NextTickListEntry> pendingTickListEntriesTreeSet = new TreeSet();
    private final Map<UUID, Entity> entitiesByUuid = Maps.newHashMap();
    public ChunkProviderServer theChunkProviderServer;

    /** Whether level saving is disabled or not */
    public boolean disableLevelSaving;

    /** is false if there are no players */
    private boolean allPlayersSleeping;
    private int updateEntityTick;

    /**
     * the teleporter to use when the entity is being transferred into the dimension
     */
    private final Teleporter worldTeleporter;
    private final SpawnerAnimals mobSpawner = new SpawnerAnimals();
    protected final VillageSiege villageSiege = new VillageSiege(this);
    private WorldServer.ServerBlockEventList[] blockEventQueue = new WorldServer.ServerBlockEventList[] {new WorldServer.ServerBlockEventList(), new WorldServer.ServerBlockEventList()};
    private int blockEventCacheIndex;
    private static final List<WeightedRandomChestContent> bonusChestContent = Lists.newArrayList(new WeightedRandomChestContent[] {new WeightedRandomChestContent(Items.stick, 0, 1, 3, 10), new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.planks), 0, 1, 3, 10), new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.log), 0, 1, 3, 10), new WeightedRandomChestContent(Items.stone_axe, 0, 1, 1, 3), new WeightedRandomChestContent(Items.wooden_axe, 0, 1, 1, 5), new WeightedRandomChestContent(Items.stone_pickaxe, 0, 1, 1, 3), new WeightedRandomChestContent(Items.wooden_pickaxe, 0, 1, 1, 5), new WeightedRandomChestContent(Items.apple, 0, 2, 3, 5), new WeightedRandomChestContent(Items.bread, 0, 2, 3, 3), new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.log2), 0, 1, 3, 10)});
    private List<NextTickListEntry> pendingTickListEntriesThisTick = Lists.newArrayList();

    public WorldServer(MinecraftServer server, ISaveHandler saveHandlerIn, WorldInfo info, int dimensionId, Profiler profilerIn)
    {
        super(saveHandlerIn, info, WorldProvider.getProviderForDimension(dimensionId), profilerIn, false);
        this.mcServer = server;
        this.theEntityTracker = new EntityTracker(this);
        this.thePlayerManager = new PlayerManager(this);
        this.provider.registerWorld(this);
        this.chunkProvider = this.createChunkProvider();
        this.worldTeleporter = new Teleporter(this);
        this.calculateInitialSkylight();
        this.calculateInitialWeather();
        this.getWorldBorder().setSize(server.getMaxWorldSize());
    }

    public World init()
    {
        this.mapStorage = new MapStorage(this.saveHandler);
        String lvt_1_1_ = VillageCollection.fileNameForProvider(this.provider);
        VillageCollection lvt_2_1_ = (VillageCollection)this.mapStorage.loadData(VillageCollection.class, lvt_1_1_);

        if (lvt_2_1_ == null)
        {
            this.villageCollectionObj = new VillageCollection(this);
            this.mapStorage.setData(lvt_1_1_, this.villageCollectionObj);
        }
        else
        {
            this.villageCollectionObj = lvt_2_1_;
            this.villageCollectionObj.setWorldsForAll(this);
        }

        this.worldScoreboard = new ServerScoreboard(this.mcServer);
        ScoreboardSaveData lvt_3_1_ = (ScoreboardSaveData)this.mapStorage.loadData(ScoreboardSaveData.class, "scoreboard");

        if (lvt_3_1_ == null)
        {
            lvt_3_1_ = new ScoreboardSaveData();
            this.mapStorage.setData("scoreboard", lvt_3_1_);
        }

        lvt_3_1_.setScoreboard(this.worldScoreboard);
        ((ServerScoreboard)this.worldScoreboard).func_96547_a(lvt_3_1_);
        this.getWorldBorder().setCenter(this.worldInfo.getBorderCenterX(), this.worldInfo.getBorderCenterZ());
        this.getWorldBorder().setDamageAmount(this.worldInfo.getBorderDamagePerBlock());
        this.getWorldBorder().setDamageBuffer(this.worldInfo.getBorderSafeZone());
        this.getWorldBorder().setWarningDistance(this.worldInfo.getBorderWarningDistance());
        this.getWorldBorder().setWarningTime(this.worldInfo.getBorderWarningTime());

        if (this.worldInfo.getBorderLerpTime() > 0L)
        {
            this.getWorldBorder().setTransition(this.worldInfo.getBorderSize(), this.worldInfo.getBorderLerpTarget(), this.worldInfo.getBorderLerpTime());
        }
        else
        {
            this.getWorldBorder().setTransition(this.worldInfo.getBorderSize());
        }

        return this;
    }

    /**
     * Runs a single tick for the world
     */
    public void tick()
    {
        super.tick();

        if (this.getWorldInfo().isHardcoreModeEnabled() && this.getDifficulty() != EnumDifficulty.HARD)
        {
            this.getWorldInfo().setDifficulty(EnumDifficulty.HARD);
        }

        this.provider.getWorldChunkManager().cleanupCache();

        if (this.areAllPlayersAsleep())
        {
            if (this.getGameRules().getBoolean("doDaylightCycle"))
            {
                long lvt_1_1_ = this.worldInfo.getWorldTime() + 24000L;
                this.worldInfo.setWorldTime(lvt_1_1_ - lvt_1_1_ % 24000L);
            }

            this.wakeAllPlayers();
        }

        this.theProfiler.startSection("mobSpawner");

        if (this.getGameRules().getBoolean("doMobSpawning") && this.worldInfo.getTerrainType() != WorldType.DEBUG_WORLD)
        {
            this.mobSpawner.findChunksForSpawning(this, this.spawnHostileMobs, this.spawnPeacefulMobs, this.worldInfo.getWorldTotalTime() % 400L == 0L);
        }

        this.theProfiler.endStartSection("chunkSource");
        this.chunkProvider.unloadQueuedChunks();
        int lvt_1_2_ = this.calculateSkylightSubtracted(1.0F);

        if (lvt_1_2_ != this.getSkylightSubtracted())
        {
            this.setSkylightSubtracted(lvt_1_2_);
        }

        this.worldInfo.setWorldTotalTime(this.worldInfo.getWorldTotalTime() + 1L);

        if (this.getGameRules().getBoolean("doDaylightCycle"))
        {
            this.worldInfo.setWorldTime(this.worldInfo.getWorldTime() + 1L);
        }

        this.theProfiler.endStartSection("tickPending");
        this.tickUpdates(false);
        this.theProfiler.endStartSection("tickBlocks");
        this.updateBlocks();
        this.theProfiler.endStartSection("chunkMap");
        this.thePlayerManager.updatePlayerInstances();
        this.theProfiler.endStartSection("village");
        this.villageCollectionObj.tick();
        this.villageSiege.tick();
        this.theProfiler.endStartSection("portalForcer");
        this.worldTeleporter.removeStalePortalLocations(this.getTotalWorldTime());
        this.theProfiler.endSection();
        this.sendQueuedBlockEvents();
    }

    public BiomeGenBase.SpawnListEntry getSpawnListEntryForTypeAt(EnumCreatureType creatureType, BlockPos pos)
    {
        List<BiomeGenBase.SpawnListEntry> lvt_3_1_ = this.getChunkProvider().getPossibleCreatures(creatureType, pos);
        return lvt_3_1_ != null && !lvt_3_1_.isEmpty() ? (BiomeGenBase.SpawnListEntry)WeightedRandom.getRandomItem(this.rand, lvt_3_1_) : null;
    }

    public boolean canCreatureTypeSpawnHere(EnumCreatureType creatureType, BiomeGenBase.SpawnListEntry spawnListEntry, BlockPos pos)
    {
        List<BiomeGenBase.SpawnListEntry> lvt_4_1_ = this.getChunkProvider().getPossibleCreatures(creatureType, pos);
        return lvt_4_1_ != null && !lvt_4_1_.isEmpty() ? lvt_4_1_.contains(spawnListEntry) : false;
    }

    /**
     * Updates the flag that indicates whether or not all players in the world are sleeping.
     */
    public void updateAllPlayersSleepingFlag()
    {
        this.allPlayersSleeping = false;

        if (!this.playerEntities.isEmpty())
        {
            int lvt_1_1_ = 0;
            int lvt_2_1_ = 0;

            for (EntityPlayer lvt_4_1_ : this.playerEntities)
            {
                if (lvt_4_1_.isSpectator())
                {
                    ++lvt_1_1_;
                }
                else if (lvt_4_1_.isPlayerSleeping())
                {
                    ++lvt_2_1_;
                }
            }

            this.allPlayersSleeping = lvt_2_1_ > 0 && lvt_2_1_ >= this.playerEntities.size() - lvt_1_1_;
        }
    }

    protected void wakeAllPlayers()
    {
        this.allPlayersSleeping = false;

        for (EntityPlayer lvt_2_1_ : this.playerEntities)
        {
            if (lvt_2_1_.isPlayerSleeping())
            {
                lvt_2_1_.wakeUpPlayer(false, false, true);
            }
        }

        this.resetRainAndThunder();
    }

    private void resetRainAndThunder()
    {
        this.worldInfo.setRainTime(0);
        this.worldInfo.setRaining(false);
        this.worldInfo.setThunderTime(0);
        this.worldInfo.setThundering(false);
    }

    public boolean areAllPlayersAsleep()
    {
        if (this.allPlayersSleeping && !this.isRemote)
        {
            for (EntityPlayer lvt_2_1_ : this.playerEntities)
            {
                if (lvt_2_1_.isSpectator() || !lvt_2_1_.isPlayerFullyAsleep())
                {
                    return false;
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Sets a new spawn location by finding an uncovered block at a random (x,z) location in the chunk.
     */
    public void setInitialSpawnLocation()
    {
        if (this.worldInfo.getSpawnY() <= 0)
        {
            this.worldInfo.setSpawnY(this.getSeaLevel() + 1);
        }

        int lvt_1_1_ = this.worldInfo.getSpawnX();
        int lvt_2_1_ = this.worldInfo.getSpawnZ();
        int lvt_3_1_ = 0;

        while (this.getGroundAboveSeaLevel(new BlockPos(lvt_1_1_, 0, lvt_2_1_)).getMaterial() == Material.air)
        {
            lvt_1_1_ += this.rand.nextInt(8) - this.rand.nextInt(8);
            lvt_2_1_ += this.rand.nextInt(8) - this.rand.nextInt(8);
            ++lvt_3_1_;

            if (lvt_3_1_ == 10000)
            {
                break;
            }
        }

        this.worldInfo.setSpawnX(lvt_1_1_);
        this.worldInfo.setSpawnZ(lvt_2_1_);
    }

    protected void updateBlocks()
    {
        super.updateBlocks();

        if (this.worldInfo.getTerrainType() == WorldType.DEBUG_WORLD)
        {
            for (ChunkCoordIntPair lvt_2_1_ : this.activeChunkSet)
            {
                this.getChunkFromChunkCoords(lvt_2_1_.chunkXPos, lvt_2_1_.chunkZPos).func_150804_b(false);
            }
        }
        else
        {
            int lvt_1_2_ = 0;
            int lvt_2_2_ = 0;

            for (ChunkCoordIntPair lvt_4_1_ : this.activeChunkSet)
            {
                int lvt_5_1_ = lvt_4_1_.chunkXPos * 16;
                int lvt_6_1_ = lvt_4_1_.chunkZPos * 16;
                this.theProfiler.startSection("getChunk");
                Chunk lvt_7_1_ = this.getChunkFromChunkCoords(lvt_4_1_.chunkXPos, lvt_4_1_.chunkZPos);
                this.playMoodSoundAndCheckLight(lvt_5_1_, lvt_6_1_, lvt_7_1_);
                this.theProfiler.endStartSection("tickChunk");
                lvt_7_1_.func_150804_b(false);
                this.theProfiler.endStartSection("thunder");

                if (this.rand.nextInt(100000) == 0 && this.isRaining() && this.isThundering())
                {
                    this.updateLCG = this.updateLCG * 3 + 1013904223;
                    int lvt_8_1_ = this.updateLCG >> 2;
                    BlockPos lvt_9_1_ = this.adjustPosToNearbyEntity(new BlockPos(lvt_5_1_ + (lvt_8_1_ & 15), 0, lvt_6_1_ + (lvt_8_1_ >> 8 & 15)));

                    if (this.isRainingAt(lvt_9_1_))
                    {
                        this.addWeatherEffect(new EntityLightningBolt(this, (double)lvt_9_1_.getX(), (double)lvt_9_1_.getY(), (double)lvt_9_1_.getZ()));
                    }
                }

                this.theProfiler.endStartSection("iceandsnow");

                if (this.rand.nextInt(16) == 0)
                {
                    this.updateLCG = this.updateLCG * 3 + 1013904223;
                    int lvt_8_2_ = this.updateLCG >> 2;
                    BlockPos lvt_9_2_ = this.getPrecipitationHeight(new BlockPos(lvt_5_1_ + (lvt_8_2_ & 15), 0, lvt_6_1_ + (lvt_8_2_ >> 8 & 15)));
                    BlockPos lvt_10_1_ = lvt_9_2_.down();

                    if (this.canBlockFreezeNoWater(lvt_10_1_))
                    {
                        this.setBlockState(lvt_10_1_, Blocks.ice.getDefaultState());
                    }

                    if (this.isRaining() && this.canSnowAt(lvt_9_2_, true))
                    {
                        this.setBlockState(lvt_9_2_, Blocks.snow_layer.getDefaultState());
                    }

                    if (this.isRaining() && this.getBiomeGenForCoords(lvt_10_1_).canRain())
                    {
                        this.getBlockState(lvt_10_1_).getBlock().fillWithRain(this, lvt_10_1_);
                    }
                }

                this.theProfiler.endStartSection("tickBlocks");
                int lvt_8_3_ = this.getGameRules().getInt("randomTickSpeed");

                if (lvt_8_3_ > 0)
                {
                    for (ExtendedBlockStorage lvt_12_1_ : lvt_7_1_.getBlockStorageArray())
                    {
                        if (lvt_12_1_ != null && lvt_12_1_.getNeedsRandomTick())
                        {
                            for (int lvt_13_1_ = 0; lvt_13_1_ < lvt_8_3_; ++lvt_13_1_)
                            {
                                this.updateLCG = this.updateLCG * 3 + 1013904223;
                                int lvt_14_1_ = this.updateLCG >> 2;
                                int lvt_15_1_ = lvt_14_1_ & 15;
                                int lvt_16_1_ = lvt_14_1_ >> 8 & 15;
                                int lvt_17_1_ = lvt_14_1_ >> 16 & 15;
                                ++lvt_2_2_;
                                IBlockState lvt_18_1_ = lvt_12_1_.get(lvt_15_1_, lvt_17_1_, lvt_16_1_);
                                Block lvt_19_1_ = lvt_18_1_.getBlock();

                                if (lvt_19_1_.getTickRandomly())
                                {
                                    ++lvt_1_2_;
                                    lvt_19_1_.randomTick(this, new BlockPos(lvt_15_1_ + lvt_5_1_, lvt_17_1_ + lvt_12_1_.getYLocation(), lvt_16_1_ + lvt_6_1_), lvt_18_1_, this.rand);
                                }
                            }
                        }
                    }
                }

                this.theProfiler.endSection();
            }
        }
    }

    protected BlockPos adjustPosToNearbyEntity(BlockPos pos)
    {
        BlockPos lvt_2_1_ = this.getPrecipitationHeight(pos);
        AxisAlignedBB lvt_3_1_ = (new AxisAlignedBB(lvt_2_1_, new BlockPos(lvt_2_1_.getX(), this.getHeight(), lvt_2_1_.getZ()))).expand(3.0D, 3.0D, 3.0D);
        List<EntityLivingBase> lvt_4_1_ = this.getEntitiesWithinAABB(EntityLivingBase.class, lvt_3_1_, new Predicate<EntityLivingBase>()
        {
            public boolean apply(EntityLivingBase p_apply_1_)
            {
                return p_apply_1_ != null && p_apply_1_.isEntityAlive() && WorldServer.this.canSeeSky(p_apply_1_.getPosition());
            }
            public boolean apply(Object p_apply_1_)
            {
                return this.apply((EntityLivingBase)p_apply_1_);
            }
        });
        return !lvt_4_1_.isEmpty() ? ((EntityLivingBase)lvt_4_1_.get(this.rand.nextInt(lvt_4_1_.size()))).getPosition() : lvt_2_1_;
    }

    public boolean isBlockTickPending(BlockPos pos, Block blockType)
    {
        NextTickListEntry lvt_3_1_ = new NextTickListEntry(pos, blockType);
        return this.pendingTickListEntriesThisTick.contains(lvt_3_1_);
    }

    public void scheduleUpdate(BlockPos pos, Block blockIn, int delay)
    {
        this.updateBlockTick(pos, blockIn, delay, 0);
    }

    public void updateBlockTick(BlockPos pos, Block blockIn, int delay, int priority)
    {
        NextTickListEntry lvt_5_1_ = new NextTickListEntry(pos, blockIn);
        int lvt_6_1_ = 0;

        if (this.scheduledUpdatesAreImmediate && blockIn.getMaterial() != Material.air)
        {
            if (blockIn.requiresUpdates())
            {
                lvt_6_1_ = 8;

                if (this.isAreaLoaded(lvt_5_1_.position.add(-lvt_6_1_, -lvt_6_1_, -lvt_6_1_), lvt_5_1_.position.add(lvt_6_1_, lvt_6_1_, lvt_6_1_)))
                {
                    IBlockState lvt_7_1_ = this.getBlockState(lvt_5_1_.position);

                    if (lvt_7_1_.getBlock().getMaterial() != Material.air && lvt_7_1_.getBlock() == lvt_5_1_.getBlock())
                    {
                        lvt_7_1_.getBlock().updateTick(this, lvt_5_1_.position, lvt_7_1_, this.rand);
                    }
                }

                return;
            }

            delay = 1;
        }

        if (this.isAreaLoaded(pos.add(-lvt_6_1_, -lvt_6_1_, -lvt_6_1_), pos.add(lvt_6_1_, lvt_6_1_, lvt_6_1_)))
        {
            if (blockIn.getMaterial() != Material.air)
            {
                lvt_5_1_.setScheduledTime((long)delay + this.worldInfo.getWorldTotalTime());
                lvt_5_1_.setPriority(priority);
            }

            if (!this.pendingTickListEntriesHashSet.contains(lvt_5_1_))
            {
                this.pendingTickListEntriesHashSet.add(lvt_5_1_);
                this.pendingTickListEntriesTreeSet.add(lvt_5_1_);
            }
        }
    }

    public void scheduleBlockUpdate(BlockPos pos, Block blockIn, int delay, int priority)
    {
        NextTickListEntry lvt_5_1_ = new NextTickListEntry(pos, blockIn);
        lvt_5_1_.setPriority(priority);

        if (blockIn.getMaterial() != Material.air)
        {
            lvt_5_1_.setScheduledTime((long)delay + this.worldInfo.getWorldTotalTime());
        }

        if (!this.pendingTickListEntriesHashSet.contains(lvt_5_1_))
        {
            this.pendingTickListEntriesHashSet.add(lvt_5_1_);
            this.pendingTickListEntriesTreeSet.add(lvt_5_1_);
        }
    }

    /**
     * Updates (and cleans up) entities and tile entities
     */
    public void updateEntities()
    {
        if (this.playerEntities.isEmpty())
        {
            if (this.updateEntityTick++ >= 1200)
            {
                return;
            }
        }
        else
        {
            this.resetUpdateEntityTick();
        }

        super.updateEntities();
    }

    /**
     * Resets the updateEntityTick field to 0
     */
    public void resetUpdateEntityTick()
    {
        this.updateEntityTick = 0;
    }

    /**
     * Runs through the list of updates to run and ticks them
     */
    public boolean tickUpdates(boolean p_72955_1_)
    {
        if (this.worldInfo.getTerrainType() == WorldType.DEBUG_WORLD)
        {
            return false;
        }
        else
        {
            int lvt_2_1_ = this.pendingTickListEntriesTreeSet.size();

            if (lvt_2_1_ != this.pendingTickListEntriesHashSet.size())
            {
                throw new IllegalStateException("TickNextTick list out of synch");
            }
            else
            {
                if (lvt_2_1_ > 1000)
                {
                    lvt_2_1_ = 1000;
                }

                this.theProfiler.startSection("cleaning");

                for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_; ++lvt_3_1_)
                {
                    NextTickListEntry lvt_4_1_ = (NextTickListEntry)this.pendingTickListEntriesTreeSet.first();

                    if (!p_72955_1_ && lvt_4_1_.scheduledTime > this.worldInfo.getWorldTotalTime())
                    {
                        break;
                    }

                    this.pendingTickListEntriesTreeSet.remove(lvt_4_1_);
                    this.pendingTickListEntriesHashSet.remove(lvt_4_1_);
                    this.pendingTickListEntriesThisTick.add(lvt_4_1_);
                }

                this.theProfiler.endSection();
                this.theProfiler.startSection("ticking");
                Iterator<NextTickListEntry> lvt_3_2_ = this.pendingTickListEntriesThisTick.iterator();

                while (lvt_3_2_.hasNext())
                {
                    NextTickListEntry lvt_4_2_ = (NextTickListEntry)lvt_3_2_.next();
                    lvt_3_2_.remove();
                    int lvt_5_1_ = 0;

                    if (this.isAreaLoaded(lvt_4_2_.position.add(-lvt_5_1_, -lvt_5_1_, -lvt_5_1_), lvt_4_2_.position.add(lvt_5_1_, lvt_5_1_, lvt_5_1_)))
                    {
                        IBlockState lvt_6_1_ = this.getBlockState(lvt_4_2_.position);

                        if (lvt_6_1_.getBlock().getMaterial() != Material.air && Block.isEqualTo(lvt_6_1_.getBlock(), lvt_4_2_.getBlock()))
                        {
                            try
                            {
                                lvt_6_1_.getBlock().updateTick(this, lvt_4_2_.position, lvt_6_1_, this.rand);
                            }
                            catch (Throwable var10)
                            {
                                CrashReport lvt_8_1_ = CrashReport.makeCrashReport(var10, "Exception while ticking a block");
                                CrashReportCategory lvt_9_1_ = lvt_8_1_.makeCategory("Block being ticked");
                                CrashReportCategory.addBlockInfo(lvt_9_1_, lvt_4_2_.position, lvt_6_1_);
                                throw new ReportedException(lvt_8_1_);
                            }
                        }
                    }
                    else
                    {
                        this.scheduleUpdate(lvt_4_2_.position, lvt_4_2_.getBlock(), 0);
                    }
                }

                this.theProfiler.endSection();
                this.pendingTickListEntriesThisTick.clear();
                return !this.pendingTickListEntriesTreeSet.isEmpty();
            }
        }
    }

    public List<NextTickListEntry> getPendingBlockUpdates(Chunk chunkIn, boolean p_72920_2_)
    {
        ChunkCoordIntPair lvt_3_1_ = chunkIn.getChunkCoordIntPair();
        int lvt_4_1_ = (lvt_3_1_.chunkXPos << 4) - 2;
        int lvt_5_1_ = lvt_4_1_ + 16 + 2;
        int lvt_6_1_ = (lvt_3_1_.chunkZPos << 4) - 2;
        int lvt_7_1_ = lvt_6_1_ + 16 + 2;
        return this.func_175712_a(new StructureBoundingBox(lvt_4_1_, 0, lvt_6_1_, lvt_5_1_, 256, lvt_7_1_), p_72920_2_);
    }

    public List<NextTickListEntry> func_175712_a(StructureBoundingBox structureBB, boolean p_175712_2_)
    {
        List<NextTickListEntry> lvt_3_1_ = null;

        for (int lvt_4_1_ = 0; lvt_4_1_ < 2; ++lvt_4_1_)
        {
            Iterator<NextTickListEntry> lvt_5_1_;

            if (lvt_4_1_ == 0)
            {
                lvt_5_1_ = this.pendingTickListEntriesTreeSet.iterator();
            }
            else
            {
                lvt_5_1_ = this.pendingTickListEntriesThisTick.iterator();
            }

            while (lvt_5_1_.hasNext())
            {
                NextTickListEntry lvt_6_1_ = (NextTickListEntry)lvt_5_1_.next();
                BlockPos lvt_7_1_ = lvt_6_1_.position;

                if (lvt_7_1_.getX() >= structureBB.minX && lvt_7_1_.getX() < structureBB.maxX && lvt_7_1_.getZ() >= structureBB.minZ && lvt_7_1_.getZ() < structureBB.maxZ)
                {
                    if (p_175712_2_)
                    {
                        this.pendingTickListEntriesHashSet.remove(lvt_6_1_);
                        lvt_5_1_.remove();
                    }

                    if (lvt_3_1_ == null)
                    {
                        lvt_3_1_ = Lists.newArrayList();
                    }

                    lvt_3_1_.add(lvt_6_1_);
                }
            }
        }

        return lvt_3_1_;
    }

    /**
     * Will update the entity in the world if the chunk the entity is in is currently loaded or its forced to update.
     * Args: entity, forceUpdate
     */
    public void updateEntityWithOptionalForce(Entity entityIn, boolean forceUpdate)
    {
        if (!this.canSpawnAnimals() && (entityIn instanceof EntityAnimal || entityIn instanceof EntityWaterMob))
        {
            entityIn.setDead();
        }

        if (!this.canSpawnNPCs() && entityIn instanceof INpc)
        {
            entityIn.setDead();
        }

        super.updateEntityWithOptionalForce(entityIn, forceUpdate);
    }

    private boolean canSpawnNPCs()
    {
        return this.mcServer.getCanSpawnNPCs();
    }

    private boolean canSpawnAnimals()
    {
        return this.mcServer.getCanSpawnAnimals();
    }

    /**
     * Creates the chunk provider for this world. Called in the constructor. Retrieves provider from worldProvider?
     */
    protected IChunkProvider createChunkProvider()
    {
        IChunkLoader lvt_1_1_ = this.saveHandler.getChunkLoader(this.provider);
        this.theChunkProviderServer = new ChunkProviderServer(this, lvt_1_1_, this.provider.createChunkGenerator());
        return this.theChunkProviderServer;
    }

    public List<TileEntity> getTileEntitiesIn(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
    {
        List<TileEntity> lvt_7_1_ = Lists.newArrayList();

        for (int lvt_8_1_ = 0; lvt_8_1_ < this.loadedTileEntityList.size(); ++lvt_8_1_)
        {
            TileEntity lvt_9_1_ = (TileEntity)this.loadedTileEntityList.get(lvt_8_1_);
            BlockPos lvt_10_1_ = lvt_9_1_.getPos();

            if (lvt_10_1_.getX() >= minX && lvt_10_1_.getY() >= minY && lvt_10_1_.getZ() >= minZ && lvt_10_1_.getX() < maxX && lvt_10_1_.getY() < maxY && lvt_10_1_.getZ() < maxZ)
            {
                lvt_7_1_.add(lvt_9_1_);
            }
        }

        return lvt_7_1_;
    }

    public boolean isBlockModifiable(EntityPlayer player, BlockPos pos)
    {
        return !this.mcServer.isBlockProtected(this, pos, player) && this.getWorldBorder().contains(pos);
    }

    public void initialize(WorldSettings settings)
    {
        if (!this.worldInfo.isInitialized())
        {
            try
            {
                this.createSpawnPosition(settings);

                if (this.worldInfo.getTerrainType() == WorldType.DEBUG_WORLD)
                {
                    this.setDebugWorldSettings();
                }

                super.initialize(settings);
            }
            catch (Throwable var6)
            {
                CrashReport lvt_3_1_ = CrashReport.makeCrashReport(var6, "Exception initializing level");

                try
                {
                    this.addWorldInfoToCrashReport(lvt_3_1_);
                }
                catch (Throwable var5)
                {
                    ;
                }

                throw new ReportedException(lvt_3_1_);
            }

            this.worldInfo.setServerInitialized(true);
        }
    }

    private void setDebugWorldSettings()
    {
        this.worldInfo.setMapFeaturesEnabled(false);
        this.worldInfo.setAllowCommands(true);
        this.worldInfo.setRaining(false);
        this.worldInfo.setThundering(false);
        this.worldInfo.setCleanWeatherTime(1000000000);
        this.worldInfo.setWorldTime(6000L);
        this.worldInfo.setGameType(WorldSettings.GameType.SPECTATOR);
        this.worldInfo.setHardcore(false);
        this.worldInfo.setDifficulty(EnumDifficulty.PEACEFUL);
        this.worldInfo.setDifficultyLocked(true);
        this.getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
    }

    /**
     * creates a spawn position at random within 256 blocks of 0,0
     */
    private void createSpawnPosition(WorldSettings settings)
    {
        if (!this.provider.canRespawnHere())
        {
            this.worldInfo.setSpawn(BlockPos.ORIGIN.up(this.provider.getAverageGroundLevel()));
        }
        else if (this.worldInfo.getTerrainType() == WorldType.DEBUG_WORLD)
        {
            this.worldInfo.setSpawn(BlockPos.ORIGIN.up());
        }
        else
        {
            this.findingSpawnPoint = true;
            WorldChunkManager lvt_2_1_ = this.provider.getWorldChunkManager();
            List<BiomeGenBase> lvt_3_1_ = lvt_2_1_.getBiomesToSpawnIn();
            Random lvt_4_1_ = new Random(this.getSeed());
            BlockPos lvt_5_1_ = lvt_2_1_.findBiomePosition(0, 0, 256, lvt_3_1_, lvt_4_1_);
            int lvt_6_1_ = 0;
            int lvt_7_1_ = this.provider.getAverageGroundLevel();
            int lvt_8_1_ = 0;

            if (lvt_5_1_ != null)
            {
                lvt_6_1_ = lvt_5_1_.getX();
                lvt_8_1_ = lvt_5_1_.getZ();
            }
            else
            {
                logger.warn("Unable to find spawn biome");
            }

            int lvt_9_1_ = 0;

            while (!this.provider.canCoordinateBeSpawn(lvt_6_1_, lvt_8_1_))
            {
                lvt_6_1_ += lvt_4_1_.nextInt(64) - lvt_4_1_.nextInt(64);
                lvt_8_1_ += lvt_4_1_.nextInt(64) - lvt_4_1_.nextInt(64);
                ++lvt_9_1_;

                if (lvt_9_1_ == 1000)
                {
                    break;
                }
            }

            this.worldInfo.setSpawn(new BlockPos(lvt_6_1_, lvt_7_1_, lvt_8_1_));
            this.findingSpawnPoint = false;

            if (settings.isBonusChestEnabled())
            {
                this.createBonusChest();
            }
        }
    }

    /**
     * Creates the bonus chest in the world.
     */
    protected void createBonusChest()
    {
        WorldGeneratorBonusChest lvt_1_1_ = new WorldGeneratorBonusChest(bonusChestContent, 10);

        for (int lvt_2_1_ = 0; lvt_2_1_ < 10; ++lvt_2_1_)
        {
            int lvt_3_1_ = this.worldInfo.getSpawnX() + this.rand.nextInt(6) - this.rand.nextInt(6);
            int lvt_4_1_ = this.worldInfo.getSpawnZ() + this.rand.nextInt(6) - this.rand.nextInt(6);
            BlockPos lvt_5_1_ = this.getTopSolidOrLiquidBlock(new BlockPos(lvt_3_1_, 0, lvt_4_1_)).up();

            if (lvt_1_1_.generate(this, this.rand, lvt_5_1_))
            {
                break;
            }
        }
    }

    /**
     * Returns null for anything other than the End
     */
    public BlockPos getSpawnCoordinate()
    {
        return this.provider.getSpawnCoordinate();
    }

    /**
     * Saves all chunks to disk while updating progress bar.
     */
    public void saveAllChunks(boolean p_73044_1_, IProgressUpdate progressCallback) throws MinecraftException
    {
        if (this.chunkProvider.canSave())
        {
            if (progressCallback != null)
            {
                progressCallback.displaySavingString("Saving level");
            }

            this.saveLevel();

            if (progressCallback != null)
            {
                progressCallback.displayLoadingString("Saving chunks");
            }

            this.chunkProvider.saveChunks(p_73044_1_, progressCallback);

            for (Chunk lvt_5_1_ : Lists.newArrayList(this.theChunkProviderServer.func_152380_a()))
            {
                if (lvt_5_1_ != null && !this.thePlayerManager.hasPlayerInstance(lvt_5_1_.xPosition, lvt_5_1_.zPosition))
                {
                    this.theChunkProviderServer.dropChunk(lvt_5_1_.xPosition, lvt_5_1_.zPosition);
                }
            }
        }
    }

    /**
     * saves chunk data - currently only called during execution of the Save All command
     */
    public void saveChunkData()
    {
        if (this.chunkProvider.canSave())
        {
            this.chunkProvider.saveExtraData();
        }
    }

    /**
     * Saves the chunks to disk.
     */
    protected void saveLevel() throws MinecraftException
    {
        this.checkSessionLock();
        this.worldInfo.setBorderSize(this.getWorldBorder().getDiameter());
        this.worldInfo.getBorderCenterX(this.getWorldBorder().getCenterX());
        this.worldInfo.getBorderCenterZ(this.getWorldBorder().getCenterZ());
        this.worldInfo.setBorderSafeZone(this.getWorldBorder().getDamageBuffer());
        this.worldInfo.setBorderDamagePerBlock(this.getWorldBorder().getDamageAmount());
        this.worldInfo.setBorderWarningDistance(this.getWorldBorder().getWarningDistance());
        this.worldInfo.setBorderWarningTime(this.getWorldBorder().getWarningTime());
        this.worldInfo.setBorderLerpTarget(this.getWorldBorder().getTargetSize());
        this.worldInfo.setBorderLerpTime(this.getWorldBorder().getTimeUntilTarget());
        this.saveHandler.saveWorldInfoWithPlayer(this.worldInfo, this.mcServer.getConfigurationManager().getHostPlayerData());
        this.mapStorage.saveAllData();
    }

    protected void onEntityAdded(Entity entityIn)
    {
        super.onEntityAdded(entityIn);
        this.entitiesById.addKey(entityIn.getEntityId(), entityIn);
        this.entitiesByUuid.put(entityIn.getUniqueID(), entityIn);
        Entity[] lvt_2_1_ = entityIn.getParts();

        if (lvt_2_1_ != null)
        {
            for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.length; ++lvt_3_1_)
            {
                this.entitiesById.addKey(lvt_2_1_[lvt_3_1_].getEntityId(), lvt_2_1_[lvt_3_1_]);
            }
        }
    }

    protected void onEntityRemoved(Entity entityIn)
    {
        super.onEntityRemoved(entityIn);
        this.entitiesById.removeObject(entityIn.getEntityId());
        this.entitiesByUuid.remove(entityIn.getUniqueID());
        Entity[] lvt_2_1_ = entityIn.getParts();

        if (lvt_2_1_ != null)
        {
            for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.length; ++lvt_3_1_)
            {
                this.entitiesById.removeObject(lvt_2_1_[lvt_3_1_].getEntityId());
            }
        }
    }

    /**
     * adds a lightning bolt to the list of lightning bolts in this world.
     */
    public boolean addWeatherEffect(Entity entityIn)
    {
        if (super.addWeatherEffect(entityIn))
        {
            this.mcServer.getConfigurationManager().sendToAllNear(entityIn.posX, entityIn.posY, entityIn.posZ, 512.0D, this.provider.getDimensionId(), new S2CPacketSpawnGlobalEntity(entityIn));
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * sends a Packet 38 (Entity Status) to all tracked players of that entity
     */
    public void setEntityState(Entity entityIn, byte state)
    {
        this.getEntityTracker().func_151248_b(entityIn, new S19PacketEntityStatus(entityIn, state));
    }

    /**
     * returns a new explosion. Does initiation (at time of writing Explosion is not finished)
     */
    public Explosion newExplosion(Entity entityIn, double x, double y, double z, float strength, boolean isFlaming, boolean isSmoking)
    {
        Explosion lvt_11_1_ = new Explosion(this, entityIn, x, y, z, strength, isFlaming, isSmoking);
        lvt_11_1_.doExplosionA();
        lvt_11_1_.doExplosionB(false);

        if (!isSmoking)
        {
            lvt_11_1_.clearAffectedBlockPositions();
        }

        for (EntityPlayer lvt_13_1_ : this.playerEntities)
        {
            if (lvt_13_1_.getDistanceSq(x, y, z) < 4096.0D)
            {
                ((EntityPlayerMP)lvt_13_1_).playerNetServerHandler.sendPacket(new S27PacketExplosion(x, y, z, strength, lvt_11_1_.getAffectedBlockPositions(), (Vec3)lvt_11_1_.getPlayerKnockbackMap().get(lvt_13_1_)));
            }
        }

        return lvt_11_1_;
    }

    public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam)
    {
        BlockEventData lvt_5_1_ = new BlockEventData(pos, blockIn, eventID, eventParam);

        for (BlockEventData lvt_7_1_ : this.blockEventQueue[this.blockEventCacheIndex])
        {
            if (lvt_7_1_.equals(lvt_5_1_))
            {
                return;
            }
        }

        this.blockEventQueue[this.blockEventCacheIndex].add(lvt_5_1_);
    }

    private void sendQueuedBlockEvents()
    {
        while (!this.blockEventQueue[this.blockEventCacheIndex].isEmpty())
        {
            int lvt_1_1_ = this.blockEventCacheIndex;
            this.blockEventCacheIndex ^= 1;

            for (BlockEventData lvt_3_1_ : this.blockEventQueue[lvt_1_1_])
            {
                if (this.fireBlockEvent(lvt_3_1_))
                {
                    this.mcServer.getConfigurationManager().sendToAllNear((double)lvt_3_1_.getPosition().getX(), (double)lvt_3_1_.getPosition().getY(), (double)lvt_3_1_.getPosition().getZ(), 64.0D, this.provider.getDimensionId(), new S24PacketBlockAction(lvt_3_1_.getPosition(), lvt_3_1_.getBlock(), lvt_3_1_.getEventID(), lvt_3_1_.getEventParameter()));
                }
            }

            this.blockEventQueue[lvt_1_1_].clear();
        }
    }

    private boolean fireBlockEvent(BlockEventData event)
    {
        IBlockState lvt_2_1_ = this.getBlockState(event.getPosition());
        return lvt_2_1_.getBlock() == event.getBlock() ? lvt_2_1_.getBlock().onBlockEventReceived(this, event.getPosition(), lvt_2_1_, event.getEventID(), event.getEventParameter()) : false;
    }

    /**
     * Syncs all changes to disk and wait for completion.
     */
    public void flush()
    {
        this.saveHandler.flush();
    }

    /**
     * Updates all weather states.
     */
    protected void updateWeather()
    {
        boolean lvt_1_1_ = this.isRaining();
        super.updateWeather();

        if (this.prevRainingStrength != this.rainingStrength)
        {
            this.mcServer.getConfigurationManager().sendPacketToAllPlayersInDimension(new S2BPacketChangeGameState(7, this.rainingStrength), this.provider.getDimensionId());
        }

        if (this.prevThunderingStrength != this.thunderingStrength)
        {
            this.mcServer.getConfigurationManager().sendPacketToAllPlayersInDimension(new S2BPacketChangeGameState(8, this.thunderingStrength), this.provider.getDimensionId());
        }

        if (lvt_1_1_ != this.isRaining())
        {
            if (lvt_1_1_)
            {
                this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(2, 0.0F));
            }
            else
            {
                this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(1, 0.0F));
            }

            this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(7, this.rainingStrength));
            this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(8, this.thunderingStrength));
        }
    }

    protected int getRenderDistanceChunks()
    {
        return this.mcServer.getConfigurationManager().getViewDistance();
    }

    public MinecraftServer getMinecraftServer()
    {
        return this.mcServer;
    }

    /**
     * Gets the EntityTracker
     */
    public EntityTracker getEntityTracker()
    {
        return this.theEntityTracker;
    }

    public PlayerManager getPlayerManager()
    {
        return this.thePlayerManager;
    }

    public Teleporter getDefaultTeleporter()
    {
        return this.worldTeleporter;
    }

    /**
     * Spawns the desired particle and sends the necessary packets to the relevant connected players.
     */
    public void spawnParticle(EnumParticleTypes particleType, double xCoord, double yCoord, double zCoord, int numberOfParticles, double xOffset, double yOffset, double zOffset, double particleSpeed, int... particleArguments)
    {
        this.spawnParticle(particleType, false, xCoord, yCoord, zCoord, numberOfParticles, xOffset, yOffset, zOffset, particleSpeed, particleArguments);
    }

    /**
     * Spawns the desired particle and sends the necessary packets to the relevant connected players.
     */
    public void spawnParticle(EnumParticleTypes particleType, boolean longDistance, double xCoord, double yCoord, double zCoord, int numberOfParticles, double xOffset, double yOffset, double zOffset, double particleSpeed, int... particleArguments)
    {
        Packet lvt_19_1_ = new S2APacketParticles(particleType, longDistance, (float)xCoord, (float)yCoord, (float)zCoord, (float)xOffset, (float)yOffset, (float)zOffset, (float)particleSpeed, numberOfParticles, particleArguments);

        for (int lvt_20_1_ = 0; lvt_20_1_ < this.playerEntities.size(); ++lvt_20_1_)
        {
            EntityPlayerMP lvt_21_1_ = (EntityPlayerMP)this.playerEntities.get(lvt_20_1_);
            BlockPos lvt_22_1_ = lvt_21_1_.getPosition();
            double lvt_23_1_ = lvt_22_1_.distanceSq(xCoord, yCoord, zCoord);

            if (lvt_23_1_ <= 256.0D || longDistance && lvt_23_1_ <= 65536.0D)
            {
                lvt_21_1_.playerNetServerHandler.sendPacket(lvt_19_1_);
            }
        }
    }

    public Entity getEntityFromUuid(UUID uuid)
    {
        return (Entity)this.entitiesByUuid.get(uuid);
    }

    public ListenableFuture<Object> addScheduledTask(Runnable runnableToSchedule)
    {
        return this.mcServer.addScheduledTask(runnableToSchedule);
    }

    public boolean isCallingFromMinecraftThread()
    {
        return this.mcServer.isCallingFromMinecraftThread();
    }

    static class ServerBlockEventList extends ArrayList<BlockEventData>
    {
        private ServerBlockEventList()
        {
        }
    }
}
