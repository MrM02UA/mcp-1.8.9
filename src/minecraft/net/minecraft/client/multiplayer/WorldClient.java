package net.minecraft.client.multiplayer;

import com.google.common.collect.Sets;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecart;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EntityFirework;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.SaveDataMemoryStorage;
import net.minecraft.world.storage.SaveHandlerMP;
import net.minecraft.world.storage.WorldInfo;

public class WorldClient extends World
{
    /** The packets that need to be sent to the server. */
    private NetHandlerPlayClient sendQueue;

    /** The ChunkProviderClient instance */
    private ChunkProviderClient clientChunkProvider;
    private final Set<Entity> entityList = Sets.newHashSet();
    private final Set<Entity> entitySpawnQueue = Sets.newHashSet();
    private final Minecraft mc = Minecraft.getMinecraft();
    private final Set<ChunkCoordIntPair> previousActiveChunkSet = Sets.newHashSet();

    public WorldClient(NetHandlerPlayClient netHandler, WorldSettings settings, int dimension, EnumDifficulty difficulty, Profiler profilerIn)
    {
        super(new SaveHandlerMP(), new WorldInfo(settings, "MpServer"), WorldProvider.getProviderForDimension(dimension), profilerIn, true);
        this.sendQueue = netHandler;
        this.getWorldInfo().setDifficulty(difficulty);
        this.setSpawnPoint(new BlockPos(8, 64, 8));
        this.provider.registerWorld(this);
        this.chunkProvider = this.createChunkProvider();
        this.mapStorage = new SaveDataMemoryStorage();
        this.calculateInitialSkylight();
        this.calculateInitialWeather();
    }

    /**
     * Runs a single tick for the world
     */
    public void tick()
    {
        super.tick();
        this.setTotalWorldTime(this.getTotalWorldTime() + 1L);

        if (this.getGameRules().getBoolean("doDaylightCycle"))
        {
            this.setWorldTime(this.getWorldTime() + 1L);
        }

        this.theProfiler.startSection("reEntryProcessing");

        for (int lvt_1_1_ = 0; lvt_1_1_ < 10 && !this.entitySpawnQueue.isEmpty(); ++lvt_1_1_)
        {
            Entity lvt_2_1_ = (Entity)this.entitySpawnQueue.iterator().next();
            this.entitySpawnQueue.remove(lvt_2_1_);

            if (!this.loadedEntityList.contains(lvt_2_1_))
            {
                this.spawnEntityInWorld(lvt_2_1_);
            }
        }

        this.theProfiler.endStartSection("chunkCache");
        this.clientChunkProvider.unloadQueuedChunks();
        this.theProfiler.endStartSection("blocks");
        this.updateBlocks();
        this.theProfiler.endSection();
    }

    /**
     * Invalidates an AABB region of blocks from the receive queue, in the event that the block has been modified
     * client-side in the intervening 80 receive ticks.
     *  
     * @param x1 X position of the block where the region begin
     * @param y1 Y position of the block where the region begin
     * @param z1 Z position of the block where the region begin
     * @param x2 X position of the block where the region end
     * @param y2 Y position of the block where the region end
     * @param z2 Z position of the block where the region end
     */
    public void invalidateBlockReceiveRegion(int x1, int y1, int z1, int x2, int y2, int z2)
    {
    }

    /**
     * Creates the chunk provider for this world. Called in the constructor. Retrieves provider from worldProvider?
     */
    protected IChunkProvider createChunkProvider()
    {
        this.clientChunkProvider = new ChunkProviderClient(this);
        return this.clientChunkProvider;
    }

    protected void updateBlocks()
    {
        super.updateBlocks();
        this.previousActiveChunkSet.retainAll(this.activeChunkSet);

        if (this.previousActiveChunkSet.size() == this.activeChunkSet.size())
        {
            this.previousActiveChunkSet.clear();
        }

        int lvt_1_1_ = 0;

        for (ChunkCoordIntPair lvt_3_1_ : this.activeChunkSet)
        {
            if (!this.previousActiveChunkSet.contains(lvt_3_1_))
            {
                int lvt_4_1_ = lvt_3_1_.chunkXPos * 16;
                int lvt_5_1_ = lvt_3_1_.chunkZPos * 16;
                this.theProfiler.startSection("getChunk");
                Chunk lvt_6_1_ = this.getChunkFromChunkCoords(lvt_3_1_.chunkXPos, lvt_3_1_.chunkZPos);
                this.playMoodSoundAndCheckLight(lvt_4_1_, lvt_5_1_, lvt_6_1_);
                this.theProfiler.endSection();
                this.previousActiveChunkSet.add(lvt_3_1_);
                ++lvt_1_1_;

                if (lvt_1_1_ >= 10)
                {
                    return;
                }
            }
        }
    }

    public void doPreChunk(int chuncX, int chuncZ, boolean loadChunk)
    {
        if (loadChunk)
        {
            this.clientChunkProvider.loadChunk(chuncX, chuncZ);
        }
        else
        {
            this.clientChunkProvider.unloadChunk(chuncX, chuncZ);
        }

        if (!loadChunk)
        {
            this.markBlockRangeForRenderUpdate(chuncX * 16, 0, chuncZ * 16, chuncX * 16 + 15, 256, chuncZ * 16 + 15);
        }
    }

    /**
     * Called when an entity is spawned in the world. This includes players.
     */
    public boolean spawnEntityInWorld(Entity entityIn)
    {
        boolean lvt_2_1_ = super.spawnEntityInWorld(entityIn);
        this.entityList.add(entityIn);

        if (!lvt_2_1_)
        {
            this.entitySpawnQueue.add(entityIn);
        }
        else if (entityIn instanceof EntityMinecart)
        {
            this.mc.getSoundHandler().playSound(new MovingSoundMinecart((EntityMinecart)entityIn));
        }

        return lvt_2_1_;
    }

    /**
     * Schedule the entity for removal during the next tick. Marks the entity dead in anticipation.
     */
    public void removeEntity(Entity entityIn)
    {
        super.removeEntity(entityIn);
        this.entityList.remove(entityIn);
    }

    protected void onEntityAdded(Entity entityIn)
    {
        super.onEntityAdded(entityIn);

        if (this.entitySpawnQueue.contains(entityIn))
        {
            this.entitySpawnQueue.remove(entityIn);
        }
    }

    protected void onEntityRemoved(Entity entityIn)
    {
        super.onEntityRemoved(entityIn);
        boolean lvt_2_1_ = false;

        if (this.entityList.contains(entityIn))
        {
            if (entityIn.isEntityAlive())
            {
                this.entitySpawnQueue.add(entityIn);
                lvt_2_1_ = true;
            }
            else
            {
                this.entityList.remove(entityIn);
            }
        }
    }

    /**
     * Add an ID to Entity mapping to entityHashSet
     *  
     * @param entityID The ID to give to the entity to spawn
     * @param entityToSpawn The Entity to spawn in the World
     */
    public void addEntityToWorld(int entityID, Entity entityToSpawn)
    {
        Entity lvt_3_1_ = this.getEntityByID(entityID);

        if (lvt_3_1_ != null)
        {
            this.removeEntity(lvt_3_1_);
        }

        this.entityList.add(entityToSpawn);
        entityToSpawn.setEntityId(entityID);

        if (!this.spawnEntityInWorld(entityToSpawn))
        {
            this.entitySpawnQueue.add(entityToSpawn);
        }

        this.entitiesById.addKey(entityID, entityToSpawn);
    }

    /**
     * Returns the Entity with the given ID, or null if it doesn't exist in this World.
     */
    public Entity getEntityByID(int id)
    {
        return (Entity)(id == this.mc.thePlayer.getEntityId() ? this.mc.thePlayer : super.getEntityByID(id));
    }

    public Entity removeEntityFromWorld(int entityID)
    {
        Entity lvt_2_1_ = (Entity)this.entitiesById.removeObject(entityID);

        if (lvt_2_1_ != null)
        {
            this.entityList.remove(lvt_2_1_);
            this.removeEntity(lvt_2_1_);
        }

        return lvt_2_1_;
    }

    public boolean invalidateRegionAndSetBlock(BlockPos pos, IBlockState state)
    {
        int lvt_3_1_ = pos.getX();
        int lvt_4_1_ = pos.getY();
        int lvt_5_1_ = pos.getZ();
        this.invalidateBlockReceiveRegion(lvt_3_1_, lvt_4_1_, lvt_5_1_, lvt_3_1_, lvt_4_1_, lvt_5_1_);
        return super.setBlockState(pos, state, 3);
    }

    /**
     * If on MP, sends a quitting packet.
     */
    public void sendQuittingDisconnectingPacket()
    {
        this.sendQueue.getNetworkManager().closeChannel(new ChatComponentText("Quitting"));
    }

    /**
     * Updates all weather states.
     */
    protected void updateWeather()
    {
    }

    protected int getRenderDistanceChunks()
    {
        return this.mc.gameSettings.renderDistanceChunks;
    }

    public void doVoidFogParticles(int posX, int posY, int posZ)
    {
        int lvt_4_1_ = 16;
        Random lvt_5_1_ = new Random();
        ItemStack lvt_6_1_ = this.mc.thePlayer.getHeldItem();
        boolean lvt_7_1_ = this.mc.playerController.getCurrentGameType() == WorldSettings.GameType.CREATIVE && lvt_6_1_ != null && Block.getBlockFromItem(lvt_6_1_.getItem()) == Blocks.barrier;
        BlockPos.MutableBlockPos lvt_8_1_ = new BlockPos.MutableBlockPos();

        for (int lvt_9_1_ = 0; lvt_9_1_ < 1000; ++lvt_9_1_)
        {
            int lvt_10_1_ = posX + this.rand.nextInt(lvt_4_1_) - this.rand.nextInt(lvt_4_1_);
            int lvt_11_1_ = posY + this.rand.nextInt(lvt_4_1_) - this.rand.nextInt(lvt_4_1_);
            int lvt_12_1_ = posZ + this.rand.nextInt(lvt_4_1_) - this.rand.nextInt(lvt_4_1_);
            lvt_8_1_.set(lvt_10_1_, lvt_11_1_, lvt_12_1_);
            IBlockState lvt_13_1_ = this.getBlockState(lvt_8_1_);
            lvt_13_1_.getBlock().randomDisplayTick(this, lvt_8_1_, lvt_13_1_, lvt_5_1_);

            if (lvt_7_1_ && lvt_13_1_.getBlock() == Blocks.barrier)
            {
                this.spawnParticle(EnumParticleTypes.BARRIER, (double)((float)lvt_10_1_ + 0.5F), (double)((float)lvt_11_1_ + 0.5F), (double)((float)lvt_12_1_ + 0.5F), 0.0D, 0.0D, 0.0D, new int[0]);
            }
        }
    }

    /**
     * also releases skins.
     */
    public void removeAllEntities()
    {
        this.loadedEntityList.removeAll(this.unloadedEntityList);

        for (int lvt_1_1_ = 0; lvt_1_1_ < this.unloadedEntityList.size(); ++lvt_1_1_)
        {
            Entity lvt_2_1_ = (Entity)this.unloadedEntityList.get(lvt_1_1_);
            int lvt_3_1_ = lvt_2_1_.chunkCoordX;
            int lvt_4_1_ = lvt_2_1_.chunkCoordZ;

            if (lvt_2_1_.addedToChunk && this.isChunkLoaded(lvt_3_1_, lvt_4_1_, true))
            {
                this.getChunkFromChunkCoords(lvt_3_1_, lvt_4_1_).removeEntity(lvt_2_1_);
            }
        }

        for (int lvt_1_2_ = 0; lvt_1_2_ < this.unloadedEntityList.size(); ++lvt_1_2_)
        {
            this.onEntityRemoved((Entity)this.unloadedEntityList.get(lvt_1_2_));
        }

        this.unloadedEntityList.clear();

        for (int lvt_1_3_ = 0; lvt_1_3_ < this.loadedEntityList.size(); ++lvt_1_3_)
        {
            Entity lvt_2_2_ = (Entity)this.loadedEntityList.get(lvt_1_3_);

            if (lvt_2_2_.ridingEntity != null)
            {
                if (!lvt_2_2_.ridingEntity.isDead && lvt_2_2_.ridingEntity.riddenByEntity == lvt_2_2_)
                {
                    continue;
                }

                lvt_2_2_.ridingEntity.riddenByEntity = null;
                lvt_2_2_.ridingEntity = null;
            }

            if (lvt_2_2_.isDead)
            {
                int lvt_3_2_ = lvt_2_2_.chunkCoordX;
                int lvt_4_2_ = lvt_2_2_.chunkCoordZ;

                if (lvt_2_2_.addedToChunk && this.isChunkLoaded(lvt_3_2_, lvt_4_2_, true))
                {
                    this.getChunkFromChunkCoords(lvt_3_2_, lvt_4_2_).removeEntity(lvt_2_2_);
                }

                this.loadedEntityList.remove(lvt_1_3_--);
                this.onEntityRemoved(lvt_2_2_);
            }
        }
    }

    /**
     * Adds some basic stats of the world to the given crash report.
     */
    public CrashReportCategory addWorldInfoToCrashReport(CrashReport report)
    {
        CrashReportCategory lvt_2_1_ = super.addWorldInfoToCrashReport(report);
        lvt_2_1_.addCrashSectionCallable("Forced entities", new Callable<String>()
        {
            public String call()
            {
                return WorldClient.this.entityList.size() + " total; " + WorldClient.this.entityList.toString();
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        lvt_2_1_.addCrashSectionCallable("Retry entities", new Callable<String>()
        {
            public String call()
            {
                return WorldClient.this.entitySpawnQueue.size() + " total; " + WorldClient.this.entitySpawnQueue.toString();
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        lvt_2_1_.addCrashSectionCallable("Server brand", new Callable<String>()
        {
            public String call() throws Exception
            {
                return WorldClient.this.mc.thePlayer.getClientBrand();
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        lvt_2_1_.addCrashSectionCallable("Server type", new Callable<String>()
        {
            public String call() throws Exception
            {
                return WorldClient.this.mc.getIntegratedServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server";
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        return lvt_2_1_;
    }

    /**
     * Plays a sound at the specified position.
     *  
     * @param pos The position where to play the sound
     * @param soundName The name of the sound to play
     * @param volume The volume of the sound
     * @param pitch The pitch of the sound
     * @param distanceDelay True if the sound is delayed over distance
     */
    public void playSoundAtPos(BlockPos pos, String soundName, float volume, float pitch, boolean distanceDelay)
    {
        this.playSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, soundName, volume, pitch, distanceDelay);
    }

    /**
     * par8 is loudness, all pars passed to minecraftInstance.sndManager.playSound
     */
    public void playSound(double x, double y, double z, String soundName, float volume, float pitch, boolean distanceDelay)
    {
        double lvt_11_1_ = this.mc.getRenderViewEntity().getDistanceSq(x, y, z);
        PositionedSoundRecord lvt_13_1_ = new PositionedSoundRecord(new ResourceLocation(soundName), volume, pitch, (float)x, (float)y, (float)z);

        if (distanceDelay && lvt_11_1_ > 100.0D)
        {
            double lvt_14_1_ = Math.sqrt(lvt_11_1_) / 40.0D;
            this.mc.getSoundHandler().playDelayedSound(lvt_13_1_, (int)(lvt_14_1_ * 20.0D));
        }
        else
        {
            this.mc.getSoundHandler().playSound(lvt_13_1_);
        }
    }

    public void makeFireworks(double x, double y, double z, double motionX, double motionY, double motionZ, NBTTagCompound compund)
    {
        this.mc.effectRenderer.addEffect(new EntityFirework.StarterFX(this, x, y, z, motionX, motionY, motionZ, this.mc.effectRenderer, compund));
    }

    public void setWorldScoreboard(Scoreboard scoreboardIn)
    {
        this.worldScoreboard = scoreboardIn;
    }

    /**
     * Sets the world time.
     */
    public void setWorldTime(long time)
    {
        if (time < 0L)
        {
            time = -time;
            this.getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
        }
        else
        {
            this.getGameRules().setOrCreateGameRule("doDaylightCycle", "true");
        }

        super.setWorldTime(time);
    }
}
