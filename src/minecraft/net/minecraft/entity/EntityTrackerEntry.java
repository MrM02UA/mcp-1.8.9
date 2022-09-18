package net.minecraft.entity;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
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
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S49PacketUpdateEntityNBT;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.storage.MapData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTrackerEntry
{
    private static final Logger logger = LogManager.getLogger();

    /** The entity that this EntityTrackerEntry tracks. */
    public Entity trackedEntity;
    public int trackingDistanceThreshold;

    /** check for sync when ticks % updateFrequency==0 */
    public int updateFrequency;

    /** The encoded entity X position. */
    public int encodedPosX;

    /** The encoded entity Y position. */
    public int encodedPosY;

    /** The encoded entity Z position. */
    public int encodedPosZ;

    /** The encoded entity yaw rotation. */
    public int encodedRotationYaw;

    /** The encoded entity pitch rotation. */
    public int encodedRotationPitch;
    public int lastHeadMotion;
    public double lastTrackedEntityMotionX;
    public double lastTrackedEntityMotionY;
    public double motionZ;
    public int updateCounter;
    private double lastTrackedEntityPosX;
    private double lastTrackedEntityPosY;
    private double lastTrackedEntityPosZ;
    private boolean firstUpdateDone;
    private boolean sendVelocityUpdates;

    /**
     * every 400 ticks a  full teleport packet is sent, rather than just a "move me +x" command, so that position
     * remains fully synced.
     */
    private int ticksSinceLastForcedTeleport;
    private Entity field_85178_v;
    private boolean ridingEntity;
    private boolean onGround;
    public boolean playerEntitiesUpdated;
    public Set<EntityPlayerMP> trackingPlayers = Sets.newHashSet();

    public EntityTrackerEntry(Entity trackedEntityIn, int trackingDistanceThresholdIn, int updateFrequencyIn, boolean sendVelocityUpdatesIn)
    {
        this.trackedEntity = trackedEntityIn;
        this.trackingDistanceThreshold = trackingDistanceThresholdIn;
        this.updateFrequency = updateFrequencyIn;
        this.sendVelocityUpdates = sendVelocityUpdatesIn;
        this.encodedPosX = MathHelper.floor_double(trackedEntityIn.posX * 32.0D);
        this.encodedPosY = MathHelper.floor_double(trackedEntityIn.posY * 32.0D);
        this.encodedPosZ = MathHelper.floor_double(trackedEntityIn.posZ * 32.0D);
        this.encodedRotationYaw = MathHelper.floor_float(trackedEntityIn.rotationYaw * 256.0F / 360.0F);
        this.encodedRotationPitch = MathHelper.floor_float(trackedEntityIn.rotationPitch * 256.0F / 360.0F);
        this.lastHeadMotion = MathHelper.floor_float(trackedEntityIn.getRotationYawHead() * 256.0F / 360.0F);
        this.onGround = trackedEntityIn.onGround;
    }

    public boolean equals(Object p_equals_1_)
    {
        return p_equals_1_ instanceof EntityTrackerEntry ? ((EntityTrackerEntry)p_equals_1_).trackedEntity.getEntityId() == this.trackedEntity.getEntityId() : false;
    }

    public int hashCode()
    {
        return this.trackedEntity.getEntityId();
    }

    public void updatePlayerList(List<EntityPlayer> players)
    {
        this.playerEntitiesUpdated = false;

        if (!this.firstUpdateDone || this.trackedEntity.getDistanceSq(this.lastTrackedEntityPosX, this.lastTrackedEntityPosY, this.lastTrackedEntityPosZ) > 16.0D)
        {
            this.lastTrackedEntityPosX = this.trackedEntity.posX;
            this.lastTrackedEntityPosY = this.trackedEntity.posY;
            this.lastTrackedEntityPosZ = this.trackedEntity.posZ;
            this.firstUpdateDone = true;
            this.playerEntitiesUpdated = true;
            this.updatePlayerEntities(players);
        }

        if (this.field_85178_v != this.trackedEntity.ridingEntity || this.trackedEntity.ridingEntity != null && this.updateCounter % 60 == 0)
        {
            this.field_85178_v = this.trackedEntity.ridingEntity;
            this.sendPacketToTrackedPlayers(new S1BPacketEntityAttach(0, this.trackedEntity, this.trackedEntity.ridingEntity));
        }

        if (this.trackedEntity instanceof EntityItemFrame && this.updateCounter % 10 == 0)
        {
            EntityItemFrame lvt_2_1_ = (EntityItemFrame)this.trackedEntity;
            ItemStack lvt_3_1_ = lvt_2_1_.getDisplayedItem();

            if (lvt_3_1_ != null && lvt_3_1_.getItem() instanceof ItemMap)
            {
                MapData lvt_4_1_ = Items.filled_map.getMapData(lvt_3_1_, this.trackedEntity.worldObj);

                for (EntityPlayer lvt_6_1_ : players)
                {
                    EntityPlayerMP lvt_7_1_ = (EntityPlayerMP)lvt_6_1_;
                    lvt_4_1_.updateVisiblePlayers(lvt_7_1_, lvt_3_1_);
                    Packet lvt_8_1_ = Items.filled_map.createMapDataPacket(lvt_3_1_, this.trackedEntity.worldObj, lvt_7_1_);

                    if (lvt_8_1_ != null)
                    {
                        lvt_7_1_.playerNetServerHandler.sendPacket(lvt_8_1_);
                    }
                }
            }

            this.sendMetadataToAllAssociatedPlayers();
        }

        if (this.updateCounter % this.updateFrequency == 0 || this.trackedEntity.isAirBorne || this.trackedEntity.getDataWatcher().hasObjectChanged())
        {
            if (this.trackedEntity.ridingEntity == null)
            {
                ++this.ticksSinceLastForcedTeleport;
                int lvt_2_2_ = MathHelper.floor_double(this.trackedEntity.posX * 32.0D);
                int lvt_3_2_ = MathHelper.floor_double(this.trackedEntity.posY * 32.0D);
                int lvt_4_2_ = MathHelper.floor_double(this.trackedEntity.posZ * 32.0D);
                int lvt_5_2_ = MathHelper.floor_float(this.trackedEntity.rotationYaw * 256.0F / 360.0F);
                int lvt_6_2_ = MathHelper.floor_float(this.trackedEntity.rotationPitch * 256.0F / 360.0F);
                int lvt_7_2_ = lvt_2_2_ - this.encodedPosX;
                int lvt_8_2_ = lvt_3_2_ - this.encodedPosY;
                int lvt_9_1_ = lvt_4_2_ - this.encodedPosZ;
                Packet lvt_10_1_ = null;
                boolean lvt_11_1_ = Math.abs(lvt_7_2_) >= 4 || Math.abs(lvt_8_2_) >= 4 || Math.abs(lvt_9_1_) >= 4 || this.updateCounter % 60 == 0;
                boolean lvt_12_1_ = Math.abs(lvt_5_2_ - this.encodedRotationYaw) >= 4 || Math.abs(lvt_6_2_ - this.encodedRotationPitch) >= 4;

                if (this.updateCounter > 0 || this.trackedEntity instanceof EntityArrow)
                {
                    if (lvt_7_2_ >= -128 && lvt_7_2_ < 128 && lvt_8_2_ >= -128 && lvt_8_2_ < 128 && lvt_9_1_ >= -128 && lvt_9_1_ < 128 && this.ticksSinceLastForcedTeleport <= 400 && !this.ridingEntity && this.onGround == this.trackedEntity.onGround)
                    {
                        if ((!lvt_11_1_ || !lvt_12_1_) && !(this.trackedEntity instanceof EntityArrow))
                        {
                            if (lvt_11_1_)
                            {
                                lvt_10_1_ = new S14PacketEntity.S15PacketEntityRelMove(this.trackedEntity.getEntityId(), (byte)lvt_7_2_, (byte)lvt_8_2_, (byte)lvt_9_1_, this.trackedEntity.onGround);
                            }
                            else if (lvt_12_1_)
                            {
                                lvt_10_1_ = new S14PacketEntity.S16PacketEntityLook(this.trackedEntity.getEntityId(), (byte)lvt_5_2_, (byte)lvt_6_2_, this.trackedEntity.onGround);
                            }
                        }
                        else
                        {
                            lvt_10_1_ = new S14PacketEntity.S17PacketEntityLookMove(this.trackedEntity.getEntityId(), (byte)lvt_7_2_, (byte)lvt_8_2_, (byte)lvt_9_1_, (byte)lvt_5_2_, (byte)lvt_6_2_, this.trackedEntity.onGround);
                        }
                    }
                    else
                    {
                        this.onGround = this.trackedEntity.onGround;
                        this.ticksSinceLastForcedTeleport = 0;
                        lvt_10_1_ = new S18PacketEntityTeleport(this.trackedEntity.getEntityId(), lvt_2_2_, lvt_3_2_, lvt_4_2_, (byte)lvt_5_2_, (byte)lvt_6_2_, this.trackedEntity.onGround);
                    }
                }

                if (this.sendVelocityUpdates)
                {
                    double lvt_13_1_ = this.trackedEntity.motionX - this.lastTrackedEntityMotionX;
                    double lvt_15_1_ = this.trackedEntity.motionY - this.lastTrackedEntityMotionY;
                    double lvt_17_1_ = this.trackedEntity.motionZ - this.motionZ;
                    double lvt_19_1_ = 0.02D;
                    double lvt_21_1_ = lvt_13_1_ * lvt_13_1_ + lvt_15_1_ * lvt_15_1_ + lvt_17_1_ * lvt_17_1_;

                    if (lvt_21_1_ > lvt_19_1_ * lvt_19_1_ || lvt_21_1_ > 0.0D && this.trackedEntity.motionX == 0.0D && this.trackedEntity.motionY == 0.0D && this.trackedEntity.motionZ == 0.0D)
                    {
                        this.lastTrackedEntityMotionX = this.trackedEntity.motionX;
                        this.lastTrackedEntityMotionY = this.trackedEntity.motionY;
                        this.motionZ = this.trackedEntity.motionZ;
                        this.sendPacketToTrackedPlayers(new S12PacketEntityVelocity(this.trackedEntity.getEntityId(), this.lastTrackedEntityMotionX, this.lastTrackedEntityMotionY, this.motionZ));
                    }
                }

                if (lvt_10_1_ != null)
                {
                    this.sendPacketToTrackedPlayers(lvt_10_1_);
                }

                this.sendMetadataToAllAssociatedPlayers();

                if (lvt_11_1_)
                {
                    this.encodedPosX = lvt_2_2_;
                    this.encodedPosY = lvt_3_2_;
                    this.encodedPosZ = lvt_4_2_;
                }

                if (lvt_12_1_)
                {
                    this.encodedRotationYaw = lvt_5_2_;
                    this.encodedRotationPitch = lvt_6_2_;
                }

                this.ridingEntity = false;
            }
            else
            {
                int lvt_2_3_ = MathHelper.floor_float(this.trackedEntity.rotationYaw * 256.0F / 360.0F);
                int lvt_3_3_ = MathHelper.floor_float(this.trackedEntity.rotationPitch * 256.0F / 360.0F);
                boolean lvt_4_3_ = Math.abs(lvt_2_3_ - this.encodedRotationYaw) >= 4 || Math.abs(lvt_3_3_ - this.encodedRotationPitch) >= 4;

                if (lvt_4_3_)
                {
                    this.sendPacketToTrackedPlayers(new S14PacketEntity.S16PacketEntityLook(this.trackedEntity.getEntityId(), (byte)lvt_2_3_, (byte)lvt_3_3_, this.trackedEntity.onGround));
                    this.encodedRotationYaw = lvt_2_3_;
                    this.encodedRotationPitch = lvt_3_3_;
                }

                this.encodedPosX = MathHelper.floor_double(this.trackedEntity.posX * 32.0D);
                this.encodedPosY = MathHelper.floor_double(this.trackedEntity.posY * 32.0D);
                this.encodedPosZ = MathHelper.floor_double(this.trackedEntity.posZ * 32.0D);
                this.sendMetadataToAllAssociatedPlayers();
                this.ridingEntity = true;
            }

            int lvt_2_4_ = MathHelper.floor_float(this.trackedEntity.getRotationYawHead() * 256.0F / 360.0F);

            if (Math.abs(lvt_2_4_ - this.lastHeadMotion) >= 4)
            {
                this.sendPacketToTrackedPlayers(new S19PacketEntityHeadLook(this.trackedEntity, (byte)lvt_2_4_));
                this.lastHeadMotion = lvt_2_4_;
            }

            this.trackedEntity.isAirBorne = false;
        }

        ++this.updateCounter;

        if (this.trackedEntity.velocityChanged)
        {
            this.func_151261_b(new S12PacketEntityVelocity(this.trackedEntity));
            this.trackedEntity.velocityChanged = false;
        }
    }

    /**
     * Sends the entity metadata (DataWatcher) and attributes to all players tracking this entity, including the entity
     * itself if a player.
     */
    private void sendMetadataToAllAssociatedPlayers()
    {
        DataWatcher lvt_1_1_ = this.trackedEntity.getDataWatcher();

        if (lvt_1_1_.hasObjectChanged())
        {
            this.func_151261_b(new S1CPacketEntityMetadata(this.trackedEntity.getEntityId(), lvt_1_1_, false));
        }

        if (this.trackedEntity instanceof EntityLivingBase)
        {
            ServersideAttributeMap lvt_2_1_ = (ServersideAttributeMap)((EntityLivingBase)this.trackedEntity).getAttributeMap();
            Set<IAttributeInstance> lvt_3_1_ = lvt_2_1_.getAttributeInstanceSet();

            if (!lvt_3_1_.isEmpty())
            {
                this.func_151261_b(new S20PacketEntityProperties(this.trackedEntity.getEntityId(), lvt_3_1_));
            }

            lvt_3_1_.clear();
        }
    }

    /**
     * Send the given packet to all players tracking this entity.
     */
    public void sendPacketToTrackedPlayers(Packet packetIn)
    {
        for (EntityPlayerMP lvt_3_1_ : this.trackingPlayers)
        {
            lvt_3_1_.playerNetServerHandler.sendPacket(packetIn);
        }
    }

    public void func_151261_b(Packet packetIn)
    {
        this.sendPacketToTrackedPlayers(packetIn);

        if (this.trackedEntity instanceof EntityPlayerMP)
        {
            ((EntityPlayerMP)this.trackedEntity).playerNetServerHandler.sendPacket(packetIn);
        }
    }

    public void sendDestroyEntityPacketToTrackedPlayers()
    {
        for (EntityPlayerMP lvt_2_1_ : this.trackingPlayers)
        {
            lvt_2_1_.removeEntity(this.trackedEntity);
        }
    }

    public void removeFromTrackedPlayers(EntityPlayerMP playerMP)
    {
        if (this.trackingPlayers.contains(playerMP))
        {
            playerMP.removeEntity(this.trackedEntity);
            this.trackingPlayers.remove(playerMP);
        }
    }

    public void updatePlayerEntity(EntityPlayerMP playerMP)
    {
        if (playerMP != this.trackedEntity)
        {
            if (this.func_180233_c(playerMP))
            {
                if (!this.trackingPlayers.contains(playerMP) && (this.isPlayerWatchingThisChunk(playerMP) || this.trackedEntity.forceSpawn))
                {
                    this.trackingPlayers.add(playerMP);
                    Packet lvt_2_1_ = this.createSpawnPacket();
                    playerMP.playerNetServerHandler.sendPacket(lvt_2_1_);

                    if (!this.trackedEntity.getDataWatcher().getIsBlank())
                    {
                        playerMP.playerNetServerHandler.sendPacket(new S1CPacketEntityMetadata(this.trackedEntity.getEntityId(), this.trackedEntity.getDataWatcher(), true));
                    }

                    NBTTagCompound lvt_3_1_ = this.trackedEntity.getNBTTagCompound();

                    if (lvt_3_1_ != null)
                    {
                        playerMP.playerNetServerHandler.sendPacket(new S49PacketUpdateEntityNBT(this.trackedEntity.getEntityId(), lvt_3_1_));
                    }

                    if (this.trackedEntity instanceof EntityLivingBase)
                    {
                        ServersideAttributeMap lvt_4_1_ = (ServersideAttributeMap)((EntityLivingBase)this.trackedEntity).getAttributeMap();
                        Collection<IAttributeInstance> lvt_5_1_ = lvt_4_1_.getWatchedAttributes();

                        if (!lvt_5_1_.isEmpty())
                        {
                            playerMP.playerNetServerHandler.sendPacket(new S20PacketEntityProperties(this.trackedEntity.getEntityId(), lvt_5_1_));
                        }
                    }

                    this.lastTrackedEntityMotionX = this.trackedEntity.motionX;
                    this.lastTrackedEntityMotionY = this.trackedEntity.motionY;
                    this.motionZ = this.trackedEntity.motionZ;

                    if (this.sendVelocityUpdates && !(lvt_2_1_ instanceof S0FPacketSpawnMob))
                    {
                        playerMP.playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(this.trackedEntity.getEntityId(), this.trackedEntity.motionX, this.trackedEntity.motionY, this.trackedEntity.motionZ));
                    }

                    if (this.trackedEntity.ridingEntity != null)
                    {
                        playerMP.playerNetServerHandler.sendPacket(new S1BPacketEntityAttach(0, this.trackedEntity, this.trackedEntity.ridingEntity));
                    }

                    if (this.trackedEntity instanceof EntityLiving && ((EntityLiving)this.trackedEntity).getLeashedToEntity() != null)
                    {
                        playerMP.playerNetServerHandler.sendPacket(new S1BPacketEntityAttach(1, this.trackedEntity, ((EntityLiving)this.trackedEntity).getLeashedToEntity()));
                    }

                    if (this.trackedEntity instanceof EntityLivingBase)
                    {
                        for (int lvt_4_2_ = 0; lvt_4_2_ < 5; ++lvt_4_2_)
                        {
                            ItemStack lvt_5_2_ = ((EntityLivingBase)this.trackedEntity).getEquipmentInSlot(lvt_4_2_);

                            if (lvt_5_2_ != null)
                            {
                                playerMP.playerNetServerHandler.sendPacket(new S04PacketEntityEquipment(this.trackedEntity.getEntityId(), lvt_4_2_, lvt_5_2_));
                            }
                        }
                    }

                    if (this.trackedEntity instanceof EntityPlayer)
                    {
                        EntityPlayer lvt_4_3_ = (EntityPlayer)this.trackedEntity;

                        if (lvt_4_3_.isPlayerSleeping())
                        {
                            playerMP.playerNetServerHandler.sendPacket(new S0APacketUseBed(lvt_4_3_, new BlockPos(this.trackedEntity)));
                        }
                    }

                    if (this.trackedEntity instanceof EntityLivingBase)
                    {
                        EntityLivingBase lvt_4_4_ = (EntityLivingBase)this.trackedEntity;

                        for (PotionEffect lvt_6_1_ : lvt_4_4_.getActivePotionEffects())
                        {
                            playerMP.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(this.trackedEntity.getEntityId(), lvt_6_1_));
                        }
                    }
                }
            }
            else if (this.trackingPlayers.contains(playerMP))
            {
                this.trackingPlayers.remove(playerMP);
                playerMP.removeEntity(this.trackedEntity);
            }
        }
    }

    public boolean func_180233_c(EntityPlayerMP playerMP)
    {
        double lvt_2_1_ = playerMP.posX - (double)(this.encodedPosX / 32);
        double lvt_4_1_ = playerMP.posZ - (double)(this.encodedPosZ / 32);
        return lvt_2_1_ >= (double)(-this.trackingDistanceThreshold) && lvt_2_1_ <= (double)this.trackingDistanceThreshold && lvt_4_1_ >= (double)(-this.trackingDistanceThreshold) && lvt_4_1_ <= (double)this.trackingDistanceThreshold && this.trackedEntity.isSpectatedByPlayer(playerMP);
    }

    private boolean isPlayerWatchingThisChunk(EntityPlayerMP playerMP)
    {
        return playerMP.getServerForPlayer().getPlayerManager().isPlayerWatchingChunk(playerMP, this.trackedEntity.chunkCoordX, this.trackedEntity.chunkCoordZ);
    }

    public void updatePlayerEntities(List<EntityPlayer> players)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < players.size(); ++lvt_2_1_)
        {
            this.updatePlayerEntity((EntityPlayerMP)players.get(lvt_2_1_));
        }
    }

    /**
     * Creates a spawn packet for the entity managed by this entry.
     */
    private Packet createSpawnPacket()
    {
        if (this.trackedEntity.isDead)
        {
            logger.warn("Fetching addPacket for removed entity");
        }

        if (this.trackedEntity instanceof EntityItem)
        {
            return new S0EPacketSpawnObject(this.trackedEntity, 2, 1);
        }
        else if (this.trackedEntity instanceof EntityPlayerMP)
        {
            return new S0CPacketSpawnPlayer((EntityPlayer)this.trackedEntity);
        }
        else if (this.trackedEntity instanceof EntityMinecart)
        {
            EntityMinecart lvt_1_1_ = (EntityMinecart)this.trackedEntity;
            return new S0EPacketSpawnObject(this.trackedEntity, 10, lvt_1_1_.getMinecartType().getNetworkID());
        }
        else if (this.trackedEntity instanceof EntityBoat)
        {
            return new S0EPacketSpawnObject(this.trackedEntity, 1);
        }
        else if (this.trackedEntity instanceof IAnimals)
        {
            this.lastHeadMotion = MathHelper.floor_float(this.trackedEntity.getRotationYawHead() * 256.0F / 360.0F);
            return new S0FPacketSpawnMob((EntityLivingBase)this.trackedEntity);
        }
        else if (this.trackedEntity instanceof EntityFishHook)
        {
            Entity lvt_1_2_ = ((EntityFishHook)this.trackedEntity).angler;
            return new S0EPacketSpawnObject(this.trackedEntity, 90, lvt_1_2_ != null ? lvt_1_2_.getEntityId() : this.trackedEntity.getEntityId());
        }
        else if (this.trackedEntity instanceof EntityArrow)
        {
            Entity lvt_1_3_ = ((EntityArrow)this.trackedEntity).shootingEntity;
            return new S0EPacketSpawnObject(this.trackedEntity, 60, lvt_1_3_ != null ? lvt_1_3_.getEntityId() : this.trackedEntity.getEntityId());
        }
        else if (this.trackedEntity instanceof EntitySnowball)
        {
            return new S0EPacketSpawnObject(this.trackedEntity, 61);
        }
        else if (this.trackedEntity instanceof EntityPotion)
        {
            return new S0EPacketSpawnObject(this.trackedEntity, 73, ((EntityPotion)this.trackedEntity).getPotionDamage());
        }
        else if (this.trackedEntity instanceof EntityExpBottle)
        {
            return new S0EPacketSpawnObject(this.trackedEntity, 75);
        }
        else if (this.trackedEntity instanceof EntityEnderPearl)
        {
            return new S0EPacketSpawnObject(this.trackedEntity, 65);
        }
        else if (this.trackedEntity instanceof EntityEnderEye)
        {
            return new S0EPacketSpawnObject(this.trackedEntity, 72);
        }
        else if (this.trackedEntity instanceof EntityFireworkRocket)
        {
            return new S0EPacketSpawnObject(this.trackedEntity, 76);
        }
        else if (this.trackedEntity instanceof EntityFireball)
        {
            EntityFireball lvt_1_4_ = (EntityFireball)this.trackedEntity;
            S0EPacketSpawnObject lvt_2_1_ = null;
            int lvt_3_1_ = 63;

            if (this.trackedEntity instanceof EntitySmallFireball)
            {
                lvt_3_1_ = 64;
            }
            else if (this.trackedEntity instanceof EntityWitherSkull)
            {
                lvt_3_1_ = 66;
            }

            if (lvt_1_4_.shootingEntity != null)
            {
                lvt_2_1_ = new S0EPacketSpawnObject(this.trackedEntity, lvt_3_1_, ((EntityFireball)this.trackedEntity).shootingEntity.getEntityId());
            }
            else
            {
                lvt_2_1_ = new S0EPacketSpawnObject(this.trackedEntity, lvt_3_1_, 0);
            }

            lvt_2_1_.setSpeedX((int)(lvt_1_4_.accelerationX * 8000.0D));
            lvt_2_1_.setSpeedY((int)(lvt_1_4_.accelerationY * 8000.0D));
            lvt_2_1_.setSpeedZ((int)(lvt_1_4_.accelerationZ * 8000.0D));
            return lvt_2_1_;
        }
        else if (this.trackedEntity instanceof EntityEgg)
        {
            return new S0EPacketSpawnObject(this.trackedEntity, 62);
        }
        else if (this.trackedEntity instanceof EntityTNTPrimed)
        {
            return new S0EPacketSpawnObject(this.trackedEntity, 50);
        }
        else if (this.trackedEntity instanceof EntityEnderCrystal)
        {
            return new S0EPacketSpawnObject(this.trackedEntity, 51);
        }
        else if (this.trackedEntity instanceof EntityFallingBlock)
        {
            EntityFallingBlock lvt_1_5_ = (EntityFallingBlock)this.trackedEntity;
            return new S0EPacketSpawnObject(this.trackedEntity, 70, Block.getStateId(lvt_1_5_.getBlock()));
        }
        else if (this.trackedEntity instanceof EntityArmorStand)
        {
            return new S0EPacketSpawnObject(this.trackedEntity, 78);
        }
        else if (this.trackedEntity instanceof EntityPainting)
        {
            return new S10PacketSpawnPainting((EntityPainting)this.trackedEntity);
        }
        else if (this.trackedEntity instanceof EntityItemFrame)
        {
            EntityItemFrame lvt_1_6_ = (EntityItemFrame)this.trackedEntity;
            S0EPacketSpawnObject lvt_2_2_ = new S0EPacketSpawnObject(this.trackedEntity, 71, lvt_1_6_.facingDirection.getHorizontalIndex());
            BlockPos lvt_3_2_ = lvt_1_6_.getHangingPosition();
            lvt_2_2_.setX(MathHelper.floor_float((float)(lvt_3_2_.getX() * 32)));
            lvt_2_2_.setY(MathHelper.floor_float((float)(lvt_3_2_.getY() * 32)));
            lvt_2_2_.setZ(MathHelper.floor_float((float)(lvt_3_2_.getZ() * 32)));
            return lvt_2_2_;
        }
        else if (this.trackedEntity instanceof EntityLeashKnot)
        {
            EntityLeashKnot lvt_1_7_ = (EntityLeashKnot)this.trackedEntity;
            S0EPacketSpawnObject lvt_2_3_ = new S0EPacketSpawnObject(this.trackedEntity, 77);
            BlockPos lvt_3_3_ = lvt_1_7_.getHangingPosition();
            lvt_2_3_.setX(MathHelper.floor_float((float)(lvt_3_3_.getX() * 32)));
            lvt_2_3_.setY(MathHelper.floor_float((float)(lvt_3_3_.getY() * 32)));
            lvt_2_3_.setZ(MathHelper.floor_float((float)(lvt_3_3_.getZ() * 32)));
            return lvt_2_3_;
        }
        else if (this.trackedEntity instanceof EntityXPOrb)
        {
            return new S11PacketSpawnExperienceOrb((EntityXPOrb)this.trackedEntity);
        }
        else
        {
            throw new IllegalArgumentException("Don\'t know how to add " + this.trackedEntity.getClass() + "!");
        }
    }

    /**
     * Remove a tracked player from our list and tell the tracked player to destroy us from their world.
     */
    public void removeTrackedPlayerSymmetric(EntityPlayerMP playerMP)
    {
        if (this.trackingPlayers.contains(playerMP))
        {
            this.trackingPlayers.remove(playerMP);
            playerMP.removeEntity(this.trackedEntity);
        }
    }
}
