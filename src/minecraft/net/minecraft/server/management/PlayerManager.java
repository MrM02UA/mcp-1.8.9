package net.minecraft.server.management;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerManager
{
    private static final Logger pmLogger = LogManager.getLogger();
    private final WorldServer theWorldServer;
    private final List<EntityPlayerMP> players = Lists.newArrayList();
    private final LongHashMap<PlayerManager.PlayerInstance> playerInstances = new LongHashMap();
    private final List<PlayerManager.PlayerInstance> playerInstancesToUpdate = Lists.newArrayList();
    private final List<PlayerManager.PlayerInstance> playerInstanceList = Lists.newArrayList();

    /**
     * Number of chunks the server sends to the client. Valid 3<=x<=15. In server.properties.
     */
    private int playerViewRadius;

    /** time what is using to check if InhabitedTime should be calculated */
    private long previousTotalWorldTime;

    /** x, z direction vectors: east, south, west, north */
    private final int[][] xzDirectionsConst = new int[][] {{1, 0}, {0, 1}, { -1, 0}, {0, -1}};

    public PlayerManager(WorldServer serverWorld)
    {
        this.theWorldServer = serverWorld;
        this.setPlayerViewRadius(serverWorld.getMinecraftServer().getConfigurationManager().getViewDistance());
    }

    /**
     * Returns the WorldServer associated with this PlayerManager
     */
    public WorldServer getWorldServer()
    {
        return this.theWorldServer;
    }

    /**
     * updates all the player instances that need to be updated
     */
    public void updatePlayerInstances()
    {
        long lvt_1_1_ = this.theWorldServer.getTotalWorldTime();

        if (lvt_1_1_ - this.previousTotalWorldTime > 8000L)
        {
            this.previousTotalWorldTime = lvt_1_1_;

            for (int lvt_3_1_ = 0; lvt_3_1_ < this.playerInstanceList.size(); ++lvt_3_1_)
            {
                PlayerManager.PlayerInstance lvt_4_1_ = (PlayerManager.PlayerInstance)this.playerInstanceList.get(lvt_3_1_);
                lvt_4_1_.onUpdate();
                lvt_4_1_.processChunk();
            }
        }
        else
        {
            for (int lvt_3_2_ = 0; lvt_3_2_ < this.playerInstancesToUpdate.size(); ++lvt_3_2_)
            {
                PlayerManager.PlayerInstance lvt_4_2_ = (PlayerManager.PlayerInstance)this.playerInstancesToUpdate.get(lvt_3_2_);
                lvt_4_2_.onUpdate();
            }
        }

        this.playerInstancesToUpdate.clear();

        if (this.players.isEmpty())
        {
            WorldProvider lvt_3_3_ = this.theWorldServer.provider;

            if (!lvt_3_3_.canRespawnHere())
            {
                this.theWorldServer.theChunkProviderServer.unloadAllChunks();
            }
        }
    }

    public boolean hasPlayerInstance(int chunkX, int chunkZ)
    {
        long lvt_3_1_ = (long)chunkX + 2147483647L | (long)chunkZ + 2147483647L << 32;
        return this.playerInstances.getValueByKey(lvt_3_1_) != null;
    }

    /**
     * passi n the chunk x and y and a flag as to whether or not the instance should be made if it doesnt exist
     */
    private PlayerManager.PlayerInstance getPlayerInstance(int chunkX, int chunkZ, boolean createIfAbsent)
    {
        long lvt_4_1_ = (long)chunkX + 2147483647L | (long)chunkZ + 2147483647L << 32;
        PlayerManager.PlayerInstance lvt_6_1_ = (PlayerManager.PlayerInstance)this.playerInstances.getValueByKey(lvt_4_1_);

        if (lvt_6_1_ == null && createIfAbsent)
        {
            lvt_6_1_ = new PlayerManager.PlayerInstance(chunkX, chunkZ);
            this.playerInstances.add(lvt_4_1_, lvt_6_1_);
            this.playerInstanceList.add(lvt_6_1_);
        }

        return lvt_6_1_;
    }

    public void markBlockForUpdate(BlockPos pos)
    {
        int lvt_2_1_ = pos.getX() >> 4;
        int lvt_3_1_ = pos.getZ() >> 4;
        PlayerManager.PlayerInstance lvt_4_1_ = this.getPlayerInstance(lvt_2_1_, lvt_3_1_, false);

        if (lvt_4_1_ != null)
        {
            lvt_4_1_.flagChunkForUpdate(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
        }
    }

    /**
     * Adds an EntityPlayerMP to the PlayerManager and to all player instances within player visibility
     */
    public void addPlayer(EntityPlayerMP player)
    {
        int lvt_2_1_ = (int)player.posX >> 4;
        int lvt_3_1_ = (int)player.posZ >> 4;
        player.managedPosX = player.posX;
        player.managedPosZ = player.posZ;

        for (int lvt_4_1_ = lvt_2_1_ - this.playerViewRadius; lvt_4_1_ <= lvt_2_1_ + this.playerViewRadius; ++lvt_4_1_)
        {
            for (int lvt_5_1_ = lvt_3_1_ - this.playerViewRadius; lvt_5_1_ <= lvt_3_1_ + this.playerViewRadius; ++lvt_5_1_)
            {
                this.getPlayerInstance(lvt_4_1_, lvt_5_1_, true).addPlayer(player);
            }
        }

        this.players.add(player);
        this.filterChunkLoadQueue(player);
    }

    /**
     * Removes all chunks from the given player's chunk load queue that are not in viewing range of the player.
     */
    public void filterChunkLoadQueue(EntityPlayerMP player)
    {
        List<ChunkCoordIntPair> lvt_2_1_ = Lists.newArrayList(player.loadedChunks);
        int lvt_3_1_ = 0;
        int lvt_4_1_ = this.playerViewRadius;
        int lvt_5_1_ = (int)player.posX >> 4;
        int lvt_6_1_ = (int)player.posZ >> 4;
        int lvt_7_1_ = 0;
        int lvt_8_1_ = 0;
        ChunkCoordIntPair lvt_9_1_ = this.getPlayerInstance(lvt_5_1_, lvt_6_1_, true).chunkCoords;
        player.loadedChunks.clear();

        if (lvt_2_1_.contains(lvt_9_1_))
        {
            player.loadedChunks.add(lvt_9_1_);
        }

        for (int lvt_10_1_ = 1; lvt_10_1_ <= lvt_4_1_ * 2; ++lvt_10_1_)
        {
            for (int lvt_11_1_ = 0; lvt_11_1_ < 2; ++lvt_11_1_)
            {
                int[] lvt_12_1_ = this.xzDirectionsConst[lvt_3_1_++ % 4];

                for (int lvt_13_1_ = 0; lvt_13_1_ < lvt_10_1_; ++lvt_13_1_)
                {
                    lvt_7_1_ += lvt_12_1_[0];
                    lvt_8_1_ += lvt_12_1_[1];
                    lvt_9_1_ = this.getPlayerInstance(lvt_5_1_ + lvt_7_1_, lvt_6_1_ + lvt_8_1_, true).chunkCoords;

                    if (lvt_2_1_.contains(lvt_9_1_))
                    {
                        player.loadedChunks.add(lvt_9_1_);
                    }
                }
            }
        }

        lvt_3_1_ = lvt_3_1_ % 4;

        for (int lvt_10_2_ = 0; lvt_10_2_ < lvt_4_1_ * 2; ++lvt_10_2_)
        {
            lvt_7_1_ += this.xzDirectionsConst[lvt_3_1_][0];
            lvt_8_1_ += this.xzDirectionsConst[lvt_3_1_][1];
            lvt_9_1_ = this.getPlayerInstance(lvt_5_1_ + lvt_7_1_, lvt_6_1_ + lvt_8_1_, true).chunkCoords;

            if (lvt_2_1_.contains(lvt_9_1_))
            {
                player.loadedChunks.add(lvt_9_1_);
            }
        }
    }

    /**
     * Removes an EntityPlayerMP from the PlayerManager.
     */
    public void removePlayer(EntityPlayerMP player)
    {
        int lvt_2_1_ = (int)player.managedPosX >> 4;
        int lvt_3_1_ = (int)player.managedPosZ >> 4;

        for (int lvt_4_1_ = lvt_2_1_ - this.playerViewRadius; lvt_4_1_ <= lvt_2_1_ + this.playerViewRadius; ++lvt_4_1_)
        {
            for (int lvt_5_1_ = lvt_3_1_ - this.playerViewRadius; lvt_5_1_ <= lvt_3_1_ + this.playerViewRadius; ++lvt_5_1_)
            {
                PlayerManager.PlayerInstance lvt_6_1_ = this.getPlayerInstance(lvt_4_1_, lvt_5_1_, false);

                if (lvt_6_1_ != null)
                {
                    lvt_6_1_.removePlayer(player);
                }
            }
        }

        this.players.remove(player);
    }

    /**
     * Determine if two rectangles centered at the given points overlap for the provided radius. Arguments: x1, z1, x2,
     * z2, radius.
     */
    private boolean overlaps(int x1, int z1, int x2, int z2, int radius)
    {
        int lvt_6_1_ = x1 - x2;
        int lvt_7_1_ = z1 - z2;
        return lvt_6_1_ >= -radius && lvt_6_1_ <= radius ? lvt_7_1_ >= -radius && lvt_7_1_ <= radius : false;
    }

    /**
     * update chunks around a player being moved by server logic (e.g. cart, boat)
     */
    public void updateMountedMovingPlayer(EntityPlayerMP player)
    {
        int lvt_2_1_ = (int)player.posX >> 4;
        int lvt_3_1_ = (int)player.posZ >> 4;
        double lvt_4_1_ = player.managedPosX - player.posX;
        double lvt_6_1_ = player.managedPosZ - player.posZ;
        double lvt_8_1_ = lvt_4_1_ * lvt_4_1_ + lvt_6_1_ * lvt_6_1_;

        if (lvt_8_1_ >= 64.0D)
        {
            int lvt_10_1_ = (int)player.managedPosX >> 4;
            int lvt_11_1_ = (int)player.managedPosZ >> 4;
            int lvt_12_1_ = this.playerViewRadius;
            int lvt_13_1_ = lvt_2_1_ - lvt_10_1_;
            int lvt_14_1_ = lvt_3_1_ - lvt_11_1_;

            if (lvt_13_1_ != 0 || lvt_14_1_ != 0)
            {
                for (int lvt_15_1_ = lvt_2_1_ - lvt_12_1_; lvt_15_1_ <= lvt_2_1_ + lvt_12_1_; ++lvt_15_1_)
                {
                    for (int lvt_16_1_ = lvt_3_1_ - lvt_12_1_; lvt_16_1_ <= lvt_3_1_ + lvt_12_1_; ++lvt_16_1_)
                    {
                        if (!this.overlaps(lvt_15_1_, lvt_16_1_, lvt_10_1_, lvt_11_1_, lvt_12_1_))
                        {
                            this.getPlayerInstance(lvt_15_1_, lvt_16_1_, true).addPlayer(player);
                        }

                        if (!this.overlaps(lvt_15_1_ - lvt_13_1_, lvt_16_1_ - lvt_14_1_, lvt_2_1_, lvt_3_1_, lvt_12_1_))
                        {
                            PlayerManager.PlayerInstance lvt_17_1_ = this.getPlayerInstance(lvt_15_1_ - lvt_13_1_, lvt_16_1_ - lvt_14_1_, false);

                            if (lvt_17_1_ != null)
                            {
                                lvt_17_1_.removePlayer(player);
                            }
                        }
                    }
                }

                this.filterChunkLoadQueue(player);
                player.managedPosX = player.posX;
                player.managedPosZ = player.posZ;
            }
        }
    }

    public boolean isPlayerWatchingChunk(EntityPlayerMP player, int chunkX, int chunkZ)
    {
        PlayerManager.PlayerInstance lvt_4_1_ = this.getPlayerInstance(chunkX, chunkZ, false);
        return lvt_4_1_ != null && lvt_4_1_.playersWatchingChunk.contains(player) && !player.loadedChunks.contains(lvt_4_1_.chunkCoords);
    }

    public void setPlayerViewRadius(int radius)
    {
        radius = MathHelper.clamp_int(radius, 3, 32);

        if (radius != this.playerViewRadius)
        {
            int lvt_2_1_ = radius - this.playerViewRadius;

            for (EntityPlayerMP lvt_5_1_ : Lists.newArrayList(this.players))
            {
                int lvt_6_1_ = (int)lvt_5_1_.posX >> 4;
                int lvt_7_1_ = (int)lvt_5_1_.posZ >> 4;

                if (lvt_2_1_ > 0)
                {
                    for (int lvt_8_1_ = lvt_6_1_ - radius; lvt_8_1_ <= lvt_6_1_ + radius; ++lvt_8_1_)
                    {
                        for (int lvt_9_1_ = lvt_7_1_ - radius; lvt_9_1_ <= lvt_7_1_ + radius; ++lvt_9_1_)
                        {
                            PlayerManager.PlayerInstance lvt_10_1_ = this.getPlayerInstance(lvt_8_1_, lvt_9_1_, true);

                            if (!lvt_10_1_.playersWatchingChunk.contains(lvt_5_1_))
                            {
                                lvt_10_1_.addPlayer(lvt_5_1_);
                            }
                        }
                    }
                }
                else
                {
                    for (int lvt_8_2_ = lvt_6_1_ - this.playerViewRadius; lvt_8_2_ <= lvt_6_1_ + this.playerViewRadius; ++lvt_8_2_)
                    {
                        for (int lvt_9_2_ = lvt_7_1_ - this.playerViewRadius; lvt_9_2_ <= lvt_7_1_ + this.playerViewRadius; ++lvt_9_2_)
                        {
                            if (!this.overlaps(lvt_8_2_, lvt_9_2_, lvt_6_1_, lvt_7_1_, radius))
                            {
                                this.getPlayerInstance(lvt_8_2_, lvt_9_2_, true).removePlayer(lvt_5_1_);
                            }
                        }
                    }
                }
            }

            this.playerViewRadius = radius;
        }
    }

    /**
     * Get the furthest viewable block given player's view distance
     */
    public static int getFurthestViewableBlock(int distance)
    {
        return distance * 16 - 16;
    }

    class PlayerInstance
    {
        private final List<EntityPlayerMP> playersWatchingChunk = Lists.newArrayList();
        private final ChunkCoordIntPair chunkCoords;
        private short[] locationOfBlockChange = new short[64];
        private int numBlocksToUpdate;
        private int flagsYAreasToUpdate;
        private long previousWorldTime;

        public PlayerInstance(int chunkX, int chunkZ)
        {
            this.chunkCoords = new ChunkCoordIntPair(chunkX, chunkZ);
            PlayerManager.this.getWorldServer().theChunkProviderServer.loadChunk(chunkX, chunkZ);
        }

        public void addPlayer(EntityPlayerMP player)
        {
            if (this.playersWatchingChunk.contains(player))
            {
                PlayerManager.pmLogger.debug("Failed to add player. {} already is in chunk {}, {}", new Object[] {player, Integer.valueOf(this.chunkCoords.chunkXPos), Integer.valueOf(this.chunkCoords.chunkZPos)});
            }
            else
            {
                if (this.playersWatchingChunk.isEmpty())
                {
                    this.previousWorldTime = PlayerManager.this.theWorldServer.getTotalWorldTime();
                }

                this.playersWatchingChunk.add(player);
                player.loadedChunks.add(this.chunkCoords);
            }
        }

        public void removePlayer(EntityPlayerMP player)
        {
            if (this.playersWatchingChunk.contains(player))
            {
                Chunk lvt_2_1_ = PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos);

                if (lvt_2_1_.isPopulated())
                {
                    player.playerNetServerHandler.sendPacket(new S21PacketChunkData(lvt_2_1_, true, 0));
                }

                this.playersWatchingChunk.remove(player);
                player.loadedChunks.remove(this.chunkCoords);

                if (this.playersWatchingChunk.isEmpty())
                {
                    long lvt_3_1_ = (long)this.chunkCoords.chunkXPos + 2147483647L | (long)this.chunkCoords.chunkZPos + 2147483647L << 32;
                    this.increaseInhabitedTime(lvt_2_1_);
                    PlayerManager.this.playerInstances.remove(lvt_3_1_);
                    PlayerManager.this.playerInstanceList.remove(this);

                    if (this.numBlocksToUpdate > 0)
                    {
                        PlayerManager.this.playerInstancesToUpdate.remove(this);
                    }

                    PlayerManager.this.getWorldServer().theChunkProviderServer.dropChunk(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos);
                }
            }
        }

        public void processChunk()
        {
            this.increaseInhabitedTime(PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos));
        }

        private void increaseInhabitedTime(Chunk theChunk)
        {
            theChunk.setInhabitedTime(theChunk.getInhabitedTime() + PlayerManager.this.theWorldServer.getTotalWorldTime() - this.previousWorldTime);
            this.previousWorldTime = PlayerManager.this.theWorldServer.getTotalWorldTime();
        }

        public void flagChunkForUpdate(int x, int y, int z)
        {
            if (this.numBlocksToUpdate == 0)
            {
                PlayerManager.this.playerInstancesToUpdate.add(this);
            }

            this.flagsYAreasToUpdate |= 1 << (y >> 4);

            if (this.numBlocksToUpdate < 64)
            {
                short lvt_4_1_ = (short)(x << 12 | z << 8 | y);

                for (int lvt_5_1_ = 0; lvt_5_1_ < this.numBlocksToUpdate; ++lvt_5_1_)
                {
                    if (this.locationOfBlockChange[lvt_5_1_] == lvt_4_1_)
                    {
                        return;
                    }
                }

                this.locationOfBlockChange[this.numBlocksToUpdate++] = lvt_4_1_;
            }
        }

        public void sendToAllPlayersWatchingChunk(Packet thePacket)
        {
            for (int lvt_2_1_ = 0; lvt_2_1_ < this.playersWatchingChunk.size(); ++lvt_2_1_)
            {
                EntityPlayerMP lvt_3_1_ = (EntityPlayerMP)this.playersWatchingChunk.get(lvt_2_1_);

                if (!lvt_3_1_.loadedChunks.contains(this.chunkCoords))
                {
                    lvt_3_1_.playerNetServerHandler.sendPacket(thePacket);
                }
            }
        }

        public void onUpdate()
        {
            if (this.numBlocksToUpdate != 0)
            {
                if (this.numBlocksToUpdate == 1)
                {
                    int lvt_1_1_ = (this.locationOfBlockChange[0] >> 12 & 15) + this.chunkCoords.chunkXPos * 16;
                    int lvt_2_1_ = this.locationOfBlockChange[0] & 255;
                    int lvt_3_1_ = (this.locationOfBlockChange[0] >> 8 & 15) + this.chunkCoords.chunkZPos * 16;
                    BlockPos lvt_4_1_ = new BlockPos(lvt_1_1_, lvt_2_1_, lvt_3_1_);
                    this.sendToAllPlayersWatchingChunk(new S23PacketBlockChange(PlayerManager.this.theWorldServer, lvt_4_1_));

                    if (PlayerManager.this.theWorldServer.getBlockState(lvt_4_1_).getBlock().hasTileEntity())
                    {
                        this.sendTileToAllPlayersWatchingChunk(PlayerManager.this.theWorldServer.getTileEntity(lvt_4_1_));
                    }
                }
                else if (this.numBlocksToUpdate == 64)
                {
                    int lvt_1_2_ = this.chunkCoords.chunkXPos * 16;
                    int lvt_2_2_ = this.chunkCoords.chunkZPos * 16;
                    this.sendToAllPlayersWatchingChunk(new S21PacketChunkData(PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos), false, this.flagsYAreasToUpdate));

                    for (int lvt_3_2_ = 0; lvt_3_2_ < 16; ++lvt_3_2_)
                    {
                        if ((this.flagsYAreasToUpdate & 1 << lvt_3_2_) != 0)
                        {
                            int lvt_4_2_ = lvt_3_2_ << 4;
                            List<TileEntity> lvt_5_1_ = PlayerManager.this.theWorldServer.getTileEntitiesIn(lvt_1_2_, lvt_4_2_, lvt_2_2_, lvt_1_2_ + 16, lvt_4_2_ + 16, lvt_2_2_ + 16);

                            for (int lvt_6_1_ = 0; lvt_6_1_ < lvt_5_1_.size(); ++lvt_6_1_)
                            {
                                this.sendTileToAllPlayersWatchingChunk((TileEntity)lvt_5_1_.get(lvt_6_1_));
                            }
                        }
                    }
                }
                else
                {
                    this.sendToAllPlayersWatchingChunk(new S22PacketMultiBlockChange(this.numBlocksToUpdate, this.locationOfBlockChange, PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos)));

                    for (int lvt_1_3_ = 0; lvt_1_3_ < this.numBlocksToUpdate; ++lvt_1_3_)
                    {
                        int lvt_2_3_ = (this.locationOfBlockChange[lvt_1_3_] >> 12 & 15) + this.chunkCoords.chunkXPos * 16;
                        int lvt_3_3_ = this.locationOfBlockChange[lvt_1_3_] & 255;
                        int lvt_4_3_ = (this.locationOfBlockChange[lvt_1_3_] >> 8 & 15) + this.chunkCoords.chunkZPos * 16;
                        BlockPos lvt_5_2_ = new BlockPos(lvt_2_3_, lvt_3_3_, lvt_4_3_);

                        if (PlayerManager.this.theWorldServer.getBlockState(lvt_5_2_).getBlock().hasTileEntity())
                        {
                            this.sendTileToAllPlayersWatchingChunk(PlayerManager.this.theWorldServer.getTileEntity(lvt_5_2_));
                        }
                    }
                }

                this.numBlocksToUpdate = 0;
                this.flagsYAreasToUpdate = 0;
            }
        }

        private void sendTileToAllPlayersWatchingChunk(TileEntity theTileEntity)
        {
            if (theTileEntity != null)
            {
                Packet lvt_2_1_ = theTileEntity.getDescriptionPacket();

                if (lvt_2_1_ != null)
                {
                    this.sendToAllPlayersWatchingChunk(lvt_2_1_);
                }
            }
        }
    }
}
