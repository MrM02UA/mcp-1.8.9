package net.minecraft.command.server;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class CommandTeleport extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "tp";
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
        return "commands.tp.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.tp.usage", new Object[0]);
        }
        else
        {
            int lvt_3_1_ = 0;
            Entity lvt_4_2_;

            if (args.length != 2 && args.length != 4 && args.length != 6)
            {
                lvt_4_2_ = getCommandSenderAsPlayer(sender);
            }
            else
            {
                lvt_4_2_ = getEntity(sender, args[0]);
                lvt_3_1_ = 1;
            }

            if (args.length != 1 && args.length != 2)
            {
                if (args.length < lvt_3_1_ + 3)
                {
                    throw new WrongUsageException("commands.tp.usage", new Object[0]);
                }
                else if (lvt_4_2_.worldObj != null)
                {
                    int lvt_5_2_ = lvt_3_1_ + 1;
                    CommandBase.CoordinateArg lvt_6_1_ = parseCoordinate(lvt_4_2_.posX, args[lvt_3_1_], true);
                    CommandBase.CoordinateArg lvt_7_1_ = parseCoordinate(lvt_4_2_.posY, args[lvt_5_2_++], 0, 0, false);
                    CommandBase.CoordinateArg lvt_8_1_ = parseCoordinate(lvt_4_2_.posZ, args[lvt_5_2_++], true);
                    CommandBase.CoordinateArg lvt_9_1_ = parseCoordinate((double)lvt_4_2_.rotationYaw, args.length > lvt_5_2_ ? args[lvt_5_2_++] : "~", false);
                    CommandBase.CoordinateArg lvt_10_1_ = parseCoordinate((double)lvt_4_2_.rotationPitch, args.length > lvt_5_2_ ? args[lvt_5_2_] : "~", false);

                    if (lvt_4_2_ instanceof EntityPlayerMP)
                    {
                        Set<S08PacketPlayerPosLook.EnumFlags> lvt_11_1_ = EnumSet.noneOf(S08PacketPlayerPosLook.EnumFlags.class);

                        if (lvt_6_1_.func_179630_c())
                        {
                            lvt_11_1_.add(S08PacketPlayerPosLook.EnumFlags.X);
                        }

                        if (lvt_7_1_.func_179630_c())
                        {
                            lvt_11_1_.add(S08PacketPlayerPosLook.EnumFlags.Y);
                        }

                        if (lvt_8_1_.func_179630_c())
                        {
                            lvt_11_1_.add(S08PacketPlayerPosLook.EnumFlags.Z);
                        }

                        if (lvt_10_1_.func_179630_c())
                        {
                            lvt_11_1_.add(S08PacketPlayerPosLook.EnumFlags.X_ROT);
                        }

                        if (lvt_9_1_.func_179630_c())
                        {
                            lvt_11_1_.add(S08PacketPlayerPosLook.EnumFlags.Y_ROT);
                        }

                        float lvt_12_1_ = (float)lvt_9_1_.func_179629_b();

                        if (!lvt_9_1_.func_179630_c())
                        {
                            lvt_12_1_ = MathHelper.wrapAngleTo180_float(lvt_12_1_);
                        }

                        float lvt_13_1_ = (float)lvt_10_1_.func_179629_b();

                        if (!lvt_10_1_.func_179630_c())
                        {
                            lvt_13_1_ = MathHelper.wrapAngleTo180_float(lvt_13_1_);
                        }

                        if (lvt_13_1_ > 90.0F || lvt_13_1_ < -90.0F)
                        {
                            lvt_13_1_ = MathHelper.wrapAngleTo180_float(180.0F - lvt_13_1_);
                            lvt_12_1_ = MathHelper.wrapAngleTo180_float(lvt_12_1_ + 180.0F);
                        }

                        lvt_4_2_.mountEntity((Entity)null);
                        ((EntityPlayerMP)lvt_4_2_).playerNetServerHandler.setPlayerLocation(lvt_6_1_.func_179629_b(), lvt_7_1_.func_179629_b(), lvt_8_1_.func_179629_b(), lvt_12_1_, lvt_13_1_, lvt_11_1_);
                        lvt_4_2_.setRotationYawHead(lvt_12_1_);
                    }
                    else
                    {
                        float lvt_11_2_ = (float)MathHelper.wrapAngleTo180_double(lvt_9_1_.func_179628_a());
                        float lvt_12_2_ = (float)MathHelper.wrapAngleTo180_double(lvt_10_1_.func_179628_a());

                        if (lvt_12_2_ > 90.0F || lvt_12_2_ < -90.0F)
                        {
                            lvt_12_2_ = MathHelper.wrapAngleTo180_float(180.0F - lvt_12_2_);
                            lvt_11_2_ = MathHelper.wrapAngleTo180_float(lvt_11_2_ + 180.0F);
                        }

                        lvt_4_2_.setLocationAndAngles(lvt_6_1_.func_179628_a(), lvt_7_1_.func_179628_a(), lvt_8_1_.func_179628_a(), lvt_11_2_, lvt_12_2_);
                        lvt_4_2_.setRotationYawHead(lvt_11_2_);
                    }

                    notifyOperators(sender, this, "commands.tp.success.coordinates", new Object[] {lvt_4_2_.getName(), Double.valueOf(lvt_6_1_.func_179628_a()), Double.valueOf(lvt_7_1_.func_179628_a()), Double.valueOf(lvt_8_1_.func_179628_a())});
                }
            }
            else
            {
                Entity lvt_5_1_ = getEntity(sender, args[args.length - 1]);

                if (lvt_5_1_.worldObj != lvt_4_2_.worldObj)
                {
                    throw new CommandException("commands.tp.notSameDimension", new Object[0]);
                }
                else
                {
                    lvt_4_2_.mountEntity((Entity)null);

                    if (lvt_4_2_ instanceof EntityPlayerMP)
                    {
                        ((EntityPlayerMP)lvt_4_2_).playerNetServerHandler.setPlayerLocation(lvt_5_1_.posX, lvt_5_1_.posY, lvt_5_1_.posZ, lvt_5_1_.rotationYaw, lvt_5_1_.rotationPitch);
                    }
                    else
                    {
                        lvt_4_2_.setLocationAndAngles(lvt_5_1_.posX, lvt_5_1_.posY, lvt_5_1_.posZ, lvt_5_1_.rotationYaw, lvt_5_1_.rotationPitch);
                    }

                    notifyOperators(sender, this, "commands.tp.success", new Object[] {lvt_4_2_.getName(), lvt_5_1_.getName()});
                }
            }
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length != 1 && args.length != 2 ? null : getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 0;
    }
}
