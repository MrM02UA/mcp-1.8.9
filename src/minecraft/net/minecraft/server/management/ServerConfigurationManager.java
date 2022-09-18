package net.minecraft.server.management;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S41PacketServerDifficulty;
import net.minecraft.network.play.server.S44PacketWorldBorder;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.demo.DemoWorldManager;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ServerConfigurationManager
{
    public static final File FILE_PLAYERBANS = new File("banned-players.json");
    public static final File FILE_IPBANS = new File("banned-ips.json");
    public static final File FILE_OPS = new File("ops.json");
    public static final File FILE_WHITELIST = new File("whitelist.json");
    private static final Logger logger = LogManager.getLogger();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd \'at\' HH:mm:ss z");

    /** Reference to the MinecraftServer object. */
    private final MinecraftServer mcServer;
    private final List<EntityPlayerMP> playerEntityList = Lists.newArrayList();
    private final Map<UUID, EntityPlayerMP> uuidToPlayerMap = Maps.newHashMap();
    private final UserListBans bannedPlayers;
    private final BanList bannedIPs;

    /** A set containing the OPs. */
    private final UserListOps ops;

    /** The Set of all whitelisted players. */
    private final UserListWhitelist whiteListedPlayers;
    private final Map<UUID, StatisticsFile> playerStatFiles;

    /** Reference to the PlayerNBTManager object. */
    private IPlayerFileData playerNBTManagerObj;

    /**
     * Server setting to only allow OPs and whitelisted players to join the server.
     */
    private boolean whiteListEnforced;

    /** The maximum number of players that can be connected at a time. */
    protected int maxPlayers;
    private int viewDistance;
    private WorldSettings.GameType gameType;

    /** True if all players are allowed to use commands (cheats). */
    private boolean commandsAllowedForAll;

    /**
     * index into playerEntities of player to ping, updated every tick; currently hardcoded to max at 200 players
     */
    private int playerPingIndex;

    public ServerConfigurationManager(MinecraftServer server)
    {
        this.bannedPlayers = new UserListBans(FILE_PLAYERBANS);
        this.bannedIPs = new BanList(FILE_IPBANS);
        this.ops = new UserListOps(FILE_OPS);
        this.whiteListedPlayers = new UserListWhitelist(FILE_WHITELIST);
        this.playerStatFiles = Maps.newHashMap();
        this.mcServer = server;
        this.bannedPlayers.setLanServer(false);
        this.bannedIPs.setLanServer(false);
        this.maxPlayers = 8;
    }

    public void initializeConnectionToPlayer(NetworkManager netManager, EntityPlayerMP playerIn)
    {
        GameProfile lvt_3_1_ = playerIn.getGameProfile();
        PlayerProfileCache lvt_4_1_ = this.mcServer.getPlayerProfileCache();
        GameProfile lvt_5_1_ = lvt_4_1_.getProfileByUUID(lvt_3_1_.getId());
        String lvt_6_1_ = lvt_5_1_ == null ? lvt_3_1_.getName() : lvt_5_1_.getName();
        lvt_4_1_.addEntry(lvt_3_1_);
        NBTTagCompound lvt_7_1_ = this.readPlayerDataFromFile(playerIn);
        playerIn.setWorld(this.mcServer.worldServerForDimension(playerIn.dimension));
        playerIn.theItemInWorldManager.setWorld((WorldServer)playerIn.worldObj);
        String lvt_8_1_ = "local";

        if (netManager.getRemoteAddress() != null)
        {
            lvt_8_1_ = netManager.getRemoteAddress().toString();
        }

        logger.info(playerIn.getName() + "[" + lvt_8_1_ + "] logged in with entity id " + playerIn.getEntityId() + " at (" + playerIn.posX + ", " + playerIn.posY + ", " + playerIn.posZ + ")");
        WorldServer lvt_9_1_ = this.mcServer.worldServerForDimension(playerIn.dimension);
        WorldInfo lvt_10_1_ = lvt_9_1_.getWorldInfo();
        BlockPos lvt_11_1_ = lvt_9_1_.getSpawnPoint();
        this.setPlayerGameTypeBasedOnOther(playerIn, (EntityPlayerMP)null, lvt_9_1_);
        NetHandlerPlayServer lvt_12_1_ = new NetHandlerPlayServer(this.mcServer, netManager, playerIn);
        lvt_12_1_.sendPacket(new S01PacketJoinGame(playerIn.getEntityId(), playerIn.theItemInWorldManager.getGameType(), lvt_10_1_.isHardcoreModeEnabled(), lvt_9_1_.provider.getDimensionId(), lvt_9_1_.getDifficulty(), this.getMaxPlayers(), lvt_10_1_.getTerrainType(), lvt_9_1_.getGameRules().getBoolean("reducedDebugInfo")));
        lvt_12_1_.sendPacket(new S3FPacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString(this.getServerInstance().getServerModName())));
        lvt_12_1_.sendPacket(new S41PacketServerDifficulty(lvt_10_1_.getDifficulty(), lvt_10_1_.isDifficultyLocked()));
        lvt_12_1_.sendPacket(new S05PacketSpawnPosition(lvt_11_1_));
        lvt_12_1_.sendPacket(new S39PacketPlayerAbilities(playerIn.capabilities));
        lvt_12_1_.sendPacket(new S09PacketHeldItemChange(playerIn.inventory.currentItem));
        playerIn.getStatFile().func_150877_d();
        playerIn.getStatFile().sendAchievements(playerIn);
        this.sendScoreboard((ServerScoreboard)lvt_9_1_.getScoreboard(), playerIn);
        this.mcServer.refreshStatusNextTick();
        ChatComponentTranslation lvt_13_1_;

        if (!playerIn.getName().equalsIgnoreCase(lvt_6_1_))
        {
            lvt_13_1_ = new ChatComponentTranslation("multiplayer.player.joined.renamed", new Object[] {playerIn.getDisplayName(), lvt_6_1_});
        }
        else
        {
            lvt_13_1_ = new ChatComponentTranslation("multiplayer.player.joined", new Object[] {playerIn.getDisplayName()});
        }

        lvt_13_1_.getChatStyle().setColor(EnumChatFormatting.YELLOW);
        this.sendChatMsg(lvt_13_1_);
        this.playerLoggedIn(playerIn);
        lvt_12_1_.setPlayerLocation(playerIn.posX, playerIn.posY, playerIn.posZ, playerIn.rotationYaw, playerIn.rotationPitch);
        this.updateTimeAndWeatherForPlayer(playerIn, lvt_9_1_);

        if (this.mcServer.getResourcePackUrl().length() > 0)
        {
            playerIn.loadResourcePack(this.mcServer.getResourcePackUrl(), this.mcServer.getResourcePackHash());
        }

        for (PotionEffect lvt_15_1_ : playerIn.getActivePotionEffects())
        {
            lvt_12_1_.sendPacket(new S1DPacketEntityEffect(playerIn.getEntityId(), lvt_15_1_));
        }

        playerIn.addSelfToInternalCraftingInventory();

        if (lvt_7_1_ != null && lvt_7_1_.hasKey("Riding", 10))
        {
            Entity lvt_14_2_ = EntityList.createEntityFromNBT(lvt_7_1_.getCompoundTag("Riding"), lvt_9_1_);

            if (lvt_14_2_ != null)
            {
                lvt_14_2_.forceSpawn = true;
                lvt_9_1_.spawnEntityInWorld(lvt_14_2_);
                playerIn.mountEntity(lvt_14_2_);
                lvt_14_2_.forceSpawn = false;
            }
        }
    }

    protected void sendScoreboard(ServerScoreboard scoreboardIn, EntityPlayerMP playerIn)
    {
        Set<ScoreObjective> lvt_3_1_ = Sets.newHashSet();

        for (ScorePlayerTeam lvt_5_1_ : scoreboardIn.getTeams())
        {
            playerIn.playerNetServerHandler.sendPacket(new S3EPacketTeams(lvt_5_1_, 0));
        }

        for (int lvt_4_2_ = 0; lvt_4_2_ < 19; ++lvt_4_2_)
        {
            ScoreObjective lvt_5_2_ = scoreboardIn.getObjectiveInDisplaySlot(lvt_4_2_);

            if (lvt_5_2_ != null && !lvt_3_1_.contains(lvt_5_2_))
            {
                for (Packet lvt_8_1_ : scoreboardIn.func_96550_d(lvt_5_2_))
                {
                    playerIn.playerNetServerHandler.sendPacket(lvt_8_1_);
                }

                lvt_3_1_.add(lvt_5_2_);
            }
        }
    }

    /**
     * Sets the NBT manager to the one for the WorldServer given.
     */
    public void setPlayerManager(WorldServer[] worldServers)
    {
        this.playerNBTManagerObj = worldServers[0].getSaveHandler().getPlayerNBTManager();
        worldServers[0].getWorldBorder().addListener(new IBorderListener()
        {
            public void onSizeChanged(WorldBorder border, double newSize)
            {
                ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(border, S44PacketWorldBorder.Action.SET_SIZE));
            }
            public void onTransitionStarted(WorldBorder border, double oldSize, double newSize, long time)
            {
                ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(border, S44PacketWorldBorder.Action.LERP_SIZE));
            }
            public void onCenterChanged(WorldBorder border, double x, double z)
            {
                ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(border, S44PacketWorldBorder.Action.SET_CENTER));
            }
            public void onWarningTimeChanged(WorldBorder border, int newTime)
            {
                ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(border, S44PacketWorldBorder.Action.SET_WARNING_TIME));
            }
            public void onWarningDistanceChanged(WorldBorder border, int newDistance)
            {
                ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(border, S44PacketWorldBorder.Action.SET_WARNING_BLOCKS));
            }
            public void onDamageAmountChanged(WorldBorder border, double newAmount)
            {
            }
            public void onDamageBufferChanged(WorldBorder border, double newSize)
            {
            }
        });
    }

    public void preparePlayer(EntityPlayerMP playerIn, WorldServer worldIn)
    {
        WorldServer lvt_3_1_ = playerIn.getServerForPlayer();

        if (worldIn != null)
        {
            worldIn.getPlayerManager().removePlayer(playerIn);
        }

        lvt_3_1_.getPlayerManager().addPlayer(playerIn);
        lvt_3_1_.theChunkProviderServer.loadChunk((int)playerIn.posX >> 4, (int)playerIn.posZ >> 4);
    }

    public int getEntityViewDistance()
    {
        return PlayerManager.getFurthestViewableBlock(this.getViewDistance());
    }

    /**
     * called during player login. reads the player information from disk.
     */
    public NBTTagCompound readPlayerDataFromFile(EntityPlayerMP playerIn)
    {
        NBTTagCompound lvt_2_1_ = this.mcServer.worldServers[0].getWorldInfo().getPlayerNBTTagCompound();
        NBTTagCompound lvt_3_1_;

        if (playerIn.getName().equals(this.mcServer.getServerOwner()) && lvt_2_1_ != null)
        {
            playerIn.readFromNBT(lvt_2_1_);
            lvt_3_1_ = lvt_2_1_;
            logger.debug("loading single player");
        }
        else
        {
            lvt_3_1_ = this.playerNBTManagerObj.readPlayerData(playerIn);
        }

        return lvt_3_1_;
    }

    /**
     * also stores the NBTTags if this is an intergratedPlayerList
     */
    protected void writePlayerData(EntityPlayerMP playerIn)
    {
        this.playerNBTManagerObj.writePlayerData(playerIn);
        StatisticsFile lvt_2_1_ = (StatisticsFile)this.playerStatFiles.get(playerIn.getUniqueID());

        if (lvt_2_1_ != null)
        {
            lvt_2_1_.saveStatFile();
        }
    }

    /**
     * Called when a player successfully logs in. Reads player data from disk and inserts the player into the world.
     */
    public void playerLoggedIn(EntityPlayerMP playerIn)
    {
        this.playerEntityList.add(playerIn);
        this.uuidToPlayerMap.put(playerIn.getUniqueID(), playerIn);
        this.sendPacketToAllPlayers(new S38PacketPlayerListItem(S38PacketPlayerListItem.Action.ADD_PLAYER, new EntityPlayerMP[] {playerIn}));
        WorldServer lvt_2_1_ = this.mcServer.worldServerForDimension(playerIn.dimension);
        lvt_2_1_.spawnEntityInWorld(playerIn);
        this.preparePlayer(playerIn, (WorldServer)null);

        for (int lvt_3_1_ = 0; lvt_3_1_ < this.playerEntityList.size(); ++lvt_3_1_)
        {
            EntityPlayerMP lvt_4_1_ = (EntityPlayerMP)this.playerEntityList.get(lvt_3_1_);
            playerIn.playerNetServerHandler.sendPacket(new S38PacketPlayerListItem(S38PacketPlayerListItem.Action.ADD_PLAYER, new EntityPlayerMP[] {lvt_4_1_}));
        }
    }

    /**
     * using player's dimension, update their movement when in a vehicle (e.g. cart, boat)
     */
    public void serverUpdateMountedMovingPlayer(EntityPlayerMP playerIn)
    {
        playerIn.getServerForPlayer().getPlayerManager().updateMountedMovingPlayer(playerIn);
    }

    /**
     * Called when a player disconnects from the game. Writes player data to disk and removes them from the world.
     */
    public void playerLoggedOut(EntityPlayerMP playerIn)
    {
        playerIn.triggerAchievement(StatList.leaveGameStat);
        this.writePlayerData(playerIn);
        WorldServer lvt_2_1_ = playerIn.getServerForPlayer();

        if (playerIn.ridingEntity != null)
        {
            lvt_2_1_.removePlayerEntityDangerously(playerIn.ridingEntity);
            logger.debug("removing player mount");
        }

        lvt_2_1_.removeEntity(playerIn);
        lvt_2_1_.getPlayerManager().removePlayer(playerIn);
        this.playerEntityList.remove(playerIn);
        UUID lvt_3_1_ = playerIn.getUniqueID();
        EntityPlayerMP lvt_4_1_ = (EntityPlayerMP)this.uuidToPlayerMap.get(lvt_3_1_);

        if (lvt_4_1_ == playerIn)
        {
            this.uuidToPlayerMap.remove(lvt_3_1_);
            this.playerStatFiles.remove(lvt_3_1_);
        }

        this.sendPacketToAllPlayers(new S38PacketPlayerListItem(S38PacketPlayerListItem.Action.REMOVE_PLAYER, new EntityPlayerMP[] {playerIn}));
    }

    /**
     * checks ban-lists, then white-lists, then space for the server. Returns null on success, or an error message
     */
    public String allowUserToConnect(SocketAddress address, GameProfile profile)
    {
        if (this.bannedPlayers.isBanned(profile))
        {
            UserListBansEntry lvt_3_1_ = (UserListBansEntry)this.bannedPlayers.getEntry(profile);
            String lvt_4_1_ = "You are banned from this server!\nReason: " + lvt_3_1_.getBanReason();

            if (lvt_3_1_.getBanEndDate() != null)
            {
                lvt_4_1_ = lvt_4_1_ + "\nYour ban will be removed on " + dateFormat.format(lvt_3_1_.getBanEndDate());
            }

            return lvt_4_1_;
        }
        else if (!this.canJoin(profile))
        {
            return "You are not white-listed on this server!";
        }
        else if (this.bannedIPs.isBanned(address))
        {
            IPBanEntry lvt_3_2_ = this.bannedIPs.getBanEntry(address);
            String lvt_4_2_ = "Your IP address is banned from this server!\nReason: " + lvt_3_2_.getBanReason();

            if (lvt_3_2_.getBanEndDate() != null)
            {
                lvt_4_2_ = lvt_4_2_ + "\nYour ban will be removed on " + dateFormat.format(lvt_3_2_.getBanEndDate());
            }

            return lvt_4_2_;
        }
        else
        {
            return this.playerEntityList.size() >= this.maxPlayers && !this.bypassesPlayerLimit(profile) ? "The server is full!" : null;
        }
    }

    /**
     * also checks for multiple logins across servers
     */
    public EntityPlayerMP createPlayerForUser(GameProfile profile)
    {
        UUID lvt_2_1_ = EntityPlayer.getUUID(profile);
        List<EntityPlayerMP> lvt_3_1_ = Lists.newArrayList();

        for (int lvt_4_1_ = 0; lvt_4_1_ < this.playerEntityList.size(); ++lvt_4_1_)
        {
            EntityPlayerMP lvt_5_1_ = (EntityPlayerMP)this.playerEntityList.get(lvt_4_1_);

            if (lvt_5_1_.getUniqueID().equals(lvt_2_1_))
            {
                lvt_3_1_.add(lvt_5_1_);
            }
        }

        EntityPlayerMP lvt_4_2_ = (EntityPlayerMP)this.uuidToPlayerMap.get(profile.getId());

        if (lvt_4_2_ != null && !lvt_3_1_.contains(lvt_4_2_))
        {
            lvt_3_1_.add(lvt_4_2_);
        }

        for (EntityPlayerMP lvt_6_1_ : lvt_3_1_)
        {
            lvt_6_1_.playerNetServerHandler.kickPlayerFromServer("You logged in from another location");
        }

        ItemInWorldManager lvt_5_3_;

        if (this.mcServer.isDemo())
        {
            lvt_5_3_ = new DemoWorldManager(this.mcServer.worldServerForDimension(0));
        }
        else
        {
            lvt_5_3_ = new ItemInWorldManager(this.mcServer.worldServerForDimension(0));
        }

        return new EntityPlayerMP(this.mcServer, this.mcServer.worldServerForDimension(0), profile, lvt_5_3_);
    }

    /**
     * Called on respawn
     */
    public EntityPlayerMP recreatePlayerEntity(EntityPlayerMP playerIn, int dimension, boolean conqueredEnd)
    {
        playerIn.getServerForPlayer().getEntityTracker().removePlayerFromTrackers(playerIn);
        playerIn.getServerForPlayer().getEntityTracker().untrackEntity(playerIn);
        playerIn.getServerForPlayer().getPlayerManager().removePlayer(playerIn);
        this.playerEntityList.remove(playerIn);
        this.mcServer.worldServerForDimension(playerIn.dimension).removePlayerEntityDangerously(playerIn);
        BlockPos lvt_4_1_ = playerIn.getBedLocation();
        boolean lvt_5_1_ = playerIn.isSpawnForced();
        playerIn.dimension = dimension;
        ItemInWorldManager lvt_6_1_;

        if (this.mcServer.isDemo())
        {
            lvt_6_1_ = new DemoWorldManager(this.mcServer.worldServerForDimension(playerIn.dimension));
        }
        else
        {
            lvt_6_1_ = new ItemInWorldManager(this.mcServer.worldServerForDimension(playerIn.dimension));
        }

        EntityPlayerMP lvt_7_1_ = new EntityPlayerMP(this.mcServer, this.mcServer.worldServerForDimension(playerIn.dimension), playerIn.getGameProfile(), lvt_6_1_);
        lvt_7_1_.playerNetServerHandler = playerIn.playerNetServerHandler;
        lvt_7_1_.clonePlayer(playerIn, conqueredEnd);
        lvt_7_1_.setEntityId(playerIn.getEntityId());
        lvt_7_1_.setCommandStats(playerIn);
        WorldServer lvt_8_1_ = this.mcServer.worldServerForDimension(playerIn.dimension);
        this.setPlayerGameTypeBasedOnOther(lvt_7_1_, playerIn, lvt_8_1_);

        if (lvt_4_1_ != null)
        {
            BlockPos lvt_9_1_ = EntityPlayer.getBedSpawnLocation(this.mcServer.worldServerForDimension(playerIn.dimension), lvt_4_1_, lvt_5_1_);

            if (lvt_9_1_ != null)
            {
                lvt_7_1_.setLocationAndAngles((double)((float)lvt_9_1_.getX() + 0.5F), (double)((float)lvt_9_1_.getY() + 0.1F), (double)((float)lvt_9_1_.getZ() + 0.5F), 0.0F, 0.0F);
                lvt_7_1_.setSpawnPoint(lvt_4_1_, lvt_5_1_);
            }
            else
            {
                lvt_7_1_.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(0, 0.0F));
            }
        }

        lvt_8_1_.theChunkProviderServer.loadChunk((int)lvt_7_1_.posX >> 4, (int)lvt_7_1_.posZ >> 4);

        while (!lvt_8_1_.getCollidingBoundingBoxes(lvt_7_1_, lvt_7_1_.getEntityBoundingBox()).isEmpty() && lvt_7_1_.posY < 256.0D)
        {
            lvt_7_1_.setPosition(lvt_7_1_.posX, lvt_7_1_.posY + 1.0D, lvt_7_1_.posZ);
        }

        lvt_7_1_.playerNetServerHandler.sendPacket(new S07PacketRespawn(lvt_7_1_.dimension, lvt_7_1_.worldObj.getDifficulty(), lvt_7_1_.worldObj.getWorldInfo().getTerrainType(), lvt_7_1_.theItemInWorldManager.getGameType()));
        BlockPos lvt_9_2_ = lvt_8_1_.getSpawnPoint();
        lvt_7_1_.playerNetServerHandler.setPlayerLocation(lvt_7_1_.posX, lvt_7_1_.posY, lvt_7_1_.posZ, lvt_7_1_.rotationYaw, lvt_7_1_.rotationPitch);
        lvt_7_1_.playerNetServerHandler.sendPacket(new S05PacketSpawnPosition(lvt_9_2_));
        lvt_7_1_.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(lvt_7_1_.experience, lvt_7_1_.experienceTotal, lvt_7_1_.experienceLevel));
        this.updateTimeAndWeatherForPlayer(lvt_7_1_, lvt_8_1_);
        lvt_8_1_.getPlayerManager().addPlayer(lvt_7_1_);
        lvt_8_1_.spawnEntityInWorld(lvt_7_1_);
        this.playerEntityList.add(lvt_7_1_);
        this.uuidToPlayerMap.put(lvt_7_1_.getUniqueID(), lvt_7_1_);
        lvt_7_1_.addSelfToInternalCraftingInventory();
        lvt_7_1_.setHealth(lvt_7_1_.getHealth());
        return lvt_7_1_;
    }

    /**
     * moves provided player from overworld to nether or vice versa
     */
    public void transferPlayerToDimension(EntityPlayerMP playerIn, int dimension)
    {
        int lvt_3_1_ = playerIn.dimension;
        WorldServer lvt_4_1_ = this.mcServer.worldServerForDimension(playerIn.dimension);
        playerIn.dimension = dimension;
        WorldServer lvt_5_1_ = this.mcServer.worldServerForDimension(playerIn.dimension);
        playerIn.playerNetServerHandler.sendPacket(new S07PacketRespawn(playerIn.dimension, playerIn.worldObj.getDifficulty(), playerIn.worldObj.getWorldInfo().getTerrainType(), playerIn.theItemInWorldManager.getGameType()));
        lvt_4_1_.removePlayerEntityDangerously(playerIn);
        playerIn.isDead = false;
        this.transferEntityToWorld(playerIn, lvt_3_1_, lvt_4_1_, lvt_5_1_);
        this.preparePlayer(playerIn, lvt_4_1_);
        playerIn.playerNetServerHandler.setPlayerLocation(playerIn.posX, playerIn.posY, playerIn.posZ, playerIn.rotationYaw, playerIn.rotationPitch);
        playerIn.theItemInWorldManager.setWorld(lvt_5_1_);
        this.updateTimeAndWeatherForPlayer(playerIn, lvt_5_1_);
        this.syncPlayerInventory(playerIn);

        for (PotionEffect lvt_7_1_ : playerIn.getActivePotionEffects())
        {
            playerIn.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(playerIn.getEntityId(), lvt_7_1_));
        }
    }

    /**
     * Transfers an entity from a world to another world.
     *  
     * @param oldWorldIn The world transfering from
     * @param toWorldIn The world transfering the entity to
     */
    public void transferEntityToWorld(Entity entityIn, int p_82448_2_, WorldServer oldWorldIn, WorldServer toWorldIn)
    {
        double lvt_5_1_ = entityIn.posX;
        double lvt_7_1_ = entityIn.posZ;
        double lvt_9_1_ = 8.0D;
        float lvt_11_1_ = entityIn.rotationYaw;
        oldWorldIn.theProfiler.startSection("moving");

        if (entityIn.dimension == -1)
        {
            lvt_5_1_ = MathHelper.clamp_double(lvt_5_1_ / lvt_9_1_, toWorldIn.getWorldBorder().minX() + 16.0D, toWorldIn.getWorldBorder().maxX() - 16.0D);
            lvt_7_1_ = MathHelper.clamp_double(lvt_7_1_ / lvt_9_1_, toWorldIn.getWorldBorder().minZ() + 16.0D, toWorldIn.getWorldBorder().maxZ() - 16.0D);
            entityIn.setLocationAndAngles(lvt_5_1_, entityIn.posY, lvt_7_1_, entityIn.rotationYaw, entityIn.rotationPitch);

            if (entityIn.isEntityAlive())
            {
                oldWorldIn.updateEntityWithOptionalForce(entityIn, false);
            }
        }
        else if (entityIn.dimension == 0)
        {
            lvt_5_1_ = MathHelper.clamp_double(lvt_5_1_ * lvt_9_1_, toWorldIn.getWorldBorder().minX() + 16.0D, toWorldIn.getWorldBorder().maxX() - 16.0D);
            lvt_7_1_ = MathHelper.clamp_double(lvt_7_1_ * lvt_9_1_, toWorldIn.getWorldBorder().minZ() + 16.0D, toWorldIn.getWorldBorder().maxZ() - 16.0D);
            entityIn.setLocationAndAngles(lvt_5_1_, entityIn.posY, lvt_7_1_, entityIn.rotationYaw, entityIn.rotationPitch);

            if (entityIn.isEntityAlive())
            {
                oldWorldIn.updateEntityWithOptionalForce(entityIn, false);
            }
        }
        else
        {
            BlockPos lvt_12_1_;

            if (p_82448_2_ == 1)
            {
                lvt_12_1_ = toWorldIn.getSpawnPoint();
            }
            else
            {
                lvt_12_1_ = toWorldIn.getSpawnCoordinate();
            }

            lvt_5_1_ = (double)lvt_12_1_.getX();
            entityIn.posY = (double)lvt_12_1_.getY();
            lvt_7_1_ = (double)lvt_12_1_.getZ();
            entityIn.setLocationAndAngles(lvt_5_1_, entityIn.posY, lvt_7_1_, 90.0F, 0.0F);

            if (entityIn.isEntityAlive())
            {
                oldWorldIn.updateEntityWithOptionalForce(entityIn, false);
            }
        }

        oldWorldIn.theProfiler.endSection();

        if (p_82448_2_ != 1)
        {
            oldWorldIn.theProfiler.startSection("placing");
            lvt_5_1_ = (double)MathHelper.clamp_int((int)lvt_5_1_, -29999872, 29999872);
            lvt_7_1_ = (double)MathHelper.clamp_int((int)lvt_7_1_, -29999872, 29999872);

            if (entityIn.isEntityAlive())
            {
                entityIn.setLocationAndAngles(lvt_5_1_, entityIn.posY, lvt_7_1_, entityIn.rotationYaw, entityIn.rotationPitch);
                toWorldIn.getDefaultTeleporter().placeInPortal(entityIn, lvt_11_1_);
                toWorldIn.spawnEntityInWorld(entityIn);
                toWorldIn.updateEntityWithOptionalForce(entityIn, false);
            }

            oldWorldIn.theProfiler.endSection();
        }

        entityIn.setWorld(toWorldIn);
    }

    /**
     * self explanitory
     */
    public void onTick()
    {
        if (++this.playerPingIndex > 600)
        {
            this.sendPacketToAllPlayers(new S38PacketPlayerListItem(S38PacketPlayerListItem.Action.UPDATE_LATENCY, this.playerEntityList));
            this.playerPingIndex = 0;
        }
    }

    public void sendPacketToAllPlayers(Packet packetIn)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < this.playerEntityList.size(); ++lvt_2_1_)
        {
            ((EntityPlayerMP)this.playerEntityList.get(lvt_2_1_)).playerNetServerHandler.sendPacket(packetIn);
        }
    }

    public void sendPacketToAllPlayersInDimension(Packet packetIn, int dimension)
    {
        for (int lvt_3_1_ = 0; lvt_3_1_ < this.playerEntityList.size(); ++lvt_3_1_)
        {
            EntityPlayerMP lvt_4_1_ = (EntityPlayerMP)this.playerEntityList.get(lvt_3_1_);

            if (lvt_4_1_.dimension == dimension)
            {
                lvt_4_1_.playerNetServerHandler.sendPacket(packetIn);
            }
        }
    }

    public void sendMessageToAllTeamMembers(EntityPlayer player, IChatComponent message)
    {
        Team lvt_3_1_ = player.getTeam();

        if (lvt_3_1_ != null)
        {
            for (String lvt_6_1_ : lvt_3_1_.getMembershipCollection())
            {
                EntityPlayerMP lvt_7_1_ = this.getPlayerByUsername(lvt_6_1_);

                if (lvt_7_1_ != null && lvt_7_1_ != player)
                {
                    lvt_7_1_.addChatMessage(message);
                }
            }
        }
    }

    public void sendMessageToTeamOrEvryPlayer(EntityPlayer player, IChatComponent message)
    {
        Team lvt_3_1_ = player.getTeam();

        if (lvt_3_1_ == null)
        {
            this.sendChatMsg(message);
        }
        else
        {
            for (int lvt_4_1_ = 0; lvt_4_1_ < this.playerEntityList.size(); ++lvt_4_1_)
            {
                EntityPlayerMP lvt_5_1_ = (EntityPlayerMP)this.playerEntityList.get(lvt_4_1_);

                if (lvt_5_1_.getTeam() != lvt_3_1_)
                {
                    lvt_5_1_.addChatMessage(message);
                }
            }
        }
    }

    public String func_181058_b(boolean p_181058_1_)
    {
        String lvt_2_1_ = "";
        List<EntityPlayerMP> lvt_3_1_ = Lists.newArrayList(this.playerEntityList);

        for (int lvt_4_1_ = 0; lvt_4_1_ < ((List)lvt_3_1_).size(); ++lvt_4_1_)
        {
            if (lvt_4_1_ > 0)
            {
                lvt_2_1_ = lvt_2_1_ + ", ";
            }

            lvt_2_1_ = lvt_2_1_ + ((EntityPlayerMP)lvt_3_1_.get(lvt_4_1_)).getName();

            if (p_181058_1_)
            {
                lvt_2_1_ = lvt_2_1_ + " (" + ((EntityPlayerMP)lvt_3_1_.get(lvt_4_1_)).getUniqueID().toString() + ")";
            }
        }

        return lvt_2_1_;
    }

    /**
     * Returns an array of the usernames of all the connected players.
     */
    public String[] getAllUsernames()
    {
        String[] lvt_1_1_ = new String[this.playerEntityList.size()];

        for (int lvt_2_1_ = 0; lvt_2_1_ < this.playerEntityList.size(); ++lvt_2_1_)
        {
            lvt_1_1_[lvt_2_1_] = ((EntityPlayerMP)this.playerEntityList.get(lvt_2_1_)).getName();
        }

        return lvt_1_1_;
    }

    public GameProfile[] getAllProfiles()
    {
        GameProfile[] lvt_1_1_ = new GameProfile[this.playerEntityList.size()];

        for (int lvt_2_1_ = 0; lvt_2_1_ < this.playerEntityList.size(); ++lvt_2_1_)
        {
            lvt_1_1_[lvt_2_1_] = ((EntityPlayerMP)this.playerEntityList.get(lvt_2_1_)).getGameProfile();
        }

        return lvt_1_1_;
    }

    public UserListBans getBannedPlayers()
    {
        return this.bannedPlayers;
    }

    public BanList getBannedIPs()
    {
        return this.bannedIPs;
    }

    public void addOp(GameProfile profile)
    {
        this.ops.addEntry(new UserListOpsEntry(profile, this.mcServer.getOpPermissionLevel(), this.ops.bypassesPlayerLimit(profile)));
    }

    public void removeOp(GameProfile profile)
    {
        this.ops.removeEntry(profile);
    }

    public boolean canJoin(GameProfile profile)
    {
        return !this.whiteListEnforced || this.ops.hasEntry(profile) || this.whiteListedPlayers.hasEntry(profile);
    }

    public boolean canSendCommands(GameProfile profile)
    {
        return this.ops.hasEntry(profile) || this.mcServer.isSinglePlayer() && this.mcServer.worldServers[0].getWorldInfo().areCommandsAllowed() && this.mcServer.getServerOwner().equalsIgnoreCase(profile.getName()) || this.commandsAllowedForAll;
    }

    public EntityPlayerMP getPlayerByUsername(String username)
    {
        for (EntityPlayerMP lvt_3_1_ : this.playerEntityList)
        {
            if (lvt_3_1_.getName().equalsIgnoreCase(username))
            {
                return lvt_3_1_;
            }
        }

        return null;
    }

    /**
     * params: x,y,z,r,dimension. The packet is sent to all players within r radius of x,y,z (r^2>x^2+y^2+z^2)
     */
    public void sendToAllNear(double x, double y, double z, double radius, int dimension, Packet packetIn)
    {
        this.sendToAllNearExcept((EntityPlayer)null, x, y, z, radius, dimension, packetIn);
    }

    /**
     * params: srcPlayer,x,y,z,r,dimension. The packet is not sent to the srcPlayer, but all other players within the
     * search radius
     */
    public void sendToAllNearExcept(EntityPlayer p_148543_1_, double x, double y, double z, double radius, int dimension, Packet p_148543_11_)
    {
        for (int lvt_12_1_ = 0; lvt_12_1_ < this.playerEntityList.size(); ++lvt_12_1_)
        {
            EntityPlayerMP lvt_13_1_ = (EntityPlayerMP)this.playerEntityList.get(lvt_12_1_);

            if (lvt_13_1_ != p_148543_1_ && lvt_13_1_.dimension == dimension)
            {
                double lvt_14_1_ = x - lvt_13_1_.posX;
                double lvt_16_1_ = y - lvt_13_1_.posY;
                double lvt_18_1_ = z - lvt_13_1_.posZ;

                if (lvt_14_1_ * lvt_14_1_ + lvt_16_1_ * lvt_16_1_ + lvt_18_1_ * lvt_18_1_ < radius * radius)
                {
                    lvt_13_1_.playerNetServerHandler.sendPacket(p_148543_11_);
                }
            }
        }
    }

    /**
     * Saves all of the players' current states.
     */
    public void saveAllPlayerData()
    {
        for (int lvt_1_1_ = 0; lvt_1_1_ < this.playerEntityList.size(); ++lvt_1_1_)
        {
            this.writePlayerData((EntityPlayerMP)this.playerEntityList.get(lvt_1_1_));
        }
    }

    public void addWhitelistedPlayer(GameProfile profile)
    {
        this.whiteListedPlayers.addEntry(new UserListWhitelistEntry(profile));
    }

    public void removePlayerFromWhitelist(GameProfile profile)
    {
        this.whiteListedPlayers.removeEntry(profile);
    }

    public UserListWhitelist getWhitelistedPlayers()
    {
        return this.whiteListedPlayers;
    }

    public String[] getWhitelistedPlayerNames()
    {
        return this.whiteListedPlayers.getKeys();
    }

    public UserListOps getOppedPlayers()
    {
        return this.ops;
    }

    public String[] getOppedPlayerNames()
    {
        return this.ops.getKeys();
    }

    /**
     * Either does nothing, or calls readWhiteList.
     */
    public void loadWhiteList()
    {
    }

    /**
     * Updates the time and weather for the given player to those of the given world
     */
    public void updateTimeAndWeatherForPlayer(EntityPlayerMP playerIn, WorldServer worldIn)
    {
        WorldBorder lvt_3_1_ = this.mcServer.worldServers[0].getWorldBorder();
        playerIn.playerNetServerHandler.sendPacket(new S44PacketWorldBorder(lvt_3_1_, S44PacketWorldBorder.Action.INITIALIZE));
        playerIn.playerNetServerHandler.sendPacket(new S03PacketTimeUpdate(worldIn.getTotalWorldTime(), worldIn.getWorldTime(), worldIn.getGameRules().getBoolean("doDaylightCycle")));

        if (worldIn.isRaining())
        {
            playerIn.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(1, 0.0F));
            playerIn.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(7, worldIn.getRainStrength(1.0F)));
            playerIn.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(8, worldIn.getThunderStrength(1.0F)));
        }
    }

    /**
     * sends the players inventory to himself
     */
    public void syncPlayerInventory(EntityPlayerMP playerIn)
    {
        playerIn.sendContainerToPlayer(playerIn.inventoryContainer);
        playerIn.setPlayerHealthUpdated();
        playerIn.playerNetServerHandler.sendPacket(new S09PacketHeldItemChange(playerIn.inventory.currentItem));
    }

    /**
     * Returns the number of players currently on the server.
     */
    public int getCurrentPlayerCount()
    {
        return this.playerEntityList.size();
    }

    /**
     * Returns the maximum number of players allowed on the server.
     */
    public int getMaxPlayers()
    {
        return this.maxPlayers;
    }

    /**
     * Returns an array of usernames for which player.dat exists for.
     */
    public String[] getAvailablePlayerDat()
    {
        return this.mcServer.worldServers[0].getSaveHandler().getPlayerNBTManager().getAvailablePlayerDat();
    }

    public void setWhiteListEnabled(boolean whitelistEnabled)
    {
        this.whiteListEnforced = whitelistEnabled;
    }

    public List<EntityPlayerMP> getPlayersMatchingAddress(String address)
    {
        List<EntityPlayerMP> lvt_2_1_ = Lists.newArrayList();

        for (EntityPlayerMP lvt_4_1_ : this.playerEntityList)
        {
            if (lvt_4_1_.getPlayerIP().equals(address))
            {
                lvt_2_1_.add(lvt_4_1_);
            }
        }

        return lvt_2_1_;
    }

    /**
     * Gets the View Distance.
     */
    public int getViewDistance()
    {
        return this.viewDistance;
    }

    public MinecraftServer getServerInstance()
    {
        return this.mcServer;
    }

    /**
     * On integrated servers, returns the host's player data to be written to level.dat.
     */
    public NBTTagCompound getHostPlayerData()
    {
        return null;
    }

    public void setGameType(WorldSettings.GameType p_152604_1_)
    {
        this.gameType = p_152604_1_;
    }

    private void setPlayerGameTypeBasedOnOther(EntityPlayerMP p_72381_1_, EntityPlayerMP p_72381_2_, World worldIn)
    {
        if (p_72381_2_ != null)
        {
            p_72381_1_.theItemInWorldManager.setGameType(p_72381_2_.theItemInWorldManager.getGameType());
        }
        else if (this.gameType != null)
        {
            p_72381_1_.theItemInWorldManager.setGameType(this.gameType);
        }

        p_72381_1_.theItemInWorldManager.initializeGameType(worldIn.getWorldInfo().getGameType());
    }

    /**
     * Sets whether all players are allowed to use commands (cheats) on the server.
     */
    public void setCommandsAllowedForAll(boolean p_72387_1_)
    {
        this.commandsAllowedForAll = p_72387_1_;
    }

    /**
     * Kicks everyone with "Server closed" as reason.
     */
    public void removeAllPlayers()
    {
        for (int lvt_1_1_ = 0; lvt_1_1_ < this.playerEntityList.size(); ++lvt_1_1_)
        {
            ((EntityPlayerMP)this.playerEntityList.get(lvt_1_1_)).playerNetServerHandler.kickPlayerFromServer("Server closed");
        }
    }

    public void sendChatMsgImpl(IChatComponent component, boolean isChat)
    {
        this.mcServer.addChatMessage(component);
        byte lvt_3_1_ = (byte)(isChat ? 1 : 0);
        this.sendPacketToAllPlayers(new S02PacketChat(component, lvt_3_1_));
    }

    /**
     * Sends the given string to every player as chat message.
     */
    public void sendChatMsg(IChatComponent component)
    {
        this.sendChatMsgImpl(component, true);
    }

    public StatisticsFile getPlayerStatsFile(EntityPlayer playerIn)
    {
        UUID lvt_2_1_ = playerIn.getUniqueID();
        StatisticsFile lvt_3_1_ = lvt_2_1_ == null ? null : (StatisticsFile)this.playerStatFiles.get(lvt_2_1_);

        if (lvt_3_1_ == null)
        {
            File lvt_4_1_ = new File(this.mcServer.worldServerForDimension(0).getSaveHandler().getWorldDirectory(), "stats");
            File lvt_5_1_ = new File(lvt_4_1_, lvt_2_1_.toString() + ".json");

            if (!lvt_5_1_.exists())
            {
                File lvt_6_1_ = new File(lvt_4_1_, playerIn.getName() + ".json");

                if (lvt_6_1_.exists() && lvt_6_1_.isFile())
                {
                    lvt_6_1_.renameTo(lvt_5_1_);
                }
            }

            lvt_3_1_ = new StatisticsFile(this.mcServer, lvt_5_1_);
            lvt_3_1_.readStatFile();
            this.playerStatFiles.put(lvt_2_1_, lvt_3_1_);
        }

        return lvt_3_1_;
    }

    public void setViewDistance(int distance)
    {
        this.viewDistance = distance;

        if (this.mcServer.worldServers != null)
        {
            for (WorldServer lvt_5_1_ : this.mcServer.worldServers)
            {
                if (lvt_5_1_ != null)
                {
                    lvt_5_1_.getPlayerManager().setPlayerViewRadius(distance);
                }
            }
        }
    }

    public List<EntityPlayerMP> getPlayerList()
    {
        return this.playerEntityList;
    }

    /**
     * Get's the EntityPlayerMP object representing the player with the UUID.
     */
    public EntityPlayerMP getPlayerByUUID(UUID playerUUID)
    {
        return (EntityPlayerMP)this.uuidToPlayerMap.get(playerUUID);
    }

    public boolean bypassesPlayerLimit(GameProfile p_183023_1_)
    {
        return false;
    }
}
