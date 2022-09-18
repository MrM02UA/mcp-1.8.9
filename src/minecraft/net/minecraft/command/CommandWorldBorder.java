package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.border.WorldBorder;

public class CommandWorldBorder extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "worldborder";
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
        return "commands.worldborder.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.worldborder.usage", new Object[0]);
        }
        else
        {
            WorldBorder lvt_3_1_ = this.getWorldBorder();

            if (args[0].equals("set"))
            {
                if (args.length != 2 && args.length != 3)
                {
                    throw new WrongUsageException("commands.worldborder.set.usage", new Object[0]);
                }

                double lvt_4_1_ = lvt_3_1_.getTargetSize();
                double lvt_6_1_ = parseDouble(args[1], 1.0D, 6.0E7D);
                long lvt_8_1_ = args.length > 2 ? parseLong(args[2], 0L, 9223372036854775L) * 1000L : 0L;

                if (lvt_8_1_ > 0L)
                {
                    lvt_3_1_.setTransition(lvt_4_1_, lvt_6_1_, lvt_8_1_);

                    if (lvt_4_1_ > lvt_6_1_)
                    {
                        notifyOperators(sender, this, "commands.worldborder.setSlowly.shrink.success", new Object[] {String.format("%.1f", new Object[]{Double.valueOf(lvt_6_1_)}), String.format("%.1f", new Object[]{Double.valueOf(lvt_4_1_)}), Long.toString(lvt_8_1_ / 1000L)});
                    }
                    else
                    {
                        notifyOperators(sender, this, "commands.worldborder.setSlowly.grow.success", new Object[] {String.format("%.1f", new Object[]{Double.valueOf(lvt_6_1_)}), String.format("%.1f", new Object[]{Double.valueOf(lvt_4_1_)}), Long.toString(lvt_8_1_ / 1000L)});
                    }
                }
                else
                {
                    lvt_3_1_.setTransition(lvt_6_1_);
                    notifyOperators(sender, this, "commands.worldborder.set.success", new Object[] {String.format("%.1f", new Object[]{Double.valueOf(lvt_6_1_)}), String.format("%.1f", new Object[]{Double.valueOf(lvt_4_1_)})});
                }
            }
            else if (args[0].equals("add"))
            {
                if (args.length != 2 && args.length != 3)
                {
                    throw new WrongUsageException("commands.worldborder.add.usage", new Object[0]);
                }

                double lvt_4_2_ = lvt_3_1_.getDiameter();
                double lvt_6_2_ = lvt_4_2_ + parseDouble(args[1], -lvt_4_2_, 6.0E7D - lvt_4_2_);
                long lvt_8_2_ = lvt_3_1_.getTimeUntilTarget() + (args.length > 2 ? parseLong(args[2], 0L, 9223372036854775L) * 1000L : 0L);

                if (lvt_8_2_ > 0L)
                {
                    lvt_3_1_.setTransition(lvt_4_2_, lvt_6_2_, lvt_8_2_);

                    if (lvt_4_2_ > lvt_6_2_)
                    {
                        notifyOperators(sender, this, "commands.worldborder.setSlowly.shrink.success", new Object[] {String.format("%.1f", new Object[]{Double.valueOf(lvt_6_2_)}), String.format("%.1f", new Object[]{Double.valueOf(lvt_4_2_)}), Long.toString(lvt_8_2_ / 1000L)});
                    }
                    else
                    {
                        notifyOperators(sender, this, "commands.worldborder.setSlowly.grow.success", new Object[] {String.format("%.1f", new Object[]{Double.valueOf(lvt_6_2_)}), String.format("%.1f", new Object[]{Double.valueOf(lvt_4_2_)}), Long.toString(lvt_8_2_ / 1000L)});
                    }
                }
                else
                {
                    lvt_3_1_.setTransition(lvt_6_2_);
                    notifyOperators(sender, this, "commands.worldborder.set.success", new Object[] {String.format("%.1f", new Object[]{Double.valueOf(lvt_6_2_)}), String.format("%.1f", new Object[]{Double.valueOf(lvt_4_2_)})});
                }
            }
            else if (args[0].equals("center"))
            {
                if (args.length != 3)
                {
                    throw new WrongUsageException("commands.worldborder.center.usage", new Object[0]);
                }

                BlockPos lvt_4_3_ = sender.getPosition();
                double lvt_5_1_ = parseDouble((double)lvt_4_3_.getX() + 0.5D, args[1], true);
                double lvt_7_1_ = parseDouble((double)lvt_4_3_.getZ() + 0.5D, args[2], true);
                lvt_3_1_.setCenter(lvt_5_1_, lvt_7_1_);
                notifyOperators(sender, this, "commands.worldborder.center.success", new Object[] {Double.valueOf(lvt_5_1_), Double.valueOf(lvt_7_1_)});
            }
            else if (args[0].equals("damage"))
            {
                if (args.length < 2)
                {
                    throw new WrongUsageException("commands.worldborder.damage.usage", new Object[0]);
                }

                if (args[1].equals("buffer"))
                {
                    if (args.length != 3)
                    {
                        throw new WrongUsageException("commands.worldborder.damage.buffer.usage", new Object[0]);
                    }

                    double lvt_4_4_ = parseDouble(args[2], 0.0D);
                    double lvt_6_3_ = lvt_3_1_.getDamageBuffer();
                    lvt_3_1_.setDamageBuffer(lvt_4_4_);
                    notifyOperators(sender, this, "commands.worldborder.damage.buffer.success", new Object[] {String.format("%.1f", new Object[]{Double.valueOf(lvt_4_4_)}), String.format("%.1f", new Object[]{Double.valueOf(lvt_6_3_)})});
                }
                else if (args[1].equals("amount"))
                {
                    if (args.length != 3)
                    {
                        throw new WrongUsageException("commands.worldborder.damage.amount.usage", new Object[0]);
                    }

                    double lvt_4_5_ = parseDouble(args[2], 0.0D);
                    double lvt_6_4_ = lvt_3_1_.getDamageAmount();
                    lvt_3_1_.setDamageAmount(lvt_4_5_);
                    notifyOperators(sender, this, "commands.worldborder.damage.amount.success", new Object[] {String.format("%.2f", new Object[]{Double.valueOf(lvt_4_5_)}), String.format("%.2f", new Object[]{Double.valueOf(lvt_6_4_)})});
                }
            }
            else if (args[0].equals("warning"))
            {
                if (args.length < 2)
                {
                    throw new WrongUsageException("commands.worldborder.warning.usage", new Object[0]);
                }

                int lvt_4_6_ = parseInt(args[2], 0);

                if (args[1].equals("time"))
                {
                    if (args.length != 3)
                    {
                        throw new WrongUsageException("commands.worldborder.warning.time.usage", new Object[0]);
                    }

                    int lvt_5_2_ = lvt_3_1_.getWarningTime();
                    lvt_3_1_.setWarningTime(lvt_4_6_);
                    notifyOperators(sender, this, "commands.worldborder.warning.time.success", new Object[] {Integer.valueOf(lvt_4_6_), Integer.valueOf(lvt_5_2_)});
                }
                else if (args[1].equals("distance"))
                {
                    if (args.length != 3)
                    {
                        throw new WrongUsageException("commands.worldborder.warning.distance.usage", new Object[0]);
                    }

                    int lvt_5_3_ = lvt_3_1_.getWarningDistance();
                    lvt_3_1_.setWarningDistance(lvt_4_6_);
                    notifyOperators(sender, this, "commands.worldborder.warning.distance.success", new Object[] {Integer.valueOf(lvt_4_6_), Integer.valueOf(lvt_5_3_)});
                }
            }
            else
            {
                if (!args[0].equals("get"))
                {
                    throw new WrongUsageException("commands.worldborder.usage", new Object[0]);
                }

                double lvt_4_7_ = lvt_3_1_.getDiameter();
                sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, MathHelper.floor_double(lvt_4_7_ + 0.5D));
                sender.addChatMessage(new ChatComponentTranslation("commands.worldborder.get.success", new Object[] {String.format("%.0f", new Object[]{Double.valueOf(lvt_4_7_)})}));
            }
        }
    }

    protected WorldBorder getWorldBorder()
    {
        return MinecraftServer.getServer().worldServers[0].getWorldBorder();
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[] {"set", "center", "damage", "warning", "add", "get"}): (args.length == 2 && args[0].equals("damage") ? getListOfStringsMatchingLastWord(args, new String[] {"buffer", "amount"}): (args.length >= 2 && args.length <= 3 && args[0].equals("center") ? func_181043_b(args, 1, pos) : (args.length == 2 && args[0].equals("warning") ? getListOfStringsMatchingLastWord(args, new String[] {"time", "distance"}): null)));
    }
}
