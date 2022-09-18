package net.minecraft.client.resources.model;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition;
import net.minecraft.client.renderer.texture.IIconCreator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.BuiltInModel;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IRegistry;
import net.minecraft.util.RegistrySimple;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModelBakery {
   private static final Set<ResourceLocation> field_177602_b = Sets.newHashSet(new ResourceLocation[]{new ResourceLocation("blocks/water_flow"), new ResourceLocation("blocks/water_still"), new ResourceLocation("blocks/lava_flow"), new ResourceLocation("blocks/lava_still"), new ResourceLocation("blocks/destroy_stage_0"), new ResourceLocation("blocks/destroy_stage_1"), new ResourceLocation("blocks/destroy_stage_2"), new ResourceLocation("blocks/destroy_stage_3"), new ResourceLocation("blocks/destroy_stage_4"), new ResourceLocation("blocks/destroy_stage_5"), new ResourceLocation("blocks/destroy_stage_6"), new ResourceLocation("blocks/destroy_stage_7"), new ResourceLocation("blocks/destroy_stage_8"), new ResourceLocation("blocks/destroy_stage_9"), new ResourceLocation("items/empty_armor_slot_helmet"), new ResourceLocation("items/empty_armor_slot_chestplate"), new ResourceLocation("items/empty_armor_slot_leggings"), new ResourceLocation("items/empty_armor_slot_boots")});
   private static final Logger field_177603_c = LogManager.getLogger();
   protected static final ModelResourceLocation field_177604_a = new ModelResourceLocation("builtin/missing", "missing");
   private static final Map<String, String> field_177600_d = Maps.newHashMap();
   private static final Joiner field_177601_e = Joiner.on(" -> ");
   private final IResourceManager field_177598_f;
   private final Map<ResourceLocation, TextureAtlasSprite> field_177599_g = Maps.newHashMap();
   private final Map<ResourceLocation, ModelBlock> field_177611_h = Maps.newLinkedHashMap();
   private final Map<ModelResourceLocation, ModelBlockDefinition.Variants> field_177612_i = Maps.newLinkedHashMap();
   private final TextureMap field_177609_j;
   private final BlockModelShapes field_177610_k;
   private final FaceBakery field_177607_l = new FaceBakery();
   private final ItemModelGenerator field_177608_m = new ItemModelGenerator();
   private RegistrySimple<ModelResourceLocation, IBakedModel> field_177605_n = new RegistrySimple();
   private static final ModelBlock field_177606_o = ModelBlock.func_178294_a("{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}");
   private static final ModelBlock field_177618_p = ModelBlock.func_178294_a("{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}");
   private static final ModelBlock field_177617_q = ModelBlock.func_178294_a("{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}");
   private static final ModelBlock field_177616_r = ModelBlock.func_178294_a("{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}");
   private Map<String, ResourceLocation> field_177615_s = Maps.newLinkedHashMap();
   private final Map<ResourceLocation, ModelBlockDefinition> field_177614_t = Maps.newHashMap();
   private Map<Item, List<String>> field_177613_u = Maps.newIdentityHashMap();

   public ModelBakery(IResourceManager p_i46085_1_, TextureMap p_i46085_2_, BlockModelShapes p_i46085_3_) {
      this.field_177598_f = p_i46085_1_;
      this.field_177609_j = p_i46085_2_;
      this.field_177610_k = p_i46085_3_;
   }

   public IRegistry<ModelResourceLocation, IBakedModel> func_177570_a() {
      this.func_177577_b();
      this.func_177597_h();
      this.func_177572_j();
      this.func_177593_l();
      this.func_177588_f();
      return this.field_177605_n;
   }

   private void func_177577_b() {
      this.func_177591_a(this.field_177610_k.func_178120_a().func_178446_a().values());
      this.field_177612_i.put(field_177604_a, new ModelBlockDefinition.Variants(field_177604_a.func_177518_c(), Lists.newArrayList(new ModelBlockDefinition.Variant[]{new ModelBlockDefinition.Variant(new ResourceLocation(field_177604_a.func_110623_a()), ModelRotation.X0_Y0, false, 1)})));
      ResourceLocation lvt_1_1_ = new ResourceLocation("item_frame");
      ModelBlockDefinition lvt_2_1_ = this.func_177586_a(lvt_1_1_);
      this.func_177569_a(lvt_2_1_, new ModelResourceLocation(lvt_1_1_, "normal"));
      this.func_177569_a(lvt_2_1_, new ModelResourceLocation(lvt_1_1_, "map"));
      this.func_177595_c();
      this.func_177590_d();
   }

   private void func_177591_a(Collection<ModelResourceLocation> p_177591_1_) {
      for(ModelResourceLocation lvt_3_1_ : p_177591_1_) {
         try {
            ModelBlockDefinition lvt_4_1_ = this.func_177586_a(lvt_3_1_);

            try {
               this.func_177569_a(lvt_4_1_, lvt_3_1_);
            } catch (Exception var6) {
               field_177603_c.warn("Unable to load variant: " + lvt_3_1_.func_177518_c() + " from " + lvt_3_1_);
            }
         } catch (Exception var7) {
            field_177603_c.warn("Unable to load definition " + lvt_3_1_, var7);
         }
      }

   }

   private void func_177569_a(ModelBlockDefinition p_177569_1_, ModelResourceLocation p_177569_2_) {
      this.field_177612_i.put(p_177569_2_, p_177569_1_.func_178330_b(p_177569_2_.func_177518_c()));
   }

   private ModelBlockDefinition func_177586_a(ResourceLocation p_177586_1_) {
      ResourceLocation lvt_2_1_ = this.func_177584_b(p_177586_1_);
      ModelBlockDefinition lvt_3_1_ = (ModelBlockDefinition)this.field_177614_t.get(lvt_2_1_);
      if(lvt_3_1_ == null) {
         List<ModelBlockDefinition> lvt_4_1_ = Lists.newArrayList();

         try {
            for(IResource lvt_6_1_ : this.field_177598_f.func_135056_b(lvt_2_1_)) {
               InputStream lvt_7_1_ = null;

               try {
                  lvt_7_1_ = lvt_6_1_.func_110527_b();
                  ModelBlockDefinition lvt_8_1_ = ModelBlockDefinition.func_178331_a(new InputStreamReader(lvt_7_1_, Charsets.UTF_8));
                  lvt_4_1_.add(lvt_8_1_);
               } catch (Exception var13) {
                  throw new RuntimeException("Encountered an exception when loading model definition of \'" + p_177586_1_ + "\' from: \'" + lvt_6_1_.func_177241_a() + "\' in resourcepack: \'" + lvt_6_1_.func_177240_d() + "\'", var13);
               } finally {
                  IOUtils.closeQuietly(lvt_7_1_);
               }
            }
         } catch (IOException var15) {
            throw new RuntimeException("Encountered an exception when loading model definition of model " + lvt_2_1_.toString(), var15);
         }

         lvt_3_1_ = new ModelBlockDefinition(lvt_4_1_);
         this.field_177614_t.put(lvt_2_1_, lvt_3_1_);
      }

      return lvt_3_1_;
   }

   private ResourceLocation func_177584_b(ResourceLocation p_177584_1_) {
      return new ResourceLocation(p_177584_1_.func_110624_b(), "blockstates/" + p_177584_1_.func_110623_a() + ".json");
   }

   private void func_177595_c() {
      for(ModelResourceLocation lvt_2_1_ : this.field_177612_i.keySet()) {
         for(ModelBlockDefinition.Variant lvt_4_1_ : ((ModelBlockDefinition.Variants)this.field_177612_i.get(lvt_2_1_)).func_178420_b()) {
            ResourceLocation lvt_5_1_ = lvt_4_1_.func_178431_a();
            if(this.field_177611_h.get(lvt_5_1_) == null) {
               try {
                  ModelBlock lvt_6_1_ = this.func_177594_c(lvt_5_1_);
                  this.field_177611_h.put(lvt_5_1_, lvt_6_1_);
               } catch (Exception var7) {
                  field_177603_c.warn("Unable to load block model: \'" + lvt_5_1_ + "\' for variant: \'" + lvt_2_1_ + "\'", var7);
               }
            }
         }
      }

   }

   private ModelBlock func_177594_c(ResourceLocation p_177594_1_) throws IOException {
      String lvt_3_1_ = p_177594_1_.func_110623_a();
      if("builtin/generated".equals(lvt_3_1_)) {
         return field_177606_o;
      } else if("builtin/compass".equals(lvt_3_1_)) {
         return field_177618_p;
      } else if("builtin/clock".equals(lvt_3_1_)) {
         return field_177617_q;
      } else if("builtin/entity".equals(lvt_3_1_)) {
         return field_177616_r;
      } else {
         Reader lvt_2_1_;
         if(lvt_3_1_.startsWith("builtin/")) {
            String lvt_4_1_ = lvt_3_1_.substring("builtin/".length());
            String lvt_5_1_ = (String)field_177600_d.get(lvt_4_1_);
            if(lvt_5_1_ == null) {
               throw new FileNotFoundException(p_177594_1_.toString());
            }

            lvt_2_1_ = new StringReader(lvt_5_1_);
         } else {
            IResource lvt_4_2_ = this.field_177598_f.func_110536_a(this.func_177580_d(p_177594_1_));
            lvt_2_1_ = new InputStreamReader(lvt_4_2_.func_110527_b(), Charsets.UTF_8);
         }

         ModelBlock var11;
         try {
            ModelBlock lvt_4_3_ = ModelBlock.func_178307_a(lvt_2_1_);
            lvt_4_3_.field_178317_b = p_177594_1_.toString();
            var11 = lvt_4_3_;
         } finally {
            lvt_2_1_.close();
         }

         return var11;
      }
   }

   private ResourceLocation func_177580_d(ResourceLocation p_177580_1_) {
      return new ResourceLocation(p_177580_1_.func_110624_b(), "models/" + p_177580_1_.func_110623_a() + ".json");
   }

   private void func_177590_d() {
      this.func_177592_e();

      for(Item lvt_2_1_ : Item.field_150901_e) {
         for(String lvt_5_1_ : this.func_177596_a(lvt_2_1_)) {
            ResourceLocation lvt_6_1_ = this.func_177583_a(lvt_5_1_);
            this.field_177615_s.put(lvt_5_1_, lvt_6_1_);
            if(this.field_177611_h.get(lvt_6_1_) == null) {
               try {
                  ModelBlock lvt_7_1_ = this.func_177594_c(lvt_6_1_);
                  this.field_177611_h.put(lvt_6_1_, lvt_7_1_);
               } catch (Exception var8) {
                  field_177603_c.warn("Unable to load item model: \'" + lvt_6_1_ + "\' for item: \'" + Item.field_150901_e.func_177774_c(lvt_2_1_) + "\'", var8);
               }
            }
         }
      }

   }

   private void func_177592_e() {
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150348_b), Lists.newArrayList(new String[]{"stone", "granite", "granite_smooth", "diorite", "diorite_smooth", "andesite", "andesite_smooth"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150346_d), Lists.newArrayList(new String[]{"dirt", "coarse_dirt", "podzol"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150344_f), Lists.newArrayList(new String[]{"oak_planks", "spruce_planks", "birch_planks", "jungle_planks", "acacia_planks", "dark_oak_planks"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150345_g), Lists.newArrayList(new String[]{"oak_sapling", "spruce_sapling", "birch_sapling", "jungle_sapling", "acacia_sapling", "dark_oak_sapling"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150354_m), Lists.newArrayList(new String[]{"sand", "red_sand"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150364_r), Lists.newArrayList(new String[]{"oak_log", "spruce_log", "birch_log", "jungle_log"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150362_t), Lists.newArrayList(new String[]{"oak_leaves", "spruce_leaves", "birch_leaves", "jungle_leaves"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150360_v), Lists.newArrayList(new String[]{"sponge", "sponge_wet"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150322_A), Lists.newArrayList(new String[]{"sandstone", "chiseled_sandstone", "smooth_sandstone"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_180395_cM), Lists.newArrayList(new String[]{"red_sandstone", "chiseled_red_sandstone", "smooth_red_sandstone"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150329_H), Lists.newArrayList(new String[]{"dead_bush", "tall_grass", "fern"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150330_I), Lists.newArrayList(new String[]{"dead_bush"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150325_L), Lists.newArrayList(new String[]{"black_wool", "red_wool", "green_wool", "brown_wool", "blue_wool", "purple_wool", "cyan_wool", "silver_wool", "gray_wool", "pink_wool", "lime_wool", "yellow_wool", "light_blue_wool", "magenta_wool", "orange_wool", "white_wool"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150327_N), Lists.newArrayList(new String[]{"dandelion"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150328_O), Lists.newArrayList(new String[]{"poppy", "blue_orchid", "allium", "houstonia", "red_tulip", "orange_tulip", "white_tulip", "pink_tulip", "oxeye_daisy"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150333_U), Lists.newArrayList(new String[]{"stone_slab", "sandstone_slab", "cobblestone_slab", "brick_slab", "stone_brick_slab", "nether_brick_slab", "quartz_slab"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_180389_cP), Lists.newArrayList(new String[]{"red_sandstone_slab"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150399_cn), Lists.newArrayList(new String[]{"black_stained_glass", "red_stained_glass", "green_stained_glass", "brown_stained_glass", "blue_stained_glass", "purple_stained_glass", "cyan_stained_glass", "silver_stained_glass", "gray_stained_glass", "pink_stained_glass", "lime_stained_glass", "yellow_stained_glass", "light_blue_stained_glass", "magenta_stained_glass", "orange_stained_glass", "white_stained_glass"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150418_aU), Lists.newArrayList(new String[]{"stone_monster_egg", "cobblestone_monster_egg", "stone_brick_monster_egg", "mossy_brick_monster_egg", "cracked_brick_monster_egg", "chiseled_brick_monster_egg"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150417_aV), Lists.newArrayList(new String[]{"stonebrick", "mossy_stonebrick", "cracked_stonebrick", "chiseled_stonebrick"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150376_bx), Lists.newArrayList(new String[]{"oak_slab", "spruce_slab", "birch_slab", "jungle_slab", "acacia_slab", "dark_oak_slab"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150463_bK), Lists.newArrayList(new String[]{"cobblestone_wall", "mossy_cobblestone_wall"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150467_bQ), Lists.newArrayList(new String[]{"anvil_intact", "anvil_slightly_damaged", "anvil_very_damaged"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150371_ca), Lists.newArrayList(new String[]{"quartz_block", "chiseled_quartz_block", "quartz_column"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150406_ce), Lists.newArrayList(new String[]{"black_stained_hardened_clay", "red_stained_hardened_clay", "green_stained_hardened_clay", "brown_stained_hardened_clay", "blue_stained_hardened_clay", "purple_stained_hardened_clay", "cyan_stained_hardened_clay", "silver_stained_hardened_clay", "gray_stained_hardened_clay", "pink_stained_hardened_clay", "lime_stained_hardened_clay", "yellow_stained_hardened_clay", "light_blue_stained_hardened_clay", "magenta_stained_hardened_clay", "orange_stained_hardened_clay", "white_stained_hardened_clay"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150397_co), Lists.newArrayList(new String[]{"black_stained_glass_pane", "red_stained_glass_pane", "green_stained_glass_pane", "brown_stained_glass_pane", "blue_stained_glass_pane", "purple_stained_glass_pane", "cyan_stained_glass_pane", "silver_stained_glass_pane", "gray_stained_glass_pane", "pink_stained_glass_pane", "lime_stained_glass_pane", "yellow_stained_glass_pane", "light_blue_stained_glass_pane", "magenta_stained_glass_pane", "orange_stained_glass_pane", "white_stained_glass_pane"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150361_u), Lists.newArrayList(new String[]{"acacia_leaves", "dark_oak_leaves"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150363_s), Lists.newArrayList(new String[]{"acacia_log", "dark_oak_log"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_180397_cI), Lists.newArrayList(new String[]{"prismarine", "prismarine_bricks", "dark_prismarine"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150404_cg), Lists.newArrayList(new String[]{"black_carpet", "red_carpet", "green_carpet", "brown_carpet", "blue_carpet", "purple_carpet", "cyan_carpet", "silver_carpet", "gray_carpet", "pink_carpet", "lime_carpet", "yellow_carpet", "light_blue_carpet", "magenta_carpet", "orange_carpet", "white_carpet"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_150398_cm), Lists.newArrayList(new String[]{"sunflower", "syringa", "double_grass", "double_fern", "double_rose", "paeonia"}));
      this.field_177613_u.put(Items.field_151031_f, Lists.newArrayList(new String[]{"bow", "bow_pulling_0", "bow_pulling_1", "bow_pulling_2"}));
      this.field_177613_u.put(Items.field_151044_h, Lists.newArrayList(new String[]{"coal", "charcoal"}));
      this.field_177613_u.put(Items.field_151112_aM, Lists.newArrayList(new String[]{"fishing_rod", "fishing_rod_cast"}));
      this.field_177613_u.put(Items.field_151115_aP, Lists.newArrayList(new String[]{"cod", "salmon", "clownfish", "pufferfish"}));
      this.field_177613_u.put(Items.field_179566_aV, Lists.newArrayList(new String[]{"cooked_cod", "cooked_salmon"}));
      this.field_177613_u.put(Items.field_151100_aR, Lists.newArrayList(new String[]{"dye_black", "dye_red", "dye_green", "dye_brown", "dye_blue", "dye_purple", "dye_cyan", "dye_silver", "dye_gray", "dye_pink", "dye_lime", "dye_yellow", "dye_light_blue", "dye_magenta", "dye_orange", "dye_white"}));
      this.field_177613_u.put(Items.field_151068_bn, Lists.newArrayList(new String[]{"bottle_drinkable", "bottle_splash"}));
      this.field_177613_u.put(Items.field_151144_bL, Lists.newArrayList(new String[]{"skull_skeleton", "skull_wither", "skull_zombie", "skull_char", "skull_creeper"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_180390_bo), Lists.newArrayList(new String[]{"oak_fence_gate"}));
      this.field_177613_u.put(Item.func_150898_a(Blocks.field_180407_aO), Lists.newArrayList(new String[]{"oak_fence"}));
      this.field_177613_u.put(Items.field_179570_aq, Lists.newArrayList(new String[]{"oak_door"}));
   }

   private List<String> func_177596_a(Item p_177596_1_) {
      List<String> lvt_2_1_ = (List)this.field_177613_u.get(p_177596_1_);
      if(lvt_2_1_ == null) {
         lvt_2_1_ = Collections.singletonList(((ResourceLocation)Item.field_150901_e.func_177774_c(p_177596_1_)).toString());
      }

      return lvt_2_1_;
   }

   private ResourceLocation func_177583_a(String p_177583_1_) {
      ResourceLocation lvt_2_1_ = new ResourceLocation(p_177583_1_);
      return new ResourceLocation(lvt_2_1_.func_110624_b(), "item/" + lvt_2_1_.func_110623_a());
   }

   private void func_177588_f() {
      for(ModelResourceLocation lvt_2_1_ : this.field_177612_i.keySet()) {
         WeightedBakedModel.Builder lvt_3_1_ = new WeightedBakedModel.Builder();
         int lvt_4_1_ = 0;

         for(ModelBlockDefinition.Variant lvt_6_1_ : ((ModelBlockDefinition.Variants)this.field_177612_i.get(lvt_2_1_)).func_178420_b()) {
            ModelBlock lvt_7_1_ = (ModelBlock)this.field_177611_h.get(lvt_6_1_.func_178431_a());
            if(lvt_7_1_ != null && lvt_7_1_.func_178303_d()) {
               ++lvt_4_1_;
               lvt_3_1_.func_177677_a(this.func_177578_a(lvt_7_1_, lvt_6_1_.func_178432_b(), lvt_6_1_.func_178433_c()), lvt_6_1_.func_178430_d());
            } else {
               field_177603_c.warn("Missing model for: " + lvt_2_1_);
            }
         }

         if(lvt_4_1_ == 0) {
            field_177603_c.warn("No weighted models for: " + lvt_2_1_);
         } else if(lvt_4_1_ == 1) {
            this.field_177605_n.func_82595_a(lvt_2_1_, lvt_3_1_.func_177675_b());
         } else {
            this.field_177605_n.func_82595_a(lvt_2_1_, lvt_3_1_.func_177676_a());
         }
      }

      for(Entry<String, ResourceLocation> lvt_2_2_ : this.field_177615_s.entrySet()) {
         ResourceLocation lvt_3_2_ = (ResourceLocation)lvt_2_2_.getValue();
         ModelResourceLocation lvt_4_2_ = new ModelResourceLocation((String)lvt_2_2_.getKey(), "inventory");
         ModelBlock lvt_5_2_ = (ModelBlock)this.field_177611_h.get(lvt_3_2_);
         if(lvt_5_2_ != null && lvt_5_2_.func_178303_d()) {
            if(this.func_177587_c(lvt_5_2_)) {
               this.field_177605_n.func_82595_a(lvt_4_2_, new BuiltInModel(lvt_5_2_.func_181682_g()));
            } else {
               this.field_177605_n.func_82595_a(lvt_4_2_, this.func_177578_a(lvt_5_2_, ModelRotation.X0_Y0, false));
            }
         } else {
            field_177603_c.warn("Missing model for: " + lvt_3_2_);
         }
      }

   }

   private Set<ResourceLocation> func_177575_g() {
      Set<ResourceLocation> lvt_1_1_ = Sets.newHashSet();
      List<ModelResourceLocation> lvt_2_1_ = Lists.newArrayList(this.field_177612_i.keySet());
      Collections.sort(lvt_2_1_, new Comparator<ModelResourceLocation>() {
         public int compare(ModelResourceLocation p_compare_1_, ModelResourceLocation p_compare_2_) {
            return p_compare_1_.toString().compareTo(p_compare_2_.toString());
         }

         // $FF: synthetic method
         public int compare(Object p_compare_1_, Object p_compare_2_) {
            return this.compare((ModelResourceLocation)p_compare_1_, (ModelResourceLocation)p_compare_2_);
         }
      });

      for(ModelResourceLocation lvt_4_1_ : lvt_2_1_) {
         ModelBlockDefinition.Variants lvt_5_1_ = (ModelBlockDefinition.Variants)this.field_177612_i.get(lvt_4_1_);

         for(ModelBlockDefinition.Variant lvt_7_1_ : lvt_5_1_.func_178420_b()) {
            ModelBlock lvt_8_1_ = (ModelBlock)this.field_177611_h.get(lvt_7_1_.func_178431_a());
            if(lvt_8_1_ == null) {
               field_177603_c.warn("Missing model for: " + lvt_4_1_);
            } else {
               lvt_1_1_.addAll(this.func_177585_a(lvt_8_1_));
            }
         }
      }

      lvt_1_1_.addAll(field_177602_b);
      return lvt_1_1_;
   }

   private IBakedModel func_177578_a(ModelBlock p_177578_1_, ModelRotation p_177578_2_, boolean p_177578_3_) {
      TextureAtlasSprite lvt_4_1_ = (TextureAtlasSprite)this.field_177599_g.get(new ResourceLocation(p_177578_1_.func_178308_c("particle")));
      SimpleBakedModel.Builder lvt_5_1_ = (new SimpleBakedModel.Builder(p_177578_1_)).func_177646_a(lvt_4_1_);

      for(BlockPart lvt_7_1_ : p_177578_1_.func_178298_a()) {
         for(EnumFacing lvt_9_1_ : lvt_7_1_.field_178240_c.keySet()) {
            BlockPartFace lvt_10_1_ = (BlockPartFace)lvt_7_1_.field_178240_c.get(lvt_9_1_);
            TextureAtlasSprite lvt_11_1_ = (TextureAtlasSprite)this.field_177599_g.get(new ResourceLocation(p_177578_1_.func_178308_c(lvt_10_1_.field_178242_d)));
            if(lvt_10_1_.field_178244_b == null) {
               lvt_5_1_.func_177648_a(this.func_177589_a(lvt_7_1_, lvt_10_1_, lvt_11_1_, lvt_9_1_, p_177578_2_, p_177578_3_));
            } else {
               lvt_5_1_.func_177650_a(p_177578_2_.func_177523_a(lvt_10_1_.field_178244_b), this.func_177589_a(lvt_7_1_, lvt_10_1_, lvt_11_1_, lvt_9_1_, p_177578_2_, p_177578_3_));
            }
         }
      }

      return lvt_5_1_.func_177645_b();
   }

   private BakedQuad func_177589_a(BlockPart p_177589_1_, BlockPartFace p_177589_2_, TextureAtlasSprite p_177589_3_, EnumFacing p_177589_4_, ModelRotation p_177589_5_, boolean p_177589_6_) {
      return this.field_177607_l.func_178414_a(p_177589_1_.field_178241_a, p_177589_1_.field_178239_b, p_177589_2_, p_177589_3_, p_177589_4_, p_177589_5_, p_177589_1_.field_178237_d, p_177589_6_, p_177589_1_.field_178238_e);
   }

   private void func_177597_h() {
      this.func_177574_i();

      for(ModelBlock lvt_2_1_ : this.field_177611_h.values()) {
         lvt_2_1_.func_178299_a(this.field_177611_h);
      }

      ModelBlock.func_178312_b(this.field_177611_h);
   }

   private void func_177574_i() {
      Deque<ResourceLocation> lvt_1_1_ = Queues.newArrayDeque();
      Set<ResourceLocation> lvt_2_1_ = Sets.newHashSet();

      for(ResourceLocation lvt_4_1_ : this.field_177611_h.keySet()) {
         lvt_2_1_.add(lvt_4_1_);
         ResourceLocation lvt_5_1_ = ((ModelBlock)this.field_177611_h.get(lvt_4_1_)).func_178305_e();
         if(lvt_5_1_ != null) {
            lvt_1_1_.add(lvt_5_1_);
         }
      }

      while(!((Deque)lvt_1_1_).isEmpty()) {
         ResourceLocation lvt_3_2_ = (ResourceLocation)lvt_1_1_.pop();

         try {
            if(this.field_177611_h.get(lvt_3_2_) != null) {
               continue;
            }

            ModelBlock lvt_4_2_ = this.func_177594_c(lvt_3_2_);
            this.field_177611_h.put(lvt_3_2_, lvt_4_2_);
            ResourceLocation lvt_5_2_ = lvt_4_2_.func_178305_e();
            if(lvt_5_2_ != null && !lvt_2_1_.contains(lvt_5_2_)) {
               lvt_1_1_.add(lvt_5_2_);
            }
         } catch (Exception var6) {
            field_177603_c.warn("In parent chain: " + field_177601_e.join(this.func_177573_e(lvt_3_2_)) + "; unable to load model: \'" + lvt_3_2_ + "\'", var6);
         }

         lvt_2_1_.add(lvt_3_2_);
      }

   }

   private List<ResourceLocation> func_177573_e(ResourceLocation p_177573_1_) {
      List<ResourceLocation> lvt_2_1_ = Lists.newArrayList(new ResourceLocation[]{p_177573_1_});
      ResourceLocation lvt_3_1_ = p_177573_1_;

      while((lvt_3_1_ = this.func_177576_f(lvt_3_1_)) != null) {
         lvt_2_1_.add(0, lvt_3_1_);
      }

      return lvt_2_1_;
   }

   private ResourceLocation func_177576_f(ResourceLocation p_177576_1_) {
      for(Entry<ResourceLocation, ModelBlock> lvt_3_1_ : this.field_177611_h.entrySet()) {
         ModelBlock lvt_4_1_ = (ModelBlock)lvt_3_1_.getValue();
         if(lvt_4_1_ != null && p_177576_1_.equals(lvt_4_1_.func_178305_e())) {
            return (ResourceLocation)lvt_3_1_.getKey();
         }
      }

      return null;
   }

   private Set<ResourceLocation> func_177585_a(ModelBlock p_177585_1_) {
      Set<ResourceLocation> lvt_2_1_ = Sets.newHashSet();

      for(BlockPart lvt_4_1_ : p_177585_1_.func_178298_a()) {
         for(BlockPartFace lvt_6_1_ : lvt_4_1_.field_178240_c.values()) {
            ResourceLocation lvt_7_1_ = new ResourceLocation(p_177585_1_.func_178308_c(lvt_6_1_.field_178242_d));
            lvt_2_1_.add(lvt_7_1_);
         }
      }

      lvt_2_1_.add(new ResourceLocation(p_177585_1_.func_178308_c("particle")));
      return lvt_2_1_;
   }

   private void func_177572_j() {
      final Set<ResourceLocation> lvt_1_1_ = this.func_177575_g();
      lvt_1_1_.addAll(this.func_177571_k());
      lvt_1_1_.remove(TextureMap.field_174945_f);
      IIconCreator lvt_2_1_ = new IIconCreator() {
         public void func_177059_a(TextureMap p_177059_1_) {
            for(ResourceLocation lvt_3_1_ : lvt_1_1_) {
               TextureAtlasSprite lvt_4_1_ = p_177059_1_.func_174942_a(lvt_3_1_);
               ModelBakery.this.field_177599_g.put(lvt_3_1_, lvt_4_1_);
            }

         }
      };
      this.field_177609_j.func_174943_a(this.field_177598_f, lvt_2_1_);
      this.field_177599_g.put(new ResourceLocation("missingno"), this.field_177609_j.func_174944_f());
   }

   private Set<ResourceLocation> func_177571_k() {
      Set<ResourceLocation> lvt_1_1_ = Sets.newHashSet();

      for(ResourceLocation lvt_3_1_ : this.field_177615_s.values()) {
         ModelBlock lvt_4_1_ = (ModelBlock)this.field_177611_h.get(lvt_3_1_);
         if(lvt_4_1_ != null) {
            lvt_1_1_.add(new ResourceLocation(lvt_4_1_.func_178308_c("particle")));
            if(this.func_177581_b(lvt_4_1_)) {
               for(String lvt_6_1_ : ItemModelGenerator.field_178398_a) {
                  ResourceLocation lvt_7_1_ = new ResourceLocation(lvt_4_1_.func_178308_c(lvt_6_1_));
                  if(lvt_4_1_.func_178310_f() == field_177618_p && !TextureMap.field_174945_f.equals(lvt_7_1_)) {
                     TextureAtlasSprite.func_176603_b(lvt_7_1_.toString());
                  } else if(lvt_4_1_.func_178310_f() == field_177617_q && !TextureMap.field_174945_f.equals(lvt_7_1_)) {
                     TextureAtlasSprite.func_176602_a(lvt_7_1_.toString());
                  }

                  lvt_1_1_.add(lvt_7_1_);
               }
            } else if(!this.func_177587_c(lvt_4_1_)) {
               for(BlockPart lvt_6_2_ : lvt_4_1_.func_178298_a()) {
                  for(BlockPartFace lvt_8_1_ : lvt_6_2_.field_178240_c.values()) {
                     ResourceLocation lvt_9_1_ = new ResourceLocation(lvt_4_1_.func_178308_c(lvt_8_1_.field_178242_d));
                     lvt_1_1_.add(lvt_9_1_);
                  }
               }
            }
         }
      }

      return lvt_1_1_;
   }

   private boolean func_177581_b(ModelBlock p_177581_1_) {
      if(p_177581_1_ == null) {
         return false;
      } else {
         ModelBlock lvt_2_1_ = p_177581_1_.func_178310_f();
         return lvt_2_1_ == field_177606_o || lvt_2_1_ == field_177618_p || lvt_2_1_ == field_177617_q;
      }
   }

   private boolean func_177587_c(ModelBlock p_177587_1_) {
      if(p_177587_1_ == null) {
         return false;
      } else {
         ModelBlock lvt_2_1_ = p_177587_1_.func_178310_f();
         return lvt_2_1_ == field_177616_r;
      }
   }

   private void func_177593_l() {
      for(ResourceLocation lvt_2_1_ : this.field_177615_s.values()) {
         ModelBlock lvt_3_1_ = (ModelBlock)this.field_177611_h.get(lvt_2_1_);
         if(this.func_177581_b(lvt_3_1_)) {
            ModelBlock lvt_4_1_ = this.func_177582_d(lvt_3_1_);
            if(lvt_4_1_ != null) {
               lvt_4_1_.field_178317_b = lvt_2_1_.toString();
            }

            this.field_177611_h.put(lvt_2_1_, lvt_4_1_);
         } else if(this.func_177587_c(lvt_3_1_)) {
            this.field_177611_h.put(lvt_2_1_, lvt_3_1_);
         }
      }

      for(TextureAtlasSprite lvt_2_2_ : this.field_177599_g.values()) {
         if(!lvt_2_2_.func_130098_m()) {
            lvt_2_2_.func_130103_l();
         }
      }

   }

   private ModelBlock func_177582_d(ModelBlock p_177582_1_) {
      return this.field_177608_m.func_178392_a(this.field_177609_j, p_177582_1_);
   }

   static {
      field_177600_d.put("missing", "{ \"textures\": {   \"particle\": \"missingno\",   \"missingno\": \"missingno\"}, \"elements\": [ {     \"from\": [ 0, 0, 0 ],     \"to\": [ 16, 16, 16 ],     \"faces\": {         \"down\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"down\", \"texture\": \"#missingno\" },         \"up\":    { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"up\", \"texture\": \"#missingno\" },         \"north\": { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"north\", \"texture\": \"#missingno\" },         \"south\": { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"south\", \"texture\": \"#missingno\" },         \"west\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"west\", \"texture\": \"#missingno\" },         \"east\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"east\", \"texture\": \"#missingno\" }    }}]}");
      field_177606_o.field_178317_b = "generation marker";
      field_177618_p.field_178317_b = "compass generation marker";
      field_177617_q.field_178317_b = "class generation marker";
      field_177616_r.field_178317_b = "block entity marker";
   }
}
