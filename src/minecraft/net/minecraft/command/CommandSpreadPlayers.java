package net.minecraft.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class CommandSpreadPlayers extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "spreadplayers";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    /**
     * Gets the usage string for the command.
     */
    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.spreadplayers.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 6)
        {
            throw new WrongUsageException("commands.spreadplayers.usage", new Object[0]);
        }
        else
        {
            int lvt_3_1_ = 0;
            BlockPos lvt_4_1_ = sender.getPosition();
            double lvt_5_1_ = parseDouble((double)lvt_4_1_.getX(), args[lvt_3_1_++], true);
            double lvt_7_1_ = parseDouble((double)lvt_4_1_.getZ(), args[lvt_3_1_++], true);
            double lvt_9_1_ = parseDouble(args[lvt_3_1_++], 0.0D);
            double lvt_11_1_ = parseDouble(args[lvt_3_1_++], lvt_9_1_ + 1.0D);
            boolean lvt_13_1_ = parseBoolean(args[lvt_3_1_++]);
            List<Entity> lvt_14_1_ = Lists.newArrayList();

            while (lvt_3_1_ < args.length)
            {
                String lvt_15_1_ = args[lvt_3_1_++];

                if (PlayerSelector.hasArguments(lvt_15_1_))
                {
                    List<Entity> lvt_16_1_ = PlayerSelector.<Entity>matchEntities(sender, lvt_15_1_, Entity.class);

                    if (lvt_16_1_.size() == 0)
                    {
                        throw new EntityNotFoundException();
                    }

                    lvt_14_1_.addAll(lvt_16_1_);
                }
                else
                {
                    EntityPlayer lvt_16_2_ = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(lvt_15_1_);

                    if (lvt_16_2_ == null)
                    {
                        throw new PlayerNotFoundException();
                    }

                    lvt_14_1_.add(lvt_16_2_);
                }
            }

            sender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, lvt_14_1_.size());

            if (lvt_14_1_.isEmpty())
            {
                throw new EntityNotFoundException();
            }
            else
            {
                sender.addChatMessage(new ChatComponentTranslation("commands.spreadplayers.spreading." + (lvt_13_1_ ? "teams" : "players"), new Object[] {Integer.valueOf(lvt_14_1_.size()), Double.valueOf(lvt_11_1_), Double.valueOf(lvt_5_1_), Double.valueOf(lvt_7_1_), Double.valueOf(lvt_9_1_)}));
                this.func_110669_a(sender, lvt_14_1_, new CommandSpreadPlayers.Position(lvt_5_1_, lvt_7_1_), lvt_9_1_, lvt_11_1_, ((Entity)lvt_14_1_.get(0)).worldObj, lvt_13_1_);
            }
        }
    }

    private void func_110669_a(ICommandSender p_110669_1_, List<Entity> p_110669_2_, CommandSpreadPlayers.Position p_110669_3_, double p_110669_4_, double p_110669_6_, World worldIn, boolean p_110669_9_) throws CommandException
    {
        Random lvt_10_1_ = new Random();
        double lvt_11_1_ = p_110669_3_.field_111101_a - p_110669_6_;
        double lvt_13_1_ = p_110669_3_.field_111100_b - p_110669_6_;
        double lvt_15_1_ = p_110669_3_.field_111101_a + p_110669_6_;
        double lvt_17_1_ = p_110669_3_.field_111100_b + p_110669_6_;
        CommandSpreadPlayers.Position[] lvt_19_1_ = this.func_110670_a(lvt_10_1_, p_110669_9_ ? this.func_110667_a(p_110669_2_) : p_110669_2_.size(), lvt_11_1_, lvt_13_1_, lvt_15_1_, lvt_17_1_);
        int lvt_20_1_ = this.func_110668_a(p_110669_3_, p_110669_4_, worldIn, lvt_10_1_, lvt_11_1_, lvt_13_1_, lvt_15_1_, lvt_17_1_, lvt_19_1_, p_110669_9_);
        double lvt_21_1_ = this.func_110671_a(p_110669_2_, worldIn, lvt_19_1_, p_110669_9_);
        notifyOperators(p_110669_1_, this, "commands.spreadplayers.success." + (p_110669_9_ ? "teams" : "players"), new Object[] {Integer.valueOf(lvt_19_1_.length), Double.valueOf(p_110669_3_.field_111101_a), Double.valueOf(p_110669_3_.field_111100_b)});

        if (lvt_19_1_.length > 1)
        {
            p_110669_1_.addChatMessage(new ChatComponentTranslation("commands.spreadplayers.info." + (p_110669_9_ ? "teams" : "players"), new Object[] {String.format("%.2f", new Object[]{Double.valueOf(lvt_21_1_)}), Integer.valueOf(lvt_20_1_)}));
        }
    }

    private int func_110667_a(List<Entity> p_110667_1_)
    {
        Set<Team> lvt_2_1_ = Sets.newHashSet();

        for (Entity lvt_4_1_ : p_110667_1_)
        {
            if (lvt_4_1_ instanceof EntityPlayer)
            {
                lvt_2_1_.add(((EntityPlayer)lvt_4_1_).getTeam());
            }
            else
            {
                lvt_2_1_.add((Object)null);
            }
        }

        return lvt_2_1_.size();
    }

    private int func_110668_a(CommandSpreadPlayers.Position p_110668_1_, double p_110668_2_, World worldIn, Random p_110668_5_, double p_110668_6_, double p_110668_8_, double p_110668_10_, double p_110668_12_, CommandSpreadPlayers.Position[] p_110668_14_, boolean p_110668_15_) throws CommandException
    {
        boolean lvt_16_1_ = true;
        double lvt_18_1_ = 3.4028234663852886E38D;
        int lvt_17_1_;

        for (lvt_17_1_ = 0; lvt_17_1_ < 10000 && lvt_16_1_; ++lvt_17_1_)
        {
            lvt_16_1_ = false;
            lvt_18_1_ = 3.4028234663852886E38D;

            for (int lvt_20_1_ = 0; lvt_20_1_ < p_110668_14_.length; ++lvt_20_1_)
            {
                CommandSpreadPlayers.Position lvt_21_1_ = p_110668_14_[lvt_20_1_];
                int lvt_22_1_ = 0;
                CommandSpreadPlayers.Position lvt_23_1_ = new CommandSpreadPlayers.Position();

                for (int lvt_24_1_ = 0; lvt_24_1_ < p_110668_14_.length; ++lvt_24_1_)
                {
                    if (lvt_20_1_ != lvt_24_1_)
                    {
                        CommandSpreadPlayers.Position lvt_25_1_ = p_110668_14_[lvt_24_1_];
                        double lvt_26_1_ = lvt_21_1_.func_111099_a(lvt_25_1_);
                        lvt_18_1_ = Math.min(lvt_26_1_, lvt_18_1_);

                        if (lvt_26_1_ < p_110668_2_)
                        {
                            ++lvt_22_1_;
                            lvt_23_1_.field_111101_a += lvt_25_1_.field_111101_a - lvt_21_1_.field_111101_a;
                            lvt_23_1_.field_111100_b += lvt_25_1_.field_111100_b - lvt_21_1_.field_111100_b;
                        }
                    }
                }

                if (lvt_22_1_ > 0)
                {
                    lvt_23_1_.field_111101_a /= (double)lvt_22_1_;
                    lvt_23_1_.field_111100_b /= (double)lvt_22_1_;
                    double lvt_24_2_ = (double)lvt_23_1_.func_111096_b();

                    if (lvt_24_2_ > 0.0D)
                    {
                        lvt_23_1_.func_111095_a();
                        lvt_21_1_.func_111094_b(lvt_23_1_);
                    }
                    else
                    {
                        lvt_21_1_.func_111097_a(p_110668_5_, p_110668_6_, p_110668_8_, p_110668_10_, p_110668_12_);
                    }

                    lvt_16_1_ = true;
                }

                if (lvt_21_1_.func_111093_a(p_110668_6_, p_110668_8_, p_110668_10_, p_110668_12_))
                {
                    lvt_16_1_ = true;
                }
            }

            if (!lvt_16_1_)
            {
                for (CommandSpreadPlayers.Position lvt_23_2_ : p_110668_14_)
                {
                    if (!lvt_23_2_.func_111098_b(worldIn))
                    {
                        lvt_23_2_.func_111097_a(p_110668_5_, p_110668_6_, p_110668_8_, p_110668_10_, p_110668_12_);
                        lvt_16_1_ = true;
                    }
                }
            }
        }

        if (lvt_17_1_ >= 10000)
        {
            throw new CommandException("commands.spreadplayers.failure." + (p_110668_15_ ? "teams" : "players"), new Object[] {Integer.valueOf(p_110668_14_.length), Double.valueOf(p_110668_1_.field_111101_a), Double.valueOf(p_110668_1_.field_111100_b), String.format("%.2f", new Object[]{Double.valueOf(lvt_18_1_)})});
        }
        else
        {
            return lvt_17_1_;
        }
    }

    private double func_110671_a(List<Entity> p_110671_1_, World worldIn, CommandSpreadPlayers.Position[] p_110671_3_, boolean p_110671_4_)
    {
        double lvt_5_1_ = 0.0D;
        int lvt_7_1_ = 0;
        Map<Team, CommandSpreadPlayers.Position> lvt_8_1_ = Maps.newHashMap();

        for (int lvt_9_1_ = 0; lvt_9_1_ < p_110671_1_.size(); ++lvt_9_1_)
        {
            Entity lvt_10_1_ = (Entity)p_110671_1_.get(lvt_9_1_);
            CommandSpreadPlayers.Position lvt_11_1_;

            if (p_110671_4_)
            {
                Team lvt_12_1_ = lvt_10_1_ instanceof EntityPlayer ? ((EntityPlayer)lvt_10_1_).getTeam() : null;

                if (!lvt_8_1_.containsKey(lvt_12_1_))
                {
                    lvt_8_1_.put(lvt_12_1_, p_110671_3_[lvt_7_1_++]);
                }

                lvt_11_1_ = (CommandSpreadPlayers.Position)lvt_8_1_.get(lvt_12_1_);
            }
            else
            {
                lvt_11_1_ = p_110671_3_[lvt_7_1_++];
            }

            lvt_10_1_.setPositionAndUpdate((double)((float)MathHelper.floor_double(lvt_11_1_.field_111101_a) + 0.5F), (double)lvt_11_1_.func_111092_a(worldIn), (double)MathHelper.floor_double(lvt_11_1_.field_111100_b) + 0.5D);
            double lvt_12_2_ = Double.MAX_VALUE;

            for (int lvt_14_1_ = 0; lvt_14_1_ < p_110671_3_.length; ++lvt_14_1_)
            {
                if (lvt_11_1_ != p_110671_3_[lvt_14_1_])
                {
                    double lvt_15_1_ = lvt_11_1_.func_111099_a(p_110671_3_[lvt_14_1_]);
                    lvt_12_2_ = Math.min(lvt_15_1_, lvt_12_2_);
                }
            }

            lvt_5_1_ += lvt_12_2_;
        }

        lvt_5_1_ = lvt_5_1_ / (double)p_110671_1_.size();
        return lvt_5_1_;
    }

    private CommandSpreadPlayers.Position[] func_110670_a(Random p_110670_1_, int p_110670_2_, double p_110670_3_, double p_110670_5_, double p_110670_7_, double p_110670_9_)
    {
        CommandSpreadPlayers.Position[] lvt_11_1_ = new CommandSpreadPlayers.Position[p_110670_2_];

        for (int lvt_12_1_ = 0; lvt_12_1_ < lvt_11_1_.length; ++lvt_12_1_)
        {
            CommandSpreadPlayers.Position lvt_13_1_ = new CommandSpreadPlayers.Position();
            lvt_13_1_.func_111097_a(p_110670_1_, p_110670_3_, p_110670_5_, p_110670_7_, p_110670_9_);
            lvt_11_1_[lvt_12_1_] = lvt_13_1_;
        }

        return lvt_11_1_;
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length >= 1 && args.length <= 2 ? func_181043_b(args, 0, pos) : null;
    }

    static class Position
    {
        double field_111101_a;
        double field_111100_b;

        Position()
        {
        }

        Position(double p_i1358_1_, double p_i1358_3_)
        {
            this.field_111101_a = p_i1358_1_;
            this.field_111100_b = p_i1358_3_;
        }

        double func_111099_a(CommandSpreadPlayers.Position p_111099_1_)
        {
            double lvt_2_1_ = this.field_111101_a - p_111099_1_.field_111101_a;
            double lvt_4_1_ = this.field_111100_b - p_111099_1_.field_111100_b;
            return Math.sqrt(lvt_2_1_ * lvt_2_1_ + lvt_4_1_ * lvt_4_1_);
        }

        void func_111095_a()
        {
            double lvt_1_1_ = (double)this.func_111096_b();
            this.field_111101_a /= lvt_1_1_;
            this.field_111100_b /= lvt_1_1_;
        }

        float func_111096_b()
        {
            return MathHelper.sqrt_double(this.field_111101_a * this.field_111101_a + this.field_111100_b * this.field_111100_b);
        }

        public void func_111094_b(CommandSpreadPlayers.Position p_111094_1_)
        {
            this.field_111101_a -= p_111094_1_.field_111101_a;
            this.field_111100_b -= p_111094_1_.field_111100_b;
        }

        public boolean func_111093_a(double p_111093_1_, double p_111093_3_, double p_111093_5_, double p_111093_7_)
        {
            boolean lvt_9_1_ = false;

            if (this.field_111101_a < p_111093_1_)
            {
                this.field_111101_a = p_111093_1_;
                lvt_9_1_ = true;
            }
            else if (this.field_111101_a > p_111093_5_)
            {
                this.field_111101_a = p_111093_5_;
                lvt_9_1_ = true;
            }

            if (this.field_111100_b < p_111093_3_)
            {
                this.field_111100_b = p_111093_3_;
                lvt_9_1_ = true;
            }
            else if (this.field_111100_b > p_111093_7_)
            {
                this.field_111100_b = p_111093_7_;
                lvt_9_1_ = true;
            }

            return lvt_9_1_;
        }

        public int func_111092_a(World worldIn)
        {
            BlockPos lvt_2_1_ = new BlockPos(this.field_111101_a, 256.0D, this.field_111100_b);

            while (lvt_2_1_.getY() > 0)
            {
                lvt_2_1_ = lvt_2_1_.down();

                if (worldIn.getBlockState(lvt_2_1_).getBlock().getMaterial() != Material.air)
                {
                    return lvt_2_1_.getY() + 1;
                }
            }

            return 257;
        }

        public boolean func_111098_b(World worldIn)
        {
            BlockPos lvt_2_1_ = new BlockPos(this.field_111101_a, 256.0D, this.field_111100_b);

            while (lvt_2_1_.getY() > 0)
            {
                lvt_2_1_ = lvt_2_1_.down();
                Material lvt_3_1_ = worldIn.getBlockState(lvt_2_1_).getBlock().getMaterial();

                if (lvt_3_1_ != Material.air)
                {
                    return !lvt_3_1_.isLiquid() && lvt_3_1_ != Material.fire;
                }
            }

            return false;
        }

        public void func_111097_a(Random p_111097_1_, double p_111097_2_, double p_111097_4_, double p_111097_6_, double p_111097_8_)
        {
            this.field_111101_a = MathHelper.getRandomDoubleInRange(p_111097_1_, p_111097_2_, p_111097_6_);
            this.field_111100_b = MathHelper.getRandomDoubleInRange(p_111097_1_, p_111097_4_, p_111097_8_);
        }
    }
}
