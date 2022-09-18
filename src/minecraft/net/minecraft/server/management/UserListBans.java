package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class UserListBans extends UserList<GameProfile, UserListBansEntry>
{
    public UserListBans(File bansFile)
    {
        super(bansFile);
    }

    protected UserListEntry<GameProfile> createEntry(JsonObject entryData)
    {
        return new UserListBansEntry(entryData);
    }

    public boolean isBanned(GameProfile profile)
    {
        return this.hasEntry(profile);
    }

    public String[] getKeys()
    {
        String[] lvt_1_1_ = new String[this.getValues().size()];
        int lvt_2_1_ = 0;

        for (UserListBansEntry lvt_4_1_ : this.getValues().values())
        {
            lvt_1_1_[lvt_2_1_++] = ((GameProfile)lvt_4_1_.getValue()).getName();
        }

        return lvt_1_1_;
    }

    /**
     * Gets the key value for the given object
     */
    protected String getObjectKey(GameProfile obj)
    {
        return obj.getId().toString();
    }

    public GameProfile isUsernameBanned(String username)
    {
        for (UserListBansEntry lvt_3_1_ : this.getValues().values())
        {
            if (username.equalsIgnoreCase(((GameProfile)lvt_3_1_.getValue()).getName()))
            {
                return (GameProfile)lvt_3_1_.getValue();
            }
        }

        return null;
    }

    /**
     * Gets the key value for the given object
     */
    protected String getObjectKey(Object obj)
    {
        return this.getObjectKey((GameProfile)obj);
    }
}
