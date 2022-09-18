package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.VertexBufferUploader;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.EnumWorldBlockLayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class ChunkRenderDispatcher
{
    private static final Logger logger = LogManager.getLogger();
    private static final ThreadFactory threadFactory = (new ThreadFactoryBuilder()).setNameFormat("Chunk Batcher %d").setDaemon(true).build();
    private final List<ChunkRenderWorker> listThreadedWorkers = Lists.newArrayList();
    private final BlockingQueue<ChunkCompileTaskGenerator> queueChunkUpdates = Queues.newArrayBlockingQueue(100);
    private final BlockingQueue<RegionRenderCacheBuilder> queueFreeRenderBuilders = Queues.newArrayBlockingQueue(5);
    private final WorldVertexBufferUploader worldVertexUploader = new WorldVertexBufferUploader();
    private final VertexBufferUploader vertexUploader = new VertexBufferUploader();
    private final Queue < ListenableFutureTask<? >> queueChunkUploads = Queues.newArrayDeque();
    private final ChunkRenderWorker renderWorker;

    public ChunkRenderDispatcher()
    {
        for (int lvt_1_1_ = 0; lvt_1_1_ < 2; ++lvt_1_1_)
        {
            ChunkRenderWorker lvt_2_1_ = new ChunkRenderWorker(this);
            Thread lvt_3_1_ = threadFactory.newThread(lvt_2_1_);
            lvt_3_1_.start();
            this.listThreadedWorkers.add(lvt_2_1_);
        }

        for (int lvt_1_2_ = 0; lvt_1_2_ < 5; ++lvt_1_2_)
        {
            this.queueFreeRenderBuilders.add(new RegionRenderCacheBuilder());
        }

        this.renderWorker = new ChunkRenderWorker(this, new RegionRenderCacheBuilder());
    }

    public String getDebugInfo()
    {
        return String.format("pC: %03d, pU: %1d, aB: %1d", new Object[] {Integer.valueOf(this.queueChunkUpdates.size()), Integer.valueOf(this.queueChunkUploads.size()), Integer.valueOf(this.queueFreeRenderBuilders.size())});
    }

    public boolean runChunkUploads(long p_178516_1_)
    {
        boolean lvt_3_1_ = false;

        while (true)
        {
            boolean lvt_4_1_ = false;

            synchronized (this.queueChunkUploads)
            {
                if (!this.queueChunkUploads.isEmpty())
                {
                    ((ListenableFutureTask)this.queueChunkUploads.poll()).run();
                    lvt_4_1_ = true;
                    lvt_3_1_ = true;
                }
            }

            if (p_178516_1_ == 0L || !lvt_4_1_)
            {
                break;
            }

            long lvt_5_1_ = p_178516_1_ - System.nanoTime();

            if (lvt_5_1_ < 0L)
            {
                break;
            }
        }

        return lvt_3_1_;
    }

    public boolean updateChunkLater(RenderChunk chunkRenderer)
    {
        chunkRenderer.getLockCompileTask().lock();
        boolean var4;

        try
        {
            final ChunkCompileTaskGenerator lvt_2_1_ = chunkRenderer.makeCompileTaskChunk();
            lvt_2_1_.addFinishRunnable(new Runnable()
            {
                public void run()
                {
                    ChunkRenderDispatcher.this.queueChunkUpdates.remove(lvt_2_1_);
                }
            });
            boolean lvt_3_1_ = this.queueChunkUpdates.offer(lvt_2_1_);

            if (!lvt_3_1_)
            {
                lvt_2_1_.finish();
            }

            var4 = lvt_3_1_;
        }
        finally
        {
            chunkRenderer.getLockCompileTask().unlock();
        }

        return var4;
    }

    public boolean updateChunkNow(RenderChunk chunkRenderer)
    {
        chunkRenderer.getLockCompileTask().lock();
        boolean var3;

        try
        {
            ChunkCompileTaskGenerator lvt_2_1_ = chunkRenderer.makeCompileTaskChunk();

            try
            {
                this.renderWorker.processTask(lvt_2_1_);
            }
            catch (InterruptedException var7)
            {
                ;
            }

            var3 = true;
        }
        finally
        {
            chunkRenderer.getLockCompileTask().unlock();
        }

        return var3;
    }

    public void stopChunkUpdates()
    {
        this.clearChunkUpdates();

        while (this.runChunkUploads(0L))
        {
            ;
        }

        List<RegionRenderCacheBuilder> lvt_1_1_ = Lists.newArrayList();

        while (((List)lvt_1_1_).size() != 5)
        {
            try
            {
                lvt_1_1_.add(this.allocateRenderBuilder());
            }
            catch (InterruptedException var3)
            {
                ;
            }
        }

        this.queueFreeRenderBuilders.addAll(lvt_1_1_);
    }

    public void freeRenderBuilder(RegionRenderCacheBuilder p_178512_1_)
    {
        this.queueFreeRenderBuilders.add(p_178512_1_);
    }

    public RegionRenderCacheBuilder allocateRenderBuilder() throws InterruptedException
    {
        return (RegionRenderCacheBuilder)this.queueFreeRenderBuilders.take();
    }

    public ChunkCompileTaskGenerator getNextChunkUpdate() throws InterruptedException
    {
        return (ChunkCompileTaskGenerator)this.queueChunkUpdates.take();
    }

    public boolean updateTransparencyLater(RenderChunk chunkRenderer)
    {
        chunkRenderer.getLockCompileTask().lock();
        boolean var3;

        try
        {
            final ChunkCompileTaskGenerator lvt_2_1_ = chunkRenderer.makeCompileTaskTransparency();

            if (lvt_2_1_ == null)
            {
                var3 = true;
                return var3;
            }

            lvt_2_1_.addFinishRunnable(new Runnable()
            {
                public void run()
                {
                    ChunkRenderDispatcher.this.queueChunkUpdates.remove(lvt_2_1_);
                }
            });
            var3 = this.queueChunkUpdates.offer(lvt_2_1_);
        }
        finally
        {
            chunkRenderer.getLockCompileTask().unlock();
        }

        return var3;
    }

    public ListenableFuture<Object> uploadChunk(final EnumWorldBlockLayer player, final WorldRenderer p_178503_2_, final RenderChunk chunkRenderer, final CompiledChunk compiledChunkIn)
    {
        if (Minecraft.getMinecraft().isCallingFromMinecraftThread())
        {
            if (OpenGlHelper.useVbo())
            {
                this.uploadVertexBuffer(p_178503_2_, chunkRenderer.getVertexBufferByLayer(player.ordinal()));
            }
            else
            {
                this.uploadDisplayList(p_178503_2_, ((ListedRenderChunk)chunkRenderer).getDisplayList(player, compiledChunkIn), chunkRenderer);
            }

            p_178503_2_.setTranslation(0.0D, 0.0D, 0.0D);
            return Futures.immediateFuture((Object)null);
        }
        else
        {
            ListenableFutureTask<Object> lvt_5_1_ = ListenableFutureTask.create(new Runnable()
            {
                public void run()
                {
                    ChunkRenderDispatcher.this.uploadChunk(player, p_178503_2_, chunkRenderer, compiledChunkIn);
                }
            }, (Object)null);

            synchronized (this.queueChunkUploads)
            {
                this.queueChunkUploads.add(lvt_5_1_);
                return lvt_5_1_;
            }
        }
    }

    private void uploadDisplayList(WorldRenderer p_178510_1_, int p_178510_2_, RenderChunk chunkRenderer)
    {
        GL11.glNewList(p_178510_2_, GL11.GL_COMPILE);
        GlStateManager.pushMatrix();
        chunkRenderer.multModelviewMatrix();
        this.worldVertexUploader.draw(p_178510_1_);
        GlStateManager.popMatrix();
        GL11.glEndList();
    }

    private void uploadVertexBuffer(WorldRenderer p_178506_1_, VertexBuffer vertexBufferIn)
    {
        this.vertexUploader.setVertexBuffer(vertexBufferIn);
        this.vertexUploader.draw(p_178506_1_);
    }

    public void clearChunkUpdates()
    {
        while (!this.queueChunkUpdates.isEmpty())
        {
            ChunkCompileTaskGenerator lvt_1_1_ = (ChunkCompileTaskGenerator)this.queueChunkUpdates.poll();

            if (lvt_1_1_ != null)
            {
                lvt_1_1_.finish();
            }
        }
    }
}
