package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;

public class UserListWhitelistEntry extends UserListEntry<GameProfile>
{
    public UserListWhitelistEntry(GameProfile profile)
    {
        super(profile);
    }

    public UserListWhitelistEntry(JsonObject json)
    {
        super(gameProfileFromJsonObject(json), json);
    }

    protected void onSerialization(JsonObject data)
    {
        if (this.getValue() != null)
        {
            data.addProperty("uuid", ((GameProfile)this.getValue()).getId() == null ? "" : ((GameProfile)this.getValue()).getId().toString());
            data.addProperty("name", ((GameProfile)this.getValue()).getName());
            super.onSerialization(data);
        }
    }

    private static GameProfile gameProfileFromJsonObject(JsonObject json)
    {
        if (json.has("uuid") && json.has("name"))
        {
            String lvt_1_1_ = json.get("uuid").getAsString();
            UUID lvt_2_1_;

            try
            {
                lvt_2_1_ = UUID.fromString(lvt_1_1_);
            }
            catch (Throwable var4)
            {
                return null;
            }

            return new GameProfile(lvt_2_1_, json.get("name").getAsString());
        }
        else
        {
            return null;
        }
    }
}
