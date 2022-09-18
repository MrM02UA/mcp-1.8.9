package net.minecraft.command;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandFill extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "fill";
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
        return "commands.fill.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 7)
        {
            throw new WrongUsageException("commands.fill.usage", new Object[0]);
        }
        else
        {
            sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
            BlockPos lvt_3_1_ = parseBlockPos(sender, args, 0, false);
            BlockPos lvt_4_1_ = parseBlockPos(sender, args, 3, false);
            Block lvt_5_1_ = CommandBase.getBlockByText(sender, args[6]);
            int lvt_6_1_ = 0;

            if (args.length >= 8)
            {
                lvt_6_1_ = parseInt(args[7], 0, 15);
            }

            BlockPos lvt_7_1_ = new BlockPos(Math.min(lvt_3_1_.getX(), lvt_4_1_.getX()), Math.min(lvt_3_1_.getY(), lvt_4_1_.getY()), Math.min(lvt_3_1_.getZ(), lvt_4_1_.getZ()));
            BlockPos lvt_8_1_ = new BlockPos(Math.max(lvt_3_1_.getX(), lvt_4_1_.getX()), Math.max(lvt_3_1_.getY(), lvt_4_1_.getY()), Math.max(lvt_3_1_.getZ(), lvt_4_1_.getZ()));
            int lvt_9_1_ = (lvt_8_1_.getX() - lvt_7_1_.getX() + 1) * (lvt_8_1_.getY() - lvt_7_1_.getY() + 1) * (lvt_8_1_.getZ() - lvt_7_1_.getZ() + 1);

            if (lvt_9_1_ > 32768)
            {
                throw new CommandException("commands.fill.tooManyBlocks", new Object[] {Integer.valueOf(lvt_9_1_), Integer.valueOf(32768)});
            }
            else if (lvt_7_1_.getY() >= 0 && lvt_8_1_.getY() < 256)
            {
                World lvt_10_1_ = sender.getEntityWorld();

                for (int lvt_11_1_ = lvt_7_1_.getZ(); lvt_11_1_ < lvt_8_1_.getZ() + 16; lvt_11_1_ += 16)
                {
                    for (int lvt_12_1_ = lvt_7_1_.getX(); lvt_12_1_ < lvt_8_1_.getX() + 16; lvt_12_1_ += 16)
                    {
                        if (!lvt_10_1_.isBlockLoaded(new BlockPos(lvt_12_1_, lvt_8_1_.getY() - lvt_7_1_.getY(), lvt_11_1_)))
                        {
                            throw new CommandException("commands.fill.outOfWorld", new Object[0]);
                        }
                    }
                }

                NBTTagCompound lvt_11_2_ = new NBTTagCompound();
                boolean lvt_12_2_ = false;

                if (args.length >= 10 && lvt_5_1_.hasTileEntity())
                {
                    String lvt_13_1_ = getChatComponentFromNthArg(sender, args, 9).getUnformattedText();

                    try
                    {
                        lvt_11_2_ = JsonToNBT.getTagFromJson(lvt_13_1_);
                        lvt_12_2_ = true;
                    }
                    catch (NBTException var21)
                    {
                        throw new CommandException("commands.fill.tagError", new Object[] {var21.getMessage()});
                    }
                }

                List<BlockPos> lvt_13_2_ = Lists.newArrayList();
                lvt_9_1_ = 0;

                for (int lvt_14_2_ = lvt_7_1_.getZ(); lvt_14_2_ <= lvt_8_1_.getZ(); ++lvt_14_2_)
                {
                    for (int lvt_15_1_ = lvt_7_1_.getY(); lvt_15_1_ <= lvt_8_1_.getY(); ++lvt_15_1_)
                    {
                        for (int lvt_16_1_ = lvt_7_1_.getX(); lvt_16_1_ <= lvt_8_1_.getX(); ++lvt_16_1_)
                        {
                            BlockPos lvt_17_1_ = new BlockPos(lvt_16_1_, lvt_15_1_, lvt_14_2_);

                            if (args.length >= 9)
                            {
                                if (!args[8].equals("outline") && !args[8].equals("hollow"))
                                {
                                    if (args[8].equals("destroy"))
                                    {
                                        lvt_10_1_.destroyBlock(lvt_17_1_, true);
                                    }
                                    else if (args[8].equals("keep"))
                                    {
                                        if (!lvt_10_1_.isAirBlock(lvt_17_1_))
                                        {
                                            continue;
                                        }
                                    }
                                    else if (args[8].equals("replace") && !lvt_5_1_.hasTileEntity())
                                    {
                                        if (args.length > 9)
                                        {
                                            Block lvt_18_1_ = CommandBase.getBlockByText(sender, args[9]);

                                            if (lvt_10_1_.getBlockState(lvt_17_1_).getBlock() != lvt_18_1_)
                                            {
                                                continue;
                                            }
                                        }

                                        if (args.length > 10)
                                        {
                                            int lvt_18_2_ = CommandBase.parseInt(args[10]);
                                            IBlockState lvt_19_1_ = lvt_10_1_.getBlockState(lvt_17_1_);

                                            if (lvt_19_1_.getBlock().getMetaFromState(lvt_19_1_) != lvt_18_2_)
                                            {
                                                continue;
                                            }
                                        }
                                    }
                                }
                                else if (lvt_16_1_ != lvt_7_1_.getX() && lvt_16_1_ != lvt_8_1_.getX() && lvt_15_1_ != lvt_7_1_.getY() && lvt_15_1_ != lvt_8_1_.getY() && lvt_14_2_ != lvt_7_1_.getZ() && lvt_14_2_ != lvt_8_1_.getZ())
                                {
                                    if (args[8].equals("hollow"))
                                    {
                                        lvt_10_1_.setBlockState(lvt_17_1_, Blocks.air.getDefaultState(), 2);
                                        lvt_13_2_.add(lvt_17_1_);
                                    }

                                    continue;
                                }
                            }

                            TileEntity lvt_18_3_ = lvt_10_1_.getTileEntity(lvt_17_1_);

                            if (lvt_18_3_ != null)
                            {
                                if (lvt_18_3_ instanceof IInventory)
                                {
                                    ((IInventory)lvt_18_3_).clear();
                                }

                                lvt_10_1_.setBlockState(lvt_17_1_, Blocks.barrier.getDefaultState(), lvt_5_1_ == Blocks.barrier ? 2 : 4);
                            }

                            IBlockState lvt_19_2_ = lvt_5_1_.getStateFromMeta(lvt_6_1_);

                            if (lvt_10_1_.setBlockState(lvt_17_1_, lvt_19_2_, 2))
                            {
                                lvt_13_2_.add(lvt_17_1_);
                                ++lvt_9_1_;

                                if (lvt_12_2_)
                                {
                                    TileEntity lvt_20_1_ = lvt_10_1_.getTileEntity(lvt_17_1_);

                                    if (lvt_20_1_ != null)
                                    {
                                        lvt_11_2_.setInteger("x", lvt_17_1_.getX());
                                        lvt_11_2_.setInteger("y", lvt_17_1_.getY());
                                        lvt_11_2_.setInteger("z", lvt_17_1_.getZ());
                                        lvt_20_1_.readFromNBT(lvt_11_2_);
                                    }
                                }
                            }
                        }
                    }
                }

                for (BlockPos lvt_15_2_ : lvt_13_2_)
                {
                    Block lvt_16_2_ = lvt_10_1_.getBlockState(lvt_15_2_).getBlock();
                    lvt_10_1_.notifyNeighborsRespectDebug(lvt_15_2_, lvt_16_2_);
                }

                if (lvt_9_1_ <= 0)
                {
                    throw new CommandException("commands.fill.failed", new Object[0]);
                }
                else
                {
                    sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, lvt_9_1_);
                    notifyOperators(sender, this, "commands.fill.success", new Object[] {Integer.valueOf(lvt_9_1_)});
                }
            }
            else
            {
                throw new CommandException("commands.fill.outOfWorld", new Object[0]);
            }
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length > 0 && args.length <= 3 ? func_175771_a(args, 0, pos) : (args.length > 3 && args.length <= 6 ? func_175771_a(args, 3, pos) : (args.length == 7 ? getListOfStringsMatchingLastWord(args, Block.blockRegistry.getKeys()) : (args.length == 9 ? getListOfStringsMatchingLastWord(args, new String[] {"replace", "destroy", "keep", "hollow", "outline"}): (args.length == 10 && "replace".equals(args[8]) ? getListOfStringsMatchingLastWord(args, Block.blockRegistry.getKeys()) : null))));
    }
}
