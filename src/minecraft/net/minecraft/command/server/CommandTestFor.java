package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandTestFor extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "testfor";
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
        return "commands.testfor.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.testfor.usage", new Object[0]);
        }
        else
        {
            Entity lvt_3_1_ = getEntity(sender, args[0]);
            NBTTagCompound lvt_4_1_ = null;

            if (args.length >= 2)
            {
                try
                {
                    lvt_4_1_ = JsonToNBT.getTagFromJson(buildString(args, 1));
                }
                catch (NBTException var6)
                {
                    throw new CommandException("commands.testfor.tagError", new Object[] {var6.getMessage()});
                }
            }

            if (lvt_4_1_ != null)
            {
                NBTTagCompound lvt_5_2_ = new NBTTagCompound();
                lvt_3_1_.writeToNBT(lvt_5_2_);

                if (!NBTUtil.func_181123_a(lvt_4_1_, lvt_5_2_, true))
                {
                    throw new CommandException("commands.testfor.failure", new Object[] {lvt_3_1_.getName()});
                }
            }

            notifyOperators(sender, this, "commands.testfor.success", new Object[] {lvt_3_1_.getName()});
        }
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 0;
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
    }
}
