package net.minecraft.world.chunk.storage;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.NibbleArray;

public class ExtendedBlockStorage
{
    /**
     * Contains the bottom-most Y block represented by this ExtendedBlockStorage. Typically a multiple of 16.
     */
    private int yBase;

    /**
     * A total count of the number of non-air blocks in this block storage's Chunk.
     */
    private int blockRefCount;

    /**
     * Contains the number of blocks in this block storage's parent chunk that require random ticking. Used to cull the
     * Chunk from random tick updates for performance reasons.
     */
    private int tickRefCount;
    private char[] data;

    /** The NibbleArray containing a block of Block-light data. */
    private NibbleArray blocklightArray;

    /** The NibbleArray containing a block of Sky-light data. */
    private NibbleArray skylightArray;

    public ExtendedBlockStorage(int y, boolean storeSkylight)
    {
        this.yBase = y;
        this.data = new char[4096];
        this.blocklightArray = new NibbleArray();

        if (storeSkylight)
        {
            this.skylightArray = new NibbleArray();
        }
    }

    public IBlockState get(int x, int y, int z)
    {
        IBlockState lvt_4_1_ = (IBlockState)Block.BLOCK_STATE_IDS.getByValue(this.data[y << 8 | z << 4 | x]);
        return lvt_4_1_ != null ? lvt_4_1_ : Blocks.air.getDefaultState();
    }

    public void set(int x, int y, int z, IBlockState state)
    {
        IBlockState lvt_5_1_ = this.get(x, y, z);
        Block lvt_6_1_ = lvt_5_1_.getBlock();
        Block lvt_7_1_ = state.getBlock();

        if (lvt_6_1_ != Blocks.air)
        {
            --this.blockRefCount;

            if (lvt_6_1_.getTickRandomly())
            {
                --this.tickRefCount;
            }
        }

        if (lvt_7_1_ != Blocks.air)
        {
            ++this.blockRefCount;

            if (lvt_7_1_.getTickRandomly())
            {
                ++this.tickRefCount;
            }
        }

        this.data[y << 8 | z << 4 | x] = (char)Block.BLOCK_STATE_IDS.get(state);
    }

    /**
     * Returns the block for a location in a chunk, with the extended ID merged from a byte array and a NibbleArray to
     * form a full 12-bit block ID.
     */
    public Block getBlockByExtId(int x, int y, int z)
    {
        return this.get(x, y, z).getBlock();
    }

    /**
     * Returns the metadata associated with the block at the given coordinates in this ExtendedBlockStorage.
     */
    public int getExtBlockMetadata(int x, int y, int z)
    {
        IBlockState lvt_4_1_ = this.get(x, y, z);
        return lvt_4_1_.getBlock().getMetaFromState(lvt_4_1_);
    }

    /**
     * Returns whether or not this block storage's Chunk is fully empty, based on its internal reference count.
     */
    public boolean isEmpty()
    {
        return this.blockRefCount == 0;
    }

    /**
     * Returns whether or not this block storage's Chunk will require random ticking, used to avoid looping through
     * random block ticks when there are no blocks that would randomly tick.
     */
    public boolean getNeedsRandomTick()
    {
        return this.tickRefCount > 0;
    }

    /**
     * Returns the Y location of this ExtendedBlockStorage.
     */
    public int getYLocation()
    {
        return this.yBase;
    }

    /**
     * Sets the saved Sky-light value in the extended block storage structure.
     */
    public void setExtSkylightValue(int x, int y, int z, int value)
    {
        this.skylightArray.set(x, y, z, value);
    }

    /**
     * Gets the saved Sky-light value in the extended block storage structure.
     */
    public int getExtSkylightValue(int x, int y, int z)
    {
        return this.skylightArray.get(x, y, z);
    }

    /**
     * Sets the saved Block-light value in the extended block storage structure.
     */
    public void setExtBlocklightValue(int x, int y, int z, int value)
    {
        this.blocklightArray.set(x, y, z, value);
    }

    /**
     * Gets the saved Block-light value in the extended block storage structure.
     */
    public int getExtBlocklightValue(int x, int y, int z)
    {
        return this.blocklightArray.get(x, y, z);
    }

    public void removeInvalidBlocks()
    {
        this.blockRefCount = 0;
        this.tickRefCount = 0;

        for (int lvt_1_1_ = 0; lvt_1_1_ < 16; ++lvt_1_1_)
        {
            for (int lvt_2_1_ = 0; lvt_2_1_ < 16; ++lvt_2_1_)
            {
                for (int lvt_3_1_ = 0; lvt_3_1_ < 16; ++lvt_3_1_)
                {
                    Block lvt_4_1_ = this.getBlockByExtId(lvt_1_1_, lvt_2_1_, lvt_3_1_);

                    if (lvt_4_1_ != Blocks.air)
                    {
                        ++this.blockRefCount;

                        if (lvt_4_1_.getTickRandomly())
                        {
                            ++this.tickRefCount;
                        }
                    }
                }
            }
        }
    }

    public char[] getData()
    {
        return this.data;
    }

    public void setData(char[] dataArray)
    {
        this.data = dataArray;
    }

    /**
     * Returns the NibbleArray instance containing Block-light data.
     */
    public NibbleArray getBlocklightArray()
    {
        return this.blocklightArray;
    }

    /**
     * Returns the NibbleArray instance containing Sky-light data.
     */
    public NibbleArray getSkylightArray()
    {
        return this.skylightArray;
    }

    /**
     * Sets the NibbleArray instance used for Block-light values in this particular storage block.
     */
    public void setBlocklightArray(NibbleArray newBlocklightArray)
    {
        this.blocklightArray = newBlocklightArray;
    }

    /**
     * Sets the NibbleArray instance used for Sky-light values in this particular storage block.
     */
    public void setSkylightArray(NibbleArray newSkylightArray)
    {
        this.skylightArray = newSkylightArray;
    }
}
