package net.minecraft.server.management;

import com.google.gson.JsonObject;
import java.io.File;
import java.net.SocketAddress;

public class BanList extends UserList<String, IPBanEntry>
{
    public BanList(File bansFile)
    {
        super(bansFile);
    }

    protected UserListEntry<String> createEntry(JsonObject entryData)
    {
        return new IPBanEntry(entryData);
    }

    public boolean isBanned(SocketAddress address)
    {
        String lvt_2_1_ = this.addressToString(address);
        return this.hasEntry(lvt_2_1_);
    }

    public IPBanEntry getBanEntry(SocketAddress address)
    {
        String lvt_2_1_ = this.addressToString(address);
        return (IPBanEntry)this.getEntry(lvt_2_1_);
    }

    private String addressToString(SocketAddress address)
    {
        String lvt_2_1_ = address.toString();

        if (lvt_2_1_.contains("/"))
        {
            lvt_2_1_ = lvt_2_1_.substring(lvt_2_1_.indexOf(47) + 1);
        }

        if (lvt_2_1_.contains(":"))
        {
            lvt_2_1_ = lvt_2_1_.substring(0, lvt_2_1_.indexOf(58));
        }

        return lvt_2_1_;
    }
}
