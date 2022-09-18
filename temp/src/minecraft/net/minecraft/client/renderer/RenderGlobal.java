package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.ChunkRenderContainer;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderList;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VboRenderList;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.ListChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.VboChunkFactory;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemRecord;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Matrix4f;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vector3d;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class RenderGlobal implements IWorldAccess, IResourceManagerReloadListener {
   private static final Logger field_147599_m = LogManager.getLogger();
   private static final ResourceLocation field_110927_h = new ResourceLocation("textures/environment/moon_phases.png");
   private static final ResourceLocation field_110928_i = new ResourceLocation("textures/environment/sun.png");
   private static final ResourceLocation field_110925_j = new ResourceLocation("textures/environment/clouds.png");
   private static final ResourceLocation field_110926_k = new ResourceLocation("textures/environment/end_sky.png");
   private static final ResourceLocation field_175006_g = new ResourceLocation("textures/misc/forcefield.png");
   private final Minecraft field_72777_q;
   private final TextureManager field_72770_i;
   private final RenderManager field_175010_j;
   private WorldClient field_72769_h;
   private Set<RenderChunk> field_175009_l = Sets.newLinkedHashSet();
   private List<RenderGlobal.ContainerLocalRenderInformation> field_72755_R = Lists.newArrayListWithCapacity(69696);
   private final Set<TileEntity> field_181024_n = Sets.newHashSet();
   private ViewFrustum field_175008_n;
   private int field_72772_v = -1;
   private int field_72771_w = -1;
   private int field_72781_x = -1;
   private VertexFormat field_175014_r;
   private VertexBuffer field_175013_s;
   private VertexBuffer field_175012_t;
   private VertexBuffer field_175011_u;
   private int field_72773_u;
   private final Map<Integer, DestroyBlockProgress> field_72738_E = Maps.newHashMap();
   private final Map<BlockPos, ISound> field_147593_P = Maps.newHashMap();
   private final TextureAtlasSprite[] field_94141_F = new TextureAtlasSprite[10];
   private Framebuffer field_175015_z;
   private ShaderGroup field_174991_A;
   private double field_174992_B = Double.MIN_VALUE;
   private double field_174993_C = Double.MIN_VALUE;
   private double field_174987_D = Double.MIN_VALUE;
   private int field_174988_E = Integer.MIN_VALUE;
   private int field_174989_F = Integer.MIN_VALUE;
   private int field_174990_G = Integer.MIN_VALUE;
   private double field_174997_H = Double.MIN_VALUE;
   private double field_174998_I = Double.MIN_VALUE;
   private double field_174999_J = Double.MIN_VALUE;
   private double field_175000_K = Double.MIN_VALUE;
   private double field_174994_L = Double.MIN_VALUE;
   private final ChunkRenderDispatcher field_174995_M = new ChunkRenderDispatcher();
   private ChunkRenderContainer field_174996_N;
   private int field_72739_F = -1;
   private int field_72740_G = 2;
   private int field_72748_H;
   private int field_72749_I;
   private int field_72750_J;
   private boolean field_175002_T = false;
   private ClippingHelper field_175001_U;
   private final Vector4f[] field_175004_V = new Vector4f[8];
   private final Vector3d field_175003_W = new Vector3d();
   private boolean field_175005_X = false;
   IRenderChunkFactory field_175007_a;
   private double field_147596_f;
   private double field_147597_g;
   private double field_147602_h;
   private boolean field_147595_R = true;

   public RenderGlobal(Minecraft p_i1249_1_) {
      this.field_72777_q = p_i1249_1_;
      this.field_175010_j = p_i1249_1_.func_175598_ae();
      this.field_72770_i = p_i1249_1_.func_110434_K();
      this.field_72770_i.func_110577_a(field_175006_g);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GlStateManager.func_179144_i(0);
      this.func_174971_n();
      this.field_175005_X = OpenGlHelper.func_176075_f();
      if(this.field_175005_X) {
         this.field_174996_N = new VboRenderList();
         this.field_175007_a = new VboChunkFactory();
      } else {
         this.field_174996_N = new RenderList();
         this.field_175007_a = new ListChunkFactory();
      }

      this.field_175014_r = new VertexFormat();
      this.field_175014_r.func_181721_a(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.POSITION, 3));
      this.func_174963_q();
      this.func_174980_p();
      this.func_174964_o();
   }

   public void func_110549_a(IResourceManager p_110549_1_) {
      this.func_174971_n();
   }

   private void func_174971_n() {
      TextureMap lvt_1_1_ = this.field_72777_q.func_147117_R();

      for(int lvt_2_1_ = 0; lvt_2_1_ < this.field_94141_F.length; ++lvt_2_1_) {
         this.field_94141_F[lvt_2_1_] = lvt_1_1_.func_110572_b("minecraft:blocks/destroy_stage_" + lvt_2_1_);
      }

   }

   public void func_174966_b() {
      if(OpenGlHelper.field_148824_g) {
         if(ShaderLinkHelper.func_148074_b() == null) {
            ShaderLinkHelper.func_148076_a();
         }

         ResourceLocation lvt_1_1_ = new ResourceLocation("shaders/post/entity_outline.json");

         try {
            this.field_174991_A = new ShaderGroup(this.field_72777_q.func_110434_K(), this.field_72777_q.func_110442_L(), this.field_72777_q.func_147110_a(), lvt_1_1_);
            this.field_174991_A.func_148026_a(this.field_72777_q.field_71443_c, this.field_72777_q.field_71440_d);
            this.field_175015_z = this.field_174991_A.func_177066_a("final");
         } catch (IOException var3) {
            field_147599_m.warn("Failed to load shader: " + lvt_1_1_, var3);
            this.field_174991_A = null;
            this.field_175015_z = null;
         } catch (JsonSyntaxException var4) {
            field_147599_m.warn("Failed to load shader: " + lvt_1_1_, var4);
            this.field_174991_A = null;
            this.field_175015_z = null;
         }
      } else {
         this.field_174991_A = null;
         this.field_175015_z = null;
      }

   }

   public void func_174975_c() {
      if(this.func_174985_d()) {
         GlStateManager.func_179147_l();
         GlStateManager.func_179120_a(770, 771, 0, 1);
         this.field_175015_z.func_178038_a(this.field_72777_q.field_71443_c, this.field_72777_q.field_71440_d, false);
         GlStateManager.func_179084_k();
      }

   }

   protected boolean func_174985_d() {
      return this.field_175015_z != null && this.field_174991_A != null && this.field_72777_q.field_71439_g != null && this.field_72777_q.field_71439_g.func_175149_v() && this.field_72777_q.field_71474_y.field_178883_an.func_151470_d();
   }

   private void func_174964_o() {
      Tessellator lvt_1_1_ = Tessellator.func_178181_a();
      WorldRenderer lvt_2_1_ = lvt_1_1_.func_178180_c();
      if(this.field_175011_u != null) {
         this.field_175011_u.func_177362_c();
      }

      if(this.field_72781_x >= 0) {
         GLAllocation.func_74523_b(this.field_72781_x);
         this.field_72781_x = -1;
      }

      if(this.field_175005_X) {
         this.field_175011_u = new VertexBuffer(this.field_175014_r);
         this.func_174968_a(lvt_2_1_, -16.0F, true);
         lvt_2_1_.func_178977_d();
         lvt_2_1_.func_178965_a();
         this.field_175011_u.func_181722_a(lvt_2_1_.func_178966_f());
      } else {
         this.field_72781_x = GLAllocation.func_74526_a(1);
         GL11.glNewList(this.field_72781_x, 4864);
         this.func_174968_a(lvt_2_1_, -16.0F, true);
         lvt_1_1_.func_78381_a();
         GL11.glEndList();
      }

   }

   private void func_174980_p() {
      Tessellator lvt_1_1_ = Tessellator.func_178181_a();
      WorldRenderer lvt_2_1_ = lvt_1_1_.func_178180_c();
      if(this.field_175012_t != null) {
         this.field_175012_t.func_177362_c();
      }

      if(this.field_72771_w >= 0) {
         GLAllocation.func_74523_b(this.field_72771_w);
         this.field_72771_w = -1;
      }

      if(this.field_175005_X) {
         this.field_175012_t = new VertexBuffer(this.field_175014_r);
         this.func_174968_a(lvt_2_1_, 16.0F, false);
         lvt_2_1_.func_178977_d();
         lvt_2_1_.func_178965_a();
         this.field_175012_t.func_181722_a(lvt_2_1_.func_178966_f());
      } else {
         this.field_72771_w = GLAllocation.func_74526_a(1);
         GL11.glNewList(this.field_72771_w, 4864);
         this.func_174968_a(lvt_2_1_, 16.0F, false);
         lvt_1_1_.func_78381_a();
         GL11.glEndList();
      }

   }

   private void func_174968_a(WorldRenderer p_174968_1_, float p_174968_2_, boolean p_174968_3_) {
      int lvt_4_1_ = 64;
      int lvt_5_1_ = 6;
      p_174968_1_.func_181668_a(7, DefaultVertexFormats.field_181705_e);

      for(int lvt_6_1_ = -384; lvt_6_1_ <= 384; lvt_6_1_ += 64) {
         for(int lvt_7_1_ = -384; lvt_7_1_ <= 384; lvt_7_1_ += 64) {
            float lvt_8_1_ = (float)lvt_6_1_;
            float lvt_9_1_ = (float)(lvt_6_1_ + 64);
            if(p_174968_3_) {
               lvt_9_1_ = (float)lvt_6_1_;
               lvt_8_1_ = (float)(lvt_6_1_ + 64);
            }

            p_174968_1_.func_181662_b((double)lvt_8_1_, (double)p_174968_2_, (double)lvt_7_1_).func_181675_d();
            p_174968_1_.func_181662_b((double)lvt_9_1_, (double)p_174968_2_, (double)lvt_7_1_).func_181675_d();
            p_174968_1_.func_181662_b((double)lvt_9_1_, (double)p_174968_2_, (double)(lvt_7_1_ + 64)).func_181675_d();
            p_174968_1_.func_181662_b((double)lvt_8_1_, (double)p_174968_2_, (double)(lvt_7_1_ + 64)).func_181675_d();
         }
      }

   }

   private void func_174963_q() {
      Tessellator lvt_1_1_ = Tessellator.func_178181_a();
      WorldRenderer lvt_2_1_ = lvt_1_1_.func_178180_c();
      if(this.field_175013_s != null) {
         this.field_175013_s.func_177362_c();
      }

      if(this.field_72772_v >= 0) {
         GLAllocation.func_74523_b(this.field_72772_v);
         this.field_72772_v = -1;
      }

      if(this.field_175005_X) {
         this.field_175013_s = new VertexBuffer(this.field_175014_r);
         this.func_180444_a(lvt_2_1_);
         lvt_2_1_.func_178977_d();
         lvt_2_1_.func_178965_a();
         this.field_175013_s.func_181722_a(lvt_2_1_.func_178966_f());
      } else {
         this.field_72772_v = GLAllocation.func_74526_a(1);
         GlStateManager.func_179094_E();
         GL11.glNewList(this.field_72772_v, 4864);
         this.func_180444_a(lvt_2_1_);
         lvt_1_1_.func_78381_a();
         GL11.glEndList();
         GlStateManager.func_179121_F();
      }

   }

   private void func_180444_a(WorldRenderer p_180444_1_) {
      Random lvt_2_1_ = new Random(10842L);
      p_180444_1_.func_181668_a(7, DefaultVertexFormats.field_181705_e);

      for(int lvt_3_1_ = 0; lvt_3_1_ < 1500; ++lvt_3_1_) {
         double lvt_4_1_ = (double)(lvt_2_1_.nextFloat() * 2.0F - 1.0F);
         double lvt_6_1_ = (double)(lvt_2_1_.nextFloat() * 2.0F - 1.0F);
         double lvt_8_1_ = (double)(lvt_2_1_.nextFloat() * 2.0F - 1.0F);
         double lvt_10_1_ = (double)(0.15F + lvt_2_1_.nextFloat() * 0.1F);
         double lvt_12_1_ = lvt_4_1_ * lvt_4_1_ + lvt_6_1_ * lvt_6_1_ + lvt_8_1_ * lvt_8_1_;
         if(lvt_12_1_ < 1.0D && lvt_12_1_ > 0.01D) {
            lvt_12_1_ = 1.0D / Math.sqrt(lvt_12_1_);
            lvt_4_1_ = lvt_4_1_ * lvt_12_1_;
            lvt_6_1_ = lvt_6_1_ * lvt_12_1_;
            lvt_8_1_ = lvt_8_1_ * lvt_12_1_;
            double lvt_14_1_ = lvt_4_1_ * 100.0D;
            double lvt_16_1_ = lvt_6_1_ * 100.0D;
            double lvt_18_1_ = lvt_8_1_ * 100.0D;
            double lvt_20_1_ = Math.atan2(lvt_4_1_, lvt_8_1_);
            double lvt_22_1_ = Math.sin(lvt_20_1_);
            double lvt_24_1_ = Math.cos(lvt_20_1_);
            double lvt_26_1_ = Math.atan2(Math.sqrt(lvt_4_1_ * lvt_4_1_ + lvt_8_1_ * lvt_8_1_), lvt_6_1_);
            double lvt_28_1_ = Math.sin(lvt_26_1_);
            double lvt_30_1_ = Math.cos(lvt_26_1_);
            double lvt_32_1_ = lvt_2_1_.nextDouble() * 3.141592653589793D * 2.0D;
            double lvt_34_1_ = Math.sin(lvt_32_1_);
            double lvt_36_1_ = Math.cos(lvt_32_1_);

            for(int lvt_38_1_ = 0; lvt_38_1_ < 4; ++lvt_38_1_) {
               double lvt_39_1_ = 0.0D;
               double lvt_41_1_ = (double)((lvt_38_1_ & 2) - 1) * lvt_10_1_;
               double lvt_43_1_ = (double)((lvt_38_1_ + 1 & 2) - 1) * lvt_10_1_;
               double lvt_45_1_ = 0.0D;
               double lvt_47_1_ = lvt_41_1_ * lvt_36_1_ - lvt_43_1_ * lvt_34_1_;
               double lvt_49_1_ = lvt_43_1_ * lvt_36_1_ + lvt_41_1_ * lvt_34_1_;
               double lvt_53_1_ = lvt_47_1_ * lvt_28_1_ + 0.0D * lvt_30_1_;
               double lvt_55_1_ = 0.0D * lvt_28_1_ - lvt_47_1_ * lvt_30_1_;
               double lvt_57_1_ = lvt_55_1_ * lvt_22_1_ - lvt_49_1_ * lvt_24_1_;
               double lvt_61_1_ = lvt_49_1_ * lvt_22_1_ + lvt_55_1_ * lvt_24_1_;
               p_180444_1_.func_181662_b(lvt_14_1_ + lvt_57_1_, lvt_16_1_ + lvt_53_1_, lvt_18_1_ + lvt_61_1_).func_181675_d();
            }
         }
      }

   }

   public void func_72732_a(WorldClient p_72732_1_) {
      if(this.field_72769_h != null) {
         this.field_72769_h.func_72848_b(this);
      }

      this.field_174992_B = Double.MIN_VALUE;
      this.field_174993_C = Double.MIN_VALUE;
      this.field_174987_D = Double.MIN_VALUE;
      this.field_174988_E = Integer.MIN_VALUE;
      this.field_174989_F = Integer.MIN_VALUE;
      this.field_174990_G = Integer.MIN_VALUE;
      this.field_175010_j.func_78717_a(p_72732_1_);
      this.field_72769_h = p_72732_1_;
      if(p_72732_1_ != null) {
         p_72732_1_.func_72954_a(this);
         this.func_72712_a();
      }

   }

   public void func_72712_a() {
      if(this.field_72769_h != null) {
         this.field_147595_R = true;
         Blocks.field_150362_t.func_150122_b(this.field_72777_q.field_71474_y.field_74347_j);
         Blocks.field_150361_u.func_150122_b(this.field_72777_q.field_71474_y.field_74347_j);
         this.field_72739_F = this.field_72777_q.field_71474_y.field_151451_c;
         boolean lvt_1_1_ = this.field_175005_X;
         this.field_175005_X = OpenGlHelper.func_176075_f();
         if(lvt_1_1_ && !this.field_175005_X) {
            this.field_174996_N = new RenderList();
            this.field_175007_a = new ListChunkFactory();
         } else if(!lvt_1_1_ && this.field_175005_X) {
            this.field_174996_N = new VboRenderList();
            this.field_175007_a = new VboChunkFactory();
         }

         if(lvt_1_1_ != this.field_175005_X) {
            this.func_174963_q();
            this.func_174980_p();
            this.func_174964_o();
         }

         if(this.field_175008_n != null) {
            this.field_175008_n.func_178160_a();
         }

         this.func_174986_e();
         synchronized(this.field_181024_n) {
            this.field_181024_n.clear();
         }

         this.field_175008_n = new ViewFrustum(this.field_72769_h, this.field_72777_q.field_71474_y.field_151451_c, this, this.field_175007_a);
         if(this.field_72769_h != null) {
            Entity lvt_2_1_ = this.field_72777_q.func_175606_aa();
            if(lvt_2_1_ != null) {
               this.field_175008_n.func_178163_a(lvt_2_1_.field_70165_t, lvt_2_1_.field_70161_v);
            }
         }

         this.field_72740_G = 2;
      }
   }

   protected void func_174986_e() {
      this.field_175009_l.clear();
      this.field_174995_M.func_178514_b();
   }

   public void func_72720_a(int p_72720_1_, int p_72720_2_) {
      if(OpenGlHelper.field_148824_g) {
         if(this.field_174991_A != null) {
            this.field_174991_A.func_148026_a(p_72720_1_, p_72720_2_);
         }

      }
   }

   public void func_180446_a(Entity p_180446_1_, ICamera p_180446_2_, float p_180446_3_) {
      if(this.field_72740_G > 0) {
         --this.field_72740_G;
      } else {
         double lvt_4_1_ = p_180446_1_.field_70169_q + (p_180446_1_.field_70165_t - p_180446_1_.field_70169_q) * (double)p_180446_3_;
         double lvt_6_1_ = p_180446_1_.field_70167_r + (p_180446_1_.field_70163_u - p_180446_1_.field_70167_r) * (double)p_180446_3_;
         double lvt_8_1_ = p_180446_1_.field_70166_s + (p_180446_1_.field_70161_v - p_180446_1_.field_70166_s) * (double)p_180446_3_;
         this.field_72769_h.field_72984_F.func_76320_a("prepare");
         TileEntityRendererDispatcher.field_147556_a.func_178470_a(this.field_72769_h, this.field_72777_q.func_110434_K(), this.field_72777_q.field_71466_p, this.field_72777_q.func_175606_aa(), p_180446_3_);
         this.field_175010_j.func_180597_a(this.field_72769_h, this.field_72777_q.field_71466_p, this.field_72777_q.func_175606_aa(), this.field_72777_q.field_147125_j, this.field_72777_q.field_71474_y, p_180446_3_);
         this.field_72748_H = 0;
         this.field_72749_I = 0;
         this.field_72750_J = 0;
         Entity lvt_10_1_ = this.field_72777_q.func_175606_aa();
         double lvt_11_1_ = lvt_10_1_.field_70142_S + (lvt_10_1_.field_70165_t - lvt_10_1_.field_70142_S) * (double)p_180446_3_;
         double lvt_13_1_ = lvt_10_1_.field_70137_T + (lvt_10_1_.field_70163_u - lvt_10_1_.field_70137_T) * (double)p_180446_3_;
         double lvt_15_1_ = lvt_10_1_.field_70136_U + (lvt_10_1_.field_70161_v - lvt_10_1_.field_70136_U) * (double)p_180446_3_;
         TileEntityRendererDispatcher.field_147554_b = lvt_11_1_;
         TileEntityRendererDispatcher.field_147555_c = lvt_13_1_;
         TileEntityRendererDispatcher.field_147552_d = lvt_15_1_;
         this.field_175010_j.func_178628_a(lvt_11_1_, lvt_13_1_, lvt_15_1_);
         this.field_72777_q.field_71460_t.func_180436_i();
         this.field_72769_h.field_72984_F.func_76318_c("global");
         List<Entity> lvt_17_1_ = this.field_72769_h.func_72910_y();
         this.field_72748_H = lvt_17_1_.size();

         for(int lvt_18_1_ = 0; lvt_18_1_ < this.field_72769_h.field_73007_j.size(); ++lvt_18_1_) {
            Entity lvt_19_1_ = (Entity)this.field_72769_h.field_73007_j.get(lvt_18_1_);
            ++this.field_72749_I;
            if(lvt_19_1_.func_145770_h(lvt_4_1_, lvt_6_1_, lvt_8_1_)) {
               this.field_175010_j.func_147937_a(lvt_19_1_, p_180446_3_);
            }
         }

         if(this.func_174985_d()) {
            GlStateManager.func_179143_c(519);
            GlStateManager.func_179106_n();
            this.field_175015_z.func_147614_f();
            this.field_175015_z.func_147610_a(false);
            this.field_72769_h.field_72984_F.func_76318_c("entityOutlines");
            RenderHelper.func_74518_a();
            this.field_175010_j.func_178632_c(true);

            for(int lvt_18_2_ = 0; lvt_18_2_ < lvt_17_1_.size(); ++lvt_18_2_) {
               Entity lvt_19_2_ = (Entity)lvt_17_1_.get(lvt_18_2_);
               boolean lvt_20_1_ = this.field_72777_q.func_175606_aa() instanceof EntityLivingBase && ((EntityLivingBase)this.field_72777_q.func_175606_aa()).func_70608_bn();
               boolean lvt_21_1_ = lvt_19_2_.func_145770_h(lvt_4_1_, lvt_6_1_, lvt_8_1_) && (lvt_19_2_.field_70158_ak || p_180446_2_.func_78546_a(lvt_19_2_.func_174813_aQ()) || lvt_19_2_.field_70153_n == this.field_72777_q.field_71439_g) && lvt_19_2_ instanceof EntityPlayer;
               if((lvt_19_2_ != this.field_72777_q.func_175606_aa() || this.field_72777_q.field_71474_y.field_74320_O != 0 || lvt_20_1_) && lvt_21_1_) {
                  this.field_175010_j.func_147937_a(lvt_19_2_, p_180446_3_);
               }
            }

            this.field_175010_j.func_178632_c(false);
            RenderHelper.func_74519_b();
            GlStateManager.func_179132_a(false);
            this.field_174991_A.func_148018_a(p_180446_3_);
            GlStateManager.func_179145_e();
            GlStateManager.func_179132_a(true);
            this.field_72777_q.func_147110_a().func_147610_a(false);
            GlStateManager.func_179127_m();
            GlStateManager.func_179147_l();
            GlStateManager.func_179142_g();
            GlStateManager.func_179143_c(515);
            GlStateManager.func_179126_j();
            GlStateManager.func_179141_d();
         }

         this.field_72769_h.field_72984_F.func_76318_c("entities");

         label738:
         for(RenderGlobal.ContainerLocalRenderInformation lvt_19_3_ : this.field_72755_R) {
            Chunk lvt_20_2_ = this.field_72769_h.func_175726_f(lvt_19_3_.field_178036_a.func_178568_j());
            ClassInheritanceMultiMap<Entity> lvt_21_2_ = lvt_20_2_.func_177429_s()[lvt_19_3_.field_178036_a.func_178568_j().func_177956_o() / 16];
            if(!lvt_21_2_.isEmpty()) {
               Iterator lvt_22_1_ = lvt_21_2_.iterator();

               while(true) {
                  Entity lvt_23_1_;
                  boolean lvt_24_1_;
                  while(true) {
                     if(!lvt_22_1_.hasNext()) {
                        continue label738;
                     }

                     lvt_23_1_ = (Entity)lvt_22_1_.next();
                     lvt_24_1_ = this.field_175010_j.func_178635_a(lvt_23_1_, p_180446_2_, lvt_4_1_, lvt_6_1_, lvt_8_1_) || lvt_23_1_.field_70153_n == this.field_72777_q.field_71439_g;
                     if(!lvt_24_1_) {
                        break;
                     }

                     boolean lvt_25_1_ = this.field_72777_q.func_175606_aa() instanceof EntityLivingBase?((EntityLivingBase)this.field_72777_q.func_175606_aa()).func_70608_bn():false;
                     if((lvt_23_1_ != this.field_72777_q.func_175606_aa() || this.field_72777_q.field_71474_y.field_74320_O != 0 || lvt_25_1_) && (lvt_23_1_.field_70163_u < 0.0D || lvt_23_1_.field_70163_u >= 256.0D || this.field_72769_h.func_175667_e(new BlockPos(lvt_23_1_)))) {
                        ++this.field_72749_I;
                        this.field_175010_j.func_147937_a(lvt_23_1_, p_180446_3_);
                        break;
                     }
                  }

                  if(!lvt_24_1_ && lvt_23_1_ instanceof EntityWitherSkull) {
                     this.field_72777_q.func_175598_ae().func_178630_b(lvt_23_1_, p_180446_3_);
                  }
               }
            }
         }

         this.field_72769_h.field_72984_F.func_76318_c("blockentities");
         RenderHelper.func_74519_b();

         for(RenderGlobal.ContainerLocalRenderInformation lvt_19_4_ : this.field_72755_R) {
            List<TileEntity> lvt_20_3_ = lvt_19_4_.field_178036_a.func_178571_g().func_178485_b();
            if(!lvt_20_3_.isEmpty()) {
               for(TileEntity lvt_22_2_ : lvt_20_3_) {
                  TileEntityRendererDispatcher.field_147556_a.func_180546_a(lvt_22_2_, p_180446_3_, -1);
               }
            }
         }

         synchronized(this.field_181024_n) {
            for(TileEntity lvt_20_4_ : this.field_181024_n) {
               TileEntityRendererDispatcher.field_147556_a.func_180546_a(lvt_20_4_, p_180446_3_, -1);
            }
         }

         this.func_180443_s();

         for(DestroyBlockProgress lvt_19_6_ : this.field_72738_E.values()) {
            BlockPos lvt_20_5_ = lvt_19_6_.func_180246_b();
            TileEntity lvt_21_4_ = this.field_72769_h.func_175625_s(lvt_20_5_);
            if(lvt_21_4_ instanceof TileEntityChest) {
               TileEntityChest lvt_22_3_ = (TileEntityChest)lvt_21_4_;
               if(lvt_22_3_.field_145991_k != null) {
                  lvt_20_5_ = lvt_20_5_.func_177972_a(EnumFacing.WEST);
                  lvt_21_4_ = this.field_72769_h.func_175625_s(lvt_20_5_);
               } else if(lvt_22_3_.field_145992_i != null) {
                  lvt_20_5_ = lvt_20_5_.func_177972_a(EnumFacing.NORTH);
                  lvt_21_4_ = this.field_72769_h.func_175625_s(lvt_20_5_);
               }
            }

            Block lvt_22_4_ = this.field_72769_h.func_180495_p(lvt_20_5_).func_177230_c();
            if(lvt_21_4_ != null && (lvt_22_4_ instanceof BlockChest || lvt_22_4_ instanceof BlockEnderChest || lvt_22_4_ instanceof BlockSign || lvt_22_4_ instanceof BlockSkull)) {
               TileEntityRendererDispatcher.field_147556_a.func_180546_a(lvt_21_4_, p_180446_3_, lvt_19_6_.func_73106_e());
            }
         }

         this.func_174969_t();
         this.field_72777_q.field_71460_t.func_175072_h();
         this.field_72777_q.field_71424_I.func_76319_b();
      }
   }

   public String func_72735_c() {
      int lvt_1_1_ = this.field_175008_n.field_178164_f.length;
      int lvt_2_1_ = 0;

      for(RenderGlobal.ContainerLocalRenderInformation lvt_4_1_ : this.field_72755_R) {
         CompiledChunk lvt_5_1_ = lvt_4_1_.field_178036_a.field_178590_b;
         if(lvt_5_1_ != CompiledChunk.field_178502_a && !lvt_5_1_.func_178489_a()) {
            ++lvt_2_1_;
         }
      }

      return String.format("C: %d/%d %sD: %d, %s", new Object[]{Integer.valueOf(lvt_2_1_), Integer.valueOf(lvt_1_1_), this.field_72777_q.field_175612_E?"(s) ":"", Integer.valueOf(this.field_72739_F), this.field_174995_M.func_178504_a()});
   }

   public String func_72723_d() {
      return "E: " + this.field_72749_I + "/" + this.field_72748_H + ", B: " + this.field_72750_J + ", I: " + (this.field_72748_H - this.field_72750_J - this.field_72749_I);
   }

   public void func_174970_a(Entity p_174970_1_, double p_174970_2_, ICamera p_174970_4_, int p_174970_5_, boolean p_174970_6_) {
      if(this.field_72777_q.field_71474_y.field_151451_c != this.field_72739_F) {
         this.func_72712_a();
      }

      this.field_72769_h.field_72984_F.func_76320_a("camera");
      double lvt_7_1_ = p_174970_1_.field_70165_t - this.field_174992_B;
      double lvt_9_1_ = p_174970_1_.field_70163_u - this.field_174993_C;
      double lvt_11_1_ = p_174970_1_.field_70161_v - this.field_174987_D;
      if(this.field_174988_E != p_174970_1_.field_70176_ah || this.field_174989_F != p_174970_1_.field_70162_ai || this.field_174990_G != p_174970_1_.field_70164_aj || lvt_7_1_ * lvt_7_1_ + lvt_9_1_ * lvt_9_1_ + lvt_11_1_ * lvt_11_1_ > 16.0D) {
         this.field_174992_B = p_174970_1_.field_70165_t;
         this.field_174993_C = p_174970_1_.field_70163_u;
         this.field_174987_D = p_174970_1_.field_70161_v;
         this.field_174988_E = p_174970_1_.field_70176_ah;
         this.field_174989_F = p_174970_1_.field_70162_ai;
         this.field_174990_G = p_174970_1_.field_70164_aj;
         this.field_175008_n.func_178163_a(p_174970_1_.field_70165_t, p_174970_1_.field_70161_v);
      }

      this.field_72769_h.field_72984_F.func_76318_c("renderlistcamera");
      double lvt_13_1_ = p_174970_1_.field_70142_S + (p_174970_1_.field_70165_t - p_174970_1_.field_70142_S) * p_174970_2_;
      double lvt_15_1_ = p_174970_1_.field_70137_T + (p_174970_1_.field_70163_u - p_174970_1_.field_70137_T) * p_174970_2_;
      double lvt_17_1_ = p_174970_1_.field_70136_U + (p_174970_1_.field_70161_v - p_174970_1_.field_70136_U) * p_174970_2_;
      this.field_174996_N.func_178004_a(lvt_13_1_, lvt_15_1_, lvt_17_1_);
      this.field_72769_h.field_72984_F.func_76318_c("cull");
      if(this.field_175001_U != null) {
         Frustum lvt_19_1_ = new Frustum(this.field_175001_U);
         lvt_19_1_.func_78547_a(this.field_175003_W.field_181059_a, this.field_175003_W.field_181060_b, this.field_175003_W.field_181061_c);
         p_174970_4_ = lvt_19_1_;
      }

      this.field_72777_q.field_71424_I.func_76318_c("culling");
      BlockPos lvt_19_2_ = new BlockPos(lvt_13_1_, lvt_15_1_ + (double)p_174970_1_.func_70047_e(), lvt_17_1_);
      RenderChunk lvt_20_1_ = this.field_175008_n.func_178161_a(lvt_19_2_);
      BlockPos lvt_21_1_ = new BlockPos(MathHelper.func_76128_c(lvt_13_1_ / 16.0D) * 16, MathHelper.func_76128_c(lvt_15_1_ / 16.0D) * 16, MathHelper.func_76128_c(lvt_17_1_ / 16.0D) * 16);
      this.field_147595_R = this.field_147595_R || !this.field_175009_l.isEmpty() || p_174970_1_.field_70165_t != this.field_174997_H || p_174970_1_.field_70163_u != this.field_174998_I || p_174970_1_.field_70161_v != this.field_174999_J || (double)p_174970_1_.field_70125_A != this.field_175000_K || (double)p_174970_1_.field_70177_z != this.field_174994_L;
      this.field_174997_H = p_174970_1_.field_70165_t;
      this.field_174998_I = p_174970_1_.field_70163_u;
      this.field_174999_J = p_174970_1_.field_70161_v;
      this.field_175000_K = (double)p_174970_1_.field_70125_A;
      this.field_174994_L = (double)p_174970_1_.field_70177_z;
      boolean lvt_22_1_ = this.field_175001_U != null;
      if(!lvt_22_1_ && this.field_147595_R) {
         this.field_147595_R = false;
         this.field_72755_R = Lists.newArrayList();
         Queue<RenderGlobal.ContainerLocalRenderInformation> lvt_23_1_ = Lists.newLinkedList();
         boolean lvt_24_1_ = this.field_72777_q.field_175612_E;
         if(lvt_20_1_ != null) {
            boolean lvt_25_2_ = false;
            RenderGlobal.ContainerLocalRenderInformation lvt_26_2_ = new RenderGlobal.ContainerLocalRenderInformation(lvt_20_1_, (EnumFacing)null, 0);
            Set<EnumFacing> lvt_27_2_ = this.func_174978_c(lvt_19_2_);
            if(lvt_27_2_.size() == 1) {
               Vector3f lvt_28_2_ = this.func_174962_a(p_174970_1_, p_174970_2_);
               EnumFacing lvt_29_1_ = EnumFacing.func_176737_a(lvt_28_2_.x, lvt_28_2_.y, lvt_28_2_.z).func_176734_d();
               lvt_27_2_.remove(lvt_29_1_);
            }

            if(lvt_27_2_.isEmpty()) {
               lvt_25_2_ = true;
            }

            if(lvt_25_2_ && !p_174970_6_) {
               this.field_72755_R.add(lvt_26_2_);
            } else {
               if(p_174970_6_ && this.field_72769_h.func_180495_p(lvt_19_2_).func_177230_c().func_149662_c()) {
                  lvt_24_1_ = false;
               }

               lvt_20_1_.func_178577_a(p_174970_5_);
               lvt_23_1_.add(lvt_26_2_);
            }
         } else {
            int lvt_25_1_ = lvt_19_2_.func_177956_o() > 0?248:8;

            for(int lvt_26_1_ = -this.field_72739_F; lvt_26_1_ <= this.field_72739_F; ++lvt_26_1_) {
               for(int lvt_27_1_ = -this.field_72739_F; lvt_27_1_ <= this.field_72739_F; ++lvt_27_1_) {
                  RenderChunk lvt_28_1_ = this.field_175008_n.func_178161_a(new BlockPos((lvt_26_1_ << 4) + 8, lvt_25_1_, (lvt_27_1_ << 4) + 8));
                  if(lvt_28_1_ != null && ((ICamera)p_174970_4_).func_78546_a(lvt_28_1_.field_178591_c)) {
                     lvt_28_1_.func_178577_a(p_174970_5_);
                     lvt_23_1_.add(new RenderGlobal.ContainerLocalRenderInformation(lvt_28_1_, (EnumFacing)null, 0));
                  }
               }
            }
         }

         while(!((Queue)lvt_23_1_).isEmpty()) {
            RenderGlobal.ContainerLocalRenderInformation lvt_25_3_ = (RenderGlobal.ContainerLocalRenderInformation)lvt_23_1_.poll();
            RenderChunk lvt_26_3_ = lvt_25_3_.field_178036_a;
            EnumFacing lvt_27_3_ = lvt_25_3_.field_178034_b;
            BlockPos lvt_28_3_ = lvt_26_3_.func_178568_j();
            this.field_72755_R.add(lvt_25_3_);

            for(EnumFacing lvt_32_1_ : EnumFacing.values()) {
               RenderChunk lvt_33_1_ = this.func_181562_a(lvt_21_1_, lvt_26_3_, lvt_32_1_);
               if((!lvt_24_1_ || !lvt_25_3_.field_178035_c.contains(lvt_32_1_.func_176734_d())) && (!lvt_24_1_ || lvt_27_3_ == null || lvt_26_3_.func_178571_g().func_178495_a(lvt_27_3_.func_176734_d(), lvt_32_1_)) && lvt_33_1_ != null && lvt_33_1_.func_178577_a(p_174970_5_) && ((ICamera)p_174970_4_).func_78546_a(lvt_33_1_.field_178591_c)) {
                  RenderGlobal.ContainerLocalRenderInformation lvt_34_1_ = new RenderGlobal.ContainerLocalRenderInformation(lvt_33_1_, lvt_32_1_, lvt_25_3_.field_178032_d + 1);
                  lvt_34_1_.field_178035_c.addAll(lvt_25_3_.field_178035_c);
                  lvt_34_1_.field_178035_c.add(lvt_32_1_);
                  lvt_23_1_.add(lvt_34_1_);
               }
            }
         }
      }

      if(this.field_175002_T) {
         this.func_174984_a(lvt_13_1_, lvt_15_1_, lvt_17_1_);
         this.field_175002_T = false;
      }

      this.field_174995_M.func_178513_e();
      Set<RenderChunk> lvt_23_2_ = this.field_175009_l;
      this.field_175009_l = Sets.newLinkedHashSet();

      for(RenderGlobal.ContainerLocalRenderInformation lvt_25_4_ : this.field_72755_R) {
         RenderChunk lvt_26_4_ = lvt_25_4_.field_178036_a;
         if(lvt_26_4_.func_178569_m() || lvt_23_2_.contains(lvt_26_4_)) {
            this.field_147595_R = true;
            if(this.func_174983_a(lvt_21_1_, lvt_25_4_.field_178036_a)) {
               this.field_72777_q.field_71424_I.func_76320_a("build near");
               this.field_174995_M.func_178505_b(lvt_26_4_);
               lvt_26_4_.func_178575_a(false);
               this.field_72777_q.field_71424_I.func_76319_b();
            } else {
               this.field_175009_l.add(lvt_26_4_);
            }
         }
      }

      this.field_175009_l.addAll(lvt_23_2_);
      this.field_72777_q.field_71424_I.func_76319_b();
   }

   private boolean func_174983_a(BlockPos p_174983_1_, RenderChunk p_174983_2_) {
      BlockPos lvt_3_1_ = p_174983_2_.func_178568_j();
      return MathHelper.func_76130_a(p_174983_1_.func_177958_n() - lvt_3_1_.func_177958_n()) > 16?false:(MathHelper.func_76130_a(p_174983_1_.func_177956_o() - lvt_3_1_.func_177956_o()) > 16?false:MathHelper.func_76130_a(p_174983_1_.func_177952_p() - lvt_3_1_.func_177952_p()) <= 16);
   }

   private Set<EnumFacing> func_174978_c(BlockPos p_174978_1_) {
      VisGraph lvt_2_1_ = new VisGraph();
      BlockPos lvt_3_1_ = new BlockPos(p_174978_1_.func_177958_n() >> 4 << 4, p_174978_1_.func_177956_o() >> 4 << 4, p_174978_1_.func_177952_p() >> 4 << 4);
      Chunk lvt_4_1_ = this.field_72769_h.func_175726_f(lvt_3_1_);

      for(BlockPos.MutableBlockPos lvt_6_1_ : BlockPos.func_177975_b(lvt_3_1_, lvt_3_1_.func_177982_a(15, 15, 15))) {
         if(lvt_4_1_.func_177428_a(lvt_6_1_).func_149662_c()) {
            lvt_2_1_.func_178606_a(lvt_6_1_);
         }
      }

      return lvt_2_1_.func_178609_b(p_174978_1_);
   }

   private RenderChunk func_181562_a(BlockPos p_181562_1_, RenderChunk p_181562_2_, EnumFacing p_181562_3_) {
      BlockPos lvt_4_1_ = p_181562_2_.func_181701_a(p_181562_3_);
      return MathHelper.func_76130_a(p_181562_1_.func_177958_n() - lvt_4_1_.func_177958_n()) > this.field_72739_F * 16?null:(lvt_4_1_.func_177956_o() >= 0 && lvt_4_1_.func_177956_o() < 256?(MathHelper.func_76130_a(p_181562_1_.func_177952_p() - lvt_4_1_.func_177952_p()) > this.field_72739_F * 16?null:this.field_175008_n.func_178161_a(lvt_4_1_)):null);
   }

   private void func_174984_a(double p_174984_1_, double p_174984_3_, double p_174984_5_) {
      this.field_175001_U = new ClippingHelperImpl();
      ((ClippingHelperImpl)this.field_175001_U).func_78560_b();
      Matrix4f lvt_7_1_ = new Matrix4f(this.field_175001_U.field_178626_c);
      lvt_7_1_.transpose();
      Matrix4f lvt_8_1_ = new Matrix4f(this.field_175001_U.field_178625_b);
      lvt_8_1_.transpose();
      Matrix4f lvt_9_1_ = new Matrix4f();
      Matrix4f.mul(lvt_8_1_, lvt_7_1_, lvt_9_1_);
      lvt_9_1_.invert();
      this.field_175003_W.field_181059_a = p_174984_1_;
      this.field_175003_W.field_181060_b = p_174984_3_;
      this.field_175003_W.field_181061_c = p_174984_5_;
      this.field_175004_V[0] = new Vector4f(-1.0F, -1.0F, -1.0F, 1.0F);
      this.field_175004_V[1] = new Vector4f(1.0F, -1.0F, -1.0F, 1.0F);
      this.field_175004_V[2] = new Vector4f(1.0F, 1.0F, -1.0F, 1.0F);
      this.field_175004_V[3] = new Vector4f(-1.0F, 1.0F, -1.0F, 1.0F);
      this.field_175004_V[4] = new Vector4f(-1.0F, -1.0F, 1.0F, 1.0F);
      this.field_175004_V[5] = new Vector4f(1.0F, -1.0F, 1.0F, 1.0F);
      this.field_175004_V[6] = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_175004_V[7] = new Vector4f(-1.0F, 1.0F, 1.0F, 1.0F);

      for(int lvt_10_1_ = 0; lvt_10_1_ < 8; ++lvt_10_1_) {
         Matrix4f.transform(lvt_9_1_, this.field_175004_V[lvt_10_1_], this.field_175004_V[lvt_10_1_]);
         this.field_175004_V[lvt_10_1_].x /= this.field_175004_V[lvt_10_1_].w;
         this.field_175004_V[lvt_10_1_].y /= this.field_175004_V[lvt_10_1_].w;
         this.field_175004_V[lvt_10_1_].z /= this.field_175004_V[lvt_10_1_].w;
         this.field_175004_V[lvt_10_1_].w = 1.0F;
      }

   }

   protected Vector3f func_174962_a(Entity p_174962_1_, double p_174962_2_) {
      float lvt_4_1_ = (float)((double)p_174962_1_.field_70127_C + (double)(p_174962_1_.field_70125_A - p_174962_1_.field_70127_C) * p_174962_2_);
      float lvt_5_1_ = (float)((double)p_174962_1_.field_70126_B + (double)(p_174962_1_.field_70177_z - p_174962_1_.field_70126_B) * p_174962_2_);
      if(Minecraft.func_71410_x().field_71474_y.field_74320_O == 2) {
         lvt_4_1_ += 180.0F;
      }

      float lvt_6_1_ = MathHelper.func_76134_b(-lvt_5_1_ * 0.017453292F - 3.1415927F);
      float lvt_7_1_ = MathHelper.func_76126_a(-lvt_5_1_ * 0.017453292F - 3.1415927F);
      float lvt_8_1_ = -MathHelper.func_76134_b(-lvt_4_1_ * 0.017453292F);
      float lvt_9_1_ = MathHelper.func_76126_a(-lvt_4_1_ * 0.017453292F);
      return new Vector3f(lvt_7_1_ * lvt_8_1_, lvt_9_1_, lvt_6_1_ * lvt_8_1_);
   }

   public int func_174977_a(EnumWorldBlockLayer p_174977_1_, double p_174977_2_, int p_174977_4_, Entity p_174977_5_) {
      RenderHelper.func_74518_a();
      if(p_174977_1_ == EnumWorldBlockLayer.TRANSLUCENT) {
         this.field_72777_q.field_71424_I.func_76320_a("translucent_sort");
         double lvt_6_1_ = p_174977_5_.field_70165_t - this.field_147596_f;
         double lvt_8_1_ = p_174977_5_.field_70163_u - this.field_147597_g;
         double lvt_10_1_ = p_174977_5_.field_70161_v - this.field_147602_h;
         if(lvt_6_1_ * lvt_6_1_ + lvt_8_1_ * lvt_8_1_ + lvt_10_1_ * lvt_10_1_ > 1.0D) {
            this.field_147596_f = p_174977_5_.field_70165_t;
            this.field_147597_g = p_174977_5_.field_70163_u;
            this.field_147602_h = p_174977_5_.field_70161_v;
            int lvt_12_1_ = 0;

            for(RenderGlobal.ContainerLocalRenderInformation lvt_14_1_ : this.field_72755_R) {
               if(lvt_14_1_.field_178036_a.field_178590_b.func_178492_d(p_174977_1_) && lvt_12_1_++ < 15) {
                  this.field_174995_M.func_178509_c(lvt_14_1_.field_178036_a);
               }
            }
         }

         this.field_72777_q.field_71424_I.func_76319_b();
      }

      this.field_72777_q.field_71424_I.func_76320_a("filterempty");
      int lvt_6_2_ = 0;
      boolean lvt_7_1_ = p_174977_1_ == EnumWorldBlockLayer.TRANSLUCENT;
      int lvt_8_2_ = lvt_7_1_?this.field_72755_R.size() - 1:0;
      int lvt_9_1_ = lvt_7_1_?-1:this.field_72755_R.size();
      int lvt_10_2_ = lvt_7_1_?-1:1;

      for(int lvt_11_1_ = lvt_8_2_; lvt_11_1_ != lvt_9_1_; lvt_11_1_ += lvt_10_2_) {
         RenderChunk lvt_12_2_ = ((RenderGlobal.ContainerLocalRenderInformation)this.field_72755_R.get(lvt_11_1_)).field_178036_a;
         if(!lvt_12_2_.func_178571_g().func_178491_b(p_174977_1_)) {
            ++lvt_6_2_;
            this.field_174996_N.func_178002_a(lvt_12_2_, p_174977_1_);
         }
      }

      this.field_72777_q.field_71424_I.func_76318_c("render_" + p_174977_1_);
      this.func_174982_a(p_174977_1_);
      this.field_72777_q.field_71424_I.func_76319_b();
      return lvt_6_2_;
   }

   private void func_174982_a(EnumWorldBlockLayer p_174982_1_) {
      this.field_72777_q.field_71460_t.func_180436_i();
      if(OpenGlHelper.func_176075_f()) {
         GL11.glEnableClientState('\u8074');
         OpenGlHelper.func_77472_b(OpenGlHelper.field_77478_a);
         GL11.glEnableClientState('\u8078');
         OpenGlHelper.func_77472_b(OpenGlHelper.field_77476_b);
         GL11.glEnableClientState('\u8078');
         OpenGlHelper.func_77472_b(OpenGlHelper.field_77478_a);
         GL11.glEnableClientState('\u8076');
      }

      this.field_174996_N.func_178001_a(p_174982_1_);
      if(OpenGlHelper.func_176075_f()) {
         for(VertexFormatElement lvt_4_1_ : DefaultVertexFormats.field_176600_a.func_177343_g()) {
            VertexFormatElement.EnumUsage lvt_5_1_ = lvt_4_1_.func_177375_c();
            int lvt_6_1_ = lvt_4_1_.func_177369_e();
            switch(lvt_5_1_) {
            case POSITION:
               GL11.glDisableClientState('\u8074');
               break;
            case UV:
               OpenGlHelper.func_77472_b(OpenGlHelper.field_77478_a + lvt_6_1_);
               GL11.glDisableClientState('\u8078');
               OpenGlHelper.func_77472_b(OpenGlHelper.field_77478_a);
               break;
            case COLOR:
               GL11.glDisableClientState('\u8076');
               GlStateManager.func_179117_G();
            }
         }
      }

      this.field_72777_q.field_71460_t.func_175072_h();
   }

   private void func_174965_a(Iterator<DestroyBlockProgress> p_174965_1_) {
      while(p_174965_1_.hasNext()) {
         DestroyBlockProgress lvt_2_1_ = (DestroyBlockProgress)p_174965_1_.next();
         int lvt_3_1_ = lvt_2_1_.func_82743_f();
         if(this.field_72773_u - lvt_3_1_ > 400) {
            p_174965_1_.remove();
         }
      }

   }

   public void func_72734_e() {
      ++this.field_72773_u;
      if(this.field_72773_u % 20 == 0) {
         this.func_174965_a(this.field_72738_E.values().iterator());
      }

   }

   private void func_180448_r() {
      GlStateManager.func_179106_n();
      GlStateManager.func_179118_c();
      GlStateManager.func_179147_l();
      GlStateManager.func_179120_a(770, 771, 1, 0);
      RenderHelper.func_74518_a();
      GlStateManager.func_179132_a(false);
      this.field_72770_i.func_110577_a(field_110926_k);
      Tessellator lvt_1_1_ = Tessellator.func_178181_a();
      WorldRenderer lvt_2_1_ = lvt_1_1_.func_178180_c();

      for(int lvt_3_1_ = 0; lvt_3_1_ < 6; ++lvt_3_1_) {
         GlStateManager.func_179094_E();
         if(lvt_3_1_ == 1) {
            GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
         }

         if(lvt_3_1_ == 2) {
            GlStateManager.func_179114_b(-90.0F, 1.0F, 0.0F, 0.0F);
         }

         if(lvt_3_1_ == 3) {
            GlStateManager.func_179114_b(180.0F, 1.0F, 0.0F, 0.0F);
         }

         if(lvt_3_1_ == 4) {
            GlStateManager.func_179114_b(90.0F, 0.0F, 0.0F, 1.0F);
         }

         if(lvt_3_1_ == 5) {
            GlStateManager.func_179114_b(-90.0F, 0.0F, 0.0F, 1.0F);
         }

         lvt_2_1_.func_181668_a(7, DefaultVertexFormats.field_181709_i);
         lvt_2_1_.func_181662_b(-100.0D, -100.0D, -100.0D).func_181673_a(0.0D, 0.0D).func_181669_b(40, 40, 40, 255).func_181675_d();
         lvt_2_1_.func_181662_b(-100.0D, -100.0D, 100.0D).func_181673_a(0.0D, 16.0D).func_181669_b(40, 40, 40, 255).func_181675_d();
         lvt_2_1_.func_181662_b(100.0D, -100.0D, 100.0D).func_181673_a(16.0D, 16.0D).func_181669_b(40, 40, 40, 255).func_181675_d();
         lvt_2_1_.func_181662_b(100.0D, -100.0D, -100.0D).func_181673_a(16.0D, 0.0D).func_181669_b(40, 40, 40, 255).func_181675_d();
         lvt_1_1_.func_78381_a();
         GlStateManager.func_179121_F();
      }

      GlStateManager.func_179132_a(true);
      GlStateManager.func_179098_w();
      GlStateManager.func_179141_d();
   }

   public void func_174976_a(float p_174976_1_, int p_174976_2_) {
      if(this.field_72777_q.field_71441_e.field_73011_w.func_177502_q() == 1) {
         this.func_180448_r();
      } else if(this.field_72777_q.field_71441_e.field_73011_w.func_76569_d()) {
         GlStateManager.func_179090_x();
         Vec3 lvt_3_1_ = this.field_72769_h.func_72833_a(this.field_72777_q.func_175606_aa(), p_174976_1_);
         float lvt_4_1_ = (float)lvt_3_1_.field_72450_a;
         float lvt_5_1_ = (float)lvt_3_1_.field_72448_b;
         float lvt_6_1_ = (float)lvt_3_1_.field_72449_c;
         if(p_174976_2_ != 2) {
            float lvt_7_1_ = (lvt_4_1_ * 30.0F + lvt_5_1_ * 59.0F + lvt_6_1_ * 11.0F) / 100.0F;
            float lvt_8_1_ = (lvt_4_1_ * 30.0F + lvt_5_1_ * 70.0F) / 100.0F;
            float lvt_9_1_ = (lvt_4_1_ * 30.0F + lvt_6_1_ * 70.0F) / 100.0F;
            lvt_4_1_ = lvt_7_1_;
            lvt_5_1_ = lvt_8_1_;
            lvt_6_1_ = lvt_9_1_;
         }

         GlStateManager.func_179124_c(lvt_4_1_, lvt_5_1_, lvt_6_1_);
         Tessellator lvt_7_2_ = Tessellator.func_178181_a();
         WorldRenderer lvt_8_2_ = lvt_7_2_.func_178180_c();
         GlStateManager.func_179132_a(false);
         GlStateManager.func_179127_m();
         GlStateManager.func_179124_c(lvt_4_1_, lvt_5_1_, lvt_6_1_);
         if(this.field_175005_X) {
            this.field_175012_t.func_177359_a();
            GL11.glEnableClientState('\u8074');
            GL11.glVertexPointer(3, 5126, 12, 0L);
            this.field_175012_t.func_177358_a(7);
            this.field_175012_t.func_177361_b();
            GL11.glDisableClientState('\u8074');
         } else {
            GlStateManager.func_179148_o(this.field_72771_w);
         }

         GlStateManager.func_179106_n();
         GlStateManager.func_179118_c();
         GlStateManager.func_179147_l();
         GlStateManager.func_179120_a(770, 771, 1, 0);
         RenderHelper.func_74518_a();
         float[] lvt_9_2_ = this.field_72769_h.field_73011_w.func_76560_a(this.field_72769_h.func_72826_c(p_174976_1_), p_174976_1_);
         if(lvt_9_2_ != null) {
            GlStateManager.func_179090_x();
            GlStateManager.func_179103_j(7425);
            GlStateManager.func_179094_E();
            GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.func_179114_b(MathHelper.func_76126_a(this.field_72769_h.func_72929_e(p_174976_1_)) < 0.0F?180.0F:0.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.func_179114_b(90.0F, 0.0F, 0.0F, 1.0F);
            float lvt_10_1_ = lvt_9_2_[0];
            float lvt_11_1_ = lvt_9_2_[1];
            float lvt_12_1_ = lvt_9_2_[2];
            if(p_174976_2_ != 2) {
               float lvt_13_1_ = (lvt_10_1_ * 30.0F + lvt_11_1_ * 59.0F + lvt_12_1_ * 11.0F) / 100.0F;
               float lvt_14_1_ = (lvt_10_1_ * 30.0F + lvt_11_1_ * 70.0F) / 100.0F;
               float lvt_15_1_ = (lvt_10_1_ * 30.0F + lvt_12_1_ * 70.0F) / 100.0F;
               lvt_10_1_ = lvt_13_1_;
               lvt_11_1_ = lvt_14_1_;
               lvt_12_1_ = lvt_15_1_;
            }

            lvt_8_2_.func_181668_a(6, DefaultVertexFormats.field_181706_f);
            lvt_8_2_.func_181662_b(0.0D, 100.0D, 0.0D).func_181666_a(lvt_10_1_, lvt_11_1_, lvt_12_1_, lvt_9_2_[3]).func_181675_d();
            int lvt_13_2_ = 16;

            for(int lvt_14_2_ = 0; lvt_14_2_ <= 16; ++lvt_14_2_) {
               float lvt_15_2_ = (float)lvt_14_2_ * 3.1415927F * 2.0F / 16.0F;
               float lvt_16_1_ = MathHelper.func_76126_a(lvt_15_2_);
               float lvt_17_1_ = MathHelper.func_76134_b(lvt_15_2_);
               lvt_8_2_.func_181662_b((double)(lvt_16_1_ * 120.0F), (double)(lvt_17_1_ * 120.0F), (double)(-lvt_17_1_ * 40.0F * lvt_9_2_[3])).func_181666_a(lvt_9_2_[0], lvt_9_2_[1], lvt_9_2_[2], 0.0F).func_181675_d();
            }

            lvt_7_2_.func_78381_a();
            GlStateManager.func_179121_F();
            GlStateManager.func_179103_j(7424);
         }

         GlStateManager.func_179098_w();
         GlStateManager.func_179120_a(770, 1, 1, 0);
         GlStateManager.func_179094_E();
         float lvt_10_2_ = 1.0F - this.field_72769_h.func_72867_j(p_174976_1_);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, lvt_10_2_);
         GlStateManager.func_179114_b(-90.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b(this.field_72769_h.func_72826_c(p_174976_1_) * 360.0F, 1.0F, 0.0F, 0.0F);
         float lvt_11_2_ = 30.0F;
         this.field_72770_i.func_110577_a(field_110928_i);
         lvt_8_2_.func_181668_a(7, DefaultVertexFormats.field_181707_g);
         lvt_8_2_.func_181662_b((double)(-lvt_11_2_), 100.0D, (double)(-lvt_11_2_)).func_181673_a(0.0D, 0.0D).func_181675_d();
         lvt_8_2_.func_181662_b((double)lvt_11_2_, 100.0D, (double)(-lvt_11_2_)).func_181673_a(1.0D, 0.0D).func_181675_d();
         lvt_8_2_.func_181662_b((double)lvt_11_2_, 100.0D, (double)lvt_11_2_).func_181673_a(1.0D, 1.0D).func_181675_d();
         lvt_8_2_.func_181662_b((double)(-lvt_11_2_), 100.0D, (double)lvt_11_2_).func_181673_a(0.0D, 1.0D).func_181675_d();
         lvt_7_2_.func_78381_a();
         lvt_11_2_ = 20.0F;
         this.field_72770_i.func_110577_a(field_110927_h);
         int lvt_12_2_ = this.field_72769_h.func_72853_d();
         int lvt_13_3_ = lvt_12_2_ % 4;
         int lvt_14_3_ = lvt_12_2_ / 4 % 2;
         float lvt_15_3_ = (float)(lvt_13_3_ + 0) / 4.0F;
         float lvt_16_2_ = (float)(lvt_14_3_ + 0) / 2.0F;
         float lvt_17_2_ = (float)(lvt_13_3_ + 1) / 4.0F;
         float lvt_18_1_ = (float)(lvt_14_3_ + 1) / 2.0F;
         lvt_8_2_.func_181668_a(7, DefaultVertexFormats.field_181707_g);
         lvt_8_2_.func_181662_b((double)(-lvt_11_2_), -100.0D, (double)lvt_11_2_).func_181673_a((double)lvt_17_2_, (double)lvt_18_1_).func_181675_d();
         lvt_8_2_.func_181662_b((double)lvt_11_2_, -100.0D, (double)lvt_11_2_).func_181673_a((double)lvt_15_3_, (double)lvt_18_1_).func_181675_d();
         lvt_8_2_.func_181662_b((double)lvt_11_2_, -100.0D, (double)(-lvt_11_2_)).func_181673_a((double)lvt_15_3_, (double)lvt_16_2_).func_181675_d();
         lvt_8_2_.func_181662_b((double)(-lvt_11_2_), -100.0D, (double)(-lvt_11_2_)).func_181673_a((double)lvt_17_2_, (double)lvt_16_2_).func_181675_d();
         lvt_7_2_.func_78381_a();
         GlStateManager.func_179090_x();
         float lvt_19_1_ = this.field_72769_h.func_72880_h(p_174976_1_) * lvt_10_2_;
         if(lvt_19_1_ > 0.0F) {
            GlStateManager.func_179131_c(lvt_19_1_, lvt_19_1_, lvt_19_1_, lvt_19_1_);
            if(this.field_175005_X) {
               this.field_175013_s.func_177359_a();
               GL11.glEnableClientState('\u8074');
               GL11.glVertexPointer(3, 5126, 12, 0L);
               this.field_175013_s.func_177358_a(7);
               this.field_175013_s.func_177361_b();
               GL11.glDisableClientState('\u8074');
            } else {
               GlStateManager.func_179148_o(this.field_72772_v);
            }
         }

         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.func_179084_k();
         GlStateManager.func_179141_d();
         GlStateManager.func_179127_m();
         GlStateManager.func_179121_F();
         GlStateManager.func_179090_x();
         GlStateManager.func_179124_c(0.0F, 0.0F, 0.0F);
         double lvt_10_3_ = this.field_72777_q.field_71439_g.func_174824_e(p_174976_1_).field_72448_b - this.field_72769_h.func_72919_O();
         if(lvt_10_3_ < 0.0D) {
            GlStateManager.func_179094_E();
            GlStateManager.func_179109_b(0.0F, 12.0F, 0.0F);
            if(this.field_175005_X) {
               this.field_175011_u.func_177359_a();
               GL11.glEnableClientState('\u8074');
               GL11.glVertexPointer(3, 5126, 12, 0L);
               this.field_175011_u.func_177358_a(7);
               this.field_175011_u.func_177361_b();
               GL11.glDisableClientState('\u8074');
            } else {
               GlStateManager.func_179148_o(this.field_72781_x);
            }

            GlStateManager.func_179121_F();
            float lvt_12_3_ = 1.0F;
            float lvt_13_4_ = -((float)(lvt_10_3_ + 65.0D));
            float lvt_14_4_ = -1.0F;
            lvt_8_2_.func_181668_a(7, DefaultVertexFormats.field_181706_f);
            lvt_8_2_.func_181662_b(-1.0D, (double)lvt_13_4_, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            lvt_8_2_.func_181662_b(1.0D, (double)lvt_13_4_, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            lvt_8_2_.func_181662_b(1.0D, -1.0D, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            lvt_8_2_.func_181662_b(-1.0D, -1.0D, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            lvt_8_2_.func_181662_b(-1.0D, -1.0D, -1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            lvt_8_2_.func_181662_b(1.0D, -1.0D, -1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            lvt_8_2_.func_181662_b(1.0D, (double)lvt_13_4_, -1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            lvt_8_2_.func_181662_b(-1.0D, (double)lvt_13_4_, -1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            lvt_8_2_.func_181662_b(1.0D, -1.0D, -1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            lvt_8_2_.func_181662_b(1.0D, -1.0D, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            lvt_8_2_.func_181662_b(1.0D, (double)lvt_13_4_, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            lvt_8_2_.func_181662_b(1.0D, (double)lvt_13_4_, -1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            lvt_8_2_.func_181662_b(-1.0D, (double)lvt_13_4_, -1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            lvt_8_2_.func_181662_b(-1.0D, (double)lvt_13_4_, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            lvt_8_2_.func_181662_b(-1.0D, -1.0D, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            lvt_8_2_.func_181662_b(-1.0D, -1.0D, -1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            lvt_8_2_.func_181662_b(-1.0D, -1.0D, -1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            lvt_8_2_.func_181662_b(-1.0D, -1.0D, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            lvt_8_2_.func_181662_b(1.0D, -1.0D, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            lvt_8_2_.func_181662_b(1.0D, -1.0D, -1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            lvt_7_2_.func_78381_a();
         }

         if(this.field_72769_h.field_73011_w.func_76561_g()) {
            GlStateManager.func_179124_c(lvt_4_1_ * 0.2F + 0.04F, lvt_5_1_ * 0.2F + 0.04F, lvt_6_1_ * 0.6F + 0.1F);
         } else {
            GlStateManager.func_179124_c(lvt_4_1_, lvt_5_1_, lvt_6_1_);
         }

         GlStateManager.func_179094_E();
         GlStateManager.func_179109_b(0.0F, -((float)(lvt_10_3_ - 16.0D)), 0.0F);
         GlStateManager.func_179148_o(this.field_72781_x);
         GlStateManager.func_179121_F();
         GlStateManager.func_179098_w();
         GlStateManager.func_179132_a(true);
      }
   }

   public void func_180447_b(float p_180447_1_, int p_180447_2_) {
      if(this.field_72777_q.field_71441_e.field_73011_w.func_76569_d()) {
         if(this.field_72777_q.field_71474_y.func_181147_e() == 2) {
            this.func_180445_c(p_180447_1_, p_180447_2_);
         } else {
            GlStateManager.func_179129_p();
            float lvt_3_1_ = (float)(this.field_72777_q.func_175606_aa().field_70137_T + (this.field_72777_q.func_175606_aa().field_70163_u - this.field_72777_q.func_175606_aa().field_70137_T) * (double)p_180447_1_);
            int lvt_4_1_ = 32;
            int lvt_5_1_ = 8;
            Tessellator lvt_6_1_ = Tessellator.func_178181_a();
            WorldRenderer lvt_7_1_ = lvt_6_1_.func_178180_c();
            this.field_72770_i.func_110577_a(field_110925_j);
            GlStateManager.func_179147_l();
            GlStateManager.func_179120_a(770, 771, 1, 0);
            Vec3 lvt_8_1_ = this.field_72769_h.func_72824_f(p_180447_1_);
            float lvt_9_1_ = (float)lvt_8_1_.field_72450_a;
            float lvt_10_1_ = (float)lvt_8_1_.field_72448_b;
            float lvt_11_1_ = (float)lvt_8_1_.field_72449_c;
            if(p_180447_2_ != 2) {
               float lvt_12_1_ = (lvt_9_1_ * 30.0F + lvt_10_1_ * 59.0F + lvt_11_1_ * 11.0F) / 100.0F;
               float lvt_13_1_ = (lvt_9_1_ * 30.0F + lvt_10_1_ * 70.0F) / 100.0F;
               float lvt_14_1_ = (lvt_9_1_ * 30.0F + lvt_11_1_ * 70.0F) / 100.0F;
               lvt_9_1_ = lvt_12_1_;
               lvt_10_1_ = lvt_13_1_;
               lvt_11_1_ = lvt_14_1_;
            }

            float lvt_12_2_ = 4.8828125E-4F;
            double lvt_13_2_ = (double)((float)this.field_72773_u + p_180447_1_);
            double lvt_15_1_ = this.field_72777_q.func_175606_aa().field_70169_q + (this.field_72777_q.func_175606_aa().field_70165_t - this.field_72777_q.func_175606_aa().field_70169_q) * (double)p_180447_1_ + lvt_13_2_ * 0.029999999329447746D;
            double lvt_17_1_ = this.field_72777_q.func_175606_aa().field_70166_s + (this.field_72777_q.func_175606_aa().field_70161_v - this.field_72777_q.func_175606_aa().field_70166_s) * (double)p_180447_1_;
            int lvt_19_1_ = MathHelper.func_76128_c(lvt_15_1_ / 2048.0D);
            int lvt_20_1_ = MathHelper.func_76128_c(lvt_17_1_ / 2048.0D);
            lvt_15_1_ = lvt_15_1_ - (double)(lvt_19_1_ * 2048);
            lvt_17_1_ = lvt_17_1_ - (double)(lvt_20_1_ * 2048);
            float lvt_21_1_ = this.field_72769_h.field_73011_w.func_76571_f() - lvt_3_1_ + 0.33F;
            float lvt_22_1_ = (float)(lvt_15_1_ * 4.8828125E-4D);
            float lvt_23_1_ = (float)(lvt_17_1_ * 4.8828125E-4D);
            lvt_7_1_.func_181668_a(7, DefaultVertexFormats.field_181709_i);

            for(int lvt_24_1_ = -256; lvt_24_1_ < 256; lvt_24_1_ += 32) {
               for(int lvt_25_1_ = -256; lvt_25_1_ < 256; lvt_25_1_ += 32) {
                  lvt_7_1_.func_181662_b((double)(lvt_24_1_ + 0), (double)lvt_21_1_, (double)(lvt_25_1_ + 32)).func_181673_a((double)((float)(lvt_24_1_ + 0) * 4.8828125E-4F + lvt_22_1_), (double)((float)(lvt_25_1_ + 32) * 4.8828125E-4F + lvt_23_1_)).func_181666_a(lvt_9_1_, lvt_10_1_, lvt_11_1_, 0.8F).func_181675_d();
                  lvt_7_1_.func_181662_b((double)(lvt_24_1_ + 32), (double)lvt_21_1_, (double)(lvt_25_1_ + 32)).func_181673_a((double)((float)(lvt_24_1_ + 32) * 4.8828125E-4F + lvt_22_1_), (double)((float)(lvt_25_1_ + 32) * 4.8828125E-4F + lvt_23_1_)).func_181666_a(lvt_9_1_, lvt_10_1_, lvt_11_1_, 0.8F).func_181675_d();
                  lvt_7_1_.func_181662_b((double)(lvt_24_1_ + 32), (double)lvt_21_1_, (double)(lvt_25_1_ + 0)).func_181673_a((double)((float)(lvt_24_1_ + 32) * 4.8828125E-4F + lvt_22_1_), (double)((float)(lvt_25_1_ + 0) * 4.8828125E-4F + lvt_23_1_)).func_181666_a(lvt_9_1_, lvt_10_1_, lvt_11_1_, 0.8F).func_181675_d();
                  lvt_7_1_.func_181662_b((double)(lvt_24_1_ + 0), (double)lvt_21_1_, (double)(lvt_25_1_ + 0)).func_181673_a((double)((float)(lvt_24_1_ + 0) * 4.8828125E-4F + lvt_22_1_), (double)((float)(lvt_25_1_ + 0) * 4.8828125E-4F + lvt_23_1_)).func_181666_a(lvt_9_1_, lvt_10_1_, lvt_11_1_, 0.8F).func_181675_d();
               }
            }

            lvt_6_1_.func_78381_a();
            GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.func_179084_k();
            GlStateManager.func_179089_o();
         }
      }
   }

   public boolean func_72721_a(double p_72721_1_, double p_72721_3_, double p_72721_5_, float p_72721_7_) {
      return false;
   }

   private void func_180445_c(float p_180445_1_, int p_180445_2_) {
      GlStateManager.func_179129_p();
      float lvt_3_1_ = (float)(this.field_72777_q.func_175606_aa().field_70137_T + (this.field_72777_q.func_175606_aa().field_70163_u - this.field_72777_q.func_175606_aa().field_70137_T) * (double)p_180445_1_);
      Tessellator lvt_4_1_ = Tessellator.func_178181_a();
      WorldRenderer lvt_5_1_ = lvt_4_1_.func_178180_c();
      float lvt_6_1_ = 12.0F;
      float lvt_7_1_ = 4.0F;
      double lvt_8_1_ = (double)((float)this.field_72773_u + p_180445_1_);
      double lvt_10_1_ = (this.field_72777_q.func_175606_aa().field_70169_q + (this.field_72777_q.func_175606_aa().field_70165_t - this.field_72777_q.func_175606_aa().field_70169_q) * (double)p_180445_1_ + lvt_8_1_ * 0.029999999329447746D) / 12.0D;
      double lvt_12_1_ = (this.field_72777_q.func_175606_aa().field_70166_s + (this.field_72777_q.func_175606_aa().field_70161_v - this.field_72777_q.func_175606_aa().field_70166_s) * (double)p_180445_1_) / 12.0D + 0.33000001311302185D;
      float lvt_14_1_ = this.field_72769_h.field_73011_w.func_76571_f() - lvt_3_1_ + 0.33F;
      int lvt_15_1_ = MathHelper.func_76128_c(lvt_10_1_ / 2048.0D);
      int lvt_16_1_ = MathHelper.func_76128_c(lvt_12_1_ / 2048.0D);
      lvt_10_1_ = lvt_10_1_ - (double)(lvt_15_1_ * 2048);
      lvt_12_1_ = lvt_12_1_ - (double)(lvt_16_1_ * 2048);
      this.field_72770_i.func_110577_a(field_110925_j);
      GlStateManager.func_179147_l();
      GlStateManager.func_179120_a(770, 771, 1, 0);
      Vec3 lvt_17_1_ = this.field_72769_h.func_72824_f(p_180445_1_);
      float lvt_18_1_ = (float)lvt_17_1_.field_72450_a;
      float lvt_19_1_ = (float)lvt_17_1_.field_72448_b;
      float lvt_20_1_ = (float)lvt_17_1_.field_72449_c;
      if(p_180445_2_ != 2) {
         float lvt_21_1_ = (lvt_18_1_ * 30.0F + lvt_19_1_ * 59.0F + lvt_20_1_ * 11.0F) / 100.0F;
         float lvt_22_1_ = (lvt_18_1_ * 30.0F + lvt_19_1_ * 70.0F) / 100.0F;
         float lvt_23_1_ = (lvt_18_1_ * 30.0F + lvt_20_1_ * 70.0F) / 100.0F;
         lvt_18_1_ = lvt_21_1_;
         lvt_19_1_ = lvt_22_1_;
         lvt_20_1_ = lvt_23_1_;
      }

      float lvt_21_2_ = lvt_18_1_ * 0.9F;
      float lvt_22_2_ = lvt_19_1_ * 0.9F;
      float lvt_23_2_ = lvt_20_1_ * 0.9F;
      float lvt_24_1_ = lvt_18_1_ * 0.7F;
      float lvt_25_1_ = lvt_19_1_ * 0.7F;
      float lvt_26_1_ = lvt_20_1_ * 0.7F;
      float lvt_27_1_ = lvt_18_1_ * 0.8F;
      float lvt_28_1_ = lvt_19_1_ * 0.8F;
      float lvt_29_1_ = lvt_20_1_ * 0.8F;
      float lvt_30_1_ = 0.00390625F;
      float lvt_31_1_ = (float)MathHelper.func_76128_c(lvt_10_1_) * 0.00390625F;
      float lvt_32_1_ = (float)MathHelper.func_76128_c(lvt_12_1_) * 0.00390625F;
      float lvt_33_1_ = (float)(lvt_10_1_ - (double)MathHelper.func_76128_c(lvt_10_1_));
      float lvt_34_1_ = (float)(lvt_12_1_ - (double)MathHelper.func_76128_c(lvt_12_1_));
      int lvt_35_1_ = 8;
      int lvt_36_1_ = 4;
      float lvt_37_1_ = 9.765625E-4F;
      GlStateManager.func_179152_a(12.0F, 1.0F, 12.0F);

      for(int lvt_38_1_ = 0; lvt_38_1_ < 2; ++lvt_38_1_) {
         if(lvt_38_1_ == 0) {
            GlStateManager.func_179135_a(false, false, false, false);
         } else {
            switch(p_180445_2_) {
            case 0:
               GlStateManager.func_179135_a(false, true, true, true);
               break;
            case 1:
               GlStateManager.func_179135_a(true, false, false, true);
               break;
            case 2:
               GlStateManager.func_179135_a(true, true, true, true);
            }
         }

         for(int lvt_39_1_ = -3; lvt_39_1_ <= 4; ++lvt_39_1_) {
            for(int lvt_40_1_ = -3; lvt_40_1_ <= 4; ++lvt_40_1_) {
               lvt_5_1_.func_181668_a(7, DefaultVertexFormats.field_181712_l);
               float lvt_41_1_ = (float)(lvt_39_1_ * 8);
               float lvt_42_1_ = (float)(lvt_40_1_ * 8);
               float lvt_43_1_ = lvt_41_1_ - lvt_33_1_;
               float lvt_44_1_ = lvt_42_1_ - lvt_34_1_;
               if(lvt_14_1_ > -5.0F) {
                  lvt_5_1_.func_181662_b((double)(lvt_43_1_ + 0.0F), (double)(lvt_14_1_ + 0.0F), (double)(lvt_44_1_ + 8.0F)).func_181673_a((double)((lvt_41_1_ + 0.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 8.0F) * 0.00390625F + lvt_32_1_)).func_181666_a(lvt_24_1_, lvt_25_1_, lvt_26_1_, 0.8F).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
                  lvt_5_1_.func_181662_b((double)(lvt_43_1_ + 8.0F), (double)(lvt_14_1_ + 0.0F), (double)(lvt_44_1_ + 8.0F)).func_181673_a((double)((lvt_41_1_ + 8.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 8.0F) * 0.00390625F + lvt_32_1_)).func_181666_a(lvt_24_1_, lvt_25_1_, lvt_26_1_, 0.8F).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
                  lvt_5_1_.func_181662_b((double)(lvt_43_1_ + 8.0F), (double)(lvt_14_1_ + 0.0F), (double)(lvt_44_1_ + 0.0F)).func_181673_a((double)((lvt_41_1_ + 8.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 0.0F) * 0.00390625F + lvt_32_1_)).func_181666_a(lvt_24_1_, lvt_25_1_, lvt_26_1_, 0.8F).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
                  lvt_5_1_.func_181662_b((double)(lvt_43_1_ + 0.0F), (double)(lvt_14_1_ + 0.0F), (double)(lvt_44_1_ + 0.0F)).func_181673_a((double)((lvt_41_1_ + 0.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 0.0F) * 0.00390625F + lvt_32_1_)).func_181666_a(lvt_24_1_, lvt_25_1_, lvt_26_1_, 0.8F).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
               }

               if(lvt_14_1_ <= 5.0F) {
                  lvt_5_1_.func_181662_b((double)(lvt_43_1_ + 0.0F), (double)(lvt_14_1_ + 4.0F - 9.765625E-4F), (double)(lvt_44_1_ + 8.0F)).func_181673_a((double)((lvt_41_1_ + 0.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 8.0F) * 0.00390625F + lvt_32_1_)).func_181666_a(lvt_18_1_, lvt_19_1_, lvt_20_1_, 0.8F).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
                  lvt_5_1_.func_181662_b((double)(lvt_43_1_ + 8.0F), (double)(lvt_14_1_ + 4.0F - 9.765625E-4F), (double)(lvt_44_1_ + 8.0F)).func_181673_a((double)((lvt_41_1_ + 8.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 8.0F) * 0.00390625F + lvt_32_1_)).func_181666_a(lvt_18_1_, lvt_19_1_, lvt_20_1_, 0.8F).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
                  lvt_5_1_.func_181662_b((double)(lvt_43_1_ + 8.0F), (double)(lvt_14_1_ + 4.0F - 9.765625E-4F), (double)(lvt_44_1_ + 0.0F)).func_181673_a((double)((lvt_41_1_ + 8.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 0.0F) * 0.00390625F + lvt_32_1_)).func_181666_a(lvt_18_1_, lvt_19_1_, lvt_20_1_, 0.8F).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
                  lvt_5_1_.func_181662_b((double)(lvt_43_1_ + 0.0F), (double)(lvt_14_1_ + 4.0F - 9.765625E-4F), (double)(lvt_44_1_ + 0.0F)).func_181673_a((double)((lvt_41_1_ + 0.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 0.0F) * 0.00390625F + lvt_32_1_)).func_181666_a(lvt_18_1_, lvt_19_1_, lvt_20_1_, 0.8F).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
               }

               if(lvt_39_1_ > -1) {
                  for(int lvt_45_1_ = 0; lvt_45_1_ < 8; ++lvt_45_1_) {
                     lvt_5_1_.func_181662_b((double)(lvt_43_1_ + (float)lvt_45_1_ + 0.0F), (double)(lvt_14_1_ + 0.0F), (double)(lvt_44_1_ + 8.0F)).func_181673_a((double)((lvt_41_1_ + (float)lvt_45_1_ + 0.5F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 8.0F) * 0.00390625F + lvt_32_1_)).func_181666_a(lvt_21_2_, lvt_22_2_, lvt_23_2_, 0.8F).func_181663_c(-1.0F, 0.0F, 0.0F).func_181675_d();
                     lvt_5_1_.func_181662_b((double)(lvt_43_1_ + (float)lvt_45_1_ + 0.0F), (double)(lvt_14_1_ + 4.0F), (double)(lvt_44_1_ + 8.0F)).func_181673_a((double)((lvt_41_1_ + (float)lvt_45_1_ + 0.5F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 8.0F) * 0.00390625F + lvt_32_1_)).func_181666_a(lvt_21_2_, lvt_22_2_, lvt_23_2_, 0.8F).func_181663_c(-1.0F, 0.0F, 0.0F).func_181675_d();
                     lvt_5_1_.func_181662_b((double)(lvt_43_1_ + (float)lvt_45_1_ + 0.0F), (double)(lvt_14_1_ + 4.0F), (double)(lvt_44_1_ + 0.0F)).func_181673_a((double)((lvt_41_1_ + (float)lvt_45_1_ + 0.5F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 0.0F) * 0.00390625F + lvt_32_1_)).func_181666_a(lvt_21_2_, lvt_22_2_, lvt_23_2_, 0.8F).func_181663_c(-1.0F, 0.0F, 0.0F).func_181675_d();
                     lvt_5_1_.func_181662_b((double)(lvt_43_1_ + (float)lvt_45_1_ + 0.0F), (double)(lvt_14_1_ + 0.0F), (double)(lvt_44_1_ + 0.0F)).func_181673_a((double)((lvt_41_1_ + (float)lvt_45_1_ + 0.5F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 0.0F) * 0.00390625F + lvt_32_1_)).func_181666_a(lvt_21_2_, lvt_22_2_, lvt_23_2_, 0.8F).func_181663_c(-1.0F, 0.0F, 0.0F).func_181675_d();
                  }
               }

               if(lvt_39_1_ <= 1) {
                  for(int lvt_45_2_ = 0; lvt_45_2_ < 8; ++lvt_45_2_) {
                     lvt_5_1_.func_181662_b((double)(lvt_43_1_ + (float)lvt_45_2_ + 1.0F - 9.765625E-4F), (double)(lvt_14_1_ + 0.0F), (double)(lvt_44_1_ + 8.0F)).func_181673_a((double)((lvt_41_1_ + (float)lvt_45_2_ + 0.5F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 8.0F) * 0.00390625F + lvt_32_1_)).func_181666_a(lvt_21_2_, lvt_22_2_, lvt_23_2_, 0.8F).func_181663_c(1.0F, 0.0F, 0.0F).func_181675_d();
                     lvt_5_1_.func_181662_b((double)(lvt_43_1_ + (float)lvt_45_2_ + 1.0F - 9.765625E-4F), (double)(lvt_14_1_ + 4.0F), (double)(lvt_44_1_ + 8.0F)).func_181673_a((double)((lvt_41_1_ + (float)lvt_45_2_ + 0.5F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 8.0F) * 0.00390625F + lvt_32_1_)).func_181666_a(lvt_21_2_, lvt_22_2_, lvt_23_2_, 0.8F).func_181663_c(1.0F, 0.0F, 0.0F).func_181675_d();
                     lvt_5_1_.func_181662_b((double)(lvt_43_1_ + (float)lvt_45_2_ + 1.0F - 9.765625E-4F), (double)(lvt_14_1_ + 4.0F), (double)(lvt_44_1_ + 0.0F)).func_181673_a((double)((lvt_41_1_ + (float)lvt_45_2_ + 0.5F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 0.0F) * 0.00390625F + lvt_32_1_)).func_181666_a(lvt_21_2_, lvt_22_2_, lvt_23_2_, 0.8F).func_181663_c(1.0F, 0.0F, 0.0F).func_181675_d();
                     lvt_5_1_.func_181662_b((double)(lvt_43_1_ + (float)lvt_45_2_ + 1.0F - 9.765625E-4F), (double)(lvt_14_1_ + 0.0F), (double)(lvt_44_1_ + 0.0F)).func_181673_a((double)((lvt_41_1_ + (float)lvt_45_2_ + 0.5F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + 0.0F) * 0.00390625F + lvt_32_1_)).func_181666_a(lvt_21_2_, lvt_22_2_, lvt_23_2_, 0.8F).func_181663_c(1.0F, 0.0F, 0.0F).func_181675_d();
                  }
               }

               if(lvt_40_1_ > -1) {
                  for(int lvt_45_3_ = 0; lvt_45_3_ < 8; ++lvt_45_3_) {
                     lvt_5_1_.func_181662_b((double)(lvt_43_1_ + 0.0F), (double)(lvt_14_1_ + 4.0F), (double)(lvt_44_1_ + (float)lvt_45_3_ + 0.0F)).func_181673_a((double)((lvt_41_1_ + 0.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + (float)lvt_45_3_ + 0.5F) * 0.00390625F + lvt_32_1_)).func_181666_a(lvt_27_1_, lvt_28_1_, lvt_29_1_, 0.8F).func_181663_c(0.0F, 0.0F, -1.0F).func_181675_d();
                     lvt_5_1_.func_181662_b((double)(lvt_43_1_ + 8.0F), (double)(lvt_14_1_ + 4.0F), (double)(lvt_44_1_ + (float)lvt_45_3_ + 0.0F)).func_181673_a((double)((lvt_41_1_ + 8.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + (float)lvt_45_3_ + 0.5F) * 0.00390625F + lvt_32_1_)).func_181666_a(lvt_27_1_, lvt_28_1_, lvt_29_1_, 0.8F).func_181663_c(0.0F, 0.0F, -1.0F).func_181675_d();
                     lvt_5_1_.func_181662_b((double)(lvt_43_1_ + 8.0F), (double)(lvt_14_1_ + 0.0F), (double)(lvt_44_1_ + (float)lvt_45_3_ + 0.0F)).func_181673_a((double)((lvt_41_1_ + 8.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + (float)lvt_45_3_ + 0.5F) * 0.00390625F + lvt_32_1_)).func_181666_a(lvt_27_1_, lvt_28_1_, lvt_29_1_, 0.8F).func_181663_c(0.0F, 0.0F, -1.0F).func_181675_d();
                     lvt_5_1_.func_181662_b((double)(lvt_43_1_ + 0.0F), (double)(lvt_14_1_ + 0.0F), (double)(lvt_44_1_ + (float)lvt_45_3_ + 0.0F)).func_181673_a((double)((lvt_41_1_ + 0.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + (float)lvt_45_3_ + 0.5F) * 0.00390625F + lvt_32_1_)).func_181666_a(lvt_27_1_, lvt_28_1_, lvt_29_1_, 0.8F).func_181663_c(0.0F, 0.0F, -1.0F).func_181675_d();
                  }
               }

               if(lvt_40_1_ <= 1) {
                  for(int lvt_45_4_ = 0; lvt_45_4_ < 8; ++lvt_45_4_) {
                     lvt_5_1_.func_181662_b((double)(lvt_43_1_ + 0.0F), (double)(lvt_14_1_ + 4.0F), (double)(lvt_44_1_ + (float)lvt_45_4_ + 1.0F - 9.765625E-4F)).func_181673_a((double)((lvt_41_1_ + 0.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + (float)lvt_45_4_ + 0.5F) * 0.00390625F + lvt_32_1_)).func_181666_a(lvt_27_1_, lvt_28_1_, lvt_29_1_, 0.8F).func_181663_c(0.0F, 0.0F, 1.0F).func_181675_d();
                     lvt_5_1_.func_181662_b((double)(lvt_43_1_ + 8.0F), (double)(lvt_14_1_ + 4.0F), (double)(lvt_44_1_ + (float)lvt_45_4_ + 1.0F - 9.765625E-4F)).func_181673_a((double)((lvt_41_1_ + 8.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + (float)lvt_45_4_ + 0.5F) * 0.00390625F + lvt_32_1_)).func_181666_a(lvt_27_1_, lvt_28_1_, lvt_29_1_, 0.8F).func_181663_c(0.0F, 0.0F, 1.0F).func_181675_d();
                     lvt_5_1_.func_181662_b((double)(lvt_43_1_ + 8.0F), (double)(lvt_14_1_ + 0.0F), (double)(lvt_44_1_ + (float)lvt_45_4_ + 1.0F - 9.765625E-4F)).func_181673_a((double)((lvt_41_1_ + 8.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + (float)lvt_45_4_ + 0.5F) * 0.00390625F + lvt_32_1_)).func_181666_a(lvt_27_1_, lvt_28_1_, lvt_29_1_, 0.8F).func_181663_c(0.0F, 0.0F, 1.0F).func_181675_d();
                     lvt_5_1_.func_181662_b((double)(lvt_43_1_ + 0.0F), (double)(lvt_14_1_ + 0.0F), (double)(lvt_44_1_ + (float)lvt_45_4_ + 1.0F - 9.765625E-4F)).func_181673_a((double)((lvt_41_1_ + 0.0F) * 0.00390625F + lvt_31_1_), (double)((lvt_42_1_ + (float)lvt_45_4_ + 0.5F) * 0.00390625F + lvt_32_1_)).func_181666_a(lvt_27_1_, lvt_28_1_, lvt_29_1_, 0.8F).func_181663_c(0.0F, 0.0F, 1.0F).func_181675_d();
                  }
               }

               lvt_4_1_.func_78381_a();
            }
         }
      }

      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179084_k();
      GlStateManager.func_179089_o();
   }

   public void func_174967_a(long p_174967_1_) {
      this.field_147595_R |= this.field_174995_M.func_178516_a(p_174967_1_);
      if(!this.field_175009_l.isEmpty()) {
         Iterator<RenderChunk> lvt_3_1_ = this.field_175009_l.iterator();

         while(lvt_3_1_.hasNext()) {
            RenderChunk lvt_4_1_ = (RenderChunk)lvt_3_1_.next();
            if(!this.field_174995_M.func_178507_a(lvt_4_1_)) {
               break;
            }

            lvt_4_1_.func_178575_a(false);
            lvt_3_1_.remove();
            long lvt_5_1_ = p_174967_1_ - System.nanoTime();
            if(lvt_5_1_ < 0L) {
               break;
            }
         }
      }

   }

   public void func_180449_a(Entity p_180449_1_, float p_180449_2_) {
      Tessellator lvt_3_1_ = Tessellator.func_178181_a();
      WorldRenderer lvt_4_1_ = lvt_3_1_.func_178180_c();
      WorldBorder lvt_5_1_ = this.field_72769_h.func_175723_af();
      double lvt_6_1_ = (double)(this.field_72777_q.field_71474_y.field_151451_c * 16);
      if(p_180449_1_.field_70165_t >= lvt_5_1_.func_177728_d() - lvt_6_1_ || p_180449_1_.field_70165_t <= lvt_5_1_.func_177726_b() + lvt_6_1_ || p_180449_1_.field_70161_v >= lvt_5_1_.func_177733_e() - lvt_6_1_ || p_180449_1_.field_70161_v <= lvt_5_1_.func_177736_c() + lvt_6_1_) {
         double lvt_8_1_ = 1.0D - lvt_5_1_.func_177745_a(p_180449_1_) / lvt_6_1_;
         lvt_8_1_ = Math.pow(lvt_8_1_, 4.0D);
         double lvt_10_1_ = p_180449_1_.field_70142_S + (p_180449_1_.field_70165_t - p_180449_1_.field_70142_S) * (double)p_180449_2_;
         double lvt_12_1_ = p_180449_1_.field_70137_T + (p_180449_1_.field_70163_u - p_180449_1_.field_70137_T) * (double)p_180449_2_;
         double lvt_14_1_ = p_180449_1_.field_70136_U + (p_180449_1_.field_70161_v - p_180449_1_.field_70136_U) * (double)p_180449_2_;
         GlStateManager.func_179147_l();
         GlStateManager.func_179120_a(770, 1, 1, 0);
         this.field_72770_i.func_110577_a(field_175006_g);
         GlStateManager.func_179132_a(false);
         GlStateManager.func_179094_E();
         int lvt_16_1_ = lvt_5_1_.func_177734_a().func_177766_a();
         float lvt_17_1_ = (float)(lvt_16_1_ >> 16 & 255) / 255.0F;
         float lvt_18_1_ = (float)(lvt_16_1_ >> 8 & 255) / 255.0F;
         float lvt_19_1_ = (float)(lvt_16_1_ & 255) / 255.0F;
         GlStateManager.func_179131_c(lvt_17_1_, lvt_18_1_, lvt_19_1_, (float)lvt_8_1_);
         GlStateManager.func_179136_a(-3.0F, -3.0F);
         GlStateManager.func_179088_q();
         GlStateManager.func_179092_a(516, 0.1F);
         GlStateManager.func_179141_d();
         GlStateManager.func_179129_p();
         float lvt_20_1_ = (float)(Minecraft.func_71386_F() % 3000L) / 3000.0F;
         float lvt_21_1_ = 0.0F;
         float lvt_22_1_ = 0.0F;
         float lvt_23_1_ = 128.0F;
         lvt_4_1_.func_181668_a(7, DefaultVertexFormats.field_181707_g);
         lvt_4_1_.func_178969_c(-lvt_10_1_, -lvt_12_1_, -lvt_14_1_);
         double lvt_24_1_ = Math.max((double)MathHelper.func_76128_c(lvt_14_1_ - lvt_6_1_), lvt_5_1_.func_177736_c());
         double lvt_26_1_ = Math.min((double)MathHelper.func_76143_f(lvt_14_1_ + lvt_6_1_), lvt_5_1_.func_177733_e());
         if(lvt_10_1_ > lvt_5_1_.func_177728_d() - lvt_6_1_) {
            float lvt_28_1_ = 0.0F;

            for(double lvt_29_1_ = lvt_24_1_; lvt_29_1_ < lvt_26_1_; lvt_28_1_ += 0.5F) {
               double lvt_31_1_ = Math.min(1.0D, lvt_26_1_ - lvt_29_1_);
               float lvt_33_1_ = (float)lvt_31_1_ * 0.5F;
               lvt_4_1_.func_181662_b(lvt_5_1_.func_177728_d(), 256.0D, lvt_29_1_).func_181673_a((double)(lvt_20_1_ + lvt_28_1_), (double)(lvt_20_1_ + 0.0F)).func_181675_d();
               lvt_4_1_.func_181662_b(lvt_5_1_.func_177728_d(), 256.0D, lvt_29_1_ + lvt_31_1_).func_181673_a((double)(lvt_20_1_ + lvt_33_1_ + lvt_28_1_), (double)(lvt_20_1_ + 0.0F)).func_181675_d();
               lvt_4_1_.func_181662_b(lvt_5_1_.func_177728_d(), 0.0D, lvt_29_1_ + lvt_31_1_).func_181673_a((double)(lvt_20_1_ + lvt_33_1_ + lvt_28_1_), (double)(lvt_20_1_ + 128.0F)).func_181675_d();
               lvt_4_1_.func_181662_b(lvt_5_1_.func_177728_d(), 0.0D, lvt_29_1_).func_181673_a((double)(lvt_20_1_ + lvt_28_1_), (double)(lvt_20_1_ + 128.0F)).func_181675_d();
               ++lvt_29_1_;
            }
         }

         if(lvt_10_1_ < lvt_5_1_.func_177726_b() + lvt_6_1_) {
            float lvt_28_2_ = 0.0F;

            for(double lvt_29_2_ = lvt_24_1_; lvt_29_2_ < lvt_26_1_; lvt_28_2_ += 0.5F) {
               double lvt_31_2_ = Math.min(1.0D, lvt_26_1_ - lvt_29_2_);
               float lvt_33_2_ = (float)lvt_31_2_ * 0.5F;
               lvt_4_1_.func_181662_b(lvt_5_1_.func_177726_b(), 256.0D, lvt_29_2_).func_181673_a((double)(lvt_20_1_ + lvt_28_2_), (double)(lvt_20_1_ + 0.0F)).func_181675_d();
               lvt_4_1_.func_181662_b(lvt_5_1_.func_177726_b(), 256.0D, lvt_29_2_ + lvt_31_2_).func_181673_a((double)(lvt_20_1_ + lvt_33_2_ + lvt_28_2_), (double)(lvt_20_1_ + 0.0F)).func_181675_d();
               lvt_4_1_.func_181662_b(lvt_5_1_.func_177726_b(), 0.0D, lvt_29_2_ + lvt_31_2_).func_181673_a((double)(lvt_20_1_ + lvt_33_2_ + lvt_28_2_), (double)(lvt_20_1_ + 128.0F)).func_181675_d();
               lvt_4_1_.func_181662_b(lvt_5_1_.func_177726_b(), 0.0D, lvt_29_2_).func_181673_a((double)(lvt_20_1_ + lvt_28_2_), (double)(lvt_20_1_ + 128.0F)).func_181675_d();
               ++lvt_29_2_;
            }
         }

         lvt_24_1_ = Math.max((double)MathHelper.func_76128_c(lvt_10_1_ - lvt_6_1_), lvt_5_1_.func_177726_b());
         lvt_26_1_ = Math.min((double)MathHelper.func_76143_f(lvt_10_1_ + lvt_6_1_), lvt_5_1_.func_177728_d());
         if(lvt_14_1_ > lvt_5_1_.func_177733_e() - lvt_6_1_) {
            float lvt_28_3_ = 0.0F;

            for(double lvt_29_3_ = lvt_24_1_; lvt_29_3_ < lvt_26_1_; lvt_28_3_ += 0.5F) {
               double lvt_31_3_ = Math.min(1.0D, lvt_26_1_ - lvt_29_3_);
               float lvt_33_3_ = (float)lvt_31_3_ * 0.5F;
               lvt_4_1_.func_181662_b(lvt_29_3_, 256.0D, lvt_5_1_.func_177733_e()).func_181673_a((double)(lvt_20_1_ + lvt_28_3_), (double)(lvt_20_1_ + 0.0F)).func_181675_d();
               lvt_4_1_.func_181662_b(lvt_29_3_ + lvt_31_3_, 256.0D, lvt_5_1_.func_177733_e()).func_181673_a((double)(lvt_20_1_ + lvt_33_3_ + lvt_28_3_), (double)(lvt_20_1_ + 0.0F)).func_181675_d();
               lvt_4_1_.func_181662_b(lvt_29_3_ + lvt_31_3_, 0.0D, lvt_5_1_.func_177733_e()).func_181673_a((double)(lvt_20_1_ + lvt_33_3_ + lvt_28_3_), (double)(lvt_20_1_ + 128.0F)).func_181675_d();
               lvt_4_1_.func_181662_b(lvt_29_3_, 0.0D, lvt_5_1_.func_177733_e()).func_181673_a((double)(lvt_20_1_ + lvt_28_3_), (double)(lvt_20_1_ + 128.0F)).func_181675_d();
               ++lvt_29_3_;
            }
         }

         if(lvt_14_1_ < lvt_5_1_.func_177736_c() + lvt_6_1_) {
            float lvt_28_4_ = 0.0F;

            for(double lvt_29_4_ = lvt_24_1_; lvt_29_4_ < lvt_26_1_; lvt_28_4_ += 0.5F) {
               double lvt_31_4_ = Math.min(1.0D, lvt_26_1_ - lvt_29_4_);
               float lvt_33_4_ = (float)lvt_31_4_ * 0.5F;
               lvt_4_1_.func_181662_b(lvt_29_4_, 256.0D, lvt_5_1_.func_177736_c()).func_181673_a((double)(lvt_20_1_ + lvt_28_4_), (double)(lvt_20_1_ + 0.0F)).func_181675_d();
               lvt_4_1_.func_181662_b(lvt_29_4_ + lvt_31_4_, 256.0D, lvt_5_1_.func_177736_c()).func_181673_a((double)(lvt_20_1_ + lvt_33_4_ + lvt_28_4_), (double)(lvt_20_1_ + 0.0F)).func_181675_d();
               lvt_4_1_.func_181662_b(lvt_29_4_ + lvt_31_4_, 0.0D, lvt_5_1_.func_177736_c()).func_181673_a((double)(lvt_20_1_ + lvt_33_4_ + lvt_28_4_), (double)(lvt_20_1_ + 128.0F)).func_181675_d();
               lvt_4_1_.func_181662_b(lvt_29_4_, 0.0D, lvt_5_1_.func_177736_c()).func_181673_a((double)(lvt_20_1_ + lvt_28_4_), (double)(lvt_20_1_ + 128.0F)).func_181675_d();
               ++lvt_29_4_;
            }
         }

         lvt_3_1_.func_78381_a();
         lvt_4_1_.func_178969_c(0.0D, 0.0D, 0.0D);
         GlStateManager.func_179089_o();
         GlStateManager.func_179118_c();
         GlStateManager.func_179136_a(0.0F, 0.0F);
         GlStateManager.func_179113_r();
         GlStateManager.func_179141_d();
         GlStateManager.func_179084_k();
         GlStateManager.func_179121_F();
         GlStateManager.func_179132_a(true);
      }
   }

   private void func_180443_s() {
      GlStateManager.func_179120_a(774, 768, 1, 0);
      GlStateManager.func_179147_l();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 0.5F);
      GlStateManager.func_179136_a(-3.0F, -3.0F);
      GlStateManager.func_179088_q();
      GlStateManager.func_179092_a(516, 0.1F);
      GlStateManager.func_179141_d();
      GlStateManager.func_179094_E();
   }

   private void func_174969_t() {
      GlStateManager.func_179118_c();
      GlStateManager.func_179136_a(0.0F, 0.0F);
      GlStateManager.func_179113_r();
      GlStateManager.func_179141_d();
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179121_F();
   }

   public void func_174981_a(Tessellator p_174981_1_, WorldRenderer p_174981_2_, Entity p_174981_3_, float p_174981_4_) {
      double lvt_5_1_ = p_174981_3_.field_70142_S + (p_174981_3_.field_70165_t - p_174981_3_.field_70142_S) * (double)p_174981_4_;
      double lvt_7_1_ = p_174981_3_.field_70137_T + (p_174981_3_.field_70163_u - p_174981_3_.field_70137_T) * (double)p_174981_4_;
      double lvt_9_1_ = p_174981_3_.field_70136_U + (p_174981_3_.field_70161_v - p_174981_3_.field_70136_U) * (double)p_174981_4_;
      if(!this.field_72738_E.isEmpty()) {
         this.field_72770_i.func_110577_a(TextureMap.field_110575_b);
         this.func_180443_s();
         p_174981_2_.func_181668_a(7, DefaultVertexFormats.field_176600_a);
         p_174981_2_.func_178969_c(-lvt_5_1_, -lvt_7_1_, -lvt_9_1_);
         p_174981_2_.func_78914_f();
         Iterator<DestroyBlockProgress> lvt_11_1_ = this.field_72738_E.values().iterator();

         while(lvt_11_1_.hasNext()) {
            DestroyBlockProgress lvt_12_1_ = (DestroyBlockProgress)lvt_11_1_.next();
            BlockPos lvt_13_1_ = lvt_12_1_.func_180246_b();
            double lvt_14_1_ = (double)lvt_13_1_.func_177958_n() - lvt_5_1_;
            double lvt_16_1_ = (double)lvt_13_1_.func_177956_o() - lvt_7_1_;
            double lvt_18_1_ = (double)lvt_13_1_.func_177952_p() - lvt_9_1_;
            Block lvt_20_1_ = this.field_72769_h.func_180495_p(lvt_13_1_).func_177230_c();
            if(!(lvt_20_1_ instanceof BlockChest) && !(lvt_20_1_ instanceof BlockEnderChest) && !(lvt_20_1_ instanceof BlockSign) && !(lvt_20_1_ instanceof BlockSkull)) {
               if(lvt_14_1_ * lvt_14_1_ + lvt_16_1_ * lvt_16_1_ + lvt_18_1_ * lvt_18_1_ > 1024.0D) {
                  lvt_11_1_.remove();
               } else {
                  IBlockState lvt_21_1_ = this.field_72769_h.func_180495_p(lvt_13_1_);
                  if(lvt_21_1_.func_177230_c().func_149688_o() != Material.field_151579_a) {
                     int lvt_22_1_ = lvt_12_1_.func_73106_e();
                     TextureAtlasSprite lvt_23_1_ = this.field_94141_F[lvt_22_1_];
                     BlockRendererDispatcher lvt_24_1_ = this.field_72777_q.func_175602_ab();
                     lvt_24_1_.func_175020_a(lvt_21_1_, lvt_13_1_, lvt_23_1_, this.field_72769_h);
                  }
               }
            }
         }

         p_174981_1_.func_78381_a();
         p_174981_2_.func_178969_c(0.0D, 0.0D, 0.0D);
         this.func_174969_t();
      }

   }

   public void func_72731_b(EntityPlayer p_72731_1_, MovingObjectPosition p_72731_2_, int p_72731_3_, float p_72731_4_) {
      if(p_72731_3_ == 0 && p_72731_2_.field_72313_a == MovingObjectPosition.MovingObjectType.BLOCK) {
         GlStateManager.func_179147_l();
         GlStateManager.func_179120_a(770, 771, 1, 0);
         GlStateManager.func_179131_c(0.0F, 0.0F, 0.0F, 0.4F);
         GL11.glLineWidth(2.0F);
         GlStateManager.func_179090_x();
         GlStateManager.func_179132_a(false);
         float lvt_5_1_ = 0.002F;
         BlockPos lvt_6_1_ = p_72731_2_.func_178782_a();
         Block lvt_7_1_ = this.field_72769_h.func_180495_p(lvt_6_1_).func_177230_c();
         if(lvt_7_1_.func_149688_o() != Material.field_151579_a && this.field_72769_h.func_175723_af().func_177746_a(lvt_6_1_)) {
            lvt_7_1_.func_180654_a(this.field_72769_h, lvt_6_1_);
            double lvt_8_1_ = p_72731_1_.field_70142_S + (p_72731_1_.field_70165_t - p_72731_1_.field_70142_S) * (double)p_72731_4_;
            double lvt_10_1_ = p_72731_1_.field_70137_T + (p_72731_1_.field_70163_u - p_72731_1_.field_70137_T) * (double)p_72731_4_;
            double lvt_12_1_ = p_72731_1_.field_70136_U + (p_72731_1_.field_70161_v - p_72731_1_.field_70136_U) * (double)p_72731_4_;
            func_181561_a(lvt_7_1_.func_180646_a(this.field_72769_h, lvt_6_1_).func_72314_b(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D).func_72317_d(-lvt_8_1_, -lvt_10_1_, -lvt_12_1_));
         }

         GlStateManager.func_179132_a(true);
         GlStateManager.func_179098_w();
         GlStateManager.func_179084_k();
      }

   }

   public static void func_181561_a(AxisAlignedBB p_181561_0_) {
      Tessellator lvt_1_1_ = Tessellator.func_178181_a();
      WorldRenderer lvt_2_1_ = lvt_1_1_.func_178180_c();
      lvt_2_1_.func_181668_a(3, DefaultVertexFormats.field_181705_e);
      lvt_2_1_.func_181662_b(p_181561_0_.field_72340_a, p_181561_0_.field_72338_b, p_181561_0_.field_72339_c).func_181675_d();
      lvt_2_1_.func_181662_b(p_181561_0_.field_72336_d, p_181561_0_.field_72338_b, p_181561_0_.field_72339_c).func_181675_d();
      lvt_2_1_.func_181662_b(p_181561_0_.field_72336_d, p_181561_0_.field_72338_b, p_181561_0_.field_72334_f).func_181675_d();
      lvt_2_1_.func_181662_b(p_181561_0_.field_72340_a, p_181561_0_.field_72338_b, p_181561_0_.field_72334_f).func_181675_d();
      lvt_2_1_.func_181662_b(p_181561_0_.field_72340_a, p_181561_0_.field_72338_b, p_181561_0_.field_72339_c).func_181675_d();
      lvt_1_1_.func_78381_a();
      lvt_2_1_.func_181668_a(3, DefaultVertexFormats.field_181705_e);
      lvt_2_1_.func_181662_b(p_181561_0_.field_72340_a, p_181561_0_.field_72337_e, p_181561_0_.field_72339_c).func_181675_d();
      lvt_2_1_.func_181662_b(p_181561_0_.field_72336_d, p_181561_0_.field_72337_e, p_181561_0_.field_72339_c).func_181675_d();
      lvt_2_1_.func_181662_b(p_181561_0_.field_72336_d, p_181561_0_.field_72337_e, p_181561_0_.field_72334_f).func_181675_d();
      lvt_2_1_.func_181662_b(p_181561_0_.field_72340_a, p_181561_0_.field_72337_e, p_181561_0_.field_72334_f).func_181675_d();
      lvt_2_1_.func_181662_b(p_181561_0_.field_72340_a, p_181561_0_.field_72337_e, p_181561_0_.field_72339_c).func_181675_d();
      lvt_1_1_.func_78381_a();
      lvt_2_1_.func_181668_a(1, DefaultVertexFormats.field_181705_e);
      lvt_2_1_.func_181662_b(p_181561_0_.field_72340_a, p_181561_0_.field_72338_b, p_181561_0_.field_72339_c).func_181675_d();
      lvt_2_1_.func_181662_b(p_181561_0_.field_72340_a, p_181561_0_.field_72337_e, p_181561_0_.field_72339_c).func_181675_d();
      lvt_2_1_.func_181662_b(p_181561_0_.field_72336_d, p_181561_0_.field_72338_b, p_181561_0_.field_72339_c).func_181675_d();
      lvt_2_1_.func_181662_b(p_181561_0_.field_72336_d, p_181561_0_.field_72337_e, p_181561_0_.field_72339_c).func_181675_d();
      lvt_2_1_.func_181662_b(p_181561_0_.field_72336_d, p_181561_0_.field_72338_b, p_181561_0_.field_72334_f).func_181675_d();
      lvt_2_1_.func_181662_b(p_181561_0_.field_72336_d, p_181561_0_.field_72337_e, p_181561_0_.field_72334_f).func_181675_d();
      lvt_2_1_.func_181662_b(p_181561_0_.field_72340_a, p_181561_0_.field_72338_b, p_181561_0_.field_72334_f).func_181675_d();
      lvt_2_1_.func_181662_b(p_181561_0_.field_72340_a, p_181561_0_.field_72337_e, p_181561_0_.field_72334_f).func_181675_d();
      lvt_1_1_.func_78381_a();
   }

   public static void func_181563_a(AxisAlignedBB p_181563_0_, int p_181563_1_, int p_181563_2_, int p_181563_3_, int p_181563_4_) {
      Tessellator lvt_5_1_ = Tessellator.func_178181_a();
      WorldRenderer lvt_6_1_ = lvt_5_1_.func_178180_c();
      lvt_6_1_.func_181668_a(3, DefaultVertexFormats.field_181706_f);
      lvt_6_1_.func_181662_b(p_181563_0_.field_72340_a, p_181563_0_.field_72338_b, p_181563_0_.field_72339_c).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
      lvt_6_1_.func_181662_b(p_181563_0_.field_72336_d, p_181563_0_.field_72338_b, p_181563_0_.field_72339_c).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
      lvt_6_1_.func_181662_b(p_181563_0_.field_72336_d, p_181563_0_.field_72338_b, p_181563_0_.field_72334_f).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
      lvt_6_1_.func_181662_b(p_181563_0_.field_72340_a, p_181563_0_.field_72338_b, p_181563_0_.field_72334_f).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
      lvt_6_1_.func_181662_b(p_181563_0_.field_72340_a, p_181563_0_.field_72338_b, p_181563_0_.field_72339_c).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
      lvt_5_1_.func_78381_a();
      lvt_6_1_.func_181668_a(3, DefaultVertexFormats.field_181706_f);
      lvt_6_1_.func_181662_b(p_181563_0_.field_72340_a, p_181563_0_.field_72337_e, p_181563_0_.field_72339_c).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
      lvt_6_1_.func_181662_b(p_181563_0_.field_72336_d, p_181563_0_.field_72337_e, p_181563_0_.field_72339_c).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
      lvt_6_1_.func_181662_b(p_181563_0_.field_72336_d, p_181563_0_.field_72337_e, p_181563_0_.field_72334_f).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
      lvt_6_1_.func_181662_b(p_181563_0_.field_72340_a, p_181563_0_.field_72337_e, p_181563_0_.field_72334_f).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
      lvt_6_1_.func_181662_b(p_181563_0_.field_72340_a, p_181563_0_.field_72337_e, p_181563_0_.field_72339_c).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
      lvt_5_1_.func_78381_a();
      lvt_6_1_.func_181668_a(1, DefaultVertexFormats.field_181706_f);
      lvt_6_1_.func_181662_b(p_181563_0_.field_72340_a, p_181563_0_.field_72338_b, p_181563_0_.field_72339_c).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
      lvt_6_1_.func_181662_b(p_181563_0_.field_72340_a, p_181563_0_.field_72337_e, p_181563_0_.field_72339_c).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
      lvt_6_1_.func_181662_b(p_181563_0_.field_72336_d, p_181563_0_.field_72338_b, p_181563_0_.field_72339_c).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
      lvt_6_1_.func_181662_b(p_181563_0_.field_72336_d, p_181563_0_.field_72337_e, p_181563_0_.field_72339_c).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
      lvt_6_1_.func_181662_b(p_181563_0_.field_72336_d, p_181563_0_.field_72338_b, p_181563_0_.field_72334_f).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
      lvt_6_1_.func_181662_b(p_181563_0_.field_72336_d, p_181563_0_.field_72337_e, p_181563_0_.field_72334_f).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
      lvt_6_1_.func_181662_b(p_181563_0_.field_72340_a, p_181563_0_.field_72338_b, p_181563_0_.field_72334_f).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
      lvt_6_1_.func_181662_b(p_181563_0_.field_72340_a, p_181563_0_.field_72337_e, p_181563_0_.field_72334_f).func_181669_b(p_181563_1_, p_181563_2_, p_181563_3_, p_181563_4_).func_181675_d();
      lvt_5_1_.func_78381_a();
   }

   private void func_72725_b(int p_72725_1_, int p_72725_2_, int p_72725_3_, int p_72725_4_, int p_72725_5_, int p_72725_6_) {
      this.field_175008_n.func_178162_a(p_72725_1_, p_72725_2_, p_72725_3_, p_72725_4_, p_72725_5_, p_72725_6_);
   }

   public void func_174960_a(BlockPos p_174960_1_) {
      int lvt_2_1_ = p_174960_1_.func_177958_n();
      int lvt_3_1_ = p_174960_1_.func_177956_o();
      int lvt_4_1_ = p_174960_1_.func_177952_p();
      this.func_72725_b(lvt_2_1_ - 1, lvt_3_1_ - 1, lvt_4_1_ - 1, lvt_2_1_ + 1, lvt_3_1_ + 1, lvt_4_1_ + 1);
   }

   public void func_174959_b(BlockPos p_174959_1_) {
      int lvt_2_1_ = p_174959_1_.func_177958_n();
      int lvt_3_1_ = p_174959_1_.func_177956_o();
      int lvt_4_1_ = p_174959_1_.func_177952_p();
      this.func_72725_b(lvt_2_1_ - 1, lvt_3_1_ - 1, lvt_4_1_ - 1, lvt_2_1_ + 1, lvt_3_1_ + 1, lvt_4_1_ + 1);
   }

   public void func_147585_a(int p_147585_1_, int p_147585_2_, int p_147585_3_, int p_147585_4_, int p_147585_5_, int p_147585_6_) {
      this.func_72725_b(p_147585_1_ - 1, p_147585_2_ - 1, p_147585_3_ - 1, p_147585_4_ + 1, p_147585_5_ + 1, p_147585_6_ + 1);
   }

   public void func_174961_a(String p_174961_1_, BlockPos p_174961_2_) {
      ISound lvt_3_1_ = (ISound)this.field_147593_P.get(p_174961_2_);
      if(lvt_3_1_ != null) {
         this.field_72777_q.func_147118_V().func_147683_b(lvt_3_1_);
         this.field_147593_P.remove(p_174961_2_);
      }

      if(p_174961_1_ != null) {
         ItemRecord lvt_4_1_ = ItemRecord.func_150926_b(p_174961_1_);
         if(lvt_4_1_ != null) {
            this.field_72777_q.field_71456_v.func_73833_a(lvt_4_1_.func_150927_i());
         }

         PositionedSoundRecord var5 = PositionedSoundRecord.func_147675_a(new ResourceLocation(p_174961_1_), (float)p_174961_2_.func_177958_n(), (float)p_174961_2_.func_177956_o(), (float)p_174961_2_.func_177952_p());
         this.field_147593_P.put(p_174961_2_, var5);
         this.field_72777_q.func_147118_V().func_147682_a(var5);
      }

   }

   public void func_72704_a(String p_72704_1_, double p_72704_2_, double p_72704_4_, double p_72704_6_, float p_72704_8_, float p_72704_9_) {
   }

   public void func_85102_a(EntityPlayer p_85102_1_, String p_85102_2_, double p_85102_3_, double p_85102_5_, double p_85102_7_, float p_85102_9_, float p_85102_10_) {
   }

   public void func_180442_a(int p_180442_1_, boolean p_180442_2_, final double p_180442_3_, final double p_180442_5_, final double p_180442_7_, double p_180442_9_, double p_180442_11_, double p_180442_13_, int... p_180442_15_) {
      try {
         this.func_174974_b(p_180442_1_, p_180442_2_, p_180442_3_, p_180442_5_, p_180442_7_, p_180442_9_, p_180442_11_, p_180442_13_, p_180442_15_);
      } catch (Throwable var19) {
         CrashReport lvt_17_1_ = CrashReport.func_85055_a(var19, "Exception while adding particle");
         CrashReportCategory lvt_18_1_ = lvt_17_1_.func_85058_a("Particle being added");
         lvt_18_1_.func_71507_a("ID", Integer.valueOf(p_180442_1_));
         if(p_180442_15_ != null) {
            lvt_18_1_.func_71507_a("Parameters", p_180442_15_);
         }

         lvt_18_1_.func_71500_a("Position", new Callable<String>() {
            public String call() throws Exception {
               return CrashReportCategory.func_85074_a(p_180442_3_, p_180442_5_, p_180442_7_);
            }

            // $FF: synthetic method
            public Object call() throws Exception {
               return this.call();
            }
         });
         throw new ReportedException(lvt_17_1_);
      }
   }

   private void func_174972_a(EnumParticleTypes p_174972_1_, double p_174972_2_, double p_174972_4_, double p_174972_6_, double p_174972_8_, double p_174972_10_, double p_174972_12_, int... p_174972_14_) {
      this.func_180442_a(p_174972_1_.func_179348_c(), p_174972_1_.func_179344_e(), p_174972_2_, p_174972_4_, p_174972_6_, p_174972_8_, p_174972_10_, p_174972_12_, p_174972_14_);
   }

   private EntityFX func_174974_b(int p_174974_1_, boolean p_174974_2_, double p_174974_3_, double p_174974_5_, double p_174974_7_, double p_174974_9_, double p_174974_11_, double p_174974_13_, int... p_174974_15_) {
      if(this.field_72777_q != null && this.field_72777_q.func_175606_aa() != null && this.field_72777_q.field_71452_i != null) {
         int lvt_16_1_ = this.field_72777_q.field_71474_y.field_74362_aa;
         if(lvt_16_1_ == 1 && this.field_72769_h.field_73012_v.nextInt(3) == 0) {
            lvt_16_1_ = 2;
         }

         double lvt_17_1_ = this.field_72777_q.func_175606_aa().field_70165_t - p_174974_3_;
         double lvt_19_1_ = this.field_72777_q.func_175606_aa().field_70163_u - p_174974_5_;
         double lvt_21_1_ = this.field_72777_q.func_175606_aa().field_70161_v - p_174974_7_;
         if(p_174974_2_) {
            return this.field_72777_q.field_71452_i.func_178927_a(p_174974_1_, p_174974_3_, p_174974_5_, p_174974_7_, p_174974_9_, p_174974_11_, p_174974_13_, p_174974_15_);
         } else {
            double lvt_23_1_ = 16.0D;
            return lvt_17_1_ * lvt_17_1_ + lvt_19_1_ * lvt_19_1_ + lvt_21_1_ * lvt_21_1_ > 256.0D?null:(lvt_16_1_ > 1?null:this.field_72777_q.field_71452_i.func_178927_a(p_174974_1_, p_174974_3_, p_174974_5_, p_174974_7_, p_174974_9_, p_174974_11_, p_174974_13_, p_174974_15_));
         }
      } else {
         return null;
      }
   }

   public void func_72703_a(Entity p_72703_1_) {
   }

   public void func_72709_b(Entity p_72709_1_) {
   }

   public void func_72728_f() {
   }

   public void func_180440_a(int p_180440_1_, BlockPos p_180440_2_, int p_180440_3_) {
      switch(p_180440_1_) {
      case 1013:
      case 1018:
         if(this.field_72777_q.func_175606_aa() != null) {
            double lvt_4_1_ = (double)p_180440_2_.func_177958_n() - this.field_72777_q.func_175606_aa().field_70165_t;
            double lvt_6_1_ = (double)p_180440_2_.func_177956_o() - this.field_72777_q.func_175606_aa().field_70163_u;
            double lvt_8_1_ = (double)p_180440_2_.func_177952_p() - this.field_72777_q.func_175606_aa().field_70161_v;
            double lvt_10_1_ = Math.sqrt(lvt_4_1_ * lvt_4_1_ + lvt_6_1_ * lvt_6_1_ + lvt_8_1_ * lvt_8_1_);
            double lvt_12_1_ = this.field_72777_q.func_175606_aa().field_70165_t;
            double lvt_14_1_ = this.field_72777_q.func_175606_aa().field_70163_u;
            double lvt_16_1_ = this.field_72777_q.func_175606_aa().field_70161_v;
            if(lvt_10_1_ > 0.0D) {
               lvt_12_1_ += lvt_4_1_ / lvt_10_1_ * 2.0D;
               lvt_14_1_ += lvt_6_1_ / lvt_10_1_ * 2.0D;
               lvt_16_1_ += lvt_8_1_ / lvt_10_1_ * 2.0D;
            }

            if(p_180440_1_ == 1013) {
               this.field_72769_h.func_72980_b(lvt_12_1_, lvt_14_1_, lvt_16_1_, "mob.wither.spawn", 1.0F, 1.0F, false);
            } else {
               this.field_72769_h.func_72980_b(lvt_12_1_, lvt_14_1_, lvt_16_1_, "mob.enderdragon.end", 5.0F, 1.0F, false);
            }
         }
      default:
      }
   }

   public void func_180439_a(EntityPlayer p_180439_1_, int p_180439_2_, BlockPos p_180439_3_, int p_180439_4_) {
      Random lvt_5_1_ = this.field_72769_h.field_73012_v;
      switch(p_180439_2_) {
      case 1000:
         this.field_72769_h.func_175731_a(p_180439_3_, "random.click", 1.0F, 1.0F, false);
         break;
      case 1001:
         this.field_72769_h.func_175731_a(p_180439_3_, "random.click", 1.0F, 1.2F, false);
         break;
      case 1002:
         this.field_72769_h.func_175731_a(p_180439_3_, "random.bow", 1.0F, 1.2F, false);
         break;
      case 1003:
         this.field_72769_h.func_175731_a(p_180439_3_, "random.door_open", 1.0F, this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1004:
         this.field_72769_h.func_175731_a(p_180439_3_, "random.fizz", 0.5F, 2.6F + (lvt_5_1_.nextFloat() - lvt_5_1_.nextFloat()) * 0.8F, false);
         break;
      case 1005:
         if(Item.func_150899_d(p_180439_4_) instanceof ItemRecord) {
            this.field_72769_h.func_175717_a(p_180439_3_, "records." + ((ItemRecord)Item.func_150899_d(p_180439_4_)).field_150929_a);
         } else {
            this.field_72769_h.func_175717_a(p_180439_3_, (String)null);
         }
         break;
      case 1006:
         this.field_72769_h.func_175731_a(p_180439_3_, "random.door_close", 1.0F, this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1007:
         this.field_72769_h.func_175731_a(p_180439_3_, "mob.ghast.charge", 10.0F, (lvt_5_1_.nextFloat() - lvt_5_1_.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1008:
         this.field_72769_h.func_175731_a(p_180439_3_, "mob.ghast.fireball", 10.0F, (lvt_5_1_.nextFloat() - lvt_5_1_.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1009:
         this.field_72769_h.func_175731_a(p_180439_3_, "mob.ghast.fireball", 2.0F, (lvt_5_1_.nextFloat() - lvt_5_1_.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1010:
         this.field_72769_h.func_175731_a(p_180439_3_, "mob.zombie.wood", 2.0F, (lvt_5_1_.nextFloat() - lvt_5_1_.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1011:
         this.field_72769_h.func_175731_a(p_180439_3_, "mob.zombie.metal", 2.0F, (lvt_5_1_.nextFloat() - lvt_5_1_.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1012:
         this.field_72769_h.func_175731_a(p_180439_3_, "mob.zombie.woodbreak", 2.0F, (lvt_5_1_.nextFloat() - lvt_5_1_.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1014:
         this.field_72769_h.func_175731_a(p_180439_3_, "mob.wither.shoot", 2.0F, (lvt_5_1_.nextFloat() - lvt_5_1_.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1015:
         this.field_72769_h.func_175731_a(p_180439_3_, "mob.bat.takeoff", 0.05F, (lvt_5_1_.nextFloat() - lvt_5_1_.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1016:
         this.field_72769_h.func_175731_a(p_180439_3_, "mob.zombie.infect", 2.0F, (lvt_5_1_.nextFloat() - lvt_5_1_.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1017:
         this.field_72769_h.func_175731_a(p_180439_3_, "mob.zombie.unfect", 2.0F, (lvt_5_1_.nextFloat() - lvt_5_1_.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1020:
         this.field_72769_h.func_175731_a(p_180439_3_, "random.anvil_break", 1.0F, this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1021:
         this.field_72769_h.func_175731_a(p_180439_3_, "random.anvil_use", 1.0F, this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1022:
         this.field_72769_h.func_175731_a(p_180439_3_, "random.anvil_land", 0.3F, this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 2000:
         int lvt_7_1_ = p_180439_4_ % 3 - 1;
         int lvt_8_1_ = p_180439_4_ / 3 % 3 - 1;
         double lvt_9_1_ = (double)p_180439_3_.func_177958_n() + (double)lvt_7_1_ * 0.6D + 0.5D;
         double lvt_11_1_ = (double)p_180439_3_.func_177956_o() + 0.5D;
         double lvt_13_1_ = (double)p_180439_3_.func_177952_p() + (double)lvt_8_1_ * 0.6D + 0.5D;

         for(int lvt_15_1_ = 0; lvt_15_1_ < 10; ++lvt_15_1_) {
            double lvt_16_1_ = lvt_5_1_.nextDouble() * 0.2D + 0.01D;
            double lvt_18_1_ = lvt_9_1_ + (double)lvt_7_1_ * 0.01D + (lvt_5_1_.nextDouble() - 0.5D) * (double)lvt_8_1_ * 0.5D;
            double lvt_20_1_ = lvt_11_1_ + (lvt_5_1_.nextDouble() - 0.5D) * 0.5D;
            double lvt_22_1_ = lvt_13_1_ + (double)lvt_8_1_ * 0.01D + (lvt_5_1_.nextDouble() - 0.5D) * (double)lvt_7_1_ * 0.5D;
            double lvt_24_1_ = (double)lvt_7_1_ * lvt_16_1_ + lvt_5_1_.nextGaussian() * 0.01D;
            double lvt_26_1_ = -0.03D + lvt_5_1_.nextGaussian() * 0.01D;
            double lvt_28_1_ = (double)lvt_8_1_ * lvt_16_1_ + lvt_5_1_.nextGaussian() * 0.01D;
            this.func_174972_a(EnumParticleTypes.SMOKE_NORMAL, lvt_18_1_, lvt_20_1_, lvt_22_1_, lvt_24_1_, lvt_26_1_, lvt_28_1_, new int[0]);
         }

         return;
      case 2001:
         Block lvt_6_1_ = Block.func_149729_e(p_180439_4_ & 4095);
         if(lvt_6_1_.func_149688_o() != Material.field_151579_a) {
            this.field_72777_q.func_147118_V().func_147682_a(new PositionedSoundRecord(new ResourceLocation(lvt_6_1_.field_149762_H.func_150495_a()), (lvt_6_1_.field_149762_H.func_150497_c() + 1.0F) / 2.0F, lvt_6_1_.field_149762_H.func_150494_d() * 0.8F, (float)p_180439_3_.func_177958_n() + 0.5F, (float)p_180439_3_.func_177956_o() + 0.5F, (float)p_180439_3_.func_177952_p() + 0.5F));
         }

         this.field_72777_q.field_71452_i.func_180533_a(p_180439_3_, lvt_6_1_.func_176203_a(p_180439_4_ >> 12 & 255));
         break;
      case 2002:
         double lvt_7_3_ = (double)p_180439_3_.func_177958_n();
         double lvt_9_3_ = (double)p_180439_3_.func_177956_o();
         double lvt_11_3_ = (double)p_180439_3_.func_177952_p();

         for(int lvt_13_4_ = 0; lvt_13_4_ < 8; ++lvt_13_4_) {
            this.func_174972_a(EnumParticleTypes.ITEM_CRACK, lvt_7_3_, lvt_9_3_, lvt_11_3_, lvt_5_1_.nextGaussian() * 0.15D, lvt_5_1_.nextDouble() * 0.2D, lvt_5_1_.nextGaussian() * 0.15D, new int[]{Item.func_150891_b(Items.field_151068_bn), p_180439_4_});
         }

         int lvt_13_5_ = Items.field_151068_bn.func_77620_a(p_180439_4_);
         float lvt_14_1_ = (float)(lvt_13_5_ >> 16 & 255) / 255.0F;
         float lvt_15_2_ = (float)(lvt_13_5_ >> 8 & 255) / 255.0F;
         float lvt_16_2_ = (float)(lvt_13_5_ >> 0 & 255) / 255.0F;
         EnumParticleTypes lvt_17_1_ = EnumParticleTypes.SPELL;
         if(Items.field_151068_bn.func_77833_h(p_180439_4_)) {
            lvt_17_1_ = EnumParticleTypes.SPELL_INSTANT;
         }

         for(int lvt_18_2_ = 0; lvt_18_2_ < 100; ++lvt_18_2_) {
            double lvt_19_1_ = lvt_5_1_.nextDouble() * 4.0D;
            double lvt_21_1_ = lvt_5_1_.nextDouble() * 3.141592653589793D * 2.0D;
            double lvt_23_1_ = Math.cos(lvt_21_1_) * lvt_19_1_;
            double lvt_25_1_ = 0.01D + lvt_5_1_.nextDouble() * 0.5D;
            double lvt_27_1_ = Math.sin(lvt_21_1_) * lvt_19_1_;
            EntityFX lvt_29_1_ = this.func_174974_b(lvt_17_1_.func_179348_c(), lvt_17_1_.func_179344_e(), lvt_7_3_ + lvt_23_1_ * 0.1D, lvt_9_3_ + 0.3D, lvt_11_3_ + lvt_27_1_ * 0.1D, lvt_23_1_, lvt_25_1_, lvt_27_1_, new int[0]);
            if(lvt_29_1_ != null) {
               float lvt_30_1_ = 0.75F + lvt_5_1_.nextFloat() * 0.25F;
               lvt_29_1_.func_70538_b(lvt_14_1_ * lvt_30_1_, lvt_15_2_ * lvt_30_1_, lvt_16_2_ * lvt_30_1_);
               lvt_29_1_.func_70543_e((float)lvt_19_1_);
            }
         }

         this.field_72769_h.func_175731_a(p_180439_3_, "game.potion.smash", 1.0F, this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 2003:
         double lvt_7_2_ = (double)p_180439_3_.func_177958_n() + 0.5D;
         double lvt_9_2_ = (double)p_180439_3_.func_177956_o();
         double lvt_11_2_ = (double)p_180439_3_.func_177952_p() + 0.5D;

         for(int lvt_13_2_ = 0; lvt_13_2_ < 8; ++lvt_13_2_) {
            this.func_174972_a(EnumParticleTypes.ITEM_CRACK, lvt_7_2_, lvt_9_2_, lvt_11_2_, lvt_5_1_.nextGaussian() * 0.15D, lvt_5_1_.nextDouble() * 0.2D, lvt_5_1_.nextGaussian() * 0.15D, new int[]{Item.func_150891_b(Items.field_151061_bv)});
         }

         for(double lvt_13_3_ = 0.0D; lvt_13_3_ < 6.283185307179586D; lvt_13_3_ += 0.15707963267948966D) {
            this.func_174972_a(EnumParticleTypes.PORTAL, lvt_7_2_ + Math.cos(lvt_13_3_) * 5.0D, lvt_9_2_ - 0.4D, lvt_11_2_ + Math.sin(lvt_13_3_) * 5.0D, Math.cos(lvt_13_3_) * -5.0D, 0.0D, Math.sin(lvt_13_3_) * -5.0D, new int[0]);
            this.func_174972_a(EnumParticleTypes.PORTAL, lvt_7_2_ + Math.cos(lvt_13_3_) * 5.0D, lvt_9_2_ - 0.4D, lvt_11_2_ + Math.sin(lvt_13_3_) * 5.0D, Math.cos(lvt_13_3_) * -7.0D, 0.0D, Math.sin(lvt_13_3_) * -7.0D, new int[0]);
         }

         return;
      case 2004:
         for(int lvt_18_3_ = 0; lvt_18_3_ < 20; ++lvt_18_3_) {
            double lvt_19_2_ = (double)p_180439_3_.func_177958_n() + 0.5D + ((double)this.field_72769_h.field_73012_v.nextFloat() - 0.5D) * 2.0D;
            double lvt_21_2_ = (double)p_180439_3_.func_177956_o() + 0.5D + ((double)this.field_72769_h.field_73012_v.nextFloat() - 0.5D) * 2.0D;
            double lvt_23_2_ = (double)p_180439_3_.func_177952_p() + 0.5D + ((double)this.field_72769_h.field_73012_v.nextFloat() - 0.5D) * 2.0D;
            this.field_72769_h.func_175688_a(EnumParticleTypes.SMOKE_NORMAL, lvt_19_2_, lvt_21_2_, lvt_23_2_, 0.0D, 0.0D, 0.0D, new int[0]);
            this.field_72769_h.func_175688_a(EnumParticleTypes.FLAME, lvt_19_2_, lvt_21_2_, lvt_23_2_, 0.0D, 0.0D, 0.0D, new int[0]);
         }

         return;
      case 2005:
         ItemDye.func_180617_a(this.field_72769_h, p_180439_3_, p_180439_4_);
      }

   }

   public void func_180441_b(int p_180441_1_, BlockPos p_180441_2_, int p_180441_3_) {
      if(p_180441_3_ >= 0 && p_180441_3_ < 10) {
         DestroyBlockProgress lvt_4_1_ = (DestroyBlockProgress)this.field_72738_E.get(Integer.valueOf(p_180441_1_));
         if(lvt_4_1_ == null || lvt_4_1_.func_180246_b().func_177958_n() != p_180441_2_.func_177958_n() || lvt_4_1_.func_180246_b().func_177956_o() != p_180441_2_.func_177956_o() || lvt_4_1_.func_180246_b().func_177952_p() != p_180441_2_.func_177952_p()) {
            lvt_4_1_ = new DestroyBlockProgress(p_180441_1_, p_180441_2_);
            this.field_72738_E.put(Integer.valueOf(p_180441_1_), lvt_4_1_);
         }

         lvt_4_1_.func_73107_a(p_180441_3_);
         lvt_4_1_.func_82744_b(this.field_72773_u);
      } else {
         this.field_72738_E.remove(Integer.valueOf(p_180441_1_));
      }

   }

   public void func_174979_m() {
      this.field_147595_R = true;
   }

   public void func_181023_a(Collection<TileEntity> p_181023_1_, Collection<TileEntity> p_181023_2_) {
      synchronized(this.field_181024_n) {
         this.field_181024_n.removeAll(p_181023_1_);
         this.field_181024_n.addAll(p_181023_2_);
      }
   }

   class ContainerLocalRenderInformation {
      final RenderChunk field_178036_a;
      final EnumFacing field_178034_b;
      final Set<EnumFacing> field_178035_c;
      final int field_178032_d;

      private ContainerLocalRenderInformation(RenderChunk p_i46248_2_, EnumFacing p_i46248_3_, int p_i46248_4_) {
         this.field_178035_c = EnumSet.noneOf(EnumFacing.class);
         this.field_178036_a = p_i46248_2_;
         this.field_178034_b = p_i46248_3_;
         this.field_178032_d = p_i46248_4_;
      }
   }
}
