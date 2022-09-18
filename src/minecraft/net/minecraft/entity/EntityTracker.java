package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.network.Packet;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTracker
{
    private static final Logger logger = LogManager.getLogger();
    private final WorldServer theWorld;
    private Set<EntityTrackerEntry> trackedEntities = Sets.newHashSet();
    private IntHashMap<EntityTrackerEntry> trackedEntityHashTable = new IntHashMap();
    private int maxTrackingDistanceThreshold;

    public EntityTracker(WorldServer theWorldIn)
    {
        this.theWorld = theWorldIn;
        this.maxTrackingDistanceThreshold = theWorldIn.getMinecraftServer().getConfigurationManager().getEntityViewDistance();
    }

    public void trackEntity(Entity entityIn)
    {
        if (entityIn instanceof EntityPlayerMP)
        {
            this.trackEntity(entityIn, 512, 2);
            EntityPlayerMP lvt_2_1_ = (EntityPlayerMP)entityIn;

            for (EntityTrackerEntry lvt_4_1_ : this.trackedEntities)
            {
                if (lvt_4_1_.trackedEntity != lvt_2_1_)
                {
                    lvt_4_1_.updatePlayerEntity(lvt_2_1_);
                }
            }
        }
        else if (entityIn instanceof EntityFishHook)
        {
            this.addEntityToTracker(entityIn, 64, 5, true);
        }
        else if (entityIn instanceof EntityArrow)
        {
            this.addEntityToTracker(entityIn, 64, 20, false);
        }
        else if (entityIn instanceof EntitySmallFireball)
        {
            this.addEntityToTracker(entityIn, 64, 10, false);
        }
        else if (entityIn instanceof EntityFireball)
        {
            this.addEntityToTracker(entityIn, 64, 10, false);
        }
        else if (entityIn instanceof EntitySnowball)
        {
            this.addEntityToTracker(entityIn, 64, 10, true);
        }
        else if (entityIn instanceof EntityEnderPearl)
        {
            this.addEntityToTracker(entityIn, 64, 10, true);
        }
        else if (entityIn instanceof EntityEnderEye)
        {
            this.addEntityToTracker(entityIn, 64, 4, true);
        }
        else if (entityIn instanceof EntityEgg)
        {
            this.addEntityToTracker(entityIn, 64, 10, true);
        }
        else if (entityIn instanceof EntityPotion)
        {
            this.addEntityToTracker(entityIn, 64, 10, true);
        }
        else if (entityIn instanceof EntityExpBottle)
        {
            this.addEntityToTracker(entityIn, 64, 10, true);
        }
        else if (entityIn instanceof EntityFireworkRocket)
        {
            this.addEntityToTracker(entityIn, 64, 10, true);
        }
        else if (entityIn instanceof EntityItem)
        {
            this.addEntityToTracker(entityIn, 64, 20, true);
        }
        else if (entityIn instanceof EntityMinecart)
        {
            this.addEntityToTracker(entityIn, 80, 3, true);
        }
        else if (entityIn instanceof EntityBoat)
        {
            this.addEntityToTracker(entityIn, 80, 3, true);
        }
        else if (entityIn instanceof EntitySquid)
        {
            this.addEntityToTracker(entityIn, 64, 3, true);
        }
        else if (entityIn instanceof EntityWither)
        {
            this.addEntityToTracker(entityIn, 80, 3, false);
        }
        else if (entityIn instanceof EntityBat)
        {
            this.addEntityToTracker(entityIn, 80, 3, false);
        }
        else if (entityIn instanceof EntityDragon)
        {
            this.addEntityToTracker(entityIn, 160, 3, true);
        }
        else if (entityIn instanceof IAnimals)
        {
            this.addEntityToTracker(entityIn, 80, 3, true);
        }
        else if (entityIn instanceof EntityTNTPrimed)
        {
            this.addEntityToTracker(entityIn, 160, 10, true);
        }
        else if (entityIn instanceof EntityFallingBlock)
        {
            this.addEntityToTracker(entityIn, 160, 20, true);
        }
        else if (entityIn instanceof EntityHanging)
        {
            this.addEntityToTracker(entityIn, 160, Integer.MAX_VALUE, false);
        }
        else if (entityIn instanceof EntityArmorStand)
        {
            this.addEntityToTracker(entityIn, 160, 3, true);
        }
        else if (entityIn instanceof EntityXPOrb)
        {
            this.addEntityToTracker(entityIn, 160, 20, true);
        }
        else if (entityIn instanceof EntityEnderCrystal)
        {
            this.addEntityToTracker(entityIn, 256, Integer.MAX_VALUE, false);
        }
    }

    public void trackEntity(Entity entityIn, int trackingRange, int updateFrequency)
    {
        this.addEntityToTracker(entityIn, trackingRange, updateFrequency, false);
    }

    /**
     * Args : Entity, trackingRange, updateFrequency, sendVelocityUpdates
     */
    public void addEntityToTracker(Entity entityIn, int trackingRange, final int updateFrequency, boolean sendVelocityUpdates)
    {
        if (trackingRange > this.maxTrackingDistanceThreshold)
        {
            trackingRange = this.maxTrackingDistanceThreshold;
        }

        try
        {
            if (this.trackedEntityHashTable.containsItem(entityIn.getEntityId()))
            {
                throw new IllegalStateException("Entity is already tracked!");
            }

            EntityTrackerEntry lvt_5_1_ = new EntityTrackerEntry(entityIn, trackingRange, updateFrequency, sendVelocityUpdates);
            this.trackedEntities.add(lvt_5_1_);
            this.trackedEntityHashTable.addKey(entityIn.getEntityId(), lvt_5_1_);
            lvt_5_1_.updatePlayerEntities(this.theWorld.playerEntities);
        }
        catch (Throwable var11)
        {
            CrashReport lvt_6_1_ = CrashReport.makeCrashReport(var11, "Adding entity to track");
            CrashReportCategory lvt_7_1_ = lvt_6_1_.makeCategory("Entity To Track");
            lvt_7_1_.addCrashSection("Tracking range", trackingRange + " blocks");
            lvt_7_1_.addCrashSectionCallable("Update interval", new Callable<String>()
            {
                public String call() throws Exception
                {
                    String lvt_1_1_ = "Once per " + updateFrequency + " ticks";

                    if (updateFrequency == Integer.MAX_VALUE)
                    {
                        lvt_1_1_ = "Maximum (" + lvt_1_1_ + ")";
                    }

                    return lvt_1_1_;
                }
                public Object call() throws Exception
                {
                    return this.call();
                }
            });
            entityIn.addEntityCrashInfo(lvt_7_1_);
            CrashReportCategory lvt_8_1_ = lvt_6_1_.makeCategory("Entity That Is Already Tracked");
            ((EntityTrackerEntry)this.trackedEntityHashTable.lookup(entityIn.getEntityId())).trackedEntity.addEntityCrashInfo(lvt_8_1_);

            try
            {
                throw new ReportedException(lvt_6_1_);
            }
            catch (ReportedException var10)
            {
                logger.error("\"Silently\" catching entity tracking error.", var10);
            }
        }
    }

    public void untrackEntity(Entity entityIn)
    {
        if (entityIn instanceof EntityPlayerMP)
        {
            EntityPlayerMP lvt_2_1_ = (EntityPlayerMP)entityIn;

            for (EntityTrackerEntry lvt_4_1_ : this.trackedEntities)
            {
                lvt_4_1_.removeFromTrackedPlayers(lvt_2_1_);
            }
        }

        EntityTrackerEntry lvt_2_2_ = (EntityTrackerEntry)this.trackedEntityHashTable.removeObject(entityIn.getEntityId());

        if (lvt_2_2_ != null)
        {
            this.trackedEntities.remove(lvt_2_2_);
            lvt_2_2_.sendDestroyEntityPacketToTrackedPlayers();
        }
    }

    public void updateTrackedEntities()
    {
        List<EntityPlayerMP> lvt_1_1_ = Lists.newArrayList();

        for (EntityTrackerEntry lvt_3_1_ : this.trackedEntities)
        {
            lvt_3_1_.updatePlayerList(this.theWorld.playerEntities);

            if (lvt_3_1_.playerEntitiesUpdated && lvt_3_1_.trackedEntity instanceof EntityPlayerMP)
            {
                lvt_1_1_.add((EntityPlayerMP)lvt_3_1_.trackedEntity);
            }
        }

        for (int lvt_2_2_ = 0; lvt_2_2_ < ((List)lvt_1_1_).size(); ++lvt_2_2_)
        {
            EntityPlayerMP lvt_3_2_ = (EntityPlayerMP)lvt_1_1_.get(lvt_2_2_);

            for (EntityTrackerEntry lvt_5_1_ : this.trackedEntities)
            {
                if (lvt_5_1_.trackedEntity != lvt_3_2_)
                {
                    lvt_5_1_.updatePlayerEntity(lvt_3_2_);
                }
            }
        }
    }

    public void func_180245_a(EntityPlayerMP p_180245_1_)
    {
        for (EntityTrackerEntry lvt_3_1_ : this.trackedEntities)
        {
            if (lvt_3_1_.trackedEntity == p_180245_1_)
            {
                lvt_3_1_.updatePlayerEntities(this.theWorld.playerEntities);
            }
            else
            {
                lvt_3_1_.updatePlayerEntity(p_180245_1_);
            }
        }
    }

    public void sendToAllTrackingEntity(Entity entityIn, Packet p_151247_2_)
    {
        EntityTrackerEntry lvt_3_1_ = (EntityTrackerEntry)this.trackedEntityHashTable.lookup(entityIn.getEntityId());

        if (lvt_3_1_ != null)
        {
            lvt_3_1_.sendPacketToTrackedPlayers(p_151247_2_);
        }
    }

    public void func_151248_b(Entity entityIn, Packet p_151248_2_)
    {
        EntityTrackerEntry lvt_3_1_ = (EntityTrackerEntry)this.trackedEntityHashTable.lookup(entityIn.getEntityId());

        if (lvt_3_1_ != null)
        {
            lvt_3_1_.func_151261_b(p_151248_2_);
        }
    }

    public void removePlayerFromTrackers(EntityPlayerMP p_72787_1_)
    {
        for (EntityTrackerEntry lvt_3_1_ : this.trackedEntities)
        {
            lvt_3_1_.removeTrackedPlayerSymmetric(p_72787_1_);
        }
    }

    public void func_85172_a(EntityPlayerMP p_85172_1_, Chunk p_85172_2_)
    {
        for (EntityTrackerEntry lvt_4_1_ : this.trackedEntities)
        {
            if (lvt_4_1_.trackedEntity != p_85172_1_ && lvt_4_1_.trackedEntity.chunkCoordX == p_85172_2_.xPosition && lvt_4_1_.trackedEntity.chunkCoordZ == p_85172_2_.zPosition)
            {
                lvt_4_1_.updatePlayerEntity(p_85172_1_);
            }
        }
    }
}
