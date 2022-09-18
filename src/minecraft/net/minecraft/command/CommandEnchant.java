package net.minecraft.command;

import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandEnchant extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "enchant";
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
        return "commands.enchant.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException("commands.enchant.usage", new Object[0]);
        }
        else
        {
            EntityPlayer lvt_3_1_ = getPlayer(sender, args[0]);
            sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 0);
            int lvt_4_1_;

            try
            {
                lvt_4_1_ = parseInt(args[1], 0);
            }
            catch (NumberInvalidException var12)
            {
                Enchantment lvt_6_1_ = Enchantment.getEnchantmentByLocation(args[1]);

                if (lvt_6_1_ == null)
                {
                    throw var12;
                }

                lvt_4_1_ = lvt_6_1_.effectId;
            }

            int lvt_5_2_ = 1;
            ItemStack lvt_6_2_ = lvt_3_1_.getCurrentEquippedItem();

            if (lvt_6_2_ == null)
            {
                throw new CommandException("commands.enchant.noItem", new Object[0]);
            }
            else
            {
                Enchantment lvt_7_1_ = Enchantment.getEnchantmentById(lvt_4_1_);

                if (lvt_7_1_ == null)
                {
                    throw new NumberInvalidException("commands.enchant.notFound", new Object[] {Integer.valueOf(lvt_4_1_)});
                }
                else if (!lvt_7_1_.canApply(lvt_6_2_))
                {
                    throw new CommandException("commands.enchant.cantEnchant", new Object[0]);
                }
                else
                {
                    if (args.length >= 3)
                    {
                        lvt_5_2_ = parseInt(args[2], lvt_7_1_.getMinLevel(), lvt_7_1_.getMaxLevel());
                    }

                    if (lvt_6_2_.hasTagCompound())
                    {
                        NBTTagList lvt_8_1_ = lvt_6_2_.getEnchantmentTagList();

                        if (lvt_8_1_ != null)
                        {
                            for (int lvt_9_1_ = 0; lvt_9_1_ < lvt_8_1_.tagCount(); ++lvt_9_1_)
                            {
                                int lvt_10_1_ = lvt_8_1_.getCompoundTagAt(lvt_9_1_).getShort("id");

                                if (Enchantment.getEnchantmentById(lvt_10_1_) != null)
                                {
                                    Enchantment lvt_11_1_ = Enchantment.getEnchantmentById(lvt_10_1_);

                                    if (!lvt_11_1_.canApplyTogether(lvt_7_1_))
                                    {
                                        throw new CommandException("commands.enchant.cantCombine", new Object[] {lvt_7_1_.getTranslatedName(lvt_5_2_), lvt_11_1_.getTranslatedName(lvt_8_1_.getCompoundTagAt(lvt_9_1_).getShort("lvl"))});
                                    }
                                }
                            }
                        }
                    }

                    lvt_6_2_.addEnchantment(lvt_7_1_, lvt_5_2_);
                    notifyOperators(sender, this, "commands.enchant.success", new Object[0]);
                    sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 1);
                }
            }
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, this.getListOfPlayers()) : (args.length == 2 ? getListOfStringsMatchingLastWord(args, Enchantment.func_181077_c()) : null);
    }

    protected String[] getListOfPlayers()
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
