package net.minecraft.client.particle;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EffectRenderer
{
    private static final ResourceLocation particleTextures = new ResourceLocation("textures/particle/particles.png");

    /** Reference to the World object. */
    protected World worldObj;
    private List<EntityFX>[][] fxLayers = new List[4][];
    private List<EntityParticleEmitter> particleEmitters = Lists.newArrayList();
    private TextureManager renderer;

    /** RNG. */
    private Random rand = new Random();
    private Map<Integer, IParticleFactory> particleTypes = Maps.newHashMap();

    public EffectRenderer(World worldIn, TextureManager rendererIn)
    {
        this.worldObj = worldIn;
        this.renderer = rendererIn;

        for (int lvt_3_1_ = 0; lvt_3_1_ < 4; ++lvt_3_1_)
        {
            this.fxLayers[lvt_3_1_] = new List[2];

            for (int lvt_4_1_ = 0; lvt_4_1_ < 2; ++lvt_4_1_)
            {
                this.fxLayers[lvt_3_1_][lvt_4_1_] = Lists.newArrayList();
            }
        }

        this.registerVanillaParticles();
    }

    private void registerVanillaParticles()
    {
        this.registerParticle(EnumParticleTypes.EXPLOSION_NORMAL.getParticleID(), new EntityExplodeFX.Factory());
        this.registerParticle(EnumParticleTypes.WATER_BUBBLE.getParticleID(), new EntityBubbleFX.Factory());
        this.registerParticle(EnumParticleTypes.WATER_SPLASH.getParticleID(), new EntitySplashFX.Factory());
        this.registerParticle(EnumParticleTypes.WATER_WAKE.getParticleID(), new EntityFishWakeFX.Factory());
        this.registerParticle(EnumParticleTypes.WATER_DROP.getParticleID(), new EntityRainFX.Factory());
        this.registerParticle(EnumParticleTypes.SUSPENDED.getParticleID(), new EntitySuspendFX.Factory());
        this.registerParticle(EnumParticleTypes.SUSPENDED_DEPTH.getParticleID(), new EntityAuraFX.Factory());
        this.registerParticle(EnumParticleTypes.CRIT.getParticleID(), new EntityCrit2FX.Factory());
        this.registerParticle(EnumParticleTypes.CRIT_MAGIC.getParticleID(), new EntityCrit2FX.MagicFactory());
        this.registerParticle(EnumParticleTypes.SMOKE_NORMAL.getParticleID(), new EntitySmokeFX.Factory());
        this.registerParticle(EnumParticleTypes.SMOKE_LARGE.getParticleID(), new EntityCritFX.Factory());
        this.registerParticle(EnumParticleTypes.SPELL.getParticleID(), new EntitySpellParticleFX.Factory());
        this.registerParticle(EnumParticleTypes.SPELL_INSTANT.getParticleID(), new EntitySpellParticleFX.InstantFactory());
        this.registerParticle(EnumParticleTypes.SPELL_MOB.getParticleID(), new EntitySpellParticleFX.MobFactory());
        this.registerParticle(EnumParticleTypes.SPELL_MOB_AMBIENT.getParticleID(), new EntitySpellParticleFX.AmbientMobFactory());
        this.registerParticle(EnumParticleTypes.SPELL_WITCH.getParticleID(), new EntitySpellParticleFX.WitchFactory());
        this.registerParticle(EnumParticleTypes.DRIP_WATER.getParticleID(), new EntityDropParticleFX.WaterFactory());
        this.registerParticle(EnumParticleTypes.DRIP_LAVA.getParticleID(), new EntityDropParticleFX.LavaFactory());
        this.registerParticle(EnumParticleTypes.VILLAGER_ANGRY.getParticleID(), new EntityHeartFX.AngryVillagerFactory());
        this.registerParticle(EnumParticleTypes.VILLAGER_HAPPY.getParticleID(), new EntityAuraFX.HappyVillagerFactory());
        this.registerParticle(EnumParticleTypes.TOWN_AURA.getParticleID(), new EntityAuraFX.Factory());
        this.registerParticle(EnumParticleTypes.NOTE.getParticleID(), new EntityNoteFX.Factory());
        this.registerParticle(EnumParticleTypes.PORTAL.getParticleID(), new EntityPortalFX.Factory());
        this.registerParticle(EnumParticleTypes.ENCHANTMENT_TABLE.getParticleID(), new EntityEnchantmentTableParticleFX.EnchantmentTable());
        this.registerParticle(EnumParticleTypes.FLAME.getParticleID(), new EntityFlameFX.Factory());
        this.registerParticle(EnumParticleTypes.LAVA.getParticleID(), new EntityLavaFX.Factory());
        this.registerParticle(EnumParticleTypes.FOOTSTEP.getParticleID(), new EntityFootStepFX.Factory());
        this.registerParticle(EnumParticleTypes.CLOUD.getParticleID(), new EntityCloudFX.Factory());
        this.registerParticle(EnumParticleTypes.REDSTONE.getParticleID(), new EntityReddustFX.Factory());
        this.registerParticle(EnumParticleTypes.SNOWBALL.getParticleID(), new EntityBreakingFX.SnowballFactory());
        this.registerParticle(EnumParticleTypes.SNOW_SHOVEL.getParticleID(), new EntitySnowShovelFX.Factory());
        this.registerParticle(EnumParticleTypes.SLIME.getParticleID(), new EntityBreakingFX.SlimeFactory());
        this.registerParticle(EnumParticleTypes.HEART.getParticleID(), new EntityHeartFX.Factory());
        this.registerParticle(EnumParticleTypes.BARRIER.getParticleID(), new Barrier.Factory());
        this.registerParticle(EnumParticleTypes.ITEM_CRACK.getParticleID(), new EntityBreakingFX.Factory());
        this.registerParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(), new EntityDiggingFX.Factory());
        this.registerParticle(EnumParticleTypes.BLOCK_DUST.getParticleID(), new EntityBlockDustFX.Factory());
        this.registerParticle(EnumParticleTypes.EXPLOSION_HUGE.getParticleID(), new EntityHugeExplodeFX.Factory());
        this.registerParticle(EnumParticleTypes.EXPLOSION_LARGE.getParticleID(), new EntityLargeExplodeFX.Factory());
        this.registerParticle(EnumParticleTypes.FIREWORKS_SPARK.getParticleID(), new EntityFirework.Factory());
        this.registerParticle(EnumParticleTypes.MOB_APPEARANCE.getParticleID(), new MobAppearance.Factory());
    }

    public void registerParticle(int id, IParticleFactory particleFactory)
    {
        this.particleTypes.put(Integer.valueOf(id), particleFactory);
    }

    public void emitParticleAtEntity(Entity entityIn, EnumParticleTypes particleTypes)
    {
        this.particleEmitters.add(new EntityParticleEmitter(this.worldObj, entityIn, particleTypes));
    }

    /**
     * Spawns the relevant particle according to the particle id.
     *  
     * @param xCoord X position of the particle
     * @param yCoord Y position of the particle
     * @param zCoord Z position of the particle
     * @param xSpeed X speed of the particle
     * @param ySpeed Y speed of the particle
     * @param zSpeed Z speed of the particle
     * @param parameters Parameters for the particle (color for redstone, ...)
     */
    public EntityFX spawnEffectParticle(int particleId, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {
        IParticleFactory lvt_15_1_ = (IParticleFactory)this.particleTypes.get(Integer.valueOf(particleId));

        if (lvt_15_1_ != null)
        {
            EntityFX lvt_16_1_ = lvt_15_1_.getEntityFX(particleId, this.worldObj, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);

            if (lvt_16_1_ != null)
            {
                this.addEffect(lvt_16_1_);
                return lvt_16_1_;
            }
        }

        return null;
    }

    public void addEffect(EntityFX effect)
    {
        int lvt_2_1_ = effect.getFXLayer();
        int lvt_3_1_ = effect.getAlpha() != 1.0F ? 0 : 1;

        if (this.fxLayers[lvt_2_1_][lvt_3_1_].size() >= 4000)
        {
            this.fxLayers[lvt_2_1_][lvt_3_1_].remove(0);
        }

        this.fxLayers[lvt_2_1_][lvt_3_1_].add(effect);
    }

    public void updateEffects()
    {
        for (int lvt_1_1_ = 0; lvt_1_1_ < 4; ++lvt_1_1_)
        {
            this.updateEffectLayer(lvt_1_1_);
        }

        List<EntityParticleEmitter> lvt_1_2_ = Lists.newArrayList();

        for (EntityParticleEmitter lvt_3_1_ : this.particleEmitters)
        {
            lvt_3_1_.onUpdate();

            if (lvt_3_1_.isDead)
            {
                lvt_1_2_.add(lvt_3_1_);
            }
        }

        this.particleEmitters.removeAll(lvt_1_2_);
    }

    private void updateEffectLayer(int layer)
    {
        for (int lvt_2_1_ = 0; lvt_2_1_ < 2; ++lvt_2_1_)
        {
            this.updateEffectAlphaLayer(this.fxLayers[layer][lvt_2_1_]);
        }
    }

    private void updateEffectAlphaLayer(List<EntityFX> entitiesFX)
    {
        List<EntityFX> lvt_2_1_ = Lists.newArrayList();

        for (int lvt_3_1_ = 0; lvt_3_1_ < entitiesFX.size(); ++lvt_3_1_)
        {
            EntityFX lvt_4_1_ = (EntityFX)entitiesFX.get(lvt_3_1_);
            this.tickParticle(lvt_4_1_);

            if (lvt_4_1_.isDead)
            {
                lvt_2_1_.add(lvt_4_1_);
            }
        }

        entitiesFX.removeAll(lvt_2_1_);
    }

    private void tickParticle(final EntityFX particle)
    {
        try
        {
            particle.onUpdate();
        }
        catch (Throwable var6)
        {
            CrashReport lvt_3_1_ = CrashReport.makeCrashReport(var6, "Ticking Particle");
            CrashReportCategory lvt_4_1_ = lvt_3_1_.makeCategory("Particle being ticked");
            final int lvt_5_1_ = particle.getFXLayer();
            lvt_4_1_.addCrashSectionCallable("Particle", new Callable<String>()
            {
                public String call() throws Exception
                {
                    return particle.toString();
                }
                public Object call() throws Exception
                {
                    return this.call();
                }
            });
            lvt_4_1_.addCrashSectionCallable("Particle Type", new Callable<String>()
            {
                public String call() throws Exception
                {
                    return lvt_5_1_ == 0 ? "MISC_TEXTURE" : (lvt_5_1_ == 1 ? "TERRAIN_TEXTURE" : (lvt_5_1_ == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + lvt_5_1_));
                }
                public Object call() throws Exception
                {
                    return this.call();
                }
            });
            throw new ReportedException(lvt_3_1_);
        }
    }

    /**
     * Renders all current particles. Args player, partialTickTime
     */
    public void renderParticles(Entity entityIn, float partialTicks)
    {
        float lvt_3_1_ = ActiveRenderInfo.getRotationX();
        float lvt_4_1_ = ActiveRenderInfo.getRotationZ();
        float lvt_5_1_ = ActiveRenderInfo.getRotationYZ();
        float lvt_6_1_ = ActiveRenderInfo.getRotationXY();
        float lvt_7_1_ = ActiveRenderInfo.getRotationXZ();
        EntityFX.interpPosX = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double)partialTicks;
        EntityFX.interpPosY = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double)partialTicks;
        EntityFX.interpPosZ = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double)partialTicks;
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.alphaFunc(516, 0.003921569F);

        for (final int lvt_8_1_ = 0; lvt_8_1_ < 3; ++lvt_8_1_)
        {
            for (int lvt_9_1_ = 0; lvt_9_1_ < 2; ++lvt_9_1_)
            {
                if (!this.fxLayers[lvt_8_1_][lvt_9_1_].isEmpty())
                {
                    switch (lvt_9_1_)
                    {
                        case 0:
                            GlStateManager.depthMask(false);
                            break;

                        case 1:
                            GlStateManager.depthMask(true);
                    }

                    switch (lvt_8_1_)
                    {
                        case 0:
                        default:
                            this.renderer.bindTexture(particleTextures);
                            break;

                        case 1:
                            this.renderer.bindTexture(TextureMap.locationBlocksTexture);
                    }

                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    Tessellator lvt_10_1_ = Tessellator.getInstance();
                    WorldRenderer lvt_11_1_ = lvt_10_1_.getWorldRenderer();
                    lvt_11_1_.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

                    for (int lvt_12_1_ = 0; lvt_12_1_ < this.fxLayers[lvt_8_1_][lvt_9_1_].size(); ++lvt_12_1_)
                    {
                        final EntityFX lvt_13_1_ = (EntityFX)this.fxLayers[lvt_8_1_][lvt_9_1_].get(lvt_12_1_);

                        try
                        {
                            lvt_13_1_.renderParticle(lvt_11_1_, entityIn, partialTicks, lvt_3_1_, lvt_7_1_, lvt_4_1_, lvt_5_1_, lvt_6_1_);
                        }
                        catch (Throwable var18)
                        {
                            CrashReport lvt_15_1_ = CrashReport.makeCrashReport(var18, "Rendering Particle");
                            CrashReportCategory lvt_16_1_ = lvt_15_1_.makeCategory("Particle being rendered");
                            lvt_16_1_.addCrashSectionCallable("Particle", new Callable<String>()
                            {
                                public String call() throws Exception
                                {
                                    return lvt_13_1_.toString();
                                }
                                public Object call() throws Exception
                                {
                                    return this.call();
                                }
                            });
                            lvt_16_1_.addCrashSectionCallable("Particle Type", new Callable<String>()
                            {
                                public String call() throws Exception
                                {
                                    return lvt_8_1_ == 0 ? "MISC_TEXTURE" : (lvt_8_1_ == 1 ? "TERRAIN_TEXTURE" : (lvt_8_1_ == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + lvt_8_1_));
                                }
                                public Object call() throws Exception
                                {
                                    return this.call();
                                }
                            });
                            throw new ReportedException(lvt_15_1_);
                        }
                    }

                    lvt_10_1_.draw();
                }
            }
        }

        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1F);
    }

    public void renderLitParticles(Entity entityIn, float partialTick)
    {
        float lvt_3_1_ = 0.017453292F;
        float lvt_4_1_ = MathHelper.cos(entityIn.rotationYaw * 0.017453292F);
        float lvt_5_1_ = MathHelper.sin(entityIn.rotationYaw * 0.017453292F);
        float lvt_6_1_ = -lvt_5_1_ * MathHelper.sin(entityIn.rotationPitch * 0.017453292F);
        float lvt_7_1_ = lvt_4_1_ * MathHelper.sin(entityIn.rotationPitch * 0.017453292F);
        float lvt_8_1_ = MathHelper.cos(entityIn.rotationPitch * 0.017453292F);

        for (int lvt_9_1_ = 0; lvt_9_1_ < 2; ++lvt_9_1_)
        {
            List<EntityFX> lvt_10_1_ = this.fxLayers[3][lvt_9_1_];

            if (!lvt_10_1_.isEmpty())
            {
                Tessellator lvt_11_1_ = Tessellator.getInstance();
                WorldRenderer lvt_12_1_ = lvt_11_1_.getWorldRenderer();

                for (int lvt_13_1_ = 0; lvt_13_1_ < lvt_10_1_.size(); ++lvt_13_1_)
                {
                    EntityFX lvt_14_1_ = (EntityFX)lvt_10_1_.get(lvt_13_1_);
                    lvt_14_1_.renderParticle(lvt_12_1_, entityIn, partialTick, lvt_4_1_, lvt_8_1_, lvt_5_1_, lvt_6_1_, lvt_7_1_);
                }
            }
        }
    }

    public void clearEffects(World worldIn)
    {
        this.worldObj = worldIn;

        for (int lvt_2_1_ = 0; lvt_2_1_ < 4; ++lvt_2_1_)
        {
            for (int lvt_3_1_ = 0; lvt_3_1_ < 2; ++lvt_3_1_)
            {
                this.fxLayers[lvt_2_1_][lvt_3_1_].clear();
            }
        }

        this.particleEmitters.clear();
    }

    public void addBlockDestroyEffects(BlockPos pos, IBlockState state)
    {
        if (state.getBlock().getMaterial() != Material.air)
        {
            state = state.getBlock().getActualState(state, this.worldObj, pos);
            int lvt_3_1_ = 4;

            for (int lvt_4_1_ = 0; lvt_4_1_ < lvt_3_1_; ++lvt_4_1_)
            {
                for (int lvt_5_1_ = 0; lvt_5_1_ < lvt_3_1_; ++lvt_5_1_)
                {
                    for (int lvt_6_1_ = 0; lvt_6_1_ < lvt_3_1_; ++lvt_6_1_)
                    {
                        double lvt_7_1_ = (double)pos.getX() + ((double)lvt_4_1_ + 0.5D) / (double)lvt_3_1_;
                        double lvt_9_1_ = (double)pos.getY() + ((double)lvt_5_1_ + 0.5D) / (double)lvt_3_1_;
                        double lvt_11_1_ = (double)pos.getZ() + ((double)lvt_6_1_ + 0.5D) / (double)lvt_3_1_;
                        this.addEffect((new EntityDiggingFX(this.worldObj, lvt_7_1_, lvt_9_1_, lvt_11_1_, lvt_7_1_ - (double)pos.getX() - 0.5D, lvt_9_1_ - (double)pos.getY() - 0.5D, lvt_11_1_ - (double)pos.getZ() - 0.5D, state)).setBlockPos(pos));
                    }
                }
            }
        }
    }

    /**
     * Adds block hit particles for the specified block
     */
    public void addBlockHitEffects(BlockPos pos, EnumFacing side)
    {
        IBlockState lvt_3_1_ = this.worldObj.getBlockState(pos);
        Block lvt_4_1_ = lvt_3_1_.getBlock();

        if (lvt_4_1_.getRenderType() != -1)
        {
            int lvt_5_1_ = pos.getX();
            int lvt_6_1_ = pos.getY();
            int lvt_7_1_ = pos.getZ();
            float lvt_8_1_ = 0.1F;
            double lvt_9_1_ = (double)lvt_5_1_ + this.rand.nextDouble() * (lvt_4_1_.getBlockBoundsMaxX() - lvt_4_1_.getBlockBoundsMinX() - (double)(lvt_8_1_ * 2.0F)) + (double)lvt_8_1_ + lvt_4_1_.getBlockBoundsMinX();
            double lvt_11_1_ = (double)lvt_6_1_ + this.rand.nextDouble() * (lvt_4_1_.getBlockBoundsMaxY() - lvt_4_1_.getBlockBoundsMinY() - (double)(lvt_8_1_ * 2.0F)) + (double)lvt_8_1_ + lvt_4_1_.getBlockBoundsMinY();
            double lvt_13_1_ = (double)lvt_7_1_ + this.rand.nextDouble() * (lvt_4_1_.getBlockBoundsMaxZ() - lvt_4_1_.getBlockBoundsMinZ() - (double)(lvt_8_1_ * 2.0F)) + (double)lvt_8_1_ + lvt_4_1_.getBlockBoundsMinZ();

            if (side == EnumFacing.DOWN)
            {
                lvt_11_1_ = (double)lvt_6_1_ + lvt_4_1_.getBlockBoundsMinY() - (double)lvt_8_1_;
            }

            if (side == EnumFacing.UP)
            {
                lvt_11_1_ = (double)lvt_6_1_ + lvt_4_1_.getBlockBoundsMaxY() + (double)lvt_8_1_;
            }

            if (side == EnumFacing.NORTH)
            {
                lvt_13_1_ = (double)lvt_7_1_ + lvt_4_1_.getBlockBoundsMinZ() - (double)lvt_8_1_;
            }

            if (side == EnumFacing.SOUTH)
            {
                lvt_13_1_ = (double)lvt_7_1_ + lvt_4_1_.getBlockBoundsMaxZ() + (double)lvt_8_1_;
            }

            if (side == EnumFacing.WEST)
            {
                lvt_9_1_ = (double)lvt_5_1_ + lvt_4_1_.getBlockBoundsMinX() - (double)lvt_8_1_;
            }

            if (side == EnumFacing.EAST)
            {
                lvt_9_1_ = (double)lvt_5_1_ + lvt_4_1_.getBlockBoundsMaxX() + (double)lvt_8_1_;
            }

            this.addEffect((new EntityDiggingFX(this.worldObj, lvt_9_1_, lvt_11_1_, lvt_13_1_, 0.0D, 0.0D, 0.0D, lvt_3_1_)).setBlockPos(pos).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
        }
    }

    public void moveToAlphaLayer(EntityFX effect)
    {
        this.moveToLayer(effect, 1, 0);
    }

    public void moveToNoAlphaLayer(EntityFX effect)
    {
        this.moveToLayer(effect, 0, 1);
    }

    private void moveToLayer(EntityFX effect, int layerFrom, int layerTo)
    {
        for (int lvt_4_1_ = 0; lvt_4_1_ < 4; ++lvt_4_1_)
        {
            if (this.fxLayers[lvt_4_1_][layerFrom].contains(effect))
            {
                this.fxLayers[lvt_4_1_][layerFrom].remove(effect);
                this.fxLayers[lvt_4_1_][layerTo].add(effect);
            }
        }
    }

    public String getStatistics()
    {
        int lvt_1_1_ = 0;

        for (int lvt_2_1_ = 0; lvt_2_1_ < 4; ++lvt_2_1_)
        {
            for (int lvt_3_1_ = 0; lvt_3_1_ < 2; ++lvt_3_1_)
            {
                lvt_1_1_ += this.fxLayers[lvt_2_1_][lvt_3_1_].size();
            }
        }

        return "" + lvt_1_1_;
    }
}
