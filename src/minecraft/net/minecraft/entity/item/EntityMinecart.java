package net.minecraft.entity.item;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityMinecart extends Entity implements IWorldNameable
{
    private boolean isInReverse;
    private String entityName;

    /** Minecart rotational logic matrix */
    private static final int[][][] matrix = new int[][][] {{{0, 0, -1}, {0, 0, 1}}, {{ -1, 0, 0}, {1, 0, 0}}, {{ -1, -1, 0}, {1, 0, 0}}, {{ -1, 0, 0}, {1, -1, 0}}, {{0, 0, -1}, {0, -1, 1}}, {{0, -1, -1}, {0, 0, 1}}, {{0, 0, 1}, {1, 0, 0}}, {{0, 0, 1}, { -1, 0, 0}}, {{0, 0, -1}, { -1, 0, 0}}, {{0, 0, -1}, {1, 0, 0}}};

    /** appears to be the progress of the turn */
    private int turnProgress;
    private double minecartX;
    private double minecartY;
    private double minecartZ;
    private double minecartYaw;
    private double minecartPitch;
    private double velocityX;
    private double velocityY;
    private double velocityZ;

    public EntityMinecart(World worldIn)
    {
        super(worldIn);
        this.preventEntitySpawning = true;
        this.setSize(0.98F, 0.7F);
    }

    public static EntityMinecart getMinecart(World worldIn, double x, double y, double z, EntityMinecart.EnumMinecartType type)
    {
        switch (type)
        {
            case CHEST:
                return new EntityMinecartChest(worldIn, x, y, z);

            case FURNACE:
                return new EntityMinecartFurnace(worldIn, x, y, z);

            case TNT:
                return new EntityMinecartTNT(worldIn, x, y, z);

            case SPAWNER:
                return new EntityMinecartMobSpawner(worldIn, x, y, z);

            case HOPPER:
                return new EntityMinecartHopper(worldIn, x, y, z);

            case COMMAND_BLOCK:
                return new EntityMinecartCommandBlock(worldIn, x, y, z);

            default:
                return new EntityMinecartEmpty(worldIn, x, y, z);
        }
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    protected void entityInit()
    {
        this.dataWatcher.addObject(17, new Integer(0));
        this.dataWatcher.addObject(18, new Integer(1));
        this.dataWatcher.addObject(19, new Float(0.0F));
        this.dataWatcher.addObject(20, new Integer(0));
        this.dataWatcher.addObject(21, new Integer(6));
        this.dataWatcher.addObject(22, Byte.valueOf((byte)0));
    }

    /**
     * Returns a boundingBox used to collide the entity with other entities and blocks. This enables the entity to be
     * pushable on contact, like boats or minecarts.
     */
    public AxisAlignedBB getCollisionBox(Entity entityIn)
    {
        return entityIn.canBePushed() ? entityIn.getEntityBoundingBox() : null;
    }

    /**
     * Returns the collision bounding box for this entity
     */
    public AxisAlignedBB getCollisionBoundingBox()
    {
        return null;
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    public boolean canBePushed()
    {
        return true;
    }

    public EntityMinecart(World worldIn, double x, double y, double z)
    {
        this(worldIn);
        this.setPosition(x, y, z);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    public double getMountedYOffset()
    {
        return 0.0D;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (!this.worldObj.isRemote && !this.isDead)
        {
            if (this.isEntityInvulnerable(source))
            {
                return false;
            }
            else
            {
                this.setRollingDirection(-this.getRollingDirection());
                this.setRollingAmplitude(10);
                this.setBeenAttacked();
                this.setDamage(this.getDamage() + amount * 10.0F);
                boolean lvt_3_1_ = source.getEntity() instanceof EntityPlayer && ((EntityPlayer)source.getEntity()).capabilities.isCreativeMode;

                if (lvt_3_1_ || this.getDamage() > 40.0F)
                {
                    if (this.riddenByEntity != null)
                    {
                        this.riddenByEntity.mountEntity((Entity)null);
                    }

                    if (lvt_3_1_ && !this.hasCustomName())
                    {
                        this.setDead();
                    }
                    else
                    {
                        this.killMinecart(source);
                    }
                }

                return true;
            }
        }
        else
        {
            return true;
        }
    }

    public void killMinecart(DamageSource source)
    {
        this.setDead();

        if (this.worldObj.getGameRules().getBoolean("doEntityDrops"))
        {
            ItemStack lvt_2_1_ = new ItemStack(Items.minecart, 1);

            if (this.entityName != null)
            {
                lvt_2_1_.setStackDisplayName(this.entityName);
            }

            this.entityDropItem(lvt_2_1_, 0.0F);
        }
    }

    /**
     * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
     */
    public void performHurtAnimation()
    {
        this.setRollingDirection(-this.getRollingDirection());
        this.setRollingAmplitude(10);
        this.setDamage(this.getDamage() + this.getDamage() * 10.0F);
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }

    /**
     * Will get destroyed next tick.
     */
    public void setDead()
    {
        super.setDead();
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        if (this.getRollingAmplitude() > 0)
        {
            this.setRollingAmplitude(this.getRollingAmplitude() - 1);
        }

        if (this.getDamage() > 0.0F)
        {
            this.setDamage(this.getDamage() - 1.0F);
        }

        if (this.posY < -64.0D)
        {
            this.kill();
        }

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

        if (this.worldObj.isRemote)
        {
            if (this.turnProgress > 0)
            {
                double lvt_1_2_ = this.posX + (this.minecartX - this.posX) / (double)this.turnProgress;
                double lvt_3_3_ = this.posY + (this.minecartY - this.posY) / (double)this.turnProgress;
                double lvt_5_1_ = this.posZ + (this.minecartZ - this.posZ) / (double)this.turnProgress;
                double lvt_7_1_ = MathHelper.wrapAngleTo180_double(this.minecartYaw - (double)this.rotationYaw);
                this.rotationYaw = (float)((double)this.rotationYaw + lvt_7_1_ / (double)this.turnProgress);
                this.rotationPitch = (float)((double)this.rotationPitch + (this.minecartPitch - (double)this.rotationPitch) / (double)this.turnProgress);
                --this.turnProgress;
                this.setPosition(lvt_1_2_, lvt_3_3_, lvt_5_1_);
                this.setRotation(this.rotationYaw, this.rotationPitch);
            }
            else
            {
                this.setPosition(this.posX, this.posY, this.posZ);
                this.setRotation(this.rotationYaw, this.rotationPitch);
            }
        }
        else
        {
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            this.motionY -= 0.03999999910593033D;
            int lvt_1_3_ = MathHelper.floor_double(this.posX);
            int lvt_2_2_ = MathHelper.floor_double(this.posY);
            int lvt_3_4_ = MathHelper.floor_double(this.posZ);

            if (BlockRailBase.isRailBlock(this.worldObj, new BlockPos(lvt_1_3_, lvt_2_2_ - 1, lvt_3_4_)))
            {
                --lvt_2_2_;
            }

            BlockPos lvt_4_1_ = new BlockPos(lvt_1_3_, lvt_2_2_, lvt_3_4_);
            IBlockState lvt_5_2_ = this.worldObj.getBlockState(lvt_4_1_);

            if (BlockRailBase.isRailBlock(lvt_5_2_))
            {
                this.func_180460_a(lvt_4_1_, lvt_5_2_);

                if (lvt_5_2_.getBlock() == Blocks.activator_rail)
                {
                    this.onActivatorRailPass(lvt_1_3_, lvt_2_2_, lvt_3_4_, ((Boolean)lvt_5_2_.getValue(BlockRailPowered.POWERED)).booleanValue());
                }
            }
            else
            {
                this.moveDerailedMinecart();
            }

            this.doBlockCollisions();
            this.rotationPitch = 0.0F;
            double lvt_6_1_ = this.prevPosX - this.posX;
            double lvt_8_1_ = this.prevPosZ - this.posZ;

            if (lvt_6_1_ * lvt_6_1_ + lvt_8_1_ * lvt_8_1_ > 0.001D)
            {
                this.rotationYaw = (float)(MathHelper.atan2(lvt_8_1_, lvt_6_1_) * 180.0D / Math.PI);

                if (this.isInReverse)
                {
                    this.rotationYaw += 180.0F;
                }
            }

            double lvt_10_1_ = (double)MathHelper.wrapAngleTo180_float(this.rotationYaw - this.prevRotationYaw);

            if (lvt_10_1_ < -170.0D || lvt_10_1_ >= 170.0D)
            {
                this.rotationYaw += 180.0F;
                this.isInReverse = !this.isInReverse;
            }

            this.setRotation(this.rotationYaw, this.rotationPitch);

            for (Entity lvt_13_1_ : this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(0.20000000298023224D, 0.0D, 0.20000000298023224D)))
            {
                if (lvt_13_1_ != this.riddenByEntity && lvt_13_1_.canBePushed() && lvt_13_1_ instanceof EntityMinecart)
                {
                    lvt_13_1_.applyEntityCollision(this);
                }
            }

            if (this.riddenByEntity != null && this.riddenByEntity.isDead)
            {
                if (this.riddenByEntity.ridingEntity == this)
                {
                    this.riddenByEntity.ridingEntity = null;
                }

                this.riddenByEntity = null;
            }

            this.handleWaterMovement();
        }
    }

    /**
     * Get's the maximum speed for a minecart
     */
    protected double getMaximumSpeed()
    {
        return 0.4D;
    }

    /**
     * Called every tick the minecart is on an activator rail. Args: x, y, z, is the rail receiving power
     */
    public void onActivatorRailPass(int x, int y, int z, boolean receivingPower)
    {
    }

    /**
     * Moves a minecart that is not attached to a rail
     */
    protected void moveDerailedMinecart()
    {
        double lvt_1_1_ = this.getMaximumSpeed();
        this.motionX = MathHelper.clamp_double(this.motionX, -lvt_1_1_, lvt_1_1_);
        this.motionZ = MathHelper.clamp_double(this.motionZ, -lvt_1_1_, lvt_1_1_);

        if (this.onGround)
        {
            this.motionX *= 0.5D;
            this.motionY *= 0.5D;
            this.motionZ *= 0.5D;
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);

        if (!this.onGround)
        {
            this.motionX *= 0.949999988079071D;
            this.motionY *= 0.949999988079071D;
            this.motionZ *= 0.949999988079071D;
        }
    }

    @SuppressWarnings("incomplete-switch")
    protected void func_180460_a(BlockPos p_180460_1_, IBlockState p_180460_2_)
    {
        this.fallDistance = 0.0F;
        Vec3 lvt_3_1_ = this.func_70489_a(this.posX, this.posY, this.posZ);
        this.posY = (double)p_180460_1_.getY();
        boolean lvt_4_1_ = false;
        boolean lvt_5_1_ = false;
        BlockRailBase lvt_6_1_ = (BlockRailBase)p_180460_2_.getBlock();

        if (lvt_6_1_ == Blocks.golden_rail)
        {
            lvt_4_1_ = ((Boolean)p_180460_2_.getValue(BlockRailPowered.POWERED)).booleanValue();
            lvt_5_1_ = !lvt_4_1_;
        }

        double lvt_7_1_ = 0.0078125D;
        BlockRailBase.EnumRailDirection lvt_9_1_ = (BlockRailBase.EnumRailDirection)p_180460_2_.getValue(lvt_6_1_.getShapeProperty());

        switch (lvt_9_1_)
        {
            case ASCENDING_EAST:
                this.motionX -= 0.0078125D;
                ++this.posY;
                break;

            case ASCENDING_WEST:
                this.motionX += 0.0078125D;
                ++this.posY;
                break;

            case ASCENDING_NORTH:
                this.motionZ += 0.0078125D;
                ++this.posY;
                break;

            case ASCENDING_SOUTH:
                this.motionZ -= 0.0078125D;
                ++this.posY;
        }

        int[][] lvt_10_1_ = matrix[lvt_9_1_.getMetadata()];
        double lvt_11_1_ = (double)(lvt_10_1_[1][0] - lvt_10_1_[0][0]);
        double lvt_13_1_ = (double)(lvt_10_1_[1][2] - lvt_10_1_[0][2]);
        double lvt_15_1_ = Math.sqrt(lvt_11_1_ * lvt_11_1_ + lvt_13_1_ * lvt_13_1_);
        double lvt_17_1_ = this.motionX * lvt_11_1_ + this.motionZ * lvt_13_1_;

        if (lvt_17_1_ < 0.0D)
        {
            lvt_11_1_ = -lvt_11_1_;
            lvt_13_1_ = -lvt_13_1_;
        }

        double lvt_19_1_ = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

        if (lvt_19_1_ > 2.0D)
        {
            lvt_19_1_ = 2.0D;
        }

        this.motionX = lvt_19_1_ * lvt_11_1_ / lvt_15_1_;
        this.motionZ = lvt_19_1_ * lvt_13_1_ / lvt_15_1_;

        if (this.riddenByEntity instanceof EntityLivingBase)
        {
            double lvt_21_1_ = (double)((EntityLivingBase)this.riddenByEntity).moveForward;

            if (lvt_21_1_ > 0.0D)
            {
                double lvt_23_1_ = -Math.sin((double)(this.riddenByEntity.rotationYaw * (float)Math.PI / 180.0F));
                double lvt_25_1_ = Math.cos((double)(this.riddenByEntity.rotationYaw * (float)Math.PI / 180.0F));
                double lvt_27_1_ = this.motionX * this.motionX + this.motionZ * this.motionZ;

                if (lvt_27_1_ < 0.01D)
                {
                    this.motionX += lvt_23_1_ * 0.1D;
                    this.motionZ += lvt_25_1_ * 0.1D;
                    lvt_5_1_ = false;
                }
            }
        }

        if (lvt_5_1_)
        {
            double lvt_21_2_ = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

            if (lvt_21_2_ < 0.03D)
            {
                this.motionX *= 0.0D;
                this.motionY *= 0.0D;
                this.motionZ *= 0.0D;
            }
            else
            {
                this.motionX *= 0.5D;
                this.motionY *= 0.0D;
                this.motionZ *= 0.5D;
            }
        }

        double lvt_21_3_ = 0.0D;
        double lvt_23_2_ = (double)p_180460_1_.getX() + 0.5D + (double)lvt_10_1_[0][0] * 0.5D;
        double lvt_25_2_ = (double)p_180460_1_.getZ() + 0.5D + (double)lvt_10_1_[0][2] * 0.5D;
        double lvt_27_2_ = (double)p_180460_1_.getX() + 0.5D + (double)lvt_10_1_[1][0] * 0.5D;
        double lvt_29_1_ = (double)p_180460_1_.getZ() + 0.5D + (double)lvt_10_1_[1][2] * 0.5D;
        lvt_11_1_ = lvt_27_2_ - lvt_23_2_;
        lvt_13_1_ = lvt_29_1_ - lvt_25_2_;

        if (lvt_11_1_ == 0.0D)
        {
            this.posX = (double)p_180460_1_.getX() + 0.5D;
            lvt_21_3_ = this.posZ - (double)p_180460_1_.getZ();
        }
        else if (lvt_13_1_ == 0.0D)
        {
            this.posZ = (double)p_180460_1_.getZ() + 0.5D;
            lvt_21_3_ = this.posX - (double)p_180460_1_.getX();
        }
        else
        {
            double lvt_31_1_ = this.posX - lvt_23_2_;
            double lvt_33_1_ = this.posZ - lvt_25_2_;
            lvt_21_3_ = (lvt_31_1_ * lvt_11_1_ + lvt_33_1_ * lvt_13_1_) * 2.0D;
        }

        this.posX = lvt_23_2_ + lvt_11_1_ * lvt_21_3_;
        this.posZ = lvt_25_2_ + lvt_13_1_ * lvt_21_3_;
        this.setPosition(this.posX, this.posY, this.posZ);
        double lvt_31_2_ = this.motionX;
        double lvt_33_2_ = this.motionZ;

        if (this.riddenByEntity != null)
        {
            lvt_31_2_ *= 0.75D;
            lvt_33_2_ *= 0.75D;
        }

        double lvt_35_1_ = this.getMaximumSpeed();
        lvt_31_2_ = MathHelper.clamp_double(lvt_31_2_, -lvt_35_1_, lvt_35_1_);
        lvt_33_2_ = MathHelper.clamp_double(lvt_33_2_, -lvt_35_1_, lvt_35_1_);
        this.moveEntity(lvt_31_2_, 0.0D, lvt_33_2_);

        if (lvt_10_1_[0][1] != 0 && MathHelper.floor_double(this.posX) - p_180460_1_.getX() == lvt_10_1_[0][0] && MathHelper.floor_double(this.posZ) - p_180460_1_.getZ() == lvt_10_1_[0][2])
        {
            this.setPosition(this.posX, this.posY + (double)lvt_10_1_[0][1], this.posZ);
        }
        else if (lvt_10_1_[1][1] != 0 && MathHelper.floor_double(this.posX) - p_180460_1_.getX() == lvt_10_1_[1][0] && MathHelper.floor_double(this.posZ) - p_180460_1_.getZ() == lvt_10_1_[1][2])
        {
            this.setPosition(this.posX, this.posY + (double)lvt_10_1_[1][1], this.posZ);
        }

        this.applyDrag();
        Vec3 lvt_37_1_ = this.func_70489_a(this.posX, this.posY, this.posZ);

        if (lvt_37_1_ != null && lvt_3_1_ != null)
        {
            double lvt_38_1_ = (lvt_3_1_.yCoord - lvt_37_1_.yCoord) * 0.05D;
            lvt_19_1_ = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

            if (lvt_19_1_ > 0.0D)
            {
                this.motionX = this.motionX / lvt_19_1_ * (lvt_19_1_ + lvt_38_1_);
                this.motionZ = this.motionZ / lvt_19_1_ * (lvt_19_1_ + lvt_38_1_);
            }

            this.setPosition(this.posX, lvt_37_1_.yCoord, this.posZ);
        }

        int lvt_38_2_ = MathHelper.floor_double(this.posX);
        int lvt_39_1_ = MathHelper.floor_double(this.posZ);

        if (lvt_38_2_ != p_180460_1_.getX() || lvt_39_1_ != p_180460_1_.getZ())
        {
            lvt_19_1_ = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.motionX = lvt_19_1_ * (double)(lvt_38_2_ - p_180460_1_.getX());
            this.motionZ = lvt_19_1_ * (double)(lvt_39_1_ - p_180460_1_.getZ());
        }

        if (lvt_4_1_)
        {
            double lvt_40_1_ = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

            if (lvt_40_1_ > 0.01D)
            {
                double lvt_42_1_ = 0.06D;
                this.motionX += this.motionX / lvt_40_1_ * lvt_42_1_;
                this.motionZ += this.motionZ / lvt_40_1_ * lvt_42_1_;
            }
            else if (lvt_9_1_ == BlockRailBase.EnumRailDirection.EAST_WEST)
            {
                if (this.worldObj.getBlockState(p_180460_1_.west()).getBlock().isNormalCube())
                {
                    this.motionX = 0.02D;
                }
                else if (this.worldObj.getBlockState(p_180460_1_.east()).getBlock().isNormalCube())
                {
                    this.motionX = -0.02D;
                }
            }
            else if (lvt_9_1_ == BlockRailBase.EnumRailDirection.NORTH_SOUTH)
            {
                if (this.worldObj.getBlockState(p_180460_1_.north()).getBlock().isNormalCube())
                {
                    this.motionZ = 0.02D;
                }
                else if (this.worldObj.getBlockState(p_180460_1_.south()).getBlock().isNormalCube())
                {
                    this.motionZ = -0.02D;
                }
            }
        }
    }

    protected void applyDrag()
    {
        if (this.riddenByEntity != null)
        {
            this.motionX *= 0.996999979019165D;
            this.motionY *= 0.0D;
            this.motionZ *= 0.996999979019165D;
        }
        else
        {
            this.motionX *= 0.9599999785423279D;
            this.motionY *= 0.0D;
            this.motionZ *= 0.9599999785423279D;
        }
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

    public Vec3 func_70495_a(double p_70495_1_, double p_70495_3_, double p_70495_5_, double p_70495_7_)
    {
        int lvt_9_1_ = MathHelper.floor_double(p_70495_1_);
        int lvt_10_1_ = MathHelper.floor_double(p_70495_3_);
        int lvt_11_1_ = MathHelper.floor_double(p_70495_5_);

        if (BlockRailBase.isRailBlock(this.worldObj, new BlockPos(lvt_9_1_, lvt_10_1_ - 1, lvt_11_1_)))
        {
            --lvt_10_1_;
        }

        IBlockState lvt_12_1_ = this.worldObj.getBlockState(new BlockPos(lvt_9_1_, lvt_10_1_, lvt_11_1_));

        if (BlockRailBase.isRailBlock(lvt_12_1_))
        {
            BlockRailBase.EnumRailDirection lvt_13_1_ = (BlockRailBase.EnumRailDirection)lvt_12_1_.getValue(((BlockRailBase)lvt_12_1_.getBlock()).getShapeProperty());
            p_70495_3_ = (double)lvt_10_1_;

            if (lvt_13_1_.isAscending())
            {
                p_70495_3_ = (double)(lvt_10_1_ + 1);
            }

            int[][] lvt_14_1_ = matrix[lvt_13_1_.getMetadata()];
            double lvt_15_1_ = (double)(lvt_14_1_[1][0] - lvt_14_1_[0][0]);
            double lvt_17_1_ = (double)(lvt_14_1_[1][2] - lvt_14_1_[0][2]);
            double lvt_19_1_ = Math.sqrt(lvt_15_1_ * lvt_15_1_ + lvt_17_1_ * lvt_17_1_);
            lvt_15_1_ = lvt_15_1_ / lvt_19_1_;
            lvt_17_1_ = lvt_17_1_ / lvt_19_1_;
            p_70495_1_ = p_70495_1_ + lvt_15_1_ * p_70495_7_;
            p_70495_5_ = p_70495_5_ + lvt_17_1_ * p_70495_7_;

            if (lvt_14_1_[0][1] != 0 && MathHelper.floor_double(p_70495_1_) - lvt_9_1_ == lvt_14_1_[0][0] && MathHelper.floor_double(p_70495_5_) - lvt_11_1_ == lvt_14_1_[0][2])
            {
                p_70495_3_ += (double)lvt_14_1_[0][1];
            }
            else if (lvt_14_1_[1][1] != 0 && MathHelper.floor_double(p_70495_1_) - lvt_9_1_ == lvt_14_1_[1][0] && MathHelper.floor_double(p_70495_5_) - lvt_11_1_ == lvt_14_1_[1][2])
            {
                p_70495_3_ += (double)lvt_14_1_[1][1];
            }

            return this.func_70489_a(p_70495_1_, p_70495_3_, p_70495_5_);
        }
        else
        {
            return null;
        }
    }

    public Vec3 func_70489_a(double p_70489_1_, double p_70489_3_, double p_70489_5_)
    {
        int lvt_7_1_ = MathHelper.floor_double(p_70489_1_);
        int lvt_8_1_ = MathHelper.floor_double(p_70489_3_);
        int lvt_9_1_ = MathHelper.floor_double(p_70489_5_);

        if (BlockRailBase.isRailBlock(this.worldObj, new BlockPos(lvt_7_1_, lvt_8_1_ - 1, lvt_9_1_)))
        {
            --lvt_8_1_;
        }

        IBlockState lvt_10_1_ = this.worldObj.getBlockState(new BlockPos(lvt_7_1_, lvt_8_1_, lvt_9_1_));

        if (BlockRailBase.isRailBlock(lvt_10_1_))
        {
            BlockRailBase.EnumRailDirection lvt_11_1_ = (BlockRailBase.EnumRailDirection)lvt_10_1_.getValue(((BlockRailBase)lvt_10_1_.getBlock()).getShapeProperty());
            int[][] lvt_12_1_ = matrix[lvt_11_1_.getMetadata()];
            double lvt_13_1_ = 0.0D;
            double lvt_15_1_ = (double)lvt_7_1_ + 0.5D + (double)lvt_12_1_[0][0] * 0.5D;
            double lvt_17_1_ = (double)lvt_8_1_ + 0.0625D + (double)lvt_12_1_[0][1] * 0.5D;
            double lvt_19_1_ = (double)lvt_9_1_ + 0.5D + (double)lvt_12_1_[0][2] * 0.5D;
            double lvt_21_1_ = (double)lvt_7_1_ + 0.5D + (double)lvt_12_1_[1][0] * 0.5D;
            double lvt_23_1_ = (double)lvt_8_1_ + 0.0625D + (double)lvt_12_1_[1][1] * 0.5D;
            double lvt_25_1_ = (double)lvt_9_1_ + 0.5D + (double)lvt_12_1_[1][2] * 0.5D;
            double lvt_27_1_ = lvt_21_1_ - lvt_15_1_;
            double lvt_29_1_ = (lvt_23_1_ - lvt_17_1_) * 2.0D;
            double lvt_31_1_ = lvt_25_1_ - lvt_19_1_;

            if (lvt_27_1_ == 0.0D)
            {
                p_70489_1_ = (double)lvt_7_1_ + 0.5D;
                lvt_13_1_ = p_70489_5_ - (double)lvt_9_1_;
            }
            else if (lvt_31_1_ == 0.0D)
            {
                p_70489_5_ = (double)lvt_9_1_ + 0.5D;
                lvt_13_1_ = p_70489_1_ - (double)lvt_7_1_;
            }
            else
            {
                double lvt_33_1_ = p_70489_1_ - lvt_15_1_;
                double lvt_35_1_ = p_70489_5_ - lvt_19_1_;
                lvt_13_1_ = (lvt_33_1_ * lvt_27_1_ + lvt_35_1_ * lvt_31_1_) * 2.0D;
            }

            p_70489_1_ = lvt_15_1_ + lvt_27_1_ * lvt_13_1_;
            p_70489_3_ = lvt_17_1_ + lvt_29_1_ * lvt_13_1_;
            p_70489_5_ = lvt_19_1_ + lvt_31_1_ * lvt_13_1_;

            if (lvt_29_1_ < 0.0D)
            {
                ++p_70489_3_;
            }

            if (lvt_29_1_ > 0.0D)
            {
                p_70489_3_ += 0.5D;
            }

            return new Vec3(p_70489_1_, p_70489_3_, p_70489_5_);
        }
        else
        {
            return null;
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        if (tagCompund.getBoolean("CustomDisplayTile"))
        {
            int lvt_2_1_ = tagCompund.getInteger("DisplayData");

            if (tagCompund.hasKey("DisplayTile", 8))
            {
                Block lvt_3_1_ = Block.getBlockFromName(tagCompund.getString("DisplayTile"));

                if (lvt_3_1_ == null)
                {
                    this.func_174899_a(Blocks.air.getDefaultState());
                }
                else
                {
                    this.func_174899_a(lvt_3_1_.getStateFromMeta(lvt_2_1_));
                }
            }
            else
            {
                Block lvt_3_2_ = Block.getBlockById(tagCompund.getInteger("DisplayTile"));

                if (lvt_3_2_ == null)
                {
                    this.func_174899_a(Blocks.air.getDefaultState());
                }
                else
                {
                    this.func_174899_a(lvt_3_2_.getStateFromMeta(lvt_2_1_));
                }
            }

            this.setDisplayTileOffset(tagCompund.getInteger("DisplayOffset"));
        }

        if (tagCompund.hasKey("CustomName", 8) && tagCompund.getString("CustomName").length() > 0)
        {
            this.entityName = tagCompund.getString("CustomName");
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        if (this.hasDisplayTile())
        {
            tagCompound.setBoolean("CustomDisplayTile", true);
            IBlockState lvt_2_1_ = this.getDisplayTile();
            ResourceLocation lvt_3_1_ = (ResourceLocation)Block.blockRegistry.getNameForObject(lvt_2_1_.getBlock());
            tagCompound.setString("DisplayTile", lvt_3_1_ == null ? "" : lvt_3_1_.toString());
            tagCompound.setInteger("DisplayData", lvt_2_1_.getBlock().getMetaFromState(lvt_2_1_));
            tagCompound.setInteger("DisplayOffset", this.getDisplayTileOffset());
        }

        if (this.entityName != null && this.entityName.length() > 0)
        {
            tagCompound.setString("CustomName", this.entityName);
        }
    }

    /**
     * Applies a velocity to each of the entities pushing them away from each other. Args: entity
     */
    public void applyEntityCollision(Entity entityIn)
    {
        if (!this.worldObj.isRemote)
        {
            if (!entityIn.noClip && !this.noClip)
            {
                if (entityIn != this.riddenByEntity)
                {
                    if (entityIn instanceof EntityLivingBase && !(entityIn instanceof EntityPlayer) && !(entityIn instanceof EntityIronGolem) && this.getMinecartType() == EntityMinecart.EnumMinecartType.RIDEABLE && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.01D && this.riddenByEntity == null && entityIn.ridingEntity == null)
                    {
                        entityIn.mountEntity(this);
                    }

                    double lvt_2_1_ = entityIn.posX - this.posX;
                    double lvt_4_1_ = entityIn.posZ - this.posZ;
                    double lvt_6_1_ = lvt_2_1_ * lvt_2_1_ + lvt_4_1_ * lvt_4_1_;

                    if (lvt_6_1_ >= 9.999999747378752E-5D)
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
                        lvt_2_1_ = lvt_2_1_ * 0.10000000149011612D;
                        lvt_4_1_ = lvt_4_1_ * 0.10000000149011612D;
                        lvt_2_1_ = lvt_2_1_ * (double)(1.0F - this.entityCollisionReduction);
                        lvt_4_1_ = lvt_4_1_ * (double)(1.0F - this.entityCollisionReduction);
                        lvt_2_1_ = lvt_2_1_ * 0.5D;
                        lvt_4_1_ = lvt_4_1_ * 0.5D;

                        if (entityIn instanceof EntityMinecart)
                        {
                            double lvt_10_1_ = entityIn.posX - this.posX;
                            double lvt_12_1_ = entityIn.posZ - this.posZ;
                            Vec3 lvt_14_1_ = (new Vec3(lvt_10_1_, 0.0D, lvt_12_1_)).normalize();
                            Vec3 lvt_15_1_ = (new Vec3((double)MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F), 0.0D, (double)MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F))).normalize();
                            double lvt_16_1_ = Math.abs(lvt_14_1_.dotProduct(lvt_15_1_));

                            if (lvt_16_1_ < 0.800000011920929D)
                            {
                                return;
                            }

                            double lvt_18_1_ = entityIn.motionX + this.motionX;
                            double lvt_20_1_ = entityIn.motionZ + this.motionZ;

                            if (((EntityMinecart)entityIn).getMinecartType() == EntityMinecart.EnumMinecartType.FURNACE && this.getMinecartType() != EntityMinecart.EnumMinecartType.FURNACE)
                            {
                                this.motionX *= 0.20000000298023224D;
                                this.motionZ *= 0.20000000298023224D;
                                this.addVelocity(entityIn.motionX - lvt_2_1_, 0.0D, entityIn.motionZ - lvt_4_1_);
                                entityIn.motionX *= 0.949999988079071D;
                                entityIn.motionZ *= 0.949999988079071D;
                            }
                            else if (((EntityMinecart)entityIn).getMinecartType() != EntityMinecart.EnumMinecartType.FURNACE && this.getMinecartType() == EntityMinecart.EnumMinecartType.FURNACE)
                            {
                                entityIn.motionX *= 0.20000000298023224D;
                                entityIn.motionZ *= 0.20000000298023224D;
                                entityIn.addVelocity(this.motionX + lvt_2_1_, 0.0D, this.motionZ + lvt_4_1_);
                                this.motionX *= 0.949999988079071D;
                                this.motionZ *= 0.949999988079071D;
                            }
                            else
                            {
                                lvt_18_1_ = lvt_18_1_ / 2.0D;
                                lvt_20_1_ = lvt_20_1_ / 2.0D;
                                this.motionX *= 0.20000000298023224D;
                                this.motionZ *= 0.20000000298023224D;
                                this.addVelocity(lvt_18_1_ - lvt_2_1_, 0.0D, lvt_20_1_ - lvt_4_1_);
                                entityIn.motionX *= 0.20000000298023224D;
                                entityIn.motionZ *= 0.20000000298023224D;
                                entityIn.addVelocity(lvt_18_1_ + lvt_2_1_, 0.0D, lvt_20_1_ + lvt_4_1_);
                            }
                        }
                        else
                        {
                            this.addVelocity(-lvt_2_1_, 0.0D, -lvt_4_1_);
                            entityIn.addVelocity(lvt_2_1_ / 4.0D, 0.0D, lvt_4_1_ / 4.0D);
                        }
                    }
                }
            }
        }
    }

    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean p_180426_10_)
    {
        this.minecartX = x;
        this.minecartY = y;
        this.minecartZ = z;
        this.minecartYaw = (double)yaw;
        this.minecartPitch = (double)pitch;
        this.turnProgress = posRotationIncrements + 2;
        this.motionX = this.velocityX;
        this.motionY = this.velocityY;
        this.motionZ = this.velocityZ;
    }

    /**
     * Sets the velocity to the args. Args: x, y, z
     */
    public void setVelocity(double x, double y, double z)
    {
        this.velocityX = this.motionX = x;
        this.velocityY = this.motionY = y;
        this.velocityZ = this.motionZ = z;
    }

    /**
     * Sets the current amount of damage the minecart has taken. Decreases over time. The cart breaks when this is over
     * 40.
     */
    public void setDamage(float p_70492_1_)
    {
        this.dataWatcher.updateObject(19, Float.valueOf(p_70492_1_));
    }

    /**
     * Gets the current amount of damage the minecart has taken. Decreases over time. The cart breaks when this is over
     * 40.
     */
    public float getDamage()
    {
        return this.dataWatcher.getWatchableObjectFloat(19);
    }

    /**
     * Sets the rolling amplitude the cart rolls while being attacked.
     */
    public void setRollingAmplitude(int p_70497_1_)
    {
        this.dataWatcher.updateObject(17, Integer.valueOf(p_70497_1_));
    }

    /**
     * Gets the rolling amplitude the cart rolls while being attacked.
     */
    public int getRollingAmplitude()
    {
        return this.dataWatcher.getWatchableObjectInt(17);
    }

    /**
     * Sets the rolling direction the cart rolls while being attacked. Can be 1 or -1.
     */
    public void setRollingDirection(int p_70494_1_)
    {
        this.dataWatcher.updateObject(18, Integer.valueOf(p_70494_1_));
    }

    /**
     * Gets the rolling direction the cart rolls while being attacked. Can be 1 or -1.
     */
    public int getRollingDirection()
    {
        return this.dataWatcher.getWatchableObjectInt(18);
    }

    public abstract EntityMinecart.EnumMinecartType getMinecartType();

    public IBlockState getDisplayTile()
    {
        return !this.hasDisplayTile() ? this.getDefaultDisplayTile() : Block.getStateById(this.getDataWatcher().getWatchableObjectInt(20));
    }

    public IBlockState getDefaultDisplayTile()
    {
        return Blocks.air.getDefaultState();
    }

    public int getDisplayTileOffset()
    {
        return !this.hasDisplayTile() ? this.getDefaultDisplayTileOffset() : this.getDataWatcher().getWatchableObjectInt(21);
    }

    public int getDefaultDisplayTileOffset()
    {
        return 6;
    }

    public void func_174899_a(IBlockState p_174899_1_)
    {
        this.getDataWatcher().updateObject(20, Integer.valueOf(Block.getStateId(p_174899_1_)));
        this.setHasDisplayTile(true);
    }

    public void setDisplayTileOffset(int p_94086_1_)
    {
        this.getDataWatcher().updateObject(21, Integer.valueOf(p_94086_1_));
        this.setHasDisplayTile(true);
    }

    public boolean hasDisplayTile()
    {
        return this.getDataWatcher().getWatchableObjectByte(22) == 1;
    }

    public void setHasDisplayTile(boolean p_94096_1_)
    {
        this.getDataWatcher().updateObject(22, Byte.valueOf((byte)(p_94096_1_ ? 1 : 0)));
    }

    /**
     * Sets the custom name tag for this entity
     */
    public void setCustomNameTag(String name)
    {
        this.entityName = name;
    }

    /**
     * Get the name of this object. For players this returns their username
     */
    public String getName()
    {
        return this.entityName != null ? this.entityName : super.getName();
    }

    /**
     * Returns true if this thing is named
     */
    public boolean hasCustomName()
    {
        return this.entityName != null;
    }

    public String getCustomNameTag()
    {
        return this.entityName;
    }

    /**
     * Get the formatted ChatComponent that will be used for the sender's username in chat
     */
    public IChatComponent getDisplayName()
    {
        if (this.hasCustomName())
        {
            ChatComponentText lvt_1_1_ = new ChatComponentText(this.entityName);
            lvt_1_1_.getChatStyle().setChatHoverEvent(this.getHoverEvent());
            lvt_1_1_.getChatStyle().setInsertion(this.getUniqueID().toString());
            return lvt_1_1_;
        }
        else
        {
            ChatComponentTranslation lvt_1_2_ = new ChatComponentTranslation(this.getName(), new Object[0]);
            lvt_1_2_.getChatStyle().setChatHoverEvent(this.getHoverEvent());
            lvt_1_2_.getChatStyle().setInsertion(this.getUniqueID().toString());
            return lvt_1_2_;
        }
    }

    public static enum EnumMinecartType
    {
        RIDEABLE(0, "MinecartRideable"),
        CHEST(1, "MinecartChest"),
        FURNACE(2, "MinecartFurnace"),
        TNT(3, "MinecartTNT"),
        SPAWNER(4, "MinecartSpawner"),
        HOPPER(5, "MinecartHopper"),
        COMMAND_BLOCK(6, "MinecartCommandBlock");

        private static final Map<Integer, EntityMinecart.EnumMinecartType> ID_LOOKUP = Maps.newHashMap();
        private final int networkID;
        private final String name;

        private EnumMinecartType(int networkID, String name)
        {
            this.networkID = networkID;
            this.name = name;
        }

        public int getNetworkID()
        {
            return this.networkID;
        }

        public String getName()
        {
            return this.name;
        }

        public static EntityMinecart.EnumMinecartType byNetworkID(int id)
        {
            EntityMinecart.EnumMinecartType lvt_1_1_ = (EntityMinecart.EnumMinecartType)ID_LOOKUP.get(Integer.valueOf(id));
            return lvt_1_1_ == null ? RIDEABLE : lvt_1_1_;
        }

        static {
            for (EntityMinecart.EnumMinecartType lvt_3_1_ : values())
            {
                ID_LOOKUP.put(Integer.valueOf(lvt_3_1_.getNetworkID()), lvt_3_1_);
            }
        }
    }
}
