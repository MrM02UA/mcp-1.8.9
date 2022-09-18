package net.minecraft.command;

import com.google.gson.JsonParseException;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentProcessor;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandTitle extends CommandBase
{
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "title";
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
        return "commands.title.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException("commands.title.usage", new Object[0]);
        }
        else
        {
            if (args.length < 3)
            {
                if ("title".equals(args[1]) || "subtitle".equals(args[1]))
                {
                    throw new WrongUsageException("commands.title.usage.title", new Object[0]);
                }

                if ("times".equals(args[1]))
                {
                    throw new WrongUsageException("commands.title.usage.times", new Object[0]);
                }
            }

            EntityPlayerMP lvt_3_1_ = getPlayer(sender, args[0]);
            S45PacketTitle.Type lvt_4_1_ = S45PacketTitle.Type.byName(args[1]);

            if (lvt_4_1_ != S45PacketTitle.Type.CLEAR && lvt_4_1_ != S45PacketTitle.Type.RESET)
            {
                if (lvt_4_1_ == S45PacketTitle.Type.TIMES)
                {
                    if (args.length != 5)
                    {
                        throw new WrongUsageException("commands.title.usage", new Object[0]);
                    }
                    else
                    {
                        int lvt_5_2_ = parseInt(args[2]);
                        int lvt_6_1_ = parseInt(args[3]);
                        int lvt_7_1_ = parseInt(args[4]);
                        S45PacketTitle lvt_8_1_ = new S45PacketTitle(lvt_5_2_, lvt_6_1_, lvt_7_1_);
                        lvt_3_1_.playerNetServerHandler.sendPacket(lvt_8_1_);
                        notifyOperators(sender, this, "commands.title.success", new Object[0]);
                    }
                }
                else if (args.length < 3)
                {
                    throw new WrongUsageException("commands.title.usage", new Object[0]);
                }
                else
                {
                    String lvt_5_3_ = buildString(args, 2);
                    IChatComponent lvt_6_2_;

                    try
                    {
                        lvt_6_2_ = IChatComponent.Serializer.jsonToComponent(lvt_5_3_);
                    }
                    catch (JsonParseException var9)
                    {
                        Throwable lvt_8_2_ = ExceptionUtils.getRootCause(var9);
                        throw new SyntaxErrorException("commands.tellraw.jsonException", new Object[] {lvt_8_2_ == null ? "" : lvt_8_2_.getMessage()});
                    }

                    S45PacketTitle lvt_7_3_ = new S45PacketTitle(lvt_4_1_, ChatComponentProcessor.processComponent(sender, lvt_6_2_, lvt_3_1_));
                    lvt_3_1_.playerNetServerHandler.sendPacket(lvt_7_3_);
                    notifyOperators(sender, this, "commands.title.success", new Object[0]);
                }
            }
            else if (args.length != 2)
            {
                throw new WrongUsageException("commands.title.usage", new Object[0]);
            }
            else
            {
                S45PacketTitle lvt_5_1_ = new S45PacketTitle(lvt_4_1_, (IChatComponent)null);
                lvt_3_1_.playerNetServerHandler.sendPacket(lvt_5_1_);
                notifyOperators(sender, this, "commands.title.success", new Object[0]);
            }
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : (args.length == 2 ? getListOfStringsMatchingLastWord(args, S45PacketTitle.Type.getNames()) : null);
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 0;
    }
}
