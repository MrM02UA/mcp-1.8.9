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

public class ModelBakery
{
    private static final Set<ResourceLocation> LOCATIONS_BUILTIN_TEXTURES = Sets.newHashSet(new ResourceLocation[] {new ResourceLocation("blocks/water_flow"), new ResourceLocation("blocks/water_still"), new ResourceLocation("blocks/lava_flow"), new ResourceLocation("blocks/lava_still"), new ResourceLocation("blocks/destroy_stage_0"), new ResourceLocation("blocks/destroy_stage_1"), new ResourceLocation("blocks/destroy_stage_2"), new ResourceLocation("blocks/destroy_stage_3"), new ResourceLocation("blocks/destroy_stage_4"), new ResourceLocation("blocks/destroy_stage_5"), new ResourceLocation("blocks/destroy_stage_6"), new ResourceLocation("blocks/destroy_stage_7"), new ResourceLocation("blocks/destroy_stage_8"), new ResourceLocation("blocks/destroy_stage_9"), new ResourceLocation("items/empty_armor_slot_helmet"), new ResourceLocation("items/empty_armor_slot_chestplate"), new ResourceLocation("items/empty_armor_slot_leggings"), new ResourceLocation("items/empty_armor_slot_boots")});
    private static final Logger LOGGER = LogManager.getLogger();
    protected static final ModelResourceLocation MODEL_MISSING = new ModelResourceLocation("builtin/missing", "missing");
    private static final Map<String, String> BUILT_IN_MODELS = Maps.newHashMap();
    private static final Joiner JOINER = Joiner.on(" -> ");
    private final IResourceManager resourceManager;
    private final Map<ResourceLocation, TextureAtlasSprite> sprites = Maps.newHashMap();
    private final Map<ResourceLocation, ModelBlock> models = Maps.newLinkedHashMap();
    private final Map<ModelResourceLocation, ModelBlockDefinition.Variants> variants = Maps.newLinkedHashMap();
    private final TextureMap textureMap;
    private final BlockModelShapes blockModelShapes;
    private final FaceBakery faceBakery = new FaceBakery();
    private final ItemModelGenerator itemModelGenerator = new ItemModelGenerator();
    private RegistrySimple<ModelResourceLocation, IBakedModel> bakedRegistry = new RegistrySimple();
    private static final ModelBlock MODEL_GENERATED = ModelBlock.deserialize("{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}");
    private static final ModelBlock MODEL_COMPASS = ModelBlock.deserialize("{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}");
    private static final ModelBlock MODEL_CLOCK = ModelBlock.deserialize("{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}");
    private static final ModelBlock MODEL_ENTITY = ModelBlock.deserialize("{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}");
    private Map<String, ResourceLocation> itemLocations = Maps.newLinkedHashMap();
    private final Map<ResourceLocation, ModelBlockDefinition> blockDefinitions = Maps.newHashMap();
    private Map<Item, List<String>> variantNames = Maps.newIdentityHashMap();

    public ModelBakery(IResourceManager p_i46085_1_, TextureMap p_i46085_2_, BlockModelShapes p_i46085_3_)
    {
        this.resourceManager = p_i46085_1_;
        this.textureMap = p_i46085_2_;
        this.blockModelShapes = p_i46085_3_;
    }

    public IRegistry<ModelResourceLocation, IBakedModel> setupModelRegistry()
    {
        this.loadVariantItemModels();
        this.loadModelsCheck();
        this.loadSprites();
        this.bakeItemModels();
        this.bakeBlockModels();
        return this.bakedRegistry;
    }

    private void loadVariantItemModels()
    {
        this.loadVariants(this.blockModelShapes.getBlockStateMapper().putAllStateModelLocations().values());
        this.variants.put(MODEL_MISSING, new ModelBlockDefinition.Variants(MODEL_MISSING.getVariant(), Lists.newArrayList(new ModelBlockDefinition.Variant[] {new ModelBlockDefinition.Variant(new ResourceLocation(MODEL_MISSING.getResourcePath()), ModelRotation.X0_Y0, false, 1)})));
        ResourceLocation lvt_1_1_ = new ResourceLocation("item_frame");
        ModelBlockDefinition lvt_2_1_ = this.getModelBlockDefinition(lvt_1_1_);
        this.registerVariant(lvt_2_1_, new ModelResourceLocation(lvt_1_1_, "normal"));
        this.registerVariant(lvt_2_1_, new ModelResourceLocation(lvt_1_1_, "map"));
        this.loadVariantModels();
        this.loadItemModels();
    }

    private void loadVariants(Collection<ModelResourceLocation> p_177591_1_)
    {
        for (ModelResourceLocation lvt_3_1_ : p_177591_1_)
        {
            try
            {
                ModelBlockDefinition lvt_4_1_ = this.getModelBlockDefinition(lvt_3_1_);

                try
                {
                    this.registerVariant(lvt_4_1_, lvt_3_1_);
                }
                catch (Exception var6)
                {
                    LOGGER.warn("Unable to load variant: " + lvt_3_1_.getVariant() + " from " + lvt_3_1_);
                }
            }
            catch (Exception var7)
            {
                LOGGER.warn("Unable to load definition " + lvt_3_1_, var7);
            }
        }
    }

    private void registerVariant(ModelBlockDefinition p_177569_1_, ModelResourceLocation p_177569_2_)
    {
        this.variants.put(p_177569_2_, p_177569_1_.getVariants(p_177569_2_.getVariant()));
    }

    private ModelBlockDefinition getModelBlockDefinition(ResourceLocation p_177586_1_)
    {
        ResourceLocation lvt_2_1_ = this.getBlockStateLocation(p_177586_1_);
        ModelBlockDefinition lvt_3_1_ = (ModelBlockDefinition)this.blockDefinitions.get(lvt_2_1_);

        if (lvt_3_1_ == null)
        {
            List<ModelBlockDefinition> lvt_4_1_ = Lists.newArrayList();

            try
            {
                for (IResource lvt_6_1_ : this.resourceManager.getAllResources(lvt_2_1_))
                {
                    InputStream lvt_7_1_ = null;

                    try
                    {
                        lvt_7_1_ = lvt_6_1_.getInputStream();
                        ModelBlockDefinition lvt_8_1_ = ModelBlockDefinition.parseFromReader(new InputStreamReader(lvt_7_1_, Charsets.UTF_8));
                        lvt_4_1_.add(lvt_8_1_);
                    }
                    catch (Exception var13)
                    {
                        throw new RuntimeException("Encountered an exception when loading model definition of \'" + p_177586_1_ + "\' from: \'" + lvt_6_1_.getResourceLocation() + "\' in resourcepack: \'" + lvt_6_1_.getResourcePackName() + "\'", var13);
                    }
                    finally
                    {
                        IOUtils.closeQuietly(lvt_7_1_);
                    }
                }
            }
            catch (IOException var15)
            {
                throw new RuntimeException("Encountered an exception when loading model definition of model " + lvt_2_1_.toString(), var15);
            }

            lvt_3_1_ = new ModelBlockDefinition(lvt_4_1_);
            this.blockDefinitions.put(lvt_2_1_, lvt_3_1_);
        }

        return lvt_3_1_;
    }

    private ResourceLocation getBlockStateLocation(ResourceLocation p_177584_1_)
    {
        return new ResourceLocation(p_177584_1_.getResourceDomain(), "blockstates/" + p_177584_1_.getResourcePath() + ".json");
    }

    private void loadVariantModels()
    {
        for (ModelResourceLocation lvt_2_1_ : this.variants.keySet())
        {
            for (ModelBlockDefinition.Variant lvt_4_1_ : ((ModelBlockDefinition.Variants)this.variants.get(lvt_2_1_)).getVariants())
            {
                ResourceLocation lvt_5_1_ = lvt_4_1_.getModelLocation();

                if (this.models.get(lvt_5_1_) == null)
                {
                    try
                    {
                        ModelBlock lvt_6_1_ = this.loadModel(lvt_5_1_);
                        this.models.put(lvt_5_1_, lvt_6_1_);
                    }
                    catch (Exception var7)
                    {
                        LOGGER.warn("Unable to load block model: \'" + lvt_5_1_ + "\' for variant: \'" + lvt_2_1_ + "\'", var7);
                    }
                }
            }
        }
    }

    private ModelBlock loadModel(ResourceLocation p_177594_1_) throws IOException
    {
        String lvt_3_1_ = p_177594_1_.getResourcePath();

        if ("builtin/generated".equals(lvt_3_1_))
        {
            return MODEL_GENERATED;
        }
        else if ("builtin/compass".equals(lvt_3_1_))
        {
            return MODEL_COMPASS;
        }
        else if ("builtin/clock".equals(lvt_3_1_))
        {
            return MODEL_CLOCK;
        }
        else if ("builtin/entity".equals(lvt_3_1_))
        {
            return MODEL_ENTITY;
        }
        else
        {
            Reader lvt_2_1_;

            if (lvt_3_1_.startsWith("builtin/"))
            {
                String lvt_4_1_ = lvt_3_1_.substring("builtin/".length());
                String lvt_5_1_ = (String)BUILT_IN_MODELS.get(lvt_4_1_);

                if (lvt_5_1_ == null)
                {
                    throw new FileNotFoundException(p_177594_1_.toString());
                }

                lvt_2_1_ = new StringReader(lvt_5_1_);
            }
            else
            {
                IResource lvt_4_2_ = this.resourceManager.getResource(this.getModelLocation(p_177594_1_));
                lvt_2_1_ = new InputStreamReader(lvt_4_2_.getInputStream(), Charsets.UTF_8);
            }

            ModelBlock var11;

            try
            {
                ModelBlock lvt_4_3_ = ModelBlock.deserialize(lvt_2_1_);
                lvt_4_3_.name = p_177594_1_.toString();
                var11 = lvt_4_3_;
            }
            finally
            {
                lvt_2_1_.close();
            }

            return var11;
        }
    }

    private ResourceLocation getModelLocation(ResourceLocation p_177580_1_)
    {
        return new ResourceLocation(p_177580_1_.getResourceDomain(), "models/" + p_177580_1_.getResourcePath() + ".json");
    }

    private void loadItemModels()
    {
        this.registerVariantNames();

        for (Item lvt_2_1_ : Item.itemRegistry)
        {
            for (String lvt_5_1_ : this.getVariantNames(lvt_2_1_))
            {
                ResourceLocation lvt_6_1_ = this.getItemLocation(lvt_5_1_);
                this.itemLocations.put(lvt_5_1_, lvt_6_1_);

                if (this.models.get(lvt_6_1_) == null)
                {
                    try
                    {
                        ModelBlock lvt_7_1_ = this.loadModel(lvt_6_1_);
                        this.models.put(lvt_6_1_, lvt_7_1_);
                    }
                    catch (Exception var8)
                    {
                        LOGGER.warn("Unable to load item model: \'" + lvt_6_1_ + "\' for item: \'" + Item.itemRegistry.getNameForObject(lvt_2_1_) + "\'", var8);
                    }
                }
            }
        }
    }

    private void registerVariantNames()
    {
        this.variantNames.put(Item.getItemFromBlock(Blocks.stone), Lists.newArrayList(new String[] {"stone", "granite", "granite_smooth", "diorite", "diorite_smooth", "andesite", "andesite_smooth"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.dirt), Lists.newArrayList(new String[] {"dirt", "coarse_dirt", "podzol"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.planks), Lists.newArrayList(new String[] {"oak_planks", "spruce_planks", "birch_planks", "jungle_planks", "acacia_planks", "dark_oak_planks"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.sapling), Lists.newArrayList(new String[] {"oak_sapling", "spruce_sapling", "birch_sapling", "jungle_sapling", "acacia_sapling", "dark_oak_sapling"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.sand), Lists.newArrayList(new String[] {"sand", "red_sand"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.log), Lists.newArrayList(new String[] {"oak_log", "spruce_log", "birch_log", "jungle_log"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.leaves), Lists.newArrayList(new String[] {"oak_leaves", "spruce_leaves", "birch_leaves", "jungle_leaves"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.sponge), Lists.newArrayList(new String[] {"sponge", "sponge_wet"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.sandstone), Lists.newArrayList(new String[] {"sandstone", "chiseled_sandstone", "smooth_sandstone"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.red_sandstone), Lists.newArrayList(new String[] {"red_sandstone", "chiseled_red_sandstone", "smooth_red_sandstone"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.tallgrass), Lists.newArrayList(new String[] {"dead_bush", "tall_grass", "fern"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.deadbush), Lists.newArrayList(new String[] {"dead_bush"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.wool), Lists.newArrayList(new String[] {"black_wool", "red_wool", "green_wool", "brown_wool", "blue_wool", "purple_wool", "cyan_wool", "silver_wool", "gray_wool", "pink_wool", "lime_wool", "yellow_wool", "light_blue_wool", "magenta_wool", "orange_wool", "white_wool"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.yellow_flower), Lists.newArrayList(new String[] {"dandelion"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.red_flower), Lists.newArrayList(new String[] {"poppy", "blue_orchid", "allium", "houstonia", "red_tulip", "orange_tulip", "white_tulip", "pink_tulip", "oxeye_daisy"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.stone_slab), Lists.newArrayList(new String[] {"stone_slab", "sandstone_slab", "cobblestone_slab", "brick_slab", "stone_brick_slab", "nether_brick_slab", "quartz_slab"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.stone_slab2), Lists.newArrayList(new String[] {"red_sandstone_slab"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.stained_glass), Lists.newArrayList(new String[] {"black_stained_glass", "red_stained_glass", "green_stained_glass", "brown_stained_glass", "blue_stained_glass", "purple_stained_glass", "cyan_stained_glass", "silver_stained_glass", "gray_stained_glass", "pink_stained_glass", "lime_stained_glass", "yellow_stained_glass", "light_blue_stained_glass", "magenta_stained_glass", "orange_stained_glass", "white_stained_glass"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.monster_egg), Lists.newArrayList(new String[] {"stone_monster_egg", "cobblestone_monster_egg", "stone_brick_monster_egg", "mossy_brick_monster_egg", "cracked_brick_monster_egg", "chiseled_brick_monster_egg"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.stonebrick), Lists.newArrayList(new String[] {"stonebrick", "mossy_stonebrick", "cracked_stonebrick", "chiseled_stonebrick"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.wooden_slab), Lists.newArrayList(new String[] {"oak_slab", "spruce_slab", "birch_slab", "jungle_slab", "acacia_slab", "dark_oak_slab"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.cobblestone_wall), Lists.newArrayList(new String[] {"cobblestone_wall", "mossy_cobblestone_wall"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.anvil), Lists.newArrayList(new String[] {"anvil_intact", "anvil_slightly_damaged", "anvil_very_damaged"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.quartz_block), Lists.newArrayList(new String[] {"quartz_block", "chiseled_quartz_block", "quartz_column"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.stained_hardened_clay), Lists.newArrayList(new String[] {"black_stained_hardened_clay", "red_stained_hardened_clay", "green_stained_hardened_clay", "brown_stained_hardened_clay", "blue_stained_hardened_clay", "purple_stained_hardened_clay", "cyan_stained_hardened_clay", "silver_stained_hardened_clay", "gray_stained_hardened_clay", "pink_stained_hardened_clay", "lime_stained_hardened_clay", "yellow_stained_hardened_clay", "light_blue_stained_hardened_clay", "magenta_stained_hardened_clay", "orange_stained_hardened_clay", "white_stained_hardened_clay"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.stained_glass_pane), Lists.newArrayList(new String[] {"black_stained_glass_pane", "red_stained_glass_pane", "green_stained_glass_pane", "brown_stained_glass_pane", "blue_stained_glass_pane", "purple_stained_glass_pane", "cyan_stained_glass_pane", "silver_stained_glass_pane", "gray_stained_glass_pane", "pink_stained_glass_pane", "lime_stained_glass_pane", "yellow_stained_glass_pane", "light_blue_stained_glass_pane", "magenta_stained_glass_pane", "orange_stained_glass_pane", "white_stained_glass_pane"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.leaves2), Lists.newArrayList(new String[] {"acacia_leaves", "dark_oak_leaves"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.log2), Lists.newArrayList(new String[] {"acacia_log", "dark_oak_log"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.prismarine), Lists.newArrayList(new String[] {"prismarine", "prismarine_bricks", "dark_prismarine"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.carpet), Lists.newArrayList(new String[] {"black_carpet", "red_carpet", "green_carpet", "brown_carpet", "blue_carpet", "purple_carpet", "cyan_carpet", "silver_carpet", "gray_carpet", "pink_carpet", "lime_carpet", "yellow_carpet", "light_blue_carpet", "magenta_carpet", "orange_carpet", "white_carpet"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.double_plant), Lists.newArrayList(new String[] {"sunflower", "syringa", "double_grass", "double_fern", "double_rose", "paeonia"}));
        this.variantNames.put(Items.bow, Lists.newArrayList(new String[] {"bow", "bow_pulling_0", "bow_pulling_1", "bow_pulling_2"}));
        this.variantNames.put(Items.coal, Lists.newArrayList(new String[] {"coal", "charcoal"}));
        this.variantNames.put(Items.fishing_rod, Lists.newArrayList(new String[] {"fishing_rod", "fishing_rod_cast"}));
        this.variantNames.put(Items.fish, Lists.newArrayList(new String[] {"cod", "salmon", "clownfish", "pufferfish"}));
        this.variantNames.put(Items.cooked_fish, Lists.newArrayList(new String[] {"cooked_cod", "cooked_salmon"}));
        this.variantNames.put(Items.dye, Lists.newArrayList(new String[] {"dye_black", "dye_red", "dye_green", "dye_brown", "dye_blue", "dye_purple", "dye_cyan", "dye_silver", "dye_gray", "dye_pink", "dye_lime", "dye_yellow", "dye_light_blue", "dye_magenta", "dye_orange", "dye_white"}));
        this.variantNames.put(Items.potionitem, Lists.newArrayList(new String[] {"bottle_drinkable", "bottle_splash"}));
        this.variantNames.put(Items.skull, Lists.newArrayList(new String[] {"skull_skeleton", "skull_wither", "skull_zombie", "skull_char", "skull_creeper"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.oak_fence_gate), Lists.newArrayList(new String[] {"oak_fence_gate"}));
        this.variantNames.put(Item.getItemFromBlock(Blocks.oak_fence), Lists.newArrayList(new String[] {"oak_fence"}));
        this.variantNames.put(Items.oak_door, Lists.newArrayList(new String[] {"oak_door"}));
    }

    private List<String> getVariantNames(Item p_177596_1_)
    {
        List<String> lvt_2_1_ = (List)this.variantNames.get(p_177596_1_);

        if (lvt_2_1_ == null)
        {
            lvt_2_1_ = Collections.singletonList(((ResourceLocation)Item.itemRegistry.getNameForObject(p_177596_1_)).toString());
        }

        return lvt_2_1_;
    }

    private ResourceLocation getItemLocation(String p_177583_1_)
    {
        ResourceLocation lvt_2_1_ = new ResourceLocation(p_177583_1_);
        return new ResourceLocation(lvt_2_1_.getResourceDomain(), "item/" + lvt_2_1_.getResourcePath());
    }

    private void bakeBlockModels()
    {
        for (ModelResourceLocation lvt_2_1_ : this.variants.keySet())
        {
            WeightedBakedModel.Builder lvt_3_1_ = new WeightedBakedModel.Builder();
            int lvt_4_1_ = 0;

            for (ModelBlockDefinition.Variant lvt_6_1_ : ((ModelBlockDefinition.Variants)this.variants.get(lvt_2_1_)).getVariants())
            {
                ModelBlock lvt_7_1_ = (ModelBlock)this.models.get(lvt_6_1_.getModelLocation());

                if (lvt_7_1_ != null && lvt_7_1_.isResolved())
                {
                    ++lvt_4_1_;
                    lvt_3_1_.add(this.bakeModel(lvt_7_1_, lvt_6_1_.getRotation(), lvt_6_1_.isUvLocked()), lvt_6_1_.getWeight());
                }
                else
                {
                    LOGGER.warn("Missing model for: " + lvt_2_1_);
                }
            }

            if (lvt_4_1_ == 0)
            {
                LOGGER.warn("No weighted models for: " + lvt_2_1_);
            }
            else if (lvt_4_1_ == 1)
            {
                this.bakedRegistry.putObject(lvt_2_1_, lvt_3_1_.first());
            }
            else
            {
                this.bakedRegistry.putObject(lvt_2_1_, lvt_3_1_.build());
            }
        }

        for (Entry<String, ResourceLocation> lvt_2_2_ : this.itemLocations.entrySet())
        {
            ResourceLocation lvt_3_2_ = (ResourceLocation)lvt_2_2_.getValue();
            ModelResourceLocation lvt_4_2_ = new ModelResourceLocation((String)lvt_2_2_.getKey(), "inventory");
            ModelBlock lvt_5_2_ = (ModelBlock)this.models.get(lvt_3_2_);

            if (lvt_5_2_ != null && lvt_5_2_.isResolved())
            {
                if (this.isCustomRenderer(lvt_5_2_))
                {
                    this.bakedRegistry.putObject(lvt_4_2_, new BuiltInModel(lvt_5_2_.getAllTransforms()));
                }
                else
                {
                    this.bakedRegistry.putObject(lvt_4_2_, this.bakeModel(lvt_5_2_, ModelRotation.X0_Y0, false));
                }
            }
            else
            {
                LOGGER.warn("Missing model for: " + lvt_3_2_);
            }
        }
    }

    private Set<ResourceLocation> getVariantsTextureLocations()
    {
        Set<ResourceLocation> lvt_1_1_ = Sets.newHashSet();
        List<ModelResourceLocation> lvt_2_1_ = Lists.newArrayList(this.variants.keySet());
        Collections.sort(lvt_2_1_, new Comparator<ModelResourceLocation>()
        {
            public int compare(ModelResourceLocation p_compare_1_, ModelResourceLocation p_compare_2_)
            {
                return p_compare_1_.toString().compareTo(p_compare_2_.toString());
            }
            public int compare(Object p_compare_1_, Object p_compare_2_)
            {
                return this.compare((ModelResourceLocation)p_compare_1_, (ModelResourceLocation)p_compare_2_);
            }
        });

        for (ModelResourceLocation lvt_4_1_ : lvt_2_1_)
        {
            ModelBlockDefinition.Variants lvt_5_1_ = (ModelBlockDefinition.Variants)this.variants.get(lvt_4_1_);

            for (ModelBlockDefinition.Variant lvt_7_1_ : lvt_5_1_.getVariants())
            {
                ModelBlock lvt_8_1_ = (ModelBlock)this.models.get(lvt_7_1_.getModelLocation());

                if (lvt_8_1_ == null)
                {
                    LOGGER.warn("Missing model for: " + lvt_4_1_);
                }
                else
                {
                    lvt_1_1_.addAll(this.getTextureLocations(lvt_8_1_));
                }
            }
        }

        lvt_1_1_.addAll(LOCATIONS_BUILTIN_TEXTURES);
        return lvt_1_1_;
    }

    private IBakedModel bakeModel(ModelBlock modelBlockIn, ModelRotation modelRotationIn, boolean uvLocked)
    {
        TextureAtlasSprite lvt_4_1_ = (TextureAtlasSprite)this.sprites.get(new ResourceLocation(modelBlockIn.resolveTextureName("particle")));
        SimpleBakedModel.Builder lvt_5_1_ = (new SimpleBakedModel.Builder(modelBlockIn)).setTexture(lvt_4_1_);

        for (BlockPart lvt_7_1_ : modelBlockIn.getElements())
        {
            for (EnumFacing lvt_9_1_ : lvt_7_1_.mapFaces.keySet())
            {
                BlockPartFace lvt_10_1_ = (BlockPartFace)lvt_7_1_.mapFaces.get(lvt_9_1_);
                TextureAtlasSprite lvt_11_1_ = (TextureAtlasSprite)this.sprites.get(new ResourceLocation(modelBlockIn.resolveTextureName(lvt_10_1_.texture)));

                if (lvt_10_1_.cullFace == null)
                {
                    lvt_5_1_.addGeneralQuad(this.makeBakedQuad(lvt_7_1_, lvt_10_1_, lvt_11_1_, lvt_9_1_, modelRotationIn, uvLocked));
                }
                else
                {
                    lvt_5_1_.addFaceQuad(modelRotationIn.rotateFace(lvt_10_1_.cullFace), this.makeBakedQuad(lvt_7_1_, lvt_10_1_, lvt_11_1_, lvt_9_1_, modelRotationIn, uvLocked));
                }
            }
        }

        return lvt_5_1_.makeBakedModel();
    }

    private BakedQuad makeBakedQuad(BlockPart p_177589_1_, BlockPartFace p_177589_2_, TextureAtlasSprite p_177589_3_, EnumFacing p_177589_4_, ModelRotation p_177589_5_, boolean p_177589_6_)
    {
        return this.faceBakery.makeBakedQuad(p_177589_1_.positionFrom, p_177589_1_.positionTo, p_177589_2_, p_177589_3_, p_177589_4_, p_177589_5_, p_177589_1_.partRotation, p_177589_6_, p_177589_1_.shade);
    }

    private void loadModelsCheck()
    {
        this.loadModels();

        for (ModelBlock lvt_2_1_ : this.models.values())
        {
            lvt_2_1_.getParentFromMap(this.models);
        }

        ModelBlock.checkModelHierarchy(this.models);
    }

    private void loadModels()
    {
        Deque<ResourceLocation> lvt_1_1_ = Queues.newArrayDeque();
        Set<ResourceLocation> lvt_2_1_ = Sets.newHashSet();

        for (ResourceLocation lvt_4_1_ : this.models.keySet())
        {
            lvt_2_1_.add(lvt_4_1_);
            ResourceLocation lvt_5_1_ = ((ModelBlock)this.models.get(lvt_4_1_)).getParentLocation();

            if (lvt_5_1_ != null)
            {
                lvt_1_1_.add(lvt_5_1_);
            }
        }

        while (!((Deque)lvt_1_1_).isEmpty())
        {
            ResourceLocation lvt_3_2_ = (ResourceLocation)lvt_1_1_.pop();

            try
            {
                if (this.models.get(lvt_3_2_) != null)
                {
                    continue;
                }

                ModelBlock lvt_4_2_ = this.loadModel(lvt_3_2_);
                this.models.put(lvt_3_2_, lvt_4_2_);
                ResourceLocation lvt_5_2_ = lvt_4_2_.getParentLocation();

                if (lvt_5_2_ != null && !lvt_2_1_.contains(lvt_5_2_))
                {
                    lvt_1_1_.add(lvt_5_2_);
                }
            }
            catch (Exception var6)
            {
                LOGGER.warn("In parent chain: " + JOINER.join(this.getParentPath(lvt_3_2_)) + "; unable to load model: \'" + lvt_3_2_ + "\'", var6);
            }

            lvt_2_1_.add(lvt_3_2_);
        }
    }

    private List<ResourceLocation> getParentPath(ResourceLocation p_177573_1_)
    {
        List<ResourceLocation> lvt_2_1_ = Lists.newArrayList(new ResourceLocation[] {p_177573_1_});
        ResourceLocation lvt_3_1_ = p_177573_1_;

        while ((lvt_3_1_ = this.getParentLocation(lvt_3_1_)) != null)
        {
            lvt_2_1_.add(0, lvt_3_1_);
        }

        return lvt_2_1_;
    }

    private ResourceLocation getParentLocation(ResourceLocation p_177576_1_)
    {
        for (Entry<ResourceLocation, ModelBlock> lvt_3_1_ : this.models.entrySet())
        {
            ModelBlock lvt_4_1_ = (ModelBlock)lvt_3_1_.getValue();

            if (lvt_4_1_ != null && p_177576_1_.equals(lvt_4_1_.getParentLocation()))
            {
                return (ResourceLocation)lvt_3_1_.getKey();
            }
        }

        return null;
    }

    private Set<ResourceLocation> getTextureLocations(ModelBlock p_177585_1_)
    {
        Set<ResourceLocation> lvt_2_1_ = Sets.newHashSet();

        for (BlockPart lvt_4_1_ : p_177585_1_.getElements())
        {
            for (BlockPartFace lvt_6_1_ : lvt_4_1_.mapFaces.values())
            {
                ResourceLocation lvt_7_1_ = new ResourceLocation(p_177585_1_.resolveTextureName(lvt_6_1_.texture));
                lvt_2_1_.add(lvt_7_1_);
            }
        }

        lvt_2_1_.add(new ResourceLocation(p_177585_1_.resolveTextureName("particle")));
        return lvt_2_1_;
    }

    private void loadSprites()
    {
        final Set<ResourceLocation> lvt_1_1_ = this.getVariantsTextureLocations();
        lvt_1_1_.addAll(this.getItemsTextureLocations());
        lvt_1_1_.remove(TextureMap.LOCATION_MISSING_TEXTURE);
        IIconCreator lvt_2_1_ = new IIconCreator()
        {
            public void registerSprites(TextureMap iconRegistry)
            {
                for (ResourceLocation lvt_3_1_ : lvt_1_1_)
                {
                    TextureAtlasSprite lvt_4_1_ = iconRegistry.registerSprite(lvt_3_1_);
                    ModelBakery.this.sprites.put(lvt_3_1_, lvt_4_1_);
                }
            }
        };
        this.textureMap.loadSprites(this.resourceManager, lvt_2_1_);
        this.sprites.put(new ResourceLocation("missingno"), this.textureMap.getMissingSprite());
    }

    private Set<ResourceLocation> getItemsTextureLocations()
    {
        Set<ResourceLocation> lvt_1_1_ = Sets.newHashSet();

        for (ResourceLocation lvt_3_1_ : this.itemLocations.values())
        {
            ModelBlock lvt_4_1_ = (ModelBlock)this.models.get(lvt_3_1_);

            if (lvt_4_1_ != null)
            {
                lvt_1_1_.add(new ResourceLocation(lvt_4_1_.resolveTextureName("particle")));

                if (this.hasItemModel(lvt_4_1_))
                {
                    for (String lvt_6_1_ : ItemModelGenerator.LAYERS)
                    {
                        ResourceLocation lvt_7_1_ = new ResourceLocation(lvt_4_1_.resolveTextureName(lvt_6_1_));

                        if (lvt_4_1_.getRootModel() == MODEL_COMPASS && !TextureMap.LOCATION_MISSING_TEXTURE.equals(lvt_7_1_))
                        {
                            TextureAtlasSprite.setLocationNameCompass(lvt_7_1_.toString());
                        }
                        else if (lvt_4_1_.getRootModel() == MODEL_CLOCK && !TextureMap.LOCATION_MISSING_TEXTURE.equals(lvt_7_1_))
                        {
                            TextureAtlasSprite.setLocationNameClock(lvt_7_1_.toString());
                        }

                        lvt_1_1_.add(lvt_7_1_);
                    }
                }
                else if (!this.isCustomRenderer(lvt_4_1_))
                {
                    for (BlockPart lvt_6_2_ : lvt_4_1_.getElements())
                    {
                        for (BlockPartFace lvt_8_1_ : lvt_6_2_.mapFaces.values())
                        {
                            ResourceLocation lvt_9_1_ = new ResourceLocation(lvt_4_1_.resolveTextureName(lvt_8_1_.texture));
                            lvt_1_1_.add(lvt_9_1_);
                        }
                    }
                }
            }
        }

        return lvt_1_1_;
    }

    private boolean hasItemModel(ModelBlock p_177581_1_)
    {
        if (p_177581_1_ == null)
        {
            return false;
        }
        else
        {
            ModelBlock lvt_2_1_ = p_177581_1_.getRootModel();
            return lvt_2_1_ == MODEL_GENERATED || lvt_2_1_ == MODEL_COMPASS || lvt_2_1_ == MODEL_CLOCK;
        }
    }

    private boolean isCustomRenderer(ModelBlock p_177587_1_)
    {
        if (p_177587_1_ == null)
        {
            return false;
        }
        else
        {
            ModelBlock lvt_2_1_ = p_177587_1_.getRootModel();
            return lvt_2_1_ == MODEL_ENTITY;
        }
    }

    private void bakeItemModels()
    {
        for (ResourceLocation lvt_2_1_ : this.itemLocations.values())
        {
            ModelBlock lvt_3_1_ = (ModelBlock)this.models.get(lvt_2_1_);

            if (this.hasItemModel(lvt_3_1_))
            {
                ModelBlock lvt_4_1_ = this.makeItemModel(lvt_3_1_);

                if (lvt_4_1_ != null)
                {
                    lvt_4_1_.name = lvt_2_1_.toString();
                }

                this.models.put(lvt_2_1_, lvt_4_1_);
            }
            else if (this.isCustomRenderer(lvt_3_1_))
            {
                this.models.put(lvt_2_1_, lvt_3_1_);
            }
        }

        for (TextureAtlasSprite lvt_2_2_ : this.sprites.values())
        {
            if (!lvt_2_2_.hasAnimationMetadata())
            {
                lvt_2_2_.clearFramesTextureData();
            }
        }
    }

    private ModelBlock makeItemModel(ModelBlock p_177582_1_)
    {
        return this.itemModelGenerator.makeItemModel(this.textureMap, p_177582_1_);
    }

    static
    {
        BUILT_IN_MODELS.put("missing", "{ \"textures\": {   \"particle\": \"missingno\",   \"missingno\": \"missingno\"}, \"elements\": [ {     \"from\": [ 0, 0, 0 ],     \"to\": [ 16, 16, 16 ],     \"faces\": {         \"down\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"down\", \"texture\": \"#missingno\" },         \"up\":    { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"up\", \"texture\": \"#missingno\" },         \"north\": { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"north\", \"texture\": \"#missingno\" },         \"south\": { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"south\", \"texture\": \"#missingno\" },         \"west\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"west\", \"texture\": \"#missingno\" },         \"east\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"east\", \"texture\": \"#missingno\" }    }}]}");
        MODEL_GENERATED.name = "generation marker";
        MODEL_COMPASS.name = "compass generation marker";
        MODEL_CLOCK.name = "class generation marker";
        MODEL_ENTITY.name = "block entity marker";
    }
}
