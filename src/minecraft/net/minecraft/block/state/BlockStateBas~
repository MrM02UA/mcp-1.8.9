package net.minecraft.block.state;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;

public abstract class BlockStateBase implements IBlockState {
   private static final Joiner field_177234_a = Joiner.on(',');
   private static final Function<Entry<IProperty, Comparable>, String> field_177233_b = new Function<Entry<IProperty, Comparable>, String>() {
      public String apply(Entry<IProperty, Comparable> p_apply_1_) {
         if(p_apply_1_ == null) {
            return "<NULL>";
         } else {
            IProperty lvt_2_1_ = (IProperty)p_apply_1_.getKey();
            return lvt_2_1_.func_177701_a() + "=" + lvt_2_1_.func_177702_a((Comparable)p_apply_1_.getValue());
         }
      }

      // $FF: synthetic method
      public Object apply(Object p_apply_1_) {
         return this.apply((Entry)p_apply_1_);
      }
   };

   public <T extends Comparable<T>> IBlockState func_177231_a(IProperty<T> p_177231_1_) {
      return this.func_177226_a(p_177231_1_, (Comparable)func_177232_a(p_177231_1_.func_177700_c(), this.func_177229_b(p_177231_1_)));
   }

   protected static <T> T func_177232_a(Collection<T> p_177232_0_, T p_177232_1_) {
      Iterator<T> lvt_2_1_ = p_177232_0_.iterator();

      while(lvt_2_1_.hasNext()) {
         if(lvt_2_1_.next().equals(p_177232_1_)) {
            if(lvt_2_1_.hasNext()) {
               return (T)lvt_2_1_.next();
            }

            return (T)p_177232_0_.iterator().next();
         }
      }

      return (T)lvt_2_1_.next();
   }

   public String toString() {
      StringBuilder lvt_1_1_ = new StringBuilder();
      lvt_1_1_.append(Block.field_149771_c.func_177774_c(this.func_177230_c()));
      if(!this.func_177228_b().isEmpty()) {
         lvt_1_1_.append("[");
         field_177234_a.appendTo(lvt_1_1_, Iterables.transform(this.func_177228_b().entrySet(), field_177233_b));
         lvt_1_1_.append("]");
      }

      return lvt_1_1_.toString();
   }
}
