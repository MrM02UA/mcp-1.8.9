package net.minecraft.client.renderer;

import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ViewFrustum
{
    protected final RenderGlobal renderGlobal;
    protected final World world;
    protected int countChunksY;
    protected int countChunksX;
    protected int countChunksZ;
    public RenderChunk[] renderChunks;

    public ViewFrustum(World worldIn, int renderDistanceChunks, RenderGlobal p_i46246_3_, IRenderChunkFactory renderChunkFactory)
    {
        this.renderGlobal = p_i46246_3_;
        this.world = worldIn;
        this.setCountChunksXYZ(renderDistanceChunks);
        this.createRenderChunks(renderChunkFactory);
    }

    protected void createRenderChunks(IRenderChunkFactory renderChunkFactory)
    {
        int lvt_2_1_ = this.countChunksX * this.countChunksY * this.countChunksZ;
        this.renderChunks = new RenderChunk[lvt_2_1_];
        int lvt_3_1_ = 0;

        for (int lvt_4_1_ = 0; lvt_4_1_ < this.countChunksX; ++lvt_4_1_)
        {
            for (int lvt_5_1_ = 0; lvt_5_1_ < this.countChunksY; ++lvt_5_1_)
            {
                for (int lvt_6_1_ = 0; lvt_6_1_ < this.countChunksZ; ++lvt_6_1_)
                {
                    int lvt_7_1_ = (lvt_6_1_ * this.countChunksY + lvt_5_1_) * this.countChunksX + lvt_4_1_;
                    BlockPos lvt_8_1_ = new BlockPos(lvt_4_1_ * 16, lvt_5_1_ * 16, lvt_6_1_ * 16);
                    this.renderChunks[lvt_7_1_] = renderChunkFactory.makeRenderChunk(this.world, this.renderGlobal, lvt_8_1_, lvt_3_1_++);
                }
            }
        }
    }

    public void deleteGlResources()
    {
        for (RenderChunk lvt_4_1_ : this.renderChunks)
        {
            lvt_4_1_.deleteGlResources();
        }
    }

    protected void setCountChunksXYZ(int renderDistanceChunks)
    {
        int lvt_2_1_ = renderDistanceChunks * 2 + 1;
        this.countChunksX = lvt_2_1_;
        this.countChunksY = 16;
        this.countChunksZ = lvt_2_1_;
    }

    public void updateChunkPositions(double viewEntityX, double viewEntityZ)
    {
        int lvt_5_1_ = MathHelper.floor_double(viewEntityX) - 8;
        int lvt_6_1_ = MathHelper.floor_double(viewEntityZ) - 8;
        int lvt_7_1_ = this.countChunksX * 16;

        for (int lvt_8_1_ = 0; lvt_8_1_ < this.countChunksX; ++lvt_8_1_)
        {
            int lvt_9_1_ = this.func_178157_a(lvt_5_1_, lvt_7_1_, lvt_8_1_);

            for (int lvt_10_1_ = 0; lvt_10_1_ < this.countChunksZ; ++lvt_10_1_)
            {
                int lvt_11_1_ = this.func_178157_a(lvt_6_1_, lvt_7_1_, lvt_10_1_);

                for (int lvt_12_1_ = 0; lvt_12_1_ < this.countChunksY; ++lvt_12_1_)
                {
                    int lvt_13_1_ = lvt_12_1_ * 16;
                    RenderChunk lvt_14_1_ = this.renderChunks[(lvt_10_1_ * this.countChunksY + lvt_12_1_) * this.countChunksX + lvt_8_1_];
                    BlockPos lvt_15_1_ = new BlockPos(lvt_9_1_, lvt_13_1_, lvt_11_1_);

                    if (!lvt_15_1_.equals(lvt_14_1_.getPosition()))
                    {
                        lvt_14_1_.setPosition(lvt_15_1_);
                    }
                }
            }
        }
    }

    private int func_178157_a(int p_178157_1_, int p_178157_2_, int p_178157_3_)
    {
        int lvt_4_1_ = p_178157_3_ * 16;
        int lvt_5_1_ = lvt_4_1_ - p_178157_1_ + p_178157_2_ / 2;

        if (lvt_5_1_ < 0)
        {
            lvt_5_1_ -= p_178157_2_ - 1;
        }

        return lvt_4_1_ - lvt_5_1_ / p_178157_2_ * p_178157_2_;
    }

    public void markBlocksForUpdate(int fromX, int fromY, int fromZ, int toX, int toY, int toZ)
    {
        int lvt_7_1_ = MathHelper.bucketInt(fromX, 16);
        int lvt_8_1_ = MathHelper.bucketInt(fromY, 16);
        int lvt_9_1_ = MathHelper.bucketInt(fromZ, 16);
        int lvt_10_1_ = MathHelper.bucketInt(toX, 16);
        int lvt_11_1_ = MathHelper.bucketInt(toY, 16);
        int lvt_12_1_ = MathHelper.bucketInt(toZ, 16);

        for (int lvt_13_1_ = lvt_7_1_; lvt_13_1_ <= lvt_10_1_; ++lvt_13_1_)
        {
            int lvt_14_1_ = lvt_13_1_ % this.countChunksX;

            if (lvt_14_1_ < 0)
            {
                lvt_14_1_ += this.countChunksX;
            }

            for (int lvt_15_1_ = lvt_8_1_; lvt_15_1_ <= lvt_11_1_; ++lvt_15_1_)
            {
                int lvt_16_1_ = lvt_15_1_ % this.countChunksY;

                if (lvt_16_1_ < 0)
                {
                    lvt_16_1_ += this.countChunksY;
                }

                for (int lvt_17_1_ = lvt_9_1_; lvt_17_1_ <= lvt_12_1_; ++lvt_17_1_)
                {
                    int lvt_18_1_ = lvt_17_1_ % this.countChunksZ;

                    if (lvt_18_1_ < 0)
                    {
                        lvt_18_1_ += this.countChunksZ;
                    }

                    int lvt_19_1_ = (lvt_18_1_ * this.countChunksY + lvt_16_1_) * this.countChunksX + lvt_14_1_;
                    RenderChunk lvt_20_1_ = this.renderChunks[lvt_19_1_];
                    lvt_20_1_.setNeedsUpdate(true);
                }
            }
        }
    }

    protected RenderChunk getRenderChunk(BlockPos pos)
    {
        int lvt_2_1_ = MathHelper.bucketInt(pos.getX(), 16);
        int lvt_3_1_ = MathHelper.bucketInt(pos.getY(), 16);
        int lvt_4_1_ = MathHelper.bucketInt(pos.getZ(), 16);

        if (lvt_3_1_ >= 0 && lvt_3_1_ < this.countChunksY)
        {
            lvt_2_1_ = lvt_2_1_ % this.countChunksX;

            if (lvt_2_1_ < 0)
            {
                lvt_2_1_ += this.countChunksX;
            }

            lvt_4_1_ = lvt_4_1_ % this.countChunksZ;

            if (lvt_4_1_ < 0)
            {
                lvt_4_1_ += this.countChunksZ;
            }

            int lvt_5_1_ = (lvt_4_1_ * this.countChunksY + lvt_3_1_) * this.countChunksX + lvt_2_1_;
            return this.renderChunks[lvt_5_1_];
        }
        else
        {
            return null;
        }
    }
}
