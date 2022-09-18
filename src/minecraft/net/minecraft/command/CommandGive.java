package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandGive extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "give";
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
        return "commands.give.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException("commands.give.usage", new Object[0]);
        }
        else
        {
            EntityPlayer lvt_3_1_ = getPlayer(sender, args[0]);
            Item lvt_4_1_ = getItemByText(sender, args[1]);
            int lvt_5_1_ = args.length >= 3 ? parseInt(args[2], 1, 64) : 1;
            int lvt_6_1_ = args.length >= 4 ? parseInt(args[3]) : 0;
            ItemStack lvt_7_1_ = new ItemStack(lvt_4_1_, lvt_5_1_, lvt_6_1_);

            if (args.length >= 5)
            {
                String lvt_8_1_ = getChatComponentFromNthArg(sender, args, 4).getUnformattedText();

                try
                {
                    lvt_7_1_.setTagCompound(JsonToNBT.getTagFromJson(lvt_8_1_));
                }
                catch (NBTException var10)
                {
                    throw new CommandException("commands.give.tagError", new Object[] {var10.getMessage()});
                }
            }

            boolean lvt_8_2_ = lvt_3_1_.inventory.addItemStackToInventory(lvt_7_1_);

            if (lvt_8_2_)
            {
                lvt_3_1_.worldObj.playSoundAtEntity(lvt_3_1_, "random.pop", 0.2F, ((lvt_3_1_.getRNG().nextFloat() - lvt_3_1_.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                lvt_3_1_.inventoryContainer.detectAndSendChanges();
            }

            if (lvt_8_2_ && lvt_7_1_.stackSize <= 0)
            {
                lvt_7_1_.stackSize = 1;
                sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, lvt_5_1_);
                EntityItem lvt_9_3_ = lvt_3_1_.dropPlayerItemWithRandomChoice(lvt_7_1_, false);

                if (lvt_9_3_ != null)
                {
                    lvt_9_3_.func_174870_v();
                }
            }
            else
            {
                sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, lvt_5_1_ - lvt_7_1_.stackSize);
                EntityItem lvt_9_2_ = lvt_3_1_.dropPlayerItemWithRandomChoice(lvt_7_1_, false);

                if (lvt_9_2_ != null)
                {
                    lvt_9_2_.setNoPickupDelay();
                    lvt_9_2_.setOwner(lvt_3_1_.getName());
                }
            }

            notifyOperators(sender, this, "commands.give.success", new Object[] {lvt_7_1_.getChatComponent(), Integer.valueOf(lvt_5_1_), lvt_3_1_.getName()});
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, this.getPlayers()) : (args.length == 2 ? getListOfStringsMatchingLastWord(args, Item.itemRegistry.getKeys()) : null);
    }

    protected String[] getPlayers()
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
