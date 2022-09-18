package net.minecraft.server.management;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerManager {
   private static final Logger field_152627_a = LogManager.getLogger();
   private final WorldServer field_72701_a;
   private final List<EntityPlayerMP> field_72699_b = Lists.newArrayList();
   private final LongHashMap<PlayerManager.PlayerInstance> field_72700_c = new LongHashMap();
   private final List<PlayerManager.PlayerInstance> field_72697_d = Lists.newArrayList();
   private final List<PlayerManager.PlayerInstance> field_111193_e = Lists.newArrayList();
   private int field_72698_e;
   private long field_111192_g;
   private final int[][] field_72696_f = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};

   public PlayerManager(WorldServer p_i1176_1_) {
      this.field_72701_a = p_i1176_1_;
      this.func_152622_a(p_i1176_1_.func_73046_m().func_71203_ab().func_72395_o());
   }

   public WorldServer func_72688_a() {
      return this.field_72701_a;
   }

   public void func_72693_b() {
      long lvt_1_1_ = this.field_72701_a.func_82737_E();
      if(lvt_1_1_ - this.field_111192_g > 8000L) {
         this.field_111192_g = lvt_1_1_;

         for(int lvt_3_1_ = 0; lvt_3_1_ < this.field_111193_e.size(); ++lvt_3_1_) {
            PlayerManager.PlayerInstance lvt_4_1_ = (PlayerManager.PlayerInstance)this.field_111193_e.get(lvt_3_1_);
            lvt_4_1_.func_73254_a();
            lvt_4_1_.func_111194_a();
         }
      } else {
         for(int lvt_3_2_ = 0; lvt_3_2_ < this.field_72697_d.size(); ++lvt_3_2_) {
            PlayerManager.PlayerInstance lvt_4_2_ = (PlayerManager.PlayerInstance)this.field_72697_d.get(lvt_3_2_);
            lvt_4_2_.func_73254_a();
         }
      }

      this.field_72697_d.clear();
      if(this.field_72699_b.isEmpty()) {
         WorldProvider lvt_3_3_ = this.field_72701_a.field_73011_w;
         if(!lvt_3_3_.func_76567_e()) {
            this.field_72701_a.field_73059_b.func_73240_a();
         }
      }

   }

   public boolean func_152621_a(int p_152621_1_, int p_152621_2_) {
      long lvt_3_1_ = (long)p_152621_1_ + 2147483647L | (long)p_152621_2_ + 2147483647L << 32;
      return this.field_72700_c.func_76164_a(lvt_3_1_) != null;
   }

   private PlayerManager.PlayerInstance func_72690_a(int p_72690_1_, int p_72690_2_, boolean p_72690_3_) {
      long lvt_4_1_ = (long)p_72690_1_ + 2147483647L | (long)p_72690_2_ + 2147483647L << 32;
      PlayerManager.PlayerInstance lvt_6_1_ = (PlayerManager.PlayerInstance)this.field_72700_c.func_76164_a(lvt_4_1_);
      if(lvt_6_1_ == null && p_72690_3_) {
         lvt_6_1_ = new PlayerManager.PlayerInstance(p_72690_1_, p_72690_2_);
         this.field_72700_c.func_76163_a(lvt_4_1_, lvt_6_1_);
         this.field_111193_e.add(lvt_6_1_);
      }

      return lvt_6_1_;
   }

   public void func_180244_a(BlockPos p_180244_1_) {
      int lvt_2_1_ = p_180244_1_.func_177958_n() >> 4;
      int lvt_3_1_ = p_180244_1_.func_177952_p() >> 4;
      PlayerManager.PlayerInstance lvt_4_1_ = this.func_72690_a(lvt_2_1_, lvt_3_1_, false);
      if(lvt_4_1_ != null) {
         lvt_4_1_.func_151253_a(p_180244_1_.func_177958_n() & 15, p_180244_1_.func_177956_o(), p_180244_1_.func_177952_p() & 15);
      }

   }

   public void func_72683_a(EntityPlayerMP p_72683_1_) {
      int lvt_2_1_ = (int)p_72683_1_.field_70165_t >> 4;
      int lvt_3_1_ = (int)p_72683_1_.field_70161_v >> 4;
      p_72683_1_.field_71131_d = p_72683_1_.field_70165_t;
      p_72683_1_.field_71132_e = p_72683_1_.field_70161_v;

      for(int lvt_4_1_ = lvt_2_1_ - this.field_72698_e; lvt_4_1_ <= lvt_2_1_ + this.field_72698_e; ++lvt_4_1_) {
         for(int lvt_5_1_ = lvt_3_1_ - this.field_72698_e; lvt_5_1_ <= lvt_3_1_ + this.field_72698_e; ++lvt_5_1_) {
            this.func_72690_a(lvt_4_1_, lvt_5_1_, true).func_73255_a(p_72683_1_);
         }
      }

      this.field_72699_b.add(p_72683_1_);
      this.func_72691_b(p_72683_1_);
   }

   public void func_72691_b(EntityPlayerMP p_72691_1_) {
      List<ChunkCoordIntPair> lvt_2_1_ = Lists.newArrayList(p_72691_1_.field_71129_f);
      int lvt_3_1_ = 0;
      int lvt_4_1_ = this.field_72698_e;
      int lvt_5_1_ = (int)p_72691_1_.field_70165_t >> 4;
      int lvt_6_1_ = (int)p_72691_1_.field_70161_v >> 4;
      int lvt_7_1_ = 0;
      int lvt_8_1_ = 0;
      ChunkCoordIntPair lvt_9_1_ = this.func_72690_a(lvt_5_1_, lvt_6_1_, true).field_73264_c;
      p_72691_1_.field_71129_f.clear();
      if(lvt_2_1_.contains(lvt_9_1_)) {
         p_72691_1_.field_71129_f.add(lvt_9_1_);
      }

      for(int lvt_10_1_ = 1; lvt_10_1_ <= lvt_4_1_ * 2; ++lvt_10_1_) {
         for(int lvt_11_1_ = 0; lvt_11_1_ < 2; ++lvt_11_1_) {
            int[] lvt_12_1_ = this.field_72696_f[lvt_3_1_++ % 4];

            for(int lvt_13_1_ = 0; lvt_13_1_ < lvt_10_1_; ++lvt_13_1_) {
               lvt_7_1_ += lvt_12_1_[0];
               lvt_8_1_ += lvt_12_1_[1];
               lvt_9_1_ = this.func_72690_a(lvt_5_1_ + lvt_7_1_, lvt_6_1_ + lvt_8_1_, true).field_73264_c;
               if(lvt_2_1_.contains(lvt_9_1_)) {
                  p_72691_1_.field_71129_f.add(lvt_9_1_);
               }
            }
         }
      }

      lvt_3_1_ = lvt_3_1_ % 4;

      for(int lvt_10_2_ = 0; lvt_10_2_ < lvt_4_1_ * 2; ++lvt_10_2_) {
         lvt_7_1_ += this.field_72696_f[lvt_3_1_][0];
         lvt_8_1_ += this.field_72696_f[lvt_3_1_][1];
         lvt_9_1_ = this.func_72690_a(lvt_5_1_ + lvt_7_1_, lvt_6_1_ + lvt_8_1_, true).field_73264_c;
         if(lvt_2_1_.contains(lvt_9_1_)) {
            p_72691_1_.field_71129_f.add(lvt_9_1_);
         }
      }

   }

   public void func_72695_c(EntityPlayerMP p_72695_1_) {
      int lvt_2_1_ = (int)p_72695_1_.field_71131_d >> 4;
      int lvt_3_1_ = (int)p_72695_1_.field_71132_e >> 4;

      for(int lvt_4_1_ = lvt_2_1_ - this.field_72698_e; lvt_4_1_ <= lvt_2_1_ + this.field_72698_e; ++lvt_4_1_) {
         for(int lvt_5_1_ = lvt_3_1_ - this.field_72698_e; lvt_5_1_ <= lvt_3_1_ + this.field_72698_e; ++lvt_5_1_) {
            PlayerManager.PlayerInstance lvt_6_1_ = this.func_72690_a(lvt_4_1_, lvt_5_1_, false);
            if(lvt_6_1_ != null) {
               lvt_6_1_.func_73252_b(p_72695_1_);
            }
         }
      }

      this.field_72699_b.remove(p_72695_1_);
   }

   private boolean func_72684_a(int p_72684_1_, int p_72684_2_, int p_72684_3_, int p_72684_4_, int p_72684_5_) {
      int lvt_6_1_ = p_72684_1_ - p_72684_3_;
      int lvt_7_1_ = p_72684_2_ - p_72684_4_;
      return lvt_6_1_ >= -p_72684_5_ && lvt_6_1_ <= p_72684_5_?lvt_7_1_ >= -p_72684_5_ && lvt_7_1_ <= p_72684_5_:false;
   }

   public void func_72685_d(EntityPlayerMP p_72685_1_) {
      int lvt_2_1_ = (int)p_72685_1_.field_70165_t >> 4;
      int lvt_3_1_ = (int)p_72685_1_.field_70161_v >> 4;
      double lvt_4_1_ = p_72685_1_.field_71131_d - p_72685_1_.field_70165_t;
      double lvt_6_1_ = p_72685_1_.field_71132_e - p_72685_1_.field_70161_v;
      double lvt_8_1_ = lvt_4_1_ * lvt_4_1_ + lvt_6_1_ * lvt_6_1_;
      if(lvt_8_1_ >= 64.0D) {
         int lvt_10_1_ = (int)p_72685_1_.field_71131_d >> 4;
         int lvt_11_1_ = (int)p_72685_1_.field_71132_e >> 4;
         int lvt_12_1_ = this.field_72698_e;
         int lvt_13_1_ = lvt_2_1_ - lvt_10_1_;
         int lvt_14_1_ = lvt_3_1_ - lvt_11_1_;
         if(lvt_13_1_ != 0 || lvt_14_1_ != 0) {
            for(int lvt_15_1_ = lvt_2_1_ - lvt_12_1_; lvt_15_1_ <= lvt_2_1_ + lvt_12_1_; ++lvt_15_1_) {
               for(int lvt_16_1_ = lvt_3_1_ - lvt_12_1_; lvt_16_1_ <= lvt_3_1_ + lvt_12_1_; ++lvt_16_1_) {
                  if(!this.func_72684_a(lvt_15_1_, lvt_16_1_, lvt_10_1_, lvt_11_1_, lvt_12_1_)) {
                     this.func_72690_a(lvt_15_1_, lvt_16_1_, true).func_73255_a(p_72685_1_);
                  }

                  if(!this.func_72684_a(lvt_15_1_ - lvt_13_1_, lvt_16_1_ - lvt_14_1_, lvt_2_1_, lvt_3_1_, lvt_12_1_)) {
                     PlayerManager.PlayerInstance lvt_17_1_ = this.func_72690_a(lvt_15_1_ - lvt_13_1_, lvt_16_1_ - lvt_14_1_, false);
                     if(lvt_17_1_ != null) {
                        lvt_17_1_.func_73252_b(p_72685_1_);
                     }
                  }
               }
            }

            this.func_72691_b(p_72685_1_);
            p_72685_1_.field_71131_d = p_72685_1_.field_70165_t;
            p_72685_1_.field_71132_e = p_72685_1_.field_70161_v;
         }
      }
   }

   public boolean func_72694_a(EntityPlayerMP p_72694_1_, int p_72694_2_, int p_72694_3_) {
      PlayerManager.PlayerInstance lvt_4_1_ = this.func_72690_a(p_72694_2_, p_72694_3_, false);
      return lvt_4_1_ != null && lvt_4_1_.field_73263_b.contains(p_72694_1_) && !p_72694_1_.field_71129_f.contains(lvt_4_1_.field_73264_c);
   }

   public void func_152622_a(int p_152622_1_) {
      p_152622_1_ = MathHelper.func_76125_a(p_152622_1_, 3, 32);
      if(p_152622_1_ != this.field_72698_e) {
         int lvt_2_1_ = p_152622_1_ - this.field_72698_e;

         for(EntityPlayerMP lvt_5_1_ : Lists.newArrayList(this.field_72699_b)) {
            int lvt_6_1_ = (int)lvt_5_1_.field_70165_t >> 4;
            int lvt_7_1_ = (int)lvt_5_1_.field_70161_v >> 4;
            if(lvt_2_1_ > 0) {
               for(int lvt_8_1_ = lvt_6_1_ - p_152622_1_; lvt_8_1_ <= lvt_6_1_ + p_152622_1_; ++lvt_8_1_) {
                  for(int lvt_9_1_ = lvt_7_1_ - p_152622_1_; lvt_9_1_ <= lvt_7_1_ + p_152622_1_; ++lvt_9_1_) {
                     PlayerManager.PlayerInstance lvt_10_1_ = this.func_72690_a(lvt_8_1_, lvt_9_1_, true);
                     if(!lvt_10_1_.field_73263_b.contains(lvt_5_1_)) {
                        lvt_10_1_.func_73255_a(lvt_5_1_);
                     }
                  }
               }
            } else {
               for(int lvt_8_2_ = lvt_6_1_ - this.field_72698_e; lvt_8_2_ <= lvt_6_1_ + this.field_72698_e; ++lvt_8_2_) {
                  for(int lvt_9_2_ = lvt_7_1_ - this.field_72698_e; lvt_9_2_ <= lvt_7_1_ + this.field_72698_e; ++lvt_9_2_) {
                     if(!this.func_72684_a(lvt_8_2_, lvt_9_2_, lvt_6_1_, lvt_7_1_, p_152622_1_)) {
                        this.func_72690_a(lvt_8_2_, lvt_9_2_, true).func_73252_b(lvt_5_1_);
                     }
                  }
               }
            }
         }

         this.field_72698_e = p_152622_1_;
      }
   }

   public static int func_72686_a(int p_72686_0_) {
      return p_72686_0_ * 16 - 16;
   }

   class PlayerInstance {
      private final List<EntityPlayerMP> field_73263_b = Lists.newArrayList();
      private final ChunkCoordIntPair field_73264_c;
      private short[] field_151254_d = new short[64];
      private int field_73262_e;
      private int field_73260_f;
      private long field_111198_g;

      public PlayerInstance(int p_i1518_2_, int p_i1518_3_) {
         this.field_73264_c = new ChunkCoordIntPair(p_i1518_2_, p_i1518_3_);
         PlayerManager.this.func_72688_a().field_73059_b.func_73158_c(p_i1518_2_, p_i1518_3_);
      }

      public void func_73255_a(EntityPlayerMP p_73255_1_) {
         if(this.field_73263_b.contains(p_73255_1_)) {
            PlayerManager.field_152627_a.debug("Failed to add player. {} already is in chunk {}, {}", new Object[]{p_73255_1_, Integer.valueOf(this.field_73264_c.field_77276_a), Integer.valueOf(this.field_73264_c.field_77275_b)});
         } else {
            if(this.field_73263_b.isEmpty()) {
               this.field_111198_g = PlayerManager.this.field_72701_a.func_82737_E();
            }

            this.field_73263_b.add(p_73255_1_);
            p_73255_1_.field_71129_f.add(this.field_73264_c);
         }
      }

      public void func_73252_b(EntityPlayerMP p_73252_1_) {
         if(this.field_73263_b.contains(p_73252_1_)) {
            Chunk lvt_2_1_ = PlayerManager.this.field_72701_a.func_72964_e(this.field_73264_c.field_77276_a, this.field_73264_c.field_77275_b);
            if(lvt_2_1_.func_150802_k()) {
               p_73252_1_.field_71135_a.func_147359_a(new S21PacketChunkData(lvt_2_1_, true, 0));
            }

            this.field_73263_b.remove(p_73252_1_);
            p_73252_1_.field_71129_f.remove(this.field_73264_c);
            if(this.field_73263_b.isEmpty()) {
               long lvt_3_1_ = (long)this.field_73264_c.field_77276_a + 2147483647L | (long)this.field_73264_c.field_77275_b + 2147483647L << 32;
               this.func_111196_a(lvt_2_1_);
               PlayerManager.this.field_72700_c.func_76159_d(lvt_3_1_);
               PlayerManager.this.field_111193_e.remove(this);
               if(this.field_73262_e > 0) {
                  PlayerManager.this.field_72697_d.remove(this);
               }

               PlayerManager.this.func_72688_a().field_73059_b.func_73241_b(this.field_73264_c.field_77276_a, this.field_73264_c.field_77275_b);
            }

         }
      }

      public void func_111194_a() {
         this.func_111196_a(PlayerManager.this.field_72701_a.func_72964_e(this.field_73264_c.field_77276_a, this.field_73264_c.field_77275_b));
      }

      private void func_111196_a(Chunk p_111196_1_) {
         p_111196_1_.func_177415_c(p_111196_1_.func_177416_w() + PlayerManager.this.field_72701_a.func_82737_E() - this.field_111198_g);
         this.field_111198_g = PlayerManager.this.field_72701_a.func_82737_E();
      }

      public void func_151253_a(int p_151253_1_, int p_151253_2_, int p_151253_3_) {
         if(this.field_73262_e == 0) {
            PlayerManager.this.field_72697_d.add(this);
         }

         this.field_73260_f |= 1 << (p_151253_2_ >> 4);
         if(this.field_73262_e < 64) {
            short lvt_4_1_ = (short)(p_151253_1_ << 12 | p_151253_3_ << 8 | p_151253_2_);

            for(int lvt_5_1_ = 0; lvt_5_1_ < this.field_73262_e; ++lvt_5_1_) {
               if(this.field_151254_d[lvt_5_1_] == lvt_4_1_) {
                  return;
               }
            }

            this.field_151254_d[this.field_73262_e++] = lvt_4_1_;
         }

      }

      public void func_151251_a(Packet p_151251_1_) {
         for(int lvt_2_1_ = 0; lvt_2_1_ < this.field_73263_b.size(); ++lvt_2_1_) {
            EntityPlayerMP lvt_3_1_ = (EntityPlayerMP)this.field_73263_b.get(lvt_2_1_);
            if(!lvt_3_1_.field_71129_f.contains(this.field_73264_c)) {
               lvt_3_1_.field_71135_a.func_147359_a(p_151251_1_);
            }
         }

      }

      public void func_73254_a() {
         if(this.field_73262_e != 0) {
            if(this.field_73262_e == 1) {
               int lvt_1_1_ = (this.field_151254_d[0] >> 12 & 15) + this.field_73264_c.field_77276_a * 16;
               int lvt_2_1_ = this.field_151254_d[0] & 255;
               int lvt_3_1_ = (this.field_151254_d[0] >> 8 & 15) + this.field_73264_c.field_77275_b * 16;
               BlockPos lvt_4_1_ = new BlockPos(lvt_1_1_, lvt_2_1_, lvt_3_1_);
               this.func_151251_a(new S23PacketBlockChange(PlayerManager.this.field_72701_a, lvt_4_1_));
               if(PlayerManager.this.field_72701_a.func_180495_p(lvt_4_1_).func_177230_c().func_149716_u()) {
                  this.func_151252_a(PlayerManager.this.field_72701_a.func_175625_s(lvt_4_1_));
               }
            } else if(this.field_73262_e == 64) {
               int lvt_1_2_ = this.field_73264_c.field_77276_a * 16;
               int lvt_2_2_ = this.field_73264_c.field_77275_b * 16;
               this.func_151251_a(new S21PacketChunkData(PlayerManager.this.field_72701_a.func_72964_e(this.field_73264_c.field_77276_a, this.field_73264_c.field_77275_b), false, this.field_73260_f));

               for(int lvt_3_2_ = 0; lvt_3_2_ < 16; ++lvt_3_2_) {
                  if((this.field_73260_f & 1 << lvt_3_2_) != 0) {
                     int lvt_4_2_ = lvt_3_2_ << 4;
                     List<TileEntity> lvt_5_1_ = PlayerManager.this.field_72701_a.func_147486_a(lvt_1_2_, lvt_4_2_, lvt_2_2_, lvt_1_2_ + 16, lvt_4_2_ + 16, lvt_2_2_ + 16);

                     for(int lvt_6_1_ = 0; lvt_6_1_ < lvt_5_1_.size(); ++lvt_6_1_) {
                        this.func_151252_a((TileEntity)lvt_5_1_.get(lvt_6_1_));
                     }
                  }
               }
            } else {
               this.func_151251_a(new S22PacketMultiBlockChange(this.field_73262_e, this.field_151254_d, PlayerManager.this.field_72701_a.func_72964_e(this.field_73264_c.field_77276_a, this.field_73264_c.field_77275_b)));

               for(int lvt_1_3_ = 0; lvt_1_3_ < this.field_73262_e; ++lvt_1_3_) {
                  int lvt_2_3_ = (this.field_151254_d[lvt_1_3_] >> 12 & 15) + this.field_73264_c.field_77276_a * 16;
                  int lvt_3_3_ = this.field_151254_d[lvt_1_3_] & 255;
                  int lvt_4_3_ = (this.field_151254_d[lvt_1_3_] >> 8 & 15) + this.field_73264_c.field_77275_b * 16;
                  BlockPos lvt_5_2_ = new BlockPos(lvt_2_3_, lvt_3_3_, lvt_4_3_);
                  if(PlayerManager.this.field_72701_a.func_180495_p(lvt_5_2_).func_177230_c().func_149716_u()) {
                     this.func_151252_a(PlayerManager.this.field_72701_a.func_175625_s(lvt_5_2_));
                  }
               }
            }

            this.field_73262_e = 0;
            this.field_73260_f = 0;
         }
      }

      private void func_151252_a(TileEntity p_151252_1_) {
         if(p_151252_1_ != null) {
            Packet lvt_2_1_ = p_151252_1_.func_145844_m();
            if(lvt_2_1_ != null) {
               this.func_151251_a(lvt_2_1_);
            }
         }

      }
   }
}
