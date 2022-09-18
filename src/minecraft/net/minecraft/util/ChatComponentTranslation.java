package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatComponentTranslation extends ChatComponentStyle
{
    private final String key;
    private final Object[] formatArgs;
    private final Object syncLock = new Object();
    private long lastTranslationUpdateTimeInMilliseconds = -1L;
    List<IChatComponent> children = Lists.newArrayList();
    public static final Pattern stringVariablePattern = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

    public ChatComponentTranslation(String translationKey, Object... args)
    {
        this.key = translationKey;
        this.formatArgs = args;

        for (Object lvt_6_1_ : args)
        {
            if (lvt_6_1_ instanceof IChatComponent)
            {
                ((IChatComponent)lvt_6_1_).getChatStyle().setParentStyle(this.getChatStyle());
            }
        }
    }

    /**
     * ensures that our children are initialized from the most recent string translation mapping.
     */
    synchronized void ensureInitialized()
    {
        synchronized (this.syncLock)
        {
            long lvt_2_1_ = StatCollector.getLastTranslationUpdateTimeInMilliseconds();

            if (lvt_2_1_ == this.lastTranslationUpdateTimeInMilliseconds)
            {
                return;
            }

            this.lastTranslationUpdateTimeInMilliseconds = lvt_2_1_;
            this.children.clear();
        }

        try
        {
            this.initializeFromFormat(StatCollector.translateToLocal(this.key));
        }
        catch (ChatComponentTranslationFormatException var6)
        {
            this.children.clear();

            try
            {
                this.initializeFromFormat(StatCollector.translateToFallback(this.key));
            }
            catch (ChatComponentTranslationFormatException var5)
            {
                throw var6;
            }
        }
    }

    /**
     * initializes our children from a format string, using the format args to fill in the placeholder variables.
     */
    protected void initializeFromFormat(String format)
    {
        boolean lvt_2_1_ = false;
        Matcher lvt_3_1_ = stringVariablePattern.matcher(format);
        int lvt_4_1_ = 0;
        int lvt_5_1_ = 0;

        try
        {
            int lvt_7_1_;

            for (; lvt_3_1_.find(lvt_5_1_); lvt_5_1_ = lvt_7_1_)
            {
                int lvt_6_1_ = lvt_3_1_.start();
                lvt_7_1_ = lvt_3_1_.end();

                if (lvt_6_1_ > lvt_5_1_)
                {
                    ChatComponentText lvt_8_1_ = new ChatComponentText(String.format(format.substring(lvt_5_1_, lvt_6_1_), new Object[0]));
                    lvt_8_1_.getChatStyle().setParentStyle(this.getChatStyle());
                    this.children.add(lvt_8_1_);
                }

                String lvt_8_2_ = lvt_3_1_.group(2);
                String lvt_9_1_ = format.substring(lvt_6_1_, lvt_7_1_);

                if ("%".equals(lvt_8_2_) && "%%".equals(lvt_9_1_))
                {
                    ChatComponentText lvt_10_1_ = new ChatComponentText("%");
                    lvt_10_1_.getChatStyle().setParentStyle(this.getChatStyle());
                    this.children.add(lvt_10_1_);
                }
                else
                {
                    if (!"s".equals(lvt_8_2_))
                    {
                        throw new ChatComponentTranslationFormatException(this, "Unsupported format: \'" + lvt_9_1_ + "\'");
                    }

                    String lvt_10_2_ = lvt_3_1_.group(1);
                    int lvt_11_1_ = lvt_10_2_ != null ? Integer.parseInt(lvt_10_2_) - 1 : lvt_4_1_++;

                    if (lvt_11_1_ < this.formatArgs.length)
                    {
                        this.children.add(this.getFormatArgumentAsComponent(lvt_11_1_));
                    }
                }
            }

            if (lvt_5_1_ < format.length())
            {
                ChatComponentText lvt_6_2_ = new ChatComponentText(String.format(format.substring(lvt_5_1_), new Object[0]));
                lvt_6_2_.getChatStyle().setParentStyle(this.getChatStyle());
                this.children.add(lvt_6_2_);
            }
        }
        catch (IllegalFormatException var12)
        {
            throw new ChatComponentTranslationFormatException(this, var12);
        }
    }

    private IChatComponent getFormatArgumentAsComponent(int index)
    {
        if (index >= this.formatArgs.length)
        {
            throw new ChatComponentTranslationFormatException(this, index);
        }
        else
        {
            Object lvt_2_1_ = this.formatArgs[index];
            IChatComponent lvt_3_1_;

            if (lvt_2_1_ instanceof IChatComponent)
            {
                lvt_3_1_ = (IChatComponent)lvt_2_1_;
            }
            else
            {
                lvt_3_1_ = new ChatComponentText(lvt_2_1_ == null ? "null" : lvt_2_1_.toString());
                lvt_3_1_.getChatStyle().setParentStyle(this.getChatStyle());
            }

            return lvt_3_1_;
        }
    }

    public IChatComponent setChatStyle(ChatStyle style)
    {
        super.setChatStyle(style);

        for (Object lvt_5_1_ : this.formatArgs)
        {
            if (lvt_5_1_ instanceof IChatComponent)
            {
                ((IChatComponent)lvt_5_1_).getChatStyle().setParentStyle(this.getChatStyle());
            }
        }

        if (this.lastTranslationUpdateTimeInMilliseconds > -1L)
        {
            for (IChatComponent lvt_3_2_ : this.children)
            {
                lvt_3_2_.getChatStyle().setParentStyle(style);
            }
        }

        return this;
    }

    public Iterator<IChatComponent> iterator()
    {
        this.ensureInitialized();
        return Iterators.concat(createDeepCopyIterator(this.children), createDeepCopyIterator(this.siblings));
    }

    /**
     * Gets the text of this component, without any special formatting codes added, for chat.  TODO: why is this two
     * different methods?
     */
    public String getUnformattedTextForChat()
    {
        this.ensureInitialized();
        StringBuilder lvt_1_1_ = new StringBuilder();

        for (IChatComponent lvt_3_1_ : this.children)
        {
            lvt_1_1_.append(lvt_3_1_.getUnformattedTextForChat());
        }

        return lvt_1_1_.toString();
    }

    /**
     * Creates a copy of this component.  Almost a deep copy, except the style is shallow-copied.
     */
    public ChatComponentTranslation createCopy()
    {
        Object[] lvt_1_1_ = new Object[this.formatArgs.length];

        for (int lvt_2_1_ = 0; lvt_2_1_ < this.formatArgs.length; ++lvt_2_1_)
        {
            if (this.formatArgs[lvt_2_1_] instanceof IChatComponent)
            {
                lvt_1_1_[lvt_2_1_] = ((IChatComponent)this.formatArgs[lvt_2_1_]).createCopy();
            }
            else
            {
                lvt_1_1_[lvt_2_1_] = this.formatArgs[lvt_2_1_];
            }
        }

        ChatComponentTranslation lvt_2_2_ = new ChatComponentTranslation(this.key, lvt_1_1_);
        lvt_2_2_.setChatStyle(this.getChatStyle().createShallowCopy());

        for (IChatComponent lvt_4_1_ : this.getSiblings())
        {
            lvt_2_2_.appendSibling(lvt_4_1_.createCopy());
        }

        return lvt_2_2_;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof ChatComponentTranslation))
        {
            return false;
        }
        else
        {
            ChatComponentTranslation lvt_2_1_ = (ChatComponentTranslation)p_equals_1_;
            return Arrays.equals(this.formatArgs, lvt_2_1_.formatArgs) && this.key.equals(lvt_2_1_.key) && super.equals(p_equals_1_);
        }
    }

    public int hashCode()
    {
        int lvt_1_1_ = super.hashCode();
        lvt_1_1_ = 31 * lvt_1_1_ + this.key.hashCode();
        lvt_1_1_ = 31 * lvt_1_1_ + Arrays.hashCode(this.formatArgs);
        return lvt_1_1_;
    }

    public String toString()
    {
        return "TranslatableComponent{key=\'" + this.key + '\'' + ", args=" + Arrays.toString(this.formatArgs) + ", siblings=" + this.siblings + ", style=" + this.getChatStyle() + '}';
    }

    public String getKey()
    {
        return this.key;
    }

    public Object[] getFormatArgs()
    {
        return this.formatArgs;
    }

    /**
     * Creates a copy of this component.  Almost a deep copy, except the style is shallow-copied.
     */
    public IChatComponent createCopy()
    {
        return this.createCopy();
    }
}
