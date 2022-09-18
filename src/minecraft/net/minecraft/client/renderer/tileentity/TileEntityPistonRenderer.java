package net.minecraft.client.renderer.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class TileEntityPistonRenderer extends TileEntitySpecialRenderer<TileEntityPiston>
{
    private final BlockRendererDispatcher blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();

    public void renderTileEntityAt(TileEntityPiston te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        BlockPos lvt_10_1_ = te.getPos();
        IBlockState lvt_11_1_ = te.getPistonState();
        Block lvt_12_1_ = lvt_11_1_.getBlock();

        if (lvt_12_1_.getMaterial() != Material.air && te.getProgress(partialTicks) < 1.0F)
        {
            Tessellator lvt_13_1_ = Tessellator.getInstance();
            WorldRenderer lvt_14_1_ = lvt_13_1_.getWorldRenderer();
            this.bindTexture(TextureMap.locationBlocksTexture);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.enableBlend();
            GlStateManager.disableCull();

            if (Minecraft.isAmbientOcclusionEnabled())
            {
                GlStateManager.shadeModel(7425);
            }
            else
            {
                GlStateManager.shadeModel(7424);
            }

            lvt_14_1_.begin(7, DefaultVertexFormats.BLOCK);
            lvt_14_1_.setTranslation((double)((float)x - (float)lvt_10_1_.getX() + te.getOffsetX(partialTicks)), (double)((float)y - (float)lvt_10_1_.getY() + te.getOffsetY(partialTicks)), (double)((float)z - (float)lvt_10_1_.getZ() + te.getOffsetZ(partialTicks)));
            World lvt_15_1_ = this.getWorld();

            if (lvt_12_1_ == Blocks.piston_head && te.getProgress(partialTicks) < 0.5F)
            {
                lvt_11_1_ = lvt_11_1_.withProperty(BlockPistonExtension.SHORT, Boolean.valueOf(true));
                this.blockRenderer.getBlockModelRenderer().renderModel(lvt_15_1_, this.blockRenderer.getModelFromBlockState(lvt_11_1_, lvt_15_1_, lvt_10_1_), lvt_11_1_, lvt_10_1_, lvt_14_1_, true);
            }
            else if (te.shouldPistonHeadBeRendered() && !te.isExtending())
            {
                BlockPistonExtension.EnumPistonType lvt_16_1_ = lvt_12_1_ == Blocks.sticky_piston ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT;
                IBlockState lvt_17_1_ = Blocks.piston_head.getDefaultState().withProperty(BlockPistonExtension.TYPE, lvt_16_1_).withProperty(BlockPistonExtension.FACING, lvt_11_1_.getValue(BlockPistonBase.FACING));
                lvt_17_1_ = lvt_17_1_.withProperty(BlockPistonExtension.SHORT, Boolean.valueOf(te.getProgress(partialTicks) >= 0.5F));
                this.blockRenderer.getBlockModelRenderer().renderModel(lvt_15_1_, this.blockRenderer.getModelFromBlockState(lvt_17_1_, lvt_15_1_, lvt_10_1_), lvt_17_1_, lvt_10_1_, lvt_14_1_, true);
                lvt_14_1_.setTranslation((double)((float)x - (float)lvt_10_1_.getX()), (double)((float)y - (float)lvt_10_1_.getY()), (double)((float)z - (float)lvt_10_1_.getZ()));
                lvt_11_1_.withProperty(BlockPistonBase.EXTENDED, Boolean.valueOf(true));
                this.blockRenderer.getBlockModelRenderer().renderModel(lvt_15_1_, this.blockRenderer.getModelFromBlockState(lvt_11_1_, lvt_15_1_, lvt_10_1_), lvt_11_1_, lvt_10_1_, lvt_14_1_, true);
            }
            else
            {
                this.blockRenderer.getBlockModelRenderer().renderModel(lvt_15_1_, this.blockRenderer.getModelFromBlockState(lvt_11_1_, lvt_15_1_, lvt_10_1_), lvt_11_1_, lvt_10_1_, lvt_14_1_, false);
            }

            lvt_14_1_.setTranslation(0.0D, 0.0D, 0.0D);
            lvt_13_1_.draw();
            RenderHelper.enableStandardItemLighting();
        }
    }

    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        this.renderTileEntityAt((TileEntityPiston)te, x, y, z, partialTicks, destroyStage);
    }
}
