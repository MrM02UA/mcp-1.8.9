package net.minecraft.network;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.util.concurrent.Futures;
import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.block.material.Material;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListBansEntry;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldServer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerPlayServer implements INetHandlerPlayServer, ITickable
{
    private static final Logger logger = LogManager.getLogger();
    public final NetworkManager netManager;
    private final MinecraftServer serverController;
    public EntityPlayerMP playerEntity;
    private int networkTickCount;
    private int field_175090_f;

    /**
     * Used to keep track of how the player is floating while gamerules should prevent that. Surpassing 80 ticks means
     * kick
     */
    private int floatingTickCount;
    private boolean field_147366_g;
    private int field_147378_h;
    private long lastPingTime;
    private long lastSentPingPacket;

    /**
     * Incremented by 20 each time a user sends a chat message, decreased by one every tick. Non-ops kicked when over
     * 200
     */
    private int chatSpamThresholdCount;
    private int itemDropThreshold;
    private IntHashMap<Short> field_147372_n = new IntHashMap();
    private double lastPosX;
    private double lastPosY;
    private double lastPosZ;
    private boolean hasMoved = true;

    public NetHandlerPlayServer(MinecraftServer server, NetworkManager networkManagerIn, EntityPlayerMP playerIn)
    {
        this.serverController = server;
        this.netManager = networkManagerIn;
        networkManagerIn.setNetHandler(this);
        this.playerEntity = playerIn;
        playerIn.playerNetServerHandler = this;
    }

    /**
     * Like the old updateEntity(), except more generic.
     */
    public void update()
    {
        this.field_147366_g = false;
        ++this.networkTickCount;
        this.serverController.theProfiler.startSection("keepAlive");

        if ((long)this.networkTickCount - this.lastSentPingPacket > 40L)
        {
            this.lastSentPingPacket = (long)this.networkTickCount;
            this.lastPingTime = this.currentTimeMillis();
            this.field_147378_h = (int)this.lastPingTime;
            this.sendPacket(new S00PacketKeepAlive(this.field_147378_h));
        }

        this.serverController.theProfiler.endSection();

        if (this.chatSpamThresholdCount > 0)
        {
            --this.chatSpamThresholdCount;
        }

        if (this.itemDropThreshold > 0)
        {
            --this.itemDropThreshold;
        }

        if (this.playerEntity.getLastActiveTime() > 0L && this.serverController.getMaxPlayerIdleMinutes() > 0 && MinecraftServer.getCurrentTimeMillis() - this.playerEntity.getLastActiveTime() > (long)(this.serverController.getMaxPlayerIdleMinutes() * 1000 * 60))
        {
            this.kickPlayerFromServer("You have been idle for too long!");
        }
    }

    public NetworkManager getNetworkManager()
    {
        return this.netManager;
    }

    /**
     * Kick a player from the server with a reason
     */
    public void kickPlayerFromServer(String reason)
    {
        final ChatComponentText lvt_2_1_ = new ChatComponentText(reason);
        this.netManager.sendPacket(new S40PacketDisconnect(lvt_2_1_), new GenericFutureListener < Future <? super Void >> ()
        {
            public void operationComplete(Future <? super Void > p_operationComplete_1_) throws Exception
            {
                NetHandlerPlayServer.this.netManager.closeChannel(lvt_2_1_);
            }
        }, new GenericFutureListener[0]);
        this.netManager.disableAutoRead();
        Futures.getUnchecked(this.serverController.addScheduledTask(new Runnable()
        {
            public void run()
            {
                NetHandlerPlayServer.this.netManager.checkDisconnected();
            }
        }));
    }

    /**
     * Processes player movement input. Includes walking, strafing, jumping, sneaking; excludes riding and toggling
     * flying/sprinting
     */
    public void processInput(C0CPacketInput packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        this.playerEntity.setEntityActionState(packetIn.getStrafeSpeed(), packetIn.getForwardSpeed(), packetIn.isJumping(), packetIn.isSneaking());
    }

    private boolean func_183006_b(C03PacketPlayer p_183006_1_)
    {
        return !Doubles.isFinite(p_183006_1_.getPositionX()) || !Doubles.isFinite(p_183006_1_.getPositionY()) || !Doubles.isFinite(p_183006_1_.getPositionZ()) || !Floats.isFinite(p_183006_1_.getPitch()) || !Floats.isFinite(p_183006_1_.getYaw());
    }

    /**
     * Processes clients perspective on player positioning and/or orientation
     */
    public void processPlayer(C03PacketPlayer packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());

        if (this.func_183006_b(packetIn))
        {
            this.kickPlayerFromServer("Invalid move packet received");
        }
        else
        {
            WorldServer lvt_2_1_ = this.serverController.worldServerForDimension(this.playerEntity.dimension);
            this.field_147366_g = true;

            if (!this.playerEntity.playerConqueredTheEnd)
            {
                double lvt_3_1_ = this.playerEntity.posX;
                double lvt_5_1_ = this.playerEntity.posY;
                double lvt_7_1_ = this.playerEntity.posZ;
                double lvt_9_1_ = 0.0D;
                double lvt_11_1_ = packetIn.getPositionX() - this.lastPosX;
                double lvt_13_1_ = packetIn.getPositionY() - this.lastPosY;
                double lvt_15_1_ = packetIn.getPositionZ() - this.lastPosZ;

                if (packetIn.isMoving())
                {
                    lvt_9_1_ = lvt_11_1_ * lvt_11_1_ + lvt_13_1_ * lvt_13_1_ + lvt_15_1_ * lvt_15_1_;

                    if (!this.hasMoved && lvt_9_1_ < 0.25D)
                    {
                        this.hasMoved = true;
                    }
                }

                if (this.hasMoved)
                {
                    this.field_175090_f = this.networkTickCount;

                    if (this.playerEntity.ridingEntity != null)
                    {
                        float lvt_17_1_ = this.playerEntity.rotationYaw;
                        float lvt_18_1_ = this.playerEntity.rotationPitch;
                        this.playerEntity.ridingEntity.updateRiderPosition();
                        double lvt_19_1_ = this.playerEntity.posX;
                        double lvt_21_1_ = this.playerEntity.posY;
                        double lvt_23_1_ = this.playerEntity.posZ;

                        if (packetIn.getRotating())
                        {
                            lvt_17_1_ = packetIn.getYaw();
                            lvt_18_1_ = packetIn.getPitch();
                        }

                        this.playerEntity.onGround = packetIn.isOnGround();
                        this.playerEntity.onUpdateEntity();
                        this.playerEntity.setPositionAndRotation(lvt_19_1_, lvt_21_1_, lvt_23_1_, lvt_17_1_, lvt_18_1_);

                        if (this.playerEntity.ridingEntity != null)
                        {
                            this.playerEntity.ridingEntity.updateRiderPosition();
                        }

                        this.serverController.getConfigurationManager().serverUpdateMountedMovingPlayer(this.playerEntity);

                        if (this.playerEntity.ridingEntity != null)
                        {
                            if (lvt_9_1_ > 4.0D)
                            {
                                Entity lvt_25_1_ = this.playerEntity.ridingEntity;
                                this.playerEntity.playerNetServerHandler.sendPacket(new S18PacketEntityTeleport(lvt_25_1_));
                                this.setPlayerLocation(this.playerEntity.posX, this.playerEntity.posY, this.playerEntity.posZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
                            }

                            this.playerEntity.ridingEntity.isAirBorne = true;
                        }

                        if (this.hasMoved)
                        {
                            this.lastPosX = this.playerEntity.posX;
                            this.lastPosY = this.playerEntity.posY;
                            this.lastPosZ = this.playerEntity.posZ;
                        }

                        lvt_2_1_.updateEntity(this.playerEntity);
                        return;
                    }

                    if (this.playerEntity.isPlayerSleeping())
                    {
                        this.playerEntity.onUpdateEntity();
                        this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
                        lvt_2_1_.updateEntity(this.playerEntity);
                        return;
                    }

                    double lvt_17_2_ = this.playerEntity.posY;
                    this.lastPosX = this.playerEntity.posX;
                    this.lastPosY = this.playerEntity.posY;
                    this.lastPosZ = this.playerEntity.posZ;
                    double lvt_19_2_ = this.playerEntity.posX;
                    double lvt_21_2_ = this.playerEntity.posY;
                    double lvt_23_2_ = this.playerEntity.posZ;
                    float lvt_25_2_ = this.playerEntity.rotationYaw;
                    float lvt_26_1_ = this.playerEntity.rotationPitch;

                    if (packetIn.isMoving() && packetIn.getPositionY() == -999.0D)
                    {
                        packetIn.setMoving(false);
                    }

                    if (packetIn.isMoving())
                    {
                        lvt_19_2_ = packetIn.getPositionX();
                        lvt_21_2_ = packetIn.getPositionY();
                        lvt_23_2_ = packetIn.getPositionZ();

                        if (Math.abs(packetIn.getPositionX()) > 3.0E7D || Math.abs(packetIn.getPositionZ()) > 3.0E7D)
                        {
                            this.kickPlayerFromServer("Illegal position");
                            return;
                        }
                    }

                    if (packetIn.getRotating())
                    {
                        lvt_25_2_ = packetIn.getYaw();
                        lvt_26_1_ = packetIn.getPitch();
                    }

                    this.playerEntity.onUpdateEntity();
                    this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, lvt_25_2_, lvt_26_1_);

                    if (!this.hasMoved)
                    {
                        return;
                    }

                    double lvt_27_1_ = lvt_19_2_ - this.playerEntity.posX;
                    double lvt_29_1_ = lvt_21_2_ - this.playerEntity.posY;
                    double lvt_31_1_ = lvt_23_2_ - this.playerEntity.posZ;
                    double lvt_33_1_ = this.playerEntity.motionX * this.playerEntity.motionX + this.playerEntity.motionY * this.playerEntity.motionY + this.playerEntity.motionZ * this.playerEntity.motionZ;
                    double lvt_35_1_ = lvt_27_1_ * lvt_27_1_ + lvt_29_1_ * lvt_29_1_ + lvt_31_1_ * lvt_31_1_;

                    if (lvt_35_1_ - lvt_33_1_ > 100.0D && (!this.serverController.isSinglePlayer() || !this.serverController.getServerOwner().equals(this.playerEntity.getName())))
                    {
                        logger.warn(this.playerEntity.getName() + " moved too quickly! " + lvt_27_1_ + "," + lvt_29_1_ + "," + lvt_31_1_ + " (" + lvt_27_1_ + ", " + lvt_29_1_ + ", " + lvt_31_1_ + ")");
                        this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
                        return;
                    }

                    float lvt_37_1_ = 0.0625F;
                    boolean lvt_38_1_ = lvt_2_1_.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.getEntityBoundingBox().contract((double)lvt_37_1_, (double)lvt_37_1_, (double)lvt_37_1_)).isEmpty();

                    if (this.playerEntity.onGround && !packetIn.isOnGround() && lvt_29_1_ > 0.0D)
                    {
                        this.playerEntity.jump();
                    }

                    this.playerEntity.moveEntity(lvt_27_1_, lvt_29_1_, lvt_31_1_);
                    this.playerEntity.onGround = packetIn.isOnGround();
                    lvt_27_1_ = lvt_19_2_ - this.playerEntity.posX;
                    lvt_29_1_ = lvt_21_2_ - this.playerEntity.posY;

                    if (lvt_29_1_ > -0.5D || lvt_29_1_ < 0.5D)
                    {
                        lvt_29_1_ = 0.0D;
                    }

                    lvt_31_1_ = lvt_23_2_ - this.playerEntity.posZ;
                    lvt_35_1_ = lvt_27_1_ * lvt_27_1_ + lvt_29_1_ * lvt_29_1_ + lvt_31_1_ * lvt_31_1_;
                    boolean lvt_41_1_ = false;

                    if (lvt_35_1_ > 0.0625D && !this.playerEntity.isPlayerSleeping() && !this.playerEntity.theItemInWorldManager.isCreative())
                    {
                        lvt_41_1_ = true;
                        logger.warn(this.playerEntity.getName() + " moved wrongly!");
                    }

                    this.playerEntity.setPositionAndRotation(lvt_19_2_, lvt_21_2_, lvt_23_2_, lvt_25_2_, lvt_26_1_);
                    this.playerEntity.addMovementStat(this.playerEntity.posX - lvt_3_1_, this.playerEntity.posY - lvt_5_1_, this.playerEntity.posZ - lvt_7_1_);

                    if (!this.playerEntity.noClip)
                    {
                        boolean lvt_42_1_ = lvt_2_1_.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.getEntityBoundingBox().contract((double)lvt_37_1_, (double)lvt_37_1_, (double)lvt_37_1_)).isEmpty();

                        if (lvt_38_1_ && (lvt_41_1_ || !lvt_42_1_) && !this.playerEntity.isPlayerSleeping())
                        {
                            this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, lvt_25_2_, lvt_26_1_);
                            return;
                        }
                    }

                    AxisAlignedBB lvt_42_2_ = this.playerEntity.getEntityBoundingBox().expand((double)lvt_37_1_, (double)lvt_37_1_, (double)lvt_37_1_).addCoord(0.0D, -0.55D, 0.0D);

                    if (!this.serverController.isFlightAllowed() && !this.playerEntity.capabilities.allowFlying && !lvt_2_1_.checkBlockCollision(lvt_42_2_))
                    {
                        if (lvt_29_1_ >= -0.03125D)
                        {
                            ++this.floatingTickCount;

                            if (this.floatingTickCount > 80)
                            {
                                logger.warn(this.playerEntity.getName() + " was kicked for floating too long!");
                                this.kickPlayerFromServer("Flying is not enabled on this server");
                                return;
                            }
                        }
                    }
                    else
                    {
                        this.floatingTickCount = 0;
                    }

                    this.playerEntity.onGround = packetIn.isOnGround();
                    this.serverController.getConfigurationManager().serverUpdateMountedMovingPlayer(this.playerEntity);
                    this.playerEntity.handleFalling(this.playerEntity.posY - lvt_17_2_, packetIn.isOnGround());
                }
                else if (this.networkTickCount - this.field_175090_f > 20)
                {
                    this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
                }
            }
        }
    }

    public void setPlayerLocation(double x, double y, double z, float yaw, float pitch)
    {
        this.setPlayerLocation(x, y, z, yaw, pitch, Collections.emptySet());
    }

    public void setPlayerLocation(double x, double y, double z, float yaw, float pitch, Set<S08PacketPlayerPosLook.EnumFlags> relativeSet)
    {
        this.hasMoved = false;
        this.lastPosX = x;
        this.lastPosY = y;
        this.lastPosZ = z;

        if (relativeSet.contains(S08PacketPlayerPosLook.EnumFlags.X))
        {
            this.lastPosX += this.playerEntity.posX;
        }

        if (relativeSet.contains(S08PacketPlayerPosLook.EnumFlags.Y))
        {
            this.lastPosY += this.playerEntity.posY;
        }

        if (relativeSet.contains(S08PacketPlayerPosLook.EnumFlags.Z))
        {
            this.lastPosZ += this.playerEntity.posZ;
        }

        float lvt_10_1_ = yaw;
        float lvt_11_1_ = pitch;

        if (relativeSet.contains(S08PacketPlayerPosLook.EnumFlags.Y_ROT))
        {
            lvt_10_1_ = yaw + this.playerEntity.rotationYaw;
        }

        if (relativeSet.contains(S08PacketPlayerPosLook.EnumFlags.X_ROT))
        {
            lvt_11_1_ = pitch + this.playerEntity.rotationPitch;
        }

        this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, lvt_10_1_, lvt_11_1_);
        this.playerEntity.playerNetServerHandler.sendPacket(new S08PacketPlayerPosLook(x, y, z, yaw, pitch, relativeSet));
    }

    /**
     * Processes the player initiating/stopping digging on a particular spot, as well as a player dropping items?. (0:
     * initiated, 1: reinitiated, 2? , 3-4 drop item (respectively without or with player control), 5: stopped; x,y,z,
     * side clicked on;)
     */
    public void processPlayerDigging(C07PacketPlayerDigging packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        WorldServer lvt_2_1_ = this.serverController.worldServerForDimension(this.playerEntity.dimension);
        BlockPos lvt_3_1_ = packetIn.getPosition();
        this.playerEntity.markPlayerActive();

        switch (packetIn.getStatus())
        {
            case DROP_ITEM:
                if (!this.playerEntity.isSpectator())
                {
                    this.playerEntity.dropOneItem(false);
                }

                return;

            case DROP_ALL_ITEMS:
                if (!this.playerEntity.isSpectator())
                {
                    this.playerEntity.dropOneItem(true);
                }

                return;

            case RELEASE_USE_ITEM:
                this.playerEntity.stopUsingItem();
                return;

            case START_DESTROY_BLOCK:
            case ABORT_DESTROY_BLOCK:
            case STOP_DESTROY_BLOCK:
                double lvt_4_1_ = this.playerEntity.posX - ((double)lvt_3_1_.getX() + 0.5D);
                double lvt_6_1_ = this.playerEntity.posY - ((double)lvt_3_1_.getY() + 0.5D) + 1.5D;
                double lvt_8_1_ = this.playerEntity.posZ - ((double)lvt_3_1_.getZ() + 0.5D);
                double lvt_10_1_ = lvt_4_1_ * lvt_4_1_ + lvt_6_1_ * lvt_6_1_ + lvt_8_1_ * lvt_8_1_;

                if (lvt_10_1_ > 36.0D)
                {
                    return;
                }
                else if (lvt_3_1_.getY() >= this.serverController.getBuildLimit())
                {
                    return;
                }
                else
                {
                    if (packetIn.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK)
                    {
                        if (!this.serverController.isBlockProtected(lvt_2_1_, lvt_3_1_, this.playerEntity) && lvt_2_1_.getWorldBorder().contains(lvt_3_1_))
                        {
                            this.playerEntity.theItemInWorldManager.onBlockClicked(lvt_3_1_, packetIn.getFacing());
                        }
                        else
                        {
                            this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(lvt_2_1_, lvt_3_1_));
                        }
                    }
                    else
                    {
                        if (packetIn.getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK)
                        {
                            this.playerEntity.theItemInWorldManager.blockRemoving(lvt_3_1_);
                        }
                        else if (packetIn.getStatus() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK)
                        {
                            this.playerEntity.theItemInWorldManager.cancelDestroyingBlock();
                        }

                        if (lvt_2_1_.getBlockState(lvt_3_1_).getBlock().getMaterial() != Material.air)
                        {
                            this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(lvt_2_1_, lvt_3_1_));
                        }
                    }

                    return;
                }

            default:
                throw new IllegalArgumentException("Invalid player action");
        }
    }

    /**
     * Processes block placement and block activation (anvil, furnace, etc.)
     */
    public void processPlayerBlockPlacement(C08PacketPlayerBlockPlacement packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        WorldServer lvt_2_1_ = this.serverController.worldServerForDimension(this.playerEntity.dimension);
        ItemStack lvt_3_1_ = this.playerEntity.inventory.getCurrentItem();
        boolean lvt_4_1_ = false;
        BlockPos lvt_5_1_ = packetIn.getPosition();
        EnumFacing lvt_6_1_ = EnumFacing.getFront(packetIn.getPlacedBlockDirection());
        this.playerEntity.markPlayerActive();

        if (packetIn.getPlacedBlockDirection() == 255)
        {
            if (lvt_3_1_ == null)
            {
                return;
            }

            this.playerEntity.theItemInWorldManager.tryUseItem(this.playerEntity, lvt_2_1_, lvt_3_1_);
        }
        else if (lvt_5_1_.getY() < this.serverController.getBuildLimit() - 1 || lvt_6_1_ != EnumFacing.UP && lvt_5_1_.getY() < this.serverController.getBuildLimit())
        {
            if (this.hasMoved && this.playerEntity.getDistanceSq((double)lvt_5_1_.getX() + 0.5D, (double)lvt_5_1_.getY() + 0.5D, (double)lvt_5_1_.getZ() + 0.5D) < 64.0D && !this.serverController.isBlockProtected(lvt_2_1_, lvt_5_1_, this.playerEntity) && lvt_2_1_.getWorldBorder().contains(lvt_5_1_))
            {
                this.playerEntity.theItemInWorldManager.activateBlockOrUseItem(this.playerEntity, lvt_2_1_, lvt_3_1_, lvt_5_1_, lvt_6_1_, packetIn.getPlacedBlockOffsetX(), packetIn.getPlacedBlockOffsetY(), packetIn.getPlacedBlockOffsetZ());
            }

            lvt_4_1_ = true;
        }
        else
        {
            ChatComponentTranslation lvt_7_1_ = new ChatComponentTranslation("build.tooHigh", new Object[] {Integer.valueOf(this.serverController.getBuildLimit())});
            lvt_7_1_.getChatStyle().setColor(EnumChatFormatting.RED);
            this.playerEntity.playerNetServerHandler.sendPacket(new S02PacketChat(lvt_7_1_));
            lvt_4_1_ = true;
        }

        if (lvt_4_1_)
        {
            this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(lvt_2_1_, lvt_5_1_));
            this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(lvt_2_1_, lvt_5_1_.offset(lvt_6_1_)));
        }

        lvt_3_1_ = this.playerEntity.inventory.getCurrentItem();

        if (lvt_3_1_ != null && lvt_3_1_.stackSize == 0)
        {
            this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = null;
            lvt_3_1_ = null;
        }

        if (lvt_3_1_ == null || lvt_3_1_.getMaxItemUseDuration() == 0)
        {
            this.playerEntity.isChangingQuantityOnly = true;
            this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = ItemStack.copyItemStack(this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem]);
            Slot lvt_7_2_ = this.playerEntity.openContainer.getSlotFromInventory(this.playerEntity.inventory, this.playerEntity.inventory.currentItem);
            this.playerEntity.openContainer.detectAndSendChanges();
            this.playerEntity.isChangingQuantityOnly = false;

            if (!ItemStack.areItemStacksEqual(this.playerEntity.inventory.getCurrentItem(), packetIn.getStack()))
            {
                this.sendPacket(new S2FPacketSetSlot(this.playerEntity.openContainer.windowId, lvt_7_2_.slotNumber, this.playerEntity.inventory.getCurrentItem()));
            }
        }
    }

    public void handleSpectate(C18PacketSpectate packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());

        if (this.playerEntity.isSpectator())
        {
            Entity lvt_2_1_ = null;

            for (WorldServer lvt_6_1_ : this.serverController.worldServers)
            {
                if (lvt_6_1_ != null)
                {
                    lvt_2_1_ = packetIn.getEntity(lvt_6_1_);

                    if (lvt_2_1_ != null)
                    {
                        break;
                    }
                }
            }

            if (lvt_2_1_ != null)
            {
                this.playerEntity.setSpectatingEntity(this.playerEntity);
                this.playerEntity.mountEntity((Entity)null);

                if (lvt_2_1_.worldObj != this.playerEntity.worldObj)
                {
                    WorldServer lvt_3_2_ = this.playerEntity.getServerForPlayer();
                    WorldServer lvt_4_2_ = (WorldServer)lvt_2_1_.worldObj;
                    this.playerEntity.dimension = lvt_2_1_.dimension;
                    this.sendPacket(new S07PacketRespawn(this.playerEntity.dimension, lvt_3_2_.getDifficulty(), lvt_3_2_.getWorldInfo().getTerrainType(), this.playerEntity.theItemInWorldManager.getGameType()));
                    lvt_3_2_.removePlayerEntityDangerously(this.playerEntity);
                    this.playerEntity.isDead = false;
                    this.playerEntity.setLocationAndAngles(lvt_2_1_.posX, lvt_2_1_.posY, lvt_2_1_.posZ, lvt_2_1_.rotationYaw, lvt_2_1_.rotationPitch);

                    if (this.playerEntity.isEntityAlive())
                    {
                        lvt_3_2_.updateEntityWithOptionalForce(this.playerEntity, false);
                        lvt_4_2_.spawnEntityInWorld(this.playerEntity);
                        lvt_4_2_.updateEntityWithOptionalForce(this.playerEntity, false);
                    }

                    this.playerEntity.setWorld(lvt_4_2_);
                    this.serverController.getConfigurationManager().preparePlayer(this.playerEntity, lvt_3_2_);
                    this.playerEntity.setPositionAndUpdate(lvt_2_1_.posX, lvt_2_1_.posY, lvt_2_1_.posZ);
                    this.playerEntity.theItemInWorldManager.setWorld(lvt_4_2_);
                    this.serverController.getConfigurationManager().updateTimeAndWeatherForPlayer(this.playerEntity, lvt_4_2_);
                    this.serverController.getConfigurationManager().syncPlayerInventory(this.playerEntity);
                }
                else
                {
                    this.playerEntity.setPositionAndUpdate(lvt_2_1_.posX, lvt_2_1_.posY, lvt_2_1_.posZ);
                }
            }
        }
    }

    public void handleResourcePackStatus(C19PacketResourcePackStatus packetIn)
    {
    }

    /**
     * Invoked when disconnecting, the parameter is a ChatComponent describing the reason for termination
     */
    public void onDisconnect(IChatComponent reason)
    {
        logger.info(this.playerEntity.getName() + " lost connection: " + reason);
        this.serverController.refreshStatusNextTick();
        ChatComponentTranslation lvt_2_1_ = new ChatComponentTranslation("multiplayer.player.left", new Object[] {this.playerEntity.getDisplayName()});
        lvt_2_1_.getChatStyle().setColor(EnumChatFormatting.YELLOW);
        this.serverController.getConfigurationManager().sendChatMsg(lvt_2_1_);
        this.playerEntity.mountEntityAndWakeUp();
        this.serverController.getConfigurationManager().playerLoggedOut(this.playerEntity);

        if (this.serverController.isSinglePlayer() && this.playerEntity.getName().equals(this.serverController.getServerOwner()))
        {
            logger.info("Stopping singleplayer server as player logged out");
            this.serverController.initiateShutdown();
        }
    }

    public void sendPacket(final Packet packetIn)
    {
        if (packetIn instanceof S02PacketChat)
        {
            S02PacketChat lvt_2_1_ = (S02PacketChat)packetIn;
            EntityPlayer.EnumChatVisibility lvt_3_1_ = this.playerEntity.getChatVisibility();

            if (lvt_3_1_ == EntityPlayer.EnumChatVisibility.HIDDEN)
            {
                return;
            }

            if (lvt_3_1_ == EntityPlayer.EnumChatVisibility.SYSTEM && !lvt_2_1_.isChat())
            {
                return;
            }
        }

        try
        {
            this.netManager.sendPacket(packetIn);
        }
        catch (Throwable var5)
        {
            CrashReport lvt_3_2_ = CrashReport.makeCrashReport(var5, "Sending packet");
            CrashReportCategory lvt_4_1_ = lvt_3_2_.makeCategory("Packet being sent");
            lvt_4_1_.addCrashSectionCallable("Packet class", new Callable<String>()
            {
                public String call() throws Exception
                {
                    return packetIn.getClass().getCanonicalName();
                }
                public Object call() throws Exception
                {
                    return this.call();
                }
            });
            throw new ReportedException(lvt_3_2_);
        }
    }

    /**
     * Updates which quickbar slot is selected
     */
    public void processHeldItemChange(C09PacketHeldItemChange packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());

        if (packetIn.getSlotId() >= 0 && packetIn.getSlotId() < InventoryPlayer.getHotbarSize())
        {
            this.playerEntity.inventory.currentItem = packetIn.getSlotId();
            this.playerEntity.markPlayerActive();
        }
        else
        {
            logger.warn(this.playerEntity.getName() + " tried to set an invalid carried item");
        }
    }

    /**
     * Process chat messages (broadcast back to clients) and commands (executes)
     */
    public void processChatMessage(C01PacketChatMessage packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());

        if (this.playerEntity.getChatVisibility() == EntityPlayer.EnumChatVisibility.HIDDEN)
        {
            ChatComponentTranslation lvt_2_1_ = new ChatComponentTranslation("chat.cannotSend", new Object[0]);
            lvt_2_1_.getChatStyle().setColor(EnumChatFormatting.RED);
            this.sendPacket(new S02PacketChat(lvt_2_1_));
        }
        else
        {
            this.playerEntity.markPlayerActive();
            String lvt_2_2_ = packetIn.getMessage();
            lvt_2_2_ = StringUtils.normalizeSpace(lvt_2_2_);

            for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_2_.length(); ++lvt_3_1_)
            {
                if (!ChatAllowedCharacters.isAllowedCharacter(lvt_2_2_.charAt(lvt_3_1_)))
                {
                    this.kickPlayerFromServer("Illegal characters in chat");
                    return;
                }
            }

            if (lvt_2_2_.startsWith("/"))
            {
                this.handleSlashCommand(lvt_2_2_);
            }
            else
            {
                IChatComponent lvt_3_2_ = new ChatComponentTranslation("chat.type.text", new Object[] {this.playerEntity.getDisplayName(), lvt_2_2_});
                this.serverController.getConfigurationManager().sendChatMsgImpl(lvt_3_2_, false);
            }

            this.chatSpamThresholdCount += 20;

            if (this.chatSpamThresholdCount > 200 && !this.serverController.getConfigurationManager().canSendCommands(this.playerEntity.getGameProfile()))
            {
                this.kickPlayerFromServer("disconnect.spam");
            }
        }
    }

    /**
     * Handle commands that start with a /
     */
    private void handleSlashCommand(String command)
    {
        this.serverController.getCommandManager().executeCommand(this.playerEntity, command);
    }

    public void handleAnimation(C0APacketAnimation packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        this.playerEntity.markPlayerActive();
        this.playerEntity.swingItem();
    }

    /**
     * Processes a range of action-types: sneaking, sprinting, waking from sleep, opening the inventory or setting jump
     * height of the horse the player is riding
     */
    public void processEntityAction(C0BPacketEntityAction packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        this.playerEntity.markPlayerActive();

        switch (packetIn.getAction())
        {
            case START_SNEAKING:
                this.playerEntity.setSneaking(true);
                break;

            case STOP_SNEAKING:
                this.playerEntity.setSneaking(false);
                break;

            case START_SPRINTING:
                this.playerEntity.setSprinting(true);
                break;

            case STOP_SPRINTING:
                this.playerEntity.setSprinting(false);
                break;

            case STOP_SLEEPING:
                this.playerEntity.wakeUpPlayer(false, true, true);
                this.hasMoved = false;
                break;

            case RIDING_JUMP:
                if (this.playerEntity.ridingEntity instanceof EntityHorse)
                {
                    ((EntityHorse)this.playerEntity.ridingEntity).setJumpPower(packetIn.getAuxData());
                }

                break;

            case OPEN_INVENTORY:
                if (this.playerEntity.ridingEntity instanceof EntityHorse)
                {
                    ((EntityHorse)this.playerEntity.ridingEntity).openGUI(this.playerEntity);
                }

                break;

            default:
                throw new IllegalArgumentException("Invalid client command!");
        }
    }

    /**
     * Processes interactions ((un)leashing, opening command block GUI) and attacks on an entity with players currently
     * equipped item
     */
    public void processUseEntity(C02PacketUseEntity packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        WorldServer lvt_2_1_ = this.serverController.worldServerForDimension(this.playerEntity.dimension);
        Entity lvt_3_1_ = packetIn.getEntityFromWorld(lvt_2_1_);
        this.playerEntity.markPlayerActive();

        if (lvt_3_1_ != null)
        {
            boolean lvt_4_1_ = this.playerEntity.canEntityBeSeen(lvt_3_1_);
            double lvt_5_1_ = 36.0D;

            if (!lvt_4_1_)
            {
                lvt_5_1_ = 9.0D;
            }

            if (this.playerEntity.getDistanceSqToEntity(lvt_3_1_) < lvt_5_1_)
            {
                if (packetIn.getAction() == C02PacketUseEntity.Action.INTERACT)
                {
                    this.playerEntity.interactWith(lvt_3_1_);
                }
                else if (packetIn.getAction() == C02PacketUseEntity.Action.INTERACT_AT)
                {
                    lvt_3_1_.interactAt(this.playerEntity, packetIn.getHitVec());
                }
                else if (packetIn.getAction() == C02PacketUseEntity.Action.ATTACK)
                {
                    if (lvt_3_1_ instanceof EntityItem || lvt_3_1_ instanceof EntityXPOrb || lvt_3_1_ instanceof EntityArrow || lvt_3_1_ == this.playerEntity)
                    {
                        this.kickPlayerFromServer("Attempting to attack an invalid entity");
                        this.serverController.logWarning("Player " + this.playerEntity.getName() + " tried to attack an invalid entity");
                        return;
                    }

                    this.playerEntity.attackTargetEntityWithCurrentItem(lvt_3_1_);
                }
            }
        }
    }

    /**
     * Processes the client status updates: respawn attempt from player, opening statistics or achievements, or
     * acquiring 'open inventory' achievement
     */
    public void processClientStatus(C16PacketClientStatus packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        this.playerEntity.markPlayerActive();
        C16PacketClientStatus.EnumState lvt_2_1_ = packetIn.getStatus();

        switch (lvt_2_1_)
        {
            case PERFORM_RESPAWN:
                if (this.playerEntity.playerConqueredTheEnd)
                {
                    this.playerEntity = this.serverController.getConfigurationManager().recreatePlayerEntity(this.playerEntity, 0, true);
                }
                else if (this.playerEntity.getServerForPlayer().getWorldInfo().isHardcoreModeEnabled())
                {
                    if (this.serverController.isSinglePlayer() && this.playerEntity.getName().equals(this.serverController.getServerOwner()))
                    {
                        this.playerEntity.playerNetServerHandler.kickPlayerFromServer("You have died. Game over, man, it\'s game over!");
                        this.serverController.deleteWorldAndStopServer();
                    }
                    else
                    {
                        UserListBansEntry lvt_3_1_ = new UserListBansEntry(this.playerEntity.getGameProfile(), (Date)null, "(You just lost the game)", (Date)null, "Death in Hardcore");
                        this.serverController.getConfigurationManager().getBannedPlayers().addEntry(lvt_3_1_);
                        this.playerEntity.playerNetServerHandler.kickPlayerFromServer("You have died. Game over, man, it\'s game over!");
                    }
                }
                else
                {
                    if (this.playerEntity.getHealth() > 0.0F)
                    {
                        return;
                    }

                    this.playerEntity = this.serverController.getConfigurationManager().recreatePlayerEntity(this.playerEntity, 0, false);
                }

                break;

            case REQUEST_STATS:
                this.playerEntity.getStatFile().func_150876_a(this.playerEntity);
                break;

            case OPEN_INVENTORY_ACHIEVEMENT:
                this.playerEntity.triggerAchievement(AchievementList.openInventory);
        }
    }

    /**
     * Processes the client closing windows (container)
     */
    public void processCloseWindow(C0DPacketCloseWindow packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        this.playerEntity.closeContainer();
    }

    /**
     * Executes a container/inventory slot manipulation as indicated by the packet. Sends the serverside result if they
     * didn't match the indicated result and prevents further manipulation by the player until he confirms that it has
     * the same open container/inventory
     */
    public void processClickWindow(C0EPacketClickWindow packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        this.playerEntity.markPlayerActive();

        if (this.playerEntity.openContainer.windowId == packetIn.getWindowId() && this.playerEntity.openContainer.getCanCraft(this.playerEntity))
        {
            if (this.playerEntity.isSpectator())
            {
                List<ItemStack> lvt_2_1_ = Lists.newArrayList();

                for (int lvt_3_1_ = 0; lvt_3_1_ < this.playerEntity.openContainer.inventorySlots.size(); ++lvt_3_1_)
                {
                    lvt_2_1_.add(((Slot)this.playerEntity.openContainer.inventorySlots.get(lvt_3_1_)).getStack());
                }

                this.playerEntity.updateCraftingInventory(this.playerEntity.openContainer, lvt_2_1_);
            }
            else
            {
                ItemStack lvt_2_2_ = this.playerEntity.openContainer.slotClick(packetIn.getSlotId(), packetIn.getUsedButton(), packetIn.getMode(), this.playerEntity);

                if (ItemStack.areItemStacksEqual(packetIn.getClickedItem(), lvt_2_2_))
                {
                    this.playerEntity.playerNetServerHandler.sendPacket(new S32PacketConfirmTransaction(packetIn.getWindowId(), packetIn.getActionNumber(), true));
                    this.playerEntity.isChangingQuantityOnly = true;
                    this.playerEntity.openContainer.detectAndSendChanges();
                    this.playerEntity.updateHeldItem();
                    this.playerEntity.isChangingQuantityOnly = false;
                }
                else
                {
                    this.field_147372_n.addKey(this.playerEntity.openContainer.windowId, Short.valueOf(packetIn.getActionNumber()));
                    this.playerEntity.playerNetServerHandler.sendPacket(new S32PacketConfirmTransaction(packetIn.getWindowId(), packetIn.getActionNumber(), false));
                    this.playerEntity.openContainer.setCanCraft(this.playerEntity, false);
                    List<ItemStack> lvt_3_2_ = Lists.newArrayList();

                    for (int lvt_4_1_ = 0; lvt_4_1_ < this.playerEntity.openContainer.inventorySlots.size(); ++lvt_4_1_)
                    {
                        lvt_3_2_.add(((Slot)this.playerEntity.openContainer.inventorySlots.get(lvt_4_1_)).getStack());
                    }

                    this.playerEntity.updateCraftingInventory(this.playerEntity.openContainer, lvt_3_2_);
                }
            }
        }
    }

    /**
     * Enchants the item identified by the packet given some convoluted conditions (matching window, which
     * should/shouldn't be in use?)
     */
    public void processEnchantItem(C11PacketEnchantItem packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        this.playerEntity.markPlayerActive();

        if (this.playerEntity.openContainer.windowId == packetIn.getWindowId() && this.playerEntity.openContainer.getCanCraft(this.playerEntity) && !this.playerEntity.isSpectator())
        {
            this.playerEntity.openContainer.enchantItem(this.playerEntity, packetIn.getButton());
            this.playerEntity.openContainer.detectAndSendChanges();
        }
    }

    /**
     * Update the server with an ItemStack in a slot.
     */
    public void processCreativeInventoryAction(C10PacketCreativeInventoryAction packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());

        if (this.playerEntity.theItemInWorldManager.isCreative())
        {
            boolean lvt_2_1_ = packetIn.getSlotId() < 0;
            ItemStack lvt_3_1_ = packetIn.getStack();

            if (lvt_3_1_ != null && lvt_3_1_.hasTagCompound() && lvt_3_1_.getTagCompound().hasKey("BlockEntityTag", 10))
            {
                NBTTagCompound lvt_4_1_ = lvt_3_1_.getTagCompound().getCompoundTag("BlockEntityTag");

                if (lvt_4_1_.hasKey("x") && lvt_4_1_.hasKey("y") && lvt_4_1_.hasKey("z"))
                {
                    BlockPos lvt_5_1_ = new BlockPos(lvt_4_1_.getInteger("x"), lvt_4_1_.getInteger("y"), lvt_4_1_.getInteger("z"));
                    TileEntity lvt_6_1_ = this.playerEntity.worldObj.getTileEntity(lvt_5_1_);

                    if (lvt_6_1_ != null)
                    {
                        NBTTagCompound lvt_7_1_ = new NBTTagCompound();
                        lvt_6_1_.writeToNBT(lvt_7_1_);
                        lvt_7_1_.removeTag("x");
                        lvt_7_1_.removeTag("y");
                        lvt_7_1_.removeTag("z");
                        lvt_3_1_.setTagInfo("BlockEntityTag", lvt_7_1_);
                    }
                }
            }

            boolean lvt_4_2_ = packetIn.getSlotId() >= 1 && packetIn.getSlotId() < 36 + InventoryPlayer.getHotbarSize();
            boolean lvt_5_2_ = lvt_3_1_ == null || lvt_3_1_.getItem() != null;
            boolean lvt_6_2_ = lvt_3_1_ == null || lvt_3_1_.getMetadata() >= 0 && lvt_3_1_.stackSize <= 64 && lvt_3_1_.stackSize > 0;

            if (lvt_4_2_ && lvt_5_2_ && lvt_6_2_)
            {
                if (lvt_3_1_ == null)
                {
                    this.playerEntity.inventoryContainer.putStackInSlot(packetIn.getSlotId(), (ItemStack)null);
                }
                else
                {
                    this.playerEntity.inventoryContainer.putStackInSlot(packetIn.getSlotId(), lvt_3_1_);
                }

                this.playerEntity.inventoryContainer.setCanCraft(this.playerEntity, true);
            }
            else if (lvt_2_1_ && lvt_5_2_ && lvt_6_2_ && this.itemDropThreshold < 200)
            {
                this.itemDropThreshold += 20;
                EntityItem lvt_7_2_ = this.playerEntity.dropPlayerItemWithRandomChoice(lvt_3_1_, true);

                if (lvt_7_2_ != null)
                {
                    lvt_7_2_.setAgeToCreativeDespawnTime();
                }
            }
        }
    }

    /**
     * Received in response to the server requesting to confirm that the client-side open container matches the servers'
     * after a mismatched container-slot manipulation. It will unlock the player's ability to manipulate the container
     * contents
     */
    public void processConfirmTransaction(C0FPacketConfirmTransaction packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        Short lvt_2_1_ = (Short)this.field_147372_n.lookup(this.playerEntity.openContainer.windowId);

        if (lvt_2_1_ != null && packetIn.getUid() == lvt_2_1_.shortValue() && this.playerEntity.openContainer.windowId == packetIn.getWindowId() && !this.playerEntity.openContainer.getCanCraft(this.playerEntity) && !this.playerEntity.isSpectator())
        {
            this.playerEntity.openContainer.setCanCraft(this.playerEntity, true);
        }
    }

    public void processUpdateSign(C12PacketUpdateSign packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        this.playerEntity.markPlayerActive();
        WorldServer lvt_2_1_ = this.serverController.worldServerForDimension(this.playerEntity.dimension);
        BlockPos lvt_3_1_ = packetIn.getPosition();

        if (lvt_2_1_.isBlockLoaded(lvt_3_1_))
        {
            TileEntity lvt_4_1_ = lvt_2_1_.getTileEntity(lvt_3_1_);

            if (!(lvt_4_1_ instanceof TileEntitySign))
            {
                return;
            }

            TileEntitySign lvt_5_1_ = (TileEntitySign)lvt_4_1_;

            if (!lvt_5_1_.getIsEditable() || lvt_5_1_.getPlayer() != this.playerEntity)
            {
                this.serverController.logWarning("Player " + this.playerEntity.getName() + " just tried to change non-editable sign");
                return;
            }

            IChatComponent[] lvt_6_1_ = packetIn.getLines();

            for (int lvt_7_1_ = 0; lvt_7_1_ < lvt_6_1_.length; ++lvt_7_1_)
            {
                lvt_5_1_.signText[lvt_7_1_] = new ChatComponentText(EnumChatFormatting.getTextWithoutFormattingCodes(lvt_6_1_[lvt_7_1_].getUnformattedText()));
            }

            lvt_5_1_.markDirty();
            lvt_2_1_.markBlockForUpdate(lvt_3_1_);
        }
    }

    /**
     * Updates a players' ping statistics
     */
    public void processKeepAlive(C00PacketKeepAlive packetIn)
    {
        if (packetIn.getKey() == this.field_147378_h)
        {
            int lvt_2_1_ = (int)(this.currentTimeMillis() - this.lastPingTime);
            this.playerEntity.ping = (this.playerEntity.ping * 3 + lvt_2_1_) / 4;
        }
    }

    private long currentTimeMillis()
    {
        return System.nanoTime() / 1000000L;
    }

    /**
     * Processes a player starting/stopping flying
     */
    public void processPlayerAbilities(C13PacketPlayerAbilities packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        this.playerEntity.capabilities.isFlying = packetIn.isFlying() && this.playerEntity.capabilities.allowFlying;
    }

    /**
     * Retrieves possible tab completions for the requested command string and sends them to the client
     */
    public void processTabComplete(C14PacketTabComplete packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        List<String> lvt_2_1_ = Lists.newArrayList();

        for (String lvt_4_1_ : this.serverController.getTabCompletions(this.playerEntity, packetIn.getMessage(), packetIn.getTargetBlock()))
        {
            lvt_2_1_.add(lvt_4_1_);
        }

        this.playerEntity.playerNetServerHandler.sendPacket(new S3APacketTabComplete((String[])lvt_2_1_.toArray(new String[lvt_2_1_.size()])));
    }

    /**
     * Updates serverside copy of client settings: language, render distance, chat visibility, chat colours, difficulty,
     * and whether to show the cape
     */
    public void processClientSettings(C15PacketClientSettings packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
        this.playerEntity.handleClientSettings(packetIn);
    }

    /**
     * Synchronizes serverside and clientside book contents and signing
     */
    public void processVanilla250Packet(C17PacketCustomPayload packetIn)
    {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());

        if ("MC|BEdit".equals(packetIn.getChannelName()))
        {
            PacketBuffer lvt_2_1_ = new PacketBuffer(Unpooled.wrappedBuffer(packetIn.getBufferData()));

            try
            {
                ItemStack lvt_3_1_ = lvt_2_1_.readItemStackFromBuffer();

                if (lvt_3_1_ != null)
                {
                    if (!ItemWritableBook.isNBTValid(lvt_3_1_.getTagCompound()))
                    {
                        throw new IOException("Invalid book tag!");
                    }

                    ItemStack lvt_4_1_ = this.playerEntity.inventory.getCurrentItem();

                    if (lvt_4_1_ == null)
                    {
                        return;
                    }

                    if (lvt_3_1_.getItem() == Items.writable_book && lvt_3_1_.getItem() == lvt_4_1_.getItem())
                    {
                        lvt_4_1_.setTagInfo("pages", lvt_3_1_.getTagCompound().getTagList("pages", 8));
                    }

                    return;
                }
            }
            catch (Exception var36)
            {
                logger.error("Couldn\'t handle book info", var36);
                return;
            }
            finally
            {
                lvt_2_1_.release();
            }

            return;
        }
        else if ("MC|BSign".equals(packetIn.getChannelName()))
        {
            PacketBuffer lvt_2_2_ = new PacketBuffer(Unpooled.wrappedBuffer(packetIn.getBufferData()));

            try
            {
                ItemStack lvt_3_3_ = lvt_2_2_.readItemStackFromBuffer();

                if (lvt_3_3_ != null)
                {
                    if (!ItemEditableBook.validBookTagContents(lvt_3_3_.getTagCompound()))
                    {
                        throw new IOException("Invalid book tag!");
                    }

                    ItemStack lvt_4_2_ = this.playerEntity.inventory.getCurrentItem();

                    if (lvt_4_2_ == null)
                    {
                        return;
                    }

                    if (lvt_3_3_.getItem() == Items.written_book && lvt_4_2_.getItem() == Items.writable_book)
                    {
                        lvt_4_2_.setTagInfo("author", new NBTTagString(this.playerEntity.getName()));
                        lvt_4_2_.setTagInfo("title", new NBTTagString(lvt_3_3_.getTagCompound().getString("title")));
                        lvt_4_2_.setTagInfo("pages", lvt_3_3_.getTagCompound().getTagList("pages", 8));
                        lvt_4_2_.setItem(Items.written_book);
                    }

                    return;
                }
            }
            catch (Exception var38)
            {
                logger.error("Couldn\'t sign book", var38);
                return;
            }
            finally
            {
                lvt_2_2_.release();
            }

            return;
        }
        else if ("MC|TrSel".equals(packetIn.getChannelName()))
        {
            try
            {
                int lvt_2_3_ = packetIn.getBufferData().readInt();
                Container lvt_3_5_ = this.playerEntity.openContainer;

                if (lvt_3_5_ instanceof ContainerMerchant)
                {
                    ((ContainerMerchant)lvt_3_5_).setCurrentRecipeIndex(lvt_2_3_);
                }
            }
            catch (Exception var35)
            {
                logger.error("Couldn\'t select trade", var35);
            }
        }
        else if ("MC|AdvCdm".equals(packetIn.getChannelName()))
        {
            if (!this.serverController.isCommandBlockEnabled())
            {
                this.playerEntity.addChatMessage(new ChatComponentTranslation("advMode.notEnabled", new Object[0]));
            }
            else if (this.playerEntity.canCommandSenderUseCommand(2, "") && this.playerEntity.capabilities.isCreativeMode)
            {
                PacketBuffer lvt_2_5_ = packetIn.getBufferData();

                try
                {
                    int lvt_3_6_ = lvt_2_5_.readByte();
                    CommandBlockLogic lvt_4_3_ = null;

                    if (lvt_3_6_ == 0)
                    {
                        TileEntity lvt_5_1_ = this.playerEntity.worldObj.getTileEntity(new BlockPos(lvt_2_5_.readInt(), lvt_2_5_.readInt(), lvt_2_5_.readInt()));

                        if (lvt_5_1_ instanceof TileEntityCommandBlock)
                        {
                            lvt_4_3_ = ((TileEntityCommandBlock)lvt_5_1_).getCommandBlockLogic();
                        }
                    }
                    else if (lvt_3_6_ == 1)
                    {
                        Entity lvt_5_2_ = this.playerEntity.worldObj.getEntityByID(lvt_2_5_.readInt());

                        if (lvt_5_2_ instanceof EntityMinecartCommandBlock)
                        {
                            lvt_4_3_ = ((EntityMinecartCommandBlock)lvt_5_2_).getCommandBlockLogic();
                        }
                    }

                    String lvt_5_3_ = lvt_2_5_.readStringFromBuffer(lvt_2_5_.readableBytes());
                    boolean lvt_6_1_ = lvt_2_5_.readBoolean();

                    if (lvt_4_3_ != null)
                    {
                        lvt_4_3_.setCommand(lvt_5_3_);
                        lvt_4_3_.setTrackOutput(lvt_6_1_);

                        if (!lvt_6_1_)
                        {
                            lvt_4_3_.setLastOutput((IChatComponent)null);
                        }

                        lvt_4_3_.updateCommand();
                        this.playerEntity.addChatMessage(new ChatComponentTranslation("advMode.setCommand.success", new Object[] {lvt_5_3_}));
                    }
                }
                catch (Exception var33)
                {
                    logger.error("Couldn\'t set command block", var33);
                }
                finally
                {
                    lvt_2_5_.release();
                }
            }
            else
            {
                this.playerEntity.addChatMessage(new ChatComponentTranslation("advMode.notAllowed", new Object[0]));
            }
        }
        else if ("MC|Beacon".equals(packetIn.getChannelName()))
        {
            if (this.playerEntity.openContainer instanceof ContainerBeacon)
            {
                try
                {
                    PacketBuffer lvt_2_6_ = packetIn.getBufferData();
                    int lvt_3_8_ = lvt_2_6_.readInt();
                    int lvt_4_4_ = lvt_2_6_.readInt();
                    ContainerBeacon lvt_5_4_ = (ContainerBeacon)this.playerEntity.openContainer;
                    Slot lvt_6_2_ = lvt_5_4_.getSlot(0);

                    if (lvt_6_2_.getHasStack())
                    {
                        lvt_6_2_.decrStackSize(1);
                        IInventory lvt_7_1_ = lvt_5_4_.func_180611_e();
                        lvt_7_1_.setField(1, lvt_3_8_);
                        lvt_7_1_.setField(2, lvt_4_4_);
                        lvt_7_1_.markDirty();
                    }
                }
                catch (Exception var32)
                {
                    logger.error("Couldn\'t set beacon", var32);
                }
            }
        }
        else if ("MC|ItemName".equals(packetIn.getChannelName()) && this.playerEntity.openContainer instanceof ContainerRepair)
        {
            ContainerRepair lvt_2_8_ = (ContainerRepair)this.playerEntity.openContainer;

            if (packetIn.getBufferData() != null && packetIn.getBufferData().readableBytes() >= 1)
            {
                String lvt_3_9_ = ChatAllowedCharacters.filterAllowedCharacters(packetIn.getBufferData().readStringFromBuffer(32767));

                if (lvt_3_9_.length() <= 30)
                {
                    lvt_2_8_.updateItemName(lvt_3_9_);
                }
            }
            else
            {
                lvt_2_8_.updateItemName("");
            }
        }
    }
}
