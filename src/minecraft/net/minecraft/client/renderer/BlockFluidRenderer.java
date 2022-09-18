package net.minecraft.client.renderer;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public class BlockFluidRenderer
{
    private TextureAtlasSprite[] atlasSpritesLava = new TextureAtlasSprite[2];
    private TextureAtlasSprite[] atlasSpritesWater = new TextureAtlasSprite[2];

    public BlockFluidRenderer()
    {
        this.initAtlasSprites();
    }

    protected void initAtlasSprites()
    {
        TextureMap lvt_1_1_ = Minecraft.getMinecraft().getTextureMapBlocks();
        this.atlasSpritesLava[0] = lvt_1_1_.getAtlasSprite("minecraft:blocks/lava_still");
        this.atlasSpritesLava[1] = lvt_1_1_.getAtlasSprite("minecraft:blocks/lava_flow");
        this.atlasSpritesWater[0] = lvt_1_1_.getAtlasSprite("minecraft:blocks/water_still");
        this.atlasSpritesWater[1] = lvt_1_1_.getAtlasSprite("minecraft:blocks/water_flow");
    }

    public boolean renderFluid(IBlockAccess blockAccess, IBlockState blockStateIn, BlockPos blockPosIn, WorldRenderer worldRendererIn)
    {
        BlockLiquid lvt_5_1_ = (BlockLiquid)blockStateIn.getBlock();
        lvt_5_1_.setBlockBoundsBasedOnState(blockAccess, blockPosIn);
        TextureAtlasSprite[] lvt_6_1_ = lvt_5_1_.getMaterial() == Material.lava ? this.atlasSpritesLava : this.atlasSpritesWater;
        int lvt_7_1_ = lvt_5_1_.colorMultiplier(blockAccess, blockPosIn);
        float lvt_8_1_ = (float)(lvt_7_1_ >> 16 & 255) / 255.0F;
        float lvt_9_1_ = (float)(lvt_7_1_ >> 8 & 255) / 255.0F;
        float lvt_10_1_ = (float)(lvt_7_1_ & 255) / 255.0F;
        boolean lvt_11_1_ = lvt_5_1_.shouldSideBeRendered(blockAccess, blockPosIn.up(), EnumFacing.UP);
        boolean lvt_12_1_ = lvt_5_1_.shouldSideBeRendered(blockAccess, blockPosIn.down(), EnumFacing.DOWN);
        boolean[] lvt_13_1_ = new boolean[] {lvt_5_1_.shouldSideBeRendered(blockAccess, blockPosIn.north(), EnumFacing.NORTH), lvt_5_1_.shouldSideBeRendered(blockAccess, blockPosIn.south(), EnumFacing.SOUTH), lvt_5_1_.shouldSideBeRendered(blockAccess, blockPosIn.west(), EnumFacing.WEST), lvt_5_1_.shouldSideBeRendered(blockAccess, blockPosIn.east(), EnumFacing.EAST)};

        if (!lvt_11_1_ && !lvt_12_1_ && !lvt_13_1_[0] && !lvt_13_1_[1] && !lvt_13_1_[2] && !lvt_13_1_[3])
        {
            return false;
        }
        else
        {
            boolean lvt_14_1_ = false;
            float lvt_15_1_ = 0.5F;
            float lvt_16_1_ = 1.0F;
            float lvt_17_1_ = 0.8F;
            float lvt_18_1_ = 0.6F;
            Material lvt_19_1_ = lvt_5_1_.getMaterial();
            float lvt_20_1_ = this.getFluidHeight(blockAccess, blockPosIn, lvt_19_1_);
            float lvt_21_1_ = this.getFluidHeight(blockAccess, blockPosIn.south(), lvt_19_1_);
            float lvt_22_1_ = this.getFluidHeight(blockAccess, blockPosIn.east().south(), lvt_19_1_);
            float lvt_23_1_ = this.getFluidHeight(blockAccess, blockPosIn.east(), lvt_19_1_);
            double lvt_24_1_ = (double)blockPosIn.getX();
            double lvt_26_1_ = (double)blockPosIn.getY();
            double lvt_28_1_ = (double)blockPosIn.getZ();
            float lvt_30_1_ = 0.001F;

            if (lvt_11_1_)
            {
                lvt_14_1_ = true;
                TextureAtlasSprite lvt_31_1_ = lvt_6_1_[0];
                float lvt_32_1_ = (float)BlockLiquid.getFlowDirection(blockAccess, blockPosIn, lvt_19_1_);

                if (lvt_32_1_ > -999.0F)
                {
                    lvt_31_1_ = lvt_6_1_[1];
                }

                lvt_20_1_ -= lvt_30_1_;
                lvt_21_1_ -= lvt_30_1_;
                lvt_22_1_ -= lvt_30_1_;
                lvt_23_1_ -= lvt_30_1_;
                float lvt_33_1_;
                float lvt_34_1_;
                float lvt_35_1_;
                float lvt_36_1_;
                float lvt_37_1_;
                float lvt_38_1_;
                float lvt_39_1_;
                float lvt_40_1_;

                if (lvt_32_1_ < -999.0F)
                {
                    lvt_33_1_ = lvt_31_1_.getInterpolatedU(0.0D);
                    lvt_37_1_ = lvt_31_1_.getInterpolatedV(0.0D);
                    lvt_34_1_ = lvt_33_1_;
                    lvt_38_1_ = lvt_31_1_.getInterpolatedV(16.0D);
                    lvt_35_1_ = lvt_31_1_.getInterpolatedU(16.0D);
                    lvt_39_1_ = lvt_38_1_;
                    lvt_36_1_ = lvt_35_1_;
                    lvt_40_1_ = lvt_37_1_;
                }
                else
                {
                    float lvt_41_1_ = MathHelper.sin(lvt_32_1_) * 0.25F;
                    float lvt_42_1_ = MathHelper.cos(lvt_32_1_) * 0.25F;
                    float lvt_43_1_ = 8.0F;
                    lvt_33_1_ = lvt_31_1_.getInterpolatedU((double)(8.0F + (-lvt_42_1_ - lvt_41_1_) * 16.0F));
                    lvt_37_1_ = lvt_31_1_.getInterpolatedV((double)(8.0F + (-lvt_42_1_ + lvt_41_1_) * 16.0F));
                    lvt_34_1_ = lvt_31_1_.getInterpolatedU((double)(8.0F + (-lvt_42_1_ + lvt_41_1_) * 16.0F));
                    lvt_38_1_ = lvt_31_1_.getInterpolatedV((double)(8.0F + (lvt_42_1_ + lvt_41_1_) * 16.0F));
                    lvt_35_1_ = lvt_31_1_.getInterpolatedU((double)(8.0F + (lvt_42_1_ + lvt_41_1_) * 16.0F));
                    lvt_39_1_ = lvt_31_1_.getInterpolatedV((double)(8.0F + (lvt_42_1_ - lvt_41_1_) * 16.0F));
                    lvt_36_1_ = lvt_31_1_.getInterpolatedU((double)(8.0F + (lvt_42_1_ - lvt_41_1_) * 16.0F));
                    lvt_40_1_ = lvt_31_1_.getInterpolatedV((double)(8.0F + (-lvt_42_1_ - lvt_41_1_) * 16.0F));
                }

                int lvt_41_2_ = lvt_5_1_.getMixedBrightnessForBlock(blockAccess, blockPosIn);
                int lvt_42_2_ = lvt_41_2_ >> 16 & 65535;
                int lvt_43_2_ = lvt_41_2_ & 65535;
                float lvt_44_1_ = lvt_16_1_ * lvt_8_1_;
                float lvt_45_1_ = lvt_16_1_ * lvt_9_1_;
                float lvt_46_1_ = lvt_16_1_ * lvt_10_1_;
                worldRendererIn.pos(lvt_24_1_ + 0.0D, lvt_26_1_ + (double)lvt_20_1_, lvt_28_1_ + 0.0D).color(lvt_44_1_, lvt_45_1_, lvt_46_1_, 1.0F).tex((double)lvt_33_1_, (double)lvt_37_1_).lightmap(lvt_42_2_, lvt_43_2_).endVertex();
                worldRendererIn.pos(lvt_24_1_ + 0.0D, lvt_26_1_ + (double)lvt_21_1_, lvt_28_1_ + 1.0D).color(lvt_44_1_, lvt_45_1_, lvt_46_1_, 1.0F).tex((double)lvt_34_1_, (double)lvt_38_1_).lightmap(lvt_42_2_, lvt_43_2_).endVertex();
                worldRendererIn.pos(lvt_24_1_ + 1.0D, lvt_26_1_ + (double)lvt_22_1_, lvt_28_1_ + 1.0D).color(lvt_44_1_, lvt_45_1_, lvt_46_1_, 1.0F).tex((double)lvt_35_1_, (double)lvt_39_1_).lightmap(lvt_42_2_, lvt_43_2_).endVertex();
                worldRendererIn.pos(lvt_24_1_ + 1.0D, lvt_26_1_ + (double)lvt_23_1_, lvt_28_1_ + 0.0D).color(lvt_44_1_, lvt_45_1_, lvt_46_1_, 1.0F).tex((double)lvt_36_1_, (double)lvt_40_1_).lightmap(lvt_42_2_, lvt_43_2_).endVertex();

                if (lvt_5_1_.shouldRenderSides(blockAccess, blockPosIn.up()))
                {
                    worldRendererIn.pos(lvt_24_1_ + 0.0D, lvt_26_1_ + (double)lvt_20_1_, lvt_28_1_ + 0.0D).color(lvt_44_1_, lvt_45_1_, lvt_46_1_, 1.0F).tex((double)lvt_33_1_, (double)lvt_37_1_).lightmap(lvt_42_2_, lvt_43_2_).endVertex();
                    worldRendererIn.pos(lvt_24_1_ + 1.0D, lvt_26_1_ + (double)lvt_23_1_, lvt_28_1_ + 0.0D).color(lvt_44_1_, lvt_45_1_, lvt_46_1_, 1.0F).tex((double)lvt_36_1_, (double)lvt_40_1_).lightmap(lvt_42_2_, lvt_43_2_).endVertex();
                    worldRendererIn.pos(lvt_24_1_ + 1.0D, lvt_26_1_ + (double)lvt_22_1_, lvt_28_1_ + 1.0D).color(lvt_44_1_, lvt_45_1_, lvt_46_1_, 1.0F).tex((double)lvt_35_1_, (double)lvt_39_1_).lightmap(lvt_42_2_, lvt_43_2_).endVertex();
                    worldRendererIn.pos(lvt_24_1_ + 0.0D, lvt_26_1_ + (double)lvt_21_1_, lvt_28_1_ + 1.0D).color(lvt_44_1_, lvt_45_1_, lvt_46_1_, 1.0F).tex((double)lvt_34_1_, (double)lvt_38_1_).lightmap(lvt_42_2_, lvt_43_2_).endVertex();
                }
            }

            if (lvt_12_1_)
            {
                float lvt_32_2_ = lvt_6_1_[0].getMinU();
                float lvt_33_3_ = lvt_6_1_[0].getMaxU();
                float lvt_34_3_ = lvt_6_1_[0].getMinV();
                float lvt_35_3_ = lvt_6_1_[0].getMaxV();
                int lvt_36_3_ = lvt_5_1_.getMixedBrightnessForBlock(blockAccess, blockPosIn.down());
                int lvt_37_3_ = lvt_36_3_ >> 16 & 65535;
                int lvt_38_3_ = lvt_36_3_ & 65535;
                worldRendererIn.pos(lvt_24_1_, lvt_26_1_, lvt_28_1_ + 1.0D).color(lvt_15_1_, lvt_15_1_, lvt_15_1_, 1.0F).tex((double)lvt_32_2_, (double)lvt_35_3_).lightmap(lvt_37_3_, lvt_38_3_).endVertex();
                worldRendererIn.pos(lvt_24_1_, lvt_26_1_, lvt_28_1_).color(lvt_15_1_, lvt_15_1_, lvt_15_1_, 1.0F).tex((double)lvt_32_2_, (double)lvt_34_3_).lightmap(lvt_37_3_, lvt_38_3_).endVertex();
                worldRendererIn.pos(lvt_24_1_ + 1.0D, lvt_26_1_, lvt_28_1_).color(lvt_15_1_, lvt_15_1_, lvt_15_1_, 1.0F).tex((double)lvt_33_3_, (double)lvt_34_3_).lightmap(lvt_37_3_, lvt_38_3_).endVertex();
                worldRendererIn.pos(lvt_24_1_ + 1.0D, lvt_26_1_, lvt_28_1_ + 1.0D).color(lvt_15_1_, lvt_15_1_, lvt_15_1_, 1.0F).tex((double)lvt_33_3_, (double)lvt_35_3_).lightmap(lvt_37_3_, lvt_38_3_).endVertex();
                lvt_14_1_ = true;
            }

            for (int lvt_32_3_ = 0; lvt_32_3_ < 4; ++lvt_32_3_)
            {
                int lvt_33_4_ = 0;
                int lvt_34_4_ = 0;

                if (lvt_32_3_ == 0)
                {
                    --lvt_34_4_;
                }

                if (lvt_32_3_ == 1)
                {
                    ++lvt_34_4_;
                }

                if (lvt_32_3_ == 2)
                {
                    --lvt_33_4_;
                }

                if (lvt_32_3_ == 3)
                {
                    ++lvt_33_4_;
                }

                BlockPos lvt_35_4_ = blockPosIn.add(lvt_33_4_, 0, lvt_34_4_);
                TextureAtlasSprite lvt_31_2_ = lvt_6_1_[1];

                if (lvt_13_1_[lvt_32_3_])
                {
                    float lvt_36_4_;
                    float lvt_37_4_;
                    double lvt_38_4_;
                    double lvt_40_3_;
                    double lvt_42_3_;
                    double lvt_44_2_;

                    if (lvt_32_3_ == 0)
                    {
                        lvt_36_4_ = lvt_20_1_;
                        lvt_37_4_ = lvt_23_1_;
                        lvt_38_4_ = lvt_24_1_;
                        lvt_42_3_ = lvt_24_1_ + 1.0D;
                        lvt_40_3_ = lvt_28_1_ + (double)lvt_30_1_;
                        lvt_44_2_ = lvt_28_1_ + (double)lvt_30_1_;
                    }
                    else if (lvt_32_3_ == 1)
                    {
                        lvt_36_4_ = lvt_22_1_;
                        lvt_37_4_ = lvt_21_1_;
                        lvt_38_4_ = lvt_24_1_ + 1.0D;
                        lvt_42_3_ = lvt_24_1_;
                        lvt_40_3_ = lvt_28_1_ + 1.0D - (double)lvt_30_1_;
                        lvt_44_2_ = lvt_28_1_ + 1.0D - (double)lvt_30_1_;
                    }
                    else if (lvt_32_3_ == 2)
                    {
                        lvt_36_4_ = lvt_21_1_;
                        lvt_37_4_ = lvt_20_1_;
                        lvt_38_4_ = lvt_24_1_ + (double)lvt_30_1_;
                        lvt_42_3_ = lvt_24_1_ + (double)lvt_30_1_;
                        lvt_40_3_ = lvt_28_1_ + 1.0D;
                        lvt_44_2_ = lvt_28_1_;
                    }
                    else
                    {
                        lvt_36_4_ = lvt_23_1_;
                        lvt_37_4_ = lvt_22_1_;
                        lvt_38_4_ = lvt_24_1_ + 1.0D - (double)lvt_30_1_;
                        lvt_42_3_ = lvt_24_1_ + 1.0D - (double)lvt_30_1_;
                        lvt_40_3_ = lvt_28_1_;
                        lvt_44_2_ = lvt_28_1_ + 1.0D;
                    }

                    lvt_14_1_ = true;
                    float lvt_46_2_ = lvt_31_2_.getInterpolatedU(0.0D);
                    float lvt_47_1_ = lvt_31_2_.getInterpolatedU(8.0D);
                    float lvt_48_1_ = lvt_31_2_.getInterpolatedV((double)((1.0F - lvt_36_4_) * 16.0F * 0.5F));
                    float lvt_49_1_ = lvt_31_2_.getInterpolatedV((double)((1.0F - lvt_37_4_) * 16.0F * 0.5F));
                    float lvt_50_1_ = lvt_31_2_.getInterpolatedV(8.0D);
                    int lvt_51_1_ = lvt_5_1_.getMixedBrightnessForBlock(blockAccess, lvt_35_4_);
                    int lvt_52_1_ = lvt_51_1_ >> 16 & 65535;
                    int lvt_53_1_ = lvt_51_1_ & 65535;
                    float lvt_54_1_ = lvt_32_3_ < 2 ? lvt_17_1_ : lvt_18_1_;
                    float lvt_55_1_ = lvt_16_1_ * lvt_54_1_ * lvt_8_1_;
                    float lvt_56_1_ = lvt_16_1_ * lvt_54_1_ * lvt_9_1_;
                    float lvt_57_1_ = lvt_16_1_ * lvt_54_1_ * lvt_10_1_;
                    worldRendererIn.pos(lvt_38_4_, lvt_26_1_ + (double)lvt_36_4_, lvt_40_3_).color(lvt_55_1_, lvt_56_1_, lvt_57_1_, 1.0F).tex((double)lvt_46_2_, (double)lvt_48_1_).lightmap(lvt_52_1_, lvt_53_1_).endVertex();
                    worldRendererIn.pos(lvt_42_3_, lvt_26_1_ + (double)lvt_37_4_, lvt_44_2_).color(lvt_55_1_, lvt_56_1_, lvt_57_1_, 1.0F).tex((double)lvt_47_1_, (double)lvt_49_1_).lightmap(lvt_52_1_, lvt_53_1_).endVertex();
                    worldRendererIn.pos(lvt_42_3_, lvt_26_1_ + 0.0D, lvt_44_2_).color(lvt_55_1_, lvt_56_1_, lvt_57_1_, 1.0F).tex((double)lvt_47_1_, (double)lvt_50_1_).lightmap(lvt_52_1_, lvt_53_1_).endVertex();
                    worldRendererIn.pos(lvt_38_4_, lvt_26_1_ + 0.0D, lvt_40_3_).color(lvt_55_1_, lvt_56_1_, lvt_57_1_, 1.0F).tex((double)lvt_46_2_, (double)lvt_50_1_).lightmap(lvt_52_1_, lvt_53_1_).endVertex();
                    worldRendererIn.pos(lvt_38_4_, lvt_26_1_ + 0.0D, lvt_40_3_).color(lvt_55_1_, lvt_56_1_, lvt_57_1_, 1.0F).tex((double)lvt_46_2_, (double)lvt_50_1_).lightmap(lvt_52_1_, lvt_53_1_).endVertex();
                    worldRendererIn.pos(lvt_42_3_, lvt_26_1_ + 0.0D, lvt_44_2_).color(lvt_55_1_, lvt_56_1_, lvt_57_1_, 1.0F).tex((double)lvt_47_1_, (double)lvt_50_1_).lightmap(lvt_52_1_, lvt_53_1_).endVertex();
                    worldRendererIn.pos(lvt_42_3_, lvt_26_1_ + (double)lvt_37_4_, lvt_44_2_).color(lvt_55_1_, lvt_56_1_, lvt_57_1_, 1.0F).tex((double)lvt_47_1_, (double)lvt_49_1_).lightmap(lvt_52_1_, lvt_53_1_).endVertex();
                    worldRendererIn.pos(lvt_38_4_, lvt_26_1_ + (double)lvt_36_4_, lvt_40_3_).color(lvt_55_1_, lvt_56_1_, lvt_57_1_, 1.0F).tex((double)lvt_46_2_, (double)lvt_48_1_).lightmap(lvt_52_1_, lvt_53_1_).endVertex();
                }
            }

            return lvt_14_1_;
        }
    }

    private float getFluidHeight(IBlockAccess blockAccess, BlockPos blockPosIn, Material blockMaterial)
    {
        int lvt_4_1_ = 0;
        float lvt_5_1_ = 0.0F;

        for (int lvt_6_1_ = 0; lvt_6_1_ < 4; ++lvt_6_1_)
        {
            BlockPos lvt_7_1_ = blockPosIn.add(-(lvt_6_1_ & 1), 0, -(lvt_6_1_ >> 1 & 1));

            if (blockAccess.getBlockState(lvt_7_1_.up()).getBlock().getMaterial() == blockMaterial)
            {
                return 1.0F;
            }

            IBlockState lvt_8_1_ = blockAccess.getBlockState(lvt_7_1_);
            Material lvt_9_1_ = lvt_8_1_.getBlock().getMaterial();

            if (lvt_9_1_ != blockMaterial)
            {
                if (!lvt_9_1_.isSolid())
                {
                    ++lvt_5_1_;
                    ++lvt_4_1_;
                }
            }
            else
            {
                int lvt_10_1_ = ((Integer)lvt_8_1_.getValue(BlockLiquid.LEVEL)).intValue();

                if (lvt_10_1_ >= 8 || lvt_10_1_ == 0)
                {
                    lvt_5_1_ += BlockLiquid.getLiquidHeightPercent(lvt_10_1_) * 10.0F;
                    lvt_4_1_ += 10;
                }

                lvt_5_1_ += BlockLiquid.getLiquidHeightPercent(lvt_10_1_);
                ++lvt_4_1_;
            }
        }

        return 1.0F - lvt_5_1_ / (float)lvt_4_1_;
    }
}
