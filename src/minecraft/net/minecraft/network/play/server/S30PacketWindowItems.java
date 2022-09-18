package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S30PacketWindowItems implements Packet<INetHandlerPlayClient>
{
    private int windowId;
    private ItemStack[] itemStacks;

    public S30PacketWindowItems()
    {
    }

    public S30PacketWindowItems(int windowIdIn, List<ItemStack> p_i45186_2_)
    {
        this.windowId = windowIdIn;
        this.itemStacks = new ItemStack[p_i45186_2_.size()];

        for (int lvt_3_1_ = 0; lvt_3_1_ < this.itemStacks.length; ++lvt_3_1_)
        {
            ItemStack lvt_4_1_ = (ItemStack)p_i45186_2_.get(lvt_3_1_);
            this.itemStacks[lvt_3_1_] = lvt_4_1_ == null ? null : lvt_4_1_.copy();
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.windowId = buf.readUnsignedByte();
        int lvt_2_1_ = buf.readShort();
        this.itemStacks = new ItemStack[lvt_2_1_];

        for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_; ++lvt_3_1_)
        {
            this.itemStacks[lvt_3_1_] = buf.readItemStackFromBuffer();
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeByte(this.windowId);
        buf.writeShort(this.itemStacks.length);

        for (ItemStack lvt_5_1_ : this.itemStacks)
        {
            buf.writeItemStackToBuffer(lvt_5_1_);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleWindowItems(this);
    }

    public int func_148911_c()
    {
        return this.windowId;
    }

    public ItemStack[] getItemStacks()
    {
        return this.itemStacks;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandler handler)
    {
        this.processPacket((INetHandlerPlayClient)handler);
    }
}
