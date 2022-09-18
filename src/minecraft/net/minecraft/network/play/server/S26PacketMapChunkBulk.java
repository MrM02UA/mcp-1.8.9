package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.chunk.Chunk;

public class S26PacketMapChunkBulk implements Packet<INetHandlerPlayClient>
{
    private int[] xPositions;
    private int[] zPositions;
    private S21PacketChunkData.Extracted[] chunksData;
    private boolean isOverworld;

    public S26PacketMapChunkBulk()
    {
    }

    public S26PacketMapChunkBulk(List<Chunk> chunks)
    {
        int lvt_2_1_ = chunks.size();
        this.xPositions = new int[lvt_2_1_];
        this.zPositions = new int[lvt_2_1_];
        this.chunksData = new S21PacketChunkData.Extracted[lvt_2_1_];
        this.isOverworld = !((Chunk)chunks.get(0)).getWorld().provider.getHasNoSky();

        for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_; ++lvt_3_1_)
        {
            Chunk lvt_4_1_ = (Chunk)chunks.get(lvt_3_1_);
            S21PacketChunkData.Extracted lvt_5_1_ = S21PacketChunkData.getExtractedData(lvt_4_1_, true, this.isOverworld, 65535);
            this.xPositions[lvt_3_1_] = lvt_4_1_.xPosition;
            this.zPositions[lvt_3_1_] = lvt_4_1_.zPosition;
            this.chunksData[lvt_3_1_] = lvt_5_1_;
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.isOverworld = buf.readBoolean();
        int lvt_2_1_ = buf.readVarIntFromBuffer();
        this.xPositions = new int[lvt_2_1_];
        this.zPositions = new int[lvt_2_1_];
        this.chunksData = new S21PacketChunkData.Extracted[lvt_2_1_];

        for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_; ++lvt_3_1_)
        {
            this.xPositions[lvt_3_1_] = buf.readInt();
            this.zPositions[lvt_3_1_] = buf.readInt();
            this.chunksData[lvt_3_1_] = new S21PacketChunkData.Extracted();
            this.chunksData[lvt_3_1_].dataSize = buf.readShort() & 65535;
            this.chunksData[lvt_3_1_].data = new byte[S21PacketChunkData.func_180737_a(Integer.bitCount(this.chunksData[lvt_3_1_].dataSize), this.isOverworld, true)];
        }

        for (int lvt_3_2_ = 0; lvt_3_2_ < lvt_2_1_; ++lvt_3_2_)
        {
            buf.readBytes(this.chunksData[lvt_3_2_].data);
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeBoolean(this.isOverworld);
        buf.writeVarIntToBuffer(this.chunksData.length);

        for (int lvt_2_1_ = 0; lvt_2_1_ < this.xPositions.length; ++lvt_2_1_)
        {
            buf.writeInt(this.xPositions[lvt_2_1_]);
            buf.writeInt(this.zPositions[lvt_2_1_]);
            buf.writeShort((short)(this.chunksData[lvt_2_1_].dataSize & 65535));
        }

        for (int lvt_2_2_ = 0; lvt_2_2_ < this.xPositions.length; ++lvt_2_2_)
        {
            buf.writeBytes(this.chunksData[lvt_2_2_].data);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleMapChunkBulk(this);
    }

    public int getChunkX(int p_149255_1_)
    {
        return this.xPositions[p_149255_1_];
    }

    public int getChunkZ(int p_149253_1_)
    {
        return this.zPositions[p_149253_1_];
    }

    public int getChunkCount()
    {
        return this.xPositions.length;
    }

    public byte[] getChunkBytes(int p_149256_1_)
    {
        return this.chunksData[p_149256_1_].data;
    }

    public int getChunkSize(int p_179754_1_)
    {
        return this.chunksData[p_179754_1_].dataSize;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }
}
