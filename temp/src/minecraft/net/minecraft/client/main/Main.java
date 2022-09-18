package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;
import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.util.Session;

public class Main {
   public static void main(String[] p_main_0_) {
      System.setProperty("java.net.preferIPv4Stack", "true");
      OptionParser lvt_1_1_ = new OptionParser();
      lvt_1_1_.allowsUnrecognizedOptions();
      lvt_1_1_.accepts("demo");
      lvt_1_1_.accepts("fullscreen");
      lvt_1_1_.accepts("checkGlErrors");
      OptionSpec<String> lvt_2_1_ = lvt_1_1_.accepts("server").withRequiredArg();
      OptionSpec<Integer> lvt_3_1_ = lvt_1_1_.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(Integer.valueOf(25565), new Integer[0]);
      OptionSpec<File> lvt_4_1_ = lvt_1_1_.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."), new File[0]);
      OptionSpec<File> lvt_5_1_ = lvt_1_1_.accepts("assetsDir").withRequiredArg().ofType(File.class);
      OptionSpec<File> lvt_6_1_ = lvt_1_1_.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
      OptionSpec<String> lvt_7_1_ = lvt_1_1_.accepts("proxyHost").withRequiredArg();
      OptionSpec<Integer> lvt_8_1_ = lvt_1_1_.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).ofType(Integer.class);
      OptionSpec<String> lvt_9_1_ = lvt_1_1_.accepts("proxyUser").withRequiredArg();
      OptionSpec<String> lvt_10_1_ = lvt_1_1_.accepts("proxyPass").withRequiredArg();
      OptionSpec<String> lvt_11_1_ = lvt_1_1_.accepts("username").withRequiredArg().defaultsTo("Player" + Minecraft.func_71386_F() % 1000L, new String[0]);
      OptionSpec<String> lvt_12_1_ = lvt_1_1_.accepts("uuid").withRequiredArg();
      OptionSpec<String> lvt_13_1_ = lvt_1_1_.accepts("accessToken").withRequiredArg().required();
      OptionSpec<String> lvt_14_1_ = lvt_1_1_.accepts("version").withRequiredArg().required();
      OptionSpec<Integer> lvt_15_1_ = lvt_1_1_.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(Integer.valueOf(854), new Integer[0]);
      OptionSpec<Integer> lvt_16_1_ = lvt_1_1_.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(Integer.valueOf(480), new Integer[0]);
      OptionSpec<String> lvt_17_1_ = lvt_1_1_.accepts("userProperties").withRequiredArg().defaultsTo("{}", new String[0]);
      OptionSpec<String> lvt_18_1_ = lvt_1_1_.accepts("profileProperties").withRequiredArg().defaultsTo("{}", new String[0]);
      OptionSpec<String> lvt_19_1_ = lvt_1_1_.accepts("assetIndex").withRequiredArg();
      OptionSpec<String> lvt_20_1_ = lvt_1_1_.accepts("userType").withRequiredArg().defaultsTo("legacy", new String[0]);
      OptionSpec<String> lvt_21_1_ = lvt_1_1_.nonOptions();
      OptionSet lvt_22_1_ = lvt_1_1_.parse(p_main_0_);
      List<String> lvt_23_1_ = lvt_22_1_.valuesOf(lvt_21_1_);
      if(!lvt_23_1_.isEmpty()) {
         System.out.println("Completely ignored arguments: " + lvt_23_1_);
      }

      String lvt_24_1_ = (String)lvt_22_1_.valueOf(lvt_7_1_);
      Proxy lvt_25_1_ = Proxy.NO_PROXY;
      if(lvt_24_1_ != null) {
         try {
            lvt_25_1_ = new Proxy(Type.SOCKS, new InetSocketAddress(lvt_24_1_, ((Integer)lvt_22_1_.valueOf(lvt_8_1_)).intValue()));
         } catch (Exception var46) {
            ;
         }
      }

      final String lvt_26_1_ = (String)lvt_22_1_.valueOf(lvt_9_1_);
      final String lvt_27_1_ = (String)lvt_22_1_.valueOf(lvt_10_1_);
      if(!lvt_25_1_.equals(Proxy.NO_PROXY) && func_110121_a(lvt_26_1_) && func_110121_a(lvt_27_1_)) {
         Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(lvt_26_1_, lvt_27_1_.toCharArray());
            }
         });
      }

      int lvt_28_1_ = ((Integer)lvt_22_1_.valueOf(lvt_15_1_)).intValue();
      int lvt_29_1_ = ((Integer)lvt_22_1_.valueOf(lvt_16_1_)).intValue();
      boolean lvt_30_1_ = lvt_22_1_.has("fullscreen");
      boolean lvt_31_1_ = lvt_22_1_.has("checkGlErrors");
      boolean lvt_32_1_ = lvt_22_1_.has("demo");
      String lvt_33_1_ = (String)lvt_22_1_.valueOf(lvt_14_1_);
      Gson lvt_34_1_ = (new GsonBuilder()).registerTypeAdapter(PropertyMap.class, new Serializer()).create();
      PropertyMap lvt_35_1_ = (PropertyMap)lvt_34_1_.fromJson((String)lvt_22_1_.valueOf(lvt_17_1_), PropertyMap.class);
      PropertyMap lvt_36_1_ = (PropertyMap)lvt_34_1_.fromJson((String)lvt_22_1_.valueOf(lvt_18_1_), PropertyMap.class);
      File lvt_37_1_ = (File)lvt_22_1_.valueOf(lvt_4_1_);
      File lvt_38_1_ = lvt_22_1_.has(lvt_5_1_)?(File)lvt_22_1_.valueOf(lvt_5_1_):new File(lvt_37_1_, "assets/");
      File lvt_39_1_ = lvt_22_1_.has(lvt_6_1_)?(File)lvt_22_1_.valueOf(lvt_6_1_):new File(lvt_37_1_, "resourcepacks/");
      String lvt_40_1_ = lvt_22_1_.has(lvt_12_1_)?(String)lvt_12_1_.value(lvt_22_1_):(String)lvt_11_1_.value(lvt_22_1_);
      String lvt_41_1_ = lvt_22_1_.has(lvt_19_1_)?(String)lvt_19_1_.value(lvt_22_1_):null;
      String lvt_42_1_ = (String)lvt_22_1_.valueOf(lvt_2_1_);
      Integer lvt_43_1_ = (Integer)lvt_22_1_.valueOf(lvt_3_1_);
      Session lvt_44_1_ = new Session((String)lvt_11_1_.value(lvt_22_1_), lvt_40_1_, (String)lvt_13_1_.value(lvt_22_1_), (String)lvt_20_1_.value(lvt_22_1_));
      GameConfiguration lvt_45_1_ = new GameConfiguration(new GameConfiguration.UserInformation(lvt_44_1_, lvt_35_1_, lvt_36_1_, lvt_25_1_), new GameConfiguration.DisplayInformation(lvt_28_1_, lvt_29_1_, lvt_30_1_, lvt_31_1_), new GameConfiguration.FolderInformation(lvt_37_1_, lvt_39_1_, lvt_38_1_, lvt_41_1_), new GameConfiguration.GameInformation(lvt_32_1_, lvt_33_1_), new GameConfiguration.ServerInformation(lvt_42_1_, lvt_43_1_.intValue()));
      Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread") {
         public void run() {
            Minecraft.func_71363_D();
         }
      });
      Thread.currentThread().setName("Client thread");
      (new Minecraft(lvt_45_1_)).func_99999_d();
   }

   private static boolean func_110121_a(String p_110121_0_) {
      return p_110121_0_ != null && !p_110121_0_.isEmpty();
   }
}
