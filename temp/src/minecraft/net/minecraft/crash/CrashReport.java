package net.minecraft.crash;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.world.gen.layer.IntCache;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrashReport {
   private static final Logger field_147150_a = LogManager.getLogger();
   private final String field_71513_a;
   private final Throwable field_71511_b;
   private final CrashReportCategory field_85061_c = new CrashReportCategory(this, "System Details");
   private final List<CrashReportCategory> field_71512_c = Lists.newArrayList();
   private File field_71510_d;
   private boolean field_85059_f = true;
   private StackTraceElement[] field_85060_g = new StackTraceElement[0];

   public CrashReport(String p_i1348_1_, Throwable p_i1348_2_) {
      this.field_71513_a = p_i1348_1_;
      this.field_71511_b = p_i1348_2_;
      this.func_71504_g();
   }

   private void func_71504_g() {
      this.field_85061_c.func_71500_a("Minecraft Version", new Callable<String>() {
         public String call() {
            return "1.8.9";
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      this.field_85061_c.func_71500_a("Operating System", new Callable<String>() {
         public String call() {
            return System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version");
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      this.field_85061_c.func_71500_a("Java Version", new Callable<String>() {
         public String call() {
            return System.getProperty("java.version") + ", " + System.getProperty("java.vendor");
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      this.field_85061_c.func_71500_a("Java VM Version", new Callable<String>() {
         public String call() {
            return System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      this.field_85061_c.func_71500_a("Memory", new Callable<String>() {
         public String call() {
            Runtime lvt_1_1_ = Runtime.getRuntime();
            long lvt_2_1_ = lvt_1_1_.maxMemory();
            long lvt_4_1_ = lvt_1_1_.totalMemory();
            long lvt_6_1_ = lvt_1_1_.freeMemory();
            long lvt_8_1_ = lvt_2_1_ / 1024L / 1024L;
            long lvt_10_1_ = lvt_4_1_ / 1024L / 1024L;
            long lvt_12_1_ = lvt_6_1_ / 1024L / 1024L;
            return lvt_6_1_ + " bytes (" + lvt_12_1_ + " MB) / " + lvt_4_1_ + " bytes (" + lvt_10_1_ + " MB) up to " + lvt_2_1_ + " bytes (" + lvt_8_1_ + " MB)";
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      this.field_85061_c.func_71500_a("JVM Flags", new Callable<String>() {
         public String call() {
            RuntimeMXBean lvt_1_1_ = ManagementFactory.getRuntimeMXBean();
            List<String> lvt_2_1_ = lvt_1_1_.getInputArguments();
            int lvt_3_1_ = 0;
            StringBuilder lvt_4_1_ = new StringBuilder();

            for(String lvt_6_1_ : lvt_2_1_) {
               if(lvt_6_1_.startsWith("-X")) {
                  if(lvt_3_1_++ > 0) {
                     lvt_4_1_.append(" ");
                  }

                  lvt_4_1_.append(lvt_6_1_);
               }
            }

            return String.format("%d total; %s", new Object[]{Integer.valueOf(lvt_3_1_), lvt_4_1_.toString()});
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      this.field_85061_c.func_71500_a("IntCache", new Callable<String>() {
         public String call() throws Exception {
            return IntCache.func_85144_b();
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
   }

   public String func_71501_a() {
      return this.field_71513_a;
   }

   public Throwable func_71505_b() {
      return this.field_71511_b;
   }

   public void func_71506_a(StringBuilder p_71506_1_) {
      if((this.field_85060_g == null || this.field_85060_g.length <= 0) && this.field_71512_c.size() > 0) {
         this.field_85060_g = (StackTraceElement[])ArrayUtils.subarray(((CrashReportCategory)this.field_71512_c.get(0)).func_147152_a(), 0, 1);
      }

      if(this.field_85060_g != null && this.field_85060_g.length > 0) {
         p_71506_1_.append("-- Head --\n");
         p_71506_1_.append("Stacktrace:\n");

         for(StackTraceElement lvt_5_1_ : this.field_85060_g) {
            p_71506_1_.append("\t").append("at ").append(lvt_5_1_.toString());
            p_71506_1_.append("\n");
         }

         p_71506_1_.append("\n");
      }

      for(CrashReportCategory lvt_3_2_ : this.field_71512_c) {
         lvt_3_2_.func_85072_a(p_71506_1_);
         p_71506_1_.append("\n\n");
      }

      this.field_85061_c.func_85072_a(p_71506_1_);
   }

   public String func_71498_d() {
      StringWriter lvt_1_1_ = null;
      PrintWriter lvt_2_1_ = null;
      Throwable lvt_3_1_ = this.field_71511_b;
      if(lvt_3_1_.getMessage() == null) {
         if(lvt_3_1_ instanceof NullPointerException) {
            lvt_3_1_ = new NullPointerException(this.field_71513_a);
         } else if(lvt_3_1_ instanceof StackOverflowError) {
            lvt_3_1_ = new StackOverflowError(this.field_71513_a);
         } else if(lvt_3_1_ instanceof OutOfMemoryError) {
            lvt_3_1_ = new OutOfMemoryError(this.field_71513_a);
         }

         lvt_3_1_.setStackTrace(this.field_71511_b.getStackTrace());
      }

      String lvt_4_1_ = lvt_3_1_.toString();

      try {
         lvt_1_1_ = new StringWriter();
         lvt_2_1_ = new PrintWriter(lvt_1_1_);
         lvt_3_1_.printStackTrace(lvt_2_1_);
         lvt_4_1_ = lvt_1_1_.toString();
      } finally {
         IOUtils.closeQuietly(lvt_1_1_);
         IOUtils.closeQuietly(lvt_2_1_);
      }

      return lvt_4_1_;
   }

   public String func_71502_e() {
      StringBuilder lvt_1_1_ = new StringBuilder();
      lvt_1_1_.append("---- Minecraft Crash Report ----\n");
      lvt_1_1_.append("// ");
      lvt_1_1_.append(func_71503_h());
      lvt_1_1_.append("\n\n");
      lvt_1_1_.append("Time: ");
      lvt_1_1_.append((new SimpleDateFormat()).format(new Date()));
      lvt_1_1_.append("\n");
      lvt_1_1_.append("Description: ");
      lvt_1_1_.append(this.field_71513_a);
      lvt_1_1_.append("\n\n");
      lvt_1_1_.append(this.func_71498_d());
      lvt_1_1_.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");

      for(int lvt_2_1_ = 0; lvt_2_1_ < 87; ++lvt_2_1_) {
         lvt_1_1_.append("-");
      }

      lvt_1_1_.append("\n\n");
      this.func_71506_a(lvt_1_1_);
      return lvt_1_1_.toString();
   }

   public File func_71497_f() {
      return this.field_71510_d;
   }

   public boolean func_147149_a(File p_147149_1_) {
      if(this.field_71510_d != null) {
         return false;
      } else {
         if(p_147149_1_.getParentFile() != null) {
            p_147149_1_.getParentFile().mkdirs();
         }

         try {
            FileWriter lvt_2_1_ = new FileWriter(p_147149_1_);
            lvt_2_1_.write(this.func_71502_e());
            lvt_2_1_.close();
            this.field_71510_d = p_147149_1_;
            return true;
         } catch (Throwable var3) {
            field_147150_a.error("Could not save crash report to " + p_147149_1_, var3);
            return false;
         }
      }
   }

   public CrashReportCategory func_85056_g() {
      return this.field_85061_c;
   }

   public CrashReportCategory func_85058_a(String p_85058_1_) {
      return this.func_85057_a(p_85058_1_, 1);
   }

   public CrashReportCategory func_85057_a(String p_85057_1_, int p_85057_2_) {
      CrashReportCategory lvt_3_1_ = new CrashReportCategory(this, p_85057_1_);
      if(this.field_85059_f) {
         int lvt_4_1_ = lvt_3_1_.func_85073_a(p_85057_2_);
         StackTraceElement[] lvt_5_1_ = this.field_71511_b.getStackTrace();
         StackTraceElement lvt_6_1_ = null;
         StackTraceElement lvt_7_1_ = null;
         int lvt_8_1_ = lvt_5_1_.length - lvt_4_1_;
         if(lvt_8_1_ < 0) {
            System.out.println("Negative index in crash report handler (" + lvt_5_1_.length + "/" + lvt_4_1_ + ")");
         }

         if(lvt_5_1_ != null && 0 <= lvt_8_1_ && lvt_8_1_ < lvt_5_1_.length) {
            lvt_6_1_ = lvt_5_1_[lvt_8_1_];
            if(lvt_5_1_.length + 1 - lvt_4_1_ < lvt_5_1_.length) {
               lvt_7_1_ = lvt_5_1_[lvt_5_1_.length + 1 - lvt_4_1_];
            }
         }

         this.field_85059_f = lvt_3_1_.func_85069_a(lvt_6_1_, lvt_7_1_);
         if(lvt_4_1_ > 0 && !this.field_71512_c.isEmpty()) {
            CrashReportCategory lvt_9_1_ = (CrashReportCategory)this.field_71512_c.get(this.field_71512_c.size() - 1);
            lvt_9_1_.func_85070_b(lvt_4_1_);
         } else if(lvt_5_1_ != null && lvt_5_1_.length >= lvt_4_1_ && 0 <= lvt_8_1_ && lvt_8_1_ < lvt_5_1_.length) {
            this.field_85060_g = new StackTraceElement[lvt_8_1_];
            System.arraycopy(lvt_5_1_, 0, this.field_85060_g, 0, this.field_85060_g.length);
         } else {
            this.field_85059_f = false;
         }
      }

      this.field_71512_c.add(lvt_3_1_);
      return lvt_3_1_;
   }

   private static String func_71503_h() {
      String[] lvt_0_1_ = new String[]{"Who set us up the TNT?", "Everything\'s going to plan. No, really, that was supposed to happen.", "Uh... Did I do that?", "Oops.", "Why did you do that?", "I feel sad now :(", "My bad.", "I\'m sorry, Dave.", "I let you down. Sorry :(", "On the bright side, I bought you a teddy bear!", "Daisy, daisy...", "Oh - I know what I did wrong!", "Hey, that tickles! Hehehe!", "I blame Dinnerbone.", "You should try our sister game, Minceraft!", "Don\'t be sad. I\'ll do better next time, I promise!", "Don\'t be sad, have a hug! <3", "I just don\'t know what went wrong :(", "Shall we play a game?", "Quite honestly, I wouldn\'t worry myself about that.", "I bet Cylons wouldn\'t have this problem.", "Sorry :(", "Surprise! Haha. Well, this is awkward.", "Would you like a cupcake?", "Hi. I\'m Minecraft, and I\'m a crashaholic.", "Ooh. Shiny.", "This doesn\'t make any sense!", "Why is it breaking :(", "Don\'t do that.", "Ouch. That hurt :(", "You\'re mean.", "This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]", "There are four lights!", "But it works on my machine."};

      try {
         return lvt_0_1_[(int)(System.nanoTime() % (long)lvt_0_1_.length)];
      } catch (Throwable var2) {
         return "Witty comment unavailable :(";
      }
   }

   public static CrashReport func_85055_a(Throwable p_85055_0_, String p_85055_1_) {
      CrashReport lvt_2_1_;
      if(p_85055_0_ instanceof ReportedException) {
         lvt_2_1_ = ((ReportedException)p_85055_0_).func_71575_a();
      } else {
         lvt_2_1_ = new CrashReport(p_85055_1_, p_85055_0_);
      }

      return lvt_2_1_;
   }
}
