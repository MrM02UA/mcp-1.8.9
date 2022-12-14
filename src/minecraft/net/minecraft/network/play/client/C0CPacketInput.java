package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0CPacketInput implements Packet<INetHandlerPlayServer>
{
    /** Positive for left strafe, negative for right */
    private float strafeSpeed;

    /** Positive for forward, negative for backward */
    private float forwardSpeed;
    private boolean jumping;
    private boolean sneaking;

    public C0CPacketInput()
    {
    }

    public C0CPacketInput(float strafeSpeed, float forwardSpeed, boolean jumping, boolean sneaking)
    {
        this.strafeSpeed = strafeSpeed;
        this.forwardSpeed = forwardSpeed;
        this.jumping = jumping;
        this.sneaking = sneaking;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.strafeSpeed = buf.readFloat();
        this.forwardSpeed = buf.readFloat();
        byte lvt_2_1_ = buf.readByte();
        this.jumping = (lvt_2_1_ & 1) > 0;
        this.sneaking = (lvt_2_1_ & 2) > 0;
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeFloat(this.strafeSpeed);
        buf.writeFloat(this.forwardSpeed);
        byte lvt_2_1_ = 0;

        if (this.jumping)
        {
            lvt_2_1_ = (byte)(lvt_2_1_ | 1);
        }

        if (this.sneaking)
        {
            lvt_2_1_ = (byte)(lvt_2_1_ | 2);
        }

        buf.writeByte(lvt_2_1_);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processInput(this);
    }

    public float getStrafeSpeed()
    {
        return this.strafeSpeed;
    }

    public float getForwardSpeed()
    {
        return this.forwardSpeed;
    }

    public boolean isJumping()
    {
        return this.jumping;
    }

    public boolean isSneaking()
    {
        return this.sneaking;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayServer)handler);
    }
}
