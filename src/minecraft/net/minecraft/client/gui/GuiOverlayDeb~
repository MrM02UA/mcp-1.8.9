package net.minecraft.client.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class GuiOverlayDebug extends Gui {
   private final Minecraft field_175242_a;
   private final FontRenderer field_175241_f;

   public GuiOverlayDebug(Minecraft p_i45543_1_) {
      this.field_175242_a = p_i45543_1_;
      this.field_175241_f = p_i45543_1_.field_71466_p;
   }

   public void func_175237_a(ScaledResolution p_175237_1_) {
      this.field_175242_a.field_71424_I.func_76320_a("debug");
      GlStateManager.func_179094_E();
      this.func_180798_a();
      this.func_175239_b(p_175237_1_);
      GlStateManager.func_179121_F();
      if(this.field_175242_a.field_71474_y.field_181657_aC) {
         this.func_181554_e();
      }

      this.field_175242_a.field_71424_I.func_76319_b();
   }

   private boolean func_175236_d() {
      return this.field_175242_a.field_71439_g.func_175140_cp() || this.field_175242_a.field_71474_y.field_178879_v;
   }

   protected void func_180798_a() {
      List<String> lvt_1_1_ = this.call();

      for(int lvt_2_1_ = 0; lvt_2_1_ < lvt_1_1_.size(); ++lvt_2_1_) {
         String lvt_3_1_ = (String)lvt_1_1_.get(lvt_2_1_);
         if(!Strings.isNullOrEmpty(lvt_3_1_)) {
            int lvt_4_1_ = this.field_175241_f.field_78288_b;
            int lvt_5_1_ = this.field_175241_f.func_78256_a(lvt_3_1_);
            int lvt_6_1_ = 2;
            int lvt_7_1_ = 2 + lvt_4_1_ * lvt_2_1_;
            func_73734_a(1, lvt_7_1_ - 1, 2 + lvt_5_1_ + 1, lvt_7_1_ + lvt_4_1_ - 1, -1873784752);
            this.field_175241_f.func_78276_b(lvt_3_1_, 2, lvt_7_1_, 14737632);
         }
      }

   }

   protected void func_175239_b(ScaledResolution p_175239_1_) {
      List<String> lvt_2_1_ = this.func_175238_c();

      for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.size(); ++lvt_3_1_) {
         String lvt_4_1_ = (String)lvt_2_1_.get(lvt_3_1_);
         if(!Strings.isNullOrEmpty(lvt_4_1_)) {
            int lvt_5_1_ = this.field_175241_f.field_78288_b;
            int lvt_6_1_ = this.field_175241_f.func_78256_a(lvt_4_1_);
            int lvt_7_1_ = p_175239_1_.func_78326_a() - 2 - lvt_6_1_;
            int lvt_8_1_ = 2 + lvt_5_1_ * lvt_3_1_;
            func_73734_a(lvt_7_1_ - 1, lvt_8_1_ - 1, lvt_7_1_ + lvt_6_1_ + 1, lvt_8_1_ + lvt_5_1_ - 1, -1873784752);
            this.field_175241_f.func_78276_b(lvt_4_1_, lvt_7_1_, lvt_8_1_, 14737632);
         }
      }

   }

   protected List<String> call() {
      BlockPos lvt_1_1_ = new BlockPos(this.field_175242_a.func_175606_aa().field_70165_t, this.field_175242_a.func_175606_aa().func_174813_aQ().field_72338_b, this.field_175242_a.func_175606_aa().field_70161_v);
      if(this.func_175236_d()) {
         return Lists.newArrayList(new String[]{"Minecraft 1.8.9 (" + this.field_175242_a.func_175600_c() + "/" + ClientBrandRetriever.getClientModName() + ")", this.field_175242_a.field_71426_K, this.field_175242_a.field_71438_f.func_72735_c(), this.field_175242_a.field_71438_f.func_72723_d(), "P: " + this.field_175242_a.field_71452_i.func_78869_b() + ". T: " + this.field_175242_a.field_71441_e.func_72981_t(), this.field_175242_a.field_71441_e.func_72827_u(), "", String.format("Chunk-relative: %d %d %d", new Object[]{Integer.valueOf(lvt_1_1_.func_177958_n() & 15), Integer.valueOf(lvt_1_1_.func_177956_o() & 15), Integer.valueOf(lvt_1_1_.func_177952_p() & 15)})});
      } else {
         Entity lvt_2_1_ = this.field_175242_a.func_175606_aa();
         EnumFacing lvt_3_1_ = lvt_2_1_.func_174811_aO();
         String lvt_4_1_ = "Invalid";
         switch(lvt_3_1_) {
         case NORTH:
            lvt_4_1_ = "Towards negative Z";
            break;
         case SOUTH:
            lvt_4_1_ = "Towards positive Z";
            break;
         case WEST:
            lvt_4_1_ = "Towards negative X";
            break;
         case EAST:
            lvt_4_1_ = "Towards positive X";
         }

         List<String> lvt_5_1_ = Lists.newArrayList(new String[]{"Minecraft 1.8.9 (" + this.field_175242_a.func_175600_c() + "/" + ClientBrandRetriever.getClientModName() + ")", this.field_175242_a.field_71426_K, this.field_175242_a.field_71438_f.func_72735_c(), this.field_175242_a.field_71438_f.func_72723_d(), "P: " + this.field_175242_a.field_71452_i.func_78869_b() + ". T: " + this.field_175242_a.field_71441_e.func_72981_t(), this.field_175242_a.field_71441_e.func_72827_u(), "", String.format("XYZ: %.3f / %.5f / %.3f", new Object[]{Double.valueOf(this.field_175242_a.func_175606_aa().field_70165_t), Double.valueOf(this.field_175242_a.func_175606_aa().func_174813_aQ().field_72338_b), Double.valueOf(this.field_175242_a.func_175606_aa().field_70161_v)}), String.format("Block: %d %d %d", new Object[]{Integer.valueOf(lvt_1_1_.func_177958_n()), Integer.valueOf(lvt_1_1_.func_177956_o()), Integer.valueOf(lvt_1_1_.func_177952_p())}), String.format("Chunk: %d %d %d in %d %d %d", new Object[]{Integer.valueOf(lvt_1_1_.func_177958_n() & 15), Integer.valueOf(lvt_1_1_.func_177956_o() & 15), Integer.valueOf(lvt_1_1_.func_177952_p() & 15), Integer.valueOf(lvt_1_1_.func_177958_n() >> 4), Integer.valueOf(lvt_1_1_.func_177956_o() >> 4), Integer.valueOf(lvt_1_1_.func_177952_p() >> 4)}), String.format("Facing: %s (%s) (%.1f / %.1f)", new Object[]{lvt_3_1_, lvt_4_1_, Float.valueOf(MathHelper.func_76142_g(lvt_2_1_.field_70177_z)), Float.valueOf(MathHelper.func_76142_g(lvt_2_1_.field_70125_A))})});
         if(this.field_175242_a.field_71441_e != null && this.field_175242_a.field_71441_e.func_175667_e(lvt_1_1_)) {
            Chunk lvt_6_1_ = this.field_175242_a.field_71441_e.func_175726_f(lvt_1_1_);
            lvt_5_1_.add("Biome: " + lvt_6_1_.func_177411_a(lvt_1_1_, this.field_175242_a.field_71441_e.func_72959_q()).field_76791_y);
            lvt_5_1_.add("Light: " + lvt_6_1_.func_177443_a(lvt_1_1_, 0) + " (" + lvt_6_1_.func_177413_a(EnumSkyBlock.SKY, lvt_1_1_) + " sky, " + lvt_6_1_.func_177413_a(EnumSkyBlock.BLOCK, lvt_1_1_) + " block)");
            DifficultyInstance lvt_7_1_ = this.field_175242_a.field_71441_e.func_175649_E(lvt_1_1_);
            if(this.field_175242_a.func_71387_A() && this.field_175242_a.func_71401_C() != null) {
               EntityPlayerMP lvt_8_1_ = this.field_175242_a.func_71401_C().func_71203_ab().func_177451_a(this.field_175242_a.field_71439_g.func_110124_au());
               if(lvt_8_1_ != null) {
                  lvt_7_1_ = lvt_8_1_.field_70170_p.func_175649_E(new BlockPos(lvt_8_1_));
               }
            }

            lvt_5_1_.add(String.format("Local Difficulty: %.2f (Day %d)", new Object[]{Float.valueOf(lvt_7_1_.func_180168_b()), Long.valueOf(this.field_175242_a.field_71441_e.func_72820_D() / 24000L)}));
         }

         if(this.field_175242_a.field_71460_t != null && this.field_175242_a.field_71460_t.func_147702_a()) {
            lvt_5_1_.add("Shader: " + this.field_175242_a.field_71460_t.func_147706_e().func_148022_b());
         }

         if(this.field_175242_a.field_71476_x != null && this.field_175242_a.field_71476_x.field_72313_a == MovingObjectPosition.MovingObjectType.BLOCK && this.field_175242_a.field_71476_x.func_178782_a() != null) {
            BlockPos lvt_6_2_ = this.field_175242_a.field_71476_x.func_178782_a();
            lvt_5_1_.add(String.format("Looking at: %d %d %d", new Object[]{Integer.valueOf(lvt_6_2_.func_177958_n()), Integer.valueOf(lvt_6_2_.func_177956_o()), Integer.valueOf(lvt_6_2_.func_177952_p())}));
         }

         return lvt_5_1_;
      }
   }

   protected List<String> func_175238_c() {
      long lvt_1_1_ = Runtime.getRuntime().maxMemory();
      long lvt_3_1_ = Runtime.getRuntime().totalMemory();
      long lvt_5_1_ = Runtime.getRuntime().freeMemory();
      long lvt_7_1_ = lvt_3_1_ - lvt_5_1_;
      List<String> lvt_9_1_ = Lists.newArrayList(new String[]{String.format("Java: %s %dbit", new Object[]{System.getProperty("java.version"), Integer.valueOf(this.field_175242_a.func_147111_S()?64:32)}), String.format("Mem: % 2d%% %03d/%03dMB", new Object[]{Long.valueOf(lvt_7_1_ * 100L / lvt_1_1_), Long.valueOf(func_175240_a(lvt_7_1_)), Long.valueOf(func_175240_a(lvt_1_1_))}), String.format("Allocated: % 2d%% %03dMB", new Object[]{Long.valueOf(lvt_3_1_ * 100L / lvt_1_1_), Long.valueOf(func_175240_a(lvt_3_1_))}), "", String.format("CPU: %s", new Object[]{OpenGlHelper.func_183029_j()}), "", String.format("Display: %dx%d (%s)", new Object[]{Integer.valueOf(Display.getWidth()), Integer.valueOf(Display.getHeight()), GL11.glGetString(7936)}), GL11.glGetString(7937), GL11.glGetString(7938)});
      if(this.func_175236_d()) {
         return lvt_9_1_;
      } else {
         if(this.field_175242_a.field_71476_x != null && this.field_175242_a.field_71476_x.field_72313_a == MovingObjectPosition.MovingObjectType.BLOCK && this.field_175242_a.field_71476_x.func_178782_a() != null) {
            BlockPos lvt_10_1_ = this.field_175242_a.field_71476_x.func_178782_a();
            IBlockState lvt_11_1_ = this.field_175242_a.field_71441_e.func_180495_p(lvt_10_1_);
            if(this.field_175242_a.field_71441_e.func_175624_G() != WorldType.field_180272_g) {
               lvt_11_1_ = lvt_11_1_.func_177230_c().func_176221_a(lvt_11_1_, this.field_175242_a.field_71441_e, lvt_10_1_);
            }

            lvt_9_1_.add("");
            lvt_9_1_.add(String.valueOf(Block.field_149771_c.func_177774_c(lvt_11_1_.func_177230_c())));

            for(Entry<IProperty, Comparable> lvt_13_1_ : lvt_11_1_.func_177228_b().entrySet()) {
               String lvt_14_1_ = ((Comparable)lvt_13_1_.getValue()).toString();
               if(lvt_13_1_.getValue() == Boolean.TRUE) {
                  lvt_14_1_ = EnumChatFormatting.GREEN + lvt_14_1_;
               } else if(lvt_13_1_.getValue() == Boolean.FALSE) {
                  lvt_14_1_ = EnumChatFormatting.RED + lvt_14_1_;
               }

               lvt_9_1_.add(((IProperty)lvt_13_1_.getKey()).func_177701_a() + ": " + lvt_14_1_);
            }
         }

         return lvt_9_1_;
      }
   }

   private void func_181554_e() {
      GlStateManager.func_179097_i();
      FrameTimer lvt_1_1_ = this.field_175242_a.func_181539_aj();
      int lvt_2_1_ = lvt_1_1_.func_181749_a();
      int lvt_3_1_ = lvt_1_1_.func_181750_b();
      long[] lvt_4_1_ = lvt_1_1_.func_181746_c();
      ScaledResolution lvt_5_1_ = new ScaledResolution(this.field_175242_a);
      int lvt_6_1_ = lvt_2_1_;
      int lvt_7_1_ = 0;
      func_73734_a(0, lvt_5_1_.func_78328_b() - 60, 240, lvt_5_1_.func_78328_b(), -1873784752);

      while(lvt_6_1_ != lvt_3_1_) {
         int lvt_8_1_ = lvt_1_1_.func_181748_a(lvt_4_1_[lvt_6_1_], 30);
         int lvt_9_1_ = this.func_181552_c(MathHelper.func_76125_a(lvt_8_1_, 0, 60), 0, 30, 60);
         this.func_73728_b(lvt_7_1_, lvt_5_1_.func_78328_b(), lvt_5_1_.func_78328_b() - lvt_8_1_, lvt_9_1_);
         ++lvt_7_1_;
         lvt_6_1_ = lvt_1_1_.func_181751_b(lvt_6_1_ + 1);
      }

      func_73734_a(1, lvt_5_1_.func_78328_b() - 30 + 1, 14, lvt_5_1_.func_78328_b() - 30 + 10, -1873784752);
      this.field_175241_f.func_78276_b("60", 2, lvt_5_1_.func_78328_b() - 30 + 2, 14737632);
      this.func_73730_a(0, 239, lvt_5_1_.func_78328_b() - 30, -1);
      func_73734_a(1, lvt_5_1_.func_78328_b() - 60 + 1, 14, lvt_5_1_.func_78328_b() - 60 + 10, -1873784752);
      this.field_175241_f.func_78276_b("30", 2, lvt_5_1_.func_78328_b() - 60 + 2, 14737632);
      this.func_73730_a(0, 239, lvt_5_1_.func_78328_b() - 60, -1);
      this.func_73730_a(0, 239, lvt_5_1_.func_78328_b() - 1, -1);
      this.func_73728_b(0, lvt_5_1_.func_78328_b() - 60, lvt_5_1_.func_78328_b(), -1);
      this.func_73728_b(239, lvt_5_1_.func_78328_b() - 60, lvt_5_1_.func_78328_b(), -1);
      if(this.field_175242_a.field_71474_y.field_74350_i <= 120) {
         this.func_73730_a(0, 239, lvt_5_1_.func_78328_b() - 60 + this.field_175242_a.field_71474_y.field_74350_i / 2, -16711681);
      }

      GlStateManager.func_179126_j();
   }

   private int func_181552_c(int p_181552_1_, int p_181552_2_, int p_181552_3_, int p_181552_4_) {
      return p_181552_1_ < p_181552_3_?this.func_181553_a(-16711936, -256, (float)p_181552_1_ / (float)p_181552_3_):this.func_181553_a(-256, -65536, (float)(p_181552_1_ - p_181552_3_) / (float)(p_181552_4_ - p_181552_3_));
   }

   private int func_181553_a(int p_181553_1_, int p_181553_2_, float p_181553_3_) {
      int lvt_4_1_ = p_181553_1_ >> 24 & 255;
      int lvt_5_1_ = p_181553_1_ >> 16 & 255;
      int lvt_6_1_ = p_181553_1_ >> 8 & 255;
      int lvt_7_1_ = p_181553_1_ & 255;
      int lvt_8_1_ = p_181553_2_ >> 24 & 255;
      int lvt_9_1_ = p_181553_2_ >> 16 & 255;
      int lvt_10_1_ = p_181553_2_ >> 8 & 255;
      int lvt_11_1_ = p_181553_2_ & 255;
      int lvt_12_1_ = MathHelper.func_76125_a((int)((float)lvt_4_1_ + (float)(lvt_8_1_ - lvt_4_1_) * p_181553_3_), 0, 255);
      int lvt_13_1_ = MathHelper.func_76125_a((int)((float)lvt_5_1_ + (float)(lvt_9_1_ - lvt_5_1_) * p_181553_3_), 0, 255);
      int lvt_14_1_ = MathHelper.func_76125_a((int)((float)lvt_6_1_ + (float)(lvt_10_1_ - lvt_6_1_) * p_181553_3_), 0, 255);
      int lvt_15_1_ = MathHelper.func_76125_a((int)((float)lvt_7_1_ + (float)(lvt_11_1_ - lvt_7_1_) * p_181553_3_), 0, 255);
      return lvt_12_1_ << 24 | lvt_13_1_ << 16 | lvt_14_1_ << 8 | lvt_15_1_;
   }

   private static long func_175240_a(long p_175240_0_) {
      return p_175240_0_ / 1024L / 1024L;
   }
}
