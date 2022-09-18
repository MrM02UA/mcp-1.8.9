package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerList
{
    private static final Logger logger = LogManager.getLogger();

    /** The Minecraft instance. */
    private final Minecraft mc;
    private final List<ServerData> servers = Lists.newArrayList();

    public ServerList(Minecraft mcIn)
    {
        this.mc = mcIn;
        this.loadServerList();
    }

    /**
     * Loads a list of servers from servers.dat, by running ServerData.getServerDataFromNBTCompound on each NBT compound
     * found in the "servers" tag list.
     */
    public void loadServerList()
    {
        try
        {
            this.servers.clear();
            NBTTagCompound lvt_1_1_ = CompressedStreamTools.read(new File(this.mc.mcDataDir, "servers.dat"));

            if (lvt_1_1_ == null)
            {
                return;
            }

            NBTTagList lvt_2_1_ = lvt_1_1_.getTagList("servers", 10);

            for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.tagCount(); ++lvt_3_1_)
            {
                this.servers.add(ServerData.getServerDataFromNBTCompound(lvt_2_1_.getCompoundTagAt(lvt_3_1_)));
            }
        }
        catch (Exception var4)
        {
            logger.error("Couldn\'t load server list", var4);
        }
    }

    /**
     * Runs getNBTCompound on each ServerData instance, puts everything into a "servers" NBT list and writes it to
     * servers.dat.
     */
    public void saveServerList()
    {
        try
        {
            NBTTagList lvt_1_1_ = new NBTTagList();

            for (ServerData lvt_3_1_ : this.servers)
            {
                lvt_1_1_.appendTag(lvt_3_1_.getNBTCompound());
            }

            NBTTagCompound lvt_2_2_ = new NBTTagCompound();
            lvt_2_2_.setTag("servers", lvt_1_1_);
            CompressedStreamTools.safeWrite(lvt_2_2_, new File(this.mc.mcDataDir, "servers.dat"));
        }
        catch (Exception var4)
        {
            logger.error("Couldn\'t save server list", var4);
        }
    }

    /**
     * Gets the ServerData instance stored for the given index in the list.
     */
    public ServerData getServerData(int index)
    {
        return (ServerData)this.servers.get(index);
    }

    /**
     * Removes the ServerData instance stored for the given index in the list.
     */
    public void removeServerData(int index)
    {
        this.servers.remove(index);
    }

    /**
     * Adds the given ServerData instance to the list.
     */
    public void addServerData(ServerData server)
    {
        this.servers.add(server);
    }

    /**
     * Counts the number of ServerData instances in the list.
     */
    public int countServers()
    {
        return this.servers.size();
    }

    /**
     * Takes two list indexes, and swaps their order around.
     */
    public void swapServers(int p_78857_1_, int p_78857_2_)
    {
        ServerData lvt_3_1_ = this.getServerData(p_78857_1_);
        this.servers.set(p_78857_1_, this.getServerData(p_78857_2_));
        this.servers.set(p_78857_2_, lvt_3_1_);
        this.saveServerList();
    }

    public void func_147413_a(int index, ServerData server)
    {
        this.servers.set(index, server);
    }

    public static void func_147414_b(ServerData p_147414_0_)
    {
        ServerList lvt_1_1_ = new ServerList(Minecraft.getMinecraft());
        lvt_1_1_.loadServerList();

        for (int lvt_2_1_ = 0; lvt_2_1_ < lvt_1_1_.countServers(); ++lvt_2_1_)
        {
            ServerData lvt_3_1_ = lvt_1_1_.getServerData(lvt_2_1_);

            if (lvt_3_1_.serverName.equals(p_147414_0_.serverName) && lvt_3_1_.serverIP.equals(p_147414_0_.serverIP))
            {
                lvt_1_1_.func_147413_a(lvt_2_1_, p_147414_0_);
                break;
            }
        }

        lvt_1_1_.saveServerList();
    }
}
