package net.minecraft.util;

import com.google.common.collect.ForwardingSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.Collection;
import java.util.Set;

public class JsonSerializableSet extends ForwardingSet<String> implements IJsonSerializable
{
    private final Set<String> underlyingSet = Sets.newHashSet();

    public void fromJson(JsonElement json)
    {
        if (json.isJsonArray())
        {
            for (JsonElement lvt_3_1_ : json.getAsJsonArray())
            {
                this.add(lvt_3_1_.getAsString());
            }
        }
    }

    /**
     * Gets the JsonElement that can be serialized.
     */
    public JsonElement getSerializableElement()
    {
        JsonArray lvt_1_1_ = new JsonArray();

        for (String lvt_3_1_ : this)
        {
            lvt_1_1_.add(new JsonPrimitive(lvt_3_1_));
        }

        return lvt_1_1_;
    }

    protected Set<String> delegate()
    {
        return this.underlyingSet;
    }

    protected Collection delegate()
    {
        return this.delegate();
    }

    protected Object delegate()
    {
        return this.delegate();
    }
}
