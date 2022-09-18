package net.minecraft.world.gen.feature;

import com.google.common.base.Predicates;
import java.util.Random;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockStateHelper;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenDesertWells extends WorldGenerator {
   private static final BlockStateHelper field_175913_a = BlockStateHelper.func_177638_a(Blocks.field_150354_m).func_177637_a(BlockSand.field_176504_a, Predicates.equalTo(BlockSand.EnumType.SAND));
   private final IBlockState field_175911_b = Blocks.field_150333_U.func_176223_P().func_177226_a(BlockStoneSlab.field_176556_M, BlockStoneSlab.EnumType.SAND).func_177226_a(BlockSlab.field_176554_a, BlockSlab.EnumBlockHalf.BOTTOM);
   private final IBlockState field_175912_c = Blocks.field_150322_A.func_176223_P();
   private final IBlockState field_175910_d = Blocks.field_150358_i.func_176223_P();

   public boolean func_180709_b(World p_180709_1_, Random p_180709_2_, BlockPos p_180709_3_) {
      while(p_180709_1_.func_175623_d(p_180709_3_) && p_180709_3_.func_177956_o() > 2) {
         p_180709_3_ = p_180709_3_.func_177977_b();
      }

      if(!field_175913_a.apply(p_180709_1_.func_180495_p(p_180709_3_))) {
         return false;
      } else {
         for(int lvt_4_1_ = -2; lvt_4_1_ <= 2; ++lvt_4_1_) {
            for(int lvt_5_1_ = -2; lvt_5_1_ <= 2; ++lvt_5_1_) {
               if(p_180709_1_.func_175623_d(p_180709_3_.func_177982_a(lvt_4_1_, -1, lvt_5_1_)) && p_180709_1_.func_175623_d(p_180709_3_.func_177982_a(lvt_4_1_, -2, lvt_5_1_))) {
                  return false;
               }
            }
         }

         for(int lvt_4_2_ = -1; lvt_4_2_ <= 0; ++lvt_4_2_) {
            for(int lvt_5_2_ = -2; lvt_5_2_ <= 2; ++lvt_5_2_) {
               for(int lvt_6_1_ = -2; lvt_6_1_ <= 2; ++lvt_6_1_) {
                  p_180709_1_.func_180501_a(p_180709_3_.func_177982_a(lvt_5_2_, lvt_4_2_, lvt_6_1_), this.field_175912_c, 2);
               }
            }
         }

         p_180709_1_.func_180501_a(p_180709_3_, this.field_175910_d, 2);

         for(EnumFacing lvt_5_3_ : EnumFacing.Plane.HORIZONTAL) {
            p_180709_1_.func_180501_a(p_180709_3_.func_177972_a(lvt_5_3_), this.field_175910_d, 2);
         }

         for(int lvt_4_4_ = -2; lvt_4_4_ <= 2; ++lvt_4_4_) {
            for(int lvt_5_4_ = -2; lvt_5_4_ <= 2; ++lvt_5_4_) {
               if(lvt_4_4_ == -2 || lvt_4_4_ == 2 || lvt_5_4_ == -2 || lvt_5_4_ == 2) {
                  p_180709_1_.func_180501_a(p_180709_3_.func_177982_a(lvt_4_4_, 1, lvt_5_4_), this.field_175912_c, 2);
               }
            }
         }

         p_180709_1_.func_180501_a(p_180709_3_.func_177982_a(2, 1, 0), this.field_175911_b, 2);
         p_180709_1_.func_180501_a(p_180709_3_.func_177982_a(-2, 1, 0), this.field_175911_b, 2);
         p_180709_1_.func_180501_a(p_180709_3_.func_177982_a(0, 1, 2), this.field_175911_b, 2);
         p_180709_1_.func_180501_a(p_180709_3_.func_177982_a(0, 1, -2), this.field_175911_b, 2);

         for(int lvt_4_5_ = -1; lvt_4_5_ <= 1; ++lvt_4_5_) {
            for(int lvt_5_5_ = -1; lvt_5_5_ <= 1; ++lvt_5_5_) {
               if(lvt_4_5_ == 0 && lvt_5_5_ == 0) {
                  p_180709_1_.func_180501_a(p_180709_3_.func_177982_a(lvt_4_5_, 4, lvt_5_5_), this.field_175912_c, 2);
               } else {
                  p_180709_1_.func_180501_a(p_180709_3_.func_177982_a(lvt_4_5_, 4, lvt_5_5_), this.field_175911_b, 2);
               }
            }
         }

         for(int lvt_4_6_ = 1; lvt_4_6_ <= 3; ++lvt_4_6_) {
            p_180709_1_.func_180501_a(p_180709_3_.func_177982_a(-1, lvt_4_6_, -1), this.field_175912_c, 2);
            p_180709_1_.func_180501_a(p_180709_3_.func_177982_a(-1, lvt_4_6_, 1), this.field_175912_c, 2);
            p_180709_1_.func_180501_a(p_180709_3_.func_177982_a(1, lvt_4_6_, -1), this.field_175912_c, 2);
            p_180709_1_.func_180501_a(p_180709_3_.func_177982_a(1, lvt_4_6_, 1), this.field_175912_c, 2);
         }

         return true;
      }
   }
}
