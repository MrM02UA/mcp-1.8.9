package net.minecraft.stats;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.TupleIntJsonSerializable;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StatisticsFile extends StatFileWriter
{
    private static final Logger logger = LogManager.getLogger();
    private final MinecraftServer mcServer;
    private final File statsFile;
    private final Set<StatBase> field_150888_e = Sets.newHashSet();
    private int field_150885_f = -300;
    private boolean field_150886_g = false;

    public StatisticsFile(MinecraftServer serverIn, File statsFileIn)
    {
        this.mcServer = serverIn;
        this.statsFile = statsFileIn;
    }

    public void readStatFile()
    {
        if (this.statsFile.isFile())
        {
            try
            {
                this.statsData.clear();
                this.statsData.putAll(this.parseJson(FileUtils.readFileToString(this.statsFile)));
            }
            catch (IOException var2)
            {
                logger.error("Couldn\'t read statistics file " + this.statsFile, var2);
            }
            catch (JsonParseException var3)
            {
                logger.error("Couldn\'t parse statistics file " + this.statsFile, var3);
            }
        }
    }

    public void saveStatFile()
    {
        try
        {
            FileUtils.writeStringToFile(this.statsFile, dumpJson(this.statsData));
        }
        catch (IOException var2)
        {
            logger.error("Couldn\'t save stats", var2);
        }
    }

    /**
     * Triggers the logging of an achievement and attempts to announce to server
     */
    public void unlockAchievement(EntityPlayer playerIn, StatBase statIn, int p_150873_3_)
    {
        int lvt_4_1_ = statIn.isAchievement() ? this.readStat(statIn) : 0;
        super.unlockAchievement(playerIn, statIn, p_150873_3_);
        this.field_150888_e.add(statIn);

        if (statIn.isAchievement() && lvt_4_1_ == 0 && p_150873_3_ > 0)
        {
            this.field_150886_g = true;

            if (this.mcServer.isAnnouncingPlayerAchievements())
            {
                this.mcServer.getConfigurationManager().sendChatMsg(new ChatComponentTranslation("chat.type.achievement", new Object[] {playerIn.getDisplayName(), statIn.createChatComponent()}));
            }
        }

        if (statIn.isAchievement() && lvt_4_1_ > 0 && p_150873_3_ == 0)
        {
            this.field_150886_g = true;

            if (this.mcServer.isAnnouncingPlayerAchievements())
            {
                this.mcServer.getConfigurationManager().sendChatMsg(new ChatComponentTranslation("chat.type.achievement.taken", new Object[] {playerIn.getDisplayName(), statIn.createChatComponent()}));
            }
        }
    }

    public Set<StatBase> func_150878_c()
    {
        Set<StatBase> lvt_1_1_ = Sets.newHashSet(this.field_150888_e);
        this.field_150888_e.clear();
        this.field_150886_g = false;
        return lvt_1_1_;
    }

    public Map<StatBase, TupleIntJsonSerializable> parseJson(String p_150881_1_)
    {
        JsonElement lvt_2_1_ = (new JsonParser()).parse(p_150881_1_);

        if (!lvt_2_1_.isJsonObject())
        {
            return Maps.newHashMap();
        }
        else
        {
            JsonObject lvt_3_1_ = lvt_2_1_.getAsJsonObject();
            Map<StatBase, TupleIntJsonSerializable> lvt_4_1_ = Maps.newHashMap();

            for (Entry<String, JsonElement> lvt_6_1_ : lvt_3_1_.entrySet())
            {
                StatBase lvt_7_1_ = StatList.getOneShotStat((String)lvt_6_1_.getKey());

                if (lvt_7_1_ != null)
                {
                    TupleIntJsonSerializable lvt_8_1_ = new TupleIntJsonSerializable();

                    if (((JsonElement)lvt_6_1_.getValue()).isJsonPrimitive() && ((JsonElement)lvt_6_1_.getValue()).getAsJsonPrimitive().isNumber())
                    {
                        lvt_8_1_.setIntegerValue(((JsonElement)lvt_6_1_.getValue()).getAsInt());
                    }
                    else if (((JsonElement)lvt_6_1_.getValue()).isJsonObject())
                    {
                        JsonObject lvt_9_1_ = ((JsonElement)lvt_6_1_.getValue()).getAsJsonObject();

                        if (lvt_9_1_.has("value") && lvt_9_1_.get("value").isJsonPrimitive() && lvt_9_1_.get("value").getAsJsonPrimitive().isNumber())
                        {
                            lvt_8_1_.setIntegerValue(lvt_9_1_.getAsJsonPrimitive("value").getAsInt());
                        }

                        if (lvt_9_1_.has("progress") && lvt_7_1_.func_150954_l() != null)
                        {
                            try
                            {
                                Constructor <? extends IJsonSerializable > lvt_10_1_ = lvt_7_1_.func_150954_l().getConstructor(new Class[0]);
                                IJsonSerializable lvt_11_1_ = (IJsonSerializable)lvt_10_1_.newInstance(new Object[0]);
                                lvt_11_1_.fromJson(lvt_9_1_.get("progress"));
                                lvt_8_1_.setJsonSerializableValue(lvt_11_1_);
                            }
                            catch (Throwable var12)
                            {
                                logger.warn("Invalid statistic progress in " + this.statsFile, var12);
                            }
                        }
                    }

                    lvt_4_1_.put(lvt_7_1_, lvt_8_1_);
                }
                else
                {
                    logger.warn("Invalid statistic in " + this.statsFile + ": Don\'t know what " + (String)lvt_6_1_.getKey() + " is");
                }
            }

            return lvt_4_1_;
        }
    }

    public static String dumpJson(Map<StatBase, TupleIntJsonSerializable> p_150880_0_)
    {
        JsonObject lvt_1_1_ = new JsonObject();

        for (Entry<StatBase, TupleIntJsonSerializable> lvt_3_1_ : p_150880_0_.entrySet())
        {
            if (((TupleIntJsonSerializable)lvt_3_1_.getValue()).getJsonSerializableValue() != null)
            {
                JsonObject lvt_4_1_ = new JsonObject();
                lvt_4_1_.addProperty("value", Integer.valueOf(((TupleIntJsonSerializable)lvt_3_1_.getValue()).getIntegerValue()));

                try
                {
                    lvt_4_1_.add("progress", ((TupleIntJsonSerializable)lvt_3_1_.getValue()).getJsonSerializableValue().getSerializableElement());
                }
                catch (Throwable var6)
                {
                    logger.warn("Couldn\'t save statistic " + ((StatBase)lvt_3_1_.getKey()).getStatName() + ": error serializing progress", var6);
                }

                lvt_1_1_.add(((StatBase)lvt_3_1_.getKey()).statId, lvt_4_1_);
            }
            else
            {
                lvt_1_1_.addProperty(((StatBase)lvt_3_1_.getKey()).statId, Integer.valueOf(((TupleIntJsonSerializable)lvt_3_1_.getValue()).getIntegerValue()));
            }
        }

        return lvt_1_1_.toString();
    }

    public void func_150877_d()
    {
        for (StatBase lvt_2_1_ : this.statsData.keySet())
        {
            this.field_150888_e.add(lvt_2_1_);
        }
    }

    public void func_150876_a(EntityPlayerMP p_150876_1_)
    {
        int lvt_2_1_ = this.mcServer.getTickCounter();
        Map<StatBase, Integer> lvt_3_1_ = Maps.newHashMap();

        if (this.field_150886_g || lvt_2_1_ - this.field_150885_f > 300)
        {
            this.field_150885_f = lvt_2_1_;

            for (StatBase lvt_5_1_ : this.func_150878_c())
            {
                lvt_3_1_.put(lvt_5_1_, Integer.valueOf(this.readStat(lvt_5_1_)));
            }
        }

        p_150876_1_.playerNetServerHandler.sendPacket(new S37PacketStatistics(lvt_3_1_));
    }

    public void sendAchievements(EntityPlayerMP player)
    {
        Map<StatBase, Integer> lvt_2_1_ = Maps.newHashMap();

        for (Achievement lvt_4_1_ : AchievementList.achievementList)
        {
            if (this.hasAchievementUnlocked(lvt_4_1_))
            {
                lvt_2_1_.put(lvt_4_1_, Integer.valueOf(this.readStat(lvt_4_1_)));
                this.field_150888_e.remove(lvt_4_1_);
            }
        }

        player.playerNetServerHandler.sendPacket(new S37PacketStatistics(lvt_2_1_));
    }

    public boolean func_150879_e()
    {
        return this.field_150886_g;
    }
}
