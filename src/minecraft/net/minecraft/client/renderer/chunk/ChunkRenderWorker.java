package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumWorldBlockLayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkRenderWorker implements Runnable
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final ChunkRenderDispatcher chunkRenderDispatcher;
    private final RegionRenderCacheBuilder regionRenderCacheBuilder;

    public ChunkRenderWorker(ChunkRenderDispatcher p_i46201_1_)
    {
        this(p_i46201_1_, (RegionRenderCacheBuilder)null);
    }

    public ChunkRenderWorker(ChunkRenderDispatcher chunkRenderDispatcherIn, RegionRenderCacheBuilder regionRenderCacheBuilderIn)
    {
        this.chunkRenderDispatcher = chunkRenderDispatcherIn;
        this.regionRenderCacheBuilder = regionRenderCacheBuilderIn;
    }

    public void run()
    {
        while (true)
        {
            try
            {
                this.processTask(this.chunkRenderDispatcher.getNextChunkUpdate());
            }
            catch (InterruptedException var3)
            {
                LOGGER.debug("Stopping due to interrupt");
                return;
            }
            catch (Throwable var4)
            {
                CrashReport lvt_2_1_ = CrashReport.makeCrashReport(var4, "Batching chunks");
                Minecraft.getMinecraft().crashed(Minecraft.getMinecraft().addGraphicsAndWorldToCrashReport(lvt_2_1_));
                return;
            }
        }
    }

    protected void processTask(final ChunkCompileTaskGenerator generator) throws InterruptedException
    {
        generator.getLock().lock();

        try
        {
            if (generator.getStatus() != ChunkCompileTaskGenerator.Status.PENDING)
            {
                if (!generator.isFinished())
                {
                    LOGGER.warn("Chunk render task was " + generator.getStatus() + " when I expected it to be pending; ignoring task");
                }

                return;
            }

            generator.setStatus(ChunkCompileTaskGenerator.Status.COMPILING);
        }
        finally
        {
            generator.getLock().unlock();
        }

        Entity lvt_2_1_ = Minecraft.getMinecraft().getRenderViewEntity();

        if (lvt_2_1_ == null)
        {
            generator.finish();
        }
        else
        {
            generator.setRegionRenderCacheBuilder(this.getRegionRenderCacheBuilder());
            float lvt_3_1_ = (float)lvt_2_1_.posX;
            float lvt_4_1_ = (float)lvt_2_1_.posY + lvt_2_1_.getEyeHeight();
            float lvt_5_1_ = (float)lvt_2_1_.posZ;
            ChunkCompileTaskGenerator.Type lvt_6_1_ = generator.getType();

            if (lvt_6_1_ == ChunkCompileTaskGenerator.Type.REBUILD_CHUNK)
            {
                generator.getRenderChunk().rebuildChunk(lvt_3_1_, lvt_4_1_, lvt_5_1_, generator);
            }
            else if (lvt_6_1_ == ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY)
            {
                generator.getRenderChunk().resortTransparency(lvt_3_1_, lvt_4_1_, lvt_5_1_, generator);
            }

            generator.getLock().lock();

            try
            {
                if (generator.getStatus() != ChunkCompileTaskGenerator.Status.COMPILING)
                {
                    if (!generator.isFinished())
                    {
                        LOGGER.warn("Chunk render task was " + generator.getStatus() + " when I expected it to be compiling; aborting task");
                    }

                    this.freeRenderBuilder(generator);
                    return;
                }

                generator.setStatus(ChunkCompileTaskGenerator.Status.UPLOADING);
            }
            finally
            {
                generator.getLock().unlock();
            }

            final CompiledChunk lvt_7_1_ = generator.getCompiledChunk();
            ArrayList lvt_8_1_ = Lists.newArrayList();

            if (lvt_6_1_ == ChunkCompileTaskGenerator.Type.REBUILD_CHUNK)
            {
                for (EnumWorldBlockLayer lvt_12_1_ : EnumWorldBlockLayer.values())
                {
                    if (lvt_7_1_.isLayerStarted(lvt_12_1_))
                    {
                        lvt_8_1_.add(this.chunkRenderDispatcher.uploadChunk(lvt_12_1_, generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(lvt_12_1_), generator.getRenderChunk(), lvt_7_1_));
                    }
                }
            }
            else if (lvt_6_1_ == ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY)
            {
                lvt_8_1_.add(this.chunkRenderDispatcher.uploadChunk(EnumWorldBlockLayer.TRANSLUCENT, generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(EnumWorldBlockLayer.TRANSLUCENT), generator.getRenderChunk(), lvt_7_1_));
            }

            final ListenableFuture<List<Object>> lvt_9_2_ = Futures.allAsList(lvt_8_1_);
            generator.addFinishRunnable(new Runnable()
            {
                public void run()
                {
                    lvt_9_2_.cancel(false);
                }
            });
            Futures.addCallback(lvt_9_2_, new FutureCallback<List<Object>>()
            {
                public void onSuccess(List<Object> p_onSuccess_1_)
                {
                    ChunkRenderWorker.this.freeRenderBuilder(generator);
                    generator.getLock().lock();
                    label21:
                    {
                        try
                        {
                            if (generator.getStatus() == ChunkCompileTaskGenerator.Status.UPLOADING)
                            {
                                generator.setStatus(ChunkCompileTaskGenerator.Status.DONE);
                                break label21;
                            }

                            if (!generator.isFinished())
                            {
                                ChunkRenderWorker.LOGGER.warn("Chunk render task was " + generator.getStatus() + " when I expected it to be uploading; aborting task");
                            }
                        }
                        finally
                        {
                            generator.getLock().unlock();
                        }

                        return;
                    }
                    generator.getRenderChunk().setCompiledChunk(lvt_7_1_);
                }
                public void onFailure(Throwable p_onFailure_1_)
                {
                    ChunkRenderWorker.this.freeRenderBuilder(generator);

                    if (!(p_onFailure_1_ instanceof CancellationException) && !(p_onFailure_1_ instanceof InterruptedException))
                    {
                        Minecraft.getMinecraft().crashed(CrashReport.makeCrashReport(p_onFailure_1_, "Rendering chunk"));
                    }
                }
                public void onSuccess(Object p_onSuccess_1_)
                {
                    this.onSuccess((List)p_onSuccess_1_);
                }
            });
        }
    }

    private RegionRenderCacheBuilder getRegionRenderCacheBuilder() throws InterruptedException
    {
        return this.regionRenderCacheBuilder != null ? this.regionRenderCacheBuilder : this.chunkRenderDispatcher.allocateRenderBuilder();
    }

    private void freeRenderBuilder(ChunkCompileTaskGenerator taskGenerator)
    {
        if (this.regionRenderCacheBuilder == null)
        {
            this.chunkRenderDispatcher.freeRenderBuilder(taskGenerator.getRegionRenderCacheBuilder());
        }
    }
}
