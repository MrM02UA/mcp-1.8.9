package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.UUID;

public class UserListBansEntry extends BanEntry<GameProfile>
{
    public UserListBansEntry(GameProfile profile)
    {
        this(profile, (Date)null, (String)null, (Date)null, (String)null);
    }

    public UserListBansEntry(GameProfile profile, Date startDate, String banner, Date endDate, String banReason)
    {
        super(profile, endDate, banner, endDate, banReason);
    }

    public UserListBansEntry(JsonObject json)
    {
        super(toGameProfile(json), json);
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

    /**
     * Convert a {@linkplain com.google.gson.JsonObject JsonObject} into a {@linkplain com.mojang.authlib.GameProfile}.
     * The json object must have {@code uuid} and {@code name} attributes or {@code null} will be returned.
     */
    private static GameProfile toGameProfile(JsonObject json)
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
