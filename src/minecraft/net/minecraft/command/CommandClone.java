package net.minecraft.command;

import com.google.common.collect.Lists;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class CommandClone extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "clone";
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
        return "commands.clone.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 9)
        {
            throw new WrongUsageException("commands.clone.usage", new Object[0]);
        }
        else
        {
            sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
            BlockPos lvt_3_1_ = parseBlockPos(sender, args, 0, false);
            BlockPos lvt_4_1_ = parseBlockPos(sender, args, 3, false);
            BlockPos lvt_5_1_ = parseBlockPos(sender, args, 6, false);
            StructureBoundingBox lvt_6_1_ = new StructureBoundingBox(lvt_3_1_, lvt_4_1_);
            StructureBoundingBox lvt_7_1_ = new StructureBoundingBox(lvt_5_1_, lvt_5_1_.add(lvt_6_1_.func_175896_b()));
            int lvt_8_1_ = lvt_6_1_.getXSize() * lvt_6_1_.getYSize() * lvt_6_1_.getZSize();

            if (lvt_8_1_ > 32768)
            {
                throw new CommandException("commands.clone.tooManyBlocks", new Object[] {Integer.valueOf(lvt_8_1_), Integer.valueOf(32768)});
            }
            else
            {
                boolean lvt_9_1_ = false;
                Block lvt_10_1_ = null;
                int lvt_11_1_ = -1;

                if ((args.length < 11 || !args[10].equals("force") && !args[10].equals("move")) && lvt_6_1_.intersectsWith(lvt_7_1_))
                {
                    throw new CommandException("commands.clone.noOverlap", new Object[0]);
                }
                else
                {
                    if (args.length >= 11 && args[10].equals("move"))
                    {
                        lvt_9_1_ = true;
                    }

                    if (lvt_6_1_.minY >= 0 && lvt_6_1_.maxY < 256 && lvt_7_1_.minY >= 0 && lvt_7_1_.maxY < 256)
                    {
                        World lvt_12_1_ = sender.getEntityWorld();

                        if (lvt_12_1_.isAreaLoaded(lvt_6_1_) && lvt_12_1_.isAreaLoaded(lvt_7_1_))
                        {
                            boolean lvt_13_1_ = false;

                            if (args.length >= 10)
                            {
                                if (args[9].equals("masked"))
                                {
                                    lvt_13_1_ = true;
                                }
                                else if (args[9].equals("filtered"))
                                {
                                    if (args.length < 12)
                                    {
                                        throw new WrongUsageException("commands.clone.usage", new Object[0]);
                                    }

                                    lvt_10_1_ = getBlockByText(sender, args[11]);

                                    if (args.length >= 13)
                                    {
                                        lvt_11_1_ = parseInt(args[12], 0, 15);
                                    }
                                }
                            }

                            List<CommandClone.StaticCloneData> lvt_14_1_ = Lists.newArrayList();
                            List<CommandClone.StaticCloneData> lvt_15_1_ = Lists.newArrayList();
                            List<CommandClone.StaticCloneData> lvt_16_1_ = Lists.newArrayList();
                            LinkedList<BlockPos> lvt_17_1_ = Lists.newLinkedList();
                            BlockPos lvt_18_1_ = new BlockPos(lvt_7_1_.minX - lvt_6_1_.minX, lvt_7_1_.minY - lvt_6_1_.minY, lvt_7_1_.minZ - lvt_6_1_.minZ);

                            for (int lvt_19_1_ = lvt_6_1_.minZ; lvt_19_1_ <= lvt_6_1_.maxZ; ++lvt_19_1_)
                            {
                                for (int lvt_20_1_ = lvt_6_1_.minY; lvt_20_1_ <= lvt_6_1_.maxY; ++lvt_20_1_)
                                {
                                    for (int lvt_21_1_ = lvt_6_1_.minX; lvt_21_1_ <= lvt_6_1_.maxX; ++lvt_21_1_)
                                    {
                                        BlockPos lvt_22_1_ = new BlockPos(lvt_21_1_, lvt_20_1_, lvt_19_1_);
                                        BlockPos lvt_23_1_ = lvt_22_1_.add(lvt_18_1_);
                                        IBlockState lvt_24_1_ = lvt_12_1_.getBlockState(lvt_22_1_);

                                        if ((!lvt_13_1_ || lvt_24_1_.getBlock() != Blocks.air) && (lvt_10_1_ == null || lvt_24_1_.getBlock() == lvt_10_1_ && (lvt_11_1_ < 0 || lvt_24_1_.getBlock().getMetaFromState(lvt_24_1_) == lvt_11_1_)))
                                        {
                                            TileEntity lvt_25_1_ = lvt_12_1_.getTileEntity(lvt_22_1_);

                                            if (lvt_25_1_ != null)
                                            {
                                                NBTTagCompound lvt_26_1_ = new NBTTagCompound();
                                                lvt_25_1_.writeToNBT(lvt_26_1_);
                                                lvt_15_1_.add(new CommandClone.StaticCloneData(lvt_23_1_, lvt_24_1_, lvt_26_1_));
                                                lvt_17_1_.addLast(lvt_22_1_);
                                            }
                                            else if (!lvt_24_1_.getBlock().isFullBlock() && !lvt_24_1_.getBlock().isFullCube())
                                            {
                                                lvt_16_1_.add(new CommandClone.StaticCloneData(lvt_23_1_, lvt_24_1_, (NBTTagCompound)null));
                                                lvt_17_1_.addFirst(lvt_22_1_);
                                            }
                                            else
                                            {
                                                lvt_14_1_.add(new CommandClone.StaticCloneData(lvt_23_1_, lvt_24_1_, (NBTTagCompound)null));
                                                lvt_17_1_.addLast(lvt_22_1_);
                                            }
                                        }
                                    }
                                }
                            }

                            if (lvt_9_1_)
                            {
                                for (BlockPos lvt_20_2_ : lvt_17_1_)
                                {
                                    TileEntity lvt_21_2_ = lvt_12_1_.getTileEntity(lvt_20_2_);

                                    if (lvt_21_2_ instanceof IInventory)
                                    {
                                        ((IInventory)lvt_21_2_).clear();
                                    }

                                    lvt_12_1_.setBlockState(lvt_20_2_, Blocks.barrier.getDefaultState(), 2);
                                }

                                for (BlockPos lvt_20_3_ : lvt_17_1_)
                                {
                                    lvt_12_1_.setBlockState(lvt_20_3_, Blocks.air.getDefaultState(), 3);
                                }
                            }

                            List<CommandClone.StaticCloneData> lvt_19_4_ = Lists.newArrayList();
                            lvt_19_4_.addAll(lvt_14_1_);
                            lvt_19_4_.addAll(lvt_15_1_);
                            lvt_19_4_.addAll(lvt_16_1_);
                            List<CommandClone.StaticCloneData> lvt_20_4_ = Lists.reverse(lvt_19_4_);

                            for (CommandClone.StaticCloneData lvt_22_2_ : lvt_20_4_)
                            {
                                TileEntity lvt_23_2_ = lvt_12_1_.getTileEntity(lvt_22_2_.pos);

                                if (lvt_23_2_ instanceof IInventory)
                                {
                                    ((IInventory)lvt_23_2_).clear();
                                }

                                lvt_12_1_.setBlockState(lvt_22_2_.pos, Blocks.barrier.getDefaultState(), 2);
                            }

                            lvt_8_1_ = 0;

                            for (CommandClone.StaticCloneData lvt_22_3_ : lvt_19_4_)
                            {
                                if (lvt_12_1_.setBlockState(lvt_22_3_.pos, lvt_22_3_.blockState, 2))
                                {
                                    ++lvt_8_1_;
                                }
                            }

                            for (CommandClone.StaticCloneData lvt_22_4_ : lvt_15_1_)
                            {
                                TileEntity lvt_23_3_ = lvt_12_1_.getTileEntity(lvt_22_4_.pos);

                                if (lvt_22_4_.compound != null && lvt_23_3_ != null)
                                {
                                    lvt_22_4_.compound.setInteger("x", lvt_22_4_.pos.getX());
                                    lvt_22_4_.compound.setInteger("y", lvt_22_4_.pos.getY());
                                    lvt_22_4_.compound.setInteger("z", lvt_22_4_.pos.getZ());
                                    lvt_23_3_.readFromNBT(lvt_22_4_.compound);
                                    lvt_23_3_.markDirty();
                                }

                                lvt_12_1_.setBlockState(lvt_22_4_.pos, lvt_22_4_.blockState, 2);
                            }

                            for (CommandClone.StaticCloneData lvt_22_5_ : lvt_20_4_)
                            {
                                lvt_12_1_.notifyNeighborsRespectDebug(lvt_22_5_.pos, lvt_22_5_.blockState.getBlock());
                            }

                            List<NextTickListEntry> lvt_21_7_ = lvt_12_1_.func_175712_a(lvt_6_1_, false);

                            if (lvt_21_7_ != null)
                            {
                                for (NextTickListEntry lvt_23_4_ : lvt_21_7_)
                                {
                                    if (lvt_6_1_.isVecInside(lvt_23_4_.position))
                                    {
                                        BlockPos lvt_24_2_ = lvt_23_4_.position.add(lvt_18_1_);
                                        lvt_12_1_.scheduleBlockUpdate(lvt_24_2_, lvt_23_4_.getBlock(), (int)(lvt_23_4_.scheduledTime - lvt_12_1_.getWorldInfo().getWorldTotalTime()), lvt_23_4_.priority);
                                    }
                                }
                            }

                            if (lvt_8_1_ <= 0)
                            {
                                throw new CommandException("commands.clone.failed", new Object[0]);
                            }
                            else
                            {
                                sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, lvt_8_1_);
                                notifyOperators(sender, this, "commands.clone.success", new Object[] {Integer.valueOf(lvt_8_1_)});
                            }
                        }
                        else
                        {
                            throw new CommandException("commands.clone.outOfWorld", new Object[0]);
                        }
                    }
                    else
                    {
                        throw new CommandException("commands.clone.outOfWorld", new Object[0]);
                    }
                }
            }
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length > 0 && args.length <= 3 ? func_175771_a(args, 0, pos) : (args.length > 3 && args.length <= 6 ? func_175771_a(args, 3, pos) : (args.length > 6 && args.length <= 9 ? func_175771_a(args, 6, pos) : (args.length == 10 ? getListOfStringsMatchingLastWord(args, new String[] {"replace", "masked", "filtered"}): (args.length == 11 ? getListOfStringsMatchingLastWord(args, new String[] {"normal", "force", "move"}): (args.length == 12 && "filtered".equals(args[9]) ? getListOfStringsMatchingLastWord(args, Block.blockRegistry.getKeys()) : null)))));
    }

    static class StaticCloneData
    {
        public final BlockPos pos;
        public final IBlockState blockState;
        public final NBTTagCompound compound;

        public StaticCloneData(BlockPos posIn, IBlockState stateIn, NBTTagCompound compoundIn)
        {
            this.pos = posIn;
            this.blockState = stateIn;
            this.compound = compoundIn;
        }
    }
}
