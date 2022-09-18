package net.minecraft.util;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

public abstract class ChatComponentStyle implements IChatComponent
{
    protected List<IChatComponent> siblings = Lists.newArrayList();
    private ChatStyle style;

    /**
     * Appends the given component to the end of this one.
     */
    public IChatComponent appendSibling(IChatComponent component)
    {
        component.getChatStyle().setParentStyle(this.getChatStyle());
        this.siblings.add(component);
        return this;
    }

    public List<IChatComponent> getSiblings()
    {
        return this.siblings;
    }

    /**
     * Appends the given text to the end of this component.
     */
    public IChatComponent appendText(String text)
    {
        return this.appendSibling(new ChatComponentText(text));
    }

    public IChatComponent setChatStyle(ChatStyle style)
    {
        this.style = style;

        for (IChatComponent lvt_3_1_ : this.siblings)
        {
            lvt_3_1_.getChatStyle().setParentStyle(this.getChatStyle());
        }

        return this;
    }

    public ChatStyle getChatStyle()
    {
        if (this.style == null)
        {
            this.style = new ChatStyle();

            for (IChatComponent lvt_2_1_ : this.siblings)
            {
                lvt_2_1_.getChatStyle().setParentStyle(this.style);
            }
        }

        return this.style;
    }

    public Iterator<IChatComponent> iterator()
    {
        return Iterators.concat(Iterators.forArray(new ChatComponentStyle[] {this}), createDeepCopyIterator(this.siblings));
    }

    /**
     * Get the text of this component, <em>and all child components</em>, with all special formatting codes removed.
     */
    public final String getUnformattedText()
    {
        StringBuilder lvt_1_1_ = new StringBuilder();

        for (IChatComponent lvt_3_1_ : this)
        {
            lvt_1_1_.append(lvt_3_1_.getUnformattedTextForChat());
        }

        return lvt_1_1_.toString();
    }

    /**
     * Gets the text of this component, with formatting codes added for rendering.
     */
    public final String getFormattedText()
    {
        StringBuilder lvt_1_1_ = new StringBuilder();

        for (IChatComponent lvt_3_1_ : this)
        {
            lvt_1_1_.append(lvt_3_1_.getChatStyle().getFormattingCode());
            lvt_1_1_.append(lvt_3_1_.getUnformattedTextForChat());
            lvt_1_1_.append(EnumChatFormatting.RESET);
        }

        return lvt_1_1_.toString();
    }

    public static Iterator<IChatComponent> createDeepCopyIterator(Iterable<IChatComponent> components)
    {
        Iterator<IChatComponent> lvt_1_1_ = Iterators.concat(Iterators.transform(components.iterator(), new Function<IChatComponent, Iterator<IChatComponent>>()
        {
            public Iterator<IChatComponent> apply(IChatComponent p_apply_1_)
            {
                return p_apply_1_.iterator();
            }
            public Object apply(Object p_apply_1_)
            {
                return this.apply((IChatComponent)p_apply_1_);
            }
        }));
        lvt_1_1_ = Iterators.transform(lvt_1_1_, new Function<IChatComponent, IChatComponent>()
        {
            public IChatComponent apply(IChatComponent p_apply_1_)
            {
                IChatComponent lvt_2_1_ = p_apply_1_.createCopy();
                lvt_2_1_.setChatStyle(lvt_2_1_.getChatStyle().createDeepCopy());
                return lvt_2_1_;
            }
            public Object apply(Object p_apply_1_)
            {
                return this.apply((IChatComponent)p_apply_1_);
            }
        });
        return lvt_1_1_;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof ChatComponentStyle))
        {
            return false;
        }
        else
        {
            ChatComponentStyle lvt_2_1_ = (ChatComponentStyle)p_equals_1_;
            return this.siblings.equals(lvt_2_1_.siblings) && this.getChatStyle().equals(lvt_2_1_.getChatStyle());
        }
    }

    public int hashCode()
    {
        return 31 * this.style.hashCode() + this.siblings.hashCode();
    }

    public String toString()
    {
        return "BaseComponent{style=" + this.style + ", siblings=" + this.siblings + '}';
    }
}
