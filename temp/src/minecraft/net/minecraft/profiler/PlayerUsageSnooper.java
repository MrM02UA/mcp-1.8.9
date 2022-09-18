package net.minecraft.profiler;

import com.google.common.collect.Maps;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.profiler.IPlayerUsage;
import net.minecraft.util.HttpUtil;

public class PlayerUsageSnooper {
   private final Map<String, Object> field_152773_a = Maps.newHashMap();
   private final Map<String, Object> field_152774_b = Maps.newHashMap();
   private final String field_76480_b = UUID.randomUUID().toString();
   private final URL field_76481_c;
   private final IPlayerUsage field_76478_d;
   private final Timer field_76479_e = new Timer("Snooper Timer", true);
   private final Object field_76476_f = new Object();
   private final long field_98224_g;
   private boolean field_76477_g;
   private int field_76483_h;

   public PlayerUsageSnooper(String p_i1563_1_, IPlayerUsage p_i1563_2_, long p_i1563_3_) {
      try {
         this.field_76481_c = new URL("http://snoop.minecraft.net/" + p_i1563_1_ + "?version=" + 2);
      } catch (MalformedURLException var6) {
         throw new IllegalArgumentException();
      }

      this.field_76478_d = p_i1563_2_;
      this.field_98224_g = p_i1563_3_;
   }

   public void func_76463_a() {
      if(!this.field_76477_g) {
         this.field_76477_g = true;
         this.func_152766_h();
         this.field_76479_e.schedule(new TimerTask() {
            public void run() {
               if(PlayerUsageSnooper.this.field_76478_d.func_70002_Q()) {
                  Map<String, Object> lvt_1_1_;
                  synchronized(PlayerUsageSnooper.this.field_76476_f) {
                     lvt_1_1_ = Maps.newHashMap(PlayerUsageSnooper.this.field_152774_b);
                     if(PlayerUsageSnooper.this.field_76483_h == 0) {
                        lvt_1_1_.putAll(PlayerUsageSnooper.this.field_152773_a);
                     }

                     lvt_1_1_.put("snooper_count", Integer.valueOf(PlayerUsageSnooper.this.field_76483_h++));
                     lvt_1_1_.put("snooper_token", PlayerUsageSnooper.this.field_76480_b);
                  }

                  HttpUtil.func_151226_a(PlayerUsageSnooper.this.field_76481_c, lvt_1_1_, true);
               }
            }
         }, 0L, 900000L);
      }
   }

   private void func_152766_h() {
      this.func_76467_g();
      this.func_152768_a("snooper_token", this.field_76480_b);
      this.func_152767_b("snooper_token", this.field_76480_b);
      this.func_152767_b("os_name", System.getProperty("os.name"));
      this.func_152767_b("os_version", System.getProperty("os.version"));
      this.func_152767_b("os_architecture", System.getProperty("os.arch"));
      this.func_152767_b("java_version", System.getProperty("java.version"));
      this.func_152768_a("version", "1.8.9");
      this.field_76478_d.func_70001_b(this);
   }

   private void func_76467_g() {
      RuntimeMXBean lvt_1_1_ = ManagementFactory.getRuntimeMXBean();
      List<String> lvt_2_1_ = lvt_1_1_.getInputArguments();
      int lvt_3_1_ = 0;

      for(String lvt_5_1_ : lvt_2_1_) {
         if(lvt_5_1_.startsWith("-X")) {
            this.func_152768_a("jvm_arg[" + lvt_3_1_++ + "]", lvt_5_1_);
         }
      }

      this.func_152768_a("jvm_args", Integer.valueOf(lvt_3_1_));
   }

   public void func_76471_b() {
      this.func_152767_b("memory_total", Long.valueOf(Runtime.getRuntime().totalMemory()));
      this.func_152767_b("memory_max", Long.valueOf(Runtime.getRuntime().maxMemory()));
      this.func_152767_b("memory_free", Long.valueOf(Runtime.getRuntime().freeMemory()));
      this.func_152767_b("cpu_cores", Integer.valueOf(Runtime.getRuntime().availableProcessors()));
      this.field_76478_d.func_70000_a(this);
   }

   public void func_152768_a(String p_152768_1_, Object p_152768_2_) {
      synchronized(this.field_76476_f) {
         this.field_152774_b.put(p_152768_1_, p_152768_2_);
      }
   }

   public void func_152767_b(String p_152767_1_, Object p_152767_2_) {
      synchronized(this.field_76476_f) {
         this.field_152773_a.put(p_152767_1_, p_152767_2_);
      }
   }

   public Map<String, String> func_76465_c() {
      Map<String, String> lvt_1_1_ = Maps.newLinkedHashMap();
      synchronized(this.field_76476_f) {
         this.func_76471_b();

         for(Entry<String, Object> lvt_4_1_ : this.field_152773_a.entrySet()) {
            lvt_1_1_.put(lvt_4_1_.getKey(), lvt_4_1_.getValue().toString());
         }

         for(Entry<String, Object> lvt_4_2_ : this.field_152774_b.entrySet()) {
            lvt_1_1_.put(lvt_4_2_.getKey(), lvt_4_2_.getValue().toString());
         }

         return lvt_1_1_;
      }
   }

   public boolean func_76468_d() {
      return this.field_76477_g;
   }

   public void func_76470_e() {
      this.field_76479_e.cancel();
   }

   public String func_80006_f() {
      return this.field_76480_b;
   }

   public long func_130105_g() {
      return this.field_98224_g;
   }
}
