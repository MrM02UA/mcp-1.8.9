package net.minecraft.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;

public class ChatStyle
{
    /**
     * The parent of this ChatStyle.  Used for looking up values that this instance does not override.
     */
    private ChatStyle parentStyle;
    private EnumChatFormatting color;
    private Boolean bold;
    private Boolean italic;
    private Boolean underlined;
    private Boolean strikethrough;
    private Boolean obfuscated;
    private ClickEvent chatClickEvent;
    private HoverEvent chatHoverEvent;
    private String insertion;

    /**
     * The base of the ChatStyle hierarchy.  All ChatStyle instances are implicitly children of this.
     */
    private static final ChatStyle rootStyle = new ChatStyle()
    {
        public EnumChatFormatting getColor()
        {
            return null;
        }
        public boolean getBold()
        {
            return false;
        }
        public boolean getItalic()
        {
            return false;
        }
        public boolean getStrikethrough()
        {
            return false;
        }
        public boolean getUnderlined()
        {
            return false;
        }
        public boolean getObfuscated()
        {
            return false;
        }
        public ClickEvent getChatClickEvent()
        {
            return null;
        }
        public HoverEvent getChatHoverEvent()
        {
            return null;
        }
        public String getInsertion()
        {
            return null;
        }
        public ChatStyle setColor(EnumChatFormatting color)
        {
            throw new UnsupportedOperationException();
        }
        public ChatStyle setBold(Boolean boldIn)
        {
            throw new UnsupportedOperationException();
        }
        public ChatStyle setItalic(Boolean italic)
        {
            throw new UnsupportedOperationException();
        }
        public ChatStyle setStrikethrough(Boolean strikethrough)
        {
            throw new UnsupportedOperationException();
        }
        public ChatStyle setUnderlined(Boolean underlined)
        {
            throw new UnsupportedOperationException();
        }
        public ChatStyle setObfuscated(Boolean obfuscated)
        {
            throw new UnsupportedOperationException();
        }
        public ChatStyle setChatClickEvent(ClickEvent event)
        {
            throw new UnsupportedOperationException();
        }
        public ChatStyle setChatHoverEvent(HoverEvent event)
        {
            throw new UnsupportedOperationException();
        }
        public ChatStyle setParentStyle(ChatStyle parent)
        {
            throw new UnsupportedOperationException();
        }
        public String toString()
        {
            return "Style.ROOT";
        }
        public ChatStyle createShallowCopy()
        {
            return this;
        }
        public ChatStyle createDeepCopy()
        {
            return this;
        }
        public String getFormattingCode()
        {
            return "";
        }
    };

    /**
     * Gets the effective color of this ChatStyle.
     */
    public EnumChatFormatting getColor()
    {
        return this.color == null ? this.getParent().getColor() : this.color;
    }

    /**
     * Whether or not text of this ChatStyle should be in bold.
     */
    public boolean getBold()
    {
        return this.bold == null ? this.getParent().getBold() : this.bold.booleanValue();
    }

    /**
     * Whether or not text of this ChatStyle should be italicized.
     */
    public boolean getItalic()
    {
        return this.italic == null ? this.getParent().getItalic() : this.italic.booleanValue();
    }

    /**
     * Whether or not to format text of this ChatStyle using strikethrough.
     */
    public boolean getStrikethrough()
    {
        return this.strikethrough == null ? this.getParent().getStrikethrough() : this.strikethrough.booleanValue();
    }

    /**
     * Whether or not text of this ChatStyle should be underlined.
     */
    public boolean getUnderlined()
    {
        return this.underlined == null ? this.getParent().getUnderlined() : this.underlined.booleanValue();
    }

    /**
     * Whether or not text of this ChatStyle should be obfuscated.
     */
    public boolean getObfuscated()
    {
        return this.obfuscated == null ? this.getParent().getObfuscated() : this.obfuscated.booleanValue();
    }

    /**
     * Whether or not this style is empty (inherits everything from the parent).
     */
    public boolean isEmpty()
    {
        return this.bold == null && this.italic == null && this.strikethrough == null && this.underlined == null && this.obfuscated == null && this.color == null && this.chatClickEvent == null && this.chatHoverEvent == null;
    }

    /**
     * The effective chat click event.
     */
    public ClickEvent getChatClickEvent()
    {
        return this.chatClickEvent == null ? this.getParent().getChatClickEvent() : this.chatClickEvent;
    }

    /**
     * The effective chat hover event.
     */
    public HoverEvent getChatHoverEvent()
    {
        return this.chatHoverEvent == null ? this.getParent().getChatHoverEvent() : this.chatHoverEvent;
    }

    /**
     * Get the text to be inserted into Chat when the component is shift-clicked
     */
    public String getInsertion()
    {
        return this.insertion == null ? this.getParent().getInsertion() : this.insertion;
    }

    /**
     * Sets the color for this ChatStyle to the given value.  Only use color values for this; set other values using the
     * specific methods.
     */
    public ChatStyle setColor(EnumChatFormatting color)
    {
        this.color = color;
        return this;
    }

    /**
     * Sets whether or not text of this ChatStyle should be in bold.  Set to false if, e.g., the parent style is bold
     * and you want text of this style to be unbolded.
     */
    public ChatStyle setBold(Boolean boldIn)
    {
        this.bold = boldIn;
        return this;
    }

    /**
     * Sets whether or not text of this ChatStyle should be italicized.  Set to false if, e.g., the parent style is
     * italicized and you want to override that for this style.
     */
    public ChatStyle setItalic(Boolean italic)
    {
        this.italic = italic;
        return this;
    }

    /**
     * Sets whether or not to format text of this ChatStyle using strikethrough.  Set to false if, e.g., the parent
     * style uses strikethrough and you want to override that for this style.
     */
    public ChatStyle setStrikethrough(Boolean strikethrough)
    {
        this.strikethrough = strikethrough;
        return this;
    }

    /**
     * Sets whether or not text of this ChatStyle should be underlined.  Set to false if, e.g., the parent style is
     * underlined and you want to override that for this style.
     */
    public ChatStyle setUnderlined(Boolean underlined)
    {
        this.underlined = underlined;
        return this;
    }

    /**
     * Sets whether or not text of this ChatStyle should be obfuscated.  Set to false if, e.g., the parent style is
     * obfuscated and you want to override that for this style.
     */
    public ChatStyle setObfuscated(Boolean obfuscated)
    {
        this.obfuscated = obfuscated;
        return this;
    }

    /**
     * Sets the event that should be run when text of this ChatStyle is clicked on.
     */
    public ChatStyle setChatClickEvent(ClickEvent event)
    {
        this.chatClickEvent = event;
        return this;
    }

    /**
     * Sets the event that should be run when text of this ChatStyle is hovered over.
     */
    public ChatStyle setChatHoverEvent(HoverEvent event)
    {
        this.chatHoverEvent = event;
        return this;
    }

    /**
     * Set a text to be inserted into Chat when the component is shift-clicked
     */
    public ChatStyle setInsertion(String insertion)
    {
        this.insertion = insertion;
        return this;
    }

    /**
     * Sets the fallback ChatStyle to use if this ChatStyle does not override some value.  Without a parent, obvious
     * defaults are used (bold: false, underlined: false, etc).
     */
    public ChatStyle setParentStyle(ChatStyle parent)
    {
        this.parentStyle = parent;
        return this;
    }

    /**
     * Gets the equivalent text formatting code for this style, without the initial section sign (U+00A7) character.
     */
    public String getFormattingCode()
    {
        if (this.isEmpty())
        {
            return this.parentStyle != null ? this.parentStyle.getFormattingCode() : "";
        }
        else
        {
            StringBuilder lvt_1_1_ = new StringBuilder();

            if (this.getColor() != null)
            {
                lvt_1_1_.append(this.getColor());
            }

            if (this.getBold())
            {
                lvt_1_1_.append(EnumChatFormatting.BOLD);
            }

            if (this.getItalic())
            {
                lvt_1_1_.append(EnumChatFormatting.ITALIC);
            }

            if (this.getUnderlined())
            {
                lvt_1_1_.append(EnumChatFormatting.UNDERLINE);
            }

            if (this.getObfuscated())
            {
                lvt_1_1_.append(EnumChatFormatting.OBFUSCATED);
            }

            if (this.getStrikethrough())
            {
                lvt_1_1_.append(EnumChatFormatting.STRIKETHROUGH);
            }

            return lvt_1_1_.toString();
        }
    }

    /**
     * Gets the immediate parent of this ChatStyle.
     */
    private ChatStyle getParent()
    {
        return this.parentStyle == null ? rootStyle : this.parentStyle;
    }

    public String toString()
    {
        return "Style{hasParent=" + (this.parentStyle != null) + ", color=" + this.color + ", bold=" + this.bold + ", italic=" + this.italic + ", underlined=" + this.underlined + ", obfuscated=" + this.obfuscated + ", clickEvent=" + this.getChatClickEvent() + ", hoverEvent=" + this.getChatHoverEvent() + ", insertion=" + this.getInsertion() + '}';
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof ChatStyle))
        {
            return false;
        }
        else
        {
            boolean var10000;
            label0:
            {
                ChatStyle lvt_2_1_ = (ChatStyle)p_equals_1_;

                if (this.getBold() == lvt_2_1_.getBold() && this.getColor() == lvt_2_1_.getColor() && this.getItalic() == lvt_2_1_.getItalic() && this.getObfuscated() == lvt_2_1_.getObfuscated() && this.getStrikethrough() == lvt_2_1_.getStrikethrough() && this.getUnderlined() == lvt_2_1_.getUnderlined())
                {
                    label85:
                    {
                        if (this.getChatClickEvent() != null)
                        {
                            if (!this.getChatClickEvent().equals(lvt_2_1_.getChatClickEvent()))
                            {
                                break label85;
                            }
                        }
                        else if (lvt_2_1_.getChatClickEvent() != null)
                        {
                            break label85;
                        }

                        if (this.getChatHoverEvent() != null)
                        {
                            if (!this.getChatHoverEvent().equals(lvt_2_1_.getChatHoverEvent()))
                            {
                                break label85;
                            }
                        }
                        else if (lvt_2_1_.getChatHoverEvent() != null)
                        {
                            break label85;
                        }

                        if (this.getInsertion() != null)
                        {
                            if (this.getInsertion().equals(lvt_2_1_.getInsertion()))
                            {
                                break label0;
                            }
                        }
                        else if (lvt_2_1_.getInsertion() == null)
                        {
                            break label0;
                        }
                    }
                }

                var10000 = false;
                return var10000;
            }
            var10000 = true;
            return var10000;
        }
    }

    public int hashCode()
    {
        int lvt_1_1_ = this.color.hashCode();
        lvt_1_1_ = 31 * lvt_1_1_ + this.bold.hashCode();
        lvt_1_1_ = 31 * lvt_1_1_ + this.italic.hashCode();
        lvt_1_1_ = 31 * lvt_1_1_ + this.underlined.hashCode();
        lvt_1_1_ = 31 * lvt_1_1_ + this.strikethrough.hashCode();
        lvt_1_1_ = 31 * lvt_1_1_ + this.obfuscated.hashCode();
        lvt_1_1_ = 31 * lvt_1_1_ + this.chatClickEvent.hashCode();
        lvt_1_1_ = 31 * lvt_1_1_ + this.chatHoverEvent.hashCode();
        lvt_1_1_ = 31 * lvt_1_1_ + this.insertion.hashCode();
        return lvt_1_1_;
    }

    /**
     * Creates a shallow copy of this style.  Changes to this instance's values will not be reflected in the copy, but
     * changes to the parent style's values WILL be reflected in both this instance and the copy, wherever either does
     * not override a value.
     */
    public ChatStyle createShallowCopy()
    {
        ChatStyle lvt_1_1_ = new ChatStyle();
        lvt_1_1_.bold = this.bold;
        lvt_1_1_.italic = this.italic;
        lvt_1_1_.strikethrough = this.strikethrough;
        lvt_1_1_.underlined = this.underlined;
        lvt_1_1_.obfuscated = this.obfuscated;
        lvt_1_1_.color = this.color;
        lvt_1_1_.chatClickEvent = this.chatClickEvent;
        lvt_1_1_.chatHoverEvent = this.chatHoverEvent;
        lvt_1_1_.parentStyle = this.parentStyle;
        lvt_1_1_.insertion = this.insertion;
        return lvt_1_1_;
    }

    /**
     * Creates a deep copy of this style.  No changes to this instance or its parent style will be reflected in the
     * copy.
     */
    public ChatStyle createDeepCopy()
    {
        ChatStyle lvt_1_1_ = new ChatStyle();
        lvt_1_1_.setBold(Boolean.valueOf(this.getBold()));
        lvt_1_1_.setItalic(Boolean.valueOf(this.getItalic()));
        lvt_1_1_.setStrikethrough(Boolean.valueOf(this.getStrikethrough()));
        lvt_1_1_.setUnderlined(Boolean.valueOf(this.getUnderlined()));
        lvt_1_1_.setObfuscated(Boolean.valueOf(this.getObfuscated()));
        lvt_1_1_.setColor(this.getColor());
        lvt_1_1_.setChatClickEvent(this.getChatClickEvent());
        lvt_1_1_.setChatHoverEvent(this.getChatHoverEvent());
        lvt_1_1_.setInsertion(this.getInsertion());
        return lvt_1_1_;
    }

    public static class Serializer implements JsonDeserializer<ChatStyle>, JsonSerializer<ChatStyle>
    {
        public ChatStyle deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            if (p_deserialize_1_.isJsonObject())
            {
                ChatStyle lvt_4_1_ = new ChatStyle();
                JsonObject lvt_5_1_ = p_deserialize_1_.getAsJsonObject();

                if (lvt_5_1_ == null)
                {
                    return null;
                }
                else
                {
                    if (lvt_5_1_.has("bold"))
                    {
                        lvt_4_1_.bold = Boolean.valueOf(lvt_5_1_.get("bold").getAsBoolean());
                    }

                    if (lvt_5_1_.has("italic"))
                    {
                        lvt_4_1_.italic = Boolean.valueOf(lvt_5_1_.get("italic").getAsBoolean());
                    }

                    if (lvt_5_1_.has("underlined"))
                    {
                        lvt_4_1_.underlined = Boolean.valueOf(lvt_5_1_.get("underlined").getAsBoolean());
                    }

                    if (lvt_5_1_.has("strikethrough"))
                    {
                        lvt_4_1_.strikethrough = Boolean.valueOf(lvt_5_1_.get("strikethrough").getAsBoolean());
                    }

                    if (lvt_5_1_.has("obfuscated"))
                    {
                        lvt_4_1_.obfuscated = Boolean.valueOf(lvt_5_1_.get("obfuscated").getAsBoolean());
                    }

                    if (lvt_5_1_.has("color"))
                    {
                        lvt_4_1_.color = (EnumChatFormatting)p_deserialize_3_.deserialize(lvt_5_1_.get("color"), EnumChatFormatting.class);
                    }

                    if (lvt_5_1_.has("insertion"))
                    {
                        lvt_4_1_.insertion = lvt_5_1_.get("insertion").getAsString();
                    }

                    if (lvt_5_1_.has("clickEvent"))
                    {
                        JsonObject lvt_6_1_ = lvt_5_1_.getAsJsonObject("clickEvent");

                        if (lvt_6_1_ != null)
                        {
                            JsonPrimitive lvt_7_1_ = lvt_6_1_.getAsJsonPrimitive("action");
                            ClickEvent.Action lvt_8_1_ = lvt_7_1_ == null ? null : ClickEvent.Action.getValueByCanonicalName(lvt_7_1_.getAsString());
                            JsonPrimitive lvt_9_1_ = lvt_6_1_.getAsJsonPrimitive("value");
                            String lvt_10_1_ = lvt_9_1_ == null ? null : lvt_9_1_.getAsString();

                            if (lvt_8_1_ != null && lvt_10_1_ != null && lvt_8_1_.shouldAllowInChat())
                            {
                                lvt_4_1_.chatClickEvent = new ClickEvent(lvt_8_1_, lvt_10_1_);
                            }
                        }
                    }

                    if (lvt_5_1_.has("hoverEvent"))
                    {
                        JsonObject lvt_6_2_ = lvt_5_1_.getAsJsonObject("hoverEvent");

                        if (lvt_6_2_ != null)
                        {
                            JsonPrimitive lvt_7_2_ = lvt_6_2_.getAsJsonPrimitive("action");
                            HoverEvent.Action lvt_8_2_ = lvt_7_2_ == null ? null : HoverEvent.Action.getValueByCanonicalName(lvt_7_2_.getAsString());
                            IChatComponent lvt_9_2_ = (IChatComponent)p_deserialize_3_.deserialize(lvt_6_2_.get("value"), IChatComponent.class);

                            if (lvt_8_2_ != null && lvt_9_2_ != null && lvt_8_2_.shouldAllowInChat())
                            {
                                lvt_4_1_.chatHoverEvent = new HoverEvent(lvt_8_2_, lvt_9_2_);
                            }
                        }
                    }

                    return lvt_4_1_;
                }
            }
            else
            {
                return null;
            }
        }

        public JsonElement serialize(ChatStyle p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_)
        {
            if (p_serialize_1_.isEmpty())
            {
                return null;
            }
            else
            {
                JsonObject lvt_4_1_ = new JsonObject();

                if (p_serialize_1_.bold != null)
                {
                    lvt_4_1_.addProperty("bold", p_serialize_1_.bold);
                }

                if (p_serialize_1_.italic != null)
                {
                    lvt_4_1_.addProperty("italic", p_serialize_1_.italic);
                }

                if (p_serialize_1_.underlined != null)
                {
                    lvt_4_1_.addProperty("underlined", p_serialize_1_.underlined);
                }

                if (p_serialize_1_.strikethrough != null)
                {
                    lvt_4_1_.addProperty("strikethrough", p_serialize_1_.strikethrough);
                }

                if (p_serialize_1_.obfuscated != null)
                {
                    lvt_4_1_.addProperty("obfuscated", p_serialize_1_.obfuscated);
                }

                if (p_serialize_1_.color != null)
                {
                    lvt_4_1_.add("color", p_serialize_3_.serialize(p_serialize_1_.color));
                }

                if (p_serialize_1_.insertion != null)
                {
                    lvt_4_1_.add("insertion", p_serialize_3_.serialize(p_serialize_1_.insertion));
                }

                if (p_serialize_1_.chatClickEvent != null)
                {
                    JsonObject lvt_5_1_ = new JsonObject();
                    lvt_5_1_.addProperty("action", p_serialize_1_.chatClickEvent.getAction().getCanonicalName());
                    lvt_5_1_.addProperty("value", p_serialize_1_.chatClickEvent.getValue());
                    lvt_4_1_.add("clickEvent", lvt_5_1_);
                }

                if (p_serialize_1_.chatHoverEvent != null)
                {
                    JsonObject lvt_5_2_ = new JsonObject();
                    lvt_5_2_.addProperty("action", p_serialize_1_.chatHoverEvent.getAction().getCanonicalName());
                    lvt_5_2_.add("value", p_serialize_3_.serialize(p_serialize_1_.chatHoverEvent.getValue()));
                    lvt_4_1_.add("hoverEvent", lvt_5_2_);
                }

                return lvt_4_1_;
            }
        }

        public JsonElement serialize(Object p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_)
        {
            return this.serialize((ChatStyle)p_serialize_1_, p_serialize_2_, p_serialize_3_);
        }

        public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
        }
    }
}
