package net.minecraft.profiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Profiler {
   private static final Logger field_151234_b = LogManager.getLogger();
   private final List<String> field_76325_b = Lists.newArrayList();
   private final List<Long> field_76326_c = Lists.newArrayList();
   public boolean field_76327_a;
   private String field_76323_d = "";
   private final Map<String, Long> field_76324_e = Maps.newHashMap();

   public void func_76317_a() {
      this.field_76324_e.clear();
      this.field_76323_d = "";
      this.field_76325_b.clear();
   }

   public void func_76320_a(String p_76320_1_) {
      if(this.field_76327_a) {
         if(this.field_76323_d.length() > 0) {
            this.field_76323_d = this.field_76323_d + ".";
         }

         this.field_76323_d = this.field_76323_d + p_76320_1_;
         this.field_76325_b.add(this.field_76323_d);
         this.field_76326_c.add(Long.valueOf(System.nanoTime()));
      }
   }

   public void func_76319_b() {
      if(this.field_76327_a) {
         long lvt_1_1_ = System.nanoTime();
         long lvt_3_1_ = ((Long)this.field_76326_c.remove(this.field_76326_c.size() - 1)).longValue();
         this.field_76325_b.remove(this.field_76325_b.size() - 1);
         long lvt_5_1_ = lvt_1_1_ - lvt_3_1_;
         if(this.field_76324_e.containsKey(this.field_76323_d)) {
            this.field_76324_e.put(this.field_76323_d, Long.valueOf(((Long)this.field_76324_e.get(this.field_76323_d)).longValue() + lvt_5_1_));
         } else {
            this.field_76324_e.put(this.field_76323_d, Long.valueOf(lvt_5_1_));
         }

         if(lvt_5_1_ > 100000000L) {
            field_151234_b.warn("Something\'s taking too long! \'" + this.field_76323_d + "\' took aprox " + (double)lvt_5_1_ / 1000000.0D + " ms");
         }

         this.field_76323_d = !this.field_76325_b.isEmpty()?(String)this.field_76325_b.get(this.field_76325_b.size() - 1):"";
      }
   }

   public List<Profiler.Result> func_76321_b(String p_76321_1_) {
      if(!this.field_76327_a) {
         return null;
      } else {
         long lvt_3_1_ = this.field_76324_e.containsKey("root")?((Long)this.field_76324_e.get("root")).longValue():0L;
         long lvt_5_1_ = this.field_76324_e.containsKey(p_76321_1_)?((Long)this.field_76324_e.get(p_76321_1_)).longValue():-1L;
         List<Profiler.Result> lvt_7_1_ = Lists.newArrayList();
         if(p_76321_1_.length() > 0) {
            p_76321_1_ = p_76321_1_ + ".";
         }

         long lvt_8_1_ = 0L;

         for(String lvt_11_1_ : this.field_76324_e.keySet()) {
            if(lvt_11_1_.length() > p_76321_1_.length() && lvt_11_1_.startsWith(p_76321_1_) && lvt_11_1_.indexOf(".", p_76321_1_.length() + 1) < 0) {
               lvt_8_1_ += ((Long)this.field_76324_e.get(lvt_11_1_)).longValue();
            }
         }

         float lvt_10_2_ = (float)lvt_8_1_;
         if(lvt_8_1_ < lvt_5_1_) {
            lvt_8_1_ = lvt_5_1_;
         }

         if(lvt_3_1_ < lvt_8_1_) {
            lvt_3_1_ = lvt_8_1_;
         }

         for(String lvt_12_1_ : this.field_76324_e.keySet()) {
            if(lvt_12_1_.length() > p_76321_1_.length() && lvt_12_1_.startsWith(p_76321_1_) && lvt_12_1_.indexOf(".", p_76321_1_.length() + 1) < 0) {
               long lvt_13_1_ = ((Long)this.field_76324_e.get(lvt_12_1_)).longValue();
               double lvt_15_1_ = (double)lvt_13_1_ * 100.0D / (double)lvt_8_1_;
               double lvt_17_1_ = (double)lvt_13_1_ * 100.0D / (double)lvt_3_1_;
               String lvt_19_1_ = lvt_12_1_.substring(p_76321_1_.length());
               lvt_7_1_.add(new Profiler.Result(lvt_19_1_, lvt_15_1_, lvt_17_1_));
            }
         }

         for(String lvt_12_2_ : this.field_76324_e.keySet()) {
            this.field_76324_e.put(lvt_12_2_, Long.valueOf(((Long)this.field_76324_e.get(lvt_12_2_)).longValue() * 999L / 1000L));
         }

         if((float)lvt_8_1_ > lvt_10_2_) {
            lvt_7_1_.add(new Profiler.Result("unspecified", (double)((float)lvt_8_1_ - lvt_10_2_) * 100.0D / (double)lvt_8_1_, (double)((float)lvt_8_1_ - lvt_10_2_) * 100.0D / (double)lvt_3_1_));
         }

         Collections.sort(lvt_7_1_);
         lvt_7_1_.add(0, new Profiler.Result(p_76321_1_, 100.0D, (double)lvt_8_1_ * 100.0D / (double)lvt_3_1_));
         return lvt_7_1_;
      }
   }

   public void func_76318_c(String p_76318_1_) {
      this.func_76319_b();
      this.func_76320_a(p_76318_1_);
   }

   public String func_76322_c() {
      return this.field_76325_b.size() == 0?"[UNKNOWN]":(String)this.field_76325_b.get(this.field_76325_b.size() - 1);
   }

   public static final class Result implements Comparable<Profiler.Result> {
      public double field_76332_a;
      public double field_76330_b;
      public String field_76331_c;

      public Result(String p_i1554_1_, double p_i1554_2_, double p_i1554_4_) {
         this.field_76331_c = p_i1554_1_;
         this.field_76332_a = p_i1554_2_;
         this.field_76330_b = p_i1554_4_;
      }

      public int compareTo(Profiler.Result p_compareTo_1_) {
         return p_compareTo_1_.field_76332_a < this.field_76332_a?-1:(p_compareTo_1_.field_76332_a > this.field_76332_a?1:p_compareTo_1_.field_76331_c.compareTo(this.field_76331_c));
      }

      public int func_76329_a() {
         return (this.field_76331_c.hashCode() & 11184810) + 4473924;
      }

      // $FF: synthetic method
      public int compareTo(Object p_compareTo_1_) {
         return this.compareTo((Profiler.Result)p_compareTo_1_);
      }
   }
}
