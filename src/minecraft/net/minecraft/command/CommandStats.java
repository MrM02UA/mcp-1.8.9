package net.minecraft.command;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandStats extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "stats";
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
        return "commands.stats.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.stats.usage", new Object[0]);
        }
        else
        {
            boolean lvt_3_1_;

            if (args[0].equals("entity"))
            {
                lvt_3_1_ = false;
            }
            else
            {
                if (!args[0].equals("block"))
                {
                    throw new WrongUsageException("commands.stats.usage", new Object[0]);
                }

                lvt_3_1_ = true;
            }

            int lvt_4_1_;

            if (lvt_3_1_)
            {
                if (args.length < 5)
                {
                    throw new WrongUsageException("commands.stats.block.usage", new Object[0]);
                }

                lvt_4_1_ = 4;
            }
            else
            {
                if (args.length < 3)
                {
                    throw new WrongUsageException("commands.stats.entity.usage", new Object[0]);
                }

                lvt_4_1_ = 2;
            }

            String lvt_5_1_ = args[lvt_4_1_++];

            if ("set".equals(lvt_5_1_))
            {
                if (args.length < lvt_4_1_ + 3)
                {
                    if (lvt_4_1_ == 5)
                    {
                        throw new WrongUsageException("commands.stats.block.set.usage", new Object[0]);
                    }

                    throw new WrongUsageException("commands.stats.entity.set.usage", new Object[0]);
                }
            }
            else
            {
                if (!"clear".equals(lvt_5_1_))
                {
                    throw new WrongUsageException("commands.stats.usage", new Object[0]);
                }

                if (args.length < lvt_4_1_ + 1)
                {
                    if (lvt_4_1_ == 5)
                    {
                        throw new WrongUsageException("commands.stats.block.clear.usage", new Object[0]);
                    }

                    throw new WrongUsageException("commands.stats.entity.clear.usage", new Object[0]);
                }
            }

            CommandResultStats.Type lvt_6_1_ = CommandResultStats.Type.getTypeByName(args[lvt_4_1_++]);

            if (lvt_6_1_ == null)
            {
                throw new CommandException("commands.stats.failed", new Object[0]);
            }
            else
            {
                World lvt_7_1_ = sender.getEntityWorld();
                CommandResultStats lvt_8_1_;

                if (lvt_3_1_)
                {
                    BlockPos lvt_9_1_ = parseBlockPos(sender, args, 1, false);
                    TileEntity lvt_10_1_ = lvt_7_1_.getTileEntity(lvt_9_1_);

                    if (lvt_10_1_ == null)
                    {
                        throw new CommandException("commands.stats.noCompatibleBlock", new Object[] {Integer.valueOf(lvt_9_1_.getX()), Integer.valueOf(lvt_9_1_.getY()), Integer.valueOf(lvt_9_1_.getZ())});
                    }

                    if (lvt_10_1_ instanceof TileEntityCommandBlock)
                    {
                        lvt_8_1_ = ((TileEntityCommandBlock)lvt_10_1_).getCommandResultStats();
                    }
                    else
                    {
                        if (!(lvt_10_1_ instanceof TileEntitySign))
                        {
                            throw new CommandException("commands.stats.noCompatibleBlock", new Object[] {Integer.valueOf(lvt_9_1_.getX()), Integer.valueOf(lvt_9_1_.getY()), Integer.valueOf(lvt_9_1_.getZ())});
                        }

                        lvt_8_1_ = ((TileEntitySign)lvt_10_1_).getStats();
                    }
                }
                else
                {
                    Entity lvt_9_2_ = getEntity(sender, args[1]);
                    lvt_8_1_ = lvt_9_2_.getCommandStats();
                }

                if ("set".equals(lvt_5_1_))
                {
                    String lvt_9_3_ = args[lvt_4_1_++];
                    String lvt_10_2_ = args[lvt_4_1_];

                    if (lvt_9_3_.length() == 0 || lvt_10_2_.length() == 0)
                    {
                        throw new CommandException("commands.stats.failed", new Object[0]);
                    }

                    CommandResultStats.setScoreBoardStat(lvt_8_1_, lvt_6_1_, lvt_9_3_, lvt_10_2_);
                    notifyOperators(sender, this, "commands.stats.success", new Object[] {lvt_6_1_.getTypeName(), lvt_10_2_, lvt_9_3_});
                }
                else if ("clear".equals(lvt_5_1_))
                {
                    CommandResultStats.setScoreBoardStat(lvt_8_1_, lvt_6_1_, (String)null, (String)null);
                    notifyOperators(sender, this, "commands.stats.cleared", new Object[] {lvt_6_1_.getTypeName()});
                }

                if (lvt_3_1_)
                {
                    BlockPos lvt_9_4_ = parseBlockPos(sender, args, 1, false);
                    TileEntity lvt_10_3_ = lvt_7_1_.getTileEntity(lvt_9_4_);
                    lvt_10_3_.markDirty();
                }
            }
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[] {"entity", "block"}): (args.length == 2 && args[0].equals("entity") ? getListOfStringsMatchingLastWord(args, this.func_175776_d()) : (args.length >= 2 && args.length <= 4 && args[0].equals("block") ? func_175771_a(args, 1, pos) : ((args.length != 3 || !args[0].equals("entity")) && (args.length != 5 || !args[0].equals("block")) ? ((args.length != 4 || !args[0].equals("entity")) && (args.length != 6 || !args[0].equals("block")) ? ((args.length != 6 || !args[0].equals("entity")) && (args.length != 8 || !args[0].equals("block")) ? null : getListOfStringsMatchingLastWord(args, this.func_175777_e())) : getListOfStringsMatchingLastWord(args, CommandResultStats.Type.getTypeNames())) : getListOfStringsMatchingLastWord(args, new String[] {"set", "clear"}))));
    }

    protected String[] func_175776_d()
    {
        return MinecraftServer.getServer().getAllUsernames();
    }

    protected List<String> func_175777_e()
    {
        Collection<ScoreObjective> lvt_1_1_ = MinecraftServer.getServer().worldServerForDimension(0).getScoreboard().getScoreObjectives();
        List<String> lvt_2_1_ = Lists.newArrayList();

        for (ScoreObjective lvt_4_1_ : lvt_1_1_)
        {
            if (!lvt_4_1_.getCriteria().isReadOnly())
            {
                lvt_2_1_.add(lvt_4_1_.getName());
            }
        }

        return lvt_2_1_;
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] args, int index)
    {
        return args.length > 0 && args[0].equals("entity") && index == 1;
    }
}
