package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;

public class ChunkProviderDebug implements IChunkProvider
{
    private static final List<IBlockState> field_177464_a = Lists.newArrayList();
    private static final int field_177462_b;
    private static final int field_181039_c;
    private final World world;

    public ChunkProviderDebug(World worldIn)
    {
        this.world = worldIn;
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    public Chunk provideChunk(int x, int z)
    {
        ChunkPrimer lvt_3_1_ = new ChunkPrimer();

        for (int lvt_4_1_ = 0; lvt_4_1_ < 16; ++lvt_4_1_)
        {
            for (int lvt_5_1_ = 0; lvt_5_1_ < 16; ++lvt_5_1_)
            {
                int lvt_6_1_ = x * 16 + lvt_4_1_;
                int lvt_7_1_ = z * 16 + lvt_5_1_;
                lvt_3_1_.setBlockState(lvt_4_1_, 60, lvt_5_1_, Blocks.barrier.getDefaultState());
                IBlockState lvt_8_1_ = func_177461_b(lvt_6_1_, lvt_7_1_);

                if (lvt_8_1_ != null)
                {
                    lvt_3_1_.setBlockState(lvt_4_1_, 70, lvt_5_1_, lvt_8_1_);
                }
            }
        }

        Chunk lvt_4_2_ = new Chunk(this.world, lvt_3_1_, x, z);
        lvt_4_2_.generateSkylightMap();
        BiomeGenBase[] lvt_5_2_ = this.world.getWorldChunkManager().loadBlockGeneratorData((BiomeGenBase[])null, x * 16, z * 16, 16, 16);
        byte[] lvt_6_2_ = lvt_4_2_.getBiomeArray();

        for (int lvt_7_2_ = 0; lvt_7_2_ < lvt_6_2_.length; ++lvt_7_2_)
        {
            lvt_6_2_[lvt_7_2_] = (byte)lvt_5_2_[lvt_7_2_].biomeID;
        }

        lvt_4_2_.generateSkylightMap();
        return lvt_4_2_;
    }

    public static IBlockState func_177461_b(int p_177461_0_, int p_177461_1_)
    {
        IBlockState lvt_2_1_ = null;

        if (p_177461_0_ > 0 && p_177461_1_ > 0 && p_177461_0_ % 2 != 0 && p_177461_1_ % 2 != 0)
        {
            p_177461_0_ = p_177461_0_ / 2;
            p_177461_1_ = p_177461_1_ / 2;

            if (p_177461_0_ <= field_177462_b && p_177461_1_ <= field_181039_c)
            {
                int lvt_3_1_ = MathHelper.abs_int(p_177461_0_ * field_177462_b + p_177461_1_);

                if (lvt_3_1_ < field_177464_a.size())
                {
                    lvt_2_1_ = (IBlockState)field_177464_a.get(lvt_3_1_);
                }
            }
        }

        return lvt_2_1_;
    }

    /**
     * Checks to see if a chunk exists at x, z
     */
    public boolean chunkExists(int x, int z)
    {
        return true;
    }

    /**
     * Populates chunk with ores etc etc
     */
    public void populate(IChunkProvider chunkProvider, int x, int z)
    {
    }

    public boolean populateChunk(IChunkProvider chunkProvider, Chunk chunkIn, int x, int z)
    {
        return false;
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
     * Return true if all chunks have been saved.
     */
    public boolean saveChunks(boolean saveAllChunks, IProgressUpdate progressCallback)
    {
        return true;
    }

    /**
     * Save extra data not associated with any Chunk.  Not saved during autosave, only during world unload.  Currently
     * unimplemented.
     */
    public void saveExtraData()
    {
    }

    /**
     * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
     */
    public boolean unloadQueuedChunks()
    {
        return false;
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    public boolean canSave()
    {
        return true;
    }

    /**
     * Converts the instance data to a readable string.
     */
    public String makeString()
    {
        return "DebugLevelSource";
    }

    public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
    {
        BiomeGenBase lvt_3_1_ = this.world.getBiomeGenForCoords(pos);
        return lvt_3_1_.getSpawnableList(creatureType);
    }

    public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position)
    {
        return null;
    }

    public int getLoadedChunkCount()
    {
        return 0;
    }

    public void recreateStructures(Chunk chunkIn, int x, int z)
    {
    }

    public Chunk provideChunk(BlockPos blockPosIn)
    {
        return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
    }

    static
    {
        for (Block lvt_1_1_ : Block.blockRegistry)
        {
            field_177464_a.addAll(lvt_1_1_.getBlockState().getValidStates());
        }

        field_177462_b = MathHelper.ceiling_float_int(MathHelper.sqrt_float((float)field_177464_a.size()));
        field_181039_c = MathHelper.ceiling_float_int((float)field_177464_a.size() / (float)field_177462_b);
    }
}
