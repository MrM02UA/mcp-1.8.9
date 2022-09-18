package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import net.minecraft.server.management.UserList;
import net.minecraft.server.management.UserListBansEntry;
import net.minecraft.server.management.UserListEntry;

public class UserListBans extends UserList<GameProfile, UserListBansEntry> {
   public UserListBans(File p_i1138_1_) {
      super(p_i1138_1_);
   }

   protected UserListEntry<GameProfile> func_152682_a(JsonObject p_152682_1_) {
      return new UserListBansEntry(p_152682_1_);
   }

   public boolean func_152702_a(GameProfile p_152702_1_) {
      return this.func_152692_d(p_152702_1_);
   }

   public String[] func_152685_a() {
      String[] lvt_1_1_ = new String[this.func_152688_e().size()];
      int lvt_2_1_ = 0;

      for(UserListBansEntry lvt_4_1_ : this.func_152688_e().values()) {
         lvt_1_1_[lvt_2_1_++] = ((GameProfile)lvt_4_1_.func_152640_f()).getName();
      }

      return lvt_1_1_;
   }

   protected String func_152681_a(GameProfile p_152681_1_) {
      return p_152681_1_.getId().toString();
   }

   public GameProfile func_152703_a(String p_152703_1_) {
      for(UserListBansEntry lvt_3_1_ : this.func_152688_e().values()) {
         if(p_152703_1_.equalsIgnoreCase(((GameProfile)lvt_3_1_.func_152640_f()).getName())) {
            return (GameProfile)lvt_3_1_.func_152640_f();
         }
      }

      return null;
   }

   // $FF: synthetic method
   protected String func_152681_a(Object p_152681_1_) {
      return this.func_152681_a((GameProfile)p_152681_1_);
   }
}
