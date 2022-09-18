package net.minecraft.network;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.net.InetSocketAddress;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PingResponseHandler extends ChannelInboundHandlerAdapter
{
    private static final Logger logger = LogManager.getLogger();
    private NetworkSystem networkSystem;

    public PingResponseHandler(NetworkSystem networkSystemIn)
    {
        this.networkSystem = networkSystemIn;
    }

    public void channelRead(ChannelHandlerContext p_channelRead_1_, Object p_channelRead_2_) throws Exception
    {
        ByteBuf lvt_3_1_ = (ByteBuf)p_channelRead_2_;
        lvt_3_1_.markReaderIndex();
        boolean lvt_4_1_ = true;

        try
        {
            if (lvt_3_1_.readUnsignedByte() == 254)
            {
                InetSocketAddress lvt_5_1_ = (InetSocketAddress)p_channelRead_1_.channel().remoteAddress();
                MinecraftServer lvt_6_1_ = this.networkSystem.getServer();
                int lvt_7_1_ = lvt_3_1_.readableBytes();

                switch (lvt_7_1_)
                {
                    case 0:
                        logger.debug("Ping: (<1.3.x) from {}:{}", new Object[] {lvt_5_1_.getAddress(), Integer.valueOf(lvt_5_1_.getPort())});
                        String lvt_8_1_ = String.format("%s\u00a7%d\u00a7%d", new Object[] {lvt_6_1_.getMOTD(), Integer.valueOf(lvt_6_1_.getCurrentPlayerCount()), Integer.valueOf(lvt_6_1_.getMaxPlayers())});
                        this.writeAndFlush(p_channelRead_1_, this.getStringBuffer(lvt_8_1_));
                        break;

                    case 1:
                        if (lvt_3_1_.readUnsignedByte() != 1)
                        {
                            return;
                        }

                        logger.debug("Ping: (1.4-1.5.x) from {}:{}", new Object[] {lvt_5_1_.getAddress(), Integer.valueOf(lvt_5_1_.getPort())});
                        String lvt_8_2_ = String.format("\u00a71\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", new Object[] {Integer.valueOf(127), lvt_6_1_.getMinecraftVersion(), lvt_6_1_.getMOTD(), Integer.valueOf(lvt_6_1_.getCurrentPlayerCount()), Integer.valueOf(lvt_6_1_.getMaxPlayers())});
                        this.writeAndFlush(p_channelRead_1_, this.getStringBuffer(lvt_8_2_));
                        break;

                    default:
                        boolean lvt_8_3_ = lvt_3_1_.readUnsignedByte() == 1;
                        lvt_8_3_ = lvt_8_3_ & lvt_3_1_.readUnsignedByte() == 250;
                        lvt_8_3_ = lvt_8_3_ & "MC|PingHost".equals(new String(lvt_3_1_.readBytes(lvt_3_1_.readShort() * 2).array(), Charsets.UTF_16BE));
                        int lvt_9_1_ = lvt_3_1_.readUnsignedShort();
                        lvt_8_3_ = lvt_8_3_ & lvt_3_1_.readUnsignedByte() >= 73;
                        lvt_8_3_ = lvt_8_3_ & 3 + lvt_3_1_.readBytes(lvt_3_1_.readShort() * 2).array().length + 4 == lvt_9_1_;
                        lvt_8_3_ = lvt_8_3_ & lvt_3_1_.readInt() <= 65535;
                        lvt_8_3_ = lvt_8_3_ & lvt_3_1_.readableBytes() == 0;

                        if (!lvt_8_3_)
                        {
                            return;
                        }

                        logger.debug("Ping: (1.6) from {}:{}", new Object[] {lvt_5_1_.getAddress(), Integer.valueOf(lvt_5_1_.getPort())});
                        String lvt_10_1_ = String.format("\u00a71\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", new Object[] {Integer.valueOf(127), lvt_6_1_.getMinecraftVersion(), lvt_6_1_.getMOTD(), Integer.valueOf(lvt_6_1_.getCurrentPlayerCount()), Integer.valueOf(lvt_6_1_.getMaxPlayers())});
                        ByteBuf lvt_11_1_ = this.getStringBuffer(lvt_10_1_);

                        try
                        {
                            this.writeAndFlush(p_channelRead_1_, lvt_11_1_);
                        }
                        finally
                        {
                            lvt_11_1_.release();
                        }
                }

                lvt_3_1_.release();
                lvt_4_1_ = false;
                return;
            }
        }
        catch (RuntimeException var21)
        {
            return;
        }
        finally
        {
            if (lvt_4_1_)
            {
                lvt_3_1_.resetReaderIndex();
                p_channelRead_1_.channel().pipeline().remove("legacy_query");
                p_channelRead_1_.fireChannelRead(p_channelRead_2_);
            }
        }
    }

    private void writeAndFlush(ChannelHandlerContext ctx, ByteBuf data)
    {
        ctx.pipeline().firstContext().writeAndFlush(data).addListener(ChannelFutureListener.CLOSE);
    }

    private ByteBuf getStringBuffer(String string)
    {
        ByteBuf lvt_2_1_ = Unpooled.buffer();
        lvt_2_1_.writeByte(255);
        char[] lvt_3_1_ = string.toCharArray();
        lvt_2_1_.writeShort(lvt_3_1_.length);

        for (char lvt_7_1_ : lvt_3_1_)
        {
            lvt_2_1_.writeChar(lvt_7_1_);
        }

        return lvt_2_1_;
    }
}
