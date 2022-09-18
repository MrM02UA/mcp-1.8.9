package net.minecraft.command;

import java.util.List;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandBlockData extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "blockdata";
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
        return "commands.blockdata.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 4)
        {
            throw new WrongUsageException("commands.blockdata.usage", new Object[0]);
        }
        else
        {
            sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
            BlockPos lvt_3_1_ = parseBlockPos(sender, args, 0, false);
            World lvt_4_1_ = sender.getEntityWorld();

            if (!lvt_4_1_.isBlockLoaded(lvt_3_1_))
            {
                throw new CommandException("commands.blockdata.outOfWorld", new Object[0]);
            }
            else
            {
                TileEntity lvt_5_1_ = lvt_4_1_.getTileEntity(lvt_3_1_);

                if (lvt_5_1_ == null)
                {
                    throw new CommandException("commands.blockdata.notValid", new Object[0]);
                }
                else
                {
                    NBTTagCompound lvt_6_1_ = new NBTTagCompound();
                    lvt_5_1_.writeToNBT(lvt_6_1_);
                    NBTTagCompound lvt_7_1_ = (NBTTagCompound)lvt_6_1_.copy();
                    NBTTagCompound lvt_8_1_;

                    try
                    {
                        lvt_8_1_ = JsonToNBT.getTagFromJson(getChatComponentFromNthArg(sender, args, 3).getUnformattedText());
                    }
                    catch (NBTException var10)
                    {
                        throw new CommandException("commands.blockdata.tagError", new Object[] {var10.getMessage()});
                    }

                    lvt_6_1_.merge(lvt_8_1_);
                    lvt_6_1_.setInteger("x", lvt_3_1_.getX());
                    lvt_6_1_.setInteger("y", lvt_3_1_.getY());
                    lvt_6_1_.setInteger("z", lvt_3_1_.getZ());

                    if (lvt_6_1_.equals(lvt_7_1_))
                    {
                        throw new CommandException("commands.blockdata.failed", new Object[] {lvt_6_1_.toString()});
                    }
                    else
                    {
                        lvt_5_1_.readFromNBT(lvt_6_1_);
                        lvt_5_1_.markDirty();
                        lvt_4_1_.markBlockForUpdate(lvt_3_1_);
                        sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 1);
                        notifyOperators(sender, this, "commands.blockdata.success", new Object[] {lvt_6_1_.toString()});
                    }
                }
            }
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length > 0 && args.length <= 3 ? func_175771_a(args, 0, pos) : null;
    }
}
