package net.minecraft.command;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

public class PlayerSelector
{
    /**
     * This matches the at-tokens introduced for command blocks, including their arguments, if any.
     */
    private static final Pattern tokenPattern = Pattern.compile("^@([pare])(?:\\[([\\w=,!-]*)\\])?$");

    /**
     * This matches things like "-1,,4", and is used for getting x,y,z,range from the token's argument list.
     */
    private static final Pattern intListPattern = Pattern.compile("\\G([-!]?[\\w-]*)(?:$|,)");

    /**
     * This matches things like "rm=4,c=2" and is used for handling named token arguments.
     */
    private static final Pattern keyValueListPattern = Pattern.compile("\\G(\\w+)=([-!]?[\\w-]*)(?:$|,)");
    private static final Set<String> WORLD_BINDING_ARGS = Sets.newHashSet(new String[] {"x", "y", "z", "dx", "dy", "dz", "rm", "r"});

    /**
     * Returns the one player that matches the given at-token.  Returns null if more than one player matches.
     */
    public static EntityPlayerMP matchOnePlayer(ICommandSender sender, String token)
    {
        return (EntityPlayerMP)matchOneEntity(sender, token, EntityPlayerMP.class);
    }

    public static <T extends Entity> T matchOneEntity(ICommandSender sender, String token, Class <? extends T > targetClass)
    {
        List<T> lvt_3_1_ = matchEntities(sender, token, targetClass);
        return (T)(lvt_3_1_.size() == 1 ? (Entity)lvt_3_1_.get(0) : null);
    }

    public static IChatComponent matchEntitiesToChatComponent(ICommandSender sender, String token)
    {
        List<Entity> lvt_2_1_ = matchEntities(sender, token, Entity.class);

        if (lvt_2_1_.isEmpty())
        {
            return null;
        }
        else
        {
            List<IChatComponent> lvt_3_1_ = Lists.newArrayList();

            for (Entity lvt_5_1_ : lvt_2_1_)
            {
                lvt_3_1_.add(lvt_5_1_.getDisplayName());
            }

            return CommandBase.join(lvt_3_1_);
        }
    }

    public static <T extends Entity> List<T> matchEntities(ICommandSender sender, String token, Class <? extends T > targetClass)
    {
        Matcher lvt_3_1_ = tokenPattern.matcher(token);

        if (lvt_3_1_.matches() && sender.canCommandSenderUseCommand(1, "@"))
        {
            Map<String, String> lvt_4_1_ = getArgumentMap(lvt_3_1_.group(2));

            if (!isEntityTypeValid(sender, lvt_4_1_))
            {
                return Collections.emptyList();
            }
            else
            {
                String lvt_5_1_ = lvt_3_1_.group(1);
                BlockPos lvt_6_1_ = func_179664_b(lvt_4_1_, sender.getPosition());
                List<World> lvt_7_1_ = getWorlds(sender, lvt_4_1_);
                List<T> lvt_8_1_ = Lists.newArrayList();

                for (World lvt_10_1_ : lvt_7_1_)
                {
                    if (lvt_10_1_ != null)
                    {
                        List<Predicate<Entity>> lvt_11_1_ = Lists.newArrayList();
                        lvt_11_1_.addAll(func_179663_a(lvt_4_1_, lvt_5_1_));
                        lvt_11_1_.addAll(getXpLevelPredicates(lvt_4_1_));
                        lvt_11_1_.addAll(getGamemodePredicates(lvt_4_1_));
                        lvt_11_1_.addAll(getTeamPredicates(lvt_4_1_));
                        lvt_11_1_.addAll(getScorePredicates(lvt_4_1_));
                        lvt_11_1_.addAll(getNamePredicates(lvt_4_1_));
                        lvt_11_1_.addAll(func_180698_a(lvt_4_1_, lvt_6_1_));
                        lvt_11_1_.addAll(getRotationsPredicates(lvt_4_1_));
                        lvt_8_1_.addAll(filterResults(lvt_4_1_, targetClass, lvt_11_1_, lvt_5_1_, lvt_10_1_, lvt_6_1_));
                    }
                }

                return func_179658_a(lvt_8_1_, lvt_4_1_, sender, targetClass, lvt_5_1_, lvt_6_1_);
            }
        }
        else
        {
            return Collections.emptyList();
        }
    }

    private static List<World> getWorlds(ICommandSender sender, Map<String, String> argumentMap)
    {
        List<World> lvt_2_1_ = Lists.newArrayList();

        if (func_179665_h(argumentMap))
        {
            lvt_2_1_.add(sender.getEntityWorld());
        }
        else
        {
            Collections.addAll(lvt_2_1_, MinecraftServer.getServer().worldServers);
        }

        return lvt_2_1_;
    }

    private static <T extends Entity> boolean isEntityTypeValid(ICommandSender commandSender, Map<String, String> params)
    {
        String lvt_2_1_ = func_179651_b(params, "type");
        lvt_2_1_ = lvt_2_1_ != null && lvt_2_1_.startsWith("!") ? lvt_2_1_.substring(1) : lvt_2_1_;

        if (lvt_2_1_ != null && !EntityList.isStringValidEntityName(lvt_2_1_))
        {
            ChatComponentTranslation lvt_3_1_ = new ChatComponentTranslation("commands.generic.entity.invalidType", new Object[] {lvt_2_1_});
            lvt_3_1_.getChatStyle().setColor(EnumChatFormatting.RED);
            commandSender.addChatMessage(lvt_3_1_);
            return false;
        }
        else
        {
            return true;
        }
    }

    private static List<Predicate<Entity>> func_179663_a(Map<String, String> p_179663_0_, String p_179663_1_)
    {
        List<Predicate<Entity>> lvt_2_1_ = Lists.newArrayList();
        final String lvt_3_1_ = func_179651_b(p_179663_0_, "type");
        final boolean lvt_4_1_ = lvt_3_1_ != null && lvt_3_1_.startsWith("!");

        if (lvt_4_1_)
        {
            lvt_3_1_ = lvt_3_1_.substring(1);
        }

        boolean lvt_6_1_ = !p_179663_1_.equals("e");
        boolean lvt_7_1_ = p_179663_1_.equals("r") && lvt_3_1_ != null;

        if ((lvt_3_1_ == null || !p_179663_1_.equals("e")) && !lvt_7_1_)
        {
            if (lvt_6_1_)
            {
                lvt_2_1_.add(new Predicate<Entity>()
                {
                    public boolean apply(Entity p_apply_1_)
                    {
                        return p_apply_1_ instanceof EntityPlayer;
                    }
                    public boolean apply(Object p_apply_1_)
                    {
                        return this.apply((Entity)p_apply_1_);
                    }
                });
            }
        }
        else
        {
            lvt_2_1_.add(new Predicate<Entity>()
            {
                public boolean apply(Entity p_apply_1_)
                {
                    return EntityList.isStringEntityName(p_apply_1_, lvt_3_1_) != lvt_4_1_;
                }
                public boolean apply(Object p_apply_1_)
                {
                    return this.apply((Entity)p_apply_1_);
                }
            });
        }

        return lvt_2_1_;
    }

    private static List<Predicate<Entity>> getXpLevelPredicates(Map<String, String> p_179648_0_)
    {
        List<Predicate<Entity>> lvt_1_1_ = Lists.newArrayList();
        final int lvt_2_1_ = parseIntWithDefault(p_179648_0_, "lm", -1);
        final int lvt_3_1_ = parseIntWithDefault(p_179648_0_, "l", -1);

        if (lvt_2_1_ > -1 || lvt_3_1_ > -1)
        {
            lvt_1_1_.add(new Predicate<Entity>()
            {
                public boolean apply(Entity p_apply_1_)
                {
                    if (!(p_apply_1_ instanceof EntityPlayerMP))
                    {
                        return false;
                    }
                    else
                    {
                        EntityPlayerMP lvt_2_1_ = (EntityPlayerMP)p_apply_1_;
                        return (lvt_2_1_ <= -1 || lvt_2_1_.experienceLevel >= lvt_2_1_) && (lvt_3_1_ <= -1 || lvt_2_1_.experienceLevel <= lvt_3_1_);
                    }
                }
                public boolean apply(Object p_apply_1_)
                {
                    return this.apply((Entity)p_apply_1_);
                }
            });
        }

        return lvt_1_1_;
    }

    private static List<Predicate<Entity>> getGamemodePredicates(Map<String, String> p_179649_0_)
    {
        List<Predicate<Entity>> lvt_1_1_ = Lists.newArrayList();
        final int lvt_2_1_ = parseIntWithDefault(p_179649_0_, "m", WorldSettings.GameType.NOT_SET.getID());

        if (lvt_2_1_ != WorldSettings.GameType.NOT_SET.getID())
        {
            lvt_1_1_.add(new Predicate<Entity>()
            {
                public boolean apply(Entity p_apply_1_)
                {
                    if (!(p_apply_1_ instanceof EntityPlayerMP))
                    {
                        return false;
                    }
                    else
                    {
                        EntityPlayerMP lvt_2_1_ = (EntityPlayerMP)p_apply_1_;
                        return lvt_2_1_.theItemInWorldManager.getGameType().getID() == lvt_2_1_;
                    }
                }
                public boolean apply(Object p_apply_1_)
                {
                    return this.apply((Entity)p_apply_1_);
                }
            });
        }

        return lvt_1_1_;
    }

    private static List<Predicate<Entity>> getTeamPredicates(Map<String, String> p_179659_0_)
    {
        List<Predicate<Entity>> lvt_1_1_ = Lists.newArrayList();
        final String lvt_2_1_ = func_179651_b(p_179659_0_, "team");
        final boolean lvt_3_1_ = lvt_2_1_ != null && lvt_2_1_.startsWith("!");

        if (lvt_3_1_)
        {
            lvt_2_1_ = lvt_2_1_.substring(1);
        }

        if (lvt_2_1_ != null)
        {
            lvt_1_1_.add(new Predicate<Entity>()
            {
                public boolean apply(Entity p_apply_1_)
                {
                    if (!(p_apply_1_ instanceof EntityLivingBase))
                    {
                        return false;
                    }
                    else
                    {
                        EntityLivingBase lvt_2_1_ = (EntityLivingBase)p_apply_1_;
                        Team lvt_3_1_ = lvt_2_1_.getTeam();
                        String lvt_4_1_ = lvt_3_1_ == null ? "" : lvt_3_1_.getRegisteredName();
                        return lvt_4_1_.equals(lvt_2_1_) != lvt_3_1_;
                    }
                }
                public boolean apply(Object p_apply_1_)
                {
                    return this.apply((Entity)p_apply_1_);
                }
            });
        }

        return lvt_1_1_;
    }

    private static List<Predicate<Entity>> getScorePredicates(Map<String, String> p_179657_0_)
    {
        List<Predicate<Entity>> lvt_1_1_ = Lists.newArrayList();
        final Map<String, Integer> lvt_2_1_ = func_96560_a(p_179657_0_);

        if (lvt_2_1_ != null && lvt_2_1_.size() > 0)
        {
            lvt_1_1_.add(new Predicate<Entity>()
            {
                public boolean apply(Entity p_apply_1_)
                {
                    Scoreboard lvt_2_1_ = MinecraftServer.getServer().worldServerForDimension(0).getScoreboard();

                    for (Entry<String, Integer> lvt_4_1_ : lvt_2_1_.entrySet())
                    {
                        String lvt_5_1_ = (String)lvt_4_1_.getKey();
                        boolean lvt_6_1_ = false;

                        if (lvt_5_1_.endsWith("_min") && lvt_5_1_.length() > 4)
                        {
                            lvt_6_1_ = true;
                            lvt_5_1_ = lvt_5_1_.substring(0, lvt_5_1_.length() - 4);
                        }

                        ScoreObjective lvt_7_1_ = lvt_2_1_.getObjective(lvt_5_1_);

                        if (lvt_7_1_ == null)
                        {
                            return false;
                        }

                        String lvt_8_1_ = p_apply_1_ instanceof EntityPlayerMP ? p_apply_1_.getName() : p_apply_1_.getUniqueID().toString();

                        if (!lvt_2_1_.entityHasObjective(lvt_8_1_, lvt_7_1_))
                        {
                            return false;
                        }

                        Score lvt_9_1_ = lvt_2_1_.getValueFromObjective(lvt_8_1_, lvt_7_1_);
                        int lvt_10_1_ = lvt_9_1_.getScorePoints();

                        if (lvt_10_1_ < ((Integer)lvt_4_1_.getValue()).intValue() && lvt_6_1_)
                        {
                            return false;
                        }

                        if (lvt_10_1_ > ((Integer)lvt_4_1_.getValue()).intValue() && !lvt_6_1_)
                        {
                            return false;
                        }
                    }

                    return true;
                }
                public boolean apply(Object p_apply_1_)
                {
                    return this.apply((Entity)p_apply_1_);
                }
            });
        }

        return lvt_1_1_;
    }

    private static List<Predicate<Entity>> getNamePredicates(Map<String, String> p_179647_0_)
    {
        List<Predicate<Entity>> lvt_1_1_ = Lists.newArrayList();
        final String lvt_2_1_ = func_179651_b(p_179647_0_, "name");
        final boolean lvt_3_1_ = lvt_2_1_ != null && lvt_2_1_.startsWith("!");

        if (lvt_3_1_)
        {
            lvt_2_1_ = lvt_2_1_.substring(1);
        }

        if (lvt_2_1_ != null)
        {
            lvt_1_1_.add(new Predicate<Entity>()
            {
                public boolean apply(Entity p_apply_1_)
                {
                    return p_apply_1_.getName().equals(lvt_2_1_) != lvt_3_1_;
                }
                public boolean apply(Object p_apply_1_)
                {
                    return this.apply((Entity)p_apply_1_);
                }
            });
        }

        return lvt_1_1_;
    }

    private static List<Predicate<Entity>> func_180698_a(Map<String, String> p_180698_0_, final BlockPos p_180698_1_)
    {
        List<Predicate<Entity>> lvt_2_1_ = Lists.newArrayList();
        final int lvt_3_1_ = parseIntWithDefault(p_180698_0_, "rm", -1);
        final int lvt_4_1_ = parseIntWithDefault(p_180698_0_, "r", -1);

        if (p_180698_1_ != null && (lvt_3_1_ >= 0 || lvt_4_1_ >= 0))
        {
            final int lvt_5_1_ = lvt_3_1_ * lvt_3_1_;
            final int lvt_6_1_ = lvt_4_1_ * lvt_4_1_;
            lvt_2_1_.add(new Predicate<Entity>()
            {
                public boolean apply(Entity p_apply_1_)
                {
                    int lvt_2_1_ = (int)p_apply_1_.getDistanceSqToCenter(p_180698_1_);
                    return (lvt_3_1_ < 0 || lvt_2_1_ >= lvt_5_1_) && (lvt_4_1_ < 0 || lvt_2_1_ <= lvt_6_1_);
                }
                public boolean apply(Object p_apply_1_)
                {
                    return this.apply((Entity)p_apply_1_);
                }
            });
        }

        return lvt_2_1_;
    }

    private static List<Predicate<Entity>> getRotationsPredicates(Map<String, String> p_179662_0_)
    {
        List<Predicate<Entity>> lvt_1_1_ = Lists.newArrayList();

        if (p_179662_0_.containsKey("rym") || p_179662_0_.containsKey("ry"))
        {
            final int lvt_2_1_ = func_179650_a(parseIntWithDefault(p_179662_0_, "rym", 0));
            final int lvt_3_1_ = func_179650_a(parseIntWithDefault(p_179662_0_, "ry", 359));
            lvt_1_1_.add(new Predicate<Entity>()
            {
                public boolean apply(Entity p_apply_1_)
                {
                    int lvt_2_1_ = PlayerSelector.func_179650_a((int)Math.floor((double)p_apply_1_.rotationYaw));
                    return lvt_2_1_ > lvt_3_1_ ? lvt_2_1_ >= lvt_2_1_ || lvt_2_1_ <= lvt_3_1_ : lvt_2_1_ >= lvt_2_1_ && lvt_2_1_ <= lvt_3_1_;
                }
                public boolean apply(Object p_apply_1_)
                {
                    return this.apply((Entity)p_apply_1_);
                }
            });
        }

        if (p_179662_0_.containsKey("rxm") || p_179662_0_.containsKey("rx"))
        {
            final int lvt_2_2_ = func_179650_a(parseIntWithDefault(p_179662_0_, "rxm", 0));
            final int lvt_3_2_ = func_179650_a(parseIntWithDefault(p_179662_0_, "rx", 359));
            lvt_1_1_.add(new Predicate<Entity>()
            {
                public boolean apply(Entity p_apply_1_)
                {
                    int lvt_2_1_ = PlayerSelector.func_179650_a((int)Math.floor((double)p_apply_1_.rotationPitch));
                    return lvt_2_2_ > lvt_3_2_ ? lvt_2_1_ >= lvt_2_2_ || lvt_2_1_ <= lvt_3_2_ : lvt_2_1_ >= lvt_2_2_ && lvt_2_1_ <= lvt_3_2_;
                }
                public boolean apply(Object p_apply_1_)
                {
                    return this.apply((Entity)p_apply_1_);
                }
            });
        }

        return lvt_1_1_;
    }

    private static <T extends Entity> List<T> filterResults(Map<String, String> params, Class <? extends T > entityClass, List<Predicate<Entity>> inputList, String type, World worldIn, BlockPos position)
    {
        List<T> lvt_6_1_ = Lists.newArrayList();
        String lvt_7_1_ = func_179651_b(params, "type");
        lvt_7_1_ = lvt_7_1_ != null && lvt_7_1_.startsWith("!") ? lvt_7_1_.substring(1) : lvt_7_1_;
        boolean lvt_8_1_ = !type.equals("e");
        boolean lvt_9_1_ = type.equals("r") && lvt_7_1_ != null;
        int lvt_10_1_ = parseIntWithDefault(params, "dx", 0);
        int lvt_11_1_ = parseIntWithDefault(params, "dy", 0);
        int lvt_12_1_ = parseIntWithDefault(params, "dz", 0);
        int lvt_13_1_ = parseIntWithDefault(params, "r", -1);
        Predicate<Entity> lvt_14_1_ = Predicates.and(inputList);
        Predicate<Entity> lvt_15_1_ = Predicates.and(EntitySelectors.selectAnything, lvt_14_1_);

        if (position != null)
        {
            int lvt_16_1_ = worldIn.playerEntities.size();
            int lvt_17_1_ = worldIn.loadedEntityList.size();
            boolean lvt_18_1_ = lvt_16_1_ < lvt_17_1_ / 16;

            if (!params.containsKey("dx") && !params.containsKey("dy") && !params.containsKey("dz"))
            {
                if (lvt_13_1_ >= 0)
                {
                    AxisAlignedBB lvt_19_2_ = new AxisAlignedBB((double)(position.getX() - lvt_13_1_), (double)(position.getY() - lvt_13_1_), (double)(position.getZ() - lvt_13_1_), (double)(position.getX() + lvt_13_1_ + 1), (double)(position.getY() + lvt_13_1_ + 1), (double)(position.getZ() + lvt_13_1_ + 1));

                    if (lvt_8_1_ && lvt_18_1_ && !lvt_9_1_)
                    {
                        lvt_6_1_.addAll(worldIn.getPlayers(entityClass, lvt_15_1_));
                    }
                    else
                    {
                        lvt_6_1_.addAll(worldIn.getEntitiesWithinAABB(entityClass, lvt_19_2_, lvt_15_1_));
                    }
                }
                else if (type.equals("a"))
                {
                    lvt_6_1_.addAll(worldIn.getPlayers(entityClass, lvt_14_1_));
                }
                else if (!type.equals("p") && (!type.equals("r") || lvt_9_1_))
                {
                    lvt_6_1_.addAll(worldIn.getEntities(entityClass, lvt_15_1_));
                }
                else
                {
                    lvt_6_1_.addAll(worldIn.getPlayers(entityClass, lvt_15_1_));
                }
            }
            else
            {
                final AxisAlignedBB lvt_19_1_ = func_179661_a(position, lvt_10_1_, lvt_11_1_, lvt_12_1_);

                if (lvt_8_1_ && lvt_18_1_ && !lvt_9_1_)
                {
                    Predicate<Entity> lvt_20_1_ = new Predicate<Entity>()
                    {
                        public boolean apply(Entity p_apply_1_)
                        {
                            return p_apply_1_.posX >= lvt_19_1_.minX && p_apply_1_.posY >= lvt_19_1_.minY && p_apply_1_.posZ >= lvt_19_1_.minZ ? p_apply_1_.posX < lvt_19_1_.maxX && p_apply_1_.posY < lvt_19_1_.maxY && p_apply_1_.posZ < lvt_19_1_.maxZ : false;
                        }
                        public boolean apply(Object p_apply_1_)
                        {
                            return this.apply((Entity)p_apply_1_);
                        }
                    };
                    lvt_6_1_.addAll(worldIn.getPlayers(entityClass, Predicates.and(lvt_15_1_, lvt_20_1_)));
                }
                else
                {
                    lvt_6_1_.addAll(worldIn.getEntitiesWithinAABB(entityClass, lvt_19_1_, lvt_15_1_));
                }
            }
        }
        else if (type.equals("a"))
        {
            lvt_6_1_.addAll(worldIn.getPlayers(entityClass, lvt_14_1_));
        }
        else if (!type.equals("p") && (!type.equals("r") || lvt_9_1_))
        {
            lvt_6_1_.addAll(worldIn.getEntities(entityClass, lvt_15_1_));
        }
        else
        {
            lvt_6_1_.addAll(worldIn.getPlayers(entityClass, lvt_15_1_));
        }

        return lvt_6_1_;
    }

    private static <T extends Entity> List<T> func_179658_a(List<T> p_179658_0_, Map<String, String> p_179658_1_, ICommandSender p_179658_2_, Class <? extends T > p_179658_3_, String p_179658_4_, final BlockPos p_179658_5_)
    {
        int lvt_6_1_ = parseIntWithDefault(p_179658_1_, "c", !p_179658_4_.equals("a") && !p_179658_4_.equals("e") ? 1 : 0);

        if (!p_179658_4_.equals("p") && !p_179658_4_.equals("a") && !p_179658_4_.equals("e"))
        {
            if (p_179658_4_.equals("r"))
            {
                Collections.shuffle((List)p_179658_0_);
            }
        }
        else if (p_179658_5_ != null)
        {
            Collections.sort((List)p_179658_0_, new Comparator<Entity>()
            {
                public int compare(Entity p_compare_1_, Entity p_compare_2_)
                {
                    return ComparisonChain.start().compare(p_compare_1_.getDistanceSq(p_179658_5_), p_compare_2_.getDistanceSq(p_179658_5_)).result();
                }
                public int compare(Object p_compare_1_, Object p_compare_2_)
                {
                    return this.compare((Entity)p_compare_1_, (Entity)p_compare_2_);
                }
            });
        }

        Entity lvt_7_1_ = p_179658_2_.getCommandSenderEntity();

        if (lvt_7_1_ != null && p_179658_3_.isAssignableFrom(lvt_7_1_.getClass()) && lvt_6_1_ == 1 && ((List)p_179658_0_).contains(lvt_7_1_) && !"r".equals(p_179658_4_))
        {
            p_179658_0_ = Lists.newArrayList(new Entity[] {lvt_7_1_});
        }

        if (lvt_6_1_ != 0)
        {
            if (lvt_6_1_ < 0)
            {
                Collections.reverse((List)p_179658_0_);
            }

            p_179658_0_ = ((List)p_179658_0_).subList(0, Math.min(Math.abs(lvt_6_1_), ((List)p_179658_0_).size()));
        }

        return (List)p_179658_0_;
    }

    private static AxisAlignedBB func_179661_a(BlockPos p_179661_0_, int p_179661_1_, int p_179661_2_, int p_179661_3_)
    {
        boolean lvt_4_1_ = p_179661_1_ < 0;
        boolean lvt_5_1_ = p_179661_2_ < 0;
        boolean lvt_6_1_ = p_179661_3_ < 0;
        int lvt_7_1_ = p_179661_0_.getX() + (lvt_4_1_ ? p_179661_1_ : 0);
        int lvt_8_1_ = p_179661_0_.getY() + (lvt_5_1_ ? p_179661_2_ : 0);
        int lvt_9_1_ = p_179661_0_.getZ() + (lvt_6_1_ ? p_179661_3_ : 0);
        int lvt_10_1_ = p_179661_0_.getX() + (lvt_4_1_ ? 0 : p_179661_1_) + 1;
        int lvt_11_1_ = p_179661_0_.getY() + (lvt_5_1_ ? 0 : p_179661_2_) + 1;
        int lvt_12_1_ = p_179661_0_.getZ() + (lvt_6_1_ ? 0 : p_179661_3_) + 1;
        return new AxisAlignedBB((double)lvt_7_1_, (double)lvt_8_1_, (double)lvt_9_1_, (double)lvt_10_1_, (double)lvt_11_1_, (double)lvt_12_1_);
    }

    public static int func_179650_a(int p_179650_0_)
    {
        p_179650_0_ = p_179650_0_ % 360;

        if (p_179650_0_ >= 160)
        {
            p_179650_0_ -= 360;
        }

        if (p_179650_0_ < 0)
        {
            p_179650_0_ += 360;
        }

        return p_179650_0_;
    }

    private static BlockPos func_179664_b(Map<String, String> p_179664_0_, BlockPos p_179664_1_)
    {
        return new BlockPos(parseIntWithDefault(p_179664_0_, "x", p_179664_1_.getX()), parseIntWithDefault(p_179664_0_, "y", p_179664_1_.getY()), parseIntWithDefault(p_179664_0_, "z", p_179664_1_.getZ()));
    }

    private static boolean func_179665_h(Map<String, String> p_179665_0_)
    {
        for (String lvt_2_1_ : WORLD_BINDING_ARGS)
        {
            if (p_179665_0_.containsKey(lvt_2_1_))
            {
                return true;
            }
        }

        return false;
    }

    private static int parseIntWithDefault(Map<String, String> p_179653_0_, String p_179653_1_, int p_179653_2_)
    {
        return p_179653_0_.containsKey(p_179653_1_) ? MathHelper.parseIntWithDefault((String)p_179653_0_.get(p_179653_1_), p_179653_2_) : p_179653_2_;
    }

    private static String func_179651_b(Map<String, String> p_179651_0_, String p_179651_1_)
    {
        return (String)p_179651_0_.get(p_179651_1_);
    }

    public static Map<String, Integer> func_96560_a(Map<String, String> p_96560_0_)
    {
        Map<String, Integer> lvt_1_1_ = Maps.newHashMap();

        for (String lvt_3_1_ : p_96560_0_.keySet())
        {
            if (lvt_3_1_.startsWith("score_") && lvt_3_1_.length() > "score_".length())
            {
                lvt_1_1_.put(lvt_3_1_.substring("score_".length()), Integer.valueOf(MathHelper.parseIntWithDefault((String)p_96560_0_.get(lvt_3_1_), 1)));
            }
        }

        return lvt_1_1_;
    }

    /**
     * Returns whether the given pattern can match more than one player.
     */
    public static boolean matchesMultiplePlayers(String p_82377_0_)
    {
        Matcher lvt_1_1_ = tokenPattern.matcher(p_82377_0_);

        if (!lvt_1_1_.matches())
        {
            return false;
        }
        else
        {
            Map<String, String> lvt_2_1_ = getArgumentMap(lvt_1_1_.group(2));
            String lvt_3_1_ = lvt_1_1_.group(1);
            int lvt_4_1_ = !"a".equals(lvt_3_1_) && !"e".equals(lvt_3_1_) ? 1 : 0;
            return parseIntWithDefault(lvt_2_1_, "c", lvt_4_1_) != 1;
        }
    }

    /**
     * Returns whether the given token has any arguments set.
     */
    public static boolean hasArguments(String p_82378_0_)
    {
        return tokenPattern.matcher(p_82378_0_).matches();
    }

    private static Map<String, String> getArgumentMap(String argumentString)
    {
        Map<String, String> lvt_1_1_ = Maps.newHashMap();

        if (argumentString == null)
        {
            return lvt_1_1_;
        }
        else
        {
            int lvt_2_1_ = 0;
            int lvt_3_1_ = -1;

            for (Matcher lvt_4_1_ = intListPattern.matcher(argumentString); lvt_4_1_.find(); lvt_3_1_ = lvt_4_1_.end())
            {
                String lvt_5_1_ = null;

                switch (lvt_2_1_++)
                {
                    case 0:
                        lvt_5_1_ = "x";
                        break;

                    case 1:
                        lvt_5_1_ = "y";
                        break;

                    case 2:
                        lvt_5_1_ = "z";
                        break;

                    case 3:
                        lvt_5_1_ = "r";
                }

                if (lvt_5_1_ != null && lvt_4_1_.group(1).length() > 0)
                {
                    lvt_1_1_.put(lvt_5_1_, lvt_4_1_.group(1));
                }
            }

            if (lvt_3_1_ < argumentString.length())
            {
                Matcher lvt_5_2_ = keyValueListPattern.matcher(lvt_3_1_ == -1 ? argumentString : argumentString.substring(lvt_3_1_));

                while (lvt_5_2_.find())
                {
                    lvt_1_1_.put(lvt_5_2_.group(1), lvt_5_2_.group(2));
                }
            }

            return lvt_1_1_;
        }
    }
}
