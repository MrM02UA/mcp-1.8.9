package net.minecraft.client.entity;

import com.mojang.authlib.GameProfile;
import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

public abstract class AbstractClientPlayer extends EntityPlayer {
   private NetworkPlayerInfo field_175157_a;

   public AbstractClientPlayer(World p_i45074_1_, GameProfile p_i45074_2_) {
      super(p_i45074_1_, p_i45074_2_);
   }

   public boolean func_175149_v() {
      NetworkPlayerInfo lvt_1_1_ = Minecraft.func_71410_x().func_147114_u().func_175102_a(this.func_146103_bH().getId());
      return lvt_1_1_ != null && lvt_1_1_.func_178848_b() == WorldSettings.GameType.SPECTATOR;
   }

   public boolean func_152122_n() {
      return this.func_175155_b() != null;
   }

   protected NetworkPlayerInfo func_175155_b() {
      if(this.field_175157_a == null) {
         this.field_175157_a = Minecraft.func_71410_x().func_147114_u().func_175102_a(this.func_110124_au());
      }

      return this.field_175157_a;
   }

   public boolean func_152123_o() {
      NetworkPlayerInfo lvt_1_1_ = this.func_175155_b();
      return lvt_1_1_ != null && lvt_1_1_.func_178856_e();
   }

   public ResourceLocation func_110306_p() {
      NetworkPlayerInfo lvt_1_1_ = this.func_175155_b();
      return lvt_1_1_ == null?DefaultPlayerSkin.func_177334_a(this.func_110124_au()):lvt_1_1_.func_178837_g();
   }

   public ResourceLocation func_110303_q() {
      NetworkPlayerInfo lvt_1_1_ = this.func_175155_b();
      return lvt_1_1_ == null?null:lvt_1_1_.func_178861_h();
   }

   public static ThreadDownloadImageData func_110304_a(ResourceLocation p_110304_0_, String p_110304_1_) {
      TextureManager lvt_2_1_ = Minecraft.func_71410_x().func_110434_K();
      ITextureObject lvt_3_1_ = lvt_2_1_.func_110581_b(p_110304_0_);
      if(lvt_3_1_ == null) {
         lvt_3_1_ = new ThreadDownloadImageData((File)null, String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", new Object[]{StringUtils.func_76338_a(p_110304_1_)}), DefaultPlayerSkin.func_177334_a(func_175147_b(p_110304_1_)), new ImageBufferDownload());
         lvt_2_1_.func_110579_a(p_110304_0_, lvt_3_1_);
      }

      return (ThreadDownloadImageData)lvt_3_1_;
   }

   public static ResourceLocation func_110311_f(String p_110311_0_) {
      return new ResourceLocation("skins/" + StringUtils.func_76338_a(p_110311_0_));
   }

   public String func_175154_l() {
      NetworkPlayerInfo lvt_1_1_ = this.func_175155_b();
      return lvt_1_1_ == null?DefaultPlayerSkin.func_177332_b(this.func_110124_au()):lvt_1_1_.func_178851_f();
   }

   public float func_175156_o() {
      float lvt_1_1_ = 1.0F;
      if(this.field_71075_bZ.field_75100_b) {
         lvt_1_1_ *= 1.1F;
      }

      IAttributeInstance lvt_2_1_ = this.func_110148_a(SharedMonsterAttributes.field_111263_d);
      lvt_1_1_ = (float)((double)lvt_1_1_ * ((lvt_2_1_.func_111126_e() / (double)this.field_71075_bZ.func_75094_b() + 1.0D) / 2.0D));
      if(this.field_71075_bZ.func_75094_b() == 0.0F || Float.isNaN(lvt_1_1_) || Float.isInfinite(lvt_1_1_)) {
         lvt_1_1_ = 1.0F;
      }

      if(this.func_71039_bw() && this.func_71011_bu().func_77973_b() == Items.field_151031_f) {
         int lvt_3_1_ = this.func_71057_bx();
         float lvt_4_1_ = (float)lvt_3_1_ / 20.0F;
         if(lvt_4_1_ > 1.0F) {
            lvt_4_1_ = 1.0F;
         } else {
            lvt_4_1_ = lvt_4_1_ * lvt_4_1_;
         }

         lvt_1_1_ *= 1.0F - lvt_4_1_ * 0.15F;
      }

      return lvt_1_1_;
   }
}
