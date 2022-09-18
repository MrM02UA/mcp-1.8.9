package net.minecraft.command.server;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandTestForBlock extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "testforblock";
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
        return "commands.testforblock.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 4)
        {
            throw new WrongUsageException("commands.testforblock.usage", new Object[0]);
        }
        else
        {
            sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
            BlockPos lvt_3_1_ = parseBlockPos(sender, args, 0, false);
            Block lvt_4_1_ = Block.getBlockFromName(args[3]);

            if (lvt_4_1_ == null)
            {
                throw new NumberInvalidException("commands.setblock.notFound", new Object[] {args[3]});
            }
            else
            {
                int lvt_5_1_ = -1;

                if (args.length >= 5)
                {
                    lvt_5_1_ = parseInt(args[4], -1, 15);
                }

                World lvt_6_1_ = sender.getEntityWorld();

                if (!lvt_6_1_.isBlockLoaded(lvt_3_1_))
                {
                    throw new CommandException("commands.testforblock.outOfWorld", new Object[0]);
                }
                else
                {
                    NBTTagCompound lvt_7_1_ = new NBTTagCompound();
                    boolean lvt_8_1_ = false;

                    if (args.length >= 6 && lvt_4_1_.hasTileEntity())
                    {
                        String lvt_9_1_ = getChatComponentFromNthArg(sender, args, 5).getUnformattedText();

                        try
                        {
                            lvt_7_1_ = JsonToNBT.getTagFromJson(lvt_9_1_);
                            lvt_8_1_ = true;
                        }
                        catch (NBTException var13)
                        {
                            throw new CommandException("commands.setblock.tagError", new Object[] {var13.getMessage()});
                        }
                    }

                    IBlockState lvt_9_2_ = lvt_6_1_.getBlockState(lvt_3_1_);
                    Block lvt_10_2_ = lvt_9_2_.getBlock();

                    if (lvt_10_2_ != lvt_4_1_)
                    {
                        throw new CommandException("commands.testforblock.failed.tile", new Object[] {Integer.valueOf(lvt_3_1_.getX()), Integer.valueOf(lvt_3_1_.getY()), Integer.valueOf(lvt_3_1_.getZ()), lvt_10_2_.getLocalizedName(), lvt_4_1_.getLocalizedName()});
                    }
                    else
                    {
                        if (lvt_5_1_ > -1)
                        {
                            int lvt_11_1_ = lvt_9_2_.getBlock().getMetaFromState(lvt_9_2_);

                            if (lvt_11_1_ != lvt_5_1_)
                            {
                                throw new CommandException("commands.testforblock.failed.data", new Object[] {Integer.valueOf(lvt_3_1_.getX()), Integer.valueOf(lvt_3_1_.getY()), Integer.valueOf(lvt_3_1_.getZ()), Integer.valueOf(lvt_11_1_), Integer.valueOf(lvt_5_1_)});
                            }
                        }

                        if (lvt_8_1_)
                        {
                            TileEntity lvt_11_2_ = lvt_6_1_.getTileEntity(lvt_3_1_);

                            if (lvt_11_2_ == null)
                            {
                                throw new CommandException("commands.testforblock.failed.tileEntity", new Object[] {Integer.valueOf(lvt_3_1_.getX()), Integer.valueOf(lvt_3_1_.getY()), Integer.valueOf(lvt_3_1_.getZ())});
                            }

                            NBTTagCompound lvt_12_1_ = new NBTTagCompound();
                            lvt_11_2_.writeToNBT(lvt_12_1_);

                            if (!NBTUtil.func_181123_a(lvt_7_1_, lvt_12_1_, true))
                            {
                                throw new CommandException("commands.testforblock.failed.nbt", new Object[] {Integer.valueOf(lvt_3_1_.getX()), Integer.valueOf(lvt_3_1_.getY()), Integer.valueOf(lvt_3_1_.getZ())});
                            }
                        }

                        sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 1);
                        notifyOperators(sender, this, "commands.testforblock.success", new Object[] {Integer.valueOf(lvt_3_1_.getX()), Integer.valueOf(lvt_3_1_.getY()), Integer.valueOf(lvt_3_1_.getZ())});
                    }
                }
            }
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length > 0 && args.length <= 3 ? func_175771_a(args, 0, pos) : (args.length == 4 ? getListOfStringsMatchingLastWord(args, Block.blockRegistry.getKeys()) : null);
    }
}
