package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClassInheritanceMultiMap<T> extends AbstractSet<T> {
   private static final Set<Class<?>> field_181158_a = Sets.newHashSet();
   private final Map<Class<?>, List<T>> field_180218_a = Maps.newHashMap();
   private final Set<Class<?>> field_180216_b = Sets.newIdentityHashSet();
   private final Class<T> field_180217_c;
   private final List<T> field_181745_e = Lists.newArrayList();

   public ClassInheritanceMultiMap(Class<T> p_i45909_1_) {
      this.field_180217_c = p_i45909_1_;
      this.field_180216_b.add(p_i45909_1_);
      this.field_180218_a.put(p_i45909_1_, this.field_181745_e);

      for(Class<?> lvt_3_1_ : field_181158_a) {
         this.func_180213_a(lvt_3_1_);
      }

   }

   protected void func_180213_a(Class<?> p_180213_1_) {
      field_181158_a.add(p_180213_1_);

      for(T lvt_3_1_ : this.field_181745_e) {
         if(p_180213_1_.isAssignableFrom(lvt_3_1_.getClass())) {
            this.func_181743_a(lvt_3_1_, p_180213_1_);
         }
      }

      this.field_180216_b.add(p_180213_1_);
   }

   protected Class<?> func_181157_b(Class<?> p_181157_1_) {
      if(this.field_180217_c.isAssignableFrom(p_181157_1_)) {
         if(!this.field_180216_b.contains(p_181157_1_)) {
            this.func_180213_a(p_181157_1_);
         }

         return p_181157_1_;
      } else {
         throw new IllegalArgumentException("Don\'t know how to search for " + p_181157_1_);
      }
   }

   public boolean add(T p_add_1_) {
      for(Class<?> lvt_3_1_ : this.field_180216_b) {
         if(lvt_3_1_.isAssignableFrom(p_add_1_.getClass())) {
            this.func_181743_a(p_add_1_, lvt_3_1_);
         }
      }

      return true;
   }

   private void func_181743_a(T p_181743_1_, Class<?> p_181743_2_) {
      List<T> lvt_3_1_ = (List)this.field_180218_a.get(p_181743_2_);
      if(lvt_3_1_ == null) {
         this.field_180218_a.put(p_181743_2_, Lists.newArrayList(new Object[]{p_181743_1_}));
      } else {
         lvt_3_1_.add(p_181743_1_);
      }

   }

   public boolean remove(Object p_remove_1_) {
      T lvt_2_1_ = p_remove_1_;
      boolean lvt_3_1_ = false;

      for(Class<?> lvt_5_1_ : this.field_180216_b) {
         if(lvt_5_1_.isAssignableFrom(lvt_2_1_.getClass())) {
            List<T> lvt_6_1_ = (List)this.field_180218_a.get(lvt_5_1_);
            if(lvt_6_1_ != null && lvt_6_1_.remove(lvt_2_1_)) {
               lvt_3_1_ = true;
            }
         }
      }

      return lvt_3_1_;
   }

   public boolean contains(Object p_contains_1_) {
      return Iterators.contains(this.func_180215_b(p_contains_1_.getClass()).iterator(), p_contains_1_);
   }

   public <S> Iterable<S> func_180215_b(final Class<S> p_180215_1_) {
      return new Iterable<S>() {
         public Iterator<S> iterator() {
            List<T> lvt_1_1_ = (List)ClassInheritanceMultiMap.this.field_180218_a.get(ClassInheritanceMultiMap.this.func_181157_b(p_180215_1_));
            if(lvt_1_1_ == null) {
               return Iterators.emptyIterator();
            } else {
               Iterator<T> lvt_2_1_ = lvt_1_1_.iterator();
               return Iterators.filter(lvt_2_1_, p_180215_1_);
            }
         }
      };
   }

   public Iterator<T> iterator() {
      return this.field_181745_e.isEmpty()?Iterators.emptyIterator():Iterators.unmodifiableIterator(this.field_181745_e.iterator());
   }

   public int size() {
      return this.field_181745_e.size();
   }
}
