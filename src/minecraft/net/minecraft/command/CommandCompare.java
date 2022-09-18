package net.minecraft.command;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class CommandCompare extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "testforblocks";
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
        return "commands.compare.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 9)
        {
            throw new WrongUsageException("commands.compare.usage", new Object[0]);
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

            if (lvt_8_1_ > 524288)
            {
                throw new CommandException("commands.compare.tooManyBlocks", new Object[] {Integer.valueOf(lvt_8_1_), Integer.valueOf(524288)});
            }
            else if (lvt_6_1_.minY >= 0 && lvt_6_1_.maxY < 256 && lvt_7_1_.minY >= 0 && lvt_7_1_.maxY < 256)
            {
                World lvt_9_1_ = sender.getEntityWorld();

                if (lvt_9_1_.isAreaLoaded(lvt_6_1_) && lvt_9_1_.isAreaLoaded(lvt_7_1_))
                {
                    boolean lvt_10_1_ = false;

                    if (args.length > 9 && args[9].equals("masked"))
                    {
                        lvt_10_1_ = true;
                    }

                    lvt_8_1_ = 0;
                    BlockPos lvt_11_1_ = new BlockPos(lvt_7_1_.minX - lvt_6_1_.minX, lvt_7_1_.minY - lvt_6_1_.minY, lvt_7_1_.minZ - lvt_6_1_.minZ);
                    BlockPos.MutableBlockPos lvt_12_1_ = new BlockPos.MutableBlockPos();
                    BlockPos.MutableBlockPos lvt_13_1_ = new BlockPos.MutableBlockPos();

                    for (int lvt_14_1_ = lvt_6_1_.minZ; lvt_14_1_ <= lvt_6_1_.maxZ; ++lvt_14_1_)
                    {
                        for (int lvt_15_1_ = lvt_6_1_.minY; lvt_15_1_ <= lvt_6_1_.maxY; ++lvt_15_1_)
                        {
                            for (int lvt_16_1_ = lvt_6_1_.minX; lvt_16_1_ <= lvt_6_1_.maxX; ++lvt_16_1_)
                            {
                                lvt_12_1_.set(lvt_16_1_, lvt_15_1_, lvt_14_1_);
                                lvt_13_1_.set(lvt_16_1_ + lvt_11_1_.getX(), lvt_15_1_ + lvt_11_1_.getY(), lvt_14_1_ + lvt_11_1_.getZ());
                                boolean lvt_17_1_ = false;
                                IBlockState lvt_18_1_ = lvt_9_1_.getBlockState(lvt_12_1_);

                                if (!lvt_10_1_ || lvt_18_1_.getBlock() != Blocks.air)
                                {
                                    if (lvt_18_1_ == lvt_9_1_.getBlockState(lvt_13_1_))
                                    {
                                        TileEntity lvt_19_1_ = lvt_9_1_.getTileEntity(lvt_12_1_);
                                        TileEntity lvt_20_1_ = lvt_9_1_.getTileEntity(lvt_13_1_);

                                        if (lvt_19_1_ != null && lvt_20_1_ != null)
                                        {
                                            NBTTagCompound lvt_21_1_ = new NBTTagCompound();
                                            lvt_19_1_.writeToNBT(lvt_21_1_);
                                            lvt_21_1_.removeTag("x");
                                            lvt_21_1_.removeTag("y");
                                            lvt_21_1_.removeTag("z");
                                            NBTTagCompound lvt_22_1_ = new NBTTagCompound();
                                            lvt_20_1_.writeToNBT(lvt_22_1_);
                                            lvt_22_1_.removeTag("x");
                                            lvt_22_1_.removeTag("y");
                                            lvt_22_1_.removeTag("z");

                                            if (!lvt_21_1_.equals(lvt_22_1_))
                                            {
                                                lvt_17_1_ = true;
                                            }
                                        }
                                        else if (lvt_19_1_ != null)
                                        {
                                            lvt_17_1_ = true;
                                        }
                                    }
                                    else
                                    {
                                        lvt_17_1_ = true;
                                    }

                                    ++lvt_8_1_;

                                    if (lvt_17_1_)
                                    {
                                        throw new CommandException("commands.compare.failed", new Object[0]);
                                    }
                                }
                            }
                        }
                    }

                    sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, lvt_8_1_);
                    notifyOperators(sender, this, "commands.compare.success", new Object[] {Integer.valueOf(lvt_8_1_)});
                }
                else
                {
                    throw new CommandException("commands.compare.outOfWorld", new Object[0]);
                }
            }
            else
            {
                throw new CommandException("commands.compare.outOfWorld", new Object[0]);
            }
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length > 0 && args.length <= 3 ? func_175771_a(args, 0, pos) : (args.length > 3 && args.length <= 6 ? func_175771_a(args, 3, pos) : (args.length > 6 && args.length <= 9 ? func_175771_a(args, 6, pos) : (args.length == 10 ? getListOfStringsMatchingLastWord(args, new String[] {"masked", "all"}): null)));
    }
}
