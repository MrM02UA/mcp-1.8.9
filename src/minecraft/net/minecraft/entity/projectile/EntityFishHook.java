package net.minecraft.entity.projectile;

import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomFishable;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityFishHook extends Entity
{
    private static final List<WeightedRandomFishable> JUNK = Arrays.asList(new WeightedRandomFishable[] {(new WeightedRandomFishable(new ItemStack(Items.leather_boots), 10)).setMaxDamagePercent(0.9F), new WeightedRandomFishable(new ItemStack(Items.leather), 10), new WeightedRandomFishable(new ItemStack(Items.bone), 10), new WeightedRandomFishable(new ItemStack(Items.potionitem), 10), new WeightedRandomFishable(new ItemStack(Items.string), 5), (new WeightedRandomFishable(new ItemStack(Items.fishing_rod), 2)).setMaxDamagePercent(0.9F), new WeightedRandomFishable(new ItemStack(Items.bowl), 10), new WeightedRandomFishable(new ItemStack(Items.stick), 5), new WeightedRandomFishable(new ItemStack(Items.dye, 10, EnumDyeColor.BLACK.getDyeDamage()), 1), new WeightedRandomFishable(new ItemStack(Blocks.tripwire_hook), 10), new WeightedRandomFishable(new ItemStack(Items.rotten_flesh), 10)});
    private static final List<WeightedRandomFishable> TREASURE = Arrays.asList(new WeightedRandomFishable[] {new WeightedRandomFishable(new ItemStack(Blocks.waterlily), 1), new WeightedRandomFishable(new ItemStack(Items.name_tag), 1), new WeightedRandomFishable(new ItemStack(Items.saddle), 1), (new WeightedRandomFishable(new ItemStack(Items.bow), 1)).setMaxDamagePercent(0.25F).setEnchantable(), (new WeightedRandomFishable(new ItemStack(Items.fishing_rod), 1)).setMaxDamagePercent(0.25F).setEnchantable(), (new WeightedRandomFishable(new ItemStack(Items.book), 1)).setEnchantable()});
    private static final List<WeightedRandomFishable> FISH = Arrays.asList(new WeightedRandomFishable[] {new WeightedRandomFishable(new ItemStack(Items.fish, 1, ItemFishFood.FishType.COD.getMetadata()), 60), new WeightedRandomFishable(new ItemStack(Items.fish, 1, ItemFishFood.FishType.SALMON.getMetadata()), 25), new WeightedRandomFishable(new ItemStack(Items.fish, 1, ItemFishFood.FishType.CLOWNFISH.getMetadata()), 2), new WeightedRandomFishable(new ItemStack(Items.fish, 1, ItemFishFood.FishType.PUFFERFISH.getMetadata()), 13)});
    private int xTile;
    private int yTile;
    private int zTile;
    private Block inTile;
    private boolean inGround;
    public int shake;
    public EntityPlayer angler;
    private int ticksInGround;
    private int ticksInAir;
    private int ticksCatchable;
    private int ticksCaughtDelay;
    private int ticksCatchableDelay;
    private float fishApproachAngle;
    public Entity caughtEntity;
    private int fishPosRotationIncrements;
    private double fishX;
    private double fishY;
    private double fishZ;
    private double fishYaw;
    private double fishPitch;
    private double clientMotionX;
    private double clientMotionY;
    private double clientMotionZ;

    public static List<WeightedRandomFishable> func_174855_j()
    {
        return FISH;
    }

    public EntityFishHook(World worldIn)
    {
        super(worldIn);
        this.xTile = -1;
        this.yTile = -1;
        this.zTile = -1;
        this.setSize(0.25F, 0.25F);
        this.ignoreFrustumCheck = true;
    }

    public EntityFishHook(World worldIn, double x, double y, double z, EntityPlayer anglerIn)
    {
        this(worldIn);
        this.setPosition(x, y, z);
        this.ignoreFrustumCheck = true;
        this.angler = anglerIn;
        anglerIn.fishEntity = this;
    }

    public EntityFishHook(World worldIn, EntityPlayer fishingPlayer)
    {
        super(worldIn);
        this.xTile = -1;
        this.yTile = -1;
        this.zTile = -1;
        this.ignoreFrustumCheck = true;
        this.angler = fishingPlayer;
        this.angler.fishEntity = this;
        this.setSize(0.25F, 0.25F);
        this.setLocationAndAngles(fishingPlayer.posX, fishingPlayer.posY + (double)fishingPlayer.getEyeHeight(), fishingPlayer.posZ, fishingPlayer.rotationYaw, fishingPlayer.rotationPitch);
        this.posX -= (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        this.posY -= 0.10000000149011612D;
        this.posZ -= (double)(MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        this.setPosition(this.posX, this.posY, this.posZ);
        float lvt_3_1_ = 0.4F;
        this.motionX = (double)(-MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * lvt_3_1_);
        this.motionZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI) * lvt_3_1_);
        this.motionY = (double)(-MathHelper.sin(this.rotationPitch / 180.0F * (float)Math.PI) * lvt_3_1_);
        this.handleHookCasting(this.motionX, this.motionY, this.motionZ, 1.5F, 1.0F);
    }

    protected void entityInit()
    {
    }

    /**
     * Checks if the entity is in range to render by using the past in distance and comparing it to its average edge
     * length * 64 * renderDistanceWeight Args: distance
     */
    public boolean isInRangeToRenderDist(double distance)
    {
        double lvt_3_1_ = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0D;

        if (Double.isNaN(lvt_3_1_))
        {
            lvt_3_1_ = 4.0D;
        }

        lvt_3_1_ = lvt_3_1_ * 64.0D;
        return distance < lvt_3_1_ * lvt_3_1_;
    }

    public void handleHookCasting(double p_146035_1_, double p_146035_3_, double p_146035_5_, float p_146035_7_, float p_146035_8_)
    {
        float lvt_9_1_ = MathHelper.sqrt_double(p_146035_1_ * p_146035_1_ + p_146035_3_ * p_146035_3_ + p_146035_5_ * p_146035_5_);
        p_146035_1_ = p_146035_1_ / (double)lvt_9_1_;
        p_146035_3_ = p_146035_3_ / (double)lvt_9_1_;
        p_146035_5_ = p_146035_5_ / (double)lvt_9_1_;
        p_146035_1_ = p_146035_1_ + this.rand.nextGaussian() * 0.007499999832361937D * (double)p_146035_8_;
        p_146035_3_ = p_146035_3_ + this.rand.nextGaussian() * 0.007499999832361937D * (double)p_146035_8_;
        p_146035_5_ = p_146035_5_ + this.rand.nextGaussian() * 0.007499999832361937D * (double)p_146035_8_;
        p_146035_1_ = p_146035_1_ * (double)p_146035_7_;
        p_146035_3_ = p_146035_3_ * (double)p_146035_7_;
        p_146035_5_ = p_146035_5_ * (double)p_146035_7_;
        this.motionX = p_146035_1_;
        this.motionY = p_146035_3_;
        this.motionZ = p_146035_5_;
        float lvt_10_1_ = MathHelper.sqrt_double(p_146035_1_ * p_146035_1_ + p_146035_5_ * p_146035_5_);
        this.prevRotationYaw = this.rotationYaw = (float)(MathHelper.atan2(p_146035_1_, p_146035_5_) * 180.0D / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float)(MathHelper.atan2(p_146035_3_, (double)lvt_10_1_) * 180.0D / Math.PI);
        this.ticksInGround = 0;
    }

    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean p_180426_10_)
    {
        this.fishX = x;
        this.fishY = y;
        this.fishZ = z;
        this.fishYaw = (double)yaw;
        this.fishPitch = (double)pitch;
        this.fishPosRotationIncrements = posRotationIncrements;
        this.motionX = this.clientMotionX;
        this.motionY = this.clientMotionY;
        this.motionZ = this.clientMotionZ;
    }

    /**
     * Sets the velocity to the args. Args: x, y, z
     */
    public void setVelocity(double x, double y, double z)
    {
        this.clientMotionX = this.motionX = x;
        this.clientMotionY = this.motionY = y;
        this.clientMotionZ = this.motionZ = z;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        super.onUpdate();

        if (this.fishPosRotationIncrements > 0)
        {
            double lvt_1_1_ = this.posX + (this.fishX - this.posX) / (double)this.fishPosRotationIncrements;
            double lvt_3_1_ = this.posY + (this.fishY - this.posY) / (double)this.fishPosRotationIncrements;
            double lvt_5_1_ = this.posZ + (this.fishZ - this.posZ) / (double)this.fishPosRotationIncrements;
            double lvt_7_1_ = MathHelper.wrapAngleTo180_double(this.fishYaw - (double)this.rotationYaw);
            this.rotationYaw = (float)((double)this.rotationYaw + lvt_7_1_ / (double)this.fishPosRotationIncrements);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.fishPitch - (double)this.rotationPitch) / (double)this.fishPosRotationIncrements);
            --this.fishPosRotationIncrements;
            this.setPosition(lvt_1_1_, lvt_3_1_, lvt_5_1_);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
        else
        {
            if (!this.worldObj.isRemote)
            {
                ItemStack lvt_1_2_ = this.angler.getCurrentEquippedItem();

                if (this.angler.isDead || !this.angler.isEntityAlive() || lvt_1_2_ == null || lvt_1_2_.getItem() != Items.fishing_rod || this.getDistanceSqToEntity(this.angler) > 1024.0D)
                {
                    this.setDead();
                    this.angler.fishEntity = null;
                    return;
                }

                if (this.caughtEntity != null)
                {
                    if (!this.caughtEntity.isDead)
                    {
                        this.posX = this.caughtEntity.posX;
                        double var10002 = (double)this.caughtEntity.height;
                        this.posY = this.caughtEntity.getEntityBoundingBox().minY + var10002 * 0.8D;
                        this.posZ = this.caughtEntity.posZ;
                        return;
                    }

                    this.caughtEntity = null;
                }
            }

            if (this.shake > 0)
            {
                --this.shake;
            }

            if (this.inGround)
            {
                if (this.worldObj.getBlockState(new BlockPos(this.xTile, this.yTile, this.zTile)).getBlock() == this.inTile)
                {
                    ++this.ticksInGround;

                    if (this.ticksInGround == 1200)
                    {
                        this.setDead();
                    }

                    return;
                }

                this.inGround = false;
                this.motionX *= (double)(this.rand.nextFloat() * 0.2F);
                this.motionY *= (double)(this.rand.nextFloat() * 0.2F);
                this.motionZ *= (double)(this.rand.nextFloat() * 0.2F);
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            }
            else
            {
                ++this.ticksInAir;
            }

            Vec3 lvt_1_3_ = new Vec3(this.posX, this.posY, this.posZ);
            Vec3 lvt_2_1_ = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            MovingObjectPosition lvt_3_2_ = this.worldObj.rayTraceBlocks(lvt_1_3_, lvt_2_1_);
            lvt_1_3_ = new Vec3(this.posX, this.posY, this.posZ);
            lvt_2_1_ = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

            if (lvt_3_2_ != null)
            {
                lvt_2_1_ = new Vec3(lvt_3_2_.hitVec.xCoord, lvt_3_2_.hitVec.yCoord, lvt_3_2_.hitVec.zCoord);
            }

            Entity lvt_4_1_ = null;
            List<Entity> lvt_5_2_ = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
            double lvt_6_1_ = 0.0D;

            for (int lvt_8_1_ = 0; lvt_8_1_ < lvt_5_2_.size(); ++lvt_8_1_)
            {
                Entity lvt_9_1_ = (Entity)lvt_5_2_.get(lvt_8_1_);

                if (lvt_9_1_.canBeCollidedWith() && (lvt_9_1_ != this.angler || this.ticksInAir >= 5))
                {
                    float lvt_10_1_ = 0.3F;
                    AxisAlignedBB lvt_11_1_ = lvt_9_1_.getEntityBoundingBox().expand((double)lvt_10_1_, (double)lvt_10_1_, (double)lvt_10_1_);
                    MovingObjectPosition lvt_12_1_ = lvt_11_1_.calculateIntercept(lvt_1_3_, lvt_2_1_);

                    if (lvt_12_1_ != null)
                    {
                        double lvt_13_1_ = lvt_1_3_.squareDistanceTo(lvt_12_1_.hitVec);

                        if (lvt_13_1_ < lvt_6_1_ || lvt_6_1_ == 0.0D)
                        {
                            lvt_4_1_ = lvt_9_1_;
                            lvt_6_1_ = lvt_13_1_;
                        }
                    }
                }
            }

            if (lvt_4_1_ != null)
            {
                lvt_3_2_ = new MovingObjectPosition(lvt_4_1_);
            }

            if (lvt_3_2_ != null)
            {
                if (lvt_3_2_.entityHit != null)
                {
                    if (lvt_3_2_.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.angler), 0.0F))
                    {
                        this.caughtEntity = lvt_3_2_.entityHit;
                    }
                }
                else
                {
                    this.inGround = true;
                }
            }

            if (!this.inGround)
            {
                this.moveEntity(this.motionX, this.motionY, this.motionZ);
                float lvt_8_2_ = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
                this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

                for (this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)lvt_8_2_) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
                {
                    ;
                }

                while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
                {
                    this.prevRotationPitch += 360.0F;
                }

                while (this.rotationYaw - this.prevRotationYaw < -180.0F)
                {
                    this.prevRotationYaw -= 360.0F;
                }

                while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
                {
                    this.prevRotationYaw += 360.0F;
                }

                this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
                this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
                float lvt_9_2_ = 0.92F;

                if (this.onGround || this.isCollidedHorizontally)
                {
                    lvt_9_2_ = 0.5F;
                }

                int lvt_10_2_ = 5;
                double lvt_11_2_ = 0.0D;

                for (int lvt_13_2_ = 0; lvt_13_2_ < lvt_10_2_; ++lvt_13_2_)
                {
                    AxisAlignedBB lvt_14_1_ = this.getEntityBoundingBox();
                    double lvt_15_1_ = lvt_14_1_.maxY - lvt_14_1_.minY;
                    double lvt_17_1_ = lvt_14_1_.minY + lvt_15_1_ * (double)lvt_13_2_ / (double)lvt_10_2_;
                    double lvt_19_1_ = lvt_14_1_.minY + lvt_15_1_ * (double)(lvt_13_2_ + 1) / (double)lvt_10_2_;
                    AxisAlignedBB lvt_21_1_ = new AxisAlignedBB(lvt_14_1_.minX, lvt_17_1_, lvt_14_1_.minZ, lvt_14_1_.maxX, lvt_19_1_, lvt_14_1_.maxZ);

                    if (this.worldObj.isAABBInMaterial(lvt_21_1_, Material.water))
                    {
                        lvt_11_2_ += 1.0D / (double)lvt_10_2_;
                    }
                }

                if (!this.worldObj.isRemote && lvt_11_2_ > 0.0D)
                {
                    WorldServer lvt_13_3_ = (WorldServer)this.worldObj;
                    int lvt_14_2_ = 1;
                    BlockPos lvt_15_2_ = (new BlockPos(this)).up();

                    if (this.rand.nextFloat() < 0.25F && this.worldObj.isRainingAt(lvt_15_2_))
                    {
                        lvt_14_2_ = 2;
                    }

                    if (this.rand.nextFloat() < 0.5F && !this.worldObj.canSeeSky(lvt_15_2_))
                    {
                        --lvt_14_2_;
                    }

                    if (this.ticksCatchable > 0)
                    {
                        --this.ticksCatchable;

                        if (this.ticksCatchable <= 0)
                        {
                            this.ticksCaughtDelay = 0;
                            this.ticksCatchableDelay = 0;
                        }
                    }
                    else if (this.ticksCatchableDelay > 0)
                    {
                        this.ticksCatchableDelay -= lvt_14_2_;

                        if (this.ticksCatchableDelay <= 0)
                        {
                            this.motionY -= 0.20000000298023224D;
                            this.playSound("random.splash", 0.25F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                            float lvt_16_1_ = (float)MathHelper.floor_double(this.getEntityBoundingBox().minY);
                            lvt_13_3_.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX, (double)(lvt_16_1_ + 1.0F), this.posZ, (int)(1.0F + this.width * 20.0F), (double)this.width, 0.0D, (double)this.width, 0.20000000298023224D, new int[0]);
                            lvt_13_3_.spawnParticle(EnumParticleTypes.WATER_WAKE, this.posX, (double)(lvt_16_1_ + 1.0F), this.posZ, (int)(1.0F + this.width * 20.0F), (double)this.width, 0.0D, (double)this.width, 0.20000000298023224D, new int[0]);
                            this.ticksCatchable = MathHelper.getRandomIntegerInRange(this.rand, 10, 30);
                        }
                        else
                        {
                            this.fishApproachAngle = (float)((double)this.fishApproachAngle + this.rand.nextGaussian() * 4.0D);
                            float lvt_16_2_ = this.fishApproachAngle * 0.017453292F;
                            float lvt_17_2_ = MathHelper.sin(lvt_16_2_);
                            float lvt_18_1_ = MathHelper.cos(lvt_16_2_);
                            double lvt_19_2_ = this.posX + (double)(lvt_17_2_ * (float)this.ticksCatchableDelay * 0.1F);
                            double lvt_21_2_ = (double)((float)MathHelper.floor_double(this.getEntityBoundingBox().minY) + 1.0F);
                            double lvt_23_1_ = this.posZ + (double)(lvt_18_1_ * (float)this.ticksCatchableDelay * 0.1F);
                            Block lvt_25_1_ = lvt_13_3_.getBlockState(new BlockPos((int)lvt_19_2_, (int)lvt_21_2_ - 1, (int)lvt_23_1_)).getBlock();

                            if (lvt_25_1_ == Blocks.water || lvt_25_1_ == Blocks.flowing_water)
                            {
                                if (this.rand.nextFloat() < 0.15F)
                                {
                                    lvt_13_3_.spawnParticle(EnumParticleTypes.WATER_BUBBLE, lvt_19_2_, lvt_21_2_ - 0.10000000149011612D, lvt_23_1_, 1, (double)lvt_17_2_, 0.1D, (double)lvt_18_1_, 0.0D, new int[0]);
                                }

                                float lvt_26_1_ = lvt_17_2_ * 0.04F;
                                float lvt_27_1_ = lvt_18_1_ * 0.04F;
                                lvt_13_3_.spawnParticle(EnumParticleTypes.WATER_WAKE, lvt_19_2_, lvt_21_2_, lvt_23_1_, 0, (double)lvt_27_1_, 0.01D, (double)(-lvt_26_1_), 1.0D, new int[0]);
                                lvt_13_3_.spawnParticle(EnumParticleTypes.WATER_WAKE, lvt_19_2_, lvt_21_2_, lvt_23_1_, 0, (double)(-lvt_27_1_), 0.01D, (double)lvt_26_1_, 1.0D, new int[0]);
                            }
                        }
                    }
                    else if (this.ticksCaughtDelay > 0)
                    {
                        this.ticksCaughtDelay -= lvt_14_2_;
                        float lvt_16_3_ = 0.15F;

                        if (this.ticksCaughtDelay < 20)
                        {
                            lvt_16_3_ = (float)((double)lvt_16_3_ + (double)(20 - this.ticksCaughtDelay) * 0.05D);
                        }
                        else if (this.ticksCaughtDelay < 40)
                        {
                            lvt_16_3_ = (float)((double)lvt_16_3_ + (double)(40 - this.ticksCaughtDelay) * 0.02D);
                        }
                        else if (this.ticksCaughtDelay < 60)
                        {
                            lvt_16_3_ = (float)((double)lvt_16_3_ + (double)(60 - this.ticksCaughtDelay) * 0.01D);
                        }

                        if (this.rand.nextFloat() < lvt_16_3_)
                        {
                            float lvt_17_3_ = MathHelper.randomFloatClamp(this.rand, 0.0F, 360.0F) * 0.017453292F;
                            float lvt_18_2_ = MathHelper.randomFloatClamp(this.rand, 25.0F, 60.0F);
                            double lvt_19_3_ = this.posX + (double)(MathHelper.sin(lvt_17_3_) * lvt_18_2_ * 0.1F);
                            double lvt_21_3_ = (double)((float)MathHelper.floor_double(this.getEntityBoundingBox().minY) + 1.0F);
                            double lvt_23_2_ = this.posZ + (double)(MathHelper.cos(lvt_17_3_) * lvt_18_2_ * 0.1F);
                            Block lvt_25_2_ = lvt_13_3_.getBlockState(new BlockPos((int)lvt_19_3_, (int)lvt_21_3_ - 1, (int)lvt_23_2_)).getBlock();

                            if (lvt_25_2_ == Blocks.water || lvt_25_2_ == Blocks.flowing_water)
                            {
                                lvt_13_3_.spawnParticle(EnumParticleTypes.WATER_SPLASH, lvt_19_3_, lvt_21_3_, lvt_23_2_, 2 + this.rand.nextInt(2), 0.10000000149011612D, 0.0D, 0.10000000149011612D, 0.0D, new int[0]);
                            }
                        }

                        if (this.ticksCaughtDelay <= 0)
                        {
                            this.fishApproachAngle = MathHelper.randomFloatClamp(this.rand, 0.0F, 360.0F);
                            this.ticksCatchableDelay = MathHelper.getRandomIntegerInRange(this.rand, 20, 80);
                        }
                    }
                    else
                    {
                        this.ticksCaughtDelay = MathHelper.getRandomIntegerInRange(this.rand, 100, 900);
                        this.ticksCaughtDelay -= EnchantmentHelper.getLureModifier(this.angler) * 20 * 5;
                    }

                    if (this.ticksCatchable > 0)
                    {
                        this.motionY -= (double)(this.rand.nextFloat() * this.rand.nextFloat() * this.rand.nextFloat()) * 0.2D;
                    }
                }

                double lvt_13_4_ = lvt_11_2_ * 2.0D - 1.0D;
                this.motionY += 0.03999999910593033D * lvt_13_4_;

                if (lvt_11_2_ > 0.0D)
                {
                    lvt_9_2_ = (float)((double)lvt_9_2_ * 0.9D);
                    this.motionY *= 0.8D;
                }

                this.motionX *= (double)lvt_9_2_;
                this.motionY *= (double)lvt_9_2_;
                this.motionZ *= (double)lvt_9_2_;
                this.setPosition(this.posX, this.posY, this.posZ);
            }
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        tagCompound.setShort("xTile", (short)this.xTile);
        tagCompound.setShort("yTile", (short)this.yTile);
        tagCompound.setShort("zTile", (short)this.zTile);
        ResourceLocation lvt_2_1_ = (ResourceLocation)Block.blockRegistry.getNameForObject(this.inTile);
        tagCompound.setString("inTile", lvt_2_1_ == null ? "" : lvt_2_1_.toString());
        tagCompound.setByte("shake", (byte)this.shake);
        tagCompound.setByte("inGround", (byte)(this.inGround ? 1 : 0));
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        this.xTile = tagCompund.getShort("xTile");
        this.yTile = tagCompund.getShort("yTile");
        this.zTile = tagCompund.getShort("zTile");

        if (tagCompund.hasKey("inTile", 8))
        {
            this.inTile = Block.getBlockFromName(tagCompund.getString("inTile"));
        }
        else
        {
            this.inTile = Block.getBlockById(tagCompund.getByte("inTile") & 255);
        }

        this.shake = tagCompund.getByte("shake") & 255;
        this.inGround = tagCompund.getByte("inGround") == 1;
    }

    public int handleHookRetraction()
    {
        if (this.worldObj.isRemote)
        {
            return 0;
        }
        else
        {
            int lvt_1_1_ = 0;

            if (this.caughtEntity != null)
            {
                double lvt_2_1_ = this.angler.posX - this.posX;
                double lvt_4_1_ = this.angler.posY - this.posY;
                double lvt_6_1_ = this.angler.posZ - this.posZ;
                double lvt_8_1_ = (double)MathHelper.sqrt_double(lvt_2_1_ * lvt_2_1_ + lvt_4_1_ * lvt_4_1_ + lvt_6_1_ * lvt_6_1_);
                double lvt_10_1_ = 0.1D;
                this.caughtEntity.motionX += lvt_2_1_ * lvt_10_1_;
                this.caughtEntity.motionY += lvt_4_1_ * lvt_10_1_ + (double)MathHelper.sqrt_double(lvt_8_1_) * 0.08D;
                this.caughtEntity.motionZ += lvt_6_1_ * lvt_10_1_;
                lvt_1_1_ = 3;
            }
            else if (this.ticksCatchable > 0)
            {
                EntityItem lvt_2_2_ = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, this.getFishingResult());
                double lvt_3_1_ = this.angler.posX - this.posX;
                double lvt_5_1_ = this.angler.posY - this.posY;
                double lvt_7_1_ = this.angler.posZ - this.posZ;
                double lvt_9_1_ = (double)MathHelper.sqrt_double(lvt_3_1_ * lvt_3_1_ + lvt_5_1_ * lvt_5_1_ + lvt_7_1_ * lvt_7_1_);
                double lvt_11_1_ = 0.1D;
                lvt_2_2_.motionX = lvt_3_1_ * lvt_11_1_;
                lvt_2_2_.motionY = lvt_5_1_ * lvt_11_1_ + (double)MathHelper.sqrt_double(lvt_9_1_) * 0.08D;
                lvt_2_2_.motionZ = lvt_7_1_ * lvt_11_1_;
                this.worldObj.spawnEntityInWorld(lvt_2_2_);
                this.angler.worldObj.spawnEntityInWorld(new EntityXPOrb(this.angler.worldObj, this.angler.posX, this.angler.posY + 0.5D, this.angler.posZ + 0.5D, this.rand.nextInt(6) + 1));
                lvt_1_1_ = 1;
            }

            if (this.inGround)
            {
                lvt_1_1_ = 2;
            }

            this.setDead();
            this.angler.fishEntity = null;
            return lvt_1_1_;
        }
    }

    private ItemStack getFishingResult()
    {
        float lvt_1_1_ = this.worldObj.rand.nextFloat();
        int lvt_2_1_ = EnchantmentHelper.getLuckOfSeaModifier(this.angler);
        int lvt_3_1_ = EnchantmentHelper.getLureModifier(this.angler);
        float lvt_4_1_ = 0.1F - (float)lvt_2_1_ * 0.025F - (float)lvt_3_1_ * 0.01F;
        float lvt_5_1_ = 0.05F + (float)lvt_2_1_ * 0.01F - (float)lvt_3_1_ * 0.01F;
        lvt_4_1_ = MathHelper.clamp_float(lvt_4_1_, 0.0F, 1.0F);
        lvt_5_1_ = MathHelper.clamp_float(lvt_5_1_, 0.0F, 1.0F);

        if (lvt_1_1_ < lvt_4_1_)
        {
            this.angler.triggerAchievement(StatList.junkFishedStat);
            return ((WeightedRandomFishable)WeightedRandom.getRandomItem(this.rand, JUNK)).getItemStack(this.rand);
        }
        else
        {
            lvt_1_1_ = lvt_1_1_ - lvt_4_1_;

            if (lvt_1_1_ < lvt_5_1_)
            {
                this.angler.triggerAchievement(StatList.treasureFishedStat);
                return ((WeightedRandomFishable)WeightedRandom.getRandomItem(this.rand, TREASURE)).getItemStack(this.rand);
            }
            else
            {
                float var10000 = lvt_1_1_ - lvt_5_1_;
                this.angler.triggerAchievement(StatList.fishCaughtStat);
                return ((WeightedRandomFishable)WeightedRandom.getRandomItem(this.rand, FISH)).getItemStack(this.rand);
            }
        }
    }

    /**
     * Will get destroyed next tick.
     */
    public void setDead()
    {
        super.setDead();

        if (this.angler != null)
        {
            this.angler.fishEntity = null;
        }
    }
}
