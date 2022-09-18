package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class UserListWhitelist extends UserList<GameProfile, UserListWhitelistEntry>
{
    public UserListWhitelist(File p_i1132_1_)
    {
        super(p_i1132_1_);
    }

    protected UserListEntry<GameProfile> createEntry(JsonObject entryData)
    {
        return new UserListWhitelistEntry(entryData);
    }

    public String[] getKeys()
    {
        String[] lvt_1_1_ = new String[this.getValues().size()];
        int lvt_2_1_ = 0;

        for (UserListWhitelistEntry lvt_4_1_ : this.getValues().values())
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

    /**
     * Gets the GameProfile for the UserListBanEntry with the specified username, if present
     */
    public GameProfile getBannedProfile(String name)
    {
        for (UserListWhitelistEntry lvt_3_1_ : this.getValues().values())
        {
            if (name.equalsIgnoreCase(((GameProfile)lvt_3_1_.getValue()).getName()))
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
