package net.minecraft.world.chunk.storage;

import com.google.common.collect.Lists;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import net.minecraft.server.MinecraftServer;

public class RegionFile
{
    private static final byte[] emptySector = new byte[4096];
    private final File fileName;
    private RandomAccessFile dataFile;
    private final int[] offsets = new int[1024];
    private final int[] chunkTimestamps = new int[1024];
    private List<Boolean> sectorFree;

    /** McRegion sizeDelta */
    private int sizeDelta;
    private long lastModified;

    public RegionFile(File fileNameIn)
    {
        this.fileName = fileNameIn;
        this.sizeDelta = 0;

        try
        {
            if (fileNameIn.exists())
            {
                this.lastModified = fileNameIn.lastModified();
            }

            this.dataFile = new RandomAccessFile(fileNameIn, "rw");

            if (this.dataFile.length() < 4096L)
            {
                for (int lvt_2_1_ = 0; lvt_2_1_ < 1024; ++lvt_2_1_)
                {
                    this.dataFile.writeInt(0);
                }

                for (int lvt_2_2_ = 0; lvt_2_2_ < 1024; ++lvt_2_2_)
                {
                    this.dataFile.writeInt(0);
                }

                this.sizeDelta += 8192;
            }

            if ((this.dataFile.length() & 4095L) != 0L)
            {
                for (int lvt_2_3_ = 0; (long)lvt_2_3_ < (this.dataFile.length() & 4095L); ++lvt_2_3_)
                {
                    this.dataFile.write(0);
                }
            }

            int lvt_2_4_ = (int)this.dataFile.length() / 4096;
            this.sectorFree = Lists.newArrayListWithCapacity(lvt_2_4_);

            for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_4_; ++lvt_3_1_)
            {
                this.sectorFree.add(Boolean.valueOf(true));
            }

            this.sectorFree.set(0, Boolean.valueOf(false));
            this.sectorFree.set(1, Boolean.valueOf(false));
            this.dataFile.seek(0L);

            for (int lvt_3_2_ = 0; lvt_3_2_ < 1024; ++lvt_3_2_)
            {
                int lvt_4_1_ = this.dataFile.readInt();
                this.offsets[lvt_3_2_] = lvt_4_1_;

                if (lvt_4_1_ != 0 && (lvt_4_1_ >> 8) + (lvt_4_1_ & 255) <= this.sectorFree.size())
                {
                    for (int lvt_5_1_ = 0; lvt_5_1_ < (lvt_4_1_ & 255); ++lvt_5_1_)
                    {
                        this.sectorFree.set((lvt_4_1_ >> 8) + lvt_5_1_, Boolean.valueOf(false));
                    }
                }
            }

            for (int lvt_3_3_ = 0; lvt_3_3_ < 1024; ++lvt_3_3_)
            {
                int lvt_4_2_ = this.dataFile.readInt();
                this.chunkTimestamps[lvt_3_3_] = lvt_4_2_;
            }
        }
        catch (IOException var6)
        {
            var6.printStackTrace();
        }
    }

    /**
     * Returns an uncompressed chunk stream from the region file.
     */
    public synchronized DataInputStream getChunkDataInputStream(int x, int z)
    {
        if (this.outOfBounds(x, z))
        {
            return null;
        }
        else
        {
            try
            {
                int lvt_3_1_ = this.getOffset(x, z);

                if (lvt_3_1_ == 0)
                {
                    return null;
                }
                else
                {
                    int lvt_4_1_ = lvt_3_1_ >> 8;
                    int lvt_5_1_ = lvt_3_1_ & 255;

                    if (lvt_4_1_ + lvt_5_1_ > this.sectorFree.size())
                    {
                        return null;
                    }
                    else
                    {
                        this.dataFile.seek((long)(lvt_4_1_ * 4096));
                        int lvt_6_1_ = this.dataFile.readInt();

                        if (lvt_6_1_ > 4096 * lvt_5_1_)
                        {
                            return null;
                        }
                        else if (lvt_6_1_ <= 0)
                        {
                            return null;
                        }
                        else
                        {
                            byte lvt_7_1_ = this.dataFile.readByte();

                            if (lvt_7_1_ == 1)
                            {
                                byte[] lvt_8_1_ = new byte[lvt_6_1_ - 1];
                                this.dataFile.read(lvt_8_1_);
                                return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(lvt_8_1_))));
                            }
                            else if (lvt_7_1_ == 2)
                            {
                                byte[] lvt_8_2_ = new byte[lvt_6_1_ - 1];
                                this.dataFile.read(lvt_8_2_);
                                return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(lvt_8_2_))));
                            }
                            else
                            {
                                return null;
                            }
                        }
                    }
                }
            }
            catch (IOException var9)
            {
                return null;
            }
        }
    }

    /**
     * Returns an output stream used to write chunk data. Data is on disk when the returned stream is closed.
     */
    public DataOutputStream getChunkDataOutputStream(int x, int z)
    {
        return this.outOfBounds(x, z) ? null : new DataOutputStream(new DeflaterOutputStream(new RegionFile.ChunkBuffer(x, z)));
    }

    /**
     * args: x, z, data, length - write chunk data at (x, z) to disk
     */
    protected synchronized void write(int x, int z, byte[] data, int length)
    {
        try
        {
            int lvt_5_1_ = this.getOffset(x, z);
            int lvt_6_1_ = lvt_5_1_ >> 8;
            int lvt_7_1_ = lvt_5_1_ & 255;
            int lvt_8_1_ = (length + 5) / 4096 + 1;

            if (lvt_8_1_ >= 256)
            {
                return;
            }

            if (lvt_6_1_ != 0 && lvt_7_1_ == lvt_8_1_)
            {
                this.write(lvt_6_1_, data, length);
            }
            else
            {
                for (int lvt_9_1_ = 0; lvt_9_1_ < lvt_7_1_; ++lvt_9_1_)
                {
                    this.sectorFree.set(lvt_6_1_ + lvt_9_1_, Boolean.valueOf(true));
                }

                int lvt_9_2_ = this.sectorFree.indexOf(Boolean.valueOf(true));
                int lvt_10_1_ = 0;

                if (lvt_9_2_ != -1)
                {
                    for (int lvt_11_1_ = lvt_9_2_; lvt_11_1_ < this.sectorFree.size(); ++lvt_11_1_)
                    {
                        if (lvt_10_1_ != 0)
                        {
                            if (((Boolean)this.sectorFree.get(lvt_11_1_)).booleanValue())
                            {
                                ++lvt_10_1_;
                            }
                            else
                            {
                                lvt_10_1_ = 0;
                            }
                        }
                        else if (((Boolean)this.sectorFree.get(lvt_11_1_)).booleanValue())
                        {
                            lvt_9_2_ = lvt_11_1_;
                            lvt_10_1_ = 1;
                        }

                        if (lvt_10_1_ >= lvt_8_1_)
                        {
                            break;
                        }
                    }
                }

                if (lvt_10_1_ >= lvt_8_1_)
                {
                    lvt_6_1_ = lvt_9_2_;
                    this.setOffset(x, z, lvt_9_2_ << 8 | lvt_8_1_);

                    for (int lvt_11_2_ = 0; lvt_11_2_ < lvt_8_1_; ++lvt_11_2_)
                    {
                        this.sectorFree.set(lvt_6_1_ + lvt_11_2_, Boolean.valueOf(false));
                    }

                    this.write(lvt_6_1_, data, length);
                }
                else
                {
                    this.dataFile.seek(this.dataFile.length());
                    lvt_6_1_ = this.sectorFree.size();

                    for (int lvt_11_3_ = 0; lvt_11_3_ < lvt_8_1_; ++lvt_11_3_)
                    {
                        this.dataFile.write(emptySector);
                        this.sectorFree.add(Boolean.valueOf(false));
                    }

                    this.sizeDelta += 4096 * lvt_8_1_;
                    this.write(lvt_6_1_, data, length);
                    this.setOffset(x, z, lvt_6_1_ << 8 | lvt_8_1_);
                }
            }

            this.setChunkTimestamp(x, z, (int)(MinecraftServer.getCurrentTimeMillis() / 1000L));
        }
        catch (IOException var12)
        {
            var12.printStackTrace();
        }
    }

    /**
     * args: sectorNumber, data, length - write the chunk data to this RegionFile
     */
    private void write(int sectorNumber, byte[] data, int length) throws IOException
    {
        this.dataFile.seek((long)(sectorNumber * 4096));
        this.dataFile.writeInt(length + 1);
        this.dataFile.writeByte(2);
        this.dataFile.write(data, 0, length);
    }

    /**
     * args: x, z - check region bounds
     */
    private boolean outOfBounds(int x, int z)
    {
        return x < 0 || x >= 32 || z < 0 || z >= 32;
    }

    /**
     * args: x, z - get chunk's offset in region file
     */
    private int getOffset(int x, int z)
    {
        return this.offsets[x + z * 32];
    }

    /**
     * args: x, z, - true if chunk has been saved / converted
     */
    public boolean isChunkSaved(int x, int z)
    {
        return this.getOffset(x, z) != 0;
    }

    /**
     * args: x, z, offset - sets the chunk's offset in the region file
     */
    private void setOffset(int x, int z, int offset) throws IOException
    {
        this.offsets[x + z * 32] = offset;
        this.dataFile.seek((long)((x + z * 32) * 4));
        this.dataFile.writeInt(offset);
    }

    /**
     * args: x, z, timestamp - sets the chunk's write timestamp
     */
    private void setChunkTimestamp(int x, int z, int timestamp) throws IOException
    {
        this.chunkTimestamps[x + z * 32] = timestamp;
        this.dataFile.seek((long)(4096 + (x + z * 32) * 4));
        this.dataFile.writeInt(timestamp);
    }

    /**
     * close this RegionFile and prevent further writes
     */
    public void close() throws IOException
    {
        if (this.dataFile != null)
        {
            this.dataFile.close();
        }
    }

    class ChunkBuffer extends ByteArrayOutputStream
    {
        private int chunkX;
        private int chunkZ;

        public ChunkBuffer(int x, int z)
        {
            super(8096);
            this.chunkX = x;
            this.chunkZ = z;
        }

        public void close() throws IOException
        {
            RegionFile.this.write(this.chunkX, this.chunkZ, this.buf, this.count);
        }
    }
}
