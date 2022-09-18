package net.minecraft.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map.Entry;

public interface IChatComponent extends Iterable<IChatComponent>
{
    IChatComponent setChatStyle(ChatStyle style);

    ChatStyle getChatStyle();

    /**
     * Appends the given text to the end of this component.
     */
    IChatComponent appendText(String text);

    /**
     * Appends the given component to the end of this one.
     */
    IChatComponent appendSibling(IChatComponent component);

    /**
     * Gets the text of this component, without any special formatting codes added, for chat.  TODO: why is this two
     * different methods?
     */
    String getUnformattedTextForChat();

    /**
     * Get the text of this component, <em>and all child components</em>, with all special formatting codes removed.
     */
    String getUnformattedText();

    /**
     * Gets the text of this component, with formatting codes added for rendering.
     */
    String getFormattedText();

    List<IChatComponent> getSiblings();

    /**
     * Creates a copy of this component.  Almost a deep copy, except the style is shallow-copied.
     */
    IChatComponent createCopy();

    public static class Serializer implements JsonDeserializer<IChatComponent>, JsonSerializer<IChatComponent>
    {
        private static final Gson GSON;

        public IChatComponent deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            if (p_deserialize_1_.isJsonPrimitive())
            {
                return new ChatComponentText(p_deserialize_1_.getAsString());
            }
            else if (!p_deserialize_1_.isJsonObject())
            {
                if (p_deserialize_1_.isJsonArray())
                {
                    JsonArray lvt_4_2_ = p_deserialize_1_.getAsJsonArray();
                    IChatComponent lvt_5_8_ = null;

                    for (JsonElement lvt_7_3_ : lvt_4_2_)
                    {
                        IChatComponent lvt_8_2_ = this.deserialize(lvt_7_3_, lvt_7_3_.getClass(), p_deserialize_3_);

                        if (lvt_5_8_ == null)
                        {
                            lvt_5_8_ = lvt_8_2_;
                        }
                        else
                        {
                            lvt_5_8_.appendSibling(lvt_8_2_);
                        }
                    }

                    return lvt_5_8_;
                }
                else
                {
                    throw new JsonParseException("Don\'t know how to turn " + p_deserialize_1_.toString() + " into a Component");
                }
            }
            else
            {
                JsonObject lvt_4_1_ = p_deserialize_1_.getAsJsonObject();
                IChatComponent lvt_5_1_;

                if (lvt_4_1_.has("text"))
                {
                    lvt_5_1_ = new ChatComponentText(lvt_4_1_.get("text").getAsString());
                }
                else if (lvt_4_1_.has("translate"))
                {
                    String lvt_6_1_ = lvt_4_1_.get("translate").getAsString();

                    if (lvt_4_1_.has("with"))
                    {
                        JsonArray lvt_7_1_ = lvt_4_1_.getAsJsonArray("with");
                        Object[] lvt_8_1_ = new Object[lvt_7_1_.size()];

                        for (int lvt_9_1_ = 0; lvt_9_1_ < lvt_8_1_.length; ++lvt_9_1_)
                        {
                            lvt_8_1_[lvt_9_1_] = this.deserialize(lvt_7_1_.get(lvt_9_1_), p_deserialize_2_, p_deserialize_3_);

                            if (lvt_8_1_[lvt_9_1_] instanceof ChatComponentText)
                            {
                                ChatComponentText lvt_10_1_ = (ChatComponentText)lvt_8_1_[lvt_9_1_];

                                if (lvt_10_1_.getChatStyle().isEmpty() && lvt_10_1_.getSiblings().isEmpty())
                                {
                                    lvt_8_1_[lvt_9_1_] = lvt_10_1_.getChatComponentText_TextValue();
                                }
                            }
                        }

                        lvt_5_1_ = new ChatComponentTranslation(lvt_6_1_, lvt_8_1_);
                    }
                    else
                    {
                        lvt_5_1_ = new ChatComponentTranslation(lvt_6_1_, new Object[0]);
                    }
                }
                else if (lvt_4_1_.has("score"))
                {
                    JsonObject lvt_6_2_ = lvt_4_1_.getAsJsonObject("score");

                    if (!lvt_6_2_.has("name") || !lvt_6_2_.has("objective"))
                    {
                        throw new JsonParseException("A score component needs a least a name and an objective");
                    }

                    lvt_5_1_ = new ChatComponentScore(JsonUtils.getString(lvt_6_2_, "name"), JsonUtils.getString(lvt_6_2_, "objective"));

                    if (lvt_6_2_.has("value"))
                    {
                        ((ChatComponentScore)lvt_5_1_).setValue(JsonUtils.getString(lvt_6_2_, "value"));
                    }
                }
                else
                {
                    if (!lvt_4_1_.has("selector"))
                    {
                        throw new JsonParseException("Don\'t know how to turn " + p_deserialize_1_.toString() + " into a Component");
                    }

                    lvt_5_1_ = new ChatComponentSelector(JsonUtils.getString(lvt_4_1_, "selector"));
                }

                if (lvt_4_1_.has("extra"))
                {
                    JsonArray lvt_6_3_ = lvt_4_1_.getAsJsonArray("extra");

                    if (lvt_6_3_.size() <= 0)
                    {
                        throw new JsonParseException("Unexpected empty array of components");
                    }

                    for (int lvt_7_2_ = 0; lvt_7_2_ < lvt_6_3_.size(); ++lvt_7_2_)
                    {
                        lvt_5_1_.appendSibling(this.deserialize(lvt_6_3_.get(lvt_7_2_), p_deserialize_2_, p_deserialize_3_));
                    }
                }

                lvt_5_1_.setChatStyle((ChatStyle)p_deserialize_3_.deserialize(p_deserialize_1_, ChatStyle.class));
                return lvt_5_1_;
            }
        }

        private void serializeChatStyle(ChatStyle style, JsonObject object, JsonSerializationContext ctx)
        {
            JsonElement lvt_4_1_ = ctx.serialize(style);

            if (lvt_4_1_.isJsonObject())
            {
                JsonObject lvt_5_1_ = (JsonObject)lvt_4_1_;

                for (Entry<String, JsonElement> lvt_7_1_ : lvt_5_1_.entrySet())
                {
                    object.add((String)lvt_7_1_.getKey(), (JsonElement)lvt_7_1_.getValue());
                }
            }
        }

        public JsonElement serialize(IChatComponent p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_)
        {
            if (p_serialize_1_ instanceof ChatComponentText && p_serialize_1_.getChatStyle().isEmpty() && p_serialize_1_.getSiblings().isEmpty())
            {
                return new JsonPrimitive(((ChatComponentText)p_serialize_1_).getChatComponentText_TextValue());
            }
            else
            {
                JsonObject lvt_4_1_ = new JsonObject();

                if (!p_serialize_1_.getChatStyle().isEmpty())
                {
                    this.serializeChatStyle(p_serialize_1_.getChatStyle(), lvt_4_1_, p_serialize_3_);
                }

                if (!p_serialize_1_.getSiblings().isEmpty())
                {
                    JsonArray lvt_5_1_ = new JsonArray();

                    for (IChatComponent lvt_7_1_ : p_serialize_1_.getSiblings())
                    {
                        lvt_5_1_.add(this.serialize((IChatComponent)lvt_7_1_, lvt_7_1_.getClass(), p_serialize_3_));
                    }

                    lvt_4_1_.add("extra", lvt_5_1_);
                }

                if (p_serialize_1_ instanceof ChatComponentText)
                {
                    lvt_4_1_.addProperty("text", ((ChatComponentText)p_serialize_1_).getChatComponentText_TextValue());
                }
                else if (p_serialize_1_ instanceof ChatComponentTranslation)
                {
                    ChatComponentTranslation lvt_5_2_ = (ChatComponentTranslation)p_serialize_1_;
                    lvt_4_1_.addProperty("translate", lvt_5_2_.getKey());

                    if (lvt_5_2_.getFormatArgs() != null && lvt_5_2_.getFormatArgs().length > 0)
                    {
                        JsonArray lvt_6_2_ = new JsonArray();

                        for (Object lvt_10_1_ : lvt_5_2_.getFormatArgs())
                        {
                            if (lvt_10_1_ instanceof IChatComponent)
                            {
                                lvt_6_2_.add(this.serialize((IChatComponent)((IChatComponent)lvt_10_1_), lvt_10_1_.getClass(), p_serialize_3_));
                            }
                            else
                            {
                                lvt_6_2_.add(new JsonPrimitive(String.valueOf(lvt_10_1_)));
                            }
                        }

                        lvt_4_1_.add("with", lvt_6_2_);
                    }
                }
                else if (p_serialize_1_ instanceof ChatComponentScore)
                {
                    ChatComponentScore lvt_5_3_ = (ChatComponentScore)p_serialize_1_;
                    JsonObject lvt_6_3_ = new JsonObject();
                    lvt_6_3_.addProperty("name", lvt_5_3_.getName());
                    lvt_6_3_.addProperty("objective", lvt_5_3_.getObjective());
                    lvt_6_3_.addProperty("value", lvt_5_3_.getUnformattedTextForChat());
                    lvt_4_1_.add("score", lvt_6_3_);
                }
                else
                {
                    if (!(p_serialize_1_ instanceof ChatComponentSelector))
                    {
                        throw new IllegalArgumentException("Don\'t know how to serialize " + p_serialize_1_ + " as a Component");
                    }

                    ChatComponentSelector lvt_5_4_ = (ChatComponentSelector)p_serialize_1_;
                    lvt_4_1_.addProperty("selector", lvt_5_4_.getSelector());
                }

                return lvt_4_1_;
            }
        }

        public static String componentToJson(IChatComponent component)
        {
            return GSON.toJson(component);
        }

        public static IChatComponent jsonToComponent(String json)
        {
            return (IChatComponent)GSON.fromJson(json, IChatComponent.class);
        }

        public JsonElement serialize(Object p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_)
        {
            return this.serialize((IChatComponent)p_serialize_1_, p_serialize_2_, p_serialize_3_);
        }

        public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
        }

        static
        {
            GsonBuilder lvt_0_1_ = new GsonBuilder();
            lvt_0_1_.registerTypeHierarchyAdapter(IChatComponent.class, new IChatComponent.Serializer());
            lvt_0_1_.registerTypeHierarchyAdapter(ChatStyle.class, new ChatStyle.Serializer());
            lvt_0_1_.registerTypeAdapterFactory(new EnumTypeAdapterFactory());
            GSON = lvt_0_1_.create();
        }
    }
}
