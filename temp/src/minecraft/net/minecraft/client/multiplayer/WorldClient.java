package net.minecraft.client.multiplayer;

import com.google.common.collect.Sets;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecart;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EntityFirework;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.SaveDataMemoryStorage;
import net.minecraft.world.storage.SaveHandlerMP;
import net.minecraft.world.storage.WorldInfo;

public class WorldClient extends World {
   private NetHandlerPlayClient field_73035_a;
   private ChunkProviderClient field_73033_b;
   private final Set<Entity> field_73032_d = Sets.newHashSet();
   private final Set<Entity> field_73036_L = Sets.newHashSet();
   private final Minecraft field_73037_M = Minecraft.func_71410_x();
   private final Set<ChunkCoordIntPair> field_73038_N = Sets.newHashSet();

   public WorldClient(NetHandlerPlayClient p_i45063_1_, WorldSettings p_i45063_2_, int p_i45063_3_, EnumDifficulty p_i45063_4_, Profiler p_i45063_5_) {
      super(new SaveHandlerMP(), new WorldInfo(p_i45063_2_, "MpServer"), WorldProvider.func_76570_a(p_i45063_3_), p_i45063_5_, true);
      this.field_73035_a = p_i45063_1_;
      this.func_72912_H().func_176144_a(p_i45063_4_);
      this.func_175652_B(new BlockPos(8, 64, 8));
      this.field_73011_w.func_76558_a(this);
      this.field_73020_y = this.func_72970_h();
      this.field_72988_C = new SaveDataMemoryStorage();
      this.func_72966_v();
      this.func_72947_a();
   }

   public void func_72835_b() {
      super.func_72835_b();
      this.func_82738_a(this.func_82737_E() + 1L);
      if(this.func_82736_K().func_82766_b("doDaylightCycle")) {
         this.func_72877_b(this.func_72820_D() + 1L);
      }

      this.field_72984_F.func_76320_a("reEntryProcessing");

      for(int lvt_1_1_ = 0; lvt_1_1_ < 10 && !this.field_73036_L.isEmpty(); ++lvt_1_1_) {
         Entity lvt_2_1_ = (Entity)this.field_73036_L.iterator().next();
         this.field_73036_L.remove(lvt_2_1_);
         if(!this.field_72996_f.contains(lvt_2_1_)) {
            this.func_72838_d(lvt_2_1_);
         }
      }

      this.field_72984_F.func_76318_c("chunkCache");
      this.field_73033_b.func_73156_b();
      this.field_72984_F.func_76318_c("blocks");
      this.func_147456_g();
      this.field_72984_F.func_76319_b();
   }

   public void func_73031_a(int p_73031_1_, int p_73031_2_, int p_73031_3_, int p_73031_4_, int p_73031_5_, int p_73031_6_) {
   }

   protected IChunkProvider func_72970_h() {
      this.field_73033_b = new ChunkProviderClient(this);
      return this.field_73033_b;
   }

   protected void func_147456_g() {
      super.func_147456_g();
      this.field_73038_N.retainAll(this.field_72993_I);
      if(this.field_73038_N.size() == this.field_72993_I.size()) {
         this.field_73038_N.clear();
      }

      int lvt_1_1_ = 0;

      for(ChunkCoordIntPair lvt_3_1_ : this.field_72993_I) {
         if(!this.field_73038_N.contains(lvt_3_1_)) {
            int lvt_4_1_ = lvt_3_1_.field_77276_a * 16;
            int lvt_5_1_ = lvt_3_1_.field_77275_b * 16;
            this.field_72984_F.func_76320_a("getChunk");
            Chunk lvt_6_1_ = this.func_72964_e(lvt_3_1_.field_77276_a, lvt_3_1_.field_77275_b);
            this.func_147467_a(lvt_4_1_, lvt_5_1_, lvt_6_1_);
            this.field_72984_F.func_76319_b();
            this.field_73038_N.add(lvt_3_1_);
            ++lvt_1_1_;
            if(lvt_1_1_ >= 10) {
               return;
            }
         }
      }

   }

   public void func_73025_a(int p_73025_1_, int p_73025_2_, boolean p_73025_3_) {
      if(p_73025_3_) {
         this.field_73033_b.func_73158_c(p_73025_1_, p_73025_2_);
      } else {
         this.field_73033_b.func_73234_b(p_73025_1_, p_73025_2_);
      }

      if(!p_73025_3_) {
         this.func_147458_c(p_73025_1_ * 16, 0, p_73025_2_ * 16, p_73025_1_ * 16 + 15, 256, p_73025_2_ * 16 + 15);
      }

   }

   public boolean func_72838_d(Entity p_72838_1_) {
      boolean lvt_2_1_ = super.func_72838_d(p_72838_1_);
      this.field_73032_d.add(p_72838_1_);
      if(!lvt_2_1_) {
         this.field_73036_L.add(p_72838_1_);
      } else if(p_72838_1_ instanceof EntityMinecart) {
         this.field_73037_M.func_147118_V().func_147682_a(new MovingSoundMinecart((EntityMinecart)p_72838_1_));
      }

      return lvt_2_1_;
   }

   public void func_72900_e(Entity p_72900_1_) {
      super.func_72900_e(p_72900_1_);
      this.field_73032_d.remove(p_72900_1_);
   }

   protected void func_72923_a(Entity p_72923_1_) {
      super.func_72923_a(p_72923_1_);
      if(this.field_73036_L.contains(p_72923_1_)) {
         this.field_73036_L.remove(p_72923_1_);
      }

   }

   protected void func_72847_b(Entity p_72847_1_) {
      super.func_72847_b(p_72847_1_);
      boolean lvt_2_1_ = false;
      if(this.field_73032_d.contains(p_72847_1_)) {
         if(p_72847_1_.func_70089_S()) {
            this.field_73036_L.add(p_72847_1_);
            lvt_2_1_ = true;
         } else {
            this.field_73032_d.remove(p_72847_1_);
         }
      }

   }

   public void func_73027_a(int p_73027_1_, Entity p_73027_2_) {
      Entity lvt_3_1_ = this.func_73045_a(p_73027_1_);
      if(lvt_3_1_ != null) {
         this.func_72900_e(lvt_3_1_);
      }

      this.field_73032_d.add(p_73027_2_);
      p_73027_2_.func_145769_d(p_73027_1_);
      if(!this.func_72838_d(p_73027_2_)) {
         this.field_73036_L.add(p_73027_2_);
      }

      this.field_175729_l.func_76038_a(p_73027_1_, p_73027_2_);
   }

   public Entity func_73045_a(int p_73045_1_) {
      return (Entity)(p_73045_1_ == this.field_73037_M.field_71439_g.func_145782_y()?this.field_73037_M.field_71439_g:super.func_73045_a(p_73045_1_));
   }

   public Entity func_73028_b(int p_73028_1_) {
      Entity lvt_2_1_ = (Entity)this.field_175729_l.func_76049_d(p_73028_1_);
      if(lvt_2_1_ != null) {
         this.field_73032_d.remove(lvt_2_1_);
         this.func_72900_e(lvt_2_1_);
      }

      return lvt_2_1_;
   }

   public boolean func_180503_b(BlockPos p_180503_1_, IBlockState p_180503_2_) {
      int lvt_3_1_ = p_180503_1_.func_177958_n();
      int lvt_4_1_ = p_180503_1_.func_177956_o();
      int lvt_5_1_ = p_180503_1_.func_177952_p();
      this.func_73031_a(lvt_3_1_, lvt_4_1_, lvt_5_1_, lvt_3_1_, lvt_4_1_, lvt_5_1_);
      return super.func_180501_a(p_180503_1_, p_180503_2_, 3);
   }

   public void func_72882_A() {
      this.field_73035_a.func_147298_b().func_150718_a(new ChatComponentText("Quitting"));
   }

   protected void func_72979_l() {
   }

   protected int func_152379_p() {
      return this.field_73037_M.field_71474_y.field_151451_c;
   }

   public void func_73029_E(int p_73029_1_, int p_73029_2_, int p_73029_3_) {
      int lvt_4_1_ = 16;
      Random lvt_5_1_ = new Random();
      ItemStack lvt_6_1_ = this.field_73037_M.field_71439_g.func_70694_bm();
      boolean lvt_7_1_ = this.field_73037_M.field_71442_b.func_178889_l() == WorldSettings.GameType.CREATIVE && lvt_6_1_ != null && Block.func_149634_a(lvt_6_1_.func_77973_b()) == Blocks.field_180401_cv;
      BlockPos.MutableBlockPos lvt_8_1_ = new BlockPos.MutableBlockPos();

      for(int lvt_9_1_ = 0; lvt_9_1_ < 1000; ++lvt_9_1_) {
         int lvt_10_1_ = p_73029_1_ + this.field_73012_v.nextInt(lvt_4_1_) - this.field_73012_v.nextInt(lvt_4_1_);
         int lvt_11_1_ = p_73029_2_ + this.field_73012_v.nextInt(lvt_4_1_) - this.field_73012_v.nextInt(lvt_4_1_);
         int lvt_12_1_ = p_73029_3_ + this.field_73012_v.nextInt(lvt_4_1_) - this.field_73012_v.nextInt(lvt_4_1_);
         lvt_8_1_.func_181079_c(lvt_10_1_, lvt_11_1_, lvt_12_1_);
         IBlockState lvt_13_1_ = this.func_180495_p(lvt_8_1_);
         lvt_13_1_.func_177230_c().func_180655_c(this, lvt_8_1_, lvt_13_1_, lvt_5_1_);
         if(lvt_7_1_ && lvt_13_1_.func_177230_c() == Blocks.field_180401_cv) {
            this.func_175688_a(EnumParticleTypes.BARRIER, (double)((float)lvt_10_1_ + 0.5F), (double)((float)lvt_11_1_ + 0.5F), (double)((float)lvt_12_1_ + 0.5F), 0.0D, 0.0D, 0.0D, new int[0]);
         }
      }

   }

   public void func_73022_a() {
      this.field_72996_f.removeAll(this.field_72997_g);

      for(int lvt_1_1_ = 0; lvt_1_1_ < this.field_72997_g.size(); ++lvt_1_1_) {
         Entity lvt_2_1_ = (Entity)this.field_72997_g.get(lvt_1_1_);
         int lvt_3_1_ = lvt_2_1_.field_70176_ah;
         int lvt_4_1_ = lvt_2_1_.field_70164_aj;
         if(lvt_2_1_.field_70175_ag && this.func_175680_a(lvt_3_1_, lvt_4_1_, true)) {
            this.func_72964_e(lvt_3_1_, lvt_4_1_).func_76622_b(lvt_2_1_);
         }
      }

      for(int lvt_1_2_ = 0; lvt_1_2_ < this.field_72997_g.size(); ++lvt_1_2_) {
         this.func_72847_b((Entity)this.field_72997_g.get(lvt_1_2_));
      }

      this.field_72997_g.clear();

      for(int lvt_1_3_ = 0; lvt_1_3_ < this.field_72996_f.size(); ++lvt_1_3_) {
         Entity lvt_2_2_ = (Entity)this.field_72996_f.get(lvt_1_3_);
         if(lvt_2_2_.field_70154_o != null) {
            if(!lvt_2_2_.field_70154_o.field_70128_L && lvt_2_2_.field_70154_o.field_70153_n == lvt_2_2_) {
               continue;
            }

            lvt_2_2_.field_70154_o.field_70153_n = null;
            lvt_2_2_.field_70154_o = null;
         }

         if(lvt_2_2_.field_70128_L) {
            int lvt_3_2_ = lvt_2_2_.field_70176_ah;
            int lvt_4_2_ = lvt_2_2_.field_70164_aj;
            if(lvt_2_2_.field_70175_ag && this.func_175680_a(lvt_3_2_, lvt_4_2_, true)) {
               this.func_72964_e(lvt_3_2_, lvt_4_2_).func_76622_b(lvt_2_2_);
            }

            this.field_72996_f.remove(lvt_1_3_--);
            this.func_72847_b(lvt_2_2_);
         }
      }

   }

   public CrashReportCategory func_72914_a(CrashReport p_72914_1_) {
      CrashReportCategory lvt_2_1_ = super.func_72914_a(p_72914_1_);
      lvt_2_1_.func_71500_a("Forced entities", new Callable<String>() {
         public String call() {
            return WorldClient.this.field_73032_d.size() + " total; " + WorldClient.this.field_73032_d.toString();
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      lvt_2_1_.func_71500_a("Retry entities", new Callable<String>() {
         public String call() {
            return WorldClient.this.field_73036_L.size() + " total; " + WorldClient.this.field_73036_L.toString();
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      lvt_2_1_.func_71500_a("Server brand", new Callable<String>() {
         public String call() throws Exception {
            return WorldClient.this.field_73037_M.field_71439_g.func_142021_k();
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      lvt_2_1_.func_71500_a("Server type", new Callable<String>() {
         public String call() throws Exception {
            return WorldClient.this.field_73037_M.func_71401_C() == null?"Non-integrated multiplayer server":"Integrated singleplayer server";
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      return lvt_2_1_;
   }

   public void func_175731_a(BlockPos p_175731_1_, String p_175731_2_, float p_175731_3_, float p_175731_4_, boolean p_175731_5_) {
      this.func_72980_b((double)p_175731_1_.func_177958_n() + 0.5D, (double)p_175731_1_.func_177956_o() + 0.5D, (double)p_175731_1_.func_177952_p() + 0.5D, p_175731_2_, p_175731_3_, p_175731_4_, p_175731_5_);
   }

   public void func_72980_b(double p_72980_1_, double p_72980_3_, double p_72980_5_, String p_72980_7_, float p_72980_8_, float p_72980_9_, boolean p_72980_10_) {
      double lvt_11_1_ = this.field_73037_M.func_175606_aa().func_70092_e(p_72980_1_, p_72980_3_, p_72980_5_);
      PositionedSoundRecord lvt_13_1_ = new PositionedSoundRecord(new ResourceLocation(p_72980_7_), p_72980_8_, p_72980_9_, (float)p_72980_1_, (float)p_72980_3_, (float)p_72980_5_);
      if(p_72980_10_ && lvt_11_1_ > 100.0D) {
         double lvt_14_1_ = Math.sqrt(lvt_11_1_) / 40.0D;
         this.field_73037_M.func_147118_V().func_147681_a(lvt_13_1_, (int)(lvt_14_1_ * 20.0D));
      } else {
         this.field_73037_M.func_147118_V().func_147682_a(lvt_13_1_);
      }

   }

   public void func_92088_a(double p_92088_1_, double p_92088_3_, double p_92088_5_, double p_92088_7_, double p_92088_9_, double p_92088_11_, NBTTagCompound p_92088_13_) {
      this.field_73037_M.field_71452_i.func_78873_a(new EntityFirework.StarterFX(this, p_92088_1_, p_92088_3_, p_92088_5_, p_92088_7_, p_92088_9_, p_92088_11_, this.field_73037_M.field_71452_i, p_92088_13_));
   }

   public void func_96443_a(Scoreboard p_96443_1_) {
      this.field_96442_D = p_96443_1_;
   }

   public void func_72877_b(long p_72877_1_) {
      if(p_72877_1_ < 0L) {
         p_72877_1_ = -p_72877_1_;
         this.func_82736_K().func_82764_b("doDaylightCycle", "false");
      } else {
         this.func_82736_K().func_82764_b("doDaylightCycle", "true");
      }

      super.func_72877_b(p_72877_1_);
   }
}
