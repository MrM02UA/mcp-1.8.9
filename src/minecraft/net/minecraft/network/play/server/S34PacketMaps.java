package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.Collection;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.Vec4b;
import net.minecraft.world.storage.MapData;

public class S34PacketMaps implements Packet<INetHandlerPlayClient>
{
    private int mapId;
    private byte mapScale;
    private Vec4b[] mapVisiblePlayersVec4b;
    private int mapMinX;
    private int mapMinY;
    private int mapMaxX;
    private int mapMaxY;
    private byte[] mapDataBytes;

    public S34PacketMaps()
    {
    }

    public S34PacketMaps(int mapIdIn, byte scale, Collection<Vec4b> visiblePlayers, byte[] colors, int minX, int minY, int maxX, int maxY)
    {
        this.mapId = mapIdIn;
        this.mapScale = scale;
        this.mapVisiblePlayersVec4b = (Vec4b[])visiblePlayers.toArray(new Vec4b[visiblePlayers.size()]);
        this.mapMinX = minX;
        this.mapMinY = minY;
        this.mapMaxX = maxX;
        this.mapMaxY = maxY;
        this.mapDataBytes = new byte[maxX * maxY];

        for (int lvt_9_1_ = 0; lvt_9_1_ < maxX; ++lvt_9_1_)
        {
            for (int lvt_10_1_ = 0; lvt_10_1_ < maxY; ++lvt_10_1_)
            {
                this.mapDataBytes[lvt_9_1_ + lvt_10_1_ * maxX] = colors[minX + lvt_9_1_ + (minY + lvt_10_1_) * 128];
            }
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.mapId = buf.readVarIntFromBuffer();
        this.mapScale = buf.readByte();
        this.mapVisiblePlayersVec4b = new Vec4b[buf.readVarIntFromBuffer()];

        for (int lvt_2_1_ = 0; lvt_2_1_ < this.mapVisiblePlayersVec4b.length; ++lvt_2_1_)
        {
            short lvt_3_1_ = (short)buf.readByte();
            this.mapVisiblePlayersVec4b[lvt_2_1_] = new Vec4b((byte)(lvt_3_1_ >> 4 & 15), buf.readByte(), buf.readByte(), (byte)(lvt_3_1_ & 15));
        }

        this.mapMaxX = buf.readUnsignedByte();

        if (this.mapMaxX > 0)
        {
            this.mapMaxY = buf.readUnsignedByte();
            this.mapMinX = buf.readUnsignedByte();
            this.mapMinY = buf.readUnsignedByte();
            this.mapDataBytes = buf.readByteArray();
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarIntToBuffer(this.mapId);
        buf.writeByte(this.mapScale);
        buf.writeVarIntToBuffer(this.mapVisiblePlayersVec4b.length);

        for (Vec4b lvt_5_1_ : this.mapVisiblePlayersVec4b)
        {
            buf.writeByte((lvt_5_1_.func_176110_a() & 15) << 4 | lvt_5_1_.func_176111_d() & 15);
            buf.writeByte(lvt_5_1_.func_176112_b());
            buf.writeByte(lvt_5_1_.func_176113_c());
        }

        buf.writeByte(this.mapMaxX);

        if (this.mapMaxX > 0)
        {
            buf.writeByte(this.mapMaxY);
            buf.writeByte(this.mapMinX);
            buf.writeByte(this.mapMinY);
            buf.writeByteArray(this.mapDataBytes);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleMaps(this);
    }

    public int getMapId()
    {
        return this.mapId;
    }

    /**
     * Sets new MapData from the packet to given MapData param
     */
    public void setMapdataTo(MapData mapdataIn)
    {
        mapdataIn.scale = this.mapScale;
        mapdataIn.mapDecorations.clear();

        for (int lvt_2_1_ = 0; lvt_2_1_ < this.mapVisiblePlayersVec4b.length; ++lvt_2_1_)
        {
            Vec4b lvt_3_1_ = this.mapVisiblePlayersVec4b[lvt_2_1_];
            mapdataIn.mapDecorations.put("icon-" + lvt_2_1_, lvt_3_1_);
        }

        for (int lvt_2_2_ = 0; lvt_2_2_ < this.mapMaxX; ++lvt_2_2_)
        {
            for (int lvt_3_2_ = 0; lvt_3_2_ < this.mapMaxY; ++lvt_3_2_)
            {
                mapdataIn.colors[this.mapMinX + lvt_2_2_ + (this.mapMinY + lvt_3_2_) * 128] = this.mapDataBytes[lvt_2_2_ + lvt_3_2_ * this.mapMaxX];
            }
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }
}
