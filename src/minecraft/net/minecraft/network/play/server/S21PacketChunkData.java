package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class S21PacketChunkData implements Packet<INetHandlerPlayClient>
{
    private int chunkX;
    private int chunkZ;
    private S21PacketChunkData.Extracted extractedData;
    private boolean field_149279_g;

    public S21PacketChunkData()
    {
    }

    public S21PacketChunkData(Chunk chunkIn, boolean p_i45196_2_, int p_i45196_3_)
    {
        this.chunkX = chunkIn.xPosition;
        this.chunkZ = chunkIn.zPosition;
        this.field_149279_g = p_i45196_2_;
        this.extractedData = getExtractedData(chunkIn, p_i45196_2_, !chunkIn.getWorld().provider.getHasNoSky(), p_i45196_3_);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.chunkX = buf.readInt();
        this.chunkZ = buf.readInt();
        this.field_149279_g = buf.readBoolean();
        this.extractedData = new S21PacketChunkData.Extracted();
        this.extractedData.dataSize = buf.readShort();
        this.extractedData.data = buf.readByteArray();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeInt(this.chunkX);
        buf.writeInt(this.chunkZ);
        buf.writeBoolean(this.field_149279_g);
        buf.writeShort((short)(this.extractedData.dataSize & 65535));
        buf.writeByteArray(this.extractedData.data);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleChunkData(this);
    }

    public byte[] getExtractedDataBytes()
    {
        return this.extractedData.data;
    }

    protected static int func_180737_a(int p_180737_0_, boolean p_180737_1_, boolean p_180737_2_)
    {
        int lvt_3_1_ = p_180737_0_ * 2 * 16 * 16 * 16;
        int lvt_4_1_ = p_180737_0_ * 16 * 16 * 16 / 2;
        int lvt_5_1_ = p_180737_1_ ? p_180737_0_ * 16 * 16 * 16 / 2 : 0;
        int lvt_6_1_ = p_180737_2_ ? 256 : 0;
        return lvt_3_1_ + lvt_4_1_ + lvt_5_1_ + lvt_6_1_;
    }

    public static S21PacketChunkData.Extracted getExtractedData(Chunk p_179756_0_, boolean p_179756_1_, boolean p_179756_2_, int p_179756_3_)
    {
        ExtendedBlockStorage[] lvt_4_1_ = p_179756_0_.getBlockStorageArray();
        S21PacketChunkData.Extracted lvt_5_1_ = new S21PacketChunkData.Extracted();
        List<ExtendedBlockStorage> lvt_6_1_ = Lists.newArrayList();

        for (int lvt_7_1_ = 0; lvt_7_1_ < lvt_4_1_.length; ++lvt_7_1_)
        {
            ExtendedBlockStorage lvt_8_1_ = lvt_4_1_[lvt_7_1_];

            if (lvt_8_1_ != null && (!p_179756_1_ || !lvt_8_1_.isEmpty()) && (p_179756_3_ & 1 << lvt_7_1_) != 0)
            {
                lvt_5_1_.dataSize |= 1 << lvt_7_1_;
                lvt_6_1_.add(lvt_8_1_);
            }
        }

        lvt_5_1_.data = new byte[func_180737_a(Integer.bitCount(lvt_5_1_.dataSize), p_179756_2_, p_179756_1_)];
        int lvt_7_2_ = 0;

        for (ExtendedBlockStorage lvt_9_1_ : lvt_6_1_)
        {
            char[] lvt_10_1_ = lvt_9_1_.getData();

            for (char lvt_14_1_ : lvt_10_1_)
            {
                lvt_5_1_.data[lvt_7_2_++] = (byte)(lvt_14_1_ & 255);
                lvt_5_1_.data[lvt_7_2_++] = (byte)(lvt_14_1_ >> 8 & 255);
            }
        }

        for (ExtendedBlockStorage lvt_9_2_ : lvt_6_1_)
        {
            lvt_7_2_ = func_179757_a(lvt_9_2_.getBlocklightArray().getData(), lvt_5_1_.data, lvt_7_2_);
        }

        if (p_179756_2_)
        {
            for (ExtendedBlockStorage lvt_9_3_ : lvt_6_1_)
            {
                lvt_7_2_ = func_179757_a(lvt_9_3_.getSkylightArray().getData(), lvt_5_1_.data, lvt_7_2_);
            }
        }

        if (p_179756_1_)
        {
            func_179757_a(p_179756_0_.getBiomeArray(), lvt_5_1_.data, lvt_7_2_);
        }

        return lvt_5_1_;
    }

    private static int func_179757_a(byte[] p_179757_0_, byte[] p_179757_1_, int p_179757_2_)
    {
        System.arraycopy(p_179757_0_, 0, p_179757_1_, p_179757_2_, p_179757_0_.length);
        return p_179757_2_ + p_179757_0_.length;
    }

    public int getChunkX()
    {
        return this.chunkX;
    }

    public int getChunkZ()
    {
        return this.chunkZ;
    }

    public int getExtractedSize()
    {
        return this.extractedData.dataSize;
    }

    public boolean func_149274_i()
    {
        return this.field_149279_g;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }

    public static class Extracted
    {
        public byte[] data;
        public int dataSize;
    }
}
