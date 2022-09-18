package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S20PacketEntityProperties implements Packet<INetHandlerPlayClient>
{
    private int entityId;
    private final List<S20PacketEntityProperties.Snapshot> field_149444_b = Lists.newArrayList();

    public S20PacketEntityProperties()
    {
    }

    public S20PacketEntityProperties(int entityIdIn, Collection<IAttributeInstance> p_i45236_2_)
    {
        this.entityId = entityIdIn;

        for (IAttributeInstance lvt_4_1_ : p_i45236_2_)
        {
            this.field_149444_b.add(new S20PacketEntityProperties.Snapshot(lvt_4_1_.getAttribute().getAttributeUnlocalizedName(), lvt_4_1_.getBaseValue(), lvt_4_1_.func_111122_c()));
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.entityId = buf.readVarIntFromBuffer();
        int lvt_2_1_ = buf.readInt();

        for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_; ++lvt_3_1_)
        {
            String lvt_4_1_ = buf.readStringFromBuffer(64);
            double lvt_5_1_ = buf.readDouble();
            List<AttributeModifier> lvt_7_1_ = Lists.newArrayList();
            int lvt_8_1_ = buf.readVarIntFromBuffer();

            for (int lvt_9_1_ = 0; lvt_9_1_ < lvt_8_1_; ++lvt_9_1_)
            {
                UUID lvt_10_1_ = buf.readUuid();
                lvt_7_1_.add(new AttributeModifier(lvt_10_1_, "Unknown synced attribute modifier", buf.readDouble(), buf.readByte()));
            }

            this.field_149444_b.add(new S20PacketEntityProperties.Snapshot(lvt_4_1_, lvt_5_1_, lvt_7_1_));
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarIntToBuffer(this.entityId);
        buf.writeInt(this.field_149444_b.size());

        for (S20PacketEntityProperties.Snapshot lvt_3_1_ : this.field_149444_b)
        {
            buf.writeString(lvt_3_1_.func_151409_a());
            buf.writeDouble(lvt_3_1_.func_151410_b());
            buf.writeVarIntToBuffer(lvt_3_1_.func_151408_c().size());

            for (AttributeModifier lvt_5_1_ : lvt_3_1_.func_151408_c())
            {
                buf.writeUuid(lvt_5_1_.getID());
                buf.writeDouble(lvt_5_1_.getAmount());
                buf.writeByte(lvt_5_1_.getOperation());
            }
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleEntityProperties(this);
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public List<S20PacketEntityProperties.Snapshot> func_149441_d()
    {
        return this.field_149444_b;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }

    public class Snapshot
    {
        private final String field_151412_b;
        private final double field_151413_c;
        private final Collection<AttributeModifier> field_151411_d;

        public Snapshot(String p_i45235_2_, double p_i45235_3_, Collection<AttributeModifier> p_i45235_5_)
        {
            this.field_151412_b = p_i45235_2_;
            this.field_151413_c = p_i45235_3_;
            this.field_151411_d = p_i45235_5_;
        }

        public String func_151409_a()
        {
            return this.field_151412_b;
        }

        public double func_151410_b()
        {
            return this.field_151413_c;
        }

        public Collection<AttributeModifier> func_151408_c()
        {
            return this.field_151411_d;
        }
    }
}
