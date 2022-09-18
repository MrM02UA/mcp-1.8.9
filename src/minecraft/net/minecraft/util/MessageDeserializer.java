package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class MessageDeserializer extends ByteToMessageDecoder
{
    private static final Logger logger = LogManager.getLogger();
    private static final Marker RECEIVED_PACKET_MARKER = MarkerManager.getMarker("PACKET_RECEIVED", NetworkManager.logMarkerPackets);
    private final EnumPacketDirection direction;

    public MessageDeserializer(EnumPacketDirection direction)
    {
        this.direction = direction;
    }

    protected void decode(ChannelHandlerContext p_decode_1_, ByteBuf p_decode_2_, List<Object> p_decode_3_) throws IOException, InstantiationException, IllegalAccessException, Exception
    {
        if (p_decode_2_.readableBytes() != 0)
        {
            PacketBuffer lvt_4_1_ = new PacketBuffer(p_decode_2_);
            int lvt_5_1_ = lvt_4_1_.readVarIntFromBuffer();
            Packet lvt_6_1_ = ((EnumConnectionState)p_decode_1_.channel().attr(NetworkManager.attrKeyConnectionState).get()).getPacket(this.direction, lvt_5_1_);

            if (lvt_6_1_ == null)
            {
                throw new IOException("Bad packet id " + lvt_5_1_);
            }
            else
            {
                lvt_6_1_.readPacketData(lvt_4_1_);

                if (lvt_4_1_.readableBytes() > 0)
                {
                    throw new IOException("Packet " + ((EnumConnectionState)p_decode_1_.channel().attr(NetworkManager.attrKeyConnectionState).get()).getId() + "/" + lvt_5_1_ + " (" + lvt_6_1_.getClass().getSimpleName() + ") was larger than I expected, found " + lvt_4_1_.readableBytes() + " bytes extra whilst reading packet " + lvt_5_1_);
                }
                else
                {
                    p_decode_3_.add(lvt_6_1_);

                    if (logger.isDebugEnabled())
                    {
                        logger.debug(RECEIVED_PACKET_MARKER, " IN: [{}:{}] {}", new Object[] {p_decode_1_.channel().attr(NetworkManager.attrKeyConnectionState).get(), Integer.valueOf(lvt_5_1_), lvt_6_1_.getClass().getName()});
                    }
                }
            }
        }
    }
}
