package net.minecraft.command;

import java.util.List;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class CommandParticle extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "particle";
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
        return "commands.particle.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 8)
        {
            throw new WrongUsageException("commands.particle.usage", new Object[0]);
        }
        else
        {
            boolean lvt_3_1_ = false;
            EnumParticleTypes lvt_4_1_ = null;

            for (EnumParticleTypes lvt_8_1_ : EnumParticleTypes.values())
            {
                if (lvt_8_1_.hasArguments())
                {
                    if (args[0].startsWith(lvt_8_1_.getParticleName()))
                    {
                        lvt_3_1_ = true;
                        lvt_4_1_ = lvt_8_1_;
                        break;
                    }
                }
                else if (args[0].equals(lvt_8_1_.getParticleName()))
                {
                    lvt_3_1_ = true;
                    lvt_4_1_ = lvt_8_1_;
                    break;
                }
            }

            if (!lvt_3_1_)
            {
                throw new CommandException("commands.particle.notFound", new Object[] {args[0]});
            }
            else
            {
                String lvt_5_2_ = args[0];
                Vec3 lvt_6_2_ = sender.getPositionVector();
                double lvt_7_2_ = (double)((float)parseDouble(lvt_6_2_.xCoord, args[1], true));
                double lvt_9_1_ = (double)((float)parseDouble(lvt_6_2_.yCoord, args[2], true));
                double lvt_11_1_ = (double)((float)parseDouble(lvt_6_2_.zCoord, args[3], true));
                double lvt_13_1_ = (double)((float)parseDouble(args[4]));
                double lvt_15_1_ = (double)((float)parseDouble(args[5]));
                double lvt_17_1_ = (double)((float)parseDouble(args[6]));
                double lvt_19_1_ = (double)((float)parseDouble(args[7]));
                int lvt_21_1_ = 0;

                if (args.length > 8)
                {
                    lvt_21_1_ = parseInt(args[8], 0);
                }

                boolean lvt_22_1_ = false;

                if (args.length > 9 && "force".equals(args[9]))
                {
                    lvt_22_1_ = true;
                }

                World lvt_23_1_ = sender.getEntityWorld();

                if (lvt_23_1_ instanceof WorldServer)
                {
                    WorldServer lvt_24_1_ = (WorldServer)lvt_23_1_;
                    int[] lvt_25_1_ = new int[lvt_4_1_.getArgumentCount()];

                    if (lvt_4_1_.hasArguments())
                    {
                        String[] lvt_26_1_ = args[0].split("_", 3);

                        for (int lvt_27_1_ = 1; lvt_27_1_ < lvt_26_1_.length; ++lvt_27_1_)
                        {
                            try
                            {
                                lvt_25_1_[lvt_27_1_ - 1] = Integer.parseInt(lvt_26_1_[lvt_27_1_]);
                            }
                            catch (NumberFormatException var29)
                            {
                                throw new CommandException("commands.particle.notFound", new Object[] {args[0]});
                            }
                        }
                    }

                    lvt_24_1_.spawnParticle(lvt_4_1_, lvt_22_1_, lvt_7_2_, lvt_9_1_, lvt_11_1_, lvt_21_1_, lvt_13_1_, lvt_15_1_, lvt_17_1_, lvt_19_1_, lvt_25_1_);
                    notifyOperators(sender, this, "commands.particle.success", new Object[] {lvt_5_2_, Integer.valueOf(Math.max(lvt_21_1_, 1))});
                }
            }
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, EnumParticleTypes.getParticleNames()) : (args.length > 1 && args.length <= 4 ? func_175771_a(args, 1, pos) : (args.length == 10 ? getListOfStringsMatchingLastWord(args, new String[] {"normal", "force"}): null));
    }
}
