package net.minecraft.client.renderer;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MouseFilter;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.BiomeGenBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

public class EntityRenderer implements IResourceManagerReloadListener
{
    private static final Logger logger = LogManager.getLogger();
    private static final ResourceLocation locationRainPng = new ResourceLocation("textures/environment/rain.png");
    private static final ResourceLocation locationSnowPng = new ResourceLocation("textures/environment/snow.png");
    public static boolean anaglyphEnable;

    /** Anaglyph field (0=R, 1=GB) */
    public static int anaglyphField;

    /** A reference to the Minecraft object. */
    private Minecraft mc;
    private final IResourceManager resourceManager;
    private Random random = new Random();
    private float farPlaneDistance;
    public final ItemRenderer itemRenderer;
    private final MapItemRenderer theMapItemRenderer;

    /** Entity renderer update count */
    private int rendererUpdateCount;

    /** Pointed entity */
    private Entity pointedEntity;
    private MouseFilter mouseFilterXAxis = new MouseFilter();
    private MouseFilter mouseFilterYAxis = new MouseFilter();
    private float thirdPersonDistance = 4.0F;

    /** Third person distance temp */
    private float thirdPersonDistanceTemp = 4.0F;

    /** Smooth cam yaw */
    private float smoothCamYaw;

    /** Smooth cam pitch */
    private float smoothCamPitch;

    /** Smooth cam filter X */
    private float smoothCamFilterX;

    /** Smooth cam filter Y */
    private float smoothCamFilterY;

    /** Smooth cam partial ticks */
    private float smoothCamPartialTicks;

    /** FOV modifier hand */
    private float fovModifierHand;

    /** FOV modifier hand prev */
    private float fovModifierHandPrev;
    private float bossColorModifier;
    private float bossColorModifierPrev;

    /** Cloud fog mode */
    private boolean cloudFog;
    private boolean renderHand = true;
    private boolean drawBlockOutline = true;

    /** Previous frame time in milliseconds */
    private long prevFrameTime = Minecraft.getSystemTime();

    /** End time of last render (ns) */
    private long renderEndNanoTime;

    /**
     * The texture id of the blocklight/skylight texture used for lighting effects
     */
    private final DynamicTexture lightmapTexture;

    /**
     * Colors computed in updateLightmap() and loaded into the lightmap emptyTexture
     */
    private final int[] lightmapColors;
    private final ResourceLocation locationLightMap;

    /**
     * Is set, updateCameraAndRender() calls updateLightmap(); set by updateTorchFlicker()
     */
    private boolean lightmapUpdateNeeded;

    /** Torch flicker X */
    private float torchFlickerX;
    private float torchFlickerDX;

    /** Rain sound counter */
    private int rainSoundCounter;
    private float[] rainXCoords = new float[1024];
    private float[] rainYCoords = new float[1024];

    /** Fog color buffer */
    private FloatBuffer fogColorBuffer = GLAllocation.createDirectFloatBuffer(16);
    private float fogColorRed;
    private float fogColorGreen;
    private float fogColorBlue;

    /** Fog color 2 */
    private float fogColor2;

    /** Fog color 1 */
    private float fogColor1;
    private int debugViewDirection = 0;
    private boolean debugView = false;
    private double cameraZoom = 1.0D;
    private double cameraYaw;
    private double cameraPitch;
    private ShaderGroup theShaderGroup;
    private static final ResourceLocation[] shaderResourceLocations = new ResourceLocation[] {new ResourceLocation("shaders/post/notch.json"), new ResourceLocation("shaders/post/fxaa.json"), new ResourceLocation("shaders/post/art.json"), new ResourceLocation("shaders/post/bumpy.json"), new ResourceLocation("shaders/post/blobs2.json"), new ResourceLocation("shaders/post/pencil.json"), new ResourceLocation("shaders/post/color_convolve.json"), new ResourceLocation("shaders/post/deconverge.json"), new ResourceLocation("shaders/post/flip.json"), new ResourceLocation("shaders/post/invert.json"), new ResourceLocation("shaders/post/ntsc.json"), new ResourceLocation("shaders/post/outline.json"), new ResourceLocation("shaders/post/phosphor.json"), new ResourceLocation("shaders/post/scan_pincushion.json"), new ResourceLocation("shaders/post/sobel.json"), new ResourceLocation("shaders/post/bits.json"), new ResourceLocation("shaders/post/desaturate.json"), new ResourceLocation("shaders/post/green.json"), new ResourceLocation("shaders/post/blur.json"), new ResourceLocation("shaders/post/wobble.json"), new ResourceLocation("shaders/post/blobs.json"), new ResourceLocation("shaders/post/antialias.json"), new ResourceLocation("shaders/post/creeper.json"), new ResourceLocation("shaders/post/spider.json")};
    public static final int shaderCount = shaderResourceLocations.length;
    private int shaderIndex;
    private boolean useShader;
    private int frameCount;

    public EntityRenderer(Minecraft mcIn, IResourceManager resourceManagerIn)
    {
        this.shaderIndex = shaderCount;
        this.useShader = false;
        this.frameCount = 0;
        this.mc = mcIn;
        this.resourceManager = resourceManagerIn;
        this.itemRenderer = mcIn.getItemRenderer();
        this.theMapItemRenderer = new MapItemRenderer(mcIn.getTextureManager());
        this.lightmapTexture = new DynamicTexture(16, 16);
        this.locationLightMap = mcIn.getTextureManager().getDynamicTextureLocation("lightMap", this.lightmapTexture);
        this.lightmapColors = this.lightmapTexture.getTextureData();
        this.theShaderGroup = null;

        for (int lvt_3_1_ = 0; lvt_3_1_ < 32; ++lvt_3_1_)
        {
            for (int lvt_4_1_ = 0; lvt_4_1_ < 32; ++lvt_4_1_)
            {
                float lvt_5_1_ = (float)(lvt_4_1_ - 16);
                float lvt_6_1_ = (float)(lvt_3_1_ - 16);
                float lvt_7_1_ = MathHelper.sqrt_float(lvt_5_1_ * lvt_5_1_ + lvt_6_1_ * lvt_6_1_);
                this.rainXCoords[lvt_3_1_ << 5 | lvt_4_1_] = -lvt_6_1_ / lvt_7_1_;
                this.rainYCoords[lvt_3_1_ << 5 | lvt_4_1_] = lvt_5_1_ / lvt_7_1_;
            }
        }
    }

    public boolean isShaderActive()
    {
        return OpenGlHelper.shadersSupported && this.theShaderGroup != null;
    }

    public void stopUseShader()
    {
        if (this.theShaderGroup != null)
        {
            this.theShaderGroup.deleteShaderGroup();
        }

        this.theShaderGroup = null;
        this.shaderIndex = shaderCount;
    }

    public void switchUseShader()
    {
        this.useShader = !this.useShader;
    }

    /**
     * What shader to use when spectating this entity
     */
    public void loadEntityShader(Entity entityIn)
    {
        if (OpenGlHelper.shadersSupported)
        {
            if (this.theShaderGroup != null)
            {
                this.theShaderGroup.deleteShaderGroup();
            }

            this.theShaderGroup = null;

            if (entityIn instanceof EntityCreeper)
            {
                this.loadShader(new ResourceLocation("shaders/post/creeper.json"));
            }
            else if (entityIn instanceof EntitySpider)
            {
                this.loadShader(new ResourceLocation("shaders/post/spider.json"));
            }
            else if (entityIn instanceof EntityEnderman)
            {
                this.loadShader(new ResourceLocation("shaders/post/invert.json"));
            }
        }
    }

    public void activateNextShader()
    {
        if (OpenGlHelper.shadersSupported)
        {
            if (this.mc.getRenderViewEntity() instanceof EntityPlayer)
            {
                if (this.theShaderGroup != null)
                {
                    this.theShaderGroup.deleteShaderGroup();
                }

                this.shaderIndex = (this.shaderIndex + 1) % (shaderResourceLocations.length + 1);

                if (this.shaderIndex != shaderCount)
                {
                    this.loadShader(shaderResourceLocations[this.shaderIndex]);
                }
                else
                {
                    this.theShaderGroup = null;
                }
            }
        }
    }

    private void loadShader(ResourceLocation resourceLocationIn)
    {
        try
        {
            this.theShaderGroup = new ShaderGroup(this.mc.getTextureManager(), this.resourceManager, this.mc.getFramebuffer(), resourceLocationIn);
            this.theShaderGroup.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
            this.useShader = true;
        }
        catch (IOException var3)
        {
            logger.warn("Failed to load shader: " + resourceLocationIn, var3);
            this.shaderIndex = shaderCount;
            this.useShader = false;
        }
        catch (JsonSyntaxException var4)
        {
            logger.warn("Failed to load shader: " + resourceLocationIn, var4);
            this.shaderIndex = shaderCount;
            this.useShader = false;
        }
    }

    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        if (this.theShaderGroup != null)
        {
            this.theShaderGroup.deleteShaderGroup();
        }

        this.theShaderGroup = null;

        if (this.shaderIndex != shaderCount)
        {
            this.loadShader(shaderResourceLocations[this.shaderIndex]);
        }
        else
        {
            this.loadEntityShader(this.mc.getRenderViewEntity());
        }
    }

    /**
     * Updates the entity renderer
     */
    public void updateRenderer()
    {
        if (OpenGlHelper.shadersSupported && ShaderLinkHelper.getStaticShaderLinkHelper() == null)
        {
            ShaderLinkHelper.setNewStaticShaderLinkHelper();
        }

        this.updateFovModifierHand();
        this.updateTorchFlicker();
        this.fogColor2 = this.fogColor1;
        this.thirdPersonDistanceTemp = this.thirdPersonDistance;

        if (this.mc.gameSettings.smoothCamera)
        {
            float lvt_1_1_ = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            float lvt_2_1_ = lvt_1_1_ * lvt_1_1_ * lvt_1_1_ * 8.0F;
            this.smoothCamFilterX = this.mouseFilterXAxis.smooth(this.smoothCamYaw, 0.05F * lvt_2_1_);
            this.smoothCamFilterY = this.mouseFilterYAxis.smooth(this.smoothCamPitch, 0.05F * lvt_2_1_);
            this.smoothCamPartialTicks = 0.0F;
            this.smoothCamYaw = 0.0F;
            this.smoothCamPitch = 0.0F;
        }
        else
        {
            this.smoothCamFilterX = 0.0F;
            this.smoothCamFilterY = 0.0F;
            this.mouseFilterXAxis.reset();
            this.mouseFilterYAxis.reset();
        }

        if (this.mc.getRenderViewEntity() == null)
        {
            this.mc.setRenderViewEntity(this.mc.thePlayer);
        }

        float lvt_1_2_ = this.mc.theWorld.getLightBrightness(new BlockPos(this.mc.getRenderViewEntity()));
        float lvt_2_2_ = (float)this.mc.gameSettings.renderDistanceChunks / 32.0F;
        float lvt_3_1_ = lvt_1_2_ * (1.0F - lvt_2_2_) + lvt_2_2_;
        this.fogColor1 += (lvt_3_1_ - this.fogColor1) * 0.1F;
        ++this.rendererUpdateCount;
        this.itemRenderer.updateEquippedItem();
        this.addRainParticles();
        this.bossColorModifierPrev = this.bossColorModifier;

        if (BossStatus.hasColorModifier)
        {
            this.bossColorModifier += 0.05F;

            if (this.bossColorModifier > 1.0F)
            {
                this.bossColorModifier = 1.0F;
            }

            BossStatus.hasColorModifier = false;
        }
        else if (this.bossColorModifier > 0.0F)
        {
            this.bossColorModifier -= 0.0125F;
        }
    }

    public ShaderGroup getShaderGroup()
    {
        return this.theShaderGroup;
    }

    public void updateShaderGroupSize(int width, int height)
    {
        if (OpenGlHelper.shadersSupported)
        {
            if (this.theShaderGroup != null)
            {
                this.theShaderGroup.createBindFramebuffers(width, height);
            }

            this.mc.renderGlobal.createBindEntityOutlineFbs(width, height);
        }
    }

    /**
     * Finds what block or object the mouse is over at the specified partial tick time. Args: partialTickTime
     */
    public void getMouseOver(float partialTicks)
    {
        Entity lvt_2_1_ = this.mc.getRenderViewEntity();

        if (lvt_2_1_ != null)
        {
            if (this.mc.theWorld != null)
            {
                this.mc.mcProfiler.startSection("pick");
                this.mc.pointedEntity = null;
                double lvt_3_1_ = (double)this.mc.playerController.getBlockReachDistance();
                this.mc.objectMouseOver = lvt_2_1_.rayTrace(lvt_3_1_, partialTicks);
                double lvt_5_1_ = lvt_3_1_;
                Vec3 lvt_7_1_ = lvt_2_1_.getPositionEyes(partialTicks);
                boolean lvt_8_1_ = false;
                int lvt_9_1_ = 3;

                if (this.mc.playerController.extendedReach())
                {
                    lvt_3_1_ = 6.0D;
                    lvt_5_1_ = 6.0D;
                }
                else
                {
                    if (lvt_3_1_ > 3.0D)
                    {
                        lvt_8_1_ = true;
                    }

                    lvt_3_1_ = lvt_3_1_;
                }

                if (this.mc.objectMouseOver != null)
                {
                    lvt_5_1_ = this.mc.objectMouseOver.hitVec.distanceTo(lvt_7_1_);
                }

                Vec3 lvt_10_1_ = lvt_2_1_.getLook(partialTicks);
                Vec3 lvt_11_1_ = lvt_7_1_.addVector(lvt_10_1_.xCoord * lvt_3_1_, lvt_10_1_.yCoord * lvt_3_1_, lvt_10_1_.zCoord * lvt_3_1_);
                this.pointedEntity = null;
                Vec3 lvt_12_1_ = null;
                float lvt_13_1_ = 1.0F;
                List<Entity> lvt_14_1_ = this.mc.theWorld.getEntitiesInAABBexcluding(lvt_2_1_, lvt_2_1_.getEntityBoundingBox().addCoord(lvt_10_1_.xCoord * lvt_3_1_, lvt_10_1_.yCoord * lvt_3_1_, lvt_10_1_.zCoord * lvt_3_1_).expand((double)lvt_13_1_, (double)lvt_13_1_, (double)lvt_13_1_), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>()
                {
                    public boolean apply(Entity p_apply_1_)
                    {
                        return p_apply_1_.canBeCollidedWith();
                    }
                    public boolean apply(Object p_apply_1_)
                    {
                        return this.apply((Entity)p_apply_1_);
                    }
                }));
                double lvt_15_1_ = lvt_5_1_;

                for (int lvt_17_1_ = 0; lvt_17_1_ < lvt_14_1_.size(); ++lvt_17_1_)
                {
                    Entity lvt_18_1_ = (Entity)lvt_14_1_.get(lvt_17_1_);
                    float lvt_19_1_ = lvt_18_1_.getCollisionBorderSize();
                    AxisAlignedBB lvt_20_1_ = lvt_18_1_.getEntityBoundingBox().expand((double)lvt_19_1_, (double)lvt_19_1_, (double)lvt_19_1_);
                    MovingObjectPosition lvt_21_1_ = lvt_20_1_.calculateIntercept(lvt_7_1_, lvt_11_1_);

                    if (lvt_20_1_.isVecInside(lvt_7_1_))
                    {
                        if (lvt_15_1_ >= 0.0D)
                        {
                            this.pointedEntity = lvt_18_1_;
                            lvt_12_1_ = lvt_21_1_ == null ? lvt_7_1_ : lvt_21_1_.hitVec;
                            lvt_15_1_ = 0.0D;
                        }
                    }
                    else if (lvt_21_1_ != null)
                    {
                        double lvt_22_1_ = lvt_7_1_.distanceTo(lvt_21_1_.hitVec);

                        if (lvt_22_1_ < lvt_15_1_ || lvt_15_1_ == 0.0D)
                        {
                            if (lvt_18_1_ == lvt_2_1_.ridingEntity)
                            {
                                if (lvt_15_1_ == 0.0D)
                                {
                                    this.pointedEntity = lvt_18_1_;
                                    lvt_12_1_ = lvt_21_1_.hitVec;
                                }
                            }
                            else
                            {
                                this.pointedEntity = lvt_18_1_;
                                lvt_12_1_ = lvt_21_1_.hitVec;
                                lvt_15_1_ = lvt_22_1_;
                            }
                        }
                    }
                }

                if (this.pointedEntity != null && lvt_8_1_ && lvt_7_1_.distanceTo(lvt_12_1_) > 3.0D)
                {
                    this.pointedEntity = null;
                    this.mc.objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, lvt_12_1_, (EnumFacing)null, new BlockPos(lvt_12_1_));
                }

                if (this.pointedEntity != null && (lvt_15_1_ < lvt_5_1_ || this.mc.objectMouseOver == null))
                {
                    this.mc.objectMouseOver = new MovingObjectPosition(this.pointedEntity, lvt_12_1_);

                    if (this.pointedEntity instanceof EntityLivingBase || this.pointedEntity instanceof EntityItemFrame)
                    {
                        this.mc.pointedEntity = this.pointedEntity;
                    }
                }

                this.mc.mcProfiler.endSection();
            }
        }
    }

    /**
     * Update FOV modifier hand
     */
    private void updateFovModifierHand()
    {
        float lvt_1_1_ = 1.0F;

        if (this.mc.getRenderViewEntity() instanceof AbstractClientPlayer)
        {
            AbstractClientPlayer lvt_2_1_ = (AbstractClientPlayer)this.mc.getRenderViewEntity();
            lvt_1_1_ = lvt_2_1_.getFovModifier();
        }

        this.fovModifierHandPrev = this.fovModifierHand;
        this.fovModifierHand += (lvt_1_1_ - this.fovModifierHand) * 0.5F;

        if (this.fovModifierHand > 1.5F)
        {
            this.fovModifierHand = 1.5F;
        }

        if (this.fovModifierHand < 0.1F)
        {
            this.fovModifierHand = 0.1F;
        }
    }

    /**
     * Changes the field of view of the player depending on if they are underwater or not
     *  
     * @param useFOVSetting If true the FOV set in the settings will be use in the calculation
     */
    private float getFOVModifier(float partialTicks, boolean useFOVSetting)
    {
        if (this.debugView)
        {
            return 90.0F;
        }
        else
        {
            Entity lvt_3_1_ = this.mc.getRenderViewEntity();
            float lvt_4_1_ = 70.0F;

            if (useFOVSetting)
            {
                lvt_4_1_ = this.mc.gameSettings.fovSetting;
                lvt_4_1_ = lvt_4_1_ * (this.fovModifierHandPrev + (this.fovModifierHand - this.fovModifierHandPrev) * partialTicks);
            }

            if (lvt_3_1_ instanceof EntityLivingBase && ((EntityLivingBase)lvt_3_1_).getHealth() <= 0.0F)
            {
                float lvt_5_1_ = (float)((EntityLivingBase)lvt_3_1_).deathTime + partialTicks;
                lvt_4_1_ /= (1.0F - 500.0F / (lvt_5_1_ + 500.0F)) * 2.0F + 1.0F;
            }

            Block lvt_5_2_ = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, lvt_3_1_, partialTicks);

            if (lvt_5_2_.getMaterial() == Material.water)
            {
                lvt_4_1_ = lvt_4_1_ * 60.0F / 70.0F;
            }

            return lvt_4_1_;
        }
    }

    private void hurtCameraEffect(float partialTicks)
    {
        if (this.mc.getRenderViewEntity() instanceof EntityLivingBase)
        {
            EntityLivingBase lvt_2_1_ = (EntityLivingBase)this.mc.getRenderViewEntity();
            float lvt_3_1_ = (float)lvt_2_1_.hurtTime - partialTicks;

            if (lvt_2_1_.getHealth() <= 0.0F)
            {
                float lvt_4_1_ = (float)lvt_2_1_.deathTime + partialTicks;
                GlStateManager.rotate(40.0F - 8000.0F / (lvt_4_1_ + 200.0F), 0.0F, 0.0F, 1.0F);
            }

            if (lvt_3_1_ < 0.0F)
            {
                return;
            }

            lvt_3_1_ = lvt_3_1_ / (float)lvt_2_1_.maxHurtTime;
            lvt_3_1_ = MathHelper.sin(lvt_3_1_ * lvt_3_1_ * lvt_3_1_ * lvt_3_1_ * (float)Math.PI);
            float lvt_4_2_ = lvt_2_1_.attackedAtYaw;
            GlStateManager.rotate(-lvt_4_2_, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-lvt_3_1_ * 14.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(lvt_4_2_, 0.0F, 1.0F, 0.0F);
        }
    }

    /**
     * Setups all the GL settings for view bobbing. Args: partialTickTime
     */
    private void setupViewBobbing(float partialTicks)
    {
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer)
        {
            EntityPlayer lvt_2_1_ = (EntityPlayer)this.mc.getRenderViewEntity();
            float lvt_3_1_ = lvt_2_1_.distanceWalkedModified - lvt_2_1_.prevDistanceWalkedModified;
            float lvt_4_1_ = -(lvt_2_1_.distanceWalkedModified + lvt_3_1_ * partialTicks);
            float lvt_5_1_ = lvt_2_1_.prevCameraYaw + (lvt_2_1_.cameraYaw - lvt_2_1_.prevCameraYaw) * partialTicks;
            float lvt_6_1_ = lvt_2_1_.prevCameraPitch + (lvt_2_1_.cameraPitch - lvt_2_1_.prevCameraPitch) * partialTicks;
            GlStateManager.translate(MathHelper.sin(lvt_4_1_ * (float)Math.PI) * lvt_5_1_ * 0.5F, -Math.abs(MathHelper.cos(lvt_4_1_ * (float)Math.PI) * lvt_5_1_), 0.0F);
            GlStateManager.rotate(MathHelper.sin(lvt_4_1_ * (float)Math.PI) * lvt_5_1_ * 3.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(Math.abs(MathHelper.cos(lvt_4_1_ * (float)Math.PI - 0.2F) * lvt_5_1_) * 5.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(lvt_6_1_, 1.0F, 0.0F, 0.0F);
        }
    }

    /**
     * sets up player's eye (or camera in third person mode)
     */
    private void orientCamera(float partialTicks)
    {
        Entity lvt_2_1_ = this.mc.getRenderViewEntity();
        float lvt_3_1_ = lvt_2_1_.getEyeHeight();
        double lvt_4_1_ = lvt_2_1_.prevPosX + (lvt_2_1_.posX - lvt_2_1_.prevPosX) * (double)partialTicks;
        double lvt_6_1_ = lvt_2_1_.prevPosY + (lvt_2_1_.posY - lvt_2_1_.prevPosY) * (double)partialTicks + (double)lvt_3_1_;
        double lvt_8_1_ = lvt_2_1_.prevPosZ + (lvt_2_1_.posZ - lvt_2_1_.prevPosZ) * (double)partialTicks;

        if (lvt_2_1_ instanceof EntityLivingBase && ((EntityLivingBase)lvt_2_1_).isPlayerSleeping())
        {
            lvt_3_1_ = (float)((double)lvt_3_1_ + 1.0D);
            GlStateManager.translate(0.0F, 0.3F, 0.0F);

            if (!this.mc.gameSettings.debugCamEnable)
            {
                BlockPos lvt_10_1_ = new BlockPos(lvt_2_1_);
                IBlockState lvt_11_1_ = this.mc.theWorld.getBlockState(lvt_10_1_);
                Block lvt_12_1_ = lvt_11_1_.getBlock();

                if (lvt_12_1_ == Blocks.bed)
                {
                    int lvt_13_1_ = ((EnumFacing)lvt_11_1_.getValue(BlockBed.FACING)).getHorizontalIndex();
                    GlStateManager.rotate((float)(lvt_13_1_ * 90), 0.0F, 1.0F, 0.0F);
                }

                GlStateManager.rotate(lvt_2_1_.prevRotationYaw + (lvt_2_1_.rotationYaw - lvt_2_1_.prevRotationYaw) * partialTicks + 180.0F, 0.0F, -1.0F, 0.0F);
                GlStateManager.rotate(lvt_2_1_.prevRotationPitch + (lvt_2_1_.rotationPitch - lvt_2_1_.prevRotationPitch) * partialTicks, -1.0F, 0.0F, 0.0F);
            }
        }
        else if (this.mc.gameSettings.thirdPersonView > 0)
        {
            double lvt_10_2_ = (double)(this.thirdPersonDistanceTemp + (this.thirdPersonDistance - this.thirdPersonDistanceTemp) * partialTicks);

            if (this.mc.gameSettings.debugCamEnable)
            {
                GlStateManager.translate(0.0F, 0.0F, (float)(-lvt_10_2_));
            }
            else
            {
                float lvt_12_2_ = lvt_2_1_.rotationYaw;
                float lvt_13_2_ = lvt_2_1_.rotationPitch;

                if (this.mc.gameSettings.thirdPersonView == 2)
                {
                    lvt_13_2_ += 180.0F;
                }

                double lvt_14_1_ = (double)(-MathHelper.sin(lvt_12_2_ / 180.0F * (float)Math.PI) * MathHelper.cos(lvt_13_2_ / 180.0F * (float)Math.PI)) * lvt_10_2_;
                double lvt_16_1_ = (double)(MathHelper.cos(lvt_12_2_ / 180.0F * (float)Math.PI) * MathHelper.cos(lvt_13_2_ / 180.0F * (float)Math.PI)) * lvt_10_2_;
                double lvt_18_1_ = (double)(-MathHelper.sin(lvt_13_2_ / 180.0F * (float)Math.PI)) * lvt_10_2_;

                for (int lvt_20_1_ = 0; lvt_20_1_ < 8; ++lvt_20_1_)
                {
                    float lvt_21_1_ = (float)((lvt_20_1_ & 1) * 2 - 1);
                    float lvt_22_1_ = (float)((lvt_20_1_ >> 1 & 1) * 2 - 1);
                    float lvt_23_1_ = (float)((lvt_20_1_ >> 2 & 1) * 2 - 1);
                    lvt_21_1_ = lvt_21_1_ * 0.1F;
                    lvt_22_1_ = lvt_22_1_ * 0.1F;
                    lvt_23_1_ = lvt_23_1_ * 0.1F;
                    MovingObjectPosition lvt_24_1_ = this.mc.theWorld.rayTraceBlocks(new Vec3(lvt_4_1_ + (double)lvt_21_1_, lvt_6_1_ + (double)lvt_22_1_, lvt_8_1_ + (double)lvt_23_1_), new Vec3(lvt_4_1_ - lvt_14_1_ + (double)lvt_21_1_ + (double)lvt_23_1_, lvt_6_1_ - lvt_18_1_ + (double)lvt_22_1_, lvt_8_1_ - lvt_16_1_ + (double)lvt_23_1_));

                    if (lvt_24_1_ != null)
                    {
                        double lvt_25_1_ = lvt_24_1_.hitVec.distanceTo(new Vec3(lvt_4_1_, lvt_6_1_, lvt_8_1_));

                        if (lvt_25_1_ < lvt_10_2_)
                        {
                            lvt_10_2_ = lvt_25_1_;
                        }
                    }
                }

                if (this.mc.gameSettings.thirdPersonView == 2)
                {
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                }

                GlStateManager.rotate(lvt_2_1_.rotationPitch - lvt_13_2_, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(lvt_2_1_.rotationYaw - lvt_12_2_, 0.0F, 1.0F, 0.0F);
                GlStateManager.translate(0.0F, 0.0F, (float)(-lvt_10_2_));
                GlStateManager.rotate(lvt_12_2_ - lvt_2_1_.rotationYaw, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(lvt_13_2_ - lvt_2_1_.rotationPitch, 1.0F, 0.0F, 0.0F);
            }
        }
        else
        {
            GlStateManager.translate(0.0F, 0.0F, -0.1F);
        }

        if (!this.mc.gameSettings.debugCamEnable)
        {
            GlStateManager.rotate(lvt_2_1_.prevRotationPitch + (lvt_2_1_.rotationPitch - lvt_2_1_.prevRotationPitch) * partialTicks, 1.0F, 0.0F, 0.0F);

            if (lvt_2_1_ instanceof EntityAnimal)
            {
                EntityAnimal lvt_10_3_ = (EntityAnimal)lvt_2_1_;
                GlStateManager.rotate(lvt_10_3_.prevRotationYawHead + (lvt_10_3_.rotationYawHead - lvt_10_3_.prevRotationYawHead) * partialTicks + 180.0F, 0.0F, 1.0F, 0.0F);
            }
            else
            {
                GlStateManager.rotate(lvt_2_1_.prevRotationYaw + (lvt_2_1_.rotationYaw - lvt_2_1_.prevRotationYaw) * partialTicks + 180.0F, 0.0F, 1.0F, 0.0F);
            }
        }

        GlStateManager.translate(0.0F, -lvt_3_1_, 0.0F);
        lvt_4_1_ = lvt_2_1_.prevPosX + (lvt_2_1_.posX - lvt_2_1_.prevPosX) * (double)partialTicks;
        lvt_6_1_ = lvt_2_1_.prevPosY + (lvt_2_1_.posY - lvt_2_1_.prevPosY) * (double)partialTicks + (double)lvt_3_1_;
        lvt_8_1_ = lvt_2_1_.prevPosZ + (lvt_2_1_.posZ - lvt_2_1_.prevPosZ) * (double)partialTicks;
        this.cloudFog = this.mc.renderGlobal.hasCloudFog(lvt_4_1_, lvt_6_1_, lvt_8_1_, partialTicks);
    }

    /**
     * sets up projection, view effects, camera position/rotation
     */
    private void setupCameraTransform(float partialTicks, int pass)
    {
        this.farPlaneDistance = (float)(this.mc.gameSettings.renderDistanceChunks * 16);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        float lvt_3_1_ = 0.07F;

        if (this.mc.gameSettings.anaglyph)
        {
            GlStateManager.translate((float)(-(pass * 2 - 1)) * lvt_3_1_, 0.0F, 0.0F);
        }

        if (this.cameraZoom != 1.0D)
        {
            GlStateManager.translate((float)this.cameraYaw, (float)(-this.cameraPitch), 0.0F);
            GlStateManager.scale(this.cameraZoom, this.cameraZoom, 1.0D);
        }

        Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance * MathHelper.SQRT_2);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();

        if (this.mc.gameSettings.anaglyph)
        {
            GlStateManager.translate((float)(pass * 2 - 1) * 0.1F, 0.0F, 0.0F);
        }

        this.hurtCameraEffect(partialTicks);

        if (this.mc.gameSettings.viewBobbing)
        {
            this.setupViewBobbing(partialTicks);
        }

        float lvt_4_1_ = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * partialTicks;

        if (lvt_4_1_ > 0.0F)
        {
            int lvt_5_1_ = 20;

            if (this.mc.thePlayer.isPotionActive(Potion.confusion))
            {
                lvt_5_1_ = 7;
            }

            float lvt_6_1_ = 5.0F / (lvt_4_1_ * lvt_4_1_ + 5.0F) - lvt_4_1_ * 0.04F;
            lvt_6_1_ = lvt_6_1_ * lvt_6_1_;
            GlStateManager.rotate(((float)this.rendererUpdateCount + partialTicks) * (float)lvt_5_1_, 0.0F, 1.0F, 1.0F);
            GlStateManager.scale(1.0F / lvt_6_1_, 1.0F, 1.0F);
            GlStateManager.rotate(-((float)this.rendererUpdateCount + partialTicks) * (float)lvt_5_1_, 0.0F, 1.0F, 1.0F);
        }

        this.orientCamera(partialTicks);

        if (this.debugView)
        {
            switch (this.debugViewDirection)
            {
                case 0:
                    GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                    break;

                case 1:
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                    break;

                case 2:
                    GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                    break;

                case 3:
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                    break;

                case 4:
                    GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            }
        }
    }

    /**
     * Render player hand
     */
    private void renderHand(float partialTicks, int xOffset)
    {
        if (!this.debugView)
        {
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            float lvt_3_1_ = 0.07F;

            if (this.mc.gameSettings.anaglyph)
            {
                GlStateManager.translate((float)(-(xOffset * 2 - 1)) * lvt_3_1_, 0.0F, 0.0F);
            }

            Project.gluPerspective(this.getFOVModifier(partialTicks, false), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();

            if (this.mc.gameSettings.anaglyph)
            {
                GlStateManager.translate((float)(xOffset * 2 - 1) * 0.1F, 0.0F, 0.0F);
            }

            GlStateManager.pushMatrix();
            this.hurtCameraEffect(partialTicks);

            if (this.mc.gameSettings.viewBobbing)
            {
                this.setupViewBobbing(partialTicks);
            }

            boolean lvt_4_1_ = this.mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase)this.mc.getRenderViewEntity()).isPlayerSleeping();

            if (this.mc.gameSettings.thirdPersonView == 0 && !lvt_4_1_ && !this.mc.gameSettings.hideGUI && !this.mc.playerController.isSpectator())
            {
                this.enableLightmap();
                this.itemRenderer.renderItemInFirstPerson(partialTicks);
                this.disableLightmap();
            }

            GlStateManager.popMatrix();

            if (this.mc.gameSettings.thirdPersonView == 0 && !lvt_4_1_)
            {
                this.itemRenderer.renderOverlays(partialTicks);
                this.hurtCameraEffect(partialTicks);
            }

            if (this.mc.gameSettings.viewBobbing)
            {
                this.setupViewBobbing(partialTicks);
            }
        }
    }

    public void disableLightmap()
    {
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public void enableLightmap()
    {
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        float lvt_1_1_ = 0.00390625F;
        GlStateManager.scale(lvt_1_1_, lvt_1_1_, lvt_1_1_);
        GlStateManager.translate(8.0F, 8.0F, 8.0F);
        GlStateManager.matrixMode(5888);
        this.mc.getTextureManager().bindTexture(this.locationLightMap);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    /**
     * Recompute a random value that is applied to block color in updateLightmap()
     */
    private void updateTorchFlicker()
    {
        this.torchFlickerDX = (float)((double)this.torchFlickerDX + (Math.random() - Math.random()) * Math.random() * Math.random());
        this.torchFlickerDX = (float)((double)this.torchFlickerDX * 0.9D);
        this.torchFlickerX += (this.torchFlickerDX - this.torchFlickerX) * 1.0F;
        this.lightmapUpdateNeeded = true;
    }

    private void updateLightmap(float partialTicks)
    {
        if (this.lightmapUpdateNeeded)
        {
            this.mc.mcProfiler.startSection("lightTex");
            World lvt_2_1_ = this.mc.theWorld;

            if (lvt_2_1_ != null)
            {
                float lvt_3_1_ = lvt_2_1_.getSunBrightness(1.0F);
                float lvt_4_1_ = lvt_3_1_ * 0.95F + 0.05F;

                for (int lvt_5_1_ = 0; lvt_5_1_ < 256; ++lvt_5_1_)
                {
                    float lvt_6_1_ = lvt_2_1_.provider.getLightBrightnessTable()[lvt_5_1_ / 16] * lvt_4_1_;
                    float lvt_7_1_ = lvt_2_1_.provider.getLightBrightnessTable()[lvt_5_1_ % 16] * (this.torchFlickerX * 0.1F + 1.5F);

                    if (lvt_2_1_.getLastLightningBolt() > 0)
                    {
                        lvt_6_1_ = lvt_2_1_.provider.getLightBrightnessTable()[lvt_5_1_ / 16];
                    }

                    float lvt_8_1_ = lvt_6_1_ * (lvt_3_1_ * 0.65F + 0.35F);
                    float lvt_9_1_ = lvt_6_1_ * (lvt_3_1_ * 0.65F + 0.35F);
                    float lvt_12_1_ = lvt_7_1_ * ((lvt_7_1_ * 0.6F + 0.4F) * 0.6F + 0.4F);
                    float lvt_13_1_ = lvt_7_1_ * (lvt_7_1_ * lvt_7_1_ * 0.6F + 0.4F);
                    float lvt_14_1_ = lvt_8_1_ + lvt_7_1_;
                    float lvt_15_1_ = lvt_9_1_ + lvt_12_1_;
                    float lvt_16_1_ = lvt_6_1_ + lvt_13_1_;
                    lvt_14_1_ = lvt_14_1_ * 0.96F + 0.03F;
                    lvt_15_1_ = lvt_15_1_ * 0.96F + 0.03F;
                    lvt_16_1_ = lvt_16_1_ * 0.96F + 0.03F;

                    if (this.bossColorModifier > 0.0F)
                    {
                        float lvt_17_1_ = this.bossColorModifierPrev + (this.bossColorModifier - this.bossColorModifierPrev) * partialTicks;
                        lvt_14_1_ = lvt_14_1_ * (1.0F - lvt_17_1_) + lvt_14_1_ * 0.7F * lvt_17_1_;
                        lvt_15_1_ = lvt_15_1_ * (1.0F - lvt_17_1_) + lvt_15_1_ * 0.6F * lvt_17_1_;
                        lvt_16_1_ = lvt_16_1_ * (1.0F - lvt_17_1_) + lvt_16_1_ * 0.6F * lvt_17_1_;
                    }

                    if (lvt_2_1_.provider.getDimensionId() == 1)
                    {
                        lvt_14_1_ = 0.22F + lvt_7_1_ * 0.75F;
                        lvt_15_1_ = 0.28F + lvt_12_1_ * 0.75F;
                        lvt_16_1_ = 0.25F + lvt_13_1_ * 0.75F;
                    }

                    if (this.mc.thePlayer.isPotionActive(Potion.nightVision))
                    {
                        float lvt_17_2_ = this.getNightVisionBrightness(this.mc.thePlayer, partialTicks);
                        float lvt_18_1_ = 1.0F / lvt_14_1_;

                        if (lvt_18_1_ > 1.0F / lvt_15_1_)
                        {
                            lvt_18_1_ = 1.0F / lvt_15_1_;
                        }

                        if (lvt_18_1_ > 1.0F / lvt_16_1_)
                        {
                            lvt_18_1_ = 1.0F / lvt_16_1_;
                        }

                        lvt_14_1_ = lvt_14_1_ * (1.0F - lvt_17_2_) + lvt_14_1_ * lvt_18_1_ * lvt_17_2_;
                        lvt_15_1_ = lvt_15_1_ * (1.0F - lvt_17_2_) + lvt_15_1_ * lvt_18_1_ * lvt_17_2_;
                        lvt_16_1_ = lvt_16_1_ * (1.0F - lvt_17_2_) + lvt_16_1_ * lvt_18_1_ * lvt_17_2_;
                    }

                    if (lvt_14_1_ > 1.0F)
                    {
                        lvt_14_1_ = 1.0F;
                    }

                    if (lvt_15_1_ > 1.0F)
                    {
                        lvt_15_1_ = 1.0F;
                    }

                    if (lvt_16_1_ > 1.0F)
                    {
                        lvt_16_1_ = 1.0F;
                    }

                    float lvt_17_3_ = this.mc.gameSettings.gammaSetting;
                    float lvt_18_2_ = 1.0F - lvt_14_1_;
                    float lvt_19_1_ = 1.0F - lvt_15_1_;
                    float lvt_20_1_ = 1.0F - lvt_16_1_;
                    lvt_18_2_ = 1.0F - lvt_18_2_ * lvt_18_2_ * lvt_18_2_ * lvt_18_2_;
                    lvt_19_1_ = 1.0F - lvt_19_1_ * lvt_19_1_ * lvt_19_1_ * lvt_19_1_;
                    lvt_20_1_ = 1.0F - lvt_20_1_ * lvt_20_1_ * lvt_20_1_ * lvt_20_1_;
                    lvt_14_1_ = lvt_14_1_ * (1.0F - lvt_17_3_) + lvt_18_2_ * lvt_17_3_;
                    lvt_15_1_ = lvt_15_1_ * (1.0F - lvt_17_3_) + lvt_19_1_ * lvt_17_3_;
                    lvt_16_1_ = lvt_16_1_ * (1.0F - lvt_17_3_) + lvt_20_1_ * lvt_17_3_;
                    lvt_14_1_ = lvt_14_1_ * 0.96F + 0.03F;
                    lvt_15_1_ = lvt_15_1_ * 0.96F + 0.03F;
                    lvt_16_1_ = lvt_16_1_ * 0.96F + 0.03F;

                    if (lvt_14_1_ > 1.0F)
                    {
                        lvt_14_1_ = 1.0F;
                    }

                    if (lvt_15_1_ > 1.0F)
                    {
                        lvt_15_1_ = 1.0F;
                    }

                    if (lvt_16_1_ > 1.0F)
                    {
                        lvt_16_1_ = 1.0F;
                    }

                    if (lvt_14_1_ < 0.0F)
                    {
                        lvt_14_1_ = 0.0F;
                    }

                    if (lvt_15_1_ < 0.0F)
                    {
                        lvt_15_1_ = 0.0F;
                    }

                    if (lvt_16_1_ < 0.0F)
                    {
                        lvt_16_1_ = 0.0F;
                    }

                    int lvt_21_1_ = 255;
                    int lvt_22_1_ = (int)(lvt_14_1_ * 255.0F);
                    int lvt_23_1_ = (int)(lvt_15_1_ * 255.0F);
                    int lvt_24_1_ = (int)(lvt_16_1_ * 255.0F);
                    this.lightmapColors[lvt_5_1_] = lvt_21_1_ << 24 | lvt_22_1_ << 16 | lvt_23_1_ << 8 | lvt_24_1_;
                }

                this.lightmapTexture.updateDynamicTexture();
                this.lightmapUpdateNeeded = false;
                this.mc.mcProfiler.endSection();
            }
        }
    }

    private float getNightVisionBrightness(EntityLivingBase entitylivingbaseIn, float partialTicks)
    {
        int lvt_3_1_ = entitylivingbaseIn.getActivePotionEffect(Potion.nightVision).getDuration();
        return lvt_3_1_ > 200 ? 1.0F : 0.7F + MathHelper.sin(((float)lvt_3_1_ - partialTicks) * (float)Math.PI * 0.2F) * 0.3F;
    }

    public void updateCameraAndRender(float partialTicks, long nanoTime)
    {
        boolean lvt_4_1_ = Display.isActive();

        if (!lvt_4_1_ && this.mc.gameSettings.pauseOnLostFocus && (!this.mc.gameSettings.touchscreen || !Mouse.isButtonDown(1)))
        {
            if (Minecraft.getSystemTime() - this.prevFrameTime > 500L)
            {
                this.mc.displayInGameMenu();
            }
        }
        else
        {
            this.prevFrameTime = Minecraft.getSystemTime();
        }

        this.mc.mcProfiler.startSection("mouse");

        if (lvt_4_1_ && Minecraft.isRunningOnMac && this.mc.inGameHasFocus && !Mouse.isInsideWindow())
        {
            Mouse.setGrabbed(false);
            Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
            Mouse.setGrabbed(true);
        }

        if (this.mc.inGameHasFocus && lvt_4_1_)
        {
            this.mc.mouseHelper.mouseXYChange();
            float lvt_5_1_ = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            float lvt_6_1_ = lvt_5_1_ * lvt_5_1_ * lvt_5_1_ * 8.0F;
            float lvt_7_1_ = (float)this.mc.mouseHelper.deltaX * lvt_6_1_;
            float lvt_8_1_ = (float)this.mc.mouseHelper.deltaY * lvt_6_1_;
            int lvt_9_1_ = 1;

            if (this.mc.gameSettings.invertMouse)
            {
                lvt_9_1_ = -1;
            }

            if (this.mc.gameSettings.smoothCamera)
            {
                this.smoothCamYaw += lvt_7_1_;
                this.smoothCamPitch += lvt_8_1_;
                float lvt_10_1_ = partialTicks - this.smoothCamPartialTicks;
                this.smoothCamPartialTicks = partialTicks;
                lvt_7_1_ = this.smoothCamFilterX * lvt_10_1_;
                lvt_8_1_ = this.smoothCamFilterY * lvt_10_1_;
                this.mc.thePlayer.setAngles(lvt_7_1_, lvt_8_1_ * (float)lvt_9_1_);
            }
            else
            {
                this.smoothCamYaw = 0.0F;
                this.smoothCamPitch = 0.0F;
                this.mc.thePlayer.setAngles(lvt_7_1_, lvt_8_1_ * (float)lvt_9_1_);
            }
        }

        this.mc.mcProfiler.endSection();

        if (!this.mc.skipRenderWorld)
        {
            anaglyphEnable = this.mc.gameSettings.anaglyph;
            final ScaledResolution lvt_5_2_ = new ScaledResolution(this.mc);
            int lvt_6_2_ = lvt_5_2_.getScaledWidth();
            int lvt_7_2_ = lvt_5_2_.getScaledHeight();
            final int lvt_8_2_ = Mouse.getX() * lvt_6_2_ / this.mc.displayWidth;
            final int lvt_9_2_ = lvt_7_2_ - Mouse.getY() * lvt_7_2_ / this.mc.displayHeight - 1;
            int lvt_10_2_ = this.mc.gameSettings.limitFramerate;

            if (this.mc.theWorld != null)
            {
                this.mc.mcProfiler.startSection("level");
                int lvt_11_1_ = Math.min(Minecraft.getDebugFPS(), lvt_10_2_);
                lvt_11_1_ = Math.max(lvt_11_1_, 60);
                long lvt_12_1_ = System.nanoTime() - nanoTime;
                long lvt_14_1_ = Math.max((long)(1000000000 / lvt_11_1_ / 4) - lvt_12_1_, 0L);
                this.renderWorld(partialTicks, System.nanoTime() + lvt_14_1_);

                if (OpenGlHelper.shadersSupported)
                {
                    this.mc.renderGlobal.renderEntityOutlineFramebuffer();

                    if (this.theShaderGroup != null && this.useShader)
                    {
                        GlStateManager.matrixMode(5890);
                        GlStateManager.pushMatrix();
                        GlStateManager.loadIdentity();
                        this.theShaderGroup.loadShaderGroup(partialTicks);
                        GlStateManager.popMatrix();
                    }

                    this.mc.getFramebuffer().bindFramebuffer(true);
                }

                this.renderEndNanoTime = System.nanoTime();
                this.mc.mcProfiler.endStartSection("gui");

                if (!this.mc.gameSettings.hideGUI || this.mc.currentScreen != null)
                {
                    GlStateManager.alphaFunc(516, 0.1F);
                    this.mc.ingameGUI.renderGameOverlay(partialTicks);
                }

                this.mc.mcProfiler.endSection();
            }
            else
            {
                GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
                GlStateManager.matrixMode(5889);
                GlStateManager.loadIdentity();
                GlStateManager.matrixMode(5888);
                GlStateManager.loadIdentity();
                this.setupOverlayRendering();
                this.renderEndNanoTime = System.nanoTime();
            }

            if (this.mc.currentScreen != null)
            {
                GlStateManager.clear(256);

                try
                {
                    this.mc.currentScreen.drawScreen(lvt_8_2_, lvt_9_2_, partialTicks);
                }
                catch (Throwable var16)
                {
                    CrashReport lvt_12_2_ = CrashReport.makeCrashReport(var16, "Rendering screen");
                    CrashReportCategory lvt_13_1_ = lvt_12_2_.makeCategory("Screen render details");
                    lvt_13_1_.addCrashSectionCallable("Screen name", new Callable<String>()
                    {
                        public String call() throws Exception
                        {
                            return EntityRenderer.this.mc.currentScreen.getClass().getCanonicalName();
                        }
                        public Object call() throws Exception
                        {
                            return this.call();
                        }
                    });
                    lvt_13_1_.addCrashSectionCallable("Mouse location", new Callable<String>()
                    {
                        public String call() throws Exception
                        {
                            return String.format("Scaled: (%d, %d). Absolute: (%d, %d)", new Object[] {Integer.valueOf(lvt_8_2_), Integer.valueOf(lvt_9_2_), Integer.valueOf(Mouse.getX()), Integer.valueOf(Mouse.getY())});
                        }
                        public Object call() throws Exception
                        {
                            return this.call();
                        }
                    });
                    lvt_13_1_.addCrashSectionCallable("Screen size", new Callable<String>()
                    {
                        public String call() throws Exception
                        {
                            return String.format("Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %d", new Object[] {Integer.valueOf(lvt_5_2_.getScaledWidth()), Integer.valueOf(lvt_5_2_.getScaledHeight()), Integer.valueOf(EntityRenderer.this.mc.displayWidth), Integer.valueOf(EntityRenderer.this.mc.displayHeight), Integer.valueOf(lvt_5_2_.getScaleFactor())});
                        }
                        public Object call() throws Exception
                        {
                            return this.call();
                        }
                    });
                    throw new ReportedException(lvt_12_2_);
                }
            }
        }
    }

    public void renderStreamIndicator(float partialTicks)
    {
        this.setupOverlayRendering();
        this.mc.ingameGUI.renderStreamIndicator(new ScaledResolution(this.mc));
    }

    private boolean isDrawBlockOutline()
    {
        if (!this.drawBlockOutline)
        {
            return false;
        }
        else
        {
            Entity lvt_1_1_ = this.mc.getRenderViewEntity();
            boolean lvt_2_1_ = lvt_1_1_ instanceof EntityPlayer && !this.mc.gameSettings.hideGUI;

            if (lvt_2_1_ && !((EntityPlayer)lvt_1_1_).capabilities.allowEdit)
            {
                ItemStack lvt_3_1_ = ((EntityPlayer)lvt_1_1_).getCurrentEquippedItem();

                if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
                {
                    BlockPos lvt_4_1_ = this.mc.objectMouseOver.getBlockPos();
                    Block lvt_5_1_ = this.mc.theWorld.getBlockState(lvt_4_1_).getBlock();

                    if (this.mc.playerController.getCurrentGameType() == WorldSettings.GameType.SPECTATOR)
                    {
                        lvt_2_1_ = lvt_5_1_.hasTileEntity() && this.mc.theWorld.getTileEntity(lvt_4_1_) instanceof IInventory;
                    }
                    else
                    {
                        lvt_2_1_ = lvt_3_1_ != null && (lvt_3_1_.canDestroy(lvt_5_1_) || lvt_3_1_.canPlaceOn(lvt_5_1_));
                    }
                }
            }

            return lvt_2_1_;
        }
    }

    private void renderWorldDirections(float partialTicks)
    {
        if (this.mc.gameSettings.showDebugInfo && !this.mc.gameSettings.hideGUI && !this.mc.thePlayer.hasReducedDebug() && !this.mc.gameSettings.reducedDebugInfo)
        {
            Entity lvt_2_1_ = this.mc.getRenderViewEntity();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GL11.glLineWidth(1.0F);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GlStateManager.pushMatrix();
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            this.orientCamera(partialTicks);
            GlStateManager.translate(0.0F, lvt_2_1_.getEyeHeight(), 0.0F);
            RenderGlobal.drawOutlinedBoundingBox(new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.005D, 1.0E-4D, 1.0E-4D), 255, 0, 0, 255);
            RenderGlobal.drawOutlinedBoundingBox(new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0E-4D, 1.0E-4D, 0.005D), 0, 0, 255, 255);
            RenderGlobal.drawOutlinedBoundingBox(new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0E-4D, 0.0033D, 1.0E-4D), 0, 255, 0, 255);
            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }
    }

    public void renderWorld(float partialTicks, long finishTimeNano)
    {
        this.updateLightmap(partialTicks);

        if (this.mc.getRenderViewEntity() == null)
        {
            this.mc.setRenderViewEntity(this.mc.thePlayer);
        }

        this.getMouseOver(partialTicks);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.5F);
        this.mc.mcProfiler.startSection("center");

        if (this.mc.gameSettings.anaglyph)
        {
            anaglyphField = 0;
            GlStateManager.colorMask(false, true, true, false);
            this.renderWorldPass(0, partialTicks, finishTimeNano);
            anaglyphField = 1;
            GlStateManager.colorMask(true, false, false, false);
            this.renderWorldPass(1, partialTicks, finishTimeNano);
            GlStateManager.colorMask(true, true, true, false);
        }
        else
        {
            this.renderWorldPass(2, partialTicks, finishTimeNano);
        }

        this.mc.mcProfiler.endSection();
    }

    private void renderWorldPass(int pass, float partialTicks, long finishTimeNano)
    {
        RenderGlobal lvt_5_1_ = this.mc.renderGlobal;
        EffectRenderer lvt_6_1_ = this.mc.effectRenderer;
        boolean lvt_7_1_ = this.isDrawBlockOutline();
        GlStateManager.enableCull();
        this.mc.mcProfiler.endStartSection("clear");
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        this.updateFogColor(partialTicks);
        GlStateManager.clear(16640);
        this.mc.mcProfiler.endStartSection("camera");
        this.setupCameraTransform(partialTicks, pass);
        ActiveRenderInfo.updateRenderInfo(this.mc.thePlayer, this.mc.gameSettings.thirdPersonView == 2);
        this.mc.mcProfiler.endStartSection("frustum");
        ClippingHelperImpl.getInstance();
        this.mc.mcProfiler.endStartSection("culling");
        ICamera lvt_8_1_ = new Frustum();
        Entity lvt_9_1_ = this.mc.getRenderViewEntity();
        double lvt_10_1_ = lvt_9_1_.lastTickPosX + (lvt_9_1_.posX - lvt_9_1_.lastTickPosX) * (double)partialTicks;
        double lvt_12_1_ = lvt_9_1_.lastTickPosY + (lvt_9_1_.posY - lvt_9_1_.lastTickPosY) * (double)partialTicks;
        double lvt_14_1_ = lvt_9_1_.lastTickPosZ + (lvt_9_1_.posZ - lvt_9_1_.lastTickPosZ) * (double)partialTicks;
        lvt_8_1_.setPosition(lvt_10_1_, lvt_12_1_, lvt_14_1_);

        if (this.mc.gameSettings.renderDistanceChunks >= 4)
        {
            this.setupFog(-1, partialTicks);
            this.mc.mcProfiler.endStartSection("sky");
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);
            GlStateManager.matrixMode(5888);
            lvt_5_1_.renderSky(partialTicks, pass);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance * MathHelper.SQRT_2);
            GlStateManager.matrixMode(5888);
        }

        this.setupFog(0, partialTicks);
        GlStateManager.shadeModel(7425);

        if (lvt_9_1_.posY + (double)lvt_9_1_.getEyeHeight() < 128.0D)
        {
            this.renderCloudsCheck(lvt_5_1_, partialTicks, pass);
        }

        this.mc.mcProfiler.endStartSection("prepareterrain");
        this.setupFog(0, partialTicks);
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        RenderHelper.disableStandardItemLighting();
        this.mc.mcProfiler.endStartSection("terrain_setup");
        lvt_5_1_.setupTerrain(lvt_9_1_, (double)partialTicks, lvt_8_1_, this.frameCount++, this.mc.thePlayer.isSpectator());

        if (pass == 0 || pass == 2)
        {
            this.mc.mcProfiler.endStartSection("updatechunks");
            this.mc.renderGlobal.updateChunks(finishTimeNano);
        }

        this.mc.mcProfiler.endStartSection("terrain");
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        GlStateManager.disableAlpha();
        lvt_5_1_.renderBlockLayer(EnumWorldBlockLayer.SOLID, (double)partialTicks, pass, lvt_9_1_);
        GlStateManager.enableAlpha();
        lvt_5_1_.renderBlockLayer(EnumWorldBlockLayer.CUTOUT_MIPPED, (double)partialTicks, pass, lvt_9_1_);
        this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
        lvt_5_1_.renderBlockLayer(EnumWorldBlockLayer.CUTOUT, (double)partialTicks, pass, lvt_9_1_);
        this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
        GlStateManager.shadeModel(7424);
        GlStateManager.alphaFunc(516, 0.1F);

        if (!this.debugView)
        {
            GlStateManager.matrixMode(5888);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            RenderHelper.enableStandardItemLighting();
            this.mc.mcProfiler.endStartSection("entities");
            lvt_5_1_.renderEntities(lvt_9_1_, lvt_8_1_, partialTicks);
            RenderHelper.disableStandardItemLighting();
            this.disableLightmap();
            GlStateManager.matrixMode(5888);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();

            if (this.mc.objectMouseOver != null && lvt_9_1_.isInsideOfMaterial(Material.water) && lvt_7_1_)
            {
                EntityPlayer lvt_16_1_ = (EntityPlayer)lvt_9_1_;
                GlStateManager.disableAlpha();
                this.mc.mcProfiler.endStartSection("outline");
                lvt_5_1_.drawSelectionBox(lvt_16_1_, this.mc.objectMouseOver, 0, partialTicks);
                GlStateManager.enableAlpha();
            }
        }

        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();

        if (lvt_7_1_ && this.mc.objectMouseOver != null && !lvt_9_1_.isInsideOfMaterial(Material.water))
        {
            EntityPlayer lvt_16_2_ = (EntityPlayer)lvt_9_1_;
            GlStateManager.disableAlpha();
            this.mc.mcProfiler.endStartSection("outline");
            lvt_5_1_.drawSelectionBox(lvt_16_2_, this.mc.objectMouseOver, 0, partialTicks);
            GlStateManager.enableAlpha();
        }

        this.mc.mcProfiler.endStartSection("destroyProgress");
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
        this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
        lvt_5_1_.drawBlockDamageTexture(Tessellator.getInstance(), Tessellator.getInstance().getWorldRenderer(), lvt_9_1_, partialTicks);
        this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
        GlStateManager.disableBlend();

        if (!this.debugView)
        {
            this.enableLightmap();
            this.mc.mcProfiler.endStartSection("litParticles");
            lvt_6_1_.renderLitParticles(lvt_9_1_, partialTicks);
            RenderHelper.disableStandardItemLighting();
            this.setupFog(0, partialTicks);
            this.mc.mcProfiler.endStartSection("particles");
            lvt_6_1_.renderParticles(lvt_9_1_, partialTicks);
            this.disableLightmap();
        }

        GlStateManager.depthMask(false);
        GlStateManager.enableCull();
        this.mc.mcProfiler.endStartSection("weather");
        this.renderRainSnow(partialTicks);
        GlStateManager.depthMask(true);
        lvt_5_1_.renderWorldBorder(lvt_9_1_, partialTicks);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.alphaFunc(516, 0.1F);
        this.setupFog(0, partialTicks);
        GlStateManager.enableBlend();
        GlStateManager.depthMask(false);
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        GlStateManager.shadeModel(7425);
        this.mc.mcProfiler.endStartSection("translucent");
        lvt_5_1_.renderBlockLayer(EnumWorldBlockLayer.TRANSLUCENT, (double)partialTicks, pass, lvt_9_1_);
        GlStateManager.shadeModel(7424);
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.disableFog();

        if (lvt_9_1_.posY + (double)lvt_9_1_.getEyeHeight() >= 128.0D)
        {
            this.mc.mcProfiler.endStartSection("aboveClouds");
            this.renderCloudsCheck(lvt_5_1_, partialTicks, pass);
        }

        this.mc.mcProfiler.endStartSection("hand");

        if (this.renderHand)
        {
            GlStateManager.clear(256);
            this.renderHand(partialTicks, pass);
            this.renderWorldDirections(partialTicks);
        }
    }

    private void renderCloudsCheck(RenderGlobal renderGlobalIn, float partialTicks, int pass)
    {
        if (this.mc.gameSettings.shouldRenderClouds() != 0)
        {
            this.mc.mcProfiler.endStartSection("clouds");
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance * 4.0F);
            GlStateManager.matrixMode(5888);
            GlStateManager.pushMatrix();
            this.setupFog(0, partialTicks);
            renderGlobalIn.renderClouds(partialTicks, pass);
            GlStateManager.disableFog();
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance * MathHelper.SQRT_2);
            GlStateManager.matrixMode(5888);
        }
    }

    private void addRainParticles()
    {
        float lvt_1_1_ = this.mc.theWorld.getRainStrength(1.0F);

        if (!this.mc.gameSettings.fancyGraphics)
        {
            lvt_1_1_ /= 2.0F;
        }

        if (lvt_1_1_ != 0.0F)
        {
            this.random.setSeed((long)this.rendererUpdateCount * 312987231L);
            Entity lvt_2_1_ = this.mc.getRenderViewEntity();
            World lvt_3_1_ = this.mc.theWorld;
            BlockPos lvt_4_1_ = new BlockPos(lvt_2_1_);
            int lvt_5_1_ = 10;
            double lvt_6_1_ = 0.0D;
            double lvt_8_1_ = 0.0D;
            double lvt_10_1_ = 0.0D;
            int lvt_12_1_ = 0;
            int lvt_13_1_ = (int)(100.0F * lvt_1_1_ * lvt_1_1_);

            if (this.mc.gameSettings.particleSetting == 1)
            {
                lvt_13_1_ >>= 1;
            }
            else if (this.mc.gameSettings.particleSetting == 2)
            {
                lvt_13_1_ = 0;
            }

            for (int lvt_14_1_ = 0; lvt_14_1_ < lvt_13_1_; ++lvt_14_1_)
            {
                BlockPos lvt_15_1_ = lvt_3_1_.getPrecipitationHeight(lvt_4_1_.add(this.random.nextInt(lvt_5_1_) - this.random.nextInt(lvt_5_1_), 0, this.random.nextInt(lvt_5_1_) - this.random.nextInt(lvt_5_1_)));
                BiomeGenBase lvt_16_1_ = lvt_3_1_.getBiomeGenForCoords(lvt_15_1_);
                BlockPos lvt_17_1_ = lvt_15_1_.down();
                Block lvt_18_1_ = lvt_3_1_.getBlockState(lvt_17_1_).getBlock();

                if (lvt_15_1_.getY() <= lvt_4_1_.getY() + lvt_5_1_ && lvt_15_1_.getY() >= lvt_4_1_.getY() - lvt_5_1_ && lvt_16_1_.canRain() && lvt_16_1_.getFloatTemperature(lvt_15_1_) >= 0.15F)
                {
                    double lvt_19_1_ = this.random.nextDouble();
                    double lvt_21_1_ = this.random.nextDouble();

                    if (lvt_18_1_.getMaterial() == Material.lava)
                    {
                        this.mc.theWorld.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (double)lvt_15_1_.getX() + lvt_19_1_, (double)((float)lvt_15_1_.getY() + 0.1F) - lvt_18_1_.getBlockBoundsMinY(), (double)lvt_15_1_.getZ() + lvt_21_1_, 0.0D, 0.0D, 0.0D, new int[0]);
                    }
                    else if (lvt_18_1_.getMaterial() != Material.air)
                    {
                        lvt_18_1_.setBlockBoundsBasedOnState(lvt_3_1_, lvt_17_1_);
                        ++lvt_12_1_;

                        if (this.random.nextInt(lvt_12_1_) == 0)
                        {
                            lvt_6_1_ = (double)lvt_17_1_.getX() + lvt_19_1_;
                            lvt_8_1_ = (double)((float)lvt_17_1_.getY() + 0.1F) + lvt_18_1_.getBlockBoundsMaxY() - 1.0D;
                            lvt_10_1_ = (double)lvt_17_1_.getZ() + lvt_21_1_;
                        }

                        this.mc.theWorld.spawnParticle(EnumParticleTypes.WATER_DROP, (double)lvt_17_1_.getX() + lvt_19_1_, (double)((float)lvt_17_1_.getY() + 0.1F) + lvt_18_1_.getBlockBoundsMaxY(), (double)lvt_17_1_.getZ() + lvt_21_1_, 0.0D, 0.0D, 0.0D, new int[0]);
                    }
                }
            }

            if (lvt_12_1_ > 0 && this.random.nextInt(3) < this.rainSoundCounter++)
            {
                this.rainSoundCounter = 0;

                if (lvt_8_1_ > (double)(lvt_4_1_.getY() + 1) && lvt_3_1_.getPrecipitationHeight(lvt_4_1_).getY() > MathHelper.floor_float((float)lvt_4_1_.getY()))
                {
                    this.mc.theWorld.playSound(lvt_6_1_, lvt_8_1_, lvt_10_1_, "ambient.weather.rain", 0.1F, 0.5F, false);
                }
                else
                {
                    this.mc.theWorld.playSound(lvt_6_1_, lvt_8_1_, lvt_10_1_, "ambient.weather.rain", 0.2F, 1.0F, false);
                }
            }
        }
    }

    /**
     * Render rain and snow
     */
    protected void renderRainSnow(float partialTicks)
    {
        float lvt_2_1_ = this.mc.theWorld.getRainStrength(partialTicks);

        if (lvt_2_1_ > 0.0F)
        {
            this.enableLightmap();
            Entity lvt_3_1_ = this.mc.getRenderViewEntity();
            World lvt_4_1_ = this.mc.theWorld;
            int lvt_5_1_ = MathHelper.floor_double(lvt_3_1_.posX);
            int lvt_6_1_ = MathHelper.floor_double(lvt_3_1_.posY);
            int lvt_7_1_ = MathHelper.floor_double(lvt_3_1_.posZ);
            Tessellator lvt_8_1_ = Tessellator.getInstance();
            WorldRenderer lvt_9_1_ = lvt_8_1_.getWorldRenderer();
            GlStateManager.disableCull();
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.alphaFunc(516, 0.1F);
            double lvt_10_1_ = lvt_3_1_.lastTickPosX + (lvt_3_1_.posX - lvt_3_1_.lastTickPosX) * (double)partialTicks;
            double lvt_12_1_ = lvt_3_1_.lastTickPosY + (lvt_3_1_.posY - lvt_3_1_.lastTickPosY) * (double)partialTicks;
            double lvt_14_1_ = lvt_3_1_.lastTickPosZ + (lvt_3_1_.posZ - lvt_3_1_.lastTickPosZ) * (double)partialTicks;
            int lvt_16_1_ = MathHelper.floor_double(lvt_12_1_);
            int lvt_17_1_ = 5;

            if (this.mc.gameSettings.fancyGraphics)
            {
                lvt_17_1_ = 10;
            }

            int lvt_18_1_ = -1;
            float lvt_19_1_ = (float)this.rendererUpdateCount + partialTicks;
            lvt_9_1_.setTranslation(-lvt_10_1_, -lvt_12_1_, -lvt_14_1_);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            BlockPos.MutableBlockPos lvt_20_1_ = new BlockPos.MutableBlockPos();

            for (int lvt_21_1_ = lvt_7_1_ - lvt_17_1_; lvt_21_1_ <= lvt_7_1_ + lvt_17_1_; ++lvt_21_1_)
            {
                for (int lvt_22_1_ = lvt_5_1_ - lvt_17_1_; lvt_22_1_ <= lvt_5_1_ + lvt_17_1_; ++lvt_22_1_)
                {
                    int lvt_23_1_ = (lvt_21_1_ - lvt_7_1_ + 16) * 32 + lvt_22_1_ - lvt_5_1_ + 16;
                    double lvt_24_1_ = (double)this.rainXCoords[lvt_23_1_] * 0.5D;
                    double lvt_26_1_ = (double)this.rainYCoords[lvt_23_1_] * 0.5D;
                    lvt_20_1_.set(lvt_22_1_, 0, lvt_21_1_);
                    BiomeGenBase lvt_28_1_ = lvt_4_1_.getBiomeGenForCoords(lvt_20_1_);

                    if (lvt_28_1_.canRain() || lvt_28_1_.getEnableSnow())
                    {
                        int lvt_29_1_ = lvt_4_1_.getPrecipitationHeight(lvt_20_1_).getY();
                        int lvt_30_1_ = lvt_6_1_ - lvt_17_1_;
                        int lvt_31_1_ = lvt_6_1_ + lvt_17_1_;

                        if (lvt_30_1_ < lvt_29_1_)
                        {
                            lvt_30_1_ = lvt_29_1_;
                        }

                        if (lvt_31_1_ < lvt_29_1_)
                        {
                            lvt_31_1_ = lvt_29_1_;
                        }

                        int lvt_32_1_ = lvt_29_1_;

                        if (lvt_29_1_ < lvt_16_1_)
                        {
                            lvt_32_1_ = lvt_16_1_;
                        }

                        if (lvt_30_1_ != lvt_31_1_)
                        {
                            this.random.setSeed((long)(lvt_22_1_ * lvt_22_1_ * 3121 + lvt_22_1_ * 45238971 ^ lvt_21_1_ * lvt_21_1_ * 418711 + lvt_21_1_ * 13761));
                            lvt_20_1_.set(lvt_22_1_, lvt_30_1_, lvt_21_1_);
                            float lvt_33_1_ = lvt_28_1_.getFloatTemperature(lvt_20_1_);

                            if (lvt_4_1_.getWorldChunkManager().getTemperatureAtHeight(lvt_33_1_, lvt_29_1_) >= 0.15F)
                            {
                                if (lvt_18_1_ != 0)
                                {
                                    if (lvt_18_1_ >= 0)
                                    {
                                        lvt_8_1_.draw();
                                    }

                                    lvt_18_1_ = 0;
                                    this.mc.getTextureManager().bindTexture(locationRainPng);
                                    lvt_9_1_.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                                }

                                double lvt_34_1_ = ((double)(this.rendererUpdateCount + lvt_22_1_ * lvt_22_1_ * 3121 + lvt_22_1_ * 45238971 + lvt_21_1_ * lvt_21_1_ * 418711 + lvt_21_1_ * 13761 & 31) + (double)partialTicks) / 32.0D * (3.0D + this.random.nextDouble());
                                double lvt_36_1_ = (double)((float)lvt_22_1_ + 0.5F) - lvt_3_1_.posX;
                                double lvt_38_1_ = (double)((float)lvt_21_1_ + 0.5F) - lvt_3_1_.posZ;
                                float lvt_40_1_ = MathHelper.sqrt_double(lvt_36_1_ * lvt_36_1_ + lvt_38_1_ * lvt_38_1_) / (float)lvt_17_1_;
                                float lvt_41_1_ = ((1.0F - lvt_40_1_ * lvt_40_1_) * 0.5F + 0.5F) * lvt_2_1_;
                                lvt_20_1_.set(lvt_22_1_, lvt_32_1_, lvt_21_1_);
                                int lvt_42_1_ = lvt_4_1_.getCombinedLight(lvt_20_1_, 0);
                                int lvt_43_1_ = lvt_42_1_ >> 16 & 65535;
                                int lvt_44_1_ = lvt_42_1_ & 65535;
                                lvt_9_1_.pos((double)lvt_22_1_ - lvt_24_1_ + 0.5D, (double)lvt_30_1_, (double)lvt_21_1_ - lvt_26_1_ + 0.5D).tex(0.0D, (double)lvt_30_1_ * 0.25D + lvt_34_1_).color(1.0F, 1.0F, 1.0F, lvt_41_1_).lightmap(lvt_43_1_, lvt_44_1_).endVertex();
                                lvt_9_1_.pos((double)lvt_22_1_ + lvt_24_1_ + 0.5D, (double)lvt_30_1_, (double)lvt_21_1_ + lvt_26_1_ + 0.5D).tex(1.0D, (double)lvt_30_1_ * 0.25D + lvt_34_1_).color(1.0F, 1.0F, 1.0F, lvt_41_1_).lightmap(lvt_43_1_, lvt_44_1_).endVertex();
                                lvt_9_1_.pos((double)lvt_22_1_ + lvt_24_1_ + 0.5D, (double)lvt_31_1_, (double)lvt_21_1_ + lvt_26_1_ + 0.5D).tex(1.0D, (double)lvt_31_1_ * 0.25D + lvt_34_1_).color(1.0F, 1.0F, 1.0F, lvt_41_1_).lightmap(lvt_43_1_, lvt_44_1_).endVertex();
                                lvt_9_1_.pos((double)lvt_22_1_ - lvt_24_1_ + 0.5D, (double)lvt_31_1_, (double)lvt_21_1_ - lvt_26_1_ + 0.5D).tex(0.0D, (double)lvt_31_1_ * 0.25D + lvt_34_1_).color(1.0F, 1.0F, 1.0F, lvt_41_1_).lightmap(lvt_43_1_, lvt_44_1_).endVertex();
                            }
                            else
                            {
                                if (lvt_18_1_ != 1)
                                {
                                    if (lvt_18_1_ >= 0)
                                    {
                                        lvt_8_1_.draw();
                                    }

                                    lvt_18_1_ = 1;
                                    this.mc.getTextureManager().bindTexture(locationSnowPng);
                                    lvt_9_1_.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                                }

                                double lvt_34_2_ = (double)(((float)(this.rendererUpdateCount & 511) + partialTicks) / 512.0F);
                                double lvt_36_2_ = this.random.nextDouble() + (double)lvt_19_1_ * 0.01D * (double)((float)this.random.nextGaussian());
                                double lvt_38_2_ = this.random.nextDouble() + (double)(lvt_19_1_ * (float)this.random.nextGaussian()) * 0.001D;
                                double lvt_40_2_ = (double)((float)lvt_22_1_ + 0.5F) - lvt_3_1_.posX;
                                double lvt_42_2_ = (double)((float)lvt_21_1_ + 0.5F) - lvt_3_1_.posZ;
                                float lvt_44_2_ = MathHelper.sqrt_double(lvt_40_2_ * lvt_40_2_ + lvt_42_2_ * lvt_42_2_) / (float)lvt_17_1_;
                                float lvt_45_1_ = ((1.0F - lvt_44_2_ * lvt_44_2_) * 0.3F + 0.5F) * lvt_2_1_;
                                lvt_20_1_.set(lvt_22_1_, lvt_32_1_, lvt_21_1_);
                                int lvt_46_1_ = (lvt_4_1_.getCombinedLight(lvt_20_1_, 0) * 3 + 15728880) / 4;
                                int lvt_47_1_ = lvt_46_1_ >> 16 & 65535;
                                int lvt_48_1_ = lvt_46_1_ & 65535;
                                lvt_9_1_.pos((double)lvt_22_1_ - lvt_24_1_ + 0.5D, (double)lvt_30_1_, (double)lvt_21_1_ - lvt_26_1_ + 0.5D).tex(0.0D + lvt_36_2_, (double)lvt_30_1_ * 0.25D + lvt_34_2_ + lvt_38_2_).color(1.0F, 1.0F, 1.0F, lvt_45_1_).lightmap(lvt_47_1_, lvt_48_1_).endVertex();
                                lvt_9_1_.pos((double)lvt_22_1_ + lvt_24_1_ + 0.5D, (double)lvt_30_1_, (double)lvt_21_1_ + lvt_26_1_ + 0.5D).tex(1.0D + lvt_36_2_, (double)lvt_30_1_ * 0.25D + lvt_34_2_ + lvt_38_2_).color(1.0F, 1.0F, 1.0F, lvt_45_1_).lightmap(lvt_47_1_, lvt_48_1_).endVertex();
                                lvt_9_1_.pos((double)lvt_22_1_ + lvt_24_1_ + 0.5D, (double)lvt_31_1_, (double)lvt_21_1_ + lvt_26_1_ + 0.5D).tex(1.0D + lvt_36_2_, (double)lvt_31_1_ * 0.25D + lvt_34_2_ + lvt_38_2_).color(1.0F, 1.0F, 1.0F, lvt_45_1_).lightmap(lvt_47_1_, lvt_48_1_).endVertex();
                                lvt_9_1_.pos((double)lvt_22_1_ - lvt_24_1_ + 0.5D, (double)lvt_31_1_, (double)lvt_21_1_ - lvt_26_1_ + 0.5D).tex(0.0D + lvt_36_2_, (double)lvt_31_1_ * 0.25D + lvt_34_2_ + lvt_38_2_).color(1.0F, 1.0F, 1.0F, lvt_45_1_).lightmap(lvt_47_1_, lvt_48_1_).endVertex();
                            }
                        }
                    }
                }
            }

            if (lvt_18_1_ >= 0)
            {
                lvt_8_1_.draw();
            }

            lvt_9_1_.setTranslation(0.0D, 0.0D, 0.0D);
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1F);
            this.disableLightmap();
        }
    }

    /**
     * Setup orthogonal projection for rendering GUI screen overlays
     */
    public void setupOverlayRendering()
    {
        ScaledResolution lvt_1_1_ = new ScaledResolution(this.mc);
        GlStateManager.clear(256);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, lvt_1_1_.getScaledWidth_double(), lvt_1_1_.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
    }

    /**
     * calculates fog and calls glClearColor
     */
    private void updateFogColor(float partialTicks)
    {
        World lvt_2_1_ = this.mc.theWorld;
        Entity lvt_3_1_ = this.mc.getRenderViewEntity();
        float lvt_4_1_ = 0.25F + 0.75F * (float)this.mc.gameSettings.renderDistanceChunks / 32.0F;
        lvt_4_1_ = 1.0F - (float)Math.pow((double)lvt_4_1_, 0.25D);
        Vec3 lvt_5_1_ = lvt_2_1_.getSkyColor(this.mc.getRenderViewEntity(), partialTicks);
        float lvt_6_1_ = (float)lvt_5_1_.xCoord;
        float lvt_7_1_ = (float)lvt_5_1_.yCoord;
        float lvt_8_1_ = (float)lvt_5_1_.zCoord;
        Vec3 lvt_9_1_ = lvt_2_1_.getFogColor(partialTicks);
        this.fogColorRed = (float)lvt_9_1_.xCoord;
        this.fogColorGreen = (float)lvt_9_1_.yCoord;
        this.fogColorBlue = (float)lvt_9_1_.zCoord;

        if (this.mc.gameSettings.renderDistanceChunks >= 4)
        {
            double lvt_10_1_ = -1.0D;
            Vec3 lvt_12_1_ = MathHelper.sin(lvt_2_1_.getCelestialAngleRadians(partialTicks)) > 0.0F ? new Vec3(lvt_10_1_, 0.0D, 0.0D) : new Vec3(1.0D, 0.0D, 0.0D);
            float lvt_13_1_ = (float)lvt_3_1_.getLook(partialTicks).dotProduct(lvt_12_1_);

            if (lvt_13_1_ < 0.0F)
            {
                lvt_13_1_ = 0.0F;
            }

            if (lvt_13_1_ > 0.0F)
            {
                float[] lvt_14_1_ = lvt_2_1_.provider.calcSunriseSunsetColors(lvt_2_1_.getCelestialAngle(partialTicks), partialTicks);

                if (lvt_14_1_ != null)
                {
                    lvt_13_1_ = lvt_13_1_ * lvt_14_1_[3];
                    this.fogColorRed = this.fogColorRed * (1.0F - lvt_13_1_) + lvt_14_1_[0] * lvt_13_1_;
                    this.fogColorGreen = this.fogColorGreen * (1.0F - lvt_13_1_) + lvt_14_1_[1] * lvt_13_1_;
                    this.fogColorBlue = this.fogColorBlue * (1.0F - lvt_13_1_) + lvt_14_1_[2] * lvt_13_1_;
                }
            }
        }

        this.fogColorRed += (lvt_6_1_ - this.fogColorRed) * lvt_4_1_;
        this.fogColorGreen += (lvt_7_1_ - this.fogColorGreen) * lvt_4_1_;
        this.fogColorBlue += (lvt_8_1_ - this.fogColorBlue) * lvt_4_1_;
        float lvt_10_2_ = lvt_2_1_.getRainStrength(partialTicks);

        if (lvt_10_2_ > 0.0F)
        {
            float lvt_11_1_ = 1.0F - lvt_10_2_ * 0.5F;
            float lvt_12_2_ = 1.0F - lvt_10_2_ * 0.4F;
            this.fogColorRed *= lvt_11_1_;
            this.fogColorGreen *= lvt_11_1_;
            this.fogColorBlue *= lvt_12_2_;
        }

        float lvt_11_2_ = lvt_2_1_.getThunderStrength(partialTicks);

        if (lvt_11_2_ > 0.0F)
        {
            float lvt_12_3_ = 1.0F - lvt_11_2_ * 0.5F;
            this.fogColorRed *= lvt_12_3_;
            this.fogColorGreen *= lvt_12_3_;
            this.fogColorBlue *= lvt_12_3_;
        }

        Block lvt_12_4_ = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, lvt_3_1_, partialTicks);

        if (this.cloudFog)
        {
            Vec3 lvt_13_2_ = lvt_2_1_.getCloudColour(partialTicks);
            this.fogColorRed = (float)lvt_13_2_.xCoord;
            this.fogColorGreen = (float)lvt_13_2_.yCoord;
            this.fogColorBlue = (float)lvt_13_2_.zCoord;
        }
        else if (lvt_12_4_.getMaterial() == Material.water)
        {
            float lvt_13_3_ = (float)EnchantmentHelper.getRespiration(lvt_3_1_) * 0.2F;

            if (lvt_3_1_ instanceof EntityLivingBase && ((EntityLivingBase)lvt_3_1_).isPotionActive(Potion.waterBreathing))
            {
                lvt_13_3_ = lvt_13_3_ * 0.3F + 0.6F;
            }

            this.fogColorRed = 0.02F + lvt_13_3_;
            this.fogColorGreen = 0.02F + lvt_13_3_;
            this.fogColorBlue = 0.2F + lvt_13_3_;
        }
        else if (lvt_12_4_.getMaterial() == Material.lava)
        {
            this.fogColorRed = 0.6F;
            this.fogColorGreen = 0.1F;
            this.fogColorBlue = 0.0F;
        }

        float lvt_13_4_ = this.fogColor2 + (this.fogColor1 - this.fogColor2) * partialTicks;
        this.fogColorRed *= lvt_13_4_;
        this.fogColorGreen *= lvt_13_4_;
        this.fogColorBlue *= lvt_13_4_;
        double lvt_14_2_ = (lvt_3_1_.lastTickPosY + (lvt_3_1_.posY - lvt_3_1_.lastTickPosY) * (double)partialTicks) * lvt_2_1_.provider.getVoidFogYFactor();

        if (lvt_3_1_ instanceof EntityLivingBase && ((EntityLivingBase)lvt_3_1_).isPotionActive(Potion.blindness))
        {
            int lvt_16_1_ = ((EntityLivingBase)lvt_3_1_).getActivePotionEffect(Potion.blindness).getDuration();

            if (lvt_16_1_ < 20)
            {
                lvt_14_2_ *= (double)(1.0F - (float)lvt_16_1_ / 20.0F);
            }
            else
            {
                lvt_14_2_ = 0.0D;
            }
        }

        if (lvt_14_2_ < 1.0D)
        {
            if (lvt_14_2_ < 0.0D)
            {
                lvt_14_2_ = 0.0D;
            }

            lvt_14_2_ = lvt_14_2_ * lvt_14_2_;
            this.fogColorRed = (float)((double)this.fogColorRed * lvt_14_2_);
            this.fogColorGreen = (float)((double)this.fogColorGreen * lvt_14_2_);
            this.fogColorBlue = (float)((double)this.fogColorBlue * lvt_14_2_);
        }

        if (this.bossColorModifier > 0.0F)
        {
            float lvt_16_2_ = this.bossColorModifierPrev + (this.bossColorModifier - this.bossColorModifierPrev) * partialTicks;
            this.fogColorRed = this.fogColorRed * (1.0F - lvt_16_2_) + this.fogColorRed * 0.7F * lvt_16_2_;
            this.fogColorGreen = this.fogColorGreen * (1.0F - lvt_16_2_) + this.fogColorGreen * 0.6F * lvt_16_2_;
            this.fogColorBlue = this.fogColorBlue * (1.0F - lvt_16_2_) + this.fogColorBlue * 0.6F * lvt_16_2_;
        }

        if (lvt_3_1_ instanceof EntityLivingBase && ((EntityLivingBase)lvt_3_1_).isPotionActive(Potion.nightVision))
        {
            float lvt_16_3_ = this.getNightVisionBrightness((EntityLivingBase)lvt_3_1_, partialTicks);
            float lvt_17_1_ = 1.0F / this.fogColorRed;

            if (lvt_17_1_ > 1.0F / this.fogColorGreen)
            {
                lvt_17_1_ = 1.0F / this.fogColorGreen;
            }

            if (lvt_17_1_ > 1.0F / this.fogColorBlue)
            {
                lvt_17_1_ = 1.0F / this.fogColorBlue;
            }

            this.fogColorRed = this.fogColorRed * (1.0F - lvt_16_3_) + this.fogColorRed * lvt_17_1_ * lvt_16_3_;
            this.fogColorGreen = this.fogColorGreen * (1.0F - lvt_16_3_) + this.fogColorGreen * lvt_17_1_ * lvt_16_3_;
            this.fogColorBlue = this.fogColorBlue * (1.0F - lvt_16_3_) + this.fogColorBlue * lvt_17_1_ * lvt_16_3_;
        }

        if (this.mc.gameSettings.anaglyph)
        {
            float lvt_16_4_ = (this.fogColorRed * 30.0F + this.fogColorGreen * 59.0F + this.fogColorBlue * 11.0F) / 100.0F;
            float lvt_17_2_ = (this.fogColorRed * 30.0F + this.fogColorGreen * 70.0F) / 100.0F;
            float lvt_18_1_ = (this.fogColorRed * 30.0F + this.fogColorBlue * 70.0F) / 100.0F;
            this.fogColorRed = lvt_16_4_;
            this.fogColorGreen = lvt_17_2_;
            this.fogColorBlue = lvt_18_1_;
        }

        GlStateManager.clearColor(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 0.0F);
    }

    /**
     * Sets up the fog to be rendered. If the arg passed in is -1 the fog starts at 0 and goes to 80% of far plane
     * distance and is used for sky rendering.
     *  
     * @param startCoords If is -1 the fog start at 0.0
     */
    private void setupFog(int startCoords, float partialTicks)
    {
        Entity lvt_3_1_ = this.mc.getRenderViewEntity();
        boolean lvt_4_1_ = false;

        if (lvt_3_1_ instanceof EntityPlayer)
        {
            lvt_4_1_ = ((EntityPlayer)lvt_3_1_).capabilities.isCreativeMode;
        }

        GL11.glFog(GL11.GL_FOG_COLOR, this.setFogColorBuffer(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 1.0F));
        GL11.glNormal3f(0.0F, -1.0F, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Block lvt_5_1_ = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, lvt_3_1_, partialTicks);

        if (lvt_3_1_ instanceof EntityLivingBase && ((EntityLivingBase)lvt_3_1_).isPotionActive(Potion.blindness))
        {
            float lvt_6_1_ = 5.0F;
            int lvt_7_1_ = ((EntityLivingBase)lvt_3_1_).getActivePotionEffect(Potion.blindness).getDuration();

            if (lvt_7_1_ < 20)
            {
                lvt_6_1_ = 5.0F + (this.farPlaneDistance - 5.0F) * (1.0F - (float)lvt_7_1_ / 20.0F);
            }

            GlStateManager.setFog(9729);

            if (startCoords == -1)
            {
                GlStateManager.setFogStart(0.0F);
                GlStateManager.setFogEnd(lvt_6_1_ * 0.8F);
            }
            else
            {
                GlStateManager.setFogStart(lvt_6_1_ * 0.25F);
                GlStateManager.setFogEnd(lvt_6_1_);
            }

            if (GLContext.getCapabilities().GL_NV_fog_distance)
            {
                GL11.glFogi(34138, 34139);
            }
        }
        else if (this.cloudFog)
        {
            GlStateManager.setFog(2048);
            GlStateManager.setFogDensity(0.1F);
        }
        else if (lvt_5_1_.getMaterial() == Material.water)
        {
            GlStateManager.setFog(2048);

            if (lvt_3_1_ instanceof EntityLivingBase && ((EntityLivingBase)lvt_3_1_).isPotionActive(Potion.waterBreathing))
            {
                GlStateManager.setFogDensity(0.01F);
            }
            else
            {
                GlStateManager.setFogDensity(0.1F - (float)EnchantmentHelper.getRespiration(lvt_3_1_) * 0.03F);
            }
        }
        else if (lvt_5_1_.getMaterial() == Material.lava)
        {
            GlStateManager.setFog(2048);
            GlStateManager.setFogDensity(2.0F);
        }
        else
        {
            float lvt_6_2_ = this.farPlaneDistance;
            GlStateManager.setFog(9729);

            if (startCoords == -1)
            {
                GlStateManager.setFogStart(0.0F);
                GlStateManager.setFogEnd(lvt_6_2_);
            }
            else
            {
                GlStateManager.setFogStart(lvt_6_2_ * 0.75F);
                GlStateManager.setFogEnd(lvt_6_2_);
            }

            if (GLContext.getCapabilities().GL_NV_fog_distance)
            {
                GL11.glFogi(34138, 34139);
            }

            if (this.mc.theWorld.provider.doesXZShowFog((int)lvt_3_1_.posX, (int)lvt_3_1_.posZ))
            {
                GlStateManager.setFogStart(lvt_6_2_ * 0.05F);
                GlStateManager.setFogEnd(Math.min(lvt_6_2_, 192.0F) * 0.5F);
            }
        }

        GlStateManager.enableColorMaterial();
        GlStateManager.enableFog();
        GlStateManager.colorMaterial(1028, 4608);
    }

    /**
     * Update and return fogColorBuffer with the RGBA values passed as arguments
     */
    private FloatBuffer setFogColorBuffer(float red, float green, float blue, float alpha)
    {
        this.fogColorBuffer.clear();
        this.fogColorBuffer.put(red).put(green).put(blue).put(alpha);
        this.fogColorBuffer.flip();
        return this.fogColorBuffer;
    }

    public MapItemRenderer getMapItemRenderer()
    {
        return this.theMapItemRenderer;
    }
}
