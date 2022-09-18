package net.minecraft.util;

import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.EntityNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.Entity;

public class ChatComponentProcessor
{
    public static IChatComponent processComponent(ICommandSender commandSender, IChatComponent component, Entity entityIn) throws CommandException
    {
        IChatComponent lvt_3_1_ = null;

        if (component instanceof ChatComponentScore)
        {
            ChatComponentScore lvt_4_1_ = (ChatComponentScore)component;
            String lvt_5_1_ = lvt_4_1_.getName();

            if (PlayerSelector.hasArguments(lvt_5_1_))
            {
                List<Entity> lvt_6_1_ = PlayerSelector.<Entity>matchEntities(commandSender, lvt_5_1_, Entity.class);

                if (lvt_6_1_.size() != 1)
                {
                    throw new EntityNotFoundException();
                }

                lvt_5_1_ = ((Entity)lvt_6_1_.get(0)).getName();
            }

            lvt_3_1_ = entityIn != null && lvt_5_1_.equals("*") ? new ChatComponentScore(entityIn.getName(), lvt_4_1_.getObjective()) : new ChatComponentScore(lvt_5_1_, lvt_4_1_.getObjective());
            ((ChatComponentScore)lvt_3_1_).setValue(lvt_4_1_.getUnformattedTextForChat());
        }
        else if (component instanceof ChatComponentSelector)
        {
            String lvt_4_2_ = ((ChatComponentSelector)component).getSelector();
            lvt_3_1_ = PlayerSelector.matchEntitiesToChatComponent(commandSender, lvt_4_2_);

            if (lvt_3_1_ == null)
            {
                lvt_3_1_ = new ChatComponentText("");
            }
        }
        else if (component instanceof ChatComponentText)
        {
            lvt_3_1_ = new ChatComponentText(((ChatComponentText)component).getChatComponentText_TextValue());
        }
        else
        {
            if (!(component instanceof ChatComponentTranslation))
            {
                return component;
            }

            Object[] lvt_4_3_ = ((ChatComponentTranslation)component).getFormatArgs();

            for (int lvt_5_2_ = 0; lvt_5_2_ < lvt_4_3_.length; ++lvt_5_2_)
            {
                Object lvt_6_2_ = lvt_4_3_[lvt_5_2_];

                if (lvt_6_2_ instanceof IChatComponent)
                {
                    lvt_4_3_[lvt_5_2_] = processComponent(commandSender, (IChatComponent)lvt_6_2_, entityIn);
                }
            }

            lvt_3_1_ = new ChatComponentTranslation(((ChatComponentTranslation)component).getKey(), lvt_4_3_);
        }

        ChatStyle lvt_4_4_ = component.getChatStyle();

        if (lvt_4_4_ != null)
        {
            lvt_3_1_.setChatStyle(lvt_4_4_.createShallowCopy());
        }

        for (IChatComponent lvt_6_3_ : component.getSiblings())
        {
            lvt_3_1_.appendSibling(processComponent(commandSender, lvt_6_3_, entityIn));
        }

        return lvt_3_1_;
    }
}
