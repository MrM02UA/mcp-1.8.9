package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.nio.FloatBuffer;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RegionRenderCache;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RenderChunk {
   private World field_178588_d;
   private final RenderGlobal field_178589_e;
   public static int field_178592_a;
   private BlockPos field_178586_f;
   public CompiledChunk field_178590_b = CompiledChunk.field_178502_a;
   private final ReentrantLock field_178587_g = new ReentrantLock();
   private final ReentrantLock field_178598_h = new ReentrantLock();
   private ChunkCompileTaskGenerator field_178599_i = null;
   private final Set<TileEntity> field_181056_j = Sets.newHashSet();
   private final int field_178596_j;
   private final FloatBuffer field_178597_k = GLAllocation.func_74529_h(16);
   private final VertexBuffer[] field_178594_l = new VertexBuffer[EnumWorldBlockLayer.values().length];
   public AxisAlignedBB field_178591_c;
   private int field_178595_m = -1;
   private boolean field_178593_n = true;
   private EnumMap<EnumFacing, BlockPos> field_181702_p = Maps.newEnumMap(EnumFacing.class);

   public RenderChunk(World p_i46197_1_, RenderGlobal p_i46197_2_, BlockPos p_i46197_3_, int p_i46197_4_) {
      this.field_178588_d = p_i46197_1_;
      this.field_178589_e = p_i46197_2_;
      this.field_178596_j = p_i46197_4_;
      if(!p_i46197_3_.equals(this.func_178568_j())) {
         this.func_178576_a(p_i46197_3_);
      }

      if(OpenGlHelper.func_176075_f()) {
         for(int lvt_5_1_ = 0; lvt_5_1_ < EnumWorldBlockLayer.values().length; ++lvt_5_1_) {
            this.field_178594_l[lvt_5_1_] = new VertexBuffer(DefaultVertexFormats.field_176600_a);
         }
      }

   }

   public boolean func_178577_a(int p_178577_1_) {
      if(this.field_178595_m == p_178577_1_) {
         return false;
      } else {
         this.field_178595_m = p_178577_1_;
         return true;
      }
   }

   public VertexBuffer func_178565_b(int p_178565_1_) {
      return this.field_178594_l[p_178565_1_];
   }

   public void func_178576_a(BlockPos p_178576_1_) {
      this.func_178585_h();
      this.field_178586_f = p_178576_1_;
      this.field_178591_c = new AxisAlignedBB(p_178576_1_, p_178576_1_.func_177982_a(16, 16, 16));

      for(EnumFacing lvt_5_1_ : EnumFacing.values()) {
         this.field_181702_p.put(lvt_5_1_, p_178576_1_.func_177967_a(lvt_5_1_, 16));
      }

      this.func_178567_n();
   }

   public void func_178570_a(float p_178570_1_, float p_178570_2_, float p_178570_3_, ChunkCompileTaskGenerator p_178570_4_) {
      CompiledChunk lvt_5_1_ = p_178570_4_.func_178544_c();
      if(lvt_5_1_.func_178487_c() != null && !lvt_5_1_.func_178491_b(EnumWorldBlockLayer.TRANSLUCENT)) {
         this.func_178573_a(p_178570_4_.func_178545_d().func_179038_a(EnumWorldBlockLayer.TRANSLUCENT), this.field_178586_f);
         p_178570_4_.func_178545_d().func_179038_a(EnumWorldBlockLayer.TRANSLUCENT).func_178993_a(lvt_5_1_.func_178487_c());
         this.func_178584_a(EnumWorldBlockLayer.TRANSLUCENT, p_178570_1_, p_178570_2_, p_178570_3_, p_178570_4_.func_178545_d().func_179038_a(EnumWorldBlockLayer.TRANSLUCENT), lvt_5_1_);
      }
   }

   public void func_178581_b(float p_178581_1_, float p_178581_2_, float p_178581_3_, ChunkCompileTaskGenerator p_178581_4_) {
      CompiledChunk lvt_5_1_ = new CompiledChunk();
      int lvt_6_1_ = 1;
      BlockPos lvt_7_1_ = this.field_178586_f;
      BlockPos lvt_8_1_ = lvt_7_1_.func_177982_a(15, 15, 15);
      p_178581_4_.func_178540_f().lock();

      IBlockAccess lvt_9_1_;
      try {
         if(p_178581_4_.func_178546_a() != ChunkCompileTaskGenerator.Status.COMPILING) {
            return;
         }

         lvt_9_1_ = new RegionRenderCache(this.field_178588_d, lvt_7_1_.func_177982_a(-1, -1, -1), lvt_8_1_.func_177982_a(1, 1, 1), 1);
         p_178581_4_.func_178543_a(lvt_5_1_);
      } finally {
         p_178581_4_.func_178540_f().unlock();
      }

      VisGraph lvt_10_1_ = new VisGraph();
      HashSet lvt_11_1_ = Sets.newHashSet();
      if(!lvt_9_1_.func_72806_N()) {
         ++field_178592_a;
         boolean[] lvt_12_1_ = new boolean[EnumWorldBlockLayer.values().length];
         BlockRendererDispatcher lvt_13_1_ = Minecraft.func_71410_x().func_175602_ab();

         for(BlockPos.MutableBlockPos lvt_15_1_ : BlockPos.func_177975_b(lvt_7_1_, lvt_8_1_)) {
            IBlockState lvt_16_1_ = lvt_9_1_.func_180495_p(lvt_15_1_);
            Block lvt_17_1_ = lvt_16_1_.func_177230_c();
            if(lvt_17_1_.func_149662_c()) {
               lvt_10_1_.func_178606_a(lvt_15_1_);
            }

            if(lvt_17_1_.func_149716_u()) {
               TileEntity lvt_18_1_ = lvt_9_1_.func_175625_s(new BlockPos(lvt_15_1_));
               TileEntitySpecialRenderer<TileEntity> lvt_19_1_ = TileEntityRendererDispatcher.field_147556_a.<TileEntity>func_147547_b(lvt_18_1_);
               if(lvt_18_1_ != null && lvt_19_1_ != null) {
                  lvt_5_1_.func_178490_a(lvt_18_1_);
                  if(lvt_19_1_.func_181055_a()) {
                     lvt_11_1_.add(lvt_18_1_);
                  }
               }
            }

            EnumWorldBlockLayer lvt_18_2_ = lvt_17_1_.func_180664_k();
            int lvt_19_2_ = lvt_18_2_.ordinal();
            if(lvt_17_1_.func_149645_b() != -1) {
               WorldRenderer lvt_20_1_ = p_178581_4_.func_178545_d().func_179039_a(lvt_19_2_);
               if(!lvt_5_1_.func_178492_d(lvt_18_2_)) {
                  lvt_5_1_.func_178493_c(lvt_18_2_);
                  this.func_178573_a(lvt_20_1_, lvt_7_1_);
               }

               lvt_12_1_[lvt_19_2_] |= lvt_13_1_.func_175018_a(lvt_16_1_, lvt_15_1_, lvt_9_1_, lvt_20_1_);
            }
         }

         for(EnumWorldBlockLayer lvt_17_2_ : EnumWorldBlockLayer.values()) {
            if(lvt_12_1_[lvt_17_2_.ordinal()]) {
               lvt_5_1_.func_178486_a(lvt_17_2_);
            }

            if(lvt_5_1_.func_178492_d(lvt_17_2_)) {
               this.func_178584_a(lvt_17_2_, p_178581_1_, p_178581_2_, p_178581_3_, p_178581_4_.func_178545_d().func_179038_a(lvt_17_2_), lvt_5_1_);
            }
         }
      }

      lvt_5_1_.func_178488_a(lvt_10_1_.func_178607_a());
      this.field_178587_g.lock();

      try {
         Set<TileEntity> lvt_12_2_ = Sets.newHashSet(lvt_11_1_);
         Set<TileEntity> lvt_13_2_ = Sets.newHashSet(this.field_181056_j);
         lvt_12_2_.removeAll(this.field_181056_j);
         lvt_13_2_.removeAll(lvt_11_1_);
         this.field_181056_j.clear();
         this.field_181056_j.addAll(lvt_11_1_);
         this.field_178589_e.func_181023_a(lvt_13_2_, lvt_12_2_);
      } finally {
         this.field_178587_g.unlock();
      }

   }

   protected void func_178578_b() {
      this.field_178587_g.lock();

      try {
         if(this.field_178599_i != null && this.field_178599_i.func_178546_a() != ChunkCompileTaskGenerator.Status.DONE) {
            this.field_178599_i.func_178542_e();
            this.field_178599_i = null;
         }
      } finally {
         this.field_178587_g.unlock();
      }

   }

   public ReentrantLock func_178579_c() {
      return this.field_178587_g;
   }

   public ChunkCompileTaskGenerator func_178574_d() {
      this.field_178587_g.lock();

      ChunkCompileTaskGenerator var1;
      try {
         this.func_178578_b();
         this.field_178599_i = new ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.REBUILD_CHUNK);
         var1 = this.field_178599_i;
      } finally {
         this.field_178587_g.unlock();
      }

      return var1;
   }

   public ChunkCompileTaskGenerator func_178582_e() {
      this.field_178587_g.lock();

      ChunkCompileTaskGenerator var1;
      try {
         if(this.field_178599_i == null || this.field_178599_i.func_178546_a() != ChunkCompileTaskGenerator.Status.PENDING) {
            if(this.field_178599_i != null && this.field_178599_i.func_178546_a() != ChunkCompileTaskGenerator.Status.DONE) {
               this.field_178599_i.func_178542_e();
               this.field_178599_i = null;
            }

            this.field_178599_i = new ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY);
            this.field_178599_i.func_178543_a(this.field_178590_b);
            var1 = this.field_178599_i;
            return var1;
         }

         var1 = null;
      } finally {
         this.field_178587_g.unlock();
      }

      return var1;
   }

   private void func_178573_a(WorldRenderer p_178573_1_, BlockPos p_178573_2_) {
      p_178573_1_.func_181668_a(7, DefaultVertexFormats.field_176600_a);
      p_178573_1_.func_178969_c((double)(-p_178573_2_.func_177958_n()), (double)(-p_178573_2_.func_177956_o()), (double)(-p_178573_2_.func_177952_p()));
   }

   private void func_178584_a(EnumWorldBlockLayer p_178584_1_, float p_178584_2_, float p_178584_3_, float p_178584_4_, WorldRenderer p_178584_5_, CompiledChunk p_178584_6_) {
      if(p_178584_1_ == EnumWorldBlockLayer.TRANSLUCENT && !p_178584_6_.func_178491_b(p_178584_1_)) {
         p_178584_5_.func_181674_a(p_178584_2_, p_178584_3_, p_178584_4_);
         p_178584_6_.func_178494_a(p_178584_5_.func_181672_a());
      }

      p_178584_5_.func_178977_d();
   }

   private void func_178567_n() {
      GlStateManager.func_179094_E();
      GlStateManager.func_179096_D();
      float lvt_1_1_ = 1.000001F;
      GlStateManager.func_179109_b(-8.0F, -8.0F, -8.0F);
      GlStateManager.func_179152_a(lvt_1_1_, lvt_1_1_, lvt_1_1_);
      GlStateManager.func_179109_b(8.0F, 8.0F, 8.0F);
      GlStateManager.func_179111_a(2982, this.field_178597_k);
      GlStateManager.func_179121_F();
   }

   public void func_178572_f() {
      GlStateManager.func_179110_a(this.field_178597_k);
   }

   public CompiledChunk func_178571_g() {
      return this.field_178590_b;
   }

   public void func_178580_a(CompiledChunk p_178580_1_) {
      this.field_178598_h.lock();

      try {
         this.field_178590_b = p_178580_1_;
      } finally {
         this.field_178598_h.unlock();
      }

   }

   public void func_178585_h() {
      this.func_178578_b();
      this.field_178590_b = CompiledChunk.field_178502_a;
   }

   public void func_178566_a() {
      this.func_178585_h();
      this.field_178588_d = null;

      for(int lvt_1_1_ = 0; lvt_1_1_ < EnumWorldBlockLayer.values().length; ++lvt_1_1_) {
         if(this.field_178594_l[lvt_1_1_] != null) {
            this.field_178594_l[lvt_1_1_].func_177362_c();
         }
      }

   }

   public BlockPos func_178568_j() {
      return this.field_178586_f;
   }

   public void func_178575_a(boolean p_178575_1_) {
      this.field_178593_n = p_178575_1_;
   }

   public boolean func_178569_m() {
      return this.field_178593_n;
   }

   public BlockPos func_181701_a(EnumFacing p_181701_1_) {
      return (BlockPos)this.field_181702_p.get(p_181701_1_);
   }
}
