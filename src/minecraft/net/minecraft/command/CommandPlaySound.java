package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class CommandPlaySound extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "playsound";
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
        return "commands.playsound.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException(this.getCommandUsage(sender), new Object[0]);
        }
        else
        {
            int lvt_3_1_ = 0;
            String lvt_4_1_ = args[lvt_3_1_++];
            EntityPlayerMP lvt_5_1_ = getPlayer(sender, args[lvt_3_1_++]);
            Vec3 lvt_6_1_ = sender.getPositionVector();
            double lvt_7_1_ = lvt_6_1_.xCoord;

            if (args.length > lvt_3_1_)
            {
                lvt_7_1_ = parseDouble(lvt_7_1_, args[lvt_3_1_++], true);
            }

            double lvt_9_1_ = lvt_6_1_.yCoord;

            if (args.length > lvt_3_1_)
            {
                lvt_9_1_ = parseDouble(lvt_9_1_, args[lvt_3_1_++], 0, 0, false);
            }

            double lvt_11_1_ = lvt_6_1_.zCoord;

            if (args.length > lvt_3_1_)
            {
                lvt_11_1_ = parseDouble(lvt_11_1_, args[lvt_3_1_++], true);
            }

            double lvt_13_1_ = 1.0D;

            if (args.length > lvt_3_1_)
            {
                lvt_13_1_ = parseDouble(args[lvt_3_1_++], 0.0D, 3.4028234663852886E38D);
            }

            double lvt_15_1_ = 1.0D;

            if (args.length > lvt_3_1_)
            {
                lvt_15_1_ = parseDouble(args[lvt_3_1_++], 0.0D, 2.0D);
            }

            double lvt_17_1_ = 0.0D;

            if (args.length > lvt_3_1_)
            {
                lvt_17_1_ = parseDouble(args[lvt_3_1_], 0.0D, 1.0D);
            }

            double lvt_19_1_ = lvt_13_1_ > 1.0D ? lvt_13_1_ * 16.0D : 16.0D;
            double lvt_21_1_ = lvt_5_1_.getDistance(lvt_7_1_, lvt_9_1_, lvt_11_1_);

            if (lvt_21_1_ > lvt_19_1_)
            {
                if (lvt_17_1_ <= 0.0D)
                {
                    throw new CommandException("commands.playsound.playerTooFar", new Object[] {lvt_5_1_.getName()});
                }

                double lvt_23_1_ = lvt_7_1_ - lvt_5_1_.posX;
                double lvt_25_1_ = lvt_9_1_ - lvt_5_1_.posY;
                double lvt_27_1_ = lvt_11_1_ - lvt_5_1_.posZ;
                double lvt_29_1_ = Math.sqrt(lvt_23_1_ * lvt_23_1_ + lvt_25_1_ * lvt_25_1_ + lvt_27_1_ * lvt_27_1_);

                if (lvt_29_1_ > 0.0D)
                {
                    lvt_7_1_ = lvt_5_1_.posX + lvt_23_1_ / lvt_29_1_ * 2.0D;
                    lvt_9_1_ = lvt_5_1_.posY + lvt_25_1_ / lvt_29_1_ * 2.0D;
                    lvt_11_1_ = lvt_5_1_.posZ + lvt_27_1_ / lvt_29_1_ * 2.0D;
                }

                lvt_13_1_ = lvt_17_1_;
            }

            lvt_5_1_.playerNetServerHandler.sendPacket(new S29PacketSoundEffect(lvt_4_1_, lvt_7_1_, lvt_9_1_, lvt_11_1_, (float)lvt_13_1_, (float)lvt_15_1_));
            notifyOperators(sender, this, "commands.playsound.success", new Object[] {lvt_4_1_, lvt_5_1_.getName()});
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length == 2 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : (args.length > 2 && args.length <= 5 ? func_175771_a(args, 2, pos) : null);
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 1;
    }
}
