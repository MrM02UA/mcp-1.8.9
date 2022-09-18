package net.minecraft.client.renderer.chunk;

import java.util.BitSet;
import java.util.Set;
import net.minecraft.util.EnumFacing;

public class SetVisibility {
   private static final int field_178623_a = EnumFacing.values().length;
   private final BitSet field_178622_b;

   public SetVisibility() {
      this.field_178622_b = new BitSet(field_178623_a * field_178623_a);
   }

   public void func_178620_a(Set<EnumFacing> p_178620_1_) {
      for(EnumFacing lvt_3_1_ : p_178620_1_) {
         for(EnumFacing lvt_5_1_ : p_178620_1_) {
            this.func_178619_a(lvt_3_1_, lvt_5_1_, true);
         }
      }

   }

   public void func_178619_a(EnumFacing p_178619_1_, EnumFacing p_178619_2_, boolean p_178619_3_) {
      this.field_178622_b.set(p_178619_1_.ordinal() + p_178619_2_.ordinal() * field_178623_a, p_178619_3_);
      this.field_178622_b.set(p_178619_2_.ordinal() + p_178619_1_.ordinal() * field_178623_a, p_178619_3_);
   }

   public void func_178618_a(boolean p_178618_1_) {
      this.field_178622_b.set(0, this.field_178622_b.size(), p_178618_1_);
   }

   public boolean func_178621_a(EnumFacing p_178621_1_, EnumFacing p_178621_2_) {
      return this.field_178622_b.get(p_178621_1_.ordinal() + p_178621_2_.ordinal() * field_178623_a);
   }

   public String toString() {
      StringBuilder lvt_1_1_ = new StringBuilder();
      lvt_1_1_.append(' ');

      for(EnumFacing lvt_5_1_ : EnumFacing.values()) {
         lvt_1_1_.append(' ').append(lvt_5_1_.toString().toUpperCase().charAt(0));
      }

      lvt_1_1_.append('\n');

      for(EnumFacing lvt_5_2_ : EnumFacing.values()) {
         lvt_1_1_.append(lvt_5_2_.toString().toUpperCase().charAt(0));

         for(EnumFacing lvt_9_1_ : EnumFacing.values()) {
            if(lvt_5_2_ == lvt_9_1_) {
               lvt_1_1_.append("  ");
            } else {
               boolean lvt_10_1_ = this.func_178621_a(lvt_5_2_, lvt_9_1_);
               lvt_1_1_.append(' ').append((char)(lvt_10_1_?'Y':'n'));
            }
         }

         lvt_1_1_.append('\n');
      }

      return lvt_1_1_.toString();
   }
}
