package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class S27PacketExplosion implements Packet<INetHandlerPlayClient>
{
    private double posX;
    private double posY;
    private double posZ;
    private float strength;
    private List<BlockPos> affectedBlockPositions;
    private float field_149152_f;
    private float field_149153_g;
    private float field_149159_h;

    public S27PacketExplosion()
    {
    }

    public S27PacketExplosion(double p_i45193_1_, double y, double z, float strengthIn, List<BlockPos> affectedBlocksIn, Vec3 p_i45193_9_)
    {
        this.posX = p_i45193_1_;
        this.posY = y;
        this.posZ = z;
        this.strength = strengthIn;
        this.affectedBlockPositions = Lists.newArrayList(affectedBlocksIn);

        if (p_i45193_9_ != null)
        {
            this.field_149152_f = (float)p_i45193_9_.xCoord;
            this.field_149153_g = (float)p_i45193_9_.yCoord;
            this.field_149159_h = (float)p_i45193_9_.zCoord;
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.posX = (double)buf.readFloat();
        this.posY = (double)buf.readFloat();
        this.posZ = (double)buf.readFloat();
        this.strength = buf.readFloat();
        int lvt_2_1_ = buf.readInt();
        this.affectedBlockPositions = Lists.newArrayListWithCapacity(lvt_2_1_);
        int lvt_3_1_ = (int)this.posX;
        int lvt_4_1_ = (int)this.posY;
        int lvt_5_1_ = (int)this.posZ;

        for (int lvt_6_1_ = 0; lvt_6_1_ < lvt_2_1_; ++lvt_6_1_)
        {
            int lvt_7_1_ = buf.readByte() + lvt_3_1_;
            int lvt_8_1_ = buf.readByte() + lvt_4_1_;
            int lvt_9_1_ = buf.readByte() + lvt_5_1_;
            this.affectedBlockPositions.add(new BlockPos(lvt_7_1_, lvt_8_1_, lvt_9_1_));
        }

        this.field_149152_f = buf.readFloat();
        this.field_149153_g = buf.readFloat();
        this.field_149159_h = buf.readFloat();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeFloat((float)this.posX);
        buf.writeFloat((float)this.posY);
        buf.writeFloat((float)this.posZ);
        buf.writeFloat(this.strength);
        buf.writeInt(this.affectedBlockPositions.size());
        int lvt_2_1_ = (int)this.posX;
        int lvt_3_1_ = (int)this.posY;
        int lvt_4_1_ = (int)this.posZ;

        for (BlockPos lvt_6_1_ : this.affectedBlockPositions)
        {
            int lvt_7_1_ = lvt_6_1_.getX() - lvt_2_1_;
            int lvt_8_1_ = lvt_6_1_.getY() - lvt_3_1_;
            int lvt_9_1_ = lvt_6_1_.getZ() - lvt_4_1_;
            buf.writeByte(lvt_7_1_);
            buf.writeByte(lvt_8_1_);
            buf.writeByte(lvt_9_1_);
        }

        buf.writeFloat(this.field_149152_f);
        buf.writeFloat(this.field_149153_g);
        buf.writeFloat(this.field_149159_h);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleExplosion(this);
    }

    public float func_149149_c()
    {
        return this.field_149152_f;
    }

    public float func_149144_d()
    {
        return this.field_149153_g;
    }

    public float func_149147_e()
    {
        return this.field_149159_h;
    }

    public double getX()
    {
        return this.posX;
    }

    public double getY()
    {
        return this.posY;
    }

    public double getZ()
    {
        return this.posZ;
    }

    public float getStrength()
    {
        return this.strength;
    }

    public List<BlockPos> getAffectedBlockPositions()
    {
        return this.affectedBlockPositions;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }
}
