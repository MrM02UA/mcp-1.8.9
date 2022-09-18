package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;

public class CommandEffect extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "effect";
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
        return "commands.effect.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException("commands.effect.usage", new Object[0]);
        }
        else
        {
            EntityLivingBase lvt_3_1_ = (EntityLivingBase)getEntity(sender, args[0], EntityLivingBase.class);

            if (args[1].equals("clear"))
            {
                if (lvt_3_1_.getActivePotionEffects().isEmpty())
                {
                    throw new CommandException("commands.effect.failure.notActive.all", new Object[] {lvt_3_1_.getName()});
                }
                else
                {
                    lvt_3_1_.clearActivePotions();
                    notifyOperators(sender, this, "commands.effect.success.removed.all", new Object[] {lvt_3_1_.getName()});
                }
            }
            else
            {
                int lvt_4_1_;

                try
                {
                    lvt_4_1_ = parseInt(args[1], 1);
                }
                catch (NumberInvalidException var11)
                {
                    Potion lvt_6_1_ = Potion.getPotionFromResourceLocation(args[1]);

                    if (lvt_6_1_ == null)
                    {
                        throw var11;
                    }

                    lvt_4_1_ = lvt_6_1_.id;
                }

                int lvt_5_2_ = 600;
                int lvt_6_2_ = 30;
                int lvt_7_1_ = 0;

                if (lvt_4_1_ >= 0 && lvt_4_1_ < Potion.potionTypes.length && Potion.potionTypes[lvt_4_1_] != null)
                {
                    Potion lvt_8_1_ = Potion.potionTypes[lvt_4_1_];

                    if (args.length >= 3)
                    {
                        lvt_6_2_ = parseInt(args[2], 0, 1000000);

                        if (lvt_8_1_.isInstant())
                        {
                            lvt_5_2_ = lvt_6_2_;
                        }
                        else
                        {
                            lvt_5_2_ = lvt_6_2_ * 20;
                        }
                    }
                    else if (lvt_8_1_.isInstant())
                    {
                        lvt_5_2_ = 1;
                    }

                    if (args.length >= 4)
                    {
                        lvt_7_1_ = parseInt(args[3], 0, 255);
                    }

                    boolean lvt_9_1_ = true;

                    if (args.length >= 5 && "true".equalsIgnoreCase(args[4]))
                    {
                        lvt_9_1_ = false;
                    }

                    if (lvt_6_2_ > 0)
                    {
                        PotionEffect lvt_10_1_ = new PotionEffect(lvt_4_1_, lvt_5_2_, lvt_7_1_, false, lvt_9_1_);
                        lvt_3_1_.addPotionEffect(lvt_10_1_);
                        notifyOperators(sender, this, "commands.effect.success", new Object[] {new ChatComponentTranslation(lvt_10_1_.getEffectName(), new Object[0]), Integer.valueOf(lvt_4_1_), Integer.valueOf(lvt_7_1_), lvt_3_1_.getName(), Integer.valueOf(lvt_6_2_)});
                    }
                    else if (lvt_3_1_.isPotionActive(lvt_4_1_))
                    {
                        lvt_3_1_.removePotionEffect(lvt_4_1_);
                        notifyOperators(sender, this, "commands.effect.success.removed", new Object[] {new ChatComponentTranslation(lvt_8_1_.getName(), new Object[0]), lvt_3_1_.getName()});
                    }
                    else
                    {
                        throw new CommandException("commands.effect.failure.notActive", new Object[] {new ChatComponentTranslation(lvt_8_1_.getName(), new Object[0]), lvt_3_1_.getName()});
                    }
                }
                else
                {
                    throw new NumberInvalidException("commands.effect.notFound", new Object[] {Integer.valueOf(lvt_4_1_)});
                }
            }
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, this.getAllUsernames()) : (args.length == 2 ? getListOfStringsMatchingLastWord(args, Potion.getPotionLocations()) : (args.length == 5 ? getListOfStringsMatchingLastWord(args, new String[] {"true", "false"}): null));
    }

    protected String[] getAllUsernames()
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
