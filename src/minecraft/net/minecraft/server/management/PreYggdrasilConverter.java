package net.minecraft.server.management;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PreYggdrasilConverter
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final File OLD_IPBAN_FILE = new File("banned-ips.txt");
    public static final File OLD_PLAYERBAN_FILE = new File("banned-players.txt");
    public static final File OLD_OPS_FILE = new File("ops.txt");
    public static final File OLD_WHITELIST_FILE = new File("white-list.txt");

    private static void lookupNames(MinecraftServer server, Collection<String> names, ProfileLookupCallback callback)
    {
        String[] lvt_3_1_ = (String[])Iterators.toArray(Iterators.filter(names.iterator(), new Predicate<String>()
        {
            public boolean apply(String p_apply_1_)
            {
                return !StringUtils.isNullOrEmpty(p_apply_1_);
            }
            public boolean apply(Object p_apply_1_)
            {
                return this.apply((String)p_apply_1_);
            }
        }), String.class);

        if (server.isServerInOnlineMode())
        {
            server.getGameProfileRepository().findProfilesByNames(lvt_3_1_, Agent.MINECRAFT, callback);
        }
        else
        {
            for (String lvt_7_1_ : lvt_3_1_)
            {
                UUID lvt_8_1_ = EntityPlayer.getUUID(new GameProfile((UUID)null, lvt_7_1_));
                GameProfile lvt_9_1_ = new GameProfile(lvt_8_1_, lvt_7_1_);
                callback.onProfileLookupSucceeded(lvt_9_1_);
            }
        }
    }

    public static String getStringUUIDFromName(String p_152719_0_)
    {
        if (!StringUtils.isNullOrEmpty(p_152719_0_) && p_152719_0_.length() <= 16)
        {
            final MinecraftServer lvt_1_1_ = MinecraftServer.getServer();
            GameProfile lvt_2_1_ = lvt_1_1_.getPlayerProfileCache().getGameProfileForUsername(p_152719_0_);

            if (lvt_2_1_ != null && lvt_2_1_.getId() != null)
            {
                return lvt_2_1_.getId().toString();
            }
            else if (!lvt_1_1_.isSinglePlayer() && lvt_1_1_.isServerInOnlineMode())
            {
                final List<GameProfile> lvt_3_1_ = Lists.newArrayList();
                ProfileLookupCallback lvt_4_1_ = new ProfileLookupCallback()
                {
                    public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_)
                    {
                        lvt_1_1_.getPlayerProfileCache().addEntry(p_onProfileLookupSucceeded_1_);
                        lvt_3_1_.add(p_onProfileLookupSucceeded_1_);
                    }
                    public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_)
                    {
                        PreYggdrasilConverter.LOGGER.warn("Could not lookup user whitelist entry for " + p_onProfileLookupFailed_1_.getName(), p_onProfileLookupFailed_2_);
                    }
                };
                lookupNames(lvt_1_1_, Lists.newArrayList(new String[] {p_152719_0_}), lvt_4_1_);
                return lvt_3_1_.size() > 0 && ((GameProfile)lvt_3_1_.get(0)).getId() != null ? ((GameProfile)lvt_3_1_.get(0)).getId().toString() : "";
            }
            else
            {
                return EntityPlayer.getUUID(new GameProfile((UUID)null, p_152719_0_)).toString();
            }
        }
        else
        {
            return p_152719_0_;
        }
    }
}
