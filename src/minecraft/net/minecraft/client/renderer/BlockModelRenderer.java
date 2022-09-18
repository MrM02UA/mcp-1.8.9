package net.minecraft.client.renderer;

import java.util.BitSet;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3i;
import net.minecraft.world.IBlockAccess;

public class BlockModelRenderer
{
    public boolean renderModel(IBlockAccess blockAccessIn, IBakedModel modelIn, IBlockState blockStateIn, BlockPos blockPosIn, WorldRenderer worldRendererIn)
    {
        Block lvt_6_1_ = blockStateIn.getBlock();
        lvt_6_1_.setBlockBoundsBasedOnState(blockAccessIn, blockPosIn);
        return this.renderModel(blockAccessIn, modelIn, blockStateIn, blockPosIn, worldRendererIn, true);
    }

    public boolean renderModel(IBlockAccess blockAccessIn, IBakedModel modelIn, IBlockState blockStateIn, BlockPos blockPosIn, WorldRenderer worldRendererIn, boolean checkSides)
    {
        boolean lvt_7_1_ = Minecraft.isAmbientOcclusionEnabled() && blockStateIn.getBlock().getLightValue() == 0 && modelIn.isAmbientOcclusion();

        try
        {
            Block lvt_8_1_ = blockStateIn.getBlock();
            return lvt_7_1_ ? this.renderModelAmbientOcclusion(blockAccessIn, modelIn, lvt_8_1_, blockPosIn, worldRendererIn, checkSides) : this.renderModelStandard(blockAccessIn, modelIn, lvt_8_1_, blockPosIn, worldRendererIn, checkSides);
        }
        catch (Throwable var11)
        {
            CrashReport lvt_9_1_ = CrashReport.makeCrashReport(var11, "Tesselating block model");
            CrashReportCategory lvt_10_1_ = lvt_9_1_.makeCategory("Block model being tesselated");
            CrashReportCategory.addBlockInfo(lvt_10_1_, blockPosIn, blockStateIn);
            lvt_10_1_.addCrashSection("Using AO", Boolean.valueOf(lvt_7_1_));
            throw new ReportedException(lvt_9_1_);
        }
    }

    public boolean renderModelAmbientOcclusion(IBlockAccess blockAccessIn, IBakedModel modelIn, Block blockIn, BlockPos blockPosIn, WorldRenderer worldRendererIn, boolean checkSides)
    {
        boolean lvt_7_1_ = false;
        float[] lvt_8_1_ = new float[EnumFacing.values().length * 2];
        BitSet lvt_9_1_ = new BitSet(3);
        BlockModelRenderer.AmbientOcclusionFace lvt_10_1_ = new BlockModelRenderer.AmbientOcclusionFace();

        for (EnumFacing lvt_14_1_ : EnumFacing.values())
        {
            List<BakedQuad> lvt_15_1_ = modelIn.getFaceQuads(lvt_14_1_);

            if (!lvt_15_1_.isEmpty())
            {
                BlockPos lvt_16_1_ = blockPosIn.offset(lvt_14_1_);

                if (!checkSides || blockIn.shouldSideBeRendered(blockAccessIn, lvt_16_1_, lvt_14_1_))
                {
                    this.renderModelAmbientOcclusionQuads(blockAccessIn, blockIn, blockPosIn, worldRendererIn, lvt_15_1_, lvt_8_1_, lvt_9_1_, lvt_10_1_);
                    lvt_7_1_ = true;
                }
            }
        }

        List<BakedQuad> lvt_11_2_ = modelIn.getGeneralQuads();

        if (lvt_11_2_.size() > 0)
        {
            this.renderModelAmbientOcclusionQuads(blockAccessIn, blockIn, blockPosIn, worldRendererIn, lvt_11_2_, lvt_8_1_, lvt_9_1_, lvt_10_1_);
            lvt_7_1_ = true;
        }

        return lvt_7_1_;
    }

    public boolean renderModelStandard(IBlockAccess blockAccessIn, IBakedModel modelIn, Block blockIn, BlockPos blockPosIn, WorldRenderer worldRendererIn, boolean checkSides)
    {
        boolean lvt_7_1_ = false;
        BitSet lvt_8_1_ = new BitSet(3);

        for (EnumFacing lvt_12_1_ : EnumFacing.values())
        {
            List<BakedQuad> lvt_13_1_ = modelIn.getFaceQuads(lvt_12_1_);

            if (!lvt_13_1_.isEmpty())
            {
                BlockPos lvt_14_1_ = blockPosIn.offset(lvt_12_1_);

                if (!checkSides || blockIn.shouldSideBeRendered(blockAccessIn, lvt_14_1_, lvt_12_1_))
                {
                    int lvt_15_1_ = blockIn.getMixedBrightnessForBlock(blockAccessIn, lvt_14_1_);
                    this.renderModelStandardQuads(blockAccessIn, blockIn, blockPosIn, lvt_12_1_, lvt_15_1_, false, worldRendererIn, lvt_13_1_, lvt_8_1_);
                    lvt_7_1_ = true;
                }
            }
        }

        List<BakedQuad> lvt_9_2_ = modelIn.getGeneralQuads();

        if (lvt_9_2_.size() > 0)
        {
            this.renderModelStandardQuads(blockAccessIn, blockIn, blockPosIn, (EnumFacing)null, -1, true, worldRendererIn, lvt_9_2_, lvt_8_1_);
            lvt_7_1_ = true;
        }

        return lvt_7_1_;
    }

    private void renderModelAmbientOcclusionQuads(IBlockAccess blockAccessIn, Block blockIn, BlockPos blockPosIn, WorldRenderer worldRendererIn, List<BakedQuad> listQuadsIn, float[] quadBounds, BitSet boundsFlags, BlockModelRenderer.AmbientOcclusionFace aoFaceIn)
    {
        double lvt_9_1_ = (double)blockPosIn.getX();
        double lvt_11_1_ = (double)blockPosIn.getY();
        double lvt_13_1_ = (double)blockPosIn.getZ();
        Block.EnumOffsetType lvt_15_1_ = blockIn.getOffsetType();

        if (lvt_15_1_ != Block.EnumOffsetType.NONE)
        {
            long lvt_16_1_ = MathHelper.getPositionRandom(blockPosIn);
            lvt_9_1_ += ((double)((float)(lvt_16_1_ >> 16 & 15L) / 15.0F) - 0.5D) * 0.5D;
            lvt_13_1_ += ((double)((float)(lvt_16_1_ >> 24 & 15L) / 15.0F) - 0.5D) * 0.5D;

            if (lvt_15_1_ == Block.EnumOffsetType.XYZ)
            {
                lvt_11_1_ += ((double)((float)(lvt_16_1_ >> 20 & 15L) / 15.0F) - 1.0D) * 0.2D;
            }
        }

        for (BakedQuad lvt_17_1_ : listQuadsIn)
        {
            this.fillQuadBounds(blockIn, lvt_17_1_.getVertexData(), lvt_17_1_.getFace(), quadBounds, boundsFlags);
            aoFaceIn.updateVertexBrightness(blockAccessIn, blockIn, blockPosIn, lvt_17_1_.getFace(), quadBounds, boundsFlags);
            worldRendererIn.addVertexData(lvt_17_1_.getVertexData());
            worldRendererIn.putBrightness4(aoFaceIn.vertexBrightness[0], aoFaceIn.vertexBrightness[1], aoFaceIn.vertexBrightness[2], aoFaceIn.vertexBrightness[3]);

            if (lvt_17_1_.hasTintIndex())
            {
                int lvt_18_1_ = blockIn.colorMultiplier(blockAccessIn, blockPosIn, lvt_17_1_.getTintIndex());

                if (EntityRenderer.anaglyphEnable)
                {
                    lvt_18_1_ = TextureUtil.anaglyphColor(lvt_18_1_);
                }

                float lvt_19_1_ = (float)(lvt_18_1_ >> 16 & 255) / 255.0F;
                float lvt_20_1_ = (float)(lvt_18_1_ >> 8 & 255) / 255.0F;
                float lvt_21_1_ = (float)(lvt_18_1_ & 255) / 255.0F;
                worldRendererIn.putColorMultiplier(aoFaceIn.vertexColorMultiplier[0] * lvt_19_1_, aoFaceIn.vertexColorMultiplier[0] * lvt_20_1_, aoFaceIn.vertexColorMultiplier[0] * lvt_21_1_, 4);
                worldRendererIn.putColorMultiplier(aoFaceIn.vertexColorMultiplier[1] * lvt_19_1_, aoFaceIn.vertexColorMultiplier[1] * lvt_20_1_, aoFaceIn.vertexColorMultiplier[1] * lvt_21_1_, 3);
                worldRendererIn.putColorMultiplier(aoFaceIn.vertexColorMultiplier[2] * lvt_19_1_, aoFaceIn.vertexColorMultiplier[2] * lvt_20_1_, aoFaceIn.vertexColorMultiplier[2] * lvt_21_1_, 2);
                worldRendererIn.putColorMultiplier(aoFaceIn.vertexColorMultiplier[3] * lvt_19_1_, aoFaceIn.vertexColorMultiplier[3] * lvt_20_1_, aoFaceIn.vertexColorMultiplier[3] * lvt_21_1_, 1);
            }
            else
            {
                worldRendererIn.putColorMultiplier(aoFaceIn.vertexColorMultiplier[0], aoFaceIn.vertexColorMultiplier[0], aoFaceIn.vertexColorMultiplier[0], 4);
                worldRendererIn.putColorMultiplier(aoFaceIn.vertexColorMultiplier[1], aoFaceIn.vertexColorMultiplier[1], aoFaceIn.vertexColorMultiplier[1], 3);
                worldRendererIn.putColorMultiplier(aoFaceIn.vertexColorMultiplier[2], aoFaceIn.vertexColorMultiplier[2], aoFaceIn.vertexColorMultiplier[2], 2);
                worldRendererIn.putColorMultiplier(aoFaceIn.vertexColorMultiplier[3], aoFaceIn.vertexColorMultiplier[3], aoFaceIn.vertexColorMultiplier[3], 1);
            }

            worldRendererIn.putPosition(lvt_9_1_, lvt_11_1_, lvt_13_1_);
        }
    }

    private void fillQuadBounds(Block blockIn, int[] vertexData, EnumFacing facingIn, float[] quadBounds, BitSet boundsFlags)
    {
        float lvt_6_1_ = 32.0F;
        float lvt_7_1_ = 32.0F;
        float lvt_8_1_ = 32.0F;
        float lvt_9_1_ = -32.0F;
        float lvt_10_1_ = -32.0F;
        float lvt_11_1_ = -32.0F;

        for (int lvt_12_1_ = 0; lvt_12_1_ < 4; ++lvt_12_1_)
        {
            float lvt_13_1_ = Float.intBitsToFloat(vertexData[lvt_12_1_ * 7]);
            float lvt_14_1_ = Float.intBitsToFloat(vertexData[lvt_12_1_ * 7 + 1]);
            float lvt_15_1_ = Float.intBitsToFloat(vertexData[lvt_12_1_ * 7 + 2]);
            lvt_6_1_ = Math.min(lvt_6_1_, lvt_13_1_);
            lvt_7_1_ = Math.min(lvt_7_1_, lvt_14_1_);
            lvt_8_1_ = Math.min(lvt_8_1_, lvt_15_1_);
            lvt_9_1_ = Math.max(lvt_9_1_, lvt_13_1_);
            lvt_10_1_ = Math.max(lvt_10_1_, lvt_14_1_);
            lvt_11_1_ = Math.max(lvt_11_1_, lvt_15_1_);
        }

        if (quadBounds != null)
        {
            quadBounds[EnumFacing.WEST.getIndex()] = lvt_6_1_;
            quadBounds[EnumFacing.EAST.getIndex()] = lvt_9_1_;
            quadBounds[EnumFacing.DOWN.getIndex()] = lvt_7_1_;
            quadBounds[EnumFacing.UP.getIndex()] = lvt_10_1_;
            quadBounds[EnumFacing.NORTH.getIndex()] = lvt_8_1_;
            quadBounds[EnumFacing.SOUTH.getIndex()] = lvt_11_1_;
            quadBounds[EnumFacing.WEST.getIndex() + EnumFacing.values().length] = 1.0F - lvt_6_1_;
            quadBounds[EnumFacing.EAST.getIndex() + EnumFacing.values().length] = 1.0F - lvt_9_1_;
            quadBounds[EnumFacing.DOWN.getIndex() + EnumFacing.values().length] = 1.0F - lvt_7_1_;
            quadBounds[EnumFacing.UP.getIndex() + EnumFacing.values().length] = 1.0F - lvt_10_1_;
            quadBounds[EnumFacing.NORTH.getIndex() + EnumFacing.values().length] = 1.0F - lvt_8_1_;
            quadBounds[EnumFacing.SOUTH.getIndex() + EnumFacing.values().length] = 1.0F - lvt_11_1_;
        }

        float lvt_12_2_ = 1.0E-4F;
        float lvt_13_2_ = 0.9999F;

        switch (facingIn)
        {
            case DOWN:
                boundsFlags.set(1, lvt_6_1_ >= 1.0E-4F || lvt_8_1_ >= 1.0E-4F || lvt_9_1_ <= 0.9999F || lvt_11_1_ <= 0.9999F);
                boundsFlags.set(0, (lvt_7_1_ < 1.0E-4F || blockIn.isFullCube()) && lvt_7_1_ == lvt_10_1_);
                break;

            case UP:
                boundsFlags.set(1, lvt_6_1_ >= 1.0E-4F || lvt_8_1_ >= 1.0E-4F || lvt_9_1_ <= 0.9999F || lvt_11_1_ <= 0.9999F);
                boundsFlags.set(0, (lvt_10_1_ > 0.9999F || blockIn.isFullCube()) && lvt_7_1_ == lvt_10_1_);
                break;

            case NORTH:
                boundsFlags.set(1, lvt_6_1_ >= 1.0E-4F || lvt_7_1_ >= 1.0E-4F || lvt_9_1_ <= 0.9999F || lvt_10_1_ <= 0.9999F);
                boundsFlags.set(0, (lvt_8_1_ < 1.0E-4F || blockIn.isFullCube()) && lvt_8_1_ == lvt_11_1_);
                break;

            case SOUTH:
                boundsFlags.set(1, lvt_6_1_ >= 1.0E-4F || lvt_7_1_ >= 1.0E-4F || lvt_9_1_ <= 0.9999F || lvt_10_1_ <= 0.9999F);
                boundsFlags.set(0, (lvt_11_1_ > 0.9999F || blockIn.isFullCube()) && lvt_8_1_ == lvt_11_1_);
                break;

            case WEST:
                boundsFlags.set(1, lvt_7_1_ >= 1.0E-4F || lvt_8_1_ >= 1.0E-4F || lvt_10_1_ <= 0.9999F || lvt_11_1_ <= 0.9999F);
                boundsFlags.set(0, (lvt_6_1_ < 1.0E-4F || blockIn.isFullCube()) && lvt_6_1_ == lvt_9_1_);
                break;

            case EAST:
                boundsFlags.set(1, lvt_7_1_ >= 1.0E-4F || lvt_8_1_ >= 1.0E-4F || lvt_10_1_ <= 0.9999F || lvt_11_1_ <= 0.9999F);
                boundsFlags.set(0, (lvt_9_1_ > 0.9999F || blockIn.isFullCube()) && lvt_6_1_ == lvt_9_1_);
        }
    }

    private void renderModelStandardQuads(IBlockAccess blockAccessIn, Block blockIn, BlockPos blockPosIn, EnumFacing faceIn, int brightnessIn, boolean ownBrightness, WorldRenderer worldRendererIn, List<BakedQuad> listQuadsIn, BitSet boundsFlags)
    {
        double lvt_10_1_ = (double)blockPosIn.getX();
        double lvt_12_1_ = (double)blockPosIn.getY();
        double lvt_14_1_ = (double)blockPosIn.getZ();
        Block.EnumOffsetType lvt_16_1_ = blockIn.getOffsetType();

        if (lvt_16_1_ != Block.EnumOffsetType.NONE)
        {
            int lvt_17_1_ = blockPosIn.getX();
            int lvt_18_1_ = blockPosIn.getZ();
            long lvt_19_1_ = (long)(lvt_17_1_ * 3129871) ^ (long)lvt_18_1_ * 116129781L;
            lvt_19_1_ = lvt_19_1_ * lvt_19_1_ * 42317861L + lvt_19_1_ * 11L;
            lvt_10_1_ += ((double)((float)(lvt_19_1_ >> 16 & 15L) / 15.0F) - 0.5D) * 0.5D;
            lvt_14_1_ += ((double)((float)(lvt_19_1_ >> 24 & 15L) / 15.0F) - 0.5D) * 0.5D;

            if (lvt_16_1_ == Block.EnumOffsetType.XYZ)
            {
                lvt_12_1_ += ((double)((float)(lvt_19_1_ >> 20 & 15L) / 15.0F) - 1.0D) * 0.2D;
            }
        }

        for (BakedQuad lvt_18_2_ : listQuadsIn)
        {
            if (ownBrightness)
            {
                this.fillQuadBounds(blockIn, lvt_18_2_.getVertexData(), lvt_18_2_.getFace(), (float[])null, boundsFlags);
                brightnessIn = boundsFlags.get(0) ? blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPosIn.offset(lvt_18_2_.getFace())) : blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPosIn);
            }

            worldRendererIn.addVertexData(lvt_18_2_.getVertexData());
            worldRendererIn.putBrightness4(brightnessIn, brightnessIn, brightnessIn, brightnessIn);

            if (lvt_18_2_.hasTintIndex())
            {
                int lvt_19_2_ = blockIn.colorMultiplier(blockAccessIn, blockPosIn, lvt_18_2_.getTintIndex());

                if (EntityRenderer.anaglyphEnable)
                {
                    lvt_19_2_ = TextureUtil.anaglyphColor(lvt_19_2_);
                }

                float lvt_20_1_ = (float)(lvt_19_2_ >> 16 & 255) / 255.0F;
                float lvt_21_1_ = (float)(lvt_19_2_ >> 8 & 255) / 255.0F;
                float lvt_22_1_ = (float)(lvt_19_2_ & 255) / 255.0F;
                worldRendererIn.putColorMultiplier(lvt_20_1_, lvt_21_1_, lvt_22_1_, 4);
                worldRendererIn.putColorMultiplier(lvt_20_1_, lvt_21_1_, lvt_22_1_, 3);
                worldRendererIn.putColorMultiplier(lvt_20_1_, lvt_21_1_, lvt_22_1_, 2);
                worldRendererIn.putColorMultiplier(lvt_20_1_, lvt_21_1_, lvt_22_1_, 1);
            }

            worldRendererIn.putPosition(lvt_10_1_, lvt_12_1_, lvt_14_1_);
        }
    }

    public void renderModelBrightnessColor(IBakedModel bakedModel, float p_178262_2_, float red, float green, float blue)
    {
        for (EnumFacing lvt_9_1_ : EnumFacing.values())
        {
            this.renderModelBrightnessColorQuads(p_178262_2_, red, green, blue, bakedModel.getFaceQuads(lvt_9_1_));
        }

        this.renderModelBrightnessColorQuads(p_178262_2_, red, green, blue, bakedModel.getGeneralQuads());
    }

    public void renderModelBrightness(IBakedModel model, IBlockState p_178266_2_, float brightness, boolean p_178266_4_)
    {
        Block lvt_5_1_ = p_178266_2_.getBlock();
        lvt_5_1_.setBlockBoundsForItemRender();
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        int lvt_6_1_ = lvt_5_1_.getRenderColor(lvt_5_1_.getStateForEntityRender(p_178266_2_));

        if (EntityRenderer.anaglyphEnable)
        {
            lvt_6_1_ = TextureUtil.anaglyphColor(lvt_6_1_);
        }

        float lvt_7_1_ = (float)(lvt_6_1_ >> 16 & 255) / 255.0F;
        float lvt_8_1_ = (float)(lvt_6_1_ >> 8 & 255) / 255.0F;
        float lvt_9_1_ = (float)(lvt_6_1_ & 255) / 255.0F;

        if (!p_178266_4_)
        {
            GlStateManager.color(brightness, brightness, brightness, 1.0F);
        }

        this.renderModelBrightnessColor(model, brightness, lvt_7_1_, lvt_8_1_, lvt_9_1_);
    }

    private void renderModelBrightnessColorQuads(float brightness, float red, float green, float blue, List<BakedQuad> listQuads)
    {
        Tessellator lvt_6_1_ = Tessellator.getInstance();
        WorldRenderer lvt_7_1_ = lvt_6_1_.getWorldRenderer();

        for (BakedQuad lvt_9_1_ : listQuads)
        {
            lvt_7_1_.begin(7, DefaultVertexFormats.ITEM);
            lvt_7_1_.addVertexData(lvt_9_1_.getVertexData());

            if (lvt_9_1_.hasTintIndex())
            {
                lvt_7_1_.putColorRGB_F4(red * brightness, green * brightness, blue * brightness);
            }
            else
            {
                lvt_7_1_.putColorRGB_F4(brightness, brightness, brightness);
            }

            Vec3i lvt_10_1_ = lvt_9_1_.getFace().getDirectionVec();
            lvt_7_1_.putNormal((float)lvt_10_1_.getX(), (float)lvt_10_1_.getY(), (float)lvt_10_1_.getZ());
            lvt_6_1_.draw();
        }
    }

    class AmbientOcclusionFace
    {
        private final float[] vertexColorMultiplier = new float[4];
        private final int[] vertexBrightness = new int[4];

        public void updateVertexBrightness(IBlockAccess blockAccessIn, Block blockIn, BlockPos blockPosIn, EnumFacing facingIn, float[] quadBounds, BitSet boundsFlags)
        {
            BlockPos lvt_7_1_ = boundsFlags.get(0) ? blockPosIn.offset(facingIn) : blockPosIn;
            BlockModelRenderer.EnumNeighborInfo lvt_8_1_ = BlockModelRenderer.EnumNeighborInfo.getNeighbourInfo(facingIn);
            BlockPos lvt_9_1_ = lvt_7_1_.offset(lvt_8_1_.field_178276_g[0]);
            BlockPos lvt_10_1_ = lvt_7_1_.offset(lvt_8_1_.field_178276_g[1]);
            BlockPos lvt_11_1_ = lvt_7_1_.offset(lvt_8_1_.field_178276_g[2]);
            BlockPos lvt_12_1_ = lvt_7_1_.offset(lvt_8_1_.field_178276_g[3]);
            int lvt_13_1_ = blockIn.getMixedBrightnessForBlock(blockAccessIn, lvt_9_1_);
            int lvt_14_1_ = blockIn.getMixedBrightnessForBlock(blockAccessIn, lvt_10_1_);
            int lvt_15_1_ = blockIn.getMixedBrightnessForBlock(blockAccessIn, lvt_11_1_);
            int lvt_16_1_ = blockIn.getMixedBrightnessForBlock(blockAccessIn, lvt_12_1_);
            float lvt_17_1_ = blockAccessIn.getBlockState(lvt_9_1_).getBlock().getAmbientOcclusionLightValue();
            float lvt_18_1_ = blockAccessIn.getBlockState(lvt_10_1_).getBlock().getAmbientOcclusionLightValue();
            float lvt_19_1_ = blockAccessIn.getBlockState(lvt_11_1_).getBlock().getAmbientOcclusionLightValue();
            float lvt_20_1_ = blockAccessIn.getBlockState(lvt_12_1_).getBlock().getAmbientOcclusionLightValue();
            boolean lvt_21_1_ = blockAccessIn.getBlockState(lvt_9_1_.offset(facingIn)).getBlock().isTranslucent();
            boolean lvt_22_1_ = blockAccessIn.getBlockState(lvt_10_1_.offset(facingIn)).getBlock().isTranslucent();
            boolean lvt_23_1_ = blockAccessIn.getBlockState(lvt_11_1_.offset(facingIn)).getBlock().isTranslucent();
            boolean lvt_24_1_ = blockAccessIn.getBlockState(lvt_12_1_.offset(facingIn)).getBlock().isTranslucent();
            float lvt_25_2_;
            int lvt_29_2_;

            if (!lvt_23_1_ && !lvt_21_1_)
            {
                lvt_25_2_ = lvt_17_1_;
                lvt_29_2_ = lvt_13_1_;
            }
            else
            {
                BlockPos lvt_33_1_ = lvt_9_1_.offset(lvt_8_1_.field_178276_g[2]);
                lvt_25_2_ = blockAccessIn.getBlockState(lvt_33_1_).getBlock().getAmbientOcclusionLightValue();
                lvt_29_2_ = blockIn.getMixedBrightnessForBlock(blockAccessIn, lvt_33_1_);
            }

            float lvt_26_2_;
            int lvt_30_2_;

            if (!lvt_24_1_ && !lvt_21_1_)
            {
                lvt_26_2_ = lvt_17_1_;
                lvt_30_2_ = lvt_13_1_;
            }
            else
            {
                BlockPos lvt_33_2_ = lvt_9_1_.offset(lvt_8_1_.field_178276_g[3]);
                lvt_26_2_ = blockAccessIn.getBlockState(lvt_33_2_).getBlock().getAmbientOcclusionLightValue();
                lvt_30_2_ = blockIn.getMixedBrightnessForBlock(blockAccessIn, lvt_33_2_);
            }

            float lvt_27_2_;
            int lvt_31_2_;

            if (!lvt_23_1_ && !lvt_22_1_)
            {
                lvt_27_2_ = lvt_18_1_;
                lvt_31_2_ = lvt_14_1_;
            }
            else
            {
                BlockPos lvt_33_3_ = lvt_10_1_.offset(lvt_8_1_.field_178276_g[2]);
                lvt_27_2_ = blockAccessIn.getBlockState(lvt_33_3_).getBlock().getAmbientOcclusionLightValue();
                lvt_31_2_ = blockIn.getMixedBrightnessForBlock(blockAccessIn, lvt_33_3_);
            }

            float lvt_28_2_;
            int lvt_32_2_;

            if (!lvt_24_1_ && !lvt_22_1_)
            {
                lvt_28_2_ = lvt_18_1_;
                lvt_32_2_ = lvt_14_1_;
            }
            else
            {
                BlockPos lvt_33_4_ = lvt_10_1_.offset(lvt_8_1_.field_178276_g[3]);
                lvt_28_2_ = blockAccessIn.getBlockState(lvt_33_4_).getBlock().getAmbientOcclusionLightValue();
                lvt_32_2_ = blockIn.getMixedBrightnessForBlock(blockAccessIn, lvt_33_4_);
            }

            int lvt_33_5_ = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPosIn);

            if (boundsFlags.get(0) || !blockAccessIn.getBlockState(blockPosIn.offset(facingIn)).getBlock().isOpaqueCube())
            {
                lvt_33_5_ = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPosIn.offset(facingIn));
            }

            float lvt_34_1_ = boundsFlags.get(0) ? blockAccessIn.getBlockState(lvt_7_1_).getBlock().getAmbientOcclusionLightValue() : blockAccessIn.getBlockState(blockPosIn).getBlock().getAmbientOcclusionLightValue();
            BlockModelRenderer.VertexTranslations lvt_35_1_ = BlockModelRenderer.VertexTranslations.getVertexTranslations(facingIn);

            if (boundsFlags.get(1) && lvt_8_1_.field_178289_i)
            {
                float lvt_36_2_ = (lvt_20_1_ + lvt_17_1_ + lvt_26_2_ + lvt_34_1_) * 0.25F;
                float lvt_37_2_ = (lvt_19_1_ + lvt_17_1_ + lvt_25_2_ + lvt_34_1_) * 0.25F;
                float lvt_38_2_ = (lvt_19_1_ + lvt_18_1_ + lvt_27_2_ + lvt_34_1_) * 0.25F;
                float lvt_39_2_ = (lvt_20_1_ + lvt_18_1_ + lvt_28_2_ + lvt_34_1_) * 0.25F;
                float lvt_40_1_ = quadBounds[lvt_8_1_.field_178286_j[0].field_178229_m] * quadBounds[lvt_8_1_.field_178286_j[1].field_178229_m];
                float lvt_41_1_ = quadBounds[lvt_8_1_.field_178286_j[2].field_178229_m] * quadBounds[lvt_8_1_.field_178286_j[3].field_178229_m];
                float lvt_42_1_ = quadBounds[lvt_8_1_.field_178286_j[4].field_178229_m] * quadBounds[lvt_8_1_.field_178286_j[5].field_178229_m];
                float lvt_43_1_ = quadBounds[lvt_8_1_.field_178286_j[6].field_178229_m] * quadBounds[lvt_8_1_.field_178286_j[7].field_178229_m];
                float lvt_44_1_ = quadBounds[lvt_8_1_.field_178287_k[0].field_178229_m] * quadBounds[lvt_8_1_.field_178287_k[1].field_178229_m];
                float lvt_45_1_ = quadBounds[lvt_8_1_.field_178287_k[2].field_178229_m] * quadBounds[lvt_8_1_.field_178287_k[3].field_178229_m];
                float lvt_46_1_ = quadBounds[lvt_8_1_.field_178287_k[4].field_178229_m] * quadBounds[lvt_8_1_.field_178287_k[5].field_178229_m];
                float lvt_47_1_ = quadBounds[lvt_8_1_.field_178287_k[6].field_178229_m] * quadBounds[lvt_8_1_.field_178287_k[7].field_178229_m];
                float lvt_48_1_ = quadBounds[lvt_8_1_.field_178284_l[0].field_178229_m] * quadBounds[lvt_8_1_.field_178284_l[1].field_178229_m];
                float lvt_49_1_ = quadBounds[lvt_8_1_.field_178284_l[2].field_178229_m] * quadBounds[lvt_8_1_.field_178284_l[3].field_178229_m];
                float lvt_50_1_ = quadBounds[lvt_8_1_.field_178284_l[4].field_178229_m] * quadBounds[lvt_8_1_.field_178284_l[5].field_178229_m];
                float lvt_51_1_ = quadBounds[lvt_8_1_.field_178284_l[6].field_178229_m] * quadBounds[lvt_8_1_.field_178284_l[7].field_178229_m];
                float lvt_52_1_ = quadBounds[lvt_8_1_.field_178285_m[0].field_178229_m] * quadBounds[lvt_8_1_.field_178285_m[1].field_178229_m];
                float lvt_53_1_ = quadBounds[lvt_8_1_.field_178285_m[2].field_178229_m] * quadBounds[lvt_8_1_.field_178285_m[3].field_178229_m];
                float lvt_54_1_ = quadBounds[lvt_8_1_.field_178285_m[4].field_178229_m] * quadBounds[lvt_8_1_.field_178285_m[5].field_178229_m];
                float lvt_55_1_ = quadBounds[lvt_8_1_.field_178285_m[6].field_178229_m] * quadBounds[lvt_8_1_.field_178285_m[7].field_178229_m];
                this.vertexColorMultiplier[lvt_35_1_.field_178191_g] = lvt_36_2_ * lvt_40_1_ + lvt_37_2_ * lvt_41_1_ + lvt_38_2_ * lvt_42_1_ + lvt_39_2_ * lvt_43_1_;
                this.vertexColorMultiplier[lvt_35_1_.field_178200_h] = lvt_36_2_ * lvt_44_1_ + lvt_37_2_ * lvt_45_1_ + lvt_38_2_ * lvt_46_1_ + lvt_39_2_ * lvt_47_1_;
                this.vertexColorMultiplier[lvt_35_1_.field_178201_i] = lvt_36_2_ * lvt_48_1_ + lvt_37_2_ * lvt_49_1_ + lvt_38_2_ * lvt_50_1_ + lvt_39_2_ * lvt_51_1_;
                this.vertexColorMultiplier[lvt_35_1_.field_178198_j] = lvt_36_2_ * lvt_52_1_ + lvt_37_2_ * lvt_53_1_ + lvt_38_2_ * lvt_54_1_ + lvt_39_2_ * lvt_55_1_;
                int lvt_56_1_ = this.getAoBrightness(lvt_16_1_, lvt_13_1_, lvt_30_2_, lvt_33_5_);
                int lvt_57_1_ = this.getAoBrightness(lvt_15_1_, lvt_13_1_, lvt_29_2_, lvt_33_5_);
                int lvt_58_1_ = this.getAoBrightness(lvt_15_1_, lvt_14_1_, lvt_31_2_, lvt_33_5_);
                int lvt_59_1_ = this.getAoBrightness(lvt_16_1_, lvt_14_1_, lvt_32_2_, lvt_33_5_);
                this.vertexBrightness[lvt_35_1_.field_178191_g] = this.getVertexBrightness(lvt_56_1_, lvt_57_1_, lvt_58_1_, lvt_59_1_, lvt_40_1_, lvt_41_1_, lvt_42_1_, lvt_43_1_);
                this.vertexBrightness[lvt_35_1_.field_178200_h] = this.getVertexBrightness(lvt_56_1_, lvt_57_1_, lvt_58_1_, lvt_59_1_, lvt_44_1_, lvt_45_1_, lvt_46_1_, lvt_47_1_);
                this.vertexBrightness[lvt_35_1_.field_178201_i] = this.getVertexBrightness(lvt_56_1_, lvt_57_1_, lvt_58_1_, lvt_59_1_, lvt_48_1_, lvt_49_1_, lvt_50_1_, lvt_51_1_);
                this.vertexBrightness[lvt_35_1_.field_178198_j] = this.getVertexBrightness(lvt_56_1_, lvt_57_1_, lvt_58_1_, lvt_59_1_, lvt_52_1_, lvt_53_1_, lvt_54_1_, lvt_55_1_);
            }
            else
            {
                float lvt_36_1_ = (lvt_20_1_ + lvt_17_1_ + lvt_26_2_ + lvt_34_1_) * 0.25F;
                float lvt_37_1_ = (lvt_19_1_ + lvt_17_1_ + lvt_25_2_ + lvt_34_1_) * 0.25F;
                float lvt_38_1_ = (lvt_19_1_ + lvt_18_1_ + lvt_27_2_ + lvt_34_1_) * 0.25F;
                float lvt_39_1_ = (lvt_20_1_ + lvt_18_1_ + lvt_28_2_ + lvt_34_1_) * 0.25F;
                this.vertexBrightness[lvt_35_1_.field_178191_g] = this.getAoBrightness(lvt_16_1_, lvt_13_1_, lvt_30_2_, lvt_33_5_);
                this.vertexBrightness[lvt_35_1_.field_178200_h] = this.getAoBrightness(lvt_15_1_, lvt_13_1_, lvt_29_2_, lvt_33_5_);
                this.vertexBrightness[lvt_35_1_.field_178201_i] = this.getAoBrightness(lvt_15_1_, lvt_14_1_, lvt_31_2_, lvt_33_5_);
                this.vertexBrightness[lvt_35_1_.field_178198_j] = this.getAoBrightness(lvt_16_1_, lvt_14_1_, lvt_32_2_, lvt_33_5_);
                this.vertexColorMultiplier[lvt_35_1_.field_178191_g] = lvt_36_1_;
                this.vertexColorMultiplier[lvt_35_1_.field_178200_h] = lvt_37_1_;
                this.vertexColorMultiplier[lvt_35_1_.field_178201_i] = lvt_38_1_;
                this.vertexColorMultiplier[lvt_35_1_.field_178198_j] = lvt_39_1_;
            }
        }

        private int getAoBrightness(int br1, int br2, int br3, int br4)
        {
            if (br1 == 0)
            {
                br1 = br4;
            }

            if (br2 == 0)
            {
                br2 = br4;
            }

            if (br3 == 0)
            {
                br3 = br4;
            }

            return br1 + br2 + br3 + br4 >> 2 & 16711935;
        }

        private int getVertexBrightness(int p_178203_1_, int p_178203_2_, int p_178203_3_, int p_178203_4_, float p_178203_5_, float p_178203_6_, float p_178203_7_, float p_178203_8_)
        {
            int lvt_9_1_ = (int)((float)(p_178203_1_ >> 16 & 255) * p_178203_5_ + (float)(p_178203_2_ >> 16 & 255) * p_178203_6_ + (float)(p_178203_3_ >> 16 & 255) * p_178203_7_ + (float)(p_178203_4_ >> 16 & 255) * p_178203_8_) & 255;
            int lvt_10_1_ = (int)((float)(p_178203_1_ & 255) * p_178203_5_ + (float)(p_178203_2_ & 255) * p_178203_6_ + (float)(p_178203_3_ & 255) * p_178203_7_ + (float)(p_178203_4_ & 255) * p_178203_8_) & 255;
            return lvt_9_1_ << 16 | lvt_10_1_;
        }
    }

    public static enum EnumNeighborInfo
    {
        DOWN(new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.5F, false, new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0]),
        UP(new EnumFacing[]{EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH}, 1.0F, false, new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0], new BlockModelRenderer.Orientation[0]),
        NORTH(new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.WEST}, 0.8F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_WEST}),
        SOUTH(new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.UP}, 0.8F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.EAST}),
        WEST(new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.6F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.SOUTH}),
        EAST(new EnumFacing[]{EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.6F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.SOUTH});

        protected final EnumFacing[] field_178276_g;
        protected final float field_178288_h;
        protected final boolean field_178289_i;
        protected final BlockModelRenderer.Orientation[] field_178286_j;
        protected final BlockModelRenderer.Orientation[] field_178287_k;
        protected final BlockModelRenderer.Orientation[] field_178284_l;
        protected final BlockModelRenderer.Orientation[] field_178285_m;
        private static final BlockModelRenderer.EnumNeighborInfo[] VALUES = new BlockModelRenderer.EnumNeighborInfo[6];

        private EnumNeighborInfo(EnumFacing[] p_i46236_3_, float p_i46236_4_, boolean p_i46236_5_, BlockModelRenderer.Orientation[] p_i46236_6_, BlockModelRenderer.Orientation[] p_i46236_7_, BlockModelRenderer.Orientation[] p_i46236_8_, BlockModelRenderer.Orientation[] p_i46236_9_)
        {
            this.field_178276_g = p_i46236_3_;
            this.field_178288_h = p_i46236_4_;
            this.field_178289_i = p_i46236_5_;
            this.field_178286_j = p_i46236_6_;
            this.field_178287_k = p_i46236_7_;
            this.field_178284_l = p_i46236_8_;
            this.field_178285_m = p_i46236_9_;
        }

        public static BlockModelRenderer.EnumNeighborInfo getNeighbourInfo(EnumFacing p_178273_0_)
        {
            return VALUES[p_178273_0_.getIndex()];
        }

        static {
            VALUES[EnumFacing.DOWN.getIndex()] = DOWN;
            VALUES[EnumFacing.UP.getIndex()] = UP;
            VALUES[EnumFacing.NORTH.getIndex()] = NORTH;
            VALUES[EnumFacing.SOUTH.getIndex()] = SOUTH;
            VALUES[EnumFacing.WEST.getIndex()] = WEST;
            VALUES[EnumFacing.EAST.getIndex()] = EAST;
        }
    }

    public static enum Orientation
    {
        DOWN(EnumFacing.DOWN, false),
        UP(EnumFacing.UP, false),
        NORTH(EnumFacing.NORTH, false),
        SOUTH(EnumFacing.SOUTH, false),
        WEST(EnumFacing.WEST, false),
        EAST(EnumFacing.EAST, false),
        FLIP_DOWN(EnumFacing.DOWN, true),
        FLIP_UP(EnumFacing.UP, true),
        FLIP_NORTH(EnumFacing.NORTH, true),
        FLIP_SOUTH(EnumFacing.SOUTH, true),
        FLIP_WEST(EnumFacing.WEST, true),
        FLIP_EAST(EnumFacing.EAST, true);

        protected final int field_178229_m;

        private Orientation(EnumFacing p_i46233_3_, boolean p_i46233_4_)
        {
            this.field_178229_m = p_i46233_3_.getIndex() + (p_i46233_4_ ? EnumFacing.values().length : 0);
        }
    }

    static enum VertexTranslations
    {
        DOWN(0, 1, 2, 3),
        UP(2, 3, 0, 1),
        NORTH(3, 0, 1, 2),
        SOUTH(0, 1, 2, 3),
        WEST(3, 0, 1, 2),
        EAST(1, 2, 3, 0);

        private final int field_178191_g;
        private final int field_178200_h;
        private final int field_178201_i;
        private final int field_178198_j;
        private static final BlockModelRenderer.VertexTranslations[] VALUES = new BlockModelRenderer.VertexTranslations[6];

        private VertexTranslations(int p_i46234_3_, int p_i46234_4_, int p_i46234_5_, int p_i46234_6_)
        {
            this.field_178191_g = p_i46234_3_;
            this.field_178200_h = p_i46234_4_;
            this.field_178201_i = p_i46234_5_;
            this.field_178198_j = p_i46234_6_;
        }

        public static BlockModelRenderer.VertexTranslations getVertexTranslations(EnumFacing p_178184_0_)
        {
            return VALUES[p_178184_0_.getIndex()];
        }

        static {
            VALUES[EnumFacing.DOWN.getIndex()] = DOWN;
            VALUES[EnumFacing.UP.getIndex()] = UP;
            VALUES[EnumFacing.NORTH.getIndex()] = NORTH;
            VALUES[EnumFacing.SOUTH.getIndex()] = SOUTH;
            VALUES[EnumFacing.WEST.getIndex()] = WEST;
            VALUES[EnumFacing.EAST.getIndex()] = EAST;
        }
    }
}
