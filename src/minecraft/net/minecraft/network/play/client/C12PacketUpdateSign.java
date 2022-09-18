package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;

public class C12PacketUpdateSign implements Packet<INetHandlerPlayServer>
{
    private BlockPos pos;
    private IChatComponent[] lines;

    public C12PacketUpdateSign()
    {
    }

    public C12PacketUpdateSign(BlockPos pos, IChatComponent[] lines)
    {
        this.pos = pos;
        this.lines = new IChatComponent[] {lines[0], lines[1], lines[2], lines[3]};
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.pos = buf.readBlockPos();
        this.lines = new IChatComponent[4];

        for (int lvt_2_1_ = 0; lvt_2_1_ < 4; ++lvt_2_1_)
        {
            String lvt_3_1_ = buf.readStringFromBuffer(384);
            IChatComponent lvt_4_1_ = IChatComponent.Serializer.jsonToComponent(lvt_3_1_);
            this.lines[lvt_2_1_] = lvt_4_1_;
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeBlockPos(this.pos);

        for (int lvt_2_1_ = 0; lvt_2_1_ < 4; ++lvt_2_1_)
        {
            IChatComponent lvt_3_1_ = this.lines[lvt_2_1_];
            String lvt_4_1_ = IChatComponent.Serializer.componentToJson(lvt_3_1_);
            buf.writeString(lvt_4_1_);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processUpdateSign(this);
    }

    public BlockPos getPosition()
    {
        return this.pos;
    }

    public IChatComponent[] getLines()
    {
        return this.lines;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayServer)handler);
    }
}
