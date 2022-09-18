package net.minecraft.entity;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.HoverEvent;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class Entity implements ICommandSender
{
    private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    private static int nextEntityID;
    private int entityId;
    public double renderDistanceWeight;

    /**
     * Blocks entities from spawning when they do their AABB check to make sure the spot is clear of entities that can
     * prevent spawning.
     */
    public boolean preventEntitySpawning;

    /** The entity that is riding this entity */
    public Entity riddenByEntity;

    /** The entity we are currently riding */
    public Entity ridingEntity;
    public boolean forceSpawn;

    /** Reference to the World object. */
    public World worldObj;
    public double prevPosX;
    public double prevPosY;
    public double prevPosZ;

    /** Entity position X */
    public double posX;

    /** Entity position Y */
    public double posY;

    /** Entity position Z */
    public double posZ;

    /** Entity motion X */
    public double motionX;

    /** Entity motion Y */
    public double motionY;

    /** Entity motion Z */
    public double motionZ;

    /** Entity rotation Yaw */
    public float rotationYaw;

    /** Entity rotation Pitch */
    public float rotationPitch;
    public float prevRotationYaw;
    public float prevRotationPitch;

    /** Axis aligned bounding box. */
    private AxisAlignedBB boundingBox;
    public boolean onGround;

    /**
     * True if after a move this entity has collided with something on X- or Z-axis
     */
    public boolean isCollidedHorizontally;

    /**
     * True if after a move this entity has collided with something on Y-axis
     */
    public boolean isCollidedVertically;

    /**
     * True if after a move this entity has collided with something either vertically or horizontally
     */
    public boolean isCollided;
    public boolean velocityChanged;
    protected boolean isInWeb;
    private boolean isOutsideBorder;

    /**
     * gets set by setEntityDead, so this must be the flag whether an Entity is dead (inactive may be better term)
     */
    public boolean isDead;

    /** How wide this entity is considered to be */
    public float width;

    /** How high this entity is considered to be */
    public float height;

    /** The previous ticks distance walked multiplied by 0.6 */
    public float prevDistanceWalkedModified;

    /** The distance walked multiplied by 0.6 */
    public float distanceWalkedModified;
    public float distanceWalkedOnStepModified;
    public float fallDistance;

    /**
     * The distance that has to be exceeded in order to triger a new step sound and an onEntityWalking event on a block
     */
    private int nextStepDistance;

    /**
     * The entity's X coordinate at the previous tick, used to calculate position during rendering routines
     */
    public double lastTickPosX;

    /**
     * The entity's Y coordinate at the previous tick, used to calculate position during rendering routines
     */
    public double lastTickPosY;

    /**
     * The entity's Z coordinate at the previous tick, used to calculate position during rendering routines
     */
    public double lastTickPosZ;

    /**
     * How high this entity can step up when running into a block to try to get over it (currently make note the entity
     * will always step up this amount and not just the amount needed)
     */
    public float stepHeight;

    /**
     * Whether this entity won't clip with collision or not (make note it won't disable gravity)
     */
    public boolean noClip;

    /**
     * Reduces the velocity applied by entity collisions by the specified percent.
     */
    public float entityCollisionReduction;
    protected Random rand;

    /** How many ticks has this entity had ran since being alive */
    public int ticksExisted;

    /**
     * The amount of ticks you have to stand inside of fire before be set on fire
     */
    public int fireResistance;
    private int fire;

    /**
     * Whether this entity is currently inside of water (if it handles water movement that is)
     */
    protected boolean inWater;

    /**
     * Remaining time an entity will be "immune" to further damage after being hurt.
     */
    public int hurtResistantTime;
    protected boolean firstUpdate;
    protected boolean isImmuneToFire;
    protected DataWatcher dataWatcher;
    private double entityRiderPitchDelta;
    private double entityRiderYawDelta;

    /** Has this entity been added to the chunk its within */
    public boolean addedToChunk;
    public int chunkCoordX;
    public int chunkCoordY;
    public int chunkCoordZ;
    public int serverPosX;
    public int serverPosY;
    public int serverPosZ;

    /**
     * Render entity even if it is outside the camera frustum. Only true in EntityFish for now. Used in RenderGlobal:
     * render if ignoreFrustumCheck or in frustum.
     */
    public boolean ignoreFrustumCheck;
    public boolean isAirBorne;
    public int timeUntilPortal;

    /** Whether the entity is inside a Portal */
    protected boolean inPortal;
    protected int portalCounter;

    /** Which dimension the player is in (-1 = the Nether, 0 = normal world) */
    public int dimension;

    /** The position of the last portal the entity was in */
    protected BlockPos lastPortalPos;

    /**
     * A horizontal vector related to the position of the last portal the entity was in
     */
    protected Vec3 lastPortalVec;

    /**
     * A direction related to the position of the last portal the entity was in
     */
    protected EnumFacing teleportDirection;
    private boolean invulnerable;
    protected UUID entityUniqueID;

    /** The command result statistics for this Entity. */
    private final CommandResultStats cmdResultStats;

    public int getEntityId()
    {
        return this.entityId;
    }

    public void setEntityId(int id)
    {
        this.entityId = id;
    }

    /**
     * Called by the /kill command.
     */
    public void onKillCommand()
    {
        this.setDead();
    }

    public Entity(World worldIn)
    {
        this.entityId = nextEntityID++;
        this.renderDistanceWeight = 1.0D;
        this.boundingBox = ZERO_AABB;
        this.width = 0.6F;
        this.height = 1.8F;
        this.nextStepDistance = 1;
        this.rand = new Random();
        this.fireResistance = 1;
        this.firstUpdate = true;
        this.entityUniqueID = MathHelper.getRandomUuid(this.rand);
        this.cmdResultStats = new CommandResultStats();
        this.worldObj = worldIn;
        this.setPosition(0.0D, 0.0D, 0.0D);

        if (worldIn != null)
        {
            this.dimension = worldIn.provider.getDimensionId();
        }

        this.dataWatcher = new DataWatcher(this);
        this.dataWatcher.addObject(0, Byte.valueOf((byte)0));
        this.dataWatcher.addObject(1, Short.valueOf((short)300));
        this.dataWatcher.addObject(3, Byte.valueOf((byte)0));
        this.dataWatcher.addObject(2, "");
        this.dataWatcher.addObject(4, Byte.valueOf((byte)0));
        this.entityInit();
    }

    protected abstract void entityInit();

    public DataWatcher getDataWatcher()
    {
        return this.dataWatcher;
    }

    public boolean equals(Object p_equals_1_)
    {
        return p_equals_1_ instanceof Entity ? ((Entity)p_equals_1_).entityId == this.entityId : false;
    }

    public int hashCode()
    {
        return this.entityId;
    }

    /**
     * Keeps moving the entity up so it isn't colliding with blocks and other requirements for this entity to be spawned
     * (only actually used on players though its also on Entity)
     */
    protected void preparePlayerToSpawn()
    {
        if (this.worldObj != null)
        {
            while (this.posY > 0.0D && this.posY < 256.0D)
            {
                this.setPosition(this.posX, this.posY, this.posZ);

                if (this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty())
                {
                    break;
                }

                ++this.posY;
            }

            this.motionX = this.motionY = this.motionZ = 0.0D;
            this.rotationPitch = 0.0F;
        }
    }

    /**
     * Will get destroyed next tick.
     */
    public void setDead()
    {
        this.isDead = true;
    }

    /**
     * Sets the width and height of the entity. Args: width, height
     */
    protected void setSize(float width, float height)
    {
        if (width != this.width || height != this.height)
        {
            float lvt_3_1_ = this.width;
            this.width = width;
            this.height = height;
            this.setEntityBoundingBox(new AxisAlignedBB(this.getEntityBoundingBox().minX, this.getEntityBoundingBox().minY, this.getEntityBoundingBox().minZ, this.getEntityBoundingBox().minX + (double)this.width, this.getEntityBoundingBox().minY + (double)this.height, this.getEntityBoundingBox().minZ + (double)this.width));

            if (this.width > lvt_3_1_ && !this.firstUpdate && !this.worldObj.isRemote)
            {
                this.moveEntity((double)(lvt_3_1_ - this.width), 0.0D, (double)(lvt_3_1_ - this.width));
            }
        }
    }

    /**
     * Sets the rotation of the entity. Args: yaw, pitch (both in degrees)
     */
    protected void setRotation(float yaw, float pitch)
    {
        this.rotationYaw = yaw % 360.0F;
        this.rotationPitch = pitch % 360.0F;
    }

    /**
     * Sets the x,y,z of the entity from the given parameters. Also seems to set up a bounding box.
     */
    public void setPosition(double x, double y, double z)
    {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        float lvt_7_1_ = this.width / 2.0F;
        float lvt_8_1_ = this.height;
        this.setEntityBoundingBox(new AxisAlignedBB(x - (double)lvt_7_1_, y, z - (double)lvt_7_1_, x + (double)lvt_7_1_, y + (double)lvt_8_1_, z + (double)lvt_7_1_));
    }

    /**
     * Adds 15% to the entity's yaw and subtracts 15% from the pitch. Clamps pitch from -90 to 90. Both arguments in
     * degrees.
     */
    public void setAngles(float yaw, float pitch)
    {
        float lvt_3_1_ = this.rotationPitch;
        float lvt_4_1_ = this.rotationYaw;
        this.rotationYaw = (float)((double)this.rotationYaw + (double)yaw * 0.15D);
        this.rotationPitch = (float)((double)this.rotationPitch - (double)pitch * 0.15D);
        this.rotationPitch = MathHelper.clamp_float(this.rotationPitch, -90.0F, 90.0F);
        this.prevRotationPitch += this.rotationPitch - lvt_3_1_;
        this.prevRotationYaw += this.rotationYaw - lvt_4_1_;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.onEntityUpdate();
    }

    /**
     * Gets called every tick from main Entity class
     */
    public void onEntityUpdate()
    {
        this.worldObj.theProfiler.startSection("entityBaseTick");

        if (this.ridingEntity != null && this.ridingEntity.isDead)
        {
            this.ridingEntity = null;
        }

        this.prevDistanceWalkedModified = this.distanceWalkedModified;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.prevRotationPitch = this.rotationPitch;
        this.prevRotationYaw = this.rotationYaw;

        if (!this.worldObj.isRemote && this.worldObj instanceof WorldServer)
        {
            this.worldObj.theProfiler.startSection("portal");
            MinecraftServer lvt_1_1_ = ((WorldServer)this.worldObj).getMinecraftServer();
            int lvt_2_1_ = this.getMaxInPortalTime();

            if (this.inPortal)
            {
                if (lvt_1_1_.getAllowNether())
                {
                    if (this.ridingEntity == null && this.portalCounter++ >= lvt_2_1_)
                    {
                        this.portalCounter = lvt_2_1_;
                        this.timeUntilPortal = this.getPortalCooldown();
                        int lvt_3_1_;

                        if (this.worldObj.provider.getDimensionId() == -1)
                        {
                            lvt_3_1_ = 0;
                        }
                        else
                        {
                            lvt_3_1_ = -1;
                        }

                        this.travelToDimension(lvt_3_1_);
                    }

                    this.inPortal = false;
                }
            }
            else
            {
                if (this.portalCounter > 0)
                {
                    this.portalCounter -= 4;
                }

                if (this.portalCounter < 0)
                {
                    this.portalCounter = 0;
                }
            }

            if (this.timeUntilPortal > 0)
            {
                --this.timeUntilPortal;
            }

            this.worldObj.theProfiler.endSection();
        }

        this.spawnRunningParticles();
        this.handleWaterMovement();

        if (this.worldObj.isRemote)
        {
            this.fire = 0;
        }
        else if (this.fire > 0)
        {
            if (this.isImmuneToFire)
            {
                this.fire -= 4;

                if (this.fire < 0)
                {
                    this.fire = 0;
                }
            }
            else
            {
                if (this.fire % 20 == 0)
                {
                    this.attackEntityFrom(DamageSource.onFire, 1.0F);
                }

                --this.fire;
            }
        }

        if (this.isInLava())
        {
            this.setOnFireFromLava();
            this.fallDistance *= 0.5F;
        }

        if (this.posY < -64.0D)
        {
            this.kill();
        }

        if (!this.worldObj.isRemote)
        {
            this.setFlag(0, this.fire > 0);
        }

        this.firstUpdate = false;
        this.worldObj.theProfiler.endSection();
    }

    /**
     * Return the amount of time this entity should stay in a portal before being transported.
     */
    public int getMaxInPortalTime()
    {
        return 0;
    }

    /**
     * Called whenever the entity is walking inside of lava.
     */
    protected void setOnFireFromLava()
    {
        if (!this.isImmuneToFire)
        {
            this.attackEntityFrom(DamageSource.lava, 4.0F);
            this.setFire(15);
        }
    }

    /**
     * Sets entity to burn for x amount of seconds, cannot lower amount of existing fire.
     */
    public void setFire(int seconds)
    {
        int lvt_2_1_ = seconds * 20;
        lvt_2_1_ = EnchantmentProtection.getFireTimeForEntity(this, lvt_2_1_);

        if (this.fire < lvt_2_1_)
        {
            this.fire = lvt_2_1_;
        }
    }

    /**
     * Removes fire from entity.
     */
    public void extinguish()
    {
        this.fire = 0;
    }

    /**
     * sets the dead flag. Used when you fall off the bottom of the world.
     */
    protected void kill()
    {
        this.setDead();
    }

    /**
     * Checks if the offset position from the entity's current position is inside of liquid. Args: x, y, z
     */
    public boolean isOffsetPositionInLiquid(double x, double y, double z)
    {
        AxisAlignedBB lvt_7_1_ = this.getEntityBoundingBox().offset(x, y, z);
        return this.isLiquidPresentInAABB(lvt_7_1_);
    }

    /**
     * Determines if a liquid is present within the specified AxisAlignedBB.
     */
    private boolean isLiquidPresentInAABB(AxisAlignedBB bb)
    {
        return this.worldObj.getCollidingBoundingBoxes(this, bb).isEmpty() && !this.worldObj.isAnyLiquid(bb);
    }

    /**
     * Tries to moves the entity by the passed in displacement. Args: x, y, z
     */
    public void moveEntity(double x, double y, double z)
    {
        if (this.noClip)
        {
            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, y, z));
            this.resetPositionToBB();
        }
        else
        {
            this.worldObj.theProfiler.startSection("move");
            double lvt_7_1_ = this.posX;
            double lvt_9_1_ = this.posY;
            double lvt_11_1_ = this.posZ;

            if (this.isInWeb)
            {
                this.isInWeb = false;
                x *= 0.25D;
                y *= 0.05000000074505806D;
                z *= 0.25D;
                this.motionX = 0.0D;
                this.motionY = 0.0D;
                this.motionZ = 0.0D;
            }

            double lvt_13_1_ = x;
            double lvt_15_1_ = y;
            double lvt_17_1_ = z;
            boolean lvt_19_1_ = this.onGround && this.isSneaking() && this instanceof EntityPlayer;

            if (lvt_19_1_)
            {
                double lvt_20_1_;

                for (lvt_20_1_ = 0.05D; x != 0.0D && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().offset(x, -1.0D, 0.0D)).isEmpty(); lvt_13_1_ = x)
                {
                    if (x < lvt_20_1_ && x >= -lvt_20_1_)
                    {
                        x = 0.0D;
                    }
                    else if (x > 0.0D)
                    {
                        x -= lvt_20_1_;
                    }
                    else
                    {
                        x += lvt_20_1_;
                    }
                }

                for (; z != 0.0D && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().offset(0.0D, -1.0D, z)).isEmpty(); lvt_17_1_ = z)
                {
                    if (z < lvt_20_1_ && z >= -lvt_20_1_)
                    {
                        z = 0.0D;
                    }
                    else if (z > 0.0D)
                    {
                        z -= lvt_20_1_;
                    }
                    else
                    {
                        z += lvt_20_1_;
                    }
                }

                for (; x != 0.0D && z != 0.0D && this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().offset(x, -1.0D, z)).isEmpty(); lvt_17_1_ = z)
                {
                    if (x < lvt_20_1_ && x >= -lvt_20_1_)
                    {
                        x = 0.0D;
                    }
                    else if (x > 0.0D)
                    {
                        x -= lvt_20_1_;
                    }
                    else
                    {
                        x += lvt_20_1_;
                    }

                    lvt_13_1_ = x;

                    if (z < lvt_20_1_ && z >= -lvt_20_1_)
                    {
                        z = 0.0D;
                    }
                    else if (z > 0.0D)
                    {
                        z -= lvt_20_1_;
                    }
                    else
                    {
                        z += lvt_20_1_;
                    }
                }
            }

            List<AxisAlignedBB> lvt_20_2_ = this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().addCoord(x, y, z));
            AxisAlignedBB lvt_21_1_ = this.getEntityBoundingBox();

            for (AxisAlignedBB lvt_23_1_ : lvt_20_2_)
            {
                y = lvt_23_1_.calculateYOffset(this.getEntityBoundingBox(), y);
            }

            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, y, 0.0D));
            boolean lvt_22_2_ = this.onGround || lvt_15_1_ != y && lvt_15_1_ < 0.0D;

            for (AxisAlignedBB lvt_24_1_ : lvt_20_2_)
            {
                x = lvt_24_1_.calculateXOffset(this.getEntityBoundingBox(), x);
            }

            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, 0.0D, 0.0D));

            for (AxisAlignedBB lvt_24_2_ : lvt_20_2_)
            {
                z = lvt_24_2_.calculateZOffset(this.getEntityBoundingBox(), z);
            }

            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, 0.0D, z));

            if (this.stepHeight > 0.0F && lvt_22_2_ && (lvt_13_1_ != x || lvt_17_1_ != z))
            {
                double lvt_23_4_ = x;
                double lvt_25_1_ = y;
                double lvt_27_1_ = z;
                AxisAlignedBB lvt_29_1_ = this.getEntityBoundingBox();
                this.setEntityBoundingBox(lvt_21_1_);
                y = (double)this.stepHeight;
                List<AxisAlignedBB> lvt_30_1_ = this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().addCoord(lvt_13_1_, y, lvt_17_1_));
                AxisAlignedBB lvt_31_1_ = this.getEntityBoundingBox();
                AxisAlignedBB lvt_32_1_ = lvt_31_1_.addCoord(lvt_13_1_, 0.0D, lvt_17_1_);
                double lvt_33_1_ = y;

                for (AxisAlignedBB lvt_36_1_ : lvt_30_1_)
                {
                    lvt_33_1_ = lvt_36_1_.calculateYOffset(lvt_32_1_, lvt_33_1_);
                }

                lvt_31_1_ = lvt_31_1_.offset(0.0D, lvt_33_1_, 0.0D);
                double lvt_35_2_ = lvt_13_1_;

                for (AxisAlignedBB lvt_38_1_ : lvt_30_1_)
                {
                    lvt_35_2_ = lvt_38_1_.calculateXOffset(lvt_31_1_, lvt_35_2_);
                }

                lvt_31_1_ = lvt_31_1_.offset(lvt_35_2_, 0.0D, 0.0D);
                double lvt_37_2_ = lvt_17_1_;

                for (AxisAlignedBB lvt_40_1_ : lvt_30_1_)
                {
                    lvt_37_2_ = lvt_40_1_.calculateZOffset(lvt_31_1_, lvt_37_2_);
                }

                lvt_31_1_ = lvt_31_1_.offset(0.0D, 0.0D, lvt_37_2_);
                AxisAlignedBB lvt_39_2_ = this.getEntityBoundingBox();
                double lvt_40_2_ = y;

                for (AxisAlignedBB lvt_43_1_ : lvt_30_1_)
                {
                    lvt_40_2_ = lvt_43_1_.calculateYOffset(lvt_39_2_, lvt_40_2_);
                }

                lvt_39_2_ = lvt_39_2_.offset(0.0D, lvt_40_2_, 0.0D);
                double lvt_42_2_ = lvt_13_1_;

                for (AxisAlignedBB lvt_45_1_ : lvt_30_1_)
                {
                    lvt_42_2_ = lvt_45_1_.calculateXOffset(lvt_39_2_, lvt_42_2_);
                }

                lvt_39_2_ = lvt_39_2_.offset(lvt_42_2_, 0.0D, 0.0D);
                double lvt_44_2_ = lvt_17_1_;

                for (AxisAlignedBB lvt_47_1_ : lvt_30_1_)
                {
                    lvt_44_2_ = lvt_47_1_.calculateZOffset(lvt_39_2_, lvt_44_2_);
                }

                lvt_39_2_ = lvt_39_2_.offset(0.0D, 0.0D, lvt_44_2_);
                double lvt_46_2_ = lvt_35_2_ * lvt_35_2_ + lvt_37_2_ * lvt_37_2_;
                double lvt_48_1_ = lvt_42_2_ * lvt_42_2_ + lvt_44_2_ * lvt_44_2_;

                if (lvt_46_2_ > lvt_48_1_)
                {
                    x = lvt_35_2_;
                    z = lvt_37_2_;
                    y = -lvt_33_1_;
                    this.setEntityBoundingBox(lvt_31_1_);
                }
                else
                {
                    x = lvt_42_2_;
                    z = lvt_44_2_;
                    y = -lvt_40_2_;
                    this.setEntityBoundingBox(lvt_39_2_);
                }

                for (AxisAlignedBB lvt_51_1_ : lvt_30_1_)
                {
                    y = lvt_51_1_.calculateYOffset(this.getEntityBoundingBox(), y);
                }

                this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, y, 0.0D));

                if (lvt_23_4_ * lvt_23_4_ + lvt_27_1_ * lvt_27_1_ >= x * x + z * z)
                {
                    x = lvt_23_4_;
                    y = lvt_25_1_;
                    z = lvt_27_1_;
                    this.setEntityBoundingBox(lvt_29_1_);
                }
            }

            this.worldObj.theProfiler.endSection();
            this.worldObj.theProfiler.startSection("rest");
            this.resetPositionToBB();
            this.isCollidedHorizontally = lvt_13_1_ != x || lvt_17_1_ != z;
            this.isCollidedVertically = lvt_15_1_ != y;
            this.onGround = this.isCollidedVertically && lvt_15_1_ < 0.0D;
            this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;
            int lvt_23_5_ = MathHelper.floor_double(this.posX);
            int lvt_24_3_ = MathHelper.floor_double(this.posY - 0.20000000298023224D);
            int lvt_25_2_ = MathHelper.floor_double(this.posZ);
            BlockPos lvt_26_1_ = new BlockPos(lvt_23_5_, lvt_24_3_, lvt_25_2_);
            Block lvt_27_2_ = this.worldObj.getBlockState(lvt_26_1_).getBlock();

            if (lvt_27_2_.getMaterial() == Material.air)
            {
                Block lvt_28_1_ = this.worldObj.getBlockState(lvt_26_1_.down()).getBlock();

                if (lvt_28_1_ instanceof BlockFence || lvt_28_1_ instanceof BlockWall || lvt_28_1_ instanceof BlockFenceGate)
                {
                    lvt_27_2_ = lvt_28_1_;
                    lvt_26_1_ = lvt_26_1_.down();
                }
            }

            this.updateFallState(y, this.onGround, lvt_27_2_, lvt_26_1_);

            if (lvt_13_1_ != x)
            {
                this.motionX = 0.0D;
            }

            if (lvt_17_1_ != z)
            {
                this.motionZ = 0.0D;
            }

            if (lvt_15_1_ != y)
            {
                lvt_27_2_.onLanded(this.worldObj, this);
            }

            if (this.canTriggerWalking() && !lvt_19_1_ && this.ridingEntity == null)
            {
                double lvt_28_2_ = this.posX - lvt_7_1_;
                double lvt_30_2_ = this.posY - lvt_9_1_;
                double lvt_32_2_ = this.posZ - lvt_11_1_;

                if (lvt_27_2_ != Blocks.ladder)
                {
                    lvt_30_2_ = 0.0D;
                }

                if (lvt_27_2_ != null && this.onGround)
                {
                    lvt_27_2_.onEntityCollidedWithBlock(this.worldObj, lvt_26_1_, this);
                }

                this.distanceWalkedModified = (float)((double)this.distanceWalkedModified + (double)MathHelper.sqrt_double(lvt_28_2_ * lvt_28_2_ + lvt_32_2_ * lvt_32_2_) * 0.6D);
                this.distanceWalkedOnStepModified = (float)((double)this.distanceWalkedOnStepModified + (double)MathHelper.sqrt_double(lvt_28_2_ * lvt_28_2_ + lvt_30_2_ * lvt_30_2_ + lvt_32_2_ * lvt_32_2_) * 0.6D);

                if (this.distanceWalkedOnStepModified > (float)this.nextStepDistance && lvt_27_2_.getMaterial() != Material.air)
                {
                    this.nextStepDistance = (int)this.distanceWalkedOnStepModified + 1;

                    if (this.isInWater())
                    {
                        float lvt_34_1_ = MathHelper.sqrt_double(this.motionX * this.motionX * 0.20000000298023224D + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.20000000298023224D) * 0.35F;

                        if (lvt_34_1_ > 1.0F)
                        {
                            lvt_34_1_ = 1.0F;
                        }

                        this.playSound(this.getSwimSound(), lvt_34_1_, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                    }

                    this.playStepSound(lvt_26_1_, lvt_27_2_);
                }
            }

            try
            {
                this.doBlockCollisions();
            }
            catch (Throwable var52)
            {
                CrashReport lvt_29_2_ = CrashReport.makeCrashReport(var52, "Checking entity block collision");
                CrashReportCategory lvt_30_3_ = lvt_29_2_.makeCategory("Entity being checked for collision");
                this.addEntityCrashInfo(lvt_30_3_);
                throw new ReportedException(lvt_29_2_);
            }

            boolean lvt_28_4_ = this.isWet();

            if (this.worldObj.isFlammableWithin(this.getEntityBoundingBox().contract(0.001D, 0.001D, 0.001D)))
            {
                this.dealFireDamage(1);

                if (!lvt_28_4_)
                {
                    ++this.fire;

                    if (this.fire == 0)
                    {
                        this.setFire(8);
                    }
                }
            }
            else if (this.fire <= 0)
            {
                this.fire = -this.fireResistance;
            }

            if (lvt_28_4_ && this.fire > 0)
            {
                this.playSound("random.fizz", 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                this.fire = -this.fireResistance;
            }

            this.worldObj.theProfiler.endSection();
        }
    }

    /**
     * Resets the entity's position to the center (planar) and bottom (vertical) points of its bounding box.
     */
    private void resetPositionToBB()
    {
        this.posX = (this.getEntityBoundingBox().minX + this.getEntityBoundingBox().maxX) / 2.0D;
        this.posY = this.getEntityBoundingBox().minY;
        this.posZ = (this.getEntityBoundingBox().minZ + this.getEntityBoundingBox().maxZ) / 2.0D;
    }

    protected String getSwimSound()
    {
        return "game.neutral.swim";
    }

    protected void doBlockCollisions()
    {
        BlockPos lvt_1_1_ = new BlockPos(this.getEntityBoundingBox().minX + 0.001D, this.getEntityBoundingBox().minY + 0.001D, this.getEntityBoundingBox().minZ + 0.001D);
        BlockPos lvt_2_1_ = new BlockPos(this.getEntityBoundingBox().maxX - 0.001D, this.getEntityBoundingBox().maxY - 0.001D, this.getEntityBoundingBox().maxZ - 0.001D);

        if (this.worldObj.isAreaLoaded(lvt_1_1_, lvt_2_1_))
        {
            for (int lvt_3_1_ = lvt_1_1_.getX(); lvt_3_1_ <= lvt_2_1_.getX(); ++lvt_3_1_)
            {
                for (int lvt_4_1_ = lvt_1_1_.getY(); lvt_4_1_ <= lvt_2_1_.getY(); ++lvt_4_1_)
                {
                    for (int lvt_5_1_ = lvt_1_1_.getZ(); lvt_5_1_ <= lvt_2_1_.getZ(); ++lvt_5_1_)
                    {
                        BlockPos lvt_6_1_ = new BlockPos(lvt_3_1_, lvt_4_1_, lvt_5_1_);
                        IBlockState lvt_7_1_ = this.worldObj.getBlockState(lvt_6_1_);

                        try
                        {
                            lvt_7_1_.getBlock().onEntityCollidedWithBlock(this.worldObj, lvt_6_1_, lvt_7_1_, this);
                        }
                        catch (Throwable var11)
                        {
                            CrashReport lvt_9_1_ = CrashReport.makeCrashReport(var11, "Colliding entity with block");
                            CrashReportCategory lvt_10_1_ = lvt_9_1_.makeCategory("Block being collided with");
                            CrashReportCategory.addBlockInfo(lvt_10_1_, lvt_6_1_, lvt_7_1_);
                            throw new ReportedException(lvt_9_1_);
                        }
                    }
                }
            }
        }
    }

    protected void playStepSound(BlockPos pos, Block blockIn)
    {
        Block.SoundType lvt_3_1_ = blockIn.stepSound;

        if (this.worldObj.getBlockState(pos.up()).getBlock() == Blocks.snow_layer)
        {
            lvt_3_1_ = Blocks.snow_layer.stepSound;
            this.playSound(lvt_3_1_.getStepSound(), lvt_3_1_.getVolume() * 0.15F, lvt_3_1_.getFrequency());
        }
        else if (!blockIn.getMaterial().isLiquid())
        {
            this.playSound(lvt_3_1_.getStepSound(), lvt_3_1_.getVolume() * 0.15F, lvt_3_1_.getFrequency());
        }
    }

    public void playSound(String name, float volume, float pitch)
    {
        if (!this.isSilent())
        {
            this.worldObj.playSoundAtEntity(this, name, volume, pitch);
        }
    }

    /**
     * @return True if this entity will not play sounds
     */
    public boolean isSilent()
    {
        return this.dataWatcher.getWatchableObjectByte(4) == 1;
    }

    /**
     * When set to true the entity will not play sounds.
     */
    public void setSilent(boolean isSilent)
    {
        this.dataWatcher.updateObject(4, Byte.valueOf((byte)(isSilent ? 1 : 0)));
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return true;
    }

    protected void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos)
    {
        if (onGroundIn)
        {
            if (this.fallDistance > 0.0F)
            {
                if (blockIn != null)
                {
                    blockIn.onFallenUpon(this.worldObj, pos, this, this.fallDistance);
                }
                else
                {
                    this.fall(this.fallDistance, 1.0F);
                }

                this.fallDistance = 0.0F;
            }
        }
        else if (y < 0.0D)
        {
            this.fallDistance = (float)((double)this.fallDistance - y);
        }
    }

    /**
     * Returns the collision bounding box for this entity
     */
    public AxisAlignedBB getCollisionBoundingBox()
    {
        return null;
    }

    /**
     * Will deal the specified amount of damage to the entity if the entity isn't immune to fire damage. Args:
     * amountDamage
     */
    protected void dealFireDamage(int amount)
    {
        if (!this.isImmuneToFire)
        {
            this.attackEntityFrom(DamageSource.inFire, (float)amount);
        }
    }

    public final boolean isImmuneToFire()
    {
        return this.isImmuneToFire;
    }

    public void fall(float distance, float damageMultiplier)
    {
        if (this.riddenByEntity != null)
        {
            this.riddenByEntity.fall(distance, damageMultiplier);
        }
    }

    /**
     * Checks if this entity is either in water or on an open air block in rain (used in wolves).
     */
    public boolean isWet()
    {
        return this.inWater || this.worldObj.isRainingAt(new BlockPos(this.posX, this.posY, this.posZ)) || this.worldObj.isRainingAt(new BlockPos(this.posX, this.posY + (double)this.height, this.posZ));
    }

    /**
     * Checks if this entity is inside water (if inWater field is true as a result of handleWaterMovement() returning
     * true)
     */
    public boolean isInWater()
    {
        return this.inWater;
    }

    /**
     * Returns if this entity is in water and will end up adding the waters velocity to the entity
     */
    public boolean handleWaterMovement()
    {
        if (this.worldObj.handleMaterialAcceleration(this.getEntityBoundingBox().expand(0.0D, -0.4000000059604645D, 0.0D).contract(0.001D, 0.001D, 0.001D), Material.water, this))
        {
            if (!this.inWater && !this.firstUpdate)
            {
                this.resetHeight();
            }

            this.fallDistance = 0.0F;
            this.inWater = true;
            this.fire = 0;
        }
        else
        {
            this.inWater = false;
        }

        return this.inWater;
    }

    /**
     * sets the players height back to normal after doing things like sleeping and dieing
     */
    protected void resetHeight()
    {
        float lvt_1_1_ = MathHelper.sqrt_double(this.motionX * this.motionX * 0.20000000298023224D + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.20000000298023224D) * 0.2F;

        if (lvt_1_1_ > 1.0F)
        {
            lvt_1_1_ = 1.0F;
        }

        this.playSound(this.getSplashSound(), lvt_1_1_, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
        float lvt_2_1_ = (float)MathHelper.floor_double(this.getEntityBoundingBox().minY);

        for (int lvt_3_1_ = 0; (float)lvt_3_1_ < 1.0F + this.width * 20.0F; ++lvt_3_1_)
        {
            float lvt_4_1_ = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
            float lvt_5_1_ = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
            this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX + (double)lvt_4_1_, (double)(lvt_2_1_ + 1.0F), this.posZ + (double)lvt_5_1_, this.motionX, this.motionY - (double)(this.rand.nextFloat() * 0.2F), this.motionZ, new int[0]);
        }

        for (int lvt_3_2_ = 0; (float)lvt_3_2_ < 1.0F + this.width * 20.0F; ++lvt_3_2_)
        {
            float lvt_4_2_ = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
            float lvt_5_2_ = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
            this.worldObj.spawnParticle(EnumParticleTypes.WATER_SPLASH, this.posX + (double)lvt_4_2_, (double)(lvt_2_1_ + 1.0F), this.posZ + (double)lvt_5_2_, this.motionX, this.motionY, this.motionZ, new int[0]);
        }
    }

    /**
     * Attempts to create sprinting particles if the entity is sprinting and not in water.
     */
    public void spawnRunningParticles()
    {
        if (this.isSprinting() && !this.isInWater())
        {
            this.createRunningParticles();
        }
    }

    protected void createRunningParticles()
    {
        int lvt_1_1_ = MathHelper.floor_double(this.posX);
        int lvt_2_1_ = MathHelper.floor_double(this.posY - 0.20000000298023224D);
        int lvt_3_1_ = MathHelper.floor_double(this.posZ);
        BlockPos lvt_4_1_ = new BlockPos(lvt_1_1_, lvt_2_1_, lvt_3_1_);
        IBlockState lvt_5_1_ = this.worldObj.getBlockState(lvt_4_1_);
        Block lvt_6_1_ = lvt_5_1_.getBlock();

        if (lvt_6_1_.getRenderType() != -1)
        {
            this.worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, this.posX + ((double)this.rand.nextFloat() - 0.5D) * (double)this.width, this.getEntityBoundingBox().minY + 0.1D, this.posZ + ((double)this.rand.nextFloat() - 0.5D) * (double)this.width, -this.motionX * 4.0D, 1.5D, -this.motionZ * 4.0D, new int[] {Block.getStateId(lvt_5_1_)});
        }
    }

    protected String getSplashSound()
    {
        return "game.neutral.swim.splash";
    }

    /**
     * Checks if the current block the entity is within of the specified material type
     */
    public boolean isInsideOfMaterial(Material materialIn)
    {
        double lvt_2_1_ = this.posY + (double)this.getEyeHeight();
        BlockPos lvt_4_1_ = new BlockPos(this.posX, lvt_2_1_, this.posZ);
        IBlockState lvt_5_1_ = this.worldObj.getBlockState(lvt_4_1_);
        Block lvt_6_1_ = lvt_5_1_.getBlock();

        if (lvt_6_1_.getMaterial() == materialIn)
        {
            float lvt_7_1_ = BlockLiquid.getLiquidHeightPercent(lvt_5_1_.getBlock().getMetaFromState(lvt_5_1_)) - 0.11111111F;
            float lvt_8_1_ = (float)(lvt_4_1_.getY() + 1) - lvt_7_1_;
            boolean lvt_9_1_ = lvt_2_1_ < (double)lvt_8_1_;
            return !lvt_9_1_ && this instanceof EntityPlayer ? false : lvt_9_1_;
        }
        else
        {
            return false;
        }
    }

    public boolean isInLava()
    {
        return this.worldObj.isMaterialInBB(this.getEntityBoundingBox().expand(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), Material.lava);
    }

    /**
     * Used in both water and by flying objects
     */
    public void moveFlying(float strafe, float forward, float friction)
    {
        float lvt_4_1_ = strafe * strafe + forward * forward;

        if (lvt_4_1_ >= 1.0E-4F)
        {
            lvt_4_1_ = MathHelper.sqrt_float(lvt_4_1_);

            if (lvt_4_1_ < 1.0F)
            {
                lvt_4_1_ = 1.0F;
            }

            lvt_4_1_ = friction / lvt_4_1_;
            strafe = strafe * lvt_4_1_;
            forward = forward * lvt_4_1_;
            float lvt_5_1_ = MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F);
            float lvt_6_1_ = MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F);
            this.motionX += (double)(strafe * lvt_6_1_ - forward * lvt_5_1_);
            this.motionZ += (double)(forward * lvt_6_1_ + strafe * lvt_5_1_);
        }
    }

    public int getBrightnessForRender(float partialTicks)
    {
        BlockPos lvt_2_1_ = new BlockPos(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
        return this.worldObj.isBlockLoaded(lvt_2_1_) ? this.worldObj.getCombinedLight(lvt_2_1_, 0) : 0;
    }

    /**
     * Gets how bright this entity is.
     */
    public float getBrightness(float partialTicks)
    {
        BlockPos lvt_2_1_ = new BlockPos(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
        return this.worldObj.isBlockLoaded(lvt_2_1_) ? this.worldObj.getLightBrightness(lvt_2_1_) : 0.0F;
    }

    /**
     * Sets the reference to the World object.
     */
    public void setWorld(World worldIn)
    {
        this.worldObj = worldIn;
    }

    /**
     * Sets the entity's position and rotation.
     */
    public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch)
    {
        this.prevPosX = this.posX = x;
        this.prevPosY = this.posY = y;
        this.prevPosZ = this.posZ = z;
        this.prevRotationYaw = this.rotationYaw = yaw;
        this.prevRotationPitch = this.rotationPitch = pitch;
        double lvt_9_1_ = (double)(this.prevRotationYaw - yaw);

        if (lvt_9_1_ < -180.0D)
        {
            this.prevRotationYaw += 360.0F;
        }

        if (lvt_9_1_ >= 180.0D)
        {
            this.prevRotationYaw -= 360.0F;
        }

        this.setPosition(this.posX, this.posY, this.posZ);
        this.setRotation(yaw, pitch);
    }

    public void moveToBlockPosAndAngles(BlockPos pos, float rotationYawIn, float rotationPitchIn)
    {
        this.setLocationAndAngles((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, rotationYawIn, rotationPitchIn);
    }

    /**
     * Sets the location and Yaw/Pitch of an entity in the world
     */
    public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch)
    {
        this.lastTickPosX = this.prevPosX = this.posX = x;
        this.lastTickPosY = this.prevPosY = this.posY = y;
        this.lastTickPosZ = this.prevPosZ = this.posZ = z;
        this.rotationYaw = yaw;
        this.rotationPitch = pitch;
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    /**
     * Returns the distance to the entity. Args: entity
     */
    public float getDistanceToEntity(Entity entityIn)
    {
        float lvt_2_1_ = (float)(this.posX - entityIn.posX);
        float lvt_3_1_ = (float)(this.posY - entityIn.posY);
        float lvt_4_1_ = (float)(this.posZ - entityIn.posZ);
        return MathHelper.sqrt_float(lvt_2_1_ * lvt_2_1_ + lvt_3_1_ * lvt_3_1_ + lvt_4_1_ * lvt_4_1_);
    }

    /**
     * Gets the squared distance to the position. Args: x, y, z
     */
    public double getDistanceSq(double x, double y, double z)
    {
        double lvt_7_1_ = this.posX - x;
        double lvt_9_1_ = this.posY - y;
        double lvt_11_1_ = this.posZ - z;
        return lvt_7_1_ * lvt_7_1_ + lvt_9_1_ * lvt_9_1_ + lvt_11_1_ * lvt_11_1_;
    }

    public double getDistanceSq(BlockPos pos)
    {
        return pos.distanceSq(this.posX, this.posY, this.posZ);
    }

    public double getDistanceSqToCenter(BlockPos pos)
    {
        return pos.distanceSqToCenter(this.posX, this.posY, this.posZ);
    }

    /**
     * Gets the distance to the position. Args: x, y, z
     */
    public double getDistance(double x, double y, double z)
    {
        double lvt_7_1_ = this.posX - x;
        double lvt_9_1_ = this.posY - y;
        double lvt_11_1_ = this.posZ - z;
        return (double)MathHelper.sqrt_double(lvt_7_1_ * lvt_7_1_ + lvt_9_1_ * lvt_9_1_ + lvt_11_1_ * lvt_11_1_);
    }

    /**
     * Returns the squared distance to the entity. Args: entity
     */
    public double getDistanceSqToEntity(Entity entityIn)
    {
        double lvt_2_1_ = this.posX - entityIn.posX;
        double lvt_4_1_ = this.posY - entityIn.posY;
        double lvt_6_1_ = this.posZ - entityIn.posZ;
        return lvt_2_1_ * lvt_2_1_ + lvt_4_1_ * lvt_4_1_ + lvt_6_1_ * lvt_6_1_;
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    public void onCollideWithPlayer(EntityPlayer entityIn)
    {
    }

    /**
     * Applies a velocity to each of the entities pushing them away from each other. Args: entity
     */
    public void applyEntityCollision(Entity entityIn)
    {
        if (entityIn.riddenByEntity != this && entityIn.ridingEntity != this)
        {
            if (!entityIn.noClip && !this.noClip)
            {
                double lvt_2_1_ = entityIn.posX - this.posX;
                double lvt_4_1_ = entityIn.posZ - this.posZ;
                double lvt_6_1_ = MathHelper.abs_max(lvt_2_1_, lvt_4_1_);

                if (lvt_6_1_ >= 0.009999999776482582D)
                {
                    lvt_6_1_ = (double)MathHelper.sqrt_double(lvt_6_1_);
                    lvt_2_1_ = lvt_2_1_ / lvt_6_1_;
                    lvt_4_1_ = lvt_4_1_ / lvt_6_1_;
                    double lvt_8_1_ = 1.0D / lvt_6_1_;

                    if (lvt_8_1_ > 1.0D)
                    {
                        lvt_8_1_ = 1.0D;
                    }

                    lvt_2_1_ = lvt_2_1_ * lvt_8_1_;
                    lvt_4_1_ = lvt_4_1_ * lvt_8_1_;
                    lvt_2_1_ = lvt_2_1_ * 0.05000000074505806D;
                    lvt_4_1_ = lvt_4_1_ * 0.05000000074505806D;
                    lvt_2_1_ = lvt_2_1_ * (double)(1.0F - this.entityCollisionReduction);
                    lvt_4_1_ = lvt_4_1_ * (double)(1.0F - this.entityCollisionReduction);

                    if (this.riddenByEntity == null)
                    {
                        this.addVelocity(-lvt_2_1_, 0.0D, -lvt_4_1_);
                    }

                    if (entityIn.riddenByEntity == null)
                    {
                        entityIn.addVelocity(lvt_2_1_, 0.0D, lvt_4_1_);
                    }
                }
            }
        }
    }

    /**
     * Adds to the current velocity of the entity. Args: x, y, z
     */
    public void addVelocity(double x, double y, double z)
    {
        this.motionX += x;
        this.motionY += y;
        this.motionZ += z;
        this.isAirBorne = true;
    }

    /**
     * Sets that this entity has been attacked.
     */
    protected void setBeenAttacked()
    {
        this.velocityChanged = true;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isEntityInvulnerable(source))
        {
            return false;
        }
        else
        {
            this.setBeenAttacked();
            return false;
        }
    }

    /**
     * interpolated look vector
     */
    public Vec3 getLook(float partialTicks)
    {
        if (partialTicks == 1.0F)
        {
            return this.getVectorForRotation(this.rotationPitch, this.rotationYaw);
        }
        else
        {
            float lvt_2_1_ = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * partialTicks;
            float lvt_3_1_ = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * partialTicks;
            return this.getVectorForRotation(lvt_2_1_, lvt_3_1_);
        }
    }

    /**
     * Creates a Vec3 using the pitch and yaw of the entities rotation.
     */
    protected final Vec3 getVectorForRotation(float pitch, float yaw)
    {
        float lvt_3_1_ = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
        float lvt_4_1_ = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float lvt_5_1_ = -MathHelper.cos(-pitch * 0.017453292F);
        float lvt_6_1_ = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3((double)(lvt_4_1_ * lvt_5_1_), (double)lvt_6_1_, (double)(lvt_3_1_ * lvt_5_1_));
    }

    public Vec3 getPositionEyes(float partialTicks)
    {
        if (partialTicks == 1.0F)
        {
            return new Vec3(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
        }
        else
        {
            double lvt_2_1_ = this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks;
            double lvt_4_1_ = this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks + (double)this.getEyeHeight();
            double lvt_6_1_ = this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks;
            return new Vec3(lvt_2_1_, lvt_4_1_, lvt_6_1_);
        }
    }

    public MovingObjectPosition rayTrace(double blockReachDistance, float partialTicks)
    {
        Vec3 lvt_4_1_ = this.getPositionEyes(partialTicks);
        Vec3 lvt_5_1_ = this.getLook(partialTicks);
        Vec3 lvt_6_1_ = lvt_4_1_.addVector(lvt_5_1_.xCoord * blockReachDistance, lvt_5_1_.yCoord * blockReachDistance, lvt_5_1_.zCoord * blockReachDistance);
        return this.worldObj.rayTraceBlocks(lvt_4_1_, lvt_6_1_, false, false, true);
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return false;
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    public boolean canBePushed()
    {
        return false;
    }

    /**
     * Adds a value to the player score. Currently not actually used and the entity passed in does nothing. Args:
     * entity, scoreToAdd
     */
    public void addToPlayerScore(Entity entityIn, int amount)
    {
    }

    public boolean isInRangeToRender3d(double x, double y, double z)
    {
        double lvt_7_1_ = this.posX - x;
        double lvt_9_1_ = this.posY - y;
        double lvt_11_1_ = this.posZ - z;
        double lvt_13_1_ = lvt_7_1_ * lvt_7_1_ + lvt_9_1_ * lvt_9_1_ + lvt_11_1_ * lvt_11_1_;
        return this.isInRangeToRenderDist(lvt_13_1_);
    }

    /**
     * Checks if the entity is in range to render by using the past in distance and comparing it to its average edge
     * length * 64 * renderDistanceWeight Args: distance
     */
    public boolean isInRangeToRenderDist(double distance)
    {
        double lvt_3_1_ = this.getEntityBoundingBox().getAverageEdgeLength();

        if (Double.isNaN(lvt_3_1_))
        {
            lvt_3_1_ = 1.0D;
        }

        lvt_3_1_ = lvt_3_1_ * 64.0D * this.renderDistanceWeight;
        return distance < lvt_3_1_ * lvt_3_1_;
    }

    /**
     * Like writeToNBTOptional but does not check if the entity is ridden. Used for saving ridden entities with their
     * riders.
     */
    public boolean writeMountToNBT(NBTTagCompound tagCompund)
    {
        String lvt_2_1_ = this.getEntityString();

        if (!this.isDead && lvt_2_1_ != null)
        {
            tagCompund.setString("id", lvt_2_1_);
            this.writeToNBT(tagCompund);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Either write this entity to the NBT tag given and return true, or return false without doing anything. If this
     * returns false the entity is not saved on disk. Ridden entities return false here as they are saved with their
     * rider.
     */
    public boolean writeToNBTOptional(NBTTagCompound tagCompund)
    {
        String lvt_2_1_ = this.getEntityString();

        if (!this.isDead && lvt_2_1_ != null && this.riddenByEntity == null)
        {
            tagCompund.setString("id", lvt_2_1_);
            this.writeToNBT(tagCompund);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Save the entity to NBT (calls an abstract helper method to write extra data)
     */
    public void writeToNBT(NBTTagCompound tagCompund)
    {
        try
        {
            tagCompund.setTag("Pos", this.newDoubleNBTList(new double[] {this.posX, this.posY, this.posZ}));
            tagCompund.setTag("Motion", this.newDoubleNBTList(new double[] {this.motionX, this.motionY, this.motionZ}));
            tagCompund.setTag("Rotation", this.newFloatNBTList(new float[] {this.rotationYaw, this.rotationPitch}));
            tagCompund.setFloat("FallDistance", this.fallDistance);
            tagCompund.setShort("Fire", (short)this.fire);
            tagCompund.setShort("Air", (short)this.getAir());
            tagCompund.setBoolean("OnGround", this.onGround);
            tagCompund.setInteger("Dimension", this.dimension);
            tagCompund.setBoolean("Invulnerable", this.invulnerable);
            tagCompund.setInteger("PortalCooldown", this.timeUntilPortal);
            tagCompund.setLong("UUIDMost", this.getUniqueID().getMostSignificantBits());
            tagCompund.setLong("UUIDLeast", this.getUniqueID().getLeastSignificantBits());

            if (this.getCustomNameTag() != null && this.getCustomNameTag().length() > 0)
            {
                tagCompund.setString("CustomName", this.getCustomNameTag());
                tagCompund.setBoolean("CustomNameVisible", this.getAlwaysRenderNameTag());
            }

            this.cmdResultStats.writeStatsToNBT(tagCompund);

            if (this.isSilent())
            {
                tagCompund.setBoolean("Silent", this.isSilent());
            }

            this.writeEntityToNBT(tagCompund);

            if (this.ridingEntity != null)
            {
                NBTTagCompound lvt_2_1_ = new NBTTagCompound();

                if (this.ridingEntity.writeMountToNBT(lvt_2_1_))
                {
                    tagCompund.setTag("Riding", lvt_2_1_);
                }
            }
        }
        catch (Throwable var5)
        {
            CrashReport lvt_3_1_ = CrashReport.makeCrashReport(var5, "Saving entity NBT");
            CrashReportCategory lvt_4_1_ = lvt_3_1_.makeCategory("Entity being saved");
            this.addEntityCrashInfo(lvt_4_1_);
            throw new ReportedException(lvt_3_1_);
        }
    }

    /**
     * Reads the entity from NBT (calls an abstract helper method to read specialized data)
     */
    public void readFromNBT(NBTTagCompound tagCompund)
    {
        try
        {
            NBTTagList lvt_2_1_ = tagCompund.getTagList("Pos", 6);
            NBTTagList lvt_3_1_ = tagCompund.getTagList("Motion", 6);
            NBTTagList lvt_4_1_ = tagCompund.getTagList("Rotation", 5);
            this.motionX = lvt_3_1_.getDoubleAt(0);
            this.motionY = lvt_3_1_.getDoubleAt(1);
            this.motionZ = lvt_3_1_.getDoubleAt(2);

            if (Math.abs(this.motionX) > 10.0D)
            {
                this.motionX = 0.0D;
            }

            if (Math.abs(this.motionY) > 10.0D)
            {
                this.motionY = 0.0D;
            }

            if (Math.abs(this.motionZ) > 10.0D)
            {
                this.motionZ = 0.0D;
            }

            this.prevPosX = this.lastTickPosX = this.posX = lvt_2_1_.getDoubleAt(0);
            this.prevPosY = this.lastTickPosY = this.posY = lvt_2_1_.getDoubleAt(1);
            this.prevPosZ = this.lastTickPosZ = this.posZ = lvt_2_1_.getDoubleAt(2);
            this.prevRotationYaw = this.rotationYaw = lvt_4_1_.getFloatAt(0);
            this.prevRotationPitch = this.rotationPitch = lvt_4_1_.getFloatAt(1);
            this.setRotationYawHead(this.rotationYaw);
            this.setRenderYawOffset(this.rotationYaw);
            this.fallDistance = tagCompund.getFloat("FallDistance");
            this.fire = tagCompund.getShort("Fire");
            this.setAir(tagCompund.getShort("Air"));
            this.onGround = tagCompund.getBoolean("OnGround");
            this.dimension = tagCompund.getInteger("Dimension");
            this.invulnerable = tagCompund.getBoolean("Invulnerable");
            this.timeUntilPortal = tagCompund.getInteger("PortalCooldown");

            if (tagCompund.hasKey("UUIDMost", 4) && tagCompund.hasKey("UUIDLeast", 4))
            {
                this.entityUniqueID = new UUID(tagCompund.getLong("UUIDMost"), tagCompund.getLong("UUIDLeast"));
            }
            else if (tagCompund.hasKey("UUID", 8))
            {
                this.entityUniqueID = UUID.fromString(tagCompund.getString("UUID"));
            }

            this.setPosition(this.posX, this.posY, this.posZ);
            this.setRotation(this.rotationYaw, this.rotationPitch);

            if (tagCompund.hasKey("CustomName", 8) && tagCompund.getString("CustomName").length() > 0)
            {
                this.setCustomNameTag(tagCompund.getString("CustomName"));
            }

            this.setAlwaysRenderNameTag(tagCompund.getBoolean("CustomNameVisible"));
            this.cmdResultStats.readStatsFromNBT(tagCompund);
            this.setSilent(tagCompund.getBoolean("Silent"));
            this.readEntityFromNBT(tagCompund);

            if (this.shouldSetPosAfterLoading())
            {
                this.setPosition(this.posX, this.posY, this.posZ);
            }
        }
        catch (Throwable var5)
        {
            CrashReport lvt_3_2_ = CrashReport.makeCrashReport(var5, "Loading entity NBT");
            CrashReportCategory lvt_4_2_ = lvt_3_2_.makeCategory("Entity being loaded");
            this.addEntityCrashInfo(lvt_4_2_);
            throw new ReportedException(lvt_3_2_);
        }
    }

    protected boolean shouldSetPosAfterLoading()
    {
        return true;
    }

    /**
     * Returns the string that identifies this Entity's class
     */
    protected final String getEntityString()
    {
        return EntityList.getEntityString(this);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected abstract void readEntityFromNBT(NBTTagCompound tagCompund);

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected abstract void writeEntityToNBT(NBTTagCompound tagCompound);

    public void onChunkLoad()
    {
    }

    /**
     * creates a NBT list from the array of doubles passed to this function
     */
    protected NBTTagList newDoubleNBTList(double... numbers)
    {
        NBTTagList lvt_2_1_ = new NBTTagList();

        for (double lvt_6_1_ : numbers)
        {
            lvt_2_1_.appendTag(new NBTTagDouble(lvt_6_1_));
        }

        return lvt_2_1_;
    }

    /**
     * Returns a new NBTTagList filled with the specified floats
     */
    protected NBTTagList newFloatNBTList(float... numbers)
    {
        NBTTagList lvt_2_1_ = new NBTTagList();

        for (float lvt_6_1_ : numbers)
        {
            lvt_2_1_.appendTag(new NBTTagFloat(lvt_6_1_));
        }

        return lvt_2_1_;
    }

    public EntityItem dropItem(Item itemIn, int size)
    {
        return this.dropItemWithOffset(itemIn, size, 0.0F);
    }

    public EntityItem dropItemWithOffset(Item itemIn, int size, float offsetY)
    {
        return this.entityDropItem(new ItemStack(itemIn, size, 0), offsetY);
    }

    /**
     * Drops an item at the position of the entity.
     */
    public EntityItem entityDropItem(ItemStack itemStackIn, float offsetY)
    {
        if (itemStackIn.stackSize != 0 && itemStackIn.getItem() != null)
        {
            EntityItem lvt_3_1_ = new EntityItem(this.worldObj, this.posX, this.posY + (double)offsetY, this.posZ, itemStackIn);
            lvt_3_1_.setDefaultPickupDelay();
            this.worldObj.spawnEntityInWorld(lvt_3_1_);
            return lvt_3_1_;
        }
        else
        {
            return null;
        }
    }

    /**
     * Checks whether target entity is alive.
     */
    public boolean isEntityAlive()
    {
        return !this.isDead;
    }

    /**
     * Checks if this entity is inside of an opaque block
     */
    public boolean isEntityInsideOpaqueBlock()
    {
        if (this.noClip)
        {
            return false;
        }
        else
        {
            BlockPos.MutableBlockPos lvt_1_1_ = new BlockPos.MutableBlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

            for (int lvt_2_1_ = 0; lvt_2_1_ < 8; ++lvt_2_1_)
            {
                int lvt_3_1_ = MathHelper.floor_double(this.posY + (double)(((float)((lvt_2_1_ >> 0) % 2) - 0.5F) * 0.1F) + (double)this.getEyeHeight());
                int lvt_4_1_ = MathHelper.floor_double(this.posX + (double)(((float)((lvt_2_1_ >> 1) % 2) - 0.5F) * this.width * 0.8F));
                int lvt_5_1_ = MathHelper.floor_double(this.posZ + (double)(((float)((lvt_2_1_ >> 2) % 2) - 0.5F) * this.width * 0.8F));

                if (lvt_1_1_.getX() != lvt_4_1_ || lvt_1_1_.getY() != lvt_3_1_ || lvt_1_1_.getZ() != lvt_5_1_)
                {
                    lvt_1_1_.set(lvt_4_1_, lvt_3_1_, lvt_5_1_);

                    if (this.worldObj.getBlockState(lvt_1_1_).getBlock().isVisuallyOpaque())
                    {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    /**
     * First layer of player interaction
     */
    public boolean interactFirst(EntityPlayer playerIn)
    {
        return false;
    }

    /**
     * Returns a boundingBox used to collide the entity with other entities and blocks. This enables the entity to be
     * pushable on contact, like boats or minecarts.
     */
    public AxisAlignedBB getCollisionBox(Entity entityIn)
    {
        return null;
    }

    /**
     * Handles updating while being ridden by an entity
     */
    public void updateRidden()
    {
        if (this.ridingEntity.isDead)
        {
            this.ridingEntity = null;
        }
        else
        {
            this.motionX = 0.0D;
            this.motionY = 0.0D;
            this.motionZ = 0.0D;
            this.onUpdate();

            if (this.ridingEntity != null)
            {
                this.ridingEntity.updateRiderPosition();
                this.entityRiderYawDelta += (double)(this.ridingEntity.rotationYaw - this.ridingEntity.prevRotationYaw);

                for (this.entityRiderPitchDelta += (double)(this.ridingEntity.rotationPitch - this.ridingEntity.prevRotationPitch); this.entityRiderYawDelta >= 180.0D; this.entityRiderYawDelta -= 360.0D)
                {
                    ;
                }

                while (this.entityRiderYawDelta < -180.0D)
                {
                    this.entityRiderYawDelta += 360.0D;
                }

                while (this.entityRiderPitchDelta >= 180.0D)
                {
                    this.entityRiderPitchDelta -= 360.0D;
                }

                while (this.entityRiderPitchDelta < -180.0D)
                {
                    this.entityRiderPitchDelta += 360.0D;
                }

                double lvt_1_1_ = this.entityRiderYawDelta * 0.5D;
                double lvt_3_1_ = this.entityRiderPitchDelta * 0.5D;
                float lvt_5_1_ = 10.0F;

                if (lvt_1_1_ > (double)lvt_5_1_)
                {
                    lvt_1_1_ = (double)lvt_5_1_;
                }

                if (lvt_1_1_ < (double)(-lvt_5_1_))
                {
                    lvt_1_1_ = (double)(-lvt_5_1_);
                }

                if (lvt_3_1_ > (double)lvt_5_1_)
                {
                    lvt_3_1_ = (double)lvt_5_1_;
                }

                if (lvt_3_1_ < (double)(-lvt_5_1_))
                {
                    lvt_3_1_ = (double)(-lvt_5_1_);
                }

                this.entityRiderYawDelta -= lvt_1_1_;
                this.entityRiderPitchDelta -= lvt_3_1_;
            }
        }
    }

    public void updateRiderPosition()
    {
        if (this.riddenByEntity != null)
        {
            this.riddenByEntity.setPosition(this.posX, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ);
        }
    }

    /**
     * Returns the Y Offset of this entity.
     */
    public double getYOffset()
    {
        return 0.0D;
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    public double getMountedYOffset()
    {
        return (double)this.height * 0.75D;
    }

    /**
     * Called when a player mounts an entity. e.g. mounts a pig, mounts a boat.
     */
    public void mountEntity(Entity entityIn)
    {
        this.entityRiderPitchDelta = 0.0D;
        this.entityRiderYawDelta = 0.0D;

        if (entityIn == null)
        {
            if (this.ridingEntity != null)
            {
                this.setLocationAndAngles(this.ridingEntity.posX, this.ridingEntity.getEntityBoundingBox().minY + (double)this.ridingEntity.height, this.ridingEntity.posZ, this.rotationYaw, this.rotationPitch);
                this.ridingEntity.riddenByEntity = null;
            }

            this.ridingEntity = null;
        }
        else
        {
            if (this.ridingEntity != null)
            {
                this.ridingEntity.riddenByEntity = null;
            }

            if (entityIn != null)
            {
                for (Entity lvt_2_1_ = entityIn.ridingEntity; lvt_2_1_ != null; lvt_2_1_ = lvt_2_1_.ridingEntity)
                {
                    if (lvt_2_1_ == this)
                    {
                        return;
                    }
                }
            }

            this.ridingEntity = entityIn;
            entityIn.riddenByEntity = this;
        }
    }

    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean p_180426_10_)
    {
        this.setPosition(x, y, z);
        this.setRotation(yaw, pitch);
        List<AxisAlignedBB> lvt_11_1_ = this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().contract(0.03125D, 0.0D, 0.03125D));

        if (!lvt_11_1_.isEmpty())
        {
            double lvt_12_1_ = 0.0D;

            for (AxisAlignedBB lvt_15_1_ : lvt_11_1_)
            {
                if (lvt_15_1_.maxY > lvt_12_1_)
                {
                    lvt_12_1_ = lvt_15_1_.maxY;
                }
            }

            y = y + (lvt_12_1_ - this.getEntityBoundingBox().minY);
            this.setPosition(x, y, z);
        }
    }

    public float getCollisionBorderSize()
    {
        return 0.1F;
    }

    /**
     * returns a (normalized) vector of where this entity is looking
     */
    public Vec3 getLookVec()
    {
        return null;
    }

    /**
     * Marks the entity as being inside a portal, activating teleportation logic in onEntityUpdate() in the following
     * tick(s).
     *  
     * @param pos The postion of the portal that the entity is in
     */
    public void setPortal(BlockPos pos)
    {
        if (this.timeUntilPortal > 0)
        {
            this.timeUntilPortal = this.getPortalCooldown();
        }
        else
        {
            if (!this.worldObj.isRemote && !pos.equals(this.lastPortalPos))
            {
                this.lastPortalPos = pos;
                BlockPattern.PatternHelper lvt_2_1_ = Blocks.portal.func_181089_f(this.worldObj, pos);
                double lvt_3_1_ = lvt_2_1_.getFinger().getAxis() == EnumFacing.Axis.X ? (double)lvt_2_1_.getPos().getZ() : (double)lvt_2_1_.getPos().getX();
                double lvt_5_1_ = lvt_2_1_.getFinger().getAxis() == EnumFacing.Axis.X ? this.posZ : this.posX;
                lvt_5_1_ = Math.abs(MathHelper.func_181160_c(lvt_5_1_ - (double)(lvt_2_1_.getFinger().rotateY().getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE ? 1 : 0), lvt_3_1_, lvt_3_1_ - (double)lvt_2_1_.func_181118_d()));
                double lvt_7_1_ = MathHelper.func_181160_c(this.posY - 1.0D, (double)lvt_2_1_.getPos().getY(), (double)(lvt_2_1_.getPos().getY() - lvt_2_1_.func_181119_e()));
                this.lastPortalVec = new Vec3(lvt_5_1_, lvt_7_1_, 0.0D);
                this.teleportDirection = lvt_2_1_.getFinger();
            }

            this.inPortal = true;
        }
    }

    /**
     * Return the amount of cooldown before this entity can use a portal again.
     */
    public int getPortalCooldown()
    {
        return 300;
    }

    /**
     * Sets the velocity to the args. Args: x, y, z
     */
    public void setVelocity(double x, double y, double z)
    {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
    }

    public void handleStatusUpdate(byte id)
    {
    }

    /**
     * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
     */
    public void performHurtAnimation()
    {
    }

    /**
     * returns the inventory of this entity (only used in EntityPlayerMP it seems)
     */
    public ItemStack[] getInventory()
    {
        return null;
    }

    /**
     * Sets the held item, or an armor slot. Slot 0 is held item. Slot 1-4 is armor. Params: Item, slot
     */
    public void setCurrentItemOrArmor(int slotIn, ItemStack stack)
    {
    }

    /**
     * Returns true if the entity is on fire. Used by render to add the fire effect on rendering.
     */
    public boolean isBurning()
    {
        boolean lvt_1_1_ = this.worldObj != null && this.worldObj.isRemote;
        return !this.isImmuneToFire && (this.fire > 0 || lvt_1_1_ && this.getFlag(0));
    }

    /**
     * Returns true if the entity is riding another entity, used by render to rotate the legs to be in 'sit' position
     * for players.
     */
    public boolean isRiding()
    {
        return this.ridingEntity != null;
    }

    /**
     * Returns if this entity is sneaking.
     */
    public boolean isSneaking()
    {
        return this.getFlag(1);
    }

    /**
     * Sets the sneaking flag.
     */
    public void setSneaking(boolean sneaking)
    {
        this.setFlag(1, sneaking);
    }

    /**
     * Get if the Entity is sprinting.
     */
    public boolean isSprinting()
    {
        return this.getFlag(3);
    }

    /**
     * Set sprinting switch for Entity.
     */
    public void setSprinting(boolean sprinting)
    {
        this.setFlag(3, sprinting);
    }

    public boolean isInvisible()
    {
        return this.getFlag(5);
    }

    /**
     * Only used by renderer in EntityLivingBase subclasses.
     * Determines if an entity is visible or not to a specfic player, if the entity is normally invisible.
     * For EntityLivingBase subclasses, returning false when invisible will render the entity semitransparent.
     */
    public boolean isInvisibleToPlayer(EntityPlayer player)
    {
        return player.isSpectator() ? false : this.isInvisible();
    }

    public void setInvisible(boolean invisible)
    {
        this.setFlag(5, invisible);
    }

    public boolean isEating()
    {
        return this.getFlag(4);
    }

    public void setEating(boolean eating)
    {
        this.setFlag(4, eating);
    }

    /**
     * Returns true if the flag is active for the entity. Known flags: 0) is burning; 1) is sneaking; 2) is riding
     * something; 3) is sprinting; 4) is eating
     */
    protected boolean getFlag(int flag)
    {
        return (this.dataWatcher.getWatchableObjectByte(0) & 1 << flag) != 0;
    }

    /**
     * Enable or disable a entity flag, see getEntityFlag to read the know flags.
     */
    protected void setFlag(int flag, boolean set)
    {
        byte lvt_3_1_ = this.dataWatcher.getWatchableObjectByte(0);

        if (set)
        {
            this.dataWatcher.updateObject(0, Byte.valueOf((byte)(lvt_3_1_ | 1 << flag)));
        }
        else
        {
            this.dataWatcher.updateObject(0, Byte.valueOf((byte)(lvt_3_1_ & ~(1 << flag))));
        }
    }

    public int getAir()
    {
        return this.dataWatcher.getWatchableObjectShort(1);
    }

    public void setAir(int air)
    {
        this.dataWatcher.updateObject(1, Short.valueOf((short)air));
    }

    /**
     * Called when a lightning bolt hits the entity.
     */
    public void onStruckByLightning(EntityLightningBolt lightningBolt)
    {
        this.attackEntityFrom(DamageSource.lightningBolt, 5.0F);
        ++this.fire;

        if (this.fire == 0)
        {
            this.setFire(8);
        }
    }

    /**
     * This method gets called when the entity kills another one.
     */
    public void onKillEntity(EntityLivingBase entityLivingIn)
    {
    }

    protected boolean pushOutOfBlocks(double x, double y, double z)
    {
        BlockPos lvt_7_1_ = new BlockPos(x, y, z);
        double lvt_8_1_ = x - (double)lvt_7_1_.getX();
        double lvt_10_1_ = y - (double)lvt_7_1_.getY();
        double lvt_12_1_ = z - (double)lvt_7_1_.getZ();
        List<AxisAlignedBB> lvt_14_1_ = this.worldObj.getCollisionBoxes(this.getEntityBoundingBox());

        if (lvt_14_1_.isEmpty() && !this.worldObj.isBlockFullCube(lvt_7_1_))
        {
            return false;
        }
        else
        {
            int lvt_15_1_ = 3;
            double lvt_16_1_ = 9999.0D;

            if (!this.worldObj.isBlockFullCube(lvt_7_1_.west()) && lvt_8_1_ < lvt_16_1_)
            {
                lvt_16_1_ = lvt_8_1_;
                lvt_15_1_ = 0;
            }

            if (!this.worldObj.isBlockFullCube(lvt_7_1_.east()) && 1.0D - lvt_8_1_ < lvt_16_1_)
            {
                lvt_16_1_ = 1.0D - lvt_8_1_;
                lvt_15_1_ = 1;
            }

            if (!this.worldObj.isBlockFullCube(lvt_7_1_.up()) && 1.0D - lvt_10_1_ < lvt_16_1_)
            {
                lvt_16_1_ = 1.0D - lvt_10_1_;
                lvt_15_1_ = 3;
            }

            if (!this.worldObj.isBlockFullCube(lvt_7_1_.north()) && lvt_12_1_ < lvt_16_1_)
            {
                lvt_16_1_ = lvt_12_1_;
                lvt_15_1_ = 4;
            }

            if (!this.worldObj.isBlockFullCube(lvt_7_1_.south()) && 1.0D - lvt_12_1_ < lvt_16_1_)
            {
                lvt_16_1_ = 1.0D - lvt_12_1_;
                lvt_15_1_ = 5;
            }

            float lvt_18_1_ = this.rand.nextFloat() * 0.2F + 0.1F;

            if (lvt_15_1_ == 0)
            {
                this.motionX = (double)(-lvt_18_1_);
            }

            if (lvt_15_1_ == 1)
            {
                this.motionX = (double)lvt_18_1_;
            }

            if (lvt_15_1_ == 3)
            {
                this.motionY = (double)lvt_18_1_;
            }

            if (lvt_15_1_ == 4)
            {
                this.motionZ = (double)(-lvt_18_1_);
            }

            if (lvt_15_1_ == 5)
            {
                this.motionZ = (double)lvt_18_1_;
            }

            return true;
        }
    }

    /**
     * Sets the Entity inside a web block.
     */
    public void setInWeb()
    {
        this.isInWeb = true;
        this.fallDistance = 0.0F;
    }

    /**
     * Get the name of this object. For players this returns their username
     */
    public String getName()
    {
        if (this.hasCustomName())
        {
            return this.getCustomNameTag();
        }
        else
        {
            String lvt_1_1_ = EntityList.getEntityString(this);

            if (lvt_1_1_ == null)
            {
                lvt_1_1_ = "generic";
            }

            return StatCollector.translateToLocal("entity." + lvt_1_1_ + ".name");
        }
    }

    /**
     * Return the Entity parts making up this Entity (currently only for dragons)
     */
    public Entity[] getParts()
    {
        return null;
    }

    /**
     * Returns true if Entity argument is equal to this Entity
     */
    public boolean isEntityEqual(Entity entityIn)
    {
        return this == entityIn;
    }

    public float getRotationYawHead()
    {
        return 0.0F;
    }

    /**
     * Sets the head's yaw rotation of the entity.
     */
    public void setRotationYawHead(float rotation)
    {
    }

    /**
     * Set the render yaw offset
     *  
     * @param offset The render yaw offset
     */
    public void setRenderYawOffset(float offset)
    {
    }

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    public boolean canAttackWithItem()
    {
        return true;
    }

    /**
     * Called when a player attacks an entity. If this returns true the attack will not happen.
     */
    public boolean hitByEntity(Entity entityIn)
    {
        return false;
    }

    public String toString()
    {
        return String.format("%s[\'%s\'/%d, l=\'%s\', x=%.2f, y=%.2f, z=%.2f]", new Object[] {this.getClass().getSimpleName(), this.getName(), Integer.valueOf(this.entityId), this.worldObj == null ? "~NULL~" : this.worldObj.getWorldInfo().getWorldName(), Double.valueOf(this.posX), Double.valueOf(this.posY), Double.valueOf(this.posZ)});
    }

    public boolean isEntityInvulnerable(DamageSource source)
    {
        return this.invulnerable && source != DamageSource.outOfWorld && !source.isCreativePlayer();
    }

    /**
     * Sets this entity's location and angles to the location and angles of the passed in entity.
     */
    public void copyLocationAndAnglesFrom(Entity entityIn)
    {
        this.setLocationAndAngles(entityIn.posX, entityIn.posY, entityIn.posZ, entityIn.rotationYaw, entityIn.rotationPitch);
    }

    /**
     * Prepares this entity in new dimension by copying NBT data from entity in old dimension
     */
    public void copyDataFromOld(Entity entityIn)
    {
        NBTTagCompound lvt_2_1_ = new NBTTagCompound();
        entityIn.writeToNBT(lvt_2_1_);
        this.readFromNBT(lvt_2_1_);
        this.timeUntilPortal = entityIn.timeUntilPortal;
        this.lastPortalPos = entityIn.lastPortalPos;
        this.lastPortalVec = entityIn.lastPortalVec;
        this.teleportDirection = entityIn.teleportDirection;
    }

    /**
     * Teleports the entity to another dimension. Params: Dimension number to teleport to
     */
    public void travelToDimension(int dimensionId)
    {
        if (!this.worldObj.isRemote && !this.isDead)
        {
            this.worldObj.theProfiler.startSection("changeDimension");
            MinecraftServer lvt_2_1_ = MinecraftServer.getServer();
            int lvt_3_1_ = this.dimension;
            WorldServer lvt_4_1_ = lvt_2_1_.worldServerForDimension(lvt_3_1_);
            WorldServer lvt_5_1_ = lvt_2_1_.worldServerForDimension(dimensionId);
            this.dimension = dimensionId;

            if (lvt_3_1_ == 1 && dimensionId == 1)
            {
                lvt_5_1_ = lvt_2_1_.worldServerForDimension(0);
                this.dimension = 0;
            }

            this.worldObj.removeEntity(this);
            this.isDead = false;
            this.worldObj.theProfiler.startSection("reposition");
            lvt_2_1_.getConfigurationManager().transferEntityToWorld(this, lvt_3_1_, lvt_4_1_, lvt_5_1_);
            this.worldObj.theProfiler.endStartSection("reloading");
            Entity lvt_6_1_ = EntityList.createEntityByName(EntityList.getEntityString(this), lvt_5_1_);

            if (lvt_6_1_ != null)
            {
                lvt_6_1_.copyDataFromOld(this);

                if (lvt_3_1_ == 1 && dimensionId == 1)
                {
                    BlockPos lvt_7_1_ = this.worldObj.getTopSolidOrLiquidBlock(lvt_5_1_.getSpawnPoint());
                    lvt_6_1_.moveToBlockPosAndAngles(lvt_7_1_, lvt_6_1_.rotationYaw, lvt_6_1_.rotationPitch);
                }

                lvt_5_1_.spawnEntityInWorld(lvt_6_1_);
            }

            this.isDead = true;
            this.worldObj.theProfiler.endSection();
            lvt_4_1_.resetUpdateEntityTick();
            lvt_5_1_.resetUpdateEntityTick();
            this.worldObj.theProfiler.endSection();
        }
    }

    /**
     * Explosion resistance of a block relative to this entity
     */
    public float getExplosionResistance(Explosion explosionIn, World worldIn, BlockPos pos, IBlockState blockStateIn)
    {
        return blockStateIn.getBlock().getExplosionResistance(this);
    }

    public boolean verifyExplosion(Explosion explosionIn, World worldIn, BlockPos pos, IBlockState blockStateIn, float p_174816_5_)
    {
        return true;
    }

    /**
     * The maximum height from where the entity is alowed to jump (used in pathfinder)
     */
    public int getMaxFallHeight()
    {
        return 3;
    }

    public Vec3 func_181014_aG()
    {
        return this.lastPortalVec;
    }

    public EnumFacing getTeleportDirection()
    {
        return this.teleportDirection;
    }

    /**
     * Return whether this entity should NOT trigger a pressure plate or a tripwire.
     */
    public boolean doesEntityNotTriggerPressurePlate()
    {
        return false;
    }

    public void addEntityCrashInfo(CrashReportCategory category)
    {
        category.addCrashSectionCallable("Entity Type", new Callable<String>()
        {
            public String call() throws Exception
            {
                return EntityList.getEntityString(Entity.this) + " (" + Entity.this.getClass().getCanonicalName() + ")";
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        category.addCrashSection("Entity ID", Integer.valueOf(this.entityId));
        category.addCrashSectionCallable("Entity Name", new Callable<String>()
        {
            public String call() throws Exception
            {
                return Entity.this.getName();
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        category.addCrashSection("Entity\'s Exact location", String.format("%.2f, %.2f, %.2f", new Object[] {Double.valueOf(this.posX), Double.valueOf(this.posY), Double.valueOf(this.posZ)}));
        category.addCrashSection("Entity\'s Block location", CrashReportCategory.getCoordinateInfo((double)MathHelper.floor_double(this.posX), (double)MathHelper.floor_double(this.posY), (double)MathHelper.floor_double(this.posZ)));
        category.addCrashSection("Entity\'s Momentum", String.format("%.2f, %.2f, %.2f", new Object[] {Double.valueOf(this.motionX), Double.valueOf(this.motionY), Double.valueOf(this.motionZ)}));
        category.addCrashSectionCallable("Entity\'s Rider", new Callable<String>()
        {
            public String call() throws Exception
            {
                return Entity.this.riddenByEntity.toString();
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
        category.addCrashSectionCallable("Entity\'s Vehicle", new Callable<String>()
        {
            public String call() throws Exception
            {
                return Entity.this.ridingEntity.toString();
            }
            public Object call() throws Exception
            {
                return this.call();
            }
        });
    }

    /**
     * Return whether this entity should be rendered as on fire.
     */
    public boolean canRenderOnFire()
    {
        return this.isBurning();
    }

    public UUID getUniqueID()
    {
        return this.entityUniqueID;
    }

    public boolean isPushedByWater()
    {
        return true;
    }

    /**
     * Get the formatted ChatComponent that will be used for the sender's username in chat
     */
    public IChatComponent getDisplayName()
    {
        ChatComponentText lvt_1_1_ = new ChatComponentText(this.getName());
        lvt_1_1_.getChatStyle().setChatHoverEvent(this.getHoverEvent());
        lvt_1_1_.getChatStyle().setInsertion(this.getUniqueID().toString());
        return lvt_1_1_;
    }

    /**
     * Sets the custom name tag for this entity
     */
    public void setCustomNameTag(String name)
    {
        this.dataWatcher.updateObject(2, name);
    }

    public String getCustomNameTag()
    {
        return this.dataWatcher.getWatchableObjectString(2);
    }

    /**
     * Returns true if this thing is named
     */
    public boolean hasCustomName()
    {
        return this.dataWatcher.getWatchableObjectString(2).length() > 0;
    }

    public void setAlwaysRenderNameTag(boolean alwaysRenderNameTag)
    {
        this.dataWatcher.updateObject(3, Byte.valueOf((byte)(alwaysRenderNameTag ? 1 : 0)));
    }

    public boolean getAlwaysRenderNameTag()
    {
        return this.dataWatcher.getWatchableObjectByte(3) == 1;
    }

    /**
     * Sets the position of the entity and updates the 'last' variables
     */
    public void setPositionAndUpdate(double x, double y, double z)
    {
        this.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
    }

    public boolean getAlwaysRenderNameTagForRender()
    {
        return this.getAlwaysRenderNameTag();
    }

    public void onDataWatcherUpdate(int dataID)
    {
    }

    public EnumFacing getHorizontalFacing()
    {
        return EnumFacing.getHorizontal(MathHelper.floor_double((double)(this.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3);
    }

    protected HoverEvent getHoverEvent()
    {
        NBTTagCompound lvt_1_1_ = new NBTTagCompound();
        String lvt_2_1_ = EntityList.getEntityString(this);
        lvt_1_1_.setString("id", this.getUniqueID().toString());

        if (lvt_2_1_ != null)
        {
            lvt_1_1_.setString("type", lvt_2_1_);
        }

        lvt_1_1_.setString("name", this.getName());
        return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new ChatComponentText(lvt_1_1_.toString()));
    }

    public boolean isSpectatedByPlayer(EntityPlayerMP player)
    {
        return true;
    }

    public AxisAlignedBB getEntityBoundingBox()
    {
        return this.boundingBox;
    }

    public void setEntityBoundingBox(AxisAlignedBB bb)
    {
        this.boundingBox = bb;
    }

    public float getEyeHeight()
    {
        return this.height * 0.85F;
    }

    public boolean isOutsideBorder()
    {
        return this.isOutsideBorder;
    }

    public void setOutsideBorder(boolean outsideBorder)
    {
        this.isOutsideBorder = outsideBorder;
    }

    public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn)
    {
        return false;
    }

    /**
     * Send a chat message to the CommandSender
     */
    public void addChatMessage(IChatComponent component)
    {
    }

    /**
     * Returns {@code true} if the CommandSender is allowed to execute the command, {@code false} if not
     */
    public boolean canCommandSenderUseCommand(int permLevel, String commandName)
    {
        return true;
    }

    /**
     * Get the position in the world. <b>{@code null} is not allowed!</b> If you are not an entity in the world, return
     * the coordinates 0, 0, 0
     */
    public BlockPos getPosition()
    {
        return new BlockPos(this.posX, this.posY + 0.5D, this.posZ);
    }

    /**
     * Get the position vector. <b>{@code null} is not allowed!</b> If you are not an entity in the world, return 0.0D,
     * 0.0D, 0.0D
     */
    public Vec3 getPositionVector()
    {
        return new Vec3(this.posX, this.posY, this.posZ);
    }

    /**
     * Get the world, if available. <b>{@code null} is not allowed!</b> If you are not an entity in the world, return
     * the overworld
     */
    public World getEntityWorld()
    {
        return this.worldObj;
    }

    /**
     * Returns the entity associated with the command sender. MAY BE NULL!
     */
    public Entity getCommandSenderEntity()
    {
        return this;
    }

    /**
     * Returns true if the command sender should be sent feedback about executed commands
     */
    public boolean sendCommandFeedback()
    {
        return false;
    }

    public void setCommandStat(CommandResultStats.Type type, int amount)
    {
        this.cmdResultStats.setCommandStatScore(this, type, amount);
    }

    public CommandResultStats getCommandStats()
    {
        return this.cmdResultStats;
    }

    /**
     * Set the CommandResultStats from the entity
     */
    public void setCommandStats(Entity entityIn)
    {
        this.cmdResultStats.addAllStats(entityIn.getCommandStats());
    }

    public NBTTagCompound getNBTTagCompound()
    {
        return null;
    }

    /**
     * Called when client receives entity's NBTTagCompound from server.
     */
    public void clientUpdateEntityNBT(NBTTagCompound compound)
    {
    }

    /**
     * New version of interactWith that includes vector information on where precisely the player targeted.
     */
    public boolean interactAt(EntityPlayer player, Vec3 targetVec3)
    {
        return false;
    }

    public boolean isImmuneToExplosions()
    {
        return false;
    }

    protected void applyEnchantments(EntityLivingBase entityLivingBaseIn, Entity entityIn)
    {
        if (entityIn instanceof EntityLivingBase)
        {
            EnchantmentHelper.applyThornEnchantments((EntityLivingBase)entityIn, entityLivingBaseIn);
        }

        EnchantmentHelper.applyArthropodEnchantments(entityLivingBaseIn, entityIn);
    }
}
