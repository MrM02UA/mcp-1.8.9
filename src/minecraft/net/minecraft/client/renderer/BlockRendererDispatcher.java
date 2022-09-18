package net.minecraft.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;

public class BlockRendererDispatcher implements IResourceManagerReloadListener
{
    private BlockModelShapes blockModelShapes;
    private final GameSettings gameSettings;
    private final BlockModelRenderer blockModelRenderer = new BlockModelRenderer();
    private final ChestRenderer chestRenderer = new ChestRenderer();
    private final BlockFluidRenderer fluidRenderer = new BlockFluidRenderer();

    public BlockRendererDispatcher(BlockModelShapes blockModelShapesIn, GameSettings gameSettingsIn)
    {
        this.blockModelShapes = blockModelShapesIn;
        this.gameSettings = gameSettingsIn;
    }

    public BlockModelShapes getBlockModelShapes()
    {
        return this.blockModelShapes;
    }

    public void renderBlockDamage(IBlockState state, BlockPos pos, TextureAtlasSprite texture, IBlockAccess blockAccess)
    {
        Block lvt_5_1_ = state.getBlock();
        int lvt_6_1_ = lvt_5_1_.getRenderType();

        if (lvt_6_1_ == 3)
        {
            state = lvt_5_1_.getActualState(state, blockAccess, pos);
            IBakedModel lvt_7_1_ = this.blockModelShapes.getModelForState(state);
            IBakedModel lvt_8_1_ = (new SimpleBakedModel.Builder(lvt_7_1_, texture)).makeBakedModel();
            this.blockModelRenderer.renderModel(blockAccess, lvt_8_1_, state, pos, Tessellator.getInstance().getWorldRenderer());
        }
    }

    public boolean renderBlock(IBlockState state, BlockPos pos, IBlockAccess blockAccess, WorldRenderer worldRendererIn)
    {
        try
        {
            int lvt_5_1_ = state.getBlock().getRenderType();

            if (lvt_5_1_ == -1)
            {
                return false;
            }
            else
            {
                switch (lvt_5_1_)
                {
                    case 1:
                        return this.fluidRenderer.renderFluid(blockAccess, state, pos, worldRendererIn);

                    case 2:
                        return false;

                    case 3:
                        IBakedModel lvt_6_1_ = this.getModelFromBlockState(state, blockAccess, pos);
                        return this.blockModelRenderer.renderModel(blockAccess, lvt_6_1_, state, pos, worldRendererIn);

                    default:
                        return false;
                }
            }
        }
        catch (Throwable var8)
        {
            CrashReport lvt_6_2_ = CrashReport.makeCrashReport(var8, "Tesselating block in world");
            CrashReportCategory lvt_7_1_ = lvt_6_2_.makeCategory("Block being tesselated");
            CrashReportCategory.addBlockInfo(lvt_7_1_, pos, state.getBlock(), state.getBlock().getMetaFromState(state));
            throw new ReportedException(lvt_6_2_);
        }
    }

    public BlockModelRenderer getBlockModelRenderer()
    {
        return this.blockModelRenderer;
    }

    private IBakedModel getBakedModel(IBlockState state, BlockPos pos)
    {
        IBakedModel lvt_3_1_ = this.blockModelShapes.getModelForState(state);

        if (pos != null && this.gameSettings.allowBlockAlternatives && lvt_3_1_ instanceof WeightedBakedModel)
        {
            lvt_3_1_ = ((WeightedBakedModel)lvt_3_1_).getAlternativeModel(MathHelper.getPositionRandom(pos));
        }

        return lvt_3_1_;
    }

    public IBakedModel getModelFromBlockState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        Block lvt_4_1_ = state.getBlock();

        if (worldIn.getWorldType() != WorldType.DEBUG_WORLD)
        {
            try
            {
                state = lvt_4_1_.getActualState(state, worldIn, pos);
            }
            catch (Exception var6)
            {
                ;
            }
        }

        IBakedModel lvt_5_1_ = this.blockModelShapes.getModelForState(state);

        if (pos != null && this.gameSettings.allowBlockAlternatives && lvt_5_1_ instanceof WeightedBakedModel)
        {
            lvt_5_1_ = ((WeightedBakedModel)lvt_5_1_).getAlternativeModel(MathHelper.getPositionRandom(pos));
        }

        return lvt_5_1_;
    }

    public void renderBlockBrightness(IBlockState state, float brightness)
    {
        int lvt_3_1_ = state.getBlock().getRenderType();

        if (lvt_3_1_ != -1)
        {
            switch (lvt_3_1_)
            {
                case 1:
                default:
                    break;

                case 2:
                    this.chestRenderer.renderChestBrightness(state.getBlock(), brightness);
                    break;

                case 3:
                    IBakedModel lvt_4_1_ = this.getBakedModel(state, (BlockPos)null);
                    this.blockModelRenderer.renderModelBrightness(lvt_4_1_, state, brightness, true);
            }
        }
    }

    public boolean isRenderTypeChest(Block p_175021_1_, int p_175021_2_)
    {
        if (p_175021_1_ == null)
        {
            return false;
        }
        else
        {
            int lvt_3_1_ = p_175021_1_.getRenderType();
            return lvt_3_1_ == 3 ? false : lvt_3_1_ == 2;
        }
    }

    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        this.fluidRenderer.initAtlasSprites();
    }
}
