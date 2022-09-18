package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.ListChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.VboChunkFactory;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemRecord;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Matrix4f;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vector3d;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class RenderGlobal implements IWorldAccess, IResourceManagerReloadListener
{
    private static final Logger logger = LogManager.getLogger();
    private static final ResourceLocation locationMoonPhasesPng = new ResourceLocation("textures/environment/moon_phases.png");
    private static final ResourceLocation locationSunPng = new ResourceLocation("textures/environment/sun.png");
    private static final ResourceLocation locationCloudsPng = new ResourceLocation("textures/environment/clouds.png");
    private static final ResourceLocation locationEndSkyPng = new ResourceLocation("textures/environment/end_sky.png");
    private static final ResourceLocation locationForcefieldPng = new ResourceLocation("textures/misc/forcefield.png");

    /** A reference to the Minecraft object. */
    private final Minecraft mc;

    /** The RenderEngine instance used by RenderGlobal */
    private final TextureManager renderEngine;
    private final RenderManager renderManager;
    private WorldClient theWorld;
    private Set<RenderChunk> chunksToUpdate = Sets.newLinkedHashSet();
    private List<RenderGlobal.ContainerLocalRenderInformation> renderInfos = Lists.newArrayListWithCapacity(69696);
    private final Set<TileEntity> setTileEntities = Sets.newHashSet();
    private ViewFrustum viewFrustum;

    /** The star GL Call list */
    private int starGLCallList = -1;

    /** OpenGL sky list */
    private int glSkyList = -1;

    /** OpenGL sky list 2 */
    private int glSkyList2 = -1;
    private VertexFormat vertexBufferFormat;
    private VertexBuffer starVBO;
    private VertexBuffer skyVBO;
    private VertexBuffer sky2VBO;

    /**
     * counts the cloud render updates. Used with mod to stagger some updates
     */
    private int cloudTickCounter;
    private final Map<Integer, DestroyBlockProgress> damagedBlocks = Maps.newHashMap();
    private final Map<BlockPos, ISound> mapSoundPositions = Maps.newHashMap();
    private final TextureAtlasSprite[] destroyBlockIcons = new TextureAtlasSprite[10];
    private Framebuffer entityOutlineFramebuffer;

    /** Stores the shader group for the entity_outline shader */
    private ShaderGroup entityOutlineShader;
    private double frustumUpdatePosX = Double.MIN_VALUE;
    private double frustumUpdatePosY = Double.MIN_VALUE;
    private double frustumUpdatePosZ = Double.MIN_VALUE;
    private int frustumUpdatePosChunkX = Integer.MIN_VALUE;
    private int frustumUpdatePosChunkY = Integer.MIN_VALUE;
    private int frustumUpdatePosChunkZ = Integer.MIN_VALUE;
    private double lastViewEntityX = Double.MIN_VALUE;
    private double lastViewEntityY = Double.MIN_VALUE;
    private double lastViewEntityZ = Double.MIN_VALUE;
    private double lastViewEntityPitch = Double.MIN_VALUE;
    private double lastViewEntityYaw = Double.MIN_VALUE;
    private final ChunkRenderDispatcher renderDispatcher = new ChunkRenderDispatcher();
    private ChunkRenderContainer renderContainer;
    private int renderDistanceChunks = -1;

    /** Render entities startup counter (init value=2) */
    private int renderEntitiesStartupCounter = 2;

    /** Count entities total */
    private int countEntitiesTotal;

    /** Count entities rendered */
    private int countEntitiesRendered;

    /** Count entities hidden */
    private int countEntitiesHidden;
    private boolean debugFixTerrainFrustum = false;
    private ClippingHelper debugFixedClippingHelper;
    private final Vector4f[] debugTerrainMatrix = new Vector4f[8];
    private final Vector3d debugTerrainFrustumPosition = new Vector3d();
    private boolean vboEnabled = false;
    IRenderChunkFactory renderChunkFactory;
    private double prevRenderSortX;
    private double prevRenderSortY;
    private double prevRenderSortZ;
    private boolean displayListEntitiesDirty = true;

    public RenderGlobal(Minecraft mcIn)
    {
        this.mc = mcIn;
        this.renderManager = mcIn.getRenderManager();
        this.renderEngine = mcIn.getTextureManager();
        this.renderEngine.bindTexture(locationForcefieldPng);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GlStateManager.bindTexture(0);
        this.updateDestroyBlockIcons();
        this.vboEnabled = OpenGlHelper.useVbo();

        if (this.vboEnabled)
        {
            this.renderContainer = new VboRenderList();
            this.renderChunkFactory = new VboChunkFactory();
        }
        else
        {
            this.renderContainer = new RenderList();
            this.renderChunkFactory = new ListChunkFactory();
        }

        this.vertexBufferFormat = new VertexFormat();
        this.vertexBufferFormat.addElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.POSITION, 3));
        this.generateStars();
        this.generateSky();
        this.generateSky2();
    }

    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        this.updateDestroyBlockIcons();
    }

    private void updateDestroyBlockIcons()
    {
        TextureMap lvt_1_1_ = this.mc.getTextureMapBlocks();

        for (int lvt_2_1_ = 0; lvt_2_1_ < this.destroyBlockIcons.length; ++lvt_2_1_)
        {
            this.destroyBlockIcons[lvt_2_1_] = lvt_1_1_.getAtlasSprite("minecraft:blocks/destroy_stage_" + lvt_2_1_);
        }
    }

    /**
     * Creates the entity outline shader to be stored in RenderGlobal.entityOutlineShader
     */
    public void makeEntityOutlineShader()
    {
        if (OpenGlHelper.shadersSupported)
        {
            if (ShaderLinkHelper.getStaticShaderLinkHelper() == null)
            {
                ShaderLinkHelper.setNewStaticShaderLinkHelper();
            }

            ResourceLocation lvt_1_1_ = new ResourceLocation("shaders/post/entity_outline.json");

            try
            {
                this.entityOutlineShader = new ShaderGroup(this.mc.getTextureManager(), this.mc.getResourceManager(), this.mc.getFramebuffer(), lvt_1_1_);
                this.entityOutlineShader.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
                this.entityOutlineFramebuffer = this.entityOutlineShader.getFramebufferRaw("final");
            }
            catch (IOException var3)
            {
                logger.warn("Failed to load shader: " + lvt_1_1_, var3);
                this.entityOutlineShader = null;
                this.entityOutlineFramebuffer = null;
            }
            catch (JsonSyntaxException var4)
            {
                logger.warn("Failed to load shader: " + lvt_1_1_, var4);
                this.entityOutlineShader = null;
                this.entityOutlineFramebuffer = null;
            }
        }
        else
        {
            this.entityOutlineShader = null;
            this.entityOutlineFramebuffer = null;
        }
    }

    public void renderEntityOutlineFramebuffer()
    {
        if (this.isRenderEntityOutlines())
        {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
            this.entityOutlineFramebuffer.framebufferRenderExt(this.mc.displayWidth, this.mc.displayHeight, false);
            GlStateManager.disableBlend();
        }
    }

    protected boolean isRenderEntityOutlines()
    {
        return this.entityOutlineFramebuffer != null && this.entityOutlineShader != null && this.mc.thePlayer != null && this.mc.thePlayer.isSpectator() && this.mc.gameSettings.keyBindSpectatorOutlines.isKeyDown();
    }

    private void generateSky2()
    {
        Tessellator lvt_1_1_ = Tessellator.getInstance();
        WorldRenderer lvt_2_1_ = lvt_1_1_.getWorldRenderer();

        if (this.sky2VBO != null)
        {
            this.sky2VBO.deleteGlBuffers();
        }

        if (this.glSkyList2 >= 0)
        {
            GLAllocation.deleteDisplayLists(this.glSkyList2);
            this.glSkyList2 = -1;
        }

        if (this.vboEnabled)
        {
            this.sky2VBO = new VertexBuffer(this.vertexBufferFormat);
            this.renderSky(lvt_2_1_, -16.0F, true);
            lvt_2_1_.finishDrawing();
            lvt_2_1_.reset();
            this.sky2VBO.bufferData(lvt_2_1_.getByteBuffer());
        }
        else
        {
            this.glSkyList2 = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(this.glSkyList2, GL11.GL_COMPILE);
            this.renderSky(lvt_2_1_, -16.0F, true);
            lvt_1_1_.draw();
            GL11.glEndList();
        }
    }

    private void generateSky()
    {
        Tessellator lvt_1_1_ = Tessellator.getInstance();
        WorldRenderer lvt_2_1_ = lvt_1_1_.getWorldRenderer();

        if (this.skyVBO != null)
        {
            this.skyVBO.deleteGlBuffers();
        }

        if (this.glSkyList >= 0)
        {
            GLAllocation.deleteDisplayLists(this.glSkyList);
            this.glSkyList = -1;
        }

        if (this.vboEnabled)
        {
            this.skyVBO = new VertexBuffer(this.vertexBufferFormat);
            this.renderSky(lvt_2_1_, 16.0F, false);
            lvt_2_1_.finishDrawing();
            lvt_2_1_.reset();
            this.skyVBO.bufferData(lvt_2_1_.getByteBuffer());
        }
        else
        {
            this.glSkyList = GLAllocation.generateDisplayLists(1);
            GL11.glNewList(this.glSkyList, GL11.GL_COMPILE);
            this.renderSky(lvt_2_1_, 16.0F, false);
            lvt_1_1_.draw();
            GL11.glEndList();
        }
    }

    private void renderSky(WorldRenderer worldRendererIn, float posY, boolean reverseX)
    {
        int lvt_4_1_ = 64;
        int lvt_5_1_ = 6;
        worldRendererIn.begin(7, DefaultVertexFormats.POSITION);

        for (int lvt_6_1_ = -384; lvt_6_1_ <= 384; lvt_6_1_ += 64)
        {
            for (int lvt_7_1_ = -384; lvt_7_1_ <= 384; lvt_7_1_ += 64)
            {
                float lvt_8_1_ = (float)lvt_6_1_;
                float lvt_9_1_ = (float)(lvt_6_1_ + 64);

                if (reverseX)
                {
                    lvt_9_1_ = (float)lvt_6_1_;
                    lvt_8_1_ = (float)(lvt_6_1_ + 64);
                }

                worldRendererIn.pos((double)lvt_8_1_, (double)posY, (double)lvt_7_1_).endVertex();
                worldRendererIn.pos((double)lvt_9_1_, (double)posY, (double)lvt_7_1_).endVertex();
                worldRendererIn.pos((double)lvt_9_1_, (double)posY, (double)(lvt_7_1_ + 64)).endVertex();
                worldRendererIn.pos((double)lvt_8_1_, (double)posY, (double)(lvt_7_1_ + 64)).endVertex();
            }
        }
    }

    private void generateStars()
    {
        Tessellator lvt_1_1_ = Tessellator.getInstance();
        WorldRenderer lvt_2_1_ = lvt_1_1_.getWorldRenderer();

        if (this.starVBO != null)
        {
            this.starVBO.deleteGlBuffers();
        }

        if (this.starGLCallList >= 0)
        {
            GLAllocation.deleteDisplayLists(this.starGLCallList);
            this.starGLCallList = -1;
        }

        if (this.vboEnabled)
        {
            this.starVBO = new VertexBuffer(this.vertexBufferFormat);
            this.renderStars(lvt_2_1_);
            lvt_2_1_.finishDrawing();
            lvt_2_1_.reset();
            this.starVBO.bufferData(lvt_2_1_.getByteBuffer());
        }
        else
        {
            this.starGLCallList = GLAllocation.generateDisplayLists(1);
            GlStateManager.pushMatrix();
            GL11.glNewList(this.starGLCallList, GL11.GL_COMPILE);
            this.renderStars(lvt_2_1_);
            lvt_1_1_.draw();
            GL11.glEndList();
            GlStateManager.popMatrix();
        }
    }

    private void renderStars(WorldRenderer worldRendererIn)
    {
        Random lvt_2_1_ = new Random(10842L);
        worldRendererIn.begin(7, DefaultVertexFormats.POSITION);

        for (int lvt_3_1_ = 0; lvt_3_1_ < 1500; ++lvt_3_1_)
        {
            double lvt_4_1_ = (double)(lvt_2_1_.nextFloat() * 2.0F - 1.0F);
            double lvt_6_1_ = (double)(lvt_2_1_.nextFloat() * 2.0F - 1.0F);
            double lvt_8_1_ = (double)(lvt_2_1_.nextFloat() * 2.0F - 1.0F);
            double lvt_10_1_ = (double)(0.15F + lvt_2_1_.nextFloat() * 0.1F);
            double lvt_12_1_ = lvt_4_1_ * lvt_4_1_ + lvt_6_1_ * lvt_6_1_ + lvt_8_1_ * lvt_8_1_;

            if (lvt_12_1_ < 1.0D && lvt_12_1_ > 0.01D)
            {
                lvt_12_1_ = 1.0D / Math.sqrt(lvt_12_1_);
                lvt_4_1_ = lvt_4_1_ * lvt_12_1_;
                lvt_6_1_ = lvt_6_1_ * lvt_12_1_;
                lvt_8_1_ = lvt_8_1_ * lvt_12_1_;
                double lvt_14_1_ = lvt_4_1_ * 100.0D;
                double lvt_16_1_ = lvt_6_1_ * 100.0D;
                double lvt_18_1_ = lvt_8_1_ * 100.0D;
                double lvt_20_1_ = Math.atan2(lvt_4_1_, lvt_8_1_);
                double lvt_22_1_ = Math.sin(lvt_20_1_);
                double lvt_24_1_ = Math.cos(lvt_20_1_);
                double lvt_26_1_ = Math.atan2(Math.sqrt(lvt_4_1_ * lvt_4_1_ + lvt_8_1_ * lvt_8_1_), lvt_6_1_);
                double lvt_28_1_ = Math.sin(lvt_26_1_);
                double lvt_30_1_ = Math.cos(lvt_26_1_);
                double lvt_32_1_ = lvt_2_1_.nextDouble() * Math.PI * 2.0D;
                double lvt_34_1_ = Math.sin(lvt_32_1_);
                double lvt_36_1_ = Math.cos(lvt_32_1_);

                for (int lvt_38_1_ = 0; lvt_38_1_ < 4; ++lvt_38_1_)
                {
                    double lvt_39_1_ = 0.0D;
                    double lvt_41_1_ = (double)((lvt_38_1_ & 2) - 1) * lvt_10_1_;
                    double lvt_43_1_ = (double)((lvt_38_1_ + 1 & 2) - 1) * lvt_10_1_;
                    double lvt_45_1_ = 0.0D;
                    double lvt_47_1_ = lvt_41_1_ * lvt_36_1_ - lvt_43_1_ * lvt_34_1_;
                    double lvt_49_1_ = lvt_43_1_ * lvt_36_1_ + lvt_41_1_ * lvt_34_1_;
                    double lvt_53_1_ = lvt_47_1_ * lvt_28_1_ + 0.0D * lvt_30_1_;
                    double lvt_55_1_ = 0.0D * lvt_28_1_ - lvt_47_1_ * lvt_30_1_;
                    double lvt_57_1_ = lvt_55_1_ * lvt_22_1_ - lvt_49_1_ * lvt_24_1_;
                    double lvt_61_1_ = lvt_49_1_ * lvt_22_1_ + lvt_55_1_ * lvt_24_1_;
                    worldRendererIn.pos(lvt_14_1_ + lvt_57_1_, lvt_16_1_ + lvt_53_1_, lvt_18_1_ + lvt_61_1_).endVertex();
                }
            }
        }
    }

    /**
     * set null to clear
     */
    public void setWorldAndLoadRenderers(WorldClient worldClientIn)
    {
        if (this.theWorld != null)
        {
            this.theWorld.removeWorldAccess(this);
        }

        this.frustumUpdatePosX = Double.MIN_VALUE;
        this.frustumUpdatePosY = Double.MIN_VALUE;
        this.frustumUpdatePosZ = Double.MIN_VALUE;
        this.frustumUpdatePosChunkX = Integer.MIN_VALUE;
        this.frustumUpdatePosChunkY = Integer.MIN_VALUE;
        this.frustumUpdatePosChunkZ = Integer.MIN_VALUE;
        this.renderManager.set(worldClientIn);
        this.theWorld = worldClientIn;

        if (worldClientIn != null)
        {
            worldClientIn.addWorldAccess(this);
            this.loadRenderers();
        }
    }

    /**
     * Loads all the renderers and sets up the basic settings usage
     */
    public void loadRenderers()
    {
        if (this.theWorld != null)
        {
            this.displayListEntitiesDirty = true;
            Blocks.leaves.setGraphicsLevel(this.mc.gameSettings.fancyGraphics);
            Blocks.leaves2.setGraphicsLevel(this.mc.gameSettings.fancyGraphics);
            this.renderDistanceChunks = this.mc.gameSettings.renderDistanceChunks;
            boolean lvt_1_1_ = this.vboEnabled;
            this.vboEnabled = OpenGlHelper.useVbo();

            if (lvt_1_1_ && !this.vboEnabled)
            {
                this.renderContainer = new RenderList();
                this.renderChunkFactory = new ListChunkFactory();
            }
            else if (!lvt_1_1_ && this.vboEnabled)
            {
                this.renderContainer = new VboRenderList();
                this.renderChunkFactory = new VboChunkFactory();
            }

            if (lvt_1_1_ != this.vboEnabled)
            {
                this.generateStars();
                this.generateSky();
                this.generateSky2();
            }

            if (this.viewFrustum != null)
            {
                this.viewFrustum.deleteGlResources();
            }

            this.stopChunkUpdates();

            synchronized (this.setTileEntities)
            {
                this.setTileEntities.clear();
            }

            this.viewFrustum = new ViewFrustum(this.theWorld, this.mc.gameSettings.renderDistanceChunks, this, this.renderChunkFactory);

            if (this.theWorld != null)
            {
                Entity lvt_2_1_ = this.mc.getRenderViewEntity();

                if (lvt_2_1_ != null)
                {
                    this.viewFrustum.updateChunkPositions(lvt_2_1_.posX, lvt_2_1_.posZ);
                }
            }

            this.renderEntitiesStartupCounter = 2;
        }
    }

    protected void stopChunkUpdates()
    {
        this.chunksToUpdate.clear();
        this.renderDispatcher.stopChunkUpdates();
    }

    public void createBindEntityOutlineFbs(int width, int height)
    {
        if (OpenGlHelper.shadersSupported)
        {
            if (this.entityOutlineShader != null)
            {
                this.entityOutlineShader.createBindFramebuffers(width, height);
            }
        }
    }

    public void renderEntities(Entity renderViewEntity, ICamera camera, float partialTicks)
    {
        if (this.renderEntitiesStartupCounter > 0)
        {
            --this.renderEntitiesStartupCounter;
        }
        else
        {
            double lvt_4_1_ = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * (double)partialTicks;
            double lvt_6_1_ = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * (double)partialTicks;
            double lvt_8_1_ = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * (double)partialTicks;
            this.theWorld.theProfiler.startSection("prepare");
            TileEntityRendererDispatcher.instance.cacheActiveRenderInfo(this.theWorld, this.mc.getTextureManager(), this.mc.fontRendererObj, this.mc.getRenderViewEntity(), partialTicks);
            this.renderManager.cacheActiveRenderInfo(this.theWorld, this.mc.fontRendererObj, this.mc.getRenderViewEntity(), this.mc.pointedEntity, this.mc.gameSettings, partialTicks);
            this.countEntitiesTotal = 0;
            this.countEntitiesRendered = 0;
            this.countEntitiesHidden = 0;
            Entity lvt_10_1_ = this.mc.getRenderViewEntity();
            double lvt_11_1_ = lvt_10_1_.lastTickPosX + (lvt_10_1_.posX - lvt_10_1_.lastTickPosX) * (double)partialTicks;
            double lvt_13_1_ = lvt_10_1_.lastTickPosY + (lvt_10_1_.posY - lvt_10_1_.lastTickPosY) * (double)partialTicks;
            double lvt_15_1_ = lvt_10_1_.lastTickPosZ + (lvt_10_1_.posZ - lvt_10_1_.lastTickPosZ) * (double)partialTicks;
            TileEntityRendererDispatcher.staticPlayerX = lvt_11_1_;
            TileEntityRendererDispatcher.staticPlayerY = lvt_13_1_;
            TileEntityRendererDispatcher.staticPlayerZ = lvt_15_1_;
            this.renderManager.setRenderPosition(lvt_11_1_, lvt_13_1_, lvt_15_1_);
            this.mc.entityRenderer.enableLightmap();
            this.theWorld.theProfiler.endStartSection("global");
            List<Entity> lvt_17_1_ = this.theWorld.getLoadedEntityList();
            this.countEntitiesTotal = lvt_17_1_.size();

            for (int lvt_18_1_ = 0; lvt_18_1_ < this.theWorld.weatherEffects.size(); ++lvt_18_1_)
            {
                Entity lvt_19_1_ = (Entity)this.theWorld.weatherEffects.get(lvt_18_1_);
                ++this.countEntitiesRendered;

                if (lvt_19_1_.isInRangeToRender3d(lvt_4_1_, lvt_6_1_, lvt_8_1_))
                {
                    this.renderManager.renderEntitySimple(lvt_19_1_, partialTicks);
                }
            }

            if (this.isRenderEntityOutlines())
            {
                GlStateManager.depthFunc(519);
                GlStateManager.disableFog();
                this.entityOutlineFramebuffer.framebufferClear();
                this.entityOutlineFramebuffer.bindFramebuffer(false);
                this.theWorld.theProfiler.endStartSection("entityOutlines");
                RenderHelper.disableStandardItemLighting();
                this.renderManager.setRenderOutlines(true);

                for (int lvt_18_2_ = 0; lvt_18_2_ < lvt_17_1_.size(); ++lvt_18_2_)
                {
                    Entity lvt_19_2_ = (Entity)lvt_17_1_.get(lvt_18_2_);
                    boolean lvt_20_1_ = this.mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase)this.mc.getRenderViewEntity()).isPlayerSleeping();
                    boolean lvt_21_1_ = lvt_19_2_.isInRangeToRender3d(lvt_4_1_, lvt_6_1_, lvt_8_1_) && (lvt_19_2_.ignoreFrustumCheck || camera.isBoundingBoxInFrustum(lvt_19_2_.getEntityBoundingBox()) || lvt_19_2_.riddenByEntity == this.mc.thePlayer) && lvt_19_2_ instanceof EntityPlayer;

                    if ((lvt_19_2_ != this.mc.getRenderViewEntity() || this.mc.gameSettings.thirdPersonView != 0 || lvt_20_1_) && lvt_21_1_)
                    {
                        this.renderManager.renderEntitySimple(lvt_19_2_, partialTicks);
                    }
                }

                this.renderManager.setRenderOutlines(false);
                RenderHelper.enableStandardItemLighting();
                GlStateManager.depthMask(false);
                this.entityOutlineShader.loadShaderGroup(partialTicks);
                GlStateManager.enableLighting();
                GlStateManager.depthMask(true);
                this.mc.getFramebuffer().bindFramebuffer(false);
                GlStateManager.enableFog();
                GlStateManager.enableBlend();
                GlStateManager.enableColorMaterial();
                GlStateManager.depthFunc(515);
                GlStateManager.enableDepth();
                GlStateManager.enableAlpha();
            }

            this.theWorld.theProfiler.endStartSection("entities");
            label738:

            for (RenderGlobal.ContainerLocalRenderInformation lvt_19_3_ : this.renderInfos)
            {
                Chunk lvt_20_2_ = this.theWorld.getChunkFromBlockCoords(lvt_19_3_.renderChunk.getPosition());
                ClassInheritanceMultiMap<Entity> lvt_21_2_ = lvt_20_2_.getEntityLists()[lvt_19_3_.renderChunk.getPosition().getY() / 16];

                if (!lvt_21_2_.isEmpty())
                {
                    Iterator lvt_22_1_ = lvt_21_2_.iterator();

                    while (true)
                    {
                        Entity lvt_23_1_;
                        boolean lvt_24_1_;

                        while (true)
                        {
                            if (!lvt_22_1_.hasNext())
                            {
                                continue label738;
                            }

                            lvt_23_1_ = (Entity)lvt_22_1_.next();
                            lvt_24_1_ = this.renderManager.shouldRender(lvt_23_1_, camera, lvt_4_1_, lvt_6_1_, lvt_8_1_) || lvt_23_1_.riddenByEntity == this.mc.thePlayer;

                            if (!lvt_24_1_)
                            {
                                break;
                            }

                            boolean lvt_25_1_ = this.mc.getRenderViewEntity() instanceof EntityLivingBase ? ((EntityLivingBase)this.mc.getRenderViewEntity()).isPlayerSleeping() : false;

                            if ((lvt_23_1_ != this.mc.getRenderViewEntity() || this.mc.gameSettings.thirdPersonView != 0 || lvt_25_1_) && (lvt_23_1_.posY < 0.0D || lvt_23_1_.posY >= 256.0D || this.theWorld.isBlockLoaded(new BlockPos(lvt_23_1_))))
                            {
                                ++this.countEntitiesRendered;
                                this.renderManager.renderEntitySimple(lvt_23_1_, partialTicks);
                                break;
                            }
                        }

                        if (!lvt_24_1_ && lvt_23_1_ instanceof EntityWitherSkull)
                        {
                            this.mc.getRenderManager().renderWitherSkull(lvt_23_1_, partialTicks);
                        }
                    }
                }
            }

            this.theWorld.theProfiler.endStartSection("blockentities");
            RenderHelper.enableStandardItemLighting();

            for (RenderGlobal.ContainerLocalRenderInformation lvt_19_4_ : this.renderInfos)
            {
                List<TileEntity> lvt_20_3_ = lvt_19_4_.renderChunk.getCompiledChunk().getTileEntities();

                if (!lvt_20_3_.isEmpty())
                {
                    for (TileEntity lvt_22_2_ : lvt_20_3_)
                    {
                        TileEntityRendererDispatcher.instance.renderTileEntity(lvt_22_2_, partialTicks, -1);
                    }
                }
            }

            synchronized (this.setTileEntities)
            {
                for (TileEntity lvt_20_4_ : this.setTileEntities)
                {
                    TileEntityRendererDispatcher.instance.renderTileEntity(lvt_20_4_, partialTicks, -1);
                }
            }

            this.preRenderDamagedBlocks();

            for (DestroyBlockProgress lvt_19_6_ : this.damagedBlocks.values())
            {
                BlockPos lvt_20_5_ = lvt_19_6_.getPosition();
                TileEntity lvt_21_4_ = this.theWorld.getTileEntity(lvt_20_5_);

                if (lvt_21_4_ instanceof TileEntityChest)
                {
                    TileEntityChest lvt_22_3_ = (TileEntityChest)lvt_21_4_;

                    if (lvt_22_3_.adjacentChestXNeg != null)
                    {
                        lvt_20_5_ = lvt_20_5_.offset(EnumFacing.WEST);
                        lvt_21_4_ = this.theWorld.getTileEntity(lvt_20_5_);
                    }
                    else if (lvt_22_3_.adjacentChestZNeg != null)
                    {
                        lvt_20_5_ = lvt_20_5_.offset(EnumFacing.NORTH);
                        lvt_21_4_ = this.theWorld.getTileEntity(lvt_20_5_);
                    }
                }

                Block lvt_22_4_ = this.theWorld.getBlockState(lvt_20_5_).getBlock();

                if (lvt_21_4_ != null && (lvt_22_4_ instanceof BlockChest || lvt_22_4_ instanceof BlockEnderChest || lvt_22_4_ instanceof BlockSign || lvt_22_4_ instanceof BlockSkull))
                {
                    TileEntityRendererDispatcher.instance.renderTileEntity(lvt_21_4_, partialTicks, lvt_19_6_.getPartialBlockDamage());
                }
            }

            this.postRenderDamagedBlocks();
            this.mc.entityRenderer.disableLightmap();
            this.mc.mcProfiler.endSection();
        }
    }

    /**
     * Gets the render info for use on the Debug screen
     */
    public String getDebugInfoRenders()
    {
        int lvt_1_1_ = this.viewFrustum.renderChunks.length;
        int lvt_2_1_ = 0;

        for (RenderGlobal.ContainerLocalRenderInformation lvt_4_1_ : this.renderInfos)
        {
            CompiledChunk lvt_5_1_ = lvt_4_1_.renderChunk.compiledChunk;

            if (lvt_5_1_ != CompiledChunk.DUMMY && !lvt_5_1_.isEmpty())
            {
                ++lvt_2_1_;
            }
        }

        return String.format("C: %d/%d %sD: %d, %s", new Object[] {Integer.valueOf(lvt_2_1_), Integer.valueOf(lvt_1_1_), this.mc.renderChunksMany ? "(s) " : "", Integer.valueOf(this.renderDistanceChunks), this.renderDispatcher.getDebugInfo()});
    }

    /**
     * Gets the entities info for use on the Debug screen
     */
    public String getDebugInfoEntities()
    {
        return "E: " + this.countEntitiesRendered + "/" + this.countEntitiesTotal + ", B: " + this.countEntitiesHidden + ", I: " + (this.countEntitiesTotal - this.countEntitiesHidden - this.countEntitiesRendered);
    }

    public void setupTerrain(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator)
    {
        if (this.mc.gameSettings.renderDistanceChunks != this.renderDistanceChunks)
        {
            this.loadRenderers();
        }

        this.theWorld.theProfiler.startSection("camera");
        double lvt_7_1_ = viewEntity.posX - this.frustumUpdatePosX;
        double lvt_9_1_ = viewEntity.posY - this.frustumUpdatePosY;
        double lvt_11_1_ = viewEntity.posZ - this.frustumUpdatePosZ;

        if (this.frustumUpdatePosChunkX != viewEntity.chunkCoordX || this.frustumUpdatePosChunkY != viewEntity.chunkCoordY || this.frustumUpdatePosChunkZ != viewEntity.chunkCoordZ || lvt_7_1_ * lvt_7_1_ + lvt_9_1_ * lvt_9_1_ + lvt_11_1_ * lvt_11_1_ > 16.0D)
        {
            this.frustumUpdatePosX = viewEntity.posX;
            this.frustumUpdatePosY = viewEntity.posY;
            this.frustumUpdatePosZ = viewEntity.posZ;
            this.frustumUpdatePosChunkX = viewEntity.chunkCoordX;
            this.frustumUpdatePosChunkY = viewEntity.chunkCoordY;
            this.frustumUpdatePosChunkZ = viewEntity.chunkCoordZ;
            this.viewFrustum.updateChunkPositions(viewEntity.posX, viewEntity.posZ);
        }

        this.theWorld.theProfiler.endStartSection("renderlistcamera");
        double lvt_13_1_ = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * partialTicks;
        double lvt_15_1_ = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * partialTicks;
        double lvt_17_1_ = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * partialTicks;
        this.renderContainer.initialize(lvt_13_1_, lvt_15_1_, lvt_17_1_);
        this.theWorld.theProfiler.endStartSection("cull");

        if (this.debugFixedClippingHelper != null)
        {
            Frustum lvt_19_1_ = new Frustum(this.debugFixedClippingHelper);
            lvt_19_1_.setPosition(this.debugTerrainFrustumPosition.x, this.debugTerrainFrustumPosition.y, this.debugTerrainFrustumPosition.z);
            camera = lvt_19_1_;
        }

        this.mc.mcProfiler.endStartSection("culling");
        BlockPos lvt_19_2_ = new BlockPos(lvt_13_1_, lvt_15_1_ + (double)viewEntity.getEyeHeight(), lvt_17_1_);
        RenderChunk lvt_20_1_ = this.viewFrustum.getRenderChunk(lvt_19_2_);
        BlockPos lvt_21_1_ = new BlockPos(MathHelper.floor_double(lvt_13_1_ / 16.0D) * 16, MathHelper.floor_double(lvt_15_1_ / 16.0D) * 16, MathHelper.floor_double(lvt_17_1_ / 16.0D) * 16);
        this.displayListEntitiesDirty = this.displayListEntitiesDirty || !this.chunksToUpdate.isEmpty() || viewEntity.posX != this.lastViewEntityX || viewEntity.posY != this.lastViewEntityY || viewEntity.posZ != this.lastViewEntityZ || (double)viewEntity.rotationPitch != this.lastViewEntityPitch || (double)viewEntity.rotationYaw != this.lastViewEntityYaw;
        this.lastViewEntityX = viewEntity.posX;
        this.lastViewEntityY = viewEntity.posY;
        this.lastViewEntityZ = viewEntity.posZ;
        this.lastViewEntityPitch = (double)viewEntity.rotationPitch;
        this.lastViewEntityYaw = (double)viewEntity.rotationYaw;
        boolean lvt_22_1_ = this.debugFixedClippingHelper != null;

        if (!lvt_22_1_ && this.displayListEntitiesDirty)
        {
            this.displayListEntitiesDirty = false;
            this.renderInfos = Lists.newArrayList();
            Queue<RenderGlobal.ContainerLocalRenderInformation> lvt_23_1_ = Lists.newLinkedList();
            boolean lvt_24_1_ = this.mc.renderChunksMany;

            if (lvt_20_1_ != null)
            {
                boolean lvt_25_2_ = false;
                RenderGlobal.ContainerLocalRenderInformation lvt_26_2_ = new RenderGlobal.ContainerLocalRenderInformation(lvt_20_1_, (EnumFacing)null, 0);
                Set<EnumFacing> lvt_27_2_ = this.getVisibleFacings(lvt_19_2_);

                if (lvt_27_2_.size() == 1)
                {
                    Vector3f lvt_28_2_ = this.getViewVector(viewEntity, partialTicks);
                    EnumFacing lvt_29_1_ = EnumFacing.getFacingFromVector(lvt_28_2_.x, lvt_28_2_.y, lvt_28_2_.z).getOpposite();
                    lvt_27_2_.remove(lvt_29_1_);
                }

                if (lvt_27_2_.isEmpty())
                {
                    lvt_25_2_ = true;
                }

                if (lvt_25_2_ && !playerSpectator)
                {
                    this.renderInfos.add(lvt_26_2_);
                }
                else
                {
                    if (playerSpectator && this.theWorld.getBlockState(lvt_19_2_).getBlock().isOpaqueCube())
                    {
                        lvt_24_1_ = false;
                    }

                    lvt_20_1_.setFrameIndex(frameCount);
                    lvt_23_1_.add(lvt_26_2_);
                }
            }
            else
            {
                int lvt_25_1_ = lvt_19_2_.getY() > 0 ? 248 : 8;

                for (int lvt_26_1_ = -this.renderDistanceChunks; lvt_26_1_ <= this.renderDistanceChunks; ++lvt_26_1_)
                {
                    for (int lvt_27_1_ = -this.renderDistanceChunks; lvt_27_1_ <= this.renderDistanceChunks; ++lvt_27_1_)
                    {
                        RenderChunk lvt_28_1_ = this.viewFrustum.getRenderChunk(new BlockPos((lvt_26_1_ << 4) + 8, lvt_25_1_, (lvt_27_1_ << 4) + 8));

                        if (lvt_28_1_ != null && ((ICamera)camera).isBoundingBoxInFrustum(lvt_28_1_.boundingBox))
                        {
                            lvt_28_1_.setFrameIndex(frameCount);
                            lvt_23_1_.add(new RenderGlobal.ContainerLocalRenderInformation(lvt_28_1_, (EnumFacing)null, 0));
                        }
                    }
                }
            }

            while (!((Queue)lvt_23_1_).isEmpty())
            {
                RenderGlobal.ContainerLocalRenderInformation lvt_25_3_ = (RenderGlobal.ContainerLocalRenderInformation)lvt_23_1_.poll();
                RenderChunk lvt_26_3_ = lvt_25_3_.renderChunk;
                EnumFacing lvt_27_3_ = lvt_25_3_.facing;
                BlockPos lvt_28_3_ = lvt_26_3_.getPosition();
                this.renderInfos.add(lvt_25_3_);

                for (EnumFacing lvt_32_1_ : EnumFacing.values())
                {
                    RenderChunk lvt_33_1_ = this.getRenderChunkOffset(lvt_21_1_, lvt_26_3_, lvt_32_1_);

                    if ((!lvt_24_1_ || !lvt_25_3_.setFacing.contains(lvt_32_1_.getOpposite())) && (!lvt_24_1_ || lvt_27_3_ == null || lvt_26_3_.getCompiledChunk().isVisible(lvt_27_3_.getOpposite(), lvt_32_1_)) && lvt_33_1_ != null && lvt_33_1_.setFrameIndex(frameCount) && ((ICamera)camera).isBoundingBoxInFrustum(lvt_33_1_.boundingBox))
                    {
                        RenderGlobal.ContainerLocalRenderInformation lvt_34_1_ = new RenderGlobal.ContainerLocalRenderInformation(lvt_33_1_, lvt_32_1_, lvt_25_3_.counter + 1);
                        lvt_34_1_.setFacing.addAll(lvt_25_3_.setFacing);
                        lvt_34_1_.setFacing.add(lvt_32_1_);
                        lvt_23_1_.add(lvt_34_1_);
                    }
                }
            }
        }

        if (this.debugFixTerrainFrustum)
        {
            this.fixTerrainFrustum(lvt_13_1_, lvt_15_1_, lvt_17_1_);
            this.debugFixTerrainFrustum = false;
        }

        this.renderDispatcher.clearChunkUpdates();
        Set<RenderChunk> lvt_23_2_ = this.chunksToUpdate;
        this.chunksToUpdate = Sets.newLinkedHashSet();

        for (RenderGlobal.ContainerLocalRenderInformation lvt_25_4_ : this.renderInfos)
        {
            RenderChunk lvt_26_4_ = lvt_25_4_.renderChunk;

            if (lvt_26_4_.isNeedsUpdate() || lvt_23_2_.contains(lvt_26_4_))
            {
                this.displayListEntitiesDirty = true;

                if (this.isPositionInRenderChunk(lvt_21_1_, lvt_25_4_.renderChunk))
                {
                    this.mc.mcProfiler.startSection("build near");
                    this.renderDispatcher.updateChunkNow(lvt_26_4_);
                    lvt_26_4_.setNeedsUpdate(false);
                    this.mc.mcProfiler.endSection();
                }
                else
                {
                    this.chunksToUpdate.add(lvt_26_4_);
                }
            }
        }

        this.chunksToUpdate.addAll(lvt_23_2_);
        this.mc.mcProfiler.endSection();
    }

    private boolean isPositionInRenderChunk(BlockPos pos, RenderChunk renderChunkIn)
    {
        BlockPos lvt_3_1_ = renderChunkIn.getPosition();
        return MathHelper.abs_int(pos.getX() - lvt_3_1_.getX()) > 16 ? false : (MathHelper.abs_int(pos.getY() - lvt_3_1_.getY()) > 16 ? false : MathHelper.abs_int(pos.getZ() - lvt_3_1_.getZ()) <= 16);
    }

    private Set<EnumFacing> getVisibleFacings(BlockPos pos)
    {
        VisGraph lvt_2_1_ = new VisGraph();
        BlockPos lvt_3_1_ = new BlockPos(pos.getX() >> 4 << 4, pos.getY() >> 4 << 4, pos.getZ() >> 4 << 4);
        Chunk lvt_4_1_ = this.theWorld.getChunkFromBlockCoords(lvt_3_1_);

        for (BlockPos.MutableBlockPos lvt_6_1_ : BlockPos.getAllInBoxMutable(lvt_3_1_, lvt_3_1_.add(15, 15, 15)))
        {
            if (lvt_4_1_.getBlock(lvt_6_1_).isOpaqueCube())
            {
                lvt_2_1_.func_178606_a(lvt_6_1_);
            }
        }
        return lvt_2_1_.func_178609_b(pos);
    }

    /**
     * Returns RenderChunk offset from given RenderChunk in given direction, or null if it can't be seen by player at
     * given BlockPos.
     */
    private RenderChunk getRenderChunkOffset(BlockPos playerPos, RenderChunk renderChunkBase, EnumFacing facing)
    {
        BlockPos lvt_4_1_ = renderChunkBase.getBlockPosOffset16(facing);
        return MathHelper.abs_int(playerPos.getX() - lvt_4_1_.getX()) > this.renderDistanceChunks * 16 ? null : (lvt_4_1_.getY() >= 0 && lvt_4_1_.getY() < 256 ? (MathHelper.abs_int(playerPos.getZ() - lvt_4_1_.getZ()) > this.renderDistanceChunks * 16 ? null : this.viewFrustum.getRenderChunk(lvt_4_1_)) : null);
    }

    private void fixTerrainFrustum(double x, double y, double z)
    {
        this.debugFixedClippingHelper = new ClippingHelperImpl();
        ((ClippingHelperImpl)this.debugFixedClippingHelper).init();
        Matrix4f lvt_7_1_ = new Matrix4f(this.debugFixedClippingHelper.modelviewMatrix);
        lvt_7_1_.transpose();
        Matrix4f lvt_8_1_ = new Matrix4f(this.debugFixedClippingHelper.projectionMatrix);
        lvt_8_1_.transpose();
        Matrix4f lvt_9_1_ = new Matrix4f();
        Matrix4f.mul(lvt_8_1_, lvt_7_1_, lvt_9_1_);
        lvt_9_1_.invert();
        this.debugTerrainFrustumPosition.x = x;
        this.debugTerrainFrustumPosition.y = y;
        this.debugTerrainFrustumPosition.z = z;
        this.debugTerrainMatrix[0] = new Vector4f(-1.0F, -1.0F, -1.0F, 1.0F);
        this.debugTerrainMatrix[1] = new Vector4f(1.0F, -1.0F, -1.0F, 1.0F);
        this.debugTerrainMatrix[2] = new Vector4f(1.0F, 1.0F, -1.0F, 1.0F);
        this.debugTerrainMatrix[3] = new Vector4f(-1.0F, 1.0F, -1.0F, 1.0F);
        this.debugTerrainMatrix[4] = new Vector4f(-1.0F, -1.0F, 1.0F, 1.0F);
        this.debugTerrainMatrix[5] = new Vector4f(1.0F, -1.0F, 1.0F, 1.0F);
        this.debugTerrainMatrix[6] = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.debugTerrainMatrix[7] = new Vector4f(-1.0F, 1.0F, 1.0F, 1.0F);

        for (int lvt_10_1_ = 0; lvt_10_1_ < 8; ++lvt_10_1_)
        {
            Matrix4f.transform(lvt_9_1_, this.debugTerrainMatrix[lvt_10_1_], this.debugTerrainMatrix[lvt_10_1_]);
            this.debugTerrainMatrix[lvt_10_1_].x /= this.debugTerrainMatrix[lvt_10_1_].w;
            this.debugTerrainMatrix[lvt_10_1_].y /= this.debugTerrainMatrix[lvt_10_1_].w;
            this.debugTerrainMatrix[lvt_10_1_].z /= this.debugTerrainMatrix[lvt_10_1_].w;
            this.debugTerrainMatrix[lvt_10_1_].w = 1.0F;
        }
    }

    protected Vector3f getViewVector(Entity entityIn, double partialTicks)
    {
        float lvt_4_1_ = (float)((double)entityIn.prevRotationPitch + (double)(entityIn.rotationPitch - entityIn.prevRotationPitch) * partialTicks);
        float lvt_5_1_ = (float)((double)entityIn.prevRotationYaw + (double)(entityIn.rotationYaw - entityIn.prevRotationYaw) * partialTicks);

        if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 2)
        {
            lvt_4_1_ += 180.0F;
        }

        float lvt_6_1_ = MathHelper.cos(-lvt_5_1_ * 0.017453292F - (float)Math.PI);
        float lvt_7_1_ = MathHelper.sin(-lvt_5_1_ * 0.017453292F - (float)Math.PI);
        float lvt_8_1_ = -MathHelper.cos(-lvt_4_1_ * 0.017453292F);
        float lvt_9_1_ = MathHelper.sin(-lvt_4_1_ * 0.017453292F);
        return new Vector3f(lvt_7_1_ * lvt_8_1_, lvt_9_1_, lvt_6_1_ * lvt_8_1_);
    }

    public int renderBlockLayer(EnumWorldBlockLayer blockLayerIn, double partialTicks, int pass, Entity entityIn)
    {
        RenderHelper.disableStandardItemLighting();

        if (blockLayerIn == EnumWorldBlockLayer.TRANSLUCENT)
        {
            this.mc.mcProfiler.startSection("translucent_sort");
            double lvt_6_1_ = entityIn.posX - this.prevRenderSortX;
            double lvt_8_1_ = entityIn.posY - this.prevRenderSortY;
            double lvt_10_1_ = entityIn.posZ - this.prevRenderSortZ;

            if (lvt_6_1_ * lvt_6_1_ + lvt_8_1_ * lvt_8_1_ + lvt_10_1_ * lvt_10_1_ > 1.0D)
            {
                this.prevRenderSortX = entityIn.posX;
                this.prevRenderSortY = entityIn.posY;
                this.prevRenderSortZ = entityIn.posZ;
                int lvt_12_1_ = 0;

                for (RenderGlobal.ContainerLocalRenderInformation lvt_14_1_ : this.renderInfos)
                {
                    if (lvt_14_1_.renderChunk.compiledChunk.isLayerStarted(blockLayerIn) && lvt_12_1_++ < 15)
                    {
                        this.renderDispatcher.updateTransparencyLater(lvt_14_1_.renderChunk);
                    }
                }
            }

            this.mc.mcProfiler.endSection();
        }

        this.mc.mcProfiler.startSection("filterempty");
        int lvt_6_2_ = 0;
        boolean lvt_7_1_ = blockLayerIn == EnumWorldBlockLayer.TRANSLUCENT;
        int lvt_8_2_ = lvt_7_1_ ? this.renderInfos.size() - 1 : 0;
        int lvt_9_1_ = lvt_7_1_ ? -1 : this.renderInfos.size();
        int lvt_10_2_ = lvt_7_1_ ? -1 : 1;

        for (int lvt_11_1_ = lvt_8_2_; lvt_11_1_ != lvt_9_1_; lvt_11_1_ += lvt_10_2_)
        {
            RenderChunk lvt_12_2_ = ((RenderGlobal.ContainerLocalRenderInformation)this.renderInfos.get(lvt_11_1_)).renderChunk;

            if (!lvt_12_2_.getCompiledChunk().isLayerEmpty(blockLayerIn))
            {
                ++lvt_6_2_;
                this.renderContainer.addRenderChunk(lvt_12_2_, blockLayerIn);
            }
        }

        this.mc.mcProfiler.endStartSection("render_" + blockLayerIn);
        this.renderBlockLayer(blockLayerIn);
        this.mc.mcProfiler.endSection();
        return lvt_6_2_;
    }

    @SuppressWarnings("incomplete-switch")
    private void renderBlockLayer(EnumWorldBlockLayer blockLayerIn)
    {
        this.mc.entityRenderer.enableLightmap();

        if (OpenGlHelper.useVbo())
        {
            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
            GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
        }

        this.renderContainer.renderChunkLayer(blockLayerIn);

        if (OpenGlHelper.useVbo())
        {
            for (VertexFormatElement lvt_4_1_ : DefaultVertexFormats.BLOCK.getElements())
            {
                VertexFormatElement.EnumUsage lvt_5_1_ = lvt_4_1_.getUsage();
                int lvt_6_1_ = lvt_4_1_.getIndex();

                switch (lvt_5_1_)
                {
                    case POSITION:
                        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
                        break;

                    case UV:
                        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + lvt_6_1_);
                        GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                        break;

                    case COLOR:
                        GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
                        GlStateManager.resetColor();
                }
            }
        }

        this.mc.entityRenderer.disableLightmap();
    }

    private void cleanupDamagedBlocks(Iterator<DestroyBlockProgress> iteratorIn)
    {
        while (iteratorIn.hasNext())
        {
            DestroyBlockProgress lvt_2_1_ = (DestroyBlockProgress)iteratorIn.next();
            int lvt_3_1_ = lvt_2_1_.getCreationCloudUpdateTick();

            if (this.cloudTickCounter - lvt_3_1_ > 400)
            {
                iteratorIn.remove();
            }
        }
    }

    public void updateClouds()
    {
        ++this.cloudTickCounter;

        if (this.cloudTickCounter % 20 == 0)
        {
            this.cleanupDamagedBlocks(this.damagedBlocks.values().iterator());
        }
    }

    private void renderSkyEnd()
    {
        GlStateManager.disableFog();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.depthMask(false);
        this.renderEngine.bindTexture(locationEndSkyPng);
        Tessellator lvt_1_1_ = Tessellator.getInstance();
        WorldRenderer lvt_2_1_ = lvt_1_1_.getWorldRenderer();

        for (int lvt_3_1_ = 0; lvt_3_1_ < 6; ++lvt_3_1_)
        {
            GlStateManager.pushMatrix();

            if (lvt_3_1_ == 1)
            {
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (lvt_3_1_ == 2)
            {
                GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (lvt_3_1_ == 3)
            {
                GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            }

            if (lvt_3_1_ == 4)
            {
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            }

            if (lvt_3_1_ == 5)
            {
                GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
            }

            lvt_2_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            lvt_2_1_.pos(-100.0D, -100.0D, -100.0D).tex(0.0D, 0.0D).color(40, 40, 40, 255).endVertex();
            lvt_2_1_.pos(-100.0D, -100.0D, 100.0D).tex(0.0D, 16.0D).color(40, 40, 40, 255).endVertex();
            lvt_2_1_.pos(100.0D, -100.0D, 100.0D).tex(16.0D, 16.0D).color(40, 40, 40, 255).endVertex();
            lvt_2_1_.pos(100.0D, -100.0D, -100.0D).tex(16.0D, 0.0D).color(40, 40, 40, 255).endVertex();
            lvt_1_1_.draw();
            GlStateManager.popMatrix();
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
    }

    public void renderSky(float partialTicks, int pass)
    {
        if (this.mc.theWorld.provider.getDimensionId() == 1)
        {
            this.renderSkyEnd();
        }
        else if (this.mc.theWorld.provider.isSurfaceWorld())
        {
            GlStateManager.disableTexture2D();
            Vec3 lvt_3_1_ = this.theWorld.getSkyColor(this.mc.getRenderViewEntity(), partialTicks);
            float lvt_4_1_ = (float)lvt_3_1_.xCoord;
            float lvt_5_1_ = (float)lvt_3_1_.yCoord;
            float lvt_6_1_ = (float)lvt_3_1_.zCoord;

            if (pass != 2)
            {
                float lvt_7_1_ = (lvt_4_1_ * 30.0F + lvt_5_1_ * 59.0F + lvt_6_1_ * 11.0F) / 100.0F;
                float lvt_8_1_ = (lvt_4_1_ * 30.0F + lvt_5_1_ * 70.0F) / 100.0F;
                float lvt_9_1_ = (lvt_4_1_ * 30.0F + lvt_6_1_ * 70.0F) / 100.0F;
                lvt_4_1_ = lvt_7_1_;
                lvt_5_1_ = lvt_8_1_;
                lvt_6_1_ = lvt_9_1_;
            }

            GlStateManager.color(lvt_4_1_, lvt_5_1_, lvt_6_1_);
            Tessellator lvt_7_2_ = Tessellator.getInstance();
            WorldRenderer lvt_8_2_ = lvt_7_2_.getWorldRenderer();
            GlStateManager.depthMask(false);
            GlStateManager.enableFog();
            GlStateManager.color(lvt_4_1_, lvt_5_1_, lvt_6_1_);

            if (this.vboEnabled)
            {
                this.skyVBO.bindBuffer();
                GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0L);
                this.skyVBO.drawArrays(7);
                this.skyVBO.unbindBuffer();
                GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
            }
            else
            {
                GlStateManager.callList(this.glSkyList);
            }

            GlStateManager.disableFog();
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            RenderHelper.disableStandardItemLighting();
            float[] lvt_9_2_ = this.theWorld.provider.calcSunriseSunsetColors(this.theWorld.getCelestialAngle(partialTicks), partialTicks);

            if (lvt_9_2_ != null)
            {
                GlStateManager.disableTexture2D();
                GlStateManager.shadeModel(7425);
                GlStateManager.pushMatrix();
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(MathHelper.sin(this.theWorld.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                float lvt_10_1_ = lvt_9_2_[0];
                float lvt_11_1_ = lvt_9_2_[1];
                float lvt_12_1_ = lvt_9_2_[2];

                if (pass != 2)
                {
                    float lvt_13_1_ = (lvt_10_1_ * 30.0F + lvt_11_1_ * 59.0F + lvt_12_1_ * 11.0F) / 100.0F;
                    float lvt_14_1_ = (lvt_10_1_ * 30.0F + lvt_11_1_ * 70.0F) / 100.0F;
                    float lvt_15_1_ = (lvt_10_1_ * 30.0F + lvt_12_1_ * 70.0F) / 100.0F;
                    lvt_10_1_ = lvt_13_1_;
                    lvt_11_1_ = lvt_14_1_;
                    lvt_12_1_ = lvt_15_1_;
                }

                lvt_8_2_.begin(6, DefaultVertexFormats.POSITION_COLOR);
                lvt_8_2_.pos(0.0D, 100.0D, 0.0D).color(lvt_10_1_, lvt_11_1_, lvt_12_1_, lvt_9_2_[3]).endVertex();
                int lvt_13_2_ = 16;

                for (int lvt_14_2_ = 0; lvt_14_2_ <= 16; ++lvt_14_2_)
                {
                    float lvt_15_2_ = (float)lvt_14_2_ * (float)Math.PI * 2.0F / 16.0F;
                    float lvt_16_1_ = MathHelper.sin(lvt_15_2_);
                    float lvt_17_1_ = MathHelper.cos(lvt_15_2_);
                    lvt_8_2_.pos((double)(lvt_16_1_ * 120.0F), (double)(lvt_17_1_ * 120.0F), (double)(-lvt_17_1_ * 40.0F * lvt_9_2_[3])).color(lvt_9_2_[0], lvt_9_2_[1], lvt_9_2_[2], 0.0F).endVertex();
                }

                lvt_7_2_.draw();
                GlStateManager.popMatrix();
                GlStateManager.shadeModel(7424);
            }

            GlStateManager.enableTexture2D();
            GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
            GlStateManager.pushMatrix();
            float lvt_10_2_ = 1.0F - this.theWorld.getRainStrength(partialTicks);
            GlStateManager.color(1.0F, 1.0F, 1.0F, lvt_10_2_);
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(this.theWorld.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
            float lvt_11_2_ = 30.0F;
            this.renderEngine.bindTexture(locationSunPng);
            lvt_8_2_.begin(7, DefaultVertexFormats.POSITION_TEX);
            lvt_8_2_.pos((double)(-lvt_11_2_), 100.0D, (double)(-lvt_11_2_)).tex(0.0D, 0.0D).endVertex();
            lvt_8_2_.pos((double)lvt_11_2_, 100.0D, (double)(-lvt_11_2_)).tex(1.0D, 0.0D).endVertex();
            lvt_8_2_.pos((double)lvt_11_2_, 100.0D, (double)lvt_11_2_).tex(1.0D, 1.0D).endVertex();
            lvt_8_2_.pos((double)(-lvt_11_2_), 100.0D, (double)lvt_11_2_).tex(0.0D, 1.0D).endVertex();
            lvt_7_2_.draw();
            lvt_11_2_ = 20.0F;
            this.renderEngine.bindTexture(locationMoonPhasesPng);
            int lvt_12_2_ = this.theWorld.getMoonPhase();
            int lvt_13_3_ = lvt_12_2_ % 4;
            int lvt_14_3_ = lvt_12_2_ / 4 % 2;
            float lvt_15_3_ = (float)(lvt_13_3_ + 0) / 4.0F;
            float lvt_16_2_ = (float)(lvt_14_3_ + 0) / 2.0F;
            float lvt_17_2_ = (float)(lvt_13_3_ + 1) / 4.0F;
            float lvt_18_1_ = (float)(lvt_14_3_ + 1) / 2.0F;
            lvt_8_2_.begin(7, DefaultVertexFormats.POSITION_TEX);
            lvt_8_2_.pos((double)(-lvt_11_2_), -100.0D, (double)lvt_11_2_).tex((double)lvt_17_2_, (double)lvt_18_1_).endVertex();
            lvt_8_2_.pos((double)lvt_11_2_, -100.0D, (double)lvt_11_2_).tex((double)lvt_15_3_, (double)lvt_18_1_).endVertex();
            lvt_8_2_.pos((double)lvt_11_2_, -100.0D, (double)(-lvt_11_2_)).tex((double)lvt_15_3_, (double)lvt_16_2_).endVertex();
            lvt_8_2_.pos((double)(-lvt_11_2_), -100.0D, (double)(-lvt_11_2_)).tex((double)lvt_17_2_, (double)lvt_16_2_).endVertex();
            lvt_7_2_.draw();
            GlStateManager.disableTexture2D();
            float lvt_19_1_ = this.theWorld.getStarBrightness(partialTicks) * lvt_10_2_;

            if (lvt_19_1_ > 0.0F)
            {
                GlStateManager.color(lvt_19_1_, lvt_19_1_, lvt_19_1_, lvt_19_1_);

                if (this.vboEnabled)
                {
                    this.starVBO.bindBuffer();
                    GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                    GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0L);
                    this.starVBO.drawArrays(7);
                    this.starVBO.unbindBuffer();
                    GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
                }
                else
                {
                    GlStateManager.callList(this.starGLCallList);
                }
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableFog();
            GlStateManager.popMatrix();
            GlStateManager.disableTexture2D();
            GlStateManager.color(0.0F, 0.0F, 0.0F);
            double lvt_10_3_ = this.mc.thePlayer.getPositionEyes(partialTicks).yCoord - this.theWorld.getHorizon();

            if (lvt_10_3_ < 0.0D)
            {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0F, 12.0F, 0.0F);

                if (this.vboEnabled)
                {
                    this.sky2VBO.bindBuffer();
                    GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                    GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0L);
                    this.sky2VBO.drawArrays(7);
                    this.sky2VBO.unbindBuffer();
                    GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
                }
                else
                {
                    GlStateManager.callList(this.glSkyList2);
                }

                GlStateManager.popMatrix();
                float lvt_12_3_ = 1.0F;
                float lvt_13_4_ = -((float)(lvt_10_3_ + 65.0D));
                float lvt_14_4_ = -1.0F;
                lvt_8_2_.begin(7, DefaultVertexFormats.POSITION_COLOR);
                lvt_8_2_.pos(-1.0D, (double)lvt_13_4_, 1.0D).color(0, 0, 0, 255).endVertex();
                lvt_8_2_.pos(1.0D, (double)lvt_13_4_, 1.0D).color(0, 0, 0, 255).endVertex();
                lvt_8_2_.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                lvt_8_2_.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                lvt_8_2_.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                lvt_8_2_.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                lvt_8_2_.pos(1.0D, (double)lvt_13_4_, -1.0D).color(0, 0, 0, 255).endVertex();
                lvt_8_2_.pos(-1.0D, (double)lvt_13_4_, -1.0D).color(0, 0, 0, 255).endVertex();
                lvt_8_2_.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                lvt_8_2_.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                lvt_8_2_.pos(1.0D, (double)lvt_13_4_, 1.0D).color(0, 0, 0, 255).endVertex();
                lvt_8_2_.pos(1.0D, (double)lvt_13_4_, -1.0D).color(0, 0, 0, 255).endVertex();
                lvt_8_2_.pos(-1.0D, (double)lvt_13_4_, -1.0D).color(0, 0, 0, 255).endVertex();
                lvt_8_2_.pos(-1.0D, (double)lvt_13_4_, 1.0D).color(0, 0, 0, 255).endVertex();
                lvt_8_2_.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                lvt_8_2_.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                lvt_8_2_.pos(-1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                lvt_8_2_.pos(-1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                lvt_8_2_.pos(1.0D, -1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                lvt_8_2_.pos(1.0D, -1.0D, -1.0D).color(0, 0, 0, 255).endVertex();
                lvt_7_2_.draw();
            }

            if (this.theWorld.provider.isSkyColored())
            {
                GlStateManager.color(lvt_4_1_ * 0.2F + 0.04F, lvt_5_1_ * 0.2F + 0.04F, lvt_6_1_ * 0.6F + 0.1F);
            }
            else
            {
                GlStateManager.color(lvt_4_1_, lvt_5_1_, lvt_6_1_);
            }

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, -((float)(lvt_10_3_ - 16.0D)), 0.0F);
            GlStateManager.callList(this.glSkyList2);
            GlStateManager.popMatrix();
            GlStateManager.enableTexture2D();
            GlStateManager.depthMask(true);
        }
    }

    public void renderClouds(float partialTicks, int pass)
    {
        if (this.mc.theWorld.provider.isSurfaceWorld())
        {
            if (this.mc.gameSettings.shouldRenderClouds() == 2)
            {
                this.renderCloudsFancy(partialTicks, pass);
            }
            else
            {
                GlStateManager.disableCull();
                float lvt_3_1_ = (float)(this.mc.getRenderViewEntity().lastTickPosY + (this.mc.getRenderViewEntity().posY - this.mc.getRenderViewEntity().lastTickPosY) * (double)partialTicks);
                int lvt_4_1_ = 32;
                int lvt_5_1_ = 8;
                Tessellator lvt_6_1_ = Tessellator.getInstance();
                WorldRenderer lvt_7_1_ = lvt_6_1_.getWorldRenderer();
                this.renderEngine.bindTexture(locationCloudsPng);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                Vec3 lvt_8_1_ = this.theWorld.getCloudColour(partialTicks);
                float lvt_9_1_ = (float)lvt_8_1_.xCoord;
                float lvt_10_1_ = (float)lvt_8_1_.yCoord;
                float lvt_11_1_ = (float)lvt_8_1_.zCoord;

                if (pass != 2)
                {
                    float lvt_12_1_ = (lvt_9_1_ * 30.0F + lvt_10_1_ * 59.0F + lvt_11_1_ * 11.0F) / 100.0F;
                    float lvt_13_1_ = (lvt_9_1_ * 30.0F + lvt_10_1_ * 70.0F) / 100.0F;
                    float lvt_14_1_ = (lvt_9_1_ * 30.0F + lvt_11_1_ * 70.0F) / 100.0F;
                    lvt_9_1_ = lvt_12_1_;
                    lvt_10_1_ = lvt_13_1_;
                    lvt_11_1_ = lvt_14_1_;
                }

                float lvt_12_2_ = 4.8828125E-4F;
                double lvt_13_2_ = (double)((float)this.cloudTickCounter + partialTicks);
                double lvt_15_1_ = this.mc.getRenderViewEntity().prevPosX + (this.mc.getRenderViewEntity().posX - this.mc.getRenderViewEntity().prevPosX) * (double)partialTicks + lvt_13_2_ * 0.029999999329447746D;
                double lvt_17_1_ = this.mc.getRenderViewEntity().prevPosZ + (this.mc.getRenderViewEntity().posZ - this.mc.getRenderViewEntity().prevPosZ) * (double)partialTicks;
                int lvt_19_1_ = MathHelper.floor_double(lvt_15_1_ / 2048.0D);
                int lvt_20_1_ = MathHelper.floor_double(lvt_17_1_ / 2048.0D);
                lvt_15_1_ = lvt_15_1_ - (double)(lvt_19_1_ * 2048);
                lvt_17_1_ = lvt_17_1_ - (double)(lvt_20_1_ * 2048);
                float lvt_21_1_ = this.theWorld.provider.getCloudHeight() - lvt_3_1_ + 0.33F;
                float lvt_22_1_ = (float)(lvt_15_1_ * 4.8828125E-4D);
                float lvt_23_1_ = (float)(lvt_17_1_ * 4.8828125E-4D);
                lvt_7_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

                for (int lvt_24_1_ = -256; lvt_24_1_ < 256; lvt_24_1_ += 32)
                {
                    for (int lvt_25_1_ = -256; lvt_25_1_ < 256; lvt_25_1_ += 32)
                    {
                        lvt_7_1_.pos((double)(lvt_24_1_ + 0), (double)lvt_21_1_, (double)(lvt_25_1_ + 32)).tex((double)((float)(lvt_24_1_ + 0) * 4.8828125E-4F + lvt_22_1_), (double)((float)(lvt_25_1_ + 32) * 4.8828125E-4F + lvt_23_1_)).color(lvt_9_1_, lvt_10_1_, lvt_11_1_, 0.8F).endVertex();
                        lvt_7_1_.pos((double)(lvt_24_1_ + 32), (double)lvt_21_1_, (double)(lvt_25_1_ + 32)).tex((double)((float)(lvt_24_1_ + 32) * 4.8828125E-4F + lvt_22_1_), (double)((float)(lvt_25_1_ + 32) * 4.8828125E-4F + lvt_23_1_)).color(lvt_9_1_, lvt_10_1_, lvt_11_1_, 0.8F).endVertex();
                        lvt_7_1_.pos((double)(lvt_24_1_ + 32), (double)lvt_21_1_, (double)(lvt_25_1_ + 0)).tex((double)((float)(lvt_24_1_ + 32) * 4.8828125E-4F + lvt_22_1_), (double)((float)(lvt_25_1_ + 0) * 4.8828125E-4F + lvt_23_1_)).color(lvt_9_1_, lvt_10_1_, lvt_11_1_, 0.8F).endVertex();
                        lvt_7_1_.pos((double)(lvt_24_1_ + 0), (double)lvt_21_1_, (double)(lvt_25_1_ + 0)).tex((double)((float)(lvt_24_1_ + 0) * 4.8828125E-4F + lvt_22_1_), (double)((float)(lvt_25_1_ + 0) * 4.8828125E-4F + lvt_23_1_)).color(lvt_9_1_, lvt_10_1_, lvt_11_1_, 0.8F).endVertex();
                    }
                }

                lvt_6_1_.draw();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.disableBlend();
                GlStateManager.enableCull();
            }
        }
    }

    /**
     * Checks if the given position is to be rendered with cloud fog
     */
    public boolean hasCloudFog(double x, double y, double z, float partialTicks)
    {
        return false;
    }

    private void renderCloudsFancy(float partialTicks, int pass)
    {
        GlStateManager.disableCull();
        float lvt_3_1_ = (float)(this.mc.getRenderViewEntity().lastTickPosY + (this.mc.getRenderViewEntity().posY - this.mc.getRenderViewEntity().lastTickPosY) * (double)partialTicks);
        Tessellator lvt_4_1_ = Tessellator.getInstance();
        WorldRenderer lvt_5_1_ = lvt_4_1_.getWorldRenderer();
        float lvt_6_1_ = 12.0F;
        float lvt_7_1_ = 4.0F;
        double lvt_8_1_ = (double)((float)this.cloudTickCounter + partialTicks);
        double lvt_10_1_ = (this.mc.getRenderViewEntity().prevPosX + (this.mc.getRenderViewEntity().posX - this.mc.getRenderViewEntity().prevPosX) * (double)partialTicks + lvt_8_1_ * 0.029999999329447746D) / 12.0D;
        double lvt_12_1_ = (this.mc.getRenderViewEntity().prevPosZ + (this.mc.getRenderViewEntity().posZ - this.mc.getRenderViewEntity().prevPosZ) * (double)partialTicks) / 12.0D + 0.33000001311302185D;
        float lvt_14_1_ = this.theWorld.provider.getCloudHeight() - lvt_3_1_ + 0.33F;
        int lvt_15_1_ = MathHelper.floor_double(lvt_10_1_ / 2048.0D);
        int lvt_16_1_ = MathHelper.floor_double(lvt_12_1_ / 2048.0D);
        lvt_10_1_ = lvt_10_1_ - (double)(lvt_15_1_ * 2048);
        lvt_12_1_ = lvt_12_1_ - (double)(lvt_16_1_ * 2048);
        this.renderEngine.bindTexture(locationCloudsPng);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        Vec3 lvt_17_1_ = this.theWorld.getCloudColour(partialTicks);
        float lvt_18_1_ = (float)lvt_17_1_.xCoord;
        float lvt_19_1_ = (float)lvt_17_1_.yCoord;
        float lvt_20_1_ = (float)lvt_17_1_.zCoord;

        if (pass != 2)
        {
            float lvt_21_1_ = (lvt_18_1_ * 30.0F + lvt_19_1_ * 59.0F + lvt_20_1_ * 11.0F) / 100.0F;
            float lvt_22_1_ = (lvt_18_1_ * 30.0F + lvt_19_1_ * 70.0F) / 100.0F;
            float lvt_23_1_ = (lvt_18_1_ * 30.0F + lvt_20_1_ * 70.0F) / 100.0F;
            lvt_18_1_ = lvt_21_1_;
            lvt_19_1_ = lvt_22_1_;
            lvt_20_1_ = lvt_23_1_;
        }

        float lvt_21_2_ = lvt_18_1_ * 0.9F;
        float lvt_22_2_ = lvt_19_1_ * 0.9F;
        float lvt_23_2_ = lvt_20_1_ * 0.9F;
        float lvt_24_1_ = lvt_18_1_ * 0.7F;
        float lvt_25_1_ = lvt_19_1_ * 0.7F;
        float lvt_26_1_ = lvt_20_1_ * 0.7F;
        float lvt_27_1_ = lvt_18_1_ * 0.8F;
        float lvt_28_1_ = lvt_19_1_ * 0.8F;
        float lvt_29_1_ = lvt_20_1_ * 0.8F;
        float lvt_30_1_ = 0.00390625F;
        float lvt_31_1_ = (float)MathHelper.floor_double(lvt_10_1_) * 0.00390625F;
        float lvt_32_1_ = (float)MathHelper.floor_double(lvt_12_1_) * 0.00390625F;
        float lvt_33_1_ = (float)(lvt_10_1_ - (double)MathHelper.floor_double(lvt_10_1_));
        float lvt_34_1_ = (float)(lvt_12_1_ - (double)MathHelper.floor_double(lvt_12_1_));
        int lvt_35_1_ = 8;
        int lvt_36_1_ = 4;
        float lvt_37_1_ = 9.765625E-4F;
        GlStateManager.scale(12.0F, 1.0F, 12.0F);

        for (int lvt_38_1_ = 0; lvt_38_1_ < 2; ++lvt_38_1_)
        {
            if (lvt_38_1_ == 0)
            {
                GlStateManager.colorMask(false, false, false, false);
            }
            else
            {
                switch (pass)
                {
                    case 0:
                        GlStateManager.colorMask(false, true, true, true);
                        break;

                    case 1:
                        GlStateManager.colorMask(true, false, false, true);
                        break;

                    case 2:
                        GlStateManager.colorMask(true, true, true, true);
                }
            }

            for (int lvt_39_1_ = -3; lvt_39_1_ <= 4; ++lvt_39_1_)
            {
                for (int lvt_40_1_ = -3; lvt_40_1_ <= 4; ++lvt_40_1_)
                {
                    lvt_5_1_.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
                    float lvt_41_1_ = (float)(lvt_39_1_ * 8);
                    float lvt_42_1_ = (float)(lvt_40_1_ * 8);
                    float lvt_43_1_ = lvt_41_1_ - lvt_33_1_;
                    float lvt_44_1_ = lvt_42_1_ - lvt_34_1_;

                    if (lvt_14_1_ > -5.0F)
                    {
                        lvt_5_1_.pos((double)(lvt_43_1_ + 0.0F), (double)(lvt_14_1_ + 0.0F), (double)(lvt_44_1_ + 8.0F)).tex((double)((lvt_41_1_ + 0.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 8.0F) * 0.00390625F + lvt_32_1_)).color(lvt_24_1_, lvt_25_1_, lvt_26_1_, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                        lvt_5_1_.pos((double)(lvt_43_1_ + 8.0F), (double)(lvt_14_1_ + 0.0F), (double)(lvt_44_1_ + 8.0F)).tex((double)((lvt_41_1_ + 8.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 8.0F) * 0.00390625F + lvt_32_1_)).color(lvt_24_1_, lvt_25_1_, lvt_26_1_, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                        lvt_5_1_.pos((double)(lvt_43_1_ + 8.0F), (double)(lvt_14_1_ + 0.0F), (double)(lvt_44_1_ + 0.0F)).tex((double)((lvt_41_1_ + 8.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 0.0F) * 0.00390625F + lvt_32_1_)).color(lvt_24_1_, lvt_25_1_, lvt_26_1_, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                        lvt_5_1_.pos((double)(lvt_43_1_ + 0.0F), (double)(lvt_14_1_ + 0.0F), (double)(lvt_44_1_ + 0.0F)).tex((double)((lvt_41_1_ + 0.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 0.0F) * 0.00390625F + lvt_32_1_)).color(lvt_24_1_, lvt_25_1_, lvt_26_1_, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                    }

                    if (lvt_14_1_ <= 5.0F)
                    {
                        lvt_5_1_.pos((double)(lvt_43_1_ + 0.0F), (double)(lvt_14_1_ + 4.0F - 9.765625E-4F), (double)(lvt_44_1_ + 8.0F)).tex((double)((lvt_41_1_ + 0.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 8.0F) * 0.00390625F + lvt_32_1_)).color(lvt_18_1_, lvt_19_1_, lvt_20_1_, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                        lvt_5_1_.pos((double)(lvt_43_1_ + 8.0F), (double)(lvt_14_1_ + 4.0F - 9.765625E-4F), (double)(lvt_44_1_ + 8.0F)).tex((double)((lvt_41_1_ + 8.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 8.0F) * 0.00390625F + lvt_32_1_)).color(lvt_18_1_, lvt_19_1_, lvt_20_1_, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                        lvt_5_1_.pos((double)(lvt_43_1_ + 8.0F), (double)(lvt_14_1_ + 4.0F - 9.765625E-4F), (double)(lvt_44_1_ + 0.0F)).tex((double)((lvt_41_1_ + 8.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 0.0F) * 0.00390625F + lvt_32_1_)).color(lvt_18_1_, lvt_19_1_, lvt_20_1_, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                        lvt_5_1_.pos((double)(lvt_43_1_ + 0.0F), (double)(lvt_14_1_ + 4.0F - 9.765625E-4F), (double)(lvt_44_1_ + 0.0F)).tex((double)((lvt_41_1_ + 0.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 0.0F) * 0.00390625F + lvt_32_1_)).color(lvt_18_1_, lvt_19_1_, lvt_20_1_, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                    }

                    if (lvt_39_1_ > -1)
                    {
                        for (int lvt_45_1_ = 0; lvt_45_1_ < 8; ++lvt_45_1_)
                        {
                            lvt_5_1_.pos((double)(lvt_43_1_ + (float)lvt_45_1_ + 0.0F), (double)(lvt_14_1_ + 0.0F), (double)(lvt_44_1_ + 8.0F)).tex((double)((lvt_41_1_ + (float)lvt_45_1_ + 0.5F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 8.0F) * 0.00390625F + lvt_32_1_)).color(lvt_21_2_, lvt_22_2_, lvt_23_2_, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                            lvt_5_1_.pos((double)(lvt_43_1_ + (float)lvt_45_1_ + 0.0F), (double)(lvt_14_1_ + 4.0F), (double)(lvt_44_1_ + 8.0F)).tex((double)((lvt_41_1_ + (float)lvt_45_1_ + 0.5F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 8.0F) * 0.00390625F + lvt_32_1_)).color(lvt_21_2_, lvt_22_2_, lvt_23_2_, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                            lvt_5_1_.pos((double)(lvt_43_1_ + (float)lvt_45_1_ + 0.0F), (double)(lvt_14_1_ + 4.0F), (double)(lvt_44_1_ + 0.0F)).tex((double)((lvt_41_1_ + (float)lvt_45_1_ + 0.5F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 0.0F) * 0.00390625F + lvt_32_1_)).color(lvt_21_2_, lvt_22_2_, lvt_23_2_, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                            lvt_5_1_.pos((double)(lvt_43_1_ + (float)lvt_45_1_ + 0.0F), (double)(lvt_14_1_ + 0.0F), (double)(lvt_44_1_ + 0.0F)).tex((double)((lvt_41_1_ + (float)lvt_45_1_ + 0.5F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 0.0F) * 0.00390625F + lvt_32_1_)).color(lvt_21_2_, lvt_22_2_, lvt_23_2_, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                        }
                    }

                    if (lvt_39_1_ <= 1)
                    {
                        for (int lvt_45_2_ = 0; lvt_45_2_ < 8; ++lvt_45_2_)
                        {
                            lvt_5_1_.pos((double)(lvt_43_1_ + (float)lvt_45_2_ + 1.0F - 9.765625E-4F), (double)(lvt_14_1_ + 0.0F), (double)(lvt_44_1_ + 8.0F)).tex((double)((lvt_41_1_ + (float)lvt_45_2_ + 0.5F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 8.0F) * 0.00390625F + lvt_32_1_)).color(lvt_21_2_, lvt_22_2_, lvt_23_2_, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                            lvt_5_1_.pos((double)(lvt_43_1_ + (float)lvt_45_2_ + 1.0F - 9.765625E-4F), (double)(lvt_14_1_ + 4.0F), (double)(lvt_44_1_ + 8.0F)).tex((double)((lvt_41_1_ + (float)lvt_45_2_ + 0.5F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 8.0F) * 0.00390625F + lvt_32_1_)).color(lvt_21_2_, lvt_22_2_, lvt_23_2_, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                            lvt_5_1_.pos((double)(lvt_43_1_ + (float)lvt_45_2_ + 1.0F - 9.765625E-4F), (double)(lvt_14_1_ + 4.0F), (double)(lvt_44_1_ + 0.0F)).tex((double)((lvt_41_1_ + (float)lvt_45_2_ + 0.5F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 0.0F) * 0.00390625F + lvt_32_1_)).color(lvt_21_2_, lvt_22_2_, lvt_23_2_, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                            lvt_5_1_.pos((double)(lvt_43_1_ + (float)lvt_45_2_ + 1.0F - 9.765625E-4F), (double)(lvt_14_1_ + 0.0F), (double)(lvt_44_1_ + 0.0F)).tex((double)((lvt_41_1_ + (float)lvt_45_2_ + 0.5F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 0.0F) * 0.00390625F + lvt_32_1_)).color(lvt_21_2_, lvt_22_2_, lvt_23_2_, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                        }
                    }

                    if (lvt_40_1_ > -1)
                    {
                        for (int lvt_45_3_ = 0; lvt_45_3_ < 8; ++lvt_45_3_)
                        {
                            lvt_5_1_.pos((double)(lvt_43_1_ + 0.0F), (double)(lvt_14_1_ + 4.0F), (double)(lvt_44_1_ + (float)lvt_45_3_ + 0.0F)).tex((double)((lvt_41_1_ + 0.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + (float)lvt_45_3_ + 0.5F) * 0.00390625F + lvt_32_1_)).color(lvt_27_1_, lvt_28_1_, lvt_29_1_, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                            lvt_5_1_.pos((double)(lvt_43_1_ + 8.0F), (double)(lvt_14_1_ + 4.0F), (double)(lvt_44_1_ + (float)lvt_45_3_ + 0.0F)).tex((double)((lvt_41_1_ + 8.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + (float)lvt_45_3_ + 0.5F) * 0.00390625F + lvt_32_1_)).color(lvt_27_1_, lvt_28_1_, lvt_29_1_, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                            lvt_5_1_.pos((double)(lvt_43_1_ + 8.0F), (double)(lvt_14_1_ + 0.0F), (double)(lvt_44_1_ + (float)lvt_45_3_ + 0.0F)).tex((double)((lvt_41_1_ + 8.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + (float)lvt_45_3_ + 0.5F) * 0.00390625F + lvt_32_1_)).color(lvt_27_1_, lvt_28_1_, lvt_29_1_, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                            lvt_5_1_.pos((double)(lvt_43_1_ + 0.0F), (double)(lvt_14_1_ + 0.0F), (double)(lvt_44_1_ + (float)lvt_45_3_ + 0.0F)).tex((double)((lvt_41_1_ + 0.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + (float)lvt_45_3_ + 0.5F) * 0.00390625F + lvt_32_1_)).color(lvt_27_1_, lvt_28_1_, lvt_29_1_, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                        }
                    }

                    if (lvt_40_1_ <= 1)
                    {
                        for (int lvt_45_4_ = 0; lvt_45_4_ < 8; ++lvt_45_4_)
                        {
                            lvt_5_1_.pos((double)(lvt_43_1_ + 0.0F), (double)(lvt_14_1_ + 4.0F), (double)(lvt_44_1_ + (float)lvt_45_4_ + 1.0F - 9.765625E-4F)).tex((double)((lvt_41_1_ + 0.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + (float)lvt_45_4_ + 0.5F) * 0.00390625F + lvt_32_1_)).color(lvt_27_1_, lvt_28_1_, lvt_29_1_, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                            lvt_5_1_.pos((double)(lvt_43_1_ + 8.0F), (double)(lvt_14_1_ + 4.0F), (double)(lvt_44_1_ + (float)lvt_45_4_ + 1.0F - 9.765625E-4F)).tex((double)((lvt_41_1_ + 8.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + (float)lvt_45_4_ + 0.5F) * 0.00390625F + lvt_32_1_)).color(lvt_27_1_, lvt_28_1_, lvt_29_1_, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                            lvt_5_1_.pos((double)(lvt_43_1_ + 8.0F), (double)(lvt_14_1_ + 0.0F), (double)(lvt_44_1_ + (float)lvt_45_4_ + 1.0F - 9.765625E-4F)).tex((double)((lvt_41_1_ + 8.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + (float)lvt_45_4_ + 0.5F) * 0.00390625F + lvt_32_1_)).color(lvt_27_1_, lvt_28_1_, lvt_29_1_, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                            lvt_5_1_.pos((double)(lvt_43_1_ + 0.0F), (double)(lvt_14_1_ + 0.0F), (double)(lvt_44_1_ + (float)lvt_45_4_ + 1.0F - 9.765625E-4F)).tex((double)((lvt_41_1_ + 0.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + (float)lvt_45_4_ + 0.5F) * 0.00390625F + lvt_32_1_)).color(lvt_27_1_, lvt_28_1_, lvt_29_1_, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                        }
                    }

                    lvt_4_1_.draw();
                }
            }
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
    }

    public void updateChunks(long finishTimeNano)
    {
        this.displayListEntitiesDirty |= this.renderDispatcher.runChunkUploads(finishTimeNano);

        if (!this.chunksToUpdate.isEmpty())
        {
            Iterator<RenderChunk> lvt_3_1_ = this.chunksToUpdate.iterator();

            while (lvt_3_1_.hasNext())
            {
                RenderChunk lvt_4_1_ = (RenderChunk)lvt_3_1_.next();

                if (!this.renderDispatcher.updateChunkLater(lvt_4_1_))
                {
                    break;
                }

                lvt_4_1_.setNeedsUpdate(false);
                lvt_3_1_.remove();
                long lvt_5_1_ = finishTimeNano - System.nanoTime();

                if (lvt_5_1_ < 0L)
                {
                    break;
                }
            }
        }
    }

    public void renderWorldBorder(Entity entityIn, float partialTicks)
    {
        Tessellator lvt_3_1_ = Tessellator.getInstance();
        WorldRenderer lvt_4_1_ = lvt_3_1_.getWorldRenderer();
        WorldBorder lvt_5_1_ = this.theWorld.getWorldBorder();
        double lvt_6_1_ = (double)(this.mc.gameSettings.renderDistanceChunks * 16);

        if (entityIn.posX >= lvt_5_1_.maxX() - lvt_6_1_ || entityIn.posX <= lvt_5_1_.minX() + lvt_6_1_ || entityIn.posZ >= lvt_5_1_.maxZ() - lvt_6_1_ || entityIn.posZ <= lvt_5_1_.minZ() + lvt_6_1_)
        {
            double lvt_8_1_ = 1.0D - lvt_5_1_.getClosestDistance(entityIn) / lvt_6_1_;
            lvt_8_1_ = Math.pow(lvt_8_1_, 4.0D);
            double lvt_10_1_ = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double)partialTicks;
            double lvt_12_1_ = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double)partialTicks;
            double lvt_14_1_ = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double)partialTicks;
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
            this.renderEngine.bindTexture(locationForcefieldPng);
            GlStateManager.depthMask(false);
            GlStateManager.pushMatrix();
            int lvt_16_1_ = lvt_5_1_.getStatus().getID();
            float lvt_17_1_ = (float)(lvt_16_1_ >> 16 & 255) / 255.0F;
            float lvt_18_1_ = (float)(lvt_16_1_ >> 8 & 255) / 255.0F;
            float lvt_19_1_ = (float)(lvt_16_1_ & 255) / 255.0F;
            GlStateManager.color(lvt_17_1_, lvt_18_1_, lvt_19_1_, (float)lvt_8_1_);
            GlStateManager.doPolygonOffset(-3.0F, -3.0F);
            GlStateManager.enablePolygonOffset();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.enableAlpha();
            GlStateManager.disableCull();
            float lvt_20_1_ = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F;
            float lvt_21_1_ = 0.0F;
            float lvt_22_1_ = 0.0F;
            float lvt_23_1_ = 128.0F;
            lvt_4_1_.begin(7, DefaultVertexFormats.POSITION_TEX);
            lvt_4_1_.setTranslation(-lvt_10_1_, -lvt_12_1_, -lvt_14_1_);
            double lvt_24_1_ = Math.max((double)MathHelper.floor_double(lvt_14_1_ - lvt_6_1_), lvt_5_1_.minZ());
            double lvt_26_1_ = Math.min((double)MathHelper.ceiling_double_int(lvt_14_1_ + lvt_6_1_), lvt_5_1_.maxZ());

            if (lvt_10_1_ > lvt_5_1_.maxX() - lvt_6_1_)
            {
                float lvt_28_1_ = 0.0F;

                for (double lvt_29_1_ = lvt_24_1_; lvt_29_1_ < lvt_26_1_; lvt_28_1_ += 0.5F)
                {
                    double lvt_31_1_ = Math.min(1.0D, lvt_26_1_ - lvt_29_1_);
                    float lvt_33_1_ = (float)lvt_31_1_ * 0.5F;
                    lvt_4_1_.pos(lvt_5_1_.maxX(), 256.0D, lvt_29_1_).tex((double)(lvt_20_1_ + lvt_28_1_), (double)(lvt_20_1_ + 0.0F)).endVertex();
                    lvt_4_1_.pos(lvt_5_1_.maxX(), 256.0D, lvt_29_1_ + lvt_31_1_).tex((double)(lvt_20_1_ + lvt_33_1_ + lvt_28_1_), (double)(lvt_20_1_ + 0.0F)).endVertex();
                    lvt_4_1_.pos(lvt_5_1_.maxX(), 0.0D, lvt_29_1_ + lvt_31_1_).tex((double)(lvt_20_1_ + lvt_33_1_ + lvt_28_1_), (double)(lvt_20_1_ + 128.0F)).endVertex();
                    lvt_4_1_.pos(lvt_5_1_.maxX(), 0.0D, lvt_29_1_).tex((double)(lvt_20_1_ + lvt_28_1_), (double)(lvt_20_1_ + 128.0F)).endVertex();
                    ++lvt_29_1_;
                }
            }

            if (lvt_10_1_ < lvt_5_1_.minX() + lvt_6_1_)
            {
                float lvt_28_2_ = 0.0F;

                for (double lvt_29_2_ = lvt_24_1_; lvt_29_2_ < lvt_26_1_; lvt_28_2_ += 0.5F)
                {
                    double lvt_31_2_ = Math.min(1.0D, lvt_26_1_ - lvt_29_2_);
                    float lvt_33_2_ = (float)lvt_31_2_ * 0.5F;
                    lvt_4_1_.pos(lvt_5_1_.minX(), 256.0D, lvt_29_2_).tex((double)(lvt_20_1_ + lvt_28_2_), (double)(lvt_20_1_ + 0.0F)).endVertex();
                    lvt_4_1_.pos(lvt_5_1_.minX(), 256.0D, lvt_29_2_ + lvt_31_2_).tex((double)(lvt_20_1_ + lvt_33_2_ + lvt_28_2_), (double)(lvt_20_1_ + 0.0F)).endVertex();
                    lvt_4_1_.pos(lvt_5_1_.minX(), 0.0D, lvt_29_2_ + lvt_31_2_).tex((double)(lvt_20_1_ + lvt_33_2_ + lvt_28_2_), (double)(lvt_20_1_ + 128.0F)).endVertex();
                    lvt_4_1_.pos(lvt_5_1_.minX(), 0.0D, lvt_29_2_).tex((double)(lvt_20_1_ + lvt_28_2_), (double)(lvt_20_1_ + 128.0F)).endVertex();
                    ++lvt_29_2_;
                }
            }

            lvt_24_1_ = Math.max((double)MathHelper.floor_double(lvt_10_1_ - lvt_6_1_), lvt_5_1_.minX());
            lvt_26_1_ = Math.min((double)MathHelper.ceiling_double_int(lvt_10_1_ + lvt_6_1_), lvt_5_1_.maxX());

            if (lvt_14_1_ > lvt_5_1_.maxZ() - lvt_6_1_)
            {
                float lvt_28_3_ = 0.0F;

                for (double lvt_29_3_ = lvt_24_1_; lvt_29_3_ < lvt_26_1_; lvt_28_3_ += 0.5F)
                {
                    double lvt_31_3_ = Math.min(1.0D, lvt_26_1_ - lvt_29_3_);
                    float lvt_33_3_ = (float)lvt_31_3_ * 0.5F;
                    lvt_4_1_.pos(lvt_29_3_, 256.0D, lvt_5_1_.maxZ()).tex((double)(lvt_20_1_ + lvt_28_3_), (double)(lvt_20_1_ + 0.0F)).endVertex();
                    lvt_4_1_.pos(lvt_29_3_ + lvt_31_3_, 256.0D, lvt_5_1_.maxZ()).tex((double)(lvt_20_1_ + lvt_33_3_ + lvt_28_3_), (double)(lvt_20_1_ + 0.0F)).endVertex();
                    lvt_4_1_.pos(lvt_29_3_ + lvt_31_3_, 0.0D, lvt_5_1_.maxZ()).tex((double)(lvt_20_1_ + lvt_33_3_ + lvt_28_3_), (double)(lvt_20_1_ + 128.0F)).endVertex();
                    lvt_4_1_.pos(lvt_29_3_, 0.0D, lvt_5_1_.maxZ()).tex((double)(lvt_20_1_ + lvt_28_3_), (double)(lvt_20_1_ + 128.0F)).endVertex();
                    ++lvt_29_3_;
                }
            }

            if (lvt_14_1_ < lvt_5_1_.minZ() + lvt_6_1_)
            {
                float lvt_28_4_ = 0.0F;

                for (double lvt_29_4_ = lvt_24_1_; lvt_29_4_ < lvt_26_1_; lvt_28_4_ += 0.5F)
                {
                    double lvt_31_4_ = Math.min(1.0D, lvt_26_1_ - lvt_29_4_);
                    float lvt_33_4_ = (float)lvt_31_4_ * 0.5F;
                    lvt_4_1_.pos(lvt_29_4_, 256.0D, lvt_5_1_.minZ()).tex((double)(lvt_20_1_ + lvt_28_4_), (double)(lvt_20_1_ + 0.0F)).endVertex();
                    lvt_4_1_.pos(lvt_29_4_ + lvt_31_4_, 256.0D, lvt_5_1_.minZ()).tex((double)(lvt_20_1_ + lvt_33_4_ + lvt_28_4_), (double)(lvt_20_1_ + 0.0F)).endVertex();
                    lvt_4_1_.pos(lvt_29_4_ + lvt_31_4_, 0.0D, lvt_5_1_.minZ()).tex((double)(lvt_20_1_ + lvt_33_4_ + lvt_28_4_), (double)(lvt_20_1_ + 128.0F)).endVertex();
                    lvt_4_1_.pos(lvt_29_4_, 0.0D, lvt_5_1_.minZ()).tex((double)(lvt_20_1_ + lvt_28_4_), (double)(lvt_20_1_ + 128.0F)).endVertex();
                    ++lvt_29_4_;
                }
            }

            lvt_3_1_.draw();
            lvt_4_1_.setTranslation(0.0D, 0.0D, 0.0D);
            GlStateManager.enableCull();
            GlStateManager.disableAlpha();
            GlStateManager.doPolygonOffset(0.0F, 0.0F);
            GlStateManager.disablePolygonOffset();
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
        }
    }

    private void preRenderDamagedBlocks()
    {
        GlStateManager.tryBlendFuncSeparate(774, 768, 1, 0);
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
        GlStateManager.doPolygonOffset(-3.0F, -3.0F);
        GlStateManager.enablePolygonOffset();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableAlpha();
        GlStateManager.pushMatrix();
    }

    private void postRenderDamagedBlocks()
    {
        GlStateManager.disableAlpha();
        GlStateManager.doPolygonOffset(0.0F, 0.0F);
        GlStateManager.disablePolygonOffset();
        GlStateManager.enableAlpha();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
    }

    public void drawBlockDamageTexture(Tessellator tessellatorIn, WorldRenderer worldRendererIn, Entity entityIn, float partialTicks)
    {
        double lvt_5_1_ = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double)partialTicks;
        double lvt_7_1_ = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double)partialTicks;
        double lvt_9_1_ = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double)partialTicks;

        if (!this.damagedBlocks.isEmpty())
        {
            this.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            this.preRenderDamagedBlocks();
            worldRendererIn.begin(7, DefaultVertexFormats.BLOCK);
            worldRendererIn.setTranslation(-lvt_5_1_, -lvt_7_1_, -lvt_9_1_);
            worldRendererIn.noColor();
            Iterator<DestroyBlockProgress> lvt_11_1_ = this.damagedBlocks.values().iterator();

            while (lvt_11_1_.hasNext())
            {
                DestroyBlockProgress lvt_12_1_ = (DestroyBlockProgress)lvt_11_1_.next();
                BlockPos lvt_13_1_ = lvt_12_1_.getPosition();
                double lvt_14_1_ = (double)lvt_13_1_.getX() - lvt_5_1_;
                double lvt_16_1_ = (double)lvt_13_1_.getY() - lvt_7_1_;
                double lvt_18_1_ = (double)lvt_13_1_.getZ() - lvt_9_1_;
                Block lvt_20_1_ = this.theWorld.getBlockState(lvt_13_1_).getBlock();

                if (!(lvt_20_1_ instanceof BlockChest) && !(lvt_20_1_ instanceof BlockEnderChest) && !(lvt_20_1_ instanceof BlockSign) && !(lvt_20_1_ instanceof BlockSkull))
                {
                    if (lvt_14_1_ * lvt_14_1_ + lvt_16_1_ * lvt_16_1_ + lvt_18_1_ * lvt_18_1_ > 1024.0D)
                    {
                        lvt_11_1_.remove();
                    }
                    else
                    {
                        IBlockState lvt_21_1_ = this.theWorld.getBlockState(lvt_13_1_);

                        if (lvt_21_1_.getBlock().getMaterial() != Material.air)
                        {
                            int lvt_22_1_ = lvt_12_1_.getPartialBlockDamage();
                            TextureAtlasSprite lvt_23_1_ = this.destroyBlockIcons[lvt_22_1_];
                            BlockRendererDispatcher lvt_24_1_ = this.mc.getBlockRendererDispatcher();
                            lvt_24_1_.renderBlockDamage(lvt_21_1_, lvt_13_1_, lvt_23_1_, this.theWorld);
                        }
                    }
                }
            }

            tessellatorIn.draw();
            worldRendererIn.setTranslation(0.0D, 0.0D, 0.0D);
            this.postRenderDamagedBlocks();
        }
    }

    /**
     * Draws the selection box for the player. Args: entityPlayer, rayTraceHit, i, itemStack, partialTickTime
     *  
     * @param execute If equals to 0 the method is executed
     */
    public void drawSelectionBox(EntityPlayer player, MovingObjectPosition movingObjectPositionIn, int execute, float partialTicks)
    {
        if (execute == 0 && movingObjectPositionIn.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
        {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.color(0.0F, 0.0F, 0.0F, 0.4F);
            GL11.glLineWidth(2.0F);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            float lvt_5_1_ = 0.002F;
            BlockPos lvt_6_1_ = movingObjectPositionIn.getBlockPos();
            Block lvt_7_1_ = this.theWorld.getBlockState(lvt_6_1_).getBlock();

            if (lvt_7_1_.getMaterial() != Material.air && this.theWorld.getWorldBorder().contains(lvt_6_1_))
            {
                lvt_7_1_.setBlockBoundsBasedOnState(this.theWorld, lvt_6_1_);
                double lvt_8_1_ = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)partialTicks;
                double lvt_10_1_ = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)partialTicks;
                double lvt_12_1_ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)partialTicks;
                drawSelectionBoundingBox(lvt_7_1_.getSelectedBoundingBox(this.theWorld, lvt_6_1_).expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D).offset(-lvt_8_1_, -lvt_10_1_, -lvt_12_1_));
            }

            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }
    }

    public static void drawSelectionBoundingBox(AxisAlignedBB boundingBox)
    {
        Tessellator lvt_1_1_ = Tessellator.getInstance();
        WorldRenderer lvt_2_1_ = lvt_1_1_.getWorldRenderer();
        lvt_2_1_.begin(3, DefaultVertexFormats.POSITION);
        lvt_2_1_.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        lvt_2_1_.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        lvt_2_1_.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        lvt_2_1_.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        lvt_2_1_.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        lvt_1_1_.draw();
        lvt_2_1_.begin(3, DefaultVertexFormats.POSITION);
        lvt_2_1_.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        lvt_2_1_.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        lvt_2_1_.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        lvt_2_1_.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        lvt_2_1_.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        lvt_1_1_.draw();
        lvt_2_1_.begin(1, DefaultVertexFormats.POSITION);
        lvt_2_1_.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        lvt_2_1_.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        lvt_2_1_.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        lvt_2_1_.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        lvt_2_1_.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        lvt_2_1_.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        lvt_2_1_.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        lvt_2_1_.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        lvt_1_1_.draw();
    }

    public static void drawOutlinedBoundingBox(AxisAlignedBB boundingBox, int red, int green, int blue, int alpha)
    {
        Tessellator lvt_5_1_ = Tessellator.getInstance();
        WorldRenderer lvt_6_1_ = lvt_5_1_.getWorldRenderer();
        lvt_6_1_.begin(3, DefaultVertexFormats.POSITION_COLOR);
        lvt_6_1_.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
        lvt_6_1_.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
        lvt_6_1_.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
        lvt_6_1_.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
        lvt_6_1_.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
        lvt_5_1_.draw();
        lvt_6_1_.begin(3, DefaultVertexFormats.POSITION_COLOR);
        lvt_6_1_.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
        lvt_6_1_.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
        lvt_6_1_.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
        lvt_6_1_.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
        lvt_6_1_.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
        lvt_5_1_.draw();
        lvt_6_1_.begin(1, DefaultVertexFormats.POSITION_COLOR);
        lvt_6_1_.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
        lvt_6_1_.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
        lvt_6_1_.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
        lvt_6_1_.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
        lvt_6_1_.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
        lvt_6_1_.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
        lvt_6_1_.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
        lvt_6_1_.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
        lvt_5_1_.draw();
    }

    /**
     * Marks the blocks in the given range for update
     */
    private void markBlocksForUpdate(int x1, int y1, int z1, int x2, int y2, int z2)
    {
        this.viewFrustum.markBlocksForUpdate(x1, y1, z1, x2, y2, z2);
    }

    public void markBlockForUpdate(BlockPos pos)
    {
        int lvt_2_1_ = pos.getX();
        int lvt_3_1_ = pos.getY();
        int lvt_4_1_ = pos.getZ();
        this.markBlocksForUpdate(lvt_2_1_ - 1, lvt_3_1_ - 1, lvt_4_1_ - 1, lvt_2_1_ + 1, lvt_3_1_ + 1, lvt_4_1_ + 1);
    }

    public void notifyLightSet(BlockPos pos)
    {
        int lvt_2_1_ = pos.getX();
        int lvt_3_1_ = pos.getY();
        int lvt_4_1_ = pos.getZ();
        this.markBlocksForUpdate(lvt_2_1_ - 1, lvt_3_1_ - 1, lvt_4_1_ - 1, lvt_2_1_ + 1, lvt_3_1_ + 1, lvt_4_1_ + 1);
    }

    /**
     * On the client, re-renders all blocks in this range, inclusive. On the server, does nothing. Args: min x, min y,
     * min z, max x, max y, max z
     */
    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2)
    {
        this.markBlocksForUpdate(x1 - 1, y1 - 1, z1 - 1, x2 + 1, y2 + 1, z2 + 1);
    }

    public void playRecord(String recordName, BlockPos blockPosIn)
    {
        ISound lvt_3_1_ = (ISound)this.mapSoundPositions.get(blockPosIn);

        if (lvt_3_1_ != null)
        {
            this.mc.getSoundHandler().stopSound(lvt_3_1_);
            this.mapSoundPositions.remove(blockPosIn);
        }

        if (recordName != null)
        {
            ItemRecord lvt_4_1_ = ItemRecord.getRecord(recordName);

            if (lvt_4_1_ != null)
            {
                this.mc.ingameGUI.setRecordPlayingMessage(lvt_4_1_.getRecordNameLocal());
            }

            PositionedSoundRecord var5 = PositionedSoundRecord.create(new ResourceLocation(recordName), (float)blockPosIn.getX(), (float)blockPosIn.getY(), (float)blockPosIn.getZ());
            this.mapSoundPositions.put(blockPosIn, var5);
            this.mc.getSoundHandler().playSound(var5);
        }
    }

    /**
     * Plays the specified sound. Arg: soundName, x, y, z, volume, pitch
     */
    public void playSound(String soundName, double x, double y, double z, float volume, float pitch)
    {
    }

    /**
     * Plays sound to all near players except the player reference given
     */
    public void playSoundToNearExcept(EntityPlayer except, String soundName, double x, double y, double z, float volume, float pitch)
    {
    }

    public void spawnParticle(int particleID, boolean ignoreRange, final double xCoord, final double yCoord, final double zCoord, double xOffset, double yOffset, double zOffset, int... parameters)
    {
        try
        {
            this.spawnEntityFX(particleID, ignoreRange, xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, parameters);
        }
        catch (Throwable var19)
        {
            CrashReport lvt_17_1_ = CrashReport.makeCrashReport(var19, "Exception while adding particle");
            CrashReportCategory lvt_18_1_ = lvt_17_1_.makeCategory("Particle being added");
            lvt_18_1_.addCrashSection("ID", Integer.valueOf(particleID));

            if (parameters != null)
            {
                lvt_18_1_.addCrashSection("Parameters", parameters);
            }

            lvt_18_1_.addCrashSectionCallable("Position", new Callable<String>()
            {
                public String call() throws Exception
                {
                    return CrashReportCategory.getCoordinateInfo(xCoord, yCoord, zCoord);
                }
                public Object call() throws Exception
                {
                    return this.call();
                }
            });
            throw new ReportedException(lvt_17_1_);
        }
    }

    private void spawnParticle(EnumParticleTypes particleIn, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... parameters)
    {
        this.spawnParticle(particleIn.getParticleID(), particleIn.getShouldIgnoreRange(), xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, parameters);
    }

    private EntityFX spawnEntityFX(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... parameters)
    {
        if (this.mc != null && this.mc.getRenderViewEntity() != null && this.mc.effectRenderer != null)
        {
            int lvt_16_1_ = this.mc.gameSettings.particleSetting;

            if (lvt_16_1_ == 1 && this.theWorld.rand.nextInt(3) == 0)
            {
                lvt_16_1_ = 2;
            }

            double lvt_17_1_ = this.mc.getRenderViewEntity().posX - xCoord;
            double lvt_19_1_ = this.mc.getRenderViewEntity().posY - yCoord;
            double lvt_21_1_ = this.mc.getRenderViewEntity().posZ - zCoord;

            if (ignoreRange)
            {
                return this.mc.effectRenderer.spawnEffectParticle(particleID, xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, parameters);
            }
            else
            {
                double lvt_23_1_ = 16.0D;
                return lvt_17_1_ * lvt_17_1_ + lvt_19_1_ * lvt_19_1_ + lvt_21_1_ * lvt_21_1_ > 256.0D ? null : (lvt_16_1_ > 1 ? null : this.mc.effectRenderer.spawnEffectParticle(particleID, xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, parameters));
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * Called on all IWorldAccesses when an entity is created or loaded. On client worlds, starts downloading any
     * necessary textures. On server worlds, adds the entity to the entity tracker.
     */
    public void onEntityAdded(Entity entityIn)
    {
    }

    /**
     * Called on all IWorldAccesses when an entity is unloaded or destroyed. On client worlds, releases any downloaded
     * textures. On server worlds, removes the entity from the entity tracker.
     */
    public void onEntityRemoved(Entity entityIn)
    {
    }

    /**
     * Deletes all display lists
     */
    public void deleteAllDisplayLists()
    {
    }

    public void broadcastSound(int soundID, BlockPos pos, int data)
    {
        switch (soundID)
        {
            case 1013:
            case 1018:
                if (this.mc.getRenderViewEntity() != null)
                {
                    double lvt_4_1_ = (double)pos.getX() - this.mc.getRenderViewEntity().posX;
                    double lvt_6_1_ = (double)pos.getY() - this.mc.getRenderViewEntity().posY;
                    double lvt_8_1_ = (double)pos.getZ() - this.mc.getRenderViewEntity().posZ;
                    double lvt_10_1_ = Math.sqrt(lvt_4_1_ * lvt_4_1_ + lvt_6_1_ * lvt_6_1_ + lvt_8_1_ * lvt_8_1_);
                    double lvt_12_1_ = this.mc.getRenderViewEntity().posX;
                    double lvt_14_1_ = this.mc.getRenderViewEntity().posY;
                    double lvt_16_1_ = this.mc.getRenderViewEntity().posZ;

                    if (lvt_10_1_ > 0.0D)
                    {
                        lvt_12_1_ += lvt_4_1_ / lvt_10_1_ * 2.0D;
                        lvt_14_1_ += lvt_6_1_ / lvt_10_1_ * 2.0D;
                        lvt_16_1_ += lvt_8_1_ / lvt_10_1_ * 2.0D;
                    }

                    if (soundID == 1013)
                    {
                        this.theWorld.playSound(lvt_12_1_, lvt_14_1_, lvt_16_1_, "mob.wither.spawn", 1.0F, 1.0F, false);
                    }
                    else
                    {
                        this.theWorld.playSound(lvt_12_1_, lvt_14_1_, lvt_16_1_, "mob.enderdragon.end", 5.0F, 1.0F, false);
                    }
                }

            default:
        }
    }

    public void playAuxSFX(EntityPlayer player, int sfxType, BlockPos blockPosIn, int data)
    {
        Random lvt_5_1_ = this.theWorld.rand;

        switch (sfxType)
        {
            case 1000:
                this.theWorld.playSoundAtPos(blockPosIn, "random.click", 1.0F, 1.0F, false);
                break;

            case 1001:
                this.theWorld.playSoundAtPos(blockPosIn, "random.click", 1.0F, 1.2F, false);
                break;

            case 1002:
                this.theWorld.playSoundAtPos(blockPosIn, "random.bow", 1.0F, 1.2F, false);
                break;

            case 1003:
                this.theWorld.playSoundAtPos(blockPosIn, "random.door_open", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1004:
                this.theWorld.playSoundAtPos(blockPosIn, "random.fizz", 0.5F, 2.6F + (lvt_5_1_.nextFloat() - lvt_5_1_.nextFloat()) * 0.8F, false);
                break;

            case 1005:
                if (Item.getItemById(data) instanceof ItemRecord)
                {
                    this.theWorld.playRecord(blockPosIn, "records." + ((ItemRecord)Item.getItemById(data)).recordName);
                }
                else
                {
                    this.theWorld.playRecord(blockPosIn, (String)null);
                }

                break;

            case 1006:
                this.theWorld.playSoundAtPos(blockPosIn, "random.door_close", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1007:
                this.theWorld.playSoundAtPos(blockPosIn, "mob.ghast.charge", 10.0F, (lvt_5_1_.nextFloat() - lvt_5_1_.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1008:
                this.theWorld.playSoundAtPos(blockPosIn, "mob.ghast.fireball", 10.0F, (lvt_5_1_.nextFloat() - lvt_5_1_.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1009:
                this.theWorld.playSoundAtPos(blockPosIn, "mob.ghast.fireball", 2.0F, (lvt_5_1_.nextFloat() - lvt_5_1_.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1010:
                this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.wood", 2.0F, (lvt_5_1_.nextFloat() - lvt_5_1_.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1011:
                this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.metal", 2.0F, (lvt_5_1_.nextFloat() - lvt_5_1_.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1012:
                this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.woodbreak", 2.0F, (lvt_5_1_.nextFloat() - lvt_5_1_.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1014:
                this.theWorld.playSoundAtPos(blockPosIn, "mob.wither.shoot", 2.0F, (lvt_5_1_.nextFloat() - lvt_5_1_.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1015:
                this.theWorld.playSoundAtPos(blockPosIn, "mob.bat.takeoff", 0.05F, (lvt_5_1_.nextFloat() - lvt_5_1_.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1016:
                this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.infect", 2.0F, (lvt_5_1_.nextFloat() - lvt_5_1_.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1017:
                this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.unfect", 2.0F, (lvt_5_1_.nextFloat() - lvt_5_1_.nextFloat()) * 0.2F + 1.0F, false);
                break;

            case 1020:
                this.theWorld.playSoundAtPos(blockPosIn, "random.anvil_break", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1021:
                this.theWorld.playSoundAtPos(blockPosIn, "random.anvil_use", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 1022:
                this.theWorld.playSoundAtPos(blockPosIn, "random.anvil_land", 0.3F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 2000:
                int lvt_7_1_ = data % 3 - 1;
                int lvt_8_1_ = data / 3 % 3 - 1;
                double lvt_9_1_ = (double)blockPosIn.getX() + (double)lvt_7_1_ * 0.6D + 0.5D;
                double lvt_11_1_ = (double)blockPosIn.getY() + 0.5D;
                double lvt_13_1_ = (double)blockPosIn.getZ() + (double)lvt_8_1_ * 0.6D + 0.5D;

                for (int lvt_15_1_ = 0; lvt_15_1_ < 10; ++lvt_15_1_)
                {
                    double lvt_16_1_ = lvt_5_1_.nextDouble() * 0.2D + 0.01D;
                    double lvt_18_1_ = lvt_9_1_ + (double)lvt_7_1_ * 0.01D + (lvt_5_1_.nextDouble() - 0.5D) * (double)lvt_8_1_ * 0.5D;
                    double lvt_20_1_ = lvt_11_1_ + (lvt_5_1_.nextDouble() - 0.5D) * 0.5D;
                    double lvt_22_1_ = lvt_13_1_ + (double)lvt_8_1_ * 0.01D + (lvt_5_1_.nextDouble() - 0.5D) * (double)lvt_7_1_ * 0.5D;
                    double lvt_24_1_ = (double)lvt_7_1_ * lvt_16_1_ + lvt_5_1_.nextGaussian() * 0.01D;
                    double lvt_26_1_ = -0.03D + lvt_5_1_.nextGaussian() * 0.01D;
                    double lvt_28_1_ = (double)lvt_8_1_ * lvt_16_1_ + lvt_5_1_.nextGaussian() * 0.01D;
                    this.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, lvt_18_1_, lvt_20_1_, lvt_22_1_, lvt_24_1_, lvt_26_1_, lvt_28_1_, new int[0]);
                }

                return;

            case 2001:
                Block lvt_6_1_ = Block.getBlockById(data & 4095);

                if (lvt_6_1_.getMaterial() != Material.air)
                {
                    this.mc.getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation(lvt_6_1_.stepSound.getBreakSound()), (lvt_6_1_.stepSound.getVolume() + 1.0F) / 2.0F, lvt_6_1_.stepSound.getFrequency() * 0.8F, (float)blockPosIn.getX() + 0.5F, (float)blockPosIn.getY() + 0.5F, (float)blockPosIn.getZ() + 0.5F));
                }

                this.mc.effectRenderer.addBlockDestroyEffects(blockPosIn, lvt_6_1_.getStateFromMeta(data >> 12 & 255));
                break;

            case 2002:
                double lvt_7_3_ = (double)blockPosIn.getX();
                double lvt_9_3_ = (double)blockPosIn.getY();
                double lvt_11_3_ = (double)blockPosIn.getZ();

                for (int lvt_13_4_ = 0; lvt_13_4_ < 8; ++lvt_13_4_)
                {
                    this.spawnParticle(EnumParticleTypes.ITEM_CRACK, lvt_7_3_, lvt_9_3_, lvt_11_3_, lvt_5_1_.nextGaussian() * 0.15D, lvt_5_1_.nextDouble() * 0.2D, lvt_5_1_.nextGaussian() * 0.15D, new int[] {Item.getIdFromItem(Items.potionitem), data});
                }

                int lvt_13_5_ = Items.potionitem.getColorFromDamage(data);
                float lvt_14_1_ = (float)(lvt_13_5_ >> 16 & 255) / 255.0F;
                float lvt_15_2_ = (float)(lvt_13_5_ >> 8 & 255) / 255.0F;
                float lvt_16_2_ = (float)(lvt_13_5_ >> 0 & 255) / 255.0F;
                EnumParticleTypes lvt_17_1_ = EnumParticleTypes.SPELL;

                if (Items.potionitem.isEffectInstant(data))
                {
                    lvt_17_1_ = EnumParticleTypes.SPELL_INSTANT;
                }

                for (int lvt_18_2_ = 0; lvt_18_2_ < 100; ++lvt_18_2_)
                {
                    double lvt_19_1_ = lvt_5_1_.nextDouble() * 4.0D;
                    double lvt_21_1_ = lvt_5_1_.nextDouble() * Math.PI * 2.0D;
                    double lvt_23_1_ = Math.cos(lvt_21_1_) * lvt_19_1_;
                    double lvt_25_1_ = 0.01D + lvt_5_1_.nextDouble() * 0.5D;
                    double lvt_27_1_ = Math.sin(lvt_21_1_) * lvt_19_1_;
                    EntityFX lvt_29_1_ = this.spawnEntityFX(lvt_17_1_.getParticleID(), lvt_17_1_.getShouldIgnoreRange(), lvt_7_3_ + lvt_23_1_ * 0.1D, lvt_9_3_ + 0.3D, lvt_11_3_ + lvt_27_1_ * 0.1D, lvt_23_1_, lvt_25_1_, lvt_27_1_, new int[0]);

                    if (lvt_29_1_ != null)
                    {
                        float lvt_30_1_ = 0.75F + lvt_5_1_.nextFloat() * 0.25F;
                        lvt_29_1_.setRBGColorF(lvt_14_1_ * lvt_30_1_, lvt_15_2_ * lvt_30_1_, lvt_16_2_ * lvt_30_1_);
                        lvt_29_1_.multiplyVelocity((float)lvt_19_1_);
                    }
                }

                this.theWorld.playSoundAtPos(blockPosIn, "game.potion.smash", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;

            case 2003:
                double lvt_7_2_ = (double)blockPosIn.getX() + 0.5D;
                double lvt_9_2_ = (double)blockPosIn.getY();
                double lvt_11_2_ = (double)blockPosIn.getZ() + 0.5D;

                for (int lvt_13_2_ = 0; lvt_13_2_ < 8; ++lvt_13_2_)
                {
                    this.spawnParticle(EnumParticleTypes.ITEM_CRACK, lvt_7_2_, lvt_9_2_, lvt_11_2_, lvt_5_1_.nextGaussian() * 0.15D, lvt_5_1_.nextDouble() * 0.2D, lvt_5_1_.nextGaussian() * 0.15D, new int[] {Item.getIdFromItem(Items.ender_eye)});
                }

                for (double lvt_13_3_ = 0.0D; lvt_13_3_ < (Math.PI * 2D); lvt_13_3_ += 0.15707963267948966D)
                {
                    this.spawnParticle(EnumParticleTypes.PORTAL, lvt_7_2_ + Math.cos(lvt_13_3_) * 5.0D, lvt_9_2_ - 0.4D, lvt_11_2_ + Math.sin(lvt_13_3_) * 5.0D, Math.cos(lvt_13_3_) * -5.0D, 0.0D, Math.sin(lvt_13_3_) * -5.0D, new int[0]);
                    this.spawnParticle(EnumParticleTypes.PORTAL, lvt_7_2_ + Math.cos(lvt_13_3_) * 5.0D, lvt_9_2_ - 0.4D, lvt_11_2_ + Math.sin(lvt_13_3_) * 5.0D, Math.cos(lvt_13_3_) * -7.0D, 0.0D, Math.sin(lvt_13_3_) * -7.0D, new int[0]);
                }

                return;

            case 2004:
                for (int lvt_18_3_ = 0; lvt_18_3_ < 20; ++lvt_18_3_)
                {
                    double lvt_19_2_ = (double)blockPosIn.getX() + 0.5D + ((double)this.theWorld.rand.nextFloat() - 0.5D) * 2.0D;
                    double lvt_21_2_ = (double)blockPosIn.getY() + 0.5D + ((double)this.theWorld.rand.nextFloat() - 0.5D) * 2.0D;
                    double lvt_23_2_ = (double)blockPosIn.getZ() + 0.5D + ((double)this.theWorld.rand.nextFloat() - 0.5D) * 2.0D;
                    this.theWorld.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, lvt_19_2_, lvt_21_2_, lvt_23_2_, 0.0D, 0.0D, 0.0D, new int[0]);
                    this.theWorld.spawnParticle(EnumParticleTypes.FLAME, lvt_19_2_, lvt_21_2_, lvt_23_2_, 0.0D, 0.0D, 0.0D, new int[0]);
                }

                return;

            case 2005:
                ItemDye.spawnBonemealParticles(this.theWorld, blockPosIn, data);
        }
    }

    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress)
    {
        if (progress >= 0 && progress < 10)
        {
            DestroyBlockProgress lvt_4_1_ = (DestroyBlockProgress)this.damagedBlocks.get(Integer.valueOf(breakerId));

            if (lvt_4_1_ == null || lvt_4_1_.getPosition().getX() != pos.getX() || lvt_4_1_.getPosition().getY() != pos.getY() || lvt_4_1_.getPosition().getZ() != pos.getZ())
            {
                lvt_4_1_ = new DestroyBlockProgress(breakerId, pos);
                this.damagedBlocks.put(Integer.valueOf(breakerId), lvt_4_1_);
            }

            lvt_4_1_.setPartialBlockDamage(progress);
            lvt_4_1_.setCloudUpdateTick(this.cloudTickCounter);
        }
        else
        {
            this.damagedBlocks.remove(Integer.valueOf(breakerId));
        }
    }

    public void setDisplayListEntitiesDirty()
    {
        this.displayListEntitiesDirty = true;
    }

    public void updateTileEntities(Collection<TileEntity> tileEntitiesToRemove, Collection<TileEntity> tileEntitiesToAdd)
    {
        synchronized (this.setTileEntities)
        {
            this.setTileEntities.removeAll(tileEntitiesToRemove);
            this.setTileEntities.addAll(tileEntitiesToAdd);
        }
    }

    class ContainerLocalRenderInformation
    {
        final RenderChunk renderChunk;
        final EnumFacing facing;
        final Set<EnumFacing> setFacing;
        final int counter;

        private ContainerLocalRenderInformation(RenderChunk renderChunkIn, EnumFacing facingIn, int counterIn)
        {
            this.setFacing = EnumSet.noneOf(EnumFacing.class);
            this.renderChunk = renderChunkIn;
            this.facing = facingIn;
            this.counter = counterIn;
        }
    }
}
