package net.minecraft.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RenderFallingBlock extends Render<EntityFallingBlock>
{
    public RenderFallingBlock(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
        this.shadowSize = 0.5F;
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityFallingBlock entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if (entity.getBlock() != null)
        {
            this.bindTexture(TextureMap.locationBlocksTexture);
            IBlockState lvt_10_1_ = entity.getBlock();
            Block lvt_11_1_ = lvt_10_1_.getBlock();
            BlockPos lvt_12_1_ = new BlockPos(entity);
            World lvt_13_1_ = entity.getWorldObj();

            if (lvt_10_1_ != lvt_13_1_.getBlockState(lvt_12_1_) && lvt_11_1_.getRenderType() != -1)
            {
                if (lvt_11_1_.getRenderType() == 3)
                {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate((float)x, (float)y, (float)z);
                    GlStateManager.disableLighting();
                    Tessellator lvt_14_1_ = Tessellator.getInstance();
                    WorldRenderer lvt_15_1_ = lvt_14_1_.getWorldRenderer();
                    lvt_15_1_.begin(7, DefaultVertexFormats.BLOCK);
                    int lvt_16_1_ = lvt_12_1_.getX();
                    int lvt_17_1_ = lvt_12_1_.getY();
                    int lvt_18_1_ = lvt_12_1_.getZ();
                    lvt_15_1_.setTranslation((double)((float)(-lvt_16_1_) - 0.5F), (double)(-lvt_17_1_), (double)((float)(-lvt_18_1_) - 0.5F));
                    BlockRendererDispatcher lvt_19_1_ = Minecraft.getMinecraft().getBlockRendererDispatcher();
                    IBakedModel lvt_20_1_ = lvt_19_1_.getModelFromBlockState(lvt_10_1_, lvt_13_1_, (BlockPos)null);
                    lvt_19_1_.getBlockModelRenderer().renderModel(lvt_13_1_, lvt_20_1_, lvt_10_1_, lvt_12_1_, lvt_15_1_, false);
                    lvt_15_1_.setTranslation(0.0D, 0.0D, 0.0D);
                    lvt_14_1_.draw();
                    GlStateManager.enableLighting();
                    GlStateManager.popMatrix();
                    super.doRender(entity, x, y, z, entityYaw, partialTicks);
                }
            }
        }
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityFallingBlock entity)
    {
        return TextureMap.locationBlocksTexture;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return this.getEntityTexture((EntityFallingBlock)entity);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.doRender((EntityFallingBlock)entity, x, y, z, entityYaw, partialTicks);
    }
}
