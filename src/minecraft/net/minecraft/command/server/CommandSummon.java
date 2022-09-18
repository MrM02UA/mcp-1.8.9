package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class CommandSummon extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getCommandName()
    {
        return "summon";
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
        return "commands.summon.usage";
    }

    /**
     * Callback when the command is invoked
     */
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException("commands.summon.usage", new Object[0]);
        }
        else
        {
            String lvt_3_1_ = args[0];
            BlockPos lvt_4_1_ = sender.getPosition();
            Vec3 lvt_5_1_ = sender.getPositionVector();
            double lvt_6_1_ = lvt_5_1_.xCoord;
            double lvt_8_1_ = lvt_5_1_.yCoord;
            double lvt_10_1_ = lvt_5_1_.zCoord;

            if (args.length >= 4)
            {
                lvt_6_1_ = parseDouble(lvt_6_1_, args[1], true);
                lvt_8_1_ = parseDouble(lvt_8_1_, args[2], false);
                lvt_10_1_ = parseDouble(lvt_10_1_, args[3], true);
                lvt_4_1_ = new BlockPos(lvt_6_1_, lvt_8_1_, lvt_10_1_);
            }

            World lvt_12_1_ = sender.getEntityWorld();

            if (!lvt_12_1_.isBlockLoaded(lvt_4_1_))
            {
                throw new CommandException("commands.summon.outOfWorld", new Object[0]);
            }
            else if ("LightningBolt".equals(lvt_3_1_))
            {
                lvt_12_1_.addWeatherEffect(new EntityLightningBolt(lvt_12_1_, lvt_6_1_, lvt_8_1_, lvt_10_1_));
                notifyOperators(sender, this, "commands.summon.success", new Object[0]);
            }
            else
            {
                NBTTagCompound lvt_13_1_ = new NBTTagCompound();
                boolean lvt_14_1_ = false;

                if (args.length >= 5)
                {
                    IChatComponent lvt_15_1_ = getChatComponentFromNthArg(sender, args, 4);

                    try
                    {
                        lvt_13_1_ = JsonToNBT.getTagFromJson(lvt_15_1_.getUnformattedText());
                        lvt_14_1_ = true;
                    }
                    catch (NBTException var20)
                    {
                        throw new CommandException("commands.summon.tagError", new Object[] {var20.getMessage()});
                    }
                }

                lvt_13_1_.setString("id", lvt_3_1_);
                Entity lvt_15_2_;

                try
                {
                    lvt_15_2_ = EntityList.createEntityFromNBT(lvt_13_1_, lvt_12_1_);
                }
                catch (RuntimeException var19)
                {
                    throw new CommandException("commands.summon.failed", new Object[0]);
                }

                if (lvt_15_2_ == null)
                {
                    throw new CommandException("commands.summon.failed", new Object[0]);
                }
                else
                {
                    lvt_15_2_.setLocationAndAngles(lvt_6_1_, lvt_8_1_, lvt_10_1_, lvt_15_2_.rotationYaw, lvt_15_2_.rotationPitch);

                    if (!lvt_14_1_ && lvt_15_2_ instanceof EntityLiving)
                    {
                        ((EntityLiving)lvt_15_2_).onInitialSpawn(lvt_12_1_.getDifficultyForLocation(new BlockPos(lvt_15_2_)), (IEntityLivingData)null);
                    }

                    lvt_12_1_.spawnEntityInWorld(lvt_15_2_);
                    Entity lvt_16_3_ = lvt_15_2_;

                    for (NBTTagCompound lvt_17_1_ = lvt_13_1_; lvt_16_3_ != null && lvt_17_1_.hasKey("Riding", 10); lvt_17_1_ = lvt_17_1_.getCompoundTag("Riding"))
                    {
                        Entity lvt_18_1_ = EntityList.createEntityFromNBT(lvt_17_1_.getCompoundTag("Riding"), lvt_12_1_);

                        if (lvt_18_1_ != null)
                        {
                            lvt_18_1_.setLocationAndAngles(lvt_6_1_, lvt_8_1_, lvt_10_1_, lvt_18_1_.rotationYaw, lvt_18_1_.rotationPitch);
                            lvt_12_1_.spawnEntityInWorld(lvt_18_1_);
                            lvt_16_3_.mountEntity(lvt_18_1_);
                        }

                        lvt_16_3_ = lvt_18_1_;
                    }

                    notifyOperators(sender, this, "commands.summon.success", new Object[0]);
                }
            }
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, EntityList.getEntityNameList()) : (args.length > 1 && args.length <= 4 ? func_175771_a(args, 1, pos) : null);
    }
}
