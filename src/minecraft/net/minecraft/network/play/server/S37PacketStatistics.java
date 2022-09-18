package net.minecraft.network.play.server;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;

public class S37PacketStatistics implements Packet<INetHandlerPlayClient>
{
    private Map<StatBase, Integer> field_148976_a;

    public S37PacketStatistics()
    {
    }

    public S37PacketStatistics(Map<StatBase, Integer> p_i45173_1_)
    {
        this.field_148976_a = p_i45173_1_;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleStatistics(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        int lvt_2_1_ = buf.readVarIntFromBuffer();
        this.field_148976_a = Maps.newHashMap();

        for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_; ++lvt_3_1_)
        {
            StatBase lvt_4_1_ = StatList.getOneShotStat(buf.readStringFromBuffer(32767));
            int lvt_5_1_ = buf.readVarIntFromBuffer();

            if (lvt_4_1_ != null)
            {
                this.field_148976_a.put(lvt_4_1_, Integer.valueOf(lvt_5_1_));
            }
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarIntToBuffer(this.field_148976_a.size());

        for (Entry<StatBase, Integer> lvt_3_1_ : this.field_148976_a.entrySet())
        {
            buf.writeString(((StatBase)lvt_3_1_.getKey()).statId);
            buf.writeVarIntToBuffer(((Integer)lvt_3_1_.getValue()).intValue());
        }
    }

    public Map<StatBase, Integer> func_148974_c()
    {
        return this.field_148976_a;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }
}
