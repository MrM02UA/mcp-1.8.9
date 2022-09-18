package net.minecraft.command;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

public class CommandEntityData extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "entitydata";
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
        return "commands.entitydata.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException("commands.entitydata.usage", new Object[0]);
        }
        else
        {
            Entity lvt_3_1_ = getEntity(sender, args[0]);

            if (lvt_3_1_ instanceof EntityPlayer)
            {
                throw new CommandException("commands.entitydata.noPlayers", new Object[] {lvt_3_1_.getDisplayName()});
            }
            else
            {
                NBTTagCompound lvt_4_1_ = new NBTTagCompound();
                lvt_3_1_.writeToNBT(lvt_4_1_);
                NBTTagCompound lvt_5_1_ = (NBTTagCompound)lvt_4_1_.copy();
                NBTTagCompound lvt_6_1_;

                try
                {
                    lvt_6_1_ = JsonToNBT.getTagFromJson(getChatComponentFromNthArg(sender, args, 1).getUnformattedText());
                }
                catch (NBTException var8)
                {
                    throw new CommandException("commands.entitydata.tagError", new Object[] {var8.getMessage()});
                }

                lvt_6_1_.removeTag("UUIDMost");
                lvt_6_1_.removeTag("UUIDLeast");
                lvt_4_1_.merge(lvt_6_1_);

                if (lvt_4_1_.equals(lvt_5_1_))
                {
                    throw new CommandException("commands.entitydata.failed", new Object[] {lvt_4_1_.toString()});
                }
                else
                {
                    lvt_3_1_.readFromNBT(lvt_4_1_);
                    notifyOperators(sender, this, "commands.entitydata.success", new Object[] {lvt_4_1_.toString()});
                }
            }
        }
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 0;
    }
}
