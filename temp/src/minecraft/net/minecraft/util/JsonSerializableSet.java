package net.minecraft.util;

import com.google.common.collect.ForwardingSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.Collection;
import java.util.Set;
import net.minecraft.util.IJsonSerializable;

public class JsonSerializableSet extends ForwardingSet<String> implements IJsonSerializable {
   private final Set<String> field_151004_a = Sets.newHashSet();

   public void func_152753_a(JsonElement p_152753_1_) {
      if(p_152753_1_.isJsonArray()) {
         for(JsonElement lvt_3_1_ : p_152753_1_.getAsJsonArray()) {
            this.add(lvt_3_1_.getAsString());
         }
      }

   }

   public JsonElement func_151003_a() {
      JsonArray lvt_1_1_ = new JsonArray();

      for(String lvt_3_1_ : this) {
         lvt_1_1_.add(new JsonPrimitive(lvt_3_1_));
      }

      return lvt_1_1_;
   }

   protected Set<String> delegate() {
      return this.field_151004_a;
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected Collection delegate() {
      return this.delegate();
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected Object delegate() {
      return this.delegate();
   }
}
