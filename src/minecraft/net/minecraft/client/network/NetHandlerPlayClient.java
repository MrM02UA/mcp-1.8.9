package net.minecraft.client.network;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.GuardianSound;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.GuiScreenDemo;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityPickupFX;
import net.minecraft.client.player.inventory.ContainerLocalMenu;
import net.minecraft.client.player.inventory.LocalBlockIntercommunication;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.stream.MetadataAchievement;
import net.minecraft.client.stream.MetadataCombat;
import net.minecraft.client.stream.MetadataPlayerDeath;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.NpcMerchant;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Items;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.network.play.server.S41PacketServerDifficulty;
import net.minecraft.network.play.server.S42PacketCombatEvent;
import net.minecraft.network.play.server.S43PacketCamera;
import net.minecraft.network.play.server.S44PacketWorldBorder;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.network.play.server.S46PacketSetCompressionLevel;
import net.minecraft.network.play.server.S47PacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import net.minecraft.network.play.server.S49PacketUpdateEntityNBT;
import net.minecraft.potion.PotionEffect;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringUtils;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.Explosion;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerPlayClient implements INetHandlerPlayClient
{
    private static final Logger logger = LogManager.getLogger();

    /**
     * The NetworkManager instance used to communicate with the server (used only by handlePlayerPosLook to update
     * positioning and handleJoinGame to inform the server of the client distribution/mods)
     */
    private final NetworkManager netManager;
    private final GameProfile profile;

    /**
     * Seems to be either null (integrated server) or an instance of either GuiMultiplayer (when connecting to a server)
     * or GuiScreenReamlsTOS (when connecting to MCO server)
     */
    private final GuiScreen guiScreenServer;

    /**
     * Reference to the Minecraft instance, which many handler methods operate on
     */
    private Minecraft gameController;

    /**
     * Reference to the current ClientWorld instance, which many handler methods operate on
     */
    private WorldClient clientWorldController;

    /**
     * True if the client has finished downloading terrain and may spawn. Set upon receipt of S08PacketPlayerPosLook,
     * reset upon respawning
     */
    private boolean doneLoadingTerrain;
    private final Map<UUID, NetworkPlayerInfo> playerInfoMap = Maps.newHashMap();
    public int currentServerMaxPlayers = 20;
    private boolean field_147308_k = false;

    /**
     * Just an ordinary random number generator, used to randomize audio pitch of item/orb pickup and randomize both
     * particlespawn offset and velocity
     */
    private final Random avRandomizer = new Random();

    public NetHandlerPlayClient(Minecraft mcIn, GuiScreen p_i46300_2_, NetworkManager p_i46300_3_, GameProfile p_i46300_4_)
    {
        this.gameController = mcIn;
        this.guiScreenServer = p_i46300_2_;
        this.netManager = p_i46300_3_;
        this.profile = p_i46300_4_;
    }

    /**
     * Clears the WorldClient instance associated with this NetHandlerPlayClient
     */
    public void cleanup()
    {
        this.clientWorldController = null;
    }

    /**
     * Registers some server properties (gametype,hardcore-mode,terraintype,difficulty,player limit), creates a new
     * WorldClient and sets the player initial dimension
     */
    public void handleJoinGame(S01PacketJoinGame packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        this.gameController.playerController = new PlayerControllerMP(this.gameController, this);
        this.clientWorldController = new WorldClient(this, new WorldSettings(0L, packetIn.getGameType(), false, packetIn.isHardcoreMode(), packetIn.getWorldType()), packetIn.getDimension(), packetIn.getDifficulty(), this.gameController.mcProfiler);
        this.gameController.gameSettings.difficulty = packetIn.getDifficulty();
        this.gameController.loadWorld(this.clientWorldController);
        this.gameController.thePlayer.dimension = packetIn.getDimension();
        this.gameController.displayGuiScreen(new GuiDownloadTerrain(this));
        this.gameController.thePlayer.setEntityId(packetIn.getEntityId());
        this.currentServerMaxPlayers = packetIn.getMaxPlayers();
        this.gameController.thePlayer.setReducedDebug(packetIn.isReducedDebugInfo());
        this.gameController.playerController.setGameType(packetIn.getGameType());
        this.gameController.gameSettings.sendSettingsToServer();
        this.netManager.sendPacket(new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString(ClientBrandRetriever.getClientModName())));
    }

    /**
     * Spawns an instance of the objecttype indicated by the packet and sets its position and momentum
     */
    public void handleSpawnObject(S0EPacketSpawnObject packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        double lvt_2_1_ = (double)packetIn.getX() / 32.0D;
        double lvt_4_1_ = (double)packetIn.getY() / 32.0D;
        double lvt_6_1_ = (double)packetIn.getZ() / 32.0D;
        Entity lvt_8_1_ = null;

        if (packetIn.getType() == 10)
        {
            lvt_8_1_ = EntityMinecart.getMinecart(this.clientWorldController, lvt_2_1_, lvt_4_1_, lvt_6_1_, EntityMinecart.EnumMinecartType.byNetworkID(packetIn.func_149009_m()));
        }
        else if (packetIn.getType() == 90)
        {
            Entity lvt_9_1_ = this.clientWorldController.getEntityByID(packetIn.func_149009_m());

            if (lvt_9_1_ instanceof EntityPlayer)
            {
                lvt_8_1_ = new EntityFishHook(this.clientWorldController, lvt_2_1_, lvt_4_1_, lvt_6_1_, (EntityPlayer)lvt_9_1_);
            }

            packetIn.func_149002_g(0);
        }
        else if (packetIn.getType() == 60)
        {
            lvt_8_1_ = new EntityArrow(this.clientWorldController, lvt_2_1_, lvt_4_1_, lvt_6_1_);
        }
        else if (packetIn.getType() == 61)
        {
            lvt_8_1_ = new EntitySnowball(this.clientWorldController, lvt_2_1_, lvt_4_1_, lvt_6_1_);
        }
        else if (packetIn.getType() == 71)
        {
            lvt_8_1_ = new EntityItemFrame(this.clientWorldController, new BlockPos(MathHelper.floor_double(lvt_2_1_), MathHelper.floor_double(lvt_4_1_), MathHelper.floor_double(lvt_6_1_)), EnumFacing.getHorizontal(packetIn.func_149009_m()));
            packetIn.func_149002_g(0);
        }
        else if (packetIn.getType() == 77)
        {
            lvt_8_1_ = new EntityLeashKnot(this.clientWorldController, new BlockPos(MathHelper.floor_double(lvt_2_1_), MathHelper.floor_double(lvt_4_1_), MathHelper.floor_double(lvt_6_1_)));
            packetIn.func_149002_g(0);
        }
        else if (packetIn.getType() == 65)
        {
            lvt_8_1_ = new EntityEnderPearl(this.clientWorldController, lvt_2_1_, lvt_4_1_, lvt_6_1_);
        }
        else if (packetIn.getType() == 72)
        {
            lvt_8_1_ = new EntityEnderEye(this.clientWorldController, lvt_2_1_, lvt_4_1_, lvt_6_1_);
        }
        else if (packetIn.getType() == 76)
        {
            lvt_8_1_ = new EntityFireworkRocket(this.clientWorldController, lvt_2_1_, lvt_4_1_, lvt_6_1_, (ItemStack)null);
        }
        else if (packetIn.getType() == 63)
        {
            lvt_8_1_ = new EntityLargeFireball(this.clientWorldController, lvt_2_1_, lvt_4_1_, lvt_6_1_, (double)packetIn.getSpeedX() / 8000.0D, (double)packetIn.getSpeedY() / 8000.0D, (double)packetIn.getSpeedZ() / 8000.0D);
            packetIn.func_149002_g(0);
        }
        else if (packetIn.getType() == 64)
        {
            lvt_8_1_ = new EntitySmallFireball(this.clientWorldController, lvt_2_1_, lvt_4_1_, lvt_6_1_, (double)packetIn.getSpeedX() / 8000.0D, (double)packetIn.getSpeedY() / 8000.0D, (double)packetIn.getSpeedZ() / 8000.0D);
            packetIn.func_149002_g(0);
        }
        else if (packetIn.getType() == 66)
        {
            lvt_8_1_ = new EntityWitherSkull(this.clientWorldController, lvt_2_1_, lvt_4_1_, lvt_6_1_, (double)packetIn.getSpeedX() / 8000.0D, (double)packetIn.getSpeedY() / 8000.0D, (double)packetIn.getSpeedZ() / 8000.0D);
            packetIn.func_149002_g(0);
        }
        else if (packetIn.getType() == 62)
        {
            lvt_8_1_ = new EntityEgg(this.clientWorldController, lvt_2_1_, lvt_4_1_, lvt_6_1_);
        }
        else if (packetIn.getType() == 73)
        {
            lvt_8_1_ = new EntityPotion(this.clientWorldController, lvt_2_1_, lvt_4_1_, lvt_6_1_, packetIn.func_149009_m());
            packetIn.func_149002_g(0);
        }
        else if (packetIn.getType() == 75)
        {
            lvt_8_1_ = new EntityExpBottle(this.clientWorldController, lvt_2_1_, lvt_4_1_, lvt_6_1_);
            packetIn.func_149002_g(0);
        }
        else if (packetIn.getType() == 1)
        {
            lvt_8_1_ = new EntityBoat(this.clientWorldController, lvt_2_1_, lvt_4_1_, lvt_6_1_);
        }
        else if (packetIn.getType() == 50)
        {
            lvt_8_1_ = new EntityTNTPrimed(this.clientWorldController, lvt_2_1_, lvt_4_1_, lvt_6_1_, (EntityLivingBase)null);
        }
        else if (packetIn.getType() == 78)
        {
            lvt_8_1_ = new EntityArmorStand(this.clientWorldController, lvt_2_1_, lvt_4_1_, lvt_6_1_);
        }
        else if (packetIn.getType() == 51)
        {
            lvt_8_1_ = new EntityEnderCrystal(this.clientWorldController, lvt_2_1_, lvt_4_1_, lvt_6_1_);
        }
        else if (packetIn.getType() == 2)
        {
            lvt_8_1_ = new EntityItem(this.clientWorldController, lvt_2_1_, lvt_4_1_, lvt_6_1_);
        }
        else if (packetIn.getType() == 70)
        {
            lvt_8_1_ = new EntityFallingBlock(this.clientWorldController, lvt_2_1_, lvt_4_1_, lvt_6_1_, Block.getStateById(packetIn.func_149009_m() & 65535));
            packetIn.func_149002_g(0);
        }

        if (lvt_8_1_ != null)
        {
            lvt_8_1_.serverPosX = packetIn.getX();
            lvt_8_1_.serverPosY = packetIn.getY();
            lvt_8_1_.serverPosZ = packetIn.getZ();
            lvt_8_1_.rotationPitch = (float)(packetIn.getPitch() * 360) / 256.0F;
            lvt_8_1_.rotationYaw = (float)(packetIn.getYaw() * 360) / 256.0F;
            Entity[] lvt_9_2_ = lvt_8_1_.getParts();

            if (lvt_9_2_ != null)
            {
                int lvt_10_1_ = packetIn.getEntityID() - lvt_8_1_.getEntityId();

                for (int lvt_11_1_ = 0; lvt_11_1_ < lvt_9_2_.length; ++lvt_11_1_)
                {
                    lvt_9_2_[lvt_11_1_].setEntityId(lvt_9_2_[lvt_11_1_].getEntityId() + lvt_10_1_);
                }
            }

            lvt_8_1_.setEntityId(packetIn.getEntityID());
            this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), lvt_8_1_);

            if (packetIn.func_149009_m() > 0)
            {
                if (packetIn.getType() == 60)
                {
                    Entity lvt_10_2_ = this.clientWorldController.getEntityByID(packetIn.func_149009_m());

                    if (lvt_10_2_ instanceof EntityLivingBase && lvt_8_1_ instanceof EntityArrow)
                    {
                        ((EntityArrow)lvt_8_1_).shootingEntity = lvt_10_2_;
                    }
                }

                lvt_8_1_.setVelocity((double)packetIn.getSpeedX() / 8000.0D, (double)packetIn.getSpeedY() / 8000.0D, (double)packetIn.getSpeedZ() / 8000.0D);
            }
        }
    }

    /**
     * Spawns an experience orb and sets its value (amount of XP)
     */
    public void handleSpawnExperienceOrb(S11PacketSpawnExperienceOrb packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity lvt_2_1_ = new EntityXPOrb(this.clientWorldController, (double)packetIn.getX() / 32.0D, (double)packetIn.getY() / 32.0D, (double)packetIn.getZ() / 32.0D, packetIn.getXPValue());
        lvt_2_1_.serverPosX = packetIn.getX();
        lvt_2_1_.serverPosY = packetIn.getY();
        lvt_2_1_.serverPosZ = packetIn.getZ();
        lvt_2_1_.rotationYaw = 0.0F;
        lvt_2_1_.rotationPitch = 0.0F;
        lvt_2_1_.setEntityId(packetIn.getEntityID());
        this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), lvt_2_1_);
    }

    /**
     * Handles globally visible entities. Used in vanilla for lightning bolts
     */
    public void handleSpawnGlobalEntity(S2CPacketSpawnGlobalEntity packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        double lvt_2_1_ = (double)packetIn.func_149051_d() / 32.0D;
        double lvt_4_1_ = (double)packetIn.func_149050_e() / 32.0D;
        double lvt_6_1_ = (double)packetIn.func_149049_f() / 32.0D;
        Entity lvt_8_1_ = null;

        if (packetIn.func_149053_g() == 1)
        {
            lvt_8_1_ = new EntityLightningBolt(this.clientWorldController, lvt_2_1_, lvt_4_1_, lvt_6_1_);
        }

        if (lvt_8_1_ != null)
        {
            lvt_8_1_.serverPosX = packetIn.func_149051_d();
            lvt_8_1_.serverPosY = packetIn.func_149050_e();
            lvt_8_1_.serverPosZ = packetIn.func_149049_f();
            lvt_8_1_.rotationYaw = 0.0F;
            lvt_8_1_.rotationPitch = 0.0F;
            lvt_8_1_.setEntityId(packetIn.func_149052_c());
            this.clientWorldController.addWeatherEffect(lvt_8_1_);
        }
    }

    /**
     * Handles the spawning of a painting object
     */
    public void handleSpawnPainting(S10PacketSpawnPainting packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        EntityPainting lvt_2_1_ = new EntityPainting(this.clientWorldController, packetIn.getPosition(), packetIn.getFacing(), packetIn.getTitle());
        this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), lvt_2_1_);
    }

    /**
     * Sets the velocity of the specified entity to the specified value
     */
    public void handleEntityVelocity(S12PacketEntityVelocity packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity lvt_2_1_ = this.clientWorldController.getEntityByID(packetIn.getEntityID());

        if (lvt_2_1_ != null)
        {
            lvt_2_1_.setVelocity((double)packetIn.getMotionX() / 8000.0D, (double)packetIn.getMotionY() / 8000.0D, (double)packetIn.getMotionZ() / 8000.0D);
        }
    }

    /**
     * Invoked when the server registers new proximate objects in your watchlist or when objects in your watchlist have
     * changed -> Registers any changes locally
     */
    public void handleEntityMetadata(S1CPacketEntityMetadata packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity lvt_2_1_ = this.clientWorldController.getEntityByID(packetIn.getEntityId());

        if (lvt_2_1_ != null && packetIn.func_149376_c() != null)
        {
            lvt_2_1_.getDataWatcher().updateWatchedObjectsFromList(packetIn.func_149376_c());
        }
    }

    /**
     * Handles the creation of a nearby player entity, sets the position and held item
     */
    public void handleSpawnPlayer(S0CPacketSpawnPlayer packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        double lvt_2_1_ = (double)packetIn.getX() / 32.0D;
        double lvt_4_1_ = (double)packetIn.getY() / 32.0D;
        double lvt_6_1_ = (double)packetIn.getZ() / 32.0D;
        float lvt_8_1_ = (float)(packetIn.getYaw() * 360) / 256.0F;
        float lvt_9_1_ = (float)(packetIn.getPitch() * 360) / 256.0F;
        EntityOtherPlayerMP lvt_10_1_ = new EntityOtherPlayerMP(this.gameController.theWorld, this.getPlayerInfo(packetIn.getPlayer()).getGameProfile());
        lvt_10_1_.prevPosX = lvt_10_1_.lastTickPosX = (double)(lvt_10_1_.serverPosX = packetIn.getX());
        lvt_10_1_.prevPosY = lvt_10_1_.lastTickPosY = (double)(lvt_10_1_.serverPosY = packetIn.getY());
        lvt_10_1_.prevPosZ = lvt_10_1_.lastTickPosZ = (double)(lvt_10_1_.serverPosZ = packetIn.getZ());
        int lvt_11_1_ = packetIn.getCurrentItemID();

        if (lvt_11_1_ == 0)
        {
            lvt_10_1_.inventory.mainInventory[lvt_10_1_.inventory.currentItem] = null;
        }
        else
        {
            lvt_10_1_.inventory.mainInventory[lvt_10_1_.inventory.currentItem] = new ItemStack(Item.getItemById(lvt_11_1_), 1, 0);
        }

        lvt_10_1_.setPositionAndRotation(lvt_2_1_, lvt_4_1_, lvt_6_1_, lvt_8_1_, lvt_9_1_);
        this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), lvt_10_1_);
        List<DataWatcher.WatchableObject> lvt_12_1_ = packetIn.func_148944_c();

        if (lvt_12_1_ != null)
        {
            lvt_10_1_.getDataWatcher().updateWatchedObjectsFromList(lvt_12_1_);
        }
    }

    /**
     * Updates an entity's position and rotation as specified by the packet
     */
    public void handleEntityTeleport(S18PacketEntityTeleport packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity lvt_2_1_ = this.clientWorldController.getEntityByID(packetIn.getEntityId());

        if (lvt_2_1_ != null)
        {
            lvt_2_1_.serverPosX = packetIn.getX();
            lvt_2_1_.serverPosY = packetIn.getY();
            lvt_2_1_.serverPosZ = packetIn.getZ();
            double lvt_3_1_ = (double)lvt_2_1_.serverPosX / 32.0D;
            double lvt_5_1_ = (double)lvt_2_1_.serverPosY / 32.0D;
            double lvt_7_1_ = (double)lvt_2_1_.serverPosZ / 32.0D;
            float lvt_9_1_ = (float)(packetIn.getYaw() * 360) / 256.0F;
            float lvt_10_1_ = (float)(packetIn.getPitch() * 360) / 256.0F;

            if (Math.abs(lvt_2_1_.posX - lvt_3_1_) < 0.03125D && Math.abs(lvt_2_1_.posY - lvt_5_1_) < 0.015625D && Math.abs(lvt_2_1_.posZ - lvt_7_1_) < 0.03125D)
            {
                lvt_2_1_.setPositionAndRotation2(lvt_2_1_.posX, lvt_2_1_.posY, lvt_2_1_.posZ, lvt_9_1_, lvt_10_1_, 3, true);
            }
            else
            {
                lvt_2_1_.setPositionAndRotation2(lvt_3_1_, lvt_5_1_, lvt_7_1_, lvt_9_1_, lvt_10_1_, 3, true);
            }

            lvt_2_1_.onGround = packetIn.getOnGround();
        }
    }

    /**
     * Updates which hotbar slot of the player is currently selected
     */
    public void handleHeldItemChange(S09PacketHeldItemChange packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

        if (packetIn.getHeldItemHotbarIndex() >= 0 && packetIn.getHeldItemHotbarIndex() < InventoryPlayer.getHotbarSize())
        {
            this.gameController.thePlayer.inventory.currentItem = packetIn.getHeldItemHotbarIndex();
        }
    }

    /**
     * Updates the specified entity's position by the specified relative moment and absolute rotation. Note that
     * subclassing of the packet allows for the specification of a subset of this data (e.g. only rel. position, abs.
     * rotation or both).
     */
    public void handleEntityMovement(S14PacketEntity packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity lvt_2_1_ = packetIn.getEntity(this.clientWorldController);

        if (lvt_2_1_ != null)
        {
            lvt_2_1_.serverPosX += packetIn.func_149062_c();
            lvt_2_1_.serverPosY += packetIn.func_149061_d();
            lvt_2_1_.serverPosZ += packetIn.func_149064_e();
            double lvt_3_1_ = (double)lvt_2_1_.serverPosX / 32.0D;
            double lvt_5_1_ = (double)lvt_2_1_.serverPosY / 32.0D;
            double lvt_7_1_ = (double)lvt_2_1_.serverPosZ / 32.0D;
            float lvt_9_1_ = packetIn.func_149060_h() ? (float)(packetIn.func_149066_f() * 360) / 256.0F : lvt_2_1_.rotationYaw;
            float lvt_10_1_ = packetIn.func_149060_h() ? (float)(packetIn.func_149063_g() * 360) / 256.0F : lvt_2_1_.rotationPitch;
            lvt_2_1_.setPositionAndRotation2(lvt_3_1_, lvt_5_1_, lvt_7_1_, lvt_9_1_, lvt_10_1_, 3, false);
            lvt_2_1_.onGround = packetIn.getOnGround();
        }
    }

    /**
     * Updates the direction in which the specified entity is looking, normally this head rotation is independent of the
     * rotation of the entity itself
     */
    public void handleEntityHeadLook(S19PacketEntityHeadLook packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity lvt_2_1_ = packetIn.getEntity(this.clientWorldController);

        if (lvt_2_1_ != null)
        {
            float lvt_3_1_ = (float)(packetIn.getYaw() * 360) / 256.0F;
            lvt_2_1_.setRotationYawHead(lvt_3_1_);
        }
    }

    /**
     * Locally eliminates the entities. Invoked by the server when the items are in fact destroyed, or the player is no
     * longer registered as required to monitor them. The latter  happens when distance between the player and item
     * increases beyond a certain treshold (typically the viewing distance)
     */
    public void handleDestroyEntities(S13PacketDestroyEntities packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

        for (int lvt_2_1_ = 0; lvt_2_1_ < packetIn.getEntityIDs().length; ++lvt_2_1_)
        {
            this.clientWorldController.removeEntityFromWorld(packetIn.getEntityIDs()[lvt_2_1_]);
        }
    }

    /**
     * Handles changes in player positioning and rotation such as when travelling to a new dimension, (re)spawning,
     * mounting horses etc. Seems to immediately reply to the server with the clients post-processing perspective on the
     * player positioning
     */
    public void handlePlayerPosLook(S08PacketPlayerPosLook packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        EntityPlayer lvt_2_1_ = this.gameController.thePlayer;
        double lvt_3_1_ = packetIn.getX();
        double lvt_5_1_ = packetIn.getY();
        double lvt_7_1_ = packetIn.getZ();
        float lvt_9_1_ = packetIn.getYaw();
        float lvt_10_1_ = packetIn.getPitch();

        if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X))
        {
            lvt_3_1_ += lvt_2_1_.posX;
        }
        else
        {
            lvt_2_1_.motionX = 0.0D;
        }

        if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y))
        {
            lvt_5_1_ += lvt_2_1_.posY;
        }
        else
        {
            lvt_2_1_.motionY = 0.0D;
        }

        if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Z))
        {
            lvt_7_1_ += lvt_2_1_.posZ;
        }
        else
        {
            lvt_2_1_.motionZ = 0.0D;
        }

        if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X_ROT))
        {
            lvt_10_1_ += lvt_2_1_.rotationPitch;
        }

        if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y_ROT))
        {
            lvt_9_1_ += lvt_2_1_.rotationYaw;
        }

        lvt_2_1_.setPositionAndRotation(lvt_3_1_, lvt_5_1_, lvt_7_1_, lvt_9_1_, lvt_10_1_);
        this.netManager.sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(lvt_2_1_.posX, lvt_2_1_.getEntityBoundingBox().minY, lvt_2_1_.posZ, lvt_2_1_.rotationYaw, lvt_2_1_.rotationPitch, false));

        if (!this.doneLoadingTerrain)
        {
            this.gameController.thePlayer.prevPosX = this.gameController.thePlayer.posX;
            this.gameController.thePlayer.prevPosY = this.gameController.thePlayer.posY;
            this.gameController.thePlayer.prevPosZ = this.gameController.thePlayer.posZ;
            this.doneLoadingTerrain = true;
            this.gameController.displayGuiScreen((GuiScreen)null);
        }
    }

    /**
     * Received from the servers PlayerManager if between 1 and 64 blocks in a chunk are changed. If only one block
     * requires an update, the server sends S23PacketBlockChange and if 64 or more blocks are changed, the server sends
     * S21PacketChunkData
     */
    public void handleMultiBlockChange(S22PacketMultiBlockChange packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

        for (S22PacketMultiBlockChange.BlockUpdateData lvt_5_1_ : packetIn.getChangedBlocks())
        {
            this.clientWorldController.invalidateRegionAndSetBlock(lvt_5_1_.getPos(), lvt_5_1_.getBlockState());
        }
    }

    /**
     * Updates the specified chunk with the supplied data, marks it for re-rendering and lighting recalculation
     */
    public void handleChunkData(S21PacketChunkData packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

        if (packetIn.func_149274_i())
        {
            if (packetIn.getExtractedSize() == 0)
            {
                this.clientWorldController.doPreChunk(packetIn.getChunkX(), packetIn.getChunkZ(), false);
                return;
            }

            this.clientWorldController.doPreChunk(packetIn.getChunkX(), packetIn.getChunkZ(), true);
        }

        this.clientWorldController.invalidateBlockReceiveRegion(packetIn.getChunkX() << 4, 0, packetIn.getChunkZ() << 4, (packetIn.getChunkX() << 4) + 15, 256, (packetIn.getChunkZ() << 4) + 15);
        Chunk lvt_2_1_ = this.clientWorldController.getChunkFromChunkCoords(packetIn.getChunkX(), packetIn.getChunkZ());
        lvt_2_1_.fillChunk(packetIn.getExtractedDataBytes(), packetIn.getExtractedSize(), packetIn.func_149274_i());
        this.clientWorldController.markBlockRangeForRenderUpdate(packetIn.getChunkX() << 4, 0, packetIn.getChunkZ() << 4, (packetIn.getChunkX() << 4) + 15, 256, (packetIn.getChunkZ() << 4) + 15);

        if (!packetIn.func_149274_i() || !(this.clientWorldController.provider instanceof WorldProviderSurface))
        {
            lvt_2_1_.resetRelightChecks();
        }
    }

    /**
     * Updates the block and metadata and generates a blockupdate (and notify the clients)
     */
    public void handleBlockChange(S23PacketBlockChange packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        this.clientWorldController.invalidateRegionAndSetBlock(packetIn.getBlockPosition(), packetIn.getBlockState());
    }

    /**
     * Closes the network channel
     */
    public void handleDisconnect(S40PacketDisconnect packetIn)
    {
        this.netManager.closeChannel(packetIn.getReason());
    }

    /**
     * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
     */
    public void onDisconnect(IChatComponent reason)
    {
        this.gameController.loadWorld((WorldClient)null);

        if (this.guiScreenServer != null)
        {
            if (this.guiScreenServer instanceof GuiScreenRealmsProxy)
            {
                this.gameController.displayGuiScreen((new DisconnectedRealmsScreen(((GuiScreenRealmsProxy)this.guiScreenServer).func_154321_a(), "disconnect.lost", reason)).getProxy());
            }
            else
            {
                this.gameController.displayGuiScreen(new GuiDisconnected(this.guiScreenServer, "disconnect.lost", reason));
            }
        }
        else
        {
            this.gameController.displayGuiScreen(new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), "disconnect.lost", reason));
        }
    }

    public void addToSendQueue(Packet p_147297_1_)
    {
        this.netManager.sendPacket(p_147297_1_);
    }

    public void handleCollectItem(S0DPacketCollectItem packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity lvt_2_1_ = this.clientWorldController.getEntityByID(packetIn.getCollectedItemEntityID());
        EntityLivingBase lvt_3_1_ = (EntityLivingBase)this.clientWorldController.getEntityByID(packetIn.getEntityID());

        if (lvt_3_1_ == null)
        {
            lvt_3_1_ = this.gameController.thePlayer;
        }

        if (lvt_2_1_ != null)
        {
            if (lvt_2_1_ instanceof EntityXPOrb)
            {
                this.clientWorldController.playSoundAtEntity(lvt_2_1_, "random.orb", 0.2F, ((this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }
            else
            {
                this.clientWorldController.playSoundAtEntity(lvt_2_1_, "random.pop", 0.2F, ((this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }

            this.gameController.effectRenderer.addEffect(new EntityPickupFX(this.clientWorldController, lvt_2_1_, lvt_3_1_, 0.5F));
            this.clientWorldController.removeEntityFromWorld(packetIn.getCollectedItemEntityID());
        }
    }

    /**
     * Prints a chatmessage in the chat GUI
     */
    public void handleChat(S02PacketChat packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

        if (packetIn.getType() == 2)
        {
            this.gameController.ingameGUI.setRecordPlaying(packetIn.getChatComponent(), false);
        }
        else
        {
            this.gameController.ingameGUI.getChatGUI().printChatMessage(packetIn.getChatComponent());
        }
    }

    /**
     * Renders a specified animation: Waking up a player, a living entity swinging its currently held item, being hurt
     * or receiving a critical hit by normal or magical means
     */
    public void handleAnimation(S0BPacketAnimation packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity lvt_2_1_ = this.clientWorldController.getEntityByID(packetIn.getEntityID());

        if (lvt_2_1_ != null)
        {
            if (packetIn.getAnimationType() == 0)
            {
                EntityLivingBase lvt_3_1_ = (EntityLivingBase)lvt_2_1_;
                lvt_3_1_.swingItem();
            }
            else if (packetIn.getAnimationType() == 1)
            {
                lvt_2_1_.performHurtAnimation();
            }
            else if (packetIn.getAnimationType() == 2)
            {
                EntityPlayer lvt_3_2_ = (EntityPlayer)lvt_2_1_;
                lvt_3_2_.wakeUpPlayer(false, false, false);
            }
            else if (packetIn.getAnimationType() == 4)
            {
                this.gameController.effectRenderer.emitParticleAtEntity(lvt_2_1_, EnumParticleTypes.CRIT);
            }
            else if (packetIn.getAnimationType() == 5)
            {
                this.gameController.effectRenderer.emitParticleAtEntity(lvt_2_1_, EnumParticleTypes.CRIT_MAGIC);
            }
        }
    }

    /**
     * Retrieves the player identified by the packet, puts him to sleep if possible (and flags whether all players are
     * asleep)
     */
    public void handleUseBed(S0APacketUseBed packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        packetIn.getPlayer(this.clientWorldController).trySleep(packetIn.getBedPosition());
    }

    /**
     * Spawns the mob entity at the specified location, with the specified rotation, momentum and type. Updates the
     * entities Datawatchers with the entity metadata specified in the packet
     */
    public void handleSpawnMob(S0FPacketSpawnMob packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        double lvt_2_1_ = (double)packetIn.getX() / 32.0D;
        double lvt_4_1_ = (double)packetIn.getY() / 32.0D;
        double lvt_6_1_ = (double)packetIn.getZ() / 32.0D;
        float lvt_8_1_ = (float)(packetIn.getYaw() * 360) / 256.0F;
        float lvt_9_1_ = (float)(packetIn.getPitch() * 360) / 256.0F;
        EntityLivingBase lvt_10_1_ = (EntityLivingBase)EntityList.createEntityByID(packetIn.getEntityType(), this.gameController.theWorld);
        lvt_10_1_.serverPosX = packetIn.getX();
        lvt_10_1_.serverPosY = packetIn.getY();
        lvt_10_1_.serverPosZ = packetIn.getZ();
        lvt_10_1_.renderYawOffset = lvt_10_1_.rotationYawHead = (float)(packetIn.getHeadPitch() * 360) / 256.0F;
        Entity[] lvt_11_1_ = lvt_10_1_.getParts();

        if (lvt_11_1_ != null)
        {
            int lvt_12_1_ = packetIn.getEntityID() - lvt_10_1_.getEntityId();

            for (int lvt_13_1_ = 0; lvt_13_1_ < lvt_11_1_.length; ++lvt_13_1_)
            {
                lvt_11_1_[lvt_13_1_].setEntityId(lvt_11_1_[lvt_13_1_].getEntityId() + lvt_12_1_);
            }
        }

        lvt_10_1_.setEntityId(packetIn.getEntityID());
        lvt_10_1_.setPositionAndRotation(lvt_2_1_, lvt_4_1_, lvt_6_1_, lvt_8_1_, lvt_9_1_);
        lvt_10_1_.motionX = (double)((float)packetIn.getVelocityX() / 8000.0F);
        lvt_10_1_.motionY = (double)((float)packetIn.getVelocityY() / 8000.0F);
        lvt_10_1_.motionZ = (double)((float)packetIn.getVelocityZ() / 8000.0F);
        this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), lvt_10_1_);
        List<DataWatcher.WatchableObject> lvt_12_2_ = packetIn.func_149027_c();

        if (lvt_12_2_ != null)
        {
            lvt_10_1_.getDataWatcher().updateWatchedObjectsFromList(lvt_12_2_);
        }
    }

    public void handleTimeUpdate(S03PacketTimeUpdate packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        this.gameController.theWorld.setTotalWorldTime(packetIn.getTotalWorldTime());
        this.gameController.theWorld.setWorldTime(packetIn.getWorldTime());
    }

    public void handleSpawnPosition(S05PacketSpawnPosition packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        this.gameController.thePlayer.setSpawnPoint(packetIn.getSpawnPos(), true);
        this.gameController.theWorld.getWorldInfo().setSpawn(packetIn.getSpawnPos());
    }

    public void handleEntityAttach(S1BPacketEntityAttach packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity lvt_2_1_ = this.clientWorldController.getEntityByID(packetIn.getEntityId());
        Entity lvt_3_1_ = this.clientWorldController.getEntityByID(packetIn.getVehicleEntityId());

        if (packetIn.getLeash() == 0)
        {
            boolean lvt_4_1_ = false;

            if (packetIn.getEntityId() == this.gameController.thePlayer.getEntityId())
            {
                lvt_2_1_ = this.gameController.thePlayer;

                if (lvt_3_1_ instanceof EntityBoat)
                {
                    ((EntityBoat)lvt_3_1_).setIsBoatEmpty(false);
                }

                lvt_4_1_ = lvt_2_1_.ridingEntity == null && lvt_3_1_ != null;
            }
            else if (lvt_3_1_ instanceof EntityBoat)
            {
                ((EntityBoat)lvt_3_1_).setIsBoatEmpty(true);
            }

            if (lvt_2_1_ == null)
            {
                return;
            }

            lvt_2_1_.mountEntity(lvt_3_1_);

            if (lvt_4_1_)
            {
                GameSettings lvt_5_1_ = this.gameController.gameSettings;
                this.gameController.ingameGUI.setRecordPlaying(I18n.format("mount.onboard", new Object[] {GameSettings.getKeyDisplayString(lvt_5_1_.keyBindSneak.getKeyCode())}), false);
            }
        }
        else if (packetIn.getLeash() == 1 && lvt_2_1_ instanceof EntityLiving)
        {
            if (lvt_3_1_ != null)
            {
                ((EntityLiving)lvt_2_1_).setLeashedToEntity(lvt_3_1_, false);
            }
            else
            {
                ((EntityLiving)lvt_2_1_).clearLeashed(false, false);
            }
        }
    }

    /**
     * Invokes the entities' handleUpdateHealth method which is implemented in LivingBase (hurt/death),
     * MinecartMobSpawner (spawn delay), FireworkRocket & MinecartTNT (explosion), IronGolem (throwing,...), Witch
     * (spawn particles), Zombie (villager transformation), Animal (breeding mode particles), Horse (breeding/smoke
     * particles), Sheep (...), Tameable (...), Villager (particles for breeding mode, angry and happy), Wolf (...)
     */
    public void handleEntityStatus(S19PacketEntityStatus packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity lvt_2_1_ = packetIn.getEntity(this.clientWorldController);

        if (lvt_2_1_ != null)
        {
            if (packetIn.getOpCode() == 21)
            {
                this.gameController.getSoundHandler().playSound(new GuardianSound((EntityGuardian)lvt_2_1_));
            }
            else
            {
                lvt_2_1_.handleStatusUpdate(packetIn.getOpCode());
            }
        }
    }

    public void handleUpdateHealth(S06PacketUpdateHealth packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        this.gameController.thePlayer.setPlayerSPHealth(packetIn.getHealth());
        this.gameController.thePlayer.getFoodStats().setFoodLevel(packetIn.getFoodLevel());
        this.gameController.thePlayer.getFoodStats().setFoodSaturationLevel(packetIn.getSaturationLevel());
    }

    public void handleSetExperience(S1FPacketSetExperience packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        this.gameController.thePlayer.setXPStats(packetIn.func_149397_c(), packetIn.getTotalExperience(), packetIn.getLevel());
    }

    public void handleRespawn(S07PacketRespawn packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

        if (packetIn.getDimensionID() != this.gameController.thePlayer.dimension)
        {
            this.doneLoadingTerrain = false;
            Scoreboard lvt_2_1_ = this.clientWorldController.getScoreboard();
            this.clientWorldController = new WorldClient(this, new WorldSettings(0L, packetIn.getGameType(), false, this.gameController.theWorld.getWorldInfo().isHardcoreModeEnabled(), packetIn.getWorldType()), packetIn.getDimensionID(), packetIn.getDifficulty(), this.gameController.mcProfiler);
            this.clientWorldController.setWorldScoreboard(lvt_2_1_);
            this.gameController.loadWorld(this.clientWorldController);
            this.gameController.thePlayer.dimension = packetIn.getDimensionID();
            this.gameController.displayGuiScreen(new GuiDownloadTerrain(this));
        }

        this.gameController.setDimensionAndSpawnPlayer(packetIn.getDimensionID());
        this.gameController.playerController.setGameType(packetIn.getGameType());
    }

    /**
     * Initiates a new explosion (sound, particles, drop spawn) for the affected blocks indicated by the packet.
     */
    public void handleExplosion(S27PacketExplosion packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Explosion lvt_2_1_ = new Explosion(this.gameController.theWorld, (Entity)null, packetIn.getX(), packetIn.getY(), packetIn.getZ(), packetIn.getStrength(), packetIn.getAffectedBlockPositions());
        lvt_2_1_.doExplosionB(true);
        this.gameController.thePlayer.motionX += (double)packetIn.func_149149_c();
        this.gameController.thePlayer.motionY += (double)packetIn.func_149144_d();
        this.gameController.thePlayer.motionZ += (double)packetIn.func_149147_e();
    }

    /**
     * Displays a GUI by ID. In order starting from id 0: Chest, Workbench, Furnace, Dispenser, Enchanting table,
     * Brewing stand, Villager merchant, Beacon, Anvil, Hopper, Dropper, Horse
     */
    public void handleOpenWindow(S2DPacketOpenWindow packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        EntityPlayerSP lvt_2_1_ = this.gameController.thePlayer;

        if ("minecraft:container".equals(packetIn.getGuiId()))
        {
            lvt_2_1_.displayGUIChest(new InventoryBasic(packetIn.getWindowTitle(), packetIn.getSlotCount()));
            lvt_2_1_.openContainer.windowId = packetIn.getWindowId();
        }
        else if ("minecraft:villager".equals(packetIn.getGuiId()))
        {
            lvt_2_1_.displayVillagerTradeGui(new NpcMerchant(lvt_2_1_, packetIn.getWindowTitle()));
            lvt_2_1_.openContainer.windowId = packetIn.getWindowId();
        }
        else if ("EntityHorse".equals(packetIn.getGuiId()))
        {
            Entity lvt_3_1_ = this.clientWorldController.getEntityByID(packetIn.getEntityId());

            if (lvt_3_1_ instanceof EntityHorse)
            {
                lvt_2_1_.displayGUIHorse((EntityHorse)lvt_3_1_, new AnimalChest(packetIn.getWindowTitle(), packetIn.getSlotCount()));
                lvt_2_1_.openContainer.windowId = packetIn.getWindowId();
            }
        }
        else if (!packetIn.hasSlots())
        {
            lvt_2_1_.displayGui(new LocalBlockIntercommunication(packetIn.getGuiId(), packetIn.getWindowTitle()));
            lvt_2_1_.openContainer.windowId = packetIn.getWindowId();
        }
        else
        {
            ContainerLocalMenu lvt_3_2_ = new ContainerLocalMenu(packetIn.getGuiId(), packetIn.getWindowTitle(), packetIn.getSlotCount());
            lvt_2_1_.displayGUIChest(lvt_3_2_);
            lvt_2_1_.openContainer.windowId = packetIn.getWindowId();
        }
    }

    /**
     * Handles pickin up an ItemStack or dropping one in your inventory or an open (non-creative) container
     */
    public void handleSetSlot(S2FPacketSetSlot packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        EntityPlayer lvt_2_1_ = this.gameController.thePlayer;

        if (packetIn.func_149175_c() == -1)
        {
            lvt_2_1_.inventory.setItemStack(packetIn.func_149174_e());
        }
        else
        {
            boolean lvt_3_1_ = false;

            if (this.gameController.currentScreen instanceof GuiContainerCreative)
            {
                GuiContainerCreative lvt_4_1_ = (GuiContainerCreative)this.gameController.currentScreen;
                lvt_3_1_ = lvt_4_1_.getSelectedTabIndex() != CreativeTabs.tabInventory.getTabIndex();
            }

            if (packetIn.func_149175_c() == 0 && packetIn.func_149173_d() >= 36 && packetIn.func_149173_d() < 45)
            {
                ItemStack lvt_4_2_ = lvt_2_1_.inventoryContainer.getSlot(packetIn.func_149173_d()).getStack();

                if (packetIn.func_149174_e() != null && (lvt_4_2_ == null || lvt_4_2_.stackSize < packetIn.func_149174_e().stackSize))
                {
                    packetIn.func_149174_e().animationsToGo = 5;
                }

                lvt_2_1_.inventoryContainer.putStackInSlot(packetIn.func_149173_d(), packetIn.func_149174_e());
            }
            else if (packetIn.func_149175_c() == lvt_2_1_.openContainer.windowId && (packetIn.func_149175_c() != 0 || !lvt_3_1_))
            {
                lvt_2_1_.openContainer.putStackInSlot(packetIn.func_149173_d(), packetIn.func_149174_e());
            }
        }
    }

    /**
     * Verifies that the server and client are synchronized with respect to the inventory/container opened by the player
     * and confirms if it is the case.
     */
    public void handleConfirmTransaction(S32PacketConfirmTransaction packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Container lvt_2_1_ = null;
        EntityPlayer lvt_3_1_ = this.gameController.thePlayer;

        if (packetIn.getWindowId() == 0)
        {
            lvt_2_1_ = lvt_3_1_.inventoryContainer;
        }
        else if (packetIn.getWindowId() == lvt_3_1_.openContainer.windowId)
        {
            lvt_2_1_ = lvt_3_1_.openContainer;
        }

        if (lvt_2_1_ != null && !packetIn.func_148888_e())
        {
            this.addToSendQueue(new C0FPacketConfirmTransaction(packetIn.getWindowId(), packetIn.getActionNumber(), true));
        }
    }

    /**
     * Handles the placement of a specified ItemStack in a specified container/inventory slot
     */
    public void handleWindowItems(S30PacketWindowItems packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        EntityPlayer lvt_2_1_ = this.gameController.thePlayer;

        if (packetIn.func_148911_c() == 0)
        {
            lvt_2_1_.inventoryContainer.putStacksInSlots(packetIn.getItemStacks());
        }
        else if (packetIn.func_148911_c() == lvt_2_1_.openContainer.windowId)
        {
            lvt_2_1_.openContainer.putStacksInSlots(packetIn.getItemStacks());
        }
    }

    /**
     * Creates a sign in the specified location if it didn't exist and opens the GUI to edit its text
     */
    public void handleSignEditorOpen(S36PacketSignEditorOpen packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        TileEntity lvt_2_1_ = this.clientWorldController.getTileEntity(packetIn.getSignPosition());

        if (!(lvt_2_1_ instanceof TileEntitySign))
        {
            lvt_2_1_ = new TileEntitySign();
            lvt_2_1_.setWorldObj(this.clientWorldController);
            lvt_2_1_.setPos(packetIn.getSignPosition());
        }

        this.gameController.thePlayer.openEditSign((TileEntitySign)lvt_2_1_);
    }

    /**
     * Updates a specified sign with the specified text lines
     */
    public void handleUpdateSign(S33PacketUpdateSign packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        boolean lvt_2_1_ = false;

        if (this.gameController.theWorld.isBlockLoaded(packetIn.getPos()))
        {
            TileEntity lvt_3_1_ = this.gameController.theWorld.getTileEntity(packetIn.getPos());

            if (lvt_3_1_ instanceof TileEntitySign)
            {
                TileEntitySign lvt_4_1_ = (TileEntitySign)lvt_3_1_;

                if (lvt_4_1_.getIsEditable())
                {
                    System.arraycopy(packetIn.getLines(), 0, lvt_4_1_.signText, 0, 4);
                    lvt_4_1_.markDirty();
                }

                lvt_2_1_ = true;
            }
        }

        if (!lvt_2_1_ && this.gameController.thePlayer != null)
        {
            this.gameController.thePlayer.addChatMessage(new ChatComponentText("Unable to locate sign at " + packetIn.getPos().getX() + ", " + packetIn.getPos().getY() + ", " + packetIn.getPos().getZ()));
        }
    }

    /**
     * Updates the NBTTagCompound metadata of instances of the following entitytypes: Mob spawners, command blocks,
     * beacons, skulls, flowerpot
     */
    public void handleUpdateTileEntity(S35PacketUpdateTileEntity packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

        if (this.gameController.theWorld.isBlockLoaded(packetIn.getPos()))
        {
            TileEntity lvt_2_1_ = this.gameController.theWorld.getTileEntity(packetIn.getPos());
            int lvt_3_1_ = packetIn.getTileEntityType();

            if (lvt_3_1_ == 1 && lvt_2_1_ instanceof TileEntityMobSpawner || lvt_3_1_ == 2 && lvt_2_1_ instanceof TileEntityCommandBlock || lvt_3_1_ == 3 && lvt_2_1_ instanceof TileEntityBeacon || lvt_3_1_ == 4 && lvt_2_1_ instanceof TileEntitySkull || lvt_3_1_ == 5 && lvt_2_1_ instanceof TileEntityFlowerPot || lvt_3_1_ == 6 && lvt_2_1_ instanceof TileEntityBanner)
            {
                lvt_2_1_.readFromNBT(packetIn.getNbtCompound());
            }
        }
    }

    /**
     * Sets the progressbar of the opened window to the specified value
     */
    public void handleWindowProperty(S31PacketWindowProperty packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        EntityPlayer lvt_2_1_ = this.gameController.thePlayer;

        if (lvt_2_1_.openContainer != null && lvt_2_1_.openContainer.windowId == packetIn.getWindowId())
        {
            lvt_2_1_.openContainer.updateProgressBar(packetIn.getVarIndex(), packetIn.getVarValue());
        }
    }

    public void handleEntityEquipment(S04PacketEntityEquipment packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity lvt_2_1_ = this.clientWorldController.getEntityByID(packetIn.getEntityID());

        if (lvt_2_1_ != null)
        {
            lvt_2_1_.setCurrentItemOrArmor(packetIn.getEquipmentSlot(), packetIn.getItemStack());
        }
    }

    /**
     * Resets the ItemStack held in hand and closes the window that is opened
     */
    public void handleCloseWindow(S2EPacketCloseWindow packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        this.gameController.thePlayer.closeScreenAndDropStack();
    }

    /**
     * Triggers Block.onBlockEventReceived, which is implemented in BlockPistonBase for extension/retraction, BlockNote
     * for setting the instrument (including audiovisual feedback) and in BlockContainer to set the number of players
     * accessing a (Ender)Chest
     */
    public void handleBlockAction(S24PacketBlockAction packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        this.gameController.theWorld.addBlockEvent(packetIn.getBlockPosition(), packetIn.getBlockType(), packetIn.getData1(), packetIn.getData2());
    }

    /**
     * Updates all registered IWorldAccess instances with destroyBlockInWorldPartially
     */
    public void handleBlockBreakAnim(S25PacketBlockBreakAnim packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        this.gameController.theWorld.sendBlockBreakProgress(packetIn.getBreakerId(), packetIn.getPosition(), packetIn.getProgress());
    }

    public void handleMapChunkBulk(S26PacketMapChunkBulk packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

        for (int lvt_2_1_ = 0; lvt_2_1_ < packetIn.getChunkCount(); ++lvt_2_1_)
        {
            int lvt_3_1_ = packetIn.getChunkX(lvt_2_1_);
            int lvt_4_1_ = packetIn.getChunkZ(lvt_2_1_);
            this.clientWorldController.doPreChunk(lvt_3_1_, lvt_4_1_, true);
            this.clientWorldController.invalidateBlockReceiveRegion(lvt_3_1_ << 4, 0, lvt_4_1_ << 4, (lvt_3_1_ << 4) + 15, 256, (lvt_4_1_ << 4) + 15);
            Chunk lvt_5_1_ = this.clientWorldController.getChunkFromChunkCoords(lvt_3_1_, lvt_4_1_);
            lvt_5_1_.fillChunk(packetIn.getChunkBytes(lvt_2_1_), packetIn.getChunkSize(lvt_2_1_), true);
            this.clientWorldController.markBlockRangeForRenderUpdate(lvt_3_1_ << 4, 0, lvt_4_1_ << 4, (lvt_3_1_ << 4) + 15, 256, (lvt_4_1_ << 4) + 15);

            if (!(this.clientWorldController.provider instanceof WorldProviderSurface))
            {
                lvt_5_1_.resetRelightChecks();
            }
        }
    }

    public void handleChangeGameState(S2BPacketChangeGameState packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        EntityPlayer lvt_2_1_ = this.gameController.thePlayer;
        int lvt_3_1_ = packetIn.getGameState();
        float lvt_4_1_ = packetIn.func_149137_d();
        int lvt_5_1_ = MathHelper.floor_float(lvt_4_1_ + 0.5F);

        if (lvt_3_1_ >= 0 && lvt_3_1_ < S2BPacketChangeGameState.MESSAGE_NAMES.length && S2BPacketChangeGameState.MESSAGE_NAMES[lvt_3_1_] != null)
        {
            lvt_2_1_.addChatComponentMessage(new ChatComponentTranslation(S2BPacketChangeGameState.MESSAGE_NAMES[lvt_3_1_], new Object[0]));
        }

        if (lvt_3_1_ == 1)
        {
            this.clientWorldController.getWorldInfo().setRaining(true);
            this.clientWorldController.setRainStrength(0.0F);
        }
        else if (lvt_3_1_ == 2)
        {
            this.clientWorldController.getWorldInfo().setRaining(false);
            this.clientWorldController.setRainStrength(1.0F);
        }
        else if (lvt_3_1_ == 3)
        {
            this.gameController.playerController.setGameType(WorldSettings.GameType.getByID(lvt_5_1_));
        }
        else if (lvt_3_1_ == 4)
        {
            this.gameController.displayGuiScreen(new GuiWinGame());
        }
        else if (lvt_3_1_ == 5)
        {
            GameSettings lvt_6_1_ = this.gameController.gameSettings;

            if (lvt_4_1_ == 0.0F)
            {
                this.gameController.displayGuiScreen(new GuiScreenDemo());
            }
            else if (lvt_4_1_ == 101.0F)
            {
                this.gameController.ingameGUI.getChatGUI().printChatMessage(new ChatComponentTranslation("demo.help.movement", new Object[] {GameSettings.getKeyDisplayString(lvt_6_1_.keyBindForward.getKeyCode()), GameSettings.getKeyDisplayString(lvt_6_1_.keyBindLeft.getKeyCode()), GameSettings.getKeyDisplayString(lvt_6_1_.keyBindBack.getKeyCode()), GameSettings.getKeyDisplayString(lvt_6_1_.keyBindRight.getKeyCode())}));
            }
            else if (lvt_4_1_ == 102.0F)
            {
                this.gameController.ingameGUI.getChatGUI().printChatMessage(new ChatComponentTranslation("demo.help.jump", new Object[] {GameSettings.getKeyDisplayString(lvt_6_1_.keyBindJump.getKeyCode())}));
            }
            else if (lvt_4_1_ == 103.0F)
            {
                this.gameController.ingameGUI.getChatGUI().printChatMessage(new ChatComponentTranslation("demo.help.inventory", new Object[] {GameSettings.getKeyDisplayString(lvt_6_1_.keyBindInventory.getKeyCode())}));
            }
        }
        else if (lvt_3_1_ == 6)
        {
            this.clientWorldController.playSound(lvt_2_1_.posX, lvt_2_1_.posY + (double)lvt_2_1_.getEyeHeight(), lvt_2_1_.posZ, "random.successful_hit", 0.18F, 0.45F, false);
        }
        else if (lvt_3_1_ == 7)
        {
            this.clientWorldController.setRainStrength(lvt_4_1_);
        }
        else if (lvt_3_1_ == 8)
        {
            this.clientWorldController.setThunderStrength(lvt_4_1_);
        }
        else if (lvt_3_1_ == 10)
        {
            this.clientWorldController.spawnParticle(EnumParticleTypes.MOB_APPEARANCE, lvt_2_1_.posX, lvt_2_1_.posY, lvt_2_1_.posZ, 0.0D, 0.0D, 0.0D, new int[0]);
            this.clientWorldController.playSound(lvt_2_1_.posX, lvt_2_1_.posY, lvt_2_1_.posZ, "mob.guardian.curse", 1.0F, 1.0F, false);
        }
    }

    /**
     * Updates the worlds MapStorage with the specified MapData for the specified map-identifier and invokes a
     * MapItemRenderer for it
     */
    public void handleMaps(S34PacketMaps packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        MapData lvt_2_1_ = ItemMap.loadMapData(packetIn.getMapId(), this.gameController.theWorld);
        packetIn.setMapdataTo(lvt_2_1_);
        this.gameController.entityRenderer.getMapItemRenderer().updateMapTexture(lvt_2_1_);
    }

    public void handleEffect(S28PacketEffect packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

        if (packetIn.isSoundServerwide())
        {
            this.gameController.theWorld.playBroadcastSound(packetIn.getSoundType(), packetIn.getSoundPos(), packetIn.getSoundData());
        }
        else
        {
            this.gameController.theWorld.playAuxSFX(packetIn.getSoundType(), packetIn.getSoundPos(), packetIn.getSoundData());
        }
    }

    /**
     * Updates the players statistics or achievements
     */
    public void handleStatistics(S37PacketStatistics packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        boolean lvt_2_1_ = false;

        for (Entry<StatBase, Integer> lvt_4_1_ : packetIn.func_148974_c().entrySet())
        {
            StatBase lvt_5_1_ = (StatBase)lvt_4_1_.getKey();
            int lvt_6_1_ = ((Integer)lvt_4_1_.getValue()).intValue();

            if (lvt_5_1_.isAchievement() && lvt_6_1_ > 0)
            {
                if (this.field_147308_k && this.gameController.thePlayer.getStatFileWriter().readStat(lvt_5_1_) == 0)
                {
                    Achievement lvt_7_1_ = (Achievement)lvt_5_1_;
                    this.gameController.guiAchievement.displayAchievement(lvt_7_1_);
                    this.gameController.getTwitchStream().func_152911_a(new MetadataAchievement(lvt_7_1_), 0L);

                    if (lvt_5_1_ == AchievementList.openInventory)
                    {
                        this.gameController.gameSettings.showInventoryAchievementHint = false;
                        this.gameController.gameSettings.saveOptions();
                    }
                }

                lvt_2_1_ = true;
            }

            this.gameController.thePlayer.getStatFileWriter().unlockAchievement(this.gameController.thePlayer, lvt_5_1_, lvt_6_1_);
        }

        if (!this.field_147308_k && !lvt_2_1_ && this.gameController.gameSettings.showInventoryAchievementHint)
        {
            this.gameController.guiAchievement.displayUnformattedAchievement(AchievementList.openInventory);
        }

        this.field_147308_k = true;

        if (this.gameController.currentScreen instanceof IProgressMeter)
        {
            ((IProgressMeter)this.gameController.currentScreen).doneLoading();
        }
    }

    public void handleEntityEffect(S1DPacketEntityEffect packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity lvt_2_1_ = this.clientWorldController.getEntityByID(packetIn.getEntityId());

        if (lvt_2_1_ instanceof EntityLivingBase)
        {
            PotionEffect lvt_3_1_ = new PotionEffect(packetIn.getEffectId(), packetIn.getDuration(), packetIn.getAmplifier(), false, packetIn.func_179707_f());
            lvt_3_1_.setPotionDurationMax(packetIn.func_149429_c());
            ((EntityLivingBase)lvt_2_1_).addPotionEffect(lvt_3_1_);
        }
    }

    public void handleCombatEvent(S42PacketCombatEvent packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity lvt_2_1_ = this.clientWorldController.getEntityByID(packetIn.field_179775_c);
        EntityLivingBase lvt_3_1_ = lvt_2_1_ instanceof EntityLivingBase ? (EntityLivingBase)lvt_2_1_ : null;

        if (packetIn.eventType == S42PacketCombatEvent.Event.END_COMBAT)
        {
            long lvt_4_1_ = (long)(1000 * packetIn.field_179772_d / 20);
            MetadataCombat lvt_6_1_ = new MetadataCombat(this.gameController.thePlayer, lvt_3_1_);
            this.gameController.getTwitchStream().func_176026_a(lvt_6_1_, 0L - lvt_4_1_, 0L);
        }
        else if (packetIn.eventType == S42PacketCombatEvent.Event.ENTITY_DIED)
        {
            Entity lvt_4_2_ = this.clientWorldController.getEntityByID(packetIn.field_179774_b);

            if (lvt_4_2_ instanceof EntityPlayer)
            {
                MetadataPlayerDeath lvt_5_1_ = new MetadataPlayerDeath((EntityPlayer)lvt_4_2_, lvt_3_1_);
                lvt_5_1_.func_152807_a(packetIn.deathMessage);
                this.gameController.getTwitchStream().func_152911_a(lvt_5_1_, 0L);
            }
        }
    }

    public void handleServerDifficulty(S41PacketServerDifficulty packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        this.gameController.theWorld.getWorldInfo().setDifficulty(packetIn.getDifficulty());
        this.gameController.theWorld.getWorldInfo().setDifficultyLocked(packetIn.isDifficultyLocked());
    }

    public void handleCamera(S43PacketCamera packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity lvt_2_1_ = packetIn.getEntity(this.clientWorldController);

        if (lvt_2_1_ != null)
        {
            this.gameController.setRenderViewEntity(lvt_2_1_);
        }
    }

    public void handleWorldBorder(S44PacketWorldBorder packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        packetIn.func_179788_a(this.clientWorldController.getWorldBorder());
    }

    @SuppressWarnings("incomplete-switch")
    public void handleTitle(S45PacketTitle packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        S45PacketTitle.Type lvt_2_1_ = packetIn.getType();
        String lvt_3_1_ = null;
        String lvt_4_1_ = null;
        String lvt_5_1_ = packetIn.getMessage() != null ? packetIn.getMessage().getFormattedText() : "";

        switch (lvt_2_1_)
        {
            case TITLE:
                lvt_3_1_ = lvt_5_1_;
                break;

            case SUBTITLE:
                lvt_4_1_ = lvt_5_1_;
                break;

            case RESET:
                this.gameController.ingameGUI.displayTitle("", "", -1, -1, -1);
                this.gameController.ingameGUI.setDefaultTitlesTimes();
                return;
        }

        this.gameController.ingameGUI.displayTitle(lvt_3_1_, lvt_4_1_, packetIn.getFadeInTime(), packetIn.getDisplayTime(), packetIn.getFadeOutTime());
    }

    public void handleSetCompressionLevel(S46PacketSetCompressionLevel packetIn)
    {
        if (!this.netManager.isLocalChannel())
        {
            this.netManager.setCompressionTreshold(packetIn.getThreshold());
        }
    }

    public void handlePlayerListHeaderFooter(S47PacketPlayerListHeaderFooter packetIn)
    {
        this.gameController.ingameGUI.getTabList().setHeader(packetIn.getHeader().getFormattedText().length() == 0 ? null : packetIn.getHeader());
        this.gameController.ingameGUI.getTabList().setFooter(packetIn.getFooter().getFormattedText().length() == 0 ? null : packetIn.getFooter());
    }

    public void handleRemoveEntityEffect(S1EPacketRemoveEntityEffect packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity lvt_2_1_ = this.clientWorldController.getEntityByID(packetIn.getEntityId());

        if (lvt_2_1_ instanceof EntityLivingBase)
        {
            ((EntityLivingBase)lvt_2_1_).removePotionEffectClient(packetIn.getEffectId());
        }
    }

    @SuppressWarnings("incomplete-switch")
    public void handlePlayerListItem(S38PacketPlayerListItem packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

        for (S38PacketPlayerListItem.AddPlayerData lvt_3_1_ : packetIn.getEntries())
        {
            if (packetIn.getAction() == S38PacketPlayerListItem.Action.REMOVE_PLAYER)
            {
                this.playerInfoMap.remove(lvt_3_1_.getProfile().getId());
            }
            else
            {
                NetworkPlayerInfo lvt_4_1_ = (NetworkPlayerInfo)this.playerInfoMap.get(lvt_3_1_.getProfile().getId());

                if (packetIn.getAction() == S38PacketPlayerListItem.Action.ADD_PLAYER)
                {
                    lvt_4_1_ = new NetworkPlayerInfo(lvt_3_1_);
                    this.playerInfoMap.put(lvt_4_1_.getGameProfile().getId(), lvt_4_1_);
                }

                if (lvt_4_1_ != null)
                {
                    switch (packetIn.getAction())
                    {
                        case ADD_PLAYER:
                            lvt_4_1_.setGameType(lvt_3_1_.getGameMode());
                            lvt_4_1_.setResponseTime(lvt_3_1_.getPing());
                            break;

                        case UPDATE_GAME_MODE:
                            lvt_4_1_.setGameType(lvt_3_1_.getGameMode());
                            break;

                        case UPDATE_LATENCY:
                            lvt_4_1_.setResponseTime(lvt_3_1_.getPing());
                            break;

                        case UPDATE_DISPLAY_NAME:
                            lvt_4_1_.setDisplayName(lvt_3_1_.getDisplayName());
                    }
                }
            }
        }
    }

    public void handleKeepAlive(S00PacketKeepAlive packetIn)
    {
        this.addToSendQueue(new C00PacketKeepAlive(packetIn.func_149134_c()));
    }

    public void handlePlayerAbilities(S39PacketPlayerAbilities packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        EntityPlayer lvt_2_1_ = this.gameController.thePlayer;
        lvt_2_1_.capabilities.isFlying = packetIn.isFlying();
        lvt_2_1_.capabilities.isCreativeMode = packetIn.isCreativeMode();
        lvt_2_1_.capabilities.disableDamage = packetIn.isInvulnerable();
        lvt_2_1_.capabilities.allowFlying = packetIn.isAllowFlying();
        lvt_2_1_.capabilities.setFlySpeed(packetIn.getFlySpeed());
        lvt_2_1_.capabilities.setPlayerWalkSpeed(packetIn.getWalkSpeed());
    }

    /**
     * Displays the available command-completion options the server knows of
     */
    public void handleTabComplete(S3APacketTabComplete packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        String[] lvt_2_1_ = packetIn.func_149630_c();

        if (this.gameController.currentScreen instanceof GuiChat)
        {
            GuiChat lvt_3_1_ = (GuiChat)this.gameController.currentScreen;
            lvt_3_1_.onAutocompleteResponse(lvt_2_1_);
        }
    }

    public void handleSoundEffect(S29PacketSoundEffect packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        this.gameController.theWorld.playSound(packetIn.getX(), packetIn.getY(), packetIn.getZ(), packetIn.getSoundName(), packetIn.getVolume(), packetIn.getPitch(), false);
    }

    public void handleResourcePack(S48PacketResourcePackSend packetIn)
    {
        final String lvt_2_1_ = packetIn.getURL();
        final String lvt_3_1_ = packetIn.getHash();

        if (lvt_2_1_.startsWith("level://"))
        {
            String lvt_4_1_ = lvt_2_1_.substring("level://".length());
            File lvt_5_1_ = new File(this.gameController.mcDataDir, "saves");
            File lvt_6_1_ = new File(lvt_5_1_, lvt_4_1_);

            if (lvt_6_1_.isFile())
            {
                this.netManager.sendPacket(new C19PacketResourcePackStatus(lvt_3_1_, C19PacketResourcePackStatus.Action.ACCEPTED));
                Futures.addCallback(this.gameController.getResourcePackRepository().setResourcePackInstance(lvt_6_1_), new FutureCallback<Object>()
                {
                    public void onSuccess(Object p_onSuccess_1_)
                    {
                        NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(lvt_3_1_, C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
                    }
                    public void onFailure(Throwable p_onFailure_1_)
                    {
                        NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(lvt_3_1_, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                    }
                });
            }
            else
            {
                this.netManager.sendPacket(new C19PacketResourcePackStatus(lvt_3_1_, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
            }
        }
        else
        {
            if (this.gameController.getCurrentServerData() != null && this.gameController.getCurrentServerData().getResourceMode() == ServerData.ServerResourceMode.ENABLED)
            {
                this.netManager.sendPacket(new C19PacketResourcePackStatus(lvt_3_1_, C19PacketResourcePackStatus.Action.ACCEPTED));
                Futures.addCallback(this.gameController.getResourcePackRepository().downloadResourcePack(lvt_2_1_, lvt_3_1_), new FutureCallback<Object>()
                {
                    public void onSuccess(Object p_onSuccess_1_)
                    {
                        NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(lvt_3_1_, C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
                    }
                    public void onFailure(Throwable p_onFailure_1_)
                    {
                        NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(lvt_3_1_, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                    }
                });
            }
            else if (this.gameController.getCurrentServerData() != null && this.gameController.getCurrentServerData().getResourceMode() != ServerData.ServerResourceMode.PROMPT)
            {
                this.netManager.sendPacket(new C19PacketResourcePackStatus(lvt_3_1_, C19PacketResourcePackStatus.Action.DECLINED));
            }
            else
            {
                this.gameController.addScheduledTask(new Runnable()
                {
                    public void run()
                    {
                        NetHandlerPlayClient.this.gameController.displayGuiScreen(new GuiYesNo(new GuiYesNoCallback()
                        {
                            public void confirmClicked(boolean result, int id)
                            {
                                NetHandlerPlayClient.this.gameController = Minecraft.getMinecraft();

                                if (result)
                                {
                                    if (NetHandlerPlayClient.this.gameController.getCurrentServerData() != null)
                                    {
                                        NetHandlerPlayClient.this.gameController.getCurrentServerData().setResourceMode(ServerData.ServerResourceMode.ENABLED);
                                    }

                                    NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(lvt_3_1_, C19PacketResourcePackStatus.Action.ACCEPTED));
                                    Futures.addCallback(NetHandlerPlayClient.this.gameController.getResourcePackRepository().downloadResourcePack(lvt_2_1_, lvt_3_1_), new FutureCallback<Object>()
                                    {
                                        public void onSuccess(Object p_onSuccess_1_)
                                        {
                                            NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(lvt_3_1_, C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
                                        }
                                        public void onFailure(Throwable p_onFailure_1_)
                                        {
                                            NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(lvt_3_1_, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                                        }
                                    });
                                }
                                else
                                {
                                    if (NetHandlerPlayClient.this.gameController.getCurrentServerData() != null)
                                    {
                                        NetHandlerPlayClient.this.gameController.getCurrentServerData().setResourceMode(ServerData.ServerResourceMode.DISABLED);
                                    }

                                    NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(lvt_3_1_, C19PacketResourcePackStatus.Action.DECLINED));
                                }

                                ServerList.func_147414_b(NetHandlerPlayClient.this.gameController.getCurrentServerData());
                                NetHandlerPlayClient.this.gameController.displayGuiScreen((GuiScreen)null);
                            }
                        }, I18n.format("multiplayer.texturePrompt.line1", new Object[0]), I18n.format("multiplayer.texturePrompt.line2", new Object[0]), 0));
                    }
                });
            }
        }
    }

    public void handleEntityNBT(S49PacketUpdateEntityNBT packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity lvt_2_1_ = packetIn.getEntity(this.clientWorldController);

        if (lvt_2_1_ != null)
        {
            lvt_2_1_.clientUpdateEntityNBT(packetIn.getTagCompound());
        }
    }

    /**
     * Handles packets that have room for a channel specification. Vanilla implemented channels are "MC|TrList" to
     * acquire a MerchantRecipeList trades for a villager merchant, "MC|Brand" which sets the server brand? on the
     * player instance and finally "MC|RPack" which the server uses to communicate the identifier of the default server
     * resourcepack for the client to load.
     */
    public void handleCustomPayload(S3FPacketCustomPayload packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

        if ("MC|TrList".equals(packetIn.getChannelName()))
        {
            PacketBuffer lvt_2_1_ = packetIn.getBufferData();

            try
            {
                int lvt_3_1_ = lvt_2_1_.readInt();
                GuiScreen lvt_4_1_ = this.gameController.currentScreen;

                if (lvt_4_1_ != null && lvt_4_1_ instanceof GuiMerchant && lvt_3_1_ == this.gameController.thePlayer.openContainer.windowId)
                {
                    IMerchant lvt_5_1_ = ((GuiMerchant)lvt_4_1_).getMerchant();
                    MerchantRecipeList lvt_6_1_ = MerchantRecipeList.readFromBuf(lvt_2_1_);
                    lvt_5_1_.setRecipes(lvt_6_1_);
                }
            }
            catch (IOException var10)
            {
                logger.error("Couldn\'t load trade info", var10);
            }
            finally
            {
                lvt_2_1_.release();
            }
        }
        else if ("MC|Brand".equals(packetIn.getChannelName()))
        {
            this.gameController.thePlayer.setClientBrand(packetIn.getBufferData().readStringFromBuffer(32767));
        }
        else if ("MC|BOpen".equals(packetIn.getChannelName()))
        {
            ItemStack lvt_2_2_ = this.gameController.thePlayer.getCurrentEquippedItem();

            if (lvt_2_2_ != null && lvt_2_2_.getItem() == Items.written_book)
            {
                this.gameController.displayGuiScreen(new GuiScreenBook(this.gameController.thePlayer, lvt_2_2_, false));
            }
        }
    }

    /**
     * May create a scoreboard objective, remove an objective from the scoreboard or update an objectives' displayname
     */
    public void handleScoreboardObjective(S3BPacketScoreboardObjective packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Scoreboard lvt_2_1_ = this.clientWorldController.getScoreboard();

        if (packetIn.func_149338_e() == 0)
        {
            ScoreObjective lvt_3_1_ = lvt_2_1_.addScoreObjective(packetIn.func_149339_c(), IScoreObjectiveCriteria.DUMMY);
            lvt_3_1_.setDisplayName(packetIn.func_149337_d());
            lvt_3_1_.setRenderType(packetIn.func_179817_d());
        }
        else
        {
            ScoreObjective lvt_3_2_ = lvt_2_1_.getObjective(packetIn.func_149339_c());

            if (packetIn.func_149338_e() == 1)
            {
                lvt_2_1_.removeObjective(lvt_3_2_);
            }
            else if (packetIn.func_149338_e() == 2)
            {
                lvt_3_2_.setDisplayName(packetIn.func_149337_d());
                lvt_3_2_.setRenderType(packetIn.func_179817_d());
            }
        }
    }

    /**
     * Either updates the score with a specified value or removes the score for an objective
     */
    public void handleUpdateScore(S3CPacketUpdateScore packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Scoreboard lvt_2_1_ = this.clientWorldController.getScoreboard();
        ScoreObjective lvt_3_1_ = lvt_2_1_.getObjective(packetIn.getObjectiveName());

        if (packetIn.getScoreAction() == S3CPacketUpdateScore.Action.CHANGE)
        {
            Score lvt_4_1_ = lvt_2_1_.getValueFromObjective(packetIn.getPlayerName(), lvt_3_1_);
            lvt_4_1_.setScorePoints(packetIn.getScoreValue());
        }
        else if (packetIn.getScoreAction() == S3CPacketUpdateScore.Action.REMOVE)
        {
            if (StringUtils.isNullOrEmpty(packetIn.getObjectiveName()))
            {
                lvt_2_1_.removeObjectiveFromEntity(packetIn.getPlayerName(), (ScoreObjective)null);
            }
            else if (lvt_3_1_ != null)
            {
                lvt_2_1_.removeObjectiveFromEntity(packetIn.getPlayerName(), lvt_3_1_);
            }
        }
    }

    /**
     * Removes or sets the ScoreObjective to be displayed at a particular scoreboard position (list, sidebar, below
     * name)
     */
    public void handleDisplayScoreboard(S3DPacketDisplayScoreboard packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Scoreboard lvt_2_1_ = this.clientWorldController.getScoreboard();

        if (packetIn.func_149370_d().length() == 0)
        {
            lvt_2_1_.setObjectiveInDisplaySlot(packetIn.func_149371_c(), (ScoreObjective)null);
        }
        else
        {
            ScoreObjective lvt_3_1_ = lvt_2_1_.getObjective(packetIn.func_149370_d());
            lvt_2_1_.setObjectiveInDisplaySlot(packetIn.func_149371_c(), lvt_3_1_);
        }
    }

    /**
     * Updates a team managed by the scoreboard: Create/Remove the team registration, Register/Remove the player-team-
     * memberships, Set team displayname/prefix/suffix and/or whether friendly fire is enabled
     */
    public void handleTeams(S3EPacketTeams packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Scoreboard lvt_2_1_ = this.clientWorldController.getScoreboard();
        ScorePlayerTeam lvt_3_1_;

        if (packetIn.getAction() == 0)
        {
            lvt_3_1_ = lvt_2_1_.createTeam(packetIn.getName());
        }
        else
        {
            lvt_3_1_ = lvt_2_1_.getTeam(packetIn.getName());
        }

        if (packetIn.getAction() == 0 || packetIn.getAction() == 2)
        {
            lvt_3_1_.setTeamName(packetIn.getDisplayName());
            lvt_3_1_.setNamePrefix(packetIn.getPrefix());
            lvt_3_1_.setNameSuffix(packetIn.getSuffix());
            lvt_3_1_.setChatFormat(EnumChatFormatting.func_175744_a(packetIn.getColor()));
            lvt_3_1_.func_98298_a(packetIn.getFriendlyFlags());
            Team.EnumVisible lvt_4_1_ = Team.EnumVisible.func_178824_a(packetIn.getNameTagVisibility());

            if (lvt_4_1_ != null)
            {
                lvt_3_1_.setNameTagVisibility(lvt_4_1_);
            }
        }

        if (packetIn.getAction() == 0 || packetIn.getAction() == 3)
        {
            for (String lvt_5_1_ : packetIn.getPlayers())
            {
                lvt_2_1_.addPlayerToTeam(lvt_5_1_, packetIn.getName());
            }
        }

        if (packetIn.getAction() == 4)
        {
            for (String lvt_5_2_ : packetIn.getPlayers())
            {
                lvt_2_1_.removePlayerFromTeam(lvt_5_2_, lvt_3_1_);
            }
        }

        if (packetIn.getAction() == 1)
        {
            lvt_2_1_.removeTeam(lvt_3_1_);
        }
    }

    /**
     * Spawns a specified number of particles at the specified location with a randomized displacement according to
     * specified bounds
     */
    public void handleParticles(S2APacketParticles packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

        if (packetIn.getParticleCount() == 0)
        {
            double lvt_2_1_ = (double)(packetIn.getParticleSpeed() * packetIn.getXOffset());
            double lvt_4_1_ = (double)(packetIn.getParticleSpeed() * packetIn.getYOffset());
            double lvt_6_1_ = (double)(packetIn.getParticleSpeed() * packetIn.getZOffset());

            try
            {
                this.clientWorldController.spawnParticle(packetIn.getParticleType(), packetIn.isLongDistance(), packetIn.getXCoordinate(), packetIn.getYCoordinate(), packetIn.getZCoordinate(), lvt_2_1_, lvt_4_1_, lvt_6_1_, packetIn.getParticleArgs());
            }
            catch (Throwable var17)
            {
                logger.warn("Could not spawn particle effect " + packetIn.getParticleType());
            }
        }
        else
        {
            for (int lvt_2_2_ = 0; lvt_2_2_ < packetIn.getParticleCount(); ++lvt_2_2_)
            {
                double lvt_3_1_ = this.avRandomizer.nextGaussian() * (double)packetIn.getXOffset();
                double lvt_5_1_ = this.avRandomizer.nextGaussian() * (double)packetIn.getYOffset();
                double lvt_7_1_ = this.avRandomizer.nextGaussian() * (double)packetIn.getZOffset();
                double lvt_9_1_ = this.avRandomizer.nextGaussian() * (double)packetIn.getParticleSpeed();
                double lvt_11_1_ = this.avRandomizer.nextGaussian() * (double)packetIn.getParticleSpeed();
                double lvt_13_1_ = this.avRandomizer.nextGaussian() * (double)packetIn.getParticleSpeed();

                try
                {
                    this.clientWorldController.spawnParticle(packetIn.getParticleType(), packetIn.isLongDistance(), packetIn.getXCoordinate() + lvt_3_1_, packetIn.getYCoordinate() + lvt_5_1_, packetIn.getZCoordinate() + lvt_7_1_, lvt_9_1_, lvt_11_1_, lvt_13_1_, packetIn.getParticleArgs());
                }
                catch (Throwable var16)
                {
                    logger.warn("Could not spawn particle effect " + packetIn.getParticleType());
                    return;
                }
            }
        }
    }

    /**
     * Updates en entity's attributes and their respective modifiers, which are used for speed bonusses (player
     * sprinting, animals fleeing, baby speed), weapon/tool attackDamage, hostiles followRange randomization, zombie
     * maxHealth and knockback resistance as well as reinforcement spawning chance.
     */
    public void handleEntityProperties(S20PacketEntityProperties packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity lvt_2_1_ = this.clientWorldController.getEntityByID(packetIn.getEntityId());

        if (lvt_2_1_ != null)
        {
            if (!(lvt_2_1_ instanceof EntityLivingBase))
            {
                throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + lvt_2_1_ + ")");
            }
            else
            {
                BaseAttributeMap lvt_3_1_ = ((EntityLivingBase)lvt_2_1_).getAttributeMap();

                for (S20PacketEntityProperties.Snapshot lvt_5_1_ : packetIn.func_149441_d())
                {
                    IAttributeInstance lvt_6_1_ = lvt_3_1_.getAttributeInstanceByName(lvt_5_1_.func_151409_a());

                    if (lvt_6_1_ == null)
                    {
                        lvt_6_1_ = lvt_3_1_.registerAttribute(new RangedAttribute((IAttribute)null, lvt_5_1_.func_151409_a(), 0.0D, 2.2250738585072014E-308D, Double.MAX_VALUE));
                    }

                    lvt_6_1_.setBaseValue(lvt_5_1_.func_151410_b());
                    lvt_6_1_.removeAllModifiers();

                    for (AttributeModifier lvt_8_1_ : lvt_5_1_.func_151408_c())
                    {
                        lvt_6_1_.applyModifier(lvt_8_1_);
                    }
                }
            }
        }
    }

    /**
     * Returns this the NetworkManager instance registered with this NetworkHandlerPlayClient
     */
    public NetworkManager getNetworkManager()
    {
        return this.netManager;
    }

    public Collection<NetworkPlayerInfo> getPlayerInfoMap()
    {
        return this.playerInfoMap.values();
    }

    public NetworkPlayerInfo getPlayerInfo(UUID p_175102_1_)
    {
        return (NetworkPlayerInfo)this.playerInfoMap.get(p_175102_1_);
    }

    /**
     * Gets the client's description information about another player on the server.
     */
    public NetworkPlayerInfo getPlayerInfo(String p_175104_1_)
    {
        for (NetworkPlayerInfo lvt_3_1_ : this.playerInfoMap.values())
        {
            if (lvt_3_1_.getGameProfile().getName().equals(p_175104_1_))
            {
                return lvt_3_1_;
            }
        }

        return null;
    }

    public GameProfile getGameProfile()
    {
        return this.profile;
    }
}
