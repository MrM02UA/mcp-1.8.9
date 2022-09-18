package net.minecraft.command;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandReplaceItem extends CommandBase
{
    private static final Map<String, Integer> SHORTCUTS = Maps.newHashMap();

    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "replaceitem";
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
        return "commands.replaceitem.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.replaceitem.usage", new Object[0]);
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
                    throw new WrongUsageException("commands.replaceitem.usage", new Object[0]);
                }

                lvt_3_1_ = true;
            }

            int lvt_4_1_;

            if (lvt_3_1_)
            {
                if (args.length < 6)
                {
                    throw new WrongUsageException("commands.replaceitem.block.usage", new Object[0]);
                }

                lvt_4_1_ = 4;
            }
            else
            {
                if (args.length < 4)
                {
                    throw new WrongUsageException("commands.replaceitem.entity.usage", new Object[0]);
                }

                lvt_4_1_ = 2;
            }

            int lvt_5_1_ = this.getSlotForShortcut(args[lvt_4_1_++]);
            Item lvt_6_1_;

            try
            {
                lvt_6_1_ = getItemByText(sender, args[lvt_4_1_]);
            }
            catch (NumberInvalidException var15)
            {
                if (Block.getBlockFromName(args[lvt_4_1_]) != Blocks.air)
                {
                    throw var15;
                }

                lvt_6_1_ = null;
            }

            ++lvt_4_1_;
            int lvt_7_2_ = args.length > lvt_4_1_ ? parseInt(args[lvt_4_1_++], 1, 64) : 1;
            int lvt_8_1_ = args.length > lvt_4_1_ ? parseInt(args[lvt_4_1_++]) : 0;
            ItemStack lvt_9_1_ = new ItemStack(lvt_6_1_, lvt_7_2_, lvt_8_1_);

            if (args.length > lvt_4_1_)
            {
                String lvt_10_1_ = getChatComponentFromNthArg(sender, args, lvt_4_1_).getUnformattedText();

                try
                {
                    lvt_9_1_.setTagCompound(JsonToNBT.getTagFromJson(lvt_10_1_));
                }
                catch (NBTException var14)
                {
                    throw new CommandException("commands.replaceitem.tagError", new Object[] {var14.getMessage()});
                }
            }

            if (lvt_9_1_.getItem() == null)
            {
                lvt_9_1_ = null;
            }

            if (lvt_3_1_)
            {
                sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 0);
                BlockPos lvt_10_2_ = parseBlockPos(sender, args, 1, false);
                World lvt_11_2_ = sender.getEntityWorld();
                TileEntity lvt_12_1_ = lvt_11_2_.getTileEntity(lvt_10_2_);

                if (lvt_12_1_ == null || !(lvt_12_1_ instanceof IInventory))
                {
                    throw new CommandException("commands.replaceitem.noContainer", new Object[] {Integer.valueOf(lvt_10_2_.getX()), Integer.valueOf(lvt_10_2_.getY()), Integer.valueOf(lvt_10_2_.getZ())});
                }

                IInventory lvt_13_1_ = (IInventory)lvt_12_1_;

                if (lvt_5_1_ >= 0 && lvt_5_1_ < lvt_13_1_.getSizeInventory())
                {
                    lvt_13_1_.setInventorySlotContents(lvt_5_1_, lvt_9_1_);
                }
            }
            else
            {
                Entity lvt_10_3_ = getEntity(sender, args[1]);
                sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 0);

                if (lvt_10_3_ instanceof EntityPlayer)
                {
                    ((EntityPlayer)lvt_10_3_).inventoryContainer.detectAndSendChanges();
                }

                if (!lvt_10_3_.replaceItemInInventory(lvt_5_1_, lvt_9_1_))
                {
                    throw new CommandException("commands.replaceitem.failed", new Object[] {Integer.valueOf(lvt_5_1_), Integer.valueOf(lvt_7_2_), lvt_9_1_ == null ? "Air" : lvt_9_1_.getChatComponent()});
                }

                if (lvt_10_3_ instanceof EntityPlayer)
                {
                    ((EntityPlayer)lvt_10_3_).inventoryContainer.detectAndSendChanges();
                }
            }

            sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, lvt_7_2_);
            notifyOperators(sender, this, "commands.replaceitem.success", new Object[] {Integer.valueOf(lvt_5_1_), Integer.valueOf(lvt_7_2_), lvt_9_1_ == null ? "Air" : lvt_9_1_.getChatComponent()});
        }
    }

    private int getSlotForShortcut(String shortcut) throws CommandException
    {
        if (!SHORTCUTS.containsKey(shortcut))
        {
            throw new CommandException("commands.generic.parameter.invalid", new Object[] {shortcut});
        }
        else
        {
            return ((Integer)SHORTCUTS.get(shortcut)).intValue();
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[] {"entity", "block"}): (args.length == 2 && args[0].equals("entity") ? getListOfStringsMatchingLastWord(args, this.getUsernames()) : (args.length >= 2 && args.length <= 4 && args[0].equals("block") ? func_175771_a(args, 1, pos) : ((args.length != 3 || !args[0].equals("entity")) && (args.length != 5 || !args[0].equals("block")) ? ((args.length != 4 || !args[0].equals("entity")) && (args.length != 6 || !args[0].equals("block")) ? null : getListOfStringsMatchingLastWord(args, Item.itemRegistry.getKeys())) : getListOfStringsMatchingLastWord(args, SHORTCUTS.keySet()))));
    }

    protected String[] getUsernames()
    {
        return MinecraftServer.getServer().getAllUsernames();
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] args, int index)
    {
        return args.length > 0 && args[0].equals("entity") && index == 1;
    }

    static
    {
        for (int lvt_0_1_ = 0; lvt_0_1_ < 54; ++lvt_0_1_)
        {
            SHORTCUTS.put("slot.container." + lvt_0_1_, Integer.valueOf(lvt_0_1_));
        }

        for (int lvt_0_2_ = 0; lvt_0_2_ < 9; ++lvt_0_2_)
        {
            SHORTCUTS.put("slot.hotbar." + lvt_0_2_, Integer.valueOf(lvt_0_2_));
        }

        for (int lvt_0_3_ = 0; lvt_0_3_ < 27; ++lvt_0_3_)
        {
            SHORTCUTS.put("slot.inventory." + lvt_0_3_, Integer.valueOf(9 + lvt_0_3_));
        }

        for (int lvt_0_4_ = 0; lvt_0_4_ < 27; ++lvt_0_4_)
        {
            SHORTCUTS.put("slot.enderchest." + lvt_0_4_, Integer.valueOf(200 + lvt_0_4_));
        }

        for (int lvt_0_5_ = 0; lvt_0_5_ < 8; ++lvt_0_5_)
        {
            SHORTCUTS.put("slot.villager." + lvt_0_5_, Integer.valueOf(300 + lvt_0_5_));
        }

        for (int lvt_0_6_ = 0; lvt_0_6_ < 15; ++lvt_0_6_)
        {
            SHORTCUTS.put("slot.horse." + lvt_0_6_, Integer.valueOf(500 + lvt_0_6_));
        }

        SHORTCUTS.put("slot.weapon", Integer.valueOf(99));
        SHORTCUTS.put("slot.armor.head", Integer.valueOf(103));
        SHORTCUTS.put("slot.armor.chest", Integer.valueOf(102));
        SHORTCUTS.put("slot.armor.legs", Integer.valueOf(101));
        SHORTCUTS.put("slot.armor.feet", Integer.valueOf(100));
        SHORTCUTS.put("slot.horse.saddle", Integer.valueOf(400));
        SHORTCUTS.put("slot.horse.armor", Integer.valueOf(401));
        SHORTCUTS.put("slot.horse.chest", Integer.valueOf(499));
    }
}
