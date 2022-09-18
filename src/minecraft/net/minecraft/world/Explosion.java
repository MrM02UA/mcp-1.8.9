package net.minecraft.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class Explosion
{
    /** whether or not the explosion sets fire to blocks around it */
    private final boolean isFlaming;

    /** whether or not this explosion spawns smoke particles */
    private final boolean isSmoking;
    private final Random explosionRNG;
    private final World worldObj;
    private final double explosionX;
    private final double explosionY;
    private final double explosionZ;
    private final Entity exploder;
    private final float explosionSize;
    private final List<BlockPos> affectedBlockPositions;
    private final Map<EntityPlayer, Vec3> playerKnockbackMap;

    public Explosion(World worldIn, Entity entityIn, double x, double y, double z, float size, List<BlockPos> affectedPositions)
    {
        this(worldIn, entityIn, x, y, z, size, false, true, affectedPositions);
    }

    public Explosion(World worldIn, Entity entityIn, double x, double y, double z, float size, boolean flaming, boolean smoking, List<BlockPos> affectedPositions)
    {
        this(worldIn, entityIn, x, y, z, size, flaming, smoking);
        this.affectedBlockPositions.addAll(affectedPositions);
    }

    public Explosion(World worldIn, Entity entityIn, double x, double y, double z, float size, boolean flaming, boolean smoking)
    {
        this.explosionRNG = new Random();
        this.affectedBlockPositions = Lists.newArrayList();
        this.playerKnockbackMap = Maps.newHashMap();
        this.worldObj = worldIn;
        this.exploder = entityIn;
        this.explosionSize = size;
        this.explosionX = x;
        this.explosionY = y;
        this.explosionZ = z;
        this.isFlaming = flaming;
        this.isSmoking = smoking;
    }

    /**
     * Does the first part of the explosion (destroy blocks)
     */
    public void doExplosionA()
    {
        Set<BlockPos> lvt_1_1_ = Sets.newHashSet();
        int lvt_2_1_ = 16;

        for (int lvt_3_1_ = 0; lvt_3_1_ < 16; ++lvt_3_1_)
        {
            for (int lvt_4_1_ = 0; lvt_4_1_ < 16; ++lvt_4_1_)
            {
                for (int lvt_5_1_ = 0; lvt_5_1_ < 16; ++lvt_5_1_)
                {
                    if (lvt_3_1_ == 0 || lvt_3_1_ == 15 || lvt_4_1_ == 0 || lvt_4_1_ == 15 || lvt_5_1_ == 0 || lvt_5_1_ == 15)
                    {
                        double lvt_6_1_ = (double)((float)lvt_3_1_ / 15.0F * 2.0F - 1.0F);
                        double lvt_8_1_ = (double)((float)lvt_4_1_ / 15.0F * 2.0F - 1.0F);
                        double lvt_10_1_ = (double)((float)lvt_5_1_ / 15.0F * 2.0F - 1.0F);
                        double lvt_12_1_ = Math.sqrt(lvt_6_1_ * lvt_6_1_ + lvt_8_1_ * lvt_8_1_ + lvt_10_1_ * lvt_10_1_);
                        lvt_6_1_ = lvt_6_1_ / lvt_12_1_;
                        lvt_8_1_ = lvt_8_1_ / lvt_12_1_;
                        lvt_10_1_ = lvt_10_1_ / lvt_12_1_;
                        float lvt_14_1_ = this.explosionSize * (0.7F + this.worldObj.rand.nextFloat() * 0.6F);
                        double lvt_15_1_ = this.explosionX;
                        double lvt_17_1_ = this.explosionY;
                        double lvt_19_1_ = this.explosionZ;

                        for (float lvt_21_1_ = 0.3F; lvt_14_1_ > 0.0F; lvt_14_1_ -= 0.22500001F)
                        {
                            BlockPos lvt_22_1_ = new BlockPos(lvt_15_1_, lvt_17_1_, lvt_19_1_);
                            IBlockState lvt_23_1_ = this.worldObj.getBlockState(lvt_22_1_);

                            if (lvt_23_1_.getBlock().getMaterial() != Material.air)
                            {
                                float lvt_24_1_ = this.exploder != null ? this.exploder.getExplosionResistance(this, this.worldObj, lvt_22_1_, lvt_23_1_) : lvt_23_1_.getBlock().getExplosionResistance((Entity)null);
                                lvt_14_1_ -= (lvt_24_1_ + 0.3F) * 0.3F;
                            }

                            if (lvt_14_1_ > 0.0F && (this.exploder == null || this.exploder.verifyExplosion(this, this.worldObj, lvt_22_1_, lvt_23_1_, lvt_14_1_)))
                            {
                                lvt_1_1_.add(lvt_22_1_);
                            }

                            lvt_15_1_ += lvt_6_1_ * 0.30000001192092896D;
                            lvt_17_1_ += lvt_8_1_ * 0.30000001192092896D;
                            lvt_19_1_ += lvt_10_1_ * 0.30000001192092896D;
                        }
                    }
                }
            }
        }

        this.affectedBlockPositions.addAll(lvt_1_1_);
        float lvt_3_2_ = this.explosionSize * 2.0F;
        int lvt_4_2_ = MathHelper.floor_double(this.explosionX - (double)lvt_3_2_ - 1.0D);
        int lvt_5_2_ = MathHelper.floor_double(this.explosionX + (double)lvt_3_2_ + 1.0D);
        int lvt_6_2_ = MathHelper.floor_double(this.explosionY - (double)lvt_3_2_ - 1.0D);
        int lvt_7_1_ = MathHelper.floor_double(this.explosionY + (double)lvt_3_2_ + 1.0D);
        int lvt_8_2_ = MathHelper.floor_double(this.explosionZ - (double)lvt_3_2_ - 1.0D);
        int lvt_9_1_ = MathHelper.floor_double(this.explosionZ + (double)lvt_3_2_ + 1.0D);
        List<Entity> lvt_10_2_ = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.exploder, new AxisAlignedBB((double)lvt_4_2_, (double)lvt_6_2_, (double)lvt_8_2_, (double)lvt_5_2_, (double)lvt_7_1_, (double)lvt_9_1_));
        Vec3 lvt_11_1_ = new Vec3(this.explosionX, this.explosionY, this.explosionZ);

        for (int lvt_12_2_ = 0; lvt_12_2_ < lvt_10_2_.size(); ++lvt_12_2_)
        {
            Entity lvt_13_1_ = (Entity)lvt_10_2_.get(lvt_12_2_);

            if (!lvt_13_1_.isImmuneToExplosions())
            {
                double lvt_14_2_ = lvt_13_1_.getDistance(this.explosionX, this.explosionY, this.explosionZ) / (double)lvt_3_2_;

                if (lvt_14_2_ <= 1.0D)
                {
                    double lvt_16_1_ = lvt_13_1_.posX - this.explosionX;
                    double lvt_18_1_ = lvt_13_1_.posY + (double)lvt_13_1_.getEyeHeight() - this.explosionY;
                    double lvt_20_1_ = lvt_13_1_.posZ - this.explosionZ;
                    double lvt_22_2_ = (double)MathHelper.sqrt_double(lvt_16_1_ * lvt_16_1_ + lvt_18_1_ * lvt_18_1_ + lvt_20_1_ * lvt_20_1_);

                    if (lvt_22_2_ != 0.0D)
                    {
                        lvt_16_1_ = lvt_16_1_ / lvt_22_2_;
                        lvt_18_1_ = lvt_18_1_ / lvt_22_2_;
                        lvt_20_1_ = lvt_20_1_ / lvt_22_2_;
                        double lvt_24_2_ = (double)this.worldObj.getBlockDensity(lvt_11_1_, lvt_13_1_.getEntityBoundingBox());
                        double lvt_26_1_ = (1.0D - lvt_14_2_) * lvt_24_2_;
                        lvt_13_1_.attackEntityFrom(DamageSource.setExplosionSource(this), (float)((int)((lvt_26_1_ * lvt_26_1_ + lvt_26_1_) / 2.0D * 8.0D * (double)lvt_3_2_ + 1.0D)));
                        double lvt_28_1_ = EnchantmentProtection.func_92092_a(lvt_13_1_, lvt_26_1_);
                        lvt_13_1_.motionX += lvt_16_1_ * lvt_28_1_;
                        lvt_13_1_.motionY += lvt_18_1_ * lvt_28_1_;
                        lvt_13_1_.motionZ += lvt_20_1_ * lvt_28_1_;

                        if (lvt_13_1_ instanceof EntityPlayer && !((EntityPlayer)lvt_13_1_).capabilities.disableDamage)
                        {
                            this.playerKnockbackMap.put((EntityPlayer)lvt_13_1_, new Vec3(lvt_16_1_ * lvt_26_1_, lvt_18_1_ * lvt_26_1_, lvt_20_1_ * lvt_26_1_));
                        }
                    }
                }
            }
        }
    }

    /**
     * Does the second part of the explosion (sound, particles, drop spawn)
     */
    public void doExplosionB(boolean spawnParticles)
    {
        this.worldObj.playSoundEffect(this.explosionX, this.explosionY, this.explosionZ, "random.explode", 4.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

        if (this.explosionSize >= 2.0F && this.isSmoking)
        {
            this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.explosionX, this.explosionY, this.explosionZ, 1.0D, 0.0D, 0.0D, new int[0]);
        }
        else
        {
            this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.explosionX, this.explosionY, this.explosionZ, 1.0D, 0.0D, 0.0D, new int[0]);
        }

        if (this.isSmoking)
        {
            for (BlockPos lvt_3_1_ : this.affectedBlockPositions)
            {
                Block lvt_4_1_ = this.worldObj.getBlockState(lvt_3_1_).getBlock();

                if (spawnParticles)
                {
                    double lvt_5_1_ = (double)((float)lvt_3_1_.getX() + this.worldObj.rand.nextFloat());
                    double lvt_7_1_ = (double)((float)lvt_3_1_.getY() + this.worldObj.rand.nextFloat());
                    double lvt_9_1_ = (double)((float)lvt_3_1_.getZ() + this.worldObj.rand.nextFloat());
                    double lvt_11_1_ = lvt_5_1_ - this.explosionX;
                    double lvt_13_1_ = lvt_7_1_ - this.explosionY;
                    double lvt_15_1_ = lvt_9_1_ - this.explosionZ;
                    double lvt_17_1_ = (double)MathHelper.sqrt_double(lvt_11_1_ * lvt_11_1_ + lvt_13_1_ * lvt_13_1_ + lvt_15_1_ * lvt_15_1_);
                    lvt_11_1_ = lvt_11_1_ / lvt_17_1_;
                    lvt_13_1_ = lvt_13_1_ / lvt_17_1_;
                    lvt_15_1_ = lvt_15_1_ / lvt_17_1_;
                    double lvt_19_1_ = 0.5D / (lvt_17_1_ / (double)this.explosionSize + 0.1D);
                    lvt_19_1_ = lvt_19_1_ * (double)(this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F);
                    lvt_11_1_ = lvt_11_1_ * lvt_19_1_;
                    lvt_13_1_ = lvt_13_1_ * lvt_19_1_;
                    lvt_15_1_ = lvt_15_1_ * lvt_19_1_;
                    this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, (lvt_5_1_ + this.explosionX * 1.0D) / 2.0D, (lvt_7_1_ + this.explosionY * 1.0D) / 2.0D, (lvt_9_1_ + this.explosionZ * 1.0D) / 2.0D, lvt_11_1_, lvt_13_1_, lvt_15_1_, new int[0]);
                    this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, lvt_5_1_, lvt_7_1_, lvt_9_1_, lvt_11_1_, lvt_13_1_, lvt_15_1_, new int[0]);
                }

                if (lvt_4_1_.getMaterial() != Material.air)
                {
                    if (lvt_4_1_.canDropFromExplosion(this))
                    {
                        lvt_4_1_.dropBlockAsItemWithChance(this.worldObj, lvt_3_1_, this.worldObj.getBlockState(lvt_3_1_), 1.0F / this.explosionSize, 0);
                    }

                    this.worldObj.setBlockState(lvt_3_1_, Blocks.air.getDefaultState(), 3);
                    lvt_4_1_.onBlockDestroyedByExplosion(this.worldObj, lvt_3_1_, this);
                }
            }
        }

        if (this.isFlaming)
        {
            for (BlockPos lvt_3_2_ : this.affectedBlockPositions)
            {
                if (this.worldObj.getBlockState(lvt_3_2_).getBlock().getMaterial() == Material.air && this.worldObj.getBlockState(lvt_3_2_.down()).getBlock().isFullBlock() && this.explosionRNG.nextInt(3) == 0)
                {
                    this.worldObj.setBlockState(lvt_3_2_, Blocks.fire.getDefaultState());
                }
            }
        }
    }

    public Map<EntityPlayer, Vec3> getPlayerKnockbackMap()
    {
        return this.playerKnockbackMap;
    }

    /**
     * Returns either the entity that placed the explosive block, the entity that caused the explosion or null.
     */
    public EntityLivingBase getExplosivePlacedBy()
    {
        return this.exploder == null ? null : (this.exploder instanceof EntityTNTPrimed ? ((EntityTNTPrimed)this.exploder).getTntPlacedBy() : (this.exploder instanceof EntityLivingBase ? (EntityLivingBase)this.exploder : null));
    }

    public void clearAffectedBlockPositions()
    {
        this.affectedBlockPositions.clear();
    }

    public List<BlockPos> getAffectedBlockPositions()
    {
        return this.affectedBlockPositions;
    }
}
