package net.minecraft.command;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class CommandExecuteAt extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "execute";
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
        return "commands.execute.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(final ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 5)
        {
            throw new WrongUsageException("commands.execute.usage", new Object[0]);
        }
        else
        {
            final Entity lvt_3_1_ = getEntity(sender, args[0], Entity.class);
            final double lvt_4_1_ = parseDouble(lvt_3_1_.posX, args[1], false);
            final double lvt_6_1_ = parseDouble(lvt_3_1_.posY, args[2], false);
            final double lvt_8_1_ = parseDouble(lvt_3_1_.posZ, args[3], false);
            final BlockPos lvt_10_1_ = new BlockPos(lvt_4_1_, lvt_6_1_, lvt_8_1_);
            int lvt_11_1_ = 4;

            if ("detect".equals(args[4]) && args.length > 10)
            {
                World lvt_12_1_ = lvt_3_1_.getEntityWorld();
                double lvt_13_1_ = parseDouble(lvt_4_1_, args[5], false);
                double lvt_15_1_ = parseDouble(lvt_6_1_, args[6], false);
                double lvt_17_1_ = parseDouble(lvt_8_1_, args[7], false);
                Block lvt_19_1_ = getBlockByText(sender, args[8]);
                int lvt_20_1_ = parseInt(args[9], -1, 15);
                BlockPos lvt_21_1_ = new BlockPos(lvt_13_1_, lvt_15_1_, lvt_17_1_);
                IBlockState lvt_22_1_ = lvt_12_1_.getBlockState(lvt_21_1_);

                if (lvt_22_1_.getBlock() != lvt_19_1_ || lvt_20_1_ >= 0 && lvt_22_1_.getBlock().getMetaFromState(lvt_22_1_) != lvt_20_1_)
                {
                    throw new CommandException("commands.execute.failed", new Object[] {"detect", lvt_3_1_.getName()});
                }

                lvt_11_1_ = 10;
            }

            String lvt_12_2_ = buildString(args, lvt_11_1_);
            ICommandSender lvt_14_1_ = new ICommandSender()
            {
                public String getName()
                {
                    return lvt_3_1_.getName();
                }
                public IChatComponent getDisplayName()
                {
                    return lvt_3_1_.getDisplayName();
                }
                public void addChatMessage(IChatComponent component)
                {
                    sender.addChatMessage(component);
                }
                public boolean canCommandSenderUseCommand(int permLevel, String commandName)
                {
                    return sender.canCommandSenderUseCommand(permLevel, commandName);
                }
                public BlockPos getPosition()
                {
                    return lvt_10_1_;
                }
                public Vec3 getPositionVector()
                {
                    return new Vec3(lvt_4_1_, lvt_6_1_, lvt_8_1_);
                }
                public World getEntityWorld()
                {
                    return lvt_3_1_.worldObj;
                }
                public Entity getCommandSenderEntity()
                {
                    return lvt_3_1_;
                }
                public boolean sendCommandFeedback()
                {
                    MinecraftServer lvt_1_1_ = MinecraftServer.getServer();
                    return lvt_1_1_ == null || lvt_1_1_.worldServers[0].getGameRules().getBoolean("commandBlockOutput");
                }
                public void setCommandStat(CommandResultStats.Type type, int amount)
                {
                    lvt_3_1_.setCommandStat(type, amount);
                }
            };
            ICommandManager lvt_15_2_ = MinecraftServer.getServer().getCommandManager();

            try
            {
                int lvt_16_1_ = lvt_15_2_.executeCommand(lvt_14_1_, lvt_12_2_);

                if (lvt_16_1_ < 1)
                {
                    throw new CommandException("commands.execute.allInvocationsFailed", new Object[] {lvt_12_2_});
                }
            }
            catch (Throwable var23)
            {
                throw new CommandException("commands.execute.failed", new Object[] {lvt_12_2_, lvt_3_1_.getName()});
            }
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : (args.length > 1 && args.length <= 4 ? func_175771_a(args, 1, pos) : (args.length > 5 && args.length <= 8 && "detect".equals(args[4]) ? func_175771_a(args, 5, pos) : (args.length == 9 && "detect".equals(args[4]) ? getListOfStringsMatchingLastWord(args, Block.blockRegistry.getKeys()) : null)));
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 0;
    }
}
