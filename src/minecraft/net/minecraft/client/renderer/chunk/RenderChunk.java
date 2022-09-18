package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.nio.FloatBuffer;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RegionRenderCache;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RenderChunk
{
    private World world;
    private final RenderGlobal renderGlobal;
    public static int renderChunksUpdated;
    private BlockPos position;
    public CompiledChunk compiledChunk = CompiledChunk.DUMMY;
    private final ReentrantLock lockCompileTask = new ReentrantLock();
    private final ReentrantLock lockCompiledChunk = new ReentrantLock();
    private ChunkCompileTaskGenerator compileTask = null;
    private final Set<TileEntity> setTileEntities = Sets.newHashSet();
    private final int index;
    private final FloatBuffer modelviewMatrix = GLAllocation.createDirectFloatBuffer(16);
    private final VertexBuffer[] vertexBuffers = new VertexBuffer[EnumWorldBlockLayer.values().length];
    public AxisAlignedBB boundingBox;
    private int frameIndex = -1;
    private boolean needsUpdate = true;
    private EnumMap<EnumFacing, BlockPos> mapEnumFacing = Maps.newEnumMap(EnumFacing.class);

    public RenderChunk(World worldIn, RenderGlobal renderGlobalIn, BlockPos blockPosIn, int indexIn)
    {
        this.world = worldIn;
        this.renderGlobal = renderGlobalIn;
        this.index = indexIn;

        if (!blockPosIn.equals(this.getPosition()))
        {
            this.setPosition(blockPosIn);
        }

        if (OpenGlHelper.useVbo())
        {
            for (int lvt_5_1_ = 0; lvt_5_1_ < EnumWorldBlockLayer.values().length; ++lvt_5_1_)
            {
                this.vertexBuffers[lvt_5_1_] = new VertexBuffer(DefaultVertexFormats.BLOCK);
            }
        }
    }

    public boolean setFrameIndex(int frameIndexIn)
    {
        if (this.frameIndex == frameIndexIn)
        {
            return false;
        }
        else
        {
            this.frameIndex = frameIndexIn;
            return true;
        }
    }

    public VertexBuffer getVertexBufferByLayer(int layer)
    {
        return this.vertexBuffers[layer];
    }

    public void setPosition(BlockPos pos)
    {
        this.stopCompileTask();
        this.position = pos;
        this.boundingBox = new AxisAlignedBB(pos, pos.add(16, 16, 16));

        for (EnumFacing lvt_5_1_ : EnumFacing.values())
        {
            this.mapEnumFacing.put(lvt_5_1_, pos.offset(lvt_5_1_, 16));
        }

        this.initModelviewMatrix();
    }

    public void resortTransparency(float x, float y, float z, ChunkCompileTaskGenerator generator)
    {
        CompiledChunk lvt_5_1_ = generator.getCompiledChunk();

        if (lvt_5_1_.getState() != null && !lvt_5_1_.isLayerEmpty(EnumWorldBlockLayer.TRANSLUCENT))
        {
            this.preRenderBlocks(generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(EnumWorldBlockLayer.TRANSLUCENT), this.position);
            generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(EnumWorldBlockLayer.TRANSLUCENT).setVertexState(lvt_5_1_.getState());
            this.postRenderBlocks(EnumWorldBlockLayer.TRANSLUCENT, x, y, z, generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(EnumWorldBlockLayer.TRANSLUCENT), lvt_5_1_);
        }
    }

    public void rebuildChunk(float x, float y, float z, ChunkCompileTaskGenerator generator)
    {
        CompiledChunk lvt_5_1_ = new CompiledChunk();
        int lvt_6_1_ = 1;
        BlockPos lvt_7_1_ = this.position;
        BlockPos lvt_8_1_ = lvt_7_1_.add(15, 15, 15);
        generator.getLock().lock();
        IBlockAccess lvt_9_1_;

        try
        {
            if (generator.getStatus() != ChunkCompileTaskGenerator.Status.COMPILING)
            {
                return;
            }

            lvt_9_1_ = new RegionRenderCache(this.world, lvt_7_1_.add(-1, -1, -1), lvt_8_1_.add(1, 1, 1), 1);
            generator.setCompiledChunk(lvt_5_1_);
        }
        finally
        {
            generator.getLock().unlock();
        }

        VisGraph lvt_10_1_ = new VisGraph();
        HashSet lvt_11_1_ = Sets.newHashSet();

        if (!lvt_9_1_.extendedLevelsInChunkCache())
        {
            ++renderChunksUpdated;
            boolean[] lvt_12_1_ = new boolean[EnumWorldBlockLayer.values().length];
            BlockRendererDispatcher lvt_13_1_ = Minecraft.getMinecraft().getBlockRendererDispatcher();

            for (BlockPos.MutableBlockPos lvt_15_1_ : BlockPos.getAllInBoxMutable(lvt_7_1_, lvt_8_1_))
            {
                IBlockState lvt_16_1_ = lvt_9_1_.getBlockState(lvt_15_1_);
                Block lvt_17_1_ = lvt_16_1_.getBlock();

                if (lvt_17_1_.isOpaqueCube())
                {
                    lvt_10_1_.func_178606_a(lvt_15_1_);
                }

                if (lvt_17_1_.hasTileEntity())
                {
                    TileEntity lvt_18_1_ = lvt_9_1_.getTileEntity(new BlockPos(lvt_15_1_));
                    TileEntitySpecialRenderer<TileEntity> lvt_19_1_ = TileEntityRendererDispatcher.instance.<TileEntity>getSpecialRenderer(lvt_18_1_);

                    if (lvt_18_1_ != null && lvt_19_1_ != null)
                    {
                        lvt_5_1_.addTileEntity(lvt_18_1_);

                        if (lvt_19_1_.forceTileEntityRender())
                        {
                            lvt_11_1_.add(lvt_18_1_);
                        }
                    }
                }

                EnumWorldBlockLayer lvt_18_2_ = lvt_17_1_.getBlockLayer();
                int lvt_19_2_ = lvt_18_2_.ordinal();

                if (lvt_17_1_.getRenderType() != -1)
                {
                    WorldRenderer lvt_20_1_ = generator.getRegionRenderCacheBuilder().getWorldRendererByLayerId(lvt_19_2_);

                    if (!lvt_5_1_.isLayerStarted(lvt_18_2_))
                    {
                        lvt_5_1_.setLayerStarted(lvt_18_2_);
                        this.preRenderBlocks(lvt_20_1_, lvt_7_1_);
                    }

                    lvt_12_1_[lvt_19_2_] |= lvt_13_1_.renderBlock(lvt_16_1_, lvt_15_1_, lvt_9_1_, lvt_20_1_);
                }
            }

            for (EnumWorldBlockLayer lvt_17_2_ : EnumWorldBlockLayer.values())
            {
                if (lvt_12_1_[lvt_17_2_.ordinal()])
                {
                    lvt_5_1_.setLayerUsed(lvt_17_2_);
                }

                if (lvt_5_1_.isLayerStarted(lvt_17_2_))
                {
                    this.postRenderBlocks(lvt_17_2_, x, y, z, generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(lvt_17_2_), lvt_5_1_);
                }
            }
        }

        lvt_5_1_.setVisibility(lvt_10_1_.computeVisibility());
        this.lockCompileTask.lock();

        try
        {
            Set<TileEntity> lvt_12_2_ = Sets.newHashSet(lvt_11_1_);
            Set<TileEntity> lvt_13_2_ = Sets.newHashSet(this.setTileEntities);
            lvt_12_2_.removeAll(this.setTileEntities);
            lvt_13_2_.removeAll(lvt_11_1_);
            this.setTileEntities.clear();
            this.setTileEntities.addAll(lvt_11_1_);
            this.renderGlobal.updateTileEntities(lvt_13_2_, lvt_12_2_);
        }
        finally
        {
            this.lockCompileTask.unlock();
        }
    }

    protected void finishCompileTask()
    {
        this.lockCompileTask.lock();

        try
        {
            if (this.compileTask != null && this.compileTask.getStatus() != ChunkCompileTaskGenerator.Status.DONE)
            {
                this.compileTask.finish();
                this.compileTask = null;
            }
        }
        finally
        {
            this.lockCompileTask.unlock();
        }
    }

    public ReentrantLock getLockCompileTask()
    {
        return this.lockCompileTask;
    }

    public ChunkCompileTaskGenerator makeCompileTaskChunk()
    {
        this.lockCompileTask.lock();
        ChunkCompileTaskGenerator var1;

        try
        {
            this.finishCompileTask();
            this.compileTask = new ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.REBUILD_CHUNK);
            var1 = this.compileTask;
        }
        finally
        {
            this.lockCompileTask.unlock();
        }

        return var1;
    }

    public ChunkCompileTaskGenerator makeCompileTaskTransparency()
    {
        this.lockCompileTask.lock();
        ChunkCompileTaskGenerator var1;

        try
        {
            if (this.compileTask == null || this.compileTask.getStatus() != ChunkCompileTaskGenerator.Status.PENDING)
            {
                if (this.compileTask != null && this.compileTask.getStatus() != ChunkCompileTaskGenerator.Status.DONE)
                {
                    this.compileTask.finish();
                    this.compileTask = null;
                }

                this.compileTask = new ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY);
                this.compileTask.setCompiledChunk(this.compiledChunk);
                var1 = this.compileTask;
                return var1;
            }

            var1 = null;
        }
        finally
        {
            this.lockCompileTask.unlock();
        }

        return var1;
    }

    private void preRenderBlocks(WorldRenderer worldRendererIn, BlockPos pos)
    {
        worldRendererIn.begin(7, DefaultVertexFormats.BLOCK);
        worldRendererIn.setTranslation((double)(-pos.getX()), (double)(-pos.getY()), (double)(-pos.getZ()));
    }

    private void postRenderBlocks(EnumWorldBlockLayer layer, float x, float y, float z, WorldRenderer worldRendererIn, CompiledChunk compiledChunkIn)
    {
        if (layer == EnumWorldBlockLayer.TRANSLUCENT && !compiledChunkIn.isLayerEmpty(layer))
        {
            worldRendererIn.sortVertexData(x, y, z);
            compiledChunkIn.setState(worldRendererIn.getVertexState());
        }

        worldRendererIn.finishDrawing();
    }

    private void initModelviewMatrix()
    {
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        float lvt_1_1_ = 1.000001F;
        GlStateManager.translate(-8.0F, -8.0F, -8.0F);
        GlStateManager.scale(lvt_1_1_, lvt_1_1_, lvt_1_1_);
        GlStateManager.translate(8.0F, 8.0F, 8.0F);
        GlStateManager.getFloat(2982, this.modelviewMatrix);
        GlStateManager.popMatrix();
    }

    public void multModelviewMatrix()
    {
        GlStateManager.multMatrix(this.modelviewMatrix);
    }

    public CompiledChunk getCompiledChunk()
    {
        return this.compiledChunk;
    }

    public void setCompiledChunk(CompiledChunk compiledChunkIn)
    {
        this.lockCompiledChunk.lock();

        try
        {
            this.compiledChunk = compiledChunkIn;
        }
        finally
        {
            this.lockCompiledChunk.unlock();
        }
    }

    public void stopCompileTask()
    {
        this.finishCompileTask();
        this.compiledChunk = CompiledChunk.DUMMY;
    }

    public void deleteGlResources()
    {
        this.stopCompileTask();
        this.world = null;

        for (int lvt_1_1_ = 0; lvt_1_1_ < EnumWorldBlockLayer.values().length; ++lvt_1_1_)
        {
            if (this.vertexBuffers[lvt_1_1_] != null)
            {
                this.vertexBuffers[lvt_1_1_].deleteGlBuffers();
            }
        }
    }

    public BlockPos getPosition()
    {
        return this.position;
    }

    public void setNeedsUpdate(boolean needsUpdateIn)
    {
        this.needsUpdate = needsUpdateIn;
    }

    public boolean isNeedsUpdate()
    {
        return this.needsUpdate;
    }

    public BlockPos getBlockPosOffset16(EnumFacing p_181701_1_)
    {
        return (BlockPos)this.mapEnumFacing.get(p_181701_1_);
    }
}
