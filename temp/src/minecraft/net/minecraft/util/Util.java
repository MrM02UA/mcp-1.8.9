package net.minecraft.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import org.apache.logging.log4j.Logger;

public class Util {
   public static Util.EnumOS func_110647_a() {
      String lvt_0_1_ = System.getProperty("os.name").toLowerCase();
      return lvt_0_1_.contains("win")?Util.EnumOS.WINDOWS:(lvt_0_1_.contains("mac")?Util.EnumOS.OSX:(lvt_0_1_.contains("solaris")?Util.EnumOS.SOLARIS:(lvt_0_1_.contains("sunos")?Util.EnumOS.SOLARIS:(lvt_0_1_.contains("linux")?Util.EnumOS.LINUX:(lvt_0_1_.contains("unix")?Util.EnumOS.LINUX:Util.EnumOS.UNKNOWN)))));
   }

   public static <V> V func_181617_a(FutureTask<V> p_181617_0_, Logger p_181617_1_) {
      try {
         p_181617_0_.run();
         return (V)p_181617_0_.get();
      } catch (ExecutionException var3) {
         p_181617_1_.fatal("Error executing task", var3);
      } catch (InterruptedException var4) {
         p_181617_1_.fatal("Error executing task", var4);
      }

      return (V)null;
   }

   public static enum EnumOS {
      LINUX,
      SOLARIS,
      WINDOWS,
      OSX,
      UNKNOWN;
   }
}
