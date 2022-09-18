package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class UserListOps extends UserList<GameProfile, UserListOpsEntry>
{
    public UserListOps(File saveFile)
    {
        super(saveFile);
    }

    protected UserListEntry<GameProfile> createEntry(JsonObject entryData)
    {
        return new UserListOpsEntry(entryData);
    }

    public String[] getKeys()
    {
        String[] lvt_1_1_ = new String[this.getValues().size()];
        int lvt_2_1_ = 0;

        for (UserListOpsEntry lvt_4_1_ : this.getValues().values())
        {
            lvt_1_1_[lvt_2_1_++] = ((GameProfile)lvt_4_1_.getValue()).getName();
        }

        return lvt_1_1_;
    }

    public boolean bypassesPlayerLimit(GameProfile profile)
    {
        UserListOpsEntry lvt_2_1_ = (UserListOpsEntry)this.getEntry(profile);
        return lvt_2_1_ != null ? lvt_2_1_.bypassesPlayerLimit() : false;
    }

    /**
     * Gets the key value for the given object
     */
    protected String getObjectKey(GameProfile obj)
    {
        return obj.getId().toString();
    }

    /**
     * Gets the GameProfile of based on the provided username.
     */
    public GameProfile getGameProfileFromName(String username)
    {
        for (UserListOpsEntry lvt_3_1_ : this.getValues().values())
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
