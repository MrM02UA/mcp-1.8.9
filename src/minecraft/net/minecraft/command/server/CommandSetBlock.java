package net.minecraft.command.server;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandSetBlock extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "setblock";
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
        return "commands.setblock.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 4)
        {
            throw new WrongUsageException("commands.setblock.usage", new Object[0]);
        }
        else
        {
            sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
            BlockPos lvt_3_1_ = parseBlockPos(sender, args, 0, false);
            Block lvt_4_1_ = CommandBase.getBlockByText(sender, args[3]);
            int lvt_5_1_ = 0;

            if (args.length >= 5)
            {
                lvt_5_1_ = parseInt(args[4], 0, 15);
            }

            World lvt_6_1_ = sender.getEntityWorld();

            if (!lvt_6_1_.isBlockLoaded(lvt_3_1_))
            {
                throw new CommandException("commands.setblock.outOfWorld", new Object[0]);
            }
            else
            {
                NBTTagCompound lvt_7_1_ = new NBTTagCompound();
                boolean lvt_8_1_ = false;

                if (args.length >= 7 && lvt_4_1_.hasTileEntity())
                {
                    String lvt_9_1_ = getChatComponentFromNthArg(sender, args, 6).getUnformattedText();

                    try
                    {
                        lvt_7_1_ = JsonToNBT.getTagFromJson(lvt_9_1_);
                        lvt_8_1_ = true;
                    }
                    catch (NBTException var12)
                    {
                        throw new CommandException("commands.setblock.tagError", new Object[] {var12.getMessage()});
                    }
                }

                if (args.length >= 6)
                {
                    if (args[5].equals("destroy"))
                    {
                        lvt_6_1_.destroyBlock(lvt_3_1_, true);

                        if (lvt_4_1_ == Blocks.air)
                        {
                            notifyOperators(sender, this, "commands.setblock.success", new Object[0]);
                            return;
                        }
                    }
                    else if (args[5].equals("keep") && !lvt_6_1_.isAirBlock(lvt_3_1_))
                    {
                        throw new CommandException("commands.setblock.noChange", new Object[0]);
                    }
                }

                TileEntity lvt_9_2_ = lvt_6_1_.getTileEntity(lvt_3_1_);

                if (lvt_9_2_ != null)
                {
                    if (lvt_9_2_ instanceof IInventory)
                    {
                        ((IInventory)lvt_9_2_).clear();
                    }

                    lvt_6_1_.setBlockState(lvt_3_1_, Blocks.air.getDefaultState(), lvt_4_1_ == Blocks.air ? 2 : 4);
                }

                IBlockState lvt_10_2_ = lvt_4_1_.getStateFromMeta(lvt_5_1_);

                if (!lvt_6_1_.setBlockState(lvt_3_1_, lvt_10_2_, 2))
                {
                    throw new CommandException("commands.setblock.noChange", new Object[0]);
                }
                else
                {
                    if (lvt_8_1_)
                    {
                        TileEntity lvt_11_1_ = lvt_6_1_.getTileEntity(lvt_3_1_);

                        if (lvt_11_1_ != null)
                        {
                            lvt_7_1_.setInteger("x", lvt_3_1_.getX());
                            lvt_7_1_.setInteger("y", lvt_3_1_.getY());
                            lvt_7_1_.setInteger("z", lvt_3_1_.getZ());
                            lvt_11_1_.readFromNBT(lvt_7_1_);
                        }
                    }

                    lvt_6_1_.notifyNeighborsRespectDebug(lvt_3_1_, lvt_10_2_.getBlock());
                    sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 1);
                    notifyOperators(sender, this, "commands.setblock.success", new Object[0]);
                }
            }
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length > 0 && args.length <= 3 ? func_175771_a(args, 0, pos) : (args.length == 4 ? getListOfStringsMatchingLastWord(args, Block.blockRegistry.getKeys()) : (args.length == 6 ? getListOfStringsMatchingLastWord(args, new String[] {"replace", "destroy", "keep"}): null));
    }
}
