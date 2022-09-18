package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemModelMesher
{
    private final Map<Integer, ModelResourceLocation> simpleShapes = Maps.newHashMap();
    private final Map<Integer, IBakedModel> simpleShapesCache = Maps.newHashMap();
    private final Map<Item, ItemMeshDefinition> shapers = Maps.newHashMap();
    private final ModelManager modelManager;

    public ItemModelMesher(ModelManager modelManager)
    {
        this.modelManager = modelManager;
    }

    public TextureAtlasSprite getParticleIcon(Item item)
    {
        return this.getParticleIcon(item, 0);
    }

    public TextureAtlasSprite getParticleIcon(Item item, int meta)
    {
        return this.getItemModel(new ItemStack(item, 1, meta)).getParticleTexture();
    }

    public IBakedModel getItemModel(ItemStack stack)
    {
        Item lvt_2_1_ = stack.getItem();
        IBakedModel lvt_3_1_ = this.getItemModel(lvt_2_1_, this.getMetadata(stack));

        if (lvt_3_1_ == null)
        {
            ItemMeshDefinition lvt_4_1_ = (ItemMeshDefinition)this.shapers.get(lvt_2_1_);

            if (lvt_4_1_ != null)
            {
                lvt_3_1_ = this.modelManager.getModel(lvt_4_1_.getModelLocation(stack));
            }
        }

        if (lvt_3_1_ == null)
        {
            lvt_3_1_ = this.modelManager.getMissingModel();
        }

        return lvt_3_1_;
    }

    protected int getMetadata(ItemStack stack)
    {
        return stack.isItemStackDamageable() ? 0 : stack.getMetadata();
    }

    protected IBakedModel getItemModel(Item item, int meta)
    {
        return (IBakedModel)this.simpleShapesCache.get(Integer.valueOf(this.getIndex(item, meta)));
    }

    private int getIndex(Item item, int meta)
    {
        return Item.getIdFromItem(item) << 16 | meta;
    }

    public void register(Item item, int meta, ModelResourceLocation location)
    {
        this.simpleShapes.put(Integer.valueOf(this.getIndex(item, meta)), location);
        this.simpleShapesCache.put(Integer.valueOf(this.getIndex(item, meta)), this.modelManager.getModel(location));
    }

    public void register(Item item, ItemMeshDefinition definition)
    {
        this.shapers.put(item, definition);
    }

    public ModelManager getModelManager()
    {
        return this.modelManager;
    }

    public void rebuildCache()
    {
        this.simpleShapesCache.clear();

        for (Entry<Integer, ModelResourceLocation> lvt_2_1_ : this.simpleShapes.entrySet())
        {
            this.simpleShapesCache.put(lvt_2_1_.getKey(), this.modelManager.getModel((ModelResourceLocation)lvt_2_1_.getValue()));
        }
    }
}
