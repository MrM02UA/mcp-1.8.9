package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;

public class CommandClearInventory extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "clear";
    }

    /**
     * Gets the usage string for the command.
     */
    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.clear.usage";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        EntityPlayerMP lvt_3_1_ = args.length == 0 ? getCommandSenderAsPlayer(sender) : getPlayer(sender, args[0]);
        Item lvt_4_1_ = args.length >= 2 ? getItemByText(sender, args[1]) : null;
        int lvt_5_1_ = args.length >= 3 ? parseInt(args[2], -1) : -1;
        int lvt_6_1_ = args.length >= 4 ? parseInt(args[3], -1) : -1;
        NBTTagCompound lvt_7_1_ = null;

        if (args.length >= 5)
        {
            try
            {
                lvt_7_1_ = JsonToNBT.getTagFromJson(buildString(args, 4));
            }
            catch (NBTException var9)
            {
                throw new CommandException("commands.clear.tagError", new Object[] {var9.getMessage()});
            }
        }

        if (args.length >= 2 && lvt_4_1_ == null)
        {
            throw new CommandException("commands.clear.failure", new Object[] {lvt_3_1_.getName()});
        }
        else
        {
            int lvt_8_2_ = lvt_3_1_.inventory.clearMatchingItems(lvt_4_1_, lvt_5_1_, lvt_6_1_, lvt_7_1_);
            lvt_3_1_.inventoryContainer.detectAndSendChanges();

            if (!lvt_3_1_.capabilities.isCreativeMode)
            {
                lvt_3_1_.updateHeldItem();
            }

            sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, lvt_8_2_);

            if (lvt_8_2_ == 0)
            {
                throw new CommandException("commands.clear.failure", new Object[] {lvt_3_1_.getName()});
            }
            else
            {
                if (lvt_6_1_ == 0)
                {
                    sender.addChatMessage(new ChatComponentTranslation("commands.clear.testing", new Object[] {lvt_3_1_.getName(), Integer.valueOf(lvt_8_2_)}));
                }
                else
                {
                    notifyOperators(sender, this, "commands.clear.success", new Object[] {lvt_3_1_.getName(), Integer.valueOf(lvt_8_2_)});
                }
            }
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, this.func_147209_d()) : (args.length == 2 ? getListOfStringsMatchingLastWord(args, Item.itemRegistry.getKeys()) : null);
    }

    protected String[] func_147209_d()
    {
        return MinecraftServer.getServer().getAllUsernames();
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 0;
    }
}
